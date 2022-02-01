/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.Metering;

/**
 * Repository interface for Header Keys to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface MeteringRepository extends MongoRepository<Metering, String> {
	
	List<Metering> findByUserid(String userid);
	
	List<Metering> findByUseridAndCreatedDateBetween(String userid, Date d1, Date d2);
	
	List<Metering> findByUseridAndStageInAndCreatedDateBetween(String userid, final List<String> stage, Date d1, Date d2);
	
	List<Metering> findByUseridAndTypeInAndCreatedDateBetween(String userid, final List<String> types, Date d1, Date d2);
	
	List<Metering> findByUseridAndCreatedDateBetweenAndTypeNotIn(String userid, Date d1, Date d2, final List<String> types);
	
	List<Metering> findByStageAndCreatedDateBetween(String stage, Date d1, Date d2);
	
	List<Metering> findByGstnusernameAndCreatedDateBetween(String gstnusername, Date d1, Date d2);
	
	List<Metering> findByCreatedDateBetween(Date startDate, Date endDate);
	
	List<Metering> findByUseridAndStage(String userid, String stage);
	
	List<Metering> findByUseridAndStageInAndCreatedDateBetween(String userid, String stage, Date d1, Date d2);
	
	Long countByEmailAndGstnusernameAndServicenameAndCreatedDateBetween(String email, String gstnusername, String servicename, Date startDate, Date endDate);

	public List<Metering>  findByUseridAndTypeAndStageAndCreatedDateBetween(String userid, String type, String stage, Date startDate, Date endDate);
}
