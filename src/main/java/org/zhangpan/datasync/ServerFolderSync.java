package org.zhangpan.datasync;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangpan.datasync.db.Task;
import org.zhangpan.datasync.db.TaskDAO;
import org.zhangpan.datasync.db.TaskDAOImpl;
import org.zhangpan.utils.ThreadUtils;

import com.baidu.pcs.PcsClient;
import com.baidu.pcs.exception.PcsException;

/**
 * 循环从服务器获取目录信息，并将修改同步至本地
 * 
 * @author zhangchao
 */
public class ServerFolderSync implements IFolderSync {
	
	private static Logger logger = LoggerFactory.getLogger(ServerFolderSync.class);
	TaskDAO taskDao = new TaskDAOImpl();
	Date lastSyncDate = null;
	List<Task> syncTasks = null;

	private static String accessToken = "3.e87dfe1d23d49c016f14158faad519e2.2592000.1360397434.3355604315-238347";
	private static String appRoot = "/apps/pcstest_oauth/";
	PcsClient pcsClient = new PcsClient(accessToken, appRoot);

	public boolean before() {
		logger.info("ServerFolderSync before");
		taskDao = new TaskDAOImpl();
		lastSyncDate = taskDao.getLastSyncDate();
		logger.info("last sync date : " + lastSyncDate);
		syncTasks = taskDao.queryAllSyncTask();
		if (syncTasks.size() == 0) {
			logger.info("has no sync task...");
			return false;
		} else {
			logger.info("has " + syncTasks.size() + " sync task...");
		}
		return true;
	}

	public void sync() {
		logger.info("ServerFolderSync sync");
		ThreadUtils.sleep(2 * 1000);
		for (Task task : syncTasks) {
			try {
				switch (task.getEventType()) {
				case add:
					boolean isDir = true;
					if (isDir) {
						pcsClient.uploadFile(task.getSrcPath(), "remoteDir", "remoteName");
					}else{
						pcsClient.mkdir("remotePath");
					}
					break;
				case move:
				case rename:
					pcsClient.move(task.getSrcPath(), task.getDstPath());
					break;
				case delete :
					pcsClient.delete(task.getSrcPath());
					break;
				case modify:
					pcsClient.uploadFile(task.getSrcPath(), "remoteDir", "remoteName");
					
				default:
					break;
				}
				taskDao.delete(task.getId());
			} catch (PcsException e) {
				task.setStatus(-1);
				taskDao.update(task);
				logger.info("sync task[" + task + "] error...");
				// e.printStackTrace();
			}
		}
	}

	public boolean after() {
		logger.info("ServerFolderSync after");
		return false;
	}

}
