/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
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
 * GSTR1 Export Invoice Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRExportDetails {

	@Id
	private ObjectId id;
	
	private String flag;
	private String inum;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date idt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double val;
	private String sbpcode;
	private String sbnum;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date sbdt;
	@JsonProperty("diff_percent")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double diffPercent;
	private String oinum;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date oidt;
	private String irn;
	private String irngendate;
	private List<GSTRItemDetails> itms=LazyList.decorate(new ArrayList<GSTRItemDetails>(), 
			FactoryUtils.instantiateFactory(GSTRItemDetails.class));
	
	public GSTRExportDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getInum() {
		return inum;
	}

	public void setInum(String inum) {
		this.inum = inum;
	}

	public Date getIdt() {
		return idt;
	}

	public void setIdt(Date idt) {
		this.idt = idt;
	}

	public Double getVal() {
		return val;
	}

	public void setVal(Double val) {
		this.val = val;
	}

	public String getSbpcode() {
		return sbpcode;
	}

	public void setSbpcode(String sbpcode) {
		this.sbpcode = sbpcode;
	}

	public String getSbnum() {
		return sbnum;
	}

	public void setSbnum(String sbnum) {
		this.sbnum = sbnum;
	}

	public Date getSbdt() {
		return sbdt;
	}

	public void setSbdt(Date sbdt) {
		this.sbdt = sbdt;
	}

	public List<GSTRItemDetails> getItms() {
		return itms;
	}

	public void setItms(List<GSTRItemDetails> itms) {
		this.itms = itms;
	}

	public Double getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(Double diffPercent) {
		this.diffPercent = diffPercent;
	}

	public String getOinum() {
		return oinum;
	}

	public void setOinum(String oinum) {
		this.oinum = oinum;
	}

	public Date getOidt() {
		return oidt;
	}

	public void setOidt(Date oidt) {
		this.oidt = oidt;
	}

	public String getIrn() {
		return irn;
	}

	public void setIrn(String irn) {
		this.irn = irn;
	}

	public String getIrngendate() {
		return irngendate;
	}

	public void setIrngendate(String irngendate) {
		this.irngendate = irngendate;
	}

}
