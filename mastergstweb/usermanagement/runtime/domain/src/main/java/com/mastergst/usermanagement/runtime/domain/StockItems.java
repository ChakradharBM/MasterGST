package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class StockItems {
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
	
	/*Inventory Fields start*/
	private Double retailPrice;
	private Double closingStock;
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
	/*Custom Fields End*/
	private String salePriceTxt;
	private String purchasePriceTxt;
	
	private Double totInQty;
	private Double totOutQty;
	private Double totBalQty;
	private List<StockAdjustments> stocks;

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

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
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

	public String getItemnoAndDescription() {
		return itemnoAndDescription;
	}

	public void setItemnoAndDescription(String itemnoAndDescription) {
		this.itemnoAndDescription = itemnoAndDescription;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Double getOpeningStock() {
		return openingStock;
	}

	public void setOpeningStock(Double openingStock) {
		this.openingStock = openingStock;
	}

	public Double getCurrentStock() {
		return currentStock;
	}

	public void setCurrentStock(Double currentStock) {
		this.currentStock = currentStock;
	}

	public Date getAsOnDate() {
		return asOnDate;
	}

	public void setAsOnDate(Date asOnDate) {
		this.asOnDate = asOnDate;
	}

	public String getStocklevel() {
		return stocklevel;
	}

	public void setStocklevel(String stocklevel) {
		this.stocklevel = stocklevel;
	}

	public String getReOrderType() {
		return reOrderType;
	}

	public void setReOrderType(String reOrderType) {
		this.reOrderType = reOrderType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTaxrate() {
		return taxrate;
	}

	public void setTaxrate(String taxrate) {
		this.taxrate = taxrate;
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

	public String getPurchaseGstType() {
		return purchaseGstType;
	}

	public void setPurchaseGstType(String purchaseGstType) {
		this.purchaseGstType = purchaseGstType;
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

	public Boolean getIsExempted() {
		return isExempted;
	}

	public void setIsExempted(Boolean isExempted) {
		this.isExempted = isExempted;
	}

	public String getExemptedType() {
		return exemptedType;
	}

	public void setExemptedType(String exemptedType) {
		this.exemptedType = exemptedType;
	}

	public Double getExmepted() {
		return exmepted;
	}

	public void setExmepted(Double exmepted) {
		this.exmepted = exmepted;
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

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getRetailPrice() {
		return retailPrice;
	}

	public void setRetailPrice(Double retailPrice) {
		this.retailPrice = retailPrice;
	}

	public Double getClosingStock() {
		return closingStock;
	}

	public void setClosingStock(Double closingStock) {
		this.closingStock = closingStock;
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

	public List<StockAdjustments> getStocks() {
		return stocks;
	}

	public void setStocks(List<StockAdjustments> stocks) {
		this.stocks = stocks;
	}

	public Double getTotInQty() {
		return totInQty;
	}

	public void setTotInQty(Double totInQty) {
		this.totInQty = totInQty;
	}

	public Double getTotOutQty() {
		return totOutQty;
	}

	public void setTotOutQty(Double totOutQty) {
		this.totOutQty = totOutQty;
	}

	public Double getTotBalQty() {
		return totBalQty;
	}

	public void setTotBalQty(Double totBalQty) {
		this.totBalQty = totBalQty;
	}
	
	
}
