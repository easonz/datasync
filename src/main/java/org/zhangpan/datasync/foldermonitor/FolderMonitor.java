package org.zhangpan.datasync.foldermonitor;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监听本地目录内容的修改事件，并同步至服务器端
 * 
 * @author zhangchao
 * 
 */
public class FolderMonitor {

	private static Logger logger = LoggerFactory.getLogger(FolderScan.class);
	private String rootFolder;
	private int mask = JNotify.FILE_CREATED | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;
	private boolean watchSubtree = true;
	private int monitorID;

	/**
	 * 保证了Singleton的单例化，同时也使得实例instance将一直不会初始化直到有人调用了getInstance()方法
	 * 即有了单例模式正确性和效率的同时，还使得Singleton具备了lazy loading的特性。
	 * 
	 * @author zhangchao
	 * 
	 */
	static class SingletonHolder {
		static FolderMonitor instance = new FolderMonitor();
	}

	private FolderMonitor() {
		logger.info("FolderMonitor constructor");
	}

	public static FolderMonitor getInstance() {
		return SingletonHolder.instance;
	}

	public void setRootFolder(String rootFolder) {
		this.rootFolder = rootFolder;
	}

	public String getRootFolder() {
		return rootFolder;
	}

	public void setMask(int mask) {
		this.mask = mask;
	}

	public int getMask() {
		return mask;
	}

	public void setWatchSubtree(boolean watchSubtree) {
		this.watchSubtree = watchSubtree;
	}

	public boolean isWatchSubtree() {
		return watchSubtree;
	}

	public void startMonitor() throws FolderCheckException {
		try {
			logger.info("startMonitor--- rootFolder:" + rootFolder + ", mask:"
					+ mask);
			monitorID = JNotify.addWatch(rootFolder, mask, watchSubtree,
					new Listener());
		} catch (JNotifyException e) {
			throw new FolderCheckException(e);
		}
	}

	public void stopMonitor() throws FolderCheckException {
		try {
			logger.info("stopMonitor");
			boolean res = JNotify.removeWatch(monitorID);
			if (!res) {
				throw new FolderCheckException("remove monitor error");
			}
		} catch (JNotifyException e) {
			throw new FolderCheckException(e);
		}
	}
}
