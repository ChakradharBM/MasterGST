/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository interface for Message  to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
public interface LatestUpdatesRepository extends PagingAndSortingRepository<LatestUpdates, String> {
	
	@Query("{'usertype': { $all: [?0] }}")
	List<LatestUpdates> findByUserType(String type);
	
	@Query("{'usertype': { $all: [?0] }}")
	List<LatestUpdates> findByUserType(String type, Sort sort);
	
}
