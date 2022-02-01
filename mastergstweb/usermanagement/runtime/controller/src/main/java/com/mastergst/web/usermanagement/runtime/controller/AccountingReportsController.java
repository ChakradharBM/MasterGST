package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.accounting.util.AccountingDataUtils;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.Amounts;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.Node;
import com.mastergst.usermanagement.runtime.repository.AccountingJournalRepository;
import com.mastergst.usermanagement.runtime.service.AccountingReportsService;
import com.mastergst.usermanagement.runtime.support.Utility;

@RestController
public class AccountingReportsController {
	
	private static final Logger logger = LogManager.getLogger(AccountingReportsController.class.getName());
	private static final String CLASSNAME = "AccountingReportsController::";
	
	@Autowired
	private AccountingReportsService accountingReportsService;
	@Autowired
	private AccountingDataUtils accountingDataUtils;
	
	@Autowired
	private AccountingJournalRepository accountingJournalRepository;
	
	@GetMapping("/getmonthlytrailbalance/{clientid}/{month}/{year}")
	public Map<String, Node> getMonthlyTrailBalanceReportData(
			@PathVariable String clientid, @PathVariable int month,@PathVariable int year) {
		final String method = "getTrailBalanceReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);
		
		return accountingReportsService.getMonthlyTrailBalance(clientid ,month ,year);
	}
	
	@GetMapping("/getyearlytrailbalance/{clientid}/{year}")
	public Map<String, Node> getYearlyTrailBalanceReportData(@PathVariable String clientid, @PathVariable int year) {
		final String method = "getYearlyTrailBalanceReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "year\t" + year);
		
		return accountingReportsService.getYearlyTrailBalance(clientid,year);
	}

	@GetMapping("/getcustomtrailbalance/{clientid}/{fromtime}/{totime}")
	public Map<String, Node> getCustomTrailBalanceReportData(
			@PathVariable String clientid,  @PathVariable String fromtime, @PathVariable String totime) {
		final String method = "getCustomTrailBalanceReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "fromtime\t" + fromtime);
		logger.debug(CLASSNAME + method + "totime\t" + totime);
		
		return accountingReportsService.getCustomTrailBalance(clientid,fromtime,totime);
	}
	
	@GetMapping("/getmonthlypandl/{clientid}/{month}/{year}")
	public Map<String,Node> getMonthlyPandLReportData(
			@PathVariable String clientid, @PathVariable int month,@PathVariable int year) throws JsonProcessingException {
		final String method = "getMonthlyPandLReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);
		
		return accountingReportsService.getMonthlyPandLReport(clientid, month, year);
	}
	
	@GetMapping("/getyearlypandl/{clientid}/{year}")
	public Map<String,Node> getYearlyPandLReportData(
			@PathVariable String clientid, @PathVariable int year) throws JsonProcessingException {
		final String method = "getMonthlyPandLReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "year\t" + year);
		
		return accountingReportsService.getYearlyPandLReport(clientid,year);
	}
	
	@GetMapping("/getmonthlybalancesheet/{clientid}/{month}/{year}")
	public Map<String,Node> getMonthlyBalanSheetReportData(
			@PathVariable String clientid, @PathVariable int month,@PathVariable int year) {
		final String method = "getMonthlyBalanSheetReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);
		
		return accountingReportsService.getMonthlyBalanceSheet(clientid,month,year);
	}
	
	@GetMapping("/getyearlybalancesheet/{clientid}/{year}")
	public Map<String,Node> getYearlyBalanSheetReportData(
			@PathVariable String clientid, @PathVariable int year) {
		final String method = "getYearlyBalanSheetReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "year\t" + year);
		
		return accountingReportsService.getYearlyBalanceSheet(clientid,year);
	}
	
	@GetMapping("/getmonthlyledgersdata/{clientid}/{month}/{year}")
	public Map<String,Object> getMonthlyLedgerReportData(@PathVariable String clientid, @PathVariable int month,@PathVariable int year, @RequestParam String ledgername, HttpServletRequest request) {
		final String method = "getMonthlyLedgerReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);
		

		Map<String,Object> data=new HashMap<>();
		
		String strMonths = month < 10 ? "0" + month : month + "";
		String monthYear = strMonths+year;
		List<String> invoiceMonth=Arrays.asList(monthYear);
		
		List<AccountingJournal> journals = accountingReportsService.getLedgersDataNew(ledgername, clientid, invoiceMonth);
		if(isNotEmpty(journals)) {
			journals.stream().forEach(journal->journal.setAccountingJournalId(journal.getId().toString())); 
		}
		List<String> previnvoiceMonth=new ArrayList<>();
		
		if(month == 1 || month == 2 ||month == 3) {
			int lastyear=year-1;
			for(int i=4;i<=12;i++) {
				if(i>9) {
					previnvoiceMonth.add(i+""+lastyear);					
				}else{
					previnvoiceMonth.add("0"+i+lastyear+"");
				}
			}
			for(int i=1;i<month;i++) {
				previnvoiceMonth.add("0"+i+year+"");				
			}
		}else {
			if(month !=4) {
				for(int i=4;i<month;i++) {
					if(i>9) {
						previnvoiceMonth.add(i+""+year);					
					}else{
						previnvoiceMonth.add("0"+i+year);
					}
				}
			}
		}
		List<AccountingJournal> openingjournals = null;
		if(isNotEmpty(previnvoiceMonth) && previnvoiceMonth.size() > 0) {
			openingjournals = accountingReportsService.getLedgersDataNew(ledgername, clientid, previnvoiceMonth);
		}
		
		Map<String, Amounts> closingAndOpeningBalance = accountingDataUtils.newCalculateLedgersAmountstb(journals, openingjournals);
		
		Amounts amounts = closingAndOpeningBalance.get(ledgername);
		if(amounts == null) {
			amounts = new Amounts(0, 0, 0, 0);
		}
		data.put("journals", journals);
		data.put("closingandOpeningamounts", amounts);
		
		return data;
	}
	
	@GetMapping("/getyearlyledgersdata/{clientid}/{year}/{ledgername}")
	public Map<String,Object> getYearlyLedgerReportData(@PathVariable String ledgername, @PathVariable String clientid,@PathVariable int year, HttpServletRequest request) {
		final String method = "getYearlyLedgerReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "year\t" + year);
		
		Map<String,Object> data = new HashMap<>();
		
		int nextYear = year+1;
		int prevsYear = year-1;
		List<String> invoiceYear = Arrays.asList("04"+year,"05"+year,"06"+year,"07"+year,"08"+year,"09"+year,"10"+year,"11"+year,"12"+year,"01"+nextYear,"02"+nextYear,"03"+nextYear);
		
		List<AccountingJournal> journals = accountingReportsService.getLedgersDataNew(ledgername, clientid, invoiceYear);
		
		if(isNotEmpty(journals)) {
			journals.stream().forEach(journal->journal.setAccountingJournalId(journal.getId().toString()));
		}
		
		List<String> previnvoiceYear = Arrays.asList("04"+prevsYear,"05"+prevsYear,"06"+prevsYear,"07"+prevsYear,"08"+prevsYear,"09"+prevsYear,"10"+prevsYear,"11"+prevsYear,"12"+prevsYear,"01"+year,"02"+year,"03"+year);
		
		List<AccountingJournal> openingjournals = accountingReportsService.getLedgersDataNew(ledgername, clientid, previnvoiceYear);
		
		Map<String, Amounts> closingAndOpeningBalance = accountingDataUtils.newCalculateLedgersAmountstb(journals, openingjournals);
		
		Amounts amounts = closingAndOpeningBalance.get(ledgername);
		if(amounts == null) {
			amounts = new Amounts(0, 0, 0, 0);
		}
		data.put("journals", journals);
		data.put("closingandOpeningamounts", amounts);
		
		return data;
	}
	
	@GetMapping("/getcustomledgersdata/{clientid}/{totime}/{fromtime}/{ledgername}")
	public Map<String,Object> getCustomLedgerReportData(@PathVariable String ledgername, @PathVariable String clientid,
										@PathVariable String fromtime, @PathVariable String totime, HttpServletRequest request) {
		final String method = "getCustomLedgerReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		
		Map<String,Object> data = new HashMap<>();
		
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		
		Date currentStDate = null;
		Date currentEndDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 0, 0, 0);
		currentStDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]), 23, 59, 59);
		currentEndDate = new java.util.Date(cal.getTimeInMillis());
		
		List<AccountingJournal> journals = accountingReportsService.getCustomLedgersDataNew(ledgername, clientid, currentStDate, currentEndDate);
		
		if(isNotEmpty(journals)) {
			journals.stream().forEach(journal->journal.setAccountingJournalId(journal.getId().toString())); 
		}
		
		Date prevStDate = null;
		Date prevEndDate = null;
		List<AccountingJournal> openingjournals = null;
		if(Integer.parseInt(fromtimes[1]) == 4 && Integer.parseInt(fromtimes[0]) == 1) {
			//no opening balance and closing balance
			openingjournals = null;
		}else {
			if(Integer.parseInt(fromtimes[2]) == Integer.parseInt(totimes[2])) {
				cal.set(Integer.parseInt(totimes[2])-1, 3, 01, 0, 0, 0);
				prevStDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]) - 1, 23, 59, 59);
				prevEndDate = new java.util.Date(cal.getTimeInMillis());
				openingjournals = accountingReportsService.getCustomLedgersDataNew(ledgername, clientid, prevStDate, prevEndDate);
			} else {
				cal.set(Integer.parseInt(totimes[2]), 3, 01, 0, 0, 0);
				prevStDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1])-1, Integer.parseInt(totimes[0])-1, 0, 0, 0);
				prevEndDate = new java.util.Date(cal.getTimeInMillis());

				openingjournals = accountingReportsService.getCustomLedgersDataNew(ledgername, clientid, prevStDate, prevEndDate);
			}
		}
		
		Map<String, Amounts> closingAndOpeningBalance = accountingDataUtils.newCalculateLedgersAmountstb(journals, openingjournals);
		
		Amounts amounts = closingAndOpeningBalance.get(ledgername);
		if(amounts == null) {
			amounts = new Amounts(0, 0, 0, 0);
		}
		data.put("journals", journals);
		data.put("closingandOpeningamounts", amounts);
		
		return data;
	}
	
	@GetMapping("/getjournalsdata/{id}")
	public AccountingJournal getJournalData(@PathVariable String id) {
		final String method = "getMonthlyLedgerReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		AccountingJournal journals=accountingJournalRepository.findOne(id);
		if(isNotEmpty(journals)) {
			journals.setAccountingJournalId(journals.getId().toString());
		}
		
		return journals;
	}
	
	@GetMapping("/gettdsdetails/{clientid}/{month}/{year}")
	public String getTdsReportData(@PathVariable String clientid, @PathVariable int month, @PathVariable int year,@RequestParam String report, HttpServletRequest request) throws JsonProcessingException {
		final String method = "getTdsReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + year);
		logger.debug(CLASSNAME + method + "year\t" + year);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		boolean tdsFlag = report.equalsIgnoreCase("tdsreport") ? true : false;
		Map<String, Object> invoicesMap = accountingReportsService.getMontlyAndYearlyDeductionOrCollection(clientid, month, year, tdsFlag, start, length, searchVal);
		
		Page<AccountingJournal> invoices = (Page<AccountingJournal>) invoicesMap.get("invoices");
		if(isNotEmpty(invoices.getContent())) {
			invoices.getContent().stream().forEach(invoice -> {
				if(isNotEmpty(invoice.getCtin())) {
					invoice.setCtin(invoice.getCtin().substring(2, 12));
				}
			});
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer = mapper.writer();
		
		return writer.writeValueAsString(invoicesMap);
	}
	
	@RequestMapping(value = "/getsummarytdsdetails/{clientid}/{month}/{year}")
	public @ResponseBody Map<String,Map<String,String>> getConsolidateReportsTotalAmounts(@PathVariable String clientid,@PathVariable int month, @PathVariable int year,@RequestParam String report,HttpServletRequest request){
		final String method = "getConsolidateReportsTotalAmounts::";
		logger.debug(CLASSNAME + method + BEGIN);
		boolean tdsFlag = report.equalsIgnoreCase("tdsreport") ? true : false;
		return accountingReportsService.getConsolidatedMonthSummeryForYearMonthwise(clientid, month,year,tdsFlag);
	}

	@GetMapping("/gettdscustomdetails/{clientid}/{fromdate}/{todate}")
	public String getTdsCustomReportData(@PathVariable String clientid, @PathVariable String fromdate, @PathVariable String todate,@RequestParam String report, HttpServletRequest request) throws JsonProcessingException {
		final String method = "getTdsCustomReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "todate\t" + todate);
		logger.debug(CLASSNAME + method + "fromdate\t" + fromdate);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		boolean tdsFlag = report.equalsIgnoreCase("tdsreport") ? true : false;
		Map<String, Object> invoicesMap = accountingReportsService.getCustomDeductionOrCollection(clientid, fromdate, todate, tdsFlag, start, length, searchVal);
		
		Page<AccountingJournal> invoices = (Page<AccountingJournal>) invoicesMap.get("invoices");
		if(isNotEmpty(invoices.getContent())) {
			invoices.getContent().stream().forEach(invoice -> {
				if(isNotEmpty(invoice.getCtin())) {
					invoice.setCtin(invoice.getCtin().substring(2, 12));
				}
			});
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer = mapper.writer();
		
		return writer.writeValueAsString(invoicesMap);
	}
	
	@RequestMapping(value = "/getsummarycustomtdsdetails/{clientid}/{fromdate}/{todate}")
	public @ResponseBody Map<String,Map<String,String>> getConsolidateReportsCustomTotalAmounts(@PathVariable String clientid, @PathVariable String fromdate, @PathVariable String todate,@RequestParam String report,HttpServletRequest request){
		final String method = "getConsolidateReportsTotalAmounts::";
		logger.debug(CLASSNAME + method + BEGIN);
		boolean tdsFlag = report.equalsIgnoreCase("tdsreport") ? true : false;
		return accountingReportsService.getConsolidatedMonthSummeryForCustomMonthwise(clientid, fromdate,todate,tdsFlag);
	}
	
	@GetMapping("/getbills/{clientid}/{type}/{yearcode}")
	public String getTdsReportData(@PathVariable String clientid, @PathVariable String type,@PathVariable String yearcode,  HttpServletRequest request) throws JsonProcessingException {
		final String method = "getTdsReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		
		boolean isPayment = type.equalsIgnoreCase("Payment") ? true : false;
		
		Map<String, Object> invoicesMap = accountingReportsService.getAllInvoicePayments(clientid,yearcode, isPayment, start, length, searchVal);
		
		//Page<InvoicePayments> invoices = (Page<InvoicePayments>) invoicesMap.get("invoices");
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer = mapper.writer();
		
		return writer.writeValueAsString(invoicesMap);
	}
	
	@GetMapping("/getstockagingDatatCustom/{clientid}/{type}/{fromtime}/{totime}")
	public String getStockAgingReportData(@PathVariable String clientid, @PathVariable String type, @PathVariable("fromtime")String fromtime, @PathVariable("totime")String totime,  HttpServletRequest request) throws JsonProcessingException {
		final String method = "getTdsReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = "dueDate";
		String sortOrder = "asc";
		sortOrder = request.getParameter("order[0][dir]");
		boolean isPayment = type.equalsIgnoreCase("Payment") ? true : false;
		Map<String, Object> invoicesMap = accountingReportsService.getStockAgingReportCustomData(clientid,fromtime,totime, isPayment,sortParam, sortOrder,start, length, searchVal);
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer = mapper.writer();
		
		return writer.writeValueAsString(invoicesMap);
	}
	@GetMapping("/getstockagingDatat/{clientid}/{type}/{month}/{year}")
	public String getStockAgingReportData(@PathVariable String clientid, @PathVariable String type,@PathVariable int month,@PathVariable int year,  HttpServletRequest request) throws JsonProcessingException {
		final String method = "getTdsReportData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = "dueDate";
		String sortOrder = "asc";
		sortOrder = request.getParameter("order[0][dir]");
		boolean isPayment = type.equalsIgnoreCase("Payment") ? true : false;
		Map<String, Object> invoicesMap = accountingReportsService.getStockAgingReportData(clientid,month,year, isPayment,sortParam,sortOrder, start, length, searchVal);
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer = mapper.writer();
		
		return writer.writeValueAsString(invoicesMap);
	}
}
