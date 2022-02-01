package com.mastergst.usermanagement.runtime.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.mastergst.usermanagement.runtime.domain.TemplateMapper;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;

public interface ImportMapperService {
	
	public Map<String, GstWorkSheet> getSalesTemplateFields();
	
	public Map<String, GstWorkSheet> getSalesTemplateFields(String simplifiedOrDetail);
	
	public Set<String> getSalesTemplateAllPages();

	public Map<String, GstWorkSheet> getPurchageTemplateFields(); 
	
	public Map<String, GstWorkSheet> getPurchageTemplateFields(String simplifiedOrDetail); 
	
	public Set<String> getPurchagesTemplateAllPages();

	public Map<String,List<String>> readMapperFile(Map<String, MultipartFile> multipartData,int skipRows); 
	
	public Map<String,Map<String,List<String>>> readMapperAndNonMapperFile(Map<String, MultipartFile> multipartData,int skipRows); 
	
	public Map<String,List<String>> readNonMapperFile(Map<String, MultipartFile> multipartData,int skipRows); 
	
	public TemplateMapper saveMapper(TemplateMapper templateMapper, Map<String,List<String>> mapperPageFields);
	
	public TemplateMapper saveMapper(TemplateMapper templateMapper, Map<String,List<String>> mapperPageFields,Map<String,List<String>> nonMapperPageFields);
	
	public TemplateMapper editMapper(String id, boolean isValid, TemplateMapper templateMapper);
	
	public List<TemplateMapper> getByUserIdAndClientIdAndMapperTypeAndIsCompleted(String userId, String clientId,String mapperType, String isCompleted);
	
	public List<TemplateMapperDoc> getMapperDocs(String userId, String clientId,String mapperType);
	
	public List<TemplateMapperDoc> getMapperDocs(String userId, String clientId);

	public TemplateMapperDoc getMapperDoc(String userId, String clientId,String mapperType, String mapperName);
	
	public TemplateMapperDoc getMapperDoc(String userId, String clientId, String mapperName);
	
	public TemplateMapper getMapper(String userId, String clientId, String mapperName);
	
	public void deleteMapper(String id);
	
	public List<TemplateMapper> getMappersByUserIdAndClientId(String userId, String clientId);
	
	public List<TemplateMapper> getMappersByClientId(String clientId);
	
	public List<TemplateMapperDoc> getMapperDocs(String clientId);
	
	public TemplateMapper getMapperById(String id);
	
	public TemplateMapper getMapper(String clientId, String mapperName);

	public Set<String> getEinvoiceTemplateAllPages();

	public Map<String, GstWorkSheet> getEinvoiceTemplateFields(String simplifiedOrDetail);

	Map<String, GstWorkSheet> getEinvoiceTemplateFields();
	
	

}
