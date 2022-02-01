package com.mastergst.usermanagement.runtime.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.Amounts;
import com.mastergst.usermanagement.runtime.domain.GroupDetails;
import com.mastergst.usermanagement.runtime.domain.Node;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;

public interface AccountingReportsService_bkp {

	
	public List<GroupDetails> getClientGroupdetails(String clientid);
	public List<ProfileLedger> getLedgersByClientid(String clientid);
		
	public Map<String, Node> getMonthlyTrailBalance(String clientid, int month, int year);
	public Map<String, Node> getYearlyTrailBalance(String clientid, int year);
	public Map<String, Node> getCustomTrailBalance(String clientid, String fromtime, String totime);
		
	public Map<String, Node> getMonthlyPandLReport(String clientid, int month, int year);
	public Map<String, Node> getYearlyPandLReport(String clientid,int year);
	
	public Map<String, Node> getMonthlyBalanceSheet(String clientid, int month, int year);
	public Map<String, Node> getYearlyBalanceSheet(String clientid,int year);
	
	public List<AccountingJournal> getLedgersData(String ledgername, String clientid, List<String> invoiceMonth);
	public Map<String, Amounts> calculateLedgersAmounts(List<AccountingJournal> journals, List<AccountingJournal> openingjournals);
	public List<AccountingJournal> getCustomLedgersData(String ledgername, String clientid, Date currentStDate, Date currentEndDate);
	
	public Map<String, Amounts> newCalculateLedgersAmounts(List<AccountingJournal> journals, List<AccountingJournal> openingjournals);
	
	
	public List<AccountingJournal> getLedgersDataNew(String ledgername, String clientid, List<String> invoiceMonth);
}
