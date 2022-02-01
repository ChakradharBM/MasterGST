/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * GSTR Ledger Balance Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRLedgerBalanceDetails {

	@Id
	private ObjectId id;

	private Double tx;
	private Double intr;
	private Double pen;
	private Double fee;
	private Double oth;
	private Double tot;
	
	public GSTRLedgerBalanceDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getTx() {
		return tx;
	}

	public void setTx(Double tx) {
		this.tx = tx;
	}

	public Double getIntr() {
		return intr;
	}

	public void setIntr(Double intr) {
		this.intr = intr;
	}

	public Double getPen() {
		return pen;
	}

	public void setPen(Double pen) {
		this.pen = pen;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public Double getOth() {
		return oth;
	}

	public void setOth(Double oth) {
		this.oth = oth;
	}

	public Double getTot() {
		return tot;
	}

	public void setTot(Double tot) {
		this.tot = tot;
	}

}
