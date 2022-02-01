/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR1 B2B invoices information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRB2B {

	@Id
	private ObjectId id;
	
	private String ctin;
	private String cfs;
	private String cfs3b;
	private String fldtr1;
	private String flprdr1;
	private String dtcancel;
	@JsonProperty("error_msg")
	private String errorMsg;
	private List<GSTRInvoiceDetails> inv=LazyList.decorate(new ArrayList<GSTRInvoiceDetails>(), 
			FactoryUtils.instantiateFactory(GSTRInvoiceDetails.class));
	
	public GSTRB2B() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getCtin() {
		return ctin;
	}

	public void setCtin(String ctin) {
		this.ctin = ctin;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List<GSTRInvoiceDetails> getInv() {
		return inv;
	}

	public void setInv(List<GSTRInvoiceDetails> inv) {
		this.inv = inv;
	}

	public String getCfs() {
		return cfs;
	}

	public void setCfs(String cfs) {
		this.cfs = cfs;
	}

	public String getCfs3b() {
		return cfs3b;
	}

	public void setCfs3b(String cfs3b) {
		this.cfs3b = cfs3b;
	}

	public String getFldtr1() {
		return fldtr1;
	}

	public void setFldtr1(String fldtr1) {
		this.fldtr1 = fldtr1;
	}

	public String getFlprdr1() {
		return flprdr1;
	}

	public void setFlprdr1(String flprdr1) {
		this.flprdr1 = flprdr1;
	}

	public String getDtcancel() {
		return dtcancel;
	}

	public void setDtcancel(String dtcancel) {
		this.dtcancel = dtcancel;
	}
	

}
