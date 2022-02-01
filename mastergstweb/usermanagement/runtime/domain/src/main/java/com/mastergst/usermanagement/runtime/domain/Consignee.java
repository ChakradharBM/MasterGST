/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * Consignee information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public class Consignee {
	
	@Id
	private ObjectId id;
	private String consigneegstin;
	private String consigneename;
	private String consigneestatecode;
	private String consigneestatename;
	private String isconsigneeto;

	public Consignee() {
		this.id = ObjectId.get();
	}
	
	public String getConsigneegstin() {
		return consigneegstin;
	}

	public void setConsigneegstin(String consigneegstin) {
		this.consigneegstin = consigneegstin;
	}

	public String getConsigneename() {
		return consigneename;
	}

	public void setConsigneename(String consigneename) {
		this.consigneename = consigneename;
	}

	public String getConsigneestatecode() {
		return consigneestatecode;
	}

	public void setConsigneestatecode(String consigneestatecode) {
		this.consigneestatecode = consigneestatecode;
	}

	public String getConsigneestatename() {
		return consigneestatename;
	}

	public void setConsigneestatename(String consigneestatename) {
		this.consigneestatename = consigneestatename;
	}

	public String getIsconsigneeto() {
		return isconsigneeto;
	}

	public void setIsconsigneeto(String isconsigneeto) {
		this.isconsigneeto = isconsigneeto;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Consignee [id=" + id + ", consigneegstin=" + consigneegstin + ", consigneename=" + consigneename
				+ ", consigneestatecode=" + consigneestatecode + ", consigneestatename=" + consigneestatename
				+ ", isconsigneeto=" + isconsigneeto + "]";
	}
}
