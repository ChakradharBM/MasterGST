/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.EinvoiceConfigurations;
import com.mastergst.usermanagement.runtime.domain.ImportResponse;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.TemplateMapper;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;
import com.mastergst.usermanagement.runtime.response.GenerateIRNResponseData;

/**
 * Service interface for Client to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface EinvoiceService {
	EinvoiceConfigurations saveEinvoiceConfigurations(EinvoiceConfigurations einvform);
	EinvoiceConfigurations getEinvoiceConfig(String clientId);
	
	Map<String, Object> getEInvoices(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year, int start, int length, String searchVal, InvoiceFilter filter, boolean isTotalRequired);
	public Map<String, Object> getCustomInvoices(Pageable pageable, Client client, String id, String retType, String type, String fromtime, String totime, int start, int length, String searchVal, InvoiceFilter filter, boolean flag, String booksOrReturns);
	InvoiceParent populateEinvoiceDetails(InvoiceParent invoice,Client client);
	InvoiceParent arrangeJsonDataForIRN(InvoiceParent respData);
	InvoiceParent populateEinvType(InvoiceParent invoice);
	InvoiceParent saveSalesInvoice(InvoiceParent invoice, final boolean isIntraState) 
			throws IllegalAccessException, InvocationTargetException;
	public InvoiceParent populateInvoiceInfo(InvoiceParent invoice, final String returntype, final boolean isIntraState);
	ImportResponse importEinvMapperSimplified(String id, String clientid, String templateName, MultipartFile file,
			String returntype, int month, int year, List<String> list, String fullname, TemplateMapper templateMapper,
			TemplateMapperDoc templateMapperDoc, OtherConfigurations otherconfig, String branch, String vertical, HttpServletRequest request);
	ImportResponse importEinvMapperDetailed(String id, String clientid, String templateName, MultipartFile file,
			String returntype, int month, int year, List<String> list, String fullname, TemplateMapper templateMapper,
			TemplateMapperDoc templateMapperDoc,OtherConfigurations otherconfig, String branch, String vertical, HttpServletRequest request);
	Map<String, List<InvoiceParent>> getEinvExcelSheetMap();
	Map<String, Object> getEInvoices(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter, boolean isTotalRequired);
	Map<String, Object> getEInvoicesForDelete(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter, boolean isTotalRequired);
	void updateAlreadyGeneratedIRNByOtherSystem(final Client client, final String returnType, String userid, final String usertype, final int month, final int year)throws Exception;
}
