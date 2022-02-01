package com.mastergst.usermanagement.runtime.service;

import java.util.List;
import java.util.Map;

import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientConfig;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;

public interface InvoicesMappingService {

	Map<String, Object> getInvoicesSupport(String clientid, int month, int year, boolean isMonthly, boolean isTransactionDate);
	Map<String, Object> getInvoices(String clientid, int month, int year, boolean isMonthly, int start, int length, String searchVal, InvoiceFilter filter,boolean isTransactiondate);
	Map<String, Object> getReconcileSummary(String clientid, int month, int year, boolean isMonthly,boolean isTransactiondate);
	public void updateReconsileStatus(String clientId, List<PurchaseRegister> purchaseRegisters, List<GSTR2> gstr2List,List<String> rtarray,List<String> matchingstatus, String invType, String gstn, String fp, String monthlyOrYearly, boolean istransactionDate);
	public ClientConfig getClientConfig(String clientId);
	//public void updateReconsileStatus(Client client, String fp, int month, int year, String monthlyOrYearly);
	public void updateReconsileStatusMatchingStatusIsNull(String clientid, List<PurchaseRegister> purchaseRegisters, List<GSTR2> gstr2, String invType, String gstnnumber, String fp, String monthlyOrYearly);
	
	public void removegstr2id(List<String> gstr2id, String clientid);
	
	public void removematchingid(List<PurchaseRegister> purchaseRegisters);
	
	List<PurchaseRegister> getPurchaseRegisters(final String invType, final String gstn, final int month, final int year, boolean isYear);
	void updateMismatchStatus(String clientid, String invType, String gstn, String fp, int month, int year, String monthlyOrYearly, boolean isBilledDate);
}
