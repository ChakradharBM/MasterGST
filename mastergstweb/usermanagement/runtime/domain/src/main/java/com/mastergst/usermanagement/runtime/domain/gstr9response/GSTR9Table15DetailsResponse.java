package com.mastergst.usermanagement.runtime.domain.gstr9response;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR9 Table15 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9Table15DetailsResponse {

	@Id
	private ObjectId id;
	
	String flag;
	@JsonProperty("rfd_clmd")
	GSTR9table15RefundSummaryDetailsResponse rfdClmd = new GSTR9table15RefundSummaryDetailsResponse();
	@JsonProperty("rfd_sanc")
	GSTR9table15RefundSummaryDetailsResponse rfdSanc = new GSTR9table15RefundSummaryDetailsResponse();
	@JsonProperty("rfd_rejt")
	GSTR9table15RefundSummaryDetailsResponse rfdRejt = new GSTR9table15RefundSummaryDetailsResponse();
	@JsonProperty("rfd_pend")
	GSTR9table15RefundSummaryDetailsResponse rfdPend = new GSTR9table15RefundSummaryDetailsResponse();
	@JsonProperty("tax_dmnd")
	GSTR9Table15DemandtaxSummaryDetailsResponse taxDmnd = new GSTR9Table15DemandtaxSummaryDetailsResponse();
	@JsonProperty("tax_paid")
	GSTR9Table15DemandtaxSummaryDetailsResponse taxPaid = new GSTR9Table15DemandtaxSummaryDetailsResponse();
	@JsonProperty("dmnd_pend")
	GSTR9Table15DemandtaxSummaryDetailsResponse dmndPend = new GSTR9Table15DemandtaxSummaryDetailsResponse();
	
	public GSTR9Table15DetailsResponse() {
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

	public GSTR9table15RefundSummaryDetailsResponse getRfdClmd() {
		return rfdClmd;
	}

	public void setRfdClmd(GSTR9table15RefundSummaryDetailsResponse rfdClmd) {
		this.rfdClmd = rfdClmd;
	}

	public GSTR9table15RefundSummaryDetailsResponse getRfdSanc() {
		return rfdSanc;
	}

	public void setRfdSanc(GSTR9table15RefundSummaryDetailsResponse rfdSanc) {
		this.rfdSanc = rfdSanc;
	}

	public GSTR9table15RefundSummaryDetailsResponse getRfdRejt() {
		return rfdRejt;
	}

	public void setRfdRejt(GSTR9table15RefundSummaryDetailsResponse rfdRejt) {
		this.rfdRejt = rfdRejt;
	}

	public GSTR9table15RefundSummaryDetailsResponse getRfdPend() {
		return rfdPend;
	}

	public void setRfdPend(GSTR9table15RefundSummaryDetailsResponse rfdPend) {
		this.rfdPend = rfdPend;
	}

	public GSTR9Table15DemandtaxSummaryDetailsResponse getTaxDmnd() {
		return taxDmnd;
	}

	public void setTaxDmnd(GSTR9Table15DemandtaxSummaryDetailsResponse taxDmnd) {
		this.taxDmnd = taxDmnd;
	}

	public GSTR9Table15DemandtaxSummaryDetailsResponse getTaxPaid() {
		return taxPaid;
	}

	public void setTaxPaid(GSTR9Table15DemandtaxSummaryDetailsResponse taxPaid) {
		this.taxPaid = taxPaid;
	}

	public GSTR9Table15DemandtaxSummaryDetailsResponse getDmndPend() {
		return dmndPend;
	}

	public void setDmndPend(GSTR9Table15DemandtaxSummaryDetailsResponse dmndPend) {
		this.dmndPend = dmndPend;
	}
}
