package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.mastergst.core.domain.Base;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HSNDetails extends Base{
	
	private String clientid;
	private String returnPeriod;
	private String userid;
	private String returnType;
	private String importtype;
	
	private List<HSNData> hsnData = Lists.newArrayList();
	
	public HSNDetails() {

	}
	
	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getReturnPeriod() {
		return returnPeriod;
	}

	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public List<HSNData> getHsnData() {
		return hsnData;
	}

	public void setHsnData(List<HSNData> hsnData) {
		this.hsnData = hsnData;
	}

	public String getImporttype() {
		return importtype;
	}

	public void setImporttype(String importtype) {
		this.importtype = importtype;
	}
}
