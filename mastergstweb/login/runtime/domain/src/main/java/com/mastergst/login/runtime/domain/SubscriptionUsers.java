package com.mastergst.login.runtime.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;

public class SubscriptionUsers {
	
	private ObjectId id;
	private String docId;
	private ObjectId userDocid;
	private String userid;
	private String planid;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date registeredDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date expiryDate;
	private Double paidAmount;
	private String apiType;
	
	private User user;

	public ObjectId getUserDocid() {
		return userDocid;
	}

	public void setUserDocid(ObjectId userDocid) {
		this.userDocid = userDocid;
	}

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

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
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

	public void setId(ObjectId id) {
		this.id = id;
	}
}
