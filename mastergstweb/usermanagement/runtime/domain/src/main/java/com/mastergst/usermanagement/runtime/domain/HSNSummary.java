/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * This class is HSN Summary POJO.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HSNSummary {

	@JsonProperty("chksum")
	private String chksum;
	
	@JsonProperty("flag")
	private String flag;
	@JsonProperty("error_msg")
	private String errorMsg;
	
	@JsonProperty("data")
	private List<HSNData> data=Lists.newArrayList();
	
	private List<HSNData> det=Lists.newArrayList();

	public HSNSummary() {

	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List<HSNData> getData() {
		return data;
	}

	public void setData(List<HSNData> data) {
		this.data = data;
	}

	public List<HSNData> getDet() {
		return det;
	}

	public void setDet(List<HSNData> det) {
		this.det = det;
	}
}
