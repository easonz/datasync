package org.zhangpan.datasync;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangpan.datasync.db.Task;
import org.zhangpan.datasync.foldermonitor.FolderCheckException;
import org.zhangpan.datasync.foldermonitor.FolderChecker;
import org.zhangpan.datasync.foldermonitor.FolderMonitor;

import com.baidu.pcs.PcsFileEntry;
import com.baidu.pcs.exception.PcsException;

/**
 * 循环从服务器获取目录信息，并将修改同步至本地
 * 
 * @author zhangchao
 */
public class ServerFolderSync extends AbstractFolderSync {
	
	private static Logger logger = LoggerFactory.getLogger(ServerFolderSync.class);

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
				String localPath = localRoot + getRelativePath(path);
				logger.info("check file : " + pcsFileEntry.toString());
				if (checkLocalExists(path)) {
					logger.info("file exists in local : " + path);
					if (!pcsFileEntry.isDir()) {
						String md5 = FolderChecker.getInstance().getFileMd5(localPath);
						if (!md5.endsWith(pcsFileEntry.getMd5())) {
							logger.info("but file is not the same : " + path);
							new File(localPath).delete();
							pcsClient.downloadToFile(path, localPath);
						}
					}
				} else {
					if(pcsFileEntry.isDir()){
						logger.info("create folder to local : " + localPath);
						new File(localPath).mkdir();
						size += syncModifys(path);
					}else{
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

	public boolean before() {
		// int modifySize = checkModifys(appRoot);
		// logger.info("modifySize is : " + modifySize);
		return true;
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
