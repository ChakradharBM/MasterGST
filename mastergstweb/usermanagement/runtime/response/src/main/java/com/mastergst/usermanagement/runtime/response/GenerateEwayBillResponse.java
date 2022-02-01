/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;


import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenerateEwayBillResponse {
	@JsonProperty("status_cd")
	private String statuscd;

	@JsonProperty("status_desc")
	private String statusdesc;	
	
	private String message;
	
	@JsonProperty("error")
	private Error error;
	
	@JsonProperty("header")
	private Map<String, String> header;
	
	
	GenerateEwayBillResponseData data = new GenerateEwayBillResponseData();

	public GenerateEwayBillResponse() {

	}

	public GenerateEwayBillResponse(String statuscd, String statusdesc) {
		this.statuscd = statuscd;
		this.statusdesc = statusdesc;
	}

	public GenerateEwayBillResponse(String statuscd, Error error) {
		this.statuscd = statuscd;
		this.error = error;
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

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	

	public GenerateEwayBillResponseData getData() {
		return data;
	}

	public void setData(GenerateEwayBillResponseData data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "EwayBillResponse [statuscd=" + statuscd + ", statusdesc=" + statusdesc + ", message=" + message
				+ ", error=" + error + ", header=" + header + ", data=" + data + "]";
	}

	

}
