package com.mastergst.usermanagement.runtime.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mastergst.usermanagement.runtime.domain.ClientConfig;
import com.mastergst.usermanagement.runtime.domain.GSTR2BSupport;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.response.MisMatchVO;

public interface ReconsilationService {

	public Page<? extends InvoiceParent> getPurchaseInvoices(String clientid, String matchingId, String matchingStatus, int month, int year);
	public Page<? extends InvoiceParent> getGstr2BSupportInvoices(String clientid, int start, int length, int month, int year, String matchingId, String matchingStatus, boolean isMonthly) throws Exception;

	public Page<? extends InvoiceParent> getPurchaseInvoices(String clientid, int start, int length, int month, int year, String matchingId, String matchingStatus,String yearcode,Boolean billdate, boolean isMonthly) throws Exception;

	public Map<String, String> getGstr2bData(String id, String clientid, int month, int year);

	public List<InvoiceParent> searchGstr2bInvoicess(List<String> ids, String clientid, String fp);
	public void updateMannualRecords(List<MisMatchVO> records, String returntype, String invoiceid);
	public Page<? extends InvoiceParent> findByMatchingIdAndMatchingStatus(String invoiceid, String mannualMatched, Pageable pageable);
	public Page<? extends InvoiceParent> findByPrMatchingIdAndMatchingStatus(String invoiceid, String mannualMatched, Pageable pageable);
	public ClientConfig getClientConfig(String clientId);
	public void updateGstr2bMismatchStatus(String clientId, List<PurchaseRegister> purchaseRegisters, List<GSTR2BSupport> gstr2List, String invType, String gstn, String fp, String monthlyOrYearly);
	public void updateGstr2bMismatchStatusTrDate(String clientId, List<PurchaseRegister> purchaseRegisters, List<GSTR2BSupport> gstr2List, String invType, String gstn, String fp, String monthlyOrYearly);
	Map<String, Object> getInvoicesSupport(String clientid, int month, int year, boolean isTransactionDate, boolean isMonthly);
	Map<String, Object> getGstr2bReconcileInvoices(String id, String clientid, int month, int year, int start, int length, String searchVal, InvoiceFilter filter, boolean isTransactionDate, boolean isMonthly);
	Map<String, Object> getGstr2bReconcileSummary(String clientid, int month, int year, boolean isTransactionDate, boolean isMonthly);
	public void updateGstr2bMismatchStatus(String clientid, String invType, String gstnnumber, String retPeriod, int month, int year, boolean isMonthly, boolean isBilledDate);
	
	
}
