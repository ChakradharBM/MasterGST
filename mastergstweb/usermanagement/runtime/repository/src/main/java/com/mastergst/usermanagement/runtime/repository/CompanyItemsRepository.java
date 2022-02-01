/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.CompanyItems;

/**
 * Repository interface for Company Items to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface CompanyItemsRepository extends MongoRepository<CompanyItems, String> {

	List<CompanyItems> findByUserid(final String userid);
	List<CompanyItems> findByClientid(final String clientid);
	CompanyItems findByClientidAndDescription(final String clientid, final String description);
	CompanyItems findByClientidAndItemTypeAndDescription(final String clientid, final String type, final String description);
	CompanyItems findByClientidAndItemTypeAndItemno(String clientid, String string, String stockItemNo);
}
