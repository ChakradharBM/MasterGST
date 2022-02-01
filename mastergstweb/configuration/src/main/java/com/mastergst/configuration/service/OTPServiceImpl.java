/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Impl class for SMS to perform CRUD operation.
 * 
 * @author Ashok Samart
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class OTPServiceImpl implements OTPService {

	private static final Logger logger = LogManager.getLogger(OTPServiceImpl.class.getName());
	private static final String CLASSNAME = "OTPServiceImpl::";

	@Autowired
	private OTPRepository otpRepository;

	public void deleteAll() {
		try {
			otpRepository.deleteAll();
		} catch(Exception e) { }
	}

	@Override
	@Transactional
	public OTP createOTP(OTP otp) {
		return otpRepository.save(otp);
	}

	@Override
	@Transactional
	public OTP findByUserid(String userid) {
		return otpRepository.findByUserid(userid);
	}

	@Override
	@Transactional
	public void deleteByUserid(String userid) {
		try {
			otpRepository.deleteByUserid(userid);
		} catch(Exception e) { }
	}
}
