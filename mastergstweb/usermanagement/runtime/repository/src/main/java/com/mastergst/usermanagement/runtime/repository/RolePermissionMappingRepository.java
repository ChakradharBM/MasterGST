/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.RolePermissionMapping;

/**
 * Repository interface for Role Permission Mapping to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface RolePermissionMappingRepository extends MongoRepository<RolePermissionMapping, String>{

	void deleteByClientidAndRole(final String clientid, final String role);
	List<RolePermissionMapping> findByClientidAndRoleidIsNull(final String clientid);
	List<RolePermissionMapping> findByClientidAndRoleidAndUseridIsNull(final String clientid, final String roleid);
	List<RolePermissionMapping> findByRoleidAndClientidIsNull(final String roleid);
	RolePermissionMapping findByClientidAndUseridAndRoleIsNull(final String clientid, String userid);
}
