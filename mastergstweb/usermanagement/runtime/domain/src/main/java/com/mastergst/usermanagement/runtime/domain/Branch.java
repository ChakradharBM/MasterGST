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
public class Branch {

	@Id
	private ObjectId id;

	private String code;
	private String name;
	private String address;
	private List<?> subbranches = Lists.newArrayList();

	public Branch() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<Branch> getSubbranches() {
		return (List<Branch>) subbranches;
	}

	public void setSubbranches(List<Branch> subbranches) {
		this.subbranches = subbranches;
	}

}
