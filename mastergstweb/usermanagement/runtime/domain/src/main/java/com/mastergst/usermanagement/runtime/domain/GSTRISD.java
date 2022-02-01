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
 * GSTR2 ISD information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRISD {

	@Id
	private ObjectId id;
	
	private String ctin;
	private String cfs;
	@JsonProperty("error_msg")
	private String errorMsg;
	
	private List<GSTRDocListDetails> doclist = LazyList.decorate(new ArrayList<GSTRDocListDetails>(), 
			FactoryUtils.instantiateFactory(GSTRDocListDetails.class));
	
	public GSTRISD() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getCtin() {
		return ctin;
	}
	public void setCtin(String ctin) {
		this.ctin = ctin;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List<GSTRDocListDetails> getDoclist() {
		return doclist;
	}

	public void setDoclist(List<GSTRDocListDetails> doclist) {
		this.doclist = doclist;
	}

	public String getCfs() {
		return cfs;
	}

	public void setCfs(String cfs) {
		this.cfs = cfs;
	}
}
