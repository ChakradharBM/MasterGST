/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * GSTR4 Tax Summary information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GSTR4TaxSummary {

	@JsonProperty("tax_pay")
	private List<GSTRLedgerBalanceSummary> taxPay=Lists.newArrayList();
	@JsonProperty("pd_by_cash")
	private List<GSTRLedgerBalanceSummary> pdByCash=Lists.newArrayList();
	
	public List<GSTRLedgerBalanceSummary> getTaxPay() {
		return taxPay;
	}
	public void setTaxPay(List<GSTRLedgerBalanceSummary> taxPay) {
		this.taxPay = taxPay;
	}
	public List<GSTRLedgerBalanceSummary> getPdByCash() {
		return pdByCash;
	}
	public void setPdByCash(List<GSTRLedgerBalanceSummary> pdByCash) {
		this.pdByCash = pdByCash;
	}
	
}
