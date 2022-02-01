package com.mastergst.usermanagement.runtime.job;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mastergst.configuration.service.NotificationEmailService;
import com.mastergst.core.util.VmUtil;
import com.mastergst.usermanagement.runtime.cron.SchedulerService;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.SmtpDetails;
import com.mastergst.usermanagement.runtime.repository.SmtpDetailsRepository;
import com.mastergst.usermanagement.runtime.service.AcknowledgementService;
import com.mastergst.usermanagement.runtime.service.ClientService;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;


@Component
@Scope(scopeName=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NotificationsJob implements Job{

	private static final Logger logger = LoggerFactory
			.getLogger(NotificationsJob.class);
	@Autowired
	private SmtpDetailsRepository smtpDetailsRepository;
	@Autowired
	private ClientService clientService;
	@Autowired
	private AcknowledgementService acknowledgementService;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String clientId = (String)dataMap.get("clientId");
		 try {
			 SmtpDetails smtpDetails  = smtpDetailsRepository.findByClientId(clientId);
			 Object valObj = dataMap.get("cronExpVal");//", smtpDetail.getSchedlueExpressionVal());
			 String cronExpVal = valObj.toString();
			 String cronExpValLatest = smtpDetails.getSchedlueExpressionVal();
			 if(!cronExpValLatest.equals(cronExpVal)){
				 String cronExprLatest = SchedulerService.generateCronExpressionByVal(cronExpValLatest);
				 Trigger trigger = TriggerBuilder
							.newTrigger()
							.withIdentity(context.getTrigger().getKey().getName(), "bvmcs")
							.withSchedule(
								CronScheduleBuilder.cronSchedule(cronExprLatest))
							.build();
				 context.getScheduler().rescheduleJob(context.getTrigger().getKey(), trigger);
			 }else{
				 Client client = clientService.findById(clientId);
				 if(isNotEmpty(client)) {
					 Map<String, Object> data = new HashMap<String, Object>();
					 data.put("client",client.getBusinessname());
					 data.put("totalInvoices", acknowledgementService.getNoOfTotalInvoices(clientId));
					 data.put("pendingInvoices", acknowledgementService.getNoOfPendingAcknowledgmentInvoices(clientId));
					 String subject = "NOTIFICATION: MasterGst Invoice attachments upload status";
					 String mailContent = VmUtil.velocityTemplate("inv_attach_notification.vm", data);
					 NotificationEmailService notificationEmailService = new NotificationEmailService(smtpDetails.getHost(), smtpDetails.getPort(), 
							 smtpDetails.getUsername(), smtpDetails.getPassword(), smtpDetails.getFrom(), smtpDetails.getToAddress(), smtpDetails.getCcAddress(), subject, mailContent);
					 notificationEmailService.send();
				 }
			 }
		} catch (Exception e) {
			logger.error("Exception occurred",e);
		}
		
	}
}
