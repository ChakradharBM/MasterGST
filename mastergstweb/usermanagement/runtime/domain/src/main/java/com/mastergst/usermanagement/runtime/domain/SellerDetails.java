/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR4 Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SellerDetails {
	@JsonProperty("Gstin")
	private String gstin;
	@JsonProperty("LglNm")
	private String lglNm;
	@JsonProperty("TrdNm")
	private String trdNm;
	@JsonProperty("Addr1")
	private String addr1;
	@JsonProperty("Addr2")
	private String addr2;
	@JsonProperty("Loc")
	private String loc;
	@JsonProperty("Pin")
	private Number pin;
	@JsonProperty("Stcd")
	private String state;
	@JsonProperty("Ph")
	private String ph;
	@JsonProperty("Em")
	private String em;

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getLglNm() {
		return lglNm;
	}

	public void setLglNm(String lglNm) {
		this.lglNm = lglNm;
	}

	public String getTrdNm() {
		return trdNm;
	}

	public void setTrdNm(String trdNm) {
		this.trdNm = trdNm;
	}

	public String getAddr1() {
		return addr1;
	}

	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}

	public String getAddr2() {
		return addr2;
	}

	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public Number getPin() {
		return pin;
	}

	public void setPin(Number pin) {
		this.pin = pin;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPh() {
		return ph;
	}

	public void setPh(String ph) {
		this.ph = ph;
	}

	public String getEm() {
		return em;
	}

	public void setEm(String em) {
		this.em = em;
	}

	@Override
	public String toString() {
		return "SellerDetails [gstin=" + gstin + ", lglNm=" + lglNm + ", trdNm=" + trdNm + ", addr1=" + addr1
				+ ", addr2=" + addr2 + ", loc=" + loc + ", pin=" + pin + ", state=" + state + ", ph=" + ph + ", em="
				+ em + "]";
	}
}
