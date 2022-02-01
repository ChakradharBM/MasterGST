/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR1 Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "ewaybill")
@JsonFilter("gstr1Filter")

public class EWAYBILL extends InvoiceParent {
	List<GSTRAdvanceTax> at=LazyList.decorate(new ArrayList<GSTRAdvanceTax>(), 
			FactoryUtils.instantiateFactory(GSTRAdvanceTax.class));
	List<GSTRB2B> b2bur=LazyList.decorate(new ArrayList<GSTRB2B>(), 
			FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTRCreditDebitNotes> cdnr=LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(), 
			FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));
	
	@JsonProperty("doc_issue")
	GSTR1DocumentIssue docIssue;

	public List<GSTRAdvanceTax> getAt() {
		return at;
	}
	public void setAt(List<GSTRAdvanceTax> at) {
		this.at = at;
	}
	public List<GSTRB2B> getB2bur() {
		return b2bur;
	}
	public void setB2bur(List<GSTRB2B> b2bur) {
		this.b2bur = b2bur;
	}
	public List<GSTRCreditDebitNotes> getCdnr() {
		return cdnr;
	}
	public void setCdnr(List<GSTRCreditDebitNotes> cdnr) {
		this.cdnr = cdnr;
	}
	public GSTR1DocumentIssue getDocIssue() {
		return docIssue;
	}
	public void setDocIssue(GSTR1DocumentIssue docIssue) {
		this.docIssue = docIssue;
	}
	
	
}
