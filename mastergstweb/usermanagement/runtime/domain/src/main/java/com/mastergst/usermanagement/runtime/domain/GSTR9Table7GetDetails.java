package com.mastergst.usermanagement.runtime.domain;

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
public class GSTR9Table7GetDetails {

	@Id
	private ObjectId id;
	
	String chksum;
	GSTR9Table7RuleSummaryDetails rule37 = new GSTR9Table7RuleSummaryDetails();
	GSTR9Table7RuleSummaryDetails rule39 = new GSTR9Table7RuleSummaryDetails();
	GSTR9Table7RuleSummaryDetails rule42 = new GSTR9Table7RuleSummaryDetails();
	GSTR9Table7RuleSummaryDetails rule43 = new GSTR9Table7RuleSummaryDetails();
	GSTR9Table7RuleSummaryDetails sec17 = new GSTR9Table7RuleSummaryDetails();
	@JsonProperty("revsl_tran1")
	GSTR9Table7ReversalSummaryDetails revslTran1 = new GSTR9Table7ReversalSummaryDetails();
	@JsonProperty("revsl_tran2")
	GSTR9Table7ReversalSummaryDetails revslTran2 = new GSTR9Table7ReversalSummaryDetails();
	List<GSTR9Table7OtherSumaryDetails> other=LazyList.decorate(new ArrayList<GSTR9Table7OtherSumaryDetails>(), 
			FactoryUtils.instantiateFactory(GSTR9Table7OtherSumaryDetails.class));
	
	@JsonProperty("tot_itc_revd")
	GSTR9Table4OtherThanExpSezDetails totItcRevd = new GSTR9Table4OtherThanExpSezDetails();
	@JsonProperty("net_itc_aval")
	GSTR9Table4OtherThanExpSezDetails netItcAval = new GSTR9Table4OtherThanExpSezDetails();
	
	public GSTR9Table7GetDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public GSTR9Table7RuleSummaryDetails getRule37() {
		return rule37;
	}

	public void setRule37(GSTR9Table7RuleSummaryDetails rule37) {
		this.rule37 = rule37;
	}

	public GSTR9Table7RuleSummaryDetails getRule39() {
		return rule39;
	}

	public void setRule39(GSTR9Table7RuleSummaryDetails rule39) {
		this.rule39 = rule39;
	}

	public GSTR9Table7RuleSummaryDetails getRule42() {
		return rule42;
	}

	public void setRule42(GSTR9Table7RuleSummaryDetails rule42) {
		this.rule42 = rule42;
	}

	public GSTR9Table7RuleSummaryDetails getRule43() {
		return rule43;
	}

	public void setRule43(GSTR9Table7RuleSummaryDetails rule43) {
		this.rule43 = rule43;
	}

	public GSTR9Table7RuleSummaryDetails getSec17() {
		return sec17;
	}

	public void setSec17(GSTR9Table7RuleSummaryDetails sec17) {
		this.sec17 = sec17;
	}

	public GSTR9Table7ReversalSummaryDetails getRevslTran1() {
		return revslTran1;
	}

	public void setRevslTran1(GSTR9Table7ReversalSummaryDetails revslTran1) {
		this.revslTran1 = revslTran1;
	}

	public GSTR9Table7ReversalSummaryDetails getRevslTran2() {
		return revslTran2;
	}

	public void setRevslTran2(GSTR9Table7ReversalSummaryDetails revslTran2) {
		this.revslTran2 = revslTran2;
	}

	public List<GSTR9Table7OtherSumaryDetails> getOther() {
		return other;
	}

	public void setOther(List<GSTR9Table7OtherSumaryDetails> other) {
		this.other = other;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public GSTR9Table4OtherThanExpSezDetails getTotItcRevd() {
		return totItcRevd;
	}

	public void setTotItcRevd(GSTR9Table4OtherThanExpSezDetails totItcRevd) {
		this.totItcRevd = totItcRevd;
	}

	public GSTR9Table4OtherThanExpSezDetails getNetItcAval() {
		return netItcAval;
	}

	public void setNetItcAval(GSTR9Table4OtherThanExpSezDetails netItcAval) {
		this.netItcAval = netItcAval;
	}
	
	
	
}
