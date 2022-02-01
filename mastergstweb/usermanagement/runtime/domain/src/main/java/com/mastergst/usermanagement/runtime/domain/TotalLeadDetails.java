package com.mastergst.usermanagement.runtime.domain;

import java.math.BigDecimal;

public class TotalLeadDetails {
	private String _id;
	 private int totalLeads;
	 private int totalNew;
	 private int totalPending;
	 private int totalJoined;
	 private int totalDemo;
	 private BigDecimal estimatedCost = new BigDecimal(0.0);
	 private BigDecimal subscriptionAmount = new BigDecimal(0.0);
	 
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public int getTotalLeads() {
		return totalLeads;
	}
	public void setTotalLeads(int totalLeads) {
		this.totalLeads = totalLeads;
	}
	public int getTotalNew() {
		return totalNew;
	}
	public void setTotalNew(int totalNew) {
		this.totalNew = totalNew;
	}
	public int getTotalPending() {
		return totalPending;
	}
	public void setTotalPending(int totalPending) {
		this.totalPending = totalPending;
	}
	public int getTotalJoined() {
		return totalJoined;
	}
	public void setTotalJoined(int totalJoined) {
		this.totalJoined = totalJoined;
	}
	public BigDecimal getEstimatedCost() {
		return estimatedCost;
	}
	public void setEstimatedCost(BigDecimal estimatedCost) {
		this.estimatedCost = estimatedCost;
	}
	public BigDecimal getSubscriptionAmount() {
		return subscriptionAmount;
	}
	public void setSubscriptionAmount(BigDecimal subscriptionAmount) {
		this.subscriptionAmount = subscriptionAmount;
	}
	public int getTotalDemo() {
		return totalDemo;
	}
	public void setTotalDemo(int totalDemo) {
		this.totalDemo = totalDemo;
	}
	
	 
}
