/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.mastergst.usermanagement.runtime.domain.GroupDetails;

/**
 * Repository interface for Customers to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface GroupDetailsRepository extends MongoRepository<GroupDetails, String> {

	//List<GroupDetails> findByUserid(final String userid);
	//GroupDetails findByClientid(final String clientid);
	List<GroupDetails> findByClientid(final String clientid);
	GroupDetails findByGroupname(final String groupname);
	GroupDetails findById(final String id);
	List<GroupDetails> findByClientidAndHeadname(String clientid, String headname);
	List<GroupDetails> findByClientidAndHeadnameIn(String clientid, List<String> headTypes);
	public GroupDetails findByClientidAndGroupnameIgnoreCase(String clientid, String groupname);
	public GroupDetails findByClientidAndSubgroups_GroupnameIgnoreCase(String clientid, String groupname);
}
