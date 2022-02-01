/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Suppliers information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "company_suppliers")
public class CompanySuppliers extends Base {
	
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
	private String country;
	private String address;
	private String pincode;
	private String city;
	private String landline;
	private String faxNo;
	private String fulltimeEmployees;
	private String companyRegNo;
	private String dateofInception;
	private String natuteOfExpertise;
	private String turnover;
	private String programsAndCert;
	private String applicability;	
	private String beneficiaryName;
	private String bankName;
	private String branchAddress;
	private String accountNumber;
	private String ifscCode;
	private String accountType;
	private String micrCode;
	private String supplierCustomerId;
	private String supplierTanPanNumber;
	private String supplierPanNumber;
	private String supplierTanNumber;
	private String supplierterms;
	private Boolean isSupplierTermsDetails;
	private String customerIdAndName;
	private String supplierLedgerName;
	private String dealerType;
	private String docId;
	private Map<String,String> filingGstr1history;
	private Map<String,String> filingGstr3bhistory;

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
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	public String getSupplierCustomerId() {
		return supplierCustomerId;
	}

	public void setSupplierCustomerId(String supplierCustomerId) {
		this.supplierCustomerId = supplierCustomerId;
	}

	public String getSupplierTanPanNumber() {
		return supplierTanPanNumber;
	}

	public void setSupplierTanPanNumber(String supplierTanPanNumber) {
		this.supplierTanPanNumber = supplierTanPanNumber;
	}

	public String getSupplierPanNumber() {
		return supplierPanNumber;
	}

	public void setSupplierPanNumber(String supplierPanNumber) {
		this.supplierPanNumber = supplierPanNumber;
	}

	public String getSupplierTanNumber() {
		return supplierTanNumber;
	}

	public void setSupplierTanNumber(String supplierTanNumber) {
		this.supplierTanNumber = supplierTanNumber;
	}

	public String getCustomerIdAndName() {
		return customerIdAndName;
	}

	public void setCustomerIdAndName(String customerIdAndName) {
		this.customerIdAndName = customerIdAndName;
	}
	public String getSupplierLedgerName() {
		return supplierLedgerName;
	}
	public void setSupplierLedgerName(String supplierLedgerName) {
		this.supplierLedgerName = supplierLedgerName;
	}
	public String getDealerType() {
		return dealerType;
	}
	public void setDealerType(String dealerType) {
		this.dealerType = dealerType;
	}
	public String getSupplierterms() {
		return supplierterms;
	}
	public void setSupplierterms(String supplierterms) {
		this.supplierterms = supplierterms;
	}
	public Boolean getIsSupplierTermsDetails() {
		return isSupplierTermsDetails;
	}
	public void setIsSupplierTermsDetails(Boolean isSupplierTermsDetails) {
		this.isSupplierTermsDetails = isSupplierTermsDetails;
	}
	
	public String getCompanyRegNo() {
		return companyRegNo;
	}
	public void setCompanyRegNo(String companyRegNo) {
		this.companyRegNo = companyRegNo;
	}
	public String getDateofInception() {
		return dateofInception;
	}
	public void setDateofInception(String dateofInception) {
		this.dateofInception = dateofInception;
	}
	public String getNatuteOfExpertise() {
		return natuteOfExpertise;
	}
	public void setNatuteOfExpertise(String natuteOfExpertise) {
		this.natuteOfExpertise = natuteOfExpertise;
	}
	public String getTurnover() {
		return turnover;
	}
	public void setTurnover(String turnover) {
		this.turnover = turnover;
	}
	public String getProgramsAndCert() {
		return programsAndCert;
	}
	public void setProgramsAndCert(String programsAndCert) {
		this.programsAndCert = programsAndCert;
	}
	public String getApplicability() {
		return applicability;
	}
	public void setApplicability(String applicability) {
		this.applicability = applicability;
	}
	public String getFaxNo() {
		return faxNo;
	}
	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}
	public String getFulltimeEmployees() {
		return fulltimeEmployees;
	}
	public void setFulltimeEmployees(String fulltimeEmployees) {
		this.fulltimeEmployees = fulltimeEmployees;
	}
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBranchAddress() {
		return branchAddress;
	}
	public void setBranchAddress(String branchAddress) {
		this.branchAddress = branchAddress;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getIfscCode() {
		return ifscCode;
	}
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getMicrCode() {
		return micrCode;
	}
	public void setMicrCode(String micrCode) {
		this.micrCode = micrCode;
	}
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public Map<String, String> getFilingGstr1history() {
		return filingGstr1history;
	}
	public void setFilingGstr1history(Map<String, String> filingGstr1history) {
		this.filingGstr1history = filingGstr1history;
	}
	public Map<String, String> getFilingGstr3bhistory() {
		return filingGstr3bhistory;
	}
	public void setFilingGstr3bhistory(Map<String, String> filingGstr3bhistory) {
		this.filingGstr3bhistory = filingGstr3bhistory;
	}
	
	
	
}
