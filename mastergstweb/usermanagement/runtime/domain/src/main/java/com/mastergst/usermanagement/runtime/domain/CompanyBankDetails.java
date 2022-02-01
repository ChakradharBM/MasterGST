/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Product information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "companybankdetails")
public class CompanyBankDetails extends Base {
	
	private String userid;
	private String fullname;
	private String clientid;
	private String bankname;
	private String accountnumber;
	private String branchname;
	private String ifsccode;
	private String accountName;
	private String modeOfPayment;
	private String creditTransfer;
	private String creditDays;
	private String directDebit;
	private String balAmtToBePaid;
	private String dueDate;
	private String qrcodeid;
	
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
	public String getBankname() {
		return bankname;
	}
	public void setBankname(String bankname) {
		this.bankname = bankname;
	}
	public String getAccountnumber() {
		return accountnumber;
	}
	public void setAccountnumber(String accountnumber) {
		this.accountnumber = accountnumber;
	}
	public String getBranchname() {
		return branchname;
	}
	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}
	public String getIfsccode() {
		return ifsccode;
	}
	public void setIfsccode(String ifsccode) {
		this.ifsccode = ifsccode;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getModeOfPayment() {
		return modeOfPayment;
	}
	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}
	public String getCreditTransfer() {
		return creditTransfer;
	}
	public void setCreditTransfer(String creditTransfer) {
		this.creditTransfer = creditTransfer;
	}
	public String getCreditDays() {
		return creditDays;
	}
	public void setCreditDays(String creditDays) {
		this.creditDays = creditDays;
	}
	public String getDirectDebit() {
		return directDebit;
	}
	public void setDirectDebit(String directDebit) {
		this.directDebit = directDebit;
	}
	public String getBalAmtToBePaid() {
		return balAmtToBePaid;
	}
	public void setBalAmtToBePaid(String balAmtToBePaid) {
		this.balAmtToBePaid = balAmtToBePaid;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public String getQrcodeid() {
		return qrcodeid;
	}
	public void setQrcodeid(String qrcodeid) {
		this.qrcodeid = qrcodeid;
	}
	
	
}
