/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTROffsetLiability;

/**
 * Repository interface for GSTR Offset Liability to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface GSTROffsetLiabilityRepository extends MongoRepository<GSTROffsetLiability, String> {
	
	GSTROffsetLiability findByGstinAndRetPeriodAndRetType(final String gstin, final String retPeriod, final String retType);
}
