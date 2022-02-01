package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2A;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Maps;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Lists;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.usermanagement.runtime.dao.GSTR2ADao;
import com.mastergst.usermanagement.runtime.dao.PurchageRegisterDao;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientConfig;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;
import com.mastergst.usermanagement.runtime.repository.GSTR2Repository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;
import com.mastergst.usermanagement.runtime.response.MisMatchVO;
import com.mastergst.usermanagement.runtime.service.ClientService;

@Controller
public class MisMatchController {
	private static final Logger logger = LogManager.getLogger(MisMatchController.class.getName());
	private static final String CLASSNAME = "ClientController::";
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	GSTR2Repository gstr2Repository;
	
	@Autowired
	PurchaseRegisterRepository purchaseRepository;
	@Autowired
	GSTR2ADao gstr2aDao;
	@Autowired
	PurchageRegisterDao purchageRegisterDao;
	
	
	@RequestMapping(value = "/mannualMatchingInvoices/{clientid}/{invoiceid}/{returntype}/{month}/{year}/{monthlyOryearly}", method = RequestMethod.GET)
	public @ResponseBody String getInvoices(@PathVariable("clientid") String clientid, @PathVariable("invoiceid") String invoiceid, @PathVariable("returntype") String returntype, @PathVariable("month") int month, @PathVariable("year") int year, @PathVariable("monthlyOryearly") String monthlyOryearly, 
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		Pageable pageable = null;
		Date stDate = null;
		Date endDate = null;
		List<String> rtarray = Lists.newArrayList();
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.B2BA);
		invTypes.add(MasterGSTConstants.CDNA);
		invTypes.add(MasterGSTConstants.ISD);
		invTypes.add(MasterGSTConstants.IMP_GOODS);
		if("monthly".equalsIgnoreCase(monthlyOryearly)) {
			Date presentDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(presentDate);
			
			int presentYear = calendar.get(Calendar.YEAR);
			int presentMonth = calendar.get(Calendar.MONTH)+1;
			Calendar cal = Calendar.getInstance();
			if(month < 10) {
				cal.set(year-1, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				if(presentYear != year) {
					cal.set(year, 9, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}else {
					cal.set(year, presentMonth, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}
				
			}else {
				cal.set(year-1, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				if(presentYear != year) {
					cal.set(year, 9, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}else {
					cal.set(year, presentMonth, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}
			}
		}else {
			int yr = year;
			if(month < 4) {
				yr = yr-1;
				Calendar cal = Calendar.getInstance();
				cal.set(year-1, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(year, 3, 0, 23, 59, 59);
				endDate = new java.util.Date(cal.getTimeInMillis());
			}else {
				Calendar cal = Calendar.getInstance();
				cal.set(year, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(year+1, 3, 0, 23, 59, 59);
				endDate = new java.util.Date(cal.getTimeInMillis());
			}
			//String year = fp.substring(2);
			
			Date presentDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(presentDate);
			int presentYear = calendar.get(Calendar.YEAR);
			for(int i=yr;i<=presentYear;i++) {
				for(int j=4;j<=12;j++) {
					String strMonth = j < 10 ? "0" + j : j + "";
					rtarray.add(strMonth+(i));
				}
				for(int k=1;k<=3;k++) {
					String strMonth = k < 10 ? "0" + k : k + "";
					rtarray.add(strMonth+(i+1));
				}
			}
			
		}
		Page<? extends InvoiceParent> invoices = null;
		if(returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) {
			List<String> matchingstatus = new ArrayList<String>();
			matchingstatus.add("Not In Purchases");
			matchingstatus.add(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
				pageable = new PageRequest(0, Integer.MAX_VALUE);
				
				List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
				Page<? extends InvoiceParent> minvoices = null; 
				if("monthly".equalsIgnoreCase(monthlyOryearly)) {
					minvoices = gstr2Repository.findByClientidAndInvtypeInAndIsAmendmentAndDateofinvoiceBetweenAndMatchingStatusIsNull(clientid,invTypes,true,stDate,endDate,pageable);
				}else {
					minvoices = gstr2Repository.findByClientidAndFpInAndInvtypeInAndIsAmendmentAndMatchingStatusIsNull(clientid, rtarray, invTypes, true, pageable);
				}
				Page<? extends InvoiceParent> mminvoices = null; 
				if("monthly".equalsIgnoreCase(monthlyOryearly)) {
					mminvoices = gstr2Repository.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndMatchingStatusInAndIsAmendment(clientid,invTypes,stDate,endDate,matchingstatus,true,pageable);
				}else {
					mminvoices = gstr2Repository.findByClientidAndFpInAndInvtypeInAndIsAmendmentAndMatchingStatusIn(clientid, rtarray, invTypes, true, matchingstatus, pageable);
				}
				List<InvoiceParent> matchingstatusnullInvoices = Lists.newArrayList();
				List<InvoiceParent> gstr2invoices = Lists.newArrayList();
				
				if(isNotEmpty(minvoices)) {
					matchingstatusnullInvoices = (List<InvoiceParent>) minvoices.getContent();
				}
				if(isNotEmpty(mminvoices)) {
					matchingstatusInvoices = (List<InvoiceParent>) mminvoices.getContent();
				}
				//List<? extends InvoiceParent> matchingstatusInvoices = invoices.getContent();
				if(isNotEmpty(matchingstatusnullInvoices) && matchingstatusnullInvoices.size() > 0) {
					gstr2invoices.addAll(matchingstatusnullInvoices);
				}
				if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
					gstr2invoices.addAll(matchingstatusInvoices);
				}
				invoices = new PageImpl<InvoiceParent>(gstr2invoices);
			
		}else {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
			List<String> matchingstatus = new ArrayList<String>();
			matchingstatus.add(MasterGSTConstants.GST_STATUS_NOTINGSTR2A);
			matchingstatus.add(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
			Page<? extends InvoiceParent> mminvoices = purchaseRepository.findByClientidAndInvtypeInAndMatchingStatusInAndDateofinvoiceBetween(clientid,invTypes,matchingstatus, stDate, endDate,pageable);
			
			Page<? extends InvoiceParent> prinvoices = clientService.getPresentMonthfyinvsMatchingIdisNull(clientid, stDate, endDate);
			List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
			List<InvoiceParent> matchingstatusnullInvoices = Lists.newArrayList();
			List<InvoiceParent> gstr2invoices = Lists.newArrayList();
			
			if(isNotEmpty(mminvoices)) {
				matchingstatusnullInvoices = (List<InvoiceParent>) mminvoices.getContent();
			}
			if(isNotEmpty(prinvoices)) {
				matchingstatusInvoices = (List<InvoiceParent>) prinvoices.getContent();
			}
			//List<? extends InvoiceParent> matchingstatusInvoices = invoices.getContent();
			if(isNotEmpty(matchingstatusnullInvoices) && matchingstatusnullInvoices.size() > 0) {
				gstr2invoices.addAll(matchingstatusnullInvoices);
			}
			if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
				gstr2invoices.addAll(matchingstatusInvoices);
			}
			invoices = new PageImpl<InvoiceParent>(gstr2invoices);
			
		}
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
	
	@RequestMapping(value = "/mannualMatchingInvoice/{invoiceid}/{returntype}", method = RequestMethod.GET)
	public @ResponseBody InvoiceParent getInvoice(@PathVariable("invoiceid") String invoiceid, @PathVariable("returntype") String returntype, 
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		InvoiceParent invoice = null;
		if(returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) {
			invoice = purchaseRepository.findOne(invoiceid);
		}else {
			invoice = gstr2Repository.findOne(invoiceid);
			
		}
		return invoice;
	}
	
	
	@RequestMapping(value = "/mannualMatchArray/{returnType}/{invoiceid}", method = RequestMethod.POST)
	public @ResponseBody void performMismatch(@RequestBody List<MisMatchVO> records,@PathVariable("returnType") String returnType,@PathVariable("invoiceid") String invoiceid,ModelMap model) throws Exception {
		final String method = "performMismatch ::";
		logger.debug(CLASSNAME + method + BEGIN);
		clientService.updateMannualRecords(records, returnType,invoiceid);
	}	
	
	@RequestMapping(value = "/viewMannualMatchingInvoices/{invoiceid}/{returntype}", method = RequestMethod.GET)
	public @ResponseBody String getInvoices(@PathVariable("invoiceid") String invoiceid, @PathVariable("returntype") String returntype, 
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		Pageable pageable = null;
		Page<? extends InvoiceParent> invoices = null;
		if(returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
			invoices = gstr2Repository.findByMatchingIdAndMatchingStatus(invoiceid,MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED,pageable);
		}else {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
			invoices = purchaseRepository.findByMatchingIdAndMatchingStatus(invoiceid,MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED,pageable);
			
		}
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
	
	@RequestMapping(value = "/getPurchaseRegisterinvs/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getMatchedAndPresentMonthFYInvoices(@PathVariable("id") String id, @PathVariable("returntype") String returntype, 
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, 
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);;
		
		Date stDate = null;
		Date endDate = null;
		Calendar cals = Calendar.getInstance();
		cals.set(year, 3, 1, 0, 0, 0);
		stDate = new java.util.Date(cals.getTimeInMillis());
		cals = Calendar.getInstance();
		cals.set(year+1, 3, 0, 23, 59, 59);
		endDate = new java.util.Date(cals.getTimeInMillis());
		
		String rtStart = "04"+year;
		String rtEnd = "03"+(year+1);		
		List<String> rtArray=Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, 
				"10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd);
		List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.ISD, 
				MasterGSTConstants.B2BA, MasterGSTConstants.CDNA,MasterGSTConstants.IMP_GOODS);
		
		
		Page<? extends InvoiceParent> invoices = clientService.getPresentMonthfyinvsMatchingIdisNull(clientid, stDate, endDate);
		Page<? extends InvoiceParent> minvoices = clientService.getPresentMonthfyinvsMatchingIdisNotNull(clientid, stDate, endDate);
		Page<? extends InvoiceParent> monthlyinvoices = gstr2Repository.findByClientidAndIsAmendmentAndFpInAndInvtypeIn(client.getId().toString(),true, rtArray, invTypes, pageable);
		
		List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> mmatchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();
		if(isNotEmpty(invoices)) {
			matchingstatusInvoices = (List<InvoiceParent>) invoices.getContent();
		}
		
		if(isNotEmpty(minvoices)) {
			mmatchingstatusInvoices = (List<InvoiceParent>) minvoices.getContent();
		}
		
		List<String> mmatchingids = Lists.newArrayList();
		if(isNotEmpty(mmatchingstatusInvoices) && mmatchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : mmatchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					mmatchingids.add(inv.getMatchingId());
				}else {
					matchingstatus.add(purchaseRepository.findOne(inv.getId().toString()));
				}
			}
		}
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList();
		if(isNotEmpty(invoices)) {
			monthlyInvoicess = (List<InvoiceParent>) monthlyinvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			for(InvoiceParent inv : monthlyInvoicess) {
				if(isNotEmpty(inv.getMatchingStatus()) && !inv.getMatchingStatus().equalsIgnoreCase(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)) {
					matchingids.add(inv.getId().toString());
				}
			}
		}
		matchingids.addAll(mmatchingids);
		Page<? extends InvoiceParent> matchedinvoices = clientService.getgstr2MatchingIdsInvoices(clientid,matchingids);
		List<InvoiceParent> matchingInvoices = Lists.newArrayList();
		if(isNotEmpty(matchedinvoices)) {
			matchingInvoices = (List<InvoiceParent>) matchedinvoices.getContent();
		}
		if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
			matchingstatus.addAll(matchingstatusInvoices);
		}
		if(isNotEmpty(matchingInvoices) && matchingInvoices.size() > 0) {
			matchingstatus.addAll(matchingInvoices);
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
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
		return writer.writeValueAsString(allinvoices);
	}
	
	@RequestMapping(value = "/getgstr2Matchedfyinvss/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getgstr2Matchedfyinvs(@PathVariable("id") String id, @PathVariable("returntype") String returntype, 
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, 
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		String strMonth = month < 10 ? "0" + month : month + "";
		String fp = strMonth+year;
		
		Date stDate = null;
		Date endDate = null;
		Calendar cals = Calendar.getInstance();
		cals.set(year, 3, 1, 0, 0, 0);
		stDate = new java.util.Date(cals.getTimeInMillis());
		cals = Calendar.getInstance();
		cals.set(year+1, 3, 0, 23, 59, 59);
		endDate = new java.util.Date(cals.getTimeInMillis());
		
		Pageable pageable = null;
		
		String rtStart = "04"+year;
		String rtEnd = "03"+(year+1);		
		List<String> rtArray=Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, 
				"10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd);
		List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.ISD, 
				MasterGSTConstants.B2BA, MasterGSTConstants.CDNA,MasterGSTConstants.IMP_GOODS);
		
		
		Page<? extends InvoiceParent> invoices = clientService.getPresentretperiodgstr2MatchingStatusisNull(clientid,rtArray);
		
		Page<? extends InvoiceParent> notinpurchasesinvoices = clientService.getPresentretperiodgstr2MatchingStatus(clientid,rtArray);
		
		Page<? extends InvoiceParent> monthlypurchaseInvoices = clientService.getPresentMonthfyinvsMatchingIdisNotNull(clientid, stDate,endDate);
		
		List<InvoiceParent> monthlypurchasematchingstatusInvoices = Lists.newArrayList();
		if(isNotEmpty(monthlypurchaseInvoices)) {
			monthlypurchasematchingstatusInvoices = (List<InvoiceParent>) monthlypurchaseInvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlypurchasematchingstatusInvoices) && monthlypurchasematchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : monthlypurchasematchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					matchingids.add(inv.getMatchingId());
				}
			}
		}
		
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList(gstr2Repository.findAll(matchingids));
		List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> notInPurchasesInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();
		if(isNotEmpty(invoices)) {
			matchingstatusInvoices = (List<InvoiceParent>) invoices.getContent();
		}
		if(isNotEmpty(notinpurchasesinvoices)) {
			notInPurchasesInvoices = (List<InvoiceParent>) notinpurchasesinvoices.getContent();
		}
		//List<? extends InvoiceParent> matchingstatusInvoices = invoices.getContent();
		if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
			matchingstatus.addAll(matchingstatusInvoices);
		}
		if(isNotEmpty(notInPurchasesInvoices) && notInPurchasesInvoices.size() > 0) {
			matchingstatus.addAll(notInPurchasesInvoices);
		}
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			for(InvoiceParent gstr2 : monthlyInvoicess) {
				if(isNotEmpty(gstr2.isAmendment()) && gstr2.isAmendment()) {
					matchingstatus.add(gstr2);
				}
			}
			
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
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
		return writer.writeValueAsString(allinvoices);
	}
	
	@RequestMapping(value = "/getgstr2MannualMatchedfyinvss/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getgstr2MannualMatchedfyinvs(@PathVariable("id") String id, @PathVariable("returntype") String returntype, 
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, 
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		String strMonth = month < 10 ? "0" + month : month + "";
		String fp = strMonth+year;
		
		Date stDate = null;
		Date endDate = null;
		Calendar cals = Calendar.getInstance();
		cals.set(year, 3, 1, 0, 0, 0);
		stDate = new java.util.Date(cals.getTimeInMillis());
		cals = Calendar.getInstance();
		cals.set(year+1, 3, 0, 23, 59, 59);
		endDate = new java.util.Date(cals.getTimeInMillis());
		
		Pageable pageable = null;
		String rtStart = "04"+year;
		String rtEnd = "03"+(year+1);		
		List<String> rtArray=Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, 
				"10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd);
		Page<? extends InvoiceParent> notinpurchasesinvoices = clientService.getPresentretperiodgstr2MannualMatchingStatus(clientid,rtArray);
		
		Page<? extends InvoiceParent> monthlypurchaseInvoices = clientService.getPresentMonthfyinvsMannualMatchingIdisNotNull(clientid, stDate,endDate);
		
		List<InvoiceParent> monthlypurchasematchingstatusInvoices = Lists.newArrayList();
		if(isNotEmpty(monthlypurchaseInvoices)) {
			monthlypurchasematchingstatusInvoices = (List<InvoiceParent>) monthlypurchaseInvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlypurchasematchingstatusInvoices) && monthlypurchasematchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : monthlypurchasematchingstatusInvoices) {
				matchingids.add(inv.getMatchingId());
			}
		}
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList(gstr2Repository.findAll(matchingids));
		List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> notInPurchasesInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();
		if(isNotEmpty(notinpurchasesinvoices)) {
			notInPurchasesInvoices = (List<InvoiceParent>) notinpurchasesinvoices.getContent();
		}
		if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
			matchingstatus.addAll(matchingstatusInvoices);
		}
		if(isNotEmpty(notInPurchasesInvoices) && notInPurchasesInvoices.size() > 0) {
			matchingstatus.addAll(notInPurchasesInvoices);
		}
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			matchingstatus.addAll(monthlyInvoicess);
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
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
		return writer.writeValueAsString(allinvoices);
	}
	
	@RequestMapping(value = "/getMannualMatchedfyinvss/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getMannualMatchedAndPresentMonthFYInvoices(@PathVariable("id") String id, @PathVariable("returntype") String returntype, 
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, 
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		//Client client = clientService.findById(clientid);
		String strMonth = month < 10 ? "0" + month : month + "";
		String fp = strMonth+year;
		Pageable pageable = null;
		Date stDate = null;
		Date endDate = null;
		Calendar cals = Calendar.getInstance();
		cals.set(year, 3, 1, 0, 0, 0);
		stDate = new java.util.Date(cals.getTimeInMillis());
		cals = Calendar.getInstance();
		cals.set(year+1, 3, 0, 23, 59, 59);
		endDate = new java.util.Date(cals.getTimeInMillis());
		
		String rtStart = "04"+year;
		String rtEnd = "03"+(year+1);		
		List<String> rtArray=Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, 
				"10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd);
		Page<? extends InvoiceParent> minvoices = clientService.getPresentMonthfyinvsMannualMatchingIdisNotNull(clientid, stDate, endDate);
		Page<? extends InvoiceParent> monthlyinvoices = clientService.getPresentretperiodgstr2MannualMatchingStatus(clientid,rtArray);
		
		List<InvoiceParent> mmatchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();

		
		if(isNotEmpty(minvoices)) {
			mmatchingstatusInvoices = (List<InvoiceParent>) minvoices.getContent();
		}
		
		List<String> mmatchingids = Lists.newArrayList();
		if(isNotEmpty(mmatchingstatusInvoices) && mmatchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : mmatchingstatusInvoices) {
				mmatchingids.add(inv.getMatchingId());
			}
		}
		
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList();
		if(isNotEmpty(monthlyinvoices)) {
			monthlyInvoicess = (List<InvoiceParent>) monthlyinvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			for(InvoiceParent inv : monthlyInvoicess) {
				matchingids.add(inv.getId().toString());
			}
		}
		matchingids.addAll(mmatchingids);
		Page<? extends InvoiceParent> matchedinvoices = clientService.getgstr2MatchingIdsMannualInvoices(clientid,matchingids);
		List<InvoiceParent> matchingInvoices = Lists.newArrayList();
		if(isNotEmpty(matchedinvoices)) {
			matchingInvoices = (List<InvoiceParent>) matchedinvoices.getContent();
		}
		if(isNotEmpty(matchingInvoices) && matchingInvoices.size() > 0) {
			matchingstatus.addAll(matchingInvoices);
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
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
		return writer.writeValueAsString(allinvoices);
	}
	
	@RequestMapping(value="/mismatchInvoice_details/{status}/{invoiceid}", method = RequestMethod.GET)
	public @ResponseBody Map<String, InvoiceParent> getInvoiceParentData(@PathVariable("status") String status,@PathVariable("invoiceid") String invoiceid) {
		Map<String, InvoiceParent> map = Maps.newHashMap();
		InvoiceParent invparent=null;
		if(status.equalsIgnoreCase("Not In GSTR 2A")) {
			invparent=purchaseRepository.findOne(invoiceid);
			if(isNotEmpty(invparent) && invparent.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
				if(isNotEmpty(((PurchaseRegister)invparent).getImpGoods()) && isNotEmpty(((PurchaseRegister)invparent).getImpGoods().get(0))) {
					GSTRB2B gstrb2b = new GSTRB2B();
					if(isNotEmpty(((PurchaseRegister)invparent).getImpGoods().get(0).getStin())) {
						gstrb2b.setCtin(((PurchaseRegister)invparent).getImpGoods().get(0).getStin());
					}else {
						gstrb2b.setCtin(" ");
					}
					List<GSTRInvoiceDetails> inv = Lists.newArrayList();
					GSTRInvoiceDetails gstrinv = new GSTRInvoiceDetails();
					inv.add(gstrinv);
					gstrb2b.setInv(inv);
					List<GSTRB2B> b2b = Lists.newArrayList();
					b2b.add(gstrb2b);
					invparent.setB2b(b2b);
				}
			}
			map.put("PurchaseRegisterinvoice",invparent);
		}else if(status.equalsIgnoreCase("Not In Purchases")) {
			invparent=gstr2Repository.findOne(invoiceid);
			if(isNotEmpty(invparent) && invparent.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
				if(isNotEmpty(((GSTR2)invparent).getImpGoods()) && isNotEmpty(((GSTR2)invparent).getImpGoods().get(0))) {
					GSTRB2B gstrb2b = new GSTRB2B();
					if(isNotEmpty(((GSTR2)invparent).getImpGoods().get(0).getStin())) {
						gstrb2b.setCtin(((GSTR2)invparent).getImpGoods().get(0).getStin());
					}else {
						gstrb2b.setCtin(" ");
					}
					List<GSTRInvoiceDetails> inv = Lists.newArrayList();
					GSTRInvoiceDetails gstrinv = new GSTRInvoiceDetails();
					inv.add(gstrinv);
					gstrb2b.setInv(inv);
					List<GSTRB2B> b2b = Lists.newArrayList();
					b2b.add(gstrb2b);
					invparent.setB2b(b2b);
				}
			}
			map.put("gstr2invoice",invparent);
		}else {
			invparent=purchaseRepository.findOne(invoiceid);
			if(isNotEmpty(invparent) && invparent.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
				if(isNotEmpty(((PurchaseRegister)invparent).getImpGoods()) && isNotEmpty(((PurchaseRegister)invparent).getImpGoods().get(0))) {
					GSTRB2B gstrb2b = new GSTRB2B();
					if(isNotEmpty(((PurchaseRegister)invparent).getImpGoods().get(0).getStin())) {
						gstrb2b.setCtin(((PurchaseRegister)invparent).getImpGoods().get(0).getStin());
					}else {
						gstrb2b.setCtin(" ");
					}
					List<GSTRInvoiceDetails> inv = Lists.newArrayList();
					GSTRInvoiceDetails gstrinv = new GSTRInvoiceDetails();
					inv.add(gstrinv);
					gstrb2b.setInv(inv);
					List<GSTRB2B> b2b = Lists.newArrayList();
					b2b.add(gstrb2b);
					invparent.setB2b(b2b);
				}
			}
			map.put("PurchaseRegisterinvoice",invparent);
			if(isNotEmpty(invparent) && isNotEmpty(invparent.getMatchingId())) {
				invparent=gstr2Repository.findOne(invparent.getMatchingId());
				if(isNotEmpty(invparent) && invparent.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					if(isNotEmpty(((GSTR2)invparent).getImpGoods()) && isNotEmpty(((GSTR2)invparent).getImpGoods().get(0))) {
						GSTRB2B gstrb2b = new GSTRB2B();
						if(isNotEmpty(((GSTR2)invparent).getImpGoods().get(0).getStin())) {
							gstrb2b.setCtin(((GSTR2)invparent).getImpGoods().get(0).getStin());
						}else {
							gstrb2b.setCtin(" ");
						}
						List<GSTRInvoiceDetails> inv = Lists.newArrayList();
						GSTRInvoiceDetails gstrinv = new GSTRInvoiceDetails();
						inv.add(gstrinv);
						gstrb2b.setInv(inv);
						List<GSTRB2B> b2b = Lists.newArrayList();
						b2b.add(gstrb2b);
						invparent.setB2b(b2b);
					}
				}
				map.put("gstr2invoice",invparent);
			}
		}
		//map.put("invoicedetails",invparent);
		return map;
	}
	
	@RequestMapping(value = "/getInvoicesForMannualMatch/{clientid}/{invoiceid}/{returntype}/{month}/{year}/{monthlyOryearly}", method = RequestMethod.GET)
	public @ResponseBody String getInvoicesForMannualMatch(@PathVariable("clientid") String clientid, @PathVariable("invoiceid") String invoiceid, @PathVariable("returntype") String returntype, @PathVariable("month") int month, @PathVariable("year") int year, @PathVariable("monthlyOryearly") String monthlyOryearly, 
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		Pageable pageable = null;
		Date stDate = null;
		Date endDate = null;
		
		String st = request.getParameter("start");
		InvoiceFilter filter = new InvoiceFilter();
		filter.setBooksOrReturns(request.getParameter("booksOrReturns"));
		filter.setPaymentStatus(request.getParameter("paymentStatus"));
		filter.setInvoiceType(request.getParameter("invoiceType"));
		filter.setUser(request.getParameter("user"));
		filter.setVendor(request.getParameter("vendor"));
		filter.setBranch(request.getParameter("branch"));
		filter.setVertical(request.getParameter("vertical"));
		filter.setReverseCharge(request.getParameter("reverseCharge"));
		filter.setSupplyType(request.getParameter("supplyType"));
		filter.setDocumentType(request.getParameter("documentType"));
		filter.setGstno(request.getParameter("gstno"));
		filter.setDateofInvoice(request.getParameter("dateofInvoice"));
		filter.setInvoiceno(request.getParameter("invoiceno"));
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		
		
		
		List<String> rtarray = Lists.newArrayList();
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.B2BA);
		invTypes.add(MasterGSTConstants.CDNA);
		invTypes.add(MasterGSTConstants.ISD);
		invTypes.add(MasterGSTConstants.IMP_GOODS);
		if("monthly".equalsIgnoreCase(monthlyOryearly)) {
			Date presentDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(presentDate);
			
			int presentYear = calendar.get(Calendar.YEAR);
			int presentMonth = calendar.get(Calendar.MONTH)+1;
			Calendar cal = Calendar.getInstance();
			if(month < 10) {
				cal.set(year-1, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				if(presentYear != year) {
					cal.set(year, 9, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}else {
					cal.set(year, presentMonth, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}
				
			}else {
				cal.set(year-1, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				if(presentYear != year) {
					cal.set(year, 9, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}else {
					cal.set(year, presentMonth, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}
			}
		}else {
			int yr = year;
			if(month < 4) {
				yr = yr-1;
				Calendar cal = Calendar.getInstance();
				cal.set(year-1, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(year, 3, 0, 23, 59, 59);
				endDate = new java.util.Date(cal.getTimeInMillis());
			}else {
				Calendar cal = Calendar.getInstance();
				cal.set(year, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(year+1, 3, 0, 23, 59, 59);
				endDate = new java.util.Date(cal.getTimeInMillis());
			}
			//String year = fp.substring(2);
			
			Date presentDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(presentDate);
			int presentYear = calendar.get(Calendar.YEAR);
			for(int i=yr;i<=presentYear;i++) {
				for(int j=4;j<=12;j++) {
					String strMonth = j < 10 ? "0" + j : j + "";
					rtarray.add(strMonth+(i));
				}
				for(int k=1;k<=3;k++) {
					String strMonth = k < 10 ? "0" + k : k + "";
					rtarray.add(strMonth+(i+1));
				}
			}
			
		}
		Page<? extends InvoiceParent> invoices = null;
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		if(returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) {
			List<String> matchingstatus = new ArrayList<String>();
			matchingstatus.add("Not In Purchases");
			matchingstatus.add(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
				pageable = new PageRequest(0, Integer.MAX_VALUE);
				if("monthly".equalsIgnoreCase(monthlyOryearly)) {
					invoicesMap.put("invoices",gstr2aDao.findByClientidAndMonthAndYearForMannualMatch(clientid, invTypes, matchingstatus, stDate, endDate, start, length, searchVal, filter));
					invoices = (Page<? extends InvoiceParent>)invoicesMap.get("invoices");
				}else {
					
					invoicesMap.put("invoices",gstr2aDao.findByClientidAndFpinForMannualMatch(clientid, invTypes, matchingstatus, rtarray, start, length, searchVal, filter));
					invoices = (Page<? extends InvoiceParent>)invoicesMap.get("invoices");
				}
			
		}else {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
			List<String> matchingstatus = new ArrayList<String>();
			matchingstatus.add(MasterGSTConstants.GST_STATUS_NOTINGSTR2A);
			matchingstatus.add(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
			
			invoicesMap.put("invoices",purchageRegisterDao.findByClientidAndMonthAndYearForMannualMatch(clientid, invTypes, matchingstatus, stDate, endDate, start, length, searchVal, filter));
			invoices = (Page<? extends InvoiceParent>)invoicesMap.get("invoices");
			
		}
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
		return writer.writeValueAsString(invoicesMap);
	}
	
	
	
}
