/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.login.runtime.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.mastergst.login.runtime.domain.HeaderKeys;
import com.mastergst.login.runtime.domain.User;


/**
 * Service interface for User to perform CRUD operation.
 * 
 * @author Ashok Samrat
 * @version 1.0
 */
public interface UserService {

	User createUser(User user);

	User getUser(String id);
	User findByUsrId(String id);
	User updateUser(User user);
	
	void saveUsers(List<User> users);

	void deleteUser(String email);

	List<User> getAllUsers();
	
	List<User> getAllUsersData(final String searchQuery);
	
	List<User> getAllUsersData(final String type,final String searchQuery);
	
	Page<User> getAllUsers(int start, int length,String fieldName, String order, String searchVal);
	
	Page<User> getAllUsers(String type, int start, int length,String fieldName, String order, String searchVal);
	

	User isExistUser(String email, String password);

	User findByEmail(String email);
	
	User findByEmailIgnoreCase(String email);

	User findByMobilenumber(String mobilenumber);

	User findById(String id);
	
	User getUserById(String id);
	
	List<User> getASPUserDetailsByType(String type);
	
	List<User> findByParentId(String parentId);

	boolean collectionExists(final String collectionName);
	
	
	List<User> getByAllUsersYearly(int year);
	List<User> getByAllUsersgetByAllUsersMonthly(int month, int year);
	public void deleteOtpunverifiedusers(String otpunverifiedusersid);

	public List<User> partnerClientLinkedUsers(String searchQuery);
	
	List<HeaderKeys> deleteHeaderkeys(String headerid, String userid);
	
	public List<HeaderKeys> getHeaderkeys(String userid);
	
	public void deleteAspDeveloperHeaderkeys(String headerid);

	//public List<User> getAllusers();

	List<User> getAllusersInYearly(int year);

	List<User> getAllusersInMonthly(int month, int year);
	
	public List<User> getAllUsersBasedOnId(List<String> partnerclientids);

	void saveRefisUsers(List<User> users);
	
	//public Page<User> findByPartnerUsers(int month, String yearCode, int start, int length, String searchVal);

	//public Page<User> findByPartnerUsers(Date stDate, Date endDate, int start, int length, String searchVal);
}
