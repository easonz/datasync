package org.zhangpan.datasync;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangpan.datasync.db.Task;
import org.zhangpan.datasync.db.TaskDAOImpl;
import org.zhangpan.utils.Constants;
import org.zhangpan.utils.ThreadUtils;

import com.baidu.pcs.exception.PcsException;

/**
 * 负责 将本地目录的修改同步至服务器端
 * 
 * @author zhangchao
 * 
 */
public class LocalFolderSync extends AbstractFolderSync {

	private static Logger logger = LoggerFactory.getLogger(LocalFolderSync.class);

	/*
	 * 一个文件名的修改会有两个事件fileRenamed，fileModified
	 * 文件内容的修改也会有两个事件fileModified，fileModified
	 * 
	 * 需要一种策略将这两个事件合并，做法：
	 * 给每一个事件加上时间戳，
	 * 然后根据文件名和时间戳来合并事件，比如至同步5分钟之前的，这样就可以将多次操作的概率降低。
	 */
	Date lastSyncDate = null;
	List<Task> syncTasks = null;

	public LocalFolderSync() {
		super();
	}

	public boolean before() {
		logger.info("LocalFolderSync before");
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
		logger.info("LocalFolderSync sync");
		ThreadUtils.sleep(2 * 1000);
		for (Task task : syncTasks) {
			try {
				String serverPath = appRoot + task.getDstPath();
				String localPath = localRoot + task.getDstPath();
				switch (task.getEventType()) {
				case add:
					boolean isDir = task.getFileType() == Constants.FOLDER;
					if (isDir) {
						pcsClient.mkdir(serverPath);
					} else {
						pcsClient.uploadFile(localPath,
								getParentDir(serverPath),
								getFileName(serverPath));
					}
					break;
				case move:
				case rename:
					pcsClient.move(chengeToServerPath(task.getSrcPath()),
							serverPath);
					break;
				case delete:
					pcsClient.delete(serverPath);
					break;
				case modify:
					pcsClient.uploadFile(localPath,
							getParentDir(serverPath), getFileName(serverPath));

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
		logger.info("LocalFolderSync after");
		return false;
	}

}
