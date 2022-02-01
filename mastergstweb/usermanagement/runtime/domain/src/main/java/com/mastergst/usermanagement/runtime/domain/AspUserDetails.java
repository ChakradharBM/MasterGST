/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "apiuserdetails")
public class AspUserDetails extends Base {

	private String userid;
	private String firstName;
	private String lastName;
	private String email;
	private String mobileNumber;
	private String companyRegisterName;
	private String companyAddress;
	private String addressProof;
	private String apitype;

	public AspUserDetails() {
		super();
	}

	public AspUserDetails(String userid) {
		super();
		this.userid = userid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getCompanyRegisterName() {
		return companyRegisterName;
	}

	public void setCompanyRegisterName(String companyRegisterName) {
		this.companyRegisterName = companyRegisterName;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getAddressProof() {
		return addressProof;
	}

	public void setAddressProof(String addressProof) {
		this.addressProof = addressProof;
	}

	public String getApitype() {
		return apitype;
	}

	public void setApitype(String apitype) {
		this.apitype = apitype;
	}
}
