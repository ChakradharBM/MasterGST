/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR4 Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreceedingDocDetails {
	@JsonProperty("InvNo")
	private String invNo;
	@JsonProperty("InvDt")
	private String invDt;
	@JsonProperty("OthRefNo")
	private String othRefNo;
	public String getOthRefNo() {
		return othRefNo;
	}
	public String getInvNo() {
		return invNo;
	}
	public void setInvNo(String invNo) {
		this.invNo = invNo;
	}
	public String getInvDt() {
		return invDt;
	}
	public void setInvDt(String invDt) {
		this.invDt = invDt;
	}
	public void setOthRefNo(String othRefNo) {
		this.othRefNo = othRefNo;
	}
	
	
}
