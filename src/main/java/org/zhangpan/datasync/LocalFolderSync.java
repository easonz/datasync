package org.zhangpan.datasync;

/**
 * 负责 将本地目录的修改同步至服务器端
 * 
 * @author zhangchao
 * 
 */
public class LocalFolderSync {

	/**
	 * 一个文件名的修改会有两个事件fileRenamed，fileModified
	 * 文件内容的修改也会有两个事件fileModified，fileModified
	 * 
	 * 需要一种策略将这两个事件合并，做法：
	 * 给每一个事件加上时间戳，
	 * 然后根据文件名和时间戳来合并事件，比如至同步5分钟之前的，这样就可以将多次操作的概率降低。
	 */

}
