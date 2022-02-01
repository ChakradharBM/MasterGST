/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * Anx Sb information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class Anx2ISDC {

	@Id
	private ObjectId id;
	private Anx2ISDCDetails org;
	private Anx2ISDCDetails amd;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public Anx2ISDCDetails getOrg() {
		return org;
	}
	public void setOrg(Anx2ISDCDetails org) {
		this.org = org;
	}
	public Anx2ISDCDetails getAmd() {
		return amd;
	}
	public void setAmd(Anx2ISDCDetails amd) {
		this.amd = amd;
	}
	
}
