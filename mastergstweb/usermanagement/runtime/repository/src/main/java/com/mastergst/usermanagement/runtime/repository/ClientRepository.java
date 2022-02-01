/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.Client;

/**
 * Repository interface for Client to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface ClientRepository extends MongoRepository<Client, String> {

	List<Client> findByGstnameIn(final List<String> gstnames);
	
	Client findByEmailAndConfigurefirm(String email,String firm);
	Client findByIdAndConfigurefirmNull(String id);
	
	List<Client> findByGroupNameIn(final List<String> groupnames);
	List<Client> findByGroupName(String groupname);
}
