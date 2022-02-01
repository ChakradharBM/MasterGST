package com.mastergst.usermanagement.runtime.job;

import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.mastergst.configuration.service.EmailService;
import com.mastergst.core.util.NullUtil;
import com.mastergst.core.util.VmUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.usermanagement.runtime.dao.SubscriptionDataDao;
import com.mastergst.usermanagement.runtime.dao.UsersDao;
import com.mastergst.usermanagement.runtime.domain.GSTR1Delete;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegisterDelete;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.repository.GSTR1DeleteRepository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterDeleteRepository;
import com.mastergst.usermanagement.runtime.support.Utility;

@Component
public class ArchiveDeleteJob implements InitializingBean, Job {

	private static final Logger logger = LoggerFactory.getLogger(ArchiveDeleteJob.class);
	
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
	
	@Autowired GSTR1DeleteRepository gstr1DeleteRepository;
	@Autowired PurchaseRegisterDeleteRepository purchaseRegisterDeleteRepository;
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("ArchiveDeleteJob : execute - Start");
		Pageable pageable = new PageRequest(0, 5000);
		List<GSTR1Delete> gstr1Content = Lists.newArrayList();
		List<PurchaseRegisterDelete> prContent = Lists.newArrayList();
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -60);
		Date today60 = cal.getTime();
		logger.info("start date  -----   "+today60);
		Page<GSTR1Delete> gstr1delete = gstr1DeleteRepository.findByDeleteddateLessThan(today60,pageable);
		gstr1Content.addAll(gstr1delete.getContent());
		while(isNotEmpty(gstr1delete) && gstr1delete.hasNext()) {
			gstr1delete = gstr1DeleteRepository.findAll(gstr1delete.nextPageable());
			gstr1Content.addAll(gstr1delete.getContent());
		}
		int batchCount = 2000;
		if(gstr1Content.size() > batchCount) {
			int index = 0;
			while((gstr1Content.size()-index) > batchCount) {
				List<GSTR1Delete> subList = gstr1Content.subList(index, index+batchCount);
				gstr1DeleteRepository.delete(subList);
				index=index+batchCount;
			}
			gstr1DeleteRepository.delete(gstr1Content.subList(index, gstr1Content.size()));
		} else {
			gstr1DeleteRepository.delete(gstr1Content);
		}
		
		Page<PurchaseRegisterDelete> prdelete = purchaseRegisterDeleteRepository.findByDeleteddateLessThan(today60,pageable);
		prContent.addAll(prdelete.getContent());
		while(isNotEmpty(prdelete) && prdelete.hasNext()) {
			prdelete = purchaseRegisterDeleteRepository.findAll(prdelete.nextPageable());
			prContent.addAll(prdelete.getContent());
		}
		if(prContent.size() > batchCount) {
			int index = 0;
			while((prContent.size()-index) > batchCount) {
				List<PurchaseRegisterDelete> subList = prContent.subList(index, index+batchCount);
				purchaseRegisterDeleteRepository.delete(subList);
				index=index+batchCount;
			}
			purchaseRegisterDeleteRepository.delete(prContent.subList(index, prContent.size()));
		} else {
			purchaseRegisterDeleteRepository.delete(prContent);
		}
		
		logger.info("ArchiveDeleteJob : execute - End");
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
