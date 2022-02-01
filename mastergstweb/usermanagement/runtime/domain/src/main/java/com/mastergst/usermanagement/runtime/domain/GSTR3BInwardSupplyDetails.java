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
import com.google.common.collect.Lists;

/**
 * GSTR3B Inward Supply Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
//@JsonFilter("gstr3binsupFilter")
public class GSTR3BInwardSupplyDetails {

	@Id
	private ObjectId id;

	@JsonProperty("isup_details")
	private List<GSTR3BDetails> isupDetails=Lists.newArrayList();
	
	public GSTR3BInwardSupplyDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<GSTR3BDetails> getIsupDetails() {
		return isupDetails;
	}

	public void setIsupDetails(List<GSTR3BDetails> isupDetails) {
		this.isupDetails = isupDetails;
	}

}
