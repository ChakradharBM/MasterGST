/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.PaymentRequestDetails;

/**
 * Repository interface for Payment Request Details to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface PaymentRequestDetailsRepository extends MongoRepository<PaymentRequestDetails, String> {

	List<PaymentRequestDetails> findByUserid(final String userid);
}
