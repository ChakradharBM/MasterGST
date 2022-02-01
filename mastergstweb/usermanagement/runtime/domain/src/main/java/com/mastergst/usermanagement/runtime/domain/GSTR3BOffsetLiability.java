/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * GSTR3B Offset Liability information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR3BOffsetLiability {

	@Id
	private ObjectId id;

	private List<GSTR3BOffsetLiabilityCash> pdcash = Lists.newArrayList();
	private GSTR3BOffsetLiabilityCredit pditc;
	@JsonProperty("tx_py")
	private List<GSTR3BTaxPayment> taxPayable = Lists.newArrayList();
	
	public GSTR3BOffsetLiability() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<GSTR3BOffsetLiabilityCash> getPdcash() {
		return pdcash;
	}

	public void setPdcash(List<GSTR3BOffsetLiabilityCash> pdcash) {
		this.pdcash = pdcash;
	}

	public GSTR3BOffsetLiabilityCredit getPditc() {
		return pditc;
	}

	public void setPditc(GSTR3BOffsetLiabilityCredit pditc) {
		this.pditc = pditc;
	}

	public List<GSTR3BTaxPayment> getTaxPayable() {
		return taxPayable;
	}

	public void setTaxPayable(List<GSTR3BTaxPayment> taxPayable) {
		this.taxPayable = taxPayable;
	}

}
