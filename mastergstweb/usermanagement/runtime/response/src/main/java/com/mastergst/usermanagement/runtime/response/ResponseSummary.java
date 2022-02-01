/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is Summary POJO.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseSummary {

	@JsonProperty("ttl_igst")
	private Double ttlIgst;
	
	@JsonProperty("ttl_cgst")
	private Double ttlCgst;
	
	@JsonProperty("ttl_sgst")
	private Double ttlSgst;
	
	@JsonProperty("sec_nm")
	private String secNm;
	
	@JsonProperty("ttl_cess")
	private Double ttlCess;
	
	@JsonProperty("ttl_tax")
	private Double ttlTax;
	
	@JsonProperty("ttl_val")
	private Double ttlVal;
	
	@JsonProperty("ttl_rec")
	private Long ttlRec;
	
	@JsonProperty("chksum")
	private String chksum;
	
	@JsonProperty("ttl_doc_issued")
	private Long ttlDocIssued;
	
	@JsonProperty("net_doc_issued")
	private Long netDocIssued;
	
	@JsonProperty("ttl_doc_cancelled")
	private Long ttlDocCancelled;
	
	@JsonProperty("ttl_nilsup_amt")
	private Double ttlNilsupAmt;
	
	@JsonProperty("ttl_expt_amt")
	private Double ttlExptAmt;
	
	@JsonProperty("ttl_ngsup_amt")
	private Double ttlNgsupAmt;
	
	@JsonProperty("rc")
	private Integer rc;
	
	@JsonProperty("ttl_txpd_igst")
	private Double ttlTxpdIgst;
	
	@JsonProperty("ttl_txpd_cgst")
	private Double ttlTxpdCgst;
	
	@JsonProperty("ttl_txpd_sgst")
	private Double ttlTxpdSgst;
	
	@JsonProperty("ttl_txpd_cess")
	private Double ttlTxpdCess;
	
	@JsonProperty("cpty_sum")
	private List<ResponseSummary> cptySum;

	public ResponseSummary() {

	}

	public Double getTtlIgst() {
		return ttlIgst;
	}

	public void setTtlIgst(Double ttlIgst) {
		this.ttlIgst = ttlIgst;
	}

	public Double getTtlCgst() {
		return ttlCgst;
	}

	public void setTtlCgst(Double ttlCgst) {
		this.ttlCgst = ttlCgst;
	}

	public Double getTtlSgst() {
		return ttlSgst;
	}

	public void setTtlSgst(Double ttlSgst) {
		this.ttlSgst = ttlSgst;
	}

	public String getSecNm() {
		return secNm;
	}

	public void setSecNm(String secNm) {
		this.secNm = secNm;
	}

	public Double getTtlCess() {
		return ttlCess;
	}

	public void setTtlCess(Double ttlCess) {
		this.ttlCess = ttlCess;
	}

	public Double getTtlTax() {
		return ttlTax;
	}

	public void setTtlTax(Double ttlTax) {
		this.ttlTax = ttlTax;
	}

	public Double getTtlVal() {
		return ttlVal;
	}

	public void setTtlVal(Double ttlVal) {
		this.ttlVal = ttlVal;
	}

	public Long getTtlRec() {
		return ttlRec;
	}

	public void setTtlRec(Long ttlRec) {
		this.ttlRec = ttlRec;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public Long getTtlDocIssued() {
		return ttlDocIssued;
	}

	public void setTtlDocIssued(Long ttlDocIssued) {
		this.ttlDocIssued = ttlDocIssued;
	}

	public Long getNetDocIssued() {
		return netDocIssued;
	}

	public void setNetDocIssued(Long netDocIssued) {
		this.netDocIssued = netDocIssued;
	}

	public Long getTtlDocCancelled() {
		return ttlDocCancelled;
	}

	public void setTtlDocCancelled(Long ttlDocCancelled) {
		this.ttlDocCancelled = ttlDocCancelled;
	}

	public Double getTtlNilsupAmt() {
		return ttlNilsupAmt;
	}

	public void setTtlNilsupAmt(Double ttlNilsupAmt) {
		this.ttlNilsupAmt = ttlNilsupAmt;
	}

	public Double getTtlExptAmt() {
		return ttlExptAmt;
	}

	public void setTtlExptAmt(Double ttlExptAmt) {
		this.ttlExptAmt = ttlExptAmt;
	}

	public Double getTtlNgsupAmt() {
		return ttlNgsupAmt;
	}

	public void setTtlNgsupAmt(Double ttlNgsupAmt) {
		this.ttlNgsupAmt = ttlNgsupAmt;
	}

	public List<ResponseSummary> getCptySum() {
		return cptySum;
	}

	public void setCptySum(List<ResponseSummary> cptySum) {
		this.cptySum = cptySum;
	}

	public Integer getRc() {
		return rc;
	}

	public void setRc(Integer rc) {
		this.rc = rc;
	}

	public Double getTtlTxpdIgst() {
		return ttlTxpdIgst;
	}

	public void setTtlTxpdIgst(Double ttlTxpdIgst) {
		this.ttlTxpdIgst = ttlTxpdIgst;
	}

	public Double getTtlTxpdCgst() {
		return ttlTxpdCgst;
	}

	public void setTtlTxpdCgst(Double ttlTxpdCgst) {
		this.ttlTxpdCgst = ttlTxpdCgst;
	}

	public Double getTtlTxpdSgst() {
		return ttlTxpdSgst;
	}

	public void setTtlTxpdSgst(Double ttlTxpdSgst) {
		this.ttlTxpdSgst = ttlTxpdSgst;
	}

	public Double getTtlTxpdCess() {
		return ttlTxpdCess;
	}

	public void setTtlTxpdCess(Double ttlTxpdCess) {
		this.ttlTxpdCess = ttlTxpdCess;
	}
}
