package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class TdsTcsCredit {

	@Id
	private ObjectId id;
	
	private List<TDSCreditData> tds=LazyList.decorate(new ArrayList<TDSCreditData>(), 
			FactoryUtils.instantiateFactory(TDSCreditData.class));
	
	private List<TDSACreditData> tdsa=LazyList.decorate(new ArrayList<TDSACreditData>(), 
			FactoryUtils.instantiateFactory(TDSACreditData.class));
	
	private List<TCSCreditData> tcs=LazyList.decorate(new ArrayList<TCSCreditData>(), 
			FactoryUtils.instantiateFactory(TCSCreditData.class));
	
	private List<TCSACreditData> tcsa=LazyList.decorate(new ArrayList<TCSACreditData>(), 
			FactoryUtils.instantiateFactory(TCSACreditData.class));
	
	SummaryData summary = new SummaryData();
	
	@JsonProperty("req_time")
	@DateTimeFormat(pattern = "DD-MM-YYYY HH:mm")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "DD-MM-YYYY HH:mm")
	private Date reqTime;

	public TdsTcsCredit() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<TDSCreditData> getTds() {
		return tds;
	}

	public void setTds(List<TDSCreditData> tds) {
		this.tds = tds;
	}

	public List<TDSACreditData> getTdsa() {
		return tdsa;
	}

	public void setTdsa(List<TDSACreditData> tdsa) {
		this.tdsa = tdsa;
	}

	public List<TCSCreditData> getTcs() {
		return tcs;
	}

	public void setTcs(List<TCSCreditData> tcs) {
		this.tcs = tcs;
	}

	public List<TCSACreditData> getTcsa() {
		return tcsa;
	}

	public void setTcsa(List<TCSACreditData> tcsa) {
		this.tcsa = tcsa;
	}

	public SummaryData getSummary() {
		return summary;
	}

	public void setSummary(SummaryData summary) {
		this.summary = summary;
	}

	public Date getReqTime() {
		return reqTime;
	}

	public void setReqTime(Date reqTime) {
		this.reqTime = reqTime;
	}
	
	
}
