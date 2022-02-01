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
 * GSTR1 Nil Sup Items information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRNilSupItems {

	@Id
	private ObjectId id;
	
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cpddr;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double exptdsply;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double ngsply;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double nilsply;
	
	public GSTRNilSupItems() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getCpddr() {
		return cpddr;
	}

	public void setCpddr(Double cpddr) {
		this.cpddr = cpddr;
	}

	public Double getExptdsply() {
		return exptdsply;
	}

	public void setExptdsply(Double exptdsply) {
		this.exptdsply = exptdsply;
	}

	public Double getNgsply() {
		return ngsply;
	}

	public void setNgsply(Double ngsply) {
		this.ngsply = ngsply;
	}

	public Double getNilsply() {
		return nilsply;
	}

	public void setNilsply(Double nilsply) {
		this.nilsply = nilsply;
	}

}
