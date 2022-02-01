package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR2X Summary Detail Data invoices information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class SummaryDetailData {
	
	@Id
	private ObjectId id;
	@JsonProperty("tot_amt")
	private Double totAmt;
	@JsonProperty("tot_iamt")
	private Double totIamt;
	@JsonProperty("tot_camt")
	private Double totCamt;
	@JsonProperty("tot_samt")
	private Double totSamt;
	@JsonProperty("tot_count")
	private Double totCount;
	
	public SummaryDetailData() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public Double getTotAmt() {
		return totAmt;
	}
	public void setTotAmt(Double totAmt) {
		this.totAmt = totAmt;
	}
	public Double getTotIamt() {
		return totIamt;
	}
	public void setTotIamt(Double totIamt) {
		this.totIamt = totIamt;
	}
	public Double getTotCamt() {
		return totCamt;
	}
	public void setTotCamt(Double totCamt) {
		this.totCamt = totCamt;
	}
	public Double getTotSamt() {
		return totSamt;
	}
	public void setTotSamt(Double totSamt) {
		this.totSamt = totSamt;
	}
	public Double getTotCount() {
		return totCount;
	}
	public void setTotCount(Double totCount) {
		this.totCount = totCount;
	}

}
