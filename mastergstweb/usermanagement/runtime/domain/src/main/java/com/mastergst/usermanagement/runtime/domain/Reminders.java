package com.mastergst.usermanagement.runtime.domain;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "Reminders")
public class Reminders extends Base implements Serializable , Comparable<Reminders>{
	private static final long serialVersionUID = 1L;
	private String userid;
	private String clientid;
	private String clientName;
	private List<String> mobileNumber;
	private List<String> email;
	private String subject;
    private String message;
	private String userDetails;
	private List<String> cc;
	
	
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
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public List<String> getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(List<String> mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public List<String> getEmail() {
		return email;
	}
	public void setEmail(List<String> email) {
		this.email = email;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getUserDetails() {
		return userDetails;
	}
	public List<String> getCc() {
		return cc;
	}
	public void setCc(List<String> cc) {
		this.cc = cc;
	}
	public void setUserDetails(String userDetails) {
		this.userDetails = userDetails;
	}
	

	
   @Override
public int compareTo(Reminders rem) {
	
	   return this.getCreatedDate().compareTo(rem.getCreatedDate());
}
	

}
