package com.mastergst.login.runtime.domain;

import java.util.Date;

import org.bson.types.ObjectId;

public class ApiExceedsUsers {

	private ObjectId id;
	private String userid;
	private String docId;
	private Double paidAmount;
	private Integer allowedInvoices;
	private Integer processedInvoices;
	private Integer exceedInvoices;

	private String apiType;
	private String subscriptionType;

	private Date registeredDate;
	private Date expiryDate;

	private User user;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
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

	public Integer getProcessedInvoices() {
		return processedInvoices;
	}

	public void setProcessedInvoices(Integer processedInvoices) {
		this.processedInvoices = processedInvoices;
	}

	public Integer getExceedInvoices() {
		return exceedInvoices;
	}

	public void setExceedInvoices(Integer exceedInvoices) {
		this.exceedInvoices = exceedInvoices;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public ObjectId getId() {
		return id;
	}

	public Date getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	public void setSubscriptionType(String subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public String getSubscriptionType() {
		return subscriptionType;
	}

	@Override
	public String toString() {
		return "ApiExceedsUsers [id=" + id + ", userid=" + userid + ", docId=" + docId + ", paidAmount=" + paidAmount
				+ ", allowedInvoices=" + allowedInvoices + ", processedInvoices=" + processedInvoices
				+ ", exceedInvoices=" + exceedInvoices + ", apiType=" + apiType + ", subscriptionType="
				+ subscriptionType + ", registeredDate=" + registeredDate + ", expiryDate=" + expiryDate + ", user="
				+ user + "]";
	}
}
