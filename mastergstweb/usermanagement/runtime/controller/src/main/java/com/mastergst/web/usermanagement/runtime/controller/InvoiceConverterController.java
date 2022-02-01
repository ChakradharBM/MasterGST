package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CompanyInvoices;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.ProformaInvoices;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.repository.AccountingJournalRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;
import com.mastergst.usermanagement.runtime.repository.ProformaInvoicesRepository;
import com.mastergst.usermanagement.runtime.repository.PurchaseOrderRepository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ProfileService;

@Controller
public class InvoiceConverterController {
	private static final Logger logger = LogManager.getLogger(InvoiceConverterController.class.getName());
	private static final String CLASSNAME = "InvoiceConverterController::";
	@Autowired
	private ProformaInvoicesRepository proformaInvoicesRepository;
	@Autowired
	private PurchaseOrderRepository purchaseOrderRepository;
	@Autowired
	AccountingJournalRepository accountingJournalRepository;
	@Autowired
	PurchaseRegisterRepository purchaseRepository;
	@Autowired
	private GSTR1Repository gstr1repository;
	@Autowired
	private ClientService clientService;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private UserService userService;
	
	public String InvSerialNoPI(Client client,String userid,int month,int year,String returntype) {
		String financialYear = (year - 1) + "-" + (year);
		if (month > 3) {
			financialYear = year + "-" + (year + 1);
		}
		CompanyInvoices invoiceConfig = profileService.getInvoiceConfigDetails(client.getId().toString(), financialYear, MasterGSTConstants.B2B, returntype);
		String startInvNo="";
	if(isNotEmpty(invoiceConfig)) {
		 startInvNo = clientService.invoiceNumberformat(invoiceConfig, financialYear, month);
		String sInvNo = "";
		sInvNo = startInvNo;
		String prevno = "";
		String[] years = financialYear.split("-");
		int yr;
		if (month > 3) {
			yr = Integer.parseInt(years[0]);
		} else {
			yr = Integer.parseInt(years[1]);
		}
		String invoiceConfigType = invoiceConfig.getInvoiceType();
		String invnumbercutoff = "Yearly";
		List<? extends InvoiceParent> invoices = clientService.getInvoicesByType(client, userid, returntype, MasterGSTConstants.B2B,invoiceConfigType, month, yr, invnumbercutoff);
		if (isNotEmpty(invoices)) {
			int stInvNo = invoiceConfig.getStartInvoiceNo();
			int stInv = 0;
			for (int i = 1; i <= 12; i++) {
				String stInvn = clientService.invNumberformat(invoiceConfig, i, financialYear);
				for (InvoiceParent invs : invoices) {
					if (isNotEmpty(invs.getInvoiceno())) {
						if (invs.getInvoiceno().startsWith(stInvn)) {
							String invno = invs.getInvoiceno().substring(sInvNo.length());
							int inv = Integer.parseInt(invno);
							if (stInvNo <= inv) {
								stInvNo = inv;
								stInv++;
							}
						}
					}
				}
			}
			if (stInvNo != invoiceConfig.getStartInvoiceNo() || stInv != 0) {
				prevno = startInvNo;
				prevno += stInvNo;
				stInvNo++;
			}
			startInvNo += stInvNo;
		} else {
			if (isNotEmpty(invoiceConfig.getStartInvoiceNo())) {
				startInvNo += invoiceConfig.getStartInvoiceNo();
			}
		}
	}
		return startInvNo;
}
	public InvoiceParent getPInvDetails(InvoiceParent invoice,String invoiceid,String startInvNo,Client client,int month,int year) throws IllegalAccessException, InvocationTargetException {
		boolean isIntraState = true;boolean isInvExist=false;
		if (isNotEmpty(invoice) && isNotEmpty(invoice.getStatename())) {
			if (!invoice.getStatename().equals(client.getStatename())) {
				isIntraState = false;
			}
		}
 		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, 0, 23, 59, 59);
		Date stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 23, 59, 59);
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		List<GSTR1> gstr1Invoice = gstr1repository.findByClientidAndInvoicenoAndDateofinvoiceBetween(invoice.getClientid(), invoice.getInvoiceno(), stDate, endDate);
		if(isNotEmpty(gstr1Invoice) && gstr1Invoice.size() >= 1){
			isInvExist=true;
		}else{
			isInvExist=false;
			GSTR1 gstrinv = new GSTR1();
			if(isNotEmpty(invoice)) {
				BeanUtils.copyProperties(gstrinv, invoice);
				
				if(isNotEmpty(startInvNo)) {
					gstrinv.setInvoiceno(startInvNo);
					if(isNotEmpty(gstrinv) && isNotEmpty(gstrinv.getB2b()) && isNotEmpty(gstrinv.getB2b().get(0)) && isNotEmpty(gstrinv.getB2b().get(0).getInv()) && isNotEmpty(gstrinv.getB2b().get(0).getInv().get(0))) {
						gstrinv.getB2b().get(0).getInv().get(0).setInum(startInvNo);
					}
				}else {
					gstrinv.setInvoiceno(invoice.getInvoiceno());
				}
				gstrinv.setId(new ObjectId(invoiceid));
				gstrinv.setInvtype("B2B");
				gstrinv.setRevchargetype("Regular");
				gstrinv.setDateofinvoice(new Date());
				if (isNotEmpty(gstrinv.getDateofinvoice())) {
					Calendar cals = Calendar.getInstance();
					cals.setTime(gstrinv.getDateofinvoice());
					month = cals.get(Calendar.MONTH) + 1;
					year = cals.get(Calendar.YEAR);
				}
				String strMonth = month < 10 ? "0" + month : month + "";
				gstrinv.setFp(strMonth + year);
				//invoice = clientService.populateInvoiceInfo(invoice, GSTR1, isIntraState);
				
				InvoiceParent invoiceForJournal= clientService.saveSalesInvoice(gstrinv,null, isIntraState);
				saveJournalEntries(invoiceForJournal,invoiceForJournal.getClientid(),"GSTR1",isIntraState);
				//proformaInvoicesRepository.delete(invoiceid);
				accountingJournalRepository.delete(invoiceid);
			}
	}
		return invoice;
	}
	public InvoiceParent getPOInvDetails(InvoiceParent invoice,String invoiceid,Client client,int month,int year) throws IllegalAccessException, InvocationTargetException{
		boolean isIntraState = true;
		boolean isInvExist=false;
		if (isNotEmpty(invoice) && isNotEmpty(invoice.getStatename())) {
			if (!invoice.getStatename().equals(client.getStatename())) {
				isIntraState = false;
			}
		}
 		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, 0, 23, 59, 59);
		Date stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 23, 59, 59);
		Date endDate = new java.util.Date(cal.getTimeInMillis());
		List<PurchaseRegister> gstr2Invoice = purchaseRepository.findByClientidAndInvoicenoAndDateofinvoiceBetween(invoice.getClientid(), invoice.getInvoiceno(), stDate, endDate);
		if(isNotEmpty(gstr2Invoice) && gstr2Invoice.size() >= 1){
			isInvExist=true;
		}else{
			isInvExist=false;
			PurchaseRegister gstrinv = new PurchaseRegister();
			if(isNotEmpty(invoice)) {
				BeanUtils.copyProperties(gstrinv, invoice);
				gstrinv.setId(new ObjectId(invoiceid));
				gstrinv.setInvtype("B2B");
				gstrinv.setRevchargetype("Regular");
				gstrinv.setDateofinvoice(new Date());
				if (isNotEmpty(gstrinv.getDateofinvoice())) {
					Calendar cals = Calendar.getInstance();
					cals.setTime(gstrinv.getDateofinvoice());
					month = cals.get(Calendar.MONTH) + 1;
					year = cals.get(Calendar.YEAR);
				}
				String strMonth = month < 10 ? "0" + month : month + "";
				gstrinv.setFp(strMonth + year);
				InvoiceParent invoiceParent = clientService.savePurchaseRegister(gstrinv, isIntraState); 
				saveJournalEntries(invoiceParent,invoiceParent.getClientid(),"GSTR2",isIntraState);
				//purchaseOrderRepository.delete(invoiceid);
				accountingJournalRepository.delete(invoiceid);
			}
	}
		return invoice;
	}
		@RequestMapping(value = "/convertPIToInvoice/{userid}/{clientid}/{invoiceid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
		public String convertPIToInvoice(@PathVariable("userid") String userid,@PathVariable("clientid") String clientid, @PathVariable("invoiceid") String invoiceid,@PathVariable("returntype") String returntype, 
				@PathVariable("month") int month,@PathVariable("year") int year,ModelMap model, HttpServletRequest request) throws Exception {
			final String method = "convertPIToInvoice::";
			logger.debug(CLASSNAME + method + BEGIN);
			InvoiceParent invoice = null;
			Client client = clientService.findById(clientid);
			invoice = proformaInvoicesRepository.findOne(invoiceid);
	 		String startInvNo= InvSerialNoPI(client, userid, month,year,MasterGSTConstants.GSTR1);
	 		if(isNotEmpty(invoice)) {
	 			invoice.setConvertedtoinv("converted");
				proformaInvoicesRepository.save((ProformaInvoices) invoice);
	 			InvoiceParent inv = getPInvDetails(invoice,invoiceid,startInvNo,client,month,year);
	 		}
	 		User user = userService.findById(userid);
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
				CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
				if(isNotEmpty(companyUser)) {
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
			return "client/all_invoice_view";
		}
		@RequestMapping(value = "/all_convertPIToInvoice/{userid}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
		public String allInvConvertPIToInvoice(@PathVariable("userid") String userid,@PathVariable("clientid") String clientid,@PathVariable("returntype") String returntype, 
				@PathVariable("month") int month,@PathVariable("year") int year,@RequestParam(value = "invoiceIds", required = true) List<String> invoiceIds,ModelMap model) throws Exception {
			final String method = "allInvConvertPIToInvoice ::";
			logger.debug(CLASSNAME + method + BEGIN);
			InvoiceParent invoiceparent = null;
			Client client = clientService.findById(clientid);
			for(String invids : invoiceIds) {
				invoiceparent = proformaInvoicesRepository.findOne(invids);
				String startInvNo= InvSerialNoPI(client, userid, month,year,MasterGSTConstants.GSTR1);
				if(isNotEmpty(invoiceparent)) {
					invoiceparent.setConvertedtoinv("converted");
					proformaInvoicesRepository.save((ProformaInvoices) invoiceparent);
					InvoiceParent inv = getPInvDetails(invoiceparent,invids,startInvNo,client,month,year);
				}
			}
			User user = userService.findById(userid);
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
				CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
				if(isNotEmpty(companyUser)) {
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
			return "client/all_invoice_view";
	}
		@RequestMapping(value = "/convertPOToInvoice/{userid}/{clientid}/{invoiceid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
		public String convertPOToInvoice(@PathVariable("userid") String userid,@PathVariable("clientid") String clientid, @PathVariable("invoiceid") String invoiceid,@PathVariable("returntype") String returntype, 
				@PathVariable("month") int month,@PathVariable("year") int year,ModelMap model, HttpServletRequest request) throws Exception {
			final String method = "convertPOToInvoice::";
			logger.debug(CLASSNAME + method + BEGIN);
			InvoiceParent invoice = null;
			Client client = clientService.findById(clientid);
			invoice = purchaseOrderRepository.findOne(invoiceid);
	 		if(isNotEmpty(invoice)) {
	 			InvoiceParent inv = getPOInvDetails(invoice,invoiceid,client,month,year);
	 		}
	 		User user = userService.findById(userid);
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
				CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
				if(isNotEmpty(companyUser)) {
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
			return "client/all_invoice_view";
		}
		@RequestMapping(value = "/all_convertPOToInvoice/{userid}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
		public String allInvConvertPOToInvoice(@PathVariable("userid") String userid,@PathVariable("clientid") String clientid,@PathVariable("returntype") String returntype, 
				@PathVariable("month") int month,@PathVariable("year") int year,@RequestParam(value = "invoiceIds", required = true) List<String> invoiceIds,ModelMap model) throws Exception {
			final String method = "allInvConvertPOToInvoice ::";
			logger.debug(CLASSNAME + method + BEGIN);
			InvoiceParent invoiceparent = null;
			Client client = clientService.findById(clientid);
			for(String invids : invoiceIds) {
				invoiceparent = purchaseOrderRepository.findOne(invids);
				if(isNotEmpty(invoiceparent)) {
					InvoiceParent inv = getPOInvDetails(invoiceparent,invids,client,month,year);
				}
			}
			User user = userService.findById(userid);
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
				CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
				if(isNotEmpty(companyUser)) {
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
			return "client/all_invoice_view";
	}
		@Async
		public void saveJournalEntries(InvoiceParent invoiceForJournal, String clientid, String returntype,boolean isIntraState) {
			clientService.saveJournalInvoice(invoiceForJournal,clientid,returntype,isIntraState);
		}

}
