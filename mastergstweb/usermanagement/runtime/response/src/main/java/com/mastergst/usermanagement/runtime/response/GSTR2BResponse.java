package com.mastergst.usermanagement.runtime.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2B;

public class GSTR2BResponse {
	
	@JsonProperty("status_cd")
	private String statuscd;

	@JsonProperty("status_desc")
	private String statusdesc;	
	
	private String message;
	
	@JsonProperty("error")
	private Error error;
	
	@JsonProperty("header")
	private Map<String, String> header;
	
	@JsonProperty("data")
	private GSTR2B gstr2b;
	
	public GSTR2BResponse() {
	}

	public GSTR2BResponse(String statuscd, String statusdesc) {
		this.statuscd = statuscd;
		this.statusdesc = statusdesc;
	}

	public GSTR2BResponse(String statuscd, Error error) {
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
	public GSTR2B getGstr2b() {
		return gstr2b;
	}

	public void setGstr2b(GSTR2B gstr2b) {
		this.gstr2b = gstr2b;
	}
}
