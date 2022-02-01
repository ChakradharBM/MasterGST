package com.mastergst.usermanagement.runtime.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.mastergst.usermanagement.runtime.domain.Anx2;
import com.mastergst.usermanagement.runtime.domain.AnxInvoiceParent;
import com.mastergst.usermanagement.runtime.domain.AnxInvoiceSupport;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.response.ANX2Response;
import com.mastergst.usermanagement.runtime.response.Response;

public interface Anx2Service {
	//void saveAnx2Invoice(InvoiceParent invoiceForAnx1, String clientid, String returntype,boolean isIntraState);
	
	//AnxInvoiceSupport getANX2ReturnInvoice(List<? extends AnxInvoiceSupport> invoices, Client client, String returntype,int iMonth, int iYear);
	ANX2Response fetchANX2UploadStatus(String id, String usertype, String clientid, String returntype, int month, int year,List<String> invoices);
	//void saveInvoices(Iterable<? extends AnxInvoiceSupport> invoices, String returnType);

	//Map<String, Object> getInvoices(Pageable pageable, Client client, String id, String retType, String string,int month, int year, int start, int length, String searchVal, InvoiceFilter filter, boolean b,String booksOrReturns);

	//Anx2 extrafields(Anx2 individualInvoice, String string);

	
}
