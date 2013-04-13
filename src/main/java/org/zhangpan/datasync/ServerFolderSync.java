package org.zhangpan.datasync;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangpan.datasync.db.Task;
import org.zhangpan.datasync.foldermonitor.DbHelper;
import org.zhangpan.datasync.foldermonitor.FolderCheckException;
import org.zhangpan.datasync.foldermonitor.FolderChecker;
import org.zhangpan.datasync.foldermonitor.FolderMonitor;
import org.zhangpan.utils.Constants;
import org.zhangpan.utils.DateFormater;
import org.zhangpan.utils.FileUtils;

import com.baidu.pcs.PcsFileEntry;
import com.baidu.pcs.exception.PcsException;

/**
 * 循环从服务器获取目录信息，并将修改同步至本地
 * 
 * @author zhangchao
 */
public class ServerFolderSync extends AbstractFolderSync {

	private static Logger logger = LoggerFactory
			.getLogger(ServerFolderSync.class);

	Date lastSyncDate = null;
	List<Task> syncTasks = new ArrayList<Task>();

	public ServerFolderSync() {
		super();
	}

	private int syncModifys(String parentDir) {
		int size = 0;
		try {

			List<PcsFileEntry> fileEntries = pcsClient.list(parentDir);

			if (fileEntries.size() == 0) {
				logger.info("folder is empty : " + parentDir);
				return size;
			}

			for (PcsFileEntry pcsFileEntry : fileEntries) {
				String path = pcsFileEntry.getPath();
				String relativePath = getRelativePath(path);
				String localPath = localRoot + relativePath;
				logger.info("check file : " + pcsFileEntry.toString());
				if (checkLocalExists(path)) {
					logger.info("file exists in local : " + path);

					if (DbHelper.hasModifiedTask(relativePath)) {
						// 本地文件在上次同步之后被修改，但还没有同步至服务器，所以无需下载
						return size;
					}

					if (!pcsFileEntry.isDir()) {
						String md5 = FolderChecker.getInstance().getFileMd5(
								localPath);
						if (!md5.equals(pcsFileEntry.getMd5())) {
							logger.info("but file is not the same : " + path);
							new File(localPath).delete();
							pcsClient.downloadToFile(path, localPath);
						}
					}
				} else {

					if (DbHelper.localModified(relativePath)) {
						// 本地文件在上次同步之后被删除，但是删除信息还没有同步至服务器，所以无需下载
						return size;
					}

					if (pcsFileEntry.isDir()) {
						logger.info("create folder to local : " + localPath);
						new File(localPath).mkdir();
						size += syncModifys(path);
					} else {
						logger.info("download file to local : " + localPath);
						pcsClient.downloadToFile(path, localPath);
						size++;
					}
				}
			}

		} catch (PcsException e) {
			e.printStackTrace();
		}
		return size;
	}

	// 检测本地和服务器的最后同步时间是否一致
	private boolean checkSystemCfg() {
		boolean isModifiy = true;
		File localCfg = new File(localRoot + Constants.LOCAL_CONFIG_FILE);
		if (!localCfg.exists()) {
			// 生成本地最后同步时间配置文件
			try {
				localCfg.createNewFile();

				// 更新本地最后同步时间配置文件
				StringBuffer sb = new StringBuffer();
				sb.append("lastSyncTime=").append(
						DateFormater.getInstance()
								.getDateAndTimeStr(new Date()));
				FileUtils.writeFile(localCfg, sb.toString());

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		try {
			// 获取服务器最后同步时间配置文件
			pcsClient.downloadToFile(appRoot + Constants.APP_CONFIG_FILE,
					localRoot + Constants.LOCAL_CONFIG_FILE);
		} catch (PcsException e) {
			logger.info("download app config file fail.");
			String errorMessage = e.getMessage();
			if (errorMessage.contains("file does not exist")) {
				// 服务器没有最后同步时间配置文件
				logger.info("server app config file fail does not exist!");
			}
		}

		File appCfg = new File(localRoot + Constants.APP_CONFIG_FILE);
		if (appCfg.exists()) {
			// 检测两者的最后同步时间是否一致
			String appLastModify = FileUtils.readFileByLines(appCfg, 0, 1);
			String sysLastModify = FileUtils.readFileByLines(localCfg, 0, 1);
			if (appLastModify.equals(sysLastModify)) {
				// 最后同步时间一致不需要同步
				logger.info("local folder is up to date, with no need for download.");
				isModifiy = false;
			}
		}

		return isModifiy;
	}

	public boolean before() {
		return checkSystemCfg();
	}

	public void sync() {
		logger.info("ServerFolderSync sync");
		try {
			// 关闭本地文件夹修改监控
			FolderMonitor.getInstance().stopMonitor();

			// 同步服务器上面的修改至本地文件夹
			syncModifys(appRoot);

			try {
				// 重构本地目标文件夹结构
				FolderChecker.getInstance().check(false);
			} catch (FolderCheckException e) {
				e.printStackTrace();
			}

		} catch (FolderCheckException e) {
			logger.error("stopMonitor error");
		} finally {
			try {
				FolderMonitor.getInstance().startMonitor();
			} catch (FolderCheckException e) {
				logger.error("startMonitor error");
			}
		}
	}

	public boolean after() {
		logger.info("ServerFolderSync after");
		return false;
	}

}
