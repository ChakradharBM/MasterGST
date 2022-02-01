package com.mastergst.usermanagement.runtime.response.ewaybill;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EwayBillRejectResponseData {
	@JsonProperty("ewayBillNo")
	private Number ewayBillNo;
	
	@JsonProperty("ewbRejectedDate")
	private String ewbRejectedDate;

	public Number getEwayBillNo() {
		return ewayBillNo;
	}

	public void setEwayBillNo(Number ewayBillNo) {
		this.ewayBillNo = ewayBillNo;
	}

	public String getEwbRejectedDate() {
		return ewbRejectedDate;
	}

	public void setEwbRejectedDate(String ewbRejectedDate) {
		this.ewbRejectedDate = ewbRejectedDate;
	}
}
