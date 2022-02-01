/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;
import com.mastergst.core.util.CustomDoubleSerializers;

/**
 * Item information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */

//@JsonIgnoreProperties({"id"})
public class EinvoiceItem {
	@JsonProperty("SlNo")
	private String slNo;
	@JsonProperty("PrdDesc")
	private String prdDesc;
	@JsonProperty("IsServc")
	private String isServc;
	
	@JsonProperty("HsnCd")
	private String hsnCd;
	@JsonProperty("Barcde")
	private String barcde;
	
	@JsonIgnore
	private Double quty;
	
	@JsonProperty("Qty")
	@JsonSerialize(using = CustomDoubleSerializers.class)
	private Double qty;
	
	@JsonProperty("FreeQty")  /**/
	private Number freeQty;
	
	@JsonProperty("Unit")
	private String unit;
	@JsonProperty("UnitPrice") 
	@JsonSerialize(using = CustomDoubleSerializers.class)
	private Double unitPrice;
	@JsonProperty("TotAmt") 
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double totAmt;
	@JsonProperty("Discount")  
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double discount;
	
	@JsonProperty("PreTaxVal")  
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double preTaxVal;
	
	@JsonProperty("OthChrg")  
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double othChrg;
	
	@JsonProperty("GstRt") /**/
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double gstRt;
	
	@JsonProperty("AssAmt")  /*Number*/
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double assAmt;

	
	@JsonProperty("IgstAmt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double igstAmt;
	@JsonProperty("CgstAmt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cgstAmt;
	@JsonProperty("SgstAmt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sgstAmt;
	
	@JsonProperty("CesRt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cesRt;
	
	@JsonProperty("CesAmt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cesAmt;
	
	@JsonProperty("CesNonAdvlAmt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cesNonAdvlAmt;
	
	
	@JsonProperty("StateCesRt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double stateCesRt;
	@JsonProperty("StateCesAmt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double stateCesAmt;
	
	@JsonProperty("StateCesNonAdvlAmt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double stateCesNonAdvlAmt;
	
	@JsonProperty("OrdLineRef")  /*String*/
	private String ordLineRef;
	
	@JsonProperty("OrgCntry")   /*String*/
	private String orgCntry;
	
	@JsonProperty("PrdSlNo")   /*String*/
	private String prdSlNo;
	
	@JsonProperty("TotItemVal")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double totItemVal;
	
	@JsonProperty("BchDtls")
	BatchDetails bchDtls;
	
	@JsonProperty("AttribDtls")
	List<AttributeDetails> attribDtls = LazyList.decorate(new ArrayList<AttributeDetails>(), 
			FactoryUtils.instantiateFactory(AttributeDetails.class));

	public String getSlNo() {
		return slNo;
	}

	public void setSlNo(String slNo) {
		this.slNo = slNo;
	}

	public String getPrdDesc() {
		return prdDesc;
	}

	public void setPrdDesc(String prdDesc) {
		this.prdDesc = prdDesc;
	}

	public String getIsServc() {
		return isServc;
	}

	public void setIsServc(String isServc) {
		this.isServc = isServc;
	}

	public String getHsnCd() {
		return hsnCd;
	}

	public void setHsnCd(String hsnCd) {
		this.hsnCd = hsnCd;
	}

	public String getBarcde() {
		return barcde;
	}

	public void setBarcde(String barcde) {
		this.barcde = barcde;
	}

	public Double getQuty() {
		return quty;
	}

	public void setQuty(Double quty) {
		this.quty = quty;
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public Number getFreeQty() {
		return freeQty;
	}

	public void setFreeQty(Number freeQty) {
		this.freeQty = freeQty;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Double getTotAmt() {
		return totAmt;
	}

	public void setTotAmt(Double totAmt) {
		this.totAmt = totAmt;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getPreTaxVal() {
		return preTaxVal;
	}

	public void setPreTaxVal(Double preTaxVal) {
		this.preTaxVal = preTaxVal;
	}

	public Double getOthChrg() {
		return othChrg;
	}

	public void setOthChrg(Double othChrg) {
		this.othChrg = othChrg;
	}

	public Double getGstRt() {
		return gstRt;
	}

	public void setGstRt(Double gstRt) {
		this.gstRt = gstRt;
	}

	public Double getAssAmt() {
		return assAmt;
	}

	public void setAssAmt(Double assAmt) {
		this.assAmt = assAmt;
	}

	public Double getIgstAmt() {
		return igstAmt;
	}

	public void setIgstAmt(Double igstAmt) {
		this.igstAmt = igstAmt;
	}

	public Double getCgstAmt() {
		return cgstAmt;
	}

	public void setCgstAmt(Double cgstAmt) {
		this.cgstAmt = cgstAmt;
	}

	public Double getSgstAmt() {
		return sgstAmt;
	}

	public void setSgstAmt(Double sgstAmt) {
		this.sgstAmt = sgstAmt;
	}

	public Double getCesRt() {
		return cesRt;
	}

	public void setCesRt(Double cesRt) {
		this.cesRt = cesRt;
	}

	public Double getCesAmt() {
		return cesAmt;
	}

	public void setCesAmt(Double cesAmt) {
		this.cesAmt = cesAmt;
	}

	public Double getCesNonAdvlAmt() {
		return cesNonAdvlAmt;
	}

	public void setCesNonAdvlAmt(Double cesNonAdvlAmt) {
		this.cesNonAdvlAmt = cesNonAdvlAmt;
	}

	public Double getStateCesRt() {
		return stateCesRt;
	}

	public void setStateCesRt(Double stateCesRt) {
		this.stateCesRt = stateCesRt;
	}

	public Double getStateCesAmt() {
		return stateCesAmt;
	}

	public void setStateCesAmt(Double stateCesAmt) {
		this.stateCesAmt = stateCesAmt;
	}

	public Double getStateCesNonAdvlAmt() {
		return stateCesNonAdvlAmt;
	}

	public void setStateCesNonAdvlAmt(Double stateCesNonAdvlAmt) {
		this.stateCesNonAdvlAmt = stateCesNonAdvlAmt;
	}

	public String getOrdLineRef() {
		return ordLineRef;
	}

	public void setOrdLineRef(String ordLineRef) {
		this.ordLineRef = ordLineRef;
	}

	public String getOrgCntry() {
		return orgCntry;
	}

	public void setOrgCntry(String orgCntry) {
		this.orgCntry = orgCntry;
	}

	public String getPrdSlNo() {
		return prdSlNo;
	}

	public void setPrdSlNo(String prdSlNo) {
		this.prdSlNo = prdSlNo;
	}

	public Double getTotItemVal() {
		return totItemVal;
	}

	public void setTotItemVal(Double totItemVal) {
		this.totItemVal = totItemVal;
	}
	
	public BatchDetails getBchDtls() {
		return bchDtls;
	}

	public void setBchDtls(BatchDetails bchDtls) {
		this.bchDtls = bchDtls;
	}

	public List<AttributeDetails> getAttribDtls() {
		return attribDtls;
	}

	public void setAttribDtls(List<AttributeDetails> attribDtls) {
		this.attribDtls = attribDtls;
	}

	@Override
	public String toString() {
		return "EinvoiceItem [slNo=" + slNo + ", prdDesc=" + prdDesc + ", isServc=" + isServc + ", hsnCd=" + hsnCd
				+ ", barcde=" + barcde + ", quty=" + quty + ", qty=" + qty + ", freeQty=" + freeQty + ", unit=" + unit
				+ ", unitPrice=" + unitPrice + ", totAmt=" + totAmt + ", Discount=" + discount + ", preTaxVal="
				+ preTaxVal + ", othChrg=" + othChrg + ", gstRt=" + gstRt + ", assAmt=" + assAmt + ", igstAmt="
				+ igstAmt + ", cgstAmt=" + cgstAmt + ", sgstAmt=" + sgstAmt + ", cesRt=" + cesRt + ", cesAmt=" + cesAmt
				+ ", cesNonAdvlAmt=" + cesNonAdvlAmt + ", stateCesRt=" + stateCesRt + ", stateCesAmt=" + stateCesAmt
				+ ", stateCesNonAdvlAmt=" + stateCesNonAdvlAmt + ", ordLineRef=" + ordLineRef + ", orgCntry=" + orgCntry
				+ ", prdSlNo=" + prdSlNo + ", totItemVal=" + totItemVal + ", attribDtls=" + attribDtls + "]";
	}
}
