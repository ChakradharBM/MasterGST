/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository interface for Subscription Plana to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
public interface SubscriptionPlanRepository extends MongoRepository<SubscriptionPlan, String> {
	
	List<SubscriptionPlan> findByCategory(String category);
	
	SubscriptionPlan findByNameAndCategory(String name, String category);
}
