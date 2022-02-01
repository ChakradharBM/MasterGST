package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;

public class ApiExceedsSubscription {

	private ObjectId id;
	private String userid;
	private String docId;
	private String fullname;
	private String email;
	private String mobilenumber;
	private SubscriptionDetails user;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
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

	public SubscriptionDetails getUser() {
		return user;
	}

	public void setUser(SubscriptionDetails user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "ApiExceedsSubscription [id=" + id + ", userid=" + userid + ", docId=" + docId + ", fullname=" + fullname
				+ ", email=" + email + ", mobilenumber=" + mobilenumber + ", user=" + user + "]";
	}
}
