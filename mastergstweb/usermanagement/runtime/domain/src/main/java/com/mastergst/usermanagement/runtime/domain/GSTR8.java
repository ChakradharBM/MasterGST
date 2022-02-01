package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mastergst.core.domain.Base;

@Document(collection = "gstr8")
@JsonFilter("gstr8Filter")
@JsonIgnoreProperties({ "id", "createdDate", "createdBy", "updatedDate", "updatedBy", "userid", "fullname", "clientid",
		"gstStatus", "gstRefId", "submitStatus", "tcsR", "tcsU", "tcsaR", "tcsaU"})
public class GSTR8 extends Base {

	private String fullname;
	private String userid;
	private String clientid;

	private String gstStatus;

	private String gstin;
	private String fp;

	@SuppressWarnings("unchecked")
	List<GSTR8TCS> tcs = LazyList.decorate(new ArrayList<GSTR8TCS>(), FactoryUtils.instantiateFactory(GSTR8TCS.class));
	@SuppressWarnings("unchecked")
	List<GSTR8TCS> tcsa = LazyList.decorate(new ArrayList<GSTR8TCS>(), FactoryUtils.instantiateFactory(GSTR8TCS.class));

	@SuppressWarnings("unchecked")
	List<GSTR8TCS> tcsR = LazyList.decorate(new ArrayList<GSTR8TCS>(), FactoryUtils.instantiateFactory(GSTR8TCS.class));
	@SuppressWarnings("unchecked")
	List<GSTR8TCS> tcsU = LazyList.decorate(new ArrayList<GSTR8TCS>(), FactoryUtils.instantiateFactory(GSTR8TCS.class));

	@SuppressWarnings("unchecked")
	List<GSTR8TCS> tcsaR = LazyList.decorate(new ArrayList<GSTR8TCS>(),
			FactoryUtils.instantiateFactory(GSTR8TCS.class));
	@SuppressWarnings("unchecked")
	List<GSTR8TCS> tcsaU = LazyList.decorate(new ArrayList<GSTR8TCS>(),
			FactoryUtils.instantiateFactory(GSTR8TCS.class));

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getGstStatus() {
		return gstStatus;
	}

	public void setGstStatus(String gstStatus) {
		this.gstStatus = gstStatus;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getFp() {
		return fp;
	}

	public void setFp(String fp) {
		this.fp = fp;
	}

	public List<GSTR8TCS> getTcs() {
		return tcs;
	}

	public void setTcs(List<GSTR8TCS> tcs) {
		this.tcs = tcs;
	}

	public List<GSTR8TCS> getTcsa() {
		return tcsa;
	}

	public void setTcsa(List<GSTR8TCS> tcsa) {
		this.tcsa = tcsa;
	}

	public List<GSTR8TCS> getTcsR() {
		return tcsR;
	}

	public void setTcsR(List<GSTR8TCS> tcsR) {
		this.tcsR = tcsR;
	}

	public List<GSTR8TCS> getTcsU() {
		return tcsU;
	}

	public void setTcsU(List<GSTR8TCS> tcsU) {
		this.tcsU = tcsU;
	}

	public List<GSTR8TCS> getTcsaR() {
		return tcsaR;
	}

	public void setTcsaR(List<GSTR8TCS> tcsaR) {
		this.tcsaR = tcsaR;
	}

	public List<GSTR8TCS> getTcsaU() {
		return tcsaU;
	}

	public void setTcsaU(List<GSTR8TCS> tcsaU) {
		this.tcsaU = tcsaU;
	}
}
