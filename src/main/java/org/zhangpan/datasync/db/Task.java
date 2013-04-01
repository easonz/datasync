package org.zhangpan.datasync.db;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class Task {

	private Long id;
	private String dstPath;
	private String srcPath;
	private EventType eventType;
	private int fileType;
	private int status;
	private Date eventDate;

	public Task() {
		fileType = -1;
		status = -1;
		eventDate = new Date();
	}

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

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public int getFileType() {
		return fileType;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	@Override
	public String toString(){
		String[] taskInfo = { String.valueOf(id == null ? -1 : id), 
				dstPath,
				srcPath, 
				eventType.toString(), 
				String.valueOf(fileType),
				eventDate.toString() };
		return StringUtils.join(taskInfo, ",");
	}
}
