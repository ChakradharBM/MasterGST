package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.mastergst.core.domain.Base;
@Document(collection = "stockAdjustments")
public class StockAdjustments extends Base{
	private String userid;
	private String fullname;
	private String clientid;
	private String transactionType;
	private String stockItemNo;
	private String stockItemName;
	private String stockItemGrpCode;
	private String stockItemGrpName;
	private Double currentStock;
	private String stockMovement;
	private Double stockItemQty;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dateOfMovement;
	private String stockPoNo;
	private String stockUnit;
	private Double stockPurchaseCost;
	private String stockVendorName;
	private String stockComments;
	private ObjectId itemId;
	private String invoiceId;
	//private CompanyItems items;
	String mthCd;
	String qrtCd;
	String yrCd;
	int sftr;
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
	public String getStockItemNo() {
		return stockItemNo;
	}
	public void setStockItemNo(String stockItemNo) {
		this.stockItemNo = stockItemNo;
	}
	public String getStockItemName() {
		return stockItemName;
	}
	public void setStockItemName(String stockItemName) {
		this.stockItemName = stockItemName;
	}
	public String getStockItemGrpCode() {
		return stockItemGrpCode;
	}
	public void setStockItemGrpCode(String stockItemGrpCode) {
		this.stockItemGrpCode = stockItemGrpCode;
	}
	public String getStockItemGrpName() {
		return stockItemGrpName;
	}
	public void setStockItemGrpName(String stockItemGrpName) {
		this.stockItemGrpName = stockItemGrpName;
	}
	public Double getCurrentStock() {
		return currentStock;
	}
	public void setCurrentStock(Double currentStock) {
		this.currentStock = currentStock;
	}
	public String getStockMovement() {
		return stockMovement;
	}
	public void setStockMovement(String stockMovement) {
		this.stockMovement = stockMovement;
	}
	public Double getStockItemQty() {
		return stockItemQty;
	}
	public void setStockItemQty(Double stockItemQty) {
		this.stockItemQty = stockItemQty;
	}
	public Date getDateOfMovement() {
		return dateOfMovement;
	}
	public void setDateOfMovement(Date dateOfMovement) {
		this.dateOfMovement = dateOfMovement;
	}
	public String getStockPoNo() {
		return stockPoNo;
	}
	public void setStockPoNo(String stockPoNo) {
		this.stockPoNo = stockPoNo;
	}
	public Double getStockPurchaseCost() {
		return stockPurchaseCost;
	}
	public void setStockPurchaseCost(Double stockPurchaseCost) {
		this.stockPurchaseCost = stockPurchaseCost;
	}
	public String getStockVendorName() {
		return stockVendorName;
	}
	public void setStockVendorName(String stockVendorName) {
		this.stockVendorName = stockVendorName;
	}
	public String getStockComments() {
		return stockComments;
	}
	public void setStockComments(String stockComments) {
		this.stockComments = stockComments;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
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
	public String getStockUnit() {
		return stockUnit;
	}
	public void setStockUnit(String stockUnit) {
		this.stockUnit = stockUnit;
	}
	public ObjectId getItemId() {
		return itemId;
	}
	public void setItemId(ObjectId itemId) {
		this.itemId = itemId;
	}
	/*
	 * public CompanyItems getItems() { return items; } public void
	 * setItems(CompanyItems items) { this.items = items; }
	 */
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	
	
}
