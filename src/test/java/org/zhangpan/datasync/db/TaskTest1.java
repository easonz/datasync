package org.zhangpan.datasync.db;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TaskTest1 {
	DBConnection dbConn = null;

	@Before
	public void setUp() {
		System.out.println("dbConn");
		dbConn = new DBConnection();
	}

	@After
	public void tearDown() {
		dbConn.closeConn();

	}

	/************ 测试插入记录 ***************/

	@Test
	public void testinsert() {
		TaskDAO taskdao = new TaskDAOImpl(new OptTemplate());
		for (int i = 0; i < 20; i++) {
			Task u = new Task();
			u.setDstPath("/abc");
			u.setSrcPath("/abc");
			u.setEventType("add");
			boolean b = taskdao.insert(u);
			if (b == false) {
				System.out.println("插入失败");
			} else {
				System.out.println("插入成功");
			}
		}

	}

	/************ 测试修改记录 ***************/

	@Test
	public void testupdate() {
		TaskDAO taskdao = new TaskDAOImpl(new OptTemplate());
		Task u = new Task();
		u.setDstPath("/abc");
		u.setSrcPath("/abc");
		u.setEventType("add");
		boolean b = taskdao.update(u);
		if (b == false) {
			System.out.println("更新失败");
		} else {
			System.out.println("更新成功");
		}

	}

	/************ 测试删除单条记录 ***************/

	@Test
	public void testdeleteById() {
		TaskDAO taskdao = new TaskDAOImpl(new OptTemplate());
		boolean b = taskdao.delete(2L);
		if (b == false) {
			System.out.println("删除失败");
		} else {
			System.out.println("删除成功");
		}

	}

	/************ 测试批量删除记录 ***************/

	@Test
	public void testdeleteByArray() {
		TaskDAO taskdao = new TaskDAOImpl(new OptTemplate());
		Long[] s = { 3L, 4L, 5L };
		boolean b = taskdao.delete(s);
		if (b == false) {
			System.out.println("删除失败");
		} else {
			System.out.println("删除成功");
		}

	}

	/********* 查询全部记录结果集为泛型 ************/
	@Test
	public void testqueryAll() {
		TaskDAO taskdao = new TaskDAOImpl(new OptTemplate());
		List<Task> list = taskdao.query();
		for (Task u : list) {
			System.out.println(u.getId());
		}
	}

	/********* 查询单条记录结果集为对象 ************/
	@Test
	public void testqueryAll4() {
		TaskDAO taskdao = new TaskDAOImpl(new OptTemplate());
		Task u = taskdao.query(1L);
		System.out.println(u.getDstPath());

	}

	/********* 分页查询全部记录结果集为pagemodel ************/
	@Test
	public void testqueryAll1() {
		TaskDAO taskdao = new TaskDAOImpl(new OptTemplate());
		PageModel pml = taskdao.query(2, 2);
		List<Task> list = pml.getList();
		for (Task u : list) {
			System.out.println(u.getId());
		}
	}

	/********* 分页模糊查询全部记录结果集为pagemodel ************/
	@Test
	public void testqueryAll2() {
		TaskDAO taskdao = new TaskDAOImpl(new OptTemplate());
		PageModel pml = taskdao.query(1, 2, "2");
		List<Task> list = pml.getList();
		for (Task u : list) {
			System.out.println(u.getId());
		}
	}

}
