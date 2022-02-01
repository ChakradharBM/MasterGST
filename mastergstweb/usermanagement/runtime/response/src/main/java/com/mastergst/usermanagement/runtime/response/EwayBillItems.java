/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EwayBillItems {
	@Id
	private ObjectId id;
	
	@JsonProperty("itemNo")
	private Number itemNo;
	@JsonProperty("productId")
	private Number productId;
	@JsonProperty("productName")
	private String productName;
	@JsonProperty("productDesc")
	private String productDesc;
	@JsonProperty("hsnCode")
	private Number hsnCode;
	@JsonProperty("quantity")
	private Double quantity;
	@JsonProperty("qtyUnit")
	private String qtyUnit;
	@JsonProperty("cgstRate")
	private Double cgstRate;
	@JsonProperty("sgstRate")
	private Double sgstRate;
	@JsonProperty("igstRate")
	private Double igstRate;
	@JsonProperty("cessRate")
	private Double cessRate;
	@JsonProperty("cessNonAdvol")
	private Double cessNonAdvol;
	@JsonProperty("taxableAmount")
	private Double taxableAmount;
	private Double totalAmount;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public Number getItemNo() {
		return itemNo;
	}
	public void setItemNo(Number itemNo) {
		this.itemNo = itemNo;
	}
	public Number getProductId() {
		return productId;
	}
	public void setProductId(Number productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public Number getHsnCode() {
		return hsnCode;
	}
	public void setHsnCode(Number hsnCode) {
		this.hsnCode = hsnCode;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public String getQtyUnit() {
		return qtyUnit;
	}
	public void setQtyUnit(String qtyUnit) {
		this.qtyUnit = qtyUnit;
	}
	public Double getCgstRate() {
		return cgstRate;
	}
	public void setCgstRate(Double cgstRate) {
		this.cgstRate = cgstRate;
	}
	public Double getSgstRate() {
		return sgstRate;
	}
	public void setSgstRate(Double sgstRate) {
		this.sgstRate = sgstRate;
	}
	public Double getIgstRate() {
		return igstRate;
	}
	public void setIgstRate(Double igstRate) {
		this.igstRate = igstRate;
	}
	public Double getCessRate() {
		return cessRate;
	}
	public void setCessRate(Double cessRate) {
		this.cessRate = cessRate;
	}
	public Double getCessNonAdvol() {
		return cessNonAdvol;
	}
	public void setCessNonAdvol(Double cessNonAdvol) {
		this.cessNonAdvol = cessNonAdvol;
	}
	public Double getTaxableAmount() {
		return taxableAmount;
	}
	public void setTaxableAmount(Double taxableAmount) {
		this.taxableAmount = taxableAmount;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	
}
