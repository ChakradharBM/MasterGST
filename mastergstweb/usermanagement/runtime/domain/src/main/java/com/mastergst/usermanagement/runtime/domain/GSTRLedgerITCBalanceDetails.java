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
 * GSTR Ledger Transaction & Open/Close Balance information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRLedgerITCBalanceDetails {

	@Id
	private ObjectId id;

	private String dt;
	private String desc;
	@JsonProperty("ref_no")
	private String refno;
	private Double igstTaxBal;
	private Double cgstTaxBal;
	private Double sgstTaxBal;
	private Double cessTaxBal;
	private Double igstTaxAmt;
	private Double cgstTaxAmt;
	private Double sgstTaxAmt;
	private Double cessTaxAmt;
	@JsonProperty("tot_rng_bal")
	private Double totRngBal;
	@JsonProperty("tr_typ")
	private String trTyp;
	@JsonProperty("tot_tr_amt")
	private Double totTrAmt;	
	@JsonProperty("ret_period")
	private String retPeriod;
	
	public GSTRLedgerITCBalanceDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getDt() {
		return dt;
	}

	public void setDt(String dt) {
		this.dt = dt;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getRefno() {
		return refno;
	}

	public void setRefno(String refno) {
		this.refno = refno;
	}

	public Double getIgstTaxBal() {
		return igstTaxBal;
	}

	public void setIgstTaxBal(Double igstTaxBal) {
		this.igstTaxBal = igstTaxBal;
	}

	public Double getCgstTaxBal() {
		return cgstTaxBal;
	}

	public void setCgstTaxBal(Double cgstTaxBal) {
		this.cgstTaxBal = cgstTaxBal;
	}

	public Double getSgstTaxBal() {
		return sgstTaxBal;
	}

	public void setSgstTaxBal(Double sgstTaxBal) {
		this.sgstTaxBal = sgstTaxBal;
	}

	public Double getCessTaxBal() {
		return cessTaxBal;
	}

	public void setCessTaxBal(Double cessTaxBal) {
		this.cessTaxBal = cessTaxBal;
	}

	public Double getIgstTaxAmt() {
		return igstTaxAmt;
	}

	public void setIgstTaxAmt(Double igstTaxAmt) {
		this.igstTaxAmt = igstTaxAmt;
	}

	public Double getCgstTaxAmt() {
		return cgstTaxAmt;
	}

	public void setCgstTaxAmt(Double cgstTaxAmt) {
		this.cgstTaxAmt = cgstTaxAmt;
	}

	public Double getSgstTaxAmt() {
		return sgstTaxAmt;
	}

	public void setSgstTaxAmt(Double sgstTaxAmt) {
		this.sgstTaxAmt = sgstTaxAmt;
	}

	public Double getCessTaxAmt() {
		return cessTaxAmt;
	}

	public void setCessTaxAmt(Double cessTaxAmt) {
		this.cessTaxAmt = cessTaxAmt;
	}

	public Double getTotRngBal() {
		return totRngBal;
	}

	public void setTotRngBal(Double totRngBal) {
		this.totRngBal = totRngBal;
	}

	public String getTrTyp() {
		return trTyp;
	}

	public void setTrTyp(String trTyp) {
		this.trTyp = trTyp;
	}

	public Double getTotTrAmt() {
		return totTrAmt;
	}

	public void setTotTrAmt(Double totTrAmt) {
		this.totTrAmt = totTrAmt;
	}

	public String getRetPeriod() {
		return retPeriod;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

}
