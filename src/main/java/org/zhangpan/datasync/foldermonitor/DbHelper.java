package org.zhangpan.datasync.foldermonitor;

import org.zhangpan.datasync.db.EventType;
import org.zhangpan.datasync.db.Task;
import org.zhangpan.datasync.db.TaskDAO;
import org.zhangpan.datasync.db.TaskDAOImpl;

public class DbHelper {

	private static TaskDAO taskDao = new TaskDAOImpl();

	/**
	 * 该文件的创建任务是否已经存在
	 * 
	 * @param dstPath
	 * @return
	 */
	public static boolean hasCreateTask(String dstPath) {
		return hasTask(dstPath, EventType.add);
	}

	/**
	 * 该文件的重命名任务是否已经存在
	 * 
	 * @param srcPath
	 * @param dstPath
	 * @return
	 */
	public static boolean hasRenameTask(String dstPath) {
		return hasTask(dstPath, EventType.rename);
	}

	/**
	 * 该文件的重命名任务是否已经存在
	 * 
	 * @param srcPath
	 * @param dstPath
	 * @return
	 */
	public static boolean hasRenameTask(String srcPath, String dstPath) {
		return hasTask(srcPath, dstPath, EventType.rename);
	}

	/**
	 * 该文件的修改任务是否已经存在
	 * 
	 * @param dstPath
	 * @return
	 */
	public static boolean hasModifiedTask(String dstPath) {
		return hasTask(dstPath, EventType.modify);
	}

	/**
	 * 该文件的删除任务是否已经存在
	 * 
	 * @param dstPath
	 * @return
	 */
	public static boolean hasDeleteTask(String dstPath) {
		return hasTask(dstPath, EventType.delete);
	}

	/**
	 * 目标文件是否在本地已经被修改
	 * 
	 * @param dstPath
	 * @return
	 */
	public static boolean localModified(String dstPath) {
		String sql = "select count (*) from sync_task where (dst_path=? and status=1 and event_type='delete') "
				+ " or (src_path=? and status=1 and event_type='rename')";
		String[] obj = { dstPath, dstPath };
		int size = taskDao.querySize(sql, obj);
		return size > 0;
	}

	public static boolean hasTask(String dstPath, EventType type) {
		String sql = "select count (*) from sync_task where dst_path=? and status=1 and event_type=?";
		String[] obj = { dstPath, type.toString() };
		int size = taskDao.querySize(sql, obj);
		return size > 0;
	}

	public static boolean hasTask(String srcPath, String dstPath, EventType type) {
		String sql = "select count (*) from sync_task where src_path=? and dst_path=? and status=1 and event_type=?";
		String[] obj = { srcPath, dstPath, type.toString() };
		int size = taskDao.querySize(sql, obj);
		return size > 0;
	}

	public static boolean addTask(Task task) {
		return taskDao.insert(task);
	}

	/**
	 * 删除任务
	 * 
	 * @param dstPath
	 * @param type
	 */
	public static void deleteTask(String dstPath, EventType type) {
		String sql = "delete from sync_task where dst_path=? and status=1 and event_type=?";
		String[] obj = { dstPath, type.toString() };
		taskDao.execSql(sql, obj);
	}

}
