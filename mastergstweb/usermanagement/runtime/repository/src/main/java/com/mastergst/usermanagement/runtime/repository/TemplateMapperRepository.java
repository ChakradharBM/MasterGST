package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.TemplateMapper;

public interface TemplateMapperRepository extends MongoRepository<TemplateMapper, String> {

	
	public List<TemplateMapper> getByUserIdAndClientId(final String userId, final String clientId);
	
	public TemplateMapper getById(final String id);
	
	public TemplateMapper getByUserIdAndClientIdAndMapperName(final String userId, final String clientId, final String mapperName);
	
	public List<TemplateMapper> getByUserIdAndClientIdAndMapperTypeAndIsCompleted(final String userId, final String clientId, String mapperType, String isCompleted);
	
	public List<TemplateMapper> findByCompanyIn(List<String> company);
	
	public List<TemplateMapper> getByClientId(final String clientId);
	
	public TemplateMapper findByCompanyInAndMapperName(List<String> company, final String mapperName);
	
	public TemplateMapper findByClientIdAndMapperName(final String clientId, final String mapperName);
	
	public TemplateMapper findByTemplateMapperdocid(final String mapperName);
}
	