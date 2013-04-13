package org.zhangpan.datasync;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangpan.datasync.foldermonitor.FolderCheckException;
import org.zhangpan.datasync.foldermonitor.FolderChecker;
import org.zhangpan.datasync.foldermonitor.FolderMonitor;
import org.zhangpan.utils.ApplicationConfigs;
import org.zhangpan.utils.Constants;

public class MainLogic {

	private static Logger logger = LoggerFactory.getLogger(MainLogic.class);
	private FolderChecker folderChecker = null;
	private FolderMonitor folderMonitor = null;
	private SyncLogic syncLogic = null;
	private AutoSyncThread autoSyncThread = null;
	private ManualSyncThread manualSyncThread = null;
	private boolean isAutoSync = false;
	private int syncInterval = 600;
	private Lock syncLock = new ReentrantLock();
	private ApplicationConfigs configs = null;
	private String rootDir = null;

	public MainLogic() {
		folderChecker = FolderChecker.getInstance();
		folderMonitor = FolderMonitor.getInstance();
		syncLogic = new SyncLogic();
		autoSyncThread = new AutoSyncThread(syncLogic, syncLock);
		configs = new ApplicationConfigs();
		rootDir = configs.getProperty(Constants.LOCAL_ROOT_DIR);
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

		logger.info("-------------------data sync program init--------------------");
		try {
			// 检测本地文件夹在系统启动时与上次退出时的差异，重构本地目标文件夹结构
			folderChecker.setRootFolder(rootDir);
			folderChecker.check(true);
			// 监控本地文件夹修改
			folderMonitor.setRootFolder(rootDir);
			folderMonitor.startMonitor();
		} catch (FolderCheckException e) {
			throw new DataSyncException(e);
		}

		// 添加java程序退出事件处理函数
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					logger.info("data sync program exit...");
					FolderMonitor.getInstance().stopMonitor();
				} catch (FolderCheckException e) {
					e.printStackTrace();
				}
			}
		});

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

		if (!syncLock.tryLock()) {

			logger.info("上次同步未结束");
			throw new DataSyncException("上次同步未结束");
		}
		syncLock.unlock();

		forceStop();

		logger.info("-------------------data sync program stop--------------------");
		System.exit(0);
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
			folderMonitor.stopMonitor();
		} catch (FolderCheckException e) {
			throw new DataSyncException(e);
		}
		forceStop();
	}
}
