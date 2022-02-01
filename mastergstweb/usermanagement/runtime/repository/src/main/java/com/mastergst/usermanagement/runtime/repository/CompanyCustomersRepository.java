/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;

/**
 * Repository interface for Customers to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface CompanyCustomersRepository extends MongoRepository<CompanyCustomers, String> {

	List<CompanyCustomers> findByUserid(final String userid);
	List<CompanyCustomers> findByClientid(final String clientid);
	List<CompanyCustomers> findByUseridAndClientid(final String userid,final String clientid);
	CompanyCustomers findByClientidAndCustomerId(final String clientid,final String customerid);
	CompanyCustomers findByNameAndClientid(final String name,final String clientid);
	CompanyCustomers findByGstnnumberAndClientid(final String gstnnumber,final String clientid);
	CompanyCustomers findByGstnnumberAndClientidAndName(final String gstnnumber,final String clientid,final String Name);
	CompanyCustomers findByClientidAndCustomerIdAndName(final String clientid,final String customerid,final String name);
}
