/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Suvidha Centers information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "company_centers")
public class CompanyCenters extends Base {
	
	private String userid;
	private String fullname;
	private String clientid;
	
	private String name;
	private String contactperson;
	private String email;
	private String mobilenumber;
	private String state;
	private String password;
	private String disable;
	private String address;
	
	private String totalClients;
	private String totalInvoices;
	private String usedClients;
	private String usedInvoices;
	
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDisable() {
		return disable;
	}
	public void setDisable(String disable) {
		this.disable = disable;
	}
	public String getTotalClients() {
		return totalClients;
	}
	
	public void setTotalClients(String totalClients) {
		this.totalClients = totalClients;
	}
	
	public String getTotalInvoices() {
		return totalInvoices;
	}
	
	public void setTotalInvoices(String totalInvoices) {
		this.totalInvoices = totalInvoices;
	}
	public String getUsedClients() {
		return usedClients;
	}
	public void setUsedClients(String usedClients) {
		this.usedClients = usedClients;
	}
	public String getUsedInvoices() {
		return usedInvoices;
	}
	public void setUsedInvoices(String usedInvoices) {
		this.usedInvoices = usedInvoices;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
