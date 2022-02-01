package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.GSTR3B;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.jxls.template.SimpleExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.accounting.domain.JournalEntrie;
import com.mastergst.usermanagement.runtime.dao.Gstr1SortDao;
import com.mastergst.usermanagement.runtime.dao.JournalsDao;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.AccountingJournalPaymentItems;
import com.mastergst.usermanagement.runtime.domain.AccountingJournalVoucherItems;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CompanyRole;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTReturnSummary;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.PaymentItems;
import com.mastergst.usermanagement.runtime.domain.Payments;
import com.mastergst.usermanagement.runtime.domain.Permission;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;
import com.mastergst.usermanagement.runtime.domain.TotalJournalAmount;
import com.mastergst.usermanagement.runtime.repository.AccountingJournalRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyRoleRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;
import com.mastergst.usermanagement.runtime.repository.LedgerRepository;
import com.mastergst.usermanagement.runtime.repository.OtherConfigurationRepository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;
import com.mastergst.usermanagement.runtime.repository.RecordPaymentsRepository;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ImportMapperService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.support.Utility;

@Controller
public class JournalController {

	private static final Logger logger = LogManager.getLogger(JournalController.class.getName());
	private static final String CLASSNAME = "JournalController::";
	
	@Autowired	private UserService userService;
	@Autowired	private ClientService clientService;
	@Autowired	private ImportMapperService importMapperService;
	@Autowired	private ProfileService profileService;
	@Autowired	private ConfigService configService;
	@Autowired	AccountingJournalRepository accountingJournalRepository;
	@Autowired	OtherConfigurationRepository otherConfigurationRepository;
	@Autowired	private LedgerRepository ledgerRepository;
	@Autowired	RecordPaymentsRepository recordPaymentsRepository;
	@Autowired	GSTR1Repository gstr1Repository;
	@Autowired	PurchaseRegisterRepository purchaseRepository;
	@Autowired	CompanyRoleRepository companyRoleRepository;
	@Autowired	private JournalsDao journalsDao;
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	
	
	
	@RequestMapping(value = "/journaldetails/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String journalDetails(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@RequestParam("type") String type,	@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,
			HttpServletRequest request) throws Exception {
		final String method = "allInvoiceView::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		HttpSession session = request.getSession(false);
		String strMonth = month < 10 ? "0" + month : month + "";
		if (isNotEmpty(session) && isNotEmpty(session.getAttribute("error.xls"))) {
			File file = (File) session.getAttribute("error.xls");
			try {
				file.delete();
			} catch (Exception e) {
			}
			session.removeAttribute("error.xls");
		}
		updateModel(model, id, fullname, usertype, month, year);
		model.addAttribute("client_id", clientid);
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		if("Monthly".equalsIgnoreCase(type)) {
			cal.set(year, month - 1, 0, 23, 59, 59);
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(year, month, 0, 23, 59, 59);
			endDate = new java.util.Date(cal.getTimeInMillis());
		}else {
			cal.set(year, 3, 0, 23, 59, 59);
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(year+1, 3, 0, 0, 0, 0);
			endDate = new java.util.Date(cal.getTimeInMillis());
		}
		
		List<AccountingJournal> journals = accountingJournalRepository.findByClientIdAndDateofinvoiceBetween(clientid,stDate,endDate);
		
		if(isNotEmpty(journals)) {
			for(AccountingJournal journal : journals) {
				if(isNotEmpty(journal)) {
					if(isNotEmpty(journal.getReturnType())) {
						if("Payment Receipt".equals(journal.getReturnType()) || "Payment".equals(journal.getReturnType())) {
							List<String> returntype = Lists.newArrayList();
							if("Payment Receipt".equals(journal.getReturnType())) {
								returntype.add("SalesRegister");
								returntype.add("GSTR1");
							}else {
								returntype.add("PurchaseRegister");
								returntype.add("GSTR2");
								returntype.add("Purchase Register");
							}
							
							if(isEmpty(journal.getInvoiceId())) {
								Payments payment = recordPaymentsRepository.findByClientidAndVoucherNumberAndReturntypeIn(clientid, journal.getInvoiceNumber(),returntype);
								if(isNotEmpty(payment)) {
									journal.setInvoiceId(payment.getId().toString());
								}
							}
						}
						if(isNotEmpty(journal.getVendorName())) {
							String vendorName = journal.getVendorName();
							if(vendorName.contains("\r\n")) {
								vendorName = vendorName.replaceAll("\r\n", "");
							}
							journal.setVendorName(vendorName);
						}
						if(isNotEmpty(journal.getLedgerName())) {
							String ledgerName = journal.getLedgerName();
							if(ledgerName.contains("\r\n")) {
								ledgerName = ledgerName.replaceAll("\r\n", "");
							}
							journal.setLedgerName(ledgerName);
						}
					}
				}

			}
		}
		model.addAttribute("journals", journals);
		OtherConfigurations othrconfigs = otherConfigurationRepository.findByClientid(clientid);
		model.addAttribute("othrconfigs", othrconfigs);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(NullUtil.isNotEmpty(companyUser)) {
				List<String> clntids = companyUser.getCompany();
				if(isNotEmpty(clntids) && clntids.size() > 0){
					if(clntids.contains(clientid)){
						model.addAttribute("companyUser", companyUser);
					}
				}
				if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
					model.addAttribute("addClient", companyUser.getAddclient());
				}else {
					model.addAttribute("addClient", "true");
				}
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		model.addAttribute("type", type);

		Client client = clientService.findById(clientid);
		
		/*if(returntype.equals(PURCHASE_REGISTER) || returntype.equals(GSTR2)){
			client.setFilingOption("Monthly");
		}*/
		model.addAttribute("client", client);
		model.addAttribute("user", user);
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(id, clientid);
		model.addAttribute("mappers", mappers);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(null, client, month, year, false));
		logger.debug(CLASSNAME + method + END);
		return "journal/journalsdetails";
	}
	
	private List<GSTReturnSummary> getGSTReturnsSummary(Pageable pageable, final Client client, int month, int year, boolean updateValues)
			throws Exception {
		List<GSTReturnSummary> returnsSummuryList = client.getReturnsSummary();
		if (isEmpty(returnsSummuryList)) {
			List<String> returntypes = configService.getDealerACL(client.getDealertype());
			returntypes = returntypes.stream().filter(String -> String.startsWith("GSTR")).collect(Collectors.toList());
			logger.debug(CLASSNAME + " getGSTReturnsSummary:: returntypes\t" + returntypes.toString());
			Calendar cal = Calendar.getInstance();
			Date date = null;
			if (month >= 0 && year >= 0) {
				cal.set(year, month - 1, 1);
				date = new java.util.Date(cal.getTimeInMillis());
			}
			returnsSummuryList = clientService.getGSTReturnsSummary(returntypes, date);
		}
		if (updateValues) {
			for (GSTReturnSummary summaryItem : returnsSummuryList) {
				String returnType = summaryItem.getReturntype();
				if (returnType.equals(MasterGSTConstants.GSTR2)) {
					returnType = MasterGSTConstants.PURCHASE_REGISTER;
				}
				Page<? extends InvoiceParent> invoices = clientService.getInvoices(pageable, client, returnType, month, year);
				Double fieldValue = 0d;
				Double salesAmt = 0d, purchaseAmt = 0d;
				if (isNotEmpty(invoices)) {
					for (InvoiceParent invoice : invoices) {
						if (returnType.equals(MasterGSTConstants.GSTR2)
								|| returnType.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
							if (isNotEmpty(invoice.getTotalitc())) {
								fieldValue += invoice.getTotalitc();
							}
							if (isNotEmpty(invoice.getTotalamount())) {
								purchaseAmt += invoice.getTotalamount();
							}
						} else {
							if (isNotEmpty(invoice.getTotaltax())) {
								fieldValue += invoice.getTotaltax();
							}
							if (returnType.equals(GSTR1) && isNotEmpty(invoice.getTotalamount())) {
								salesAmt += invoice.getTotalamount();
							}
						}
					}
					if (isEmpty(summaryItem.getFieldValue())) {
						summaryItem.setFieldValue(fieldValue);
					} else {
						summaryItem.setFieldValue(summaryItem.getFieldValue() + fieldValue);
					}
				}
			}
		}
		return returnsSummuryList;
	}
	
	@RequestMapping(value = "/journalsExcelDownload/{id}/{clientid}/{month}/{year}/{type}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource downloadExcelData(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid,@PathVariable("month") int month,@PathVariable("year") int year,@PathVariable("type") String type, HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		Client client = clientService.findById(clientid);
		List<AccountingJournal> excelDataLst=new ArrayList<AccountingJournal>();
		SimpleDateFormat formatdate=new SimpleDateFormat("dd-MM-YYYY");
		response.setHeader("Content-Disposition", "inline; filename='MGST_Journal.xls");
		
		OtherConfigurations othrconfigs = otherConfigurationRepository.findByClientid(clientid);
		String debit="", credit="",groupName="", ac=" A/c";
		String tcsReceivable="TCS Receivable",tdsPayable="TDS Payable";
		
		if(isNotEmpty(othrconfigs)) {
			if(othrconfigs.isEnableDrcr()) {
				debit = "Dr ";
				credit = "Cr ";
			}else{
				debit = "By ";
				credit = "To ";
			}
		}else {
			debit = "Dr ";
			credit = "Cr ";
		}
		File file = new File("MGST_Journal.xls");
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers = null;
			headers = Arrays.asList("Date","TRN Type","TRN sl.no","Account No","BGL Description","Txn Amount","Narration Details");			
			Date stDate = null;
			Date endDate = null;
			Calendar cal = Calendar.getInstance();
			if("Monthly".equalsIgnoreCase(type)) {
				cal.set(year, month - 1, 0, 23, 59, 59);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(year, month, 0, 23, 59, 59);
				endDate = new java.util.Date(cal.getTimeInMillis());
			}else {
				cal.set(year, 3, 0, 23, 59, 59);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(year+1, 3, 0, 0, 0, 0);
				endDate = new java.util.Date(cal.getTimeInMillis());
			}
			
			List<AccountingJournal> journals = accountingJournalRepository.findByClientIdAndDateofinvoiceBetweenAndStatusNull(clientid,stDate,endDate);
			for(AccountingJournal accJrnl: journals) {
				//RCM Checking start
				if("RCM".equalsIgnoreCase(accJrnl.getRcmorinterorintra())) {
					if(MasterGSTConstants.GSTR1.equalsIgnoreCase(accJrnl.getReturnType())) {
						if(isNotEmpty(accJrnl.getVendorName())) {
							ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
							if(isNotEmpty(ledger)) {
								if(isNotEmpty(ledger.getGrpsubgrpName())) {
									groupName=ledger.getGrpsubgrpName();
								}else {
									groupName="";
								}
							}else {
								groupName="";
							}
							excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
						}else {
							ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Customer");
							if(isNotEmpty(ledger)) {
								if(isNotEmpty(ledger.getGrpsubgrpName())) {
									groupName=ledger.getGrpsubgrpName();
								}else {
									groupName="";
								}
							}else {
								groupName="";
							}
							excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Customer"+ac, accJrnl.getJournalNumber(),"Customer", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
						}
						if(isNotEmpty(accJrnl.getLedgerName())) {
							ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
							if(isNotEmpty(ledger)) {
								if(isNotEmpty(ledger.getGrpsubgrpName())) {
									groupName=ledger.getGrpsubgrpName();
								}else {
									groupName="";
								}
							}else {
								groupName="";
							}
							excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getLedgerName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));	  
						}else {
							ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Sales");
							if(isNotEmpty(ledger)) {
								if(isNotEmpty(ledger.getGrpsubgrpName())) {
									groupName=ledger.getGrpsubgrpName();
								}else {
									groupName="";
								}
							}else {
								groupName="";
							}
							excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Sales"+ac, accJrnl.getJournalNumber(), "Sales", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
						}
						if(isNotEmpty(accJrnl.getTdsamount())){
							ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tcsReceivable);
							if(isNotEmpty(ledger)) {
								if(isNotEmpty(ledger.getGrpsubgrpName())) {
									groupName=ledger.getGrpsubgrpName();
								}else {
									groupName="";
								}
							}else {
								groupName="";
							}
							excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tcsReceivable+ac, accJrnl.getJournalNumber(), tcsReceivable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
						}
					}else {
						// start itcinelg eq true
						if(isNotEmpty(accJrnl.getItcinelg()) && "true".equalsIgnoreCase(accJrnl.getItcinelg())) {
							if(isNotEmpty(accJrnl.getVendorName())) {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
							}else {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Vendor"+ac, accJrnl.getJournalNumber(),"Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
							}
							if(isNotEmpty(accJrnl.getLedgerName())) {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getLedgerName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
							}else {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expense");
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Expense"+ac, accJrnl.getJournalNumber(),"Expense", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
							}
						}//end itcinelg eq true
						else {
							//start Intra
							if(isNotEmpty(accJrnl.getInterorintra()) && "Intra".equalsIgnoreCase(accJrnl.getInterorintra())) {
								if(isNotEmpty(accJrnl.getVendorName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Vendor"+ac, accJrnl.getJournalNumber(), "Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getLedgerName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getLedgerName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expense");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Expense"+ac, accJrnl.getJournalNumber(), "Expense", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}
								ProfileLedger cgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "CGST Receivable RCM");
								if(isNotEmpty(cgst_ledger)) {
									if(isNotEmpty(cgst_ledger.getGrpsubgrpName())) {
										groupName=cgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"CGST Receivable RCM"+ac, accJrnl.getJournalNumber(), "CGST Receivable RCM", groupName, accJrnl.getCgstamount(), accJrnl.getInvoiceNumber()));
								ProfileLedger sgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "SGST Receivable RCM");
								if(isNotEmpty(sgst_ledger)) {
									if(isNotEmpty(sgst_ledger.getGrpsubgrpName())) {
										groupName=sgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"SGST Receivable RCM"+ac, accJrnl.getJournalNumber(), "SGST Receivable RCM", groupName, accJrnl.getSgstamount(), accJrnl.getInvoiceNumber()));
								ProfileLedger ccgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "CGST Payable RCM");
								if(isNotEmpty(ccgst_ledger)) {
									if(isNotEmpty(ccgst_ledger.getGrpsubgrpName())) {
										groupName=ccgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"CGST Payable RCM"+ac, accJrnl.getJournalNumber(),"CGST Payable RCM", groupName, accJrnl.getRcgstamount(), accJrnl.getInvoiceNumber()));
								ProfileLedger csgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "SGST Payable RCM");
								if(isNotEmpty(csgst_ledger)) {
									if(isNotEmpty(csgst_ledger.getGrpsubgrpName())) {
										groupName=csgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"SGST Payable RCM"+ac, accJrnl.getJournalNumber(), "SGST Payable RCM", groupName, accJrnl.getRsgstamount(), accJrnl.getInvoiceNumber()));
							}//end Intra
							else {
								if(isNotEmpty(accJrnl.getVendorName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Vendor"+ac, accJrnl.getJournalNumber(),"Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getLedgerName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(),accJrnl.getLedgerName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expense");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Expense"+ac, accJrnl.getJournalNumber(), "Expense", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}
								ProfileLedger cgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "IGST Receivable RCM");
								if(isNotEmpty(cgst_ledger)) {
									if(isNotEmpty(cgst_ledger.getGrpsubgrpName())) {
										groupName=cgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"IGST Receivable RCM"+ac, accJrnl.getJournalNumber(), "IGST Receivable RCM", groupName, accJrnl.getIgstamount(), accJrnl.getInvoiceNumber()));
								ProfileLedger sgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "IGST Payable RCM");
								if(isNotEmpty(sgst_ledger)) {
									if(isNotEmpty(sgst_ledger.getGrpsubgrpName())) {
										groupName=sgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"IGST Payable RCM"+ac, accJrnl.getJournalNumber(), "IGST Payable RCM", groupName, accJrnl.getRigstamount(), accJrnl.getInvoiceNumber()));
							}
						}
					}
				}//RCM Checking end
				else {
					//GSTR1 Checking start
					if(MasterGSTConstants.GSTR1.equalsIgnoreCase(accJrnl.getReturnType())) {
						//Intra Checking start
						if(isNotEmpty(accJrnl.getRcmorinterorintra()) && "Intra".equalsIgnoreCase(accJrnl.getRcmorinterorintra())) {
							//Nill Supplies Checking start
							if(isNotEmpty(accJrnl.getInvoiceType()) && "Nil Supplies".equalsIgnoreCase(accJrnl.getInvoiceType())) {
								if(isNotEmpty(accJrnl.getVendorName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(),accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Vendor"+ac, accJrnl.getJournalNumber(), "Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getLedgerName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expenses");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Expenses"+ac, accJrnl.getJournalNumber(), "Expenses", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getTdsamount())){
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tcsReceivable);
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tcsReceivable+ac, accJrnl.getJournalNumber(), tcsReceivable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
								}
							}//Nill Supplies Checking end
							//Advances Checking start
							else if(isNotEmpty(accJrnl.getInvoiceType()) && "Advances".equalsIgnoreCase(accJrnl.getInvoiceType())) {
								if(isNotEmpty(accJrnl.getVendorName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Cash");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Cash/Bank"+ac, accJrnl.getJournalNumber(), "Cash/Bank", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getLedgerName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Advance Vendor");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Advance Vendor"+ac, accJrnl.getJournalNumber(), "Advance Vendor", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getTdsamount())){
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tcsReceivable);
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tcsReceivable+ac, accJrnl.getJournalNumber(), tcsReceivable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
								}
								ProfileLedger cgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "CGST Payable");
								if(isNotEmpty(cgst_ledger)) {
									if(isNotEmpty(cgst_ledger.getGrpsubgrpName())) {
										groupName=cgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"CGST Payable"+ac, accJrnl.getJournalNumber(), "CGST Payable", groupName, accJrnl.getCgstamount(), accJrnl.getInvoiceNumber()));
								ProfileLedger sgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "SGST Payable");
								if(isNotEmpty(sgst_ledger)) {
									if(isNotEmpty(sgst_ledger.getGrpsubgrpName())) {
										groupName=sgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"SGST Payable"+ac, accJrnl.getJournalNumber(), "SGST Payable", groupName, accJrnl.getCgstamount(), accJrnl.getInvoiceNumber()));
							}//Advances Checking end
							//Advance Adjusted Detail Checking start
							else if(isNotEmpty(accJrnl.getInvoiceType()) && "Advance Adjusted Detail".equalsIgnoreCase(accJrnl.getInvoiceType())) {
								if(isNotEmpty(accJrnl.getVendorName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Cash");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Cash/Bank"+ac, accJrnl.getJournalNumber(), "Cash/Bank", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getLedgerName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Cash");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Cash/Bank"+ac, accJrnl.getJournalNumber(), "Cash/Bank", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getTdsamount())){
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tcsReceivable);
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tcsReceivable+ac, accJrnl.getJournalNumber(), tcsReceivable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
								}
								if(accJrnl.getCgstamount()>0) {
									ProfileLedger cgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "CGST Payable");
									if(isNotEmpty(cgst_ledger)) {
										if(isNotEmpty(cgst_ledger.getGrpsubgrpName())) {
											groupName=cgst_ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"CGST Payable"+ac, accJrnl.getJournalNumber(), "CGST Payable", groupName, accJrnl.getCgstamount(), accJrnl.getInvoiceNumber()));
								}
								if(accJrnl.getSgstamount()>0) {
									ProfileLedger cgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "SGST Payable");
									if(isNotEmpty(cgst_ledger)) {
										if(isNotEmpty(cgst_ledger.getGrpsubgrpName())) {
											groupName=cgst_ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"SGST Payable"+ac, accJrnl.getJournalNumber(), "SGST Payable", groupName, accJrnl.getSgstamount(), accJrnl.getInvoiceNumber()));
								}
								if(accJrnl.getIgstamount()>0) {
									ProfileLedger sgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "IGST Payable");
									if(isNotEmpty(sgst_ledger)) {
										if(isNotEmpty(sgst_ledger.getGrpsubgrpName())) {
											groupName=sgst_ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"IGST Payable"+ac, accJrnl.getJournalNumber(), "IGST Payable", groupName, accJrnl.getIgstamount(), accJrnl.getInvoiceNumber()));
								}
							}//Advance Adjusted Detail Checking end
							else {
								if(isNotEmpty(accJrnl.getVendorName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Cash");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Cash/Bank"+ac, accJrnl.getJournalNumber(), "Cash/Bank", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getLedgerName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Sales");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Sales"+ac, accJrnl.getJournalNumber(), "Sales", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getTdsamount())){
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tcsReceivable);
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tcsReceivable+ac, accJrnl.getJournalNumber(), tcsReceivable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
								}
								ProfileLedger cgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "CGST Payable");
								if(isNotEmpty(cgst_ledger)) {
									if(isNotEmpty(cgst_ledger.getGrpsubgrpName())) {
										groupName=cgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"CGST Payable"+ac, accJrnl.getJournalNumber(), "CGST Payable", groupName, accJrnl.getCgstamount(), accJrnl.getInvoiceNumber()));
								ProfileLedger sgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "SGST Payable");
								if(isNotEmpty(sgst_ledger)) {
									if(isNotEmpty(sgst_ledger.getGrpsubgrpName())) {
										groupName=sgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"SGST Payable"+ac, accJrnl.getJournalNumber(), "SGST Payable", groupName, accJrnl.getCgstamount(), accJrnl.getInvoiceNumber()));
							}
							
						}//Intra Checking end
						//Not In Intra Checking end
						else {
							//Nil Supplies Checking start
							if(isNotEmpty(accJrnl.getInvoiceType()) && "Nil Supplies".equalsIgnoreCase(accJrnl.getInvoiceType())) {
								if(isNotEmpty(accJrnl.getVendorName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Vendor"+ac, accJrnl.getJournalNumber(), "Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getLedgerName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Sales");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Sales"+ac, accJrnl.getJournalNumber(), "Sales", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getTdsamount())){
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tcsReceivable);
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tcsReceivable+ac, accJrnl.getJournalNumber(), tcsReceivable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
								}
								ProfileLedger cgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "IGST Payable");
								if(isNotEmpty(cgst_ledger)) {
									if(isNotEmpty(cgst_ledger.getGrpsubgrpName())) {
										groupName=cgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"IGST Payable"+ac, accJrnl.getJournalNumber(), "IGST Payable", groupName, accJrnl.getIgstamount(), accJrnl.getInvoiceNumber()));
								
							}//NIl Supplies Checking end
							//Advances Checking start
							else if(isNotEmpty(accJrnl.getInvoiceType()) && "Advances".equalsIgnoreCase(accJrnl.getInvoiceType())) {
								if(isNotEmpty(accJrnl.getVendorName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Cash");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Cash/Bank"+ac, accJrnl.getJournalNumber(), "Cash/Bank", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getLedgerName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getLedgerName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Advance Vendor");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Advance Vendor"+ac, accJrnl.getJournalNumber(), "Advance Vendor", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getTdsamount())){
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tcsReceivable);
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tcsReceivable+ac, accJrnl.getJournalNumber(), tcsReceivable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
								}
								ProfileLedger cgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "IGST Payable");
								if(isNotEmpty(cgst_ledger)) {
									if(isNotEmpty(cgst_ledger.getGrpsubgrpName())) {
										groupName=cgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"IGST Payable"+ac, accJrnl.getJournalNumber(), "IGST Payable", groupName, accJrnl.getIgstamount(), accJrnl.getInvoiceNumber()));
							}//Advances Checking end
							//Advances Adjusted Detail Checking start
							else if(isNotEmpty(accJrnl.getInvoiceType()) && "Advance Adjusted Detail".equalsIgnoreCase(accJrnl.getInvoiceType())) {
								if(isNotEmpty(accJrnl.getVendorName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Cash");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Cash/Bank"+ac, accJrnl.getJournalNumber(), "Cash/Bank", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getLedgerName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Advance Vendor");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Advance Vendor"+ac, accJrnl.getJournalNumber(), "Advance Vendor", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getTdsamount())){
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tcsReceivable);
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tcsReceivable+ac, accJrnl.getJournalNumber(), tcsReceivable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
								}
								/*ProfileLedger cgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "IGST Payable");
								if(isNotEmpty(cgst_ledger)) {
									if(isNotEmpty(cgst_ledger.getGrpsubgrpName())) {
										groupName=cgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
										groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"IGST Payable"+ac, accJrnl.getJournalNumber(), "IGST Payable", groupName, accJrnl.getCgstamount(), accJrnl.getInvoiceNumber()));*/
							}//Advances Adjusted Detail Checking end
							else {
								if(isNotEmpty(accJrnl.getVendorName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Customer");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Customer"+ac, accJrnl.getJournalNumber(), "Customer", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getLedgerName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Sales");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Sales"+ac, accJrnl.getJournalNumber(), "Sales", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getTdsamount())){
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tcsReceivable);
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tcsReceivable+ac, accJrnl.getJournalNumber(), tcsReceivable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
								}
								ProfileLedger cgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "IGST Payable");
								if(isNotEmpty(cgst_ledger)) {
									if(isNotEmpty(cgst_ledger.getGrpsubgrpName())) {
										groupName=cgst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"IGST Payable"+ac, accJrnl.getJournalNumber(), "IGST Payable", groupName, accJrnl.getIgstamount(), accJrnl.getInvoiceNumber()));
							}
						}//Not In Intra Checking end
					}//GSTR1 Checking end
					else {
						//Purchases Nil Supplies Checking start
						if(isNotEmpty(accJrnl.getInvoiceType()) && "Nil Supplies".equalsIgnoreCase(accJrnl.getInvoiceType())) {
							if(isNotEmpty(accJrnl.getLedgerName())) {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
							}else {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expenses");
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Expenses"+ac, accJrnl.getJournalNumber(), "Expenses", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
							}
							if(isNotEmpty(accJrnl.getVendorName())) {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
							}else {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Vendor"+ac, accJrnl.getJournalNumber(), "Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
							}
							if(isNotEmpty(accJrnl.getTdsamount())){
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tdsPayable);
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tdsPayable+ac, accJrnl.getJournalNumber(), tdsPayable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
							}
						}//Purchases Nil Supplies Checking end
						//Purchases Advances Checking start
						else if(isNotEmpty(accJrnl.getInvoiceType()) && "Advances".equalsIgnoreCase(accJrnl.getInvoiceType())) {
							if(isNotEmpty(accJrnl.getVendorName())) {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
							}else {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Advance Vendor");
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Advance Vendor"+ac, accJrnl.getJournalNumber(), "Advance Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
							}
							if(isNotEmpty(accJrnl.getLedgerName())) {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
							}else {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Cash");
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Cash/Bank"+ac, accJrnl.getJournalNumber(), "Cash/Bank", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
							}
							if(isNotEmpty(accJrnl.getTdsamount())){
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tdsPayable);
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tdsPayable+ac, accJrnl.getJournalNumber(), tdsPayable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
							}
						}//Purchases Advances Checking end
						//Purchases Advance Adjusted Detail Checking start
						else if(isNotEmpty(accJrnl.getInvoiceType()) && "Advance Adjusted Detail".equalsIgnoreCase(accJrnl.getInvoiceType())) {
							if(isNotEmpty(accJrnl.getVendorName())) {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
							}else {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Advance Vendor");
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Advance Vendor"+ac, accJrnl.getJournalNumber(), "Advance Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
							}
							if(isNotEmpty(accJrnl.getLedgerName())) {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
							}else {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Cash");
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Cash/Bank"+ac, accJrnl.getJournalNumber(), "Cash/Bank", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
							}
							if(isNotEmpty(accJrnl.getTdsamount())){
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tdsPayable);
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tdsPayable+ac, accJrnl.getJournalNumber(), tdsPayable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
							}
						}//Purchases Advance Adjusted Detail Checking end
						//Purchases Import Services Checking start
						else if(isNotEmpty(accJrnl.getInvoiceType()) && "Import Services".equalsIgnoreCase(accJrnl.getInvoiceType())) {
							if(isNotEmpty(accJrnl.getVendorName())) {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
							}else {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), "Vendor", accJrnl.getJournalNumber(), "Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
							}
							if(isNotEmpty(accJrnl.getLedgerName())) {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
							}else {
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expense");
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Expense"+ac, accJrnl.getJournalNumber(), "Expense", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
							}
							if(isNotEmpty(accJrnl.getTdsamount())){
								ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tdsPayable);
								if(isNotEmpty(ledger)) {
									if(isNotEmpty(ledger.getGrpsubgrpName())) {
										groupName=ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tdsPayable+ac, accJrnl.getJournalNumber(), tdsPayable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
							}
							ProfileLedger igst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "IGST Receivable RCM");
							if(isNotEmpty(igst_ledger)) {
								if(isNotEmpty(igst_ledger.getGrpsubgrpName())) {
									groupName=igst_ledger.getGrpsubgrpName();
								}else {
									groupName="";
								}
							}else {
								groupName="";
							}
							excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"IGST Receivable RCM"+ac, accJrnl.getJournalNumber(), "IGST Receivable RCM", groupName, accJrnl.getIgstamount(), accJrnl.getInvoiceNumber()));
							ProfileLedger iigst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "IGST Payable RCM");
							if(isNotEmpty(iigst_ledger)) {
								if(isNotEmpty(iigst_ledger.getGrpsubgrpName())) {
									groupName=iigst_ledger.getGrpsubgrpName();
								}else {
									groupName="";
								}
							}else {
								groupName="";
							}
							excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"IGST Payable RCM"+ac, accJrnl.getJournalNumber(), "IGST Payable RCM", groupName, accJrnl.getRigstamount(), accJrnl.getInvoiceNumber()));
						}//Purchases Import Services Checking end
						//Purchases Import Goods Checking start
						else if(isNotEmpty(accJrnl.getInvoiceType()) && "Import Goods".equalsIgnoreCase(accJrnl.getInvoiceType())) {
							if(isNotEmpty(accJrnl.getItcinelg()) && "true".equalsIgnoreCase(accJrnl.getItcinelg())) {
								if(isNotEmpty(accJrnl.getLedgerName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expenditure / Capital Good");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Expenditure / Capital Good"+ac, accJrnl.getJournalNumber(), "Expenditure / Capital Good", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getVendorName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Vendor"+ac, accJrnl.getJournalNumber(), "Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getTdsamount())){
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tdsPayable);
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tdsPayable+ac, accJrnl.getJournalNumber(), tdsPayable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
								}
							}else {
								if(isNotEmpty(accJrnl.getLedgerName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expenditure / Capital Good");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Expenditure / Capital Good"+ac, accJrnl.getJournalNumber(), "Expenditure / Capital Good", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getVendorName())) {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}else {
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Vendor"+ac, accJrnl.getJournalNumber(), "Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
								}
								if(isNotEmpty(accJrnl.getTdsamount())){
									ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tdsPayable);
									if(isNotEmpty(ledger)) {
										if(isNotEmpty(ledger.getGrpsubgrpName())) {
											groupName=ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tdsPayable+ac, accJrnl.getJournalNumber(), tdsPayable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
								}
								ProfileLedger iigst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "IGST Receivable");
								if(isNotEmpty(iigst_ledger)) {
									if(isNotEmpty(iigst_ledger.getGrpsubgrpName())) {
										groupName=iigst_ledger.getGrpsubgrpName();
									}else {
										groupName="";
									}
								}else {
									groupName="";
								}
								excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"IGST Receivable"+ac, accJrnl.getJournalNumber(), "IGST Receivable", groupName, accJrnl.getIgstamount(), accJrnl.getInvoiceNumber()));
							}
						}//Purchases Import Goods Checking end
						//Purchases Intra Start
						else {
							if(isNotEmpty(accJrnl.getRcmorinterorintra()) && "Intra".equalsIgnoreCase(accJrnl.getRcmorinterorintra())) {
								if(isNotEmpty(accJrnl.getItcinelg()) && "true".equalsIgnoreCase(accJrnl.getItcinelg())){
									if(isNotEmpty(accJrnl.getLedgerName())) {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
									}else {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expenditure / Capital Good");
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Expenditure / Capital Good"+ac, accJrnl.getJournalNumber(), "Expenditure / Capital Good", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
									}
									if(isNotEmpty(accJrnl.getVendorName())) {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
									}else {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Vendor"+ac, accJrnl.getJournalNumber(), "Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
									}
									if(isNotEmpty(accJrnl.getTdsamount())){
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tdsPayable);
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tdsPayable+ac, accJrnl.getJournalNumber(), tdsPayable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
									}
								}else {//B2B Unregistered start
									if(isNotEmpty(accJrnl.getInvoiceType()) && "B2B Unregistered".equalsIgnoreCase(accJrnl.getInvoiceType())) {
										if(isNotEmpty(accJrnl.getLedgerName())) {
											ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
											if(isNotEmpty(ledger)) {
												if(isNotEmpty(ledger.getGrpsubgrpName())) {
													groupName=ledger.getGrpsubgrpName();
												}else {
													groupName="";
												}
											}else {
												groupName="";
											}
											excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
										}else {
											ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expenditure / Capital Good");
											if(isNotEmpty(ledger)) {
												if(isNotEmpty(ledger.getGrpsubgrpName())) {
													groupName=ledger.getGrpsubgrpName();
												}else {
													groupName="";
												}
											}else {
												groupName="";
											}
											excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Expenditure / Capital Good"+ac, accJrnl.getJournalNumber(), "Expenditure / Capital Good", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
										}
										/*ProfileLedger sgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "SGST Receivable");
										if(isNotEmpty(sgst_ledger)) {
											if(isNotEmpty(sgst_ledger.getGrpsubgrpName())) {
												groupName=sgst_ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+" SGST Receivable"+ac, accJrnl.getJournalNumber(), " SGST Receivable", groupName, accJrnl.getSgstamount(), accJrnl.getInvoiceNumber()));
										ProfileLedger cgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "CGST Receivable");
										if(isNotEmpty(cgst_ledger)) {
											if(isNotEmpty(cgst_ledger.getGrpsubgrpName())) {
												groupName=cgst_ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"CGST Receivable"+ac, accJrnl.getJournalNumber(), "CGST Receivable", groupName, accJrnl.getCgstamount(), accJrnl.getInvoiceNumber()));*/
										if(isNotEmpty(accJrnl.getVendorName())) {
											ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
											if(isNotEmpty(ledger)) {
												if(isNotEmpty(ledger.getGrpsubgrpName())) {
													groupName=ledger.getGrpsubgrpName();
												}else {
													groupName="";
												}
											}else {
												groupName="";
											}
											excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
										}else {
											ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
											if(isNotEmpty(ledger)) {
												if(isNotEmpty(ledger.getGrpsubgrpName())) {
													groupName=ledger.getGrpsubgrpName();
												}else {
													groupName="";
												}
											}else {
												groupName="";
											}
											excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Vendor"+ac, accJrnl.getJournalNumber(), "Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
										}
										if(isNotEmpty(accJrnl.getTdsamount())){
											ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tdsPayable);
											if(isNotEmpty(ledger)) {
												if(isNotEmpty(ledger.getGrpsubgrpName())) {
													groupName=ledger.getGrpsubgrpName();
												}else {
													groupName="";
												}
											}else {
												groupName="";
											}
											excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tdsPayable+ac, accJrnl.getJournalNumber(), tdsPayable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
										}
									}//B2B Unregistered end
									//B2B start
									else {
										if(isNotEmpty(accJrnl.getLedgerName())) {
											ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
											if(isNotEmpty(ledger)) {
												if(isNotEmpty(ledger.getGrpsubgrpName())) {
													groupName=ledger.getGrpsubgrpName();
												}else {
													groupName="";
												}
											}else {
												groupName="";
											}
											excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
										}else {
											ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expenditure / Capital Good");
											if(isNotEmpty(ledger)) {
												if(isNotEmpty(ledger.getGrpsubgrpName())) {
													groupName=ledger.getGrpsubgrpName();
												}else {
													groupName="";
												}
											}else {
												groupName="";
											}
											excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Expenditure / Capital Good"+ac, accJrnl.getJournalNumber(), "Expenditure / Capital Good", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
										}
										if(isNotEmpty(accJrnl.getTdsamount())){
											ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tdsPayable);
											if(isNotEmpty(ledger)) {
												if(isNotEmpty(ledger.getGrpsubgrpName())) {
													groupName=ledger.getGrpsubgrpName();
												}else {
													groupName="";
												}
											}else {
												groupName="";
											}
											excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tdsPayable+ac, accJrnl.getJournalNumber(), tdsPayable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
										}
										ProfileLedger sgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "SGST Receivable");
										if(isNotEmpty(sgst_ledger)) {
											if(isNotEmpty(sgst_ledger.getGrpsubgrpName())) {
												groupName=sgst_ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"SGST Receivable"+ac, accJrnl.getJournalNumber(), "SGST Receivable", groupName, accJrnl.getSgstamount(), accJrnl.getInvoiceNumber()));
										ProfileLedger cgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "CGST Receivable");
										if(isNotEmpty(cgst_ledger)) {
											if(isNotEmpty(cgst_ledger.getGrpsubgrpName())) {
												groupName=cgst_ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"CGST Receivable"+ac, accJrnl.getJournalNumber(), "CGST Receivable", groupName, accJrnl.getCgstamount(), accJrnl.getInvoiceNumber()));
										if(isNotEmpty(accJrnl.getVendorName())) {
											ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
											if(isNotEmpty(ledger)) {
												if(isNotEmpty(ledger.getGrpsubgrpName())) {
													groupName=ledger.getGrpsubgrpName();
												}else {
													groupName="";
												}
											}else {
												groupName="";
											}
											excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
										}else {
											ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
											if(isNotEmpty(ledger)) {
												if(isNotEmpty(ledger.getGrpsubgrpName())) {
													groupName=ledger.getGrpsubgrpName();
												}else {
													groupName="";
												}
											}else {
												groupName="";
											}
											excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Vendor"+ac, accJrnl.getJournalNumber(), "Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
										}
									}//B2B end
								}
							}else {
								if(isNotEmpty(accJrnl.getItcinelg()) && "true".equalsIgnoreCase(accJrnl.getItcinelg())) {
									if(isNotEmpty(accJrnl.getLedgerName())) {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
									}else {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expenditure / Capital Good");
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Expenditure / Capital Good"+ac, accJrnl.getJournalNumber(), "Expenditure / Capital Good", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
									}
									if(isNotEmpty(accJrnl.getVendorName())) {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
									}else {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Vendor"+ac, accJrnl.getJournalNumber(), "Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
									}
									if(isNotEmpty(accJrnl.getTdsamount())){
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tdsPayable);
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tdsPayable+ac, accJrnl.getJournalNumber(), tdsPayable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
									}
								}else if(isNotEmpty(accJrnl.getInvoiceType()) && "B2B Unregistered".equalsIgnoreCase(accJrnl.getInvoiceType())) {
									if(isNotEmpty(accJrnl.getLedgerName())) {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
									}else {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expenditure / Capital Good");
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Expenditure / Capital Good"+ac, accJrnl.getJournalNumber(), "Expenditure / Capital Good", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
									}
									if(isNotEmpty(accJrnl.getVendorName())) {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
									}else {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Vendor"+ac, accJrnl.getJournalNumber(), "Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
									}
									if(isNotEmpty(accJrnl.getTdsamount())){
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tdsPayable);
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tdsPayable+ac, accJrnl.getJournalNumber(), tdsPayable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
									}
								}else {
									if(isNotEmpty(accJrnl.getLedgerName())) {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getLedgerName());
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+accJrnl.getLedgerName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
									}else {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Expenditure / Capital Good");
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"Expenditure / Capital Good"+ac, accJrnl.getJournalNumber(), "Expenditure / Capital Good", groupName, accJrnl.getSalesorPurchases(), accJrnl.getInvoiceNumber()));
									}
									if(isNotEmpty(accJrnl.getVendorName())) {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, accJrnl.getVendorName());
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+accJrnl.getVendorName()+ac, accJrnl.getJournalNumber(), accJrnl.getVendorName(), groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
									}else {
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "Vendor");
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+"Vendor"+ac, accJrnl.getJournalNumber(), "Vendor", groupName, accJrnl.getCustomerorSupplierAccount(), accJrnl.getInvoiceNumber()));
									}
									if(isNotEmpty(accJrnl.getTdsamount())){
										ProfileLedger ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, tdsPayable);
										if(isNotEmpty(ledger)) {
											if(isNotEmpty(ledger.getGrpsubgrpName())) {
												groupName=ledger.getGrpsubgrpName();
											}else {
												groupName="";
											}
										}else {
											groupName="";
										}
										excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), credit+tdsPayable+ac, accJrnl.getJournalNumber(), tdsPayable, groupName, accJrnl.getTdsamount(), accJrnl.getInvoiceNumber()));
									}
									ProfileLedger cgst_ledger=ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, "IGST Receivable");
									if(isNotEmpty(cgst_ledger)) {
										if(isNotEmpty(cgst_ledger.getGrpsubgrpName())) {
											groupName=cgst_ledger.getGrpsubgrpName();
										}else {
											groupName="";
										}
									}else {
										groupName="";
									}
									excelDataLst.add(new AccountingJournal(formatdate.format(accJrnl.getCreatedDate()), debit+"IGST Receivable"+ac, accJrnl.getJournalNumber(), "IGST Receivable", groupName, accJrnl.getIgstamount(), accJrnl.getInvoiceNumber()));
								}
							}
						}//Purchases Intra Start
					}
				}
			}
			
			SimpleExporter exporter = new SimpleExporter();
			exporter.gridExport(headers, excelDataLst,"ledgerDate,trnName,journalNumber,accNumber,bglDescription,amount,invoiceNumber",fos);
				
			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "downloadExcelData : ERROR", e);
		}
		return new FileSystemResource(new File("MGST_Journal.xls"));
	}
	
	@RequestMapping(value = "/record_vouchers/{id}/{clientid}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addVoucherDetails(@ModelAttribute("voucherform") AccountingJournal vouchers, @PathVariable("id") String id,@PathVariable("clientid") String clientid,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype ,
			@RequestParam(required = false, name = "actiontype") String actiontype,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "RecordVouchers::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		if (NullUtil.isNotEmpty(vouchers.getId()) && new ObjectId(id).equals(vouchers.getId())) {
			vouchers.setId(null);
		}
		if(NullUtil.isNotEmpty(vouchers.getId())) {
			AccountingJournal journal = accountingJournalRepository.findOne(vouchers.getId().toString());
			if(isNotEmpty(journal)) {
				vouchers.setCreatedDate(journal.getCreatedDate());
			}
		}
		vouchers.setReturnType("Voucher");
		vouchers.setInvoiceType("Voucher");
		if(isNotEmpty(vouchers.getVoucherNumber())) {
			vouchers.setInvoiceNumber(vouchers.getVoucherNumber());
		}
		List<AccountingJournalVoucherItems> journalItems = Lists.newArrayList();
		Map<String, AccountingJournalVoucherItems> acpaymentjournal = Maps.newHashMap(); 
		if(isNotEmpty(vouchers.getVoucheritems())) {
			for(AccountingJournalVoucherItems voucherItem : vouchers.getVoucheritems()) {
				if(acpaymentjournal.size() == 0) {
					AccountingJournalVoucherItems acpaymentitem = new AccountingJournalVoucherItems();
					acpaymentitem.setLedger(voucherItem.getLedger());
					acpaymentitem.setModeOfVoucher(voucherItem.getModeOfVoucher());
					if(isNotEmpty(voucherItem.getCramount())) {
						acpaymentitem.setCramount(voucherItem.getCramount());
					}
					if(isNotEmpty(voucherItem.getDramount())) {
						acpaymentitem.setDramount(voucherItem.getDramount());
					}
					acpaymentjournal.put(voucherItem.getModeOfVoucher()+voucherItem.getLedger(), acpaymentitem);
				}else {
					if(isNotEmpty(acpaymentjournal.get(voucherItem.getModeOfVoucher()+voucherItem.getLedger()))) {
						AccountingJournalVoucherItems acpaymentitem = acpaymentjournal.get(voucherItem.getModeOfVoucher()+voucherItem.getLedger());
						acpaymentitem.setLedger(voucherItem.getLedger());
						acpaymentitem.setModeOfVoucher(voucherItem.getModeOfVoucher());
						if(isNotEmpty(voucherItem.getCramount())) {
							acpaymentitem.setCramount(acpaymentitem.getCramount() + voucherItem.getCramount());
						}else {
							acpaymentitem.setCramount(acpaymentitem.getCramount());
						}
						if(isNotEmpty(voucherItem.getDramount())) {
							acpaymentitem.setDramount(acpaymentitem.getDramount() + voucherItem.getDramount());
						}else {
							acpaymentitem.setDramount(acpaymentitem.getDramount());
						}
					
						acpaymentjournal.put(voucherItem.getModeOfVoucher()+voucherItem.getLedger(), acpaymentitem);
					}else {
						AccountingJournalVoucherItems acpaymentitem = new AccountingJournalVoucherItems();
						acpaymentitem.setLedger(voucherItem.getLedger());
						acpaymentitem.setModeOfVoucher(voucherItem.getModeOfVoucher());
						if(isNotEmpty(voucherItem.getCramount())) {
							acpaymentitem.setCramount(voucherItem.getCramount());
						}
						if(isNotEmpty(voucherItem.getDramount())) {
							acpaymentitem.setDramount(voucherItem.getDramount());
						}
						acpaymentjournal.put(voucherItem.getModeOfVoucher()+voucherItem.getLedger(), acpaymentitem);
					}
				}
			}
		}
		Map<String, AccountingJournalVoucherItems> reverseSortedMap = new TreeMap<String, AccountingJournalVoucherItems>(Collections.reverseOrder());
		 
		reverseSortedMap.putAll(acpaymentjournal);
		List<JournalEntrie> drEntries = Lists.newArrayList();
		List<JournalEntrie> crEntries = Lists.newArrayList();
		 for (Map.Entry<String,AccountingJournalVoucherItems> entry : reverseSortedMap.entrySet()) {
			 journalItems.add(entry.getValue());
			 if(isNotEmpty(entry.getValue()) && isNotEmpty(entry.getValue().getModeOfVoucher()) && entry.getValue().getModeOfVoucher().equalsIgnoreCase("Dr")) {
				 drEntries.add(new JournalEntrie(entry.getValue().getLedger(), entry.getValue().getDramount() != null ? entry.getValue().getDramount() : 0));
			 }else {
				 crEntries.add(new JournalEntrie(entry.getValue().getLedger(), entry.getValue().getCramount() != null ? entry.getValue().getCramount() : 0));
			 }
		 }
		 vouchers.setCrEntrie(crEntries);
		 vouchers.setDrEntrie(drEntries);
		 SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			String ptDate[] = vouchers.getVoucherDate().split("-");
			String strmonth = ptDate[1]+ptDate[2];
			vouchers.setInvoiceMonth(strmonth);
			try {
				vouchers.setDateofinvoice(dateFormat.parse(vouchers.getVoucherDate()));
				Date dt = (Date)vouchers.getDateofinvoice();
				if(isNotEmpty(dt)) {
					int vcmonth = dt.getMonth();
					int vcyear = dt.getYear()+1900;
					int vcquarter = vcmonth/3;
					vcquarter = vcquarter == 0 ? 4 : vcquarter;
					String vcyearCode = vcquarter == 4 ? (vcyear-1)+"-"+vcyear : (vcyear)+"-"+(vcyear+1);
					vcmonth++;
					
					vouchers.setMthCd(""+vcmonth);
					vouchers.setYrCd(""+vcyearCode);
					vouchers.setQrtCd(""+vcquarter);
				}
			} catch (ParseException e) {
			}
		 vouchers.setNoofpayments(journalItems.size());
		 vouchers.setJournalvoucheritems(journalItems);
		 if(isNotEmpty(vouchers.getTotcramount())) {
			 vouchers.setCreditTotal(vouchers.getTotcramount());
		 }else {
			 vouchers.setCreditTotal(0d);
		 }
		 if(isNotEmpty(vouchers.getTotdramount())) {
			 vouchers.setDebitTotal(vouchers.getTotdramount());
		 }else {
			 vouchers.setDebitTotal(0d);
		 }
		accountingJournalRepository.save(vouchers);
		
		if("ledgerreport".equalsIgnoreCase(actiontype)) {
			return "redirect:/ledgerreports/"+ id + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year;
		}else {
			return "redirect:/journaldetails/" + id + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year + "?type=Monthly";			
		}
	}
	
	@RequestMapping(value = "/record_contra/{id}/{clientid}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addContaDetails(@ModelAttribute("contraform") AccountingJournal contra, @PathVariable("id") String id,@PathVariable("clientid") String clientid,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype ,
			@RequestParam(required = false, name = "actiontype") String actiontype,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "RecordVouchers::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		if (NullUtil.isNotEmpty(contra.getId()) && new ObjectId(id).equals(contra.getId())) {
			contra.setId(null);
		}
		if(NullUtil.isNotEmpty(contra.getId())) {
			AccountingJournal journal = accountingJournalRepository.findOne(contra.getId().toString());
			if(isNotEmpty(journal)) {
				contra.setCreatedDate(journal.getCreatedDate());
			}
		}
		contra.setReturnType("Contra");
		contra.setInvoiceType("Contra");
		if(isNotEmpty(contra.getContraNumber())) {
			contra.setInvoiceNumber(contra.getContraNumber());
		}
		List<AccountingJournalVoucherItems> journalItems = Lists.newArrayList();
		Map<String, AccountingJournalVoucherItems> acpaymentjournal = Maps.newHashMap(); 
		if(isNotEmpty(contra.getContraitems())) {
			for(AccountingJournalVoucherItems contraItem : contra.getContraitems()) {
				if(acpaymentjournal.size() == 0) {
					AccountingJournalVoucherItems acpaymentitem = new AccountingJournalVoucherItems();
					acpaymentitem.setLedger(contraItem.getLedger());
					acpaymentitem.setModeOfVoucher(contraItem.getModeOfVoucher());
					if(isNotEmpty(contraItem.getCramount())) {
						acpaymentitem.setCramount(contraItem.getCramount());
					}
					if(isNotEmpty(contraItem.getDramount())) {
						acpaymentitem.setDramount(contraItem.getDramount());
					}
					acpaymentjournal.put(contraItem.getModeOfVoucher()+contraItem.getLedger(), acpaymentitem);
				}else {
					if(isNotEmpty(acpaymentjournal.get(contraItem.getModeOfVoucher()+contraItem.getLedger()))) {
						AccountingJournalVoucherItems acpaymentitem = acpaymentjournal.get(contraItem.getModeOfVoucher()+contraItem.getLedger());
						acpaymentitem.setLedger(contraItem.getLedger());
						acpaymentitem.setModeOfVoucher(contraItem.getModeOfVoucher());
						if(isNotEmpty(contraItem.getCramount())) {
							acpaymentitem.setCramount(acpaymentitem.getCramount() + contraItem.getCramount());
						}else {
							acpaymentitem.setCramount(acpaymentitem.getCramount());
						}
						if(isNotEmpty(contraItem.getDramount())) {
							acpaymentitem.setDramount(acpaymentitem.getDramount() + contraItem.getDramount());
						}else {
							acpaymentitem.setDramount(acpaymentitem.getDramount());
						}
					
						acpaymentjournal.put(contraItem.getModeOfVoucher()+contraItem.getLedger(), acpaymentitem);
					}else {
						AccountingJournalVoucherItems acpaymentitem = new AccountingJournalVoucherItems();
						acpaymentitem.setLedger(contraItem.getLedger());
						acpaymentitem.setModeOfVoucher(contraItem.getModeOfVoucher());
						if(isNotEmpty(contraItem.getCramount())) {
							acpaymentitem.setCramount(contraItem.getCramount());
						}
						if(isNotEmpty(contraItem.getDramount())) {
							acpaymentitem.setDramount(contraItem.getDramount());
						}
						acpaymentjournal.put(contraItem.getModeOfVoucher()+contraItem.getLedger(), acpaymentitem);
					}
				}
			}
		}
		Map<String, AccountingJournalVoucherItems> reverseSortedMap = new TreeMap<String, AccountingJournalVoucherItems>(Collections.reverseOrder());
		 
		reverseSortedMap.putAll(acpaymentjournal);
		List<JournalEntrie> drEntries = Lists.newArrayList();
		List<JournalEntrie> crEntries = Lists.newArrayList();
		 for (Map.Entry<String,AccountingJournalVoucherItems> entry : reverseSortedMap.entrySet()) {
			 journalItems.add(entry.getValue());
			 if(isNotEmpty(entry.getValue()) && isNotEmpty(entry.getValue().getModeOfVoucher()) && entry.getValue().getModeOfVoucher().equalsIgnoreCase("Dr")) {
				 drEntries.add(new JournalEntrie(entry.getValue().getLedger(), entry.getValue().getDramount() != null ? entry.getValue().getDramount() : 0));
			 }else {
				 crEntries.add(new JournalEntrie(entry.getValue().getLedger(), entry.getValue().getCramount() != null ? entry.getValue().getCramount() : 0));
			 }
		 }
		 contra.setCrEntrie(crEntries);
		 contra.setDrEntrie(drEntries);
		 SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			String ptDate[] = contra.getContraDate().split("-");
			String strmonth = ptDate[1]+ptDate[2];
			contra.setInvoiceMonth(strmonth);
			try {
				contra.setDateofinvoice(dateFormat.parse(contra.getContraDate()));
				Date dt = (Date)contra.getDateofinvoice();
				if(isNotEmpty(dt)) {
					int vcmonth = dt.getMonth();
					int vcyear = dt.getYear()+1900;
					int vcquarter = vcmonth/3;
					vcquarter = vcquarter == 0 ? 4 : vcquarter;
					String vcyearCode = vcquarter == 4 ? (vcyear-1)+"-"+vcyear : (vcyear)+"-"+(vcyear+1);
					vcmonth++;
					
					contra.setMthCd(""+vcmonth);
					contra.setYrCd(""+vcyearCode);
					contra.setQrtCd(""+vcquarter);
				}
				
			} catch (ParseException e) {
			}
		 contra.setNoofpayments(journalItems.size());
		 contra.setJournalcontraitems(journalItems);
		 if(isNotEmpty(contra.getTotcramount())) {
			 contra.setCreditTotal(contra.getTotcramount());
		 }else {
			 contra.setCreditTotal(0d);
		 }
		 if(isNotEmpty(contra.getTotdramount())) {
			 contra.setDebitTotal(contra.getTotdramount());
		 }else {
			 contra.setDebitTotal(0d);
		 }
		accountingJournalRepository.save(contra);
		
		if("ledgerreport".equalsIgnoreCase(actiontype)) {
			return "redirect:/ledgerreports/"+ id + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year;
		}else {
			return "redirect:/journaldetails/" + id + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year + "?type=Monthly";
		}
	}
	@RequestMapping(value = "/deljournal/{journalId}", method = RequestMethod.GET)
	public @ResponseBody void deleteJournal(@PathVariable("journalId") String journalId, 
			ModelMap model) throws Exception {
		final String method = "deleteJournal::";
		logger.debug(CLASSNAME + method + BEGIN);
		accountingJournalRepository.delete(journalId);
	}
	
	@RequestMapping(value = "/allVoucherContra/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody List<AccountingJournal> allVoucherContra(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month,
			@PathVariable("year") int year, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "Report::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		List<AccountingJournal> voucherContraList = accountingJournalRepository.findByClientIdAndReturnType(clientid,returntype);
		if(isNotEmpty(voucherContraList)) {
			for(AccountingJournal journal : voucherContraList) {
				journal.setUserId(journal.getId().toString());
			}
		}
		return voucherContraList;
	}
	
	@RequestMapping(value = "/journalInvoicedetails/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String journalInvoices(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,@PathVariable("returntype") String returntype,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,
			HttpServletRequest request) throws Exception {
		final String method = "allInvoiceView::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		//HttpSession session = request.getSession(false);
		List<String> returntypes = Lists.newArrayList();
		if("GSTR1".equals(returntype)) {
			returntypes.add("GSTR1");
			returntypes.add("SalesRegister");
		}else {
			returntypes.add("GSTR2");
			returntypes.add("Purchase Register");
			returntypes.add("PurchaseRegister");
		}
		List<AccountingJournal> journals = accountingJournalRepository.findByClientIdAndReturnTypeInAndStatusNull(clientid,returntypes);
		List<String> invoiceids = Lists.newArrayList();
		if(isNotEmpty(journals)) {
			for(AccountingJournal accountingJournal : journals) {
				if(isNotEmpty(accountingJournal.getInvoiceId())) {
					invoiceids.add(accountingJournal.getInvoiceId());
				}
			}
		}
		if("GSTR1".equals(returntype)) {
			Iterable<GSTR1> invoices = gstr1Repository.findAll(invoiceids);
			for(InvoiceParent invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(MapperFeature.USE_ANNOTATIONS);
			ObjectWriter writer=null;
			if(returntype.equals(GSTR1)) {
				FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", SimpleBeanPropertyFilter.serializeAll());
				writer=mapper.writer(filters);
			} else {
				writer=mapper.writer();
			}
			return writer.writeValueAsString(invoices);
		}else {
			Iterable<PurchaseRegister> invoices = purchaseRepository.findAll(invoiceids);
			for(InvoiceParent invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(MapperFeature.USE_ANNOTATIONS);
			ObjectWriter writer=null;
			if(returntype.equals(GSTR1)) {
				FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", SimpleBeanPropertyFilter.serializeAll());
				writer=mapper.writer(filters);
			} else {
				writer=mapper.writer();
			}
			return writer.writeValueAsString(invoices);
		}
	}
	
	@CrossOrigin(origins = "*")
    @RequestMapping(value = "/updtjournalDetails", method = RequestMethod.GET)
    public @ResponseBody String updateCreatedDate() {
		
		List<AccountingJournal> journals = accountingJournalRepository.findAll();
		
		List<AccountingJournal> filteredJournlas = Lists.newArrayList();
		for(AccountingJournal accountingJournal: journals) {
			if(isNotEmpty(accountingJournal.getReturnType())) {
				if("GSTR1".equalsIgnoreCase(accountingJournal.getReturnType())) {
					if(isNotEmpty(accountingJournal.getInvoiceId())) {
						InvoiceParent invoice = gstr1Repository.findOne(accountingJournal.getInvoiceId());
						if(isNotEmpty(invoice)) {
							accountingJournal.setDateofinvoice(invoice.getDateofinvoice());
							accountingJournal.setInvoiceMonth(invoice.getFp());
							filteredJournlas.add(accountingJournal);
						}
					}
				}else if("GSTR2".equalsIgnoreCase(accountingJournal.getReturnType())) {
					if(isNotEmpty(accountingJournal.getInvoiceId())) {
						InvoiceParent invoice = purchaseRepository.findOne(accountingJournal.getInvoiceId());
						if(isNotEmpty(invoice)) {
							if(isNotEmpty(invoice.getBillDate())) {
								accountingJournal.setDateofinvoice(invoice.getBillDate());
								Calendar cal = Calendar.getInstance();
								cal.setTime(invoice.getBillDate());
								int month = cal.get(Calendar.MONTH)+1;
								int year = cal.get(Calendar.YEAR);
								String strMonth =  month<10 ? "0"+month : month+"";
								String retPeriod = strMonth+year;
								accountingJournal.setInvoiceMonth(retPeriod);
							}else {
								accountingJournal.setDateofinvoice(invoice.getDateofinvoice());
								accountingJournal.setInvoiceMonth(invoice.getFp());
							}
							filteredJournlas.add(accountingJournal);
						}
					}
				}else if("Payment Receipt".equalsIgnoreCase(accountingJournal.getReturnType()) || "Payment".equalsIgnoreCase(accountingJournal.getReturnType())) {
					if(isNotEmpty(accountingJournal.getInvoiceId())) {
						Payments payment = recordPaymentsRepository.findOne(accountingJournal.getInvoiceId());
						if(isNotEmpty(payment)) {
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							String ptDate[] = payment.getPaymentDate().split("-");
							String strmonth = ptDate[1]+ptDate[2];
							accountingJournal.setInvoiceMonth(strmonth);
							try {
								accountingJournal.setDateofinvoice(dateFormat.parse(payment.getPaymentDate()));
							} catch (ParseException e) {
							}
							filteredJournlas.add(accountingJournal);
						}
					}
				}
				
			}
		
		}
		
		List<AccountingJournal> vjournals =  accountingJournalRepository.findByReturnType("Voucher");
		for(AccountingJournal accountingJournal: vjournals) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			String ptDate[] = accountingJournal.getVoucherDate().split("-");
			String strmonth = ptDate[1]+ptDate[2];
			accountingJournal.setInvoiceMonth(strmonth);
			try {
				accountingJournal.setDateofinvoice(dateFormat.parse(accountingJournal.getVoucherDate()));
			} catch (ParseException e) {
			}
			filteredJournlas.add(accountingJournal);
		}
		
		List<AccountingJournal> cjournals =  accountingJournalRepository.findByReturnType("Contra");
		for(AccountingJournal accountingJournal: cjournals) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			String ptDate[] = accountingJournal.getContraDate().split("-");
			String strmonth = ptDate[1]+ptDate[2];
			accountingJournal.setInvoiceMonth(strmonth);
			try {
				accountingJournal.setDateofinvoice(dateFormat.parse(accountingJournal.getContraDate()));
			} catch (ParseException e) {
			}
			filteredJournlas.add(accountingJournal);
		}
		
    	if(isNotEmpty(filteredJournlas)) {
    		accountingJournalRepository.save(filteredJournlas);
    	}
    	return "Completed Successfully";
    }
	
	@CrossOrigin(origins = "*")
    @RequestMapping(value = "/updtcompanyroles", method = RequestMethod.GET)
    public @ResponseBody String updtjournalPaymentsAndReceipts() {
		List<CompanyRole> companyroles = Lists.newArrayList();
		List<CompanyRole> companyRoles = companyRoleRepository.findAll();
		if(isNotEmpty(companyRoles)) {
			for(CompanyRole companyrole : companyRoles) {
				Map<String, List<Permission>> permissions = companyrole.getPermissions();
				if(isNotEmpty(permissions)) {
					List<Permission> permission = permissions.get("Settings");
					if(isNotEmpty(permission)) {
						for(Permission per : permission) {
							if("Company Details".equalsIgnoreCase(per.getName())) {
								per.setName("Profile");
							}else if("Invoice / Print".equalsIgnoreCase(per.getName())) {
								per.setName("Configurations");
							}else if("Bank Details".equalsIgnoreCase(per.getName())) {
								per.setName("Bank Information");
							}
						}
					}
				}
				companyroles.add(companyrole);
			}
		}
		if(isNotEmpty(companyroles)) {
			companyRoleRepository.save(companyroles);
		}
    	return "Completed Successfully...!";
    }
	
	@RequestMapping(value = "/getAddtionalJournalInvs/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getAdditionalInvoices(@PathVariable("id") String id, @PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year,
			@RequestParam("type") String type,ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		if(type.equalsIgnoreCase("Yearly")){
			month = 0;
		}
		String yearCode = Utility.getYearCode(month, year);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String sortParam = "dateofinvoice";
		String sortOrder = "asc";
		//sortOrder = request.getParameter("order[0][dir]");
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		Page<AccountingJournal> journals = journalsDao.findByClientidAndMonthAndYear(clientid, month, yearCode, start, length,sortParam,sortOrder);
		TotalJournalAmount totalJournalAmount = journalsDao.getTotalInvoicesAmountsForMonth(clientid, month, yearCode);
		invoicesMap.put("invoices",journals);
		invoicesMap.put("invoicesAmount",totalJournalAmount);
		for(AccountingJournal journal : journals) {
			journal.setUserId(journal.getId().toString());
			if(isNotEmpty(journal)) {
				if(isNotEmpty(journal.getReturnType())) {
					if("Payment Receipt".equals(journal.getReturnType()) || "Payment".equals(journal.getReturnType())) {
						List<String> returntype = Lists.newArrayList();
						if("Payment Receipt".equals(journal.getReturnType())) {
							returntype.add("SalesRegister");
							returntype.add("GSTR1");
						}else {
							returntype.add("PurchaseRegister");
							returntype.add("GSTR2");
							returntype.add("Purchase Register");
						}
						if(isEmpty(journal.getInvoiceId())) {
							Payments payment = recordPaymentsRepository.findByClientidAndVoucherNumberAndReturntypeIn(clientid, journal.getInvoiceNumber(),returntype);
							if(isNotEmpty(payment)) {
								journal.setInvoiceId(payment.getId().toString());
							}
						}
					}
					if(isNotEmpty(journal.getVendorName())) {
						String vendorName = journal.getVendorName();
						if(vendorName.contains("\r\n")) {
							vendorName = vendorName.replaceAll("\r\n", "");
						}
						journal.setVendorName(vendorName);
					}
					if(isNotEmpty(journal.getLedgerName())) {
						String ledgerName = journal.getLedgerName();
						if(ledgerName.contains("\r\n")) {
							ledgerName = ledgerName.replaceAll("\r\n", "");
						}
						journal.setLedgerName(ledgerName);
					}
				}
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(invoicesMap);
	}
	
	
}
