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
import com.google.common.collect.Lists;

/**
 * GSTR Ledger Provisional Credit Balance List information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRLedgerProvCreditBalanceList {

	@Id
	private ObjectId id;
	
	private List<GSTRLedgerProvisionalDetails> provCrdBal=Lists.newArrayList();
	
	public GSTRLedgerProvCreditBalanceList() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<GSTRLedgerProvisionalDetails> getProvCrdBal() {
		return provCrdBal;
	}

	public void setProvCrdBal(List<GSTRLedgerProvisionalDetails> provCrdBal) {
		this.provCrdBal = provCrdBal;
	}

}
