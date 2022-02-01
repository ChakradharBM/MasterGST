package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR9Table8ITC2A {

	@Id
	private ObjectId id;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double iamt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double camt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double samt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double csamt;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
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
}
