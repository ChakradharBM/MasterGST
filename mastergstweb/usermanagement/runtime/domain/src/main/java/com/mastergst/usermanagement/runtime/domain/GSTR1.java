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
 * GSTR1 Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "gstr1")
@JsonFilter("gstr1Filter")
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid", "createdDate", "createdBy", "updatedDate", "updatedBy", "customerEmail","customerCCMailIds","customerMailIds","mailSubject","mailMessage","isIncludeSignature","gstr2bMatchingId","gstr2bMatchingStatus","gstr2bMatchingRsn","docKey","docId",
	"totaltax", "totalamount", "totaltaxableamount", "totalitc", "dateofinvoice", "statename", "invtype", "revchargetype", "billedtoname", 
	"isbilledto", "consigneename", "consigneepos", "consigneeaddress", "ecomoperatorid", "invoiceno", "gstStatus", "gstRefId","matchingId", 
	"matchingStatus", "errorMsg", "items","bankDetails","strDate","referenceNumber","ewayBillNumber","branch","vertical","notes","terms",
	"strAmendment","isAmendment","amendment","advRemainingAmount","advOriginalInvoiceNumber","advPCustname","advPInvamt","advPIgstamt","advPCgstamt","advPSgstamt","paymentStatus","govtInvoiceStatus","ledgerName","vendorName","dealerType",
	"tdstcsenable","section","tcstdspercentage","tcstdsAmount","netAmount","invoiceEcomOperator","invoiceEcomGSTIN","customField1","customField2","customField3","customField4","clientAddress","itcClaimedDate","taxRate","generateMode","eBillDate","eBillNo","supplyType",
	"subSupplyType","docType","fromGstin","fromTrdName","fromAddr1","fromAddr2","fromPlace","fromPincode","fromStateCode","toGstin","toTrdName","toAddr1","toAddr2","toPlace","toPincode","toStateCode","transporterId","transporterName","status","actualDist","noValidDays","validUpto","extendedTimes","rejectStatus",
	"vehicleType","transactionType","otherValue","cessNonAdvolValue","VehiclListDetails","lutNo","totalIgstAmount","totalCgstAmount","totalSgstAmount","totalCessAmount","totalExemptedAmount","transDistance","actFromStateCode","actToStateCode","cancelebillcmnts","mannualMatchInvoices","hsnsum","categorytype","mthCd",
	"qrtCd","yrCd","trDatemthCd","trDateqrtCd","trDateyrCd","sftr","totaltaxableamount_str","totaltax_str","totalamount_str","dateofinvoice_str","ewayBillDate_str","eCommSupplyType","addressType","buyerPincode","einvCategory","einvExpCategory","countryCode","irnNo","irnStatus","taxSch","version","typ","Version","Typ","totalStateCessAmount","totalAssAmount","totalCessNonAdVal","totalDiscountAmount","totalOthrChrgeAmount",
	"dispatcherDtls","shipmentDtls","docDtls","tranDtls","sellerDtls","buyerDtls","valDtls","bchDtls","payDtls","refDtls","addlDocDtls","expDtls","ewbDtls","itemList","DispatcherDtls","ShipmentDtls","DocDtls","TranDtls","SellerDtls","BuyerDtls","ValDtls","BchDtls","PayDtls","RefDtls","AddlDocDtls","ExpDtls","EwbDtls","ItemList","s3attachment","csftr","s3attachementDate","gstr1orEinvoice","revchargeNo","IgstOnIntra","igstOnIntra",
	"signedQrCode","signedInvoice","ackNo","einvStatus","ackDt","fromPin","toPin","transType","strTransDate","addcurrencyCode","exchangeRate","bankname","accountnumber","accountname","branchname","ifsccode"
	,"dispatchname","dispatchAddr1","dispatchAddr2","dispatchLoc","dispatchState","dispatchpin","shipmentGstin","shipmentTrdnm","shipmentLgnm","shipmentAddr1","shipmentAddr2","shipmentLoc","shipmentState","shipmentpin","itcavl","samebilladdress","pendingAmount","receivedAmount","convertedtoinv","dueDate","termDays","originalinvamount","tcsorTdsType","subSupplyDesc","customFieldText1","customFieldText2","customFieldText3","customFieldText4","premium","period","printerintra","srctype","docuploads","cessType","amendmentRefId","advTaxableType","totalamountforbillofsupplycdn","cdnnilsupplies","tdsenable","tdsSection","tdspercentage","tdsAmount","tdsNetAmount","strOdate","invoiceLevelCess","extendValidity","ebillValidator","ewaybillRejectDate","entertaimentprintto"})
public class GSTR1 extends InvoiceParent {
	List<GSTRAdvanceTax> at=LazyList.decorate(new ArrayList<GSTRAdvanceTax>(), 
			FactoryUtils.instantiateFactory(GSTRAdvanceTax.class));
	List<GSTRB2B> b2bur=LazyList.decorate(new ArrayList<GSTRB2B>(), 
			FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTRCreditDebitNotes> cdnr=LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(), 
			FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));
    List<GSTRB2B> b2ba=LazyList.decorate(new ArrayList<GSTRB2B>(), FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTRB2CL> b2cla = LazyList.decorate(new ArrayList<GSTRB2CL>(), FactoryUtils.instantiateFactory(GSTRB2CL.class));
	List<GSTRB2CSA> b2csa=LazyList.decorate(new ArrayList<GSTRB2CSA>(), FactoryUtils.instantiateFactory(GSTRB2CSA.class));
	List<GSTRAdvanceTax> ata = LazyList.decorate(new ArrayList<GSTRAdvanceTax>(), FactoryUtils.instantiateFactory(GSTRAdvanceTax.class));
	
	
	List<GSTRExports> expa = LazyList.decorate(new ArrayList<GSTRExports>(),FactoryUtils.instantiateFactory(GSTRExports.class));
	List<GSTRInvoiceDetails> cdnura = LazyList.decorate(new ArrayList<GSTRInvoiceDetails>(),FactoryUtils.instantiateFactory(GSTRInvoiceDetails.class));
	List<GSTRCreditDebitNotes> cdnra=LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(), FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));
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
	public List<GSTRB2B> getB2ba() {
		return b2ba;
	}
	public void setB2ba(List<GSTRB2B> b2ba) {
		this.b2ba = b2ba;
	}
	public List<GSTRB2CL> getB2cla() {
		return b2cla;
	}
		
	public List<GSTRB2CSA> getB2csa() {
		return b2csa;
	}
	public void setB2csa(List<GSTRB2CSA> b2csa) {
		this.b2csa = b2csa;
	}
	public void setB2cla(List<GSTRB2CL> b2cla) {
		this.b2cla = b2cla;
	}
	public List<GSTRAdvanceTax> getAta() {
		return ata;
	}
	public void setAta(List<GSTRAdvanceTax> ata) {
		this.ata = ata;
	}
	
	public List<GSTRExports> getExpa() {
		return expa;
	}
	public void setExpa(List<GSTRExports> expa) {
		this.expa = expa;
	}
	public List<GSTRInvoiceDetails> getCdnura() {
		return cdnura;
	}
	public void setCdnura(List<GSTRInvoiceDetails> cdnura) {
		this.cdnura = cdnura;
	}
	public List<GSTRCreditDebitNotes> getCdnra() {
		return cdnra;
	}
	public void setCdnra(List<GSTRCreditDebitNotes> cdnra) {
		this.cdnra = cdnra;
	}
}
