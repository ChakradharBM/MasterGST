/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.dao.PartnersDao;
import com.mastergst.usermanagement.runtime.domain.ClientPaymentDetails;
import com.mastergst.usermanagement.runtime.domain.PartnerClient;
import com.mastergst.usermanagement.runtime.domain.PartnerClientInfo;
import com.mastergst.usermanagement.runtime.domain.PartnerFilter;
import com.mastergst.usermanagement.runtime.domain.PartnerPayments;
import com.mastergst.usermanagement.runtime.domain.PaymentDetails;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.domain.TotalLeadDetails;
import com.mastergst.usermanagement.runtime.repository.PartnerClientRepository;
import com.mastergst.usermanagement.runtime.repository.PartnerPaymentsRepository;
import com.mastergst.usermanagement.runtime.repository.PaymentDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.SubscriptionDetailsRepository;
import com.mastergst.usermanagement.runtime.support.AdminUtils;
import com.mastergst.usermanagement.runtime.support.Utility;

/**
 * Service interface for Partner's Client to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class PartnerServiceImpl implements PartnerService {
	
	private static final Logger logger = LogManager.getLogger(PartnerServiceImpl.class.getName());
	private static final String CLASSNAME = "PartnerServiceImpl::";
	private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
	
	@Autowired
	PartnerClientRepository partnerClientRepository;
	@Autowired
	SubscriptionDetailsRepository subscriptionDetailsRepository;
	@Autowired
	UserService userService;
	@Autowired
	PartnersDao partnersDao;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private PartnerPaymentsRepository partnerPaymentsRepository;
	@Autowired
	private AdminUtils adminUtils;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private PaymentDetailsRepository paymentDetailsRepository;
	
	@Override
	@Transactional
	public PartnerClient createClient(PartnerClient partnerClient) {
		final String method = "createClient::";
		logger.debug(CLASSNAME + method + BEGIN);
		//partnerClient.setRefid(randomNumber(4));
		return partnerClientRepository.save(partnerClient);
	}

	@Override
	@Transactional
	public List<PartnerClient> getPartnerClients(String userid) {
		final String method = "getPartnerClients::";
		logger.debug(CLASSNAME + method + BEGIN);
		List<PartnerClient> clients = partnerClientRepository.findByUserid(userid);
		/*if(NullUtil.isNotEmpty(clients)) {
			for(PartnerClient client : clients) {
				if(NullUtil.isEmpty(client.getRefid())) { client.setRefid(randomNumber(4)); }
			}
		}
		*/
		/*if(isNotEmpty(clients)) {
			Collections.sort(clients, (c1, c2)->{
				System.out.println(c2.getCreatedDate().compareTo(c1.getCreatedDate()));
				return c2.getCreatedDate().compareTo(c1.getCreatedDate());
			});			
		}*/
		logger.debug(CLASSNAME + method + END);
		return clients;
	}
	@Override
	public Page<? extends PartnerClient> getPartners(Pageable pageable, String id, int start, int length,String searchVal) {
		Page<? extends PartnerClient> partners = null;
		partners = partnersDao.getPartners(id,start, length, searchVal);
		return partners;
	}
	@Override
	@Transactional
	public List<PartnerClient> getPartnerJoinedClients(String userid) {
		final String method = "getPartnerJoinedClients::";
		logger.debug(CLASSNAME + method + BEGIN);
		List<PartnerClient> clients = partnerClientRepository.findByUseridAndClientidIsNotNull(userid);
		if(NullUtil.isNotEmpty(clients)) {
			for(PartnerClient client : clients) {
				SubscriptionDetails subscriptionData = subscriptionDetailsRepository.findByUserid(client.getClientid());
				if(NullUtil.isNotEmpty(subscriptionData)){
					client.setSubscriptionAmount(subscriptionData.getPaidAmount());
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
		return clients;
	}
	
	@Override
	@Transactional
	public Page<PartnerClient> getPartnerJoinedClients(String userid, String type, int start, int length, String sortParam, String sortOrder, String searchVal) {
		final String method = "getPartnerJoinedClients::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		Criteria criteria = Criteria.where("userid").is(userid);
		if(StringUtils.hasLength(type)){
			criteria.and("salesstatus").is(type);
		}else {
			criteria.and("clientid").nin("", null);
		}
		Query query = Query.query(criteria);
		long total = mongoTemplate.count(query, PartnerClient.class, "partner_client");
		if (total == 0) {
			return new PageImpl<PartnerClient>(Collections.<PartnerClient> emptyList());
		}
		
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Sort sort = null;
		if(NullUtil.isNotEmpty(sortParam) && NullUtil.isNotEmpty(sortOrder)){	
			sort = new Sort(
					new Order("asc".equalsIgnoreCase(sortOrder) ? Direction.ASC : Direction.DESC, "createdDate"),
					new Order("asc".equalsIgnoreCase(sortOrder) ? Direction.ASC : Direction.DESC, "needFollowupdate"),
					new Order("asc".equalsIgnoreCase(sortOrder) ? Direction.ASC : Direction.DESC, sortParam)
					);
		}else {
			sort = new Sort(new Order(Direction.DESC, "createdDate"));
		}
		Pageable pageable = null;
		if(start == 0 && length == 0){
		}else {
			pageable = new PageRequest(start/length, length,sort);
			query.with(pageable);
		}
		Page<PartnerClient> clients =new PageImpl<PartnerClient>(mongoTemplate.find(query, PartnerClient.class, "partner_client"), pageable, total);
		
		
		if(NullUtil.isNotEmpty(clients) && NullUtil.isNotEmpty(clients.getContent())) {
			for(PartnerClient client : clients.getContent()) {
				client.setUserid(client.getId().toString());
				SubscriptionDetails subscriptionData = subscriptionDetailsRepository.findByUserid(client.getClientid());
				if(NullUtil.isNotEmpty(subscriptionData)){
					client.setSubscriptionAmount(subscriptionData.getPaidAmount());
				}else {
					client.setSubscriptionAmount(0d);
				}
			}
		}
		return clients;
	}
	
	@Override
	@Transactional
	public List<PartnerClient> getPartnerBilledClients(String userid) {
		final String method = "getPartnerBilledClients::";
		logger.debug(CLASSNAME + method + BEGIN);
		List<PartnerClient> clients = partnerClientRepository.findByUseridAndClientidIsNotNull(userid);
		if(NullUtil.isNotEmpty(clients)) {
			for(PartnerClient client : clients) {
				if(NullUtil.isEmpty(client.getRefid())) {
					client.setRefid(randomNumber(4));
				}
				SubscriptionDetails subscriptionData = subscriptionDetailsRepository.findByUserid(client.getClientid());
				if(NullUtil.isNotEmpty(subscriptionData)){
					client.setSubscriptionAmount(subscriptionData.getPaidAmount());
					client.setUpdatedDate(subscriptionData.getUpdatedDate());
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
		return clients;
	}
	
	@Override
	@Transactional
	public void updatePartnerDetails(final String inviteId, final User user) {
		final String method = "updatePartnerDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		//PartnerClient partnerClient = partnerClientRepository.findOne(inviteId);
		
		PartnerClient partnerClient = partnerClientRepository.findByEmail(user.getEmail());
		
		User partner=userService.findById(inviteId);
		
		if(NullUtil.isEmpty(partnerClient)) {
			partnerClient = new PartnerClient();
			partnerClient.setUserid(inviteId);
			partnerClient.setFullname(user.getFullname());
			//partnerClient.setRefid(randomNumber(4));
			partnerClient.setName(user.getFullname());
			partnerClient.setEmail(user.getEmail());
			partnerClient.setMobilenumber(user.getMobilenumber());
			
			partnerClient.setJoinDate(new Date());
		}
		
		if(NullUtil.isEmpty(partnerClient.getJoinDate())) {
			partnerClient.setJoinDate(new Date());
		}
		
		partnerClient.setClienttype(user.getType());
		
		if(NullUtil.isNotEmpty(partner)) {
			partnerClient.setRefid(partner.getUserSequenceid()+"");
			partnerClient.setPartnername(partner.getFullname());
			partnerClient.setPartneremail(partner.getEmail());
			partnerClient.setPartnermobileno(partner.getMobilenumber());	
		}
		partnerClient.setClientid(user.getId().toString());
		partnerClient.setStatus("Joined");
		partnerClientRepository.save(partnerClient);
		logger.debug(CLASSNAME + method + END);
	}
	
	@Override
	@Transactional
	public void updatePartnerDetailsFromAdmin(final String inviteId, final User user) {
		final String method = "updatePartnerDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		PartnerClient partnerClient = partnerClientRepository.findByEmail(user.getEmail());
		
		User partner=userService.findById(inviteId);
		if(NullUtil.isEmpty(partnerClient)) {
			partnerClient = new PartnerClient();
			partnerClient.setUserid(inviteId);
			partnerClient.setFullname(user.getFullname());
			
			partnerClient.setName(user.getFullname());
			partnerClient.setEmail(user.getEmail());
			partnerClient.setMobilenumber(user.getMobilenumber());
			partnerClient.setJoinDate(new Date());
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
			partnerClient.setMthCd(month+"");
			partnerClient.setWeekCd(wkcd+"");
			partnerClient.setYrCd(yearCode+"");
			partnerClient.setDayCd(datecd+"");
		}else {
			partnerClient.setUserid(inviteId);
		}
		if(NullUtil.isEmpty(partnerClient.getJoinDate())) {
			partnerClient.setJoinDate(new Date());
		}
		partnerClient.setClienttype(user.getType());
		
		if(NullUtil.isNotEmpty(partner)) {
			partnerClient.setRefid(partner.getUserSequenceid()+"");
			partnerClient.setPartnername(partner.getFullname());
			partnerClient.setPartneremail(partner.getEmail());
			partnerClient.setPartnermobileno(partner.getMobilenumber());
		}
		
		partnerClient.setClientid(user.getId().toString());
		partnerClient.setStatus("Joined");
		partnerClientRepository.save(partnerClient);
		logger.debug(CLASSNAME + method + END);
	}
	
	private static String randomNumber(int len) {
		String numbers = "0123456789";
		Random rndm_method = new Random();
		char[] otp = new char[len];
		for (int i = 0; i < len; i++) {
			otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length()));
		}
		return new String(otp);
	}
	
	
	@Override
	public List<PartnerClient> getPartnerReferenceClients(String userid) {
		final String method = "getPartnerBilledClients::";
		logger.debug(CLASSNAME + method + BEGIN);
		List<PartnerClient> clients = partnerClientRepository.findByUserid(userid);
		
		List<PartnerClient> referenceClients=new ArrayList<PartnerClient>();
		if(NullUtil.isNotEmpty(clients)) {
			
			List<String> clientids=new ArrayList<String>();
			for(PartnerClient client : clients) {
				if(NullUtil.isNotEmpty(client.getClientid())) {
					clientids.add(client.getClientid());					
				}
			}
			
			List<SubscriptionDetails> subscriptionData = null;
			
			if(NullUtil.isNotEmpty(clientids)) {			
				subscriptionData=subscriptionDetailsRepository.findByUseridIn(clientids);
			}
			Map<String,Double> subscriptionMap=new HashMap<String,Double>();
			
			if(NullUtil.isNotEmpty(subscriptionData)){
				for(SubscriptionDetails data: subscriptionData) {
					double subscriptionAmount=0;
					if(subscriptionMap.containsKey(data.getUserid())) {
						double existAmount=subscriptionMap.get(data.getUserid());
						if(NullUtil.isNotEmpty(data) && NullUtil.isNotEmpty(data.getPaidAmount())){
							existAmount+=subscriptionAmount=data.getPaidAmount();
						}
						subscriptionMap.put(data.getUserid(), existAmount);
					}else {
						if(NullUtil.isNotEmpty(data) && NullUtil.isNotEmpty(data.getPaidAmount())){
							subscriptionAmount=data.getPaidAmount();
						}
						subscriptionMap.put(data.getUserid(), subscriptionAmount);
					}
				}
			}
			
			for(PartnerClient client : clients) {
				if(NullUtil.isNotEmpty(client.getClientid())) {
					double aubscriptionAmount=subscriptionMap.get(client.getClientid());
					if(NullUtil.isNotEmpty(aubscriptionAmount)) {
						client.setSubscriptionAmount(aubscriptionAmount);
					}else {
						client.setSubscriptionAmount(0.0);
					}
				}else {
					client.setSubscriptionAmount(0.0);					
				}
				referenceClients.add(client);
			}
			
		}
		logger.debug(CLASSNAME + method + END);
		return referenceClients;
	}
	
	
	@Override
	public Page<PartnerClient> getPartnerClients(int month,String year,int start, int  length, String sortParam, String sortOrder,String  searchVal) {
		Query query = new Query();
		Criteria criteria =  Criteria.where("yrCd").is(year);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if(NullUtil.isNotEmpty(searchVal)){
			criteria = new Criteria().andOperator(
					criteria, new Criteria().orOperator(
							Criteria.where("fullname").regex(searchVal,"i"), 
							Criteria.where("email").regex(searchVal,"i"), 
							Criteria.where("mobilenumber").regex(searchVal,"i"),
							Criteria.where("partnername").regex(searchVal,"i"),
							Criteria.where("partneremail").regex(searchVal,"i"),
							Criteria.where("partnermobileno").regex(searchVal,"i"),
							Criteria.where("status").regex(searchVal,"i"),
							Criteria.where("clienttype").regex(searchVal)
					)
			);
		}
		query.addCriteria(criteria);
		Sort sort = new Sort(new Order(Direction.DESC, "createdDate"));
		
		Pageable pageable = new PageRequest(start, length, sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, PartnerClient.class, "partner_client");
		if (total == 0) {
			return new PageImpl<PartnerClient>(Collections.<PartnerClient> emptyList());
		}
		return new PageImpl<PartnerClient>(mongoTemplate.find(query, PartnerClient.class, "partner_client"), pageable, total);
	}
	
	@Override
	public Page<PartnerClient> getPartnerClients(int month,String year,int start, int  length, String sortParam, String sortOrder,String  searchVal, PartnerFilter filter,List<String> userids) {
		Query query = new Query();
		Criteria criteria =  Criteria.where("userid").in(userids).and("yrCd").is(year);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if(NullUtil.isNotEmpty(searchVal)){
			criteria = new Criteria().andOperator(
					criteria, new Criteria().orOperator(
							Criteria.where("fullname").regex(searchVal,"i"), 
							Criteria.where("email").regex(searchVal,"i"), 
							Criteria.where("mobilenumber").regex(searchVal,"i"),
							Criteria.where("partnername").regex(searchVal,"i"),
							Criteria.where("partneremail").regex(searchVal,"i"),
							Criteria.where("partnermobileno").regex(searchVal,"i"),
							Criteria.where("status").regex(searchVal,"i"),
							Criteria.where("clienttype").regex(searchVal)
					)
			);
		}
		if(filter.getPartnername() != null){
			criteria.and("partnername").in(Arrays.asList(filter.getPartnername()));
		}
		query.addCriteria(criteria);
		Sort sort = null;
		if(NullUtil.isNotEmpty(sortParam) && NullUtil.isNotEmpty(sortOrder)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(sortOrder) ? Direction.ASC : Direction.DESC, sortParam));
		}
		Pageable pageable = new PageRequest(start, length, sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, PartnerClient.class, "partner_client");
		if (total == 0) {
			return new PageImpl<PartnerClient>(Collections.<PartnerClient> emptyList());
		}
		return new PageImpl<PartnerClient>(mongoTemplate.find(query, PartnerClient.class, "partner_client"), pageable, total);
	}
	
	@Override
	public List<PartnerClient> getAllPartnerClients(){
		
		return partnerClientRepository.findAll();
	}
	
	@Override
	public void updatePartnerClientInfo(List<PartnerClient> filteredLst) {
		
		partnerClientRepository.save(filteredLst);
	}

	@Override
	public PartnerClient findById(String id) {
		return partnerClientRepository.findOne(id);
	}
@Override
public Page<User> getPartners(String tabName, int start, int length, String sortParam, String sortOrder,String searchVal) {
	Query query = new Query();
	Criteria criteria = new Criteria();
	if("salesTab".equalsIgnoreCase(tabName)) {
		criteria = Criteria.where("partnerType").is("Sales Team");
	}else if("partnersTab".equalsIgnoreCase(tabName)) {
		criteria = Criteria.where("partnerType").ne("Sales Team");
	}
	
	query.addCriteria(criteria);
	Sort sort = null;
	if(NullUtil.isNotEmpty(sortParam) && NullUtil.isNotEmpty(sortOrder)){	
		sort = new Sort(new Order("asc".equalsIgnoreCase(sortOrder) ? Direction.ASC : Direction.DESC, sortParam));
	}
	Pageable pageable = new PageRequest(start, length, sort);
	query.with(pageable);
	long total = mongoTemplate.count(query, User.class, "users");
	if (total == 0) {
		return new PageImpl<User>(Collections.<User> emptyList());
	}
	return new PageImpl<User>(mongoTemplate.find(query, User.class, "users"), pageable, total);
}
@Override
public Page<User> getPartners(String tabName) {
	Query query = new Query();
	Criteria criteria = new Criteria();
	if("salesTab".equalsIgnoreCase(tabName)) {
		criteria = Criteria.where("partnerType").is("Sales Team");
	}else if("partnersTab".equalsIgnoreCase(tabName)) {
		criteria = Criteria.where("partnerType").ne("Sales Team");
	}
	
	query.addCriteria(criteria);
	Pageable pageable = null;
	long total = mongoTemplate.count(query, User.class, "users");
	if (total == 0) {
		return new PageImpl<User>(Collections.<User> emptyList());
	}
	return new PageImpl<User>(mongoTemplate.find(query, User.class, "users"), pageable, total);
}
@Override
public Page<PartnerClient> getSalesTeamPartners(String tabName, List<String> userids, int month, int year,int start, int length, String sortParam, String sortOrder, String searchVal) {
	Page<PartnerClient> pclient = null;
	String yearCode = Utility.getYearCode(month, year);
	pclient = partnersDao.getSalesTeamPartners(tabName, userids,month,yearCode, start, length, sortParam, sortOrder, searchVal);
	return pclient;
}
@Override
public Map<String, Map<String, String>> getConsolidatedSummeryForDays(String yearCode) {
	List<TotalLeadDetails> leadDetails = partnersDao.getConsolidatedSummeryForYear(yearCode);
	
	return null;
}
@Override
public Map<String, Map<String, String>> getConsolidatedSummeryForDaysInMonth(String yearCode,int mthcd,String dayWeek,String tabName,List<String> userids) {
	PartnerFilter filter = null;
	List<TotalLeadDetails> leadDetails = partnersDao.getConsolidatedSummeryForDaysInMonth(yearCode, mthcd,dayWeek,tabName,userids,filter);
	List<TotalLeadDetails> pleadDetails = partnersDao.getConsolidatedSummeryForDaysInMonthPending(yearCode, mthcd,dayWeek,tabName,userids,filter);
	List<TotalLeadDetails> jleadDetails = partnersDao.getConsolidatedSummeryForDaysInMonthJoined(yearCode, mthcd,dayWeek,tabName,userids,filter);
	List<TotalLeadDetails> nleadDetails = partnersDao.getConsolidatedSummeryForDaysInMonthNew(yearCode, mthcd,dayWeek,tabName,userids,filter);
	Map<String, TotalLeadDetails> summerySlsData = new HashMap<String, TotalLeadDetails>();
	for(TotalLeadDetails gstr1InvoiceAmount : leadDetails){
		String code = gstr1InvoiceAmount.get_id();
		summerySlsData.put(code, gstr1InvoiceAmount);
	}
	Map<String, TotalLeadDetails> psummerySlsData = new HashMap<String, TotalLeadDetails>();
	for(TotalLeadDetails gstr1InvoiceAmount : pleadDetails){
		String code = gstr1InvoiceAmount.get_id();
		psummerySlsData.put(code, gstr1InvoiceAmount);
	}
	Map<String, TotalLeadDetails> jsummerySlsData = new HashMap<String, TotalLeadDetails>();
	for(TotalLeadDetails gstr1InvoiceAmount : jleadDetails){
		String code = gstr1InvoiceAmount.get_id();
		jsummerySlsData.put(code, gstr1InvoiceAmount);
	}
	Map<String, TotalLeadDetails> nsummerySlsData = new HashMap<String, TotalLeadDetails>();
	for(TotalLeadDetails gstr1InvoiceAmount : nleadDetails){
		String code = gstr1InvoiceAmount.get_id();
		nsummerySlsData.put(code, gstr1InvoiceAmount);
	}
	Map<String, Map<String, String>> summeryReturnData = new HashMap<String, Map<String, String>>();
	int ct = 31;
	
	if(mthcd > 0) {
		if("weekTable".equalsIgnoreCase(dayWeek)) {
			ct = 5;
		}
	}else {
		ct = 12;
	}
	
	int ttotalTransactions = 0, ttotalPending =0, ttotalJoined =0, ttotalNew =0;
	Double testimatedAmt = 0d, tsubscriptionAmt = 0d;
	for(int i=1; i<=ct; i++){
		String cd = Integer.toString(i);
		int totalTransactions = 0, totalPending =0, totalJoined =0, totalNew =0;
		Double estimatedAmt = 0d, subscriptionAmt = 0d;
		TotalLeadDetails invoiceAmountSls = summerySlsData.get(cd);
		TotalLeadDetails pinvoiceAmountSls = psummerySlsData.get(cd);
		TotalLeadDetails jinvoiceAmountSls = jsummerySlsData.get(cd);
		TotalLeadDetails ninvoiceAmountSls = nsummerySlsData.get(cd);
		Map<String, String> reportMap = new HashMap<String, String>();
		summeryReturnData.put(cd, reportMap);
		if(invoiceAmountSls != null){
			totalTransactions = invoiceAmountSls.getTotalLeads();
			ttotalTransactions +=totalTransactions; 
			estimatedAmt = invoiceAmountSls.getEstimatedCost().doubleValue();
			subscriptionAmt = invoiceAmountSls.getSubscriptionAmount().doubleValue();
			testimatedAmt += estimatedAmt; 
			tsubscriptionAmt += subscriptionAmt;
			if(pinvoiceAmountSls != null) {
				totalPending = pinvoiceAmountSls.getTotalPending();
				ttotalPending += totalPending;
			}
			if(jinvoiceAmountSls != null) {
				totalJoined = jinvoiceAmountSls.getTotalJoined();
				ttotalJoined += totalJoined;
			}
			if(ninvoiceAmountSls != null) {
				totalNew = ninvoiceAmountSls.getTotalNew();
				ttotalNew += totalNew;
			}
		}
		reportMap.put("estimatedAmt", decimalFormat.format(estimatedAmt));
		reportMap.put("subscriptionAmt", decimalFormat.format(subscriptionAmt));
		reportMap.put("totalLeads", String.valueOf(totalTransactions));
		reportMap.put("totalPending", String.valueOf(totalPending));
		reportMap.put("totalJoined", String.valueOf(totalJoined));
		reportMap.put("totalNew", String.valueOf(totalNew));
		
	}
	
	Map<String, String> totreportMap = new HashMap<String, String>();
	
	totreportMap.put("estimatedAmt", decimalFormat.format(testimatedAmt));
	totreportMap.put("subscriptionAmt", decimalFormat.format(tsubscriptionAmt));
	totreportMap.put("totalLeads", String.valueOf(ttotalTransactions));
	totreportMap.put("totalPending", String.valueOf(ttotalPending));
	totreportMap.put("totalJoined", String.valueOf(ttotalJoined));
	totreportMap.put("totalNew", String.valueOf(ttotalNew));
	summeryReturnData.put("totals", totreportMap);
	return summeryReturnData;
}

@Override
public Map<String, Object> getPartnerSupport(String yearCode, int mthcd, String dayWeek, String tabName, List<String> userids) {
	Map<String, Object> supportObj = new HashMap<>();
	List<String> partnerNames = partnersDao.getPartnerNames(yearCode, mthcd, dayWeek, tabName, userids);
	supportObj.put("partnerNames", partnerNames);
	return supportObj;
}

@Override
public Page<User> getPartners(String tabName, PartnerFilter filter) {
	Query query = new Query();
	List<Criteria> criterias = new ArrayList<Criteria>();
	Criteria criteria = Criteria.where("type").is("partner");
	if("salesTab".equalsIgnoreCase(tabName)) {
		if(filter.getPartnerType() != null){
			List<String> partnerType = Arrays.asList(filter.getPartnerType());
			if(partnerType.contains("Silver Partner")) {
				Criteria criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("partnerType").in(partnerType),Criteria.where("partnerType").is(""), Criteria.where("partnerType").is(null)));	
				criterias.add(criteriaa);
			}else {
				criteria.and("partnerType").in(Arrays.asList(filter.getPartnerType()));
			}
		}else {
			criteria.and("partnerType").is("Sales Team");
		}
	}else if("partnersTab".equalsIgnoreCase(tabName)) {
		if(filter.getPartnerType() != null){
			List<String> partnerType = Arrays.asList(filter.getPartnerType());
			if(partnerType.contains("Silver Partner")) {
				Criteria criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("partnerType").in(partnerType),Criteria.where("partnerType").is(""), Criteria.where("partnerType").is(null)));	
				criterias.add(criteriaa);
			}else {
				criteria.and("partnerType").in(Arrays.asList(filter.getPartnerType()));
			}
		}else {
			criteria.and("partnerType").ne("Sales Team");
		}
	}else {
		if(filter.getPartnerType() != null){
			List<String> partnerType = Arrays.asList(filter.getPartnerType());
			if(partnerType.contains("Silver Partner")) {
				Criteria criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("partnerType").in(partnerType),Criteria.where("partnerType").is(""), Criteria.where("partnerType").is(null)));	
				criterias.add(criteriaa);
			}else {
				criteria.and("partnerType").in(Arrays.asList(filter.getPartnerType()));
			}
		}
	}
	if(NullUtil.isNotEmpty(criterias)) {
		criteria.andOperator(criterias.toArray(new Criteria[criterias.size()]));				
	}
	query.addCriteria(criteria);
	Pageable pageable = null;
	long total = mongoTemplate.count(query, User.class, "users");
	if (total == 0) {
		return new PageImpl<User>(Collections.<User> emptyList());
	}
	return new PageImpl<User>(mongoTemplate.find(query, User.class, "users"), pageable, total);
}

@Override
public Page<PartnerClient> getSalesTeamPartners(String tabName, List<String> userids, int month, int year, int start,int length, String sortParam, String sortOrder, String searchVal, PartnerFilter filter) {
	Page<PartnerClient> pclient = null;
	String yearCode = Utility.getYearCode(month, year);
	pclient = partnersDao.getSalesTeamPartners(tabName, userids,month,yearCode, start, length, sortParam, sortOrder, searchVal,filter);
	return pclient;
}

@Override
public Map<String, Map<String, String>> getConsolidatedSummeryForDaysInMonth(String yearCode,int mthcd,String dayWeek,String tabName,List<String> userids, PartnerFilter filter) {
	List<TotalLeadDetails> leadDetails = partnersDao.getConsolidatedSummeryForDaysInMonth(yearCode, mthcd,dayWeek,tabName,userids,filter);
	List<TotalLeadDetails> pleadDetails = partnersDao.getConsolidatedSummeryForDaysInMonthPending(yearCode, mthcd,dayWeek,tabName,userids,filter);
	List<TotalLeadDetails> jleadDetails = partnersDao.getConsolidatedSummeryForDaysInMonthJoined(yearCode, mthcd,dayWeek,tabName,userids,filter);
	List<TotalLeadDetails> nleadDetails = partnersDao.getConsolidatedSummeryForDaysInMonthNew(yearCode, mthcd,dayWeek,tabName,userids,filter);
	List<TotalLeadDetails> dleadDetails = partnersDao.getConsolidatedSummeryForDaysInMonthDemo(yearCode, mthcd,dayWeek,tabName,userids,filter);
	Map<String, TotalLeadDetails> summerySlsData = new HashMap<String, TotalLeadDetails>();
	for(TotalLeadDetails gstr1InvoiceAmount : leadDetails){
		String code = gstr1InvoiceAmount.get_id();
		summerySlsData.put(code, gstr1InvoiceAmount);
	}
	Map<String, TotalLeadDetails> psummerySlsData = new HashMap<String, TotalLeadDetails>();
	for(TotalLeadDetails gstr1InvoiceAmount : pleadDetails){
		String code = gstr1InvoiceAmount.get_id();
		psummerySlsData.put(code, gstr1InvoiceAmount);
	}
	Map<String, TotalLeadDetails> jsummerySlsData = new HashMap<String, TotalLeadDetails>();
	for(TotalLeadDetails gstr1InvoiceAmount : jleadDetails){
		String code = gstr1InvoiceAmount.get_id();
		jsummerySlsData.put(code, gstr1InvoiceAmount);
	}
	Map<String, TotalLeadDetails> nsummerySlsData = new HashMap<String, TotalLeadDetails>();
	for(TotalLeadDetails gstr1InvoiceAmount : nleadDetails){
		String code = gstr1InvoiceAmount.get_id();
		nsummerySlsData.put(code, gstr1InvoiceAmount);
	}
	
	Map<String, TotalLeadDetails> dsummerySlsData = new HashMap<String, TotalLeadDetails>();
	for(TotalLeadDetails gstr1InvoiceAmount : dleadDetails){
		String code = gstr1InvoiceAmount.get_id();
		dsummerySlsData.put(code, gstr1InvoiceAmount);
	}
	
	Map<String, Map<String, String>> summeryReturnData = new HashMap<String, Map<String, String>>();
	int ct = 31;
	
	if(mthcd > 0) {
		if("weekTable".equalsIgnoreCase(dayWeek)) {
			ct = 5;
		}
	}else {
		ct = 12;
	}
	
	int ttotalTransactions = 0, ttotalPending =0, ttotalJoined =0, ttotalNew =0, ttotalDemo =0;
	Double testimatedAmt = 0d, tsubscriptionAmt = 0d;
	for(int i=1; i<=ct; i++){
		String cd = Integer.toString(i);
		int totalTransactions = 0, totalPending =0, totalJoined =0, totalNew =0, totalDemo =0;
		Double estimatedAmt = 0d, subscriptionAmt = 0d;
		TotalLeadDetails invoiceAmountSls = summerySlsData.get(cd);
		TotalLeadDetails pinvoiceAmountSls = psummerySlsData.get(cd);
		TotalLeadDetails jinvoiceAmountSls = jsummerySlsData.get(cd);
		TotalLeadDetails ninvoiceAmountSls = nsummerySlsData.get(cd);
		TotalLeadDetails dinvoiceAmountSls = dsummerySlsData.get(cd);
		Map<String, String> reportMap = new HashMap<String, String>();
		summeryReturnData.put(cd, reportMap);
		if(invoiceAmountSls != null){
			totalTransactions = invoiceAmountSls.getTotalLeads();
			ttotalTransactions +=totalTransactions; 
			estimatedAmt = invoiceAmountSls.getEstimatedCost().doubleValue();
			subscriptionAmt = invoiceAmountSls.getSubscriptionAmount().doubleValue();
			testimatedAmt += estimatedAmt; 
			tsubscriptionAmt += subscriptionAmt;
			if(pinvoiceAmountSls != null) {
				totalPending = pinvoiceAmountSls.getTotalPending();
				ttotalPending += totalPending;
			}
			if(jinvoiceAmountSls != null) {
				totalJoined = jinvoiceAmountSls.getTotalJoined();
				ttotalJoined += totalJoined;
			}
			if(ninvoiceAmountSls != null) {
				totalNew = ninvoiceAmountSls.getTotalNew();
				ttotalNew += totalNew;
			}
			if(dinvoiceAmountSls != null) {
				totalDemo = dinvoiceAmountSls.getTotalDemo();
				ttotalDemo += totalDemo;
			}
		}
		reportMap.put("estimatedAmt", decimalFormat.format(estimatedAmt));
		reportMap.put("subscriptionAmt", decimalFormat.format(subscriptionAmt));
		reportMap.put("totalLeads", String.valueOf(totalTransactions));
		reportMap.put("totalPending", String.valueOf(totalPending));
		reportMap.put("totalJoined", String.valueOf(totalJoined));
		reportMap.put("totalNew", String.valueOf(totalNew));
		reportMap.put("totalDemo", String.valueOf(totalDemo));
		
	}
	
	Map<String, String> totreportMap = new HashMap<String, String>();
	
	totreportMap.put("estimatedAmt", decimalFormat.format(testimatedAmt));
	totreportMap.put("subscriptionAmt", decimalFormat.format(tsubscriptionAmt));
	totreportMap.put("totalLeads", String.valueOf(ttotalTransactions));
	totreportMap.put("totalPending", String.valueOf(ttotalPending));
	totreportMap.put("totalJoined", String.valueOf(ttotalJoined));
	totreportMap.put("totalNew", String.valueOf(ttotalNew));
	totreportMap.put("totalDemo", String.valueOf(ttotalDemo));
	summeryReturnData.put("totals", totreportMap);
	return summeryReturnData;
}

	@Override
	public PartnerPayments findByPartnerPaymentsUserid(String userid, int month, int year) {
		
		//Criteria criteria = Criteria.where("").is("").and("").is("");
		//if() {}
		
		
		return partnerPaymentsRepository.findByUseridAndMthCdAndYrCd(userid, month+"", Utility.getYearCode(month, year));
	}
	
	@Override
	public PartnerPayments findByPartnerPaymentsDocIdAndMonthYearCode(String docid, int month, String yearCode) {
		
		return partnerPaymentsRepository.findByIdAndMthCdAndYrCd(docid, month+"", yearCode);
	}
	
	@Override
	public PartnerPayments savePartnerPayments(PartnerPayments payments, int month, int year) {
		
		PartnerPayments payment = partnerPaymentsRepository.findByUseridAndMthCdAndYrCd(payments.getUserid(), month+"", Utility.getYearCode(month, year));
		if(NullUtil.isNotEmpty(payment)) {
			payment.setSubscriptionamount(payments.getSubscriptionamount());
			payment.setPercentage(payments.getPercentage());
			payment.setPaidamount(payments.getSubscriptionamount());
			payment.setPaidamount(payments.getPartneramt());
			
			payment.setInvoicedate(payments.getInvoicedate());
			payment.setInvoiceno(payments.getInvoiceno());
			payment.setTdsamt(payments.getTdsamt());
			
			payment.setPaidamount(payments.getPaidamount());
		}
		String status = null;
		Double partneramt = ((payments.getSubscriptionamount() * payments.getPercentage()) / 100) - payments.getPaidamount();
		if(partneramt == 0.0 || partneramt == 0 ) {
			status = MasterGSTConstants.DONE;
		}else {
			if(partneramt > 0) {
				status = MasterGSTConstants.PARTIALLY;
			}else {
				status = MasterGSTConstants.PENDING;
			}				
		}
		payment.setPartnerPayment(status);
		getClientPaymentsUpdate(payments.getUserid(), month, Utility.getYearCode(month, year), status);
		return partnerPaymentsRepository.save(payment);
	}
	
	@Override
	public PartnerPayments savePpartnerPayments(PartnerPayments payments, int month, String yearCode) {
		
		PartnerPayments payment = partnerPaymentsRepository.findByUseridAndMthCdAndYrCd(payments.getUserid(), month+"", yearCode);
		if(NullUtil.isNotEmpty(payment)) {
			payment.setSubscriptionamount(payments.getSubscriptionamount());
			payment.setPercentage(payments.getPercentage());
			payment.setPaidamount(payments.getSubscriptionamount());
			payment.setPaidamount(payments.getPartneramt());
			
			payment.setInvoicedate(payments.getInvoicedate());
			payment.setInvoiceno(payments.getInvoiceno());
			payment.setTdsamt(payments.getTdsamt());
			
			payment.setPaidamount(payments.getPaidamount());
		}
		String status = null;
		Double partneramt = ((payments.getSubscriptionamount() * payments.getPercentage()) / 100) - payments.getPaidamount();
		if(partneramt == 0.0 || partneramt == 0 ) {
			status = MasterGSTConstants.DONE;
		}else {
			if(partneramt > 0) {
				status = MasterGSTConstants.PARTIALLY;
			}else {
				status = MasterGSTConstants.PENDING;
			}				
		}
		payment.setPartnerPayment(status);
		getClientPaymentsUpdate(payments.getUserid(), month, yearCode, status);
		return partnerPaymentsRepository.save(payment);
	}
	
	@Async
	public void getClientPaymentsUpdate(String userid, int month, String yearCode, String status) {
		
		List<PartnerClient> clients = partnerClientRepository.findByUseridAndClientidIsNotNull(userid);
		if(NullUtil.isNotEmpty(clients)) {
			for(PartnerClient client : clients) {
				if(isNotEmpty(client) && isNotEmpty(client.getClientid())){
					
					List<PaymentDetails> payments = subscriptionService.getPartnerPaymentDetails(client.getClientid(), month, yearCode);
					if(NullUtil.isNotEmpty(payments)){
						
						for(PaymentDetails payment : payments) {
							payment.setPartnerPayment(status);
						}
						paymentDetailsRepository.save(payments);
					}
				}
			}
		}
	}
		
		@Override
		public Page<User> findByPartnerUsers(int month, String yearCode, int start, int length, String searchVal){
			
			Criteria subscribeCriteria = Criteria.where("yrCd").is(yearCode);
			//.and("paidAmount").gt(100);
			if(month>0) {
				subscribeCriteria.and("mthCd").is(month+"");
			}
			
			Query subscribeQuery = Query.query(subscribeCriteria);
			/*
			 	List<SubscriptionDetails> subscriptionList = mongoTemplate.find(subscribeQuery, SubscriptionDetails.class, "subscription_data");
				for ( SubscriptionDetails subscription :subscriptionList) {
				if(subscriptionMap.containsKey(subscription.getUserid())) {
					subscriptionMap.put(subscription.getUserid(), subscriptionMap.get(subscription.getUserid()) + subscription.getPaidAmount());
				}else {
					subscriptionMap.put(subscription.getUserid(), subscription.getPaidAmount());
				}
			}
			*/
			List<PaymentDetails> payments = mongoTemplate.find(subscribeQuery, PaymentDetails.class, "payment_data");
			
			Map<String, Double> subscriptionMap = new HashMap<>();
			for ( PaymentDetails subscription :payments) {
				if(subscriptionMap.containsKey(subscription.getUserid())) {
					subscriptionMap.put(subscription.getUserid(), subscriptionMap.get(subscription.getUserid()) + subscription.getAmount());
				}else {
					subscriptionMap.put(subscription.getUserid(), subscription.getAmount());
				}
			}
			
			List<PartnerClient> clients = partnerClientRepository.findByClientidIn(subscriptionMap.keySet());
			//.and("category").is("Parent");
			List<String> userids = new ArrayList<>();
			for(PartnerClient clnt : clients) {
				userids.add(clnt.getUserid());
			}
			
			if(NullUtil.isEmpty(userids)) {
				return new PageImpl<User>(Collections.<User> emptyList());
			}
			Criteria criteria = Criteria.where("type").is("partner").and("_id").in(userids);
			
			Query query = Query.query(criteria);
			addAllInvoicesQueryFirlds(query);
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, User.class, "users");
			if (total == 0) {
				return new PageImpl<User>(Collections.<User> emptyList());
			}
			return new PageImpl<User>(mongoTemplate.find(query, User.class, "users"), pageable, total);
		}
		
		//@Override
		public Page<User> findByPartnerUsers(Date stDate, Date endDate, int start, int length, String searchVal) {
			
			Criteria subscribeCriteria = Criteria.where("paidAmount").gt(100).andOperator(
					Criteria.where("createdDate").gte(stDate),
					Criteria.where("createdDate").lte(endDate)
			);
			
			Query subscribeQuery = Query.query(subscribeCriteria);
			
			List<SubscriptionDetails> subscriptionList = mongoTemplate.find(subscribeQuery, SubscriptionDetails.class, "subscription_data");
			Map<String, Double> subscriptionMap = new HashMap<>();
			for ( SubscriptionDetails subscription :subscriptionList) {
				if(subscriptionMap.containsKey(subscription.getUserid())) {
					subscriptionMap.put(subscription.getUserid(), subscriptionMap.get(subscription.getUserid()) + subscription.getPaidAmount());
				}else {
					subscriptionMap.put(subscription.getUserid(), subscription.getPaidAmount());
				}
			}
			
			/*Criteria partnerClientCriteria = Criteria.where("clientid").in(subscriptionMap.keySet());
			Query partnerClientQuery = Query.query(partnerClientCriteria);
			List<PartnerClient> clients = mongoTemplate.find(partnerClientQuery, PartnerClient.class, "partner_client");
			*/
			List<PartnerClient> clients = partnerClientRepository.findByClientidIn(subscriptionMap.keySet());
			//.and("category").is("Parent");
			List<String> userids = new ArrayList<>();
			for(PartnerClient clnt : clients) {
				userids.add(clnt.getUserid());
			}
			
			if(NullUtil.isEmpty(userids)) {
				return new PageImpl<User>(Collections.<User> emptyList());
			}
			
			Criteria criteria = Criteria.where("type").is("partner").and("_id").in(userids);
			/*Criteria.where("type").is("partner").andOperator(
					Criteria.where("createdDate").gte(stDate),
					Criteria.where("createdDate").lte(endDate)
			);*/
			Query query = Query.query(criteria);
			addAllInvoicesQueryFirlds(query);
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, User.class, "users");
			if (total == 0) {
				return new PageImpl<User>(Collections.<User> emptyList());
			}
			return new PageImpl<User>(mongoTemplate.find(query, User.class, "users"), pageable, total);
		}
		
		private void addAllInvoicesQueryFirlds(Query query){
			
			query.fields().include("userid");
			query.fields().include("fullname");
			query.fields().include("mobilenumber");
			query.fields().include("subscriptionamount");
			query.fields().include("partnerPercentage");
			query.fields().include("paidAmount");
			query.fields().include("invoiceFromPartner");
			query.fields().include("paymentStatus");
		}
		
		@Override
		public Page<PartnerClientInfo> getPartnerReferenceClients(String userid, int start, int  length, String sortParam, String sortOrder,String  searchVal) {
			Query query = new Query();
			Criteria criteria = Criteria.where("userid").is(userid);
			
			if(NullUtil.isNotEmpty(searchVal)){
				criteria = new Criteria().andOperator(
						criteria, new Criteria().orOperator(
								Criteria.where("name").regex(searchVal,"i"), 
								Criteria.where("email").regex(searchVal,"i"), 
								Criteria.where("mobilenumber").regex(searchVal,"i"),
								Criteria.where("status").regex(searchVal,"i"),
								Criteria.where("clienttype").regex(searchVal,"i")
						)
				);
			}
			
			Sort sort = null;
			if(NullUtil.isNotEmpty(sortParam) && NullUtil.isNotEmpty(sortOrder)){	
				sort = new Sort(new Order("asc".equalsIgnoreCase(sortOrder) ? Direction.ASC : Direction.DESC, sortParam));
			}
			Pageable pageable = new PageRequest(start, length, sort);
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("subscription_data", "clientid", "userid", "subscription"),
    				Aggregation.match(criteria),
       				Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
       				Aggregation.limit(pageable.getPageSize())
       			);
			Aggregation aggCount = Aggregation.newAggregation(
					Aggregation.lookup("subscription_data", "clientid", "userid", "subscription"),
    				Aggregation.match(criteria)
				);
    		query.addCriteria(criteria);
			query.with(pageable);
			long total = mongoTemplate.aggregate(aggCount, "partner_client", PartnerClientInfo.class).getMappedResults().size();
			
			if (total == 0) {
				return new PageImpl<PartnerClientInfo>(Collections.<PartnerClientInfo> emptyList());
			}
			List<PartnerClientInfo> aggregationResult = mongoTemplate.aggregate(aggregation, "partner_client", PartnerClientInfo.class).getMappedResults();
			return new PageImpl<PartnerClientInfo>(aggregationResult, pageable, total);
		}
		
		@Override
		public Page<ClientPaymentDetails> getPartnerClientPayments(String userid, String payment, int month, int year, int start, int length, String sortParam, String sortOrder, String searchVal) {
			
			Criteria criteria = Criteria.where("userid").is(userid);
			
			Query query = Query.query(criteria);
			List<String> clientids = mongoTemplate.getCollection("partner_client").distinct("clientid", query.getQueryObject());
			
			if(clientids.size() == 0) {
				return new PageImpl<ClientPaymentDetails>(Collections.<ClientPaymentDetails> emptyList());
			}
			
			Criteria aggrCriteria = Criteria.where("userid").in(clientids)
					.and("partnerPayment").in(payment, MasterGSTConstants.PARTIALLY);
					
			if(month > 0) {
				aggrCriteria.and("mthCd").is(month+"").and("yrCd").is(Utility.getYearCode(month, year));
			}else {
				aggrCriteria.and("yrCd").is(Utility.getYearCode(4, year));
			}
			
			if(StringUtils.hasLength(searchVal)){
				aggrCriteria = new Criteria().andOperator(aggrCriteria, adminUtils.getPartnerClientSearchValueCriteria(searchVal));
			}
			
			Pageable pageable = new PageRequest((start/length), length);
			
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("partner_client", "userid", "clientid", "partnerClient"),
    				Aggregation.match(aggrCriteria),
       				Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
       				Aggregation.limit(pageable.getPageSize())
       			);
			Aggregation aggCount = Aggregation.newAggregation(
					Aggregation.lookup("partner_client", "userid", "clientid", "partnerClient"),
    				Aggregation.match(aggrCriteria)
				);
    		long total = mongoTemplate.aggregate(aggCount, "payment_data", ClientPaymentDetails.class).getMappedResults().size();
			
			if (total == 0) {
				return new PageImpl<ClientPaymentDetails>(Collections.<ClientPaymentDetails> emptyList());
			}
			List<ClientPaymentDetails> aggregationResult = mongoTemplate.aggregate(aggregation, "payment_data", ClientPaymentDetails.class).getMappedResults();
			return new PageImpl<ClientPaymentDetails>(aggregationResult, pageable, total);
		}
		
		@Override
		public Page<PartnerPayments> getPartnerPayments(String userid, String payment, int month, int year, int start, int length,
				String sortParam, String sortOrder, String searchVal) {
			Criteria criteria = Criteria.where("userid").is(userid)
					.and("partnerPayment").in(payment, MasterGSTConstants.PARTIALLY);
			if(month > 0) {
				criteria.and("mthCd").is(month+"").and("yrCd").is(Utility.getYearCode(month, year));
			}else {
				criteria.and("yrCd").is(Utility.getYearCode(4, year));				
			}
			
			if(StringUtils.hasLength(searchVal)){
				criteria = new Criteria().andOperator(criteria, adminUtils.getPartnerClientSearchValueCriteria(searchVal));
			}
			Query query = Query.query(criteria);
			
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			
    		long total = mongoTemplate.count(query, PartnerPayments.class, "partner_payments");
			if (total == 0) {
				return new PageImpl<PartnerPayments>(Collections.<PartnerPayments> emptyList());
			}
			return new PageImpl<PartnerPayments>(mongoTemplate.find(query, PartnerPayments.class, "partner_payments"), pageable, total);
		}
		
		private Criteria getSearchValueCriteria(final String searchVal){
			List<Criteria> criterias = new ArrayList<Criteria>();
			criterias.add(Criteria.where("clienttype").regex(searchVal, "i"));
			criterias.add(Criteria.where("name").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("email").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("mobilenumber").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("estimatedCost").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("status").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("createdDate").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("updatedDate").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("joinDate").regex(searchVal, "i"));
	 		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
		}

}
