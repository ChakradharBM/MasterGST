/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * TransportDetails Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransportDetails {
	@JsonProperty("SupTyp")
	private String supTyp;
	@JsonProperty("RegRev")
	private String regRev;
	@JsonProperty("TaxSch")
	private String taxSch;
	@JsonProperty("EcmGstin")
	private String ecmGstin;
	@JsonProperty("IgstOnIntra")
	private String igstOnIntra;

	public String getSupTyp() {
		return supTyp;
	}

	public void setSupTyp(String supTyp) {
		this.supTyp = supTyp;
	}

	public String getRegRev() {
		return regRev;
	}

	public void setRegRev(String regRev) {
		this.regRev = regRev;
	}

	public String getTaxSch() {
		return taxSch;
	}

	public void setTaxSch(String taxSch) {
		this.taxSch = taxSch;
	}

	public String getEcmGstin() {
		return ecmGstin;
	}

	public void setEcmGstin(String ecmGstin) {
		this.ecmGstin = ecmGstin;
	}

	public String getIgstOnIntra() {
		return igstOnIntra;
	}

	public void setIgstOnIntra(String igstOnIntra) {
		this.igstOnIntra = igstOnIntra;
	}

	@Override
	public String toString() {
		return "TransportDetails [supTyp=" + supTyp + ", regRev=" + regRev + ", taxSch=" + taxSch + ", ecmGstin="
				+ ecmGstin + "]";
	}

}
