/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastergst.usermanagement.runtime.domain.GSTR6;

/**
 * This class MasterGST GSTR6 response POJO.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GSTR6Response extends GSTRResponse {
	
	@JsonProperty("data")
	private GSTR6 data;

	public GSTR6Response() {
	}

	public GSTR6 getData() {
		return data;
	}

	public void setData(GSTR6 data) {
		this.data = data;
	}

}
