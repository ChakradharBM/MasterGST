package com.mastergst.usermanagement.runtime.domain.gstr2b;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR2BIMPG {
	@Id
	private ObjectId id;
	private String refdt;
	private String recdt;
	private String portcode;
	private String boenum;
	private String boedt;
	private String isamd;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double txval;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double igst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cess;
	
	public GSTR2BIMPG() {
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
	public String getRecdt() {
		return recdt;
	}
	public void setRecdt(String recdt) {
		this.recdt = recdt;
	}
	public String getPortcode() {
		return portcode;
	}
	public void setPortcode(String portcode) {
		this.portcode = portcode;
	}
	public String getBoenum() {
		return boenum;
	}
	public void setBoenum(String boenum) {
		this.boenum = boenum;
	}
	public String getBoedt() {
		return boedt;
	}
	public void setBoedt(String boedt) {
		this.boedt = boedt;
	}
	public String getIsamd() {
		return isamd;
	}
	public void setIsamd(String isamd) {
		this.isamd = isamd;
	}
	public Double getTxval() {
		return txval;
	}
	public void setTxval(Double txval) {
		this.txval = txval;
	}
	public Double getIgst() {
		return igst;
	}
	public void setIgst(Double igst) {
		this.igst = igst;
	}
	public Double getCess() {
		return cess;
	}
	public void setCess(Double cess) {
		this.cess = cess;
	}
	
}
