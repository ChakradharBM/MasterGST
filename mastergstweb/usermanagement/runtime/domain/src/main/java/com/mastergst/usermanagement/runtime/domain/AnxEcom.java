/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * Anx Ecom information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class AnxEcom {

	@Id
	private ObjectId id;
	
	private String etin;
	private String flag;
	private String chksum;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sup;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double supr;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double nsup;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double igst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cess;




	public String getEtin() {
		return etin;
	}

	public void setEtin(String etin) {
		this.etin = etin;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public Double getSup() {
		return sup;
	}

	public void setSup(Double sup) {
		this.sup = sup;
	}

	public Double getSupr() {
		return supr;
	}

	public void setSupr(Double supr) {
		this.supr = supr;
	}

	public Double getNsup() {
		return nsup;
	}

	public void setNsup(Double nsup) {
		this.nsup = nsup;
	}

	public Double getIgst() {
		return igst;
	}

	public void setIgst(Double igst) {
		this.igst = igst;
	}

	public Double getCgst() {
		return cgst;
	}

	public void setCgst(Double cgst) {
		this.cgst = cgst;
	}

	public Double getSgst() {
		return sgst;
	}

	public void setSgst(Double sgst) {
		this.sgst = sgst;
	}

	public Double getCess() {
		return cess;
	}

	public void setCess(Double cess) {
		this.cess = cess;
	}

	public AnxEcom() {
		this.id = ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}
}
