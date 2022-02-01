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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * GSTR1 Advance Tax information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"id"})
public class GSTRAdvanceTax {

	@Id
	private ObjectId id;
	
	private String flag;
	private String pos;
	@JsonProperty("sply_ty")
	private String splyTy;
	@JsonProperty("error_msg")
	private String errorMsg;
	@JsonProperty("diff_percent")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double diffPercent;
	private String omon;
	private List<GSTRItemDetails> itms=LazyList.decorate(new ArrayList<GSTRItemDetails>(), 
			FactoryUtils.instantiateFactory(GSTRItemDetails.class));
	
	public GSTRAdvanceTax() {
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

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getSplyTy() {
		return splyTy;
	}

	public void setSplyTy(String splyTy) {
		this.splyTy = splyTy;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
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

	public String getOmon() {
		return omon;
	}

	public void setOmon(String omon) {
		this.omon = omon;
	}

}
