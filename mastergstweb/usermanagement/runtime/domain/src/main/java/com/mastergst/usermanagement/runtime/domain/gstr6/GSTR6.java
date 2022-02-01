/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain.gstr6;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRCreditDebitNotes;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;

/**
 * GSTR6 Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "gstr6")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
//@JsonFilter("gstr2Filter")
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid", "createdDate", "createdBy", "updatedDate", "updatedBy"})
/*@JsonFilter("gstr6Filter")
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid", "createdDate", "createdBy", "updatedDate", "updatedBy", 
	"totaltax", "totalamount", "totaltaxableamount", "totalitc", "dateofinvoice", "statename", "invtype", "revchargetype", "billedtoname", 
	"isbilledto", "consigneename", "consigneepos", "consigneeaddress", "ecomoperatorid", "invoiceno", "gstStatus", "gstRefId", 
	"matchingId", "matchingStatus", "errorMsg", "items","bankDetails","strDate","referenceNumber","ewayBillNumber","branch","vertical",
	"notes","terms","strAmendment","isAmendment","amendment"})*/
public class GSTR6 extends InvoiceParent {
	
	List<GSTR6ISD> isd=LazyList.decorate(new ArrayList<GSTR6ISD>(), 
			FactoryUtils.instantiateFactory(GSTR6ISD.class));
	List<GSTRB2B> b2ba=LazyList.decorate(new ArrayList<GSTRB2B>(), 
			FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTRCreditDebitNotes> cdna=LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(), 
			FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));
	List<GSTR6ISD> isda=LazyList.decorate(new ArrayList<GSTR6ISD>(), 
			FactoryUtils.instantiateFactory(GSTR6ISD.class));
	GSTR6LatefeeDetails latefee;
	//private GSTR6ITCSummary itcDetails;
	private GSTR6ITCDetails totalItc;
	private GSTR6ITCDetails elgItc;
	private GSTR6ITCDetails inelgItc;
	public List<GSTR6ISD> getIsd() {
		return isd;
	}
	public void setIsd(List<GSTR6ISD> isd) {
		this.isd = isd;
	}
	public List<GSTRB2B> getB2ba() {
		return b2ba;
	}
	public void setB2ba(List<GSTRB2B> b2ba) {
		this.b2ba = b2ba;
	}
	public List<GSTRCreditDebitNotes> getCdna() {
		return cdna;
	}
	public void setCdna(List<GSTRCreditDebitNotes> cdna) {
		this.cdna = cdna;
	}
	public List<GSTR6ISD> getIsda() {
		return isda;
	}
	public void setIsda(List<GSTR6ISD> isda) {
		this.isda = isda;
	}
	public GSTR6LatefeeDetails getLatefee() {
		return latefee;
	}
	public void setLatefee(GSTR6LatefeeDetails latefee) {
		this.latefee = latefee;
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
	/*public GSTR6ITCSummary getItcDetails() {
		return itcDetails;
	}
	public void setItcDetails(GSTR6ITCSummary itcDetails) {
		this.itcDetails = itcDetails;
	}*/
	@Override
	public String toString() {
		return "GSTR6 [b2ba=" + b2ba + ", cdna=" + cdna + "]";
	}
	
	
	
}
