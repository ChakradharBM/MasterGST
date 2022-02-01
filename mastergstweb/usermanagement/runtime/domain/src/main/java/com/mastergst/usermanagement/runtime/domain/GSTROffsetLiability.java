/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * GSTR Offset Liability details
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "offset_liability")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id","retType"})
public class GSTROffsetLiability {

	@Id
	private ObjectId id;
	
	private String gstin;
	@JsonProperty("ret_period")
	private String retPeriod;
	private String retType;
	@JsonProperty("op_liab")
	private List<GSTRLedgerBalanceSummary> openLiab=Lists.newArrayList();
	
	public GSTROffsetLiability() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getRetPeriod() {
		return retPeriod;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	public String getRetType() {
		return retType;
	}

	public void setRetType(String retType) {
		this.retType = retType;
	}

	public List<GSTRLedgerBalanceSummary> getOpenLiab() {
		return openLiab;
	}

	public void setOpenLiab(List<GSTRLedgerBalanceSummary> openLiab) {
		this.openLiab = openLiab;
	}

}
