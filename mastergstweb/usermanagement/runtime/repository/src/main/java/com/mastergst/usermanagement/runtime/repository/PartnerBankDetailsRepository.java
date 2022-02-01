/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.PartnerBankDetails;

/**
 * Repository interface for Partner to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface PartnerBankDetailsRepository extends MongoRepository<PartnerBankDetails, String> {

	//List<PartnerBankDetails> findByUserid(final String userid);
	PartnerBankDetails findByUserid(final String userid);
}
