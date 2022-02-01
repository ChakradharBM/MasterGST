package com.mastergst.usermanagement.runtime.cron;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import com.mastergst.usermanagement.runtime.job.ArchiveDeleteJob;
import com.mastergst.usermanagement.runtime.job.NotificationsJob;
import com.mastergst.usermanagement.runtime.job.RemindersJob;
import com.mastergst.usermanagement.runtime.service.RefreshTokenSchedulerService;

@Component("mgstJobFactory")
public abstract class JobFactoryImpl implements JobFactory{

	@Autowired
	private RemindersJob remindersJob;
	@Autowired
	private ArchiveDeleteJob archiveDeleteJob;
	@Autowired
	private RefreshTokenSchedulerService refreshTokenSchedulerService;
	
	@Override
	public Job newJob(TriggerFiredBundle bundle, Scheduler arg1) throws SchedulerException {
		JobDetail jobDetail = bundle.getJobDetail();
		Class clazz = jobDetail.getJobClass();
		if(clazz.equals(RemindersJob.class)){
			return remindersJob;
		}else if(clazz.equals(NotificationsJob.class)){
			return getNotificationsJob();
		}else if(clazz.equals(RefreshTokenSchedulerService.class)){
			return refreshTokenSchedulerService;
 		}else if(clazz.equals(ArchiveDeleteJob.class)){
			return archiveDeleteJob;
 		}
		return null;
	}
	
	@Lookup
	public abstract NotificationsJob getNotificationsJob();
	
	
}
