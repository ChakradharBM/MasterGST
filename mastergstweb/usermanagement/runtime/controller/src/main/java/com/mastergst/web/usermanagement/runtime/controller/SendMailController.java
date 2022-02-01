package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.DecimalFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.DeliveryChallan;
import com.mastergst.usermanagement.runtime.domain.Estimates;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.ProformaInvoices;
import com.mastergst.usermanagement.runtime.domain.Reminders;
import com.mastergst.usermanagement.runtime.repository.CompanyCustomersRepository;
import com.mastergst.usermanagement.runtime.repository.DeliveryChallanRepository;
import com.mastergst.usermanagement.runtime.repository.EstimatesRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;
import com.mastergst.usermanagement.runtime.repository.ProformaInvoicesRepository;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.PrintService;

@Controller
public class SendMailController {
	private static final Logger logger = LogManager.getLogger(SendMailController.class.getName());
	private static final String CLASSNAME = "SendMailController::";
	 private static DecimalFormat df2 = new DecimalFormat("#.##");
	 @Autowired
	 private PrintService printService;
	 @Autowired
	 private CompanyCustomersRepository companyCustomersRepository;
	 @Autowired
	 private ClientService clientService;
	 @Autowired
	 private GSTR1Repository gstr1Repository;
	 @Autowired
	 private DeliveryChallanRepository deliveryChallanRepository;
	 @Autowired
	 private ProformaInvoicesRepository proformaInvoicesRepository;
	 @Autowired
	 private EstimatesRepository estimatesRepository;
		
		@RequestMapping(value="/getCustomerDetails/{clientid}/{billedtoname}")
		public @ResponseBody Map<String, String> getCustomersData(@PathVariable String clientid,@PathVariable String billedtoname) {
			Map<String, String> map = Maps.newHashMap();
			CompanyCustomers customers = null;
			if(isNotEmpty(clientid) && isNotEmpty(billedtoname)) {
				customers = companyCustomersRepository.findByNameAndClientid(billedtoname, clientid);
			}
			if(isNotEmpty(customers) && isNotEmpty(customers.getEmail())) {
				map.put("customerEmail",customers.getEmail());
			}
			return map;
		}
		@RequestMapping(value="/getClientDetails/{clientid}")
		public @ResponseBody Client getClientData(@PathVariable String clientid) {
			String clientSignDetails="";
			Client client = clientService.findById(clientid);
			if(isNotEmpty(client) && isNotEmpty(client.getClientSignature())) {
				clientSignDetails = client.getClientSignature();
				if(clientSignDetails.contains("\r")) {
					clientSignDetails = clientSignDetails.replaceAll("\r", "#mgst#");
				}else if(clientSignDetails.contains("\n")) {
					clientSignDetails = clientSignDetails.replaceAll("\n", "#mgst#");
				}else if(clientSignDetails.contains("\r\n")) {
					clientSignDetails = clientSignDetails.replaceAll("\r\n", "#mgst#");
				}
			}
			if(isNotEmpty(client)) {
				client.setClientSignature(clientSignDetails);
			}
			return client;
		}
		
		@Async
		 @RequestMapping(value ="/sendMails/{userid}/{invoiceid}/{returntype}/{clientid}", method = RequestMethod.POST)
			public @ResponseBody void sendReminders(@RequestBody Reminders reminder,@PathVariable("userid") String userid,
					@PathVariable("invoiceid") String invoiceid,@PathVariable("returntype") String returntype,@PathVariable("clientid") String clientid,
					@RequestParam("signcheck") boolean signcheck,HttpServletRequest request)throws MasterGSTException {
			InvoiceParent invoice = null;
			if(isNotEmpty(returntype)) {
				if(returntype.equals(MasterGSTConstants.GSTR1)) {
					invoice = gstr1Repository.findOne(invoiceid);
				}else if(returntype.equals(MasterGSTConstants.DELIVERYCHALLANS)) {
					invoice = deliveryChallanRepository.findOne(invoiceid);
				}else if(returntype.equals(MasterGSTConstants.PROFORMAINVOICES)) {
					invoice = proformaInvoicesRepository.findOne(invoiceid);
				}else if(returntype.equals(MasterGSTConstants.ESTIMATES)) {
					invoice = estimatesRepository.findOne(invoiceid);
				}else if(returntype.equals(MasterGSTConstants.EINVOICE)) {
					invoice = gstr1Repository.findOne(invoiceid);
				}
			}
			String mail="";String ccmail="";
			if(isNotEmpty(reminder.getEmail())) {
				saveCustomerMail(invoice,reminder.getEmail().get(0), returntype);
				invoice.setCustomerEmail(reminder.getEmail().get(0));
				for(String mailid : reminder.getEmail()) {
					mail += mailid;
				}
				invoice.setCustomerMailIds(mail.replaceFirst(",", ""));
				for(String ccmailid : reminder.getCc()) {
					ccmail += ","+ccmailid;
				}
				invoice.setCustomerCCMailIds(ccmail.replaceFirst(",", ""));
				if(isNotEmpty(reminder.getSubject())) {
					invoice.setMailSubject(reminder.getSubject());
				}
				if(isNotEmpty(reminder.getClientName())) {
					invoice.setBilledtoname(reminder.getClientName());
				}
				if(isNotEmpty(reminder.getMessage())) {
					invoice.setMailMessage(reminder.getMessage());
				}
				if(isNotEmpty(signcheck)) {
					invoice.setIsIncludeSignature(signcheck);
				}
				if(isNotEmpty(returntype)) {
					if(returntype.equals(MasterGSTConstants.GSTR1)) {
						gstr1Repository.save((GSTR1) invoice);
					}else if(returntype.equals(MasterGSTConstants.DELIVERYCHALLANS)) {
						deliveryChallanRepository.save((DeliveryChallan) invoice);
					}else if(returntype.equals(MasterGSTConstants.PROFORMAINVOICES)) {
						proformaInvoicesRepository.save((ProformaInvoices) invoice);
					}else if(returntype.equals(MasterGSTConstants.ESTIMATES)) {
						estimatesRepository.save((Estimates) invoice);
					}else if(returntype.equals(MasterGSTConstants.EINVOICE)) {
						gstr1Repository.save((GSTR1) invoice);
					}
				}
				if(returntype.equals(MasterGSTConstants.EINVOICE)) {
					for(String mailid : reminder.getEmail()) {
						printService.sendEinvoicePDFAttachment(mailid,reminder.getCc(),reminder,userid, clientid, invoiceid, signcheck, returntype, request);
					}
				}else {
					for(String mailid : reminder.getEmail()) {
						printService.sendPDFAttachment(mailid,reminder.getCc(),reminder,userid, clientid, invoiceid, signcheck, returntype, request);
					}
				}
			} 
		}
		private void saveCustomerMail(InvoiceParent invoice, String mail, String returntype) {
			CompanyCustomers customer=null;
			if(isNotEmpty(invoice) && isNotEmpty(invoice.getBilledtoname())) {
				customer = companyCustomersRepository.findByNameAndClientid(invoice.getBilledtoname(), invoice.getClientid());
			}
			if(isNotEmpty(mail)) {
				if(isNotEmpty(customer)) {
					if(NullUtil.isEmpty(customer.getEmail())) {
						customer.setEmail(mail);
						companyCustomersRepository.save(customer);
					}
				}
				invoice.setCustomerEmail(mail);
				if(returntype.equals(MasterGSTConstants.EINVOICE)) {
					if(isNotEmpty(invoice) && isNotEmpty(invoice.getBuyerDtls())) {
						if(NullUtil.isEmpty(invoice.getBuyerDtls().getEm())) {
							invoice.getBuyerDtls().setEm(mail);
						}
					}
				}
			}
		}
}
