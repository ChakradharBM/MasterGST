package com.mastergst.usermanagement.runtime.accounting.util;

import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.ADVANCES;
import static com.mastergst.core.common.MasterGSTConstants.ATA;
import static com.mastergst.core.common.MasterGSTConstants.ATPAID;
import static com.mastergst.core.common.MasterGSTConstants.B2B;
import static com.mastergst.core.common.MasterGSTConstants.B2BUR;
import static com.mastergst.core.common.MasterGSTConstants.B2BA;
import static com.mastergst.core.common.MasterGSTConstants.B2C;
import static com.mastergst.core.common.MasterGSTConstants.B2CL;
import static com.mastergst.core.common.MasterGSTConstants.B2CLA;
import static com.mastergst.core.common.MasterGSTConstants.B2CSA;
import static com.mastergst.core.common.MasterGSTConstants.CREDIT_DEBIT_NOTES;
import static com.mastergst.core.common.MasterGSTConstants.CDNA;
import static com.mastergst.core.common.MasterGSTConstants.CDNUR;
import static com.mastergst.core.common.MasterGSTConstants.CDNURA;
import static com.mastergst.core.common.MasterGSTConstants.EXPA;
import static com.mastergst.core.common.MasterGSTConstants.EXPORTS;
import static com.mastergst.core.common.MasterGSTConstants.NIL;
import static com.mastergst.core.common.MasterGSTConstants.TXPA;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.mastergst.core.common.AccountConstants;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.accounting.domain.JournalEntrie;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.repository.AccountingJournalRepository;
import com.mastergst.usermanagement.runtime.repository.LedgerRepository;

@Component
public class AccountingJournalsUtils {
	@Autowired	AccountingJournalRepository accountingJournalRepository;
	@Autowired	LedgerRepository ledgerRepository;
	private static final Logger log = LogManager.getLogger(AccountingJournalsUtils.class.getName());
	public void createJournalsEntries(String userid, String clientid, String returntype, InvoiceParent invoice, boolean isIntraState) {
		
		if ("Sales Register".equalsIgnoreCase(returntype) || "SalesRegister".equalsIgnoreCase(returntype) || GSTR1.equalsIgnoreCase(returntype)) {
			salesJournalsEntries(userid, clientid, returntype, invoice, isIntraState);
		}else {
			purchaseJournalsEntries(userid, clientid, returntype, invoice, isIntraState);
			//purchase register journals method
		}
		
	}

	/* invTyp
		R-Regular
		DE-Deemed Exports
		SEWP-Supplies to SEZ with Payment
		SEWPC-Supplies to SEZ with payment(Tax collected from customer)
		SEWOP-Supplies to SEZ without Payment
	 */
	
	public void salesJournalsEntries(String userid, String clientid, String returntype, InvoiceParent invoice, boolean isIntraState) {
		AccountingJournal journal = addDefaultInfo(returntype, invoice, clientid,isIntraState);
		Double totalDebitAmt = 0d, totalCreditAmt = 0d;
		List<JournalEntrie> drEntries = Lists.newArrayList();
		List<JournalEntrie> crEntries = Lists.newArrayList();
				
		if(isNotEmpty(invoice) && isNotEmpty(invoice.getInvtype())){
			boolean tcspayableflag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
			if(tcspayableflag) {
				journal.setTcs_payable(invoice.getTcstdsAmount());
			}
			
			boolean isRevcharge = invoice.getRevchargetype() != null ? invoice.getRevchargetype().equalsIgnoreCase("Yes") || invoice.getRevchargetype().equalsIgnoreCase("Reverse") ?  true : false  : false;
			String vendorName = invoice.getVendorName() != null ? invoice.getVendorName() != "" ? invoice.getVendorName() : AccountConstants.OTHER_DEBTORS : AccountConstants.OTHER_DEBTORS;
			boolean roundoffAmt = invoice.getRoundOffAmount() != null ? invoice.getRoundOffAmount() != 0 ? true : false : false;
			if(invoice.getInvtype().equals(B2B) || invoice.getInvtype().equals(B2BA)) {
				String invTyp = (invoice.getB2b() != null && invoice.getB2b().get(0) != null && invoice.getB2b().get(0).getInv()!= null && invoice.getB2b().get(0).getInv().get(0)!= null && invoice.getB2b().get(0).getInv().get(0).getInvTyp()!= null) ? invoice.getB2b().get(0).getInv().get(0).getInvTyp() : "R";
				boolean igstFlag = invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() > 0 ? true : false : false;
				boolean cgstFlag = invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() > 0 ? true : false : false;
				boolean sgstFlag = invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() > 0 ? true : false : false;
				boolean cessFlag = invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean exemptFlag = invoice.getTotalExemptedAmount() != null ?  invoice.getTotalExemptedAmount() > 0 ? true : false : false;
				
				
				double totalAmt = 0d;
				if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
					int i=1;
					for(Item item : journal.getItems()) {
						String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.SALES :  AccountConstants.SALES;
						if(roundoffAmt && i == 1) {
							crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue() + invoice.getRoundOffAmount()));
							totalAmt += item.getTotal()+invoice.getRoundOffAmount();
						}else {
							crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
							totalAmt += item.getTotal();
						}
						i++;
					}
				}
				/*if(isNotEmpty(invoice.getRoundOffAmount())) {
					totalAmt += invoice.getRoundOffAmount(); 
				}*/
				totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
				if("SEWPC".equals(invTyp) || "SEWP".equals(invTyp)) {
					if(!isRevcharge && igstFlag) {
						totalAmt -= invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0;
					}
				}
				drEntries.add(new JournalEntrie(vendorName, totalAmt));
				if("R".equals(invTyp) || "DE".equals(invTyp)) {
					if(!isIntraState && !isRevcharge && igstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(isIntraState && !isRevcharge && cgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
					}
					if(isIntraState && !isRevcharge && sgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
					}
					if(!isRevcharge && cessFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
					}
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}
				}else if("SEWP".equals(invTyp)){
					if(!isRevcharge && igstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.IGST_REFUND_RECEIVABLE, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}	
				}else if("SEWPC".equals(invTyp)) {
					if(!isRevcharge && igstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.IGST_REFUND_RECEIVABLE, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}
				}else if("SEWOP".equals(invTyp)){
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}	
				}
				if(exemptFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.SALES_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
				}
			}else if(invoice.getInvtype().equals(B2C) || invoice.getInvtype().equals(B2CSA)) {
				String invTyp = (invoice.getB2b() != null && invoice.getB2b().get(0) != null && invoice.getB2b().get(0).getInv()!= null && invoice.getB2b().get(0).getInv().get(0)!= null && invoice.getB2b().get(0).getInv().get(0).getInvTyp()!= null) ?
						invoice.getB2b().get(0).getInv().get(0).getInvTyp() : "R";
				
				boolean igstFlag = invoice.getTotalIgstAmount() != null ?  invoice.getTotalIgstAmount() > 0 ? true : false : false;
				boolean cgstFlag = invoice.getTotalCgstAmount() != null ?  invoice.getTotalCgstAmount() > 0 ? true : false : false;
				boolean sgstFlag = invoice.getTotalSgstAmount() != null ?  invoice.getTotalSgstAmount() > 0 ? true : false : false;
				boolean cessFlag = invoice.getTotalCessAmount() != null ?  invoice.getTotalCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean exemptFlag = invoice.getTotalExemptedAmount() != null ?  invoice.getTotalExemptedAmount() > 0 ? true : false : false;
				
				double totalAmt = 0d;
				if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
					int i =1;
					for(Item item : journal.getItems()) {
						String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.SALES :  AccountConstants.SALES;
						if(roundoffAmt && i == 1) {
							crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
							totalAmt += item.getTotal()+invoice.getRoundOffAmount();
						}else {
							crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
							totalAmt += item.getTotal();
						}
						i++;
					}
				}
				/*if(isNotEmpty(invoice.getRoundOffAmount())) {
					totalAmt += invoice.getRoundOffAmount(); 
				}*/
				totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
				if("SEWPC".equals(invTyp) || "SEWP".equals(invTyp)) {
					if(!isRevcharge && igstFlag) {
						totalAmt -= invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0;
					}
				}
				drEntries.add(new JournalEntrie(vendorName, totalAmt));
				if("R".equals(invTyp) || "DE".equals(invTyp)) {
					if(!isIntraState && !isRevcharge && igstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(isIntraState && !isRevcharge && cgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
					}
					if(isIntraState && !isRevcharge && sgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
					}
					if(!isRevcharge && cessFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
					}
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}
				}else if("SEWP".equals(invTyp)){
					if(!isRevcharge && igstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.IGST_REFUND_RECEIVABLE, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}	
				}else if("SEWPC".equals(invTyp)) {
					if(!isRevcharge && igstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.IGST_REFUND_RECEIVABLE, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}
				}else if("SEWOP".equals(invTyp)){
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}	
				}
				if(exemptFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.SALES_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
				}
			}else if(invoice.getInvtype().equals(B2CL) || invoice.getInvtype().equals(B2CLA)) {
				String invTyp = (invoice.getB2b() != null && invoice.getB2b().get(0) != null && invoice.getB2b().get(0).getInv()!= null && invoice.getB2b().get(0).getInv().get(0)!= null && invoice.getB2b().get(0).getInv().get(0).getInvTyp()!= null) ?
						invoice.getB2b().get(0).getInv().get(0).getInvTyp() : "R";
				boolean igstFlag = invoice.getTotalIgstAmount() != null ?  invoice.getTotalIgstAmount() > 0 ? true : false : false;
				boolean cgstFlag = invoice.getTotalCgstAmount() != null ?  invoice.getTotalCgstAmount() > 0 ? true : false : false;
				boolean sgstFlag = invoice.getTotalSgstAmount() != null ?  invoice.getTotalSgstAmount() > 0 ? true : false : false;
				boolean cessFlag = invoice.getTotalCessAmount() != null ?  invoice.getTotalCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean exemptFlag = invoice.getTotalExemptedAmount() != null ?  invoice.getTotalExemptedAmount() > 0 ? true : false : false;
				
				double totalAmt = 0d;
				if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
					int i = 1;
					for(Item item : journal.getItems()) {
						String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.SALES :  AccountConstants.SALES;
						if(roundoffAmt && i == 1) {
							crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
							totalAmt += item.getTotal()+invoice.getRoundOffAmount(); 
						}else {
							crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
							totalAmt += item.getTotal();
						}
						i++;
					}
				}
				/*if(isNotEmpty(invoice.getRoundOffAmount())) {
					totalAmt += invoice.getRoundOffAmount(); 
				}*/
				totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
				if("SEWPC".equals(invTyp) || "SEWP".equals(invTyp)) {
					if(!isRevcharge && igstFlag) {
						if (isNotEmpty(invoice.getTotalIgstAmount())) {
							totalAmt -= invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0;
						}
					}
				}
				drEntries.add(new JournalEntrie(vendorName, totalAmt));
				if("R".equals(invTyp) || "DE".equals(invTyp)) {
					if(!isIntraState && !isRevcharge && igstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(isIntraState && !isRevcharge && cgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
					}
					if(isIntraState && !isRevcharge && sgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
					}
					if(!isRevcharge && cessFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
					}
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}
				}else if("SEWP".equals(invTyp)){
					if(!isRevcharge && igstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.IGST_REFUND_RECEIVABLE, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}	
				}else if("SEWPC".equals(invTyp)) {
					if(!isRevcharge && igstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.IGST_REFUND_RECEIVABLE, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}
				}else if("SEWOP".equals(invTyp)){
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}	
				}
				if(exemptFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.SALES_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
				}
			}else if(invoice.getInvtype().equals(CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(CDNA)) {
				String invTyp = (invoice.getB2b() != null && invoice.getB2b().get(0) != null && invoice.getB2b().get(0).getInv()!= null && invoice.getB2b().get(0).getInv().get(0)!= null && invoice.getB2b().get(0).getInv().get(0).getInvTyp()!= null) ?
						invoice.getB2b().get(0).getInv().get(0).getInvTyp() : "R";
				boolean igstFlag = invoice.getTotalIgstAmount() != null ?  invoice.getTotalIgstAmount() > 0 ? true : false : false;
				boolean cgstFlag = invoice.getTotalCgstAmount() != null ?  invoice.getTotalCgstAmount() > 0 ? true : false : false;
				boolean sgstFlag = invoice.getTotalSgstAmount() != null ?  invoice.getTotalSgstAmount() > 0 ? true : false : false;
				boolean cessFlag = invoice.getTotalCessAmount() != null ?  invoice.getTotalCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean exemptFlag = invoice.getTotalExemptedAmount() != null ?  invoice.getTotalExemptedAmount() > 0 ? true : false : false;
				
				boolean isDebitNote = (((GSTR1)invoice).getCdnr() != null && ((GSTR1)invoice).getCdnr().get(0) != null && ((GSTR1)invoice).getCdnr().get(0).getNt() != null && ((GSTR1)invoice).getCdnr().get(0).getNt().get(0) != null && ((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty() != null) ? ((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty().equals("D") ? true : false : false;
				if(invoice.getInvtype().equals(CDNA)) {
					isDebitNote = (((GSTR1)invoice).getCdnra() != null && ((GSTR1)invoice).getCdnra().get(0) != null && ((GSTR1)invoice).getCdnra().get(0).getNt() != null && ((GSTR1)invoice).getCdnra().get(0).getNt().get(0) != null && ((GSTR1)invoice).getCdnra().get(0).getNt().get(0).getNtty() != null) ? ((GSTR1)invoice).getCdnra().get(0).getNt().get(0).getNtty().equals("D") ? true : false : false;
				}
				if(isDebitNote) {
					double totalAmt = 0d;
					if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
						int i = 1;
						for(Item item : journal.getItems()) {
							String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.SALES :  AccountConstants.SALES;
							if(roundoffAmt && i == 1) {
								crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
								totalAmt += item.getTotal()+invoice.getRoundOffAmount();
							}else {
								crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
								totalAmt += item.getTotal();
							}
							i++;
						}
					}
					/*if(isNotEmpty(invoice.getRoundOffAmount())) {
						totalAmt += invoice.getRoundOffAmount(); 
					}*/
					totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
					if("SEWPC".equals(invTyp) || "SEWP".equals(invTyp)) {
						if(!isRevcharge && igstFlag) {
							if (isNotEmpty(invoice.getTotalIgstAmount())) {
								totalAmt -= invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0;
							}
						}
					}
					drEntries.add(new JournalEntrie(vendorName, totalAmt));
					if("R".equals(invTyp) || "DE".equals(invTyp)) {
						if(!isIntraState && !isRevcharge && igstFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						}
						if(isIntraState && !isRevcharge && cgstFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
						}
						if(isIntraState && !isRevcharge && sgstFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
						}
						if(!isRevcharge && cessFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
						}
						if(tcsFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}else if("SEWP".equals(invTyp)) {
						if(!isRevcharge && igstFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.IGST_REFUND_RECEIVABLE, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						}
						if(tcsFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}else if("SEWPC".equals(invTyp)) {
						if(!isRevcharge && igstFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.IGST_REFUND_RECEIVABLE, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						}
						if(tcsFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}else if("SEWOP".equals(invTyp)) {
						if(tcsFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}
					if(exemptFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.SALES_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
					}
				}else {
					
					double totalAmt = 0d;
					if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
						int i = 1;
						for(Item item : journal.getItems()) {
							String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.SALES :  AccountConstants.SALES;
							if(roundoffAmt && i == 1) {
								drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
								totalAmt += item.getTotal()+invoice.getRoundOffAmount();
							}else {
								drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
								totalAmt += item.getTotal();
							}
							i++;
						}
					}
					/*if(isNotEmpty(invoice.getRoundOffAmount())) {
						totalAmt += invoice.getRoundOffAmount(); 
					}*/
					totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
					if("SEWPC".equals(invTyp) || "SEWP".equals(invTyp)) {
						if(!isRevcharge && igstFlag) {
							totalAmt -= invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0;
						}
					}
					crEntries.add(new JournalEntrie(vendorName, totalAmt));
					if("R".equals(invTyp) || "DE".equals(invTyp)) {
						if(!isIntraState && !isRevcharge && igstFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						}
						if(isIntraState && !isRevcharge && cgstFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
						}
						if(isIntraState && !isRevcharge && sgstFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
						}
						if(!isRevcharge && cessFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
						}
						if(tcsFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}else if("SEWP".equals(invTyp)) {
						if(!isRevcharge && igstFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.IGST_REFUND_RECEIVABLE, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						}
						if(tcsFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}else if("SEWPC".equals(invTyp)) {
						if(!isRevcharge && igstFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.IGST_REFUND_RECEIVABLE, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						}
						if(tcsFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}else if("SEWOP".equals(invTyp)) {
						if(tcsFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}
					if(exemptFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.SALES_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
					}
				}
				
			}else if(invoice.getInvtype().equals(CDNUR) || invoice.getInvtype().equals(CDNURA)) {
				String invTyp = (invoice.getCdnur() != null && invoice.getCdnur().get(0) != null && invoice.getCdnur().get(0).getTyp() != null) ?
						invoice.getCdnur().get(0).getTyp() : "EXPWP";
				boolean igstFlag = invoice.getTotalIgstAmount() != null ?  invoice.getTotalIgstAmount() > 0 ? true : false : false;
				boolean cgstFlag = invoice.getTotalCgstAmount() != null ?  invoice.getTotalCgstAmount() > 0 ? true : false : false;
				boolean sgstFlag = invoice.getTotalSgstAmount() != null ?  invoice.getTotalSgstAmount() > 0 ? true : false : false;
				boolean cessFlag = invoice.getTotalCessAmount() != null ?  invoice.getTotalCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean exemptFlag = invoice.getTotalExemptedAmount() != null ?  invoice.getTotalExemptedAmount() > 0 ? true : false : false;
				
				boolean isDebitNote = (invoice.getCdnur() != null && invoice.getCdnur().get(0) != null && invoice.getCdnur().get(0).getNtty() != null) ? invoice.getCdnur().get(0).getNtty().equals("D") ? true : false : false;
				if(invoice.getInvtype().equals(CDNURA)) {
					isDebitNote = (((GSTR1)invoice).getCdnura() != null && ((GSTR1)invoice).getCdnura().get(0) != null && ((GSTR1)invoice).getCdnura().get(0).getNtty() != null) ? ((GSTR1)invoice).getCdnura().get(0).getNtty().equals("D") ? true : false : false;
				}
				
				if(isDebitNote) {
					double totalAmt = 0d;
					if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
						int i = 1;
						for(Item item : journal.getItems()) {
							String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.SALES :  AccountConstants.SALES;
							if(roundoffAmt && i == 1) {
								crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
								totalAmt += item.getTotal()+invoice.getRoundOffAmount();
							}else {
								crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
								totalAmt += item.getTotal();
							}
							i++;
						}
					}
					/*if(isNotEmpty(invoice.getRoundOffAmount())) {
						totalAmt += invoice.getRoundOffAmount(); 
					}*/
					totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
					if("EXPWP".equals(invTyp)) {
						if(!isRevcharge && igstFlag) {
							totalAmt -= invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0;
						}
					}
					drEntries.add(new JournalEntrie(vendorName, totalAmt));
					
					if("EXPWP".equals(invTyp)) {
						
						if(igstFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
							drEntries.add(new JournalEntrie(AccountConstants.IGST_REFUND_RECEIVABLE, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						}
						if(tcsFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}else if("EXPWOP".equals(invTyp)) {
						//IGST apply
						if(tcsFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}else if("B2CL".equals(invTyp)) {
						if(!isIntraState && !isRevcharge && igstFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						}
						if(isIntraState && !isRevcharge && cgstFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
						}
						if(isIntraState && !isRevcharge && sgstFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
						}
						if(!isRevcharge && cessFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
						}
						if(tcsFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}else if("B2CS".equals(invTyp)) {
						if(!isIntraState && !isRevcharge && igstFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						}
						if(isIntraState && !isRevcharge && cgstFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
						}
						if(isIntraState && !isRevcharge && sgstFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
						}
						if(!isRevcharge && cessFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
						}
						if(tcsFlag) {
							crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}
					if(exemptFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.SALES_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
					}
				}else {
					double totalAmt = 0d;
					if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
						int i = 1;
						for(Item item : journal.getItems()) {
							String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.SALES :  AccountConstants.SALES;
							if(roundoffAmt && i == 1) {
								drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
								totalAmt += item.getTotal()+invoice.getRoundOffAmount();
							}else {
								drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
								totalAmt += item.getTotal();
							}
							i++;
						}
					}
					/*if(isNotEmpty(invoice.getRoundOffAmount())) {
						totalAmt += invoice.getRoundOffAmount(); 
					}*/
					totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
					if("EXPWP".equals(invTyp)) {
						if(!isRevcharge && igstFlag) {
							totalAmt -= invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0;
						}
					}
					crEntries.add(new JournalEntrie(vendorName, totalAmt));
					
					if("EXPWP".equals(invTyp)) {
						
						if(igstFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
							crEntries.add(new JournalEntrie(AccountConstants.IGST_REFUND_RECEIVABLE, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						}
						if(tcsFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}else if("EXPWOP".equals(invTyp)) {
						//no TCS and no IGST apply
						if(tcsFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}else if("B2CL".equals(invTyp)) {
						if(!isIntraState && !isRevcharge && igstFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						}
						if(isIntraState && !isRevcharge && cgstFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
						}
						if(isIntraState && !isRevcharge && sgstFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
						}
						if(!isRevcharge && cessFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
						}
						if(tcsFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}else if("B2CS".equals(invTyp)) {
						if(!isIntraState && !isRevcharge && igstFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
						}
						if(isIntraState && !isRevcharge && cgstFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
						}
						if(isIntraState && !isRevcharge && sgstFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
						}
						if(!isRevcharge && cessFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
						}
						if(tcsFlag) {
							drEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
						}
					}
					if(exemptFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.SALES_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
					}
				}
			}else if(invoice.getInvtype().equals(EXPORTS) || invoice.getInvtype().equals(EXPA)) {
				String expTyp = invoice.getExp() != null && invoice.getExp().get(0)!=null && invoice.getExp().get(0).getInv()!= null && invoice.getExp().get(0).getExpTyp()!= null ? invoice.getExp().get(0).getExpTyp() : "WPAY";
				boolean exemptFlag = invoice.getTotalExemptedAmount() != null ?  invoice.getTotalExemptedAmount() > 0 ? true : false : false;
				if("WPAY".equalsIgnoreCase(expTyp)) {
					
					double totalAmt = 0d;
					double totalIAmt = 0d;
					if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
						int i = 1;
						for(Item item : journal.getItems()) {
							String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.SALES :  AccountConstants.SALES;
							if(roundoffAmt && i == 1) {
								crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
								totalAmt += item.getTotal()+invoice.getRoundOffAmount();
							}else {
								crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
								totalAmt += item.getTotal();
							}
							totalIAmt += item.getIgstamount();
							i++;
						}
					}
					/*if(isNotEmpty(invoice.getRoundOffAmount())) {
						totalAmt += invoice.getRoundOffAmount(); 
					}*/
					totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
					drEntries.add(new JournalEntrie(vendorName, totalAmt - totalIAmt));
					drEntries.add(new JournalEntrie(AccountConstants.IGST_REFUND_RECEIVABLE, totalIAmt));
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					boolean tcsflag = invoice.getTcstdsAmount() == null ? false : invoice.getTcstdsAmount() > 0 ? true : false;
					boolean cessflag = invoice.getTotalCessAmount() == null ? false : invoice.getTotalCessAmount() > 0 ? true : false;
					if(cessflag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(tcsflag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}
					if(exemptFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.SALES_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
					}
				}else {
					double totalAmt = 0d;
					if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
						for(Item item : journal.getItems()) {
							String ledgerName = journal.getLedgerName() !=null ? journal.getLedgerName() != "" ? journal.getLedgerName() : AccountConstants.SALES :  AccountConstants.SALES;
							crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
							totalAmt += item.getTotal(); 
						}
					}
					if(isNotEmpty(invoice.getRoundOffAmount())) {
						totalAmt += invoice.getRoundOffAmount(); 
					}
					if (isNotEmpty(invoice.getTcstdsAmount())) {
						totalAmt += invoice.getTcstdsAmount();
					}
					drEntries.add(new JournalEntrie(vendorName, totalAmt));
					
					if(exemptFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.SALES_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
					}
				}
			}else if(invoice.getInvtype().equals(NIL)) {
				double totalAmt = 0d;
				if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
					int i = 1;
					for(Item item : journal.getItems()) {
						String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.SALES :  AccountConstants.SALES;
						if(roundoffAmt && i == 1) {
							crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
							totalAmt += item.getTotal()+invoice.getRoundOffAmount();
						}else {
							crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
							totalAmt += item.getTotal();
						}
						i++;
					}
				}
				/*if(isNotEmpty(invoice.getRoundOffAmount())) {
					totalAmt += invoice.getRoundOffAmount(); 
				}*/
				totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
				drEntries.add(new JournalEntrie(vendorName, totalAmt));
				boolean tcsflag = invoice.getTcstdsAmount() == null ? false : invoice.getTcstdsAmount() > 0 ? true : false;
				if(tcsflag) {
					crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
				}
			}else if(invoice.getInvtype().equals(ADVANCES) || invoice.getInvtype().equals(ATA)) {
				boolean igstFlag = invoice.getTotalIgstAmount() != null ?  invoice.getTotalIgstAmount() > 0 ? true : false : false;
				boolean cgstFlag = invoice.getTotalCgstAmount() != null ?  invoice.getTotalCgstAmount() > 0 ? true : false : false;
				boolean sgstFlag = invoice.getTotalSgstAmount() != null ?  invoice.getTotalSgstAmount() > 0 ? true : false : false;
				boolean cessFlag = invoice.getTotalCessAmount() != null ?  invoice.getTotalCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				double totalAmt = 0d;
				double totalIAmt = 0d;
				if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
					int i=1;
					for(Item item : journal.getItems()) {
						String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.SALES :  AccountConstants.SALES;
						if(i == 1) {
							double itemTotal = item.getTotal() != null ? item.getTotal() : 0;
							itemTotal += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
							itemTotal += invoice.getRoundOffAmount() != null ? invoice.getRoundOffAmount() : 0;
							drEntries.add(new JournalEntrie(ledgerName, itemTotal));
						}else {
							drEntries.add(new JournalEntrie(ledgerName, item.getTotal()));
						}
						totalAmt += item.getTotal();
						i++;
					}
				}
				//totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
				
				crEntries.add(new JournalEntrie(vendorName, totalAmt));
				if(!isIntraState && !isRevcharge && igstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					totalIAmt += invoice.getTotalIgstAmount();
				}
				if(isIntraState && !isRevcharge && cgstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
					totalIAmt += invoice.getTotalCgstAmount();
				}
				if(isIntraState && !isRevcharge && sgstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
					totalIAmt += invoice.getTotalSgstAmount();
				}
				if(!isRevcharge && cessFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
					totalIAmt += invoice.getTotalCessAmount();
				}
				if(totalIAmt > 0) {
					drEntries.add(new JournalEntrie(AccountConstants.TAX_ON_ADVANCE, totalIAmt));
				}
				if(tcsFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
				}
			}else if(invoice.getInvtype().equals(ATPAID) || invoice.getInvtype().equals(TXPA)) {
				boolean igstFlag = invoice.getTotalIgstAmount() != null ?  invoice.getTotalIgstAmount() > 0 ? true : false : false;
				boolean cgstFlag = invoice.getTotalCgstAmount() != null ?  invoice.getTotalCgstAmount() > 0 ? true : false : false;
				boolean sgstFlag = invoice.getTotalSgstAmount() != null ?  invoice.getTotalSgstAmount() > 0 ? true : false : false;
				boolean cessFlag = invoice.getTotalCessAmount() != null ?  invoice.getTotalCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				double totalIAmt = 0d;
				
				if(igstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					totalIAmt += invoice.getTotalIgstAmount();
				}
				if(cgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
					totalIAmt += invoice.getTotalCgstAmount();
				}
				if(sgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
					totalIAmt += invoice.getTotalSgstAmount();
				}
				if(cessFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
					totalIAmt += invoice.getTotalCessAmount();
				}
				totalIAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
				if(totalIAmt > 0) {
					crEntries.add(new JournalEntrie(AccountConstants.TAX_ON_ADVANCE, totalIAmt));
				}
				if(tcsFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.TCS_PAYABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
				}
			}
		}
		double totalCredit = 0.0;
		double totalDebit = 0.0;
		for(JournalEntrie creditentrie : crEntries) {
			totalCredit += creditentrie.getValue() == null ? 0.0 : creditentrie.getValue();
		}
		for(JournalEntrie debitentrie : drEntries) {
			totalDebit += debitentrie.getValue() == null ? 0.0 : debitentrie.getValue();
		}
		journal.setCreditTotal(Math.abs(totalCredit));
		journal.setDebitTotal(Math.abs(totalDebit));
		journal.setDrEntrie(drEntries);
		journal.setCrEntrie(crEntries);
		accountingJournalRepository.save(journal);
	}

	public void purchaseJournalsEntries(String userid, String clientid, String returntype, InvoiceParent invoice,boolean isIntraState) {
		AccountingJournal journal = addDefaultInfo(returntype, invoice, clientid,isIntraState);
		List<JournalEntrie> drEntries = Lists.newArrayList();
		List<JournalEntrie> crEntries = Lists.newArrayList();
		if(isNotEmpty(invoice) && isNotEmpty(invoice.getInvtype())){
			boolean isRevcharge = invoice.getRevchargetype() != null ? invoice.getRevchargetype().equalsIgnoreCase("Yes") || invoice.getRevchargetype().equalsIgnoreCase("Reverse") ?  true : false  : false;
			String vendorName = invoice.getVendorName() != null ? invoice.getVendorName() != "" ? invoice.getVendorName() : AccountConstants.OTHER_CREDITORS : AccountConstants.OTHER_CREDITORS;
			boolean isItcElg = journal.getItcinelg() != null ? journal.getItcinelg().equalsIgnoreCase("true") ?  true : false  : false;
			boolean roundoffFlag = invoice.getRoundOffAmount() != null ? invoice.getRoundOffAmount() != 0 ? true : false : false;
			boolean tdspayableflag = invoice.getTdsAmount() != null ?  invoice.getTdsAmount() > 0 ? true : false : false;
			if(tdspayableflag) {
				journal.setTds_payable(invoice.getTdsAmount());
			}
			if(invoice.getInvtype().equals(B2B) || invoice.getInvtype().equals(B2BUR)) {
				boolean igstFlag = journal.getIgstamount() != null ? journal.getIgstamount() > 0 ? true : false : false;
				boolean cgstFlag = journal.getCgstamount() != null ? journal.getCgstamount() > 0 ? true : false : false;
				boolean sgstFlag = journal.getSgstamount() != null ? journal.getSgstamount() > 0 ? true : false : false;
				boolean cessFlag = journal.getCessAmount() != null ? journal.getCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean tdsFlag = invoice.getTdsAmount() != null ?  invoice.getTdsAmount() > 0 ? true : false : false;
				boolean exemptFlag = invoice.getTotalExemptedAmount() != null ?  invoice.getTotalExemptedAmount() > 0 ? true : false : false;
				double totalAmt = 0d;
				if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
					int i = 1;
					for(Item item : journal.getItems()) {
						String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.PURCHASES :  AccountConstants.PURCHASES;
						if(roundoffFlag && i == 1) {
							drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
							totalAmt += item.getTotal()+invoice.getRoundOffAmount();
						}else {
							drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
							totalAmt += item.getTotal();
						}
						i++;
					}
				}
				/*if(isNotEmpty(invoice.getRoundOffAmount())) {
					totalAmt += invoice.getRoundOffAmount(); 
				}*/
				totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
				totalAmt -= invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0;
				crEntries.add(new JournalEntrie(vendorName, totalAmt));
				if(!isItcElg && !isIntraState && !isRevcharge && igstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
				}
				if(!isItcElg && isIntraState && !isRevcharge && cgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
				}
				if(!isItcElg && isIntraState && !isRevcharge && sgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
				}
				if(!isItcElg && !isRevcharge && cessFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
				}
				if(!isItcElg && !isIntraState && isRevcharge && igstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST_RCM, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST_RCM, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
				}
				if(!isItcElg && isIntraState && isRevcharge && cgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST_RCM, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST_RCM, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
				}
				if(!isItcElg && isIntraState && isRevcharge && sgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST_RCM, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST_RCM, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
				}
				if(!isItcElg && isRevcharge && cessFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS_RCM, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS_RCM, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
				}
				
				if(isItcElg && !isIntraState && isRevcharge && igstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST_RCM, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
				}
				if(isItcElg && isIntraState && isRevcharge && cgstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST_RCM, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
				}
				if(isItcElg && isIntraState && isRevcharge && sgstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST_RCM, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
				}
				if(isItcElg && isRevcharge && cessFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS_RCM, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
				}
				if(tcsFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.TCS_RECEIVABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
				}
				if(tdsFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.TDS_PAYABLE, invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0));
				}
				if(exemptFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.PURCHASE_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
				}
			}else if(invoice.getInvtype().equals(CREDIT_DEBIT_NOTES)) {
				boolean igstFlag = journal.getIgstamount() != null ? journal.getIgstamount() > 0 ? true : false : false;
				boolean cgstFlag = journal.getCgstamount() != null ? journal.getCgstamount() > 0 ? true : false : false;
				boolean sgstFlag = journal.getSgstamount() != null ? journal.getSgstamount() > 0 ? true : false : false;
				boolean cessFlag = journal.getCessAmount() != null ? journal.getCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean tdsFlag = invoice.getTdsAmount() != null ?  invoice.getTdsAmount() > 0 ? true : false : false;
				boolean exemptFlag = invoice.getTotalExemptedAmount() != null ?  invoice.getTotalExemptedAmount() > 0 ? true : false : false;
				
				boolean isDebitNote = (invoice.getCdn() != null && invoice.getCdn().get(0) != null && invoice.getCdn().get(0).getNt() != null && invoice.getCdn().get(0).getNt().get(0) != null && invoice.getCdn().get(0).getNt().get(0).getNtty() != null) ? invoice.getCdn().get(0).getNt().get(0).getNtty().equals("D") ? true : false : false;
				if(isDebitNote) {
					double totalAmt = 0d;
					if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
						int i = 1;
						for(Item item : journal.getItems()) {
							String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.PURCHASES :  AccountConstants.PURCHASES;
							if(roundoffFlag && i == 1) {
								drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
								totalAmt += item.getTotal()+invoice.getRoundOffAmount();
							}else {
								drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
								totalAmt += item.getTotal();
							}
							i++;
						}
					}
					/*if(isNotEmpty(invoice.getRoundOffAmount())) {
						totalAmt += invoice.getRoundOffAmount(); 
					}*/
					totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
					totalAmt -= invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0;
					crEntries.add(new JournalEntrie(vendorName, totalAmt));
					if(!isItcElg && !isIntraState && !isRevcharge && igstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
					}
					if(!isItcElg && isIntraState && !isRevcharge && cgstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
					}
					if(!isItcElg && isIntraState && !isRevcharge && sgstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
					}
					if(!isItcElg && !isRevcharge && cessFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
					}
					if(!isItcElg && !isIntraState && isRevcharge && igstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST_RCM, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST_RCM, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(!isItcElg && isIntraState && isRevcharge && cgstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST_RCM, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST_RCM, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
					}
					if(!isItcElg && isIntraState && isRevcharge && sgstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST_RCM, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST_RCM, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
					}
					if(!isItcElg && isRevcharge && cessFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS_RCM, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS_RCM, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
					}
					
					if(isItcElg && !isIntraState && isRevcharge && igstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST_RCM, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
					}
					if(isItcElg && isIntraState && isRevcharge && cgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST_RCM, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
					}
					if(isItcElg && isIntraState && isRevcharge && sgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST_RCM, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
					}
					if(isItcElg && isRevcharge && cessFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS_RCM, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
					}
					
					if(tcsFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.TCS_RECEIVABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}
					if(tdsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TDS_PAYABLE, invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0));
					}
					if(exemptFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.PURCHASE_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
					}
				}else {
					
					double totalAmt = 0d;
					if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
						int i = 1;
						for(Item item : journal.getItems()) {
							String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.PURCHASES :  AccountConstants.PURCHASES;
							if(roundoffFlag && i == 1) {
								crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
								totalAmt += item.getTotal()+invoice.getRoundOffAmount();
							}else {
								crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
								totalAmt += item.getTotal();
							}
							i++;
						}
					}
					/*if(isNotEmpty(invoice.getRoundOffAmount())) {
						totalAmt += invoice.getRoundOffAmount(); 
					}*/
					totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
					totalAmt -= invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0;
					drEntries.add(new JournalEntrie(vendorName, totalAmt));
					if(!isItcElg && !isIntraState && !isRevcharge && igstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
					}
					if(!isItcElg && isIntraState && !isRevcharge && cgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
					}
					if(!isItcElg && isIntraState && !isRevcharge && sgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
					}
					if(!isItcElg && !isRevcharge && cessFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
					}
					if(!isItcElg && !isIntraState && isRevcharge && igstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST_RCM, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
						drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST_RCM, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(!isItcElg && isIntraState && isRevcharge && cgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST_RCM, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
						drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST_RCM, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
					}
					if(!isItcElg && isIntraState && isRevcharge && sgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST_RCM, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
						drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST_RCM, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
					}
					if(!isItcElg && isRevcharge && cessFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS_RCM, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
						drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS_RCM, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
					}
					
					if(isItcElg && !isIntraState && isRevcharge && igstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST_RCM, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
					}
					if(isItcElg && isIntraState && isRevcharge && cgstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST_RCM, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
					}
					if(isItcElg && isIntraState && isRevcharge && sgstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST_RCM, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
					}
					if(isItcElg && isRevcharge && cessFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS_RCM, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
					}
					
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_RECEIVABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}
					if(tdsFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.TDS_PAYABLE, invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0));
					}
					if(exemptFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.PURCHASE_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
					}
				}
			}else if(invoice.getInvtype().equals(CDNUR)) {
				boolean igstFlag = journal.getIgstamount() != null ? journal.getIgstamount() > 0 ? true : false : false;
				boolean cgstFlag = journal.getCgstamount() != null ? journal.getCgstamount() > 0 ? true : false : false;
				boolean sgstFlag = journal.getSgstamount() != null ? journal.getSgstamount() > 0 ? true : false : false;
				boolean cessFlag = journal.getCessAmount() != null ? journal.getCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean tdsFlag = invoice.getTdsAmount() != null ?  invoice.getTdsAmount() > 0 ? true : false : false;
				boolean exemptFlag = invoice.getTotalExemptedAmount() != null ?  invoice.getTotalExemptedAmount() > 0 ? true : false : false;
				
				boolean isDebitNote = (invoice.getCdnur() != null && invoice.getCdnur().get(0) != null && invoice.getCdnur().get(0).getNtty() != null) ? invoice.getCdnur().get(0).getNtty().equals("D") ? true : false : false;
				if(isDebitNote) {
					double totalAmt = 0d;
					if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
						int i = 1;
						for(Item item : journal.getItems()) {
							String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.PURCHASES :  AccountConstants.PURCHASES;
							if(roundoffFlag && i == 1) {
								drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
								totalAmt += item.getTotal()+invoice.getRoundOffAmount();
							}else {
								drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
								totalAmt += item.getTotal();
							}
							i++;
						}
					}
					/*if(isNotEmpty(invoice.getRoundOffAmount())) {
						totalAmt += invoice.getRoundOffAmount(); 
					}*/
					totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
					totalAmt -= invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0;
					crEntries.add(new JournalEntrie(vendorName, totalAmt));
					if(!isItcElg && !isIntraState && !isRevcharge && igstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
					}
					if(!isItcElg && isIntraState && !isRevcharge && cgstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
					}
					if(!isItcElg && isIntraState && !isRevcharge && sgstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
					}
					if(!isItcElg && !isRevcharge && cessFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
					}
					if(!isItcElg && !isIntraState && isRevcharge && igstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST_RCM, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST_RCM, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(!isItcElg && isIntraState && isRevcharge && cgstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST_RCM, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST_RCM, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
					}
					if(!isItcElg && isIntraState && isRevcharge && sgstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST_RCM, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST_RCM, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
					}
					if(!isItcElg && isRevcharge && cessFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS_RCM, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS_RCM, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
					}
					if(isItcElg && !isIntraState && isRevcharge && igstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST_RCM, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
					}
					if(isItcElg && isIntraState && isRevcharge && cgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST_RCM, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
					}
					if(isItcElg && isIntraState && isRevcharge && sgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST_RCM, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
					}
					if(isItcElg && isRevcharge && cessFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS_RCM, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
					}
					if(tcsFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.TCS_RECEIVABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}
					if(tdsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TDS_PAYABLE, invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0));
					}
					if(exemptFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.PURCHASE_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
					}
				}else {
					
					double totalAmt = 0d;
					if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
						int i = 1;
						for(Item item : journal.getItems()) {
							String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.PURCHASES :  AccountConstants.PURCHASES;
							if(roundoffFlag && i == 1) {
								crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
								totalAmt += item.getTotal()+invoice.getRoundOffAmount();
							}else {
								crEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
								totalAmt += item.getTotal();
							}
							i++;
						}
					}
					/*if(isNotEmpty(invoice.getRoundOffAmount())) {
						totalAmt += invoice.getRoundOffAmount(); 
					}*/
					totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
					totalAmt -= invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0;
					drEntries.add(new JournalEntrie(vendorName, totalAmt));
					if(!isItcElg && !isIntraState && !isRevcharge && igstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
					}
					if(!isItcElg && isIntraState && !isRevcharge && cgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
					}
					if(!isItcElg && isIntraState && !isRevcharge && sgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
					}
					if(!isItcElg && !isRevcharge && cessFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
					}
					if(!isItcElg && !isIntraState && isRevcharge && igstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST_RCM, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
						drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST_RCM, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					}
					if(!isItcElg && isIntraState && isRevcharge && cgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST_RCM, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
						drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST_RCM, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
					}
					if(!isItcElg && isIntraState && isRevcharge && sgstFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST_RCM, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
						drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST_RCM, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
					}
					if(!isItcElg && isRevcharge && cessFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS_RCM, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
						drEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS_RCM, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
					}
					if(isItcElg && !isIntraState && isRevcharge && igstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST_RCM, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
					}
					if(isItcElg && isIntraState && isRevcharge && cgstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST_RCM, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
					}
					if(isItcElg && isIntraState && isRevcharge && sgstFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST_RCM, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
					}
					if(isItcElg && isRevcharge && cessFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS_RCM, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
					}
					if(tcsFlag) {
						crEntries.add(new JournalEntrie(AccountConstants.TCS_RECEIVABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
					}
					if(tdsFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.TDS_PAYABLE, invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0));
					}
					if(exemptFlag) {
						drEntries.add(new JournalEntrie(AccountConstants.PURCHASE_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
					}
				}
			}else if(invoice.getInvtype().equals(NIL)) {
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean tdsFlag = invoice.getTdsAmount() != null ?  invoice.getTdsAmount() > 0 ? true : false : false;
				double totalAmt = 0d;
				if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
					int i = 1;
					for(Item item : journal.getItems()) {
						String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.PURCHASES :  AccountConstants.PURCHASES;
						if(roundoffFlag && i == 1) {
							drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
							totalAmt += item.getTotal()+invoice.getRoundOffAmount();
						}else {
							drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
							totalAmt += item.getTotal();
						}
						i++;
					}
				}
				/*if(isNotEmpty(invoice.getRoundOffAmount())) {
					totalAmt += invoice.getRoundOffAmount(); 
				}*/
				totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
				totalAmt -= invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0;
				crEntries.add(new JournalEntrie(vendorName, totalAmt));
				if(tcsFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.TCS_RECEIVABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
				}
				if(tdsFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.TDS_PAYABLE, invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0));
				}
			}else if(invoice.getInvtype().equals(ADVANCES)) {
				boolean igstFlag = journal.getIgstamount() != null ? journal.getIgstamount() > 0 ? true : false : false;
				boolean cgstFlag = journal.getCgstamount() != null ? journal.getCgstamount() > 0 ? true : false : false;
				boolean sgstFlag = journal.getSgstamount() != null ? journal.getSgstamount() > 0 ? true : false : false;
				boolean cessFlag = journal.getCessAmount() != null ? journal.getCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean tdsFlag = invoice.getTdsAmount() != null ?  invoice.getTdsAmount() > 0 ? true : false : false;
				boolean exemptFlag = invoice.getTotalExemptedAmount() != null ?  invoice.getTotalExemptedAmount() > 0 ? true : false : false;
				double totalAmt = 0d;
				double totalIAmt = 0d;
				if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
					int i=1;
					for(Item item : journal.getItems()) {
						String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.PURCHASES :  AccountConstants.PURCHASES;
						if(i==1) {
							double itemTotal = item.getTotal() != null ? item.getTotal() : 0;
							itemTotal += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
							itemTotal -= invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0;
							crEntries.add(new JournalEntrie(ledgerName, itemTotal));
							itemTotal += invoice.getRoundOffAmount() != null ? invoice.getRoundOffAmount() : 0;
						}else {
							crEntries.add(new JournalEntrie(ledgerName, item.getTotal()));
						}
						totalAmt += item.getTotal();
						i++;
					}
				}
				totalAmt += invoice.getRoundOffAmount() != null ? invoice.getRoundOffAmount() : 0;
				
				
				drEntries.add(new JournalEntrie(vendorName, totalAmt));
				if(!isIntraState && isRevcharge && igstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST_RCM, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
					totalIAmt += journal.getIgstamount();
				}
				if(isIntraState && isRevcharge && cgstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST_RCM, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
					totalIAmt += journal.getCgstamount();
				}
				if(isIntraState && isRevcharge && sgstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST_RCM, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
					totalIAmt += journal.getSgstamount();
				}
				if(isRevcharge && cessFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS_RCM, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
					totalIAmt += journal.getCessAmount();
				}
				if(isRevcharge) {
					if(totalIAmt > 0) {
						drEntries.add(new JournalEntrie(AccountConstants.TAX_ON_ADVANCE_RCM, totalIAmt));
					}
				}
				if(tcsFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.TCS_RECEIVABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
				}
				if(tdsFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.TDS_PAYABLE, invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0));
				}
				if(exemptFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.PURCHASE_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
				}
			}else if(invoice.getInvtype().equals(ATPAID)) {
				boolean igstFlag = invoice.getTotalIgstAmount() != null ?  invoice.getTotalIgstAmount() > 0 ? true : false : false;
				boolean cgstFlag = invoice.getTotalCgstAmount() != null ?  invoice.getTotalCgstAmount() > 0 ? true : false : false;
				boolean sgstFlag = invoice.getTotalSgstAmount() != null ?  invoice.getTotalSgstAmount() > 0 ? true : false : false;
				boolean cessFlag = invoice.getTotalCessAmount() != null ?  invoice.getTotalCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean tdsFlag = invoice.getTdsAmount() != null ?  invoice.getTdsAmount() > 0 ? true : false : false;
				double totalIAmt = 0d;
				
				if(igstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
					totalIAmt += invoice.getTotalIgstAmount();
				}
				if(cgstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
					totalIAmt += invoice.getTotalCgstAmount();
				}
				if(sgstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
					totalIAmt += invoice.getTotalSgstAmount();
				}
				if(cessFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
					totalIAmt += invoice.getTotalCessAmount();
				}
				totalIAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
				totalIAmt -= invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0;
				if(totalIAmt > 0) {
					drEntries.add(new JournalEntrie(AccountConstants.TAX_ON_ADVANCE, totalIAmt));
				}
				if(tcsFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.TCS_RECEIVABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
				}
				if(tdsFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.TDS_PAYABLE, invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0));
				}
			}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
				isIntraState = false;
				isRevcharge = true;
				boolean igstFlag = journal.getIgstamount() != null ? journal.getIgstamount() > 0 ? true : false : false;
				boolean cgstFlag = journal.getCgstamount() != null ? journal.getCgstamount() > 0 ? true : false : false;
				boolean sgstFlag = journal.getSgstamount() != null ? journal.getSgstamount() > 0 ? true : false : false;
				boolean cessFlag = journal.getCessAmount() != null ? journal.getCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean tdsFlag = invoice.getTdsAmount() != null ?  invoice.getTdsAmount() > 0 ? true : false : false;
				boolean exemptFlag = invoice.getTotalExemptedAmount() != null ?  invoice.getTotalExemptedAmount() > 0 ? true : false : false;
				double totalAmt = 0d;
				if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
					int i = 1;
					for(Item item : journal.getItems()) {
						String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.PURCHASES :  AccountConstants.PURCHASES;
						if(roundoffFlag && i == 1) {
							drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
							totalAmt += item.getTotal()+invoice.getRoundOffAmount();
						}else {
							drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
							totalAmt += item.getTotal();
						}
						i++;
					}
				}
				/*if(isNotEmpty(invoice.getRoundOffAmount())) {
					totalAmt += invoice.getRoundOffAmount(); 
				}*/
				totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
				totalAmt -= invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0;
				crEntries.add(new JournalEntrie(vendorName, totalAmt));
				if(!isItcElg && !isIntraState && !isRevcharge && igstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
				}
				if(!isItcElg && isIntraState && !isRevcharge && cgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
				}
				if(!isItcElg && isIntraState && !isRevcharge && sgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
				}
				if(!isItcElg && !isRevcharge && cessFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
				}
				if(!isItcElg && !isIntraState && isRevcharge && igstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
				}
				if(!isItcElg && isIntraState && isRevcharge && cgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
				}
				if(!isItcElg && isIntraState && isRevcharge && sgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
				}
				if(!isItcElg && isRevcharge && cessFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
				}
				
				if(isItcElg && !isIntraState && isRevcharge && igstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
				}
				if(isItcElg && isIntraState && isRevcharge && cgstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
				}
				if(isItcElg && isIntraState && isRevcharge && sgstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
				}
				if(isItcElg && isRevcharge && cessFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
				}
				if(tcsFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.TCS_RECEIVABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
				}
				if(tdsFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.TDS_PAYABLE, invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0));
				}
				if(exemptFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.PURCHASE_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
				}
			}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_SERVICES)) {
				isIntraState = false;
				boolean igstFlag = journal.getIgstamount() != null ? journal.getIgstamount() > 0 ? true : false : false;
				boolean cgstFlag = journal.getCgstamount() != null ? journal.getCgstamount() > 0 ? true : false : false;
				boolean sgstFlag = journal.getSgstamount() != null ? journal.getSgstamount() > 0 ? true : false : false;
				boolean cessFlag = journal.getCessAmount() != null ? journal.getCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean tdsFlag = invoice.getTdsAmount() != null ?  invoice.getTdsAmount() > 0 ? true : false : false;
				boolean exemptFlag = invoice.getTotalExemptedAmount() != null ?  invoice.getTotalExemptedAmount() > 0 ? true : false : false;
				double totalAmt = 0d;
				if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
					int i = 1;
					for(Item item : journal.getItems()) {
						String ledgerName = item.getLedgerName() !=null ? item.getLedgerName() != "" ? item.getLedgerName() : AccountConstants.PURCHASES :  AccountConstants.PURCHASES;
						if(roundoffFlag && i == 1) {
							drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()+invoice.getRoundOffAmount()));
							totalAmt += item.getTotal()+invoice.getRoundOffAmount();
						}else {
							drEntries.add(new JournalEntrie(ledgerName, item.getTaxablevalue()));
							totalAmt += item.getTotal();
						}
						i++;
					}
				}
				/*if(isNotEmpty(invoice.getRoundOffAmount())) {
					totalAmt += invoice.getRoundOffAmount(); 
				}*/
				totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
				totalAmt -= invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0;
				crEntries.add(new JournalEntrie(vendorName, totalAmt));
				if(!isItcElg && !isIntraState && !isRevcharge && igstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
				}
				if(!isItcElg && isIntraState && !isRevcharge && cgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
				}
				if(!isItcElg && isIntraState && !isRevcharge && sgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
				}
				if(!isItcElg && !isRevcharge && cessFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
				}
				if(!isItcElg && !isIntraState && isRevcharge && igstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST_RCM, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST_RCM, invoice.getTotalIgstAmount() != null ? invoice.getTotalIgstAmount() : 0));
				}
				if(!isItcElg && isIntraState && isRevcharge && cgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST_RCM, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST_RCM, invoice.getTotalCgstAmount() != null ? invoice.getTotalCgstAmount() : 0));
				}
				if(!isItcElg && isIntraState && isRevcharge && sgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST_RCM, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST_RCM, invoice.getTotalSgstAmount() != null ? invoice.getTotalSgstAmount() : 0));
				}
				if(!isItcElg && isRevcharge && cessFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS_RCM, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS_RCM, invoice.getTotalCessAmount() != null ? invoice.getTotalCessAmount() : 0));
				}
				
				if(isItcElg && !isIntraState && isRevcharge && igstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_IGST_RCM, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
				}
				if(isItcElg && isIntraState && isRevcharge && cgstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CGST_RCM, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
				}
				if(isItcElg && isIntraState && isRevcharge && sgstFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_SGST_RCM, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
				}
				if(isItcElg && isRevcharge && cessFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.OUTPUT_CESS_RCM, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
				}
				if(tcsFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.TCS_RECEIVABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
				}
				if(tdsFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.TDS_PAYABLE, invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0));
				}
				if(exemptFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.PURCHASE_EXEMPT, invoice.getTotalExemptedAmount() != null ? invoice.getTotalExemptedAmount() : 0));
				}
			}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
				boolean igstFlag = journal.getIgstamount() != null ? journal.getIgstamount() > 0 ? true : false : false;
				boolean cgstFlag = journal.getCgstamount() != null ? journal.getCgstamount() > 0 ? true : false : false;
				boolean sgstFlag = journal.getSgstamount() != null ? journal.getSgstamount() > 0 ? true : false : false;
				boolean cessFlag = journal.getCessAmount() != null ? journal.getCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean tdsFlag = invoice.getTdsAmount() != null ?  invoice.getTdsAmount() > 0 ? true : false : false;
				boolean isdineligibleFlag = journal.getIsdineligiblecredit() != null ?  journal.getIsdineligiblecredit() > 0 ? true : false : false;
				double totalAmt = 0d;
				if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
					for(Item item : journal.getItems()) {
						totalAmt += item.getTotal(); 
					}
				}
				
				totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
				totalAmt -= invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0;
				crEntries.add(new JournalEntrie(AccountConstants.HO_ISD, totalAmt));
				if(igstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
				}
				if(cgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
				}
				if(sgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
				}
				if(cessFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
				}
				if(tcsFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.TCS_RECEIVABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
				}
				if(isdineligibleFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.ISD_INELIGIBLE, journal.getIsdineligiblecredit() != null ? journal.getIsdineligiblecredit() : 0));
				}
				if(tdsFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.TDS_PAYABLE, invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0));
				}
			}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ITC_REVERSAL)) {
				boolean igstFlag = journal.getIgstamount() != null ? journal.getIgstamount() > 0 ? true : false : false;
				boolean cgstFlag = journal.getCgstamount() != null ? journal.getCgstamount() > 0 ? true : false : false;
				boolean sgstFlag = journal.getSgstamount() != null ? journal.getSgstamount() > 0 ? true : false : false;
				boolean cessFlag = journal.getCessAmount() != null ? journal.getCessAmount() > 0 ? true : false : false;
				boolean tcsFlag = invoice.getTcstdsAmount() != null ?  invoice.getTcstdsAmount() > 0 ? true : false : false;
				boolean tdsFlag = invoice.getTdsAmount() != null ?  invoice.getTdsAmount() > 0 ? true : false : false;
				double totalAmt = 0d;
				if(isNotEmpty(journal) && isNotEmpty(journal.getItems())) {
					for(Item item : journal.getItems()) {
						totalAmt += item.getTotal(); 
					}
				}
				
				totalAmt += invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0;
				totalAmt -= invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0;
				crEntries.add(new JournalEntrie(AccountConstants.INPUT_ITC_REVERSAL, totalAmt));
				if(igstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_IGST, journal.getIgstamount() != null ? journal.getIgstamount() : 0));
				}
				if(cgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CGST, journal.getCgstamount() != null ? journal.getCgstamount() : 0));
				}
				if(sgstFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_SGST, journal.getSgstamount() != null ? journal.getSgstamount() : 0));
				}
				if(cessFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.INPUT_CESS, journal.getCessAmount() != null ? journal.getCessAmount() : 0));
				}
				if(tcsFlag) {
					drEntries.add(new JournalEntrie(AccountConstants.TCS_RECEIVABLE, invoice.getTcstdsAmount() != null ? invoice.getTcstdsAmount() : 0));
				}
				if(tdsFlag) {
					crEntries.add(new JournalEntrie(AccountConstants.TDS_PAYABLE, invoice.getTdsAmount() != null ? invoice.getTdsAmount() : 0));
				}
			}
		}
		double totalCredit = 0.0;
		double totalDebit = 0.0;
		
		for(JournalEntrie creditentrie : crEntries) {
			totalCredit += creditentrie.getValue() == null ? 0.0 : creditentrie.getValue();
		}
		for(JournalEntrie debitentrie : drEntries) {
			totalDebit += debitentrie.getValue() == null ? 0.0 : debitentrie.getValue();
		}
		journal.setCreditTotal(Math.abs(totalCredit));
		journal.setDebitTotal(Math.abs(totalDebit));
		journal.setDrEntrie(drEntries);
		journal.setCrEntrie(crEntries);
		accountingJournalRepository.save(journal);
	}
	
	public void accountingJournalExists(String clientid, InvoiceParent invoice, String returntype) {
		
		String docId = invoice.getId().toString();
		
		accountingJournalRepository.deleteByInvoiceId(docId);
	}
		
	public AccountingJournal addDefaultInfo(String returntype, InvoiceParent invoice,String clientid,boolean isIntraState) {
		if("Sales Register".equalsIgnoreCase(returntype) || "SalesRegister".equalsIgnoreCase(returntype) || "GSTR1".equalsIgnoreCase(returntype)) {
			List<AccountingJournal> journals = accountingJournalRepository.findByClientId(clientid);
			String journalnumber = "1";
			if (isNotEmpty(journals)) {
				journalnumber = journals.size() + 1 + "";
			}
			String docId = invoice != null ? invoice.getId().toString() : "";
			AccountingJournal journal = accountingJournalRepository.findByInvoiceIdAndClientIdAndReturnType(docId, clientid, returntype);
			if (isEmpty(journal)) {
				journal = new AccountingJournal();
			}
			if (isEmpty(journal.getJournalNumber())) {
				journal.setJournalNumber(journalnumber);
			}
			String dealertype = "";
			if (isNotEmpty(invoice.getDealerType())) {
				dealertype = invoice.getDealerType();
			}
			if (isNotEmpty(invoice.getClientid())) {
				journal.setClientId(invoice.getClientid());
			}
			journal.setInvoiceId(invoice.getId().toString());
			journal.setReturnType(returntype);
			journal.setInvoiceNumber(invoice.getInvoiceno());
			if (isNotEmpty(invoice.getBilledtoname())) {
				journal.setVendorName(invoice.getBilledtoname());
			}
			if (isNotEmpty(invoice.getDateofinvoice())) {
				journal.setDateofinvoice(invoice.getDateofinvoice());
				Calendar cal = Calendar.getInstance();
				cal.setTime(invoice.getDateofinvoice());
				int month = cal.get(Calendar.MONTH) + 1;
				int year = cal.get(Calendar.YEAR);
				String strMonth = month < 10 ? "0" + month : month + "";
				journal.setInvoiceMonth(strMonth + year);
			}
			if(isNotEmpty(invoice.getMthCd())) {
				journal.setMthCd(invoice.getMthCd());
			}
			if(isNotEmpty(invoice.getYrCd())) {
				journal.setYrCd(invoice.getYrCd());
			}
			if(isNotEmpty(invoice.getQrtCd())) {
				journal.setQrtCd(invoice.getQrtCd());
			}
			if (isNotEmpty(invoice.getUserid())) {
				journal.setUserId(invoice.getUserid());
			}
			if(isNotEmpty(invoice.getRoundOffAmount())) {
				journal.setRoundOffAmount(invoice.getRoundOffAmount());
			}else {
				journal.setRoundOffAmount(0d);
			}
			
			Map<String,Item> ledgerNamemMap = Maps.newHashMap();
			List<String> ledgerkeys = Lists.newArrayList();
			Double taxableAmount = 0d;
			Double totalAmount = 0d;
			Double igstAmount = 0d;
			Double cgstAmount = 0d;
			Double sgstAmount = 0d;
			Double cessAmount = 0d;
			Double exemptedAmount = 0d;
			if (isNotEmpty(invoice.getItems())) {
				for (Item item : invoice.getItems()) {
					String ledgerName = "";
					if(isNotEmpty(item.getLedgerName())) {
						ledgerName = item.getLedgerName().trim();
						
						ProfileLedger ledger = ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, ledgerName);
						if(isEmpty(ledger)) {
							ledger = new ProfileLedger();
							ledger.setClientid(clientid);
							ledger.setGrpsubgrpName(AccountConstants.SALES);
							ledger.setLedgerName(item.getLedgerName());
							ledger.setLedgerpath(AccountConstants.SALES_DEFAULT_PATH);
							ledger.setLedgerOpeningBalance(0);
							ledgerRepository.save(ledger);
						}
					}else {
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
							ledgerName = AccountConstants.CASH;
						}else {
							ledgerName = AccountConstants.SALES;
						}
					}
					
					if(!ledgerNamemMap.containsKey(ledgerName)) {
						item.setLedgerName(ledgerName);
						ledgerNamemMap.put(ledgerName, item);
						ledgerkeys.add(ledgerName);
					}else {
						Item itm = ledgerNamemMap.get(ledgerName);
						if(isNotEmpty(itm.getIgstamount())) {
							if(isNotEmpty(item.getIgstamount())) {
								itm.setIgstamount(itm.getIgstamount() + item.getIgstamount());
							}
						}else {
							if(isNotEmpty(item.getIgstamount())) {
								itm.setIgstamount(item.getIgstamount());
							}
						}
						if(isNotEmpty(itm.getCgstamount())) {
							if(isNotEmpty(item.getCgstamount())) {
								itm.setCgstamount(itm.getCgstamount() + item.getCgstamount());
							}
						}else {
							if(isNotEmpty(item.getCgstamount())) {
								itm.setCgstamount(item.getCgstamount());
							}
						}
						if(isNotEmpty(itm.getSgstamount())) {
							if(isNotEmpty(item.getSgstamount())) {
								itm.setSgstamount(itm.getSgstamount() + item.getSgstamount());
							}
						}else {
							if(isNotEmpty(item.getSgstamount())) {
								itm.setSgstamount(item.getSgstamount());
							}
						}
						if(isNotEmpty(itm.getCessamount())) {
							if(isNotEmpty(item.getCessamount())) {
								itm.setCessamount(itm.getCessamount() + item.getCessamount());
							}
						}else {
							if(isNotEmpty(item.getCessamount())) {
								itm.setCessamount(item.getCessamount());
							}
						}
						if(isNotEmpty(itm.getExmepted())) {
							if (isNotEmpty(item.getExmepted())) {
								Double qty = item.getQuantity() == null ? 1 : item.getQuantity();
								itm.setExmepted(itm.getExmepted() + (item.getExmepted()*qty));
							}
						}else {
							if(isNotEmpty(item.getExmepted())) {
								Double qty = item.getQuantity() == null ? 1 : item.getQuantity();
								itm.setExmepted(item.getExmepted()*qty);
							}
						}
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
							if(isNotEmpty(itm.getTaxablevalue())) {
								if (isNotEmpty(item.getAdvadjustedAmount())) {
									itm.setTaxablevalue(itm.getTaxablevalue() + item.getAdvadjustedAmount());
								}
							}else {
								if (isNotEmpty(item.getAdvadjustedAmount())) {
									itm.setTaxablevalue(item.getAdvadjustedAmount());
								}
							}
						}else {
							if(isNotEmpty(itm.getTaxablevalue())) {
								if(isNotEmpty(item.getTaxablevalue())) {
									itm.setTaxablevalue(itm.getTaxablevalue() + item.getTaxablevalue());
								}
							}else {
								if(isNotEmpty(item.getTaxablevalue())) {
									itm.setTaxablevalue(item.getTaxablevalue());
								}
							}
						}
						if(isNotEmpty(itm.getTotal())) {
							if(isNotEmpty(item.getTotal())) {
								itm.setTotal(itm.getTotal() + item.getTotal());
							}
						}else {
							if(isNotEmpty(item.getTotal())) {
								itm.setTotal(item.getTotal());
							}
						}
						ledgerNamemMap.put(ledgerName, itm);
					}
					
					if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
						if (isNotEmpty(item.getAdvadjustedAmount())) {
							taxableAmount = taxableAmount + item.getAdvadjustedAmount();
						}
					}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
						if (isNotEmpty(item.getRateperitem())) {
							taxableAmount = taxableAmount + item.getRateperitem();
						}
					} else {
						if (isNotEmpty(item.getTaxablevalue())) {
							taxableAmount = taxableAmount + item.getTaxablevalue();
						}
					}
					if (isNotEmpty(item.getTotal())) {
						totalAmount = totalAmount + item.getTotal();
					}
					if (isNotEmpty(item.getIgstamount())) {
						igstAmount = igstAmount + item.getIgstamount();
					}
					if (isNotEmpty(item.getSgstamount())) {
						sgstAmount = sgstAmount + item.getSgstamount();
					}
					if (isNotEmpty(item.getCgstamount())) {
						cgstAmount = cgstAmount + item.getCgstamount();
					}
					if (isNotEmpty(item.getCessamount())) {
						cessAmount = cessAmount + item.getCessamount();
					}
					if (isNotEmpty(item.getExmepted())) {
						Double qty = item.getQuantity() == null ? 1 : item.getQuantity();
						exemptedAmount = exemptedAmount + (item.getExmepted()*qty);
					}
				}
			}
			String revchargetype = "";
			if (isNotEmpty(invoice.getRevchargetype())) {
				revchargetype = invoice.getRevchargetype();
			}
	
			if ("Reverse".equalsIgnoreCase(revchargetype)) {
				if (isNotEmpty(invoice.getTcstdsAmount())) {
					journal.setTdsamount(invoice.getTcstdsAmount());
					journal.setCustomerorSupplierAccount(taxableAmount - invoice.getTcstdsAmount());
				} else {
					journal.setTdsamount(0d);
					journal.setCustomerorSupplierAccount(taxableAmount);
				}
				journal.setSalesorPurchases(taxableAmount);
				journal.setRcmorinterorintra("RCM");
				journal.setIgstamount(0d);
				journal.setSgstamount(0d);
				journal.setCgstamount(0d);
				journal.setCessAmount(0d);
				journal.setExemptedamount(exemptedAmount);
			} else {
				if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.NIL)) {
					if (isNotEmpty(invoice.getTcstdsAmount())) {
						journal.setTdsamount(invoice.getTcstdsAmount());
						journal.setCustomerorSupplierAccount(taxableAmount - invoice.getTcstdsAmount());
					} else {
						journal.setTdsamount(0d);
						journal.setCustomerorSupplierAccount(taxableAmount);
					}
					journal.setSalesorPurchases(taxableAmount);
					journal.setRcmorinterorintra("RCM");
					journal.setIgstamount(0d);
					journal.setSgstamount(0d);
					journal.setCgstamount(0d);
					journal.setCessAmount(0d);
					journal.setExemptedamount(exemptedAmount);
				} else if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
					String exptype = invoice.getExp().get(0).getExpTyp();
					journal.setSalesorPurchases(taxableAmount);
					if ("Composition".equals(dealertype)) {
						journal.setRcmorinterorintra("RCM");
						journal.setIgstamount(0d);
						journal.setSgstamount(0d);
						journal.setCgstamount(0d);
						journal.setCessAmount(0d);
						journal.setExemptedamount(exemptedAmount);
					} else {
						if (exptype.equals("WOPAY")) {
							journal.setRcmorinterorintra("RCM");
							journal.setIgstamount(0d);
							journal.setSgstamount(0d);
							journal.setCgstamount(0d);
							journal.setCessAmount(0d);
							journal.setExemptedamount(exemptedAmount);
						} else if(exptype.equals("WPAY")){
							journal.setRcmorinterorintra("Inter");
							journal.setIgstamount(igstAmount);
							journal.setSgstamount(sgstAmount);
							journal.setCgstamount(cgstAmount);
							journal.setCessAmount(cessAmount);
							journal.setExemptedamount(exemptedAmount);
						}else {
							if (isIntraState) {
								journal.setRcmorinterorintra("Intra");
							} else {
								journal.setRcmorinterorintra("Inter");
							}
							journal.setIgstamount(igstAmount);
							journal.setSgstamount(sgstAmount);
							journal.setCgstamount(cgstAmount);
							journal.setCessAmount(cessAmount);
							journal.setExemptedamount(exemptedAmount);
						}
					}
					if (isNotEmpty(invoice.getTcstdsAmount())) {
						journal.setTdsamount(invoice.getTcstdsAmount());
						journal.setCustomerorSupplierAccount(totalAmount - invoice.getTcstdsAmount());
					} else {
						journal.setTdsamount(0d);
						journal.setCustomerorSupplierAccount(totalAmount);
					}
				} else if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)
						|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2C)
						|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2CL)) {
					String invtype = invoice.getB2b().get(0).getInv().get(0).getInvTyp();
					journal.setSalesorPurchases(taxableAmount);
					if(isNotEmpty(invtype) && invtype.equals("SEWPC")) {
						journal.setInvType("SEWPC");
					}else {
						journal.setInvType(" ");
					}
					if ("Composition".equals(dealertype)) {
						journal.setRcmorinterorintra("RCM");
						journal.setIgstamount(0d);
						journal.setSgstamount(0d);
						journal.setCgstamount(0d);
						journal.setCessAmount(0d);
						journal.setExemptedamount(exemptedAmount);
					} else {
						if (invtype.equals("SEWOP")) {
							journal.setRcmorinterorintra("RCM");
							journal.setIgstamount(0d);
							journal.setSgstamount(0d);
							journal.setCgstamount(0d);
							journal.setCessAmount(0d);
							journal.setExemptedamount(exemptedAmount);
						} else if(invtype.equals("CBW") || invtype.equals("SEWP") || invtype.equals("SEWPC")){
							journal.setRcmorinterorintra("Inter");
							journal.setIgstamount(igstAmount);
							journal.setSgstamount(sgstAmount);
							journal.setCgstamount(cgstAmount);
							journal.setCessAmount(cessAmount);
							journal.setExemptedamount(exemptedAmount);
						}else {
							if (isIntraState) {
								journal.setRcmorinterorintra("Intra");
							} else {
								journal.setRcmorinterorintra("Inter");
							}
							journal.setIgstamount(igstAmount);
							journal.setSgstamount(sgstAmount);
							journal.setCgstamount(cgstAmount);
							journal.setCessAmount(cessAmount);
							journal.setExemptedamount(exemptedAmount);
						}
					}
					if (isNotEmpty(invoice.getTcstdsAmount())) {
						journal.setTdsamount(invoice.getTcstdsAmount());
						journal.setCustomerorSupplierAccount(totalAmount - invoice.getTcstdsAmount());
					} else {
						journal.setTdsamount(0d);
						journal.setCustomerorSupplierAccount(totalAmount);
					}
				} else {
					if ("Composition".equals(dealertype)) {
						journal.setRcmorinterorintra("RCM");
						journal.setIgstamount(0d);
						journal.setSgstamount(0d);
						journal.setCgstamount(0d);
						journal.setCessAmount(0d);
						journal.setExemptedamount(exemptedAmount);
					} else {
						if (isIntraState) {
							journal.setRcmorinterorintra("Intra");
						} else {
							journal.setRcmorinterorintra("Inter");
						}
						journal.setIgstamount(igstAmount);
						journal.setSgstamount(sgstAmount);
						journal.setCgstamount(cgstAmount);
						journal.setCessAmount(cessAmount);
						journal.setExemptedamount(exemptedAmount);
					}
					journal.setSalesorPurchases(taxableAmount);
					if (isNotEmpty(invoice.getTcstdsAmount())) {
						journal.setTdsamount(invoice.getTcstdsAmount());
						journal.setCustomerorSupplierAccount(totalAmount - invoice.getTcstdsAmount());
					} else {
						journal.setTdsamount(0d);
						journal.setCustomerorSupplierAccount(totalAmount);
					}
				}
			}
			
			List<Item> itemlist = Lists.newArrayList();
			for(String key : ledgerkeys) {
				Item itm = ledgerNamemMap.get(key);
				itemlist.add(itm);
			}
			journal.setItems(itemlist);
			journal.setInvoiceType(invoice.getInvtype());
			if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
				journal.setSalesorPurchases(taxableAmount);
			}
			if(invoice != null && invoice.getB2b() != null && invoice.getB2b().get(0) != null && invoice.getB2b().get(0).getCtin()!= null) {
				journal.setCtin(invoice.getB2b().get(0).getCtin().trim());
				String pancategory = getPanCategory(invoice.getB2b().get(0).getCtin().trim());
				if(isNotEmpty(pancategory)) {
					journal.setPancategory(pancategory);
				}else {
					journal.setPancategory(null);
				}
			}
			if(invoice != null && invoice.getSection() != null) {
				journal.setTcssection(invoice.getSection());
			}
			if(invoice != null && invoice.getTcstdspercentage() != null) {
				journal.setTcspercentage(invoice.getTcstdspercentage()+"");
			}
			if(invoice != null && invoice.getDueDate() != null) {
				journal.setDueDate(invoice.getDueDate());
			}else if(invoice != null && invoice.getDateofinvoice() != null) {
				journal.setDueDate(invoice.getDateofinvoice());
			}
			boolean invoiceamountflag = invoice.getTotalamount() != null ?  invoice.getTotalamount() > 0 ? true : false : false;
			if(invoiceamountflag) {
				journal.setInvoiceamount(invoice.getTotalamount());
			}
			return journal;
		}else {

			AccountingJournal journal = accountingJournalRepository.findByInvoiceId(invoice.getId().toString());
			List<AccountingJournal> journals = accountingJournalRepository.findByClientId(clientid);
			String journalnumber = "1";
			if (isNotEmpty(journals)) {
				journalnumber = (journals.size() + 1) + "";
			}
			if (isEmpty(journal)) {
				journal = new AccountingJournal();
			}
			if(isNotEmpty(invoice.getRoundOffAmount())) {
				journal.setRoundOffAmount(invoice.getRoundOffAmount());
			}else {
				journal.setRoundOffAmount(0d);
			}
			if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ADVANCES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
				if (isNotEmpty(invoice.getDateofinvoice())) {
					journal.setDateofinvoice(invoice.getDateofinvoice());
					Calendar cal = Calendar.getInstance();
					cal.setTime(invoice.getDateofinvoice());
					int months = cal.get(Calendar.MONTH) + 1;
					int years = cal.get(Calendar.YEAR);
					String strMonths = months < 10 ? "0" + months : months + "";
					journal.setInvoiceMonth(strMonths + years);
					if(isNotEmpty(invoice.getMthCd())) {
						journal.setMthCd(invoice.getMthCd());
					}
					if(isNotEmpty(invoice.getYrCd())) {
						journal.setYrCd(invoice.getYrCd());
					}
					if(isNotEmpty(invoice.getQrtCd())) {
						journal.setQrtCd(invoice.getQrtCd());
					}
				}
			}else {
				if (isNotEmpty(invoice.getBillDate())) {
					journal.setDateofinvoice(invoice.getBillDate());
					Calendar cal = Calendar.getInstance();
					cal.setTime(invoice.getBillDate());
					int months = cal.get(Calendar.MONTH) + 1;
					int years = cal.get(Calendar.YEAR);
					String strMonths = months < 10 ? "0" + months : months + "";
					journal.setInvoiceMonth(strMonths + years);
					if(isNotEmpty(invoice.getTrDatemthCd())) {
						journal.setMthCd(invoice.getTrDatemthCd());
					}
					if(isNotEmpty(invoice.getTrDateyrCd())) {
						journal.setYrCd(invoice.getTrDateyrCd());
					}
					if(isNotEmpty(invoice.getTrDateqrtCd())) {
						journal.setQrtCd(invoice.getTrDateqrtCd());
					}
				} else if (isNotEmpty(invoice.getDateofinvoice())) {
					journal.setDateofinvoice(invoice.getDateofinvoice());
					Calendar cal = Calendar.getInstance();
					cal.setTime(invoice.getDateofinvoice());
					int months = cal.get(Calendar.MONTH) + 1;
					int years = cal.get(Calendar.YEAR);
					String strMonths = months < 10 ? "0" + months : months + "";
					journal.setInvoiceMonth(strMonths + years);
					if(isNotEmpty(invoice.getMthCd())) {
						journal.setMthCd(invoice.getMthCd());
					}
					if(isNotEmpty(invoice.getYrCd())) {
						journal.setYrCd(invoice.getYrCd());
					}
					if(isNotEmpty(invoice.getQrtCd())) {
						journal.setQrtCd(invoice.getQrtCd());
					}
				}
			}
			if (isNotEmpty(invoice.getUserid())) {
				journal.setUserId(invoice.getUserid());
			}
			if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
				if(isNotEmpty(invoice.getAdvPCustname())) {
					journal.setLedgerName(invoice.getAdvPCustname());
				}else {
					journal.setLedgerName(AccountConstants.OTHER_CREDITORS);							
				}
				if(isNotEmpty(invoice.getAdvPCustname())) {
					journal.setVendorName(invoice.getAdvPCustname());
				}else {
					journal.setVendorName(AccountConstants.OTHER_CREDITORS);							
				}
			}else {
				if (isNotEmpty(invoice.getLedgerName())) {
					journal.setLedgerName(invoice.getLedgerName());
				}
				if (isNotEmpty(invoice.getVendorName())) {
					if (isNotEmpty(invoice.getInvoiceEcomGSTIN()) && isNotEmpty(invoice.getInvoiceEcomOperator())) {
						String ecomname = invoice.getInvoiceEcomOperator() + "("+ invoice.getInvoiceEcomGSTIN() + " - "	+ invoice.getVendorName() + ")";
						journal.setVendorName(ecomname);
					} else if (isNotEmpty(invoice.getInvoiceEcomGSTIN())) {
						String ecomname = invoice.getInvoiceEcomGSTIN() + "("+ invoice.getVendorName() + ")";
						journal.setVendorName(ecomname);
					} else if (isNotEmpty(invoice.getInvoiceEcomOperator())) {
						String ecomname = invoice.getInvoiceEcomOperator() + "("+ invoice.getVendorName() + ")";
						journal.setVendorName(ecomname);
					} else {
						journal.setVendorName(invoice.getVendorName());
					}
				} else if (isNotEmpty(invoice.getBilledtoname())) {
					if (isNotEmpty(invoice.getInvoiceEcomGSTIN()) && isNotEmpty(invoice.getInvoiceEcomOperator())) {
						String ecomname = invoice.getInvoiceEcomOperator() + "("+ invoice.getInvoiceEcomGSTIN() + " - " + invoice.getBilledtoname()	+ ")";
						journal.setVendorName(ecomname);
					} else if (isNotEmpty(invoice.getInvoiceEcomGSTIN())) {
						String ecomname = invoice.getInvoiceEcomGSTIN() + "("+ invoice.getBilledtoname() + ")";
						journal.setVendorName(ecomname);
					} else if (isNotEmpty(invoice.getInvoiceEcomOperator())) {
						String ecomname = invoice.getInvoiceEcomOperator() + "("+ invoice.getBilledtoname() + ")";
						journal.setVendorName(ecomname);
					} else {
						journal.setVendorName(invoice.getBilledtoname());
					}
				} else {
					if (isNotEmpty(invoice.getInvoiceEcomGSTIN()) && isNotEmpty(invoice.getInvoiceEcomOperator())) {
						String ecomname = invoice.getInvoiceEcomOperator() + "("+ invoice.getInvoiceEcomGSTIN() + " - " + "Other Creditors)";
						journal.setVendorName(ecomname);
					} else if (isNotEmpty(invoice.getInvoiceEcomGSTIN())) {
						String ecomname = invoice.getInvoiceEcomGSTIN() + "(Other Creditors)";
						journal.setVendorName(ecomname);
					} else if (isNotEmpty(invoice.getInvoiceEcomOperator())) {
						String ecomname = invoice.getInvoiceEcomOperator() + "(Other Creditors)";
						journal.setVendorName(ecomname);
					} else {
						journal.setVendorName("Other Creditors");
					}
				}
			}
			if (isNotEmpty(invoice.getClientid())) {
				journal.setClientId(invoice.getClientid());
			}
			if (isNotEmpty(invoice.getInvtype())) {
				if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					invoice.setRevchargetype("Reverse");
				}
				journal.setInvoiceType(invoice.getInvtype());
				if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(invoice.getInvtype()) || MasterGSTConstants.CDNUR.equalsIgnoreCase(invoice.getInvtype())) {
					if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(invoice.getInvtype())) {
						String docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
						journal.setCreditDebitNoteType(docType);
					} else {
						String docType = invoice.getCdnur().get(0).getNtty();
						journal.setCreditDebitNoteType(docType);
					}
				}

				if (isEmpty(invoice.getLedgerName())) {
					if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
						journal.setLedgerName(AccountConstants.CASH);
					} else if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
						journal.setLedgerName("HO-ISD");
					} else {
						journal.setLedgerName("Expenditure");
					}
				} else {
					journal.setLedgerName(invoice.getLedgerName());
				}
			}
			journal.setInvoiceId(invoice.getId().toString());
			journal.setReturnType("GSTR2");
			journal.setInvoiceNumber(invoice.getInvoiceno());
			if (isEmpty(journal.getJournalNumber())) {
				journal.setJournalNumber(journalnumber);
			}
			Double taxableAmount = 0d;
			Double totalAmount = 0d;
			Double exemptedAmount = 0d;
			Double igstAmount = 0d;
			Double cgstAmount = 0d;
			Double sgstAmount = 0d;
			Double cessAmount = 0d;
			Double isdineligibleAmount = 0d;

			Double rigstAmount = 0d;
			Double rcgstAmount = 0d;
			Double rsgstAmount = 0d;
			Double rcessAmount = 0d;
			Double icsgstamountrcm = 0d;
			String itcinelg = "false";
			String itcType = "";
			Map<String,Item> ledgerNamemMap = Maps.newHashMap();
			List<String> ledgerkeys = Lists.newArrayList();
			if (isNotEmpty(invoice.getItems())) {
				for (Item item : invoice.getItems()) {
					if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
						if (isNotEmpty(item.getTaxablevalue())) {
							item.setTotal(item.getTaxablevalue());
						}
					}
					if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
						if (isNotEmpty(item.getIsdcessamount())) {
							item.setCessamount(item.getIsdcessamount());
						}
					}
					String ledgerName = "";
					if(isNotEmpty(item.getLedgerName())) {
						ledgerName = item.getLedgerName().trim();
					}else {
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
							ledgerName = AccountConstants.CASH;
						}else {
							ledgerName = AccountConstants.PURCHASES;
						}
					}
					if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
						item.setElg("");
					}
					if(!ledgerNamemMap.containsKey(ledgerName)) {
						item.setLedgerName(ledgerName);
						Item newitem = new Item();
						try {
							BeanUtils.copyProperties(newitem, item);
						}catch (IllegalAccessException | InvocationTargetException e) {
							log.error("ERROR in BeanUtils copy", e);
						}
						if(isNotEmpty(newitem.getTaxablevalue())) {
							Double taxable = newitem.getTaxablevalue();
							if ((isNotEmpty(newitem.getElg()) && ("no".equalsIgnoreCase(newitem.getElg()) || "pending".equalsIgnoreCase(newitem.getElg()))) || isEmpty(newitem.getElg())) {
								if (isNotEmpty(newitem.getIgstamount())) {
									taxable = taxable + newitem.getIgstamount();
								}
								if (isNotEmpty(newitem.getSgstamount())) {
									taxable = taxable + newitem.getSgstamount();
								}
								if (isNotEmpty(newitem.getCgstamount())) {
									taxable = taxable + newitem.getCgstamount();
								}
								if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
									if (isNotEmpty(newitem.getIsdcessamount())) {
										taxable = taxable + newitem.getIsdcessamount();
									}
								}else {
									if (isNotEmpty(newitem.getCessamount())) {
										taxable = taxable + newitem.getCessamount();
									}
								}
							}else {
								if (isNotEmpty(newitem.getIgstavltax()) && newitem.getIgstavltax() > 0d) {
									if (isNotEmpty(newitem.getElgpercent())) {
										if (isNotEmpty(newitem.getIgstamount())) {
											taxable = taxable + (((100 - newitem.getElgpercent()) / 100) * newitem.getIgstamount());
										}
									}
								}
								if (isNotEmpty(newitem.getSgstavltax())  && newitem.getSgstavltax() > 0d) {
									if (isNotEmpty(newitem.getElgpercent())) {
										if (isNotEmpty(newitem.getSgstamount())) {
											taxable = taxable + (((100 - newitem.getElgpercent()) / 100) * newitem.getSgstamount());
										}
									}
								}
								if (isNotEmpty(newitem.getCgstavltax())  && newitem.getCgstavltax() > 0d) {
									if (isNotEmpty(newitem.getElgpercent())) {
										if (isNotEmpty(newitem.getCgstamount())) {
											taxable = taxable + (((100 - newitem.getElgpercent()) / 100) * newitem.getCgstamount());
										}
									}
								}
								if (isNotEmpty(newitem.getCessavltax())  && newitem.getCessavltax() > 0d) {
									if (isNotEmpty(newitem.getElgpercent())) {
										if (isNotEmpty(newitem.getCessamount())) {
											taxable = taxable + (((100 - newitem.getElgpercent()) / 100) * newitem.getCessamount());
										}
									}
								}
							}
							newitem.setTaxablevalue(taxable);
						}
						ledgerNamemMap.put(ledgerName, newitem);
						ledgerkeys.add(ledgerName);
					}else {
						Item itm = ledgerNamemMap.get(ledgerName);
						if(isNotEmpty(itm.getIgstamount())) {
							if(isNotEmpty(item.getIgstamount())) {
								itm.setIgstamount(itm.getIgstamount() + item.getIgstamount());
							}
						}else {
							if(isNotEmpty(item.getIgstamount())) {
								itm.setIgstamount(item.getIgstamount());
							}
						}
						
						if(isNotEmpty(itm.getIgstavltax())) {
							if(isNotEmpty(item.getIgstavltax())) {
								itm.setIgstavltax(itm.getIgstavltax() + item.getIgstavltax());
							}
						}else {
							if(isNotEmpty(item.getIgstavltax())) {
								itm.setIgstavltax(item.getIgstavltax());
							}
						}
						
						if(isNotEmpty(itm.getCgstamount())) {
							if(isNotEmpty(item.getCgstamount())) {
								itm.setCgstamount(itm.getCgstamount() + item.getCgstamount());
							}
						}else {
							if(isNotEmpty(item.getCgstamount())) {
								itm.setCgstamount(item.getCgstamount());
							}
						}
						if(isNotEmpty(itm.getCgstavltax())) {
							if(isNotEmpty(item.getCgstavltax())) {
								itm.setCgstavltax(itm.getCgstavltax() + item.getCgstavltax());
							}
						}else {
							if(isNotEmpty(item.getCgstavltax())) {
								itm.setCgstavltax(item.getCgstavltax());
							}
						}
						if(isNotEmpty(itm.getSgstamount())) {
							if(isNotEmpty(item.getSgstamount())) {
								itm.setSgstamount(itm.getSgstamount() + item.getSgstamount());
							}
						}else {
							if(isNotEmpty(item.getSgstamount())) {
								itm.setSgstamount(item.getSgstamount());
							}
						}
						if(isNotEmpty(itm.getSgstavltax())) {
							if(isNotEmpty(item.getSgstavltax())) {
								itm.setSgstavltax(itm.getSgstavltax() + item.getSgstavltax());
							}
						}else {
							if(isNotEmpty(item.getSgstavltax())) {
								itm.setSgstavltax(item.getSgstavltax());
							}
						}
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
							if(isNotEmpty(itm.getCessamount())) {
								if(isNotEmpty(item.getIsdcessamount())) {
									itm.setCessamount(itm.getCessamount() + item.getIsdcessamount());
								}
							}else {
								if(isNotEmpty(item.getIsdcessamount())) {
									itm.setCessamount(item.getIsdcessamount());
								}
							}
						}else {
							if(isNotEmpty(itm.getCessamount())) {
								if(isNotEmpty(item.getCessamount())) {
									itm.setCessamount(itm.getCessamount() + item.getCessamount());
								}
							}else {
								if(isNotEmpty(item.getCessamount())) {
									itm.setCessamount(item.getCessamount());
								}
							}
						}
						if(isNotEmpty(itm.getCessavltax())) {
							if(isNotEmpty(item.getCessavltax())) {
								itm.setCessavltax(itm.getCessavltax() + item.getCessavltax());
							}
						}else {
							if(isNotEmpty(item.getCessavltax())) {
								itm.setCessavltax(item.getCessavltax());
							}
						}
						if(isNotEmpty(itm.getExmepted())) {
							if(isNotEmpty(item.getExmepted())) {
								itm.setExmepted(itm.getExmepted() + item.getExmepted());
							}
						}else {
							if(isNotEmpty(item.getExmepted())) {
								itm.setExmepted(item.getExmepted());
							}
						}
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
							if(isNotEmpty(itm.getTaxablevalue())) {
								if (isNotEmpty(item.getAdvadjustedAmount())) {
									itm.setTaxablevalue(itm.getTaxablevalue() + item.getAdvadjustedAmount());
								}
							}else {
								if (isNotEmpty(item.getAdvadjustedAmount())) {
									itm.setTaxablevalue(item.getAdvadjustedAmount());
								}
							}
						}else {
							if(isNotEmpty(itm.getTaxablevalue())) {
								if(isNotEmpty(item.getTaxablevalue())) {
									Double taxable = item.getTaxablevalue();
									if ((isNotEmpty(item.getElg()) && ("no".equalsIgnoreCase(item.getElg()) || "pending".equalsIgnoreCase(item.getElg()))) || isEmpty(item.getElg())) {
										if (isNotEmpty(item.getIgstamount())) {
											taxable = taxable + item.getIgstamount();
										}
										if (isNotEmpty(item.getSgstamount())) {
											taxable = taxable + item.getSgstamount();
										}
										if (isNotEmpty(item.getCgstamount())) {
											taxable = taxable + item.getCgstamount();
										}
										if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
											if (isNotEmpty(item.getIsdcessamount())) {
												taxable = taxable + item.getIsdcessamount();
											}
										}else {
											if (isNotEmpty(item.getCessamount())) {
												taxable = taxable + item.getCessamount();
											}
										}
									}else {	
										if (isNotEmpty(item.getIgstavltax())   && item.getIgstavltax() > 0d) {
											if (isNotEmpty(item.getElgpercent())) {
												if (isNotEmpty(item.getIgstamount())) {
													taxable = taxable + (((100 - item.getElgpercent()) / 100) * item.getIgstamount());
												}
											}
										}
										if (isNotEmpty(item.getSgstavltax())  && item.getSgstavltax() > 0d) {
											if (isNotEmpty(item.getElgpercent())) {
												if (isNotEmpty(item.getSgstamount())) {
													taxable = taxable + (((100 - item.getElgpercent()) / 100) * item.getSgstamount());
												}
											}
										}
										if (isNotEmpty(item.getCgstavltax())  && item.getCgstavltax() > 0d) {
											if (isNotEmpty(item.getElgpercent())) {
												if (isNotEmpty(item.getCgstamount())) {
													taxable = taxable + (((100 - item.getElgpercent()) / 100) * item.getCgstamount());
												}
											}
										}
										if (isNotEmpty(item.getCessavltax())  && item.getCessavltax() > 0d) {
											if (isNotEmpty(item.getElgpercent())) {
												if (isNotEmpty(item.getCessamount())) {
													taxable = taxable + (((100 - item.getElgpercent()) / 100) * item.getCessamount());
												}
											}
										}
									}
									itm.setTaxablevalue(itm.getTaxablevalue() + taxable);
								}
							}else {
								if(isNotEmpty(item.getTaxablevalue())) {
									Double taxable = item.getTaxablevalue();
									if ((isNotEmpty(item.getElg()) && ("no".equalsIgnoreCase(item.getElg()) || "pending".equalsIgnoreCase(item.getElg()))) || isEmpty(item.getElg())) {
										if (isNotEmpty(item.getIgstamount())) {
											taxable = taxable + item.getIgstamount();
										}
										if (isNotEmpty(item.getSgstamount())) {
											taxable = taxable + item.getSgstamount();
										}
										if (isNotEmpty(item.getCgstamount())) {
											taxable = taxable + item.getCgstamount();
										}
										if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
											if (isNotEmpty(item.getIsdcessamount())) {
												taxable = taxable + item.getIsdcessamount();
											}
										}else {
											if (isNotEmpty(item.getCessamount())) {
												taxable = taxable + item.getCessamount();
											}
										}
									}else {
										if (isNotEmpty(item.getIgstavltax()) && item.getIgstavltax() > 0d) {
											if (isNotEmpty(item.getElgpercent())) {
												if (isNotEmpty(item.getIgstamount())) {
													taxable = taxable + (((100 - item.getElgpercent()) / 100) * item.getIgstamount());
												}
											}
										}
										if (isNotEmpty(item.getSgstavltax())  && item.getSgstavltax() > 0d) {
											if (isNotEmpty(item.getElgpercent())) {
												if (isNotEmpty(item.getSgstamount())) {
													taxable = taxable + (((100 - item.getElgpercent()) / 100) * item.getSgstamount());
												}
											}
										}
										if (isNotEmpty(item.getCgstavltax())  && item.getCgstavltax() > 0d) {
											if (isNotEmpty(item.getElgpercent())) {
												if (isNotEmpty(item.getCgstamount())) {
													taxable = taxable + (((100 - item.getElgpercent()) / 100) * item.getCgstamount());
												}
											}
										}
										if (isNotEmpty(item.getCessavltax())  && item.getCessavltax() > 0d) {
											if (isNotEmpty(item.getElgpercent())) {
												if (isNotEmpty(item.getCessamount())) {
													taxable = taxable + (((100 - item.getElgpercent()) / 100) * item.getCessamount());
												}
											}
										}
									}
									itm.setTaxablevalue(taxable);
								}
							}
						}
						if(isNotEmpty(itm.getTotal())) {
							if(isNotEmpty(item.getTotal())) {
								itm.setTotal(itm.getTotal() + item.getTotal());
							}
						}else {
							if(isNotEmpty(item.getTotal())) {
								itm.setTotal(item.getTotal());
							}
						}
						ledgerNamemMap.put(ledgerName, itm);
					}
					
					
					if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.NIL)) {
						if (isNotEmpty(item.getTotal())) {
							totalAmount = totalAmount + item.getTotal();
						}
					} else if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
						if (isNotEmpty(item.getTotal())) {
							totalAmount = totalAmount + item.getTotal();
						}
						if (isNotEmpty(item.getIgstamount())) {
							igstAmount = igstAmount + item.getIgstamount();
						}
						if (isNotEmpty(item.getCgstamount())) {
							cgstAmount = cgstAmount + item.getCgstamount();
						}
						if (isNotEmpty(item.getSgstamount())) {
							sgstAmount = sgstAmount + item.getSgstamount();
						}
						if (isNotEmpty(item.getCessamount())) {
							cessAmount = cessAmount + item.getCessamount();
						}
					} else if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
						if (isNotEmpty(item.getTotal())) {
							totalAmount = totalAmount + item.getTotal();
						}
						if (isNotEmpty(item.getIgstamount())) {
							igstAmount = igstAmount + item.getIgstamount();
						}
						if (isNotEmpty(item.getCgstamount())) {
							cgstAmount = cgstAmount + item.getCgstamount();
						}
						if (isNotEmpty(item.getSgstamount())) {
							sgstAmount = sgstAmount + item.getSgstamount();
						}
						if (isNotEmpty(item.getCessamount())) {
							cessAmount = cessAmount + item.getCessamount();
						}
					} else if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ITC_REVERSAL)) {
						if (isNotEmpty(item.getTotal())) {
							totalAmount = totalAmount + item.getTotal();
						}
						if (isNotEmpty(item.getIgstamount())) {
							igstAmount = igstAmount + item.getIgstamount();
						}
						if (isNotEmpty(item.getCgstamount())) {
							cgstAmount = cgstAmount + item.getCgstamount();
						}
						if (isNotEmpty(item.getSgstamount())) {
							sgstAmount = sgstAmount + item.getSgstamount();
						}
						if (isNotEmpty(item.getCessamount())) {
							cessAmount = cessAmount + item.getCessamount();
						}else if(isNotEmpty(item.getIsdcessamount())) {
							cessAmount = cessAmount + item.getIsdcessamount();
						}
					} else if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
						if (isNotEmpty(item.getIsdType())
								&& ("Eligible - Credit distributed as".equalsIgnoreCase(item.getIsdType()) || "Eligible - Credit distributed".equalsIgnoreCase(item.getIsdType()))) {
							if (isNotEmpty(item.getTotal())) {
								totalAmount = totalAmount + item.getTotal();
							}
							if (isNotEmpty(item.getIgstamount())) {
								igstAmount = igstAmount + item.getIgstamount();
							}
							if (isNotEmpty(item.getCgstamount())) {
								cgstAmount = cgstAmount + item.getCgstamount();
							}
							if (isNotEmpty(item.getSgstamount())) {
								sgstAmount = sgstAmount + item.getSgstamount();
							}
							if (isNotEmpty(item.getIsdcessamount())) {
								cessAmount = cessAmount + item.getIsdcessamount();
							}
						}
						if (isNotEmpty(item.getIsdType())
								&& ("Ineligible - Credit distributed as".equalsIgnoreCase(item.getIsdType()) || "Ineligible - Credit distributed".equalsIgnoreCase(item.getIsdType()))) {
							if (isNotEmpty(item.getTotal())) {
								totalAmount = totalAmount + item.getTotal();
							}
							if (isNotEmpty(item.getIgstamount())) {
								isdineligibleAmount = isdineligibleAmount + item.getIgstamount();
							}
							if (isNotEmpty(item.getCgstamount())) {
								isdineligibleAmount = isdineligibleAmount + item.getCgstamount();
							}
							if (isNotEmpty(item.getSgstamount())) {
								isdineligibleAmount = isdineligibleAmount + item.getSgstamount();
							}
							if (isNotEmpty(item.getIsdcessamount())) {
								isdineligibleAmount = isdineligibleAmount + item.getIsdcessamount();
							}
						}
					} else {

						if (isNotEmpty(item.getElg())) {
							itcType = item.getElg();
							if ("no".equalsIgnoreCase(itcType) || "pending".equalsIgnoreCase(itcType)) {
								itcinelg = "true";
								if (isNotEmpty(item.getTaxablevalue())) {
									taxableAmount = taxableAmount + item.getTaxablevalue();
								}
								if (isNotEmpty(item.getExmepted())) {
									Double qty = item.getQuantity() == null ? 1 : item.getQuantity();
									exemptedAmount = exemptedAmount + (item.getExmepted()*qty);
								}
								if (isNotEmpty(item.getTotal())) {
									totalAmount = totalAmount + item.getTotal();
								}
								if (isNotEmpty(item.getIgstamount())) {
									igstAmount = igstAmount + item.getIgstamount();
									taxableAmount = taxableAmount + item.getIgstamount();
									icsgstamountrcm = icsgstamountrcm + item.getIgstamount();
								}
								if (isNotEmpty(item.getSgstamount())) {
									sgstAmount = sgstAmount + item.getSgstamount();
									taxableAmount = taxableAmount + item.getSgstamount();
									icsgstamountrcm = icsgstamountrcm + item.getSgstamount();
								}
								if (isNotEmpty(item.getCgstamount())) {
									cgstAmount = cgstAmount + item.getCgstamount();
									taxableAmount = taxableAmount + item.getCgstamount();
									icsgstamountrcm = icsgstamountrcm + item.getCgstamount();
								}
								if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
									if (isNotEmpty(item.getIsdcessamount())) {
										cessAmount = cessAmount + item.getIsdcessamount();
										taxableAmount = taxableAmount + item.getIsdcessamount();
										icsgstamountrcm = icsgstamountrcm + item.getIsdcessamount();
									}
								}else {
									if (isNotEmpty(item.getCessamount())) {
										cessAmount = cessAmount + item.getCessamount();
										taxableAmount = taxableAmount + item.getCessamount();
										icsgstamountrcm = icsgstamountrcm + item.getCessamount();
									}
								}
							} else {
								itcinelg = "false";
								if (isNotEmpty(item.getTaxablevalue())) {
									taxableAmount = taxableAmount + item.getTaxablevalue();
								}
								if (isNotEmpty(item.getExmepted())) {
									Double qty = item.getQuantity() == null ? 1 : item.getQuantity();
									exemptedAmount = exemptedAmount + (item.getExmepted()*qty);
								}
								if (isNotEmpty(item.getTotal())) {
									totalAmount = totalAmount + item.getTotal();
								}
								if (isNotEmpty(item.getIgstavltax())) {
									igstAmount = igstAmount + item.getIgstavltax();
									icsgstamountrcm = icsgstamountrcm + item.getIgstavltax();
									if (isNotEmpty(item.getElgpercent())) {
										if (isNotEmpty(item.getIgstamount())) {
											taxableAmount = taxableAmount + (((100 - item.getElgpercent()) / 100) * item.getIgstamount());
										}
									}
								}
								if (isNotEmpty(item.getIgstamount())) {
									rigstAmount = rigstAmount + item.getIgstamount();
								}
								if (isNotEmpty(item.getSgstavltax())) {
									sgstAmount = sgstAmount + item.getSgstavltax();
									icsgstamountrcm = icsgstamountrcm + item.getSgstavltax();
									if (isNotEmpty(item.getElgpercent())) {
										if (isNotEmpty(item.getSgstamount())) {
											taxableAmount = taxableAmount + (((100 - item.getElgpercent()) / 100) * item.getSgstamount());
										}
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									rsgstAmount = rsgstAmount + item.getSgstamount();
								}
								if (isNotEmpty(item.getCgstavltax())) {
									cgstAmount = cgstAmount + item.getCgstavltax();
									icsgstamountrcm = icsgstamountrcm + item.getCgstavltax();
									if (isNotEmpty(item.getElgpercent())) {
										if (isNotEmpty(item.getCgstamount())) {
											taxableAmount = taxableAmount + (((100 - item.getElgpercent()) / 100) * item.getCgstamount());
										}
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									rcgstAmount = rcgstAmount + item.getCgstamount();
								}
								
								if (isNotEmpty(item.getCessavltax())) {
									cessAmount = cessAmount + item.getCessavltax();
									icsgstamountrcm = icsgstamountrcm + item.getCessavltax();
									if (isNotEmpty(item.getElgpercent())) {
										if (isNotEmpty(item.getCessamount())) {
											taxableAmount = taxableAmount + (((100 - item.getElgpercent()) / 100) * item.getCessamount());
										}
									}
								}
								if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
									if (isNotEmpty(item.getIsdcessamount())) {
										rcessAmount = rcessAmount + item.getIsdcessamount();
									}
								}else {
									if (isNotEmpty(item.getCessamount())) {
										rcessAmount = rcessAmount + item.getCessamount();
									}
								}
							}
						} else {
							itcinelg = "true";
							if (isNotEmpty(item.getTaxablevalue())) {
								taxableAmount = taxableAmount + item.getTaxablevalue();
							}
							if (isNotEmpty(item.getExmepted())) {
								Double qty = item.getQuantity() == null ? 1 : item.getQuantity();
								exemptedAmount = exemptedAmount + (item.getExmepted()*qty);
							}
							if (isNotEmpty(item.getTotal())) {
								totalAmount = totalAmount + item.getTotal();
							}
							if (isNotEmpty(item.getIgstavltax())) {

								igstAmount = igstAmount + item.getIgstavltax();
								icsgstamountrcm = icsgstamountrcm + item.getIgstavltax();
								if (isNotEmpty(item.getElgpercent())) {
									if (isNotEmpty(item.getIgstamount())) {
										taxableAmount = taxableAmount + (((100 - item.getElgpercent()) / 100) * item.getIgstamount());
									}
								}
							}
							if (isNotEmpty(item.getIgstamount())) {
								rigstAmount = rigstAmount + item.getIgstamount();
							}
							if (isNotEmpty(item.getSgstavltax())) {
								sgstAmount = sgstAmount + item.getSgstavltax();
								icsgstamountrcm = icsgstamountrcm + item.getSgstavltax();
								if (isNotEmpty(item.getElgpercent())) {
									if (isNotEmpty(item.getSgstamount())) {
										taxableAmount = taxableAmount + (((100 - item.getElgpercent()) / 100) * item.getSgstamount());
									}
								}
							}
							if (isNotEmpty(item.getSgstamount())) {
								rsgstAmount = rsgstAmount + item.getSgstamount();
							}
							if (isNotEmpty(item.getCgstavltax())) {
								cgstAmount = cgstAmount + item.getCgstavltax();
								icsgstamountrcm = icsgstamountrcm + item.getCgstavltax();
								if (isNotEmpty(item.getElgpercent())) {
									if (isNotEmpty(item.getCgstamount())) {
										taxableAmount = taxableAmount + (((100 - item.getElgpercent()) / 100) * item.getCgstamount());
									}
								}
							}
							if (isNotEmpty(item.getCgstamount())) {
								rcgstAmount = rcgstAmount + item.getCgstamount();
							}
							
							if (isNotEmpty(item.getCessavltax())) {
								cessAmount = cessAmount + item.getCessavltax();
								icsgstamountrcm = icsgstamountrcm + item.getCessavltax();
								if (isNotEmpty(item.getElgpercent())) {
									if (isNotEmpty(item.getCessamount())) {
										taxableAmount = taxableAmount + (((100 - item.getElgpercent()) / 100) * item.getCessamount());
									}
								}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
								if (isNotEmpty(item.getIsdcessamount())) {
									rcessAmount = rcessAmount + item.getIsdcessamount();
								}
							}else {
								if (isNotEmpty(item.getCessamount())) {
									rcessAmount = rcessAmount + item.getCessamount();
								}
							}
						}

					}
				}
			}
			String revchargetype = "";
			if (isNotEmpty(invoice.getRevchargetype())) {
				revchargetype = invoice.getRevchargetype();
			}
			journal.setItcinelg(itcinelg);
			if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.NIL)) {
				double tcsamt,tdsamt = 0d;
				tdsamt = invoice.getTdsAmount() == null ? 0d : invoice.getTdsAmount();
				tcsamt = invoice.getTcstdsAmount() == null ? 0d : invoice.getTcstdsAmount();
				journal.setTdsamount(tdsamt);
				journal.setTdstcsAmount(tcsamt);
				journal.setCustomerorSupplierAccount(totalAmount + tcsamt - tdsamt);
				journal.setSalesorPurchases(totalAmount);
			} else if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
				double tcsamt,tdsamt = 0d;
				tdsamt = invoice.getTdsAmount() == null ? 0d : invoice.getTdsAmount();
				tcsamt = invoice.getTcstdsAmount() == null ? 0d : invoice.getTcstdsAmount();
				journal.setTdsamount(tdsamt);
				journal.setTdstcsAmount(tcsamt);
				journal.setCustomerorSupplierAccount(totalAmount + tcsamt - tdsamt);
				journal.setSalesorPurchases(totalAmount);
				if ("Reverse".equalsIgnoreCase(revchargetype)) {
					journal.setRcmorinterorintra("RCM");
					if (isIntraState) {
						journal.setInterorintra("Intra");
					} else {
						journal.setInterorintra("Inter");
					}
				}else {
					if (isIntraState) {
						journal.setRcmorinterorintra("Intra");
					} else {
						journal.setRcmorinterorintra("Inter");
					}
				}
				journal.setIgstamount(igstAmount);
				journal.setSgstamount(sgstAmount);
				journal.setCgstamount(cgstAmount);
				journal.setCessAmount(cessAmount);
			} else if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
				double tcsamt,tdsamt = 0d;
				tdsamt = invoice.getTdsAmount() == null ? 0d : invoice.getTdsAmount();
				tcsamt = invoice.getTcstdsAmount() == null ? 0d : invoice.getTcstdsAmount();
				journal.setTdsamount(tdsamt);
				journal.setTdstcsAmount(tcsamt);
				journal.setCustomerorSupplierAccount(totalAmount + tcsamt - tdsamt);
				journal.setSalesorPurchases(totalAmount);
				journal.setIgstamount(igstAmount);
				journal.setSgstamount(sgstAmount);
				journal.setCgstamount(cgstAmount);
				journal.setCessAmount(cessAmount);
			} else if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_SERVICES)) {
				double tcsamt,tdsamt = 0d;
				tdsamt = invoice.getTdsAmount() == null ? 0d : invoice.getTdsAmount();
				tcsamt = invoice.getTcstdsAmount() == null ? 0d : invoice.getTcstdsAmount();
				journal.setTdsamount(tdsamt);
				journal.setTdstcsAmount(tcsamt);
				journal.setCustomerorSupplierAccount(taxableAmount + tcsamt - tdsamt);
				journal.setSalesorPurchases(taxableAmount);
				journal.setExemptedamount(exemptedAmount);
				journal.setIcsgstamountrcm(icsgstamountrcm);
				journal.setToicsgstamountrcm(icsgstamountrcm);
				journal.setIgstamount(igstAmount);
				journal.setSgstamount(sgstAmount);
				journal.setCgstamount(cgstAmount);
				journal.setCessAmount(cessAmount);
				if (isIntraState) {
					journal.setInterorintra("Intra");
				} else {
					journal.setInterorintra("Inter");
				}
			} else if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ITC_REVERSAL)) {
				journal.setCustomerorSupplierAccount(totalAmount);
				journal.setSalesorPurchases(totalAmount);
				journal.setExemptedamount(exemptedAmount);
				journal.setIgstamount(igstAmount);
				journal.setSgstamount(sgstAmount);
				journal.setCgstamount(cgstAmount);
				journal.setCessAmount(cessAmount);
				if (isIntraState) {
					journal.setInterorintra("Intra");
				} else {
					journal.setInterorintra("Inter");
				}
			} else if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
				journal.setCustomerorSupplierAccount(totalAmount);
				journal.setSalesorPurchases(totalAmount);
				journal.setExemptedamount(exemptedAmount);
				journal.setIgstamount(igstAmount);
				journal.setSgstamount(sgstAmount);
				journal.setCgstamount(cgstAmount);
				journal.setCessAmount(cessAmount);
				journal.setIsdineligiblecredit(isdineligibleAmount);
				if(isNotEmpty(((PurchaseRegister)invoice).getIsd()) && isNotEmpty(((PurchaseRegister)invoice).getIsd().get(0))
						&& isNotEmpty(((PurchaseRegister)invoice).getIsd().get(0).getDoclist())  && isNotEmpty(((PurchaseRegister)invoice).getIsd().get(0).getDoclist().get(0)) 
						 && isNotEmpty(((PurchaseRegister)invoice).getIsd().get(0).getDoclist().get(0).getIsdDocty())) {
					String isddoctype = ((PurchaseRegister)invoice).getIsd().get(0).getDoclist().get(0).getIsdDocty();
					journal.setInterorintra(isddoctype);
				}
			} else if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
				journal.setSalesorPurchases(taxableAmount);
				journal.setExemptedamount(exemptedAmount);
				journal.setIgstamount(igstAmount);
				journal.setSgstamount(sgstAmount);
				journal.setCgstamount(cgstAmount);
				journal.setCessAmount(cessAmount);
				double tcsamt,tdsamt = 0d;
				tdsamt = invoice.getTdsAmount() == null ? 0d : invoice.getTdsAmount();
				tcsamt = invoice.getTcstdsAmount() == null ? 0d : invoice.getTcstdsAmount();
				journal.setTdsamount(tdsamt);
				journal.setTdstcsAmount(tcsamt);
				journal.setCustomerorSupplierAccount(totalAmount + tcsamt - tdsamt);
				if (isIntraState) {
					journal.setRcmorinterorintra("Intra");
				} else {
					journal.setRcmorinterorintra("Inter");
				}
			} else {
				if ("Reverse".equalsIgnoreCase(revchargetype)) {
					if ("no".equalsIgnoreCase(itcType) || "Pending".equalsIgnoreCase(itcType)) {
						double tcsamt,tdsamt = 0d;
						tdsamt = invoice.getTdsAmount() == null ? 0d : invoice.getTdsAmount();
						tcsamt = invoice.getTcstdsAmount() == null ? 0d : invoice.getTcstdsAmount();
						journal.setTdsamount(tdsamt);
						journal.setTdstcsAmount(tcsamt);
						journal.setCustomerorSupplierAccount(taxableAmount + tcsamt - tdsamt);
						journal.setSalesorPurchases(totalAmount);
					} else {
						double tcsamt,tdsamt = 0d;
						tdsamt = invoice.getTdsAmount() == null ? 0d : invoice.getTdsAmount();
						tcsamt = invoice.getTcstdsAmount() == null ? 0d : invoice.getTcstdsAmount();
						journal.setTdsamount(tdsamt);
						journal.setTdstcsAmount(tcsamt);
						journal.setCustomerorSupplierAccount(totalAmount + tcsamt - tdsamt);
						journal.setSalesorPurchases(taxableAmount);
					}
					journal.setIcsgstamountrcm(icsgstamountrcm);
					journal.setToicsgstamountrcm(icsgstamountrcm);
					journal.setRcmorinterorintra("RCM");
					journal.setRigstamount(rigstAmount);
					journal.setRcgstamount(rcgstAmount);
					journal.setRsgstamount(rsgstAmount);
					journal.setRcessamount(rcessAmount);
					journal.setIgstamount(igstAmount);
					journal.setSgstamount(sgstAmount);
					journal.setCgstamount(cgstAmount);
					journal.setCessAmount(cessAmount);
					journal.setExemptedamount(exemptedAmount);
					if (isIntraState) {
						journal.setInterorintra("Intra");
					} else {
						journal.setInterorintra("Inter");
					}
				} else {
					if (isIntraState) {
						journal.setRcmorinterorintra("Intra");
					} else {
						journal.setRcmorinterorintra("Inter");
					}
					journal.setIgstamount(igstAmount);
					journal.setSgstamount(sgstAmount);
					journal.setCgstamount(cgstAmount);
					journal.setCessAmount(cessAmount);
					journal.setExemptedamount(exemptedAmount);
					double tcsamt,tdsamt = 0d;
					tdsamt = invoice.getTdsAmount() == null ? 0d : invoice.getTdsAmount();
					tcsamt = invoice.getTcstdsAmount() == null ? 0d : invoice.getTcstdsAmount();
					journal.setSalesorPurchases(taxableAmount);
					journal.setTdsamount(tdsamt);
					journal.setTdstcsAmount(tcsamt);
					journal.setCustomerorSupplierAccount(totalAmount + tcsamt - tdsamt);
				}
			}
			List<Item> itemlist = Lists.newArrayList();
			for(String key : ledgerkeys) {
				Item itm = ledgerNamemMap.get(key);
				itemlist.add(itm);
			}
			journal.setItems(itemlist);
			if(invoice != null && invoice.getB2b() != null && invoice.getB2b().get(0) != null && invoice.getB2b().get(0).getCtin()!= null) {
				journal.setCtin(invoice.getB2b().get(0).getCtin().trim());
				String pancategory = getPanCategory(invoice.getB2b().get(0).getCtin().trim());
				if(isNotEmpty(pancategory)) {
					journal.setPancategory(pancategory);
				}else {
					journal.setPancategory(null);
				}
			}
			if(invoice != null && invoice.getSection() != null) {
				journal.setTcssection(invoice.getSection());
			}
			if(invoice != null && invoice.getTcstdspercentage() != null) {
				journal.setTcspercentage(invoice.getTcstdspercentage()+"");
			}
			
			if(invoice != null && invoice.getTdsSection() != null) {
				journal.setTdssection(invoice.getTdsSection());
			}
			if(invoice != null && invoice.getTdspercentage() != null) {
				journal.setTdspercentage(invoice.getTdspercentage()+"");
			}
			if(invoice != null && invoice.getDueDate() != null) {
				journal.setDueDate(invoice.getDueDate());
			}else if(invoice != null && invoice.getDateofinvoice() != null) {
				journal.setDueDate(invoice.getDateofinvoice());
			}
			boolean invoiceamountflag = invoice.getTotalamount() != null ?  invoice.getTotalamount() > 0 ? true : false : false;
			if(invoiceamountflag) {
				journal.setInvoiceamount(invoice.getTotalamount());
			}
			return journal;
		}
	}
	
	public String getPanCategory(String gstno) {
		String category = "";
		if(isNotEmpty(gstno) && gstno.length() >= 6) {
			category = gstno.substring(5, 6).toUpperCase();
		}
		switch(category) {
			case "A": category = AccountConstants.PAN_A;
			break;
			case "B": category = AccountConstants.PAN_B;
			break;
			case "C": category = AccountConstants.PAN_C;
			break;
			case "F": category = AccountConstants.PAN_F;
			break;
			case "G": category = AccountConstants.PAN_G;
			break;
			case "H": category = AccountConstants.PAN_H;
			break;
			case "J": category = AccountConstants.PAN_J;
			break;
			case "L": category = AccountConstants.PAN_L;
			break;
			case "P": category = AccountConstants.PAN_P;
			break;
			case "T": category = AccountConstants.PAN_T;
			break;
			default : category = "";
			break;
		}
		return category;
	}
}
