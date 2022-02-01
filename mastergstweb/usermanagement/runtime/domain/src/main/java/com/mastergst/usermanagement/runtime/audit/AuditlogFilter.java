package com.mastergst.usermanagement.runtime.audit;

public class AuditlogFilter {
	
	
	private String[] clientname;
	private String[] username;
	private String[] action;
	
	public String[] getClientname() {
		return clientname;
	}
	public void setClientname(String[] clientname) {
		this.clientname = clientname;
	}
	public void setClientname(String clientname) {
		this.clientname = clientname == null ? null : clientname.split(",");
	}
	public String[] getUsername() {
		return username;
	}
	public void setUsername(String[] username) {
		this.username = username;
	}
	public void setUsername(String username) {
		this.username = username == null ? null : username.split(",");
	}
	public String[] getAction() {
		return action;
	}
	public void setAction(String[] action) {
		this.action = action;
	}
	public void setAction(String action) {
		this.action = action == null ? null : action.split(",");
	}
}
