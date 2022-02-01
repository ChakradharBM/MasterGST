package com.mastergst.usermanagement.runtime.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransGSTINResponse {
	@JsonProperty("status_cd")
	private String statuscd;

	@JsonProperty("status_desc")
	private String statusdesc;

	private String message;

	@JsonProperty("error")
	private Error error;

	@JsonProperty("header")
	private Map<String, String> header;

	TransGSTINResponseData data = new TransGSTINResponseData();

	public TransGSTINResponse() {

	}

	public TransGSTINResponse(String statuscd, String statusdesc) {
		this.statuscd = statuscd;
		this.statusdesc = statusdesc;
	}

	public TransGSTINResponse(String statuscd, Error error) {
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public TransGSTINResponseData getData() {
		return data;
	}

	public void setData(TransGSTINResponseData data) {
		this.data = data;
	}

}
