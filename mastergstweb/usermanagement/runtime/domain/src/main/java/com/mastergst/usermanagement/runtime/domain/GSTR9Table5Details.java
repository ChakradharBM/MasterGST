package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR9 Table5 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id","chksum"})
public class GSTR9Table5Details {
	
	@Id
	private ObjectId id;
	
	String chksum;
	@JsonProperty("zero_rtd")
	GSTR9Table5ItemDetails zeroRtd = new GSTR9Table5ItemDetails();
	GSTR9Table5ItemDetails sez = new GSTR9Table5ItemDetails();
	GSTR9Table5ItemDetails rchrg = new GSTR9Table5ItemDetails();
	GSTR9Table5ItemDetails exmt = new GSTR9Table5ItemDetails();
	GSTR9Table5ItemDetails nil = new GSTR9Table5ItemDetails();
	@JsonProperty("non_gst")
	GSTR9Table5ItemDetails nonGst = new GSTR9Table5ItemDetails();
	@JsonProperty("cr_nt")
	GSTR9Table5ItemDetails crNt = new GSTR9Table5ItemDetails();
	@JsonProperty("dr_nt")
	GSTR9Table5ItemDetails drNt = new GSTR9Table5ItemDetails();
	@JsonProperty("amd_pos")
	GSTR9Table5ItemDetails amdPos = new GSTR9Table5ItemDetails();
	@JsonProperty("amd_neg")
	GSTR9Table5ItemDetails amdNeg = new GSTR9Table5ItemDetails();
	
	public GSTR9Table5Details() {
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

	public GSTR9Table5ItemDetails getZeroRtd() {
		return zeroRtd;
	}

	public void setZeroRtd(GSTR9Table5ItemDetails zeroRtd) {
		this.zeroRtd = zeroRtd;
	}

	public GSTR9Table5ItemDetails getSez() {
		return sez;
	}

	public void setSez(GSTR9Table5ItemDetails sez) {
		this.sez = sez;
	}

	public GSTR9Table5ItemDetails getRchrg() {
		return rchrg;
	}

	public void setRchrg(GSTR9Table5ItemDetails rchrg) {
		this.rchrg = rchrg;
	}

	public GSTR9Table5ItemDetails getExmt() {
		return exmt;
	}

	public void setExmt(GSTR9Table5ItemDetails exmt) {
		this.exmt = exmt;
	}

	public GSTR9Table5ItemDetails getNil() {
		return nil;
	}

	public void setNil(GSTR9Table5ItemDetails nil) {
		this.nil = nil;
	}

	public GSTR9Table5ItemDetails getNonGst() {
		return nonGst;
	}

	public void setNonGst(GSTR9Table5ItemDetails nonGst) {
		this.nonGst = nonGst;
	}

	public GSTR9Table5ItemDetails getCrNt() {
		return crNt;
	}

	public void setCrNt(GSTR9Table5ItemDetails crNt) {
		this.crNt = crNt;
	}

	public GSTR9Table5ItemDetails getDrNt() {
		return drNt;
	}

	public void setDrNt(GSTR9Table5ItemDetails drNt) {
		this.drNt = drNt;
	}

	public GSTR9Table5ItemDetails getAmdPos() {
		return amdPos;
	}

	public void setAmdPos(GSTR9Table5ItemDetails amdPos) {
		this.amdPos = amdPos;
	}

	public GSTR9Table5ItemDetails getAmdNeg() {
		return amdNeg;
	}

	public void setAmdNeg(GSTR9Table5ItemDetails amdNeg) {
		this.amdNeg = amdNeg;
	}


	
	
	
}
