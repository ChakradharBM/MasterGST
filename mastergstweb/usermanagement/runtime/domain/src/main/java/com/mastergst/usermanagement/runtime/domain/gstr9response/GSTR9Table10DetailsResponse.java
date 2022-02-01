package com.mastergst.usermanagement.runtime.domain.gstr9response;

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
@JsonIgnoreProperties({ "id" })
public class GSTR9Table10DetailsResponse {
	@Id
	private ObjectId id;

	String flag;
	@JsonProperty("dbn_amd")
	GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse dbnAmd = new GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse();
	@JsonProperty("cdn_amd")
	GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse cdnAmd = new GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse();
	@JsonProperty("itc_rvsl")
	// GSTR9Table10SummaryDetails itcRvsl = new GSTR9Table10SummaryDetails();
	GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse itcRvsl = new GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse();
	@JsonProperty("itc_availd")
	// GSTR9Table10SummaryDetails itcAvaild = new GSTR9Table10SummaryDetails();
	GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse itcAvaild = new GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse();

	@JsonProperty("total_turnover")
	// GSTR9Table10SummaryDetails totalTurnover = new GSTR9Table10SummaryDetails();
	GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse totalTurnover = new GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse();

	public GSTR9Table10DetailsResponse() {
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

	public GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse getDbnAmd() {
		return dbnAmd;
	}

	public void setDbnAmd(GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse dbnAmd) {
		this.dbnAmd = dbnAmd;
	}

	public GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse getCdnAmd() {
		return cdnAmd;
	}

	public void setCdnAmd(GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse cdnAmd) {
		this.cdnAmd = cdnAmd;
	}

	public GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse getItcRvsl() {
		return itcRvsl;
	}

	public void setItcRvsl(GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse itcRvsl) {
		this.itcRvsl = itcRvsl;
	}

	public GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse getItcAvaild() {
		return itcAvaild;
	}

	public void setItcAvaild(GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse itcAvaild) {
		this.itcAvaild = itcAvaild;
	}

	public GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse getTotalTurnover() {
		return totalTurnover;
	}

	public void setTotalTurnover(GSTR9Table10CreditAndDebitNoteSummaryDetailsResponse totalTurnover) {
		this.totalTurnover = totalTurnover;
	}
}
