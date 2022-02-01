package com.mastergst.usermanagement.runtime.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocPerDetails {
	@JsonProperty("InvStDt")
	private String invStDt;
	@JsonProperty("InvEndDt")
	private String invEndDt;
	public String getInvStDt() {
		return invStDt;
	}
	public void setInvStDt(String invStDt) {
		this.invStDt = invStDt;
	}
	public String getInvEndDt() {
		return invEndDt;
	}
	public void setInvEndDt(String invEndDt) {
		this.invEndDt = invEndDt;
	}
	
}
