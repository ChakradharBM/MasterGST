/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.mastergst.core.domain.Base;

/**
 * Payment details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "payment_data")
public class PaymentDetails extends Base {

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
	
	private String gstSandboxApplied;
	private String gstSandboxPayment;
	private String gstProductionApplied;
	private String gstProductionPayment;
	private String ewaybillSandboxApplied;
	private String ewaybillSandboxPayment;
	private String ewaybillProductionApplied;
	private String ewaybillProductionPayment;
	
	private Double rate;
	private Double cgstrate;
	private Double cgstamount;
	
	private Double sgstrate;
	private Double sgstamount;
	
	private Double igstrate;
	private Double igstamount;
	
	private Double totalAmount;
	private String couponCode;
	private Double discountAmount;

	private String partnerPayment;
		//Done, Pending, Partially Paid 

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

	public String getGstSandboxApplied() {
		return gstSandboxApplied;
	}

	public void setGstSandboxApplied(String gstSandboxApplied) {
		this.gstSandboxApplied = gstSandboxApplied;
	}

	public String getGstSandboxPayment() {
		return gstSandboxPayment;
	}

	public void setGstSandboxPayment(String gstSandboxPayment) {
		this.gstSandboxPayment = gstSandboxPayment;
	}

	public String getGstProductionApplied() {
		return gstProductionApplied;
	}

	public void setGstProductionApplied(String gstProductionApplied) {
		this.gstProductionApplied = gstProductionApplied;
	}

	public String getGstProductionPayment() {
		return gstProductionPayment;
	}

	public void setGstProductionPayment(String gstProductionPayment) {
		this.gstProductionPayment = gstProductionPayment;
	}

	public String getEwaybillSandboxApplied() {
		return ewaybillSandboxApplied;
	}

	public void setEwaybillSandboxApplied(String ewaybillSandboxApplied) {
		this.ewaybillSandboxApplied = ewaybillSandboxApplied;
	}

	public String getEwaybillSandboxPayment() {
		return ewaybillSandboxPayment;
	}

	public void setEwaybillSandboxPayment(String ewaybillSandboxPayment) {
		this.ewaybillSandboxPayment = ewaybillSandboxPayment;
	}

	public String getEwaybillProductionApplied() {
		return ewaybillProductionApplied;
	}

	public void setEwaybillProductionApplied(String ewaybillProductionApplied) {
		this.ewaybillProductionApplied = ewaybillProductionApplied;
	}

	public String getEwaybillProductionPayment() {
		return ewaybillProductionPayment;
	}

	public void setEwaybillProductionPayment(String ewaybillProductionPayment) {
		this.ewaybillProductionPayment = ewaybillProductionPayment;
	}

	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public Double getCgstrate() {
		return cgstrate;
	}
	public void setCgstrate(Double cgstrate) {
		this.cgstrate = cgstrate;
	}
	public Double getCgstamount() {
		return cgstamount;
	}
	public void setCgstamount(Double cgstamount) {
		this.cgstamount = cgstamount;
	}
	public Double getSgstrate() {
		return sgstrate;
	}
	public void setSgstrate(Double sgstrate) {
		this.sgstrate = sgstrate;
	}
	public Double getSgstamount() {
		return sgstamount;
	}
	public void setSgstamount(Double sgstamount) {
		this.sgstamount = sgstamount;
	}
	public Double getIgstrate() {
		return igstrate;
	}
	public void setIgstrate(Double igstrate) {
		this.igstrate = igstrate;
	}
	public Double getIgstamount() {
		return igstamount;
	}
	public void setIgstamount(Double igstamount) {
		this.igstamount = igstamount;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	public Double getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(Double discountAmount) {
		this.discountAmount = discountAmount;
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
}
