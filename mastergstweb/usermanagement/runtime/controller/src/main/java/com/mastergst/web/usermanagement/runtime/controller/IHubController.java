/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR3B;
import static com.mastergst.core.common.MasterGSTConstants.GSTR4;
import static com.mastergst.core.common.MasterGSTConstants.GSTR6;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR1DocumentIssue;
import com.mastergst.usermanagement.runtime.domain.GSTR3B;
import com.mastergst.usermanagement.runtime.domain.GSTR3BOffsetLiabilityCash;
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6;
import com.mastergst.usermanagement.runtime.domain.GSTROffsetLiability;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.TurnoverOptions;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.repository.GSTINPublicDataRepository;
import com.mastergst.usermanagement.runtime.response.ANX2Response;
import com.mastergst.usermanagement.runtime.response.DigioResponse;
import com.mastergst.usermanagement.runtime.response.LedgerResponse;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.service.Anx1Service;
import com.mastergst.usermanagement.runtime.service.Anx2Service;
import com.mastergst.usermanagement.runtime.response.ResponseData;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.IHubConsumerService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;

/**
 * Handles all ihub invocation calls.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 * @since 1.0
 */
@Controller
public class IHubController {

	private static final Logger logger = LogManager.getLogger(IHubController.class.getName());
	private static final String CLASSNAME = "IHubController::";
	
	@Autowired
	private Anx2Service anx2Service;
	@Autowired
	private GSTINPublicDataRepository gstinPublicDataRepository;
	@Autowired
	private ClientService clientService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private IHubConsumerService iHubConsumerService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProfileService profileService;
	@Autowired
	private Anx1Service anx1Service;
	
	@Autowired
	ClientUserMappingRepository clientUserMappingRepository;
	
	public String primaryHolderMessage(Client client, User usr) {
		
		String message = "Primary Account Holder of <span style='color:blue;'>"+client.getBusinessname()+"</span> is Subscription is expired, please contact <span style='color:blue'>"+usr.getFullname()+","+usr.getEmail()+" & "+usr.getMobilenumber()+"</span> to renew";
		return message;
	}
	
	@RequestMapping(value = "/ihubotp/{clientid}", method = RequestMethod.GET)
	public @ResponseBody Response otpRequest(@PathVariable("clientid") String clientid, ModelMap model)
			throws Exception {
		final String method = "otpRequest::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		return iHubConsumerService.otpRequest(client.getStatename(), client.getGstname(), null);
	}

	@RequestMapping(value = "/verifyotp", method = RequestMethod.GET)
	public @ResponseBody Response otpRequest(@RequestParam("state") String state,
			@RequestParam("gstName") String gstName, ModelMap model)
					throws Exception {
		final String method = "otpRequest::";
		logger.debug(CLASSNAME + method + BEGIN);
		return iHubConsumerService.otpRequest(state, gstName, InetAddress.getLocalHost().getHostAddress());
	}

	@RequestMapping(value = "/ihubauth/{otp}", method = RequestMethod.POST)
	public @ResponseBody Response authRequest(@RequestBody Response response, @PathVariable("otp") String otp,
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "authRequest::";
		logger.debug(CLASSNAME + method + BEGIN);
		//request.getSession().removeAttribute("tokenError");
		return iHubConsumerService.authRequest(otp, response);
	}

	@RequestMapping(value = "/ihubretsum/{id}/{clientid}/{returntype}", method = RequestMethod.GET)
	public @ResponseBody Response iHubRetSummary(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
			@RequestParam("month") int month, @RequestParam("year") int year, ModelMap model) throws Exception {
		final String method = "iHubRetSummary::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (year <= 0) {
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
		}
		String strMonth = (month) < 10 ? "0" + (month) : (month) + "";
		String retPeriod = strMonth + year;
		Client client = clientService.findById(clientid);
		String gstn = client.getGstnnumber();
		if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
			Response latestGenerateResponse = null;
			try {
			latestGenerateResponse = iHubConsumerService.returnSubmit(client, client.getGstnnumber(), returntype, retPeriod, id,true,true);
			if (isNotEmpty(latestGenerateResponse) && isNotEmpty(latestGenerateResponse.getStatuscd()) && (latestGenerateResponse.getStatuscd().equals("1") || (latestGenerateResponse.getStatuscd().equals("0") && isNotEmpty(latestGenerateResponse.getError()) && isNotEmpty(latestGenerateResponse.getError().getErrorcd()) && (latestGenerateResponse.getError().getErrorcd().equalsIgnoreCase("RET09008") || latestGenerateResponse.getError().getErrorcd().equalsIgnoreCase("RET09007"))))){
					Response response = null;
					try {
						if(returntype.equals(MasterGSTConstants.ANX1)) {
							response = iHubConsumerService.anx1ReturnSummary(client, gstn, retPeriod, id, returntype, true);
						}else {
							response = iHubConsumerService.returnSummary(client, gstn, retPeriod, id, returntype, true);
						}
						if (isNotEmpty(returntype) && returntype.equals(GSTR4)) {
							GSTROffsetLiability offsetLiability = clientService.getGSTROffsetLiability(gstn, retPeriod, returntype);
							if (isEmpty(offsetLiability)) {
								offsetLiability = new GSTROffsetLiability();
								offsetLiability.setGstin(gstn);
								offsetLiability.setRetPeriod(retPeriod);
							}
							if (isNotEmpty(response) && isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals("1")
									&& isNotEmpty(response.getData()) && isNotEmpty(response.getData().getTaxPyPd())
									&& isNotEmpty(response.getData().getTaxPyPd().getTaxPay())) {
								offsetLiability.setOpenLiab(response.getData().getTaxPyPd().getTaxPay());
							}
						}
						return response;
					} catch (Exception e) {
						response = new Response();
						response.setStatuscd("0");
						response.setStatusdesc(e.getMessage());
						return response;
					}
				}else {
					return latestGenerateResponse;
				}
			}catch (Exception e) {
				latestGenerateResponse = new Response();
				latestGenerateResponse.setStatuscd("0");
				latestGenerateResponse.setStatusdesc(e.getMessage());
				return latestGenerateResponse;
			}
		}else {
			Response response = null;
			try {
				if(returntype.equals(MasterGSTConstants.ANX1)) {
					response = iHubConsumerService.anx1ReturnSummary(client, gstn, retPeriod, id, returntype, true);
				}else {
					response = iHubConsumerService.returnSummary(client, gstn, retPeriod, id, returntype, true);
				}
				if (isNotEmpty(returntype) && returntype.equals(GSTR4)) {
					GSTROffsetLiability offsetLiability = clientService.getGSTROffsetLiability(gstn, retPeriod, returntype);
					if (isEmpty(offsetLiability)) {
						offsetLiability = new GSTROffsetLiability();
						offsetLiability.setGstin(gstn);
						offsetLiability.setRetPeriod(retPeriod);
					}
					if (isNotEmpty(response) && isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals("1")
							&& isNotEmpty(response.getData()) && isNotEmpty(response.getData().getTaxPyPd())
							&& isNotEmpty(response.getData().getTaxPyPd().getTaxPay())) {
						offsetLiability.setOpenLiab(response.getData().getTaxPyPd().getTaxPay());
					}
				}
				return response;
			} catch (Exception e) {
				response = new Response();
				response.setStatuscd("0");
				response.setStatusdesc(e.getMessage());
				return response;
			}
		}
	}

	@RequestMapping(value = "/ihubothsum/{id}/{clientid}/{returntype}", method = RequestMethod.GET)
	public @ResponseBody Response iHubOtherSummary(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
			@RequestParam("month") int month, @RequestParam("year") int year, ModelMap model) throws Exception {
		final String method = "iHubOtherSummary::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (year <= 0) {
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
		}
		String strMonth = (month) < 10 ? "0" + (month) : (month) + "";
		String retPeriod = strMonth + year;
		Client client = clientService.findById(clientid);
		String gstn = client.getGstnnumber();
		User user = userService.findById(id);
		String usrid = id;
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser.getCompany())){
				if(companyUser.getCompany().contains(clientid)){
					usrid = user.getParentid();
				}
			}
		}
		String userid = userid(id,clientid);
		if (!subscriptionService.allowUploadInvoices(userid, 1l)) {
			Response errorResponse = new Response();
			errorResponse.setStatuscd("0");
			if(usrid.equals(user.getParentid())){
				User usr = userRepository.findById(userid);
				String errormsg = "";
				if(isNotEmpty(usr)) {
					errormsg = primaryHolderMessage(client,usr);
				}else {
					errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
				}
				errorResponse.setStatusdesc(errormsg);
			}else{
				errorResponse.setStatusdesc("Your subscription has expired. Kindly subscribe to proceed further!");
			}
			return errorResponse;
		}
		Response response = iHubConsumerService.returnHSNSummary(client, gstn, returntype, retPeriod, id, true);
		if (returntype.equals(GSTR1)) {
			Response docResponse = iHubConsumerService.returnDocIssue(client, gstn, retPeriod, id, true);
			if (isNotEmpty(docResponse) && isNotEmpty(docResponse.getData())
					&& isNotEmpty(docResponse.getData().getDocIssue())) {
				if(isNotEmpty(response) && isNotEmpty(response.getData())) {
					response.getData().setDocIssue(docResponse.getData().getDocIssue());
				}else {
					response = docResponse;
				}
				
			}
		} /*else if (returntype.equals(GSTR6)) {
			GSTR6 itcResponse = (GSTR6) iHubConsumerService.getGSTRXInvoices(client, gstn, month, year, returntype, "itc", null, id, true);
			if (isNotEmpty(itcResponse) && isNotEmpty(itcResponse.getItcDetails())
					&& isNotEmpty(itcResponse.getItcDetails().getTotalItc())) {
				response.getData().setItcDetails(itcResponse.getItcDetails());
			}
		}*/
		return response;
	}

	@RequestMapping(value = "/ihubsdocissue/{id}/{clientid}/{returntype}", method = RequestMethod.GET)
	public @ResponseBody Response iHubSaveDocumentIssue(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
			@RequestParam("month") int month, @RequestParam("year") int year, ModelMap model) throws Exception {
		final String method = "iHubSaveDocumentIssue::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		if (isNotEmpty(client)) {
			User user = userService.findById(id);
			String usrid = id;
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
				CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
				if(isNotEmpty(companyUser.getCompany())){
					if(companyUser.getCompany().contains(clientid)){
						usrid = user.getParentid();
					}
				}
			}
			String userid = userid(id,clientid);
			if (!subscriptionService.allowUploadInvoices(userid, 1l)) {
				Response errorResponse = new Response();
				errorResponse.setStatuscd("0");
				if(usrid.equals(user.getParentid())){
					User usr = userRepository.findById(userid);
					String errormsg = "";
					if(isNotEmpty(usr)) {
						errormsg = primaryHolderMessage(client,usr);
					}else {
						errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
					}
					errorResponse.setStatusdesc(errormsg);
				}else{
					errorResponse.setStatusdesc("Your subscription has expired. Kindly subscribe to proceed further!");
				}
				return errorResponse;
			}
			String strMonth = month < 10 ? "0" + month : month + "";
			String returnPeriod = strMonth + year;
			GSTR1DocumentIssue docIssue = clientService.getDocumentIssue(clientid, returnPeriod);
			if (isNotEmpty(docIssue)) {
				GSTR1 invoice = new GSTR1();
				invoice.setGstin(client.getGstnnumber());
				int imonth = Integer.parseInt(returnPeriod.substring(0, 1));
				int iyear = Integer.parseInt(returnPeriod.substring(2))-1;
				String strYear="";
				if(imonth < 4) {
					if(iyear == 2016 || iyear == 2017) {
						if(imonth < 4) {
							strYear="APR2017-JUN2017";
						}else {
							strYear="2017-2018";
						}
					}else {
						iyear = iyear-1;
						strYear=iyear+"-"+(iyear+1);
					}
				}else {
					strYear=iyear+"-"+(iyear+1);
				}
				Double turnOver = 0d;
				Double curtgt = 0d;
				if(isNotEmpty(client) && isNotEmpty(client.getTurnovergoptions())) {
					//String strYear = iyear+"";
					for(TurnoverOptions turnoverOptions : client.getTurnovergoptions()) {
						if(isNotEmpty(turnoverOptions)) {
							if(strYear.equals(turnoverOptions.getYear()) ) {
								turnOver = turnoverOptions.getTurnover();
							}
							if("APR2017-JUN2017".equals(turnoverOptions.getYear())) {
								curtgt = turnoverOptions.getTurnover();
							}
						}
					}
				}
				//System.out.println(turnOver);
				invoice.setGt(turnOver);
				invoice.setCurGt(curtgt);
				invoice.setFp(returnPeriod);
				invoice.setDocIssue(docIssue);
				Response saveResponse = null;
				try {
					saveResponse = iHubConsumerService.saveReturns(invoice, client.getStatename(), client.getGstname(),
							client.getGstnnumber(), returnPeriod, returntype, true);
					if (isNotEmpty(saveResponse.getStatuscd()) && saveResponse.getStatuscd().equals("1")) {
						String refId = saveResponse.getData().getReferenceId();
						return iHubConsumerService.returnStatus(refId, client.getStatename(), client.getGstname(),
								client.getGstnnumber(), returnPeriod, true);
					}
					return saveResponse;
				} catch (Exception e) {
					saveResponse = new Response();
					saveResponse.setStatuscd("0");
					saveResponse.setStatusdesc(e.getMessage());
					return saveResponse;
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
		return new Response();
	}

	@RequestMapping(value = "/ihubsavestatus/{id}/{usertype}/{clientid}/{returntype}", method = RequestMethod.POST)
	public @ResponseBody Response iHubSelInvoicesStatus( @RequestBody List<String> invoices,@PathVariable("id") String id,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @RequestParam("month") int month,
			@RequestParam("year") int year, @RequestParam("hsn") String hsnSum,ModelMap model)
					throws Exception {
		final String method = "iHubSelInvoicesStatus::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(id);
		String usrid = id;
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getCompany())){
				if(companyUser.getCompany().contains(clientid)){
					usrid = user.getParentid();
				}
			}
		}
		String userid = userid(id,clientid);
		if (isEmpty(invoices)) {
			Client client = clientService.findById(clientid);
			List<? extends InvoiceParent> invoiceList = clientService.getInvoices(null, client, returntype, month, year,
					MasterGSTConstants.GST_STATUS_SUCCESS);
			if (!subscriptionService.allowUploadInvoices(userid, invoiceList.size() * 1l)) {
				Response errorResponse = new Response();
				errorResponse.setStatuscd("0");
				if(usrid.equals(user.getParentid())){
					User usr = userRepository.findById(userid);
					String errormsg = "";
					if(isNotEmpty(usr)) {
						errormsg = primaryHolderMessage(client,usr);
					}else {
						errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
					}
					errorResponse.setStatusdesc(errormsg);
				}else{
					errorResponse.setStatusdesc("Your subscription has expired. Kindly subscribe to proceed further!");
				}
				return errorResponse;
			}
		} else if (!subscriptionService.allowUploadInvoices(userid, invoices.size() * 1l)) {
			Client client = clientService.findById(clientid);
			Response errorResponse = new Response();
			errorResponse.setStatuscd("0");
			if(usrid.equals(user.getParentid())){
				User usr = userRepository.findById(userid);
				String errormsg = "";
				if(isNotEmpty(usr)) {
					errormsg = primaryHolderMessage(client,usr);
				}else {
					errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
				}
				errorResponse.setStatusdesc(errormsg);
			}else{
				errorResponse.setStatusdesc("Your subscription has expired. Kindly subscribe to proceed further!");
			}
			return errorResponse;
		}
		Response response = null;
		try {
			if(returntype.equalsIgnoreCase(MasterGSTConstants.ANX1)) {
				response = anx1Service.fetchUploadStatus(id, usertype, clientid, returntype, month, year, invoices);
			}else {
				response = clientService.fetchUploadStatus(id, usertype, clientid, returntype, month, year, invoices,hsnSum);
			}
			
			return response;
		} catch (Exception e) {
			response = new Response();
			response.setStatuscd("0");
			response.setStatusdesc(e.getMessage());
			return response;
		}
	}

	@RequestMapping(value = "/ihubsaveANX2status/{id}/{usertype}/{clientid}/{returntype}", method = RequestMethod.GET)
	public @ResponseBody ANX2Response iHubANX2InvoicesStatus(@PathVariable("id") String id,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @RequestParam("month") int month,
			@RequestParam("year") int year, @RequestParam("invoices") List<String> invoices, ModelMap model)
					throws Exception {
		final String method = "iHubSelInvoicesStatus::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(id);
		String usrid = id;
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser.getCompany())){
				if(companyUser.getCompany().contains(clientid)){
					usrid = user.getParentid();
				}
			}
		}
		String userid = userid(id,clientid);
		if (isEmpty(invoices)) {
			Client client = clientService.findById(clientid);
			List<? extends InvoiceParent> invoiceList = clientService.getInvoices(null, client, returntype, month, year,
					MasterGSTConstants.GST_STATUS_SUCCESS);
			if (!subscriptionService.allowUploadInvoices(userid, invoiceList.size() * 1l)) {
				ANX2Response errorResponse = new ANX2Response();
				errorResponse.setStatuscd("0");
				if(usrid.equals(user.getParentid())){
					User usr = userRepository.findById(userid);
					String errormsg = "";
					if(isNotEmpty(usr)) {
						errormsg = primaryHolderMessage(client,usr);
					}else {
						errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
					}
					errorResponse.setStatusdesc(errormsg);
				}else{
					errorResponse.setStatusdesc("Your subscription has expired. Kindly subscribe to proceed further!");
				}
				return errorResponse;
			}
		} else if (!subscriptionService.allowUploadInvoices(userid, invoices.size() * 1l)) {
			Client client = clientService.findById(clientid);
			ANX2Response errorResponse = new ANX2Response();
			errorResponse.setStatuscd("0");
			if(usrid.equals(user.getParentid())){
				User usr = userRepository.findById(userid);
				String errormsg = "";
				if(isNotEmpty(usr)) {
					errormsg = primaryHolderMessage(client,usr);
				}else {
					errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
				}
				errorResponse.setStatusdesc(errormsg);
			}else{
				errorResponse.setStatusdesc("Your subscription has expired. Kindly subscribe to proceed further!");
			}
			return errorResponse;
		}
		ANX2Response response = null;
		try {
			if(returntype.equalsIgnoreCase(MasterGSTConstants.ANX2)) {
				response = anx2Service.fetchANX2UploadStatus(id, usertype, clientid, returntype, month, year, invoices);
			}
			return response;
		} catch (Exception e) {
			response = new ANX2Response();
			response.setStatuscd("0");
			response.setStatusdesc(e.getMessage());
			return response;
		}
	}
	
	
	@RequestMapping(value = "/ihubsaveoffliab/{id}/{clientid}/{returntype}/{invId}", method = RequestMethod.GET)
	public @ResponseBody Response iHubSaveOffsetLiability(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
			@PathVariable("invId") String invId, @RequestParam("month") int month, @RequestParam("year") int year,
			ModelMap model) throws Exception {
		final String method = "iHubSaveOffsetLiability::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(id);
		String usrid = id;
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser.getCompany())){
				if(companyUser.getCompany().contains(clientid)){
					usrid = user.getParentid();
				}
			}
		}
		String userid = userid(id,clientid);
		if (!subscriptionService.allowUploadInvoices(userid, 1l)) {
			Client client = clientService.findById(clientid);
			Response errorResponse = new Response();
			errorResponse.setStatuscd("0");
			if(usrid.equals(user.getParentid())){
				User usr = userRepository.findById(userid);
				String errormsg = "";
				if(isNotEmpty(usr)) {
					errormsg = primaryHolderMessage(client,usr);
				}else {
					errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
				}
				errorResponse.setStatusdesc(errormsg);
			}else{
				errorResponse.setStatusdesc("Your subscription has expired. Kindly subscribe to proceed further!");
			}
			return errorResponse;
		}
		if (year <= 0) {
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
		}
		String strMonth = (month) < 10 ? "0" + (month) : (month) + "";
		String retPeriod = strMonth + year;
		Client client = clientService.findById(clientid);
		String gstn = client.getGstnnumber();
		Object offsetLiability = null;
		if (isNotEmpty(returntype) && returntype.equals(GSTR3B)) {
			GSTR3B gstr3b = clientService.getSuppliesInvoice(invId);
			if (isNotEmpty(gstr3b.getOffLiab())) {
				Integer liabId = null, transType = null;
				if (isNotEmpty(gstr3b.getOffLiab().getTaxPayable())) {
					liabId = gstr3b.getOffLiab().getTaxPayable().get(0).getLiabLdgId();
					transType = gstr3b.getOffLiab().getTaxPayable().get(0).getTransType();
					gstr3b.getOffLiab().setTaxPayable(Lists.newArrayList());
				}
				if (isNotEmpty(gstr3b.getOffLiab().getPdcash())) {
					for (GSTR3BOffsetLiabilityCash cash : gstr3b.getOffLiab().getPdcash()) {
						if (liabId != null) {
							cash.setLiabLdgId(liabId);
						} else {
							cash.setLiabLdgId(0);
						}
						if (transType != null) {
							cash.setTransType(transType);
						} else {
							cash.setTransType(3002);
						}
						if (isEmpty(cash.getIpd())) {
							cash.setIpd(0d);
						}
						if (isEmpty(cash.getCpd())) {
							cash.setCpd(0d);
						}
						if (isEmpty(cash.getSpd())) {
							cash.setSpd(0d);
						}
						if (isEmpty(cash.getCspd())) {
							cash.setCspd(0d);
						}
						if (isEmpty(cash.getIgstIntrpd())) {
							cash.setIgstIntrpd(0d);
						}
						if (isEmpty(cash.getCgstIntrpd())) {
							cash.setCgstIntrpd(0d);
						}
						if (isEmpty(cash.getSgstIntrpd())) {
							cash.setSgstIntrpd(0d);
						}
						if (isEmpty(cash.getCessIntrpd())) {
							cash.setCessIntrpd(0d);
						}
						if (isEmpty(cash.getCgstLfeepd())) {
							cash.setCgstLfeepd(0d);
						}
						if (isEmpty(cash.getSgstLfeepd())) {
							cash.setSgstLfeepd(0d);
						}
					}
				}
				if (isNotEmpty(gstr3b.getOffLiab().getPditc())) {
					if (liabId != null) {
						gstr3b.getOffLiab().getPditc().setLiabLdgId(liabId);
					} else {
						gstr3b.getOffLiab().getPditc().setLiabLdgId(0);
					}
					if (transType != null) {
						gstr3b.getOffLiab().getPditc().setTransType(transType);
					} else {
						gstr3b.getOffLiab().getPditc().setTransType(3002);
					}
					if (isEmpty(gstr3b.getOffLiab().getPditc().getIgstPdigst())) {
						gstr3b.getOffLiab().getPditc().setIgstPdigst(0d);
					}
					if (isEmpty(gstr3b.getOffLiab().getPditc().getIgstPdcgst())) {
						gstr3b.getOffLiab().getPditc().setIgstPdcgst(0d);
					}
					if (isEmpty(gstr3b.getOffLiab().getPditc().getIgstPdsgst())) {
						gstr3b.getOffLiab().getPditc().setIgstPdsgst(0d);
					}
					if (isEmpty(gstr3b.getOffLiab().getPditc().getCgstPdigst())) {
						gstr3b.getOffLiab().getPditc().setCgstPdigst(0d);
					}
					if (isEmpty(gstr3b.getOffLiab().getPditc().getCgstPdcgst())) {
						gstr3b.getOffLiab().getPditc().setCgstPdcgst(0d);
					}
					if (isEmpty(gstr3b.getOffLiab().getPditc().getSgstPdigst())) {
						gstr3b.getOffLiab().getPditc().setSgstPdigst(0d);
					}
					if (isEmpty(gstr3b.getOffLiab().getPditc().getSgstPdsgst())) {
						gstr3b.getOffLiab().getPditc().setSgstPdsgst(0d);
					}
					if (isEmpty(gstr3b.getOffLiab().getPditc().getCessPdcess())) {
						gstr3b.getOffLiab().getPditc().setCessPdcess(0d);
					}
				}
			}
			offsetLiability = gstr3b.getOffLiab();
		} else if (isNotEmpty(returntype) && returntype.equals(GSTR4)) {
			offsetLiability = clientService.getGSTROffsetLiability(gstn, retPeriod, returntype);
		} else if (isNotEmpty(returntype) && returntype.equals(GSTR6)) {
			GSTR6 gstr6 = (GSTR6) iHubConsumerService.getGSTRXInvoices(client, gstn, month, year, returntype, "latefee", null, id, true);
			if(isNotEmpty(gstr6) && isNotEmpty(gstr6.getLatefee()) && isNotEmpty(gstr6.getLatefee().getForoffset())) {
				Map<String, Object> offsetLatefee = Maps.newHashMap();
				offsetLatefee.put("gstin", gstn);
				offsetLatefee.put("ret_period", retPeriod);
				offsetLatefee.put("ret_type", "R6");
				offsetLatefee.put("liab_id", gstr6.getLatefee().getForoffset().getLiabId());
				offsetLatefee.put("tran_cd", gstr6.getLatefee().getForoffset().getTranCd());
				if(isNotEmpty(gstr6.getLatefee().getiLamt())) {
					offsetLatefee.put("igstfee", gstr6.getLatefee().getiLamt());
				}
				if(isNotEmpty(gstr6.getLatefee().getcLamt())) {
					offsetLatefee.put("cgstfee", gstr6.getLatefee().getcLamt());
				}
				if(isNotEmpty(gstr6.getLatefee().getsLamt())) {
					offsetLatefee.put("sgstfee", gstr6.getLatefee().getsLamt());
				}
				return iHubConsumerService.offsetLiability(client, gstn, returntype, retPeriod, id, offsetLatefee);
			}
		}
		if (isNotEmpty(offsetLiability)) {
			return iHubConsumerService.offsetLiability(client, gstn, returntype, retPeriod, id, offsetLiability);
		} else {
			Response response = new Response();
			response.setStatuscd("0");
			response.setStatusdesc("Unable to generate Offset Liability");
			return response;
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/signrtns/{clientid}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody DigioResponse uploadSignDocument(@PathVariable("clientid") String clientid,
			@RequestBody String uploadData) {
		logger.debug(CLASSNAME + "uploadSignDocument:: Begin");
		logger.debug(CLASSNAME + "uploadSignDocument:: uploadData\t" + uploadData);
		return iHubConsumerService.signDocument(uploadData, clientid, true);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/signkycrtns/{clientid}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody DigioResponse uploadSignKYCDocument(@PathVariable("clientid") String clientid,
			@RequestBody String uploadData) {
		logger.debug(CLASSNAME + "uploadSignKYCDocument:: Begin");
		logger.debug(CLASSNAME + "uploadSignKYCDocument:: uploadData\t" + uploadData);
		return iHubConsumerService.signDocument(uploadData, clientid, false);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/getsigndoc/{signid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody DigioResponse fetchSignedDocument(@PathVariable("signid") String signid) {
		logger.debug(CLASSNAME + "fetchSignedDocument:: Begin");
		logger.debug(CLASSNAME + "fetchSignedDocument:: signid {}", signid);
		return iHubConsumerService.getSignedDocument(signid);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/fotpevc/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Response evcOTP(@PathVariable("id") String id, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month,
			@PathVariable("year") int year) {
		logger.debug(CLASSNAME + "evcOTP:: Begin");
		Client client = clientService.findById(clientid);
		User user = userService.findById(id);
		String usrid = id;
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser.getCompany())){
				if(companyUser.getCompany().contains(clientid)){
					usrid = user.getParentid();
				}
			}
		}
		String userid = userid(id,clientid);
		if (!subscriptionService.allowUploadInvoices(userid, 1l)) {
			Response errorResponse = new Response();
			errorResponse.setStatuscd("0");
			if(usrid.equals(user.getParentid())){
				User usr = userRepository.findById(userid);
				String errormsg = "";
				if(isNotEmpty(usr)) {
					errormsg = primaryHolderMessage(client,usr);
				}else {
					errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
				}
				errorResponse.setStatusdesc(errormsg);
			}else{
				errorResponse.setStatusdesc("Your subscription has expired. Kindly subscribe to proceed further!");
			}
			return errorResponse;
		}
		String gstn = client.getGstnnumber();
		return iHubConsumerService.returnOTPForEVC(client, gstn, returntype, id, true);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/fretevcfile/{id}/{clientid}/{returntype}/{evcotp}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody Response returnEVCFile(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
			@PathVariable("evcotp") String evcotp, @PathVariable("month") int month, @PathVariable("year") int year) {
		logger.debug(CLASSNAME + "returnEVCFile:: Begin");
		if (year <= 0) {
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
		}
		String strMonth = (month) < 10 ? "0" + (month) : (month) + "";
		String retPeriod = strMonth + year;
		Client client = clientService.findById(clientid);
		try {
			Response summaryReponse = iHubConsumerService.returnSummary(client, client.getGstnnumber(), retPeriod, id,returntype, true);
			ObjectMapper mapper = new ObjectMapper();
			String strContent = null;
			if (NullUtil.isNotEmpty(summaryReponse) && NullUtil.isNotEmpty(summaryReponse.getData())) {
				strContent = mapper.writeValueAsString(summaryReponse.getData());
				return iHubConsumerService.returnFileByEVC(client, retPeriod, id, strContent, evcotp, returntype);
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnEVCFile:: ERROR", e);
		}
		return new Response();
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/ihubprocfile/{id}/{clientid}/{returntype}", method = RequestMethod.GET)
	public @ResponseBody Response iHubProceedToFile(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
			@RequestParam("month") int month, @RequestParam("year") int year, ModelMap model) throws Exception {
		final String method = "iHubProceedToFile::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (year <= 0) {
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
		}
		String strMonth = (month) < 10 ? "0" + (month) : (month) + "";
		String retPeriod = strMonth + year;
		Client client = clientService.findById(clientid);
		String gstn = client.getGstnnumber();
		Response response = null;
		try {
			Response proceedResponse = iHubConsumerService.proceedToFile(client, gstn, retPeriod);
			if (isNotEmpty(proceedResponse.getStatuscd()) 
					&& proceedResponse.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)
					&& isNotEmpty(proceedResponse.getData())
					&& isNotEmpty(proceedResponse.getData().getReferenceId())) {
				String refId = proceedResponse.getData().getReferenceId();
				Response gstnResponse = iHubConsumerService.returnStatus(refId, client.getStatename(),
						client.getGstname(), gstn, retPeriod, true);
				int i = 0;
				while ((i < 5) && (isNotEmpty(gstnResponse) && isNotEmpty(gstnResponse.getData())
						&& isNotEmpty(gstnResponse.getData().getStatusCd())
						&& gstnResponse.getData().getStatusCd().equals(MasterGSTConstants.GST_RETURN_CODE_IP))) {
					gstnResponse = iHubConsumerService.returnStatus(refId, client.getStatename(), client.getGstname(),
							gstn, retPeriod, false);
					i++;
				}
				return gstnResponse;
			}
			return proceedResponse;
		} catch (Exception e) {
			response = new Response();
			response.setStatuscd("0");
			response.setStatusdesc(e.getMessage());
			return response;
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/retfile/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Response returnFile(@PathVariable("id") String id, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month,
			@PathVariable("year") int year, @RequestBody String signData) {
		logger.debug(CLASSNAME + "returnFile:: Begin");
		logger.debug(CLASSNAME + "returnFile:: signData {}", signData);
		if (year <= 0) {
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
		}
		String strMonth = (month) < 10 ? "0" + (month) : (month) + "";
		String retPeriod = strMonth + year;
		Client client = clientService.findById(clientid);
		return iHubConsumerService.returnFile(client, retPeriod, id, signData, returntype);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/retsubmit/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Response returnSubmit(@PathVariable("id") String id, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month,
			@PathVariable("year") int year) {
		logger.debug(CLASSNAME + "returnSubmit:: Begin");
		if (year <= 0) {
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
		}
		String strMonth = month < 10 ? "0" + month : month + "";
		String retPeriod = strMonth + year;
		Client client = clientService.findById(clientid);
		User user = userService.findById(id);
		String usrid = id;
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser.getCompany())){
				if(companyUser.getCompany().contains(clientid)){
					usrid = user.getParentid();
				}
			}
		}
		String userid = userid(id,clientid);
		if (!subscriptionService.allowUploadInvoices(userid, 1l)) {
			Response errorResponse = new Response();
			errorResponse.setStatuscd("0");
			if(usrid.equals(user.getParentid())){
				User usr = userRepository.findById(userid);
				String errormsg = "";
				if(isNotEmpty(usr)) {
					errormsg = primaryHolderMessage(client,usr);
				}else {
					errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
				}
				errorResponse.setStatusdesc(errormsg);
			}else{
				errorResponse.setStatusdesc("Your subscription has expired. Kindly subscribe to proceed further!");
			}
			return errorResponse;
		}
		Response response = null;
		try {
			response = iHubConsumerService.returnSubmit(client, client.getGstnnumber(), returntype, retPeriod, id,true,false);
			if (returntype.equals(GSTR3B) && isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals("1")) {
				GSTR3B gstr3b = clientService.getSuppliesInvoice(clientid, retPeriod);
				if (isNotEmpty(gstr3b)) {
					gstr3b.setSubmitStatus(true);
					clientService.saveSuppliesInvoice(gstr3b, returntype);
				}
			} else if (isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals("1")) {
				Page<? extends InvoiceParent> invoices = clientService.getInvoices(null, client, id, returntype, "reports", month, year);
				if(isNotEmpty(invoices)) {
					for(InvoiceParent inv : invoices) {
						inv.setGstStatus(MasterGSTConstants.STATUS_SUBMITTED);
					}
					clientService.saveInvoices(invoices, returntype);
				}
			}
			return response;
		} catch (Exception e) {
			response = new Response();
			response.setStatuscd("0");
			response.setStatusdesc(e.getMessage());
			return response;
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/rettcfile/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Response returnTrueCopyFile(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestBody String signData) {
		logger.debug(CLASSNAME + "returnTrueCopyFile:: Begin");
		logger.debug(CLASSNAME + "returnTrueCopyFile:: signData {}", signData);
		if (year <= 0) {
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
		}
		String strMonth = (month) < 10 ? "0" + (month) : (month) + "";
		String retPeriod = strMonth + year;
		Client client = clientService.findById(clientid);
		Map<String, Object> signMap = Maps.newHashMap();
		signMap.put("pkcs7", signData);
		Map<String, String> contentMap = Maps.newHashMap();
		contentMap.put("gstin", client.getGstnnumber());
		contentMap.put("ret_period", retPeriod);
		signMap.put("content", contentMap);
		ObjectMapper mapper = new ObjectMapper();
		try {
			signData = mapper.writeValueAsString(signMap);
		} catch (JsonProcessingException e) {
			logger.error(CLASSNAME + "returnTrueCopyFile:: ERROR", e);
		}
		return iHubConsumerService.returnFile(client, retPeriod, id, signData, returntype);
	}

	@RequestMapping(value = "/ihubldgrcsh/{id}/{clientid}/{fromdate}/{todate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LedgerResponse iHubLedgerCashDetails(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("fromdate") String fromdate,
			@PathVariable("todate") String todate) {
		logger.debug(CLASSNAME + "iHubLedgerCashDetails:: Begin");
		Client client = clientService.findById(clientid);
		return iHubConsumerService.getLedgerCashDetails(client, client.getGstnnumber(), fromdate, todate, id, true);
	}

	@RequestMapping(value = "/ihubldgritc/{id}/{clientid}/{fromdate}/{todate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LedgerResponse iHubLedgerITCDetails(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("fromdate") String fromdate,
			@PathVariable("todate") String todate) {
		logger.debug(CLASSNAME + "iHubLedgerITCDetails:: Begin");
		Client client = clientService.findById(clientid);
		return iHubConsumerService.getLedgerITCDetails(client, client.getGstnnumber(), fromdate, todate, id, true);
	}

	@RequestMapping(value = "/ihubldgrtax/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody LedgerResponse iHubLedgerTaxDetails(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("month") int month,
			@PathVariable("year") int year) {
		logger.debug(CLASSNAME + "iHubLedgerTaxDetails:: Begin");
		Client client = clientService.findById(clientid);
		String strMonth = (month) < 10 ? "0" + (month) : (month) + "";
		String retPeriod = strMonth + year;
		return iHubConsumerService.getTaxLedgerDetails(client, client.getGstnnumber(), retPeriod, id, true);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/publicsearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Response publicSearch(@RequestParam("gstin") String gstin,@RequestParam("userid") String userid,HttpServletRequest request) {
		logger.debug(CLASSNAME + "publicSearch:: Begin");
		logger.debug(CLASSNAME + "publicSearch:: gstin {}", gstin);
		Response gstnResponse = new Response();
		if(isNotEmpty(userid)) {
			User user = userRepository.findOne(userid);
			String referer = request.getHeader("referer");
			if(isNotEmpty(user) && isNotEmpty(referer) && referer.contains("app.mastergst.com")) {
				Calendar cal = Calendar.getInstance();
				int pmonth = cal.get(Calendar.MONTH);
				int pyear = cal.get(Calendar.YEAR);
				int tdate = cal.get(Calendar.DATE);
				cal.set(pyear, pmonth, tdate, 0, 0,0);
				Date stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(pyear, pmonth, tdate, 23, 59, 59);
				Date endDate = new java.util.Date(cal.getTimeInMillis());
				
				GSTINPublicData gstndata = gstinPublicDataRepository.findByGstinAndCreatedDateBetween(gstin, stDate, endDate);
				if(isNotEmpty(gstndata)) {
					ResponseData data = new ResponseData();
					BeanUtils.copyProperties(gstndata, data);
					gstnResponse.setStatuscd(MasterGSTConstants.SUCCESS_CODE);
					gstnResponse.setData(data);
					return gstnResponse;
				}else {
				Response response = iHubConsumerService.publicSearch(gstin);
					if (isNotEmpty(response) && isNotEmpty(response.getData()) && isNotEmpty(response.getStatuscd())
							&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
						CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> response);
						future.thenAcceptAsync(responseObj -> {
							try {
								gstinPublicDataRepository.deleteByGstinAndUseridIsNull(gstin);
							} catch(Exception e) {}
							GSTINPublicData publicData = new GSTINPublicData();
							BeanUtils.copyProperties(responseObj.getData(), publicData);
							publicData.setCreatedDate(new Date());
							publicData.setUserid(userid);
							try {
								publicData.setIpAddress(InetAddress.getLocalHost().getHostAddress());
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							gstinPublicDataRepository.save(publicData);
						});
					}else if(NullUtil.isNotEmpty(response.getError()) && NullUtil.isNotEmpty(response.getError().getMessage())) {
						if(response.getError().getMessage().equalsIgnoreCase("This feature is temporarily down from GSTN, please try after some time.")) {
							GSTINPublicData gstnodata = gstinPublicDataRepository.findByGstin(gstin);
							if(isNotEmpty(gstnodata)) {
								ResponseData data = new ResponseData();
								BeanUtils.copyProperties(gstnodata, data);
								gstnResponse.setStatuscd(MasterGSTConstants.SUCCESS_CODE);
								gstnResponse.setData(data);
								return gstnResponse;
							}else {
								return response;
							}
						}else {
							return response;
						}
					}
					return response;
				}
			}else {
				gstnResponse.setStatuscd("0");
				return gstnResponse;
			}
		}else {
			gstnResponse.setStatuscd("0");
			return gstnResponse;
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/publicsearch1", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Response publicSearch1(@RequestParam("gstin") String gstin,
			@RequestParam("ipAddress") String ipAddress, HttpServletRequest request) {
		logger.debug(CLASSNAME + "publicSearch1:: Begin");
		logger.debug(CLASSNAME + "publicSearch1:: gstin {}", gstin);
		Response gstnResponse = new Response();
		String referer = request.getHeader("referer");
		if(isNotEmpty(referer) && referer.contains("mastergst.com")) {
			/*Calendar cal = Calendar.getInstance();
			int pmonth = cal.get(Calendar.MONTH);
			int pyear = cal.get(Calendar.YEAR);
			int tdate = cal.get(Calendar.DATE);
			cal.set(pyear, pmonth, tdate, 0, 0,0);
			Date stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(pyear, pmonth, tdate, 23, 59, 59);
			Date endDate = new java.util.Date(cal.getTimeInMillis());*/
			
			//GSTINPublicData gstndata = gstinPublicDataRepository.findByGstinAndCreatedDateBetween(gstin, stDate, endDate);
			GSTINPublicData gstndata = gstinPublicDataRepository.findByGstin(gstin);
			if(isNotEmpty(gstndata)) {
				ResponseData data = new ResponseData();
				BeanUtils.copyProperties(gstndata, data);
				gstnResponse.setStatuscd(MasterGSTConstants.SUCCESS_CODE);
				gstnResponse.setData(data);
				return gstnResponse;
			}else {
				gstnResponse.setStatuscd("0");
				return gstnResponse;
			}/*else {
				 Response response = iHubConsumerService.publicSearch(gstin); if
				 (isNotEmpty(response) && isNotEmpty(response.getData()) &&
				 isNotEmpty(response.getStatuscd()) &&
				 response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
				 CompletableFuture<Response> future = CompletableFuture.supplyAsync(() ->
				 response); future.thenAcceptAsync(responseObj -> { try {
				 gstinPublicDataRepository.deleteByGstinAndUseridIsNull(gstin); }
				 catch(Exception e) {} GSTINPublicData publicData = new GSTINPublicData();
				 BeanUtils.copyProperties(responseObj.getData(), publicData);
				 publicData.setCreatedDate(new Date()); publicData.setIpAddress(ipAddress);
				 gstinPublicDataRepository.save(publicData); }); }
				
				return response;
			}*/
		}else {
			gstnResponse.setStatuscd("0");
			return gstnResponse;
		}
	}
	
	public String userid(String id,String clientid) {
		String userid = id;
		User user = userService.findById(userid);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(NullUtil.isNotEmpty(companyUser)) {
				List<String> clntids = companyUser.getCompany();
				if(isNotEmpty(clntids) && clntids.size() > 0){
					if(clntids.contains(clientid)){
						ClientUserMapping clntusermapping = clientUserMappingRepository.findByClientidAndCreatedByIsNotNull(clientid);
						if(isNotEmpty(clntusermapping) && isNotEmpty(clntusermapping.getCreatedBy())) {
							userid = clntusermapping.getCreatedBy();
						}else {
							userid = user.getParentid();
						}
					}
				}else {
					userid = user.getParentid();
				}
			}
		}
		return userid;
	}
}
