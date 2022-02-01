package com.mastergst.usermanagement.runtime.domain.gstr9response;

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
public class GSTR9Table9DetailsResponse {
	@Id
	private ObjectId id;
	
	String flag;
	
	GSTR9Table9SummaryDetailsResponse iamt = new GSTR9Table9SummaryDetailsResponse();
	GSTR9Table9SummaryDetailsResponse camt = new GSTR9Table9SummaryDetailsResponse();
	GSTR9Table9SummaryDetailsResponse samt = new GSTR9Table9SummaryDetailsResponse();
	GSTR9Table9SummaryDetailsResponse csamt = new GSTR9Table9SummaryDetailsResponse();
	GSTR9Table9SummaryDetailsResponse intr = new GSTR9Table9SummaryDetailsResponse();
	GSTR9Table9SummaryDetailsResponse tee = new GSTR9Table9SummaryDetailsResponse();
	GSTR9Table9SummaryDetailsResponse pen = new GSTR9Table9SummaryDetailsResponse();
	GSTR9Table9SummaryDetailsResponse other = new GSTR9Table9SummaryDetailsResponse();
	
	public GSTR9Table9DetailsResponse() {
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

	public GSTR9Table9SummaryDetailsResponse getIamt() {
		return iamt;
	}

	public void setIamt(GSTR9Table9SummaryDetailsResponse iamt) {
		this.iamt = iamt;
	}

	public GSTR9Table9SummaryDetailsResponse getCamt() {
		return camt;
	}

	public void setCamt(GSTR9Table9SummaryDetailsResponse camt) {
		this.camt = camt;
	}

	public GSTR9Table9SummaryDetailsResponse getSamt() {
		return samt;
	}

	public void setSamt(GSTR9Table9SummaryDetailsResponse samt) {
		this.samt = samt;
	}

	public GSTR9Table9SummaryDetailsResponse getCsamt() {
		return csamt;
	}

	public void setCsamt(GSTR9Table9SummaryDetailsResponse csamt) {
		this.csamt = csamt;
	}

	public GSTR9Table9SummaryDetailsResponse getIntr() {
		return intr;
	}

	public void setIntr(GSTR9Table9SummaryDetailsResponse intr) {
		this.intr = intr;
	}

	public GSTR9Table9SummaryDetailsResponse getTee() {
		return tee;
	}

	public void setTee(GSTR9Table9SummaryDetailsResponse tee) {
		this.tee = tee;
	}

	public GSTR9Table9SummaryDetailsResponse getPen() {
		return pen;
	}

	public void setPen(GSTR9Table9SummaryDetailsResponse pen) {
		this.pen = pen;
	}

	public GSTR9Table9SummaryDetailsResponse getOther() {
		return other;
	}

	public void setOther(GSTR9Table9SummaryDetailsResponse other) {
		this.other = other;
	}
	
}
