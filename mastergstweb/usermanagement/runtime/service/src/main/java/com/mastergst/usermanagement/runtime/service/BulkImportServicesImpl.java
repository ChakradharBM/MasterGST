package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.ADVANCES;
import static com.mastergst.core.common.MasterGSTConstants.ANX1;
import static com.mastergst.core.common.MasterGSTConstants.ATPAID;
import static com.mastergst.core.common.MasterGSTConstants.B2B;
import static com.mastergst.core.common.MasterGSTConstants.B2BUR;
import static com.mastergst.core.common.MasterGSTConstants.B2C;
import static com.mastergst.core.common.MasterGSTConstants.B2CL;
import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.CDNUR;
import static com.mastergst.core.common.MasterGSTConstants.CREDIT_DEBIT_NOTES;
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
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.DateUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
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
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.dao.NewAnx1Dao;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.AccountingJournalPaymentItems;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientAddlInfo;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CompanyInvoices;
import com.mastergst.usermanagement.runtime.domain.CompanySuppliers;
import com.mastergst.usermanagement.runtime.domain.DeliveryChallan;
import com.mastergst.usermanagement.runtime.domain.DeliveryChallana;
import com.mastergst.usermanagement.runtime.domain.Estimate;
import com.mastergst.usermanagement.runtime.domain.Estimates;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.GSTR4;
import com.mastergst.usermanagement.runtime.domain.GSTR5;
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6;
import com.mastergst.usermanagement.runtime.domain.GSTRAdvanceTax;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRB2CL;
import com.mastergst.usermanagement.runtime.domain.GSTRB2CS;
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
import com.mastergst.usermanagement.runtime.repository.AccountingJournalRepository;
import com.mastergst.usermanagement.runtime.repository.ClientAddlInfoRepository;
import com.mastergst.usermanagement.runtime.repository.ClientRepository;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyCustomersRepository;
import com.mastergst.usermanagement.runtime.repository.CompanySuppliersRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyUserRepository;
import com.mastergst.usermanagement.runtime.repository.DeliveryChallanRepository;
import com.mastergst.usermanagement.runtime.repository.EstimatesRepository;
import com.mastergst.usermanagement.runtime.repository.GSTINPublicDataRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR1ARepository;
import com.mastergst.usermanagement.runtime.repository.GSTR1DocumentIssueRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;
import com.mastergst.usermanagement.runtime.repository.GSTR2Repository;
import com.mastergst.usermanagement.runtime.repository.GSTR3BRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR4Repository;
import com.mastergst.usermanagement.runtime.repository.GSTR5Repository;
import com.mastergst.usermanagement.runtime.repository.GSTR6Repository;
import com.mastergst.usermanagement.runtime.repository.GSTR9CRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR9Repository;
import com.mastergst.usermanagement.runtime.repository.GSTROffsetLiabilityRepository;
import com.mastergst.usermanagement.runtime.repository.LedgerRepository;
import com.mastergst.usermanagement.runtime.repository.NewAnx1Repository;
import com.mastergst.usermanagement.runtime.repository.OtherConfigurationRepository;
import com.mastergst.usermanagement.runtime.repository.ProformaInvoicesRepository;
import com.mastergst.usermanagement.runtime.repository.PurchaseOrderRepository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;
import com.mastergst.usermanagement.runtime.repository.RecordPaymentsRepository;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.ResponseData;

@Service
@Transactional(readOnly = true)
public class BulkImportServicesImpl implements BulkImportServices{

	private static final Logger logger = LogManager.getLogger(ClientServiceImpl.class.getName());
	private static final String CLASSNAME = "ClientServiceImpl::";
	private static SimpleDateFormat dateFormatOnlyDate = new SimpleDateFormat("dd/MM/yyyy");
	private static String DOUBLE_FORMAT  = "%.2f";
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private ClientService clientService;
	@Autowired
	UserService userService;
	@Autowired
	EinvoiceService einvoiceService;
	@Autowired
	GSTR1Repository gstr1Repository;
	@Autowired
	GSTR1ARepository gstr1ARepository;
	@Autowired
	GSTR2Repository gstr2Repository;
	@Autowired
	GSTR4Repository gstr4Repository;
	@Autowired
	GSTR5Repository gstr5Repository;
	@Autowired
	GSTR6Repository gstr6Repository;
	@Autowired
	PurchaseRegisterRepository purchaseRepository;
	@Autowired
	RecordPaymentsRepository recordPaymentsRepository;
	@Autowired
	ClientRepository clientRepository;
	@Autowired
	ClientUserMappingRepository clientUserMappingRepository;
	@Autowired
	ClientAddlInfoRepository clientAddlInfoRepository;
	@Autowired
	CompanyUserRepository companyUserRepository;
	@Autowired
	CompanyCustomersRepository companyCustomersRepository;
	@Autowired
	private GSTINPublicDataRepository gstinPublicDataRepository;
	@Autowired
	GSTR3BRepository gstr3BRepository;
	@Autowired
	GSTR9Repository gstr9Repository;
	@Autowired
	GSTR9CRepository gstr9cRepository;
	@Autowired
	NewAnx1Repository anx1Repository;
	@Autowired
	GSTR1DocumentIssueRepository gstr1DocumentIssueRepository;
	@Autowired
	DeliveryChallanRepository deliverychallanRepository;
	@Autowired
	ProformaInvoicesRepository proformainvoicesRepository;
	@Autowired
	EstimatesRepository estimatesRepository;
	@Autowired
	PurchaseOrderRepository purchaseOrderRepository;
	@Autowired
	GSTROffsetLiabilityRepository gstrOffsetLiabilityRepository;

	@Autowired
	AccountingJournalRepository accountingJournalRepository;
	@Autowired
	CompanySuppliersRepository companySuppliersRepository;
	@Autowired
	ClientUtils clientUtils;
	@Autowired
	OtherConfigurationRepository otherConfigurationRepository;
	@Autowired
	LedgerRepository ledgerRepository;
	@Autowired
	NewAnx1Dao newAnx1Dao;
	@Autowired
	IHubConsumerService iHubConsumerService;
	@Autowired
	ConfigService configService;
	@Autowired
	ProfileService profileService;
	
	@Transactional(readOnly = true)
	public Client findById(final String id) {
		logger.debug(CLASSNAME + "findById : " + BEGIN);
		logger.debug(CLASSNAME + "findById : id\t" + id);
		ObjectId objId = new ObjectId(id);
		Client client = mongoTemplate.findById(objId, Client.class);
		logger.debug(CLASSNAME + "findById : Begin");
		return client;
	}
	
	@Override
	@Transactional
	public void updateExcelData(final Map<String, List<InvoiceParent>> beans, final List<String> sheetList,
			final String returntype, final String id, final String fullname, final String clientId,
			final String templateType) throws IllegalArgumentException, IOException {
		logger.debug(CLASSNAME + "updateExcelData : Begin");
		Client client = findById(clientId);
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

		List<? extends InvoiceParent> invoices = null;
		if (returntype.equals(GSTR1)) {
			invoices = new ArrayList<GSTR1>();
		} else if (returntype.equals(GSTR4)) {
			invoices = new ArrayList<GSTR4>();
		} else if (returntype.equals(GSTR5)) {
			invoices = new ArrayList<GSTR5>();
		} else if (returntype.equals(GSTR6)) {
			invoices = new ArrayList<GSTR6>();
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
				}else if(sheetName.equals("invoiceList")) {
					type = B2B;
				}
				invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, beans.get(sheetName), returntype,
						client, type, id, fullname, clientId, templateType);
			}
		} else {
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, invoiceList, returntype, client, null, id,
					fullname, clientId, templateType);
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, creditList, returntype, client,
					CREDIT_DEBIT_NOTES, id, fullname, clientId, templateType);
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, exportList, returntype, client, EXPORTS,
					id, fullname, clientId, templateType);
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, advReceiptList, returntype, client,
					ADVANCES, id, fullname, clientId, templateType);
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, advAdjustedList, returntype, client,
					ATPAID, id, fullname, clientId, templateType);
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, cdnurList, returntype, client, CDNUR, id,
					fullname, clientId, templateType);
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, nilList, returntype, client, NIL, id,
					fullname, clientId, templateType);
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, b2bList, returntype, client, B2B, id,
					fullname, clientId, templateType);
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, b2cList, returntype, client, B2C, id,
					fullname, clientId, templateType);
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, b2clList, returntype, client, B2CL, id,
					fullname, clientId, templateType);
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, b2buList, returntype, client, B2BUR, id,
					fullname, clientId, templateType);
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, impgList, returntype, client, IMP_GOODS,
					id, fullname, clientId, templateType);
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, impsList, returntype, client,
					IMP_SERVICES, id, fullname, clientId, templateType);
			invoices = updateSalesInvoiceData((List<InvoiceParent>) invoices, itrvslList, returntype, client,
					ITC_REVERSAL, id, fullname, clientId, templateType);
		}
		if (isNotEmpty(invoices)) {
			OtherConfigurations otherconfig = clientService.getOtherConfig(clientId);
			if (returntype.equals(GSTR1)) {
				if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffSalesField()) && !otherconfig.isEnableroundoffSalesField()) {
					for(GSTR1 salesinvoice : (List<GSTR1>) invoices) {
						if(isNotEmpty(salesinvoice.getTotalamount())) {
							Double notroundofftamnt = salesinvoice.getTotalamount();
							Double roundofftamt = (double) Math.round(notroundofftamnt);
							Double roundoffamt = 0d;
							roundoffamt = roundofftamt - notroundofftamnt;
							salesinvoice.setTotalamount(roundofftamt);
							salesinvoice.setNotroundoftotalamount(notroundofftamnt);
							salesinvoice.setRoundOffAmount(Double.parseDouble(df2.format(roundoffamt)));
							String totalAmtStr = String.format(DOUBLE_FORMAT,roundofftamt);
							salesinvoice.setTotalamount_str(totalAmtStr);
						}
					}
				}
				
				for(GSTR1 salesinvoice : (List<GSTR1>) invoices) {
					Double totalIGST = 0d;
					Double totalCGST = 0d;
					Double totalSGST = 0d;
					Double totalExempted = 0d;
					Double totalCessAmount = 0d;
					
					for (Item item : salesinvoice.getItems()) {
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
						if(isNotEmpty(item.getCessamount())) {
							totalCessAmount+=item.getCessamount();
						}
					}
					salesinvoice.setTotalIgstAmount(totalIGST);
					salesinvoice.setTotalCgstAmount(totalCGST);
					salesinvoice.setTotalSgstAmount(totalSGST);
					salesinvoice.setTotalExemptedAmount(totalExempted);
					salesinvoice.setTotalCessAmount(totalCessAmount);
				}
				List<GSTR1> salesInvoices = gstr1Repository.save((List<GSTR1>) invoices);
				
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
						if (salesinvoice.getInvtype().equals(MasterGSTConstants.EXPORTS)
								|| salesinvoice.getInvtype().equals(MasterGSTConstants.EXPA)) {
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
						}
						
						saveJournalInvoice(salesinvoice, salesinvoice.getClientid(), returntype, isIntraState);
					}
				}
			} else if (returntype.equals(GSTR4)) {
				gstr4Repository.save((List<GSTR4>) invoices);
			} else if (returntype.equals(GSTR5)) {
				gstr5Repository.save((List<GSTR5>) invoices);
			} else if (returntype.equals(GSTR6)) {
				gstr6Repository.save((List<GSTR6>) invoices);
			} else {
				if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
					for(PurchaseRegister salesinvoice : (List<PurchaseRegister>) invoices) {
						if(isNotEmpty(salesinvoice.getTotalamount())) {
							Double notroundofftamnt = salesinvoice.getTotalamount();
							Double roundofftamt = (double) Math.round(notroundofftamnt);
							Double roundoffamt = 0d;
							roundoffamt = roundofftamt - notroundofftamnt;
							salesinvoice.setTotalamount(roundofftamt);
							salesinvoice.setNotroundoftotalamount(notroundofftamnt);
							salesinvoice.setRoundOffAmount(Double.parseDouble(df2.format(roundoffamt)));
							String totalAmtStr = String.format(DOUBLE_FORMAT,roundofftamt);
							salesinvoice.setTotalamount_str(totalAmtStr);
						}
					}
				}
				for(PurchaseRegister salesinvoice : (List<PurchaseRegister>) invoices) {
					Double totalIGST = 0d;
					Double totalCGST = 0d;
					Double totalSGST = 0d;
					Double totalExempted = 0d;
					Double totalCessAmount = 0d;
					
					for (Item item : salesinvoice.getItems()) {
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
						if(isNotEmpty(item.getCessamount())) {
							totalCessAmount+=item.getCessamount();
						}
					}
					salesinvoice.setTotalIgstAmount(totalIGST);
					salesinvoice.setTotalCgstAmount(totalCGST);
					salesinvoice.setTotalSgstAmount(totalSGST);
					salesinvoice.setTotalExemptedAmount(totalExempted);
					salesinvoice.setTotalCessAmount(totalCessAmount);
				}
				List<PurchaseRegister> purchaseRegisters = purchaseRepository.save((List<PurchaseRegister>) invoices);
				List<PurchaseRegister> b2bRegister = Lists.newArrayList();
				List<PurchaseRegister> cdnRegister = Lists.newArrayList();
				for(PurchaseRegister purchaseRegister : purchaseRegisters) {
					if(isNotEmpty(purchaseRegister.getInvtype()) && purchaseRegister.getInvtype().equals(B2B)) {
						b2bRegister.add(purchaseRegister);
					} else if (isNotEmpty(purchaseRegister.getInvtype())
							&& purchaseRegister.getInvtype().equals(CREDIT_DEBIT_NOTES)) {
						cdnRegister.add(purchaseRegister);
					}
					String statename = "";
					if (isNotEmpty(purchaseRegister.getB2b().get(0).getCtin())) {
						String gstinNumber = purchaseRegister.getB2b().get(0).getCtin().trim();
						List<StateConfig> states = configService.getStates();
						if (isNotEmpty(gstinNumber)) {
							gstinNumber = gstinNumber.substring(0,2);
							for (StateConfig state : states) {
								if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
									statename = state.getName();
									break;
								}
							}
						}
					}
					boolean isIntraState = true;
					if (isNotEmpty(statename)) {
						if (!statename.equals(client.getStatename())) {
							isIntraState = false;
						}
					}
					saveJournalInvoice(purchaseRegister, purchaseRegister.getClientid(), returntype, isIntraState);
				}
			}
		}
		logger.debug(CLASSNAME + "updateExcelData : End");
	}

	private List<? extends InvoiceParent> updateSalesInvoiceData(List<InvoiceParent> existingInvoices,
			List<? extends InvoiceParent> invoiceList, final String returntype, Client client, String invType,
			final String id, final String fullname, final String clientId, final String templateType) {
		logger.debug(CLASSNAME + "updateSalesInvoiceData : Begin {}", invoiceList);
		
		OtherConfigurations otherconfig = clientService.getOtherConfig(clientId);
		List<String> invtypes = Lists.newArrayList();
		if(isEmpty(invType)) {
			invtypes.add("B2B");
			invtypes.add("B2C");
			invtypes.add("B2CL");
		}else {
			invtypes.add(invType);
		}
		if (isEmpty(invoiceList)) {
			return existingInvoices;
		}
		for (InvoiceParent invoice : invoiceList) {
			boolean present = false;
			boolean isIntraState = true;
			if (isNotEmpty(invoice.getStatename())) {
				String strState = invoice.getStatename();
				Integer pos = null;
				List<StateConfig> states = configService.getStates();
				if (isNotEmpty(strState)) {
					if (strState.contains("-")) {
						strState = strState.substring(0, strState.indexOf("-")).trim();
					}
					for (StateConfig state : states) {
						if (state.getName().equals(strState)) {
							pos = state.getTin();
							break;
						} else if (state.getCode().equals(strState)) {
							pos = state.getTin();
							invoice.setStatename(state.getName());
							break;
						} else if (state.getTin().toString().equals(strState)) {
							pos = state.getTin();
							invoice.setStatename(state.getName());
							break;
						}
					}
					if (isNotEmpty(pos) && isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())
							&& isEmpty(invoice.getB2b().get(0).getInv().get(0).getPos())) {
						String strPos = pos < 10 ? "0"+pos : pos.toString();
						invoice.getB2b().get(0).getInv().get(0).setPos(strPos);
					}
					String strClientState = client.getStatename();
					if (strClientState.contains("-")) {
						strClientState = strClientState.substring(0, strClientState.indexOf("-")).trim();
					}
					if ("Purchase Register".equals(returntype) || "GSTR2".equals(returntype)
							|| "PurchaseRegister".equals(returntype)) {
						if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
							strState = (invoice.getB2b().get(0).getCtin().trim()).substring(0, 2);

						}
					}
					if (!strClientState.equals(strState)) {
						isIntraState = false;
					}
				}
			}
			if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())
					&& isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
				if(isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getInum())) {
					String val = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getInum().trim())
							.toPlainString();
					invoice.getB2b().get(0).getInv().get(0).setInum(val);
				} else {
					invoice.getB2b().get(0).getInv().get(0)
							.setInum(invoice.getB2b().get(0).getInv().get(0).getInum().trim());
				}
			}
			if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {

				invoice.getB2b().get(0).setCtin(invoice.getB2b().get(0).getCtin().trim());
			}
			for (InvoiceParent exstngInv : existingInvoices) {
				if ((isNotEmpty(invoice.getB2b()) && isNotEmpty(exstngInv.getB2b())
						&& isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())
						&& isNotEmpty(exstngInv.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invtypes)
						&& isNotEmpty(exstngInv.getInvtype()) && invtypes.contains(exstngInv.getInvtype())
						&& (exstngInv.getB2b().get(0).getInv().get(0).getInum().trim())
								.equals((invoice.getB2b().get(0).getInv().get(0).getInum().trim()))
						&& isNotEmpty(invoice.getFp()) && exstngInv.getFp().equals(invoice.getFp()))) {
					if (isEmpty(invoice.getB2b().get(0).getCtin()) || exstngInv.getB2b().get(0).getCtin().toUpperCase()
							.equals(invoice.getB2b().get(0).getCtin().toUpperCase())) {
						if(exstngInv.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
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
									exstngInv.getItems().addAll(invoice.getItems());
									List<GSTRItems> gstrItems = populateItemData(exstngInv, isIntraState,returntype);
									Double totalvalue = 0d;
									for(GSTRItems gstritem : gstrItems) {
										if(isNotEmpty(gstritem.getItem())) {
											if(isNotEmpty(gstritem.getItem().getTxval())) {
												totalvalue = totalvalue+gstritem.getItem().getTxval();
											}
											if(isNotEmpty(gstritem.getItem().getCamt())) {
												totalvalue = totalvalue+gstritem.getItem().getCamt();
											}
											if(isNotEmpty(gstritem.getItem().getSamt())) {
												totalvalue = totalvalue+gstritem.getItem().getSamt();
											}
											if(isNotEmpty(gstritem.getItem().getIamt())) {
												totalvalue = totalvalue+gstritem.getItem().getIamt();
											}
											if(isNotEmpty(gstritem.getItem().getCsamt())) {
												totalvalue = totalvalue+gstritem.getItem().getCsamt();
											}
										}
									}
									if (returntype.equals(GSTR1)) {
										((GSTR1) exstngInv).getCdnr().get(0).getNt().get(0).setItms(gstrItems);
										
										if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffSalesField()) && !otherconfig.isEnableroundoffSalesField()) {
											totalvalue = (double) Math.round(totalvalue);
										}
										((GSTR1) exstngInv).getCdnr().get(0).getNt().get(0).setVal(totalvalue);
									}else {
										((PurchaseRegister) exstngInv).getCdn().get(0).getNt().get(0).setItms(gstrItems);
										if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
											totalvalue = (double) Math.round(totalvalue);
										}
										((PurchaseRegister) exstngInv).getCdn().get(0).getNt().get(0).setVal(totalvalue);
									}
									//exstngInv.getB2b().get(0).getInv().get(0).setItms(gstrItems);
									present = true;
								}
							}
							
						}else if(exstngInv.getInvtype().equals(MasterGSTConstants.CDNUR)){
							
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
									exstngInv.getItems().addAll(invoice.getItems());
									List<GSTRItems> gstrItems = populateItemData(exstngInv, isIntraState,returntype);
									Double totalvalue = 0d;
									for(GSTRItems gstritem : gstrItems) {
										if(isNotEmpty(gstritem.getItem())) {
											if(isNotEmpty(gstritem.getItem().getTxval())) {
												totalvalue = totalvalue+gstritem.getItem().getTxval();
											}
											if(isNotEmpty(gstritem.getItem().getCamt())) {
												totalvalue = totalvalue+gstritem.getItem().getCamt();
											}
											if(isNotEmpty(gstritem.getItem().getSamt())) {
												totalvalue = totalvalue+gstritem.getItem().getSamt();
											}
											if(isNotEmpty(gstritem.getItem().getIamt())) {
												totalvalue = totalvalue+gstritem.getItem().getIamt();
											}
											if(isNotEmpty(gstritem.getItem().getCsamt())) {
												totalvalue = totalvalue+gstritem.getItem().getCsamt();
											}
										}
									}
									if (returntype.equals(GSTR1)) {
										((GSTR1) exstngInv).getCdnur().get(0).setItms(gstrItems);
										if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffSalesField()) && !otherconfig.isEnableroundoffSalesField()) {
											totalvalue = (double) Math.round(totalvalue);
										}
										((GSTR1) exstngInv).getCdnur().get(0).setVal(totalvalue);
									}else {
										((PurchaseRegister) exstngInv).getCdnur().get(0).setItms(gstrItems);
										if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
											totalvalue = (double) Math.round(totalvalue);
										}
										((PurchaseRegister) exstngInv).getCdnur().get(0).setVal(totalvalue);
									}
									present = true;
								}
							}
						}else if(exstngInv.getInvtype().equals(MasterGSTConstants.B2C)) {
							if (isNotEmpty(invoice.getItems())) {
								if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
									if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE)
											|| invoice.getItems().get(0).getCategory()
													.equals(MasterGSTConstants.GOODS)) {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
									} else {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
									}
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								}
								exstngInv.getItems().addAll(invoice.getItems());
								String stateTin = getStateCode(invoice.getStatename());
								List<GSTRB2CS> b2cs = Lists.newArrayList();
								double totaltaxable = 0d;
								double totaltax = 0d;
								for (Item item : exstngInv.getItems()) {
									GSTRB2CS gstrb2csDetail = new GSTRB2CS();
									if (isNotEmpty(item.getIgstrate())) {
										gstrb2csDetail.setRt(item.getIgstrate());
									} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())) {
										gstrb2csDetail.setRt(item.getCgstrate() + item.getSgstrate());
									}
									if (!isIntraState && isNotEmpty(item.getIgstamount())) {
										gstrb2csDetail.setIamt(item.getIgstamount());
										totaltax = totaltax+item.getIgstamount();
									} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
										gstrb2csDetail.setCamt(item.getCgstamount());
										gstrb2csDetail.setSamt(item.getSgstamount());
										totaltax = totaltax+item.getCgstamount();
										totaltax = totaltax+item.getSgstamount();
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
									String ecomGSTIN = invoice.getB2cs().get(0).getEtin();
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
									b2cs.add(gstrb2csDetail);
								}
								exstngInv.setTotaltaxableamount(totaltaxable);
								exstngInv.setTotaltax(totaltax);
								exstngInv.setTotalamount(totaltaxable + totaltax);
								exstngInv.setB2cs(b2cs);
								present = true;
							}
						}else if(exstngInv.getInvtype().equals(MasterGSTConstants.B2CL)) {
							if (isNotEmpty(invoice.getItems())) {
								if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
									if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE)
											|| invoice.getItems().get(0).getCategory()
													.equals(MasterGSTConstants.GOODS)) {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
									} else {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
									}
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								}
								exstngInv.getItems().addAll(invoice.getItems());
								List<GSTRItems> gstrItems = populateItemData(exstngInv, isIntraState, returntype);
								Double totalvalue = 0d;
								for(GSTRItems gstritem : gstrItems) {
									if(isNotEmpty(gstritem.getItem())) {
										if(isNotEmpty(gstritem.getItem().getTxval())) {
											totalvalue = totalvalue+gstritem.getItem().getTxval();
										}
										if(isNotEmpty(gstritem.getItem().getCamt())) {
											totalvalue = totalvalue+gstritem.getItem().getCamt();
										}
										if(isNotEmpty(gstritem.getItem().getSamt())) {
											totalvalue = totalvalue+gstritem.getItem().getSamt();
										}
										if(isNotEmpty(gstritem.getItem().getIamt())) {
											totalvalue = totalvalue+gstritem.getItem().getIamt();
										}
										if(isNotEmpty(gstritem.getItem().getCsamt())) {
											totalvalue = totalvalue+gstritem.getItem().getCsamt();
										}
									}
								}
								exstngInv.getB2cl().get(0).getInv().get(0).setItms(gstrItems);
								if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffSalesField()) && !otherconfig.isEnableroundoffSalesField()) {
									totalvalue = (double) Math.round(totalvalue);
								}
								exstngInv.getB2cl().get(0).getInv().get(0).setVal(totalvalue);
									present = true;
							}
						}else if(exstngInv.getInvtype().equals(MasterGSTConstants.B2BUR)) {
							if (isNotEmpty(invoice.getItems())) {
								if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
									if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE)
											|| invoice.getItems().get(0).getCategory()
													.equals(MasterGSTConstants.GOODS)) {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
									} else {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
									}
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								}
								exstngInv.getItems().addAll(invoice.getItems());
								List<GSTRItems> gstrItems = populateItemData(exstngInv, isIntraState, returntype);
								Double totalvalue = 0d;
								for(GSTRItems gstritem : gstrItems) {
									if(isNotEmpty(gstritem.getItem())) {
										if(isNotEmpty(gstritem.getItem().getTxval())) {
											totalvalue = totalvalue+gstritem.getItem().getTxval();
										}
										if(isNotEmpty(gstritem.getItem().getCamt())) {
											totalvalue = totalvalue+gstritem.getItem().getCamt();
										}
										if(isNotEmpty(gstritem.getItem().getSamt())) {
											totalvalue = totalvalue+gstritem.getItem().getSamt();
										}
										if(isNotEmpty(gstritem.getItem().getIamt())) {
											totalvalue = totalvalue+gstritem.getItem().getIamt();
										}
										if(isNotEmpty(gstritem.getItem().getCsamt())) {
											totalvalue = totalvalue+gstritem.getItem().getCsamt();
										}
									}
								}
								((PurchaseRegister) exstngInv).getB2bur().get(0).getInv().get(0).setItms(gstrItems);
								if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
									totalvalue = (double) Math.round(totalvalue);
								}
								((PurchaseRegister) exstngInv).getB2bur().get(0).getInv().get(0).setVal(totalvalue);
								present = true;
							}
						} else if(exstngInv.getInvtype().equals(MasterGSTConstants.IMP_GOODS)){
							if (isNotEmpty(invoice.getItems())) {
								if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
									if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE)
											|| invoice.getItems().get(0).getCategory()
													.equals(MasterGSTConstants.GOODS)) {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
									} else {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
									}
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								}
								exstngInv.getItems().addAll(invoice.getItems());
								List<GSTRItems> gstrItems = populateItemData(exstngInv, false, returntype);
								List<GSTRImportItems> items = Lists.newArrayList();
								double totalValue = 0d;
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
										if(isNotEmpty(gstrItem.getItem().getTxval())) {
											totalValue = totalValue+gstrItem.getItem().getTxval();
										}
										if(isNotEmpty(gstrItem.getItem().getIamt())) {
											totalValue = totalValue+gstrItem.getItem().getIamt();
										}
										if(isNotEmpty(gstrItem.getItem().getCsamt())) {
											totalValue = totalValue+gstrItem.getItem().getCsamt();
										}
									}
								}
								((PurchaseRegister) exstngInv).getImpGoods().get(0).setItms(items);
								if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
									totalValue = (double) Math.round(totalValue);
								}
								((PurchaseRegister) exstngInv).getImpGoods().get(0).setBoeVal(totalValue);
								present = true;
							}
						}else if(exstngInv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_SERVICES)){
							if (isNotEmpty(invoice.getItems())) {
								if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
									if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE)
											|| invoice.getItems().get(0).getCategory()
													.equals(MasterGSTConstants.GOODS)) {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
									} else {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
									}
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								}
								exstngInv.getItems().addAll(invoice.getItems());
								List<GSTRItems> gstrItems = populateItemData(exstngInv, false, returntype);
								List<GSTRImportItems> items = Lists.newArrayList();
								double totalValue = 0d;
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
										if(isNotEmpty(gstrItem.getItem().getTxval())) {
											totalValue = totalValue+gstrItem.getItem().getTxval();
										}
										if(isNotEmpty(gstrItem.getItem().getIamt())) {
											totalValue = totalValue+gstrItem.getItem().getIamt();
										}
										if(isNotEmpty(gstrItem.getItem().getCsamt())) {
											totalValue = totalValue+gstrItem.getItem().getCsamt();
										}
									}
								}
								((PurchaseRegister) exstngInv).getImpServices().get(0).setItms(items);
								if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
									totalValue = (double) Math.round(totalValue);
								}
								((PurchaseRegister) exstngInv).getImpServices().get(0).setIval(totalValue);
								present = true;
							}
						}else {
							
							if (isNotEmpty(invoice.getItems())) {
								if (isNotEmpty(invoice.getItems().get(0).getCategory())) {
									if (invoice.getItems().get(0).getCategory().equals(MasterGSTConstants.PRODUCT_CODE)
											|| invoice.getItems().get(0).getCategory()
													.equals(MasterGSTConstants.GOODS)) {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
									} else {
										invoice.getItems().get(0).setCategory(MasterGSTConstants.SAC);
									}
								} else {
									invoice.getItems().get(0).setCategory(MasterGSTConstants.HSN);
								}
								exstngInv.getItems().addAll(invoice.getItems());
								List<GSTRItems> gstrItems = populateItemData(exstngInv, isIntraState,returntype);
								exstngInv.getB2b().get(0).getInv().get(0).setItms(gstrItems);
								Double totalvalue = 0d;
								for(GSTRItems gstritem : gstrItems) {
									if(isNotEmpty(gstritem.getItem())) {
										if(isNotEmpty(gstritem.getItem().getTxval())) {
											totalvalue = totalvalue+gstritem.getItem().getTxval();
										}
										if(isNotEmpty(gstritem.getItem().getCamt())) {
											totalvalue = totalvalue+gstritem.getItem().getCamt();
										}
										if(isNotEmpty(gstritem.getItem().getSamt())) {
											totalvalue = totalvalue+gstritem.getItem().getSamt();
										}
										if(isNotEmpty(gstritem.getItem().getIamt())) {
											totalvalue = totalvalue+gstritem.getItem().getIamt();
										}
										if(isNotEmpty(gstritem.getItem().getCsamt())) {
											totalvalue = totalvalue+gstritem.getItem().getCsamt();
										}
									}
								}
								if (returntype.equals(GSTR1)) {
									if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffSalesField()) && !otherconfig.isEnableroundoffSalesField()) {
										totalvalue = (double) Math.round(totalvalue);
									}
								}else {
									if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
										totalvalue = (double) Math.round(totalvalue);
									}
								}
								exstngInv.getB2b().get(0).getInv().get(0).setVal(totalvalue);
								present = true;
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
				}
				InvoiceParent eInv = null;
				if (returntype.equals(GSTR1)) {
					if (isNotEmpty(pInvType)) {
						eInv = gstr1Repository.findByClientidAndInvtypeAndInvoicenoAndFp(client.getId().toString(),
								pInvType, invoice.getB2b().get(0).getInv().get(0).getInum(), invoice.getFp());
					}
				} else {
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
						invoice.setId(eInv.getId());
						invoice.getItems().addAll(eInv.getItems());
					}
				
			}
			if (!present) {
				invoice.setUserid(id);
				invoice.setFullname(fullname);
				invoice.setClientid(clientId);
				invoice.setGstin(client.getGstnnumber());
				if (isNotEmpty(pInvType)) {
					invoice.setInvtype(pInvType);
				}
				Double totalvalue = 0d;
				Double notroundofftamnt = 0d;
				if(isNotEmpty(invoice.getItems())) {
					for(Item item: invoice.getItems()){
						if(isNotEmpty(item) && isNotEmpty(item.getTotal())) {
							totalvalue += item.getTotal();
							notroundofftamnt += item.getTotal();
						}
					}
				}
				if(isNotEmpty(totalvalue)) {
					if (returntype.equals(GSTR1)) {
						if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffSalesField()) && !otherconfig.isEnableroundoffSalesField()) {
							totalvalue = (double) Math.round(totalvalue);
						}
					}else {
						if(isNotEmpty(otherconfig) && isNotEmpty(otherconfig.isEnableroundoffPurField()) && !otherconfig.isEnableroundoffPurField()) {
							totalvalue = (double) Math.round(totalvalue);
						}
					}
				}
				invoice.setTotalamount(totalvalue);
				invoice = populateInvoiceInfo(invoice, returntype, isIntraState);
				
				if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
					if (returntype.equals(GSTR1)) {
						((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setVal(totalvalue);
					}else {
						((PurchaseRegister) invoice).getCdn().get(0).getNt().get(0).setVal(totalvalue);
					}
				}else if(invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
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
				invoice.setTotalamount(notroundofftamnt);
				if (returntype.equals(GSTR1)) {
					existingInvoices.add((GSTR1) invoice);
				} else if (returntype.equals(GSTR4)) {
					existingInvoices.add((GSTR4) invoice);
				} else if (returntype.equals(GSTR5)) {
					existingInvoices.add((GSTR5) invoice);
				} else if (returntype.equals(GSTR6)) {
					existingInvoices.add((GSTR6) invoice);
				} else {
					existingInvoices.add((PurchaseRegister) invoice);
				}
			}
		}
		logger.debug(CLASSNAME + "updateSalesInvoiceData : End");
		return existingInvoices;
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
		
		payments.setPreviousPendingBalance(invoice.getTotalamount());
		payments.setPendingBalance(0d);
		
		payments.setBankAmount(0d);
		payments.setTdsItAmount(0d);
		payments.setTdsGstAmount(0d);
		payments.setDiscountAmount(0d);
		payments.setOthersAmount(0d);
		
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
		
		payments.setCashAmount(invoice.getTotalamount());
		payments.setPaidAmount(invoice.getTotalamount());
		
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
				journal.setVendorName("Other Creditors");
			}
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		String ptDate[] = payment.getPaymentDate().split("-");
		String strmonth = ptDate[1]+ptDate[2];
		journal.setInvoiceMonth(strmonth);
		try {
			journal.setDateofinvoice(dateFormat.parse(payment.getPaymentDate()));
			Date dt = (Date)journal.getDateofinvoice();
			if(isNotEmpty(dt)) {
				int vcmonth = dt.getMonth();
				int vcyear = dt.getYear()+1900;
				int vcquarter = vcmonth/3;
				vcquarter = vcquarter == 0 ? 4 : vcquarter;
				String vcyearCode = vcquarter == 4 ? (vcyear-1)+"-"+vcyear : (vcyear)+"-"+(vcyear+1);
				vcmonth++;
				
				journal.setMthCd(""+vcmonth);
				journal.setYrCd(""+vcyearCode);
				journal.setQrtCd(""+vcquarter);
			}
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
	
	public void saveJournalInvoice(InvoiceParent invoiceForJournal, String clientid, String returntype,
			boolean isIntraState) {
		if (isNotEmpty(invoiceForJournal)) {
			if ("Sales Register".equalsIgnoreCase(returntype) || "SalesRegister".equalsIgnoreCase(returntype)
					|| "GSTR1".equalsIgnoreCase(returntype)) {
				String dealertype = "";
				if (isNotEmpty(invoiceForJournal.getDealerType())) {
					dealertype = invoiceForJournal.getDealerType();
				}
				AccountingJournal journal = accountingJournalRepository
						.findByInvoiceId(invoiceForJournal.getId().toString());
				List<AccountingJournal> journals = accountingJournalRepository.findByClientId(clientid);
				String journalnumber = "1";
				if (isNotEmpty(journals)) {
					journalnumber = journals.size() + 1 + "";
				}
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
					} else if (invoiceForJournal.getInvtype().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
						String exptype = invoiceForJournal.getExp().get(0).getExpTyp();
						journal.setSalesorPurchases(taxableAmount);
						if ("Composition".equals(dealertype)) {
							journal.setRcmorinterorintra("RCM");
							journal.setIgstamount(0d);
							journal.setSgstamount(0d);
							journal.setCgstamount(0d);
						} else {
							if (exptype.equals("WOPAY")) {
								journal.setRcmorinterorintra("RCM");
								journal.setIgstamount(0d);
								journal.setSgstamount(0d);
								journal.setCgstamount(0d);
							} else if(exptype.equals("WPAY")){
								journal.setRcmorinterorintra("Inter");
								journal.setIgstamount(igstAmount);
								journal.setSgstamount(sgstAmount);
								journal.setCgstamount(cgstAmount);
							}else {
								if (isIntraState) {
									journal.setRcmorinterorintra("Intra");
								} else {
									journal.setRcmorinterorintra("Inter");
								}
								journal.setIgstamount(igstAmount);
								journal.setSgstamount(sgstAmount);
								journal.setCgstamount(cgstAmount);
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
						} else {
							if (invtype.equals("SEWOP")) {
								journal.setRcmorinterorintra("RCM");
								journal.setIgstamount(0d);
								journal.setSgstamount(0d);
								journal.setCgstamount(0d);
							} else if(invtype.equals("CBW") || invtype.equals("SEWP")){
								journal.setRcmorinterorintra("Inter");
								journal.setIgstamount(igstAmount);
								journal.setSgstamount(sgstAmount);
								journal.setCgstamount(cgstAmount);
							}else {
								if (isIntraState) {
									journal.setRcmorinterorintra("Intra");
								} else {
									journal.setRcmorinterorintra("Inter");
								}
								journal.setIgstamount(igstAmount);
								journal.setSgstamount(sgstAmount);
								journal.setCgstamount(cgstAmount);
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
						} else {
							if (isIntraState) {
								journal.setRcmorinterorintra("Intra");
							} else {
								journal.setRcmorinterorintra("Inter");
							}
							journal.setIgstamount(igstAmount);
							journal.setSgstamount(sgstAmount);
							journal.setCgstamount(cgstAmount);
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
				accountingJournalRepository.save(journal);
			} else {
				AccountingJournal journal = accountingJournalRepository
						.findByInvoiceId(invoiceForJournal.getId().toString());
				List<AccountingJournal> journals = accountingJournalRepository.findByClientId(clientid);
				String journalnumber = "1";
				if (isNotEmpty(journals)) {
					journalnumber = (journals.size() + 1) + "";
				}
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
				} else if (isNotEmpty(invoiceForJournal.getDateofinvoice())) {
					journal.setDateofinvoice(invoiceForJournal.getDateofinvoice());
					Calendar cal = Calendar.getInstance();
					cal.setTime(invoiceForJournal.getDateofinvoice());
					int months = cal.get(Calendar.MONTH) + 1;
					int years = cal.get(Calendar.YEAR);
					String strMonths = months < 10 ? "0" + months : months + "";
					journal.setInvoiceMonth(strMonths + years);
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
						journal.setIgstamount(igstAmount);
						journal.setSgstamount(sgstAmount);
						journal.setCgstamount(cgstAmount);
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

	private  boolean isScientificNotation(String numberString) {
	    // Validate number
	    try {
	        new BigDecimal(numberString);
	    } catch (NumberFormatException e) {
	        return false;
	    }
	    // Check for scientific notation
	    return numberString.toUpperCase().contains("E") && numberString.charAt(1)=='.';   
	}
	
	private List<GSTRItems> populateItemData(InvoiceParent invoice, boolean isIntraState, String returntype) {
		Double totalAmount = 0d;
		Double totalTax = 0d;
		Double totalTaxableAmt = 0d;
		Double totalITC = 0d;
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
					if (isNotEmpty(item.getCgstrate()) && item.getCgstrate() > 0 && isNotEmpty(item.getSgstrate()) && item.getSgstrate() > 0) {
						gstrItemDetail.setRt(item.getCgstrate() + item.getSgstrate());
					} else if (isNotEmpty(item.getIgstrate()) && item.getIgstrate() > 0) {
						gstrItemDetail.setRt(item.getIgstrate());
					} else if (isEmpty(gstrItemDetail.getRt()) && isNotEmpty(item.getRate()) && item.getRate() > 0) {
						gstrItemDetail.setRt(item.getRate());
						if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
							item.setIgstrate(item.getRate());
						} else {
							item.setCgstrate(item.getRate() / 2);
							item.setSgstrate(item.getRate() / 2);
						}
					} else {
						gstrItemDetail.setRt(0d);
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
						if (isNotEmpty(item.getCgstamount()) && item.getCgstamount() >= 0 && isNotEmpty(item.getSgstamount()) && item.getSgstamount() >= 0) {
							logger.debug(invoice.getInvtype());
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
					} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())
							&& item.getCgstrate() > 0) {
						gstrItemDetail.setRt(item.getCgstrate() + item.getSgstrate());
					} else if (isEmpty(gstrItemDetail.getRt()) && isNotEmpty(item.getRate()) && item.getRate() > 0) {
						gstrItemDetail.setRt(item.getRate());
						if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
							item.setIgstrate(item.getRate());
						} else {
							item.setCgstrate(item.getRate() / 2);
							item.setSgstrate(item.getRate() / 2);
						}
					} else {
						gstrItemDetail.setRt(0d);
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

		invoice.setTotaltax(totalTax);
		invoice.setTotaltaxableamount(totalTaxableAmt);
		invoice.setTotalitc(totalITC);
		return gstrItems;
	}

	private String getStateCode(String strState) {
		List<StateConfig> states = configService.getStates();
		if (isNotEmpty(strState)) {
			for (StateConfig state : states) {
				if (state.getName().equals(strState)) {
					Integer tin = state.getTin();
					return (tin < 10) ? ("0" + tin) : (tin + "");
				} else if (state.getCode().equals(strState)) {
					Integer tin = state.getTin();
					return (tin < 10) ? ("0" + tin) : (tin + "");
				} else if (state.getTin().toString().equals(strState)) {
					Integer tin = state.getTin();
					return (tin < 10) ? ("0" + tin) : (tin + "");
				} else if (state.getName().equals(state.getTin() + "-" + strState)) {
					Integer tin = state.getTin();
					return (tin < 10) ? ("0" + tin) : (tin + "");
				}
			}
			if (strState.contains("-")) {
				strState = strState.substring(0, strState.indexOf("-")).trim();
				return (strState.length() < 2) ? ("0" + strState) : strState;
			}
			if (strState.equals(",")) {
				return "";
			}
		}
		return strState;
	}
	
	private InvoiceParent populateInvoiceInfo(InvoiceParent invoice, final String returntype, final boolean isIntraState) {

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
							List<StateConfig> states = configService.getStates();
							for (StateConfig state : states) {
								String name = state.getName();
								String[] nm = state.getName().split("-");
								if (nm[0].equalsIgnoreCase(statename)) {
									statename = name;
									break;
								}
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
							List<StateConfig> states = configService.getStates();
							for (StateConfig state : states) {
								String name = state.getName();
								String[] nm = state.getName().split("-");
								if (nm[0].equalsIgnoreCase(statename)) {
									statename = name;
									break;
								}
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

		if (isNotEmpty(invoice.getLedgerName())) {
			ProfileLedger ldgr = ledgerRepository.findByClientidAndLedgerNameIgnoreCase(invoice.getClientid(), invoice.getLedgerName());
			if (returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER) || returntype.equals("PurchaseRegister")) {
				if (isEmpty(ldgr)) {
					ProfileLedger lgrdr = new ProfileLedger();
					lgrdr.setClientid(invoice.getClientid());
					if (isNotEmpty(invoice.getLedgerName())) {
						lgrdr.setLedgerName(invoice.getLedgerName());
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
					if (isNotEmpty(invoice.getLedgerName())) {
						lgrdr.setLedgerName(invoice.getLedgerName());
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
				GSTINPublicData gstndata = gstinPublicDataRepository.findByGstin(invoice.getB2b().get(0).getCtin().toUpperCase().trim());
				if(isNotEmpty(gstndata)) {
					if (isNotEmpty(gstndata.getDty())) {
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
		//if (!returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
			if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0))
					&& isNotEmpty(invoice.getB2b().get(0).getInv())
					&& isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())
					&& isNotEmpty(invoice.getDateofinvoice())) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(invoice.getDateofinvoice());
				int month = cal.get(Calendar.MONTH) + 1;
				int year = cal.get(Calendar.YEAR);
				String submissionYear = (year - 1) + "-" + (year);
				if (month > 3) {
					submissionYear = year + "-" + (year + 1);
				}
				String invType = "";
				if (invoice.getInvtype().equals(MasterGSTConstants.B2B)
						|| invoice.getInvtype().equals(MasterGSTConstants.B2C)
						|| invoice.getInvtype().equals(MasterGSTConstants.B2CL)) {
					invType = MasterGSTConstants.B2B;
					if(returntype.equals("Purchase Register")){
						invType = "PurchaseReverseChargeNo";
					}
				} else if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
					if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())){
						if(invoice.getCdn().get(0).getNt().get(0).getNtty().equalsIgnoreCase("D")){
							invType = "Debit Note";
						}else if(invoice.getCdn().get(0).getNt().get(0).getNtty().equalsIgnoreCase("C")){
							invType = "Credit Note";
						}
					}
				} else if(returntype.equals("Purchase Register")){
					invType = "PurchaseReverseChargeNo";
				}else {
					invType = invoice.getInvtype();
				}
				CompanyInvoices invoiceConfig = profileService.getInvoiceConfigDetails(invoice.getClientid(),
						submissionYear, invType, returntype);
				String revNo="";
				if(returntype.equals("Purchase Register")) {
					if(isNotEmpty(invoice) && isNotEmpty(invoice.getRevchargeNo())) {
						revNo = invoice.getRevchargeNo();
					}
				}else {
					revNo = invoice.getB2b().get(0).getInv().get(0).getInum();
				}
				if (isNotEmpty(invoiceConfig) && (isEmpty(invoiceConfig.getPrefix()) || (isNotEmpty(invoiceConfig.getPrefix()) && revNo.startsWith(invoiceConfig.getPrefix())))) {
					String startInvNo = "";
					String endInvNo = "";
					String invPrefix = "";
					String actualNo = "";
					if(returntype.equals("Purchase Register")) {
						if(isNotEmpty(invoice) && isNotEmpty(invoice.getRevchargeNo())) {
							actualNo = invoice.getRevchargeNo();
						}
					}else {
						actualNo = invoice.getB2b().get(0).getInv().get(0).getInum();
					}
					if (isNotEmpty(invoiceConfig.getPrefix())) {
						startInvNo += invoiceConfig.getPrefix().toUpperCase();
						endInvNo += invoiceConfig.getPrefix().toUpperCase();
						invPrefix += invoiceConfig.getPrefix().toUpperCase();
						actualNo = actualNo.replaceFirst(invoiceConfig.getPrefix(), "");
					}
					if (isNotEmpty(invoiceConfig.getAllowMonth()) && invoiceConfig.getAllowMonth().equals("true")) {

						if (isNotEmpty(invoiceConfig.getFormatMonth())) {
							if (invoiceConfig.getFormatMonth().equals("02")) {
								invPrefix += (month < 10 ? "0" + month : month);
								startInvNo += (month < 10 ? "0" + month : month);
								endInvNo += (month < 10 ? "0" + month : month);
								actualNo = actualNo.replaceFirst(
										(month < 10 ? "0" + month : Integer.valueOf(month).toString()), "");
							} else if (invoiceConfig.getFormatMonth().equals("02/")) {
								invPrefix += (month < 10 ? "0" + month + "/" : month + "/");
								startInvNo += (month < 10 ? "0" + month + "/" : month + "/");
								endInvNo += (month < 10 ? "0" + month + "/" : month + "/");
								actualNo = actualNo.replaceFirst(
										(month < 10 ? "0" + month : Integer.valueOf(month).toString()) + "/", "");
							} else if (invoiceConfig.getFormatMonth().equals("02-")) {
								invPrefix += (month < 10 ? "0" + month + "-" : month + "-");
								startInvNo += (month < 10 ? "0" + month + "-" : month + "-");
								endInvNo += (month < 10 ? "0" + month + "-" : month + "-");
								actualNo = actualNo.replaceFirst(
										(month < 10 ? "0" + month : Integer.valueOf(month).toString()) + "-", "");
							} else if (invoiceConfig.getFormatMonth().equals("FEB")) {
								String monthString = getMonthName(month);
								invPrefix += monthString;
								startInvNo += monthString;
								endInvNo += monthString;
								actualNo = actualNo.replaceFirst(monthString, "");
							} else if (invoiceConfig.getFormatMonth().equals("FEB/")) {
								String monthString = getMonthName(month) + "/";
								invPrefix += monthString;
								startInvNo += monthString;
								endInvNo += monthString;
								actualNo = actualNo.replaceFirst(monthString, "");
							} else if (invoiceConfig.getFormatMonth().equals("FEB-")) {
								String monthString = getMonthName(month) + "-";
								invPrefix += monthString;
								startInvNo += monthString;
								endInvNo += monthString;
								actualNo = actualNo.replaceFirst(monthString, "");
							}
						} else {
							invPrefix += (month < 10 ? "0" + month : month);
							startInvNo += (month < 10 ? "0" + month : month);
							endInvNo += (month < 10 ? "0" + month : month);
							actualNo = actualNo
									.replaceFirst((month < 10 ? "0" + month : Integer.valueOf(month).toString()), "");
						}
					}
					if (isNotEmpty(invoiceConfig.getAllowYear()) && invoiceConfig.getAllowYear().equals("true")) {

						if (isNotEmpty(invoiceConfig.getFormatYear())) {
							if (invoiceConfig.getFormatYear().equals("19")) {
								invPrefix += (Integer.valueOf(year).toString().substring(2));
								startInvNo += (Integer.valueOf(year).toString().substring(2));
								endInvNo += (Integer.valueOf(year).toString().substring(2));
								actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2)), "");
							} else if (invoiceConfig.getFormatYear().equals("19/")) {
								invPrefix += (Integer.valueOf(year).toString().substring(2)) + "/";
								startInvNo += (Integer.valueOf(year).toString().substring(2)) + "/";
								endInvNo += (Integer.valueOf(year).toString().substring(2)) + "/";
								actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2)) + "/",
										"");
							} else if (invoiceConfig.getFormatYear().equals("19-")) {
								invPrefix += (Integer.valueOf(year).toString().substring(2)) + "-";
								startInvNo += (Integer.valueOf(year).toString().substring(2)) + "-";
								endInvNo += (Integer.valueOf(year).toString().substring(2)) + "-";
								actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2)) + "-",
										"");
							} else if (invoiceConfig.getFormatYear().equals("2019")) {
								invPrefix += (Integer.valueOf(year).toString());
								startInvNo += (Integer.valueOf(year).toString());
								endInvNo += (Integer.valueOf(year).toString());
								actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()), "");
							} else if (invoiceConfig.getFormatYear().equals("2019/")) {
								invPrefix += (Integer.valueOf(year).toString()) + "/";
								startInvNo += (Integer.valueOf(year).toString()) + "/";
								endInvNo += (Integer.valueOf(year).toString()) + "/";
								actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()) + "/", "");
							} else if (invoiceConfig.getFormatYear().equals("2019-")) {
								invPrefix += (Integer.valueOf(year).toString()) + "-";
								startInvNo += (Integer.valueOf(year).toString()) + "-";
								endInvNo += (Integer.valueOf(year).toString()) + "-";
								actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()) + "-", "");
							} else if (invoiceConfig.getFormatYear().equals("19-20")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2));
									startInvNo += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2));
									endInvNo += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString().substring(2))
											+ "-" + (Integer.valueOf(year).toString().substring(2)), "");
								} else {
									invPrefix += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2));
									startInvNo += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2));
									endInvNo += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2))
											+ "-" + (Integer.valueOf(year + 1).toString().substring(2)), "");
								}
							} else if (invoiceConfig.getFormatYear().equals("19-20-")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString().substring(2))
											+ "-" + (Integer.valueOf(year).toString().substring(2)) + "-", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2))
											+ "-" + (Integer.valueOf(year + 1).toString().substring(2)) + "-", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("19-20/")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString().substring(2))
											+ "-" + (Integer.valueOf(year).toString().substring(2)) + "/", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2))
											+ "-" + (Integer.valueOf(year + 1).toString().substring(2)) + "/", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("1920")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2));
									startInvNo += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2));
									endInvNo += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)), "");
								} else {
									invPrefix += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2));
									startInvNo += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2));
									endInvNo += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)), "");
								}
							} else if (invoiceConfig.getFormatYear().equals("1920-")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "-", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("1920/")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "/", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("2019-20")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2));
									startInvNo += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2));
									endInvNo += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)), "");
								} else {
									invPrefix += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2));
									startInvNo += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2));
									endInvNo += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)), "");
								}
							} else if (invoiceConfig.getFormatYear().equals("2019-20-")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("2019-20/")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("201920")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2));
									startInvNo += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2));
									endInvNo += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)), "");
								} else {
									invPrefix += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2));
									startInvNo += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2));
									endInvNo += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)), "");
								}
							} else if (invoiceConfig.getFormatYear().equals("201920-")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "-", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("201920/")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "/", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/", "");
								}
							}
						} else {
							invPrefix += (Integer.valueOf(year).toString());
							startInvNo += (Integer.valueOf(year).toString());
							endInvNo += (Integer.valueOf(year).toString());
							actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()), "");
						}
					}
					String ssinvno = startInvNo;
					if (isNotEmpty(invoiceConfig.getStartInvoiceNo())) {
						startInvNo += invoiceConfig.getStartInvoiceNo();
					}
					if (isNotEmpty(invoiceConfig.getEndInvoiceNo())) {
						endInvNo += invoiceConfig.getEndInvoiceNo();
					}
					String invOrRevNo = "";
					if(returntype.equals("Purchase Register")) {
						if(isNotEmpty(invoice) && isNotEmpty(invoice.getRevchargeNo())) {
							invOrRevNo = invoice.getRevchargeNo();
						}
					}else {
						invOrRevNo = invoice.getB2b().get(0).getInv().get(0).getInum();
					}
					if (invOrRevNo.startsWith(ssinvno)) {
						ClientAddlInfo clientInfo = clientAddlInfoRepository.findByClientIdAndReturnTypeAndInvoiceTypeAndFinancialYearAndMonth(invoice.getClientid(), returntype, invType, submissionYear, month);
						if (isEmpty(clientInfo)) {
							clientInfo = clientAddlInfoRepository.findByClientIdAndReturnTypeAndInvoiceTypeAndFinancialYearAndMonth(invoice.getClientid(), returntype, "ALL", submissionYear, month);
						}
						if (isEmpty(clientInfo)) {
							clientInfo = new ClientAddlInfo();
							clientInfo.setClientId(invoice.getClientid());
							clientInfo.setReturnType(returntype);
							clientInfo.setFinancialYear(submissionYear);
							clientInfo.setMonth(month);
							try {
								Integer invNo = Integer.parseInt(actualNo);
								invNo++;
								clientInfo.setInvoiceNo(invPrefix + invNo);
								if (isNotEmpty(invoiceConfig.getInvoiceType()) && !invoiceConfig.equals("ALL")) {
									clientInfo.setInvoiceType(invoiceConfig.getInvoiceType());
								}
								clientAddlInfoRepository.save(clientInfo);
							} catch (Exception e) {
							}
						} else {
							String invnum = invoice.getB2b().get(0).getInv().get(0).getInum();
							if("PurchaseReverseChargeNo".equals(invType)) {
								if(isNotEmpty(invoice.getRevchargeNo())) {
									invnum = invoice.getRevchargeNo();
								}
							}
							
							if (clientInfo.getInvoiceNo().compareTo(invnum) <= 0) {
								try {
									Integer invNo = Integer.parseInt(actualNo);
									invNo++;
									clientInfo.setInvoiceNo(invPrefix + invNo);
									if (isNotEmpty(invoiceConfig.getInvoiceType()) && !invoiceConfig.equals("ALL")) {
										clientInfo.setInvoiceType(invoiceConfig.getInvoiceType());
									}
									clientAddlInfoRepository.save(clientInfo);
								} catch (Exception e) {
								}
							}
						}
					}
				}
			}
		//}
		List<GSTRItems> gstrItems = Lists.newArrayList();
		String stateTin = getStateCode(invoice.getStatename());
		if (isNotEmpty(invoice.getItems())) {
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
										// tally nill supplies facing problem (tally import)
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
					if (isEmpty(item.getCategory())) {
						if (isNotEmpty(item.getHsn()) && item.getHsn().contains(" : ")) {
							item.setCategory(MasterGSTConstants.GOODS);
						} else {
							item.setCategory(MasterGSTConstants.SERVICES);
						}
					}
					if ((isNotEmpty(item.getIgstrate()) || isNotEmpty(item.getCgstrate()))
							|| (isNotEmpty(item.getHsn()) || isNotEmpty(item.getSac()) || isNotEmpty(item.getRate()))) {
						if (isNotEmpty(item.getRate()) && item.getRate() > 0) {
							if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0
									&& isEmpty(item.getIgstrate())) {
								item.setIgstrate(item.getRate());
							} else if (isNotEmpty(item.getCgstamount()) && item.getCgstamount() > 0
									&& isNotEmpty(item.getSgstamount()) && isEmpty(item.getCgstrate())
									&& isEmpty(item.getSgstrate())) {
								item.setCgstrate(item.getRate() / 2);
								item.setSgstrate(item.getRate() / 2);
							}
						}
						fItems.add(item);
					}
				}
				invoice.setItems(fItems);
			}
			if (!invoice.getInvtype().equals(MasterGSTConstants.ATPAID)) {
				if (invoice.getInvtype().equals(MasterGSTConstants.IMP_GOODS) || invoice.getInvtype().equals(MasterGSTConstants.IMP_SERVICES)) {
					gstrItems = populateItemData(invoice, false, returntype);
				}else {
					gstrItems = populateItemData(invoice, isIntraState, returntype);
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
					invoice.setInvtype(B2CL);
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
				} else {
					invoice.setInvtype(B2C);
					if (isEmpty(invoice.getB2cs())) {
						List<GSTRB2CS> b2cs = Lists.newArrayList();
						invoice.setB2cs(b2cs);
					}
					List<GSTRB2CS> b2cs = Lists.newArrayList();
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
						String ecomGSTIN = invoice.getB2cs().get(0).getEtin();
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
						b2cs.add(gstrb2csDetail);
					}
					invoice.setB2cs(b2cs);
				}
			} else {
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
				if (isEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
					invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
				} else {
					String invTyp = invoice.getB2b().get(0).getInv().get(0).getInvTyp();
					if ("Regular".equals(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
					} else if ("Deemed Exports".equals(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("DE");
					} else if ("Supplies to SEZ with payment".equals(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWP");
					} else if ("Supplies to SEZ without payment".equals(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWOP");
					} else if ("Sale from Bonded Warehouse".equals(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("CBW");
					} else {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp(invTyp);
					}
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
						invoice.getB2b().get(0).getInv().get(0)
								.setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
					}
					if (isEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
					} else {
						invoice.getB2b().get(0).getInv().get(0)
								.setInvTyp(invoice.getB2b().get(0).getInv().get(0).getInvTyp());
					}
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

					String ecomGSTIN = invoice.getB2cs().get(0).getEtin();
					List<GSTRB2CS> b2cs = Lists.newArrayList();
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
						b2cs.add(gstrb2csDetail);
					}
					invoice.setB2cs(b2cs);
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
			} else if (invoice.getInvtype().equals(ATPAID) || invoice.getInvtype().equals(MasterGSTConstants.TXPA)) {
				Double totalAmount = 0d;
				Double totalTax = 0d;
				Double totalTaxableAmt = 0d;
				Double totalITC = 0d;
				Client client = findById(invoice.getClientid());
				List<GSTRAdvanceTax> txpd = Lists.newArrayList();
				for (Item item : invoice.getItems()) {
					GSTRAdvanceTax gstrtxpdDetails = new GSTRAdvanceTax();
					List<GSTRItems> gstradvItems = Lists.newArrayList();
					String advStateTin = getStateCode(item.getAdvStateName());
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
				String diffper = invoice.getDiffPercent();
				if ("Yes".equals(diffper)) {
					invoice.getTxpd().get(0).setDiffPercent(0.65);
				}
			} else if (invoice.getInvtype().equals(NIL)) {
				
			} else if (invoice.getInvtype().equals(EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)) {
				invoice.setB2cs(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
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
						invoice.getExp().get(0).getInv().get(0)
								.setInum(invoice.getB2b().get(0).getInv().get(0).getInum());
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
			} else if (invoice.getInvtype().equals(CREDIT_DEBIT_NOTES)
					|| invoice.getInvtype().equals(MasterGSTConstants.CDNA)) {

				if (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())) {
					invoice.setInvtype(CDNUR);
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
				} else {
					invoice.setB2cs(Lists.newArrayList());
					invoice.setExp(Lists.newArrayList());
					invoice.setTxpd(Lists.newArrayList());
					invoice.setCdnur(Lists.newArrayList());
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
						notes.get(0).getNt().get(0).setInum(notes.get(0).getNt().get(0).getNtNum());
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
						if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
							notes.get(0).getNt().get(0).setNtNum(invoice.getB2b().get(0).getInv().get(0).getInum());
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
				invoice.getB2cl().get(0).getInv().get(0).setItms(gstrItems);
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
							b2cs.add(gstrb2csDetail);
						}
						invoice.setB2cs(b2cs);
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
				String ecomGSTIN = invoice.getB2cs().get(0).getEtin();
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
				Client client = findById(invoice.getClientid());
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
		if("Advances".equals(invoice.getInvtype()) || "Nil Supplies".equals(invoice.getInvtype()) || "Advance Adjusted Detail".equals(invoice.getInvtype())){
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
			Client client = findById(invoice.getClientid());
			invoice = einvoiceService.populateEinvoiceDetails(invoice, client);
		}
		savetcstdsamount(returntype,invoice);
		invoice.setCsftr(1);
		return invoice;
	}
	
	public String getMonthName(int month) {
		String monthString;
		switch (month) {
		case 1:
			monthString = "JAN";
			break;
		case 2:
			monthString = "FEB";
			break;
		case 3:
			monthString = "MAR";
			break;
		case 4:
			monthString = "APR";
			break;
		case 5:
			monthString = "MAY";
			break;
		case 6:
			monthString = "JUN";
			break;
		case 7:
			monthString = "JUL";
			break;
		case 8:
			monthString = "AUG";
			break;
		case 9:
			monthString = "SEP";
			break;
		case 10:
			monthString = "OCT";
			break;
		case 11:
			monthString = "NOV";
			break;
		case 12:
			monthString = "DEC";
			break;
		default:
			monthString = "Invalid month";
			break;
		}
		return monthString;
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
					try {
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
						advReceiptinv.setItems(items);
						saveSalesInvoice(advReceiptinv, isIntraState);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			} else if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)	|| returntype.equals(MasterGSTConstants.GSTR2)) {
				PurchaseRegister inv = purchaseRepository.findByClientidAndInvtypeAndInvoiceno(invoice.getClientid(), MasterGSTConstants.ADVANCES, item.getAdvReceiptNo());
				Double advRemainingAmount = item.getAdvAdjustableAmount() - item.getTotal();
				if (isNotEmpty(inv)) {
					inv.setAdvRemainingAmount(advRemainingAmount);
					purchaseRepository.save((PurchaseRegister) inv);
				} else {
					try {

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
						savePurchaseRegister(advReceiptinv, isIntraState);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
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
				} else if (isNotEmpty(item.getIgstrate()) && item.getIgstrate() > 0) {
					gstrItemDetail.setRt(item.getIgstrate());
				} else if (isEmpty(gstrItemDetail.getRt()) && isNotEmpty(item.getRate()) && item.getRate() > 0) {
					gstrItemDetail.setRt(item.getRate());
					if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
						item.setIgstrate(item.getRate());
					} else {
						item.setCgstrate(item.getRate() / 2);
						item.setSgstrate(item.getRate() / 2);
					}
				} else {
					gstrItemDetail.setRt(0d);
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
				} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate()) && item.getCgstrate() > 0) {
					gstrItemDetail.setRt(item.getCgstrate() + item.getSgstrate());
				} else if (isEmpty(gstrItemDetail.getRt()) && isNotEmpty(item.getRate()) && item.getRate() > 0) {
					gstrItemDetail.setRt(item.getRate());
					if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
						item.setIgstrate(item.getRate());
					} else {
						item.setCgstrate(item.getRate() / 2);
						item.setSgstrate(item.getRate() / 2);
					}
				} else {
					gstrItemDetail.setRt(0d);
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
	
	@Override
	@Transactional
	public InvoiceParent saveSalesInvoice(InvoiceParent invoice, final boolean isIntraState)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug(CLASSNAME + "saveSalesInvoice : Begin");
		invoice = populateInvoiceInfo(invoice, GSTR1, isIntraState);
		gstr1Repository.save((GSTR1) invoice);
		if(invoice.getInvtype().equals(ADVANCES) || invoice.getInvtype().equals(MasterGSTConstants.ATA)) {
			saveAdvancePayments(invoice,GSTR1);
		}
		return invoice;
	}
	
	@Override
	@Transactional
	public InvoiceParent savePurchaseRegister(InvoiceParent invoice, final boolean isIntraState)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug(CLASSNAME + "savePurchaseRegister : Begin");
		if (isNotEmpty(invoice.getNotes())) {
			String notes = invoice.getNotes();
			notes = notes.substring(1);
			invoice.setNotes(notes);
		}
		invoice = populateInvoiceInfo(invoice, PURCHASE_REGISTER, isIntraState);
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

}
