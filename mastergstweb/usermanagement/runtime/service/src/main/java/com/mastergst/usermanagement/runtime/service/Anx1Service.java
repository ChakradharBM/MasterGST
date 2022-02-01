package com.mastergst.usermanagement.runtime.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.mastergst.usermanagement.runtime.domain.AnxInvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.response.Response;

public interface Anx1Service {
	void saveAnx1Invoice(InvoiceParent invoiceForAnx1, String clientid, String returntype,boolean isIntraState);
	
	Map<String, Object> getInvoices(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year, int start, int length, String searchVal, InvoiceFilter filter, boolean isTotalRequired, String booksOrReturns);
	
	AnxInvoiceParent getGSTRReturnInvoice(List<? extends AnxInvoiceParent> invoices, final Client client, final String returntype, final int iMonth, final int iYear);
	Response fetchUploadStatus(final String userid, final String usertype, String clientid, String returntype, int month, int year, List<String> invoiceList);
	public List<? extends AnxInvoiceParent> getSelectedInvoices(final List<String> invoiceList, final String returnType);
	List<? extends AnxInvoiceParent> getInvoices(Pageable pageable, final Client client, final String returnType, int month, int year, final String status);
	public void saveInvoices(Iterable<? extends AnxInvoiceParent> invoices, final String returnType);

	public void performDownload(Client client, String invType, String string, String clientid, String id, int month, int year);
	
	AnxInvoiceParent getInvoice(final String invoiceId, final String returnType);
}
