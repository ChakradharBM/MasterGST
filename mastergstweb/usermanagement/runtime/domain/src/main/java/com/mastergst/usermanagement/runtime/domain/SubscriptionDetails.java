/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.mastergst.core.domain.Base;

/**
 * Subscription details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "subscription_data")
public class SubscriptionDetails extends Base {

	private ObjectId userDocid;
	private String userid;
	private String planid;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date registeredDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date expiryDate;
	private Double paidAmount;
	private Double suvidhaPlanAmount;
	private Integer allowedClients;
	private Integer allowedInvoices;
	private Integer processedInvoices;
	private Double invoiceCost;
	private Integer processedSandboxInvoices;
	private Integer allowedCenters;
	private Integer usedCenters;
	private String apiType;

	private String subscriptionType;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date subscriptionTopupDate;
	/*@DateTimeFormat(pattern = "yyyy-MM-dd")
	private String subscriptionStartDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private String subscriptionExpiryDate;*/

	private String gstSandboxApplied;
	private String gstSandboxPayment;
	private String gstProductionApplied;
	private String gstProductionPayment;
	private String ewaybillSandboxApplied;
	private String ewaybillSandboxPayment;
	private String ewaybillProductionApplied;
	private String ewaybillProductionPayment;

	private String gstSubscriptionStartDate;
	private String gstSubscriptionExpiryDate;

	private String gstSandboxSubscriptionStartDate;
	private String gstSandboxSubscriptionExpiryDate;

	private String ewaybillSubscriptionStartDate;
	private String ewaybillSubscriptionExpiryDate;

	private String ewaybillSandboxSubscriptionStartDate;
	private String ewaybillSandboxSubscriptionExpiryDate;

	private String subscriptionStatus;
	/*
	 * modification-date : 26-12-2018 Payment Details newly added 3 variable 1.Type-
	 * subscriptionType 2.SubscriptionStartDate 3.SubscriptionExpiryDate add setters
	 * and getters also
	 */

	private String mthCd;
	private String yrCd;
	private String qrtCd;

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
	public Double getPaidAmount() {
		return paidAmount;
	}
	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}
	public Integer getAllowedClients() {
		return allowedClients;
	}
	public void setAllowedClients(Integer allowedClients) {
		this.allowedClients = allowedClients;
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
	public Double getInvoiceCost() {
		return invoiceCost;
	}
	public void setInvoiceCost(Double invoiceCost) {
		this.invoiceCost = invoiceCost;
	}
	public Integer getProcessedSandboxInvoices() {
		return processedSandboxInvoices;
	}
	public void setProcessedSandboxInvoices(Integer processedSandboxInvoices) {
		this.processedSandboxInvoices = processedSandboxInvoices;
	}
	public Integer getAllowedCenters() {
		return allowedCenters;
	}
	public void setAllowedCenters(Integer allowedCenters) {
		this.allowedCenters = allowedCenters;
	}
	public Integer getUsedCenters() {
		return usedCenters;
	}
	public void setUsedCenters(Integer usedCenters) {
		this.usedCenters = usedCenters;
	}
	public Double getSuvidhaPlanAmount() {
		return suvidhaPlanAmount;
	}
	public void setSuvidhaPlanAmount(Double suvidhaPlanAmount) {
		this.suvidhaPlanAmount = suvidhaPlanAmount;
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

	
	public Date getSubscriptionTopupDate() {
		return subscriptionTopupDate;
	}

	public void setSubscriptionTopupDate(Date subscriptionTopupDate) {
		this.subscriptionTopupDate = subscriptionTopupDate;
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

	public String getGstSubscriptionStartDate() {
		return gstSubscriptionStartDate;
	}

	public void setGstSubscriptionStartDate(String gstSubscriptionStartDate) {
		this.gstSubscriptionStartDate = gstSubscriptionStartDate;
	}

	public String getGstSubscriptionExpiryDate() {
		return gstSubscriptionExpiryDate;
	}

	public void setGstSubscriptionExpiryDate(String gstSubscriptionExpiryDate) {
		this.gstSubscriptionExpiryDate = gstSubscriptionExpiryDate;
	}

	public String getGstSandboxSubscriptionStartDate() {
		return gstSandboxSubscriptionStartDate;
	}

	public void setGstSandboxSubscriptionStartDate(String gstSandboxSubscriptionStartDate) {
		this.gstSandboxSubscriptionStartDate = gstSandboxSubscriptionStartDate;
	}

	public String getGstSandboxSubscriptionExpiryDate() {
		return gstSandboxSubscriptionExpiryDate;
	}

	public void setGstSandboxSubscriptionExpiryDate(String gstSandboxSubscriptionExpiryDate) {
		this.gstSandboxSubscriptionExpiryDate = gstSandboxSubscriptionExpiryDate;
	}

	public String getEwaybillSubscriptionStartDate() {
		return ewaybillSubscriptionStartDate;
	}

	public void setEwaybillSubscriptionStartDate(String ewaybillSubscriptionStartDate) {
		this.ewaybillSubscriptionStartDate = ewaybillSubscriptionStartDate;
	}

	public String getEwaybillSubscriptionExpiryDate() {
		return ewaybillSubscriptionExpiryDate;
	}

	public void setEwaybillSubscriptionExpiryDate(String ewaybillSubscriptionExpiryDate) {
		this.ewaybillSubscriptionExpiryDate = ewaybillSubscriptionExpiryDate;
	}

	public String getEwaybillSandboxSubscriptionStartDate() {
		return ewaybillSandboxSubscriptionStartDate;
	}

	public void setEwaybillSandboxSubscriptionStartDate(String ewaybillSandboxSubscriptionStartDate) {
		this.ewaybillSandboxSubscriptionStartDate = ewaybillSandboxSubscriptionStartDate;
	}

	public String getEwaybillSandboxSubscriptionExpiryDate() {
		return ewaybillSandboxSubscriptionExpiryDate;
	}

	public void setEwaybillSandboxSubscriptionExpiryDate(String ewaybillSandboxSubscriptionExpiryDate) {
		this.ewaybillSandboxSubscriptionExpiryDate = ewaybillSandboxSubscriptionExpiryDate;
	}
	public String getSubscriptionStatus() {
		return subscriptionStatus;
	}
	public void setSubscriptionStatus(String subscriptionStatus) {
		this.subscriptionStatus = subscriptionStatus;
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

	public String getQrtCd() {
		return qrtCd;
	}

	public void setQrtCd(String qrtCd) {
		this.qrtCd = qrtCd;
	}

	public ObjectId getUserDocid() {
		return userDocid;
	}

	public void setUserDocid(ObjectId userDocid) {
		this.userDocid = userDocid;
	}

	@Override
	public String toString() {
		return "SubscriptionDetails [userDocid=" + userDocid + ", userid=" + userid + ", registeredDate="
				+ registeredDate + ", expiryDate=" + expiryDate + ", paidAmount=" + paidAmount + ", allowedInvoices="
				+ allowedInvoices + ", processedInvoices=" + processedInvoices + ", apiType=" + apiType
				+ ", subscriptionType=" + subscriptionType + ", subscriptionStatus=" + subscriptionStatus + ", mthCd="
				+ mthCd + ", yrCd=" + yrCd + ", qrtCd=" + qrtCd + "]";
	}
}
