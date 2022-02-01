package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR9 Table10 Summary information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id","chksum"})
public class GSTR9Table10Details {
	@Id
	private ObjectId id;
	
	String flag;
	String chksum;
	@JsonProperty("dbn_amd")
	GSTR9Table10CreditAndDebitNoteSummaryDetails dbnAmd = new GSTR9Table10CreditAndDebitNoteSummaryDetails();
	@JsonProperty("cdn_amd")
	GSTR9Table10CreditAndDebitNoteSummaryDetails cdnAmd = new GSTR9Table10CreditAndDebitNoteSummaryDetails();
	@JsonProperty("itc_rvsl")
	GSTR9Table10SummaryDetails itcRvsl = new GSTR9Table10SummaryDetails();
	@JsonProperty("itc_availd")
	GSTR9Table10SummaryDetails itcAvaild = new GSTR9Table10SummaryDetails();
	
	
	
	public GSTR9Table10Details() {
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

	public GSTR9Table10CreditAndDebitNoteSummaryDetails getDbnAmd() {
		return dbnAmd;
	}

	public void setDbnAmd(GSTR9Table10CreditAndDebitNoteSummaryDetails dbnAmd) {
		this.dbnAmd = dbnAmd;
	}

	public GSTR9Table10CreditAndDebitNoteSummaryDetails getCdnAmd() {
		return cdnAmd;
	}

	public void setCdnAmd(GSTR9Table10CreditAndDebitNoteSummaryDetails cdnAmd) {
		this.cdnAmd = cdnAmd;
	}

	public GSTR9Table10SummaryDetails getItcRvsl() {
		return itcRvsl;
	}

	public void setItcRvsl(GSTR9Table10SummaryDetails itcRvsl) {
		this.itcRvsl = itcRvsl;
	}

	public GSTR9Table10SummaryDetails getItcAvaild() {
		return itcAvaild;
	}

	public void setItcAvaild(GSTR9Table10SummaryDetails itcAvaild) {
		this.itcAvaild = itcAvaild;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	
	

}
