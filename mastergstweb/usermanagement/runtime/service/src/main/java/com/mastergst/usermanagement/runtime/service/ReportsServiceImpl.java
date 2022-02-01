package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.ANX1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1A;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2A;
import static com.mastergst.core.common.MasterGSTConstants.GSTR4;
import static com.mastergst.core.common.MasterGSTConstants.GSTR5;
import static com.mastergst.core.common.MasterGSTConstants.GSTR6;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.dao.ExpensesDao;
import com.mastergst.usermanagement.runtime.dao.GSTR2ADao;
import com.mastergst.usermanagement.runtime.dao.Gstr1Dao;
import com.mastergst.usermanagement.runtime.dao.Gstr1SortDao;
import com.mastergst.usermanagement.runtime.dao.Gstr2Dao;
import com.mastergst.usermanagement.runtime.dao.Gstr2bDao;
import com.mastergst.usermanagement.runtime.dao.PurchageRegisterDao;
import com.mastergst.usermanagement.runtime.dao.PurchageRegisterSortDao;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR4;
import com.mastergst.usermanagement.runtime.domain.GSTR5;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;
import com.mastergst.usermanagement.runtime.support.Utility;

@Service
@Transactional(readOnly = true)
public class ReportsServiceImpl implements ReportsService {
	
	private static final Logger logger = LogManager.getLogger(ReportsServiceImpl.class.getName());
	private static final String CLASSNAME = "ClientServiceImpl::";
	private static SimpleDateFormat dateFormatOnlyDate = new SimpleDateFormat("dd/MM/yyyy");
	private static String DOUBLE_FORMAT  = "%.2f";
	private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
	
	@Autowired
	private ClientService clientService;
	@Autowired
	private PurchageRegisterDao purchageRegisterDao;
	@Autowired
	private Gstr1Dao gstr1Dao;
	@Autowired
	private PurchageRegisterSortDao purchageRegisterSortDao;
	@Autowired
	private Gstr1SortDao gstr1SortDao;
	@Autowired
	private GSTR2ADao gstr2ADao;
	@Autowired
	private Gstr2Dao gstr2Dao;
	@Autowired
	private GSTR1Repository gstr1Repository;
	@Autowired
	private PurchaseRegisterRepository purchaseRepository;
	@Autowired
	private Gstr2bDao gstr2bDao;
	
	@Autowired
	private ExpensesDao expensesDao;
	
	@Override
	public Map<String, Map<String, String>> getConsolidatedSummeryForYearGlobalReports(List<String> clientids,
			String returntype, String yearCode, boolean checkQuarterly,String dwnldFromGSTN,InvoiceFilter filter) {
		// TODO Auto-generated method stub

		List<TotalInvoiceAmount> gstrInvoiceAmounts = null;
		if(GSTR1.equalsIgnoreCase(returntype)) {
			gstrInvoiceAmounts = gstr1Dao.getConsolidatedSummeryForGlobalYearMonthwise(clientids, yearCode, checkQuarterly,dwnldFromGSTN,"",filter);			
		}else if(GSTR2A.equalsIgnoreCase(returntype)){
			List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,
					MasterGSTConstants.ISD, MasterGSTConstants.B2BA, MasterGSTConstants.CDNA);
			gstrInvoiceAmounts = gstr2Dao.getConsolidatedMultimonthSummeryForYearMonthwiseClientidIn(clientids, invTypes, yearCode,filter);
		}else if(returntype.equals(MasterGSTConstants.EINVOICE)) {
			gstrInvoiceAmounts = gstr1Dao.getConsolidatedSummeryForGlobalYearMonthwise(clientids, yearCode, checkQuarterly,dwnldFromGSTN,"gstrOrEinvoice",filter);	
		}else {
			gstrInvoiceAmounts = purchageRegisterDao.getConsolidatedGlobalReportsSummeryForYear(clientids, yearCode, checkQuarterly,filter);
		}
		
		Map<String, TotalInvoiceAmount> summerySlsData = new HashMap<String, TotalInvoiceAmount>();
		for(TotalInvoiceAmount gstr1InvoiceAmount : gstrInvoiceAmounts){
			String code = gstr1InvoiceAmount.get_id();
			summerySlsData.put(code, gstr1InvoiceAmount);
		}
		List<TotalInvoiceAmount> invs = new ArrayList<>();
		Map<String, Map<String, String>> summeryReturnData = new HashMap<String, Map<String, String>>();		
		int ct = 12;
		if(checkQuarterly){
			ct = 4;
		}
		for(int i=1; i<=ct; i++){
			String cd = Integer.toString(i);
			int totalTransactions = 0;
			Double totalAmount = 0d, itcAmount =0d;
			Double salesAmt = 0d, taxAmt = 0d, salesTax = 0d, igst = 0d, cgst = 0d, sgst = 0d , cess = 0d, exempted = 0d,tcs = 0d,ptcs = 0d,tds = 0d;
			TotalInvoiceAmount invoiceAmountSls = summerySlsData.get(cd);
			Map<String, String> reportMap = new HashMap<String, String>();
			summeryReturnData.put(cd, reportMap);
			if(invoiceAmountSls != null){
				totalTransactions = invoiceAmountSls.getTotalTransactions();
				salesAmt = invoiceAmountSls.getTotalTaxableAmount().doubleValue();
				taxAmt = invoiceAmountSls.getTotalTaxAmount().doubleValue();
				salesTax = invoiceAmountSls.getTotalTaxAmount().doubleValue();
				igst = invoiceAmountSls.getTotalIGSTAmount().doubleValue();
				cgst = invoiceAmountSls.getTotalCGSTAmount().doubleValue();
				sgst = invoiceAmountSls.getTotalSGSTAmount().doubleValue();
				
				if(GSTR1.equalsIgnoreCase(returntype)) {
					exempted = invoiceAmountSls.getTotalExemptedAmount().doubleValue();					
					tcs = invoiceAmountSls.getTcsTdsAmount().doubleValue();
				}else {
					itcAmount= invoiceAmountSls.getTotalITCAvailable().doubleValue();
					ptcs = invoiceAmountSls.getTcsTdsAmount().doubleValue();
					tds = invoiceAmountSls.getTdsAmount().doubleValue();
				}
				
				cess=invoiceAmountSls.getTotalCESSAmount().doubleValue();
				//totalAmount = salesAmt + igst+ cgst + sgst;
				totalAmount = invoiceAmountSls.getTotalAmount().doubleValue();
			}
			reportMap.put("Sales", decimalFormat.format(salesAmt));
			reportMap.put("Tax", decimalFormat.format(taxAmt));
			reportMap.put("SalesTax", decimalFormat.format(salesTax));
			reportMap.put("igst", decimalFormat.format(igst));
			reportMap.put("cgst", decimalFormat.format(cgst));
			reportMap.put("sgst", decimalFormat.format(sgst));
			reportMap.put("cess", decimalFormat.format(cess));
			reportMap.put("exempted", decimalFormat.format(exempted));
			reportMap.put("tcsamount", decimalFormat.format(tcs));
			reportMap.put("tdsamount", decimalFormat.format(tds));
			reportMap.put("ptcsamount", decimalFormat.format(ptcs));
			reportMap.put("totalTransactions", String.valueOf(totalTransactions));
			reportMap.put("totalamt", decimalFormat.format(totalAmount));
			reportMap.put("itc", decimalFormat.format(itcAmount));
		}
		return summeryReturnData;
	}

	
	@Override
	public Map<String, Map<String, String>> getConsolidatedSummeryForGlobalCustomReports(List<String> clientids, String returntype, String fromtime, String totime,String dwnldFromGSTN,InvoiceFilter filter) {
		logger.debug(CLASSNAME + "getConsolidatedSummeryForGlobalCustomReports : Begin");
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1,
				Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23,
					59, 59);
		endDate = new java.util.Date(cal.getTimeInMillis());
		
		List<TotalInvoiceAmount> gstrInvoiceAmounts = null;
		if(GSTR1.equalsIgnoreCase(returntype)) {
			gstrInvoiceAmounts = gstr1Dao.getConsolidatedSummeryForGlobalCustom(clientids, returntype, stDate, endDate,dwnldFromGSTN,"",filter);
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.EINVOICE)) {
			gstrInvoiceAmounts = gstr1Dao.getConsolidatedSummeryForGlobalCustom(clientids, returntype, stDate, endDate,dwnldFromGSTN,"gstrOrEinvoice",filter);
		}else {
			gstrInvoiceAmounts = purchageRegisterDao.getConsolidatedSummeryForGlobalCustom(clientids, returntype, stDate, endDate,filter);
		}
		Map<String, TotalInvoiceAmount> summerySlsData = new HashMap<String, TotalInvoiceAmount>();
		for(TotalInvoiceAmount gstr1InvoiceAmount : gstrInvoiceAmounts){
			String code = gstr1InvoiceAmount.get_id();
			summerySlsData.put(code, gstr1InvoiceAmount);
		}
		List<TotalInvoiceAmount> invs = new ArrayList<>();
		Map<String, Map<String, String>> summeryReturnData = new HashMap<String, Map<String, String>>();		
		
		
		for(int i=1; i<=12; i++){
			String cd = Integer.toString(i);
			int totalTransactions = 0;
			Double totalAmount = 0d, itcAmount =0d;
			Double salesAmt = 0d, taxAmt = 0d, salesTax = 0d, igst = 0d, cgst = 0d, sgst = 0d , cess = 0d, exempted = 0d,tcs = 0d,ptcs = 0d,tds = 0d;
			TotalInvoiceAmount invoiceAmountSls = summerySlsData.get(cd);
			Map<String, String> reportMap = new HashMap<String, String>();
			summeryReturnData.put(cd, reportMap);
			if(invoiceAmountSls != null){
				totalTransactions = invoiceAmountSls.getTotalTransactions();
				salesAmt = invoiceAmountSls.getTotalTaxableAmount().doubleValue();
				taxAmt = invoiceAmountSls.getTotalTaxAmount().doubleValue();
				salesTax = invoiceAmountSls.getTotalTaxAmount().doubleValue();
				igst = invoiceAmountSls.getTotalIGSTAmount().doubleValue();
				cgst = invoiceAmountSls.getTotalCGSTAmount().doubleValue();
				sgst = invoiceAmountSls.getTotalSGSTAmount().doubleValue();
				
				if(GSTR1.equalsIgnoreCase(returntype)) {
					exempted = invoiceAmountSls.getTotalExemptedAmount().doubleValue();					
					tcs = invoiceAmountSls.getTcsTdsAmount().doubleValue();
				}else {
					itcAmount= invoiceAmountSls.getTotalITCAvailable().doubleValue();
					ptcs = invoiceAmountSls.getTcsTdsAmount().doubleValue();
					tds = invoiceAmountSls.getTdsAmount().doubleValue();
				}
				
				cess=invoiceAmountSls.getTotalCESSAmount().doubleValue();
				//totalAmount = salesAmt + igst+ cgst + sgst;
				totalAmount = invoiceAmountSls.getTotalAmount().doubleValue();
			}
			
			reportMap.put("Sales", decimalFormat.format(salesAmt));
			reportMap.put("Tax", decimalFormat.format(taxAmt));
			reportMap.put("SalesTax", decimalFormat.format(salesTax));
			reportMap.put("igst", decimalFormat.format(igst));
			reportMap.put("cgst", decimalFormat.format(cgst));
			reportMap.put("sgst", decimalFormat.format(sgst));
			reportMap.put("cess", decimalFormat.format(cess));
			reportMap.put("exempted", decimalFormat.format(exempted));
			reportMap.put("tcsamount", decimalFormat.format(tcs));
			reportMap.put("ptcsamount", decimalFormat.format(ptcs));
			reportMap.put("tdsamount", decimalFormat.format(tds));
			reportMap.put("totalTransactions", String.valueOf(totalTransactions));
			reportMap.put("totalamt", decimalFormat.format(totalAmount));
			reportMap.put("itc", decimalFormat.format(itcAmount));
		}
		return summeryReturnData;
	}
	
	@Override
	public Map getGlobalReportInvoices(Pageable pageable, List<String> clientids, String id, String retType,
			String reports, int month, int year, int start, int length, String searchVal, InvoiceFilter filter,boolean b,String dwnldFromGSTN) {
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		Page<? extends InvoiceParent> invoices = null;
		TotalInvoiceAmount totalInvoiceAmount = null;
		if (isNotEmpty(retType)) {
			String yearCode = Utility.getYearCode(month, year);
			if (retType.equals(GSTR1)) {
					invoices = gstr1Dao.findByClientidInAndMonthAndYear(clientids, month, yearCode, start, length, searchVal, filter,dwnldFromGSTN);
					totalInvoiceAmount = gstr1Dao.getTotalInvoicesAmountsForMonth(clientids, month, yearCode, searchVal, filter,dwnldFromGSTN,"");
			
			} else if (retType.equals(GSTR2)) {
				invoices = purchageRegisterDao.findByClientidInAndMonthAndYear(clientids, month, yearCode, start, length, searchVal, filter);
				totalInvoiceAmount = purchageRegisterDao.getTotalInvoicesAmountsForMonth(clientids, month, yearCode, searchVal, filter);
			}else if(retType.equals(GSTR2A)) {
				String strMonth =  month<10 ? "0"+month : month+"";
				String retPeriod = strMonth+year;
				List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.ISD, 
						MasterGSTConstants.B2BA, MasterGSTConstants.CDNA);
				invoices = gstr2ADao.findByClientidInAndMonthAndYear(clientids,invTypes,retPeriod,month,yearCode,start,length,searchVal,filter);
				totalInvoiceAmount = gstr2ADao.getTotalInvoicesAmountsForMonthClientIdin(clientids, invTypes,retPeriod,month,yearCode, searchVal, filter);
			}else if (retType.equals(MasterGSTConstants.EINVOICE)) {
				invoices = gstr1Dao.findByClientidInAndMonthAndYear(clientids, month, yearCode, start, length, searchVal, filter,dwnldFromGSTN);
				totalInvoiceAmount = gstr1Dao.getTotalInvoicesAmountsForMonth(clientids, month, yearCode, searchVal, filter,dwnldFromGSTN,"gstrOrEinvoice");
			}
			invoicesMap.put("invoices",invoices);
			invoicesMap.put("invoicesAmount",totalInvoiceAmount);
		}
		return invoicesMap;
	}
	
	@Transactional(readOnly=true)
	public Map<String, Object> getGlobalReportsCustomInvoices(Pageable pageable,List<String> clientids,String id,String retType,String type,String fromtime,String totime,int start,int length,String searchVal, InvoiceFilter filter, boolean flag,String dwnldFromGSTN) {
		logger.debug(CLASSNAME + "getGlobalReportsCustomInvoices : Begin");
		
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		Page<? extends InvoiceParent> invoices = null;
		List<TotalInvoiceAmount> totalInvoiceAmount = null;
		TotalInvoiceAmount totInvAmt = null;
		int totTransactions =0;
		double totTaxable = 0d,totVal = 0d,totTax = 0d,totIgst = 0d,totCgst = 0d,totSgst = 0d,totcess = 0d,totItc = 0d,totTcs = 0d,totTds = 0d,totExempted=0d;
		if (isNotEmpty(retType)) {
			Date stDate = null;
			Date endDate = null;
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23,59, 59);
			endDate = new java.util.Date(cal.getTimeInMillis());
			
			if("GSTR1".equalsIgnoreCase(retType)) {
				invoices = gstr1Dao.findByClientidInAndFromtimeAndTotime(clientids, stDate, endDate, start, length, searchVal, filter,dwnldFromGSTN);
				totalInvoiceAmount =gstr1Dao.getTotalInvoicesAmountsForCustom(clientids, stDate, endDate, searchVal, filter,dwnldFromGSTN,"");	
				/*  for(TotalInvoiceAmount invAmts : totalInvoiceAmount) {
					totInvAmt = invAmts;
					totTransactions += invAmts.getTotalTransactions();
					//totTaxable += invAmts.getTotalTaxableAmount();
					//totVal += invAmts.getTotalAmount();
					//totTax += invAmts.getTotalTaxAmount();
					
				} */
			}else if(retType.equalsIgnoreCase(MasterGSTConstants.EINVOICE)){
				invoices = gstr1Dao.findByClientidInAndFromtimeAndTotime(clientids, stDate, endDate, start, length, searchVal, filter,dwnldFromGSTN);
				totalInvoiceAmount =gstr1Dao.getTotalInvoicesAmountsForCustom(clientids, stDate, endDate, searchVal, filter,dwnldFromGSTN,"gstrOrEinvoice");	
			}else {
				invoices = purchageRegisterDao.findByClientidInAndFromtimeAndTotime(clientids, stDate, endDate, start, length, searchVal, filter);
				totalInvoiceAmount =purchageRegisterDao.getTotalInvoicesAmountsForCustom(clientids, stDate, endDate, searchVal, filter);				
			}
		}
		
		invoicesMap.put("invoices",invoices);
		invoicesMap.put("invoicesAmount",totalInvoiceAmount);
		return invoicesMap;
	}
	
	
	@Override
	public Map getGlobalReportInvoices(Pageable pageable, List<String> clientids, String id, String retType,
			String reports, int month, int year, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter,boolean b,String dwnldFromGSTN) {
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		Page<? extends InvoiceParent> invoices = null;
		TotalInvoiceAmount totalInvoiceAmount = null;
		if (isNotEmpty(retType)) {
			String yearCode = Utility.getYearCode(month, year);
			if (retType.equals(GSTR1)) {
					invoices = gstr1SortDao.findByClientidInAndMonthAndYear(clientids, month, yearCode, start, length, searchVal,fieldName,order, filter,dwnldFromGSTN);
					totalInvoiceAmount = gstr1Dao.getTotalInvoicesAmountsForMonth(clientids, month, yearCode, searchVal, filter,dwnldFromGSTN,"");
			
			} else if (retType.equals(GSTR2)) {
				invoices = purchageRegisterSortDao.findByClientidInAndMonthAndYear(clientids, month, yearCode, start, length,searchVal,fieldName,order,  filter);
				totalInvoiceAmount = purchageRegisterDao.getTotalInvoicesAmountsForMonth(clientids, month, yearCode, searchVal, filter);
			}else if(retType.equals(GSTR2A)) {
				String strMonth =  month<10 ? "0"+month : month+"";
				String retPeriod = strMonth+year;
				List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.ISD, 
						MasterGSTConstants.B2BA, MasterGSTConstants.CDNA, MasterGSTConstants.IMP_GOODS);
				invoices = gstr2ADao.findByClientidInAndMonthAndYear(clientids, invTypes, retPeriod, month, yearCode, start, length, searchVal, fieldName, order, filter);
				totalInvoiceAmount = gstr2ADao.getTotalInvoicesAmountsForMonthClientIdin(clientids, invTypes, retPeriod, month, yearCode, searchVal, filter);
			}else if(retType.equals(MasterGSTConstants.GSTR2B)) {
				List<String> fps = null;
				if(month > 0) {
					String strMonth = month < 10 ? "0" + month : month + "";
					String fp = strMonth + year;
					fps = Arrays.asList(fp);
				}else {
					fps = Arrays.asList("04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), "03"+(year+1));
				}
				invoices = gstr2bDao.findByClientidInAndMonthAndYear(clientids, fps, start,length, searchVal, fieldName, order, filter);
				totalInvoiceAmount = gstr2bDao.getTotalInvoicesAmountsForMonthClientIdin(clientids, fps, searchVal, filter);
			}else if (retType.equals(MasterGSTConstants.EINVOICE)) {
				invoices = gstr1SortDao.findByClientidInAndMonthAndYear(clientids, month, yearCode, start, length, searchVal,fieldName,order, filter,dwnldFromGSTN);
				totalInvoiceAmount = gstr1Dao.getTotalInvoicesAmountsForMonth(clientids, month, yearCode, searchVal, filter,dwnldFromGSTN,"gstrOrEinvoice");
			}
			invoicesMap.put("invoices",invoices);
			invoicesMap.put("invoicesAmount",totalInvoiceAmount);
		}
		return invoicesMap;
	}
	
	@Transactional(readOnly=true)
	public Map<String, Object> getGlobalReportsCustomInvoices(Pageable pageable,List<String> clientids,String id,String retType,String type,String fromtime,String totime,int start,int length,String searchVal,String fieldName, String order, InvoiceFilter filter, boolean flag,String dwnldFromGSTN) {
		logger.debug(CLASSNAME + "getGlobalReportsCustomInvoices : Begin");
		
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		Page<? extends InvoiceParent> invoices = null;
		List<TotalInvoiceAmount> totalInvoiceAmount = null;
		TotalInvoiceAmount totInvAmt = null;
		int totTransactions =0;
		double totTaxable = 0d,totVal = 0d,totTax = 0d,totIgst = 0d,totCgst = 0d,totSgst = 0d,totcess = 0d,totItc = 0d,totTcs = 0d,totTds = 0d,totExempted=0d;
		if (isNotEmpty(retType)) {
			Date stDate = null;
			Date endDate = null;
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23,59, 59);
			endDate = new java.util.Date(cal.getTimeInMillis());
			
			if("GSTR1".equalsIgnoreCase(retType)) {
				invoices = gstr1SortDao.findByClientidInAndFromtimeAndTotime(clientids, stDate, endDate, start, length, searchVal,fieldName,order, filter,dwnldFromGSTN);
				totalInvoiceAmount =gstr1Dao.getTotalInvoicesAmountsForCustom(clientids, stDate, endDate, searchVal, filter,dwnldFromGSTN,"");	
			}else if(retType.equalsIgnoreCase(MasterGSTConstants.EINVOICE)){
				invoices = gstr1SortDao.findByClientidInAndFromtimeAndTotime(clientids, stDate, endDate, start, length, searchVal,fieldName,order, filter,dwnldFromGSTN);
				totalInvoiceAmount =gstr1Dao.getTotalInvoicesAmountsForCustom(clientids, stDate, endDate, searchVal, filter,dwnldFromGSTN,"gstrOrEinvoice");	
			}else {
				invoices = purchageRegisterSortDao.findByClientidInAndFromtimeAndTotime(clientids, stDate, endDate, start, length, searchVal,fieldName,order, filter);
				totalInvoiceAmount =purchageRegisterDao.getTotalInvoicesAmountsForCustom(clientids, stDate, endDate, searchVal, filter);				
			}
		}
		
		invoicesMap.put("invoices",invoices);
		invoicesMap.put("invoicesAmount",totalInvoiceAmount);
		return invoicesMap;
	}
	
	private Date[] caliculateStEndDates(final Client client, final String returnType, final String reports,int month, int year){
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		if(returnType.equals("Unclaimed")){
			if(year <= 2018) {
				cal.set(2017, 6, 0, 0, 0, 0);
			} else {
				if(month > 3) {
					cal.set(year, 3, 0, 0, 0, 0);
				} else {
					cal.set(year-1, 3, 0, 0, 0, 0);
				}	
			}
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(year, month, 0, 0, 0, 0);
			endDate = new java.util.Date(cal.getTimeInMillis());
		}else{
			if(isNotEmpty(client.getFilingOption()) && client.getFilingOption().equals(MasterGSTConstants.FILING_OPTION_YEARLY)) {
				if(month == 1 || month == 2 || month == 3) {
					cal.set(year-1, 3, 0, 0, 0, 0);
				} else {
					cal.set(year, 3, 0, 0, 0, 0);
				}
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				if(month == 1 || month == 2 || month == 3) {
					cal.set(year, 3, 0, 0, 0, 0);
				} else {
					cal.set(year+1, 3, 0, 0, 0, 0);
				}
				endDate = new java.util.Date(cal.getTimeInMillis());
			} else if((!returnType.equals(MasterGSTConstants.GSTR2) && !returnType.equals(MasterGSTConstants.PURCHASE_REGISTER)) && isNotEmpty(client.getFilingOption()) && client.getFilingOption().equals(MasterGSTConstants.FILING_OPTION_QUARTERLY)) {
				if("reports".equals(reports)) {
					cal.set(year, month - 1, 0, 0, 0, 0);
					stDate = new java.util.Date(cal.getTimeInMillis());
					cal = Calendar.getInstance();
					cal.set(year, month, 0, 0, 0, 0);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}else {
					if(month == 1 || month == 2 || month == 3) {
						cal.set(year, 0, 0, 0, 0, 0);
					} else if(month == 4 || month == 5 || month == 6) {
						cal.set(year, 3, 0, 0, 0, 0);
					} else if(month == 7 || month == 8 || month == 9) {
						cal.set(year, 6, 0, 0, 0, 0);
					} else if(month == 10 || month == 11 || month == 12) {
						cal.set(year, 9, 0, 0, 0, 0);
					}
					stDate = new java.util.Date(cal.getTimeInMillis());
					cal = Calendar.getInstance();
					if(month == 1 || month == 2 || month == 3) {
						cal.set(year, 3, 0, 0, 0, 0);
					} else if(month == 4 || month == 5 || month == 6) {
						cal.set(year, 6, 0, 0, 0, 0);
					} else if(month == 7 || month == 8 || month == 9) {
						cal.set(year, 9, 0, 0, 0, 0);
					} else if(month == 10 || month == 11 || month == 12) {
						cal.set(year+1, 0, 0, 0, 0, 0);
					}
					endDate = new java.util.Date(cal.getTimeInMillis());
				}
			} else {
				cal.set(year, month - 1, 0, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(year, month, 0, 0, 0, 0);
				endDate = new java.util.Date(cal.getTimeInMillis());
			}
		}
		return new Date[]{stDate, endDate};
	}
	
	public Map<String, String> getGlobalReportsSummaryMap(Pageable pageable, List<String> clientids, int month, int year,String returntype, String dwnldFromGSTN) throws Exception {
		Map<String, String> reportMap = Maps.newHashMap();
		Double salesAmt = 0d, totalInv = 0d, totalAmt = 0d, purchaseAmt = 0d, taxAmt = 0d, salesTax = 0d, purchaseTax = 0d , igst = 0d, cgst = 0d, sgst = 0d , cess = 0d, itc = 0d, exempted = 0d,tcs = 0d,tds = 0d;
		Page<? extends InvoiceParent> sinvoices=null;
		if(returntype.equals(MasterGSTConstants.GSTR1)) { 
		//sinvoices = clientService.getInvoices(pageable, client, GSTR1, month, year, false);
			sinvoices = getAdminGroupInvoices(pageable, clientids, GSTR1, month, year, false);
		if (isNotEmpty(sinvoices)) {
			reportMap.put("totalInv", decimalFormat.format(sinvoices.getTotalElements()));
			for (InvoiceParent invoice : sinvoices) {
				String gstStatus = "";
				if(isEmpty(invoice.getGstStatus())) {
					gstStatus = "";
				}else {
					gstStatus = invoice.getGstStatus();
				}
				if(!"CANCELLED".equals(gstStatus)) {
					if(isNotEmpty(invoice.getTcstdsAmount())) {
						tcs += invoice.getTcstdsAmount();
					}
					if (isNotEmpty(invoice.getTotaltax())) {
						taxAmt += invoice.getTotaltax();
						salesTax += invoice.getTotaltax();
					}
					List<? extends Item> items = invoice.getItems();
					if (isNotEmpty(invoice.getItems())){
						for(Item item : items){
							if(isNotEmpty(item.getIgstamount())){
								igst += item.getIgstamount();
							}
							if(isNotEmpty(item.getCgstamount())){
								cgst += item.getCgstamount();
							}
							if(isNotEmpty(item.getSgstamount())){
								sgst += item.getSgstamount();
							}
							if(isNotEmpty(item.getExmepted())) {
								exempted += item.getExmepted();
							}
							if(isNotEmpty(item.getCessamount())) {
								cess += item.getCessamount();
							}
						}
					}
					if (isNotEmpty(invoice.getTotaltaxableamount())) {
						if (isNotEmpty(invoice.getInvtype())
								&& invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
							if (isNotEmpty(((GSTR1) invoice).getCdnr()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt())) {
								if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty()) && ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty().equals("C")) {
									salesAmt -= invoice.getTotaltaxableamount();
								} else {
									salesAmt += invoice.getTotaltaxableamount();
								}
							}
						} else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.CDNUR)){
							if (isNotEmpty( invoice.getCdnur()) && isNotEmpty(invoice.getCdnur().get(0).getNtty()) && invoice.getCdnur().get(0).getNtty().equals("C")) {
									salesAmt -= invoice.getTotaltaxableamount();
								} else {
									salesAmt += invoice.getTotaltaxableamount();
								}
						}else {
							salesAmt += invoice.getTotaltaxableamount();
						}
					}
					
					
					if (isNotEmpty(invoice.getTotalamount())) {
						if (isNotEmpty(invoice.getInvtype())
								&& invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
							if (isNotEmpty(((GSTR1) invoice).getCdnr()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt())) {
								if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty()) && ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty().equals("C")) {
									totalAmt -= invoice.getTotalamount();
								} else {
									totalAmt += invoice.getTotalamount();
								}
							}
						} else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.CDNUR)){
							if (isNotEmpty( invoice.getCdnur()) && isNotEmpty(invoice.getCdnur().get(0).getNtty()) && invoice.getCdnur().get(0).getNtty().equals("C")) {
									totalAmt -= invoice.getTotalamount();
								} else {
									totalAmt += invoice.getTotalamount();
								}
						}else {
							totalAmt += invoice.getTotalamount();
						}
					}
					
				}
			}
		}
	
	}else if(returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) { 	
		Page<? extends InvoiceParent> pinvoices = getAdminGroupInvoices(pageable, clientids, PURCHASE_REGISTER, month, year,
				false);
		if (isNotEmpty(pinvoices)) {
			reportMap.put("totalInv", decimalFormat.format(pinvoices.getTotalElements()));
			
			for (InvoiceParent invoice : pinvoices) {
				if(isNotEmpty(invoice.getTcstdsAmount())) {
					tds += invoice.getTcstdsAmount();
				}
				if (isNotEmpty(invoice.getTotalitc())) {
					taxAmt -= invoice.getTotalitc();
					itc += invoice.getTotalitc();
				}
				List<? extends Item> items = invoice.getItems();
				if (isNotEmpty(invoice.getItems())){
					for(Item item : items){
						if(isNotEmpty(item.getIgstamount())){
							igst += item.getIgstamount();
						}
						if(isNotEmpty(item.getCgstamount())){
							cgst += item.getCgstamount();
						}
						if(isNotEmpty(item.getSgstamount())){
							sgst += item.getSgstamount();
						}
						if(isNotEmpty(item.getIgstavltax())){
							purchaseTax += item.getIgstavltax();
						}
						if(isNotEmpty(item.getCgstavltax())){
							purchaseTax += item.getCgstavltax();
						}
						if(isNotEmpty(item.getSgstavltax())){
							purchaseTax += item.getSgstavltax();
						}
					}
				}
				if (isNotEmpty(invoice.getTotaltaxableamount())) {
					if (isNotEmpty(invoice.getInvtype())
							&& invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
						if (isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0).getNt())) {
							if (isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())
									&& invoice.getCdn().get(0).getNt().get(0).getNtty().equals("D")) {
								purchaseAmt -= invoice.getTotaltaxableamount();
							} else {
								purchaseAmt += invoice.getTotaltaxableamount();
							}
						}
					}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.CDNUR)){
						if (isNotEmpty( invoice.getCdnur()) && isNotEmpty(invoice.getCdnur().get(0).getNtty()) && invoice.getCdnur().get(0).getNtty().equals("D")) {
							purchaseAmt -= invoice.getTotaltaxableamount();
						} else {
							purchaseAmt += invoice.getTotaltaxableamount();
						}
				} else {
						purchaseAmt += invoice.getTotaltaxableamount();
					}
				}
				
				if (isNotEmpty(invoice.getTotalamount())) {
					if (isNotEmpty(invoice.getInvtype())
							&& invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
						if (isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0).getNt())) {
							if (isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty()) && invoice.getCdn().get(0).getNt().get(0).getNtty().equals("D")) {
								totalAmt -= invoice.getTotalamount();
							} else {
								totalAmt += invoice.getTotalamount();
							}
						}
					} else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.CDNUR)){
						if (isNotEmpty( invoice.getCdnur()) && isNotEmpty(invoice.getCdnur().get(0).getNtty()) && invoice.getCdnur().get(0).getNtty().equals("D")) {
								totalAmt -= invoice.getTotalamount();
							} else {
								totalAmt += invoice.getTotalamount();
							}
					}else {
						totalAmt += invoice.getTotalamount();
					}
				}	
				
			}
		}else {
			reportMap.put("totalInv", decimalFormat.format(0d));
		}
	
	}
		reportMap.put("Sales", decimalFormat.format(salesAmt));
		reportMap.put("Purchase", decimalFormat.format(purchaseAmt));
		reportMap.put("Balance", decimalFormat.format(salesAmt - purchaseAmt));
		reportMap.put("Tax", decimalFormat.format(taxAmt));
		reportMap.put("itcamount", decimalFormat.format(itc));
		reportMap.put("SalesTax", decimalFormat.format(salesTax));
		reportMap.put("PurchaseTax", decimalFormat.format(purchaseTax));
		reportMap.put("totalAmt", decimalFormat.format(totalAmt));
		reportMap.put("igst", decimalFormat.format(igst));
		reportMap.put("cgst", decimalFormat.format(cgst));
		reportMap.put("sgst", decimalFormat.format(sgst));
		reportMap.put("exempted", decimalFormat.format(exempted));
		reportMap.put("cess", decimalFormat.format(cess));
		reportMap.put("tcsamount", decimalFormat.format(tcs));
		reportMap.put("tdsamount", decimalFormat.format(tds));
		return reportMap;
	}


	private Page<? extends InvoiceParent> getAdminGroupInvoices(Pageable pageable, List<String> clientids, String returnType, int month, int year, boolean checkQuarterly) {
		logger.debug(CLASSNAME + "getInvoices : Begin");
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		if (isNotEmpty(returnType)) {
			Client client =null;
			Date stDate = null;
			Date endDate = null;
			for(String clientid : clientids) {
				client = clientService.findById(clientid);
			}
			Calendar cal = Calendar.getInstance();
			if(isNotEmpty(client.getFilingoptions())) {
				String yr;
				if(month == 1 || month == 2 || month == 3) {
					yr = (year-1)+"-"+(year);
				}else {
					yr = year+"-"+(year+1);
				}
				//String yr=year+"-"+(year+1);
				client.getFilingoptions().forEach(options->{
					if(options.getYear().equalsIgnoreCase(yr)){
						//client.setFilingOption(options.getOption());			
					}
				});
			}
			
			String yearCode = Utility.getYearCode(month, year);
			if (returnType.equals(GSTR1)) {
				String strMonth =  month<10 ? "0"+month : month+"";
				String retPeriod = strMonth+year;
				List<String> invTypes = new ArrayList<String>();
				invTypes.add(MasterGSTConstants.B2B);
				invTypes.add(MasterGSTConstants.B2BA);
				invTypes.add(MasterGSTConstants.CDNA);
				invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
				invTypes.add(MasterGSTConstants.B2CL);
				invTypes.add(MasterGSTConstants.B2CLA);
				invTypes.add(MasterGSTConstants.CDNUR);
				invTypes.add(MasterGSTConstants.CDNURA);
				invTypes.add(MasterGSTConstants.B2C);
				invTypes.add(MasterGSTConstants.B2CSA);
				invTypes.add(MasterGSTConstants.EXPORTS);
				invTypes.add(MasterGSTConstants.EXPA);
				List<GSTR1> gstr1 = gstr1Repository.findByClientidInAndInvtypeInAndDateofinvoiceBetween(clientids, invTypes,stDate, endDate);
				
				List<String> otherinvTypes = new ArrayList<String>();
				otherinvTypes.add(MasterGSTConstants.ADVANCES);
				otherinvTypes.add(MasterGSTConstants.NIL);
				otherinvTypes.add(MasterGSTConstants.ATPAID);
				List<GSTR1> gstr1nilltxpat = gstr1Repository.findByClientidInAndInvtypeInAndFp(clientids, otherinvTypes,retPeriod);
				
				List<GSTR1> allinvoices = Lists.newArrayList();
				allinvoices.addAll(gstr1);
				allinvoices.addAll(gstr1nilltxpat);
				Page<? extends InvoiceParent> invoices = new PageImpl<>(allinvoices);
				return invoices;
			}  else if (returnType.equals(GSTR2)) {
				return purchaseRepository.findByClientidInAndDateofinvoiceBetween(clientids, stDate, endDate, pageable);
			} else if (returnType.equals(PURCHASE_REGISTER)) {
				//return purchaseRepository.findByClientidAndDateofinvoiceBetween(client.getId().toString(), stDate,endDate, pageable);
			} 
		}
		return null;
	}
	@Override
	public  Map<String, Map<String, String>> getConsolidatedSummeryForYearReports(List<String> clientids,String returntype, String yearCd, boolean checkQuarterly,String dwnldFromGSTN){
		
		List<TotalInvoiceAmount> gstrInvoiceAmounts = null;
		if(GSTR1.equalsIgnoreCase(returntype)) {
			gstrInvoiceAmounts = gstr1Dao.getConsolidatedSummeryForYearMonthwise(clientids, yearCd, checkQuarterly,dwnldFromGSTN);			
		}else {
			gstrInvoiceAmounts = purchageRegisterDao.getConsolidatedReportsSummeryForYearMonthwise(clientids, yearCd, checkQuarterly);
		}
		
		Map<String, TotalInvoiceAmount> summerySlsData = new HashMap<String, TotalInvoiceAmount>();
		for(TotalInvoiceAmount gstr1InvoiceAmount : gstrInvoiceAmounts){
			String code = gstr1InvoiceAmount.get_id();
			summerySlsData.put(code, gstr1InvoiceAmount);
		}
		List<TotalInvoiceAmount> invs = new ArrayList<>();
		Map<String, Map<String, String>> summeryReturnData = new HashMap<String, Map<String, String>>();		
		int ct = 12;
		if(checkQuarterly){
			ct = 4;
		}
		for(int i=1; i<=ct; i++){
			String cd = Integer.toString(i);
			int totalTransactions = 0;
			Double totalAmount = 0d, itcAmount =0d;
			Double salesAmt = 0d, taxAmt = 0d, salesTax = 0d, igst = 0d, cgst = 0d, sgst = 0d , cess = 0d, exempted = 0d,tcs = 0d,tds = 0d;
			TotalInvoiceAmount invoiceAmountSls = summerySlsData.get(cd);
			Map<String, String> reportMap = new HashMap<String, String>();
			summeryReturnData.put(cd, reportMap);
			if(invoiceAmountSls != null){
				totalTransactions = invoiceAmountSls.getTotalTransactions();
				salesAmt = invoiceAmountSls.getTotalTaxableAmount().doubleValue();
				taxAmt = invoiceAmountSls.getTotalTaxAmount().doubleValue();
				salesTax = invoiceAmountSls.getTotalTaxAmount().doubleValue();
				igst = invoiceAmountSls.getTotalIGSTAmount().doubleValue();
				cgst = invoiceAmountSls.getTotalCGSTAmount().doubleValue();
				sgst = invoiceAmountSls.getTotalSGSTAmount().doubleValue();
				
				if(GSTR1.equalsIgnoreCase(returntype)) {
					exempted = invoiceAmountSls.getTotalExemptedAmount().doubleValue();					
					tcs = invoiceAmountSls.getTcsTdsAmount().doubleValue();
				}else {
					itcAmount= invoiceAmountSls.getTotalITCAvailable().doubleValue();
					tds = invoiceAmountSls.getTcsTdsAmount().doubleValue();
				}
				
				cess=invoiceAmountSls.getTotalCESSAmount().doubleValue();
				//totalAmount = salesAmt + igst+ cgst + sgst;
				totalAmount = invoiceAmountSls.getTotalAmount().doubleValue();
			}
			reportMap.put("Sales", decimalFormat.format(salesAmt));
			reportMap.put("Tax", decimalFormat.format(taxAmt));
			reportMap.put("SalesTax", decimalFormat.format(salesTax));
			reportMap.put("igst", decimalFormat.format(igst));
			reportMap.put("cgst", decimalFormat.format(cgst));
			reportMap.put("sgst", decimalFormat.format(sgst));
			reportMap.put("cess", decimalFormat.format(cess));
			reportMap.put("exempted", decimalFormat.format(exempted));
			reportMap.put("tcsamount", decimalFormat.format(tcs));
			reportMap.put("tdsamount", decimalFormat.format(tds));
			reportMap.put("totalTransactions", String.valueOf(totalTransactions));
			reportMap.put("totalamt", decimalFormat.format(totalAmount));
			reportMap.put("itc", decimalFormat.format(itcAmount));
		}
		return summeryReturnData;
	}


	@Override
	public Map<String, Object> getGlobalInvoicesSupport(List<String> clientids, String returnType, String string,
			int month, int year,String dwnldFromGSTN) {
		Map<String, Object> supportObj = new HashMap<>();
		if (isNotEmpty(returnType)) {
			String yearCd = Utility.getYearCode(month, year); 
			List<String> billToNames = null;
			List<String> customFields = null;
			if (returnType.equals(GSTR1)) {
				billToNames = gstr1Dao.getBilledToNames(clientids, month, yearCd,dwnldFromGSTN);
				customFields = getGlobalReportsCustomFields(clientids,returnType);
			}else if (returnType.equals(GSTR2)) {
				billToNames = purchageRegisterDao.getBilledToNames(clientids, month, yearCd);
				customFields = getGlobalReportsCustomFields(clientids, returnType);
			}else if(MasterGSTConstants.GSTR2A.equalsIgnoreCase(returnType)) {
				String strMonth = month < 10 ? "0" + month : month + "";
				String fp = strMonth + year;
				List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,
						MasterGSTConstants.ISD, MasterGSTConstants.B2BA, MasterGSTConstants.CDNA);
				billToNames = gstr2Dao.getMultimonthBillToNamesClientIdIn(clientids, invTypes, yearCd,month,fp);
			}else if(MasterGSTConstants.GSTR2B.equalsIgnoreCase(returnType)) {
				
				List<String> fps = null;
				if(month > 0) {
					String strMonth = month < 10 ? "0" + month : month + "";
					String fp = strMonth + year;
					fps = Arrays.asList(fp);
				}else {
					fps = Arrays.asList("04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), "03"+(year+1));
				}
				
				billToNames = gstr2bDao.getMultimonthBillToNamesClientIdIn(clientids, yearCd, month, fps);
			}
			supportObj.put("billToNames", billToNames);
			supportObj.put("customFields", customFields);
		}
		return supportObj;
	}
	
	private List<String> getGlobalReportsCustomFields(List<String> clientids, String returnType) {
		List<String> custList = Lists.newArrayList();
		for(String id : clientids) {
			custList = clientService.getCustomFields(id,returnType);
		}
		return custList;
	}


	@Override
	public Map<String, Object> getGlobalCustomInvoicesSupport(List<String> clientids, String returnType, String string,
			String fromtime, String totime,String dwnldFromGSTN) {
		logger.debug(CLASSNAME + "getGlobalCustomInvoicesSupport : Begin");
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1,
				Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23,
					59, 59);
		endDate = new java.util.Date(cal.getTimeInMillis());
		
		Map<String, Object> supportObj = new HashMap<>();
		if (isNotEmpty(returnType)) {
			List<String> billToNames = null;List<String> customFields = null;
			if (returnType.equals(MasterGSTConstants.GSTR1)) {
				billToNames = gstr1Dao.getGlobalCustomBillToNames(clientids, stDate, endDate,dwnldFromGSTN);
				customFields = getGlobalReportsCustomFields(clientids, returnType);
			}else if (returnType.equals(MasterGSTConstants.GSTR2)) {
				billToNames = purchageRegisterDao.getGlobalCustomBillToNames(clientids, stDate, endDate);
				customFields = getGlobalReportsCustomFields(clientids, returnType);
			} 
			supportObj.put("billToNames", billToNames);
			supportObj.put("customFields", customFields);
		}
		return supportObj;	
	}


	@Override
	public Map<String, Map<String, String>> getConsolidatedSummeryForYearMonthwise(List<String> clientids, String yearCd) {
		List<TotalInvoiceAmount> gstr1InvoiceAmounts = gstr1Dao.getConsolidatedSummeryForGlobalYearMonthwise(clientids, yearCd, false,"","",new InvoiceFilter());
		List<TotalInvoiceAmount> purchageInvoiceAmounts = purchageRegisterDao.getConsolidatedGlobalReportsSummeryForYear(clientids, yearCd, false,new InvoiceFilter());
		List<TotalInvoiceAmount> expensesInvoiceAmounts = expensesDao.getConsolidatedSummeryForYearMonthwise(clientids, yearCd,false);
		Map<String, TotalInvoiceAmount> summerySlsData = new HashMap<String, TotalInvoiceAmount>();
		for(TotalInvoiceAmount gstr1InvoiceAmount : gstr1InvoiceAmounts){
			String code = gstr1InvoiceAmount.get_id();
			summerySlsData.put(code, gstr1InvoiceAmount);
		}
		List<TotalInvoiceAmount> invs = new ArrayList<>();
		for(TotalInvoiceAmount gstr4InvoiceAmount : invs){
			String code = gstr4InvoiceAmount.get_id();
			TotalInvoiceAmount storedData = summerySlsData.get(code);
			if(storedData == null){
				summerySlsData.put(code, gstr4InvoiceAmount);
			}else{
				storedData.setTotalTaxAmount(storedData.getTotalTaxAmount().add(gstr4InvoiceAmount.getTotalTaxAmount()));
				storedData.setTotalIGSTAmount(storedData.getTotalIGSTAmount().add(gstr4InvoiceAmount.getTotalIGSTAmount()));
				storedData.setTotalCGSTAmount(storedData.getTotalCGSTAmount().add(gstr4InvoiceAmount.getTotalCGSTAmount()));
				storedData.setTotalSGSTAmount(storedData.getTotalSGSTAmount().add(gstr4InvoiceAmount.getTotalSGSTAmount()));
				storedData.setTotalTaxableAmount(storedData.getTotalTaxableAmount().add(gstr4InvoiceAmount.getTotalTaxableAmount()));
			}
		}

		Map<String, TotalInvoiceAmount> summeryPchData = new HashMap<String, TotalInvoiceAmount>();
		for(TotalInvoiceAmount purchageInvoiceAmount : purchageInvoiceAmounts){
			String code = purchageInvoiceAmount.get_id();
			summeryPchData.put(code, purchageInvoiceAmount);
		}
		
		Map<String, TotalInvoiceAmount> summeryExpensesData = new HashMap<String, TotalInvoiceAmount>();
		for(TotalInvoiceAmount expenseInvoiceAmount : expensesInvoiceAmounts){
			String code = expenseInvoiceAmount.get_id();
			summeryExpensesData.put(code, expenseInvoiceAmount);
		}
		
		Map<String, Map<String, String>> summeryReturnData = new HashMap<String, Map<String, String>>();
		int ct = 12;
		for(int i=1; i<=ct; i++){
			String cd = Integer.toString(i);
			Map<String, String> reportMap = new HashMap<String, String>();
			summeryReturnData.put(cd, reportMap);
			Double salesAmt = 0d, purchaseAmt = 0d,expenseAmt = 0d, taxAmt = 0d, salesTax = 0d, purchaseTax = 0d , igst = 0d, cgst = 0d, sgst = 0d , cess = 0d, exempted = 0d,tcs = 0d,ptcs = 0d,tds = 0d;
			TotalInvoiceAmount invoiceAmountSls = summerySlsData.get(cd);
			TotalInvoiceAmount invoiceAmountPch = summeryPchData.get(cd);
			TotalInvoiceAmount invoiceAmountExp = summeryExpensesData.get(cd);
			if(invoiceAmountSls != null){
				salesAmt = invoiceAmountSls.getTotalTaxableAmount().doubleValue();
				taxAmt = invoiceAmountSls.getTotalTaxAmount().doubleValue();
				salesTax = invoiceAmountSls.getTotalTaxAmount().doubleValue();
				igst = invoiceAmountSls.getTotalIGSTAmount().doubleValue();
				cgst = invoiceAmountSls.getTotalCGSTAmount().doubleValue();
				sgst = invoiceAmountSls.getTotalSGSTAmount().doubleValue();
				exempted = invoiceAmountSls.getTotalExemptedAmount().doubleValue();
				tcs = invoiceAmountSls.getTcsTdsAmount().doubleValue();
			}
			if(invoiceAmountPch != null){
				purchaseAmt = invoiceAmountPch.getTotalTaxableAmount().doubleValue();
				taxAmt -= invoiceAmountPch.getTotalTaxAmount().doubleValue();
				purchaseTax = invoiceAmountPch.getTotalTaxAmount().doubleValue();
				igst += invoiceAmountPch.getTotalIGSTAmount().doubleValue();
				cgst += invoiceAmountPch.getTotalCGSTAmount().doubleValue();
				sgst += invoiceAmountPch.getTotalSGSTAmount().doubleValue();
				ptcs = invoiceAmountPch.getTcsTdsAmount().doubleValue();
				tds = invoiceAmountPch.getTdsAmount().doubleValue();
			}
			if(invoiceAmountExp != null) {
				expenseAmt = invoiceAmountExp.getTotalAmount().doubleValue();
			}
			reportMap.put("Sales", decimalFormat.format(salesAmt));
			reportMap.put("Purchase", decimalFormat.format(purchaseAmt));
			reportMap.put("Expenses", decimalFormat.format(expenseAmt));
			reportMap.put("Balance", decimalFormat.format(salesAmt - (purchaseAmt+expenseAmt)));
			reportMap.put("Tax", decimalFormat.format(taxAmt));
			reportMap.put("SalesTax", decimalFormat.format(salesTax));
			reportMap.put("PurchaseTax", decimalFormat.format(purchaseTax));
			reportMap.put("igst", decimalFormat.format(igst));
			reportMap.put("cgst", decimalFormat.format(cgst));
			reportMap.put("sgst", decimalFormat.format(sgst));
			reportMap.put("exempted", decimalFormat.format(exempted));
			reportMap.put("tcsamount", decimalFormat.format(tcs));
			reportMap.put("ptcsamount", decimalFormat.format(ptcs));
			reportMap.put("tdsamount", decimalFormat.format(tds));
		}
		return summeryReturnData;

	}

}
