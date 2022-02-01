/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.login.runtime.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.HeaderKeys;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.repository.HeaderKeysRepository;
/**
 * Service Impl class for User to perform CRUD operation.
 * 
 * @author Ashok Samart
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

	private static final Logger logger = LogManager.getLogger(UserServiceImpl.class.getName());
	private static final String CLASSNAME = "UserServiceImpl::";
	@Autowired
	UserRepository userRepository;

	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	private HeaderKeysRepository headerKeysRepository;
	
	
	
	@Override
	@Transactional
	@CacheEvict(value="userCache", key="#user.email")
	public User createUser(User user) {
		//user.setRefid(randomNumber(4));
		return userRepository.save(user);
	}

	@Override
	@Transactional(readOnly=true)
	public boolean collectionExists(final String collectionName) {
		return mongoTemplate.collectionExists(collectionName);
	}

	@Override
	@Transactional(readOnly=true)
	@Cacheable(value="userCache", key="#id")
	public User getUser(String id) {
		User user = userRepository.findOne(id);
		/*
		 * if(user != null && NullUtil.isEmpty(user.getRefid())) {
		 * user.setRefid(randomNumber(4)); }
		 */
		return user;
	}

	@Override
	@Transactional
	@CacheEvict(value="userCache", key="#user.email")
	public User updateUser(User user) {
		/*
		 * if(NullUtil.isEmpty(user.getRefid())) { user.setRefid(randomNumber(4)); }
		 */
		return userRepository.save(user);
	}
	
	@Override
	@Transactional
	@CacheEvict(allEntries=true)
	public void saveUsers(List<User> users) {
		userRepository.save(users);
	}

	@Override
	@Transactional
	@CacheEvict(value="userCache", key="#email")
	public void deleteUser(String email) {
		userRepository.deleteByEmail(email);
	}

	@Override
	@Transactional
	public List<User> getAllUsers() {
		return (List<User>) userRepository.findAll();
	}
	
	@Override
	@Transactional
	public Page<User> getAllUsers(int start, int length,String fieldName, String order, String searchVal) {
		Sort sort = null;
		if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}
		return (Page<User>) userRepository.findAll(new PageRequest(start, length, sort));
	}
	
	@Override
	@Transactional
	public Page<User> getAllUsers(String type, int start, int length,String fieldName, String order, String searchVal) {
		/*if("All".equalsIgnoreCase(type)){
			return getAllUsers(start, length, fieldName, order, searchVal);
		}*/
		Query query = new Query();
		Criteria criteria = new Criteria();
		if("testaccounts".equalsIgnoreCase(type)) {
			query.addCriteria(criteria.where("needToFollowUp").is("Test Account"));
		}else if("spamusers".equalsIgnoreCase(type)) {
			query.addCriteria(criteria.where("parentid").exists(false));
			query.addCriteria(criteria.where("category").ne("Child"));
			query.addCriteria(criteria.where("needToFollowUp").ne("Test Account"));
			query.addCriteria(criteria.where("otpVerified").ne("true"));
		}else {
			boolean flag = false;
			if(!"subusers".equalsIgnoreCase(type)){
				flag = true;
				//query.addCriteria(criteria.where("parentid").exists(false));
				query.addCriteria(criteria.where("category").ne("Child"));
				//query.addCriteria(criteria.where("needToFollowUp").ne("Test Account"));
				query.addCriteria(criteria.where("otpVerified").ne("false"));
			}else{
				flag = true;
				//query.addCriteria(criteria.where("parentid").exists(true));
				//query.addCriteria(criteria.where("isglobal").is("true"));
				//query.addCriteria(criteria.where("needToFollowUp").ne("Test Account"));
				query.addCriteria(criteria.where("category").ne("Parent"));
				query.addCriteria(criteria.where("type").ne("subadmin"));
			}
			if(!"All".equalsIgnoreCase(type) && !"spamusers".equalsIgnoreCase(type)){
				if(!"subusers".equalsIgnoreCase(type)){
					if("active-aspdeveloper".equalsIgnoreCase(type)) {
						type = MasterGSTConstants.ASPDEVELOPER;
						flag = false;
						query.addCriteria(criteria.where("userkeys").exists(true));
					}else if("active-cacmas".equalsIgnoreCase(type)) {
						flag = false;
						type = MasterGSTConstants.CAS;
					}else if("active-taxp".equalsIgnoreCase(type)) {
						flag = false;
						type = MasterGSTConstants.TAXPRACTITIONERS;
					}else if("active-business".equalsIgnoreCase(type)) {
						flag = false;
						type = MasterGSTConstants.BUSINESS;
					}else if("active-enterprise".equalsIgnoreCase(type)) {
						flag = false;
						type = MasterGSTConstants.ENTERPRISE;
					}else if("active-suvidha".equalsIgnoreCase(type)) {
						flag = false;
						type = MasterGSTConstants.SUVIDHA_CENTERS;						
					}
					query.addCriteria(criteria.where("type").is(type));
				}
			}
			if(!flag) {
				List<String> slaesStatus = Arrays.asList("Call Not Lift", "Duplicate", "Not Required",
						"Ready to Go", "Ready to Pay", "Yet to Take Decision", "Test Account", "Pricing Issue", "Sandbox Testing");
				//query.addCriteria(criteria.where("needToFollowUp").nin(slaesStatus));
				query.addCriteria(criteria.where("needToFollowUp").is("Closed"));
			}else {
				query.addCriteria(criteria.where("needToFollowUp").ne("Test Account"));				
			}
		}
		if(NullUtil.isNotEmpty(searchVal)){
			criteria = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("fullname").regex(searchVal,"i"), Criteria.where("email").regex(searchVal,"i"), Criteria.where("mobilenumber").regex(searchVal)));
		}
		query.addCriteria(criteria);
		Sort sort = null;
		if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(
					new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, "createdDate"),
					new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName)
					);
		}else {
			sort = new Sort(new Order(Direction.DESC, "createdDate"));
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
	@Transactional(readOnly=true)
	public User isExistUser(String email, String password) {
		logger.debug(CLASSNAME + "isExistUser : Begin");
		return userRepository.findByEmailAndPassword(email, password);
	}

	@Override
	@Transactional(readOnly=true)
	//@Cacheable(value="userCache", key="#email")
	public User findByEmail(String email) {
		logger.debug(CLASSNAME + "findByEmail : Begin");
		return userRepository.findByEmail(email);
	}
	
	@Override
	@Transactional(readOnly=true)
	public User findByEmailIgnoreCase(String email) {
		logger.debug(CLASSNAME + "findByEmailIgnoreCase : Begin");
		return userRepository.findByEmailIgnoreCase(email);
	}

	@Override
	@Transactional
	public User findByMobilenumber(String mobilenumber) {
		logger.debug(CLASSNAME + "findByMobilenumber : Begin");
		return userRepository.findByMobilenumber(mobilenumber);
	}
	
	@Override
	@Transactional
	public User findByUsrId(String id) {
		logger.debug(CLASSNAME + "findById : Begin");
		logger.debug(CLASSNAME + "findById : id\t" + id);
		ObjectId objId = new ObjectId(id);
		User user = mongoTemplate.findById(objId, User.class);
		return user;
	}

	@Override
	@Transactional(readOnly=true)
	@Cacheable(value="userCache", key="#id")
	public User findById(String id) {
		logger.debug(CLASSNAME + "findById : Begin");
		logger.debug(CLASSNAME + "findById : id\t" + id);
		ObjectId objId = new ObjectId(id);
		User user = mongoTemplate.findById(objId, User.class);
		return user;
	}
	
	@Override
	@Cacheable(value="userCache", key="#id")
	public User getUserById(String id) {
		User usr = userRepository.findById(id);
		return usr;
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<User> findByParentId(String parentId) {
		logger.debug(CLASSNAME + "findByParentId : Begin");
		return userRepository.findByParentid(parentId);
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
	public List<User> getAllUsersData(final String searchQuery) {
		List<String> usertypes = Lists.newArrayList();
		usertypes.add(MasterGSTConstants.ASPDEVELOPER);
		usertypes.add(MasterGSTConstants.PARTNER);
		Query query = new Query();
		Criteria criteria = new Criteria();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(criteria.where("type").nin(usertypes));
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("email").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE))));
		return mongoTemplate.find(query, User.class, "users");
	}
	
	@Override
	public List<User> getAllUsersData(final String type,final String searchQuery) {
		List<String> usertypes = Lists.newArrayList();
		if(type.equals(MasterGSTConstants.SUVIDHA_CENTERS)){
			usertypes.add(MasterGSTConstants.SUVIDHA_CENTERS);
			usertypes.add(MasterGSTConstants.BUSINESS);
		} else if(type.equals(MasterGSTConstants.CAS)){
			usertypes.add(MasterGSTConstants.CAS);
			usertypes.add(MasterGSTConstants.BUSINESS);
			usertypes.add(MasterGSTConstants.ENTERPRISE);
		} else if(type.equals(MasterGSTConstants.ENTERPRISE)){
			usertypes.add(MasterGSTConstants.ENTERPRISE);
			usertypes.add(MasterGSTConstants.CAS);
		} else if(type.equals(MasterGSTConstants.BUSINESS)){
			usertypes.add(MasterGSTConstants.BUSINESS);
			usertypes.add(MasterGSTConstants.CAS);
			usertypes.add(MasterGSTConstants.SUVIDHA_CENTERS);
		}
		Query query = new Query();
		Criteria criteria = new Criteria();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(criteria.where("type").in(usertypes));
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("email").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE))));
		return mongoTemplate.find(query, User.class, "users");
	}
	
	@Override
	public List<User> getASPUserDetailsByType(String type) {
		return userRepository.findByType(type);
	}

	@Override
	public List<User> getByAllUsersYearly(int year) {
		
		Date startdate = null;
		Date enddate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, 0, 1, 0, 0, 0);
		startdate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, 12, 0, 0, 0, 0);
		enddate = new java.util.Date(cal.getTimeInMillis());
		
		//return userRepository.findByCategoryAndCreatedDateBetween("Parent", startdate,enddate);
		
		return userRepository.findByCreatedDateBetween(startdate,enddate);
	}
	
	@Override
	public List<User> getByAllUsersgetByAllUsersMonthly(int month,int year) {
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 1, 0, 0, 0);
		Date startdate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 0, 0, 0);
		Date enddate = new java.util.Date(cal.getTimeInMillis());
		
		//return userRepository.findByCategoryAndCreatedDateBetween("Parent", startdate,enddate);
		
		return userRepository.findByCreatedDateBetween(startdate,enddate);
	}
	
	@Override
	public void deleteOtpunverifiedusers(String otpunverifiedusersid) {

	userRepository.delete(otpunverifiedusersid);
	}
	
	@Override
	public List<User> partnerClientLinkedUsers(String searchQuery) {
		
		Query query = new Query();
		Criteria criteria = new Criteria();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "email"));
		query.addCriteria(criteria.where("type").is(MasterGSTConstants.PARTNER));
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("email").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE))));
		return mongoTemplate.find(query, User.class, "users");
	}
	
	@Override
	public List<HeaderKeys> getHeaderkeys(String userid) {
		
		return headerKeysRepository.findByUserid(userid);
	}
	
	@Override
	public List<HeaderKeys> deleteHeaderkeys(String headerid, String userid) {
		
		headerKeysRepository.delete(headerid);

		return headerKeysRepository.findByUserid(userid);
	}
	@Override
	public void deleteAspDeveloperHeaderkeys(String headerid) {
		
		headerKeysRepository.delete(headerid);
	}
	
		@Override
	public List<User> getAllusersInYearly(int year){
		
		Date startDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, 0, 1, 0, 0, 0);
		startDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, 12, 0, 0, 0, 0);
		endDate = new java.util.Date(cal.getTimeInMillis());
		
		Query query = new Query();
		Criteria criteria = new Criteria();
		
		query.addCriteria(criteria.where("parentid").exists(false));
		query.addCriteria(criteria.where("type").ne("partner"));
		query.addCriteria(criteria.where("needToFollowUp").ne("Test Account"));
		query.addCriteria(
				criteria.where("otpVerified").ne("false")
					.andOperator(
							Criteria.where("createdDate").lt(endDate),
							Criteria.where("createdDate").gte(startDate)
					)
				);
		
		return new ArrayList<User>(mongoTemplate.find(query, User.class, "users"));
	}
	
	@Override
	public List<User> getAllusersInMonthly(int month, int year){
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 1, 0, 0, 0);
		Date startDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 0, 0, 0);
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		
		Query query = new Query();
		Criteria criteria = new Criteria();
		
		query.addCriteria(criteria.where("parentid").exists(false));
		query.addCriteria(criteria.where("type").ne("partner"));
		query.addCriteria(criteria.where("needToFollowUp").ne("Test Account"));
		query.addCriteria(
				criteria.where("otpVerified").ne("false")
					.andOperator(
							Criteria.where("createdDate").lt(endDate),
							Criteria.where("createdDate").gte(startDate)
					)
		);
		
		return new ArrayList<User>(mongoTemplate.find(query, User.class, "users"));
	}
	
	@Override
	public List<User> getAllUsersBasedOnId(List<String> partnerclientids){
		
		return userRepository.findByIdIn(partnerclientids);
	}
	
	@Override
	public void saveRefisUsers(List<User> users) {
		userRepository.save(users);
	}
	/*
	@Override
	public Page<User> findByPartnerUsers(int month, String yearCode, int start, int length, String searchVal){
		
		Criteria criteria = Criteria.where("type").is("partner").and("yrCd").is(yearCode);
		//.and("category").is("Parent");
		if(month>0) {
			criteria.and("mthCd").is(month+"");
		}
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
	
	@Override
	public Page<User> findByPartnerUsers(Date stDate, Date endDate, int start, int length, String searchVal) {
		Criteria criteria = Criteria.where("type").is("partner").andOperator(
				Criteria.where("createdDate").gte(stDate),
				Criteria.where("createdDate").lte(endDate)
		);
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, User.class, "users");
		if (total == 0) {
			return new PageImpl<User>(Collections.<User> emptyList());
		}
		return new PageImpl<User>(mongoTemplate.find(query, User.class, "users"), pageable, total);
	}*/
	
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
	
}
