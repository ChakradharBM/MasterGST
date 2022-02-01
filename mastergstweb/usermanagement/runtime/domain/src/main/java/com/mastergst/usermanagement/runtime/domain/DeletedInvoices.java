package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

public class DeletedInvoices {

	private List<String> invnos;
	private String userid;
	private String clientid;

	public List<String> getInvnos() {
		return invnos;
	}

	public void setInvnos(List<String> invnos) {
		this.invnos = invnos;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	@Override
	public String toString() {
		return "DeletedInvoices [invnos=" + invnos + ", userid=" + userid + ", clientid=" + clientid + "]";
	}
}
