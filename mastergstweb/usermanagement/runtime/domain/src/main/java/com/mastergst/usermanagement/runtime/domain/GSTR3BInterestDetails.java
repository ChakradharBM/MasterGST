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
 * GSTR3B Interest Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
//@JsonFilter("gstr3bitrstFilter")
public class GSTR3BInterestDetails {

	@Id
	private ObjectId id;

	@JsonProperty("intr_details")
	private GSTR3BDetails intrDetails=new GSTR3BDetails();
	@JsonProperty("ltfee_details")
	private GSTR3BDetails latefeeDetails=new GSTR3BDetails();
	
	public GSTR3BInterestDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public GSTR3BDetails getIntrDetails() {
		return intrDetails;
	}

	public void setIntrDetails(GSTR3BDetails intrDetails) {
		this.intrDetails = intrDetails;
	}

	public GSTR3BDetails getLatefeeDetails() {
		return latefeeDetails;
	}

	public void setLatefeeDetails(GSTR3BDetails latefeeDetails) {
		this.latefeeDetails = latefeeDetails;
	}

}
