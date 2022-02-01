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

/**
 * GSTR1 Item Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRItems {

	@Id
	private ObjectId id;
	
	private Integer num;
	
	@JsonProperty("itm_det")
	private GSTRItemDetails item=new GSTRItemDetails();
	
	private GSTRITCDetails itc;
	
	public GSTRItems() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public GSTRItemDetails getItem() {
		return item;
	}

	public void setItem(GSTRItemDetails item) {
		this.item = item;
	}

	public GSTRITCDetails getItc() {
		return itc;
	}

	public void setItc(GSTRITCDetails itc) {
		this.itc = itc;
	}

}
