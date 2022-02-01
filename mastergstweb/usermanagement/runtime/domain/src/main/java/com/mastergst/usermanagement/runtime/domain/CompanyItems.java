/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.mastergst.core.domain.Base;

/**
 * Company Items information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "companyitems")
public class CompanyItems extends Base {
	
	private String userid;
	private String fullname;
	private String clientid;
	private String itemType;
	private String itemno;
	private String description;
	private String itemgroupno;
	private String groupdescription;
	private String itemnoAndDescription;
	private String itemDescription;
	private String unit;
	private Double openingStock;
	private Double currentStock;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date asOnDate;
	private String stocklevel;
	private String reOrderType;
	private String code;
	private String taxrate;
	private Double salePrice;
	private Double purchasePrice;
	private String purchaseGstType;
	private Double purchaseTaxAmt;
	private Double purchaseTotAmt;
	private Double wholeSalePrice;
	private Double mrpPrice;
	private Boolean isExempted;
	private String exemptedType;
	private Double exmepted;
	private Boolean isDiscount;
	private String discountType;
	private Double discount;
	
	
	private Double sellingpriceb2b;
	private Double sellingpriceb2c;
	/*Inventory Fields start*/
	private Double retailPrice;
	private Double closingStock;
	private String ledgerName;
	private String category;
	/*Inventory Fields End*/
	String mthCd;
	String qrtCd;
	String yrCd;
	int sftr;
	/*Custom Fields Start*/
	private String itemCustomField1;
	private String itemCustomField2;
	private String itemCustomField3;
	private String itemCustomField4;
	private String itemCustomFieldText1;
	private String itemCustomFieldText2;
	private String itemCustomFieldText3;
	private String itemCustomFieldText4;
	/*Custom Fields End*/
	private String salePriceTxt;
	private String purchasePriceTxt;
	private Double totalQtyUsage;
	private Double stockAdjItemQty;
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
	public String getItemno() {
		return itemno;
	}
	public void setItemno(String itemno) {
		this.itemno = itemno;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Double getSellingpriceb2b() {
		return sellingpriceb2b;
	}
	public void setSellingpriceb2b(Double sellingpriceb2b) {
		this.sellingpriceb2b = sellingpriceb2b;
	}
	public Double getSellingpriceb2c() {
		return sellingpriceb2c;
	}
	public void setSellingpriceb2c(Double sellingpriceb2c) {
		this.sellingpriceb2c = sellingpriceb2c;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getStocklevel() {
		return stocklevel;
	}
	public void setStocklevel(String stocklevel) {
		this.stocklevel = stocklevel;
	}
	public String getTaxrate() {
		return taxrate;
	}
	public void setTaxrate(String taxrate) {
		this.taxrate = taxrate;
	}
	public String getItemnoAndDescription() {
		return itemnoAndDescription;
	}
	public void setItemnoAndDescription(String itemnoAndDescription) {
		this.itemnoAndDescription = itemnoAndDescription;
	}

	public String getItemgroupno() {
		return itemgroupno;
	}

	public void setItemgroupno(String itemgroupno) {
		this.itemgroupno = itemgroupno;
	}

	public String getGroupdescription() {
		return groupdescription;
	}

	public void setGroupdescription(String groupdescription) {
		this.groupdescription = groupdescription;
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
	public Boolean getIsExempted() {
		return isExempted;
	}
	public void setIsExempted(Boolean isExempted) {
		this.isExempted = isExempted;
	}
	public Double getRetailPrice() {
		return retailPrice;
	}
	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}
	public Double getWholeSalePrice() {
		return wholeSalePrice;
	}
	public void setWholeSalePrice(Double wholeSalePrice) {
		this.wholeSalePrice = wholeSalePrice;
	}
	public Double getMrpPrice() {
		return mrpPrice;
	}
	public void setMrpPrice(Double mrpPrice) {
		this.mrpPrice = mrpPrice;
	}
	public Double getOpeningStock() {
		return openingStock;
	}
	public void setOpeningStock(Double openingStock) {
		this.openingStock = openingStock;
	}
	public Double getClosingStock() {
		return closingStock;
	}
	public void setClosingStock(Double closingStock) {
		this.closingStock = closingStock;
	}
	public String getExemptedType() {
		return exemptedType;
	}
	public void setExemptedType(String exemptedType) {
		this.exemptedType = exemptedType;
	}
	public String getReOrderType() {
		return reOrderType;
	}
	public void setReOrderType(String reOrderType) {
		this.reOrderType = reOrderType;
	}
	public String getLedgerName() {
		return ledgerName;
	}
	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public Date getAsOnDate() {
		return asOnDate;
	}
	public void setAsOnDate(Date asOnDate) {
		this.asOnDate = asOnDate;
	}
	public Double getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}
	public Double getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public Boolean getIsDiscount() {
		return isDiscount;
	}
	public void setIsDiscount(Boolean isDiscount) {
		this.isDiscount = isDiscount;
	}
	public String getDiscountType() {
		return discountType;
	}
	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}
	public String getPurchaseGstType() {
		return purchaseGstType;
	}
	public void setPurchaseGstType(String purchaseGstType) {
		this.purchaseGstType = purchaseGstType;
	}
	public String getItemDescription() {
		return itemDescription;
	}
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	public Double getCurrentStock() {
		return currentStock;
	}
	public void setCurrentStock(Double currentStock) {
		this.currentStock = currentStock;
	}
	public Double getPurchaseTaxAmt() {
		return purchaseTaxAmt;
	}
	public void setPurchaseTaxAmt(Double purchaseTaxAmt) {
		this.purchaseTaxAmt = purchaseTaxAmt;
	}
	public Double getPurchaseTotAmt() {
		return purchaseTotAmt;
	}
	public void setPurchaseTotAmt(Double purchaseTotAmt) {
		this.purchaseTotAmt = purchaseTotAmt;
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
	public String getSalePriceTxt() {
		return salePriceTxt;
	}
	public void setSalePriceTxt(String salePriceTxt) {
		this.salePriceTxt = salePriceTxt;
	}
	public String getPurchasePriceTxt() {
		return purchasePriceTxt;
	}
	public void setPurchasePriceTxt(String purchasePriceTxt) {
		this.purchasePriceTxt = purchasePriceTxt;
	}
	public Double getTotalQtyUsage() {
		return totalQtyUsage;
	}
	public void setTotalQtyUsage(Double totalQtyUsage) {
		this.totalQtyUsage = totalQtyUsage;
	}
	public String getItemCustomFieldText1() {
		return itemCustomFieldText1;
	}
	public void setItemCustomFieldText1(String itemCustomFieldText1) {
		this.itemCustomFieldText1 = itemCustomFieldText1;
	}
	public String getItemCustomFieldText2() {
		return itemCustomFieldText2;
	}
	public void setItemCustomFieldText2(String itemCustomFieldText2) {
		this.itemCustomFieldText2 = itemCustomFieldText2;
	}
	public String getItemCustomFieldText3() {
		return itemCustomFieldText3;
	}
	public void setItemCustomFieldText3(String itemCustomFieldText3) {
		this.itemCustomFieldText3 = itemCustomFieldText3;
	}
	public String getItemCustomFieldText4() {
		return itemCustomFieldText4;
	}
	public void setItemCustomFieldText4(String itemCustomFieldText4) {
		this.itemCustomFieldText4 = itemCustomFieldText4;
	}
	public Double getStockAdjItemQty() {
		return stockAdjItemQty;
	}
	public void setStockAdjItemQty(Double stockAdjItemQty) {
		this.stockAdjItemQty = stockAdjItemQty;
	}
}
