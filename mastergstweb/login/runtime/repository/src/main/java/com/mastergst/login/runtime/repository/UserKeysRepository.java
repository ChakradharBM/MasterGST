/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.login.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.login.runtime.domain.UserKeys;

/**
 * Repository interface for User Keys to perform CRUD operation.
 * 
 * @author Ashok Samrat
 * @version 1.0
 */
public interface UserKeysRepository extends MongoRepository<UserKeys, String> {

}
