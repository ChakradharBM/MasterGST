package com.mastergst.usermanagement.runtime.domain;

public class PaymentFilter {
	
	private String[] financialYear;
	private String[] month;
	private String[] vendor;
	private String[] gstno;
	private String[] paymentMode;
	
	public String[] getVendor() {
		return vendor;
	}
	
	public void setVendor(String[] vendor) {
		this.vendor = vendor;
	}
	
	public void setVendor(String vendor) {
		this.vendor = vendor == null ? null : vendor.split(",");
	}
	
	public String[] getFinancialYear() {
		return financialYear;
	}
	
	public void setFinancialYear(String[] financialYear) {
		this.financialYear = financialYear;
	}
	
	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear == null ? null : financialYear.split(",");
	}
	
	public String[] getMonth() {
		return month;
	}
	
	public void setMonth(String[] month) {
		this.month = month;
	}
	
	public void setMonth(String month) {
		this.month = month == null ? null : month.split(",");
	}
	
	public String[] getGstno() {
		return gstno;
	}
	
	public void setGstno(String gstno) {
		this.gstno = gstno == null ? null : gstno.split(",");
	}
	
	public String[] getPaymentMode() {
		return paymentMode;
	}
	
	public void setPaymentMode(String[] paymentMode) {
		this.paymentMode = paymentMode;
	}
	
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode == null ? null : paymentMode.split(",");
	}
}
