package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastergst.core.domain.Base;

@Document(collection = "gstr9c")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid","returnPeriod","matchingId","matchingStatus"})
@JsonFilter("gstr9cFilter")
public class GSTR9C extends Base{
	private String userid;
	private String fullname;
	private String clientid;
	private Date dateofinvoice;
	private String returnPeriod;
	private String matchingId;
	private String matchingStatus;
	
	@JsonProperty("audited_data")
	GSTR9CAuditedData auditedData = new GSTR9CAuditedData();
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public Date getDateofinvoice() {
		return dateofinvoice;
	}
	public void setDateofinvoice(Date dateofinvoice) {
		this.dateofinvoice = dateofinvoice;
	}
	public GSTR9CAuditedData getAuditedData() {
		return auditedData;
	}
	public void setAuditedData(GSTR9CAuditedData auditedData) {
		this.auditedData = auditedData;
	}
	public String getReturnPeriod() {
		return returnPeriod;
	}
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}
	public String getMatchingId() {
		return matchingId;
	}
	public void setMatchingId(String matchingId) {
		this.matchingId = matchingId;
	}
	public String getMatchingStatus() {
		return matchingStatus;
	}
	public void setMatchingStatus(String matchingStatus) {
		this.matchingStatus = matchingStatus;
	}
	
	
	

}
