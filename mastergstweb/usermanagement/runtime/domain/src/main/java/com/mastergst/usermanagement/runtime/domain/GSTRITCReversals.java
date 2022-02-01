/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR1 ITC Reversals information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRITCReversals {

	@Id
	private ObjectId id;
	
	private String flag;
	private String chksum;
	@JsonProperty("error_msg")
	private String errorMsg;
	@JsonProperty("rule2_2")
	private GSTRItemDetails rule22;
	@JsonProperty("rule7_1_m")
	private GSTRItemDetails rule71m;
	@JsonProperty("rule8_1_h")
	private GSTRItemDetails rule81h;
	@JsonProperty("rule7_2_a")
	private GSTRItemDetails rule72a;
	@JsonProperty("rule7_2_b")
	private GSTRItemDetails rule72b;
	private GSTRItemDetails revitc;
	private GSTRItemDetails other;
	
	public GSTRITCReversals() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public GSTRItemDetails getRule22() {
		return rule22;
	}

	public void setRule22(GSTRItemDetails rule22) {
		this.rule22 = rule22;
	}

	public GSTRItemDetails getRule71m() {
		return rule71m;
	}

	public void setRule71m(GSTRItemDetails rule71m) {
		this.rule71m = rule71m;
	}

	public GSTRItemDetails getRule81h() {
		return rule81h;
	}

	public void setRule81h(GSTRItemDetails rule81h) {
		this.rule81h = rule81h;
	}

	public GSTRItemDetails getRule72a() {
		return rule72a;
	}

	public void setRule72a(GSTRItemDetails rule72a) {
		this.rule72a = rule72a;
	}

	public GSTRItemDetails getRule72b() {
		return rule72b;
	}

	public void setRule72b(GSTRItemDetails rule72b) {
		this.rule72b = rule72b;
	}

	public GSTRItemDetails getRevitc() {
		return revitc;
	}

	public void setRevitc(GSTRItemDetails revitc) {
		this.revitc = revitc;
	}

	public GSTRItemDetails getOther() {
		return other;
	}

	public void setOther(GSTRItemDetails other) {
		this.other = other;
	}

}
