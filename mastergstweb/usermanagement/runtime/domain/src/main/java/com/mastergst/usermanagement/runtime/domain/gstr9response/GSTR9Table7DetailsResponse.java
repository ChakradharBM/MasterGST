package com.mastergst.usermanagement.runtime.domain.gstr9response;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR9 Table7 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9Table7DetailsResponse {

	@Id
	private ObjectId id;
	
	String flag;
	GSTR9Table7RuleSummaryDetailsResponse rule37 = new GSTR9Table7RuleSummaryDetailsResponse();
	GSTR9Table7RuleSummaryDetailsResponse rule39 = new GSTR9Table7RuleSummaryDetailsResponse();
	GSTR9Table7RuleSummaryDetailsResponse rule42 = new GSTR9Table7RuleSummaryDetailsResponse();
	GSTR9Table7RuleSummaryDetailsResponse rule43 = new GSTR9Table7RuleSummaryDetailsResponse();
	GSTR9Table7RuleSummaryDetailsResponse sec17 = new GSTR9Table7RuleSummaryDetailsResponse();
	@JsonProperty("revsl_tran1")
	GSTR9Table7ReversalSummaryDetailsResponse revslTran1 = new GSTR9Table7ReversalSummaryDetailsResponse();
	@JsonProperty("revsl_tran2")
	GSTR9Table7ReversalSummaryDetailsResponse revslTran2 = new GSTR9Table7ReversalSummaryDetailsResponse();
	List<GSTR9Table7OtherSumaryDetailsResponse> other=LazyList.decorate(new ArrayList<GSTR9Table7OtherSumaryDetailsResponse>(), 
			FactoryUtils.instantiateFactory(GSTR9Table7OtherSumaryDetailsResponse.class));
	
	@JsonProperty("tot_itc_revd")
	GSTR9Table7RuleSummaryDetailsResponse totItcRevd = new GSTR9Table7RuleSummaryDetailsResponse();
	@JsonProperty("net_itc_aval")
	GSTR9Table7RuleSummaryDetailsResponse netItcAval = new GSTR9Table7RuleSummaryDetailsResponse();
	
	
	public GSTR9Table7RuleSummaryDetailsResponse getTotItcRevd() {
		return totItcRevd;
	}

	public void setTotItcRevd(GSTR9Table7RuleSummaryDetailsResponse totItcRevd) {
		this.totItcRevd = totItcRevd;
	}

	public GSTR9Table7RuleSummaryDetailsResponse getNetItcAval() {
		return netItcAval;
	}

	public void setNetItcAval(GSTR9Table7RuleSummaryDetailsResponse netItcAval) {
		this.netItcAval = netItcAval;
	}

	public GSTR9Table7DetailsResponse() {
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

	public GSTR9Table7RuleSummaryDetailsResponse getRule37() {
		return rule37;
	}

	public void setRule37(GSTR9Table7RuleSummaryDetailsResponse rule37) {
		this.rule37 = rule37;
	}

	public GSTR9Table7RuleSummaryDetailsResponse getRule39() {
		return rule39;
	}

	public void setRule39(GSTR9Table7RuleSummaryDetailsResponse rule39) {
		this.rule39 = rule39;
	}

	public GSTR9Table7RuleSummaryDetailsResponse getRule42() {
		return rule42;
	}

	public void setRule42(GSTR9Table7RuleSummaryDetailsResponse rule42) {
		this.rule42 = rule42;
	}

	public GSTR9Table7RuleSummaryDetailsResponse getRule43() {
		return rule43;
	}

	public void setRule43(GSTR9Table7RuleSummaryDetailsResponse rule43) {
		this.rule43 = rule43;
	}

	public GSTR9Table7RuleSummaryDetailsResponse getSec17() {
		return sec17;
	}

	public void setSec17(GSTR9Table7RuleSummaryDetailsResponse sec17) {
		this.sec17 = sec17;
	}

	public GSTR9Table7ReversalSummaryDetailsResponse getRevslTran1() {
		return revslTran1;
	}

	public void setRevslTran1(GSTR9Table7ReversalSummaryDetailsResponse revslTran1) {
		this.revslTran1 = revslTran1;
	}

	public GSTR9Table7ReversalSummaryDetailsResponse getRevslTran2() {
		return revslTran2;
	}

	public void setRevslTran2(GSTR9Table7ReversalSummaryDetailsResponse revslTran2) {
		this.revslTran2 = revslTran2;
	}

	public List<GSTR9Table7OtherSumaryDetailsResponse> getOther() {
		return other;
	}

	public void setOther(List<GSTR9Table7OtherSumaryDetailsResponse> other) {
		this.other = other;
	}
	
}
