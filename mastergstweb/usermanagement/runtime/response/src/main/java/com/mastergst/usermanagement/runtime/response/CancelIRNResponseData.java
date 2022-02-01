package com.mastergst.usermanagement.runtime.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CancelIRNResponseData {

	@JsonProperty("Irn")
	private String Irn;
	@JsonProperty("CancelDate")
	private String cancelDate;
	public String getIrn() {
		return Irn;
	}
	public void setIrn(String irn) {
		Irn = irn;
	}
	public String getCancelDate() {
		return cancelDate;
	}
	public void setCancelDate(String cancelDate) {
		this.cancelDate = cancelDate;
	}
	
	
}
