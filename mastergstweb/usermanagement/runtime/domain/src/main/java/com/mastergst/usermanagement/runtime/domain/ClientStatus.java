/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Client status information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "client_status")
public class ClientStatus extends Base {

	private String clientId;
	private String returnType;
	private String returnPeriod;
	private String status;
	private String arn;
	private String mof;
	private Date dof;
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getReturnPeriod() {
		return returnPeriod;
	}
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getDof() {
		return dof;
	}
	public void setDof(Date dof) {
		this.dof = dof;
	}
	public String getArn() {
		return arn;
	}
	public void setArn(String arn) {
		this.arn = arn;
	}
	public String getMof() {
		return mof;
	}
	public void setMof(String mof) {
		this.mof = mof;
	}
}
