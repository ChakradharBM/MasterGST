/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR3B Supply Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR3BSupplyDetails {

	@Id
	private ObjectId id;

	@JsonProperty("osup_det")
	private GSTR3BDetails osupDet=new GSTR3BDetails();
	@JsonProperty("osup_zero")
	private GSTR3BDetails osupZero=new GSTR3BDetails();
	@JsonProperty("osup_nil_exmp")
	private GSTR3BDetails osupNilExmp=new GSTR3BDetails();
	@JsonProperty("isup_rev")
	private GSTR3BDetails isupRev=new GSTR3BDetails();
	@JsonProperty("osup_nongst")
	private GSTR3BDetails osupNongst=new GSTR3BDetails();

	public GSTR3BSupplyDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public GSTR3BDetails getOsupDet() {
		return osupDet;
	}

	public void setOsupDet(GSTR3BDetails osupDet) {
		this.osupDet = osupDet;
	}

	public GSTR3BDetails getOsupZero() {
		return osupZero;
	}

	public void setOsupZero(GSTR3BDetails osupZero) {
		this.osupZero = osupZero;
	}

	public GSTR3BDetails getOsupNilExmp() {
		return osupNilExmp;
	}

	public void setOsupNilExmp(GSTR3BDetails osupNilExmp) {
		this.osupNilExmp = osupNilExmp;
	}

	public GSTR3BDetails getIsupRev() {
		return isupRev;
	}

	public void setIsupRev(GSTR3BDetails isupRev) {
		this.isupRev = isupRev;
	}

	public GSTR3BDetails getOsupNongst() {
		return osupNongst;
	}

	public void setOsupNongst(GSTR3BDetails osupNongst) {
		this.osupNongst = osupNongst;
	}

}
