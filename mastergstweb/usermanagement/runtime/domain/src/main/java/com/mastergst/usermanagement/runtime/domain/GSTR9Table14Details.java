package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * GSTR9 Table14 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id","chksum"})
public class GSTR9Table14Details {

	@Id
	private ObjectId id;
	
	String flag;
	String chksum;
	GSTR9Table14SummaryDetails iamt = new GSTR9Table14SummaryDetails();
	GSTR9Table14SummaryDetails samt = new GSTR9Table14SummaryDetails();
	GSTR9Table14SummaryDetails camt = new GSTR9Table14SummaryDetails();
	GSTR9Table14SummaryDetails csamt = new GSTR9Table14SummaryDetails();
	GSTR9Table14SummaryDetails intr = new GSTR9Table14SummaryDetails();
	
	public GSTR9Table14Details() {
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

	public GSTR9Table14SummaryDetails getIamt() {
		return iamt;
	}

	public void setIamt(GSTR9Table14SummaryDetails iamt) {
		this.iamt = iamt;
	}

	public GSTR9Table14SummaryDetails getSamt() {
		return samt;
	}

	public void setSamt(GSTR9Table14SummaryDetails samt) {
		this.samt = samt;
	}

	public GSTR9Table14SummaryDetails getCamt() {
		return camt;
	}

	public void setCamt(GSTR9Table14SummaryDetails camt) {
		this.camt = camt;
	}

	public GSTR9Table14SummaryDetails getCsamt() {
		return csamt;
	}

	public void setCsamt(GSTR9Table14SummaryDetails csamt) {
		this.csamt = csamt;
	}

	public GSTR9Table14SummaryDetails getIntr() {
		return intr;
	}

	public void setIntr(GSTR9Table14SummaryDetails intr) {
		this.intr = intr;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}
	
	
}
