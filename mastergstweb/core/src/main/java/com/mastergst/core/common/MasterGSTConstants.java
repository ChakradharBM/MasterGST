/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.core.common;

/**
 * This contains all MaterGST Constants.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
public final class MasterGSTConstants {

	private MasterGSTConstants() {
	}
	public static final String BEGIN = "Begin";
	public static final String END = "End";
	public static final String SUCCESS = "Success";
	public static final String SUCCESS_CODE = "1";
	public static final String EMAIL_EXIST = "Email already exists";
	public static final String DEMO_USER = "Demo";
	public static final String SITE_ENROLL_USER = "SiteEnroll";
	public static final String USER_NOT_EXIST = "User not exist";
	
	public static final String CUSTOMER = "customer";
	public static final String PARTNER = "partner";
	public static final String ASPDEVELOPER = "aspdeveloper";
	public static final String CONSUMER = "consumer";
	public static final String CAS = "cacmas";
	public static final String TAXPRACTITIONERS = "taxp";
	public static final String SUVIDHA_CENTERS = "suvidha";
	public static final String BUSINESS = "business";
	public static final String ENTERPRISE = "enterprise";
	
	public static final String USER_DIRECT = "Parent";
	public static final String USER_CLIENT = "Child";
	
	public static final String EMAIL_ERROR = "EmailError";
	public static final String PASSWORD_ENCODE_FORMAT = "utf-8";
	public static final String PASSWORD_ENCODE_EXCEPTION = "Password encode Unsupporting exception";
	
	public static final String SIGNUPDEMO_EMAILSUBJECT = "Welcome to MasterGST - Partner Enrollment";
	public static final String SIGNUPINVITE_EMAILSUBJECT = "MasterGST - Signup Invitation";
	public static final String SITEENROLL_EMAILSUBJECT = "Welcome to MasterGST - Enrollment";
	public static final String RESETPWD_EMAILSUBJECT = "Welcome to MasterGST - Reset Password";
	public static final String CREATEUSER_EMAILSUBJECT = "Welcome to MasterGST - Thank you for Signup";
	public static final String OTP_EMAILSUBJECT = "Welcome to MasterGST - OTP";
	
	public static final String STATUS_FILED = "Filed";
	public static final String STATUS_SUBMITTED = "Submitted";
	public static final String STATUS_INELIGIBLE = "Ineligible";
	
	public static final String SUCESS_FAIL = "Sucess-Business Error";
	public static final String SUCESS = "Sucess";
	
	public static final String B2B = "B2B";
	public static final String B2C = "B2C";
	public static final String B2CL = "B2CL";
	public static final String EXPORTS = "Exports";
	public static final String ADVANCES = "Advances";
	public static final String ATPAID = "Advance Adjusted Detail";
	public static final String NIL = "Nil Supplies";
	public static final String CDNUR = "Credit/Debit Note for Unregistered Taxpayers";
	public static final String CREDIT_DEBIT_NOTES = "Credit/Debit Notes";
	public static final String CREDIT_NOTES = "Credit Note";
	public static final String DEBIT_NOTES = "Debit Note";
	public static final String B2BUR = "B2B Unregistered";
	public static final String ITC_REVERSAL = "ITC Reversal";
	public static final String ISD = "ISD";
	public static final String ISDCN = "ISDCN";
	public static final String ISDDN = "ISDDN";
	public static final String IMP_GOODS = "Import Goods";
	public static final String IMP_SERVICES = "Import Services";
	public static final String DE = "DE";
	public static final String SEZWP = "SEZWP";
	public static final String SEZWOP = "SEZWOP";
	public static final String ISDC = "ISDC";
	
	public static final String DEA = "DEA";
	public static final String ECOM = "ECOM";
	public static final String EXPWOP = "EXPWOP";
	public static final String EXPWP = "EXPWP";
	public static final String IMPGSEZ = "IMPGSEZ";
	public static final String IMPG = "IMPG";
	public static final String IMPS = "IMPS";
	public static final String MIS = "MIS";
	public static final String RJDOC = "RJDOC";
	public static final String REV = "REV";
	public static final String SEZWOPA = "SEZWOPA";
	public static final String SEZWPA = "SEZWPA";
	
	public static final String B2BA = "B2BA";
	public static final String B2CSA = "B2CSA";
	public static final String B2CLA = "B2CLA";
	public static final String CDNA = "CDNA";
	public static final String CDNURA = "CDNURA";
	public static final String EXPA = "EXPA";
	public static final String ATA = "ATA";
	public static final String TXPA = "TXPA";
	public static final String ISDA = "ISDA";
	
	public static final String YES = "Yes";
	public static final String NO = "No";
	public static final String COMMA = ",";
	public static final String PENDING = "Pending";
	public static final String PURCHASE_REGISTER = "Purchase Register";
	public static final String PRODUCTS = "PRODUCTS";
	public static final String SERVICES = "SERVICES";
	public static final String ITEMS = "ITEMS";
	public static final String CUSTOMERS = "CUSTOMERS";
	public static final String SUPPLIERS = "SUPPLIERS";
	public static final String PRODUCT_CODE = "G";
	public static final String SERVICE_CODE = "S";
	public static final String HSN = "HSN";
	public static final String SAC = "SAC";
	public static final String GOODS = "Goods";
	public static final String DEALER_NON_COMPOUND = "NonCompound";
	public static final String DEALER_CASUAL = "Casual";
	public static final String DEALER_COMPOUND = "Compound";
	public static final String DEALER_INPUT_SERVICE_DISTRIBUTOR = "InputServiceDistributor";
	
	public static final String TYPE_TCS = "TCS";
	public static final String TYPE_REVERSE = "Reverse";
	
	public static final String GST_FLAG_ACCEPT = "A";
	public static final String GST_FLAG_REJECT = "R";
	public static final String GST_FLAG_MODIFY = "M";
	public static final String GST_FLAG_DELETE = "D";
	
	public static final String GST_STATUS_SUCCESS = "SUCCESS";
	public static final String GST_STATUS_ERROR = "ERROR";
	public static final String GST_STATUS_CANCEL = "CANCELLED";
	public static final String GST_STATUS_MATCHED = "Matched";
	public static final String GST_STATUS_MATCHED_PREVIOUS_MONTH = "Matched In Other Months";
	public static final String GST_STATUS_MISMATCHED = "Mismatched";
	public static final String GST_STATUS_PARTIALMATCHED = "Partially Matched";
	public static final String GST_STATUS_NOTINPURCHASES = "Not In Purchases";
	public static final String GST_STATUS_NOTINGSTR2A = "Not In GSTR 2A";
	public static final String GST_STATUS_ROUNDOFFMATCHED = "Round Off Matched";
	public static final String GST_STATUS_ROUNDOFFMATCHED_PREVIOUS_MONTH = "Round Off Matched In Other Months";
	public static final String GST_STATUS_PROBABLEMATCHED = "Probable Matched";
	public static final String GST_STATUS_MANNUAL_MATCHED = "Manual Matched";
	
	public static final String GST_STATUS_TAX_MISMATCHED = "Tax Mismatched";
	public static final String GST_STATUS_INVOICENO_MISMATCHED = "Invoice No Mismatched";
	public static final String GST_STATUS_INVOICE_VALUE_MISMATCHED = "Invoice Value Mismatched";
	public static final String GST_STATUS_GST_NO_MISMATCHED = "GST No Mismatched";
	public static final String GST_STATUS_INVOICE_DATE_MISMATCHED = "Invoice Date Mismatched";
	
	public static final String GST_RETURN_CODE_P = "P";
	public static final String GST_RETURN_CODE_PE = "PE";
	public static final String GST_RETURN_CODE_IP = "IP";
	public static final String GST_RETURN_CODE_ER = "ER";
	public static final String GST_RETURN_CODE_REC = "REC";
	
	public static final String REVERSE_CHARGE_YES = "Y";
	public static final String REVERSE_CHARGE_NO = "N";
	
	public static final String SUPPLY_TYPE_INTER = "INTER";
	public static final String SUPPLY_TYPE_INTRA = "INTRA";
	
	public static final String TALLY_TEMPLATE = "TALLY";
	public static final String EXCEL_TEMPLATE = "EXCEL";
	public static final String MASTERGST_EXCEL_TEMPLATE = "MASTERGSTEXCEL";
	
	public static final String FILING_OPTION_MONTHLY = "Monthly";
	public static final String FILING_OPTION_QUARTERLY = "Quarterly";
	public static final String FILING_OPTION_YEARLY = "Yearly";
	
	public static final String TRACK_STATUS_SERVICE_NAME = "View and TrackReturns";
	
	public static final String GSTR1 = "GSTR1";
	public static final String GSTR2 = "GSTR2";
	public static final String GSTR3 = "GSTR3";
	public static final String GSTR4 = "GSTR4";
	public static final String GSTR5 = "GSTR5";
	public static final String GSTR6 = "GSTR6";
	public static final String GSTR7 = "GSTR7";
	public static final String GSTR8 = "GSTR8";
	public static final String GSTR9 = "GSTR9";
	public static final String GSTR10 = "GSTR10";
	public static final String GSTR11 = "GSTR11";
	public static final String GSTR1A = "GSTR1A";
	public static final String GSTR2A = "GSTR2A";
	public static final String GSTR2B = "GSTR2B";
	public static final String GSTR3A = "GSTR3A";
	public static final String GSTR3B = "GSTR3B";
	public static final String GSTR4A = "GSTR4A";
	public static final String GSTR5A = "GSTR5A";
	public static final String GSTR6A = "GSTR6A";
	public static final String GSTR7A = "GSTR7A";
	public static final String GSTR8A = "GSTR8A";
	public static final String GSTR9A = "GSTR9A";
	public static final String GSTR9C = "GSTR9C";
	public static final String GSTR10A = "GSTR10A";
	public static final String GSTR11A = "GSTR11A";
	public static final String ANX1 = "ANX1";
	public static final String ANX1A = "ANX1A";
	public static final String ANX2 = "ANX2";
	public static final String ANX2A = "ANX2A";
	public static final String EINVOICE = "EINVOICE";
	
	public static final String SANDBOX = "Sandbox";
	public static final String PRODUCTION = "Production";
	public static final String EWAYBILLSANDBOX = "EwayBillSandBox";
	public static final String EWAYBILLPRODUCTION = "EwayBillProduction";
	public static final String EINVOICE_SANDBOX = "EInvoiceSandBox";
	public static final String EINVOICE_PRODUCTION = "EInvoiceProduction";
	public static final String SALES_EMAIL = "Sales@mastergst.com";
	
	public static final String GSTSANDBOX_API = "GSTSANDBOXAPI";
	public static final String EWAYBILLSANDBOX_API = "EWAYBILLSANDBOXAPI";
	public static final String GST_API = "GSTAPI";
	public static final String EWAYBILL_API = "EWAYAPI";
	
	public static final String DELIVERYCHALLANS = "DELIVERYCHALLANS";
	public static final String PROFORMAINVOICES = "PROFORMAINVOICES";
	public static final String ESTIMATES = "ESTIMATES";
	public static final String PURCHASEORDER = "PurchaseOrder";
	public static final String EXPENSES = "EXPENSES";
	public static final String EWAYBILL = "EWAYBILL";
	public static final String RESULTS = "RESULTS";
	
	
	public static final String SEQUENCE_GENERATOR="MasterGST_SequenceGenerator";
	public static final String GENERATED = "Generated";
	
	public static final String INVOICEVIEW_OPTION_MONTHLY = "Monthly";
	public static final String INVOICEVIEW_OPTION_YEARLY = "Yearly";
	
	public static final String JOINED = "Joined";
	public static final String INVITED = "Invited";
	public static final String SUBSCRIBED = "Subscribed";
	public static final String DONE = "Done";
	public static final String PARTIALLY = "Partially Paid";
	
	public static final String MONTHLY = "Monthly";
	public static final String YEARLY = "Yearly";
	public static final String NOT_REQUIRED ="Not Required";
	public static final String GST_STATUS_NOTINGSTR2B = "Not In GSTR2B";
	public static final String OTHER_TERRITORY = "Other Territory";
}