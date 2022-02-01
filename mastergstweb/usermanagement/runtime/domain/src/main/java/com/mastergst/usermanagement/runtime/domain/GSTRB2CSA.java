/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR1 B2CS Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({ "id" })
public class GSTRB2CSA {

	@Id
	private ObjectId id;

	private String flag;
	@JsonProperty("sply_ty")
	private String splyTy;
	private String pos;
	private String typ;
	private String omon;

	@JsonProperty("diff_percent")
	private Double diffPercent;

	private List<GSTRItemDetails> itms = LazyList.decorate(new ArrayList<GSTRItemDetails>(),
			FactoryUtils.instantiateFactory(GSTRItemDetails.class));

	public GSTRB2CSA() {
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

	public String getOmon() {
		return omon;
	}

	public void setOmon(String omon) {
		this.omon = omon;
	}

	public List<GSTRItemDetails> getItms() {
		return itms;
	}

	public void setItms(List<GSTRItemDetails> itms) {
		this.itms = itms;
	}

	public Double getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(Double diffPercent) {
		this.diffPercent = diffPercent;
	}
}
