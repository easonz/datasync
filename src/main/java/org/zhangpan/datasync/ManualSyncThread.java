package org.zhangpan.datasync;

import java.util.concurrent.locks.Lock;

public class ManualSyncThread extends Thread {

	private SyncLogic syncLogic = null;
	private Lock syncLock = null;

	public ManualSyncThread() {
		super();
	}

	public ManualSyncThread(SyncLogic syncLogic) {
		super();
		this.syncLogic = syncLogic;
	}

	public ManualSyncThread(SyncLogic syncLogic, Lock syncLock) {
		super();
		this.syncLogic = syncLogic;
		this.syncLock = syncLock;
	}

	public void setSyncLogic(SyncLogic syncLogic) {
		this.syncLogic = syncLogic;
	}

	public SyncLogic getSyncLogic() {
		return syncLogic;
	}

	public void setSyncLock(Lock syncLock) {
		this.syncLock = syncLock;
	}

	public Lock getSyncLock() {
		return syncLock;
	}

	public void tryStop() {
		interrupt();
	}

	@Override
	public void run() {
		syncLock.lock();
		try {

			syncLogic.run();

		} finally {
			syncLock.unlock();
		}
	}
}
