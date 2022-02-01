/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.login.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
 * Handles and retrieves the login page depending on the URI template.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping
public class LoginController {
	private static final Logger logger = LogManager.getLogger(LoginController.class.getName());
	private static final String CLASSNAME = "LoginController::";

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

	@RequestMapping(value = {"/login","/"}, method = RequestMethod.GET)
	public String index(ModelMap model) {
		model.addAttribute("headerflag", "login");
		return "login/login";
	}

	@RequestMapping(value = "/reset", method = RequestMethod.GET)
	public String reset() {
		return "login/reset";
	}

	@RequestMapping(value = "/resetpwd", method = RequestMethod.POST)
	public String resetpassword(@RequestParam("email") String emailormobile, ModelMap model) throws Exception {
		final String method = "resetpassword::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = null;
		try {
			if (NullUtil.isNotEmpty(emailormobile) && emailormobile.contains("@")) {
				user = userService.findByEmailIgnoreCase(emailormobile);
				if (NullUtil.isEmpty(user)) {
					model.addAttribute("error", "Entered Email Id does not exist");
					logger.debug(CLASSNAME + method + END);
					return "login/reset";
				}
			} else {
				user = userService.findByMobilenumber(emailormobile);
				if (NullUtil.isEmpty(user)) {
					model.addAttribute("error", "Entered Mobile Number does not exist");
					logger.debug(CLASSNAME + method + END);
					return "login/reset";
				}
			}
			if (NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getMobilenumber())) {
				otpService.deleteByUserid(user.getId().toString());
				OTP otp = new OTP();
				otp.setOtp(SMSUtil.OTP(4));
				otp.setUserid(user.getId().toString());
				otp.setStatus("init");
				otpService.createOTP(otp);
				final User localUser = user;
				masterGstExecutorService.execute(()->{
					List<String> destinationNos = Lists.newArrayList();
					destinationNos.add(localUser.getMobilenumber());
					String sms = null;
					boolean isOtpFail = false;
					try{
						sms = smsService.sendSMS(destinationNos, otp.getOtp(), false,false);
					} catch (Exception e1) {
						logger.error(CLASSNAME + method + "Email ERROR {}", e1);
					}
					logger.debug(CLASSNAME + method + "SMS\t" + sms);
					if (NullUtil.isNotEmpty(sms)) {
						if (sms.contains("OK:")) {
							otp.setStatus("success");
							otpService.createOTP(otp);
							logger.debug(CLASSNAME + method + END);
						} else {
							isOtpFail = true;
						}
					} else {
						isOtpFail = true;
					}
					try{
						emailService.sendEnrollEmail(localUser.getEmail(),
								VmUtil.velocityTemplate("otp.vm", localUser.getFullname(), otp.getOtp(), null),
								MasterGSTConstants.RESETPWD_EMAILSUBJECT);
						if(isOtpFail){
							otp.setStatus("success");
							otpService.createOTP(otp);
							isOtpFail = false;
						}
					} catch(MasterGSTException e) {
						isOtpFail = true;
						logger.error(CLASSNAME + method + "Email ERROR {}", e);
					}
					if(isOtpFail){
						otp.setStatus("error");
						otpService.createOTP(otp);
					}
				});
				model.addAttribute("id", user.getId());
				model.addAttribute("source", "login/reset_password");
				return "otp/otp";
			} else {
				model.addAttribute("error", "Entered Data user is not exist");
				logger.debug(CLASSNAME + method + END);
				return "login/reset";
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			model.addAttribute("error", e.getMessage());
			logger.debug(CLASSNAME + method + END);
			return "login/reset";
		}
	}

	@RequestMapping(value = "/resetsubmit", method = RequestMethod.POST)
	public String resetsubmit(@RequestParam("hiddenid") String hiddenid, @RequestParam String password,
			@RequestParam String confirmpassword, ModelMap model) throws Exception {
		final String method = "resetsubmit::";
		logger.debug(CLASSNAME + method + BEGIN);
		try {
			User user = userService.getUser(hiddenid);
			if (NullUtil.isEmpty(user)) {
				model.addAttribute("id", hiddenid);
				model.addAttribute("error", "User Data not exists");
				logger.debug(CLASSNAME + method + END);
				return "login/reset_password";
			} else if(!password.equals(confirmpassword)) {
				model.addAttribute("id", hiddenid);
				model.addAttribute("error", "Password doesn't match. Please enter the correct values");
				logger.debug(CLASSNAME + method + END);
				return "login/reset_password";
			} else {
				user.setPassword(Base64.getEncoder()
						.encodeToString(password.getBytes(MasterGSTConstants.PASSWORD_ENCODE_FORMAT)));
				User createdUser = userService.createUser(user);
				
				emailService.sendEnrollEmail(createdUser.getEmail(),
						VmUtil.velocityTemplate("reset_pwd.vm", createdUser.getFullname(), createdUser.getEmail(),
								new String(Base64.getDecoder().decode(createdUser.getPassword()),
										MasterGSTConstants.PASSWORD_ENCODE_FORMAT)),
						MasterGSTConstants.RESETPWD_EMAILSUBJECT);
				logger.debug(CLASSNAME + method + END);
				return "login/reset_password_success";
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(CLASSNAME + method + e.getMessage());
			model.addAttribute("error", MasterGSTConstants.PASSWORD_ENCODE_EXCEPTION);
			logger.debug(CLASSNAME + method + END);
			return "login/reset_password";
		} catch (MasterGSTException e) {
			logger.error(CLASSNAME + method + e.getMessage());
			model.addAttribute("error", e.getMessage());
			logger.debug(CLASSNAME + method + END);
			return "login/reset_password";
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request) throws Exception {
		final String method = "logout::";
		logger.debug(CLASSNAME + method + BEGIN);
		HttpSession session = request.getSession(false);
		if (session != null) {
			if(isNotEmpty(session.getAttribute("pdf"))) {
				File file = (File) session.getAttribute("pdf");
				try {
					file.delete();
				} catch(Exception e) {}
				session.removeAttribute("pdf");
			}
			Enumeration<String> enumeration = session.getAttributeNames();
			while (enumeration.hasMoreElements()) {
				session.removeAttribute(enumeration.nextElement());
			}
			session.invalidate();
		}
		logger.debug(CLASSNAME + method + END);
		return "login/login";
	}
	
	@RequestMapping(value = "/terms", method = RequestMethod.GET)
	public String terms(ModelMap model) {
		return "compliance/terms";
	}
	
	@RequestMapping(value = "/privacy", method = RequestMethod.GET)
	public String privacy(ModelMap model) {
		return "compliance/privacy";
	}

}
