package com.mastergst.usermanagement.runtime.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.EmailService;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.VmUtil;
import com.mastergst.login.runtime.domain.CompaignEmail;
import com.mastergst.login.runtime.domain.ReconcileUsers;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.CompaignEmailRepository;
import com.mastergst.usermanagement.runtime.dao.AdminSupportDao;

@Service
public class AdminSupportServiceImpl implements AdminSupportService {

	@Autowired private AdminSupportDao adminSupportDao;
	@Autowired
	private EmailService emailService;
	@Autowired
	private CompaignEmailRepository compaignEmailRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	@Override
	public Page<ReconcileUsers> getReconcileInfo(int start, int length, String searchVal) {
		
		return adminSupportDao.getReconcileInfo(start, length, searchVal);
	}

	
	
	@Override
	public void compaignEmails(CompaignEmail compaignEmail, String type) {
		
		sendCompaignEmails(compaignEmail, type);
	}
	
	@Async
	public void sendCompaignEmails(CompaignEmail compaignEmail, String type) {
		Query query = new Query();
		query.fields().include("userid");
		query.fields().include("fullname");
		query.fields().include("email");
		
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
				query.addCriteria(criteria.where("category").ne("Child"));
				query.addCriteria(criteria.where("otpVerified").ne("false"));
			}else{
				flag = true;
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
				query.addCriteria(criteria.where("needToFollowUp").ne("Closed"));	
			}else {
				query.addCriteria(criteria.where("needToFollowUp").ne("Test Account"));				
			}
		}
		
		 List<User> users = mongoTemplate.find(query, User.class, "users");
		
		 List<String> failureIds = Lists.newArrayList();
		 compaignEmail.setUserType(type);
		 users.forEach( user ->{
			try {
				Map<String, Object> data = Maps.newHashMap();
				data.put("fullname", user.getFullname());
				data.put("info", compaignEmail.getMailBody());
				emailService.sendCompaignEmail(user.getEmail(), VmUtil.velocityTemplate("email_compain.vm", data), compaignEmail.getSubject());
				compaignEmail.setSuccess(compaignEmail.getSuccess() + 1);
			}catch (Exception e) {
				compaignEmail.setFailure(compaignEmail.getFailure() + 1);
				failureIds.add(user.getEmail());
			} 
		 });
		 compaignEmail.setFailureMails(failureIds);
		 compaignEmailRepository.save(compaignEmail);
	}
}
