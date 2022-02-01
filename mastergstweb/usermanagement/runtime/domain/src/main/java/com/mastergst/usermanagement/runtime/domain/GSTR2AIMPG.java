package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR2AIMPG {
	
	@Id
	private ObjectId id;
	private String refdt;
	private String portcd;
	private Integer benum;
	private String bedt;
	private String amd;
	private String sgstin;
	private String tdname;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double txval;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double iamt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double csamt;
	
	public GSTR2AIMPG() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getRefdt() {
		return refdt;
	}

	public void setRefdt(String refdt) {
		this.refdt = refdt;
	}

	public String getPortcd() {
		return portcd;
	}

	public void setPortcd(String portcd) {
		this.portcd = portcd;
	}

	public Integer getBenum() {
		return benum;
	}

	public void setBenum(Integer benum) {
		this.benum = benum;
	}

	public String getBedt() {
		return bedt;
	}

	public void setBedt(String bedt) {
		this.bedt = bedt;
	}

	public String getAmd() {
		return amd;
	}

	public void setAmd(String amd) {
		this.amd = amd;
	}

	public Double getTxval() {
		return txval;
	}

	public void setTxval(Double txval) {
		this.txval = txval;
	}

	public Double getIamt() {
		return iamt;
	}

	public void setIamt(Double iamt) {
		this.iamt = iamt;
	}

	public Double getCsamt() {
		return csamt;
	}

	public void setCsamt(Double csamt) {
		this.csamt = csamt;
	}

	public String getSgstin() {
		return sgstin;
	}

	public void setSgstin(String sgstin) {
		this.sgstin = sgstin;
	}

	public String getTdname() {
		return tdname;
	}

	public void setTdname(String tdname) {
		this.tdname = tdname;
	}

}
