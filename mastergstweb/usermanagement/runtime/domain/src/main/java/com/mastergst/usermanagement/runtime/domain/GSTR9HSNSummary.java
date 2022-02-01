package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@JsonIgnoreProperties({"chksum"})
public class GSTR9HSNSummary {
	
	
	String chksum;
	@JsonProperty("items")
	private List<GSTR9HSNData> items=Lists.newArrayList();
	
	public GSTR9HSNSummary() {

	}

	public List<GSTR9HSNData> getItems() {
		return items;
	}

	public void setItems(List<GSTR9HSNData> items) {
		this.items = items;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	
	

}
