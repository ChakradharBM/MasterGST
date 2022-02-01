/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR4 Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeDetails {
	@JsonProperty("Nm")
	private String nm;
	@JsonProperty("Val")
	private String val;
	public String getNm() {
		return nm;
	}
	public void setNm(String nm) {
		this.nm = nm;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	
	
	
	
}
