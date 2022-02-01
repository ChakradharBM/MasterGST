/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.jsoup.select.Evaluator.IsEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Lists;
import com.mastergst.configuration.service.EmailService;
import com.mastergst.configuration.service.SMSService;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.LeadsComments;
import com.mastergst.usermanagement.runtime.domain.PartnerBankDetails;
import com.mastergst.usermanagement.runtime.domain.PartnerClient;
import com.mastergst.usermanagement.runtime.repository.LeadsCommentsRepository;
import com.mastergst.usermanagement.runtime.repository.PartnerClientRepository;
import com.mastergst.usermanagement.runtime.service.PartnerService;
import com.mastergst.usermanagement.runtime.service.ProfileService;

/**
 * Handles Partner activities depending on the URI template.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 * @since 1.0
 */
@Controller
public class PartnerController {
	private static final Logger logger = LogManager.getLogger(PartnerController.class.getName());
	private static final String CLASSNAME = "PartnerController::";

	@Autowired
	PartnerService partnerService;
	@Autowired
	SMSService smsService;
	@Autowired
	EmailService emailService;
	@Autowired
	UserService userService;
	@Autowired
	ProfileService profileService;
	@Autowired
	PartnerClientRepository partnerClientRepository;
	@Autowired
	LeadsCommentsRepository leadsCommentsRepository;
	@Autowired
	UserRepository userRepository;;
	@RequestMapping(value = "/pinvit/{id}/{name}", method = RequestMethod.GET)
	public String invitations(@PathVariable("id") String id, @PathVariable("name") String fullname, ModelMap model)
			throws Exception {
		final String method = "invitations::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("user", userService.getUser(id));
		
		//model.addAttribute("clientsList", partnerService.getPartnerClients(id));
		model.addAttribute("page", "invite");
		logger.debug(CLASSNAME + method + END);
		List<String> list = Arrays.asList("GST API Sandbox","GST API Production","Eway Bill API Sandbox","Eway Bill API Production","Einvoice API Sandbox","Einvoice API Production","E-Invoice Software","Eway Bill Software","GST software","Suvidha Kendra","Tally Connector","SAP Connector","Oracle connector","MultiGSTN Tool","Reminders","Acknowledgements");
		model.addAttribute("productTypes", list);
		return "partners/invitations";
	}
	@RequestMapping(value = "/getpartnerinvitations/{id}/{name}", method = RequestMethod.GET)
	public @ResponseBody String invitations(@PathVariable("id") String id, @PathVariable("name") String fullname, ModelMap model, HttpServletRequest request)throws Exception {
		final String method = "invitations::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		Pageable pageable = null;
		Page<? extends PartnerClient> partners = partnerService.getPartners(pageable,id, start, length, searchVal);
		if(isNotEmpty(partners)) {
			for(PartnerClient partner : partners) {
				partner.setUserid(partner.getId().toString());
			}
		}
		logger.debug(CLASSNAME + method + END);
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=mapper.writer();
		return writer.writeValueAsString(partners);
	}
	@RequestMapping(value = "/getPartner/{partnerid}", method = RequestMethod.GET)
	public @ResponseBody String getSupplier(@PathVariable("partnerid") String partnerid, ModelMap model) throws Exception {
		final String method = "getPartner::";
		logger.debug(CLASSNAME + method + BEGIN);
		PartnerClient partners = partnerClientRepository.findOne(partnerid);
		if(partners != null){
			partners.setUserid(partners.getId().toString());
		}
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer=mapper.writer();
		writer=mapper.writer();
		return writer.writeValueAsString(partners);
	}
	/*@RequestMapping(value = "/paddclient/{id}/{name}", method = RequestMethod.POST)
	public String addPartnerClient(@ModelAttribute("client") PartnerClient client, @PathVariable("id") String id,
			@PathVariable("name") String fullname, ModelMap model) throws Exception {
		final String method = "addPartnerClient::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		if (NullUtil.isNotEmpty(client.getId()) && new ObjectId(id).equals(client.getId())) {
			client.setId(null);
		}
		
		if(NullUtil.isNotEmpty(client.getId())) {
		}else {
			int month=-1,year=-1,wkcd = 1,datecd =1;
			
			Date dt = new Date();
			if(dt != null){
				month = dt.getMonth();
				year = dt.getYear()+1900;
				Calendar cal = Calendar.getInstance();
				cal.setTime(dt);
				wkcd = cal.get(Calendar.WEEK_OF_MONTH);
				datecd = cal.get(Calendar.DAY_OF_MONTH);
			}
			int quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			client.setMthCd(month+"");
			client.setWeekCd(wkcd+"");
			client.setYrCd(yearCode+"");
			client.setDayCd(datecd+"");
		}
		User partner=userService.getUserById(id);
		
		if(NullUtil.isNotEmpty(partner)) {
			if(NullUtil.isNotEmpty(partner.getUserSequenceid())) {
				client.setRefid(partner.getUserSequenceid()+"");
				client.setPartnername(partner.getFullname());
				client.setPartneremail(partner.getEmail());
				client.setPartnermobileno(partner.getMobilenumber());	
			}
		}
		if(NullUtil.isNotEmpty(client.getIsLead()) && "true".equalsIgnoreCase(client.getIsLead())) {
			client.setStatus("New");
		}
		
		//client = partnerService.createClient(client);
		if(NullUtil.isNotEmpty(client.getIsLead()) && "false".equalsIgnoreCase(client.getIsLead()) && NullUtil.isEmpty(client.getId())) {
			logger.debug(CLASSNAME + method + " Send Invite");
			try {
				String inviteUrl = "http://app.mastergst.com/signupall?subscrid=&inviteId=" + id;
				emailService.sendEnrollEmail(client.getEmail(), "Dear " + client.getName() + ", <br><br>" + client.getContent()
						+ "<br><br><a href=\"" + inviteUrl + "\">Click here</a>",
						MasterGSTConstants.SIGNUPINVITE_EMAILSUBJECT);
				List<String> destinationNos = Lists.newArrayList();
				destinationNos.add(client.getMobilenumber());
				smsService.sendSMS(destinationNos,
						"Dear " + client.getName() + ", " + partner.getFullname() + " recommended MasterGST and invited you to join, Pls Click here for signup: " + inviteUrl, false,true);
			} catch (MasterGSTException e) {
				logger.error(CLASSNAME + method + "Message ERROR", e);
				throw new MasterGSTException();
			}
		}
		client = partnerService.createClient(client);
		List<String> list = Arrays.asList("GST API Sandbox","GST API Production","Eway Bill API Sandbox","Eway Bill API Production","Einvoice API Sandbox","Einvoice API Production","E-Invoice Software","Eway Bill Software","GST software","Suvidha Kendra","Tally Connector","SAP Connector","Oracle connector","MultiGSTN Tool","Reminders","Acknowledgements");
		model.addAttribute("productTypes", list);
		logger.debug(CLASSNAME + method + END);
		return "redirect:/pinvit/" + id + "/" + fullname;
	}*/
	
	@RequestMapping(value = "/pinvresend", method = RequestMethod.GET)
	public @ResponseBody String resendInvite(@RequestParam("userid") String userid,@RequestParam("id") String id, @RequestParam("name") String name,
			@RequestParam("content") String content, @RequestParam("email") String email,
			@RequestParam("mobile") String mobile, ModelMap model) throws Exception {
		final String method = "resendInvite::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		PartnerClient client = partnerService.findById(id);
		User partner = null;
		if(NullUtil.isNotEmpty(client)) {
			client.setStatus("Pending");
			partnerClientRepository.save(client);
			partner=userService.findByUsrId(client.getUserid());
		}
		try {
			String inviteUrl = "http://app.mastergst.com/signupall?subscrid=&inviteId=" + userid;
			emailService.sendEnrollEmail(email,
					"Dear " + name + ", <br><br>" + content + "<br><br><a href=\"" + inviteUrl + "\">Click here</a>",
					MasterGSTConstants.SIGNUPINVITE_EMAILSUBJECT);
			List<String> destinationNos = Lists.newArrayList();
			destinationNos.add(mobile);
			smsService.sendSMS(destinationNos,
					"Dear " + client.getName() + ", " + partner.getFullname() + " recommended MasterGST and invited you to join, Pls Click here for signup: " + inviteUrl, false,true);
			
			logger.debug(CLASSNAME + method + END);
			return "Success";
		} catch (MasterGSTException e) {
			logger.error(CLASSNAME + method + "Message ERROR", e);
			throw new MasterGSTException();
		}
		// return "Error";
	}

	@RequestMapping(value = "/pcnt/{id}/{name}", method = RequestMethod.GET)
	public String clients(@PathVariable("id") String id, @PathVariable("name") String fullname, @RequestParam String type, ModelMap model)
			throws Exception {
		final String method = "clients::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("user", userService.getUser(id));
		model.addAttribute("clientsList", partnerService.getPartnerJoinedClients(id));
		List<String> list = Arrays.asList("GST API Sandbox","GST API Production","Eway Bill API Sandbox","Eway Bill API Production","Einvoice API Sandbox","Einvoice API Production","E-Invoice Software","Eway Bill Software","GST software","Suvidha Kendra","Tally Connector","SAP Connector","Oracle connector","MultiGSTN Tool","Reminders","Acknowledgements");
		model.addAttribute("productTypes", list);
		logger.debug(CLASSNAME + method + END);
		
		if(MasterGSTConstants.NOT_REQUIRED.equals(type)) {
			return "partners/clients_notrequired";
		}
		
		return "partners/clients";
	}
	
	@RequestMapping(value = "/pcntdetials/{id}/{name}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> clientsDetails(@PathVariable("id") String id, 
			@PathVariable("name") String fullname, @RequestParam(required = false) String type,HttpServletRequest request) throws Exception {
		final String method = "clientsDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		if(type == null || "".equals(type.trim())){
			type = null;
		}
		String sortParam = null;
		String sortOrder = "des";
		
		String sortCol = request.getParameter("order[0][column]");
		if(!NullUtil.isEmpty(sortCol)){
			String field = sortCol;
			sortParam = request.getParameter("columns["+field+"][name]");
			if(field != null){
				sortOrder = request.getParameter("order[0][dir]");
			}
		}
		
		Page<PartnerClient> clients = partnerService.getPartnerJoinedClients(id, type, start, length, sortParam, sortOrder, searchVal);
		if(isNotEmpty(clients)) {
			for(PartnerClient partner : clients) {
				partner.setUserid(partner.getId().toString());
			}
		}
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("clients", clients);
		logger.debug(CLASSNAME + method + END);
		return dataMap;
	}
	
	@RequestMapping(value = "/paddclient/{id}/{name}", method = RequestMethod.POST)
	public String addPartnerClient(@ModelAttribute("client") PartnerClient client, @PathVariable("id") String id,
			@PathVariable("name") String fullname, ModelMap model) throws Exception {
		final String method = "addPartnerClient::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		int month=-1,year=-1,wkcd = 1,datecd =1;
		String docId = client.getId().toString();
		if (NullUtil.isNotEmpty(client.getId()) && new ObjectId(id).equals(client.getId())) {
			client.setId(null);
			docId = null;
		}
		
		PartnerClient dbClient = null;
		if(isNotEmpty(docId)) {
			dbClient = partnerClientRepository.findOne(docId);
		}
		if(isNotEmpty(dbClient)){
			if(isNotEmpty(dbClient.getClientid())) {
				client.setClientid(dbClient.getClientid());
			}
			if(isNotEmpty(dbClient.getStatus())) {
				client.setStatus(dbClient.getStatus());				
			}
			Date dt = new Date(); 
			if(isNotEmpty(dbClient.getCreatedDate())) {
				dt = dbClient.getCreatedDate();
			}
			client.setCreatedDate(dt);
			if(dt != null){
				month = dt.getMonth();
				year = dt.getYear()+1900;
				Calendar cal = Calendar.getInstance();
				cal.setTime(dt);
				wkcd = cal.get(Calendar.WEEK_OF_MONTH);
				datecd = cal.get(Calendar.DAY_OF_MONTH);
			}
			int quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			client.setMthCd(month+"");
			client.setWeekCd(wkcd+"");
			client.setYrCd(yearCode+"");
			client.setDayCd(datecd+"");
		}else {
			Date dt = new Date();
			if(dt != null){
				month = dt.getMonth();
				year = dt.getYear()+1900;
				Calendar cal = Calendar.getInstance();
				cal.setTime(dt);
				wkcd = cal.get(Calendar.WEEK_OF_MONTH);
				datecd = cal.get(Calendar.DAY_OF_MONTH);
			}
			int quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			client.setMthCd(month+"");
			client.setWeekCd(wkcd+"");
			client.setYrCd(yearCode+"");
			client.setDayCd(datecd+"");
		}
		
		
		
		/*if(NullUtil.isNotEmpty(client.getId())) {
		}else {
			int month=-1,year=-1,wkcd = 1,datecd =1;
			
			Date dt = new Date();
			if(dt != null){
				month = dt.getMonth();
				year = dt.getYear()+1900;
				Calendar cal = Calendar.getInstance();
				cal.setTime(dt);
				wkcd = cal.get(Calendar.WEEK_OF_MONTH);
				datecd = cal.get(Calendar.DAY_OF_MONTH);
			}
			int quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			client.setMthCd(month+"");
			client.setWeekCd(wkcd+"");
			client.setYrCd(yearCode+"");
			client.setDayCd(datecd+"");
		}*/
		User partner=userService.getUserById(id);
		
		if(NullUtil.isNotEmpty(partner)) {
			if(NullUtil.isNotEmpty(partner.getUserSequenceid())) {
				client.setRefid(partner.getUserSequenceid()+"");
				client.setPartnername(partner.getFullname());
				client.setPartneremail(partner.getEmail());
				client.setPartnermobileno(partner.getMobilenumber());	
			}
		}
		if(NullUtil.isNotEmpty(client.getIsLead()) && "true".equalsIgnoreCase(client.getIsLead())) {
			client.setStatus("New");
		}
		
		//TODO Bank Details
		
		
		//client = partnerService.createClient(client);
		if(NullUtil.isNotEmpty(client.getIsLead()) && "false".equalsIgnoreCase(client.getIsLead()) && NullUtil.isEmpty(client.getId())) {
			logger.debug(CLASSNAME + method + " Send Invite");
			/*try {
				String inviteUrl = "http://app.mastergst.com/signupall?subscrid=&inviteId=" + id;
				//emailService.sendEnrollEmail(client.getEmail(), "Dear " + client.getName() + ", <br><br>" + client.getContent()
				//		+ "<br><br><a href=\"" + inviteUrl + "\">Click here</a>", MasterGSTConstants.SIGNUPINVITE_EMAILSUBJECT);
				List<String> destinationNos = Lists.newArrayList();
				destinationNos.add(client.getMobilenumber());
				//smsService.sendSMS(destinationNos, "Dear " + client.getName() + ", " + partner.getFullname() + " recommended MasterGST and invited you to join, Pls Click here for signup: " + inviteUrl, false,true);
			} catch (MasterGSTException e) {
				logger.error(CLASSNAME + method + "Message ERROR", e);
				throw new MasterGSTException();
			}*/
		}
		client = partnerService.createClient(client);
		List<String> list = Arrays.asList("GST API Sandbox","GST API Production","Eway Bill API Sandbox","Eway Bill API Production","Einvoice API Sandbox","Einvoice API Production","E-Invoice Software","Eway Bill Software","GST software","Suvidha Kendra","Tally Connector","SAP Connector","Oracle connector","MultiGSTN Tool","Reminders","Acknowledgements");
		model.addAttribute("productTypes", list);
		logger.debug(CLASSNAME + method + END);
		return "redirect:/pinvit/" + id + "/" + fullname;
	}

	@RequestMapping(value = "/pbil/{id}/{name}", method = RequestMethod.GET)
	public String billing(@PathVariable("id") String id, @PathVariable("name") String fullname, ModelMap model)
			throws Exception {
		final String method = "billing::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("user", userService.getUser(id));
		model.addAttribute("clientsList", partnerService.getPartnerBilledClients(id));
		logger.debug(CLASSNAME + method + END);
		return "partners/billing";
	}
	
	@RequestMapping(value = "/pbankDetails/{id}/{name}", method = RequestMethod.GET)
	public String bankDetails(@PathVariable("id") String id, @PathVariable("name") String fullname, ModelMap model)
			throws Exception {
		final String method = "billing::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("user", userService.getUser(id));
		model.addAttribute("bankdetails", profileService.getPBankDetails(id));
		logger.debug(CLASSNAME + method + END);
		return "partners/bankdetails";
	}
	
	@RequestMapping(value = "/createpbankdetails/{id}/{name}", method = RequestMethod.POST)
	public String addBankDetails(@ModelAttribute("bankdetails") PartnerBankDetails bankdetails, @PathVariable("id") String id,
			@PathVariable("name") String fullname, ModelMap model) throws Exception {
		final String method = "addBankDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if (NullUtil.isNotEmpty(bankdetails.getId()) && new ObjectId(id).equals(bankdetails.getId())) {
			bankdetails.setId(null);
		}
		profileService.savePBankDetails(bankdetails);
		return "redirect:/pbankDetails/" + id + "/" + fullname;
	}

	@RequestMapping(value = "/leadscomments/{inviteid}")
	public @ResponseBody  List<LeadsComments> LeadsComments(@PathVariable("inviteid") String inviteid,ModelMap model) throws Exception {
		  
		List<LeadsComments> comments = leadsCommentsRepository.findByInviteid(inviteid);
		 
		Collections.sort(comments, Collections.reverseOrder());
		return comments;	 
	}
	@RequestMapping(value ="/saveLeadsComments/{inviteid}/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public @ResponseBody List<LeadsComments> saveLeadComments(@RequestBody MultiValueMap<String, String> messageData,
			@PathVariable("inviteid") String inviteid, @PathVariable("id") String id) throws MasterGSTException {
		User user = userService.findById(id);
		LeadsComments comments = new LeadsComments();
		comments.setUserid(id);
		comments.setInviteid(inviteid);
		if(isNotEmpty(user) && isNotEmpty(user.getFullname())) {
			comments.setAddedby(user.getFullname());
		}
		comments.setLeadscomments(messageData.getFirst("comments"));
		comments.setCommentDate(new Date());
		leadsCommentsRepository.save(comments);
		 List<LeadsComments> cmmntsLst = leadsCommentsRepository.findByInviteid(inviteid);
		Collections.sort(cmmntsLst, Collections.reverseOrder());
		return cmmntsLst;
	}
	@RequestMapping(value = "/getLeadComments")
	public @ResponseBody  List<LeadsComments> getLeadsComments(
			@RequestParam("id") String id,ModelMap model) throws Exception {
		//System.out.println(email);
		PartnerClient user = partnerClientRepository.findOne(id);
		List<LeadsComments> comments = null;
		if(isNotEmpty(user)) {
			comments= leadsCommentsRepository.findByInviteid(user.getId().toString());
		}
		Collections.sort(comments, Collections.reverseOrder());
		return comments;	 
	}
	
	@RequestMapping(value = "/getSoftwareTypes", method = RequestMethod.GET)
	public @ResponseBody List<String> softwareTypesData(ModelMap model) throws Exception {
		final String method = "industryTypeData::";
		logger.debug(CLASSNAME + method + BEGIN);
		List<String> list = Arrays.asList("GST API Sandbox","GST API Production","Eway Bill API Sandbox","Eway Bill API Production","Einvoice API Sandbox","Einvoice API Production","E-Invoice Software","Eway Bill Software","GST software","Suvidha Kendra","Tally Connector","SAP Connector","Oracle connector","MultiGSTN Tool","Reminders","Acknowledgements");
		model.addAttribute("productTypes", list);
		return list;
	}
}
