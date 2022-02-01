package com.mastergst.usermanagement.runtime.cron;

import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mastergst.login.runtime.repository.HeaderKeysRepository;
import com.mastergst.usermanagement.runtime.domain.SmtpDetails;
import com.mastergst.usermanagement.runtime.job.ArchiveDeleteJob;
import com.mastergst.usermanagement.runtime.job.NotificationsJob;
import com.mastergst.usermanagement.runtime.job.RemindersJob;
import com.mastergst.usermanagement.runtime.service.IHubConsumerService;
import com.mastergst.usermanagement.runtime.service.RefreshTokenSchedulerService;
import com.mastergst.usermanagement.runtime.service.SmtpDetailsService;

@Service
public class SchedulerService implements InitializingBean, DisposableBean{
	
	private static final Logger logger = LoggerFactory
			.getLogger(SchedulerService.class);

	@Value("${reminder.subscription.cron}")
	private String cronExpr;
	
	@Value("${archivedelete.cron}")
	private String deleteCronExpr;
	
	@Autowired
	@Qualifier("mgstJobFactory")
	private JobFactory jobFactory;
	
	@Autowired
	private SmtpDetailsService smtpDetailsService;
	
	@Value("${ihub.email}")
	private String iHubEmail;
	@Value("${ihub.client.id}")
	private String ihubclientid;
	@Value("${ihub.client.secret}")
	private String ihubclientsecretid;
	@Autowired
	private HeaderKeysRepository headerKeysRepository;
	@Autowired
	private IHubConsumerService iHubConsumerService;
	
	private org.quartz.Scheduler qScheduler;
	
	private void init(){
		try {
			qScheduler = new StdSchedulerFactory().getScheduler();
			qScheduler.setJobFactory(jobFactory);
			qScheduler.start();
			intializeRefreshTokenService();
			initializeRemindersJob();
			initializeNotificationService();
			initializeArchiveDelete();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	private void initializeArchiveDelete(){
		logger.debug("MgstSchedulerService : initializeArchiveDelete - Start");
		
			JobDetail job = JobBuilder.newJob(ArchiveDeleteJob.class)
					.withIdentity("mastergst-archive-delete", "bvmcs").build();
			Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("mastergst-archive-delete-trig", "bvmcs")
				.withSchedule(
					CronScheduleBuilder.cronSchedule(deleteCronExpr)) 
				.build();
			
			try {
				qScheduler.scheduleJob(job, trigger);
			} catch (SchedulerException e) {
				logger.error("MgstSchedulerService : initializeArchiveDelete - ERROR ", e);
			}
		logger.debug("MgstSchedulerService : initializeArchiveDelete - End");
	}
	
	private void initializeRemindersJob(){
		logger.debug("MgstSchedulerService : initializeRemindersJob - Start");
		
			JobDetail job = JobBuilder.newJob(RemindersJob.class)
					.withIdentity("mastergst-reminder-subscription", "bvmcs").build();
			Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity("mastergst-reminder-subscription-trig", "bvmcs")
				.withSchedule(
					CronScheduleBuilder.cronSchedule(cronExpr)) 
				.build();
			
			try {
				qScheduler.scheduleJob(job, trigger);
			} catch (SchedulerException e) {
				logger.error("MgstSchedulerService : initializeRemindersJob - ERROR ", e);
			}
		logger.debug("MgstSchedulerService : initializeRemindersJob - End");
	}
	
	private void initializeNotificationService(){
		
		logger.debug("MgstSchedulerService : initializeNotificationService - Start");
		new Thread(()->{
			List<SmtpDetails> smtpDetails = smtpDetailsService.getSmtpConfigutedClients();
			if(smtpDetails != null){
				for(SmtpDetails smtpDetail : smtpDetails){
					String cronExprVal = smtpDetail.getSchedlueExpressionVal();
					String cronExpr = generateCronExpressionByVal(cronExprVal);
					if(cronExpr != null && !"".equals(cronExpr)){
						JobDetail job = JobBuilder.newJob(NotificationsJob.class)
								.withIdentity("mastergst-notification-"+smtpDetail.getClientId(), "bvmcs").build();
						job.getJobDataMap().put("clientId", smtpDetail.getClientId());
						job.getJobDataMap().put("cronExpVal", cronExpr);
						Trigger trigger = TriggerBuilder
							.newTrigger()
							.withIdentity("mastergst-notification-trig"+smtpDetail.getClientId(), "bvmcs")
							.withSchedule(
								CronScheduleBuilder.cronSchedule(cronExpr))
							.build();
						
						try {
							qScheduler.scheduleJob(job, trigger);
						} catch (SchedulerException e) {
							logger.error("MgstSchedulerService : initializeNotificationService - ERROR ", e);
						}
					}
				}
			}
		}).start();
		logger.debug("MgstSchedulerService : initializeNotificationService - End");
	}
	
	private void intializeRefreshTokenService(){
		logger.debug("MgstSchedulerService : intializeRefreshTokenService - Start");
		JobDetail job = JobBuilder.newJob(RefreshTokenSchedulerService.class)
				.withIdentity("mastergst", "bvmcs").build();
		job.getJobDataMap().put("iHubConsumerService", iHubConsumerService);
		job.getJobDataMap().put("headerKeysRepository", headerKeysRepository);
		job.getJobDataMap().put("email", iHubEmail);
		job.getJobDataMap().put("ihubclientid", ihubclientid);
		job.getJobDataMap().put("ihubclientsecretid", ihubclientsecretid);
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
			logger.error("RefreshTokenSchedulerService : intializeRefreshTokenService : ERROR ", e);
		}
		logger.debug("RefreshTokenSchedulerService : intializeRefreshTokenService : End");
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	@Override
	public void destroy() throws Exception {
		qScheduler.shutdown();
	}
	
	public static String generateCronExpressionByVal(String val){
		if(val == null || "".equals(val)){
			return null;
		}
		int v = Integer.parseInt(val);
		if(v == 24){
			return "0 0 3 * * ?";
		}else{
			return "0 0 0/"+v+" * * ?";
		}
		
	}
}
