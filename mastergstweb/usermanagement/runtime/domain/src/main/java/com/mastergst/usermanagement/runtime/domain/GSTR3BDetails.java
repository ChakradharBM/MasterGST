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
 * GSTR3B Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR3BDetails {

	@Id
	private ObjectId id;

	private String pos;
	private String ty;
	
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double txval;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double iamt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double camt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double samt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double csamt;
	
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double inter;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double intra;

	public GSTR3BDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getTy() {
		return ty;
	}

	public void setTy(String ty) {
		this.ty = ty;
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

	public Double getCamt() {
		return camt;
	}

	public void setCamt(Double camt) {
		this.camt = camt;
	}

	public Double getSamt() {
		return samt;
	}

	public void setSamt(Double samt) {
		this.samt = samt;
	}

	public Double getCsamt() {
		return csamt;
	}

	public void setCsamt(Double csamt) {
		this.csamt = csamt;
	}

	public Double getInter() {
		return inter;
	}

	public void setInter(Double inter) {
		this.inter = inter;
	}

	public Double getIntra() {
		return intra;
	}

	public void setIntra(Double intra) {
		this.intra = intra;
	}

	@Override
	public String toString() {
		return "GSTR3BDetails [id=" + id + ", pos=" + pos + ", ty=" + ty + ", txval=" + txval + ", iamt=" + iamt
				+ ", camt=" + camt + ", samt=" + samt + ", csamt=" + csamt + ", inter=" + inter + ", intra=" + intra
				+ "]";
	}

}
