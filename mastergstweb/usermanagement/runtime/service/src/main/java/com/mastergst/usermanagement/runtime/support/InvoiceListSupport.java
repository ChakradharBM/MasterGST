package com.mastergst.usermanagement.runtime.support;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.mastergst.core.common.MasterGSTConstants;

public class InvoiceListSupport {
	
	private static Map<String, Map<String, String>> invoiceTypeListMap = new HashMap<String, Map<String, String>>();
	
	static{
		Map<String, String> gstr1 = new LinkedHashMap<String, String>();
		gstr1.put(MasterGSTConstants.ADVANCES, "Advance Receipts (Receipt voucher)");
		gstr1.put(MasterGSTConstants.ATA, MasterGSTConstants.ATA);
		gstr1.put(MasterGSTConstants.ATPAID, "Adv. Adjustments");
		gstr1.put(MasterGSTConstants.TXPA, MasterGSTConstants.TXPA);
		gstr1.put(MasterGSTConstants.B2B, "B2B Invoices");
		gstr1.put(MasterGSTConstants.B2BA, "B2BA Invoices");
		gstr1.put(MasterGSTConstants.B2C, "B2CS (Small) Invoices");
		gstr1.put(MasterGSTConstants.B2CSA, "B2CSA (Small) Invoices");
		gstr1.put(MasterGSTConstants.B2CL, "B2CL (Large)");
		gstr1.put(MasterGSTConstants.B2CLA, "B2CLA (Large)");
		//gstr1.put(MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.CREDIT_DEBIT_NOTES);
		gstr1.put(MasterGSTConstants.CDNA, MasterGSTConstants.CDNA);
		gstr1.put("Credit Note", "Credit Note");
		gstr1.put("Debit Note", "Debit Note");
		//gstr1.put(MasterGSTConstants.CDNUR, MasterGSTConstants.CDNUR);
		gstr1.put(MasterGSTConstants.CDNURA, MasterGSTConstants.CDNURA);
		gstr1.put("Debit Note(UR)", "Debit Note(UR)");
		gstr1.put("Credit Note(UR)", "Credit Note(UR)");
		gstr1.put(MasterGSTConstants.EXPORTS, MasterGSTConstants.EXPORTS);
		gstr1.put(MasterGSTConstants.EXPA, MasterGSTConstants.EXPA);
		gstr1.put(MasterGSTConstants.NIL, "Bill of Supply");
		invoiceTypeListMap.put("GSTR1", gstr1);
		invoiceTypeListMap.put("GSTR1Amnd", gstr1);

		Map<String, String> gstr2 = new LinkedHashMap<String, String>();
		gstr2.put(MasterGSTConstants.ADVANCES, "Advance Payments");
		gstr2.put(MasterGSTConstants.ATPAID, "Adv. Adjustments");
		gstr2.put("B2B", "B2B Invoices");
		gstr2.put(MasterGSTConstants.B2BUR, MasterGSTConstants.B2BUR);
		//gstr2.put("B2C", "B2CS (Small) Invoices");
		//gstr2.put("B2CL", "B2CL (Large)");
		//gstr2.put(MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.CREDIT_DEBIT_NOTES);
		gstr2.put("Credit Note", "Credit Note");
		gstr2.put("Debit Note", "Debit Note");
		//gstr2.put(MasterGSTConstants.CDNUR, MasterGSTConstants.CDNUR);
		gstr2.put("Debit Note(UR)", "Debit Note(UR)");
		gstr2.put("Credit Note(UR)", "Credit Note(UR)");
		gstr2.put(MasterGSTConstants.IMP_GOODS, MasterGSTConstants.IMP_GOODS+"(Bill of Entry)");
		gstr2.put(MasterGSTConstants.IMP_SERVICES, MasterGSTConstants.IMP_SERVICES);
		gstr2.put(MasterGSTConstants.ITC_REVERSAL, MasterGSTConstants.ITC_REVERSAL);
		gstr2.put(MasterGSTConstants.NIL, "Bill of Supply");
		gstr2.put(MasterGSTConstants.ISD, MasterGSTConstants.ISD);
		invoiceTypeListMap.put("GSTR2", gstr2);
		
		invoiceTypeListMap.put(MasterGSTConstants.PURCHASE_REGISTER, gstr2);
		Map<String, String> gstr2a = new LinkedHashMap<String, String>();
		gstr2a.put("B2B", "B2B Invoices");
		gstr2a.put("B2BA", "B2BA Invoices");
		gstr2a.put("CDNA", "CDNA");
		gstr2a.put("Credit Note", "Credit Note");
		gstr2a.put("Debit Note", "Debit Note");
		gstr2a.put(MasterGSTConstants.IMP_GOODS, MasterGSTConstants.IMP_GOODS+"(Bill of Entry)");
		gstr2a.put(MasterGSTConstants.IMP_SERVICES, MasterGSTConstants.IMP_SERVICES);
		gstr2a.put(MasterGSTConstants.ISD, MasterGSTConstants.ISD);
		invoiceTypeListMap.put("GSTR2A", gstr2a);
		
		invoiceTypeListMap.put("Unclaimed", gstr2);

		Map<String, String> gstr4 = new LinkedHashMap<String, String>();
		gstr4.put(MasterGSTConstants.ADVANCES, "Advance Receipts (Receipt voucher)");
		gstr4.put("Adv. Adjustments", "Adv. Adjustments");
		gstr4.put("B2B", "B2B Invoices");
		gstr4.put(MasterGSTConstants.B2BUR, MasterGSTConstants.B2BUR);
		gstr4.put(MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.CREDIT_DEBIT_NOTES);
		//gstr2.put("Credit Note", "Credit Note");
		gstr4.put(MasterGSTConstants.CDNUR, MasterGSTConstants.CDNUR);
		//gstr4.put("Debit Note", "Debit Note");
		//gstr4.put("Credit Note", "Credit Note");
		//gstr4.put("Debit Note(UR)", "Debit Note(UR)");
		//gstr4.put("Credit Note(UR)", "Credit Note(UR)");
		gstr4.put(MasterGSTConstants.IMP_SERVICES, MasterGSTConstants.IMP_SERVICES);
		invoiceTypeListMap.put("GSTR4", gstr4);
		
		Map<String, String> gstr5 = new LinkedHashMap<String, String>();
		gstr5.put("B2B", "B2B Invoices");
		gstr5.put("B2C", "B2CS (Small) Invoices");
		gstr5.put(MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.CREDIT_DEBIT_NOTES);
		//gstr2.put("Credit Note", "Credit Note");
		gstr5.put(MasterGSTConstants.CDNUR, MasterGSTConstants.CDNUR);
		//gstr5.put("Debit Note", "Debit Note");
		//gstr5.put("Credit Note", "Credit Note");
		//gstr5.put("Debit Note(UR)", "Debit Note(UR)");
		//gstr5.put("Credit Note(UR)", "Credit Note(UR)");
		gstr5.put(MasterGSTConstants.IMP_GOODS, MasterGSTConstants.IMP_GOODS+"(Bill of Entry)");
		invoiceTypeListMap.put("GSTR5", gstr5);
		
		Map<String, String> gstr6 = new LinkedHashMap<String, String>();
		gstr6.put("B2B", "B2B Invoices");
		gstr5.put(MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.CREDIT_DEBIT_NOTES);
		//gstr6.put("Debit Note", "Debit Note");
		//gstr6.put("Credit Note", "Credit Note");
		gstr6.put(MasterGSTConstants.ISD, MasterGSTConstants.ISD);
		invoiceTypeListMap.put("GSTR6", gstr6);
		Map<String, String> ewaybill = new LinkedHashMap<String, String>();
		ewaybill.put("B2B", "B2B Invoices");
		invoiceTypeListMap.put("EWAYBILL", ewaybill);
		Map<String, String> einvoice = new LinkedHashMap<String, String>();
		einvoice.put("B2B", "B2B Invoices");
		einvoice.put(MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.CREDIT_DEBIT_NOTES);
		einvoice.put(MasterGSTConstants.CDNUR, MasterGSTConstants.CDNUR);
		einvoice.put(MasterGSTConstants.EXPORTS, MasterGSTConstants.EXPORTS);
		einvoice.put(MasterGSTConstants.B2C, MasterGSTConstants.B2C);
		invoiceTypeListMap.put("EINVOICE", einvoice);
		
		Map<String, String> anxinvoice = new LinkedHashMap<String, String>();
		anxinvoice.put("B2B", "B2B Invoices");
		anxinvoice.put(MasterGSTConstants.B2C, "B2C Invoices");
		anxinvoice.put(MasterGSTConstants.DE,"Deemed Exports");
		anxinvoice.put("SEWP","Supplies to SEZ with payment");
		anxinvoice.put("SEWOP","Supplies to SEZ without payment");
		anxinvoice.put(MasterGSTConstants.EXPWP,"Exports with payment");
		anxinvoice.put(MasterGSTConstants.EXPWOP,"Exports without payment");
		anxinvoice.put(MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.CREDIT_DEBIT_NOTES);
		anxinvoice.put(MasterGSTConstants.CDNUR, MasterGSTConstants.CDNUR);
		anxinvoice.put("Import Goods", MasterGSTConstants.IMP_GOODS);
		anxinvoice.put("Import Services", MasterGSTConstants.IMP_SERVICES);
		invoiceTypeListMap.put("ANX1", anxinvoice);
		
		invoiceTypeListMap.put("ANX2", anxinvoice);
		
		
	}
	
	public static Map<String, String> getInvTypeList(String inv){
		return invoiceTypeListMap.get(inv);
	}

}
