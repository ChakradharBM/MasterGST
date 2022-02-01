package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GSTR3BAutoPopulateDetails {
	@Id
	private ObjectId id;
	private String r1fildt;
	private String r2bgendt;
	private String r3bgendt;
	private GSTR3BLiabilities liabitc;

	public GSTR3BAutoPopulateDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getR1fildt() {
		return r1fildt;
	}

	public void setR1fildt(String r1fildt) {
		this.r1fildt = r1fildt;
	}

	public String getR2bgendt() {
		return r2bgendt;
	}

	public void setR2bgendt(String r2bgendt) {
		this.r2bgendt = r2bgendt;
	}

	public String getR3bgendt() {
		return r3bgendt;
	}

	public void setR3bgendt(String r3bgendt) {
		this.r3bgendt = r3bgendt;
	}

	public GSTR3BLiabilities getLiabitc() {
		return liabitc;
	}

	public void setLiabitc(GSTR3BLiabilities liabitc) {
		this.liabitc = liabitc;
	}

}
