/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * Anx common information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class Anx2Details {

	@Id
	private ObjectId id;
	
	private String flag;
	private String doctyp;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double diffprcnt;
	private String sec7act;
	private String rfndelg;
	private String action;
	private String cfs;
	private String pos;
	private String itcent;
	private String itcentedtbl;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date uplddt;
	private String chksum;
	private String clmrfnd;
	private AnxDoc doc;
	private List<AnxItems> items;
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
	public String getDoctyp() {
		return doctyp;
	}
	public void setDoctyp(String doctyp) {
		this.doctyp = doctyp;
	}
	public Double getDiffprcnt() {
		return diffprcnt;
	}
	public void setDiffprcnt(Double diffprcnt) {
		this.diffprcnt = diffprcnt;
	}
	public String getSec7act() {
		return sec7act;
	}
	public void setSec7act(String sec7act) {
		this.sec7act = sec7act;
	}
	public String getRfndelg() {
		return rfndelg;
	}
	public void setRfndelg(String rfndelg) {
		this.rfndelg = rfndelg;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getCfs() {
		return cfs;
	}
	public void setCfs(String cfs) {
		this.cfs = cfs;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getItcent() {
		return itcent;
	}
	public void setItcent(String itcent) {
		this.itcent = itcent;
	}
	public String getItcentedtbl() {
		return itcentedtbl;
	}
	public void setItcentedtbl(String itcentedtbl) {
		this.itcentedtbl = itcentedtbl;
	}
	public Date getUplddt() {
		return uplddt;
	}
	public void setUplddt(Date uplddt) {
		this.uplddt = uplddt;
	}
	public String getChksum() {
		return chksum;
	}
	public void setChksum(String chksum) {
		this.chksum = chksum;
	}
	public String getClmrfnd() {
		return clmrfnd;
	}
	public void setClmrfnd(String clmrfnd) {
		this.clmrfnd = clmrfnd;
	}
	public AnxDoc getDoc() {
		return doc;
	}
	public void setDoc(AnxDoc doc) {
		this.doc = doc;
	}
	public List<AnxItems> getItems() {
		return items;
	}
	public void setItems(List<AnxItems> items) {
		this.items = items;
	}

	
}
