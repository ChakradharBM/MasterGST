/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for HSN to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
public interface HSNRepository extends MongoRepository<HSNConfig, String> {
	HSNConfig findByName(String name);
	HSNConfig findByCode(String name);
	
}
