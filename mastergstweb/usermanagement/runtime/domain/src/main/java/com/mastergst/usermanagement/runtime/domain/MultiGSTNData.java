package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;
@Document(collection = "multigstdata")
public class MultiGSTNData extends Base{
	private String userid;
	private String gstno;
	private String gstin;
	private String rgdt;
	private String tradeNam;
	private String lstupdt;
	private String dty;
	private String status;
	private String gstnid;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getGstno() {
		return gstno;
	}
	public void setGstno(String gstno) {
		this.gstno = gstno;
	}
	public String getGstin() {
		return gstin;
	}
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}
	public String getRgdt() {
		return rgdt;
	}
	public void setRgdt(String rgdt) {
		this.rgdt = rgdt;
	}
	public String getTradeNam() {
		return tradeNam;
	}
	public void setTradeNam(String tradeNam) {
		this.tradeNam = tradeNam;
	}
	public String getLstupdt() {
		return lstupdt;
	}
	public void setLstupdt(String lstupdt) {
		this.lstupdt = lstupdt;
	}
	public String getDty() {
		return dty;
	}
	public void setDty(String dty) {
		this.dty = dty;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getGstnid() {
		return gstnid;
	}
	public void setGstnid(String gstnid) {
		this.gstnid = gstnid;
	}
	
	


}
