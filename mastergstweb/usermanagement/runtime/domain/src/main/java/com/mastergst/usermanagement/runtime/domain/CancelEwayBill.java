package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

public class CancelEwayBill {

	private Number ewbNo;
	private Number cancelRsnCode;
	private String cancelRmrk;
	private List<String> invoiceIds;
	
	public Number getEwbNo() {
		return ewbNo;
	}
	public void setEwbNo(Number ewbNo) {
		this.ewbNo = ewbNo;
	}
	public Number getCancelRsnCode() {
		return cancelRsnCode;
	}
	public void setCancelRsnCode(Number cancelRsnCode) {
		this.cancelRsnCode = cancelRsnCode;
	}
	public String getCancelRmrk() {
		return cancelRmrk;
	}
	public void setCancelRmrk(String cancelRmrk) {
		this.cancelRmrk = cancelRmrk;
	}
	public List<String> getInvoiceIds() {
		return invoiceIds;
	}
	public void setInvoiceIds(List<String> invoiceIds) {
		this.invoiceIds = invoiceIds;
	}
	
	
}
