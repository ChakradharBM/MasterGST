package com.mastergst.usermanagement.runtime.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EwayBillCancelResponseData {

	@JsonProperty("ewayBillNo")
	private Number ewayBillNo;
	@JsonProperty("cancelDate")
	private String cancelDate;
	
	public Number getEwayBillNo() {
		return ewayBillNo;
	}
	public void setEwayBillNo(Number ewayBillNo) {
		this.ewayBillNo = ewayBillNo;
	}
	public String getCancelDate() {
		return cancelDate;
	}
	public void setCancelDate(String cancelDate) {
		this.cancelDate = cancelDate;
	}
	@Override
	public String toString() {
		return "EwayBillCancelResponseData [ewayBillNo=" + ewayBillNo + ", cancelDate=" + cancelDate
				+ ", getEwayBillNo()=" + getEwayBillNo() + ", getCancelDate()=" + getCancelDate() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	
}
