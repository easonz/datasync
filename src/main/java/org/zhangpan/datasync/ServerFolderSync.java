package org.zhangpan.datasync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangpan.utils.ThreadUtils;

/**
 * 循环从服务器获取目录信息，并将修改同步至本地
 * 
 * @author zhangchao
 */
public class ServerFolderSync implements IFolderSync {
	
	private static Logger logger = LoggerFactory.getLogger(ServerFolderSync.class);

	public boolean before() {
		logger.info("ServerFolderSync before");
		return false;
	}

	public void sync() {
		logger.info("ServerFolderSync sync");
		ThreadUtils.sleep(2 * 1000);
	}

	public boolean after() {
		logger.info("ServerFolderSync after");
		return false;
	}

}
