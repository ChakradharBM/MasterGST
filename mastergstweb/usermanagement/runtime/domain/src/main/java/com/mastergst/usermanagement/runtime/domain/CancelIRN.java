package com.mastergst.usermanagement.runtime.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CancelIRN {
	@JsonProperty("Irn")
	private String irn;
	@JsonProperty("CnlRsn")
	private String cnlRsn;
	@JsonProperty("CnlRem")
	private String cnlRem;
	public String getIrn() {
		return irn;
	}
	public void setIrn(String irn) {
		this.irn = irn;
	}
	public String getCnlRsn() {
		return cnlRsn;
	}
	public void setCnlRsn(String cnlRsn) {
		this.cnlRsn = cnlRsn;
	}
	public String getCnlRem() {
		return cnlRem;
	}
	public void setCnlRem(String cnlRem) {
		this.cnlRem = cnlRem;
	}
	
	
}
