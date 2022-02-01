package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({ "id" })
public class GSTR8TCS {

	@Id
	private ObjectId id;
	private String ofp;
	private String stin;
	private String ostin;
	private Double supR;
	private Double retsupR;
	private Double supU;
	private Double retsupU;
	private Double amt;
	private Double camt;
	private Double samt;
	private Double iamt;
	private String flag;

	public GSTR8TCS() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getOfp() {
		return ofp;
	}

	public void setOfp(String ofp) {
		this.ofp = ofp;
	}

	public String getStin() {
		return stin;
	}

	public void setStin(String stin) {
		this.stin = stin;
	}

	public String getOstin() {
		return ostin;
	}

	public void setOstin(String ostin) {
		this.ostin = ostin;
	}

	public Double getSupR() {
		return supR;
	}

	public void setSupR(Double supR) {
		this.supR = supR;
	}

	public Double getRetsupR() {
		return retsupR;
	}

	public void setRetsupR(Double retsupR) {
		this.retsupR = retsupR;
	}

	public Double getSupU() {
		return supU;
	}

	public void setSupU(Double supU) {
		this.supU = supU;
	}

	public Double getRetsupU() {
		return retsupU;
	}

	public void setRetsupU(Double retsupU) {
		this.retsupU = retsupU;
	}

	public Double getAmt() {
		return amt;
	}

	public void setAmt(Double amt) {
		this.amt = amt;
	}

	public Double getCamt() {
		return camt;
	}

	public void setCamt(Double camt) {
		this.camt = camt;
	}

	public Double getSamt() {
		return samt;
	}

	public void setSamt(Double samt) {
		this.samt = samt;
	}

	public Double getIamt() {
		return iamt;
	}

	public void setIamt(Double iamt) {
		this.iamt = iamt;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "GSTR8TCS [stin=" + stin + ", supR=" + supR + ", retsupR=" + retsupR + ", supU=" + supU + ", retsupU="
				+ retsupU + ", amt=" + amt + ", camt=" + camt + ", samt=" + samt + ", iamt=" + iamt + "]";
	}
	
	
}
