/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.login.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CompanyCenters;
import com.mastergst.usermanagement.runtime.domain.CompanyInvoices;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.domain.UserSequenceIdGenerator;
import com.mastergst.usermanagement.runtime.repository.CompanyUserRepository;
import com.mastergst.usermanagement.runtime.repository.UserSequenceIdGeneratorRepository;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.PartnerService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;

/**
 * Handles and retrieves the signup page depending on the URI template.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping
public class SignupController {

	private static final Logger logger = LogManager.getLogger(SignupController.class.getName());
	private static final String CLASSNAME = "SignupController::";

	@Autowired
	UserService userService;
	@Autowired
	SMSService smsService;
	@Autowired
	EmailService emailService;
	@Autowired
	OTPService otpService;
	@Autowired
	PartnerService partnerService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	ClientService clientService;
	@Autowired
	ProfileService profileService;
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MasterGstExecutorService masterGstExecutorService;
	
	@Autowired
	CompanyUserRepository companyUserRepository;
	
	@Autowired
	UserSequenceIdGeneratorRepository userSequenceIdGeneratorRepository;


	@RequestMapping(value = {"/signupall", "/invsignup"}, method = RequestMethod.GET)
	public String singupall(ModelMap model, @RequestParam String inviteId, 
			@RequestParam String subscrid) {
		model.addAttribute("headerflag", "signup");
		model.addAttribute("inviteId", inviteId);
		model.addAttribute("subscrid", subscrid);
		return "signup/signup_all";
	}
	
	@RequestMapping(value = "/getuser", method = RequestMethod.GET)
	public @ResponseBody User getUserProfile(ModelMap model, @RequestParam String userId) {
		 User usr = userRepository.findOne(userId);
		 if(NullUtil.isNotEmpty(usr) && NullUtil.isNotEmpty(usr.getLastLoggedIn())) {
			SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-YYY hh:mm:ss a");
			sdf.format(usr.getLastLoggedIn());
			usr.setUsrLastLoggedIn(sdf.format(usr.getLastLoggedIn()));
			usr.setLastLoggedIn(usr.getLastLoggedIn());
		}
		return usr;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public ModelAndView singup(ModelMap model, @RequestParam String type,  
			@RequestParam String inviteId, @RequestParam String subscrid) throws Exception {
		logger.debug(CLASSNAME + "singup : Begin");
		logger.info(CLASSNAME + "dashboard : type\t" + type);
		userBasedModel(type, model);
		model.addAttribute("inviteId", inviteId);
		model.addAttribute("subscrid", subscrid);
		return new ModelAndView("signup/signup", "user", new User());
	}
	
	@RequestMapping(value = "/updtuserdtls", method = RequestMethod.POST)
	public @ResponseBody User updateUserDetails(@RequestBody User user, @RequestParam String id, ModelMap model) throws Exception {
		final String method = "updateUserDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		User dbUser = userService.findById(id);
		User dbUserUpdate = null;
		if(NullUtil.isNotEmpty(dbUser)) {
			List<CompanyUser> cmpyUsers = profileService.getUsers(dbUser.getId().toString());
			if (isNotEmpty(dbUser.getEmail())) {
				CompanyUser cuser = profileService.getCompanyUser(dbUser.getEmail());
				if (isNotEmpty(cuser)) {
					cuser.setEmail(user.getEmail());
					companyUserRepository.save(cuser);
				}
			}
			if(NullUtil.isNotEmpty(user.getFullname())) {
				dbUser.setFullname(user.getFullname());
			}
			if(NullUtil.isNotEmpty(user.isAccessGstr9())) {
				dbUser.setAccessGstr9(user.isAccessGstr9());
			}
			if(NullUtil.isNotEmpty(user.isAccessDwnldewabillinv())) {
				dbUser.setAccessDwnldewabillinv(user.isAccessDwnldewabillinv());
			}
			if(NullUtil.isNotEmpty(user.isAccessMultiGSTNSearch())) {
				dbUser.setAccessMultiGSTNSearch(user.isAccessMultiGSTNSearch());
			}
			if(NullUtil.isNotEmpty(user.isAccessReminders())) {
				dbUser.setAccessReminders(user.isAccessReminders());
			}
			if(NullUtil.isNotEmpty(user.getMobilenumber())) {
				dbUser.setMobilenumber(user.getMobilenumber());
			}
			if(NullUtil.isNotEmpty(user.getEmail())) {
				dbUser.setEmail(user.getEmail());
			}
			if(NullUtil.isNotEmpty(user.getPartnerPercentage())) {
				dbUser.setPartnerPercentage(user.getPartnerPercentage());
			}
			if(NullUtil.isNotEmpty(user.getAgreementStatus())) {
				dbUser.setAgreementStatus(user.getAgreementStatus());
			}
			if(NullUtil.isNotEmpty(user.getNeedToFollowUp())) {
				dbUser.setNeedToFollowUp(user.getNeedToFollowUp());
			}
			if(NullUtil.isNotEmpty(user.getSandboxApplied())) {
				dbUser.setSandboxApplied(user.getSandboxApplied());
			}
			if(NullUtil.isNotEmpty(user.getNeedToFollowUpComment())) {
				dbUser.setNeedToFollowUpComment(user.getNeedToFollowUpComment());
			}else {
				dbUser.setNeedToFollowUpComment("");
			}
			
			if(NullUtil.isNotEmpty(user.getGstin())) {
				dbUser.setGstin(user.getGstin());
			}
			if(NullUtil.isNotEmpty(user.getPan())) {
				dbUser.setPan(user.getPan());
			}
			if(NullUtil.isNotEmpty(user.getAuthorisedSignatory())) {
				dbUser.setAuthorisedSignatory(user.getAuthorisedSignatory());
			}
			
			if(NullUtil.isNotEmpty(user.getAuthorisedPANNumber())) {
				dbUser.setAuthorisedPANNumber(user.getAuthorisedPANNumber());
			}
			if(NullUtil.isNotEmpty(user.getBusinessName())) {
				dbUser.setBusinessName(user.getBusinessName());
			}
			if(NullUtil.isNotEmpty(user.getDealerType())) {
				dbUser.setDealerType(user.getDealerType());
			}
			if(NullUtil.isNotEmpty(user.getStateName())) {
				dbUser.setStateName(user.getStateName());
			}			
			if(NullUtil.isNotEmpty(user.getQuotationSent())) {
				dbUser.setQuotationSent(user.getQuotationSent());
			}
			if(NullUtil.isNotEmpty(user.getNeedFollowupdate())) {
				dbUser.setNeedFollowupdate(user.getNeedFollowupdate());
			}
			if(NullUtil.isNotEmpty(user.getNeedFollowup())) {
				dbUser.setNeedFollowup(user.getNeedFollowup());
			}
			if(NullUtil.isNotEmpty(user.isAccessAcknowledgement())) {
				dbUser.setAccessAcknowledgement(user.isAccessAcknowledgement());
			}
			if(NullUtil.isNotEmpty(user.getPartnerType())) {
				dbUser.setPartnerType(user.getPartnerType());
			}
			if(NullUtil.isNotEmpty(user.getPartnerEmail())) {
				dbUser.setPartnerEmail(user.getPartnerEmail());
				User partnerUser = userService.findByEmail(user.getPartnerEmail());
				if(NullUtil.isNotEmpty(partnerUser)) {
					partnerService.updatePartnerDetailsFromAdmin(partnerUser.getId().toString(), dbUser);
					dbUser.setRefid(partnerUser.getUserSequenceid()+"");
				}
				
			}
			if(NullUtil.isNotEmpty(user.getType())) {
				
				if(!user.getType().equalsIgnoreCase(dbUser.getType())) {
					dbUser.setType(user.getType());
						List<SubscriptionDetails> subscriptiondata=subscriptionService.getSubscriptionsByuserid(dbUser.getId().toString());
						subscriptionService.subscriptiondataDeleteByUserid(subscriptiondata);
						Calendar cal = Calendar.getInstance();
						Date today = cal.getTime();
						if(user.getType().equalsIgnoreCase(MasterGSTConstants.ASPDEVELOPER)) {
							SubscriptionDetails gstSandboxSubscriptionDetails = new SubscriptionDetails();
							SubscriptionDetails ewaySandboxSubscriptionDetails = new SubscriptionDetails();
							gstSandboxSubscriptionDetails.setUserid(dbUser.getId().toString());
							gstSandboxSubscriptionDetails.setPaidAmount(10d);
							gstSandboxSubscriptionDetails.setRegisteredDate(today);
							cal.add(Calendar.DATE, 20);
							Date gstNext20Days = cal.getTime();
							gstSandboxSubscriptionDetails.setExpiryDate(gstNext20Days);
							gstSandboxSubscriptionDetails.setApiType(MasterGSTConstants.GSTSANDBOX_API);
							gstSandboxSubscriptionDetails.setAllowedClients(0);
							gstSandboxSubscriptionDetails.setAllowedCenters(0);
							gstSandboxSubscriptionDetails.setAllowedInvoices(0);
							subscriptionService.updateSubscriptionData(gstSandboxSubscriptionDetails);
							ewaySandboxSubscriptionDetails.setUserid(dbUser.getId().toString());
							ewaySandboxSubscriptionDetails.setPaidAmount(10d);
							ewaySandboxSubscriptionDetails.setRegisteredDate(today);
							ewaySandboxSubscriptionDetails.setExpiryDate(gstNext20Days);
							ewaySandboxSubscriptionDetails.setApiType(MasterGSTConstants.EWAYBILLSANDBOX_API);
							ewaySandboxSubscriptionDetails.setAllowedClients(0);
							ewaySandboxSubscriptionDetails.setAllowedCenters(0);
							ewaySandboxSubscriptionDetails.setAllowedInvoices(0);
							subscriptionService.updateSubscriptionData(ewaySandboxSubscriptionDetails);
						}else{
							SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
							subscriptionDetails.setUserid(dbUser.getId().toString());
							subscriptionDetails.setPaidAmount(0d);
							subscriptionDetails.setRegisteredDate(today);
							cal.add(Calendar.MONTH, 1);
							Date nextMonth = cal.getTime();
							subscriptionDetails.setExpiryDate(nextMonth);
							subscriptionDetails.setAllowedClients(0);
							subscriptionDetails.setAllowedCenters(0);
							subscriptionDetails.setAllowedInvoices(0);
							subscriptionService.updateSubscriptionData(subscriptionDetails);
						}
				}
			}
			
			dbUser.setDisable(user.getDisable());
			if(isNotEmpty(user.getDisable()) && user.getDisable().equalsIgnoreCase("true")) {
				updateCompanyUsers(user.getDisable(), dbUser.getId().toString());
			}
			dbUserUpdate = userService.updateUser(dbUser);
		}
		SubscriptionDetails subscriptionDetails = subscriptionService.getSubscriptionData(dbUserUpdate.getId().toString());
		if(NullUtil.isNotEmpty(subscriptionDetails)){
			dbUserUpdate.setSubscriptionType(subscriptionDetails.getPlanid());
			dbUserUpdate.setPaidAmount(Double.toString(subscriptionDetails.getPaidAmount()));
		}
		
		List<Client> lClient = clientService.findByUserid(dbUserUpdate.getId().toString());
		List<CompanyInvoices> lCompanyInvoices = profileService.getInvoices(dbUserUpdate.getId().toString());
		if(NullUtil.isNotEmpty(dbUserUpdate.getType()) && dbUserUpdate.getType().equals(MasterGSTConstants.SUVIDHA_CENTERS)) {
			List<CompanyCenters> centers = profileService.getCenters(dbUserUpdate.getId().toString());
			if(NullUtil.isNotEmpty(centers)) {
				dbUserUpdate.setTotalCenters(centers.size()+"");
			}
		}
		if(NullUtil.isNotEmpty(lClient)){
			dbUserUpdate.setTotalClients(Integer.toString(lClient.size()));
		}
		if(NullUtil.isNotEmpty(lCompanyInvoices)){
			dbUserUpdate.setTotalInvoices(Integer.toString(lCompanyInvoices.size()));
		}
		
		dbUserUpdate.setUserId(dbUserUpdate.getId().toString());
		logger.debug(CLASSNAME + method + END);
		return dbUserUpdate;
	}
	
	@Async
	public void updateCompanyUsers(String flag, String userid) {
		//List<CompanyUser> users = companyUserRepository.findByUserid(userid);
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setDisable(flag);
					userRepository.save(usr);
				}
			}
		}
	}
	
	@RequestMapping(value = "/updteprofileuserdtls", method = RequestMethod.POST)
	public @ResponseBody User updateUserProfileDetails(@RequestBody User user, @RequestParam String id, ModelMap model) throws Exception {
		final String method = "updateUserDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		User dbUser = userService.findById(id);
		User dbUserUpdate = null;
		if(NullUtil.isNotEmpty(dbUser)) {
			if(NullUtil.isNotEmpty(user.getAddress())) {
				dbUser.setAddress(user.getAddress());
			}
			if(NullUtil.isNotEmpty(user.getMobilenumber())) {
				dbUser.setMobilenumber(user.getMobilenumber());
				if(NullUtil.isNotEmpty(dbUser.getParentid())) {
					CompanyUser companyUser = profileService.getCompanyUser(dbUser.getEmail());
					if(NullUtil.isNotEmpty(companyUser)) {
						companyUser.setMobile(user.getMobilenumber());
						companyUserRepository.save(companyUser);
					}
				}
			}
			
			dbUserUpdate = userService.updateUser(dbUser);
		}
		
		
		dbUserUpdate.setUserId(dbUserUpdate.getId().toString());
		logger.debug(CLASSNAME + method + END);
		return dbUserUpdate;
	}

	@RequestMapping(value = "/createuser", method = RequestMethod.POST)
	public ModelAndView createuser(@ModelAttribute("user") User user, @RequestParam String usertype, 
			@RequestParam String inviteId, @RequestParam String subscrid, ModelMap model) throws Exception {
		final String method = "createuser::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "usertype\t" + usertype);
		boolean isExist = userService.collectionExists("users");
		logger.debug(CLASSNAME + "dashboard:: isExist\t" + isExist);
		User createdUser = null;
		try {
			if (isExist) {
				createdUser = userService.findByEmail(user.getEmail().toLowerCase());
			}
			if ((NullUtil.isNotEmpty(createdUser) && NullUtil.isEmpty(createdUser.getType()))
					|| (NullUtil.isNotEmpty(createdUser) && NullUtil.isNotEmpty(createdUser.getType()) 
							&& !(createdUser.getType().equals(MasterGSTConstants.DEMO_USER) || createdUser.getType().equals(MasterGSTConstants.SITE_ENROLL_USER)))) {
				userBasedModel(usertype, model);
				model.addAttribute("error", "Enter email id already exists");
				logger.debug(CLASSNAME + method + END);
				return new ModelAndView("signup/signup", "user", user);
			} else {
				if(NullUtil.isNotEmpty(createdUser) && NullUtil.isEmpty(createdUser.getPassword()) 
						&& (createdUser.getType().equals(MasterGSTConstants.DEMO_USER) || createdUser.getType().equals(MasterGSTConstants.SITE_ENROLL_USER))) {
					user.setId(createdUser.getId());
				} else {
					user.setCategory(MasterGSTConstants.USER_DIRECT);
					Date dt = new Date();
					user.setCreatedDate(dt);
					if(usertype.equals(MasterGSTConstants.PARTNER)){
						int month=-1,year=-1;
						month = dt.getMonth();
						year = dt.getYear()+1900;
						int quarter = month/3;
						quarter = quarter == 0 ? 4 : quarter;
						String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
						month++;
						user.setMthCd(""+month);
						user.setYrCd(yearCode);
					}
				}
				user.setPassword(Base64.getEncoder()
						.encodeToString(user.getPassword().getBytes(MasterGSTConstants.PASSWORD_ENCODE_FORMAT)));
				user.setType(usertype);
				user.setEmail(user.getEmail().toLowerCase());
				if(usertype.equals(MasterGSTConstants.SUVIDHA_CENTERS)){
					user.setDisable("true");
				}else{
					user.setDisable("false");
				}
				user.setOtpVerified("false");
				
				UserSequenceIdGenerator sequencegenerator=userSequenceIdGeneratorRepository.findBySequenceIdName(MasterGSTConstants.SEQUENCE_GENERATOR);
				if(isNotEmpty(sequencegenerator) && isNotEmpty(sequencegenerator.getUserSequenceId())) {
					user.setUserSequenceid(sequencegenerator.getUserSequenceId());
					sequencegenerator.setUserSequenceId(sequencegenerator.getUserSequenceId()+1);
					userSequenceIdGeneratorRepository.save(sequencegenerator);
				}
				
				if(NullUtil.isNotEmpty(inviteId)) {
					User invitepartner=userService.findById(inviteId);
					if(NullUtil.isNotEmpty(invitepartner)) {
						if(NullUtil.isNotEmpty(invitepartner.getEmail())) {
							user.setPartnerEmail(invitepartner.getEmail());
						}
						user.setRefid(invitepartner.getUserSequenceid()+"");
					}
				}
				createdUser = userService.createUser(user);
				if(usertype.equals(MasterGSTConstants.SUVIDHA_CENTERS)){
					
					CompanyCenters center=new CompanyCenters();
					center.setUserid(user.getId().toString());
					center.setFullname(user.getFullname());
					center.setName(user.getFullname());
					center.setContactperson(user.getFullname());
					center.setEmail(user.getEmail().toLowerCase());
					center.setMobilenumber(user.getMobilenumber());
					center.setPassword(user.getPassword());
					center.setAddress(user.getAddress());
					profileService.saveCenterSuvidhaSignup(center);
				}
				
				if(NullUtil.isNotEmpty(inviteId)) {
					partnerService.updatePartnerDetails(inviteId, createdUser);
				}
				if (NullUtil.isNotEmpty(createdUser)) {
					final User createdUser1 = createdUser;
						otpService.deleteByUserid(createdUser1.getId().toString());
						OTP otp = new OTP();
						otp.setOtp(SMSUtil.OTP(4));
						otp.setUserid(createdUser1.getId().toString());
						otp.setStatus("init");
						logger.debug(CLASSNAME + method + " SMS Begin");
						otpService.createOTP(otp);
						masterGstExecutorService.execute(()->{
						List<String> destinationNos = new ArrayList<String>();
						destinationNos.add(createdUser1.getMobilenumber());
						String sms = null;
						boolean isOtpFail = false;
						try {
							sms = smsService.sendSMS(destinationNos, otp.getOtp(), false,false);
						} catch (Exception e1) {
							logger.error(CLASSNAME + method + "Email ERROR {}", e1);
						}
						logger.debug(CLASSNAME + method + "SMS End\t" + sms);
						if (NullUtil.isNotEmpty(sms)) {
							if (sms.contains("OK:")) {
								otp.setStatus("success");
								otpService.createOTP(otp);
							} else {
								isOtpFail = true;
							}
						} else {
							isOtpFail = true;
						}
						try {
							logger.debug(CLASSNAME + method + "emailService Begin");
							emailService.sendEnrollEmail(createdUser1.getEmail(),
							VmUtil.velocityTemplate("otp.vm", user.getFullname(), otp.getOtp(), null),
							MasterGSTConstants.OTP_EMAILSUBJECT);
							if(isOtpFail){
								otp.setStatus("success");
								otpService.createOTP(otp);
								isOtpFail = false;
							}
							logger.debug(CLASSNAME + method + "emailService end");
						} catch(MasterGSTException e) {
							isOtpFail = true;
							logger.error(CLASSNAME + method + "Email ERROR {}", e);
						}
						if(isOtpFail){
							otp.setStatus("error");
							otpService.createOTP(otp);
						}
						Calendar cal = Calendar.getInstance();
						Date today = cal.getTime();
						if(usertype.equals(MasterGSTConstants.ASPDEVELOPER)){
							SubscriptionDetails gstSandboxSubscriptionDetails = new SubscriptionDetails();
							SubscriptionDetails ewaySandboxSubscriptionDetails = new SubscriptionDetails();
							gstSandboxSubscriptionDetails.setUserid(createdUser1.getId().toString());
							gstSandboxSubscriptionDetails.setPaidAmount(10d);
							gstSandboxSubscriptionDetails.setRegisteredDate(today);
							cal.add(Calendar.DATE, 20);
							Date gstNext20Days = cal.getTime();
							gstSandboxSubscriptionDetails.setExpiryDate(gstNext20Days);
							gstSandboxSubscriptionDetails.setApiType(MasterGSTConstants.GSTSANDBOX_API);
							gstSandboxSubscriptionDetails.setAllowedClients(0);
							gstSandboxSubscriptionDetails.setAllowedCenters(0);
							gstSandboxSubscriptionDetails.setAllowedInvoices(0);
							subscriptionService.updateSubscriptionData(gstSandboxSubscriptionDetails);
							ewaySandboxSubscriptionDetails.setUserid(createdUser1.getId().toString());
							ewaySandboxSubscriptionDetails.setPaidAmount(10d);
							ewaySandboxSubscriptionDetails.setRegisteredDate(today);
							ewaySandboxSubscriptionDetails.setExpiryDate(gstNext20Days);
							ewaySandboxSubscriptionDetails.setApiType(MasterGSTConstants.EWAYBILLSANDBOX_API);
							ewaySandboxSubscriptionDetails.setAllowedClients(0);
							ewaySandboxSubscriptionDetails.setAllowedCenters(0);
							ewaySandboxSubscriptionDetails.setAllowedInvoices(0);
							subscriptionService.updateSubscriptionData(ewaySandboxSubscriptionDetails);
						}else{
							SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
							subscriptionDetails.setUserid(createdUser1.getId().toString());
							subscriptionDetails.setPaidAmount(0d);
							subscriptionDetails.setRegisteredDate(today);
							cal.add(Calendar.MONTH, 1);
							Date nextMonth = cal.getTime();
							subscriptionDetails.setExpiryDate(nextMonth);
							subscriptionDetails.setAllowedClients(0);
							subscriptionDetails.setAllowedCenters(0);
							subscriptionDetails.setAllowedInvoices(0);
							subscriptionService.updateSubscriptionData(subscriptionDetails);
						}
					});
					
					model.addAttribute("id", createdUser1.getId());
					model.addAttribute("source", "otp/otp_success");
					model.addAttribute("initialSignup", "yes");
					logger.debug(CLASSNAME + method + END);
				}
				model.addAttribute("subscrid", subscrid);
				logger.debug(CLASSNAME + method + "new otp");
				return new ModelAndView("otp/otp", "otp", new OTP());
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(CLASSNAME + "signupdemo" + e.getMessage());
			e.printStackTrace();
			model.addAttribute("error", MasterGSTConstants.PASSWORD_ENCODE_EXCEPTION);
			return new ModelAndView("signup/signup", "user", user);
		} catch (MasterGSTException e) {
			logger.error(CLASSNAME + "signupdemo" + e.getMessage());
			e.printStackTrace();
			model.addAttribute("error", e.getMessage());
			return new ModelAndView("signup/signup", "user", user);
		}

	}

	private void userBasedModel(String type, ModelMap model) throws Exception {
		switch (type) {
		case MasterGSTConstants.PARTNER:
			model.addAttribute("typeheader", "Partner Enrollment");
			model.addAttribute("usertype", MasterGSTConstants.PARTNER);
			model.addAttribute("nooftext", "Number of Clients");
			break;
		case MasterGSTConstants.ASPDEVELOPER:
			model.addAttribute("typeheader", "ASP/Developer Enrollment");
			model.addAttribute("usertype", MasterGSTConstants.ASPDEVELOPER);
			break;
		case MasterGSTConstants.CAS:
			model.addAttribute("typeheader", "CA/CMA/CS/Tax Professional Enrollment");
			model.addAttribute("usertype", MasterGSTConstants.CAS);
			model.addAttribute("nooftext", "Number of Clients");
			//model.addAttribute("registrationnumber", "yes");
			break;
		case MasterGSTConstants.TAXPRACTITIONERS:
			model.addAttribute("typeheader", "Tax Professional");
			model.addAttribute("usertype", MasterGSTConstants.TAXPRACTITIONERS);
			model.addAttribute("nooftext", "Number of Clients");

			break;
		case MasterGSTConstants.SUVIDHA_CENTERS:
			model.addAttribute("typeheader", "Suvidha Centers");
			model.addAttribute("usertype", MasterGSTConstants.SUVIDHA_CENTERS);
			model.addAttribute("nametext", "Suvidha Kendra / Franchise Name");
			break;
		case MasterGSTConstants.BUSINESS:
			model.addAttribute("typeheader", "Small/Medium Business Enrollment");
			model.addAttribute("nametext", "Business Name");
			model.addAttribute("nooftext", "Number of Branches");
			model.addAttribute("erptext", "Any ERP");
			model.addAttribute("usertype", MasterGSTConstants.BUSINESS);
			break;
		case MasterGSTConstants.ENTERPRISE:
			model.addAttribute("typeheader", "Enterprise Enrollment");
			model.addAttribute("nametext", "Enterprises Name");
			model.addAttribute("nooftext", "Number of Branches");
			model.addAttribute("erptext", "Using Any ERPs");
			model.addAttribute("usertype", MasterGSTConstants.ENTERPRISE);
			break;
		default:
			break;
		}

	}

}
