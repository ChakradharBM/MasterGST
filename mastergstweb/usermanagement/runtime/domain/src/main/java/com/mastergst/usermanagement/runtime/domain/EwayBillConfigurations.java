package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "ewaybill_config")
public class EwayBillConfigurations extends Base {
	
	private String clientid;
	private String userName;
	private String password;
	private String connStaus;
	
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConnStaus() {
		return connStaus;
	}
	public void setConnStaus(String connStaus) {
		this.connStaus = connStaus;
	}
	
	
}