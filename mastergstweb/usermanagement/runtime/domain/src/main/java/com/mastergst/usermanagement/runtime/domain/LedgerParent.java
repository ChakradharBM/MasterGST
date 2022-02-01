/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * Ledger common information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LedgerParent {

	private String gstin;
	@JsonProperty("fr_dt")
	private String frDt;
	@JsonProperty("to_dt")
	private String toDt;
	@JsonProperty("ret_period")
	private String retPeriod;
	@JsonProperty("ret_type")
	private String retType;
	
	@JsonProperty("op_bal")
	private GSTRLedgerBalanceSummary openBal;
	@JsonProperty("cl_bal")
	private GSTRLedgerBalanceSummary closeBal;
	private List<GSTRLedgerBalanceSummary> tr;
	
	private GSTRLedgerITCDetails itcLdgDtls;
	private GSTRLedgerProvCreditBalanceList provCrdBalList;
	
	@JsonProperty("cash_bal")
	private GSTRLedgerBalanceSummary cashBal;
	@JsonProperty("itc_bal")
	private GSTRLedgerGSTBalanceDetails itcBal;
	
	@JsonProperty("op_liab")
	private List<GSTRLedgerBalanceSummary> openLiab=Lists.newArrayList();

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

	public String getRetPeriod() {
		return retPeriod;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	public String getRetType() {
		return retType;
	}

	public void setRetType(String retType) {
		this.retType = retType;
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

	public List<GSTRLedgerBalanceSummary> getTr() {
		return tr;
	}

	public void setTr(List<GSTRLedgerBalanceSummary> tr) {
		this.tr = tr;
	}

	public GSTRLedgerITCDetails getItcLdgDtls() {
		return itcLdgDtls;
	}

	public void setItcLdgDtls(GSTRLedgerITCDetails itcLdgDtls) {
		this.itcLdgDtls = itcLdgDtls;
	}

	public GSTRLedgerProvCreditBalanceList getProvCrdBalList() {
		return provCrdBalList;
	}

	public void setProvCrdBalList(GSTRLedgerProvCreditBalanceList provCrdBalList) {
		this.provCrdBalList = provCrdBalList;
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

	public List<GSTRLedgerBalanceSummary> getOpenLiab() {
		return openLiab;
	}

	public void setOpenLiab(List<GSTRLedgerBalanceSummary> openLiab) {
		this.openLiab = openLiab;
	}
	
}
