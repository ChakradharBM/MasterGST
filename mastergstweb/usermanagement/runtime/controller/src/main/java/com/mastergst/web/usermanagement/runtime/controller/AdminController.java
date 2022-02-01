/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.NO;
import static com.mastergst.core.common.MasterGSTConstants.YES;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxls.template.SimpleExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.LatestNews;
import com.mastergst.configuration.service.LatestUpdates;
import com.mastergst.configuration.service.Message;
import com.mastergst.configuration.service.Role;
import com.mastergst.configuration.service.RoleRepository;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.ApiExceedsUsers;
import com.mastergst.login.runtime.domain.CompaignEmail;
import com.mastergst.login.runtime.domain.HeaderKeys;
import com.mastergst.login.runtime.domain.SubscriptionUsers;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.HeaderKeysRepository;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.dao.PartnersDao;
import com.mastergst.usermanagement.runtime.domain.AdminSummary;
import com.mastergst.usermanagement.runtime.domain.AspUserDetails;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientPaymentDetails;
import com.mastergst.usermanagement.runtime.domain.Comments;
import com.mastergst.usermanagement.runtime.domain.CompanyCenters;
import com.mastergst.usermanagement.runtime.domain.CompanyRole;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.GSTReturnSummary;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.PartnerBankDetails;
import com.mastergst.usermanagement.runtime.domain.PartnerClient;
import com.mastergst.usermanagement.runtime.domain.PartnerClientInfo;
import com.mastergst.usermanagement.runtime.domain.PartnerFilter;
import com.mastergst.usermanagement.runtime.domain.PartnerPayments;
import com.mastergst.usermanagement.runtime.domain.PaymentDetails;
import com.mastergst.usermanagement.runtime.domain.PaymentLink;
import com.mastergst.usermanagement.runtime.domain.Permission;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.domain.UserSequenceIdGenerator;
import com.mastergst.usermanagement.runtime.repository.ClientRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyRoleRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyUserRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR2Repository;
import com.mastergst.usermanagement.runtime.repository.PartnerClientRepository;
import com.mastergst.usermanagement.runtime.repository.PaymentLinkRepository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;
import com.mastergst.usermanagement.runtime.repository.SubscriptionDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.UserSequenceIdGeneratorRepository;
import com.mastergst.usermanagement.runtime.service.AdminSupportService;
import com.mastergst.usermanagement.runtime.service.AspUserDetailsService;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.CommentsService;
import com.mastergst.usermanagement.runtime.service.LatestNewsService;
import com.mastergst.usermanagement.runtime.service.LatestUpdatesService;
import com.mastergst.usermanagement.runtime.service.MessageService;
import com.mastergst.usermanagement.runtime.service.MeteringService;
import com.mastergst.usermanagement.runtime.service.PartnerService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;
import com.mastergst.usermanagement.runtime.support.Utility;
import com.mastergst.usermanagement.runtime.domain.TotalLeadDetails;
/**
 * Handles Admin depending on the URI template.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping
public class AdminController {

	private static final Logger logger = LogManager.getLogger(AdminController.class.getName());
	private static final String CLASSNAME = "AdminController::";
	DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");  
	@Autowired
	UserService userService;
	@Autowired
	MeteringService meteringService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	ClientService clientService;
	@Autowired
	PartnerService partnerService;
	@Autowired
	ProfileService profileService;
	@Autowired
	MessageService messageService;
	@Autowired
	private CommentsService commentService;

	@Autowired
	private LatestUpdatesService latestUpdatesService;
	@Autowired
	private AdminSupportService adminSupportService;
	
	@Autowired
	private LatestNewsService latestNewsService;
	
	@Autowired
	private AspUserDetailsService aspUserDetailsService;
	
	@Autowired
	CompanyUserRepository companyUserRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserSequenceIdGeneratorRepository userSequenceIdGeneratorRepository;
	
	@Autowired
	PurchaseRegisterRepository purchaseRepository;
	
	@Autowired
	GSTR2Repository gstr2Repository;
	
	@Autowired
	CompanyRoleRepository companyRoleRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	ClientRepository clientRepository;
	
	@Autowired
	PartnersDao partnersDao;
	@Autowired
	PartnerClientRepository partnerClientRepository;
	@Autowired
	SubscriptionDetailsRepository subscriptionDetailsRepository;
	@Autowired
	PaymentLinkRepository paymentLinkRepository;
	/**
	 * To navigate to user page in Admin tool 
	 *  
	 * @param id
	 * @param fullname
	 * @param userType
	 * @param model
	 * @return String - allusers/users.jsp
	 * @throws Exception
	 */
	@RequestMapping(value = "/obtainusers", method = RequestMethod.GET)
	public String obtainUsersPage(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "usertype", required = true) String userType, ModelMap model) throws Exception {
		final String method = "obtainUsersPage::obtainUsersPage::";
		logger.debug(CLASSNAME + method + BEGIN);
		User usr = userService.findById(id);
		model.addAttribute("id", id);
		model.addAttribute("userDetails", usr);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", userType);
		logger.debug(CLASSNAME + method + END);
		return "allusers/users";
	}
	
	/**
	 * Get the User Details based on the Userid and UserType
	 * 
	 * @param id
	 * @param type
	 * @param model
	 * @return User - User Details based on id & type
	 * @throws Exception
	 */
	@RequestMapping(value = "/admusrdtls", method = RequestMethod.GET)
	public @ResponseBody User userDetailsInAdmin(@RequestParam(value = "id", required = true) String id, 
			@RequestParam(value = "type", required = true) String type, ModelMap model) throws Exception {
		final String method = "userDetailsInAdmin::";
		logger.debug(CLASSNAME + method + BEGIN);
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");  
		User usr = userService.findById(id);
		User user = new User();
		if(NullUtil.isNotEmpty(usr.getLastLoggedIn())) {
			
			SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-YYY hh:mm:ss a");
			
			sdf.format(usr.getLastLoggedIn());
			user.setUsrLastLoggedIn(sdf.format(usr.getLastLoggedIn()));
			user.setLastLoggedIn(usr.getLastLoggedIn());
		}
		if(NullUtil.isNotEmpty(usr.getNeedToFollowUp())) {
			user.setNeedToFollowUp(usr.getNeedToFollowUp());
		}
		if(NullUtil.isNotEmpty(usr.getAgreementStatus())) {
			user.setAgreementStatus(usr.getAgreementStatus());
		}
		if(NullUtil.isNotEmpty(usr.getNeedToFollowUpComment())) {
			user.setNeedToFollowUpComment(usr.getNeedToFollowUpComment());
		}
		Comments comments=commentService.getCommentsData(id);
		if(NullUtil.isNotEmpty(comments)) {
			user.setComments(comments.getComments());
			user.setCommentDate(comments.getCommentDate());
			user.setAddedby(comments.getAddedby());
		}
		SubscriptionDetails subscriptionDetails = null;
		if(NullUtil.isNotEmpty(type) && type.equals(MasterGSTConstants.ASPDEVELOPER)){
			SubscriptionDetails gstApiAllowedCountSubscriptionDetails = subscriptionService.getSubscriptionData(id,"GSTAPI");
			SubscriptionDetails gstApiAllowedSandboxUsageCountSubScriptionDetails = subscriptionService.getSubscriptionData(id,"GSTSANDBOXAPI");
			SubscriptionDetails ewaybillApiAllowedCountSubscriptionDetails = subscriptionService.getSubscriptionData(id,"EWAYAPI");
			SubscriptionDetails ewaybillSanboxAllowedCountSubscriptionDetails = subscriptionService.getSubscriptionData(id,"EWAYBILLSANDBOXAPI");
			SubscriptionDetails einvApiAllowedCountSubscriptionDetails = subscriptionService.getSubscriptionData(id,"E-INVOICEAPI");
			SubscriptionDetails einvSanboxAllowedCountSubscriptionDetails = subscriptionService.getSubscriptionData(id,"E-INVOICESANDBOXAPI");
			Calendar calendar = Calendar.getInstance();
		    calendar.add(Calendar.DAY_OF_YEAR, 1);
		    Date tomorrowDate = calendar.getTime();
			if(NullUtil.isNotEmpty(gstApiAllowedCountSubscriptionDetails)) {
				user.setGstAPIAllowedInvoices(gstApiAllowedCountSubscriptionDetails.getAllowedInvoices());
				user.setGstAPIUsageCountInvoices(gstApiAllowedCountSubscriptionDetails.getProcessedInvoices());
				Date endDate=gstApiAllowedCountSubscriptionDetails.getExpiryDate();
				if(tomorrowDate.compareTo(endDate)>0) {
					user.setGstAPIStatus("EXPIRED");
				}else {
					user.setGstAPIStatus("ACTIVE");					
				}
				user.setGstSubscriptionStartDate(dateFormat.format(gstApiAllowedCountSubscriptionDetails.getRegisteredDate()));
				user.setGstSubscriptionExpiryDate(dateFormat.format(gstApiAllowedCountSubscriptionDetails.getExpiryDate()));
			}
			if(NullUtil.isNotEmpty(gstApiAllowedSandboxUsageCountSubScriptionDetails)) {
				user.setGstSanboxAllowedCountInvoices(gstApiAllowedSandboxUsageCountSubScriptionDetails.getAllowedInvoices());
				user.setGstSanboxUsageCountInvoices(gstApiAllowedSandboxUsageCountSubScriptionDetails.getProcessedSandboxInvoices());
user.setGstSandboxSubscriptionStartDate(dateFormat.format(gstApiAllowedSandboxUsageCountSubScriptionDetails.getRegisteredDate()));
			if(NullUtil.isNotEmpty(gstApiAllowedSandboxUsageCountSubScriptionDetails.getExpiryDate())) {
					if(NullUtil.isNotEmpty(gstApiAllowedSandboxUsageCountSubScriptionDetails.getSubscriptionType())){
						 if("UNLIMITED".equalsIgnoreCase(gstApiAllowedSandboxUsageCountSubScriptionDetails.getSubscriptionType())){
								user.setGstSandboxSubscriptionExpiryDate("UNLIMITED");
								user.setGstSandboxAPIStatus("ACTIVE");	
							}else{
								Date endDate=gstApiAllowedSandboxUsageCountSubScriptionDetails.getExpiryDate();
								user.setGstSandboxSubscriptionExpiryDate(dateFormat.format(gstApiAllowedSandboxUsageCountSubScriptionDetails.getExpiryDate()));	
								
								if(tomorrowDate.compareTo(endDate)>0) {
									user.setGstSandboxAPIStatus("EXPIRED");
								}else {
									user.setGstSandboxAPIStatus("ACTIVE");					
								}
						}
					}else{
						Date endDate=gstApiAllowedSandboxUsageCountSubScriptionDetails.getExpiryDate();
						user.setGstSandboxSubscriptionExpiryDate(dateFormat.format(gstApiAllowedSandboxUsageCountSubScriptionDetails.getExpiryDate()));	
						
						if(tomorrowDate.compareTo(endDate)>0) {
							user.setGstSandboxAPIStatus("EXPIRED");
						}else {
							user.setGstSandboxAPIStatus("ACTIVE");					
						}
					}
			}else{
				user.setGstSandboxAPIStatus("ACTIVE");
				user.setGstSandboxSubscriptionExpiryDate("UNLIMITED");
			}
			}
			if(NullUtil.isNotEmpty(ewaybillApiAllowedCountSubscriptionDetails)) {
				user.setEwaybillAPIAllowedInvoices(ewaybillApiAllowedCountSubscriptionDetails.getAllowedInvoices());
				user.setEwaybillAPIUsageCountInvoices(ewaybillApiAllowedCountSubscriptionDetails.getProcessedInvoices());
				Date endDate=ewaybillApiAllowedCountSubscriptionDetails.getExpiryDate();
				if(tomorrowDate.compareTo(endDate)>0) {
					user.setEwaybillAPIStatus("EXPIRED");
				}else {
					user.setEwaybillAPIStatus("ACTIVE");					
				}
				user.setEwaybillSubscriptionStartDate(dateFormat.format(ewaybillApiAllowedCountSubscriptionDetails.getRegisteredDate()));
				user.setEwaybillSubscriptionExpiryDate(dateFormat.format(ewaybillApiAllowedCountSubscriptionDetails.getExpiryDate()));				
			}
			if(NullUtil.isNotEmpty(ewaybillSanboxAllowedCountSubscriptionDetails)) {
				user.setEwaybillSanboxAllowedInvoices(ewaybillSanboxAllowedCountSubscriptionDetails.getAllowedInvoices());
				user.setEwaybillSanboxUsageCountInvoices(ewaybillSanboxAllowedCountSubscriptionDetails.getProcessedSandboxInvoices());
				
				
				if(NullUtil.isNotEmpty(ewaybillSanboxAllowedCountSubscriptionDetails.getExpiryDate())) {
					if(NullUtil.isNotEmpty(ewaybillSanboxAllowedCountSubscriptionDetails.getSubscriptionType())){
						 if("UNLIMITED".equalsIgnoreCase(ewaybillSanboxAllowedCountSubscriptionDetails.getSubscriptionType())){
								user.setEwaybillSandboxSubscriptionExpiryDate("UNLIMITED");
								user.setEwaybillSandboxAPIStatus("ACTIVE");	
							}else{
								Date endDate=ewaybillSanboxAllowedCountSubscriptionDetails.getExpiryDate();
								user.setEwaybillSandboxSubscriptionExpiryDate(dateFormat.format(ewaybillSanboxAllowedCountSubscriptionDetails.getExpiryDate()));	
								
								if(tomorrowDate.compareTo(endDate)>0) {
									user.setEwaybillSandboxAPIStatus("EXPIRED");
								}else {
									user.setEwaybillSandboxAPIStatus("ACTIVE");					
								}
						}
					}else{
						Date endDate=ewaybillSanboxAllowedCountSubscriptionDetails.getExpiryDate();
						user.setEwaybillSandboxSubscriptionExpiryDate(dateFormat.format(ewaybillSanboxAllowedCountSubscriptionDetails.getExpiryDate()));	
						
						if(tomorrowDate.compareTo(endDate)>0) {
							user.setEwaybillSandboxAPIStatus("EXPIRED");
						}else {
							user.setEwaybillSandboxAPIStatus("ACTIVE");					
						}
					}
			}else{
				user.setEwaybillSandboxAPIStatus("ACTIVE");
				user.setEwaybillSandboxSubscriptionExpiryDate("UNLIMITED");
			}
				user.setEwaybillSandboxSubscriptionStartDate(dateFormat.format(ewaybillSanboxAllowedCountSubscriptionDetails.getRegisteredDate()));
			}
			
			
			if(NullUtil.isNotEmpty(einvApiAllowedCountSubscriptionDetails)) {
				user.setEinvAPIAllowedInvoices(einvApiAllowedCountSubscriptionDetails.getAllowedInvoices());
				user.setEinvAPIUsageCountInvoices(einvApiAllowedCountSubscriptionDetails.getProcessedInvoices());
				Date endDate=einvApiAllowedCountSubscriptionDetails.getExpiryDate();
				if(tomorrowDate.compareTo(endDate)>0) {
					user.setEinvAPIStatus("EXPIRED");
				}else {
					user.setEinvAPIStatus("ACTIVE");					
				}
				user.setEinvSubscriptionStartDate(dateFormat.format(einvApiAllowedCountSubscriptionDetails.getRegisteredDate()));
				user.setEinvSubscriptionExpiryDate(dateFormat.format(einvApiAllowedCountSubscriptionDetails.getExpiryDate()));				
			}
			if(NullUtil.isNotEmpty(einvSanboxAllowedCountSubscriptionDetails)) {
				user.setEinvSanboxAllowedInvoices(einvSanboxAllowedCountSubscriptionDetails.getAllowedInvoices());
				user.setEinvSanboxUsageCountInvoices(einvSanboxAllowedCountSubscriptionDetails.getProcessedSandboxInvoices());
				
				
				if(NullUtil.isNotEmpty(einvSanboxAllowedCountSubscriptionDetails.getExpiryDate())) {
					if(NullUtil.isNotEmpty(einvSanboxAllowedCountSubscriptionDetails.getSubscriptionType())){
						 if("UNLIMITED".equalsIgnoreCase(einvSanboxAllowedCountSubscriptionDetails.getSubscriptionType())){
								user.setEinvSandboxSubscriptionExpiryDate("UNLIMITED");
								user.setEinvSandboxAPIStatus("ACTIVE");	
							}else{
								Date endDate=einvSanboxAllowedCountSubscriptionDetails.getExpiryDate();
								user.setEinvSandboxSubscriptionExpiryDate(dateFormat.format(einvSanboxAllowedCountSubscriptionDetails.getExpiryDate()));	
								
								if(tomorrowDate.compareTo(endDate)>0) {
									user.setEinvSandboxAPIStatus("EXPIRED");
								}else {
									user.setEinvSandboxAPIStatus("ACTIVE");					
								}
						}
					}else{
						Date endDate=einvSanboxAllowedCountSubscriptionDetails.getExpiryDate();
						user.setEinvSandboxSubscriptionExpiryDate(dateFormat.format(einvSanboxAllowedCountSubscriptionDetails.getExpiryDate()));	
						
						if(tomorrowDate.compareTo(endDate)>0) {
							user.setEinvSandboxAPIStatus("EXPIRED");
						}else {
							user.setEinvSandboxAPIStatus("ACTIVE");					
						}
					}
			}else{
				user.setEinvSandboxAPIStatus("ACTIVE");
				user.setEinvSandboxSubscriptionExpiryDate("UNLIMITED");
			}
				user.setEinvSandboxSubscriptionStartDate(dateFormat.format(einvSanboxAllowedCountSubscriptionDetails.getRegisteredDate()));
			}
			
			
		}else{
			subscriptionDetails = subscriptionService.getSubscriptionData(id);
		}
		if(NullUtil.isNotEmpty(subscriptionDetails)){
			user.setSubscriptionType(subscriptionDetails.getPlanid());
			user.setPaidAmount(Double.toString(subscriptionDetails.getPaidAmount()));
			user.setSubscriptionStartDate(dateFormat.format(subscriptionDetails.getRegisteredDate()));
			user.setSubscriptionExpiryDate(dateFormat.format(subscriptionDetails.getExpiryDate()));
			if(NullUtil.isNotEmpty(subscriptionDetails.getAllowedCenters())){
				user.setTotalCenters(subscriptionDetails.getAllowedCenters()+"");
			}
			if(NullUtil.isNotEmpty(type) && (type.equals(MasterGSTConstants.ENTERPRISE) || type.equals(MasterGSTConstants.BUSINESS))){
				if(NullUtil.isNotEmpty(subscriptionDetails.getAllowedClients())){
					user.setTotalClients(subscriptionDetails.getAllowedClients()+"");
				}
			}
			if(NullUtil.isNotEmpty(subscriptionDetails.getAllowedInvoices())){
				user.setTotalInvoices(subscriptionDetails.getAllowedInvoices()+"");
			}
			if(NullUtil.isNotEmpty(subscriptionDetails.getProcessedInvoices())){
				user.setTotalInvoicesUsed(subscriptionDetails.getProcessedInvoices()+"");
				user.setBranches(subscriptionDetails.getProcessedInvoices()+"");
				user.setAnyerp(subscriptionDetails.getProcessedSandboxInvoices()+"");
			}
			if(NullUtil.isNotEmpty(type) && (type.equals(MasterGSTConstants.SUVIDHA_CENTERS) || type.equals(MasterGSTConstants.CAS))){
				if(NullUtil.isNotEmpty(subscriptionDetails.getAllowedClients())){
					user.setTotalClients(subscriptionDetails.getAllowedClients()+"");
				}else {
					List<Client> lClient = clientService.findByUserid(user.getId().toString());
					if(lClient != null){
						user.setTotalClients(Integer.toString(lClient.size()));
					}
				}
			}
		}
		/*if(NullUtil.isNotEmpty(type) && (type.equals(MasterGSTConstants.SUVIDHA_CENTERS) || type.equals(MasterGSTConstants.CAS))){
			List<Client> lClient = clientService.findByUserid(id);
			if(lClient != null){
				user.setTotalClients(Integer.toString(lClient.size()));
			}
		}*/
		if(NullUtil.isNotEmpty(type) && type.equals(MasterGSTConstants.PARTNER)) {
			List<PartnerClient> partnerTotalInvitedClient = partnerService.getPartnerClients(id);
			List<PartnerClient> partnerTotalJoinedClient = partnerService.getPartnerJoinedClients(id);
			if(partnerTotalInvitedClient != null){
				user.setTotalInvitedClients(Integer.toString(partnerTotalInvitedClient.size()));
			}
			if(partnerTotalInvitedClient != null && partnerTotalJoinedClient != null){
				user.setTotalPendingClients(Integer.toString(partnerTotalInvitedClient.size() - partnerTotalJoinedClient.size()));
			}
			if(partnerTotalJoinedClient != null){
				long subscribedCount = 0;
				user.setTotalJoinedClients(Integer.toString(partnerTotalJoinedClient.size()));
				for(PartnerClient partnerTotalJoinedClients : partnerTotalJoinedClient){
					if(NullUtil.isNotEmpty(partnerTotalJoinedClients.getClientid())){
						List<PaymentDetails> payments = subscriptionService.getPaymentDetails(partnerTotalJoinedClients.getClientid().toString());
						if(!payments.isEmpty()){
							subscribedCount++;
						}
					}
				}
				user.setTotalSubscribedClients(subscribedCount+"");
			}
		}
		logger.debug(CLASSNAME + method + END);
		return user;
	}

	public String userDefinedSortColumn(String type) {
		
		if(type.equals("All") || type.equals("business") || type.equals("enterprise")) {
			return 4+"";
		}else if(type.equals("aspdeveloper") || type.equals("cacmas") || type.equals("suvidha")) {
			return 3+"";
		}else if(type.equals("partner")) {
			return 0+"";
		}else if(type.equals("subusers")) {
			return 4+"";
		}else if(type.equals("subcenters") || type.equals("testaccounts") || type.equals("spamusers")) {
			return 6+"";
		}
		return 0+"";
	}
	
	
	/**
	 * Get the Users list based on UserType 
	 * 
	 * @param type
	 * @param request
	 * @return Map<String, Object> - All Users based on User type
	 * @throws Exception
	 */
	@RequestMapping(value = "/allusers", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> allUsers(@RequestParam(value = "type", required = false) String type,  HttpServletRequest request) throws Exception {
		if(NullUtil.isEmpty(type)){
			type = "All";
		}
		String startStr = request.getParameter("start");
		int start = 0;
		if(NullUtil.isNotEmpty(startStr)){
			start = Integer.parseInt(startStr);
		}
		String lengthStr = request.getParameter("length");
		int length = 10;
		if(NullUtil.isNotEmpty(lengthStr)){
			length = Integer.parseInt(lengthStr);
		}
		if(start != 0){
			start = start/length;
		}
		String searchVal = request.getParameter("search[value]");
		if(searchVal != null && "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = null;
		String sortOrder = "asc";
		
		if("spamusers".equalsIgnoreCase(type)) {
			String sortCol = request.getParameter("order[0][column]");
			if(!NullUtil.isEmpty(sortCol)){
				String field = sortCol;
				sortParam = request.getParameter("columns["+field+"][name]");
				if(field != null){
					sortOrder = request.getParameter("order[0][dir]");
				}
			}
		}else {
			String sortCol = request.getParameter("order[0][column]");
			if(!NullUtil.isEmpty(sortCol)){
				String field = sortCol;
				sortParam = request.getParameter("columns["+field+"][name]");
				if(field != null){
					sortOrder = request.getParameter("order[0][dir]");
				}
			}
		}
		
		Map<String, User> map = Maps.newHashMap();
		//Get 
		Page<User> pageUsers = userService.getAllUsers(type, start, length, sortParam, sortOrder, searchVal);
		List<User> users = pageUsers.getContent();
		users.stream().forEach(usr->usr.setUserId(usr.getId().toString()));
		for(User usr : users) {
			map.put(usr.getId().toString(), usr);
		}
		for(User usr : users) {
			if(NullUtil.isNotEmpty(usr.getParentid())) {
				User parentUser = userService.findById(usr.getParentid());
				if(NullUtil.isNotEmpty(parentUser)){
					usr.setParentName(parentUser.getFullname());
					usr.setParentEmailId(parentUser.getEmail());
				}else{
					usr.setParentName("");
					usr.setParentEmailId("");
				}
			}
			if(NullUtil.isNotEmpty(usr.getParentid()) && NullUtil.isNotEmpty(map.get(usr.getParentid()))) {
				usr.setParentid(map.get(usr.getParentid()).getFullname());
			}
			usr.setBranches("");
			usr.setAnyerp("");
			usr.setUserId(usr.getId().toString());
		}
		Map<String, Object> userData = new HashMap<>();
		userData.put("data", users);
		userData.put("recordsFiltered", pageUsers.getTotalElements());
		userData.put("recordsTotal", pageUsers.getTotalElements());
		userData.put("draw", request.getParameter("draw"));
		return userData;
	}

	
	/**
	 * Get the Users list based on UserType 
	 * 
	 * @param type
	 * @param request
	 * @return Map<String, Object> - All Users based on User type
	 * @throws Exception
	 */
	@RequestMapping(value = "/allsubcenters", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> allSubCenters(@RequestParam(value = "type", required = false) String type,  HttpServletRequest request) throws Exception {
		
		String startStr = request.getParameter("start");
		int start = 0;
		if(NullUtil.isNotEmpty(startStr)){
			start = Integer.parseInt(startStr);
		}
		String lengthStr = request.getParameter("length");
		int length = 10;
		if(NullUtil.isNotEmpty(lengthStr)){
			length = Integer.parseInt(lengthStr);
		}
		if(start != 0){
			start = start/length;
		}
		String searchVal = request.getParameter("search[value]");
		if(searchVal != null && "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = null;
		String sortOrder = "asc";
		String sortCol = request.getParameter("order[0][column]");
		if(!NullUtil.isEmpty(sortCol)){
			String field = sortCol;
			sortParam = request.getParameter("columns["+field+"][name]");
			if(field != null){
				sortOrder = request.getParameter("order[0][dir]");
			}
		}
						
		Page<CompanyCenters> pageCenters = profileService.getAllSubCenters(start, length, sortParam, sortOrder, searchVal);
		
		List<CompanyCenters> companyCentersList=pageCenters.getContent();
		User user=null;
		
		Map<String, Object> userData = new HashMap<>();
		List<User> usersLst=new ArrayList<>();
		for(CompanyCenters center : companyCentersList) {
			if(NullUtil.isNotEmpty(center)) {
				user=new User();
				
				User usr=userService.findByEmail(center.getEmail());
				
				if(NullUtil.isNotEmpty(usr)) {
					user.setId(usr.getId());
					user.setUserId(usr.getId().toString());
				}
				user.setFullname(center.getName());
				user.setEmail(center.getEmail());
				user.setMobilenumber(center.getMobilenumber());
				user.setCreatedDate(center.getCreatedDate());
				if(NullUtil.isNotEmpty(center.getUserid())) {
					User parentUser = userService.findById(center.getUserid());
					if(NullUtil.isNotEmpty(parentUser)){
						user.setType(parentUser.getType());
						user.setParentName(parentUser.getFullname());
						user.setParentEmailId(parentUser.getEmail());
					}else{
						user.setType("");
						user.setParentName("");
						user.setParentEmailId("");
					}
				}
				usersLst.add(user);			
			}
		}
		
		userData.put("data",usersLst);
		userData.put("recordsFiltered", pageCenters.getTotalElements());
		userData.put("recordsTotal", pageCenters.getTotalElements());
		userData.put("draw", request.getParameter("draw"));
		return userData;
	}
	
		
	/**
	 * Get Messages based on UserType
	 * 
	 * @param type
	 * @param request
	 * @return  Map<String, Object> - Messages based on User type
	 * @throws Exception
	 */
	@RequestMapping(value = "/messages", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> allMessages(@RequestParam(value = "type", required = false) String type,  HttpServletRequest request) throws Exception {
		if(NullUtil.isEmpty(type)){
			type = "All";
		}
		String startStr = request.getParameter("start");
		int start = 0;
		if(NullUtil.isNotEmpty(startStr)){
			start = Integer.parseInt(startStr);
		}
		String lengthStr = request.getParameter("length");
		int length = 10;
		if(NullUtil.isNotEmpty(lengthStr)){
			length = Integer.parseInt(lengthStr);
		}
		if(start != 0){
			start = start/length;
		}
		String searchVal = request.getParameter("search[value]");
		if(searchVal != null && "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = null;
		String sortOrder = "asc";
		String sortCol = request.getParameter("order[0][column]");
		if(!NullUtil.isEmpty(sortCol)){
			sortParam = request.getParameter("columns["+sortCol+"][name]");
			if(sortParam != null){
				sortOrder = request.getParameter("order[0][dir]");
			}
		}
		Page<Message> messages = messageService.getMessages(start, length, sortParam, sortOrder, searchVal);
		messages.getContent().forEach((m)->m.setMsgId(m.getId().toString()));
		Map<String, Object> userData = new HashMap<>();
		userData.put("data", messages.getContent());
		userData.put("recordsFiltered", messages.getTotalElements());
		userData.put("recordsTotal", messages.getTotalElements());
		userData.put("draw", request.getParameter("draw"));
		return userData;
	}
	
	
	/**
	 * @author vimala
	 * @param type
	 * @param request
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value = "/updates", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> allupdates(@RequestParam(value = "type", required = false) String type,  HttpServletRequest request) throws Exception {
		if(NullUtil.isEmpty(type)){
			type = "All";
		}
		String startStr = request.getParameter("start");
		int start = 0;
		if(NullUtil.isNotEmpty(startStr)){
			start = Integer.parseInt(startStr);
		}
		String lengthStr = request.getParameter("length");
		int length = 10;
		if(NullUtil.isNotEmpty(lengthStr)){
			length = Integer.parseInt(lengthStr);
		}
		if(start != 0){
			start = start/length;
		}
		String searchVal = request.getParameter("search[value]");
		if(searchVal != null && "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = null;
		String sortOrder = "asc";
		String sortCol = request.getParameter("order[0][column]");
		if(!NullUtil.isEmpty(sortCol)){
			sortParam = request.getParameter("columns["+sortCol+"][name]");
			if(sortParam != null){
				sortOrder = request.getParameter("order[0][dir]");
			}
		}
		Page<LatestUpdates> updates = messageService.getUpadtes(start, length, sortParam, sortOrder, searchVal);
		
		updates.getContent().forEach(update->{update.setUpdateId(update.getId().toString());});
		/*
		List<LatestUpdates> lst=new ArrayList<LatestUpdates>();
		for(LatestUpdates update:updates) {
			update.setUpdateId(update.getId().toString());
			lst.add(update);
		}*/
		//updates.getContent().forEach((m)->m.setUpdateId(m.getId().toString()));
		Map<String, Object> userData = new HashMap<>();
		userData.put("data", updates.getContent());
		userData.put("recordsFiltered", updates.getTotalElements());
		userData.put("recordsTotal", updates.getTotalElements());
		userData.put("draw", request.getParameter("draw"));
		return userData;
	}
	
	
	/**
	 * To Save Message Details and return Same Message Details
	 * 
	 * @param message Message
	 * @return Message - Save the message & returns message
	 * @throws Exception
	 */
	@RequestMapping(value = "/message", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Message addMessage(@RequestBody Message message) throws Exception {
		final String method = "addMessage::addMessage::";
		logger.debug(CLASSNAME + method + BEGIN);
		Message msg =  messageService.addMessage(message);
		msg.setMsgId(msg.getId().toString());
		return msg;
	}
	
	/**
	 * To Update Message details based on Message Id and return Updated Message Details
	 * 
	 * @param message
	 * @param id
	 * @return Message - Update the Message based on Message Id
	 * @throws Exception
	 */
	@RequestMapping(value = "/message/{id}", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Message updateMessage(@RequestBody Message message, @PathVariable("id") String id) throws Exception {
		final String method = "updateMessage::updateMessage::";
		logger.debug(CLASSNAME + method + BEGIN);
		Message msg =  messageService.updateMessage(id, message);
		msg.setMsgId(msg.getId().toString());
		return msg;
	}

	/**
	 * To Navigate to Messages page in Admin tool
	 * 
	 * @param id
	 * @param fullname
	 * @param userType
	 * @param model
	 * @return String - admin/messages.jsp
	 * @throws Exception
	 */
	@RequestMapping(value = "/obtainmessages", method = RequestMethod.GET)
	public String obtainMessagesPage(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "usertype", required = true) String userType,@RequestParam(value = "msgtype", required = true) String msgtype, ModelMap model) throws Exception {
		final String method = "obtainMessagesPage::obtainMessagesPage::";
		logger.debug(CLASSNAME + method + BEGIN);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", userType);
		model.addAttribute("msgtype", msgtype);
		logger.debug(CLASSNAME + method + END);
		return "admin/messages";
	}
	
	@RequestMapping(value = "/obtainleads", method = RequestMethod.GET)
	public String obtainLeadsPage(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "usertype", required = true) String userType, ModelMap model) throws Exception {
		final String method = "obtainLeadsPage::obtainLeadsPage::";
		logger.debug(CLASSNAME + method + BEGIN);
		User usr = userService.findById(id);
		model.addAttribute("user", usr);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", userType);
		List<PartnerClient> clients = partnerClientRepository.findByClientidIsNotNullAndSubscriptionAmountIsNull();
		if(NullUtil.isNotEmpty(clients)) {
			for(PartnerClient client : clients) {
				SubscriptionDetails subscriptionData = subscriptionDetailsRepository.findByUserid(client.getClientid());
				if(NullUtil.isNotEmpty(subscriptionData) && NullUtil.isNotEmpty(subscriptionData.getPaidAmount())){
					client.setSubscriptionAmount(subscriptionData.getPaidAmount());
				}
				partnerService.createClient(client);
			}
		}
		List<String> list = Arrays.asList("GST API Sandbox","GST API Production","Eway Bill API Sandbox","Eway Bill API Production","Einvoice API Sandbox","Einvoice API Production","E-Invoice Software","Eway Bill Software","GST software","Suvidha Kendra","Tally Connector","SAP Connector","Oracle connector","MultiGSTN Tool","Reminders","Acknowledgements");
		model.addAttribute("productTypes", list);
		logger.debug(CLASSNAME + method + END);
		return "admin/leads";
	}
	
	
	/**
	 * To Navigate to latest Updates page in Admin tool
	 * 
	 * @param id
	 * @param fullname
	 * @param userType
	 * @param model
	 * @return String - admin/latestUpdatespage.jsp
	 * @throws Exception
	 */
	@RequestMapping(value = "/latest_updates", method = RequestMethod.GET)
	public String latestUpdatesPage(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "usertype", required = true) String userType, ModelMap model) throws Exception {
		final String method = "latestUpdatesPage::latestUpdatesPage::";
		logger.debug(CLASSNAME + method + BEGIN);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", userType);
		logger.debug(CLASSNAME + method + END);
		return "admin/latestUpdatespage";
	}
	
	/**
	 * To Save updates Details and return Same updates Details
	 * 
	 * @param updates updates
	 * @return updates - Save the updates & returns updates
	 * @throws Exception
	 */
	@RequestMapping(value = "/updates", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LatestUpdates addUpdate(@RequestBody LatestUpdates updates) throws Exception {
		final String method = "addUpdate::addUpdate::";
		LatestUpdates l_updates =  latestUpdatesService.addupdates(updates);
		l_updates.setUpdateId(l_updates.getId().toString());
		return l_updates;
	}
	
	/**
	 * To Update updates details based on updates Id and return Updated update Details
	 * 
	 * @param message
	 * @param id
	 * @return updates - Update the updates based on Message Id
	 * @throws Exception
	 */
	@RequestMapping(value = "/updates/{id}", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LatestUpdates updateUpdates(@RequestBody LatestUpdates update, @PathVariable("id") String id) throws Exception {
		final String method = "updateMessage::updateMessage::";
		logger.debug(CLASSNAME + method + BEGIN);
		LatestUpdates updates =  latestUpdatesService.updateUpdates(id, update);
		updates.setUpdateId(updates.getId().toString());
		return updates;
	}
	
	
	
	
	/**
	 * To Save news Details and return Same news Details
	 * 
	 * @param News news
	 * @return News - Save the News & returns News
	 * @throws Exception
	 */
	@RequestMapping(value = "/news", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LatestNews addnews(@RequestBody LatestNews news) throws Exception {
		final String method = "addNews::addNews::";
		LatestNews l_news =  latestNewsService.addnews(news);
		l_news.setNewsId(l_news.getId().toString());
		return l_news;
	}
	
	/**
	 * To Update news details based on news Id and return Updated news Details
	 * 
	 * @param news
	 * @param id
	 * @return news - Update the news based on news Id
	 * @throws Exception
	 */
	@RequestMapping(value = "/news/{id}", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LatestNews updateNews(@RequestBody LatestNews news, @PathVariable("id") String id) throws Exception {
		final String method = "updateNews::updateNews::";
		logger.debug(CLASSNAME + method + BEGIN);
		LatestNews unews =  latestNewsService.updateNews(id, news);
		unews.setNewsId(unews.getId().toString());
		return unews;
	}
	
	/**
	 * Handles for Getting the monthly Api's Usage totals of particular Year
	 * 
	 * @param year
	 * @param model
	 * @return Map<String, String>
	 * @throws Exception
	 */
	@RequestMapping(value = "/mdfymonthlyapiusage", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> getMonthlyApiUsage(
			@RequestParam(value = "year", required = true) int year, ModelMap model) throws Exception {
		final String method = "mdfymonthlyapiusage::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, String> reportMap = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, 0, 1, 0, 0, 0);
		Date startDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, 12, 0, 23, 59, 59);
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		reportMap = meteringService.getYearWiseMeteringData(startDate, endDate);
		logger.debug(CLASSNAME + method + END);
		return reportMap;
	}

	/**
	 * Handles for Getting the list of Sub Users of particular User based on id 
	 * 
	 * @param id
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/secondaryUsers/{id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> secondaryUsers(@PathVariable("id") String id) throws Exception {
		String method = "secondaryUsers :: ";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		List<User> secondaryUsers = Lists.newArrayList();
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(id, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					secondaryUsers.add(usr);
				}
			}
		}
		Map<String, Object> secondaryUsersData = new HashMap<>();
		secondaryUsersData.put("data", secondaryUsers);
		secondaryUsersData.put("recordsFiltered", secondaryUsers.size());
		secondaryUsersData.put("recordsTotal", secondaryUsers.size());
		secondaryUsersData.put("draw", 1);
		return secondaryUsersData;
	}
	/**
	 * Handles for Getting the list of Sub Users of particular User based on id 
	 * 
	 * @param id
	 * @return Map<String, Object>
	 * @throws Exception
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/secondaryCenters/{id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> secondaryCenters(@PathVariable("id") String id) throws Exception {
		String method = "secondaryUsers :: ";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		List<CompanyCenters> secondaryCenters = profileService.getCenters(id);
		Map<String, Object> secondaryUsersData = new HashMap<>();
		secondaryUsersData.put("data", secondaryCenters);
		secondaryUsersData.put("recordsFiltered", secondaryCenters.size());
		secondaryUsersData.put("recordsTotal", secondaryCenters.size());
		secondaryUsersData.put("draw", 1);
		return secondaryUsersData;
	}
	
	

	/**
	 * Handles for Getting the Api's Usage totals of particular User based on the Userid and Year
	 * 
	 * @param userId
	 * @param year
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mdfymonthlyinvoiceusage", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> getMonthlyApiUsageData(@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "year", required = true) int year, ModelMap model) throws Exception {
		final String method = "mdfymonthlyapiusage::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, String> summaryMap = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		cal.set(year, 0, 1, 0, 0, 0);
		Date startDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, 12, 0, 23, 59, 59);
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		summaryMap = meteringService.getUserWiseMeteringData(userId,startDate, endDate);
		model.addAttribute("summaryMap", summaryMap);
		logger.debug(CLASSNAME + method + END);
		return summaryMap;			
	}

	/**
	 * To navigate to MonthlyAPIUsage page in Admin tool
	 * 
	 * @param id
	 * @param fullname
	 * @param userType
	 * @param model
	 * @return String - admin/reports.jsp
	 * @throws Exception
	 */
	@RequestMapping(value = "/userreports", method = RequestMethod.GET)
	public String adminUserReports(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "usertype", required = true) String userType, ModelMap model) throws Exception {
		final String method = "adminUserReports::";
		logger.debug(CLASSNAME + method + BEGIN);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", userType);
		logger.debug(CLASSNAME + method + END);
		
		return "admin/reports";
	}

	@RequestMapping(value = "/monthlywiseusagereports", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> MonthlyWiseAPIUsageReport(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,@RequestParam(value = "usertype", required = true) String userType,
			@RequestParam(value="currentmonth", required=true) String currentmonth,
			@RequestParam(value="currentyear", required=true) String currentyear,ModelMap model) throws Exception {
		final String method = "expiredUsers::";
		logger.debug(CLASSNAME + method + BEGIN);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", userType);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy");
		
		Integer convertMonth=Integer.valueOf(currentmonth);
		
		String month = convertMonth < 10 ? "0" + currentmonth : currentmonth ;
		Date startdate=dateFormat.parse("01/"+month+"/"+currentyear);
		
		/*Date startdate=dateFormat.parse("01/12/2018");
		Date enddate=dateFormat.parse("31/12/2018");*/
		
		Date enddate=null;;
		if("02".equalsIgnoreCase(month)) {
			enddate=dateFormat.parse("28/"+month+"/"+currentyear);
		}else {
			enddate=dateFormat.parse("31/"+month+"/"+currentyear);
		}
		//List<Metering> meteringList=meteringService.getASPMonthlyWiseMeteringData(startdate, enddate);
		
		Map<String, Object> mapData = new HashMap<>();	
		 
		Map<String, String> meteringdata=null;
		List<User> usersList=userService.getASPUserDetailsByType(MasterGSTConstants.ASPDEVELOPER);
				
		User user=null;
		
		List<User> userMeteringData=new ArrayList<>();
		
		for(User usr :usersList) {
			meteringdata=meteringService.getUserWiseMeteringData(usr.getId().toString(), startdate, enddate);
			user=new User();
			user.setId(usr.getId());
			user.setFullname(usr.getFullname());
			user.setEmail(usr.getEmail());
			
			for (Map.Entry<String, String> entry : meteringdata.entrySet()) {
				
				switch(entry.getKey()) {
					case "ProductionTotal" : user.setGstAPIAllowedInvoices(Integer.parseInt(entry.getValue()));
				    break;
				   
					case "SandboxTotal" : user.setGstSanboxAllowedCountInvoices(Integer.parseInt(entry.getValue()));
					break;
					
					case "EwayBillProductionTotal" : user.setEwaybillAPIAllowedInvoices(Integer.parseInt(entry.getValue()));						
					break;
					
					case "EwayBillSandboxTotal" : user.setEwaybillSanboxAllowedInvoices(Integer.parseInt(entry.getValue()));
					break;
					
					case "EinvProductionTotal" : user.setEinvAPIAllowedInvoices(Integer.parseInt(entry.getValue()));						
					break;
					
					case "EinvSandboxTotal" : user.setEinvSanboxAllowedInvoices(Integer.parseInt(entry.getValue()));
					break;
				}
		    }			
			userMeteringData.add(user);
		}
				
		mapData.put("data", userMeteringData);
		mapData.put("recordsFiltered", userMeteringData.size());
		mapData.put("recordsTotal", userMeteringData.size());
		mapData.put("draw", 1);
		
		logger.debug(CLASSNAME + method + END);
		return mapData;
	}
	
	@RequestMapping(value = "/summaryreportdata/{reportype}/{reportperiod}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getSubscriptionSummaryYearlyReport(@PathVariable String reportype,@PathVariable String reportperiod) {
		
		int year;
		int month;
		List<User> allusers=null;
		if("Monthly".equalsIgnoreCase(reportype)) {
			String[] monthyear=reportperiod.split("-");
			month=Integer.parseInt(monthyear[0].trim());
			year=Integer.parseInt(monthyear[1].trim());
			allusers=userService.getByAllUsersgetByAllUsersMonthly(month,year);
		}else {
			year=Integer.parseInt(reportperiod.trim());
			allusers=userService.getByAllUsersYearly(year);
		}
		
		//System.out.println(usersCountMap);
		
		return getUsersSummaryReport(allusers);
	}
	
	public Map<String, Object> getUsersSummaryReport(List<User> allusers){
		List<User> users=allusers.stream().filter(usr->!"bvmcs@mastergst.com".equalsIgnoreCase(usr.getEmail())).collect(Collectors.toList());
		
		Map<String,Object> usersCountMap=new HashMap<String, Object>();
		List<User> signuptotal=users.stream().filter(user->!"Test Account".equalsIgnoreCase(user.getNeedToFollowUp()) && !MasterGSTConstants.PARTNER.equalsIgnoreCase(user.getType())).collect(Collectors.toList());
		usersCountMap.put("signuptotal",(long) signuptotal.size());
		
		List<String> userids=signuptotal.stream().map(user->user.getId().toString()).collect(Collectors.toList());
		Double  totalrevenue=subscriptionService.getByRevenueSubscriptionDataUsersidInAndNotInDemo(userids);
		//getByRevenueSubscriptionDataUsersidIn(userids);
		usersCountMap.put("totalrevenue",totalrevenue);
		
		List<User> causers=users.stream().filter(user->MasterGSTConstants.CAS.equalsIgnoreCase(user.getType()) && !"Test Account".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<String> causerids=causers.stream().map(user->user.getId().toString()).collect(Collectors.toList());
		Double  carevenue=subscriptionService.getByRevenueSubscriptionDataUsersidIn(causerids);
		usersCountMap.put("carevenue",carevenue);
		List<User> enterpriseusers=users.stream().filter(user->MasterGSTConstants.ENTERPRISE.equalsIgnoreCase(user.getType()) && !"Test Account".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<String> enterpriseuserids=enterpriseusers.stream().map(user->user.getId().toString()).collect(Collectors.toList());
		Double  enterpriserevenue=subscriptionService.getByRevenueSubscriptionDataUsersidIn(enterpriseuserids);
		usersCountMap.put("enterpriserevenue",enterpriserevenue);
		List<User> businessusers=users.stream().filter(user->MasterGSTConstants.BUSINESS.equalsIgnoreCase(user.getType()) && !"Test Account".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<String> businessuserids=businessusers.stream().map(user->user.getId().toString()).collect(Collectors.toList());
		Double  businessrevenue=subscriptionService.getByRevenueSubscriptionDataUsersidIn(businessuserids);
		usersCountMap.put("businessrevenue",businessrevenue);
		
		Double webapprevenu=carevenue+enterpriserevenue+businessrevenue;
		usersCountMap.put("webapprevenu",webapprevenu);
		
		List<User> suvidhakendrausers=users.stream().filter(user->MasterGSTConstants.SUVIDHA_CENTERS.equalsIgnoreCase(user.getType()) && !"Test Account".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<String> suvidhakendrauserids=suvidhakendrausers.stream().map(user->user.getId().toString()).collect(Collectors.toList());
		Double  suvidhakendrarevenue=subscriptionService.getByRevenueSubscriptionDataUsersidIn(suvidhakendrauserids);
		usersCountMap.put("suvidhakendrarevenue",suvidhakendrarevenue);
		List<User> aspusers=users.stream().filter(user->MasterGSTConstants.ASPDEVELOPER.equalsIgnoreCase(user.getType()) && !"Test Account".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		
		List<String> aspuserids=aspusers.stream().map(user->user.getId().toString()).collect(Collectors.toList());
		
		usersCountMap.put("causers",(long) causers.size());
		usersCountMap.put("enterpriseusers",(long) enterpriseusers.size());
		usersCountMap.put("businessusers",(long) businessusers.size());
		Long webappusercount=(Long)usersCountMap.get("causers")+(Long)usersCountMap.get("enterpriseusers")+(Long)usersCountMap.get("businessusers");
		usersCountMap.put("webappusers",webappusercount);
		usersCountMap.put("suvidhakendrausers",(long) suvidhakendrausers.size());
		usersCountMap.put("aspusers",(long) aspusers.size());
		
		List<User> ca_notreqiured=users.stream().filter(user->MasterGSTConstants.CAS.equalsIgnoreCase(user.getType()) && "Not Required".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<User> enterprise_notreqiured=users.stream().filter(user->MasterGSTConstants.ENTERPRISE.equalsIgnoreCase(user.getType()) && "Not Required".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<User> business_notreqiured=users.stream().filter(user->MasterGSTConstants.BUSINESS.equalsIgnoreCase(user.getType()) && "Not Required".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<User> suvidhakendra_notreqiured=users.stream().filter(user->MasterGSTConstants.SUVIDHA_CENTERS.equalsIgnoreCase(user.getType()) && "Not Required".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<User> asp_notreqiured=users.stream().filter(user->MasterGSTConstants.ASPDEVELOPER.equalsIgnoreCase(user.getType()) && "Not Required".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());

		usersCountMap.put("ca_notreqiured",(long) ca_notreqiured.size());
		usersCountMap.put("enterprise_notreqiured",(long) enterprise_notreqiured.size());
		usersCountMap.put("business_notreqiured",(long) business_notreqiured.size());
		Long webapp_notreqiured=(Long)usersCountMap.get("ca_notreqiured")+(Long)usersCountMap.get("enterprise_notreqiured")+(Long)usersCountMap.get("business_notreqiured");
		usersCountMap.put("webapp_notreqiured",webapp_notreqiured);
		usersCountMap.put("suvidhakendra_notreqiured",(long) suvidhakendra_notreqiured.size());
		usersCountMap.put("asp_notreqiured",(long) asp_notreqiured.size());
		List<User> notreqiuredtotal=users.stream().filter(user->"Not Required".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		usersCountMap.put("notreqiuredtotal",(long) notreqiuredtotal.size());
		
		List<User> ca_readytogo=users.stream().filter(user->MasterGSTConstants.CAS.equalsIgnoreCase(user.getType()) && "Ready to Go".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<User> enterprise_readytogo=users.stream().filter(user->MasterGSTConstants.ENTERPRISE.equalsIgnoreCase(user.getType()) && "Ready to Go".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<User> business_readytogo=users.stream().filter(user->MasterGSTConstants.BUSINESS.equalsIgnoreCase(user.getType()) && "Ready to Go".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<User> suvidhakendra_readytogo=users.stream().filter(user->MasterGSTConstants.SUVIDHA_CENTERS.equalsIgnoreCase(user.getType()) && "Ready to Go".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<User> asp_readytogo=users.stream().filter(user->MasterGSTConstants.ASPDEVELOPER.equalsIgnoreCase(user.getType()) && "Ready to Go".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		
		usersCountMap.put("ca_readytogo",(long) ca_readytogo.size());
		usersCountMap.put("enterprise_readytogo",(long) enterprise_readytogo.size());
		usersCountMap.put("business_readytogo",(long) business_readytogo.size());
		Long webapp_readytogo=(Long)usersCountMap.get("ca_readytogo")+(Long)usersCountMap.get("enterprise_readytogo")+(Long)usersCountMap.get("business_readytogo");
		usersCountMap.put("webapp_readytogo",webapp_readytogo);
		usersCountMap.put("suvidhakendra_readytogo",(long) suvidhakendra_readytogo.size());
		usersCountMap.put("asp_readytogo",(long) asp_readytogo.size());
		List<User> readytogototal=users.stream().filter(user->"Ready to Go".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		usersCountMap.put("readytogototal",(long) readytogototal.size());
		
		List<User> ca_paid=users.stream().filter(user->MasterGSTConstants.CAS.equalsIgnoreCase(user.getType()) && "Closed".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<User> enterprise_paid=users.stream().filter(user->MasterGSTConstants.ENTERPRISE.equalsIgnoreCase(user.getType()) && "Closed".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<User> business_paid=users.stream().filter(user->MasterGSTConstants.BUSINESS.equalsIgnoreCase(user.getType()) && "Closed".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<User> suvidhakendra_paid=users.stream().filter(user->MasterGSTConstants.SUVIDHA_CENTERS.equalsIgnoreCase(user.getType()) && "Closed".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		List<User> asp_paid=users.stream().filter(user->MasterGSTConstants.ASPDEVELOPER.equalsIgnoreCase(user.getType()) && "Closed".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		
		usersCountMap.put("ca_paid",(long) ca_paid.size());
		usersCountMap.put("enterprise_paid",(long) enterprise_paid.size());
		usersCountMap.put("business_paid",(long) business_paid.size());
		Long webapp_paid=(Long)usersCountMap.get("ca_paid")+(Long)usersCountMap.get("enterprise_paid")+(Long)usersCountMap.get("business_paid");
		usersCountMap.put("webapp_paid",webapp_paid);
		usersCountMap.put("suvidhakendra_paid",(long) suvidhakendra_paid.size());
		usersCountMap.put("asp_paid",(long) asp_paid.size());
		List<User> paidtotal=users.stream().filter(user->"Closed".equalsIgnoreCase(user.getNeedToFollowUp())).collect(Collectors.toList());
		usersCountMap.put("paidtotal",(long) paidtotal.size());
		
		List<SubscriptionDetails> aspsubscriptiondata= subscriptionService.getASPUsersidsIn(aspuserids);
		//aspsubscriptiondata.forEach(data->{System.out.println(data.getApiType());});
		List<SubscriptionDetails> gstsandboxusers=aspsubscriptiondata.stream().filter(subdata->MasterGSTConstants.GSTSANDBOX_API.equalsIgnoreCase(subdata.getApiType())).collect(Collectors.toList());
		List<SubscriptionDetails> gstapiusers=aspsubscriptiondata.stream().filter(subdata->MasterGSTConstants.GST_API.equalsIgnoreCase(subdata.getApiType())).collect(Collectors.toList());
		List<SubscriptionDetails> ewaybillsandboxusers=aspsubscriptiondata.stream().filter(subdata->MasterGSTConstants.EWAYBILLSANDBOX_API.equalsIgnoreCase(subdata.getApiType())).collect(Collectors.toList());
		List<SubscriptionDetails> ewaybillapiusers=aspsubscriptiondata.stream().filter(subdata->MasterGSTConstants.EWAYBILL_API.equalsIgnoreCase(subdata.getApiType())).collect(Collectors.toList());
		List<SubscriptionDetails> einvoicesandboxusers=aspsubscriptiondata.stream().filter(subdata->"E-INVOICESANDBOXAPI".equalsIgnoreCase(subdata.getApiType())).collect(Collectors.toList());
		List<SubscriptionDetails> einvoiceapiusers=aspsubscriptiondata.stream().filter(subdata->"E-INVOICEAPI".equalsIgnoreCase(subdata.getApiType())).collect(Collectors.toList());
		usersCountMap.put("aspsubscriptions",(long) aspsubscriptiondata.size());
		usersCountMap.put("gstsandboxusers",(long) gstsandboxusers.size());
		usersCountMap.put("gstapiusers",(long) gstapiusers.size());
		usersCountMap.put("ewaybillsandboxusers",(long) ewaybillsandboxusers.size());
		usersCountMap.put("ewaybillapiusers",(long) ewaybillapiusers.size());
		usersCountMap.put("einvoicesandboxusers",(long) einvoicesandboxusers.size());
		usersCountMap.put("einvoiceapiusers",(long) einvoiceapiusers.size());
		
		/*
		 * apis users payments is done subscriptiondetails table new entry available,
		 * that purpose api users count and paid users are same
		 */
		usersCountMap.put("gstapi_paid",(long) gstapiusers.size());
		usersCountMap.put("ewaybillapi_paid",(long) ewaybillapiusers.size());
		usersCountMap.put("einvoiceapi_paid",(long) einvoiceapiusers.size());
		List<SubscriptionDetails> gstsandbox_paid=aspsubscriptiondata.stream().filter(subdata->MasterGSTConstants.GSTSANDBOX_API.equalsIgnoreCase(subdata.getApiType()) && "UNLIMITED".equalsIgnoreCase(subdata.getSubscriptionType())).collect(Collectors.toList());
		List<SubscriptionDetails> ewaybillsandbox_paid=aspsubscriptiondata.stream().filter(subdata->MasterGSTConstants.EWAYBILLSANDBOX_API.equalsIgnoreCase(subdata.getApiType()) && "UNLIMITED".equalsIgnoreCase(subdata.getSubscriptionType())).collect(Collectors.toList());
		List<SubscriptionDetails> einvoicesandbox_paid=aspsubscriptiondata.stream().filter(subdata->"E-INVOICESANDBOXAPI".equalsIgnoreCase(subdata.getApiType()) && "UNLIMITED".equalsIgnoreCase(subdata.getSubscriptionType())).collect(Collectors.toList());
		usersCountMap.put("gstsandbox_paid",(long) gstsandbox_paid.size());
		usersCountMap.put("ewaybillsandbox_paid",(long) ewaybillsandbox_paid.size());
		usersCountMap.put("einvoicesandbox_paid",(long) einvoicesandbox_paid.size());
		
		List<String> findgstapiuserids=gstapiusers.stream().map(gstapi->gstapi.getUserid()).collect(Collectors.toList());
		List<String> findewaybilluserids=ewaybillapiusers.stream().map(gstapi->gstapi.getUserid()).collect(Collectors.toList());
		List<String> findeinvoiceuserids=einvoiceapiusers.stream().map(gstapi->gstapi.getUserid()).collect(Collectors.toList());
		
		List<String> sansboxpaiduserids=gstsandbox_paid.stream().map(data->data.getUserid()).collect(Collectors.toList());
		List<String> ewaybillsansboxpaiduserids=ewaybillsandbox_paid.stream().map(data->data.getUserid()).collect(Collectors.toList());
		List<String> einvoicesansboxpaiduserids=einvoicesandbox_paid.stream().map(data->data.getUserid()).collect(Collectors.toList());
		Double gstapisandbox_revenue=subscriptionService.getByRevenueSubscriptionDataUsersidInAndApitype(sansboxpaiduserids,MasterGSTConstants.GSTSANDBOX_API);
		Double gstapi_revenue=subscriptionService.getByRevenueSubscriptionDataUsersidInAndApitype(findgstapiuserids,MasterGSTConstants.GST_API);
		Double ewaybillsandbox_revenue=subscriptionService.getByRevenueSubscriptionDataUsersidInAndApitype(ewaybillsansboxpaiduserids,MasterGSTConstants.EWAYBILLSANDBOX_API);
		Double ewaybillapi_revenue=subscriptionService.getByRevenueSubscriptionDataUsersidInAndApitype(findewaybilluserids,MasterGSTConstants.EWAYBILL_API);
		Double einvoicesandbox_revenue=subscriptionService.getByRevenueSubscriptionDataUsersidInAndApitype(einvoicesansboxpaiduserids,"E-INVOICESANDBOXAPI");
		Double einvoiceapi_revenue=subscriptionService.getByRevenueSubscriptionDataUsersidInAndApitype(findeinvoiceuserids,"E-INVOICEAPI");
		usersCountMap.put("gstapisandbox_revenue",gstapisandbox_revenue);
		usersCountMap.put("ewaybillsandbox_revenue",ewaybillsandbox_revenue);
		usersCountMap.put("gstapi_revenue",gstapi_revenue);
		usersCountMap.put("ewaybillapi_revenue",ewaybillapi_revenue);
		usersCountMap.put("einvoicesandbox_revenue",einvoicesandbox_revenue);
		usersCountMap.put("einvoiceapi_revenue",einvoiceapi_revenue);

		Double  asprevenue=subscriptionService.getByRevenueSubscriptionDataUsersidInAndNotInDemo(aspuserids);
		usersCountMap.put("asprevenue",asprevenue);
		
		List<SubscriptionDetails> gstsandbox_readytogo=gstsandboxusers.stream().filter(data->"Demo".equalsIgnoreCase(data.getSubscriptionType())).collect(Collectors.toList());
		List<SubscriptionDetails> ewaybillsandbox_readytogo=ewaybillsandboxusers.stream().filter(data->"Demo".equalsIgnoreCase(data.getSubscriptionType())).collect(Collectors.toList());
		List<SubscriptionDetails> einvoicesandbox_readytogo=einvoicesandboxusers.stream().filter(data->"Demo".equalsIgnoreCase(data.getSubscriptionType())).collect(Collectors.toList());
		usersCountMap.put("gstsandbox_readytogo",(long) gstsandbox_readytogo.size());
		usersCountMap.put("ewaybillsandbox_readytogo",(long) ewaybillsandbox_readytogo.size());
		usersCountMap.put("einvoicesandbox_readytogo",(long) einvoicesandbox_readytogo.size());
		
		usersCountMap.put("gstapi_readytogo",0l);
		usersCountMap.put("ewaybillapi_readytogo",0l);
		usersCountMap.put("einvoiceapi_readytogo",0l);
		
		return usersCountMap;
	}
	
	@RequestMapping(value = "/summaryexcelreport/{reportype}/{reportperiod}", method = RequestMethod.GET,produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource downloadExcelData(@PathVariable String reportype,
			@PathVariable String reportperiod, HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		int year;
		int month;
		List<User> allusers=null;
		if("Monthly".equalsIgnoreCase(reportype)) {
			String[] monthyear=reportperiod.split("-");
			month=Integer.parseInt(monthyear[0].trim());
			year=Integer.parseInt(monthyear[1].trim());
			allusers=userService.getByAllUsersgetByAllUsersMonthly(month,year);
		}else {
			year=Integer.parseInt(reportperiod.trim());
			allusers=userService.getByAllUsersYearly(year);
		}
		
		response.setHeader("Content-Disposition", "inline; filename='Users_Summary_Report.xls");
		List<AdminSummary> exceldata=new ArrayList<AdminSummary>();
		Map<String, Object> usermap=getUsersSummaryReport(allusers);
		
		exceldata.add(new AdminSummary("API", "", usermap.get("aspusers")+"(asp users subscriptions"+usermap.get("aspsubscriptions")+")", usermap.get("asp_notreqiured"), 0, usermap.get("asp_readytogo"), usermap.get("asp_paid"), "",usermap.get("asprevenue")));
		exceldata.add(new AdminSummary("", "GST API Sandbox", usermap.get("gstsandboxusers"), usermap.get("gstsanbox_notreqiured"), "", usermap.get("gstsandbox_readytogo"), usermap.get("gstsandbox_paid"), usermap.get("gstapisandbox_revenue"),""));
		exceldata.add(new AdminSummary("", "GST API Production", usermap.get("gstapiusers"), usermap.get("gstapi_notreqiured"), "", usermap.get("gstapi_readytogo"), usermap.get("gstapi_paid"), usermap.get("gstapi_revenue"),""));
		exceldata.add(new AdminSummary("", "Eway Bill Sandbox", usermap.get("ewaybillsandboxusers"), usermap.get("ewaybillsandbox_notreqiured"), "", usermap.get("ewaybillsandbox_readytogo"), usermap.get("ewaybillsandbox_paid"), usermap.get("ewaybillsandbox_revenue"),""));
		exceldata.add(new AdminSummary("", "Eway Bill Production", usermap.get("ewaybillapiusers"), usermap.get("ewaybill_notreqiured"), "", usermap.get("ewaybillapi_readytogo"), usermap.get("ewaybillapi_paid"), usermap.get("ewaybillapi_revenue"),""));
		exceldata.add(new AdminSummary("", "Einvoice Sandbox", usermap.get("einvoicesandboxusers"), usermap.get("einvoicesandbox_notreqiured"), "", usermap.get("einvoicesandbox_readytogo"), usermap.get("einvoicesandbox_paid"), usermap.get("einvoicesandbox_revenue"),""));
		exceldata.add(new AdminSummary("", "Einvoice Production", usermap.get("einvoiceapiusers"), usermap.get("einvoice_notreqiured"), "", usermap.get("einvoiceapi_readytogo"), usermap.get("einvoiceapi_paid"), usermap.get("einvoiceapi_revenue"),""));
		
		exceldata.add(new AdminSummary("Web App", "", usermap.get("webappusers"), usermap.get("webapp_notreqiured"), 0, usermap.get("webapp_readytogo"), usermap.get("webapp_paid"), "",usermap.get("webapprevenu")));
		exceldata.add(new AdminSummary("", "CA", usermap.get("causers"), usermap.get("ca_notreqiured"), 0, usermap.get("ca_readytogo"), usermap.get("ca_paid"), usermap.get("carevenue"),""));
		exceldata.add(new AdminSummary("", "Small Medium Business", usermap.get("businessusers"), usermap.get("business_notreqiured"), 0, usermap.get("business_readytogo"), usermap.get("business_paid"), usermap.get("businessrevenue"),""));
		exceldata.add(new AdminSummary("", "Enterprises", usermap.get("enterpriseusers"), usermap.get("enterprise_notreqiured"), 0, usermap.get("enterprise_readytogo"), usermap.get("enterprise_paid"), usermap.get("enterpriserevenue"),""));
		
		exceldata.add(new AdminSummary("Suvidha Kendra", "", usermap.get("suvidhakendrausers"), usermap.get("suvidhakendra_notreqiured"), 0, usermap.get("suvidhakendra_readytogo"), usermap.get("suvidhakendra_paid"), "",usermap.get("suvidhakendrarevenue")));
		exceldata.add(new AdminSummary("", "Suvidha Kendras", usermap.get("suvidhakendrausers"), usermap.get("suvidhakendra_notreqiured"), 0, usermap.get("suvidhakendra_readytogo"), usermap.get("suvidhakendra_paid"), usermap.get("suvidhakendrarevenue"),""));
		
		exceldata.add(new AdminSummary("", "Total Users", usermap.get("signuptotal"), usermap.get("notreqiuredtotal"), 0, usermap.get("readytogototal"), usermap.get("paidtotal"),"", usermap.get("totalrevenue")));
		
		File file = new File("Users_Summary_Report.xls");
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers = null;
			headers = Arrays.asList("","","Signup","Not required","Demo Completed","Ready to Go","Paid","Revenue","Total Revenue");			
			SimpleExporter exporter = new SimpleExporter();
			exporter.gridExport(headers, exceldata,"app,usertype,singup,notrequired,democompleted,readytogo,paid,revenue,totalRevenue",fos);
				
			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "downloadExcelData : ERROR", e);
		}
		return new FileSystemResource(new File("Users_Summary_Report.xls"));
	}
	
	@RequestMapping(value = "/deletespamusers/{otpunverifieduserid}", method = RequestMethod.GET)
	public void deleteAllSpamUsers(@PathVariable String otpunverifieduserid) throws Exception {

		User usr=userService.findById(otpunverifieduserid);
			if(NullUtil.isNotEmpty(usr)) {
				SubscriptionDetails subscriptiondetails=subscriptionService.getSubscriptionDataByDelete(usr.getId().toString());
				if(NullUtil.isNotEmpty(subscriptiondetails)) {
					subscriptionService.deletesubscriptiondata(subscriptiondetails.getId().toString());
				}
				userService.deleteOtpunverifiedusers(usr.getId().toString());
			}
	}
	
	@RequestMapping(value = "/srchpartneremail", method = RequestMethod.GET)
	public @ResponseBody List<User> partnerClientData(ModelMap model,
			@RequestParam(value = "query", required = true) String query) throws Exception {
		final String method = "partnerClientData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + query);
					
		return userService.partnerClientLinkedUsers(query);
	}
	
	@RequestMapping(value = "/updateusrotpverify/{usrid}", method = RequestMethod.GET)
	public @ResponseBody void updateUserOtpverfiryDetails(@PathVariable String usrid, ModelMap model) throws Exception {
		final String method = "updateUserOtpverfiryDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		if(NullUtil.isNotEmpty(usrid)) {
			User dbUser = userService.findById(usrid);
			if(NullUtil.isNotEmpty(dbUser)) {
				dbUser.setOtpVerified("true");
				userService.updateUser(dbUser);
			}
		}
	}
	
	@RequestMapping(value = "/getupdates", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAllUpdatesData() throws Exception {
		Map<String, Object> mapData=new HashMap<String,Object>();
		
		List<LatestUpdates> updateddata=latestUpdatesService.getAllUpdates();
		updateddata.forEach(updates->updates.setUpdateId(updates.getId().toString()));
		mapData.put("data", updateddata);
		mapData.put("recordsFiltered", updateddata.size());
		mapData.put("recordsTotal", updateddata.size());
		mapData.put("draw", 1);
		return mapData;
	}
	
	@RequestMapping(value = "/getnews", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAllNewsData() throws Exception {
		Map<String, Object> mapData=new HashMap<String,Object>();
		
		List<LatestNews> updateddata=latestNewsService.getAllNews();
		updateddata.forEach(news->news.setNewsId(news.getId().toString()));
		mapData.put("data", updateddata);
		mapData.put("recordsFiltered", updateddata.size());
		mapData.put("recordsTotal", updateddata.size());
		mapData.put("draw", 1);
		return mapData;
	}
	
	@RequestMapping(value = "/referenceclients/{userid}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getPartnerReferenceClients(@PathVariable String userid, HttpServletRequest request, ModelMap model) throws Exception{
		final String method = "getPartnerReferenceClients::";
		logger.debug(CLASSNAME + method + BEGIN);
	
		/*
		List<PartnerClient> partnerClients=partnerService.getPartnerReferenceClients(userid);
		
		Map<String, Object> mapData=new HashMap<String,Object>();
		
		mapData.put("data", partnerClients);
		mapData.put("recordsFiltered", partnerClients.size());
		mapData.put("recordsTotal", partnerClients.size());
		mapData.put("draw", 1);
		*/
		
		String startStr = request.getParameter("start");
		int start = 0;
		if(NullUtil.isNotEmpty(startStr)){
			start = Integer.parseInt(startStr);
		}
		String lengthStr = request.getParameter("length");
		int length = 10;
		if(NullUtil.isNotEmpty(lengthStr)){
			length = Integer.parseInt(lengthStr);
		}
		if(start != 0){
			start = start/length;
		}
		String searchVal = request.getParameter("search[value]");
		if(searchVal != null && "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = "createdDate";
		String sortOrder = "asc";
		sortOrder = request.getParameter("order[0][dir]");
		
		Page<PartnerClientInfo> partnerClients = partnerService.getPartnerReferenceClients(userid, start, length, sortParam, sortOrder, searchVal);
		
		partnerClients.getContent().forEach(client -> {
			Double payment = 0d;
			if(isNotEmpty(client.getSubscription())) {
				for(SubscriptionDetails subscription : client.getSubscription()) {
					payment += subscription.getPaidAmount();
				}
			}
			client.setSubscriptionAmount(payment);
		});
		logger.debug(CLASSNAME + method + END);
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer = mapper.writer();
		return writer.writeValueAsString(partnerClients);
	}
	
	@RequestMapping(value = "/summaryusercountdata/{reportype}/{reportperiod}", method = RequestMethod.GET)
	public @ResponseBody Map<String, List<User>> getUsersCountSummaryReport(@PathVariable String reportype,@PathVariable String reportperiod){
		
		
		int year;
		int month;
		List<User> allusers=null;
		if("Monthly".equalsIgnoreCase(reportype)) {
			String[] monthyear=reportperiod.split("-");
			month=Integer.parseInt(monthyear[0].trim());
			year=Integer.parseInt(monthyear[1].trim());
			allusers=userService.getAllusersInMonthly(month,year);
		}else {
			year=Integer.parseInt(reportperiod.trim());
			allusers=userService.getAllusersInYearly(year);
		}
		
		//List<User> allUsers=userService.getAllusers();
		
		Map<String, List<User>> users=allusers.stream().collect(Collectors.groupingBy(User::getType));
	
		return users;
	}
	
	@RequestMapping(value = "/getheaderkeys/{usrid}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getHeaderKeys(@PathVariable String usrid, ModelMap model) throws Exception {
		final String method = "getHeaderKeys::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		List<HeaderKeys> headerkeys = userService.getHeaderkeys(usrid);
		headerkeys.stream().forEach(headers->headers.setHeaderid(headers.getId().toString()));
		Map<String, Object> headerkeysData = new HashMap<>();
		headerkeysData.put("data", headerkeys);
		headerkeysData.put("recordsFiltered", headerkeys.size());
		headerkeysData.put("recordsTotal", headerkeys.size());
		headerkeysData.put("draw", 1);
		return headerkeysData;
	}
	
	@RequestMapping(value = "/deleteheaderkeys/{headerid}/{userid}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> deleteHeaderKeys(@PathVariable String headerid, @PathVariable String userid, ModelMap model) throws Exception {
		final String method = "deleteHeaderKeys::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		List<HeaderKeys> headerkeys = userService.deleteHeaderkeys(headerid,userid);
		headerkeys.stream().forEach(headers->headers.setHeaderid(headers.getId().toString()));
		Map<String, Object> headerkeysData = new HashMap<>();
		headerkeysData.put("data", headerkeys);
		headerkeysData.put("recordsFiltered", headerkeys.size());
		headerkeysData.put("recordsTotal", headerkeys.size());
		headerkeysData.put("draw", 1);
		return headerkeysData;
	}
	
	@RequestMapping(value = "/getasp_apiuserdetails/{userid}", method = RequestMethod.GET)
	public @ResponseBody AspUserDetails getAspUserDetails(@PathVariable String userid) {
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-YYYY");
		
		AspUserDetails aspUserDetails=(AspUserDetails) aspUserDetailsService.getAspUserDetails(userid);
				
		if(NullUtil.isNotEmpty(aspUserDetails) && NullUtil.isNotEmpty(aspUserDetails.getCreatedDate())) {
			aspUserDetails.setAddressProof(sdf.format(aspUserDetails.getCreatedDate()));
		}
		
		return aspUserDetails;
	}
	
	@RequestMapping(value = "/leadclients/{month}/{year}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getLeadClients(@PathVariable("month") int month, @PathVariable("year") int year,ModelMap model, HttpServletRequest request) throws Exception{
		
		final String method = "getLeadClients::";
		logger.debug(CLASSNAME + method + BEGIN);
		String tabName = request.getParameter("tabName");
		String reporttype = request.getParameter("reportType");
		PartnerFilter partnerFilter = new PartnerFilter();
		partnerFilter.setPartnerType(request.getParameter("partnerType"));
		partnerFilter.setPartnername(request.getParameter("partnerName"));
		
		String startStr = request.getParameter("start");
		int start = 0;
		if(NullUtil.isNotEmpty(startStr)){
			start = Integer.parseInt(startStr);
		}
		String lengthStr = request.getParameter("length");
		int length = 10;
		if(NullUtil.isNotEmpty(lengthStr)){
			length = Integer.parseInt(lengthStr);
		}
		if(start != 0){
			start = start/length;
		}
		String searchVal = request.getParameter("search[value]");
		if(searchVal != null && "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = "createdDate";
		String sortOrder = "asc";
		sortOrder = request.getParameter("order[0][dir]");
		Map<String, Object> mapData=new HashMap<String,Object>();
		Page<PartnerClient> pageUsers = null;
		if("salesTab".equalsIgnoreCase(tabName) || "partnersTab".equalsIgnoreCase(tabName)) {
			List<String> userids = Lists.newArrayList();
			Page<User> saleUsers = partnerService.getPartners(tabName,partnerFilter);
			List<User> saleclnts=saleUsers.getContent();
			for(User user : saleclnts) {
				if (!userids.contains(user.getId().toString())) { 
					userids.add(user.getId().toString());
				}
			}
			pageUsers = partnerService.getSalesTeamPartners(tabName,userids, month,year,start, length, sortParam, sortOrder, searchVal,partnerFilter);
			List<PartnerClient> saleprtnrclnts=pageUsers.getContent();
			for(PartnerClient clnts : saleprtnrclnts) {
				clnts.setCreatedBy(clnts.getId().toString());
			}
			mapData.put("data", saleprtnrclnts);
		}else {
			List<String> userids = Lists.newArrayList();
			Page<User> saleUsers = partnerService.getPartners(tabName,partnerFilter);
			List<User> saleclnts=saleUsers.getContent();
			for(User user : saleclnts) {
				if (!userids.contains(user.getId().toString())) { 
					userids.add(user.getId().toString());
				}
			}
			 pageUsers = partnerService.getPartnerClients(month,Utility.getYearCode(month, year),start, length, sortParam, sortOrder, searchVal,partnerFilter,userids);
			List<PartnerClient> prtnrclnts=pageUsers.getContent();
			for(PartnerClient clnts : prtnrclnts) {
				clnts.setCreatedBy(clnts.getId().toString());
			}
			mapData.put("data", prtnrclnts);
		}
		mapData.put("recordsFiltered", pageUsers.getTotalElements());
		mapData.put("recordsTotal", pageUsers.getTotalElements());
		mapData.put("draw", request.getParameter("draw"));
		
		logger.debug(CLASSNAME + method + END);
		return mapData;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping("/updateParentClientInfo")
	public @ResponseBody String partnerClientsUpdate() {
		
		final String method = "partnerClientsUpdate::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		List<PartnerClient> pcLst=partnerService.getAllPartnerClients();
		
		List<PartnerClient> filteredLst=new ArrayList<PartnerClient>();
		
		for(PartnerClient prtnrclient: pcLst) {
			
			String puserid=prtnrclient.getUserid();
			if(isNotEmpty(puserid)) {
				User partner=userService.getUserById(puserid);
				
				if(isNotEmpty(partner)) {
					if(isNotEmpty(partner.getEmail())) {
						prtnrclient.setPartneremail(partner.getEmail());						
					}
					if(isNotEmpty(partner.getMobilenumber())) {
						prtnrclient.setPartnermobileno(partner.getMobilenumber());											
					}
					if(isNotEmpty(partner.getFullname())) {
						prtnrclient.setPartnername(partner.getFullname());						
					}
					if(isNotEmpty(partner.getUserSequenceid())) {
						prtnrclient.setRefid(partner.getUserSequenceid()+"");						
					}else {
						prtnrclient.setRefid("");
					}
				}
				
			}
			String cuserid=prtnrclient.getClientid();
			
			if(isNotEmpty(cuserid)) {
				User refUser=userService.findById(cuserid);
				if(isNotEmpty(refUser) && isNotEmpty(refUser.getType())) {
					prtnrclient.setClienttype(refUser.getType());					
				}
			}
			
			filteredLst.add(prtnrclient);
		}
		
		if(isNotEmpty(filteredLst)) {
			partnerService.updatePartnerClientInfo(filteredLst);
		}
		
		logger.debug(CLASSNAME + method + END);
		return "partner-clients data update successfully...!";
	}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping("/updateUsers")
	public @ResponseBody String usersUpdate() {
		
		final String method = "usersUpdate::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		List<User> filteredLst=new ArrayList<User>();
		List<User> usersLst=userService.getAllUsers();
		
		
		List<PartnerClient> pcLst=partnerService.getAllPartnerClients();

		Map<String,PartnerClient> partnerClntMap=new HashMap<>();
		for(PartnerClient prtnrclnt: pcLst) {
			
			
			if(isNotEmpty(prtnrclnt) && isNotEmpty(prtnrclnt.getClientid())) {
				
				partnerClntMap.put(prtnrclnt.getClientid(), prtnrclnt);
			}
		}
		
		for(User usr:usersLst) {
			if(partnerClntMap.containsKey(usr.getId().toString())) {
				PartnerClient partner=partnerClntMap.get(usr.getId().toString());
				
				if(isNotEmpty(partner)) {
					if(isNotEmpty(partner.getRefid())) {
						usr.setRefid(partner.getRefid());
					}else {
						usr.setRefid("");
					}
				}else {
					usr.setRefid("");
				}
			}else {
				usr.setRefid("");
			}
			filteredLst.add(usr);
		}
		
		userService.saveRefisUsers(filteredLst);
		logger.debug(CLASSNAME + method + END);
		return "user data update successfully...!";
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping("/prinvoices")
	public @ResponseBody String purchaseinvoices() {
		List<PurchaseRegister> updatedInvoices = Lists.newArrayList();
		
		List<PurchaseRegister> prInvoices = purchaseRepository.findByMatchingStatus(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
		if(isNotEmpty(prInvoices) && prInvoices.size()>0) {
			for(PurchaseRegister invoice: prInvoices) {
				invoice.setMannualMatchInvoices("Single");
				updatedInvoices.add(invoice);
			}
			purchaseRepository.save(updatedInvoices);
		}
		
		
		return "Purchase Invoices data update successfully...!";
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping("/gstr2ainvoices")
	public @ResponseBody String gstr2invoices() {
		List<GSTR2> updatedInvoices = Lists.newArrayList();
		
		List<GSTR2> prInvoices = gstr2Repository.findByMatchingStatus(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
		if(isNotEmpty(prInvoices) && prInvoices.size()>0) {
			for(GSTR2 invoice: prInvoices) {
				if(isNotEmpty(invoice.getMatchingId()) && (invoice.getMatchingId().equals("5e030231b647b3291344aa31") || invoice.getMatchingId().equals("5e05b5fdb647b374907262b5"))) {
					invoice.setMannualMatchInvoices("multiple");
				}else {
					invoice.setMannualMatchInvoices("Single");
				}
				updatedInvoices.add(invoice);
			}
			gstr2Repository.save(updatedInvoices);
		}
		
		
		return "gstr2  data update successfully...!";
	}
	
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping("/updateCompanyRoles")
	public @ResponseBody String companyRolesUpdate() {
		final String method = "partnerClientsUpdate::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		List<String> categoryList = Arrays.asList("Invoices","Settings","GSTN Actions","Reports","GSTR1","GSTR2","GSTR3B","GSTR4","GSTR5","GSTR6","General","All Configurations","Excel Download In Books And Returns","Excel Download In Reports","Multi GSTIN");
		
		List<String> returnsList = Arrays.asList("GSTR1","GSTR2","GSTR3B","GSTR4","GSTR5","GSTR6");
		
		List<String> clientCategoryList = Arrays.asList("Invoices","Settings","GSTN Actions","Reports","General","All Configurations","Excel Download In Books And Returns","Excel Download In Reports");
		
		
		List<CompanyRole> companyRoles = companyRoleRepository.findAll();
		List<CompanyRole> updatedroles = Lists.newArrayList();
		for(CompanyRole companyrole : companyRoles) {
			Map<String, List<Permission>> permissions = companyrole.getPermissions();
			if(isNotEmpty(companyrole) && isNotEmpty(companyrole.getClientid())) {
				Client client = clientRepository.findOne(companyrole.getClientid());
				if(isNotEmpty(client) ) {
					
					List<GSTReturnSummary> retsummary = client.getReturnsSummary();
					if(isNotEmpty(retsummary) && retsummary.size()>0) {
						for(GSTReturnSummary summary: retsummary) {
							if(returnsList.contains(summary.getReturntype())) {
								if("true".equalsIgnoreCase(summary.getActive())) {
									List<Permission> permission = permissions.get(summary.getReturntype());
									if(isNotEmpty(permission)) {
										List<String> permissionName = Lists.newArrayList();
										for(Permission perm : permission) {
											if(isNotEmpty(perm.getName())) {
												permissionName.add(perm.getName());
											}
										}
										List<Role> rolesBycategory = roleRepository.findByCategory(summary.getReturntype());
										for(Role categoryRole : rolesBycategory) {
											if(!permissionName.contains(categoryRole.getName())) {
												Permission tPerm = new Permission();
												tPerm.setName(categoryRole.getName());
												tPerm.setStatus(NO);
												permission.add(tPerm);
											}
										}
									}else {
										permission = Lists.newArrayList();
										List<Role> rolesBycategory = roleRepository.findByCategory(summary.getReturntype());
										for(Role categoryRole : rolesBycategory) {
												Permission tPerm = new Permission();
												tPerm.setName(categoryRole.getName());
												tPerm.setStatus(NO);
												permission.add(tPerm);
										}
										permissions.put(summary.getReturntype(), permission);
									}
								}
							}
						}
					}
					for(String category : clientCategoryList) {
						if(!category.equalsIgnoreCase("Invoices") && !category.equalsIgnoreCase("Settings")) {
							List<Permission> permission = permissions.get(category);
							if(isNotEmpty(permission)) {
								List<String> permissionName = Lists.newArrayList();
								for(Permission perm : permission) {
									if(isNotEmpty(perm.getName())) {
										permissionName.add(perm.getName());
									}
								}
								List<Role> rolesBycategory = roleRepository.findByCategory(category);
								for(Role categoryRole : rolesBycategory) {
									if(!permissionName.contains(categoryRole.getName())) {
										Permission tPerm = new Permission();
										tPerm.setName(categoryRole.getName());
										tPerm.setStatus(NO);
										permission.add(tPerm);
									}
								}
							}else {
								permission = Lists.newArrayList();
								List<Role> rolesBycategory = roleRepository.findByCategory(category);
								for(Role categoryRole : rolesBycategory) {
										Permission tPerm = new Permission();
										tPerm.setName(categoryRole.getName());
										if(category.equalsIgnoreCase("All Configurations")) {
											tPerm.setStatus(YES);
										}else {
											tPerm.setStatus(NO);
										}
										permission.add(tPerm);
								}
								permissions.put(category, permission);
							}
						}else {
								List<Role> rolesBycategory = roleRepository.findByCategory(category);
								List<Permission> roles = permissions.get(category);
								if(isNotEmpty(roles)) {
									List<String> permissionName = Lists.newArrayList();
									for(Permission permission : roles) {
										permission.setStatus(permission.getStatus()+"-"+YES);
										permissionName.add(permission.getName()+"-"+permission.getStatus());
									}
									for(Role categoryRole : rolesBycategory) {
										Set<String> privileges = categoryRole.getPrivileges();
										for(String privilege : privileges) {
											if(!permissionName.contains(categoryRole.getName()+"-"+privilege+"-"+YES)) {
												Permission tPerm = new Permission();
												tPerm.setName(categoryRole.getName());
												tPerm.setStatus(privilege+"-"+NO);
												roles.add(tPerm);
											}
										}
										
									}
								}else {
									roles = Lists.newArrayList();
									for(Role categoryRole : rolesBycategory) {
										Set<String> privileges = categoryRole.getPrivileges();
										for(String privilege : privileges) {
												Permission tPerm = new Permission();
												tPerm.setName(categoryRole.getName());
												tPerm.setStatus(privilege+"-"+NO);
												roles.add(tPerm);
										}
									}
									permissions.put(category, roles);
								}
						}
						
					}
				}
			}else {
				for(String category : categoryList) {
					if(!category.equalsIgnoreCase("Invoices") && !category.equalsIgnoreCase("Settings")) {
						List<Permission> permission = permissions.get(category);
						if(isNotEmpty(permission)) {
							List<String> permissionName = Lists.newArrayList();
							for(Permission perm : permission) {
								if(isNotEmpty(perm.getName())) {
									permissionName.add(perm.getName());
								}
							}
							List<Role> rolesBycategory = roleRepository.findByCategory(category);
							for(Role categoryRole : rolesBycategory) {
								if(!permissionName.contains(categoryRole.getName())) {
									Permission tPerm = new Permission();
									tPerm.setName(categoryRole.getName());
									tPerm.setStatus(NO);
									permission.add(tPerm);
								}
							}
						}else {
							permission = Lists.newArrayList();
							List<Role> rolesBycategory = roleRepository.findByCategory(category);
							for(Role categoryRole : rolesBycategory) {
									Permission tPerm = new Permission();
									tPerm.setName(categoryRole.getName());
									if(category.equalsIgnoreCase("All Configurations")) {
										tPerm.setStatus(YES);
									}else {
										tPerm.setStatus(NO);
									}
									permission.add(tPerm);
							}
							permissions.put(category, permission);
						}
					}else {
							List<Role> rolesBycategory = roleRepository.findByCategory(category);
							List<Permission> roles = permissions.get(category);
							if(isNotEmpty(roles)) {
								List<String> permissionName = Lists.newArrayList();
								for(Permission permission : roles) {
									permission.setStatus(permission.getStatus()+"-"+YES);
									permissionName.add(permission.getName()+"-"+permission.getStatus());
								}
								for(Role categoryRole : rolesBycategory) {
									Set<String> privileges = categoryRole.getPrivileges();
									for(String privilege : privileges) {
										if(!permissionName.contains(categoryRole.getName()+"-"+privilege+"-"+YES)) {
											Permission tPerm = new Permission();
											tPerm.setName(categoryRole.getName());
											tPerm.setStatus(privilege+"-"+NO);
											roles.add(tPerm);
										}
									}
									
								}
							}else {
								roles = Lists.newArrayList();
								for(Role categoryRole : rolesBycategory) {
									Set<String> privileges = categoryRole.getPrivileges();
									for(String privilege : privileges) {
											Permission tPerm = new Permission();
											tPerm.setName(categoryRole.getName());
											tPerm.setStatus(privilege+"-"+NO);
											roles.add(tPerm);
									}
								}
								permissions.put(category, roles);
							}
					}
					
				}
			}
			companyrole.setPermissions(permissions);
			updatedroles.add(companyrole);
		}
		
		
		if(isNotEmpty(updatedroles)) {
			companyRoleRepository.save(updatedroles);
		}
		
		logger.debug(CLASSNAME + method + END);
		return "Company roles data update successfully...!";
	}
	
	@RequestMapping(value = "/leadDaysTotalSummary/{month}/{year}/{dayWeek}")
	public @ResponseBody Map<String,Map<String,String>> getConsolidateLeadReports(@PathVariable int month, @PathVariable int year,@PathVariable String dayWeek,HttpServletRequest request){
		final String method = "getConsolidateReportsTotalAmounts::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, Map<String, String>> summaryMap = new HashMap<String, Map<String, String>>();
		String tabName = request.getParameter("tabName");
		PartnerFilter partnerFilter = new PartnerFilter();
		partnerFilter.setPartnerType(request.getParameter("partnerType"));
		partnerFilter.setPartnername(request.getParameter("partnerName"));
		List<String> userids = Lists.newArrayList();
		Page<User> saleUsers = partnerService.getPartners(tabName,partnerFilter);
		List<User> saleclnts=saleUsers.getContent();
		for(User user : saleclnts) {
			if (!userids.contains(user.getId().toString())) { 
				userids.add(user.getId().toString());
			}
		}
			summaryMap = partnerService.getConsolidatedSummeryForDaysInMonth(Utility.getYearCode(month, year),month,dayWeek,tabName,userids,partnerFilter);
		return summaryMap;
	}
	
	@RequestMapping(value = "/getAdditionalPartnerDetails/{month}/{year}/{dayWeek}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAdditionalInvoicesSupport(@PathVariable int month, @PathVariable int year,@PathVariable String dayWeek,HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, Object> summaryMap = new HashMap<String, Object>();
		String tabName = request.getParameter("tabName");
		List<String> userids = Lists.newArrayList();
		if("salesTab".equalsIgnoreCase(tabName) || "partnersTab".equalsIgnoreCase(tabName)) {
			Page<User> saleUsers = partnerService.getPartners(tabName);
			List<User> saleclnts=saleUsers.getContent();
			for(User user : saleclnts) {
				if (!userids.contains(user.getId().toString())) { 
					userids.add(user.getId().toString());
				}
			}
			summaryMap = partnerService.getPartnerSupport(Utility.getYearCode(month, year),month,dayWeek,tabName,userids);
		}else {
			summaryMap = partnerService.getPartnerSupport(Utility.getYearCode(month, year),month,dayWeek,tabName,userids);
		}
		return summaryMap;
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping("/updateUserNewReturnsActionsRoles")
	public @ResponseBody String updateUserNewReturnsActionsRoles() {
		final String method = "updateUserRoles::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		List<String> categoryList = Arrays.asList("Einvoice Actions", "Ewaybill Actions");
		List<String> einvReturnsList = Arrays.asList("Generate IRN", "Cancel IRN");
		List<String> ewaybillReturnsList = Arrays.asList("Generate Ewaybill", "Download Ewaybill", "Cancel Ewaybill", "Update Vehicle");
		
		List<CompanyRole> companyRoles = companyRoleRepository.findAll();
		List<CompanyRole> updatedroles = Lists.newArrayList();
		for(CompanyRole companyrole : companyRoles) {
			List<Permission> role = Lists.newArrayList();
			Map<String, List<Permission>> permissions = companyrole.getPermissions();
			if(isNotEmpty(companyrole) && isNotEmpty(companyrole.getClientid())) {
				Client client = clientRepository.findOne(companyrole.getClientid());
				if(isNotEmpty(client) ) {
					for(String category : categoryList) {
						if("Einvoice Actions".equals(category)) {
							for(String rtnType : einvReturnsList) {
								Permission tPerm = new Permission();
								tPerm.setName(rtnType);
								tPerm.setStatus(YES);
								role.add(tPerm);
								permissions.put(category, role);
							}							
						}else {
							for(String rtnType : ewaybillReturnsList) {
								Permission tPerm = new Permission();
								tPerm.setName(rtnType);
								tPerm.setStatus(YES);
								role.add(tPerm);
								permissions.put(category, role);
							}
						}
					}
				}
			}else {
				for(String category : categoryList) {
					if("Einvoice Actions".equals(category)) {
						for(String rtnType : einvReturnsList) {
							Permission tPerm = new Permission();
							tPerm.setName(rtnType);
							tPerm.setStatus(YES);
							role.add(tPerm);
							permissions.put(category, role);
						}							
					}else {
						for(String rtnType : ewaybillReturnsList) {
							Permission tPerm = new Permission();
							tPerm.setName(rtnType);
							tPerm.setStatus(YES);
							role.add(tPerm);
							permissions.put(category, role);
						}
					}
				}
			}
			companyrole.setPermissions(permissions);
			updatedroles.add(companyrole);
		}
		
		
		if(isNotEmpty(updatedroles)) {
			companyRoleRepository.save(updatedroles);
		}
		
		logger.debug(CLASSNAME + method + END);
		return "Company roles data update successfully...!";
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping("/updateUserRoles")
	public @ResponseBody String updateUserRoles() {
		final String method = "updateUserRoles::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		List<String> categoryList = Arrays.asList("New Returns");
		List<String> returnsList = Arrays.asList("E-invoice", "Ewaybill");
		
		List<CompanyRole> companyRoles = companyRoleRepository.findAll();
		List<CompanyRole> updatedroles = Lists.newArrayList();
		for(CompanyRole companyrole : companyRoles) {
			List<Permission> role = Lists.newArrayList();
			Map<String, List<Permission>> permissions = companyrole.getPermissions();
			if(isNotEmpty(companyrole) && isNotEmpty(companyrole.getClientid())) {
				Client client = clientRepository.findOne(companyrole.getClientid());
				if(isNotEmpty(client) ) {
					for(String category : categoryList) {
						for(String rtnType : returnsList) {
							Permission tPerm = new Permission();
							tPerm.setName(rtnType);
							tPerm.setStatus(YES);
							role.add(tPerm);
							permissions.put(category, role);
						}
					}
				}
			}else {
				for(String category : categoryList) {
					for(String rtnType : returnsList) {
						Permission tPerm = new Permission();
						tPerm.setName(rtnType);
						tPerm.setStatus(YES);
						role.add(tPerm);
						permissions.put(category, role);
					}
					
				}
			}
			companyrole.setPermissions(permissions);
			updatedroles.add(companyrole);
		}
		
		
		if(isNotEmpty(updatedroles)) {
			companyRoleRepository.save(updatedroles);
		}
		
		logger.debug(CLASSNAME + method + END);
		return "Company roles data update successfully...!";
	}
@RequestMapping(value = "/getusers", method = RequestMethod.GET)
	public @ResponseBody User getusers(@RequestParam(value = "id", required = true) String userid, 
			ModelMap model) throws Exception {
		User user = userService.findById(userid);
		return user;
	}
	@RequestMapping(value = "/getuserDetails", method = RequestMethod.GET)
	public @ResponseBody User getuserDetails(@RequestParam(value = "email", required = true) String email, 
			ModelMap model) throws Exception {
		final String method = "getuserDetails::";
		User user = userService.findByEmail(email);
		
		SubscriptionDetails subscriptionDetails = null;
		if(NullUtil.isNotEmpty(user.getType()) && user.getType().equals(MasterGSTConstants.ASPDEVELOPER)){
			SubscriptionDetails gstApiAllowedCountSubscriptionDetails = subscriptionService.getSubscriptionData(user.getId().toString(),"GSTAPI");
			SubscriptionDetails gstApiAllowedSandboxUsageCountSubScriptionDetails = subscriptionService.getSubscriptionData(user.getId().toString(),"GSTSANDBOXAPI");
			SubscriptionDetails ewaybillApiAllowedCountSubscriptionDetails = subscriptionService.getSubscriptionData(user.getId().toString(),"EWAYAPI");
			SubscriptionDetails ewaybillSanboxAllowedCountSubscriptionDetails = subscriptionService.getSubscriptionData(user.getId().toString(),"EWAYBILLSANDBOXAPI");
			SubscriptionDetails einvApiAllowedCountSubscriptionDetails = subscriptionService.getSubscriptionData(user.getId().toString(),"E-INVOICEAPI");
			SubscriptionDetails einvSanboxAllowedCountSubscriptionDetails = subscriptionService.getSubscriptionData(user.getId().toString(),"E-INVOICESANDBOXAPI");
			Calendar calendar = Calendar.getInstance();
		    calendar.add(Calendar.DAY_OF_YEAR, 1);
		    Date tomorrowDate = calendar.getTime();
			if(NullUtil.isNotEmpty(gstApiAllowedCountSubscriptionDetails)) {
				user.setGstAPIAllowedInvoices(gstApiAllowedCountSubscriptionDetails.getAllowedInvoices());
				user.setGstAPIUsageCountInvoices(gstApiAllowedCountSubscriptionDetails.getProcessedInvoices());
				Date endDate=gstApiAllowedCountSubscriptionDetails.getExpiryDate();
				if(tomorrowDate.compareTo(endDate)>0) {
					user.setGstAPIStatus("EXPIRED");
				}else {
					user.setGstAPIStatus("ACTIVE");					
				}
				user.setGstSubscriptionStartDate(dateFormat.format(gstApiAllowedCountSubscriptionDetails.getRegisteredDate()));
				user.setGstSubscriptionExpiryDate(dateFormat.format(gstApiAllowedCountSubscriptionDetails.getExpiryDate()));
			}
			if(NullUtil.isNotEmpty(gstApiAllowedSandboxUsageCountSubScriptionDetails)) {
				user.setGstSanboxAllowedCountInvoices(gstApiAllowedSandboxUsageCountSubScriptionDetails.getAllowedInvoices());
				user.setGstSanboxUsageCountInvoices(gstApiAllowedSandboxUsageCountSubScriptionDetails.getProcessedSandboxInvoices());
				user.setGstSandboxSubscriptionStartDate(dateFormat.format(gstApiAllowedSandboxUsageCountSubScriptionDetails.getRegisteredDate()));
			if(NullUtil.isNotEmpty(gstApiAllowedSandboxUsageCountSubScriptionDetails.getExpiryDate())) {
					if(NullUtil.isNotEmpty(gstApiAllowedSandboxUsageCountSubScriptionDetails.getSubscriptionType())){
						 if("UNLIMITED".equalsIgnoreCase(gstApiAllowedSandboxUsageCountSubScriptionDetails.getSubscriptionType())){
								user.setGstSandboxSubscriptionExpiryDate("UNLIMITED");
								user.setGstSandboxAPIStatus("ACTIVE");	
							}else{
								Date endDate=gstApiAllowedSandboxUsageCountSubScriptionDetails.getExpiryDate();
								user.setGstSandboxSubscriptionExpiryDate(dateFormat.format(gstApiAllowedSandboxUsageCountSubScriptionDetails.getExpiryDate()));	
								
								if(tomorrowDate.compareTo(endDate)>0) {
									user.setGstSandboxAPIStatus("EXPIRED");
								}else {
									user.setGstSandboxAPIStatus("ACTIVE");					
								}
						}
					}else{
						Date endDate=gstApiAllowedSandboxUsageCountSubScriptionDetails.getExpiryDate();
						user.setGstSandboxSubscriptionExpiryDate(dateFormat.format(gstApiAllowedSandboxUsageCountSubScriptionDetails.getExpiryDate()));	
						
						if(tomorrowDate.compareTo(endDate)>0) {
							user.setGstSandboxAPIStatus("EXPIRED");
						}else {
							user.setGstSandboxAPIStatus("ACTIVE");					
						}
					}
			}else{
				user.setGstSandboxAPIStatus("ACTIVE");
				user.setGstSandboxSubscriptionExpiryDate("UNLIMITED");
			}
			}
			if(NullUtil.isNotEmpty(ewaybillApiAllowedCountSubscriptionDetails)) {
				user.setEwaybillAPIAllowedInvoices(ewaybillApiAllowedCountSubscriptionDetails.getAllowedInvoices());
				user.setEwaybillAPIUsageCountInvoices(ewaybillApiAllowedCountSubscriptionDetails.getProcessedInvoices());
				Date endDate=ewaybillApiAllowedCountSubscriptionDetails.getExpiryDate();
				if(tomorrowDate.compareTo(endDate)>0) {
					user.setEwaybillAPIStatus("EXPIRED");
				}else {
					user.setEwaybillAPIStatus("ACTIVE");					
				}
				user.setEwaybillSubscriptionStartDate(dateFormat.format(ewaybillApiAllowedCountSubscriptionDetails.getRegisteredDate()));
				user.setEwaybillSubscriptionExpiryDate(dateFormat.format(ewaybillApiAllowedCountSubscriptionDetails.getExpiryDate()));				
			}
			if(NullUtil.isNotEmpty(ewaybillSanboxAllowedCountSubscriptionDetails)) {
				user.setEwaybillSanboxAllowedInvoices(ewaybillSanboxAllowedCountSubscriptionDetails.getAllowedInvoices());
				user.setEwaybillSanboxUsageCountInvoices(ewaybillSanboxAllowedCountSubscriptionDetails.getProcessedSandboxInvoices());
				
				
				if(NullUtil.isNotEmpty(ewaybillSanboxAllowedCountSubscriptionDetails.getExpiryDate())) {
					if(NullUtil.isNotEmpty(ewaybillSanboxAllowedCountSubscriptionDetails.getSubscriptionType())){
						 if("UNLIMITED".equalsIgnoreCase(ewaybillSanboxAllowedCountSubscriptionDetails.getSubscriptionType())){
								user.setEwaybillSandboxSubscriptionExpiryDate("UNLIMITED");
								user.setEwaybillSandboxAPIStatus("ACTIVE");	
							}else{
								Date endDate=ewaybillSanboxAllowedCountSubscriptionDetails.getExpiryDate();
								user.setEwaybillSandboxSubscriptionExpiryDate(dateFormat.format(ewaybillSanboxAllowedCountSubscriptionDetails.getExpiryDate()));	
								
								if(tomorrowDate.compareTo(endDate)>0) {
									user.setEwaybillSandboxAPIStatus("EXPIRED");
								}else {
									user.setEwaybillSandboxAPIStatus("ACTIVE");					
								}
						}
					}else{
						Date endDate=ewaybillSanboxAllowedCountSubscriptionDetails.getExpiryDate();
						user.setEwaybillSandboxSubscriptionExpiryDate(dateFormat.format(ewaybillSanboxAllowedCountSubscriptionDetails.getExpiryDate()));	
						
						if(tomorrowDate.compareTo(endDate)>0) {
							user.setEwaybillSandboxAPIStatus("EXPIRED");
						}else {
							user.setEwaybillSandboxAPIStatus("ACTIVE");					
						}
					}
			}else{
				user.setEwaybillSandboxAPIStatus("ACTIVE");
				user.setEwaybillSandboxSubscriptionExpiryDate("UNLIMITED");
			}
				user.setEwaybillSandboxSubscriptionStartDate(dateFormat.format(ewaybillSanboxAllowedCountSubscriptionDetails.getRegisteredDate()));
			}
			
			
			if(NullUtil.isNotEmpty(einvApiAllowedCountSubscriptionDetails)) {
				user.setEinvAPIAllowedInvoices(einvApiAllowedCountSubscriptionDetails.getAllowedInvoices());
				user.setEinvAPIUsageCountInvoices(einvApiAllowedCountSubscriptionDetails.getProcessedInvoices());
				Date endDate=einvApiAllowedCountSubscriptionDetails.getExpiryDate();
				if(tomorrowDate.compareTo(endDate)>0) {
					user.setEinvAPIStatus("EXPIRED");
				}else {
					user.setEinvAPIStatus("ACTIVE");					
				}
				user.setEinvSubscriptionStartDate(dateFormat.format(einvApiAllowedCountSubscriptionDetails.getRegisteredDate()));
				user.setEinvSubscriptionExpiryDate(dateFormat.format(einvApiAllowedCountSubscriptionDetails.getExpiryDate()));				
			}
			if(NullUtil.isNotEmpty(einvSanboxAllowedCountSubscriptionDetails)) {
				user.setEinvSanboxAllowedInvoices(einvSanboxAllowedCountSubscriptionDetails.getAllowedInvoices());
				user.setEinvSanboxUsageCountInvoices(einvSanboxAllowedCountSubscriptionDetails.getProcessedSandboxInvoices());
				
				
				if(NullUtil.isNotEmpty(einvSanboxAllowedCountSubscriptionDetails.getExpiryDate())) {
					if(NullUtil.isNotEmpty(einvSanboxAllowedCountSubscriptionDetails.getSubscriptionType())){
						 if("UNLIMITED".equalsIgnoreCase(einvSanboxAllowedCountSubscriptionDetails.getSubscriptionType())){
								user.setEinvSandboxSubscriptionExpiryDate("UNLIMITED");
								user.setEinvSandboxAPIStatus("ACTIVE");	
							}else{
								Date endDate=einvSanboxAllowedCountSubscriptionDetails.getExpiryDate();
								user.setEinvSandboxSubscriptionExpiryDate(dateFormat.format(einvSanboxAllowedCountSubscriptionDetails.getExpiryDate()));	
								
								if(tomorrowDate.compareTo(endDate)>0) {
									user.setEinvSandboxAPIStatus("EXPIRED");
								}else {
									user.setEinvSandboxAPIStatus("ACTIVE");					
								}
						}
					}else{
						Date endDate=einvSanboxAllowedCountSubscriptionDetails.getExpiryDate();
						user.setEinvSandboxSubscriptionExpiryDate(dateFormat.format(einvSanboxAllowedCountSubscriptionDetails.getExpiryDate()));	
						
						if(tomorrowDate.compareTo(endDate)>0) {
							user.setEinvSandboxAPIStatus("EXPIRED");
						}else {
							user.setEinvSandboxAPIStatus("ACTIVE");					
						}
					}
			}else{
				user.setEinvSandboxAPIStatus("ACTIVE");
				user.setEinvSandboxSubscriptionExpiryDate("UNLIMITED");
			}
				user.setEinvSandboxSubscriptionStartDate(dateFormat.format(einvSanboxAllowedCountSubscriptionDetails.getRegisteredDate()));
			}
			
			
		}else{
			subscriptionDetails = subscriptionService.getSubscriptionData(user.getId().toString());
		}
		if(NullUtil.isNotEmpty(subscriptionDetails)){
			user.setSubscriptionType(subscriptionDetails.getPlanid());
			user.setPaidAmount(Double.toString(subscriptionDetails.getPaidAmount()));
			user.setSubscriptionStartDate(dateFormat.format(subscriptionDetails.getRegisteredDate()));
			user.setSubscriptionExpiryDate(dateFormat.format(subscriptionDetails.getExpiryDate()));
			if(NullUtil.isNotEmpty(subscriptionDetails.getAllowedCenters())){
				user.setTotalCenters(subscriptionDetails.getAllowedCenters()+"");
			}
			if(NullUtil.isNotEmpty(user.getType()) && (user.getType().equals(MasterGSTConstants.ENTERPRISE) || user.getType().equals(MasterGSTConstants.BUSINESS))){
				if(NullUtil.isNotEmpty(subscriptionDetails.getAllowedClients())){
					user.setTotalClients(subscriptionDetails.getAllowedClients()+"");
				}
			}
			if(NullUtil.isNotEmpty(subscriptionDetails.getAllowedInvoices())){
				user.setTotalInvoices(subscriptionDetails.getAllowedInvoices()+"");
			}
			if(NullUtil.isNotEmpty(subscriptionDetails.getProcessedInvoices())){
				user.setTotalInvoicesUsed(subscriptionDetails.getProcessedInvoices()+"");
				user.setBranches(subscriptionDetails.getProcessedInvoices()+"");
				user.setAnyerp(subscriptionDetails.getProcessedSandboxInvoices()+"");
			}
			if(NullUtil.isNotEmpty(user.getType()) && (user.getType().equals(MasterGSTConstants.SUVIDHA_CENTERS) || user.getType().equals(MasterGSTConstants.CAS))){
				if(NullUtil.isNotEmpty(subscriptionDetails.getAllowedClients())){
					user.setTotalClients(subscriptionDetails.getAllowedClients()+"");
				}else {
					List<Client> lClient = clientService.findByUserid(user.getId().toString());
					if(lClient != null){
						user.setTotalClients(Integer.toString(lClient.size()));
					}
				}
			}
		}
		
		if(NullUtil.isNotEmpty(user.getType()) && user.getType().equals(MasterGSTConstants.PARTNER)) {
			List<PartnerClient> partnerTotalInvitedClient = partnerService.getPartnerClients(user.getId().toString());
			List<PartnerClient> partnerTotalJoinedClient = partnerService.getPartnerJoinedClients(user.getId().toString());
			if(partnerTotalInvitedClient != null){
				user.setTotalInvitedClients(Integer.toString(partnerTotalInvitedClient.size()));
			}
			if(partnerTotalInvitedClient != null && partnerTotalJoinedClient != null){
				user.setTotalPendingClients(Integer.toString(partnerTotalInvitedClient.size() - partnerTotalJoinedClient.size()));
			}
			if(partnerTotalJoinedClient != null){
				long subscribedCount = 0;
				user.setTotalJoinedClients(Integer.toString(partnerTotalJoinedClient.size()));
				for(PartnerClient partnerTotalJoinedClients : partnerTotalJoinedClient){
					if(NullUtil.isNotEmpty(partnerTotalJoinedClients.getClientid())){
						List<PaymentDetails> payments = subscriptionService.getPaymentDetails(partnerTotalJoinedClients.getClientid().toString());
						if(!payments.isEmpty()){
							subscribedCount++;
						}
					}
				}
				user.setTotalSubscribedClients(subscribedCount+"");
			}
		}
		if(isNotEmpty(user.getId())) {
			user.setPassword(user.getId().toString());
		}
		return user;
	}
	
	@RequestMapping(value = "/getPartnetPaymentInvoices/{month}/{year}/{fromtime}/{totime}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getPartnetPaymentInvoices(@PathVariable int month, @PathVariable int year,
			@PathVariable String fromtime, @PathVariable String totime, @RequestParam String type, HttpServletRequest request) throws Exception {
		final String method = "getPartnetPaymentInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		Page<User> users = null;
		/*if("custom".equals(type)) {
			String[] fromtimes = fromtime.split("-");
			String[] totimes = totime.split("-");
			
			Date stDate = null;
			Date endDate = null;
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23, 59, 59);
			endDate = new java.util.Date(cal.getTimeInMillis());
			users = userService.findByPartnerUsers(stDate, endDate, start, length, searchVal);
		}else {
		}*/
		String yearCode = Utility.getYearCode(month, year);
			
		users = partnerService.findByPartnerUsers(month, yearCode, start, length, searchVal);
		if(isNotEmpty(users)) {
			findByPartnerClientsAndSubscriptionData(users, month, yearCode);
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer = mapper.writer();
		return writer.writeValueAsString(users);
	}
	
	public void findByPartnerClientsAndSubscriptionData(Page<User> users, int month, String yearCode) {
		users.getContent().stream().forEach(user->{
			user.setUserId(user.getId().toString());
			Double percentage = 25d;
			if(isNotEmpty(user.getPartnerPercentage())) {
				percentage = Double.parseDouble(user.getPartnerPercentage());
			}else {
				user.setPartnerPercentage("25");
			}
			List<PartnerClient> clients = partnerClientRepository.findByUseridAndClientidIsNotNull(user.getId().toString());
			Double subscriptionamts = 0d;
			int joinClients = clients.size();
			
			if(NullUtil.isNotEmpty(clients)) {
				for(PartnerClient client : clients) {
					if(isNotEmpty(client) && isNotEmpty(client.getClientid())){
						/*
						 List<SubscriptionDetails> subscription = subscriptionDetailsRepository.findByUseridAndMthCdAndYrCd(client.getClientid(), month+"", yearCode);
						 if(NullUtil.isNotEmpty(subscription)){
							for(SubscriptionDetails subscrb : subscription) {
								subscriptionamts += subscrb.getPaidAmount();							
							}
						}*/
						List<PaymentDetails> payments = subscriptionService.getPartnerPaymentDetails(client.getClientid(), month, yearCode);
						if(NullUtil.isNotEmpty(payments)){
							
							for(PaymentDetails payment : payments) {
								subscriptionamts += payment.getAmount();  
							}
						}
					}
				}
			}
			
			Double partneramt = (subscriptionamts * percentage) / 100;
			
			user.setPaidAmount(BigDecimal.valueOf(partneramt).toPlainString());
			user.setSubscriptionamount(BigDecimal.valueOf(subscriptionamts).toPlainString());
		});
	}
	
	@GetMapping("/getpartnerPayments/{userid}")
	public @ResponseBody PartnerPayments getpartnerPayments(@PathVariable String userid, @RequestParam int month, @RequestParam int year) {
		final String method = "getpartnerPayments::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		return partnerService.findByPartnerPaymentsUserid(userid, month, year);
	}
	
	@GetMapping("/getppartnerPayments/{docid}")
	public @ResponseBody PartnerPayments getppartnerPayments(@PathVariable String docid, @RequestParam int month, @RequestParam String year) {
		final String method = "getpartnerPayments::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		return partnerService.findByPartnerPaymentsDocIdAndMonthYearCode(docid, month, year);
	}
	
	@PostMapping("/savepartnerPayments/{userid}")
	public @ResponseBody PartnerPayments createComments(@PathVariable String userid, @RequestBody PartnerPayments partnerPayments,
			 @RequestParam int month, @RequestParam int year, HttpServletRequest request) {
		final String method = "getpartnerPayments::";
		logger.debug(CLASSNAME + method + BEGIN);
		String type = request.getParameter("type");
		
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
		partnerPayments.setMthCd(month+"");
		partnerPayments.setYrCd(yearCode);
		partnerPayments.setQrtCd(quarter+"");
		partnerPayments.setUserid(userid);
		return partnerService.savePartnerPayments(partnerPayments, month, year);
	}
	
	@PostMapping("/saveppartnerPayments/{userid}")
	public @ResponseBody PartnerPayments payPartnerPayments(@PathVariable String userid, @RequestBody PartnerPayments partnerPayments,
			 @RequestParam int month, @RequestParam String year, HttpServletRequest request) {
		final String method = "getpartnerPayments::";
		logger.debug(CLASSNAME + method + BEGIN);
		String type = request.getParameter("type");
		
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		
		partnerPayments.setMthCd(month+"");
		partnerPayments.setYrCd(year);
		partnerPayments.setQrtCd(quarter+"");
		partnerPayments.setUserid(userid);
		return partnerService.savePpartnerPayments(partnerPayments, month, year);
	}
	
	@RequestMapping(value = "/getApiExceedsUsers", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getApiExceedsUsers(@RequestParam(name = "apis", required = false) String apis,@RequestParam(name = "type", required = false) String type, HttpServletRequest request) throws Exception {
		final String method = "getApiExceedsUsers::";
		logger.debug(CLASSNAME + method + BEGIN);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}	
		Page<ApiExceedsUsers> subscriptions = subscriptionService.getExceedUsageApisInfo(apis,start, length, searchVal);	
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer = mapper.writer();
		return writer.writeValueAsString(subscriptions);
	}
	
	@RequestMapping(value = "/partnerbankdetails", method = RequestMethod.GET)
	public @ResponseBody PartnerBankDetails getPartnerbankDetails(@RequestParam(value = "id", required = true) String id, ModelMap model) throws Exception {
		return profileService.getPBankDetails(id);
	}
	
	@RequestMapping(value = "/expriyusers", method = RequestMethod.GET)
	public @ResponseBody String expiryUsers(@RequestParam(value = "id", required = true) String id,
			@RequestParam String type, HttpServletRequest request, ModelMap model) throws Exception {
		final String method = "expiryUsers::";
		logger.debug(CLASSNAME + method + BEGIN);
	
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}	
	
		Page<SubscriptionUsers> users = subscriptionService.getExpiredUsers(type, start, length, searchVal);
		users.forEach(usr->{
			usr.setDocId(usr.getId().toString());
		});
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer = mapper.writer();
		return writer.writeValueAsString(users);
	}
	
	@RequestMapping(value = "/clientpayments/{userid}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getPartnerClientsPayments(@PathVariable String userid, 
			@RequestParam String payment,@RequestParam int month, @RequestParam int year,
			HttpServletRequest request, ModelMap model) throws Exception{
		final String method = "getPartnerClientsPayments::";
		logger.debug(CLASSNAME + method + BEGIN);
			
		String startStr = request.getParameter("start");
		int start = 0;
		if(NullUtil.isNotEmpty(startStr)){
			start = Integer.parseInt(startStr);
		}
		String lengthStr = request.getParameter("length");
		int length = 10;
		if(NullUtil.isNotEmpty(lengthStr)){
			length = Integer.parseInt(lengthStr);
		}
		if(start != 0){
			start = start/length;
		}
		String searchVal = request.getParameter("search[value]");
		if(searchVal != null && "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = "createdDate";
		String sortOrder = "asc";
		sortOrder = request.getParameter("order[0][dir]");
		
		Page<ClientPaymentDetails> partnerClients = partnerService.getPartnerClientPayments(userid, payment , month, year,  start, length, sortParam, sortOrder, searchVal);
		
		
		logger.debug(CLASSNAME + method + END);
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer = mapper.writer();
		return writer.writeValueAsString(partnerClients);
	}
	
	@RequestMapping(value = "/partnerpayments/{userid}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getPartnerPaymentsDone(@PathVariable String userid, 
			@RequestParam String payment, @RequestParam int month, @RequestParam int year,
			HttpServletRequest request, ModelMap model) throws Exception{
		final String method = "getPartnerPaymentsDone::";
		logger.debug(CLASSNAME + method + BEGIN);
			
		String startStr = request.getParameter("start");
		int start = 0;
		if(NullUtil.isNotEmpty(startStr)){
			start = Integer.parseInt(startStr);
		}
		String lengthStr = request.getParameter("length");
		int length = 10;
		if(NullUtil.isNotEmpty(lengthStr)){
			length = Integer.parseInt(lengthStr);
		}
		if(start != 0){
			start = start/length;
		}
		String searchVal = request.getParameter("search[value]");
		if(searchVal != null && "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = "createdDate";
		String sortOrder = "asc";
		sortOrder = request.getParameter("order[0][dir]");
		
		Page<PartnerPayments> partnerClients = partnerService.getPartnerPayments(userid, payment, month, year, start, length, sortParam, sortOrder, searchVal);
		
		if(isNotEmpty(partnerClients.getContent()) && partnerClients.getContent().size() > 0) {
			partnerClients.getContent().forEach(ppayment ->{ppayment.setDocId(ppayment.getId().toString());});
		}
		logger.debug(CLASSNAME + method + END);
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer = mapper.writer();
		return writer.writeValueAsString(partnerClients);
	}
	
	@RequestMapping(value = "/sbscrpymtlink/{id}/{amount}/{invoices}/{category}/{rateofinclusivetax}", method = RequestMethod.GET)
	public @ResponseBody void subscriptionPayment(@PathVariable("id") String id,@PathVariable("amount") Double amount, @PathVariable("invoices") int invoices,@PathVariable("category") String category,@PathVariable("rateofinclusivetax") String rateofinclusivetax, ModelMap model) throws Exception {
		final String method = "subscriptionPayment::";
		logger.debug(CLASSNAME + method + BEGIN);
		PaymentLink payentlinkDetails = paymentLinkRepository.findByUserid(id);
		if(isNotEmpty(payentlinkDetails)) {
			paymentLinkRepository.delete(payentlinkDetails);
		}
		PaymentLink payentlinkDetail = new PaymentLink();
		payentlinkDetail.setUserid(id);
		payentlinkDetail.setPaidAmount(amount);
		payentlinkDetail.setAllowedInvoices(invoices);
		payentlinkDetail.setCategory(category);
		payentlinkDetail.setRateofinclusivetax(rateofinclusivetax);
		paymentLinkRepository.save(payentlinkDetail);
		//subscriptionService.updateOfflinePaymentDetails(id, usertype, amount, invoices, -1, -1, invRate, pymtMode, refNo, apiType, statename,"",paymentSubscriptionType,subscriptionStartDate,subscriptionExpiryDate);
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/compainemails", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody void sendEmails(@RequestBody CompaignEmail compaignEmail, @RequestParam(name = "type") String type) {
		adminSupportService.compaignEmails(compaignEmail, type);
	}
	
}
