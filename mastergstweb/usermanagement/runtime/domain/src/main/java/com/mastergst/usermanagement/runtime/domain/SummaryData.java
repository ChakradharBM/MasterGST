package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR2X Summary Data information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class SummaryData {
	
	@Id
	private ObjectId id;
	
	private String gstin;
	private String chksum;
	@JsonProperty("ret_period")
	private String retPeriod;
	SummarySection tds = new SummarySection();
	SummarySection tdsa = new SummarySection();
	SummarySection tcs = new SummarySection();
	SummarySection tcsa = new SummarySection();
	
	public SummaryData() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getGstin() {
		return gstin;
	}
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}
	public String getChksum() {
		return chksum;
	}
	public void setChksum(String chksum) {
		this.chksum = chksum;
	}
	public String getRetPeriod() {
		return retPeriod;
	}
	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}
	public SummarySection getTds() {
		return tds;
	}
	public void setTds(SummarySection tds) {
		this.tds = tds;
	}
	public SummarySection getTdsa() {
		return tdsa;
	}
	public void setTdsa(SummarySection tdsa) {
		this.tdsa = tdsa;
	}
	public SummarySection getTcs() {
		return tcs;
	}
	public void setTcs(SummarySection tcs) {
		this.tcs = tcs;
	}
	public SummarySection getTcsa() {
		return tcsa;
	}
	public void setTcsa(SummarySection tcsa) {
		this.tcsa = tcsa;
	}
	
	

}
