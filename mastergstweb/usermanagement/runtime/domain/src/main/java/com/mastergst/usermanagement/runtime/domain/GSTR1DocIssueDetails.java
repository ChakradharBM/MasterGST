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
 * GSTR1 Document Issue Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id","docnumstring"})
public class GSTR1DocIssueDetails {

	@Id
	private ObjectId id;
	private String docnumstring;
	@JsonProperty("doc_num")
	private Integer docNum;
	private List<GSTR1DocDetails> docs=LazyList.decorate(new ArrayList<GSTR1DocDetails>(), 
			FactoryUtils.instantiateFactory(GSTR1DocDetails.class));
	
	public GSTR1DocIssueDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Integer getDocNum() {
		return docNum;
	}

	public void setDocNum(Integer docNum) {
		this.docNum = docNum;
	}

	public List<GSTR1DocDetails> getDocs() {
		return docs;
	}

	public void setDocs(List<GSTR1DocDetails> docs) {
		this.docs = docs;
	}

	public String getDocnumstring() {
		return docnumstring;
	}

	public void setDocnumstring(String docnumstring) {
		this.docnumstring = docnumstring;
	}
	

}
