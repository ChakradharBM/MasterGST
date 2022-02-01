/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR3B;

/**
 * Repository interface for Invoice to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface GSTR3BRepository extends MongoRepository<GSTR3B, String> {

	List<GSTR3B> findByClientid(final String clientid);
	GSTR3B findByClientidAndRetPeriod(final String clientid, String retPeriod);
	
	@Transactional
	@Modifying
	void deleteByClientid(final String clientid);
	
	@Transactional
	@Modifying
	void deleteByClientidAndRetPeriod(final String clientid, String retPeriod);
	
	
	List<GSTR3B> findByClientidAndRetPeriodIn(final String clientid, List<String> retPeriod);
	
	List<GSTR3B> findByClientidInAndRetPeriod(List<String> clientid, String retPeriod);
}
