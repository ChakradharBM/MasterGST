package com.mastergst.usermanagement.runtime.audit;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;

public interface AuditlogService {
	public void saveAuditLog(String userid,String clientid,String invoiceNumber,String action,String returntype,InvoiceParent oldinvoice,InvoiceParent newInvoice);
	
	public String auditlogDescription(String action,String invoiceNumber,String returntype);
	
	Map<String, Object> getAuditlogs(Pageable pageable,final String userid, List<String> listofclients, int month, int year, int start, int length, AuditlogFilter filter, String searchVal,String fieldName, String order);
	Map<String, Object> getAuditLogCustom(Pageable pageable,final String userid, List<String> clientids, String fromtime, String totime, int start, int length,AuditlogFilter filter, String searchVal,String fieldName, String order);
	
	AuditlogFilter auditlogFilter(HttpServletRequest request);
	
	Page<? extends Auditlog> getAuditlogsExcel(final String userid, List<String> listofclients, int month, int year, int start, int length, AuditlogFilter filter, String searchVal,Pageable pageable);
	Page<? extends Auditlog> getAuditlogsCustomExcel(final String userid, List<String> clientids, String fromtime, String totime, int start, int length,AuditlogFilter filter, String searchVal,Pageable pageable);
}
