package com.mastergst.usermanagement.runtime.domain.gstr2b;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR2BCPDetails {
	@Id
	private ObjectId id;
	private String ctin;
	private String trdnm;
	private String supprd;
	private String supfildt;
	private Integer ttldocs;
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

	public GSTR2BCPDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getCtin() {
		return ctin;
	}

	public void setCtin(String ctin) {
		this.ctin = ctin;
	}

	public String getTrdnm() {
		return trdnm;
	}

	public void setTrdnm(String trdnm) {
		this.trdnm = trdnm;
	}

	public String getSupprd() {
		return supprd;
	}

	public void setSupprd(String supprd) {
		this.supprd = supprd;
	}

	public String getSupfildt() {
		return supfildt;
	}

	public void setSupfildt(String supfildt) {
		this.supfildt = supfildt;
	}

	public Integer getTtldocs() {
		return ttldocs;
	}

	public void setTtldocs(Integer ttldocs) {
		this.ttldocs = ttldocs;
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

	public Double getTxval() {
		return txval;
	}

	public void setTxval(Double txval) {
		this.txval = txval;
	}
	

}
