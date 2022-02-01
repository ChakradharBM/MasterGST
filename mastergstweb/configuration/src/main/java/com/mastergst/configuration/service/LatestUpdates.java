/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Message Configuration information.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
@Document(collection = "latest_updates")
public class LatestUpdates extends Base {

	private String description;
	private String updateId;
	private String title;
	private List<String> usertype;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUpdateId() {
		return updateId;
	}

	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getUsertype() {
		return usertype;
	}

	public void setUsertype(List<String> usertype) {
		this.usertype = usertype;
	}

	@Override
	public String toString() {
		return "LatestUpdates [description=" + description + ", updateId=" + updateId + ", title=" + title
				+ ", usertype=" + usertype + "]";
	}
}
