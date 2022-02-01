package com.mastergst.usermanagement.runtime.domain;

public class ExpensesFilter {
	private String[] category;
	private String[] paymentMode;
	private String fromtime;
	private String totime;
	
	public String[] getCategory() {
		return category;
	}
	public void setCategory(String[] category) {
		this.category = category;
	}
	public void setCategory(String category) {
		this.category = category == null ? null : category.split(",");
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
	public String getFromtime() {
		return fromtime;
	}
	public void setFromtime(String fromtime) {
		this.fromtime = fromtime;
	}
	public String getTotime() {
		return totime;
	}
	public void setTotime(String totime) {
		this.totime = totime;
	}
	
}
