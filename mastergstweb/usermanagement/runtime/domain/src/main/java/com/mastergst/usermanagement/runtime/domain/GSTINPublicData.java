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

/**
 * GSTIN Public information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public class GSTINPublicData {

	@Id
	private ObjectId id;
	private String userid;

	private String gstinpublicdataid;

	private String status;
	private String parentid;
	private String gstin;
	private String ctb;
	private String rgdt;
	private String sts;
	private String lgnm;
	private String stj;
	private String ctj;
	private String dty;
	private String cxdt;
	private String lstupdt;
	private String stjCd;
	private String ctjCd;
	private String tradeNam;
	private List<String> nba;
	private GSTINPublicAddress pradr = new GSTINPublicAddress();
	
	List<GSTINPublicAddress> adadr = LazyList.decorate(new ArrayList<GSTINPublicAddress>(), 
			FactoryUtils.instantiateFactory(GSTINPublicAddress.class));
	
	
	private Date createdDate;
	
	private String ipAddress;

	public GSTINPublicData() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getCtb() {
		return ctb;
	}

	public void setCtb(String ctb) {
		this.ctb = ctb;
	}

	public String getRgdt() {
		return rgdt;
	}

	public void setRgdt(String rgdt) {
		this.rgdt = rgdt;
	}

	public String getSts() {
		return sts;
	}

	public void setSts(String sts) {
		this.sts = sts;
	}

	public String getLgnm() {
		return lgnm;
	}

	public void setLgnm(String lgnm) {
		this.lgnm = lgnm;
	}

	public String getStj() {
		return stj;
	}

	public void setStj(String stj) {
		this.stj = stj;
	}

	public String getCtj() {
		return ctj;
	}

	public void setCtj(String ctj) {
		this.ctj = ctj;
	}

	public String getDty() {
		return dty;
	}

	public void setDty(String dty) {
		this.dty = dty;
	}

	public String getCxdt() {
		return cxdt;
	}

	public void setCxdt(String cxdt) {
		this.cxdt = cxdt;
	}

	public String getLstupdt() {
		return lstupdt;
	}

	public void setLstupdt(String lstupdt) {
		this.lstupdt = lstupdt;
	}

	public String getStjCd() {
		return stjCd;
	}

	public void setStjCd(String stjCd) {
		this.stjCd = stjCd;
	}

	public String getCtjCd() {
		return ctjCd;
	}

	public void setCtjCd(String ctjCd) {
		this.ctjCd = ctjCd;
	}

	public String getTradeNam() {
		return tradeNam;
	}

	public void setTradeNam(String tradeNam) {
		this.tradeNam = tradeNam;
	}

	public List<String> getNba() {
		return nba;
	}

	public void setNba(List<String> nba) {
		this.nba = nba;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public GSTINPublicAddress getPradr() {
		return pradr;
	}

	public void setPradr(GSTINPublicAddress pradr) {
		this.pradr = pradr;
	}

	public List<GSTINPublicAddress> getAdadr() {
		return adadr;
	}

	public void setAdadr(List<GSTINPublicAddress> adadr) {
		this.adadr = adadr;
	}
	public String getGstinpublicdataid() {
		return gstinpublicdataid;
	}

	public void setGstinpublicdataid(String gstinpublicdataid) {
		this.gstinpublicdataid = gstinpublicdataid;
	}

}
