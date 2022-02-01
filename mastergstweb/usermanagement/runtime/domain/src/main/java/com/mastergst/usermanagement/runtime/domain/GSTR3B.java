/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastergst.core.domain.Base;

/**
 * GSTR3B information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "gstr3b")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid", "gstStatus", "gstRefId", "submitStatus", "totaltax", "totalamount", "totaltaxableamount", "createdDate", "createdBy", "updatedDate", "updatedBy", "offLiab", "bankDetails","r3bautopop","r1fildt","r2bgendt","r3bgendt"})
@JsonFilter("gstr3bFilter")
public class GSTR3B extends Base {
	private String userid;
	private String fullname;
	private String clientid;
	
	private String gstStatus;
	private String gstRefId;
	private boolean submitStatus;

	private String gstin;
	@JsonProperty("ret_period")
	private String retPeriod;
	@JsonProperty("sup_details")
	private GSTR3BSupplyDetails supDetails=new GSTR3BSupplyDetails();
	@JsonProperty("inter_sup")
	private GSTR3BInterSupplyDetails interSup=new GSTR3BInterSupplyDetails();
	@JsonProperty("itc_elg")
	private GSTR3BITCDetails itcElg=new GSTR3BITCDetails();
	@JsonProperty("inward_sup")
	private GSTR3BInwardSupplyDetails inwardSup=new GSTR3BInwardSupplyDetails();
	@JsonProperty("intr_ltfee")
	private GSTR3BInterestDetails intrLtfee=new GSTR3BInterestDetails();
	
	private GSTR3BAutoPopulateDetails r3bautopop = new GSTR3BAutoPopulateDetails();
	
	private GSTR3BOffsetLiability offLiab;
	
	private Double totaltaxableamount;
	private Double totalamount;
	private Double totaltax;
	
	private String matchingId;
	private String matchingStatus;
	private Date dateofinvoice;
	
	private String r1fildt;
	private String r2bgendt;
	private String r3bgendt;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public String getGstStatus() {
		return gstStatus;
	}
	public void setGstStatus(String gstStatus) {
		this.gstStatus = gstStatus;
	}
	public String getGstRefId() {
		return gstRefId;
	}
	public void setGstRefId(String gstRefId) {
		this.gstRefId = gstRefId;
	}
	public boolean isSubmitStatus() {
		return submitStatus;
	}
	public void setSubmitStatus(boolean submitStatus) {
		this.submitStatus = submitStatus;
	}
	public String getGstin() {
		return gstin;
	}
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}
	public String getRetPeriod() {
		return retPeriod;
	}
	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}
	public GSTR3BSupplyDetails getSupDetails() {
		return supDetails;
	}
	public void setSupDetails(GSTR3BSupplyDetails supDetails) {
		this.supDetails = supDetails;
	}
	public GSTR3BInterSupplyDetails getInterSup() {
		return interSup;
	}
	public void setInterSup(GSTR3BInterSupplyDetails interSup) {
		this.interSup = interSup;
	}
	public GSTR3BITCDetails getItcElg() {
		return itcElg;
	}
	public void setItcElg(GSTR3BITCDetails itcElg) {
		this.itcElg = itcElg;
	}
	public GSTR3BInwardSupplyDetails getInwardSup() {
		return inwardSup;
	}
	public void setInwardSup(GSTR3BInwardSupplyDetails inwardSup) {
		this.inwardSup = inwardSup;
	}
	public GSTR3BInterestDetails getIntrLtfee() {
		return intrLtfee;
	}
	public void setIntrLtfee(GSTR3BInterestDetails intrLtfee) {
		this.intrLtfee = intrLtfee;
	}
	public Double getTotaltaxableamount() {
		return totaltaxableamount;
	}
	public void setTotaltaxableamount(Double totaltaxableamount) {
		this.totaltaxableamount = totaltaxableamount;
	}
	public Double getTotalamount() {
		return totalamount;
	}
	public void setTotalamount(Double totalamount) {
		this.totalamount = totalamount;
	}
	public Double getTotaltax() {
		return totaltax;
	}
	public void setTotaltax(Double totaltax) {
		this.totaltax = totaltax;
	}
	public GSTR3BOffsetLiability getOffLiab() {
		return offLiab;
	}
	public void setOffLiab(GSTR3BOffsetLiability offLiab) {
		this.offLiab = offLiab;
	}
	public String getMatchingId() {
		return matchingId;
	}
	public void setMatchingId(String matchingId) {
		this.matchingId = matchingId;
	}
	public String getMatchingStatus() {
		return matchingStatus;
	}
	public void setMatchingStatus(String matchingStatus) {
		this.matchingStatus = matchingStatus;
	}
	public Date getDateofinvoice() {
		return dateofinvoice;
	}
	public void setDateofinvoice(Date dateofinvoice) {
		this.dateofinvoice = dateofinvoice;
	}
	public GSTR3BAutoPopulateDetails getR3bautopop() {
		return r3bautopop;
	}
	public void setR3bautopop(GSTR3BAutoPopulateDetails r3bautopop) {
		this.r3bautopop = r3bautopop;
	}
	public String getR1fildt() {
		return r1fildt;
	}
	public void setR1fildt(String r1fildt) {
		this.r1fildt = r1fildt;
	}
	public String getR2bgendt() {
		return r2bgendt;
	}
	public void setR2bgendt(String r2bgendt) {
		this.r2bgendt = r2bgendt;
	}
	public String getR3bgendt() {
		return r3bgendt;
	}
	public void setR3bgendt(String r3bgendt) {
		this.r3bgendt = r3bgendt;
	}
	
}
