package org.zhangpan.datasync;

import java.util.concurrent.locks.Lock;

public class AutoSyncThread extends Thread {

	private SyncLogic syncLogic = null;
	private Lock syncLock = null;
	private int syncInterval = 600;
	private boolean keepRunning = true;
	private Object sleepObj = new Object();

	public AutoSyncThread() {
		super();
	}

	public AutoSyncThread(SyncLogic syncLogic) {
		super();
		this.syncLogic = syncLogic;
	}

	public AutoSyncThread(SyncLogic syncLogic, Lock syncLock) {
		super();
		this.syncLogic = syncLogic;
		this.syncLock = syncLock;
	}

	public void setSyncInterval(int syncInterval) {
		this.syncInterval = syncInterval;
	}

	public int getSyncInterval() {
		return syncInterval;
	}

	public void setSyncLogic(SyncLogic syncLogic) {
		this.syncLogic = syncLogic;
	}

	public SyncLogic getSyncLogic() {
		return syncLogic;
	}

	public void tryStop() {
		keepRunning = false;
		interrupt();
	}

	@Override
	public void run() {

		int count = 0;
		while (count == 0 || keepRunning) {

			syncLock.lock();
			try {
				syncLogic.run();
			} finally {
				syncLock.unlock();
			}

			try {
				synchronized (sleepObj) {
					sleepObj.wait(syncInterval * 1000);
				}
			} catch (InterruptedException e) {
				throw new DataSyncException(e);
			}
		}
	}

	public void awake() {
		synchronized (sleepObj) {
			sleepObj.notifyAll();
		}
	}
}
