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
 * GSTR3B Tax Payment information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR3BTaxPayment {

	@Id
	private ObjectId id;

	@JsonProperty("liab_ldg_id")
	private Integer liabLdgId;
	@JsonProperty("trans_typ")
	private Integer transType;
	@JsonProperty("trans_desc")
	private String transDesc;
	private GSTR3BTaxPaymentDetails igst;
	private GSTR3BTaxPaymentDetails cgst;
	private GSTR3BTaxPaymentDetails sgst;
	private GSTR3BTaxPaymentDetails cess;
	
	public GSTR3BTaxPayment() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Integer getLiabLdgId() {
		return liabLdgId;
	}

	public void setLiabLdgId(Integer liabLdgId) {
		this.liabLdgId = liabLdgId;
	}

	public Integer getTransType() {
		return transType;
	}

	public void setTransType(Integer transType) {
		this.transType = transType;
	}

	public String getTransDesc() {
		return transDesc;
	}

	public void setTransDesc(String transDesc) {
		this.transDesc = transDesc;
	}

	public GSTR3BTaxPaymentDetails getIgst() {
		return igst;
	}

	public void setIgst(GSTR3BTaxPaymentDetails igst) {
		this.igst = igst;
	}

	public GSTR3BTaxPaymentDetails getCgst() {
		return cgst;
	}

	public void setCgst(GSTR3BTaxPaymentDetails cgst) {
		this.cgst = cgst;
	}

	public GSTR3BTaxPaymentDetails getSgst() {
		return sgst;
	}

	public void setSgst(GSTR3BTaxPaymentDetails sgst) {
		this.sgst = sgst;
	}

	public GSTR3BTaxPaymentDetails getCess() {
		return cess;
	}

	public void setCess(GSTR3BTaxPaymentDetails cess) {
		this.cess = cess;
	}

}
