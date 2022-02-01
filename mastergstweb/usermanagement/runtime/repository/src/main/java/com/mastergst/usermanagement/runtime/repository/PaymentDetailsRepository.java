/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.PaymentDetails;
import com.mastergst.usermanagement.runtime.domain.Payments;

/**
 * Repository interface for Payment Details to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface PaymentDetailsRepository extends MongoRepository<PaymentDetails, String> {

	List<PaymentDetails> findByUserid(final String userid);
	List<PaymentDetails> findByUseridAndApiType(final String userid,final String apiType);
	
	List<PaymentDetails> findByUseridAndApiTypeAndSubscriptionStartDateInAndSubscriptionExpiryDateIn(final String userid,final String apiType,final List<String> subscriptionstartdate,final List<String> subscriptionenddate);

	List<PaymentDetails> findByUseridIn(final List<String> userids);
	List<PaymentDetails> findByUseridInAndApiType(List<String> userids, String apitype);
	public Payments findById(String docid);
}
