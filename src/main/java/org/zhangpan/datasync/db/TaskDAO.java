package org.zhangpan.datasync.db;

import java.util.List;

public interface TaskDAO {

	public boolean insert(Task task); // 增

	public boolean delete(Long id); // 单条删除

	public boolean delete(Long[] taskIds); // 批量删除

	public boolean update(Task task); // 修改

	public List<Task> query(); // 全部查询

	public Task query(Long id); // 单记录查询

	public PageModel query(int pageNo, int pageSize); // 分页查询

	public PageModel query(int pageNo, int pageSize, String condition); // 分页模糊查询

}
