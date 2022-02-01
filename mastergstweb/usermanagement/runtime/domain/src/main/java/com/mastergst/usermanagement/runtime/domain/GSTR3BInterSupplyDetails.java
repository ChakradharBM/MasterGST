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
 * GSTR3B Inter Supply Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
//@JsonFilter("gstr3bsupiFilter")
public class GSTR3BInterSupplyDetails {

	@Id
	private ObjectId id;

	@JsonProperty("unreg_details")
	private List<GSTR3BDetails> unregDetails=Lists.newArrayList();
	@JsonProperty("comp_details")
	private List<GSTR3BDetails> compDetails=Lists.newArrayList();
	@JsonProperty("uin_details")
	private List<GSTR3BDetails> uinDetails=Lists.newArrayList();
	
	public GSTR3BInterSupplyDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<GSTR3BDetails> getUnregDetails() {
		return unregDetails;
	}

	public void setUnregDetails(List<GSTR3BDetails> unregDetails) {
		this.unregDetails = unregDetails;
	}

	public List<GSTR3BDetails> getCompDetails() {
		return compDetails;
	}

	public void setCompDetails(List<GSTR3BDetails> compDetails) {
		this.compDetails = compDetails;
	}

	public List<GSTR3BDetails> getUinDetails() {
		return uinDetails;
	}

	public void setUinDetails(List<GSTR3BDetails> uinDetails) {
		this.uinDetails = uinDetails;
	}

}
