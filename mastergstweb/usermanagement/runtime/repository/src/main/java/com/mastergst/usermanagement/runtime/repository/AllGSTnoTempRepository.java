/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.AllGSTINTempData;

/**
 * Repository interface for Customers to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface AllGSTnoTempRepository extends MongoRepository<AllGSTINTempData, String> {

	
	List<AllGSTINTempData> findByClientid(final String clientid);
	List<AllGSTINTempData> findByUserid(final String userid);
	AllGSTINTempData findByUseridAndGstno(final String userid , final String gstno);
	
	List<AllGSTINTempData> findByUseridAndGstnoAndStatusNull(String userid , final String gstno);
	AllGSTINTempData findByUseridAndGstnoAndStatusNotNull(String userid , final String gstno);
	@Transactional
	@Modifying
	void deleteByUseridAndGstnoAndStatusNull(String userid , final String gstno);
}
