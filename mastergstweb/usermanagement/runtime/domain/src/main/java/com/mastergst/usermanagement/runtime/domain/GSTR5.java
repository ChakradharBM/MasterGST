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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR5 Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "gstr5")
@JsonFilter("gstr1Filter")
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid", "createdDate", "createdBy", "updatedDate", "updatedBy", 
	"totaltax", "totalamount", "totaltaxableamount", "totalitc", "dateofinvoice", "statename", "invtype", "revchargetype", "billedtoname", 
	"isbilledto", "consigneename", "consigneepos", "consigneeaddress", "ecomoperatorid", "invoiceno", "gstStatus", "gstRefId", 
	"matchingId", "matchingStatus", "errorMsg", "items","bankDetails","strDate","referenceNumber","ewayBillNumber","branch","vertical",
	"notes","terms","strAmendment","isAmendment","amendment"})
public class GSTR5 extends InvoiceParent {
	List<GSTRAdvanceTax> at=LazyList.decorate(new ArrayList<GSTRAdvanceTax>(), 
			FactoryUtils.instantiateFactory(GSTRAdvanceTax.class));
	List<GSTRB2B> b2bur=LazyList.decorate(new ArrayList<GSTRB2B>(), 
			FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTRCreditDebitNotes> cdnr=LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(), 
			FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));
	@JsonProperty("imp_g")
	List<GSTRImportDetails> impGoods=LazyList.decorate(new ArrayList<GSTRImportDetails>(), 
			FactoryUtils.instantiateFactory(GSTRImportDetails.class));

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
	public List<GSTRImportDetails> getImpGoods() {
		return impGoods;
	}
	public void setImpGoods(List<GSTRImportDetails> impGoods) {
		this.impGoods = impGoods;
	}
	
}
