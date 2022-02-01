/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.CompanySuppliers;

/**
 * Repository interface for Customers to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface CompanySuppliersRepository extends MongoRepository<CompanySuppliers, String> {

	List<CompanySuppliers> findByUserid(final String userid);
	List<CompanySuppliers> findByClientid(final String clientid);
	List<CompanySuppliers> findByUseridAndClientid(final String userid,final String clientid);
	CompanySuppliers findByClientidAndSupplierCustomerId(final String clientid, final String supplierid);
	CompanySuppliers findByNameAndClientid(final String name,final String clientid);
}
