package com.mastergst.usermanagement.runtime.domain;

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
@JsonIgnoreProperties({"id","chksum"})
public class GSTR9Table15Details {

	@Id
	private ObjectId id;
	
	String flag;
	String chksum;
	@JsonProperty("rfd_clmd")
	GSTR9table15RefundSummaryDetails rfdClmd = new GSTR9table15RefundSummaryDetails();
	@JsonProperty("rfd_sanc")
	GSTR9table15RefundSummaryDetails rfdSanc = new GSTR9table15RefundSummaryDetails();
	@JsonProperty("rfd_rejt")
	GSTR9table15RefundSummaryDetails rfdRejt = new GSTR9table15RefundSummaryDetails();
	@JsonProperty("rfd_pend")
	GSTR9table15RefundSummaryDetails rfdPend = new GSTR9table15RefundSummaryDetails();
	@JsonProperty("tax_dmnd")
	GSTR9Table15DemandtaxSummaryDetails taxDmnd = new GSTR9Table15DemandtaxSummaryDetails();
	@JsonProperty("tax_paid")
	GSTR9Table15DemandtaxSummaryDetails taxPaid = new GSTR9Table15DemandtaxSummaryDetails();
	@JsonProperty("dmnd_tax_pend")
	GSTR9Table15DemandtaxSummaryDetails dmndTaxPend = new GSTR9Table15DemandtaxSummaryDetails();
	
	
	
	public GSTR9Table15Details() {
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

	public GSTR9table15RefundSummaryDetails getRfdClmd() {
		return rfdClmd;
	}

	public void setRfdClmd(GSTR9table15RefundSummaryDetails rfdClmd) {
		this.rfdClmd = rfdClmd;
	}

	public GSTR9table15RefundSummaryDetails getRfdSanc() {
		return rfdSanc;
	}

	public void setRfdSanc(GSTR9table15RefundSummaryDetails rfdSanc) {
		this.rfdSanc = rfdSanc;
	}

	public GSTR9table15RefundSummaryDetails getRfdRejt() {
		return rfdRejt;
	}

	public void setRfdRejt(GSTR9table15RefundSummaryDetails rfdRejt) {
		this.rfdRejt = rfdRejt;
	}

	public GSTR9table15RefundSummaryDetails getRfdPend() {
		return rfdPend;
	}

	public void setRfdPend(GSTR9table15RefundSummaryDetails rfdPend) {
		this.rfdPend = rfdPend;
	}

	public GSTR9Table15DemandtaxSummaryDetails getTaxDmnd() {
		return taxDmnd;
	}

	public void setTaxDmnd(GSTR9Table15DemandtaxSummaryDetails taxDmnd) {
		this.taxDmnd = taxDmnd;
	}

	public GSTR9Table15DemandtaxSummaryDetails getTaxPaid() {
		return taxPaid;
	}

	public void setTaxPaid(GSTR9Table15DemandtaxSummaryDetails taxPaid) {
		this.taxPaid = taxPaid;
	}

	public GSTR9Table15DemandtaxSummaryDetails getDmndTaxPend() {
		return dmndTaxPend;
	}

	public void setDmndTaxPend(GSTR9Table15DemandtaxSummaryDetails dmndTaxPend) {
		this.dmndTaxPend = dmndTaxPend;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	
	
	
}
