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
public class GSTRLedgerCashDetails extends LedgerParent {

	@Id
	private ObjectId id;
	
	private String gstin;
	@JsonProperty("fr_dt")
	private String frDt;
	@JsonProperty("to_dt")
	private String toDt;
	@JsonProperty("op_bal")
	private GSTRLedgerBalanceSummary openBal;
	@JsonProperty("cl_bal")
	private GSTRLedgerBalanceSummary closeBal;
	
	public GSTRLedgerCashDetails() {
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

	public String getFrDt() {
		return frDt;
	}

	public void setFrDt(String frDt) {
		this.frDt = frDt;
	}

	public String getToDt() {
		return toDt;
	}

	public void setToDt(String toDt) {
		this.toDt = toDt;
	}

	public GSTRLedgerBalanceSummary getOpenBal() {
		return openBal;
	}

	public void setOpenBal(GSTRLedgerBalanceSummary openBal) {
		this.openBal = openBal;
	}

	public GSTRLedgerBalanceSummary getCloseBal() {
		return closeBal;
	}

	public void setCloseBal(GSTRLedgerBalanceSummary closeBal) {
		this.closeBal = closeBal;
	}

}
