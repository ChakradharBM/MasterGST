/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.CompanyRole;

/**
 * Repository interface for Role to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface CompanyRoleRepository extends MongoRepository<CompanyRole, String> {

	List<CompanyRole> findByUserid(final String userid);
	List<CompanyRole> findByUseridAndClientid(final String userid, final String clientid);
	List<CompanyRole> findByUseridAndClientidIsNull(final String userid);
	CompanyRole findByClientidAndUseridIsNull(final String clientid);
	CompanyRole findByName(final String name);
	CompanyRole findByUseridAndId(final String userid, final String roleid);
	
	CompanyRole findById(final String roleid);
}
