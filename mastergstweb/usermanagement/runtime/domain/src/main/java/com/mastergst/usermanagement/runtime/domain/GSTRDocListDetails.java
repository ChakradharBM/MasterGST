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
 * GSTR2 Document List Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRDocListDetails {

	@Id
	private ObjectId id;
	
	private String chksum;
	@JsonProperty("isd_docty")
	private String isdDocty;
	private String docnum;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date docdt;
	@JsonProperty("itc_elg")
	private String itcElg;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	@JsonProperty("tx_i")
	private Double iamt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	@JsonProperty("tx_s")
	private Double samt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	@JsonProperty("tx_c")
	private Double camt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	@JsonProperty("tx_cs")
	private Double cess;
	
	private String odocnum;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date odocdt;
	
	public GSTRDocListDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public String getIsdDocty() {
		return isdDocty;
	}

	public void setIsdDocty(String isdDocty) {
		this.isdDocty = isdDocty;
	}

	public String getDocnum() {
		return docnum;
	}

	public void setDocnum(String docnum) {
		this.docnum = docnum;
	}

	public Date getDocdt() {
		return docdt;
	}

	public void setDocdt(Date docdt) {
		this.docdt = docdt;
	}

	public String getItcElg() {
		return itcElg;
	}

	public void setItcElg(String itcElg) {
		this.itcElg = itcElg;
	}

	public Double getIamt() {
		return iamt;
	}

	public void setIamt(Double iamt) {
		this.iamt = iamt;
	}

	public Double getSamt() {
		return samt;
	}

	public void setSamt(Double samt) {
		this.samt = samt;
	}

	public Double getCamt() {
		return camt;
	}

	public void setCamt(Double camt) {
		this.camt = camt;
	}

	public Double getCess() {
		return cess;
	}

	public void setCess(Double cess) {
		this.cess = cess;
	}

	public String getOdocnum() {
		return odocnum;
	}

	public void setOdocnum(String odocnum) {
		this.odocnum = odocnum;
	}

	public Date getOdocdt() {
		return odocdt;
	}

	public void setOdocdt(Date odocdt) {
		this.odocdt = odocdt;
	}
}
