package org.zhangpan.datasync.foldermonitor;

import static org.zhangpan.datasync.TaskOperate.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import net.contentobjects.jnotify.JNotifyListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangpan.datasync.db.EventType;
import org.zhangpan.datasync.db.Task;
import org.zhangpan.datasync.db.TaskDAO;
import org.zhangpan.datasync.db.TaskDAOImpl;
import org.zhangpan.utils.CommonConfigs;
import org.zhangpan.utils.Constants;

/**
 * 
 * @author zhangchao
 * 
 */
public class Listener implements JNotifyListener {

	private static Logger logger = LoggerFactory.getLogger(Listener.class);
	TaskDAO taskDao = new TaskDAOImpl();
	private static String rootDir = null;
	
	Set<String> excludeFiles = new HashSet<String>();

	public Listener() {
		excludeFiles.add(FolderChecker.INFO_FILENAME);
		rootDir = new CommonConfigs().getProperty(Constants.LOCAL_ROOT_DIR);
	}

	private boolean isExclude(String fileName) {
		for (String name : excludeFiles) {
			if (fileName.endsWith(name)) {
				return true;
			}
		}
		return false;
	}

	private String getRelativePath(String path) {
		return path.substring(rootDir.length());
	}

	public void fileRenamed(final int wd, final String rootPath,
			final String oldName, final String newName) {

		if (isExclude(newName)) {
			return;
		}

		String oldPathName = getRelativePath(rootPath + "/" + oldName);
		String newPathName = getRelativePath(rootPath + "/" + newName);

		Task task = new Task();
		task.setSrcPath(oldPathName);
		task.setDstPath(newPathName);
		if (new File(rootPath + "/" + newPathName).isDirectory()) {
			task.setFileType(Constants.FOLDER);
		} else {
			task.setFileType(Constants.FILE);
		}
		task.setEventType(EventType.rename);
		add(task);

		logger.info("JNotifyTest.fileRenamed() : wd #" + wd + " path = "
				+ getRelativePath(rootPath) + ", " + oldName + " -> " + newName);
	}

	public void fileModified(final int wd, final String rootPath,
			final String name) {

		if (isExclude(name)) {
			return;
		}

		if (isAdded(name)) {
			logger.info(name + " has added to db...");
			return;
		}
		
		String pathName = getRelativePath(rootPath + "/" + name);

		Task task = new Task();
		task.setDstPath(pathName);
		if (new File(rootPath + "/" + name).isDirectory()) {
			task.setFileType(Constants.FOLDER);
		} else {
			task.setFileType(Constants.FILE);
		}
		task.setEventType(EventType.modify);
		add(task);

		logger.info("JNotifyTest.fileModified() : wd #" + wd
 + " path = "
				+ pathName);

	}

	public void fileDeleted(final int wd, final String rootPath,
			final String name) {

		if (isExclude(name)) {
			return;
		}

		String pathName = getRelativePath(rootPath + "/" + name);

		Task task = new Task();
		task.setDstPath(pathName);
		if (new File(rootPath + "/" + name).isDirectory()) {
			task.setFileType(Constants.FOLDER);
		} else {
			task.setFileType(Constants.FILE);
		}
		task.setEventType(EventType.delete);
		add(task);

		logger.info("JNotifyTest.fileDeleted() : wd #" + wd + " path = "
				+ pathName);
	}

	public void fileCreated(final int wd, final String rootPath,
			final String name) {

		if (isExclude(name)) {
			return;
		}

		String pathName = getRelativePath(rootPath + "/" + name);

		Task task = new Task();
		task.setDstPath(pathName);
		task.setEventType(EventType.add);
		if (new File(rootPath + "/" + name).isDirectory()) {
			task.setFileType(Constants.FOLDER);
		} else {
			task.setFileType(Constants.FILE);
		}
		add(task);

		logger.info("JNotifyTest.fileCreated() : wd #" + wd + " path = "
				+ pathName);
	}
}
