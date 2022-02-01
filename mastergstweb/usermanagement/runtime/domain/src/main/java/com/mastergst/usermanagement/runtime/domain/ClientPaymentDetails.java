package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class ClientPaymentDetails {

	private String userid;
	private String planid;
	private String pymtrequestid;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date paymentDate;
	private Double amount;
	private String orderId;
	private String trackingId;
	private String status;
	private String response;
	private String customerName;
	private String customerGSTN;
	private String customerPOS;
	private String customerAddress;
	private String failureMessage;
	private String statename;
	private String apiType;

	private String subscriptionType;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private String subscriptionStartDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private String subscriptionExpiryDate;

	private String partnerPayment;
	private PartnerClient partnerClient;

	private String mthCd;
	private String yrCd;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPlanid() {
		return planid;
	}

	public void setPlanid(String planid) {
		this.planid = planid;
	}

	public String getPymtrequestid() {
		return pymtrequestid;
	}

	public void setPymtrequestid(String pymtrequestid) {
		this.pymtrequestid = pymtrequestid;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerGSTN() {
		return customerGSTN;
	}

	public void setCustomerGSTN(String customerGSTN) {
		this.customerGSTN = customerGSTN;
	}

	public String getCustomerPOS() {
		return customerPOS;
	}

	public void setCustomerPOS(String customerPOS) {
		this.customerPOS = customerPOS;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getFailureMessage() {
		return failureMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}

	public String getStatename() {
		return statename;
	}

	public void setStatename(String statename) {
		this.statename = statename;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(String subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public String getSubscriptionStartDate() {
		return subscriptionStartDate;
	}

	public void setSubscriptionStartDate(String subscriptionStartDate) {
		this.subscriptionStartDate = subscriptionStartDate;
	}

	public String getSubscriptionExpiryDate() {
		return subscriptionExpiryDate;
	}

	public void setSubscriptionExpiryDate(String subscriptionExpiryDate) {
		this.subscriptionExpiryDate = subscriptionExpiryDate;
	}

	public String getPartnerPayment() {
		return partnerPayment;
	}

	public void setPartnerPayment(String partnerPayment) {
		this.partnerPayment = partnerPayment;
	}

	public String getMthCd() {
		return mthCd;
	}

	public void setMthCd(String mthCd) {
		this.mthCd = mthCd;
	}

	public String getYrCd() {
		return yrCd;
	}

	public void setYrCd(String yrCd) {
		this.yrCd = yrCd;
	}

	public PartnerClient getPartnerClient() {
		return partnerClient;
	}

	public void setPartnerClient(PartnerClient partnerClient) {
		this.partnerClient = partnerClient;
	}
}
