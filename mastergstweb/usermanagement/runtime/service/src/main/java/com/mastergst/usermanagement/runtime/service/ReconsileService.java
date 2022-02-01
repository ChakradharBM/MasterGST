package com.mastergst.usermanagement.runtime.service;


import java.util.Date;

import org.springframework.data.domain.Page;

import com.mastergst.usermanagement.runtime.domain.InvoiceParent;

public interface ReconsileService {

	String getMatchedAndPresentMonthPurchasesInvoices(final String clientid, final int month, final String yearCode,final String fp, final int year)throws Exception;
	String getMannualMatchedAndPresentMonthPurchasesInvoices(final String clientid, final int month, final String yearCode,final String fp,final int year)throws Exception;
	
	String getMatchedAndPresentMonthGSTR2AInvoices(final String clientid, final int month, final String yearCode,final String fp,final int year)throws Exception;
	String getMannualMatchedAndPresentMonthGSTR2AInvoices(final String clientid, final int month, final String yearCode,final String fp,final int year)throws Exception;
	
	String getMatchedAndPresentMonthFYInvoices(final String clientid, final int year)throws Exception;
	String getgstr2Matchedfyinvs(final String clientid, final int year)throws Exception;
	String getgstr2MannualMatchedfyinvs(final String clientid, final int year)throws Exception;
	String getMannualMatchedAndPresentMonthFYInvoices(final String clientid, final int year)throws Exception;
	
	Page<? extends InvoiceParent> getPresentMonthfyinvsMatchingIdisNull(final String clientid,final String yrcd);
	Page<? extends InvoiceParent> getPresentMonthfyinvsMatchingIdisNotNull(final String clientid,final String yrcd);
	
	
	String getMatchedAndPresentMonthPurchasesInvoicesByTransactionDate(final String clientid, final int month, final String yearCode,final String fp, final int year)throws Exception;
	String getMannualMatchedAndPresentMonthPurchasesInvoicesByTransactionDate(final String clientid, final int month, final String yearCode,final String fp,final int year)throws Exception;
	String getMatchedAndPresentMonthGSTR2AInvoicesByTransactionDate(final String clientid, final int month, final String yearCode,final String fp,final int year)throws Exception;
	
	Page<? extends InvoiceParent> getPresentMonthfyinvsMatchingIdisNullByTransactionDate(final String clientid,final Date d1, final Date d2);
	Page<? extends InvoiceParent> getPresentMonthfyinvsMannualMatchingIdisNotNullByTransactionDate(final String clientid,final Date d1, final Date d2);
	Page<? extends InvoiceParent> getPresentMonthfyinvsMatchingIdisNotNullByTransactionDate(final String clientid,final Date d1, final Date d2);
	String getMannualMatchedAndPresentMonthGSTR2AInvoicesByTransactionDate(final String clientid, final int month, final String yearCode,final String fp,final int year)throws Exception;
	
	public Page<? extends InvoiceParent> getReport_PresentMonthfyinvsMannualMatchingIdisNotNullByTransactionDate(String clientid, Date d1, Date d2);
	
}
