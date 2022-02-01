/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.CompanyServices;

/**
 * Repository interface for Services to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface CompanyServicesRepository extends MongoRepository<CompanyServices, String> {

	List<CompanyServices> findByUserid(final String userid);
	List<CompanyServices> findByClientid(final String clientid);
}
