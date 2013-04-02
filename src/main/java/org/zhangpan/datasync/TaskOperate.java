package org.zhangpan.datasync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangpan.datasync.db.Task;
import org.zhangpan.datasync.db.TaskDAO;
import org.zhangpan.datasync.db.TaskDAOImpl;

public class TaskOperate {

	private static Logger logger = LoggerFactory.getLogger(TaskOperate.class);
	private static TaskDAO taskDao = new TaskDAOImpl();

	public TaskOperate() {

	}
	
	
	public static boolean isAdded(String name) {

		String sql = "select count(*) from sync_task where dst_path=?";
		String[] obj = { name };
		int size = taskDao.querySize(sql, obj);
		return size > 0;
	}

	public static boolean add(Task task) {
		return taskDao.insert(task);
	}

	public static void clearTaskAdd(Task task) {
		String sql = "delete from sync_task where src_path=? or dst_path=?";
		String[] obj = { task.getSrcPath(), task.getSrcPath() };
		taskDao.querySize(sql, obj);
	}

	public static void clearTaskModify(Task task) {
		String sql = "delete from sync_task where src_path=? or dst_path=?";
		String[] obj = { task.getSrcPath(), task.getSrcPath() };
		taskDao.querySize(sql, obj);
	}

	public static void clearTaskRename(Task task) {
		String sql = "delete from sync_task where src_path=? or dst_path=?";
		String[] obj = { task.getSrcPath(), task.getSrcPath() };
		taskDao.querySize(sql, obj);
	}
}
