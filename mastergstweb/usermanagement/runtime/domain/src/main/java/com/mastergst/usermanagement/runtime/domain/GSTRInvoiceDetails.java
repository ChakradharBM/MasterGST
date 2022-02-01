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
 * GSTR1 Invoice Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id","address","rsn"})
public class GSTRInvoiceDetails {

	@Id
	private ObjectId id;
	
	private String flag;
	private String chksum;
	private String inum;
	private String idt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double val;
	private String pos;
	private String address;
	private String rchrg;
	private String etin;
	private String rtin;
	@JsonProperty("inv_typ")
	private String invTyp;
	private String ntty;
	@JsonProperty("nt_num")
	private String ntNum;
	@JsonProperty("nt_dt")
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date ntDt;
	private String rsn;
	@JsonProperty("p_gst")
	private String pGst;
	@JsonProperty("sply_ty")
	private String splyType;
	private String typ;
	@JsonProperty("error_msg")
	private String errorMsg;
	private String irn;
	private String irngendate;
	
	//GSTR2A B2BA
	private String oinum;
	private String oidt;
	private String aspd;
	private String atyp;

	// GSTR2A CDNA
	@JsonProperty("ont_num")
	private String ontNum;
	@JsonProperty("ont_dt")
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date ontDt;
	@JsonProperty("diff_percent")
	private Double diffPercent;

	private String cflag;
	private String updby;
	@JsonProperty("d_flag")
	private String dFlag;

	private List<GSTRItems> itms = LazyList.decorate(new ArrayList<GSTRItems>(),
			FactoryUtils.instantiateFactory(GSTRItems.class));
	
	public GSTRInvoiceDetails() {
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

	public String getInum() {
		return inum;
	}

	public void setInum(String inum) {
		this.inum = inum;
	}

	public String getIdt() {
		return idt;
	}

	public void setIdt(String idt) {
		this.idt = idt;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRchrg() {
		return rchrg;
	}

	public void setRchrg(String rchrg) {
		this.rchrg = rchrg;
	}

	public String getEtin() {
		return etin;
	}

	public void setEtin(String etin) {
		this.etin = etin;
	}

	public String getRtin() {
		return rtin;
	}

	public void setRtin(String rtin) {
		this.rtin = rtin;
	}

	public String getInvTyp() {
		return invTyp;
	}

	public void setInvTyp(String invTyp) {
		this.invTyp = invTyp;
	}

	public String getNtty() {
		return ntty;
	}

	public void setNtty(String ntty) {
		this.ntty = ntty;
	}

	public String getNtNum() {
		return ntNum;
	}

	public void setNtNum(String ntNum) {
		this.ntNum = ntNum;
	}

	public Date getNtDt() {
		return ntDt;
	}

	public void setNtDt(Date ntDt) {
		this.ntDt = ntDt;
	}

	public String getRsn() {
		return rsn;
	}

	public void setRsn(String rsn) {
		this.rsn = rsn;
	}

	public String getpGst() {
		return pGst;
	}

	public void setpGst(String pGst) {
		this.pGst = pGst;
	}

	public String getSplyType() {
		return splyType;
	}

	public void setSplyType(String splyType) {
		this.splyType = splyType;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List<GSTRItems> getItms() {
		return itms;
	}

	public void setItms(List<GSTRItems> itms) {
		this.itms = itms;
	}

	public String getOinum() {
		return oinum;
	}

	public void setOinum(String oinum) {
		this.oinum = oinum;
	}

	public String getOidt() {
		return oidt;
	}

	public void setOidt(String oidt) {
		this.oidt = oidt;
	}

	public String getOntNum() {
		return ontNum;
	}

	public void setOntNum(String ontNum) {
		this.ontNum = ontNum;
	}

	public Date getOntDt() {
		return ontDt;
	}

	public void setOntDt(Date ontDt) {
		this.ontDt = ontDt;
	}

	public Double getDiffPercent() {
		return diffPercent;
	}

	public void setDiffPercent(Double diffPercent) {
		this.diffPercent = diffPercent;
	}

	public String getAspd() {
		return aspd;
	}

	public void setAspd(String aspd) {
		this.aspd = aspd;
	}

	public String getAtyp() {
		return atyp;
	}

	public void setAtyp(String atyp) {
		this.atyp = atyp;
	}

	public String getCflag() {
		return cflag;
	}

	public void setCflag(String cflag) {
		this.cflag = cflag;
	}

	public String getUpdby() {
		return updby;
	}

	public void setUpdby(String updby) {
		this.updby = updby;
	}

	public String getdFlag() {
		return dFlag;
	}

	public void setdFlag(String dFlag) {
		this.dFlag = dFlag;
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
