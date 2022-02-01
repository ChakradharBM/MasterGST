/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;
import java.util.Set;

import com.mastergst.core.domain.Base;

/**
 * GSTIN Public information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public class AllGSTINTempData extends Base {

	private String userid;

	private String parentid;
	private Set<String> gstnos;

	private String gstno;

	private String clientid;
	private String status;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Set<String> getGstnos() {
		return gstnos;
	}

	public void setGstnos(Set<String> gstnos) {
		this.gstnos = gstnos;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getGstno() {
		return gstno;
	}

	public void setGstno(String gstno) {
		this.gstno = gstno;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "AllGSTINTempData [userid=" + userid + ", parentid=" + parentid + ", gstnos=" + gstnos + ", gstno="
				+ gstno + ", clientid=" + clientid + ", status=" + status + "]";
	}

	
	
	
}
