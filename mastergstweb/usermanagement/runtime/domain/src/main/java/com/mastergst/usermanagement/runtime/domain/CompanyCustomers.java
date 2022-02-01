/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Customer information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "companycustomers")
public class CompanyCustomers extends Base {
	
	private String userid;
	private String fullname;
	private String clientid;
	private String name;
	private String type;
	private String gstnnumber;
	private String contactperson;
	private String email;
	private String mobilenumber;
	private String state;
	private String address;
	private String pincode;
	private String city;
	private String landline;
	private String country;
	private String customerId;
	private String customerTanPanNumber;
	private String customerPanNumber;
	private String customerTanNumber;
	private String customerBankName;
	private String customerAccountNumber;
	private String customerBranchName;
	private String customerAccountName;
	private String customerBankIfscCode;
	private String customerLedgerName;
	private String customerterms;
	private Boolean isCustomerTermsDetails;
	private String creditPeriod;
	private Double creditAmount;
	private Double openingbalance;
	private String customerIdAndName;
	private String dealerType;
	private String docId;
	
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getGstnnumber() {
		return gstnnumber;
	}
	public void setGstnnumber(String gstnnumber) {
		this.gstnnumber = gstnnumber;
	}
	public String getContactperson() {
		return contactperson;
	}
	public void setContactperson(String contactperson) {
		this.contactperson = contactperson;
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
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getLandline() {
		return landline;
	}
	public void setLandline(String landline) {
		this.landline = landline;
	}
	public String getCustomerBankName() {
		return customerBankName;
	}
	public void setCustomerBankName(String customerBankName) {
		this.customerBankName = customerBankName;
	}
	public String getCustomerAccountNumber() {
		return customerAccountNumber;
	}
	public void setCustomerAccountNumber(String customerAccountNumber) {
		this.customerAccountNumber = customerAccountNumber;
	}
	public String getCustomerBranchName() {
		return customerBranchName;
	}
	public void setCustomerBranchName(String customerBranchName) {
		this.customerBranchName = customerBranchName;
	}
	public String getCustomerAccountName() {
		return customerAccountName;
	}
	public void setCustomerAccountName(String customerAccountName) {
		this.customerAccountName = customerAccountName;
	}
	public String getCustomerBankIfscCode() {
		return customerBankIfscCode;
	}
	public void setCustomerBankIfscCode(String customerBankIfscCode) {
		this.customerBankIfscCode = customerBankIfscCode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerTanPanNumber() {
		return customerTanPanNumber;
	}

	public void setCustomerTanPanNumber(String customerTanPanNumber) {
		this.customerTanPanNumber = customerTanPanNumber;
	}

	public String getCustomerPanNumber() {
		return customerPanNumber;
	}

	public void setCustomerPanNumber(String customerPanNumber) {
		this.customerPanNumber = customerPanNumber;
	}

	public String getCustomerTanNumber() {
		return customerTanNumber;
	}

	public void setCustomerTanNumber(String customerTanNumber) {
		this.customerTanNumber = customerTanNumber;
	}

	public String getCustomerIdAndName() {
		return customerIdAndName;
	}

	public void setCustomerIdAndName(String customerIdAndName) {
		this.customerIdAndName = customerIdAndName;
	}
	public String getCustomerLedgerName() {
		return customerLedgerName;
	}
	public void setCustomerLedgerName(String customerLedgerName) {
		this.customerLedgerName = customerLedgerName;
	}
	public String getDealerType() {
		return dealerType;
	}
	public void setDealerType(String dealerType) {
		this.dealerType = dealerType;
	}
	public String getCreditPeriod() {
		return creditPeriod;
	}
	public void setCreditPeriod(String creditPeriod) {
		this.creditPeriod = creditPeriod;
	}
	public Double getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(Double creditAmount) {
		this.creditAmount = creditAmount;
	}
	public Double getOpeningbalance() {
		return openingbalance;
	}
	public void setOpeningbalance(Double openingbalance) {
		this.openingbalance = openingbalance;
	}
	public String getCustomerterms() {
		return customerterms;
	}
	public void setCustomerterms(String customerterms) {
		this.customerterms = customerterms;
	}
	public Boolean getIsCustomerTermsDetails() {
		return isCustomerTermsDetails;
	}
	public void setIsCustomerTermsDetails(Boolean isCustomerTermsDetails) {
		this.isCustomerTermsDetails = isCustomerTermsDetails;
	}
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	

}
