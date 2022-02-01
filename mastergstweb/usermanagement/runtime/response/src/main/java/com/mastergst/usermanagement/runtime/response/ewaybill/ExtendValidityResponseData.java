package com.mastergst.usermanagement.runtime.response.ewaybill;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtendValidityResponseData {
	@JsonProperty("ewayBillNo")
	private String ewayBillNo;
	@JsonProperty("updatedDate")
	private String updatedDate;
	@JsonProperty("validUpto")
	private String validUpto;

	public String getEwayBillNo() {
		return ewayBillNo;
	}

	public void setEwayBillNo(String ewayBillNo) {
		this.ewayBillNo = ewayBillNo;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getValidUpto() {
		return validUpto;
	}

	public void setValidUpto(String validUpto) {
		this.validUpto = validUpto;
	}

}
