package com.mastergst.usermanagement.runtime.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.util.SystemOutLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Lists;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.AccountingJournalPaymentItems;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CustomData;
import com.mastergst.usermanagement.runtime.domain.CustomFields;
import com.mastergst.usermanagement.runtime.domain.DeliveryChallan;
import com.mastergst.usermanagement.runtime.domain.EWAYBILL;
import com.mastergst.usermanagement.runtime.domain.Estimates;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.PartnerClient;
import com.mastergst.usermanagement.runtime.domain.PaymentItems;
import com.mastergst.usermanagement.runtime.domain.Payments;
import com.mastergst.usermanagement.runtime.domain.ProformaInvoices;
import com.mastergst.usermanagement.runtime.domain.PurchaseOrder;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.repository.CustomFieldsRepository;
import com.mongodb.WriteResult;
@Service
public class MigrateDao {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	CustomFieldsRepository customFieldsRepository;
	private static SimpleDateFormat dateFormatOnlyDate = new SimpleDateFormat("dd/MM/yyyy");
		private static String DOUBLE_FORMAT  = "%.2f";
		
		public void updateForSumStrs(){
			//updateForSumStrs(GSTR1.class,MasterGSTConstants.GSTR1);
			//updateForSumStrs(PurchaseRegister.class,MasterGSTConstants.PURCHASE_REGISTER);
			updateForSumStrs(ProformaInvoices.class,MasterGSTConstants.PROFORMAINVOICES);
			//updateForSumStrs(GSTR2.class,MasterGSTConstants.GSTR2);
			updateForSumStrs(Estimates.class,MasterGSTConstants.ESTIMATES);
			updateForSumStrs(PurchaseOrder.class,MasterGSTConstants.PURCHASEORDER);
			updateForSumStrs(DeliveryChallan.class,MasterGSTConstants.DELIVERYCHALLANS);
			updateForSumStrs(EWAYBILL.class,MasterGSTConstants.EWAYBILL);
		}
		
		public void updateForgstr1pendingamt1718SumStrs(String yrcd){
			updateForgstr1pendingamt1718SumStrs(GSTR1.class,MasterGSTConstants.GSTR1,yrcd);
		}
		public void updateForgstr1pendingamt1819SumStrs(){
			updateForgstr1pendingamt1819SumStrs(GSTR1.class,MasterGSTConstants.GSTR1,"2018-2019");
		}
		public void updateForgstr1pendingamt1920SumStrs(){
			updateForgstr1pendingamt1920SumStrs(GSTR1.class,MasterGSTConstants.GSTR1,"2019-2020");
		}
		public void updateForgstr1pendingamt2021SumStrs(){
			updateForgstr1pendingamt2021SumStrs(GSTR1.class,MasterGSTConstants.GSTR1,"2020-2021");
		}
		
		public void updateForprpendingamt1718SumStrs(String yrcd){
			updateForprpendingamt1718SumStrs(PurchaseRegister.class,MasterGSTConstants.PURCHASE_REGISTER,yrcd);
		}
		public void updateForprpendingamt1819SumStrs(){
			updateForprpendingamt1819SumStrs(PurchaseRegister.class,MasterGSTConstants.PURCHASE_REGISTER,"2018-2019");
		}
		public void updateForprpendingamt1920SumStrs(){
			updateForprpendingamt1920SumStrs(PurchaseRegister.class,MasterGSTConstants.PURCHASE_REGISTER,"2019-2020");
		}
		public void updateForprpendingamt2021SumStrs(){
			updateForprpendingamt2021SumStrs(PurchaseRegister.class,MasterGSTConstants.PURCHASE_REGISTER,"2020-2021");
		}
		
		
		public void updateForgstr21819SumStrs(){
			updateForgstr2SumStrs(GSTR2.class,MasterGSTConstants.GSTR2,"2018-2019");
		}
		public void updateForgstr21920SumStrs(){
			updateForgstr2SumStrs(GSTR2.class,MasterGSTConstants.GSTR2,"2019-2020");
		}
		
		public void updateForgstr1SumStrs(){
			updateForSumStrs(GSTR1.class,MasterGSTConstants.GSTR1);
		}
		
		public void updateForprSumStrs(){
			updateForSumStrs(PurchaseRegister.class,MasterGSTConstants.PURCHASE_REGISTER);
		}
		
		public void updateForgstr21718SumStrs(){
			updateForgstr2SumStrs(GSTR2.class,MasterGSTConstants.GSTR2,"2017-2018");
		}
		
		private void updateForSumStrs(Class<? extends InvoiceParent> invoiceType,String returntype){
			Query query = new Query();
			query.fields().include("_id");
			query.fields().include("totaltaxableamount");
			query.fields().include("totaltax");
			query.fields().include("totalamount");
			query.fields().include("dateofinvoice");
			query.fields().include("invoiceno");
			if(returntype.equals(MasterGSTConstants.EWAYBILL)) {
				query.fields().include("eBillDate");
			}
			int length = 5000;
			for(int i=0;i< Integer.MAX_VALUE;i++) {
				Pageable pageable = new PageRequest(i, length);
				query.with(pageable);
			List<? extends InvoiceParent> invoices = mongoTemplate.find(query, invoiceType);
			if(NullUtil.isEmpty(invoices)) {
				break;
			}
			int totalRowsEffected = 0;
			for(InvoiceParent invoice : invoices){			
				try {
					int count = 0;
					Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
					Update update = new Update();
					
					Double totalTaxableAmt = invoice.getTotaltaxableamount();
					Double totalTax = invoice.getTotaltax();
					Double totalAmt = invoice.getTotalamount();
					Date dateOfInvoice = invoice.getDateofinvoice();
					
					
					String totalTaxableAmtStr = "";
					if(NullUtil.isNotEmpty(totalTaxableAmt)) {
						totalTaxableAmtStr = String.format(DOUBLE_FORMAT,Math.abs(totalTaxableAmt));
						update.set("totaltaxableamount_str", totalTaxableAmtStr);
						update.set("totaltaxableamount",Math.abs(totalTaxableAmt));
						count++;
					}
					String totalTaxStr = "";
					if(NullUtil.isNotEmpty(totalTax)) {
						totalTaxStr = String.format(DOUBLE_FORMAT,Math.abs(totalTax));
						update.set("totaltax_str", totalTaxStr);
						update.set("totaltax",Math.abs(totalTax));
						count++;
					}
					String totalAmtStr = "";
					if(NullUtil.isNotEmpty(totalAmt)) {
						totalAmtStr = String.format(DOUBLE_FORMAT,Math.abs(totalAmt));
						update.set("totalamount_str", totalAmtStr);
						update.set("totalamount",Math.abs(totalAmt));
						count++;
					}
					
					String dateOfInvoiceStr = "";
					if(NullUtil.isNotEmpty(dateOfInvoice)) {
						dateOfInvoiceStr = dateFormatOnlyDate.format(dateOfInvoice);
						update.set("dateofinvoice_str", dateOfInvoiceStr);
						count++;
					}
					if(returntype.equals(MasterGSTConstants.EWAYBILL)) {
						String ewayBillDateStr = "";
						Date ewayBillDate = invoice.geteBillDate();
						if(NullUtil.isNotEmpty(ewayBillDate)) {
							ewayBillDateStr = dateFormatOnlyDate.format(ewayBillDate);
							update.set("ewayBillDate_str", ewayBillDateStr);
							count++;
						}
					}
					if(count > 0) {
						WriteResult result = mongoTemplate.updateFirst(queryTmp, update, invoiceType);
						totalRowsEffected += result.getN();
					}
				} catch (Exception e) {
					if(NullUtil.isNotEmpty(invoice.getInvoiceno())) {
						System.out.println(invoice.getId()+"-"+invoice.getInvoiceno()+" is not able to update in "+invoiceType.getName());
					}else {
						System.out.println(invoice.getId()+" is not able to update in "+invoiceType.getName());
					}
					e.printStackTrace();
				}
			}
			System.out.println("TotalRows Effceted: "+totalRowsEffected+ ", in "+invoiceType.getName());
		}
		}
		
		private void updateForgstr2SumStrs(Class<? extends InvoiceParent> invoiceType,String returntype, String yrcd){
			Query query = new Query();
			query.fields().include("_id");
			query.fields().include("totaltaxableamount");
			query.fields().include("totaltax");
			query.fields().include("totalamount");
			query.fields().include("dateofinvoice");
			query.fields().include("invoiceno");
			if(returntype.equals(MasterGSTConstants.EWAYBILL)) {
				query.fields().include("eBillDate");
			}
			Criteria criteria = Criteria.where("yrCd").is(yrcd);
			query.addCriteria(criteria);
			int length = 5000;
			for(int i=0;i< Integer.MAX_VALUE;i++) {
				Pageable pageable = new PageRequest(i, length);
				query.with(pageable);
			List<? extends InvoiceParent> invoices = mongoTemplate.find(query, invoiceType);
			if(NullUtil.isEmpty(invoices)) {
				break;
			}
			int totalRowsEffected = 0;
			for(InvoiceParent invoice : invoices){			
				try {
					int count = 0;
					Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
					Update update = new Update();
					
					Double totalTaxableAmt = invoice.getTotaltaxableamount();
					Double totalTax = invoice.getTotaltax();
					Double totalAmt = invoice.getTotalamount();
					Date dateOfInvoice = invoice.getDateofinvoice();
					
					
					String totalTaxableAmtStr = "";
					if(NullUtil.isNotEmpty(totalTaxableAmt)) {
						totalTaxableAmtStr = String.format(DOUBLE_FORMAT,Math.abs(totalTaxableAmt));
						update.set("totaltaxableamount_str", totalTaxableAmtStr);
						update.set("totaltaxableamount",Math.abs(totalTaxableAmt));
						count++;
					}
					String totalTaxStr = "";
					if(NullUtil.isNotEmpty(totalTax)) {
						totalTaxStr = String.format(DOUBLE_FORMAT,Math.abs(totalTax));
						update.set("totaltax_str", totalTaxStr);
						update.set("totaltax",Math.abs(totalTax));
						count++;
					}
					String totalAmtStr = "";
					if(NullUtil.isNotEmpty(totalAmt)) {
						totalAmtStr = String.format(DOUBLE_FORMAT,Math.abs(totalAmt));
						update.set("totalamount_str", totalAmtStr);
						update.set("totalamount",Math.abs(totalAmt));
						count++;
					}
					
					String dateOfInvoiceStr = "";
					if(NullUtil.isNotEmpty(dateOfInvoice)) {
						dateOfInvoiceStr = dateFormatOnlyDate.format(dateOfInvoice);
						update.set("dateofinvoice_str", dateOfInvoiceStr);
						count++;
					}
					if(returntype.equals(MasterGSTConstants.EWAYBILL)) {
						String ewayBillDateStr = "";
						Date ewayBillDate = invoice.geteBillDate();
						if(NullUtil.isNotEmpty(ewayBillDate)) {
							ewayBillDateStr = dateFormatOnlyDate.format(ewayBillDate);
							update.set("ewayBillDate_str", ewayBillDateStr);
							count++;
						}
					}
					if(count > 0) {
						WriteResult result = mongoTemplate.updateFirst(queryTmp, update, invoiceType);
						totalRowsEffected += result.getN();
					}
				} catch (Exception e) {
					if(NullUtil.isNotEmpty(invoice.getInvoiceno())) {
						System.out.println(invoice.getId()+"-"+invoice.getInvoiceno()+" is not able to update in "+invoiceType.getName());
					}else {
						System.out.println(invoice.getId()+" is not able to update in "+invoiceType.getName());
					}
					e.printStackTrace();
				}
			}
			System.out.println("TotalRows Effceted: "+totalRowsEffected+ ", in "+invoiceType.getName());
		}
		}
	
	
	
	public void updateGstr1TaxAmounts(){
		
		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("items");
		query.fields().include("totaltaxableamount");
		query.fields().include("totaltax");
		query.fields().include("dateofinvoice");
		query.fields().include("fp");
		query.fields().include("invtype");
		query.fields().include("totalamount");
		query.fields().include("cdn");
		query.fields().include("cdnr");
		query.fields().include("cdnur");
		query.fields().include("gstStatus");
		int length = 5000;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			System.out.println(i);
			System.out.println(length);
		List<GSTR1> invoices = mongoTemplate.find(query, GSTR1.class);
		if(NullUtil.isEmpty(invoices)) {
			break;
		}
		System.out.println(invoices.size());
		for(GSTR1 invoice : invoices){
			try {
		List<Item> items = invoice.getItems();
		double totalIgstAmount = 0.0;
		double totalCgstAmount = 0.0;
		double totalSgstAmount = 0.0;
		double totalCessAmount = 0.0;
		double totalExemptedAmount = 0.0;
		for(Item item : items){
		totalIgstAmount += item.getIgstamount() == null ? 0.0 : item.getIgstamount();
		totalCgstAmount += item.getCgstamount() == null ? 0.0 : item.getCgstamount();
		totalSgstAmount += item.getSgstamount() == null ? 0.0 : item.getSgstamount();
		totalCessAmount += item.getCessamount() == null ? 0.0 : item.getCessamount();
		Double quantity = item.getQuantity();
		Double exmp = item.getExmepted();
		if(quantity != null && exmp != null){
		totalExemptedAmount += quantity.floatValue() * exmp;
		}
		}
		Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
		Update update = new Update();
		update.set("totalIgstAmount", totalIgstAmount);
		update.set("totalCgstAmount", totalCgstAmount);
		update.set("totalSgstAmount", totalSgstAmount);
		update.set("totalCessAmount", totalCessAmount);
		update.set("totalExemptedAmount", totalExemptedAmount);

		String invType = invoice.getInvtype();
		boolean isDebitCreditNotes = "Credit/Debit Notes".equals(invType);
		boolean isCDNA = "CDNA".equals(invType);
		boolean isCDNUR = "Credit/Debit Note for Unregistered Taxpayers".equals(invType);
		boolean isCreditNote = "Credit Note".equals(invType);
		boolean isDebitNote = "Debit Note".equals(invType);
		boolean isCreditNoteUr = "Credit Note(UR)".equals(invType);
		boolean isDebitNoteUr = "Debit Note(UR)".equals(invType);
		boolean isCdnNtNttyExists = invoice.getCdn() != null && invoice.getCdn().size() > 0 && invoice.getCdn().get(0).getNt() != null && invoice.getCdn().get(0).getNt().size() > 0;
		String cdnNtNtty = null;
		if(isCdnNtNttyExists){
		cdnNtNtty = invoice.getCdn().get(0).getNt().get(0).getNtty();
		}

		boolean isCdnrNtNttyExists = invoice.getCdnr() != null && invoice.getCdnr().size() > 0 && invoice.getCdnr().get(0).getNt() != null && invoice.getCdnr().get(0).getNt().size() > 0;
		String cdnrNtNtty = null;
		if(isCdnrNtNttyExists){
		cdnrNtNtty = invoice.getCdnr().get(0).getNt().get(0).getNtty();
		}

		boolean isCdnurNttyExists = invoice.getCdnur() != null && invoice.getCdnur().size() > 0;
		String cdnurNtty = null;
		if(isCdnurNttyExists){
		cdnurNtty = invoice.getCdnur().get(0).getNtty();
		}
		int sumFactor = 1;


		if(!"CANCELLED".equals(invoice.getGstStatus())){
			if((isDebitCreditNotes || isCDNA )){
				if(isCdnNtNttyExists){
					if(!"D".equals(cdnNtNtty)){
					sumFactor = -1;
					}
				}else if(isCdnrNtNttyExists){
					if("C".equals(cdnrNtNtty)){
					sumFactor = -1;
					}
				}
			}else if(isCDNUR && isCdnurNttyExists){
				if("C".equals(cdnurNtty)){
				sumFactor = -1;
				}
			}
	
			if(isCreditNote || isDebitNote || isCDNA){
				if(isCdnNtNttyExists){
					if(!"D".equals(cdnNtNtty)){
					sumFactor = -1;
					}
				}else if(isCdnrNtNttyExists){
					if("C".equals(cdnrNtNtty)){
					sumFactor = -1;
					}
				}
			}else if((isCreditNoteUr || isDebitNoteUr || isCDNUR) && isCdnurNttyExists){
				if("C".equals(cdnurNtty)){
				sumFactor = -1;
				}
			}
		}else{
		sumFactor = 0;
		}
		update.set("sftr", sumFactor);
		update.set("csftr", 1);
		int month=-1,year=-1;
		boolean chnged = false;
		if("Advances".equals(invType) || "Nil Supplies".equals(invType) || "Advance Adjusted Detail".equals(invType)){
			String fp = invoice.getFp();
			if(fp != null){
				try {
					month = Integer.parseInt(fp.substring(0,2));
					month--;
					year = Integer.parseInt(fp.substring(2));
					chnged = true;
				} catch (NumberFormatException e) {
					
				}
			}
		}
		if(!chnged){
			Date dt = (Date)invoice.getDateofinvoice();
			if(dt != null){
				month = dt.getMonth();
				year = dt.getYear()+1900;
			}
		}
		
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
		month++;
		update.set("mthCd", ""+month);
		update.set("qrtCd", ""+quarter);
		update.set("yrCd", ""+yearCode);

		WriteResult result = mongoTemplate.updateFirst(queryTmp, update, GSTR1.class);
		System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
			}catch(Exception e) {
				//System.out.println(e.printStackTrace()+"doc affected ---- "+ invoice.getId());
				e.printStackTrace();
			}
		//break;
		}
	}

	}
	
	public void updateEwayBillTaxAmounts(){
		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("items");
		query.fields().include("totaltaxableamount");
		query.fields().include("totaltax");
		query.fields().include("dateofinvoice");
		query.fields().include("eBillDate");
		query.fields().include("ewayBillNumber");
		query.fields().include("fp");
		query.fields().include("invtype");
		query.fields().include("totalamount");
		query.fields().include("cdn");
		query.fields().include("cdnr");
		query.fields().include("cdnur");
		query.fields().include("gstStatus");
		int length = 5000;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			List<EWAYBILL> invoices = mongoTemplate.find(query, EWAYBILL.class);
		if(NullUtil.isEmpty(invoices)) {
			break;
		}
		
		for(EWAYBILL invoice : invoices){
		List<Item> items = invoice.getItems();
		double totalIgstAmount = 0.0;
		double totalCgstAmount = 0.0;
		double totalSgstAmount = 0.0;
		double totalCessAmount = 0.0;
		double totalExemptedAmount = 0.0;
		for(Item item : items){
		totalIgstAmount += item.getIgstamount() == null ? 0.0 : item.getIgstamount();
		totalCgstAmount += item.getCgstamount() == null ? 0.0 : item.getCgstamount();
		totalSgstAmount += item.getSgstamount() == null ? 0.0 : item.getSgstamount();
		totalCessAmount += item.getCessamount() == null ? 0.0 : item.getCessamount();
		Double quantity = item.getQuantity();
		Double exmp = item.getExmepted();
		if(quantity != null && exmp != null){
		totalExemptedAmount += quantity.floatValue() * exmp;
		}
		}
		Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
		Update update = new Update();
		update.set("totalIgstAmount", totalIgstAmount);
		update.set("totalCgstAmount", totalCgstAmount);
		update.set("totalSgstAmount", totalSgstAmount);
		update.set("totalCessAmount", totalCessAmount);
		update.set("totalExemptedAmount", totalExemptedAmount);
		int sumFactor = 1;
		update.set("sftr", sumFactor);
		update.set("csftr", 1);
		int month=-1,year=-1;
			Date dt = invoice.geteBillDate();
			if(dt != null){
				month = dt.getMonth();
				year = dt.getYear()+1900;
			}
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
		month++;
		update.set("mthCd", ""+month);
		update.set("qrtCd", ""+quarter);
		update.set("yrCd", ""+yearCode);
		WriteResult result = mongoTemplate.updateFirst(queryTmp, update, EWAYBILL.class);
		System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
		}
		}
		}
	public void updatePurchageRegTaxAmounts(){
		
		
		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("items");
		query.fields().include("totaltaxableamount");
		query.fields().include("totaltax");
		query.fields().include("dateofinvoice");
		query.fields().include("totalamount");
		query.fields().include("cdn");
		query.fields().include("cdnur");
		query.fields().include("gstStatus");
		query.fields().include("billDate");
		Calendar cal = Calendar.getInstance();
		int months = cal.get(2);
		int years = cal.get(Calendar.YEAR);
		cal.set(years, months, 0, 23, 59, 59);
		/*
		Date stDate = new java.util.Date(cal.getTimeInMillis());
		System.out.println(stDate);
		
		 * Criteria criteria = Criteria.where("createdDate").gte(stDate);
		 * query.addCriteria(criteria);
		 */
		int length = 5000;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
		List<PurchaseRegister> invoices = mongoTemplate.find(query, PurchaseRegister.class);
		if(NullUtil.isEmpty(invoices)) {
			break;
		}
		System.out.println(invoices.size());
		for(PurchaseRegister invoice : invoices){
			List<Item> items = invoice.getItems();
			double totalIgstAmount = 0.0;
			double totalCgstAmount = 0.0;
			double totalSgstAmount = 0.0;
			double totalCessAmount = 0.0;
			double totalExemptedAmount = 0.0;
			for(Item item : items){
				totalIgstAmount += item.getIgstamount() == null ? 0.0 : item.getIgstamount();
				totalCgstAmount += item.getCgstamount() == null ? 0.0 : item.getCgstamount();
				totalSgstAmount += item.getSgstamount() == null ? 0.0 : item.getSgstamount();
				totalCessAmount += item.getCessamount() == null ? 0.0 : item.getCessamount();
				Double quantity = item.getQuantity();
				Double exmp = item.getExmepted();
				if(quantity != null && exmp != null){
					totalExemptedAmount += quantity.floatValue() * exmp;
				}
			}
			Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
			Update update = new Update();
			update.set("totalIgstAmount", totalIgstAmount);
			update.set("totalCgstAmount", totalCgstAmount);
			update.set("totalSgstAmount", totalSgstAmount);
			update.set("totalCessAmount", totalCessAmount);
			update.set("totalExemptedAmount", totalExemptedAmount);
			
			String invType = invoice.getInvtype();
			boolean isDebitCreditNotes = "Credit/Debit Notes".equals(invType);
			boolean isCDNA = "CDNA".equals(invType);
			boolean isCDNUR = "Credit/Debit Note for Unregistered Taxpayers".equals(invType);
			boolean isCreditNote = "Credit Note".equals(invType);
			boolean isDebitNote = "Debit Note".equals(invType);
			boolean isCreditNoteUr = "Credit Note(UR)".equals(invType);
			boolean isDebitNoteUr = "Debit Note(UR)".equals(invType);
			boolean isCdnNtNttyExists = invoice.getCdn() != null && invoice.getCdn().size() > 0 && invoice.getCdn().get(0).getNt() != null && invoice.getCdn().get(0).getNt().size() > 0;
			String cdnNtNtty = null;
			if(isCdnNtNttyExists){
				cdnNtNtty = invoice.getCdn().get(0).getNt().get(0).getNtty();
			}
			
			/*boolean isCdnrNtNttyExists = invoice.getCdnr() != null && invoice.getCdnr().size() > 0 && invoice.getCdnr().get(0).getNt() != null && invoice.getCdnr().get(0).getNt().size() > 0;
		String cdnrNtNtty = null;
		if(isCdnrNtNttyExists){
		cdnrNtNtty = invoice.getCdnr().get(0).getNt().get(0).getNtty();
		}*/
			
			boolean isCdnurNttyExists = invoice.getCdnur() != null && invoice.getCdnur().size() > 0;
			String cdnurNtty = null;
			if(isCdnurNttyExists){
				cdnurNtty = invoice.getCdnur().get(0).getNtty();
			}
			int sumFactor = 1;
			
			
			if(!"CANCELLED".equals(invoice.getGstStatus())){
				if((isDebitCreditNotes || isCDNA )){
					if(isCdnNtNttyExists){
						if(!"D".equals(cdnNtNtty)){
							sumFactor = -1;
						}
					}/*else if(isCdnrNtNttyExists){
		if("C".equals(cdnrNtNtty)){
		sumFactor = -1;
		}
		}*/
				}else if(isCDNUR && isCdnurNttyExists){
					if("C".equals(cdnurNtty)){
						sumFactor = -1;
					}
				}
				
				if(isCreditNote || isDebitNote || isCDNA){
					if(isCdnNtNttyExists){
						if(!"D".equals(cdnNtNtty)){
							sumFactor = -1;
						}
					}/*else if(isCdnrNtNttyExists){
		if("C".equals(cdnrNtNtty)){
		sumFactor = -1;
		}
		}*/
				}else if((isCreditNoteUr || isDebitNoteUr || isCDNUR) && isCdnurNttyExists){
					if("C".equals(cdnurNtty)){
						sumFactor = -1;
					}
				}
			}else{
				sumFactor = 0;
			}
			update.set("sftr", sumFactor);
			update.set("csftr", 1);
			Date dt = (Date)invoice.getDateofinvoice();
			int month = dt.getMonth();
			int year = dt.getYear()+1900;
			int quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			update.set("mthCd", ""+month);
			update.set("qrtCd", ""+quarter);
			update.set("yrCd", ""+yearCode);
			if(NullUtil.isNotEmpty(invoice.getBillDate())) {
				Date billdt = (Date)invoice.getBillDate();
				int billmonth = billdt.getMonth();
				int billyear = billdt.getYear()+1900;
				int billquarter = billmonth/3;
				billquarter = billquarter == 0 ? 4 : billquarter;
				String billyearCode = billquarter == 4 ? (billyear-1)+"-"+billyear : (billyear)+"-"+(billyear+1);
				billmonth++;
				update.set("trDatemthCd", ""+billmonth);
				update.set("trDateqrtCd", ""+billquarter);
				update.set("trDateyrCd", ""+billyearCode);
			}
			WriteResult result = mongoTemplate.updateFirst(queryTmp, update, PurchaseRegister.class);
			System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
			//break;
		}
		}
		
	}
	
	
public void updateGstr2ATaxAmounts(){
		
		
		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("items");
		query.fields().include("totaltaxableamount");
		query.fields().include("totaltax");
		query.fields().include("dateofinvoice");
		query.fields().include("fp");
		query.fields().include("invtype");
		query.fields().include("totalamount");
		query.fields().include("cdn");
		query.fields().include("cdnur");
		query.fields().include("gstStatus");
		Calendar cal = Calendar.getInstance();
		int months = cal.get(2);
		int years = cal.get(Calendar.YEAR);
		cal.set(years, months, 0, 23, 59, 59);
		
		/*
		 * Date stDate = new java.util.Date(cal.getTimeInMillis());
		 * System.out.println(stDate); Criteria criteria =
		 * Criteria.where("createdDate").gte(stDate); query.addCriteria(criteria);
		 */
		int length = 5000;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
		List<GSTR2> invoices = mongoTemplate.find(query, GSTR2.class);
		if(NullUtil.isEmpty(invoices)) {
			break;
		}
		System.out.println(invoices.size());
		for(GSTR2 invoice : invoices){
			List<Item> items = invoice.getItems();
			double totalIgstAmount = 0.0;
			double totalCgstAmount = 0.0;
			double totalSgstAmount = 0.0;
			double totalCessAmount = 0.0;
			double totalExemptedAmount = 0.0;
			for(Item item : items){
				totalIgstAmount += item.getIgstamount() == null ? 0.0 : item.getIgstamount();
				totalCgstAmount += item.getCgstamount() == null ? 0.0 : item.getCgstamount();
				totalSgstAmount += item.getSgstamount() == null ? 0.0 : item.getSgstamount();
				totalCessAmount += item.getCessamount() == null ? 0.0 : item.getCessamount();
				Double quantity = item.getQuantity();
				Double exmp = item.getExmepted();
				if(quantity != null && exmp != null){
					totalExemptedAmount += quantity.floatValue() * exmp;
				}
			}
			Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
			Update update = new Update();
			update.set("totalIgstAmount", totalIgstAmount);
			update.set("totalCgstAmount", totalCgstAmount);
			update.set("totalSgstAmount", totalSgstAmount);
			update.set("totalCessAmount", totalCessAmount);
			update.set("totalExemptedAmount", totalExemptedAmount);
			
			String invType = invoice.getInvtype();
			boolean isDebitCreditNotes = "Credit/Debit Notes".equals(invType);
			boolean isCDNA = "CDNA".equals(invType);
			boolean isCDNUR = "Credit/Debit Note for Unregistered Taxpayers".equals(invType);
			boolean isCreditNote = "Credit Note".equals(invType);
			boolean isDebitNote = "Debit Note".equals(invType);
			boolean isCreditNoteUr = "Credit Note(UR)".equals(invType);
			boolean isDebitNoteUr = "Debit Note(UR)".equals(invType);
			boolean isCdnNtNttyExists = invoice.getCdn() != null && invoice.getCdn().size() > 0 && invoice.getCdn().get(0).getNt() != null && invoice.getCdn().get(0).getNt().size() > 0;
			String cdnNtNtty = null;
			if(isCdnNtNttyExists){
				cdnNtNtty = invoice.getCdn().get(0).getNt().get(0).getNtty();
			}
			
			/*boolean isCdnrNtNttyExists = invoice.getCdnr() != null && invoice.getCdnr().size() > 0 && invoice.getCdnr().get(0).getNt() != null && invoice.getCdnr().get(0).getNt().size() > 0;
		String cdnrNtNtty = null;
		if(isCdnrNtNttyExists){
		cdnrNtNtty = invoice.getCdnr().get(0).getNt().get(0).getNtty();
		}*/
			
			boolean isCdnurNttyExists = invoice.getCdnur() != null && invoice.getCdnur().size() > 0;
			String cdnurNtty = null;
			if(isCdnurNttyExists){
				cdnurNtty = invoice.getCdnur().get(0).getNtty();
			}
			int sumFactor = 1;
			
			
			if(!"CANCELLED".equals(invoice.getGstStatus())){
				if((isDebitCreditNotes || isCDNA )){
					if(isCdnNtNttyExists){
						if(!"D".equals(cdnNtNtty)){
							sumFactor = -1;
						}
					}/*else if(isCdnrNtNttyExists){
		if("C".equals(cdnrNtNtty)){
		sumFactor = -1;
		}
		}*/
				}else if(isCDNUR && isCdnurNttyExists){
					if("C".equals(cdnurNtty)){
						sumFactor = -1;
					}
				}
				
				if(isCreditNote || isDebitNote || isCDNA){
					if(isCdnNtNttyExists){
						if(!"D".equals(cdnNtNtty)){
							sumFactor = -1;
						}
					}/*else if(isCdnrNtNttyExists){
		if("C".equals(cdnrNtNtty)){
		sumFactor = -1;
		}
		}*/
				}else if((isCreditNoteUr || isDebitNoteUr || isCDNUR) && isCdnurNttyExists){
					if("C".equals(cdnurNtty)){
						sumFactor = -1;
					}
				}
			}else{
				sumFactor = 0;
			}
			update.set("sftr", sumFactor);
			update.set("csftr", 1);
			int month=-1,year=-1;
			String fp = invoice.getFp();
			if(fp != null){
				try {
					month = Integer.parseInt(fp.substring(0,2));
					month--;
					year = Integer.parseInt(fp.substring(2));
				} catch (NumberFormatException e) {
					
				}
			}
			int quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			update.set("mthCd", ""+month);
			update.set("qrtCd", ""+quarter);
			update.set("yrCd", ""+yearCode);
			
			WriteResult result = mongoTemplate.updateFirst(queryTmp, update, GSTR2.class);
			System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
			//break;
		}
		}
	}

public void updateProformaTaxAmounts(){
	
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("items");
	query.fields().include("totaltaxableamount");
	query.fields().include("totaltax");
	query.fields().include("dateofinvoice");
	query.fields().include("fp");
	query.fields().include("invtype");
	query.fields().include("totalamount");
	query.fields().include("gstStatus");
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
	List<ProformaInvoices> invoices = mongoTemplate.find(query, ProformaInvoices.class);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	System.out.println(invoices.size());
	for(ProformaInvoices invoice : invoices){
	List<Item> items = invoice.getItems();
	double totalIgstAmount = 0.0;
	double totalCgstAmount = 0.0;
	double totalSgstAmount = 0.0;
	double totalCessAmount = 0.0;
	double totalExemptedAmount = 0.0;
	for(Item item : items){
	totalIgstAmount += item.getIgstamount() == null ? 0.0 : item.getIgstamount();
	totalCgstAmount += item.getCgstamount() == null ? 0.0 : item.getCgstamount();
	totalSgstAmount += item.getSgstamount() == null ? 0.0 : item.getSgstamount();
	totalCessAmount += item.getCessamount() == null ? 0.0 : item.getCessamount();
	Double quantity = item.getQuantity();
	Double exmp = item.getExmepted();
	if(quantity != null && exmp != null){
	totalExemptedAmount += quantity.floatValue() * exmp;
	}
	}
	Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
	Update update = new Update();
	update.set("totalIgstAmount", totalIgstAmount);
	update.set("totalCgstAmount", totalCgstAmount);
	update.set("totalSgstAmount", totalSgstAmount);
	update.set("totalCessAmount", totalCessAmount);
	update.set("totalExemptedAmount", totalExemptedAmount);

	String invType = invoice.getInvtype();
	int sumFactor = 1;


	if(!"CANCELLED".equals(invoice.getGstStatus())){
		sumFactor = 1;
	}else{
	sumFactor = 0;
	}
	update.set("sftr", sumFactor);
	update.set("csftr", 1);
	int month=-1,year=-1;
	
		Date dt = (Date)invoice.getDateofinvoice();
		if(dt != null){
			month = dt.getMonth();
			year = dt.getYear()+1900;
		}
	
	
	int quarter = month/3;
	quarter = quarter == 0 ? 4 : quarter;
	String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
	month++;
	update.set("mthCd", ""+month);
	update.set("qrtCd", ""+quarter);
	update.set("yrCd", ""+yearCode);

	WriteResult result = mongoTemplate.updateFirst(queryTmp, update, ProformaInvoices.class);
	System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
	//break;
	}
	}
	}
	
public void updateDeliveryChallanTaxAmounts(){
	
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("items");
	query.fields().include("totaltaxableamount");
	query.fields().include("totaltax");
	query.fields().include("dateofinvoice");
	query.fields().include("fp");
	query.fields().include("invtype");
	query.fields().include("totalamount");
	query.fields().include("gstStatus");
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
	List<DeliveryChallan> invoices = mongoTemplate.find(query, DeliveryChallan.class);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	System.out.println(invoices.size());
	for(DeliveryChallan invoice : invoices){
	List<Item> items = invoice.getItems();
	double totalIgstAmount = 0.0;
	double totalCgstAmount = 0.0;
	double totalSgstAmount = 0.0;
	double totalCessAmount = 0.0;
	double totalExemptedAmount = 0.0;
	for(Item item : items){
	totalIgstAmount += item.getIgstamount() == null ? 0.0 : item.getIgstamount();
	totalCgstAmount += item.getCgstamount() == null ? 0.0 : item.getCgstamount();
	totalSgstAmount += item.getSgstamount() == null ? 0.0 : item.getSgstamount();
	totalCessAmount += item.getCessamount() == null ? 0.0 : item.getCessamount();
	Double quantity = item.getQuantity();
	Double exmp = item.getExmepted();
	if(quantity != null && exmp != null){
	totalExemptedAmount += quantity.floatValue() * exmp;
	}
	}
	Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
	Update update = new Update();
	update.set("totalIgstAmount", totalIgstAmount);
	update.set("totalCgstAmount", totalCgstAmount);
	update.set("totalSgstAmount", totalSgstAmount);
	update.set("totalCessAmount", totalCessAmount);
	update.set("totalExemptedAmount", totalExemptedAmount);

	String invType = invoice.getInvtype();
	int sumFactor = 1;


	if(!"CANCELLED".equals(invoice.getGstStatus())){
		sumFactor = 1;
	}else{
	sumFactor = 0;
	}
	update.set("sftr", sumFactor);
	int month=-1,year=-1;
	
		Date dt = (Date)invoice.getDateofinvoice();
		if(dt != null){
			month = dt.getMonth();
			year = dt.getYear()+1900;
		}
	
	
	int quarter = month/3;
	quarter = quarter == 0 ? 4 : quarter;
	String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
	month++;
	update.set("mthCd", ""+month);
	update.set("qrtCd", ""+quarter);
	update.set("yrCd", ""+yearCode);
	update.set("csftr", 1);
	WriteResult result = mongoTemplate.updateFirst(queryTmp, update, DeliveryChallan.class);
	System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
	//break;
	}
	}

	}
public void updateEstimatesTaxAmounts(){
	
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("items");
	query.fields().include("totaltaxableamount");
	query.fields().include("totaltax");
	query.fields().include("dateofinvoice");
	query.fields().include("fp");
	query.fields().include("invtype");
	query.fields().include("totalamount");
	query.fields().include("gstStatus");
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
	List<Estimates> invoices = mongoTemplate.find(query, Estimates.class);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	System.out.println(invoices.size());
	for(Estimates invoice : invoices){
	List<Item> items = invoice.getItems();
	double totalIgstAmount = 0.0;
	double totalCgstAmount = 0.0;
	double totalSgstAmount = 0.0;
	double totalCessAmount = 0.0;
	double totalExemptedAmount = 0.0;
	for(Item item : items){
	totalIgstAmount += item.getIgstamount() == null ? 0.0 : item.getIgstamount();
	totalCgstAmount += item.getCgstamount() == null ? 0.0 : item.getCgstamount();
	totalSgstAmount += item.getSgstamount() == null ? 0.0 : item.getSgstamount();
	totalCessAmount += item.getCessamount() == null ? 0.0 : item.getCessamount();
	Double quantity = item.getQuantity();
	Double exmp = item.getExmepted();
	if(quantity != null && exmp != null){
	totalExemptedAmount += quantity.floatValue() * exmp;
	}
	}
	Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
	Update update = new Update();
	update.set("totalIgstAmount", totalIgstAmount);
	update.set("totalCgstAmount", totalCgstAmount);
	update.set("totalSgstAmount", totalSgstAmount);
	update.set("totalCessAmount", totalCessAmount);
	update.set("totalExemptedAmount", totalExemptedAmount);

	String invType = invoice.getInvtype();
	int sumFactor = 1;


	if(!"CANCELLED".equals(invoice.getGstStatus())){
		sumFactor = 1;
	}else{
	sumFactor = 0;
	}
	update.set("sftr", sumFactor);
	int month=-1,year=-1;
	
		Date dt = (Date)invoice.getDateofinvoice();
		if(dt != null){
			month = dt.getMonth();
			year = dt.getYear()+1900;
		}
	
	
	int quarter = month/3;
	quarter = quarter == 0 ? 4 : quarter;
	String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
	month++;
	update.set("mthCd", ""+month);
	update.set("qrtCd", ""+quarter);
	update.set("yrCd", ""+yearCode);
	update.set("csftr", 1);
	WriteResult result = mongoTemplate.updateFirst(queryTmp, update, Estimates.class);
	System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
	//break;
	}
	}
	}
public void updatePurchaseorderTaxAmounts(){
	
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("items");
	query.fields().include("totaltaxableamount");
	query.fields().include("totaltax");
	query.fields().include("dateofinvoice");
	query.fields().include("fp");
	query.fields().include("invtype");
	query.fields().include("totalamount");
	query.fields().include("gstStatus");
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
	List<PurchaseOrder> invoices = mongoTemplate.find(query, PurchaseOrder.class);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	System.out.println(invoices.size());
	for(PurchaseOrder invoice : invoices){
	List<Item> items = invoice.getItems();
	double totalIgstAmount = 0.0;
	double totalCgstAmount = 0.0;
	double totalSgstAmount = 0.0;
	double totalCessAmount = 0.0;
	double totalExemptedAmount = 0.0;
	for(Item item : items){
	totalIgstAmount += item.getIgstamount() == null ? 0.0 : item.getIgstamount();
	totalCgstAmount += item.getCgstamount() == null ? 0.0 : item.getCgstamount();
	totalSgstAmount += item.getSgstamount() == null ? 0.0 : item.getSgstamount();
	totalCessAmount += item.getCessamount() == null ? 0.0 : item.getCessamount();
	Double quantity = item.getQuantity();
	Double exmp = item.getExmepted();
	if(quantity != null && exmp != null){
	totalExemptedAmount += quantity.floatValue() * exmp;
	}
	}
	Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
	Update update = new Update();
	update.set("totalIgstAmount", totalIgstAmount);
	update.set("totalCgstAmount", totalCgstAmount);
	update.set("totalSgstAmount", totalSgstAmount);
	update.set("totalCessAmount", totalCessAmount);
	update.set("totalExemptedAmount", totalExemptedAmount);

	String invType = invoice.getInvtype();
	int sumFactor = 1;


	if(!"CANCELLED".equals(invoice.getGstStatus())){
		sumFactor = 1;
	}else{
	sumFactor = 0;
	}
	update.set("sftr", sumFactor);
	int month=-1,year=-1;
	
		Date dt = (Date)invoice.getDateofinvoice();
		if(dt != null){
			month = dt.getMonth();
			year = dt.getYear()+1900;
		}
	
	
	int quarter = month/3;
	quarter = quarter == 0 ? 4 : quarter;
	String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
	month++;
	update.set("mthCd", ""+month);
	update.set("qrtCd", ""+quarter);
	update.set("yrCd", ""+yearCode);
	update.set("csftr", 1);
	WriteResult result = mongoTemplate.updateFirst(queryTmp, update, PurchaseOrder.class);
	System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
	//break;
	}
	}
	}

public void updateGstr1B2CTaxAmounts(){
	
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("items");
	query.fields().include("totaltaxableamount");
	query.fields().include("totaltax");
	query.fields().include("dateofinvoice");
	query.fields().include("fp");
	query.fields().include("invtype");
	query.fields().include("totalamount");
	query.fields().include("cdn");
	query.fields().include("cdnr");
	query.fields().include("cdnur");
	query.fields().include("gstStatus");
	
	query.addCriteria(new Criteria().where("invtype").is("B2C"));
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
		System.out.println(i);
		System.out.println(length);
	List<GSTR1> invoices = mongoTemplate.find(query, GSTR1.class);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	System.out.println(invoices.size());
	for(GSTR1 invoice : invoices){
		try {
	List<Item> items = invoice.getItems();
	double totalIgstAmount = 0.0;
	double totalCgstAmount = 0.0;
	double totalSgstAmount = 0.0;
	double totalCessAmount = 0.0;
	double totalExemptedAmount = 0.0;
	for(Item item : items){
	totalIgstAmount += item.getIgstamount() == null ? 0.0 : item.getIgstamount();
	totalCgstAmount += item.getCgstamount() == null ? 0.0 : item.getCgstamount();
	totalSgstAmount += item.getSgstamount() == null ? 0.0 : item.getSgstamount();
	totalCessAmount += item.getCessamount() == null ? 0.0 : item.getCessamount();
	Double quantity = item.getQuantity();
	Double exmp = item.getExmepted();
	if(quantity != null && exmp != null){
	totalExemptedAmount += quantity.floatValue() * exmp;
	}
	}
	Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
	Update update = new Update();
	update.set("totalIgstAmount", totalIgstAmount);
	update.set("totalCgstAmount", totalCgstAmount);
	update.set("totalSgstAmount", totalSgstAmount);
	update.set("totalCessAmount", totalCessAmount);
	update.set("totalExemptedAmount", totalExemptedAmount);

	String invType = invoice.getInvtype();
	boolean isDebitCreditNotes = "Credit/Debit Notes".equals(invType);
	boolean isCDNA = "CDNA".equals(invType);
	boolean isCDNUR = "Credit/Debit Note for Unregistered Taxpayers".equals(invType);
	boolean isCreditNote = "Credit Note".equals(invType);
	boolean isDebitNote = "Debit Note".equals(invType);
	boolean isCreditNoteUr = "Credit Note(UR)".equals(invType);
	boolean isDebitNoteUr = "Debit Note(UR)".equals(invType);
	boolean isCdnNtNttyExists = invoice.getCdn() != null && invoice.getCdn().size() > 0 && invoice.getCdn().get(0).getNt() != null && invoice.getCdn().get(0).getNt().size() > 0;
	String cdnNtNtty = null;
	if(isCdnNtNttyExists){
	cdnNtNtty = invoice.getCdn().get(0).getNt().get(0).getNtty();
	}

	boolean isCdnrNtNttyExists = invoice.getCdnr() != null && invoice.getCdnr().size() > 0 && invoice.getCdnr().get(0).getNt() != null && invoice.getCdnr().get(0).getNt().size() > 0;
	String cdnrNtNtty = null;
	if(isCdnrNtNttyExists){
	cdnrNtNtty = invoice.getCdnr().get(0).getNt().get(0).getNtty();
	}

	boolean isCdnurNttyExists = invoice.getCdnur() != null && invoice.getCdnur().size() > 0;
	String cdnurNtty = null;
	if(isCdnurNttyExists){
	cdnurNtty = invoice.getCdnur().get(0).getNtty();
	}
	int sumFactor = 1;


	if(!"CANCELLED".equals(invoice.getGstStatus())){
		if((isDebitCreditNotes || isCDNA )){
			if(isCdnNtNttyExists){
				if(!"D".equals(cdnNtNtty)){
				sumFactor = -1;
				}
			}else if(isCdnrNtNttyExists){
				if("C".equals(cdnrNtNtty)){
				sumFactor = -1;
				}
			}
		}else if(isCDNUR && isCdnurNttyExists){
			if("C".equals(cdnurNtty)){
			sumFactor = -1;
			}
		}

		if(isCreditNote || isDebitNote || isCDNA){
			if(isCdnNtNttyExists){
				if(!"D".equals(cdnNtNtty)){
				sumFactor = -1;
				}
			}else if(isCdnrNtNttyExists){
				if("C".equals(cdnrNtNtty)){
				sumFactor = -1;
				}
			}
		}else if((isCreditNoteUr || isDebitNoteUr || isCDNUR) && isCdnurNttyExists){
			if("C".equals(cdnurNtty)){
			sumFactor = -1;
			}
		}
	}else{
	sumFactor = 0;
	}
	update.set("sftr", sumFactor);
	update.set("csftr", 1);
	int month=-1,year=-1;
	boolean chnged = false;
	if("Advances".equals(invType) || "Nil Supplies".equals(invType) || "Advance Adjusted Detail".equals(invType)){
		String fp = invoice.getFp();
		if(fp != null){
			try {
				month = Integer.parseInt(fp.substring(0,2));
				month--;
				year = Integer.parseInt(fp.substring(2));
				chnged = true;
			} catch (NumberFormatException e) {
				
			}
		}
	}
	if(!chnged){
		Date dt = (Date)invoice.getDateofinvoice();
		if(dt != null){
			month = dt.getMonth();
			year = dt.getYear()+1900;
		}
	}
	
	int quarter = month/3;
	quarter = quarter == 0 ? 4 : quarter;
	String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
	month++;
	update.set("mthCd", ""+month);
	update.set("qrtCd", ""+quarter);
	update.set("yrCd", ""+yearCode);

	Double totalTaxableAmt = invoice.getTotaltaxableamount();
	Double totalTax = invoice.getTotaltax();
	Double totalAmt = invoice.getTotalamount();
	Date dateOfInvoice = invoice.getDateofinvoice();
	
	
	String totalTaxableAmtStr = "";
	if(NullUtil.isNotEmpty(totalTaxableAmt)) {
		totalTaxableAmtStr = String.format(DOUBLE_FORMAT,Math.abs(totalTaxableAmt));
		update.set("totaltaxableamount_str", totalTaxableAmtStr);
		update.set("totaltaxableamount",Math.abs(totalTaxableAmt));
		
	}
	String totalTaxStr = "";
	if(NullUtil.isNotEmpty(totalTax)) {
		totalTaxStr = String.format(DOUBLE_FORMAT,Math.abs(totalTax));
		update.set("totaltax_str", totalTaxStr);
		update.set("totaltax",Math.abs(totalTax));
		
	}
	String totalAmtStr = "";
	if(NullUtil.isNotEmpty(totalAmt)) {
		totalAmtStr = String.format(DOUBLE_FORMAT,Math.abs(totalAmt));
		update.set("totalamount_str", totalAmtStr);
		update.set("totalamount",Math.abs(totalAmt));
		
	}
	
	String dateOfInvoiceStr = "";
	if(NullUtil.isNotEmpty(dateOfInvoice)) {
		dateOfInvoiceStr = dateFormatOnlyDate.format(dateOfInvoice);
		update.set("dateofinvoice_str", dateOfInvoiceStr);
		
	}
	
	
	WriteResult result = mongoTemplate.updateFirst(queryTmp, update, GSTR1.class);
	System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
		}catch(Exception e) {
			//System.out.println(e.printStackTrace()+"doc affected ---- "+ invoice.getId());
			e.printStackTrace();
		}
	//break;
	}
}

}

public void updatePartnerClient(){
	
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("createdDate");
	query.fields().include("updatedDate");
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
	List<PartnerClient> invoices = mongoTemplate.find(query, PartnerClient.class);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	System.out.println(invoices.size());
	for(PartnerClient invoice : invoices){
	Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
	Update update = new Update();
	int month=-1,year=-1,wkcd = 1,datecd =1;
	
		Date dt = null;
		
		if(NullUtil.isNotEmpty(invoice.getCreatedDate())) {
			dt = (Date)invoice.getCreatedDate();
		}else if(NullUtil.isNotEmpty(invoice.getUpdatedDate())) {
			dt = (Date)invoice.getUpdatedDate();
		}
		
		if(dt != null){
			month = dt.getMonth();
			year = dt.getYear()+1900;
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			wkcd = cal.get(Calendar.WEEK_OF_MONTH);
			datecd = cal.get(Calendar.DAY_OF_MONTH);
		}
	
	
	int quarter = month/3;
	quarter = quarter == 0 ? 4 : quarter;
	String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
	month++;
	update.set("mthCd", ""+month);
	update.set("weekCd", ""+wkcd);
	update.set("yrCd", ""+yearCode);
	update.set("dayCd", ""+datecd);
	WriteResult result = mongoTemplate.updateFirst(queryTmp, update, PartnerClient.class);
	System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
	//break;
	}
	}
	}


private void updateForgstr1pendingamt1718SumStrs(Class<? extends InvoiceParent> invoiceType,String returntype, String yrcd){
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("totalamount");
	query.fields().include("paymentStatus");
	query.fields().include("dateofinvoice");
	query.fields().include("dueDate");
	Criteria criteria = Criteria.where("yrCd").is(yrcd);
	query.addCriteria(criteria);
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
	List<? extends InvoiceParent> invoices = mongoTemplate.find(query, invoiceType);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	int totalRowsEffected = 0;
	for(InvoiceParent invoice : invoices){			
		try {
			int count = 0;
			Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
			Update update = new Update();
			if(NullUtil.isEmpty(invoice.getPaymentStatus())) {
				if(NullUtil.isNotEmpty(invoice.getTotalamount()) && invoice.getTotalamount() > 0d) {
					update.set("receivedAmount", 0d);
					update.set("pendingAmount", invoice.getTotalamount());
					count++;
				}
			}
			if(NullUtil.isEmpty(invoice.getDueDate())) {
				if(NullUtil.isNotEmpty(invoice.getDateofinvoice())) {
					update.set("dueDate", invoice.getDateofinvoice());
					update.set("termDays","0");
					count++;
				}
			}
			
			if(count > 0) {
				WriteResult result = mongoTemplate.updateFirst(queryTmp, update, invoiceType);
				totalRowsEffected += result.getN();
			}
		} catch (Exception e) {
			if(NullUtil.isNotEmpty(invoice.getInvoiceno())) {
				System.out.println(invoice.getId()+"-"+invoice.getInvoiceno()+" is not able to update in "+invoiceType.getName());
			}else {
				System.out.println(invoice.getId()+" is not able to update in "+invoiceType.getName());
			}
			e.printStackTrace();
		}
	}
	System.out.println("TotalRows Effceted: "+totalRowsEffected+ ", in "+invoiceType.getName());
}
}

private void updateForgstr1pendingamt1819SumStrs(Class<? extends InvoiceParent> invoiceType,String returntype, String yrcd){
	List<String> allStatusList = Lists.newArrayList();
	allStatusList.add("Partially Paid");
	allStatusList.add("Paid");
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("totalamount");
	Criteria criteria = Criteria.where("yrCd").is(yrcd).and("paymentStatus").in(allStatusList);
	query.addCriteria(criteria);
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
	List<? extends InvoiceParent> invoices = mongoTemplate.find(query, invoiceType);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	int totalRowsEffected = 0;
	for(InvoiceParent invoice : invoices){			
		try {
			int count = 0;
			Double pendingAmount = 0d;
			Query paymentquery = new Query();
			Criteria paymentcriteria = Criteria.where("invoiceid").is(invoice.getId().toString());
			paymentquery.addCriteria(paymentcriteria);
			List<Payments> paymentlist = mongoTemplate.find(paymentquery, Payments.class);
			if(NullUtil.isNotEmpty(paymentlist)) {
				for(Payments payment : paymentlist) {
					if(NullUtil.isNotEmpty(payment.getPaymentitems())) {
						for(PaymentItems paymentitem :  payment.getPaymentitems()) {
							if(NullUtil.isNotEmpty(paymentitem.getAmount())) {
								pendingAmount += paymentitem.getAmount();
							}
						}
					}
				}
			}
			Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
			Update update = new Update();
			
			if(NullUtil.isNotEmpty(invoice.getTotalamount()) && invoice.getTotalamount() > 0d) {
				update.set("receivedAmount", pendingAmount);
				update.set("pendingAmount", invoice.getTotalamount()-pendingAmount);
				count++;
			}
			if(count > 0) {
				WriteResult result = mongoTemplate.updateFirst(queryTmp, update, invoiceType);
				totalRowsEffected += result.getN();
			}
		} catch (Exception e) {
			if(NullUtil.isNotEmpty(invoice.getInvoiceno())) {
				System.out.println(invoice.getId()+"-"+invoice.getInvoiceno()+" is not able to update in "+invoiceType.getName());
			}else {
				System.out.println(invoice.getId()+" is not able to update in "+invoiceType.getName());
			}
			e.printStackTrace();
		}
	}
	System.out.println("TotalRows Effceted: "+totalRowsEffected+ ", in "+invoiceType.getName());
}
}

private void updateForgstr1pendingamt1920SumStrs(Class<? extends InvoiceParent> invoiceType,String returntype, String yrcd){
	List<String> allStatusList = Lists.newArrayList();
	allStatusList.add("Partially Paid");
	allStatusList.add("Paid");
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("totalamount");
	Criteria criteria = Criteria.where("yrCd").is(yrcd).and("paymentStatus").in(allStatusList);
	query.addCriteria(criteria);
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
	List<? extends InvoiceParent> invoices = mongoTemplate.find(query, invoiceType);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	int totalRowsEffected = 0;
	for(InvoiceParent invoice : invoices){			
		try {
			int count = 0;
			Double pendingAmount = 0d;
			Query paymentquery = new Query();
			Criteria paymentcriteria = Criteria.where("invoiceid").is(invoice.getId().toString());
			paymentquery.addCriteria(paymentcriteria);
			List<Payments> paymentlist = mongoTemplate.find(paymentquery, Payments.class);
			if(NullUtil.isNotEmpty(paymentlist)) {
				for(Payments payment : paymentlist) {
					if(NullUtil.isNotEmpty(payment.getPaymentitems())) {
						for(PaymentItems paymentitem :  payment.getPaymentitems()) {
							if(NullUtil.isNotEmpty(paymentitem.getAmount())) {
								pendingAmount += paymentitem.getAmount();
							}
						}
					}
				}
			}
			Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
			Update update = new Update();
			
			if(NullUtil.isNotEmpty(invoice.getTotalamount()) && invoice.getTotalamount() > 0d) {
				update.set("receivedAmount", pendingAmount);
				update.set("pendingAmount", invoice.getTotalamount()-pendingAmount);
				count++;
			}
			if(count > 0) {
				WriteResult result = mongoTemplate.updateFirst(queryTmp, update, invoiceType);
				totalRowsEffected += result.getN();
			}
		} catch (Exception e) {
			if(NullUtil.isNotEmpty(invoice.getInvoiceno())) {
				System.out.println(invoice.getId()+"-"+invoice.getInvoiceno()+" is not able to update in "+invoiceType.getName());
			}else {
				System.out.println(invoice.getId()+" is not able to update in "+invoiceType.getName());
			}
			e.printStackTrace();
		}
	}
	System.out.println("TotalRows Effceted: "+totalRowsEffected+ ", in "+invoiceType.getName());
}
}

private void updateForgstr1pendingamt2021SumStrs(Class<? extends InvoiceParent> invoiceType,String returntype, String yrcd){
	List<String> allStatusList = Lists.newArrayList();
	allStatusList.add("Partially Paid");
	allStatusList.add("Paid");
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("totalamount");
	Criteria criteria = Criteria.where("yrCd").is(yrcd).and("paymentStatus").in(allStatusList);
	query.addCriteria(criteria);
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
	List<? extends InvoiceParent> invoices = mongoTemplate.find(query, invoiceType);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	int totalRowsEffected = 0;
	for(InvoiceParent invoice : invoices){			
		try {
			int count = 0;
			Double pendingAmount = 0d;
			Query paymentquery = new Query();
			Criteria paymentcriteria = Criteria.where("invoiceid").is(invoice.getId().toString());
			paymentquery.addCriteria(paymentcriteria);
			List<Payments> paymentlist = mongoTemplate.find(paymentquery, Payments.class);
			if(NullUtil.isNotEmpty(paymentlist)) {
				for(Payments payment : paymentlist) {
					if(NullUtil.isNotEmpty(payment.getPaymentitems())) {
						for(PaymentItems paymentitem :  payment.getPaymentitems()) {
							if(NullUtil.isNotEmpty(paymentitem.getAmount())) {
								pendingAmount += paymentitem.getAmount();
							}
						}
					}
				}
			}
			Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
			Update update = new Update();
			
			if(NullUtil.isNotEmpty(invoice.getTotalamount()) && invoice.getTotalamount() > 0d) {
				update.set("receivedAmount", pendingAmount);
				update.set("pendingAmount", invoice.getTotalamount()-pendingAmount);
				count++;
			}
			if(count > 0) {
				WriteResult result = mongoTemplate.updateFirst(queryTmp, update, invoiceType);
				totalRowsEffected += result.getN();
			}
		} catch (Exception e) {
			if(NullUtil.isNotEmpty(invoice.getInvoiceno())) {
				System.out.println(invoice.getId()+"-"+invoice.getInvoiceno()+" is not able to update in "+invoiceType.getName());
			}else {
				System.out.println(invoice.getId()+" is not able to update in "+invoiceType.getName());
			}
			e.printStackTrace();
		}
	}
	System.out.println("TotalRows Effceted: "+totalRowsEffected+ ", in "+invoiceType.getName());
}
}

private void updateForprpendingamt1718SumStrs(Class<? extends InvoiceParent> invoiceType,String returntype, String yrcd){
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("totalamount");
	query.fields().include("paymentStatus");
	query.fields().include("dateofinvoice");
	query.fields().include("dueDate");
	Criteria criteria = Criteria.where("yrCd").is(yrcd);
	query.addCriteria(criteria);
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
	List<? extends InvoiceParent> invoices = mongoTemplate.find(query, invoiceType);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	int totalRowsEffected = 0;
	for(InvoiceParent invoice : invoices){			
		try {
			int count = 0;
			Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
			Update update = new Update();
			if(NullUtil.isEmpty(invoice.getPaymentStatus())) {
				if(NullUtil.isNotEmpty(invoice.getTotalamount()) && invoice.getTotalamount() > 0d) {
					update.set("receivedAmount", 0d);
					update.set("pendingAmount", invoice.getTotalamount());
					count++;
				}
			}
			if(NullUtil.isEmpty(invoice.getDueDate())) {
				if(NullUtil.isNotEmpty(invoice.getDateofinvoice())) {
					update.set("dueDate", invoice.getDateofinvoice());
					update.set("termDays","0");
					count++;
				}
			}
			
			if(count > 0) {
				WriteResult result = mongoTemplate.updateFirst(queryTmp, update, invoiceType);
				totalRowsEffected += result.getN();
			}
		} catch (Exception e) {
			if(NullUtil.isNotEmpty(invoice.getInvoiceno())) {
				System.out.println(invoice.getId()+"-"+invoice.getInvoiceno()+" is not able to update in "+invoiceType.getName());
			}else {
				System.out.println(invoice.getId()+" is not able to update in "+invoiceType.getName());
			}
			e.printStackTrace();
		}
	}
	System.out.println("TotalRows Effceted: "+totalRowsEffected+ ", in "+invoiceType.getName());
}
}

private void updateForprpendingamt1819SumStrs(Class<? extends InvoiceParent> invoiceType,String returntype, String yrcd){
	List<String> allStatusList = Lists.newArrayList();
	allStatusList.add("Partially Paid");
	allStatusList.add("Paid");
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("totalamount");
	Criteria criteria = Criteria.where("yrCd").is(yrcd).and("paymentStatus").in(allStatusList);
	query.addCriteria(criteria);
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
	List<? extends InvoiceParent> invoices = mongoTemplate.find(query, invoiceType);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	int totalRowsEffected = 0;
	for(InvoiceParent invoice : invoices){			
		try {
			int count = 0;
			Double pendingAmount = 0d;
			Query paymentquery = new Query();
			Criteria paymentcriteria = Criteria.where("invoiceid").is(invoice.getId().toString());
			paymentquery.addCriteria(paymentcriteria);
			List<Payments> paymentlist = mongoTemplate.find(paymentquery, Payments.class);
			if(NullUtil.isNotEmpty(paymentlist)) {
				for(Payments payment : paymentlist) {
					if(NullUtil.isNotEmpty(payment.getPaymentitems())) {
						for(PaymentItems paymentitem :  payment.getPaymentitems()) {
							if(NullUtil.isNotEmpty(paymentitem.getAmount())) {
								pendingAmount += paymentitem.getAmount();
							}
						}
					}
				}
			}
			Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
			Update update = new Update();
			
			if(NullUtil.isNotEmpty(invoice.getTotalamount()) && invoice.getTotalamount() > 0d) {
				update.set("receivedAmount", pendingAmount);
				update.set("pendingAmount", invoice.getTotalamount()-pendingAmount);
				count++;
			}
			if(count > 0) {
				WriteResult result = mongoTemplate.updateFirst(queryTmp, update, invoiceType);
				totalRowsEffected += result.getN();
			}
		} catch (Exception e) {
			if(NullUtil.isNotEmpty(invoice.getInvoiceno())) {
				System.out.println(invoice.getId()+"-"+invoice.getInvoiceno()+" is not able to update in "+invoiceType.getName());
			}else {
				System.out.println(invoice.getId()+" is not able to update in "+invoiceType.getName());
			}
			e.printStackTrace();
		}
	}
	System.out.println("TotalRows Effceted: "+totalRowsEffected+ ", in "+invoiceType.getName());
}
}

private void updateForprpendingamt1920SumStrs(Class<? extends InvoiceParent> invoiceType,String returntype, String yrcd){
	List<String> allStatusList = Lists.newArrayList();
	allStatusList.add("Partially Paid");
	allStatusList.add("Paid");
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("totalamount");
	Criteria criteria = Criteria.where("yrCd").is(yrcd).and("paymentStatus").in(allStatusList);
	query.addCriteria(criteria);
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
	List<? extends InvoiceParent> invoices = mongoTemplate.find(query, invoiceType);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	int totalRowsEffected = 0;
	for(InvoiceParent invoice : invoices){			
		try {
			int count = 0;
			Double pendingAmount = 0d;
			Query paymentquery = new Query();
			Criteria paymentcriteria = Criteria.where("invoiceid").is(invoice.getId().toString());
			paymentquery.addCriteria(paymentcriteria);
			List<Payments> paymentlist = mongoTemplate.find(paymentquery, Payments.class);
			if(NullUtil.isNotEmpty(paymentlist)) {
				for(Payments payment : paymentlist) {
					if(NullUtil.isNotEmpty(payment.getPaymentitems())) {
						for(PaymentItems paymentitem :  payment.getPaymentitems()) {
							if(NullUtil.isNotEmpty(paymentitem.getAmount())) {
								pendingAmount += paymentitem.getAmount();
							}
						}
					}
				}
			}
			Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
			Update update = new Update();
			
			if(NullUtil.isNotEmpty(invoice.getTotalamount()) && invoice.getTotalamount() > 0d) {
				update.set("receivedAmount", pendingAmount);
				update.set("pendingAmount", invoice.getTotalamount()-pendingAmount);
				count++;
			}
			if(count > 0) {
				WriteResult result = mongoTemplate.updateFirst(queryTmp, update, invoiceType);
				totalRowsEffected += result.getN();
			}
		} catch (Exception e) {
			if(NullUtil.isNotEmpty(invoice.getInvoiceno())) {
				System.out.println(invoice.getId()+"-"+invoice.getInvoiceno()+" is not able to update in "+invoiceType.getName());
			}else {
				System.out.println(invoice.getId()+" is not able to update in "+invoiceType.getName());
			}
			e.printStackTrace();
		}
	}
	System.out.println("TotalRows Effceted: "+totalRowsEffected+ ", in "+invoiceType.getName());
}
}

private void updateForprpendingamt2021SumStrs(Class<? extends InvoiceParent> invoiceType,String returntype, String yrcd){
	List<String> allStatusList = Lists.newArrayList();
	allStatusList.add("Partially Paid");
	allStatusList.add("Paid");
	Query query = new Query();
	query.fields().include("_id");
	query.fields().include("totalamount");
	Criteria criteria = Criteria.where("yrCd").is(yrcd).and("paymentStatus").in(allStatusList);
	query.addCriteria(criteria);
	int length = 5000;
	for(int i=0;i< Integer.MAX_VALUE;i++) {
		Pageable pageable = new PageRequest(i, length);
		query.with(pageable);
	List<? extends InvoiceParent> invoices = mongoTemplate.find(query, invoiceType);
	if(NullUtil.isEmpty(invoices)) {
		break;
	}
	int totalRowsEffected = 0;
	for(InvoiceParent invoice : invoices){			
		try {
			int count = 0;
			Double pendingAmount = 0d;
			Query paymentquery = new Query();
			Criteria paymentcriteria = Criteria.where("invoiceid").is(invoice.getId().toString());
			paymentquery.addCriteria(paymentcriteria);
			List<Payments> paymentlist = mongoTemplate.find(paymentquery, Payments.class);
			if(NullUtil.isNotEmpty(paymentlist)) {
				for(Payments payment : paymentlist) {
					if(NullUtil.isNotEmpty(payment.getPaymentitems())) {
						for(PaymentItems paymentitem :  payment.getPaymentitems()) {
							if(NullUtil.isNotEmpty(paymentitem.getAmount())) {
								pendingAmount += paymentitem.getAmount();
							}
						}
					}
				}
			}
			Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
			Update update = new Update();
			
			if(NullUtil.isNotEmpty(invoice.getTotalamount()) && invoice.getTotalamount() > 0d) {
				update.set("receivedAmount", pendingAmount);
				update.set("pendingAmount", invoice.getTotalamount()-pendingAmount);
				count++;
			}
			if(count > 0) {
				WriteResult result = mongoTemplate.updateFirst(queryTmp, update, invoiceType);
				totalRowsEffected += result.getN();
			}
		} catch (Exception e) {
			if(NullUtil.isNotEmpty(invoice.getInvoiceno())) {
				System.out.println(invoice.getId()+"-"+invoice.getInvoiceno()+" is not able to update in "+invoiceType.getName());
			}else {
				System.out.println(invoice.getId()+" is not able to update in "+invoiceType.getName());
			}
			e.printStackTrace();
		}
	}
	System.out.println("TotalRows Effceted: "+totalRowsEffected+ ", in "+invoiceType.getName());
}
}

	public void migrateUsers(){
		
		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("createdDate");
		query.fields().include("updatedDate");
		Criteria criteria = Criteria.where("type").is("partner");
		query.addCriteria(criteria);
		int length = 5000;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			List<User> users = mongoTemplate.find(query, User.class);
			if(NullUtil.isEmpty(users)) {
				break;
			}
			System.out.println(users.size());
			for(User user : users){
				Query queryTmp = Query.query(Criteria.where("_id").is(user.getId()));
				Update update = new Update();
				int month=-1,year=-1;
				
				Date dt = null;
					
				if(NullUtil.isNotEmpty(user.getCreatedDate())) {
					dt = (Date)user.getCreatedDate();
				}else if(NullUtil.isNotEmpty(user.getUpdatedDate())) {
					dt = (Date)user.getUpdatedDate();
				}
					
				if(dt != null){
					month = dt.getMonth();
					year = dt.getYear()+1900;
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(dt);
				}
							
				int quarter = month/3;
				quarter = quarter == 0 ? 4 : quarter;
				String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
				month++;
				update.set("mthCd", ""+month);
				update.set("yrCd", ""+yearCode);
				WriteResult result = mongoTemplate.updateFirst(queryTmp, update, User.class);
				System.out.println(result.getN()+"doc affected ---- "+ user.getId());
			}
		}
	}
	
	public void migrateClients(){
		
		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("customField1");
		query.fields().include("customField2");
		query.fields().include("customField3");
		query.fields().include("customField4");
		
		query.fields().include("ewaycustomField1");
		query.fields().include("ewaycustomField2");
		query.fields().include("ewaycustomField3");
		query.fields().include("ewaycustomField4");
		
		query.fields().include("einvcustomField1");
		query.fields().include("einvcustomField2");
		query.fields().include("einvcustomField3");
		query.fields().include("einvcustomField4");
		int length = 5000;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			List<Client> clients = mongoTemplate.find(query, Client.class);
			if(NullUtil.isEmpty(clients)) {
				break;
			}
			System.out.println(clients.size());
			
			for(Client client : clients){
				List<CustomData> salesList = Lists.newArrayList();
				List<CustomData> purList = Lists.newArrayList();
				List<CustomData> ewaybillList = Lists.newArrayList();
				List<CustomData> einvList = Lists.newArrayList();
				CustomFields fields = new CustomFields();
				fields.setClientid(client.getId().toString());
				if(NullUtil.isNotEmpty(client.getCustomField1())) {
					if(NullUtil.isNotEmpty(client.getCustomField1().getNameInSales()) && NullUtil.isNotEmpty(client.getCustomField1().isDisplayInSales()) && client.getCustomField1().isDisplayInSales()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getCustomField1().getNameInSales());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getCustomField1().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getCustomField1().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						salesList.add(data);
					}
					if(NullUtil.isNotEmpty(client.getCustomField1().getNameInPurchase()) && NullUtil.isNotEmpty(client.getCustomField1().isDisplayInPurchases()) && client.getCustomField1().isDisplayInPurchases()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getCustomField1().getNameInPurchase());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getCustomField1().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getCustomField1().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						purList.add(data);
					}
				}
				if(NullUtil.isNotEmpty(client.getCustomField2())) {
					if(NullUtil.isNotEmpty(client.getCustomField2().getNameInSales()) && NullUtil.isNotEmpty(client.getCustomField2().isDisplayInSales()) && client.getCustomField2().isDisplayInSales()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getCustomField2().getNameInSales());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getCustomField2().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getCustomField2().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						salesList.add(data);
					}
					if(NullUtil.isNotEmpty(client.getCustomField2().getNameInPurchase()) && NullUtil.isNotEmpty(client.getCustomField2().isDisplayInPurchases()) && client.getCustomField2().isDisplayInPurchases()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getCustomField2().getNameInPurchase());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getCustomField2().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getCustomField2().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						purList.add(data);
					}
				}
				
				if(NullUtil.isNotEmpty(client.getCustomField3())) {
					if(NullUtil.isNotEmpty(client.getCustomField3().getNameInSales()) && NullUtil.isNotEmpty(client.getCustomField3().isDisplayInSales()) && client.getCustomField3().isDisplayInSales()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getCustomField3().getNameInSales());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getCustomField3().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getCustomField3().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						salesList.add(data);
					}
					if(NullUtil.isNotEmpty(client.getCustomField3().getNameInPurchase()) && NullUtil.isNotEmpty(client.getCustomField3().isDisplayInPurchases()) && client.getCustomField3().isDisplayInPurchases()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getCustomField3().getNameInPurchase());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getCustomField3().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getCustomField3().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						purList.add(data);
					}
				}
				if(NullUtil.isNotEmpty(client.getCustomField4())) {
					if(NullUtil.isNotEmpty(client.getCustomField4().getNameInSales()) && NullUtil.isNotEmpty(client.getCustomField4().isDisplayInSales()) && client.getCustomField4().isDisplayInSales()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getCustomField4().getNameInSales());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getCustomField4().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getCustomField4().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						salesList.add(data);
					}
					if(NullUtil.isNotEmpty(client.getCustomField4().getNameInPurchase()) && NullUtil.isNotEmpty(client.getCustomField4().isDisplayInPurchases()) && client.getCustomField4().isDisplayInPurchases()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getCustomField4().getNameInPurchase());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getCustomField4().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getCustomField4().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						purList.add(data);
					}
				}
				
				if(NullUtil.isNotEmpty(client.getEwaycustomField1())) {
					if(NullUtil.isNotEmpty(client.getEwaycustomField1().getNameInEwayBill()) && NullUtil.isNotEmpty(client.getEwaycustomField1().isDisplayInEwayBill()) && client.getEwaycustomField1().isDisplayInEwayBill()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getEwaycustomField1().getNameInEwayBill());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getEwaycustomField1().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getEwaycustomField1().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						ewaybillList.add(data);
					}
				}
				if(NullUtil.isNotEmpty(client.getEwaycustomField2())) {
					if(NullUtil.isNotEmpty(client.getEwaycustomField2().getNameInEwayBill()) && NullUtil.isNotEmpty(client.getEwaycustomField2().isDisplayInEwayBill()) && client.getEwaycustomField2().isDisplayInEwayBill()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getEwaycustomField2().getNameInEwayBill());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getEwaycustomField2().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getEwaycustomField2().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						ewaybillList.add(data);
					}
				}
				
				if(NullUtil.isNotEmpty(client.getEwaycustomField3())) {
					if(NullUtil.isNotEmpty(client.getEwaycustomField3().getNameInEwayBill()) && NullUtil.isNotEmpty(client.getEwaycustomField3().isDisplayInEwayBill()) && client.getEwaycustomField3().isDisplayInEwayBill()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getEwaycustomField3().getNameInEwayBill());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getEwaycustomField3().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getEwaycustomField3().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						ewaybillList.add(data);
					}
				}
				if(NullUtil.isNotEmpty(client.getEwaycustomField4())) {
					if(NullUtil.isNotEmpty(client.getEwaycustomField4().getNameInEwayBill()) && NullUtil.isNotEmpty(client.getEwaycustomField4().isDisplayInEwayBill()) && client.getEwaycustomField4().isDisplayInEwayBill()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getEwaycustomField4().getNameInEwayBill());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getEwaycustomField4().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getEwaycustomField4().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						ewaybillList.add(data);
					}
				}
				
				if(NullUtil.isNotEmpty(client.getEinvcustomField1())) {
					if(NullUtil.isNotEmpty(client.getEinvcustomField1().getNameInEinvoice()) && NullUtil.isNotEmpty(client.getEinvcustomField1().isDisplayInEinvoice()) && client.getEinvcustomField1().isDisplayInEinvoice()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getEinvcustomField1().getNameInEinvoice());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getEinvcustomField1().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getEinvcustomField1().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						einvList.add(data);
					}
				}
				if(NullUtil.isNotEmpty(client.getEinvcustomField2())) {
					if(NullUtil.isNotEmpty(client.getEinvcustomField2().getNameInEinvoice()) && NullUtil.isNotEmpty(client.getEinvcustomField2().isDisplayInEinvoice()) && client.getEinvcustomField2().isDisplayInEinvoice()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getEinvcustomField2().getNameInEinvoice());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getEinvcustomField2().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getEinvcustomField2().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						einvList.add(data);
					}
				}
				
				if(NullUtil.isNotEmpty(client.getEinvcustomField3())) {
					if(NullUtil.isNotEmpty(client.getEinvcustomField3().getNameInEinvoice()) && NullUtil.isNotEmpty(client.getEinvcustomField3().isDisplayInEinvoice()) && client.getEinvcustomField3().isDisplayInEinvoice()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getEinvcustomField3().getNameInEinvoice());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getEinvcustomField3().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getEinvcustomField3().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						einvList.add(data);
					}
				}
				if(NullUtil.isNotEmpty(client.getEinvcustomField4())) {
					if(NullUtil.isNotEmpty(client.getEinvcustomField4().getNameInEinvoice()) && NullUtil.isNotEmpty(client.getEinvcustomField4().isDisplayInEinvoice()) && client.getEinvcustomField4().isDisplayInEinvoice()) {
						CustomData data = new CustomData();
						data.setCustomFieldName(client.getEinvcustomField4().getNameInEinvoice());
						data.setCustomFieldType("input");
						if(NullUtil.isNotEmpty(client.getEinvcustomField4().isDisplayInPrint())) {
							data.setDisplayInPrint(client.getEinvcustomField4().isDisplayInPrint());
						}else {
							data.setDisplayInPrint(false);
						}
						data.setIsMandatory(false);
						einvList.add(data);
					}
				}
				if(NullUtil.isNotEmpty(salesList)) {
					fields.setSales(salesList);
				}
				if(NullUtil.isNotEmpty(purList)) {
					fields.setPurchase(purList);
				}
				if(NullUtil.isNotEmpty(einvList)) {
					fields.setEinvoice(einvList);
				}
				if(NullUtil.isNotEmpty(ewaybillList)) {
					fields.setEwaybill(ewaybillList);
				}
				if(NullUtil.isNotEmpty(salesList) || NullUtil.isNotEmpty(purList) || NullUtil.isNotEmpty(einvList) || NullUtil.isNotEmpty(ewaybillList)) {
					customFieldsRepository.save(fields);
				}
				
				System.out.println("doc affected ---- "+ client.getId());
				
			}
		}
	}
	
	public void updateJournalGSTR1(){
		
		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("dateofinvoice");
		query.fields().include("invoiceType");
		query.fields().include("customerorSupplierAccount");
		query.fields().include("salesorPurchases");
		query.fields().include("igstamount");
		query.fields().include("sgstamount");
		query.fields().include("cgstamount");
		query.fields().include("tdsamount");
		query.fields().include("roundOffAmount");
		
		query.addCriteria(new Criteria().where("returnType").is("GSTR1"));
		int length = 5000;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			System.out.println(i);
			System.out.println(length);
		List<AccountingJournal> invoices = mongoTemplate.find(query, AccountingJournal.class);
		if(NullUtil.isEmpty(invoices)) {
			break;
		}
		System.out.println(invoices.size());
		for(AccountingJournal invoice : invoices){
			try {
		Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
		Update update = new Update();
		double totalCredit = 0.0;
		double totalDebit = 0.0;
		
		totalDebit += invoice.getCustomerorSupplierAccount() == null ? 0.0 : invoice.getCustomerorSupplierAccount();
		totalDebit += invoice.getTdsamount() == null ? 0.0 : (invoice.getTdsamount()*2);
		totalDebit += invoice.getRoundOffAmount() == null ? 0.0 : invoice.getRoundOffAmount();
		
		totalCredit += invoice.getSalesorPurchases() == null ? 0.0 : invoice.getSalesorPurchases();
		totalCredit += invoice.getSgstamount() == null ? 0.0 : invoice.getSgstamount();
		totalCredit += invoice.getCgstamount() == null ? 0.0 : invoice.getCgstamount();
		totalCredit += invoice.getIgstamount() == null ? 0.0 : invoice.getIgstamount();
		totalCredit += invoice.getTdsamount() == null ? 0.0 : invoice.getTdsamount();
		totalCredit += invoice.getRoundOffAmount() == null ? 0.0 : invoice.getRoundOffAmount();
		
		int month=-1,year=-1;
		boolean chnged = false;
		
		if(!chnged){
			Date dt = (Date)invoice.getDateofinvoice();
			if(dt != null){
				month = dt.getMonth();
				year = dt.getYear()+1900;
			}
		}
		
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
		month++;
		update.set("mthCd", ""+month);
		update.set("qrtCd", ""+quarter);
		update.set("yrCd", ""+yearCode);
		update.set("creditTotal", Math.abs(totalCredit));
		update.set("debitTotal", Math.abs(totalDebit));
		WriteResult result = mongoTemplate.updateFirst(queryTmp, update, AccountingJournal.class);
		System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
			}catch(Exception e) {
				//System.out.println(e.printStackTrace()+"doc affected ---- "+ invoice.getId());
				e.printStackTrace();
			}
		//break;
		}
	}

	}
	
	public void updateJournalVoucher(){
		
		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("dateofinvoice");
		query.fields().include("totdramount");
		query.fields().include("totcramount");
		List<String> retTypeList = Lists.newArrayList();
		retTypeList.add("Voucher");
		retTypeList.add("Contra");
		query.addCriteria(new Criteria().where("returnType").in(retTypeList));
		int length = 5000;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			System.out.println(i);
			System.out.println(length);
		List<AccountingJournal> invoices = mongoTemplate.find(query, AccountingJournal.class);
		if(NullUtil.isEmpty(invoices)) {
			break;
		}
		System.out.println(invoices.size());
		for(AccountingJournal invoice : invoices){
			try {
		Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
		Update update = new Update();
		double totalCredit = 0.0;
		double totalDebit = 0.0;
		
		totalDebit += invoice.getTotdramount() == null ? 0.0 : invoice.getTotdramount();
		totalCredit += invoice.getTotcramount() == null ? 0.0 : invoice.getTotcramount();
		
		int month=-1,year=-1;
		boolean chnged = false;
		
		if(!chnged){
			Date dt = (Date)invoice.getDateofinvoice();
			if(dt != null){
				month = dt.getMonth();
				year = dt.getYear()+1900;
			}
		}
		
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
		month++;
		update.set("mthCd", ""+month);
		update.set("qrtCd", ""+quarter);
		update.set("yrCd", ""+yearCode);
		update.set("creditTotal", Math.abs(totalCredit));
		update.set("debitTotal", Math.abs(totalDebit));
		WriteResult result = mongoTemplate.updateFirst(queryTmp, update, AccountingJournal.class);
		System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
			}catch(Exception e) {
				//System.out.println(e.printStackTrace()+"doc affected ---- "+ invoice.getId());
				e.printStackTrace();
			}
		//break;
		}
	}

	}
	
	
	public void updateJournalDate(){
		
		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("dateofinvoice");
		
		int length = 5000;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			System.out.println(i);
			System.out.println(length);
		List<AccountingJournal> invoices = mongoTemplate.find(query, AccountingJournal.class);
		if(NullUtil.isEmpty(invoices)) {
			break;
		}
		System.out.println(invoices.size());
		for(AccountingJournal invoice : invoices){
			try {
		Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
		Update update = new Update();

		int month=-1,year=-1;
		boolean chnged = false;
		
		if(!chnged){
			Date dt = (Date)invoice.getDateofinvoice();
			if(dt != null){
				month = dt.getMonth();
				year = dt.getYear()+1900;
			}
		}
		
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
		month++;
		update.set("mthCd", ""+month);
		update.set("qrtCd", ""+quarter);
		update.set("yrCd", ""+yearCode);

		WriteResult result = mongoTemplate.updateFirst(queryTmp, update, AccountingJournal.class);
		System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
			}catch(Exception e) {
				//System.out.println(e.printStackTrace()+"doc affected ---- "+ invoice.getId());
				e.printStackTrace();
			}
		//break;
		}
	}

	}
	
	public void updateJournalPaymentReceipt(){
		
		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("dateofinvoice");
		query.fields().include("paymentReceivedAmount");
		query.fields().include("paymentitems");
		query.addCriteria(new Criteria().where("returnType").is("Payment Receipt"));
		int length = 5000;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			System.out.println(i);
			System.out.println(length);
		List<AccountingJournal> invoices = mongoTemplate.find(query, AccountingJournal.class);
		if(NullUtil.isEmpty(invoices)) {
			break;
		}
		System.out.println(invoices.size());
		for(AccountingJournal invoice : invoices){
			try {
		Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
		Update update = new Update();
		double totalCredit = 0.0;
		double totalDebit = 0.0;
		
		if(NullUtil.isNotEmpty(invoice.getPaymentitems())) {
			for(AccountingJournalPaymentItems items : invoice.getPaymentitems()) {
				totalDebit += items.getAmount() == null ? 0.0 : items.getAmount();
			}
		}
		
		totalCredit += invoice.getPaymentReceivedAmount() == null ? 0.0 : invoice.getPaymentReceivedAmount();
		
		int month=-1,year=-1;
		boolean chnged = false;
		
		if(!chnged){
			Date dt = (Date)invoice.getDateofinvoice();
			if(dt != null){
				month = dt.getMonth();
				year = dt.getYear()+1900;
			}
		}
		
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
		month++;
		update.set("mthCd", ""+month);
		update.set("qrtCd", ""+quarter);
		update.set("yrCd", ""+yearCode);
		update.set("creditTotal", Math.abs(totalCredit));
		update.set("debitTotal", Math.abs(totalDebit));
		WriteResult result = mongoTemplate.updateFirst(queryTmp, update, AccountingJournal.class);
		System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
			}catch(Exception e) {
				//System.out.println(e.printStackTrace()+"doc affected ---- "+ invoice.getId());
				e.printStackTrace();
			}
		//break;
		}
	}

	}
	
	public void updateJournalPayments(){
		
		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("dateofinvoice");
		query.fields().include("paymentReceivedAmount");
		query.fields().include("paymentitems");
		query.addCriteria(new Criteria().where("returnType").is("Payment"));
		int length = 5000;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			System.out.println(i);
			System.out.println(length);
		List<AccountingJournal> invoices = mongoTemplate.find(query, AccountingJournal.class);
		if(NullUtil.isEmpty(invoices)) {
			break;
		}
		System.out.println(invoices.size());
		for(AccountingJournal invoice : invoices){
			try {
		Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
		Update update = new Update();
		double totalCredit = 0.0;
		double totalDebit = 0.0;
		
		if(NullUtil.isNotEmpty(invoice.getPaymentitems())) {
			for(AccountingJournalPaymentItems items : invoice.getPaymentitems()) {
				totalCredit += items.getAmount() == null ? 0.0 : items.getAmount();
			}
		}
		
		totalDebit += invoice.getPaymentReceivedAmount() == null ? 0.0 : invoice.getPaymentReceivedAmount();
		
		int month=-1,year=-1;
		boolean chnged = false;
		
		if(!chnged){
			Date dt = (Date)invoice.getDateofinvoice();
			if(dt != null){
				month = dt.getMonth();
				year = dt.getYear()+1900;
			}
		}
		
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
		month++;
		update.set("mthCd", ""+month);
		update.set("qrtCd", ""+quarter);
		update.set("yrCd", ""+yearCode);
		update.set("creditTotal", Math.abs(totalCredit));
		update.set("debitTotal", Math.abs(totalDebit));
		WriteResult result = mongoTemplate.updateFirst(queryTmp, update, AccountingJournal.class);
		System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
			}catch(Exception e) {
				//System.out.println(e.printStackTrace()+"doc affected ---- "+ invoice.getId());
				e.printStackTrace();
			}
		//break;
		}
	}

	}
	
	
public void updateGstr1AdvAdjTaxAmounts(){
		
		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("items");
		query.fields().include("totaltaxableamount");
		query.fields().include("totaltax");
		query.fields().include("dateofinvoice");
		query.fields().include("fp");
		query.fields().include("invtype");
		query.fields().include("totalamount");
		query.fields().include("cdn");
		query.fields().include("cdnr");
		query.fields().include("cdnur");
		query.fields().include("gstStatus");
		query.addCriteria(new Criteria().where("invtype").is("Advance Adjusted Detail"));
		int length = 5000;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			System.out.println(i);
			System.out.println(length);
		List<GSTR1> invoices = mongoTemplate.find(query, GSTR1.class);
		if(NullUtil.isEmpty(invoices)) {
			break;
		}
		System.out.println(invoices.size());
		for(GSTR1 invoice : invoices){
			try {
				List<Item> items = invoice.getItems();
				double totaladvAdjAmount = 0.0;
				for(Item item : items){
					totaladvAdjAmount += item.getAdvadjustedAmount() == null ? 0.0 : item.getAdvadjustedAmount();
				}
				Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
				Update update = new Update();
				update.set("totaltaxableamount", totaladvAdjAmount);
				int sumFactor = -1;
				update.set("sftr", sumFactor);
				update.set("csftr", 1);
		
				WriteResult result = mongoTemplate.updateFirst(queryTmp, update, GSTR1.class);
				System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	}
	
}
