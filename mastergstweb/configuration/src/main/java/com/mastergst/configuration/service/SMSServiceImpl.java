/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.NullUtil;

/**
 * Service Impl class for SMS to perform CRUD operation.
 * 
 * @author Ashok Samart
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class SMSServiceImpl implements SMSService {

	private static final Logger logger = LogManager.getLogger(SMSServiceImpl.class.getName());
	private static final String CLASSNAME = "SMSServiceImpl::";

	@Autowired
	private SMSRepository smsRepository;

	@Value("${sms.gateway.url}")
	private String url;
	@Value("${sms.gateway.senderid}")
	private String senderid;
	@Value("${sms.gateway.username}")
	private String username;
	@Value("${sms.gateway.password}")
	private String password;
	@Value("${sms.gateway.template}")
	private String template;
	@Value("${sms.gateway.remindertemplate}")
	private String reminderTemplate;

	/**
	 * Used to Create the SMS Configuration
	 * 
	 * @return {@link SMSConfig}
	 */
	public void createSMSConfig() {
		SMSConfig smsConfig = new SMSConfig();
		smsConfig.setUsername(username);
		smsConfig.setPassword(password);
		smsConfig.setUrl(url);
		smsConfig.setSenderid(senderid);
		smsConfig.setTemplate(template);
		if (NullUtil.isNotEmpty(smsRepository.findAll())) {
			smsRepository.deleteAll();
		}
		smsRepository.save(smsConfig);
	}
	

	/**
	 * Sends the SMS to destination numbers
	 * 
	 * @param destinationNos
	 *            List of destination numbers
	 * @param message
	 *            SMS Message as a String
	 * @return String as a result
	 * @throws MasterGSTException
	 */
	public String sendSMS(final List<String> destinationNos, String message, boolean isRemainder,boolean isPartner) throws MasterGSTException {
		final String method = "sendSMS(final List<String> destinationNos,final String message)::";
		logger.debug(CLASSNAME + method + BEGIN);
		return sendSMS(destinationNos, message, true, isRemainder,isPartner);
	}
	
	/**
	 * Sends the SMS to destination numbers
	 * 
	 * @param destinationNos
	 *            List of destination numbers
	 * @param message
	 *            SMS Message as a String
	 * @param includeTemplate
	 *            flag to attach template to SMS content
	 * @return String as a result
	 * @throws MasterGSTException
	 */
	public String sendSMS(final List<String> destinationNos, String message, boolean includeTemplate, boolean isRemainder,boolean isPartner) throws MasterGSTException {
		final String method = "sendSMS(final List<String> destinationNos,final String message)::";
		logger.debug(CLASSNAME + method + BEGIN);
		String smsStatus = "";
		try {
			List<SMSConfig> lSMSConfig = smsRepository.findAll();
			if (NullUtil.isEmpty(lSMSConfig)) {
				for (String destinationNo : destinationNos) {
					if(includeTemplate) {
						if(isRemainder) {
							smsStatus = sendSMS(prepareRequestString(destinationNo, reminderTemplate +" "+ message +" - MasterGST", username, password), url);
						} else if(isPartner){
							smsStatus = sendSMS(prepareRequestString(destinationNo, message +" - MasterGST", username, password), url);
						} else {
							smsStatus = sendSMS(prepareRequestString(destinationNo, template +" "+ message +" - MasterGST", username, password), url);
						}
					} else {
						smsStatus = sendSMS(prepareRequestString(destinationNo, message +" - MasterGST", username, password),
								url);
					}
				}
			} else {
				SMSConfig smsConfig = lSMSConfig.get(0);
				for (String destinationNo : destinationNos) {
					if(includeTemplate) {
						if(isRemainder) {
							smsStatus = sendSMS(prepareRequestString(destinationNo, reminderTemplate +" "+ message +" - MasterGST",
									smsConfig.getUsername(), smsConfig.getPassword()), smsConfig.getUrl());
						}else if(isPartner){
							smsStatus = sendSMS(prepareRequestString(destinationNo, message +" - MasterGST",
									smsConfig.getUsername(), smsConfig.getPassword()), smsConfig.getUrl());
						} else {
							smsStatus = sendSMS(prepareRequestString(destinationNo, smsConfig.getTemplate() +" "+ message +" - MasterGST",
									smsConfig.getUsername(), smsConfig.getPassword()), smsConfig.getUrl());
						}
					} else {
						smsStatus = sendSMS(prepareRequestString(destinationNo, message +" - MasterGST",
								smsConfig.getUsername(), smsConfig.getPassword()), smsConfig.getUrl());
					}
				}
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException("SMS Exception");
		}
		logger.debug(CLASSNAME + method + END);
		return smsStatus;
	}

	/**
	 * Build up Query String for sending SMS
	 * 
	 * @param destinationNo
	 *            destination number
	 * @param message
	 *            SMS Message as a String
	 * @param smsgatewayUserName
	 *            SMS Gateway UserName as a String
	 * @param smsgatewayPassword
	 *            SMS Gateway Password as a String
	 * @param smsgatewayURL
	 *            SMS Gateway URL as a String
	 * @return String as a Request XML
	 * @throws Exception
	 */
	private String prepareRequestString(final String destinationNo, final String message,
			final String smsgatewayUserName, final String smsgatewayPassword) throws Exception {
		return "User=" + URLEncoder.encode(smsgatewayUserName, "UTF-8") + "&passwd=" + smsgatewayPassword
				+ "&mobilenumber=" + destinationNo + "&message=" + URLEncoder.encode(message, "UTF-8")
				+ "&sid=mstgst&mtype=N&DR=Y";
	}

	/**
	 * Send your XML in an HTTP POST to the SMS provider XML gateway
	 * 
	 * @param requestString
	 *            XML as a requestString
	 * @param String
	 *            as a result
	 * @throws Exception
	 */
	private String sendSMS(final String requestString, final String smsgatewayURL) throws Exception {
		String response = "";
		URL url = new URL(smsgatewayURL);
		HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
		urlconnection.setRequestMethod("POST");
		urlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		urlconnection.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
		out.write(requestString);
		out.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
		String decodedString;
		while ((decodedString = in.readLine()) != null) {
			response += decodedString;
		}
		in.close();

		return response;
	}

}
