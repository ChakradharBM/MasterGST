package com.mastergst.usermanagement.runtime.domain;


import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "payment_link")
public class PaymentLink extends Base{
	private String userid;
	private String category;
	private String rateofinclusivetax;
	private Double paidAmount;
	private Integer allowedInvoices;
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Double getPaidAmount() {
		return paidAmount;
	}
	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}
	public Integer getAllowedInvoices() {
		return allowedInvoices;
	}
	public void setAllowedInvoices(Integer allowedInvoices) {
		this.allowedInvoices = allowedInvoices;
	}
	public String getRateofinclusivetax() {
		return rateofinclusivetax;
	}
	public void setRateofinclusivetax(String rateofinclusivetax) {
		this.rateofinclusivetax = rateofinclusivetax;
	}
	
}
