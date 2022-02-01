package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GSTR9Table9AutoCalculatedDetails {
	@Id
	private ObjectId id;
	GSTR9Table9AllDetails iamt = new GSTR9Table9AllDetails();
	GSTR9Table9AllDetails camt = new GSTR9Table9AllDetails();
	GSTR9Table9AllDetails samt = new GSTR9Table9AllDetails();
	GSTR9Table9AllDetails csamt = new GSTR9Table9AllDetails();
	GSTR9Table9AllDetails intr = new GSTR9Table9AllDetails();
	GSTR9Table9AllDetails fee = new GSTR9Table9AllDetails();
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public GSTR9Table9AllDetails getIamt() {
		return iamt;
	}
	public void setIamt(GSTR9Table9AllDetails iamt) {
		this.iamt = iamt;
	}
	public GSTR9Table9AllDetails getCamt() {
		return camt;
	}
	public void setCamt(GSTR9Table9AllDetails camt) {
		this.camt = camt;
	}
	public GSTR9Table9AllDetails getSamt() {
		return samt;
	}
	public void setSamt(GSTR9Table9AllDetails samt) {
		this.samt = samt;
	}
	public GSTR9Table9AllDetails getCsamt() {
		return csamt;
	}
	public void setCsamt(GSTR9Table9AllDetails csamt) {
		this.csamt = csamt;
	}
	public GSTR9Table9AllDetails getIntr() {
		return intr;
	}
	public void setIntr(GSTR9Table9AllDetails intr) {
		this.intr = intr;
	}
	public GSTR9Table9AllDetails getFee() {
		return fee;
	}
	public void setFee(GSTR9Table9AllDetails fee) {
		this.fee = fee;
	}
	
}
