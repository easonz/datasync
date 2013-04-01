package org.zhangpan.datasync.foldermonitor;

import java.io.File;

import net.contentobjects.jnotify.JNotifyListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangpan.datasync.db.EventType;
import org.zhangpan.datasync.db.Task;
import org.zhangpan.datasync.db.TaskDAO;
import org.zhangpan.datasync.db.TaskDAOImpl;
import org.zhangpan.utils.Constants;

/**
 * 
 * @author zhangchao
 * 
 */
public class Listener implements JNotifyListener {

	private static Logger logger = LoggerFactory.getLogger(Listener.class);
	TaskDAO taskDao = new TaskDAOImpl();

	public void fileRenamed(final int wd, final String rootPath,
			final String oldName, final String newName) {
		logger.info("JNotifyTest.fileRenamed() : wd #" + wd + " root = "
				+ rootPath + ", " + oldName + " -> " + newName);

		Task task = new Task();
		task.setSrcPath(oldName);
		task.setDstPath(newName);
		task.setEventType(EventType.rename);
		taskDao.insert(task);

	}

	public void fileModified(final int wd, final String rootPath,
			final String name) {

		if (isAdded(name)) {
			logger.info(name + " has added to db...");
			return;
		}

		Task task = new Task();
		task.setSrcPath(name);
		task.setEventType(EventType.modify);
		taskDao.insert(task);

		logger.info("JNotifyTest.fileModified() : wd #" + wd
				+ " root = " + rootPath + ", " + name);

	}

	private boolean isAdded(String name) {
		String sql = "select count(*) from sync_task where src_path=?";
		String[] obj = { name };
		int size = taskDao.querySize(sql, obj);
		return size > 0;
	}

	public void fileDeleted(final int wd, final String rootPath,
			final String name) {

		Task task = new Task();
		task.setSrcPath(name);
		task.setEventType(EventType.delete);
		taskDao.insert(task);

		logger.info("JNotifyTest.fileDeleted() : wd #" + wd + " root = "
				+ rootPath + ", " + name);
	}

	public void fileCreated(final int wd, final String rootPath,
			final String name) {

		Task task = new Task();
		task.setSrcPath(name);
		task.setEventType(EventType.add);
		if (new File(rootPath + File.separator + name).isDirectory()) {
			task.setFileType(Constants.FOLDER);
		} else {
			task.setFileType(Constants.FILE);
		}
		taskDao.insert(task);

		logger.info("JNotifyTest.fileCreated() : wd #" + wd + " root = "
				+ rootPath + ", " + name);
	}
}
