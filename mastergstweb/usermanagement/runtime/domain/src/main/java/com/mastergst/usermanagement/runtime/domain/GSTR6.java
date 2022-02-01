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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * GSTR6 Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "gstr6")
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid", "createdDate", "createdBy", "updatedDate", "updatedBy", 
	"totaltax", "totalamount", "totaltaxableamount", "totalitc", "dateofinvoice", "statename", "invtype", "revchargetype", "billedtoname", 
	"isbilledto", "consigneename", "consigneepos", "consigneeaddress", "ecomoperatorid", "invoiceno", "gstStatus", "gstRefId", 
	"matchingId", "matchingStatus", "errorMsg", "items","bankDetails","strDate","referenceNumber","ewayBillNumber","branch","vertical",
	"notes","terms","strAmendment","isAmendment","amendment"})
public class GSTR6 extends InvoiceParent {
	
	/*List<GSTRISD> isd=LazyList.decorate(new ArrayList<GSTRISD>(), 
			FactoryUtils.instantiateFactory(GSTRISD.class));
	List<GSTRB2B> b2ba=LazyList.decorate(new ArrayList<GSTRB2B>(), 
			FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTRCreditDebitNotes> cdna=LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(), 
			FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));
	List<GSTRISD> isda=LazyList.decorate(new ArrayList<GSTRISD>(), 
			FactoryUtils.instantiateFactory(GSTRISD.class));
	GSTR6LatefeeDetails latefee;
	private GSTR6ITCSummary itcDetails;
	
	public List<GSTRISD> getIsd() {
		return isd;
	}
	public void setIsd(List<GSTRISD> isd) {
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
	public List<GSTRISD> getIsda() {
		return isda;
	}
	public void setIsda(List<GSTRISD> isda) {
		this.isda = isda;
	}
	public GSTR6LatefeeDetails getLatefee() {
		return latefee;
	}
	public void setLatefee(GSTR6LatefeeDetails latefee) {
		this.latefee = latefee;
	}
	public GSTR6ITCSummary getItcDetails() {
		return itcDetails;
	}
	public void setItcDetails(GSTR6ITCSummary itcDetails) {
		this.itcDetails = itcDetails;
	}
	*/
}
