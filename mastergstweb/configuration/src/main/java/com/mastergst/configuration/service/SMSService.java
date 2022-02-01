/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import java.util.List;

import com.mastergst.core.exception.MasterGSTException;

/**
 * Service interface for SMS to perform CRUD operation.
 * 
 * @author Ashok Samrat
 * @version 1.0
 */
public interface SMSService {

	void createSMSConfig();

	String sendSMS(List<String> destinationNos, String message, boolean isRemainder,boolean isPartner) throws MasterGSTException;
	
	public String sendSMS(final List<String> destinationNos, String message, boolean includeTemplate, boolean isRemainder,boolean isPartner) throws MasterGSTException;

}
