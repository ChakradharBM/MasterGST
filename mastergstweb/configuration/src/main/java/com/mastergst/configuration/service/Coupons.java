/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;
/**
 * Coupon Configuration information.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */

@Document(collection = "coupons")
public class Coupons extends Base{
	
	private String code;
	private Integer value;
	private Integer disCountValue;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public Integer getDisCountValue() {
		return disCountValue;
	}
	public void setDisCountValue(Integer disCountValue) {
		this.disCountValue = disCountValue;
	}
	
	

}
