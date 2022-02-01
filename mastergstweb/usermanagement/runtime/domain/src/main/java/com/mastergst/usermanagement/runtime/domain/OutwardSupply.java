package com.mastergst.usermanagement.runtime.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;
@JsonIgnoreProperties({"type", "iamt", "camt", "samt", "cess"})
public class OutwardSupply {
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double rate;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double txval;
	
	private String type;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double iamt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double camt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double samt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cess;
	
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public Double getTxval() {
		return txval;
	}
	public void setTxval(Double txval) {
		this.txval = txval;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Double getIamt() {
		return iamt;
	}
	public void setIamt(Double iamt) {
		this.iamt = iamt;
	}
	public Double getCamt() {
		return camt;
	}
	public void setCamt(Double camt) {
		this.camt = camt;
	}
	public Double getSamt() {
		return samt;
	}
	public void setSamt(Double samt) {
		this.samt = samt;
	}
	public Double getCess() {
		return cess;
	}
	public void setCess(Double cess) {
		this.cess = cess;
	}
}
