/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.PartnerClient;

/**
 * Repository interface for Partner's Client to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface PartnerClientRepository extends MongoRepository<PartnerClient, String> {
	
	List<PartnerClient> findByUserid(final String userid);
	List<PartnerClient> findByUseridAndClientidIsNotNull(final String userid);
	List<PartnerClient> findByUseridAndSubscriptionTypeIsNotNull(final String userid);
	PartnerClient findByEmail(final String email);
	List<PartnerClient> findByClientidIsNotNullAndSubscriptionAmountIsNull();
	
	List<PartnerClient> findByClientidIn(Set<String> keySet);
	public PartnerClient findByClientid(String clientid);
}
