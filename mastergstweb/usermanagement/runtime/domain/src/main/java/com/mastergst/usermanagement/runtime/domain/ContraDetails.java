/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR4 Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContraDetails {
	@JsonProperty("RecAdvRefr")
	private String recAdvRefr;
	@JsonProperty("RecAdvDt")
	private String recAdvDt;
	@JsonProperty("TendRefr")
	private String tendRefr;
	
	@JsonProperty("ContrRefr")
	private String contrRefr;
	@JsonProperty("ExtRefr")
	private String extRefr;
	@JsonProperty("ProjRefr")
	private String projRefr;
	
	@JsonProperty("PORefr")
	private String pORefr;
	@JsonProperty("PORefDt")
	private String pORefDt;
	public String getRecAdvRefr() {
		return recAdvRefr;
	}
	public void setRecAdvRefr(String recAdvRefr) {
		this.recAdvRefr = recAdvRefr;
	}
	public String getRecAdvDt() {
		return recAdvDt;
	}
	public void setRecAdvDt(String recAdvDt) {
		this.recAdvDt = recAdvDt;
	}
	public String getTendRefr() {
		return tendRefr;
	}
	public void setTendRefr(String tendRefr) {
		this.tendRefr = tendRefr;
	}
	public String getContrRefr() {
		return contrRefr;
	}
	public void setContrRefr(String contrRefr) {
		this.contrRefr = contrRefr;
	}
	public String getExtRefr() {
		return extRefr;
	}
	public void setExtRefr(String extRefr) {
		this.extRefr = extRefr;
	}
	public String getProjRefr() {
		return projRefr;
	}
	public void setProjRefr(String projRefr) {
		this.projRefr = projRefr;
	}
	public String getpORefr() {
		return pORefr;
	}
	public void setpORefr(String pORefr) {
		this.pORefr = pORefr;
	}
	public String getpORefDt() {
		return pORefDt;
	}
	public void setpORefDt(String pORefDt) {
		this.pORefDt = pORefDt;
	}
	
	
}
