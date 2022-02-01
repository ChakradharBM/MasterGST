/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Dealer and Feature Configuration information.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
@Document(collection = "delearfeature")
public class DealerConfig extends Base {
	private String deleartype;
	private String featureid;

	public String getDeleartype() {
		return deleartype;
	}

	public void setDeleartype(String deleartype) {
		this.deleartype = deleartype;
	}

	public String getFeatureid() {
		return featureid;
	}

	public void setFeatureid(String featureid) {
		this.featureid = featureid;
	}

}
