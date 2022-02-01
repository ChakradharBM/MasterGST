/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.CompanyGroup;

/**
 * Repository interface for Group to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface CompanyGroupRepository extends MongoRepository<CompanyGroup, String> {

	List<CompanyGroup> findByUserid(final String userid);
	List<CompanyGroup> findByClientid(final String clientid);
	List<CompanyGroup> findByUseridAndClientid(final String userid, final String clientid);
	List<CompanyGroup> findByUseridAndClientidIsNull(final String userid);
}
