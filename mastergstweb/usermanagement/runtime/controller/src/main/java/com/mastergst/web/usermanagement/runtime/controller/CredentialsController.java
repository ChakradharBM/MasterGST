/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.mastergst.configuration.service.Message;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.HeaderKeys;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.domain.UserKeys;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.service.MessageService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;

/**
 * Handles User Credentials user depending on the URI template.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping
public class CredentialsController {

	private static final Logger logger = LogManager.getLogger(CredentialsController.class.getName());
	private static final String CLASSNAME = "CredentialsController::";

	@Autowired
	UserService userService;
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	MessageService messageService;
	
	@RequestMapping(value = "/credentials", method = RequestMethod.GET)
	public ModelAndView credentials(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname, @RequestParam(value = "usertype", required = false) String userType,@RequestParam(value = "creType", required = true) String creType,  ModelMap model) throws Exception {
		final String method = "credentials::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.getUser(id);
		List<UserKeys> lUserKeys = user.getUserkeys();
		credentialsModel(user, lUserKeys, model);
		model.addAttribute("usertype", userType);
		model.addAttribute("creType", creType);
		String str = user.getFullname();
		
		String strArr[] = str.split("\\s");
		if (strArr.length >= 2) {
			model.addAttribute("fname", strArr[0]);
			model.addAttribute("lname", strArr[1]);
		} else {
			model.addAttribute("fname",user.getFullname());
		}
		
		if("authorizationKeys".equalsIgnoreCase(creType)) {
			Map<String,String> headerkeysTypeMap=new HashMap<String,String>();
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getUserkeys()) ) {
				for(UserKeys userkeys:user.getUserkeys()) {
					headerkeysTypeMap.put(userkeys.getClientid(), userkeys.getStage());
				}
			}
			List<HeaderKeys> headerkeys = userService.getHeaderkeys(id);
			headerkeys.stream().forEach(headers->{
				headers.setHeaderid(headers.getId().toString());
				if(headerkeysTypeMap.containsKey(headers.getUserclientid())){
					if("Production".equalsIgnoreCase(headerkeysTypeMap.get(headers.getUserclientid()))) {
						headers.setHeaderKeysType("GST Production");
					}else if("Sandbox".equalsIgnoreCase(headerkeysTypeMap.get(headers.getUserclientid()))) {
						headers.setHeaderKeysType("GST Sandbox");
					}else {
						headers.setHeaderKeysType(headerkeysTypeMap.get(headers.getUserclientid()));
					}
				}
			});
			model.addAttribute("headerkeys", headerkeys);
		}
		
		List<Message> messages = messageService.getMessagesByUserType(MasterGSTConstants.ASPDEVELOPER);
		messages.forEach((m)->m.setMsgId(m.getId().toString()));
		model.addAttribute("messages", messages);
		model.addAttribute("messageCount",messages.size());
		logger.debug(CLASSNAME + method + END);
		return new ModelAndView("credentials/credentials_"+creType, "userkeys", new UserKeys());
	}
	
	@RequestMapping(value = "/createcredentials", method = RequestMethod.POST)
	public String createcredentials(@ModelAttribute("userkeys") UserKeys userkeys,@RequestParam(value = "creType", required = true) String creType,
			@RequestParam("hiddenid") String hiddenid, ModelMap model) throws Exception {
		final String method = "createcredentials::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.getUser(hiddenid);
		userkeys.setIsenabled(false);
		List<UserKeys> lUserKeys = user.getUserkeys();
		if (NullUtil.isEmpty(lUserKeys)) {
			lUserKeys = Lists.newArrayList();
		}
		lUserKeys.add(userkeys);
		user.setUserkeys(lUserKeys);
		User updatedUser = userService.createUser(user);
		credentialsModel(updatedUser, lUserKeys, model);
		model.addAttribute("creType", creType);
		logger.debug(CLASSNAME + method + END);
		//return new ModelAndView("credentials/credentials_"+creType, "userkeys", new UserKeys());
		
		return "redirect:/credentials?id="+hiddenid+"&fullname="+user.getFullname()+"&usertype=&creType="+creType;
	}
	
	@RequestMapping(value = "/updatecredentials", method = RequestMethod.POST)
	public @ResponseBody User updateCredentials(@RequestBody UserKeys userkey,
			@RequestParam("id") String id, ModelMap model) throws Exception {
		final String method = "updateCredentials::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.getUser(id);
		userkey.setCreatedate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		if(NullUtil.isNotEmpty(userkey.getClientid()) && NullUtil.isNotEmpty(userkey.getClientsecret())) {
			List<UserKeys> userkeys = user.getUserkeys();
			if (NullUtil.isEmpty(userkeys)) {
				userkeys = Lists.newArrayList();
			} else {
				List<UserKeys> tUserkeys = Lists.newArrayList();
				for(UserKeys key : userkeys) {
					if(!key.getStage().equals(userkey.getStage())) {
						tUserkeys.add(key);
					}
				}
				userkeys = tUserkeys;
			}
			userkeys.add(userkey);
			user.setUserkeys(userkeys);
		} else if(NullUtil.isNotEmpty(userkey.getStage()) 
				&& NullUtil.isNotEmpty(userkey.isIsenabled())) {
			List<UserKeys> userkeys = user.getUserkeys();
			if(NullUtil.isNotEmpty(userkeys)) {
				for(UserKeys key : userkeys) {
					if(key.getStage().equals(userkey.getStage())) {
						key.setIsenabled(userkey.isIsenabled());
					}
				}
			}
		}
		User usr = userService.updateUser(user);
		
		SubscriptionDetails subscriptionDetails = subscriptionService.getSubscriptionData(usr.getId().toString());
		if(subscriptionDetails != null){
			usr.setSubscriptionType(subscriptionDetails.getPlanid());
			usr.setPaidAmount(Double.toString(subscriptionDetails.getPaidAmount()));
		}
		usr.setUserId(usr.getId().toString());
		
		logger.debug(CLASSNAME + method + END);
		return usr;
	}

	@RequestMapping(value = "/deletekeys", method = RequestMethod.GET)
	public ModelAndView deletekeys(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "keyid", required = true) String keyid, @RequestParam(value = "creType", required = true) String creType, ModelMap model) throws Exception {
		final String method = "deletekeys::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.getUser(id);
		List<UserKeys> lUserKeys = user.getUserkeys();
		if (NullUtil.isNotEmpty(lUserKeys)) {
			lUserKeys.removeIf(userKeys -> userKeys.getId().toString().contains(keyid));
		}
		user.setUserkeys(lUserKeys);
		User updatedUser = userService.createUser(user);
		credentialsModel(updatedUser, lUserKeys, model);
		model.addAttribute("creType", creType);
		logger.debug(CLASSNAME + method + END);
		return new ModelAndView("credentials/credentials_"+creType, "userkeys", new UserKeys());
	}

	private void credentialsModel(User user, List<UserKeys> lUserKeys, ModelMap model) throws Exception {
		model.addAttribute("id", user.getId());
		model.addAttribute("fullname", user.getFullname());
		model.addAttribute("mail", user.getEmail());
		model.addAttribute("mobilenumber", user.getMobilenumber());
		model.addAttribute("address", user.getAddress());
		addEnvkeys(lUserKeys, model);
		model.addAttribute("headerflag", "credentials");
		model.addAttribute("otheruser", "no");
	}
	
	private void addEnvkeys(List<UserKeys> lUserKeys, ModelMap model){
		if(lUserKeys != null){
			lUserKeys.forEach(key->model.addAttribute(key.getStage(), key));
		}
	}
	
	@RequestMapping(value = "/deleteAspHeaderkeys", method = RequestMethod.GET)
	public @ResponseBody List<HeaderKeys> deleteHeaderkeys(@RequestParam String headerkeyid,
			@RequestParam(value = "id", required = true) String id) throws Exception {
		final String method = "deleteHeaderkeys::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.getUser(id);
		userService.deleteAspDeveloperHeaderkeys(headerkeyid);
		List<HeaderKeys> headerkeys = userService.getHeaderkeys(id);
				
		Map<String,String> headerkeysTypeMap=new HashMap<String,String>();
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getUserkeys()) ) {
			for(UserKeys userkeys:user.getUserkeys()) {
				headerkeysTypeMap.put(userkeys.getClientid(), userkeys.getStage());
			}
		}
		
		headerkeys.stream().forEach(headers->{
			headers.setHeaderid(headers.getId().toString());
			if(headerkeysTypeMap.containsKey(headers.getUserclientid())){
				if("Production".equalsIgnoreCase(headerkeysTypeMap.get(headers.getUserclientid()))) {
					headers.setHeaderKeysType("GST Production");
				}else if("Sandbox".equalsIgnoreCase(headerkeysTypeMap.get(headers.getUserclientid()))) {
					headers.setHeaderKeysType("GST Sandbox");
				}else {
					headers.setHeaderKeysType(headerkeysTypeMap.get(headers.getUserclientid()));
				}
			}
		});
			
		logger.debug(CLASSNAME + method + END);
		
		return headerkeys;
	}
}
