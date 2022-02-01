/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;


import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenerateIRNResponse {
	@JsonProperty("status_cd")
	private String statuscd;

	@JsonProperty("status_desc")
	private String statusdesc;	
	
	@JsonProperty("info")
	private String info;

	@JsonProperty("ErrorDetails")
	private List<Map<String,String>> errors;
	
	@JsonProperty("header")
	private Map<String, String> header;
	
	@JsonRawValue
	GenerateIRNResponseData data = new GenerateIRNResponseData();

	public GenerateIRNResponse() {

	}

	public GenerateIRNResponse(String statuscd, String statusdesc) {
		this.statuscd = statuscd;
		this.statusdesc = statusdesc;
	}

	public String getStatuscd() {
		return statuscd;
	}

	public void setStatuscd(String statuscd) {
		this.statuscd = statuscd;
	}

	public String getStatusdesc() {
		return statusdesc;
	}

	public void setStatusdesc(String statusdesc) {
		this.statusdesc = statusdesc;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public List<Map<String, String>> getErrors() {
		return errors;
	}

	public void setErrors(List<Map<String, String>> errors) {
		this.errors = errors;
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public GenerateIRNResponseData getData() {
		return data;
	}

	public void setData(GenerateIRNResponseData data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "GenerateIRNResponse [statuscd=" + statuscd + ", statusdesc=" + statusdesc + ", info=" + info
				+ ", errors=" + errors + ", header=" + header + ", data=" + data + "]";
	}

	

}
