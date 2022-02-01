package com.mastergst.usermanagement.runtime.domain.gstr9response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR9HSNItemsResponse {

	@JsonProperty("hsn_sc")
	private String hsnSc;
	
	@JsonProperty("desc")
	private String desc;
	
	@JsonProperty("uqc")
	private String uqc;
	
	@JsonProperty("qty")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double qty;
	
	@JsonProperty("rt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double rt;
	
	@JsonProperty("txval")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double txval;
	
	@JsonProperty("iamt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double iamt;
	
	@JsonProperty("camt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double camt;
	
	@JsonProperty("samt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double samt;
	
	@JsonProperty("csamt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double csamt;
	@JsonProperty("isconcesstional")
	private String isconcesstional;
	
	
	public String getHsnSc() {
		return hsnSc;
	}

	public void setHsnSc(String hsnSc) {
		this.hsnSc = hsnSc;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUqc() {
		return uqc;
	}

	public void setUqc(String uqc) {
		this.uqc = uqc;
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
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

	public Double getCsamt() {
		return csamt;
	}

	public void setCsamt(Double csamt) {
		this.csamt = csamt;
	}

	public String getIsconcesstional() {
		return isconcesstional;
	}

	public void setIsconcesstional(String isconcesstional) {
		this.isconcesstional = isconcesstional;
	}
}
