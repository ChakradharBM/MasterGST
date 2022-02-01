/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Anx information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid", "createdDate", "createdBy", "updatedDate", "updatedBy", 
	"totaltax", "totalamount", "totaltaxableamount", "totalitc", "dateofinvoice", "statename", "invtype", "revchargetype", "billedtoname", 
	"isbilledto", "consigneename", "consigneepos", "consigneeaddress", "ecomoperatorid", "invoiceno", "gstStatus", "gstRefId","matchingId", 
	"matchingStatus", "errorMsg", "items","bankDetails","strDate","referenceNumber","ewayBillNumber","branch","vertical","notes","terms",
	"strAmendment","isAmendment","amendment","advRemainingAmount","advOriginalInvoiceNumber","advPCustname","advPInvamt","advPIgstamt","advPCgstamt","advPSgstamt","paymentStatus","govtInvoiceStatus","ledgerName","vendorName","dealerType",
	"tdstcsenable","section","tcstdspercentage","tcstdsAmount","netAmount","invoiceEcomOperator","invoiceEcomGSTIN","customField1","customField2","customField3","customField4","clientAddress","itcClaimedDate","taxRate","generateMode","eBillDate","eBillNo","supplyType",
	"subSupplyType","docType","fromGstin","fromTrdName","fromAddr1","fromAddr2","fromPlace","fromPincode","fromStateCode","toGstin","toTrdName","toAddr1","toAddr2","toPlace","toPincode","toStateCode","transporterId","transporterName","status","actualDist","noValidDays","validUpto","extendedTimes","rejectStatus",
	"vehicleType","transactionType","otherValue","cessNonAdvolValue","VehiclListDetails","lutNo","totalIgstAmount","totalCgstAmount","totalSgstAmount","totalCessAmount","totalExemptedAmount"})
public class Anx1 extends InvoiceParent {
	
	private String rtnprd;
	private String profile;
	private String issez;
	@JsonProperty("b2b")
	private List<AnxDocs> anxb2b;
	private List<AnxDetails> b2c;
	private List<AnxDetails> expwp;
	private List<AnxDetails> expwop;
	private List<AnxDocs> sezwp;
	private List<AnxDocs> sezwop;
	private List<AnxDocs> de;
	private List<AnxDocs> rev;
	private List<AnxDocs> impg;
	private List<AnxDetails> imps;
	private List<AnxDocs> impgsez;
	private List<AnxDocs> mis;
	private List<AnxDocs> b2ba;
	private List<AnxDocs> dea;
	private List<AnxDocs> sezwpa;
	private List<AnxDocs> sezwopa;
	private List<AnxEcom> ecom;

	public String getRtnprd() {
		return rtnprd;
	}

	public void setRtnprd(String rtnprd) {
		this.rtnprd = rtnprd;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getIssez() {
		return issez;
	}
	public void setIssez(String issez) {
		this.issez = issez;
	}

	public List<AnxDetails> getB2c() {
		return b2c;
	}

	public void setB2c(List<AnxDetails> b2c) {
		this.b2c = b2c;
	}

	public List<AnxDocs> getAnxb2b() {
		return anxb2b;
	}

	public void setAnxb2b(List<AnxDocs> anxb2b) {
		this.anxb2b = anxb2b;
	}

	public List<AnxDetails> getExpwp() {
		return expwp;
	}

	public void setExpwp(List<AnxDetails> expwp) {
		this.expwp = expwp;
	}

	public List<AnxDetails> getExpwop() {
		return expwop;
	}

	public void setExpwop(List<AnxDetails> expwop) {
		this.expwop = expwop;
	}

	public List<AnxDocs> getSezwp() {
		return sezwp;
	}

	public void setSezwp(List<AnxDocs> sezwp) {
		this.sezwp = sezwp;
	}

	public List<AnxDocs> getSezwop() {
		return sezwop;
	}

	public void setSezwop(List<AnxDocs> sezwop) {
		this.sezwop = sezwop;
	}

	public List<AnxDocs> getDe() {
		return de;
	}

	public void setDe(List<AnxDocs> de) {
		this.de = de;
	}

	public List<AnxDocs> getRev() {
		return rev;
	}

	public void setRev(List<AnxDocs> rev) {
		this.rev = rev;
	}

	public List<AnxDocs> getImpg() {
		return impg;
	}

	public void setImpg(List<AnxDocs> impg) {
		this.impg = impg;
	}

	public List<AnxDetails> getImps() {
		return imps;
	}

	public void setImps(List<AnxDetails> imps) {
		this.imps = imps;
	}

	public List<AnxDocs> getImpgsez() {
		return impgsez;
	}

	public void setImpgsez(List<AnxDocs> impgsez) {
		this.impgsez = impgsez;
	}

	public List<AnxDocs> getMis() {
		return mis;
	}

	public void setMis(List<AnxDocs> mis) {
		this.mis = mis;
	}

	public List<AnxDocs> getB2ba() {
		return b2ba;
	}

	public void setB2ba(List<AnxDocs> b2ba) {
		this.b2ba = b2ba;
	}

	public List<AnxDocs> getDea() {
		return dea;
	}

	public void setDea(List<AnxDocs> dea) {
		this.dea = dea;
	}

	public List<AnxDocs> getSezwpa() {
		return sezwpa;
	}

	public void setSezwpa(List<AnxDocs> sezwpa) {
		this.sezwpa = sezwpa;
	}

	public List<AnxDocs> getSezwopa() {
		return sezwopa;
	}

	public void setSezwopa(List<AnxDocs> sezwopa) {
		this.sezwopa = sezwopa;
	}

	public List<AnxEcom> getEcom() {
		return ecom;
	}

	public void setEcom(List<AnxEcom> ecom) {
		this.ecom = ecom;
	}

}
