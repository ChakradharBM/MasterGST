/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR4 Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayeeDetails {
	@JsonProperty("Nm")
	private String nm;
	@JsonProperty("AccDet")
	private String accDet;
	@JsonProperty("Mode")
	private String mode;
	
	@JsonProperty("FinInsBr")
	private String finInsBr;
	@JsonProperty("PayTerm")
	private String payTerm;
	@JsonProperty("PayInstr")
	private String payInstr;
	
	@JsonProperty("CrTrn")
	private String crTrn;
	@JsonProperty("DirDr")
	private String dirDr;
	@JsonProperty("CrDay")
	private String crDay;
	
	@JsonProperty("PaidAmt")
	private Number paidAmt;
	@JsonProperty("PaymtDue")
	private Number paymtDue;
	public String getNm() {
		return nm;
	}
	public void setNm(String nm) {
		this.nm = nm;
	}
	public String getAccDet() {
		return accDet;
	}
	public void setAccDet(String accDet) {
		this.accDet = accDet;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getFinInsBr() {
		return finInsBr;
	}
	public void setFinInsBr(String finInsBr) {
		this.finInsBr = finInsBr;
	}
	public String getPayTerm() {
		return payTerm;
	}
	public void setPayTerm(String payTerm) {
		this.payTerm = payTerm;
	}
	public String getPayInstr() {
		return payInstr;
	}
	public void setPayInstr(String payInstr) {
		this.payInstr = payInstr;
	}
	public String getCrTrn() {
		return crTrn;
	}
	public void setCrTrn(String crTrn) {
		this.crTrn = crTrn;
	}
	public String getDirDr() {
		return dirDr;
	}
	public void setDirDr(String dirDr) {
		this.dirDr = dirDr;
	}
	public String getCrDay() {
		return crDay;
	}
	public void setCrDay(String crDay) {
		this.crDay = crDay;
	}
	public Number getPaidAmt() {
		return paidAmt;
	}
	public void setPaidAmt(Number paidAmt) {
		this.paidAmt = paidAmt;
	}
	public Number getPaymtDue() {
		return paymtDue;
	}
	public void setPaymtDue(Number paymtDue) {
		this.paymtDue = paymtDue;
	}
	
}
