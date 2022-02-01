/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;

/**
 * Repository interface for Client User Mapping to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface ClientUserMappingRepository extends MongoRepository<ClientUserMapping, String>{

	List<ClientUserMapping> findByUserid(final String userid);
	List<ClientUserMapping> findByUseridIn(final List<String> useridList);
	List<ClientUserMapping> findByClientid(final String clientid);
	ClientUserMapping findByUseridAndClientid(final String userid, final String clientid);
	ClientUserMapping findByClientidAndCreatedByIsNotNull(final String clientid);
	
	List<ClientUserMapping> findByCreatedBy(final String createdBy);
	
	@Transactional
	@Modifying
	void deleteByClientid(final String clientid);
	
	@Transactional
	@Modifying
	void deleteByUserid(final String userid);
	
	@Transactional
	@Modifying
	void deleteByUseridAndClientid(final String userid, final String clientid);
}
