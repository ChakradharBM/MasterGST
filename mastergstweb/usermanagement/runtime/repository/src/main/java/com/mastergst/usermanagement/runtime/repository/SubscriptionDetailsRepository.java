/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;

/**
 * Repository interface for Subscription Details to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface SubscriptionDetailsRepository extends MongoRepository<SubscriptionDetails, String> {

	SubscriptionDetails findByUserid(final String userid);
	
	SubscriptionDetails findById(String id);
	
	List<SubscriptionDetails> findByUseridAndApiTypeIn(final String userid,final List<String> apiType);

	SubscriptionDetails findByUseridAndApiType(final String userid, final String apiType);
	SubscriptionDetails findByUseridAndSubscriptionType(final String userid,final String subscriptionType);
	
	List<SubscriptionDetails> findByCreatedDateBetween(Date startDate, Date endDate);
	
	List<SubscriptionDetails> findByUserid(List<String> userids);

	//List<SubscriptionDetails> findByUserPaymentType(String paidUser);

	List<SubscriptionDetails> findByApiTypeIn(List<String> apislist);
	
	List<SubscriptionDetails> findByUseridIn(List<String> apislist);

	List<SubscriptionDetails> findByUseridInAndApiType(List<String> userids, String apitype);
	
	List<SubscriptionDetails> findByUseridAndMthCdAndYrCd(final String userid,final String mthCd,final String yrCd);
}
