package com.mastergst.usermanagement.runtime.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EwayBillVehicleUpdateResponseData {

	@JsonProperty("vehUpdDate")
	private String vehUpdDate;
	@JsonProperty("validUpto")
	private String validUpto;
	public String getVehUpdDate() {
		return vehUpdDate;
	}
	public void setVehUpdDate(String vehUpdDate) {
		this.vehUpdDate = vehUpdDate;
	}
	public String getValidUpto() {
		return validUpto;
	}
	public void setValidUpto(String validUpto) {
		this.validUpto = validUpto;
	}
	@Override
	public String toString() {
		return "EwayBillVehicleUpdateResponseData [vehUpdDate=" + vehUpdDate + ", validUpto=" + validUpto + "]";
	}
	
	
}
