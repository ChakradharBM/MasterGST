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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * GSTR7 TDS invoices information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR7TDS {
	@Id
	private ObjectId id;
	
	@JsonProperty("gstin_ded")
	private String gstinDed;
	private String chksum;
	@JsonProperty("amt_ded")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double amtDed;
	private Double iamt;
	private Double camt;
	private Double samt;
	
	public GSTR7TDS() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getGstinDed() {
		return gstinDed;
	}
	public void setGstinDed(String gstinDed) {
		this.gstinDed = gstinDed;
	}
	public String getChksum() {
		return chksum;
	}
	public void setChksum(String chksum) {
		this.chksum = chksum;
	}
	public Double getAmtDed() {
		return amtDed;
	}
	public void setAmtDed(Double amtDed) {
		this.amtDed = amtDed;
	}
	public Double getIamt() {
		return iamt;
	}
	public void setIamt(Double iamt) {
		this.iamt = iamt;
	}
	public Double getCamt() {
		return camt;
	}
	public void setCamt(Double camt) {
		this.camt = camt;
	}
	public Double getSamt() {
		return samt;
	}
	public void setSamt(Double samt) {
		this.samt = samt;
	}

}
