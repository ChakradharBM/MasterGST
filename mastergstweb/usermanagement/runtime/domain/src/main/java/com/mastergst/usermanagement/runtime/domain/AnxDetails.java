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
public class AnxDetails {

	@Id
	private ObjectId id;
	
	
	private String doctyp;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double diffprcnt;
	private String sec7act;
	private String rfndelg;
	private String action;
	private String pos;
	private String amended;
	private String aprd;
	
	private String amdtyp;
	private String chksum;
	
	private String flag;
	private String clmrfnd;
	private String suptyp;
	private String octin;
	private String odoctyp;
	private String invalid;
	private AnxDoc odoc;
	private AnxBoe boe;
	private AnxDoc doc;
	private AnxSb sb;
	
	private List<AnxItems> items;

	public AnxDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getAmended() {
		return amended;
	}

	public void setAmended(String amended) {
		this.amended = amended;
	}

	public String getAprd() {
		return aprd;
	}

	public void setAprd(String aprd) {
		this.aprd = aprd;
	}

	public String getAmdtyp() {
		return amdtyp;
	}

	public void setAmdtyp(String amdtyp) {
		this.amdtyp = amdtyp;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
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

	public AnxSb getSb() {
		return sb;
	}

	public void setSb(AnxSb sb) {
		this.sb = sb;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getClmrfnd() {
		return clmrfnd;
	}

	public void setClmrfnd(String clmrfnd) {
		this.clmrfnd = clmrfnd;
	}

	public String getSuptyp() {
		return suptyp;
	}

	public void setSuptyp(String suptyp) {
		this.suptyp = suptyp;
	}

	public String getOctin() {
		return octin;
	}

	public void setOctin(String octin) {
		this.octin = octin;
	}

	public String getOdoctyp() {
		return odoctyp;
	}

	public void setOdoctyp(String odoctyp) {
		this.odoctyp = odoctyp;
	}

	public String getInvalid() {
		return invalid;
	}

	public void setInvalid(String invalid) {
		this.invalid = invalid;
	}

	public AnxDoc getOdoc() {
		return odoc;
	}

	public void setOdoc(AnxDoc odoc) {
		this.odoc = odoc;
	}

	public AnxBoe getBoe() {
		return boe;
	}

	public void setBoe(AnxBoe boe) {
		this.boe = boe;
	}

	

}
