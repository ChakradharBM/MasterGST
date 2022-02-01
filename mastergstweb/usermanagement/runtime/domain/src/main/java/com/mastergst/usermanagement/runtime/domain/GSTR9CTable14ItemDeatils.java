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
public class GSTR9CTable14ItemDeatils {
	@Id
	private ObjectId id;
	
	private String desc;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double val;
	@JsonProperty("itc_amt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double itcAmt;
	@JsonProperty("itc_avail")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double itcAvail;
	
	public GSTR9CTable14ItemDeatils() {
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
	public Double getVal() {
		return val;
	}
	public void setVal(Double val) {
		this.val = val;
	}
	public Double getItcAmt() {
		return itcAmt;
	}
	public void setItcAmt(Double itcAmt) {
		this.itcAmt = itcAmt;
	}
	public Double getItcAvail() {
		return itcAvail;
	}
	public void setItcAvail(Double itcAvail) {
		this.itcAvail = itcAvail;
	}
	
	
}
