/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.CompanyCenters;

/**
 * Repository interface for Centers to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface CompanyCentersRepository extends MongoRepository<CompanyCenters, String> {

	List<CompanyCenters> findByUserid(final String userid);
	List<CompanyCenters> findByClientid(final String clientid);
}
