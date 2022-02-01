/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * GSTR Ledger ITC information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRLedgerITCDetails {

	@Id
	private ObjectId id;
	
	private String gstin;
	@JsonProperty("fr_dt")
	private String frDt;
	@JsonProperty("to_dt")
	private String toDt;
	@JsonProperty("op_bal")
	private GSTRLedgerITCBalanceDetails openBal;
	@JsonProperty("cl_bal")
	private GSTRLedgerITCBalanceDetails closeBal;
	private List<GSTRLedgerITCBalanceDetails> tr=Lists.newArrayList();
	
	public GSTRLedgerITCDetails() {
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

	public GSTRLedgerITCBalanceDetails getOpenBal() {
		return openBal;
	}

	public void setOpenBal(GSTRLedgerITCBalanceDetails openBal) {
		this.openBal = openBal;
	}

	public GSTRLedgerITCBalanceDetails getCloseBal() {
		return closeBal;
	}

	public void setCloseBal(GSTRLedgerITCBalanceDetails closeBal) {
		this.closeBal = closeBal;
	}

	public List<GSTRLedgerITCBalanceDetails> getTr() {
		return tr;
	}

	public void setTr(List<GSTRLedgerITCBalanceDetails> tr) {
		this.tr = tr;
	}

}
