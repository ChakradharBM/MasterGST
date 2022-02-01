package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR9 Table4 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9Table4GetDetails {
	@Id
	private ObjectId id;
	
	GSTR9Table4OtherThanExpSezDetails b2c=new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails b2b=new GSTR9Table4OtherThanExpSezDetails(); 
	GSTR9Table4ExpSezDetails exp=new GSTR9Table4ExpSezDetails(); 
	GSTR9Table4ExpSezDetails sez=new GSTR9Table4ExpSezDetails(); 
	GSTR9Table4OtherThanExpSezDetails deemed=new GSTR9Table4OtherThanExpSezDetails(); 
	GSTR9Table4OtherThanExpSezDetails at=new GSTR9Table4OtherThanExpSezDetails(); 
	GSTR9Table4OtherThanExpSezDetails rchrg=new GSTR9Table4OtherThanExpSezDetails();
	@JsonProperty("cr_nt")
	GSTR9Table4OtherThanExpSezDetails crNt=new GSTR9Table4OtherThanExpSezDetails(); 
	@JsonProperty("dr_nt")
	GSTR9Table4OtherThanExpSezDetails drNt=new GSTR9Table4OtherThanExpSezDetails(); 
	@JsonProperty("amd_pos")
	GSTR9Table4OtherThanExpSezDetails amdPos=new GSTR9Table4OtherThanExpSezDetails(); 
	@JsonProperty("amd_neg")
	GSTR9Table4OtherThanExpSezDetails amdNeg=new GSTR9Table4OtherThanExpSezDetails();
	String chksum;
	
	@JsonProperty("sub_totalAG")
	GSTR9Table4OtherThanExpSezDetails subTotalAG = new GSTR9Table4OtherThanExpSezDetails();
	@JsonProperty("sub_totalIL")
	GSTR9Table4OtherThanExpSezDetails subTotalIL = new GSTR9Table4OtherThanExpSezDetails();
	@JsonProperty("sup_adv")
	GSTR9Table4OtherThanExpSezDetails supAdv = new GSTR9Table4OtherThanExpSezDetails();
	public GSTR9Table4GetDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public GSTR9Table4OtherThanExpSezDetails getB2c() {
		return b2c;
	}

	public void setB2c(GSTR9Table4OtherThanExpSezDetails b2c) {
		this.b2c = b2c;
	}

	public GSTR9Table4OtherThanExpSezDetails getB2b() {
		return b2b;
	}

	public void setB2b(GSTR9Table4OtherThanExpSezDetails b2b) {
		this.b2b = b2b;
	}

	public GSTR9Table4ExpSezDetails getExp() {
		return exp;
	}

	public void setExp(GSTR9Table4ExpSezDetails exp) {
		this.exp = exp;
	}

	public GSTR9Table4ExpSezDetails getSez() {
		return sez;
	}

	public void setSez(GSTR9Table4ExpSezDetails sez) {
		this.sez = sez;
	}

	public GSTR9Table4OtherThanExpSezDetails getDeemed() {
		return deemed;
	}

	public void setDeemed(GSTR9Table4OtherThanExpSezDetails deemed) {
		this.deemed = deemed;
	}

	public GSTR9Table4OtherThanExpSezDetails getAt() {
		return at;
	}

	public void setAt(GSTR9Table4OtherThanExpSezDetails at) {
		this.at = at;
	}

	public GSTR9Table4OtherThanExpSezDetails getRchrg() {
		return rchrg;
	}

	public void setRchrg(GSTR9Table4OtherThanExpSezDetails rchrg) {
		this.rchrg = rchrg;
	}

	public GSTR9Table4OtherThanExpSezDetails getCrNt() {
		return crNt;
	}

	public void setCrNt(GSTR9Table4OtherThanExpSezDetails crNt) {
		this.crNt = crNt;
	}

	public GSTR9Table4OtherThanExpSezDetails getDrNt() {
		return drNt;
	}

	public void setDrNt(GSTR9Table4OtherThanExpSezDetails drNt) {
		this.drNt = drNt;
	}

	public GSTR9Table4OtherThanExpSezDetails getAmdPos() {
		return amdPos;
	}

	public void setAmdPos(GSTR9Table4OtherThanExpSezDetails amdPos) {
		this.amdPos = amdPos;
	}

	public GSTR9Table4OtherThanExpSezDetails getAmdNeg() {
		return amdNeg;
	}

	public void setAmdNeg(GSTR9Table4OtherThanExpSezDetails amdNeg) {
		this.amdNeg = amdNeg;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public GSTR9Table4OtherThanExpSezDetails getSubTotalAG() {
		return subTotalAG;
	}

	public void setSubTotalAG(GSTR9Table4OtherThanExpSezDetails subTotalAG) {
		this.subTotalAG = subTotalAG;
	}

	public GSTR9Table4OtherThanExpSezDetails getSubTotalIL() {
		return subTotalIL;
	}

	public void setSubTotalIL(GSTR9Table4OtherThanExpSezDetails subTotalIL) {
		this.subTotalIL = subTotalIL;
	}

	public GSTR9Table4OtherThanExpSezDetails getSupAdv() {
		return supAdv;
	}

	public void setSupAdv(GSTR9Table4OtherThanExpSezDetails supAdv) {
		this.supAdv = supAdv;
	}

	
	

}
