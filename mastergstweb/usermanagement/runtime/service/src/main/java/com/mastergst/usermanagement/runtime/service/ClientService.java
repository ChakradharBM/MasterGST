/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mastergst.configuration.service.CountryConfig;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.configuration.service.UQCConfig;
import com.mastergst.login.runtime.domain.HeaderKeys;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.usermanagement.runtime.domain.AnxInvoiceSupport;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientConfig;
import com.mastergst.usermanagement.runtime.domain.ClientStatus;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CompanyInvoices;
import com.mastergst.usermanagement.runtime.domain.CompanySuppliers;
import com.mastergst.usermanagement.runtime.domain.EBillVehicleListDetails;
import com.mastergst.usermanagement.runtime.domain.EWAYBILL;
import com.mastergst.usermanagement.runtime.domain.EwayBillConfigurations;
import com.mastergst.usermanagement.runtime.domain.FilingStatusReportsVO;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR1DocumentIssue;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.GSTR3B;
import com.mastergst.usermanagement.runtime.domain.GSTR4Annual;
import com.mastergst.usermanagement.runtime.domain.GSTR9;
import com.mastergst.usermanagement.runtime.domain.GSTR9C;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.GSTROffsetLiability;
import com.mastergst.usermanagement.runtime.domain.GSTReturnSummary;
import com.mastergst.usermanagement.runtime.domain.HSNData;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.InvoiceParentSupport;
import com.mastergst.usermanagement.runtime.domain.InvoiceTypeSummery;
import com.mastergst.usermanagement.runtime.domain.Messages;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.Payments;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.Reminders;
import com.mastergst.usermanagement.runtime.domain.SupplierComments;
import com.mastergst.usermanagement.runtime.domain.SupplierStatus;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6;
import com.mastergst.usermanagement.runtime.response.EwayBillResponseData;
import com.mastergst.usermanagement.runtime.response.MisMatchVO;
import com.mastergst.usermanagement.runtime.response.Response;

/**
 * Service interface for Client to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface ClientService {
	
	HeaderKeys getHeaderkeysGstusername(final String gstusername);
	HeaderKeys getHeaderkeysGstusername(final String gstusername,final String email);
	Client saveClient(Client client);

	Client createClient(Client client, String userid);
	
	Client createClient(Client client, String userid, int month, int year);
	
	Client addBranchInfo(final String clientid, final String name, final String address, final String parentId);
	Client addCostCenterInfo(final String clientid, final String name, final String parentId);
	
	Client updateBranchInfo(final String clientid, final String id, final String name, final String address, final String parentId);
	Client updateCostCenterInfo(final String clientid, final String id, final String name, final String parentId);
	
	Client addVerticalInfo(final String clientid, final String name, final String address, final String parentId);

	Client updateVerticalInfo(final String clientid, final String id, final String name, final String address, final String parentId);
	
	boolean collectionExists(final String collectionName);
	
	List<Client> findByUserid(final String userid);
	
	List<Client> findByUserCreatedClients(final String userid);
	
	List<Client> findByGSTNames(final List<String> gstnames);
	
	List<String> fetchClientIds(final String userid);
	
	List<Client> findClients(final List<String> clientIds);
	
	List<String> getClientList(final String userid, List<String> clientIds);
	
	boolean checkAddClientEligibility(final String userid);
	
	Client findById(final String id);
	
	ClientStatus getClientStatus(final String clientId, final String returnType, final String returnPeriod);
	List<ClientStatus> getClientReturnStatus(final String clientId, final String returnType, final String returnPeriod);
	
	Map<String, String> getClientStatus(final List<String> clientList, final String returnPeriod);
	
	Map<String, Integer> getReportStatus(final List<String> clientList, final String returnType, final String returnPeriod);
	
	Map<String, List<Client>> getReportStatuss(final List<String> clientList, final String returnType, final String returnPeriod);
	
	ClientStatus saveClientStatus(final ClientStatus clientStatus);
	
	Map<String, Map<String, String>> getFilingStatusReport(final String clientId, final int year);
	
	Map<String, Map<String, List<String>>> getFilingStatusReports(final String clientId, final int year);
	
	//Map<String, Map<String, String>> getClientFilingStatusReport(final String clientId, final int year);
	Map<String, Map<String, List<String>>> getClientFilingStatusReport(final String clientId, final int year,final String exceldwnldOrreport);
	
	GSTR3B saveSuppliesInvoice(GSTR3B gstr3B, final String returnType);
	
	GSTR9 saveAnnualInvoice(GSTR9 gstr9);
	GSTR3B getSuppliesInvoice(final String clientId, final String returnPeriod);
	List<GSTR3B> getSuppliesInvoice(List<String> clientId, final String returnPeriod);
	GSTR9 getAnnualInvoice(final String clientId, final String fp);
	
	GSTR3B getSuppliesInvoice(final String id);
	
	/*InvoiceParent saveSalesInvoice(InvoiceParent invoice, final boolean isIntraState) 
			throws IllegalAccessException, InvocationTargetException;
	*/
	InvoiceParent saveSalesInvoice(InvoiceParent invoice, InvoiceParent oldinvoice, final boolean isIntraState) 
			throws IllegalAccessException, InvocationTargetException;
	InvoiceParent saveAnx1Invoice(InvoiceParent invoice, final boolean isIntraState) 
			throws IllegalAccessException, InvocationTargetException;
	
	InvoiceParent saveGSTRInvoice(InvoiceParent invoice, final String retType, final boolean isIntraState)
			throws IllegalAccessException, InvocationTargetException;
	
	InvoiceParent saveGSTRInvoice(InvoiceParent invoice, final String retType);
	
	GSTROffsetLiability saveGSTROffsetLiability(GSTROffsetLiability offLiability);
	
	GSTROffsetLiability getGSTROffsetLiability(final String gstn, final String retPeriod, final String retType);
	
	GSTROffsetLiability getGSTROffsetLiability(final String id);
	
	InvoiceParent savePurchaseRegister(InvoiceParent invoice, final boolean isIntraState) 
			throws IllegalAccessException, InvocationTargetException;
	
	InvoiceParent getInvoice(final String invoiceId, final String returnType);
	
	String deleteInvoice(final String invoiceId, final String returnType,final String booksOrReturns);
	
	void cancelInvoice(final String invoiceId, final String returnType,final String booksOrReturns);
	
	void deleteClient(final String userId, final String clientId);
	
	void deleteAllClient(final String userId);
	
	void deleteInvoices(final String clientid, final String returnType, final int month, final int year);
	
	Page<? extends InvoiceParent> getInvoices(Pageable pageable, final String clientid, final String returnType);
	
	Page<? extends InvoiceParent> getInvoices(Pageable pageable, final String clientid, final String returnType, int month, int year);
	
	List<? extends InvoiceParent> getSelectedInvoices(final List<String> invoiceList, final String returnType);
	
	Page<? extends InvoiceParent> getInvoices(Pageable pageable, final Client client, final String returnType, int month, int year);
	
	List<? extends InvoiceParent> getInvoicesByInvtype(final Client client, final String userid, final String returnType, final List<String> invtype, int month, int year);
	
	Page<? extends InvoiceParent> getInvoices(Pageable pageable, final Client client, final String returnType, int month,
			int year, boolean checkQuarterly);
	
	Map<String, Map<String, String>> getConsolidatedSummeryForYearMonthwise(final Client client, String yearCd, boolean checkQuarterly);
	
	/*Page<? extends InvoiceParent> getInvoices(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year, int start, int length, String searchVal);*/
	
	Map<String, Object> getInvoices(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year, int start, int length, String searchVal, InvoiceFilter filter, boolean isTotalRequired, String booksOrReturns);
	Map<String, Object> getDeleteAllInvoices(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year, int start, int length, String searchVal, InvoiceFilter filter, boolean isTotalRequired, String booksOrReturns);
	
	Page<? extends InvoiceParent> getInvoices(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year);
	
	Page<? extends InvoiceParent> getHSNInvoices(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year);
	
	Page<? extends InvoiceParent> getInvoicesByBranchName(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year, List<String> branch);	
	Map<String, Object> getInvoicesSupport(final Client client, final String returnType,final String reports, int month,
			int year);
	
	Page<? extends InvoiceParent> getInvoices(Pageable pageable, final Client client, final String userid, final String returnType, int month,
			int year,String filingOption);
	
	Page<? extends InvoiceParent> getfyInvoices(Pageable pageable, final Client client, final String userid, final String returnType, int month,
			int year,String filingOption);
	
	Page<? extends InvoiceParent> getgstr2Matchedfyinvs(Pageable pageable, final Client client, final String userid, final String returnType, int month,
			int year,String filingOption);
	
	Page<? extends InvoiceParent> getInvoices(Pageable pageable, final Client client, final String userid, final String returnType, int year);
	
	Page<? extends InvoiceParent> getInvoices(Pageable pageable, final Client client, final String userid, final String returnType, String fromtime, String totime);
	
	List<? extends InvoiceParent> getInvoices(Pageable pageable, final Client client, final String returnType, int month, int year, final String status);
	
	List<? extends InvoiceParent> getInvoices(final String clientid, final String returnType, final List<String> invoiceIds);
	
	void saveInvoices(Iterable<? extends InvoiceParent> invoices, final String returnType);
	
	void saveGSTErrors(List<? extends InvoiceParent> invoices, final InvoiceParentSupport gstResponse, final String returnType);
	
	void updateExcelData(final Map<String, List<InvoiceParent>> beans, final List<String> sheetList, final String returntype, final String id, 
			final String fullname, final String clientId, final String templateType) throws IllegalArgumentException, IOException;
	
	List<GSTReturnSummary> getGSTReturnsSummary(List<String> returntypes, Date date);
	
	List<GSTReturnSummary> getGSTReturnsSummary();
	
	List<GSTReturnSummary> getGSTReturnsSummary(Date date);
	
	Response fetchUploadStatus(final String userid, final String usertype, String clientid, String returntype, int month, int year, List<String> invoiceList,String hsnSum);
	
	Response delSelectedInvoices(String clientid, String returntype, int month, int year, List<String> invoiceList);
	
	InvoiceParent getGSTRReturnInvoice(List<? extends InvoiceParent> invoices, final Client client, final String returntype, final int iMonth, final int iYear,final String hsnSum);
	
	GSTR3B populateGSTR3BDetails(Client client, int month, int year);
	
	GSTR3B populateGSTR3BDetailss(Client client, int month, int year);
	
	void updateMismatchRecords(List<MisMatchVO> records, boolean acceptFlag);
	
	void syncInvoiceData(final Client client, final String returnType, String userid, final String usertype, final int month, final int year);
	
	List<PurchaseRegister> getUnclaimedInvoices(final String clientId, final int month, final int year);
	
	List<PurchaseRegister> getPurchaseRegisters(final String invType, final String gstn, final int month, final int year, boolean isYear);
	
	void updateGSTR2AReturnData(InvoiceParent gstr2a, final String invType, final String gstn, final String clientid, final String fp, final int month, final int year);
	
	GSTR1DocumentIssue getDocumentIssue(final String documentId);
	
	GSTR1DocumentIssue getDocumentIssue(final String clientid, final String returnPeriod);
	
	GSTR1DocumentIssue saveDocumentIssue(final GSTR1DocumentIssue docIssue);
	
	void processGSTRData(InvoiceParent invoice, final Client client, String status, String returnType, String invType, String userid, 
			final String usertype, final int month, final int year);
	
	//void updateMismatchStatus(String clientId, List<PurchaseRegister> purchaseRegisters, List<GSTR2> gstr2List, final String invType, 
			//final String gstn, final String fp,final String monthlyOrYearly);
			
	void updateMismatchStatus(String clientId, final String invType, final String gstn, final String fp, int month, int year, final String monthlyOrYearly,final boolean billdate);
	
	Page<? extends InvoiceParent> getAmendments(Pageable pageable, final String clientid, final String returnType, int month,
			int year);
	
	List<? extends InvoiceParent> getOldInvoices(final String clientid, final String returnType, int month,
			int year);
	List<? extends InvoiceParent> getOldInvoicesByBranch(final String clientid, final String returnType, int month,
			int year,List<String> branch);

	Client findByEmailandfirm(String email, String firm);
	List<CountryConfig> getCountriesList();

	Map<String,Object> getDefaultInvoiceNo(String clientId, String id, String returntype, String invoiceType, String financialYear,int month);
	Map<String, Object> getDefaultAccountingInvoiceNo(String clientId, String userId, String returntype,String invoiceType, String financialYear, int month);
	
	Page<? extends InvoiceParent> getMonthlyInvoices(Pageable pageable, final Client client, final String userid, final String returnType, int month,
			int year);
	
	Page<? extends InvoiceParent> getMonthlyInvoicesmatchingStatusisNull(Pageable pageable, final Client client, final String userid, final String returnType, int month,
			int year);
	public Payments saveRecordPayments(Payments payments);
	public InvoiceParent recordPaymentDetais(final String id,final String returntype);
	public InvoiceParent recordPaymentDetails(final String invoiceNumber, final String clientid,final String fp,final String returntype);
	public List<Payments> recordPaymentsHistory(final String clientid,final String invoiceNumber);
	//boolean invoiceNumberCheck(final String invoiceNumber,final String returnType, final String clientid,final Date d1, final Date d2);
	boolean invoiceNumberCheckInEdit(final String invoiceNumber,final String returnType, final String clientid,final Date d1, final Date d2,boolean edit);
	List<Payments> allrecordpayments(final String clientid,final String invoiceNumber);
	List<Payments> findByClientid(final String clientid);
	
	public boolean purchaseInvoiceNoCheck1(String clientid,String invoiceid, String companyDBId, Date d1, Date d2,boolean edit);
	
	public boolean purchaseInvoiceNoCheck(String clientid,String invoiceid,String returntype, String gstin, Date d1, Date d2,boolean edit);
	
	void updateClientConfig(ClientConfig clientConfig);
	
	ClientConfig getClientConfig(String clientId);
	
	void updateCategory(String returnType, int month, int year);
	
	List<? extends InvoiceParent> getSearchedInvoiceData(final String returntype,final Date d1, final Date d2,final String clientid, final String searchQuery);
	
	List<? extends InvoiceParent> getSearchedInvoiceData(final String returntype,final String invoicetype,final Date d1, final Date d2,final String clientid, final String billedtoname, String searchQuery);
	
	List<? extends InvoiceParent> getSearchedAdvReceiptInvoiceData(final String returntype,final String invoicetype,final Date d1, final Date d2,final String clientid, final String searchQuery);
	
	List<Client> getClientDataByGroupName(List<String> groupnames);
	
	Page<? extends InvoiceParent> getAdminGroupReportInvoicesMonthly(Pageable pageable, String returntype, List<String> clientids, int month,int year);
	
	Page<? extends InvoiceParent> getAdminGroupReportInvoicesYearly(Pageable pageable, String returntype, List<String> clientids, int year);
	Page<? extends InvoiceParent> getAdminGroupReportInvoicesCustom(Pageable pageable, String returntype, List<String> clientids, String fromtime, String totime);
	public Messages saveMessages(Messages messages);
	
	Page<? extends InvoiceParent> getMatchedAndPresentMonthfyinvs(final String clientid,final Date d1, final Date d2);
	
	Page<? extends InvoiceParent> getPresentMonthfyinvsMatchingIdisNull(final String clientid,final Date d1, final Date d2);
	
	Page<? extends InvoiceParent> getPresentMonthfyinvsMatchingIdisNotNull(final String clientid,final Date d1, final Date d2);
	
	Page<? extends InvoiceParent> getPresentretperiodgstr2MatchingStatusisNotNull(final String clientid,final List<String> fp);
	
	Page<? extends InvoiceParent> getPresentretperiodgstr2MatchingStatusisNull(final String clientid,final List<String> fp);
	
	Page<? extends InvoiceParent> getPresentretperiodgstr2MatchingStatus(final String clientid,final List<String> fp);
	
	Page<? extends InvoiceParent> getPresentretperiodgstr2MannualMatchingStatus(final String clientid,final List<String> fp);
	
	Page<? extends InvoiceParent> getPresentMonthfyinvsMannualMatchingIdisNotNull(final String clientid,final Date d1, final Date d2);
	
	Page<? extends InvoiceParent> getgstr2MatchingIdsInvoices(final String clientid,List<String> matchingids);
	
	Page<? extends InvoiceParent> getgstr2MatchingIdsMannualInvoices(final String clientid,List<String> matchingids);
	
	void updateGSTR2AReturnData_2A(String invType, String gstn, String fp, int month, int year);
	
	public List<Payments> findByClientid(String clientid,String returntype);
	public List<Payments> findByInvoiceid(String invoiceid,String returntype);
	
	
	Page<? extends InvoiceParent> getGSTR2A_VS_GSTR2YearlyInvoices(Pageable pageable, String clientid,String returntype, int year);
	Page<? extends InvoiceParent> getGSTR2A_VS_GSTR2CustomInvoices(Pageable pageable, String clientid,String returntype, String totime,String fromtime);
	
	Page<Payments> getAdminGroupReportPaymentsMonthly(Pageable pageable, String returntype, List<String> clientids, int month,int year);
	Page<Payments> getAdminGroupReportPaymentsYearly(Pageable pageable, String returntype, List<String> clientids,int year);
	Page<Payments> getAdminGroupReportPaymentsCustom(Pageable pageable, String returntype, List<String> clientids,String fromtime, String totime);

	public Page<? extends InvoiceParent>  getGSTR2_GSTR3B_VS_GSTR2AInvoices(Pageable pageable, final String clientid, final String returnType, int month, int year);


	public Page<? extends InvoiceParent> getInvoiceBasedOnClientidAndFP(Pageable pageable,String clientid,String returntype, String fp);

	
	public Page<? extends InvoiceParent> getMonthlyInvoiceBasedOnClientidAndDateofitcClaimed(Pageable pageable,String clientid, String returntype, int month,int  year);
	//public Page<? extends InvoiceParent> getYearlyInvoiceBasedOnClientidAndDateofitcClaimed(Pageable pageable,String clientid, String returntype,int  year);
	//public Page<? extends InvoiceParent> getCustomInvoiceBasedOnClientidAndDateofitcClaimed(Pageable pageable,String clientid, String returntype,String fromtime, String totime);


	//public List<PurchaseRegister> getITCUnclaimedInvoices_MonthlyReport(final String clientId, final int month, final int year);
	//public List<PurchaseRegister> getITCUnclaimedInvoices_YearlyReport(final String clientId, final int year);
	//public List<PurchaseRegister> getITCUnclaimedInvoices_CustomReport(final String clientId, String fromtime,String totime);

	Page<? extends InvoiceParent> getYearlyInvoices(Pageable pageable, final String clientid, final String returntype,int year);

	Map<String, Map<String, Map<String, String>>> getAllSupplierStatusBasedOnClientid(String clientid, int year);

	//SupplierStatus getSupplierStatusByMonthwise(String supplierid, String returnperiod, String returntype);
	
	List<SupplierStatus> getSupplierStatusByMonthwise(String supplierid, String returnperiod, String returntype);

	Map<String, Map<String, List<String>>> getReportFilingStatusReports(String clientId, int year);
	
	OtherConfigurations saveOtherConfigurations(OtherConfigurations otherform);
	OtherConfigurations getOtherConfig(String clientId);
	EwayBillConfigurations saveEwayBillConfigurations(EwayBillConfigurations ebillform);
	EwayBillConfigurations getEwayBillConfig(String clientId);
	void deletePayment(final String paymentId,final String clientId,final String voucherNumber,final String invoiceNumber, final String returntype);

	public Page<? extends InvoiceParent> getMultiMonthReport(Pageable pageable, final Client client, final String userid, final String returnType,	int year);
	public Page<? extends InvoiceParent> getGSTR1AdminGroupReportInvoicesMonthly(Pageable pageable, String returntype, List<String> clientids, int month,int year);
	Page<? extends InvoiceParent> getGSTR1AdminGroupReportInvoicesYearly(Pageable pageable, String returntype, List<String> clientids, int year);
	Page<? extends InvoiceParent> getGSTR1AdminGroupReportInvoicesCustom(Pageable pageable, String returntype, List<String> clientids, String fromtime, String totime);
		public Page<? extends InvoiceParent> getComaprisionReportQuaterlyInvoices(Pageable pageable, Client client, String userid,String returnType, String reports, int month, int year);
	List<GSTR3B> getComaprisionReqportQuarterlySuppliesInvoice(String clientId, List<String> returnPeriod);
	
	public List<EWAYBILL> populateEwayBillData(List<EwayBillResponseData> data, User user, Client client, final String returntype) throws ParseException;
	public List<EWAYBILL> populateEwayBillData(List<EwayBillResponseData> data, User user, Client client, final String returntype,final String inwardoroutward,Map<String,StateConfig> statetin) throws ParseException;
	EwayBillResponseData genEwayBillResponseData(InvoiceParent data,Client client);
	//List<EWAYBILL> getEwayBillInvoices(String clientId);
	
	void updateMannualRecords(List<MisMatchVO> records,String returntype,String invoiceid);
	
	void saveJournalInvoice(InvoiceParent invoiceForJournal, String clientid, String returntype,boolean isIntraState);
	public InvoiceParent ewayBillvehicleDetails(final String id,final String returntype);
	public List<EBillVehicleListDetails> ewayBillVehicleHistory(final String clientid,final String ewayBillNumber);

	public Page<? extends InvoiceParent> getReport_PresentMonthfyinvsMannualMatchingIdisNotNull(String clientid, Date d1, Date d2);

	public Page<? extends InvoiceParent> getReport_Presentretperiodgstr2MannualMatchingStatus(String clientid, List<String> fp);
	
	void updateInprogressInvoices(final Client client, final String returnType, String userid, final String usertype, final int month, final int year);
	public Reminders saveReminders(Reminders reminders);
	SupplierComments saveSupplierComments(SupplierComments Supcomments);
	
	GSTR9C getAnnual9CInvoice(final String clientId, final String fp);
	GSTR9C saveAnnual9CInvoice(GSTR9C gstr9);

	List<InvoiceTypeSummery> getInvoiceSummeryByTypeForMonth(final String clientId, final String returnType, final int month, final	int year);
	public Map<String, Integer>  getMismatchCount(List<String> clientIds, int month, int year);
	public Map<String, Integer>  getTotalInvoicesByGststatus(final String clientId, final String returnType, final int month, final	int year);
	
	public List<GSTReturnSummary> getGSTReturnsSummary(final Client client, int month, int year, boolean updateValues);
	List<? extends InvoiceParent> getInvoicesByType(Client client, String userid, String returntype, String b2b,String invoiceConfigType, int month, int yr, String invnumbercutoff);
	String invNumberformat(CompanyInvoices invoiceConfig, int i, String financialYear);
	public String invoiceNumberformat(CompanyInvoices companyInvoices,String financialYear,int month);
	public InvoiceParent extrafields(InvoiceParent invoice, String returntype);
	public String fetchAcknowledgementClientId(String userid);

	public  Map<String, Map<String, String>> getConsolidatedSummeryForYearReports(Client client, String returntype, String yearCd,boolean checkQuarterly, String reportType, InvoiceFilter invoiceFilter);

	public Map<String, Object> getCustomInvoices(Pageable pageable, Client client, String id, String retType, String type, String fromtime, String totime, int start, int length, String searchVal, InvoiceFilter filter, boolean flag, String booksOrReturns);

	public Map<String, Map<String, String>> getConsolidatedSummeryForCustomReports(Client client, String returntype, String fromtime, String totime,InvoiceFilter filter );

	Map<String, Object> getCustomInvoicesSupport(Client client, String returnType, String reports, String fromtime,String totime);

	Map<String, Object> getMultiMonthReport(Pageable pageable, Client client, String id, String returntype, int month, int year, int start, int length, String searchVal, InvoiceFilter filter);

	Map<String, Object> getMultimonthInvoicesSupport(Client client, String returntype, int month, int year);

	Page<? extends InvoiceParent> getItcInvoices(String clientid, String returntype, String itcinvtype, Date stDate, Date endDate, int start, int length, String searchVal);

	TotalInvoiceAmount getItcTotalInvoicesAmounts(String clientid, String returntype, String itcinvtype, Date stDate, Date endDate, String searchVal);
	void processAnx2Data(AnxInvoiceSupport anx2, Client client, String anx22, String invType, String userid, int month,int year);
	
	GSTR3B populateGSTR3BOffsetLiabilityDetails(Client client, int month, int year, String id);
	Map<String, Map<String, Map<String, List<String>>>> getAllSupplierStatusBasedOnClientid(String clientid, int year, List<String> rtArray);
	
	public void getDownloadGSTRXStatus(Client client, String userId, String gstr, int month, int year);
	public Page<? extends InvoiceParent> getDaoInvoices(final Client client, final String returntype, int month, int year, String reporttype, InvoiceFilter filter);

	public Page<? extends InvoiceParent> getDaoInvoices(Client client, String returntype, String fromtime, String totime, InvoiceFilter invoiceFilter);

	InvoiceFilter invoiceFilter(HttpServletRequest request);

	Page<? extends InvoiceParent> getGlobalReportDaoInvoices(List<String> clientids, String returntype, int month, int year, String booksorReturns,InvoiceFilter filter);

	Page<? extends InvoiceParent> getGlobalReportDaoInvoices(List<String> clientids, String returntype, String fromtime, String totime, String booksorReturns,InvoiceFilter filter);
	
	//public InvoiceParent populateInvoiceInfo(InvoiceParent invoice, final String returntype, final boolean isIntraState);
		public InvoiceParent populateInvoiceInfo(InvoiceParent invoice, InvoiceParent oldinvoice, final String returntype, final boolean isIntraState);
	
	Map<String, Object> getInvoices(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter, boolean isTotalRequired, String booksOrReturns);
	
	public Map<String, Object> getCustomInvoices(Pageable pageable, Client client, String id, String retType, String type, String fromtime, String totime, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter, boolean flag, String booksOrReturns);
	void changeSubscriptionData(String usrid,String usertype);
	
	List<PurchaseRegister> getPurchaseRegistersByTransactionDate(final String invType, final String gstn, final int month, final int year, boolean isYear);
	
	void updateMismatchStatusByTransactionDate(String clientId, List<PurchaseRegister> purchaseRegisters, List<GSTR2> gstr2List, final String invType, 
			final String gstn, final String fp,final String monthlyOrYearly);

	void populateHSNList(List<HSNData> hsnDataList, InvoiceParent invoiceParent, Map<String, String> hsnMap,Map<String, String> sacMap, List<UQCConfig> uqcList, String returntype);
	void populateNewHSNList(List<HSNData> hsnDataList, InvoiceParent invoiceParent, Map<String, String> hsnMap,Map<String, String> sacMap, List<UQCConfig> uqcList, final String returnType); 
	GSTR6 getGSTR6AnnualInvoice(String clientid, String retPeriod);
	public void consolidatedItemRate(GSTRInvoiceDetails invoiceDetails);
	boolean checkHsnOrSac(String hsnVal);

	GSTR3B populateGSTR3BLiabilityAutoCalcDetails(Client client, int month, int year);

	List<String> getCustomFields(String clientid, String returnType);
	
	public Map<String, Object> getAllSupplierStatusBasedOnClientid(String id,String clientid, int year, int start, int length, String searchVal, InvoiceFilter filter);

	public Map<String, Object> getSuppliersBillToGstns(String clientId);
	
	Map<String, Map<String, Map<String, List<String>>>> getAllSupplierStatusBasedOnClientid(List<CompanySuppliers> companySuppliers, int year, List<String> rtArray);
	
	List<FilingStatusReportsVO> getAllSupplierStatusBasedOnClientids(List<CompanySuppliers> companySuppliers, int year, List<String> rtArray);
	
	Page<PurchaseRegister> getUpdatePurchaseRegisters(String invType, String clientid, int month, int year,
			boolean isYear, Pageable pageable);
	public void removegstr2id(List<String> gstr2id, String clientid);
	public void removematchingid(List<PurchaseRegister> purchaseRegisters);
	
	public Page<PurchaseRegister> getPageablePurchaseRegisters(final String invType, final String clientid, final int month,
			final int year, boolean isYear, Pageable pageable, boolean billdate);
	
	Page<PurchaseRegister> getPurchaseRegistersByInvoiceNos(final String clientid, final String invtype, List<String> invoiceNos, Pageable pageable);

	Page<PurchaseRegister> getPurchaseRegistersByGstr2bMatchingStatusInvoiceNos(String clientid, String invtype, List<String> invoiceNos, Pageable pageable);

	public String deleteInvoice(InvoiceParent invoice, String returnType, String booksOrReturns);
	
	Page<? extends InvoiceParent> getGlobalReportDaoInvoices(List<String> clientids, String returntype, int month, int year, String booksorReturns,InvoiceFilter filter,Pageable pageable);
	
	Page<? extends InvoiceParent> getGlobalReportDaoInvoices(List<String> clientids, String returntype, String fromtime, String totime, String booksorReturns,InvoiceFilter filter,Pageable pageable);
}
