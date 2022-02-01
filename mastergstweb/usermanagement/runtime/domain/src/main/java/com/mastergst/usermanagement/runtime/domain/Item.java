/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * Item information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public class Item {

	@Id
	private ObjectId id;

	private String itemno;
	private String category;
	private String hsn;
	private String barCode;
	private String sac;
	private String uqc;
	private Double quantity;
	private String freeQty;
	private Double rateperitem;
	private Double total;
	private Double discount;
	private Double exmepted;
	private Double othrCharges;
	private Double assAmt;
	private Double taxablevalue;
	private Double rate;
	private Double cgstrate;
	private Double cgstamount;
	private Double cgstavltax;
	
	private Double sgstrate;
	private Double sgstamount;
	private Double ugstrate;
	private Double ugstamount;
	private Double sgstavltax;
	
	private Double igstrate;
	private Double igstamount;
	private Double igstavltax;
	
	private Double stateCess;
	private Double cessNonAdvol;
	private Double cessrate;
	private Double cessamount;
	private Double cessavltax;
	private Double isdcessamount;
	private String elg;
	private Double elgpercent;
	
	private Double advreceived;
	
	private String splyType;
	private Double nilAmt;
	private Double exptAmt;
	private Double ngsupAmt;

	private String confirmtaxblevalue;
	private String additionalchargesdescription;
	private Long additionalchargevalue;
	private String isittaxblevalue;
	private String itemNotescomments;

	private Long invoicevalue;

	private String reversecharges;
	
	private String type;
	private String itcRevtype;
	private String isdType;
	private String advReceiptNo;
	private String advReceiptDate;
	private String advStateName;
	private Double advReceivedAmount;
	private Double advAdjustableAmount;
	private Double advadjustedAmount;
	private String desc;
	private Double advRemaingAmount;
	
	private Double currencytotalAmount;
	private String ledgerName;
	private String disper;
	/*Item Custom Fields*/
	private String itemCustomField1;
	private String itemCustomField2;
	private String itemCustomField3;
	private String itemCustomField4;
	private String itemId;
	public Item() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getItemno() {
		return itemno;
	}

	public void setItemno(String itemno) {
		this.itemno = itemno;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getHsn() {
		return hsn;
	}

	public void setHsn(String hsn) {
		this.hsn = hsn;
	}

	public String getSac() {
		return sac;
	}

	public void setSac(String sac) {
		this.sac = sac;
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

	public Double getRateperitem() {
		return rateperitem;
	}

	public void setRateperitem(Double rateperitem) {
		this.rateperitem = rateperitem;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	
	public Double getExmepted() {
		return exmepted;
	}

	public void setExmepted(Double exmepted) {
		this.exmepted = exmepted;
	}
	
	public Double getTaxablevalue() {
		return taxablevalue;
	}

	public void setTaxablevalue(Double taxablevalue) {
		this.taxablevalue = taxablevalue;
	}

	public Double getCgstrate() {
		return cgstrate;
	}

	public void setCgstrate(Double cgstrate) {
		this.cgstrate = cgstrate;
	}

	public Double getCgstamount() {
		return cgstamount;
	}

	public void setCgstamount(Double cgstamount) {
		this.cgstamount = cgstamount;
	}

	public Double getCgstavltax() {
		return cgstavltax;
	}

	public void setCgstavltax(Double cgstavltax) {
		this.cgstavltax = cgstavltax;
	}

	public Double getSgstrate() {
		return sgstrate;
	}

	public void setSgstrate(Double sgstrate) {
		this.sgstrate = sgstrate;
	}

	public Double getSgstamount() {
		return sgstamount;
	}

	public void setSgstamount(Double sgstamount) {
		this.sgstamount = sgstamount;
	}

	public Double getSgstavltax() {
		return sgstavltax;
	}

	public void setSgstavltax(Double sgstavltax) {
		this.sgstavltax = sgstavltax;
	}

	public Double getIgstrate() {
		return igstrate;
	}

	public void setIgstrate(Double igstrate) {
		this.igstrate = igstrate;
	}

	public Double getIgstamount() {
		return igstamount;
	}

	public void setIgstamount(Double igstamount) {
		this.igstamount = igstamount;
	}

	public Double getIgstavltax() {
		return igstavltax;
	}

	public void setIgstavltax(Double igstavltax) {
		this.igstavltax = igstavltax;
	}

	public Double getCessrate() {
		return cessrate;
	}

	public void setCessrate(Double cessrate) {
		this.cessrate = cessrate;
	}

	public Double getCessamount() {
		return cessamount;
	}

	public void setCessamount(Double cessamount) {
		this.cessamount = cessamount;
	}

	public Double getCessavltax() {
		return cessavltax;
	}

	public void setCessavltax(Double cessavltax) {
		this.cessavltax = cessavltax;
	}

	public String getElg() {
		return elg;
	}

	public void setElg(String elg) {
		this.elg = elg;
	}

	public Double getElgpercent() {
		return elgpercent;
	}

	public void setElgpercent(Double elgpercent) {
		this.elgpercent = elgpercent;
	}

	public Double getAdvreceived() {
		return advreceived;
	}

	public void setAdvreceived(Double advreceived) {
		this.advreceived = advreceived;
	}

	public String getSplyType() {
		return splyType;
	}

	public void setSplyType(String splyType) {
		this.splyType = splyType;
	}

	public Double getNilAmt() {
		return nilAmt;
	}

	public void setNilAmt(Double nilAmt) {
		this.nilAmt = nilAmt;
	}

	public Double getExptAmt() {
		return exptAmt;
	}

	public void setExptAmt(Double exptAmt) {
		this.exptAmt = exptAmt;
	}

	public Double getNgsupAmt() {
		return ngsupAmt;
	}

	public void setNgsupAmt(Double ngsupAmt) {
		this.ngsupAmt = ngsupAmt;
	}

	public String getConfirmtaxblevalue() {
		return confirmtaxblevalue;
	}

	public void setConfirmtaxblevalue(String confirmtaxblevalue) {
		this.confirmtaxblevalue = confirmtaxblevalue;
	}

	public String getAdditionalchargesdescription() {
		return additionalchargesdescription;
	}

	public void setAdditionalchargesdescription(String additionalchargesdescription) {
		this.additionalchargesdescription = additionalchargesdescription;
	}

	public Long getAdditionalchargevalue() {
		return additionalchargevalue;
	}

	public void setAdditionalchargevalue(Long additionalchargevalue) {
		this.additionalchargevalue = additionalchargevalue;
	}

	public String getIsittaxblevalue() {
		return isittaxblevalue;
	}

	public void setIsittaxblevalue(String isittaxblevalue) {
		this.isittaxblevalue = isittaxblevalue;
	}

	public Long getInvoicevalue() {
		return invoicevalue;
	}

	public void setInvoicevalue(Long invoicevalue) {
		this.invoicevalue = invoicevalue;
	}

	public String getReversecharges() {
		return reversecharges;
	}

	public void setReversecharges(String reversecharges) {
		this.reversecharges = reversecharges;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAdvReceiptNo() {
		return advReceiptNo;
	}

	public void setAdvReceiptNo(String advReceiptNo) {
		this.advReceiptNo = advReceiptNo;
	}

	public String getAdvReceiptDate() {
		return advReceiptDate;
	}

	public void setAdvReceiptDate(String advReceiptDate) {
		this.advReceiptDate = advReceiptDate;
	}

	public String getAdvStateName() {
		return advStateName;
	}

	public void setAdvStateName(String advStateName) {
		this.advStateName = advStateName;
	}

	public Double getAdvReceivedAmount() {
		return advReceivedAmount;
	}

	public void setAdvReceivedAmount(Double advReceivedAmount) {
		this.advReceivedAmount = advReceivedAmount;
	}

	public Double getAdvAdjustableAmount() {
		return advAdjustableAmount;
	}

	public void setAdvAdjustableAmount(Double advAdjustableAmount) {
		this.advAdjustableAmount = advAdjustableAmount;
	}

	public Double getAdvadjustedAmount() {
		return advadjustedAmount;
	}

	public void setAdvadjustedAmount(Double advadjustedAmount) {
		this.advadjustedAmount = advadjustedAmount;
	}

	public String getItcRevtype() {
		return itcRevtype;
	}

	public void setItcRevtype(String itcRevtype) {
		this.itcRevtype = itcRevtype;
	}

	public String getIsdType() {
		return isdType;
	}

	public void setIsdType(String isdType) {
		this.isdType = isdType;
	}

	public Double getIsdcessamount() {
		return isdcessamount;
	}

	public void setIsdcessamount(Double isdcessamount) {
		this.isdcessamount = isdcessamount;
	}

	public String getItemNotescomments() {
		return itemNotescomments;
	}

	public void setItemNotescomments(String itemNotescomments) {
		this.itemNotescomments = itemNotescomments;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Double getUgstrate() {
		return ugstrate;
	}

	public void setUgstrate(Double ugstrate) {
		this.ugstrate = ugstrate;
	}

	public Double getUgstamount() {
		return ugstamount;
	}

	public void setUgstamount(Double ugstamount) {
		this.ugstamount = ugstamount;
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
	public Double getCurrencytotalAmount() {
		return currencytotalAmount;
	}

	public void setCurrencytotalAmount(Double currencytotalAmount) {
		this.currencytotalAmount = currencytotalAmount;
	}

	public Double getCessNonAdvol() {
		return cessNonAdvol;
	}
	public void setCessNonAdvol(Double cessNonAdvol) {
		this.cessNonAdvol = cessNonAdvol;
	}

	public Double getAdvRemaingAmount() {
		return advRemaingAmount;
	}

	public void setAdvRemaingAmount(Double advRemaingAmount) {
		this.advRemaingAmount = advRemaingAmount;
	}

	public String getLedgerName() {
		return ledgerName;
	}

	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}

	public String getDisper() {
		return disper;
	}

	public void setDisper(String disper) {
		this.disper = disper;
	}


	public String getItemCustomField1() {
		return itemCustomField1;
	}

	public void setItemCustomField1(String itemCustomField1) {
		this.itemCustomField1 = itemCustomField1;
	}

	public String getItemCustomField2() {
		return itemCustomField2;
	}

	public void setItemCustomField2(String itemCustomField2) {
		this.itemCustomField2 = itemCustomField2;
	}

	public String getItemCustomField3() {
		return itemCustomField3;
	}

	public void setItemCustomField3(String itemCustomField3) {
		this.itemCustomField3 = itemCustomField3;
	}

	public String getItemCustomField4() {
		return itemCustomField4;
	}

	public void setItemCustomField4(String itemCustomField4) {
		this.itemCustomField4 = itemCustomField4;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

}
