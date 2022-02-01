/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.ClientStatus;

/**
 * Repository interface for Client Status to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface ClientStatusRepository extends MongoRepository<ClientStatus, String> {

	List<ClientStatus> findByClientIdAndReturnPeriod(final String clientId, final String returnPeriod);
	List<ClientStatus> findByClientIdAndReturnPeriodIn(final String clientId, final List<String> returnPeriods);
	ClientStatus findByClientIdAndReturnTypeAndReturnPeriod(final String clientId, final String returnType, final String returnPeriod);
	List<ClientStatus> findByClientIdAndReturnPeriodAndReturnType(final String clientId, final String returnPeriod,final String returnType);
	List<ClientStatus> findByReturnTypeAndReturnPeriodAndClientIdIn(final String returnType, final String returnPeriod, final List<String> clientIds);
	List<ClientStatus> findByReturnPeriodAndClientIdIn(final String returnPeriod, final List<String> clientIds);
	List<ClientStatus> findByClientIdAndReturnTypeAndReturnPeriodIn(final String clientId, final String returnType,final List<String> returnPeriods);
	List<ClientStatus> findByClientIdAndReturnTypeInAndReturnPeriodIn(String clientid, List<String> returnArr,
			List<String> rtArray);
}
