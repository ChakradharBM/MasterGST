package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.util.ImportResponse;

@Document(collection = "bulkimport_task")
public class BulkImportTask {

	private String id;
	private String userid;
	private String clientid;
	@CreatedDate
	private Date taskStartTime;
	@LastModifiedDate
	private Date taskEndTime;
	private String task;
	private Long imported;
	private Long failure;
	private String type;
	private String fileName;

	
	private ImportResponse response;

	

	public BulkImportTask(String userid, String clientid, String task, String type, String fileName) {
		super();
		this.userid = userid;
		this.clientid = clientid;
		this.task = task;
		this.type = type;
		this.fileName = fileName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BulkImportTask() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public Date getTaskStartTime() {
		return taskStartTime;
	}

	public void setTaskStartTime(Date taskStartTime) {
		this.taskStartTime = taskStartTime;
	}

	public Date getTaskEndTime() {
		return taskEndTime;
	}

	public void setTaskEndTime(Date taskEndTime) {
		this.taskEndTime = taskEndTime;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public ImportResponse getResponse() {
		return response;
	}

	public void setResponse(ImportResponse response) {
		this.response = response;
	}

	public Long getImported() {
		return imported;
	}

	public void setImported(Long imported) {
		this.imported = imported;
	}

	public Long getFailure() {
		return failure;
	}

	public void setFailure(Long failure) {
		this.failure = failure;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
