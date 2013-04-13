package org.zhangpan.datasync.foldermonitor;

import java.io.File;

import net.contentobjects.jnotify.JNotifyListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangpan.datasync.db.EventType;
import org.zhangpan.datasync.db.Task;
import org.zhangpan.datasync.db.TaskDAO;
import org.zhangpan.datasync.db.TaskDAOImpl;
import org.zhangpan.utils.ApplicationConfigs;
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
	
	public Listener() {
		rootDir = new ApplicationConfigs().getProperty(Constants.LOCAL_ROOT_DIR);
	}

	private String getRelativePath(String path) {
		return path.substring(rootDir.length());
	}

	private void setFileType(File file, Task task) {
		if (file.isDirectory()) {
			task.setFileType(Constants.FOLDER);
		} else {
			task.setFileType(Constants.FILE);
		}
	}

	public void fileRenamed(final int wd, final String rootPath,
			final String oldName, final String newName) {

		if (FileHelper.isExclude(newName)) {
			return;
		}

		String oldPathName = getRelativePath(rootPath + oldName);
		String newPathName = getRelativePath(rootPath + newName);
		logger.info("JNotifyTest.fileRenamed() : wd #" + wd + " path = "
				+ getRelativePath(rootPath) + ", " + oldName + " -> " + newName);
		if (DbHelper.hasCreateTask(oldPathName)) {
			// 源文件在上次一同步后创建，然后被重命名，所以就直接上传重命名后的文件
			DbHelper.deleteTask(oldPathName, EventType.add);
			logger.info(oldName + " add task has been deleteed...");

			Task task = new Task();
			task.setDstPath(newPathName);
			setFileType(new File(rootPath + newPathName), task);
			task.setEventType(EventType.add);
			DbHelper.addTask(task);
			logger.info(newName + " add task has added to db...");
			return;
		}

		Task task = new Task();
		task.setSrcPath(oldPathName);
		task.setDstPath(newPathName);
		setFileType(new File(rootPath + newPathName), task);
		task.setEventType(EventType.rename);
		DbHelper.addTask(task);
		logger.info(oldName + "->" + newName + " rename task add to db...");
	}

	public void fileModified(final int wd, final String rootPath,
			final String name) {

		// 重新计算文件Md5值
		FolderChecker.getInstance().freshFileMd5(rootPath + name);

		if (FileHelper.isExclude(name)) {
			return;
		}

		String pathName = getRelativePath(rootPath + name);
		logger.info("JNotifyTest.fileModified() : wd #" + wd + " path = "
				+ pathName);
		if (DbHelper.hasModifiedTask(pathName)) {
			// 该文件已经有未同步的修改任务，无需再添加
			logger.info(name + " modify task has been added to db...");
			return;
		}
		

		Task task = new Task();
		task.setDstPath(pathName);
		setFileType(new File(rootPath + name), task);
		task.setEventType(EventType.modify);
		DbHelper.addTask(task);
		logger.info(name + " modify task add to db...");


	}

	public void fileDeleted(final int wd, final String rootPath,
			final String name) {

		if (FileHelper.isExclude(name)) {
			return;
		}

		String pathName = getRelativePath(rootPath + name);
		logger.info("JNotifyTest.fileDeleted() : wd #" + wd + " path = "
				+ pathName);

		if (DbHelper.hasModifiedTask(pathName)) {
			// 该文件已经有未同步的修改任务，删除后，再添加任务
			logger.info(name + " modify task has been delete...");
			DbHelper.deleteTask(pathName, EventType.modify);
			logger.info(name + " server with no need to modify...");
		}

		if (DbHelper.hasCreateTask(pathName)) {
			// 该文件已经有未同步的创建任务，删除后，再添加任务
			logger.info(name + " add task has been delete...");
			DbHelper.deleteTask(pathName, EventType.add);
			logger.info(name + " server with no need to add...");
		}

		if (DbHelper.hasRenameTask(pathName)) {
			// 该文件已经有未同步的重命名任务，删除后，再添加任务
			logger.info(name + " rename task has been delete...");
			DbHelper.deleteTask(pathName, EventType.rename);
			logger.info(name + " server with no need to rename...");
		}

		// 再添加重命名任务
		Task task = new Task();
		task.setDstPath(pathName);
		setFileType(new File(rootPath + name), task);
		task.setEventType(EventType.delete);
		DbHelper.addTask(task);

		logger.info(name + " delete task has added to db...");

	}

	public void fileCreated(final int wd, final String rootPath,
			final String name) {

		if (FileHelper.isExclude(name)) {
			return;
		}

		String pathName = getRelativePath(rootPath + name);
		logger.info("JNotifyTest.fileCreated() : wd #" + wd + " path = "
				+ pathName);

		Task task = new Task();
		task.setDstPath(pathName);
		task.setEventType(EventType.add);
		setFileType(new File(rootPath + name), task);
		DbHelper.addTask(task);

		logger.info(name + " add task has added to db...");

	}
}
