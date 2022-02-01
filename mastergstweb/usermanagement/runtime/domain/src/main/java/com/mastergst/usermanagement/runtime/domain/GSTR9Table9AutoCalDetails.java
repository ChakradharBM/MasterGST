package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9Table9AutoCalDetails {
	@Id
	private ObjectId id;
	
	
	String chksum;
	GSTR9Table9AutoCalSummaryDetails iamt = new GSTR9Table9AutoCalSummaryDetails();
	GSTR9Table9AutoCalSummaryDetails camt = new GSTR9Table9AutoCalSummaryDetails();
	GSTR9Table9AutoCalSummaryDetails samt = new GSTR9Table9AutoCalSummaryDetails();
	GSTR9Table9AutoCalSummaryDetails csamt = new GSTR9Table9AutoCalSummaryDetails();
	GSTR9Table9AutoCalSummaryDetails intr = new GSTR9Table9AutoCalSummaryDetails();
	GSTR9Table9AutoCalSummaryDetails fee = new GSTR9Table9AutoCalSummaryDetails();
	
	
	public GSTR9Table9AutoCalDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	
	public GSTR9Table9AutoCalSummaryDetails getIamt() {
		return iamt;
	}

	public void setIamt(GSTR9Table9AutoCalSummaryDetails iamt) {
		this.iamt = iamt;
	}

	public GSTR9Table9AutoCalSummaryDetails getCamt() {
		return camt;
	}

	public void setCamt(GSTR9Table9AutoCalSummaryDetails camt) {
		this.camt = camt;
	}

	public GSTR9Table9AutoCalSummaryDetails getSamt() {
		return samt;
	}

	public void setSamt(GSTR9Table9AutoCalSummaryDetails samt) {
		this.samt = samt;
	}

	public GSTR9Table9AutoCalSummaryDetails getCsamt() {
		return csamt;
	}

	public void setCsamt(GSTR9Table9AutoCalSummaryDetails csamt) {
		this.csamt = csamt;
	}

	public GSTR9Table9AutoCalSummaryDetails getIntr() {
		return intr;
	}

	public void setIntr(GSTR9Table9AutoCalSummaryDetails intr) {
		this.intr = intr;
	}

	public GSTR9Table9AutoCalSummaryDetails getFee() {
		return fee;
	}

	public void setFee(GSTR9Table9AutoCalSummaryDetails fee) {
		this.fee = fee;
	}


	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}
}
