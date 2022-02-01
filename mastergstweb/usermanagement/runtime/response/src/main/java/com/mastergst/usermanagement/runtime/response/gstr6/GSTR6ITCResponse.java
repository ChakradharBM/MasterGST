package com.mastergst.usermanagement.runtime.response.gstr6;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastergst.usermanagement.runtime.domain.GSTR6;

public class GSTR6ITCResponse {

	@JsonProperty("status_cd")
	private String statuscd;

	@JsonProperty("status_desc")
	private String statusdesc;	
	
	private String message;
	
	@JsonProperty("error")
	private Error error;
	
	@JsonProperty("header")
	private Map<String, String> header;
	private GSTR6 data;

	public GSTR6ITCResponse() {
	}

	public GSTR6ITCResponse(String statuscd, String statusdesc) {
		this.statuscd = statuscd;
		this.statusdesc = statusdesc;
	}

	public GSTR6ITCResponse(String statuscd, Error error) {
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
	public GSTR6 getData() {
		return data;
	}
	public void setData(GSTR6 data) {
		this.data = data;
	}
	
	
}
