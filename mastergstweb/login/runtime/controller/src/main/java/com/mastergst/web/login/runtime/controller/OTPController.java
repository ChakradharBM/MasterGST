/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.login.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.mastergst.configuration.service.EmailService;
import com.mastergst.configuration.service.OTP;
import com.mastergst.configuration.service.OTPService;
import com.mastergst.configuration.service.SMSService;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.MasterGstExecutorService;
import com.mastergst.core.util.NullUtil;
import com.mastergst.core.util.SMSUtil;
import com.mastergst.core.util.VmUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;

/**
 * Handles and retrieves the OTP page depending on the URI template.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping
public class OTPController {
	private static final Logger logger = LogManager.getLogger(OTPController.class.getName());
	private static final String CLASSNAME = "OTPController::";
	@Autowired
	UserService userService;
	@Autowired
	SMSService smsService;
	@Autowired
	EmailService emailService;
	@Autowired
	OTPService otpService;
	
	@Autowired
	MasterGstExecutorService masterGstExecutorService;

	@RequestMapping(value = "/otptryagian", method = RequestMethod.POST)
	public @ResponseBody String otptryagian(@RequestParam String userid, ModelMap model) throws Exception {
		final String method = "otptryagian::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "userid\t" + userid);
		User user = userService.findById(userid);
		if (NullUtil.isNotEmpty(userid)) {
			OTP otp = new OTP();
			otp.setOtp(SMSUtil.OTP(4));
			otp.setUserid(user.getId().toString());
			otpService.deleteByUserid(user.getId().toString());
			try {
				List<String> destinationNos = Lists.newArrayList();
				destinationNos.add(user.getMobilenumber());
				String sms = smsService.sendSMS(destinationNos, otp.getOtp(), false,false);
				logger.debug(CLASSNAME + method + "SMS\t" + sms);

				if (NullUtil.isNotEmpty(sms)) {
					if (sms.contains("OK:")) {
						otpService.createOTP(otp);
						model.addAttribute("id", user.getId());
						return "Success";
					} else {
						return sms;
					}
				} else {
					return sms;
				}
			} catch (MasterGSTException e) {
				logger.error(CLASSNAME + method + e.getMessage());
				return "SMS Exception";
			}
		} else {
			return MasterGSTConstants.USER_NOT_EXIST;
		}
	}
	
	@RequestMapping(value = "/otpstatus", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> otpstatus(@RequestParam("userId") String userId) throws Exception {
		OTP otp = otpService.findByUserid(userId);
		Map<String, String> result = new HashMap<>();
		String status = otp == null ? "error": otp.getStatus();
		result.put("status", status);
		return result;
	}

	@RequestMapping(value = "/otpsubmit", method = RequestMethod.POST)
	public String otpsubmit(@ModelAttribute("otp") OTP otp, @RequestParam String hiddenid, 
			@RequestParam String source, @RequestParam String subscrid, ModelMap model) throws Exception {
		final String method = "otpsubmit::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "source\t" + source);
		OTP dbOTP = otpService.findByUserid(hiddenid);
		logger.debug(CLASSNAME + method + "dbOTP\t" + dbOTP);
		
		if (NullUtil.isNotEmpty(dbOTP) && NullUtil.isNotEmpty(dbOTP.getOtp())) {
			
			String enterOtp = otp.getOtp1() + otp.getOtp2() + otp.getOtp3() + otp.getOtp4();
			logger.debug(CLASSNAME + method + "Enter OTP\t" + enterOtp);
			if (enterOtp.equalsIgnoreCase(dbOTP.getOtp().trim())) {
				model.addAttribute("id", hiddenid);
				if (NullUtil.isNotEmpty(otpService.findByUserid(hiddenid))) {
					otpService.deleteByUserid(hiddenid);
					if (!source.equalsIgnoreCase("login/reset_password")) {
						User user = userService.findById(hiddenid);
						user.setOtpVerified("true");
						userService.updateUser(user);
						masterGstExecutorService.execute(()->{
							try {
								String userguide = userGuide(user.getType());
								emailService.sendEnrollEmail(user.getEmail(),
										VmUtil.velocityTemplate("email_enrollment.vm", userguide, user.getFullname(),
												user.getEmail(),
												new String(Base64.getDecoder().decode(user.getPassword()),
														MasterGSTConstants.PASSWORD_ENCODE_FORMAT)),
										MasterGSTConstants.CREATEUSER_EMAILSUBJECT);

							} catch (MasterGSTException e) {
								logger.error(CLASSNAME + "signupdemo" + e.getMessage());
								model.addAttribute("error", MasterGSTConstants.EMAIL_ERROR);
								//return source;
							} catch (Exception e) {
								logger.error(CLASSNAME + "signupdemo" + e.getMessage());
								model.addAttribute("error", MasterGSTConstants.EMAIL_ERROR);
								//return source;
							}
							
						});
					}else if(source.equalsIgnoreCase("login/reset_password")){
						User user = userService.findById(hiddenid);
						user.setOtpVerified("true");
						userService.updateUser(user);
					}
				}
				//TODO: Redirect user to plan review page
				if(NullUtil.isNotEmpty(subscrid)) {
					
				}
				return source;
			} else {
				logger.debug(CLASSNAME + method + END);
				model.addAttribute("id", hiddenid);
				model.addAttribute("source", source);
				model.addAttribute("error", "Enter OTP is not matching");
				return "otp/otp";
			}
		} else {
			logger.debug(CLASSNAME + method + END);
			model.addAttribute("error", "Enter OTP is not matching");
			return "otp/otp";
		}
	}

	private String userGuide(String type) throws Exception {
		String userGuide = "";
		switch (type) {
		case MasterGSTConstants.PARTNER:
			userGuide = "<a href=http://www.mastergst.com/blog/user-guide-to-business-partners/>Click here User Guide for Business Partner:</a>";
			break;
		default:
			userGuide = "<a href=http://mastergst.com/blog/how-to-file-gst-returns-using-mastergst/>Click here for Product User guide:</a>";
			break;
		}
		return userGuide;

	}

}
