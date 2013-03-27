package org.zhangpan.datasync.db;

import java.sql.ResultSet;
import java.util.List;

@SuppressWarnings("unchecked")
public class TaskDAOImpl implements TaskDAO {

	private OptTemplate optTemplate = null;

	public TaskDAOImpl(OptTemplate optTemplate) {
		super();
		this.optTemplate = optTemplate;
	}

	public boolean delete(Long id) {
		String sql = "delete from sync_task where id=?";
		Object[] obj = { id };
		return optTemplate.update(sql, obj, false);
	}

	public boolean delete(Long[] userIds) {
		StringBuffer sbStr = new StringBuffer();
		Object[] obj = userIds;
		;
		for (int i = 0; i < userIds.length; i++) {
			sbStr.append("?,");
		}
		String sql = "delete from sync_task where id in("
				+ sbStr.substring(0, sbStr.length() - 1) + ")";
		return optTemplate.update(sql, obj, false);
	}

	public boolean insert(Task task) {
		String sql = "insert into sync_task(dst_path, src_path, event_type, event_date) values(?,?,?,?)";
		Object[] obj = { task.getDstPath(), task.getSrcPath(), task.getEventType(), task.getEventDate() };
		return optTemplate.update(sql, obj, false);
	}

	public List<Task> query() {
		String sql = "select * from sync_task";
		Object[] obj = {};
		return (List<Task>) optTemplate.query(sql, obj,
				new TaskDAOObjectMapper());

	}

	public Task query(Long id) {
		String sql = "select * from sync_task";
		Object[] obj = {};
		return (Task) optTemplate.query(sql, obj, new TaskDAOObjectMapper())
				.get(0);
	}

	public PageModel query(int pageNo, int pageSize) {
		String sql1 = "select * from sync_task";
		Object[] obj1 = {};
		List<Task> list1 = (List<Task>) optTemplate.query(sql1, obj1,
				new TaskDAOObjectMapper());
		int i = list1.size();
		String sql = "select * from (select j.*,rownum rn from (select * from sync_task) j where rownum<=?) where rn>?";
		Object[] obj = { pageNo * pageSize, (pageNo - 1) * pageSize };
		List<Task> list = (List<Task>) optTemplate.query(sql, obj,
				new TaskDAOObjectMapper());
		PageModel pagemodel = new PageModel();
		pagemodel.setPageNo(pageNo);
		pagemodel.setPageSize(pageSize);
		pagemodel.setList(list);
		pagemodel.setTotalRecords(i);
		return pagemodel;
	}

	public PageModel query(int pageNo, int pageSize, String condition) {
		String sql1 = "select * from sync_task";
		Object[] obj1 = {};
		List<Task> list1 = (List<Task>) optTemplate.query(sql1, obj1,
				new TaskDAOObjectMapper());
		int i = list1.size();
		String sql = "select * from (select j.*,rownum rn from (select * from sync_task where id like '"
				+ condition
				+ "%' or dst_path like '"
				+ condition
				+ "%' or src_path like '"
				+ condition
				+ "%') j where rownum<=?) where rn>?";
		Object[] obj = { pageNo * pageSize, (pageNo - 1) * pageSize };
		List<Task> list = (List<Task>) optTemplate.query(sql, obj,
				new TaskDAOObjectMapper());
		PageModel pagemodel = new PageModel();
		pagemodel.setPageNo(pageNo);
		pagemodel.setPageSize(pageSize);
		pagemodel.setList(list);
		pagemodel.setTotalRecords(i);
		return pagemodel;
	}

	public boolean update(Task task) {
		String sql = "update sync_task set dst_path=?, src_path=?, event_type=?, event_date=? where id=?";
		Object[] obj = { task.getDstPath(), task.getSrcPath(),
				task.getEventType(), task.getEventDate(), task.getId() };
		return optTemplate.update(sql, obj, false);
	}

}

class TaskDAOObjectMapper implements ObjectMapper {
	public Object mapping(ResultSet rs) {
		Task u = new Task();
		try {

			u.setId(rs.getLong("id"));
			u.setDstPath(rs.getString("dst_path"));
			u.setSrcPath(rs.getString("src_path"));
			u.setEventType(rs.getString("event_type"));
			u.setEventDate(rs.getString("event_date"));

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return u;
	}

}