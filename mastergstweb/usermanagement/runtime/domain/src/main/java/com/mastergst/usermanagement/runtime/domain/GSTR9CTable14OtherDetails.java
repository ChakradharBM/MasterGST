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
public class GSTR9CTable14OtherDetails {

	@Id
	private ObjectId id;
	@JsonProperty("itc_avail")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double itcAvail;
	
	public GSTR9CTable14OtherDetails() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public Double getItcAvail() {
		return itcAvail;
	}
	public void setItcAvail(Double itcAvail) {
		this.itcAvail = itcAvail;
	}
	
	
}
