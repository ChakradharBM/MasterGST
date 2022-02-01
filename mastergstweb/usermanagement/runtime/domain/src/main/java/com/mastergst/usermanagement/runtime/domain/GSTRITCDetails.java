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
 * GSTR1 ITC Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRITCDetails {

	@Id
	private ObjectId id;
	
	@JsonSerialize(using = CustomDoubleSerializer.class)
	@JsonProperty("tx_i")
	private Double iTax;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	@JsonProperty("tx_c")
	private Double cTax;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	@JsonProperty("tx_s")
	private Double sTax;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	@JsonProperty("tx_cs")
	private Double csTax;
	private String elg;

	public GSTRITCDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getiTax() {
		return iTax;
	}

	public void setiTax(Double iTax) {
		this.iTax = iTax;
	}

	public Double getcTax() {
		return cTax;
	}

	public void setcTax(Double cTax) {
		this.cTax = cTax;
	}

	public Double getsTax() {
		return sTax;
	}

	public void setsTax(Double sTax) {
		this.sTax = sTax;
	}

	public Double getCsTax() {
		return csTax;
	}

	public void setCsTax(Double csTax) {
		this.csTax = csTax;
	}

	public String getElg() {
		return elg;
	}

	public void setElg(String elg) {
		this.elg = elg;
	}

}
