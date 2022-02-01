/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.mastergst.usermanagement.runtime.domain.PrintConfiguration;

/**
 * Repository interface for Role to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface PrintConfigurationRepository extends MongoRepository<PrintConfiguration, String> {

	PrintConfiguration findByClientid(final String clientid);
	
	@Transactional
	@Modifying
	void deleteByClientid(final String clientid);
}

