package com.mastergst.usermanagement.runtime.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Mapper configuration
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "template_mapper")
public class TemplateMapper extends Base {
	
	private String mapperName;
	private String mapperType;
	private String userId;
	private String clientId;
	private String isCompleted;
	private String skipRows;
	private boolean globaltemplate;
	private Map<String, Map> mapperConfig;
	private Map<String,List<String>> mapperPageFields;
	private Map<String,List<String>> nonMapperPageFields;
	private List<String> company;
	private String templateMapperdocid;
	private String simplifiedOrDetail;
	private LinkedHashMap<String,String> discountConfig;
	
	
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
	public String getMapperType() {
		return mapperType;
	}
	public void setMapperType(String mapperType) {
		this.mapperType = mapperType;
	}
	public String getMapperName() {
		return mapperName;
	}
	public void setMapperName(String mapperName) {
		this.mapperName = mapperName;
	}
	public boolean isGlobaltemplate() {
		return globaltemplate;
	}
	public void setGlobaltemplate(boolean globaltemplate) {
		this.globaltemplate = globaltemplate;
	}
	public Map<String, Map> getMapperConfig() {
		return mapperConfig;
	}
	public void setMapperConfig(Map<String, Map> mapperConfig) {
		this.mapperConfig = mapperConfig;
	}
	public String getIsCompleted() {
		return isCompleted;
	}
	public void setIsCompleted(String isCompleted) {
		this.isCompleted = isCompleted;
	}
	public Map<String, List<String>> getMapperPageFields() {
		return mapperPageFields;
	}
	public void setMapperPageFields(Map<String, List<String>> mapperPageFields) {
		this.mapperPageFields = mapperPageFields;
	}
	public List<String> getCompany() {
		return company;
	}
	public void setCompany(List<String> company) {
		this.company = company;
	}
	public String getTemplateMapperdocid() {
		return templateMapperdocid;
	}
	public void setTemplateMapperdocid(String templateMapperdocid) {
		this.templateMapperdocid = templateMapperdocid;
	}
	public String getSkipRows() {
		return skipRows;
	}
	public void setSkipRows(String skipRows) {
		this.skipRows = skipRows;
	}
	public Map<String, List<String>> getNonMapperPageFields() {
		return nonMapperPageFields;
	}
	public void setNonMapperPageFields(Map<String, List<String>> nonMapperPageFields) {
		this.nonMapperPageFields = nonMapperPageFields;
	}
	public String getSimplifiedOrDetail() {
		return simplifiedOrDetail;
	}
	public void setSimplifiedOrDetail(String simplifiedOrDetail) {
		this.simplifiedOrDetail = simplifiedOrDetail;
	}
	public LinkedHashMap<String, String> getDiscountConfig() {
		return discountConfig;
	}
	public void setDiscountConfig(LinkedHashMap<String, String> discountConfig) {
		this.discountConfig = discountConfig;
	}
	
	
}
