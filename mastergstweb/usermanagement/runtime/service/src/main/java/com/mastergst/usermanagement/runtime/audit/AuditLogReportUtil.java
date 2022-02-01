package com.mastergst.usermanagement.runtime.audit;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.stereotype.Component;

import com.beust.jcommander.internal.Lists;
import com.mastergst.usermanagement.runtime.response.audit.AuditVO;

@Component
public class AuditLogReportUtil {
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
	public List<AuditVO> invoiceListItems(List<Auditlog> auditlogs){
		List<AuditVO> auditVOList = Lists.newArrayList();
		for (Auditlog audit : auditlogs) {
			AuditVO auditVO = new AuditVO();
			auditVO.setClientname(audit.getClientname() != null ? audit.getClientname() : "");
			auditVO.setUsername(audit.getUsername() != null ? audit.getUsername() : "");
			auditVO.setUseremail(audit.getUseremail() != null ? audit.getUseremail() : "");
			auditVO.setAction(audit.getAction() != null ? audit.getAction() : "");
			auditVO.setGstn(audit.getGstn() != null ? audit.getGstn() : "");
			auditVO.setInvoicenumber(audit.getInvoicenumber() != null ? audit.getInvoicenumber() : "");
			auditVO.setDescription(audit.getDescription() != null ? audit.getDescription() : "");
			auditVO.setReturntype(audit.getReturntype() != null ? audit.getReturntype() : "");
			auditVO.setCreateddate(audit.getCreatedDate() != null ? df.format(audit.getCreatedDate()).toString() : "");
			
			/*-- Previous Values Start --*/
			auditVO.setOinvoiceNumber(audit.getAuditingFields() != null ? audit.getAuditingFields().getOinvoiceNumber() != null ? audit.getAuditingFields().getOinvoiceNumber() : "" : "");
			auditVO.setOgstin(audit.getAuditingFields() != null ? audit.getAuditingFields().getOgstin() != null ? audit.getAuditingFields().getOgstin() : "" : "");
			auditVO.setOinvoiceDate(audit.getAuditingFields() != null ? audit.getAuditingFields().getOinvoiceDate() != null ? audit.getAuditingFields().getOinvoiceDate() : "" : "");
			boolean ototalamount = audit.getAuditingFields() != null ? audit.getAuditingFields().getOtotalamount() != null ? true : false : false;
			if(ototalamount) {
				auditVO.setOtotalamount(audit.getAuditingFields().getOtotalamount());
			}
			boolean otaxableamount = audit.getAuditingFields() != null ? audit.getAuditingFields().getOtotaltaxableamount() != null ? true : false : false;
			if(otaxableamount) {
				auditVO.setOtotaltaxableamount(audit.getAuditingFields().getOtotaltaxableamount());
			}
			boolean oigstamount = audit.getAuditingFields() != null ? audit.getAuditingFields().getOtotalIGSTAmount() != null ? true : false : false;
			if(oigstamount) {
				auditVO.setOtotalIGSTAmount(audit.getAuditingFields().getOtotalIGSTAmount());
			}
			boolean ocgstamount = audit.getAuditingFields() != null ? audit.getAuditingFields().getOtotalCGSTAmount() != null ? true : false : false;
			if(ocgstamount) {
				auditVO.setOtotalCGSTAmount(audit.getAuditingFields().getOtotalCGSTAmount());
			}
			boolean osgstamount = audit.getAuditingFields() != null ? audit.getAuditingFields().getOtotalSGSTAmount() != null ? true : false : false;
			if(osgstamount) {
				auditVO.setOtotalSGSTAmount(audit.getAuditingFields().getOtotalSGSTAmount());
			}
			boolean ocessamount = audit.getAuditingFields() != null ? audit.getAuditingFields().getOtotalCESSAmount() != null ? true : false : false;
			if(ocessamount) {
				auditVO.setOtotalCESSAmount(audit.getAuditingFields().getOtotalCESSAmount());
			}
			/*-- Previous Values End --*/
			
			/*-- Updated Values Start --*/
			auditVO.setNinvoiceNumber(audit.getAuditingFields() != null ? audit.getAuditingFields().getNinvoiceNumber() != null ? audit.getAuditingFields().getNinvoiceNumber() : "" : "");
			auditVO.setNgstin(audit.getAuditingFields() != null ? audit.getAuditingFields().getNgstin() != null ? audit.getAuditingFields().getNgstin() : "" : "");
			auditVO.setNinvoiceDate(audit.getAuditingFields() != null ? audit.getAuditingFields().getNinvoiceDate() != null ? audit.getAuditingFields().getNinvoiceDate() : "" : "");
			boolean ntotalamount = audit.getAuditingFields() != null ? audit.getAuditingFields().getNtotalamount() != null ? true : false : false;
			if(ntotalamount) {
				auditVO.setNtotalamount(audit.getAuditingFields().getNtotalamount());
			}
			boolean ntaxableamount = audit.getAuditingFields() != null ? audit.getAuditingFields().getNtotaltaxableamount() != null ? true : false : false;
			if(ntaxableamount) {
				auditVO.setNtotaltaxableamount(audit.getAuditingFields().getNtotaltaxableamount());
			}
			boolean nigstamount = audit.getAuditingFields() != null ? audit.getAuditingFields().getNtotalIGSTAmount() != null ? true : false : false;
			if(nigstamount) {
				auditVO.setNtotalIGSTAmount(audit.getAuditingFields().getNtotalIGSTAmount());
			}
			boolean ncgstamount = audit.getAuditingFields() != null ? audit.getAuditingFields().getNtotalCGSTAmount() != null ? true : false : false;
			if(ncgstamount) {
				auditVO.setNtotalCGSTAmount(audit.getAuditingFields().getNtotalCGSTAmount());
			}
			boolean nsgstamount = audit.getAuditingFields() != null ? audit.getAuditingFields().getNtotalSGSTAmount() != null ? true : false : false;
			if(nsgstamount) {
				auditVO.setNtotalSGSTAmount(audit.getAuditingFields().getNtotalSGSTAmount());
			}
			boolean ncessamount = audit.getAuditingFields() != null ? audit.getAuditingFields().getNtotalCESSAmount() != null ? true : false : false;
			if(ncessamount) {
				auditVO.setNtotalCESSAmount(audit.getAuditingFields().getNtotalCESSAmount());
			}
			/*-- Updated Values End --*/
			auditVOList.add(auditVO);
		}
		return auditVOList;
		
	}
}
