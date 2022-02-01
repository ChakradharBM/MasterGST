/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import org.springframework.data.mongodb.repository.MongoRepository;
/**
 * Repository interface for SMS to perform CRUD operation.
 * 
 * @author Ashok Samrat
 * @version 1.0
 */
public interface SMSRepository extends MongoRepository<SMSConfig, String> {

}
