/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.login.runtime.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mastergst.configuration.service.EmailService;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.NullUtil;
import com.mastergst.core.util.VmUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;

/**
 * This class provides the all Authentication API ROA services
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("site")
public class SiteController {

	private static final Logger logger = LogManager.getLogger(SiteController.class.getName());
	private static final String CLASSNAME = "SiteController::";

	@Autowired
	UserService userService;

	@Autowired
	EmailService emailService;

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/signupdemo", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String signupdemo(@RequestBody User user) throws Exception {
		logger.info(CLASSNAME + "signupdemo:: Begin");
		logger.info(CLASSNAME + "signupdemo:: user.getEmail()\t"+user.getEmail());
		boolean isExist=userService.collectionExists("users");
		logger.info(CLASSNAME + "signupdemo:: isExist\t"+isExist);
		User usermail= null;
		if(isExist){
			usermail = userService.findByEmail(user.getEmail().toLowerCase());
		}
		if (NullUtil.isEmpty(usermail)) {
			
			user.setType(MasterGSTConstants.DEMO_USER);
			user.setEmail(user.getEmail().toLowerCase());
			userService.createUser(user);
			try {
				emailService.sendEnrollEmail(user.getEmail().toLowerCase(), VmUtil.velocityTemplate("email_site_signupdemo.vm"),
						MasterGSTConstants.SIGNUPDEMO_EMAILSUBJECT);
				logger.info(CLASSNAME + "signupdemo:: End");
			} catch (MasterGSTException e) {
				logger.error(CLASSNAME + "signupdemo" + e.getMessage());
				return MasterGSTConstants.EMAIL_ERROR;
			}
			return MasterGSTConstants.SUCCESS;
		} else {
			return MasterGSTConstants.EMAIL_EXIST;
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/siteenroll", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String siteenroll(@RequestBody User user) throws Exception {
		logger.info(CLASSNAME + "siteenroll:: Begin");
		logger.info(CLASSNAME + "siteenroll:: user.getEmail()\t"+user.getEmail());
		boolean isExist=userService.collectionExists("users");
		logger.info(CLASSNAME + "siteenroll:: isExist\t"+isExist);
		User usermail= null;
		if(isExist){
			usermail = userService.findByEmail(user.getEmail().toLowerCase());
		}
		if (NullUtil.isEmpty(usermail)) {
			logger.info(CLASSNAME + "isEmpty(usermail)");
			user.setEmail(user.getEmail().toLowerCase());
			user.setType(MasterGSTConstants.SITE_ENROLL_USER);
			userService.createUser(user);
			try {
				emailService.sendEnrollEmail(user.getEmail().toLowerCase(), VmUtil.velocityTemplate("email_site_enroll.vm"),
						MasterGSTConstants.SITEENROLL_EMAILSUBJECT);
				logger.info(CLASSNAME + "siteenroll:: End");
			} catch (MasterGSTException e) {
				logger.error(CLASSNAME + "siteenroll" + e.getMessage());
				return MasterGSTConstants.EMAIL_ERROR;
			}
			return MasterGSTConstants.SUCCESS;
		} else {
			return MasterGSTConstants.EMAIL_EXIST;
		}
	}

}
