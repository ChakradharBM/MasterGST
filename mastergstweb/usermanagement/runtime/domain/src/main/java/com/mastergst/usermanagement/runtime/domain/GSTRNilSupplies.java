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
 * GSTR1 Nil Supplies information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRNilSupplies {

	@Id
	private ObjectId id;
	
	private String flag;
	private String chksum;
	@JsonProperty("error_msg")
	private String errorMsg;
	private GSTRNilSupItems inter;
	private GSTRNilSupItems intra;
	
	public GSTRNilSupplies() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public GSTRNilSupItems getInter() {
		return inter;
	}

	public void setInter(GSTRNilSupItems inter) {
		this.inter = inter;
	}

	public GSTRNilSupItems getIntra() {
		return intra;
	}

	public void setIntra(GSTRNilSupItems intra) {
		this.intra = intra;
	}

}
