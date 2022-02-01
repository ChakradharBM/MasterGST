/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for UQC to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
public interface UQCRepository extends MongoRepository<UQCConfig, String> {
	UQCConfig findByName(final String name);
	UQCConfig findByNameIgnoreCase(final String name);
	UQCConfig findByCodeIgnoreCase(final String code);
	
}
