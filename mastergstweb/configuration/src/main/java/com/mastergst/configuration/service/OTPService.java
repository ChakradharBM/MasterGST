/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;


/**
 * Service interface for OTP to perform CRUD operation.
 * 
 * @author Ashok Samrat
 * @version 1.0
 */
public interface OTPService {
	
	void deleteAll();

	 OTP createOTP(OTP otp);

     OTP findByUserid(String userid);
   
     void deleteByUserid(String userid);
}

