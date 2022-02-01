package com.mastergst.usermanagement.runtime.domain;

import java.util.Map;

public class DashboardTotal {
	
	Client client;
	Map<String, Map<String, String>> summaryMap;
	
	
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public Map<String, Map<String, String>> getSummaryMap() {
		return summaryMap;
	}
	public void setSummaryMap(Map<String, Map<String, String>> summaryMap) {
		this.summaryMap = summaryMap;
	}
	
	

}
