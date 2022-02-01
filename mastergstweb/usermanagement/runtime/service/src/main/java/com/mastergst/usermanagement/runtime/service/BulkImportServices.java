package com.mastergst.usermanagement.runtime.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.mastergst.usermanagement.runtime.domain.InvoiceParent;

public interface BulkImportServices {
	
	public void updateExcelData(final Map<String, List<InvoiceParent>> beans, final List<String> sheetList, final String returntype, final String id, 
			final String fullname, final String clientId, final String templateType) throws IllegalArgumentException, IOException;

	public InvoiceParent savePurchaseRegister(InvoiceParent invoice, boolean isIntraState) throws IllegalAccessException, InvocationTargetException;

	public InvoiceParent saveSalesInvoice(InvoiceParent invoice, boolean isIntraState) throws IllegalAccessException, InvocationTargetException;

}
