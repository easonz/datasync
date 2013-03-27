package org.zhangpan.datasync;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangpan.datasync.foldermonitor.FolderCheckException;
import org.zhangpan.datasync.foldermonitor.FolderChecker;
import org.zhangpan.datasync.foldermonitor.FolderMonitor;

public class MainLogic {

	private static Logger logger = LoggerFactory.getLogger(MainLogic.class);
	private FolderChecker folderChecker = null;
	private SyncLogic syncLogic = null;
	private AutoSyncThread autoSyncThread = null;
	private ManualSyncThread manualSyncThread = null;
	private String rootDir = null;
	private boolean isAutoSync = false;
	private int syncInterval = 600;
	private Lock syncLock = new ReentrantLock();

	public MainLogic() {
		folderChecker = new FolderChecker();
		syncLogic = new SyncLogic();
		autoSyncThread = new AutoSyncThread(syncLogic, syncLock);
	}

	public boolean isAutoSync() {
		return isAutoSync;
	}

	public void setAutoSync(boolean isAutoSync) {
		this.isAutoSync = isAutoSync;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	public void setSyncInterval(int syncInterval) {
		this.syncInterval = syncInterval;
	}

	public int getSyncInterval() {
		return syncInterval;
	}

	public void init() {

		logger.info("init");
		try {
			// 检测本地文件夹在系统启动时与上次退出时的差异
			folderChecker.init();
			// 监控本地文件夹修改
			FolderMonitor.getInstance().setRootFolder("d:/test");
			FolderMonitor.getInstance().startMonitor();
		} catch (FolderCheckException e) {
			throw new DataSyncException(e);
		}

		if (isAutoSync) {
			autoSyncThread.start();
		}
	}

	public void startSync() {
		
		if (!syncLock.tryLock()) {

			logger.info("上次同步未结束");
			throw new DataSyncException("上次同步未结束");
		}
		syncLock.unlock();

		if (!isAutoSync) {
			logger.info("start ManualSyncThread to Sync");
			manualSyncThread = new ManualSyncThread(syncLogic, syncLock);
			manualSyncThread.start();
		} else {
			logger.info("start AutoSyncThread to Sync");
			if (!autoSyncThread.isAlive()) {
				autoSyncThread.start();
			} else {
				autoSyncThread.awake();
			}
		}
		
	}

	public void stopSync() {

		logger.info("stopSync");

		if (isAutoSync && autoSyncThread.holdsLock(syncLock) || !isAutoSync
				&& manualSyncThread.holdsLock(syncLock)) {
			throw new DataSyncException("上次同步未结束");
		}
	}

	public void forceStop() {

		logger.info("forceStop");

		try {

			if (isAutoSync) {
				autoSyncThread.interrupt();
			} else {
				manualSyncThread.interrupt();
			}
		} catch (Throwable e) {
			throw new DataSyncException(e);
		}
	}

	public void exit() {
		try {
			FolderMonitor.getInstance().stopMonitor();
		} catch (FolderCheckException e) {
			throw new DataSyncException(e);
		}
		forceStop();
	}
}
