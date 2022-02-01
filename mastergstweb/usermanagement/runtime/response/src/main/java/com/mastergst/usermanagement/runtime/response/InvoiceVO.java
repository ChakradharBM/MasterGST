/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

import java.util.Date;

/**
 * Invoice Details VO
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public class InvoiceVO {
	private String ewayBillNo;
	private Date ewayBillDate;
	private String supplyType;
	private String subSupplyType;
	private String docType;
	private String fromGstin;
	private String fromTrdName;
	private String fromAddr1;
	private String fromAddr2;
	private String fromPlace;
	private Number fromPincode;
	private Number fromStateCode;
	private String toGstin;
	private String toTrdName;
	private String toAddr1;
	private String toAddr2;
	private String toPlace;
	private Number toPincode;
	private Number toStateCode;
	private String transporterId;
	private String transporterName;
	
	private Number actualDist;
	private Number noValidDays;
	private String validUpto;
	private Number extendedTimes;
	private String rejectStatus;
	private String vehicleType;
	private Number transactionType;
	private Number otherValue;
	private Number cessNonAdvolValue;
	private String vehicleNo;
	private Number tripshtNo;
	private String userGSTINTransin;
	private String enteredDate;
	private String transMode;
	private String transDocNo;
	private String transDocDate;
	private String groupNo;
	
	/*E-invoice Fileds Start*/
	private String irnNo;
	private String irnStatus;
	private String barCode;
	private String freeQty;
	private Double othrCharges;
	private Double assAmt;
	private Double stateCess;
	private Double cessnonAdvol;
	
	private String gstStatus;
	private String companyGSTIN;
	private String companyStatename;
	private String originalInvoiceNo;
	private String originalInvoiceDate;
	private String returnPeriod;
	private String type;
	private String invoiceNo;
	private Date invoiceDate;
	private String customerGSTIN;
	private String customerName;
	private String customerID;
	private String customerPAN;
	private String customerTAN;
	private String customerTANPAN;
	private String customerLedgerName;
	private String state;
	private String recharge;
	private String ecomGSTN;
	private String category;
	private String hsnCode;
	private String uqc;
	private Double quantity;
	private String rate;
	private Double taxableValue;
	private String itcType;
	private String eligiblePercentage;
	private Double igstRate;
	private Double igstAmount;
	private Double igstTax;
	private Double cgstRate;
	private Double cgstAmount;
	private Double cgstTax;
	private Double sgstRate;
	private Double sgstAmount;
	private Double sgstTax;
	private Double cessRate;
	private Double cessAmount;
	private Double cessTax;
	private Double totalValue;
	private Double totaltax;
	private Double totalItc;
	private Double totalinItc;
	private String status;
	private Double rateperitem;
	private Double ExemptedVal;
	private String itemname;
	private String itemNotesComments;
	private String itemno;
	private double itemDiscount;
	private double itemExmepted;
	private String itemNotescomments;
	private String billingAddress;
	private String shipingAddress;
	private String ecommerceGSTIN;
	private String ewayBillNumber;
	private String ledgerName;
	private String rateInclusiveOfTax;
	private String reverseCharge;
	private String reference;
	private String branch;
	private String vertical;
	private String differentialPercentage;
	private String addTCS;
	private String tcsSection;
	private String tcsPercentage;
	private String bankName;
	private String accountNumber;
	private String accountName;
	private String branchName;
	private String ifsccode;
	private String customerNotes;
	private String termsAndConditions;
	private double tcsAmount;
	private double tcsNetAmount;
	private String customField1;
	private String customField2;
	private String customField3;
	private String customField4;
	private Date dateOfItcClaimed;
	private String revChargeNo;
	private Date transactionDate;
	private String placeOfSupply;
	private String counterFilingStatus;
	
	private String ackno;
	private String einvstatus;
	private String ackdt;
	private String irndt;
	/*Exports Fields Start*/
	private String portCode;
	private String shipBillNo;
	private Date shipBillDate;
	private String additionalCurrencyCode;
	private String exchangeRate;
	private double currencyTotal;
	/*Exports Fields End*/
	private String qrcode;
	public Date getDateOfItcClaimed() {
		return dateOfItcClaimed;
	}

	public void setDateOfItcClaimed(Date dateOfItcClaimed) {
		this.dateOfItcClaimed = dateOfItcClaimed;
	}

	public double getItemDiscount() {
		return itemDiscount;
	}

	public void setItemDiscount(double itemDiscount) {
		this.itemDiscount = itemDiscount;
	}

	public double getItemExmepted() {
		return itemExmepted;
	}

	public void setItemExmepted(double itemExmepted) {
		this.itemExmepted = itemExmepted;
	}

	public String getItemNotescomments() {
		return itemNotescomments;
	}

	public void setItemNotescomments(String itemNotescomments) {
		this.itemNotescomments = itemNotescomments;
	}

	public String getItemno() {
		return itemno;
	}

	public void setItemno(String itemno) {
		this.itemno = itemno;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public String getCustomerGSTIN() {
		return customerGSTIN;
	}
	public void setCustomerGSTIN(String customerGSTIN) {
		this.customerGSTIN = customerGSTIN;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getRecharge() {
		return recharge;
	}
	public void setRecharge(String recharge) {
		this.recharge = recharge;
	}
	public String getEcomGSTN() {
		return ecomGSTN;
	}
	public void setEcomGSTN(String ecomGSTN) {
		this.ecomGSTN = ecomGSTN;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getHsnCode() {
		return hsnCode;
	}
	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
	}
	public String getUqc() {
		return uqc;
	}
	public void setUqc(String uqc) {
		this.uqc = uqc;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public Double getTaxableValue() {
		return taxableValue;
	}
	public void setTaxableValue(Double taxableValue) {
		this.taxableValue = taxableValue;
	}
	public String getItcType() {
		return itcType;
	}
	public void setItcType(String itcType) {
		this.itcType = itcType;
	}
	public String getEligiblePercentage() {
		return eligiblePercentage;
	}
	public void setEligiblePercentage(String eligiblePercentage) {
		this.eligiblePercentage = eligiblePercentage;
	}
	public Double getIgstRate() {
		return igstRate;
	}
	public void setIgstRate(Double igstRate) {
		this.igstRate = igstRate;
	}
	public Double getIgstAmount() {
		return igstAmount;
	}
	public void setIgstAmount(Double igstAmount) {
		this.igstAmount = igstAmount;
	}
	public Double getIgstTax() {
		return igstTax;
	}
	public void setIgstTax(Double igstTax) {
		this.igstTax = igstTax;
	}
	public Double getCgstRate() {
		return cgstRate;
	}
	public void setCgstRate(Double cgstRate) {
		this.cgstRate = cgstRate;
	}
	public Double getCgstAmount() {
		return cgstAmount;
	}
	public void setCgstAmount(Double cgstAmount) {
		this.cgstAmount = cgstAmount;
	}
	public Double getCgstTax() {
		return cgstTax;
	}
	public void setCgstTax(Double cgstTax) {
		this.cgstTax = cgstTax;
	}
	public Double getSgstRate() {
		return sgstRate;
	}
	public void setSgstRate(Double sgstRate) {
		this.sgstRate = sgstRate;
	}
	public Double getSgstAmount() {
		return sgstAmount;
	}
	public void setSgstAmount(Double sgstAmount) {
		this.sgstAmount = sgstAmount;
	}
	public Double getSgstTax() {
		return sgstTax;
	}
	public void setSgstTax(Double sgstTax) {
		this.sgstTax = sgstTax;
	}
	public Double getCessRate() {
		return cessRate;
	}
	public void setCessRate(Double cessRate) {
		this.cessRate = cessRate;
	}
	public Double getCessAmount() {
		return cessAmount;
	}
	public void setCessAmount(Double cessAmount) {
		this.cessAmount = cessAmount;
	}
	public Double getCessTax() {
		return cessTax;
	}
	public void setCessTax(Double cessTax) {
		this.cessTax = cessTax;
	}
	public Double getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(Double totalValue) {
		this.totalValue = totalValue;
	}
	public Double getTotaltax() {
		return totaltax;
	}
	public void setTotaltax(Double totaltax) {
		this.totaltax = totaltax;
	}
	public Double getTotalItc() {
		return totalItc;
	}
	public void setTotalItc(Double totalItc) {
		this.totalItc = totalItc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Double getRateperitem() {
		return rateperitem;
	}
	public void setRateperitem(Double rateperitem) {
		this.rateperitem = rateperitem;
	}
	public String getCompanyGSTIN() {
		return companyGSTIN;
	}
	public void setCompanyGSTIN(String companyGSTIN) {
		this.companyGSTIN = companyGSTIN;
	}
	public String getCompanyStatename() {
		return companyStatename;
	}
	public void setCompanyStatename(String companyStatename) {
		this.companyStatename = companyStatename;
	}
	public String getOriginalInvoiceNo() {
		return originalInvoiceNo;
	}
	public void setOriginalInvoiceNo(String originalInvoiceNo) {
		this.originalInvoiceNo = originalInvoiceNo;
	}
	public String getOriginalInvoiceDate() {
		return originalInvoiceDate;
	}
	public void setOriginalInvoiceDate(String originalInvoiceDate) {
		this.originalInvoiceDate = originalInvoiceDate;
	}
	public Double getExemptedVal() {
		return ExemptedVal;
	}
	public void setExemptedVal(Double exemptedVal) {
		ExemptedVal = exemptedVal;
	}

	public String getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
	}

	public String getShipingAddress() {
		return shipingAddress;
	}

	public void setShipingAddress(String shipingAddress) {
		this.shipingAddress = shipingAddress;
	}

	public String getEwayBillNumber() {
		return ewayBillNumber;
	}

	public void setEwayBillNumber(String ewayBillNumber) {
		this.ewayBillNumber = ewayBillNumber;
	}

	public String getLedgerName() {
		return ledgerName;
	}

	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}

	public String getEcommerceGSTIN() {
		return ecommerceGSTIN;
	}

	public void setEcommerceGSTIN(String ecommerceGSTIN) {
		this.ecommerceGSTIN = ecommerceGSTIN;
	}

	public String getRateInclusiveOfTax() {
		return rateInclusiveOfTax;
	}

	public void setRateInclusiveOfTax(String rateInclusiveOfTax) {
		this.rateInclusiveOfTax = rateInclusiveOfTax;
	}

	public String getReverseCharge() {
		return reverseCharge;
	}

	public void setReverseCharge(String reverseCharge) {
		this.reverseCharge = reverseCharge;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
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

	public String getDifferentialPercentage() {
		return differentialPercentage;
	}

	public void setDifferentialPercentage(String differentialPercentage) {
		this.differentialPercentage = differentialPercentage;
	}

	public String getAddTCS() {
		return addTCS;
	}

	public void setAddTCS(String addTCS) {
		this.addTCS = addTCS;
	}

	public String getTcsSection() {
		return tcsSection;
	}

	public void setTcsSection(String tcsSection) {
		this.tcsSection = tcsSection;
	}

	public String getTcsPercentage() {
		return tcsPercentage;
	}

	public void setTcsPercentage(String tcsPercentage) {
		this.tcsPercentage = tcsPercentage;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getIfsccode() {
		return ifsccode;
	}

	public void setIfsccode(String ifsccode) {
		this.ifsccode = ifsccode;
	}

	public String getCustomerNotes() {
		return customerNotes;
	}

	public void setCustomerNotes(String customerNotes) {
		this.customerNotes = customerNotes;
	}

	public String getTermsAndConditions() {
		return termsAndConditions;
	}

	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}

	public double getTcsAmount() {
		return tcsAmount;
	}

	public void setTcsAmount(double tcsAmount) {
		this.tcsAmount = tcsAmount;
	}

	public double getTcsNetAmount() {
		return tcsNetAmount;
	}

	public void setTcsNetAmount(double tcsNetAmount) {
		this.tcsNetAmount = tcsNetAmount;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public String getItemNotesComments() {
		return itemNotesComments;
	}

	public void setItemNotesComments(String itemNotesComments) {
		this.itemNotesComments = itemNotesComments;
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

	public String getGstStatus() {
		return gstStatus;
	}

	public void setGstStatus(String gstStatus) {
		this.gstStatus = gstStatus;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public String getCustomerPAN() {
		return customerPAN;
	}

	public void setCustomerPAN(String customerPAN) {
		this.customerPAN = customerPAN;
	}

	public String getCustomerTAN() {
		return customerTAN;
	}

	public void setCustomerTAN(String customerTAN) {
		this.customerTAN = customerTAN;
	}

	public String getCustomerTANPAN() {
		return customerTANPAN;
	}

	public void setCustomerTANPAN(String customerTANPAN) {
		this.customerTANPAN = customerTANPAN;
	}

	public String getEwayBillNo() {
		return ewayBillNo;
	}

	public void setEwayBillNo(String ewayBillNo) {
		this.ewayBillNo = ewayBillNo;
	}

	public Date getEwayBillDate() {
		return ewayBillDate;
	}

	public void setEwayBillDate(Date ewayBillDate) {
		this.ewayBillDate = ewayBillDate;
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

	public Number getOtherValue() {
		return otherValue;
	}

	public void setOtherValue(Number otherValue) {
		this.otherValue = otherValue;
	}

	public Number getCessNonAdvolValue() {
		return cessNonAdvolValue;
	}

	public void setCessNonAdvolValue(Number cessNonAdvolValue) {
		this.cessNonAdvolValue = cessNonAdvolValue;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public Number getTripshtNo() {
		return tripshtNo;
	}

	public void setTripshtNo(Number tripshtNo) {
		this.tripshtNo = tripshtNo;
	}

	public String getUserGSTINTransin() {
		return userGSTINTransin;
	}

	public void setUserGSTINTransin(String userGSTINTransin) {
		this.userGSTINTransin = userGSTINTransin;
	}

	public String getEnteredDate() {
		return enteredDate;
	}

	public void setEnteredDate(String enteredDate) {
		this.enteredDate = enteredDate;
	}

	public String getTransMode() {
		return transMode;
	}

	public void setTransMode(String transMode) {
		this.transMode = transMode;
	}

	public String getTransDocNo() {
		return transDocNo;
	}

	public void setTransDocNo(String transDocNo) {
		this.transDocNo = transDocNo;
	}

	public String getTransDocDate() {
		return transDocDate;
	}

	public void setTransDocDate(String transDocDate) {
		this.transDocDate = transDocDate;
	}

	public String getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(String groupNo) {
		this.groupNo = groupNo;
	}
	public String getIrnNo() {
		return irnNo;
	}
	public void setIrnNo(String irnNo) {
		this.irnNo = irnNo;
	}
	public String getIrnStatus() {
		return irnStatus;
	}
	public void setIrnStatus(String irnStatus) {
		this.irnStatus = irnStatus;
	}
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	public String getFreeQty() {
		return freeQty;
	}
	public void setFreeQty(String freeQty) {
		this.freeQty = freeQty;
	}
	public Double getOthrCharges() {
		return othrCharges;
	}
	public void setOthrCharges(Double othrCharges) {
		this.othrCharges = othrCharges;
	}
	public Double getAssAmt() {
		return assAmt;
	}
	public void setAssAmt(Double assAmt) {
		this.assAmt = assAmt;
	}
	public Double getStateCess() {
		return stateCess;
	}
	public void setStateCess(Double stateCess) {
		this.stateCess = stateCess;
	}
	public Double getCessnonAdvol() {
		return cessnonAdvol;
	}
	public void setCessnonAdvol(Double cessnonAdvol) {
		this.cessnonAdvol = cessnonAdvol;
	}

	public String getRevChargeNo() {
		return revChargeNo;
	}

	public void setRevChargeNo(String revChargeNo) {
		this.revChargeNo = revChargeNo;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getCounterFilingStatus() {
		return counterFilingStatus;
	}

	public void setCounterFilingStatus(String counterFilingStatus) {
		this.counterFilingStatus = counterFilingStatus;
	}

	public String getPlaceOfSupply() {
		return placeOfSupply;
	}

	public void setPlaceOfSupply(String placeOfSupply) {
		this.placeOfSupply = placeOfSupply;
	}

	public String getAckno() {
		return ackno;
	}

	public void setAckno(String ackno) {
		this.ackno = ackno;
	}

	public String getEinvstatus() {
		return einvstatus;
	}

	public void setEinvstatus(String einvstatus) {
		this.einvstatus = einvstatus;
	}

	public String getAckdt() {
		return ackdt;
	}

	public void setAckdt(String ackdt) {
		this.ackdt = ackdt;
	}

	public String getIrndt() {
		return irndt;
	}

	public void setIrndt(String irndt) {
		this.irndt = irndt;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getShipBillNo() {
		return shipBillNo;
	}

	public void setShipBillNo(String shipBillNo) {
		this.shipBillNo = shipBillNo;
	}

	public Date getShipBillDate() {
		return shipBillDate;
	}

	public void setShipBillDate(Date shipBillDate) {
		this.shipBillDate = shipBillDate;
	}

	public String getAdditionalCurrencyCode() {
		return additionalCurrencyCode;
	}

	public void setAdditionalCurrencyCode(String additionalCurrencyCode) {
		this.additionalCurrencyCode = additionalCurrencyCode;
	}

	public String getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public double getCurrencyTotal() {
		return currencyTotal;
	}

	public void setCurrencyTotal(double currencyTotal) {
		this.currencyTotal = currencyTotal;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}

	public String getCustomerLedgerName() {
		return customerLedgerName;
	}

	public void setCustomerLedgerName(String customerLedgerName) {
		this.customerLedgerName = customerLedgerName;
	}

	public Double getTotalinItc() {
		return totalinItc;
	}

	public void setTotalinItc(Double totalinItc) {
		this.totalinItc = totalinItc;
	}
	
	
}
