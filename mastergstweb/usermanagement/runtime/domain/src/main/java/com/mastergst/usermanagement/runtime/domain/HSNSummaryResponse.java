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
public class HSNSummaryResponse {

	@JsonProperty("chksum")
	private String chksum;
	
	@JsonProperty("flag")
	private String flag;
	@JsonProperty("error_msg")
	private String errorMsg;
	
	@JsonProperty("data")
	private List<HSNResponse> data=Lists.newArrayList();
	
	private List<HSNResponse> det=Lists.newArrayList();

	public HSNSummaryResponse() {

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

	public List<HSNResponse> getData() {
		return data;
	}

	public void setData(List<HSNResponse> data) {
		this.data = data;
	}

	public List<HSNResponse> getDet() {
		return det;
	}

	public void setDet(List<HSNResponse> det) {
		this.det = det;
	}
}
