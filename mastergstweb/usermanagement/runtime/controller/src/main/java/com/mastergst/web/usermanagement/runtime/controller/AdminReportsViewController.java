package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mastergst.core.util.NullUtil;

@Controller
public class AdminReportsViewController {
	private static final Logger logger = LogManager.getLogger(AdminReportsViewController.class.getName());
	private static final String CLASSNAME = "AdminReportsViewController::";
		
	@RequestMapping("/adminreports")
	public String reportViewName(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "usertype", required = true) String userType,
			@RequestParam(value = "type", required = true) String reportType, ModelMap model) {
		String viewName = null;
		final String method = "reportViewName::";
		logger.debug(CLASSNAME + method + BEGIN);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", userType);
		
		if(NullUtil.isNotEmpty(reportType)) {
			
			switch(reportType) {
				case "paidexpiredyusers": viewName= "paidExpiredUsers";
					break;
				case "expiredyusers": viewName= "expiredUsers";
					break;
				case "beforeexpiryusers": viewName= "beforeExpiryUsers";
					break;
				case "partnerpayments": viewName= "partnerpayment_report";
					break;
				case "getapisexceeds": viewName= "apisexceeds_report";
					break;
				case "monthlyapiusage": viewName= "monthlyDashboard";
					break;
				case "monthlywiseusagereport": viewName= "monthlyWiseReport";
					break;
				case "apiversion": viewName= "apiversionpage";
					break;
				case "activeUsers": viewName= "activeUsers";
					break;
				case "demoUsers": viewName= "demoUsers";
					break;
				case "subscriptionsummaryreport": viewName= "subscriptionsummaryReport";
					break;
				case "userscountsummaryreport": viewName= "userCountSummaryReport";
					break;	
				case "reconcilestatus": viewName ="reconcile_status";
					break;
			}
		}
		logger.debug(CLASSNAME + method + END);
		return "admin/"+viewName;
	}
}
