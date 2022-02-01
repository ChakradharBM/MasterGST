/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GSTINPublicAddress {
	
	private GSTINPublicAddressData addr=new GSTINPublicAddressData();
	private String ntr;
	public GSTINPublicAddressData getAddr() {
		return addr;
	}
	public void setAddr(GSTINPublicAddressData addr) {
		this.addr = addr;
	}
	public String getNtr() {
		return ntr;
	}
	public void setNtr(String ntr) {
		this.ntr = ntr;
	}

}
