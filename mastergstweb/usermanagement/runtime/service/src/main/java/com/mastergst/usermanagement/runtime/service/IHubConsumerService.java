/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import java.text.ParseException;
import java.util.Date;

import com.mastergst.core.domain.Base;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.login.runtime.domain.HeaderKeys;
import com.mastergst.usermanagement.runtime.domain.Anx1InvoiceSupport;
import com.mastergst.usermanagement.runtime.domain.AnxInvoiceParent;
import com.mastergst.usermanagement.runtime.domain.AnxInvoiceSupport;
import com.mastergst.usermanagement.runtime.domain.CancelEwayBill;
import com.mastergst.usermanagement.runtime.domain.CancelIRN;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.EBillVehicleListDetails;
import com.mastergst.usermanagement.runtime.domain.EWAYBILL;
import com.mastergst.usermanagement.runtime.domain.EinvoiceConfigurations;
import com.mastergst.usermanagement.runtime.domain.GSTR3B;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.ewaybill.ExtendValidity;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2B;
import com.mastergst.usermanagement.runtime.response.ANX2Response;
import com.mastergst.usermanagement.runtime.response.CancelIRNResponse;
import com.mastergst.usermanagement.runtime.response.DigioResponse;
import com.mastergst.usermanagement.runtime.response.EwayBillCancelResponse;
import com.mastergst.usermanagement.runtime.response.GSTR9GetResponse;
import com.mastergst.usermanagement.runtime.response.EwayBillDateResponse;
import com.mastergst.usermanagement.runtime.response.EwayBillResponse;
import com.mastergst.usermanagement.runtime.response.EwayBillResponseData;
import com.mastergst.usermanagement.runtime.response.EwayBillVehicleUpdateResponse;
import com.mastergst.usermanagement.runtime.response.GSTR1AutoLiabilityResponse;
import com.mastergst.usermanagement.runtime.response.GSTR2BResponse;
import com.mastergst.usermanagement.runtime.response.GSTR8GetResponse;
import com.mastergst.usermanagement.runtime.response.GSTRCommonResponse;
import com.mastergst.usermanagement.runtime.response.GenerateEwayBillResponse;
import com.mastergst.usermanagement.runtime.response.GenerateIRNResponse;
import com.mastergst.usermanagement.runtime.response.LedgerResponse;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.TransGSTINResponse;
import com.mastergst.usermanagement.runtime.response.ewaybill.EwayBillRejectResponse;
import com.mastergst.usermanagement.runtime.response.ewaybill.ExtendValidityResponse;
import com.mastergst.usermanagement.runtime.response.gstr4.GSTR4AnnualCMPResponse;

/**
 * Service interface for consuming IHub services.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface IHubConsumerService {

	Response otpRequest(final String state, final String gstName, final String ipAddress);
	Response authRequest(final String otp, final Response otpResponse);
	void invokeRefreshToken(HeaderKeys headerKey) throws Exception;
	Response saveReturns(final Base invoice, final String state, final String gstName, final String gstn, 
			final String retPeriod, final String returnType, final boolean initial) throws Exception;
	Response newReturnStatus(final String refId, final String state, final String gstName, final String gstn, 
			final String retPeriod,final String rettype, final boolean initial);
	/*ANX2 start */
	ANX2Response saveANX2Returns(final Base invoice, final String state, final String gstName, final String gstn, 
			final String retPeriod, final String returnType, final boolean initial) throws Exception;
	ANX2Response newANX2ReturnStatus(final String refId, final String state, final String gstName, final String gstn, 
			final String retPeriod,final String rettype, final boolean initial);
	/*ANX2 End */
	Response returnStatus(final String refId, final String state, final String gstName, final String gstn, 
			final String retPeriod, final boolean initial);
	InvoiceParent getGSTRXInvoices(final Client client, final String gstin, final int month, final int year, 
			final String gstr, final String type, final String ctin, final String userId, final boolean initial) throws MasterGSTException;
	Response returnSummary(final Client client, final String gstin, final String retPeriod, final String userId, 
			final String returnType, final boolean initial)  throws Exception;
	Response anx1ReturnSummary(final Client client, final String gstin, final String retPeriod, final String userId, 
			final String returnType, final boolean initial)  throws Exception;
	Response returnHSNSummary(final Client client, final String gstin, final String returnType, final String retPeriod, final String userId, final boolean initial);
	Response returnDocIssue(final Client client, final String gstin, final String retPeriod, final String userId, final boolean initial);
	DigioResponse signDocument(final String uploadData, final String clientId, final boolean isDSC);
	DigioResponse getSignedDocument(String signid);
	Response offsetLiability(final Client client, final String gstin, final String returnType, final String retPeriod, 
			final String userId, final Object offsetLiability) throws Exception;
	Response returnSubmit(final Client client, final String gstin, final String returnType, 
			final String retPeriod, final String userId, final boolean initial,final boolean latestsummary) throws Exception;
	Response returnOTPForEVC(final Client client, final String gstin, final String returnType,  
			final String userId, final boolean initial);
	Response proceedToFile(final Client client, final String gstin, final String retPeriod);
	Response returnFile(final Client client, final String retPeriod, final String userId, 
			String signData, final String returnType);
	Response returnFileByEVC(final Client client, final String retPeriod, final String userId, 
			String content, final String evcotp, final String returnType);
	Response trackStatus(final Client client, final String gstin, final String retPeriod, final String userId, 
			final String returnType, final boolean initial);
	Response publicSearch(final String gstin);
	EwayBillResponse getGSTINDetailsByEwayBillno(final String gstin,final String ewbno, final String ipAddress);
	EwayBillDateResponse getGSTINDetailsByDate(final String gstin,final String date, final String ipAddress);
	Response authenticateEwayBillApi(final String gstin, final String ipAddress,final String uname, final String pwd);
	EwayBillCancelResponse cancelEwayBill(final String gstin, final String ipAddress,CancelEwayBill cancelewaybill);
	EwayBillVehicleUpdateResponse updateVehicle(final String gstin, final String ewbno,final String vehicleType, final String ipAddress,EBillVehicleListDetails vehicleDetails) throws ParseException;
	GenerateEwayBillResponse generateEwayBill(final String gstin, final String ipAddress, EwayBillResponseData respData);
	
	LedgerResponse getLedgerCashDetails(final Client client, final String gstin, final String fromDate, final String toDate, 
			final String userId, final boolean initial);
	LedgerResponse getLedgerITCDetails(final Client client, final String gstin, final String fromDate, final String toDate, 
			final String userId, final boolean initial);
	LedgerResponse getCashITCBalanceDetails(final Client client, final String gstin, final String retPeriod, 
			final String userId, final boolean initial);
	LedgerResponse getTaxLedgerDetails(final Client client, final String gstin, final String retPeriod, 
			final String userId, final boolean initial);
	LedgerResponse getReturnLiabilityBalanceDetails(final Client client, final String gstin, final String retPeriod, 
			final String retType, final String userId, final boolean initial);
	Response publicRettrack(String gstin, String fy);
	GSTRCommonResponse getAutoCalculatedDetails(final Client client, final String returnType, final String gstin, final String retPeriod);
	
	GSTR9GetResponse getGSTR9Details(final Client client, final String returnType, final String gstin, final String retPeriod);
	Response authenticateEinvoiceAPI(final String gstin, final String ipAddress,final String uname, final String pwd);
	GenerateIRNResponse generateIRN(final String gstin, final String ipAddress, InvoiceParent respData);
	CancelIRNResponse cancelIRN(final String gstin, final String ipAddress,String clientid, CancelIRN cancelirn);
	GenerateIRNResponse getIRNByDocDetails(final String gstin, final String ipAddress,EinvoiceConfigurations configDetails,HeaderKeys headerKeys,String invoiceNumber,String invoiceDate,String invtype);
	/* E-INVOICE END*/
	
	/* ANX2 DOWNLOAD START*/
	AnxInvoiceSupport getANX2Invoices(final Client client, final String gstin, final int month, final int year, 
			final String gstr, final String type, final String ctin, final String userId, final boolean initial) throws MasterGSTException;
	
	
	/* ANX2 DOWNLOAD END */
	Anx1InvoiceSupport getANX1Invoices(final Client client, final String gstin, final int month, final int year, 
			final String gstr, final String type, final String ctin, final String userId, final boolean initial) throws MasterGSTException;
	
	EwayBillDateResponse getEbillDetailsByOtherParties(final String gstin,final String date, final String ipAddress);
	GSTR2B getGstr2bInvoices(Client client, String gstnnumber, String userid, String returnPeriod, String returnType, boolean initial,int fileNum) throws MasterGSTException;
	GSTR2BResponse getGstr2bInvoice(Client client, String gstnnumber, String userid, String returnPeriod, String returnType, boolean initial,int fileNum) throws MasterGSTException;
	public GSTR8GetResponse getGSTR8Details(Client client, String returnType, String gstin, String retPeriod);
	GSTR4AnnualCMPResponse getAnnualCmp(final Client client,final String gstin,final String retPeriod);
	Response getGSTR3BLiablitiesAutoCalcDetails(Client client, String gstin,String retPeriod);
	GSTR1AutoLiabilityResponse getGSTR1AutoLiabilityDetails(Client client, String gstnnumber, String retPeriod);
	TransGSTINResponse getTransinDetails(String transin, String gstin, String ipAddress);
	ExtendValidityResponse extendEwaybillValidity(String gstnnumber, String hostAddress, ExtendValidity evalidity);
	EwayBillRejectResponse rejectEwayBill(String gstnnumber, String hostAddress, CancelEwayBill rebill);
}
