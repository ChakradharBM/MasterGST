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
 * GSTR Liability Ledger Details For Return Liability information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRLiabilityLedgerDetailsForReturnLiability extends LedgerParent {
	
	@Id
	private ObjectId id;
	
	private String gstin;
	@JsonProperty("ret_period")
	private String retPeriod;
	
	@JsonProperty("cl_bal")
	private GSTRLedgerBalanceSummary clBal;

	
	public GSTRLiabilityLedgerDetailsForReturnLiability() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getRetPeriod() {
		return retPeriod;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}
	
	public GSTRLedgerBalanceSummary getClBal() {
		return clBal;
	}
	
	public void setClBal(GSTRLedgerBalanceSummary clBal) {
		this.clBal = clBal;
	}

}
