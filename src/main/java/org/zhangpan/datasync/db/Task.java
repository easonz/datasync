package org.zhangpan.datasync.db;

import org.apache.commons.lang3.StringUtils;

public class Task {

	private Long id;
	private String dstPath;
	private String srcPath;
	private String eventType;
	private String eventDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDstPath() {
		return dstPath;
	}

	public void setDstPath(String dstPath) {
		this.dstPath = dstPath;
	}

	public String getSrcPath() {
		return srcPath;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	@Override
	public String toString(){
		String[] taskInfo = { String.valueOf(id == null ? -1 : id), 
				dstPath,
				srcPath, 
				eventType, 
				eventDate };
		return StringUtils.join(taskInfo, ",");
	}
}
