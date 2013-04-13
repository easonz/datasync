package org.zhangpan.datasync;

import java.io.File;

import org.zhangpan.datasync.db.TaskDAOImpl;
import org.zhangpan.utils.ApplicationConfigs;
import org.zhangpan.utils.Constants;

import com.baidu.pcs.PcsClient;

public abstract class AbstractFolderSync {

	protected static TaskDAOImpl taskDao = null;
	protected static String accessToken = null;
	protected static String appRoot = null;
	protected static String localRoot = null;
	PcsClient pcsClient = null;

	public AbstractFolderSync() {
		taskDao = new TaskDAOImpl();
		accessToken = new ApplicationConfigs().getProperty(Constants.ACCESS_TOKEN);
		localRoot = new ApplicationConfigs().getProperty(Constants.LOCAL_ROOT_DIR);
		appRoot = new ApplicationConfigs().getProperty(Constants.APP_ROOT_DIR);
		pcsClient = new PcsClient(accessToken, appRoot);
	}

	protected boolean checkLocalExists(String serverPath) {
		String relativePath = getRelativePath(serverPath);
		return new File(localRoot + relativePath).exists();
	}

	protected boolean checkServerExists(String localPath) {
		return true;
	}

	protected String getRelativePath(String serverPath) {
		return serverPath.substring(appRoot.length());
	}

	protected String chengeToServerPath(String path) {
		if (path.startsWith(localRoot)) {
			return appRoot + path.substring(localRoot.length());
		} else {
			return appRoot + path;
		}
	}

	protected String chengeToLocalPath(String path) {
		if (path.startsWith(appRoot)) {
			return localRoot + path.substring(appRoot.length());
		} else {
			return localRoot + path;
		}
	}

	protected String getParentDir(String path) {
		return path.substring(0, path.lastIndexOf("/") + 1);
	}

	protected String getFileName(String path) {
		return path.substring(path.lastIndexOf("/") + 1);
	}

	public boolean before() {
		return true;
	}

	public abstract void sync();

	public boolean after() {
		return true;
	}

}
