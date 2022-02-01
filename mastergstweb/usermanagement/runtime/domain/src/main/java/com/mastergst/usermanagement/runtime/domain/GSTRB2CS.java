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
 * GSTR1 B2CS Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRB2CS {

	@Id
	private ObjectId id;
	
	private String flag;
	@JsonProperty("sply_ty")
	private String splyTy;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double rt;
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
	private String pos;
	private String typ;
	private String etin;
	@JsonProperty("error_msg")
	private String errorMsg;
	@JsonProperty("diff_percent")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double diffPercent;

	private String omon;

	public GSTRB2CS() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getSplyTy() {
		return splyTy;
	}

	public void setSplyTy(String splyTy) {
		this.splyTy = splyTy;
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

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public String getEtin() {
		return etin;
	}

	public void setEtin(String etin) {
		this.etin = etin;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Double getDiffPercent() {
		return diffPercent;
	}
	public void setDiffPercent(Double diffPercent) {
		this.diffPercent = diffPercent;
	}

	public String getOmon() {
		return omon;
	}

	public void setOmon(String omon) {
		this.omon = omon;
	}
}
