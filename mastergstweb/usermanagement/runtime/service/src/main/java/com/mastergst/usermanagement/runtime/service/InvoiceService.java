package com.mastergst.usermanagement.runtime.service;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;

import com.mastergst.configuration.service.StateConfig;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.FilingStatusReportsVO;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.ReturnsDownloadStatus;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2B;
import com.mastergst.usermanagement.runtime.response.GSTR2BResponse;
import com.mastergst.usermanagement.runtime.response.InvoiceVO;
import com.mastergst.usermanagement.runtime.response.gstr2b.GSTR2BVO;

public interface InvoiceService {

	GSTR2B syncGstr2bInvoices(Client client, String userid, String returntype, String returnPeriod, boolean initial);
	
	GSTR2BResponse syncGstr2bInvoice(Client client, String userid, String returntype, String returnPeriod, boolean initial);

	List<GSTR2BVO> getGSTR2BData(String clientid, String returnPeriod,GSTR2B gstr2bdata);

	ByteArrayInputStream itcToExcel(List<GSTR2BVO> gstr2bList,List<GSTR2BVO> gstr2bunAvlList,List<InvoiceParent> b2bInvoicesList,List<InvoiceParent> b2baInvoicesList,List<InvoiceParent> cdnInvoicesList,List<InvoiceParent> cdnaInvoicesList,List<InvoiceParent> isdInvoicesList,List<InvoiceParent> isdaInvoicesList,List<InvoiceParent> impgInvoicesList,List<InvoiceParent> impgsezInvoicesList,String type) throws IOException;

	List<GSTR2BVO> getGSTR2BITCunavlData(String clientid, String returnPeriod, GSTR2B gstr2bdata);

	Map<String, Object> getITCReportParams(Client client, List<GSTR2BVO> gstr2bavlList, List<GSTR2BVO> gstr2bunavlList);
	
	ByteArrayInputStream gstr2aToExcel(List<InvoiceVO> InvoicesList,List<InvoiceVO> ammendedInvoicesList,List<String> headers,String dwnldxlsyearlytype) throws IOException;
	
	List<InvoiceVO> invoiceListItems(Page<? extends InvoiceParent> invoices, String returntype,List<StateConfig> states,Client client);
	
	List<InvoiceVO> getInvoice_Wise_List(Page<? extends InvoiceParent> invoices, String returntype,List<StateConfig> states,Client client);
	Page<? extends InvoiceParent> getDaoInvoices(Client client, String returntype, int month, int year, String reporttype,List<String> invTypes,InvoiceFilter filter);
	
	Map<String, Map<String, String>> getConsolidatedSummeryForYearReports(Client client, String returntype, String yearCd,boolean checkQuarterly, String reportType, InvoiceFilter invoiceFilter);
	
	ByteArrayInputStream supplierFilingStatusToExcel(List<FilingStatusReportsVO> InvoicesList,List<String> headers) throws IOException;
	
	ByteArrayInputStream supplierFilingStatusToMultipleExcel(List<List<FilingStatusReportsVO>> InvoicesList,List<String> headers,HttpServletResponse response) throws IOException;

	public ReturnsDownloadStatus downloadGstr2bInvoice(Client client, String userid, String returntype, String returnPeriod, boolean initial);

}
