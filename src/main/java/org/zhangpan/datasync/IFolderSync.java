package org.zhangpan.datasync;

public interface IFolderSync {

	public boolean before();

	public void sync();

	public boolean after();

}
