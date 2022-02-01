/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastergst.usermanagement.runtime.domain.AnxInvoiceSupport;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;

/**
 * This class MasterGST response POJO.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NewReturnsResponse {
	@JsonProperty("status_cd")
	private String statuscd;

	@JsonProperty("status_desc")
	private String statusdesc;	
	
	@JsonProperty("error")
	private Error error;
	
	@JsonProperty("header")
	private Map<String, String> header;
	
	@JsonProperty("data")
	private AnxInvoiceSupport data;

	public NewReturnsResponse() {

	}

	public NewReturnsResponse(String statuscd, String statusdesc) {
		this.statuscd = statuscd;
		this.statusdesc = statusdesc;
	}

	public NewReturnsResponse(String statuscd, Error error) {
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

	public AnxInvoiceSupport getData() {
		return data;
	}

	public void setData(AnxInvoiceSupport data) {
		this.data = data;
	}

}
