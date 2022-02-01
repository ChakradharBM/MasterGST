package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2A;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.template.SimpleExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.ReconcileTemp;
import com.mastergst.configuration.service.ReconcileTempRepository;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientConfig;
import com.mastergst.usermanagement.runtime.domain.ClientStatus;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CompanyItems;
import com.mastergst.usermanagement.runtime.domain.CompanySuppliers;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.CustomData;
import com.mastergst.usermanagement.runtime.domain.CustomFields;
import com.mastergst.usermanagement.runtime.domain.FilingStatusReportsVO;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.Metering;
import com.mastergst.usermanagement.runtime.domain.MeteringVo;
import com.mastergst.usermanagement.runtime.domain.PrintConfiguration;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.repository.ClientStatusRepository;
import com.mastergst.usermanagement.runtime.repository.CustomFieldsRepository;
import com.mastergst.usermanagement.runtime.response.FinancialSummaryVO;
import com.mastergst.usermanagement.runtime.response.InvoiceVO;
import com.mastergst.usermanagement.runtime.service.ClientReportsUtil;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.InvoiceService;
import com.mastergst.usermanagement.runtime.service.MeteringService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.support.Utility;

@Controller
public class ReportsViewController {
	private static final Logger logger = LogManager.getLogger(ReportsViewController.class.getName());
	private static final String CLASSNAME = "ReportsViewController::";
	@Autowired	private MongoTemplate mongoTemplate;
	@Autowired	private ClientService clientService;
	@Autowired	private ConfigService configService;
	@Autowired	private UserService userService;
	@Autowired	private ProfileService profileService;
	@Autowired	MeteringService meteringService;
	@Autowired	InvoiceService invoiceService;
	@Autowired	private ClientStatusRepository clientStatusRepository;
	@Autowired	private ClientReportsUtil clientReportsUtil;
	@Autowired	CustomFieldsRepository customFieldsRepository;
	private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
	@Autowired	ReconcileTempRepository reconcileTempRepository;
	
	@RequestMapping("/reports/{id}/{name}/{usertype}/{clientid}/{month}/{year}")
	public String reportView(@PathVariable("id") String id, @PathVariable("name") String fullname,
		@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, @PathVariable("month") int month, 
		@PathVariable("year") int year, @RequestParam String type, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "reportView::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		model.addAttribute("user", user);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
				List<String> clntids = companyUser.getCompany();
				if(isNotEmpty(clntids) && clntids.size() > 0){
					if(clntids.contains(clientid)){
						model.addAttribute("companyUser", companyUser);
					}
				}
				if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
					model.addAttribute("addClient", companyUser.getAddclient());
				}else {
					model.addAttribute("addClient", "true");
				}
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", clientService.getGSTReturnsSummary(client, month, year, false));
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		String viewName=null;
		
		switch(type) {
			//Invoices
			case "salesreports": viewName= "sales_report";
				break;
			case "purchasereports": viewName= "purchase_report";
				break;
			case "ewaybillreports": viewName= "ewaybillreport";
				break;
			case "einvoicereports": viewName= "einvoicereport";
			break;
			
			//Multi Month
			case "multimonthgstr1Reports": viewName= "multi_month_summaryGSTR1";
				break;
			case "multiMonthreports": 
						int[] monthArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
						Map<String, Map<String, String>> summaryMap = Maps.newHashMap();
						for (int i : monthArray) {
							Map<String, String> reportMap = null;
							if (i < 4) {
								reportMap = getMultimonthGSTR2ASummaryMap(client, i, year + 1,"GSTR2");
								summaryMap.put(i + "", reportMap);
							} else {
								reportMap = getMultimonthGSTR2ASummaryMap(client, i, year,"GSTR2");
								summaryMap.put(i + "", reportMap);
							}
						}
						model.addAttribute("summaryMap", summaryMap);	
						viewName= "multi_month_summary";
				break;
			case "multimoth3bReports": viewName= "multi_month_summaryGSTR3B";
				break;
			case "multimoth2bReports": viewName= "multi_month_summaryGSTR2B";
				break;
			//Summary Report
			case "monthlyTaxandItcSummary": model.addAttribute("usr", user);
					viewName = "monthlyTaxandItcSummary";
				break;
			case "filingStatusReports": viewName = "filingStatus_report";
				break;
			case "suppliercompliance": 	
					List<CompanySuppliers> cmpySupplierLst = profileService.getSuppliers(clientid);
					if(isNotEmpty(cmpySupplierLst)) {	
						//List<CompanySuppliers> gstnumberSuplliers = new ArrayList<CompanySuppliers>();
						//List<CompanySuppliers> noGstnumberSuplliers = new ArrayList<CompanySuppliers>();
						/*cmpySupplierLst.forEach(supplier->{
							if(NullUtil.isNotEmpty(supplier.getGstnnumber())) {
								if(isNotEmpty(supplier.getState())) {
									if(!supplier.getState().equalsIgnoreCase("97-Other Territory")) {
										noGstnumberSuplliers.add(supplier);
									}
								}else {
									noGstnumberSuplliers.add(supplier);									
								}
							}
						});*/
						//model.addAttribute("suppliers", gstnumberSuplliers);
						//model.addAttribute("noGstnumberSuplliers", noGstnumberSuplliers);
					}else {
						model.addAttribute("supplierss", "suppliers_notfound");
					}
					viewName = "suppliercomplianceReport";
				break;
			case "itcreports": viewName = "ITC_Report";
				break;
			case "itc_unclaimedreports": viewName = "ITC_UnclaimedReport";
				break;
			
			//HSN and Tax Slab Wise Report
			case "hsnsalesreports": viewName = "HSNsales_report";
				break;
			case "hsnpurchasereports": viewName = "HSNpurchase_report";
				break;
			case "taxslabwisesalesreports": viewName = "taxslabwisesales_report";
				break;
			case "taxslabwisepurchasesreports": viewName = "taxslabwisepurchase_report";
				break;
			
			//Ledger
			case "cash": viewName = "ledger";
				break;
			case "credit": viewName = "ledger";
				break;
			case "tax": viewName = "ledger";
				break;
				
			//Accounting Reports
			case "trailbalancereports": viewName = "trialbalance_report";
				break;	
			case "pandlreport": viewName = "pandL_report";
				break;
			case "balancesheet": viewName = "balancesheet_report";
				break;
			case "tdsreport": viewName = "tds_report";
				break;
			case "tcsreport": viewName = "tcs_report";
				break;
			case "agingreport_payment": viewName = "aging_report";
				model.addAttribute("type", "payment");
				break;
			case "agingreport_receipt": viewName = "aging_report";
				model.addAttribute("type", "receipt");
				break;
			case "ledgerreports": viewName = "ledger_report";
					List<ProfileLedger> profileLedgers=profileService.getLedgerDetails(clientid);
					Collections.sort(profileLedgers, new Comparator<ProfileLedger>() {
						@Override
						public int compare(ProfileLedger obj1, ProfileLedger obj2) {
							return obj1.getLedgerName().compareTo(obj2.getLedgerName());
						}
					});
					model.addAttribute("ledgers", profileLedgers);
				break;
			case "yearlyRecocileReport": 
					ClientConfig clientConfig = clientService.getClientConfig(clientid);
					model.addAttribute("clientConfig", clientConfig);
					model.addAttribute("client", client);
					model.addAttribute("yearlyreconcilem", "yearly");
					viewName = "yearlyRecocileReport";
					ReconcileTemp temp =  reconcileTempRepository.findByClientid(clientid);
					if(isNotEmpty(temp)) {
						Long invs = 0l;
						Long totalinvoices = 0l;
						Long totalgstr2ainvoices = 1l;
						if(isNotEmpty(temp.getProcessedinvoices())) {
							invs += temp.getProcessedinvoices();
						}
							if(isNotEmpty(temp.getTotalinvoices())) {
								totalinvoices += temp.getTotalinvoices();
							}
							if(isNotEmpty(temp.getProcessedgstr2ainvoices())) {
								totalgstr2ainvoices += temp.getProcessedgstr2ainvoices();
							}
						
						model.addAttribute("reconcileCounts", totalgstr2ainvoices);
					}
				break;
			case "yearlyGstr2bRecocileReport": 
				ClientConfig clientConfigg = clientService.getClientConfig(clientid);
				model.addAttribute("clientConfig", clientConfigg);
				model.addAttribute("client", client);
				model.addAttribute("yearlyreconcilem", "yearly");
				ReconcileTemp tempp =  reconcileTempRepository.findByClientid(clientid);
				if(isNotEmpty(tempp)) {
					Long invs = 0l;
					Long totalinvoices = 0l;
					Long totalgstr2ainvoices = 1l;
					if(isNotEmpty(tempp.getProcessedinvoices())) {
						invs += tempp.getProcessedinvoices();
					}
					if(isNotEmpty(tempp.getTotalinvoices())) {
						totalinvoices += tempp.getTotalinvoices();
					}
					if(isNotEmpty(tempp.getProcessedgstr2ainvoices())) {
						totalgstr2ainvoices += tempp.getProcessedgstr2ainvoices();
					}
					model.addAttribute("reconcileCounts", totalgstr2ainvoices);
				}
				viewName = "yearlyGstr2bRecocileReport";
				break;
			case "multiMonthreports3BVS1": viewName = "multi_month_summary3BVS1";
				break;
			case "multiMonthreports3BVS2A": viewName = "multi_month_summary3BVS2A";
				break;
			case "multiMonthreports2AVS2_invoice": viewName = "multi_month_summary2AVS2_invoice";
				break;
			case "multiMonthreports2AVS2": viewName = "multi_month_summary2AVS2";
				break;
			case "multiMonthreports2BVS2": viewName = "multi_month_summary2BVS2";
			break;
			case "stockSummaryreports": viewName= "stockSummaryreports";
			break;
			case "stockDetailsreport" : viewName= "stockDetailsReport";
			break;
			case "stockLedgerreports": viewName= "stockLedgerreports";
			break;
			case "ratereports": viewName= "stockRateListReports";
			break;
			case "itemSalesreports": viewName= "itemSalesReports";
			break;
			case "partyWisereports": viewName= "partyWiseReports";
			break;
			case "lowStockReports" : viewName="lowStockReports";
			break;
			case "agingReports":viewName="agingReports";
			break;
		}
		if("stockSummaryreports".equalsIgnoreCase(viewName) || "stockLedgerreports".equalsIgnoreCase(viewName) || "stockDetailsReport".equalsIgnoreCase(viewName) ||
				"stockRateListReports".equalsIgnoreCase(viewName) || "itemSalesReports".equalsIgnoreCase(viewName) || "partyWiseReports".equalsIgnoreCase(viewName) ||
				"lowStockReports".equalsIgnoreCase(viewName) || "agingReports".equalsIgnoreCase(viewName)) {
			if("partyWiseReports".equalsIgnoreCase(viewName)) {
				List<CompanyCustomers> customers=profileService.getCustomers(clientid);
				Collections.sort(customers, new Comparator<CompanyCustomers>() {
					@Override
					public int compare(CompanyCustomers obj1, CompanyCustomers obj2) {
						return obj1.getName().compareTo(obj2.getName());
					}
				});
				model.addAttribute("customers", customers);
			}
			List<CompanyItems> items=profileService.getItems(clientid);
			Collections.sort(items, new Comparator<CompanyItems>() {
				@Override
				public int compare(CompanyItems obj1, CompanyItems obj2) {
					return obj1.getItemno().compareTo(obj2.getItemno());
				}
			});
			model.addAttribute("items", items);
			model.addAttribute("type", type); 
	    	return "inventory/"+viewName;
		}
		if("ledger".equalsIgnoreCase(viewName)) {
			model.addAttribute("type", type); 
	    	return "ledger/"+viewName;
		}
		
		return "reports/"+viewName;
	}
	
	@RequestMapping("/travelreports/{id}/{name}/{usertype}/{clientid}/{month}/{year}")
	public String treportView(@PathVariable("id") String id, @PathVariable("name") String fullname,
		@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, @PathVariable("month") int month, 
		@PathVariable("year") int year, @RequestParam String type, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "reportView::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		model.addAttribute("user", user);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
				List<String> clntids = companyUser.getCompany();
				if(isNotEmpty(clntids) && clntids.size() > 0){
					if(clntids.contains(clientid)){
						model.addAttribute("companyUser", companyUser);
					}
				}
				if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
					model.addAttribute("addClient", companyUser.getAddclient());
				}else {
					model.addAttribute("addClient", "true");
				}
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", clientService.getGSTReturnsSummary(client, month, year, false));
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		String viewName=null;
		
		switch(type) {
			case "avgbalance": viewName= "avg_balance";
				break;
			case "busdetail": viewName= "busdetail";
				break;
			case "gstpayment": viewName= "gst_payment";
				break;
			case "pandl": viewName= "pandl";
				break;
			case "variableexpence": viewName= "variable_expence";
				break;
			case "vehicalwisemilage": viewName= "vehical_wise_milage";
				break;
			
		}
		
		
		return "travel/"+viewName;
	}
	
	@RequestMapping(value = "/getitcinvs/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getItcClaimedInvoices(@PathVariable("id") String id, @PathVariable("returntype") String returntype, @PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam String  itcinvtype, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
	
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		
		//String itcinvtype = request.getParameter("itcinvtype");
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 0, 23, 59, 59);
		if(month == 0) {
			cal.set(year, 3, 0, 23, 59, 59);
		}
		Date stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 23, 59, 59);
		if(month == 0) {
			cal.set(year + 1, 3, 0, 23, 59, 59);
		}
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		
		Page<? extends InvoiceParent> invoices = clientService.getItcInvoices(clientid,returntype, itcinvtype, stDate, endDate, start, length, searchVal);
		if(isNotEmpty(invoices)) {
			for(InvoiceParent invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
		
		TotalInvoiceAmount totalInvoiceAmount = clientService.getItcTotalInvoicesAmounts(clientid, returntype, itcinvtype, stDate, endDate, searchVal);
		
		Map<String,Object> invoicesMap=new HashMap();
		invoicesMap.put("invoices", invoices);
		invoicesMap.put("invoicesAmount",totalInvoiceAmount);	
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(invoicesMap);
	}

	@RequestMapping(value = "/getitcCustomInvs/{id}/{clientid}/{returntype}/{fromtime}/{totime}", method = RequestMethod.GET)
	public @ResponseBody String getItcCustomInvoices(@PathVariable("id") String id, @PathVariable("returntype") String returntype, @PathVariable("clientid") String clientid,
			@PathVariable String fromtime, @PathVariable String totime, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		
		String itcinvtype = request.getParameter("itcinvtype");
		
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1,
				Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23, 59, 59);
		endDate = new java.util.Date(cal.getTimeInMillis());
		
		Page<? extends InvoiceParent> invoices = clientService.getItcInvoices(clientid,returntype, itcinvtype, stDate, endDate, start, length, searchVal);
		if(isNotEmpty(invoices)) {
			for(InvoiceParent invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
		
		TotalInvoiceAmount totalInvoiceAmount = clientService.getItcTotalInvoicesAmounts(clientid, returntype, itcinvtype, stDate, endDate, searchVal);
		
		Map<String,Object> invoicesMap=new HashMap();
		invoicesMap.put("invoices", invoices);
		invoicesMap.put("invoicesAmount",totalInvoiceAmount);	
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(invoicesMap);
	}
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	
	private Map<String, String> getMultimonthGSTR2ASummaryMap(final Client client, int month, int year,String returntype) throws Exception {
		Map<String, String> reportMap = Maps.newHashMap();
		Double purchaseAmt = 0d, salesAmt = 0d, taxAmt = 0d, salesTax = 0d, purchaseTax = 0d, igst = 0d, cgst = 0d, sgst = 0d, totalTaxableValue = 0d;
		int totalInvoices = 0;
		Page<? extends InvoiceParent> pinvoices = clientService.getInvoices(null, client, returntype, month, year,
				false);
		if(isNotEmpty(pinvoices)){
			totalInvoices = pinvoices.getNumberOfElements();
		}
		if("GSTR2".equals(returntype)){
		if (isNotEmpty(pinvoices)) {
			for (InvoiceParent invoice : pinvoices) {
				if (isNotEmpty(invoice.getTotalitc())) {
					purchaseTax += invoice.getTotalitc();
				}
				if (isNotEmpty(invoice.getTotaltax())) {
					taxAmt += invoice.getTotaltax();
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
					}
				}
				if (isNotEmpty(invoice.getTotalamount())) {
						purchaseAmt += invoice.getTotalamount();
					
				}
				if (isNotEmpty(invoice.getTotaltaxableamount())) {
					totalTaxableValue += invoice.getTotaltaxableamount();
				}
			}
		}
		}else if("GSTR1".equals(returntype)){
			if (isNotEmpty(pinvoices)) {
				for (InvoiceParent invoice : pinvoices) {
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
						}
					}
					if (isNotEmpty(invoice.getTotalamount())) {
							salesAmt += invoice.getTotalamount();
					}
					if (isNotEmpty(invoice.getTotaltaxableamount())) {
						totalTaxableValue += invoice.getTotaltaxableamount();
					}
				}
			}
			
		}else if("Purchase Register".equals(returntype)){
			if (isNotEmpty(pinvoices)) {
				for (InvoiceParent invoice : pinvoices) {
					if (isNotEmpty(invoice.getTotalitc())) {
						purchaseTax += invoice.getTotalitc();
					}
					if (isNotEmpty(invoice.getTotaltax())) {
						taxAmt += invoice.getTotaltax();
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
						}
					}
					if (isNotEmpty(invoice.getTotalamount())) {
							purchaseAmt += invoice.getTotalamount();
					}
					if (isNotEmpty(invoice.getTotaltaxableamount())) {
						totalTaxableValue += invoice.getTotaltaxableamount();
					}
				}
			}
		}
		reportMap.put("Purchase", decimalFormat.format(purchaseAmt));
		reportMap.put("PurchaseTax", decimalFormat.format(purchaseTax));
		reportMap.put("Sales", decimalFormat.format(salesAmt));
		reportMap.put("salesTax", decimalFormat.format(salesTax));
		reportMap.put("TotalTaxableValue", decimalFormat.format(totalTaxableValue));
		reportMap.put("TotalTax", decimalFormat.format(taxAmt));
		reportMap.put("igst", decimalFormat.format(igst));
		reportMap.put("cgst", decimalFormat.format(cgst));
		reportMap.put("sgst", decimalFormat.format(sgst));
		reportMap.put("totalinvoices", totalInvoices+"");
		return reportMap;
	}
	
	@RequestMapping(value = "/dwnldfilingSummary/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource downloadFilingSummaryExcelData(@PathVariable("id") String id,
	@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, HttpServletResponse response, HttpServletRequest request) {
	response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	Client client = clientService.findById(clientid);
	String gstnumber = "";
	if(NullUtil.isNotEmpty(client)){
		gstnumber = client.getGstnnumber();
	}
	response.setHeader("Content-Disposition", "inline; filename='MGST_"+gstnumber+"_"+month+year+".xls");

	List<String> rtArray=Lists.newArrayList();
	List<String> retperiod = Lists.newArrayList();
	List<String> retperiod1 = Lists.newArrayList();
	List<String> returnArr=Arrays.asList(MasterGSTConstants.GSTR1,MasterGSTConstants.GSTR3B);

	List<FilingStatusReportsVO> filingStatusReportsVOList=Lists.newArrayList();
	List<ClientStatus> clientStatus = null ;
	String yearCode = Utility.getYearCode(month, year);
	String[] yrcd = yearCode.split("-");
	Date dt = new Date();
	int yr = dt.getYear()+1900;
	int mnth = dt.getMonth()+1;
	if(yrcd[0].equals(Integer.toString(yr))) {
		for(int i = 4; i<=mnth-1;i++) {
			String strMonth = month < 10 ? "0" + i : i + "";
			rtArray.add(strMonth+""+yrcd[0]);
			retperiod.add(strMonth+""+yrcd[0]);
			retperiod1.add(strMonth+""+yrcd[0]);
		}
	}else{
		String rtStart = "04"+year;
		String rtEnd = "03"+(year+1);
		rtArray=new LinkedList<String>(Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year,
		"10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd));
	
		retperiod=new LinkedList<String>(Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year,
		"10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd));
		retperiod1=new LinkedList<String>(Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year,
		"10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd));
	}

	clientStatus = clientStatusRepository.findByClientIdAndReturnTypeInAndReturnPeriodIn(clientid, returnArr, rtArray);
	for(ClientStatus clients : clientStatus) {
		FilingStatusReportsVO reportsVo = new FilingStatusReportsVO();
		SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
		if(isNotEmpty(client)) {
			if(isNotEmpty(client.getBusinessname())) {
				reportsVo.setSupplierName(client.getBusinessname());
			}
			if(isNotEmpty(client.getGstnnumber())) {
				reportsVo.setGstin(client.getGstnnumber());
			}
		}
		reportsVo.setArnNo(clients.getArn());
		reportsVo.setDateOfFiling(date.format(clients.getDof()));
		reportsVo.setModeOfFiling(clients.getMof());
		if(clients.getReturnType().equals(MasterGSTConstants.GSTR1)) {
			retperiod.remove(new String(clients.getReturnPeriod()));
		}else {
			retperiod1.remove(new String(clients.getReturnPeriod()));
		}
		reportsVo.setRetPeriod(clients.getReturnPeriod());
		reportsVo.setReturnType(clients.getReturnType());
		reportsVo.setStatus(clients.getStatus());
		filingStatusReportsVOList.add(reportsVo);
	}
	for(String returntype : returnArr){
	List<String> gstr1Orgstr3b = Lists.newArrayList();

	if(returntype.equals("GSTR1")){
		gstr1Orgstr3b = retperiod;
	}else{
		gstr1Orgstr3b = retperiod1;
	}

	for(String gstr1Orgstr3bdetails : gstr1Orgstr3b) {
			FilingStatusReportsVO reportsVo = new FilingStatusReportsVO();
		
			if(isNotEmpty(client)) {
				if(isNotEmpty(client.getBusinessname())) {
					reportsVo.setSupplierName(client.getBusinessname());
				}
				if(isNotEmpty(client.getGstnnumber())) {
					reportsVo.setGstin(client.getGstnnumber());
				}
			}
			reportsVo.setArnNo("");
			reportsVo.setDateOfFiling("");
			reportsVo.setModeOfFiling("");
			reportsVo.setRetPeriod(gstr1Orgstr3bdetails);
			reportsVo.setReturnType(returntype);
			reportsVo.setStatus("Pending");
			filingStatusReportsVOList.add(reportsVo);
		}
	}

	File file = new File("MGST_"+gstnumber+"_"+month+year+".xls");
	try {
	file.createNewFile();
	FileOutputStream fos = new FileOutputStream(file);
	List<String> headers = Arrays.asList("ARN", "GSTIN", "Mode Of Filing", "Date Of Filing", "Return Period", "Return Type","Status");
	SimpleExporter exporter = new SimpleExporter();
	exporter.gridExport(headers, filingStatusReportsVOList,"arnNo, gstin, modeOfFiling,dateOfFiling,retPeriod,returnType,status",fos);

	return new FileSystemResource(file);
	} catch (IOException e) {
	logger.error(CLASSNAME + "downloadFilingSummaryExcelData : ERROR", e);
	}
	return new FileSystemResource(new File("MGST_"+gstnumber+"_"+month+year+".xls"));
	}
	
	@RequestMapping(value = "/dwnldsupplierSummary/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource downloadSupplierExcelData(@PathVariable("id") String id,
	@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, HttpServletResponse response, HttpServletRequest request) {
	response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	Client client = clientService.findById(clientid);
	String gstnumber = "";
	if(NullUtil.isNotEmpty(client)){
	gstnumber = client.getGstnnumber();
	}

	List<String> rtArray=Arrays.asList(
	"04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year,
	"11"+year, "12"+year, "01"+(year+1), "02"+(year+1), "03"+(year+1));
	String cYear = request.getParameter("cYear");

	if((cYear !=null && cYear.trim() !="")) {
		int currYear = Integer.parseInt(cYear);
		if(currYear == year) {
			String cMonth = request.getParameter("cMonth");
			if((cMonth !=null && cMonth.trim() !="")) {
				rtArray= new ArrayList<>();
				int currMonth=Integer.parseInt(cMonth);
				if(currMonth ==1 || currMonth == 2 || currMonth == 3) {
					for(int mnth=1; mnth<=currMonth; mnth++){
						rtArray.add("0"+mnth+year);
					}
					for(int mnth=4; mnth<=12; mnth++){
						if(mnth<=9) {
							rtArray.add("0"+mnth+year);
						}else {
							rtArray.add(""+mnth+year);
						}
					}
				}else {
					for(int mnth=4; mnth<=currMonth; mnth++){
						if(mnth<=9) {
							rtArray.add("0"+mnth+year);
						}else {
							rtArray.add(""+mnth+year);
						}
					}
				}
			}
		}
	}

	response.setHeader("Content-Disposition", "inline; filename='MGST_"+gstnumber+"_"+month+year+".xlsx");

	List<FilingStatusReportsVO> filingStatusReportsVOList=Lists.newArrayList();

	Map<String,Map<String,Map<String,List<String>>>> supplierMap=clientService.getAllSupplierStatusBasedOnClientid(clientid,year, rtArray);

	supplierMap.forEach((supMapKey,supValMap)->{
		String suppliername=supMapKey.substring(0, (supMapKey.length()-16));
		String suppliergstn =supMapKey.substring((suppliername.length()+1));

		Map<String,Map<String,List<String>>> returnMap=supValMap;
		returnMap.forEach((retMapKey,retMapValue)->{

			Map<String,List<String>> filingMap=retMapValue;
			filingMap.forEach((mapkey,mapvalue)->{

				FilingStatusReportsVO reportsVo = new FilingStatusReportsVO();
				if(suppliername.equalsIgnoreCase("null")) {
					reportsVo.setSupplierName("");
				}else {
					reportsVo.setSupplierName(suppliername);
				}
				reportsVo.setGstin(suppliergstn);
				String[] returnType=retMapKey.split("_");
				reportsVo.setReturnType(returnType[0]);
				if(returnType[0].equals("GSTR1")) {
					reportsVo.setRetPeriod(mapkey.substring(returnType[0].length(), 11));
				}else {
					reportsVo.setRetPeriod(mapkey.substring(returnType[0].length()-1, 11));
				}
				if(mapvalue.size()>1){
					for(int n=0;n < mapvalue.size(); n++) {
						if(n == 0) {
							reportsVo.setStatus(mapvalue.get(n));
						}else if(n == 1) {
							reportsVo.setModeOfFiling(mapvalue.get(n));
						}else if(n == 2) {
							reportsVo.setDateOfFiling(mapvalue.get(n));
						}else if(n == 3) {
							reportsVo.setArnNo(mapvalue.get(n));
						}
					}
				}else {
				reportsVo.setStatus(mapvalue.get(0));
				}
				filingStatusReportsVOList.add(reportsVo);
			});
		});
	});

	File file = new File("MGST_"+gstnumber+"_"+month+year+".xlsx");
	try {
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		List<String> headers = Arrays.asList("Supplier Name", "ARN", "GSTIN", "Mode Of Filing", "Date Of Filing", "Return Period", "Return Type","Status");
		SimpleExporter exporter = new SimpleExporter();
		exporter.gridExport(headers, filingStatusReportsVOList,"supplierName, arnNo, gstin, modeOfFiling,dateOfFiling,retPeriod,returnType,status",fos);
		return new FileSystemResource(file);
	} catch (IOException e) {
		logger.error(CLASSNAME + "downloadSupplierExcelData : ERROR", e);
	}
	return new FileSystemResource(new File("MGST_"+gstnumber+"_"+month+year+".xlsx"));
	}
	
	@RequestMapping(value = "/dwnldAspUsage", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource downloadAspUsageExcelData(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "dashboardType", required = true) String dashboardType,
			@RequestParam(value = "day", required = true) int day,
			@RequestParam(value = "month", required = true) int month, 
			@RequestParam(value = "year", required = true) int year,@RequestParam(value = "cost", required = true) String cost, HttpServletResponse response, HttpServletRequest request) {
	response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	response.setHeader("Content-Disposition", "inline; filename='MGST_"+month+year+".xls");
	Calendar cal = Calendar.getInstance();
	if(day > 0) {
		cal.set(year, month, day, 0, 0, 0);
	}else {
		cal.set(year, month, 0, 0, 0, 0);
	}
	Date startDate = new java.util.Date(cal.getTimeInMillis());
	cal = Calendar.getInstance();
	if(day > 0) {
		cal.set(year, month, day+1, 0, 0, 0);
	}else {
		cal.set(year, month+1, 0, 0, 0, 0);
	}
	Date endDate = new java.util.Date(cal.getTimeInMillis());
	List<MeteringVo> meteringVOList=Lists.newArrayList();
		Page<Metering> metering  = meteringService.getGSTAllMeteringsFoExcel(dashboardType, id, startDate, endDate);
		if(NullUtil.isNotEmpty(metering) && NullUtil.isNotEmpty(metering.getContent())){
			for(Metering meter : metering.getContent()){
				meter.setMeterid(meter.getId().toString());
				if("Fail".equals(meter.getStatus()) && NullUtil.isNotEmpty(meter.getErrorMessage())){
					meter.setCost("0.0");
				}else{
					meter.setCost(cost);
				}
				MeteringVo meteringvo = new MeteringVo();
				if(NullUtil.isNotEmpty(meter.getGstnusername())) {
					meteringvo.setGstnusername(meter.getGstnusername());
				}else {
					meteringvo.setGstnusername("");
				}
				if(NullUtil.isNotEmpty(meter.getIpusr())) {
					meteringvo.setIpusr(meter.getIpusr());
				}else {
					meteringvo.setIpusr("");
				}
				if(NullUtil.isNotEmpty(meter.getType())) {
					meteringvo.setType(meter.getType());
				}else {
					meteringvo.setType("");
				}
				if(NullUtil.isNotEmpty(meter.getServicename())) {
					meteringvo.setServicename(meter.getServicename());
				}else {
					meteringvo.setServicename("");
				}
				if(NullUtil.isNotEmpty(meter.getStatus())) {
					meteringvo.setStatus(meter.getStatus());
				}else {
					meteringvo.setStatus("");
				}
				if(NullUtil.isNotEmpty(meter.getSize())) {
					meteringvo.setSize(meter.getSize()+"");
				}else {
					meteringvo.setSize("");
				}
				if(NullUtil.isNotEmpty(meter.getCost())) {
					meteringvo.setCost(meter.getCost());
				}else {
					meteringvo.setCost("");
				}
				if(NullUtil.isNotEmpty(meter.getStarttime())) {
					Date currentDate = new Date(meter.getStarttime());
					DateFormat df = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss a");
					meteringvo.setStarttime(df.format(currentDate));
				}else {
					meteringvo.setStarttime("");
				}
				meteringVOList.add(meteringvo);
				
			}
		}

	File file = new File("MGST_"+month+year+".xls");
	try {
	file.createNewFile();
	FileOutputStream fos = new FileOutputStream(file);
	List<String> headers = Arrays.asList("GST Username", "IP Address", "Type", "Service Name", "Status", "Size","Cost","Start Time");
	SimpleExporter exporter = new SimpleExporter();
	exporter.gridExport(headers, meteringVOList,"gstnusername, ipusr, type,servicename,status,size,cost,starttime",fos);

	return new FileSystemResource(file);
	} catch (IOException e) {
	logger.error(CLASSNAME + "downloadFilingSummaryExcelData : ERROR", e);
	}
	return new FileSystemResource(new File("MGST_"+month+year+".xls"));
	}
	
	@RequestMapping(value = "/dwnldsupplierSummarynew/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public void downloadSupplierExcelDatanew(@PathVariable("id") String id,
	@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, HttpServletResponse response, HttpServletRequest request) {
	Client client = clientService.findById(clientid);
	String gstnumber = "";
	if(NullUtil.isNotEmpty(client)){
	gstnumber = client.getGstnnumber();
	}
	List<String> rtArray=Arrays.asList(
	"04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year,
	"11"+year, "12"+year, "01"+(year+1), "02"+(year+1), "03"+(year+1));
	String cYear = request.getParameter("cYear");

	if((cYear !=null && cYear.trim() !="")) {
		int currYear = Integer.parseInt(cYear);
		if(currYear == year) {
			String cMonth = request.getParameter("cMonth");
			if((cMonth !=null && cMonth.trim() !="")) {
				rtArray= new ArrayList<>();
				int currMonth=Integer.parseInt(cMonth);
				if(currMonth ==1 || currMonth == 2 || currMonth == 3) {
					for(int mnth=1; mnth<=currMonth; mnth++){
						rtArray.add("0"+mnth+year);
					}
					for(int mnth=4; mnth<=12; mnth++){
						if(mnth<=9) {
							rtArray.add("0"+mnth+year);
						}else {
							rtArray.add(""+mnth+year);
						}
					}
				}else {
					for(int mnth=4; mnth<=currMonth; mnth++){
						if(mnth<=9) {
							rtArray.add("0"+mnth+year);
						}else {
							rtArray.add(""+mnth+year);
						}
					}
				}
			}
		}
	}
	
	List<String> headers = Arrays.asList("Supplier Name", "ARN", "GSTIN", "Mode Of Filing", "Date Of Filing", "Return Period", "Return Type","Status");
	OutputStream nout = null;
	ZipOutputStream zipOutputStream = null;
	try {
		nout = response.getOutputStream();
		String fileName = "MGST_"+gstnumber+"_"+month+year+".zip";
		response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
		response.setContentType("application/octet-stream; charset=utf-8");
		zipOutputStream = new ZipOutputStream(nout);
		int c =0;
		Query query = new Query();
		Criteria criteria = Criteria.where("clientid").is(clientid);
		query.addCriteria(criteria);
		int length = 5000;
		for(int z=0;z< Integer.MAX_VALUE;z++) {
			Pageable pageable = new PageRequest(z, length);
			query.with(pageable);
			List<CompanySuppliers> companySuppliers = mongoTemplate.find(query, CompanySuppliers.class);
			if(NullUtil.isEmpty(companySuppliers)) {
				break;
			}
			List<FilingStatusReportsVO> filingStatusReportsVOList=Lists.newArrayList();
			Map<String,Map<String,Map<String,List<String>>>> supplierMap=clientService.getAllSupplierStatusBasedOnClientid(companySuppliers,year, rtArray);
			supplierMap.forEach((supMapKey,supValMap)->{
				String suppliername=supMapKey.substring(0, (supMapKey.length()-16));
				String suppliergstn =supMapKey.substring((suppliername.length()+1));

				Map<String,Map<String,List<String>>> returnMap=supValMap;
				returnMap.forEach((retMapKey,retMapValue)->{

					Map<String,List<String>> filingMap=retMapValue;
					filingMap.forEach((mapkey,mapvalue)->{

						FilingStatusReportsVO reportsVo = new FilingStatusReportsVO();
						if(suppliername.equalsIgnoreCase("null")) {
							reportsVo.setSupplierName("");
						}else {
							reportsVo.setSupplierName(suppliername);
						}
						reportsVo.setGstin(suppliergstn);
						String[] returnType=retMapKey.split("_");
						reportsVo.setReturnType(returnType[0]);
						if(returnType[0].equals("GSTR1")) {
							reportsVo.setRetPeriod(mapkey.substring(returnType[0].length(), 11));
						}else {
							reportsVo.setRetPeriod(mapkey.substring(returnType[0].length()-1, 11));
						}
						if(mapvalue.size()>1){
							for(int n=0;n < mapvalue.size(); n++) {
								if(n == 0) {
									reportsVo.setStatus(mapvalue.get(n));
								}else if(n == 1) {
									reportsVo.setModeOfFiling(mapvalue.get(n));
								}else if(n == 2) {
									reportsVo.setDateOfFiling(mapvalue.get(n));
								}else if(n == 3) {
									reportsVo.setArnNo(mapvalue.get(n));
								}
							}
						}else {
						reportsVo.setStatus(mapvalue.get(0));
						}
						filingStatusReportsVOList.add(reportsVo);
					});
				});
			});
			double i = ((double)filingStatusReportsVOList.size())/60000;
			int j = (int)i;
			if(i-(int)i > 0) {
				j = (int)i+1;
			}
			List<List<FilingStatusReportsVO>> lt = Lists.newArrayList();
			int a=0;
			int b = 60000;
			if(filingStatusReportsVOList.size() < 60000) {
				b= filingStatusReportsVOList.size();
			}
			for(int k = 1; k <= j;k++) {
				lt.add(filingStatusReportsVOList.subList(a, b));
				a = b;
				if(k == j-1) {
					b = filingStatusReportsVOList.size();
				}else {
					b = b+60000;
				}
			}
			for(List<FilingStatusReportsVO> InvoicesList: lt) {
			    try(
			        Workbook workbook = new XSSFWorkbook();
			        ByteArrayOutputStream out = new ByteArrayOutputStream();
			    ){
			      Sheet sheet = workbook.createSheet("Supplier_Filing_Status");
			      Font headerFont = workbook.createFont();
			      headerFont.setBold(true);
			      headerFont.setColor(IndexedColors.BLUE.getIndex());
			      CellStyle headerCellStyle = workbook.createCellStyle();
			      headerCellStyle.setFont(headerFont);
			      headerCellStyle.setAlignment(headerCellStyle.ALIGN_CENTER);
			      // Row for Header
			      Row headerRow = sheet.createRow(0);
			      // Header
			      for (int col = 0; col < headers.size(); col++) {
			        Cell cell = headerRow.createCell(col);
			        cell.setCellValue(headers.get(col));
			        cell.setCellStyle(headerCellStyle);
			        sheet.autoSizeColumn(col);
			      }
			    	   int b2bIdx = 1;
			    	   if(isNotEmpty(InvoicesList)) {
			 		      for (FilingStatusReportsVO invoice : InvoicesList) {
			 		    	 Row row1 = sheet.createRow(b2bIdx++);
			 		    	row1.createCell(0).setCellValue(invoice.getSupplierName());
						    row1.createCell(1).setCellValue(invoice.getArnNo());
						    row1.createCell(2).setCellValue(invoice.getGstin());
						    row1.createCell(3).setCellValue(invoice.getModeOfFiling());
						    row1.createCell(4).setCellValue(invoice.getDateOfFiling());
						    row1.createCell(5).setCellValue(invoice.getReturnType());
						    row1.createCell(6).setCellValue(invoice.getRetPeriod());
						    row1.createCell(7).setCellValue(invoice.getStatus());
			 		      }
			    	   } 
			      //Setting Auto Column Width
			       sheet.autoSizeColumn(0);
			       sheet.setColumnWidth(0,50 * 256);
			       sheet.autoSizeColumn(1);
		           sheet.setColumnWidth(1,15 * 256);
		           sheet.autoSizeColumn(2);
		           sheet.setColumnWidth(2,15 * 256);
		           sheet.autoSizeColumn(3);
		           sheet.setColumnWidth(3,15 * 256);
		           sheet.autoSizeColumn(4);
		           sheet.setColumnWidth(4,15 * 256);
		           sheet.autoSizeColumn(5);
		           sheet.setColumnWidth(5,15 * 256);
		           sheet.autoSizeColumn(6);
		           sheet.setColumnWidth(6,15 * 256);
		           sheet.autoSizeColumn(7);
		           sheet.setColumnWidth(7,15 * 256);
			      workbook.write(out);
			      ZipEntry entry = new ZipEntry("Supplier_Filing_Status"+(c+1)+ ".xls");
					zipOutputStream.putNextEntry(entry);
					out.writeTo(zipOutputStream);
					zipOutputStream.closeEntry();
					out.close();
					workbook.close();
					c++;
			    }
		    }
		}
		nout.flush();  
	    zipOutputStream.flush();
	} catch (IOException e) {
		logger.error(CLASSNAME + "downloadSupplierExcelData : ERROR", e);
	}finally {
		try {
			if (isNotEmpty(zipOutputStream)) {
				zipOutputStream.close();
			}	
			if (isNotEmpty(nout)) {
				nout.close();
			}				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	}

	@RequestMapping(value = "/dwnldsupplierSummaryn/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public void downloadSupplierExcelDatan(@PathVariable("id") String id,
	@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, HttpServletResponse response, HttpServletRequest request) {
	Client client = clientService.findById(clientid);
	String gstnumber = "";
	if(NullUtil.isNotEmpty(client)){
		gstnumber = client.getGstnnumber();
	}
	List<String> rtArray=Arrays.asList(
	"04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year,
	"11"+year, "12"+year, "01"+(year+1), "02"+(year+1), "03"+(year+1));
	String cYear = request.getParameter("cYear");

	if((cYear !=null && cYear.trim() !="")) {
		int currYear = Integer.parseInt(cYear);
		if(currYear == year) {
			String cMonth = request.getParameter("cMonth");
			if((cMonth !=null && cMonth.trim() !="")) {
				rtArray= new ArrayList<>();
				int currMonth=Integer.parseInt(cMonth);
				if(currMonth ==1 || currMonth == 2 || currMonth == 3) {
					for(int mnth=1; mnth<=currMonth; mnth++){
						rtArray.add("0"+mnth+year);
					}
					for(int mnth=4; mnth<=12; mnth++){
						if(mnth<=9) {
							rtArray.add("0"+mnth+year);
						}else {
							rtArray.add(""+mnth+year);
						}
					}
				}else {
					for(int mnth=4; mnth<=currMonth; mnth++){
						if(mnth<=9) {
							rtArray.add("0"+mnth+year);
						}else {
							rtArray.add(""+mnth+year);
						}
					}
				}
			}
		}
	}
	
	List<String> headers = Arrays.asList("Supplier Name", "ARN", "GSTIN", "Mode Of Filing", "Date Of Filing", "Return Period", "Return Type","Status");
	OutputStream nout = null;
	ZipOutputStream zipOutputStream = null;
	try {
		nout = response.getOutputStream();
		String fileName = "MGST_"+gstnumber+"_"+month+year+".zip";
		response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
		response.setContentType("application/octet-stream; charset=utf-8");
		zipOutputStream = new ZipOutputStream(nout);
		byte[] buf = new byte[1024];
		int len = 0;
		int c =0;
		Query query = new Query();
		List<String> gstno = Lists.newArrayList();
		gstno.add("");
		gstno.add(null);
		Criteria criteria = Criteria.where("clientid").is(clientid).and("gstnnumber").nin(gstno);
		
		query.addCriteria(criteria);
		int length = 2500;
		for(int z=0;z< Integer.MAX_VALUE;z++) {
			Pageable pageable = new PageRequest(z, length);
			query.with(pageable);
			List<CompanySuppliers> companySuppliers = mongoTemplate.find(query, CompanySuppliers.class);
			
			if(NullUtil.isEmpty(companySuppliers)) {
				break;
			}
			List<FilingStatusReportsVO> filingStatusReportsVOList=Lists.newArrayList();
			filingStatusReportsVOList = clientService.getAllSupplierStatusBasedOnClientids(companySuppliers,year, rtArray);
			double i = ((double)filingStatusReportsVOList.size())/60000;
			int j = (int)i;
			if(i-(int)i > 0) {
				j = (int)i+1;
			}
			List<List<FilingStatusReportsVO>> lt = Lists.newArrayList();
			int a=0;
			int b = 60000;
			if(filingStatusReportsVOList.size() < 60000) {
				b= filingStatusReportsVOList.size();
			}
			for(int k = 1; k <= j;k++) {
				lt.add(filingStatusReportsVOList.subList(a, b));
				a = b;
				if(k == j-1) {
					b = filingStatusReportsVOList.size();
				}else {
					b = b+60000;
				}
			}
			for(List<FilingStatusReportsVO> InvoicesList: lt) {
				File file = new File("Supplier_Filing_Status"+gstnumber+"_"+(c+1)+ ".xls");
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				SimpleExporter exporter = new SimpleExporter();
				exporter.gridExport(headers, InvoicesList,"supplierName, arnNo, gstin, modeOfFiling,dateOfFiling,retPeriod,returnType,status",fos);
				String fname = file.getName();
				FileInputStream fileInputStream = new FileInputStream(file);
				zipOutputStream.putNextEntry(new ZipEntry(fname));
				while((len=fileInputStream.read(buf)) >0){
					zipOutputStream.write(buf, 0, len);
				}
				 				//shut down; 
				zipOutputStream.closeEntry();
				if(isNotEmpty(fileInputStream)){
					fileInputStream.close();
				}
				file.delete();
				c++;
		    }
		}
		nout.flush();  
	    zipOutputStream.flush();
	} catch (IOException e) {
		logger.error(CLASSNAME + "downloadSupplierExcelData : ERROR", e);
	}finally {
		try {
			if (isNotEmpty(zipOutputStream)) {
				zipOutputStream.close();
			}	
			if (isNotEmpty(nout)) {
				nout.close();
			}				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	}
	
	
	
	/*@RequestMapping(value = "/monthgstr1fulldwnldyearlyxlsnew/{id}/{clientid}/{returntype}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody ResponseEntity<InputStreamResource> getInvoiceGSTR1YearlyFullReport(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype, 
			@PathVariable("year") int year, HttpServletRequest request) throws IOException {
		final String method = "getInvoiceFullReport::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String customField1 = "CustomField1";
		String customField2 = "CustomField2";
		String customField3 = "CustomField3";
		String customField4 = "CustomField4";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
			if(isNotEmpty(customFields)) {
				if("GSTR1".equals(returntype) || "SalesRegister".equals(returntype)) {
					if(isNotEmpty(customFields.getSales())) {
						int i=1;
						for(CustomData customdata : customFields.getSales()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(GSTR2.equals(returntype) || GSTR2A.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
					if(isNotEmpty(customFields.getPurchase())) {
						int i=1;
						for(CustomData customdata : customFields.getPurchase()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(returntype.equals(MasterGSTConstants.EINVOICE)){
					if(isNotEmpty(customFields.getEinvoice())) {
						int i=1;
						for(CustomData customdata : customFields.getEinvoice()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}
			}
			
		}
		List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,MasterGSTConstants.ISD,MasterGSTConstants.IMP_GOODS,MasterGSTConstants.IMP_SERVICES);
		List<String> ammendedInvTypes = Arrays.asList(MasterGSTConstants.B2BA, MasterGSTConstants.CDNA);
		Page<? extends InvoiceParent> invoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", invTypes,clientService.invoiceFilter(request));
		Page<? extends InvoiceParent> ammendedinvoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", ammendedInvTypes,clientService.invoiceFilter(request));
		if(GSTR2A.equals(returntype)) { returntype = GSTR2; }
		List<InvoiceVO> invoiceVOList =clientReportsUtil.invoiceListItems(invoices,returntype);
		List<InvoiceVO> ammendedInvoiceVOList =clientReportsUtil.invoiceListItems(ammendedinvoices,returntype);
		
		logger.debug(invoiceVOList);
		List<String> headers=null;
		
			headers=Arrays.asList("Invoice Date", "Invoice No","Invoice Type","Return Period", "Reverse Charge","GSTIN", "Place Of Supply", "Supplier Name", "CompanyStateName", "Counter Party Filing Status","Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Original Invoice Number","Original Invoice Date","Eway Bill Number", "Ledger", "State","Reverse Charge","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Supplier Notes",customField1,customField2,customField3,customField4,"Terms & Conditions","Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
					"Rate","Discount","Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","ITC Type", "ITC Available","Total Tax","Total Invoice Value","TDS Amount","Net Receivable(Total Invoice Value - TDS Amount)");
		
			ByteArrayInputStream in = invoiceService.gstr2aToExcel(invoiceVOList,ammendedInvoiceVOList,headers,"fulldetails");
		    HttpHeaders header = new HttpHeaders();
		    header.add("Content-Disposition", "attachment; filename='MGST_"+returntype+"_"+gstnumber+"_"+year+".xlsx");
		     return ResponseEntity.ok().headers(header).body(new InputStreamResource(in));
	}*/
	
	@RequestMapping(value = "/mutltimonthReportsTotalSummarynew/{clientid}/{returntype}/{month}/{year}")
	public @ResponseBody Map<String,Map<String,String>> getConsolidateMultimonthReportsTotalAmounts(@PathVariable String returntype, @PathVariable String clientid, @PathVariable int month, 
			@PathVariable int year,HttpServletRequest request){
		final String method = "getConsolidateMultimonthReportsTotalAmounts::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client=clientService.findById(clientid);
		InvoiceFilter filter =clientService.invoiceFilter(request);
		return invoiceService.getConsolidatedSummeryForYearReports(client, returntype, Utility.getYearCode(month, year),false, "Multimonth-Reports",filter);
	}
	
	@RequestMapping(value = "/dwnldReportsFinancialSummaryxlsnew/{id}/{clientid}/{returntype}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource dwnldReportsFinancialSummary(@PathVariable("id") String id,@PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, 
			@RequestParam String reporttype, @RequestParam ("year") int year, @RequestParam String fromdate, @RequestParam String todate,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String clientname = "";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			clientname = client.getBusinessname().replace("/", "");
		}
		
		String fy =null;
		if(reporttype.equalsIgnoreCase("invoice_report")) {
			if(year != 0) {
				String nextyear = ((year+1)+"").substring(2);
				fy = year+"_"+nextyear;
			}else {
				fy = fromdate+"_to_"+todate;
			}
		}else {
			String nextyear = ((year+1)+"").substring(2);
			fy = year+"_"+nextyear;
		}
		
		String filename = ""+clientname+"_"+gstnumber+"_"+fy+"-Financial_Summary.xls";
		response.setHeader("Content-Disposition", "inline; filename='"+clientname+"_"+gstnumber+"_"+fy+"-Financial_Summary.xls");
		
		Map<String, Map<String, String>> summaryMap = null;
		
		
			summaryMap = invoiceService.getConsolidatedSummeryForYearReports(client, returntype, Utility.getYearCode(0, year), false, "Multimonth-Reports",clientService.invoiceFilter(request));			
		
		Double totSales =0d,totTaxable= 0d, totAmt =0d, totInv = 0d, totAmt1 = 0d, totpurchase = 0d,totbalance = 0d,totSalesTax = 0d,totPurchasetax = 0d,totTax = 0d,totExempted = 0d, totTcsAmount = 0d,totTdsAmount = 0d;
		Double totIgst = 0d, totCgst = 0d, totSgst = 0d, totCess = 0d,totItc = 0d;
		int[] monthArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		
		for (int i : monthArray) {
			totInv += Double.valueOf(summaryMap.get(i + "").get("totalTransactions"));
			totTaxable += Double.valueOf(summaryMap.get(i + "").get("Sales"));
			totAmt += Double.valueOf(summaryMap.get(i + "").get("totalamt"));
			totSalesTax += Double.valueOf(summaryMap.get(i + "").get("SalesTax"));
			totPurchasetax += Double.valueOf(summaryMap.get(i + "").get("SalesTax"));
			totExempted += Double.valueOf(summaryMap.get(i + "").get("exempted"));
			totAmt1 += Double.valueOf(summaryMap.get(i + "").get("totalamt"));
			totIgst += Double.valueOf(summaryMap.get(i + "").get("igst"));
			totCgst += Double.valueOf(summaryMap.get(i + "").get("cgst"));
			totSgst += Double.valueOf(summaryMap.get(i + "").get("sgst"));
			totCess += Double.valueOf(summaryMap.get(i + "").get("cess"));
			totTcsAmount += Double.valueOf(summaryMap.get(i + "").get("tcsamount"));
			totTdsAmount += Double.valueOf(summaryMap.get(i + "").get("tdsamount"));
			if(NullUtil.isNotEmpty(summaryMap.get(i + "").get("itc"))) {
				totItc += Double.valueOf(summaryMap.get(i + "").get("itc"));
			}
			totTax += Double.valueOf(summaryMap.get(i + "").get("Tax"));
		}

		Map<String, String> totReportMap = Maps.newHashMap();
		totReportMap.put("totInv", decimalFormat.format(totInv));
		totReportMap.put("totTaxable", decimalFormat.format(totTaxable));
		totReportMap.put("totAmt", decimalFormat.format(totAmt));
		totReportMap.put("totSalesTax", decimalFormat.format(totSalesTax));
		totReportMap.put("totPurchasetax", decimalFormat.format(totPurchasetax));
		totReportMap.put("totExempted", decimalFormat.format(totExempted));
		totReportMap.put("totAmt1", decimalFormat.format(totAmt1));
		totReportMap.put("totIgst", decimalFormat.format(totIgst));
		totReportMap.put("totCgst", decimalFormat.format(totCgst));
		totReportMap.put("totSgst", decimalFormat.format(totSgst));
		totReportMap.put("totCess", decimalFormat.format(totCess));
		totReportMap.put("totTcsAmount", decimalFormat.format(totTcsAmount));
		totReportMap.put("totTdsAmount", decimalFormat.format(totTdsAmount));
		totReportMap.put("totItc", decimalFormat.format(totItc));
		/* totReportMap.put("totbalance", decimalFormat.format(totbalance));
		totReportMap.put("totSalesTax", decimalFormat.format(totSalesTax));
		totReportMap.put("totPurchasetax", decimalFormat.format(totPurchasetax));
		totReportMap.put("totTdsAmount", decimalFormat.format(totTdsAmount));*/ 
		//totReportMap.put("totTax", decimalFormat.format(totTax)); 
		summaryMap.put("totals", totReportMap);
		Map<String, FinancialSummaryVO> summaryMapr = Maps.newHashMap();
		Iterator iter = summaryMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Map<String,String> monthSummary= (Map<String, String>) entry.getValue();
			String mnth = entry.getKey().toString();
			if(!"totals".equals(mnth)) {
			Iterator miter = monthSummary.entrySet().iterator();
			while (miter.hasNext()) {
				Map.Entry mentry = (Map.Entry) miter.next();
				if(summaryMapr.containsKey(mentry.getKey())) {
					String typ = mentry.getKey().toString();
						 if(!typ.equals("exempted") && !typ.equals("tdsamount")) {
								FinancialSummaryVO sfinSummary = summaryMapr.get(mentry.getKey());
								switch(mnth) {
									case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
									break;
							}
							summaryMapr.put(mentry.getKey().toString(), sfinSummary);
						 }	
					 
					 
					 
					 
			}else {
					String typ = mentry.getKey().toString();
						if(!typ.equals("exempted") && !typ.equals("tdsamount")) {
							FinancialSummaryVO sfinSummary = new FinancialSummaryVO();
							switch(typ) {
									case "totalTransactions": sfinSummary.setType("Total Transactions");
									break;
									case "Sales": sfinSummary.setType("Taxable Value");
									break;
									//case "Purchase": sfinSummary.setType("Purchases");
									//break;
									case "SalesTax": sfinSummary.setType("Tax Value");
									break;
									case "totalamt": sfinSummary.setType("Total Amount");
									break;
									//case "exempted": sfinSummary.setType("Exempted Value");
									//break;
									case "igst": sfinSummary.setType("IGST Amount");
									break;
									case "cgst": sfinSummary.setType("CGST Amount");
									break;
									case "sgst": sfinSummary.setType("SGST Amount");
									break;
									case "cess": sfinSummary.setType("CESS Amount");
									break;
									case "tcsamount": sfinSummary.setType("TCS Value");
									break;
									case "itc": sfinSummary.setType("ITC Value");
									break;
									default : sfinSummary.setType(typ);
									break;
							}
							
							switch(mnth) {
								case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
								break;
							}
							
							summaryMapr.put(mentry.getKey().toString(), sfinSummary);
					}
							
				}
			}
		}
		}
		
		Map<String,String> totals = summaryMap.get("totals");
		List<FinancialSummaryVO> financialSummaryVOList=Lists.newArrayList();
		Iterator summary = summaryMapr.entrySet().iterator();
		while (summary.hasNext()) {
			Map.Entry entry = (Map.Entry) summary.next();
			if("Sales".equals(entry.getKey().toString()) || "Purchase".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTaxable").toString()));
				financialSummaryVOList.add(sales);	
			}else if("SalesTax".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totSalesTax").toString()));
				financialSummaryVOList.add(sales);	
			}else if("PurchaseTax".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totPurchasetax").toString()));
				financialSummaryVOList.add(sales);	
			}else if("exempted".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totExempted").toString()));
				financialSummaryVOList.add(sales);	
			}else if("tcsamount".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTcsAmount").toString()));
				financialSummaryVOList.add(sales);	
			}else if("tdsamount".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				//sales.setTotalamt(Double.parseDouble(totals.get("totTdsAmount").toString()));
				financialSummaryVOList.add(sales);	
			}else if("igst".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totIgst").toString()));
				financialSummaryVOList.add(sales);	
			}else if("cgst".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totCgst").toString()));
				financialSummaryVOList.add(sales);	
			}else if("sgst".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totSgst").toString()));
				financialSummaryVOList.add(sales);	
			}else if("cess".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totCess").toString()));
				financialSummaryVOList.add(sales);	
			} else if("totalTransactions".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totInv").toString()));
				financialSummaryVOList.add(sales);	
			}else if("totTaxable".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTaxable").toString()));
				financialSummaryVOList.add(sales);	
			}else if("totalamt".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totAmt").toString()));
				financialSummaryVOList.add(sales);	
			}else if("itc".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totItc").toString()));
				financialSummaryVOList.add(sales);	
			}
				
		}
		
		File file = new File(filename);
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers = null;
					headers = Arrays.asList("", "April", "May", "June", "July", "August", "september","October", "November", "December", "January","February","March","Total");				
				
			SimpleExporter exporter = new SimpleExporter();
			exporter.gridExport(headers, financialSummaryVOList,"type, aprilamt, mayamt, juneamt,julyamt,augustamt,sepamt,octamt, novamt, decamt, janamt,febamt,maramt,totalamt",fos);
				
			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "downloadExcelData : ERROR", e);
		}
		return new FileSystemResource(new File(filename));
	}

	
	@RequestMapping(value = "/newdwnldAspUsage", method = RequestMethod.GET)
	public @ResponseBody void newDownloadAspUsageExcelData(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "dashboardType", required = true) String dashboardType,
			@RequestParam(value = "day", required = true) int day,
			@RequestParam(value = "month", required = true) int month, 
			@RequestParam(value = "year", required = true) int year,@RequestParam(value = "cost", required = true) String cost, HttpServletResponse response, HttpServletRequest request) {
		Calendar cal = Calendar.getInstance();
		if(day > 0) {
			cal.set(year, month, day, 0, 0, 0);
		}else {
			cal.set(year, month, 1, 0, 0, 0);
		}
		Date startDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		if(day > 0) {
			cal.set(year, month, day+1, 0, 0, 0);
		}else {
			cal.set(year, month+1, 0, 23, 59, 59);
		}
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		
		List<String> list = new ArrayList<String>();
		list.add("EWAYAPI");
		list.add("WayBillAuthentication");
		Query query = new Query();
		
		Criteria criteria = new Criteria();
		if(NullUtil.isNotEmpty(id)){
			query.addCriteria(criteria.where("userid").is(id));
		}
		query.addCriteria(Criteria.where("createdDate").gte(startDate).lte(endDate));
		String gstewayUsername = "GST User Name";
		if("gst".equals(dashboardType)){
			list.add("e-Invoice");
			list.add("GENERATE");
			list.add("CANCEL");
			list.add("GETIRN");
			list.add("GSTNDETAILS");
			list.add("GENERATE_EWAYBILL");
			list.add("CANCEL_EWAYBILL");
			list.add("SYNC_GSTIN_FROMCP");
			list.add("GETEWAYBILLIRN");
			list.add("GETIRNBYDOCDETAILS");
			list.add("HEALTH");
			list.add("generateQrCode");
			query.addCriteria(Criteria.where("stage").is(MasterGSTConstants.PRODUCTION));
			query.addCriteria(Criteria.where("type").nin(list));
			gstewayUsername = "GST User Name";
		}else if("eway".equals(dashboardType)){
			query.addCriteria(Criteria.where("stage").is(MasterGSTConstants.EWAYBILLPRODUCTION));
			query.addCriteria(Criteria.where("type").in(list));
			gstewayUsername = "e-Way Bill User Name";
		}else if("einv".equals(dashboardType)) {
			list=new ArrayList<String>();
			list.add("e-Invoice");
			list.add("GENERATE");
			list.add("CANCEL");
			list.add("GETIRN");
			list.add("GSTNDETAILS");
			list.add("GENERATE_EWAYBILL");
			list.add("CANCEL_EWAYBILL");
			list.add("SYNC_GSTIN_FROMCP");
			list.add("GETEWAYBILLIRN");
			list.add("GETIRNBYDOCDETAILS");
			list.add("HEALTH");
			list.add("generateQrCode");
			query.addCriteria(Criteria.where("stage").is(MasterGSTConstants.EINVOICE_PRODUCTION));
			query.addCriteria(Criteria.where("type").in(list));
			gstewayUsername = "e-Invoice User Name";
		}
		
		query.addCriteria(criteria);
		int length = 10000;
		
		OutputStream nout = null;
		ZipOutputStream zipOutputStream = null;
		try {
			nout = response.getOutputStream();
			month=month+1;
			String fileName = "MGST_"+month+year+".zip";
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
			response.setContentType("application/octet-stream; charset=utf-8");
			zipOutputStream = new ZipOutputStream(nout);
			List<String> headers = Arrays.asList(gstewayUsername, "IP Address", "Type", "Service Name", "Status", "Size","Cost","Start Time");
			int c =0;
			for(int z=0;z< Integer.MAX_VALUE;z++) {
				Pageable pageable = new PageRequest(z, length);
				query.with(pageable);
				List<Metering> metering  = mongoTemplate.find(query, Metering.class);
				
				if(NullUtil.isEmpty(metering)) {
					break;
				}
				List<MeteringVo> meteringVOList=Lists.newArrayList();
				if(NullUtil.isNotEmpty(metering) && NullUtil.isNotEmpty(metering)){
					for(Metering meter : metering){
						meter.setMeterid(meter.getId().toString());
						if("Fail".equals(meter.getStatus()) && NullUtil.isNotEmpty(meter.getErrorMessage())){
							meter.setCost("0.0");
						}else{
							meter.setCost(cost);
						}
						MeteringVo meteringvo = new MeteringVo();
						if(NullUtil.isNotEmpty(meter.getGstnusername())) {
							meteringvo.setGstnusername(meter.getGstnusername());
						}else {
							meteringvo.setGstnusername("");
						}
						if(NullUtil.isNotEmpty(meter.getIpusr())) {
							meteringvo.setIpusr(meter.getIpusr());
						}else {
							meteringvo.setIpusr("");
						}
						if(NullUtil.isNotEmpty(meter.getType())) {
							meteringvo.setType(meter.getType());
						}else {
							meteringvo.setType("");
						}
						if(NullUtil.isNotEmpty(meter.getServicename())) {
							meteringvo.setServicename(meter.getServicename());
						}else {
							meteringvo.setServicename("");
						}
						if(NullUtil.isNotEmpty(meter.getStatus())) {
							meteringvo.setStatus(meter.getStatus());
						}else {
							meteringvo.setStatus("");
						}
						if(NullUtil.isNotEmpty(meter.getSize())) {
							meteringvo.setSize(meter.getSize()+"");
						}else {
							meteringvo.setSize("");
						}
						if(NullUtil.isNotEmpty(meter.getCost())) {
							meteringvo.setCost(meter.getCost());
						}else {
							meteringvo.setCost("");
						}
						if(NullUtil.isNotEmpty(meter.getStarttime())) {
							Date currentDate = new Date(meter.getStarttime());
							DateFormat df = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss a");
							meteringvo.setStarttime(df.format(currentDate));
						}else {
							meteringvo.setStarttime("");
						}
						meteringVOList.add(meteringvo);
					}
				}
				byte[] buf = new byte[1024];
				int len = 0;
				double i = ((double)meteringVOList.size())/60000;
				int j = (int)i;
				if(i-(int)i > 0) {
					j = (int)i+1;
				}
				List<List<MeteringVo>> lt = Lists.newArrayList();
				int a=0;
				int b = 60000;
				if(meteringVOList.size() < 60000) {
					b= meteringVOList.size();
				}
				for(int k = 1; k <= j;k++) {
					lt.add(meteringVOList.subList(a, b));
					a = b;
					if(k == j-1) {
						b = meteringVOList.size();
					}else {
						b = b+60000;
					}
				}				
				for(List<MeteringVo> InvoicesList: lt) {
					File file = new File("API_Metering"+(c+1)+ ".xls");
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					SimpleExporter exporter = new SimpleExporter();
					exporter.gridExport(headers, InvoicesList,"gstnusername, ipusr, type, servicename,status,size,cost,starttime",fos);
					String fname = file.getName();
					FileInputStream fileInputStream = new FileInputStream(file);
					zipOutputStream.putNextEntry(new ZipEntry(fname));
					while((len=fileInputStream.read(buf)) >0){
						zipOutputStream.write(buf, 0, len);
					}
					 				//shut down; 
					zipOutputStream.closeEntry();
					if(isNotEmpty(fileInputStream)){
						fileInputStream.close();
					}
					file.delete();
					c++;
			    }
			}
				nout.flush();  
			    zipOutputStream.flush();
			} catch (IOException e) {
				logger.error(CLASSNAME + "downloadSupplierExcelData : ERROR", e);
			}finally {
				try {
					if (isNotEmpty(zipOutputStream)) {
						zipOutputStream.close();
					}	
					if (isNotEmpty(nout)) {
						nout.close();
					}				
				} catch (IOException e) {
				e.printStackTrace();
				}
			}
		}
	
	@RequestMapping(value = "/gstr2bmultimonthdwnldxlsyearlyn/{id}/{clientid}/{returntype}/{year}/{dwnldxlsyearlytype}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody ResponseEntity<InputStreamResource> gstr2bMultiMonthDownloadExcelDataByYearly(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype, 
			@PathVariable("year") int year,@PathVariable("dwnldxlsyearlytype") String dwnldxlsyearlytype, HttpServletRequest request)throws IOException {
		Client client = clientService.findById(clientid);
		List<StateConfig> states = configService.getStates();
		String gstnumber = "";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
		}
			
		List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,MasterGSTConstants.ISD,MasterGSTConstants.IMP_GOODS,MasterGSTConstants.IMP_SERVICES);
		List<String> ammendedInvTypes = Arrays.asList(MasterGSTConstants.B2BA, MasterGSTConstants.CDNA, MasterGSTConstants.ISDA);
		Page<? extends InvoiceParent> invoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", invTypes,clientService.invoiceFilter(request));
		Page<? extends InvoiceParent> ammendedinvoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", ammendedInvTypes,clientService.invoiceFilter(request));
		
		
		List<InvoiceVO> invoiceVOList =null;
		List<InvoiceVO> ammendedInvoiceVOList =null;
		if("itemwise".equalsIgnoreCase(dwnldxlsyearlytype)) {
			invoiceVOList = invoiceService.invoiceListItems(invoices,returntype,states,client);
			ammendedInvoiceVOList = invoiceService.invoiceListItems(ammendedinvoices,returntype,states,client);
		}else if("invoicewise".equalsIgnoreCase(dwnldxlsyearlytype)){
			invoiceVOList = invoiceService.getInvoice_Wise_List(invoices,returntype,states,client);
			ammendedInvoiceVOList = invoiceService.getInvoice_Wise_List(ammendedinvoices,returntype,states,client);
		}
		List<String> headers = null;
		if("itemwise".equalsIgnoreCase(dwnldxlsyearlytype)) {
			headers = Arrays.asList("Invoice Date", "Invoice No", "GSTIN", "Place Of Supply", "Supplier Name","CompanyGSTIN","CompanySateName","Counter Party Filing Status", "OriginalInvNo", "OriginalInvDate", "Return Period","Reverse Charge",
					"Invoice Type", "State","Item Name","Item Notes","HSN / SAC Code","Unique Quantity Code","Quantity","Rate", "Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount",
					"SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","ITC Type", "ITC Available","Total Tax","Total Invoice Value");				
		}else if("invoicewise".equalsIgnoreCase(dwnldxlsyearlytype)){
			headers = Arrays.asList("Invoice Date", "Invoice No", "GSTIN", "Place Of Supply","Supplier Name","CompanyGSTIN","CompanySateName","Counter Party Filing Status", "OriginalInvNo","OriginalInvDate","Return Period","Reverse Charge",
					"Invoice Type", "State","Branch", "Taxable Value", "IGST Amount", "CGST Amount",
					"SGST Amount", "CESS Amount", "ITC Available","Total Tax","Total Invoice Value");	
		}
		ByteArrayInputStream in = invoiceService.gstr2aToExcel(invoiceVOList, ammendedInvoiceVOList, headers, dwnldxlsyearlytype);
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Disposition", "attachment; filename='MGST_"+returntype+"_"+gstnumber+"_"+year+".xlsx");
		return ResponseEntity.ok().headers(header).body(new InputStreamResource(in));
	}
	
	@RequestMapping(value = "/monthgstr2bfulldwnldyearlyxlsnewa/{id}/{clientid}/{returntype}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody ResponseEntity<InputStreamResource> getInvoiceGSTR2bYearlyFullReport(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype, 
			@PathVariable("year") int year, HttpServletRequest request) throws IOException {
		final String method = "getInvoiceFullReport::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String customField1 = "CustomField1";
		String customField2 = "CustomField2";
		String customField3 = "CustomField3";
		String customField4 = "CustomField4";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
			if(isNotEmpty(customFields)) {
				if("GSTR1".equals(returntype) || "SalesRegister".equals(returntype)) {
					if(isNotEmpty(customFields.getSales())) {
						int i=1;
						for(CustomData customdata : customFields.getSales()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(GSTR2.equals(returntype) || GSTR2A.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype) || "GSTR2B".equals(returntype)){
					if(isNotEmpty(customFields.getPurchase())) {
						int i=1;
						for(CustomData customdata : customFields.getPurchase()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(returntype.equals(MasterGSTConstants.EINVOICE)){
					if(isNotEmpty(customFields.getEinvoice())) {
						int i=1;
						for(CustomData customdata : customFields.getEinvoice()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}
			}
			
		}
		List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,MasterGSTConstants.ISD,MasterGSTConstants.IMP_GOODS,MasterGSTConstants.IMP_SERVICES);
		List<String> ammendedInvTypes = Arrays.asList(MasterGSTConstants.B2BA, MasterGSTConstants.CDNA, MasterGSTConstants.ISDA);
		Page<? extends InvoiceParent> invoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", invTypes,clientService.invoiceFilter(request));
		Page<? extends InvoiceParent> ammendedinvoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", ammendedInvTypes,clientService.invoiceFilter(request));
		List<InvoiceVO> invoiceVOList =clientReportsUtil.invoiceListItems(invoices,returntype);
		List<InvoiceVO> ammendedInvoiceVOList =clientReportsUtil.invoiceListItems(ammendedinvoices,returntype);
		
		
		logger.debug(invoiceVOList);
		List<String> headers=null;
		
		headers=Arrays.asList("Invoice Date", "Invoice No","Invoice Type","Return Period", "Reverse Charge","GSTIN", "Place Of Supply", "Supplier Name", "CompanyStateName", "Counter Party Filing Status","Ecommerce GSTIN", "Billing Address","Shipment Address",
				"Original Invoice Number","Original Invoice Date","Eway Bill Number", "Ledger", "State","Reverse Charge","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage",
				"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Supplier Notes",customField1,customField2,customField3,customField4,"Terms & Conditions","Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
				"Rate","Discount","Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","ITC Type", "ITC Available","Total Tax","Total Invoice Value","TDS Amount","Net Receivable(Total Invoice Value - TDS Amount)");
		
		ByteArrayInputStream in = invoiceService.gstr2aToExcel(invoiceVOList, ammendedInvoiceVOList, headers,"fulldetails");
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Disposition", "attachment; filename='MGST_"+returntype+"_"+gstnumber+"_"+year+".xlsx");
		return ResponseEntity.ok().headers(header).body(new InputStreamResource(in));
	}
	
	
	@RequestMapping(value = "/monthgstr2bfulldwnldyearlyxlsnew/{id}/{clientid}/{returntype}/{year}", method = RequestMethod.GET)
	public void getInvoiceGSTR2bYearlyFullReports(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype, 
			@PathVariable("year") int year, HttpServletRequest request,HttpServletResponse response) throws IOException {
		final String method = "getInvoiceFullReport::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String customField1 = "CustomField1";
		String customField2 = "CustomField2";
		String customField3 = "CustomField3";
		String customField4 = "CustomField4";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
			if(isNotEmpty(customFields)) {
				if("GSTR1".equals(returntype) || "SalesRegister".equals(returntype)) {
					if(isNotEmpty(customFields.getSales())) {
						int i=1;
						for(CustomData customdata : customFields.getSales()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(GSTR2.equals(returntype) || GSTR2A.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype) || "GSTR2B".equals(returntype)){
					if(isNotEmpty(customFields.getPurchase())) {
						int i=1;
						for(CustomData customdata : customFields.getPurchase()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(returntype.equals(MasterGSTConstants.EINVOICE)){
					if(isNotEmpty(customFields.getEinvoice())) {
						int i=1;
						for(CustomData customdata : customFields.getEinvoice()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}
			}
			
		}
		List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,MasterGSTConstants.ISD,MasterGSTConstants.IMP_GOODS,MasterGSTConstants.IMP_SERVICES);
		List<String> ammendedInvTypes = Arrays.asList(MasterGSTConstants.B2BA, MasterGSTConstants.CDNA, MasterGSTConstants.ISDA);
		Page<? extends InvoiceParent> invoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", invTypes,clientService.invoiceFilter(request));
		Page<? extends InvoiceParent> ammendedinvoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", ammendedInvTypes,clientService.invoiceFilter(request));
		List<InvoiceVO> invoiceVOList =clientReportsUtil.invoiceListItems(invoices,returntype);
		List<InvoiceVO> ammendedInvoiceVOList =clientReportsUtil.invoiceListItems(ammendedinvoices,returntype);
		List<String> headers=null;
		headers=Arrays.asList("Invoice Date", "Invoice No","Invoice Status","Invoice Type","Return Period","Reverse Charge", "GSTIN", "Customer Name", "CompanyStateName", "Ecommerce GSTIN", "Billing Address","Shipment Address",
				"Eway Bill Number", "Ledger", "Place Of Supply","Reverse Charge","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage","Port Code","Shipping Bill No","Shipping Bill Date","Additional Currency Code","Exchange Rate",
				"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Customer Notes","Terms & Conditions","Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
				"Rate","Discount","Exempted", "Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","Total Tax","Total Invoice Value","TCS Amount","Net Receivable(Total Invoice Value + TCS Amount)","Total Currency Value",customField1,customField2,customField3,customField4);
		OutputStream nout = null;
		ZipOutputStream zipOutputStream = null;
		try {
			nout = response.getOutputStream();
			String fileName = "MGST_" + returntype + "_" +gstnumber+"_"+ year + ".zip";
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
			response.setContentType("application/octet-stream; charset=utf-8");
			zipOutputStream = new ZipOutputStream(nout);
			byte[] buf = new byte[1024];
			int c=0;
			int len = 0;
			double i = ((double)invoiceVOList.size())/60000;
			int j = (int)i;
			if(i-(int)i > 0) {
				j = (int)i+1;
			}
			List<List<InvoiceVO>> lt = Lists.newArrayList();
			int a=0;
			int b = 60000;
			if(invoiceVOList.size() < 60000) {
				b= invoiceVOList.size();
			}
			for(int k = 1; k <= j;k++) {
				lt.add(invoiceVOList.subList(a, b));
				a = b;
				if(k == j-1) {
					b = invoiceVOList.size();
				}else {
					b = b+60000;
				}
			}
			for(List<InvoiceVO> InvoicesList: lt) {
				File file1 = new File("MGST_"+returntype+"_"+gstnumber+"_"+year+"_"+(c+1)+ ".xls");
				file1.createNewFile();
				FileOutputStream fos = new FileOutputStream(file1);
				SimpleExporter exporter = new SimpleExporter();
				exporter.gridExport(headers, InvoicesList,"invoiceDate, invoiceNo,gstStatus,type,returnPeriod,recharge, customerGSTIN, customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber, ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount,itemExmepted, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal,customField1,customField2,customField3,customField4",fos);
				String fname = file1.getName();
				FileInputStream fileInputStream = new FileInputStream(file1);
				zipOutputStream.putNextEntry(new ZipEntry(fname));
				while((len=fileInputStream.read(buf)) >0){
					zipOutputStream.write(buf, 0, len);
				}
				 				//shut down; 
				zipOutputStream.closeEntry();
				if(isNotEmpty(fileInputStream)){
					fileInputStream.close();
				}
				file1.delete();
				c++;
		    }
			byte[] buf1 = new byte[1024];
			int da=0;
			int lenn = 0;
			double ia = ((double)ammendedInvoiceVOList.size())/60000;
			int ja = (int)ia;
			if(ia-(int)ia > 0) {
				ja = (int)ia+1;
			}
			List<List<InvoiceVO>> lta = Lists.newArrayList();
			int aa=0;
			int ba = 60000;
			if(ammendedInvoiceVOList.size() < 60000) {
				ba= ammendedInvoiceVOList.size();
			}
			for(int k = 1; k <= j;k++) {
				lta.add(ammendedInvoiceVOList.subList(aa, ba));
				aa = ba;
				if(k == ja-1) {
					ba = ammendedInvoiceVOList.size();
				}else {
					ba = ba+60000;
				}
			}
			for(List<InvoiceVO> InvoicesList: lta) {
				File file1 = new File("MGST_"+returntype+"_Ammended_"+gstnumber+"_"+year+"_"+(da+1)+ ".xls");
				file1.createNewFile();
				FileOutputStream fos = new FileOutputStream(file1);
				SimpleExporter exporter = new SimpleExporter();
				exporter.gridExport(headers, InvoicesList,"invoiceDate, invoiceNo,gstStatus,type,returnPeriod,recharge, customerGSTIN, customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber, ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount,itemExmepted, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal,customField1,customField2,customField3,customField4",fos);
				String fname = file1.getName();
				FileInputStream fileInputStream = new FileInputStream(file1);
				zipOutputStream.putNextEntry(new ZipEntry(fname));
				while((lenn=fileInputStream.read(buf1)) >0){
					zipOutputStream.write(buf1, 0, lenn);
				}
				 				//shut down; 
				zipOutputStream.closeEntry();
				if(isNotEmpty(fileInputStream)){
					fileInputStream.close();
				}
				file1.delete();
				da++;
		    }
		} catch (IOException e) {
			logger.error(CLASSNAME + "downloadSupplierExcelData : ERROR", e);
		}finally {
			try {
				if (isNotEmpty(zipOutputStream)) {
					zipOutputStream.close();
				}	
				if (isNotEmpty(nout)) {
					nout.close();
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	@RequestMapping(value = "/monthgstr1fulldwnldyearlyxlsnew/{id}/{clientid}/{returntype}/{year}", method = RequestMethod.GET)
	public void getInvoiceGSTR1YearlyFullReportn(@PathVariable("id") String id,@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype, @PathVariable("year") int year, HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String method = "getInvoiceFullReport::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String customField1 = "CustomField1";
		String customField2 = "CustomField2";
		String customField3 = "CustomField3";
		String customField4 = "CustomField4";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
			if(isNotEmpty(customFields)) {
				if("GSTR1".equals(returntype) || "SalesRegister".equals(returntype)) {
					if(isNotEmpty(customFields.getSales())) {
						int i=1;
						for(CustomData customdata : customFields.getSales()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(GSTR2.equals(returntype) || GSTR2A.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
					if(isNotEmpty(customFields.getPurchase())) {
						int i=1;
						for(CustomData customdata : customFields.getPurchase()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(returntype.equals(MasterGSTConstants.EINVOICE)){
					if(isNotEmpty(customFields.getEinvoice())) {
						int i=1;
						for(CustomData customdata : customFields.getEinvoice()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}
			}
			
		}
		List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,MasterGSTConstants.ISD,MasterGSTConstants.IMP_GOODS,MasterGSTConstants.IMP_SERVICES);
		List<String> ammendedInvTypes = Arrays.asList(MasterGSTConstants.B2BA, MasterGSTConstants.CDNA);
		Page<? extends InvoiceParent> invoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", invTypes,clientService.invoiceFilter(request));
		Page<? extends InvoiceParent> ammendedinvoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", ammendedInvTypes,clientService.invoiceFilter(request));
		if(GSTR2A.equals(returntype)) { returntype = GSTR2; }
		List<InvoiceVO> invoiceVOList =clientReportsUtil.invoiceListItems(invoices,returntype);
		List<InvoiceVO> ammendedInvoiceVOList =clientReportsUtil.invoiceListItems(ammendedinvoices,returntype);
		List<String> headers=null;
			headers=Arrays.asList("Invoice Date", "Invoice No","Invoice Type","Return Period", "GSTIN", "Place Of Supply", "Supplier Name", "CompanyStateName", "Counter Party Filing Status","Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Original Invoice Number","Original Invoice Date","Eway Bill Number", "Ledger", "State","Reverse Charge","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Supplier Notes","Terms & Conditions","Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
					"Rate","Discount","Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","Total Tax","Total Invoice Value","TDS Amount","Net Receivable(Total Invoice Value - TDS Amount)",customField1,customField2,customField3,customField4);
			OutputStream nout = null;
			ZipOutputStream zipOutputStream = null;
			try {
				nout = response.getOutputStream();
				String fileName = "MGST_" + returntype + "_" +gstnumber+"_"+ year + ".zip";
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
				response.setContentType("application/octet-stream; charset=utf-8");
				zipOutputStream = new ZipOutputStream(nout);
				byte[] buf = new byte[1024];
				int c=0;
				int len = 0;
				double i = ((double)invoiceVOList.size())/60000;
				int j = (int)i;
				if(i-(int)i > 0) {
					j = (int)i+1;
				}
				List<List<InvoiceVO>> lt = Lists.newArrayList();
				int a=0;
				int b = 60000;
				if(invoiceVOList.size() < 60000) {
					b= invoiceVOList.size();
				}
				for(int k = 1; k <= j;k++) {
					lt.add(invoiceVOList.subList(a, b));
					a = b;
					if(k == j-1) {
						b = invoiceVOList.size();
					}else {
						b = b+60000;
					}
				}
				for(List<InvoiceVO> InvoicesList: lt) {
					File file1 = new File("MGST_"+returntype+"_"+gstnumber+"_"+year+"_"+(c+1)+ ".xls");
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					SimpleExporter exporter = new SimpleExporter();
					//exporter.gridExport(headers, InvoicesList,"invoiceDate, invoiceNo,gstStatus,type,returnPeriod,recharge, customerGSTIN, customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber, ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount,itemExmepted, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal,customField1,customField2,customField3,customField4",fos);
					exporter.gridExport(headers, InvoicesList,"invoiceDate, invoiceNo,type,returnPeriod, customerGSTIN,placeOfSupply, customerName, companyStatename,counterFilingStatus,ecommerceGSTIN,billingAddress,shipingAddress,originalInvoiceNo,originalInvoiceDate,ewayBillNumber, ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue,tcsAmount,tcsNetAmount,customField1,customField2,customField3,customField4",fos);
					String fname = file1.getName();
					FileInputStream fileInputStream = new FileInputStream(file1);
					zipOutputStream.putNextEntry(new ZipEntry(fname));
					while((len=fileInputStream.read(buf)) >0){
						zipOutputStream.write(buf, 0, len);
					}
					 				//shut down; 
					zipOutputStream.closeEntry();
					if(isNotEmpty(fileInputStream)){
						fileInputStream.close();
					}
					file1.delete();
					c++;
			    }
				byte[] buf1 = new byte[1024];
				int da=0;
				int lenn = 0;
				double ia = ((double)ammendedInvoiceVOList.size())/60000;
				int ja = (int)ia;
				if(ia-(int)ia > 0) {
					ja = (int)ia+1;
				}
				List<List<InvoiceVO>> lta = Lists.newArrayList();
				int aa=0;
				int ba = 60000;
				if(ammendedInvoiceVOList.size() < 60000) {
					ba= ammendedInvoiceVOList.size();
				}
				for(int k = 1; k <= j;k++) {
					lta.add(ammendedInvoiceVOList.subList(aa, ba));
					aa = ba;
					if(k == ja-1) {
						ba = ammendedInvoiceVOList.size();
					}else {
						ba = ba+60000;
					}
				}
				for(List<InvoiceVO> InvoicesList: lta) {
					File file1 = new File("MGST_"+returntype+"_Ammended_"+gstnumber+"_"+year+"_"+(da+1)+ ".xls");
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					SimpleExporter exporter = new SimpleExporter();
					exporter.gridExport(headers, InvoicesList,"invoiceDate, invoiceNo,type,returnPeriod, customerGSTIN,placeOfSupply, customerName, companyStatename,counterFilingStatus,ecommerceGSTIN,billingAddress,shipingAddress,originalInvoiceNo,originalInvoiceDate,ewayBillNumber, ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue,tcsAmount,tcsNetAmount,customField1,customField2,customField3,customField4",fos);
					String fname = file1.getName();
					FileInputStream fileInputStream = new FileInputStream(file1);
					zipOutputStream.putNextEntry(new ZipEntry(fname));
					while((lenn=fileInputStream.read(buf1)) >0){
						zipOutputStream.write(buf1, 0, lenn);
					}
					 				//shut down; 
					zipOutputStream.closeEntry();
					if(isNotEmpty(fileInputStream)){
						fileInputStream.close();
					}
					file1.delete();
					da++;
			    }
			} catch (IOException e) {
				logger.error(CLASSNAME + "downloadSupplierExcelData : ERROR", e);
			}finally {
				try {
					if (isNotEmpty(zipOutputStream)) {
						zipOutputStream.close();
					}	
					if (isNotEmpty(nout)) {
						nout.close();
					}				
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}
	
	@RequestMapping(value = "/gstr2bmultimonthdwnldxlsyearly/{id}/{clientid}/{returntype}/{year}/{dwnldxlsyearlytype}", method = RequestMethod.GET)
	public void gstr2bMultiMonthDownloadExcelDataByYearlynew(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype, 
			@PathVariable("year") int year,@PathVariable("dwnldxlsyearlytype") String dwnldxlsyearlytype, HttpServletRequest request,HttpServletResponse response)throws IOException {
		Client client = clientService.findById(clientid);
		List<StateConfig> states = configService.getStates();
		String gstnumber = "";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
		}
			
		List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,MasterGSTConstants.ISD,MasterGSTConstants.IMP_GOODS,MasterGSTConstants.IMP_SERVICES);
		List<String> ammendedInvTypes = Arrays.asList(MasterGSTConstants.B2BA, MasterGSTConstants.CDNA, MasterGSTConstants.ISDA);
		Page<? extends InvoiceParent> invoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", invTypes,clientService.invoiceFilter(request));
		Page<? extends InvoiceParent> ammendedinvoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", ammendedInvTypes,clientService.invoiceFilter(request));
		
		
		List<InvoiceVO> invoiceVOList =null;
		List<InvoiceVO> ammendedInvoiceVOList =null;
		if("itemwise".equalsIgnoreCase(dwnldxlsyearlytype)) {
			invoiceVOList = invoiceService.invoiceListItems(invoices,returntype,states,client);
			ammendedInvoiceVOList = invoiceService.invoiceListItems(ammendedinvoices,returntype,states,client);
		}else if("invoicewise".equalsIgnoreCase(dwnldxlsyearlytype)){
			invoiceVOList = invoiceService.getInvoice_Wise_List(invoices,returntype,states,client);
			ammendedInvoiceVOList = invoiceService.getInvoice_Wise_List(ammendedinvoices,returntype,states,client);
		}
		List<String> headers = null;
		if("itemwise".equalsIgnoreCase(dwnldxlsyearlytype)) {
			headers = Arrays.asList("Invoice Date", "Invoice No", "GSTIN", "Place Of Supply", "Supplier Name","CompanyGSTIN","CompanySateName","Counter Party Filing Status", "OriginalInvNo", "OriginalInvDate", "Return Period","Reverse Charge",
					"Invoice Type", "State","Item Name","Item Notes","HSN / SAC Code","Unique Quantity Code","Quantity","Rate", "Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount",
					"SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","ITC Type", "ITC Available","Total Tax","Total Invoice Value");				
		}else if("invoicewise".equalsIgnoreCase(dwnldxlsyearlytype)){
			headers = Arrays.asList("Invoice Date", "Invoice No", "GSTIN", "Place Of Supply","Supplier Name","CompanyGSTIN","CompanySateName","Counter Party Filing Status", "OriginalInvNo","OriginalInvDate","Return Period","Reverse Charge",
					"Invoice Type", "State","Branch", "Taxable Value", "IGST Amount", "CGST Amount",	"SGST Amount", "CESS Amount", "ITC Available","Total Tax","Total Invoice Value");	
		}
		OutputStream nout = null;
		ZipOutputStream zipOutputStream = null;
		try {
			nout = response.getOutputStream();
			String fileName = "MGST_" + returntype + "_" +gstnumber+"_"+ year + ".zip";
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
			response.setContentType("application/octet-stream; charset=utf-8");
			zipOutputStream = new ZipOutputStream(nout);
			byte[] buf = new byte[1024];
			int c=0;
			int len = 0;
			double i = ((double)invoiceVOList.size())/60000;
			int j = (int)i;
			if(i-(int)i > 0) {
				j = (int)i+1;
			}
			List<List<InvoiceVO>> lt = Lists.newArrayList();
			int a=0;
			int b = 60000;
			if(invoiceVOList.size() < 60000) {
				b= invoiceVOList.size();
			}
			for(int k = 1; k <= j;k++) {
				lt.add(invoiceVOList.subList(a, b));
				a = b;
				if(k == j-1) {
					b = invoiceVOList.size();
				}else {
					b = b+60000;
				}
			}
			for(List<InvoiceVO> InvoicesList: lt) {
				File file1 = new File("MGST_"+returntype+"_"+gstnumber+"_"+year+"_"+(c+1)+ ".xls");
				file1.createNewFile();
				FileOutputStream fos = new FileOutputStream(file1);
				SimpleExporter exporter = new SimpleExporter();
				if("itemwise".equalsIgnoreCase(dwnldxlsyearlytype)) {
					exporter.gridExport(headers, InvoicesList,"invoiceDate, invoiceNo, customerGSTIN,placeOfSupply,customerName,companyGSTIN,companyStatename,counterFilingStatus,originalInvoiceNo,originalInvoiceDate,returnPeriod,recharge, type, state,itemname,itemNotesComments,hsnCode,uqc,quantity,rateperitem, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, itcType, totalItc,totaltax,totalValue",fos);
				}else if("invoicewise".equalsIgnoreCase(dwnldxlsyearlytype)){
					exporter.gridExport(headers, InvoicesList,"invoiceDate, invoiceNo, customerGSTIN,placeOfSupply,customerName,companyGSTIN,companyStatename,counterFilingStatus, originalInvoiceNo,originalInvoiceDate,returnPeriod, recharge,type, state,branch, taxableValue, igstAmount, cgstAmount,  sgstAmount,  cessAmount, totalItc, totaltax,totalValue",fos);															
				}
				String fname = file1.getName();
				FileInputStream fileInputStream = new FileInputStream(file1);
				zipOutputStream.putNextEntry(new ZipEntry(fname));
				while((len=fileInputStream.read(buf)) >0){
					zipOutputStream.write(buf, 0, len);
				}
				 				//shut down; 
				zipOutputStream.closeEntry();
				if(isNotEmpty(fileInputStream)){
					fileInputStream.close();
				}
				file1.delete();
				c++;
		    }
			byte[] buf1 = new byte[1024];
			int da=0;
			int lenn = 0;
			double ia = ((double)ammendedInvoiceVOList.size())/60000;
			int ja = (int)ia;
			if(ia-(int)ia > 0) {
				ja = (int)ia+1;
			}
			List<List<InvoiceVO>> lta = Lists.newArrayList();
			int aa=0;
			int ba = 60000;
			if(ammendedInvoiceVOList.size() < 60000) {
				ba= ammendedInvoiceVOList.size();
			}
			for(int k = 1; k <= j;k++) {
				lta.add(ammendedInvoiceVOList.subList(aa, ba));
				aa = ba;
				if(k == ja-1) {
					ba = ammendedInvoiceVOList.size();
				}else {
					ba = ba+60000;
				}
			}
			for(List<InvoiceVO> InvoicesList: lta) {
				File file1 = new File("MGST_"+returntype+"_Ammended_"+gstnumber+"_"+year+"_"+(da+1)+ ".xls");
				file1.createNewFile();
				FileOutputStream fos = new FileOutputStream(file1);
				SimpleExporter exporter = new SimpleExporter();
				if("itemwise".equalsIgnoreCase(dwnldxlsyearlytype)) {
					exporter.gridExport(headers, InvoicesList,"invoiceDate, invoiceNo, customerGSTIN,placeOfSupply,customerName,companyGSTIN,companyStatename,counterFilingStatus,originalInvoiceNo,originalInvoiceDate,returnPeriod,recharge, type, state,itemname,itemNotesComments,hsnCode,uqc,quantity,rateperitem, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, itcType, totalItc,totaltax,totalValue",fos);
				}else if("invoicewise".equalsIgnoreCase(dwnldxlsyearlytype)){
					exporter.gridExport(headers, InvoicesList,"invoiceDate, invoiceNo, customerGSTIN,placeOfSupply,customerName,companyGSTIN,companyStatename,counterFilingStatus, originalInvoiceNo,originalInvoiceDate,returnPeriod, recharge,type, state,branch, taxableValue, igstAmount, cgstAmount,  sgstAmount,  cessAmount, totalItc, totaltax,totalValue",fos);															
				}
				String fname = file1.getName();
				FileInputStream fileInputStream = new FileInputStream(file1);
				zipOutputStream.putNextEntry(new ZipEntry(fname));
				while((lenn=fileInputStream.read(buf1)) >0){
					zipOutputStream.write(buf1, 0, lenn);
				}
				 				//shut down; 
				zipOutputStream.closeEntry();
				if(isNotEmpty(fileInputStream)){
					fileInputStream.close();
				}
				file1.delete();
				da++;
		    }
		} catch (IOException e) {
			logger.error(CLASSNAME + "downloadSupplierExcelData : ERROR", e);
		}finally {
			try {
				if (isNotEmpty(zipOutputStream)) {
					zipOutputStream.close();
				}	
				if (isNotEmpty(nout)) {
					nout.close();
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@RequestMapping(value = "/multimonthdwnldxlsyearlynew/{id}/{clientid}/{returntype}/{year}/{dwnldxlsyearlytype}", method = RequestMethod.GET)
	public void multiMonthDownloadExcelDataByYearlyn(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype, 
			@PathVariable("year") int year,@PathVariable("dwnldxlsyearlytype") String dwnldxlsyearlytype, HttpServletRequest request,HttpServletResponse response)throws IOException {
		Client client = clientService.findById(clientid);
		List<StateConfig> states = configService.getStates();
		String gstnumber = "";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
		}
		List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,MasterGSTConstants.ISD,MasterGSTConstants.IMP_GOODS,MasterGSTConstants.IMP_SERVICES);
		List<String> ammendedInvTypes = Arrays.asList(MasterGSTConstants.B2BA, MasterGSTConstants.CDNA);
		Page<? extends InvoiceParent> invoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", invTypes,clientService.invoiceFilter(request));
		Page<? extends InvoiceParent> ammendedinvoices = invoiceService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", ammendedInvTypes,clientService.invoiceFilter(request));
		if(GSTR2A.equals(returntype)) { returntype = GSTR2; }
		List<InvoiceVO> invoiceVOList =null;
		List<InvoiceVO> ammendedInvoiceVOList =null;
		if("itemwise".equalsIgnoreCase(dwnldxlsyearlytype)) {
			invoiceVOList = invoiceService.invoiceListItems(invoices,returntype,states,client);
			ammendedInvoiceVOList = invoiceService.invoiceListItems(ammendedinvoices,returntype,states,client);
		}else if("invoicewise".equalsIgnoreCase(dwnldxlsyearlytype)){
			invoiceVOList = invoiceService.getInvoice_Wise_List(invoices,returntype,states,client);
			ammendedInvoiceVOList = invoiceService.getInvoice_Wise_List(ammendedinvoices,returntype,states,client);
		}
			List<String> headers = null;
				if("itemwise".equalsIgnoreCase(dwnldxlsyearlytype)) {
					headers = Arrays.asList("Invoice Date", "Invoice No", "GSTIN", "Place Of Supply", "Supplier Name","CompanyGSTIN","CompanySateName","Counter Party Filing Status", "OriginalInvNo", "OriginalInvDate", "Return Period","Reverse Charge",
							"Invoice Type", "State","Item Name","Item Notes","HSN / SAC Code","Unique Quantity Code","Quantity","Rate", "Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount",
							"SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","ITC Type", "ITC Available","Total Tax","Total Invoice Value");				
				}else if("invoicewise".equalsIgnoreCase(dwnldxlsyearlytype)){
					headers = Arrays.asList("Invoice Date", "Invoice No", "GSTIN", "Place Of Supply","Supplier Name","CompanyGSTIN","CompanySateName","Counter Party Filing Status", "OriginalInvNo","OriginalInvDate","Return Period","Reverse Charge",
							"Invoice Type", "State","Branch", "Taxable Value", "IGST Amount", "CGST Amount",	"SGST Amount", "CESS Amount", "ITC Available","Total Tax","Total Invoice Value");	
				}
				OutputStream nout = null;
				ZipOutputStream zipOutputStream = null;
				try {
					nout = response.getOutputStream();
					String fileName = "MGST_" + returntype + "_" +gstnumber+"_"+ year + ".zip";
					response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
					response.setContentType("application/octet-stream; charset=utf-8");
					zipOutputStream = new ZipOutputStream(nout);
					byte[] buf = new byte[1024];
					int c=0;
					int len = 0;
					double i = ((double)invoiceVOList.size())/60000;
					int j = (int)i;
					if(i-(int)i > 0) {
						j = (int)i+1;
					}
					List<List<InvoiceVO>> lt = Lists.newArrayList();
					int a=0;
					int b = 60000;
					if(invoiceVOList.size() < 60000) {
						b= invoiceVOList.size();
					}
					for(int k = 1; k <= j;k++) {
						lt.add(invoiceVOList.subList(a, b));
						a = b;
						if(k == j-1) {
							b = invoiceVOList.size();
						}else {
							b = b+60000;
						}
					}
					for(List<InvoiceVO> InvoicesList: lt) {
						File file1 = new File("MGST_"+returntype+"_"+gstnumber+"_"+year+"_"+(c+1)+ ".xls");
						file1.createNewFile();
						FileOutputStream fos = new FileOutputStream(file1);
						SimpleExporter exporter = new SimpleExporter();
						if("itemwise".equalsIgnoreCase(dwnldxlsyearlytype)) {
							exporter.gridExport(headers, InvoicesList,"invoiceDate, invoiceNo, customerGSTIN,placeOfSupply,customerName,companyGSTIN,companyStatename,counterFilingStatus,originalInvoiceNo,originalInvoiceDate,returnPeriod,recharge, type, state,itemname,itemNotesComments,hsnCode,uqc,quantity,rateperitem, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, itcType, totalItc,totaltax,totalValue",fos);
						}else if("invoicewise".equalsIgnoreCase(dwnldxlsyearlytype)){
							exporter.gridExport(headers, InvoicesList,"invoiceDate, invoiceNo, customerGSTIN,placeOfSupply,customerName,companyGSTIN,companyStatename,counterFilingStatus, originalInvoiceNo,originalInvoiceDate,returnPeriod, recharge,type, state,branch, taxableValue, igstAmount, cgstAmount,  sgstAmount,  cessAmount, totalItc, totaltax,totalValue",fos);															
						}
						String fname = file1.getName();
						FileInputStream fileInputStream = new FileInputStream(file1);
						zipOutputStream.putNextEntry(new ZipEntry(fname));
						while((len=fileInputStream.read(buf)) >0){
							zipOutputStream.write(buf, 0, len);
						}
						 				//shut down; 
						zipOutputStream.closeEntry();
						if(isNotEmpty(fileInputStream)){
							fileInputStream.close();
						}
						file1.delete();
						c++;
				    }
					byte[] buf1 = new byte[1024];
					int da=0;
					int lenn = 0;
					double ia = ((double)ammendedInvoiceVOList.size())/60000;
					int ja = (int)ia;
					if(ia-(int)ia > 0) {
						ja = (int)ia+1;
					}
					List<List<InvoiceVO>> lta = Lists.newArrayList();
					int aa=0;
					int ba = 60000;
					if(ammendedInvoiceVOList.size() < 60000) {
						ba= ammendedInvoiceVOList.size();
					}
					for(int k = 1; k <= j;k++) {
						lta.add(ammendedInvoiceVOList.subList(aa, ba));
						aa = ba;
						if(k == ja-1) {
							ba = ammendedInvoiceVOList.size();
						}else {
							ba = ba+60000;
						}
					}
					for(List<InvoiceVO> InvoicesList: lta) {
						File file1 = new File("MGST_"+returntype+"_Ammended_"+gstnumber+"_"+year+"_"+(da+1)+ ".xls");
						file1.createNewFile();
						FileOutputStream fos = new FileOutputStream(file1);
						SimpleExporter exporter = new SimpleExporter();
						if("itemwise".equalsIgnoreCase(dwnldxlsyearlytype)) {
							exporter.gridExport(headers, InvoicesList,"invoiceDate, invoiceNo, customerGSTIN,placeOfSupply,customerName,companyGSTIN,companyStatename,counterFilingStatus,originalInvoiceNo,originalInvoiceDate,returnPeriod,recharge, type, state,itemname,itemNotesComments,hsnCode,uqc,quantity,rateperitem, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, itcType, totalItc,totaltax,totalValue",fos);
						}else if("invoicewise".equalsIgnoreCase(dwnldxlsyearlytype)){
							exporter.gridExport(headers, InvoicesList,"invoiceDate, invoiceNo, customerGSTIN,placeOfSupply,customerName,companyGSTIN,companyStatename,counterFilingStatus, originalInvoiceNo,originalInvoiceDate,returnPeriod, recharge,type, state,branch, taxableValue, igstAmount, cgstAmount,  sgstAmount,  cessAmount, totalItc, totaltax,totalValue",fos);															
						}
						String fname = file1.getName();
						FileInputStream fileInputStream = new FileInputStream(file1);
						zipOutputStream.putNextEntry(new ZipEntry(fname));
						while((lenn=fileInputStream.read(buf1)) >0){
							zipOutputStream.write(buf1, 0, lenn);
						}
						 				//shut down; 
						zipOutputStream.closeEntry();
						if(isNotEmpty(fileInputStream)){
							fileInputStream.close();
						}
						file1.delete();
						da++;
				    }
				} catch (IOException e) {
					logger.error(CLASSNAME + "downloadSupplierExcelData : ERROR", e);
				}finally {
					try {
						if (isNotEmpty(zipOutputStream)) {
							zipOutputStream.close();
						}	
						if (isNotEmpty(nout)) {
							nout.close();
						}				
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
	}
	
}
