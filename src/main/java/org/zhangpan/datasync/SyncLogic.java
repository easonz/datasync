package org.zhangpan.datasync;


public class SyncLogic implements Runnable {

	private LocalFolderSync localFolderSync = null;
	private ServerFolderSync serverFolderSync = null;

	public SyncLogic() {
		localFolderSync = new LocalFolderSync();
		serverFolderSync = new ServerFolderSync();
	}

	public void run() {

		// 同步服务器至本地文件夹
		if (serverFolderSync.before()) {
			serverFolderSync.sync();
			serverFolderSync.after();
		}

		// 同步本地文件夹至服务器
		if (localFolderSync.before()) {
			localFolderSync.sync();
			localFolderSync.after();
		}
	}
}
