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
 * GSTR1 Credit Debit Notes information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR6ISDDetails {

	@Id
	private ObjectId id;
	@JsonProperty("error_msg")
	private String errorMsg;
	private List<GSTR6EligibleOrInEligibleMain> isd=LazyList.decorate(new ArrayList<GSTR6EligibleOrInEligibleMain>(), 
			FactoryUtils.instantiateFactory(GSTR6EligibleOrInEligibleMain.class));

	public GSTR6ISDDetails() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<GSTR6EligibleOrInEligibleMain> getIsd() {
		return isd;
	}

	public void setIsd(List<GSTR6EligibleOrInEligibleMain> isd) {
		this.isd = isd;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
