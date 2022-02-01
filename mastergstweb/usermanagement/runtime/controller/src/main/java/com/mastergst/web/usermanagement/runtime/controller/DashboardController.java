/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.usermanagement.runtime.controller;
import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxls.template.SimpleExporter;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.EmailService;
import com.mastergst.configuration.service.Message;
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
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientStatus;
import com.mastergst.usermanagement.runtime.domain.DashboardTotal;
import com.mastergst.usermanagement.runtime.domain.FilingStatusReportsVO;
import com.mastergst.usermanagement.runtime.domain.GSTReturnSummary;
import com.mastergst.usermanagement.runtime.domain.Metering;
import com.mastergst.usermanagement.runtime.domain.MeteringVo;
import com.mastergst.usermanagement.runtime.domain.PaymentDetails;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.response.FinancialSummaryVO;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.MessageService;
import com.mastergst.usermanagement.runtime.service.MeteringService;
import com.mastergst.usermanagement.runtime.service.PartnerService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.ReportsService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;
import com.mastergst.usermanagement.runtime.support.Utility;

/**
 * Handles User dashboard and apis and credentials the logged in user depending
 * on the URI template.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping
public class DashboardController {

	private static final Logger logger = LogManager.getLogger(DashboardController.class.getName());
	private static final String CLASSNAME = "DashboardController::";

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
	OTPService otpService;
	@Autowired
	MasterGstExecutorService masterGstExecutorService;
	@Autowired
	SMSService smsService;
	@Autowired
	EmailService emailService;
	@Autowired
	private ReportsService reportsService;
	
	private DecimalFormat decimalFormat = new DecimalFormat("#0.00");

	@RequestMapping(value = "/signin", method = RequestMethod.POST)
	public String dashboard(@RequestParam String email, @RequestParam String password, ModelMap model, HttpServletRequest request)
			throws Exception {
		final String method = "signin :: dashboard::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "email\t" + email);
		//boolean isExist = userService.collectionExists("users");
		//logger.debug(CLASSNAME + method + "isExist\t" + isExist);
		User user = userService.findByEmailIgnoreCase(email);
		try {
			/*if (isExist) {
				user = userService.findByEmailIgnoreCase(email);
			}*/
			if (NullUtil.isEmpty(user)) {
				model.addAttribute("error", "Email Id does not exist");
				logger.debug(CLASSNAME + method + END);
				return "login/login";
			}  else if (NullUtil.isEmpty(user.getPassword())) {
				model.addAttribute("error", "You haven't yet signed up.");
				logger.debug(CLASSNAME + method + END);
				return "login/login";
			} else if (!password.equals(new String(Base64.getDecoder().decode(user.getPassword()),
					MasterGSTConstants.PASSWORD_ENCODE_FORMAT))) {
				model.addAttribute("error", "Password entered is not correct");
				logger.debug(CLASSNAME + method + END);
				return "login/login";
			} else if(NullUtil.isNotEmpty(user.getOtpVerified()) && user.getOtpVerified().equals("false")){
				otpService.deleteByUserid(user.getId().toString());
				OTP otp = new OTP();
				otp.setOtp(SMSUtil.OTP(4));
				otp.setUserid(user.getId().toString());
				otp.setStatus("init");
				logger.debug(CLASSNAME + method + " SMS Begin");
				otpService.createOTP(otp);
				masterGstExecutorService.execute(()->{
				List<String> destinationNos = new ArrayList<String>();
				destinationNos.add(user.getMobilenumber());
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
					emailService.sendEnrollEmail(user.getEmail(),
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
			});
				model.addAttribute("id", user.getId());
				model.addAttribute("source", "otp/otp_success");
				model.addAttribute("initialSignup", "no");
				return "otp/otp";
			}else if (NullUtil.isNotEmpty(user.getDisable()) && user.getDisable().equals("true")) {
				model.addAttribute("error", "User is inactive");
				logger.debug(CLASSNAME + method + END);
				if(user.getType().equals(MasterGSTConstants.SUVIDHA_CENTERS)) {
					return "login/login_error";
				} else {
					return "login/login";
				}
			}else {
				request.getSession().setAttribute("User", user.getId().toString());
				user.setLastLoggedIn(new Date());
				userService.updateUser(user);
				if(NullUtil.isNotEmpty(user.getEmail()) && NullUtil.isNotEmpty(user.getType()) && user.getType().equalsIgnoreCase("subadmin")) {
					return "redirect:obtainusers?id="+user.getId()+"&fullname="+user.getFullname()+"&usertype="+user.getType();
				}else if(NullUtil.isNotEmpty(user.getEmail()) && user.getEmail().equals("bvmcs@mastergst.com")) {
					return "redirect:obtainusers?id="+user.getId()+"&fullname="+user.getFullname()+"&usertype="+user.getType();
				} else if (NullUtil.isNotEmpty(user.getType()) && user.getType().equalsIgnoreCase(MasterGSTConstants.ASPDEVELOPER)) {
					List<Metering> lMetering = new ArrayList<Metering>();
					SubscriptionDetails subscriptionDetails= null;
					subscriptionDetails = subscriptionService.getSubscriptionData(user.getId().toString(),MasterGSTConstants.GST_API);
					model.addAttribute("dashboardType", "gst");
					model.addAttribute("dashboardApiType", "all");
					model.addAttribute("id", user.getId());
					model.addAttribute("fullname", user.getFullname());
					model.addAttribute("usertype", user.getType());
					model.addAttribute("lmetering", lMetering);
					model.addAttribute("houranday", 1 + " Hour");
					model.addAttribute("headerflag", "dashboard");
					model.addAttribute("otheruser", "no");
					if(NullUtil.isNotEmpty(subscriptionDetails) && NullUtil.isNotEmpty(subscriptionDetails.getInvoiceCost()) && NullUtil.isNotEmpty(subscriptionDetails.getAllowedInvoices())) {
						model.addAttribute("rate", subscriptionDetails.getInvoiceCost());
						model.addAttribute("totalinvoices", subscriptionDetails.getAllowedInvoices());
						Calendar cals = Calendar.getInstance();
						cals.add(Calendar.DATE, 1);
						Date tommarowDate = cals.getTime();
						cals = Calendar.getInstance();
						Date expirydate = subscriptionDetails.getExpiryDate();
						if(NullUtil.isNotEmpty(expirydate)){
							long timeDiff = Math.abs((cals.getTimeInMillis() - expirydate.getTime())/86400000);
							if(NullUtil.isNotEmpty(subscriptionDetails.getSubscriptionType())){
								if("UNLIMITED".equalsIgnoreCase(subscriptionDetails.getSubscriptionType())){
									model.addAttribute("subscriptionStatus", "Active");
								}else{
									if(tommarowDate.compareTo(expirydate)>0){
										model.addAttribute("subscriptionStatus", "Expired");
									}else if((int)timeDiff < 45){
										model.addAttribute("days", Integer.toString((int)timeDiff));
										model.addAttribute("subscriptionStatus", "About to Expire");
									}else{
										model.addAttribute("subscriptionStatus", "Active");
									}
								}
							}else{
								if(tommarowDate.compareTo(expirydate)>0){
									model.addAttribute("subscriptionStatus", "Expired");
								}else if((int)timeDiff < 45){
									model.addAttribute("days", Integer.toString((int)timeDiff));
									model.addAttribute("subscriptionStatus", "About to Expire");
								}else{
									model.addAttribute("subscriptionStatus", "Active");
								}
							}
						}else{
							model.addAttribute("subscriptionStatus", "Active");
						}
						Integer usedCountIn = subscriptionDetails.getProcessedInvoices();
						int usedCount = usedCountIn == null ? 0 : usedCountIn ;
						if(subscriptionDetails.getAllowedInvoices() > usedCount) {
							model.addAttribute("avlInvoices", (subscriptionDetails.getAllowedInvoices() - usedCount));
						} else {
							model.addAttribute("avlInvoices", 0);
						}
						
						double avlBilling = subscriptionDetails.getPaidAmount() - (usedCount*subscriptionDetails.getInvoiceCost());
						if(avlBilling > 0) {
							double billing = ((int)(avlBilling*100))/100.0;
							model.addAttribute("avlBilling", billing);
						} else {
							if(isNotEmpty(subscriptionDetails.getExpiryDate()) && isNotEmpty(subscriptionDetails.getRegisteredDate())) {
								DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
								String stdate = dateFormat.format(subscriptionDetails.getRegisteredDate());
								String expdate = dateFormat.format(subscriptionDetails.getExpiryDate());
								
								List<String> startdate = Lists.newArrayList();
								startdate.add(stdate);
								String[]  std= stdate.split("-");
								String dateCode = Integer.parseInt(std[0]) < 10 ? std[0].substring(1) : std[0];
								String monthCode = Integer.parseInt(std[1]) < 10 ? std[1].substring(1) : std[1];
								String stdt = dateCode+"-"+monthCode+"-"+std[2];
								startdate.add(stdt);
								
								List<String> enddate = Lists.newArrayList();
								enddate.add(expdate);
								String[]  etd= expdate.split("-");
								String edateCode = Integer.parseInt(etd[0]) < 10 ? etd[0].substring(1) : etd[0];
								String emonthCode = Integer.parseInt(etd[1]) < 10 ? etd[1].substring(1) : etd[1];
								String etdt = edateCode+"-"+emonthCode+"-"+etd[2];
								enddate.add(etdt);
								
								List<PaymentDetails> paymentList=subscriptionService.getPaymentDetails(user.getId().toString(),MasterGSTConstants.GST_API,startdate,enddate);
								double billing = 0;
								if(isNotEmpty(paymentList)) {
									for(PaymentDetails payment : paymentList) {
										if(isNotEmpty(payment.getAmount())) {
											billing = billing+payment.getAmount();
										}
									}
								}
								double avlBillings = billing - (usedCount*subscriptionDetails.getInvoiceCost());
								if(avlBillings > 0) {
									double billings = ((int)(avlBillings*100))/100.0;
									model.addAttribute("avlBilling", billings);
								}else {
									model.addAttribute("avlBilling", 0);
								}
							}else {
								model.addAttribute("avlBilling", 0);
							}
						}
						model.addAttribute("usedInvoices", usedCount);
						double usedBilling = ((int)(usedCount*subscriptionDetails.getInvoiceCost()*100))/100.0;
						model.addAttribute("usedBilling", usedBilling);
					}
					List<Message> messages = messageService.getMessagesByUserType(MasterGSTConstants.ASPDEVELOPER);
					messages.forEach((m)->m.setMsgId(m.getId().toString()));
					model.addAttribute("messages", messages);
					model.addAttribute("messageCount", messages.size());
					logger.debug(CLASSNAME + method + END);
					return "dashboard/dashboard_asp";
				} else if (NullUtil.isNotEmpty(user.getType())
						&& user.getType().equalsIgnoreCase(MasterGSTConstants.PARTNER)) {
					model.addAttribute("id", user.getId());
					model.addAttribute("fullname", user.getFullname());
					model.addAttribute("page", "invite");
					model.addAttribute("user", user);
					model.addAttribute("clientsList", partnerService.getPartnerClients(user.getId().toString()));
					List<Message> messages = messageService.getMessagesByUserType(MasterGSTConstants.PARTNER);
					messages.forEach((m)->m.setMsgId(m.getId().toString()));
					model.addAttribute("messages", messages);
					List<String> list = Arrays.asList("GST API Sandbox","GST API Production","Eway Bill API Sandbox","Eway Bill API Production","Einvoice API Sandbox","Einvoice API Production","E-Invoice Software","Eway Bill Software","GST software","Suvidha Kendra","Tally Connector","SAP Connector","Oracle connector","MultiGSTN Tool","Reminders","Acknowledgements");
					model.addAttribute("productTypes", list);
					logger.debug(CLASSNAME + method + "Partner");
					return "partners/invitations";
				} else if (NullUtil.isNotEmpty(user.getType())
						&& !user.getType().equalsIgnoreCase(MasterGSTConstants.ASPDEVELOPER)) {
					model.addAttribute("id", user.getId());
					model.addAttribute("fullname", user.getFullname());
					model.addAttribute("usertype", user.getType());
					Calendar cal = Calendar.getInstance();
					model.addAttribute("month", cal.get(Calendar.MONTH)+1);
					model.addAttribute("year", cal.get(Calendar.YEAR));
					logger.debug(CLASSNAME + method + "Another user not ASP Developer");
					logger.debug(CLASSNAME + method + END);
					
					List<Message> messages = messageService.getMessagesByUserType(user.getType());
					messages.forEach((m)->m.setMsgId(m.getId().toString()));
					model.addAttribute("messages", messages);
					model.addAttribute("messageCount",messages.size());
					List<String> clientList = clientService.fetchClientIds(user.getId().toString());
					if (NullUtil.isNotEmpty(clientList)) {
						//model.addAttribute("lClient", lClient);
						model.addAttribute("clientList", clientList);
						request.getSession().setAttribute("clientIds", clientList);
						Map<String, GSTReturnSummary> returnsSummaryMap = Maps.newHashMap();
						List<GSTReturnSummary> returnsSummaryList = clientService.getGSTReturnsSummary();
						for (GSTReturnSummary summary : returnsSummaryList) {
							if (!returnsSummaryMap.containsKey(summary.getReturntype())) {
								if (NullUtil.isEmpty(summary.getStatusMap())) {
									Map<String, Integer> statusMap = Maps.newHashMap();
									statusMap.put(MasterGSTConstants.PENDING, 0);
									summary.setStatusMap(statusMap);
								}
								returnsSummaryMap.put(summary.getReturntype(), summary);
							}
						}
						
						model.addAttribute("returnsSummaryMap", returnsSummaryMap);
						if(NullUtil.isNotEmpty(user.getIsglobal())) {
							if("true".equalsIgnoreCase(user.getIsglobal())) {
								if(NullUtil.isNotEmpty(user.getParentid())) {
									User parentUsr=userService.findById(user.getParentid());
									if(NullUtil.isNotEmpty(parentUsr.isAccessAcknowledgement())) {
										if(parentUsr.isAccessAcknowledgement()) {
											String status = profileService.getAcknowledgementPermissions(user.getEmail());
											if(NullUtil.isNotEmpty(status)) {
												if("Acknowledgement User".equalsIgnoreCase(status)) {
													return "redirect:acknldgrusers?id="+user.getId()+"&fullname="+user.getFullname()+"&usertype="+user.getType();
												}else {
													model.addAttribute("Acknowledgements", true);
												}
											}
										}
									}
								}
							}
						}
						logger.debug(CLASSNAME + method + END);
						return "dashboard/dashboard_ca";
					} else {
						if(NullUtil.isNotEmpty(user.getIsglobal())) {
							if("true".equalsIgnoreCase(user.getIsglobal())) {
								if(NullUtil.isNotEmpty(user.getParentid())) {
									User parentUsr=userService.findById(user.getParentid());
									if(NullUtil.isNotEmpty(parentUsr.isAccessAcknowledgement())) {
										if(parentUsr.isAccessAcknowledgement()) {
											String status = profileService.getAcknowledgementPermissions(user.getEmail());
											if(NullUtil.isNotEmpty(status)) {
												if("Acknowledgement User".equalsIgnoreCase(status)) {
													return "redirect:acknldgrusers?id="+user.getId()+"&fullname="+user.getFullname()+"&usertype="+user.getType();												
												}else {
													model.addAttribute("Acknowledgements", true);
												}
											}else {
												model.addAttribute("Acknowledgements", false);
											}
										}
									}
								}
							}
						}
						return "dashboard/dashboard_ca_init";
					}
				} else {
					model.addAttribute("otheruser", "yes");
					model.addAttribute("id", user.getId());
					model.addAttribute("fullname", user.getFullname());
					model.addAttribute("usertype", user.getType());
					model.addAttribute("mail", user.getEmail());
					model.addAttribute("mobilenumber", user.getMobilenumber());
					model.addAttribute("address", user.getAddress());
					model.addAttribute("headerflag", "credentials");
					logger.debug(CLASSNAME + method + END);
					return "credentials/credentials";
				}
				
				
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(CLASSNAME + method + MasterGSTConstants.PASSWORD_ENCODE_EXCEPTION);
			model.addAttribute("error", MasterGSTConstants.PASSWORD_ENCODE_EXCEPTION);
			logger.debug(CLASSNAME + method + END);
			return "login/login";

		}
	}
	
	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public String dashboard(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "hour", required = true) String hour,
			@RequestParam(value = "hdvalue", required = true) String hdvalue, 
			@RequestParam(value = "usertype", required = false) String userType, 
			@RequestParam(value = "dashboardType", required = true) String dashboardType, ModelMap model) throws Exception {
		final String method = "dashboard::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "hour\t" + hour);
		logger.debug(CLASSNAME + method + "hdvalue\t" + hdvalue);
		logger.debug(CLASSNAME + method + "dashboardType\t" + dashboardType);
		User user = userService.findById(id);
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH)+1;
		int year = cal.get(Calendar.YEAR);
		int day = cal.get(Calendar.DATE);
		cal.set(year, month, day, 0, 0, 0);
		Date startDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, day+1, 0, 0, 0);
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		List<Metering> lMetering = new ArrayList<Metering>();
		SubscriptionDetails subscriptionDetails= null;
			SubscriptionDetails	subscriptionDetailsGst = null,subscriptionDetailsEway = null, subscriptionDetailsEinv = null;
			if("gst".equals(dashboardType)){
			  	subscriptionDetailsGst = subscriptionService.getSubscriptionData(user.getId().toString(),"GSTAPI");
			}else if("eway".equals(dashboardType)){
				subscriptionDetailsEway = subscriptionService.getSubscriptionData(user.getId().toString(),"EWAYAPI");
			}else if("einv".equals(dashboardType)){
				subscriptionDetailsEinv = subscriptionService.getSubscriptionData(user.getId().toString(),"E-INVOICEAPI");
			}
			if(NullUtil.isNotEmpty(subscriptionDetailsGst)){
				lMetering = meteringService.getGSTMeteringData(user.getId().toString(), startDate, endDate);
				subscriptionDetails = subscriptionDetailsGst;
			}else if(NullUtil.isNotEmpty(subscriptionDetailsEway)){
				lMetering = meteringService.getEWayMeteringData(user.getId().toString(), startDate, endDate);
				subscriptionDetails = subscriptionDetailsEway;
			}else if(NullUtil.isNotEmpty(subscriptionDetailsEinv)){
				lMetering = meteringService.getEInvMeteringData(user.getId().toString(), startDate, endDate);
				subscriptionDetails = subscriptionDetailsEinv;
			}else{
				if(!"einv".equals(dashboardType)){
					lMetering = meteringService.getMeteringData(user.getId().toString(), startDate, endDate);
					subscriptionDetails = subscriptionService.getSubscriptionData(user.getId().toString());
				}
			}
			model.addAttribute("dashboardType", dashboardType);
			model.addAttribute("dashboardApiType", "all");
		Map<String, String> meteringMap = getRequestMetering(lMetering);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("lmetering", lMetering);
		model.addAttribute("houranday", hdvalue + " " + hour);
		model.addAttribute("requestPerSecond", meteringMap.get("requestPerSecond"));
		model.addAttribute("failCounter", meteringMap.get("failCounter"));
		model.addAttribute("headerflag", "dashboard");
		model.addAttribute("otheruser", "no");
		model.addAttribute("usertype", userType);
		model.addAttribute("dashboardType", dashboardType);
		if(NullUtil.isNotEmpty(subscriptionDetails) 
				&& NullUtil.isNotEmpty(subscriptionDetails.getInvoiceCost())
				&& NullUtil.isNotEmpty(subscriptionDetails.getAllowedInvoices())) {
			model.addAttribute("rate", subscriptionDetails.getInvoiceCost());
			model.addAttribute("totalinvoices", subscriptionDetails.getAllowedInvoices());
			Integer usedCountIn = subscriptionDetails.getProcessedInvoices();
			int usedCount = usedCountIn == null ? 0 : usedCountIn;
			Calendar cals = Calendar.getInstance();
			cals.add(Calendar.DATE, 1);
			Date tommarowDate = cals.getTime();
			cals = Calendar.getInstance();
			Date expirydate = subscriptionDetails.getExpiryDate();
			long timeDiff = Math.abs((cals.getTimeInMillis() - expirydate.getTime())/86400000);
			if(tommarowDate.compareTo(expirydate)>0){
				model.addAttribute("subscriptionStatus", "Expired");
			}else if((int)timeDiff < 45){
				model.addAttribute("days", Integer.toString((int)timeDiff));
				model.addAttribute("subscriptionStatus", "About to Expire");
			}else{
				model.addAttribute("subscriptionStatus", "Active");
			}
			if(subscriptionDetails.getAllowedInvoices() > usedCount) {
				model.addAttribute("avlInvoices", (subscriptionDetails.getAllowedInvoices() - usedCount));
			} else {
				model.addAttribute("avlInvoices", 0);
			}
			double avlBilling = subscriptionDetails.getPaidAmount() - (usedCount*subscriptionDetails.getInvoiceCost());
			if(avlBilling > 0) {
				double billing = ((int)(avlBilling*100))/100.0;
				model.addAttribute("avlBilling", billing);
			} else {
				if(isNotEmpty(subscriptionDetails.getExpiryDate()) && isNotEmpty(subscriptionDetails.getRegisteredDate())) {
					DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					String stdate = dateFormat.format(subscriptionDetails.getRegisteredDate());
					String expdate = dateFormat.format(subscriptionDetails.getExpiryDate());
					List<String> startdate = Lists.newArrayList();
					startdate.add(stdate);
					String[]  std= stdate.split("-");
					String dateCode = Integer.parseInt(std[0]) < 10 ? std[0].substring(1) : std[0];
					String monthCode = Integer.parseInt(std[1]) < 10 ? std[1].substring(1) : std[1];
					String stdt = dateCode+"-"+monthCode+"-"+std[2];
					startdate.add(stdt);
					
					List<String> enddate = Lists.newArrayList();
					enddate.add(expdate);
					String[]  etd= expdate.split("-");
					String edateCode = Integer.parseInt(etd[0]) < 10 ? etd[0].substring(1) : etd[0];
					String emonthCode = Integer.parseInt(etd[1]) < 10 ? etd[1].substring(1) : etd[1];
					String etdt = edateCode+"-"+emonthCode+"-"+etd[2];
					enddate.add(etdt);
					String apiType = "GSTAPI";
					if("gst".equals(dashboardType)){
						apiType = "GSTAPI";
					}else if("eway".equals(dashboardType)){
						apiType = "EWAYAPI";
					}else if("einv".equals(dashboardType)){
						apiType = "E-INVOICEAPI";
					}
					List<PaymentDetails> paymentList=subscriptionService.getPaymentDetails(user.getId().toString(),apiType,startdate,enddate);
					double billing = 0;
					if(isNotEmpty(paymentList)) {
						for(PaymentDetails payment : paymentList) {
							if(isNotEmpty(payment.getAmount())) {
								billing = billing+payment.getAmount();
							}
						}
					}
					double avlBillings = billing - (usedCount*subscriptionDetails.getInvoiceCost());
					if(avlBillings > 0) {
						double billings = ((int)(avlBillings*100))/100.0;
						model.addAttribute("avlBilling", billings);
					}else {
						model.addAttribute("avlBilling", 0);
					}
				}else {
					model.addAttribute("avlBilling", 0);
				}
			}
			model.addAttribute("usedInvoices", usedCount);
			model.addAttribute("usedBilling", ((int)(usedCount*subscriptionDetails.getInvoiceCost()*100.0))/100.0);
		}else{
			model.addAttribute("avlInvoices", 0);
			model.addAttribute("avlBilling", 0);
			model.addAttribute("usedInvoices", 0);
			model.addAttribute("usedBilling", 0);
		}
		List<Message> messages = messageService.getMessagesByUserType(MasterGSTConstants.ASPDEVELOPER);
		messages.forEach((m)->m.setMsgId(m.getId().toString()));
		model.addAttribute("messages", messages);
		model.addAttribute("messageCount",messages.size());
		logger.debug(CLASSNAME + method + END);
		return "dashboard/dashboard_asp";
	}
	
	@RequestMapping(value = "/getrtmeterdata", method = RequestMethod.GET)
	public @ResponseBody Map<String,String> returnPeriodMeterData(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "dashboardType", required = true) String dashboardType,
			@RequestParam(value = "month", required = true) int month, 
			@RequestParam(value = "year", required = true) int year, 
		 ModelMap model) throws Exception {
		final String method = "meterData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		Map<String,String> map=Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, 0, 0, 0, 0);
		Date startDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month+1, 0, 0, 0, 0);
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		
		List<Metering> gstLMetering = new ArrayList<Metering>();
		List<Metering> ewayLMetering = new ArrayList<Metering>();
		List<Metering> oldLMetering = new ArrayList<Metering>();
		List<Metering> lMetering = new ArrayList<Metering>();
			gstLMetering = meteringService.getGSTMeteringData(id, startDate, endDate);
			ewayLMetering = meteringService.getEWayMeteringData(id, startDate, endDate);
			oldLMetering = meteringService.getMeteringData(id, startDate, endDate);
		SubscriptionDetails gstSubscriptionDetails,ewaySubscriptionDetails;
			gstSubscriptionDetails = subscriptionService.getSubscriptionData(id,"GSTAPI");
			ewaySubscriptionDetails = subscriptionService.getSubscriptionData(id,"EWAYAPI");
		if(NullUtil.isNotEmpty(gstSubscriptionDetails) && NullUtil.isNotEmpty(ewaySubscriptionDetails)){
			if("gst".equals(dashboardType)){
				lMetering = gstLMetering;
			}else{
				lMetering = ewayLMetering;
			}
		}else if(NullUtil.isNotEmpty(gstSubscriptionDetails)){
			lMetering = gstLMetering;
		}else if(NullUtil.isNotEmpty(ewaySubscriptionDetails)){
			lMetering = ewayLMetering;
		}else{
			lMetering = oldLMetering;
		}
		
		Map<String, String> meteringMap = getRequestMetering(lMetering);
		map.put("requestPerSecond", meteringMap.get("requestPerSecond"));
		map.put("failCounter", meteringMap.get("failCounter"));
		/*SubscriptionDetails subscriptionDetails = subscriptionService.getSubscriptionData(id);
		if(NullUtil.isNotEmpty(subscriptionDetails) && NullUtil.isNotEmpty(subscriptionDetails.getInvoiceCost())) {
			model.addAttribute("rate", subscriptionDetails.getInvoiceCost());
			model.addAttribute("totalinvoices", subscriptionDetails.getAllowedInvoices());
			cal = Calendar.getInstance();
			cal.setTime(subscriptionDetails.getRegisteredDate());
			Date mStartDate = new java.util.Date(cal.getTimeInMillis());
			List<Metering> usedMetering = meteringService.getMeteringData(id, mStartDate, endDate);
			int invCount = 0;
			int usedCount = 0;
			for(Metering metering : usedMetering) {
				if((NullUtil.isNotEmpty(metering.getStatus()) && metering.getStatus().equals(MasterGSTConstants.SUCCESS))
						|| (NullUtil.isEmpty(metering.getErrorMessage()))) {
					invCount++;
					if(NullUtil.isNotEmpty(metering.getCreatedDate()) && (metering.getCreatedDate().getMonth() == (endDate.getMonth()-1))) {
						usedCount++;
					}
				}
			}
			//map.put("apiData", ((subscriptionDetails.getAllowedInvoices() - invCount) +"/"+ (subscriptionDetails.getPaidAmount() - (invCount*subscriptionDetails.getInvoiceCost()))));
			//map.put("billingData", usedCount+"/"+(usedCount*subscriptionDetails.getInvoiceCost()));
		}*/
		return map;
	}
	
	@RequestMapping(value = "/getmeterdata", method = RequestMethod.GET)
	public @ResponseBody List<Metering> meterData(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "dashboardType", required = true) String dashboardType,
			@RequestParam(value = "day", required = true) int day,
			@RequestParam(value = "month", required = true) int month, 
			@RequestParam(value = "year", required = true) int year, ModelMap model) throws Exception {
		final String method = "meterData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, 0, 0, 0);
		Date startDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, day+1, 0, 0, 0);
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		List<Metering> gstLMetering = new ArrayList<Metering>();
		List<Metering> ewayLMetering = new ArrayList<Metering>();
		List<Metering> oldLMetering = new ArrayList<Metering>();
		List<Metering> lMetering = new ArrayList<Metering>();
			gstLMetering = meteringService.getGSTMeteringData(id, startDate, endDate);
			ewayLMetering = meteringService.getEWayMeteringData(id, startDate, endDate);
			oldLMetering = meteringService.getMeteringData(id, startDate, endDate);
		
		SubscriptionDetails gstSubscriptionDetails,ewaySubscriptionDetails;
			gstSubscriptionDetails = subscriptionService.getSubscriptionData(id,"GSTAPI");
			ewaySubscriptionDetails = subscriptionService.getSubscriptionData(id,"EWAYAPI");
		if(NullUtil.isNotEmpty(gstSubscriptionDetails) && NullUtil.isNotEmpty(ewaySubscriptionDetails)){
			if("gst".equals(dashboardType)){
				lMetering = gstLMetering;
			}else{
				lMetering = ewayLMetering;
			}
		}else if(NullUtil.isNotEmpty(gstSubscriptionDetails)){
			lMetering = gstLMetering;
		}else if(NullUtil.isNotEmpty(ewaySubscriptionDetails)){
			lMetering = ewayLMetering;
		}else if("gst".equals(dashboardType) && NullUtil.isNotEmpty(gstLMetering)){
			lMetering = gstLMetering;
		}else if("eway".equals(dashboardType) && NullUtil.isNotEmpty(ewayLMetering)){
			lMetering = ewayLMetering;
		}else{
			lMetering = oldLMetering;
		}
		return lMetering;
	}
	
	@RequestMapping(value = "/getAspMeterdataTraffic", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> meterTrafficData(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "dashboardType", required = true) String dashboardType,
			@RequestParam(value = "day", required = true) int day,
			@RequestParam(value = "month", required = true) int month, 
			@RequestParam(value = "year", required = true) int year, ModelMap model) throws Exception {
		final String method = "meterData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		User user = userService.findById(id);
		Calendar cal = Calendar.getInstance();
		if(day > 0) {
			cal.set(year, month, day, 0, 0, 0);
		}else {
			cal.set(year, month, 1, 0, 0, 0);
		}
		
		Date startDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		if(day > 0) {
			cal.set(year, month, day+1, 0, 0, 0);
		}else {
			cal.set(year, month+1, 0, 23, 59, 59);
		}
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		List<Metering> lMetering = new ArrayList<Metering>();
		
		if(NullUtil.isNotEmpty(user.getGstApi()) && NullUtil.isNotEmpty(user.getEwaybillApi()) && "gst".equals(dashboardType)){
			lMetering = meteringService.getGSTMeteringData(user.getId().toString(), startDate, endDate);
		}else if(NullUtil.isNotEmpty(user.getGstApi()) && NullUtil.isNotEmpty(user.getEwaybillApi()) && "eway".equals(dashboardType)){
			lMetering = meteringService.getEWayMeteringData(user.getId().toString(), startDate, endDate);
		}else if(NullUtil.isNotEmpty(user.getGstApi()) && "gst".equals(dashboardType)){
			lMetering = meteringService.getGSTMeteringData(user.getId().toString(), startDate, endDate);
		}else if(NullUtil.isNotEmpty(user.getEwaybillApi()) && "eway".equals(dashboardType)){
			lMetering = meteringService.getEWayMeteringData(user.getId().toString(), startDate, endDate);
		}else {
			if("gst".equals(dashboardType)){
				lMetering = meteringService.getGSTMeteringData(user.getId().toString(), startDate, endDate);
			}else if("eway".equals(dashboardType)){
				lMetering = meteringService.getEWayMeteringData(user.getId().toString(), startDate, endDate);
			}else if("einv".equals(dashboardType)){
				lMetering = meteringService.getEInvMeteringData(user.getId().toString(), startDate, endDate);
			}else{
				lMetering = meteringService.getMeteringData(user.getId().toString(), startDate, endDate);
			}
			
		}
		Map<String, String> meteringMap = Maps.newHashMap();
		meteringMap = getRequestMetering(lMetering);
		Map<String, Object> meterData = new HashMap<>();
		meterData.put("requestPerSecond", meteringMap.get("requestPerSecond"));
		meterData.put("failCounter", meteringMap.get("failCounter"));
		return meterData;
	}
	
	@RequestMapping(value = "/getAspMeterdata", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> allAspMeterdata(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "dashboardType", required = true) String dashboardType,
			@RequestParam(value = "day", required = true) int day,
			@RequestParam(value = "month", required = true) int month, 
			@RequestParam(value = "year", required = true) int year,@RequestParam(value = "cost", required = true) String cost,ModelMap model,  HttpServletRequest request) throws Exception {
		Calendar cal = Calendar.getInstance();
		if(day > 0) {
			cal.set(year, month, day, 0, 0, 0);
		}else {
			cal.set(year, month, 1, 0, 0, 0);
		}
		Date startDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		if(day > 0) {
			cal.set(year, month, day+1, 0, 0, 0);
		}else {
			cal.set(year, month+1, 0, 23, 59, 59);
		}
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		String startStr = request.getParameter("start");
		int start = 0;
		if(NullUtil.isNotEmpty(startStr)){
			start = Integer.parseInt(startStr);
		}
		//String lengthStr = request.getParameter("length");
		int length = 50;
		/*if(NullUtil.isNotEmpty(lengthStr)){
			length = Integer.parseInt(lengthStr);
		}*/
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
		
			Map<String, Object> meterData = new HashMap<>();
			Page<Metering> metering  = meteringService.getGSTAllMeterings(dashboardType, id, start, length, sortParam, sortOrder, searchVal, startDate, endDate);
			if(NullUtil.isNotEmpty(metering) && NullUtil.isNotEmpty(metering.getContent())){
				for(Metering meter : metering.getContent()){
					meter.setMeterid(meter.getId().toString());
					if("Fail".equals(meter.getStatus()) && NullUtil.isNotEmpty(meter.getErrorMessage())){
						meter.setCost("0.0");
					}else{
						meter.setCost(cost);
					}
				}
			}
			metering.getContent().forEach((m)->m.setMeterid(m.getId().toString()));
			meterData.put("data", metering.getContent());
			meterData.put("recordsFiltered", metering.getTotalElements());
			meterData.put("recordsTotal", metering.getTotalElements());
			meterData.put("draw", request.getParameter("draw"));
			return meterData;
		
	}
	
	@RequestMapping(value = "/apis", method = RequestMethod.GET)
	public String apis(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname, 
			@RequestParam(value = "apiType", required = true) String apiType, 
			@RequestParam(value = "usertype", required = false) String userType, ModelMap model) throws Exception {
		final String method = "apis::";
		logger.debug(CLASSNAME + method + BEGIN);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("headerflag", "apis");
		model.addAttribute("otheruser", "no");
		model.addAttribute("usertype", userType);
		model.addAttribute("apiType", apiType);
		List<Message> messages = messageService.getMessagesByUserType(MasterGSTConstants.ASPDEVELOPER);
		
		String type=null;
		
		if("eway".equals(apiType)) {
			type="EWAYAPI";
		}else if("einv".equals(apiType)){
			type="E-INVOICEAPI";
			//type="GSTAPI";
		}else {
			type="GSTAPI";
		}
		SubscriptionDetails subscriptionDetails=subscriptionService.getSubscriptionData(id, type);
		if(NullUtil.isNotEmpty(subscriptionDetails)) {		
			Calendar cals = Calendar.getInstance();
			cals.add(Calendar.DATE, 1);
			Date tommarowDate = cals.getTime();
			cals = Calendar.getInstance();
			Date expirydate = subscriptionDetails.getExpiryDate();
			long timeDiff = Math.abs((cals.getTimeInMillis() - expirydate.getTime())/86400000);
			if(tommarowDate.compareTo(expirydate)>0){
				model.addAttribute("subscriptionStatus", "Expired");
			}else if((int)timeDiff < 45){
				model.addAttribute("days", Integer.toString((int)timeDiff));
				model.addAttribute("subscriptionStatus", "About to Expire");
			}else{
				model.addAttribute("subscriptionStatus", "Active");
			}
			
			
			model.addAttribute("dashboardType", apiType);
		}
		
		messages.forEach((m)->m.setMsgId(m.getId().toString()));
		model.addAttribute("messages", messages);
		model.addAttribute("messageCount",messages.size());
		logger.debug(CLASSNAME + method + END);
		return "api/apis";
	}
	
	@RequestMapping(value = "/errorcodes", method = RequestMethod.GET)
	public String errorcodes(ModelMap model){
		return "api/errorcodes";
	}
	
	@RequestMapping(value = "/e-inovoice-errorcodes", method = RequestMethod.GET)
	public String einvoiceerrorcodes(ModelMap model){
		return "api/e-inovoice-errorcodes";
	}
	@RequestMapping(value = "/statecode-list", method = RequestMethod.GET)
	public String statecodeList(ModelMap model){
		return "api/statecode-list";
	}
	@RequestMapping(value = "/eway-errorcodes", method = RequestMethod.GET)
	public String ewayerrorcodes(ModelMap model){
		return "api/eway-errorcodes";
	}
	
	
	@RequestMapping(value = "/messages", method = RequestMethod.GET)
	public String messages(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "messageId", required = true) String messageId,
			@RequestParam(value = "usertype", required = true) String userType, ModelMap model) throws Exception {
		final String method = "apis::";
		logger.debug(CLASSNAME + method + BEGIN);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", userType);
		model.addAttribute("messageId", messageId);
		model.addAttribute("headerflag", "dashboard");
		model.addAttribute("otheruser", "no");
		List<Message> messages = messageService.getMessagesByUserType(userType);
		messages.forEach((m)->m.setMsgId(m.getId().toString()));
		model.addAttribute("messages", messages);
		model.addAttribute("messageCount",messages.size());
		String forwardPage = null;
		if(MasterGSTConstants.ASPDEVELOPER.equalsIgnoreCase(userType)){
			forwardPage = "notifications/notification_asp";
		}
		logger.debug(CLASSNAME + method + END);
		return forwardPage;
	}

	public List<Metering> getMetering(List<Metering> lmetering, String dayandhour, long dayandhourValue)
			throws Exception {
		List<Metering> dayandhourMetering = Lists.newArrayList();
		logger.debug(CLASSNAME + "getMeterring:Begin");
		logger.debug(CLASSNAME + "getMeterring:dayandhour\t" + dayandhour);
		try {
			if (NullUtil.isNotEmpty(lmetering)) {
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				Date date = new Date();
				String dateStart = dateFormat.format(date);
				for (Metering metering : lmetering) {
					String dateStop = dateFormat.format(metering.getCreatedDate());
					Date d1 = dateFormat.parse(dateStart);
					Date d2 = dateFormat.parse(dateStop);

					long diff = (d1.getTime() - d2.getTime())/1000;
					long diffHours = diff / (60 * 60);
					long diffDays = diff / (24 * 60 * 60 * 1000);
					logger.debug(CLASSNAME + "diffDaysdiffDaysdiffDaysdiffDays\t" + diffDays);
					if (dayandhour.equalsIgnoreCase("Hour")) {
						if (diffHours <= dayandhourValue) {
							logger.debug(CLASSNAME + "diffHours <= dayandhourValue\t" + dayandhourValue);
							dayandhourMetering.add(metering);
						}
					} else if (dayandhour.equalsIgnoreCase("Day")) {
						if (diffDays <= dayandhourValue) {
							dayandhourMetering.add(metering);
						}
					} else {
						dayandhourMetering.add(metering);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dayandhourMetering;
	}

	public Map<String, String> getRequestMetering(List<Metering> lmetering) throws Exception {
		Map<String, String> meteringMap = Maps.newHashMap();
		String requestPerSecond = "0/0";
		int failCounter = 0;
		if (NullUtil.isNotEmpty(lmetering)) {
			long totalSeconds = 0;
			for (Metering metering : lmetering) {
				long diff = metering.getEndtime() - metering.getStarttime();
				totalSeconds = totalSeconds + diff;
				if (metering.getStatus().equalsIgnoreCase("Fail") && NullUtil.isNotEmpty(metering.getErrorMessage())) {
					failCounter++;
				}
			}
			requestPerSecond = lmetering.size() + "/" + TimeUnit.MILLISECONDS.toSeconds(totalSeconds);
		}
		meteringMap.put("requestPerSecond", requestPerSecond);
		meteringMap.put("failCounter", String.valueOf(failCounter));

		return meteringMap;
	}

	public int getErrorMetering(List<Metering> lmetering) throws Exception {
		int failCounter = 0;
		if (NullUtil.isNotEmpty(lmetering)) {
			for (Metering metering : lmetering) {
				if (metering.getStatus().equalsIgnoreCase("Fail")) {
					failCounter++;
				}
			}
		}
		return failCounter;
	}

	@RequestMapping(value = "/allindclientsinvoicessummarys", method = RequestMethod.GET)
	public @ResponseBody Map<String,Map<String, Map<String, String>>> getAllClientMonthlyReturnSummary(@RequestParam(value = "userid", required = true) String userid,@RequestParam(value = "year", required = true) int year,@RequestParam("clientIds") List<String> clientIds, ModelMap model,HttpServletRequest request) throws Exception {
		final String method = "getClientReturnSummary::";
		logger.debug(CLASSNAME + method + BEGIN);
		String yrcode = (year)+"-"+(year+1);
		if(isNotEmpty(clientIds)) {
			Map<String,Map<String, Map<String, String>>> allsummarymap = new HashMap<String,Map<String, Map<String, String>>>();
			Map<String, Map<String, String>> summaryMap = reportsService.getConsolidatedSummeryForYearMonthwise(clientIds, yrcode);
			Double totSales =0d,totpurchase = 0d,totbalance = 0d,totSalesTax = 0d,totPurchasetax = 0d,totTax = 0d,totExempted = 0d, totTcsAmount = 0d,ptotTcsAmount = 0d,totTdsAmount = 0d;
			int[] monthArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
			for (int i : monthArray) {
				totSales += Double.valueOf(summaryMap.get(i + "").get("Sales"));
				totpurchase += Double.valueOf(summaryMap.get(i + "").get("Purchase"));
				totbalance += Double.valueOf(summaryMap.get(i + "").get("Balance"));
				totSalesTax += Double.valueOf(summaryMap.get(i + "").get("SalesTax"));
				totPurchasetax += Double.valueOf(summaryMap.get(i + "").get("PurchaseTax"));
				totExempted += Double.valueOf(summaryMap.get(i + "").get("exempted"));
				totTcsAmount += Double.valueOf(summaryMap.get(i + "").get("tcsamount"));
				ptotTcsAmount += Double.valueOf(summaryMap.get(i + "").get("ptcsamount"));
				totTdsAmount += Double.valueOf(summaryMap.get(i + "").get("tdsamount"));
				totTax += Double.valueOf(summaryMap.get(i + "").get("Tax"));
			}
			for(int i=1; i<=12;i++) {
				if(i < 4) {
					Map<String, String> reportMap = summaryMap.get(i+"");
					Double cummulativeTax = 0d;
					if(Double.valueOf(summaryMap.get(i + "").get("Tax")) != 0) {
						for(int j=4 ; j<=12;j++) {
							cummulativeTax += Double.valueOf(summaryMap.get(j + "").get("Tax"));
						}
						for(int k=i ; k<=i && k>0;k--) {
							cummulativeTax += Double.valueOf(summaryMap.get(k + "").get("Tax"));
						}
					}
					reportMap.put("cummulativeTax", decimalFormat.format(cummulativeTax));
					summaryMap.put(i + "", reportMap);
				}else {
					Map<String, String> reportMap = summaryMap.get(i+"");
					Double cummulativeTax = 0d;
					if(Double.valueOf(summaryMap.get(i + "").get("Tax")) != 0) {
						for(int j=i ; j<=i && j>=4;j--) {
							cummulativeTax += Double.valueOf(summaryMap.get(j + "").get("Tax"));
						}
					}
					reportMap.put("cummulativeTax", decimalFormat.format(cummulativeTax));
					summaryMap.put(i + "", reportMap);
				}
			}
			Map<String, String> totReportMap = Maps.newHashMap();
			totReportMap.put("totSales", decimalFormat.format(totSales));
			totReportMap.put("totpurchase", decimalFormat.format(totpurchase));
			totReportMap.put("totbalance", decimalFormat.format(totbalance));
			totReportMap.put("totSalesTax", decimalFormat.format(totSalesTax));
			totReportMap.put("totPurchasetax", decimalFormat.format(totPurchasetax));
			totReportMap.put("totExempted", decimalFormat.format(totExempted));
			totReportMap.put("totTcsAmount", decimalFormat.format(totTcsAmount));
			totReportMap.put("ptotTcsAmount", decimalFormat.format(ptotTcsAmount));
			totReportMap.put("totTdsAmount", decimalFormat.format(totTdsAmount));
			totReportMap.put("totTax", decimalFormat.format(totTax));
			summaryMap.put("totals", totReportMap);
			allsummarymap.put("All",summaryMap);
			
			for(String clientid : clientIds) {
				List<String> clientId = Lists.newArrayList();
				clientId.add(clientid);
				Map<String, Map<String, String>> csummaryMap = reportsService.getConsolidatedSummeryForYearMonthwise(clientId, yrcode);
				Double ctotSales =0d,ctotpurchase = 0d,ctotbalance = 0d,ctotSalesTax = 0d,ctotPurchasetax = 0d,ctotTax = 0d,ctotExempted = 0d, ctotTcsAmount = 0d,pctotTcsAmount = 0d,ctotTdsAmount = 0d;
				int[] cmonthArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
				for (int i : cmonthArray) {
					ctotSales += Double.valueOf(csummaryMap.get(i + "").get("Sales"));
					ctotpurchase += Double.valueOf(csummaryMap.get(i + "").get("Purchase"));
					ctotbalance += Double.valueOf(csummaryMap.get(i + "").get("Balance"));
					ctotSalesTax += Double.valueOf(csummaryMap.get(i + "").get("SalesTax"));
					ctotPurchasetax += Double.valueOf(csummaryMap.get(i + "").get("PurchaseTax"));
					ctotExempted += Double.valueOf(csummaryMap.get(i + "").get("exempted"));
					ctotTcsAmount += Double.valueOf(csummaryMap.get(i + "").get("tcsamount"));
					pctotTcsAmount += Double.valueOf(csummaryMap.get(i + "").get("ptcsamount"));
					ctotTdsAmount += Double.valueOf(csummaryMap.get(i + "").get("tdsamount"));
					ctotTax += Double.valueOf(csummaryMap.get(i + "").get("Tax"));
				}
				for(int i=1; i<=12;i++) {
					if(i < 4) {
						Map<String, String> reportMap = csummaryMap.get(i+"");
						Double cummulativeTax = 0d;
						if(Double.valueOf(csummaryMap.get(i + "").get("Tax")) != 0) {
							for(int j=4 ; j<=12;j++) {
								cummulativeTax += Double.valueOf(csummaryMap.get(j + "").get("Tax"));
							}
							for(int k=i ; k<=i && k>0;k--) {
								cummulativeTax += Double.valueOf(csummaryMap.get(k + "").get("Tax"));
							}
						}
						reportMap.put("cummulativeTax", decimalFormat.format(cummulativeTax));
						csummaryMap.put(i + "", reportMap);
					}else {
						Map<String, String> reportMap = csummaryMap.get(i+"");
						Double cummulativeTax = 0d;
						if(Double.valueOf(csummaryMap.get(i + "").get("Tax")) != 0) {
							for(int j=i ; j<=i && j>=4;j--) {
								cummulativeTax += Double.valueOf(csummaryMap.get(j + "").get("Tax"));
							}
						}
						reportMap.put("cummulativeTax", decimalFormat.format(cummulativeTax));
						csummaryMap.put(i + "", reportMap);
					}
				}
				Map<String, String> ctotReportMap = Maps.newHashMap();
				ctotReportMap.put("totSales", decimalFormat.format(ctotSales));
				ctotReportMap.put("totpurchase", decimalFormat.format(ctotpurchase));
				ctotReportMap.put("totbalance", decimalFormat.format(ctotbalance));
				ctotReportMap.put("totSalesTax", decimalFormat.format(ctotSalesTax));
				ctotReportMap.put("totPurchasetax", decimalFormat.format(ctotPurchasetax));
				ctotReportMap.put("totExempted", decimalFormat.format(ctotExempted));
				ctotReportMap.put("totTcsAmount", decimalFormat.format(ctotTcsAmount));
				ctotReportMap.put("ptotTcsAmount", decimalFormat.format(pctotTcsAmount));
				ctotReportMap.put("totTdsAmount", decimalFormat.format(ctotTdsAmount));
				ctotReportMap.put("totTax", decimalFormat.format(ctotTax));
				csummaryMap.put("totals", ctotReportMap);
				Client cclient = clientService.findById(clientid);
				String clientname = "";
				if(isNotEmpty(cclient.getGstnnumber())) {
					clientname = clientname+cclient.getGstnnumber();
				}
				allsummarymap.put(clientname,csummaryMap);
			}
			
			//return summaryMap;
			return allsummarymap;
		}else {
			return null;
		}
	}
	
	
	
	@RequestMapping(value = "/allindclientsinvoicessummary", method = RequestMethod.GET)
	public @ResponseBody List<DashboardTotal> getAllClientMonthlyReturnSummaryTotals(@RequestParam(value = "userid", required = true) String userid,@RequestParam(value = "year", required = true) int year,@RequestParam("clientIds") List<String> clientIds, ModelMap model,HttpServletRequest request) throws Exception {
		final String method = "getClientReturnSummary::";
		logger.debug(CLASSNAME + method + BEGIN);
		String yrcode = (year)+"-"+(year+1);
		if(isNotEmpty(clientIds)) {
			List<DashboardTotal> dashboardtotal = Lists.newArrayList();
			DashboardTotal dbt = new DashboardTotal();
			Map<String, Map<String, String>> summaryMap = reportsService.getConsolidatedSummeryForYearMonthwise(clientIds, yrcode);
			Double totSales =0d,totpurchase = 0d,totexpenses = 0d,totbalance = 0d,totSalesTax = 0d,totPurchasetax = 0d,totTax = 0d,totExempted = 0d, totTcsAmount = 0d,ptotTcsAmount = 0d,totTdsAmount = 0d;
			int[] monthArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
			for (int i : monthArray) {
				totSales += Double.valueOf(summaryMap.get(i + "").get("Sales"));
				totpurchase += Double.valueOf(summaryMap.get(i + "").get("Purchase"));
				totexpenses += Double.valueOf(summaryMap.get(i + "").get("Expenses"));
				totbalance += Double.valueOf(summaryMap.get(i + "").get("Balance"));
				totSalesTax += Double.valueOf(summaryMap.get(i + "").get("SalesTax"));
				totPurchasetax += Double.valueOf(summaryMap.get(i + "").get("PurchaseTax"));
				totExempted += Double.valueOf(summaryMap.get(i + "").get("exempted"));
				totTcsAmount += Double.valueOf(summaryMap.get(i + "").get("tcsamount"));
				ptotTcsAmount += Double.valueOf(summaryMap.get(i + "").get("ptcsamount"));
				totTdsAmount += Double.valueOf(summaryMap.get(i + "").get("tdsamount"));
				totTax += Double.valueOf(summaryMap.get(i + "").get("Tax"));
			}
			for(int i=1; i<=12;i++) {
				if(i < 4) {
					Map<String, String> reportMap = summaryMap.get(i+"");
					Double cummulativeTax = 0d;
					if(Double.valueOf(summaryMap.get(i + "").get("Tax")) != 0) {
						for(int j=4 ; j<=12;j++) {
							cummulativeTax += Double.valueOf(summaryMap.get(j + "").get("Tax"));
						}
						for(int k=i ; k<=i && k>0;k--) {
							cummulativeTax += Double.valueOf(summaryMap.get(k + "").get("Tax"));
						}
					}
					reportMap.put("cummulativeTax", decimalFormat.format(cummulativeTax));
					summaryMap.put(i + "", reportMap);
				}else {
					Map<String, String> reportMap = summaryMap.get(i+"");
					Double cummulativeTax = 0d;
					if(Double.valueOf(summaryMap.get(i + "").get("Tax")) != 0) {
						for(int j=i ; j<=i && j>=4;j--) {
							cummulativeTax += Double.valueOf(summaryMap.get(j + "").get("Tax"));
						}
					}
					reportMap.put("cummulativeTax", decimalFormat.format(cummulativeTax));
					summaryMap.put(i + "", reportMap);
				}
			}
			Map<String, String> totReportMap = Maps.newHashMap();
			totReportMap.put("totSales", decimalFormat.format(totSales));
			totReportMap.put("totpurchase", decimalFormat.format(totpurchase));
			totReportMap.put("totexpenses", decimalFormat.format(totexpenses));
			totReportMap.put("totbalance", decimalFormat.format(totbalance));
			totReportMap.put("totSalesTax", decimalFormat.format(totSalesTax));
			totReportMap.put("totPurchasetax", decimalFormat.format(totPurchasetax));
			totReportMap.put("totExempted", decimalFormat.format(totExempted));
			totReportMap.put("totTcsAmount", decimalFormat.format(totTcsAmount));
			totReportMap.put("ptotTcsAmount", decimalFormat.format(ptotTcsAmount));
			totReportMap.put("totTdsAmount", decimalFormat.format(totTdsAmount));
			totReportMap.put("totTax", decimalFormat.format(totTax));
			summaryMap.put("totals", totReportMap);
			Client allclients = new Client();
			allclients.setBusinessname("Allclients");
			dbt.setClient(allclients);
			dbt.setSummaryMap(summaryMap);
			dashboardtotal.add(dbt);
			for(String clientid : clientIds) {
				List<String> clientId = Lists.newArrayList();
				clientId.add(clientid);
				Map<String, Map<String, String>> csummaryMap = reportsService.getConsolidatedSummeryForYearMonthwise(clientId, yrcode);
				Double ctotSales =0d,ctotpurchase = 0d,ctotexpenses = 0d,ctotbalance = 0d,ctotSalesTax = 0d,ctotPurchasetax = 0d,ctotTax = 0d,ctotExempted = 0d, ctotTcsAmount = 0d,pctotTcsAmount = 0d,ctotTdsAmount = 0d;
				int[] cmonthArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
				for (int i : cmonthArray) {
					ctotSales += Double.valueOf(csummaryMap.get(i + "").get("Sales"));
					ctotpurchase += Double.valueOf(csummaryMap.get(i + "").get("Purchase"));
					ctotexpenses += Double.valueOf(csummaryMap.get(i + "").get("Expenses"));
					ctotbalance += Double.valueOf(csummaryMap.get(i + "").get("Balance"));
					ctotSalesTax += Double.valueOf(csummaryMap.get(i + "").get("SalesTax"));
					ctotPurchasetax += Double.valueOf(csummaryMap.get(i + "").get("PurchaseTax"));
					ctotExempted += Double.valueOf(csummaryMap.get(i + "").get("exempted"));
					ctotTcsAmount += Double.valueOf(csummaryMap.get(i + "").get("tcsamount"));
					pctotTcsAmount += Double.valueOf(csummaryMap.get(i + "").get("ptcsamount"));
					ctotTdsAmount += Double.valueOf(csummaryMap.get(i + "").get("tdsamount"));
					ctotTax += Double.valueOf(csummaryMap.get(i + "").get("Tax"));
				}
				for(int i=1; i<=12;i++) {
					if(i < 4) {
						Map<String, String> reportMap = csummaryMap.get(i+"");
						Double cummulativeTax = 0d;
						if(Double.valueOf(csummaryMap.get(i + "").get("Tax")) != 0) {
							for(int j=4 ; j<=12;j++) {
								cummulativeTax += Double.valueOf(csummaryMap.get(j + "").get("Tax"));
							}
							for(int k=i ; k<=i && k>0;k--) {
								cummulativeTax += Double.valueOf(csummaryMap.get(k + "").get("Tax"));
							}
						}
						reportMap.put("cummulativeTax", decimalFormat.format(cummulativeTax));
						csummaryMap.put(i + "", reportMap);
					}else {
						Map<String, String> reportMap = csummaryMap.get(i+"");
						Double cummulativeTax = 0d;
						if(Double.valueOf(csummaryMap.get(i + "").get("Tax")) != 0) {
							for(int j=i ; j<=i && j>=4;j--) {
								cummulativeTax += Double.valueOf(csummaryMap.get(j + "").get("Tax"));
							}
						}
						reportMap.put("cummulativeTax", decimalFormat.format(cummulativeTax));
						csummaryMap.put(i + "", reportMap);
					}
				}
				Map<String, String> ctotReportMap = Maps.newHashMap();
				ctotReportMap.put("totSales", decimalFormat.format(ctotSales));
				ctotReportMap.put("totpurchase", decimalFormat.format(ctotpurchase));
				ctotReportMap.put("totexpenses", decimalFormat.format(ctotexpenses));
				ctotReportMap.put("totbalance", decimalFormat.format(ctotbalance));
				ctotReportMap.put("totSalesTax", decimalFormat.format(ctotSalesTax));
				ctotReportMap.put("totPurchasetax", decimalFormat.format(ctotPurchasetax));
				ctotReportMap.put("totExempted", decimalFormat.format(ctotExempted));
				ctotReportMap.put("totTcsAmount", decimalFormat.format(ctotTcsAmount));
				ctotReportMap.put("ptotTcsAmount", decimalFormat.format(pctotTcsAmount));
				ctotReportMap.put("totTdsAmount", decimalFormat.format(ctotTdsAmount));
				ctotReportMap.put("totTax", decimalFormat.format(ctotTax));
				csummaryMap.put("totals", ctotReportMap);
				DashboardTotal cdbt = new DashboardTotal();
				Client cclient = clientService.findById(clientid);
				cdbt.setClient(cclient);
				cdbt.setSummaryMap(csummaryMap);
				dashboardtotal.add(cdbt);
			}
			return dashboardtotal;
		}else {
			return null;
		}
	}
	
	@RequestMapping(value = "/allindclientsdwnldFinancialSummaryxls", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource downloadFinancialSummary(@RequestParam(value = "userid", required = true) String userid,@RequestParam(value = "year", required = true) int year,@RequestParam("clientIds") List<String> clientIds,HttpServletResponse response, HttpServletRequest request) throws Exception {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		String yrcode = (year)+"-"+(year+1);
		String filename = ""+yrcode+"-Financial_Summary.xls";
		response.setHeader("Content-Disposition", "inline; filename='"+yrcode+"-Financial_Summary.xls");
		Map<String, Map<String, String>> summaryMap = reportsService.getConsolidatedSummeryForYearMonthwise(clientIds, yrcode);
		Double totSales =0d,totpurchase = 0d,totexpenses = 0d,totbalance = 0d,totSalesTax = 0d,totPurchasetax = 0d,totTax = 0d,totExempted = 0d, totTcsAmount = 0d,ptotTcsAmount = 0d,totTdsAmount = 0d;
		int[] monthArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		for (int i : monthArray) {
			totSales += Double.valueOf(summaryMap.get(i + "").get("Sales"));
			totpurchase += Double.valueOf(summaryMap.get(i + "").get("Purchase"));
			totexpenses += Double.valueOf(summaryMap.get(i + "").get("Expenses"));
			totbalance += Double.valueOf(summaryMap.get(i + "").get("Balance"));
			totSalesTax += Double.valueOf(summaryMap.get(i + "").get("SalesTax"));
			totPurchasetax += Double.valueOf(summaryMap.get(i + "").get("PurchaseTax"));
			totExempted += Double.valueOf(summaryMap.get(i + "").get("exempted"));
			totTcsAmount += Double.valueOf(summaryMap.get(i + "").get("tcsamount"));
			ptotTcsAmount += Double.valueOf(summaryMap.get(i + "").get("ptcsamount"));
			totTdsAmount += Double.valueOf(summaryMap.get(i + "").get("tdsamount"));
			totTax += Double.valueOf(summaryMap.get(i + "").get("Tax"));
		}
		for(int i=1; i<=12;i++) {
			if(i < 4) {
				Map<String, String> reportMap = summaryMap.get(i+"");
				Double cummulativeTax = 0d;
				if(Double.valueOf(summaryMap.get(i + "").get("Tax")) != 0) {
					for(int j=4 ; j<=12;j++) {
						cummulativeTax += Double.valueOf(summaryMap.get(j + "").get("Tax"));
					}
					for(int k=i ; k<=i && k>0;k--) {
						cummulativeTax += Double.valueOf(summaryMap.get(k + "").get("Tax"));
					}
				}
				reportMap.put("cummulativeTax", decimalFormat.format(cummulativeTax));
				summaryMap.put(i + "", reportMap);
			}else {
				Map<String, String> reportMap = summaryMap.get(i+"");
				Double cummulativeTax = 0d;
				if(Double.valueOf(summaryMap.get(i + "").get("Tax")) != 0) {
					for(int j=i ; j<=i && j>=4;j--) {
						cummulativeTax += Double.valueOf(summaryMap.get(j + "").get("Tax"));
					}
				}
				reportMap.put("cummulativeTax", decimalFormat.format(cummulativeTax));
				summaryMap.put(i + "", reportMap);
			}
		}
		Map<String, String> totReportMap = Maps.newHashMap();
		totReportMap.put("totSales", decimalFormat.format(totSales));
		totReportMap.put("totpurchase", decimalFormat.format(totpurchase));
		totReportMap.put("totexpenses", decimalFormat.format(totexpenses));
		totReportMap.put("totbalance", decimalFormat.format(totbalance));
		totReportMap.put("totSalesTax", decimalFormat.format(totSalesTax));
		totReportMap.put("totPurchasetax", decimalFormat.format(totPurchasetax));
		totReportMap.put("totExempted", decimalFormat.format(totExempted));
		totReportMap.put("totTcsAmount", decimalFormat.format(totTcsAmount));
		totReportMap.put("ptotTcsAmount", decimalFormat.format(ptotTcsAmount));
		totReportMap.put("totTdsAmount", decimalFormat.format(totTdsAmount));
		totReportMap.put("totTax", decimalFormat.format(totTax));
		summaryMap.put("totals", totReportMap);
		Map<String, FinancialSummaryVO> summaryMapr = Maps.newHashMap();
		Iterator iter = summaryMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Map<String,String> monthSummary= (Map<String, String>) entry.getValue();
			String mnth = entry.getKey().toString();
			if(!"totals".equals(mnth)) {
			Iterator miter = monthSummary.entrySet().iterator();
			while (miter.hasNext()) {
				Map.Entry mentry = (Map.Entry) miter.next();
				if(summaryMapr.containsKey(mentry.getKey())) {
					String typ = mentry.getKey().toString();
					if(!typ.equals("sgst") && !typ.equals("cgst")  && !typ.equals("igst")) {
						
					FinancialSummaryVO sfinSummary = summaryMapr.get(mentry.getKey());
					switch(mnth) {
					case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
					break;
						
				}
				summaryMapr.put(mentry.getKey().toString(), sfinSummary);
					}
				}else {
					String typ = mentry.getKey().toString();
					if(!typ.equals("sgst") && !typ.equals("cgst")  && !typ.equals("igst")) {
					FinancialSummaryVO sfinSummary = new FinancialSummaryVO();
					switch(typ) {
					case "Sales": sfinSummary.setType("Sales");
					break;
					case "Purchase": sfinSummary.setType("Purchases");
					break;
					case "Expenses": sfinSummary.setType("Expenses");
					break;
					case "Balance": sfinSummary.setType("Balance");
					break;
					case "SalesTax": sfinSummary.setType("Output Tax");
					break;
					case "PurchaseTax": sfinSummary.setType("Input Tax");
					break;
					case "Tax": sfinSummary.setType("Monthly Tax");
					break;
					case "tcsamount": sfinSummary.setType("TCS Payable");
					break;
					case "ptcsamount": sfinSummary.setType("TCS Receivable");
					break;
					case "tdsamount": sfinSummary.setType("TDS Payable");
					break;
					case "exempted": sfinSummary.setType("Exempted");
					break;
					case "cummulativeTax": sfinSummary.setType("Cummulative Tax");
					break;
					default : sfinSummary.setType(typ);
					break;
					}
					
					switch(mnth) {
					case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
					break;
					}
					
					summaryMapr.put(mentry.getKey().toString(), sfinSummary);
					}
				}
			}
		}
		}
		
		Map<String,String> totals = summaryMap.get("totals");
		
		List<FinancialSummaryVO> financialSummaryVOList=Lists.newArrayList();
		Iterator summary = summaryMapr.entrySet().iterator();
		while (summary.hasNext()) {
			Map.Entry entry = (Map.Entry) summary.next();
			if("Sales".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totSales").toString()));
				financialSummaryVOList.add(sales);	
			}else if("Purchase".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totpurchase").toString()));
				financialSummaryVOList.add(sales);	
			}else if("Expenses".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totexpenses").toString()));
				financialSummaryVOList.add(sales);	
			}else if("Balance".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totbalance").toString()));
				financialSummaryVOList.add(sales);	
			}else if("Tax".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTax").toString()));
				financialSummaryVOList.add(sales);	
			}else if("SalesTax".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totSalesTax").toString()));
				financialSummaryVOList.add(sales);	
			}else if("PurchaseTax".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totPurchasetax").toString()));
				financialSummaryVOList.add(sales);	
			}else if("exempted".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totExempted").toString()));
				financialSummaryVOList.add(sales);	
			}else if("tcsamount".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTcsAmount").toString()));
				financialSummaryVOList.add(sales);	
			}else if("ptcsamount".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("ptotTcsAmount").toString()));
				financialSummaryVOList.add(sales);	
			}else if("tdsamount".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTdsAmount").toString()));
				financialSummaryVOList.add(sales);	
			}else if("cummulativeTax".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTax").toString()));
				financialSummaryVOList.add(sales);	
			}
				
		}
		
		File file = new File(filename);
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers = null;
					headers = Arrays.asList("", "April", "May", "June", "July", "August", "september","October", "November", "December", "January","February","March","YTD(Year To Date)");				
				
			SimpleExporter exporter = new SimpleExporter();
			exporter.gridExport(headers, financialSummaryVOList,"type, aprilamt, mayamt, juneamt,julyamt,augustamt,sepamt,octamt, novamt, decamt, janamt,febamt,maramt,totalamt",fos);
				
			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "downloadExcelData : ERROR", e);
		}
		return new FileSystemResource(new File(filename));
	}
}
