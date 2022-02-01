package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR9 Table8 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id","chksum"})
public class GSTR9Table8Details {

	@Id
	private ObjectId id;
	String flag;
	String chksum;
	
	@JsonProperty("itc_inwd_supp")
	GSTR9Table8SummaryDetails itcInwdSupp = new GSTR9Table8SummaryDetails();
	@JsonProperty("itc_nt_availd")
	GSTR9Table8SummaryDetails itcNtAvaild = new GSTR9Table8SummaryDetails();
	@JsonProperty("itc_nt_eleg")
	GSTR9Table8SummaryDetails itcNtEleg = new GSTR9Table8SummaryDetails();
	@JsonProperty("iog_taxpaid")
	GSTR9Table8SummaryDetails iogTaxpaid = new GSTR9Table8SummaryDetails();
	
	
	public GSTR9Table8Details() {
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
	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public GSTR9Table8SummaryDetails getItcInwdSupp() {
		return itcInwdSupp;
	}

	public void setItcInwdSupp(GSTR9Table8SummaryDetails itcInwdSupp) {
		this.itcInwdSupp = itcInwdSupp;
	}

	public GSTR9Table8SummaryDetails getItcNtAvaild() {
		return itcNtAvaild;
	}

	public void setItcNtAvaild(GSTR9Table8SummaryDetails itcNtAvaild) {
		this.itcNtAvaild = itcNtAvaild;
	}

	public GSTR9Table8SummaryDetails getItcNtEleg() {
		return itcNtEleg;
	}

	public void setItcNtEleg(GSTR9Table8SummaryDetails itcNtEleg) {
		this.itcNtEleg = itcNtEleg;
	}

	public GSTR9Table8SummaryDetails getIogTaxpaid() {
		return iogTaxpaid;
	}

	public void setIogTaxpaid(GSTR9Table8SummaryDetails iogTaxpaid) {
		this.iogTaxpaid = iogTaxpaid;
	}

	
	
}
