/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.CompanyUser;

/**
 * Repository interface for User to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface CompanyUserRepository extends MongoRepository<CompanyUser, String> {

	List<CompanyUser> findByUserid(final String userid);
	List<CompanyUser> findByClientid(final String clientid);
	List<CompanyUser> findByUseridAndClientid(final String userid, final String clientid);
	List<CompanyUser> findByUseridAndIsglobal(final String userid, final String isglobal);
	List<CompanyUser> findByUseridAndClientidIsNull(final String userid);
	List<CompanyUser> findByUseridAndCompanyIn(final String userid, List<String> company);
	CompanyUser findByEmail(final String email);
	CompanyUser findByEmailAndCompanyIn(final String email, List<String> company);
	List<CompanyUser> findByCompanyIn(List<String> company);
	List<CompanyUser> findAllByUserid(final String userid);
	CompanyUser findByEmailAndIsglobal(final String clientid,final String isglobal);
}
