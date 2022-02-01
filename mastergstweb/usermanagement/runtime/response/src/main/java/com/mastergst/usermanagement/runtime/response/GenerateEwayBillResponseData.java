package com.mastergst.usermanagement.runtime.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenerateEwayBillResponseData {

	@JsonProperty("ewayBillNo")
	private Number ewayBillNo;
	@JsonProperty("ewayBillDate")
	private String ewayBillDate;
	
	@JsonProperty("validUpto")
	private String validUpto;

	public Number getEwayBillNo() {
		return ewayBillNo;
	}

	public void setEwayBillNo(Number ewayBillNo) {
		this.ewayBillNo = ewayBillNo;
	}

	public String getEwayBillDate() {
		return ewayBillDate;
	}

	public void setEwayBillDate(String ewayBillDate) {
		this.ewayBillDate = ewayBillDate;
	}

	public String getValidUpto() {
		return validUpto;
	}

	public void setValidUpto(String validUpto) {
		this.validUpto = validUpto;
	}

	@Override
	public String toString() {
		return "GenerateEwayBillResponseData [ewayBillNo=" + ewayBillNo + ", ewayBillDate=" + ewayBillDate
				+ ", validUpto=" + validUpto + ", getEwayBillNo()=" + getEwayBillNo() + ", getEwayBillDate()="
				+ getEwayBillDate() + ", getValidUpto()=" + getValidUpto() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
}
