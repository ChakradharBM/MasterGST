/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR1DocumentIssue;

/**
 * Repository interface for Doc Issue to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface GSTR1DocumentIssueRepository extends MongoRepository<GSTR1DocumentIssue, String> {

	List<GSTR1DocumentIssue> findByClientid(final String clientid);
	GSTR1DocumentIssue findByClientidAndReturnPeriod(final String clientid, final String returnPeriod);
}
