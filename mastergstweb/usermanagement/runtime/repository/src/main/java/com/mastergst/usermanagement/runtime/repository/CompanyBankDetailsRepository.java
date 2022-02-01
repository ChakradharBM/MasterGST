/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.CompanyBankDetails;

/**
 * Repository interface for Products to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface CompanyBankDetailsRepository extends MongoRepository<CompanyBankDetails, String> {

	List<CompanyBankDetails> findByUseridAndClientid(final String userid,final String clientid);
	List<CompanyBankDetails> findByClientidAndUseridNotNull(final String clientid);
	CompanyBankDetails findByClientid(final String clientid);
	CompanyBankDetails findByUserid(final String userid);
	CompanyBankDetails findByClientidAndAccountnumber(final String clientid,final String accountNumber);
}
