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
 * GSTR1 Import Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTRImportDetails {

	@Id
	private ObjectId id;
	
	private String flag;
	private String chksum;
	@JsonProperty("is_sez")
	private String isSez;
	private String stin;
	@JsonProperty("boe_num")
	private Integer boeNum;
	@JsonProperty("boe_dt")
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date boeDt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	@JsonProperty("boe_val")
	private Double boeVal;
	@JsonProperty("port_code")
	private String portCode;
	private String inum;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date idt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double ival;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double val;
	private String pos;	
	@JsonProperty("sply_ty")
	private String splyType;
	
	private String errorMsg;
	
	private List<GSTRImportItems> itms=LazyList.decorate(new ArrayList<GSTRImportItems>(), 
			FactoryUtils.instantiateFactory(GSTRImportItems.class));

	public GSTRImportDetails() {
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

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public String getIsSez() {
		return isSez;
	}

	public void setIsSez(String isSez) {
		this.isSez = isSez;
	}

	public String getStin() {
		return stin;
	}

	public void setStin(String stin) {
		this.stin = stin;
	}

	public Integer getBoeNum() {
		return boeNum;
	}

	public void setBoeNum(Integer boeNum) {
		this.boeNum = boeNum;
	}

	public Date getBoeDt() {
		return boeDt;
	}

	public void setBoeDt(Date boeDt) {
		this.boeDt = boeDt;
	}

	public Double getBoeVal() {
		return boeVal;
	}

	public void setBoeVal(Double boeVal) {
		this.boeVal = boeVal;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
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

	public Double getIval() {
		return ival;
	}

	public void setIval(Double ival) {
		this.ival = ival;
	}
	
	public Double getVal() {
		return val;
	}

	public void setVal(Double val) {
		this.val = val;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List<GSTRImportItems> getItms() {
		return itms;
	}

	public void setItms(List<GSTRImportItems> itms) {
		this.itms = itms;
	}
	
	public void setSplyType(String splyType) {
		this.splyType = splyType;
	}

	public String getSplyType() {
		return splyType;
	}


}
