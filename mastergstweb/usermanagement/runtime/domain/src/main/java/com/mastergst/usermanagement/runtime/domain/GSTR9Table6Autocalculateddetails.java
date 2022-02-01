package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GSTR9Table6Autocalculateddetails {
	@Id
	private ObjectId id;
	GSTR9Table6ITC3B itc_3b = new GSTR9Table6ITC3B();
	GSTR9Table6IsdAndItcClaimedDetails isd = new GSTR9Table6IsdAndItcClaimedDetails();
	GSTR9Table6Trans1AndTrans2Details trans1 = new GSTR9Table6Trans1AndTrans2Details();
	GSTR9Table6Trans1AndTrans2Details trans2 = new GSTR9Table6Trans1AndTrans2Details();
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public GSTR9Table6ITC3B getItc_3b() {
		return itc_3b;
	}
	public void setItc_3b(GSTR9Table6ITC3B itc_3b) {
		this.itc_3b = itc_3b;
	}
	public GSTR9Table6IsdAndItcClaimedDetails getIsd() {
		return isd;
	}
	public void setIsd(GSTR9Table6IsdAndItcClaimedDetails isd) {
		this.isd = isd;
	}
	public GSTR9Table6Trans1AndTrans2Details getTrans1() {
		return trans1;
	}
	public void setTrans1(GSTR9Table6Trans1AndTrans2Details trans1) {
		this.trans1 = trans1;
	}
	public GSTR9Table6Trans1AndTrans2Details getTrans2() {
		return trans2;
	}
	public void setTrans2(GSTR9Table6Trans1AndTrans2Details trans2) {
		this.trans2 = trans2;
	}
	
	
}
