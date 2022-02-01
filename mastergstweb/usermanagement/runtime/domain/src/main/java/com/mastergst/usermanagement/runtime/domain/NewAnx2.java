/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Anx1 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "Anx2")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid", "createdDate", "createdBy", "updatedDate", "updatedBy", 
	"totaltax", "totalamount", "totaltaxableamount", "totalitc", "dateofinvoice", "statename", "invtype", "revchargetype", "billedtoname", 
	"isbilledto", "consigneename", "consigneepos", "consigneeaddress", "ecomoperatorid", "invoiceno", "gstStatus", "gstRefId","matchingId", 
	"matchingStatus", "errorMsg", "items","bankDetails","strDate","referenceNumber","ewayBillNumber","branch","vertical","notes","terms",
	"strAmendment","isAmendment","amendment","advRemainingAmount","advOriginalInvoiceNumber","advPCustname","advPInvamt","advPIgstamt","advPCgstamt","advPSgstamt","paymentStatus","govtInvoiceStatus","ledgerName","vendorName","dealerType",
	"tdstcsenable","section","tcstdspercentage","tcstdsAmount","netAmount","invoiceEcomOperator","invoiceEcomGSTIN","customField1","customField2","customField3","customField4","clientAddress","itcClaimedDate","taxRate","generateMode","eBillDate","eBillNo","supplyType",
	"subSupplyType","docType","fromGstin","fromTrdName","fromAddr1","fromAddr2","fromPlace","fromPincode","fromStateCode","toGstin","toTrdName","toAddr1","toAddr2","toPlace","toPincode","toStateCode","transporterId","transporterName","status","actualDist","noValidDays","validUpto","extendedTimes","rejectStatus",
	"vehicleType","transactionType","otherValue","cessNonAdvolValue","VehiclListDetails","lutNo","totalIgstAmount","totalCgstAmount","totalSgstAmount","totalCessAmount","totalExemptedAmount","sftr","transDistance","actFromStateCode","actToStateCode","cancelebillcmnts","mannualMatchInvoices","hsnsum","categorytype","mthCd",
	"qrtCd","yrCd","trDatemthCd","trDateqrtCd","trDateyrCd","sftr","totaltaxableamount_str","totaltax_str","totalamount_str","dateofinvoice_str","ewayBillDate_str","eCommSupplyType","addressType","buyerPincode","einvCategory","einvExpCategory","countryCode","irnNo","irnStatus","taxSch","version","typ","Version","Typ","totalStateCessAmount","totalAssAmount","totalCessNonAdVal",
	"dispatcherDtls","shipmentDtls","docDtls","tranDtls","sellerDtls","buyerDtls","valDtls","bchDtls","payDtls","refDtls","addlDocDtls","expDtls","ewbDtls","itemList","DispatcherDtls","ShipmentDtls","DocDtls","TranDtls","SellerDtls","BuyerDtls","ValDtls","BchDtls","PayDtls","RefDtls","AddlDocDtls","ExpDtls","EwbDtls","ItemList","s3attachment"})
public class NewAnx2 extends AnxInvoiceParent {
	

	private String rtnprd;
	
	@JsonProperty("b2b")
	private List<Anx2Docs> anx2b2b;
	private List<Anx2Docs> sezwp;
	private List<Anx2Docs> sezwop;
	private List<Anx2Docs> de;
	private List<Anx2ISDC> isdc;
	//private List<Anx2ITC> itcsum;
	public String getRtnprd() {
		return rtnprd;
	}
	public void setRtnprd(String rtnprd) {
		this.rtnprd = rtnprd;
	}

	public List<Anx2Docs> getAnx2b2b() {
		return anx2b2b;
	}
	public void setAnx2b2b(List<Anx2Docs> anx2b2b) {
		this.anx2b2b = anx2b2b;
	}
	public List<Anx2Docs> getSezwp() {
		return sezwp;
	}
	public void setSezwp(List<Anx2Docs> sezwp) {
		this.sezwp = sezwp;
	}
	public List<Anx2Docs> getSezwop() {
		return sezwop;
	}
	public void setSezwop(List<Anx2Docs> sezwop) {
		this.sezwop = sezwop;
	}
	public List<Anx2Docs> getDe() {
		return de;
	}
	public void setDe(List<Anx2Docs> de) {
		this.de = de;
	}
	public List<Anx2ISDC> getIsdc() {
		return isdc;
	}
	public void setIsdc(List<Anx2ISDC> isdc) {
		this.isdc = isdc;
	}

}
