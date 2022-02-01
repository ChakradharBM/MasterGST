package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.ADVANCES;
import static com.mastergst.core.common.MasterGSTConstants.ANX1;
import static com.mastergst.core.common.MasterGSTConstants.ATA;
import static com.mastergst.core.common.MasterGSTConstants.ATPAID;
import static com.mastergst.core.common.MasterGSTConstants.B2B;
import static com.mastergst.core.common.MasterGSTConstants.B2BA;
import static com.mastergst.core.common.MasterGSTConstants.B2BUR;
import static com.mastergst.core.common.MasterGSTConstants.B2C;
import static com.mastergst.core.common.MasterGSTConstants.B2CL;
import static com.mastergst.core.common.MasterGSTConstants.B2CLA;
import static com.mastergst.core.common.MasterGSTConstants.B2CSA;
import static com.mastergst.core.common.MasterGSTConstants.CDNA;
import static com.mastergst.core.common.MasterGSTConstants.CDNUR;
import static com.mastergst.core.common.MasterGSTConstants.CDNURA;
import static com.mastergst.core.common.MasterGSTConstants.CREDIT_DEBIT_NOTES;
import static com.mastergst.core.common.MasterGSTConstants.EXPA;
import static com.mastergst.core.common.MasterGSTConstants.EXPORTS;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.GSTR4;
import static com.mastergst.core.common.MasterGSTConstants.GSTR5;
import static com.mastergst.core.common.MasterGSTConstants.GSTR6;
import static com.mastergst.core.common.MasterGSTConstants.IMP_GOODS;
import static com.mastergst.core.common.MasterGSTConstants.IMP_SERVICES;
import static com.mastergst.core.common.MasterGSTConstants.ITC_REVERSAL;
import static com.mastergst.core.common.MasterGSTConstants.NIL;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.common.MasterGSTConstants.TXPA;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.core.common.AccountConstants;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.accounting.util.AccountingJournalsUtils;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.AccountingJournalPaymentItems;
import com.mastergst.usermanagement.runtime.domain.BuyerDetails;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CompanySuppliers;
import com.mastergst.usermanagement.runtime.domain.DeliveryChallan;
import com.mastergst.usermanagement.runtime.domain.DeliveryChallana;
import com.mastergst.usermanagement.runtime.domain.DispatcherDetails;
import com.mastergst.usermanagement.runtime.domain.EBillVehicleListDetails;
import com.mastergst.usermanagement.runtime.domain.EWAYBILL;
import com.mastergst.usermanagement.runtime.domain.Estimate;
import com.mastergst.usermanagement.runtime.domain.Estimates;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.GSTR4;
import com.mastergst.usermanagement.runtime.domain.GSTR5;
import com.mastergst.usermanagement.runtime.domain.GSTRAdvanceTax;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRB2CL;
import com.mastergst.usermanagement.runtime.domain.GSTRB2CS;
import com.mastergst.usermanagement.runtime.domain.GSTRB2CSA;
import com.mastergst.usermanagement.runtime.domain.GSTRCreditDebitNotes;
import com.mastergst.usermanagement.runtime.domain.GSTRDocListDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRExportDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRExports;
import com.mastergst.usermanagement.runtime.domain.GSTRITCDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRITCReversals;
import com.mastergst.usermanagement.runtime.domain.GSTRImportDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRImportItems;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItemDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItems;
import com.mastergst.usermanagement.runtime.domain.GSTRNilInvoices;
import com.mastergst.usermanagement.runtime.domain.GSTRNilItems;
import com.mastergst.usermanagement.runtime.domain.GSTRNilSupItems;
import com.mastergst.usermanagement.runtime.domain.GSTRNilSupplies;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.PaymentItems;
import com.mastergst.usermanagement.runtime.domain.Payments;
import com.mastergst.usermanagement.runtime.domain.PreformaInvoice;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;
import com.mastergst.usermanagement.runtime.domain.ProformaInvoices;
import com.mastergst.usermanagement.runtime.domain.PurchaseOrder;
import com.mastergst.usermanagement.runtime.domain.PurchaseOrders;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.ShipmentDetails;
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6;
import com.mastergst.usermanagement.runtime.repository.AccountingJournalRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyCustomersRepository;
import com.mastergst.usermanagement.runtime.repository.CompanySuppliersRepository;
import com.mastergst.usermanagement.runtime.repository.EwayBillRepository;
import com.mastergst.usermanagement.runtime.repository.GSTINPublicDataRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;
import com.mastergst.usermanagement.runtime.repository.GSTR2Repository;
import com.mastergst.usermanagement.runtime.repository.GSTR4Repository;
import com.mastergst.usermanagement.runtime.repository.GSTR5Repository;
import com.mastergst.usermanagement.runtime.repository.GSTR6Repository;
import com.mastergst.usermanagement.runtime.repository.LedgerRepository;
import com.mastergst.usermanagement.runtime.repository.OtherConfigurationRepository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.ResponseData;
import com.mastergst.usermanagement.runtime.support.InvoicePendingActions;

@Service
public class NewBulkImportServiceImpl implements NewBulkImportService{
	
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	private static String DOUBLE_FORMAT  = "%.2f";
	private static SimpleDateFormat dateFormatOnlyDate = new SimpleDateFormat("dd/MM/yyyy");
	
	private String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd","dd-MM-yy","dd-MMM-yy","dd/MM/yy","dd/MMM/yy"};
	
	Double[] statndardTaxRate = { 0.0,0.1,0.25,1.0,1.5,3.0,5.0,7.5,12.0,18.0,28.0 };
	@Autowired
	protected MongoTemplate mongoTemplate;
	@Autowired
	private ConfigService configService;
	@Autowired
	private ClientService clientService;
	@Autowired
	GSTR1Repository gstr1Repository;
	@Autowired
	PurchaseRegisterRepository purchaseRepository;
	@Autowired
	private EwayBillRepository ewayBillRepository;
	@Autowired
	GSTR4Repository gstr4Repository;
	@Autowired
	GSTR5Repository gstr5Repository;
	@Autowired
	GSTR6Repository gstr6Repository;
	@Autowired
	AccountingJournalRepository accountingJournalRepository;
	@Autowired
	CompanyCustomersRepository companyCustomersRepository;
	@Autowired
	private ServiceUtils serviceUtils;
	@Autowired
	EinvoiceService einvoiceService;
	@Autowired
	private GSTINPublicDataRepository gstinPublicDataRepository;
	@Autowired
	IHubConsumerService iHubConsumerService;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private CompanySuppliersRepository companySuppliersRepository;
	@Autowired
	private LedgerRepository ledgerRepository;
	@Autowired
	private GSTR2Repository gstr2Repository;
	@Autowired
	OtherConfigurationRepository otherConfigurationRepository;
	@Autowired
	ClientUtils clientUtils;
	@Autowired private AccountingJournalsUtils accountingJournalsUtils;
	
	@Override
	public InvoiceParent importInvoicesHsnQtyRateMandatory(InvoiceParent invoice, String returnType, String clientid,String branch,
			String vertical, int month, int year, String[] patterns, Map<String, String> statesMap) {
		if (isNotEmpty(invoice.getStrDate())) {
			invoice.setDateofinvoice(invdate(invoice.getStrDate(),patterns,year,month));
		}
		if (isNotEmpty(invoice.getSection()) && isNotEmpty(invoice.getTcstdspercentage())) {
			invoice.setTdstcsenable(true);
		}
		if (isNotEmpty(invoice.getSection())) {
			invoiceSection(invoice,invoice.getSection());
		}
		if (isNotEmpty(invoice.getTransactionDate())) {
			invoice.setBillDate(invdate(invoice.getTransactionDate(),patterns,year,month));
		}
		if (isNotEmpty(invoice.getItcClaimedDate())) {
			invoice.setDateofitcClaimed(invdate(invoice.getItcClaimedDate(),patterns,year,month));
		}
		if (isNotEmpty(invoice.getStatename())) {
			String statename = invoice.getStatename();
			if (statename.contains("-")) {
				statename = statename.substring(statename.indexOf("-")+1).trim();
			}
			if(isNotEmpty(statename) && statename.contains("&")) {
				statename = invStatename(statename);
			}
			statename = statesMap.get(statename.replaceAll("\\s", "").toLowerCase());
			invoice.setStatename(statename);
		}
		if(isNotEmpty(invoice)) {
			CompanyCustomers customer = null;
			if(isNotEmpty(invoice.getInvoiceCustomerId()) && isNotEmpty(invoice.getBilledtoname())) {
				customer = companyCustomersRepository.findByClientidAndCustomerIdAndName(clientid, invoice.getInvoiceCustomerId(), invoice.getBilledtoname());
			}else if(isNotEmpty(invoice.getInvoiceCustomerId())) {
				customer = companyCustomersRepository.findByClientidAndCustomerId(invoice.getClientid(), invoice.getInvoiceCustomerId());
			}else if(isNotEmpty(invoice.getBilledtoname())) {
				customer = companyCustomersRepository.findByNameAndClientid(invoice.getBilledtoname(), invoice.getClientid());
			}
			if(isNotEmpty(customer)) {
				if(isNotEmpty(customer.getAddress())) {
					invoice.getB2b().get(0).getInv().get(0).setAddress(customer.getAddress());
				}
				if(isEmpty(invoice.getBilledtoname())) {
					if(isNotEmpty(customer.getName())) {
						invoice.setBilledtoname(customer.getName());
					}
				}
			}
		}
		if (isNotEmpty(invoice.getRevchargetype())) {
			String revChargeType = invoice.getRevchargetype();
			if ("YES".equalsIgnoreCase(revChargeType) || "Y".equalsIgnoreCase(revChargeType)) {
				invoice.setRevchargetype("Reverse");
			} else if ("NO".equalsIgnoreCase(revChargeType) || "N".equalsIgnoreCase(revChargeType)) {
				invoice.setRevchargetype("Regular");
			}
		}
		if (isNotEmpty(invoice.getExp().get(0).getExpTyp())) {
			String expType = invoice.getExp().get(0).getExpTyp();
			if ("Export with IGST".equalsIgnoreCase(expType) || "WPAY".equalsIgnoreCase(expType)) {
				invoice.getExp().get(0).setExpTyp("WPAY");
			} else if ("Export under Bond/LUT".equalsIgnoreCase(expType) || "WOPAY".equalsIgnoreCase(expType)) {
				invoice.getExp().get(0).setExpTyp("WOPAY");
			}

		}
		if (returnType.equals(MasterGSTConstants.GSTR2)	|| returnType.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
			if (isNotEmpty(((PurchaseRegister) invoice).getImpGoods().get(0).getIsSez())) {
				String revChargeType = ((PurchaseRegister) invoice).getImpGoods().get(0).getIsSez();
				if ("YES".equalsIgnoreCase(revChargeType) || "Y".equalsIgnoreCase(revChargeType)) {
					((PurchaseRegister) invoice).getImpGoods().get(0).setIsSez("Y");
				} else if ("NO".equalsIgnoreCase(revChargeType)	|| "N".equalsIgnoreCase(revChargeType)) {
					((PurchaseRegister) invoice).getImpGoods().get(0).setIsSez("N");
				}
			}
		}
		if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
			String invTyp = invoice.getB2b().get(0).getInv().get(0).getInvTyp();
			if ("Regular".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
			} else if ("Deemed Exports".equalsIgnoreCase(invTyp) || "Deemed Exp".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("DE");
			} else if ("Supplies to SEZ with payment".equalsIgnoreCase(invTyp) || "SEZ supplies with payment".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWP");
			} else if ("Supplies to SEZ without payment".equalsIgnoreCase(invTyp) || "SEZ supplies without payment".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWOP");
			} else if ("Sale from Bonded Warehouse".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("CBW");
			} else {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp(invTyp);
			}
		} else {
			invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
		}
		if (isNotEmpty(invoice.getItems().get(0).getItcRevtype())) {
			String itctype = invoice.getItems().get(0).getItcRevtype();
			if ("Amount in terms of rule 37(2)".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule2_2");
			} else if ("Amount in terms of rule 42(1)(m)".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule7_1_m");
			} else if ("Amount in terms of rule 43(1)(h)".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule8_1_h");
			} else if ("Amount in terms of rule 42(2)(a)".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule7_2_a");
			} else if ("Amount in terms of rule 42(2)(b)".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule7_2_b");
			} else if ("On account of amount paid subsequent to reversal of ITC".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("revitc");
			} else if ("Any other liability (Pl specify)".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("other");
			}
		}
		if (isNotEmpty(invoice.getItems().get(0).getElg())) {
			String elgType = invoice.getItems().get(0).getElg();
			if ("Capital Good".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("cp");
			} else if ("Inputs".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("ip");
			} else if ("Input Service".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("is");
			} else if ("Ineligible".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("no");
			} else if ("Not Selected".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("pending");
			}
		}
		if (returnType.equals(MasterGSTConstants.GSTR1)) {
			if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
				String docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
				if ("Credit".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("C");
				} else if ("Debit".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("D");
				} else if ("Refund".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("R");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt(),patterns);

					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
				String docType = ((GSTR1) invoice).getCdnur().get(0).getNtty();
				if ("Credit".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setNtty("C");
				} else if ("Debit".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setNtty("D");
				} else if ("Refund".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setNtty("R");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((GSTR1) invoice).getCdnur().get(0).getIdt(), patterns);

					((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((GSTR1) invoice).getCdnur().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}

			if (isNotEmpty(((GSTR1) invoice).getItems().get(0).getAdvReceiptDate())) {
				try {
					Date invDate = DateUtils.parseDate(((GSTR1) invoice).getItems().get(0).getAdvReceiptDate(), patterns);

					((GSTR1) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((GSTR1) invoice).getItems().get(0).getAdvReceiptDate());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((GSTR1) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((GSTR1) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}

			if (isNotEmpty(((GSTR1) invoice).getItems().get(0).getAdvStateName())) {
				String statename = ((GSTR1) invoice).getItems().get(0).getAdvStateName();
				if (statename.contains("-")) {
					statename = statename.substring(statename.indexOf("-")+1).trim();
				}
				if(isNotEmpty(statename) && statename.contains("&")) {
					statename = invStatename(statename);
				}
				statename = statesMap.get(statename.replaceAll("\\s", "").toLowerCase());
				((GSTR1) invoice).getItems().get(0).setAdvStateName(statename);
			}

			if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getpGst())) {
				String pGst = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getpGst();
				if ("YES".equalsIgnoreCase(pGst) || "Y".equalsIgnoreCase(pGst)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(pGst) || "N".equalsIgnoreCase(pGst)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setpGst("N");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getpGst())) {
				String docType = ((GSTR1) invoice).getCdnur().get(0).getpGst();
				if ("YES".equalsIgnoreCase(docType) || "Y".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(docType) || "N".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setpGst("N");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getTyp())) {
				String type = ((GSTR1) invoice).getCdnur().get(0).getTyp();
				if ("Exports With Payment".equalsIgnoreCase(type)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("EXPWP");
				} else if ("Exports Without Payment".equalsIgnoreCase(type)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("EXPWOP");
				} else if ("B2CL".equalsIgnoreCase(type)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("B2CL");
				}
			}
		} else if (returnType.equals(MasterGSTConstants.GSTR2) || returnType.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
			if (isNotEmpty(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getNtty())) {
				String docType = ((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getNtty();
				if ("Credit".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setNtty("C");
				} else if ("Debit".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setNtty("D");
				} else if ("Refund".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setNtty("R");
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getIdt(), patterns);

					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getItems().get(0).getAdvReceiptDate())) {
				try {
					Date invDate = DateUtils.parseDate(((PurchaseRegister) invoice).getItems().get(0).getAdvReceiptDate(),patterns);

					((PurchaseRegister) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((PurchaseRegister) invoice).getItems().get(0).getAdvReceiptDate());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((PurchaseRegister) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((PurchaseRegister) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getItems().get(0).getAdvStateName())) {
				String statename = ((PurchaseRegister) invoice).getItems().get(0).getAdvStateName();
				if (statename.contains("-")) {
					statename = statename.substring(statename.indexOf("-")+1).trim();
				}
				if(isNotEmpty(statename) && statename.contains("&")) {
					statename = invStatename(statename);
				}
				statename = statesMap.get(statename.replaceAll("\\s", "").toLowerCase());
				((PurchaseRegister) invoice).getItems().get(0).setAdvStateName(statename);
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getCdnur().get(0).getNtty())) {
				String docType = ((PurchaseRegister) invoice).getCdnur().get(0).getNtty();
				if ("Credit".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setNtty("C");
				} else if ("Debit".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setNtty("D");
				} else if ("Refund".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setNtty("R");
				}
			}

			if (isNotEmpty(((PurchaseRegister) invoice).getCdnur().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((PurchaseRegister) invoice).getCdnur().get(0).getIdt(), patterns);

					((PurchaseRegister) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((PurchaseRegister) invoice).getCdnur().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((PurchaseRegister) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((PurchaseRegister) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getpGst())) {
				String pGst = ((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getpGst();
				if ("YES".equalsIgnoreCase(pGst) || "Y".equalsIgnoreCase(pGst)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(pGst) || "N".equalsIgnoreCase(pGst)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setpGst("N");
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getCdnur().get(0).getpGst())) {
				String docType = ((PurchaseRegister) invoice).getCdnur().get(0).getpGst();
				if ("YES".equalsIgnoreCase(docType) || "Y".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(docType) || "N".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setpGst("N");
				}
			}
		}
		if (isNotEmpty(branch)) {
			invoice.setBranch(branch);
		}else {
			invoice.setBranch("Main Branch");
		}
		if (isNotEmpty(vertical)) {
			invoice.setVertical(vertical);
		}
		
		return invoice;
	}

	@Override
	public InvoiceParent importInvoicesTally(InvoiceParent invoice, Client client, String returnType, String branch, String vertical, int month, int year, String[] patterns, Map<String, String> statesMap) {
		String stateName = client.getStatename();
		if (isNotEmpty(invoice.getStrDate())) {
			invoice.setDateofinvoice(invdate(invoice.getStrDate(),patterns,year,month));
		}
		if (isNotEmpty(invoice.getSection()) && isNotEmpty(invoice.getTcstdspercentage())) {
			invoice.setTdstcsenable(true);
		}
		if (isNotEmpty(invoice.getSection())) {
			invoiceSection(invoice,invoice.getSection());
		}
		if (isNotEmpty(invoice.getTransactionDate())) {
			invoice.setBillDate(invdate(invoice.getTransactionDate(),patterns,year,month));
		}else {
			if(isNotEmpty(invoice.getDateofinvoice())) {
				invoice.setBillDate(invoice.getDateofinvoice());
			}
		}
		if (isNotEmpty(invoice.getItcClaimedDate())) {
			invoice.setDateofitcClaimed(invdate(invoice.getItcClaimedDate(),patterns,year,month));
		}
		boolean interStateFlag = false;
		if (returnType.equals(MasterGSTConstants.GSTR2) || returnType.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
			if(isEmpty(invoice.getStatename())) {
				invoice.setStatename(client.getStatename());
			}
		}
		if (isNotEmpty(invoice.getStatename())) {
			String statename = invoice.getStatename();
			if (statename.contains("-")) {
				statename = statename.substring(statename.indexOf("-")+1).trim();
			}
			if(isNotEmpty(statename) && statename.contains("&")) {
				statename = invStatename(statename);
			}
			statename = statesMap.get(statename.replaceAll("\\s", "").toLowerCase());
			invoice.setStatename(statename);
		}
		

		if (isNotEmpty(invoice.getRevchargetype())) {
			String revChargeType = invoice.getRevchargetype();
			if ("YES".equalsIgnoreCase(revChargeType) || "Y".equalsIgnoreCase(revChargeType)) {
				invoice.setRevchargetype("Reverse");
			} else if ("NO".equalsIgnoreCase(revChargeType) || "N".equalsIgnoreCase(revChargeType)) {
				invoice.setRevchargetype("Regular");
			}
		}
		String exptyp = "";
		if (isNotEmpty(invoice.getExp().get(0).getExpTyp())) {
			String expType = invoice.getExp().get(0).getExpTyp();
			if ("Export with IGST".equalsIgnoreCase(expType)) {
				invoice.getExp().get(0).setExpTyp("WPAY");
				exptyp = "WPAY";
			} else if ("Export under Bond/LUT".equalsIgnoreCase(expType)) {
				invoice.getExp().get(0).setExpTyp("WOPAY");
				exptyp = "WOPAY";
			}else if ("WOPAY".equalsIgnoreCase(expType)) {
				exptyp = "WOPAY";
			}else if ("WPAY".equalsIgnoreCase(expType)) {
				exptyp = "WPAY";
			}

		}
		if (isNotEmpty(invoice.getNil().getInv().get(0).getSplyType())) {
			invoice.setGenerateMode(MasterGSTConstants.TALLY_TEMPLATE+"-"+MasterGSTConstants.NIL);
			String type = invoice.getNil().getInv().get(0).getSplyType();
			if ("Inter-State supplies to registered persons".equals(type)) {
				 invoice.getNil().getInv().get(0).setSplyType("INTRB2B");
			} else if ("Intra-State supplies to registered persons".equals(type)) {
				 invoice.getNil().getInv().get(0).setSplyType("INTRAB2B");
			}else if ("Inter-State supplies to unregistered persons".equals(type)) {
				 invoice.getNil().getInv().get(0).setSplyType("INTRB2C");
			}else if ("Intra-State supplies to unregistered persons".equals(type)) {
				 invoice.getNil().getInv().get(0).setSplyType("INTRAB2C");
			}
			nilSupplyTally(invoice);
			if (isEmpty(invoice.getNil().getInv().get(0).getExptAmt())) {
				invoice.getNil().getInv().get(0).setExptAmt(0d);
			}
			if (isEmpty(invoice.getNil().getInv().get(0).getNilAmt())) {
				invoice.getNil().getInv().get(0).setNilAmt(0d);
			}
			if (isEmpty(invoice.getNil().getInv().get(0).getNgsupAmt())) {
				invoice.getNil().getInv().get(0).setNgsupAmt(0d);
			}
		}
		if (returnType.equals(MasterGSTConstants.GSTR2) || returnType.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
			if (isNotEmpty(((PurchaseRegister) invoice).getImpGoods().get(0).getIsSez())) {
				String revChargeType = ((PurchaseRegister) invoice).getImpGoods().get(0).getIsSez();
				if ("YES".equalsIgnoreCase(revChargeType) || "Y".equalsIgnoreCase(revChargeType)) {
					((PurchaseRegister) invoice).getImpGoods().get(0).setIsSez("Y");
				} else if ("NO".equalsIgnoreCase(revChargeType) || "N".equalsIgnoreCase(revChargeType)) {
					((PurchaseRegister) invoice).getImpGoods().get(0).setIsSez("N");
				}
			}
		}
		String invTyps = "";
		if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
			String invTyp = invoice.getB2b().get(0).getInv().get(0).getInvTyp();
			if ("Regular".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
				invTyps = "R";
			} else if ("Deemed Exports".equalsIgnoreCase(invTyp) || "Deemed Exp".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("DE");
				invTyps = "DE";
			} else if ("Supplies to SEZ with payment".equalsIgnoreCase(invTyp) || "SEZ supplies with payment".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWP");
				invTyps = "SEWP";
			} else if ("Supplies to SEZ without payment".equalsIgnoreCase(invTyp) || "SEZ supplies without payment".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWOP");
				invTyps = "SEWOP";
			} else if ("Sale from Bonded Warehouse".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("CBW");
				invTyps = "CBW";
			} else {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp(invTyp);
				invTyps = invTyp;
			}
		} else {
			invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
			invTyps = "R";
		}
		
		if ("GSTR1".equals(returnType) || "Sales Register".equals(returnType) || "SalesRegister".equals(returnType)) {
			if (isNotEmpty(((GSTR1) invoice)) && isNotEmpty(((GSTR1) invoice).getCdnr()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0)) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0)) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInvTyp())) {
				String invTyp = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInvTyp();
				if ("Regular".equals(invTyp)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setInvTyp("R");
					invTyps = "R";
				} else if ("Deemed Exports".equals(invTyp)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setInvTyp("DE");
					invTyps = "DE";
				} else if ("Supplies to SEZ with payment".equals(invTyp)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setInvTyp("SEWP");
					invTyps = "SEWP";
				} else if ("Supplies to SEZ without payment".equals(invTyp)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setInvTyp("SEWOP");
					invTyps = "SEWOP";
				} else if ("Sale from Bonded Warehouse".equals(invTyp)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setInvTyp("CBW");
					invTyps = "CBW";
				} else {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setInvTyp(invTyp);
					invTyps = invTyp;
				}
			}
			if (isNotEmpty(((GSTR1) invoice)) && isNotEmpty(((GSTR1) invoice).getCdnur()) && isNotEmpty(((GSTR1) invoice).getCdnur().get(0)) && isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getTyp())) {
				String invTyp = ((GSTR1) invoice).getCdnur().get(0).getTyp();
				if ("Exports With Payment".equalsIgnoreCase(invTyp)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("EXPWP");
					invTyps = "EXPWP";
				} else if ("Exports Without Payment".equalsIgnoreCase(invTyp)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("EXPWOP");
					invTyps = "EXPWOP";
				} else if ("B2CL".equalsIgnoreCase(invTyp)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("B2CL");
					invTyps = "B2CL";
				} else if ("B2CS".equalsIgnoreCase(invTyp)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("B2CS");
					invTyps = "B2CS";
				}else {
					((GSTR1) invoice).getCdnur().get(0).setTyp(invTyp);
					invTyps = invTyp;
				}
			}
			if (stateName.equalsIgnoreCase(invoice.getStatename())) {
				interStateFlag = false;
			} else {
				interStateFlag = true;
			}
		}else {
			String statenameFromGstin = "";
			String pos = "";
			if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && isNotEmpty(invoice.getB2b().get(0).getCtin().trim())) {
				statenameFromGstin = (invoice.getB2b().get(0).getCtin().trim()).substring(0, 2);
			}
			if(isNotEmpty(stateName)) {
				String[] nm = (stateName).split("-");
				pos = nm[0];
			}
			if(isNotEmpty(statenameFromGstin) && isNotEmpty(pos)) {
				if (statenameFromGstin.equals(pos)) {
					interStateFlag = false;
				} else {
					interStateFlag = true;
				}
				
			}else {
				interStateFlag = true;
			}
		}
			
		if("WPAY".equalsIgnoreCase(exptyp) || "WOPAY".equalsIgnoreCase(exptyp) || "SEWP".equalsIgnoreCase(invTyps) || "SEWOP".equalsIgnoreCase(invTyps) || "EXPWP".equalsIgnoreCase(invTyps) || "EXPWOP".equalsIgnoreCase(invTyps) ) {
			interStateFlag = true;
		}
		if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
			if(isEmpty(invoice.getItems().get(0).getRateperitem())) {
				invoice.getItems().get(0).setRateperitem(invoice.getItems().get(0).getTaxablevalue());
			}
			if(isEmpty(invoice.getItems().get(0).getQuantity())) {
				invoice.getItems().get(0).setQuantity(1d);
			}
		}
		Double cessAmount = 0d;
		if (isNotEmpty(invoice.getItems().get(0).getTaxablevalue())
				&& isNotEmpty(invoice.getItems().get(0).getCessrate())) {
			if(isNotEmpty(invoice.getItems().get(0).getCessrate())) {
				Double cessRate = invoice.getItems().get(0).getCessrate();
				Double taxableValue = invoice.getItems().get(0).getTaxablevalue();
				if("WOPAY".equalsIgnoreCase(exptyp) || "SEWOP".equalsIgnoreCase(invTyps) || "EXPWOP".equalsIgnoreCase(invTyps)) {
					invoice.getItems().get(0).setCessamount(0d);
				}else {
					cessAmount = taxableValue * cessRate / 100;
					invoice.getItems().get(0).setCessamount(Math.round(cessAmount * 100.0) / 100.0);
				}
			}
		}
		if("GSTR1".equalsIgnoreCase(returnType) || "Sales Register".equalsIgnoreCase(returnType) || "SalesRegister".equalsIgnoreCase(returnType)) {
		if (isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && isNotEmpty(invoice.getItems().get(0).getRate())) {
			if (interStateFlag) {
				Double rate = invoice.getItems().get(0).getRate();
				Double taxableValue = invoice.getItems().get(0).getTaxablevalue();
				invoice.getItems().get(0).setIgstrate(rate);
				if("WOPAY".equalsIgnoreCase(exptyp) || "SEWOP".equalsIgnoreCase(invTyps) || "EXPWOP".equalsIgnoreCase(invTyps)) {
					invoice.getItems().get(0).setIgstamount(0d);
				}else {
					Double igstAmount = taxableValue * rate / 100;
					invoice.getItems().get(0).setIgstamount(Math.round(igstAmount * 100.0) / 100.0);
				}
			} else {
				Double rate = invoice.getItems().get(0).getRate();
				Double taxableValue = invoice.getItems().get(0).getTaxablevalue();
				invoice.getItems().get(0).setCgstrate(rate / 2);
				invoice.getItems().get(0).setSgstrate(rate / 2);
				Double csgstAmount = (taxableValue * rate / 100) / 2;
				invoice.getItems().get(0).setCgstamount(Math.round(csgstAmount * 100.0) / 100.0);
				invoice.getItems().get(0).setSgstamount(Math.round(csgstAmount * 100.0) / 100.0);
			}
		}
		}
		if("GSTR2".equalsIgnoreCase(returnType) || "Purchase Register".equalsIgnoreCase(returnType) || "PurchaseRegister".equalsIgnoreCase(returnType)) {
			if(isEmpty(invoice.getItems().get(0).getElg())) {
				invoice.getItems().get(0).setElg("ip");
			}
			Double tax = 0d;
			Double taxAvail = 0d;
			if(isEmpty(invoice.getItems().get(0).getElgpercent())) {
				if(interStateFlag && isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
					tax += invoice.getItems().get(0).getIgstamount();
				}else {
					if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
						tax += invoice.getItems().get(0).getCgstamount();
					}
					if(isNotEmpty(invoice.getItems().get(0).getSgstamount())) {
						tax += invoice.getItems().get(0).getSgstamount();
					}
				}
				if(interStateFlag && isNotEmpty(invoice.getItems().get(0).getIgstavltax())) {
					taxAvail += invoice.getItems().get(0).getIgstavltax();
				}else {
					if(isNotEmpty(invoice.getItems().get(0).getCgstavltax())) {
						taxAvail += invoice.getItems().get(0).getCgstavltax();
					}
					if(isNotEmpty(invoice.getItems().get(0).getSgstavltax())) {
						taxAvail += invoice.getItems().get(0).getSgstavltax();
					}
				}
				if(tax-taxAvail == 0d) {
					invoice.getItems().get(0).setElgpercent(100d);
				}else {
					Double elgpercent = (tax-taxAvail)/100;
					invoice.getItems().get(0).setElgpercent(elgpercent);
				}
			}
		}
		if(isEmpty(invoice.getItems().get(0).getRate())) {
			Double tax = 0d;
			Double rate = 0d;
			Double taxrate = 0d;
			if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
				if(interStateFlag && isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
					tax += invoice.getItems().get(0).getIgstamount();
				}else {
					if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
						tax += invoice.getItems().get(0).getCgstamount();
					}
					if(isNotEmpty(invoice.getItems().get(0).getSgstamount())) {
						tax += invoice.getItems().get(0).getSgstamount();
					}
				}
				
				rate = (tax*100)/(invoice.getItems().get(0).getTaxablevalue());
				taxrate = findClosest(statndardTaxRate,taxrate);
			}
			invoice.getItems().get(0).setRate(rate);
		}
		
		if(isEmpty(invoice.getItems().get(0).getTotal())) {
			Double total = 0d;
			if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
				total += invoice.getItems().get(0).getTaxablevalue();
			}
			if(interStateFlag && isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
				total += invoice.getItems().get(0).getIgstamount();
			}else {
				if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
					total += invoice.getItems().get(0).getCgstamount();
				}
				if(isNotEmpty(invoice.getItems().get(0).getSgstamount())) {
					total += invoice.getItems().get(0).getSgstamount();
				}
			}
			invoice.getItems().get(0).setTotal(total);
			
		}else{
			Double total = 0d;
			if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
				total += invoice.getItems().get(0).getTaxablevalue();
			}
			if(interStateFlag && isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
				total += invoice.getItems().get(0).getIgstamount();
			}else {
				if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
					total += invoice.getItems().get(0).getCgstamount();
				}
				if(isNotEmpty(invoice.getItems().get(0).getSgstamount())) {
					total += invoice.getItems().get(0).getSgstamount();
				}
			}
			
			Double originaltotal = invoice.getItems().get(0).getTotal();
			if(originaltotal - total > 2) {
				invoice.getItems().get(0).setTotal(total);
			} 
		}
		
		if (isNotEmpty(invoice.getItems().get(0).getItcRevtype())) {
			String itctype = invoice.getItems().get(0).getItcRevtype();
			if ("Amount in terms of rule 37(2)".equals(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule2_2");
			} else if ("Amount in terms of rule 42(1)(m)".equals(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule7_1_m");
			} else if ("Amount in terms of rule 43(1)(h)".equals(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule8_1_h");
			} else if ("Amount in terms of rule 42(2)(a)".equals(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule7_2_a");
			} else if ("Amount in terms of rule 42(2)(b)".equals(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule7_2_b");
			} else if ("On account of amount paid subsequent to reversal of ITC"
					.equals(itctype)) {
				invoice.getItems().get(0).setItcRevtype("revitc");
			} else if ("Any other liability (Pl specify)".equals(itctype)) {
				invoice.getItems().get(0).setItcRevtype("other");
			}
		}
		if (isNotEmpty(invoice.getItems().get(0).getElg())) {
			String elgType = invoice.getItems().get(0).getElg();
			if ("Capital Good".equals(elgType)) {
				invoice.getItems().get(0).setElg("cp");
			} else if ("Inputs".equals(elgType)) {
				invoice.getItems().get(0).setElg("ip");
			} else if ("Input Service".equals(elgType)) {
				invoice.getItems().get(0).setElg("is");
			} else if ("Ineligible".equals(elgType)) {
				invoice.getItems().get(0).setElg("no");
			} else if ("Not Selected".equals(elgType)) {
				invoice.getItems().get(0).setElg("pending");
			}
		}
		if (returnType.equals(MasterGSTConstants.GSTR1)) {
			if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
				String docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
				if ("Credit".equals(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("C");
				} else if ("Debit".equals(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("D");
				} else if ("Refund".equals(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("R");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt(),patterns);
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
				String docType = ((GSTR1) invoice).getCdnur().get(0).getNtty();
				if ("Credit".equals(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setNtty("C");
				} else if ("Debit".equals(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setNtty("D");
				} else if ("Refund".equals(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setNtty("R");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((GSTR1) invoice).getCdnur().get(0).getIdt(),patterns);
					((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((GSTR1) invoice).getCdnur().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getItems().get(0).getAdvReceiptDate())) {
				try {
					Date invDate = DateUtils.parseDate(((GSTR1) invoice).getItems().get(0).getAdvReceiptDate(), patterns);
					((GSTR1) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((GSTR1) invoice).getItems().get(0).getAdvReceiptDate());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((GSTR1) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((GSTR1) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getItems().get(0).getAdvStateName())) {
				String statename = ((GSTR1) invoice).getItems().get(0).getAdvStateName();
				if (statename.contains("-")) {
					statename = statename.substring(statename.indexOf("-")+1).trim();
				}
				if(isNotEmpty(statename) && statename.contains("&")) {
					statename = invStatename(statename);
				}
				statename = statesMap.get(statename.replaceAll("\\s", "").toLowerCase());
				((GSTR1) invoice).getItems().get(0).setAdvStateName(statename);
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getpGst())) {
				String pGst = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getpGst();
				if ("YES".equalsIgnoreCase(pGst) || "Y".equalsIgnoreCase(pGst)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(pGst) || "N".equalsIgnoreCase(pGst)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setpGst("N");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getpGst())) {
				String docType = ((GSTR1) invoice).getCdnur().get(0).getpGst();
				if ("YES".equalsIgnoreCase(docType) || "Y".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(docType) || "N".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setpGst("N");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getTyp())) {
				String type = ((GSTR1) invoice).getCdnur().get(0).getTyp();
				if ("Exports With Payment".equals(type)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("EXPWP");
				} else if ("Exports Without Payment".equals(type)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("EXPWOP");
				} else if ("B2CL".equals(type)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("B2CL");
				}
			}
		} else if (returnType.equals(MasterGSTConstants.GSTR2)|| returnType.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
			if (isNotEmpty(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getNtty())) {
				String docType = ((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getNtty();
				if ("Credit".equals(docType)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setNtty("C");
				} else if ("Debit".equals(docType)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setNtty("D");
				} else if ("Refund".equals(docType)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setNtty("R");
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getIdt(), patterns);

					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getItems().get(0).getAdvReceiptDate())) {
				try {
					Date invDate = DateUtils.parseDate(((PurchaseRegister) invoice).getItems().get(0).getAdvReceiptDate(),patterns);

					((PurchaseRegister) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((PurchaseRegister) invoice).getItems().get(0).getAdvReceiptDate());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((PurchaseRegister) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((PurchaseRegister) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getItems().get(0).getAdvStateName())) {
				String statename = ((PurchaseRegister) invoice).getItems().get(0).getAdvStateName();
				if (statename.contains("-")) {
					statename = statename.substring(statename.indexOf("-")+1).trim();
				}
				if(isNotEmpty(statename) && statename.contains("&")) {
					statename = invStatename(statename);
				}
				statename = statesMap.get(statename.replaceAll("\\s", "").toLowerCase());
				((PurchaseRegister) invoice).getItems().get(0).setAdvStateName(statename);
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getCdnur().get(0).getNtty())) {
				String docType = ((PurchaseRegister) invoice).getCdnur().get(0).getNtty();
				if ("Credit".equals(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setNtty("C");
				} else if ("Debit".equals(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setNtty("D");
				} else if ("Refund".equals(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setNtty("R");
				}
			}

			if (isNotEmpty(((PurchaseRegister) invoice).getCdnur().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((PurchaseRegister) invoice).getCdnur().get(0).getIdt(), patterns);

					((PurchaseRegister) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((PurchaseRegister) invoice).getCdnur().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((PurchaseRegister) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((PurchaseRegister) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getpGst())) {
				String pGst = ((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getpGst();
				if ("YES".equalsIgnoreCase(pGst) || "Y".equalsIgnoreCase(pGst)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(pGst) || "N".equalsIgnoreCase(pGst)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setpGst("N");
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getCdnur().get(0).getpGst())) {
				String docType = ((PurchaseRegister) invoice).getCdnur().get(0).getpGst();
				if ("YES".equalsIgnoreCase(docType) || "Y".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(docType) || "N".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setpGst("N");
				}
			}
		}
		if(isNotEmpty(invoice.getItems())) {
			List<Item> itms = Lists.newArrayList();
			for(Item item: invoice.getItems()){
				if(isNotEmpty(item)) {
					Item itm = changeInvoiceAmounts(item);
					itms.add(itm);
				}
			}
			if(isNotEmpty(itms)) {
				invoice.setItems(itms);
			}
		}
		if (isNotEmpty(invoice.getStrDate())) {
			invoice.setDateofinvoice(invdate(invoice.getStrDate(),patterns,year,month));
		} else {
			Calendar cal = Calendar.getInstance();
			cal.set(year, month - 1, 1);
			invoice.setDateofinvoice(cal.getTime());
		}
		if (isNotEmpty(branch)) {
			invoice.setBranch(branch);
		}else {
			invoice.setBranch("Main Branch");
		}
		if (isNotEmpty(vertical)) {
			invoice.setVertical(vertical);
		}
		return invoice;
	}

	@Override
	public InvoiceParent importInvoicesSimplified(InvoiceParent invoice, Client client, String returnType,
			String branch, String vertical, int month, int year, String[] patterns,Map<String, String> statesMap) {
		String stateName = client.getStatename();
		if(returnType.equals(MasterGSTConstants.PURCHASE_REGISTER) || returnType.equals(MasterGSTConstants.GSTR2)) {
			if(isEmpty(invoice.getStatename())) {
				if(isNotEmpty(client.getStatename())) {
					invoice.setStatename(client.getStatename());
				}
			}
		}
		if (isNotEmpty(invoice.getStrDate())) {
			invoice.setDateofinvoice(invdate(invoice.getStrDate(),patterns,year,month));
		}
		if (isNotEmpty(invoice.getSection()) && isNotEmpty(invoice.getTcstdspercentage())) {
			invoice.setTdstcsenable(true);
		}
		if (isNotEmpty(invoice.getSection())) {
			invoiceSection(invoice,invoice.getSection());
		}
		if (isNotEmpty(invoice.getTransactionDate())) {
			invoice.setBillDate(invdate(invoice.getTransactionDate(),patterns,year,month));
		}else {
			if(isNotEmpty(invoice.getDateofinvoice())) {
				invoice.setBillDate(invoice.getDateofinvoice());
			}
		}
		if (isNotEmpty(invoice.getItcClaimedDate())) {
			invoice.setDateofitcClaimed(invdate(invoice.getItcClaimedDate(),patterns,year,month));
		}
		boolean interStateFlag = false;
		if (isNotEmpty(invoice.getStatename())) {
			String statename = invoice.getStatename();
			if (statename.contains("-")) {
				statename = statename.substring(statename.indexOf("-")+1).trim();
			}
			if(isNotEmpty(statename) && statename.contains("&")) {
				statename = invStatename(statename);
			}
			statename = statesMap.get(statename.replaceAll("\\s", "").toLowerCase());
			invoice.setStatename(statename);
		}
		

		if (isNotEmpty(invoice.getRevchargetype())) {
			String revChargeType = invoice.getRevchargetype();
			if ("YES".equalsIgnoreCase(revChargeType) || "Y".equalsIgnoreCase(revChargeType)) {
				invoice.setRevchargetype("Reverse");
			} else if ("NO".equalsIgnoreCase(revChargeType) || "N".equalsIgnoreCase(revChargeType)) {
				invoice.setRevchargetype("Regular");
			}
		}
		String exptyp = "";
		if (isNotEmpty(invoice.getExp().get(0).getExpTyp())) {
			String expType = invoice.getExp().get(0).getExpTyp();
			if ("Export with IGST".equalsIgnoreCase(expType) || "WPAY".equalsIgnoreCase(expType)) {
				invoice.getExp().get(0).setExpTyp("WPAY");
				exptyp = "WPAY";
			} else if ("Export under Bond/LUT".equalsIgnoreCase(expType) || "WOPAY".equalsIgnoreCase(expType)) {
				invoice.getExp().get(0).setExpTyp("WOPAY");
				exptyp = "WOPAY";
			}

		}
		if (returnType.equals(MasterGSTConstants.GSTR2) || returnType.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
			if (isNotEmpty(((PurchaseRegister) invoice).getImpGoods().get(0).getIsSez())) {
				String revChargeType = ((PurchaseRegister) invoice).getImpGoods().get(0).getIsSez();
				if ("YES".equalsIgnoreCase(revChargeType) || "Y".equalsIgnoreCase(revChargeType)) {
					((PurchaseRegister) invoice).getImpGoods().get(0).setIsSez("Y");
				} else if ("NO".equalsIgnoreCase(revChargeType) || "N".equalsIgnoreCase(revChargeType)) {
					((PurchaseRegister) invoice).getImpGoods().get(0).setIsSez("N");
				}
			}
		}
		String invTyps = "";
		if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
			String invTyp = invoice.getB2b().get(0).getInv().get(0).getInvTyp();
			if ("Regular".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
				invTyps = "R";
			} else if ("Deemed Exports".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("DE");
				invTyps = "DE";
			} else if ("Supplies to SEZ with payment".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWP");
				invTyps = "SEWP";
			} else if ("Supplies to SEZ without payment".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWOP");
				invTyps = "SEWOP";
			} else if ("Sale from Bonded Warehouse".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("CBW");
				invTyps = "CBW";
			} else {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp(invTyp);
				invTyps = invTyp;
			}
		} else {
			invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
			invTyps = "R";
		}
		
		if ("GSTR1".equals(returnType) || "Sales Register".equals(returnType) || "SalesRegister".equals(returnType)) {
			if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInvTyp())) {
				String invTyp = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInvTyp();
				if ("Regular".equals(invTyp)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setInvTyp("R");
					invTyps = "R";
				} else if ("Deemed Exports".equals(invTyp)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setInvTyp("DE");
					invTyps = "DE";
				} else if ("Supplies to SEZ with payment".equals(invTyp)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setInvTyp("SEWP");
					invTyps = "SEWP";
				} else if ("Supplies to SEZ without payment".equals(invTyp)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setInvTyp("SEWOP");
					invTyps = "SEWOP";
				} else if ("Sale from Bonded Warehouse".equals(invTyp)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setInvTyp("CBW");
					invTyps = "CBW";
				} else {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setInvTyp(invTyp);
					invTyps = invTyp;
				}
			}
			if (isNotEmpty(((GSTR1) invoice)) && isNotEmpty(((GSTR1) invoice).getCdnur()) && isNotEmpty(((GSTR1) invoice).getCdnur().get(0)) && isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getTyp())) {
				String invTyp = ((GSTR1) invoice).getCdnur().get(0).getTyp();
				if ("Exports With Payment".equalsIgnoreCase(invTyp)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("EXPWP");
					invTyps = "EXPWP";
				} else if ("Exports Without Payment".equalsIgnoreCase(invTyp)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("EXPWOP");
					invTyps = "EXPWOP";
				} else if ("B2CL".equalsIgnoreCase(invTyp)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("B2CL");
					invTyps = "B2CL";
				} else if ("B2CS".equalsIgnoreCase(invTyp)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("B2CS");
					invTyps = "B2CS";
				}else {
					((GSTR1) invoice).getCdnur().get(0).setTyp(invTyp);
					invTyps = invTyp;
				}
			}
			if (stateName.equalsIgnoreCase(invoice.getStatename())) {
				interStateFlag = false;
			} else {
				interStateFlag = true;
			}
		}else {
			String statenameFromGstin = "";
			String pos = "";
			if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && isNotEmpty(invoice.getB2b().get(0).getCtin().trim())) {
				statenameFromGstin = (invoice.getB2b().get(0).getCtin().trim()).substring(0, 2);
			}
			if(isNotEmpty(stateName)) {
				String[] nm = (stateName).split("-");
				pos = nm[0];
			}
			if(isNotEmpty(statenameFromGstin) && isNotEmpty(pos)) {
				if (statenameFromGstin.equals(pos)) {
					interStateFlag = false;
				} else {
					interStateFlag = true;
				}
				
			}else {
				interStateFlag = true;
			}
		}
			
		if("WPAY".equalsIgnoreCase(exptyp) || "WOPAY".equalsIgnoreCase(exptyp) || "SEWP".equalsIgnoreCase(invTyps) || "SEWOP".equalsIgnoreCase(invTyps) || "EXPWP".equalsIgnoreCase(invTyps) || "EXPWOP".equalsIgnoreCase(invTyps) ) {
			interStateFlag = true;
		}
		Double cessAmount = 0d;
		if (isNotEmpty(invoice.getItems().get(0).getTaxablevalue())&& isNotEmpty(invoice.getItems().get(0).getCessrate())) {
			if(isNotEmpty(invoice.getItems().get(0).getCessrate())) {
				Double cessRate = invoice.getItems().get(0).getCessrate();
				Double taxableValue = invoice.getItems().get(0).getTaxablevalue();
				if("WOPAY".equalsIgnoreCase(exptyp) || "SEWOP".equalsIgnoreCase(invTyps) || "EXPWOP".equalsIgnoreCase(invTyps)) {
					invoice.getItems().get(0).setCessamount(0d);
				}else {
					cessAmount = taxableValue * cessRate / 100;
					invoice.getItems().get(0).setCessamount(Math.round(cessAmount * 100.0) / 100.0);
				}
			}
		}
		
		if (isNotEmpty(invoice.getItems().get(0).getTaxablevalue())&& isNotEmpty(invoice.getItems().get(0).getRate())) {
			if (interStateFlag) {
				Double rate = invoice.getItems().get(0).getRate();
				Double taxableValue = invoice.getItems().get(0).getTaxablevalue();
				invoice.getItems().get(0).setIgstrate(rate);
				Double igstAmount = 0d;
				if("WOPAY".equalsIgnoreCase(exptyp) || "SEWOP".equalsIgnoreCase(invTyps) || "EXPWOP".equalsIgnoreCase(invTyps)) {
					invoice.getItems().get(0).setIgstamount(0d);
				}else {
					igstAmount = taxableValue * rate / 100;
					invoice.getItems().get(0).setIgstamount(Math.round(igstAmount * 100.0) / 100.0);
				}
				if("GSTR2".equalsIgnoreCase(returnType) || "Purchase Register".equalsIgnoreCase(returnType) || "PurchaseRegister".equalsIgnoreCase(returnType)) {
					if(isNotEmpty(invoice.getItems().get(0).getElgpercent())) {
						Double elgPercent = invoice.getItems().get(0).getElgpercent();
						Double igstAmountavlTax = igstAmount * elgPercent / 100;
						invoice.getItems().get(0).setIgstavltax(Math.round(igstAmountavlTax * 100.0) / 100.0);
						Double cessAmountavlTax = cessAmount * elgPercent / 100;
						invoice.getItems().get(0).setCessavltax(Math.round(cessAmountavlTax * 100.0) / 100.0);
					}
				}
			} else {
				Double rate = invoice.getItems().get(0).getRate();
				Double taxableValue = invoice.getItems().get(0).getTaxablevalue();
				invoice.getItems().get(0).setCgstrate(rate / 2);
				invoice.getItems().get(0).setSgstrate(rate / 2);
				Double csgstAmount = (taxableValue * rate / 100) / 2;
				invoice.getItems().get(0).setCgstamount(Math.round(csgstAmount * 100.0) / 100.0);
				invoice.getItems().get(0).setSgstamount(Math.round(csgstAmount * 100.0) / 100.0);
				if("GSTR2".equalsIgnoreCase(returnType) || "Purchase Register".equalsIgnoreCase(returnType) || "PurchaseRegister".equalsIgnoreCase(returnType)) {
					if(isNotEmpty(invoice.getItems().get(0).getElgpercent())) {
						Double elgPercent = invoice.getItems().get(0).getElgpercent();
						Double csgstAmountavlTax = csgstAmount * elgPercent / 100;
						invoice.getItems().get(0).setCgstavltax(Math.round(csgstAmountavlTax * 100.0) / 100.0);
						invoice.getItems().get(0).setSgstavltax(Math.round(csgstAmountavlTax * 100.0) / 100.0);
						Double cessAmountavlTax = cessAmount * elgPercent / 100;
						invoice.getItems().get(0).setCessavltax(Math.round(cessAmountavlTax * 100.0) / 100.0);
					}
				}
			}
		}
		
		
		if (isNotEmpty(invoice.getItems().get(0).getItcRevtype())) {
			String itctype = invoice.getItems().get(0).getItcRevtype();
			if ("Amount in terms of rule 37(2)".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule2_2");
			} else if ("Amount in terms of rule 42(1)(m)".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule7_1_m");
			} else if ("Amount in terms of rule 43(1)(h)".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule8_1_h");
			} else if ("Amount in terms of rule 42(2)(a)".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule7_2_a");
			} else if ("Amount in terms of rule 42(2)(b)".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("rule7_2_b");
			} else if ("On account of amount paid subsequent to reversal of ITC".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("revitc");
			} else if ("Any other liability (Pl specify)".equalsIgnoreCase(itctype)) {
				invoice.getItems().get(0).setItcRevtype("other");
			}
		}
		if (isNotEmpty(invoice.getItems().get(0).getElg())) {
			String elgType = invoice.getItems().get(0).getElg();
			if ("Capital Good".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("cp");
			} else if ("Inputs".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("ip");
			} else if ("Input Service".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("is");
			} else if ("Ineligible".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("no");
			} else if ("Not Selected".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("pending");
			}
		}
		if (returnType.equals(MasterGSTConstants.GSTR1)) {
			if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
				String docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
				if ("Credit".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("C");
				} else if ("Debit".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("D");
				} else if ("Refund".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("R");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt(),patterns);

					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
				String docType = ((GSTR1) invoice).getCdnur().get(0).getNtty();
				if ("Credit".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setNtty("C");
				} else if ("Debit".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setNtty("D");
				} else if ("Refund".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setNtty("R");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((GSTR1) invoice).getCdnur().get(0).getIdt(),patterns);

					((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((GSTR1) invoice).getCdnur().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}

			if (isNotEmpty(
					((GSTR1) invoice).getItems().get(0).getAdvReceiptDate())) {
				try {
					Date invDate = DateUtils.parseDate(((GSTR1) invoice).getItems().get(0).getAdvReceiptDate(), patterns);

					((GSTR1) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((GSTR1) invoice).getItems().get(0).getAdvReceiptDate());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((GSTR1) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((GSTR1) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}

			if (isNotEmpty(((GSTR1) invoice).getItems().get(0).getAdvStateName())) {
				String statename = ((GSTR1) invoice).getItems().get(0).getAdvStateName();
				if (statename.contains("-")) {
					statename = statename.substring(statename.indexOf("-")+1).trim();
				}
				if(isNotEmpty(statename) && statename.contains("&")) {
					statename = invStatename(statename);
				}
				statename = statesMap.get(statename.replaceAll("\\s", "").toLowerCase());
				((GSTR1) invoice).getItems().get(0).setAdvStateName(statename);
			}

			if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getpGst())) {
				String pGst = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getpGst();
				if ("YES".equalsIgnoreCase(pGst) || "Y".equalsIgnoreCase(pGst)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(pGst) || "N".equalsIgnoreCase(pGst)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setpGst("N");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getpGst())) {
				String docType = ((GSTR1) invoice).getCdnur().get(0).getpGst();
				if ("YES".equalsIgnoreCase(docType) || "Y".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(docType) || "N".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setpGst("N");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getTyp())) {
				String type = ((GSTR1) invoice).getCdnur().get(0).getTyp();
				if ("Exports With Payment".equalsIgnoreCase(type)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("EXPWP");
				} else if ("Exports Without Payment".equalsIgnoreCase(type)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("EXPWOP");
				} else if ("B2CL".equalsIgnoreCase(type)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("B2CL");
				}
			}
		} else if (returnType.equals(MasterGSTConstants.GSTR2) || returnType.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
			if (isNotEmpty(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getNtty())) {
				String docType = ((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getNtty();
				if ("Credit".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setNtty("C");
				} else if ("Debit".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setNtty("D");
				} else if ("Refund".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setNtty("R");
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getIdt(), patterns);

					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getItems().get(0).getAdvReceiptDate())) {
				try {
					Date invDate = DateUtils.parseDate(((PurchaseRegister) invoice).getItems().get(0).getAdvReceiptDate(),patterns);

					((PurchaseRegister) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((PurchaseRegister) invoice).getItems().get(0).getAdvReceiptDate());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((PurchaseRegister) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((PurchaseRegister) invoice).getItems().get(0).setAdvReceiptDate(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getItems().get(0).getAdvStateName())) {
				String statename = ((PurchaseRegister) invoice).getItems().get(0).getAdvStateName();
				if (statename.contains("-")) {
					statename = statename.substring(statename.indexOf("-")+1).trim();
				}
				if(isNotEmpty(statename) && statename.contains("&")) {
					statename = invStatename(statename);
				}
				statename = statesMap.get(statename.replaceAll("\\s", "").toLowerCase());
				((PurchaseRegister) invoice).getItems().get(0).setAdvStateName(statename);
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getCdnur().get(0).getNtty())) {
				String docType = ((PurchaseRegister) invoice).getCdnur().get(0).getNtty();
				if ("Credit".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setNtty("C");
				} else if ("Debit".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setNtty("D");
				} else if ("Refund".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setNtty("R");
				}
			}

			if (isNotEmpty(((PurchaseRegister) invoice).getCdnur().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((PurchaseRegister) invoice).getCdnur().get(0).getIdt(), patterns);

					((PurchaseRegister) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((PurchaseRegister) invoice).getCdnur().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((PurchaseRegister) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((PurchaseRegister) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getpGst())) {
				String pGst = ((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).getpGst();
				if ("YES".equalsIgnoreCase(pGst) || "Y".equalsIgnoreCase(pGst)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(pGst) || "N".equalsIgnoreCase(pGst)) {
					((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setpGst("N");
				}
			}
			if (isNotEmpty(((PurchaseRegister) invoice).getCdnur().get(0).getpGst())) {
				String docType = ((PurchaseRegister) invoice).getCdnur().get(0).getpGst();
				if ("YES".equalsIgnoreCase(docType) || "Y".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(docType) || "N".equalsIgnoreCase(docType)) {
					((PurchaseRegister) invoice).getCdnur().get(0).setpGst("N");
				}
			}
		}
		if (isNotEmpty(invoice.getStrDate())) {
			invoice.setDateofinvoice(invdate(invoice.getStrDate(),patterns,year,month));
		} else {
			Calendar cal = Calendar.getInstance();
			cal.set(year, month - 1, 1);
			invoice.setDateofinvoice(cal.getTime());
		}
		if (isNotEmpty(branch)) {
			invoice.setBranch(branch);
		}else {
			invoice.setBranch("Main Branch");
		}
		if (isNotEmpty(vertical)) {
			invoice.setVertical(vertical);
		}
		return invoice;
	}

	@Override
	public Date invdate(String invdate, String[] patterns, int year, int month) {
		Date invoiceDate =new Date();
		try {
			if(invdate.contains("-") || invdate.contains("/")) {
				try {
					double dblDate = Double.parseDouble(invdate);
					invoiceDate = DateUtil.getJavaDate(dblDate);
				} catch (NumberFormatException exp) {
					Calendar cal = Calendar.getInstance();
					if(invdate.contains("-")) {
						String date[] = invdate.split("-");
						if(date.length >= 3) {
						int mnths = 1;
						int years = Integer.parseInt(date[2].replaceAll("\\s", ""));
						if(date[1].length()>2) {
							String mnth = date[1].toUpperCase().replaceAll("\\s", "");
							mnths = month(mnth);
						}else {
							mnths = Integer.parseInt(date[1].replaceAll("\\s", ""));
						}
						if(date[2].length()<=2) {
							years = Integer.parseInt("20"+date[2].replaceAll("\\s", ""));
						}else {
							years = Integer.parseInt(date[2].replaceAll("\\s", ""));
						}
						cal.set(years, mnths - 1, Integer.parseInt(date[0].replaceAll("\\s", "")));
						invoiceDate = cal.getTime();
						}
					}else if(invdate.contains("/")) {
						String date[] = invdate.split("/");
						if(date.length >= 3) {
						int mnths = 1;
						if(date[1].length()>2) {
							String mnth = date[1].toUpperCase().replaceAll("\\s", "");
							mnths = month(mnth);
						}else {
							mnths = Integer.parseInt(date[1].replaceAll("\\s", ""));
						}
						int years = Integer.parseInt(date[2].replaceAll("\\s", ""));
						if(date[2].length()<=2) {
							years = Integer.parseInt("20"+date[2].replaceAll("\\s", ""));
						}else {
							years = Integer.parseInt(date[2].replaceAll("\\s", ""));
						}
						cal.set(years, mnths - 1, Integer.parseInt(date[0].replaceAll("\\s", "")));
						invoiceDate = cal.getTime();
						}
					}
				}
			}else {
				invoiceDate = DateUtils.parseDate(invdate,patterns);
			}
		} catch (java.text.ParseException e) {
			try {
				double dblDate = Double.parseDouble(invdate);
				invoiceDate = DateUtil.getJavaDate(dblDate);
			} catch (NumberFormatException exp) {
				Calendar cal = Calendar.getInstance();
				cal.set(year, month - 1, 1);
				invoiceDate = cal.getTime();
			}
		}
		return invoiceDate;
	}

	@Override
	public int month(String mnth) {
		int mnths = 1;
		switch(mnth) {
		case "JAN": mnths = 1;
		break;
		case "FEB": mnths = 2;
		break;
		case "MAR": mnths = 3;
		break;
		case "APR": mnths = 4;
		break;
		case "MAY": mnths = 5;
		break;
		case "JUN": mnths = 6;
		break;
		case "JUL": mnths = 7;
		break;
		case "AUG": mnths = 8;
		break;
		case "SEP": mnths = 9;
		break;
		case "OCT": mnths = 10;
		break;
		case "NOV": mnths = 11;
		break;
		case "DEC": mnths = 12;
		break;
		default : mnths = 1;
		break;
		}
		return mnths;
	}

	@Override
	public String errorMsg(String key, InvoiceParent invoice, String returntype, boolean hsnqtyrate,String template) {
		String errorVal = "";
		if(returntype.equals(MasterGSTConstants.GSTR1) && (key.equals("creditList") || key.equals("cdnurList")	|| key.equals("exportList"))) {
			if(isEmpty(invoice.getStatename())) {
				errorVal += "Please Enter Statename,";
			}
			if(isEmpty(invoice.getStrDate()) || "Invoice Date*".equals(invoice.getStrDate()) || "Credit/Debit Note Date*".equals(invoice.getStrDate())) {
				errorVal += "Please Enter Invoice Date,";
			}
			if(!hsnqtyrate) {
				if(isEmpty(invoice.getItems().get(0).getHsn())) {
					errorVal += "Please Enter HSN,";
				}
				if(isEmpty(invoice.getItems().get(0).getQuantity())) {
					errorVal += "Please Enter Quantity,";
				}else if((invoice.getItems().get(0).getQuantity() <= 0)) {
					errorVal += "Please Enter Valid Quantity,";
				}
				if((isNotEmpty(invoice.getItems().get(0).getRateperitem())	&& invoice.getItems().get(0).getRateperitem() < 0)) {
					errorVal += "Please Enter valid Item Rate,";
				}
			}
			if(key.equals("creditList")) {
				if(isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())) {
					errorVal += "Please Enter GSTIN Number,";
				}
				if(isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
					errorVal += "Please Enter Document Type,";
				}
			}
			if(key.equals("cdnurList")) {
				if(isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
					errorVal += "Please Enter Document Type,";
				}
			}
			if(key.equals("exportList")) {
				if(isEmpty(invoice.getExp().get(0).getExpTyp())) {
					errorVal += "Please Enter Export Type,";
				}
			}
			if(template.equalsIgnoreCase("custom")) {
				if((isNotEmpty(invoice.getItems().get(0).getRate())	&& invoice.getItems().get(0).getRate() < 0)) {
					errorVal += "Please Enter valid Rate";
				}
			}else {
			
				if((isNotEmpty(invoice.getItems().get(0).getIgstrate())	&& invoice.getItems().get(0).getIgstrate() < 0)) {
					errorVal += "Please Enter valid Igst Rate,";
				}
				if((isNotEmpty(invoice.getItems().get(0).getCgstrate())	&& invoice.getItems().get(0).getCgstrate() < 0)) {
					errorVal += "Please Enter valid Cgst Rate,";
				}
				if((isNotEmpty(invoice.getItems().get(0).getSgstrate())	&& invoice.getItems().get(0).getSgstrate() < 0)) {
					errorVal += "Please Enter valid Sgst Rate,";
				}
				if((isNotEmpty(invoice.getItems().get(0).getCessrate())	&& invoice.getItems().get(0).getCessrate() < 0)) {
					errorVal += "Please Enter valid Cess Rate,";
				}
			}
			if(isEmpty(invoice.getItems())) {
				errorVal += "Please Enter Values";
			}
		}else if(key.equals("nilList")) {
			if(isEmpty(invoice.getStatename())) {
				errorVal += "Please Enter Statename,";
			}
			if(isEmpty(invoice.getStrDate())) {
				errorVal += "Please Enter Invoice Date,";
			}
			if(!hsnqtyrate) {
				if(isEmpty(invoice.getItems().get(0).getHsn())) {
					errorVal += "Please Enter HSN,";
				}
				if(isEmpty(invoice.getItems().get(0).getQuantity())) {
					errorVal += "Please Enter Quantity,";
				}else if((invoice.getItems().get(0).getQuantity() <= 0)) {
					errorVal += "Please Enter Valid Quantity,";
				}
				if(isEmpty(invoice.getItems().get(0).getRateperitem())) {
					errorVal += "Please Enter Item Rate,";
				}
			}
			if(isEmpty(invoice.getItems().get(0).getTaxablevalue())) {
				errorVal += "Please Enter Taxable Value,";
			}
			if(isEmpty(invoice.getItems().get(0).getType())) {
				errorVal += "Please Enter Supply Type,";
			}
			if(isEmpty(invoice.getItems().get(0).getTotal())) {
				errorVal += "Please Enter Total Invoice Value,";
			}
			if(isEmpty(invoice.getItems())) {
				errorVal += "Please Enter Values";
			}
		}else if(key.equals("itrvslList")) {
			if(isEmpty(invoice.getStatename())) {
				errorVal += "Please Enter Statename,";
			}
			if(isEmpty(invoice.getStrDate())) {
				errorVal += "Please Enter Invoice Date,";
			}
			if(isEmpty(invoice.getB2b().get(0).getCtin())) {
				errorVal += "Please Enter GSTIN Number,";
			}
			if(isEmpty(invoice.getItems().get(0).getItcRevtype())) {
				errorVal += "Please Enter Rule,";
			}
			if(isEmpty(invoice.getItems().get(0).getTaxablevalue())) {
				errorVal += "Please Enter Taxable Value,";
			}
			if((isNotEmpty(invoice.getItems().get(0).getIgstamount())	&& invoice.getItems().get(0).getIgstamount() < 0)) {
				errorVal += "Please Enter valid Igst Amount,";
			}
			if((isNotEmpty(invoice.getItems().get(0).getCgstamount())	&& invoice.getItems().get(0).getCgstamount() < 0)) {
				errorVal += "Please Enter valid Cgst Amount,";
			}
			if((isNotEmpty(invoice.getItems().get(0).getSgstamount())	&& invoice.getItems().get(0).getSgstamount() < 0)) {
				errorVal += "Please Enter valid Sgst Amount,";
			}
			if((isNotEmpty(invoice.getItems().get(0).getIsdcessamount())	&& invoice.getItems().get(0).getIsdcessamount() < 0)) {
				errorVal += "Please Enter valid Cess Amount";
			}
		}else if(key.equals("impgList") || key.equals("impsList")) {
			if(isEmpty(invoice.getStatename())) {
				errorVal += "Please Enter Statename,";
			}
			if(isEmpty(invoice.getStrDate()) || "Invoice Date*".equals(invoice.getStrDate())) {
				errorVal += "Please Enter Invoice Date";
			}
		}else if(key.equals("advAdjustedList")) {
			if(isEmpty(invoice.getStrDate()) || "Invoice Date*".equals(invoice.getStrDate())) {
				errorVal += "Please Enter Invoice Date";
			}
		}
		return errorVal;
	}

	@Override
	public String tallyErrorMsg(String key, InvoiceParent invoice, String returntype) {
		String errorVal = "";
		if(returntype.equals(MasterGSTConstants.GSTR1) && (key.equals("creditList") || key.equals("cdnurList")	|| key.equals("exportList"))) {
			if(isEmpty(invoice.getStatename())) {
				errorVal += "Please Enter Statename,";
			}
			if(isEmpty(invoice.getStrDate()) || "Invoice Date*".equals(invoice.getStrDate()) || "Credit/Debit Note Date*".equals(invoice.getStrDate())) {
				errorVal += "Please Enter Invoice Date,";
			}
			if(key.equals("creditList")) {
				if(isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())) {
					errorVal += "Please Enter GSTIN Number,";
				}
				if(isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
					errorVal += "Please Enter Document Type,";
				}
			}
			if(key.equals("cdnurList")) {
				if(isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
					errorVal += "Please Enter Document Type,";
				}
			}
			if(key.equals("exportList")) {
				if(isEmpty(invoice.getExp().get(0).getExpTyp())) {
					errorVal += "Please Enter Export Type,";
				}
			}
			if(isEmpty(invoice.getItems())) {
				errorVal += "Please Enter Values";
			}
		}else if(key.equals("b2cList")) {
			if(isEmpty(invoice.getStatename()) || "Place Of Supply".equalsIgnoreCase(invoice.getStatename())) {
				errorVal += "Please Enter Place Of Supply";
			}
		}else if(key.equals("impgList")) {
			if(isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue()))) {
				errorVal += "Please Enter Values";
			}
		}else if(key.equals("nilList")) {
			if("Description".equalsIgnoreCase(invoice.getNil().getInv().get(0).getSplyType())) {
				if(isEmpty(invoice.getNil().getInv().get(0).getNilAmt()) && isEmpty(invoice.getNil().getInv().get(0).getExptAmt()) && isEmpty(invoice.getNil().getInv().get(0).getNgsupAmt())) {
					errorVal += "";
				}
			}else {
				if(isNotEmpty(invoice.getNil().getInv().get(0).getSplyType())) {
					if(isEmpty(invoice.getNil().getInv().get(0).getNilAmt()) && isEmpty(invoice.getNil().getInv().get(0).getExptAmt()) && isEmpty(invoice.getNil().getInv().get(0).getNgsupAmt())) {
						errorVal += "Please Enter Values";
					}
				}
			}
		}
		return errorVal;
	}

	
	
	@Override
	public String einvErrorMsg(String key, InvoiceParent invoice, String returntype, boolean hsnqtyrate,String template) {
		String errorVal = "";
		if(returntype.equals(MasterGSTConstants.GSTR1) && (key.equals("creditList") || key.equals("cdnurList")	|| key.equals("exportList"))) {
			if(isEmpty(invoice.getStatename())) {
				errorVal += "Please Enter Statename,";
			}
			if(isEmpty(invoice.getItems().get(0).getHsn())) {
				errorVal += "Please Enter HSN";
			}
			if(isEmpty(invoice.getEinvCategory())) {
				errorVal += "Please Enter  Invoice Category";
			}
			if(key.equals("exportList")) {
				if(isEmpty(invoice.getEinvCategory())) {
					errorVal += "Please Enter Category Type";
				}
			}
			if(key.equals("creditList")) {
				if(isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())) {
					errorVal += "Please Enter GSTIN Number,";
				}
				if(isEmpty(((GSTR1) invoice).getCdn().get(0).getNt().get(0).getNtty())) {
					errorVal += "Please Enter Document Type,";
				}
			}
			if(key.equals("cdnurList")) {
				if(isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
					errorVal += "Please Enter Document Type,";
				}
			}
			if(template.equalsIgnoreCase("custom")) {
				if((isNotEmpty(invoice.getItems().get(0).getRate())	&& invoice.getItems().get(0).getRate() < 0)) {
					errorVal += "Please Enter valid Rate";
				}
			}else {
				if((isNotEmpty(invoice.getItems().get(0).getIgstrate())	&& invoice.getItems().get(0).getIgstrate() < 0)) {
					errorVal += "Please Enter valid Igst Rate,";
				}
				if((isNotEmpty(invoice.getItems().get(0).getCgstrate())	&& invoice.getItems().get(0).getCgstrate() < 0)) {
					errorVal += "Please Enter valid Cgst Rate,";
				}
				if((isNotEmpty(invoice.getItems().get(0).getSgstrate())	&& invoice.getItems().get(0).getSgstrate() < 0)) {
					errorVal += "Please Enter valid Sgst Rate,";
				}
				if((isNotEmpty(invoice.getItems().get(0).getCessrate())	&& invoice.getItems().get(0).getCessrate() < 0)) {
					errorVal += "Please Enter valid Cess Rate,";
				}
			}
			if(isEmpty(invoice.getItems())) {
				errorVal += "Please Enter Values";
			}
		}
		return errorVal;
	}


	@Override
	public InvoiceParent importEInvoicesHsnQtyRateMandatory(InvoiceParent invoice, String returntype, String branch,String vertical, int month,	int year, String[] patterns,Map<String, String> statesMap) {
		if(isEmpty(invoice.getGstr1orEinvoice())) {
			invoice.setGstr1orEinvoice("Einvoice");
		}
		if (isNotEmpty(invoice.getStrDate())) {
			invoice.setDateofinvoice(invdate(invoice.getStrDate(),patterns,year,month));
		}
		if (isNotEmpty(invoice.getSection()) && isNotEmpty(invoice.getTcstdspercentage())) {
			invoice.setTdstcsenable(true);
		}
		if (isNotEmpty(invoice.getSection())) {
			invoiceSection(invoice,invoice.getSection());
		}
		if (isNotEmpty(invoice.getStatename())) {
			String statename = invoice.getStatename();
			if (statename.contains("-")) {
				statename = statename.substring(statename.indexOf("-")+1).trim();
			}
			if(isNotEmpty(statename) && statename.contains("&")) {
				statename = invStatename(statename);
			}
			statename = statesMap.get(statename.replaceAll("\\s", "").toLowerCase());
			invoice.setStatename(statename);
		}
		if (isNotEmpty(invoice.getIgstOnIntra())) {
			String igstontra = invoice.getIgstOnIntra();
			if ("Yes".equalsIgnoreCase(igstontra)) {
				invoice.setIgstOnIntra("Y");
			} else if ("No".equalsIgnoreCase(igstontra)) {
				invoice.setIgstOnIntra("N");
			}	
		}	
		if (isNotEmpty(invoice.getExp().get(0).getExpTyp())) {
			String expType = invoice.getExp().get(0).getExpTyp();
			if ("Export with IGST".equalsIgnoreCase(expType) || "WPAY".equalsIgnoreCase(expType)) {
				invoice.getExp().get(0).setExpTyp("WPAY");
			} else if ("Export under Bond/LUT".equalsIgnoreCase(expType)|| "WOPAY".equalsIgnoreCase(expType)) {
				invoice.getExp().get(0).setExpTyp("WOPAY");
			}
		}
		if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
			String invTyp = invoice.getB2b().get(0).getInv().get(0).getInvTyp();
			if ("Regular".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
			} else if ("Deemed Exports".equalsIgnoreCase(invTyp) || "Deemed Exp".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("DE");
			} else if ("Supplies to SEZ with payment".equalsIgnoreCase(invTyp) || "SEZ supplies with payment".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWP");
			} else if ("Supplies to SEZ without payment".equalsIgnoreCase(invTyp) || "SEZ supplies without payment".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWOP");
			} else if ("Sale from Bonded Warehouse".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("CBW");
			} else {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp(invTyp);
			}
		} else {
			invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
		}
		if (isNotEmpty(invoice.getEinvCategory()) && isNotEmpty(invoice.getGstr1orEinvoice()) && invoice.getGstr1orEinvoice().equalsIgnoreCase("Einvoice")) {
			String invTyp = invoice.getEinvCategory();
			if ("Deemed Export".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("DE");
				invoice.setEinvCategory("DEXP");
			} else if ("Business to Business".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
				invoice.setEinvCategory("B2B");
			} else if ("SEZ with payment".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWP");
				invoice.setEinvCategory("SEZWP");
			} else if ("SEZ without payment".equalsIgnoreCase(invTyp)) {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWOP");
				invoice.setEinvCategory("SEZWOP");
			} else if ("Export with Payment".equalsIgnoreCase(invTyp)) {
				invoice.getExp().get(0).setExpTyp("WPAY");
				invoice.setEinvCategory("EXPWP");
			}else if ("Export without payment".equalsIgnoreCase(invTyp)) {
				invoice.getExp().get(0).setExpTyp("WOPAY");
				invoice.setEinvCategory("EXPWOP");
			} else {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp(invTyp);
				invoice.setEinvCategory(invTyp);
			}
		} else {
			invoice.getB2b().get(0).getInv().get(0).setInvTyp("B2B");
			invoice.setEinvCategory("B2B");
		}
		if (returntype.equals(MasterGSTConstants.GSTR1)) {
			if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt(),patterns);
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
				String docType = ((GSTR1) invoice).getCdnur().get(0).getNtty();
				if ("INVOICE".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setNtty("R");
					invoice.setTyp("INV");
				} else if ("CREDIT NOTE".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setNtty("C");
					invoice.setTyp("CRN");
				} else if ("DEBIT NOTE".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setNtty("D");
					invoice.setTyp("DBN");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
				if(isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getpGst())) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setpGst("N");
				}
				String docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
				if ("INVOICE".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("R");
					invoice.setTyp("INV");
				} else if ("CREDIT NOTE".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("C");
					invoice.setTyp("CRN");
				} else if ("DEBIT NOTE".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("D");
					invoice.setTyp("DBN");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getIdt())) {
				try {
					Date invDate = DateUtils.parseDate(((GSTR1) invoice).getCdnur().get(0).getIdt(), patterns);

					((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
				} catch (java.text.ParseException e) {
					try {
						double dblDate = Double.parseDouble(((GSTR1) invoice).getCdnur().get(0).getIdt());
						Date invDate = DateUtil.getJavaDate(dblDate);
						((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
					} catch (NumberFormatException exp) {
						Calendar cal = Calendar.getInstance();
						cal.set(year, month - 1, 1);
						((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
					}
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getpGst())) {
				String pGst = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getpGst();
				if ("YES".equalsIgnoreCase(pGst) || "Y".equalsIgnoreCase(pGst)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(pGst) || "N".equalsIgnoreCase(pGst)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setpGst("N");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getpGst())) {
				String docType = ((GSTR1) invoice).getCdnur().get(0).getpGst();
				if ("YES".equalsIgnoreCase(docType) || "Y".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setpGst("Y");
				} else if ("NO".equalsIgnoreCase(docType) || "N".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnur().get(0).setpGst("N");
				}
			}
			if (isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getTyp())) {
				String type = ((GSTR1) invoice).getCdnur().get(0).getTyp();
				if ("Exports With Payment".equalsIgnoreCase(type)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("EXPWP");
				} else if ("Exports Without Payment".equalsIgnoreCase(type)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("EXPWOP");
				} else if ("B2CL".equalsIgnoreCase(type)) {
					((GSTR1) invoice).getCdnur().get(0).setTyp("B2CL");
				}
			}
			if (isNotEmpty(invoice.getTyp())) {
				String docType = invoice.getTyp();
				if ("INVOICE".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("R");
					invoice.setTyp("INV");
				} else if ("CREDIT NOTE".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("C");
					invoice.setTyp("CRN");
				} else if ("DEBIT NOTE".equalsIgnoreCase(docType)) {
					((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setNtty("D");
					invoice.setTyp("DBN");
				}
			}else {
				invoice.setTyp("INV");
			}
		} 
		if (isNotEmpty(branch)) {
			invoice.setBranch(branch);
		}else {
			invoice.setBranch("Main Branch");
		}
		if (isNotEmpty(vertical)) {
			invoice.setVertical(vertical);
		}
		if(isNotEmpty(invoice.getBilledtoname())) {
			String customername = invoice.getBilledtoname();
			if(isEmpty(invoice.getBuyerDtls())) {
				BuyerDetails buyerDetails = new BuyerDetails();
				buyerDetails.setLglNm(customername);
				invoice.setBuyerDtls(buyerDetails);
			}else {
				invoice.getBuyerDtls().setLglNm(customername);
			}
		}
		if(isNotEmpty(invoice.getBuyerPincode())) {
			if(isEmpty(invoice.getBuyerDtls())) {
				BuyerDetails buyerDetails = new BuyerDetails();
				buyerDetails.setPin(invoice.getBuyerPincode());
				invoice.setBuyerDtls(buyerDetails);
			}else {
				invoice.getBuyerDtls().setPin(invoice.getBuyerPincode());
			}
		}
		if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
			if(isEmpty(invoice.getBuyerDtls())) {
				BuyerDetails buyerDetails = new BuyerDetails();
				buyerDetails.setGstin(invoice.getB2b().get(0).getCtin());
				invoice.setBuyerDtls(buyerDetails);
			}else {
				invoice.getBuyerDtls().setGstin(invoice.getB2b().get(0).getCtin());
			}
		}
		if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
			if(isEmpty(invoice.getBuyerDtls())) {
				BuyerDetails buyerDetails = new BuyerDetails();
				buyerDetails.setAddr1(invoice.getB2b().get(0).getInv().get(0).getAddress());
				invoice.setBuyerDtls(buyerDetails);
			}else {
				invoice.getBuyerDtls().setAddr1(invoice.getB2b().get(0).getInv().get(0).getAddress());
			}
		}
		
		if(isNotEmpty(invoice.getDispatchname())) {
			if(isEmpty(invoice.getDispatcherDtls())) {
				DispatcherDetails dispatchDetails = new DispatcherDetails();
				dispatchDetails.setNm(invoice.getDispatchname());
				invoice.setDispatcherDtls(dispatchDetails);
			}else {
				invoice.getDispatcherDtls().setNm(invoice.getDispatchname());
			}
		}
		if(isNotEmpty(invoice.getDispatchAddr1())) {
			if(isEmpty(invoice.getDispatcherDtls())) {
				DispatcherDetails dispatchDetails = new DispatcherDetails();
				dispatchDetails.setAddr1(invoice.getDispatchAddr1());
				invoice.setDispatcherDtls(dispatchDetails);
			}else {
				invoice.getDispatcherDtls().setAddr1(invoice.getDispatchAddr1());
			}
		}
		if(isNotEmpty(invoice.getDispatchAddr2())) {
			if(isEmpty(invoice.getDispatcherDtls())) {
				DispatcherDetails dispatchDetails = new DispatcherDetails();
				dispatchDetails.setAddr2(invoice.getDispatchAddr2());
				invoice.setDispatcherDtls(dispatchDetails);
			}else {
				invoice.getDispatcherDtls().setAddr2(invoice.getDispatchAddr2());
			}
		}
		if(isNotEmpty(invoice.getDispatchLoc())) {
			if(isEmpty(invoice.getDispatcherDtls())) {
				DispatcherDetails dispatchDetails = new DispatcherDetails();
				dispatchDetails.setLoc(invoice.getDispatchLoc());
				invoice.setDispatcherDtls(dispatchDetails);
			}else {
				invoice.getDispatcherDtls().setLoc(invoice.getDispatchLoc());
			}
		}
		if(isNotEmpty(invoice.getDispatchpin())) {
			if(isEmpty(invoice.getDispatcherDtls())) {
				DispatcherDetails dispatchDetails = new DispatcherDetails();
				dispatchDetails.setPin(invoice.getDispatchpin());
				invoice.setDispatcherDtls(dispatchDetails);
			}else {
				invoice.getDispatcherDtls().setPin(invoice.getDispatchpin());
			}
		}
		if(isNotEmpty(invoice.getDispatchState())) {
			if(isEmpty(invoice.getDispatcherDtls())) {
				DispatcherDetails dispatchDetails = new DispatcherDetails();
				dispatchDetails.setStcd(invoice.getDispatchState());
				invoice.setDispatcherDtls(dispatchDetails);
			}else {
				invoice.getDispatcherDtls().setStcd(invoice.getDispatchState());
			}
		}
		
		if(isNotEmpty(invoice.getShipmentGstin())) {
			if(isEmpty(invoice.getShipmentDtls())) {
				ShipmentDetails shipmentDetails = new ShipmentDetails();
				shipmentDetails.setGstin(invoice.getShipmentGstin());
				invoice.setShipmentDtls(shipmentDetails);
			}else {
				invoice.getShipmentDtls().setGstin(invoice.getShipmentGstin());
			}
		}
		if(isNotEmpty(invoice.getShipmentLgnm())) {
			if(isEmpty(invoice.getShipmentDtls())) {
				ShipmentDetails shipmentDetails = new ShipmentDetails();
				shipmentDetails.setLglNm(invoice.getShipmentLgnm());
				invoice.setShipmentDtls(shipmentDetails);
			}else {
				invoice.getShipmentDtls().setLglNm(invoice.getShipmentLgnm());
			}
		}
		if(isNotEmpty(invoice.getShipmentTrdnm())) {
			if(isEmpty(invoice.getShipmentDtls())) {
				ShipmentDetails shipmentDetails = new ShipmentDetails();
				shipmentDetails.setTrdNm(invoice.getShipmentTrdnm());
				invoice.setShipmentDtls(shipmentDetails);
			}else {
				invoice.getShipmentDtls().setTrdNm(invoice.getShipmentTrdnm());
			}
		}
		if(isNotEmpty(invoice.getShipmentAddr1())) {
			if(isEmpty(invoice.getShipmentDtls())) {
				ShipmentDetails shipmentDetails = new ShipmentDetails();
				shipmentDetails.setAddr1(invoice.getShipmentAddr1());
				invoice.setShipmentDtls(shipmentDetails);
			}else {
				invoice.getShipmentDtls().setAddr1(invoice.getShipmentAddr1());
			}
		}
		if(isNotEmpty(invoice.getShipmentAddr2())) {
			if(isEmpty(invoice.getShipmentDtls())) {
				ShipmentDetails shipmentDetails = new ShipmentDetails();
				shipmentDetails.setAddr2(invoice.getShipmentAddr2());
				invoice.setShipmentDtls(shipmentDetails);
			}else {
				invoice.getShipmentDtls().setAddr2(invoice.getShipmentAddr2());
			}
		}
		if(isNotEmpty(invoice.getShipmentLoc())) {
			if(isEmpty(invoice.getShipmentDtls())) {
				ShipmentDetails shipmentDetails = new ShipmentDetails();
				shipmentDetails.setLoc(invoice.getShipmentLoc());
				invoice.setShipmentDtls(shipmentDetails);
			}else {
				invoice.getShipmentDtls().setLoc(invoice.getShipmentLoc());
			}
		}
		if(isNotEmpty(invoice.getShipmentpin())) {
			if(isEmpty(invoice.getShipmentDtls())) {
				ShipmentDetails shipmentDetails = new ShipmentDetails();
				shipmentDetails.setPin(invoice.getShipmentpin());
				invoice.setShipmentDtls(shipmentDetails);
			}else {
				invoice.getShipmentDtls().setPin(invoice.getShipmentpin());
			}
		}
		if(isNotEmpty(invoice.getShipmentState())) {
			if(isEmpty(invoice.getShipmentDtls())) {
				ShipmentDetails shipmentDetails = new ShipmentDetails();
				shipmentDetails.setStcd(invoice.getShipmentState());
				invoice.setShipmentDtls(shipmentDetails);
			}else {
				invoice.getShipmentDtls().setStcd(invoice.getShipmentState());
			}
		}
		return invoice;
	}

	@Override
	public String invStatename(String statename) {
		String stname = "";
		if(isNotEmpty(statename) && statename.contains("&")) {
			stname = statename;
			stname = stname.replace("&", "and");
		}
		return stname;
	}

	@Override
	public InvoiceParent importEwaybillInvoices(InvoiceParent invoice, String returntype, String branch,
			String vertical, int month, int year, String[] patterns, Client client,Map<String, String> statesMap) {
		if (isNotEmpty(invoice.getStrDate())) {
			invoice.setDateofinvoice(invdate(invoice.getStrDate(),patterns,year,month));
		}
		if(isNotEmpty(client)) {
			if(isNotEmpty(client.getGstnnumber())) {
				invoice.setFromGstin(client.getGstnnumber());
			}
			if(isNotEmpty(client.getAddress())) {
				String address =  client.getAddress();
				if(address.length() > 120) {
					address = address.substring(0, 119);
				}
				invoice.setFromAddr1(address);
				invoice.setFromAddr2("");
			}else {
				invoice.setFromAddr1("");
				invoice.setFromAddr2("");
			}
			if(isNotEmpty(client.getPincode())) {
				invoice.setFromPincode(client.getPincode());
			}
		}
		if(isNotEmpty(invoice.getDocType())) {
			String doctype = invoice.getDocType();
			if("Tax Invoice".equalsIgnoreCase(doctype)) {
				invoice.setDocType("INV");
			}else if("Delivery Challan".equalsIgnoreCase(doctype)) {
				invoice.setDocType("CHL");
			}else if("Bill of Supply".equalsIgnoreCase(doctype)) {
				invoice.setDocType("BIL");
			}else if("Bill of Entry".equalsIgnoreCase(doctype)) {
				invoice.setDocType("BOE");
			}else if("Credit Note".equalsIgnoreCase(doctype)) {
				invoice.setDocType("CNT");
			}else if("Others".equalsIgnoreCase(doctype)) {
				invoice.setDocType("OTH");
			}
		}
		if(isNotEmpty(invoice.getSupplyType())) {
			if(("Inward").equalsIgnoreCase(invoice.getSupplyType())) {
				invoice.setSupplyType("I");
			}else if(("Outward").equalsIgnoreCase(invoice.getSupplyType())) {
				invoice.setSupplyType("O");
			}
		}
		if(isNotEmpty(invoice.getSubSupplyType())) {
			if(("Supply").equalsIgnoreCase(invoice.getSubSupplyType())) {
				invoice.setSubSupplyType("1");
			}else if(("Import").equalsIgnoreCase(invoice.getSubSupplyType())) {
				invoice.setSubSupplyType("2");
			}else if(("Export").equalsIgnoreCase(invoice.getSubSupplyType())) {
				invoice.setSubSupplyType("3");
			}else if(("Job Work").equalsIgnoreCase(invoice.getSubSupplyType())) {
				invoice.setSubSupplyType("4");
			}else if(("For Own Use").equalsIgnoreCase(invoice.getSubSupplyType())) {
				invoice.setSubSupplyType("5");
			}else if(("Job work Returns").equalsIgnoreCase(invoice.getSubSupplyType())) {
				invoice.setSubSupplyType("6");
			}else if(("Sales Return").equalsIgnoreCase(invoice.getSubSupplyType())) {
				invoice.setSubSupplyType("7");
			}else if(("Others").equalsIgnoreCase(invoice.getSubSupplyType())) {
				invoice.setSubSupplyType("8");
			}else if(("SKD/CKD").equalsIgnoreCase(invoice.getSubSupplyType())) {
				invoice.setSubSupplyType("9");
			}else if(("Line Sales").equalsIgnoreCase(invoice.getSubSupplyType())) {
				invoice.setSubSupplyType("10");
			}else if(("Recipient  Not Known").equalsIgnoreCase(invoice.getSubSupplyType())) {
				invoice.setSubSupplyType("11");
			}else if(("Exhibition or Fairs").equalsIgnoreCase(invoice.getSubSupplyType())) {
				invoice.setSubSupplyType("12");
			}
		
		}
		if(isNotEmpty(invoice.getVehiclListDetails().get(0))) {
			EBillVehicleListDetails vehicle = invoice.getVehiclListDetails().get(0); 
			if (isNotEmpty(invoice.getStrTransDate())) {
				Date tDate = invdate(invoice.getStrTransDate(),patterns,year,month);
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
				String strDate = dateFormat.format(tDate);  
				vehicle.setTransDocDate(strDate);
			}
		}	
		if(isNotEmpty(invoice.getFromPin())) {
			invoice.setFromPincode(Integer.parseInt(invoice.getFromPin()));
		}
		if(isNotEmpty(invoice.getToPin())) {
			invoice.setToPincode(Integer.parseInt(invoice.getToPin()));
		}
		if(isNotEmpty(invoice.getTransType())) {
			if("1. Regular".equalsIgnoreCase(invoice.getTransType())) {
				invoice.setTransactionType(1);
			}else if("2. Bill To-Ship To".equalsIgnoreCase(invoice.getTransType())) {
				invoice.setTransactionType(2);
			}else if("3. Bill From-Dispatch From".equalsIgnoreCase(invoice.getTransType())) {
				invoice.setTransactionType(3);
			}else if("Combinations of 2 & 3".equalsIgnoreCase(invoice.getTransType())) {
				invoice.setTransactionType(4);
			}
		}
		if(isNotEmpty(invoice.getVehiclListDetails().get(0).getTransMode())) {
			String transMode = invoice.getVehiclListDetails().get(0).getTransMode();
			if("Road".equalsIgnoreCase(transMode)) {
				invoice.getVehiclListDetails().get(0).setTransMode("1");
			}else if("Rail".equalsIgnoreCase(transMode)) {
				invoice.getVehiclListDetails().get(0).setTransMode("2");
			}else if("Air".equalsIgnoreCase(transMode)) {
				invoice.getVehiclListDetails().get(0).setTransMode("3");
			}else if("Ship".equalsIgnoreCase(transMode)) {
				invoice.getVehiclListDetails().get(0).setTransMode("4");
			}
		}
		if(isNotEmpty(invoice.getVehicleType())) {
			String vehType = invoice.getVehicleType();
			if("Regular".equalsIgnoreCase(vehType)) {
				invoice.setVehicleType("R");
			}else if("Over Dimensional Cargo".equalsIgnoreCase(vehType)) {
				invoice.setVehicleType("O");
			}
		}
		if (isNotEmpty(invoice.getStatename())) {
			String statename = invoice.getStatename();
			if (statename.contains("-")) {
				statename = statename.substring(statename.indexOf("-")+1).trim();
			}
			if(isNotEmpty(statename) && statename.contains("&")) {
				statename = invStatename(statename);
			}
			statename = statesMap.get(statename.replaceAll("\\s", "").toLowerCase());
			invoice.setStatename(statename);
		}
		if (isNotEmpty(branch)) {
			invoice.setBranch(branch);
		}else {
			invoice.setBranch("Main Branch");
		}
		if (isNotEmpty(vertical)) {
			invoice.setVertical(vertical);
		}
		
		return invoice;

	}

	@Override
	@Transactional
	public void updateExcelData(Map<String, List<InvoiceParent>> beans, List<String> sheetList, String returntype,
			String id, String fullname, String clientId, String templateType, OtherConfigurations otherconfig)
			throws IllegalArgumentException, IOException {
		Client client = clientService.findById(clientId);
		Map<String, String> hsnMap = configService.getHSNMap();
		Map<String, String> sacMap = configService.getSACMap();
		Map<String,StateConfig> stateconfig = getStatesMap();
		Map<String,StateConfig> statetin = getStatesTinMap();
		List<? extends InvoiceParent> invoiceList = beans.get("invoiceList");
		List<? extends InvoiceParent> creditList = beans.get("creditList");
		List<? extends InvoiceParent> exportList = beans.get("exportList");
		List<? extends InvoiceParent> advReceiptList = beans.get("advReceiptList");
		List<? extends InvoiceParent> b2bList = beans.get("b2bList");
		List<? extends InvoiceParent> b2cList = beans.get("b2cList");
		List<? extends InvoiceParent> b2clList = beans.get("b2clList");
		List<? extends InvoiceParent> advAdjustedList = beans.get("advAdjustedList");
		List<? extends InvoiceParent> cdnurList = beans.get("cdnurList");
		List<? extends InvoiceParent> nilList = beans.get("nilList");
		List<? extends InvoiceParent> b2buList = beans.get("b2buList");
		List<? extends InvoiceParent> impgList = beans.get("impgList");
		List<? extends InvoiceParent> impsList = beans.get("impsList");
		List<? extends InvoiceParent> itrvslList = beans.get("itrvslList");
		
		List<? extends InvoiceParent> b2baList = beans.get("b2baList");
		List<? extends InvoiceParent> b2claList = beans.get("b2claList");
		List<? extends InvoiceParent> b2csaList = beans.get("b2csaList");
		List<? extends InvoiceParent> cdnraList = beans.get("cdnraList");
		List<? extends InvoiceParent> cdnuraList = beans.get("cdnuraList");
		List<? extends InvoiceParent> expaList = beans.get("expaList");
		List<? extends InvoiceParent> ataList = beans.get("ataList");
		List<? extends InvoiceParent> txpaList = beans.get("txpaList");
		
		List<InvoicePendingActions> invoicePendingActions = new ArrayList<InvoicePendingActions>();
		List<? extends InvoiceParent> invoices = null;
		if (returntype.equals(GSTR1)) {
			invoices = new ArrayList<GSTR1>();
		} else if (returntype.equals(GSTR4)) {
			invoices = new ArrayList<GSTR4>();
		} else if (returntype.equals(GSTR5)) {
			invoices = new ArrayList<GSTR5>();
		} else if (returntype.equals(GSTR6)) {
			invoices = new ArrayList<GSTR6>();
		}else if (returntype.equals(MasterGSTConstants.EWAYBILL)) {
			invoices = new ArrayList<EWAYBILL>();
		} else {
			invoices = new ArrayList<PurchaseRegister>();
		}
		if (isNotEmpty(sheetList)) {
			for (String sheetName : sheetList) {
				String type = null;
				if (sheetName.equals("creditList")) {
					type = CREDIT_DEBIT_NOTES;
				} else if (sheetName.equals("exportList")) {
					type = EXPORTS;
				} else if (sheetName.equals("advReceiptList")) {
					type = ADVANCES;
				} else if (sheetName.equals("advAdjustedList")) {
					type = ATPAID;
				} else if (sheetName.equals("cdnurList")) {
					type = CDNUR;
				} else if (sheetName.equals("nilList")) {
					type = NIL;
				} else if (sheetName.equals("b2bList")) {
					type = B2B;
				} else if (sheetName.equals("b2cList")) {
					type = B2C;
				} else if (sheetName.equals("b2clList")) {
					type = B2CL;
				} else if (sheetName.equals("b2buList")) {
					type = B2BUR;
				} else if (sheetName.equals("impgList")) {
					type = IMP_GOODS;
				} else if (sheetName.equals("impsList")) {
					type = IMP_SERVICES;
				} else if (sheetName.equals("itrvslList")) {
					type = ITC_REVERSAL;
				} else if (sheetName.equals("b2baList")) {
					type = B2BA;
				}  else if (sheetName.equals("b2csaList")) {
					type = B2CSA;
				} else if (sheetName.equals("b2claList")) {
					type = B2CLA;
				} else if (sheetName.equals("cdnraList")) {
					type = CDNA;
				} else if (sheetName.equals("cdnuraList")) {
					type = CDNURA;
				} else if (sheetName.equals("expaList")) {
					type = EXPA;
				} else if (sheetName.equals("ataList")) {
					type = ATA;
				} else if (sheetName.equals("txpaList")) {
					type = TXPA;
				} else if(sheetName.equals("invoiceList")) {
					type = B2B;
				}
				if(isNotEmpty(beans.get(sheetName))) {
					invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, beans.get(sheetName), returntype,
						client, type, id, fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
				}
			}
		} else {
			if(isNotEmpty(invoiceList)) {
				invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, invoiceList, returntype, client, null, id,
					fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(creditList)) {
			invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, creditList, returntype, client,
					CREDIT_DEBIT_NOTES, id, fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(exportList)) {
			invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, exportList, returntype, client, EXPORTS,
					id, fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(advReceiptList)) {
			invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, advReceiptList, returntype, client,
					ADVANCES, id, fullname, clientId, templateType,statetin,stateconfig,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(advAdjustedList)) {
			invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, advAdjustedList, returntype, client,
					ATPAID, id, fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(cdnurList)) {
			invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, cdnurList, returntype, client, CDNUR, id,
					fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(nilList)) {
			invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, nilList, returntype, client, NIL, id,
					fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(b2bList)) {
			invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, b2bList, returntype, client, B2B, id,
					fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(b2cList)) {
			invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, b2cList, returntype, client, B2C, id,
					fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(b2clList)) {
			invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, b2clList, returntype, client, B2CL, id,
					fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(b2buList)) {
			invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, b2buList, returntype, client, B2BUR, id,
					fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(impgList)) {
			invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, impgList, returntype, client, IMP_GOODS,
					id, fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(impsList)) {
			invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, impsList, returntype, client,
					IMP_SERVICES, id, fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(itrvslList)) {
			invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, itrvslList, returntype, client,
					ITC_REVERSAL, id, fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(cdnraList)) {
				invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, creditList, returntype, client, CDNA,
						id, fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(expaList)) {
				invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, exportList, returntype, client, EXPA,
						id, fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(ataList)) {
				invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, advReceiptList, returntype, client, ATA,
						id, fullname, clientId, templateType,statetin,stateconfig,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(txpaList)) {
				invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, advAdjustedList, returntype, client, TXPA,
						id, fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(cdnuraList)) {
				invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, cdnurList, returntype, client, CDNURA, 
						id,	fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
				
			if(isNotEmpty(b2baList)) {
				invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, b2bList, returntype, client, B2BA, 
						id, fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(b2csaList)) {
				invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, b2cList, returntype, client, B2CSA, 
						id, fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
			if(isNotEmpty(b2claList)) {
				invoices = updateSalesInvoiceDataNew((List<InvoiceParent>) invoices, b2clList, returntype, client, B2CLA, 
						id, fullname, clientId, templateType,stateconfig,statetin,otherconfig,invoicePendingActions,hsnMap,sacMap);
			}
				
		}
		if (isNotEmpty(invoices)) {
			if (returntype.equals(GSTR1)) {
				List<GSTR1> salesInvoices = gstr1Repository.save((List<GSTR1>) invoices);
				if(isNotEmpty(otherconfig) && (isEmpty(otherconfig.isEnablejournals()) || (isNotEmpty(otherconfig.isEnablejournals()) && !otherconfig.isEnablejournals()))) {	
					for(GSTR1 salesinvoice : salesInvoices) {
						if(isNotEmpty(salesinvoice)) {
							if (salesinvoice.getInvtype().equals(MasterGSTConstants.ADVANCES) || salesinvoice.getInvtype().equals(MasterGSTConstants.ATA)) {
								saveAdvancePayments(salesinvoice, returntype);
							}
							boolean isIntraState = true;
							if (isNotEmpty(salesinvoice.getStatename())) {
								if (!salesinvoice.getStatename().equals(client.getStatename())) {
									isIntraState = false;
								}
							}
							if (salesinvoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || salesinvoice.getInvtype().equals(MasterGSTConstants.EXPA)) {
								List<GSTRExports> exp = salesinvoice.getExp();
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
							}else if (salesinvoice.getInvtype().equals(MasterGSTConstants.B2B) || salesinvoice.getInvtype().equals(MasterGSTConstants.B2BA) || salesinvoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || salesinvoice.getInvtype().equals(MasterGSTConstants.CDNUR) || salesinvoice.getInvtype().equals(MasterGSTConstants.CDNA) || salesinvoice.getInvtype().equals(MasterGSTConstants.CDNURA)) {
								if(isNotEmpty(salesinvoice.getB2b()) && isNotEmpty(salesinvoice.getB2b().get(0)) && isNotEmpty(salesinvoice.getB2b().get(0).getInv()) && isNotEmpty(salesinvoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(salesinvoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
									String invtyp = salesinvoice.getB2b().get(0).getInv().get(0).getInvTyp();
									if (invtyp.equals("SEWP") || invtyp.equals("SEWOP")  || invtyp.equals("CBW")) {
										isIntraState = false;
									}
								}
								if(salesinvoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || salesinvoice.getInvtype().equals(MasterGSTConstants.CDNUR) || salesinvoice.getInvtype().equals(MasterGSTConstants.CDNA) || salesinvoice.getInvtype().equals(MasterGSTConstants.CDNURA)) {
									if (isEmpty(salesinvoice.getB2b()) || isEmpty(salesinvoice.getB2b().get(0).getCtin())) {
										isIntraState = false;
									}
								}
							}
							//clientService.saveJournalInvoice(salesinvoice, salesinvoice.getClientid(), returntype, isIntraState);
							accountingJournalsUtils.createJournalsEntries(salesinvoice.getUserid(),salesinvoice.getClientid(),returntype,salesinvoice,isIntraState);
						}
					}
				}
			} else if (returntype.equals(GSTR4)) {
				gstr4Repository.save((List<GSTR4>) invoices);
			} else if (returntype.equals(GSTR5)) {
				gstr5Repository.save((List<GSTR5>) invoices);
			} else if (returntype.equals(GSTR6)) {
				gstr6Repository.save((List<GSTR6>) invoices);
			}else if (returntype.equals(MasterGSTConstants.EWAYBILL)) {
				ewayBillRepository.save((List<EWAYBILL>) invoices);
			} else {
				List<PurchaseRegister> purchaseRegisters = purchaseRepository.save((List<PurchaseRegister>) invoices);
				if(isNotEmpty(otherconfig) && (isEmpty(otherconfig.isEnablejournals()) || (isNotEmpty(otherconfig.isEnablejournals()) && !otherconfig.isEnablejournals()))) {
					  for(PurchaseRegister purchaseRegister : purchaseRegisters) {
						  String statename = ""; 
						  if(isNotEmpty(purchaseRegister.getB2b().get(0).getCtin())) { 
							  String gstinNumber = purchaseRegister.getB2b().get(0).getCtin().trim(); 
							  if(isNotEmpty(gstinNumber)) { 
								  gstinNumber = gstinNumber.substring(0,2);
								  StateConfig state = statetin.get(gstinNumber);
								  if(isNotEmpty(state) & isNotEmpty(state.getName())) {
									  statename = state.getName();
								  }
							  } 
						  } 
						  boolean isIntraState = true;
							if (isNotEmpty(statename)) {
								if (!statename.equals(client.getStatename())) {
									isIntraState = false;
								}
							}
							if(purchaseRegister.getInvtype().equals(MasterGSTConstants.B2BUR) || purchaseRegister.getInvtype().equals(MasterGSTConstants.B2B) || purchaseRegister.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || purchaseRegister.getInvtype().equals(MasterGSTConstants.CDNUR)){
								String revcharge = purchaseRegister.getRevchargetype();
								String subtype = purchaseRegister.getPrinterintra();
								if(isNotEmpty(revcharge) && ("Reverse".equalsIgnoreCase(revcharge) || "Y".equalsIgnoreCase(revcharge))) {
									if(isNotEmpty(purchaseRegister) && isNotEmpty(purchaseRegister.getB2b()) && isNotEmpty(purchaseRegister.getB2b().get(0)) && isNotEmpty(purchaseRegister.getB2b().get(0).getInv()) && isNotEmpty(purchaseRegister.getB2b().get(0).getInv().get(0)) && isNotEmpty(purchaseRegister.getB2b().get(0).getInv().get(0).getInvTyp()) && purchaseRegister.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWP")) {
										isIntraState = false;
									}else {
										if(purchaseRegister.getInvtype().equals(MasterGSTConstants.B2B)) {
											if (isEmpty(purchaseRegister.getB2b().get(0).getCtin())) {
												if(isNotEmpty(subtype) && "Inter".equalsIgnoreCase(subtype)) {
													isIntraState = false;
												}else {
													isIntraState = true;
												}
											}
										}else {
											if(isNotEmpty(subtype) && "Inter".equalsIgnoreCase(subtype)) {
												isIntraState = false;
											}else {
												isIntraState = true;
											}
										}
									}
								}else {
									if(isNotEmpty(purchaseRegister) && isNotEmpty(purchaseRegister.getB2b()) && isNotEmpty(purchaseRegister.getB2b().get(0)) && isNotEmpty(purchaseRegister.getB2b().get(0).getInv()) && isNotEmpty(purchaseRegister.getB2b().get(0).getInv().get(0)) && isNotEmpty(purchaseRegister.getB2b().get(0).getInv().get(0).getInvTyp()) && purchaseRegister.getB2b().get(0).getInv().get(0).getInvTyp().equalsIgnoreCase("SEWP")) {
										isIntraState = false;
									}
								}
							}
						accountingJournalsUtils.createJournalsEntries(purchaseRegister.getUserid(),purchaseRegister.getClientid(),returntype,purchaseRegister,isIntraState);
					  }
				}
			}
		}
	}

	@Override
	public Map<String, StateConfig> getStatesMap(){
		Map<String, StateConfig> statesMap = new HashMap<String, StateConfig>();
		List<StateConfig> states = configService.getStates();
		for (StateConfig state : states) {
			String name = state.getName();
			String[] nm = name.split("-");
			statesMap.put(nm[1].replaceAll("\\s", "").toLowerCase(), state);
		}
		return statesMap;
	}

	@Override
	public Map<String, String> getStateMap(){
		Map<String, String> statesMap = new HashMap<String, String>();
		List<StateConfig> states = configService.getStates();
		for (StateConfig state : states) {
			String name = state.getName();
			String[] nm = name.split("-");
			statesMap.put(nm[0].replaceAll("\\s", "").toLowerCase(), name);
			statesMap.put(nm[1].replaceAll("\\s", "").toLowerCase(), name);
		}
		return statesMap;
	}
	
	
	public Item changeInvoiceAmounts(Item item) {
		if(NullUtil.isNotEmpty(item.getTaxablevalue())) {
			item.setTaxablevalue(Double.parseDouble(df2.format(item.getTaxablevalue())));
			if(isEmpty(item.getRateperitem())) {
				item.setRateperitem(Double.parseDouble(df2.format(item.getTaxablevalue())));
			}
		}
		if(isEmpty(item.getQuantity())) {
			item.setQuantity(1d);
		}
		if(NullUtil.isNotEmpty(item.getIgstamount())) {
			item.setIgstamount(Double.parseDouble(df2.format(item.getIgstamount())));
		}
		if(NullUtil.isNotEmpty(item.getCgstamount())) {
			item.setCgstamount(Double.parseDouble(df2.format(item.getCgstamount())));
		}
		if(NullUtil.isNotEmpty(item.getSgstamount())) {
			item.setSgstamount(Double.parseDouble(df2.format(item.getSgstamount())));
		}
		if(NullUtil.isNotEmpty(item.getDiscount())) {
			item.setDiscount(Double.parseDouble(df2.format(item.getDiscount())));
		}
		if(NullUtil.isNotEmpty(item.getRateperitem())) {
			item.setRateperitem(Double.parseDouble(df2.format(item.getRateperitem())));
		}
		if(NullUtil.isNotEmpty(item.getCessamount())) {
			item.setCessamount(Double.parseDouble(df2.format(item.getCessamount())));
		}
		if(NullUtil.isNotEmpty(item.getIgstavltax())) {
			item.setIgstavltax(Double.parseDouble(df2.format(item.getIgstavltax())));
		}
		if(NullUtil.isNotEmpty(item.getSgstavltax())) {
			item.setSgstavltax(Double.parseDouble(df2.format(item.getSgstavltax())));
		}
		if(NullUtil.isNotEmpty(item.getCgstavltax())) {
			item.setCgstavltax(Double.parseDouble(df2.format(item.getCgstavltax())));
		}if(NullUtil.isNotEmpty(item.getCessavltax())) {
			item.setCessavltax(Double.parseDouble(df2.format(item.getCessavltax())));
		}
		Double total = 0d;
		if(NullUtil.isEmpty(item.getTotal()) || item.getTotal() <= 0) {
			if(NullUtil.isNotEmpty(item.getTaxablevalue())) {
				total += item.getTaxablevalue();
			}
			if(NullUtil.isNotEmpty(item.getIgstamount())) {
				total += item.getIgstamount();
			}
			if(NullUtil.isNotEmpty(item.getCgstamount())) {
				total += item.getCgstamount();
			}
			if(NullUtil.isNotEmpty(item.getSgstamount())) {
				total += item.getSgstamount();
			}
			if(NullUtil.isNotEmpty(item.getCessamount())) {
				total += item.getCessamount();
			}
			item.setTotal(total);
		}
		if(NullUtil.isNotEmpty(item.getTotal())) {
			item.setTotal(Double.parseDouble(df2.format(item.getTotal())));
		}
		return item;
	}

	public void nilSupplyTally(InvoiceParent invoice) {
		List<Item> items=Lists.newArrayList();
		String type = "Nil Rated";
		Double nilTotAmt = 0.0;
		if (isNotEmpty(invoice.getNil().getInv().get(0).getExptAmt())) {
			type = "Exempted";
			Double exptAmt= invoice.getNil().getInv().get(0).getExptAmt();
			nilTotAmt+= exptAmt;
			Item item = new Item();
			item.setType(type);
			item.setTotal(exptAmt);
			item.setTaxablevalue(exptAmt);
			item.setRateperitem(exptAmt);
			item.setQuantity(1d);
			items.add(item);			
		}
		if (isNotEmpty(invoice.getNil().getInv().get(0).getNilAmt())) {
			type = "Nil Rated";
			Double nilAmt= invoice.getNil().getInv().get(0).getNilAmt();
			nilTotAmt+= nilAmt;
			Item item = new Item();
			item.setType(type);
			item.setTotal(nilAmt);
			item.setTaxablevalue(nilAmt);
			item.setRateperitem(nilAmt);
			item.setQuantity(1d);
			items.add(item);
		}
		if (isNotEmpty(invoice.getNil().getInv().get(0).getNgsupAmt())) {
			type = "Non-GST";
			Double nongstAmt= invoice.getNil().getInv().get(0).getNgsupAmt();
			nilTotAmt+= nongstAmt;
			Item item = new Item();
			item.setType(type);
			item.setTotal(nongstAmt);
			item.setTaxablevalue(nongstAmt);
			item.setRateperitem(nongstAmt);
			item.setQuantity(1d);
			items.add(item);
		}
		if(isNotEmpty(items)) {
			invoice.setItems(items);
			invoice.setTotalamount(nilTotAmt);
			invoice.setTotaltaxableamount(nilTotAmt);
		}
	}
	public void invoiceSection(InvoiceParent invoice,String section) {
		if ("192A".equals(section)) {
			invoice.setSection("192A(10)");
		} else if ("193".equals(section)) {
			invoice.setSection("193(10)");
		} else if ("194A".equals(section)) {
			invoice.setSection("194A(10)");
		} else if ("194B".equals(section)) {
			invoice.setSection("194B(30)");
		} else if ("194BB".equals(section)) {
			invoice.setSection("194BB(30)");
		} else if ("194C".equals(section)) {
			invoice.setSection("194C(1)");
		} else if ("194D".equals(section)) {
			invoice.setSection("194D(5)");
		} else if ("194DA".equals(section)) {
			invoice.setSection("194DA(2)");
		} else if ("194E".equals(section)) {
			invoice.setSection("194E(20)");
		} else if ("194EE".equals(section)) {
			invoice.setSection("194EE(10)");
		} else if ("194G".equals(section)) {
			invoice.setSection("194G(5)");
		} else if ("194H".equals(section)) {
			invoice.setSection("194H(5)");
		} else if ("194-I".equals(section)) {
			invoice.setSection("194-I(2)");
		} else if ("194-IA".equals(section)) {
			invoice.setSection("194-IA(1)");
		} else if ("194-IB".equals(section)) {
			invoice.setSection("194-IB(5)");
		} else if ("194-IC".equals(section)) {
			invoice.setSection("194-IC(10)");
		} else if ("194J".equals(section)) {
			invoice.setSection("194J(10)");
		} else if ("194LA".equals(section)) {
			invoice.setSection("194LA(10)");
		} else if ("194LBB".equals(section)) {
			invoice.setSection("194LBB(10)");
		} else if ("194LBC".equals(section)) {
			invoice.setSection("194LBC(25)");
		} else if ("194M".equals(section)) {
			invoice.setSection("194M(5)");
		} else if ("194N".equals(section)) {
			invoice.setSection("194N(2)");
		} else if ("206C".equals(section)) {
			invoice.setSection("206C(1)");
		}
	}
	
	private List<? extends InvoiceParent> updateSalesInvoiceDataNew(List<InvoiceParent> existingInvoices,
			List<? extends InvoiceParent> invoiceList, final String returntype, Client client, String invType,
			final String id, final String fullname, final String clientId, final String templateType,Map<String,StateConfig> stateconfig,Map<String,StateConfig> statetin,OtherConfigurations otherconfig,List<InvoicePendingActions> invoicePendingActions,Map<String, String> hsnMap,Map<String, String> sacMap) {
		List<String> invtypes = Lists.newArrayList();
		if(isEmpty(invType) || invType.equalsIgnoreCase("B2B")) {
			invtypes.add("B2B");
			invtypes.add("B2C");
			invtypes.add("B2CL");
		} else if(isEmpty(invType) || invType.equalsIgnoreCase("B2BA")) {
			invtypes.add("B2BA");
			invtypes.add("B2CSA");
			invtypes.add("B2CLA");
		} else {
			invtypes.add(invType);
		}
		if (isEmpty(invoiceList)) {
			return existingInvoices;
		}
		Map<String,InvoiceParent> invoicesmap = new HashMap<String, InvoiceParent>();
		for (InvoiceParent invoice : invoiceList) {
			boolean present = false;
			boolean isIntraState = true;
			if (isNotEmpty(invoice.getStatename())) {
				String strState = invoice.getStatename();
				if (isNotEmpty(strState)) {
					if(strState.contains("-")) {
						String strClientState = client.getStatename();
						if (strClientState.contains("-")) {
							strClientState = strClientState.substring(0, strClientState.indexOf("-")).trim();
						}
						if ("Purchase Register".equals(returntype) || "GSTR2".equals(returntype) || "PurchaseRegister".equals(returntype)) {
							if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								strState = (invoice.getB2b().get(0).getCtin().trim()).substring(0, 2);
	
							}
						}else if (strState.contains("-")) {
								strState = strState.substring(0, strState.indexOf("-")).trim();
						}
						if (!strClientState.equals(strState)) {
							isIntraState = false;
						}
						if(("GSTR1".equals(returntype) || "SalesRegister".equals(returntype)) && isNotEmpty(invType) && invType.equals(MasterGSTConstants.CDNUR)) {
							isIntraState = false;
						}
					}else {
						String strClientState = client.getStatename();
						if (strClientState.contains("-")) {
							strClientState = strClientState.substring(strClientState.indexOf("-")+1).trim();
						}
						if ("Purchase Register".equals(returntype) || "GSTR2".equals(returntype) || "PurchaseRegister".equals(returntype)) {
							if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								strState = (invoice.getB2b().get(0).getCtin().trim()).substring(0, 2);
	
							}
						}else if (strState.contains("-")) {
								strState = strState.substring(strState.indexOf("-")+1).trim();
						}
						if (!strClientState.equals(strState)) {
							isIntraState = false;
						}
						if(("GSTR1".equals(returntype) || "SalesRegister".equals(returntype)) && isNotEmpty(invType) && invType.equals(MasterGSTConstants.CDNUR)) {
							isIntraState = false;
						}
					}
				}
			}
			if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
				if (isEmpty(invType) || invType.equals(B2B)) {
					String pInvType = B2B;
					Double totalAmount = 0d;
					if (isNotEmpty(invoice.getItems())) {
						for (Item item : invoice.getItems()) {
							if (isNotEmpty(item.getTotal())) {
								totalAmount += item.getTotal();
							}
						}
					}
					if (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())
							|| invoice.getB2b().get(0).getCtin().trim().isEmpty()) {
						
						if (totalAmount > 250000 && !isIntraState) {
							pInvType = B2CL;
						} else {
							pInvType = B2C;
						}
					} else {
						pInvType = B2B;
					}
					
					invoice.setInvtype(pInvType);
				}else if (isEmpty(invType) || invType.equals(B2BA)) {
					String pInvType = B2BA;
					Double totalAmount = 0d;
					if (isNotEmpty(invoice.getItems())) {
						for (Item item : invoice.getItems()) {
							if (isNotEmpty(item.getTotal())) {
								totalAmount += item.getTotal();
							}
						}
					}
					if (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())
							|| invoice.getB2b().get(0).getCtin().trim().isEmpty()) {
						
						if (totalAmount > 250000 && !isIntraState) {
							pInvType = B2CLA;
						} else {
							pInvType = B2CSA;
						}
					} else {
						pInvType = B2BA;
					}
					
					invoice.setInvtype(pInvType);
				}else {
					invoice.setInvtype(invType);
				}
			}else {
				invoice.setInvtype(invType);
			}
			if("GSTR1".equals(returntype) || "SalesRegister".equals(returntype)) {
				if(isNotEmpty(invoice.getInvtype())) {
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
						if(invoice.getInvtype().equals(MasterGSTConstants.EXPA)) {
							invoice.setStatename(MasterGSTConstants.OTHER_TERRITORY);
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
				}
			}
			if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())
					&& isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
				if(serviceUtils.isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getInum())) {
					String val = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getInum().trim()).toPlainString();
					invoice.getB2b().get(0).getInv().get(0).setInum(val);
				} else {
					invoice.getB2b().get(0).getInv().get(0).setInum(invoice.getB2b().get(0).getInv().get(0).getInum().trim());
				}
			}
			if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
				invoice.getB2b().get(0).setCtin(invoice.getB2b().get(0).getCtin().trim());
			}
			String mapkey = "";
			if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
				mapkey = mapkey+invoice.getB2b().get(0).getInv().get(0).getInum().trim();
			}
			if(isNotEmpty(invoice.getInvtype())) {
				mapkey = mapkey+invoice.getInvtype().trim();
			}
			if(isNotEmpty(invoice.getFp())) {
				mapkey = mapkey+invoice.getFp().trim();
			}
			if(!templateType.equalsIgnoreCase(MasterGSTConstants.MASTERGST_EXCEL_TEMPLATE) && isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equals(MasterGSTConstants.NIL) || invoice.getInvtype().equals(MasterGSTConstants.ATPAID) || invoice.getInvtype().equals(MasterGSTConstants.ADVANCES))) {
				present = false;
			}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.ITC_REVERSAL)){
				present = false;
			}else if(templateType.equalsIgnoreCase(MasterGSTConstants.TALLY_TEMPLATE) && (isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.B2C))){
				present = false;
			}else {
			if(isNotEmpty(invoicesmap)) {
				InvoiceParent exstngInv = invoicesmap.get(mapkey);
				if(isNotEmpty(exstngInv)) {
				if (isEmpty(invoice.getB2b().get(0).getCtin()) || exstngInv.getB2b().get(0).getCtin().toUpperCase().equals(invoice.getB2b().get(0).getCtin().toUpperCase())) {
					if(exstngInv.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || exstngInv.getInvtype().equals(MasterGSTConstants.CDNA)) {
						String invCdnDocType;
						String existCdnDocType;
						if (returntype.equals(GSTR1)) {
							invCdnDocType = ((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty();
							existCdnDocType = ((GSTR1)exstngInv).getCdnr().get(0).getNt().get(0).getNtty();
						}else {
							invCdnDocType = ((PurchaseRegister)invoice).getCdn().get(0).getNt().get(0).getNtty();
							existCdnDocType = ((PurchaseRegister)invoice).getCdn().get(0).getNt().get(0).getNtty();
						}
						if(invCdnDocType.equals(existCdnDocType)) {
							if (isNotEmpty(invoice.getItems())) {
								if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
									if (invoice.getItems().get(0).getCategory()
											.equals(MasterGSTConstants.PRODUCT_CODE)
											|| invoice.getItems().get(0).getCategory()
													.equals(MasterGSTConstants.GOODS)) {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
									} else {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
									}
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								}
								defaultLedgerName(invoice,returntype);
								exstngInv.getItems().addAll(invoice.getItems());
								if (returntype.equals(GSTR1)) {
									GSTRItems gstritem = populateItem(exstngInv, isIntraState, returntype,invoice.getItems().get(0),((GSTR1) exstngInv).getCdnr().get(0).getNt().get(0).getItms(),null,hsnMap,sacMap);
									((GSTR1) exstngInv).getCdnr().get(0).getNt().get(0).getItms().add(gstritem);
									Double totalAmount = totalAmounts(exstngInv,gstritem,invoice,returntype,otherconfig);
									if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffSalesField()) && !otherconfig.isEnableroundoffSalesField()) {
										totalAmount = (double) Math.round(totalAmount);
									}
									((GSTR1) exstngInv).getCdnr().get(0).getNt().get(0).setVal(totalAmount);
								}else {
									GSTRItems gstritem = populateItem(exstngInv, isIntraState, returntype,invoice.getItems().get(0),((PurchaseRegister) exstngInv).getCdn().get(0).getNt().get(0).getItms(),null,hsnMap,sacMap);
									((PurchaseRegister) exstngInv).getCdn().get(0).getNt().get(0).getItms().add(gstritem);
									Double totalAmount = totalAmounts(exstngInv,gstritem,invoice,returntype,otherconfig);
									if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
										totalAmount = (double) Math.round(totalAmount);
									}
									((PurchaseRegister) exstngInv).getCdn().get(0).getNt().get(0).setVal(totalAmount);
								}
								present = true;
							}
						}
						
					}else if(exstngInv.getInvtype().equals(MasterGSTConstants.CDNUR) || exstngInv.getInvtype().equals(MasterGSTConstants.CDNURA)){
						String invCdnDocType;
						String existCdnDocType;
						if (returntype.equals(GSTR1)) {
							invCdnDocType = ((GSTR1)invoice).getCdnur().get(0).getNtty();
							existCdnDocType = ((GSTR1)exstngInv).getCdnur().get(0).getNtty();
						}else {
							invCdnDocType = ((PurchaseRegister)invoice).getCdnur().get(0).getNtty();
							existCdnDocType = ((PurchaseRegister)exstngInv).getCdnur().get(0).getNtty();
						}
						if(invCdnDocType.equals(existCdnDocType)) {
							if (isNotEmpty(invoice.getItems())) {
								if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
									if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE)	|| invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.GOODS)) {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
									} else {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
									}
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								}
								defaultLedgerName(invoice,returntype);
								exstngInv.getItems().addAll(invoice.getItems());
								if (returntype.equals(GSTR1)) {
									GSTRItems gstritem = populateItem(exstngInv, isIntraState, returntype,invoice.getItems().get(0),((GSTR1) exstngInv).getCdnur().get(0).getItms(),null,hsnMap,sacMap);
									((GSTR1) exstngInv).getCdnur().get(0).getItms().add(gstritem);
									Double totalAmount = totalAmounts(exstngInv,gstritem,invoice,returntype,otherconfig);
									if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffSalesField()) && !otherconfig.isEnableroundoffSalesField()) {
										totalAmount = (double) Math.round(totalAmount);
									}
									((GSTR1) exstngInv).getCdnur().get(0).setVal(totalAmount);
								}else {
									GSTRItems gstritem = populateItem(exstngInv, isIntraState, returntype,invoice.getItems().get(0),((PurchaseRegister) exstngInv).getCdnur().get(0).getItms(),null,hsnMap,sacMap);
									((PurchaseRegister) exstngInv).getCdnur().get(0).getItms().add(gstritem);
									Double totalAmount = totalAmounts(exstngInv,gstritem,invoice,returntype,otherconfig);
									if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
										totalAmount = (double) Math.round(totalAmount);
									}
									((PurchaseRegister) exstngInv).getCdnur().get(0).setVal(totalAmount);
								}
								present = true;
							}
						}
					}else if(exstngInv.getInvtype().equals(MasterGSTConstants.B2C) || exstngInv.getInvtype().equals(MasterGSTConstants.B2CSA)) {
						if (isNotEmpty(invoice.getItems())) {
							if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
								if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE)	|| invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.GOODS)) {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
								}
							} else {
								invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
							}
							defaultLedgerName(invoice,returntype);
							exstngInv.getItems().addAll(invoice.getItems());
							String stateTin = serviceUtils.getStateCode(invoice.getStatename());
							List<GSTRB2CS> b2cs = Lists.newArrayList();
							double totaltaxable = 0d;
							double totaltax = 0d;
							double totaligsttax = 0d;
							double totalcgsttax = 0d;
							double totalsgsttax = 0d;
							
							String b2cskey="";
							List<GSTRB2CS> newInvs = Lists.newArrayList();
							List<String> b2cskeys = Lists.newArrayList();
							Map<String,GSTRB2CS> newb2csmap = Maps.newHashMap();
							
							for (Item item : exstngInv.getItems()) {
								GSTRB2CS gstrb2csDetail = new GSTRB2CS();
								if (!isIntraState && isNotEmpty(item.getIgstrate())) {
									gstrb2csDetail.setRt(item.getIgstrate());
								} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())) {
									gstrb2csDetail.setRt(item.getCgstrate() + item.getSgstrate());
								}
								if (!isIntraState && isNotEmpty(item.getIgstamount())) {
									gstrb2csDetail.setIamt(item.getIgstamount());
									totaltax = totaltax+item.getIgstamount();
									totaligsttax = totaligsttax + item.getIgstamount();
								} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
									gstrb2csDetail.setCamt(item.getCgstamount());
									gstrb2csDetail.setSamt(item.getSgstamount());
									totaltax = totaltax+item.getCgstamount();
									totaltax = totaltax+item.getSgstamount();
									totalcgsttax = totalcgsttax+item.getCgstamount();
									totalsgsttax = totalsgsttax+item.getSgstamount();
								}
								if (isIntraState) {
									gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTRA);
								} else {
									gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTER);
								}
								gstrb2csDetail.setCsamt(item.getCessamount());
								gstrb2csDetail.setTxval(item.getTaxablevalue());
								if(isNotEmpty(item.getTaxablevalue())) {
									totaltaxable = totaltaxable+item.getTaxablevalue();
								}
								if (isNotEmpty(invoice.getStatename())) {
									gstrb2csDetail.setPos(stateTin);
								}
								String ecomGSTIN = "";
								if (isNotEmpty(invoice.getB2cs()) && isNotEmpty(invoice.getB2cs().get(0))
										&& isNotEmpty(invoice.getB2cs().get(0).getEtin())) {
									ecomGSTIN = invoice.getB2cs().get(0).getEtin();
								}
								gstrb2csDetail.setTyp("OE");
								if (isNotEmpty(ecomGSTIN)) {
									gstrb2csDetail.setTyp("E");
									gstrb2csDetail.setEtin(ecomGSTIN.toUpperCase());
								} else if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
									gstrb2csDetail.setTyp("E");
									gstrb2csDetail.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
								}
								String diffper = invoice.getDiffPercent();
								if ("Yes".equals(diffper)) {
									gstrb2csDetail.setDiffPercent(0.65);
								}
								if(B2CSA.equals(invoice.getInvtype())) {
									if(isNotEmpty(invoice.getB2cs()) && isNotEmpty(invoice.getB2cs().get(0)) && isNotEmpty(invoice.getB2cs().get(0).getOmon())) {
										gstrb2csDetail.setOmon(invoice.getB2cs().get(0).getOmon());
									}
								}
								//b2cs.add(gstrb2csDetail);
								b2cskey = gstrb2csDetail.getPos()+"-"+gstrb2csDetail.getRt();
								if(!newb2csmap.containsKey(b2cskey)) {
									newb2csmap.put(b2cskey, gstrb2csDetail);
									b2cskeys.add(b2cskey);
								}else {
									GSTRB2CS b2c = newb2csmap.get(b2cskey);
									if (isNotEmpty(gstrb2csDetail.getTxval())) {
										if (isNotEmpty(b2c.getTxval())) {
											b2c.setTxval(b2c.getTxval() + gstrb2csDetail.getTxval());
										} else {
											b2c.setTxval(gstrb2csDetail.getTxval());
										}
									}
									if (isNotEmpty(gstrb2csDetail.getIamt())) {
										if (isNotEmpty(b2c.getIamt())) {
											b2c.setIamt(b2c.getIamt() + gstrb2csDetail.getIamt());
										} else {
											b2c.setIamt(gstrb2csDetail.getIamt());
										}
									}
									if (isNotEmpty(gstrb2csDetail.getCamt())) {
										if (isNotEmpty(b2c.getCamt())) {
											b2c.setCamt(b2c.getCamt() + gstrb2csDetail.getCamt());
										} else {
											b2c.setCamt(gstrb2csDetail.getCamt());
										}
									}
									if (isNotEmpty(gstrb2csDetail.getSamt())) {
										if (isNotEmpty(b2c.getSamt())) {
											b2c.setSamt(b2c.getSamt() + gstrb2csDetail.getSamt());
										} else {
											b2c.setSamt(gstrb2csDetail.getSamt());
										}
									}
									if (isNotEmpty(gstrb2csDetail.getCsamt())) {
										if (isNotEmpty(b2c.getCsamt())) {
											b2c.setCsamt(b2c.getCsamt() + gstrb2csDetail.getCsamt());
										} else {
											b2c.setCsamt(gstrb2csDetail.getCsamt());
										}
									}
									newb2csmap.put(b2cskey, b2c);
								}
							}
							exstngInv.setTotaltaxableamount(totaltaxable);
							exstngInv.setTotaltax(totaltax);
							exstngInv.setTotalamount(totaltaxable + totaltax);
							exstngInv.setTotalIgstAmount(totaligsttax);
							exstngInv.setTotalCgstAmount(totalcgsttax);
							exstngInv.setTotalSgstAmount(totalsgsttax);
							for(String key : b2cskeys) {
								GSTRB2CS b2c = newb2csmap.get(key);
								newInvs.add(b2c);
							}
							exstngInv.setB2cs(newInvs);
							present = true;
							
							if(B2CSA.equals(invoice.getInvtype())) {
								if(invoice.getInvtype().equals(MasterGSTConstants.B2CSA)) {
									
									Map<String,GSTRItemDetails> itemsmap = Maps.newHashMap();
									List<GSTRB2CSA> gstrb2csaDetaillst = Lists.newArrayList();
									GSTRB2CSA gstrb2csaDetail = new GSTRB2CSA();
									if (isIntraState) {
										gstrb2csaDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTRA);
									} else {
										gstrb2csaDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTER);
									}
									if (isNotEmpty(invoice.getStatename())) {
										gstrb2csaDetail.setPos(stateTin);
									}
									String ecomGSTIN = "";
									if (isNotEmpty(invoice.getB2cs()) && isNotEmpty(invoice.getB2cs().get(0))
											&& isNotEmpty(invoice.getB2cs().get(0).getEtin())) {
										ecomGSTIN = invoice.getB2cs().get(0).getEtin();
									}
									gstrb2csaDetail.setTyp("OE");
									if (isNotEmpty(ecomGSTIN)) {
										gstrb2csaDetail.setTyp("E");
									} else if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
										gstrb2csaDetail.setTyp("E");
									}
									String diffper = invoice.getDiffPercent();
									if ("Yes".equals(diffper)) {
										gstrb2csaDetail.setDiffPercent(0.65);
									}
									if(isNotEmpty(invoice.getB2cs()) && isNotEmpty(invoice.getB2cs().get(0)) && isNotEmpty(invoice.getB2cs().get(0).getOmon())) {
										gstrb2csaDetail.setOmon(invoice.getB2cs().get(0).getOmon());
									}
									List<GSTRItemDetails> itms = Lists.newArrayList();
									for (Item item : invoice.getItems()) {
										GSTRItemDetails itm = new GSTRItemDetails();
										
										if (isNotEmpty(item.getIgstrate())) {
											itm.setRt(item.getIgstrate());
										} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())) {
											itm.setRt(item.getCgstrate() + item.getSgstrate());
										}
										if (!isIntraState && isNotEmpty(item.getIgstamount())) {
											itm.setIamt(item.getIgstamount());
										} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
											itm.setCamt(item.getCgstamount());
											itm.setSamt(item.getSgstamount());
										}
										
										if (isNotEmpty(item.getCessamount())) {
											itm.setCsamt(item.getCessamount());
										}
										if (isNotEmpty(item.getTaxablevalue())) {
											itm.setTxval(item.getTaxablevalue());
										}
										if(!itemsmap.containsKey(b2cskey)) {
											itemsmap.put(b2cskey, itm);
										}else {
											GSTRItemDetails itmm = itemsmap.get(b2cskey);
											if (isNotEmpty(item.getTaxablevalue())) {
												if (isNotEmpty(itmm.getTxval())) {
													itmm.setTxval(itmm.getTxval() + item.getTaxablevalue());
												} else {
													itmm.setTxval(item.getTaxablevalue());
												}
											}
											if (isNotEmpty(item.getIgstamount())) {
												if (isNotEmpty(itmm.getIamt())) {
													itmm.setIamt(itmm.getIamt() + item.getIgstamount());
												} else {
													itmm.setIamt(item.getIgstamount());
												}
											}
											if (isNotEmpty(item.getCgstamount())) {
												if (isNotEmpty(itmm.getCamt())) {
													itmm.setCamt(itmm.getCamt() + item.getCgstamount());
												} else {
													itmm.setCamt(item.getCgstamount());
												}
											}
											if (isNotEmpty(item.getSgstamount())) {
												if (isNotEmpty(itmm.getSamt())) {
													itmm.setSamt(itmm.getSamt() + item.getSgstamount());
												} else {
													itmm.setSamt(item.getSgstamount());
												}
											}
											if (isNotEmpty(item.getCessamount())) {
												if (isNotEmpty(itmm.getCsamt())) {
													itmm.setCsamt(itmm.getCsamt() + item.getCessamount());
												} else {
													itmm.setCsamt(item.getCessamount());
												}
											}
											itemsmap.put(b2cskey, itmm);
										}
									}
									List<GSTRItemDetails> items = itemsmap.values().stream().collect(Collectors.toList());
									gstrb2csaDetail.setItms(items);
									gstrb2csaDetaillst.add(gstrb2csaDetail);
									((GSTR1)invoice).setB2csa(gstrb2csaDetaillst);
									clientUtils.populateImportAmendmentDetails(invoice, null, invoice.getB2cs().get(0).getOmon(), B2C);
								}
							}
						}
					}else if(exstngInv.getInvtype().equals(MasterGSTConstants.B2CL) || exstngInv.getInvtype().equals(MasterGSTConstants.B2CLA)) {
						if (isNotEmpty(invoice.getItems())) {
							if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
								if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE)	|| invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.GOODS)) {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
								}
							} else {
								invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
							}
							defaultLedgerName(invoice,returntype);
							exstngInv.getItems().addAll(invoice.getItems());
							GSTRItems gstritem = populateItem(exstngInv, isIntraState, returntype,invoice.getItems().get(0),exstngInv.getB2cl().get(0).getInv().get(0).getItms(),null,hsnMap,sacMap);
							exstngInv.getB2cl().get(0).getInv().get(0).getItms().add(gstritem);
							Double totalAmount = totalAmounts(exstngInv,gstritem,invoice,returntype,otherconfig);
							if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffSalesField()) && !otherconfig.isEnableroundoffSalesField()) {
								totalAmount = (double) Math.round(totalAmount);
							}
							exstngInv.getB2cl().get(0).getInv().get(0).setVal(totalAmount);
								present = true;
						}
					}else if(exstngInv.getInvtype().equals(MasterGSTConstants.B2BUR)) {
						if (isNotEmpty(invoice.getItems())) {
							if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
								if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE) || invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.GOODS)) {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
								}
							} else {
								invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
							}
							defaultLedgerName(invoice,returntype);
							exstngInv.getItems().addAll(invoice.getItems());
							GSTRItems gstritem = populateItem(exstngInv, isIntraState, returntype,invoice.getItems().get(0),((PurchaseRegister) exstngInv).getB2bur().get(0).getInv().get(0).getItms(),null,hsnMap,sacMap);
							((PurchaseRegister) exstngInv).getB2bur().get(0).getInv().get(0).getItms().add(gstritem);
							Double totalAmount = totalAmounts(exstngInv,gstritem,invoice,returntype,otherconfig);
							if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
								totalAmount = (double) Math.round(totalAmount);
							}
							((PurchaseRegister) exstngInv).getB2bur().get(0).getInv().get(0).setVal(totalAmount);
							present = true;
						}
					} else if(exstngInv.getInvtype().equals(MasterGSTConstants.IMP_GOODS)){
						if (isNotEmpty(invoice.getItems())) {
							if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
								if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE)	|| invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.GOODS)) {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
								}
							} else {
								invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
							}
							defaultLedgerName(invoice,returntype);
							exstngInv.getItems().addAll(invoice.getItems());
							GSTRItems gstritem = populateItem(exstngInv, isIntraState, returntype,invoice.getItems().get(0),null,((PurchaseRegister) exstngInv).getImpGoods().get(0).getItms(),hsnMap,sacMap);
							double totalValue = 0d;
							if(isNotEmpty(exstngInv.getTotalamount())) {
								totalValue = exstngInv.getTotalamount();
							}
								if (isNotEmpty(gstritem.getItem()) && isNotEmpty(gstritem.getItc())) {
									GSTRImportItems importItem = new GSTRImportItems();
									importItem.setNum(gstritem.getNum());
									importItem.setRt(gstritem.getItem().getRt());
									importItem.setTxval(gstritem.getItem().getTxval());
									importItem.setIamt(gstritem.getItem().getIamt());
									importItem.setCsamt(gstritem.getItem().getCsamt());
									importItem.setElg(gstritem.getItc().getElg());
									importItem.setiTax(gstritem.getItc().getiTax());
									importItem.setCsTax(gstritem.getItc().getCsTax());
									if(isNotEmpty(gstritem.getItem().getTxval())) {
										totalValue = totalValue+gstritem.getItem().getTxval();
									}
									if(isNotEmpty(gstritem.getItem().getIamt())) {
										totalValue = totalValue+gstritem.getItem().getIamt();
									}
									if(isNotEmpty(gstritem.getItem().getCsamt())) {
										totalValue = totalValue+gstritem.getItem().getCsamt();
									}
									((PurchaseRegister) exstngInv).getImpGoods().get(0).getItms().add(importItem);
								}
							
							if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
								totalValue = (double) Math.round(totalValue);
							}
							Double totalAmount = totalAmounts(exstngInv,gstritem,invoice,returntype,otherconfig);
							((PurchaseRegister) exstngInv).getImpGoods().get(0).setBoeVal(totalValue);
							present = true;
						}
					}else if(exstngInv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_SERVICES)){
						if (isNotEmpty(invoice.getItems())) {
							if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
								if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE)	|| invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.GOODS)) {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
								}
							} else {
								invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
							}
							defaultLedgerName(invoice,returntype);
							exstngInv.getItems().addAll(invoice.getItems());
							GSTRItems gstritem = populateItem(exstngInv, isIntraState, returntype,invoice.getItems().get(0),null,((PurchaseRegister) exstngInv).getImpServices().get(0).getItms(),hsnMap,sacMap);
							
							double totalValue = 0d;
							if(isNotEmpty(exstngInv.getTotalamount())) {
								totalValue = exstngInv.getTotalamount();
							}
								if (isNotEmpty(gstritem.getItem()) && isNotEmpty(gstritem.getItc())) {
									GSTRImportItems importItem = new GSTRImportItems();
									importItem.setNum(gstritem.getNum());
									importItem.setRt(gstritem.getItem().getRt());
									importItem.setTxval(gstritem.getItem().getTxval());
									importItem.setIamt(gstritem.getItem().getIamt());
									importItem.setCsamt(gstritem.getItem().getCsamt());
									importItem.setElg(gstritem.getItc().getElg());
									importItem.setiTax(gstritem.getItc().getiTax());
									importItem.setCsTax(gstritem.getItc().getCsTax());
									if(isNotEmpty(gstritem.getItem().getTxval())) {
										totalValue = totalValue+gstritem.getItem().getTxval();
									}
									if(isNotEmpty(gstritem.getItem().getIamt())) {
										totalValue = totalValue+gstritem.getItem().getIamt();
									}
									if(isNotEmpty(gstritem.getItem().getCsamt())) {
										totalValue = totalValue+gstritem.getItem().getCsamt();
									}
									((PurchaseRegister) exstngInv).getImpServices().get(0).getItms().add(importItem);
								}
							if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
								totalValue = (double) Math.round(totalValue);
							}
							Double totalAmount = totalAmounts(exstngInv,gstritem,invoice,returntype,otherconfig);
							((PurchaseRegister) exstngInv).getImpServices().get(0).setIval(totalValue);
							present = true;
						}
					}else if(exstngInv.getInvtype().equalsIgnoreCase(MasterGSTConstants.EXPORTS)){
						if (isNotEmpty(invoice.getItems())) {
							if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
								if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE) || invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.GOODS)) {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
								}
							} else {
								invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
							}
							defaultLedgerName(invoice,returntype);
							exstngInv.getItems().addAll(invoice.getItems());
							//List<GSTRItems> gstrItems = populateItemData(exstngInv, isIntraState,returntype);
							GSTRItems gstritem = populateItem(exstngInv, isIntraState, returntype,invoice.getItems().get(0),exstngInv.getB2b().get(0).getInv().get(0).getItms(),null,hsnMap,sacMap);
							if(isNotEmpty(gstritem) && isNotEmpty(gstritem.getItem())) {
								exstngInv.getExp().get(0).getInv().get(0).getItms().add(gstritem.getItem());
							}
							Double totalAmount = totalAmounts(exstngInv,gstritem,invoice,returntype,otherconfig);
							exstngInv.getExp().get(0).getInv().get(0).setVal(totalAmount);
							present = true;
						}
					}else if(exstngInv.getInvtype().equalsIgnoreCase(MasterGSTConstants.EXPA)){
						if (isNotEmpty(invoice.getItems())) {
							if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
								if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE) || invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.GOODS)) {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
								}
							} else {
								invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
							}
							defaultLedgerName(invoice,returntype);
							exstngInv.getItems().addAll(invoice.getItems());
							//List<GSTRItems> gstrItems = populateItemData(exstngInv, isIntraState,returntype);
							GSTRItems gstritem = populateItem(exstngInv, isIntraState, returntype,invoice.getItems().get(0),exstngInv.getB2b().get(0).getInv().get(0).getItms(),null,hsnMap,sacMap);
							if(isNotEmpty(gstritem) && isNotEmpty(gstritem.getItem())) {
								exstngInv.getExp().get(0).getInv().get(0).getItms().add(gstritem.getItem());
							}
							Double totalAmount = totalAmounts(exstngInv,gstritem,invoice,returntype,otherconfig);
							exstngInv.getExp().get(0).getInv().get(0).setVal(totalAmount);
							present = true;
							((GSTR1)invoice).setExpa(exstngInv.getExp());
							String oidt = new SimpleDateFormat("dd/MM/YYYY").format(exstngInv.getExp().get(0).getInv().get(0).getOidt());
							clientUtils.populateImportAmendmentDetails(invoice, exstngInv.getExp().get(0).getInv().get(0).getOinum(), oidt, EXPORTS);
						}
					}else if(exstngInv.getInvtype().equalsIgnoreCase(MasterGSTConstants.NIL)){
						if (isNotEmpty(invoice.getItems())) {
							if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
								if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE) || invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.GOODS)) {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
								}
							} else {
								invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
							}
							defaultLedgerName(invoice,returntype);
							exstngInv.getItems().addAll(invoice.getItems());
							String supplyType = null;
							if (isIntraState) {
								if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
									supplyType = MasterGSTConstants.SUPPLY_TYPE_INTRA + B2B;
								} else {
									supplyType = MasterGSTConstants.SUPPLY_TYPE_INTRA + B2C;
								}
							} else {
								if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
									supplyType = "INTR" + B2B;
								} else {
									supplyType = "INTR" + B2C;
								}
							}
								if (isNotEmpty(invoice.getItems().get(0).getType()) && isNotEmpty(supplyType)) {
									if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
										if (isNotEmpty(((PurchaseRegister)(exstngInv)).getNilSupplies())) {
											PurchaseRegister pinvoice = (PurchaseRegister) (invoice);
											if (isEmpty(pinvoice.getNilSupplies())) {
												pinvoice.setNilSupplies(new GSTRNilSupplies());
											}
											if (isIntraState) {
												if (isEmpty(((PurchaseRegister)(exstngInv)).getNilSupplies().getIntra())) {
													((PurchaseRegister)(exstngInv)).getNilSupplies().setIntra(new GSTRNilSupItems());
												}
												populateNilItems(invoice.getItems().get(0), ((PurchaseRegister)(exstngInv)).getNilSupplies().getIntra());
											} else {
												if (isEmpty(((PurchaseRegister)(exstngInv)).getNilSupplies().getInter())) {
													((PurchaseRegister)(exstngInv)).getNilSupplies().setInter(new GSTRNilSupItems());
												}
												populateNilItems(invoice.getItems().get(0), ((PurchaseRegister)(exstngInv)).getNilSupplies().getInter());
											}
										}
									}else {
										if (isNotEmpty(exstngInv.getNil()) && isNotEmpty(exstngInv.getNil().getInv())) {
											boolean nilpresent = false;
											for (GSTRNilItems eItem : exstngInv.getNil().getInv()) {
												if (isNotEmpty(eItem.getSplyType()) && eItem.getSplyType().equals(supplyType)) {
													nilpresent = true;
													populateNilItems(invoice.getItems().get(0), eItem);
													break;
												}
											}
											if (!nilpresent) {
												GSTRNilItems eItem = new GSTRNilItems();
												eItem.setSplyType(supplyType);
												populateNilItems(invoice.getItems().get(0), eItem);
												exstngInv.getNil().getInv().add(eItem);
											}
										} else {
											GSTRNilInvoices nil = new GSTRNilInvoices();
											List<GSTRNilItems> nilItems = Lists.newArrayList();
											GSTRNilItems eItem = new GSTRNilItems();
											eItem.setSplyType(supplyType);
											populateNilItems(invoice.getItems().get(0), eItem);
											nilItems.add(eItem);
											nil.setInv(nilItems);
											exstngInv.setNil(nil);
										}
									}
								}
								nilTotalAmounts(exstngInv,returntype,otherconfig);
								present = true;
						}
					}else {
						if (isNotEmpty(invoice.getItems())) {
							if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
								if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE) || invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.GOODS)) {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
								}
							} else {
								invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
							}
							defaultLedgerName(invoice,returntype);
							exstngInv.getItems().addAll(invoice.getItems());
							//List<GSTRItems> gstrItems = populateItemData(exstngInv, isIntraState,returntype);
							GSTRItems gstritem = populateItem(exstngInv, isIntraState, returntype,invoice.getItems().get(0),exstngInv.getB2b().get(0).getInv().get(0).getItms(),null,hsnMap,sacMap);
							exstngInv.getB2b().get(0).getInv().get(0).getItms().add(gstritem);
							Double totalAmount = totalAmounts(exstngInv,gstritem,invoice,returntype,otherconfig);
							exstngInv.getB2b().get(0).getInv().get(0).setVal(totalAmount);
							present = true;
						}
					}
				}
			}
			}else {
				if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.B2CSA)) {
					if (isNotEmpty(invoice.getItems())) {
						if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
							if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE)	|| invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.GOODS)) {
								invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
							} else {
								invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
							}
						} else {
							invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
						}
						defaultLedgerName(invoice,returntype);
						String stateTin = serviceUtils.getStateCode(invoice.getStatename());
						List<GSTRB2CS> b2cs = Lists.newArrayList();
						double totaltaxable = 0d;
						double totaltax = 0d;
						double totaligsttax = 0d;
						double totalcgsttax = 0d;
						double totalsgsttax = 0d;
						
						String b2cskey="";
						List<GSTRB2CS> newInvs = Lists.newArrayList();
						List<String> b2cskeys = Lists.newArrayList();
						Map<String,GSTRB2CS> newb2csmap = Maps.newHashMap();
						
						for (Item item : invoice.getItems()) {
							GSTRB2CS gstrb2csDetail = new GSTRB2CS();
							if (!isIntraState && isNotEmpty(item.getIgstrate())) {
								gstrb2csDetail.setRt(item.getIgstrate());
							} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())) {
								gstrb2csDetail.setRt(item.getCgstrate() + item.getSgstrate());
							}
							if (!isIntraState && isNotEmpty(item.getIgstamount())) {
								gstrb2csDetail.setIamt(item.getIgstamount());
								totaltax = totaltax+item.getIgstamount();
								totaligsttax = totaligsttax + item.getIgstamount();
							} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
								gstrb2csDetail.setCamt(item.getCgstamount());
								gstrb2csDetail.setSamt(item.getSgstamount());
								totaltax = totaltax+item.getCgstamount();
								totaltax = totaltax+item.getSgstamount();
								totalcgsttax = totalcgsttax+item.getCgstamount();
								totalsgsttax = totalsgsttax+item.getSgstamount();
							}
							if (isIntraState) {
								gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTRA);
							} else {
								gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTER);
							}
							gstrb2csDetail.setCsamt(item.getCessamount());
							gstrb2csDetail.setTxval(item.getTaxablevalue());
							if(isNotEmpty(item.getTaxablevalue())) {
								totaltaxable = totaltaxable+item.getTaxablevalue();
							}
							if (isNotEmpty(invoice.getStatename())) {
								gstrb2csDetail.setPos(stateTin);
							}
							String ecomGSTIN = "";
							if (isNotEmpty(invoice.getB2cs()) && isNotEmpty(invoice.getB2cs().get(0))
									&& isNotEmpty(invoice.getB2cs().get(0).getEtin())) {
								ecomGSTIN = invoice.getB2cs().get(0).getEtin();
							}
							gstrb2csDetail.setTyp("OE");
							if (isNotEmpty(ecomGSTIN)) {
								gstrb2csDetail.setTyp("E");
								gstrb2csDetail.setEtin(ecomGSTIN.toUpperCase());
							} else if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
								gstrb2csDetail.setTyp("E");
								gstrb2csDetail.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
							}
							String diffper = invoice.getDiffPercent();
							if ("Yes".equals(diffper)) {
								gstrb2csDetail.setDiffPercent(0.65);
							}
							if(isNotEmpty(invoice.getB2cs()) && isNotEmpty(invoice.getB2cs().get(0)) && isNotEmpty(invoice.getB2cs().get(0).getOmon())) {
								gstrb2csDetail.setOmon(invoice.getB2cs().get(0).getOmon());
							}
							//b2cs.add(gstrb2csDetail);
							b2cskey = gstrb2csDetail.getPos()+"-"+gstrb2csDetail.getRt();
							if(!newb2csmap.containsKey(b2cskey)) {
								newb2csmap.put(b2cskey, gstrb2csDetail);
								b2cskeys.add(b2cskey);
							}else {
								GSTRB2CS b2c = newb2csmap.get(b2cskey);
								if (isNotEmpty(gstrb2csDetail.getTxval())) {
									if (isNotEmpty(b2c.getTxval())) {
										b2c.setTxval(b2c.getTxval() + gstrb2csDetail.getTxval());
									} else {
										b2c.setTxval(gstrb2csDetail.getTxval());
									}
								}
								if (isNotEmpty(gstrb2csDetail.getIamt())) {
									if (isNotEmpty(b2c.getIamt())) {
										b2c.setIamt(b2c.getIamt() + gstrb2csDetail.getIamt());
									} else {
										b2c.setIamt(gstrb2csDetail.getIamt());
									}
								}
								if (isNotEmpty(gstrb2csDetail.getCamt())) {
									if (isNotEmpty(b2c.getCamt())) {
										b2c.setCamt(b2c.getCamt() + gstrb2csDetail.getCamt());
									} else {
										b2c.setCamt(gstrb2csDetail.getCamt());
									}
								}
								if (isNotEmpty(gstrb2csDetail.getSamt())) {
									if (isNotEmpty(b2c.getSamt())) {
										b2c.setSamt(b2c.getSamt() + gstrb2csDetail.getSamt());
									} else {
										b2c.setSamt(gstrb2csDetail.getSamt());
									}
								}
								if (isNotEmpty(gstrb2csDetail.getCsamt())) {
									if (isNotEmpty(b2c.getCsamt())) {
										b2c.setCsamt(b2c.getCsamt() + gstrb2csDetail.getCsamt());
									} else {
										b2c.setCsamt(gstrb2csDetail.getCsamt());
									}
								}
								newb2csmap.put(b2cskey, b2c);
							}
						}
						invoice.setTotaltaxableamount(totaltaxable);
						invoice.setTotaltax(totaltax);
						invoice.setTotalamount(totaltaxable + totaltax);
						invoice.setTotalIgstAmount(totaligsttax);
						invoice.setTotalCgstAmount(totalcgsttax);
						invoice.setTotalSgstAmount(totalsgsttax);
						for(String key : b2cskeys) {
							GSTRB2CS b2c = newb2csmap.get(key);
							newInvs.add(b2c);
						}
						invoice.setB2cs(newInvs);
												
						if(B2CSA.equals(invoice.getInvtype())) {
							if(invoice.getInvtype().equals(MasterGSTConstants.B2CSA)) {
								
								Map<String,GSTRItemDetails> itemsmap = Maps.newHashMap();
								List<GSTRB2CSA> gstrb2csaDetaillst = Lists.newArrayList();
								GSTRB2CSA gstrb2csaDetail = new GSTRB2CSA();
								if (isIntraState) {
									gstrb2csaDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTRA);
								} else {
									gstrb2csaDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTER);
								}
								if (isNotEmpty(invoice.getStatename())) {
									gstrb2csaDetail.setPos(stateTin);
								}
								String ecomGSTIN = "";
								if (isNotEmpty(invoice.getB2cs()) && isNotEmpty(invoice.getB2cs().get(0))
										&& isNotEmpty(invoice.getB2cs().get(0).getEtin())) {
									ecomGSTIN = invoice.getB2cs().get(0).getEtin();
								}
								gstrb2csaDetail.setTyp("OE");
								if (isNotEmpty(ecomGSTIN)) {
									gstrb2csaDetail.setTyp("E");
								} else if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
									gstrb2csaDetail.setTyp("E");
								}
								String diffper = invoice.getDiffPercent();
								if ("Yes".equals(diffper)) {
									gstrb2csaDetail.setDiffPercent(0.65);
								}
								if(isNotEmpty(invoice.getB2cs()) && isNotEmpty(invoice.getB2cs().get(0)) && isNotEmpty(invoice.getB2cs().get(0).getOmon())) {
									gstrb2csaDetail.setOmon(invoice.getB2cs().get(0).getOmon());
								}
								List<GSTRItemDetails> itms = Lists.newArrayList();
								for (Item item : invoice.getItems()) {
									GSTRItemDetails itm = new GSTRItemDetails();
									
									if (isNotEmpty(item.getIgstrate())) {
										itm.setRt(item.getIgstrate());
									} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())) {
										itm.setRt(item.getCgstrate() + item.getSgstrate());
									}
									if (!isIntraState && isNotEmpty(item.getIgstamount())) {
										itm.setIamt(item.getIgstamount());
									} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
										itm.setCamt(item.getCgstamount());
										itm.setSamt(item.getSgstamount());
									}
									
									if (isNotEmpty(item.getCessamount())) {
										itm.setCsamt(item.getCessamount());
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										itm.setTxval(item.getTaxablevalue());
									}
									if(!itemsmap.containsKey(b2cskey)) {
										itemsmap.put(b2cskey, itm);
									}else {
										GSTRItemDetails itmm = itemsmap.get(b2cskey);
										if (isNotEmpty(item.getTaxablevalue())) {
											if (isNotEmpty(itmm.getTxval())) {
												itmm.setTxval(itmm.getTxval() + item.getTaxablevalue());
											} else {
												itmm.setTxval(item.getTaxablevalue());
											}
										}
										if (isNotEmpty(item.getIgstamount())) {
											if (isNotEmpty(itmm.getIamt())) {
												itmm.setIamt(itmm.getIamt() + item.getIgstamount());
											} else {
												itmm.setIamt(item.getIgstamount());
											}
										}
										if (isNotEmpty(item.getCgstamount())) {
											if (isNotEmpty(itmm.getCamt())) {
												itmm.setCamt(itmm.getCamt() + item.getCgstamount());
											} else {
												itmm.setCamt(item.getCgstamount());
											}
										}
										if (isNotEmpty(item.getSgstamount())) {
											if (isNotEmpty(itmm.getSamt())) {
												itmm.setSamt(itmm.getSamt() + item.getSgstamount());
											} else {
												itmm.setSamt(item.getSgstamount());
											}
										}
										if (isNotEmpty(item.getCessamount())) {
											if (isNotEmpty(itmm.getCsamt())) {
												itmm.setCsamt(itmm.getCsamt() + item.getCessamount());
											} else {
												itmm.setCsamt(item.getCessamount());
											}
										}
										itemsmap.put(b2cskey, itmm);
									}
								}
								List<GSTRItemDetails> items = itemsmap.values().stream().collect(Collectors.toList());
								gstrb2csaDetail.setItms(items);
								gstrb2csaDetaillst.add(gstrb2csaDetail);
								((GSTR1)invoice).setB2csa(gstrb2csaDetaillst);
								clientUtils.populateImportAmendmentDetails(invoice, null, invoice.getB2cs().get(0).getOmon(), B2C);
							}
						}
					}
				}
			}
		}
			String pInvType = invType;
			if (!present && isNotEmpty(invoice.getB2b())
					&& isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
				if (isEmpty(invType) || invType.equals(B2B)) {
					Double totalAmount = 0d;
					if (isNotEmpty(invoice.getItems())) {
						for (Item item : invoice.getItems()) {
							if (isNotEmpty(item.getTotal())) {
								totalAmount += item.getTotal();
							}
						}
					}
					if (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())
							|| invoice.getB2b().get(0).getCtin().trim().isEmpty()) {
						
						if (totalAmount > 250000 && !isIntraState) {
							pInvType = B2CL;
						} else {
							pInvType = B2C;
						}
					} else {
						pInvType = B2B;
					}
				}else if (isEmpty(invType) || invType.equals(B2BA)) {
					Double totalAmount = 0d;
					if (isNotEmpty(invoice.getItems())) {
						for (Item item : invoice.getItems()) {
							if (isNotEmpty(item.getTotal())) {
								totalAmount += item.getTotal();
							}
						}
					}
					if (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())
							|| invoice.getB2b().get(0).getCtin().trim().isEmpty()) {
						
						if (totalAmount > 250000 && !isIntraState) {
							pInvType = B2CLA;
						} else {
							pInvType = B2CSA;
						}
					} else {
						pInvType = B2BA;
					}
				}
				InvoiceParent eInv = null;
				if (returntype.equals(GSTR1)) {
					if (isNotEmpty(pInvType)) {
						eInv = gstr1Repository.findByClientidAndInvtypeAndInvoicenoAndFp(client.getId().toString(),
								pInvType, invoice.getB2b().get(0).getInv().get(0).getInum(), invoice.getFp());
					}
				}else if (returntype.equals(MasterGSTConstants.EWAYBILL)) {
					if (isNotEmpty(pInvType)) {
						eInv = ewayBillRepository.findByClientidAndInvtypeAndInvoicenoAndFp(client.getId().toString(),
								pInvType, invoice.getB2b().get(0).getInv().get(0).getInum(), invoice.getFp());
					}
				}else {
					if (isNotEmpty(pInvType)) {
						if(isNotEmpty(invoice.getB2b().get(0).getCtin())) {
							eInv = purchaseRepository.findByClientidAndInvtypeAndInvoicenoAndB2b_CtinIgnoreCaseAndFp(client.getId().toString(),
								pInvType, invoice.getB2b().get(0).getInv().get(0).getInum(),invoice.getB2b().get(0).getCtin(), invoice.getFp());
						}else {
							eInv = purchaseRepository.findByClientidAndInvtypeAndInvoicenoAndFp(client.getId().toString(),
									pInvType, invoice.getB2b().get(0).getInv().get(0).getInum(), invoice.getFp());
						}
					}
				}
				
					if (isNotEmpty(eInv)) {
						present = true;
					}
				
			}
			if (!present) {
				if(isNotEmpty(invoice.getInvtype()) && (B2BA.equals(invoice.getInvtype()) || B2CLA.equals(invoice.getInvtype()) || CDNA.equals(invoice.getInvtype()) || CDNURA.equals(invoice.getInvtype()) || EXPA.equals(invoice.getInvtype()))) {
					if(isNotEmpty(invoice.getStrOdate())) {
						Calendar cal = Calendar.getInstance();
						int month = cal.get(Calendar.MONTH) + 1;
						int year = cal.get(Calendar.YEAR);
						Date strOdate = invdate(invoice.getStrOdate(), patterns, year, month);
						invoice.setStrOdate(new SimpleDateFormat("dd-MM-yyyy").format(strOdate));
					}
				}
				if (isNotEmpty(invoice.getStatename())) {
					//String strState = invoice.getStatename();
					Integer pos = null;
					String strstate = invoice.getStatename();
					if (isNotEmpty(strstate)) {
						if (strstate.contains("-")) {
							strstate = strstate.substring(strstate.indexOf("-")+1).trim();
						}
						StateConfig state = stateconfig.get(strstate.replaceAll("\\s", "").toLowerCase());
						if(isNotEmpty(state)) {
							invoice.setStatename(state.getName());
							pos = state.getTin();
						}
						if (isNotEmpty(pos) && isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())
								&& isEmpty(invoice.getB2b().get(0).getInv().get(0).getPos())) {
							String strPos = pos < 10 ? "0"+pos : pos.toString();
							invoice.getB2b().get(0).getInv().get(0).setPos(strPos);
						}
					}
				}
				invoice.setUserid(id);
				invoice.setFullname(fullname);
				invoice.setClientid(clientId);
				invoice.setGstin(client.getGstnnumber());
				if (isNotEmpty(pInvType)) {
					invoice.setInvtype(pInvType);
				}
				Double totalvalue = 0d;
				invoice = populateInvoiceInfo(invoice, returntype, isIntraState,stateconfig,hsnMap,sacMap,statetin,invoicePendingActions);
				if(isNotEmpty(invoice.getTotalamount())) {
					totalvalue = invoice.getTotalamount();
				}
				if(isNotEmpty(totalvalue)) {
					if (returntype.equals(GSTR1)) {
						if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffSalesField()) && !otherconfig.isEnableroundoffSalesField()) {
							totalvalue = (double) Math.round(totalvalue);
						}
					}else {
						if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
							totalvalue = (double) Math.round(totalvalue);
							if(isNotEmpty(invoice.getTotalamount())) {
								Double notroundofftamnt = invoice.getTotalamount();
								Double roundofftamt = (double) Math.round(notroundofftamnt);
								Double roundoffamt = 0d;
								roundoffamt = roundofftamt - notroundofftamnt;
								invoice.setTotalamount(roundofftamt);
								invoice.setNotroundoftotalamount(notroundofftamnt);
								invoice.setRoundOffAmount(Double.parseDouble(df2.format(roundoffamt)));
								String totalAmtStr = String.format(DOUBLE_FORMAT,roundofftamt);
								invoice.setTotalamount_str(totalAmtStr);
							}
						}
					}
				}
				if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNA)) {
					if (returntype.equals(GSTR1)) {
						((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setVal(totalvalue);
					}else {
						((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setVal(totalvalue);
					}
				}else if(invoice.getInvtype().equals(MasterGSTConstants.CDNUR) || invoice.getInvtype().equals(MasterGSTConstants.CDNURA)) {
					if (returntype.equals(GSTR1)) {
						((GSTR1) invoice).getCdnur().get(0).setVal(totalvalue);
					}else {
						((PurchaseRegister) invoice).getCdnur().get(0).setVal(totalvalue);
					}
				}else if(invoice.getInvtype().equals(MasterGSTConstants.B2CL)) {
					invoice.getB2cl().get(0).getInv().get(0).setVal(totalvalue);
				}else if(invoice.getInvtype().equals(MasterGSTConstants.B2BUR)) {
					((PurchaseRegister) invoice).getB2bur().get(0).getInv().get(0).setVal(totalvalue);
				}else if(invoice.getInvtype().equals(MasterGSTConstants.IMP_GOODS)){
					((PurchaseRegister) invoice).getImpGoods().get(0).setBoeVal(totalvalue);
				}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_SERVICES)){
					((PurchaseRegister) invoice).getImpServices().get(0).setIval(totalvalue);
				}else {
					invoice.getB2b().get(0).getInv().get(0).setVal(totalvalue);
				}
				if(!templateType.equalsIgnoreCase(MasterGSTConstants.MASTERGST_EXCEL_TEMPLATE) && isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equals(MasterGSTConstants.NIL) || invoice.getInvtype().equals(MasterGSTConstants.ATPAID) || invoice.getInvtype().equals(MasterGSTConstants.ADVANCES))) {
				}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.ITC_REVERSAL)){
				}else if(templateType.equalsIgnoreCase(MasterGSTConstants.TALLY_TEMPLATE) && (isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.B2C))){
				}else {
					String key = "";
					if(isNotEmpty(invoice.getInvoiceno())) {
						key = key+invoice.getInvoiceno().trim();
					}
					if(isNotEmpty(invoice.getInvtype())) {
						key = key+invoice.getInvtype().trim();
					}
					if(isNotEmpty(invoice.getFp())) {
						key = key+invoice.getFp().trim();
					}
					invoicesmap.put(key, invoice);
				}
				if (returntype.equals(GSTR1)) {
					existingInvoices.add((GSTR1) invoice);
				} else if (returntype.equals(GSTR4)) {
					existingInvoices.add((GSTR4) invoice);
				} else if (returntype.equals(GSTR5)) {
					existingInvoices.add((GSTR5) invoice);
				} else if (returntype.equals(GSTR6)) {
					existingInvoices.add((GSTR6) invoice);
				} else if (returntype.equals(MasterGSTConstants.EWAYBILL)) {
					existingInvoices.add((EWAYBILL) invoice);
				}else {
					existingInvoices.add((PurchaseRegister) invoice);
				}
			}
		}
		return existingInvoices;
	}

	private GSTRItems populateItem(InvoiceParent invoice, boolean isIntraState, String returntype,Item item,List<GSTRItems> gstrItems,List<GSTRImportItems> gstrimportitems,Map<String, String> hsnMap,Map<String, String> sacMap) {
		String invType = invoice.getInvtype();
			Double txvalue = 0d;
			if (invType.equals(MasterGSTConstants.ATPAID)) {
				if (isNotEmpty(item.getAdvadjustedAmount())) {
					txvalue = item.getAdvadjustedAmount();
				}
				GSTR1 inv = gstr1Repository.findByClientidAndInvtypeAndInvoiceno(invoice.getClientid(),	MasterGSTConstants.ADVANCES, item.getAdvReceiptNo());
				Double advRemainingAmount = item.getAdvAdjustableAmount() - item.getAdvadjustedAmount();
				inv.setAdvRemainingAmount(advRemainingAmount);
				gstr1Repository.save((GSTR1) inv);
			} else {
				if (isNotEmpty(item.getTaxablevalue())) {
					txvalue = item.getTaxablevalue();
				}
			}
			if (isNotEmpty(txvalue)) {
				GSTRItems gstrItem = new GSTRItems();
				GSTRItemDetails gstrItemDetail = new GSTRItemDetails();
				GSTRITCDetails gstrITCDetail = new GSTRITCDetails();
				if (isNotEmpty(item.getHsn())) {
					String code = null;
					String description = null;
					if (item.getHsn().contains(" : ")) {
						String hsncode[] = item.getHsn().split(" : ");
						code = hsncode[0];
						description = hsncode[1];
					} else {
						code = item.getHsn();
					}
					if (hsnMap.containsKey(code)) {
						item.setCategory(MasterGSTConstants.GOODS);
					} else if (hsnMap.containsValue(code)) {
						item.setCategory(MasterGSTConstants.GOODS);
					}else if (sacMap.containsKey(code)) {
						item.setCategory(MasterGSTConstants.SERVICES);
					} else if (sacMap.containsValue(code)) {
						item.setCategory(MasterGSTConstants.SERVICES);
					}else if (isEmpty(description)) {
						for (String key : sacMap.keySet()) {
							if (sacMap.get(key).endsWith(" : " + code)) {
								item.setCategory(MasterGSTConstants.SERVICES);
								break;
							}
						}
					}else if (isEmpty(description)) {
						for (String key : hsnMap.keySet()) {
							if (hsnMap.get(key).endsWith(" : " + code)) {
								item.setCategory(MasterGSTConstants.GOODS);
								break;
							}
						}
					}
					if (isEmpty(item.getUqc())) {
						item.setUqc("");
					}
					if (isEmpty(item.getRateperitem())) {
						item.setRateperitem(0d);
					}
					if (isEmpty(item.getDiscount())) {
						item.setDiscount(0d);
					}
					if (isEmpty(item.getQuantity())) {
						item.setQuantity(0d);
					}
					if (MasterGSTConstants.ADVANCES.equals(invoice.getInvtype())) {
						if (isNotEmpty(item.getTotal())) {
							item.setAdvreceived(item.getTotal());
						}
					}
				}
				if (isIntraState) {
					if (isNotEmpty(item.getCgstrate()) && item.getCgstrate() > 0 && isNotEmpty(item.getSgstrate()) && item.getSgstrate() > 0) {
						gstrItemDetail.setRt(item.getCgstrate() + item.getSgstrate());
						item.setRate(item.getCgstrate() + item.getSgstrate());
					} else if (isNotEmpty(item.getIgstrate()) && item.getIgstrate() > 0) {
						gstrItemDetail.setRt(item.getIgstrate());
						item.setRate(item.getIgstrate());
					} else if (isEmpty(gstrItemDetail.getRt()) && isNotEmpty(item.getRate()) && item.getRate() > 0) {
						gstrItemDetail.setRt(item.getRate());
						if (invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)|| (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) &&("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWPC".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWOP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())))) {
							item.setIgstrate(item.getRate());
						}else {
							if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
								item.setIgstrate(item.getRate());
							} else {
								item.setCgstrate(item.getRate() / 2);
								item.setSgstrate(item.getRate() / 2);
							}
						}
					} else {
						gstrItemDetail.setRt(0d);
						if (invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)|| (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) &&("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWPC".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWOP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())))) {
							item.setIgstrate(0d);
						}else {
							if(isIntraState) {
								item.setCgstrate(0d);
								item.setSgstrate(0d);
							}else {
								item.setIgstrate(0d);
							}
						}
					}
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())	&& !"R".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						if ("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
							gstrItemDetail.setCamt(item.getIgstamount() / 2);
							gstrItemDetail.setSamt(item.getIgstamount() / 2);
						} else if ("CWB".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
							gstrItemDetail.setIamt(item.getIgstamount());
						} else {
							gstrItemDetail.setCamt(item.getCgstamount());
							gstrItemDetail.setSamt(item.getSgstamount());
						}
					} else {
						if (!invoice.getInvtype().equals(MasterGSTConstants.ADVANCES)) {
							if (isNotEmpty(item.getCgstamount()) && item.getCgstamount() >= 0 && isNotEmpty(item.getSgstamount()) && item.getSgstamount() >= 0) {
								if (invoice.getInvtype().equals(EXPORTS)) {
									gstrItemDetail.setIamt(0d);
								} else {
									gstrItemDetail.setCamt(item.getCgstamount());
									gstrItemDetail.setSamt(item.getSgstamount());
								}
							} else if (isNotEmpty(item.getIgstamount())) {
								gstrItemDetail.setIamt(item.getIgstamount());
							}
						}else {
							if (isNotEmpty(item.getCgstamount())  && isNotEmpty(item.getSgstamount())) {
								gstrItemDetail.setCamt(item.getCgstamount());
								gstrItemDetail.setSamt(item.getSgstamount());
								
							} else if (isNotEmpty(item.getIgstamount())) {
								gstrItemDetail.setIamt(item.getIgstamount());
							}
						}
					}
					if (isNotEmpty(item.getCgstavltax())) {
						gstrITCDetail.setcTax(item.getCgstavltax());
					}
					if (isNotEmpty(item.getSgstavltax())) {
						gstrITCDetail.setsTax(item.getSgstavltax());
					}
				} else {
					if (isNotEmpty(item.getIgstrate()) && item.getIgstrate() > 0) {
						gstrItemDetail.setRt(item.getIgstrate());
						item.setRate(item.getIgstrate());
					} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())	&& item.getCgstrate() > 0) {
						gstrItemDetail.setRt(item.getCgstrate() + item.getSgstrate());
						item.setRate(item.getCgstrate() + item.getSgstrate());
					} else if (isEmpty(gstrItemDetail.getRt()) && isNotEmpty(item.getRate()) && item.getRate() > 0) {
						gstrItemDetail.setRt(item.getRate());
						if (invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)|| (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) &&("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWPC".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWOP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())))) {
							item.setIgstrate(item.getRate());
						}else {
							if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
								item.setIgstrate(item.getRate());
							} else {
								item.setCgstrate(item.getRate() / 2);
								item.setSgstrate(item.getRate() / 2);
							}
						}
					} else {
						gstrItemDetail.setRt(0d);
						if (invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)|| (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) &&("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWPC".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWOP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())))) {
							item.setIgstrate(0d);
						}else {
							if(isIntraState) {
								item.setCgstrate(0d);
								item.setSgstrate(0d);
							}else {
								item.setIgstrate(0d);
							}
						}
					}
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())	&& !"R".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						if ("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
							gstrItemDetail.setIamt(item.getIgstamount());
						} else if ("CWB".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
							gstrItemDetail.setIamt(item.getIgstamount());
						} else {
							gstrItemDetail.setIamt(item.getIgstamount());
						}
					} else {
						if (!invoice.getInvtype().equals(MasterGSTConstants.ADVANCES)) {
							if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() >= 0) {
								gstrItemDetail.setIamt(item.getIgstamount());
							} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
								if (invoice.getInvtype().equals(EXPORTS)) {
									gstrItemDetail.setIamt(0d);
								} else {
									gstrItemDetail.setCamt(item.getCgstamount());
									gstrItemDetail.setSamt(item.getSgstamount());
								}
							}
						}else {
							if (isNotEmpty(item.getIgstamount())) {
								gstrItemDetail.setIamt(item.getIgstamount());
							} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
								gstrItemDetail.setCamt(item.getCgstamount());
								gstrItemDetail.setSamt(item.getSgstamount());
								
							}
						}
					}
					if (isNotEmpty(item.getIgstavltax())) {
						gstrITCDetail.setiTax(item.getIgstavltax());
					}
				}
				if (isNotEmpty(item.getCessamount())) {
					gstrItemDetail.setCsamt(item.getCessamount());
				}
				if (isNotEmpty(item.getCessavltax())) {
					gstrITCDetail.setCsTax(item.getCessavltax());
				}
				if (isNotEmpty(item.getElg())) {
					if (item.getElg().startsWith("Input")) {
						item.setElg("ip");
					} else if (item.getElg().startsWith("Capital")) {
						item.setElg("cp");
					} else if (item.getElg().startsWith("In")) {
						item.setElg("is");
					} else if (item.getElg().startsWith("Not")) {
						item.setElg("no");
					}
					gstrITCDetail.setElg(item.getElg());
				}
				if (isNotEmpty(item.getTaxablevalue())) {
					gstrItemDetail.setTxval(item.getTaxablevalue());
				}
				if (isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equals(ATPAID) || invoice.getInvtype().equals(ADVANCES) || invoice.getInvtype().equals(MasterGSTConstants.ATA) || invoice.getInvtype().equals(MasterGSTConstants.TXPA))) {
						if (isNotEmpty(item.getTaxablevalue())) {
							gstrItemDetail.setAdvAmt(item.getTaxablevalue());
						}
				}
				gstrItem.setItem(gstrItemDetail);
				if (returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
					if (isNotEmpty(gstrITCDetail.getElg())) {
						gstrItem.setItc(gstrITCDetail);
					}
				}
				if(isNotEmpty(gstrItems)) {
					gstrItem.setNum(gstrItems.size() + 1);
				}else if(isNotEmpty(gstrimportitems)){
					gstrItem.setNum(gstrimportitems.size() + 1);
				}
				return gstrItem; 
			}
		return null;
	}
	private Double totalAmounts(InvoiceParent exstngInv,GSTRItems gstritem,InvoiceParent invoice,String returntype,OtherConfigurations otherconfig) {
		Double totalTax = 0d;
		if(isNotEmpty(exstngInv.getTotaltax())) {
			totalTax = exstngInv.getTotaltax();
		}
		Double totalITC = 0d;
		if(isNotEmpty(exstngInv.getTotalitc())) {
			totalITC = exstngInv.getTotalitc();
		}
		Double totalAmount = 0d;
		if(isNotEmpty(exstngInv.getTotalamount())) {
			totalAmount = exstngInv.getTotalamount();
		}
		Double totalCurrencyAmount = 0d;
		if(isNotEmpty(exstngInv.getTotalCurrencyAmount())) {
			totalCurrencyAmount = exstngInv.getTotalCurrencyAmount();
		}
		Double totalTaxableAmt = 0d;
		if(isNotEmpty(exstngInv.getTotaltaxableamount())) {
			totalTaxableAmt = exstngInv.getTotaltaxableamount();
		}
		Double totalIGST = 0d;
		Double totalCGST = 0d;
		Double totalSGST = 0d;
		Double totalExempted = 0d;
		Double totalCessAmount = 0d;
		
		if(isNotEmpty(exstngInv.getTotalIgstAmount())) {
			totalIGST = exstngInv.getTotalIgstAmount();
		}
		if(isNotEmpty(exstngInv.getTotalCgstAmount())) {
			totalCGST = exstngInv.getTotalCgstAmount();
		}
		if(isNotEmpty(exstngInv.getTotalSgstAmount())) {
			totalSGST = exstngInv.getTotalSgstAmount();
		}
		if(isNotEmpty(exstngInv.getTotalExemptedAmount())) {
			totalExempted = exstngInv.getTotalExemptedAmount();
		}
		if(isNotEmpty(exstngInv.getTotalCessAmount())) {
			totalCessAmount = exstngInv.getTotalCessAmount();
		}
		
		if (isNotEmpty(gstritem.getItem().getCamt())) {
			totalTax += gstritem.getItem().getCamt();
			totalCGST += gstritem.getItem().getCamt();
		}
		if (isNotEmpty(gstritem.getItem().getIamt())) {
			totalTax += gstritem.getItem().getIamt();
			totalIGST += gstritem.getItem().getIamt();
		}
		if (isNotEmpty(gstritem.getItem().getSamt())) {
			totalTax += gstritem.getItem().getSamt();
			totalSGST += gstritem.getItem().getSamt();
		}
		if (isNotEmpty(gstritem.getItem().getCsamt())) {
			totalTax += gstritem.getItem().getCsamt();
			totalCessAmount += gstritem.getItem().getCsamt();
		}
		if (isNotEmpty(invoice.getItems().get(0).getExmepted())) {
			totalExempted += invoice.getItems().get(0).getExmepted();
		}
		if (isNotEmpty(gstritem.getItc())){
			if (isNotEmpty(gstritem.getItc().getiTax())) {
				totalITC += gstritem.getItc().getiTax();
			}
			if (isNotEmpty(gstritem.getItc().getcTax())) {
				totalITC += gstritem.getItc().getcTax();
			}
			if (isNotEmpty(gstritem.getItc().getsTax())) {
				totalITC += gstritem.getItc().getsTax();
			}
			if (isNotEmpty(gstritem.getItc().getCsTax())) {
				totalITC += gstritem.getItc().getCsTax();
			}
		}
		if (isNotEmpty(invoice.getItems().get(0).getTotal())) {
			totalAmount += invoice.getItems().get(0).getTotal();
		}
		if (isNotEmpty(invoice.getItems().get(0).getCurrencytotalAmount())) {
			totalCurrencyAmount += invoice.getItems().get(0).getCurrencytotalAmount();
		}
		if (isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
			totalTaxableAmt += invoice.getItems().get(0).getTaxablevalue();
		}
		if (isNotEmpty(invoice.getItems().get(0).getAdditionalchargevalue())) {
			totalTaxableAmt += invoice.getItems().get(0).getAdditionalchargevalue();
		}
		exstngInv.setTotalIgstAmount(totalIGST);
		exstngInv.setTotalCgstAmount(totalCGST);
		exstngInv.setTotalSgstAmount(totalSGST);
		exstngInv.setTotaltax(totalTax);
		exstngInv.setTotalExemptedAmount(totalExempted);
		exstngInv.setTotalCessAmount(totalCessAmount);
		exstngInv.setTotalamount(totalAmount);
		exstngInv.setTotaltaxableamount(totalTaxableAmt);
		exstngInv.setTotalitc(totalITC);
		exstngInv.setTotalCurrencyAmount(totalCurrencyAmount);
		savetcstdsamount(returntype,exstngInv);
		if (returntype.equals(GSTR1)) {
			if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffSalesField()) && !otherconfig.isEnableroundoffSalesField()) {
				totalAmount = (double) Math.round(totalAmount);
				if(isNotEmpty(exstngInv.getTotalamount())) {
					Double notroundofftamnt = exstngInv.getTotalamount();
					Double roundofftamt = (double) Math.round(notroundofftamnt);
					Double roundoffamt = 0d;
					roundoffamt = roundofftamt - notroundofftamnt;
					exstngInv.setTotalamount(roundofftamt);
					exstngInv.setNotroundoftotalamount(notroundofftamnt);
					exstngInv.setRoundOffAmount(Double.parseDouble(df2.format(roundoffamt)));
					String totalAmtStr = String.format(DOUBLE_FORMAT,roundofftamt);
					exstngInv.setTotalamount_str(totalAmtStr);
				}
			}
		}else {
			if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
				totalAmount = (double) Math.round(totalAmount);
				if(isNotEmpty(exstngInv.getTotalamount())) {
					Double notroundofftamnt = exstngInv.getTotalamount();
					Double roundofftamt = (double) Math.round(notroundofftamnt);
					Double roundoffamt = 0d;
					roundoffamt = roundofftamt - notroundofftamnt;
					exstngInv.setTotalamount(roundofftamt);
					exstngInv.setNotroundoftotalamount(notroundofftamnt);
					exstngInv.setRoundOffAmount(Double.parseDouble(df2.format(roundoffamt)));
					String totalAmtStr = String.format(DOUBLE_FORMAT,roundofftamt);
					exstngInv.setTotalamount_str(totalAmtStr);
				}
			}
		}
		if(isNotEmpty(exstngInv.getTotaltax())) {
			exstngInv.setTotaltax_str(String.format(DOUBLE_FORMAT,exstngInv.getTotaltax()));
		}
		if(isNotEmpty(exstngInv.getTotaltaxableamount())) {
			exstngInv.setTotaltaxableamount_str(String.format(DOUBLE_FORMAT,exstngInv.getTotaltaxableamount()));
		}
		if(isNotEmpty(exstngInv.getTotalamount())) {
			exstngInv.setTotalamount_str(String.format(DOUBLE_FORMAT,exstngInv.getTotalamount()));
		}
		if(isEmpty(exstngInv.getDueDate()) && isNotEmpty(exstngInv.getDateofinvoice())) {
			exstngInv.setDueDate(exstngInv.getDateofinvoice());
			exstngInv.setTermDays("0");
		}
		if(isNotEmpty(exstngInv.getTotalamount())) {
			exstngInv.setPendingAmount(exstngInv.getTotalamount());
			exstngInv.setReceivedAmount(0d);
		}
		
		
		return totalAmount;
	}
	private void addAdditionalData(InvoiceParent invoice, final String returntype, final boolean isIntraState,Map<String,StateConfig> stateconfig,Map<String,String> hsnMap,Map<String,String> sacMap,Map<String,StateConfig> statetin, List<InvoicePendingActions> invoicePendingActions) {
		if (isNotEmpty(invoice.getBilledtoname())) {
			ProfileLedger ldgr = ledgerRepository.findByClientidAndLedgerNameIgnoreCase(invoice.getClientid(),invoice.getBilledtoname());
			if (returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER)
					|| returntype.equals("PurchaseRegister")) {
				CompanySuppliers companysupplier = companySuppliersRepository.findByNameAndClientid(invoice.getBilledtoname(), invoice.getClientid());
				if (isEmpty(companysupplier)) {
					invoice.setVendorName(invoice.getBilledtoname());
					companysupplier = new CompanySuppliers();
					companysupplier.setName(invoice.getBilledtoname());
					companysupplier.setSupplierLedgerName(invoice.getBilledtoname());
					companysupplier.setClientid(invoice.getClientid());
					companysupplier.setUserid(invoice.getUserid());
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0))) {
						if (isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0))) {
							if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
								String address = invoice.getB2b().get(0).getInv().get(0).getAddress();
								if (address.length() == 1 && address.equals(",")) {
								} else {
									companysupplier.setAddress(address);
								}
							}
						}
						if (isNotEmpty(invoice.getB2b().get(0).getCtin())) {
							companysupplier.setGstnnumber(invoice.getB2b().get(0).getCtin().toUpperCase().trim());
							String statename = invoice.getB2b().get(0).getCtin().toUpperCase().trim().substring(0, 2);
							StateConfig state = statetin.get(statename);
							if(isNotEmpty(state)) {
								statename = state.getName();
							}
							companysupplier.setState(statename);
						}
					}
					profileService.saveSupplier(companysupplier);
				}
				if (isEmpty(ldgr)) {
					ProfileLedger lgrdr = new ProfileLedger();
					lgrdr.setClientid(invoice.getClientid());
					if (isNotEmpty(invoice.getBilledtoname())) {
						lgrdr.setLedgerName(invoice.getBilledtoname());
					}
					lgrdr.setGrpsubgrpName("Sundry Creditors");
					lgrdr.setLedgerpath("Liabilities/Current liabilities/Sundry Creditors");
					ledgerRepository.save(lgrdr);
				}
			} else {
				CompanyCustomers companycustomer = companyCustomersRepository.findByNameAndClientid(invoice.getBilledtoname(), invoice.getClientid());
				if (isEmpty(companycustomer)) {
					invoice.setVendorName(invoice.getBilledtoname());
					companycustomer = new CompanyCustomers();
					companycustomer.setName(invoice.getBilledtoname());
					companycustomer.setCustomerLedgerName(invoice.getBilledtoname());
					companycustomer.setClientid(invoice.getClientid());
					companycustomer.setUserid(invoice.getUserid());
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0))) {
						if (isNotEmpty(invoice.getB2b().get(0).getInv())
								&& isNotEmpty(invoice.getB2b().get(0).getInv().get(0))) {
							if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
								String address = invoice.getB2b().get(0).getInv().get(0).getAddress();
								if (address.length() == 1 && address.equals(",")) {
								} else {
									companycustomer.setAddress(address);
								}
							}
						}
						if (isNotEmpty(invoice.getB2b().get(0).getCtin())) {
							companycustomer.setGstnnumber(invoice.getB2b().get(0).getCtin().toUpperCase().trim());
							String statename = invoice.getB2b().get(0).getCtin().toUpperCase().trim().substring(0, 2);
							StateConfig state = statetin.get(statename);
							if(isNotEmpty(state)) {
								statename = state.getName();
							}
							companycustomer.setState(statename);
						}
					}
					profileService.saveCustomer(companycustomer);
				}
				if (isEmpty(ldgr)) {
					ProfileLedger lgrdr = new ProfileLedger();
					lgrdr.setClientid(invoice.getClientid());
					if (isNotEmpty(invoice.getBilledtoname())) {
						lgrdr.setLedgerName(invoice.getBilledtoname());
					}
					lgrdr.setGrpsubgrpName(AccountConstants.SUNDRY_DEBTORS);
					lgrdr.setLedgerpath("Assets/Current Assets/Sundry Debtors");
					lgrdr.setLedgerOpeningBalance(0);
					ledgerRepository.save(lgrdr);
				}
			}
		}
		if (isNotEmpty(invoice.getItems())) {
			for (Item nItem : invoice.getItems()) {
				if (isNotEmpty(nItem.getLedgerName())) {
					ProfileLedger ldgr = ledgerRepository.findByClientidAndLedgerNameIgnoreCase(invoice.getClientid(), nItem.getLedgerName());
					if (returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER) || returntype.equals("PurchaseRegister")) {
						if (isEmpty(ldgr)) {
							ProfileLedger lgrdr = new ProfileLedger();
							lgrdr.setClientid(invoice.getClientid());
							if (isNotEmpty(nItem.getLedgerName())) {
								lgrdr.setLedgerName(nItem.getLedgerName());
							}
							if (MasterGSTConstants.ADVANCES.equalsIgnoreCase(invoice.getInvtype())) {
								lgrdr.setGrpsubgrpName("Cash in hand'");
								lgrdr.setLedgerpath("Assets/Current Assets/Cash in hand");
							} else if (MasterGSTConstants.ISD.equalsIgnoreCase(invoice.getInvtype())) {
								lgrdr.setGrpsubgrpName("Output Tax");
								lgrdr.setLedgerpath("Liabilities/Current liabilities/Duties & Taxes - Liabilities/Output Tax");
							} else {
								lgrdr.setGrpsubgrpName("Expenditure");
								lgrdr.setLedgerpath("Expenditure/Expenditure");
							}
							ledgerRepository.save(lgrdr);
						}
					} else {
						if (isEmpty(ldgr)) {
							ProfileLedger lgrdr = new ProfileLedger();
							lgrdr.setClientid(invoice.getClientid());
							if (isNotEmpty(nItem.getLedgerName())) {
								lgrdr.setLedgerName(nItem.getLedgerName());
							}
							/*if (MasterGSTConstants.ADVANCES.equalsIgnoreCase(invoice.getInvtype())) {
								lgrdr.setGrpsubgrpName("Cash in hand'");
								lgrdr.setLedgerpath("Assets/Current Assets/Cash in hand");
							} else {
								lgrdr.setGrpsubgrpName(AccountConstants.SALES);
								lgrdr.setLedgerpath(AccountConstants.SALES_DEFAULT_PATH);
							}*/
							lgrdr.setGrpsubgrpName(AccountConstants.SALES);
							lgrdr.setLedgerpath(AccountConstants.SALES_DEFAULT_PATH);
							lgrdr.setLedgerOpeningBalance(0);
							ledgerRepository.save(lgrdr);
						}
					}
				}
			}
		}
	}
	private InvoiceParent populateInvoiceInfo(InvoiceParent invoice, final String returntype, final boolean isIntraState,Map<String,StateConfig> stateconfig,Map<String,String> hsnMap,Map<String,String> sacMap,Map<String,StateConfig> statetin, List<InvoicePendingActions> invoicePendingActions) {
		 addAdditionalData(invoice, returntype, isIntraState, stateconfig, hsnMap, sacMap, statetin, invoicePendingActions);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Double totalIGST = 0d;
		Double totalCGST = 0d;
		Double totalSGST = 0d;
		Double totalExempted = 0d;
		Double totalAssAmt = 0d;
		Double totalStateCessAmt = 0d;
		Double totalCessNonAdVal = 0d;
		Double totalCessAmount = 0d;
		Double toralDiscAmount = 0d;
		Double totalOthrChrgAmount  = 0d;
		if (isNotEmpty(invoice.getB2b().get(0).getCtin())) {
			invoice.getB2b().get(0).setCtin(invoice.getB2b().get(0).getCtin().toUpperCase().trim());
			 if (isNotEmpty(invoice.getDealerType())) { 
				 GSTINPublicData gstndata =	 gstinPublicDataRepository.findByGstin(invoice.getB2b().get(0).getCtin().toUpperCase().trim()); 
				 if(isNotEmpty(gstndata)) { 
					 if(isNotEmpty(gstndata.getDty())) {
						 invoice.setDealerType(gstndata.getDty());
					 }
				 }else { 
					Response response = iHubConsumerService.publicSearch(invoice.getB2b().get(0).getCtin().toUpperCase().trim()); 
					if (isNotEmpty(response) && isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
						 ResponseData data = response.getData(); 
						 if (isNotEmpty(data.getDty())) {
							 invoice.setDealerType(data.getDty()); 
						 }
					}
				 }
			 }
		}
		String ecomtin = "";
		if (isNotEmpty(invoice.getB2cs()) && isNotEmpty(invoice.getB2cs().get(0))
				&& isNotEmpty(invoice.getB2cs().get(0).getEtin())) {
			ecomtin = invoice.getB2cs().get(0).getEtin();
			invoice.setInvoiceEcomGSTIN(ecomtin.trim());
		}
		if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0))
				&& isNotEmpty(invoice.getB2b().get(0).getInv())
				&& isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
			String address = invoice.getB2b().get(0).getInv().get(0).getAddress();
			if (address.length() == 1 && address.equals(",")) {
				invoice.getB2b().get(0).getInv().get(0).setAddress("");
			}
		}
		List<GSTRItems> gstrItems = Lists.newArrayList();
		String statename = "";
		if(isNotEmpty(invoice.getStatename())) {
			statename = invoice.getStatename();
		}
		if (statename.contains("-")) {
			statename = statename.substring(statename.indexOf("-")+1).trim();
		}
		StateConfig state = stateconfig.get(statename.replaceAll("\\s", "").toLowerCase());
		String stateTin = "";
		if(isNotEmpty(state)) {
			Integer tin = state.getTin();
			stateTin = (tin < 10) ? ("0" + tin) : (tin + "");
		}
		if (isNotEmpty(invoice.getItems())) {
			if(isNotEmpty(invoice.getInvtype())) {
				for (Item nItem : invoice.getItems()) {
					if(isEmpty(nItem.getLedgerName())) {
						if(invoice.getInvtype().equals(MasterGSTConstants.ADVANCES)) {
							nItem.setLedgerName(AccountConstants.CASH);
						}else {
							if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
								nItem.setLedgerName(AccountConstants.PURCHASES);
							}else {
								nItem.setLedgerName(AccountConstants.SALES);
							}
						}
					}
				}
			}
			if (isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getInvtype())
					&& invoice.getInvtype().equals(MasterGSTConstants.NIL)) {
				for (Item nItem : invoice.getItems()) {
					if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
						if (isNotEmpty(nItem.getType())) {
							PurchaseRegister pinvoice = (PurchaseRegister) (invoice);
							if (isEmpty(pinvoice.getNilSupplies())) {
								pinvoice.setNilSupplies(new GSTRNilSupplies());
							}
							if (isIntraState) {
								if (isEmpty(pinvoice.getNilSupplies().getIntra())) {
									pinvoice.getNilSupplies().setIntra(new GSTRNilSupItems());
								}
								populateNilItems(nItem, pinvoice.getNilSupplies().getIntra());
							} else {
								if (isEmpty(pinvoice.getNilSupplies().getInter())) {
									pinvoice.getNilSupplies().setInter(new GSTRNilSupItems());
								}
								populateNilItems(nItem, pinvoice.getNilSupplies().getInter());
							}
						}
					} else {
						String supplyType = null;
						if (isIntraState) {
							if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								supplyType = MasterGSTConstants.SUPPLY_TYPE_INTRA + B2B;
							} else {
								supplyType = MasterGSTConstants.SUPPLY_TYPE_INTRA + B2C;
							}
						} else {
							if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								supplyType = "INTR" + B2B;
							} else {
								supplyType = "INTR" + B2C;
							}
						}
						if(!(MasterGSTConstants.TALLY_TEMPLATE+"-"+MasterGSTConstants.NIL).equals(invoice.getGenerateMode())) {
							if (isNotEmpty(nItem.getType()) && isNotEmpty(supplyType)) {
								if (isNotEmpty(invoice.getNil()) && isNotEmpty(invoice.getNil().getInv())) {
									boolean present = false;
									for (GSTRNilItems eItem : invoice.getNil().getInv()) {
										if (isNotEmpty(eItem.getSplyType()) && eItem.getSplyType().equals(supplyType)) {
											present = true;
											populateNilItems(nItem, eItem);
											break;
										}
									}
									if (!present) {
										GSTRNilItems eItem = new GSTRNilItems();
										eItem.setSplyType(supplyType);
										populateNilItems(nItem, eItem);
										invoice.getNil().getInv().add(eItem);
									}
								} else {
									GSTRNilInvoices nil = new GSTRNilInvoices();
									List<GSTRNilItems> nilItems = Lists.newArrayList();
									GSTRNilItems eItem = new GSTRNilItems();
									eItem.setSplyType(supplyType);
									populateNilItems(nItem, eItem);
									nilItems.add(eItem);
									nil.setInv(nilItems);
									invoice.setNil(nil);
								}
							}
						}else {
							if(isNotEmpty(invoice.getGenerateMode())){
								if((MasterGSTConstants.TALLY_TEMPLATE+"-"+MasterGSTConstants.NIL).equals(invoice.getGenerateMode())) {
									if (isNotEmpty(invoice.getNil().getInv().get(0).getSplyType())) {
										invoice.setGenerateMode(MasterGSTConstants.TALLY_TEMPLATE+"-"+MasterGSTConstants.NIL);
										String type = invoice.getNil().getInv().get(0).getSplyType();
										GSTRNilItems eItem = new GSTRNilItems();
										eItem.setSplyType("");
										populateNilItems(nItem, eItem);
									}
								}
							}
						}
					}
				}
			}
			if(isNotEmpty(invoice.getGenerateMode())){
				if((MasterGSTConstants.TALLY_TEMPLATE+"-"+MasterGSTConstants.NIL).equals(invoice.getGenerateMode())) {
					invoice.setGenerateMode(null);
				}
			}
			if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
				PurchaseRegister pinvoice = (PurchaseRegister) (invoice);
				if (isNotEmpty(pinvoice) && isNotEmpty(pinvoice.getItems()) && isNotEmpty(pinvoice.getInvtype()) && pinvoice.getInvtype().equals(MasterGSTConstants.ISD)) {
					pinvoice.getIsd().get(0).setCtin(pinvoice.getB2b().get(0).getCtin());
					List<GSTRDocListDetails> doclist = getIsdItem(pinvoice);
					pinvoice.getIsd().get(0).setDoclist(doclist);
				}
				invoice = pinvoice;
			}
			if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
				PurchaseRegister pinvoice = (PurchaseRegister) (invoice);
				if (isNotEmpty(pinvoice.getItems()) && isNotEmpty(pinvoice.getInvtype()) && pinvoice.getInvtype().equals(MasterGSTConstants.ITC_REVERSAL)) {
					for (Item pitem : pinvoice.getItems()) {
						GSTRItemDetails item = getReversalItemByType(pinvoice, pitem.getItcRevtype());
						if (item != null) {
							if (isNotEmpty(item.getIamt())) {
								Double iamt = item.getIamt() + pitem.getIgstamount();
								item.setIamt(iamt);
							} else {
								item.setIamt(pitem.getIgstamount());
							}
							if (isNotEmpty(item.getCamt())) {
								Double camt = item.getCamt() + pitem.getCgstamount();
								item.setCamt(camt);
							} else {
								item.setCamt(pitem.getCgstamount());
							}
							if (isNotEmpty(item.getSamt())) {
								Double samt = item.getSamt() + pitem.getSgstamount();
								item.setSamt(samt);
							} else {
								item.setSamt(pitem.getSgstamount());
							}
							if (isNotEmpty(item.getCsamt())) {
								Double cessamt = item.getCsamt() + pitem.getIsdcessamount();
								item.setCsamt(cessamt);
							} else {
								item.setCsamt(pitem.getIsdcessamount());
							}
						}
					}
				}
				invoice = pinvoice;
			}
			if (isEmpty(invoice.getInvtype())) {
				List<Item> fItems = Lists.newArrayList();
				for (Item item : invoice.getItems()) {
					if (isEmpty(item.getQuantity())) {
						item.setQuantity(1d);
					}
					if (isEmpty(item.getRateperitem())) {
						if (isNotEmpty(item.getTaxablevalue())) {
							item.setRateperitem(item.getTaxablevalue());
						}
					}
					if(isNotEmpty(invoice.getGstr1orEinvoice()) && "Einvoice".equalsIgnoreCase(invoice.getGstr1orEinvoice())) {
						if(isNotEmpty(item.getTaxablevalue())) {
							item.setAssAmt(item.getTaxablevalue());
						}
					}
					if (isEmpty(item.getCategory())) {
						if (isNotEmpty(item.getHsn()) && item.getHsn().contains(" : ")) {
							item.setCategory(MasterGSTConstants.GOODS);
						} else {
							item.setCategory(MasterGSTConstants.SERVICES);
						}
					}
					if ((isNotEmpty(item.getIgstrate()) || isNotEmpty(item.getCgstrate())) || (isNotEmpty(item.getHsn()) || isNotEmpty(item.getSac()) || isNotEmpty(item.getRate()))) {
						if (isNotEmpty(item.getRate()) && item.getRate() > 0) {
							if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0 && isEmpty(item.getIgstrate())) {
								item.setIgstrate(item.getRate());
							} else if (isNotEmpty(item.getCgstamount()) && item.getCgstamount() > 0
									&& isNotEmpty(item.getSgstamount()) && isEmpty(item.getCgstrate())
									&& isEmpty(item.getSgstrate())) {
								item.setCgstrate(item.getRate() / 2);
								item.setSgstrate(item.getRate() / 2);
							}
						}else {
							if(isIntraState) {
								item.setCgstrate(item.getRate() / 2);
								item.setSgstrate(item.getRate() / 2);
							}else {
								item.setIgstrate(item.getRate());
							}
						}
						fItems.add(item);
					}
				}
				invoice.setItems(fItems);
			}
			if (!invoice.getInvtype().equals(MasterGSTConstants.ATPAID) || !invoice.getInvtype().equals(MasterGSTConstants.TXPA)) {
				if (invoice.getInvtype().equals(MasterGSTConstants.IMP_GOODS) || invoice.getInvtype().equals(MasterGSTConstants.IMP_SERVICES)) {
					gstrItems = populateItemData(invoice, false, returntype,hsnMap,sacMap);
				}else {
					gstrItems = populateItemData(invoice, isIntraState, returntype,hsnMap,sacMap);
				}
			}
		}
		if (isNotEmpty(invoice.getRevchargetype()) && !invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_TCS)) {
			if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
				invoice.getB2b().get(0).getInv().get(0).setEtin("");
				invoice.setEcomoperatorid("");
			}
		}
		if (isEmpty(invoice.getInvoiceno()) && isNotEmpty(invoice.getB2b())
				&& isNotEmpty(invoice.getB2b().get(0).getInv())
				&& isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
			invoice.setInvoiceno(invoice.getB2b().get(0).getInv().get(0).getInum());
		}
		if (isEmpty(invoice.getInvtype())) {
			invoice.setExp(Lists.newArrayList());
			invoice.setTxpd(Lists.newArrayList());
			invoice.setCdn(Lists.newArrayList());
			invoice.setCdnur(Lists.newArrayList());
			if (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())) {
				if (invoice.getTotalamount() > 250000 && !isIntraState) {
					if(invoice.getInvtype().endsWith("A")) {
						invoice.setInvtype(B2CLA);
					}else {
						invoice.setInvtype(B2CL);						
					}
					if (isEmpty(invoice.getB2cl())) {
						List<GSTRB2CL> b2cl = Lists.newArrayList();
						GSTRB2CL gstrb2clInvoice = new GSTRB2CL();
						List<GSTRInvoiceDetails> inv = Lists.newArrayList();
						GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
						List<GSTRItems> itms = Lists.newArrayList();
						GSTRItems gstrItem = new GSTRItems();
						gstrItem.setNum(1);
						itms.add(gstrItem);
						gstrInvoiceDetail.setItms(itms);
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							gstrInvoiceDetail.setDiffPercent(0.65);
						}
						inv.add(gstrInvoiceDetail);
						gstrb2clInvoice.setInv(inv);
						b2cl.add(gstrb2clInvoice);
						invoice.setB2cl(b2cl);
					} else if (isEmpty(invoice.getB2cl().get(0).getInv())) {
						List<GSTRInvoiceDetails> inv = Lists.newArrayList();
						GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							gstrInvoiceDetail.setDiffPercent(0.65);
						}
						List<GSTRItems> itms = Lists.newArrayList();
						GSTRItems gstrItem = new GSTRItems();
						gstrItem.setNum(1);
						itms.add(gstrItem);
						gstrInvoiceDetail.setItms(itms);
						inv.add(gstrInvoiceDetail);
						invoice.getB2cl().get(0).setInv(inv);
					}
					if (isNotEmpty(invoice.getDateofinvoice())) {
						invoice.getB2cl().get(0).getInv().get(0)
								.setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
						if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
							invoice.getB2cl().get(0).getInv().get(0)
									.setInum(invoice.getB2b().get(0).getInv().get(0).getInum().toUpperCase());
						}
						if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
							invoice.getB2cl().get(0).getInv().get(0)
									.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
						}
					}
					if (isNotEmpty(ecomtin)) {
						invoice.getB2cl().get(0).getInv().get(0).setEtin(ecomtin);
					}
					invoice.getB2cl().get(0).getInv().get(0).setVal(invoice.getTotalamount());
					if (isNotEmpty(invoice.getStatename())) {
						invoice.getB2cl().get(0).setPos(stateTin);
					}
					String diffper = invoice.getDiffPercent();
					if ("Yes".equals(diffper)) {
						invoice.getB2cl().get(0).getInv().get(0).setDiffPercent(0.65);
					}
					if (isNotEmpty(gstrItems)) {
						for (GSTRItems gstrItem : gstrItems) {
							if (isNotEmpty(gstrItem.getItem()) && isNotEmpty(gstrItem.getItem().getAdvAmt())) {
								gstrItem.getItem().setAdvAmt(null);
							}
						}
					}
					invoice.getB2cl().get(0).getInv().get(0).setItms(gstrItems);
					if(invoice.getInvtype().equals(B2CLA)) {
						((GSTR1)invoice).setB2cla(invoice.getB2cl());
					}
				} else {
					if(invoice.getInvtype().endsWith("A")) {
						invoice.setInvtype(B2CSA);
					}else {
						invoice.setInvtype(B2C);
					}
					
					if (isEmpty(invoice.getB2cs())) {
						List<GSTRB2CS> b2cs = Lists.newArrayList();
						invoice.setB2cs(b2cs);
					}
					List<GSTRB2CS> b2cs = Lists.newArrayList();
					String b2cskey="";
					List<GSTRB2CS> newInvs = Lists.newArrayList();
					List<String> b2cskeys = Lists.newArrayList();
					Map<String,GSTRB2CS> newb2csmap = Maps.newHashMap();
					for (Item item : invoice.getItems()) {
						GSTRB2CS gstrb2csDetail = new GSTRB2CS();
						if (isNotEmpty(item.getIgstrate())) {
							gstrb2csDetail.setRt(item.getIgstrate());
						} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())) {
							gstrb2csDetail.setRt(item.getCgstrate() + item.getSgstrate());
						}
						if (!isIntraState && isNotEmpty(item.getIgstamount())) {
							gstrb2csDetail.setIamt(item.getIgstamount());
							// gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTER);
						} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
							gstrb2csDetail.setCamt(item.getCgstamount());
							gstrb2csDetail.setSamt(item.getSgstamount());
						}
						if (isIntraState) {
							gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTRA);
						} else {
							gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTER);
						}
						gstrb2csDetail.setCsamt(item.getCessamount());
						gstrb2csDetail.setTxval(item.getTaxablevalue());
						if (isNotEmpty(invoice.getStatename())) {
							gstrb2csDetail.setPos(stateTin);
						}
						gstrb2csDetail.setTyp("OE");
						if (isNotEmpty(ecomtin)) {
							gstrb2csDetail.setTyp("E");
							gstrb2csDetail.setEtin(ecomtin.toUpperCase());
						} else if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
							gstrb2csDetail.setTyp("E");
							gstrb2csDetail.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
						}
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							gstrb2csDetail.setDiffPercent(0.65);
						}
//						b2cs.add(gstrb2csDetail);
						b2cskey = gstrb2csDetail.getPos()+"-"+gstrb2csDetail.getRt();
						if(!newb2csmap.containsKey(b2cskey)) {
							newb2csmap.put(b2cskey, gstrb2csDetail);
							b2cskeys.add(b2cskey);
						}else {
							GSTRB2CS b2c = newb2csmap.get(b2cskey);
							if (isNotEmpty(gstrb2csDetail.getTxval())) {
								if (isNotEmpty(b2c.getTxval())) {
									b2c.setTxval(b2c.getTxval() + gstrb2csDetail.getTxval());
								} else {
									b2c.setTxval(gstrb2csDetail.getTxval());
								}
							}
							if (isNotEmpty(gstrb2csDetail.getIamt())) {
								if (isNotEmpty(b2c.getIamt())) {
									b2c.setIamt(b2c.getIamt() + gstrb2csDetail.getIamt());
								} else {
									b2c.setIamt(gstrb2csDetail.getIamt());
								}
							}
							if (isNotEmpty(gstrb2csDetail.getCamt())) {
								if (isNotEmpty(b2c.getCamt())) {
									b2c.setCamt(b2c.getCamt() + gstrb2csDetail.getCamt());
								} else {
									b2c.setCamt(gstrb2csDetail.getCamt());
								}
							}
							if (isNotEmpty(gstrb2csDetail.getSamt())) {
								if (isNotEmpty(b2c.getSamt())) {
									b2c.setSamt(b2c.getSamt() + gstrb2csDetail.getSamt());
								} else {
									b2c.setSamt(gstrb2csDetail.getSamt());
								}
							}
							if (isNotEmpty(gstrb2csDetail.getCsamt())) {
								if (isNotEmpty(b2c.getCsamt())) {
									b2c.setCsamt(b2c.getCsamt() + gstrb2csDetail.getCsamt());
								} else {
									b2c.setCsamt(gstrb2csDetail.getCsamt());
								}
							}
							newb2csmap.put(b2cskey, b2c);
						}
					}
					for(String key : b2cskeys) {
						GSTRB2CS b2c = newb2csmap.get(key);
						newInvs.add(b2c);
					}
					invoice.setB2cs(newInvs);
					if(invoice.getInvtype().equals(B2CSA)) {
						invoice.setB2cs(newInvs);
						
					}
				}
			} else {
				if(invoice.getInvtype().endsWith("A")) {
					invoice.setInvtype(B2BA);
				}else {
					invoice.setInvtype(B2B);
				}
				if (isEmpty(invoice.getB2b().get(0).getInv())) {
					List<GSTRInvoiceDetails> inv = Lists.newArrayList();
					GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
					List<GSTRItems> itms = Lists.newArrayList();
					GSTRItems gstrItem = new GSTRItems();
					gstrItem.setNum(1);
					itms.add(gstrItem);
					gstrInvoiceDetail.setItms(itms);
					String diffper = invoice.getDiffPercent();
					if ("Yes".equals(diffper)) {
						gstrInvoiceDetail.setDiffPercent(0.65);
					}
					inv.add(gstrInvoiceDetail);
					invoice.getB2b().get(0).setInv(inv);
				} else if (isEmpty(invoice.getB2b().get(0).getInv().get(0).getItms())) {
					List<GSTRItems> itms = Lists.newArrayList();
					GSTRItems gstrItem = new GSTRItems();
					gstrItem.setNum(1);
					itms.add(gstrItem);
					invoice.getB2b().get(0).getInv().get(0).setItms(itms);
				}
				if (isNotEmpty(ecomtin)) {
					invoice.getB2b().get(0).getInv().get(0).setEtin(ecomtin);
				}
				String diffper = invoice.getDiffPercent();
				if ("Yes".equals(diffper)) {
					invoice.getB2b().get(0).getInv().get(0).setDiffPercent(0.65);
				}
				if (isNotEmpty(invoice.getDateofinvoice())) {
					invoice.getB2b().get(0).getInv().get(0).setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
				}
				if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
					String invTyp = invoice.getB2b().get(0).getInv().get(0).getInvTyp();
					if ("Regular".equalsIgnoreCase(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
					} else if ("Deemed Exports".equalsIgnoreCase(invTyp) || "Deemed Exp".equalsIgnoreCase(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("DE");
					} else if ("Supplies to SEZ with payment".equalsIgnoreCase(invTyp) || "SEZ supplies with payment".equalsIgnoreCase(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWP");
					} else if ("Supplies to SEZ without payment".equalsIgnoreCase(invTyp) || "SEZ supplies without payment".equalsIgnoreCase(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWOP");
					} else if ("Sale from Bonded Warehouse".equalsIgnoreCase(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("CBW");
					} else {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp(invTyp);
					}
				} else {
					invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
				}
				invoice.getB2b().get(0).getInv().get(0).setVal(invoice.getTotalamount());
				if (isNotEmpty(invoice.getStatename())) {
					invoice.getB2b().get(0).getInv().get(0).setPos(stateTin);
				}
				if ((isNotEmpty(invoice.getRevchargetype())
						&& invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_REVERSE))
						|| (isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals("YES"))) {
					invoice.setRevchargetype(MasterGSTConstants.TYPE_REVERSE);
					invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_YES);
				} else {
					invoice.setRevchargetype("Regular");
					invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_NO);
				}
				if (isNotEmpty(gstrItems)) {
					for (GSTRItems gstrItem : gstrItems) {
						if (isNotEmpty(gstrItem.getItem()) && isNotEmpty(gstrItem.getItem().getAdvAmt())) {
							gstrItem.getItem().setAdvAmt(null);
						}
					}
				}
				invoice.getB2b().get(0).getInv().get(0).setItms(gstrItems);
			}
		} else {
			if (invoice.getInvtype().equals(B2C) || invoice.getInvtype().equals(MasterGSTConstants.B2CSA)) {
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());

				if (isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					invoice.setB2cs(Lists.newArrayList());
					invoice.setB2cl(Lists.newArrayList());
					invoice.setInvtype(B2B);
					if (isEmpty(invoice.getB2b().get(0).getInv())) {
						List<GSTRInvoiceDetails> inv = Lists.newArrayList();
						GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
						List<GSTRItems> itms = Lists.newArrayList();
						GSTRItems gstrItem = new GSTRItems();
						gstrItem.setNum(1);
						itms.add(gstrItem);
						gstrInvoiceDetail.setItms(itms);
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							gstrInvoiceDetail.setDiffPercent(0.65);
						}
						inv.add(gstrInvoiceDetail);
						invoice.getB2b().get(0).setInv(inv);
					} else if (isEmpty(invoice.getB2b().get(0).getInv().get(0).getItms())) {
						List<GSTRItems> itms = Lists.newArrayList();
						GSTRItems gstrItem = new GSTRItems();
						gstrItem.setNum(1);
						itms.add(gstrItem);
						invoice.getB2b().get(0).getInv().get(0).setItms(itms);
					}
					String diffper = invoice.getDiffPercent();
					if ("Yes".equals(diffper)) {
						invoice.getB2b().get(0).getInv().get(0).setDiffPercent(0.65);
					}
					if (isNotEmpty(invoice.getDateofinvoice())) {
						invoice.getB2b().get(0).getInv().get(0).setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
					}
					if (isEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
					} else {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp(invoice.getB2b().get(0).getInv().get(0).getInvTyp());
					}
					invoice.getB2b().get(0).getInv().get(0).setVal(invoice.getTotalamount());
					if (isNotEmpty(invoice.getStatename())) {
						invoice.getB2b().get(0).getInv().get(0).setPos(stateTin);
					}
					if (isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_REVERSE)) {
						invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_YES);
					} else {
						invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_NO);
					}
					if (isNotEmpty(gstrItems)) {
						for (GSTRItems gstrItem : gstrItems) {
							if (isNotEmpty(gstrItem.getItem()) && isNotEmpty(gstrItem.getItem().getAdvAmt())) {
								gstrItem.getItem().setAdvAmt(null);
							}
						}
					}
					invoice.getB2b().get(0).getInv().get(0).setItms(gstrItems);
					if (isNotEmpty(ecomtin)) {
						invoice.getB2b().get(0).getInv().get(0).setEtin(ecomtin);
					}
				} else {
					List<GSTRB2CS> b2cs = Lists.newArrayList();
					String b2cskey="";
					List<GSTRB2CS> newInvs = Lists.newArrayList();
					List<String> b2cskeys = Lists.newArrayList();
					Map<String,GSTRB2CS> newb2csmap = Maps.newHashMap();
					for (Item item : invoice.getItems()) {
						GSTRB2CS gstrb2csDetail = new GSTRB2CS();
						if (isNotEmpty(item.getIgstrate()) && item.getIgstrate() > 0) {
							gstrb2csDetail.setRt(item.getIgstrate());
						} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())) {
							gstrb2csDetail.setRt(item.getCgstrate() + item.getSgstrate());
						}
						int retval = 0;
						if (isNotEmpty(item.getIgstamount())) {
							retval = Double.compare(0d, item.getIgstamount());
						}
						if (!isIntraState && isNotEmpty(item.getIgstamount()) && (retval < 0 || retval > 0)) {
							gstrb2csDetail.setIamt(item.getIgstamount());
						} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
							gstrb2csDetail.setCamt(item.getCgstamount());
							gstrb2csDetail.setSamt(item.getSgstamount());
						}
						if (isIntraState) {
							gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTRA);
						} else {
							gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTER);
						}
						gstrb2csDetail.setCsamt(item.getCessamount());
						gstrb2csDetail.setTxval(item.getTaxablevalue());
						if (isNotEmpty(invoice.getStatename())) {
							gstrb2csDetail.setPos(stateTin);
						}
						gstrb2csDetail.setTyp("OE");
						if (isNotEmpty(ecomtin)) {
							gstrb2csDetail.setTyp("E");
							gstrb2csDetail.setEtin(ecomtin.toUpperCase());
						} else if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
							gstrb2csDetail.setTyp("E");
							gstrb2csDetail.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
						}
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							gstrb2csDetail.setDiffPercent(0.65);
						}
						//b2cs.add(gstrb2csDetail);
						b2cskey = gstrb2csDetail.getPos()+"-"+gstrb2csDetail.getRt();
						if(!newb2csmap.containsKey(b2cskey)) {
							newb2csmap.put(b2cskey, gstrb2csDetail);
							b2cskeys.add(b2cskey);
						}else {
							GSTRB2CS b2c = newb2csmap.get(b2cskey);
							if (isNotEmpty(gstrb2csDetail.getTxval())) {
								if (isNotEmpty(b2c.getTxval())) {
									b2c.setTxval(b2c.getTxval() + gstrb2csDetail.getTxval());
								} else {
									b2c.setTxval(gstrb2csDetail.getTxval());
								}
							}
							if (isNotEmpty(gstrb2csDetail.getIamt())) {
								if (isNotEmpty(b2c.getIamt())) {
									b2c.setIamt(b2c.getIamt() + gstrb2csDetail.getIamt());
								} else {
									b2c.setIamt(gstrb2csDetail.getIamt());
								}
							}
							if (isNotEmpty(gstrb2csDetail.getCamt())) {
								if (isNotEmpty(b2c.getCamt())) {
									b2c.setCamt(b2c.getCamt() + gstrb2csDetail.getCamt());
								} else {
									b2c.setCamt(gstrb2csDetail.getCamt());
								}
							}
							if (isNotEmpty(gstrb2csDetail.getSamt())) {
								if (isNotEmpty(b2c.getSamt())) {
									b2c.setSamt(b2c.getSamt() + gstrb2csDetail.getSamt());
								} else {
									b2c.setSamt(gstrb2csDetail.getSamt());
								}
							}
							if (isNotEmpty(gstrb2csDetail.getCsamt())) {
								if (isNotEmpty(b2c.getCsamt())) {
									b2c.setCsamt(b2c.getCsamt() + gstrb2csDetail.getCsamt());
								} else {
									b2c.setCsamt(gstrb2csDetail.getCsamt());
								}
							}
							newb2csmap.put(b2cskey, b2c);
						}
					}
					for(String key : b2cskeys) {
						GSTRB2CS b2c = newb2csmap.get(key);
						newInvs.add(b2c);
					}
					invoice.setB2cs(newInvs);
					//invoice.setB2cs(b2cs);
				}
			} else if (invoice.getInvtype().equals(ADVANCES) || invoice.getInvtype().equals(MasterGSTConstants.ATA)) {
				List<GSTRAdvanceTax> at = null;
				if (returntype.equals(GSTR1)) {
					at = ((GSTR1) invoice).getAt();
				} else if (returntype.equals(GSTR2)) {
					at = ((GSTR2) invoice).getTxi();
				} else if (returntype.equals(PURCHASE_REGISTER)) {
					at = ((PurchaseRegister) invoice).getTxi();
				} else if (returntype.equals(GSTR4)) {
					at = ((GSTR4) invoice).getAt();
				}
				if (at == null) {
					at = Lists.newArrayList();
					GSTRAdvanceTax gstratDetails = new GSTRAdvanceTax();
					at.add(gstratDetails);
					if (returntype.equals(GSTR1)) {
						((GSTR1) invoice).setAt(at);
					} else if (returntype.equals(GSTR2)) {
						((GSTR2) invoice).setTxi(at);
					} else if (returntype.equals(PURCHASE_REGISTER)) {
						((GSTR2) invoice).setTxi(at);
					} else if (returntype.equals(GSTR4)) {
						((GSTR4) invoice).setAt(at);
					}
				}
				String splyType = "";
				if (isIntraState) {
					splyType = MasterGSTConstants.SUPPLY_TYPE_INTRA;
				} else {
					splyType = MasterGSTConstants.SUPPLY_TYPE_INTER;
				}
				if (at.size() == 0) {
					GSTRAdvanceTax gstratDetails = new GSTRAdvanceTax();
					gstratDetails.setSplyTy(splyType);
					at.get(0).setSplyTy(splyType);
				} else if (isEmpty(at.get(0).getSplyTy())) {
					at.get(0).setSplyTy(splyType);
				}
				invoice.setPaymentStatus("Paid");
				invoice.setB2cs(Lists.newArrayList());
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
				if (isNotEmpty(invoice.getStatename())) {
					at.get(0).setPos(stateTin);
				}
				String diffper = invoice.getDiffPercent();
				if ("Yes".equals(diffper)) {
					at.get(0).setDiffPercent(0.65);
				}
				List<GSTRItemDetails> gstrItemDetails = Lists.newArrayList();
				for (GSTRItems gstrItem : gstrItems) {
					gstrItem.getItem().setTxval(null);
					gstrItemDetails.add(gstrItem.getItem());
				}
				at.get(0).setItms(gstrItemDetails);
				//ata-omom
				if(invoice.getInvtype().equals(ATA)) {
					//String oinum = invoice.getB2b().get(0).getInv().get(0).getOinum();
					String fp = invoice.getFp();
					clientUtils.populateImportAmendmentDetails(invoice, null, fp, ADVANCES);
				}
			} else if (invoice.getInvtype().equals(ATPAID) || invoice.getInvtype().equals(MasterGSTConstants.TXPA)) {
				Double totalAmount = 0d;
				Double totalTax = 0d;
				Double totalTaxableAmt = 0d;
				Double totalITC = 0d;
				Client client = clientService.findById(invoice.getClientid());
				List<GSTRAdvanceTax> txpd = Lists.newArrayList();
				for (Item item : invoice.getItems()) {
					
					 if (invoice.getInvtype().equals(MasterGSTConstants.TXPA)) {
						 if(isNotEmpty(invoice.getStatename())) {
							 item.setAdvStateName(invoice.getStatename());
						 }
					 }
					
					GSTRAdvanceTax gstrtxpdDetails = new GSTRAdvanceTax();
					List<GSTRItems> gstradvItems = Lists.newArrayList();
					String advStatename = "";
					if (isNotEmpty(item.getAdvStateName())) {
						advStatename = item.getAdvStateName();
					}
					if (item.getAdvStateName().contains("-")) {
						advStatename = item.getAdvStateName().substring(item.getAdvStateName().indexOf("-")+1).trim();
					}
					StateConfig advstate = stateconfig.get(advStatename.replaceAll("\\s", "").toLowerCase());
					String advStateTin = "";
					if(isNotEmpty(advstate)) {
						Integer tin = advstate.getTin();
						advStateTin = (tin < 10) ? ("0" + tin) : (tin + "");
					}
					boolean isadvIntraState = true;
					if (isNotEmpty(item.getAdvStateName())) {
						if (!item.getAdvStateName().equals(client.getStatename())) {
							isadvIntraState = false;
						}
					}
					if (isadvIntraState) {
						gstrtxpdDetails.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTRA);
					} else {
						gstrtxpdDetails.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTER);
					}
					if (isNotEmpty(item.getAdvStateName())) {
						gstrtxpdDetails.setPos(advStateTin);
					}
					gstradvItems = populateAdvanceItemData(invoice, item, isadvIntraState, returntype);
					List<GSTRItemDetails> gstrItemDetails = Lists.newArrayList();
					for (GSTRItems gstrItem : gstradvItems) {
						gstrItem.getItem().setTxval(null);
						gstrItemDetails.add(gstrItem.getItem());
					}
					gstrtxpdDetails.setItms(gstrItemDetails);
					txpd.add(gstrtxpdDetails);
					if (isNotEmpty(item.getCgstamount())) {
						totalTax += item.getCgstamount();
					}
					if (isNotEmpty(item.getIgstamount())) {
						totalTax += item.getIgstamount();
					}
					if (isNotEmpty(item.getSgstamount())) {
						totalTax += item.getSgstamount();
					}
					if (isNotEmpty(item.getCessamount())) {
						totalTax += item.getCessamount();
					}
					if (isNotEmpty(item.getTotal())) {
						totalAmount += item.getTotal();
					}
					if (isNotEmpty(item.getTaxablevalue())) {
						totalTaxableAmt += item.getTaxablevalue();
					}
					if (isNotEmpty(item.getAdditionalchargevalue())) {
						totalTaxableAmt += item.getAdditionalchargevalue();
					}
					if (isNotEmpty(invoice.getRoundOffAmount())) {
						invoice.setTotalamount(totalAmount + invoice.getRoundOffAmount());
					} else {
						invoice.setRoundOffAmount(0.0);
						invoice.setTotalamount(totalAmount);
					}
					invoice.setTotaltax(totalTax);
					invoice.setTotaltaxableamount(totalTaxableAmt);
					invoice.setTotalitc(totalITC);
				}
				invoice.setTxpd(txpd);
				invoice.setPaymentStatus("Paid");
				invoice.setB2cs(Lists.newArrayList());
				invoice.setExp(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
				invoice.setNil(null);
				String diffper = invoice.getDiffPercent();
				if ("Yes".equals(diffper)) {
					invoice.getTxpd().get(0).setDiffPercent(0.65);
				}
				if(invoice.getInvtype().equals(TXPA)) {
					String oinum = invoice.getB2b().get(0).getInv().get(0).getOinum();
					String fp = invoice.getFp();
					clientUtils.populateImportAmendmentDetails(invoice, oinum, fp, ATPAID);
				}
			} else if (invoice.getInvtype().equals(NIL)) {
				
			} else if (invoice.getInvtype().equals(EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)) {
				invoice.setB2cs(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
				invoice.setNil(null);
				if (invoice.getExp() == null) {
					List<GSTRExports> expList = Lists.newArrayList();
					GSTRExports gstrExport = new GSTRExports();
					List<GSTRExportDetails> inv = Lists.newArrayList();
					GSTRExportDetails gstrExportDetail = new GSTRExportDetails();
					inv.add(gstrExportDetail);
					gstrExport.setInv(inv);
					expList.add(gstrExport);
					invoice.setExp(expList);
				} else if (isEmpty(invoice.getExp().get(0).getInv())) {
					List<GSTRExportDetails> inv = Lists.newArrayList();
					GSTRExportDetails gstrExportDetail = new GSTRExportDetails();
					inv.add(gstrExportDetail);
					invoice.getExp().get(0).setInv(inv);
				}
				if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
						invoice.getExp().get(0).getInv().get(0).setInum(invoice.getB2b().get(0).getInv().get(0).getInum());
					}
				}
				String diffper = invoice.getDiffPercent();
				if ("Yes".equals(diffper)) {
					invoice.getExp().get(0).getInv().get(0).setDiffPercent(0.65);
				}
				if (isNotEmpty(invoice.getDateofinvoice())) {
					invoice.getExp().get(0).getInv().get(0).setIdt(invoice.getDateofinvoice());
				}
				invoice.getExp().get(0).getInv().get(0).setVal(invoice.getTotalamount());
				List<GSTRItemDetails> gstrItemDetails = Lists.newArrayList();
				for (GSTRItems gstrItem : gstrItems) {
					gstrItemDetails.add(gstrItem.getItem());
				}
				invoice.getExp().get(0).getInv().get(0).setItms(gstrItemDetails);
				if(invoice.getInvtype().equals(EXPA)) {
					String oinum = invoice.getB2b().get(0).getInv().get(0).getOinum();
					String oidt = invoice.getB2b().get(0).getInv().get(0).getOidt();
					String fp = invoice.getFp();
					clientUtils.populateImportAmendmentDetails(invoice, oinum, oidt, EXPORTS);
				}
				//expa-oidt
			} else if (invoice.getInvtype().equals(CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNA)) {
				if (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())) {
					
					String cdn = invoice.getInvtype().equals(MasterGSTConstants.CDNA) ? CDNURA : CDNUR;
					invoice.setInvtype(cdn);						
					if (invoice.getCdnur() == null) {
						List<GSTRInvoiceDetails> cdnrList = Lists.newArrayList();
						GSTRInvoiceDetails gstrCreditDebitNote = new GSTRInvoiceDetails();
						cdnrList.add(gstrCreditDebitNote);
						invoice.setCdnur(cdnrList);
					}
					String type = invoice.getCdnur().get(0).getTyp();
					String invType = invoice.getCdnur().get(0).getInvTyp();
					if ((isEmpty(invoice.getCdnur().get(0).getNtNum()) && isEmpty(invoice.getCdnur().get(0).getNtDt()))
							&& (isNotEmpty(invoice.getCdn().get(0).getNt())
									&& isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtNum()))) {
						invoice.setCdnur(invoice.getCdn().get(0).getNt());
						if (returntype.equals(PURCHASE_REGISTER)) {
							invoice.getCdnur().get(0).setInvTyp(invType);
						} else {
							invoice.getCdnur().get(0).setTyp(type);
							if(type.equalsIgnoreCase("B2CL")) {
								invoice.getCdnur().get(0).setPos(stateTin);
							}
						}
					}
					if ((isNotEmpty(invoice.getCdn().get(0).getNt()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty()))) {
						invoice.getCdnur().get(0).setNtty(invoice.getCdn().get(0).getNt().get(0).getNtty());
					}
					invoice.setB2cs(Lists.newArrayList());
					invoice.setExp(Lists.newArrayList());
					invoice.setTxpd(Lists.newArrayList());
					invoice.setCdn(Lists.newArrayList());
					if (isNotEmpty(invoice.getCdnur().get(0).getNtNum())) {
						if(serviceUtils.isScientificNotation(invoice.getCdnur().get(0).getNtNum())) {
							String val = new BigDecimal(invoice.getCdnur().get(0).getNtNum()).toPlainString();
							invoice.getCdnur().get(0).setInum(val);
						} else {
							invoice.getCdnur().get(0).setInum(invoice.getCdnur().get(0).getNtNum().trim());
						}
					}else if(isNotEmpty(invoice.getCdnur().get(0).getInum())) {
						if(serviceUtils.isScientificNotation(invoice.getCdnur().get(0).getInum())) {
							String val = new BigDecimal(invoice.getCdnur().get(0).getInum()).toPlainString();
							invoice.getCdnur().get(0).setInum(val);
						} else {
							invoice.getCdnur().get(0).setInum(invoice.getCdnur().get(0).getInum().trim());
						}
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
						if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
							if(serviceUtils.isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getInum())) {
								String val = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getInum()).toPlainString();
								invoice.getCdnur().get(0).setNtNum(val);
							} else {
								invoice.getCdnur().get(0).setNtNum(invoice.getB2b().get(0).getInv().get(0).getInum().trim());
							}
						}
					}
					if (isNotEmpty(invoice.getCdnur().get(0).getNtDt())) {
						invoice.getCdnur().get(0).setIdt(simpleDateFormat.format(invoice.getCdnur().get(0).getNtDt()));
					}
					if (isNotEmpty(invoice.getDateofinvoice())) {
						String ntdt = simpleDateFormat.format(invoice.getDateofinvoice());
						ntdt = ntdt+"T18:30:000Z";
						SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:sss'Z'");
						try {
							invoice.getCdnur().get(0).setNtDt(simpleDateFormat1.parse(ntdt));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						invoice.getCdnur().get(0).setRtin(invoice.getB2b().get(0).getCtin().toUpperCase());
					}
					if (returntype.equals(GSTR4)) {
						if (isIntraState) {
							invoice.getCdnur().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTRA);
						} else {
							invoice.getCdnur().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTER);
						}
					}
					if (isNotEmpty(invoice.getRevchargetype())
							&& invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_REVERSE)) {
						invoice.getCdnur().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_YES);
					} else {
						invoice.getCdnur().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_NO);
					}
					
					String diffper = invoice.getDiffPercent();
					if ("Yes".equals(diffper)) {
						invoice.getCdnur().get(0).setDiffPercent(0.65);
					}
					invoice.getCdnur().get(0).setVal(invoice.getTotalamount());
					invoice.getCdnur().get(0).setItms(gstrItems);
				} else {
					invoice.setB2cs(Lists.newArrayList());
					invoice.setExp(Lists.newArrayList());
					invoice.setTxpd(Lists.newArrayList());
					invoice.setCdnur(Lists.newArrayList());
					invoice.setNil(null);
					List<GSTRCreditDebitNotes> notes = null;
					if (returntype.equals(GSTR1)) {
						notes = ((GSTR1) invoice).getCdnr();
					} else if (returntype.equals(GSTR2)) {
						notes = ((GSTR2) invoice).getCdn();
					} else if (returntype.equals(PURCHASE_REGISTER)) {
						notes = ((PurchaseRegister) invoice).getCdn();
					} else if (returntype.equals(GSTR4)) {
						notes = ((GSTR4) invoice).getCdnr();
					} else if (returntype.equals(GSTR6)) {
						notes = ((GSTR6) invoice).getCdn();
					} else if (returntype.equals(GSTR5)) {
						notes = ((GSTR5) invoice).getCdn();
					}
					if ((returntype.equals(GSTR1) || returntype.equals(GSTR4) || returntype.equals(GSTR5))
							&& isNotEmpty(notes)) {
						if ((isEmpty(notes.get(0).getNt()) || (isEmpty(notes.get(0).getNt().get(0).getNtNum())
								&& isEmpty(notes.get(0).getNt().get(0).getNtDt())))
								&& (isNotEmpty(invoice.getCdn().get(0).getNt())
										&& isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtNum()))) {
							notes.get(0).setNt(invoice.getCdn().get(0).getNt());
						}
					} else if ((returntype.equals(GSTR1) || returntype.equals(GSTR4) || returntype.equals(GSTR5))
							&& isEmpty(notes)) {
						notes = Lists.newArrayList();
						notes.add(invoice.getCdn().get(0));
					}
					invoice.setCdn(Lists.newArrayList());
					if (notes == null) {
						List<GSTRCreditDebitNotes> cdnrList = Lists.newArrayList();
						GSTRCreditDebitNotes gstrCreditDebitNote = new GSTRCreditDebitNotes();
						List<GSTRInvoiceDetails> nt = Lists.newArrayList();
						GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
						nt.add(gstrInvoiceDetail);
						gstrCreditDebitNote.setNt(nt);
						cdnrList.add(gstrCreditDebitNote);
						if (returntype.equals(GSTR1)) {
							((GSTR1) invoice).setCdnr(cdnrList);
						} else if (returntype.equals(GSTR2)) {
							((GSTR2) invoice).setCdn(cdnrList);
						} else if (returntype.equals(PURCHASE_REGISTER)) {
							((PurchaseRegister) invoice).setCdn(cdnrList);
						} else if (returntype.equals(GSTR4)) {
							((GSTR4) invoice).setCdnr(cdnrList);
						} else if (returntype.equals(GSTR6)) {
							((GSTR6) invoice).setCdn(cdnrList);
						} else if (returntype.equals(GSTR5)) {
							((GSTR5) invoice).setCdn(cdnrList);
						}
					} else if (isEmpty(notes.get(0).getNt())) {
						List<GSTRInvoiceDetails> nt = Lists.newArrayList();
						GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
						nt.add(gstrInvoiceDetail);
						notes.get(0).setNt(nt);
					}
					if (isNotEmpty(notes.get(0).getNt().get(0).getNtNum())) {
						if(serviceUtils.isScientificNotation(notes.get(0).getNt().get(0).getNtNum())) {
							String val = new BigDecimal(notes.get(0).getNt().get(0).getNtNum()).toPlainString();
							notes.get(0).getNt().get(0).setInum(val);
						} else {
							notes.get(0).getNt().get(0).setInum(notes.get(0).getNt().get(0).getNtNum().trim());
						}
					}else if(isNotEmpty(notes.get(0).getNt().get(0).getInum())) {
						if(serviceUtils.isScientificNotation(notes.get(0).getNt().get(0).getInum())) {
							String val = new BigDecimal(notes.get(0).getNt().get(0).getInum()).toPlainString();
							notes.get(0).getNt().get(0).setInum(val);
						} else {
							notes.get(0).getNt().get(0).setInum(notes.get(0).getNt().get(0).getInum().trim());
						}
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
						if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
							if(serviceUtils.isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getInum())) {
								String val = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getInum()).toPlainString();
								notes.get(0).getNt().get(0).setNtNum(val);
							} else {
								notes.get(0).getNt().get(0).setNtNum(invoice.getB2b().get(0).getInv().get(0).getInum().trim());
							}
						}
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						notes.get(0).setCtin(invoice.getB2b().get(0).getCtin().toUpperCase());
					}
					if (isNotEmpty(notes.get(0).getNt().get(0).getNtDt())) {
						notes.get(0).getNt().get(0).setIdt(simpleDateFormat.format(notes.get(0).getNt().get(0).getNtDt()));
					}
					if (isNotEmpty(invoice.getDateofinvoice())) {
						String ntdt = simpleDateFormat.format(invoice.getDateofinvoice());
						ntdt = ntdt+"T18:30:000Z";
						SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:sss'Z'");
						try {
							notes.get(0).getNt().get(0).setNtDt(simpleDateFormat1.parse(ntdt));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					if (isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_REVERSE)) {
						notes.get(0).getNt().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_YES);
					} else {
						notes.get(0).getNt().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_NO);
					}
					if(isNotEmpty(invoice.getStatename())) {
						notes.get(0).getNt().get(0).setPos(stateTin);
					}
					if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						notes.get(0).getNt().get(0).setInvTyp(invoice.getB2b().get(0).getInv().get(0).getInvTyp());
					}
					String diffper = invoice.getDiffPercent();
					if ("Yes".equals(diffper)) {
						notes.get(0).getNt().get(0).setDiffPercent(0.65);
					}
					if (returntype.equals(GSTR4)) {
						if (isIntraState) {
							notes.get(0).getNt().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTRA);
						} else {
							notes.get(0).getNt().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTER);
						}
					}
					notes.get(0).getNt().get(0).setVal(invoice.getTotalamount());
					notes.get(0).getNt().get(0).setItms(gstrItems);
					if (returntype.equals(GSTR1)) {
						((GSTR1) invoice).setCdnr(notes);
						
						if(invoice.getInvtype().equals(CDNA)) {
							String oinum = invoice.getB2b().get(0).getInv().get(0).getOinum();
							String oidt = invoice.getB2b().get(0).getInv().get(0).getOidt();
							String fp = invoice.getFp();
							clientUtils.populateImportAmendmentDetails(invoice, oinum, oidt, CREDIT_DEBIT_NOTES);
						}
					} else if (returntype.equals(GSTR2)) {
						((GSTR2) invoice).setCdn(notes);
					} else if (returntype.equals(PURCHASE_REGISTER)) {
						((PurchaseRegister) invoice).setCdn(notes);
					} else if (returntype.equals(GSTR4)) {
						((GSTR4) invoice).setCdnr(notes);
					} else if (returntype.equals(GSTR6)) {
						((GSTR6) invoice).setCdn(notes);
					} else if (returntype.equals(GSTR5)) {
						((GSTR5) invoice).setCdn(notes);
					}
				}

			} else if (invoice.getInvtype().equals(CDNUR) || invoice.getInvtype().equals(MasterGSTConstants.CDNURA)) {

				if (invoice.getCdnur() == null) {
					List<GSTRInvoiceDetails> cdnrList = Lists.newArrayList();
					GSTRInvoiceDetails gstrCreditDebitNote = new GSTRInvoiceDetails();
					cdnrList.add(gstrCreditDebitNote);
					invoice.setCdnur(cdnrList);
				}
				String type = invoice.getCdnur().get(0).getTyp();
				String invType = invoice.getCdnur().get(0).getInvTyp();
				if ((isEmpty(invoice.getCdnur().get(0).getNtNum()) && isEmpty(invoice.getCdnur().get(0).getNtDt()))
						&& (isNotEmpty(invoice.getCdn().get(0).getNt())
								&& isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtNum()))) {
					invoice.setCdnur(invoice.getCdn().get(0).getNt());
					if (returntype.equals(PURCHASE_REGISTER)) {
						invoice.getCdnur().get(0).setInvTyp(invType);
					} else {
						invoice.getCdnur().get(0).setTyp(type);
					}
				}
				if ((isNotEmpty(invoice.getCdn().get(0).getNt()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty()))) {
					invoice.getCdnur().get(0).setNtty(invoice.getCdn().get(0).getNt().get(0).getNtty());
				}
				invoice.setB2cs(Lists.newArrayList());
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setNil(null);
				
				if (isNotEmpty(invoice.getCdnur().get(0).getNtNum())) {
					invoice.getCdnur().get(0).setInum(invoice.getCdnur().get(0).getNtNum());
				}
				if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
						invoice.getCdnur().get(0).setNtNum(invoice.getB2b().get(0).getInv().get(0).getInum());
					}
				}
				if (isNotEmpty(invoice.getCdnur().get(0).getNtDt())) {
					invoice.getCdnur().get(0).setIdt(simpleDateFormat.format(invoice.getCdnur().get(0).getNtDt()));
				}
				if (isNotEmpty(invoice.getDateofinvoice())) {
					String ntdt = simpleDateFormat.format(invoice.getDateofinvoice());
					ntdt = ntdt+"T18:30:000Z";
					SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:sss'Z'");
					try {
						invoice.getCdnur().get(0).setNtDt(simpleDateFormat1.parse(ntdt));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					invoice.getCdnur().get(0).setRtin(invoice.getB2b().get(0).getCtin().toUpperCase());
				}
				if (returntype.equals(GSTR4)) {
					if (isIntraState) {
						invoice.getCdnur().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTRA);
					} else {
						invoice.getCdnur().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTER);
					}
				}
				if (isNotEmpty(invoice.getRevchargetype())
						&& invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_REVERSE)) {
					invoice.getCdnur().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_YES);
				} else {
					invoice.getCdnur().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_NO);
				}
				String diffper = invoice.getDiffPercent();
				if ("Yes".equals(diffper)) {
					invoice.getCdnur().get(0).setDiffPercent(0.65);
				}
				invoice.getCdnur().get(0).setVal(invoice.getTotalamount());
				invoice.getCdnur().get(0).setItms(gstrItems);
				if(invoice.getInvtype().equals(CDNURA)) {
					String oinum = invoice.getB2b().get(0).getInv().get(0).getOinum();
					String oidt = invoice.getB2b().get(0).getInv().get(0).getOidt();
					String fp = invoice.getFp();
					clientUtils.populateImportAmendmentDetails(invoice, oinum, oidt, CDNUR);
				}
			} else if (invoice.getInvtype().equals(IMP_GOODS)) {
				List<GSTRImportDetails> impGoods = null;
				if (returntype.equals(GSTR2)) {
					impGoods = ((GSTR2) invoice).getImpGoods();
				} else if (returntype.equals(PURCHASE_REGISTER)) {
					impGoods = ((PurchaseRegister) invoice).getImpGoods();
				} else if (returntype.equals(GSTR5)) {
					impGoods = ((GSTR5) invoice).getImpGoods();
				}
				if (impGoods == null) {
					impGoods = Lists.newArrayList();
					GSTRImportDetails gstrImportDetails = new GSTRImportDetails();
					List<GSTRImportItems> itms = Lists.newArrayList();
					GSTRImportItems importItem = new GSTRImportItems();
					itms.add(importItem);
					gstrImportDetails.setItms(itms);
					impGoods.add(gstrImportDetails);
				} else if (isEmpty(impGoods.get(0).getItms())) {
					List<GSTRImportItems> itms = Lists.newArrayList();
					GSTRImportItems importItem = new GSTRImportItems();
					itms.add(importItem);
					impGoods.get(0).setItms(itms);
				}
				impGoods.get(0).setBoeDt(invoice.getDateofinvoice());
				impGoods.get(0).setBoeNum(Integer.parseInt(invoice.getInvoiceno()));
				impGoods.get(0).setBoeVal(invoice.getTotalamount());
				List<GSTRImportItems> items = Lists.newArrayList();
				for (GSTRItems gstrItem : gstrItems) {
					if (isNotEmpty(gstrItem.getItem()) && isNotEmpty(gstrItem.getItc())) {
						GSTRImportItems importItem = new GSTRImportItems();
						importItem.setNum(gstrItem.getNum());
						importItem.setRt(gstrItem.getItem().getRt());
						importItem.setTxval(gstrItem.getItem().getTxval());
						importItem.setIamt(gstrItem.getItem().getIamt());
						importItem.setCsamt(gstrItem.getItem().getCsamt());
						importItem.setElg(gstrItem.getItc().getElg());
						importItem.setiTax(gstrItem.getItc().getiTax());
						importItem.setCsTax(gstrItem.getItc().getCsTax());
						items.add(importItem);
					}
				}
				impGoods.get(0).setItms(items);
				if (returntype.equals(GSTR2)) {
					((GSTR2) invoice).setImpGoods(impGoods);
				} else if (returntype.equals(PURCHASE_REGISTER)) {
					((PurchaseRegister) invoice).setImpGoods(impGoods);
				} else if (returntype.equals(GSTR5)) {
					((GSTR5) invoice).setImpGoods(impGoods);
				}
			} else if (invoice.getInvtype().equals(IMP_SERVICES)) {
				List<GSTRImportDetails> impServices = null;
				if (returntype.equals(GSTR2)) {
					impServices = ((GSTR2) invoice).getImpServices();
				} else if (returntype.equals(PURCHASE_REGISTER)) {
					impServices = ((PurchaseRegister) invoice).getImpServices();
				} else if (returntype.equals(GSTR4)) {
					impServices = ((GSTR4) invoice).getImpServices();
				}
				if (impServices == null) {
					impServices = Lists.newArrayList();
					GSTRImportDetails gstrImportDetails = new GSTRImportDetails();
					List<GSTRImportItems> itms = Lists.newArrayList();
					GSTRImportItems importItem = new GSTRImportItems();
					itms.add(importItem);
					gstrImportDetails.setItms(itms);
					impServices.add(gstrImportDetails);
				} else if (isEmpty(impServices.get(0).getItms())) {
					List<GSTRImportItems> itms = Lists.newArrayList();
					GSTRImportItems importItem = new GSTRImportItems();
					itms.add(importItem);
					impServices.get(0).setItms(itms);
				}
				if (isNotEmpty(invoice.getDateofinvoice())) {
					impServices.get(0).setIdt(invoice.getDateofinvoice());
				}
				if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
						impServices.get(0).setInum(invoice.getB2b().get(0).getInv().get(0).getInum());
					}
				}
				if (returntype.equals(GSTR4)) {
					if (isIntraState) {
						impServices.get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTRA);
					} else {
						impServices.get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTER);
					}
					impServices.get(0).setVal(invoice.getTotalamount());
				} else {
					impServices.get(0).setIval(invoice.getTotalamount());
				}
				List<GSTRImportItems> items = Lists.newArrayList();
				for (GSTRItems gstrItem : gstrItems) {
					if (isNotEmpty(gstrItem.getItem()) && isNotEmpty(gstrItem.getItc())) {
						GSTRImportItems importItem = new GSTRImportItems();
						importItem.setNum(gstrItem.getNum());
						importItem.setRt(gstrItem.getItem().getRt());
						importItem.setTxval(gstrItem.getItem().getTxval());
						importItem.setIamt(gstrItem.getItem().getIamt());
						importItem.setCsamt(gstrItem.getItem().getCsamt());
						importItem.setElg(gstrItem.getItc().getElg());
						importItem.setiTax(gstrItem.getItc().getiTax());
						importItem.setCsTax(gstrItem.getItc().getCsTax());
						items.add(importItem);
					}
				}
				impServices.get(0).setItms(items);
				if (returntype.equals(GSTR2)) {
					((GSTR2) invoice).setImpServices(impServices);
				} else if (returntype.equals(PURCHASE_REGISTER)) {
					((PurchaseRegister) invoice).setImpServices(impServices);
				} else if (returntype.equals(GSTR4)) {
					((GSTR4) invoice).setImpServices(impServices);
				}
			} else if (invoice.getInvtype().equals(B2BUR)) {
				invoice.setB2cs(Lists.newArrayList());
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());

				if (isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					if (returntype.equals(GSTR2)) {
						((GSTR2) invoice).setB2bur(Lists.newArrayList());
					} else if (returntype.equals(PURCHASE_REGISTER)) {
						((PurchaseRegister) invoice).setB2bur(Lists.newArrayList());
					} else if (returntype.equals(GSTR4)) {
						((GSTR4) invoice).setB2bur(Lists.newArrayList());
					}
					invoice.setInvtype(B2B);
					if (isNotEmpty(invoice.getDateofinvoice())) {
						invoice.getB2b().get(0).getInv().get(0)
								.setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
					}
					invoice.getB2b().get(0).getInv().get(0)
							.setInvTyp(invoice.getB2b().get(0).getInv().get(0).getInvTyp());
					invoice.getB2b().get(0).getInv().get(0).setVal(invoice.getTotalamount());
					if (isNotEmpty(invoice.getStatename())) {
						invoice.getB2b().get(0).getInv().get(0).setPos(stateTin);
					}
					if (isNotEmpty(invoice.getRevchargetype())
							&& invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_REVERSE)) {
						invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_YES);
					} else {
						invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_NO);
					}
					String diffper = invoice.getDiffPercent();
					if ("Yes".equals(diffper)) {
						invoice.getB2b().get(0).getInv().get(0).setDiffPercent(0.65);
					}
					invoice.getB2b().get(0).getInv().get(0).setItms(gstrItems);
					if (isNotEmpty(ecomtin)) {
						invoice.getB2b().get(0).getInv().get(0).setEtin(ecomtin);
					}
				} else {
					List<GSTRB2B> b2bur = null;
					if (returntype.equals(GSTR2)) {
						b2bur = ((GSTR2) invoice).getB2bur();
					} else if (returntype.equals(PURCHASE_REGISTER)) {
						b2bur = ((PurchaseRegister) invoice).getB2bur();
					} else if (returntype.equals(GSTR4)) {
						b2bur = ((GSTR4) invoice).getB2bur();
					}
					if (b2bur == null) {
						b2bur = Lists.newArrayList();
						GSTRB2B gstrb2b = new GSTRB2B();
						List<GSTRInvoiceDetails> inv = Lists.newArrayList();
						GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
						inv.add(gstrInvoiceDetail);
						gstrb2b.setInv(inv);
						b2bur.add(gstrb2b);
					} else if (isEmpty(b2bur.get(0).getInv())) {
						List<GSTRInvoiceDetails> inv = Lists.newArrayList();
						GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
						inv.add(gstrInvoiceDetail);
						b2bur.get(0).setInv(inv);
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
						if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
							b2bur.get(0).getInv().get(0).setInum(invoice.getB2b().get(0).getInv().get(0).getInum());
						}
					}
					if (isNotEmpty(invoice.getDateofinvoice())) {
						b2bur.get(0).getInv().get(0).setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
					}
					if (returntype.equals(GSTR4)) {
						if (isIntraState) {
							b2bur.get(0).getInv().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTRA);
						} else {
							b2bur.get(0).getInv().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTER);
						}
					}
					b2bur.get(0).getInv().get(0).setVal(invoice.getTotalamount());
					if (isNotEmpty(invoice.getStatename())) {
						b2bur.get(0).getInv().get(0).setPos(stateTin);
					}
					if (isNotEmpty(ecomtin)) {
						b2bur.get(0).getInv().get(0).setEtin(ecomtin);
					}
					b2bur.get(0).getInv().get(0).setItms(gstrItems);
					if (returntype.equals(GSTR2)) {
						((GSTR2) invoice).setB2bur(b2bur);
					} else if (returntype.equals(PURCHASE_REGISTER)) {
						((PurchaseRegister) invoice).setB2bur(b2bur);
					} else if (returntype.equals(GSTR4)) {
						((GSTR4) invoice).setB2bur(b2bur);
					}
				}

			} else if (invoice.getInvtype().equals(B2CL) || invoice.getInvtype().equals(MasterGSTConstants.B2CLA)) {
				invoice.setB2cs(Lists.newArrayList());
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());

				if (isEmpty(invoice.getB2cl())) {
					GSTRB2CL gstrb2cl = new GSTRB2CL();
					gstrb2cl.getInv().add(new GSTRInvoiceDetails());
					invoice.getB2cl().add(gstrb2cl);
				} else if (isEmpty(invoice.getB2cl().get(0).getInv())) {
					invoice.getB2cl().get(0).getInv().add(new GSTRInvoiceDetails());
				}
				if (isNotEmpty(invoice.getDateofinvoice())) {
					invoice.getB2cl().get(0).getInv().get(0)
							.setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
				}
				if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
						invoice.getB2cl().get(0).getInv().get(0)
								.setInum(invoice.getB2b().get(0).getInv().get(0).getInum());
					}
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
						invoice.getB2cl().get(0).getInv().get(0)
								.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
					}
				}
				invoice.getB2cl().get(0).getInv().get(0).setVal(invoice.getTotalamount());
				if (isNotEmpty(invoice.getStatename())) {
					invoice.getB2cl().get(0).setPos(stateTin);
				}
				String diffper = invoice.getDiffPercent();
				if ("Yes".equals(diffper)) {
					invoice.getB2cl().get(0).getInv().get(0).setDiffPercent(0.65);
				}
				if (isNotEmpty(ecomtin)) {
					invoice.getB2cl().get(0).getInv().get(0).setEtin(ecomtin);
				}
				if(invoice.getInvtype().equals(B2CLA)  && isNotEmpty(invoice.getStrOdate())) {
					invoice.getB2b().get(0).getInv().get(0).setOidt(invoice.getStrOdate());
				}//oidt add
				invoice.getB2cl().get(0).getInv().get(0).setItms(gstrItems);
				if(invoice.getInvtype().equals(B2CLA)) {
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getOinum())) {
						invoice.getB2cl().get(0).getInv().get(0)
								.setOinum(invoice.getB2b().get(0).getInv().get(0).getOinum());
					}
					String oinum = invoice.getB2b().get(0).getInv().get(0).getOinum();
					String oidt = invoice.getB2b().get(0).getInv().get(0).getOidt();
					String fp = invoice.getFp();
					clientUtils.populateImportAmendmentDetails(invoice, oinum, oidt, B2CL);
				}
			} else if (invoice.getInvtype().equals(B2B) || invoice.getInvtype().equals(MasterGSTConstants.B2BA)) {
				invoice.setB2cs(Lists.newArrayList());
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());

				if ("GSTR1".equals(returntype)
						&& (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin()))) {
					if (invoice.getTotalamount() > 250000 && !isIntraState) {
						invoice.setInvtype(B2CL);
						if (isEmpty(invoice.getB2cl())) {
							List<GSTRB2CL> b2cl = Lists.newArrayList();
							GSTRB2CL gstrb2clInvoice = new GSTRB2CL();
							List<GSTRInvoiceDetails> inv = Lists.newArrayList();
							GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
							String diffper = invoice.getDiffPercent();
							if ("Yes".equals(diffper)) {
								gstrInvoiceDetail.setDiffPercent(0.65);
							}
							List<GSTRItems> itms = Lists.newArrayList();
							GSTRItems gstrItem = new GSTRItems();
							gstrItem.setNum(1);
							itms.add(gstrItem);
							gstrInvoiceDetail.setItms(itms);
							inv.add(gstrInvoiceDetail);
							gstrb2clInvoice.setInv(inv);
							b2cl.add(gstrb2clInvoice);
							invoice.setB2cl(b2cl);
							if (isNotEmpty(ecomtin)) {
								invoice.getB2cl().get(0).getInv().get(0).setEtin(ecomtin);
							}
						} else if (isEmpty(invoice.getB2cl().get(0).getInv())) {
							List<GSTRInvoiceDetails> inv = Lists.newArrayList();
							GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
							String diffper = invoice.getDiffPercent();
							if ("Yes".equals(diffper)) {
								gstrInvoiceDetail.setDiffPercent(0.65);
							}
							List<GSTRItems> itms = Lists.newArrayList();
							GSTRItems gstrItem = new GSTRItems();
							gstrItem.setNum(1);
							itms.add(gstrItem);
							gstrInvoiceDetail.setItms(itms);
							inv.add(gstrInvoiceDetail);
							invoice.getB2cl().get(0).setInv(inv);
						}
						if (isNotEmpty(invoice.getDateofinvoice())) {
							invoice.getB2cl().get(0).getInv().get(0)
									.setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
						}
						if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
							if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
								invoice.getB2cl().get(0).getInv().get(0)
										.setInum(invoice.getB2b().get(0).getInv().get(0).getInum());
							}
							if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
								invoice.getB2cl().get(0).getInv().get(0)
										.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin());
							}
						}
						invoice.getB2cl().get(0).getInv().get(0).setVal(invoice.getTotalamount());
						if (isNotEmpty(invoice.getStatename())) {
							invoice.getB2cl().get(0).setPos(stateTin);
						}
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							invoice.getB2cl().get(0).getInv().get(0).setDiffPercent(0.65);
						}
						if (isNotEmpty(ecomtin)) {
							invoice.getB2cl().get(0).getInv().get(0).setEtin(ecomtin);
						}
						invoice.getB2cl().get(0).getInv().get(0).setItms(gstrItems);
					} else {
						invoice.setInvtype(B2C);
						if (isEmpty(invoice.getB2cs())) {
							List<GSTRB2CS> b2cs = Lists.newArrayList();
							invoice.setB2cs(b2cs);
						}
						List<GSTRB2CS> b2cs = Lists.newArrayList();
						String b2cskey="";
						List<GSTRB2CS> newInvs = Lists.newArrayList();
						List<String> b2cskeys = Lists.newArrayList();
						Map<String,GSTRB2CS> newb2csmap = Maps.newHashMap();
						for (Item item : invoice.getItems()) {
							GSTRB2CS gstrb2csDetail = new GSTRB2CS();
							if (isNotEmpty(item.getIgstrate())) {
								gstrb2csDetail.setRt(item.getIgstrate());
							} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())) {
								gstrb2csDetail.setRt(item.getCgstrate() + item.getSgstrate());
							}
							if (!isIntraState && isNotEmpty(item.getIgstamount())) {
								gstrb2csDetail.setIamt(item.getIgstamount());
							} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
								gstrb2csDetail.setCamt(item.getCgstamount());
								gstrb2csDetail.setSamt(item.getSgstamount());
							}
							if (isIntraState) {
								gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTRA);
							} else {
								gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTER);
							}
							gstrb2csDetail.setCsamt(item.getCessamount());
							gstrb2csDetail.setTxval(item.getTaxablevalue());
							if (isNotEmpty(invoice.getStatename())) {
								gstrb2csDetail.setPos(stateTin);
							}
							String ecomGSTIN = null;
							if (!invoice.getB2cs().isEmpty()) {
								ecomGSTIN = invoice.getB2cs().get(0).getEtin();
							}
							gstrb2csDetail.setTyp("OE");
							if (isNotEmpty(ecomtin)) {
								gstrb2csDetail.setTyp("E");
								gstrb2csDetail.setEtin(ecomtin.toUpperCase());
							} else if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
								gstrb2csDetail.setTyp("E");
								gstrb2csDetail.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
							}
							String diffper = invoice.getDiffPercent();
							if ("Yes".equals(diffper)) {
								gstrb2csDetail.setDiffPercent(0.65);
							}
							b2cskey = gstrb2csDetail.getPos()+"-"+gstrb2csDetail.getRt();
							if(!newb2csmap.containsKey(b2cskey)) {
								newb2csmap.put(b2cskey, gstrb2csDetail);
								b2cskeys.add(b2cskey);
							}else {
								GSTRB2CS b2c = newb2csmap.get(b2cskey);
								if (isNotEmpty(gstrb2csDetail.getTxval())) {
									if (isNotEmpty(b2c.getTxval())) {
										b2c.setTxval(b2c.getTxval() + gstrb2csDetail.getTxval());
									} else {
										b2c.setTxval(gstrb2csDetail.getTxval());
									}
								}
								if (isNotEmpty(gstrb2csDetail.getIamt())) {
									if (isNotEmpty(b2c.getIamt())) {
										b2c.setIamt(b2c.getIamt() + gstrb2csDetail.getIamt());
									} else {
										b2c.setIamt(gstrb2csDetail.getIamt());
									}
								}
								if (isNotEmpty(gstrb2csDetail.getCamt())) {
									if (isNotEmpty(b2c.getCamt())) {
										b2c.setCamt(b2c.getCamt() + gstrb2csDetail.getCamt());
									} else {
										b2c.setCamt(gstrb2csDetail.getCamt());
									}
								}
								if (isNotEmpty(gstrb2csDetail.getSamt())) {
									if (isNotEmpty(b2c.getSamt())) {
										b2c.setSamt(b2c.getSamt() + gstrb2csDetail.getSamt());
									} else {
										b2c.setSamt(gstrb2csDetail.getSamt());
									}
								}
								if (isNotEmpty(gstrb2csDetail.getCsamt())) {
									if (isNotEmpty(b2c.getCsamt())) {
										b2c.setCsamt(b2c.getCsamt() + gstrb2csDetail.getCsamt());
									} else {
										b2c.setCsamt(gstrb2csDetail.getCsamt());
									}
								}
								newb2csmap.put(b2cskey, b2c);
							}
						}
						for(String key : b2cskeys) {
							GSTRB2CS b2c = newb2csmap.get(key);
							newInvs.add(b2c);
						}
						invoice.setB2cs(newInvs);
						//invoice.setB2cs(b2cs);
					}
				} else if (!returntype.equals(ANX1)) {

					if (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())) {
						invoice.setB2cs(Lists.newArrayList());
						invoice.setExp(Lists.newArrayList());
						invoice.setTxpd(Lists.newArrayList());
						invoice.setCdn(Lists.newArrayList());
						invoice.setCdnur(Lists.newArrayList());
						invoice.setInvtype(MasterGSTConstants.B2BUR);
						List<GSTRB2B> b2bur = null;
						if (returntype.equals(GSTR2)) {
							b2bur = ((GSTR2) invoice).getB2bur();
						} else if (returntype.equals(PURCHASE_REGISTER)) {
							b2bur = ((PurchaseRegister) invoice).getB2bur();
						} else if (returntype.equals(GSTR4)) {
							b2bur = ((GSTR4) invoice).getB2bur();
						}
						if (b2bur == null) {
							b2bur = Lists.newArrayList();
							GSTRB2B gstrb2b = new GSTRB2B();
							List<GSTRInvoiceDetails> inv = Lists.newArrayList();
							GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
							inv.add(gstrInvoiceDetail);
							gstrb2b.setInv(inv);
							b2bur.add(gstrb2b);
						} else if (isEmpty(b2bur.get(0).getInv())) {
							List<GSTRInvoiceDetails> inv = Lists.newArrayList();
							GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
							inv.add(gstrInvoiceDetail);
							b2bur.get(0).setInv(inv);
						}
						if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
							if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
								b2bur.get(0).getInv().get(0).setInum(invoice.getB2b().get(0).getInv().get(0).getInum());
							}
						}
						if (isNotEmpty(invoice.getDateofinvoice())) {
							b2bur.get(0).getInv().get(0).setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
						}
						if (returntype.equals(GSTR4)) {
							if (isIntraState) {
								b2bur.get(0).getInv().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTRA);
							} else {
								b2bur.get(0).getInv().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTER);
							}
						}
						b2bur.get(0).getInv().get(0).setVal(invoice.getTotalamount());
						if (isNotEmpty(invoice.getStatename())) {
							b2bur.get(0).getInv().get(0).setPos(stateTin);
						}
						b2bur.get(0).getInv().get(0).setItms(gstrItems);
						if (returntype.equals(GSTR2)) {
							((GSTR2) invoice).setB2bur(b2bur);
						} else if (returntype.equals(PURCHASE_REGISTER)) {
							((PurchaseRegister) invoice).setB2bur(b2bur);
						} else if (returntype.equals(GSTR4)) {
							((GSTR4) invoice).setB2bur(b2bur);
						}

					} else {

						if (isNotEmpty(invoice.getDateofinvoice())) {
							invoice.getB2b().get(0).getInv().get(0)
									.setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
						}
						if(invoice.getInvtype().equals(B2BA)  && isNotEmpty(invoice.getStrOdate())) {
							invoice.getB2b().get(0).getInv().get(0).setOidt(invoice.getStrOdate());
						}//oidt add
						invoice.getB2b().get(0).getInv().get(0)
								.setInvTyp(invoice.getB2b().get(0).getInv().get(0).getInvTyp());
						invoice.getB2b().get(0).getInv().get(0).setVal(invoice.getTotalamount());
						if (isNotEmpty(invoice.getStatename())) {
							invoice.getB2b().get(0).getInv().get(0).setPos(stateTin);
						}
						if (isNotEmpty(invoice.getRevchargetype())
								&& invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_REVERSE)) {
							invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_YES);
						} else {
							invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_NO);
						}
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							invoice.getB2b().get(0).getInv().get(0).setDiffPercent(0.65);
						}
						invoice.getB2b().get(0).getInv().get(0).setItms(gstrItems);
						if (isNotEmpty(ecomtin)) {
							invoice.getB2b().get(0).getInv().get(0).setEtin(ecomtin);
						}
						if(invoice.getInvtype().equals(B2BA)) {
							String oinum = invoice.getB2b().get(0).getInv().get(0).getOinum();
							String oidt = invoice.getB2b().get(0).getInv().get(0).getOidt();
							String fp = invoice.getFp();
							clientUtils.populateImportAmendmentDetails(invoice, oinum, oidt, B2B);
							//((GSTR1)invoice).setB2ba(invoice.getB2b());
						}
					}
				}
			} else if (invoice.getInvtype().equals(B2C) || invoice.getInvtype().equals(MasterGSTConstants.B2CSA)) {
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());

				if (isIntraState && isNotEmpty(invoice.getItems().get(0).getCgstrate())
						&& isNotEmpty(invoice.getItems().get(0).getSgstrate())) {
					invoice.getB2cs().get(0)
							.setRt(invoice.getItems().get(0).getCgstrate() + invoice.getItems().get(0).getSgstrate());
					invoice.getB2cs().get(0).setCamt(invoice.getItems().get(0).getCgstamount());
					invoice.getB2cs().get(0).setSamt(invoice.getItems().get(0).getSgstamount());
				} else if (isNotEmpty(invoice.getItems().get(0).getIgstrate())) {
					invoice.getB2cs().get(0).setRt(invoice.getItems().get(0).getIgstrate());
					invoice.getB2cs().get(0).setIamt(invoice.getItems().get(0).getIgstamount());
				}
				if (isNotEmpty(invoice.getItems().get(0).getCessamount())) {
					invoice.getB2cs().get(0).setCsamt(invoice.getItems().get(0).getCessamount());
				}
				if (isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
					invoice.getB2cs().get(0).setTxval(invoice.getItems().get(0).getTaxablevalue());
				}
				invoice.getB2cs().get(0).setTyp("OE");
				if (isNotEmpty(ecomtin)) {
					invoice.getB2cs().get(0).setTyp("E");
					invoice.getB2cs().get(0).setEtin(ecomtin.toUpperCase());
				} else if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
					invoice.getB2cs().get(0).setTyp("E");
					invoice.getB2cs().get(0).setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
				}
				String diffper = invoice.getDiffPercent();
				if ("Yes".equals(diffper)) {
					invoice.getB2cs().get(0).setDiffPercent(0.65);
				}
				if (isNotEmpty(invoice.getStatename())) {
					invoice.getB2cs().get(0).setPos(stateTin);
				}
			}
			if (returntype.equals(MasterGSTConstants.DELIVERYCHALLANS)) {
				invoice.setB2cs(Lists.newArrayList());
				invoice.setB2cl(Lists.newArrayList());
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
				invoice.setInvtype(MasterGSTConstants.DELIVERYCHALLANS);
				List<DeliveryChallana> deliveryChallan = null;
				deliveryChallan = ((DeliveryChallan) invoice).getDc();
				if (deliveryChallan == null) {
					deliveryChallan = Lists.newArrayList();
					DeliveryChallana challan = new DeliveryChallana();
					deliveryChallan.add(challan);
					((DeliveryChallan) invoice).setDc(deliveryChallan);

				}
			}
			if (returntype.equals(MasterGSTConstants.PROFORMAINVOICES)) {
				invoice.setB2cs(Lists.newArrayList());
				invoice.setB2cl(Lists.newArrayList());
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
				invoice.setInvtype(MasterGSTConstants.PROFORMAINVOICES);
				List<PreformaInvoice> preformaInvoice = null;
				preformaInvoice = ((ProformaInvoices) invoice).getPi();
				if (preformaInvoice == null) {
					preformaInvoice = Lists.newArrayList();
					PreformaInvoice challan = new PreformaInvoice();
					challan.setExpirydate(simpleDateFormat.format(invoice.getExpiryDate()));
					preformaInvoice.add(challan);
					((ProformaInvoices) invoice).setPi(preformaInvoice);
				} else {
					preformaInvoice.get(0).setExpirydate(simpleDateFormat.format(invoice.getExpiryDate()));
				}
			}
			if (returntype.equals(MasterGSTConstants.ESTIMATES)) {
				invoice.setB2cs(Lists.newArrayList());
				invoice.setB2cl(Lists.newArrayList());
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
				invoice.setInvtype(MasterGSTConstants.ESTIMATES);
				List<Estimate> estimate = null;
				estimate = ((Estimates) invoice).getEst();
				if (estimate == null) {
					estimate = Lists.newArrayList();
					Estimate estimates = new Estimate();
					estimates.setExpirydate(simpleDateFormat.format(invoice.getExpiryDate()));
					estimate.add(estimates);
					((Estimates) invoice).setEst(estimate);
				} else {
					estimate.get(0).setExpirydate(simpleDateFormat.format(invoice.getExpiryDate()));
				}
			}
			if (returntype.equals(MasterGSTConstants.PURCHASEORDER)) {
				invoice.setB2cs(Lists.newArrayList());
				invoice.setB2cl(Lists.newArrayList());
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
				invoice.setInvtype(MasterGSTConstants.PURCHASEORDER);
				List<PurchaseOrders> purchaseOrder = null;
				purchaseOrder = ((PurchaseOrder) invoice).getPo();
				if (purchaseOrder == null) {
					purchaseOrder = Lists.newArrayList();
					PurchaseOrders purchaseOrders = new PurchaseOrders();
					purchaseOrders.setDeliverydate(simpleDateFormat.format(invoice.getDeliveryDate()));
					purchaseOrder.add(purchaseOrders);
					((PurchaseOrder) invoice).setPo(purchaseOrder);
				} else {
					purchaseOrder.get(0).setDeliverydate(simpleDateFormat.format(invoice.getDeliveryDate()));
				}
			}
			if (returntype.equals(MasterGSTConstants.EWAYBILL)) {
				Client client = clientService.findById(invoice.getClientid());
				CompanyCustomers customer = companyCustomersRepository.findOne(invoice.getClientid());
				invoice.seteBillDate(invoice.geteBillDate());
				invoice.setGenerateMode("EwayBill");
				invoice.setFromGstin(client.getGstnnumber());
				invoice.setFromTrdName(client.getBusinessname());
				if(isNotEmpty(client.getAddress())) {
					String address =  client.getAddress();
					if(address.length() > 120) {
						address = address.substring(0, 119);
					}
					invoice.setFromAddr1(address);
					invoice.setFromAddr2("");
				}else {
					invoice.setFromAddr1("");
					invoice.setFromAddr2("");
				}
				String state_Code = client.getGstnnumber();
				invoice.setFromStateCode(Integer.parseInt(state_Code.substring(0, 2)));
				invoice.setToGstin(invoice.getB2b().get(0).getCtin());
				if(isNotEmpty(invoice) && isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
					String address =  invoice.getB2b().get(0).getInv().get(0).getAddress();
					if(address.length() > 120) {
						address = address.substring(0, 119);
					}
					invoice.setToAddr1(address);
					invoice.setToAddr2("");
				}else {
					invoice.setToAddr1("");
					invoice.setToAddr2("");
				}
				invoice.setToPlace(invoice.getStatename());
				String state_TCode = invoice.getB2b().get(0).getCtin();
				invoice.setToStateCode(Integer.parseInt(state_TCode.substring(0, 2)));
				if (isNotEmpty(customer) && isNotEmpty(customer.getName())) {
					invoice.setToTrdName(customer.getName());
				}
				invoice.setActFromStateCode(Integer.parseInt(state_Code.substring(0, 2)));
				invoice.setActToStateCode(Integer.parseInt(state_TCode.substring(0, 2)));
			}
		}
		if (isEmpty(invoice.getDateofinvoice())) {
			invoice.setDateofinvoice(new Date());
		}
		int mnths = -1,yrs=-1;
		if(ATA.equals(invoice.getInvtype()) || TXPA.equals(invoice.getInvtype()) || "Advances".equals(invoice.getInvtype()) || "Nil Supplies".equals(invoice.getInvtype()) || "Advance Adjusted Detail".equals(invoice.getInvtype())){
			String fp = invoice.getFp();
			int sumFactor = 1;
			if(fp != null){
				try {
					mnths = Integer.parseInt(fp.substring(0,2));
					mnths--;
					yrs = Integer.parseInt(fp.substring(2));
				} catch (NumberFormatException e) {
					
				}
			}
			int quarter = mnths/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (yrs-1)+"-"+yrs : (yrs)+"-"+(yrs+1);
			mnths++;
			
			invoice.setMthCd(""+mnths);
			invoice.setYrCd(""+yearCode);
			invoice.setQrtCd(""+quarter);
			invoice.setSftr(sumFactor);
		}else {
			
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
		boolean isCdnurNttyExists = invoice.getCdnur() != null && invoice.getCdnur().size() > 0;
		String cdnurNtty = null;
		if(isCdnurNttyExists){
			cdnurNtty = invoice.getCdnur().get(0).getNtty();
		}
		int sumFactor = 1;
		if(!"CANCELLED".equals(invoice.getGstStatus())){
			if(returntype.equals("GSTR1")) {
				boolean isCdnrNtNttyExists = ((GSTR1)invoice).getCdnr() != null && ((GSTR1)invoice).getCdnr().size() > 0 && ((GSTR1)invoice).getCdnr().get(0).getNt() != null && ((GSTR1)invoice).getCdnr().get(0).getNt().size() > 0;
				String cdnrNtNtty = null;
				if(isCdnrNtNttyExists){
				cdnrNtNtty = ((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty();
				}
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
			}else {
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
			}
			
		}else{
			sumFactor = 0;
		}
		invoice.setSftr(sumFactor);
		Date dt = null;
		if (returntype.equals(MasterGSTConstants.EWAYBILL)) {
			if(isNotEmpty(invoice.geteBillDate())) {
				dt = (Date)invoice.geteBillDate();
			}else {
				dt = (Date)invoice.getDateofinvoice();
			}
		}else {
			dt = (Date)invoice.getDateofinvoice();
		}
		if(isNotEmpty(dt)) {
			int month = dt.getMonth();
			int year = dt.getYear()+1900;
			int quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			
			invoice.setMthCd(""+month);
			invoice.setYrCd(""+yearCode);
			invoice.setQrtCd(""+quarter);
		}
		}
		if(NullUtil.isNotEmpty(invoice.getBillDate())) {
			Date billdt = (Date)invoice.getBillDate();
			int billmonth = billdt.getMonth();
			int billyear = billdt.getYear()+1900;
			int billquarter = billmonth/3;
			billquarter = billquarter == 0 ? 4 : billquarter;
			String billyearCode = billquarter == 4 ? (billyear-1)+"-"+billyear : (billyear)+"-"+(billyear+1);
			billmonth++;
			invoice.setTrDatemthCd(""+billmonth);
			invoice.setTrDateqrtCd(""+billquarter);
			invoice.setTrDateyrCd(""+billyearCode);
		}
		Date ewayBillDate = null;
		Double totalTaxableAmt = invoice.getTotaltaxableamount();
		Double totalTax = invoice.getTotaltax();
		Double totalAmt = invoice.getTotalamount();
		Date dateOfInvoice = invoice.getDateofinvoice();
		if(isNotEmpty(invoice.geteBillDate())) {
			ewayBillDate = invoice.geteBillDate();
		}
		String totalTaxableAmtStr = String.format(DOUBLE_FORMAT,totalTaxableAmt);
		String totalTaxStr = String.format(DOUBLE_FORMAT,totalTax);
		String totalAmtStr = String.format(DOUBLE_FORMAT,totalAmt);
		String dateOfInvoiceStr = dateFormatOnlyDate.format(dateOfInvoice);
		String ewayBillDateStr = "";
		if(isNotEmpty(ewayBillDate)) {
			ewayBillDateStr = dateFormatOnlyDate.format(ewayBillDate);
		}
		invoice.setTotaltaxableamount_str(totalTaxableAmtStr);
		invoice.setTotaltax_str(totalTaxStr);
		invoice.setTotalamount_str(totalAmtStr);
		invoice.setDateofinvoice_str(dateOfInvoiceStr);
		if(isNotEmpty(ewayBillDateStr)) {
			invoice.setEwayBillDate_str(ewayBillDateStr);
		}
		for (Item item : invoice.getItems()) {
			if (isNotEmpty(item.getIgstamount())) {
				totalIGST += item.getIgstamount();
			}
			if (isNotEmpty(item.getCgstamount())) {
				totalCGST += item.getCgstamount();
			}
			if (isNotEmpty(item.getSgstamount())) {
				totalSGST += item.getSgstamount();
			}
			if (isNotEmpty(item.getExmepted())) {
				totalExempted += item.getExmepted();
			}
			if(isNotEmpty(item.getStateCess())) {
				totalStateCessAmt += item.getStateCess();
			}
			if(isNotEmpty(item.getAssAmt())) {
				totalAssAmt += item.getAssAmt();
			}
			if(isNotEmpty(item.getCessNonAdvol())) {
				totalCessNonAdVal += item.getCessNonAdvol();
			}
			if(isNotEmpty(item.getCessamount())) {
				totalCessAmount+=item.getCessamount();
			}
			if(isNotEmpty(item.getDiscount())) {
				toralDiscAmount+=item.getDiscount();
			}
			if(isNotEmpty(item.getOthrCharges())) {
				totalOthrChrgAmount+=item.getOthrCharges();
			}
		}
		invoice.setTotalIgstAmount(totalIGST);
		invoice.setTotalCgstAmount(totalCGST);
		invoice.setTotalSgstAmount(totalSGST);
		invoice.setTotalExemptedAmount(totalExempted);
		invoice.setTotalCessAmount(totalCessAmount);
		if(isNotEmpty(totalAssAmt)) {
			invoice.setTotalAssAmount(totalAssAmt);
		}
		if(isNotEmpty(totalCessNonAdVal)) {
			invoice.setTotalCessNonAdVal(totalCessNonAdVal);
		}
		if(isNotEmpty(totalStateCessAmt)) {
			invoice.setTotalStateCessAmount(totalStateCessAmt);
		}
		if(isNotEmpty(toralDiscAmount)) {
			invoice.setTotalDiscountAmount(toralDiscAmount);
		}
		if(isNotEmpty(totalOthrChrgAmount)) {
			invoice.setTotalOthrChrgeAmount(totalOthrChrgAmount);
		}
		if (returntype.equals(MasterGSTConstants.GSTR1) && isNotEmpty(invoice.getGstr1orEinvoice()) && invoice.getGstr1orEinvoice().equalsIgnoreCase("Einvoice")) {
			Client client = clientService.findById(invoice.getClientid());
			invoice = einvoiceService.populateEinvoiceDetails(invoice, client);
		}
		savetcstdsamount(returntype,invoice);
		invoice.setCsftr(1);
		return invoice;
	}
	private void populateNilItems(Item nItem, GSTRNilSupItems eItem) {
		nItem.setElg("");
		if (nItem.getType().equals("Nil Rated")) {
			if (isEmpty(eItem.getNilsply())) {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setNilsply(nItem.getTaxablevalue());
				} else {
					eItem.setNilsply(0d);
				}
			} else {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setNilsply(eItem.getNilsply() + nItem.getTaxablevalue());
				} else {
					eItem.setNilsply(eItem.getNilsply() + 0d);
				}
			}
			if (isEmpty(eItem.getExptdsply())) {
				eItem.setExptdsply(0d);
			} else {
				eItem.setExptdsply(eItem.getExptdsply() + 0d);
			}
			if (isEmpty(eItem.getNgsply())) {
				eItem.setNgsply(0d);
			} else {
				eItem.setNgsply(eItem.getNgsply() + 0d);
			}
			if (isEmpty(eItem.getCpddr())) {
				eItem.setCpddr(0d);
			} else {
				eItem.setCpddr(eItem.getCpddr() + 0d);
			}
		} else if (nItem.getType().equals("Exempted")) {
			if (isEmpty(eItem.getExptdsply())) {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setExptdsply(nItem.getTaxablevalue());
				} else {
					eItem.setNilsply(0d);
				}
			} else {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setExptdsply(eItem.getExptdsply() + nItem.getTaxablevalue());
				} else {
					eItem.setExptdsply(eItem.getExptdsply() + 0d);
				}
			}
			if (isEmpty(eItem.getNilsply())) {
				eItem.setNilsply(0d);
			} else {
				eItem.setNilsply(eItem.getNilsply() + 0d);
			}
			if (isEmpty(eItem.getNgsply())) {
				eItem.setNgsply(0d);
			} else {
				eItem.setNgsply(eItem.getNgsply() + 0d);
			}
			if (isEmpty(eItem.getCpddr())) {
				eItem.setCpddr(0d);
			} else {
				eItem.setCpddr(eItem.getCpddr() + 0d);
			}
		} else if (nItem.getType().equals("Non-GST")) {
			if (isEmpty(eItem.getNgsply())) {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setNgsply(nItem.getTaxablevalue());
				} else {
					eItem.setNgsply(0d);
				}
			} else {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setNgsply(eItem.getNgsply() + nItem.getTaxablevalue());
				} else {
					eItem.setNgsply(eItem.getNgsply() + 0d);
				}
			}
			if (isEmpty(eItem.getNilsply())) {
				eItem.setNilsply(0d);
			} else {
				eItem.setNilsply(eItem.getNilsply() + 0d);
			}
			if (isEmpty(eItem.getExptdsply())) {
				eItem.setExptdsply(0d);
			} else {
				eItem.setExptdsply(eItem.getExptdsply() + 0d);
			}
			if (isEmpty(eItem.getCpddr())) {
				eItem.setCpddr(0d);
			} else {
				eItem.setCpddr(eItem.getCpddr() + 0d);
			}
		} else if (nItem.getType().equals("From Compounding Dealer")) {
			if (isEmpty(eItem.getCpddr())) {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setCpddr(nItem.getTaxablevalue());
				} else {
					eItem.setCpddr(0d);
				}
			} else {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setCpddr(eItem.getCpddr() + nItem.getTaxablevalue());
				} else {
					eItem.setCpddr(eItem.getCpddr() + 0d);
				}
			}
			if (isEmpty(eItem.getNilsply())) {
				eItem.setNilsply(0d);
			} else {
				eItem.setNilsply(eItem.getNilsply() + 0d);
			}
			if (isEmpty(eItem.getExptdsply())) {
				eItem.setExptdsply(0d);
			} else {
				eItem.setExptdsply(eItem.getExptdsply() + 0d);
			}
			if (isEmpty(eItem.getNgsply())) {
				eItem.setNgsply(0d);
			} else {
				eItem.setNgsply(eItem.getNgsply() + 0d);
			}
		}
	}

	private void populateNilItems(Item nItem, GSTRNilItems eItem) {
		nItem.setElg("");
		if (nItem.getType().equals("Nil Rated")) {
			if (isEmpty(eItem.getNilAmt())) {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setNilAmt(nItem.getTaxablevalue());
				} else {
					eItem.setNilAmt(0d);
				}
			} else {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setNilAmt(eItem.getNilAmt() + nItem.getTaxablevalue());
				} else {
					eItem.setNilAmt(eItem.getNilAmt() + 0d);
				}
			}
			if (isEmpty(eItem.getExptAmt())) {
				eItem.setExptAmt(0d);
			} else {
				eItem.setExptAmt(eItem.getExptAmt() + 0d);
			}
			if (isEmpty(eItem.getNgsupAmt())) {
				eItem.setNgsupAmt(0d);
			} else {
				eItem.setNgsupAmt(eItem.getNgsupAmt() + 0d);
			}
		} else if (nItem.getType().equals("Exempted")) {
			if (isEmpty(eItem.getExptAmt())) {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setExptAmt(nItem.getTaxablevalue());
				} else {
					eItem.setExptAmt(0d);
				}
			} else {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setExptAmt(eItem.getExptAmt() + nItem.getTaxablevalue());
				} else {
					eItem.setExptAmt(eItem.getExptAmt() + 0d);
				}
			}
			if (isEmpty(eItem.getNilAmt())) {
				eItem.setNilAmt(0d);
			} else {
				eItem.setNilAmt(eItem.getNilAmt() + 0d);
			}
			if (isEmpty(eItem.getNgsupAmt())) {
				eItem.setNgsupAmt(0d);
			} else {
				eItem.setNgsupAmt(eItem.getNgsupAmt() + 0d);
			}
		} else if (nItem.getType().equals("Non-GST")) {
			if (isEmpty(eItem.getNgsupAmt())) {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setNgsupAmt(nItem.getTaxablevalue());
				} else {
					eItem.setNgsupAmt(0d);
				}
			} else {
				if (isNotEmpty(nItem.getTaxablevalue())) {
					eItem.setNgsupAmt(eItem.getNgsupAmt() + nItem.getTaxablevalue());
				} else {
					eItem.setNgsupAmt(eItem.getNgsupAmt() + 0d);
				}
			}
			if (isEmpty(eItem.getNilAmt())) {
				eItem.setNilAmt(0d);
			} else {
				eItem.setNilAmt(eItem.getNilAmt() + 0d);
			}
			if (isEmpty(eItem.getExptAmt())) {
				eItem.setExptAmt(0d);
			} else {
				eItem.setExptAmt(eItem.getExptAmt() + 0d);
			}
		}
	}
	private List<GSTRDocListDetails> getIsdItem(PurchaseRegister invoice) {
		String isddocType = invoice.getIsd().get(0).getDoclist().get(0).getIsdDocty();
		String isddocno = invoice.getB2b().get(0).getInv().get(0).getInum();
		List<GSTRDocListDetails> doclist = Lists.newArrayList();
		for (Item item : invoice.getItems()) {
			GSTRDocListDetails isditems = new GSTRDocListDetails();
			isditems.setDocnum(isddocno);
			isditems.setDocdt(invoice.getDateofinvoice());
			isditems.setIsdDocty(isddocType);
			if (isNotEmpty(item.getIgstamount())) {
				isditems.setIamt(item.getIgstamount());
			}
			if (isNotEmpty(item.getCgstamount())) {
				isditems.setCamt(item.getCgstamount());
			}
			if (isNotEmpty(item.getSgstamount())) {
				isditems.setSamt(item.getSgstamount());
			}
			if (isNotEmpty(item.getCessamount())) {
				isditems.setCess(item.getIsdcessamount());
			}
			if (isNotEmpty(item.getIsdType())) {
				String isdtype = "Y";
				if (item.getIsdType().equalsIgnoreCase("Eligible - Credit distributed")
						|| item.getIsdType().equalsIgnoreCase("Eligible - Credit distributed as")) {
					isdtype = "Y";
				} else if (item.getIsdType().equalsIgnoreCase("Ineligible - Credit distributed")
						|| item.getIsdType().equalsIgnoreCase("Ineligible - Credit distributed as")) {
					isdtype = "N";
				}
				isditems.setItcElg(isdtype);
			}
			doclist.add(isditems);
		}
		return doclist;
	}

	private GSTRItemDetails getReversalItemByType(PurchaseRegister invoice, String itemType) {
		if (isEmpty(invoice.getItcRvsl())) {
			invoice.setItcRvsl(new GSTRITCReversals());
		}
		if (itemType.equals("rule2_2")) {
			if (isEmpty(invoice.getItcRvsl().getRule22())) {
				invoice.getItcRvsl().setRule22(new GSTRItemDetails());
			}
			return invoice.getItcRvsl().getRule22();
		} else if (itemType.equals("rule7_1_m")) {
			if (isEmpty(invoice.getItcRvsl().getRule71m())) {
				invoice.getItcRvsl().setRule71m(new GSTRItemDetails());
			}
			return invoice.getItcRvsl().getRule71m();
		} else if (itemType.equals("rule8_1_h")) {
			if (isEmpty(invoice.getItcRvsl().getRule81h())) {
				invoice.getItcRvsl().setRule81h(new GSTRItemDetails());
			}
			return invoice.getItcRvsl().getRule81h();
		} else if (itemType.equals("rule7_2_a")) {
			if (isEmpty(invoice.getItcRvsl().getRule72a())) {
				invoice.getItcRvsl().setRule72a(new GSTRItemDetails());
			}
			return invoice.getItcRvsl().getRule72a();
		} else if (itemType.equals("rule7_2_b")) {
			if (isEmpty(invoice.getItcRvsl().getRule72b())) {
				invoice.getItcRvsl().setRule72b(new GSTRItemDetails());
			}
			return invoice.getItcRvsl().getRule72b();
		} else if (itemType.equals("revitc")) {
			if (isEmpty(invoice.getItcRvsl().getRevitc())) {
				invoice.getItcRvsl().setRevitc(new GSTRItemDetails());
			}
			return invoice.getItcRvsl().getRevitc();
		} else if (itemType.equals("other")) {
			if (isEmpty(invoice.getItcRvsl().getOther())) {
				invoice.getItcRvsl().setOther(new GSTRItemDetails());
			}
			return invoice.getItcRvsl().getOther();
		}
		return null;
	}

	private List<GSTRItems> populateAdvanceItemData(InvoiceParent invoice, Item item, boolean isIntraState,
			String returntype) {
		String invType = invoice.getInvtype();
		List<GSTRItems> gstrItems = Lists.newArrayList();
		Double txvalue = 0d;
		if (invType.equals(MasterGSTConstants.ATPAID)) {
			if (isNotEmpty(item.getAdvadjustedAmount())) {
				txvalue = item.getAdvadjustedAmount();
			}

			if (returntype.equals(MasterGSTConstants.GSTR1)) {
				GSTR1 inv = gstr1Repository.findByClientidAndInvtypeAndInvoiceno(invoice.getClientid(),	MasterGSTConstants.ADVANCES, item.getAdvReceiptNo());
				Double advRemainingAmount = item.getAdvAdjustableAmount() - item.getTotal();
				if (isNotEmpty(inv)) {
					inv.setAdvRemainingAmount(advRemainingAmount);
					gstr1Repository.save((GSTR1) inv);
				} else {
						DateConverter converter = new DateConverter();
						String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd" };
						converter.setPatterns(patterns);
						ConvertUtils.register(converter, Date.class);
						GSTR1 advReceiptinv = new GSTR1();
						advReceiptinv.setInvtype(MasterGSTConstants.ADVANCES);
						advReceiptinv.setClientid(invoice.getClientid());
						advReceiptinv.setUserid(invoice.getUserid());
						if (isNotEmpty(item.getAdvReceiptNo())) {
							advReceiptinv.getB2b().get(0).getInv().get(0).setInum(item.getAdvReceiptNo());
						}
						if (isNotEmpty(item.getAdvStateName())) {
							advReceiptinv.setStatename(item.getAdvStateName());
						}
						if (isNotEmpty(item.getAdvReceiptDate())) {
							try {
								Date invDate = DateUtils.parseDate(item.getAdvReceiptDate(), patterns);
								advReceiptinv.setDateofinvoice(invDate);
							} catch (java.text.ParseException e) {
								try {
									double dblDate = Double.parseDouble(item.getAdvReceiptDate());
									Date invDate = DateUtil.getJavaDate(dblDate);
									advReceiptinv.setDateofinvoice(invDate);
								} catch (NumberFormatException exp) {
								}
							}
						}

						List<Item> items = Lists.newArrayList();
						Item advReceiptItem = new Item();
						if (isNotEmpty(item.getAdvReceivedAmount())) {
							advReceiptItem.setTotal(item.getAdvReceivedAmount());
						}
						if (isNotEmpty(item.getRate())) {
							advReceiptItem.setRate(item.getRate());
						}
						if (isNotEmpty(item.getCessrate())) {
							advReceiptItem.setCessrate(item.getCessrate());
						}
						Double advtaxableamt = 0d;
						if (isNotEmpty(item.getAdvReceivedAmount()) && isNotEmpty(item.getCessrate())) {
							advReceiptItem.setCessamount(item.getAdvReceivedAmount() * item.getCessrate() / 100);
							advtaxableamt += item.getAdvReceivedAmount() * item.getCessrate() / 100;
						}
						if (isIntraState) {
							if (isNotEmpty(item.getAdvReceivedAmount()) && isNotEmpty(item.getRate())) {
								advReceiptItem.setCgstamount(item.getAdvReceivedAmount() * item.getRate() / 200);
								advReceiptItem.setSgstamount(item.getAdvReceivedAmount() * item.getRate() / 200);
								advtaxableamt += item.getAdvReceivedAmount() * item.getRate() / 100;
							}
						} else {
							if (isNotEmpty(item.getAdvReceivedAmount()) && isNotEmpty(item.getRate())) {
								advReceiptItem.setIgstamount(item.getAdvReceivedAmount() * item.getRate() / 100);
								advtaxableamt += item.getAdvReceivedAmount() * item.getRate() / 100;
							}
						}
						if (isNotEmpty(item.getAdvReceivedAmount())) {
							advReceiptItem.setTaxablevalue(item.getAdvReceivedAmount() - advtaxableamt);
						} else {
							advReceiptItem.setTaxablevalue(0d);
						}
						items.add(advReceiptItem);
					
				}
			} else if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)	|| returntype.equals(MasterGSTConstants.GSTR2)) {
				PurchaseRegister inv = purchaseRepository.findByClientidAndInvtypeAndInvoiceno(invoice.getClientid(), MasterGSTConstants.ADVANCES, item.getAdvReceiptNo());
				Double advRemainingAmount = item.getAdvAdjustableAmount() - item.getTotal();
				if (isNotEmpty(inv)) {
					inv.setAdvRemainingAmount(advRemainingAmount);
					purchaseRepository.save((PurchaseRegister) inv);
				} else {
						DateConverter converter = new DateConverter();
						String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd" };
						converter.setPatterns(patterns);
						ConvertUtils.register(converter, Date.class);
						PurchaseRegister advReceiptinv = new PurchaseRegister();
						advReceiptinv.setInvtype(MasterGSTConstants.ADVANCES);
						advReceiptinv.setClientid(invoice.getClientid());
						advReceiptinv.setUserid(invoice.getUserid());
						if (isNotEmpty(item.getAdvReceiptNo())) {
							advReceiptinv.getB2b().get(0).getInv().get(0).setInum(item.getAdvReceiptNo());
						}
						if (isNotEmpty(item.getAdvStateName())) {
							advReceiptinv.setStatename(item.getAdvStateName());
						}
						if (isNotEmpty(item.getAdvReceiptDate())) {
							try {
								Date invDate = DateUtils.parseDate(item.getAdvReceiptDate(), patterns);
								advReceiptinv.setDateofinvoice(invDate);
								advReceiptinv.setBillDate(invDate);
							} catch (java.text.ParseException e) {
								try {
									double dblDate = Double.parseDouble(item.getAdvReceiptDate());
									Date invDate = DateUtil.getJavaDate(dblDate);
									advReceiptinv.setDateofinvoice(invDate);
									advReceiptinv.setBillDate(invDate);
								} catch (NumberFormatException exp) {
								}
							}
						}
						List<Item> items = Lists.newArrayList();
						Item advReceiptItem = new Item();
						if (isNotEmpty(item.getAdvReceivedAmount())) {
							advReceiptItem.setTotal(item.getAdvReceivedAmount());
						}
						if (isNotEmpty(item.getRate())) {
							advReceiptItem.setRate(item.getRate());
						}
						if (isNotEmpty(item.getCessrate())) {
							advReceiptItem.setCessrate(item.getCessrate());
						}
						Double advtaxableamt = 0d;
						if (isNotEmpty(item.getAdvReceivedAmount()) && isNotEmpty(item.getCessrate())) {
							advReceiptItem.setCessamount(item.getAdvReceivedAmount() * item.getCessrate() / 100);
							advtaxableamt += item.getAdvReceivedAmount() * item.getCessrate() / 100;
						}
						if (isIntraState) {
							if (isNotEmpty(item.getAdvReceivedAmount()) && isNotEmpty(item.getRate())) {
								advReceiptItem.setCgstamount(item.getAdvReceivedAmount() * item.getRate() / 200);
								advReceiptItem.setSgstamount(item.getAdvReceivedAmount() * item.getRate() / 200);
								advtaxableamt += item.getAdvReceivedAmount() * item.getRate() / 100;
							}
						} else {
							if (isNotEmpty(item.getAdvReceivedAmount()) && isNotEmpty(item.getRate())) {
								advReceiptItem.setIgstamount(item.getAdvReceivedAmount() * item.getRate() / 100);
								advtaxableamt += item.getAdvReceivedAmount() * item.getRate() / 100;
							}
						}
						if (isNotEmpty(item.getAdvReceivedAmount())) {
							advReceiptItem.setTaxablevalue(item.getAdvReceivedAmount() - advtaxableamt);
						} else {
							advReceiptItem.setTaxablevalue(0d);
						}
						items.add(advReceiptItem);
						advReceiptinv.setItems(items);
					}
			}
		} else {
			if (isNotEmpty(item.getTaxablevalue())) {
				txvalue = item.getTaxablevalue();
			}
		}
		if (isNotEmpty(txvalue)) {
			GSTRItems gstrItem = new GSTRItems();
			GSTRItemDetails gstrItemDetail = new GSTRItemDetails();
			GSTRITCDetails gstrITCDetail = new GSTRITCDetails();
			Map<String, String> hsnMap = configService.getHSNMap();
			Map<String, String> sacMap = configService.getSACMap();
			if (isNotEmpty(item.getHsn())) {
				String code = null;
				String description = null;
				if (item.getHsn().contains(" : ")) {
					String hsncode[] = item.getHsn().split(" : ");
					code = hsncode[0];
					description = hsncode[1];
				} else {
					code = item.getHsn();
				}

				if (hsnMap.containsKey(code)) {
					item.setCategory(MasterGSTConstants.GOODS);
				} else if (hsnMap.containsValue(code)) {
					item.setCategory(MasterGSTConstants.GOODS);
				}
				if (isEmpty(description)) {
					for (String key : hsnMap.keySet()) {
						if (hsnMap.get(key).endsWith(" : " + code)) {
							item.setCategory(MasterGSTConstants.GOODS);
							break;
						}
					}
				}
				if (isEmpty(item.getUqc())) {
					item.setUqc("");
				}
				if (isEmpty(item.getRateperitem())) {
					item.setRateperitem(0d);
				}
				if (isEmpty(item.getDiscount())) {
					item.setDiscount(0d);
				}
				if (isEmpty(item.getQuantity())) {
					item.setQuantity(0d);
				}
				if (MasterGSTConstants.ADVANCES.equals(invoice.getInvtype())) {
					if (isNotEmpty(item.getTotal())) {
						item.setAdvreceived(item.getTotal());
					}
				}
				if (sacMap.containsKey(code)) {
					item.setCategory(MasterGSTConstants.SERVICES);
				} else if (sacMap.containsValue(code)) {
					item.setCategory(MasterGSTConstants.SERVICES);
				}
				if (isEmpty(description)) {
					for (String key : sacMap.keySet()) {
						if (sacMap.get(key).endsWith(" : " + code)) {
							item.setCategory(MasterGSTConstants.SERVICES);
							break;
						}
					}
				}
			}
			if (isIntraState) {
				if (isNotEmpty(item.getCgstrate()) && item.getCgstrate() > 0 && isNotEmpty(item.getSgstrate())	&& item.getSgstrate() > 0) {
					gstrItemDetail.setRt(item.getCgstrate() + item.getSgstrate());
					item.setRate(item.getCgstrate() + item.getSgstrate());
				} else if (isNotEmpty(item.getIgstrate()) && item.getIgstrate() > 0) {
					gstrItemDetail.setRt(item.getIgstrate());
					item.setRate(item.getIgstrate());
				} else if (isEmpty(gstrItemDetail.getRt()) && isNotEmpty(item.getRate()) && item.getRate() > 0) {
					gstrItemDetail.setRt(item.getRate());
					if (invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)|| (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) &&("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWPC".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWOP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())))) {
						item.setIgstrate(item.getRate());
					}else {
						if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
							item.setIgstrate(item.getRate());
						} else {
							item.setCgstrate(item.getRate() / 2);
							item.setSgstrate(item.getRate() / 2);
						}
					}
				} else {
					gstrItemDetail.setRt(0d);
					if (invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)|| (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) &&("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWPC".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWOP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())))) {
						item.setIgstrate(0d);
					}else {
						if(isIntraState) {
							item.setCgstrate(0d);
							item.setSgstrate(0d);
						}else {
							item.setIgstrate(0d);
						}
					}
				}

				if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())	&& !"R".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
					if ("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						gstrItemDetail.setCamt(item.getIgstamount() / 2);
						gstrItemDetail.setSamt(item.getIgstamount() / 2);
					} else if ("CWB".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						gstrItemDetail.setIamt(item.getIgstamount());
					} else {
						gstrItemDetail.setCamt(item.getCgstamount());
						gstrItemDetail.setSamt(item.getSgstamount());
					}
				} else {
					if (isNotEmpty(item.getCgstamount()) && item.getCgstamount() > 0 && isNotEmpty(item.getSgstamount()) && item.getSgstamount() > 0) {
						if (invoice.getInvtype().equals(EXPORTS)) {
							gstrItemDetail.setIamt(0d);
						} else {
							gstrItemDetail.setCamt(item.getCgstamount());
							gstrItemDetail.setSamt(item.getSgstamount());
						}
					} else if (isNotEmpty(item.getIgstamount())) {
						gstrItemDetail.setIamt(item.getIgstamount());
					}
				}

				if (isNotEmpty(item.getCgstavltax())) {
					gstrITCDetail.setcTax(item.getCgstavltax());
				}
				if (isNotEmpty(item.getSgstavltax())) {
					gstrITCDetail.setsTax(item.getSgstavltax());
				}
			} else {
				if (isNotEmpty(item.getIgstrate()) && item.getIgstrate() > 0) {
					gstrItemDetail.setRt(item.getIgstrate());
					item.setRate(item.getIgstrate());
				} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate()) && item.getCgstrate() > 0) {
					gstrItemDetail.setRt(item.getCgstrate() + item.getSgstrate());
					item.setRate(item.getCgstrate() + item.getSgstrate());
				} else if (isEmpty(gstrItemDetail.getRt()) && isNotEmpty(item.getRate()) && item.getRate() > 0) {
					gstrItemDetail.setRt(item.getRate());
					if (invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)|| (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) &&("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWPC".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWOP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())))) {
						item.setIgstrate(item.getRate());
					}else {
						if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
							item.setIgstrate(item.getRate());
						} else {
							item.setCgstrate(item.getRate() / 2);
							item.setSgstrate(item.getRate() / 2);
						}
					}
				} else {
					gstrItemDetail.setRt(0d);
					if (invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)|| (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) &&("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWPC".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWOP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())))) {
						item.setIgstrate(0d);
					}else {
						if(isIntraState) {
							item.setCgstrate(0d);
							item.setSgstrate(0d);
						}else {
							item.setIgstrate(0d);
						}
					}
				}
				if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())
						&& !"R".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
					if ("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						gstrItemDetail.setIamt(item.getIgstamount());
					} else if ("CWB".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						gstrItemDetail.setIamt(item.getIgstamount());
					} else {
						gstrItemDetail.setIamt(item.getIgstamount());
					}
				} else {
					if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
						gstrItemDetail.setIamt(item.getIgstamount());
					} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
						if (invoice.getInvtype().equals(EXPORTS)) {
							gstrItemDetail.setIamt(0d);
						} else {
							gstrItemDetail.setCamt(item.getCgstamount());
							gstrItemDetail.setSamt(item.getSgstamount());
						}
					}
				}
				if (isNotEmpty(item.getIgstavltax())) {
					gstrITCDetail.setiTax(item.getIgstavltax());
				}
			}
			if (isNotEmpty(item.getCessamount())) {
				gstrItemDetail.setCsamt(item.getCessamount());
			}
			if (isNotEmpty(item.getCessavltax())) {
				gstrITCDetail.setCsTax(item.getCessavltax());
			}
			if (isNotEmpty(item.getElg())) {
				if (item.getElg().startsWith("Input")) {
					item.setElg("ip");
				} else if (item.getElg().startsWith("Capital")) {
					item.setElg("cp");
				} else if (item.getElg().startsWith("In")) {
					item.setElg("is");
				} else if (item.getElg().startsWith("Not")) {
					item.setElg("no");
				}
				gstrITCDetail.setElg(item.getElg());
			}
			if (isNotEmpty(item.getTaxablevalue())) {
				gstrItemDetail.setTxval(item.getTaxablevalue());
			}
			if (isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equals(ATPAID) || invoice.getInvtype().equals(ADVANCES) || invoice.getInvtype().equals(MasterGSTConstants.ATA) || invoice.getInvtype().equals(MasterGSTConstants.TXPA))) {
				if (isNotEmpty(item.getTotal())) {
					gstrItemDetail.setAdvAmt(item.getTotal());
				}
			}
			gstrItem.setItem(gstrItemDetail);
			if (isNotEmpty(gstrITCDetail.getElg())) {
				gstrItem.setItc(gstrITCDetail);
			}
			gstrItem.setNum(gstrItems.size() + 1);
			gstrItems.add(gstrItem);

		}
		return gstrItems;
	}
	private List<GSTRItems> populateItemData(InvoiceParent invoice, boolean isIntraState, String returntype,Map<String, String> hsnMap,Map<String, String> sacMap) {
		Double totalAmount = 0d;
		Double totalTax = 0d;
		Double totalTaxableAmt = 0d;
		Double totalITC = 0d;
		Double totalCurrencyAmount = 0d;
		String invType = invoice.getInvtype();
		List<GSTRItems> gstrItems = Lists.newArrayList();
		for (Item item : invoice.getItems()) {
			Double txvalue = 0d;
			if (invType.equals(MasterGSTConstants.ATPAID)) {
				if (isNotEmpty(item.getAdvadjustedAmount())) {
					txvalue = item.getAdvadjustedAmount();
				}
				GSTR1 inv = gstr1Repository.findByClientidAndInvtypeAndInvoiceno(invoice.getClientid(),
						MasterGSTConstants.ADVANCES, item.getAdvReceiptNo());
				Double advRemainingAmount = item.getAdvAdjustableAmount() - item.getAdvadjustedAmount();
				inv.setAdvRemainingAmount(advRemainingAmount);
				gstr1Repository.save((GSTR1) inv);
			} else {
				if (isNotEmpty(item.getTaxablevalue())) {
					txvalue = item.getTaxablevalue();
				}
			}
			if (isNotEmpty(txvalue)) {
				GSTRItems gstrItem = new GSTRItems();
				GSTRItemDetails gstrItemDetail = new GSTRItemDetails();
				GSTRITCDetails gstrITCDetail = new GSTRITCDetails();
				if (isNotEmpty(item.getHsn())) {
					String code = null;
					String description = null;
					if (item.getHsn().contains(" : ")) {
						String hsncode[] = item.getHsn().split(" : ");
						code = hsncode[0];
						description = hsncode[1];
					} else {
						code = item.getHsn();
					}

					if (hsnMap.containsKey(code)) {
						item.setCategory(MasterGSTConstants.GOODS);
					} else if (hsnMap.containsValue(code)) {
						item.setCategory(MasterGSTConstants.GOODS);
					}else if (sacMap.containsKey(code)) {
						item.setCategory(MasterGSTConstants.SERVICES);
					} else if (sacMap.containsValue(code)) {
						item.setCategory(MasterGSTConstants.SERVICES);
					}else if (isEmpty(description)) {
						for (String key : hsnMap.keySet()) {
							if (hsnMap.get(key).endsWith(" : " + code)) {
								item.setCategory(MasterGSTConstants.GOODS);
								break;
							}
						}
					}else if (isEmpty(description)) {
						for (String key : sacMap.keySet()) {
							if (sacMap.get(key).endsWith(" : " + code)) {
								item.setCategory(MasterGSTConstants.SERVICES);
								break;
							}
						}
					}
					if (isEmpty(item.getUqc())) {
						item.setUqc("");
					}

					if (isEmpty(item.getRateperitem())) {
						item.setRateperitem(0d);
					}

					if (isEmpty(item.getDiscount())) {
						item.setDiscount(0d);
					}

					if (isEmpty(item.getQuantity())) {
						item.setQuantity(0d);
					}
					if (MasterGSTConstants.ADVANCES.equals(invoice.getInvtype())) {
						if (isNotEmpty(item.getTotal())) {
							item.setAdvreceived(item.getTotal());
						}
					}
					
				}
				if (isIntraState) {
					if (isNotEmpty(item.getCgstrate()) && item.getCgstrate() > 0 && isNotEmpty(item.getSgstrate()) && item.getSgstrate() > 0) {
						gstrItemDetail.setRt(item.getCgstrate() + item.getSgstrate());
						item.setRate(item.getCgstrate() + item.getSgstrate());
					} else if (isNotEmpty(item.getIgstrate()) && item.getIgstrate() > 0) {
						gstrItemDetail.setRt(item.getIgstrate());
						item.setRate(item.getIgstrate());
					} else if (isEmpty(gstrItemDetail.getRt()) && isNotEmpty(item.getRate()) && item.getRate() > 0) {
						gstrItemDetail.setRt(item.getRate());
						if (invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)|| (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) &&("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWPC".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWOP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())))) {
							item.setIgstrate(item.getRate());
						}else {
							if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
								item.setIgstrate(item.getRate());
							} else {
								item.setCgstrate(item.getRate() / 2);
								item.setSgstrate(item.getRate() / 2);
							}
						}
					} else {
						gstrItemDetail.setRt(0d);
						if (invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)|| (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) &&("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWPC".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWOP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())))) {
							item.setIgstrate(0d);
						}else {
							if(isIntraState) {
								item.setCgstrate(0d);
								item.setSgstrate(0d);
							}else {
								item.setIgstrate(0d);
							}
						}
					}

					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())	&& !"R".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						if ("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
							gstrItemDetail.setCamt(item.getIgstamount() / 2);
							gstrItemDetail.setSamt(item.getIgstamount() / 2);
						} else if ("CWB".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
							gstrItemDetail.setIamt(item.getIgstamount());
						} else {
							gstrItemDetail.setCamt(item.getCgstamount());
							gstrItemDetail.setSamt(item.getSgstamount());
						}
					} else {
						if (!invoice.getInvtype().equals(MasterGSTConstants.ADVANCES)) {
							if (isNotEmpty(item.getCgstamount()) && item.getCgstamount() >= 0 && isNotEmpty(item.getSgstamount()) && item.getSgstamount() >= 0) {
								if (invoice.getInvtype().equals(EXPORTS)) {
									gstrItemDetail.setIamt(0d);
								} else {
									gstrItemDetail.setCamt(item.getCgstamount());
									gstrItemDetail.setSamt(item.getSgstamount());
								}
							} else if (isNotEmpty(item.getIgstamount())) {
								gstrItemDetail.setIamt(item.getIgstamount());
							}
						}else {
							if (isNotEmpty(item.getCgstamount())  && isNotEmpty(item.getSgstamount())) {
								gstrItemDetail.setCamt(item.getCgstamount());
								gstrItemDetail.setSamt(item.getSgstamount());
								
							} else if (isNotEmpty(item.getIgstamount())) {
								gstrItemDetail.setIamt(item.getIgstamount());
							}
						}
					}

					if (isNotEmpty(item.getCgstavltax())) {
						gstrITCDetail.setcTax(item.getCgstavltax());
					}
					if (isNotEmpty(item.getSgstavltax())) {
						gstrITCDetail.setsTax(item.getSgstavltax());
					}
				} else {
					if (isNotEmpty(item.getIgstrate()) && item.getIgstrate() > 0) {
						gstrItemDetail.setRt(item.getIgstrate());
						item.setRate(item.getIgstrate());
					} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate()) && item.getCgstrate() > 0) {
						gstrItemDetail.setRt(item.getCgstrate() + item.getSgstrate());
						item.setRate(item.getCgstrate() + item.getSgstrate());
					} else if (isEmpty(gstrItemDetail.getRt()) && isNotEmpty(item.getRate()) && item.getRate() > 0) {
						gstrItemDetail.setRt(item.getRate());
						if (invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)|| (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) &&("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWPC".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWOP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())))) {
							item.setIgstrate(item.getRate());
						}else {
							if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
								item.setIgstrate(item.getRate());
							} else {
								item.setCgstrate(item.getRate() / 2);
								item.setSgstrate(item.getRate() / 2);
							}
						}
					} else {
						gstrItemDetail.setRt(0d);
						if (invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)|| (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) &&("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWPC".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWOP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())))) {
							item.setIgstrate(0d);
						}else {
							if(isIntraState) {
								item.setCgstrate(0d);
								item.setSgstrate(0d);
							}else {
								item.setIgstrate(0d);
							}
						}
					}
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())	&& !"R".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						if ("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
							gstrItemDetail.setIamt(item.getIgstamount());
						} else if ("CWB".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
							gstrItemDetail.setIamt(item.getIgstamount());
						} else {
							gstrItemDetail.setIamt(item.getIgstamount());
						}
					} else {
						if (!invoice.getInvtype().equals(MasterGSTConstants.ADVANCES)) {
							if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() >= 0) {
								gstrItemDetail.setIamt(item.getIgstamount());
							} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
								if (invoice.getInvtype().equals(EXPORTS)) {
									gstrItemDetail.setIamt(0d);
								} else {
									gstrItemDetail.setCamt(item.getCgstamount());
									gstrItemDetail.setSamt(item.getSgstamount());
								}
							}
						}else {
							if (isNotEmpty(item.getIgstamount())) {
								gstrItemDetail.setIamt(item.getIgstamount());
							} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
								gstrItemDetail.setCamt(item.getCgstamount());
								gstrItemDetail.setSamt(item.getSgstamount());
								
							}
						}
					}
					if (isNotEmpty(item.getIgstavltax())) {
						gstrITCDetail.setiTax(item.getIgstavltax());
					}
				}
				if (isNotEmpty(item.getCessamount())) {
					gstrItemDetail.setCsamt(item.getCessamount());
				}
				if (isNotEmpty(item.getCessavltax())) {
					gstrITCDetail.setCsTax(item.getCessavltax());
				}
				if (isNotEmpty(item.getElg())) {
					if (item.getElg().startsWith("Input")) {
						item.setElg("ip");
					} else if (item.getElg().startsWith("Capital")) {
						item.setElg("cp");
					} else if (item.getElg().startsWith("In")) {
						item.setElg("is");
					} else if (item.getElg().startsWith("Not")) {
						item.setElg("no");
					}
					gstrITCDetail.setElg(item.getElg());
				}
				if (isNotEmpty(item.getTaxablevalue())) {
					gstrItemDetail.setTxval(item.getTaxablevalue());
				}
				if (isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equals(ATPAID) || invoice.getInvtype().equals(ADVANCES) || invoice.getInvtype().equals(MasterGSTConstants.ATA) || invoice.getInvtype().equals(MasterGSTConstants.TXPA))) {
						if (isNotEmpty(item.getTaxablevalue())) {
							gstrItemDetail.setAdvAmt(item.getTaxablevalue());
						}
					
				}
				gstrItem.setItem(gstrItemDetail);
				if (returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
					if (isNotEmpty(gstrITCDetail.getElg())) {
						gstrItem.setItc(gstrITCDetail);
					}
				}
				gstrItem.setNum(gstrItems.size() + 1);
				gstrItems.add(gstrItem);
				if (isNotEmpty(item.getCgstamount())) {
					totalTax += item.getCgstamount();
				}
				if (isNotEmpty(item.getIgstamount())) {
					totalTax += item.getIgstamount();
				}
				if (isNotEmpty(item.getSgstamount())) {
					totalTax += item.getSgstamount();
				}
				if (isNotEmpty(item.getCessamount())) {
					totalTax += item.getCessamount();
				}
				if (isNotEmpty(gstrITCDetail.getiTax())) {
					totalITC += gstrITCDetail.getiTax();
				}
				if (isNotEmpty(gstrITCDetail.getcTax())) {
					totalITC += gstrITCDetail.getcTax();
				}
				if (isNotEmpty(gstrITCDetail.getsTax())) {
					totalITC += gstrITCDetail.getsTax();
				}
				if (isNotEmpty(gstrITCDetail.getCsTax())) {
					totalITC += gstrITCDetail.getCsTax();
				}
				if (isNotEmpty(item.getTotal())) {
					totalAmount += item.getTotal();
				}
				if (isNotEmpty(item.getCurrencytotalAmount())) {
					totalCurrencyAmount += item.getCurrencytotalAmount();
				}
				if (isNotEmpty(item.getTaxablevalue())) {
					totalTaxableAmt += item.getTaxablevalue();
				}
				if (isNotEmpty(item.getAdditionalchargevalue())) {
					totalTaxableAmt += item.getAdditionalchargevalue();
				}
			}
		}

		if (isNotEmpty(invoice.getRoundOffAmount())) {
			invoice.setTotalamount(totalAmount + invoice.getRoundOffAmount());
		} else {
			invoice.setRoundOffAmount(0.0);
			invoice.setTotalamount(totalAmount);
		}
		if(isNotEmpty(totalCurrencyAmount)) {
			invoice.setTotalCurrencyAmount(totalCurrencyAmount);
		}
		
		invoice.setTotaltax(totalTax);
		invoice.setTotaltaxableamount(totalTaxableAmt);
		invoice.setTotalitc(totalITC);
		return gstrItems;
	}
	@Async
	private void saveAdvancePayments(InvoiceParent invoice, String returntype) {
		SimpleDateFormat pmntDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Payments payments = new Payments();
		payments.setClientid(invoice.getClientid());
		payments.setUserid(invoice.getUserid());
		if(isNotEmpty(invoice.getId())) {
			payments.setInvoiceid(invoice.getId().toString());
		}
		if(isNotEmpty(invoice.getBilledtoname())) {
			payments.setCustomerName(invoice.getBilledtoname());
		}else {
			payments.setCustomerName("");
		}
		if(isNotEmpty(invoice.getB2b().get(0).getCtin())) {
			payments.setGstNumber(invoice.getB2b().get(0).getCtin());
		}
		payments.setInvoiceNumber(invoice.getInvoiceno());
		payments.setVoucherNumber(UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0,6)+"/"+invoice.getInvoiceno());
		payments.setInvtype("Advances");
		
		payments.setPaymentDate(pmntDateFormat.format(invoice.getDateofinvoice()));
		payments.setReturntype(returntype);
		payments.setReceivedAmount(invoice.getTotalamount().toString());
		
		payments.setCashAmount(invoice.getTotalamount());
		payments.setPaidAmount(invoice.getTotalamount());
		payments.setBankAmount(0d);
		payments.setTdsItAmount(0d);
		payments.setTdsGstAmount(0d);
		payments.setDiscountAmount(0d);
		payments.setOthersAmount(0d);
		
		payments.setPreviousPendingBalance(invoice.getTotalamount());
		payments.setPendingBalance(0d);
		
		PaymentItems advpmntitms = new PaymentItems();
		List<PaymentItems> pmntitems = Lists.newArrayList();
		advpmntitms.setId(new ObjectId());
		advpmntitms.setModeOfPayment("Cash");
		advpmntitms.setAmount(invoice.getTotalamount());
		advpmntitms.setReferenceNumber("");
		advpmntitms.setPendingBalance(invoice.getTotalamount());
		advpmntitms.setLedger("Cash");
		pmntitems.add(advpmntitms);
		payments.setPaymentitems(pmntitems);
		
		Payments payment = clientService.saveRecordPayments(payments);
		AccountingJournal journal=new AccountingJournal();
		
		journal.setClientId(invoice.getClientid());
		journal.setUserId(invoice.getUserid());
		journal.setInvoiceNumber(payment.getVoucherNumber());
		journal.setInvoiceId(payment.getId().toString());
		if(isNotEmpty(invoice.getVendorName())) {
			journal.setVendorName(invoice.getVendorName());					
		}else {
			if(returntype.equals("GSTR1") || returntype.equals("SalesRegister")) {
				journal.setVendorName(AccountConstants.OTHER_DEBTORS);
			}else if(returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER) || returntype.equals("PurchaseRegister")) {
				journal.setVendorName(AccountConstants.OTHER_CREDITORS);
			}
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		String ptDate[] = payment.getPaymentDate().split("-");
		String strmonth = ptDate[1]+ptDate[2];
		journal.setInvoiceMonth(strmonth);
		try {
			journal.setDateofinvoice(dateFormat.parse(payment.getPaymentDate()));
		} catch (ParseException e) {
		}
		Double amountReceived = 0d;
		if(isNotEmpty(payment.getPreviousPendingBalance()) && isNotEmpty(payment.getPendingBalance())) {
				amountReceived = payment.getPreviousPendingBalance() - payment.getPendingBalance();
		}
		journal.setPaymentReceivedAmount(amountReceived);
		List<PaymentItems> paymentItems = payment.getPaymentitems();
		List<AccountingJournalPaymentItems> journalItems = Lists.newArrayList();
		Map<String, AccountingJournalPaymentItems> acpaymentjournal = Maps.newHashMap(); 
		if(isNotEmpty(paymentItems)) {
			for(PaymentItems payItem : paymentItems) {
				if(acpaymentjournal.size() == 0) {
					AccountingJournalPaymentItems acpaymentitem = new AccountingJournalPaymentItems();
					acpaymentitem.setLedgerName(payItem.getLedger());
					acpaymentitem.setAmount(payItem.getAmount());
					acpaymentjournal.put(payItem.getLedger(), acpaymentitem);
				}else {
					if(isNotEmpty(acpaymentjournal.get(payItem.getLedger()))) {
						AccountingJournalPaymentItems acpaymentitem = acpaymentjournal.get(payItem.getLedger());
						acpaymentitem.setAmount(acpaymentitem.getAmount() + payItem.getAmount());
						acpaymentjournal.put(payItem.getLedger(), acpaymentitem);
					}else {
						AccountingJournalPaymentItems acpaymentitem = new AccountingJournalPaymentItems();
						acpaymentitem.setLedgerName(payItem.getLedger());
						acpaymentitem.setAmount(payItem.getAmount());
						acpaymentjournal.put(payItem.getLedger(), acpaymentitem);
					}
				}
			}
		}
		 for (Map.Entry<String,AccountingJournalPaymentItems> entry : acpaymentjournal.entrySet()) {
			 journalItems.add(entry.getValue());
		 }
		 journal.setNoofpayments(journalItems.size());
		 journal.setPaymentitems(journalItems);
		 Double creditDebitTotal = 0d;
		 for(AccountingJournalPaymentItems items : journalItems) {
			 creditDebitTotal += items.getAmount() == null ? 0.0 : items.getAmount();
		 }
		 if(returntype.equals("GSTR1") || returntype.equals("SalesRegister")) {
			 journal.setReturnType("Payment Receipt");
			journal.setDebitTotal(creditDebitTotal);
			journal.setCreditTotal(amountReceived);
		 }else if(returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER) || returntype.equals("PurchaseRegister")) {
			 journal.setReturnType("Payment");
			journal.setCreditTotal(creditDebitTotal);
			journal.setDebitTotal(amountReceived);
		 }
		 accountingJournalRepository.save(journal);
		
		
	}
	
	public void saveJournalInvoice(InvoiceParent invoiceForJournal, String clientid, String returntype,boolean isIntraState,String journalnumber) {
		if (isNotEmpty(invoiceForJournal)) {
			if ("Sales Register".equalsIgnoreCase(returntype) || "SalesRegister".equalsIgnoreCase(returntype)
					|| "GSTR1".equalsIgnoreCase(returntype)) {
				String dealertype = "";
				if (isNotEmpty(invoiceForJournal.getDealerType())) {
					dealertype = invoiceForJournal.getDealerType();
				}
				AccountingJournal journal = accountingJournalRepository.findByInvoiceId(invoiceForJournal.getId().toString());
				if (isEmpty(journal)) {
					journal = new AccountingJournal();
				}
				if (isNotEmpty(invoiceForJournal.getDateofinvoice())) {
					journal.setDateofinvoice(invoiceForJournal.getDateofinvoice());
					Calendar cal = Calendar.getInstance();
					cal.setTime(invoiceForJournal.getDateofinvoice());
					int month = cal.get(Calendar.MONTH) + 1;
					int year = cal.get(Calendar.YEAR);
					String strMonth = month < 10 ? "0" + month : month + "";
					journal.setInvoiceMonth(strMonth + year);
				}
				if(isNotEmpty(invoiceForJournal.getMthCd())) {
					journal.setMthCd(invoiceForJournal.getMthCd());
				}
				if(isNotEmpty(invoiceForJournal.getYrCd())) {
					journal.setYrCd(invoiceForJournal.getYrCd());
				}
				if(isNotEmpty(invoiceForJournal.getQrtCd())) {
					journal.setQrtCd(invoiceForJournal.getQrtCd());
				}
				
				if(isNotEmpty(invoiceForJournal.getRoundOffAmount())) {
					journal.setRoundOffAmount(invoiceForJournal.getRoundOffAmount());
				}else {
					journal.setRoundOffAmount(0d);
				}
				
				if (isNotEmpty(invoiceForJournal.getUserid())) {
					journal.setUserId(invoiceForJournal.getUserid());
				}
				if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
					if(isNotEmpty(invoiceForJournal.getAdvPCustname())) {
						journal.setVendorName(invoiceForJournal.getAdvPCustname());
					}else {
						journal.setVendorName(AccountConstants.OTHER_DEBTORS);							
					}
				}else { 
					if (isNotEmpty(invoiceForJournal.getVendorName())) {
						if (isNotEmpty(invoiceForJournal.getInvoiceEcomGSTIN())
								&& isNotEmpty(invoiceForJournal.getInvoiceEcomOperator())) {
							String ecomname = invoiceForJournal.getInvoiceEcomOperator() + "("
									+ invoiceForJournal.getInvoiceEcomGSTIN() + " - "
									+ invoiceForJournal.getVendorName() + ")";
							journal.setVendorName(ecomname);
						} else if (isNotEmpty(invoiceForJournal.getInvoiceEcomGSTIN())) {
							String ecomname = invoiceForJournal.getInvoiceEcomGSTIN() + "("
									+ invoiceForJournal.getVendorName() + ")";
							journal.setVendorName(ecomname);
						} else if (isNotEmpty(invoiceForJournal.getInvoiceEcomOperator())) {
							String ecomname = invoiceForJournal.getInvoiceEcomOperator() + "("
									+ invoiceForJournal.getVendorName() + ")";
							journal.setVendorName(ecomname);
						} else {
							journal.setVendorName(invoiceForJournal.getVendorName());
						}
					} else if (isNotEmpty(invoiceForJournal.getBilledtoname())) {
						if (isNotEmpty(invoiceForJournal.getInvoiceEcomGSTIN())
								&& isNotEmpty(invoiceForJournal.getInvoiceEcomOperator())) {
							String ecomname = invoiceForJournal.getInvoiceEcomOperator() + "("
									+ invoiceForJournal.getInvoiceEcomGSTIN() + " - " + invoiceForJournal.getBilledtoname()
									+ ")";
							journal.setVendorName(ecomname);
						} else if (isNotEmpty(invoiceForJournal.getInvoiceEcomGSTIN())) {
							String ecomname = invoiceForJournal.getInvoiceEcomGSTIN() + "("
									+ invoiceForJournal.getBilledtoname() + ")";
							journal.setVendorName(ecomname);
						} else if (isNotEmpty(invoiceForJournal.getInvoiceEcomOperator())) {
							String ecomname = invoiceForJournal.getInvoiceEcomOperator() + "("
									+ invoiceForJournal.getBilledtoname() + ")";
							journal.setVendorName(ecomname);
						} else {
							journal.setVendorName(invoiceForJournal.getBilledtoname());
						}
					} else {
						if (isNotEmpty(invoiceForJournal.getInvoiceEcomGSTIN())
								&& isNotEmpty(invoiceForJournal.getInvoiceEcomOperator())) {
							String ecomname = invoiceForJournal.getInvoiceEcomOperator() + "("
									+ invoiceForJournal.getInvoiceEcomGSTIN() + " - " + AccountConstants.OTHER_DEBTORS +")";
							journal.setVendorName(ecomname);
						} else if (isNotEmpty(invoiceForJournal.getInvoiceEcomGSTIN())) {
							String ecomname = invoiceForJournal.getInvoiceEcomGSTIN() + "("+AccountConstants.OTHER_DEBTORS+")";
							journal.setVendorName(ecomname);
						} else if (isNotEmpty(invoiceForJournal.getInvoiceEcomOperator())) {
							String ecomname = invoiceForJournal.getInvoiceEcomOperator() + "("+AccountConstants.OTHER_DEBTORS+")";
							journal.setVendorName(ecomname);
						} else {
							journal.setVendorName(AccountConstants.OTHER_DEBTORS);
						}
					}
				}

				if (isNotEmpty(invoiceForJournal.getClientid())) {
					journal.setClientId(invoiceForJournal.getClientid());
				}
				if (isNotEmpty(invoiceForJournal.getInvtype())) {
					journal.setInvoiceType(invoiceForJournal.getInvtype());
					if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(invoiceForJournal.getInvtype())) {
						if (isNotEmpty(((GSTR1) invoiceForJournal).getCdnr().get(0).getNt().get(0).getNtty())) {
							String docType = ((GSTR1) invoiceForJournal).getCdnr().get(0).getNt().get(0).getNtty();
							journal.setCreditDebitNoteType(docType);
						}
					} else if (MasterGSTConstants.CDNUR.equalsIgnoreCase(invoiceForJournal.getInvtype())) {
						if (isNotEmpty(((GSTR1) invoiceForJournal).getCdnur().get(0).getNtty())) {
							String docType = invoiceForJournal.getCdnur().get(0).getNtty();
							journal.setCreditDebitNoteType(docType);
						}
					}
					if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
						if(isNotEmpty(invoiceForJournal.getAdvPCustname())) {
							journal.setLedgerName(invoiceForJournal.getAdvPCustname());
						}else {
							journal.setLedgerName(AccountConstants.OTHER_DEBTORS);							
						}
					}else {
						if (isEmpty(invoiceForJournal.getLedgerName())) {
							if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
								journal.setLedgerName("Cash");
							} else {
								journal.setLedgerName("Sales");
							}
						} else {
							journal.setLedgerName(invoiceForJournal.getLedgerName());
						}
					}

				}
				journal.setInvoiceId(invoiceForJournal.getId().toString());
				journal.setReturnType(returntype);
				journal.setInvoiceNumber(invoiceForJournal.getInvoiceno());
				if (isEmpty(journal.getJournalNumber())) {
					journal.setJournalNumber(journalnumber);
				}
				Double taxableAmount = 0d;
				Double totalAmount = 0d;
				Double igstAmount = 0d;
				Double cgstAmount = 0d;
				Double sgstAmount = 0d;
				Double cessAmount = 0d;
				if (!invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
					if(isNotEmpty(invoiceForJournal.getTotalamount())) {
						totalAmount = invoiceForJournal.getTotalamount();
					}
					if(isNotEmpty(invoiceForJournal.getTotalIgstAmount())) {
						igstAmount = invoiceForJournal.getTotalIgstAmount();
					}
					if(isNotEmpty(invoiceForJournal.getTotalCgstAmount())) {
						cgstAmount = invoiceForJournal.getTotalCgstAmount();
					}
					if(isNotEmpty(invoiceForJournal.getTotalSgstAmount())) {
						sgstAmount = invoiceForJournal.getTotalSgstAmount();
					}
					if(isNotEmpty(invoiceForJournal.getTotalCessAmount())) {
						cessAmount = cessAmount + invoiceForJournal.getTotalCessAmount();
					}
				}else {
					if (isNotEmpty(invoiceForJournal.getItems())) {
						for (Item item : invoiceForJournal.getItems()) {
							if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
								if (isNotEmpty(item.getAdvadjustedAmount())) {
									taxableAmount = taxableAmount + item.getAdvadjustedAmount();
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
						}
					}
				}
				String revchargetype = "";
				if (isNotEmpty(invoiceForJournal.getRevchargetype())) {
					revchargetype = invoiceForJournal.getRevchargetype();
				}

				if ("Reverse".equalsIgnoreCase(revchargetype)) {
					if (isNotEmpty(invoiceForJournal.getTcstdsAmount())) {
						journal.setTdsamount(invoiceForJournal.getTcstdsAmount());
						journal.setCustomerorSupplierAccount(taxableAmount - invoiceForJournal.getTcstdsAmount());
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
				} else {
					if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.NIL)) {
						if (isNotEmpty(invoiceForJournal.getTcstdsAmount())) {
							journal.setTdsamount(invoiceForJournal.getTcstdsAmount());
							journal.setCustomerorSupplierAccount(taxableAmount - invoiceForJournal.getTcstdsAmount());
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
					} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
						String exptype = invoiceForJournal.getExp().get(0).getExpTyp();
						journal.setSalesorPurchases(taxableAmount);
						if ("Composition".equals(dealertype)) {
							journal.setRcmorinterorintra("RCM");
							journal.setIgstamount(0d);
							journal.setSgstamount(0d);
							journal.setCgstamount(0d);
							journal.setCessAmount(0d);
						} else {
							if (exptype.equals("WOPAY")) {
								journal.setRcmorinterorintra("RCM");
								journal.setIgstamount(0d);
								journal.setSgstamount(0d);
								journal.setCgstamount(0d);
								journal.setCessAmount(0d);
							} else if(exptype.equals("WPAY")){
								journal.setRcmorinterorintra("Inter");
								journal.setIgstamount(igstAmount);
								journal.setSgstamount(sgstAmount);
								journal.setCgstamount(cgstAmount);
								journal.setCessAmount(cessAmount);
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
							}
						}
						if (isNotEmpty(invoiceForJournal.getTcstdsAmount())) {
							journal.setTdsamount(invoiceForJournal.getTcstdsAmount());
							journal.setCustomerorSupplierAccount(totalAmount - invoiceForJournal.getTcstdsAmount());
						} else {
							journal.setTdsamount(0d);
							journal.setCustomerorSupplierAccount(totalAmount);
						}
					} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)
							|| invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2C)
							|| invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2CL)) {
						String invtype = invoiceForJournal.getB2b().get(0).getInv().get(0).getInvTyp();
						journal.setSalesorPurchases(taxableAmount);
						if ("Composition".equals(dealertype)) {
							journal.setRcmorinterorintra("RCM");
							journal.setIgstamount(0d);
							journal.setSgstamount(0d);
							journal.setCgstamount(0d);
							journal.setCessAmount(0d);
						} else {
							if (invtype.equals("SEWOP")) {
								journal.setRcmorinterorintra("RCM");
								journal.setIgstamount(0d);
								journal.setSgstamount(0d);
								journal.setCgstamount(0d);
								journal.setCessAmount(0d);
							} else if(invtype.equals("CBW") || invtype.equals("SEWP")){
								journal.setRcmorinterorintra("Inter");
								journal.setIgstamount(igstAmount);
								journal.setSgstamount(sgstAmount);
								journal.setCgstamount(cgstAmount);
								journal.setCessAmount(cessAmount);
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
							}
						}
						if (isNotEmpty(invoiceForJournal.getTcstdsAmount())) {
							journal.setTdsamount(invoiceForJournal.getTcstdsAmount());
							journal.setCustomerorSupplierAccount(totalAmount - invoiceForJournal.getTcstdsAmount());
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
						}
						journal.setSalesorPurchases(taxableAmount);
						if (isNotEmpty(invoiceForJournal.getTcstdsAmount())) {
							journal.setTdsamount(invoiceForJournal.getTcstdsAmount());
							journal.setCustomerorSupplierAccount(totalAmount - invoiceForJournal.getTcstdsAmount());
						} else {
							journal.setTdsamount(0d);
							journal.setCustomerorSupplierAccount(totalAmount);
						}

					}
				}
				double totalCredit = 0.0;
				double totalDebit = 0.0;
				
				totalDebit += journal.getCustomerorSupplierAccount() == null ? 0.0 : journal.getCustomerorSupplierAccount();
				totalDebit += journal.getTdsamount() == null ? 0.0 : (journal.getTdsamount()*2);
				totalDebit += journal.getRoundOffAmount() == null ? 0.0 : journal.getRoundOffAmount();
				
				totalCredit += journal.getSalesorPurchases() == null ? 0.0 : journal.getSalesorPurchases();
				totalCredit += journal.getSgstamount() == null ? 0.0 : journal.getSgstamount();
				totalCredit += journal.getCgstamount() == null ? 0.0 : journal.getCgstamount();
				totalCredit += journal.getIgstamount() == null ? 0.0 : journal.getIgstamount();
				totalCredit += journal.getCessAmount() == null ? 0.0 : journal.getCessAmount();
				totalCredit += journal.getTdsamount() == null ? 0.0 : journal.getTdsamount();
				totalCredit += journal.getRoundOffAmount() == null ? 0.0 : journal.getRoundOffAmount();
				journal.setCreditTotal(Math.abs(totalCredit));
				journal.setDebitTotal(Math.abs(totalDebit));
				accountingJournalRepository.save(journal);
			} else {
				AccountingJournal journal = accountingJournalRepository
						.findByInvoiceId(invoiceForJournal.getId().toString());
				//List<AccountingJournal> journals = accountingJournalRepository.findByClientId(clientid);
				if (isEmpty(journal)) {
					journal = new AccountingJournal();
				}
				if(isNotEmpty(invoiceForJournal.getRoundOffAmount())) {
					journal.setRoundOffAmount(invoiceForJournal.getRoundOffAmount());
				}else {
					journal.setRoundOffAmount(0d);
				}
				if (isNotEmpty(invoiceForJournal.getBillDate())) {
					journal.setDateofinvoice(invoiceForJournal.getBillDate());
					Calendar cal = Calendar.getInstance();
					cal.setTime(invoiceForJournal.getBillDate());
					int months = cal.get(Calendar.MONTH) + 1;
					int years = cal.get(Calendar.YEAR);
					String strMonths = months < 10 ? "0" + months : months + "";
					journal.setInvoiceMonth(strMonths + years);
					if(isNotEmpty(invoiceForJournal.getTrDatemthCd())) {
						journal.setMthCd(invoiceForJournal.getTrDatemthCd());
					}
					if(isNotEmpty(invoiceForJournal.getTrDateyrCd())) {
						journal.setYrCd(invoiceForJournal.getTrDateyrCd());
					}
					if(isNotEmpty(invoiceForJournal.getTrDateqrtCd())) {
						journal.setQrtCd(invoiceForJournal.getTrDateqrtCd());
					}
				} else if (isNotEmpty(invoiceForJournal.getDateofinvoice())) {
					journal.setDateofinvoice(invoiceForJournal.getDateofinvoice());
					Calendar cal = Calendar.getInstance();
					cal.setTime(invoiceForJournal.getDateofinvoice());
					int months = cal.get(Calendar.MONTH) + 1;
					int years = cal.get(Calendar.YEAR);
					String strMonths = months < 10 ? "0" + months : months + "";
					journal.setInvoiceMonth(strMonths + years);
					
					if(isNotEmpty(invoiceForJournal.getMthCd())) {
						journal.setMthCd(invoiceForJournal.getMthCd());
					}
					if(isNotEmpty(invoiceForJournal.getYrCd())) {
						journal.setYrCd(invoiceForJournal.getYrCd());
					}
					if(isNotEmpty(invoiceForJournal.getQrtCd())) {
						journal.setQrtCd(invoiceForJournal.getQrtCd());
					}
				}
				if (isNotEmpty(invoiceForJournal.getUserid())) {
					journal.setUserId(invoiceForJournal.getUserid());
				}
				if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
					if(isNotEmpty(invoiceForJournal.getAdvPCustname())) {
						journal.setLedgerName(invoiceForJournal.getAdvPCustname());
					}else {
						journal.setLedgerName("Other Creditors");							
					}
				}else {
					if (isNotEmpty(invoiceForJournal.getLedgerName())) {
						journal.setLedgerName(invoiceForJournal.getLedgerName());
					}
				}
				if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
					if(isNotEmpty(invoiceForJournal.getAdvPCustname())) {
						journal.setVendorName(invoiceForJournal.getAdvPCustname());
					}else {
						journal.setVendorName("Other Creditors");							
					}
				}else {
					if (isNotEmpty(invoiceForJournal.getVendorName())) {
						if (isNotEmpty(invoiceForJournal.getInvoiceEcomGSTIN())
								&& isNotEmpty(invoiceForJournal.getInvoiceEcomOperator())) {
							String ecomname = invoiceForJournal.getInvoiceEcomOperator() + "("
									+ invoiceForJournal.getInvoiceEcomGSTIN() + " - "
									+ invoiceForJournal.getVendorName() + ")";
							journal.setVendorName(ecomname);
						} else if (isNotEmpty(invoiceForJournal.getInvoiceEcomGSTIN())) {
							String ecomname = invoiceForJournal.getInvoiceEcomGSTIN() + "("
									+ invoiceForJournal.getVendorName() + ")";
							journal.setVendorName(ecomname);
						} else if (isNotEmpty(invoiceForJournal.getInvoiceEcomOperator())) {
							String ecomname = invoiceForJournal.getInvoiceEcomOperator() + "("
									+ invoiceForJournal.getVendorName() + ")";
							journal.setVendorName(ecomname);
						} else {
							journal.setVendorName(invoiceForJournal.getVendorName());
						}
					} else if (isNotEmpty(invoiceForJournal.getBilledtoname())) {
						if (isNotEmpty(invoiceForJournal.getInvoiceEcomGSTIN())
								&& isNotEmpty(invoiceForJournal.getInvoiceEcomOperator())) {
							String ecomname = invoiceForJournal.getInvoiceEcomOperator() + "("
									+ invoiceForJournal.getInvoiceEcomGSTIN() + " - " + invoiceForJournal.getBilledtoname()
									+ ")";
							journal.setVendorName(ecomname);
						} else if (isNotEmpty(invoiceForJournal.getInvoiceEcomGSTIN())) {
							String ecomname = invoiceForJournal.getInvoiceEcomGSTIN() + "("
									+ invoiceForJournal.getBilledtoname() + ")";
							journal.setVendorName(ecomname);
						} else if (isNotEmpty(invoiceForJournal.getInvoiceEcomOperator())) {
							String ecomname = invoiceForJournal.getInvoiceEcomOperator() + "("
									+ invoiceForJournal.getBilledtoname() + ")";
							journal.setVendorName(ecomname);
						} else {
							journal.setVendorName(invoiceForJournal.getBilledtoname());
						}
					} else {
						if (isNotEmpty(invoiceForJournal.getInvoiceEcomGSTIN())
								&& isNotEmpty(invoiceForJournal.getInvoiceEcomOperator())) {
							String ecomname = invoiceForJournal.getInvoiceEcomOperator() + "("
									+ invoiceForJournal.getInvoiceEcomGSTIN() + " - " + "Other Creditors)";
							journal.setVendorName(ecomname);
						} else if (isNotEmpty(invoiceForJournal.getInvoiceEcomGSTIN())) {
							String ecomname = invoiceForJournal.getInvoiceEcomGSTIN() + "(Other Creditors)";
							journal.setVendorName(ecomname);
						} else if (isNotEmpty(invoiceForJournal.getInvoiceEcomOperator())) {
							String ecomname = invoiceForJournal.getInvoiceEcomOperator() + "(Other Creditors)";
							journal.setVendorName(ecomname);
						} else {
							journal.setVendorName("Other Creditors");
						}
					}
				}
				if (isNotEmpty(invoiceForJournal.getClientid())) {
					journal.setClientId(invoiceForJournal.getClientid());
				}
				if (isNotEmpty(invoiceForJournal.getInvtype())) {
					journal.setInvoiceType(invoiceForJournal.getInvtype());
					if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(invoiceForJournal.getInvtype())
							|| MasterGSTConstants.CDNUR.equalsIgnoreCase(invoiceForJournal.getInvtype())) {

						if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(invoiceForJournal.getInvtype())) {
							String docType = invoiceForJournal.getCdn().get(0).getNt().get(0).getNtty();
							journal.setCreditDebitNoteType(docType);
						} else {
							String docType = invoiceForJournal.getCdnur().get(0).getNtty();
							journal.setCreditDebitNoteType(docType);
						}
					}

					if (isEmpty(invoiceForJournal.getLedgerName())) {
						if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
							journal.setLedgerName("Cash");
						} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
							journal.setLedgerName("HO-ISD");
						} else {
							journal.setLedgerName("Expenditure");
						}
					} else {
						journal.setLedgerName(invoiceForJournal.getLedgerName());
					}

				}
				journal.setInvoiceId(invoiceForJournal.getId().toString());
				journal.setReturnType("GSTR2");
				journal.setInvoiceNumber(invoiceForJournal.getInvoiceno());
				if (isEmpty(journal.getJournalNumber())) {
					journal.setJournalNumber(journalnumber);
				}
				Double taxableAmount = 0d;
				Double totalAmount = 0d;
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
				if (isNotEmpty(invoiceForJournal.getItems())) {
					for (Item item : invoiceForJournal.getItems()) {
						if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.NIL)) {
							if (isNotEmpty(item.getTotal())) {
								totalAmount = totalAmount + item.getTotal();
							}
						} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
							if (isNotEmpty(item.getTotal())) {
								totalAmount = totalAmount + item.getTotal();
							}
						} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
							if (isNotEmpty(item.getTotal())) {
								totalAmount = totalAmount + item.getTotal();
							}
						} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ITC_REVERSAL)) {
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
						} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
							if (isNotEmpty(item.getIsdType())
									&& "Eligible - Credit distributed as".equalsIgnoreCase(item.getIsdType())) {
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
							}
							if (isNotEmpty(item.getIsdType())
									&& "Ineligible - Credit distributed as".equalsIgnoreCase(item.getIsdType())) {
								if (isNotEmpty(item.getIgstamount())) {
									totalAmount = totalAmount + item.getIgstamount();
									isdineligibleAmount = isdineligibleAmount + item.getIgstamount();
								}
								if (isNotEmpty(item.getCgstamount())) {
									totalAmount = totalAmount + item.getCgstamount();
									isdineligibleAmount = isdineligibleAmount + item.getCgstamount();
								}
								if (isNotEmpty(item.getSgstamount())) {
									totalAmount = totalAmount + item.getSgstamount();
									isdineligibleAmount = isdineligibleAmount + item.getSgstamount();
								}
								if (isNotEmpty(item.getCessamount())) {
									totalAmount = totalAmount + item.getCessamount();
									isdineligibleAmount = isdineligibleAmount + item.getCessamount();
								}
							}
						} else {

							if (isNotEmpty(item.getElg())) {
								itcType = item.getElg();
								if ("no".equalsIgnoreCase(itcType)) {
									itcinelg = "true";
									if (isNotEmpty(item.getTaxablevalue())) {
										taxableAmount = taxableAmount + item.getTaxablevalue();
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
									if (isNotEmpty(item.getCessamount())) {
										cessAmount = cessAmount + item.getCessamount();
										taxableAmount = taxableAmount + item.getCessamount();
										icsgstamountrcm = icsgstamountrcm + item.getCessamount();
									}
								} else {
									itcinelg = "false";
									if (isNotEmpty(item.getTaxablevalue())) {
										taxableAmount = taxableAmount + item.getTaxablevalue();
									}
									if (isNotEmpty(item.getTotal())) {
										totalAmount = totalAmount + item.getTotal();
									}
									if (isNotEmpty(item.getIgstavltax())) {
										igstAmount = igstAmount + item.getIgstavltax();
										icsgstamountrcm = icsgstamountrcm + item.getIgstavltax();
										if (isNotEmpty(item.getElgpercent())) {
											if (isNotEmpty(item.getIgstamount())) {
												taxableAmount = taxableAmount
														+ (((100 - item.getElgpercent()) / 100) * item.getIgstamount());

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
												taxableAmount = taxableAmount
														+ (((100 - item.getElgpercent()) / 100) * item.getSgstamount());

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
												taxableAmount = taxableAmount
														+ (((100 - item.getElgpercent()) / 100) * item.getCgstamount());
											}
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										rcgstAmount = rcgstAmount + item.getCgstamount();
									}
									if (isNotEmpty(item.getCessavltax())) {
										cessAmount = cgstAmount + item.getCessavltax();
										icsgstamountrcm = icsgstamountrcm + item.getCessavltax();
										if (isNotEmpty(item.getElgpercent())) {
											if (isNotEmpty(item.getCessamount())) {
												taxableAmount = taxableAmount + (((100 - item.getElgpercent()) / 100) * item.getCessamount());
											}
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										rcessAmount = rcessAmount + item.getCessamount();
									}
								}
							} else {
								itcinelg = "false";
								if (isNotEmpty(item.getTaxablevalue())) {
									taxableAmount = taxableAmount + item.getTaxablevalue();
								}
								if (isNotEmpty(item.getTotal())) {
									totalAmount = totalAmount + item.getTotal();
								}
								if (isNotEmpty(item.getIgstavltax())) {

									igstAmount = igstAmount + item.getIgstavltax();
									icsgstamountrcm = icsgstamountrcm + item.getIgstavltax();
									if (isNotEmpty(item.getElgpercent())) {
										if (isNotEmpty(item.getIgstamount())) {
											taxableAmount = taxableAmount
													+ (((100 - item.getElgpercent()) / 100) * item.getIgstamount());
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
											taxableAmount = taxableAmount
													+ (((100 - item.getElgpercent()) / 100) * item.getSgstamount());
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
											taxableAmount = taxableAmount
													+ (((100 - item.getElgpercent()) / 100) * item.getCgstamount());
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
								if (isNotEmpty(item.getCessamount())) {
									rcessAmount = rcessAmount + item.getCessamount();
								}
							}

						}
					}
				}
				String revchargetype = "";
				if (isNotEmpty(invoiceForJournal.getRevchargetype())) {
					revchargetype = invoiceForJournal.getRevchargetype();
				}
				journal.setItcinelg(itcinelg);
				if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.NIL)) {
					if (isNotEmpty(invoiceForJournal.getTcstdsAmount())) {
						journal.setTdsamount(invoiceForJournal.getTcstdsAmount());
						journal.setCustomerorSupplierAccount(totalAmount - invoiceForJournal.getTcstdsAmount());
					} else {
						journal.setTdsamount(0d);
						journal.setCustomerorSupplierAccount(totalAmount);
					}
					journal.setSalesorPurchases(totalAmount);
				} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
					if (isNotEmpty(invoiceForJournal.getTcstdsAmount())) {
						journal.setTdsamount(invoiceForJournal.getTcstdsAmount());
						journal.setCustomerorSupplierAccount(totalAmount - invoiceForJournal.getTcstdsAmount());
					} else {
						journal.setTdsamount(0d);
						journal.setCustomerorSupplierAccount(totalAmount);
					}
					journal.setSalesorPurchases(totalAmount);
				} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)) {
					if (isNotEmpty(invoiceForJournal.getTcstdsAmount())) {
						journal.setTdsamount(invoiceForJournal.getTcstdsAmount());
						journal.setCustomerorSupplierAccount(totalAmount - invoiceForJournal.getTcstdsAmount());
					} else {
						journal.setTdsamount(0d);
						journal.setCustomerorSupplierAccount(totalAmount);
					}
					journal.setSalesorPurchases(totalAmount);
				} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_SERVICES)) {
					if (isNotEmpty(invoiceForJournal.getTcstdsAmount())) {
						journal.setTdsamount(invoiceForJournal.getTcstdsAmount());
						journal.setCustomerorSupplierAccount(taxableAmount - invoiceForJournal.getTcstdsAmount());
					} else {
						journal.setTdsamount(0d);
						journal.setCustomerorSupplierAccount(taxableAmount);
					}
					journal.setSalesorPurchases(taxableAmount);
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
				} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ITC_REVERSAL)) {
					journal.setCustomerorSupplierAccount(totalAmount);
					journal.setSalesorPurchases(totalAmount);
					journal.setIgstamount(igstAmount);
					journal.setSgstamount(sgstAmount);
					journal.setCgstamount(cgstAmount);
					journal.setCessAmount(cessAmount);
					if (isIntraState) {
						journal.setInterorintra("Intra");
					} else {
						journal.setInterorintra("Inter");
					}
				} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
					journal.setCustomerorSupplierAccount(totalAmount);
					journal.setSalesorPurchases(totalAmount);
					journal.setIgstamount(igstAmount);
					journal.setSgstamount(sgstAmount);
					journal.setCgstamount(cgstAmount);
					journal.setCessAmount(cessAmount);
					journal.setIsdineligiblecredit(isdineligibleAmount);
					if (isIntraState) {
						journal.setInterorintra("Intra");
					} else {
						journal.setInterorintra("Inter");
					}
				} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					journal.setSalesorPurchases(taxableAmount);
					journal.setIgstamount(igstAmount);
					journal.setSgstamount(sgstAmount);
					journal.setCgstamount(cgstAmount);
					journal.setCessAmount(cessAmount);
					if (isNotEmpty(invoiceForJournal.getTcstdsAmount())) {
						journal.setTdsamount(invoiceForJournal.getTcstdsAmount());
						journal.setCustomerorSupplierAccount(totalAmount - invoiceForJournal.getTcstdsAmount());
					} else {
						journal.setTdsamount(0d);
						journal.setCustomerorSupplierAccount(totalAmount);
					}
					if (isIntraState) {
						journal.setRcmorinterorintra("Intra");
					} else {
						journal.setRcmorinterorintra("Inter");
					}
				} else {
					if ("Reverse".equalsIgnoreCase(revchargetype)) {
						if ("no".equals(itcType)) {
							if (isNotEmpty(invoiceForJournal.getTcstdsAmount())) {
								journal.setTdsamount(invoiceForJournal.getTcstdsAmount());
								journal.setCustomerorSupplierAccount(totalAmount - invoiceForJournal.getTcstdsAmount());
							} else {
								journal.setTdsamount(0d);
								journal.setCustomerorSupplierAccount(totalAmount);
							}
							journal.setSalesorPurchases(totalAmount);
						} else {
							if (isNotEmpty(invoiceForJournal.getTcstdsAmount())) {
								journal.setTdsamount(invoiceForJournal.getTcstdsAmount());
								journal.setCustomerorSupplierAccount(totalAmount - invoiceForJournal.getTcstdsAmount());
							} else {
								journal.setTdsamount(0d);
								journal.setCustomerorSupplierAccount(totalAmount);
							}
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
						journal.setSalesorPurchases(taxableAmount);
						journal.setIgstamount(igstAmount);
						journal.setSgstamount(sgstAmount);
						journal.setCgstamount(cgstAmount);
						journal.setCessAmount(cessAmount);
						if (isNotEmpty(invoiceForJournal.getTcstdsAmount())) {
							journal.setTdsamount(invoiceForJournal.getTcstdsAmount());
							journal.setCustomerorSupplierAccount(totalAmount - invoiceForJournal.getTcstdsAmount());
						} else {
							journal.setTdsamount(0d);
							journal.setCustomerorSupplierAccount(totalAmount);
						}
					}
				}
				accountingJournalRepository.save(journal);

			}

		}
	}
	private Map<String, StateConfig> getStatesTinMap(){
		Map<String, StateConfig> statesMap = new HashMap<String, StateConfig>();
		List<StateConfig> states = configService.getStates();
		for (StateConfig state : states) {
			String name = state.getName();
			String[] nm = name.split("-");
			statesMap.put(nm[0].replaceAll("\\s", "").toLowerCase(), state);
		}
		return statesMap;
	}

	@Override
	public InvoiceParent savePurchaseRegister(InvoiceParent invoice, boolean isIntraState)
			throws IllegalAccessException, InvocationTargetException {
		Map<String, String> hsnMap = configService.getHSNMap();
		Map<String, String> sacMap = configService.getSACMap();
		Map<String,StateConfig> stateconfig = getStatesMap();
		Map<String,StateConfig> statetin = getStatesTinMap();
		List<InvoicePendingActions> invoicePendingActions = new ArrayList<InvoicePendingActions>();
		if (isNotEmpty(invoice.getNotes())) {
			String notes = invoice.getNotes();
			notes = notes.substring(1);
			invoice.setNotes(notes);
		}
		invoice = populateInvoiceInfo(invoice, PURCHASE_REGISTER, isIntraState,stateconfig,hsnMap,sacMap,statetin,invoicePendingActions);
		PurchaseRegister purchaseRegister = purchaseRepository.save((PurchaseRegister) invoice);
		if (isNotEmpty(purchaseRegister.getMatchingId()) && isNotEmpty(purchaseRegister.getMatchingStatus())
				&& purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MATCHED)) {
			GSTR2 gstr2 = gstr2Repository.findOne(purchaseRegister.getMatchingId());
			ObjectId gstr2Id = gstr2.getId();
			BeanUtils.copyProperties(gstr2, purchaseRegister);
			gstr2.setId(gstr2Id);
			gstr2Repository.save(gstr2);
		}
		return purchaseRegister;
	}

	@Override
	public InvoiceParent saveSalesInvoice(InvoiceParent invoice, boolean isIntraState)
			throws IllegalAccessException, InvocationTargetException {
		Map<String, String> hsnMap = configService.getHSNMap();
		Map<String, String> sacMap = configService.getSACMap();
		Map<String,StateConfig> stateconfig = getStatesMap();
		Map<String,StateConfig> statetin = getStatesTinMap();
		List<InvoicePendingActions> invoicePendingActions = new ArrayList<InvoicePendingActions>();
		invoice = populateInvoiceInfo(invoice, GSTR1, isIntraState,stateconfig,hsnMap,sacMap,statetin,invoicePendingActions);
		gstr1Repository.save((GSTR1) invoice);
		if(invoice.getInvtype().equals(ADVANCES) || invoice.getInvtype().equals(MasterGSTConstants.ATA)) {
			saveAdvancePayments(invoice,GSTR1);
		}
		return invoice;
	}
	
	private Double nilTotalAmounts(InvoiceParent exstngInv,String returntype,OtherConfigurations otherconfig) {
		
		Double totaltaxable = 0d;
		if(returntype.equalsIgnoreCase(PURCHASE_REGISTER)) {
			if(isNotEmpty(((PurchaseRegister)exstngInv).getNilSupplies())) {
				if(isNotEmpty(((PurchaseRegister)exstngInv).getNilSupplies().getInter())) {
					if(isNotEmpty(((PurchaseRegister)exstngInv).getNilSupplies().getInter().getExptdsply())) {
						totaltaxable += ((PurchaseRegister)exstngInv).getNilSupplies().getInter().getExptdsply();
					}
					if(isNotEmpty(((PurchaseRegister)exstngInv).getNilSupplies().getInter().getNgsply())) {
						totaltaxable += ((PurchaseRegister)exstngInv).getNilSupplies().getInter().getNgsply();
					}
					if(isNotEmpty(((PurchaseRegister)exstngInv).getNilSupplies().getInter().getNilsply())) {
						totaltaxable += ((PurchaseRegister)exstngInv).getNilSupplies().getInter().getNilsply();
					}
					if(isNotEmpty(((PurchaseRegister)exstngInv).getNilSupplies().getInter().getCpddr())) {
						totaltaxable += ((PurchaseRegister)exstngInv).getNilSupplies().getInter().getCpddr();
					}
				}
				if(isNotEmpty(((PurchaseRegister)exstngInv).getNilSupplies().getIntra())) {
					if(isNotEmpty(((PurchaseRegister)exstngInv).getNilSupplies().getIntra().getExptdsply())) {
						totaltaxable += ((PurchaseRegister)exstngInv).getNilSupplies().getIntra().getExptdsply();
					}
					if(isNotEmpty(((PurchaseRegister)exstngInv).getNilSupplies().getIntra().getNgsply())) {
						totaltaxable += ((PurchaseRegister)exstngInv).getNilSupplies().getIntra().getNgsply();
					}
					if(isNotEmpty(((PurchaseRegister)exstngInv).getNilSupplies().getIntra().getNilsply())) {
						totaltaxable += ((PurchaseRegister)exstngInv).getNilSupplies().getIntra().getNilsply();
					}
					if(isNotEmpty(((PurchaseRegister)exstngInv).getNilSupplies().getIntra().getCpddr())) {
						totaltaxable += ((PurchaseRegister)exstngInv).getNilSupplies().getIntra().getCpddr();
					}
				}
			}
		}else {
			if(isNotEmpty(exstngInv.getNil()) && isNotEmpty(exstngInv.getNil().getInv())) {
				for(GSTRNilItems nilitem : exstngInv.getNil().getInv()) {
					if(isNotEmpty(nilitem.getExptAmt())) {
						totaltaxable += nilitem.getExptAmt();
					}
					if(isNotEmpty(nilitem.getNgsupAmt())) {
						totaltaxable += nilitem.getNgsupAmt();
					}
					if(isNotEmpty(nilitem.getNilAmt())) {
						totaltaxable += nilitem.getNilAmt();
					}
				}
			}
		}
		
		exstngInv.setTotalamount(totaltaxable);
		exstngInv.setTotaltaxableamount(totaltaxable);
		if (returntype.equals(GSTR1)) {
			if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffSalesField()) && !otherconfig.isEnableroundoffSalesField()) {
				totaltaxable = (double) Math.round(totaltaxable);
				if(isNotEmpty(exstngInv.getTotalamount())) {
					Double notroundofftamnt = exstngInv.getTotalamount();
					Double roundofftamt = (double) Math.round(notroundofftamnt);
					Double roundoffamt = 0d;
					roundoffamt = roundofftamt - notroundofftamnt;
					exstngInv.setTotalamount(roundofftamt);
					exstngInv.setNotroundoftotalamount(notroundofftamnt);
					exstngInv.setRoundOffAmount(Double.parseDouble(df2.format(roundoffamt)));
					String totalAmtStr = String.format(DOUBLE_FORMAT,roundofftamt);
					exstngInv.setTotalamount_str(totalAmtStr);
				}
			}
		}else {
			if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
				totaltaxable = (double) Math.round(totaltaxable);
				if(isNotEmpty(exstngInv.getTotalamount())) {
					Double notroundofftamnt = exstngInv.getTotalamount();
					Double roundofftamt = (double) Math.round(notroundofftamnt);
					Double roundoffamt = 0d;
					roundoffamt = roundofftamt - notroundofftamnt;
					exstngInv.setTotalamount(roundofftamt);
					exstngInv.setNotroundoftotalamount(notroundofftamnt);
					exstngInv.setRoundOffAmount(Double.parseDouble(df2.format(roundoffamt)));
					String totalAmtStr = String.format(DOUBLE_FORMAT,roundofftamt);
					exstngInv.setTotalamount_str(totalAmtStr);
				}
			}
		}
		if(isNotEmpty(exstngInv.getTotaltaxableamount())) {
			exstngInv.setTotaltaxableamount_str(String.format(DOUBLE_FORMAT,exstngInv.getTotaltaxableamount()));
		}
		if(isNotEmpty(exstngInv.getTotalamount())) {
			exstngInv.setTotalamount_str(String.format(DOUBLE_FORMAT,exstngInv.getTotalamount()));
		}
		if(isEmpty(exstngInv.getDueDate()) && isNotEmpty(exstngInv.getDateofinvoice())) {
			exstngInv.setDueDate(exstngInv.getDateofinvoice());
			exstngInv.setTermDays("0");
		}
		if(isNotEmpty(exstngInv.getTotalamount())) {
			exstngInv.setPendingAmount(exstngInv.getTotalamount());
			exstngInv.setReceivedAmount(0d);
		}
		return totaltaxable;
	}
	
	public void savetcstdsamount(String returntype,InvoiceParent invoice) {
		OtherConfigurations configdetails = otherConfigurationRepository.findByClientid(invoice.getClientid());
		boolean tcstds = false;
		if(isNotEmpty(configdetails)) {
			if(isNotEmpty(configdetails.isEnableTCS()) && configdetails.isEnableTCS()) {
				tcstds = true;
			}
		}
		
		Double taxableortotalamt = null;
		Double tcstdsamnt = null;
		if(tcstds) {
			if(isNotEmpty(invoice.getTotaltaxableamount())) {
				taxableortotalamt = invoice.getTotaltaxableamount();
			}
		}else {
			if(isNotEmpty(invoice.getTotalamount())) {
				taxableortotalamt = invoice.getTotalamount();
			}
		}
		
		if(isNotEmpty(taxableortotalamt) && isNotEmpty(invoice.getTcstdspercentage())) {
			tcstdsamnt = (taxableortotalamt) * invoice.getTcstdspercentage() / 100;
		}
		
		if (returntype.equals(MasterGSTConstants.GSTR1) || returntype.equals("SalesRegister")) {
			if (invoice.getInvtype().equals(MasterGSTConstants.ATPAID)) {
				if (isNotEmpty(tcstdsamnt) && isNotEmpty(invoice.getTotalamount()) && isNotEmpty(invoice.getTotaltax()) && isNotEmpty(invoice.getTcstdspercentage())) {
					invoice.setTcstdsAmount(tcstdsamnt);
					invoice.setNetAmount(invoice.getTotalamount() + tcstdsamnt);
				}
			} else {
				if (isNotEmpty(invoice.getTotalamount()) && isNotEmpty(invoice.getTcstdspercentage())) {
					invoice.setTcstdsAmount(tcstdsamnt);
					invoice.setNetAmount(invoice.getTotalamount() + tcstdsamnt);
				}
			}
		} else if (returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals("PurchaseRegister")) {
			if (invoice.getInvtype().equals(MasterGSTConstants.ATPAID)) {
				if (isNotEmpty(tcstdsamnt) && isNotEmpty(invoice.getTotalamount()) && isNotEmpty(invoice.getTotaltax()) && isNotEmpty(invoice.getTcstdspercentage())) {
					invoice.setTcstdsAmount(tcstdsamnt);
					invoice.setNetAmount(invoice.getTotalamount() - tcstdsamnt);
				}
			} else if (!invoice.getInvtype().equals(MasterGSTConstants.ISD)	&& !invoice.getInvtype().equals(MasterGSTConstants.ITC_REVERSAL)) {
				if (isNotEmpty(tcstdsamnt) && isNotEmpty(invoice.getTotalamount()) && isNotEmpty(invoice.getTcstdspercentage())) {
					invoice.setTcstdsAmount(tcstdsamnt);
					invoice.setNetAmount(invoice.getTotalamount() - tcstdsamnt);
				}
			}
		}
	}
	
	public static Double findClosest(Double rate[], double target)
    {
        int n = rate.length;
 
        // Corner cases
        if (target <= rate[0])
            return rate[0];
        if (target >= rate[n - 1])
            return rate[n - 1];
 
        int i = 0, j = n, mid = 0;
        while (i < j) {
            mid = (i + j) / 2;
 
            if (rate[mid] == target)
                return rate[mid];
 
            if (target < rate[mid]) {
        
                if (mid > 0 && target > rate[mid - 1])
                    return getClosest(rate[mid - 1],
                    		rate[mid], target);
                 
                j = mid;             
            }
 
            else {
                if (mid < n-1 && target < rate[mid + 1])
                    return getClosest(rate[mid],
                    		rate[mid + 1], target);               
                i = mid + 1; // update i
            }
        }
 
        return rate[mid];
    }
 
    public static Double getClosest(Double val1, Double val2, Double target)
    {
        if (target - val1 >= val2 - target)
            return val2;
        else
            return val1;       
    }
    
    public void defaultLedgerName(InvoiceParent invoice,String returntype) {
		if(isNotEmpty(invoice) && isNotEmpty(invoice.getInvtype()) && isNotEmpty(invoice.getItems())) {
			if(isEmpty(invoice.getItems().get(0).getLedgerName())) {
				if(invoice.getInvtype().equals(MasterGSTConstants.ADVANCES)) {
					invoice.getItems().get(0).setLedgerName("Cash");
				}else {
					if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
						invoice.getItems().get(0).setLedgerName(AccountConstants.PURCHASES);
					}else {
						invoice.getItems().get(0).setLedgerName(AccountConstants.SALES);
					}
				}
			}
		}
	}
}
