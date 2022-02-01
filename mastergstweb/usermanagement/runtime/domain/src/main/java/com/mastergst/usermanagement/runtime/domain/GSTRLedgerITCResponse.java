/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * GSTR Ledger ITC response information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRLedgerITCResponse extends LedgerParent {

	@Id
	private ObjectId id;
	
	private GSTRLedgerITCDetails itcLdgDtls;
	private GSTRLedgerProvCreditBalanceList provCrdBalList;
	
	public GSTRLedgerITCResponse() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public GSTRLedgerITCDetails getItcLdgDtls() {
		return itcLdgDtls;
	}

	public void setItcLdgDtls(GSTRLedgerITCDetails itcLdgDtls) {
		this.itcLdgDtls = itcLdgDtls;
	}

	public GSTRLedgerProvCreditBalanceList getProvCrdBalList() {
		return provCrdBalList;
	}

	public void setProvCrdBalList(GSTRLedgerProvCreditBalanceList provCrdBalList) {
		this.provCrdBalList = provCrdBalList;
	}

}
