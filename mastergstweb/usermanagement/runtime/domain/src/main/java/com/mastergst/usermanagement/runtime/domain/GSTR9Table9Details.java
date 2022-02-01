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
@JsonIgnoreProperties({"id","chksum"})
public class GSTR9Table9Details {
	@Id
	private ObjectId id;
	
	String flag;
	String chksum;
	GSTR9Table9SummaryDetails iamt = new GSTR9Table9SummaryDetails();
	GSTR9Table9SummaryDetails camt = new GSTR9Table9SummaryDetails();
	GSTR9Table9SummaryDetails samt = new GSTR9Table9SummaryDetails();
	GSTR9Table9SummaryDetails csamt = new GSTR9Table9SummaryDetails();
	GSTR9Table9SummaryDetails intr = new GSTR9Table9SummaryDetails();
	GSTR9Table9SummaryDetails fee = new GSTR9Table9SummaryDetails();
	GSTR9Table9SummaryDetails pen = new GSTR9Table9SummaryDetails();
	GSTR9Table9SummaryDetails other = new GSTR9Table9SummaryDetails();
	
	public GSTR9Table9Details() {
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

	public GSTR9Table9SummaryDetails getIamt() {
		return iamt;
	}

	public void setIamt(GSTR9Table9SummaryDetails iamt) {
		this.iamt = iamt;
	}

	public GSTR9Table9SummaryDetails getCamt() {
		return camt;
	}

	public void setCamt(GSTR9Table9SummaryDetails camt) {
		this.camt = camt;
	}

	public GSTR9Table9SummaryDetails getSamt() {
		return samt;
	}

	public void setSamt(GSTR9Table9SummaryDetails samt) {
		this.samt = samt;
	}

	public GSTR9Table9SummaryDetails getCsamt() {
		return csamt;
	}

	public void setCsamt(GSTR9Table9SummaryDetails csamt) {
		this.csamt = csamt;
	}

	public GSTR9Table9SummaryDetails getIntr() {
		return intr;
	}

	public void setIntr(GSTR9Table9SummaryDetails intr) {
		this.intr = intr;
	}

	public GSTR9Table9SummaryDetails getFee() {
		return fee;
	}

	public void setFee(GSTR9Table9SummaryDetails fee) {
		this.fee = fee;
	}

	public GSTR9Table9SummaryDetails getPen() {
		return pen;
	}

	public void setPen(GSTR9Table9SummaryDetails pen) {
		this.pen = pen;
	}

	public GSTR9Table9SummaryDetails getOther() {
		return other;
	}

	public void setOther(GSTR9Table9SummaryDetails other) {
		this.other = other;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}
	
}
