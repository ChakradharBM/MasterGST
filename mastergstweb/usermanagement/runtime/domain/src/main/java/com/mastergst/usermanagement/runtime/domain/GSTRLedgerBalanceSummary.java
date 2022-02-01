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
 * GSTR Ledger Balance Summary information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRLedgerBalanceSummary {

	@Id
	private ObjectId id;

	@JsonProperty("dpt_dt")
	private String dptDt;
	private String desc;
	private String refNo;
	@JsonProperty("tot_rng_bal")
	private Double totRngBal;
	@JsonProperty("tr_typ")
	private String trTyp;
	private GSTRLedgerBalanceDetails igst;
	private GSTRLedgerBalanceDetails cgst;
	private GSTRLedgerBalanceDetails sgst;
	private GSTRLedgerBalanceDetails cess;
	@JsonProperty("rpt_dt")
	private String rptDt;
	@JsonProperty("dpt_time")
	private String dptTime;
	@JsonProperty("tot_tr_amt")
	private Double totTrAmt;	
	@JsonProperty("ret_period")
	private String retPeriod;	
	private GSTRLedgerBalanceDetails igstbal;
	private GSTRLedgerBalanceDetails cgstbal;
	private GSTRLedgerBalanceDetails sgstbal;
	private GSTRLedgerBalanceDetails cessbal;
	@JsonProperty("igst_tot_bal")
	private Double igstTotBal;
	@JsonProperty("cgst_tot_bal")
	private Double cgstTotBal;
	@JsonProperty("sgst_tot_bal")
	private Double sgstTotBal;
	@JsonProperty("cess_tot_bal")
	private Double cessTotBal;
	private String dt;
	@JsonProperty("tot_tr_bal")
	private Double totTrBal;
	@JsonProperty("dschrg_typ")
	private String dschrgTyp;
	@JsonProperty("liab_id")
	private Integer liabId;
	@JsonProperty("tran_cd")
	private Integer tranCd;
	@JsonProperty("tran_desc")
	private String tranDesc;
	@JsonProperty("ref_no")
	private String referenceNo;
	@JsonProperty("trancd")
	private Integer trancode;
	@JsonProperty("trandate")
	private String trandate;
	@JsonProperty("debit_id")
	private String debitId;
	
	public GSTRLedgerBalanceSummary() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getDptDt() {
		return dptDt;
	}

	public void setDptDt(String dptDt) {
		this.dptDt = dptDt;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
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

	public GSTRLedgerBalanceDetails getIgst() {
		return igst;
	}

	public void setIgst(GSTRLedgerBalanceDetails igst) {
		this.igst = igst;
	}

	public GSTRLedgerBalanceDetails getCgst() {
		return cgst;
	}

	public void setCgst(GSTRLedgerBalanceDetails cgst) {
		this.cgst = cgst;
	}

	public GSTRLedgerBalanceDetails getSgst() {
		return sgst;
	}

	public void setSgst(GSTRLedgerBalanceDetails sgst) {
		this.sgst = sgst;
	}

	public GSTRLedgerBalanceDetails getCess() {
		return cess;
	}

	public void setCess(GSTRLedgerBalanceDetails cess) {
		this.cess = cess;
	}

	public String getRptDt() {
		return rptDt;
	}

	public void setRptDt(String rptDt) {
		this.rptDt = rptDt;
	}

	public String getDptTime() {
		return dptTime;
	}

	public void setDptTime(String dptTime) {
		this.dptTime = dptTime;
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

	public GSTRLedgerBalanceDetails getIgstbal() {
		return igstbal;
	}

	public void setIgstbal(GSTRLedgerBalanceDetails igstbal) {
		this.igstbal = igstbal;
	}

	public GSTRLedgerBalanceDetails getCgstbal() {
		return cgstbal;
	}

	public void setCgstbal(GSTRLedgerBalanceDetails cgstbal) {
		this.cgstbal = cgstbal;
	}

	public GSTRLedgerBalanceDetails getSgstbal() {
		return sgstbal;
	}

	public void setSgstbal(GSTRLedgerBalanceDetails sgstbal) {
		this.sgstbal = sgstbal;
	}

	public GSTRLedgerBalanceDetails getCessbal() {
		return cessbal;
	}

	public void setCessbal(GSTRLedgerBalanceDetails cessbal) {
		this.cessbal = cessbal;
	}

	public Double getIgstTotBal() {
		return igstTotBal;
	}

	public void setIgstTotBal(Double igstTotBal) {
		this.igstTotBal = igstTotBal;
	}

	public Double getCgstTotBal() {
		return cgstTotBal;
	}

	public void setCgstTotBal(Double cgstTotBal) {
		this.cgstTotBal = cgstTotBal;
	}

	public Double getSgstTotBal() {
		return sgstTotBal;
	}

	public void setSgstTotBal(Double sgstTotBal) {
		this.sgstTotBal = sgstTotBal;
	}

	public Double getCessTotBal() {
		return cessTotBal;
	}

	public void setCessTotBal(Double cessTotBal) {
		this.cessTotBal = cessTotBal;
	}

	public String getDt() {
		return dt;
	}

	public void setDt(String dt) {
		this.dt = dt;
	}

	public Double getTotTrBal() {
		return totTrBal;
	}

	public void setTotTrBal(Double totTrBal) {
		this.totTrBal = totTrBal;
	}

	public String getDschrgTyp() {
		return dschrgTyp;
	}

	public void setDschrgTyp(String dschrgTyp) {
		this.dschrgTyp = dschrgTyp;
	}

	public Integer getLiabId() {
		return liabId;
	}

	public void setLiabId(Integer liabId) {
		this.liabId = liabId;
	}

	public Integer getTranCd() {
		return tranCd;
	}

	public void setTranCd(Integer tranCd) {
		this.tranCd = tranCd;
	}

	public String getTranDesc() {
		return tranDesc;
	}

	public void setTranDesc(String tranDesc) {
		this.tranDesc = tranDesc;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public Integer getTrancode() {
		return trancode;
	}

	public void setTrancode(Integer trancode) {
		this.trancode = trancode;
	}

	public String getTrandate() {
		return trandate;
	}

	public void setTrandate(String trandate) {
		this.trandate = trandate;
	}

	public String getDebitId() {
		return debitId;
	}

	public void setDebitId(String debitId) {
		this.debitId = debitId;
	}

}
