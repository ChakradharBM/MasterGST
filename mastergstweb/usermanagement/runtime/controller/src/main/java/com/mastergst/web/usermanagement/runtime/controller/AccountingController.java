package com.mastergst.web.usermanagement.runtime.controller;
import static com.mastergst.core.common.MasterGSTConstants.BEGIN;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mastergst.core.common.AccountConstants;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ProfileService;

@Controller
public class AccountingController {
	private static final Logger logger = LogManager.getLogger(AccountingController.class.getName());
	private static final String CLASSNAME = "AccountingController::";
	
	@Autowired	private ClientService clientService;
	@Autowired	private ProfileService profileService;
	
	
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	/*
	 * @RequestMapping(value =
	 * "/trailbalancereports/{id}/{name}/{usertype}/{clientid}/{month}/{year}",
	 * method = RequestMethod.GET) public String
	 * trailbalancereportsView(@PathVariable("id") String id, @PathVariable("name")
	 * String fullname,
	 * 
	 * @PathVariable("usertype") String usertype, @PathVariable("clientid") String
	 * clientid,
	 * 
	 * @PathVariable("month") int month, @PathVariable("year") int year, ModelMap
	 * model, HttpServletRequest request) throws Exception { final String method =
	 * "Report::"; logger.debug(CLASSNAME + method + BEGIN); logger.debug(CLASSNAME
	 * + method + "id\t" + id); logger.debug(CLASSNAME + method + "fullname\t" +
	 * fullname); logger.debug(CLASSNAME + method + "clientid\t" + clientid);
	 * updateModel(model, id, fullname, usertype, month, year); User user =
	 * userService.findById(id);
	 * 
	 * Client client = clientService.findById(clientid);
	 * model.addAttribute("client", client); return "reports/trialbalance_report"; }
	 */
		
		@RequestMapping(value = "/balancesheetreports/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
		public String balancesheetreportsView(@PathVariable("id") String id, @PathVariable("name") String fullname,
				@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
				@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,
				HttpServletRequest request) throws Exception {
			final String method = "Report::";
			logger.debug(CLASSNAME + method + BEGIN);
			logger.debug(CLASSNAME + method + "id\t" + id);
			logger.debug(CLASSNAME + method + "fullname\t" + fullname);
			logger.debug(CLASSNAME + method + "clientid\t" + clientid);
			updateModel(model, id, fullname, usertype, month, year);
			Client client = clientService.findById(clientid);
			model.addAttribute("client", client);
			return "reports/balancesheet_report";
		}
		
		@RequestMapping(value = "/pandlreports/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
		public String PandLreportsView(@PathVariable("id") String id, @PathVariable("name") String fullname,
				@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
				@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,
				HttpServletRequest request) throws Exception {
			final String method = "Report::";
			logger.debug(CLASSNAME + method + BEGIN);
			logger.debug(CLASSNAME + method + "id\t" + id);
			logger.debug(CLASSNAME + method + "fullname\t" + fullname);
			logger.debug(CLASSNAME + method + "clientid\t" + clientid);
			updateModel(model, id, fullname, usertype, month, year);
			Client client = clientService.findById(clientid);
			model.addAttribute("client", client);
			return "reports/PandL_report";
		}
		
		@RequestMapping(value = "/tdsreport/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
		public String TDSreportView(@PathVariable("id") String id, @PathVariable("name") String fullname,
				@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
				@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,
				HttpServletRequest request) throws Exception {
			final String method = "Report::";
			logger.debug(CLASSNAME + method + BEGIN);
			logger.debug(CLASSNAME + method + "id\t" + id);
			logger.debug(CLASSNAME + method + "fullname\t" + fullname);
			logger.debug(CLASSNAME + method + "clientid\t" + clientid);
			updateModel(model, id, fullname, usertype, month, year);
			Client client = clientService.findById(clientid);
			model.addAttribute("client", client);
			return "reports/TDS_report";
		}
		
		@RequestMapping(value = "/agingreport/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
		public String agingreportView(@PathVariable("id") String id, @PathVariable("name") String fullname,
				@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
				@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,
				HttpServletRequest request) throws Exception {
			final String method = "Report::";
			logger.debug(CLASSNAME + method + BEGIN);
			logger.debug(CLASSNAME + method + "id\t" + id);
			logger.debug(CLASSNAME + method + "fullname\t" + fullname);
			logger.debug(CLASSNAME + method + "clientid\t" + clientid);
			updateModel(model, id, fullname, usertype, month, year);
			Client client = clientService.findById(clientid);
			model.addAttribute("client", client);
			return "reports/aging_report";
		}
		
		@RequestMapping(value = "/ledgerNames/{id}/{clientid}/{returntype}", method = RequestMethod.GET)
		public @ResponseBody List<String> fetchBankdetails(@PathVariable("id") String id,@PathVariable("clientid") String clientid,@PathVariable("returntype") String returntype, ModelMap model) {
			final String method = "fetchBankdetails::";
			logger.debug(CLASSNAME + method + BEGIN);
			List<String> ledgeNames = profileService.ledgerNamesForBankCash(clientid);
			if(ledgeNames.isEmpty()) {
				ledgeNames.add("Bank");
				ledgeNames.add("Cash");
				if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
					ledgeNames.add(AccountConstants.TDS_RECEIVABLE);
					ledgeNames.add("Discount Allowed");
				}else {
					ledgeNames.add(AccountConstants.TDS_PAYABLE);
					ledgeNames.add("Discount Received");
				}
				ledgeNames.add("TDS-GST");
				ledgeNames.add("Discount");
			}else {
				
				if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
					ledgeNames.add(AccountConstants.TDS_RECEIVABLE);
					ledgeNames.add("Discount Allowed");
				}else {
					ledgeNames.add(AccountConstants.TDS_PAYABLE);
					ledgeNames.add("Discount Received");
				}
				ledgeNames.add("TDS-GST");
			}
			return ledgeNames;
			
		}
		
}