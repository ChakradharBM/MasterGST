/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import com.google.api.client.util.Lists;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.HeaderKeys;
import com.mastergst.login.runtime.repository.HeaderKeysRepository;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.repository.MeteringRepository;

/**
 * Service class that performs scheduling of Data sync calls.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class SyncDataSchedulerService implements InitializingBean, DisposableBean, Job {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SyncDataSchedulerService.class);
	
	@Value("${ihub.email}")
	private String iHubEmail;
	@Autowired
	private HeaderKeysRepository headerKeysRepository;
	@Autowired
	private MeteringRepository meteringRepository;
	@Autowired
	private IHubConsumerService iHubConsumerService;
	@Autowired
	private ClientService clientService;
	org.quartz.Scheduler qScheduler;
	private void scheduleSyncInvocations() {
		logger.debug("SyncDataSchedulerService : scheduleSyncInvocations : Begin");
		JobDetail job = JobBuilder.newJob(SyncDataSchedulerService.class)
				.withIdentity("mastergst-sync", "bvmcs").build();
		job.getJobDataMap().put("iHubConsumerService", iHubConsumerService);
		job.getJobDataMap().put("headerKeysRepository", headerKeysRepository);
		job.getJobDataMap().put("meteringRepository", meteringRepository);
		job.getJobDataMap().put("clientService", clientService);
		job.getJobDataMap().put("email", iHubEmail);
		Trigger trigger = TriggerBuilder
			.newTrigger()
			.withIdentity("mastergst-sync-trigger", "bvmcs")
			.withSchedule(
				CronScheduleBuilder.cronSchedule("0 1 3 ? * 6")) //Every Saturday 3:01 AM
			.build();
		
		try {
			qScheduler = new StdSchedulerFactory().getScheduler();
			qScheduler.start();
			qScheduler.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			logger.error("SyncDataSchedulerService : scheduleSyncInvocations : ERROR ", e);
		}
		logger.debug("SyncDataSchedulerService : scheduleSyncInvocations : End");
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("SyncDataSchedulerService : execute : Begin");
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH)+1;
		int year = cal.get(Calendar.YEAR);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)-2, cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		Date stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)+2, 0, 0, 0);
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		ClientService eClientService = ((ClientService) context.getJobDetail().getJobDataMap().get("clientService"));
		HeaderKeysRepository eHeaderKeysRepository = ((HeaderKeysRepository) context.getJobDetail().getJobDataMap().get("headerKeysRepository"));
		MeteringRepository eMeteringRepository = ((MeteringRepository) context.getJobDetail().getJobDataMap().get("meteringRepository"));
		String email = ((String) context.getJobDetail().getJobDataMap().get("email"));
		List<HeaderKeys> headerKeys = eHeaderKeysRepository.findByEmailAndUpdatedDateBetweenAndAuthtokenNotNull(email, stDate, endDate);
		cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)-7, 0, 0, 0);
		Date statusStartDate = new java.util.Date(cal.getTimeInMillis());
		List<String> userNameList = Lists.newArrayList();
		for(HeaderKeys headerKey : headerKeys) {
			if(NullUtil.isNotEmpty(headerKey)) {
				Long count = eMeteringRepository.countByEmailAndGstnusernameAndServicenameAndCreatedDateBetween(email, headerKey.getGstusername(), MasterGSTConstants.TRACK_STATUS_SERVICE_NAME, statusStartDate, now);
				if(count == 0) {
					userNameList.add(headerKey.getGstusername());
				}
			}
		}
		List<Client> clients = eClientService.findByGSTNames(userNameList);
		for(Client client : clients) {
			eClientService.syncInvoiceData(client, MasterGSTConstants.GSTR1, null, null, month, year);
		}
		logger.debug("SyncDataSchedulerService : execute : End");
	}

	@Override
	public void destroy() throws Exception {
		qScheduler.shutdown();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		scheduleSyncInvocations();
	}

}
