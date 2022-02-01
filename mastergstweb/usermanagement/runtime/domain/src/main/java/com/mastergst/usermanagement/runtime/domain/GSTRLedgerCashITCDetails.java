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
 * GSTR Ledger Cash information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRLedgerCashITCDetails extends LedgerParent {

	@Id
	private ObjectId id;
	
	private String gstin;
	@JsonProperty("ret_period")
	private String retPeriod;
	@JsonProperty("cash_bal")
	private GSTRLedgerBalanceSummary cashBal;
	@JsonProperty("itc_bal")
	private GSTRLedgerGSTBalanceDetails itcBal;
	
	public GSTRLedgerCashITCDetails() {
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

	public GSTRLedgerBalanceSummary getCashBal() {
		return cashBal;
	}

	public void setCashBal(GSTRLedgerBalanceSummary cashBal) {
		this.cashBal = cashBal;
	}

	public GSTRLedgerGSTBalanceDetails getItcBal() {
		return itcBal;
	}

	public void setItcBal(GSTRLedgerGSTBalanceDetails itcBal) {
		this.itcBal = itcBal;
	}

}
