package com.mastergst.usermanagement.runtime.domain.ewaybill;

import java.util.List;

public class ExtendValidityDO {
	private Number extnRsnCode;
	private String extnRemarks;
	private String consignmentStatus;
	private String transitType;
	private Number remainingDistance;
	private List<String> invoiceIds;
	
	public Number getExtnRsnCode() {
		return extnRsnCode;
	}
	public void setExtnRsnCode(Number extnRsnCode) {
		this.extnRsnCode = extnRsnCode;
	}
	public String getExtnRemarks() {
		return extnRemarks;
	}
	public void setExtnRemarks(String extnRemarks) {
		this.extnRemarks = extnRemarks;
	}
	public String getConsignmentStatus() {
		return consignmentStatus;
	}
	public void setConsignmentStatus(String consignmentStatus) {
		this.consignmentStatus = consignmentStatus;
	}
	public String getTransitType() {
		return transitType;
	}
	public void setTransitType(String transitType) {
		this.transitType = transitType;
	}
	public Number getRemainingDistance() {
		return remainingDistance;
	}
	public void setRemainingDistance(Number remainingDistance) {
		this.remainingDistance = remainingDistance;
	}
	public List<String> getInvoiceIds() {
		return invoiceIds;
	}
	public void setInvoiceIds(List<String> invoiceIds) {
		this.invoiceIds = invoiceIds;
	}
	
}
