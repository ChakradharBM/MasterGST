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
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR1 Document Issue information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "doc_issue")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id","returnPeriod", "clientid"})
public class GSTR1DocumentIssue {

	@Id
	private ObjectId id;

	private String flag;
	private String chksum;
	private String clientid;
	private String returnPeriod;
	@JsonProperty("doc_det")
	private List<GSTR1DocIssueDetails> docDet=LazyList.decorate(new ArrayList<GSTR1DocIssueDetails>(), 
			FactoryUtils.instantiateFactory(GSTR1DocIssueDetails.class));
	
	public GSTR1DocumentIssue() {
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

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public List<GSTR1DocIssueDetails> getDocDet() {
		return docDet;
	}

	public void setDocDet(List<GSTR1DocIssueDetails> docDet) {
		this.docDet = docDet;
	}

}
