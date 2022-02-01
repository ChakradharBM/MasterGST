package com.mastergst.configuration.service;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Subgroup{
	 @Id 
	  private ObjectId id;
	 
	private String subgroupname;
	private String subgrouppath;
	
	public Subgroup() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getSubgrouppath() {
		return subgrouppath;
	}

	public void setSubgrouppath(String subgrouppath) {
		this.subgrouppath = subgrouppath;
	}

	public String getSubgroupname() {
		return subgroupname;
	}

	public void setSubgroupname(String subgroupname) {
		this.subgroupname = subgroupname;
	}
}