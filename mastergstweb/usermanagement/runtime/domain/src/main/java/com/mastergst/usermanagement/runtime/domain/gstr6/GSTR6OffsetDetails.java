/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain.gstr6;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR6 Offset Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR6OffsetDetails {

	@Id
	private ObjectId id;

	@JsonProperty("liab_id")
	private Integer liabId;
	@JsonProperty("tran_cd")
	private Integer tranCd;

	public GSTR6OffsetDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Integer getLiabId() {
		return liabId;
	}

	public void setLiabId(Integer liabId) {
		this.liabId = liabId;
	}

	public Integer getTranCd() {
		return tranCd;
	}

	public void setTranCd(Integer tranCd) {
		this.tranCd = tranCd;
	}

}
