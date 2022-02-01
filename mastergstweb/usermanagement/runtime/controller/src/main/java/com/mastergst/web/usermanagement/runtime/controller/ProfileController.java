/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.CUSTOMERS;
import static com.mastergst.core.common.MasterGSTConstants.DEALER_COMPOUND;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.FILING_OPTION_MONTHLY;
import static com.mastergst.core.common.MasterGSTConstants.FILING_OPTION_QUARTERLY;
import static com.mastergst.core.common.MasterGSTConstants.ITEMS;
import static com.mastergst.core.common.MasterGSTConstants.PRODUCTS;
import static com.mastergst.core.common.MasterGSTConstants.SERVICES;
import static com.mastergst.core.common.MasterGSTConstants.SUPPLIERS;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jxls.template.SimpleExporter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.amazonaws.regions.Regions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.mastergst.configuration.service.CommonLedgerRepository;
import com.mastergst.configuration.service.Commonledger;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.EmailService;
import com.mastergst.configuration.service.GoogleDriveAdapter;
import com.mastergst.configuration.service.Groups;
import com.mastergst.configuration.service.GroupsRepository;
import com.mastergst.configuration.service.SMSService;
import com.mastergst.configuration.service.Subgroup;
import com.mastergst.core.common.AccountConstants;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.MasterGstExecutorService;
import com.mastergst.core.util.NullUtil;
import com.mastergst.core.util.VmUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.dao.MultiGSTNDao;
import com.mastergst.usermanagement.runtime.domain.AccountingHeads;
import com.mastergst.usermanagement.runtime.domain.AllGSTINTempData;
import com.mastergst.usermanagement.runtime.domain.AuditorAdrressDetails;
import com.mastergst.usermanagement.runtime.domain.Branch;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientConfig;
import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;
import com.mastergst.usermanagement.runtime.domain.CommonBO;
import com.mastergst.usermanagement.runtime.domain.CompanyBankDetails;
import com.mastergst.usermanagement.runtime.domain.CompanyCenters;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CompanyGroup;
import com.mastergst.usermanagement.runtime.domain.CompanyInvoices;
import com.mastergst.usermanagement.runtime.domain.CompanyItems;
import com.mastergst.usermanagement.runtime.domain.CompanyProducts;
import com.mastergst.usermanagement.runtime.domain.CompanyRole;
import com.mastergst.usermanagement.runtime.domain.CompanyServices;
import com.mastergst.usermanagement.runtime.domain.CompanySuppliers;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.CostCenter;
import com.mastergst.usermanagement.runtime.domain.CustomData;
import com.mastergst.usermanagement.runtime.domain.CustomFields;
import com.mastergst.usermanagement.runtime.domain.DeletedInvoices;
import com.mastergst.usermanagement.runtime.domain.EcommerceOpeartor;
import com.mastergst.usermanagement.runtime.domain.EinvoiceConfigurations;
import com.mastergst.usermanagement.runtime.domain.EinvoiceCustomFields;
import com.mastergst.usermanagement.runtime.domain.EwayBillConfigurations;
import com.mastergst.usermanagement.runtime.domain.EwayBillCustomFields;
import com.mastergst.usermanagement.runtime.domain.Expenses;
import com.mastergst.usermanagement.runtime.domain.ExpensesFilter;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicDataVO;
import com.mastergst.usermanagement.runtime.domain.GSTReturnSummary;
import com.mastergst.usermanagement.runtime.domain.GroupDetails;
import com.mastergst.usermanagement.runtime.domain.InvoiceCutomFields;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.MgstResponse;
import com.mastergst.usermanagement.runtime.domain.MultiGSTNData;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.Permission;
import com.mastergst.usermanagement.runtime.domain.PrintConfiguration;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;
import com.mastergst.usermanagement.runtime.domain.Reminders;
import com.mastergst.usermanagement.runtime.domain.SmtpDetails;
import com.mastergst.usermanagement.runtime.domain.StockAdjustments;
import com.mastergst.usermanagement.runtime.domain.Subgroups;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;
import com.mastergst.usermanagement.runtime.domain.TurnoverOptions;
import com.mastergst.usermanagement.runtime.domain.UpdateClients;
import com.mastergst.usermanagement.runtime.domain.Vertical;
import com.mastergst.usermanagement.runtime.repository.AllGSTnoTempRepository;
import com.mastergst.usermanagement.runtime.repository.ClientRepository;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyBankDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyCustomersRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyItemsRepository;
import com.mastergst.usermanagement.runtime.repository.CompanySuppliersRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyUserRepository;
import com.mastergst.usermanagement.runtime.repository.CustomFieldsRepository;
import com.mastergst.usermanagement.runtime.repository.ExpensesRepository;
import com.mastergst.usermanagement.runtime.repository.GSTINPublicDataRepository;
import com.mastergst.usermanagement.runtime.repository.GroupDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.LedgerRepository;
import com.mastergst.usermanagement.runtime.repository.MultiGSTnRepository;
import com.mastergst.usermanagement.runtime.repository.RemindersRepository;
import com.mastergst.usermanagement.runtime.repository.StockAdjustmentRepository;
import com.mastergst.usermanagement.runtime.response.MultiGstinVO;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.service.AccountingUtils;
import com.mastergst.usermanagement.runtime.service.AuditorService;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ClientUtils;
import com.mastergst.usermanagement.runtime.service.EinvoiceService;
import com.mastergst.usermanagement.runtime.service.IHubConsumerService;
import com.mastergst.usermanagement.runtime.service.ImportMapperService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.SmtpDetailsService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

/**
 * Handles Profile related CRUD operations depending on the URI template.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 * @since 1.0
 */
@Controller
public class ProfileController {

	private static final Logger logger = LogManager.getLogger(ProfileController.class.getName());
	private static final String CLASSNAME = "ProfileController::";
	@Autowired	MasterGstExecutorService masterGstExecutorService;
	@Autowired	SMSService smsService;
	@Autowired	RemindersRepository remindersRepository;
	@Autowired	private EmailService emailService;
	@Autowired	private EinvoiceService einvoiceService;
	@Autowired	ClientService clientService;
	@Autowired	ConfigService configService;
	@Autowired	ProfileService profileService;
	@Autowired	UserService userService;
	@Autowired	AuditorService auditorService;
	@Autowired	SubscriptionService subscriptionService;
	@Autowired	GoogleDriveAdapter googleDriveAdapter;
	@Autowired	GridFsOperations gridOperations;
	@Autowired	ClientUserMappingRepository clientUserMappingRepository;
	@Autowired	CompanyUserRepository companyUserRepository;
	@Autowired	GroupDetailsRepository groupDetailsRepository;
	@Autowired	GroupsRepository groupsRepository;
	@Autowired	LedgerRepository ledgerRepository;
	@Autowired	CommonLedgerRepository commonLedgerRepository;
	@Autowired	private IHubConsumerService iHubConsumerService;
	@Autowired	private GSTINPublicDataRepository gstinPublicDataRepository;
	@Autowired	private AllGSTnoTempRepository allGSTnoTempRepository;
	@Autowired	UserRepository userRepository;
	@Autowired	ClientRepository clientRepository;
	@Autowired	CompanyCustomersRepository companyCustomersRepository;
	@Autowired	CompanySuppliersRepository companySuppliersRepository;
	@Autowired	CompanyItemsRepository companyItemsRepository;
	@Autowired	SmtpDetailsService smtpDetailsService;
	@Autowired	private ImportMapperService importMapperService;
	@Autowired	private MultiGSTnRepository multiGSTnRepository;
	@Autowired	MultiGSTNDao multiGSTNDao;
	@Autowired	AllGSTnoTempRepository allgstnoTempRepository;
	@Autowired	CompanyBankDetailsRepository companyBankDetailsRepository;
	@Autowired	CustomFieldsRepository customFieldsRepository;
	@Autowired	ClientUtils clientUtils;
	@Autowired
	private AccountingUtils accountUtils;
	
	private String gstnFormat = "[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}|[0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1}|[0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1}|[0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1}|[9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1}";
	
	@RequestMapping(value = "/about/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String aboutProfileView(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model)
			throws Exception {
		final String method = "aboutProfileView::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);

		Client dbClient = clientService.findById(clientid);
		
		List<TurnoverOptions> turnoveroptionLst=new ArrayList<TurnoverOptions>();
		Set<String> set=new HashSet<String>();
		if(isNotEmpty(dbClient.getTurnovergoptions())) {
			dbClient.getTurnovergoptions().forEach(optn->{
				set.add(optn.getYear());
			});
			if(!set.contains("APR2017-JUN2017")){
				turnoveroptionLst.add(new TurnoverOptions(new ObjectId(), "APR2017-JUN2017", 0d));
				for(TurnoverOptions option:dbClient.getTurnovergoptions()) {
					turnoveroptionLst.add(option);
				}
				dbClient.setTurnovergoptions(turnoveroptionLst);
			}
		}else {
			turnoveroptionLst.add(new TurnoverOptions(new ObjectId(), "APR2017-JUN2017", 0d));			
			dbClient.setTurnovergoptions(turnoveroptionLst);
		}
		clientService.saveClient(dbClient);
		
		
		User user = userService.findById(id);
		Client client = clientService.findById(clientid);
		/*
		if(isNotEmpty(client)) {
			if(isEmpty(client.getIsgroupexist()) || !client.getIsgroupexist()) {
				List<GroupDetails> groupdtls = Lists.newArrayList();
				List<Groups> defgrps = groupsRepository.findAll();
				for(Groups grps : defgrps) {
					GroupDetails grpdetails = new GroupDetails();
					grpdetails.setClientid(client.getId().toString());
					grpdetails.setGroupname(grps.getName());
					grpdetails.setHeadname(grps.getHeadname());
					grpdetails.setPath(grps.getPath());
					List<Subgroups> subgroup = Lists.newArrayList();
					if(isNotEmpty(grps.getSubgroup())) {
						for(Subgroup subgrp : grps.getSubgroup()) {
							if(isNotEmpty(subgrp) && isNotEmpty(subgrp.getSubgroupname())) {
								Subgroups subgrps = new Subgroups();
								subgrps.setGroupname(subgrp.getSubgroupname());
								subgrps.setHeadname(grpdetails.getGroupname());
								subgrps.setPath(grpdetails.getPath()+"/"+subgrp.getSubgroupname());
								subgrps.setGroupid(grpdetails.getId().toString());
								subgroup.add(subgrps);
							}
						}
					}
					grpdetails.setSubgroups(subgroup);
					groupdtls.add(grpdetails);
				}
				groupDetailsRepository.save(groupdtls);
				
				List<ProfileLedger> ledger = Lists.newArrayList();
				ProfileLedger lgrdr = new ProfileLedger();
				lgrdr.setClientid(clientid);	
				lgrdr.setLedgerName("Other Debtors");
				lgrdr.setGrpsubgrpName("Sundry Debtors");
				lgrdr.setLedgerpath("Assets/Current Assets/Sundry Debtors");
				lgrdr.setLedgerOpeningBalance(0);
				ledger.add(lgrdr);
				ProfileLedger lgrcr = new ProfileLedger();
				lgrcr.setClientid(clientid);	
				lgrcr.setLedgerName("Other Creditors");
				lgrcr.setGrpsubgrpName("Sundry Creditors");
				lgrcr.setLedgerpath("Assets/Current Assets/Sundry Creditors");
				lgrcr.setLedgerOpeningBalance(0);		
				ledger.add(lgrcr);
			
				ledgerRepository.save(ledger);
				client.setIsgroupexist(true);
				clientService.saveClient(client);
			}
			
			if(isEmpty(client.getIscommonotherledgerexist()) || !client.getIscommonotherledgerexist()) {
				clientUtils.saveDefaultGroupDetails(clientid);
				
				List<ProfileLedger> ledger = Lists.newArrayList();
				ProfileLedger hoisd = new ProfileLedger();
				hoisd.setClientid(clientid);	
				hoisd.setLedgerName("HO-ISD");
				hoisd.setGrpsubgrpName("Output Tax");
				hoisd.setLedgerpath("Liabilities/Current liabilities/Duties & Taxes - Liabilities/Output Tax");
				hoisd.setLedgerOpeningBalance(0);
				ledger.add(hoisd);
				
				ProfileLedger tdsPayable = new ProfileLedger();
				tdsPayable.setClientid(clientid);	
				tdsPayable.setLedgerName("TDS Payable");
				tdsPayable.setGrpsubgrpName("Output Tax");
				tdsPayable.setLedgerpath("Liabilities/Current liabilities/Duties & Taxes - Liabilities/Output Tax");
				tdsPayable.setLedgerOpeningBalance(0);
				ledger.add(tdsPayable);
				
				
				ProfileLedger ineligible = new ProfileLedger();
				ineligible.setClientid(clientid);	
				ineligible.setLedgerName("Ineligible GST Credit Receivable");
				ineligible.setGrpsubgrpName("Other expenses");
				ineligible.setLedgerpath("Expenditure/Indirect Expenses/Other expenses");
				ledger.add(ineligible);
				
				ProfileLedger others = new ProfileLedger();
				others.setClientid(clientid);	
				others.setLedgerName("Others");
				others.setGrpsubgrpName("Suspense Ac");
				others.setLedgerpath("Suspense Ac/Others");
				ledger.add(others);
				
				ledgerRepository.save(ledger);
				client.setIscommonotherledgerexist(true);
				clientService.saveClient(client);
			}
			
			if(isEmpty(client.getIscommonbankledgerexist()) || !client.getIscommonbankledgerexist()) {
				List<ProfileLedger> ledger = Lists.newArrayList();
				ProfileLedger lgrbank = new ProfileLedger();
				lgrbank.setClientid(clientid);	
				lgrbank.setLedgerName("Bank");
				lgrbank.setGrpsubgrpName("Bank");
				lgrbank.setLedgerpath("Assets/Current Assets/Bank");
				ledger.add(lgrbank);
				
				ProfileLedger tdsit = new ProfileLedger();
				tdsit.setClientid(clientid);	
				tdsit.setLedgerName("TDS-IT");
				tdsit.setGrpsubgrpName("TDS Receivables");
				tdsit.setLedgerpath("Assets/Current Assets/Duties & Taxes - Assets/TDS Receivables");
				ledger.add(tdsit);
				
				ProfileLedger tdsgst = new ProfileLedger();
				tdsgst.setClientid(clientid);	
				tdsgst.setLedgerName("TDS-GST");
				tdsgst.setGrpsubgrpName("TDS Receivables");
				tdsgst.setLedgerpath("Assets/Current Assets/Duties & Taxes - Assets/TDS Receivables");
				ledger.add(tdsgst);
				
				ProfileLedger discount = new ProfileLedger();
				discount.setClientid(clientid);	
				discount.setLedgerName("Discount");
				discount.setGrpsubgrpName("Other expenses");
				discount.setLedgerpath("Expenditure/Indirect Expenses/Other expenses");
				ledger.add(discount);
				ledgerRepository.save(ledger);
				client.setIscommonbankledgerexist(true);
				clientService.saveClient(client);
			}
			if(isEmpty(client.getIscommonledgerexist()) || !client.getIscommonledgerexist()) {
				List<ProfileLedger> legerdtls = Lists.newArrayList();
				List<Commonledger> defledgers = commonLedgerRepository.findAll();
				
				for(Commonledger ldgr : defledgers) {
					ProfileLedger lgrdr = new ProfileLedger();
					lgrdr.setClientid(clientid);	
					lgrdr.setLedgerName(ldgr.getLedgerName());
					lgrdr.setGrpsubgrpName(ldgr.getGrpsubgrpName());
					lgrdr.setLedgerpath(ldgr.getLedgerpath());
					legerdtls.add(lgrdr);
				}
				
				List<CompanyCustomers> customers= profileService.getCustomers(clientid);
				if(isNotEmpty(customers)) {
					for(CompanyCustomers customer : customers) {
						
							ProfileLedger lgrdr = new ProfileLedger();
							lgrdr.setClientid(clientid);
							if(isNotEmpty(customer.getCustomerLedgerName())) {
								lgrdr.setLedgerName(customer.getCustomerLedgerName());
							}else {
								lgrdr.setLedgerName(customer.getName());
							}
							lgrdr.setGrpsubgrpName("Sundry Debtors");
							lgrdr.setLedgerpath("Assets/Current Assets/Sundry Debtors");
							lgrdr.setLedgerOpeningBalance(0);
							legerdtls.add(lgrdr);
						
					}
				}
				List<CompanySuppliers> suppliers= profileService.getSuppliers(clientid);
				if(isNotEmpty(suppliers)) {
					for(CompanySuppliers supplier : suppliers) {
							ProfileLedger lgrdr = new ProfileLedger();
							lgrdr.setClientid(clientid);
							if(isNotEmpty(supplier.getSupplierLedgerName())) {
								lgrdr.setLedgerName(supplier.getSupplierLedgerName());
							}else {
								lgrdr.setLedgerName(supplier.getName());
							}
							lgrdr.setGrpsubgrpName("Sundry Creditors");
							lgrdr.setLedgerpath("Laibilities/Current liabilies/Sundry Creditors");
							legerdtls.add(lgrdr);
					}
				}
				
				ledgerRepository.save(legerdtls);
				client.setIscommonledgerexist(true);
				clientService.saveClient(client);
			}
			String name = "Main Branch";
			String baddress = "";
			if(isNotEmpty(client.getAddress())) {
				baddress = client.getAddress();
			}
			List<Branch> branches =client.getBranches();
			boolean branchexists = false;
			for(int i=0;i<branches.size();i++) {
				String bname = branches.get(i).getName();
				if(name.equalsIgnoreCase(bname)) {
					branchexists =  true;
					break;
			   }else {
				   branchexists = false;
			   }
			}
			if(!branchexists) {
				Branch branch = new Branch();
				branch.setName("Main Branch");
				branch.setAddress(baddress);
				client.getBranches().add(branch);
				clientService.saveClient(client);
			}
			
		}*/
		if(isNotEmpty(client)) {
			String name = "Main Branch";
			String baddress = "";
			if(isNotEmpty(client.getAddress())) {
				baddress = client.getAddress();
			}
			List<Branch> branches =client.getBranches();
			boolean branchexists = false;
			for(int i=0;i<branches.size();i++) {
				String bname = branches.get(i).getName();
				if(name.equalsIgnoreCase(bname)) {
					branchexists =  true;
					break;
			   }else {
				   branchexists = false;
			   }
			}
			if(!branchexists) {
				Branch branch = new Branch();
				branch.setName("Main Branch");
				branch.setAddress(baddress);
				client.getBranches().add(branch);
				clientService.saveClient(client);
			}
		}
		accountUtils.initiliazeGroupsAndLedgers(clientid, id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
				List<String> clntids = companyUser.getCompany();
				if(isNotEmpty(clntids) && clntids.contains(clientid)){
					model.addAttribute("companyUser", companyUser);
				}else {
					ClientUserMapping clntusermapping = clientUserMappingRepository.findByClientidAndCreatedByIsNotNull(clientid);
					if(isEmpty(clntusermapping)) {
						ClientUserMapping clientuserMapping = clientUserMappingRepository.findByUseridAndClientid(user.getId().toString(), clientid);
						if(isNotEmpty(clientuserMapping) && isEmpty(clientuserMapping.getCreatedBy())) {
							clientuserMapping.setCreatedBy(user.getId().toString());
							clientUserMappingRepository.save(clientuserMapping);
						}
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
			ClientUserMapping clntusermapping = clientUserMappingRepository.findByClientidAndCreatedByIsNotNull(clientid);
			if(isEmpty(clntusermapping)) {
				ClientUserMapping clientuserMapping = clientUserMappingRepository.findByUseridAndClientid(user.getId().toString(), clientid);
				if(isNotEmpty(clientuserMapping) && isEmpty(clientuserMapping.getCreatedBy())) {
					clientuserMapping.setCreatedBy(user.getId().toString());
					clientUserMappingRepository.save(clientuserMapping);
				}
			}
		}
		
		if(isNotEmpty(client.getJournalEnteringDate())) {
			  SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
			    String journalDate = formatter.format(client.getJournalEnteringDate());  
			    model.addAttribute("journalDate", journalDate);
		}
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		model.addAttribute("client", client);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));

		logger.debug(CLASSNAME + method + END);
		return "profile/about";
	}
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}

	private List<GSTReturnSummary> getGSTReturnsSummary(final Client client) throws Exception {
		List<GSTReturnSummary> returnSummaryList = client.getReturnsSummary();
		if(isEmpty(returnSummaryList)) {
			List<String> returntypes = configService.getDealerACL(client.getDealertype());
			returntypes = returntypes.stream().filter(String -> String.startsWith("GSTR")).collect(Collectors.toList());
			logger.debug(CLASSNAME + " getGSTReturnsSummary:: returntypes\t" + returntypes.toString());
			returnSummaryList = clientService.getGSTReturnsSummary(returntypes, null);
		}
		return returnSummaryList;
	}

	@RequestMapping(value = "/updateclient/{userid}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String updateClient(@ModelAttribute("client") Client client, @PathVariable("userid") String userid,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model)
			throws Exception {
		final String method = "updateClient::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + userid);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, userid, fullname, usertype, month, year);

		Client dbClient = clientService.findById(client.getId().toString());
		dbClient.setContactperson(client.getContactperson());
		dbClient.setBusinessname(client.getBusinessname());
		dbClient.setEmail(client.getEmail());
		dbClient.setPincode(client.getPincode());
		dbClient.setMobilenumber(client.getMobilenumber());
		dbClient.setGstnnumber(client.getGstnnumber());
		dbClient.setGstname(client.getGstname());
		dbClient.setAddress(client.getAddress());
		dbClient.setPortalusername(client.getPortalusername());
		dbClient.setPortalpassword(client.getPortalpassword());
		dbClient.setStatename(client.getStatename());
		dbClient.setSignatoryName(client.getSignatoryName());
		dbClient.setSignatoryPAN(client.getSignatoryPAN());
		dbClient.setPannumber(client.getPannumber());
		dbClient.setDealertype(client.getDealertype());
		dbClient.setGroupName(client.getGroupName());
		dbClient.setBusinessEmail(client.getBusinessEmail());
		dbClient.setBusinessMobilenumber(client.getBusinessMobilenumber());
		dbClient.setCinNumber(client.getCinNumber());
		dbClient.setLutNumber(client.getLutNumber().toUpperCase());
		dbClient.setLutStartDate(client.getLutStartDate());
		dbClient.setLutExpiryDate(client.getLutExpiryDate());
		dbClient.setMsmeNo(client.getMsmeNo());
		dbClient.setJournalEnteringDate(client.getJournalEnteringDate());
		if(isEmpty(client.getGstnnumber())) {
			dbClient.setClienttype("UnRegistered");
		}else {
			dbClient.setClienttype("");
		}
		String delearType =  client.getDealertype();
		if(delearType.equals(DEALER_COMPOUND)){
			dbClient.setFilingOption(FILING_OPTION_QUARTERLY);
		}else if(NullUtil.isNotEmpty(client.getFilingOption())){
			dbClient.setFilingOption(client.getFilingOption());
		}else{
			dbClient.setFilingOption(FILING_OPTION_MONTHLY);
		}
		clientService.createClient(dbClient, userid, month, year);

		logger.debug(CLASSNAME + method + END);
		return "redirect:/about/" + userid + "/" + fullname + "/" + usertype + "/" + client.getId() + "/" + month + "/" + year;
	}

	@RequestMapping(value = "/addclientinfo/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String addClientInfo(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@RequestParam("type") String type, @RequestParam("name") String name,
			@RequestParam("address") String address, @RequestParam("parentId") String parentId, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model)
			throws Exception {
		final String method = "addClientInfo::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		if (NullUtil.isNotEmpty(type) && type.equals("Branch")) {
			Client client = clientService.addBranchInfo(clientid, name, address, parentId);
			model.addAttribute("client", client);
			model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		} else {
			Client client = clientService.addVerticalInfo(clientid, name, address, parentId);
			model.addAttribute("client", client);
			model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		}

		logger.debug(CLASSNAME + method + END);
		return "profile/about";
	}

	@RequestMapping(value = "/updateclientinfo/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String updateClientInfo(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@RequestParam("type") String type, @RequestParam("elemId") String elemId, @RequestParam("name") String name,
			@RequestParam("address") String address, @RequestParam("parentId") String parentId, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model)
			throws Exception {
		final String method = "updateClientInfo::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		if (NullUtil.isNotEmpty(type) && type.equals("Branch")) {
			Client client = clientService.updateBranchInfo(clientid, elemId, name, address, parentId);
			model.addAttribute("client", client);
			model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		} else {
			Client client = clientService.updateVerticalInfo(clientid, elemId, name, address, parentId);
			model.addAttribute("client", client);
			model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		}
		logger.debug(CLASSNAME + method + END);
		return "profile/about";
	}

	@RequestMapping(value = "/cp_customers/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String profileCustomer(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "profileCustomer::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		Client client = clientService.findById(clientid);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		List<CompanyCustomers> customers= profileService.getCustomers(clientid);
		if(isNotEmpty(customers)) {
			for(CompanyCustomers customer : customers) {
				customer.setUserid(customer.getId().toString());
				if(isNotEmpty(customer.getAddress())) {
					String custaddress = customer.getAddress();
					if(custaddress.contains("\n")) {
						custaddress = custaddress.replace("\n", "");
					}
					customer.setAddress(custaddress);
				}
				
				if(isNotEmpty(customer.getCustomerterms())) {
					String custterms = customer.getCustomerterms();
					if(custterms.contains("\r\n")) {
						custterms = custterms.replaceAll("\r\n", "#mgst# ");
					}
					if(custterms.contains("\n")) {
						custterms = custterms.replaceAll("\n", "#mgst# ");
					}
					customer.setCustomerterms(custterms);
				}
			}
		}
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		model.addAttribute("customers", customers);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		logger.debug(CLASSNAME + method + END);
		return "profile/customer";
	}
	
	 @RequestMapping(value = "/getcustomers/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String profileCustomer1(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,HttpServletRequest request) throws Exception {
		final String method = "profileCustomer::";
		logger.debug(CLASSNAME + method + BEGIN);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		Pageable pageable = null;
		Page<? extends CompanyCustomers> customers = profileService.getCustomersDetails(pageable,clientid,month, year, start, length,searchVal);
		if(isNotEmpty(customers)) {
			for(CompanyCustomers customer : customers) {
				customer.setUserid(customer.getId().toString());
			}
		} 
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;

			writer=mapper.writer();
	
		return writer.writeValueAsString(customers);
		
	} 
	 
	 @RequestMapping(value = "/getCustomer/{custid}", method = RequestMethod.GET)
		public @ResponseBody String getCustomer(@PathVariable("custid") String custid, ModelMap model) throws Exception {
			final String method = "getCustomer::";
			logger.debug(CLASSNAME + method + BEGIN);
			CompanyCustomers customers = companyCustomersRepository.findOne(custid);
			if(customers != null){
				customers.setUserid(customers.getId().toString());
			}
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer=null;

				writer=mapper.writer();
			
			return writer.writeValueAsString(customers);
		}
	 
	 @RequestMapping(value = "/getSuppliers/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
		public @ResponseBody String profileSupplier1(@PathVariable("id") String id, @PathVariable("name") String fullname, 
				@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
				@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,HttpServletRequest request) throws Exception {
			final String method = "profileSupplier1::";
			logger.debug(CLASSNAME + method + BEGIN);
			String st = request.getParameter("start");
			int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
			String len = request.getParameter("length");
			int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
			String searchVal = request.getParameter("search[value]");
			if(searchVal == null || "".equals(searchVal.trim())){
				searchVal = null;
			}
			Pageable pageable = null;
			Page<? extends CompanySuppliers> suppliers = profileService.getSupplierDetails(pageable,clientid,month, year, start, length,searchVal);
			if(isNotEmpty(suppliers)) {
				for(CompanySuppliers supplier : suppliers) {
					supplier.setUserid(supplier.getId().toString());
				}
			} 
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(MapperFeature.USE_ANNOTATIONS);
			ObjectWriter writer=mapper.writer();
			return writer.writeValueAsString(suppliers);
		} 
	 
	 @RequestMapping(value = "/getSupplier/{supid}", method = RequestMethod.GET)
		public @ResponseBody String getSupplier(@PathVariable("supid") String supid, ModelMap model) throws Exception {
			final String method = "geSupplier::";
			logger.debug(CLASSNAME + method + BEGIN);
			CompanySuppliers suppliers = companySuppliersRepository.findOne(supid);
			if(suppliers != null){
				suppliers.setUserid(suppliers.getId().toString());
			}
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer=mapper.writer();
			writer=mapper.writer();
			return writer.writeValueAsString(suppliers);
		}
	 
	 @RequestMapping(value = "/getItems/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
		public @ResponseBody String profileItems(@PathVariable("id") String id, @PathVariable("name") String fullname, 
				@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
				@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,HttpServletRequest request) throws Exception {
			final String method = "profileItems::";
			logger.debug(CLASSNAME + method + BEGIN);
			String type = request.getParameter("reportType");
			String st = request.getParameter("start");
			int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
			String len = request.getParameter("length");
			int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
			String searchVal = request.getParameter("search[value]");
			if(searchVal == null || "".equals(searchVal.trim())){
				searchVal = null;
			}
			Pageable pageable = null;
			Page<? extends CompanyItems> items = profileService.getItemDetails(pageable,clientid, type, month, year, start, length,searchVal);
			if(isNotEmpty(items)) {
				for(CompanyItems item : items) {
					item.setUserid(item.getId().toString());
				}
			} 
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(MapperFeature.USE_ANNOTATIONS);
			ObjectWriter writer=mapper.writer();
			return writer.writeValueAsString(items);
		} 
	 
	 @RequestMapping(value = "/getItem/{itemid}", method = RequestMethod.GET)
		public @ResponseBody String getItem(@PathVariable("itemid") String itemid, ModelMap model) throws Exception {
			final String method = "getItem::";
			logger.debug(CLASSNAME + method + BEGIN);
			CompanyItems items = companyItemsRepository.findOne(itemid);
			if(items != null){
				items.setUserid(items.getId().toString());
			}
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer=mapper.writer();
			writer=mapper.writer();
			return writer.writeValueAsString(items);
		}
	 
	@RequestMapping(value = "/cp_users/{id}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody List<CompanyUser> getUserProfile(ModelMap model, @PathVariable("id") String id,@PathVariable("clientid") String clientid) {
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
				List<String> clntids = companyUser.getCompany();
				if(isNotEmpty(clntids) && clntids.size()>0){
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
		return profileService.getUsers(id, clientid);
	}
	
	@RequestMapping(value = "/cp_createcustomer/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addCustomer(@ModelAttribute("product") CompanyCustomers customer, 
			@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addCustomer::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if(NullUtil.isNotEmpty(customer.getId()) 
				&& new ObjectId(id).equals(customer.getId())) {
			customer.setId(null);
			String ledgerName = "";
			if(isNotEmpty(customer.getCustomerLedgerName())) {
				ledgerName = customer.getCustomerLedgerName();
			}else {
				ledgerName = customer.getName();
			}
			
			ProfileLedger ldgr = ledgerRepository.findByClientidAndLedgerNameIgnoreCase(customer.getClientid(),ledgerName);
			if(isEmpty(ldgr)) {
				ProfileLedger lgrdr = new ProfileLedger();
				lgrdr.setClientid(customer.getClientid());
				if(isNotEmpty(customer.getCustomerLedgerName())) {
					lgrdr.setLedgerName(customer.getCustomerLedgerName());
				}else {
					lgrdr.setLedgerName(customer.getName());
				}
				lgrdr.setGrpsubgrpName("Sundry Debtors");
				lgrdr.setLedgerpath("Assets/Current Assets/Sundry Debtors");
				lgrdr.setLedgerOpeningBalance(0);
				ledgerRepository.save(lgrdr);
			}
			
		}else {
			if(isEmpty(customer.getCustomerLedgerName())) {
				customer.setCustomerLedgerName(customer.getName());
			}
		}
		profileService.saveCustomer(customer);
		logger.debug(CLASSNAME + method + END);
		return "redirect:/cp_customers/"+id+"/"+fullname+"/"+usertype+"/"+customer.getClientid() + "/" + month + "/" + year;
	}
	
	@RequestMapping(value = "/cp_centers/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String profileCenter(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "profileCenter::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			model.addAttribute("companyUser", companyUser);
			if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
				model.addAttribute("addClient", companyUser.getAddclient());
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		SubscriptionDetails subscriptionDetails = subscriptionService.getUserSubscriptionDetails(id);
		if(isNotEmpty(subscriptionDetails)){
			model.addAttribute("allowedCenters", subscriptionDetails.getAllowedCenters());
		}
		List<CompanyCenters> centers = profileService.getCenters(id);
		if (isNotEmpty(centers)) {
			for (CompanyCenters center : centers) {
				center.setPassword(new String(Base64.getDecoder().decode(center.getPassword()),
						MasterGSTConstants.PASSWORD_ENCODE_FORMAT));
			}
		}
		model.addAttribute("centers", centers);
		model.addAttribute("useremail", user.getEmail());
		logger.debug(CLASSNAME + method + END);
		return "profile/centers";
	}
	
	@RequestMapping(value = "/cp_createcenter/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addCenter(@ModelAttribute("center") CompanyCenters center, 
			@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addCenter::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			model.addAttribute("companyUser", companyUser);
			if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
				model.addAttribute("addClient", companyUser.getAddclient());
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		if(isNotEmpty(center.getId()) 
				&& new ObjectId(id).equals(center.getId())) {
			center.setId(null);
		}
		SubscriptionDetails subscriptionDetails = subscriptionService.getUserSubscriptionDetails(id);
		if(isNotEmpty(subscriptionDetails)){
			model.addAttribute("allowedCenters", subscriptionDetails.getAllowedCenters());
		}
		List<CompanyCenters> centers = profileService.getCenters(id);
		if (isEmpty(center.getId()) && isNotEmpty(centers)) {
			if(!subscriptionService.allowAddCenters(id, centers.size())) {
				model.addAttribute("error", "Your subscription has expired. Kindly subscribe to proceed further!");
			} else {
				center = profileService.saveCenter(center, null);
				centers.add(center);
			}
		} else {
			//centers = Lists.newArrayList();
			center = profileService.saveCenter(center, null);
			//centers.add(center);
			centers = profileService.getCenters(id);
		}
		updateModel(model, id, fullname, usertype, month, year);
		if (isNotEmpty(centers)) {
			for (CompanyCenters compCenter : centers) {
				compCenter.setPassword(new String(Base64.getDecoder().decode(compCenter.getPassword()),
						MasterGSTConstants.PASSWORD_ENCODE_FORMAT));
			}
		}
		model.addAttribute("centers", centers);
		model.addAttribute("useremail", user.getEmail());
		logger.debug(CLASSNAME + method + END);
		return "profile/centers";
	}
	
	@RequestMapping(value = "/cp_allcenters/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String profileAllCenter(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "profileCenter::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			model.addAttribute("companyUser", companyUser);
			if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
				model.addAttribute("addClient", companyUser.getAddclient());
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		List<CompanyCenters> centers = profileService.getCenters(id);
		if (isNotEmpty(centers)) {
			for (CompanyCenters center : centers) {
				center.setPassword(new String(Base64.getDecoder().decode(center.getPassword()),
						MasterGSTConstants.PASSWORD_ENCODE_FORMAT));
				
				User usr = userService.findByEmail(center.getEmail());
				if(isNotEmpty(usr)){
					SubscriptionDetails subscriptionDetails = subscriptionService.getSubscriptionData(usr.getId().toString());
					if(NullUtil.isNotEmpty(subscriptionDetails)) {
						if(NullUtil.isNotEmpty(subscriptionDetails.getAllowedClients())) {
							center.setTotalClients(subscriptionDetails.getAllowedClients()+"");
						}
						if(NullUtil.isNotEmpty(subscriptionDetails.getAllowedInvoices())) {
							center.setTotalInvoices(subscriptionDetails.getAllowedInvoices()+"");
						}
						if(NullUtil.isNotEmpty(subscriptionDetails.getProcessedInvoices())) {
							center.setUsedInvoices(subscriptionDetails.getProcessedInvoices()+"");
						}
					}
					List<Client> lClient = clientService.findByUserid(usr.getId().toString());
					if(lClient != null){
						center.setUsedClients(Integer.toString(lClient.size()));
					}
				}
			}
		}
		model.addAttribute("centers", centers);
		logger.debug(CLASSNAME + method + END);
		return "profile/allcenters";
	}
	
	/*@RequestMapping(value = "/cp_centersfilings/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String profileCenterFilings(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "profileCenterfilings::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		List<CompanyCenters> centers = profileService.getCenters(id);
		if (isNotEmpty(centers)) {
			for (CompanyCenters center : centers) {
				center.setPassword(new String(Base64.getDecoder().decode(center.getPassword()),
						MasterGSTConstants.PASSWORD_ENCODE_FORMAT));
			}
		}
		model.addAttribute("centers", centers);
		logger.debug(CLASSNAME + method + END);
		return "profile/suvidhacenterfiling";
	}*/
	
	@RequestMapping(value = "/cp_centersclients/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String profileCenterBillings(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "profileCenterClients::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user)){
			model.addAttribute("email", user.getEmail());
		}
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			model.addAttribute("companyUser", companyUser);
			if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
				model.addAttribute("addClient", companyUser.getAddclient());
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		List<Client> lClient = clientService.findByUserid(id);
		if (isNotEmpty(lClient)) {
			model.addAttribute("centers", lClient);
		}
		logger.debug(CLASSNAME + method + END);
		return "profile/suvidhacenterclient";
	}
	
	@RequestMapping(value = "/updateSelectedClients/{id}", method = RequestMethod.POST)
	public @ResponseBody String delSelectedClients(@PathVariable("id") String id,@RequestBody UpdateClients updateclients, ModelMap model,HttpServletRequest request)
					throws Exception {
		if(NullUtil.isNotEmpty(updateclients)) {
			List<String> clientids =updateclients.getClientids();
			for(String clientid:clientids) {
				Client client = clientService.findById(clientid.trim());
				if(NullUtil.isNotEmpty(updateclients.getSalescutOffdate())) {
					client.setCutOffDateForSales(updateclients.getSalescutOffdate());
				}
				if(NullUtil.isNotEmpty(updateclients.getPurchasecutOffdate())) {
					client.setCutOffDateForPurchases(updateclients.getPurchasecutOffdate());
				}
				clientService.saveClient(client);
			}
		}
		return MasterGSTConstants.SUCCESS;
		
	}
	
	
	@RequestMapping(value = "/cp_smtp/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String profileSmtp(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "profileCenterClients::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user)){
			model.addAttribute("email", user.getEmail());
		}
		model.addAttribute("smtpDetails", smtpDetailsService.getAllSmtpDetails(id));
		logger.debug(CLASSNAME + method + END);
		return "profile/smtp";
	}
	
	@RequestMapping(value="saveSmtpDetails", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody MgstResponse saveSmtpDetails(@RequestBody SmtpDetails smtpDetails){
		logger.debug("Start");
		smtpDetailsService.saveSmtpDetails(smtpDetails);
		return new MgstResponse(MgstResponse.SUCCESS);
	}
	
	@RequestMapping(value="smtpdetails/{id}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody MgstResponse getSmtpDetails(@PathVariable("id") String id){
		logger.debug("Start");
		SmtpDetails smytpDetails = smtpDetailsService.getSmtpDetails(id);
		return new MgstResponse(smytpDetails, MgstResponse.SUCCESS);
	}
	
	@RequestMapping(value = "/dwnldallclientxls/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource downloadExcelData(@PathVariable("id") String id,
			@PathVariable("name") String name, @PathVariable("usertype") String usertype, 
			@PathVariable("month") int month, @PathVariable("year") int year, HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "inline; filename=AllClients.xls");
		List<Client> lClient = clientService.findByUserid(id);
		List<Client> clientList = Lists.newArrayList();
		for (Client client : lClient) {
			if (isNotEmpty(client)) {
				Client clnt = new Client();
					if (isNotEmpty(client.getGstnnumber())) {
						clnt.setGstnnumber(client.getGstnnumber());
					}
					if (isNotEmpty(client.getPannumber())) {
						clnt.setPannumber(client.getPannumber());
					}
					if (isNotEmpty(client.getSignatoryName())) {
						clnt.setSignatoryName(client.getSignatoryName());
					}
					if (isNotEmpty(client.getSignatoryPAN())) {
						clnt.setSignatoryPAN(client.getSignatoryPAN());
					}
					if (isNotEmpty(client.getEmail())) {
						clnt.setEmail(client.getEmail());
					}
					if (isNotEmpty(client.getMobilenumber())) {
						clnt.setMobilenumber(client.getMobilenumber());
					}
					if (isNotEmpty(client.getBusinessname())) {
						clnt.setBusinessname(client.getBusinessname());
					}
					if (isNotEmpty(client.getStatename())) {
						clnt.setStatename(client.getStatename());
					}
					if (isNotEmpty(client.getGstname())) {
						clnt.setGstname(client.getGstname());
					}
					if (isNotEmpty(client.getDealertype())) {
						clnt.setDealertype(client.getDealertype());
					}
					if (isNotEmpty(client.getTurnover())) {
						clnt.setTurnover(client.getTurnover());
					}
					if (isNotEmpty(client.getAddress())) {
						clnt.setAddress(client.getAddress());
					}
					clientList.add(clnt);
			}
		}
		logger.debug(clientList);
		File file = new File("AllClients.xls");
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers = Arrays.asList("GSTIN Number", "Pan Number", "Authorised Signatory Name", "Authorised Person PAN No", "EmailId",
					"Mobile Number", "Business Name", "State", "GSTIN UserName", "Dealer Type", "Aggregate Turnover of previous Year", "Address");
			SimpleExporter exporter = new SimpleExporter();
			exporter.gridExport(headers, clientList,
					"gstnnumber, pannumber, signatoryName, signatoryPAN, email, mobilenumber, businessname, statename, gstname, dealertype, turnover, address",
					fos);
			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "downloadExcelData : ERROR", e);
		}
		return new FileSystemResource(new File("AllClients.xls"));
	}
	
	@RequestMapping(value = "/cp_suppliers/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String profileSupplier(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "profileSupplier::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		List<CompanySuppliers> suppliers= profileService.getSuppliers(clientid);
		if(isNotEmpty(suppliers)) {
			for(CompanySuppliers supplier : suppliers) {
				if(isNotEmpty(supplier.getAddress())) {
					String suppaddress = supplier.getAddress();
					if(suppaddress.contains("\n")) {
						suppaddress = suppaddress.replace("\n", "");
					}
					supplier.setAddress(suppaddress);
				}
				
				if(isNotEmpty(supplier.getSupplierterms())) {
					String supterms = supplier.getSupplierterms();
					if(supterms.contains("\r\n")) {
						supterms = supterms.replaceAll("\r\n", "#mgst# ");
					}
					if(supterms.contains("\n")) {
						supterms = supterms.replaceAll("\n", "#mgst# ");
					}
					supplier.setSupplierterms(supterms);
				}
			}
		}
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		model.addAttribute("customers", suppliers);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		logger.debug(CLASSNAME + method + END);
		return "profile/suppliers";
	}
	
	@RequestMapping(value = "/cp_createsupplier/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addSupplier(@ModelAttribute("product") CompanySuppliers supplier, 
			@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addSupplier::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if(NullUtil.isNotEmpty(supplier.getId()) 
				&& new ObjectId(id).equals(supplier.getId())) {
			supplier.setId(null);
			String ledgerName = "";
			if(isNotEmpty(supplier.getSupplierLedgerName())) {
				ledgerName = supplier.getSupplierLedgerName();
			}else {
				ledgerName = supplier.getName();
			}
			
			ProfileLedger ldgr = ledgerRepository.findByClientidAndLedgerNameIgnoreCase(supplier.getClientid(),ledgerName);
			if(isEmpty(ldgr)) {
				ProfileLedger lgrdr = new ProfileLedger();
				lgrdr.setClientid(supplier.getClientid());
				if(isNotEmpty(supplier.getSupplierLedgerName())) {
					lgrdr.setLedgerName(supplier.getSupplierLedgerName());
				}else {
					lgrdr.setLedgerName(supplier.getName());
				}
				lgrdr.setGrpsubgrpName("Sundry Creditors");
				lgrdr.setLedgerpath("Liabilities/Current liabilities/Sundry Creditors");
				ledgerRepository.save(lgrdr);
			}
		}else {
			if(isEmpty(supplier.getSupplierLedgerName())) {
				supplier.setSupplierLedgerName(supplier.getName());
			}
		}
		profileService.saveSupplier(supplier);
		logger.debug(CLASSNAME + method + END);
		return "redirect:/cp_suppliers/"+id+"/"+fullname+"/"+usertype+"/"+supplier.getClientid() + "/" + month + "/" + year;
	}

	@RequestMapping(value = "/cp_upload/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String profileUpload(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("type") String type, ModelMap model)
			throws Exception {
		final String method = "profileUpload::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
			id=user.getParentid();
		}else {
			model.addAttribute("addClient", "true");
		}
		String finYear = (year - 1) + "-" + (year);
		if (month > 3) {
			finYear = year + "-" + (year + 1);
		}
		Client client = clientService.findById(clientid);
		String clintSignature="";
		if(isNotEmpty(client.getClientSignature())) {
			clintSignature = client.getClientSignature();
			if(clintSignature.contains("\r\n")) {
				clintSignature = clintSignature.replaceAll("\r\n", "#mgst# ");
			}else if(clintSignature.contains("\r")) {
				clintSignature = clintSignature.replaceAll("\r", "#mgst# ");
			}else if(clintSignature.contains("\n")) {
				clintSignature = clintSignature.replaceAll("\n", "#mgst# ");
			}
			model.addAttribute("clientSignature", clintSignature);
		}
		if(isNotEmpty(client.getNotes())) {
			String clntnotes = client.getNotes();
			if(clntnotes.contains("\r\n")) {
				clntnotes = clntnotes.replaceAll("\r\n", "#mgst# ");
			}else if(clntnotes.contains("\r")) {
				clntnotes = clntnotes.replaceAll("\r", "#mgst# ");
			}else if(clntnotes.contains("\n")) {
				clntnotes = clntnotes.replaceAll("\n", "#mgst# ");
			}
			client.setNotes(clntnotes);
		}
		if(isNotEmpty(client.getTerms())) {
			String clintterms = client.getTerms();
			if(clintterms.contains("\r\n")) {
				clintterms = clintterms.replaceAll("\r\n", "#mgst# ");
			}else if(clintterms.contains("\r")) {
				clintterms = clintterms.replaceAll("\r", "#mgst# ");
			}else if(clintterms.contains("\n")) {
				clintterms = clintterms.replaceAll("\n", "#mgst# ");
			}
			client.setTerms(clintterms);
		}
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		
		//List<CompanyInvoices> invoices = profileService.getInvoiceSubmissionDetails(id, clientid, finYear);
		List<CompanyInvoices> invoices = profileService.getUserInvoiceSubmissionDetails(clientid, finYear);
		model.addAttribute("invoices", invoices);
		String invType = null;
		if (isNotEmpty(invoices)) {
			for (CompanyInvoices invoiceSubmissionData : invoices) {
				if(isNotEmpty(invoiceSubmissionData.getInvoiceType())){
					invType = "No";
				}
			}
		}
		model.addAttribute("invType", invType);
		String[] pArray = {"Discount","Rate"};
		model.addAttribute("permissionArray", pArray);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		if(pconfig == null) {
			pconfig = new PrintConfiguration();
		}
		model.addAttribute("pconfig", pconfig);
		OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
		if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableSalesFields())) {
			otherconfig.setEnableSalesFields(false);
		}
		if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnablePurFields())) {
			otherconfig.setEnablePurFields(false);
		}
		if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableLedgerSalesField())) {
			otherconfig.setEnableLedgerSalesField(true);
		}
		if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableLedgerPurField())) {
			otherconfig.setEnableLedgerPurField(true);
		}
		if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableroundoffSalesField())) {
			otherconfig.setEnableroundoffSalesField(false);
		}
		if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableroundoffPurField())) {
			otherconfig.setEnableroundoffPurField(false);
		}
		model.addAttribute("otherconfig", otherconfig);
		ClientConfig clientConfig = clientService.getClientConfig(clientid);
		if(isNotEmpty(clientConfig) && isNotEmpty(clientConfig.getReconcileDiff())) {
			model.addAttribute("reconsileAmt", clientConfig.getReconcileDiff());
		} else {
			model.addAttribute("reconsileAmt", 0d);
		}
		model.addAttribute("clientConfig", clientConfig);
		
		EwayBillConfigurations ewaybillconfig = clientService.getEwayBillConfig(clientid);
		if(isNotEmpty(ewaybillconfig)) {
			model.addAttribute("ebillname", ewaybillconfig.getUserName());
			model.addAttribute("ebillpwd", new String(Base64.getDecoder().decode(ewaybillconfig.getPassword()),MasterGSTConstants.PASSWORD_ENCODE_FORMAT));
			model.addAttribute("ebillstatus", ewaybillconfig.getConnStaus());
		}
		EinvoiceConfigurations einvoiceConfig = einvoiceService.getEinvoiceConfig(clientid);
		if(isNotEmpty(einvoiceConfig)) {
			model.addAttribute("eInvUname", einvoiceConfig.getUserName());
			model.addAttribute("eInvPwd", new String(Base64.getDecoder().decode(einvoiceConfig.getPassword()),MasterGSTConstants.PASSWORD_ENCODE_FORMAT));
			model.addAttribute("eInvstatus", einvoiceConfig.getConnStaus());
		}
		
		model.addAttribute("invType", invType);
		model.addAttribute("type", type);
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		logger.debug(CLASSNAME + method + END);
		return "profile/upload";
	}
	
	@RequestMapping(value = "/invoicetype/{id}/{name}/{usertype}/{clientid}/{year}", method = RequestMethod.GET)
	public @ResponseBody List<CompanyInvoices> checkAddClientEligibility(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			 @PathVariable("year") String year,ModelMap model) throws Exception {
		final String method = "checkAddClientEligibility::";
		logger.debug(CLASSNAME + method + BEGIN);
		User usr = userService.findById(id);
		if(isNotEmpty(usr.getParentid())){
			id = usr.getParentid();
		}
		List<CompanyInvoices> invoiceSubmissionDatas = profileService.getUserInvoiceSubmissionDetails(clientid, year);
		String invType = null;
		String rettypeSales = null;
		String rettypePurchase = null;
		if (isNotEmpty(invoiceSubmissionDatas)) {
			if(isNotEmpty(invoiceSubmissionDatas)) {
				for(CompanyInvoices companyInvoice : invoiceSubmissionDatas) {
					companyInvoice.setUserid(companyInvoice.getId().toString());
				}
			} 
			if(invoiceSubmissionDatas.size() <= 1){
				invType = "No";
				for (CompanyInvoices invoiceSubmissionData : invoiceSubmissionDatas) {
					if(isNotEmpty(invoiceSubmissionData.getInvoiceType()) && isNotEmpty(invoiceSubmissionData.getReturnType())){
						if("ALL".equals(invoiceSubmissionData.getInvoiceType())) {
							if("GSTR1".equals(invoiceSubmissionData.getReturnType())){
								rettypeSales = "Sales";
							}else if("Purchase Register".equals(invoiceSubmissionData.getReturnType())){
								rettypePurchase = "Purchase";
							}
						}
					}
				}
			}else{
			for (CompanyInvoices invoiceSubmissionData : invoiceSubmissionDatas) {
				if(isNotEmpty(invoiceSubmissionData.getInvoiceType()) && isNotEmpty(invoiceSubmissionData.getReturnType())){
					
					if("ALL".equals(invoiceSubmissionData.getInvoiceType()) && (NullUtil.isEmpty(invType) || "Yes".equals(invType))) {	
						if(("ALL".equals(invoiceSubmissionData.getInvoiceType())) && ("Purchase Register".equals(invoiceSubmissionData.getReturnType()) || "GSTR1".equals(invoiceSubmissionData.getReturnType()))){
							invType = "Yes";
							if("GSTR1".equals(invoiceSubmissionData.getReturnType())){
								rettypeSales = "Sales";
							}else if("Purchase Register".equals(invoiceSubmissionData.getReturnType())){
								rettypePurchase = "Purchase";
							}
						}else {
							invType = "No";
						}
					}else{
						invType = "No";
					}
				}
			}
			}
		}
		model.addAttribute("invType", invType);
		model.addAttribute("rettypeSales", rettypeSales);
		model.addAttribute("rettypePurchase", rettypePurchase);
		return invoiceSubmissionDatas;
	}

	@RequestMapping(value = "/saveinvsubmssion/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.POST)
	public String saveInvoiceSubmission(@ModelAttribute("invoice") CompanyInvoices invoice,
			@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) {
		final String method = "saveInvoiceSubmission::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		Client client = clientService.findById(clientid);
		// client.setGstsubmiton(submiton);
		// client = clientService.createClient(client);
		model.addAttribute("client", client);
		if (NullUtil.isNotEmpty(invoice.getId()) && new ObjectId(id).equals(invoice.getId())) {
			invoice.setId(null);
		}
		if (NullUtil.isEmpty(invoice.getSubmittedDate())) {
			invoice.setSubmittedDate(new Date());
		}
		if(NullUtil.isEmpty(invoice.getInvoiceType())) {
			String invoiceType="ALL";
			invoice.setInvoiceType(invoiceType);
		}else {
			if(invoice.getInvoiceType().equalsIgnoreCase("Receipts")) {
				invoice.setReturnType("GSTR1");
			}else if(invoice.getInvoiceType().equalsIgnoreCase("Payments")) {
				invoice.setReturnType("GSTR2");
			}else if(invoice.getInvoiceType().equalsIgnoreCase("Vouchers")) {
				invoice.setReturnType("Vouchers");
			}else if(invoice.getInvoiceType().equalsIgnoreCase("Contra")){
				invoice.setReturnType("Contra");
			}
		}
		if(NullUtil.isNotEmpty(invoice.getPrefix())) {
			invoice.setPrefix(invoice.getPrefix().toUpperCase());
		}
		profileService.saveInvoiceSubmission(invoice);
		logger.debug(CLASSNAME + method + END);
		return "redirect:/cp_upload/" + id + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year+ "?type=invoiceconfig";
	}

	@RequestMapping(value = "/cp_products/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String profileProducts(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model)
			throws Exception {
		final String method = "profileProducts::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		model.addAttribute("products", profileService.getProducts(clientid));
		logger.debug(CLASSNAME + method + END);
		return "profile/products";
	}

	@RequestMapping(value = "/cp_createproduct/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addProduct(@ModelAttribute("product") CompanyProducts product, @PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model)
			throws Exception {
		final String method = "addProduct::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if (NullUtil.isNotEmpty(product.getId()) && new ObjectId(id).equals(product.getId())) {
			product.setId(null);
		}
		profileService.saveProduct(product);
		logger.debug(CLASSNAME + method + END);
		return "redirect:/cp_products/" + id + "/" + fullname + "/" + usertype + "/" + product.getClientid() + "/" + month + "/" + year;
	}

	@RequestMapping(value = "/cp_services/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String profileServices(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model)
			throws Exception {
		final String method = "profileServices::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		model.addAttribute("services", profileService.getServices(clientid));
		logger.debug(CLASSNAME + method + END);
		return "profile/services";
	}

	@RequestMapping(value = "/cp_createservice/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addService(@ModelAttribute("service") CompanyServices service, @PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) {
		final String method = "addService::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if (NullUtil.isNotEmpty(service.getId()) && new ObjectId(id).equals(service.getId())) {
			service.setId(null);
		}
		profileService.saveService(service);
		logger.debug(CLASSNAME + method + END);
		return "redirect:/cp_services/" + id + "/" + fullname + "/" + usertype + "/" + service.getClientid() + "/" + month + "/" + year;
	}

	@RequestMapping(value = "/cp_items/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String profileItems(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "profileItems::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		model.addAttribute("items", profileService.getItems(clientid));
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		logger.debug(CLASSNAME + method + END);
		return "profile/items";
	}

	@RequestMapping(value = "/cp_createitem/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addItem(@ModelAttribute("item") CompanyItems item, @PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addItem::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if (NullUtil.isNotEmpty(item.getId()) && new ObjectId(id).equals(item.getId())) {
			item.setId(null);
		}
		if(item.getExmepted() == null) {
			item.setExemptedType("");
		}
		profileService.saveItem(item);
		//if(isEmpty(item.getId())) {
			if(isNotEmpty(item.getItemType()) && item.getItemType().equals("Product")){
			profileService.saveDefaultStocks(id, item);
			}
		//}
		logger.debug(CLASSNAME + method + END);
		return "redirect:/cp_items/" + id + "/" + fullname + "/" + usertype + "/" + item.getClientid() + "/" + month + "/" + year;
	}
	
	@RequestMapping(value = "/cp_createadditem/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody String addpopItem(@ModelAttribute("item") CompanyItems item, @PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addItem::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if (NullUtil.isNotEmpty(item.getId()) && new ObjectId(id).equals(item.getId())) {
			item.setId(null);
		}
		if(item.getExmepted() == null) {
			item.setExemptedType("");
		}
		CompanyItems copanyitem =profileService.saveItem(item);
		//if(isEmpty(item.getId())) {
			profileService.saveDefaultStocks(id, item);
		//}
		logger.debug(CLASSNAME + method + END);
		if(isNotEmpty(copanyitem)) {
			return copanyitem.getId().toString();
		}
		return "";
	}

	@RequestMapping(value = "/cp_user/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String companyUsers(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "companyUsers::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User cuser = userService.findById(id);
		CompanyUser mainUser = null;
		model.addAttribute("cemail", cuser.getEmail());
		ClientUserMapping clntusermapping = clientUserMappingRepository.findByClientidAndCreatedByIsNotNull(clientid);
		if(NullUtil.isNotEmpty(cuser) && NullUtil.isNotEmpty(cuser.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(cuser.getEmail(), clientid);
			
			if(isNotEmpty(clntusermapping)) {
				model.addAttribute("adminroles", profileService.getRoles(clntusermapping.getCreatedBy(),clientid));
			}else {
				model.addAttribute("adminroles", profileService.getRoles(cuser.getParentid(),null));
			}
			if(isNotEmpty(companyUser)) {
				List<String> clntids = companyUser.getCompany();
				if(isNotEmpty(clntids) && clntids.size() > 0){
					if(clntids.contains(clientid)){
						model.addAttribute("companyUser", companyUser);
					}
				}
				
				if(isNotEmpty(clntusermapping) && isNotEmpty(clntusermapping.getUserid())) {
					mainUser = new CompanyUser();
					User muser = userService.findById(clntusermapping.getUserid());
					if(isNotEmpty(muser)) {
						if(isNotEmpty(muser.getFullname())) {
							mainUser.setName(muser.getFullname()+"(Admin)");
						}
						if(isNotEmpty(muser.getMobilenumber())) {
							mainUser.setMobile(muser.getMobilenumber());
						}
						if(isNotEmpty(muser.getEmail())) {
							mainUser.setEmail(muser.getEmail());;
						}
						mainUser.setDisable("false");
						mainUser.setAddsubuser("main");
						mainUser.setIsglobal("true");
					}
				}
				ClientUserMapping clntusermappings = clientUserMappingRepository.findByUseridAndClientid(cuser.getId().toString(), clientid);
				if(isNotEmpty(clntusermappings)) {
					if(isEmpty(clntusermappings.getAddSubUser()) || "false".equals(clntusermappings.getAddSubUser())) {
						model.addAttribute("addsubuser", "false");
					}else {
						model.addAttribute("addsubuser", "true");
					}
				}
				if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
					model.addAttribute("addClient", companyUser.getAddclient());
				}else {
					model.addAttribute("addClient", "true");
				}
			}else {
				if(isNotEmpty(clntusermapping) && isNotEmpty(clntusermapping.getUserid())) {
					mainUser = new CompanyUser();
					User muser = userService.findById(clntusermapping.getUserid());
					if(isNotEmpty(muser)) {
						if(isNotEmpty(muser.getFullname())) {
							mainUser.setName(muser.getFullname()+"(Admin)");
						}
						if(isNotEmpty(muser.getMobilenumber())) {
							mainUser.setMobile(muser.getMobilenumber());
						}
						if(isNotEmpty(muser.getEmail())) {
							mainUser.setEmail(muser.getEmail());;
						}
						mainUser.setDisable("false");
						mainUser.setAddsubuser("main");
						mainUser.setIsglobal("true");
					}
				}
				model.addAttribute("addClient", "true");
			}
		}else {
			
			 mainUser = new CompanyUser(); 
			 if(isNotEmpty(cuser.getFullname())) {
				 mainUser.setName(cuser.getFullname()+"(Admin)"); 
			 }
			 if(isNotEmpty(cuser.getMobilenumber())) {
				 mainUser.setMobile(cuser.getMobilenumber());
			 }
			 if(isNotEmpty(cuser.getEmail())) {
				 mainUser.setEmail(cuser.getEmail());
			 }
			 mainUser.setDisable("false"); 
			 mainUser.setAddsubuser("main");
			 mainUser.setIsglobal("true");
			model.addAttribute("adminroles", profileService.getRoles(id,null));
			model.addAttribute("addClient", "true");
		}
		List<Client> clients = Lists.newArrayList();
		if (NullUtil.isNotEmpty(clientid)) {
			Client client = clientService.findById(clientid);
			model.addAttribute("client", client);
			model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
			clients.add(client);
		}
		model.addAttribute("clients", clients);
		//List<CompanyUser> users = profileService.getUsers(id, clientid);
		List<CompanyUser> users = profileService.getUsersByClientId(clientid);
		if(isNotEmpty(mainUser)) {
			users.add(mainUser);
		}
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-YYY hh:mm:ss a");
		if (isNotEmpty(users)) {
			for (CompanyUser user : users) {
				if(isNotEmpty(user.getEmail())) {
					User usr = userRepository.findByEmail(user.getEmail());
					if(isNotEmpty(usr)) {
						ClientUserMapping clntusermappings = clientUserMappingRepository.findByUseridAndClientid(usr.getId().toString(), clientid);
						if(isNotEmpty(clntusermappings)) {
							if(isNotEmpty(clntusermappings.getAddSubUser()) && !"main".equalsIgnoreCase(user.getAddsubuser())) {
								user.setAddsubuser(clntusermappings.getAddSubUser());
							}
							if(isNotEmpty(clntusermappings.getRole())) {
								user.setRole(clntusermappings.getRole());
							}else {
								user.setRole("");
							}
						}
						if(isNotEmpty(usr) && isNotEmpty(usr.getLastLoggedIn())) {
							user.setUsrLastloggedin(sdf.format(usr.getLastLoggedIn()));
						}else {
							user.setUsrLastloggedin("");
						}
					}else {
						user.setUsrLastloggedin("");
					}
				}
				if(isNotEmpty(user.getPassword())) {
				user.setPassword(new String(Base64.getDecoder().decode(user.getPassword()),
						MasterGSTConstants.PASSWORD_ENCODE_FORMAT));
				}
			}
		}
		model.addAttribute("users", users);
		model.addAttribute("features", configService.getFeatures());
		if(isNotEmpty(clntusermapping)) {
			model.addAttribute("roles", profileService.getRoles(clntusermapping.getCreatedBy(),clientid));
		}else {
			model.addAttribute("roles", profileService.getRoles(id, clientid));
		}
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		logger.debug(CLASSNAME + method + END);
		return "profile/users";
	}

	@RequestMapping(value = "/cp_customer/{id}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody List<CompanyCustomers> getCompanyCustomers(ModelMap model, @PathVariable("id") String id,@PathVariable("clientid") String clientid) {
		return profileService.getCustomers(id, clientid);
	}
	
	@RequestMapping(value = "/cp_supplier/{id}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody List<CompanySuppliers> getCompanySuppliers(ModelMap model, @PathVariable("id") String id,@PathVariable("clientid") String clientid) {
		return profileService.getSuppliers(id,clientid);
	}
	
	@RequestMapping(value = "/teamuser/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String teamUsers(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "teamUsers::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User cuser = userService.findById(id);
		if(NullUtil.isNotEmpty(cuser) && NullUtil.isNotEmpty(cuser.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(cuser.getEmail());
			model.addAttribute("companyUser", companyUser);
		}
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-YYY hh:mm:ss a");
		List<CompanyUser> users = profileService.getGlobalUsers(id);
		if (isNotEmpty(users)) {
			for (CompanyUser user : users) {
				user.setPassword(new String(Base64.getDecoder().decode(user.getPassword()),	MasterGSTConstants.PASSWORD_ENCODE_FORMAT));
				if(isNotEmpty(user.getEmail())) {
					User usr = userService.findByEmail(user.getEmail());
					if(isNotEmpty(usr) && isNotEmpty(usr.getLastLoggedIn())) {
						user.setUsrLastloggedin(sdf.format(usr.getLastLoggedIn()));
					}
				}else {
					user.setUsrLastloggedin("");
				}
				if(isEmpty(user.getAddclient())) {
					user.setAddclient("true");
				}
			}
		}
		model.addAttribute("users", users);
		model.addAttribute("features", configService.getFeatures());
		model.addAttribute("roles", profileService.getRoles(id, null));
		model.addAttribute("clients", clientService.findByUserCreatedClients(id));
		if(MasterGSTConstants.SUVIDHA_CENTERS.equals(usertype)) {
			model.addAttribute("centers", profileService.getCenters(id));
		}
		logger.debug(CLASSNAME + method + END);
		return "profile/users";
	}

	@RequestMapping(value = "/createprofileuser/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addUser(@ModelAttribute("user") CompanyUser user, @PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addUser::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if (NullUtil.isNotEmpty(user.getId()) && new ObjectId(id).equals(user.getId())) {
			user.setId(null);
		}
		if (NullUtil.isEmpty(user.getClientid())) {
			user.setIsglobal("true");
		} else {
			if(isEmpty(user.getCompany())) {
				user.setCompany(Lists.newArrayList());
				user.getCompany().add(user.getClientid());
			}	
		}
		if(NullUtil.isEmpty(user.getDisable())){
			user.setDisable("true");
		}else {
			user.setDisable("false");
		}
		
		if(NullUtil.isEmpty(user.getAddclient())){
			user.setAddclient("false");
		}
		try {
			CompanyUser pUser = profileService.getCompanyUser(user.getEmail());
			User usr = userService.findByEmail(user.getEmail());
			if(user.getPassword() != null){
				user.setPassword(Base64.getEncoder()
					.encodeToString(user.getPassword().getBytes(MasterGSTConstants.PASSWORD_ENCODE_FORMAT)));
			}else{
				if(isNotEmpty(usr)){
					if(isNotEmpty(usr.getPassword())){
						user.setPassword(usr.getPassword());
					}
					if(isNotEmpty(usr.getMobilenumber())){
						user.setMobile(usr.getMobilenumber());
					}
				}
			}
			if(isEmpty(pUser) || isEmpty(pUser.getId())) {
				profileService.saveUser(user, user.getClientid());
			} else {
				/*List<String> cuser1 = user.getCompany();
				if(NullUtil.isNotEmpty(pUser)){
					if(isNotEmpty(pUser.getCompany())){
						for(String cId : pUser.getCompany()){
							if(isNotEmpty(cuser1)){
								if(!cuser1.contains(cId)){
									ClientUserMapping clientUserMapping = clientUserMappingRepository.findByUseridAndClientid(pUser.getUserid(), cId);
									clientUserMappingRepository.delete(clientUserMapping.getId().toString());
								}
							}
						}
					}
				}*/
				
				List<String> clntIds = pUser.getCompany();
				if(isNotEmpty(clntIds)) {
					for(String cId : pUser.getCompany()) {
						clientUserMappingRepository.deleteByUseridAndClientid(usr.getId().toString(), cId);
					}
				}
				
				if(isEmpty(user.getId())) {
					if(isNotEmpty(pUser.getCompany())) {
						if(isEmpty(user.getCompany())) {
							user.setCompany(Lists.newArrayList());
						}
						for(String company : pUser.getCompany()) {
							if(!user.getCompany().contains(company)) {
								user.getCompany().add(company);
							}
						}
					}
				}
				if(isNotEmpty(pUser.getMobile())){
					user.setMobile(pUser.getMobile());
				}
				user.setId(pUser.getId());
				profileService.saveUser(user, user.getClientid());
			}
			profileService.createUserForClient(user, id);
		} catch (UnsupportedEncodingException e) {
			logger.error(CLASSNAME + method + " ERROR", e);
		}
		if (NullUtil.isNotEmpty(user.getClientid())) {
			return "redirect:/cp_user/" + id + "/" + fullname + "/" + usertype + "/" + user.getClientid() + "/" + month + "/" + year;
		} else {
			return "redirect:/teamuser/" + id + "/" + fullname + "/" + usertype + "/" + month + "/" + year;
		}
	}
	
	@RequestMapping(value = "/createprofileuserClient/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addUserforClient(@ModelAttribute("user") CompanyUser user, @PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addUser::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if(NullUtil.isEmpty(user.getAddsubuser())) {
			user.setAddsubuser("false");
		}
		if (NullUtil.isNotEmpty(user.getId()) && new ObjectId(id).equals(user.getId())) {
			user.setId(null);
		}
		if (NullUtil.isEmpty(user.getClientid())) {
			user.setIsglobal("true");
		} else {
			if(isEmpty(user.getCompany())) {
				user.setCompany(Lists.newArrayList());
				user.getCompany().add(user.getClientid());
			}	
		}
		if(NullUtil.isEmpty(user.getDisable())){
			user.setDisable("true");
		}
		if(NullUtil.isEmpty(user.getAddclient())){
			user.setAddclient("false");
		}
		try {
			CompanyUser pUser = profileService.getCompanyUser(user.getEmail());
			if(user.getPassword() != null){
				user.setPassword(Base64.getEncoder()
					.encodeToString(user.getPassword().getBytes(MasterGSTConstants.PASSWORD_ENCODE_FORMAT)));
			}else{
				User usr = userService.findByEmail(user.getEmail());
				if(isNotEmpty(usr)){
					if(isNotEmpty(usr.getPassword())){
						user.setPassword(usr.getPassword());
					}
					if(isNotEmpty(usr.getMobilenumber())){
						user.setMobile(usr.getMobilenumber());
					}
				}
			}
			if(isEmpty(pUser) || isEmpty(pUser.getId())) {
				profileService.saveUser(user, user.getClientid());
			} else {
				if(isEmpty(user.getId())) {
					if(isNotEmpty(pUser.getCompany())) {
						if(isEmpty(user.getCompany())) {
							user.setCompany(Lists.newArrayList());
						}
						for(String company : pUser.getCompany()) {
							if(!user.getCompany().contains(company)) {
								user.getCompany().add(company);
							}
						}
					}
				}else{
					if(isNotEmpty(pUser.getCompany())) {
						if(isEmpty(user.getCompany())) {
							user.setCompany(Lists.newArrayList());
						}
						for(String company : pUser.getCompany()) {
							if(!user.getCompany().contains(company)) {
								user.getCompany().add(company);
							}
						}
					}
				}
				
				if(isNotEmpty(pUser.getMobile())){
					user.setMobile(pUser.getMobile());
				}
				user.setId(pUser.getId());
				profileService.saveUser(user, user.getClientid());
			}
			profileService.createUserForClient(user, id);
		} catch (UnsupportedEncodingException e) {
			logger.error(CLASSNAME + method + " ERROR", e);
		}
		if (NullUtil.isNotEmpty(user.getClientid())) {
			return "redirect:/cp_user/" + id + "/" + fullname + "/" + usertype + "/" + user.getClientid() + "/" + month + "/" + year;
		} else {
			return "redirect:/teamuser/" + id + "/" + fullname + "/" + usertype + "/" + month + "/" + year;
		}
	}

	@RequestMapping(value = "/chkemail", method = RequestMethod.GET)
	public @ResponseBody User checkEmail(@RequestParam("email") String email, HttpServletResponse response, ModelMap model) throws Exception {
		final String method = "checkEmail::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findByEmailIgnoreCase(email);
		logger.debug(user);
		if(isNotEmpty(user) && isNotEmpty(user.getPassword())) {
			user.setPassword(new String(Base64.getDecoder().decode(user.getPassword()),
				MasterGSTConstants.PASSWORD_ENCODE_FORMAT));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String format = formatter.format(user.getCreatedDate());
			response.setHeader("user_id", user.getId().toString());
			response.setHeader("cdate", format);
			response.setHeader("exstngtype", user.getType());
			response.setHeader("parentid", user.getParentid());
		} else {
			user = new User();
		}
		return user;
	}

	@RequestMapping(value = "/cp_role/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String companyRoles(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "companyRoles::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User usr = userService.findById(id);
		if(NullUtil.isNotEmpty(usr) && NullUtil.isNotEmpty(usr.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(usr.getEmail());
			if(isNotEmpty(companyUser)) {
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
		if (NullUtil.isNotEmpty(clientid)) {
			Client client = clientService.findById(clientid);
			model.addAttribute("client", client);
			model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		}
		Map<String,Map<String, Set<Permission>>> permissionMap = profileService.getPermissionMap(clientid, null);
		List<CompanyRole> roles = profileService.getRoles(id, clientid);
		model.addAttribute("permissionMap", permissionMap);
		model.addAttribute("roles", roles);
		model.addAttribute("features", configService.getFeatures());
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		logger.debug(CLASSNAME + method + END);
		return "profile/roles";
	}

	@RequestMapping(value = "/teamrole/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String teamRoles(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "teamRoles::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			model.addAttribute("companyUser", companyUser);
			if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
				model.addAttribute("addClient", companyUser.getAddclient());
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		Map<String,Map<String, Set<Permission>>> permissionMap = profileService.getPermissionMap(null, null);
		List<CompanyRole> roles = profileService.getRoles(id, null);
		model.addAttribute("cuser", user);
		model.addAttribute("permissionMap", permissionMap);
		model.addAttribute("roles", roles);
		model.addAttribute("features", configService.getFeatures());
		model.addAttribute("acknowledgementTab", "tabenable");
		logger.debug(CLASSNAME + method + END);
		return "profile/roles";
	}

	@RequestMapping(value = "/createrole/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addRole(@ModelAttribute("role") CompanyRole role, @PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addRole::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if (NullUtil.isNotEmpty(role.getId()) && new ObjectId(id).equals(role.getId())) {
			role.setId(null);
		}
		profileService.saveRole(role);
		if (NullUtil.isNotEmpty(role.getClientid())) {
			return "redirect:/cp_role/" + id + "/" + fullname + "/" + usertype + "/" + role.getClientid() + "/" + month + "/" + year;
		} else {
			return "redirect:/teamrole/" + id + "/" + fullname + "/" + usertype + "/" + month + "/" + year;
		}
	}

	@RequestMapping(value = "/cp_group/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String companyGroups(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "companyGroups::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		if (NullUtil.isNotEmpty(clientid)) {
			Client client = clientService.findById(clientid);
			model.addAttribute("client", client);
			model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		}
		model.addAttribute("groups", profileService.getGroups(id, clientid));
		model.addAttribute("features", configService.getFeatures());
		logger.debug(CLASSNAME + method + END);
		return "profile/groups";
	}

	@RequestMapping(value = "/teamgroup/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String teamGroups(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "teamGroups::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			model.addAttribute("companyUser", companyUser);
			if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
				model.addAttribute("addClient", companyUser.getAddclient());
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		model.addAttribute("groups", profileService.getGroups(id, null));
		model.addAttribute("features", configService.getFeatures());
		logger.debug(CLASSNAME + method + END);
		return "profile/groups";
	}

	@RequestMapping(value = "/creategroup/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addGroup(@ModelAttribute("group") CompanyGroup group, @PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addGroup::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if (NullUtil.isNotEmpty(group.getId()) && new ObjectId(id).equals(group.getId())) {
			group.setId(null);
		}
		profileService.saveGroup(group);
		if (NullUtil.isNotEmpty(group.getClientid())) {
			return "redirect:/cp_group/" + id + "/" + fullname + "/" + usertype + "/" + group.getClientid() + "/" + month + "/" + year;
		} else {
			return "redirect:/teamgroup/" + id + "/" + fullname + "/" + usertype + "/" + month + "/" + year;
		}
	}

	@RequestMapping(value = "/cp_features/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String features(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "features::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		if (NullUtil.isNotEmpty(clientid)) {
			Client client = clientService.findById(clientid);
			model.addAttribute("client", client);
			model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		}
		model.addAttribute("features", configService.getFeatures());
		logger.debug(CLASSNAME + method + END);
		return "profile/features";
	}
	
	@RequestMapping(value = "/cp_bankDetails/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String companyBankdetails(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model)
			throws Exception {
		final String method = "profileBankDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		if (NullUtil.isNotEmpty(clientid)) {
			Client client = clientService.findById(clientid);
			model.addAttribute("client", client);
			model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		}
		model.addAttribute("bankdetails", profileService.getCompanyBankDetails(clientid));
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		logger.debug(CLASSNAME + method + END);
		return "profile/bankdetails";
	}
	
	@RequestMapping(value = "/bnkdtls/{id}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody List<CompanyBankDetails> fetchBankdetails(@PathVariable("id") String id,@PathVariable("clientid") String clientid, ModelMap model) {
		final String method = "fetchBankdetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		return profileService.getCompanyBankDetails(clientid);
	}
	
	@RequestMapping(value = "/createbankdetails/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addBankDetails(@ModelAttribute("bankdetails") CompanyBankDetails bankdetails, @PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, 
			@PathVariable("month") int month, @PathVariable("year") int year,@RequestParam("file") MultipartFile file, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "addBankDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if (NullUtil.isNotEmpty(bankdetails.getId()) && new ObjectId(id).equals(bankdetails.getId())) {
			bankdetails.setId(null);
		}
		profileService.saveBankDetailsQRCode(bankdetails,file);
		return "redirect:/cp_bankDetails/" + id + "/" + fullname + "/" + usertype + "/" + bankdetails.getClientid() + "/" + month + "/" + year;
	}

	@RequestMapping(value = "/features/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String features(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "features::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			model.addAttribute("companyUser", companyUser);
			if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
				model.addAttribute("addClient", companyUser.getAddclient());
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		model.addAttribute("features", configService.getFeatures());
		logger.debug(CLASSNAME + method + END);
		return "profile/features";
	}
	
	@RequestMapping(value = "/usrprms", method = RequestMethod.GET)
	public @ResponseBody Map<String, List<Permission>> getPermissions(ModelMap model,
			@RequestParam(value = "userid", required = true) String userid) throws Exception {
		final String method = "getPermissions::";
		logger.debug(CLASSNAME + method + BEGIN);
		return profileService.getPermissions(userid);
	}
	
	@RequestMapping(value = "/usrclntprms", method = RequestMethod.GET)
	public @ResponseBody Map<String, List<Permission>> getUserClientPermissions(ModelMap model,
			@RequestParam(value = "userid", required = true) String userid,
			@RequestParam(value = "clientid", required = true) String clientid) throws Exception {
		final String method = "getUserClientPermissions::";
		logger.debug(CLASSNAME + method + BEGIN);
		if(isNotEmpty(clientid)) {
			Map<String, List<Permission>> response = profileService.getUserClientPermissions(userid, clientid);
			/*if(isEmpty(response)) {
				response = profileService.getPermissions(userid);
			}*/
			return response;
		} else {
			return profileService.getPermissions(userid);
		}
	}
	
	@RequestMapping(value = "/sclntprms/{clientid}", method = RequestMethod.GET)
	public @ResponseBody void saveClientPermissions(@PathVariable("clientid") String clientid,
			@RequestParam(value = "permissions", required = true) Set<String> permissions) throws Exception {
		final String method = "saveClientPermissions :: ";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.saveClientPermissions(clientid, permissions);
		logger.debug(CLASSNAME + method + END);
	}

	@RequestMapping(value = "/uploadFile/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.POST)
	public String uploadFile(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@RequestParam("category") String category, @RequestParam("file") MultipartFile file, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "uploadFile::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (file != null && !file.isEmpty()) {
			logger.debug(CLASSNAME + method + file.getOriginalFilename());
			try {
				profileService.updateExcelData(file, category, id, fullname, clientid);
			} catch (Exception e) {
				logger.error(CLASSNAME + method + " ERROR", e);
				model.addAttribute("error", "Unable to import due to - "+e.getMessage());
			}
		} else {
			model.addAttribute("error", "Select the file to import");
		}
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		logger.debug(CLASSNAME + method + END);
		if (category.equals(PRODUCTS)) {
			model.addAttribute("products", profileService.getProducts(clientid));
			return "redirect:/cp_products/" + id + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year;
		} else if (category.equals(SERVICES)) {
			model.addAttribute("services", profileService.getServices(clientid));
			return "redirect:/cp_services/" + id + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year;
		} else if (isNotEmpty(category) && category.equals(ITEMS)) {
			model.addAttribute("items", profileService.getItems(clientid));
			return "redirect:/cp_items/" + id + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year;
		} else if (isNotEmpty(category) && category.equals(CUSTOMERS)) {
			model.addAttribute("customers", profileService.getCustomers(clientid));
			return "redirect:/cp_customers/" + id + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year;
		} else if (isNotEmpty(category) && category.equals(SUPPLIERS)) {
			model.addAttribute("customers", profileService.getSuppliers(clientid));
			return "redirect:/cp_suppliers/" + id + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year;
		}
		return "profile/about";
	}
	
	@RequestMapping(value = "/srchcustomer", method = RequestMethod.GET)
	public @ResponseBody List<CompanyCustomers> customerData(ModelMap model,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "clientid", required = true) String clientid) throws Exception {
		final String method = "customerData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + query);
				
		List<CompanyCustomers> cmpnycustomerslist=profileService.getSearchedCustomers(clientid, query);
				
		//cmpnycustomerslist.stream().filter(cust->cust.getCustomerId()!=null).forEach(cust->cust.setCustomerIdAndName(cust.getCustomerId()+"-"+cust.getName()));
		
		cmpnycustomerslist.stream().forEach(cust->{ 
			if(NullUtil.isNotEmpty(cust.getAddress())) {
				String custadd = cust.getAddress(); 
				if(custadd.contains("\n")) {
					custadd = custadd.replace("\n", "");
				}
				cust.setAddress(custadd);
			}
			if(NullUtil.isNotEmpty(cust.getCustomerId())) {
				cust.setCustomerIdAndName(cust.getCustomerId()+"-"+cust.getName());
			}else {
				cust.setCustomerIdAndName(cust.getName());
			}
		});
		return cmpnycustomerslist;
	}	
	@RequestMapping(value = "/srchgroupName", method = RequestMethod.GET)
	public @ResponseBody List<Client> clientData(ModelMap model,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "id", required = true) String id) throws Exception {
		final String method = "clientData::";
		logger.debug(CLASSNAME + method + BEGIN);
		List<Client> lClient = clientService.findByUserid(id);
		List<Client> lClients = profileService.getSearchedGroupName(id,query);
		List<Client> groupnameList = Lists.newArrayList();
		
		if(isNotEmpty(lClients) && isNotEmpty(lClient)) {
			for (Client client : lClients) {
				if(lClient.contains(client)){
					groupnameList.add(client);
				}
			}
		}
		return groupnameList;
	}
	
	@RequestMapping(value = "/srchUsers/{usertype}/{id}", method = RequestMethod.GET)
	public @ResponseBody List<User> userData(ModelMap model,@PathVariable("usertype") String usertype,@PathVariable("id") String id,
			@RequestParam(value = "query", required = true) String query) throws Exception {
		final String method = "userData::";
		logger.debug(CLASSNAME + method + BEGIN);
		User usr = userService.findById(id);
		List<User> users = userService.getAllUsersData(usertype,query);
		users.remove(usr);
		return users;
	}
	
	@RequestMapping(value = "/srchsupplier", method = RequestMethod.GET)
	public @ResponseBody List<CompanySuppliers> supplierData(ModelMap model,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "clientid", required = true) String clientid) throws Exception {
		final String method = "supplierData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + query);
		
		List<CompanySuppliers> supplierslist=profileService.getSearchedSuppliers(clientid, query);
		
		supplierslist.stream().forEach(cust->{ 
			if(NullUtil.isNotEmpty(cust.getAddress())) {
				String custadd = cust.getAddress(); 
				if(custadd.contains("\n")) {
					custadd = custadd.replace("\n", "");
				}
				cust.setAddress(custadd);
			}
			if(NullUtil.isNotEmpty(cust.getSupplierCustomerId()))
				cust.setCustomerIdAndName(cust.getSupplierCustomerId()+"-"+cust.getName());
			else
				cust.setCustomerIdAndName(cust.getName());
		});
		
		return supplierslist;
	}
	
	@RequestMapping(value = "/srchitem", method = RequestMethod.GET)
	public @ResponseBody List<CompanyItems> itemData(ModelMap model,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "clientid", required = true) String clientid) throws Exception {
		final String method = "itemData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + query);
		
		
		List<CompanyItems> companyItemsLst=profileService.getSearchedItems(clientid, query);
		
		
		/*companyItemsLst.stream().forEach(item->item.setItemnoAndFullname(item.getItemno()+"-"+item.getFullname()));
		System.out.println(companyItemsLst);*/	
		return companyItemsLst;
	}
	
	@RequestMapping(value = "/srchcommon", method = RequestMethod.GET)
	public @ResponseBody CommonBO getCommonList(ModelMap model,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "clientid", required = true) String clientid) throws Exception {
		final String method = "getCommonList::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + query);
		return profileService.getCommonList(clientid, query);
	}
	
	@RequestMapping(value = "/clntlogo/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.POST)
	public String saveClientLogo(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("file") MultipartFile file) {
		final String method = "saveClientLogo::";
		logger.debug(CLASSNAME + method + BEGIN);
		DBObject metaData = new BasicDBObject();
		metaData.put("clientid", clientid);
		String contentType = file.getContentType();
		if(isEmpty(contentType)) {
			contentType = "image/png";
		}
		Client client = clientService.findById(clientid);
		if(isNotEmpty(client.getLogoid())) {
			gridOperations.delete(new Query(Criteria.where("_id").is(client.getLogoid())));
		}
		try {
			String imageId = gridOperations.store(file.getInputStream(), file.getName(), contentType, metaData).getId().toString();
			client.setLogoid(imageId);
			clientService.saveClient(client);
		} catch (IOException e) {
			logger.error(CLASSNAME + method + "ERROR", e);
		}
		return "redirect:/about/" + id + "/" + fullname + "/" + usertype + "/" + client.getId() + "/" + month + "/" + year;
	}
	
	@RequestMapping(value = "/getlogo/{imgid}", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getLogo(@PathVariable("imgid") String imgid) {
		final String method = "getLogo::";
		logger.debug(CLASSNAME + method + BEGIN);
		if(isNotEmpty(imgid)) {
			GridFSDBFile imageFile = gridOperations.findOne(new Query(Criteria.where("_id").is(imgid)));
			try {
				byte[] targetArray = ByteStreams.toByteArray(imageFile.getInputStream());
				final HttpHeaders headers = new HttpHeaders();
			    headers.setContentType(MediaType.IMAGE_PNG);
			    return new ResponseEntity<byte[]>(targetArray, headers, HttpStatus.CREATED);
			} catch (IOException e) {
				logger.error(CLASSNAME + method + "ERROR", e);
			}
		}
		return null;
	}
	@RequestMapping(value = "/deleteLogo/{logoid}/{clientId}", method = RequestMethod.GET)
	public @ResponseBody void deleteLogo(@PathVariable("logoid") String logoid, @PathVariable("clientId") String clientId, 
			ModelMap model) throws Exception {
		final String method = "deleteLogo::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientRepository.findOne(clientId);
		if(isNotEmpty(client) && isNotEmpty(client.getLogoid()) && logoid.equalsIgnoreCase(client.getLogoid())) {
			client.setLogoid("");
		}
		clientService.saveClient(client);
	}
	@RequestMapping(value = "/delproduct/{productid}", method = RequestMethod.GET)
	public @ResponseBody void deleteProduct(@PathVariable("productid") String productid, 
			ModelMap model) throws Exception {
		final String method = "deleteProduct::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteProduct(productid);
	}
	
	@RequestMapping(value = "/delcustomer/{customerid}", method = RequestMethod.GET)
	public @ResponseBody void deleteCustomer(@PathVariable("customerid") String customerid, 
			ModelMap model) throws Exception {
		final String method = "deleteCustomer::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteCustomer(customerid);
	}
	
	@RequestMapping(value = "/delsupplier/{supplierid}", method = RequestMethod.GET)
	public @ResponseBody void deleteSupplier(@PathVariable("supplierid") String supplierid, 
			ModelMap model) throws Exception {
		final String method = "deleteSupplier::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteSupplier(supplierid);
	}
	
	@RequestMapping(value = "/delitem/{itemid}", method = RequestMethod.GET)
	public @ResponseBody void deleteItem(@PathVariable("itemid") String itemid, 
			ModelMap model) throws Exception {
		final String method = "deleteSupplier::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteItem(itemid);
	}
	
	@RequestMapping(value = "/delservice/{serviceid}", method = RequestMethod.GET)
	public @ResponseBody void deleteService(@PathVariable("serviceid") String serviceid, 
			ModelMap model) throws Exception {
		final String method = "deleteSupplier::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteService(serviceid);
	}
	
	@RequestMapping(value = "/cp_branches/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String branchList(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("type") String type,  ModelMap model)
			throws Exception {
		final String method = "branchList::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("type", type);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		model.addAttribute("id", id);
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		logger.debug(CLASSNAME + method + END);
		updateModel(model, id, fullname, usertype, month, year);
		
		return "profile/branches";
	}
	
	@RequestMapping(value = "/cp_verticals/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String verticalList(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("type") String type, ModelMap model)
			throws Exception {
		final String method = "verticalList::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("type", type);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		model.addAttribute("id", id);
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		logger.debug(CLASSNAME + method + END);
		updateModel(model, id, fullname, usertype, month, year);
		return "profile/verticals";
	}
	
	@RequestMapping(value = "/cp_costcenter/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String costcenterList(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("type") String type,  ModelMap model)
			throws Exception {
		final String method = "branchList::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("type", type);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		model.addAttribute("id", id);
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		logger.debug(CLASSNAME + method + END);
		updateModel(model, id, fullname, usertype, month, year);
		
		return "profile/costcenter";
	}
	
	@RequestMapping(value = "/addbranch/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String addBranch(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@RequestParam("type") String type, @RequestParam("name") String name, @RequestParam("elemId") String elemId,
			@RequestParam("address") String address, @RequestParam("parentId") String parentId, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model)
			throws Exception {
		final String method = "addBranchInfo::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		if(isEmpty(elemId)) {
			clientService.addBranchInfo(clientid, name, address, parentId);
		} else {
			clientService.updateBranchInfo(clientid, elemId, name, address, parentId);
		}
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("type", type);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		//model.addAttribute("branches", lclient.getBranches());
		logger.debug(CLASSNAME + method + END);
		if(type.equals("Branch") || type == "") {
		    return "redirect:/cp_branches/" + id + "/" + fullname + "/" + usertype + "/" + client.getId() + "/" + month + "/" + year + "?type=";
		} else {
			 return "redirect:/cp_branches/" + id + "/" + fullname + "/" + usertype + "/" + client.getId() + "/" + month + "/" + year + "?type=compsbrnch";
		}
	}
	
	@RequestMapping(value = "/addcostcenter/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String addCostcenter(@PathVariable("id") String id, @PathVariable("name") String fullname,@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, @RequestParam("type") String type, @RequestParam("name") String name, @RequestParam("elemId") String elemId, @RequestParam("parentId") String parentId, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model)throws Exception {
		final String method = "addCostcenterInfo::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		if(isEmpty(elemId)) {
			clientService.addCostCenterInfo(clientid, name, parentId);
		} else {
			clientService.updateCostCenterInfo(clientid, elemId, name, parentId);
		}
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("type", type);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		logger.debug(CLASSNAME + method + END);
		if(type.equals("CostCenter") || type == "") {
		    return "redirect:/cp_costcenter/" + id + "/" + fullname + "/" + usertype + "/" + client.getId() + "/" + month + "/" + year + "?type=";
		} else {
			 return "redirect:/cp_costcenter/" + id + "/" + fullname + "/" + usertype + "/" + client.getId() + "/" + month + "/" + year + "?type=compsbrnch";
		}
	}
	
	@RequestMapping(value = "/addvertical/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String addVertical(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@RequestParam("type") String type, @RequestParam("name") String name, @RequestParam("elemId") String elemId,
			@RequestParam("address") String address, @RequestParam("parentId") String parentId, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model)
			throws Exception {
		final String method = "addClientInfo::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		if(isEmpty(elemId)) {
			clientService.addVerticalInfo(clientid, name, address, parentId);
		} else {
			clientService.updateVerticalInfo(clientid, elemId, name, address, parentId);
		}
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("type", type);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		//model.addAttribute("branches", lclient.getBranches());
		logger.debug(CLASSNAME + method + END);
		
		if(type.equals("Vertical") || type == "") {
		    return "redirect:/cp_verticals/" + id + "/" + fullname + "/" + usertype + "/" + client.getId() + "/" + month + "/" + year + "?type=";
		} else {
			 return "redirect:/cp_verticals/" + id + "/" + fullname + "/" + usertype + "/" + client.getId() + "/" + month + "/" + year + "?type=compsvrtl";
		}

	}
	
	@RequestMapping(value = "/updtusrdoc/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updateUserDocAccess(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updateUserDocAccess::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessDrive(flag);
			userService.updateUser(user);
		}
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessDrive(flag);
					userRepository.save(usr);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/updtusrtravelreport/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updateUserTravelReportAccess(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updateUserDocAccess::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessTravel(flag);
			userService.updateUser(user);
		}
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessTravel(flag);
					userRepository.save(usr);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/updtusrimport/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updateUserImportAccess(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updateUserImportAccess::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessImports(flag);
			userService.updateUser(user);
		}
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessImports(flag);
					userRepository.save(usr);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/updtusrreport/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updateUserReportAccess(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updateUserImportAccess::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessReports(flag);;
			userService.updateUser(user);
		}
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessReports(flag);
					userRepository.save(usr);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/teamdocs/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String enableDocs(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "enableDocs::";
		logger.debug(CLASSNAME + method + BEGIN);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			model.addAttribute("companyUser", companyUser);
			if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
				model.addAttribute("addClient", companyUser.getAddclient());
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		logger.debug(CLASSNAME + method + END);
		return "profile/enabledocs";
	}
	
	@RequestMapping(value = "/getdocs/{id}", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> fetchDocs(@PathVariable("id") String id, ModelMap model) throws Exception {
		final String method = "fetchDocs::";
		logger.debug(CLASSNAME + method + BEGIN);
		return googleDriveAdapter.getFiles(id);
	}
	
	@RequestMapping(value = "/cp_docs/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String docs(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "docs::";
		logger.debug(CLASSNAME + method + BEGIN);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		Client client = clientService.findById(clientid);
		String driveId=null;
		if(isNotEmpty(client)) {
			driveId=client.getDriveid();
		}
		if(isNotEmpty(driveId)) {
			model.addAttribute("docs", googleDriveAdapter.getFileList(driveId, id));
		}
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		logger.debug(CLASSNAME + method + END);
		return "profile/docs";
	}
	
	@RequestMapping(value = "/invpdf", method = RequestMethod.POST)
	public @ResponseBody void invoicePDF(@RequestBody String html, HttpServletRequest request) throws Exception {
		final String method = "invoicePDF::";
		logger.debug(CLASSNAME + method + BEGIN);
		HttpSession session = request.getSession(false);
		if(session != null) {
			File file = new File(session.getId()+"invoice.pdf");
		    try {
	        	PdfRendererBuilder pdfBuilder = new PdfRendererBuilder();
	        	W3CDom w3cDom = new W3CDom();
	            Document w3cDoc = w3cDom.fromJsoup(Jsoup.parse(html));
	            pdfBuilder.withW3cDocument(w3cDoc, "/");
	            FileOutputStream fileOutputStream = new FileOutputStream(file);
	            pdfBuilder.toStream(fileOutputStream);
	            pdfBuilder.run();
	            session.setAttribute("pdf", file);
			} catch(Exception e) {
				logger.error(CLASSNAME + method, e);
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/dwnldpdf", method = RequestMethod.GET, produces = "application/pdf")
	public @ResponseBody FileSystemResource downloadPDF(HttpServletResponse response, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(isNotEmpty(session) && isNotEmpty(session.getAttribute("pdf"))) {
			File file = (File) session.getAttribute("pdf");
			response.setContentType("application/pdf");
		    response.setHeader("Content-Disposition", "inline; filename=invoice.pdf");
			return new FileSystemResource(file);
		}
		return new FileSystemResource(new File("invoice.pdf"));
	}
	
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/updtusrdate", method = RequestMethod.GET)
    public @ResponseBody String updateCreatedDate() {
    	List<User> users = userService.getAllUsers();
    	List<User> filteredUsers = Lists.newArrayList();
    	for(User user : users) {
    		if(isEmpty(user.getCreatedDate()) && isNotEmpty(user.getUpdatedDate())) {
    			user.setCreatedDate(user.getUpdatedDate());
    			filteredUsers.add(user);
    		}
    	}
    	if(isNotEmpty(filteredUsers)) {
    		userService.saveUsers(filteredUsers);
    	}
    	return "Completed Successfully";
    }
    
    @RequestMapping(value = "/delinvconfig/{id}", method = RequestMethod.GET)
	public @ResponseBody void delInvoiceConfig(@PathVariable("id") String id, ModelMap model) throws Exception {
		final String method = "delInvoiceConfig::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteInvoiceSubmission(id);
    }
	
	@RequestMapping(value = "/uploadDoc/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.POST)
	public String uploadDoc(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year, 
			@RequestParam("file") MultipartFile file, ModelMap model) throws Exception {
		final String method = "uploadDoc::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		if (file != null && !file.isEmpty()) {
			logger.debug(CLASSNAME + method + file.getOriginalFilename());
			String driveId=null;
			if(isNotEmpty(client) && isEmpty(client.getDriveid())) {
				driveId=googleDriveAdapter.createFolder(client.getGstnnumber(), id);
				client.setDriveid(driveId);
				clientService.saveClient(client);
			} else if(isNotEmpty(client)) {
				driveId=client.getDriveid();
			}
			if(isNotEmpty(driveId)) {
				googleDriveAdapter.uploadFile(file, driveId, id);
				model.addAttribute("docs", googleDriveAdapter.getFiles(driveId, id));
			}
		} else {
			model.addAttribute("error", "Select the file to upload");
		}
		updateModel(model, id, fullname, usertype, month, year);
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		logger.debug(CLASSNAME + method + END);
		return "redirect:/cp_docs/" + id + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year;
	}
	@RequestMapping(value = "/delbnkdtl/{banknameid}", method = RequestMethod.GET)
	public @ResponseBody void deleteBankDetails(@PathVariable("banknameid") String banknameid, 
			ModelMap model) throws Exception {
		final String method = "deleteBankDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		if(isNotEmpty(banknameid)) {
			CompanyBankDetails bankdetail = companyBankDetailsRepository.findOne(banknameid);
			if(isNotEmpty(bankdetail) && isNotEmpty(bankdetail.getQrcodeid())) {
				gridOperations.delete(new Query(Criteria.where("_id").is(bankdetail.getQrcodeid())));
			}
			ledgerRepository.deleteByClientidAndBankId(bankdetail.getClientid(),banknameid);
		}
		profileService.deleteBankDetail(banknameid);
	}
	
	@RequestMapping(value = "/delbranch/{branchid}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody void deleteBranchDetails(@PathVariable("branchid") String branchid,@PathVariable("clientid") String clientid, 
			ModelMap model) throws Exception {
		final String method = "deleteBranchDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteBranch(branchid, clientid);
	}
	
	@RequestMapping(value = "/delcostcenter/{branchid}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody void deleteCostCenterDetails(@PathVariable("branchid") String branchid,@PathVariable("clientid") String clientid, 
			ModelMap model) throws Exception {
		final String method = "deleteBranchDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteCostcenter(branchid, clientid);
	}
	
	@RequestMapping(value = "/delvertical/{verticalid}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody void deleteVerticalDetails(@PathVariable("verticalid") String verticalid,@PathVariable("clientid") String clientid, 
			ModelMap model) throws Exception {
		final String method = "deleteVerticalDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteVertical(verticalid, clientid);
	}
	
	@RequestMapping(value = "/delSubVertical/{subVerticalId}/{verticalid}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody void deleteSubVerticalDetails(@PathVariable("subVerticalId") String subVerticalId,@PathVariable("verticalid") String verticalid,@PathVariable("clientid") String clientid, 
			ModelMap model) throws Exception {
		final String method = "deleteSubVerticalDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteSubVertical(subVerticalId,verticalid, clientid);
	}
	
	@RequestMapping(value = "/delSubBranch/{subBranchId}/{branchid}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody void deleteSubBranchDetails(@PathVariable("subBranchId") String subBranchId,@PathVariable("branchid") String branchid,@PathVariable("clientid") String clientid, 
			ModelMap model) throws Exception {
		final String method = "deleteSubBranchDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteSubBranch(subBranchId,branchid, clientid);
	}
	
	@RequestMapping(value = "/delSubCostCenter/{subBranchId}/{branchid}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody void deleteSubCostCenterDetails(@PathVariable("subBranchId") String subBranchId,@PathVariable("branchid") String branchid,@PathVariable("clientid") String clientid, 
			ModelMap model) throws Exception {
		final String method = "deleteSubBranchDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteSubCostcenter(subBranchId,branchid, clientid);
	}
	/*@CrossOrigin(origins = "*")
    @RequestMapping(value = "/updtSubscriptionDetails", method = RequestMethod.GET)
    public @ResponseBody String updateSubscriptionDetails() {
		List<SubscriptionDetails> subscriptionDetailsList = subscriptionService.getAllSubscriptions();
    	List<SubscriptionDetails> filteredUsers = Lists.newArrayList();
    	for(SubscriptionDetails subscriptionDetails : subscriptionDetailsList) {
    		if(isEmpty(subscriptionDetails.getApiType())) {
    			User usr = userService.findById(subscriptionDetails.getUserid());
    			if(NullUtil.isNotEmpty(usr)){
	    			if("aspdeveloper".equals(usr.getType())){
	    				subscriptionDetails.setApiType("GSTAPI");
	    				filteredUsers.add(subscriptionDetails);
	    			}
    			}
    		}
    	}
    	if(isNotEmpty(filteredUsers)) {
    		subscriptionService.saveSubscriptionDetails(filteredUsers);
    	}
    	return "Completed Successfully";
    }
	
	@CrossOrigin(origins = "*")
    @RequestMapping(value = "/updteSubscriptionDetails/{month}/{year}", method = RequestMethod.GET)
    public @ResponseBody String updateSubscriptionDetailsByMonthYear(@PathVariable("month") int month,@PathVariable("year") int year) {
		List<SubscriptionDetails> subscriptionDetailsList = subscriptionService.getAllSubscriptions();
    	List<SubscriptionDetails> filteredUsers = Lists.newArrayList();
    	for(SubscriptionDetails subscriptionDetails : subscriptionDetailsList) {
    		if(isEmpty(subscriptionDetails.getApiType())) {
    			User usr = userService.findById(subscriptionDetails.getUserid());
    			if(NullUtil.isNotEmpty(usr)){
	    			logger.info(subscriptionDetails.getUserid() +"-" + usr);
	    			if("aspdeveloper".equals(usr.getType())){
	    				subscriptionDetails.setApiType("GSTAPI");
	    				filteredUsers.add(subscriptionDetails);
	    			}
    			}
    		}
    	}
    	if(isNotEmpty(filteredUsers)) {
    		logger.info(filteredUsers);
    		subscriptionService.saveSubscriptionDetails(filteredUsers);
    	}
    	return "Completed Successfully";
    }*/
	
	@RequestMapping(value = "/cp_ClientsReports/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String userClientReports(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "profileCenterClients::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		return "profile/reports";
	}
	
	@RequestMapping(value = "/cp_ClientsReportsData/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String userClientReportsData(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "profileCenterClients::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		List<String> retTypes = Arrays.asList("GSTR1","GSTR3B","GSTR4","GSTR5","GSTR6","GSTR8","GSTR9","GSTR10");
		List<Client> clients = clientService.findByUserid(id);
		List<String> clientids = Lists.newArrayList();
		for(Client client : clients) {
			clientids.add(client.getId().toString());
		}
		model.addAttribute("clientids",clientids);
		model.addAttribute("retTypes",retTypes);
		model.addAttribute("clientslst",clients);
		return "profile/userClientReports";
	}
	
	
	@RequestMapping(value = "/userclntflngstatus/{id}/{year}", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> clientFilingStatus(@PathVariable("id") String id,
			@PathVariable("year") int year,@RequestParam("exceldwnld") String exceldwnld, ModelMap model) throws Exception {
		final String method = "clientFilingStatus::";
		logger.debug(CLASSNAME + method + BEGIN);
		List<Map<String, Object>> userclients = Lists.newArrayList();
		List<Client> lClient = Lists.newArrayList();
		if(NullUtil.isNotEmpty(id)){
			lClient = clientService.findByUserid(id);
		}
		
		if(NullUtil.isNotEmpty(lClient)){
			for(Client client : lClient){
				Map<String,Object> clntname_gstnumber = Maps.newHashMap();
				clntname_gstnumber.put("clientName", client.getBusinessname());
				clntname_gstnumber.put("gstno", client.getGstnnumber());
				clntname_gstnumber.put("clientid", client.getId().toString());
				Map<String, Map<String, List<String>>> clientFilingStatus= clientService.getClientFilingStatusReport(client.getId().toString(), year,exceldwnld);
				
				clntname_gstnumber.put("statusmap", clientFilingStatus);
				userclients.add(clntname_gstnumber);
			}
			
		}
		return userclients;
	}
	
	@RequestMapping(value = "/createPrintConfiguration/{id}/{clientid}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addPrintConfiguration(@ModelAttribute("printform") PrintConfiguration printform, @PathVariable("id") String id,@PathVariable("clientid") String clientid,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addRole::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if (NullUtil.isNotEmpty(printform.getId()) && new ObjectId(id).equals(printform.getId())) {
			printform.setId(null);
		}
		printform.setClientid(clientid);
		profileService.savePrintConfiguration(printform);
		return "redirect:/cp_upload/"+id+"/"+fullname+"/"+usertype+"/"+clientid + "/" + month + "/" + year+ "?type=printconfig";
	}
	
	@RequestMapping(value = "/clntsignature/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.POST)
	public String saveClientSignature(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("file") MultipartFile file) {
		final String method = "saveClientSignature::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		if(file != null && isNotEmpty(file.getOriginalFilename())) {
			DBObject metaData = new BasicDBObject();
			metaData.put("clientid", clientid);
			String contentType = file.getContentType();
			if(isEmpty(contentType)) {
				contentType = "image/png";
			}
			if(isNotEmpty(client.getSignid())) {
				gridOperations.delete(new Query(Criteria.where("_id").is(client.getSignid())));
			}
			try {
				String imageId = gridOperations.store(file.getInputStream(), file.getName(), contentType, metaData).getId().toString();
				client.setSignid(imageId);
				clientService.saveClient(client);
			} catch (IOException e) {
				logger.error(CLASSNAME + method + "ERROR", e);
			}
		}
		return "redirect:/cp_upload/" + id + "/" + fullname + "/" + usertype + "/" + client.getId() + "/" + month + "/" + year+ "?type=";
	}
	
	@RequestMapping(value = "/clntqrcode/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.POST)
	public String saveClientQrCode(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("file") MultipartFile file) {
		final String method = "saveClientSignature::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		if(file != null && isNotEmpty(file.getOriginalFilename())) {
			DBObject metaData = new BasicDBObject();
			metaData.put("clientid", clientid);
			String contentType = file.getContentType();
			if(isEmpty(contentType)) {
				contentType = "image/png";
			}
			if(isNotEmpty(client.getQrcodeid())) {
				gridOperations.delete(new Query(Criteria.where("_id").is(client.getQrcodeid())));
			}
			try {
				String imageId = gridOperations.store(file.getInputStream(), file.getName(), contentType, metaData).getId().toString();
				client.setQrcodeid(imageId);
				clientService.saveClient(client);
			} catch (IOException e) {
				logger.error(CLASSNAME + method + "ERROR", e);
			}
		}
		return "redirect:/cp_upload/" + id + "/" + fullname + "/" + usertype + "/" + client.getId() + "/" + month + "/" + year+ "?type=";
	}
	
	@RequestMapping(value = "/clntpermissions/{clientid}", method = RequestMethod.GET)
	public @ResponseBody void saveClientPermissions(@PathVariable("clientid") String clientid,
			@RequestParam("role") String role, @RequestParam("permissions") List<String> permissions) {
		final String method = "saveClientPermissions::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.updateClientPermissions(clientid, role, permissions);
	}
	
	@RequestMapping(value="/customeridexits/{clientid}/{customerid}",method=RequestMethod.GET)
	public @ResponseBody String customeridexits(@PathVariable String clientid,@PathVariable String customerid) {
		
		CompanyCustomers companycustomer=profileService.getCompanyCustomerData(clientid, customerid);
		if(NullUtil.isNotEmpty(companycustomer)) {
			return "success";
		}else {
			return "not-success";
		}
	}
	
	@RequestMapping(value="/rolesexits/{userid}/{clientid}/{name}",method=RequestMethod.GET)
	public @ResponseBody boolean rolesexits(@PathVariable String userid,@PathVariable String clientid,@PathVariable String name) {
		
		List<CompanyRole> roles =profileService.getRoles(userid, clientid);
		boolean roleexists = false;

		for(CompanyRole role : roles) {
		String rolename = role.getName();
		
		if(rolename.equalsIgnoreCase(name.trim())) {
			roleexists =  true;
			break;
	   }else {
		   roleexists = false;
	   }
	}
		return roleexists;
	}
	
	@RequestMapping(value="/branchesexits/{clientid}",method=RequestMethod.GET)
	public @ResponseBody boolean branchesexits(@PathVariable String clientid,@RequestParam String name,@RequestParam String address) {
		Client client=clientService.findById(clientid);
		List<Branch> branches =client.getBranches();
		boolean branchexists = false;
		for(int i=0;i<branches.size();i++) {
			String bname = branches.get(i).getName();
			String baddr = branches.get(i).getAddress();
			if(name.equals(bname) && address.equals(baddr)) {
				branchexists =  true;
				break;
		   }else {
			   branchexists = false;
		   }
		}
		return branchexists;
	}
	
	@RequestMapping(value="/costcenterexists/{clientid}",method=RequestMethod.GET)
	public @ResponseBody boolean costcenterexists(@PathVariable String clientid,@RequestParam String name) {
		Client client=clientService.findById(clientid);
		List<CostCenter> costcenter =client.getCostCenter();
		boolean costcenterexists = false;
		for(int i=0;i<costcenter.size();i++) {
			String costcentername = costcenter.get(i).getName();
			if(name.equals(costcentername)) {
				costcenterexists =  true;
				break;
		   }else {
			   costcenterexists = false;
		   }
		}
		return costcenterexists;
	}
	
	@RequestMapping(value="/verticalsexits/{clientid}",method=RequestMethod.GET)
	public @ResponseBody boolean verticalsexits(@PathVariable String clientid,@RequestParam String name,@RequestParam String address) {
		Client client=clientService.findById(clientid);
		List<Vertical> vertical =client.getVerticals();
		boolean verticalexists = false;
		for(int i=0;i<vertical.size();i++) {
			String vname = vertical.get(i).getName();
			String vaddr = vertical.get(i).getAddress();
			if(name.equals(vname) && address.equals(vaddr)) {
				verticalexists =  true;
				break;
		   }else {
			   verticalexists = false;
		   }
		}
		return verticalexists;
	}
	
 @RequestMapping(value="/bankdetailsexits/{clientid}/{accno}",method=RequestMethod.GET)
	public @ResponseBody boolean bankdetailsexits(@PathVariable String clientid,@PathVariable String accno) {
		//Client client=clientService.findById(clientid);
		List<CompanyBankDetails> bankdetails = profileService.getCompanyBankDetails(clientid);
		boolean bankdetailsexists = false;
	
			for(CompanyBankDetails bank : bankdetails) {
			String accountno = bank.getAccountnumber();
			
			if(accno.equals(accountno)) {
				bankdetailsexists =  true;
				break;
		   }else {
			   bankdetailsexists = false;
		   }
		}
		return bankdetailsexists;
	} 
	@RequestMapping(value="/suppliercustomeridexits/{clientid}/{supplierid}",method=RequestMethod.GET)
	public @ResponseBody String suppliercustomeridexits(@PathVariable String clientid,@PathVariable String supplierid) {
		CompanySuppliers companysuppliers=profileService.getCompanySupplierData(clientid, supplierid);
		if(NullUtil.isNotEmpty(companysuppliers)) {
			return "success";
		}else {
			return "not-success";
		}
	}
	
	@RequestMapping(value = "/subuserEmailCheck/{usertype}/{id}", method = RequestMethod.GET)
	public @ResponseBody User subuserEmailCheck(@PathVariable String usertype,@PathVariable String id,@RequestParam(value = "emailId", required = true) String emailId) throws Exception {
		final String method = "subuserEmailCheck::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findByEmailIgnoreCase(emailId);
		User userById = userService.findById(id);
		if(emailId.equalsIgnoreCase(userById.getEmail())){
			return user;
		}
		List<User> users = userService.getAllUsersData(usertype,emailId);
		
		if(isNotEmpty(users)){
			return null;
		}
		return user;
	}
	
	@RequestMapping(value = "/delinkUser/{userid}/{email}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody void delinkUser(@PathVariable("userid") String userid,@PathVariable("email") String email,@PathVariable("clientid") String clientid,
			ModelMap model) throws Exception {
		final String method = "deleteProduct::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.delinkUser(userid,email,clientid);
	}
	
	@RequestMapping(value = "/deleteAdminUser/{userid}", method = RequestMethod.GET)
	public @ResponseBody void deleteAdminUser(@PathVariable("userid") String userid,
			ModelMap model) throws Exception {
		final String method = "deleteProduct::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteAdminUser(userid);
		
		
		//profileService.delinkUser(userid,email,clientid);
	}
	
	@RequestMapping(value = "/adminsales_cp_users/{id}", method = RequestMethod.GET)
	public @ResponseBody List<CompanyUser> getAllUserProfileById(ModelMap model, @PathVariable("id") String id) {
		User user = userService.findById(id);
		
		return profileService.getAllUsersByUserid(id);
	}
	
	@RequestMapping(value = "/cp_ClientsReportsGroupData/{returntype}/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String userClientReportsGroupDatasales(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable  String returntype,
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "profileCenterClients::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		
		List<Client> listOfClients= clientService.findByUserid(id);
		User user=userService.findById(id);
		List<Branch> branches=new ArrayList<>();
		List<Vertical> verticals=new ArrayList<>();
		
		listOfClients.stream().forEach(clnt->{
			if(isNotEmpty(clnt.getBranches())) {
				clnt.getBranches().stream().forEach(b->{
					branches.add(b);
				});
			}
			
		});
		
		listOfClients.stream().forEach(clnt->{
			if(isNotEmpty(clnt.getVerticals())) {
				clnt.getVerticals().stream().forEach(v->{
					verticals.add(v);
				});
			}
		});
		
		model.addAttribute("user", user);
		model.addAttribute("listOfClients", listOfClients);
		model.addAttribute("userLst", profileService.getAllUsersByUserid(id));
		model.addAttribute("branches", branches);
		model.addAttribute("verticals", verticals);
		
		if(returntype.equalsIgnoreCase("Sales")) {
			return "profile/userClientGroupReportssales";
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
			return "profile/userClientGroupReportsGSTR1";
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2)) {
			return "profile/userClientGroupReportspurchase";
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR3B)) {
			return "profile/userClientGroupReportsgstr3b";
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2B)){
				return "profile/userClientGroupReportsGSTR2B";
		}else {
			return "profile/userClientGroupReportsGSTR2A";
		}
	}
	
	@RequestMapping(value = "/getclientdata/{groupsnamelist}/{id}", method = RequestMethod.GET)
	public @ResponseBody List<Client> getClientData(@PathVariable("groupsnamelist") List<String> groupnames,@PathVariable String id, ModelMap model) throws Exception {
		final String method = "getClientData::";
		logger.debug(CLASSNAME + method + BEGIN);
		/*
		 * for(String s:groupnames) { System.out.println(s);}
		 */	
		List<Client> clntLst=new ArrayList<Client>();
		List<Client> clientsdata= clientService.getClientDataByGroupName(groupnames);
		
		clientsdata.stream().forEach(c->c.setFirstname(c.getId().toString()));
		Map<String,Client> userClients=clientsdata.stream().collect(Collectors.toMap(clnt->clnt.getId().toString(),Function.identity()));
		List<ClientUserMapping> mappings = clientUserMappingRepository.findByUserid(id);
		
		for(ClientUserMapping mapping:mappings) {
			if(isNotEmpty(userClients.get(mapping.getClientid()))) {
				Client clnt=userClients.get(mapping.getClientid());
				clntLst.add(clnt);
			}
		}
		//model.addAttribute("listOfClients", listOfClients);
		return clntLst;
	}
	
	@RequestMapping(value = "/getclientsdata/{id}", method = RequestMethod.GET)
	public @ResponseBody List<Client> getClientsData(@PathVariable("id") String id, ModelMap model) throws Exception {
		final String method = "getClientsData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		
		List<Client> listOfClients= clientService.findByUserid(id);
		listOfClients.stream().forEach(c->c.setFirstname(c.getId().toString()));
		return listOfClients;
	}
	
	@RequestMapping(value="/subbranchesexits/{clientid}",method=RequestMethod.GET)
	public @ResponseBody boolean subbranchesexits(@PathVariable String clientid,@RequestParam String name,@RequestParam String address) {
		Client client=clientService.findById(clientid);
		List<Branch> branches =client.getBranches();
		List<Branch> sbranches = Lists.newArrayList();
		boolean sbranchexists = false;
		for(int i=0;i<branches.size();i++) {
			//sbranches = branches.get(i).getSubbranches();
			sbranches.addAll( branches.get(i).getSubbranches());
		}
		for(int i=0;i<sbranches.size();i++) {
			String bname = sbranches.get(i).getName();
			String baddr = sbranches.get(i).getAddress();
			if(name.equals(bname) && address.equals(baddr)) {
				sbranchexists =  true;
				break;
		   }else {
			   sbranchexists = false;
		   }
		}
		return sbranchexists;
	}
	
	@RequestMapping(value="/subcostcenterexists/{clientid}",method=RequestMethod.GET)
	public @ResponseBody boolean subcostcenterexists(@PathVariable String clientid,@RequestParam String name) {
		Client client=clientService.findById(clientid);
		List<CostCenter> costcenter =client.getCostCenter();
		List<CostCenter> scostcenter = Lists.newArrayList();
		boolean scostcenterexists = false;
		for(int i=0;i<costcenter.size();i++) {
			scostcenter.addAll( costcenter.get(i).getSubcostcenter());
		}
		for(int i=0;i<scostcenter.size();i++) {
			String bname = scostcenter.get(i).getName();
			if(name.equals(bname)) {
				scostcenterexists =  true;
				break;
		   }else {
			   scostcenterexists = false;
		   }
		}
		return scostcenterexists;
	}
	
	@RequestMapping(value="/subverticalsexits/{clientid}",method=RequestMethod.GET)
	public @ResponseBody boolean subverticalsexits(@PathVariable String clientid,@RequestParam String name,@RequestParam String address) {
		Client client=clientService.findById(clientid);
		List<Vertical>  vertical=client.getVerticals();
		List<Vertical> sverticals = Lists.newArrayList();
		boolean sverticalsexist = false;
		for(int i=0;i<vertical.size();i++) {
			//sbranches = branches.get(i).getSubbranches();
			sverticals.addAll( vertical.get(i).getSubverticals());
		}
		for(int i=0;i<sverticals.size();i++) {
			String vname = sverticals.get(i).getName();
			String vaddr = sverticals.get(i).getAddress();
			if(name.equals(vname) && address.equals(vaddr)) {
				sverticalsexist =  true;
				break;
		   }else {
			   sverticalsexist = false;
		   }
		}
		return sverticalsexist;
	}
	@RequestMapping(value = "/delgroup/{id}", method = RequestMethod.GET)
	public @ResponseBody void deleteGroup(@PathVariable("id") String id, 
			ModelMap model) throws Exception {
		final String method = "deleteGroup::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteGroup(id);
	}
	@RequestMapping(value = "/delSubGroup/{subGroupId}/{groupid}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody void deleteSubGroupDetails(@PathVariable("subGroupId") String subGroupId,@PathVariable("groupid") String groupid,@PathVariable("clientid") String clientid, 
			ModelMap model) throws Exception {
		final String method = "deleteSubGroupDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteSubGroup(subGroupId, groupid, clientid);
	}
	@RequestMapping(value = "/delledger/{ledgerid}", method = RequestMethod.GET)
	public @ResponseBody void deleteLedger(@PathVariable("ledgerid") String ledgerid, 
			ModelMap model) throws Exception {
		final String method = "deleteLedger::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteLedger(ledgerid);
	}
	
	@RequestMapping(value = "/groupnameexits/{clientid}/{groupname}", method = RequestMethod.GET)
	public @ResponseBody String groupnameExists(@PathVariable String clientid, 
			@PathVariable String groupname, ModelMap model) throws Exception {
		final String method = "groupnameexists::";
		logger.debug(CLASSNAME + method + BEGIN);
		GroupDetails group=profileService.groupnameExists(clientid,groupname);
		
		if(isNotEmpty(group)) {
			return "success";
		}
		return "groupnamenot-exists";
	}
	
	@RequestMapping(value = "/subgroupnameexits/{clientid}/{groupname}", method = RequestMethod.GET)
	public @ResponseBody String subGroupNameExists(@PathVariable String clientid, 
			@PathVariable String groupname, ModelMap model) throws Exception {
		final String method = "groupnameexists::";
		logger.debug(CLASSNAME + method + BEGIN);
		GroupDetails group=profileService.subgroupnameExists(clientid,groupname);
		
		if(isNotEmpty(group)) {
			return "success";
		}
		return "groupnamenot-exists";
	}
	
	@RequestMapping(value = "/ledgernameexits/{clientid}/{ledgername}", method = RequestMethod.GET)
	public @ResponseBody String ledgernameExists(@PathVariable String clientid, 
			@PathVariable String ledgername, ModelMap model) throws Exception {
		final String method = "ledgernameexists::";
		logger.debug(CLASSNAME + method + BEGIN);
		ProfileLedger ledger=profileService.ledgerNameExists(clientid,ledgername);
		
		if(isNotEmpty(ledger)) {
			return "success";
		}
		return "ledgernamenot-exists";
	}
	
	@RequestMapping(value = "/cp_savegroupdetails/{id}/{clientid}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String addgroup(@ModelAttribute("groupdetails") GroupDetails  groupdetails, @PathVariable("id") String id,@PathVariable("clientid") String clientid,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, @RequestParam("elemId") String elemId, @RequestParam("type") String type,
			@RequestParam("parentId") String parentId, 	@RequestParam("subgroupid") String subgroupid, @RequestParam("pathname") String pathname, @RequestParam("groupname") String groupname,@RequestParam("headname") String headname,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addgroup::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if(NullUtil.isNotEmpty(groupdetails.getId()) 
				&& new ObjectId(id).equals(groupdetails.getId())) {
			groupdetails.setId(null);
		}
		groupdetails.setClientid(clientid);
		if(isEmpty(elemId)) {
			profileService.addGroupInfo(clientid, groupname, headname, parentId,subgroupid,pathname);
			
		} else {
			profileService.updateGroupInfo(clientid, elemId, groupname, headname, parentId);
		}
		model.addAttribute("type", type);
		
		if(type.equals("Group") || type == "") {
		return "redirect:/cp_Accounting/"+id+"/"+fullname+"/"+usertype+"/"+clientid + "/" + month + "/" + year+ "?type=group";
		}else {
			return "redirect:/cp_Accounting/"+id+"/"+fullname+"/"+usertype+"/"+clientid + "/" + month + "/" + year+ "?type=sgroup";
		}
	}
	
	
	
	@RequestMapping(value = "/groupslist/{clientid}")
	public @ResponseBody List<GroupDetails> getGroupsList(@PathVariable("clientid") String clientid) {
		
	
		List<GroupDetails> grpdtls = profileService.getGroupDetails(clientid);
		
		List<GroupDetails> sgrpdtls = Lists.newArrayList();
		for(GroupDetails group : grpdtls) {
			group.setName(group.getId().toString());
			List<Subgroups> slist = group.getSubgroups(); 
			if(isNotEmpty(slist)) {
				for(Subgroups sgroup : slist) {
					GroupDetails grp = new GroupDetails();
					grp.setName(group.getId().toString());
					grp.setUserid(sgroup.getId().toString());
					grp.setGroupname(sgroup.getGroupname());
					grp.setHeadname(sgroup.getHeadname());
					grp.setPath(sgroup.getPath());
					sgrpdtls.add(grp);
				}
			}
		}
		
		if(isNotEmpty(sgrpdtls)) {
		for(GroupDetails group : sgrpdtls) {
			GroupDetails grp = new GroupDetails();
			grp.setGroupname(group.getGroupname());
			grp.setHeadname(group.getHeadname());
			grp.setName(group.getName());
			grp.setUserid(group.getUserid());
			grp.setPath(group.getPath());
			grpdtls.add(grp);
		}
		}
		return grpdtls;
	}
	
	@RequestMapping(value = "/headslist/{clientid}")
	public @ResponseBody List<AccountingHeads> getGroupHeadsList(@PathVariable("clientid") String clientid) {
		
		List<AccountingHeads> headsdtls = profileService.getHeadDetails(clientid);
		
		List<AccountingHeads> headdtls = Lists.newArrayList();
		for(AccountingHeads mainHeads : headsdtls) {
			List<AccountingHeads> slist = mainHeads.getHeads();
			if(isNotEmpty(slist)) {
				for(AccountingHeads head : slist) {
					if(isNotEmpty(head) && (isNotEmpty(head.getHeadname()) && isNotEmpty(head.getName()))) {
						head.setHeads(null);
						headdtls.add(head);						
					}
				}
			}
		}
		
		return headdtls;
	}
	
	
	@RequestMapping(value = "/ledgerlist/{clientid}")
	public @ResponseBody List<ProfileLedger> getLedgerList(@PathVariable("clientid") String clientid) {
		
		List<ProfileLedger> ledgerList = 	profileService.getLedgerDetails(clientid);
		
		return ledgerList;
	}
	
	@RequestMapping(value = "/contraledgerlist/{clientid}")
	public @ResponseBody List<ProfileLedger> getContraLedgerList(@PathVariable("clientid") String clientid) {
		List<String> groupsubgroupnames = Lists.newArrayList();
		groupsubgroupnames.add("Cash in hand");
		groupsubgroupnames.add("Bank accounts");
		List<ProfileLedger> ledgerList = 	profileService.getLedgerDetails(clientid,groupsubgroupnames);
		
		return ledgerList;
	}
	
	@RequestMapping(value = "/cp_Accounting/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String accountingDetails(@ModelAttribute("groupdetails") GroupDetails  groupdetails, @PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year,@RequestParam("type") String type, ModelMap model)
			throws Exception {
		final String method = "accountingDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
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
		
		if (NullUtil.isNotEmpty(clientid)) {
			Client client = clientService.findById(clientid);
			model.addAttribute("client", client);
			
			if(isNotEmpty(client.getJournalEnteringDate())) {
				SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
				model.addAttribute("ledgerOpeningBalanceMonth", sdf.format(client.getJournalEnteringDate()));
			}else {
				SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
				
				String[] clyear=sdf.format(client.getCreatedDate()).split("-");
				
				model.addAttribute("ledgerOpeningBalanceMonth", "01-04-"+clyear[2]);
			}
			
			model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		}
		
		
		
		/*if (NullUtil.isNotEmpty(clientid)) {
			Client client = clientService.findById(clientid);
			model.addAttribute("client", client);
			if(isNotEmpty(client.getJournalEnteringDate())) {
				SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
				model.addAttribute("ledgerOpeningBalanceMonth", sdf.format(client.getJournalEnteringDate()));
			}else {
				SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
				
				String[] clyear=sdf.format(client.getCreatedDate()).split("-");
				
				model.addAttribute("ledgerOpeningBalanceMonth", "01-04-"+clyear[2]);
			}
			
			model.addAttribute("client", client);
			model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
			
			if(isEmpty(client.getIsgroupexist()) || !client.getIsgroupexist()) {
				List<GroupDetails> groupdtls = Lists.newArrayList();
				List<Groups> defgrps = groupsRepository.findAll();
				for(Groups grps : defgrps) {
					GroupDetails grpdetails = new GroupDetails();
					grpdetails.setClientid(client.getId().toString());
					grpdetails.setGroupname(grps.getName());
					grpdetails.setHeadname(grps.getHeadname());
					
					String grppath=grps.getHeadname()+"/"+grps.getName();
					grpdetails.setPath(grppath);
					//grpdetails.setPath(grps.getPath());
					List<Subgroups> subgroup = Lists.newArrayList();
					if(isNotEmpty(grps.getSubgroup())) {
						for(Subgroup subgrp : grps.getSubgroup()) {
							if(isNotEmpty(subgrp) && isNotEmpty(subgrp.getSubgroupname())) {
								Subgroups subgrps = new Subgroups();
								subgrps.setGroupname(subgrp.getSubgroupname());
								subgrps.setHeadname(grpdetails.getGroupname());
								subgrps.setPath(grpdetails.getPath()+"/"+subgrp.getSubgroupname());
								subgrps.setGroupid(grpdetails.getId().toString());
								subgroup.add(subgrps);
							}
						}
					}
					grpdetails.setSubgroups(subgroup);
					groupdtls.add(grpdetails);
				}
				groupDetailsRepository.save(groupdtls);
				
				List<ProfileLedger> ledger = Lists.newArrayList();
				ProfileLedger lgrdr = new ProfileLedger();
				lgrdr.setClientid(clientid);	
				lgrdr.setLedgerName(AccountConstants.OTHER_DEBTORS);
				lgrdr.setGrpsubgrpName(AccountConstants.SUNDRY_DEBTORS);
				lgrdr.setLedgerOpeningBalance(0);
				lgrdr.setLedgerpath("Assets/Current Assets/Sundry Debtors");
				ledger.add(lgrdr);
				ProfileLedger lgrcr = new ProfileLedger();
				lgrcr.setClientid(clientid);	
				lgrcr.setLedgerName(AccountConstants.OTHER_CREDITORS);
				lgrcr.setGrpsubgrpName(AccountConstants.SUNDRY_CREDITORS);
				lgrcr.setLedgerpath("Assets/Current Assets/Sundry Creditors");
				lgrcr.setLedgerOpeningBalance(0);		
				ledger.add(lgrcr);
			
				ledgerRepository.save(ledger);
				client.setIsgroupexist(true);
				clientService.saveClient(client);
			}
			if(isEmpty(client.getIscommonbankledgerexist()) || !client.getIscommonbankledgerexist()) {
				List<ProfileLedger> ledger = Lists.newArrayList();
				ProfileLedger lgrbank = new ProfileLedger();
				lgrbank.setClientid(clientid);	
				lgrbank.setLedgerName("Bank");
				lgrbank.setGrpsubgrpName("Bank");
				lgrbank.setLedgerpath("Assets/Current Assets/Bank");
				ledger.add(lgrbank);
				
				ProfileLedger tdsit = new ProfileLedger();
				tdsit.setClientid(clientid);	
				tdsit.setLedgerName("TDS-IT");
				tdsit.setGrpsubgrpName("TDS Receivables");
				tdsit.setLedgerpath("Assets/Current Assets/Duties & Taxes - Assets/TDS Receivables");
				ledger.add(tdsit);
				
				ProfileLedger tdsgst = new ProfileLedger();
				tdsgst.setClientid(clientid);	
				tdsgst.setLedgerName("TDS-GST");
				tdsgst.setGrpsubgrpName("TDS Receivables");
				tdsgst.setLedgerpath("Assets/Current Assets/Duties & Taxes - Assets/TDS Receivables");
				ledger.add(tdsgst);
				
				ProfileLedger discount = new ProfileLedger();
				discount.setClientid(clientid);	
				discount.setLedgerName("Discount");
				discount.setGrpsubgrpName("Other expenses");
				discount.setLedgerpath("Expenditure/Indirect Expenses/Other expenses");
				ledger.add(discount);
				ledgerRepository.save(ledger);
				client.setIscommonbankledgerexist(true);
				clientService.saveClient(client);
			}
			if(isEmpty(client.getIscommonledgerexist()) || !client.getIscommonledgerexist()) {
				List<ProfileLedger> legerdtls = Lists.newArrayList();
				List<Commonledger> defledgers = commonLedgerRepository.findAll();
				
				for(Commonledger ldgr : defledgers) {
					ProfileLedger lgrdr = new ProfileLedger();
					lgrdr.setClientid(clientid);	
					lgrdr.setLedgerName(ldgr.getLedgerName());
					lgrdr.setGrpsubgrpName(ldgr.getGrpsubgrpName());
					lgrdr.setLedgerpath(ldgr.getLedgerpath());
					legerdtls.add(lgrdr);
				}
				
				List<CompanyCustomers> customers= profileService.getCustomers(clientid);
				if(isNotEmpty(customers)) {
					for(CompanyCustomers customer : customers) {
						
							ProfileLedger lgrdr = new ProfileLedger();
							lgrdr.setClientid(clientid);
							if(isNotEmpty(customer.getCustomerLedgerName())) {
								lgrdr.setLedgerName(customer.getCustomerLedgerName());
							}else {
								lgrdr.setLedgerName(customer.getName());
							}
							lgrdr.setGrpsubgrpName("Sundry Debtors");
							lgrdr.setLedgerpath("Assets/Current Assets/Sundry Debtors");
							lgrdr.setLedgerOpeningBalance(0);
							legerdtls.add(lgrdr);
						
					}
				}
				List<CompanySuppliers> suppliers= profileService.getSuppliers(clientid);
				if(isNotEmpty(suppliers)) {
					for(CompanySuppliers supplier : suppliers) {
							ProfileLedger lgrdr = new ProfileLedger();
							lgrdr.setClientid(clientid);
							if(isNotEmpty(supplier.getSupplierLedgerName())) {
								lgrdr.setLedgerName(supplier.getSupplierLedgerName());
							}else {
								lgrdr.setLedgerName(supplier.getName());
							}
							lgrdr.setGrpsubgrpName("Sundry Creditors");
							lgrdr.setLedgerpath("Liabilities/Current liabilities/Sundry Creditors");
							lgrdr.setLedgerOpeningBalance(0);
							legerdtls.add(lgrdr);
					}
				}
				
				ledgerRepository.save(legerdtls);
				client.setIscommonledgerexist(true);
				clientService.saveClient(client);
			}
			
		}*/
		
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		
		model.addAttribute("type", type);
		model.addAttribute("accountingdetails", profileService.getGroupDetails(clientid));
		model.addAttribute("ledgerdetails", profileService.getLedgerDetails(clientid));
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		return "profile/accountingdetails";
	}
	
	@RequestMapping(value = "/cp_saveledgerdetails/{id}/{clientid}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String addLedger(@ModelAttribute("ledgerdetails") ProfileLedger  ledgerdetails,@RequestParam String oldledgername,
			@PathVariable("id") String id,@PathVariable("clientid") String clientid,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addgroup::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		
		Client client=clientService.findById(clientid);
		
		//ledgertype type eq to edit
		ProfileLedger existLedger= profileService.ledgerNameExists(clientid, oldledgername);
		if(isNotEmpty(existLedger)) {
			existLedger.setLedgerName(ledgerdetails.getLedgerName());
			existLedger.setLedgerpath(ledgerdetails.getLedgerpath());
			existLedger.setGrpsubgrpName(ledgerdetails.getGrpsubgrpName());
			if(isNotEmpty(client) && isNotEmpty(client.getJournalEnteringDate())) {
				SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
				String[] dateArr=sdf.format(client.getJournalEnteringDate()).split("-");
				if(isNotEmpty(dateArr) && dateArr.length>=2) {
					
					int mnth=Integer.parseInt(dateArr[1]);
					String yr=dateArr[2];
					
					String monthYear= mnth>=10?mnth+yr:"0"+mnth+yr;
					existLedger.setLedgerOpeningBalanceMonth(monthYear);	
				}
			}
			if(isNotEmpty(ledgerdetails.getLedgerOpeningBalanceType())) {
				existLedger.setLedgerOpeningBalanceType(ledgerdetails.getLedgerOpeningBalanceType());				
			}
			if(isNotEmpty(ledgerdetails.getLedgerOpeningBalance())) {
				existLedger.setLedgerOpeningBalance(ledgerdetails.getLedgerOpeningBalance());				
			}
			
			profileService.saveLegerDetails(existLedger);
		}else {
			if(NullUtil.isNotEmpty(ledgerdetails.getId())
					&& new ObjectId(id).equals(ledgerdetails.getId())) {
				ledgerdetails.setId(null);
			}
			if(isNotEmpty(client) && isNotEmpty(client.getJournalEnteringDate())) {
				SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
				String[] dateArr=sdf.format(client.getJournalEnteringDate()).split("-");
				if(isNotEmpty(dateArr) && dateArr.length>=2) {
					int mnth=Integer.parseInt(dateArr[1]);
					String yr=dateArr[2];
					
					String monthYear= mnth>=10?mnth+yr:"0"+mnth+yr;
					ledgerdetails.setLedgerOpeningBalanceMonth(monthYear);	
				}
			}
			profileService.saveLegerDetails(ledgerdetails);
		}
		return "redirect:/cp_Accounting/"+id+"/"+fullname+"/"+usertype+"/"+clientid + "/" + month + "/" + year+ "?type=ledger";
	}
	
	@RequestMapping(value = "/cp_addsaveledgerdetails/{id}/{clientid}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addinvLedger(@ModelAttribute("ledgerdetails") ProfileLedger  ledgerdetails, @PathVariable("id") String id,@PathVariable("clientid") String clientid,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addgroup::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if(NullUtil.isNotEmpty(ledgerdetails.getId()) 
				&& new ObjectId(id).equals(ledgerdetails.getId())) {
			ledgerdetails.setId(null);
		}
		Client client=clientService.findById(clientid);
		
		if(isNotEmpty(client) && isNotEmpty(client.getJournalEnteringDate())) {
			SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
			String[] dateArr=sdf.format(client.getJournalEnteringDate()).split("-");
			if(isNotEmpty(dateArr) && dateArr.length>=2) {
				int mnth=Integer.parseInt(dateArr[1]);
				String yr=dateArr[2];
				
				String monthYear= mnth>=10?mnth+yr:"0"+mnth+yr;
				ledgerdetails.setLedgerOpeningBalanceMonth(monthYear);	
			}
		}
		profileService.saveLegerDetails(ledgerdetails);
		return "redirect:/cp_Accounting/"+id+"/"+fullname+"/"+usertype+"/"+clientid + "/" + month + "/" + year+ "?type=ledger";
	}
	
	
	@RequestMapping(value = "/addturnover/{userid}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addturnover(@PathVariable("userid") String userid,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,
			@RequestParam("clientid")String clientid, 
			@RequestParam("turnover")Double turnover,@RequestParam("financialyear")String financialyear) {
		final String method = "addturnover::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + userid);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, userid, fullname, usertype, month, year);
		Client dbClient = clientService.findById(clientid);
		List<TurnoverOptions> turnoveroptionLst=new ArrayList<TurnoverOptions>();
		Set<String> set=new HashSet<String>();
		if(isNotEmpty(dbClient.getTurnovergoptions())) {
			dbClient.getTurnovergoptions().forEach(optn->{
				set.add(optn.getYear());
			});
			if(set.contains(financialyear)){
				for(TurnoverOptions option:dbClient.getTurnovergoptions()) {
					if(financialyear.equals(option.getYear())){
						option.setTurnover(turnover);
						turnoveroptionLst.add(option);
					}else {
						turnoveroptionLst.add(option);
					}
				}
			}else {
				turnoveroptionLst.add(new TurnoverOptions(new ObjectId(), financialyear, turnover));
				for(TurnoverOptions option:dbClient.getTurnovergoptions()) {
					if(financialyear.equals(option.getYear())){
						option.setTurnover(turnover);
						turnoveroptionLst.add(option);
					}else {
						turnoveroptionLst.add(option);
					}
				}
			}
		}else {
			turnoveroptionLst.add(new TurnoverOptions(new ObjectId(), financialyear, turnover));			
		}
		dbClient.setTurnovergoptions(turnoveroptionLst);
		clientService.saveClient(dbClient);

		logger.debug(CLASSNAME + method + END);
		return "redirect:/about/" + userid + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year;
	}
	
	@RequestMapping(value = "/cp_ecommerce/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String EcommerceOperator(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "EcommerceOperator::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		Client client = clientService.findById(clientid);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			model.addAttribute("companyUser", companyUser);
			if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
				model.addAttribute("addClient", companyUser.getAddclient());
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		List<EcommerceOpeartor> eoperator = profileService.getOperators(clientid);
		model.addAttribute("eoperator", eoperator);
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		
		logger.debug(CLASSNAME + method + END);
		return "profile/ecommerce_operator";
	}
	
	@RequestMapping(value = "/cp_createeoperator/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String addEOperator(@ModelAttribute("operator") EcommerceOpeartor operator,
			@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addEOperator::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		
		if(NullUtil.isNotEmpty(operator.getId()) && new ObjectId(id).equals(operator.getId())) {
			operator.setId(null);
		}
		
		profileService.saveOperator(operator);
		logger.debug(CLASSNAME + method + END);
		return "redirect:/cp_ecommerce/"+id+"/"+fullname+"/"+usertype+"/"+operator.getClientid() + "/" + month + "/" + year;
	}
	
	@RequestMapping(value = "/deloperator/{operatorid}", method = RequestMethod.GET)
	public @ResponseBody void deleteOperator(@PathVariable("operatorid") String operatorid, 
			ModelMap model) throws Exception {
		final String method = "deleteOperator::";
		logger.debug(CLASSNAME + method + BEGIN);
		profileService.deleteOperator(operatorid);
	}
	
	@RequestMapping(value = "/updtusrjournalexcel/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updateUserJournalExcelAccess(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updateUserJournalExcelAccess::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessJournalExcel(flag);
			userService.updateUser(user);
		}
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessJournalExcel(flag);
					userRepository.save(usr);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/updtusrgstr9access/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updateUserGSTR9Access(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updateUserGSTR9Access::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessGstr9(flag);
			userService.updateUser(user);
		}
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessGstr9(flag);
					userRepository.save(usr);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/updtusrDwnldewabillinvaccess/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updateUserDwnldewabillinvAccess(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updateUserDwnldewabillinvAccess::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessDwnldewabillinv(flag);
			userService.updateUser(user);
		}
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessDwnldewabillinv(flag);
					userRepository.save(usr);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/updtusrmultiGstnSearchaccess/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updateMultiGSTNAccess(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updateMultiGSTNAcces::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessMultiGSTNSearch(flag);
			userService.updateUser(user);
		}
		
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessMultiGSTNSearch(flag);
					userRepository.save(usr);
				}
			}
		}
		
		
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/updtusrReminders/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updateRemindersAccess(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updateRemindersAccess::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessReminders(flag);
			userService.updateUser(user);
		}
		
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessReminders(flag);
					userRepository.save(usr);
				}
			}
		}
		
		
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/updtusrANX1/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updateANX1Access(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updateANX1Access::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessANX1(flag);
			userService.updateUser(user);
		}
		
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessANX1(flag);
					userRepository.save(usr);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/updtusrANX2/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updateANX2Access(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updateANX2Access::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessANX2(flag);
			userService.updateUser(user);
		}
		
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessANX2(flag);
					userRepository.save(usr);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/updtusrEinvoice/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updateEinvoiceAccess(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updateEinvoiceAccess::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessEinvoice(flag);
			userService.updateUser(user);
		}
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessEinvoice(flag);
					userRepository.save(usr);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/updtusrCustomFieldTemplate/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updtusrCustomFieldTemplateAccess(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updtusrCustomFieldTemplateAccess::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessCustomFieldTemplate(flag);
			userService.updateUser(user);
		}
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessCustomFieldTemplate(flag);
					userRepository.save(usr);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/updtusrEntertaimentEinvoiceTemplate/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updtusrEntertaimentEinvoiceTemplateAccess(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updtusrCustomFieldTemplateAccess::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessEntertainmentEinvoiceTemplate(flag);
			userService.updateUser(user);
		}
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessEntertainmentEinvoiceTemplate(flag);
					userRepository.save(usr);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	@RequestMapping(value = "/updtusrAcknowledgement/{userid}", method = RequestMethod.GET)
	public @ResponseBody void updtusrAcknowledgement(@PathVariable("userid") String userid,
			@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		final String method = "updtusrAcknowledgement::";
		logger.debug(CLASSNAME + method + BEGIN);
		User user = userService.findById(userid);
		if (isNotEmpty(user)) {
			user.setAccessAcknowledgement(flag);
			userService.updateUser(user);
		}
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		for(CompanyUser cusr : users) {
			if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
				User usr = userRepository.findByEmail(cusr.getEmail());
				if(isNotEmpty(usr)) {
					usr.setAccessAcknowledgement(flag);
					userRepository.save(usr);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
	}
	@RequestMapping(value = "/eccommercegstins", method = RequestMethod.GET)
	public @ResponseBody List<EcommerceOpeartor> eccommersData(ModelMap model,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "clientid", required = true) String clientid) throws Exception {
		final String method = "eccommersData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + query);
		List<EcommerceOpeartor> eoperator = profileService.getSearchedOperators(clientid,query);
		return eoperator;
	}
	
	
	  @RequestMapping(value = "/saveInvoiceCustomFields/{clientid}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	  public @ResponseBody void saveInvoiceCustomFields(@RequestBody  MultiValueMap<String, String> formData,  @PathVariable("clientid") String clientid) { 
		  final String method =  "saveInvoiceCustomFields::"; 
		  logger.debug(CLASSNAME + method + BEGIN);
	  Client client = clientService.findById(clientid); 
	  InvoiceCutomFields customField1 = new InvoiceCutomFields(); 
	  InvoiceCutomFields customField2 = new InvoiceCutomFields(); 
	  InvoiceCutomFields customField3 = new InvoiceCutomFields(); 
	  InvoiceCutomFields customField4 = new InvoiceCutomFields();
	  
	  String dInSales1 = formData.getFirst("sacustomfiled1"); 
	  boolean displayInSlaes1; 
	  if("true".equals(dInSales1)) {
		  displayInSlaes1 = true; 
	  }else { 
		  displayInSlaes1 = false; 
	  } 
	  customField1.setDisplayInSales(displayInSlaes1);
	  String dInPurchase1 = formData.getFirst("pucustomfiled1"); 
	  boolean displayInPurchases1; 
	  if("true".equals(dInPurchase1)) { 
		  displayInPurchases1 = true; 
	  }else { 
		  displayInPurchases1 = false; 
	  }
	  customField1.setDisplayInPurchases(displayInPurchases1); 
	  String dInPrint1 = formData.getFirst("pcustomfiled1"); 
	  boolean displayInPrint1;
	  if("true".equals(dInPrint1)) { 
		  displayInPrint1 = true; 
	  }else { 
		  displayInPrint1 = false; 
	  } 
	  customField1.setDisplayInPrint(displayInPrint1);
	  customField1.setNameInSales(formData.getFirst("clabelinput1"));
	  customField1.setNameInPurchase(formData.getFirst("customlabel1"));
	  client.setCustomField1(customField1);
	  
	  String dInSales2 = formData.getFirst("sacustomfiled2"); 
	  boolean displayInSlaes2; 
	  if("true".equals(dInSales2)) { 
		  displayInSlaes2 = true; 
	  }else { 
		  displayInSlaes2 = false; 
	  } 
	  customField2.setDisplayInSales(displayInSlaes2);
	  String dInPurchase2 = formData.getFirst("pucustomfiled2"); 
	  boolean displayInPurchases2; 
	  if("true".equals(dInPurchase2)) { 
		  displayInPurchases2 = true; 
	  }else { 
		  displayInPurchases2 = false; }
		customField2.setDisplayInPurchases(displayInPurchases2);
		String dInPrint2 = formData.getFirst("pcustomfiled2");
		boolean displayInPrint2;
		if ("true".equals(dInPrint2)) {
			displayInPrint2 = true;
		} else {
			displayInPrint2 = false;
		}
		customField2.setDisplayInPrint(displayInPrint2);
		customField2.setNameInSales(formData.getFirst("clabelinput2"));
		customField2.setNameInPurchase(formData.getFirst("customlabel2"));
		client.setCustomField2(customField2);

		String dInSales3 = formData.getFirst("sacustomfiled3");
		boolean displayInSlaes3;
		if ("true".equals(dInSales3)) {
			displayInSlaes3 = true;
		} else {
			displayInSlaes3 = false;
		}
		customField3.setDisplayInSales(displayInSlaes3);
		String dInPurchase3 = formData.getFirst("pucustomfiled3");
		boolean displayInPurchases3;
		if ("true".equals(dInPurchase3)) {
			displayInPurchases3 = true;
		} else {
			displayInPurchases3 = false;
		}
		customField3.setDisplayInPurchases(displayInPurchases3);
		String dInPrint3 = formData.getFirst("pcustomfiled3");
		boolean displayInPrint3;
		if ("true".equals(dInPrint3)) {
			displayInPrint3 = true;
		} else {
			displayInPrint3 = false;
		}
		customField3.setDisplayInPrint(displayInPrint3);
		customField3.setNameInSales(formData.getFirst("clabelinput3"));
		customField3.setNameInPurchase(formData.getFirst("customlabel3"));
		client.setCustomField3(customField3);

		String dInSales4 = formData.getFirst("sacustomfiled4");
		boolean displayInSlaes4;
		if ("true".equals(dInSales4)) {
			displayInSlaes4 = true;
		} else {
			displayInSlaes4 = false;
		}
		customField4.setDisplayInSales(displayInSlaes4);
		String dInPurchase4 = formData.getFirst("pucustomfiled4");
		boolean displayInPurchases4;
		if ("true".equals(dInPurchase4)) {
			displayInPurchases4 = true;
		} else {
			displayInPurchases4 = false;
		}
		customField4.setDisplayInPurchases(displayInPurchases4);
		String dInPrint4 = formData.getFirst("pcustomfiled4");
		boolean displayInPrint4;
		if ("true".equals(dInPrint4)) {
			displayInPrint4 = true;
		} else {
			displayInPrint4 = false;
		}
		customField4.setDisplayInPrint(displayInPrint4);
		customField4.setNameInSales(formData.getFirst("clabelinput4"));
		customField4.setNameInPurchase(formData.getFirst("customlabel4"));
		client.setCustomField4(customField4);
	  
	 clientService.saveClient(client); 
	}
	 
	  @RequestMapping(value = "/cp_multiGtstin/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
		public String userMultiGSTINReport(@PathVariable("id") String id, @PathVariable("name") String fullname, 
				@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
				@PathVariable("year") int year, @RequestParam("type") String type,ModelMap model) throws Exception {
			final String method = "profileCenterClients::";
			logger.debug(CLASSNAME + method + BEGIN);
			logger.debug(CLASSNAME + method + "id\t" + id);
			logger.debug(CLASSNAME + method + "fullname\t" + fullname);
			updateModel(model, id, fullname, usertype, month, year);
			User usr = userRepository.findById(id);
			model.addAttribute("user",usr);
			if(type.equalsIgnoreCase("initial")) {
				multiGSTNDao.multigstdata(id);
			}
			//model.addAttribute("multigstin", profileService.getGstnum(id));
			//model.addAttribute("multigstinpublic", profileService.getpublicgstnum(id));
			return "profile/multiGstinReport";
		}
	@RequestMapping(value = "/cp_creategstins/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody String addGstnos(@ModelAttribute("multigstin") AllGSTINTempData gstno,
			@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, @PathVariable("year") int year,
			ModelMap model) throws Exception {

		List<String> gstnlist = new ArrayList<String>();
		gstnlist.addAll(gstno.getGstnos());
		User user = userService.findById(id);

		if (NullUtil.isNotEmpty(user.getParentid())) {
			gstno.setParentid(user.getParentid());
		}
		final String method = "addGstno::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		AllGSTINTempData tempgst = profileService.saveGstno(gstno);
		if(NullUtil.isNotEmpty(tempgst.getGstno())) {
			List<MultiGSTNData> gstndataa =  multiGSTnRepository.findByUseridAndGstin(id,tempgst.getGstno());
			if(NullUtil.isNotEmpty(gstndataa)) {
				multiGSTnRepository.delete(gstndataa);
			}
			
			MultiGSTNData gstndata = new MultiGSTNData();
			gstndata.setGstin(tempgst.getGstno());
			gstndata.setUserid(id);
			gstndata.setGstnid(tempgst.getId().toString());
			
			multiGSTnRepository.save(gstndata);
		}
		
		
		logger.debug(CLASSNAME + method + END);

		return "redirect:/cp_multiGtstin/" + id + "/" + fullname + "/" + usertype + "/" + month + "/" + year+"?type=multigstin";
	}

	@RequestMapping(value = "/cp_creategstin/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody String addGstno(@RequestParam(name = "gstno") String[] gstno, @PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {

		final String method = "addGstno::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + END);

		List<String> gstnset = new ArrayList<String>();

		if (isNotEmpty(gstno)) {

			for (String gstn : gstno) {

				if (isNotEmpty(gstn.trim())) {

					gstnset.add(gstn);
				}

			}
		}

		List<AllGSTINTempData> gstnos = profileService.getGstnum(id);
		Map<String, AllGSTINTempData> esitsGstnos = gstnos.stream()
				.collect(Collectors.toMap(usrgstn -> usrgstn.getGstno(), Function.identity(),(existing, replacement) -> existing));
		if (isNotEmpty(gstnset)) {
			for (String gstn : gstnset) {
				if(isNotEmpty(gstn)) {
				if (!esitsGstnos.containsKey(gstn)) {

					AllGSTINTempData data = new AllGSTINTempData();
					data.setUserid(id);
					User user = userService.findById(id);
					if (NullUtil.isNotEmpty(user.getParentid())) {
						data.setParentid(user.getParentid());
					}
					data.setGstno(gstn);
					AllGSTINTempData existGSTN = allgstnoTempRepository.findByUseridAndGstnoAndStatusNotNull(id,gstn);
        			AllGSTINTempData tempgst = null;
    				if(NullUtil.isNotEmpty(existGSTN)) {
    					
    					List<AllGSTINTempData> dupLst = allgstnoTempRepository.findByUseridAndGstnoAndStatusNull(id,gstn);
    					allgstnoTempRepository.delete(dupLst);
    					tempgst = profileService.saveGstno(existGSTN);
    				}else {
    					List<AllGSTINTempData> dupLst = allgstnoTempRepository.findByUseridAndGstnoAndStatusNull(id,gstn);
    					allgstnoTempRepository.delete(dupLst);
    					tempgst = profileService.saveGstno(data);
    				}
        			if(NullUtil.isNotEmpty(tempgst.getGstno())) {
        				
        				List<MultiGSTNData> gstndataa =  multiGSTnRepository.findByUseridAndGstin(id,tempgst.getGstno());
        				if(NullUtil.isNotEmpty(gstndataa)) {
        					multiGSTnRepository.delete(gstndataa);
        				}
        				
        				MultiGSTNData mgstndata = new MultiGSTNData();
        				mgstndata.setGstin(tempgst.getGstno());
        				mgstndata.setUserid(id);
        				mgstndata.setGstnid(tempgst.getId().toString());
        				
        				multiGSTnRepository.save(mgstndata);
        			}
				}
			}
			}
		}


		model.addAttribute("multigstin", profileService.getGstnum(id));

		return "";
	}

	@RequestMapping(value = "/multigstnuploadFile/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody void multiGstnFileUpload(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @RequestParam("file") MultipartFile file,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "multiGstnFileUpload::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (file != null && !file.isEmpty()) {
			logger.debug(CLASSNAME + method + file.getOriginalFilename());
			try {
				 profileService.updateMultiGstnoExcelData(file, id, fullname);
			} catch (Exception e) {
				logger.error(CLASSNAME + method + " ERROR", e);
				model.addAttribute("error", "Unable to import due to - " + e.getMessage());
			}
		} else {
			model.addAttribute("error", "Select the file to import");
		}
		
		//return "redirect:/cp_multiGtstin/" + id + "/" + fullname + "/" + usertype + "/" + month + "/" + year+"?type=multigstin";
	}
	
	@RequestMapping(value = "/validateSelectedgstnoss/{id}/{month}/{year}/{gstnum}", method = RequestMethod.GET)
	public @ResponseBody void validateSelectedgstno(@PathVariable("id") String id,
			 @PathVariable("month") int month,	@PathVariable("year") int year, @PathVariable("gstnum") List<String> gstnums, ModelMap model,HttpServletRequest request)
					throws Exception {
		final String method = "validateSelectedgstno::";
		logger.debug(CLASSNAME + method + BEGIN);
		for(String gstnum:gstnums) {
			if (isNotEmpty(gstnum) && Pattern.matches(gstnFormat, gstnum.trim())) {
				
				AllGSTINTempData existGSTN = allGSTnoTempRepository.findByUseridAndGstnoAndStatusNotNull(id,gstnum);
				
				if(NullUtil.isNotEmpty(existGSTN)) {
					
					List<AllGSTINTempData> dupLst = allGSTnoTempRepository.findByUseridAndGstnoAndStatusNull(id,gstnum);
					allGSTnoTempRepository.delete(dupLst);
				}else {
					Response response = iHubConsumerService.publicSearch(gstnum);
					if (isNotEmpty(response) && isNotEmpty(response.getData()) && isNotEmpty(response.getStatuscd())
							&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
						
						gstinPublicDataRepository.deleteByGstin(gstnum);
							
						GSTINPublicData publicData = new GSTINPublicData();
						BeanUtils.copyProperties(response.getData(), publicData);
						publicData.setCreatedDate(new Date());
						try {
							publicData.setIpAddress(InetAddress.getLocalHost().getHostAddress());
						} catch (UnknownHostException e) {
								
							e.printStackTrace();
						}
						publicData.setUserid(id);
						User user = userService.findById(id);
						if (NullUtil.isNotEmpty(user.getParentid())) {
							publicData.setParentid(user.getParentid());
						}
						publicData.setStatus("VALID");
						gstinPublicDataRepository.save(publicData);
							
						//AllGSTINTempData gstde = allGSTnoTempRepository.findByUseridAndGstno(id,gstnum);
						List<AllGSTINTempData> dupLst = allGSTnoTempRepository.findByUseridAndGstnoAndStatusNull(id,gstnum);
						
						if(isNotEmpty(dupLst)) {
							allGSTnoTempRepository.delete(dupLst);	
						}
					}else {
						AllGSTINTempData gstde = allGSTnoTempRepository.findByUseridAndGstno(id,gstnum);
						if(isNotEmpty(gstde)) {
							gstde.setStatus("Invalid GSTIN");
							profileService.saveGstno(gstde);
						}
						List<AllGSTINTempData> dupLst = allGSTnoTempRepository.findByUseridAndGstnoAndStatusNull(id,gstnum);
						
						if(isNotEmpty(dupLst)) {
							allGSTnoTempRepository.delete(dupLst);	
						}
					}
				}
			}else {
				AllGSTINTempData gstde = allGSTnoTempRepository.findByUseridAndGstno(id,gstnum);
				if(isNotEmpty(gstde)) {
					gstde.setStatus("Invalid GSTIN");
					profileService.saveGstno(gstde);
				}
				List<AllGSTINTempData> dupLst = allGSTnoTempRepository.findByUseridAndGstnoAndStatusNull(id,gstnum);
				allGSTnoTempRepository.delete(dupLst);
			}
		}
	}
	
	@RequestMapping(value = "/deleteSelectedgstnos/{id}/{month}/{year}/{dgstnum}", method = RequestMethod.GET)
	public @ResponseBody void deleteSelectedgstno(@PathVariable("id") String id,
			 @PathVariable("month") int month,	@PathVariable("year") int year, @PathVariable("dgstnum") String dgstnum, ModelMap model,HttpServletRequest request)
					throws Exception {
		final String method = "deleteSelectedgstno::";
		logger.debug(CLASSNAME + method + BEGIN);
		AllGSTINTempData gstde = allGSTnoTempRepository.findOne(dgstnum);	
		GSTINPublicData pgstde = gstinPublicDataRepository.findOne(dgstnum);
		MultiGSTNData multigst = multiGSTnRepository.findByGstnid(dgstnum);
		if(isNotEmpty(multigst)) {
			multiGSTnRepository.delete(multigst);
		}
		if(isNotEmpty(gstde)) {
		allGSTnoTempRepository.delete(gstde);
		}
		if(isNotEmpty(pgstde)) {
		//gstinPublicDataRepository.delete(pgstde);
			//gstinPublicDataRepository.delete(pgstde);
			pgstde.setParentid("");
			pgstde.setUserid("");
			List<GSTINPublicData> gstdata = gstinPublicDataRepository.findByUseridAndParentidAndGstin("", "", pgstde.getGstin());
			if(isEmpty(gstdata)) {
				gstinPublicDataRepository.save(pgstde);
			}else {
				logger.info(gstdata.size());
				gstinPublicDataRepository.delete(gstdata);
				gstinPublicDataRepository.save(pgstde);
			}
		}
	}
	
	@RequestMapping(value = "/deleteSelectedmultigstnos/{id}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody void deleteMultiSelectedgstno(@PathVariable("id") String id,
			 @PathVariable("month") int month,	@PathVariable("year") int year, @RequestBody List<MultiGstinVO> deletegstin, ModelMap model,HttpServletRequest request)
					throws Exception {
		final String method = "deleteSelectedgstno::";
		logger.debug(CLASSNAME + method + BEGIN);
		if(isNotEmpty(deletegstin)) {
			for(MultiGstinVO records : deletegstin) {
				if(isNotEmpty(records.getGstnid())) {
					List<AllGSTINTempData> alltempdata =  Lists.newArrayList(allGSTnoTempRepository.findAll(records.getGstnid()));
					allGSTnoTempRepository.delete(alltempdata);
				}
				if(isNotEmpty(records.getMultigstin())) {
					List<MultiGSTNData> alltempdata =  Lists.newArrayList(multiGSTnRepository.findAll(records.getMultigstin()));
					multiGSTnRepository.delete(alltempdata);
				}
				//MultiGSTNData multigst = multiGSTnRepository.findByGstnid(dgstnum);
			}
		}
		/*for(String dgstnum: deletegstin) {
		AllGSTINTempData gstde = allGSTnoTempRepository.findOne(dgstnum);	
		GSTINPublicData pgstde = gstinPublicDataRepository.findOne(dgstnum);
		MultiGSTNData multigst = multiGSTnRepository.findByGstnid(dgstnum);
		if(isNotEmpty(multigst)) {
			multiGSTnRepository.delete(multigst);
			
		}
		if(isNotEmpty(gstde)) {
		allGSTnoTempRepository.delete(gstde);
		}
		if(isNotEmpty(pgstde)) {
		//gstinPublicDataRepository.delete(pgstde);
			//gstinPublicDataRepository.delete(pgstde);
			pgstde.setParentid("");
			pgstde.setUserid("");
			List<GSTINPublicData> gstdata = gstinPublicDataRepository.findByUseridAndParentidAndGstin("", "", pgstde.getGstin());
			if(isEmpty(gstdata)) {
				gstinPublicDataRepository.save(pgstde);
			}else {
				logger.info(gstdata.size());
				gstinPublicDataRepository.delete(gstdata);
				gstinPublicDataRepository.save(pgstde);
			}
		}
		}*/
	}
	
	
	/*@RequestMapping(value = "/dwnldmultigstinsxls/{id}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource dgstno(@PathVariable("id") String id,
			 HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		User user = userService.findById(id);
		
		response.setHeader("Content-Disposition", "inline; filename=MGST_GSTnoTemplate.xls");
		List<GSTINPublicData> gstnolist = profileService.getpublicgstnum(id);
		List<AllGSTINTempData> tempgstnolist = profileService.getGstnum(id);
		List<GSTINPublicDataVO> multigstnoList = gstnoListItems(gstnolist, tempgstnolist);
		
		logger.debug(multigstnoList);
		File file = new File("MGST_MultiGSTnoTemplate.xls");
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers = null;
			
			headers = Arrays.asList("GSTIN","Company Name","Status","State Jurisdiction","Center Jurisdiction","Date Of Registration","Constitution of Business","TaxPayer Type","GSTIN / UIN Status","Date of Cancellation","Last Updated Date","State Jurisdiction Code","Center Jurisdiction Code","Registration Trade Name", "Flat No","Building No","Building Name","Street Name","Location","District","State","Longitude","Lattitude","Pin Code", "Adadr Flat No","Addr Building No","Adadr Building Name","Adadr Street Name","Adadr Location","Adadr District","Adadr State","Adadr Longitude","Adadr Lattitude","Adadr Pin Code");
			
			SimpleExporter exporter = new SimpleExporter();
			
			exporter.gridExport(headers, multigstnoList,
					"gstin, lgnm, status, stj, ctj, rgdt, ctb, dty, sts, cxdt, lstupdt, stjCd, ctjCd, tradeNam, flno, bno, bnm, st, loc, dst, stcd,lg, lt, pncd, aflno, abno, abnm, ast, aloc, adst, astcd,alg, alt, apncd",
					fos);
			
			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "downloadExcelData : ERROR", e);
		}
		return new FileSystemResource(new File("MGST_MultiGSTnoTemplate.xls"));
	}*/
	
	@RequestMapping(value = "/dwnldmultigstinsxls/{id}", method = RequestMethod.GET)
	public void dgstnoo(@PathVariable("id") String id, HttpServletResponse response, HttpServletRequest request) {
		//User user = userService.findById(id);
		String date = new SimpleDateFormat("dd-MM-yyyy:HH:mm:ss.sss").format(new Date());
		date = date.replaceAll(":", "-");
		List<GSTINPublicData> gstnolist = profileService.getpublicgstnum(id);
		List<AllGSTINTempData> tempgstnolist = profileService.getGstnum(id);
		List<GSTINPublicDataVO> multigstnoList = gstnoListItems(gstnolist, tempgstnolist);
		List<String> headers = null;
		
		headers = Arrays.asList("GSTIN","Company Name","Status","State Jurisdiction","Center Jurisdiction","Date Of Registration","Constitution of Business","TaxPayer Type","GSTIN / UIN Status","Date of Cancellation","Last Updated Date","State Jurisdiction Code","Center Jurisdiction Code","Registration Trade Name", "Flat No","Building No","Building Name","Street Name","Location","District","State","Longitude","Lattitude","Pin Code", "Adadr Flat No","Addr Building No","Adadr Building Name","Adadr Street Name","Adadr Location","Adadr District","Adadr State","Adadr Longitude","Adadr Lattitude","Adadr Pin Code");
		
		OutputStream nout = null;
		ZipOutputStream zipOutputStream = null;
		try {
			nout = response.getOutputStream();
			String fileName = "MGST_MultiGSTNo_"+date+".zip";
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
			response.setContentType("application/octet-stream; charset=utf-8");
			zipOutputStream = new ZipOutputStream(nout);
			byte[] buf = new byte[1024];
			int c=0;
			int len = 0;
			
			double i = ((double)multigstnoList.size())/60000;
			int j = (int)i;
			if(i-(int)i > 0) {
				j = (int)i+1;
			}
			List<List<GSTINPublicDataVO>> lt = Lists.newArrayList();
			int a=0;
			int b = 60000;
			if(multigstnoList.size() < 60000) {
				b= multigstnoList.size();
			}
			for(int k = 1; k <= j;k++) {
				lt.add(multigstnoList.subList(a, b));
				a = b;
				if(k == j-1) {
					b = multigstnoList.size();
				}else {
					b = b+60000;
				}
			}
			for(List<GSTINPublicDataVO> InvoicesList: lt) {
				File file1 = new File("MGST_MultiGSTno_"+date+"_"+(c+1)+".xls");
				file1.createNewFile();
				FileOutputStream fos = new FileOutputStream(file1);
				SimpleExporter exporter = new SimpleExporter();
				exporter.gridExport(headers, InvoicesList,"gstin, lgnm, status, stj, ctj, rgdt, ctb, dty, sts, cxdt, lstupdt, stjCd, ctjCd, tradeNam, flno, bno, bnm, st, loc, dst, stcd,lg, lt, pncd, aflno, abno, abnm, ast, aloc, adst, astcd,alg, alt, apncd",fos);
				String fname = file1.getName();
				FileInputStream fileInputStream = new FileInputStream(file1);
				zipOutputStream.putNextEntry(new ZipEntry(fname));
				while((len=fileInputStream.read(buf)) >0){
					zipOutputStream.write(buf, 0, len);
				}
				 				//shut down; 
				zipOutputStream.closeEntry();
				if(isNotEmpty(fileInputStream)){
					fileInputStream.close();
				}
				file1.delete();
				c++;
			}
			
		}catch (IOException e) {
			logger.error(CLASSNAME + "downloadMultiGstnoExcelData : ERROR", e);
		}finally {
			try {
				if (isNotEmpty(zipOutputStream)) {
					zipOutputStream.close();
				}	
				if (isNotEmpty(nout)) {
					nout.close();
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	public List<GSTINPublicDataVO> gstnoListItems(List<GSTINPublicData> gstnolist , List<AllGSTINTempData> tempgstnolist){
		List<GSTINPublicDataVO> multigstnoList = Lists.newArrayList();
		if(isNotEmpty(gstnolist)) {
			for(GSTINPublicData gstno : gstnolist) {
				GSTINPublicDataVO gstnodata = new GSTINPublicDataVO();
				if(isNotEmpty(gstno.getGstin())) {
					gstnodata.setGstin(gstno.getGstin());
				}
				if(isNotEmpty(gstno.getLgnm())) {
					gstnodata.setLgnm(gstno.getLgnm());
				}
				if(isNotEmpty(gstno.getDty())) {
					gstnodata.setDty(gstno.getDty());
				}
				if(isNotEmpty(gstno.getRgdt())) {
					gstnodata.setRgdt(gstno.getRgdt());
				}
				if(isNotEmpty(gstno.getLstupdt())) {
					gstnodata.setLstupdt(gstno.getLstupdt());
				}
				gstnodata.setStatus("VALID");
				if(isNotEmpty(gstno.getStj())) {
					gstnodata.setStj(gstno.getStj());
				}
				if(isNotEmpty(gstno.getCtj())) {
					gstnodata.setCtj(gstno.getCtj());
				}
				if(isNotEmpty(gstno.getCtb())) {
					gstnodata.setCtb(gstno.getCtb());
				}
				if(isNotEmpty(gstno.getSts())) {
					gstnodata.setSts(gstno.getSts());
				}
				if(isNotEmpty(gstno.getCxdt())) {
					gstnodata.setCxdt(gstno.getCxdt());
				}
				if(isNotEmpty(gstno.getStjCd())) {
					gstnodata.setStjCd(gstno.getStjCd());
				}
				if(isNotEmpty(gstno.getCtjCd())) {
					gstnodata.setCtjCd(gstno.getCtjCd());
				}
				if(isNotEmpty(gstno.getTradeNam())){
					gstnodata.setTradeNam(gstno.getTradeNam());
				}
				if(isNotEmpty(gstno.getPradr().getAddr().getBno())) {
					gstnodata.setBno(gstno.getPradr().getAddr().getBno());
					}
				if(isNotEmpty(gstno.getPradr().getAddr().getBnm())) {
					gstnodata.setBnm(gstno.getPradr().getAddr().getBnm());
				}
				if(isNotEmpty(gstno.getPradr().getAddr().getSt())) {
					gstnodata.setSt(gstno.getPradr().getAddr().getSt());
				}
				if(isNotEmpty(gstno.getPradr().getAddr().getLoc())) {
					gstnodata.setLoc(gstno.getPradr().getAddr().getLoc());
				}
				if(isNotEmpty(gstno.getPradr().getAddr().getStcd())) {
					gstnodata.setStcd(gstno.getPradr().getAddr().getStcd());
				}
				if(isNotEmpty(gstno.getPradr().getAddr().getPncd())) {
					gstnodata.setPncd(gstno.getPradr().getAddr().getPncd());
				}
				if(isNotEmpty(gstno.getPradr().getAddr().getFlno())) {
					gstnodata.setFlno(gstno.getPradr().getAddr().getFlno());
				}
				if(isNotEmpty(gstno.getPradr().getAddr().getDst())) {
					gstnodata.setDst(gstno.getPradr().getAddr().getDst());
				}
				if(isNotEmpty(gstno.getPradr().getAddr().getLt())) {
					gstnodata.setLt(gstno.getPradr().getAddr().getLt());
				}
				if(isNotEmpty(gstno.getPradr().getAddr().getLg())) {
					gstnodata.setLg(gstno.getPradr().getAddr().getLg());
				}
				
				if(isNotEmpty(gstno.getAdadr()) && isNotEmpty(gstno.getAdadr().get(0)) && isNotEmpty(gstno.getAdadr().get(0).getAddr())) {
					if(isNotEmpty(gstno.getAdadr().get(0).getAddr().getBno())) {
						gstnodata.setAbno(gstno.getAdadr().get(0).getAddr().getBno());
					}
					if(isNotEmpty(gstno.getAdadr().get(0).getAddr().getBnm())) {
						gstnodata.setAbnm(gstno.getAdadr().get(0).getAddr().getBnm());
					}
					if(isNotEmpty(gstno.getAdadr().get(0).getAddr().getSt())) {
						gstnodata.setAst(gstno.getAdadr().get(0).getAddr().getSt());
					}
					if(isNotEmpty(gstno.getAdadr().get(0).getAddr().getLoc())) {
						gstnodata.setAloc(gstno.getAdadr().get(0).getAddr().getLoc());
					}
					if(isNotEmpty(gstno.getAdadr().get(0).getAddr().getStcd())) {
						gstnodata.setAstcd(gstno.getAdadr().get(0).getAddr().getStcd());
					}
					if(isNotEmpty(gstno.getAdadr().get(0).getAddr().getPncd())) {
						gstnodata.setApncd(gstno.getAdadr().get(0).getAddr().getPncd());
					}
					if(isNotEmpty(gstno.getAdadr().get(0).getAddr().getFlno())) {
						gstnodata.setAflno(gstno.getAdadr().get(0).getAddr().getFlno());
					}
					if(isNotEmpty(gstno.getAdadr().get(0).getAddr().getDst())) {
						gstnodata.setAdst(gstno.getAdadr().get(0).getAddr().getDst());
					}
					if(isNotEmpty(gstno.getAdadr().get(0).getAddr().getLt())) {
						gstnodata.setAlt(gstno.getAdadr().get(0).getAddr().getLt());
					}
					if(isNotEmpty(gstno.getAdadr().get(0).getAddr().getLg())) {
						gstnodata.setAlg(gstno.getAdadr().get(0).getAddr().getLg());
					}
				}
				
				multigstnoList.add(gstnodata);
			}
		}
		if(isNotEmpty(tempgstnolist)) {
			for(AllGSTINTempData gstno : tempgstnolist) {
				GSTINPublicDataVO gstnodata = new GSTINPublicDataVO();
				if(isNotEmpty(gstno.getGstno())) {
					gstnodata.setGstin(gstno.getGstno());
				}
				if(isNotEmpty(gstno.getStatus())) {
					gstnodata.setStatus(gstno.getStatus());
				}
				multigstnoList.add(gstnodata);
			}
		}
		return multigstnoList;
		
	}
	
	@RequestMapping(value = "/multiGtstinDetails/{id}/{gstinid}", method = RequestMethod.GET)
	public @ResponseBody GSTINPublicData gstinDetails(@PathVariable("id") String id, @PathVariable("gstinid") String gstinid,
			ModelMap model) throws Exception {
		final String method = "profileCenterClients::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		return gstinPublicDataRepository.findOne(gstinid);
	}
	
	
	@CrossOrigin(origins = "*")
    @RequestMapping(value = "/updtclntUsermappingDetails", method = RequestMethod.GET)
    public @ResponseBody String updateClientUserMapping() {
		List<CompanyUser> companyUsers = companyUserRepository.findAll();
		for(CompanyUser companyUser : companyUsers) {
			if(isNotEmpty(companyUser)) {
			User user = userRepository.findByEmail(companyUser.getEmail());
			if(isNotEmpty(user)) {
				if(isNotEmpty(companyUser.getRole())) {
					String roleid = companyUser.getRole();
					List<String> clientids = companyUser.getCompany();
					if(isNotEmpty(clientids)) {
						for(String clientid : clientids) {
								ClientUserMapping clientuserMapping = clientUserMappingRepository.findByUseridAndClientid(user.getId().toString(), clientid);
								if(isNotEmpty(clientuserMapping)) {
									clientuserMapping.setRole(roleid);
									clientUserMappingRepository.save(clientuserMapping);
								}
							}
							
						}
					}
				}
			}
		}
    	return "Completed Successfully";
    }
	
	@CrossOrigin(origins = "*")
    @RequestMapping(value = "/updtclntgstr9", method = RequestMethod.GET)
    public @ResponseBody String updateGSTR9() {
		List<Client> clients = clientRepository.findAll();
		for(Client client: clients) {
			if(isNotEmpty(client.getReturnsSummary()) && client.getReturnsSummary().size()>0) {
				for(GSTReturnSummary gstReturnSummary: client.getReturnsSummary()) {
					if(gstReturnSummary.getReturntype().equalsIgnoreCase(MasterGSTConstants.GSTR9)) {
						gstReturnSummary.setActive("true");
					}
				}
			}
			clientRepository.save(client);
		}
    	return "Completed Successfully";
    }
	
	 @RequestMapping(value = "/cp_reminders/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
		public String clientReminders(@PathVariable("id") String id, @PathVariable("name") String fullname, 
				@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
				@PathVariable("year") int year, ModelMap model) throws Exception {
			final String method = "clientReminders::";
			logger.debug(CLASSNAME + method + BEGIN);
			logger.debug(CLASSNAME + method + "id\t" + id);
			logger.debug(CLASSNAME + method + "fullname\t" + fullname);
			updateModel(model, id, fullname, usertype, month, year);
			User user = userRepository.findOne(id);
			String usrDetails = "";
			if(isNotEmpty(user) && isNotEmpty(user.getUserSignatureDetails())) {
				usrDetails = user.getUserSignatureDetails();
				if(usrDetails.contains("\r")) {
					usrDetails = usrDetails.replaceAll("\r", "#mgst# ");
				}else if(usrDetails.contains("\n")) {
					usrDetails = usrDetails.replaceAll("\n", "#mgst# ");
				}else if(usrDetails.contains("\r\n")) {
					usrDetails = usrDetails.replaceAll("\r\n", "#mgst# ");
				}
				model.addAttribute("usersignature",usrDetails);
			}
			if(NullUtil.isNotEmpty(user)){
				model.addAttribute("email", user.getEmail());
			}
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
				CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
				model.addAttribute("companyUser", companyUser);
				if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
					model.addAttribute("addClient", companyUser.getAddclient());
				}else {
					model.addAttribute("addClient", "true");
				}
			}else {
				model.addAttribute("addClient", "true");
			}
			List<Client> lClient = clientService.findByUserid(id);
			if (isNotEmpty(lClient)) {
				model.addAttribute("centers", lClient);
			}
			model.addAttribute("user", user);
			return "profile/reminders";
		}

	 @RequestMapping(value = "/cp_remindersdata/{id}", method = RequestMethod.GET)
	 public @ResponseBody String getRemindersData(@PathVariable String id, @RequestParam String type, HttpServletRequest request) throws JsonProcessingException {
		 
		 Map<String,Object> dataMap=new HashMap<>();
		 User user = userRepository.findOne(id);
		 Client clint = null;
		 if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			 CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			 dataMap.put("companyUser", companyUser);
			 dataMap.put("user", user);
		 }
		 
		 String client_ids = request.getParameter("clientids");
			String[] clientids =null;
			
			if(client_ids !=null && !"".equals(client_ids.trim())) {
				clientids =client_ids.split(",");
			}		 
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		 
		if(type.equalsIgnoreCase("clients")) {
			Page<Client> clients =profileService.getRemindersClients(id, start, length, searchVal);
			if(isNotEmpty(clients)) {
				for(Client clnt:clients.getContent()){
					clnt.setDocId(clnt.getId().toString());
				}
			}
			dataMap.put("clientstab", clients);			 
		}else if(type.equalsIgnoreCase("customers")) {
			Page<CompanyCustomers> customers =profileService.getRemindersCustomers(id, clientids,start, length, searchVal);
			if(isNotEmpty(customers)) {
				for(CompanyCustomers customer:customers.getContent()){
					 clint = clientService.findById(customer.getClientid());
					if(isNotEmpty(clint) && isNotEmpty(clint.getBusinessname())) {
						customer.setContactperson(clint.getBusinessname());
					} 
					customer.setDocId(customer.getId().toString());
				}
			}
			dataMap.put("customerstab", customers);
		 }else if(type.equalsIgnoreCase("suppliers")) {
			Page<CompanySuppliers> suppliers =profileService.getRemindersSuppliers(id, clientids,start, length, searchVal);
			if(isNotEmpty(suppliers)) {
				for(CompanySuppliers supplier:suppliers.getContent()){
					clint = clientService.findById(supplier.getClientid());
					if(isNotEmpty(clint) && isNotEmpty(clint.getBusinessname())) {
						supplier.setContactperson(clint.getBusinessname());
					} 
					supplier.setDocId(supplier.getId().toString());
				}
			}
			dataMap.put("supplierstab", suppliers);
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=mapper.writer();
	
		return writer.writeValueAsString(dataMap);
	 }
	  
	 @Async
	 @RequestMapping(value ="/sendreminders/{id}/{clientid}", method = RequestMethod.POST)
		public @ResponseBody void sendReminders(@RequestBody Reminders reminder,@PathVariable("id") String id,@PathVariable("clientid") String clientid,
				@RequestParam("signcheck") boolean signcheck)throws MasterGSTException {
		 
		 User usr = userRepository.findById(id);
		 Reminders reminders=new Reminders();
		 reminders.setClientid(clientid);
		 reminders.setClientName(reminder.getClientName());
		 reminders.setEmail(reminder.getEmail());
		 reminders.setCc(reminders.getCc());
		 reminders.setMobileNumber(reminder.getMobileNumber());
		 reminders.setSubject(reminder.getSubject());
		 reminders.setMessage(reminder.getMessage());
		 reminders.setUserDetails(reminder.getUserDetails());
		 String userdetails = "";
		 if(signcheck) {
			if(isNotEmpty(usr) && isNotEmpty(usr.getUserSignatureDetails())) {
				userdetails = usr.getUserSignatureDetails();
				reminder.setUserDetails(usr.getUserSignatureDetails());
			}else {
				userdetails = "";
				reminder.setUserDetails("");
			}
		}else {
			userdetails = "";
			reminder.setUserDetails("");
		}
		
	    clientService.saveReminders(reminder);
	    for(String mail :reminders.getEmail()) {
	    	emailService.sendEnrollEmail(mail, VmUtil.velocityTemplate("reminder.vm", reminders.getClientName(),reminders.getMessage(), null,userdetails), reminders.getSubject(),reminders.getCc());
	    }
	    masterGstExecutorService.execute(()->{
			List<String> destinationNos = new ArrayList<String>();
			destinationNos.addAll(reminders.getMobileNumber());
			String sms = null;
			try {
				sms = smsService.sendSMS(destinationNos, reminders.getMessage().trim(), true,false);
			} catch (MasterGSTException e) {
				
				e.printStackTrace();
			}
			
		});
	   
	    //return "success";
	}
	 
	 @Async
	 @RequestMapping(value ="/sendreminderstoAllClients/{id}",method = RequestMethod.POST)
		public @ResponseBody void sendRemindersToAllClients(@RequestBody Reminders reminders,@PathVariable("id") String id,@RequestParam(value = "clientids", required = true) List<String> clientids,
				@RequestParam("signcheck") boolean signcheck)throws MasterGSTException {
		 List<String> destinationNos = new ArrayList<String>();
		 User usr = userRepository.findById(id);
		 String userdetails = "";
		 if(signcheck) {
			if(isNotEmpty(usr) && isNotEmpty(usr.getUserSignatureDetails())) {
				userdetails = usr.getUserSignatureDetails();
				//reminder.setUserDetails(usr.getUserSignatureDetails());
			}else {
				userdetails = "";
				//reminder.setUserDetails("");
			}
		}else {
			userdetails = "";
			//reminder.setUserDetails("");
		}
		for(String ids : clientids) {
				 Client client = clientService.findById(ids);
				 
				 CompanyCustomers customers= profileService.getCompanyCustomers(ids);
				 CompanySuppliers suppliers= profileService.getCompanySuppliers(ids);
				 
				if(isNotEmpty(client)) {
					List<String> clientEmail = Lists.newArrayList();
					List<String> clientMobile = Lists.newArrayList();
					clientEmail.add(client.getEmail());
					clientMobile.add(client.getMobilenumber());
					 Reminders reminder=new Reminders();
					 reminder.setClientid(ids);
					 reminder.setClientName(client.getBusinessname());
					 reminder.setCc(reminders.getCc());
					 if(isNotEmpty(clientEmail)) {
						 reminder.setEmail(clientEmail);
					 }
					 reminder.setMobileNumber(clientMobile);
					 reminder.setSubject(reminders.getSubject());
					 reminder.setMessage(reminders.getMessage());
					 if(isNotEmpty(usr) && isNotEmpty(usr.getUserSignatureDetails())) {
						 reminder.setUserDetails(usr.getUserSignatureDetails());
					 }
					 
				    clientService.saveReminders(reminder);
					if(isNotEmpty(client.getEmail()) && isNotEmpty(client.getBusinessname())) {
						  emailService.sendEnrollEmail(client.getEmail(), VmUtil.velocityTemplate("reminder.vm", client.getBusinessname(),reminders.getMessage(), null,userdetails), reminders.getSubject(),reminders.getCc());
					}
					destinationNos.add(client.getMobilenumber());
				}else if(isNotEmpty(suppliers)) {
					List<String> clientEmail = Lists.newArrayList();
					List<String> clientMobile = Lists.newArrayList();
					clientEmail.add(suppliers.getEmail());
					clientMobile.add(suppliers.getMobilenumber());
					 Reminders reminder=new Reminders();
					 reminder.setClientid(ids);
					 reminder.setClientName(suppliers.getName());
					 reminder.setCc(reminders.getCc());
					 if(isNotEmpty(clientEmail)) {
						 reminder.setEmail(clientEmail);
					 }
					 reminder.setMobileNumber(clientMobile);
					 reminder.setSubject(reminders.getSubject());
					 reminder.setMessage(reminders.getMessage());
					 if(isNotEmpty(usr) && isNotEmpty(usr.getUserSignatureDetails())) {
						 reminder.setUserDetails(usr.getUserSignatureDetails());
					 }
					 
				    clientService.saveReminders(reminder);
					if(isNotEmpty(suppliers.getEmail()) && isNotEmpty(suppliers.getName())) {
						  emailService.sendEnrollEmail(suppliers.getEmail(), VmUtil.velocityTemplate("reminder.vm", suppliers.getName(),reminders.getMessage(), null,userdetails), reminders.getSubject(),reminders.getCc());
					}
					destinationNos.add(suppliers.getMobilenumber());
				}else if(isNotEmpty(customers)) {
						List<String> clientEmail = Lists.newArrayList();
						List<String> clientMobile = Lists.newArrayList();
						clientEmail.add(customers.getEmail());
						clientMobile.add(customers.getMobilenumber());
						 Reminders reminder=new Reminders();
						 reminder.setClientid(ids);
						 reminder.setClientName(customers.getName());
						 reminder.setCc(reminders.getCc());
						 if(isNotEmpty(clientEmail)) {
							 reminder.setEmail(clientEmail);
						 }
						 reminder.setMobileNumber(clientMobile);
						 reminder.setSubject(reminders.getSubject());
						 reminder.setMessage(reminders.getMessage());
						 if(isNotEmpty(usr) && isNotEmpty(usr.getUserSignatureDetails())) {
							 reminder.setUserDetails(usr.getUserSignatureDetails());
						 }
						 
					    clientService.saveReminders(reminder);
						if(isNotEmpty(client.getEmail()) && isNotEmpty(customers.getName())) {
							  emailService.sendEnrollEmail(customers.getEmail(), VmUtil.velocityTemplate("reminder.vm", customers.getName(),reminders.getMessage(), null,userdetails), reminders.getSubject(),reminders.getCc());
						}
						destinationNos.add(customers.getMobilenumber());
					}
			}
			masterGstExecutorService.execute(()->{
				String sms = null;
				try {
					sms = smsService.sendSMS(destinationNos, reminders.getMessage().trim(), true,false);
				} catch (MasterGSTException e) {
					e.printStackTrace();
				}
				
			});	
	   // return "success";
		}
	 
	 @RequestMapping(value = "/getAllReminders/{clientid}")
		public @ResponseBody  List<Reminders> RemindersAll(@PathVariable("clientid") String clientid,ModelMap model) throws Exception {
			  
			List<Reminders> reminders = remindersRepository.findByClientid(clientid);
			 
			Collections.sort(reminders, Collections.reverseOrder());
			return reminders;	 
		}
	 
	 @RequestMapping(value = "/saveUserSignature/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
		 public @ResponseBody User saveUserSignature(@RequestBody MultiValueMap<String, String> userData,@PathVariable("id") String id) throws UnsupportedEncodingException, UnknownHostException {
				final String method = "saveUserSignature::";
				logger.debug(CLASSNAME + method + BEGIN);  
			
				User usr = userRepository.findById(id);
				if(isNotEmpty(usr)) {
					usr.setUserSignatureDetails(userData.getFirst("userdetails"));
				}
			  
			return userService.createUser(usr);	 
		}
	
	 @RequestMapping(value = "/saveauditordetails/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.POST)
		public String addAuditor(@ModelAttribute("auditorDetails") AuditorAdrressDetails auditorDetails, @PathVariable("id") String id,
				@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, @PathVariable("month") int month, 
				@PathVariable("year") int year, ModelMap model) throws Exception {
			final String method = "addUser::";
			logger.debug(CLASSNAME + method + BEGIN);
			logger.debug(CLASSNAME + method + "id\t" + id);
			logger.debug(CLASSNAME + method + "fullname\t" + fullname);
			if (NullUtil.isNotEmpty(auditorDetails.getId()) && new ObjectId(id).equals(auditorDetails.getId())) {
				auditorDetails.setId(null);
			}
			auditorService.saveAuditor(auditorDetails);
			return "redirect:/cp_auditors/" + id + "/" + fullname + "/" + usertype + "/" + month + "/" + year;
			
		}
	 
	 
	 @RequestMapping(value = "/cp_auditors/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
		public String auditorDetails(@PathVariable("id") String id, @PathVariable("name") String fullname, 
				@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
				@PathVariable("year") int year, ModelMap model) throws Exception {
			final String method = "AuditorDetails::";
			logger.debug(CLASSNAME + method + BEGIN);
			logger.debug(CLASSNAME + method + "id\t" + id);
			logger.debug(CLASSNAME + method + "fullname\t" + fullname);
			updateModel(model, id, fullname, usertype, month, year);
			User user = userRepository.findOne(id);
			
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
				CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
				model.addAttribute("companyUser", companyUser);
				if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
					model.addAttribute("addClient", companyUser.getAddclient());
				}else {
					model.addAttribute("addClient", "true");
				}
			}else {
				model.addAttribute("addClient", "true");
			}
			List<AuditorAdrressDetails>  auditorDetails =  auditorService.getAuditors(id);
			model.addAttribute("auditorDetails", auditorDetails);
			model.addAttribute("user", user);
			return "profile/auditorDetails";
		}
	 
	 @RequestMapping(value = "/getaudit/{auditorid}", method = RequestMethod.GET)
		public @ResponseBody AuditorAdrressDetails getInvoice(@PathVariable("auditorid") String auditorid, ModelMap model) throws Exception {
			final String method = "getInvoice::";
			AuditorAdrressDetails auditorDetails = auditorService.findAuditor(auditorid);
			if(isNotEmpty(auditorDetails)) {
				auditorDetails.setUserid(auditorDetails.getId().toString());
			}
			return auditorDetails;
		}
	 
	 @RequestMapping(value = "/cp_acknowledgementUser/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	 public String acknowledgementUser(@PathVariable("id") String id, @PathVariable("name") String fullname,
					@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
					@PathVariable("year") int year, ModelMap model) throws Exception {
		 final String method = "teamUsers::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User cuser = userService.findById(id);
		if(NullUtil.isNotEmpty(cuser) && NullUtil.isNotEmpty(cuser.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(cuser.getEmail());
			model.addAttribute("companyUser", companyUser);
			if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
				model.addAttribute("addClient", companyUser.getAddclient());
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		List<Client> listOfClients= clientService.findByUserid(id);
		model.addAttribute("users", profileService.getGlobalUsers(id));
		model.addAttribute("clients", clientService.findByUserCreatedClients(id));
		
		logger.debug(CLASSNAME + method + END);
		boolean flag=true;
		if(!profileService.isStorageCredentialsEntered(id)) {
			List<String> regions = new ArrayList<>();
			for(Regions re : Regions.values()){
				regions.add(re.getName());
			}
			model.addAttribute("regions", regions);
			model.addAttribute("listOfGroups", listOfClients);
			//aws s3 configuration
			return "profile/aws-s3";
		}
		return "profile/acknowledgement-user";
		
	}
	
	 @RequestMapping(value = "/cp_acknowledgementSavedCredentials/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	 public String acknowledgementUserSavedCredentails(@PathVariable("id") String id, @PathVariable("name") String fullname,
					@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
					@PathVariable("year") int year, ModelMap model) throws Exception {
		 final String method = "teamUsers::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User cuser = userService.findById(id);
		List<Client> listOfClients= clientService.findByUserid(id);
		if(NullUtil.isNotEmpty(cuser) && NullUtil.isNotEmpty(cuser.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(cuser.getEmail());
			model.addAttribute("companyUser", companyUser);
			if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
				model.addAttribute("addClient", companyUser.getAddclient());
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		
		model.addAttribute("users", profileService.getGlobalUsers(id));
		model.addAttribute("clients", clientService.findByUserCreatedClients(id));
		
		logger.debug(CLASSNAME + method + END);
		boolean flag=true;
			List<String> regions = new ArrayList<>();
			for(Regions re : Regions.values()){
				regions.add(re.getName());
			}
			model.addAttribute("regions", regions);
			model.addAttribute("listOfGroups", listOfClients);
		return "profile/acknowledgement-clients";
		
	}
	 
	 @RequestMapping(value = "/checkAcknowledgementsAndRoles", method = RequestMethod.GET)
	 public @ResponseBody String checkAcknowledgementRoles(@RequestParam String clientids, @RequestParam String roleid, ModelMap model) throws Exception {
		 final String method = "checkAcknowledgementRoles::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		String status = profileService.getAcknowledgementRoles(roleid);
		
		logger.debug(CLASSNAME + method + END);
		return status;
	}
	 
	  @RequestMapping(value = "/saveEwayOrEinvCustomFields/{clientid}/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	  public @ResponseBody void saveEwayOrEinvCustomFields(@RequestBody  MultiValueMap<String, String> formData,  @PathVariable("clientid") String clientid,@PathVariable("type") String type) { 
		  final String method =  "saveInvoiceCustomFields::"; 
		  logger.debug(CLASSNAME + method + BEGIN);
	  Client client = clientService.findById(clientid); 
	  if(type.equalsIgnoreCase("eway")) {
	  EwayBillCustomFields customField1 = new EwayBillCustomFields(); 
	  EwayBillCustomFields customField2 = new EwayBillCustomFields(); 
	  EwayBillCustomFields customField3 = new EwayBillCustomFields(); 
	  EwayBillCustomFields customField4 = new EwayBillCustomFields();
	  
	  String dInEwayBill1 = formData.getFirst("sacustomfiled1"); 
	  boolean displayInEwayBill; 
	  if("true".equals(dInEwayBill1)) {
		  displayInEwayBill = true; 
	  }else { 
		  displayInEwayBill = false; 
	  } 
	  customField1.setDisplayInEwayBill(displayInEwayBill);
	  String dInPrint1 = formData.getFirst("pcustomfiled1"); 
	  boolean displayInPrint1;
	  if("true".equals(dInPrint1)) { 
		  displayInPrint1 = true; 
	  }else { 
		  displayInPrint1 = false; 
	  } 
	  customField1.setDisplayInPrint(displayInPrint1);
	  customField1.setNameInEwayBill(formData.getFirst("clabelinput1"));
	  client.setEwaycustomField1(customField1);
	  
	  String dInSales2 = formData.getFirst("sacustomfiled2"); 
	  boolean displayInSlaes2; 
	  if("true".equals(dInSales2)) { 
		  displayInSlaes2 = true; 
	  }else { 
		  displayInSlaes2 = false; 
	  } 
	  customField2.setDisplayInEwayBill(displayInSlaes2);
		String dInPrint2 = formData.getFirst("pcustomfiled2");
		boolean displayInPrint2;
		if ("true".equals(dInPrint2)) {
			displayInPrint2 = true;
		} else {
			displayInPrint2 = false;
		}
		customField2.setDisplayInPrint(displayInPrint2);
		customField2.setNameInEwayBill(formData.getFirst("clabelinput2"));
		client.setEwaycustomField2(customField2);

		String dInSales3 = formData.getFirst("sacustomfiled3");
		boolean displayInSlaes3;
		if ("true".equals(dInSales3)) {
			displayInSlaes3 = true;
		} else {
			displayInSlaes3 = false;
		}
		customField3.setDisplayInEwayBill(displayInSlaes3);
		String dInPrint3 = formData.getFirst("pcustomfiled3");
		boolean displayInPrint3;
		if ("true".equals(dInPrint3)) {
			displayInPrint3 = true;
		} else {
			displayInPrint3 = false;
		}
		customField3.setDisplayInPrint(displayInPrint3);
		customField3.setNameInEwayBill(formData.getFirst("clabelinput3"));
		client.setEwaycustomField3(customField3);

		String dInSales4 = formData.getFirst("sacustomfiled4");
		boolean displayInSlaes4;
		if ("true".equals(dInSales4)) {
			displayInSlaes4 = true;
		} else {
			displayInSlaes4 = false;
		}
		customField4.setDisplayInEwayBill(displayInSlaes4);
		String dInPrint4 = formData.getFirst("pcustomfiled4");
		boolean displayInPrint4;
		if ("true".equals(dInPrint4)) {
			displayInPrint4 = true;
		} else {
			displayInPrint4 = false;
		}
		customField4.setDisplayInPrint(displayInPrint4);
		customField4.setNameInEwayBill(formData.getFirst("clabelinput4"));
		client.setEwaycustomField4(customField4);
	  }else {
		  EinvoiceCustomFields customField1 = new EinvoiceCustomFields(); 
		  EinvoiceCustomFields customField2 = new EinvoiceCustomFields(); 
		  EinvoiceCustomFields customField3 = new EinvoiceCustomFields(); 
		  EinvoiceCustomFields customField4 = new EinvoiceCustomFields();
		  
		  String dInEinvoice1 = formData.getFirst("sacustomfiled1"); 
		  boolean displayInEinvoice; 
		  if("true".equals(dInEinvoice1)) {
			  displayInEinvoice = true; 
		  }else { 
			  displayInEinvoice = false; 
		  } 
		  customField1.setDisplayInEinvoice(displayInEinvoice);
		  String dInPrint1 = formData.getFirst("pcustomfiled1"); 
		  boolean displayInPrint1;
		  if("true".equals(dInPrint1)) { 
			  displayInPrint1 = true; 
		  }else { 
			  displayInPrint1 = false; 
		  } 
		  customField1.setDisplayInPrint(displayInPrint1);
		  customField1.setNameInEinvoice(formData.getFirst("clabelinput1"));
		  client.setEinvcustomField1(customField1);
		  
		  String dInSales2 = formData.getFirst("sacustomfiled2"); 
		  boolean displayInSlaes2; 
		  if("true".equals(dInSales2)) { 
			  displayInSlaes2 = true; 
		  }else { 
			  displayInSlaes2 = false; 
		  } 
		  customField2.setDisplayInEinvoice(displayInSlaes2);
			String dInPrint2 = formData.getFirst("pcustomfiled2");
			boolean displayInPrint2;
			if ("true".equals(dInPrint2)) {
				displayInPrint2 = true;
			} else {
				displayInPrint2 = false;
			}
			customField2.setDisplayInPrint(displayInPrint2);
			customField2.setNameInEinvoice(formData.getFirst("clabelinput2"));
			client.setEinvcustomField2(customField2);

			String dInSales3 = formData.getFirst("sacustomfiled3");
			boolean displayInSlaes3;
			if ("true".equals(dInSales3)) {
				displayInSlaes3 = true;
			} else {
				displayInSlaes3 = false;
			}
			customField3.setDisplayInEinvoice(displayInSlaes3);
			String dInPrint3 = formData.getFirst("pcustomfiled3");
			boolean displayInPrint3;
			if ("true".equals(dInPrint3)) {
				displayInPrint3 = true;
			} else {
				displayInPrint3 = false;
			}
			customField3.setDisplayInPrint(displayInPrint3);
			customField3.setNameInEinvoice(formData.getFirst("clabelinput3"));
			client.setEinvcustomField3(customField3);

			String dInSales4 = formData.getFirst("sacustomfiled4");
			boolean displayInSlaes4;
			if ("true".equals(dInSales4)) {
				displayInSlaes4 = true;
			} else {
				displayInSlaes4 = false;
			}
			customField4.setDisplayInEinvoice(displayInSlaes4);
			String dInPrint4 = formData.getFirst("pcustomfiled4");
			boolean displayInPrint4;
			if ("true".equals(dInPrint4)) {
				displayInPrint4 = true;
			} else {
				displayInPrint4 = false;
			}
			customField4.setDisplayInPrint(displayInPrint4);
			customField4.setNameInEinvoice(formData.getFirst("clabelinput4"));
			client.setEinvcustomField4(customField4);

	  }
	 clientService.saveClient(client); 
	}
	  
	  @RequestMapping(value = "/savecustomFields/{id}/{clientid}", method = RequestMethod.POST)
		public @ResponseBody String addCustomDetails(@RequestBody CustomData customdetails,@PathVariable("id") String userid,
				@PathVariable("clientid") String clientid, ModelMap model, HttpServletRequest request) throws Exception {
			final String method = "addCustomDetails::";
			String rParam = request.getParameter("displayType");
			String oldid = request.getParameter("oldid");
			String oldCustomname = request.getParameter("oldname");
			if(isNotEmpty(oldCustomname) && oldCustomname.contains("-mgst-")) {
				oldCustomname = oldCustomname.replaceAll("-mgst-", "&");
			}
			String oldCustomtype = request.getParameter("oldtype");
			String message = null;
			if(isNotEmpty(oldid)) {
				CustomFields beforeCustomData = customFieldsRepository.findOne(oldid);
				message = profileService.editCustomFieldsData(rParam,userid,clientid,beforeCustomData,customdetails,oldCustomname,oldCustomtype);
			}else {
				CustomFields oldCustomData = customFieldsRepository.findByClientid(clientid);
				message = profileService.saveCustomFieldsData(rParam,userid,clientid,oldCustomData,customdetails);
			}
			return message;
	  }
	  @RequestMapping(value = "/getCustomFields/{clientid}", method = RequestMethod.GET)
		public @ResponseBody CustomFields getCustomFieldDetails(@PathVariable("clientid") String clientid, ModelMap model, HttpServletRequest request) throws Exception {
			final String method = "addCustomDetails::";
			CustomFields customFields = customFieldsRepository.findByClientid(clientid);
			if(isNotEmpty(customFields)) {
				customFields.setUserid(customFields.getId().toString());
				if(isNotEmpty(customFields.getSales())) {
					for(CustomData data : customFields.getSales()) {
						data.setTypeId(data.getId().toString());
					}
				}
				if(isNotEmpty(customFields.getPurchase())) {
					for(CustomData data : customFields.getPurchase()) {
						data.setTypeId(data.getId().toString());
					}
				}
				if(isNotEmpty(customFields.getEinvoice())) {
					for(CustomData data : customFields.getEinvoice()) {
						data.setTypeId(data.getId().toString());
					}
				}
				if(isNotEmpty(customFields.getEwaybill())) {
					for(CustomData data : customFields.getEwaybill()) {
						data.setTypeId(data.getId().toString());
					}
				}
				if(isNotEmpty(customFields.getItems())) {
					for(CustomData data : customFields.getItems()) {
						data.setTypeId(data.getId().toString());
					}
				}
			}
			return customFields;
	  }
	  @RequestMapping(value = "/checkCustomFields/{clientid}", method = RequestMethod.GET)
		public @ResponseBody boolean getCustomDetails(@PathVariable("clientid") String clientid, ModelMap model, HttpServletRequest request) throws Exception {
			CustomFields customData = customFieldsRepository.findByClientid(clientid);
			String fieldName = request.getParameter("fieldname");
			String fieldType = request.getParameter("fieldtype");
			String type = request.getParameter("type");
			boolean isExist = false;
			if(isNotEmpty(customData)) {
				if(type.equalsIgnoreCase("Sales")) {
					if(isNotEmpty(customData.getSales()) && customData.getSales().size() > 0) {
						for(CustomData data : customData.getSales()) {
							if(fieldName.equalsIgnoreCase(data.getCustomFieldName()) && fieldType.equalsIgnoreCase(data.getCustomFieldType())) {
								isExist = true;
								break;
							}else {
								isExist = false;
							}
						}
					}
				}else if(type.equalsIgnoreCase("Purchase")) {
					if(isNotEmpty(customData.getPurchase()) && customData.getPurchase().size() > 0) {
						for(CustomData data : customData.getPurchase()) {
							if(fieldName.equalsIgnoreCase(data.getCustomFieldName()) && fieldType.equalsIgnoreCase(data.getCustomFieldType())) {
								isExist = true;
								break;
							}else {
								isExist = false;
							}
						}
					}
				}else if(type.equalsIgnoreCase("E-invoice")) {
					if(isNotEmpty(customData.getEinvoice()) && customData.getEinvoice().size() > 0) {
						for(CustomData data : customData.getEinvoice()) {
							if(fieldName.equalsIgnoreCase(data.getCustomFieldName()) && fieldType.equalsIgnoreCase(data.getCustomFieldType())) {
								isExist = true;
								break;
							}else {
								isExist = false;
							}
						}
					}
				}else if(type.equalsIgnoreCase("Ewaybill")) {
					if(isNotEmpty(customData.getEwaybill()) && customData.getEwaybill().size() > 0) {
						for(CustomData data : customData.getEwaybill()) {
							if(fieldName.equalsIgnoreCase(data.getCustomFieldName()) && fieldType.equalsIgnoreCase(data.getCustomFieldType())) {
								isExist = true;
								break;
							}else {
								isExist = false;
							}
						}
					}
				}
				
				
			}
			return isExist;
	  }
	  @RequestMapping(value = "/deleteCustomField/{id}/{typeid}/{clientid}/{type}", method = RequestMethod.GET)
		public @ResponseBody void deleteCustomFields(@PathVariable("id") String id,@PathVariable("typeid") String typeid,@PathVariable("clientid") String clientid, @PathVariable("type") String type,
				ModelMap model) throws Exception {
			final String method = "deleteSubGroupDetails::";
			logger.debug(CLASSNAME + method + BEGIN);
			profileService.deleteCustomFields(id, typeid, clientid,type);
		}
	  
	  @RequestMapping(value = "/updtusrAccess/{userid}", method = RequestMethod.GET)
	  public @ResponseBody void updateUserAccess(@PathVariable("userid") String userid,@RequestParam("type") String returntype,
				@RequestParam("flag") boolean flag, ModelMap model) throws Exception {
		  final String method = "updateUserAccess::";
		  logger.debug(CLASSNAME + method + BEGIN);
		  User user = userService.findById(userid);
		  if (isNotEmpty(user)) {
			  if(isNotEmpty(returntype)) {
				 /*if("GSTR4Annual".equalsIgnoreCase(returntype)) {
					 user.setAccessGstr4Annual(flag);
				 }else if("GSTR6".equalsIgnoreCase(returntype)) {
					 user.setAccessGstr6(flag);					 
				 }else if("GSTR8".equalsIgnoreCase(returntype)) {
					 user.setAccessGstr8(flag);
				 }*/
				 user.setAccessGstr8(flag);
			 }
			 userService.updateUser(user);
		  }
			
		  List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		  if(isNotEmpty(users)) {
			  for(CompanyUser cusr : users) {
				  if(isNotEmpty(cusr) && isNotEmpty(cusr.getEmail())) {
					  User usr = userRepository.findByEmail(cusr.getEmail());
					  if(isNotEmpty(usr)) {
						  if(isNotEmpty(returntype)) {
							  /*if("GSTR4Annual".equalsIgnoreCase(returntype)) {
								  user.setAccessGstr4Annual(flag);
							  }else if("GSTR6".equalsIgnoreCase(returntype)) {
								  user.setAccessGstr6(flag);					 
							  }else if("GSTR8".equalsIgnoreCase(returntype)) {
								  user.setAccessGstr8(flag);
							  }*/
							  user.setAccessGstr8(flag);
						  }
						  userRepository.save(usr);
					  }
				  }
			  }
		  }
		  logger.debug(CLASSNAME + method + END);
	  }
		@RequestMapping(value = "/saveClientSignature/{clientid}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
		public @ResponseBody void saveInvConfiurations(@RequestBody MultiValueMap<String, String> formData,
				@PathVariable("clientid") String clientid) {
			final String method = "saveClientSignature::";
			logger.debug(CLASSNAME + method + BEGIN);
			Client client = clientService.findById(clientid);
			 client.setClientSignature(formData.getFirst("clientSignature"));
			 clientService.saveClient(client);
		}
		@RequestMapping(value = "/dwnldItemXls/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
		public @ResponseBody FileSystemResource downloadItemExcelData(@PathVariable("id") String id, @PathVariable("clientid") String clientid, 
				@PathVariable("month") int month, @PathVariable("year") int year, HttpServletResponse response, HttpServletRequest request) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			Client client = clientService.findById(clientid);
			String gstnumber="";
			if(isNotEmpty(client) && isNotEmpty(client.getGstnnumber())) {
				gstnumber = client.getGstnnumber();
			}
			response.setHeader("Content-Disposition", "inline; filename='MGST_"+gstnumber+"_"+year+".xls");
			List<CompanyItems> items = companyItemsRepository.findByClientid(clientid);
			File file = new File("MGST_"+gstnumber+"_"+month+year+".xls");
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				List<String> headers = Arrays.asList("Item No","Item Name","Item Group Code","Item Group Name","Item Description","Unit Of Measurement","Opening Stock","As on Date","Saftery Stock Level","Re-Order Required","HSN/SAC Code","Tax Rate","Item Sale Price","Item Purchase Price","Whole Sale Price","MRP Price","Discount Value","Exempted Value","Item Custom Field1","Item Custom Field2","Item Custom Field3","Item Custom Field4");
				SimpleExporter exporter = new SimpleExporter();
				exporter.gridExport(headers, items, "itemno,description,itemgroupno,groupdescription,itemDescription,unit,openingStock,asOnDate,stocklevel,reOrderType,code,taxrate,salePrice,purchasePrice,wholeSalePrice,mrpPrice,discount,exmepted,itemCustomField1,itemCustomField2,itemCustomField3,itemCustomField4", fos);
			}catch (IOException e) {
				logger.error(CLASSNAME + "downloadItemExcelData : ERROR", e);
			}
			return new FileSystemResource(new File("MGST_"+gstnumber+"_"+month+year+".xls"));
		}
		
}
