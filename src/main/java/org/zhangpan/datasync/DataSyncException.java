package org.zhangpan.datasync;

/**
 * 文件夹扫描 异常
 * 
 * @author ning
 * 
 */
public class DataSyncException extends RuntimeException {

	public DataSyncException(Exception e) {
		super(e);
	}

	public DataSyncException(Throwable e) {
		super(e);
	}

	public DataSyncException(String detailMessage) {
		super(detailMessage);
	}
}
