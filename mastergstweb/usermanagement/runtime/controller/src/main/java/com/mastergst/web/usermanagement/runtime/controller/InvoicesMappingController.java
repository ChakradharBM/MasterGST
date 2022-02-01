package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2A;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.api.client.util.Maps;
import com.mastergst.configuration.service.ReconcileTemp;
import com.mastergst.configuration.service.ReconcileTempRepository;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.dao.InvoicesMappingDao;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientConfig;
import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.repository.OtherConfigurationRepository;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ImportMapperService;
import com.mastergst.usermanagement.runtime.service.InvoicesMappingService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;
import com.mastergst.usermanagement.runtime.support.Utility;

@Controller
public class InvoicesMappingController {
	private static final Logger logger = LogManager.getLogger(InvoicesMappingController.class.getName());
	private static final String CLASSNAME = "InvoicesMappingController::";
	
	@Autowired
	private InvoicesMappingService invoicesMappingService;
	@Autowired private ClientService clientService;
	@Autowired private ProfileService profileService;
	@Autowired private UserService userService;
	@Autowired private ClientUserMappingRepository clientUserMappingRepository;
	@Autowired private SubscriptionService subscriptionService;
	@Autowired private UserRepository userRepository;
	@Autowired private ReconcileTempRepository reconcileTempRepository;
	@Autowired InvoicesMappingDao invoicesMappingDao;
	@Autowired OtherConfigurationRepository otherConfigurationRepository;
	@Autowired private ImportMapperService importMapperService;
	
	@RequestMapping(value = "/getReconsileInvsSupport/{clientid}/{month}/{year}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAdditionalInvoicesSupport(@PathVariable("clientid") String clientid, @PathVariable("year") int year, 
			@RequestParam(required = false, defaultValue = MasterGSTConstants.YEARLY) String isMonthly, @PathVariable int month, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
		Boolean billdate = false;
		if(isNotEmpty(otherconfig)){
			billdate = otherconfig.isEnableTransDate();
		}
		boolean trdate = false;
		if(isEmpty(otherconfig) || !billdate) {
			trdate = false;
		}else {
			trdate = true;
		}
		Map<String, Object> invoicesMap = invoicesMappingService.getInvoicesSupport(clientid, month, year, isMonthly.equals(MasterGSTConstants.YEARLY) ? false : true,trdate);
		return invoicesMap;
	}
	
	@RequestMapping(value = "/getInvs/{clientid}/{month}/{year}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getInvoicest(@PathVariable("clientid") String clientid, @PathVariable("year") int year,
			@PathVariable int month ,@RequestParam(required = false, defaultValue = MasterGSTConstants.YEARLY) String isMonthly, @RequestParam String returntype, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
		Boolean billdate = false;
		if(isNotEmpty(otherconfig)){
			billdate = otherconfig.isEnableTransDate();
		}
		String st = request.getParameter("start");
		InvoiceFilter filter = invoiceFilter(request);

		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		boolean trdate = false;
		if(isEmpty(otherconfig) || !billdate) {
			trdate = false;
		}else {
			trdate = true;
		}
		Map<String, Object> invoicesMap = invoicesMappingService.getInvoices(clientid, month, year, isMonthly.equals(MasterGSTConstants.YEARLY) ? false : true, start, length, searchVal, filter,trdate);
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		if(returntype.equals(GSTR1)) {
			FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", SimpleBeanPropertyFilter.serializeAll());
			writer=mapper.writer(filters);
		} else {
			writer=mapper.writer();
		}
		return writer.writeValueAsString(invoicesMap);
	}
	
	@RequestMapping(value = "/getReconcileSummary/{clientid}/{month}/{year}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getReconcileSummary(@PathVariable("clientid") String clientid,
			@PathVariable int month, @RequestParam (required = false, defaultValue = MasterGSTConstants.YEARLY)String isMonthly, @PathVariable("year") int year, HttpServletRequest request) throws Exception {
		Boolean billdate = false;
		OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
		if(isNotEmpty(otherconfig)){
			billdate = otherconfig.isEnableTransDate();
		}
		boolean trdate = false;
		if(isEmpty(otherconfig) || !billdate) {
			trdate = false;
		}else {
			trdate = true;
		}
		Map<String, Object> summary = invoicesMappingService.getReconcileSummary(clientid, month, year, isMonthly.equals(MasterGSTConstants.YEARLY) ? false : true,trdate);
		
		return summary;
	}
	
	@RequestMapping(value = "/reconsilationverification/{clientid}", method = RequestMethod.GET)
	public @ResponseBody  Map<String, Object> verifyReconsilationCounts(@PathVariable String clientid){
		Map<String, Object> countMap = Maps.newHashMap();
		ReconcileTemp temp = reconcileTempRepository.findByClientid(clientid);
		if(isNotEmpty(temp)) {
			Long invs = 0l;
			Long totalinvoices = 0l;
			Long totalgstr2ainvoices = 0l;
			
			Long totalprinvoices = 0l;
			Long totalprprocessedinvoices = 0l;
			String monthlyoryearly = null, returntype = "";
			monthlyoryearly = temp.getMonthlyoryearly();
			if(isNotEmpty(temp.getReturntype())) {
				returntype = temp.getReturntype();
			}
			if(isNotEmpty(temp.getProcessedinvoices())) {
				invs += temp.getProcessedinvoices();
			}
			if(isNotEmpty(temp.getTotalinvoices())) {
				totalinvoices += temp.getTotalinvoices();
			}
			if(isNotEmpty(temp.getProcessedgstr2ainvoices())) {
				totalgstr2ainvoices += temp.getProcessedgstr2ainvoices();
			}
			if(isNotEmpty(temp.getTotalpurchaseinvoices())) {
				totalprinvoices += temp.getTotalpurchaseinvoices();
			}
			if(isNotEmpty(temp.getProcessedpurchaseinvoices())) {
				totalprprocessedinvoices += temp.getProcessedpurchaseinvoices();
			}
			String fullname = "";
			if(isNotEmpty(temp.getFullname())) {
				fullname = temp.getFullname();
			}
			countMap.put("temp", temp);
			countMap.put("fullname", fullname);
			countMap.put("returntype", returntype);
			countMap.put("countinvs", invs);
			countMap.put("totalinvoices", totalinvoices);
			countMap.put("totalgstr2ainvoices", totalgstr2ainvoices);
			
			countMap.put("totalprinvoices", totalprinvoices);
			countMap.put("totalprprocessedinvoices", totalprprocessedinvoices);
			
			countMap.put("monthlyoryearly", monthlyoryearly);
			
		}
		return countMap;
	}
	
	@RequestMapping(value = "/reconsilationverification/{clientid}/{yearly}", method = RequestMethod.GET)
	public @ResponseBody  Map<String, Object> verifyReconsilationCountsYearly(@PathVariable String clientid,@PathVariable String yearly){
		Map<String, Object> countMap = Maps.newHashMap();
		List<ReconcileTemp> temp =  reconcileTempRepository.findByClientidAndMonthlyoryearly(clientid, yearly);
		//temp = reconcileTempRepository.findByClientid(clientid);
		if(isNotEmpty(temp)) {
			Long invs = 0l;
			Long totalinvoices = 0l;
			Long totalgstr2ainvoices = 0l;
			for(ReconcileTemp rec : temp) {
				invs += rec.getProcessedinvoices();
				if(isNotEmpty(rec.getTotalinvoices())) {
					totalinvoices += rec.getTotalinvoices();
				}
				if(isNotEmpty(rec.getProcessedgstr2ainvoices())) {
					totalgstr2ainvoices += rec.getProcessedgstr2ainvoices();
				}
				countMap.put(rec.getInvtype(), rec.getProcessedinvoices());
			}
			
			countMap.put("countinvs", invs);
			countMap.put("totalinvoices", totalinvoices);
			countMap.put("totalgstr2ainvoices", totalgstr2ainvoices);
		}
		return countMap;
	}
	
	
	@RequestMapping(value = "/reconsilationcompleted/{clientid}", method = RequestMethod.GET)
	public @ResponseBody  String verifyReconsilationYearlyCompleted(@PathVariable String clientid){
		//List<ReconcileTemp> temp =  reconcileTempRepository.findByClientidAndMonthlyoryearly(clientid, yearly);
		ReconcileTemp temp =  reconcileTempRepository.findByClientid(clientid);
		if(isEmpty(temp)) {
			return "Completed";
		}
		return "Not Completed";
	}
	
	@RequestMapping(value = "/performReconcile/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public String reconcileInvoices(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month,
			@PathVariable("year") int year,@RequestParam(name = "reconcileType", required = false, defaultValue = MasterGSTConstants.YEARLY) String monthlyOryearly, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "reconcileInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Client client = clientService.findById(clientid);
		Date reconciledDate = new Date();
		client.setReconcileDate(simpleDateFormat.format(reconciledDate));
		clientService.saveClient(client);
		String strMonth = month < 10 ? "0" + month : month + "";
		String retPeriod = strMonth+year;
		//String tokenError = (String) request.getSession().getAttribute("tokenError");
		User user = userService.findById(id);
		ReconcileTemp reconcileTemp = new ReconcileTemp();
		reconcileTemp.setClientid(clientid);
		reconcileTemp.setReturntype(MasterGSTConstants.GSTR2A);
		reconcileTemp.setMonthlyoryearly(monthlyOryearly);
		if(isNotEmpty(user)) {
			reconcileTemp.setUserid(new ObjectId(id));
			reconcileTemp.setInitiateduserid(id);
			if(isNotEmpty(user.getFullname())) {
				reconcileTemp.setFullname(user.getFullname());
			}
		}
		reconcileTempRepository.save(reconcileTemp);
		String usrid = id;
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
				if(isNotEmpty(companyUser.getCompany())){
					if(companyUser.getCompany().contains(clientid)){
						usrid = user.getParentid();
					}
				}
				if(isNotEmpty(companyUser.getAddclient())) {
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
		String userid = userid(id,clientid);
		if (!subscriptionService.allowUploadInvoices(userid, 1l)) {
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid()) && usrid.equals(user.getParentid())){
				User usr = userRepository.findById(userid);
				String errormsg = "";
				if(isNotEmpty(usr)) {
					errormsg = primaryHolderMessage(client,usr);
				}else {
					errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
				}
				
				model.addAttribute("error", errormsg);
			}else{
				model.addAttribute("error", "Your subscription has expired. Kindly subscribe to proceed further!");
			}
		} else {
			List<String> matchingstatus = Lists.newArrayList();
			matchingstatus.add("");
			matchingstatus.add(null);
			matchingstatus.add(MasterGSTConstants.GST_STATUS_NOTINGSTR2A);
			List<String> inv = Lists.newArrayList();
			inv.add(MasterGSTConstants.B2B);
			inv.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
			inv.add(MasterGSTConstants.IMP_GOODS);
			List<String> rtarray = Lists.newArrayList();
			if("monthly".equalsIgnoreCase(monthlyOryearly)) {
				rtarray.add(retPeriod);
			}else {
				Date presentDate = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(presentDate);
				int presentYear = calendar.get(Calendar.YEAR);
				int yr = year;
					//for (int i = yr; i <= presentYear; i++) {
						for (int j = 4; j <= 12; j++) {
							String strMonths = j < 10 ? "0" + j : j + "";
							rtarray.add(strMonths + (yr-1));
						}
						for (int k = 1; k <= 3; k++) {
							String strMonths = k < 10 ? "0" + k : k + "";
							rtarray.add(strMonths + (yr));
						}
					//}
			}
			
			long gstr2aList = invoicesMappingDao.findByClientidAndFpInAndInvtypeAndIsAmendments(clientid, rtarray, inv,matchingstatus, true);
			ReconcileTemp recon = reconcileTempRepository.findByClientid(clientid);
			recon.setTotalinvoices((Long)gstr2aList);
			recon = reconcileTempRepository.save(recon);
			//if(otpcheck.equalsIgnoreCase("OTP_VERIFIED")) {
				String[] invTypes = {MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,MasterGSTConstants.IMP_GOODS};
				OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
				Boolean billdate = false;
				if(isNotEmpty(otherconfig)){
					billdate = otherconfig.isEnableTransDate();
				}
				
				for (String invType : invTypes) {
					ReconcileTemp temp = reconcileTempRepository.findByClientid(clientid);
					List<String> invoic = Lists.newArrayList();
					invoic.add(invType);
					long gstr2ainvList = 0l;
					gstr2ainvList = invoicesMappingDao.findByClientidAndFpInAndInvtypeAndIsAmendments(clientid, rtarray, invoic,matchingstatus, true);
					if(invType.equals(MasterGSTConstants.B2B)) {
						temp.setTotalb2binvoices((Long)gstr2ainvList);
					}else if(invType.equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
						temp.setTotalcreditinvoices(gstr2ainvList);
					}else if(invType.equals(MasterGSTConstants.IMP_GOODS)) {
						temp.setTotalimpginvoices(gstr2ainvList);
					}
					reconcileTempRepository.save(temp);
					if("monthly".equalsIgnoreCase(monthlyOryearly)) {
						if(isEmpty(otherconfig) || !billdate) {
							invoicesMappingService.updateMismatchStatus(clientid, invType, client.getGstnnumber(), retPeriod, month, year, "monthly",true);
						}else {
							invoicesMappingService.updateMismatchStatus(clientid, invType, client.getGstnnumber(), retPeriod, month, year, "monthly",false);
						}
					}else {
						invoicesMappingService.updateMismatchStatus(clientid, invType, client.getGstnnumber(), retPeriod, month, year, "Yearly",true);
					}
				}

		}
		updateModel(model, id, fullname, usertype, month, year);
		model.addAttribute("type", "mmtchinv");
		ClientConfig clientConfig = clientService.getClientConfig(clientid);
		model.addAttribute("clientConfig", clientConfig);
		model.addAttribute("client", client);
		if(returntype.equals(GSTR2A)){
			returntype = GSTR2;
		}
		model.addAttribute("returntype", returntype);
		
		model.addAttribute("lGSTReturnsSummury", clientService.getGSTReturnsSummary(client, month, year, false));
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		ReconcileTemp temp = reconcileTempRepository.findByClientid(clientid);
		if(isNotEmpty(temp)) {
			reconcileTempRepository.delete(temp);
		}
		logger.debug(CLASSNAME + method + END);
		return "client/all_invoice_view";
	}
	
	//@RequestMapping(value = "/performReconcile/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody void performReconcile(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month,
			@PathVariable("year") int year, @RequestParam(name = "reconcileType", required = false, defaultValue = MasterGSTConstants.YEARLY) String monthlyOryearly,  ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "reconcileInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		User user = userService.findByUsrId(id);
		Client client = clientService.findById(clientid);
		Date reconciledDate = new Date();
		client.setReconcileDate(simpleDateFormat.format(reconciledDate));
		clientService.saveClient(client);
		String strMonth = month < 10 ? "0" + month : month + "";
		String retPeriod = strMonth+year;
		String yearcode  = Utility.getYearCode(month, year);
		boolean isYearly = false;
		String monthlyoryearly = "Yearly";
		int yr = year;
		Date presentDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(presentDate);
		int presentYear = calendar.get(Calendar.YEAR);
		List<String> rtarray = Lists.newArrayList();
		if(monthlyOryearly.equals(MasterGSTConstants.MONTHLY)) {
			isYearly = true;
			monthlyoryearly = "Monthly";
			rtarray.add(retPeriod);
		}else {
			for (int i = yr; i <= presentYear; i++) {
				for (int j = 4; j <= 12; j++) {
					String strMonths = j < 10 ? "0" + j : j + "";
					rtarray.add(strMonths + (i));
				}
				for (int k = 1; k <= 3; k++) {
					String strMonths = k < 10 ? "0" + k : k + "";
					rtarray.add(strMonths + (i + 1));
				}
			}
		}
		List<String> matchingstatus = Lists.newArrayList();
		matchingstatus.add("");
		matchingstatus.add(null);
		
		List<String> inv = Lists.newArrayList();
		inv.add(MasterGSTConstants.B2B);
		inv.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		inv.add(MasterGSTConstants.IMP_GOODS);
		long gstr2aList = invoicesMappingDao.findByClientidAndFpInAndInvtypeAndIsAmendments(clientid, rtarray, inv,matchingstatus, true);
		ReconcileTemp reconcileTemp = new ReconcileTemp(clientid, 0l,monthlyoryearly,(Long)gstr2aList);
		if(isNotEmpty(user)) {
			reconcileTemp.setUserid(new ObjectId(id));
			reconcileTemp.setInitiateduserid(id);
			if(isNotEmpty(user.getFullname())) {
				reconcileTemp.setFullname(user.getFullname());
			}
		}
		reconcileTempRepository.save(reconcileTemp);
		
		OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
		Boolean billdate = false;
		if(isNotEmpty(otherconfig)){
			billdate = otherconfig.isEnableTransDate();
		}
		String[] invTypes = {MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.IMP_GOODS};
		List<String> matchingstatuspr = Lists.newArrayList();
		matchingstatuspr.add("");
		matchingstatuspr.add(null);
		matchingstatuspr.add(MasterGSTConstants.GST_STATUS_NOTINGSTR2A);
		for (String invType : invTypes) {
			if(isEmpty(otherconfig) || !billdate || monthlyoryearly.equalsIgnoreCase("Yearly")) {
				List<PurchaseRegister> purchaseRegisters = invoicesMappingDao.getPurchaseRegistersMatchingStatusIs(invType, clientid, month, year, yearcode, isYearly,false,matchingstatuspr);
				if (isNotEmpty(purchaseRegisters)) {
					invoicesMappingService.updateReconsileStatus(clientid, purchaseRegisters, null, rtarray,matchingstatus,invType, client.getGstnnumber(), retPeriod, monthlyoryearly,false);
				}else {
					List<String> notpurc = Lists.newArrayList();
					notpurc.add(invType);
					long gstr2aListnopurchases = invoicesMappingDao.findByClientidAndFpInAndInvtypeAndIsAmendments(clientid, rtarray, notpurc,matchingstatus, true);
					ReconcileTemp reconciletemp = reconcileTempRepository.findByMonthlyoryearlyAndClientid(monthlyoryearly,clientid);
					if(isNotEmpty(reconciletemp) && isNotEmpty(reconciletemp.getProcessedgstr2ainvoices())) {
						reconciletemp.setProcessedgstr2ainvoices(reconciletemp.getProcessedgstr2ainvoices()+(Long)gstr2aListnopurchases);
					}else {
						reconciletemp.setProcessedgstr2ainvoices((Long)gstr2aListnopurchases);
					}
					reconcileTempRepository.save(reconciletemp);
				}
			}else {
				List<PurchaseRegister> purchaseRegisters = invoicesMappingDao.getPurchaseRegistersMatchingStatusIs(invType, clientid, month, year, yearcode, isYearly,true,matchingstatuspr);
				
				if (isNotEmpty(purchaseRegisters)) {
					invoicesMappingService.updateReconsileStatus(clientid, purchaseRegisters, null,rtarray,matchingstatus, invType, client.getGstnnumber(), retPeriod, monthlyoryearly,true);
				}else {
					List<String> notpurc = Lists.newArrayList();
					notpurc.add(invType);
					long gstr2aListnopurchases = invoicesMappingDao.findByClientidAndFpInAndInvtypeAndIsAmendments(clientid, rtarray, notpurc,matchingstatus, true);
					ReconcileTemp reconciletemp = reconcileTempRepository.findByMonthlyoryearlyAndClientid(monthlyoryearly,clientid);
					if(isNotEmpty(reconciletemp) && isNotEmpty(reconciletemp.getProcessedgstr2ainvoices())) {
						reconciletemp.setProcessedgstr2ainvoices(reconciletemp.getProcessedgstr2ainvoices()+(Long)gstr2aListnopurchases);
					}else {
						reconciletemp.setProcessedgstr2ainvoices((Long)gstr2aListnopurchases);
					}
					reconcileTempRepository.save(reconciletemp);
				}
			}
			if(invType.equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
				List<ReconcileTemp> temp = reconcileTempRepository.findByClientidAndMonthlyoryearly(clientid,monthlyoryearly);
				if(isNotEmpty(temp)) {
					reconcileTempRepository.delete(temp);
					logger.info("reconcile temp deleted");
					
				}
			}
		}
			
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/verifySubscription/{id}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody String verifySubscription(@PathVariable String id, @PathVariable String clientid) {
		User user = userService.findById(id);
		Client client = clientService.findById(clientid);
		String usrid = id;
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
				if(isNotEmpty(companyUser.getCompany())){
					if(companyUser.getCompany().contains(clientid)){
						usrid = user.getParentid();
					}
				}
			}
		}
		String errormsg = "verified";
		String userid = userid(id,clientid);
		if (!subscriptionService.allowUploadInvoices(userid, 1l)) {
			if(usrid.equals(user.getParentid())){
				User usr = userRepository.findById(userid);
				if(isNotEmpty(usr)) {
					errormsg = primaryHolderMessage(client,usr);
				}else {
					errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
				}
			}else{
				errormsg = "Your subscription has expired. Kindly subscribe to proceed further!";
			}
		}
		return errormsg;
	}
	
	
	public String primaryHolderMessage(Client client, User usr) {
		
		String message = "Primary Account Holder of <span style='color:blue;'>"+client.getBusinessname()+"</span> is Subscription is expired, please contact <span style='color:blue'>"+usr.getFullname()+","+usr.getEmail()+" & "+usr.getMobilenumber()+"</span> to renew";
		return message;
	}
	public InvoiceFilter invoiceFilter(HttpServletRequest request) {
		InvoiceFilter filter = new InvoiceFilter();
		filter.setDocumentType(request.getParameter("documentType"));
		filter.setInvoiceType(request.getParameter("invoiceType"));
		String user = request.getParameter("user");
		if(isNotEmpty(user)) {
			if(user.contains("-mgst-")) {
				user = user.replaceAll("-mgst-", "&");
			}
		}
		filter.setUser(user);
		String vendor = request.getParameter("vendor");
		if(isNotEmpty(vendor)) {
			if(vendor.contains("-mgst-")) {
				vendor = vendor.replaceAll("-mgst-", "&");
			}
		}
		filter.setVendor(vendor);
		filter.setBranch(request.getParameter("branch"));
		filter.setVertical(request.getParameter("vertical"));
		return filter;	
	}
	
	public String userid(String id,String clientid) {
		String userid = id;
		User user = userService.findById(userid);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(NullUtil.isNotEmpty(companyUser)) {
				List<String> clntids = companyUser.getCompany();
				if(isNotEmpty(clntids) && clntids.size() > 0){
					if(clntids.contains(clientid)){
						ClientUserMapping clntusermapping = clientUserMappingRepository.findByClientidAndCreatedByIsNotNull(clientid);
						if(isNotEmpty(clntusermapping) && isNotEmpty(clntusermapping.getCreatedBy())) {
							userid = clntusermapping.getCreatedBy();
						}else {
							userid = user.getParentid();
						}
					}
				}else {
					userid = user.getParentid();
				}
			}
		}
		return userid;
	}
	
	
	//-------------------------------------------------------------------
	
	
	//@RequestMapping(value = "/reconcileinv/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public String gstr2AReconcileInvoices(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month,
			@PathVariable("year") int year, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "gstr2AReconcileInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Client client = clientService.findById(clientid);
		Date reconciledDate = new Date();
		client.setReconcileDate(simpleDateFormat.format(reconciledDate));
		clientService.saveClient(client);
		String strMonth = month < 10 ? "0" + month : month + "";
		String retPeriod = strMonth+year;
		
		User user = userService.findById(id);
		ReconcileTemp reconcileTemp = new ReconcileTemp();
		reconcileTemp.setClientid(clientid);
		reconcileTemp.setMonthlyoryearly("Monthly");
		if(isNotEmpty(user)) {
			reconcileTemp.setUserid(new ObjectId(id));
			reconcileTemp.setInitiateduserid(id);
			if(isNotEmpty(user.getFullname())) {
				reconcileTemp.setFullname(user.getFullname());
			}
		}
		reconcileTempRepository.save(reconcileTemp);
		String usrid = id;
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
				if(isNotEmpty(companyUser.getCompany())){
					if(companyUser.getCompany().contains(clientid)){
						usrid = user.getParentid();
					}
				}
				if(isNotEmpty(companyUser.getAddclient())) {
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
		String userid = userid(id,clientid);
		if (!subscriptionService.allowUploadInvoices(userid, 1l)) {
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid()) && usrid.equals(user.getParentid())){
				User usr = userRepository.findById(userid);
				String errormsg = "";
				if(isNotEmpty(usr)) {
					errormsg = primaryHolderMessage(client,usr);
				}else {
					errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
				}
				
				model.addAttribute("error", errormsg);
			}else{
				model.addAttribute("error", "Your subscription has expired. Kindly subscribe to proceed further!");
			}
		} else {
			List<String> matchingstatus = Arrays.asList("", null, MasterGSTConstants.GST_STATUS_NOTINGSTR2A);
			List<String> inv = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.IMP_GOODS);
			List<String> rtarray = Arrays.asList(retPeriod);
			
			long gstr2aList = invoicesMappingDao.findByClientidAndFpInAndInvtypeAndIsAmendments(clientid, rtarray, inv,matchingstatus, true);
			ReconcileTemp recon = reconcileTempRepository.findByClientid(clientid);
			recon.setTotalinvoices((Long)gstr2aList);
			recon = reconcileTempRepository.save(recon);
			
			String[] invTypes = {MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,MasterGSTConstants.IMP_GOODS};
			OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
			Boolean billdate = false;
			if(isNotEmpty(otherconfig)){
				billdate = otherconfig.isEnableTransDate();
			}
			for (String invType : invTypes) {
				ReconcileTemp temp = reconcileTempRepository.findByClientid(clientid);
				List<String> invoic = Lists.newArrayList();
				invoic.add(invType);
				long gstr2ainvList = 0l;
				gstr2ainvList = invoicesMappingDao.findByClientidAndFpInAndInvtypeAndIsAmendments(clientid, rtarray, invoic,matchingstatus, true);
				if(invType.equals(MasterGSTConstants.B2B)) {
					temp.setTotalb2binvoices((Long)gstr2ainvList);
				}else if(invType.equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
					temp.setTotalcreditinvoices(gstr2ainvList);
				}else if(invType.equals(MasterGSTConstants.IMP_GOODS)) {
					temp.setTotalimpginvoices(gstr2ainvList);
				}
				reconcileTempRepository.save(temp);
				if(isEmpty(otherconfig) || !billdate) {
					clientService.updateMismatchStatus(clientid, invType, client.getGstnnumber(), retPeriod, month, year, "monthly",true);
				}else {
					clientService.updateMismatchStatus(clientid, invType, client.getGstnnumber(), retPeriod, month, year, "monthly",false);
				}
			}
		}
		model.addAttribute("type", "mmtchinv");
		model.addAttribute("client", client);
		if(returntype.equals(MasterGSTConstants.GSTR2A)){
			returntype = MasterGSTConstants.GSTR2;
		}
		model.addAttribute("returntype", returntype);
		model.addAttribute("lGSTReturnsSummury", clientService.getGSTReturnsSummary(client, month, year, false));
		ReconcileTemp temp = reconcileTempRepository.findByClientid(clientid);
		if(isNotEmpty(temp)) {
			reconcileTempRepository.delete(temp);
		}
		logger.debug(CLASSNAME + method + END);
		return "client/all_invoice_view";
	}
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	
	
}
