/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicAddress;
import com.mastergst.usermanagement.runtime.domain.GSTR1DocumentIssue;
import com.mastergst.usermanagement.runtime.domain.GSTR3BAutoPopulateDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR3BITCDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR3BInterSupplyDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR3BInterestDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR3BInwardSupplyDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR3BOffsetLiability;
import com.mastergst.usermanagement.runtime.domain.GSTR3BSupplyDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR4TaxSummary;
import com.mastergst.usermanagement.runtime.domain.GSTR6ITCSummary;
import com.mastergst.usermanagement.runtime.domain.GSTR6LatefeeMain;
import com.mastergst.usermanagement.runtime.domain.HSNSummaryResponse;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.InvoiceParentSupport;

/**
 * This class is Reference Data POJO.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseData {

	@JsonProperty("reference_id")
	private String referenceId;
	
	@JsonProperty("ref_id")
	private String refId;
	
	@JsonProperty("status_cd")
	private String statusCd;
	
	@JsonProperty("ret_period")
	private String retPeriod;
	
	@JsonProperty("gstin")
	private String gstin;
	
	@JsonProperty("ctb")
	private String ctb;
	
	@JsonProperty("rgdt")
	private String rgdt;
	
	@JsonProperty("sts")
	private String sts;
	
	@JsonProperty("lgnm")
	private String lgnm;
	
	@JsonProperty("stj")
	private String stj;
	
	@JsonProperty("ctj")
	private String ctj;
	
	@JsonProperty("dty")
	private String dty;
	
	@JsonProperty("cxdt")
	private String cxdt;
	
	private String lstupdt;
	private String stjCd;
	private String ctjCd;
	private String tradeNam;
	
	@JsonProperty("nba")
	private List<String> nba;
	
	@JsonProperty("chksum")
	private String chksum;
	
	@JsonProperty("adadr")
	private List<GSTINPublicAddress> adadr;
	
	@JsonProperty("pradr")
	private GSTINPublicAddress pradr;
	
	@JsonProperty("summ_typ")
	private String sumType;
	
	@JsonProperty("dbt_id")
	private String dbtId;
	
	@JsonProperty("sec_sum")
	private List<ResponseSummary> secSum;
	
	@JsonProperty("section_summary")
	private List<ResponseSummary> sectionSummary;
	
	@JsonProperty("itcDetails")
	private GSTR6ITCSummary itcDetails;
	
	@JsonProperty("lateFeemain")
	private GSTR6LatefeeMain lateFeemain;
	
	@JsonProperty("tax_py_pd")
	private GSTR4TaxSummary taxPyPd;
	
	@JsonProperty("error_report")
	private InvoiceParentSupport errorReport;
	
	@JsonProperty("hsn")
	private HSNSummaryResponse hsn;
	
	@JsonProperty("sup_details")
	private GSTR3BSupplyDetails supDetails;
	@JsonProperty("inter_sup")
	private GSTR3BInterSupplyDetails interSup;
	@JsonProperty("itc_elg")
	private GSTR3BITCDetails itcElg;
	@JsonProperty("inward_sup")
	private GSTR3BInwardSupplyDetails inwardSup;
	@JsonProperty("intr_ltfee")
	private GSTR3BInterestDetails intrLtfee;
	@JsonProperty("tx_pmt")
	private GSTR3BOffsetLiability taxPymt;
	@JsonProperty("doc_issue")
	private GSTR1DocumentIssue docIssue;
	@JsonProperty("EFiledlist")
	private List<TrackResponse> statusList;
	private GSTR3BAutoPopulateDetails r3bautopop;
	
	@JsonProperty("generate_summary")
	private String generateSummary;
	
	public ResponseData() {

	}

	public ResponseData(String referenceId, String statusCd, InvoiceParentSupport errorReport) {
		this.referenceId = referenceId;
		this.statusCd = statusCd;
		this.errorReport = errorReport;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getStatusCd() {
		return statusCd;
	}

	public void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}

	public String getRetPeriod() {
		return retPeriod;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public List<ResponseSummary> getSecSum() {
		return secSum;
	}

	public void setSecSum(List<ResponseSummary> secSum) {
		this.secSum = secSum;
	}

	public InvoiceParentSupport getErrorReport() {
		return errorReport;
	}

	public void setErrorReport(InvoiceParentSupport errorReport) {
		this.errorReport = errorReport;
	}

	public HSNSummaryResponse getHsn() {
		return hsn;
	}

	public void setHsn(HSNSummaryResponse hsn) {
		this.hsn = hsn;
	}

	public String getCtb() {
		return ctb;
	}

	public void setCtb(String ctb) {
		this.ctb = ctb;
	}

	public String getSts() {
		return sts;
	}

	public void setSts(String sts) {
		this.sts = sts;
	}

	public String getLgnm() {
		return lgnm;
	}

	public void setLgnm(String lgnm) {
		this.lgnm = lgnm;
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

	public GSTR3BOffsetLiability getTaxPymt() {
		return taxPymt;
	}

	public void setTaxPymt(GSTR3BOffsetLiability taxPymt) {
		this.taxPymt = taxPymt;
	}

	public List<String> getNba() {
		return nba;
	}

	public void setNba(List<String> nba) {
		this.nba = nba;
	}

	public String getRgdt() {
		return rgdt;
	}

	public void setRgdt(String rgdt) {
		this.rgdt = rgdt;
	}

	public String getStj() {
		return stj;
	}

	public void setStj(String stj) {
		this.stj = stj;
	}

	public String getCtj() {
		return ctj;
	}

	public void setCtj(String ctj) {
		this.ctj = ctj;
	}

	public String getDty() {
		return dty;
	}

	public void setDty(String dty) {
		this.dty = dty;
	}

	public String getCxdt() {
		return cxdt;
	}

	public void setCxdt(String cxdt) {
		this.cxdt = cxdt;
	}

	public GSTR1DocumentIssue getDocIssue() {
		return docIssue;
	}

	public void setDocIssue(GSTR1DocumentIssue docIssue) {
		this.docIssue = docIssue;
	}

	public List<TrackResponse> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<TrackResponse> statusList) {
		this.statusList = statusList;
	}

	public String getLstupdt() {
		return lstupdt;
	}

	public void setLstupdt(String lstupdt) {
		this.lstupdt = lstupdt;
	}

	public String getStjCd() {
		return stjCd;
	}

	public void setStjCd(String stjCd) {
		this.stjCd = stjCd;
	}

	public String getCtjCd() {
		return ctjCd;
	}

	public void setCtjCd(String ctjCd) {
		this.ctjCd = ctjCd;
	}

	public String getTradeNam() {
		return tradeNam;
	}

	public void setTradeNam(String tradeNam) {
		this.tradeNam = tradeNam;
	}

	public String getSumType() {
		return sumType;
	}

	public void setSumType(String sumType) {
		this.sumType = sumType;
	}

	public GSTR4TaxSummary getTaxPyPd() {
		return taxPyPd;
	}

	public void setTaxPyPd(GSTR4TaxSummary taxPyPd) {
		this.taxPyPd = taxPyPd;
	}

	public String getDbtId() {
		return dbtId;
	}

	public void setDbtId(String dbtId) {
		this.dbtId = dbtId;
	}

	public List<ResponseSummary> getSectionSummary() {
		return sectionSummary;
	}

	public void setSectionSummary(List<ResponseSummary> sectionSummary) {
		this.sectionSummary = sectionSummary;
	}

	public GSTR6ITCSummary getItcDetails() {
		return itcDetails;
	}

	public void setItcDetails(GSTR6ITCSummary itcDetails) {
		this.itcDetails = itcDetails;
	}

	public GSTR6LatefeeMain getLateFeemain() {
		return lateFeemain;
	}

	public void setLateFeemain(GSTR6LatefeeMain lateFeemain) {
		this.lateFeemain = lateFeemain;
	}
	
	public List<GSTINPublicAddress> getAdadr() {
		return adadr;
	}

	public void setAdadr(List<GSTINPublicAddress> adadr) {
		this.adadr = adadr;
	}

	public GSTINPublicAddress getPradr() {
		return pradr;
	}

	public void setPradr(GSTINPublicAddress pradr) {
		this.pradr = pradr;
	}
	
	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public GSTR3BAutoPopulateDetails getR3bautopop() {
		return r3bautopop;
	}

	public void setR3bautopop(GSTR3BAutoPopulateDetails r3bautopop) {
		this.r3bautopop = r3bautopop;
	}

	public String getGenerateSummary() {
		return generateSummary;
	}

	public void setGenerateSummary(String generateSummary) {
		this.generateSummary = generateSummary;
	}

	@Override
	public String toString() {
		return "ResponseData [referenceId=" + referenceId + ", statusCd=" + statusCd + ", retPeriod=" + retPeriod
				+ ", gstin=" + gstin + ", ctb=" + ctb + ", rgdt=" + rgdt + ", sts=" + sts + ", lgnm=" + lgnm + ", stj="
				+ stj + ", ctj=" + ctj + ", dty=" + dty + ", cxdt=" + cxdt + ", lstupdt=" + lstupdt + ", stjCd=" + stjCd
				+ ", ctjCd=" + ctjCd + ", tradeNam=" + tradeNam + ", nba=" + nba + ", adadr=" + adadr + ", pradr=" + pradr + ", chksum=" + chksum + ", sumType="
				+ sumType + ", dbtId=" + dbtId + ", secSum=" + secSum + ", sectionSummary=" + sectionSummary
				+ ", itcDetails=" + itcDetails + ", lateFeemain=" + lateFeemain + ", taxPyPd=" + taxPyPd
				+ ", errorReport=" + errorReport + ", hsn=" + hsn + ", supDetails=" + supDetails + ", interSup="
				+ interSup + ", itcElg=" + itcElg + ", inwardSup=" + inwardSup + ", intrLtfee=" + intrLtfee
				+ ", taxPymt=" + taxPymt + ", docIssue=" + docIssue + ", statusList=" + statusList + "]";
	}

}
