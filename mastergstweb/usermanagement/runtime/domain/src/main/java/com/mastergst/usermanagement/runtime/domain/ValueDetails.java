/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * GSTR4 Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValueDetails {
	@JsonProperty("AssVal")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double assVal;
	@JsonProperty("SgstVal")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sgstVal;
	@JsonProperty("CgstVal")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cgstVal;
	@JsonProperty("IgstVal")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double igstVal;
	@JsonProperty("CesVal")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cesVal;
	@JsonProperty("StCesVal")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double stCesVal;
	@JsonProperty("Discount")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double discount;
	@JsonProperty("OthChrg")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double othChrg;
	@JsonProperty("RndOffAmt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double rndOffAmt;
	@JsonProperty("TotInvValFc")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double totInvValFc;

	@JsonProperty("TotInvVal")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double totInvVal;

	public Double getAssVal() {
		return assVal;
	}

	public void setAssVal(Double assVal) {
		this.assVal = assVal;
	}

	public Double getSgstVal() {
		return sgstVal;
	}

	public void setSgstVal(Double sgstVal) {
		this.sgstVal = sgstVal;
	}

	public Double getCgstVal() {
		return cgstVal;
	}

	public void setCgstVal(Double cgstVal) {
		this.cgstVal = cgstVal;
	}

	public Double getIgstVal() {
		return igstVal;
	}

	public void setIgstVal(Double igstVal) {
		this.igstVal = igstVal;
	}

	public Double getCesVal() {
		return cesVal;
	}

	public void setCesVal(Double cesVal) {
		this.cesVal = cesVal;
	}

	public Double getStCesVal() {
		return stCesVal;
	}

	public void setStCesVal(Double stCesVal) {
		this.stCesVal = stCesVal;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getOthChrg() {
		return othChrg;
	}

	public void setOthChrg(Double othChrg) {
		this.othChrg = othChrg;
	}

	public Double getRndOffAmt() {
		return rndOffAmt;
	}

	public void setRndOffAmt(Double rndOffAmt) {
		this.rndOffAmt = rndOffAmt;
	}

	public Double getTotInvValFc() {
		return totInvValFc;
	}

	public void setTotInvValFc(Double totInvValFc) {
		this.totInvValFc = totInvValFc;
	}

	public Double getTotInvVal() {
		return totInvVal;
	}

	public void setTotInvVal(Double totInvVal) {
		this.totInvVal = totInvVal;
	}

	@Override
	public String toString() {
		return "ValueDetails [assVal=" + assVal + ", sgstVal=" + sgstVal + ", cgstVal=" + cgstVal + ", igstVal="
				+ igstVal + ", cesVal=" + cesVal + ", stCesVal=" + stCesVal + ", rndOffAmt=" + rndOffAmt
				+ ", totInvValFc=" + totInvValFc + ", totInvVal=" + totInvVal + "]";
	}
}
