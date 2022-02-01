package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

public class UpdateClients {
	private List<String> clientids;
	private String userid;
	private String salescutOffdate;
	private String purchasecutOffdate;
	
	
	public List<String> getClientids() {
		return clientids;
	}
	public void setClientids(List<String> clientids) {
		this.clientids = clientids;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getSalescutOffdate() {
		return salescutOffdate;
	}
	public void setSalescutOffdate(String salescutOffdate) {
		this.salescutOffdate = salescutOffdate;
	}
	public String getPurchasecutOffdate() {
		return purchasecutOffdate;
	}
	public void setPurchasecutOffdate(String purchasecutOffdate) {
		this.purchasecutOffdate = purchasecutOffdate;
	}
	
	
}
