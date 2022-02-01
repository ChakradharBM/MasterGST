/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.common.collect.Lists;
import com.mastergst.core.domain.Base;

/**
 * Customer information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "groupdetails")
public class GroupDetails {


	  @Id 
	  private ObjectId id;
	 

	private String userid;
	private String clientid;
	private String groupname;
	private String headname;

	private boolean readonly;

	private String name;
	private String path;

	private Amounts amounts;

	private List<Subgroups> subgroups = Lists.newArrayList();

	public GroupDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getHeadname() {
		return headname;
	}

	public void setHeadname(String headname) {
		this.headname = headname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Subgroups> getSubgroups() {
		return (List<Subgroups>)subgroups;
	}

	public void setSubgroups(List<Subgroups> subgroups) {
		this.subgroups = subgroups;
	}

	public Amounts getAmounts() {
		return amounts;
	}

	public void setAmounts(Amounts amounts) {
		this.amounts = amounts;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
}
