/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.login.runtime.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.login.runtime.domain.HeaderKeys;

/**
 * Repository interface for Header Keys to perform CRUD operation.
 * 
 * @author Ashok Samrat
 * @version 1.0
 */
public interface HeaderKeysRepository extends MongoRepository<HeaderKeys, String> {

	HeaderKeys findByUseridAndUserclientidAndUserclientsecret(String userid, String userclientid,
			String userclientsecret);

	HeaderKeys findByUseridAndGstusernameAndStatecdAndIpusr(final String userid, final String gstusername,
			final String statecd, final String ipusr);
	
	HeaderKeys findByGstusernameAndStatecd(final String gstusername, final String statecd);
	
	HeaderKeys findByGstusernameAndStatecdAndIpusr(final String gstusername, final String statecd, final String ipusr);
	
	HeaderKeys findByGstusernameAndStatecdAndEmailAndIpusr(final String gstusername, final String statecd, final String email, final String ipusr);
	
	HeaderKeys findByGstusernameAndStatecdAndEmail(final String gstusername, final String statecd, final String email);
	
	List<HeaderKeys> findByUpdatedDateBetween(Date d1, Date d2);
	
	List<HeaderKeys> findByUpdatedDateBetweenAndAuthtokenNotNull(Date d1, Date d2);
	
	List<HeaderKeys> findByEmailAndUpdatedDateBetween(final String email, Date d1, Date d2);
	
	List<HeaderKeys> findByEmailAndUpdatedDateBetweenAndAuthtokenNotNull(final String email, Date d1, Date d2);
	
	List<HeaderKeys> findByEmailAndUpdatedDateBetweenAndUserclientidAndUserclientsecretAndAuthtokenNotNull(final String email, Date d1, Date d2,final String userclientid,final String userclientsecret);
	
	List<HeaderKeys> findByEmailAndUpdatedDateBetweenAndUserclientidAndUserclientsecretAndAuthtokenNotNullAndRefreshtokenerorNull(final String email, Date d1, Date d2,final String userclientid,final String userclientsecret);
	
	HeaderKeys findByGstusername(final String gstusername);
	
	HeaderKeys findByGstusernameAndEmail(final String gstusername,final String email);
	
	public List<HeaderKeys> findByUserid(final String userid);

	HeaderKeys findByUsername(String clientGstName);
}
