package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * GSTR2X Summary Section Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class SummarySection {
	
	@Id
	private ObjectId id;
	
	SummaryDetailData accepted = new SummaryDetailData();
	SummaryDetailData rejected = new SummaryDetailData();
	
	public SummarySection() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public SummaryDetailData getAccepted() {
		return accepted;
	}
	public void setAccepted(SummaryDetailData accepted) {
		this.accepted = accepted;
	}
	public SummaryDetailData getRejected() {
		return rejected;
	}
	public void setRejected(SummaryDetailData rejected) {
		this.rejected = rejected;
	}
	

}
