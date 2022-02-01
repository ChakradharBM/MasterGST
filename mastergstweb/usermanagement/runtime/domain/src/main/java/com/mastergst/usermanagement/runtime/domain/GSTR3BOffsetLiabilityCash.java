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
 * GSTR3B Offset Liability Cash Paid information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR3BOffsetLiabilityCash {

	@Id
	private ObjectId id;

	@JsonProperty("liab_ldg_id")
	private Integer liabLdgId;
	@JsonProperty("trans_typ")
	private Integer transType;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double ipd;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cpd;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double spd;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cspd;
	@JsonProperty("i_intrpd")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double igstIntrpd;
	@JsonProperty("c_intrpd")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cgstIntrpd;
	@JsonProperty("s_intrpd")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sgstIntrpd;
	@JsonProperty("cs_intrpd")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cessIntrpd;
	@JsonProperty("c_lfeepd")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cgstLfeepd;
	@JsonProperty("s_lfeepd")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sgstLfeepd;
	
	public GSTR3BOffsetLiabilityCash() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Integer getLiabLdgId() {
		return liabLdgId;
	}

	public void setLiabLdgId(Integer liabLdgId) {
		this.liabLdgId = liabLdgId;
	}

	public Integer getTransType() {
		return transType;
	}

	public void setTransType(Integer transType) {
		this.transType = transType;
	}

	public Double getIpd() {
		return ipd;
	}

	public void setIpd(Double ipd) {
		this.ipd = ipd;
	}

	public Double getCpd() {
		return cpd;
	}

	public void setCpd(Double cpd) {
		this.cpd = cpd;
	}

	public Double getSpd() {
		return spd;
	}

	public void setSpd(Double spd) {
		this.spd = spd;
	}

	public Double getCspd() {
		return cspd;
	}

	public void setCspd(Double cspd) {
		this.cspd = cspd;
	}

	public Double getIgstIntrpd() {
		return igstIntrpd;
	}

	public void setIgstIntrpd(Double igstIntrpd) {
		this.igstIntrpd = igstIntrpd;
	}

	public Double getCgstIntrpd() {
		return cgstIntrpd;
	}

	public void setCgstIntrpd(Double cgstIntrpd) {
		this.cgstIntrpd = cgstIntrpd;
	}

	public Double getSgstIntrpd() {
		return sgstIntrpd;
	}

	public void setSgstIntrpd(Double sgstIntrpd) {
		this.sgstIntrpd = sgstIntrpd;
	}

	public Double getCessIntrpd() {
		return cessIntrpd;
	}

	public void setCessIntrpd(Double cessIntrpd) {
		this.cessIntrpd = cessIntrpd;
	}

	public Double getCgstLfeepd() {
		return cgstLfeepd;
	}

	public void setCgstLfeepd(Double cgstLfeepd) {
		this.cgstLfeepd = cgstLfeepd;
	}

	public Double getSgstLfeepd() {
		return sgstLfeepd;
	}

	public void setSgstLfeepd(Double sgstLfeepd) {
		this.sgstLfeepd = sgstLfeepd;
	}

}
