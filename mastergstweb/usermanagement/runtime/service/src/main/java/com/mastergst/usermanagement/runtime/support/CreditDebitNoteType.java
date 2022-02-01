package com.mastergst.usermanagement.runtime.support;

import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;

public class CreditDebitNoteType {
	
	public static String getNtty(InvoiceParent invoice, String returntype) {
		String ntty = "D";
		if(returntype.equalsIgnoreCase(GSTR1) || returntype.equals("SalesRegister")) {
			if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
				if(isNotEmpty(((GSTR1) invoice).getCdnr()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
					ntty = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
				}
			}
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase("Purchase Register")) {
			if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
				if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0).getNt()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
					ntty = invoice.getCdn().get(0).getNt().get(0).getNtty();
				}
			}
		}
		if(invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
			if(isNotEmpty(invoice.getCdnur()) && isNotEmpty(invoice.getCdnur().get(0).getNtty()) && isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
				ntty = invoice.getCdnur().get(0).getNtty();
			}
		}
		
		return ntty;
	}

}
