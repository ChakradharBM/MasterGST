/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain.gstr6;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * GSTR6 Late fee Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR6LatefeeDetails {

	@Id
	private ObjectId id;

	private String debitId;
	
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double iLamt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cLamt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sLamt;
	private GSTR6OffsetDetails foroffset;

	public GSTR6LatefeeDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getDebitId() {
		return debitId;
	}

	public void setDebitId(String debitId) {
		this.debitId = debitId;
	}

	public Double getiLamt() {
		return iLamt;
	}

	public void setiLamt(Double iLamt) {
		this.iLamt = iLamt;
	}

	public Double getcLamt() {
		return cLamt;
	}

	public void setcLamt(Double cLamt) {
		this.cLamt = cLamt;
	}

	public Double getsLamt() {
		return sLamt;
	}

	public void setsLamt(Double sLamt) {
		this.sLamt = sLamt;
	}

	public GSTR6OffsetDetails getForoffset() {
		return foroffset;
	}

	public void setForoffset(GSTR6OffsetDetails foroffset) {
		this.foroffset = foroffset;
	}

}
