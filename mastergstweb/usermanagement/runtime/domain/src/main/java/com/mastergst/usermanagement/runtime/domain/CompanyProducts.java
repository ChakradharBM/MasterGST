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
@Document(collection = "companyproducts")
public class CompanyProducts extends Base {
	
	private String userid;
	private String fullname;
	private String clientid;
	private Long serialno;
	private String name;
	private String description;
	private String hsn;
	private String unit;
	private String cost;
	private Double taxrate;
	
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
	public Long getSerialno() {
		return serialno;
	}
	public void setSerialno(Long serialno) {
		this.serialno = serialno;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getHsn() {
		return hsn;
	}
	public void setHsn(String hsn) {
		this.hsn = hsn;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
	
	@Override
	public String toString() {
		return "CompanyProducts [userid=" + userid + ", fullname=" + fullname + ", clientid=" + clientid + ", serialno="
				+ serialno + ", name=" + name + ", description=" + description + ", hsn=" + hsn + "]";
	}
	public Double getTaxrate() {
		return taxrate;
	}
	public void setTaxrate(Double taxrate) {
		this.taxrate = taxrate;
	}
	
}
