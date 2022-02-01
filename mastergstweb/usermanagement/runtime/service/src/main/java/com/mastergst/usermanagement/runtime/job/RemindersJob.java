package com.mastergst.usermanagement.runtime.job;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.mastergst.configuration.service.EmailService;
import com.mastergst.core.util.NullUtil;
import com.mastergst.core.util.VmUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.usermanagement.runtime.dao.SubscriptionDataDao;
import com.mastergst.usermanagement.runtime.dao.UsersDao;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.support.Utility;

@Component
public class RemindersJob implements InitializingBean, Job {

	private static final Logger logger = LoggerFactory
			.getLogger(RemindersJob.class);
	
	@Autowired
	private SubscriptionDataDao subscriptionDataDao;
	
	@Autowired
	private UsersDao usersDao;
	
	@Value("${reminder.subscription.before.days}")
	private String subscriptionReminderBeforeDays;
	
	private int[] reminderBeforeDays;
	
	private int reminderBeforeMax;
	
	@Autowired
	private EmailService emailService;
	
	private void init(){
		logger.debug("ReminderService : init - Start");
		try {
			initializeReminderDays();
		} catch (Exception e) {
			logger.error("ReminderService : init - ERROR ", e);
		}
		logger.debug("ReminderService : init - End");
	}
	
	private void initializeReminderDays(){
		if(subscriptionReminderBeforeDays == null || subscriptionReminderBeforeDays.isEmpty()){
			reminderBeforeDays = new int[]{30};
			reminderBeforeMax = 30;
		}else{
			String[] days = subscriptionReminderBeforeDays.split(",");
			reminderBeforeDays = new int[days.length];
			int max = 0;
			for(int i=0;i<days.length;i++){
				reminderBeforeDays[i] = Integer.parseInt(days[i]);
				if(max < reminderBeforeDays[i]){
					max = reminderBeforeDays[i];
				}
			}
			reminderBeforeMax = max;
		}
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("ReminderService : execute - Start");
		List<String> ccemail = Lists.newArrayList();
		List<String> userdetails = Lists.newArrayList();
		userdetails.add("Duplicate");
		userdetails.add("Not Required");
		ccemail.add("renewals@mastergst.com");
		List<SubscriptionDetails> subscriptionDetails =  subscriptionDataDao.getSubscriptionsEndsByDays(reminderBeforeMax);
		List<SubscriptionDetails> subscriptionDetailsexpiry = subscriptionDataDao.getSubscriptionsAboutToExpiry();
		Calendar currentDate = Calendar.getInstance();
		LocalDate curentLocalDate = LocalDate.of(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH)+1, currentDate.get(Calendar.DAY_OF_MONTH));
		if(subscriptionDetails != null && !subscriptionDetails.isEmpty()){
			 List<ObjectId> userIds = subscriptionDetails.stream().map(sd -> new ObjectId(sd.getUserid())).collect(Collectors.toList()); 
			 List<User> users = usersDao.getUserMainDetails(userIds,userdetails);
			 for(SubscriptionDetails details : subscriptionDetails){
				 if(NullUtil.isNotEmpty(details.getApiType()) && (details.getApiType().equalsIgnoreCase("E-INVOICEAPI") || details.getApiType().equalsIgnoreCase("GSTAPI") || details.getApiType().equalsIgnoreCase("EWAYAPI"))) {
					 Calendar expCalender = Calendar.getInstance();
					 expCalender.setTime(details.getExpiryDate());
					 LocalDate curentExpLocalDate = LocalDate.of(expCalender.get(Calendar.YEAR), expCalender.get(Calendar.MONTH)+1, expCalender.get(Calendar.DAY_OF_MONTH));
					 long differenceDays = ChronoUnit.DAYS.between(curentLocalDate, curentExpLocalDate);
					 boolean isFound = false;
					 if(differenceDays == 0){
						 isFound = true; 
					 }else{
						 for(int beDays : reminderBeforeDays){
							 if(differenceDays == beDays){
								 isFound = true;
								 break;
							 }
						 }
					 }
					 User user = null;
					 if(isFound){
						 for(Iterator<User> userIte = users.iterator();userIte.hasNext();){
							User us = userIte.next();
							if(us.getId().toString().equals(details.getUserid())){
								user = us;
								userIte.remove();
								break;
							}
						 }
					 }
					 if(user != null && !"Partner".equalsIgnoreCase(user.getType()) && !"Test Account".equalsIgnoreCase(user.getNeedToFollowUp()) && NullUtil.isEmpty(user.getParentid())){
						 try {
							 Map<String, Object> data = new HashMap<String, Object>();
							 data.put("fullname",user.getFullname());
							 data.put("differenceDays", differenceDays);
							 if(details.getExpiryDate() != null){
								 data.put("expiryDate",Utility.getFormatedDateStr(details.getExpiryDate()));
							 }
							 String mailContent = VmUtil.velocityTemplate("subscription_reminder.vm", data);
							 String toMailId = user.getReminderEmail() == null || user.getReminderEmail().isEmpty() ? user.getEmail() : user.getReminderEmail(); 
							 String subject = "";
							 if(differenceDays > 0){
								 subject = "REMINDER: MasterGst Subscription expires in "+differenceDays+" days";
							 }else if(differenceDays < 0){
								 subject = "REMINDER: MasterGst Subscription expired";
							 }else{
								 subject = "REMINDER: MasterGst Subscription expires today";
							 }
							 emailService.sendEnrollEmail(toMailId, mailContent, subject,ccemail);
						} catch (Exception e) {
							e.printStackTrace();
						}
					 }
			 	}else {
			 		if(NullUtil.isEmpty(details.getApiType())) {
			 			Calendar expCalender = Calendar.getInstance();
						 expCalender.setTime(details.getExpiryDate());
						 LocalDate curentExpLocalDate = LocalDate.of(expCalender.get(Calendar.YEAR), expCalender.get(Calendar.MONTH)+1, expCalender.get(Calendar.DAY_OF_MONTH));
						 long differenceDays = ChronoUnit.DAYS.between(curentLocalDate, curentExpLocalDate);
						 boolean isFound = false;
						 if(differenceDays == 0){
							 isFound = true; 
						 }else{
							 for(int beDays : reminderBeforeDays){
								 if(differenceDays == beDays){
									 isFound = true;
									 break;
								 }
							 }
						 }
						 User user = null;
						 if(isFound){
							 for(Iterator<User> userIte = users.iterator();userIte.hasNext();){
								User us = userIte.next();
								if(us.getId().toString().equals(details.getUserid())){
									user = us;
									userIte.remove();
									break;
								}
							 }
						 }
						 if(user != null && !"Partner".equalsIgnoreCase(user.getType()) && !"Test Account".equalsIgnoreCase(user.getNeedToFollowUp()) && NullUtil.isEmpty(user.getParentid())){
							 try {
								 Map<String, Object> data = new HashMap<String, Object>();
								 data.put("fullname",user.getFullname());
								 data.put("differenceDays", differenceDays);
								 if(details.getExpiryDate() != null){
									 data.put("expiryDate",Utility.getFormatedDateStr(details.getExpiryDate()));
								 }
								 String mailContent = VmUtil.velocityTemplate("subscription_reminder.vm", data);
								 String toMailId = user.getReminderEmail() == null || user.getReminderEmail().isEmpty() ? user.getEmail() : user.getReminderEmail(); 
								 String subject = "";
								 if(differenceDays > 0){
									 subject = "REMINDER: MasterGst Subscription expires in "+differenceDays+" days";
								 }else if(differenceDays < 0){
									 subject = "REMINDER: MasterGst Subscription expired";
								 }else{
									 subject = "REMINDER: MasterGst Subscription expires today";
								 }
								 emailService.sendEnrollEmail(toMailId, mailContent, subject,ccemail);
							} catch (Exception e) {
								e.printStackTrace();
							}
						 }
			 		}
			 	}
			 }
		}
		
		if(subscriptionDetailsexpiry != null  && !subscriptionDetailsexpiry.isEmpty()) {
			List<ObjectId> userIds = subscriptionDetailsexpiry.stream().map(sd -> new ObjectId(sd.getUserid())).collect(Collectors.toList()); 
			 List<User> users = usersDao.getUserMainDetails(userIds,userdetails);
			 for(SubscriptionDetails details : subscriptionDetailsexpiry){
				 if(NullUtil.isNotEmpty(details.getApiType()) && (details.getApiType().equalsIgnoreCase("E-INVOICEAPI") || details.getApiType().equalsIgnoreCase("GSTAPI") || details.getApiType().equalsIgnoreCase("EWAYAPI"))) {
					if(NullUtil.isNotEmpty(details.getAllowedInvoices()) && NullUtil.isNotEmpty(details.getProcessedInvoices())) {
						int differenceDays = details.getAllowedInvoices() - details.getProcessedInvoices();
						 User user = null;
							 for(Iterator<User> userIte = users.iterator();userIte.hasNext();){
								User us = userIte.next();
								if(us.getId().toString().equals(details.getUserid())){
									user = us;
									userIte.remove();
									break;
								}
							 }
							 if(user != null && !"Partner".equalsIgnoreCase(user.getType()) && !"Test Account".equalsIgnoreCase(user.getNeedToFollowUp()) && NullUtil.isEmpty(user.getParentid())){
							 try {
								 Map<String, Object> data = new HashMap<String, Object>();
								 data.put("fullname",user.getFullname());
								 data.put("differenceDays", differenceDays);
								 data.put("expiryDate",differenceDays);
								 String mailContent = VmUtil.velocityTemplate("subscription_reminder_usage.vm", data);
								 String toMailId = user.getReminderEmail() == null || user.getReminderEmail().isEmpty() ? user.getEmail() : user.getReminderEmail(); 
								 String subject = "";
								 subject = "REMINDER: MasterGst Subscription Remaining invoices are "+differenceDays;
								 emailService.sendEnrollEmail(toMailId, mailContent, subject,ccemail);
							} catch (Exception e) {
								e.printStackTrace();
							}
						 }
					}
				 }else {
					 if(NullUtil.isEmpty(details.getApiType())) {
							int differenceDays = details.getAllowedInvoices() - details.getProcessedInvoices();
							 User user = null;
								 for(Iterator<User> userIte = users.iterator();userIte.hasNext();){
									User us = userIte.next();
									if(us.getId().toString().equals(details.getUserid())){
										user = us;
										userIte.remove();
										break;
									}
								 }
								 if(user != null && !"Partner".equalsIgnoreCase(user.getType()) && !"Test Account".equalsIgnoreCase(user.getNeedToFollowUp()) && NullUtil.isEmpty(user.getParentid())){
								 try {
									 Map<String, Object> data = new HashMap<String, Object>();
									 data.put("fullname",user.getFullname());
									 data.put("differenceDays", differenceDays);
									 data.put("expiryDate",differenceDays);
									 String mailContent = VmUtil.velocityTemplate("subscription_reminder_usage.vm", data);
									 String toMailId = user.getReminderEmail() == null || user.getReminderEmail().isEmpty() ? user.getEmail() : user.getReminderEmail(); 
									 String subject = "";
									 subject = "REMINDER: MasterGst Subscription Remaining invoices are "+differenceDays;
									 emailService.sendEnrollEmail(toMailId, mailContent, subject,ccemail);
								} catch (Exception e) {
									e.printStackTrace();
								}
							 }
						}
					 }
				 }
			}
		logger.info("ReminderService : execute - End");
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
}
