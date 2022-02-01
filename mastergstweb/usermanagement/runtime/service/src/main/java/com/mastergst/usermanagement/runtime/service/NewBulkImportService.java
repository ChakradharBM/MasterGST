package com.mastergst.usermanagement.runtime.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.mastergst.configuration.service.StateConfig;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;

public interface NewBulkImportService {
	public InvoiceParent savePurchaseRegister(InvoiceParent invoice, boolean isIntraState) throws IllegalAccessException, InvocationTargetException;

	public InvoiceParent saveSalesInvoice(InvoiceParent invoice, boolean isIntraState) throws IllegalAccessException, InvocationTargetException;
	
	InvoiceParent importInvoicesHsnQtyRateMandatory(InvoiceParent invoice, String returnType, String clientid,String branch,String vertical,int month,int year,String[] patterns,Map<String, String>  statesMap);
	
	InvoiceParent importInvoicesTally(InvoiceParent invoice, Client client, String returnType, String branch,String vertical,int month,int year,String[] patterns, Map<String, String> statesMap);
	
	InvoiceParent importInvoicesSimplified(InvoiceParent invoice, Client client, String returnType, String branch,String vertical,int month,int year,String[] patterns,Map<String, String> statesMap);
	
	public Date invdate(String invdate,String[] patterns,int year,int month);
	public int month(String mnth);
	public String errorMsg(String key, InvoiceParent invoice, String returntype,boolean hsnqtyrate,String template);
	public String tallyErrorMsg(String key, InvoiceParent invoice, String returntype);

	public String einvErrorMsg(String key, InvoiceParent invoice, String returntype, boolean hsnqtyrate, String template);

	InvoiceParent importEInvoicesHsnQtyRateMandatory(InvoiceParent invoice, String returntype, String branch, String vertical, int month, int year, String[] patterns,Map<String, String> statesMap);
	
	public String invStatename(String statename);

	InvoiceParent importEwaybillInvoices(InvoiceParent invoice, String returntype, String branch,String vertical, int month, int year, String[] patterns,Client client,Map<String, String> statesMap);
	
	void updateExcelData(final Map<String, List<InvoiceParent>> beans, final List<String> sheetList, final String returntype, final String id, 
			final String fullname, final String clientId, final String templateType,OtherConfigurations otherconfig) throws IllegalArgumentException, IOException;
	public Map<String, StateConfig> getStatesMap();
	public Map<String, String> getStateMap();

}
