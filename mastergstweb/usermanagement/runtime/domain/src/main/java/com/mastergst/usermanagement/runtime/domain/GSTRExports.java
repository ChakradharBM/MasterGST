/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR1 Exports information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRExports {

	@Id
	private ObjectId id;
	
	@JsonProperty("exp_typ")
	private String expTyp;
	@JsonProperty("error_msg")
	private String errorMsg;
	private List<GSTRExportDetails> inv=LazyList.decorate(new ArrayList<GSTRExportDetails>(), 
			FactoryUtils.instantiateFactory(GSTRExportDetails.class));
	
	public GSTRExports() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getExpTyp() {
		return expTyp;
	}

	public void setExpTyp(String expTyp) {
		this.expTyp = expTyp;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List<GSTRExportDetails> getInv() {
		return inv;
	}

	public void setInv(List<GSTRExportDetails> inv) {
		this.inv = inv;
	}

}
