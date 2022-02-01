package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.common.collect.Lists;

@Document(collection = "accounting_heads")
public class AccountingHeads {

	@Id
	private ObjectId id;

	private String headname;
	private String name;
	private String path;

	private boolean readonly;

	private List<AccountingHeads> heads;// = Lists.newArrayList();

	public AccountingHeads() {
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

	public List<AccountingHeads> getHeads() {
		return heads;
	}

	public void setHeads(List<AccountingHeads> heads) {
		this.heads = heads;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	@Override
	public String toString() {
		return "AccountingHeads [headname=" + headname + ", name=" + name + ", path=" + path
				+ ", readonly=" + readonly + ", heads=" + heads + "]";
	}

	
}
