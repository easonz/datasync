package org.zhangpan.datasync.foldermonitor;

/**
 * 文件夹扫描 异常
 * 
 * @author ning
 * 
 */
public class FolderCheckException extends Exception {

	public FolderCheckException(Exception e) {
		super(e);
	}

	public FolderCheckException(String detailMessage) {
		super(detailMessage);
	}
}
