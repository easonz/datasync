package org.zhangpan.datasync;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangpan.datasync.db.Task;
import org.zhangpan.datasync.db.TaskDAOImpl;
import org.zhangpan.utils.Constants;
import org.zhangpan.utils.DateFormater;
import org.zhangpan.utils.FileUtils;
import org.zhangpan.utils.ThreadUtils;

import com.baidu.pcs.PcsFileEntry;
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
						uploadFile(localPath,
								getParentDir(serverPath),
								getFileName(serverPath), true);
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
					uploadFile(localPath, getParentDir(serverPath),
							getFileName(serverPath), true);

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

		// 更新 本地和服务器的最后同步时间配置
		File localCfg = new File(localRoot + Constants.LOCAL_CONFIG_FILE);
		File appCfg = new File(localRoot + Constants.APP_CONFIG_FILE);
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("lastSyncTime=").append(
					DateFormater.getInstance().getDateAndTimeStr(new Date()));
			FileUtils.writeFile(localCfg, sb.toString());
			FileUtils.writeFile(appCfg, sb.toString());

			uploadFile(localRoot + Constants.LOCAL_CONFIG_FILE, appRoot,
					Constants.APP_CONFIG_FILE, true);
			logger.info("upload app config file success.");
		} catch (PcsException e) {
			logger.info("upload app config file fail.");
		}
		logger.info("LocalFolderSync after");
		return false;
	}

	/**
	 * 
	 * @param localPath
	 * @param remoteDir
	 * @param remoteName
	 * @param overwrite 是否覆盖服务端的文件
	 * @return
	 * @throws PcsException
	 */
	private void uploadFile(String localPath, String remoteDir,
			String remoteName, boolean overwrite) throws PcsException {

		if (overwrite) {
			List<PcsFileEntry> files = pcsClient.search(remoteDir, remoteName,
					false);
			if (files.size() == 1) {
				logger.info("delete exits servre file : " + remoteDir + remoteName);
				pcsClient.delete(remoteDir + remoteName);
			}
		}

		pcsClient.uploadFile(localPath, remoteDir, remoteName);
	}

}
