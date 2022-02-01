/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.google.common.collect.Maps;

/**
 * GST Return Summary Information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public class GSTReturnSummary {

	@Id
	private ObjectId id;	

	private String returntype;
	private String description="";
	private String status;
	private String duedate;
	private String fieldName;
	private Double fieldValue;
	private String active;
	private Map<String, Integer> statusMap;
	
	
	public GSTReturnSummary() {
		this.id = ObjectId.get();
		statusMap=Maps.newHashMap();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getReturntype() {
		return returntype;
	}

	public void setReturntype(String returntype) {
		this.returntype = returntype;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDuedate() {
		return duedate;
	}

	public void setDuedate(String duedate) {
		this.duedate = duedate;
	}

	public Map<String, Integer> getStatusMap() {
		return statusMap;
	}

	public void setStatusMap(Map<String, Integer> statusMap) {
		this.statusMap = statusMap;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Double getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(Double fieldValue) {
		this.fieldValue = fieldValue;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "GSTReturnSummary [id=" + id + ", returntype=" + returntype
				+ ", description=" + description + ", status=" + status
				+ ", duedate=" + duedate + ", fieldName=" + fieldName
				+ ", fieldValue=" + fieldValue + ", active=" + active
				+ ", statusMap=" + statusMap + "]";
	}
	
	

}
