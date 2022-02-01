/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR Ledger Provisional information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRLedgerProvisionalDetails {

	@Id
	private ObjectId id;

	private Double igstProCrBal;
	private Double cgstProCrBal;
	private Double sgstProCrBal;
	private Double cessProCrBal;
	private Double totProCrBal;
	@JsonProperty("ret_period")
	private String retPeriod;
	
	public GSTRLedgerProvisionalDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getIgstProCrBal() {
		return igstProCrBal;
	}

	public void setIgstProCrBal(Double igstProCrBal) {
		this.igstProCrBal = igstProCrBal;
	}

	public Double getCgstProCrBal() {
		return cgstProCrBal;
	}

	public void setCgstProCrBal(Double cgstProCrBal) {
		this.cgstProCrBal = cgstProCrBal;
	}

	public Double getSgstProCrBal() {
		return sgstProCrBal;
	}

	public void setSgstProCrBal(Double sgstProCrBal) {
		this.sgstProCrBal = sgstProCrBal;
	}

	public Double getCessProCrBal() {
		return cessProCrBal;
	}

	public void setCessProCrBal(Double cessProCrBal) {
		this.cessProCrBal = cessProCrBal;
	}

	public Double getTotProCrBal() {
		return totProCrBal;
	}

	public void setTotProCrBal(Double totProCrBal) {
		this.totProCrBal = totProCrBal;
	}

	public String getRetPeriod() {
		return retPeriod;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

}
