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
 * GSTR Ledger Balance Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRLedgerGSTBalanceDetails {

	@Id
	private ObjectId id;
	
	@JsonProperty("igst_bal")
	private Double igstBal;
	@JsonProperty("cgst_bal")
	private Double cgstBal;
	@JsonProperty("sgst_bal")
	private Double sgstBal;
	@JsonProperty("cess_bal")
	private Double cessBal;
	
	public GSTRLedgerGSTBalanceDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getIgstBal() {
		return igstBal;
	}

	public void setIgstBal(Double igstBal) {
		this.igstBal = igstBal;
	}

	public Double getCgstBal() {
		return cgstBal;
	}

	public void setCgstBal(Double cgstBal) {
		this.cgstBal = cgstBal;
	}

	public Double getSgstBal() {
		return sgstBal;
	}

	public void setSgstBal(Double sgstBal) {
		this.sgstBal = sgstBal;
	}

	public Double getCessBal() {
		return cessBal;
	}

	public void setCessBal(Double cessBal) {
		this.cessBal = cessBal;
	}

}
