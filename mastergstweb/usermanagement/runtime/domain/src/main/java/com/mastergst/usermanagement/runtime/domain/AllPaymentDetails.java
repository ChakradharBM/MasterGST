/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.google.common.collect.Lists;

/**
 * Branch information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public class AllPaymentDetails {

	private List<String> month;
	
	private List<String> year;

	public List<String> getMonth() {
		return month;
	}

	public void setMonth(List<String> month) {
		this.month = month;
	}

	public List<String> getYear() {
		return year;
	}

	public void setYear(List<String> year) {
		this.year = year;
	}
	
}
