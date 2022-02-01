package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties({"id"})
public class GSTR9Table8AutoCalDetails {
	
	@Id
	private ObjectId id;
	String chksum;
	
	
	
	@JsonProperty("itc_2a")
	GSTR9Table8ITC2A itc2a = new GSTR9Table8ITC2A();
	
	public GSTR9Table8AutoCalDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	
	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	

	public GSTR9Table8ITC2A getItc2a() {
		return itc2a;
	}

	public void setItc2a(GSTR9Table8ITC2A itc2a) {
		this.itc2a = itc2a;
	}
}
