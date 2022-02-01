/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastergst.usermanagement.runtime.domain.GSTR1;

/**
 * This class MasterGST GSTR1 response POJO.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GSTR1Response extends GSTRResponse {
	
	@JsonProperty("data")
	private GSTR1 data;

	public GSTR1Response() {
	}

	public GSTR1 getData() {
		return data;
	}

	public void setData(GSTR1 data) {
		this.data = data;
	}

}
