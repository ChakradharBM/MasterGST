package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxls.template.SimpleExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.StorageCredentials;
import com.mastergst.usermanagement.runtime.repository.StorageCredentailsRepository;
import com.mastergst.usermanagement.runtime.response.InvoiceVO;
import com.mastergst.usermanagement.runtime.repository.ClientRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyUserRepository;
import com.mastergst.usermanagement.runtime.service.AcknowledgementService;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ProfileService;

@Controller
public class AcknowledgementController {
	private static final Logger logger = LogManager.getLogger(AcknowledgementController.class.getName());
	private static final String CLASSNAME = "AcknowledgementController::";
	
	@Autowired
	private UserService userService;

	@Autowired
	private ClientService clientService;
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private AcknowledgementService acknowledgementService;
	
	@Autowired
	private CompanyUserRepository CompanyUserRepository;
	@Autowired
	private StorageCredentailsRepository storageCredentailsRepository;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private ClientController clientController;
	
	@RequestMapping(value = "/getpendingacknldgeinvs/{id}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getMonthlyAndYearlyPendingInvoices(@PathVariable String id, 
			@PathVariable String returntype, @PathVariable int month, @PathVariable int year, 
			@RequestParam("pendingOrUpload") String pendingOrUpload,@RequestParam("clientids")List<String> clientids,@RequestParam("customerNames")List<String> customerNames, HttpServletRequest request) throws Exception {
		final String method = "getMonthlyPendingInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		String st = request.getParameter("start");
		String len = request.getParameter("length");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);

		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		
		InvoiceFilter filter = new InvoiceFilter();
		filter.setVendor(request.getParameter("billedtoname"));
		filter.setInvoiceType(request.getParameter("invoiceno"));
		User usr = userService.findById(id);
		CompanyUser companyuser = CompanyUserRepository.findByEmail(usr.getEmail());
		//List<String> companies = Lists.newArrayList();
		//List<String> customers = Lists.newArrayList();
		//if(isNotEmpty(companyuser.getCompany())) {
			//companies = companyuser.getCompany();
		//}
		if(NullUtil.isEmpty(customerNames)) {
			if(isNotEmpty(companyuser.getCustomer())) {
				customerNames = companyuser.getCustomer();
			}
		}
		List<String> customers = Lists.newArrayList();
		for(String customername : customerNames) {
			if(customername.contains("-mgst-")) {
				customername = customername.replaceAll("-mgst-", "&");
			}
			customers.add(customername);
		}
		
		Page<? extends InvoiceParent> invoices=acknowledgementService.getMonthlyAndYearlyAcknowledgementInvoices(id, pendingOrUpload, clientids,customers,month, year, start, length, searchVal, filter);
		if(isNotEmpty(invoices)) {
			for(InvoiceParent invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
				
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		if(returntype.equals(GSTR1)) {
			FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", SimpleBeanPropertyFilter.serializeAll());
			writer=mapper.writer(filters);
		} else {
			writer=mapper.writer();
		}
		return writer.writeValueAsString(invoices);
	}
	
	@RequestMapping(value = "/getpendingcustomacknldgeinvs/{id}/{returntype}/{fromtime}/{totime}", method = RequestMethod.GET)
	public @ResponseBody String getCustomPendingInvoices(@PathVariable String id, 
			@PathVariable String returntype, @PathVariable String fromtime,  @PathVariable String totime,
			@RequestParam("pendingOrUpload") String pendingOrUpload,@RequestParam("clientids")List<String> clientids,@RequestParam("customerNames")List<String> customerNames, HttpServletRequest request) throws Exception {
		final String method = "getMonthlyPendingInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		String st = request.getParameter("start");
		String len = request.getParameter("length");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);

		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		
		InvoiceFilter filter = new InvoiceFilter();
		filter.setVendor(request.getParameter("billedtoname"));
		filter.setInvoiceType(request.getParameter("invoiceno"));
		User usr = userService.findById(id);
		CompanyUser companyuser = CompanyUserRepository.findByEmail(usr.getEmail());
		if(NullUtil.isEmpty(customerNames)) {
			if(isNotEmpty(companyuser.getCustomer())) {
				customerNames = companyuser.getCustomer();
			}
		}
		
		List<String> customers = Lists.newArrayList();
		for(String customername : customerNames) {
			if(customername.contains("-mgst-")) {
				customername = customername.replaceAll("-mgst-", "&");
			}
			customers.add(customername);
		}
		
		Page<? extends InvoiceParent> invoices=acknowledgementService.getCustomAcknowledgementInvoices(id,  pendingOrUpload, clientids, customerNames,fromtime, totime, start, length, searchVal, filter);
		if(isNotEmpty(invoices)) {
			for(InvoiceParent invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
				
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		if(returntype.equals(GSTR1)) {
			FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", SimpleBeanPropertyFilter.serializeAll());
			writer=mapper.writer(filters);
		} else {
			writer=mapper.writer();
		}
		return writer.writeValueAsString(invoices);
	}
	
	/*@RequestMapping(value = "/getpendingAcknlowledgeInvsSupport/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getMonthlyAndYearlyPendingSupportInvoices(@PathVariable String id, @PathVariable String clientid,
			@PathVariable String returntype, @PathVariable int month, @PathVariable int year, 
			@RequestParam("pendingOrUpload") String pendingOrUpload, HttpServletRequest request) throws Exception {
		final String method = "getMonthlyPendingInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		return acknowledgementService.getMonthlyAndYearlyAcknowledgementSupportBilledNamesAndInvoicenos(id, clientid, pendingOrUpload, month, year);
	}*/
	@RequestMapping(value = "/getcustomAcknlowledgeInvsSupport/{id}/{clientid}/{returntype}/{fromtime}/{totime}", method = RequestMethod.GET)
	public @ResponseBody String getCustomPendingSupportInvoices(@PathVariable String id, @PathVariable String clientid,
			@PathVariable String returntype, @PathVariable String fromtime, @PathVariable String totime, 
			@RequestParam("pendingOrUpload") String pendingOrUpload, HttpServletRequest request) throws Exception {
		final String method = "getMonthlyPendingInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		Page<? extends InvoiceParent> invoices=acknowledgementService.getCustomAcknowledgementInvoices(id, clientid, pendingOrUpload, fromtime, totime);
		if(isNotEmpty(invoices)) {
			for(InvoiceParent invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}	
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		if(returntype.equals(GSTR1)) {
			FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", SimpleBeanPropertyFilter.serializeAll());
			writer=mapper.writer(filters);
		} else {
			writer=mapper.writer();
		}
		return writer.writeValueAsString(invoices);
	}
	
	@RequestMapping(value = "/acknldgrusers", method = RequestMethod.GET)
	public String acknowledgementUsersPage(@RequestParam String id,
			@RequestParam String fullname, @RequestParam String usertype, ModelMap model) throws Exception {
		final String method = "acknowledgementUsersPage::acknowledgementUsersPage::";
		logger.debug(CLASSNAME + method + BEGIN);
		User usr = userService.findById(id);
		model.addAttribute("id", id);
		model.addAttribute("user", usr);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		
		Calendar cal = Calendar.getInstance();
		model.addAttribute("month", cal.get(Calendar.MONTH)+1);
		model.addAttribute("year", cal.get(Calendar.YEAR));
		
		String clientid = clientService.fetchAcknowledgementClientId(id);
		model.addAttribute("isStorageCredentialsAvailable", profileService.isStorageCredentialsEnteredForClient(clientid));
		if (NullUtil.isNotEmpty(clientid)) {
			model.addAttribute("clientid", clientid);
			model.addAttribute("client",clientService.findById(clientid));
			model.addAttribute("clients", acknowledgementService.getClientsByEmail(usr.getEmail()));
			CompanyUser companyuser = CompanyUserRepository.findByEmail(usr.getEmail());
			List<String> customers = Lists.newArrayList();
			if(isNotEmpty(companyuser.getCustomer())) {
				customers = companyuser.getCustomer();
			}
			model.addAttribute("customers", customers);
		}else {
			model.addAttribute("acknowlegementAlert", "clients are not assign contact your admin");
		}
		
		logger.debug(CLASSNAME + method + END);
		return "dashboard/acknowledgement";
		//return "dashboard/attachments";
	}
	
	@RequestMapping(value = "/getUsersAcknldgeInvs/{id}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getUsersAcknowledgementInvsByMonthAndYear(@PathVariable String id, @PathVariable String returntype,
			@PathVariable int month, @PathVariable int year, @RequestParam("clientids")List<String> clientids, @RequestParam String pendingOrUpload, HttpServletRequest request) throws Exception{
		final String method = "getUsersAcknowledgementInvsByMonthAndYear::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		String st = request.getParameter("start");
		String len = request.getParameter("length");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);

		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		
		InvoiceFilter filter = new InvoiceFilter();
		filter.setVendor(request.getParameter("billedtoname"));
		filter.setInvoiceType(request.getParameter("invoiceno"));
				
		Page<? extends InvoiceParent> invoices=acknowledgementService.getUsersMonthlyAndYearlyAcknowledgementInvoices(id, clientids, pendingOrUpload, month, year, start, length, searchVal, filter);
		if(isNotEmpty(invoices)) {
			for(InvoiceParent invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
				
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		if(returntype.equals(GSTR1)) {
			FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", SimpleBeanPropertyFilter.serializeAll());
			writer=mapper.writer(filters);
		} else {
			writer=mapper.writer();
		}
		return writer.writeValueAsString(invoices);
	}
	
	@RequestMapping(value = "/getUsersCustomAcknldgeInvs/{id}/{returntype}/{fromtime}/{totime}", method = RequestMethod.GET)
	public @ResponseBody String getUsersCustomAcknowledgementInvsByMonthAndYear(@PathVariable String id, @PathVariable String returntype,
			@PathVariable String fromtime, @PathVariable String totime, @RequestParam("clientids")List<String> clientids, @RequestParam String pendingOrUpload, HttpServletRequest request) throws Exception{
		final String method = "getUsersCustomAcknowledgementInvsByMonthAndYear::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		String st = request.getParameter("start");
		String len = request.getParameter("length");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);

		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		
		InvoiceFilter filter = new InvoiceFilter();
		filter.setVendor(request.getParameter("billedtoname"));
		filter.setInvoiceType(request.getParameter("invoiceno"));
				
		Page<? extends InvoiceParent> invoices=acknowledgementService.getUsersCustomAcknowledgementInvoices(id, clientids, pendingOrUpload, fromtime, totime, start, length, searchVal, filter);
		if(isNotEmpty(invoices)) {
			for(InvoiceParent invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
				
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		if(returntype.equals(GSTR1)) {
			FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", SimpleBeanPropertyFilter.serializeAll());
			writer=mapper.writer(filters);
		} else {
			writer=mapper.writer();
		}
		return writer.writeValueAsString(invoices);
	}
	
	@RequestMapping("/updateacknwldgmntsubmissiondate/{invid}/{updatedDate}")
	public @ResponseBody String updateAcknowledgementSubmissionDate(@PathVariable String invid, @PathVariable String updatedDate) throws JsonProcessingException {
		final String method = "updateAcknowledgementSubmissionDate::";
		logger.debug(CLASSNAME + method + BEGIN);
		InvoiceParent invoice =acknowledgementService.updateAcknowledgementSubmissionDate(invid, updatedDate);

		if(isNotEmpty(invoice)) {
			invoice.setUserid(invoice.getId().toString());
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", SimpleBeanPropertyFilter.serializeAll());
		writer=mapper.writer(filters);
		return writer.writeValueAsString(invoice);
	}
	
	@RequestMapping(value = "/getStorageCredentials/{id}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getStorageCredentials(@PathVariable String id, @PathVariable String returntype,
			@PathVariable int month, @PathVariable int year, HttpServletRequest request) throws Exception{
		final String method = "getStorageCredentials::";
		logger.debug(CLASSNAME + method + BEGIN);
		String st = request.getParameter("start");
		String len = request.getParameter("length");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
	
		Page<? extends StorageCredentials> credentials=acknowledgementService.getStorageCredentials(id,month, year, start, length, searchVal);
		if(isNotEmpty(credentials)) {
			for(StorageCredentials credential : credentials) {
				Client client = clientRepository.findOne(credential.getClientId());
				if(NullUtil.isEmpty(credential.getClientName())) {
					if(isNotEmpty(client) && isNotEmpty(client.getBusinessname())) {
						credential.setClientName(client.getBusinessname());
					}
				}
				if(NullUtil.isEmpty(credential.getGroupName())) {
					if(isNotEmpty(client) && isNotEmpty(client.getGroupName())) {
						credential.setGroupName(client.getGroupName());
					}
					
				}
				credential.setUserid(credential.getId().toString());
				
			}
		}
				
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		if(returntype.equals(GSTR1)) {
			FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", SimpleBeanPropertyFilter.serializeAll());
			writer=mapper.writer(filters);
		} else {
			writer=mapper.writer();
		}
		return writer.writeValueAsString(credentials);
	}
	
	 @RequestMapping(value = "/getCredentials/{credentailId}", method = RequestMethod.GET)
		public @ResponseBody String getCredentials(@PathVariable("credentailId") String credentailId, ModelMap model) throws Exception {
			final String method = "getCredentials::";
			logger.debug(CLASSNAME + method + BEGIN);
			StorageCredentials credentials = storageCredentailsRepository.findOne(credentailId);
			if(credentials != null){
				if(NullUtil.isEmpty(credentials.getGroupName())) {
					Client client = clientRepository.findOne(credentials.getClientId());
					if(isNotEmpty(client) && isNotEmpty(client.getGroupName())) {
						credentials.setGroupName(client.getGroupName());
					}
				}
				credentials.setUserid(credentials.getId().toString());
			}
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer=mapper.writer();
			writer=mapper.writer();
			return writer.writeValueAsString(credentials);
	}
	
	@RequestMapping(value = "/getXlsUsersAcknldgeInvs/{id}/{returntype}/{month}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource getXlsUsersAcknowledgementInvsByMonthAndYear(@PathVariable String id, @PathVariable String returntype,
			@PathVariable int month, @PathVariable int year, @RequestParam("clientids")List<String> clientids, 
			@RequestParam String pendingOrUpload, HttpServletRequest request, HttpServletResponse response) throws Exception{
		final String method = "getXlsUsersAcknowledgementInvsByMonthAndYear::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		Page<? extends InvoiceParent> invoices=acknowledgementService.getUsersMonthlyAndYearlyAcknowledgementInvoices(id, clientids, pendingOrUpload, month, year, 0, -1, null, null);
		
		response.setHeader("Content-Disposition", "inline; filename='MGST_Acknowledgement_"+pendingOrUpload.toUpperCase()+"_"+month+year+".xls");
		
		List<InvoiceVO> invoiceVOList= clientController.getInvoice_Wise_List(invoices,returntype);
		

		logger.debug(invoiceVOList);
		File file = new File("MGST_Acknowledgement_"+pendingOrUpload.toUpperCase()+"_"+month+year+".xls");
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers = Arrays.asList("Invoice Date", "Invoice No","Invoice Status", "GSTIN", "Customer Name", "CompanyGSTIN","CompanyStateName", "OriginalInvNo", "OriginalInvDate", "Return Period",
							"Invoice Type", "State", "Taxable Value", "IGST Amount","CGST Amount",
							"SGST Amount", "CESS Amount", "Total Tax","Total Invoice Value");										
			
			SimpleExporter exporter = new SimpleExporter();
			exporter.gridExport(
					headers, invoiceVOList,
						"invoiceDate, invoiceNo,gstStatus, customerGSTIN, customerName, companyGSTIN,companyStatename,originalInvoiceNo,originalInvoiceDate,returnPeriod, type, state, taxableValue,  igstAmount,  cgstAmount,  sgstAmount,  cessAmount,totaltax,totalValue",
						fos);										
			
			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "getXlsUsersAcknowledgementInvsByMonthAndYear : ERROR", e);
		}
		return new FileSystemResource(new File("MGST_Acknowledgement_"+pendingOrUpload.toUpperCase()+"_"+month+year+".xls"));
	}
	
	@RequestMapping(value = "/getXlsUsersCustomAcknldgeInvs/{id}/{returntype}/{fromtime}/{totime}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource getXlsUsersCustomAcknowledgementInvsByMonthAndYear(@PathVariable String id, @PathVariable String returntype,
			@PathVariable String fromtime, @PathVariable String totime, @RequestParam("clientids")List<String> clientids,
			@RequestParam String pendingOrUpload, HttpServletRequest request, HttpServletResponse response) throws Exception{
		final String method = "getXlsUsersCustomAcknowledgementInvsByMonthAndYear::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				
		Page<? extends InvoiceParent> invoices=acknowledgementService.getUsersCustomAcknowledgementInvoices(id, clientids, pendingOrUpload, fromtime, totime, 0, -1, null, null);
		response.setHeader("Content-Disposition", "inline; filename='MGST_Acknowledgement_"+pendingOrUpload.toUpperCase()+"_"+fromtime+"_"+totime+".xls");
		
		List<InvoiceVO> invoiceVOList= clientController.getInvoice_Wise_List(invoices,returntype);
		logger.debug(invoiceVOList);
		File file = new File("MGST_Acknowledgement_"+pendingOrUpload.toUpperCase()+"_"+fromtime+"_"+totime+".xls");
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers = Arrays.asList("Invoice Date", "Invoice No","Invoice Status", "GSTIN", "Customer Name", "CompanyGSTIN","CompanyStateName", "OriginalInvNo", "OriginalInvDate", "Return Period",
							"Invoice Type", "State", "Taxable Value", "IGST Amount","CGST Amount",
							"SGST Amount", "CESS Amount", "Total Tax","Total Invoice Value");										
			
			SimpleExporter exporter = new SimpleExporter();
			exporter.gridExport(
					headers, invoiceVOList,
						"invoiceDate, invoiceNo,gstStatus, customerGSTIN, customerName, companyGSTIN,companyStatename,originalInvoiceNo,originalInvoiceDate,returnPeriod, type, state, taxableValue,  igstAmount,  cgstAmount,  sgstAmount,  cessAmount,totaltax,totalValue",
						fos);										
			
			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "getXlsUsersCustomAcknowledgementInvsByMonthAndYear : ERROR", e);
		}
		return new FileSystemResource(new File("MGST_Acknowledgement_"+pendingOrUpload.toUpperCase()+"_"+fromtime+"_"+totime+".xls"));
	}
	
	@RequestMapping(value = "/getXlspendingacknldgeinvs/{id}/{returntype}/{month}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource getXlsMonthlyAndYearlyPendingInvoices(@PathVariable String id, 
			@PathVariable String returntype, @PathVariable int month, @PathVariable int year, 
			@RequestParam("pendingOrUpload") String pendingOrUpload,@RequestParam("clientids")List<String> clientids,
			@RequestParam("customerNames")List<String> customerNames, HttpServletRequest request, HttpServletResponse response) throws Exception {
		final String method = "getMonthlyPendingInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		User usr = userService.findById(id);
		CompanyUser companyuser = CompanyUserRepository.findByEmail(usr.getEmail());
		
		if(NullUtil.isEmpty(customerNames)) {
			if(isNotEmpty(companyuser.getCustomer())) {
				customerNames = companyuser.getCustomer();
			}
		}
		List<String> customers = Lists.newArrayList();
		for(String customername : customerNames) {
			if(customername.contains("-mgst-")) {
				customername = customername.replaceAll("-mgst-", "&");
			}
			customers.add(customername);
		}
				
		Page<? extends InvoiceParent> invoices=acknowledgementService.getMonthlyAndYearlyAcknowledgementInvoices(id, pendingOrUpload, clientids,customers,month, year, 0, -1, null, null);
		response.setHeader("Content-Disposition", "inline; filename='MGST_Acknowledgement_"+pendingOrUpload.toUpperCase()+"_"+month+"_"+year+".xls");
		
		List<InvoiceVO> invoiceVOList= clientController.getInvoice_Wise_List(invoices,returntype);
		logger.debug(invoiceVOList);
		File file = new File("MGST_Acknowledgement_"+pendingOrUpload.toUpperCase()+"_"+month+"_"+year+".xls");
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers = Arrays.asList("Invoice Date", "Invoice No","Invoice Status", "GSTIN", "Customer Name", "CompanyGSTIN","CompanyStateName", "OriginalInvNo", "OriginalInvDate", "Return Period",
							"Invoice Type", "State", "Taxable Value", "IGST Amount","CGST Amount",
							"SGST Amount", "CESS Amount", "Total Tax","Total Invoice Value");										
			
			SimpleExporter exporter = new SimpleExporter();
			exporter.gridExport(
					headers, invoiceVOList,
						"invoiceDate, invoiceNo,gstStatus, customerGSTIN, customerName, companyGSTIN,companyStatename,originalInvoiceNo,originalInvoiceDate,returnPeriod, type, state, taxableValue,  igstAmount,  cgstAmount,  sgstAmount,  cessAmount,totaltax,totalValue",
						fos);										
			
			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "getXlsUsersCustomAcknowledgementInvsByMonthAndYear : ERROR", e);
		}
		return new FileSystemResource(new File("MGST_Acknowledgement_"+pendingOrUpload.toUpperCase()+"_"+month+"_"+year+".xls"));
	}
	
	@RequestMapping(value = "/getXlspendingcustomacknldgeinvs/{id}/{returntype}/{fromtime}/{totime}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource getXlsCustomPendingInvoices(@PathVariable String id, 
			@PathVariable String returntype, @PathVariable String fromtime,  @PathVariable String totime,
			@RequestParam("pendingOrUpload") String pendingOrUpload,@RequestParam("clientids")List<String> clientids,
			@RequestParam("customerNames")List<String> customerNames, HttpServletRequest reques, HttpServletResponse response) throws Exception {
		final String method = "getXlsCustomPendingInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		response.setHeader("Content-Disposition", "inline; filename='MGST_Acknowledgement_"+pendingOrUpload.toUpperCase()+"_"+fromtime+"_"+totime+".xls");
		User usr = userService.findById(id);
		CompanyUser companyuser = CompanyUserRepository.findByEmail(usr.getEmail());
		
		if(NullUtil.isEmpty(customerNames)) {
			if(isNotEmpty(companyuser.getCustomer())) {
				customerNames = companyuser.getCustomer();
			}
		}
		List<String> customers = Lists.newArrayList();
		for(String customername : customerNames) {
			if(customername.contains("-mgst-")) {
				customername = customername.replaceAll("-mgst-", "&");
			}
			customers.add(customername);
		}		
		Page<? extends InvoiceParent> invoices=acknowledgementService.getCustomAcknowledgementInvoices(id,  pendingOrUpload, clientids, customers,fromtime, totime, 0, -1, null, null);
		
		List<InvoiceVO> invoiceVOList= clientController.getInvoice_Wise_List(invoices,returntype);
		logger.debug(invoiceVOList);
		File file = new File("MGST_Acknowledgement_"+pendingOrUpload.toUpperCase()+"_"+fromtime+"_"+totime+".xls");
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers = Arrays.asList("Invoice Date", "Invoice No","Invoice Status", "GSTIN", "Customer Name", "CompanyGSTIN","CompanyStateName", "OriginalInvNo", "OriginalInvDate", "Return Period",
							"Invoice Type", "State", "Taxable Value", "IGST Amount","CGST Amount",
							"SGST Amount", "CESS Amount", "Total Tax","Total Invoice Value");										
			
			SimpleExporter exporter = new SimpleExporter();
			exporter.gridExport(
					headers, invoiceVOList,
						"invoiceDate, invoiceNo,gstStatus, customerGSTIN, customerName, companyGSTIN,companyStatename,originalInvoiceNo,originalInvoiceDate,returnPeriod, type, state, taxableValue,  igstAmount,  cgstAmount,  sgstAmount,  cessAmount,totaltax,totalValue",
						fos);										
			
			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "getXlsCustomPendingInvoices : ERROR", e);
		}
		return new FileSystemResource(new File("MGST_Acknowledgement_"+pendingOrUpload.toUpperCase()+"_"+fromtime+"_"+totime+".xls"));
	}
}
