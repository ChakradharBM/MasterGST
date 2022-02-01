/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for Role to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface RoleRepository extends MongoRepository<Role, String> {

	Role findByName(final String name);
	List<Role> findByNameIn(final List<String> nameList);
	List<Role> findByCategory(final String category);
}
