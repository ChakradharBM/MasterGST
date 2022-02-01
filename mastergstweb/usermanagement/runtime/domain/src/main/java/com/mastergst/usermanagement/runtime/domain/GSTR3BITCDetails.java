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
 * GSTR3B ITC Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
//@JsonFilter("gstr3bitcFilter")
public class GSTR3BITCDetails {

	@Id
	private ObjectId id;

	@JsonProperty("itc_avl")
	private List<GSTR3BDetails> itcAvl=Lists.newArrayList();
	@JsonProperty("itc_rev")
	private List<GSTR3BDetails> itcRev=Lists.newArrayList();
	@JsonProperty("itc_net")
	private GSTR3BDetails itcNet=new GSTR3BDetails();
	@JsonProperty("itc_inelg")
	private List<GSTR3BDetails> itcInelg=Lists.newArrayList();
	
	public GSTR3BITCDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<GSTR3BDetails> getItcAvl() {
		return itcAvl;
	}

	public void setItcAvl(List<GSTR3BDetails> itcAvl) {
		this.itcAvl = itcAvl;
	}

	public List<GSTR3BDetails> getItcRev() {
		return itcRev;
	}

	public void setItcRev(List<GSTR3BDetails> itcRev) {
		this.itcRev = itcRev;
	}

	public GSTR3BDetails getItcNet() {
		return itcNet;
	}

	public void setItcNet(GSTR3BDetails itcNet) {
		this.itcNet = itcNet;
	}

	public List<GSTR3BDetails> getItcInelg() {
		return itcInelg;
	}

	public void setItcInelg(List<GSTR3BDetails> itcInelg) {
		this.itcInelg = itcInelg;
	}

}
