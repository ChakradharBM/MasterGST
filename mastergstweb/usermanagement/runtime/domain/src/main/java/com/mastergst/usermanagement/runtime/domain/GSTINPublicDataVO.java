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
public class GSTINPublicDataVO {

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
	private String bnm;
	private String st;
	private String loc;
	private String bno;
	private String dst;
	private String stcd;
	private String city;
	private String flno;
	private String lt;
	private String pncd;
	private String lg;
	private Date createdDate;
	private String ntr;
	private String ipAddress;
	
	private String abnm;
	private String ast;
	private String aloc;
	private String abno;
	private String adst;
	private String astcd;
	private String acity;
	private String aflno;
	private String alt;
	private String apncd;
	private String alg;
	
	
	public String getGstinpublicdataid() {
		return gstinpublicdataid;
	}

	public void setGstinpublicdataid(String gstinpublicdataid) {
		this.gstinpublicdataid = gstinpublicdataid;
	}
	
	public GSTINPublicDataVO() {
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
	public String getBnm() {
		return bnm;
	}

	public void setBnm(String bnm) {
		this.bnm = bnm;
	}

	public String getSt() {
		return st;
	}

	public void setSt(String st) {
		this.st = st;
	}

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public String getBno() {
		return bno;
	}

	public void setBno(String bno) {
		this.bno = bno;
	}

	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	public String getStcd() {
		return stcd;
	}

	public void setStcd(String stcd) {
		this.stcd = stcd;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getFlno() {
		return flno;
	}

	public void setFlno(String flno) {
		this.flno = flno;
	}

	public String getLt() {
		return lt;
	}

	public void setLt(String lt) {
		this.lt = lt;
	}

	public String getPncd() {
		return pncd;
	}

	public void setPncd(String pncd) {
		this.pncd = pncd;
	}

	public String getLg() {
		return lg;
	}

	public void setLg(String lg) {
		this.lg = lg;
	}
	public String getNtr() {
		return ntr;
	}

	public void setNtr(String ntr) {
		this.ntr = ntr;
	}

	public String getAbnm() {
		return abnm;
	}

	public void setAbnm(String abnm) {
		this.abnm = abnm;
	}

	public String getAst() {
		return ast;
	}

	public void setAst(String ast) {
		this.ast = ast;
	}

	public String getAloc() {
		return aloc;
	}

	public void setAloc(String aloc) {
		this.aloc = aloc;
	}

	public String getAbno() {
		return abno;
	}

	public void setAbno(String abno) {
		this.abno = abno;
	}

	public String getAdst() {
		return adst;
	}

	public void setAdst(String adst) {
		this.adst = adst;
	}

	public String getAstcd() {
		return astcd;
	}

	public void setAstcd(String astcd) {
		this.astcd = astcd;
	}

	public String getAcity() {
		return acity;
	}

	public void setAcity(String acity) {
		this.acity = acity;
	}

	public String getAflno() {
		return aflno;
	}

	public void setAflno(String aflno) {
		this.aflno = aflno;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getApncd() {
		return apncd;
	}

	public void setApncd(String apncd) {
		this.apncd = apncd;
	}

	public String getAlg() {
		return alg;
	}

	public void setAlg(String alg) {
		this.alg = alg;
	}
	
}
