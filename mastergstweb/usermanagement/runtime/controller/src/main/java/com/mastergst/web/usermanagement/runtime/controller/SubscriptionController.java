/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ccavenue.security.AesCryptUtil;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.Coupons;
import com.mastergst.configuration.service.Message;
import com.mastergst.configuration.service.SubscriptionPlan;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.PaymentDetails;
import com.mastergst.usermanagement.runtime.domain.PaymentLink;
import com.mastergst.usermanagement.runtime.domain.PaymentRequestDetails;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.repository.PaymentLinkRepository;
import com.mastergst.usermanagement.runtime.repository.PaymentRequestDetailsRepository;
import com.mastergst.usermanagement.runtime.service.CouponService;
import com.mastergst.usermanagement.runtime.service.MessageService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;

/**
 * Handles Subscription logic depending on the URI template.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 * @since 1.0
 */
@Controller
public class SubscriptionController {
	
	private static final Logger logger = LogManager.getLogger(SubscriptionController.class.getName());
	private static final String CLASSNAME = "SubscriptionController::";
	
	@Autowired
	UserService userService;
	
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private CouponService couponService;
	@Autowired
	private PaymentRequestDetailsRepository paymentRequestDetailsRepository;
	@Autowired
	MessageService messageService;
	@Autowired
	private	PaymentLinkRepository paymentLinkRepository;
	
	@Value("${ccavenue.url}")
	private String ccavenueUrl;
	@Value("${ccavenue.access.code}")
	private String accessCode;
	@Value("${ccavenue.working.key}")
	private String workingKey;
	@Value("${ccavenue.merchantid}")
	private String merchantId;
	@Value("${ccavenue.url.redirect}")
	private String redirectUrl;
	@Value("${ccavenue.url.cancel}")
	private String cancelUrl;
	
	@Value("${test.planid}")
	private String testplanid;

	@RequestMapping(value = "/sbscrpln/{id}/{name}/{usertype}/{type}/{month}/{year}", method = RequestMethod.GET)
	public String planSelection(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("type") String type,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "planSelection::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		model.addAttribute("plans", subscriptionService.getSubscriptions(type));
		model.addAttribute("pType", type);
		logger.debug(CLASSNAME + method + END);
		return "subscription/plans";
	}
	
	@RequestMapping(value = "/sbscrrvw/{id}/{name}/{usertype}/{planid}/{type}/{month}/{year}", method = RequestMethod.GET)
	public String planReview(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("planid") String planid,
			@PathVariable("type") String type, @PathVariable("month") int month, @PathVariable("year") int year,
			ModelMap model) throws Exception {
		final String method = "planReview::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		model.addAttribute("plans", subscriptionService.getSubscriptions(type));
		SubscriptionPlan subscriptionPlan = subscriptionService.getPlan(planid);
		model.addAttribute("plan", subscriptionPlan);
		model.addAttribute("type", "Review");
		model.addAttribute("pType", type);
		Calendar cal = Calendar.getInstance();
		if (NullUtil.isNotEmpty(subscriptionPlan) && NullUtil.isNotEmpty(subscriptionPlan.getCategory())
				&& !subscriptionPlan.getCategory().equals("AddOn")) {
			cal.add(Calendar.YEAR, 1);
			cal.add(Calendar.DATE, -1);
			model.addAttribute("expiryDate", cal.getTime());
		} else {
			SubscriptionDetails subscriptionDetails = subscriptionService.getSubscriptionData(id);
			if(NullUtil.isNotEmpty(subscriptionDetails)) {
				model.addAttribute("expiryDate", subscriptionDetails.getExpiryDate());
			}
		}
		logger.debug(CLASSNAME + method + END);
		return "subscription/subscription";
	}
	
	
	@RequestMapping(value = "/sbscrrvwk/{id}/{name}/{usertype}/{type}/{month}/{year}", method = RequestMethod.GET)
	public String paymentLink(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("type") String type, @PathVariable("month") int month, @PathVariable("year") int year,
			ModelMap model) throws Exception {
		final String method = "paymentLink::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		//model.addAttribute("plans", subscriptionService.getSubscriptions(type));
		//SubscriptionPlan subscriptionPlan = subscriptionService.getPlan(planid);
		PaymentLink paymentlink = paymentLinkRepository.findByUserid(id);
		//model.addAttribute("plan", subscriptionPlan);
		model.addAttribute("paymentlink", paymentlink);
		model.addAttribute("type", "Review");
		model.addAttribute("pType", type);
		Calendar cal = Calendar.getInstance();
			cal.add(Calendar.YEAR, 1);
			cal.add(Calendar.DATE, -1);
			model.addAttribute("expiryDate", cal.getTime());
		logger.debug(CLASSNAME + method + END);
		return "subscription/payment";
	}
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/dbllng/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String billing(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, @PathVariable("year") int year,
			ModelMap model) throws Exception {
		String method = "billing :: ";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		User user=userService.getUserById(id);
		
		if(NullUtil.isNotEmpty(user.getCreatedDate())){
			
			model.addAttribute("userregisterdate", user.getCreatedDate());
		}
		updateModel(model, id, fullname, usertype, month, year);
		model.addAttribute("otheruser", "no");
		SubscriptionDetails subscriptionDetails = null;
		if(!usertype.equals(MasterGSTConstants.ASPDEVELOPER)){
			subscriptionDetails = subscriptionService.getSubscriptionData(id);
			model.addAttribute("subscription", subscriptionDetails);
			model.addAttribute("creType", "authorizationKeys");
		}else{
			List<SubscriptionDetails> subscriptionDetailsList = subscriptionService.getAllSubscriptionDetailsByUser(id);
			if (NullUtil.isNotEmpty(subscriptionDetailsList)) {
				for(SubscriptionDetails subscriptionDetail:subscriptionDetailsList){
					Calendar cals = Calendar.getInstance();
					cals.add(Calendar.DATE, 1);
					Date tommarowDate = cals.getTime();
					Date expirydate = subscriptionDetail.getExpiryDate();
					
					if("UNLIMITED".equalsIgnoreCase(subscriptionDetail.getSubscriptionType())) {
					
						subscriptionDetail.setSubscriptionStatus("Active");
					}else {
						if(NullUtil.isNotEmpty(expirydate)) {
							if(tommarowDate.compareTo(expirydate)>0){
								subscriptionDetail.setSubscriptionStatus("Expired");
							}else{
								subscriptionDetail.setSubscriptionStatus("Active");
							}
						}
					}
				}
				model.addAttribute("subscriptionDetailsList", subscriptionDetailsList);
			}
		}
		String pType = "ASP";

		if (usertype.equals(MasterGSTConstants.CAS) || usertype.equals(MasterGSTConstants.TAXPRACTITIONERS)) {
			//pType = "CAS";
			pType = "ASP";
		} else if (usertype.equals(MasterGSTConstants.BUSINESS)) {
			pType = "Business";
		}
		
		if(usertype.equals(MasterGSTConstants.ENTERPRISE) || usertype.equals(MasterGSTConstants.BUSINESS) || usertype.equals(MasterGSTConstants.CAS) || usertype.equals(MasterGSTConstants.TAXPRACTITIONERS) || usertype.equals(MasterGSTConstants.SUVIDHA_CENTERS)) {
			String msgData = null;
			if (NullUtil.isNotEmpty(subscriptionDetails)) {
				//model.addAttribute("plan", subscriptionService.getPlan(subscriptionDetails.getPlanid()));
				List<PaymentDetails> lstPayments=subscriptionService.getPaymentDetails(id);
				if(NullUtil.isNotEmpty(lstPayments)) {
						msgData = "no";
				}else {
					msgData = "yes";
				}
				PaymentLink paymentLink = paymentLinkRepository.findByUserid(id);
				String payment = "no";
				if(NullUtil.isNotEmpty(paymentLink)) {
					payment = "yes";
					msgData = "no";
				}
				model.addAttribute("msgdata", msgData);
				model.addAttribute("paymentLink", payment);
				model.addAttribute("payments", subscriptionService.getPaymentDetails(id));
				if(usertype.equals(MasterGSTConstants.ENTERPRISE)){
					model.addAttribute("pType", MasterGSTConstants.ENTERPRISE);
				}else {
					model.addAttribute("pType", pType);
				}
				//model.addAttribute("pType", pType);
				return "subscription/billing";
			}
		}
		if(usertype.equals(MasterGSTConstants.ASPDEVELOPER)){
			List<Message> messages = messageService.getMessagesByUserType(MasterGSTConstants.ASPDEVELOPER);
			messages.forEach((m)->m.setMsgId(m.getId().toString()));
			model.addAttribute("messages", messages);
			model.addAttribute("messageCount",messages.size());
			//below line mandatory apiType header tab purpose 
			model.addAttribute("type", "gstapis");
		}
		if(NullUtil.isNotEmpty(subscriptionDetails) && NullUtil.isNotEmpty(subscriptionDetails.getPlanid())) {
			model.addAttribute("plan", subscriptionService.getPlan(subscriptionDetails.getPlanid()));
			model.addAttribute("payments", subscriptionService.getPaymentDetails(id));
			model.addAttribute("pType", pType);
			return "subscription/billing";
		} else if(usertype.equals(MasterGSTConstants.ASPDEVELOPER) || usertype.equals(MasterGSTConstants.SUVIDHA_CENTERS)) {
			//model.addAttribute("plan", "");
			model.addAttribute("payments", subscriptionService.getPaymentDetails(id));
			model.addAttribute("pType", "");
			return "subscription/billing";
		} else {
			return "redirect:/sbscrpln/" + id + "/" + fullname + "/" + usertype + "/" + pType + "/" + month + "/" + year;
		}
	}
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/payments/{id}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> payments(@PathVariable("id") String id) throws Exception {
		String method = "payments :: ";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		List<PaymentDetails> payments = subscriptionService.getPaymentDetails(id);
		Map<String, Object> paymentData = new HashMap<>();
		paymentData.put("data", payments);
		paymentData.put("recordsFiltered", payments.size());
		paymentData.put("recordsTotal", payments.size());
		paymentData.put("draw", 1);
		return paymentData;
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/prfmpayment", method = RequestMethod.POST)
	public @ResponseBody String performPayment(@RequestBody PaymentRequestDetails paymentRequestDetails, ModelMap model) {
		logger.debug(CLASSNAME + "performPayment" + BEGIN);
		paymentRequestDetails = paymentRequestDetailsRepository.save(paymentRequestDetails);
		return paymentRequestDetails.getId().toString();
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/ccavreq/{id}/{amount}", method = RequestMethod.GET)
	public String ccAvenueRequest(@PathVariable("id") String id, @PathVariable("amount") String amount, ModelMap model) {
		logger.debug(CLASSNAME + "ccAvenueRequest" + BEGIN);
		Map <String,String> propMap = Maps.newHashMap();
		propMap.put("merchant_id", merchantId);
		propMap.put("tid", "");
		propMap.put("order_id", Math.abs(new Random().nextLong())+"");
		propMap.put("currency", "INR");
		propMap.put("amount", amount);
		String qs = "/"+id;
		propMap.put("redirect_url", redirectUrl+qs);
		propMap.put("cancel_url", cancelUrl+qs);
		propMap.put("language","EN");
		logger.debug(CLASSNAME + "ccAvenueRequest : Map {}", propMap);
		try {
			String ccaRequest="";
			for(String key : propMap.keySet()) {
				ccaRequest = ccaRequest + key + "=" + URLEncoder.encode(propMap.get(key),"UTF-8") + "&";
			}
			AesCryptUtil aesUtil=new AesCryptUtil(workingKey);
			String encRequest = aesUtil.encrypt(ccaRequest);
			model.addAttribute("encRequest", encRequest);
			model.addAttribute("access_code", accessCode);
			model.addAttribute("ccavenueUrl", ccavenueUrl);
		} catch(Exception e) {
			logger.error(CLASSNAME + "ccAvenueRequest : ERROR", e);
		}
		return "ccavenue/ccavRequest";
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/ccavreqp/{id}/{amount}", method = RequestMethod.GET)
	public String ccAvenueRequestLink(@PathVariable("id") String id, @PathVariable("amount") String amount, ModelMap model) {
		logger.debug(CLASSNAME + "ccAvenueRequest" + BEGIN);
		Map <String,String> propMap = Maps.newHashMap();
		propMap.put("merchant_id", merchantId);
		propMap.put("tid", "");
		propMap.put("order_id", Math.abs(new Random().nextLong())+"");
		propMap.put("currency", "INR");
		propMap.put("amount", amount);
		String qs = "/payment/"+id;
		propMap.put("redirect_url", redirectUrl+qs);
		propMap.put("cancel_url", cancelUrl+qs);
		propMap.put("language","EN");
		logger.debug(CLASSNAME + "ccAvenueRequest : Map {}", propMap);
		try {
			String ccaRequest="";
			for(String key : propMap.keySet()) {
				ccaRequest = ccaRequest + key + "=" + URLEncoder.encode(propMap.get(key),"UTF-8") + "&";
			}
			AesCryptUtil aesUtil=new AesCryptUtil(workingKey);
			String encRequest = aesUtil.encrypt(ccaRequest);
			model.addAttribute("encRequest", encRequest);
			model.addAttribute("access_code", accessCode);
			model.addAttribute("ccavenueUrl", ccavenueUrl);
		} catch(Exception e) {
			logger.error(CLASSNAME + "ccAvenueRequest : ERROR", e);
		}
		return "ccavenue/ccavRequest";
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/ccavresp/{id}", method = RequestMethod.POST)
	public String ccAvenueResponse(@PathVariable("id") String id, HttpServletRequest request, ModelMap model) {
		final String method = "ccAvenueResponse::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		String encResp= request.getParameter("encResp");
		AesCryptUtil aesUtil=new AesCryptUtil(workingKey);
		String response = aesUtil.decrypt(encResp);
		logger.debug(CLASSNAME + "ccAvenueResponse : Response {}", response);
		PaymentRequestDetails paymentRequestDetails = paymentRequestDetailsRepository.findOne(id);
		if(NullUtil.isNotEmpty(paymentRequestDetails)) {
			logger.debug(CLASSNAME + method + "name\t" + paymentRequestDetails.getName());
			SubscriptionPlan plan = subscriptionService.getPlan(paymentRequestDetails.getPlanid());
			if(NullUtil.isNotEmpty(response)) {
				Map<String, String> responseMap = subscriptionService.processPaymentResponse(paymentRequestDetails, paymentRequestDetails.getUserid(), encResp, response, plan);
				if(NullUtil.isNotEmpty(responseMap)) {
					if(responseMap.containsKey("order_status")) {
						model.addAttribute("type", responseMap.get("order_status"));
					}
					if(responseMap.containsKey("order_id")) {
						model.addAttribute("order_id", responseMap.get("order_id"));
					}
					if(responseMap.containsKey("failure_message")) {
						model.addAttribute("error", responseMap.get("failure_message"));
					}
				}
			}
			updateModel(model, paymentRequestDetails.getUserid(), paymentRequestDetails.getName(), paymentRequestDetails.getUsertype(), 
					paymentRequestDetails.getMonth(), paymentRequestDetails.getYear());
			model.addAttribute("plans", subscriptionService.getSubscriptions(plan.getCategory()));
			model.addAttribute("plan", plan);
		}
		logger.debug(CLASSNAME + method + END);
		return "subscription/subscription";
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/ccavresp/{payment}/{id}", method = RequestMethod.POST)
	public @ResponseBody String ccAvenueResponseP(@PathVariable("payment") String payment,@PathVariable("id") String id, HttpServletRequest request, ModelMap model) {
		final String method = "ccAvenueResponse::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		String encResp= request.getParameter("encResp");
		AesCryptUtil aesUtil=new AesCryptUtil(workingKey);
		String response = aesUtil.decrypt(encResp);
		logger.debug(CLASSNAME + "ccAvenueResponse : Response {}", response);
		PaymentRequestDetails paymentRequestDetails = paymentRequestDetailsRepository.findOne(id);
		if(NullUtil.isNotEmpty(paymentRequestDetails)) {
			logger.debug(CLASSNAME + method + "name\t" + paymentRequestDetails.getName());
			PaymentLink paymentLink = paymentLinkRepository.findByUserid(paymentRequestDetails.getUserid());
			if(NullUtil.isNotEmpty(response)) {
				Map<String, String> responseMap = subscriptionService.processPaymentLinkResponse(paymentRequestDetails, paymentRequestDetails.getUserid(), encResp, response, paymentLink);
				if(NullUtil.isNotEmpty(responseMap)) {
					if(responseMap.containsKey("order_status")) {
						model.addAttribute("type", responseMap.get("order_status"));
					}
					if(responseMap.containsKey("order_id")) {
						model.addAttribute("order_id", responseMap.get("order_id"));
					}
					if(responseMap.containsKey("failure_message")) {
						model.addAttribute("error", responseMap.get("failure_message"));
					}
				}
			}
			updateModel(model, paymentRequestDetails.getUserid(), paymentRequestDetails.getName(), paymentRequestDetails.getUsertype(), 
					paymentRequestDetails.getMonth(), paymentRequestDetails.getYear());
		}
		logger.debug(CLASSNAME + method + END);
		return "subscription/payment";
	}
	
	@RequestMapping(value = "/sbscrpymt/{id}/{usertype}/{amount}/{invoices}/{invRate}/{pymtMode}/{refNo}/{apiType}/{statename}/{paymentSubscriptionType}/{subscriptionStartDate}/{subscriptionExpiryDate}", method = RequestMethod.GET)
	public @ResponseBody void subscriptionPayment(@PathVariable("id") String id, @PathVariable("usertype") String usertype, 
			@PathVariable("amount") Double amount, @PathVariable("invoices") int invoices, @PathVariable("invRate") Double invRate,  
			@PathVariable("pymtMode") String pymtMode, @PathVariable("refNo") String refNo, @PathVariable("apiType") String apiType, 
			@PathVariable("statename") String statename,@PathVariable("paymentSubscriptionType") String paymentSubscriptionType, @PathVariable("subscriptionStartDate") String subscriptionStartDate,@PathVariable("subscriptionExpiryDate") String subscriptionExpiryDate, ModelMap model) throws Exception {
		final String method = "subscriptionPayment::";
		logger.debug(CLASSNAME + method + BEGIN);
		subscriptionService.updateOfflinePaymentDetails(id, usertype, amount, invoices, -1, -1, invRate, pymtMode, refNo, apiType, statename,"",paymentSubscriptionType,subscriptionStartDate,subscriptionExpiryDate);
		logger.debug(CLASSNAME + method + END);
	}
	@RequestMapping(value = "/sbscrpymtsretrivedata/{id}/{apiType}", method = RequestMethod.GET)
	public @ResponseBody SubscriptionDetails subscriptionPaymentDataRetriveBySubscriptionType(@PathVariable("id") String id,
							@PathVariable("apiType") String apiType, ModelMap model) throws Exception {
		final String method = "subscriptionPayment::";
						
		logger.debug(CLASSNAME + method + BEGIN);
		SubscriptionDetails subscriptionDetails=subscriptionService.getSubscriptionPaymentDetails(id,apiType);
		logger.debug(CLASSNAME + method + END);
		if(NullUtil.isNotEmpty(subscriptionDetails)) {
			return subscriptionDetails;	
		}else{
			if("GSTSANDBOXAPI".equals(apiType) || "EWAYBILLSANDBOXAPI".equals(apiType)){
				SubscriptionDetails subscriptionDetailss = new SubscriptionDetails();
				Calendar calss = Calendar.getInstance();
				calss.add(Calendar.DATE, 7);
				Date expiryDate = calss.getTime();
				subscriptionDetailss.setRegisteredDate(new Date());
				subscriptionDetailss.setExpiryDate(expiryDate);
				return subscriptionDetailss;
			}
		}
			
		return null;
	}
	
	@RequestMapping(value = "/sbscrpymtsretrivedata/{id}", method = RequestMethod.GET)
	public @ResponseBody SubscriptionDetails subscriptionPaymentDataRetriveBySubscriptionTypeNonAsp(@PathVariable("id") String id,ModelMap model) throws Exception {
		final String method = "subscriptionPayment::";
						
		logger.debug(CLASSNAME + method + BEGIN);
		SubscriptionDetails subscriptionDetails=subscriptionService.getSubscriptionPaymentDetails(id);
		logger.debug(CLASSNAME + method + END);
		if(NullUtil.isNotEmpty(subscriptionDetails)) {
			return subscriptionDetails;	
		}else{
			
			return null;
		}	
	}
	
	@RequestMapping(value = "/sbscrsuvidhapymt/{id}/{usertype}/{amount}/{invoices}/{clients}/{centers}/{pymtMode}/{refNo}/{statename}/{paymentSubscriptionType}/{subscriptionStartDate}/{subscriptionExpiryDate}", method = RequestMethod.GET)
	public @ResponseBody void subscriptionSuvidhaPayment(@PathVariable("id") String id,
			@PathVariable("usertype") String usertype, @PathVariable("amount") Double amount,
			@PathVariable("invoices") int invoices, @PathVariable("clients") int clients,
			@PathVariable("centers") int centers, @PathVariable("pymtMode") String pymtMode,
			@PathVariable("refNo") String refNo, @PathVariable("statename") String statename,
			@PathVariable("paymentSubscriptionType") String paymentSubscriptionType,
			@PathVariable("subscriptionStartDate") String subscriptionStartDate,
			@PathVariable("subscriptionExpiryDate") String subscriptionExpiryDate, ModelMap model) throws Exception {
		final String method = "subscriptionPayment::";
		logger.debug(CLASSNAME + method + BEGIN);
		subscriptionService.updateOfflinePaymentDetails(id, usertype, amount, invoices, clients, centers,
				amount / invoices, pymtMode, refNo, "", statename, "", paymentSubscriptionType, subscriptionStartDate,
				subscriptionExpiryDate);
		logger.debug(CLASSNAME + method + END);
	}

	@RequestMapping(value = "/sbscrenterprisepymt/{id}/{usertype}/{amount}/{invoices}/{clients}/{pymtMode}/{refNo}/{statename}/{paymentSubscriptionType}/{subscriptionStartDate}/{subscriptionExpiryDate}", method = RequestMethod.GET)
	public @ResponseBody void subscriptionEnterprisePayment(@PathVariable("id") String id,
			@PathVariable("usertype") String usertype, @PathVariable("amount") Double amount,
			@PathVariable("invoices") int invoices, @PathVariable("clients") int clients,
			@PathVariable("pymtMode") String pymtMode, @PathVariable("refNo") String refNo,
			@PathVariable("statename") String statename, @PathVariable("paymentSubscriptionType") String paymentSubscriptionType,
			@PathVariable("subscriptionStartDate") String subscriptionStartDate,
			@PathVariable("subscriptionExpiryDate") String subscriptionExpiryDate, ModelMap model) throws Exception {
		final String method = "subscriptionPayment::";
		logger.debug(CLASSNAME + method + BEGIN);
		subscriptionService.updateOfflinePaymentDetails(id, usertype, amount, invoices, clients, -1, amount / invoices,
				pymtMode, refNo, "", statename, "", paymentSubscriptionType, subscriptionStartDate, subscriptionExpiryDate);
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/sbscrcacmaspymt/{id}/{usertype}/{amount}/{invoices}/{clients}/{pymtMode}/{refNo}/{statename}/{planid}/{subscriptionStartDate}/{subscriptionExpiryDate}", method = RequestMethod.GET)
	public @ResponseBody void subscriptionCacmasPayment(@PathVariable("id") String id,
			@PathVariable("usertype") String usertype, @PathVariable("amount") Double amount,
			@PathVariable("invoices") int invoices, @PathVariable("clients") int clients,
			@PathVariable("pymtMode") String pymtMode, @PathVariable("refNo") String refNo,
			@PathVariable("statename") String statename, @PathVariable("planid") String planid,
			@PathVariable("subscriptionStartDate") String subscriptionStartDate,
			@PathVariable("subscriptionExpiryDate") String subscriptionExpiryDate, ModelMap model) throws Exception {
		final String method = "subscriptionPayment::";
		logger.debug(CLASSNAME + method + BEGIN);
		subscriptionService.updateOfflinePaymentDetails(id, usertype, amount, invoices, clients, -1, amount / invoices,
				pymtMode, refNo, "", statename,planid , "", subscriptionStartDate, subscriptionExpiryDate);
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/sbscrbusinesspymt/{id}/{usertype}/{amount}/{invoices}/{clients}/{pymtMode}/{refNo}/{statename}/{planid}/{subscriptionStartDate}/{subscriptionExpiryDate}", method = RequestMethod.GET)
	public @ResponseBody void subscriptionBussinessPayment(@PathVariable("id") String id,
			@PathVariable("usertype") String usertype, @PathVariable("amount") Double amount,
			@PathVariable("invoices") int invoices, @PathVariable("clients") int clients,
			@PathVariable("pymtMode") String pymtMode, @PathVariable("refNo") String refNo,
			@PathVariable("statename") String statename, @PathVariable("planid") String planid,
			@PathVariable("subscriptionStartDate") String subscriptionStartDate,
			@PathVariable("subscriptionExpiryDate") String subscriptionExpiryDate, ModelMap model) throws Exception {
		final String method = "subscriptionPayment::";
		logger.debug(CLASSNAME + method + BEGIN);
		subscriptionService.updateOfflinePaymentDetails(id, usertype, amount, invoices, clients, -1, amount / invoices,
				pymtMode, refNo, "", statename, planid, "", subscriptionStartDate, subscriptionExpiryDate);
		logger.debug(CLASSNAME + method + END);
	}

	@RequestMapping(value = "/sbscrfreepln/{id}/{usertype}/{invoices}/{clients}/{days}/{planid}/{subscriptionStartDate}/{subscriptionExpiryDate}", method = RequestMethod.GET)
	public @ResponseBody void subscriptionFreePlan(@PathVariable("id") String id,
			@PathVariable("usertype") String usertype, @PathVariable("invoices") int invoices,
			@PathVariable("clients") int clients, @PathVariable("days") int days, @PathVariable("planid") String planid,
			@PathVariable("subscriptionStartDate") String subscriptionStartDate,
			@PathVariable("subscriptionExpiryDate") String subscriptionExpiryDate, ModelMap model) throws Exception {
		final String method = "subscriptionFreePlan::";
		logger.debug(CLASSNAME + method + BEGIN);
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		SubscriptionDetails subscriptionDetails = subscriptionService.getUserSubscriptionDetails(id);
		if(NullUtil.isEmpty(subscriptionDetails)) {
			subscriptionDetails = new SubscriptionDetails();
			subscriptionDetails.setUserid(id);
			subscriptionDetails.setRegisteredDate(today);
		}
		subscriptionDetails.setPlanid(planid);
		subscriptionDetails.setAllowedClients(clients);
		subscriptionDetails.setAllowedInvoices(invoices);
		subscriptionDetails.setPaidAmount(0d);
		subscriptionDetails.setInvoiceCost(0d);
		cal.add(Calendar.DATE, days);
		Date expiryDate = cal.getTime();
		subscriptionDetails.setExpiryDate(expiryDate);
		
		
		subscriptionService.updateSubscriptionData(subscriptionDetails);
		logger.debug(CLASSNAME + method + END);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/coupons/{code}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> coupons(@PathVariable("code") String code) throws Exception {
		String method = "payments :: ";
		logger.debug(CLASSNAME + method + BEGIN);
		Coupons coupons = couponService.getCoupon(code);
		Map<String, Object> couponData = new HashMap<>();
		couponData.put("data", coupons);
		return couponData;
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/coupons", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Coupons> coupons() throws Exception {
		String method = "payments :: ";
		logger.debug(CLASSNAME + method + BEGIN);
		List<Coupons> coupon = couponService.getAllCoupon();
		return coupon;
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/getCaPlans/{name}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getCaPlans(@PathVariable("name") String name) throws Exception {
		String method = "getCaPlans :: ";
		logger.debug(CLASSNAME + method + BEGIN);
		SubscriptionPlan plan = subscriptionService.getPlanByName(name);
		String planid = plan.getId().toString();
		Map<String, Object> planData = new HashMap<>();
		planData.put("data", plan);
		planData.put("planid", planid);
		return planData;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/getBusinessPlans/{name}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getBussinessPlans(@PathVariable("name") String name) throws Exception {
		String method = "getCaPlans :: ";
		logger.debug(CLASSNAME + method + BEGIN);
		SubscriptionPlan plan = subscriptionService.getPlanByNameForBussiness(name);
		String planid = plan.getId().toString();
		Map<String, Object> planData = new HashMap<>();
		planData.put("data", plan);
		planData.put("planid", planid);
		return planData;
	}

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value="/suvidhaPaymentData/{userId}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Integer getSuvidhaPaymentData(@PathVariable String userId) {
		
		List<PaymentDetails> payments = subscriptionService.getPaymentDetails(userId);
				
		return payments.size();
	}

	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/dbilling/{id}/{name}/{apiType}/{usertype}/{month}/{year}/{type}", method = RequestMethod.GET)
	public String getAllASPPaymentDetails(@PathVariable String apiType,@PathVariable("id") String id, @PathVariable("name") String fullname,
				@PathVariable("usertype") String usertype, @PathVariable("month") int month, @PathVariable("year") int year,
				@PathVariable("type")String type,ModelMap map) {
		
		String method = "billing :: ";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(map, id, fullname, usertype, month, year);
		map.addAttribute("otheruser", "no");
		SubscriptionDetails subscriptionDetails = subscriptionService.getSubscriptionData(id,apiType);
		
		User user=userService.getUserById(id);
		
		if(NullUtil.isNotEmpty(user.getCreatedDate())){
			
			map.addAttribute("userregisterdate", user.getCreatedDate());
		}
		
		map.addAttribute("subscription", subscriptionDetails);
		
		List<PaymentDetails> paymentList=subscriptionService.getPaymentDetails(id, apiType);
		map.addAttribute("paymentList", paymentList);

		/*List<SubscriptionDetails> subscriptionDetailsList = subscriptionService.getGSTAPIsSubscriptionDetailsApiTye(id);
		if (NullUtil.isNotEmpty(subscriptionDetailsList)) {
			
			map.addAttribute("subscriptionDetailsList", subscriptionDetailsList);
		}*/	
			List<Message> messages = messageService.getMessagesByUserType(MasterGSTConstants.ASPDEVELOPER);
			messages.forEach((m) -> m.setMsgId(m.getId().toString()));
			map.addAttribute("messages", messages);
			map.addAttribute("messageCount", messages.size());
			//below line mandatory apiType header tab purpose "gstapis"
			map.addAttribute("type", type);
		
		
		return "subscription/billing-details";
	}
	
	
	@RequestMapping(value = "/suvidhacenterpayments", method = RequestMethod.GET)
	public @ResponseBody SubscriptionDetails suvidhaCentersPaymentsDetails(@RequestParam("email") String email) {
		
		User user=userService.findByEmail(email);
		
		if(NullUtil.isNotEmpty(user)) {
			if(NullUtil.isNotEmpty(user.getId())) {
				SubscriptionDetails subscriptionDetails=subscriptionService.getUserSubscriptionDetails(user.getId().toString());
				if(NullUtil.isNotEmpty(subscriptionDetails)) {
					
					return subscriptionDetails;
				}else {
					return null;
				}
			}else {
				return null;
			}
		}else {
			return null;
		}
	}
	
	
	@RequestMapping(value = "/activeUsers", method = RequestMethod.GET)
	public String activeUsersReports(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "usertype", required = true) String userType, ModelMap model) throws Exception {
		final String method = "activeUsersReports::";
		logger.debug(CLASSNAME + method + BEGIN);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", userType);
		logger.debug(CLASSNAME + method + END);
		
		return "admin/activeUsers";
	}
	
	@RequestMapping(value = "/demoUsers", method = RequestMethod.GET)
	public String demoUsersReports(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "usertype", required = true) String userType, ModelMap model) throws Exception {
		final String method = "activeUsersReports::";
		logger.debug(CLASSNAME + method + BEGIN);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", userType);
		logger.debug(CLASSNAME + method + END);
		
		return "admin/demoUsers";
	}
	
	@RequestMapping(value = "/activeUsersReportData", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> activeUsersReportData(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "usertype", required = true) String userType, ModelMap model) throws Exception {
		String method = "activeUsersReportData ::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "userType\t" + userType);
		Map<String, Object> mapData = new HashMap<>();
		
		List<User> users=subscriptionService.getPaidUsersAndActiveUsers();
		users.forEach(usr->usr.setType(usr.getType().toUpperCase()));
		mapData.put("data", users);
		mapData.put("recordsFiltered", users.size());
		mapData.put("recordsTotal", users.size());
		mapData.put("draw", 1);
		
		return mapData;
	}
	
	
	@RequestMapping(value = "/demoUsersReportData", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> demoUsersReportData(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "usertype", required = true) String userType, ModelMap model) throws Exception {
		String method = "activeUsersReportData ::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "userType\t" + userType);
		Map<String, Object> mapData = new HashMap<>();
		
		List<User> users=subscriptionService.getDemoUsers();
		users.forEach(usr->usr.setType(usr.getType().toUpperCase()));
		mapData.put("data", users);
		mapData.put("recordsFiltered", users.size());
		mapData.put("recordsTotal", users.size());
		mapData.put("draw", 1);
		
		return mapData;
	}
}
