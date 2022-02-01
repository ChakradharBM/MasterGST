package com.mastergst.usermanagement.runtime.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.StorageCredentials;

public interface AcknowledgementService {
	
	Page<GSTR1> getMonthlyAndYearlyAcknowledgementInvoices(String id, String pendingOrUpload,  List<String> companies,List<String> customers,int month, int year, int start, int length, String searchVal, InvoiceFilter filter);
	
	public Map<String, Object> getMonthlyAndYearlyAcknowledgementSupportBilledNamesAndInvoicenos(String id, String clientid, String pendingOrUpload, int month, int year);
			
	public Page<? extends InvoiceParent> getCustomAcknowledgementInvoices(String id, String pendingOrUpload, List<String> companies,List<String> customers,String fromtime, String totime, int start, int length, String searchVal, InvoiceFilter filter);
	public Page<? extends InvoiceParent> getCustomAcknowledgementInvoices(String id, String clientid, String pendingOrUpload,  String fromtime, String totime);

	public Page<? extends InvoiceParent> getUsersMonthlyAndYearlyAcknowledgementInvoices(String id, List<String> clientids, String pendingOrUpload, int month, int year, int start, int length, String searchVal, InvoiceFilter filter);

	public Page<? extends InvoiceParent> getUsersCustomAcknowledgementInvoices(String id, List<String> clientids, String pendingOrUpload, String fromtime, String totime, int start, int length, String searchVal, InvoiceFilter filter);

	public InvoiceParent updateAcknowledgementSubmissionDate(String invid, String updatedDate);
	
	public long getNoOfPendingAcknowledgmentInvoices(String clientId);
	
	public long getNoOfTotalInvoices(String clientId);
	
	public List<Client> getClientsByEmail(String email);
		Page<? extends StorageCredentials> getStorageCredentials(String id, int month, int year, int start, int length,
			String searchVal);
}
