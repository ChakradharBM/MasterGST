/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is Track Status POJO.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TrackResponse {

	private String arn;
	
	@JsonProperty("ret_prd")
	private String retPeriod;
	private String mof;
	private String dof;
	private String rtntype;
	private String status;
	private String valid;
	
	public String getArn() {
		return arn;
	}
	public void setArn(String arn) {
		this.arn = arn;
	}
	public String getRetPeriod() {
		return retPeriod;
	}
	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}
	public String getMof() {
		return mof;
	}
	public void setMof(String mof) {
		this.mof = mof;
	}
	public String getDof() {
		return dof;
	}
	public void setDof(String dof) {
		this.dof = dof;
	}
	public String getRtntype() {
		return rtntype;
	}
	public void setRtntype(String rtntype) {
		this.rtntype = rtntype;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getValid() {
		return valid;
	}
	public void setValid(String valid) {
		this.valid = valid;
	}
	
	
}
