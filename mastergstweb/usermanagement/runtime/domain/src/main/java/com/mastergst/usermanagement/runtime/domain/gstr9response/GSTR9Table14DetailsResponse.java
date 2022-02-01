package com.mastergst.usermanagement.runtime.domain.gstr9response;

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
@JsonIgnoreProperties({"id"})
public class GSTR9Table14DetailsResponse {

	@Id
	private ObjectId id;
	
	String flag;
	
	GSTR9Table14SummaryDetailsResponse iamt = new GSTR9Table14SummaryDetailsResponse();
	GSTR9Table14SummaryDetailsResponse samt = new GSTR9Table14SummaryDetailsResponse();
	GSTR9Table14SummaryDetailsResponse camt = new GSTR9Table14SummaryDetailsResponse();
	GSTR9Table14SummaryDetailsResponse csamt = new GSTR9Table14SummaryDetailsResponse();
	GSTR9Table14SummaryDetailsResponse intr = new GSTR9Table14SummaryDetailsResponse();
	
	public GSTR9Table14DetailsResponse() {
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

	public GSTR9Table14SummaryDetailsResponse getIamt() {
		return iamt;
	}

	public void setIamt(GSTR9Table14SummaryDetailsResponse iamt) {
		this.iamt = iamt;
	}

	public GSTR9Table14SummaryDetailsResponse getSamt() {
		return samt;
	}

	public void setSamt(GSTR9Table14SummaryDetailsResponse samt) {
		this.samt = samt;
	}

	public GSTR9Table14SummaryDetailsResponse getCamt() {
		return camt;
	}

	public void setCamt(GSTR9Table14SummaryDetailsResponse camt) {
		this.camt = camt;
	}

	public GSTR9Table14SummaryDetailsResponse getCsamt() {
		return csamt;
	}

	public void setCsamt(GSTR9Table14SummaryDetailsResponse csamt) {
		this.csamt = csamt;
	}

	public GSTR9Table14SummaryDetailsResponse getIntr() {
		return intr;
	}

	public void setIntr(GSTR9Table14SummaryDetailsResponse intr) {
		this.intr = intr;
	}
	
	
}
