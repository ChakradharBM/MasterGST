/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * GSTR3B Tax Payment details
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR3BTaxPaymentDetails {

	@Id
	private ObjectId id;

	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double intr;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double tx;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double fee;
	
	public GSTR3BTaxPaymentDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getIntr() {
		return intr;
	}

	public void setIntr(Double intr) {
		this.intr = intr;
	}

	public Double getTx() {
		return tx;
	}

	public void setTx(Double tx) {
		this.tx = tx;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

}
