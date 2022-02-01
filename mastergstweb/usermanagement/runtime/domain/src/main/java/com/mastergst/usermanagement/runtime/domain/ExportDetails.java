/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * ExportDetails Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportDetails {
	@JsonProperty("ShipBNo")
	private String shipBNo;
	@JsonProperty("ShipBDt")
	private String shipBDt;
	@JsonProperty("Port")
	private String port;
	
	@JsonProperty("RefClm")
	private String refClm;
	@JsonProperty("ForCur")
	private String forCur;
	@JsonProperty("CntCode")
	private String cntCode;
	@JsonProperty("ExpDuty")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double expDuty;
	public String getShipBNo() {
		return shipBNo;
	}
	public void setShipBNo(String shipBNo) {
		this.shipBNo = shipBNo;
	}
	public String getShipBDt() {
		return shipBDt;
	}
	public void setShipBDt(String shipBDt) {
		this.shipBDt = shipBDt;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getRefClm() {
		return refClm;
	}
	public void setRefClm(String refClm) {
		this.refClm = refClm;
	}
	public String getForCur() {
		return forCur;
	}
	public void setForCur(String forCur) {
		this.forCur = forCur;
	}
	public String getCntCode() {
		return cntCode;
	}
	public void setCntCode(String cntCode) {
		this.cntCode = cntCode;
	}
	public Double getExpDuty() {
		return expDuty;
	}
	public void setExpDuty(Double expDuty) {
		this.expDuty = expDuty;
	}
	
	
}
