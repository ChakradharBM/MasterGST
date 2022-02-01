package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;
import java.util.List;

public class PartnerClientInfo {

	private String userid;
	private String fullname;
	private String clientid;
	private String refid;

	private String name;
	private String email;
	private String mobilenumber;
	private String content;
	private String subscriptionType;
	private Double subscriptionAmount;
	private String status = "Pending";

	private String partnername;
	private String partneremail;
	private String partnermobileno;
	// clienttype ca,suvidha,asp...
	private String clienttype;

	private Date joinDate;

	private String isLead;
	private Double estimatedCost;
	private String state;
	private String city;
	private String industryType;
	private Boolean needFollowup;
	private String needFollowupdate;
	private List<String> productType;

	private List<SubscriptionDetails> subscription;

	String mthCd;
	String weekCd;
	String yrCd;
	String dayCd;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getRefid() {
		return refid;
	}

	public void setRefid(String refid) {
		this.refid = refid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobilenumber() {
		return mobilenumber;
	}

	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(String subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public Double getSubscriptionAmount() {
		return subscriptionAmount;
	}

	public void setSubscriptionAmount(Double subscriptionAmount) {
		this.subscriptionAmount = subscriptionAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPartnername() {
		return partnername;
	}

	public void setPartnername(String partnername) {
		this.partnername = partnername;
	}

	public String getPartneremail() {
		return partneremail;
	}

	public void setPartneremail(String partneremail) {
		this.partneremail = partneremail;
	}

	public String getPartnermobileno() {
		return partnermobileno;
	}

	public void setPartnermobileno(String partnermobileno) {
		this.partnermobileno = partnermobileno;
	}

	public String getClienttype() {
		return clienttype;
	}

	public void setClienttype(String clienttype) {
		this.clienttype = clienttype;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public String getIsLead() {
		return isLead;
	}

	public void setIsLead(String isLead) {
		this.isLead = isLead;
	}

	public Double getEstimatedCost() {
		return estimatedCost;
	}

	public void setEstimatedCost(Double estimatedCost) {
		this.estimatedCost = estimatedCost;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getIndustryType() {
		return industryType;
	}

	public void setIndustryType(String industryType) {
		this.industryType = industryType;
	}

	public Boolean getNeedFollowup() {
		return needFollowup;
	}

	public void setNeedFollowup(Boolean needFollowup) {
		this.needFollowup = needFollowup;
	}

	public String getNeedFollowupdate() {
		return needFollowupdate;
	}

	public void setNeedFollowupdate(String needFollowupdate) {
		this.needFollowupdate = needFollowupdate;
	}

	public List<String> getProductType() {
		return productType;
	}

	public void setProductType(List<String> productType) {
		this.productType = productType;
	}

	public List<SubscriptionDetails> getSubscription() {
		return subscription;
	}

	public void setSubscription(List<SubscriptionDetails> subscription) {
		this.subscription = subscription;
	}

	public String getMthCd() {
		return mthCd;
	}

	public void setMthCd(String mthCd) {
		this.mthCd = mthCd;
	}

	public String getWeekCd() {
		return weekCd;
	}

	public void setWeekCd(String weekCd) {
		this.weekCd = weekCd;
	}

	public String getYrCd() {
		return yrCd;
	}

	public void setYrCd(String yrCd) {
		this.yrCd = yrCd;
	}

	public String getDayCd() {
		return dayCd;
	}

	public void setDayCd(String dayCd) {
		this.dayCd = dayCd;
	}
}
