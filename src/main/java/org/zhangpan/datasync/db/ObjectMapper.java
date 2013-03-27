package org.zhangpan.datasync.db;

import java.sql.ResultSet;

public interface ObjectMapper {
	public Object mapping(ResultSet rs);

}