package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.TemplateMapper;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;

public interface TemplateMapperDocRepository extends MongoRepository<TemplateMapperDoc, String> {
	
	public TemplateMapperDoc getByUserIdAndClientIdAndMapperTypeAndMapperName(final String userId, final String clientId, String mapperType, String mapperName);

	public List<TemplateMapperDoc> getByUserIdAndClientIdAndMapperType(final String userId, final String clientId, String mapperType);
	
	public List<TemplateMapperDoc> getByUserIdAndClientId(final String userId, final String clientId);
	
	public TemplateMapperDoc getByUserIdAndClientIdAndMapperName(final String userId, final String clientId, String mapperName);
	
	public List<TemplateMapperDoc> findByCompanyIn(List<String> company);
	
	public List<TemplateMapperDoc> getByClientId(final String clientId);
	
	@Transactional
	@Modifying
	void deleteByUserIdAndClientIdAndMapperName(final String userId, final String clientId, String mapperName);
}
