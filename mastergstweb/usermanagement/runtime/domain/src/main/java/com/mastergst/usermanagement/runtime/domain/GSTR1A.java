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
 * GSTR1A Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "gstr1a")
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid", "createdDate", "createdBy", "updatedDate", "updatedBy", 
	"totaltax", "totalamount", "totaltaxableamount", "totalitc", "dateofinvoice", "statename", "invtype", "revchargetype", "billedtoname", 
	"isbilledto", "consigneename", "consigneepos", "consigneeaddress", "ecomoperatorid", "invoiceno", "gstStatus", "gstRefId", 
	"matchingId", "matchingStatus", "errorMsg", "items","bankDetails","strDate","referenceNumber","ewayBillNumber","branch","vertical",
	"notes","terms","strAmendment","isAmendment","amendment"})
public class GSTR1A extends InvoiceParent {
	List<GSTRCreditDebitNotes> cdnr=LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(), 
			FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));
	
	public List<GSTRCreditDebitNotes> getCdnr() {
		return cdnr;
	}
	public void setCdnr(List<GSTRCreditDebitNotes> cdnr) {
		this.cdnr = cdnr;
	}
	
}
