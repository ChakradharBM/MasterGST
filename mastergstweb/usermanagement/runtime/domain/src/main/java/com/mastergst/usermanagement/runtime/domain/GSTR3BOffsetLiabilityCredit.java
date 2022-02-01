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
 * GSTR3B Offset Liability Credit Paid information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR3BOffsetLiabilityCredit {

	@Id
	private ObjectId id;

	@JsonProperty("liab_ldg_id")
	private Integer liabLdgId;
	@JsonProperty("trans_typ")
	private Integer transType;
	@JsonProperty("i_pdi")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double igstPdigst;
	@JsonProperty("i_pdc")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double igstPdcgst;
	@JsonProperty("i_pds")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double igstPdsgst;
	@JsonProperty("c_pdi")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cgstPdigst;
	@JsonProperty("c_pdc")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cgstPdcgst;
	@JsonProperty("s_pdi")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sgstPdigst;
	@JsonProperty("s_pds")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sgstPdsgst;
	@JsonProperty("cs_pdcs")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cessPdcess;
	
	public GSTR3BOffsetLiabilityCredit() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getIgstPdigst() {
		return igstPdigst;
	}

	public void setIgstPdigst(Double igstPdigst) {
		this.igstPdigst = igstPdigst;
	}

	public Double getIgstPdcgst() {
		return igstPdcgst;
	}

	public void setIgstPdcgst(Double igstPdcgst) {
		this.igstPdcgst = igstPdcgst;
	}

	public Double getIgstPdsgst() {
		return igstPdsgst;
	}

	public void setIgstPdsgst(Double igstPdsgst) {
		this.igstPdsgst = igstPdsgst;
	}

	public Double getCgstPdigst() {
		return cgstPdigst;
	}

	public void setCgstPdigst(Double cgstPdigst) {
		this.cgstPdigst = cgstPdigst;
	}

	public Double getCgstPdcgst() {
		return cgstPdcgst;
	}

	public void setCgstPdcgst(Double cgstPdcgst) {
		this.cgstPdcgst = cgstPdcgst;
	}

	public Double getSgstPdigst() {
		return sgstPdigst;
	}

	public void setSgstPdigst(Double sgstPdigst) {
		this.sgstPdigst = sgstPdigst;
	}

	public Double getSgstPdsgst() {
		return sgstPdsgst;
	}

	public void setSgstPdsgst(Double sgstPdsgst) {
		this.sgstPdsgst = sgstPdsgst;
	}

	public Double getCessPdcess() {
		return cessPdcess;
	}

	public void setCessPdcess(Double cessPdcess) {
		this.cessPdcess = cessPdcess;
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

}
