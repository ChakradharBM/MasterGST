package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GSTR9Table8AutoCalculatedDetails {
	
	@Id
	private ObjectId id;
	GSTR9Table8ITC2A itc_2a = new GSTR9Table8ITC2A();
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public GSTR9Table8ITC2A getItc_2a() {
		return itc_2a;
	}
	public void setItc_2a(GSTR9Table8ITC2A itc_2a) {
		this.itc_2a = itc_2a;
	}
	
	
}
