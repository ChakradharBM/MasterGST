/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.login.runtime.repository;


import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.mastergst.login.runtime.domain.User;

/**
 * Repository interface for User to perform CRUD operation.
 * 
 * @author Ashok Samrat
 * @version 1.0
 */

public interface UserRepository extends PagingAndSortingRepository<User, String> {

	
	List<User> findByFullname(String fullname);
	
	User findByEmail(String email);
	
	@Transactional
	@Modifying
	void deleteByEmail(String email);
	
	User findById(String id);
	
	User findByEmailIgnoreCase(String email);
	 
	User findByEmailAndPassword(String email, String password);
	 
	User findByMobilenumber(String mobilenumber);
	
	List<User> findByParentid(String parentid);

	List<User> findByType(String type);
	
	Page<User> findByType(String type, Pageable pagable);
	
	Page<User> findByIdInAndDisableAndNeedToFollowUpIn(Set<String> userIdsLst, String flag,List<String> acctype, Pageable pagable);

	Page<User> findByIdInAndDisableAndNeedToFollowUpAndTypeIn(List<String> paidUserIds, String flag, String needToFollowUp,List<String> usertypes, Pageable pagable);

	Page<User> findByIdInAndDisable(List<String> paidUserIds, String flag, Pageable pagable);
	
	List<User> findByCreatedDateBetween(Date startdate,Date enddate);
	
	List<User> findByTypeAndCreatedDateBetween(String usertype,Date startdate,Date enddate);
	
	List<User> findByCategoryAndCreatedDateBetween(String usertype,Date startdate,Date enddate);
		List<User> findByIdIn(List<String> partnerclientids);
}
