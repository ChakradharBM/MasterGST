/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * User information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "companyuser")
public class CompanyUser extends Base {
	
	private String userid;
	private String fullname;
	private String clientid;
	
	private String teamuserid;
	private String name;
	private String password;
	private List<String> company;
	private List<String> centers;
	private List<String> branch;
	private List<String> vertical;
	private List<String> customer;
	private String role;
	private String group;
	private String email;
	private String numberofclients;
	private Integer addedclients;
	private String mobile;
	private String isglobal;
	private String disable;
	private String addsubuser;
	private String addclient;
	private String usrLastloggedin;

	private boolean accessAcknowledgement;

	public boolean isAccessAcknowledgement() {
		return accessAcknowledgement;
	}

	public void setAccessAcknowledgement(boolean accessAcknowledgement) {
		this.accessAcknowledgement = accessAcknowledgement;
	}

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
	public String getTeamuserid() {
		return teamuserid;
	}
	public void setTeamuserid(String teamuserid) {
		this.teamuserid = teamuserid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<String> getCompany() {
		return company;
	}
	public void setCompany(List<String> company) {
		this.company = company;
	}
	public List<String> getBranch() {
		return branch;
	}
	public void setBranch(List<String> branch) {
		this.branch = branch;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNumberofclients() {
		return numberofclients;
	}
	public void setNumberofclients(String numberofclients) {
		this.numberofclients = numberofclients;
	}
	public Integer getAddedclients() {
		return addedclients;
	}
	public void setAddedclients(Integer addedclients) {
		this.addedclients = addedclients;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getIsglobal() {
		return isglobal;
	}
	public void setIsglobal(String isglobal) {
		this.isglobal = isglobal;
	}
	public String getDisable() {
		return disable;
	}
	public void setDisable(String disable) {
		this.disable = disable;
	}
	public List<String> getCenters() {
		return centers;
	}
	public void setCenters(List<String> centers) {
		this.centers = centers;
	}
	public List<String> getVertical() {
		return vertical;
	}
	public void setVertical(List<String> vertical) {
		this.vertical = vertical;
	}
	 public String getAddsubuser() {
		 return addsubuser; 
	} 
	 public void setAddsubuser(String addsubuser) {
		 this.addsubuser = addsubuser; 
	}

	public List<String> getCustomer() {
		return customer;
	}

	public void setCustomer(List<String> customer) {
		this.customer = customer;
	}

	public String getUsrLastloggedin() {
		return usrLastloggedin;
	}

	public void setUsrLastloggedin(String usrLastloggedin) {
		this.usrLastloggedin = usrLastloggedin;
	}

	public String getAddclient() {
		return addclient;
	}

	public void setAddclient(String addclient) {
		this.addclient = addclient;
	}
	
}
