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
public class EwayBillDetails {
	@JsonProperty("TransId")
	private String transId;
	@JsonProperty("TransName")
	private String transName;
	@JsonProperty("TransMode")
	private String transMode;
	
	@JsonProperty("Distance")
	private String distance;
	@JsonProperty("TransDocNo")
	private String transDocNo;
	@JsonProperty("TransDocDt")
	private String TransDocDt;
	@JsonProperty("VehNo")
	private String vehNo;
	@JsonProperty("VehType")
	private String vehType;
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	public String getTransName() {
		return transName;
	}
	public void setTransName(String transName) {
		this.transName = transName;
	}
	public String getTransMode() {
		return transMode;
	}
	public void setTransMode(String transMode) {
		this.transMode = transMode;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getTransDocNo() {
		return transDocNo;
	}
	public void setTransDocNo(String transDocNo) {
		this.transDocNo = transDocNo;
	}
	public String getTransDocDt() {
		return TransDocDt;
	}
	public void setTransDocDt(String transDocDt) {
		TransDocDt = transDocDt;
	}
	public String getVehNo() {
		return vehNo;
	}
	public void setVehNo(String vehNo) {
		this.vehNo = vehNo;
	}
	public String getVehType() {
		return vehType;
	}
	public void setVehType(String vehType) {
		this.vehType = vehType;
	}
	
	
}
