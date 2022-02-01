package com.mastergst.usermanagement.runtime.domain.gstr2b;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR2BDocList {
	@Id
	private ObjectId id;
	private String doctyp;
	private String docnum;
	private String docdt;
	private String oinvnum;
	private String oinvdt;
	private String itcelg;
	//GSTR2B ISDA
	private String odoctyp;
	private String odocnum;
	private String odocdt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double igst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cess;
	
	public GSTR2BDocList() {
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
	public String getDocnum() {
		return docnum;
	}
	public void setDocnum(String docnum) {
		this.docnum = docnum;
	}
	public String getDocdt() {
		return docdt;
	}
	public void setDocdt(String docdt) {
		this.docdt = docdt;
	}
	public String getOinvnum() {
		return oinvnum;
	}
	public void setOinvnum(String oinvnum) {
		this.oinvnum = oinvnum;
	}
	public String getOinvdt() {
		return oinvdt;
	}
	public void setOinvdt(String oinvdt) {
		this.oinvdt = oinvdt;
	}
	public String getOdoctyp() {
		return odoctyp;
	}
	public void setOdoctyp(String odoctyp) {
		this.odoctyp = odoctyp;
	}
	public String getOdocnum() {
		return odocnum;
	}
	public void setOdocnum(String odocnum) {
		this.odocnum = odocnum;
	}
	public String getOdocdt() {
		return odocdt;
	}
	public void setOdocdt(String odocdt) {
		this.odocdt = odocdt;
	}
	public String getItcelg() {
		return itcelg;
	}
	public void setItcelg(String itcelg) {
		this.itcelg = itcelg;
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
