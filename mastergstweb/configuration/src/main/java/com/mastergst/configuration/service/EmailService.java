/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import java.io.File;
import java.util.List;

import org.springframework.http.HttpEntity;

import com.mastergst.core.exception.MasterGSTException;

/**
 * Service interface for Email to perform CRUD operation.
 * 
 * @author Ashok Samrat
 * @version 1.0
 */
public interface EmailService {

	void createEmailConfig();
	
	void sendEnrollEmail(final String to,final String templatebody,final String subject) throws MasterGSTException;
	void sendBulkImportEmail(final String to, final String cc, final String templatebody,final String subject,File file) throws MasterGSTException;

	void sendEnrollEmail(String first, String velocityTemplate, String first2, List<String> ccmails) throws MasterGSTException;

	void sendAttachmentEmail(String customerMail, List<String> ccmail, String velocityTemplate, String string, File file) throws MasterGSTException;
	void sendCompaignEmail(String to, String templatebody, String subjecttxt) throws MasterGSTException;

}
