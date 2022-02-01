package com.mastergst.usermanagement.runtime.domain.gstr9response;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
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
@JsonIgnoreProperties({ "id" })
public class GSTR9Table4DetailsResponse {
	@Id
	private ObjectId id;

	String flag;

	GSTR9Table4OtherThanExpSezDetailsResponse b2c = new GSTR9Table4OtherThanExpSezDetailsResponse();
	GSTR9Table4OtherThanExpSezDetailsResponse b2b = new GSTR9Table4OtherThanExpSezDetailsResponse();
	GSTR9Table4ExpSezDetailsResponse exp = new GSTR9Table4ExpSezDetailsResponse();
	GSTR9Table4ExpSezDetailsResponse sez = new GSTR9Table4ExpSezDetailsResponse();
	GSTR9Table4OtherThanExpSezDetailsResponse deemed = new GSTR9Table4OtherThanExpSezDetailsResponse();
	GSTR9Table4OtherThanExpSezDetailsResponse at = new GSTR9Table4OtherThanExpSezDetailsResponse();
	GSTR9Table4OtherThanExpSezDetailsResponse rchrg = new GSTR9Table4OtherThanExpSezDetailsResponse();
	@JsonProperty("cr_nt")
	GSTR9Table4OtherThanExpSezDetailsResponse crNt = new GSTR9Table4OtherThanExpSezDetailsResponse();
	@JsonProperty("dr_nt")
	GSTR9Table4OtherThanExpSezDetailsResponse drNt = new GSTR9Table4OtherThanExpSezDetailsResponse();
	@JsonProperty("amd_pos")
	GSTR9Table4OtherThanExpSezDetailsResponse amdPos = new GSTR9Table4OtherThanExpSezDetailsResponse();
	@JsonProperty("amd_neg")
	GSTR9Table4OtherThanExpSezDetailsResponse amdNeg = new GSTR9Table4OtherThanExpSezDetailsResponse();

	@JsonProperty("sub_totalAG")
	GSTR9Table4OtherThanExpSezDetailsResponse subTotalAG = new GSTR9Table4OtherThanExpSezDetailsResponse();
	@JsonProperty("sub_totalIL")
	GSTR9Table4OtherThanExpSezDetailsResponse subTotalIL = new GSTR9Table4OtherThanExpSezDetailsResponse();
	@JsonProperty("sup_adv")
	GSTR9Table4OtherThanExpSezDetailsResponse supAdv = new GSTR9Table4OtherThanExpSezDetailsResponse();

	public GSTR9Table4DetailsResponse() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public GSTR9Table4OtherThanExpSezDetailsResponse getB2c() {
		return b2c;
	}

	public void setB2c(GSTR9Table4OtherThanExpSezDetailsResponse b2c) {
		this.b2c = b2c;
	}

	public GSTR9Table4OtherThanExpSezDetailsResponse getB2b() {
		return b2b;
	}

	public void setB2b(GSTR9Table4OtherThanExpSezDetailsResponse b2b) {
		this.b2b = b2b;
	}

	public GSTR9Table4ExpSezDetailsResponse getExp() {
		return exp;
	}

	public void setExp(GSTR9Table4ExpSezDetailsResponse exp) {
		this.exp = exp;
	}

	public GSTR9Table4ExpSezDetailsResponse getSez() {
		return sez;
	}

	public void setSez(GSTR9Table4ExpSezDetailsResponse sez) {
		this.sez = sez;
	}

	public GSTR9Table4OtherThanExpSezDetailsResponse getDeemed() {
		return deemed;
	}

	public void setDeemed(GSTR9Table4OtherThanExpSezDetailsResponse deemed) {
		this.deemed = deemed;
	}

	public GSTR9Table4OtherThanExpSezDetailsResponse getAt() {
		return at;
	}

	public void setAt(GSTR9Table4OtherThanExpSezDetailsResponse at) {
		this.at = at;
	}

	public GSTR9Table4OtherThanExpSezDetailsResponse getRchrg() {
		return rchrg;
	}

	public void setRchrg(GSTR9Table4OtherThanExpSezDetailsResponse rchrg) {
		this.rchrg = rchrg;
	}

	public GSTR9Table4OtherThanExpSezDetailsResponse getCrNt() {
		return crNt;
	}

	public void setCrNt(GSTR9Table4OtherThanExpSezDetailsResponse crNt) {
		this.crNt = crNt;
	}

	public GSTR9Table4OtherThanExpSezDetailsResponse getDrNt() {
		return drNt;
	}

	public void setDrNt(GSTR9Table4OtherThanExpSezDetailsResponse drNt) {
		this.drNt = drNt;
	}

	public GSTR9Table4OtherThanExpSezDetailsResponse getAmdPos() {
		return amdPos;
	}

	public void setAmdPos(GSTR9Table4OtherThanExpSezDetailsResponse amdPos) {
		this.amdPos = amdPos;
	}

	public GSTR9Table4OtherThanExpSezDetailsResponse getAmdNeg() {
		return amdNeg;
	}

	public void setAmdNeg(GSTR9Table4OtherThanExpSezDetailsResponse amdNeg) {
		this.amdNeg = amdNeg;
	}

	public GSTR9Table4OtherThanExpSezDetailsResponse getSubTotalAG() {
		return subTotalAG;
	}

	public void setSubTotalAG(GSTR9Table4OtherThanExpSezDetailsResponse subTotalAG) {
		this.subTotalAG = subTotalAG;
	}

	public GSTR9Table4OtherThanExpSezDetailsResponse getSubTotalIL() {
		return subTotalIL;
	}

	public void setSubTotalIL(GSTR9Table4OtherThanExpSezDetailsResponse subTotalIL) {
		this.subTotalIL = subTotalIL;
	}

	public GSTR9Table4OtherThanExpSezDetailsResponse getSupAdv() {
		return supAdv;
	}

	public void setSupAdv(GSTR9Table4OtherThanExpSezDetailsResponse supAdv) {
		this.supAdv = supAdv;
	}
}
