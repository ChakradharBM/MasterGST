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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * GSTR1 Nil Invoices information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRNilItems {

	@Id
	private ObjectId id;
	
	@JsonProperty("nil_amt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double nilAmt;
	@JsonProperty("expt_amt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double exptAmt;
	@JsonProperty("ngsup_amt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double ngsupAmt;
	@JsonProperty("sply_ty")
	private String splyType;

	public GSTRNilItems() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getNilAmt() {
		return nilAmt;
	}

	public void setNilAmt(Double nilAmt) {
		this.nilAmt = nilAmt;
	}

	public Double getExptAmt() {
		return exptAmt;
	}

	public void setExptAmt(Double exptAmt) {
		this.exptAmt = exptAmt;
	}

	public Double getNgsupAmt() {
		return ngsupAmt;
	}

	public void setNgsupAmt(Double ngsupAmt) {
		this.ngsupAmt = ngsupAmt;
	}

	public String getSplyType() {
		return splyType;
	}

	public void setSplyType(String splyType) {
		this.splyType = splyType;
	}
	@Override
	public String toString() {
		return "GSTRNilItems [id=" + id + ", nilAmt=" + nilAmt + ", exptAmt=" + exptAmt + ", ngsupAmt=" + ngsupAmt
				+ ", splyType=" + splyType + "]";
	}

}
