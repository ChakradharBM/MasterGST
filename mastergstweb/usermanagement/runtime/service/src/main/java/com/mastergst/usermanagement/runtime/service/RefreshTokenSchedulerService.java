/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.HeaderKeys;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.domain.UserKeys;
import com.mastergst.login.runtime.repository.HeaderKeysRepository;
import com.mastergst.login.runtime.service.UserService;

/**
 * Service class that performs scheduling of Refresh Token calls.
 * 
 * @author Ashok Samart
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class RefreshTokenSchedulerService implements Job {
	
	private static final Logger logger = LoggerFactory.getLogger(RefreshTokenSchedulerService.class);
	
	private UserService userService;
	
	/**@Value("${ihub.email}")
	private String iHubEmail;
	@Autowired
	private HeaderKeysRepository headerKeysRepository;
	@Autowired
	private IHubConsumerService iHubConsumerService;
	
	private void scheduleTokenInvocations() {
		logger.debug("RefreshTokenSchedulerService : scheduleTokenInvocations : Begin");
		JobDetail job = JobBuilder.newJob(RefreshTokenSchedulerService.class)
				.withIdentity("mastergst", "bvmcs").build();
		job.getJobDataMap().put("iHubConsumerService", iHubConsumerService);
		job.getJobDataMap().put("headerKeysRepository", headerKeysRepository);
		job.getJobDataMap().put("email", iHubEmail);
		Trigger trigger = TriggerBuilder
			.newTrigger()
			.withIdentity("mastergst-refreshtoken", "bvmcs")
			.withSchedule(
				CronScheduleBuilder.cronSchedule("0 0/20 * * * ?"))
			.build();
		org.quartz.Scheduler qScheduler;
		try {
			qScheduler = new StdSchedulerFactory().getScheduler();
			qScheduler.start();
			qScheduler.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			logger.error("RefreshTokenSchedulerService : scheduleTokenInvocations : ERROR ", e);
		}
		logger.debug("RefreshTokenSchedulerService : scheduleTokenInvocations : End");
	}**/

	
	public boolean getStage(User user, HeaderKeys headerKey) {
		logger.debug("RefreshTokenSchedulerService : getStage : Begin");
		boolean stage = true;
		if(NullUtil.isNotEmpty(user)) {
			List<UserKeys> lUserKeys = user.getUserkeys();
			if (NullUtil.isNotEmpty(lUserKeys)) {
				for (UserKeys userKeys : lUserKeys) {
					if ((userKeys.getClientid().trim().equals(headerKey.getUserclientid())) && (userKeys.getClientsecret().trim().equals(headerKey.getUserclientsecret()))) {
						if (NullUtil.isNotEmpty(userKeys.getStage())) {
							logger.debug("RefreshTokenSchedulerService : getStage stage:: "+userKeys.getStage());
							if (MasterGSTConstants.EINVOICE_SANDBOX.equals(userKeys.getStage().trim()) || MasterGSTConstants.EINVOICE_PRODUCTION.equals(userKeys.getStage().trim())
									|| MasterGSTConstants.EWAYBILLSANDBOX.equals(userKeys.getStage().trim())|| MasterGSTConstants.EWAYBILLPRODUCTION.equals(userKeys.getStage().trim())) {
								stage = false;
							}
						}
					}
				}
			}			
		}
		logger.debug("RefreshTokenSchedulerService : getStage flag:: "+stage);
		return stage;
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("RefreshTokenSchedulerService : getStage : Begin");
		Calendar cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)-1, 0, 0, 0);
		Date stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)+1, 0, 0, 0);
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		IHubConsumerService eIHubConsumerService = ((IHubConsumerService) context.getJobDetail().getJobDataMap().get("iHubConsumerService"));
		HeaderKeysRepository eHeaderKeysRepository = ((HeaderKeysRepository) context.getJobDetail().getJobDataMap().get("headerKeysRepository"));
		String email = (String) context.getJobDetail().getJobDataMap().get("email");
		String ihubclientid = (String) context.getJobDetail().getJobDataMap().get("ihubclientid");
		String ihubclientsecretid = (String) context.getJobDetail().getJobDataMap().get("ihubclientsecretid");
		//List<HeaderKeys> headerKeys = eHeaderKeysRepository.findByEmailAndUpdatedDateBetweenAndAuthtokenNotNull(email,stDate, endDate);
		//List<HeaderKeys> headerKeys = eHeaderKeysRepository.findByEmailAndUpdatedDateBetweenAndUserclientidAndUserclientsecretAndAuthtokenNotNull(email,stDate, endDate,ihubclientid,ihubclientsecretid);
		List<HeaderKeys> headerKeys = eHeaderKeysRepository.findByEmailAndUpdatedDateBetweenAndUserclientidAndUserclientsecretAndAuthtokenNotNullAndRefreshtokenerorNull(email,stDate, endDate,ihubclientid,ihubclientsecretid);
		
		for(HeaderKeys headerKey : headerKeys) {
			if(NullUtil.isNotEmpty(headerKey) && NullUtil.isNotEmpty(headerKey.getUpdatedDate()) && NullUtil.isNotEmpty(headerKey.getExpiry()) && headerKey.getExpiry() > 0) {
				long duration = Calendar.getInstance().getTime().getTime() - headerKey.getUpdatedDate().getTime();
				long diff = TimeUnit.MILLISECONDS.toMinutes(duration);
				if((diff <= 1440) && (diff > 340)) { // < ((headerKey.getExpiry()/2)-30)
					try {
						eIHubConsumerService.invokeRefreshToken(headerKey);
					} catch (Exception e) {
						logger.error("RefreshTokenSchedulerService : execute : ERROR {}", e);
					}
				}
			}
		}
		logger.info("RefreshTokenSchedulerService : getStage : End");
	}

}
