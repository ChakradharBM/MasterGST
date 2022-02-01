package com.mastergst.usermanagement.runtime.domain.gstr2b;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR2BItems {
	@Id
	private ObjectId id;
	private Integer num;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double rt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double txval;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double igst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cess;

	public GSTR2BItems() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Double getRt() {
		return rt;
	}

	public void setRt(Double rt) {
		this.rt = rt;
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

	public Double getCgst() {
		return cgst;
	}

	public void setCgst(Double cgst) {
		this.cgst = cgst;
	}

	public Double getSgst() {
		return sgst;
	}

	public void setSgst(Double sgst) {
		this.sgst = sgst;
	}

	public Double getCess() {
		return cess;
	}

	public void setCess(Double cess) {
		this.cess = cess;
	}
}
