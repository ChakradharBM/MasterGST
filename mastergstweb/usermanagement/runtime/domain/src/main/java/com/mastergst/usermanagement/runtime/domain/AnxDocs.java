/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Anx Docs information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id","pos"})
public class AnxDocs {

	@Id
	private ObjectId id;
	
	private String ctin;
	private String type;
	private String pos;
	private String tblref;
	private List<AnxDetails> docs;

	public AnxDocs() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getCtin() {
		return ctin;
	}

	public void setCtin(String ctin) {
		this.ctin = ctin;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getTblref() {
		return tblref;
	}

	public void setTblref(String tblref) {
		this.tblref = tblref;
	}

	public List<AnxDetails> getDocs() {
		return docs;
	}

	public void setDocs(List<AnxDetails> docs) {
		this.docs = docs;
	}

}
