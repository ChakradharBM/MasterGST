/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * GSTR7 TDSA invoices information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR7TDSA {
	@Id
	private ObjectId id;
	
	@JsonProperty("ogstin_ded")
	private String ogstinDed;
	@DateTimeFormat(pattern = "MMYYYY")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMYYYY")
	private Date omonth;
	@JsonProperty("oamt_ded")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double oamtDed;
	@JsonProperty("gstin_ded")
	private String gstinDed;
	private String chksum;
	@JsonProperty("amt_ded")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double amtDed;
	private Double iamt;
	private Double camt;
	private Double samt;
	private String source;
	@JsonProperty("act_tkn")
	private String actTkn;
	
	public GSTR7TDSA() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getOgstinDed() {
		return ogstinDed;
	}
	public void setOgstinDed(String ogstinDed) {
		this.ogstinDed = ogstinDed;
	}
	public Date getOmonth() {
		return omonth;
	}
	public void setOmonth(Date omonth) {
		this.omonth = omonth;
	}
	public Double getOamtDed() {
		return oamtDed;
	}
	public void setOamtDed(Double oamtDed) {
		this.oamtDed = oamtDed;
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getActTkn() {
		return actTkn;
	}

	public void setActTkn(String actTkn) {
		this.actTkn = actTkn;
	}

}
