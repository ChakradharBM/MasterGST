/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastergst.usermanagement.runtime.domain.GSTR2;

/**
 * This class MasterGST GSTR2 response POJO.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GSTR2Response extends GSTRResponse {
	
	@JsonProperty("data")
	private GSTR2 data;

	public GSTR2Response() {
	}

	public GSTR2 getData() {
		return data;
	}

	public void setData(GSTR2 data) {
		this.data = data;
	}

}
