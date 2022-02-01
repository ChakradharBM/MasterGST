package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9Table6AutoCalDetails {
	
	@Id
	private ObjectId id;
	
	GSTR9Table6Trans1AndTrans2Details tran1 = new GSTR9Table6Trans1AndTrans2Details();
	GSTR9Table6Trans1AndTrans2Details tran2 = new GSTR9Table6Trans1AndTrans2Details();
	GSTR9Table6IsdAndItcClaimedDetails other = new GSTR9Table6IsdAndItcClaimedDetails();
	String chksum;
	@JsonProperty("itc_3b")
	GSTR9Table6ITC3B itc3b = new GSTR9Table6ITC3B();
	GSTR9Table6IsdAndItcClaimedDetails isd = new GSTR9Table6IsdAndItcClaimedDetails();
	public GSTR9Table6AutoCalDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	

	public GSTR9Table6IsdAndItcClaimedDetails getIsd() {
		return isd;
	}

	public void setIsd(GSTR9Table6IsdAndItcClaimedDetails isd) {
		this.isd = isd;
	}

	public GSTR9Table6Trans1AndTrans2Details getTran1() {
		return tran1;
	}

	public void setTran1(GSTR9Table6Trans1AndTrans2Details tran1) {
		this.tran1 = tran1;
	}

	public GSTR9Table6Trans1AndTrans2Details getTran2() {
		return tran2;
	}

	public void setTran2(GSTR9Table6Trans1AndTrans2Details tran2) {
		this.tran2 = tran2;
	}

	public GSTR9Table6IsdAndItcClaimedDetails getOther() {
		return other;
	}

	public void setOther(GSTR9Table6IsdAndItcClaimedDetails other) {
		this.other = other;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public GSTR9Table6ITC3B getItc3b() {
		return itc3b;
	}

	public void setItc3b(GSTR9Table6ITC3B itc3b) {
		this.itc3b = itc3b;
	}

}
