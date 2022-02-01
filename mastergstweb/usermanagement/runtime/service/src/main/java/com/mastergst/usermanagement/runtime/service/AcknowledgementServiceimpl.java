package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Lists;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.dao.AcknowledgementDao;
import com.mastergst.usermanagement.runtime.dao.StorageCredentialsDao;
import com.mastergst.usermanagement.runtime.dao.UsersDao;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.repository.ClientRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyUserRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;
import com.mastergst.usermanagement.runtime.support.Utility;
import com.mastergst.usermanagement.runtime.domain.StorageCredentials;

@Service
public class AcknowledgementServiceimpl implements AcknowledgementService {
	private static final Logger logger = LogManager.getLogger(AcknowledgementServiceimpl.class.getName());
	private static final String CLASSNAME = "AcknowledgementServiceimpl::";

	@Autowired
	private AcknowledgementDao acknowledgementDao;
	@Autowired
	private GSTR1Repository gstr1Repository;
	@Autowired
	private CompanyUserRepository companyUserRepository;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private StorageCredentialsDao storageCredentailsDao;

	@Override
	public Page<GSTR1> getMonthlyAndYearlyAcknowledgementInvoices(String id, String pendingOrUpload, List<String> companies,List<String> customers,
			int month,int year, int start, int length, String searchVal, InvoiceFilter filter) {
		final String method = "getMonthlyAcknowledgementInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);

		String yearCode = Utility.getYearCode(month, year);
		//List<String> custnames = Lists.newArrayList();
		//List<CompanyUser> users = companyUserRepository.findAllByUserid(id);
		
		return acknowledgementDao.getMonthlyAndYearlyAcknowledgementInvoices(id, pendingOrUpload, companies,customers,month, yearCode, start, length, searchVal, filter);
	}
	
	@Override
	public Page<? extends InvoiceParent> getCustomAcknowledgementInvoices(String id, String pendingOrUpload, List<String> companies,List<String> customers,String fromtime, String totime, int start, int length, String searchVal,
			InvoiceFilter filter) {
		
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1,
		Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23, 59, 59); 
		endDate = new java.util.Date(cal.getTimeInMillis());
		
		return acknowledgementDao.getCustomAcknowledgementInvoices(id, pendingOrUpload, companies,customers,stDate, endDate, start, length, searchVal, filter);
	}
	
	@Override
	public Map<String, Object> getMonthlyAndYearlyAcknowledgementSupportBilledNamesAndInvoicenos(String id, String clientid, String pendingOrUpload, int month, int year) {
		final String method = "getMonthlyAndYearlyAcknowledgementSupportBilledNamesAndInvoicenos::";
		logger.debug(CLASSNAME + method + BEGIN);

		String yearCode = Utility.getYearCode(month, year);
		Map<String, Object> supportObj = new HashMap<>();
		List<String> billedtonames = acknowledgementDao.getBillToNames(clientid,  month, yearCode);
		List<String> invoicenos = acknowledgementDao.getInvoicenos(clientid, month, yearCode);
		
		supportObj.put("billedtonames", billedtonames);
		supportObj.put("invoicenos", invoicenos);
		
		return supportObj;
	}
	
	@Override
	public Page<? extends InvoiceParent> getCustomAcknowledgementInvoices(String id, String clientid,
			String pendingOrUpload, String fromtime, String totime) {
		Pageable pageable=null;
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1,
		Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23, 59, 59); endDate = new java.util.Date(cal.getTimeInMillis());
		return gstr1Repository.findByClientidAndDateofinvoiceBetween(clientid, stDate, endDate, pageable);
	}

	@Override
	public Page<? extends InvoiceParent> getUsersMonthlyAndYearlyAcknowledgementInvoices(String id,	List<String> clientids, String pendingOrUpload,int month, int year, int start, int length, String searchVal, InvoiceFilter filter) {
		final String method = "getUsersMonthlyAndYearlyAcknowledgementInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);

		String yearCode = Utility.getYearCode(month, year);

		return acknowledgementDao.getUserMonthlyAndYearlyAcknowledgementInvoices(id, clientids, pendingOrUpload, month, yearCode, start, length, searchVal, filter);	
	}
	
	@Override
	public Page<? extends InvoiceParent> getUsersCustomAcknowledgementInvoices(String id, List<String> clientids, String pendingOrUpload, String fromtime, String totime, int start, int length, String searchVal, InvoiceFilter filter) {
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1,
		Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23, 59, 59); 
		endDate = new java.util.Date(cal.getTimeInMillis());
		
		return acknowledgementDao.getUsersCustomAcknowledgementInvoices(id, clientids, pendingOrUpload, stDate, endDate, start, length, searchVal, filter);
	}
	
	@Override
	public InvoiceParent updateAcknowledgementSubmissionDate(String invid, String updatedDate) {
		final String method = "updateAcknowledgementSubmissionDate::";
		logger.debug(CLASSNAME + method + BEGIN);
		String[] updtDate=updatedDate.split("-");
		InvoiceParent invoice= gstr1Repository.findOne(invid);
		
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(updtDate[2]), Integer.parseInt(updtDate[1]) - 1,
				Integer.parseInt(updtDate[0]) - 1, 23, 59, 59);
		Date submissionDate = new java.util.Date(cal.getTimeInMillis());
		//invoice.setS3attachment("upload");
		invoice.setS3attachementDate(submissionDate);
		
		return (InvoiceParent) gstr1Repository.save((GSTR1)invoice);
	}
	
	public long getNoOfPendingAcknowledgmentInvoices(String clientId){
		return acknowledgementDao.getNumberOfPendingAcknowledgementInvoices(clientId);
	}
	
	public long getNoOfTotalInvoices(String clientId){
		return acknowledgementDao.getNumberOfInvoices(clientId);
	}

	@Override
	public List<Client> getClientsByEmail(String email) {
		
		CompanyUser companyUser = companyUserRepository.findByEmail(email);
		if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getCompany())) {
			return Lists.newArrayList(clientRepository.findAll(companyUser.getCompany()));
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Page<? extends StorageCredentials> getStorageCredentials(String id, int month, int year, int start,
			int length, String searchVal) {
		
		return storageCredentailsDao.findById(id, start, length, searchVal);
	}
}
