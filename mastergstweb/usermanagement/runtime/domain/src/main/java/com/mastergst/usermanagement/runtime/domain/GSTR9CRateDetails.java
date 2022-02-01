package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9CRateDetails {
	@Id
	private ObjectId id;
	
	private String desc;
	
	@JsonProperty("tax_val")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double taxVal;
	
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double cgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double sgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double igst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double cess;
	
	public GSTR9CRateDetails() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Double getTaxVal() {
		return taxVal;
	}
	public void setTaxVal(Double taxVal) {
		this.taxVal = taxVal;
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
	
	
	

}
