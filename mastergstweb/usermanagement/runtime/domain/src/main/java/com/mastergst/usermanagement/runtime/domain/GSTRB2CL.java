/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * GSTR1 B2CL invoices information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRB2CL {

	@Id
	private ObjectId id;
	
	private String pos;
	@JsonProperty("error_msg")
	private String errorMsg;
	@JsonProperty("diff_percent")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double diffPercent;
	private List<GSTRInvoiceDetails> inv=Lists.newArrayList();
	
	public GSTRB2CL() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List<GSTRInvoiceDetails> getInv() {
		return inv;
	}

	public void setInv(List<GSTRInvoiceDetails> inv) {
		this.inv = inv;
	}

	public Double getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(Double diffPercent) {
		this.diffPercent = diffPercent;
	}

}
