package com.mastergst.usermanagement.runtime.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;

public interface ReportsService {
	Map<String, Object> getGlobalReportInvoices(Pageable pageable, List<String> listofclients, String id, String retType,
			String string, int month, int year, int start, int length, String searchVal, InvoiceFilter filter,boolean b,String dwnldFromGSTN);

	Map<String, Object> getGlobalReportsCustomInvoices(Pageable pageable, List<String> clientids, String id, String retType,
			String type, String fromtime, String totime, int start, int length, String searchVal, InvoiceFilter filter,boolean flag,String dwnldFromGSTN);
	
	Map<String, Object> getGlobalReportInvoices(Pageable pageable, List<String> listofclients, String id, String retType,
			String string, int month, int year, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter,boolean b,String dwnldFromGSTN);

	Map<String, Object> getGlobalReportsCustomInvoices(Pageable pageable, List<String> clientids, String id, String retType,
			String type, String fromtime, String totime, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter,boolean flag,String dwnldFromGSTN);

	
	Map<String, Map<String, String>> getConsolidatedSummeryForYearGlobalReports(List<String> clientids,
			String returntype, String yearCode, boolean b,String dwnldFromGSTN,InvoiceFilter filter);

	Map<String, Map<String, String>> getConsolidatedSummeryForGlobalCustomReports(List<String> clientids,
			String returntype, String fromtime, String totime,String dwnldFromGSTN,InvoiceFilter filter);
	
	public Map<String, String> getGlobalReportsSummaryMap(Pageable pageable, List<String> clientids, int i, int j, String returntype,String dwnldFromGSTN) throws Exception;

	Map<String, Map<String, String>> getConsolidatedSummeryForYearReports(List<String> clientids, String returntype,
			String yearCode, boolean checkQuarterly,String dwnldFromGSTN);

	Map<String, Object> getGlobalInvoicesSupport(List<String> clientids, String returntype, String string, int month,
			int year,String dwnldFromGSTN);

	Map<String, Object> getGlobalCustomInvoicesSupport(List<String> clientids, String returntype, String string,
			String fromtime, String totime,String dwnldFromGSTN);
	
	Map<String, Map<String, String>> getConsolidatedSummeryForYearMonthwise(List<String> clientids, String yearCd);

}
