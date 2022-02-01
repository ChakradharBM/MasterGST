/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;

import javax.transaction.Transactional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;

/**
 * Repository interface for GST Public Search to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface GSTINPublicDataRepository extends MongoRepository<GSTINPublicData, String> {
	
	GSTINPublicData findByGstinAndCreatedDateBetween(String gstin,Date d1, Date d2);
	
	GSTINPublicData findByGstin(String gstin);
	
	Page<GSTINPublicData> findByGstin(String gstin, Pageable pageable);
	List<GSTINPublicData> findByUserid(String userid);
	
	@Transactional
	@Modifying
	void deleteByGstin(String gstin);
	
	@Transactional
	@Modifying
	void deleteByGstinAndUseridIsNull(String gstin);
	
	GSTINPublicData findByUseridAndGstin(final String userid , final String gstin);
	
	List<GSTINPublicData> findByUseridAndParentidAndGstin(final String userid,final String parentid, final String gstin);

}
