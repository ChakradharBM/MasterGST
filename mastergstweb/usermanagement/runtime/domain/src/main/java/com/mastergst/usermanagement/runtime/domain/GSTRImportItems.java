/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * GSTR1 Import Item Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRImportItems {

	@Id
	private ObjectId id;
	
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double rt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double txval;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double iamt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double csamt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	@JsonProperty("tx_i")
	private Double iTax;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	@JsonProperty("tx_cs")
	private Double csTax;
	private Long inum;
	private String idt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double ival;
	private String pos;
	
	private String elg;
	private Integer num;

	public GSTRImportItems() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getRt() {
		return rt;
	}

	public void setRt(Double rt) {
		this.rt = rt;
	}

	public Double getTxval() {
		return txval;
	}

	public void setTxval(Double txval) {
		this.txval = txval;
	}

	public Double getIamt() {
		return iamt;
	}

	public void setIamt(Double iamt) {
		this.iamt = iamt;
	}

	public Double getCsamt() {
		return csamt;
	}

	public void setCsamt(Double csamt) {
		this.csamt = csamt;
	}

	public Double getiTax() {
		return iTax;
	}

	public void setiTax(Double iTax) {
		this.iTax = iTax;
	}

	public Double getCsTax() {
		return csTax;
	}

	public void setCsTax(Double csTax) {
		this.csTax = csTax;
	}

	public Long getInum() {
		return inum;
	}

	public void setInum(Long inum) {
		this.inum = inum;
	}

	public String getIdt() {
		return idt;
	}

	public void setIdt(String idt) {
		this.idt = idt;
	}

	public Double getIval() {
		return ival;
	}

	public void setIval(Double ival) {
		this.ival = ival;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getElg() {
		return elg;
	}

	public void setElg(String elg) {
		this.elg = elg;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

}
