package org.zhangpan.datasync;

import java.io.File;

import org.zhangpan.datasync.db.TaskDAO;
import org.zhangpan.datasync.db.TaskDAOImpl;
import org.zhangpan.utils.CommonConfigs;
import org.zhangpan.utils.Constants;

import com.baidu.pcs.PcsClient;

public abstract class AbstractFolderSync {

	TaskDAO taskDao = null;
	protected static String accessToken = "";
	protected static String appRoot = "";
	protected static String localRoot = "";
	PcsClient pcsClient = null;

	public AbstractFolderSync() {
		taskDao = new TaskDAOImpl();
		pcsClient = new PcsClient(accessToken, appRoot);
		accessToken = new CommonConfigs().getProperty(Constants.ACCESS_TOKEN);
		localRoot = new CommonConfigs().getProperty(Constants.LOCAL_ROOT_DIR);
		appRoot = new CommonConfigs().getProperty(Constants.APP_ROOT_DIR);
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
