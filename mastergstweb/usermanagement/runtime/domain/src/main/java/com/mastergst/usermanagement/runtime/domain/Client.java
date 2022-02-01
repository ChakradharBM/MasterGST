/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.google.common.collect.Lists;
import com.mastergst.core.domain.Base;

/**
 * Client information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "client")
public class Client extends Base implements Serializable {

	private static final long serialVersionUID = 1L;
	private String firstname;
	private String lastname;
	private String clienttype;
	private String email;
	private Integer pincode;
	private String mobilenumber;
	private String businessEmail;
	private String businessMobilenumber;
	private String businessname;
	private String pannumber;
	private String gstnnumber;
	private String gstname;
	private Double turnover;
	private String dealertype;
	private String previousquarters;
	private Double disclousedturnover;
	private String prevoiusfillings;
	private String statename;
	private String status;
	private String contactperson;
	private String address;
	private String businessno;
	private String portalusername;
	private String portalpassword;
	private String signatoryName;
	private String signatoryPAN;
	private String active;
	private String gstsubmiton;
	private Integer mismatches;
	private String filingOption;
	private String groupName;
	private String logoid;
	private String signid;
	private String qrcodeid;
	private String driveid;
	private String notes;
	private String terms;
	private String reportView;
	private String prevPendingInv;
	private String authorisedSignatory;
	private boolean enableAuthorisedSignatory;
	private String configurefirm;
	private String reArrangeInvoiceNo;
	private boolean digitalSignOn;
	private String designation;
	private String reconcileDate;
	/*
	 * private Boolean isgroupexist; private Boolean iscommonledgerexist; private
	 * Boolean iscommonbankledgerexist; private Boolean iscommonotherledgerexist;
	 */
	private String cinNumber;
	private String lutNumber;
	//@DateTimeFormat(pattern = "dd/MM/yyyy")
	private String lutStartDate;
	//@DateTimeFormat(pattern = "dd/MM/yyyy")
	private String lutExpiryDate;
	private String msmeNo;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date journalEnteringDate;
	
	private List<PaymentTerms> paymentTerms = Lists.newArrayList();
	private List<Branch> branches = Lists.newArrayList();
	private List<CostCenter> costCenter = Lists.newArrayList();
	private List<Vertical> verticals = Lists.newArrayList();
	private List<GSTReturnSummary> returnsSummary = Lists.newArrayList();
	
	private List<FilingOptions> filingoptions=Lists.newArrayList();
	private List<TurnoverOptions> turnovergoptions=Lists.newArrayList();
	
	InvoiceCutomFields customField1 = new InvoiceCutomFields();
	InvoiceCutomFields customField2 = new InvoiceCutomFields();
	InvoiceCutomFields customField3 = new InvoiceCutomFields();
	InvoiceCutomFields customField4 = new InvoiceCutomFields();
	
	EwayBillCustomFields ewaycustomField1 = new EwayBillCustomFields();
	EwayBillCustomFields ewaycustomField2 = new EwayBillCustomFields();
	EwayBillCustomFields ewaycustomField3 = new EwayBillCustomFields();
	EwayBillCustomFields ewaycustomField4 = new EwayBillCustomFields();
	
	EinvoiceCustomFields einvcustomField1 = new EinvoiceCustomFields();
	EinvoiceCustomFields einvcustomField2 = new EinvoiceCustomFields();
	EinvoiceCustomFields einvcustomField3 = new EinvoiceCustomFields();
	EinvoiceCustomFields einvcustomField4 = new EinvoiceCustomFields();
	
	private String userid;
	private String cutOffDateForSales;
	private String cutOffDateForPurchases;
	
	private String invoiceViewOption;
	private String docId;
	
	private String clientSignature;
	private Boolean allowDiscount = true;
	private Boolean allowPurDiscount = true;
	private Boolean allowSalesCess = true;
	private Boolean allowPurCess = true;
	private Boolean allowEinvoiceCess = true;
	private Boolean allowLedgerName = true;
	private Boolean allowPurLedgerName = true;
	private Boolean allowExempted;
	private Boolean allowEinvDiscount = true;

	private Boolean isGroupExit;
	private Boolean isCommonLedgerExit;

	private String gstr2bReconcileDate;

	public List<TurnoverOptions> getTurnovergoptions() {
		return turnovergoptions;
	}

	public void setTurnovergoptions(List<TurnoverOptions> turnovergoptions) {
		this.turnovergoptions = turnovergoptions;
	}

	public List<FilingOptions> getFilingoptions() {
		return filingoptions;
	}

	public void setFilingoptions(List<FilingOptions> filingoptions) {
		this.filingoptions = filingoptions;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobilenumber() {
		return mobilenumber;
	}

	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}

	public String getBusinessname() {
		return businessname;
	}

	public void setBusinessname(String businessname) {
		this.businessname = businessname;
	}

	public String getPannumber() {
		return pannumber;
	}

	public void setPannumber(String pannumber) {
		this.pannumber = pannumber;
	}

	public String getGstnnumber() {
		return gstnnumber;
	}

	public void setGstnnumber(String gstnnumber) {
		this.gstnnumber = gstnnumber;
	}

	public Double getTurnover() {
		return turnover;
	}

	public void setTurnover(Double turnover) {
		this.turnover = turnover;
	}

	public String getDealertype() {
		return dealertype;
	}

	public void setDealertype(String dealertype) {
		this.dealertype = dealertype;
	}

	public String getPreviousquarters() {
		return previousquarters;
	}

	public void setPreviousquarters(String previousquarters) {
		this.previousquarters = previousquarters;
	}

	public Double getDisclousedturnover() {
		return disclousedturnover;
	}

	public void setDisclousedturnover(Double disclousedturnover) {
		this.disclousedturnover = disclousedturnover;
	}

	public String getPrevoiusfillings() {
		return prevoiusfillings;
	}

	public void setPrevoiusfillings(String prevoiusfillings) {
		this.prevoiusfillings = prevoiusfillings;
	}

	public String getStatename() {
		return statename;
	}

	public void setStatename(String statename) {
		this.statename = statename;
	}

	public String getGstname() {
		return gstname;
	}

	public void setGstname(String gstname) {
		this.gstname = gstname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContactperson() {
		return contactperson;
	}

	public void setContactperson(String contactperson) {
		this.contactperson = contactperson;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBusinessno() {
		return businessno;
	}

	public void setBusinessno(String businessno) {
		this.businessno = businessno;
	}

	public String getPortalusername() {
		return portalusername;
	}

	public void setPortalusername(String portalusername) {
		this.portalusername = portalusername;
	}

	public String getPortalpassword() {
		return portalpassword;
	}

	public void setPortalpassword(String portalpassword) {
		this.portalpassword = portalpassword;
	}

	public String getSignatoryName() {
		return signatoryName;
	}

	public void setSignatoryName(String signatoryName) {
		this.signatoryName = signatoryName;
	}

	public String getSignatoryPAN() {
		return signatoryPAN;
	}

	public void setSignatoryPAN(String signatoryPAN) {
		this.signatoryPAN = signatoryPAN;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getGstsubmiton() {
		return gstsubmiton;
	}

	public void setGstsubmiton(String gstsubmiton) {
		this.gstsubmiton = gstsubmiton;
	}

	public Integer getMismatches() {
		return mismatches;
	}

	public void setMismatches(Integer mismatches) {
		this.mismatches = mismatches;
	}

	public String getFilingOption() {
		return filingOption;
	}

	public void setFilingOption(String filingOption) {
		this.filingOption = filingOption;
	}

	public String getLogoid() {
		return logoid;
	}

	public void setLogoid(String logoid) {
		this.logoid = logoid;
	}

	public String getDriveid() {
		return driveid;
	}

	public void setDriveid(String driveid) {
		this.driveid = driveid;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}
	
	public List<Branch> getBranches() {
		return branches;
	}

	public void setBranches(List<Branch> branches) {
		this.branches = branches;
	}

	public List<Vertical> getVerticals() {
		return verticals;
	}

	public void setVerticals(List<Vertical> verticals) {
		this.verticals = verticals;
	}

	public List<GSTReturnSummary> getReturnsSummary() {
		return returnsSummary;
	}

	public void setReturnsSummary(List<GSTReturnSummary> returnsSummary) {
		this.returnsSummary = returnsSummary;
	}

	public String getReportView() {
		return reportView;
	}

	public void setReportView(String reportView) {
		this.reportView = reportView;
	}

	public String getBusinessMobilenumber() {
		return businessMobilenumber;
	}

	public void setBusinessMobilenumber(String businessMobilenumber) {
		this.businessMobilenumber = businessMobilenumber;
	}

	public String getBusinessEmail() {
		return businessEmail;
	}

	public void setBusinessEmail(String businessEmail) {
		this.businessEmail = businessEmail;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getAuthorisedSignatory() {
		return authorisedSignatory;
	}

	public void setAuthorisedSignatory(String authorisedSignatory) {
		this.authorisedSignatory = authorisedSignatory;
	}

	public boolean isEnableAuthorisedSignatory() {
		return enableAuthorisedSignatory;
	}

	public void setEnableAuthorisedSignatory(boolean enableAuthorisedSignatory) {
		this.enableAuthorisedSignatory = enableAuthorisedSignatory;
	}

	public String getPrevPendingInv() {
		return prevPendingInv;
	}

	public void setPrevPendingInv(String prevPendingInv) {
		this.prevPendingInv = prevPendingInv;
	}
	public String getConfigurefirm() {
		return configurefirm;
	}

	public void setConfigurefirm(String configurefirm) {
		this.configurefirm = configurefirm;
	}

	public String getReArrangeInvoiceNo() {
		return reArrangeInvoiceNo;
	}

	public void setReArrangeInvoiceNo(String reArrangeInvoiceNo) {
		this.reArrangeInvoiceNo = reArrangeInvoiceNo;
	}

	public String getSignid() {
		return signid;
	}

	public void setSignid(String signid) {
		this.signid = signid;
	}

	public boolean isDigitalSignOn() {
		return digitalSignOn;
	}

	public void setDigitalSignOn(boolean digitalSignOn) {
		this.digitalSignOn = digitalSignOn;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getReconcileDate() {
		return reconcileDate;
	}

	public void setReconcileDate(String reconcileDate) {
		this.reconcileDate = reconcileDate;
	}

	/*public Boolean getIsgroupexist() {
		return isgroupexist;
	}

	public void setIsgroupexist(Boolean isgroupexist) {
		this.isgroupexist = isgroupexist;
	}

	public Boolean getIscommonledgerexist() {
		return iscommonledgerexist;
	}

	public void setIscommonledgerexist(Boolean iscommonledgerexist) {
		this.iscommonledgerexist = iscommonledgerexist;
	}

	public Boolean getIscommonbankledgerexist() {
		return iscommonbankledgerexist;
	}

	public void setIscommonbankledgerexist(Boolean iscommonbankledgerexist) {
		this.iscommonbankledgerexist = iscommonbankledgerexist;
	}*/

	public InvoiceCutomFields getCustomField1() {
		return customField1;
	}

	public void setCustomField1(InvoiceCutomFields customField1) {
		this.customField1 = customField1;
	}

	public InvoiceCutomFields getCustomField2() {
		return customField2;
	}

	public void setCustomField2(InvoiceCutomFields customField2) {
		this.customField2 = customField2;
	}

	public InvoiceCutomFields getCustomField3() {
		return customField3;
	}

	public void setCustomField3(InvoiceCutomFields customField3) {
		this.customField3 = customField3;
	}

	public InvoiceCutomFields getCustomField4() {
		return customField4;
	}

	public void setCustomField4(InvoiceCutomFields customField4) {
		this.customField4 = customField4;
	}
	
	public String getCinNumber() {
		return cinNumber;
	}

	public void setCinNumber(String cinNumber) {
		this.cinNumber = cinNumber;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
public String getLutNumber() {
		return lutNumber;
	}

	public void setLutNumber(String lutNumber) {
		this.lutNumber = lutNumber;
	}

	public String getLutStartDate() {
		return lutStartDate;
	}

	public void setLutStartDate(String lutStartDate) {
		this.lutStartDate = lutStartDate;
	}

	public String getLutExpiryDate() {
		return lutExpiryDate;
	}

	public void setLutExpiryDate(String lutExpiryDate) {
		this.lutExpiryDate = lutExpiryDate;
	}

	public String getMsmeNo() {
		return msmeNo;
	}

	public void setMsmeNo(String msmeNo) {
		this.msmeNo = msmeNo;
	}

	public Date getJournalEnteringDate() {
		return journalEnteringDate;
	}

	public void setJournalEnteringDate(Date journalEnteringDate) {
		this.journalEnteringDate = journalEnteringDate;
	}

	/*public Boolean getIscommonotherledgerexist() {
		return iscommonotherledgerexist;
	}

	public void setIscommonotherledgerexist(Boolean iscommonotherledgerexist) {
		this.iscommonotherledgerexist = iscommonotherledgerexist;
	}*/
	public Integer getPincode() {
		return pincode;
	}
	public void setPincode(Integer pincode) {
		this.pincode = pincode;
	}

	public String getCutOffDateForSales() {
		return cutOffDateForSales;
	}

	public void setCutOffDateForSales(String cutOffDateForSales) {
		this.cutOffDateForSales = cutOffDateForSales;
	}

	public String getCutOffDateForPurchases() {
		return cutOffDateForPurchases;
	}

	public void setCutOffDateForPurchases(String cutOffDateForPurchases) {
		this.cutOffDateForPurchases = cutOffDateForPurchases;
	}

	public String getClienttype() {
		return clienttype;
	}

	public void setClienttype(String clienttype) {
		this.clienttype = clienttype;
	}

	public String getInvoiceViewOption() {
		return invoiceViewOption;
	}

	public void setInvoiceViewOption(String invoiceViewOption) {
		this.invoiceViewOption = invoiceViewOption;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public EwayBillCustomFields getEwaycustomField1() {
		return ewaycustomField1;
	}

	public void setEwaycustomField1(EwayBillCustomFields ewaycustomField1) {
		this.ewaycustomField1 = ewaycustomField1;
	}

	public EwayBillCustomFields getEwaycustomField2() {
		return ewaycustomField2;
	}

	public void setEwaycustomField2(EwayBillCustomFields ewaycustomField2) {
		this.ewaycustomField2 = ewaycustomField2;
	}

	public EwayBillCustomFields getEwaycustomField3() {
		return ewaycustomField3;
	}

	public void setEwaycustomField3(EwayBillCustomFields ewaycustomField3) {
		this.ewaycustomField3 = ewaycustomField3;
	}

	public EwayBillCustomFields getEwaycustomField4() {
		return ewaycustomField4;
	}

	public void setEwaycustomField4(EwayBillCustomFields ewaycustomField4) {
		this.ewaycustomField4 = ewaycustomField4;
	}

	public EinvoiceCustomFields getEinvcustomField1() {
		return einvcustomField1;
	}

	public void setEinvcustomField1(EinvoiceCustomFields einvcustomField1) {
		this.einvcustomField1 = einvcustomField1;
	}

	public EinvoiceCustomFields getEinvcustomField2() {
		return einvcustomField2;
	}

	public void setEinvcustomField2(EinvoiceCustomFields einvcustomField2) {
		this.einvcustomField2 = einvcustomField2;
	}

	public EinvoiceCustomFields getEinvcustomField3() {
		return einvcustomField3;
	}

	public void setEinvcustomField3(EinvoiceCustomFields einvcustomField3) {
		this.einvcustomField3 = einvcustomField3;
	}

	public EinvoiceCustomFields getEinvcustomField4() {
		return einvcustomField4;
	}

	public void setEinvcustomField4(EinvoiceCustomFields einvcustomField4) {
		this.einvcustomField4 = einvcustomField4;
	}

	public String getQrcodeid() {
		return qrcodeid;
	}

	public void setQrcodeid(String qrcodeid) {
		this.qrcodeid = qrcodeid;
	}
	public Boolean getAllowDiscount() {
		return allowDiscount;
	}
	public void setAllowDiscount(Boolean allowDiscount) {
		this.allowDiscount = allowDiscount;
	}
	public Boolean getAllowPurDiscount() {
		return allowPurDiscount;
	}

	public void setAllowPurDiscount(Boolean allowPurDiscount) {
		this.allowPurDiscount = allowPurDiscount;
	}

	public Boolean getAllowExempted() {
		return allowExempted;
	}
	public void setAllowExempted(Boolean allowExempted) {
		this.allowExempted = allowExempted;
	}

	public Boolean getAllowLedgerName() {
		return allowLedgerName;
	}

	public void setAllowLedgerName(Boolean allowLedgerName) {
		this.allowLedgerName = allowLedgerName;
	}
	
	public Boolean getAllowEinvDiscount() {
		return allowEinvDiscount;
	}

	public void setAllowEinvDiscount(Boolean allowEinvDiscount) {
		this.allowEinvDiscount = allowEinvDiscount;
	}

	public List<PaymentTerms> getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(List<PaymentTerms> paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	public String getClientSignature() {
		return clientSignature;
	}

	public void setClientSignature(String clientSignature) {
		this.clientSignature = clientSignature;
	}
	
	public String getGstr2bReconcileDate() {
		return gstr2bReconcileDate;
	}

	public void setGstr2bReconcileDate(String gstr2bReconcileDate) {
		this.gstr2bReconcileDate = gstr2bReconcileDate;
	}

	public List<CostCenter> getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(List<CostCenter> costCenter) {
		this.costCenter = costCenter;
	}

	public Boolean getAllowSalesCess() {
		return allowSalesCess;
	}

	public void setAllowSalesCess(Boolean allowSalesCess) {
		this.allowSalesCess = allowSalesCess;
	}

	public Boolean getAllowPurCess() {
		return allowPurCess;
	}

	public void setAllowPurCess(Boolean allowPurCess) {
		this.allowPurCess = allowPurCess;
	}

	public Boolean getAllowEinvoiceCess() {
		return allowEinvoiceCess;
	}

	public void setAllowEinvoiceCess(Boolean allowEinvoiceCess) {
		this.allowEinvoiceCess = allowEinvoiceCess;
	}

	public Boolean getAllowPurLedgerName() {
		return allowPurLedgerName;
	}

	public void setAllowPurLedgerName(Boolean allowPurLedgerName) {
		this.allowPurLedgerName = allowPurLedgerName;
	}

	public Boolean getIsGroupExit() {
		return isGroupExit;
	}

	public void setIsGroupExit(Boolean isGroupExit) {
		this.isGroupExit = isGroupExit;
	}

	public Boolean getIsCommonLedgerExit() {
		return isCommonLedgerExit;
	}

	public void setIsCommonLedgerExit(Boolean isCommonLedgerExit) {
		this.isCommonLedgerExit = isCommonLedgerExit;
	}
}
