package com.mastergst.usermanagement.runtime.domain;

public class PartnerFilter {
	public String[] partnerType;
	public String[] partnername;
	
	
	public String[] getPartnerType() {
		return partnerType;
	}
	public void setPartnerType(String[] partnerType) {
		this.partnerType = partnerType;
	}
	public void setPartnerType(String partnerType) {
		this.partnerType = partnerType == null ? null : partnerType.split(",");
	}
	
	public String[] getPartnername() {
		return partnername;
	}
	public void setPartnername(String[] partnername) {
		this.partnername = partnername;
	}
	public void setPartnername(String partnername) {
		this.partnername = partnername == null ? null : partnername.split(",");
	}
}
