package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * GSTR9 Table9 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9Table9GetDetails {
	@Id
	private ObjectId id;
	
	String flag;
	String chksum;
	GSTR9Table9SummaryGetDetails iamt = new GSTR9Table9SummaryGetDetails();
	GSTR9Table9SummaryGetDetails camt = new GSTR9Table9SummaryGetDetails();
	GSTR9Table9SummaryGetDetails samt = new GSTR9Table9SummaryGetDetails();
	GSTR9Table9SummaryGetDetails csamt = new GSTR9Table9SummaryGetDetails();
	GSTR9Table9SummaryGetDetails intr = new GSTR9Table9SummaryGetDetails();
	GSTR9Table9SummaryGetDetails fee = new GSTR9Table9SummaryGetDetails();
	GSTR9Table9SummaryGetDetails pen = new GSTR9Table9SummaryGetDetails();
	GSTR9Table9SummaryGetDetails other = new GSTR9Table9SummaryGetDetails();
	
	public GSTR9Table9GetDetails() {
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

	public GSTR9Table9SummaryGetDetails getIamt() {
		return iamt;
	}

	public void setIamt(GSTR9Table9SummaryGetDetails iamt) {
		this.iamt = iamt;
	}

	public GSTR9Table9SummaryGetDetails getCamt() {
		return camt;
	}

	public void setCamt(GSTR9Table9SummaryGetDetails camt) {
		this.camt = camt;
	}

	public GSTR9Table9SummaryGetDetails getSamt() {
		return samt;
	}

	public void setSamt(GSTR9Table9SummaryGetDetails samt) {
		this.samt = samt;
	}

	public GSTR9Table9SummaryGetDetails getCsamt() {
		return csamt;
	}

	public void setCsamt(GSTR9Table9SummaryGetDetails csamt) {
		this.csamt = csamt;
	}

	public GSTR9Table9SummaryGetDetails getIntr() {
		return intr;
	}

	public void setIntr(GSTR9Table9SummaryGetDetails intr) {
		this.intr = intr;
	}

	public GSTR9Table9SummaryGetDetails getFee() {
		return fee;
	}

	public void setFee(GSTR9Table9SummaryGetDetails fee) {
		this.fee = fee;
	}

	public GSTR9Table9SummaryGetDetails getPen() {
		return pen;
	}

	public void setPen(GSTR9Table9SummaryGetDetails pen) {
		this.pen = pen;
	}

	public GSTR9Table9SummaryGetDetails getOther() {
		return other;
	}

	public void setOther(GSTR9Table9SummaryGetDetails other) {
		this.other = other;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}
	
}
