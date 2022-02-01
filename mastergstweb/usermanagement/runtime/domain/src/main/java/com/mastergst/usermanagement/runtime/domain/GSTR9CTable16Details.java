package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9CTable16Details {

	@Id
	private ObjectId id;
	
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double cgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double sgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double igst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double cess;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double inter;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double pen;
	
	public GSTR9CTable16Details() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
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
	public Double getInter() {
		return inter;
	}
	public void setInter(Double inter) {
		this.inter = inter;
	}
	public Double getPen() {
		return pen;
	}
	public void setPen(Double pen) {
		this.pen = pen;
	}
	
	
}
