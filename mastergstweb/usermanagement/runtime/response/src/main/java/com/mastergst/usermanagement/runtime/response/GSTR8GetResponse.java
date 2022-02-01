package com.mastergst.usermanagement.runtime.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastergst.usermanagement.runtime.domain.GSTR8;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GSTR8GetResponse{
	
	@JsonProperty("status_cd")
	private String statuscd;

	@JsonProperty("status_desc")
	private String statusdesc;	
	
	@JsonProperty("error")
	private Error error;
	
	@JsonProperty("header")
	private Map<String, String> header;
	
	@JsonProperty("data")
	private GSTR8 data;
	
	public GSTR8GetResponse() {

	}

	public GSTR8GetResponse(String statuscd, String statusdesc) {
		this.statuscd = statuscd;
		this.statusdesc = statusdesc;
	}

	public GSTR8GetResponse(String statuscd, Error error) {
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

	public GSTR8 getData() {
		return data;
	}

	public void setData(GSTR8 data) {
		this.data = data;
	}
}
