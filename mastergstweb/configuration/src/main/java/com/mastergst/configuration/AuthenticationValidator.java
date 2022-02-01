/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationValidator {
	
	private String STATIC_PATH = null;
	private String ACTUATOR_PATH = null;
	private Map<String, String> OPEN_URIS = new HashMap<>();
	
	public AuthenticationValidator(String contextPath){
		
		STATIC_PATH = contextPath+"/static";
		ACTUATOR_PATH = contextPath+"/manage";
		String LOGIN_PATH1 = contextPath+("/".equals(contextPath) ? "" : "/");
		OPEN_URIS.put(LOGIN_PATH1, LOGIN_PATH1);
		OPEN_URIS.put(contextPath+"/static", contextPath+"/static");
		OPEN_URIS.put(contextPath+"/login", contextPath+"/login");
		OPEN_URIS.put(contextPath+"/signin", contextPath+"/signin");
		OPEN_URIS.put(contextPath+"/createuser", contextPath+"/createuser");
		OPEN_URIS.put(contextPath+"/otpstatus", contextPath+"/otpstatus");
		OPEN_URIS.put(contextPath+"/otpsubmit", contextPath+"/otpsubmit");
		OPEN_URIS.put(contextPath+"/otptryagian", contextPath+"/otptryagian");
		OPEN_URIS.put(contextPath+"/resetsubmit", contextPath+"/resetsubmit");
		OPEN_URIS.put(contextPath+"/reset", contextPath+"/reset");
		OPEN_URIS.put(contextPath+"/terms", contextPath+"/terms");
		OPEN_URIS.put(contextPath+"/privacy", contextPath+"/privacy");
		OPEN_URIS.put(contextPath+"/errorcodes", contextPath+"/errorcodes");
		OPEN_URIS.put(contextPath+"/e-inovoice-errorcodes", contextPath+"/e-inovoice-errorcodes");
		OPEN_URIS.put(contextPath+"/statecode-list", contextPath+"/statecode-list");
		OPEN_URIS.put(contextPath+"/eway-errorcodes", contextPath+"/eway-errorcodes");
		OPEN_URIS.put(contextPath+"/resetpwd", contextPath+"/resetpwd");
		OPEN_URIS.put(contextPath+"/signupall", contextPath+"/signupall");
		OPEN_URIS.put(contextPath+"/signup", contextPath+"/signup");
		OPEN_URIS.put(contextPath+"/health", contextPath+"/health");
		OPEN_URIS.put(contextPath+"/info", contextPath+"/info");
		OPEN_URIS.put(contextPath+"/metrics", contextPath+"/metrics");
		OPEN_URIS.put(contextPath+"/trace", contextPath+"/trace");
		OPEN_URIS.put(contextPath+"/loggers", contextPath+"/loggers");
		OPEN_URIS.put(contextPath+"/manage", contextPath+"/manage");
		OPEN_URIS.put(contextPath+"/hsnsacconfig", contextPath+"/hsnsacconfig");
		OPEN_URIS.put(contextPath+"/stateconfig", contextPath+"/stateconfig");
		OPEN_URIS.put(contextPath+"/hsnconfig", contextPath+"/hsnconfig");
		OPEN_URIS.put(contextPath+"/sacconfig", contextPath+"/sacconfig");
		OPEN_URIS.put(contextPath+"/uqcconfig", contextPath+"/uqcconfig");
		OPEN_URIS.put(contextPath+"/publicsearch1", contextPath+"/publicsearch1");
		OPEN_URIS.put(contextPath+"/publicsearch", contextPath+"/publicsearch");
		OPEN_URIS.put(contextPath+"/updtusrdate", contextPath+"/updtusrdate");
		OPEN_URIS.put(contextPath+"/updtjournalDetails", contextPath+"/updtjournalDetails");
		OPEN_URIS.put(contextPath+"/updtclntUsermappingDetails", contextPath+"/updtclntUsermappingDetails");
		OPEN_URIS.put(contextPath+"/updtSubscriptionDetails", contextPath+"/updtSubscriptionDetails");
		OPEN_URIS.put(contextPath+"/updtitemcategory", contextPath+"/updtitemcategory");
		OPEN_URIS.put(contextPath+"/updtclntgstr9", contextPath+"/updtclntgstr9");
		OPEN_URIS.put(contextPath+"/updtcompanyroles", contextPath+"/updtcompanyroles");
		OPEN_URIS.put(contextPath+"/updateUsersSequenceId", contextPath+"/updateUsersSequenceId");
		OPEN_URIS.put(contextPath+"/updateUserRoles", contextPath+"/updateUserRoles");
		OPEN_URIS.put(contextPath+"/updateUserNewReturnsActionsRoles", contextPath+"/updateUserNewReturnsActionsRoles");

		OPEN_URIS.put(contextPath+"/updateParentClientInfo", contextPath+"/updateParentClientInfo");
		OPEN_URIS.put(contextPath+"/updateUsers", contextPath+"/updateUsers");
		OPEN_URIS.put(contextPath+"/prinvoices", contextPath+"/prinvoices");
		OPEN_URIS.put(contextPath+"/gstr2ainvoices", contextPath+"/gstr2ainvoices");
		OPEN_URIS.put(contextPath+"/updateCompanyRoles", contextPath+"/updateCompanyRoles");
		OPEN_URIS.put(contextPath+"/migrate", contextPath+"/migrate");
		OPEN_URIS.put(contextPath+"/migratepr", contextPath+"/migratepr");
		OPEN_URIS.put(contextPath+"/migrate2a", contextPath+"/migrate2a");
		OPEN_URIS.put(contextPath+"/migratepdee", contextPath+"/migratepdee");
		OPEN_URIS.put(contextPath+"/migratesumstrs", contextPath+"/migratesumstrs");
		OPEN_URIS.put(contextPath+"/migrategstr1sumstrs", contextPath+"/migrategstr1sumstrs");
		OPEN_URIS.put(contextPath+"/migrateprsumstrs", contextPath+"/migrateprsumstrs");
		OPEN_URIS.put(contextPath+"/migrategstr21718sumstrs", contextPath+"/migrategstr21718sumstrs");
		OPEN_URIS.put(contextPath+"/migrategstr21819sumstrs", contextPath+"/migrategstr21819sumstrs");
		OPEN_URIS.put(contextPath+"/migrategstr21920sumstrs", contextPath+"/migrategstr21920sumstrs");
		OPEN_URIS.put(contextPath+"/migrateb2cinv", contextPath+"/migrateb2cinv");
		OPEN_URIS.put(contextPath+"/migratepartnerclient", contextPath+"/migratepartnerclient");
		
		OPEN_URIS.put(contextPath+"/migrategstr11718sumstrs", contextPath+"/migrategstr11718sumstrs");
		OPEN_URIS.put(contextPath+"/migrategstr11819sumstrs", contextPath+"/migrategstr11819sumstrs");
		OPEN_URIS.put(contextPath+"/migrategstr11920sumstrs", contextPath+"/migrategstr11920sumstrs");
		OPEN_URIS.put(contextPath+"/migrategstr12021sumstrs", contextPath+"/migrategstr12021sumstrs");
		OPEN_URIS.put(contextPath+"/migrategstr12122sumstrs", contextPath+"/migrategstr12122sumstrs");
		
		OPEN_URIS.put(contextPath+"/migratepr1718sumstrs", contextPath+"/migratepr1718sumstrs");
		OPEN_URIS.put(contextPath+"/migratepr1819sumstrs", contextPath+"/migratepr1819sumstrs");
		OPEN_URIS.put(contextPath+"/migratepr1920sumstrs", contextPath+"/migratepr1920sumstrs");
		OPEN_URIS.put(contextPath+"/migratepr2021sumstrs", contextPath+"/migratepr2021sumstrs");
		OPEN_URIS.put(contextPath+"/migratepr2122sumstrs", contextPath+"/migratepr2122sumstrs");
		
		
		
		OPEN_URIS.put(contextPath+"/migrateUsers", contextPath+"/migrateUsers");
		OPEN_URIS.put(contextPath+"/mirageReceiptsPayments", contextPath+"/mirageReceiptsPayments");
		OPEN_URIS.put(contextPath+"/migratecustomfields", contextPath+"/migratecustomfields");
		OPEN_URIS.put(contextPath+"/mirageEinvoiceSubscription", contextPath+"/mirageEinvoiceSubscription");
		OPEN_URIS.put(contextPath+"/mirageEinvoiceSandboxSubscription", contextPath+"/mirageEinvoiceSandboxSubscription");
		OPEN_URIS.put(contextPath+"/updateSubscriptionDetails", contextPath+"/updateSubscriptionDetails");
		OPEN_URIS.put(contextPath+"/migratejournals", contextPath+"/migratejournals");
		OPEN_URIS.put(contextPath+"/migratejournalsGSTR1", contextPath+"/migratejournalsGSTR1");
		OPEN_URIS.put(contextPath+"/migratejournalsVC", contextPath+"/migratejournalsVC");
		OPEN_URIS.put(contextPath+"/migratejournalsPaymentReceipt", contextPath+"/migratejournalsPaymentReceipt");
		OPEN_URIS.put(contextPath+"/migratejournalsPayemnts", contextPath+"/migratejournalsPayemnts");
		OPEN_URIS.put(contextPath+"/mirgatePartnerPayments", contextPath+"/mirgatePartnerPayments");
		
		OPEN_URIS.put(contextPath+"/migrate2b", contextPath+"/migrate2b");
		OPEN_URIS.put(contextPath+"/migrate2bCridetDebitNotes", contextPath+"/migrate2bCridetDebitNotes");
		
		OPEN_URIS.put(contextPath+"/migrateadvadj", contextPath+"/migrateadvadj");
		OPEN_URIS.put(contextPath+"/migratenewledgers", contextPath+"/migratenewledgers");
		OPEN_URIS.put(contextPath+"/mirgatetcstdspayable", contextPath+"/mirgatetcstdspayable");
		OPEN_URIS.put(contextPath+"/updaterecordpayments", contextPath+"/updaterecordpayments");
		
		
		
	}
	
	public boolean isValid(String path) {
		return path.startsWith(STATIC_PATH) || path.startsWith(ACTUATOR_PATH) || OPEN_URIS.containsKey(path);
	}

}