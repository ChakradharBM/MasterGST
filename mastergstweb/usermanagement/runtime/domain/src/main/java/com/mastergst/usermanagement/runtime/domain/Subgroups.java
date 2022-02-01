package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Subgroups {

	@Id 
	  private ObjectId id;
	 

	private String userid;
	private String clientid;
	private String groupname;
	private String headname;
	private String name;
	private String path;
	private String groupid;
	private String subgroupid;

	private boolean readonly;

	private Amounts amounts;

	public Subgroups() {
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

	public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getSubgroupid() {
		return subgroupid;
	}

	public void setSubgroupid(String subgroupid) {
		this.subgroupid = subgroupid;
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