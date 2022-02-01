package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.usermanagement.runtime.accounting.util.AccountingJournalsUtils;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTRExports;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;

@Component
public class InvoiceSaveUtils {
	private static final Logger logger = LogManager.getLogger(InvoiceSaveUtils.class.getName());
	private static final String CLASSNAME = "InvoiceSaveUtils::";
	@Autowired	private ClientService clientService;
	@Autowired	private Anx1Service anx1Service;
	@Autowired	private UserRepository userRepository;
	@Autowired	private GSTR1Repository gstr1Repository;
	@Autowired private AccountingJournalsUtils accountingJournalsUtils;
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	public void saveInvoice(InvoiceParent invoice, String returntype, String usertype, int month, int year,
			ModelMap model) throws Exception {
		final String method = "saveInvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		InvoiceParent oldinvoice = null;
		if (isNotEmpty(invoice.getDateofinvoice())) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(invoice.getDateofinvoice());
			month = cal.get(Calendar.MONTH) + 1;
			year = cal.get(Calendar.YEAR);
		}
		String strMonth = month < 10 ? "0" + month : month + "";
		invoice.setFp(strMonth + year);
		if(isNotEmpty(invoice.getNotes())) {
			String notes = invoice.getNotes();
			notes = notes.substring(1);
			invoice.setNotes(notes);
		}
		updateModel(model, invoice.getUserid(), invoice.getFullname(), usertype, month, year);
		User user = userRepository.findOne(invoice.getUserid());
		Client client = clientService.findById(invoice.getClientid());
		model.addAttribute("client", client);
		model.addAttribute("returntype", returntype);
		model.addAttribute("lGSTReturnsSummury", clientService.getGSTReturnsSummary(client, month, year, false));

		if (isEmpty(invoice.getBranch()) && isNotEmpty(client.getBranches()) && client.getBranches().size() == 1) {
			invoice.setBranch(client.getBranches().get(0).getCode());
		}
		if (isEmpty(invoice.getVertical()) && isNotEmpty(client.getVerticals()) && client.getVerticals().size() == 1) {
			invoice.setVertical(client.getVerticals().get(0).getCode());
		}
		if(isNotEmpty(invoice.getStrAmendment()) && invoice.getStrAmendment().equals("true")) {
			//invoice.setAmendment(true);
			if(isNotEmpty(invoice.getInvtype()) && !invoice.getInvtype().endsWith("A")) {
				InvoiceParent inv = gstr1Repository.findByAmendmentRefId(invoice.getId().toString());
				if(isNotEmpty(inv)) {
					gstr1Repository.delete((GSTR1)inv);
				}
				oldinvoice = gstr1Repository.findOne(invoice.getId().toString());
				if(invoice.getInvtype().equals(MasterGSTConstants.B2B)) {
					invoice.setInvtype(MasterGSTConstants.B2BA);
				} else if(invoice.getInvtype().equals(MasterGSTConstants.B2CL)) {
					invoice.setInvtype(MasterGSTConstants.B2CLA);
				} else if(invoice.getInvtype().equals(MasterGSTConstants.B2C)) {
					invoice.setInvtype(MasterGSTConstants.B2CSA);
				} else if(invoice.getInvtype().equals(MasterGSTConstants.ADVANCES)) {
					invoice.setInvtype(MasterGSTConstants.ATA);
				} else if(invoice.getInvtype().equals(MasterGSTConstants.ATPAID)) {
					invoice.setInvtype(MasterGSTConstants.TXPA);
				} else if(invoice.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
					invoice.setInvtype(MasterGSTConstants.EXPA);
				} else if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
					invoice.setInvtype(MasterGSTConstants.CDNA);
				} else if(invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
					invoice.setInvtype(MasterGSTConstants.CDNURA);
				}
			}
		}

		boolean isIntraState = true;
		if (isNotEmpty(invoice.getStatename())) {
			if (!invoice.getStatename().equals(client.getStatename())) {
				isIntraState = false;
			}
		}
		if (invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)) {
			List<GSTRExports> exp = invoice.getExp();
			if (isNotEmpty(exp)) {
				GSTRExports exps = exp.get(0);
				if (isNotEmpty(exps)) {
					if (exps.getExpTyp().equals("WPAY")) {
						isIntraState = false;
					} else {
						isIntraState = true;
					}
				}
			}
		}else if (invoice.getInvtype().equals(MasterGSTConstants.B2B) || invoice.getInvtype().equals(MasterGSTConstants.B2BA) || invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR) || invoice.getInvtype().equals(MasterGSTConstants.CDNA) || invoice.getInvtype().equals(MasterGSTConstants.CDNURA)) {
			if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
				String invtyp = invoice.getB2b().get(0).getInv().get(0).getInvTyp();
				if (invtyp.equals("SEWP") || invtyp.equals("SEWPC") || invtyp.equals("SEWOP")  || invtyp.equals("CBW")) {
					isIntraState = false;
				}
			}
			if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR) || invoice.getInvtype().equals(MasterGSTConstants.CDNA) || invoice.getInvtype().equals(MasterGSTConstants.CDNURA)) {
				if (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())) {
					isIntraState = false;
					if(isNotEmpty(invoice.getCdnur()) && isNotEmpty(invoice.getCdnur().get(0)) && isNotEmpty(invoice.getCdnur().get(0).getTyp())) {
						if("B2CS".equalsIgnoreCase(invoice.getCdnur().get(0).getTyp())) {
							if (isNotEmpty(invoice.getStatename())) {
								if (!invoice.getStatename().equals(client.getStatename())) {
									isIntraState = false;
								}else {
									isIntraState = true;
								}
							}
						}
					}
				}
			}
		}
		
		InvoiceParent invoiceForJournal;
		if (isEmpty(returntype) || returntype.equals(GSTR1)) {
			invoiceForJournal = clientService.saveSalesInvoice(invoice, oldinvoice, isIntraState);
		} else {
			invoiceForJournal = clientService.saveGSTRInvoice(invoice, returntype, isIntraState);
		}
		if (!returntype.equals(MasterGSTConstants.EWAYBILL)) {
			if (isEmpty(returntype) || returntype.equals(GSTR1)) {
				saveNewJournalEntries(invoiceForJournal,invoice.getClientid(),invoice.getUserid(),returntype,isIntraState);
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	@Async
	public void saveJournalEntries(InvoiceParent invoiceForJournal, String clientid, String returntype,boolean isIntraState) {
		clientService.saveJournalInvoice(invoiceForJournal,clientid,returntype,isIntraState);
	}
	
	@Async
	public void saveNewJournalEntries(InvoiceParent invoiceForJournal, String clientid,String userid, String returntype,boolean isIntraState) {
		accountingJournalsUtils.createJournalsEntries(userid,clientid,returntype,invoiceForJournal,isIntraState);
	}
	
	@Async
	public void saveAnx1(InvoiceParent invoiceForJournal, String clientid, String returntype,boolean isIntraState) {
		anx1Service.saveAnx1Invoice(invoiceForJournal,clientid,returntype,isIntraState);
	}
	
	
}
