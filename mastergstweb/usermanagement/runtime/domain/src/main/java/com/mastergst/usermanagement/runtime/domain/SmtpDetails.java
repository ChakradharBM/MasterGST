package com.mastergst.usermanagement.runtime.domain;

import javax.persistence.Transient;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "smtpdetails")
public class SmtpDetails extends Base{
	
	private String userId;
	private String clientId;
	private String host;
	private String port;
	private String auth;
	private String from;
	private String username;
	private String password;
	private String toAddress;
	private String ccAddress;
	private String schedlueExpressionVal;
	@Transient
	private String bussinessName;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	public String getCcAddress() {
		return ccAddress;
	}
	public void setCcAddress(String ccAddress) {
		this.ccAddress = ccAddress;
	}
	public String getBussinessName() {
		return bussinessName;
	}
	public void setBussinessName(String bussinessName) {
		this.bussinessName = bussinessName;
	}
	@Transient
	public String getDocId(){
		return this.id == null ? null : this.id.toString();
	}
	@Transient
	public void setDocId(String docId){
		if(docId != null && !"".equals(docId.trim())){
			this.id  = new ObjectId(docId);
		}
	}
	public String getSchedlueExpressionVal() {
		return schedlueExpressionVal;
	}
	public void setSchedlueExpressionVal(String schedlueExpressionVal) {
		this.schedlueExpressionVal = schedlueExpressionVal;
	}
}
