package com.mastergst.configuration.service;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.common.collect.Lists;


@Document(collection = "groups")
public class Groups{
	
	  @Id 
	  private ObjectId id;
	
	private String headname;
	private String name;
	private String path;
	
	private List<Subgroup> subgroup = Lists.newArrayList();
	
	public Groups() {
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getHeadname() {
		return headname;
	}

	public void setHeadname(String headname) {
		this.headname = headname;
	}
	public List<Subgroup> getSubgroup() {
		return (List<Subgroup>)subgroup;
	}

	public void setSubgroup(List<Subgroup> subgroup) {
		this.subgroup = subgroup;
	}

}