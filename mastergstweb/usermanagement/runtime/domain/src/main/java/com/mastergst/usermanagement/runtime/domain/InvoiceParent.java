/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.annotation.Transient;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.domain.Base;
import com.mastergst.core.util.CustomDoubleSerializer;
import com.mastergst.usermanagement.runtime.domain.ewaybill.ExtendValidityDO;

/**
 * Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InvoiceParent extends Base {
	String docKey;
	String docId;
	List<String> amendmentRefId;
	String invoiceEcomOperator;
	String invoiceEcomGSTIN;
	String invoiceCustomerId;
	String customerEmail;
	String govtInvoiceStatus;
	String companyDBId;
	String userid;
	String fullname;
	String clientid;
	String s3attachment;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	Date s3attachementDate;
	String gstin;
	String fp;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double gt;
	@JsonProperty("cur_gt")
	Double curGt;
	List<GSTRB2B> b2b = LazyList.decorate(new ArrayList<GSTRB2B>(), FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTRB2CL> b2cl = LazyList.decorate(new ArrayList<GSTRB2CL>(), FactoryUtils.instantiateFactory(GSTRB2CL.class));
	List<GSTRB2CS> b2cs = LazyList.decorate(new ArrayList<GSTRB2CS>(), FactoryUtils.instantiateFactory(GSTRB2CS.class));
	List<GSTRExports> exp = LazyList.decorate(new ArrayList<GSTRExports>(),
			FactoryUtils.instantiateFactory(GSTRExports.class));
	List<GSTRAdvanceTax> txpd = LazyList.decorate(new ArrayList<GSTRAdvanceTax>(),
			FactoryUtils.instantiateFactory(GSTRAdvanceTax.class));
	List<GSTRAdvanceTax> txpda = LazyList.decorate(new ArrayList<GSTRAdvanceTax>(),
			FactoryUtils.instantiateFactory(GSTRAdvanceTax.class));
	List<GSTRInvoiceDetails> cdnur = LazyList.decorate(new ArrayList<GSTRInvoiceDetails>(),
			FactoryUtils.instantiateFactory(GSTRInvoiceDetails.class));
	List<GSTRCreditDebitNotes> cdn = LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(),
			FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));
	HSNSummary hsn;
	List<HSNSummary> hsnsum;
	GSTRNilInvoices nil=new GSTRNilInvoices();
	ExtendValidityDO extendValidity;
	
	/* For E-invoice Start*/
	@JsonProperty("DispDtls")
	DispatcherDetails dispatcherDtls= null;
	@JsonProperty("ShipDtls")
	ShipmentDetails shipmentDtls = null;
	@JsonProperty("DocDtls")
	DocDetails docDtls = new DocDetails();
	@JsonProperty("TranDtls")
	TransportDetails tranDtls = new TransportDetails();
	@JsonProperty("SellerDtls")
	SellerDetails sellerDtls = new SellerDetails();
	@JsonProperty("BuyerDtls")
	BuyerDetails buyerDtls = new BuyerDetails();
	@JsonProperty("ValDtls")
	ValueDetails valDtls = new ValueDetails();
	//@JsonProperty("BchDtls")
	//BatchDetails bchDtls;// = new BatchDetails();
	@JsonProperty("PayDtls")
	PayeeDetails payDtls;// = new PayeeDetails();
	@JsonProperty("RefDtls")
	ReferenceDetails refDtls;// = new ReferenceDetails();
	@JsonProperty("AddlDocDtls")
	List<AddlDocDetails> addlDocDtls;// = new AddlDocDetails();
	@JsonProperty("ExpDtls")
	ExportDetails expDtls = new ExportDetails();
	@JsonProperty("EwbDtls")
	EwayBillDetails ewbDtls;// = new EwayBillDetails();
	@JsonProperty("ItemList")
	List<EinvoiceItem> itemList = LazyList.decorate(new ArrayList<EinvoiceItem>(), FactoryUtils.instantiateFactory(EinvoiceItem.class));
	@JsonProperty("IgstOnIntra")
	private String igstOnIntra;
	private String signedQrCode;
	private String signedInvoice;
	private String ackNo;
	private String einvStatus;
	private String ackDt;
	/*For E-invoice End */
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	Date dateofinvoice;
	String paymentStatus;
	String lutNo;
	String invoiceno;
	String statename;
	String invtype;
	String revchargetype;
	String revchargeNo;
	String billedtoname;
	String isbilledto;
	String consigneename;
	String consigneepos;
	String consigneeaddress;
	String ecomoperatorid;
	String recipientstatecode;
	Double totaltaxableamount;
	Double totalamount;
	Double totalamountforbillofsupplycdn;
	String cdnnilsupplies;
	Double originalinvamount;
	Double roundOffAmount;
	Double notroundoftotalamount;

	Double totaltax;
	Double totalitc;
	String itcClaimedDate;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	Date dateofitcClaimed;
	Date dateofitcCleared;
	
	Number eBillNo;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	Date eBillDate;
	String generateMode;
	String gstStatus;
	String gstRefId;
	String matchingId;
	String matchingStatus;
	String strDate;
	String strOdate;
	String referenceNumber;
	String ewayBillNumber;
	String branch;
	String vertical;
	String includetax;
	String cessType;
	String invoiceLevelCess;
	
	String strAmendment;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	Date expiryDate;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	Date deliveryDate;
	boolean isAmendment=false;
	private String notes;
	private String terms;
	private String termDays;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	Date dueDate;
	
	String premium;
	String period;
	
	String transactionDate;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	Date billDate;
	
	@JsonProperty("error_msg")
	String errorMsg;
	String diffPercent;
	
	Double advRemainingAmount;
	String advOriginalInvoiceNumber;
	String advPCustname;
	Double advPInvamt;
	Double advPIgstamt;
	Double advPCgstamt;
	Double advPSgstamt;
	
	String ledgerName;
	
	//String customerOrSupplierLedgerName;
	String vendorName;
	
	String dealerType;
	
	boolean tdstcsenable = false;
	String section;
	Double tcstdspercentage;
	Double tcstdsAmount;
	Double netAmount;
	
	boolean tdsenable = false;
	String tdsSection;
	Double tdspercentage;
	Double tdsAmount;
	//Double tdsNetAmount;
	
	String tcsorTdsType;
	
	String customField1;
	String customField2;
	String customField3;
	String customField4;
	String customFieldText1;
	String customFieldText2;
	String customFieldText3;
	String customFieldText4;
	
	String clientAddress;
	
	String fromPin;
	String toPin;
	String transType;
	String strTransDate;
	String supplyType;
	String subSupplyType;
	String subSupplyDesc;
	String docType;
	String fromGstin;
	String fromTrdName;
	String fromAddr1;
	String fromAddr2;
	String fromPlace;
	Number fromPincode;
	Number fromStateCode;
	String toGstin;
	String toTrdName;
	String toAddr1;
	String toAddr2;
	String toPlace;
	Number toPincode;
	Number toStateCode;
	String transDistance;
	Number actFromStateCode;
	Number actToStateCode;
	
	
	String transporterId;
	String transporterName;
	String status;
	Number actualDist;
	Number noValidDays;
	String validUpto;
	boolean ebillValidator;
	String ewaybillRejectDate;
	Number extendedTimes;
	String rejectStatus;
	String vehicleType;
	Number transactionType;
	Double otherValue;
	Number cessNonAdvolValue;
	Double taxRate;
	
	/*E-invoice Fields start */
	String eCommSupplyType;
	String addressType;
	Integer buyerPincode;
	String einvCategory;
	String einvExpCategory;
	String countryCode;
	String irnNo;
	String irnStatus;
	String gstr1orEinvoice;
	
	@JsonProperty("Version")
	String version;
	@JsonProperty("Typ")
	String typ;
	CompanyBankDetails bankDetails = new CompanyBankDetails();
	List<Item> items = LazyList.decorate(new ArrayList<Item>(), 
			FactoryUtils.instantiateFactory(Item.class));
	List<EBillVehicleListDetails> VehiclListDetails=LazyList.decorate(new ArrayList<EBillVehicleListDetails>(), 
			FactoryUtils.instantiateFactory(EBillVehicleListDetails.class));
	/* Send Mail Fields Start*/
	String customerMailIds;
	String customerCCMailIds;
	String mailSubject;
	String mailMessage;
	Boolean isIncludeSignature;
	/* Send Mail Fields End*/
	Double totalIgstAmount;
	Double totalCgstAmount;
	Double totalSgstAmount;
	Double totalCessAmount;
	Double totalExemptedAmount;
	Double totalStateCessAmount;
	Double totalAssAmount;
	Double totalCessNonAdVal;
	Double totalDiscountAmount;
	Double totalOthrChrgeAmount;
	
	String cancelebillcmnts;
	String mannualMatchInvoices;
	String mthCd;
	String qrtCd;
	String yrCd;
	String trDatemthCd;
	String trDateqrtCd;
	String trDateyrCd;
	int sftr;
	int csftr;
	String categorytype;
	String totaltaxableamount_str;
	String totaltax_str;
	String totalamount_str;
	String dateofinvoice_str;
	String ewayBillDate_str;
	String addcurrencyCode;
	String exchangeRate;
	Double totalCurrencyAmount;
	
	String bankname;
	String accountnumber;
	String accountname;
	String branchname;
	String ifsccode;
	
	String dispatchname;
	String dispatchAddr1;
	String dispatchAddr2;
	String dispatchLoc;
	String dispatchState;
	int dispatchpin;
	
	String shipmentGstin;
	String shipmentTrdnm;
	String shipmentLgnm;
	String shipmentAddr1;
	String shipmentAddr2;
	String shipmentLoc;
	String shipmentState;
	int shipmentpin;
	String itcavl;
	String samebilladdress;
	
	Double pendingAmount;
	Double receivedAmount;
	
	boolean advTaxableType;
	String convertedtoinv;
	String printerintra;
	private List<String> gstr2bMatchingId;
	private String gstr2bMatchingStatus;
	private String gstr2bMatchingRsn;
	
	String srctype;
	
	String entertaimentprintto;
	
	GSTR1DocumentIssue docuploads= new GSTR1DocumentIssue();
	
	
	
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
	public String getGstin() {
		return gstin;
	}
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}
	public String getFp() {
		return fp;
	}
	public void setFp(String fp) {
		this.fp = fp;
	}
	public Double getGt() {
		return gt;
	}
	public void setGt(Double gt) {
		this.gt = gt;
	}
	public Double getCurGt() {
		return curGt;
	}
	public void setCurGt(Double curGt) {
		this.curGt = curGt;
	}
	public List<GSTRB2B> getB2b() {
		return b2b;
	}
	public void setB2b(List<GSTRB2B> b2b) {
		this.b2b = b2b;
	}
	public List<GSTRB2CL> getB2cl() {
		return b2cl;
	}
	public void setB2cl(List<GSTRB2CL> b2cl) {
		this.b2cl = b2cl;
	}
	public List<GSTRB2CS> getB2cs() {
		return b2cs;
	}
	public void setB2cs(List<GSTRB2CS> b2cs) {
		this.b2cs = b2cs;
	}
	public List<GSTRExports> getExp() {
		return exp;
	}
	public void setExp(List<GSTRExports> exp) {
		this.exp = exp;
	}
	public List<GSTRAdvanceTax> getTxpd() {
		return txpd;
	}
	public void setTxpd(List<GSTRAdvanceTax> txpd) {
		this.txpd = txpd;
	}
	public List<GSTRInvoiceDetails> getCdnur() {
		return cdnur;
	}
	public void setCdnur(List<GSTRInvoiceDetails> cdnur) {
		this.cdnur = cdnur;
	}
	public List<GSTRCreditDebitNotes> getCdn() {
		return cdn;
	}
	public void setCdn(List<GSTRCreditDebitNotes> cdn) {
		this.cdn = cdn;
	}
	public HSNSummary getHsn() {
		return hsn;
	}
	public void setHsn(HSNSummary hsn) {
		this.hsn = hsn;
	}
	public GSTRNilInvoices getNil() {
		return nil;
	}
	public void setNil(GSTRNilInvoices nil) {
		this.nil = nil;
	}
	public Date getDateofinvoice() {
		return dateofinvoice;
	}
	public void setDateofinvoice(Date dateofinvoice) {
		this.dateofinvoice = dateofinvoice;
	}
	public String getInvoiceno() {
		return invoiceno;
	}
	public void setInvoiceno(String invoiceno) {
		this.invoiceno = invoiceno;
	}
	public String getStatename() {
		return statename;
	}
	public void setStatename(String statename) {
		this.statename = statename;
	}
	public String getInvtype() {
		return invtype;
	}
	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}
	public String getRevchargetype() {
		return revchargetype;
	}
	public void setRevchargetype(String revchargetype) {
		this.revchargetype = revchargetype;
	}
	public String getBilledtoname() {
		return billedtoname;
	}
	public void setBilledtoname(String billedtoname) {
		this.billedtoname = billedtoname;
	}
	public String getIsbilledto() {
		return isbilledto;
	}
	public void setIsbilledto(String isbilledto) {
		this.isbilledto = isbilledto;
	}
	public String getConsigneename() {
		return consigneename;
	}
	public void setConsigneename(String consigneename) {
		this.consigneename = consigneename;
	}
	public String getConsigneepos() {
		return consigneepos;
	}
	public void setConsigneepos(String consigneepos) {
		this.consigneepos = consigneepos;
	}
	public String getConsigneeaddress() {
		return consigneeaddress;
	}
	public void setConsigneeaddress(String consigneeaddress) {
		this.consigneeaddress = consigneeaddress;
	}
	public String getEcomoperatorid() {
		return ecomoperatorid;
	}
	public void setEcomoperatorid(String ecomoperatorid) {
		this.ecomoperatorid = ecomoperatorid;
	}
	public String getRecipientstatecode() {
		return recipientstatecode;
	}
	public void setRecipientstatecode(String recipientstatecode) {
		this.recipientstatecode = recipientstatecode;
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
	public Double getTotalitc() {
		return totalitc;
	}
	public void setTotalitc(Double totalitc) {
		this.totalitc = totalitc;
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
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public CompanyBankDetails getBankDetails() {
		return bankDetails;
	}
	public void setBankDetails(CompanyBankDetails bankDetails) {
		this.bankDetails = bankDetails;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
		
	public String getStrOdate() {
		return strOdate;
	}
	public void setStrOdate(String strOdate) {
		this.strOdate = strOdate;
	}
	public String getStrDate() {
		return strDate;
	}
	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}
	
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getVertical() {
		return vertical;
	}
	public void setVertical(String vertical) {
		this.vertical = vertical;
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
	public String getEwayBillNumber() {
		return ewayBillNumber;
	}
	public void setEwayBillNumber(String ewayBillNumber) {
		this.ewayBillNumber = ewayBillNumber;
	}
	public boolean isAmendment() {
		return isAmendment;
	}
	public void setAmendment(boolean isAmendment) {
		this.isAmendment = isAmendment;
	}
	public String getStrAmendment() {
		return strAmendment;
	}
	public void setStrAmendment(String strAmendment) {
		this.strAmendment = strAmendment;
	}
	public Date getDateofitcClaimed() {
		return dateofitcClaimed;
	}
	public void setDateofitcClaimed(Date dateofitcClaimed) {
		this.dateofitcClaimed = dateofitcClaimed;
	}
	public String getInvoiceCustomerId() {
		return invoiceCustomerId;
	}
	public void setInvoiceCustomerId(String invoiceCustomerId) {
		this.invoiceCustomerId = invoiceCustomerId;
	}
	public String getDiffPercent() {
		return diffPercent;
	}
	public void setDiffPercent(String diffPercent) {
		this.diffPercent = diffPercent;
	}

	public Double getRoundOffAmount() {
		return roundOffAmount;
	}

	public void setRoundOffAmount(Double roundOffAmount) {
		this.roundOffAmount = roundOffAmount;
	}

	public String getCompanyDBId() {
		return companyDBId;
	}

	public void setCompanyDBId(String companyDBId) {
		this.companyDBId = companyDBId;
	}

	public Double getNotroundoftotalamount() {
		return notroundoftotalamount;
	}

	public void setNotroundoftotalamount(Double notroundoftotalamount) {
		this.notroundoftotalamount = notroundoftotalamount;
	}
	public Date getBillDate() {
		return billDate;
	}
	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}	

	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	

	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public String getIncludetax() {
		return includetax;
	}
	public void setIncludetax(String includetax) {
		this.includetax = includetax;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public Double getAdvRemainingAmount() {
		return advRemainingAmount;
	}
	public void setAdvRemainingAmount(Double advRemainingAmount) {
		this.advRemainingAmount = advRemainingAmount;
	}
	public String getAdvOriginalInvoiceNumber() {
		return advOriginalInvoiceNumber;
	}
	public void setAdvOriginalInvoiceNumber(String advOriginalInvoiceNumber) {
		this.advOriginalInvoiceNumber = advOriginalInvoiceNumber;
	}
	public String getAdvPCustname() {
		return advPCustname;
	}
	public void setAdvPCustname(String advPCustname) {
		this.advPCustname = advPCustname;
	}
	public Double getAdvPInvamt() {
		return advPInvamt;
	}
	public void setAdvPInvamt(Double advPInvamt) {
		this.advPInvamt = advPInvamt;
	}
	public Double getAdvPIgstamt() {
		return advPIgstamt;
	}
	public void setAdvPIgstamt(Double advPIgstamt) {
		this.advPIgstamt = advPIgstamt;
	}
	public Double getAdvPCgstamt() {
		return advPCgstamt;
	}
	public void setAdvPCgstamt(Double advPCgstamt) {
		this.advPCgstamt = advPCgstamt;
	}
	public Double getAdvPSgstamt() {
		return advPSgstamt;
	}
	public void setAdvPSgstamt(Double advPSgstamt) {
		this.advPSgstamt = advPSgstamt;
	}
	
	public String getGovtInvoiceStatus() {
		return govtInvoiceStatus;
	}
	public void setGovtInvoiceStatus(String govtInvoiceStatus) {
		this.govtInvoiceStatus = govtInvoiceStatus;
	}
	
	public Date getDateofitcCleared() {
		return dateofitcCleared;
	}
	public void setDateofitcCleared(Date dateofitcCleared) {
		this.dateofitcCleared = dateofitcCleared;
	}
	public String getLedgerName() {
		return ledgerName;
	}
	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}

	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	/*
	 * public String getCustomerOrSupplierLedgerName() { return
	 * customerOrSupplierLedgerName; } public void
	 * setCustomerOrSupplierLedgerName(String customerOrSupplierLedgerName) {
	 * this.customerOrSupplierLedgerName = customerOrSupplierLedgerName; }
	 */
	public String getDealerType() {
		return dealerType;
	}
	public void setDealerType(String dealerType) {
		this.dealerType = dealerType;
	}
	public boolean isTdstcsenable() {
		return tdstcsenable;
	}
	public void setTdstcsenable(boolean tdstcsenable) {
		this.tdstcsenable = tdstcsenable;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public Double getTcstdspercentage() {
		return tcstdspercentage;
	}
	public void setTcstdspercentage(Double tcstdspercentage) {
		this.tcstdspercentage = tcstdspercentage;
	}
	public Double getTcstdsAmount() {
		return tcstdsAmount;
	}
	public void setTcstdsAmount(Double tcstdsAmount) {
		this.tcstdsAmount = tcstdsAmount;
	}
	public Double getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(Double netAmount) {
		this.netAmount = netAmount;
	}
	public String getInvoiceEcomOperator() {
		return invoiceEcomOperator;
	}
	public void setInvoiceEcomOperator(String invoiceEcomOperator) {
		this.invoiceEcomOperator = invoiceEcomOperator;
	}
	public String getInvoiceEcomGSTIN() {
		return invoiceEcomGSTIN;
	}
	public void setInvoiceEcomGSTIN(String invoiceEcomGSTIN) {
		this.invoiceEcomGSTIN = invoiceEcomGSTIN;
	}
	public String getCustomField1() {
		return customField1;
	}
	public void setCustomField1(String customField1) {
		this.customField1 = customField1;
	}
	public String getCustomField2() {
		return customField2;
	}
	public void setCustomField2(String customField2) {
		this.customField2 = customField2;
	}
	public String getCustomField3() {
		return customField3;
	}
	public void setCustomField3(String customField3) {
		this.customField3 = customField3;
	}
	public String getCustomField4() {
		return customField4;
	}
	public void setCustomField4(String customField4) {
		this.customField4 = customField4;
	}
	public String getClientAddress() {
		return clientAddress;
	}
	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}
	public String getItcClaimedDate() {
		return itcClaimedDate;
	}
	public void setItcClaimedDate(String itcClaimedDate) {
		this.itcClaimedDate = itcClaimedDate;
	}
	public Double getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}
	public String getGenerateMode() {
		return generateMode;
	}
	public void setGenerateMode(String generateMode) {
		this.generateMode = generateMode;
	}
	public Number geteBillNo() {
		return eBillNo;
	}
	public void seteBillNo(Number eBillNo) {
		this.eBillNo = eBillNo;
	}
	public Date geteBillDate() {
		return eBillDate;
	}
	public void seteBillDate(Date eBillDate) {
		this.eBillDate = eBillDate;
	}
	public String getSupplyType() {
		return supplyType;
	}
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}
	public String getSubSupplyType() {
		return subSupplyType;
	}
	public void setSubSupplyType(String subSupplyType) {
		this.subSupplyType = subSupplyType;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getTransporterId() {
		return transporterId;
	}
	public void setTransporterId(String transporterId) {
		this.transporterId = transporterId;
	}
	public String getTransporterName() {
		return transporterName;
	}
	public void setTransporterName(String transporterName) {
		this.transporterName = transporterName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Number getActualDist() {
		return actualDist;
	}
	public void setActualDist(Number actualDist) {
		this.actualDist = actualDist;
	}
	public Number getNoValidDays() {
		return noValidDays;
	}
	public void setNoValidDays(Number noValidDays) {
		this.noValidDays = noValidDays;
	}
	public String getValidUpto() {
		return validUpto;
	}
	public void setValidUpto(String validUpto) {
		this.validUpto = validUpto;
	}
	public Number getExtendedTimes() {
		return extendedTimes;
	}
	public void setExtendedTimes(Number extendedTimes) {
		this.extendedTimes = extendedTimes;
	}
	public String getRejectStatus() {
		return rejectStatus;
	}
	public void setRejectStatus(String rejectStatus) {
		this.rejectStatus = rejectStatus;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public Number getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(Number transactionType) {
		this.transactionType = transactionType;
	}
	public Double getOtherValue() {
		return otherValue;
	}
	public void setOtherValue(Double otherValue) {
		this.otherValue = otherValue;
	}
	public Number getCessNonAdvolValue() {
		return cessNonAdvolValue;
	}
	public void setCessNonAdvolValue(Number cessNonAdvolValue) {
		this.cessNonAdvolValue = cessNonAdvolValue;
	}
	public String getFromGstin() {
		return fromGstin;
	}
	public void setFromGstin(String fromGstin) {
		this.fromGstin = fromGstin;
	}
	public String getFromTrdName() {
		return fromTrdName;
	}
	public void setFromTrdName(String fromTrdName) {
		this.fromTrdName = fromTrdName;
	}
	public String getFromAddr1() {
		return fromAddr1;
	}
	public void setFromAddr1(String fromAddr1) {
		this.fromAddr1 = fromAddr1;
	}
	public String getFromAddr2() {
		return fromAddr2;
	}
	public void setFromAddr2(String fromAddr2) {
		this.fromAddr2 = fromAddr2;
	}
	public String getFromPlace() {
		return fromPlace;
	}
	public void setFromPlace(String fromPlace) {
		this.fromPlace = fromPlace;
	}
	public Number getFromPincode() {
		return fromPincode;
	}
	public void setFromPincode(Number fromPincode) {
		this.fromPincode = fromPincode;
	}
	public Number getFromStateCode() {
		return fromStateCode;
	}
	public void setFromStateCode(Number fromStateCode) {
		this.fromStateCode = fromStateCode;
	}
	public String getToGstin() {
		return toGstin;
	}
	public void setToGstin(String toGstin) {
		this.toGstin = toGstin;
	}
	public String getToTrdName() {
		return toTrdName;
	}
	public void setToTrdName(String toTrdName) {
		this.toTrdName = toTrdName;
	}
	public String getToAddr1() {
		return toAddr1;
	}
	public void setToAddr1(String toAddr1) {
		this.toAddr1 = toAddr1;
	}
	public String getToAddr2() {
		return toAddr2;
	}
	public void setToAddr2(String toAddr2) {
		this.toAddr2 = toAddr2;
	}
	public String getToPlace() {
		return toPlace;
	}
	public void setToPlace(String toPlace) {
		this.toPlace = toPlace;
	}
	public Number getToPincode() {
		return toPincode;
	}
	public void setToPincode(Number toPincode) {
		this.toPincode = toPincode;
	}
	public Number getToStateCode() {
		return toStateCode;
	}
	public void setToStateCode(Number toStateCode) {
		this.toStateCode = toStateCode;
	}
	
	public List<EBillVehicleListDetails> getVehiclListDetails() {
		return VehiclListDetails;
	}
	public void setVehiclListDetails(List<EBillVehicleListDetails> vehiclListDetails) {
		VehiclListDetails = vehiclListDetails;
	}
	public String getLutNo() {
		return lutNo;
	}
	public void setLutNo(String lutNo) {
		this.lutNo = lutNo;
	}
	
	
	public Double getTotalIgstAmount() {
		return totalIgstAmount;
	}
	public void setTotalIgstAmount(Double totalIgstAmount) {
		this.totalIgstAmount = totalIgstAmount;
	}
	public Double getTotalCgstAmount() {
		return totalCgstAmount;
	}
	public void setTotalCgstAmount(Double totalCgstAmount) {
		this.totalCgstAmount = totalCgstAmount;
	}
	public Double getTotalSgstAmount() {
		return totalSgstAmount;
	}
	public void setTotalSgstAmount(Double totalSgstAmount) {
		this.totalSgstAmount = totalSgstAmount;
	}
	public Double getTotalCessAmount() {
		return totalCessAmount;
	}
	public void setTotalCessAmount(Double totalCessAmount) {
		this.totalCessAmount = totalCessAmount;
	}
	public Double getTotalExemptedAmount() {
		return totalExemptedAmount;
	}
	public void setTotalExemptedAmount(Double totalExemptedAmount) {
		this.totalExemptedAmount = totalExemptedAmount;
	}
	public String getTransDistance() {
		return transDistance;
	}
	public void setTransDistance(String transDistance) {
		this.transDistance = transDistance;
	}
	public Number getActFromStateCode() {
		return actFromStateCode;
	}
	public void setActFromStateCode(Number actFromStateCode) {
		this.actFromStateCode = actFromStateCode;
	}
	public Number getActToStateCode() {
		return actToStateCode;
	}
	public void setActToStateCode(Number actToStateCode) {
		this.actToStateCode = actToStateCode;
	}
	public String getCancelebillcmnts() {
		return cancelebillcmnts;
	}
	public void setCancelebillcmnts(String cancelebillcmnts) {
		this.cancelebillcmnts = cancelebillcmnts;
	}
	public String getMannualMatchInvoices() {
		return mannualMatchInvoices;
	}
	public void setMannualMatchInvoices(String mannualMatchInvoices) {
		this.mannualMatchInvoices = mannualMatchInvoices;
	}
	public String getCategorytype() {
		return categorytype;
	}
	public void setCategorytype(String categorytype) {
		this.categorytype = categorytype;
	}
	public List<HSNSummary> getHsnsum() {
		return hsnsum;
	}
	public void setHsnsum(List<HSNSummary> hsnsum) {
		this.hsnsum = hsnsum;
	}
	public String geteCommSupplyType() {
		return eCommSupplyType;
	}
	public void seteCommSupplyType(String eCommSupplyType) {
		this.eCommSupplyType = eCommSupplyType;
	}
	public String getAddressType() {
		return addressType;
	}
	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}
	public Integer getBuyerPincode() {
		return buyerPincode;
	}
	public void setBuyerPincode(Integer buyerPincode) {
		this.buyerPincode = buyerPincode;
	}
	public String getEinvCategory() {
		return einvCategory;
	}
	public void setEinvCategory(String einvCategory) {
		this.einvCategory = einvCategory;
	}
	public String getEinvExpCategory() {
		return einvExpCategory;
	}
	public void setEinvExpCategory(String einvExpCategory) {
		this.einvExpCategory = einvExpCategory;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getIrnNo() {
		return irnNo;
	}
	public void setIrnNo(String irnNo) {
		this.irnNo = irnNo;
	}
	public String getMthCd() {
		return mthCd;
	}
	public void setMthCd(String mthCd) {
		this.mthCd = mthCd;
	}
	public String getQrtCd() {
		return qrtCd;
	}
	public void setQrtCd(String qrtCd) {
		this.qrtCd = qrtCd;
	}
	public String getYrCd() {
		return yrCd;
	}
	public void setYrCd(String yrCd) {
		this.yrCd = yrCd;
	}
	public int getSftr() {
		return sftr;
	}
	public void setSftr(int sftr) {
		this.sftr = sftr;
	}
	public String getTrDatemthCd() {
		return trDatemthCd;
	}
	public void setTrDatemthCd(String trDatemthCd) {
		this.trDatemthCd = trDatemthCd;
	}
	public String getTrDateqrtCd() {
		return trDateqrtCd;
	}
	public void setTrDateqrtCd(String trDateqrtCd) {
		this.trDateqrtCd = trDateqrtCd;
	}
	public String getTrDateyrCd() {
		return trDateyrCd;
	}
	public void setTrDateyrCd(String trDateyrCd) {
		this.trDateyrCd = trDateyrCd;
	}
	public String getTotaltaxableamount_str() {
		return totaltaxableamount_str;
	}
	public void setTotaltaxableamount_str(String totaltaxableamount_str) {
		this.totaltaxableamount_str = totaltaxableamount_str;
	}
	public String getTotaltax_str() {
		return totaltax_str;
	}
	public void setTotaltax_str(String totaltax_str) {
		this.totaltax_str = totaltax_str;
	}
	public String getTotalamount_str() {
		return totalamount_str;
	}
	public void setTotalamount_str(String totalamount_str) {
		this.totalamount_str = totalamount_str;
	}
	public String getDateofinvoice_str() {
		return dateofinvoice_str;
	}
	public void setDateofinvoice_str(String dateofinvoice_str) {
		this.dateofinvoice_str = dateofinvoice_str;
	}
	public String getEwayBillDate_str() {
		return ewayBillDate_str;
	}
	public void setEwayBillDate_str(String ewayBillDate_str) {
		this.ewayBillDate_str = ewayBillDate_str;
	}
	public String getIrnStatus() {
		return irnStatus;
	}
	public void setIrnStatus(String irnStatus) {
		this.irnStatus = irnStatus;
	}
	public Double getTotalStateCessAmount() {
		return totalStateCessAmount;
	}
	public void setTotalStateCessAmount(Double totalStateCessAmount) {
		this.totalStateCessAmount = totalStateCessAmount;
	}
	public Double getTotalAssAmount() {
		return totalAssAmount;
	}
	public void setTotalAssAmount(Double totalAssAmount) {
		this.totalAssAmount = totalAssAmount;
	}
	public Double getTotalCessNonAdVal() {
		return totalCessNonAdVal;
	}
	public void setTotalCessNonAdVal(Double totalCessNonAdVal) {
		this.totalCessNonAdVal = totalCessNonAdVal;
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getTyp() {
		return typ;
	}
	public void setTyp(String typ) {
		this.typ = typ;
	}
	public DispatcherDetails getDispatcherDtls() {
		return dispatcherDtls;
	}
	public void setDispatcherDtls(DispatcherDetails dispatcherDtls) {
		this.dispatcherDtls = dispatcherDtls;
	}
	public ShipmentDetails getShipmentDtls() {
		return shipmentDtls;
	}
	public void setShipmentDtls(ShipmentDetails shipmentDtls) {
		this.shipmentDtls = shipmentDtls;
	}
	
	public String getGstr1orEinvoice() {
		return gstr1orEinvoice;
	}
	public void setGstr1orEinvoice(String gstr1orEinvoice) {
		this.gstr1orEinvoice = gstr1orEinvoice;
	}
	public DocDetails getDocDtls() {
		return docDtls;
	}
	public void setDocDtls(DocDetails docDtls) {
		this.docDtls = docDtls;
	}
	public TransportDetails getTranDtls() {
		return tranDtls;
	}
	public void setTranDtls(TransportDetails tranDtls) {
		this.tranDtls = tranDtls;
	}
	public SellerDetails getSellerDtls() {
		return sellerDtls;
	}
	public void setSellerDtls(SellerDetails sellerDtls) {
		this.sellerDtls = sellerDtls;
	}
	public BuyerDetails getBuyerDtls() {
		return buyerDtls;
	}
	public void setBuyerDtls(BuyerDetails buyerDtls) {
		this.buyerDtls = buyerDtls;
	}
	public ValueDetails getValDtls() {
		return valDtls;
	}
	public void setValDtls(ValueDetails valDtls) {
		this.valDtls = valDtls;
	}
	
	public PayeeDetails getPayDtls() {
		return payDtls;
	}
	public void setPayDtls(PayeeDetails payDtls) {
		this.payDtls = payDtls;
	}
	public ReferenceDetails getRefDtls() {
		return refDtls;
	}
	public void setRefDtls(ReferenceDetails refDtls) {
		this.refDtls = refDtls;
	}
	public List<AddlDocDetails> getAddlDocDtls() {
		return addlDocDtls;
	}
	public void setAddlDocDtls(List<AddlDocDetails> addlDocDtls) {
		this.addlDocDtls = addlDocDtls;
	}
	public ExportDetails getExpDtls() {
		return expDtls;
	}
	public void setExpDtls(ExportDetails expDtls) {
		this.expDtls = expDtls;
	}
	public EwayBillDetails getEwbDtls() {
		return ewbDtls;
	}
	public void setEwbDtls(EwayBillDetails ewbDtls) {
		this.ewbDtls = ewbDtls;
	}
	public List<EinvoiceItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<EinvoiceItem> itemList) {
		this.itemList = itemList;
	}
	@Transient
	public String getInvoiceId() {
		return getId() == null ? null : getId().toString();
	}
	public String getS3attachment() {
		return s3attachment;
	}
	public void setS3attachment(String s3attachment) {
		this.s3attachment = s3attachment;
	}
	public int getCsftr() {
		return csftr;
	}
	public void setCsftr(int csftr) {
		this.csftr = csftr;
	}
	public Date getS3attachementDate() {
		return s3attachementDate;
	}
	public void setS3attachementDate(Date s3attachementDate) {
		this.s3attachementDate = s3attachementDate;
	}
	public String getRevchargeNo() {
		return revchargeNo;
	}
	public void setRevchargeNo(String revchargeNo) {
		this.revchargeNo = revchargeNo;
	}
	public String getIgstOnIntra() {
		return igstOnIntra;
	}
	public void setIgstOnIntra(String igstOnIntra) {
		this.igstOnIntra = igstOnIntra;
	}
	public String getSignedQrCode() {
		return signedQrCode;
	}
	public void setSignedQrCode(String signedQrCode) {
		this.signedQrCode = signedQrCode;
	}
	public Double getTotalDiscountAmount() {
		return totalDiscountAmount;
	}
	public void setTotalDiscountAmount(Double totalDiscountAmount) {
		this.totalDiscountAmount = totalDiscountAmount;
	}
	public Double getTotalOthrChrgeAmount() {
		return totalOthrChrgeAmount;
	}
	public void setTotalOthrChrgeAmount(Double totalOthrChrgeAmount) {
		this.totalOthrChrgeAmount = totalOthrChrgeAmount;
	}
	public String getFromPin() {
		return fromPin;
	}
	public void setFromPin(String fromPin) {
		this.fromPin = fromPin;
	}
	public String getToPin() {
		return toPin;
	}
	public void setToPin(String toPin) {
		this.toPin = toPin;
	}
	public String getTransType() {
		return transType;
	}
	public void setTransType(String transType) {
		this.transType = transType;
	}
	public String getStrTransDate() {
		return strTransDate;
	}
	public void setStrTransDate(String strTransDate) {
		this.strTransDate = strTransDate;
	}
	
	public String getSignedInvoice() {
		return signedInvoice;
	}
	public void setSignedInvoice(String signedInvoice) {
		this.signedInvoice = signedInvoice;
	}
	public String getAckNo() {
		return ackNo;
	}
	public void setAckNo(String ackNo) {
		this.ackNo = ackNo;
	}
	public String getEinvStatus() {
		return einvStatus;
	}
	public void setEinvStatus(String einvStatus) {
		this.einvStatus = einvStatus;
	}
	
	public String getAckDt() {
		return ackDt;
	}
	public void setAckDt(String ackDt) {
		this.ackDt = ackDt;
	}
	
	public String getAddcurrencyCode() {
		return addcurrencyCode;
	}
	public void setAddcurrencyCode(String addcurrencyCode) {
		this.addcurrencyCode = addcurrencyCode;
	}
	public String getExchangeRate() {
		return exchangeRate;
	}
	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	
	public Double getTotalCurrencyAmount() {
		return totalCurrencyAmount;
	}
	public void setTotalCurrencyAmount(Double totalCurrencyAmount) {
		this.totalCurrencyAmount = totalCurrencyAmount;
	}
	public String getBankname() {
		return bankname;
	}
	public void setBankname(String bankname) {
		this.bankname = bankname;
	}
	public String getAccountnumber() {
		return accountnumber;
	}
	public void setAccountnumber(String accountnumber) {
		this.accountnumber = accountnumber;
	}
	public String getAccountname() {
		return accountname;
	}
	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}
	public String getBranchname() {
		return branchname;
	}
	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}
	public String getIfsccode() {
		return ifsccode;
	}
	public void setIfsccode(String ifsccode) {
		this.ifsccode = ifsccode;
	}
	public String getDispatchname() {
		return dispatchname;
	}
	public void setDispatchname(String dispatchname) {
		this.dispatchname = dispatchname;
	}
	public String getDispatchAddr1() {
		return dispatchAddr1;
	}
	public void setDispatchAddr1(String dispatchAddr1) {
		this.dispatchAddr1 = dispatchAddr1;
	}
	public String getDispatchAddr2() {
		return dispatchAddr2;
	}
	public void setDispatchAddr2(String dispatchAddr2) {
		this.dispatchAddr2 = dispatchAddr2;
	}
	public String getDispatchLoc() {
		return dispatchLoc;
	}
	public void setDispatchLoc(String dispatchLoc) {
		this.dispatchLoc = dispatchLoc;
	}
	public String getDispatchState() {
		return dispatchState;
	}
	public void setDispatchState(String dispatchState) {
		this.dispatchState = dispatchState;
	}
	public int getDispatchpin() {
		return dispatchpin;
	}
	public void setDispatchpin(int dispatchpin) {
		this.dispatchpin = dispatchpin;
	}
	public String getShipmentGstin() {
		return shipmentGstin;
	}
	public void setShipmentGstin(String shipmentGstin) {
		this.shipmentGstin = shipmentGstin;
	}
	public String getShipmentTrdnm() {
		return shipmentTrdnm;
	}
	public void setShipmentTrdnm(String shipmentTrdnm) {
		this.shipmentTrdnm = shipmentTrdnm;
	}
	public String getShipmentLgnm() {
		return shipmentLgnm;
	}
	public void setShipmentLgnm(String shipmentLgnm) {
		this.shipmentLgnm = shipmentLgnm;
	}
	public String getShipmentAddr1() {
		return shipmentAddr1;
	}
	public void setShipmentAddr1(String shipmentAddr1) {
		this.shipmentAddr1 = shipmentAddr1;
	}
	public String getShipmentAddr2() {
		return shipmentAddr2;
	}
	public void setShipmentAddr2(String shipmentAddr2) {
		this.shipmentAddr2 = shipmentAddr2;
	}
	public String getShipmentLoc() {
		return shipmentLoc;
	}
	public void setShipmentLoc(String shipmentLoc) {
		this.shipmentLoc = shipmentLoc;
	}
	public String getShipmentState() {
		return shipmentState;
	}
	public void setShipmentState(String shipmentState) {
		this.shipmentState = shipmentState;
	}
	public int getShipmentpin() {
		return shipmentpin;
	}
	public void setShipmentpin(int shipmentpin) {
		this.shipmentpin = shipmentpin;
	}
	public String getItcavl() {
		return itcavl;
	}
	public void setItcavl(String itcavl) {
		this.itcavl = itcavl;
	}
	public String getSamebilladdress() {
		return samebilladdress;
	}
	public void setSamebilladdress(String samebilladdress) {
		this.samebilladdress = samebilladdress;
	}
	public Double getPendingAmount() {
		return pendingAmount;
	}
	public void setPendingAmount(Double pendingAmount) {
		this.pendingAmount = pendingAmount;
	}
	public Double getReceivedAmount() {
		return receivedAmount;
	}
	public void setReceivedAmount(Double receivedAmount) {
		this.receivedAmount = receivedAmount;
	}
	public String getConvertedtoinv() {
		return convertedtoinv;
	}
	public void setConvertedtoinv(String convertedtoinv) {
		this.convertedtoinv = convertedtoinv;
	}
	public String getTermDays() {
		return termDays;
	}
	public void setTermDays(String termDays) {
		this.termDays = termDays;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public Double getOriginalinvamount() {
		return originalinvamount;
	}
	public void setOriginalinvamount(Double originalinvamount) {
		this.originalinvamount = originalinvamount;
	}
	public String getTcsorTdsType() {
		return tcsorTdsType;
	}
	public void setTcsorTdsType(String tcsorTdsType) {
		this.tcsorTdsType = tcsorTdsType;
	}
	public String getSubSupplyDesc() {
		return subSupplyDesc;
	}
	public void setSubSupplyDesc(String subSupplyDesc) {
		this.subSupplyDesc = subSupplyDesc;
	}
	public String getCustomFieldText1() {
		return customFieldText1;
	}
	public void setCustomFieldText1(String customFieldText1) {
		this.customFieldText1 = customFieldText1;
	}
	public String getCustomFieldText2() {
		return customFieldText2;
	}
	public void setCustomFieldText2(String customFieldText2) {
		this.customFieldText2 = customFieldText2;
	}
	public String getCustomFieldText3() {
		return customFieldText3;
	}
	public void setCustomFieldText3(String customFieldText3) {
		this.customFieldText3 = customFieldText3;
	}
	public String getCustomFieldText4() {
		return customFieldText4;
	}
	public void setCustomFieldText4(String customFieldText4) {
		this.customFieldText4 = customFieldText4;
	}
	public String getPremium() {
		return premium;
	}
	public void setPremium(String premium) {
		this.premium = premium;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getPrinterintra() {
		return printerintra;
	}
	public void setPrinterintra(String printerintra) {
		this.printerintra = printerintra;
	}
	public String getCustomerEmail() {
		return customerEmail;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	public String getCustomerMailIds() {
		return customerMailIds;
	}
	public void setCustomerMailIds(String customerMailIds) {
		this.customerMailIds = customerMailIds;
	}
	public String getCustomerCCMailIds() {
		return customerCCMailIds;
	}
	public void setCustomerCCMailIds(String customerCCMailIds) {
		this.customerCCMailIds = customerCCMailIds;
	}
	
	public List<String> getGstr2bMatchingId() {
		return gstr2bMatchingId;
	}

	public void setGstr2bMatchingId(List<String> gstr2bMatchingId) {
		this.gstr2bMatchingId = gstr2bMatchingId;
	}

	public String getGstr2bMatchingStatus() {
		return gstr2bMatchingStatus;
	}

	public void setGstr2bMatchingStatus(String gstr2bMatchingStatus) {
		this.gstr2bMatchingStatus = gstr2bMatchingStatus;
	}

	public String getGstr2bMatchingRsn() {
		return gstr2bMatchingRsn;
	}

	public void setGstr2bMatchingRsn(String gstr2bMatchingRsn) {
		this.gstr2bMatchingRsn = gstr2bMatchingRsn;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getDocKey() {
		return docKey;
	}

	public void setDocKey(String docKey) {
		this.docKey = docKey;
	}
	public String getMailSubject() {
		return mailSubject;
	}
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
	public String getMailMessage() {
		return mailMessage;
	}
	public void setMailMessage(String mailMessage) {
		this.mailMessage = mailMessage;
	}
	public Boolean getIsIncludeSignature() {
		return isIncludeSignature;
	}
	public void setIsIncludeSignature(Boolean isIncludeSignature) {
		this.isIncludeSignature = isIncludeSignature;
	}
	public String getSrctype() {
		return srctype;
	}
	public void setSrctype(String srctype) {
		this.srctype = srctype;
	}
	public GSTR1DocumentIssue getDocuploads() {
		return docuploads;
	}
	public void setDocuploads(GSTR1DocumentIssue docuploads) {
		this.docuploads = docuploads;
	}
	public String getCessType() {
		return cessType;
	}
	public void setCessType(String cessType) {
		this.cessType = cessType;
	}
	public List<GSTRAdvanceTax> getTxpda() {
		return txpda;
	}

	public void setTxpda(List<GSTRAdvanceTax> txpda) {
		this.txpda = txpda;
	}

	public List<String> getAmendmentRefId() {
		return amendmentRefId;
	}

	public void setAmendmentRefId(List<String> amendmentRefId) {
		this.amendmentRefId = amendmentRefId;
	}
	public Double getTotalamountforbillofsupplycdn() {
		return totalamountforbillofsupplycdn;
	}
	public void setTotalamountforbillofsupplycdn(Double totalamountforbillofsupplycdn) {
		this.totalamountforbillofsupplycdn = totalamountforbillofsupplycdn;
	}
	public String getCdnnilsupplies() {
		return cdnnilsupplies;
	}
	public void setCdnnilsupplies(String cdnnilsupplies) {
		this.cdnnilsupplies = cdnnilsupplies;
	}
	public boolean isAdvTaxableType() {
		return advTaxableType;
	}
	public void setAdvTaxableType(boolean advTaxableType) {
		this.advTaxableType = advTaxableType;
	}
	public boolean isTdsenable() {
		return tdsenable;
	}
	public void setTdsenable(boolean tdsenable) {
		this.tdsenable = tdsenable;
	}
	public String getTdsSection() {
		return tdsSection;
	}
	public void setTdsSection(String tdsSection) {
		this.tdsSection = tdsSection;
	}
	public Double getTdspercentage() {
		return tdspercentage;
	}
	public void setTdspercentage(Double tdspercentage) {
		this.tdspercentage = tdspercentage;
	}
	public Double getTdsAmount() {
		return tdsAmount;
	}
	public void setTdsAmount(Double tdsAmount) {
		this.tdsAmount = tdsAmount;
	}
	public String getInvoiceLevelCess() {
		return invoiceLevelCess;
	}
	public void setInvoiceLevelCess(String invoiceLevelCess) {
		this.invoiceLevelCess = invoiceLevelCess;
	}
	public ExtendValidityDO getExtendValidity() {
		return extendValidity;
	}
	public void setExtendValidity(ExtendValidityDO extendValidity) {
		this.extendValidity = extendValidity;
	}
	public boolean isEbillValidator() {
		return ebillValidator;
	}
	public void setEbillValidator(boolean ebillValidator) {
		this.ebillValidator = ebillValidator;
	}
	public String getEwaybillRejectDate() {
		return ewaybillRejectDate;
	}
	public void setEwaybillRejectDate(String ewaybillRejectDate) {
		this.ewaybillRejectDate = ewaybillRejectDate;
	}
	public String getEntertaimentprintto() {
		return entertaimentprintto;
	}
	public void setEntertaimentprintto(String entertaimentprintto) {
		this.entertaimentprintto = entertaimentprintto;
	}
}
