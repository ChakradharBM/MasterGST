package com.mastergst.usermanagement.runtime.domain;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.google.common.collect.Lists;
import com.mastergst.core.domain.Base;

@Document(collection = "template_mapper_doc")
public class TemplateMapperDoc extends Base {
	
	private String mapperName;
	private String mapperType;
	private String userId;
	private String clientId;
	private String xmlDoc;
	private List<String> invTypes=Lists.newArrayList();
	private List<String> company;
	private boolean globaltemplate;
	
	public String getMapperName() {
		return mapperName;
	}
	public void setMapperName(String mapperName) {
		this.mapperName = mapperName;
	}
	public String getMapperType() {
		return mapperType;
	}
	public void setMapperType(String mapperType) {
		this.mapperType = mapperType;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getXmlDoc() {
		return xmlDoc;
	}
	public void setXmlDoc(String xmlDoc) {
		this.xmlDoc = xmlDoc;
	}
	public List<String> getInvTypes() {
		return invTypes;
	}
	public void setInvTypes(List<String> invTypes) {
		this.invTypes = invTypes;
	}
	public List<String> getCompany() {
		return company;
	}
	public void setCompany(List<String> company) {
		this.company = company;
	}
	public boolean isGlobaltemplate() {
		return globaltemplate;
	}
	public void setGlobaltemplate(boolean globaltemplate) {
		this.globaltemplate = globaltemplate;
	}
	
	
}
