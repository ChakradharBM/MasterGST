package com.mastergst.usermanagement.runtime.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.mastergst.login.runtime.domain.User;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Reminders;

public interface PrintService {
	Map<String, Object> getReportParams(Client client, InvoiceParent invoiceParent, String returntype) throws Exception;
	void sendPDFAttachment(String mail, List<String> ccmail, Reminders reminder, String id,String clientid,String invId, Boolean signcheck, String returntype,HttpServletRequest request);
	void sendEinvoicePDFAttachment(String mail, List<String> cc, Reminders reminder, String id,String clientid,String invId, Boolean signcheck, String returntype,HttpServletRequest request);
}
