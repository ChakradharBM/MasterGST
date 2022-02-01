/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR3BOffsetLiability;

/**
 * Repository interface for GSTR3B Offset Liability to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface GSTR3BOffsetLiabilityRepository extends MongoRepository<GSTR3BOffsetLiability, String> {
	
}
