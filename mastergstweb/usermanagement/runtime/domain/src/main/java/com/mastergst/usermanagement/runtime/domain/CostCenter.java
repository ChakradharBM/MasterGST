package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.google.common.collect.Lists;

public class CostCenter {
	
	@Id
	private ObjectId id;

	private String name;
	private List<?> subcostcenter = Lists.newArrayList();
	
	public CostCenter() {
		this.id = ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<CostCenter> getSubcostcenter() {
		return (List<CostCenter>) subcostcenter;
	}
	public void setSubcostcenter(List<CostCenter> subcostcenter) {
		this.subcostcenter = subcostcenter;
	}
}
