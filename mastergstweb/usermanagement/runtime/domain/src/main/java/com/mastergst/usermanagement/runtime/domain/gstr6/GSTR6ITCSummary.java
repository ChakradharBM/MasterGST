/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain.gstr6;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR ITC summary information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR6ITCSummary {
	
	@Id
	private ObjectId id;
	
	private String gstin;
	@JsonProperty("ret_period")
	private String retPeriod;
	private GSTR6ITCDetails totalItc;
	private GSTR6ITCDetails elgItc;
	private GSTR6ITCDetails inelgItc;
	//private GSTR6ITCDetails elgitc;
	//private GSTR6ITCDetails inelgitc;
	
	public GSTR6ITCSummary() {
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

	public GSTR6ITCDetails getTotalItc() {
		return totalItc;
	}

	public void setTotalItc(GSTR6ITCDetails totalItc) {
		this.totalItc = totalItc;
	}

	public GSTR6ITCDetails getElgItc() {
		return elgItc;
	}

	public void setElgItc(GSTR6ITCDetails elgItc) {
		this.elgItc = elgItc;
	}

	public GSTR6ITCDetails getInelgItc() {
		return inelgItc;
	}

	public void setInelgItc(GSTR6ITCDetails inelgItc) {
		this.inelgItc = inelgItc;
	}

	/*public GSTR6ITCDetails getElgitc() {
		return elgitc;
	}

	public void setElgitc(GSTR6ITCDetails elgitc) {
		this.elgitc = elgitc;
	}

	public GSTR6ITCDetails getInelgitc() {
		return inelgitc;
	}

	public void setInelgitc(GSTR6ITCDetails inelgitc) {
		this.inelgitc = inelgitc;
	}*/

}
