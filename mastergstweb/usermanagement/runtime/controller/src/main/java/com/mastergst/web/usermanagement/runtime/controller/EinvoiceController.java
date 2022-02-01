package com.mastergst.web.usermanagement.runtime.controller;



import static com.mastergst.core.common.MasterGSTConstants.ANX1;
import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.EXCEL_TEMPLATE;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR4;
import static com.mastergst.core.common.MasterGSTConstants.GSTR5;
import static com.mastergst.core.common.MasterGSTConstants.GSTR6;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jxls.reader.ReaderBuilder;
import org.jxls.reader.ReaderConfig;
import org.jxls.reader.XLSReadStatus;
import org.jxls.reader.XLSReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.Currencycodes;
import com.mastergst.configuration.service.CurrencycodesRepository;
import com.mastergst.configuration.service.ErrorCodeConfig;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.core.common.AuditLogConstants;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.MoneyConverterUtil;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.HeaderKeys;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.audit.AuditlogService;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.BuyerDetails;
import com.mastergst.usermanagement.runtime.domain.CancelIRN;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;
import com.mastergst.usermanagement.runtime.domain.CompanyBankDetails;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CompanyInvoices;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.CustomFields;
import com.mastergst.usermanagement.runtime.domain.DeletedInvoices;
import com.mastergst.usermanagement.runtime.domain.DispatcherDetails;
import com.mastergst.usermanagement.runtime.domain.EinvoiceConfigurations;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTRExports;
import com.mastergst.usermanagement.runtime.domain.ImportResponse;
import com.mastergst.usermanagement.runtime.domain.ImportSummary;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.PrintConfiguration;
import com.mastergst.usermanagement.runtime.domain.ShipmentDetails;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.domain.TemplateMapper;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;
import com.mastergst.usermanagement.runtime.repository.AccountingJournalRepository;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyBankDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyCustomersRepository;
import com.mastergst.usermanagement.runtime.repository.CustomFieldsRepository;
import com.mastergst.usermanagement.runtime.repository.EinvoiceConfigurationRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;
import com.mastergst.usermanagement.runtime.response.CancelIRNResponse;
import com.mastergst.usermanagement.runtime.response.GenerateIRNResponse;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.service.BulkImportTaskService;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.EinvoiceService;
import com.mastergst.usermanagement.runtime.service.IHubConsumerService;
import com.mastergst.usermanagement.runtime.service.ImportInvoiceService;
import com.mastergst.usermanagement.runtime.service.ImportMapperService;
import com.mastergst.usermanagement.runtime.service.PrintService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;
import com.mastergst.usermanagement.runtime.support.WorkbookUtility;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
@Controller
public class EinvoiceController {
	private static final Logger logger = LogManager.getLogger(EinvoiceController.class.getName());
	private static final String CLASSNAME = "EinvoiceController::";
	private String gstnFormat = "[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}|[0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1}|[0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1}|[0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1}|[9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1}";
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	private static final String INVOICENO_REGEX ="^[0-9a-zA-Z/-]{1,16}$";
	@Autowired	private ImportInvoiceService importInvoiceService;
	@Autowired	private ClientService clientService;
	@Autowired	private EinvoiceService einvoiceService;
	@Autowired	private UserService userService;
	@Autowired	private ProfileService profileService;
	@Autowired	private ConfigService configService;
	@Autowired	private ImportMapperService importMapperService;
	@Autowired	private IHubConsumerService iHubConsumerService;
	@Autowired	private GSTR1Repository gstr1Repository;
	@Autowired	private EinvoiceConfigurationRepository  einvoiceConfigurationRepository;
	@Autowired	private PrintService printService;
	@Autowired  private ResourceLoader resourceLoader;
	@Autowired	private BulkImportTaskService bulkImportTaskService; 
	@Autowired	private CompanyCustomersRepository companyCustomersRepository;
	@Autowired	private CurrencycodesRepository currencycodesRepository;
	@Autowired	private CompanyBankDetailsRepository companyBankDetailsRepository;
	@Autowired	private SubscriptionService subscriptionService;
	@Autowired	private ClientUserMappingRepository clientUserMappingRepository;
	@Autowired	UserRepository userRepository;
	@Autowired	CustomFieldsRepository customFieldsRepository;
	@Autowired	AccountingJournalRepository accountingJournalRepository;
	@Autowired AuditlogService auditlogService;
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	@RequestMapping(value = "/einvoice/{id}/{name}/{usertype}/{returntype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String einvoiceView(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype,@PathVariable("returntype") String returntype, @PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year,
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "einvoiceView::";
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
		
		Client client = clientService.findById(clientid);
		EinvoiceConfigurations einvConfig=null;
		if(isNotEmpty(client)) {
			einvConfig = einvoiceService.getEinvoiceConfig(client.getId().toString());
		}
		if(isNotEmpty(einvConfig) && isNotEmpty(einvConfig.getConnStaus())) {
			String einvoiceStatus = einvConfig.getConnStaus();
			if(isNotEmpty(einvConfig.getUserName())) {
				HeaderKeys headerKeys=clientService.getHeaderkeysGstusername(einvConfig.getUserName());			
				if(isEmpty(headerKeys)) {
					einvoiceStatus = "InActive";
				}else {
					
					if(NullUtil.isNotEmpty(headerKeys) && NullUtil.isNotEmpty(headerKeys.getEinvoiceTokenExpiry())) {
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date = sdf.parse(headerKeys.getEinvoiceTokenExpiry());
						long duration = date.getTime() - Calendar.getInstance().getTime().getTime();
						long diff = TimeUnit.MILLISECONDS.toMinutes(duration);
							if(diff <= 0) {
								einvoiceStatus = "Expired";
							}else {
								einvoiceStatus = "Active";
							}
					}else {
						einvoiceStatus = "Expired";
					}	
				}
			}
			einvConfig.setConnStaus(einvoiceStatus);
			einvoiceService.saveEinvoiceConfigurations(einvConfig);
			
			model.addAttribute("einvAuthSttaus", einvConfig.getConnStaus());
		}
		model.addAttribute("einvoiceType",returntype);
		model.addAttribute("user", user);
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", clientService.getGSTReturnsSummary(client, month, year, false));
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		model.addAttribute("pconfig", pconfig);
		logger.debug(CLASSNAME + method + END);
		return "einvoice/einvoice_view";
	}
	
	@RequestMapping(value = "/syncirnstatus/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public String updateInvStatus(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "syncInvData::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		User user = userService.findById(id);
		EinvoiceConfigurations einvConfig=null;
		if(isNotEmpty(client)) {
			einvConfig = einvoiceService.getEinvoiceConfig(client.getId().toString());
		}
		String einvoiceStatus = "";
		if(isNotEmpty(einvConfig) && isNotEmpty(einvConfig.getConnStaus())) {
			einvoiceStatus = einvConfig.getConnStaus();
			if(isNotEmpty(einvConfig.getUserName())) {
				HeaderKeys headerKeys=clientService.getHeaderkeysGstusername(einvConfig.getUserName());			
				if(isEmpty(headerKeys)) {
					einvoiceStatus = "InActive";
				}else {
					
					if(NullUtil.isNotEmpty(headerKeys) && NullUtil.isNotEmpty(headerKeys.getEinvoiceTokenExpiry())) {
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date date = sdf.parse(headerKeys.getEinvoiceTokenExpiry());
						long duration = date.getTime() - Calendar.getInstance().getTime().getTime();
						long diff = TimeUnit.MILLISECONDS.toMinutes(duration);
							if(diff <= 0) {
								einvoiceStatus = "Expired";
							}else {
								einvoiceStatus = "Active";
							}
					}else {
						einvoiceStatus = "Expired";
					}	
				}
			}
			einvConfig.setConnStaus(einvoiceStatus);
			einvoiceService.saveEinvoiceConfigurations(einvConfig);
		}
		if(isNotEmpty(einvoiceStatus) && einvoiceStatus.equalsIgnoreCase("Active")) {
			einvoiceService.updateAlreadyGeneratedIRNByOtherSystem(client,returntype,id,usertype,month,year);
		}
		logger.debug(CLASSNAME + method + END);
		return "redirect:/einvoice/" + id + "/" + fullname + "/" + usertype + "/" + returntype + "/" + clientid + "/" + month + "/" + year;
	}
	
	
	
	@RequestMapping(value = "/einvprint/{id}/{name}/{usertype}/{clientid}/{returntype}/{invId}", method = RequestMethod.GET)
	public String printEInvoice(@PathVariable("id") String id, @PathVariable("name") String fullname,@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype,@PathVariable("invId") String invId, ModelMap model)throws Exception {
		final String method = "printEInvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("returntype", returntype);
		User usr = userService.findById(id);
		Client client = clientService.findById(clientid);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		model.addAttribute("client", client);
		if(isNotEmpty(client.getStatename())){
			String[] state = client.getStatename().split("-");
			model.addAttribute("state_code", state[0]);
		}
		InvoiceParent invoiceParent = clientService.getInvoice(invId, "GSTR1");
		String invNumberText = "";
		String invDateText = "";
		String invText = "";
		
		if(isNotEmpty(invoiceParent.getStatename())){
			String[] state = invoiceParent.getStatename().split("-");
			model.addAttribute("istate_code", state[0]);
		}
		
			String amountinwords="";
			if(isNotEmpty(invoiceParent.getRoundOffAmount()) && invoiceParent.getRoundOffAmount() != 0d) {
				Double totamount = 0d;
				if(invoiceParent.isTdstcsenable() && isNotEmpty(invoiceParent.getNotroundoftotalamount())) {
					totamount = invoiceParent.getNotroundoftotalamount();
					if(isNotEmpty(invoiceParent.getTcstdsAmount())) {
						totamount += invoiceParent.getTcstdsAmount();
					}
				}else if(isNotEmpty(invoiceParent.getTotalamount())) {
					totamount = invoiceParent.getTotalamount();
					if(isNotEmpty(invoiceParent.getTcstdsAmount())) {
						totamount += invoiceParent.getTcstdsAmount();
					}
				}
				amountinwords=MoneyConverterUtil.getMoneyIntoWords(totamount).toUpperCase();
			}else if(isNotEmpty(invoiceParent.getTotalamount())) {
				Double totamount = invoiceParent.getTotalamount();
				if(isNotEmpty(invoiceParent.getTcstdsAmount())) {
					totamount += invoiceParent.getTcstdsAmount();
				}
				amountinwords=MoneyConverterUtil.getMoneyIntoWords(totamount).toUpperCase();
			}
			
			//String invText="";
			//String category="";
			if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getTyp())) {
				if(("INV").equalsIgnoreCase(invoiceParent.getTyp())) {
					invText =  "TAX INVOICE";
				}else if(("CRN").equalsIgnoreCase(invoiceParent.getTyp())) {
					invText =  "CREDIT NOTE";
				}else if(("DBN").equalsIgnoreCase(invoiceParent.getTyp())) {
					invText =  "DEBIT NOTE";
				}else {
					invText =  "TAX INVOICE";
				}
			}else {
				invText =  "TAX INVOICE";
			}
			
			if(invoiceParent.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
				String docType = "";String originalInvNo = ""; String originalInvDate="";
				
					docType = ((GSTR1) invoiceParent).getCdnr().get(0).getNt().get(0).getNtty();
					originalInvNo = ((GSTR1) invoiceParent).getCdnr().get(0).getNt().get(0).getInum();
					originalInvDate = ((GSTR1) invoiceParent).getCdnr().get(0).getNt().get(0).getIdt();
				
				
				model.addAttribute("originalInvNo", originalInvNo);
				model.addAttribute("originalInvDate", originalInvDate);
				//if(isNotEmpty(invoiceParent.getTyp()) && ("CRN").equalsIgnoreCase(invoiceParent.getTyp())){
				if(docType.equals("C")){
					invText="CREDIT NOTE";
					invNumberText="Credit Note.No";
					invDateText= "Credit Note Date";
				}else if(docType.equals("D")){
				//else if(isNotEmpty(invoiceParent.getTyp()) &&("DBN").equalsIgnoreCase(invoiceParent.getTyp())){
					invText="DEBIT NOTE";
					invNumberText="Debit Note.No";
					invDateText= "Debit Note Date";
				}
			}else if(invoiceParent.getInvtype().equals(MasterGSTConstants.CDNUR)){
				String docType = invoiceParent.getCdnur().get(0).getNtty();
				String originalInvNo = invoiceParent.getCdnur().get(0).getInum();
				String originalInvDate = invoiceParent.getCdnur().get(0).getIdt();
				model.addAttribute("originalInvNo", originalInvNo);
				model.addAttribute("originalInvDate", originalInvDate);
				//if(isNotEmpty(invoiceParent.getTyp()) && ("CRN").equalsIgnoreCase(invoiceParent.getTyp())){
				if(docType.equals("C")){
					invText="CREDIT NOTE";
					invNumberText="Credit Note.No";
					invDateText= "Credit Note Date";
				}else if(docType.equals("D")){
				//else if(isNotEmpty(invoiceParent.getTyp()) &&("DBN").equalsIgnoreCase(invoiceParent.getTyp())){
					invText="DEBIT NOTE";
					invNumberText="Debit Note.No";
					invDateText= "Debit Note Date";
				}
			}else{
				invNumberText="Invoice Number";
				invDateText= "Invoice Date";
				if(invoiceParent.getInvtype().equals(MasterGSTConstants.EXPORTS)){
					invText = "EXPORT INVOICE";
				}else{
					if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getInvoiceText())){
						invText =  pconfig.getInvoiceText();
					}else{
						invText = "TAX INVOICE";
					}
				}
			}
			String custAddress = "";
			//CompanyCustomers customers = companyCustomersRepository.findByNameAndClientid(invoiceParent.getBilledtoname(), clientid);
			CompanyCustomers customers = null;
			if(isNotEmpty(invoiceParent.getB2b()) && isNotEmpty(invoiceParent.getB2b().get(0)) && isNotEmpty(invoiceParent.getB2b().get(0).getCtin())) {
				String gstnnumber =invoiceParent.getB2b().get(0).getCtin().trim();
				customers = companyCustomersRepository.findByGstnnumberAndClientid(gstnnumber, clientid);
			}else {
				customers = companyCustomersRepository.findByNameAndClientid(invoiceParent.getBilledtoname(), clientid);
			}
			if(isNotEmpty(invoiceParent.getBuyerDtls())) {
				
				if(isNotEmpty(invoiceParent.getBuyerDtls().getAddr1())) {
					custAddress = invoiceParent.getBuyerDtls().getAddr1();
				}
				if(isNotEmpty(invoiceParent.getEntertaimentprintto())) {
					if(isNotEmpty(invoiceParent.getBuyerDtls().getAddr2())) {
						model.addAttribute("theatreName", invoiceParent.getBuyerDtls().getAddr2());
					}else {
						model.addAttribute("theatreName", "");
					}
				}else {
					if(isNotEmpty(invoiceParent.getBuyerDtls().getAddr2())) {
						custAddress = custAddress+','+ invoiceParent.getBuyerDtls().getAddr2();
					}
				}
				if(isNotEmpty(invoiceParent.getBuyerDtls().getLoc())) {
					custAddress = custAddress+','+ invoiceParent.getBuyerDtls().getLoc();
				}
			}
			if(isNotEmpty(invoiceParent.getStatename())) {
				if(isNotEmpty(custAddress)) {
					if(invoiceParent.getStatename().contains("-")) {
						custAddress = custAddress+','+ invoiceParent.getStatename().substring(3);
					}else {
						custAddress = custAddress+','+ invoiceParent.getStatename();
					}
				}else {
					if(invoiceParent.getStatename().contains("-")) {
						custAddress = invoiceParent.getStatename().substring(3);
					}else {
						custAddress = invoiceParent.getStatename();
					}
				}
			}
			if(isNotEmpty(invoiceParent.getBuyerDtls())) {
				if(isNotEmpty(invoiceParent.getBuyerDtls().getPin())) {
					if(isNotEmpty(custAddress)) {
						custAddress = custAddress+'-'+ invoiceParent.getBuyerDtls().getPin();
					}else {
						custAddress = invoiceParent.getBuyerDtls().getPin()+"";
					}
				}
			}
			String amountcurrency = "";
			if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
				//Currency c1 = Currency.getInstance(invoiceParent.getAddcurrencyCode());
				Double.parseDouble(df2.format(invoiceParent.getTotalCurrencyAmount()));
				Currencycodes currencycode = currencycodesRepository.findByCode(invoiceParent.getAddcurrencyCode());
				String currencySymbol = "";
				String mainunit="";
				String fractionunit = "";
				if(isNotEmpty(currencycode) && isNotEmpty(currencycode.getSymbolcode())) {
					currencySymbol = currencycode.getSymbolcode();
				}
				if(isNotEmpty(currencycode) && isNotEmpty(currencycode.getMainunit())) {
					mainunit = currencycode.getMainunit();
				}
				if(isNotEmpty(currencycode) && isNotEmpty(currencycode.getFractionunit())) {
					fractionunit = currencycode.getFractionunit();
				}
				
				if(isNotEmpty(invoiceParent.getTotalCurrencyAmount())) {
					amountcurrency=MoneyConverterUtil.convertNumber(invoiceParent.getTotalCurrencyAmount(),mainunit,fractionunit).toUpperCase();
				}
				model.addAttribute("currencyTotal", currencySymbol+" "+Double.parseDouble(df2.format(invoiceParent.getTotalCurrencyAmount())));
			}
			if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getInvtype()) && "B2C".equalsIgnoreCase(invoiceParent.getInvtype())) {
				if(isNotEmpty(invoiceParent.getBankDetails()) && isNotEmpty(invoiceParent.getBankDetails().getAccountnumber())) {
					CompanyBankDetails bankdetails = companyBankDetailsRepository.findByClientidAndAccountnumber(invoiceParent.getClientid(), invoiceParent.getBankDetails().getAccountnumber());
					if(isNotEmpty(bankdetails) && isNotEmpty(bankdetails.getQrcodeid())) {
						model.addAttribute("qrcodeid", bankdetails.getQrcodeid());
					}
				}
			}
			if((isNotEmpty(invoiceParent.getShipmentDtls()) && isNotEmpty(invoiceParent.getShipmentDtls().getAddr1())) || 
					(isNotEmpty(invoiceParent.getDispatcherDtls()) && isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1()))) {
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getTrdNm())) {
					model.addAttribute("c_ship_name", invoiceParent.getShipmentDtls().getTrdNm());
				}else if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls()) && NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getNm())) {
					model.addAttribute("c_ship_name", invoiceParent.getDispatcherDtls().getNm());
				}else {
					model.addAttribute("c_ship_name", "");
				}
				String shipaddr = "";
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getAddr1())) {
					shipaddr = invoiceParent.getShipmentDtls().getAddr1();
					if(isNotEmpty(invoiceParent.getShipmentDtls().getAddr2())) {
						shipaddr = shipaddr+','+ invoiceParent.getShipmentDtls().getAddr2();
					}
					if(isNotEmpty(invoiceParent.getShipmentDtls().getLoc())) {
						shipaddr = shipaddr+','+ invoiceParent.getShipmentDtls().getLoc();
					}
					if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getStcd())) {
						if(invoiceParent.getShipmentDtls().getStcd().contains("-")) {
							shipaddr = shipaddr+','+ invoiceParent.getShipmentDtls().getStcd().substring(3);
						}else {
							String statename = invoiceParent.getShipmentDtls().getStcd();
							List<StateConfig> states = configService.getStates();
							for (StateConfig state : states) {
								String name = state.getName();
								String[] nm = state.getName().split("-");
								if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
									statename = name;
									break;
								}else if((nm[0].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
									statename = name;
									break;
								}
							}
							if(statename.contains("-")) {
								statename = statename.substring(3);
							}
							shipaddr = shipaddr+','+ statename;
						}
					}
					if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getPin())) {
						shipaddr = shipaddr+'-'+ invoiceParent.getShipmentDtls().getPin();
					}
					model.addAttribute("headText", "Details Of Shipment");
				}else if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls()) && NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1())) {
					shipaddr = invoiceParent.getDispatcherDtls().getAddr1();
					if(isNotEmpty(invoiceParent.getDispatcherDtls().getAddr2())) {
						shipaddr = shipaddr+','+ invoiceParent.getDispatcherDtls().getAddr2();
					}
					if(isNotEmpty(invoiceParent.getDispatcherDtls().getLoc())) {
						shipaddr = shipaddr+','+ invoiceParent.getDispatcherDtls().getLoc();
					}
					if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getStcd())) {
						if(invoiceParent.getDispatcherDtls().getStcd().contains("-")) {
							shipaddr = shipaddr+','+ invoiceParent.getDispatcherDtls().getStcd().substring(3);
						}else {
							String statename = invoiceParent.getDispatcherDtls().getStcd();
							List<StateConfig> states = configService.getStates();
							for (StateConfig state : states) {
								String name = state.getName();
								String[] nm = state.getName().split("-");
								if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
									statename = name;
									break;
								}else if((nm[0].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
									statename = name;
									break;
								}
							}
							if(statename.contains("-")) {
								statename = statename.substring(3);
							}
							shipaddr = shipaddr+','+ statename;
						}
					}
					if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getPin())) {
						shipaddr = shipaddr+'-'+ invoiceParent.getDispatcherDtls().getPin();
					}
					model.addAttribute("headText", "Details Of Dispatcher");
				}else {
					model.addAttribute("c_ship_address", "");
				}
				model.addAttribute("c_ship_address", shipaddr);
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getGstin())) {
					model.addAttribute("c_ship_gstin", invoiceParent.getShipmentDtls().getGstin());
				}else {
					model.addAttribute("c_ship_gstin", "");
				}
			}
			if(isNotEmpty(invoiceParent.getShipmentDtls()) && isNotEmpty(invoiceParent.getShipmentDtls().getAddr1()) && 
					isNotEmpty(invoiceParent.getDispatcherDtls()) && isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1())) {
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getTrdNm())) {
					model.addAttribute("c_ship_name", invoiceParent.getShipmentDtls().getTrdNm());
				}else {
					model.addAttribute("c_ship_name","");
				}
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getGstin())) {
					model.addAttribute("c_ship_gstin", invoiceParent.getShipmentDtls().getGstin());
				}else {
					model.addAttribute("c_ship_gstin", "");
				}
				if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls()) && NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getNm())) {
					model.addAttribute("c_disp_name", invoiceParent.getDispatcherDtls().getNm());
				}else {
					model.addAttribute("c_disp_name","");
				}
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getAddr1())) {
					String shipaddr = "";
					shipaddr = invoiceParent.getShipmentDtls().getAddr1();
					if(isNotEmpty(invoiceParent.getShipmentDtls().getAddr2())) {
						shipaddr = shipaddr+','+ invoiceParent.getShipmentDtls().getAddr2();
					}
					if(isNotEmpty(invoiceParent.getShipmentDtls().getLoc())) {
						shipaddr = shipaddr+','+ invoiceParent.getShipmentDtls().getLoc();
					}
					if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getStcd())) {
						if(invoiceParent.getShipmentDtls().getStcd().contains("-")) {
							shipaddr = shipaddr+','+ invoiceParent.getShipmentDtls().getStcd().substring(3);
						}else {
							String statename = invoiceParent.getShipmentDtls().getStcd();
							List<StateConfig> states = configService.getStates();
							for (StateConfig state : states) {
								String name = state.getName();
								String[] nm = state.getName().split("-");
								if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
									statename = name;
									break;
								}else if((nm[0].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
									statename = name;
									break;
								}
							}
							if(statename.contains("-")) {
								statename = statename.substring(3);
							}
							shipaddr = shipaddr+','+ statename;
						}
					}
					if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getPin())) {
						shipaddr = shipaddr+'-'+ invoiceParent.getShipmentDtls().getPin();
					}
					model.addAttribute("c_ship_address", shipaddr);
				}else {
					model.addAttribute("c_ship_address", "");
				}
				if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls()) && NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1())) {
					String shipaddr = "";
					shipaddr = invoiceParent.getDispatcherDtls().getAddr1();
					if(isNotEmpty(invoiceParent.getDispatcherDtls().getAddr2())) {
						shipaddr = shipaddr+','+ invoiceParent.getDispatcherDtls().getAddr2();
					}
					if(isNotEmpty(invoiceParent.getDispatcherDtls().getLoc())) {
						shipaddr = shipaddr+','+ invoiceParent.getDispatcherDtls().getLoc();
					}
					if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getStcd())) {
						if(invoiceParent.getDispatcherDtls().getStcd().contains("-")) {
							shipaddr = shipaddr+','+ invoiceParent.getDispatcherDtls().getStcd().substring(3);
						}else {
							String statename = invoiceParent.getDispatcherDtls().getStcd();
							List<StateConfig> states = configService.getStates();
							for (StateConfig state : states) {
								String name = state.getName();
								String[] nm = state.getName().split("-");
								if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
									statename = name;
									break;
								}else if((nm[0].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
									statename = name;
									break;
								}
							}
							if(statename.contains("-")) {
								statename = statename.substring(3);
							}
							shipaddr = shipaddr+','+ statename;
						}
					}
					if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getPin())) {
						shipaddr = shipaddr+'-'+ invoiceParent.getDispatcherDtls().getPin();
					}
					model.addAttribute("c_disp_address", shipaddr);
				}else {
					model.addAttribute("c_disp_address", "");
				}
			}
			if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getAuthSignText())) {
				model.addAttribute("signatureText", pconfig.getAuthSignText());
			}else {
				model.addAttribute("signatureText", "Authorised Signature");
			}
			if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getEinvoiceHeaderText())) {
				model.addAttribute("einvoiceheaderText", pconfig.getEinvoiceHeaderText());
			}else {
				model.addAttribute("einvoiceheaderText", "");
			}
			if(isNotEmpty(invoiceParent.getTermDays())) {
				if(!invoiceParent.getTermDays().equalsIgnoreCase("0")) {
					String term = "Net "+invoiceParent.getTermDays()+" days";
					if(isNotEmpty(invoiceParent.getDueDate())) {
						term = term+" - " +new SimpleDateFormat("dd/MM/yyyy").format(invoiceParent.getDueDate()); 
					}
					invoiceParent.setTerms(term);
				}
			}
			if(isNotEmpty(invoiceParent.getAckNo())){
				model.addAttribute("ackno",invoiceParent.getAckNo());
			}else {
				model.addAttribute("ackno","");
			}
			if(isNotEmpty(invoiceParent.getAckDt())){
				model.addAttribute("ackdt",invoiceParent.getAckDt());
			}else {
				model.addAttribute("ackdt","");
			}
			if(isNotEmpty(invoiceParent.getIgstOnIntra())) {
				if(invoiceParent.getIgstOnIntra().equals("Y")) {
					model.addAttribute("igstonintra", "Yes");
				}else {
					model.addAttribute("igstonintra", "No");
				}
			}else {
				model.addAttribute("igstonintra", "");
			}
			if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getInvtype())) {
				if("B2B".equalsIgnoreCase(invoiceParent.getInvtype())) {
					model.addAttribute("supply","B2B");
				}else if(invoiceParent.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
					model.addAttribute("supply","CDN");
				}else if(invoiceParent.getInvtype().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
					model.addAttribute("supply","EXP");
				}
			}
		model.addAttribute("printDate", new SimpleDateFormat("dd-MM-YYY hh:mm:ss a").format(new Date()));
		model.addAttribute("custAddress", custAddress);
		model.addAttribute("invoiceNumberText", invNumberText);
		model.addAttribute("invoiceDateText", invDateText);
		model.addAttribute("invText", invText);
		model.addAttribute("invoice", invoiceParent);
		model.addAttribute("amountinwords", amountinwords);
		model.addAttribute("amountcurrency", amountcurrency);
		CustomFields customFields = customFieldsRepository.findByClientid(clientid);
		if(isNotEmpty(customFields)) {
			model.addAttribute("customFieldList", customFields.getEinvoice());
		}
		
		if(isNotEmpty(invoiceParent.getEntertaimentprintto()) && invoiceParent.getEntertaimentprintto().equalsIgnoreCase("Producer")) {
			if(isNotEmpty(invoiceParent.getCustomFieldText1()) && invoiceParent.getCustomFieldText1().equalsIgnoreCase("Picture Name")) {
				model.addAttribute("CustomField1", invoiceParent.getCustomFieldText1());
				model.addAttribute("CustomField1Val", invoiceParent.getCustomField1());
			}else if(isNotEmpty(invoiceParent.getCustomFieldText2()) && invoiceParent.getCustomFieldText2().equalsIgnoreCase("Picture Name")) {
				model.addAttribute("CustomField1", invoiceParent.getCustomFieldText2());
				model.addAttribute("CustomField1Val", invoiceParent.getCustomField2());
			}else if(isNotEmpty(invoiceParent.getCustomFieldText3()) && invoiceParent.getCustomFieldText3().equalsIgnoreCase("Picture Name")) {
				model.addAttribute("CustomField1", invoiceParent.getCustomFieldText3());
				model.addAttribute("CustomField1Val", invoiceParent.getCustomField3());
			}else if(isNotEmpty(invoiceParent.getCustomFieldText4()) && invoiceParent.getCustomFieldText4().equalsIgnoreCase("Picture Name")) {
				model.addAttribute("CustomField1", invoiceParent.getCustomFieldText4());
				model.addAttribute("CustomField1Val", invoiceParent.getCustomField4());
			}
		}
		
		logger.debug(CLASSNAME + method + END);
		if(isNotEmpty(usr) && isNotEmpty(usr.getEmail()) && (usr.getEmail().endsWith("@oceansparkle.in") || usr.getEmail().endsWith("@kvrco.in"))) {
			return "einvoice/einvoice_print_osl";
		}else if(isNotEmpty(usr) && isNotEmpty(usr.getEmail()) && (usr.getEmail().endsWith("@fhpl.net") || usr.getEmail().endsWith("@apollohealthresources.com"))){
			return "einvoice/einvoice_print_fhpl";
		}else if(isNotEmpty(usr) && isNotEmpty(usr.isAccessEntertainmentEinvoiceTemplate()) && usr.isAccessEntertainmentEinvoiceTemplate()) {
			return "einvoice/einvoice_print_al";
		}else {
			return "einvoice/einvoice_print";
		}
	}
	
	
			/*
			* Bar code generate example
			*/
			/*@RequestMapping("/qr_image.jpg")
			private void createQRImage(HttpServletResponse response) throws WriterException, IOException {
				String qrCodeText = "This is Your Sample E-invoice";
				String filePath = "png";
				int size = 160;
				String fileType = "png";
				//System.out.println("DONE");
				ServletOutputStream out = response.getOutputStream();
			
				// Create the ByteMatrix for the QR-Code that encodes the given String
				Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
				hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
				QRCodeWriter qrCodeWriter = new QRCodeWriter();
				BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
				// Make the BufferedImage that are to hold the QRCode
				int matrixWidth = byteMatrix.getWidth();
				BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
				image.createGraphics();
			
				Graphics2D graphics = (Graphics2D) image.getGraphics();
				graphics.setColor(Color.WHITE);
				graphics.fillRect(0, 0, matrixWidth, matrixWidth);
				// Paint and save the image using the ByteMatrix
				graphics.setColor(Color.BLACK);
			
				for (int i = 0; i < matrixWidth; i++) {
					for (int j = 0; j < matrixWidth; j++) {
						if (byteMatrix.get(i, j)) {
							graphics.fillRect(i, j, 1, 1);
						}
					}
				}
				
				
				ImageIO.write(image, fileType, out);				
			}
			*/
		@RequestMapping("{id}/qrImage.jpg")
		private void createQRImage(@PathVariable("id") String id,HttpServletResponse response) throws Exception{
			InvoiceParent invoiceParent = clientService.getInvoice(id, "GSTR1");
			BufferedImage image = null;
			ServletOutputStream out = response.getOutputStream();
			if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getSignedQrCode())) {
				image = generateQRCodeImage(invoiceParent.getSignedQrCode());
			}
			
	        //File outputfile = new File("D:/qrcode.png");
	        ImageIO.write(image, "png", out);
		}
		
		@RequestMapping("b2c/{id}/{clientid}/qrImage.jpg")
		private void createB2CQRImage(@PathVariable("id") String id,@PathVariable("clientid") String clientid,HttpServletResponse response) throws Exception{
			InvoiceParent invoiceParent = clientService.getInvoice(id, "GSTR1");
			BufferedImage image = null;
			ServletOutputStream out = response.getOutputStream();
			String url = "";
			if(isNotEmpty(invoiceParent.getBankDetails()) && isNotEmpty(invoiceParent.getBankDetails().getAccountnumber()) && isNotEmpty(invoiceParent.getBankDetails().getIfsccode()) && isNotEmpty(invoiceParent.getBankDetails().getAccountName())) {
				Client client = clientService.findById(clientid);
				String invdate = "";
				if(isNotEmpty(invoiceParent.getDateofinvoice())) {
					invdate = new SimpleDateFormat("dd-MM-yyyy").format(invoiceParent.getDateofinvoice());
				}
				url = "upi://pay?pa="+invoiceParent.getBankDetails().getAccountnumber()+"@"+invoiceParent.getBankDetails().getIfsccode()+".ifsc.npci&pn="+invoiceParent.getBankDetails().getAccountName()+"&mc=0000&tr="+invoiceParent.getInvoiceno()+"&am="+invoiceParent.getTotalamount_str()+"&mam="+invoiceParent.getTotalamount_str()+"&cu=INR&mode=01&b2cSellerGstin="+client.getGstnnumber()+"&b2cUPIID="+invoiceParent.getBankDetails().getAccountnumber()+"@"+invoiceParent.getBankDetails().getIfsccode()+"&b2cBankAcNo="+invoiceParent.getBankDetails().getAccountnumber()+"&b2cIFSCCode="+invoiceParent.getBankDetails().getIfsccode()+"&b2cInvNo="+invoiceParent.getInvoiceno()+"&b2cInvDate="+invdate+"&b2cGSTAmount="+invoiceParent.getTotaltax()+"&b2cCGSTAmount="+invoiceParent.getTotalCgstAmount()+"&b2cSGSTAmount="+invoiceParent.getTotalSgstAmount()+"&b2cIGSTAmount="+invoiceParent.getTotalIgstAmount()+"&b2cCESS="+invoiceParent.getTotalCessAmount()+"&size=150";
				//url="upi://pay?pa=50100319996810@HDFC0004154.ifsc.npci&pn=BRIJESH REDDY VISAVARAM&mc=0000&tr=ABC12345&am=10&mam=10&cu=INR&mode=01&b2cSellerGstin=29AABCT1332L000&b2cUPIID=null&b2cBankAcNo=50100319996810&b2cIFSCCode=HDFC0004154&b2cInvNo=ABC12345&b2cInvDate=25-03-2021&b2cCGSTAmount=0&b2cSGSTAmount=0&b2cIGSTAmount=10&b2cCESS=0&size=150";
				image = generateQRCodeImage(url);
			}
			
			//if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getSignedQrCode())) {
				
			//}
			
	        //File outputfile = new File("D:/qrcode.png");
	        ImageIO.write(image, "png", out);
		}
		    public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
		        QRCodeWriter barcodeWriter = new QRCodeWriter();
		        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 160, 160);    
		        return MatrixToImageWriter.toBufferedImage(bitMatrix);
		    }
			@RequestMapping(value = "/einvoiceprintpdf/{id}/{name}/{usertype}/{clientid}/{returntype}/{invId}", method = RequestMethod.GET,produces = MediaType.APPLICATION_PDF_VALUE)
			public HttpEntity<byte[]>  printEwayBillInvoicePdf(@PathVariable("id") String id, @PathVariable("name") String fullname,
					@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
					@PathVariable("invId") String invId, @PathVariable("returntype") String returntype, ModelMap model)
							throws Exception {
				logger.debug("START :: printEInvoicePdf");
				try {
					Client client = clientService.findById(clientid);
					User usr = userService.findById(id);
					InvoiceParent invoiceParent = clientService.getInvoice(invId, "GSTR1");
					String filename = invoiceParent.getInvoiceno()+"_";
					if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getB2b()) && isNotEmpty(invoiceParent.getB2b().get(0))&& isNotEmpty(invoiceParent.getB2b().get(0).getCtin())) {
						filename += invoiceParent.getB2b().get(0).getCtin()+"_";
					}
					if(isNotEmpty(invoiceParent.getDateofinvoice())) {
						filename  += new SimpleDateFormat("dd-MM-yyyy").format(invoiceParent.getDateofinvoice());
					} 
					// Set param values.
					Map<String, Object> params = printService.getReportParams(client, invoiceParent,returntype);
					PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
					String xmlPath = "classpath:/report/invoice";
					if(isNotEmpty(usr) && isNotEmpty(usr.getEmail()) && (usr.getEmail().endsWith("@oceansparkle.in") || usr.getEmail().endsWith("@kvrco.in"))) {
						if((Boolean)params.get("isIGST") && !(Boolean)params.get("isCGST")) {
							if(invoiceParent.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
								if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
									xmlPath = "classpath:/report/invoice-einv-igst-currency-OSL";
								}else {
									xmlPath = "classpath:/report/invoice-einv-igst-OSL";
								}
							}else {
								xmlPath = "classpath:/report/invoice-einv-igst-OSL";
							}
						} else if(!(Boolean)params.get("isIGST") && (Boolean)params.get("isCGST")) {
							xmlPath = "classpath:/report/invoice-einv-cgst-OSL";
						}else {
							if(invoiceParent.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
								if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
									xmlPath = "classpath:/report/invoice-einv-currency-OSL";
								}else {
									xmlPath = "classpath:/report/invoice-einv-OSL";
								}
							}else {
								xmlPath = "classpath:/report/invoice-einv-OSL";
							}
						}
					}else if(isNotEmpty(usr) && isNotEmpty(usr.getEmail()) && (usr.getEmail().endsWith("@fhpl.net") || usr.getEmail().endsWith("@apollohealthresources.com"))){
						if((Boolean)params.get("isIGST") && !(Boolean)params.get("isCGST")) {
							if(invoiceParent.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
								if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
									xmlPath = "classpath:/report/invoice-einv-igst-currency-FHPL";
								}else {
									xmlPath = "classpath:/report/invoice-einv-igst-FHPL";
								}
							}else {
								xmlPath = "classpath:/report/invoice-einv-igst-FHPL";
							}
						} else if(!(Boolean)params.get("isIGST") && (Boolean)params.get("isCGST")) {
							xmlPath = "classpath:/report/invoice-einv-cgst-FHPL";
						}else {
							if(invoiceParent.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
								if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
									xmlPath = "classpath:/report/invoice-einv-currency-FHPL";
								}else {
									xmlPath = "classpath:/report/invoice-einv-FHPL";
								}
							}else {
								xmlPath = "classpath:/report/invoice-einv-FHPL";
							}
						}
					}else {
						if((Boolean)params.get("isIGST") && !(Boolean)params.get("isCGST")) {
							if(invoiceParent.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
								if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
									xmlPath = "classpath:/report/invoice-einv-igst-currency";
								}else {
									xmlPath = "classpath:/report/invoice-einv-igst";
								}
							}else {
								xmlPath = "classpath:/report/invoice-einv-igst";
							}
						} else if(!(Boolean)params.get("isIGST") && (Boolean)params.get("isCGST")) {
							xmlPath = "classpath:/report/invoice-einv-cgst";
						}else {
							if(invoiceParent.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
								if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
									xmlPath = "classpath:/report/invoice-einv-currency";
								}else {
									xmlPath = "classpath:/report/invoice-einv";
								}
							}else {
								xmlPath = "classpath:/report/invoice-einv";
							}
						}
					}
					
					if(invoiceParent.isTdstcsenable()) {
						xmlPath += "-tcs";
					}
					if(isNotEmpty(invoiceParent.getShipmentDtls()) && isNotEmpty(invoiceParent.getShipmentDtls().getAddr1()) || 
							isNotEmpty(invoiceParent.getDispatcherDtls()) && isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1())) {
						xmlPath += "-ship";
						if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getTrdNm())) {
							params.put("c_ship_name", invoiceParent.getShipmentDtls().getTrdNm());
						}else if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls()) && NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getNm())) {
							params.put("c_ship_name", invoiceParent.getDispatcherDtls().getNm());
						}else {
							params.put("c_ship_name", "");
						}
						if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getAddr1())) {
							String shipaddr ="";
							shipaddr = invoiceParent.getShipmentDtls().getAddr1();
							if(isNotEmpty(invoiceParent.getShipmentDtls().getAddr2())) {
								shipaddr = shipaddr+','+ invoiceParent.getShipmentDtls().getAddr2();
							}
							if(isNotEmpty(invoiceParent.getShipmentDtls().getLoc())) {
								shipaddr = shipaddr+','+ invoiceParent.getShipmentDtls().getLoc();
							}
							if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getStcd())) {
								if(invoiceParent.getShipmentDtls().getStcd().contains("-")) {
									shipaddr = shipaddr+','+ invoiceParent.getShipmentDtls().getStcd().substring(3);
								}else {
									String statename = invoiceParent.getShipmentDtls().getStcd();
									List<StateConfig> states = configService.getStates();
									for (StateConfig state : states) {
										String name = state.getName();
										String[] nm = state.getName().split("-");
										if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
											statename = name;
											break;
										}else if((nm[0].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
											statename = name;
											break;
										}
									}
									shipaddr = shipaddr+','+ statename;
								}
							}
							if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getPin())) {
								shipaddr = shipaddr+'-'+ invoiceParent.getShipmentDtls().getPin();
							}
							params.put("c_ship_address", shipaddr);
							params.put("headText", "Details Of Shipment");
						}else if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls()) && NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1())) {
							String shipaddr ="";
							shipaddr = invoiceParent.getDispatcherDtls().getAddr1();
							if(isNotEmpty(invoiceParent.getDispatcherDtls().getAddr2())) {
								shipaddr = shipaddr+','+ invoiceParent.getDispatcherDtls().getAddr2();
							}
							if(isNotEmpty(invoiceParent.getDispatcherDtls().getLoc())) {
								shipaddr = shipaddr+','+ invoiceParent.getDispatcherDtls().getLoc();
							}
							if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getStcd())) {
								if(invoiceParent.getDispatcherDtls().getStcd().contains("-")) {
									shipaddr = shipaddr+','+ invoiceParent.getDispatcherDtls().getStcd().substring(3);
								}else {
									String statename = invoiceParent.getDispatcherDtls().getStcd();
									List<StateConfig> states = configService.getStates();
									for (StateConfig state : states) {
										String name = state.getName();
										String[] nm = state.getName().split("-");
										if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
											statename = name;
											break;
										}else if((nm[0].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
											statename = name;
											break;
										}
									}
									shipaddr = shipaddr+','+ statename;
								}
							}
							if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getPin())) {
								shipaddr = shipaddr+'-'+ invoiceParent.getDispatcherDtls().getPin();
							}
							params.put("c_ship_address", shipaddr);
							params.put("headText", "Details Of Dispatcher");
						}else {
							params.put("c_ship_address", "");
						}
					}
					if(isNotEmpty(invoiceParent.getShipmentDtls()) && isNotEmpty(invoiceParent.getShipmentDtls().getAddr1()) && isNotEmpty(invoiceParent.getDispatcherDtls())&& isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1())) {
						xmlPath += "-disp";
					}
					if(isNotEmpty(pconfig) && isNotEmpty(pconfig.isEnableRoundOffAmt()) && pconfig.isEnableRoundOffAmt()) {
						xmlPath += "-roff";
					}
					if(isNotEmpty(invoiceParent.getEntertaimentprintto())) {
						if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getEinvoiceHeaderText())) {
							params.put("einvheadertext", pconfig.getEinvoiceHeaderText());
						}else {
							if(isNotEmpty(client) && isNotEmpty(client.getBusinessname())) {
								params.put("einvheadertext", client.getBusinessname());
							}else {
								params.put("einvheadertext", "");
							}
						}
						if(invoiceParent.getEntertaimentprintto().equalsIgnoreCase("Exhibitor")) {
							xmlPath += "-al";
						}else {
							xmlPath += "-al-d";
						}
					}
					if(!xmlPath.endsWith("jrxml")) {
						xmlPath += ".jrxml";
					}
					Resource config = resourceLoader.getResource(xmlPath);
					// get report file and then load into jasperDesign
					JasperDesign jasperDesign = JRXmlLoader.load(config.getInputStream());
					// compile the jasperDesign
					JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
					if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getInvoiceText())) {
						params.put("title", pconfig.getInvoiceText());
					} else {
						params.put("title", "INVOICE");
					}
					if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getQtyText())) {
						params.put("invqty", pconfig.getQtyText());
					} else {
						params.put("invqty", "Qty");
					}
					if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getRateText())) {
						params.put("invrate", pconfig.getRateText());
					} else {
						params.put("invrate", "Rate");
					}
					if(isNotEmpty(usr) && isNotEmpty(usr.getEmail()) && (usr.getEmail().endsWith("@oceansparkle.in") || usr.getEmail().endsWith("@kvrco.in"))) {
						if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getIrnNo())) {
							params.put("irnNoText","IRN : ");
						}else {
							params.put("irnNoText","");
						}
					}else {
						if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getIrnNo())) {
							params.put("irnNoText","IRN No : ");
						}else {
							params.put("irnNoText","");
						}
					}
					params.put(JRParameter.REPORT_LOCALE, new Locale("en", "IN"));
					if(isNotEmpty(invoiceParent.getEntertaimentprintto())) {
						JRGzipVirtualizer jrGzipVirtualizer = null;
						if(jrGzipVirtualizer == null){
							jrGzipVirtualizer = new JRGzipVirtualizer(100);
							jrGzipVirtualizer.setReadOnly(true);
						}
						if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getItems()) && invoiceParent.getItems().size() > 5) {
							params.put(JRParameter.REPORT_VIRTUALIZER, jrGzipVirtualizer);
							params.put(JRParameter.IS_IGNORE_PAGINATION, false);
						}else {
							params.put(JRParameter.IS_IGNORE_PAGINATION, true);
						}
					}
					JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params,
							new JRBeanCollectionDataSource(invoiceParent.getItems()));
					// export to pdf
					byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
					HttpHeaders header = new HttpHeaders();
					header.setContentType(MediaType.APPLICATION_PDF);
					header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename="+filename+".pdf");
					header.setContentLength(data.length);
					logger.debug("END :: printEInvoicePdf");
					return new HttpEntity<byte[]>(data, header);
				} catch (Exception e) {
					logger.error("printEInvoicePdf :: id "+id, e);
					return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
				}
			}
	@RequestMapping(value = "/saveEinvoiceconfig/{clientid}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public @ResponseBody EinvoiceConfigurations saveEinvoiceConfig(@RequestBody MultiValueMap<String, String> ebillData,
			@PathVariable("clientid") String clientid) throws UnsupportedEncodingException, UnknownHostException {
		final String method = "saveEinvoiceConfig::";
		logger.debug(CLASSNAME + method + BEGIN);
		EinvoiceConfigurations einvform = new EinvoiceConfigurations();
		Client client = clientService.findById(clientid);
		
		einvform.setUserName(ebillData.getFirst("username"));
		einvform.setPassword(Base64.getEncoder().encodeToString(ebillData.getFirst("password").getBytes(MasterGSTConstants.PASSWORD_ENCODE_FORMAT)));
		einvform.setClientid(clientid);
		String msg = null;	
		Response response = iHubConsumerService.authenticateEinvoiceAPI(client.getGstnnumber(), InetAddress.getLocalHost().getHostAddress(), ebillData.getFirst("username"), ebillData.getFirst("password"));
		if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) && (response.getStatuscd().equalsIgnoreCase("Sucess") || response.getStatuscd().equalsIgnoreCase(MasterGSTConstants.SUCCESS) || response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE))) {
			einvform.setConnStaus("Active");
		}else {
			einvform.setConnStaus("InActive");
			String jsonStr="";
			if(isNotEmpty(response) && isNotEmpty(response.getStatusdesc())) {
		        jsonStr = response.getStatusdesc();
		        JSONArray jsonObj = new JSONArray(jsonStr);
				JSONObject object=(JSONObject) jsonObj.get(0);
				msg = object.getString("ErrorMessage");
			}
		}
		EinvoiceConfigurations configs = einvoiceService.saveEinvoiceConfigurations(einvform);
		if(isNotEmpty(configs) && isNotEmpty(msg)){
			configs.setResponse(msg);
		}
		return configs;
	}
	
	 @RequestMapping(value = "/authEInvoiceConfig/{clientid}", method = RequestMethod.POST)
		public @ResponseBody String authEinvconfigdetails(@RequestBody MultiValueMap<String, String> ebillData,
				@PathVariable("clientid") String clientid, ModelMap model) throws Exception {
			final String method = "authEinvconfigdetails::";
			logger.debug(CLASSNAME + method + BEGIN);
			Client client = clientService.findById(clientid);
			EinvoiceConfigurations einvform = new EinvoiceConfigurations();
			einvform.setUserName(ebillData.getFirst("username"));
			einvform.setPassword(Base64.getEncoder().encodeToString(ebillData.getFirst("password").getBytes(MasterGSTConstants.PASSWORD_ENCODE_FORMAT)));
			einvform.setClientid(clientid);
				Response response = iHubConsumerService.authenticateEinvoiceAPI(client.getGstnnumber(), InetAddress.getLocalHost().getHostAddress(), ebillData.getFirst("username"), ebillData.getFirst("password"));
				ErrorCodeConfig errors=null;String desc="";
				if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) && (response.getStatuscd().equalsIgnoreCase("Sucess") || response.getStatuscd().equalsIgnoreCase(MasterGSTConstants.SUCCESS) || response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE))) {
						einvform.setConnStaus("Active");
						model.addAttribute("einvflag", response.getError());
					}else {
						einvform.setConnStaus("InActive");
						String jsonStr="";
						if(isNotEmpty(response) && isNotEmpty(response.getStatusdesc())) {
					        jsonStr = response.getStatusdesc();
					        JSONArray jsonObj = new JSONArray(jsonStr);
							JSONObject object=(JSONObject) jsonObj.get(0);
							desc = object.getString("ErrorMessage");
						}
					
					}
					einvoiceService.saveEinvoiceConfigurations(einvform);
					return desc;
				
		}
	 
	 @RequestMapping(value = "/saveeinvoiceasdraft/{returntype}/{usertype}/{month}/{year}", method = RequestMethod.POST)
		public String savereinvoiceasdraft(@ModelAttribute("einvoice")GSTR1 einvoice,
				@PathVariable("returntype") String returntype, @PathVariable("usertype") String usertype,
				@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
			final String method = "saverewaybillinvoiceasdraft::";
			logger.debug(CLASSNAME + method + BEGIN);
			logger.debug(CLASSNAME + method + "id {}", einvoice.getId());
			String action = einvoice.getId() != null ? AuditLogConstants.EDITANDSAVE : AuditLogConstants.SAVEASDRAFT;
			String invoiceNumber = einvoice.getB2b() != null ? einvoice.getB2b().get(0) != null ? einvoice.getB2b().get(0).getInv() != null ? einvoice.getB2b().get(0).getInv().get(0).getInum() != null ? einvoice.getB2b().get(0).getInv().get(0).getInum() : "" : "" : "" : "";
			InvoiceParent oldinvoice = null;
			if(action.equalsIgnoreCase(AuditLogConstants.EDITANDSAVE)) {
				oldinvoice = gstr1Repository.findOne(einvoice.getId().toString());
			}
			einvoice.setGstr1orEinvoice("Einvoice");
			if(isNotEmpty(einvoice) && isNotEmpty(einvoice.getIrnStatus())) {
				if(einvoice.getIrnStatus().equalsIgnoreCase(MasterGSTConstants.GST_STATUS_CANCEL)) {
					einvoice.setGstStatus(MasterGSTConstants.GST_STATUS_CANCEL);
				}else if(einvoice.getIrnStatus().equalsIgnoreCase(MasterGSTConstants.GENERATED)){
					einvoice.setGstStatus(MasterGSTConstants.GST_STATUS_SUCCESS);
				}
			}
			saveInvoice(einvoice, returntype, usertype, month, year, model);
			if(action.equalsIgnoreCase(AuditLogConstants.SAVEINVOICE)) {
				auditlogService.saveAuditLog(einvoice.getUserid(), einvoice.getClientid(),invoiceNumber,action,MasterGSTConstants.EINVOICE,null,null);
			}else {
				auditlogService.saveAuditLog(einvoice.getUserid(), einvoice.getClientid(),invoiceNumber,action,MasterGSTConstants.EINVOICE,oldinvoice,einvoice);
			}
			return "redirect:/einvoice/" +einvoice.getUserid() + "/" + einvoice.getFullname() + "/" + usertype + "/"
			+ returntype + "/" +  einvoice.getClientid() + "/" + month + "/" + year;
		}
	 
		public void saveInvoice(InvoiceParent invoice, String returntype, String usertype, int month, int year,
				ModelMap model) throws Exception {
			final String method = "saveInvoice::";
			logger.debug(CLASSNAME + method + BEGIN);
			if (isNotEmpty(invoice.getDateofinvoice())) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(invoice.getDateofinvoice());
				month = cal.get(Calendar.MONTH) + 1;
				year = cal.get(Calendar.YEAR);
			}
			updateModel(model, invoice.getUserid(), invoice.getFullname(), usertype, month, year);

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
					if(invoice.getInvtype().equals(MasterGSTConstants.B2B)) {
						invoice.setInvtype(MasterGSTConstants.B2BA);
					} else if(invoice.getInvtype().equals(MasterGSTConstants.B2CL)) {
						invoice.setInvtype(MasterGSTConstants.B2CLA);
					} else if(invoice.getInvtype().equals(MasterGSTConstants.B2C)) {
						invoice.setInvtype(MasterGSTConstants.B2CSA);
					} else if(invoice.getInvtype().equals(MasterGSTConstants.ADVANCES)) {
						invoice.setInvtype(MasterGSTConstants.TXPA);
					} else if(invoice.getInvtype().equals(MasterGSTConstants.ATPAID)) {
						invoice.setInvtype(MasterGSTConstants.ATA);
					} else if(invoice.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
						invoice.setInvtype(MasterGSTConstants.EXPA);
					} else if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
						invoice.setInvtype(MasterGSTConstants.CDNA);
					} else if(invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
						invoice.setInvtype(MasterGSTConstants.CDNURA);
					}
				}
				if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
					if(isEmpty(invoice.getB2b().get(0).getInv().get(0).getOinum())) {
						invoice.setGstStatus("");
						invoice.getB2b().get(0).getInv().get(0).setOinum(invoice.getB2b().get(0).getInv().get(0).getInum());
						invoice.getB2b().get(0).getInv().get(0).setOidt(invoice.getB2b().get(0).getInv().get(0).getIdt());
					}
				}
				if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0).getNt())) {
					if(isEmpty(invoice.getCdn().get(0).getNt().get(0).getOntNum())) {
						invoice.setGstStatus("");
						invoice.getCdn().get(0).getNt().get(0).setOntNum(invoice.getCdn().get(0).getNt().get(0).getNtNum());
						invoice.getCdn().get(0).getNt().get(0).setOntDt(invoice.getCdn().get(0).getNt().get(0).getNtDt());
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
					if (isNotEmpty(exps) && isNotEmpty(exps.getExpTyp())) {
						if (exps.getExpTyp().equals("WPAY")) {
							isIntraState = false;
						} else {
							isIntraState = true;
						}
					}
				}
			}

			String strMonth = month < 10 ? "0" + month : month + "";
			invoice.setFp(strMonth + year);
			
			InvoiceParent invoiceForJournal;
				invoice = einvoiceService.populateEinvType(invoice);
				invoiceForJournal = einvoiceService.saveSalesInvoice(invoice, isIntraState);
			
			logger.debug(CLASSNAME + method + END);
		}
		
		@RequestMapping(value = "/getEInvs/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
		public @ResponseBody String getAdditionalInvoices(@PathVariable("id") String id, @PathVariable("returntype") String returntype, 
				@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year,  @RequestParam(value = "booksOrReturns", required = false)String booksOrReturns,
				ModelMap model, HttpServletRequest request) throws Exception {
			final String method = "getInvoices::";
			logger.debug(CLASSNAME + method + BEGIN);
			String st = request.getParameter("start");
			InvoiceFilter filter = clientService.invoiceFilter(request);
			
			int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
			String len = request.getParameter("length");
			int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
			String searchVal = request.getParameter("search[value]");
			if(searchVal == null || "".equals(searchVal.trim())){
				searchVal = null;
			}
			String sortParam = "dateofinvoice";
			String sortOrder = "asc";
			sortOrder = request.getParameter("order[0][dir]");
			
			Client client = clientService.findById(clientid);
			String retType = returntype;
			Pageable pageable = null;
			Map<String, Object> invoicesMap = einvoiceService.getEInvoices(pageable, client, id, retType, "notreports", month, year, start, length, searchVal,sortParam,sortOrder, filter, true);
			Page<? extends InvoiceParent> invoices = (Page<? extends InvoiceParent>)invoicesMap.get("invoices");
			if(isNotEmpty(invoices)) {
				for(InvoiceParent invoiceParent : invoices) {
					invoiceParent.setUserid(invoiceParent.getId().toString());
				}
			} 
			
			/*Map<String, Object> invoiceData = new HashMap<>();
			invoiceData.put("data", invoices);
			invoiceData.put("recordsFiltered", invoices.getTotalElements());
			invoiceData.put("recordsTotal", invoices.getTotalElements());
			invoiceData.put("draw", request.getParameter("draw"));*/
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(MapperFeature.USE_ANNOTATIONS);
			ObjectWriter writer=null;

				writer=mapper.writer();
			
			return writer.writeValueAsString(invoicesMap);
		}
		
		
		@RequestMapping(value = "/getAddtionalCustomEInvs/{id}/{clientid}/{returntype}/{fromtime}/{totime}", method = RequestMethod.GET)
		public @ResponseBody String getAdditionalCustomInvoices(@PathVariable("id") String id, @PathVariable("returntype") String returntype, 
				@PathVariable("clientid") String clientid, @PathVariable("fromtime")String fromtime, @PathVariable("totime")String totime, @RequestParam("booksOrReturns")String booksOrReturns,
				ModelMap model, HttpServletRequest request) throws Exception {
			final String method = "getInvoices::";
			logger.debug(CLASSNAME + method + BEGIN);
			String st = request.getParameter("start");
			InvoiceFilter filter = clientService.invoiceFilter(request);
	
			int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
			String len = request.getParameter("length");
			int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
			String searchVal = request.getParameter("search[value]");
			if(searchVal == null || "".equals(searchVal.trim())){
				searchVal = null;
			}
			
			Client client = clientService.findById(clientid);
			String retType = returntype;
			Pageable pageable = null;
			Map<String, Object> invoicesMap = einvoiceService.getCustomInvoices(pageable, client, id, retType, "notreports", fromtime, totime, start, length, searchVal, filter, true,booksOrReturns);
			Page<? extends InvoiceParent> invoices = (Page<? extends InvoiceParent>)invoicesMap.get("invoices");
			if(isNotEmpty(invoices)) {
				for(InvoiceParent invoiceParent : invoices) {
					invoiceParent.setUserid(invoiceParent.getId().toString());
				}
			} 
					
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(MapperFeature.USE_ANNOTATIONS);
			ObjectWriter writer=null;

				writer=mapper.writer();
			return writer.writeValueAsString(invoicesMap);
		}
		
		@RequestMapping(value = "/allInvgenerateIRN/{id}/{clientid}/{returntype}/{usertype}/{month}/{year}", method = RequestMethod.POST)
		public @ResponseBody String allInvgenerateIRN(@RequestBody List<String> invoiceIds,@PathVariable("id") String userid,@PathVariable("clientid") String clientid,@PathVariable("returntype") String returntype, @PathVariable("usertype") String usertype,
				@PathVariable("month") int month, @PathVariable("year") int year,ModelMap model) throws Exception {
				final String method = "allInvgenerateIRN::";
				Client client = clientService.findById(clientid);
				InvoiceParent invoice = null;
				GenerateIRNResponse eresponse = null;
				for(String id : invoiceIds) {
					GSTR1 invoice1 = gstr1Repository.findOne(id);
					if(isNotEmpty(invoice1)) {
						if(isNotEmpty(invoice1.getInvtype()) && !"B2C".equalsIgnoreCase(invoice1.getInvtype())) {
							if(isEmpty(invoice1.getIrnNo())) {
								BuyerDetails buydetails = invoice1.getBuyerDtls();
								DispatcherDetails dispatchDetails = invoice1.getDispatcherDtls();
								ShipmentDetails shipmentDetails = invoice1.getShipmentDtls();
								invoice = einvoiceService.populateEinvoiceDetails(invoice1, client);
								eresponse = iHubConsumerService.generateIRN(client.getGstnnumber(), InetAddress.getLocalHost().getHostAddress(), invoice);
									if(isNotEmpty(eresponse) && isNotEmpty(eresponse.getStatuscd()) && (eresponse.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE) || eresponse.getStatuscd().equalsIgnoreCase("Sucess") || eresponse.getStatuscd().equalsIgnoreCase(MasterGSTConstants.SUCCESS))) {
										String invoiceNumber = invoice.getB2b() != null ? invoice.getB2b().get(0) != null ? invoice.getB2b().get(0).getInv() != null ? invoice.getB2b().get(0).getInv().get(0).getInum() != null ? invoice.getB2b().get(0).getInv().get(0).getInum() : "" : "" : "" : "";
										auditlogService.saveAuditLog(userid, clientid,invoiceNumber,AuditLogConstants.GENERATEIRN,MasterGSTConstants.EINVOICE,null,null);
										invoice.setIrnNo(eresponse.getData().getIrn());
										invoice.setIrnStatus("Generated");
										invoice.setGstr1orEinvoice("Einvoice");
										//invoice.setGovtInvoiceStatus(MasterGSTConstants.SUCCESS);
										//invoice.setGstStatus(MasterGSTConstants.GST_STATUS_SUCCESS);
										invoice.setSignedQrCode(eresponse.getData().getSignedQRCode());
										invoice.setSignedInvoice(eresponse.getData().getSignedInvoice());
										invoice.setAckNo(eresponse.getData().getAckNo().toString());
										invoice.setAckDt(eresponse.getData().getAckDt());
										invoice.setEinvStatus(eresponse.getData().getStatus());
										if(isNotEmpty(buydetails)) {
											invoice.setBuyerDtls(buydetails);
										}
										if(isNotEmpty(dispatchDetails)) {
											invoice.setDispatcherDtls(dispatchDetails);
										}
										if(isNotEmpty(shipmentDetails)) {
											invoice.setShipmentDtls(shipmentDetails);
										}
										invoice.setSrctype("MGSTWEB");
										String usrid = userid(invoice.getUserid(),client.getId().toString());
										clientService.changeSubscriptionData(usrid,usertype);
										gstr1Repository.save((GSTR1) invoice);
									}else {
										 String jsonStr="";
										 String desc="";
											if(isNotEmpty(eresponse) && isNotEmpty(eresponse.getStatusdesc())) {
										        jsonStr = eresponse.getStatusdesc();
										        JSONArray jsonObj = new JSONArray(jsonStr);
												JSONObject object=(JSONObject) jsonObj.get(0);
												desc = object.getString("ErrorMessage");
											}
										invoice.setIrnStatus(desc);
										invoice.setGstStatus(desc);
										invoice.setSrctype("MGSTWEB");
										gstr1Repository.save((GSTR1) invoice);	
									}
							}
						}
					}
				}
				return "success";
			}
		
		@RequestMapping(value = "/generateIRN/{returntype}/{usertype}/{month}/{year}", method = RequestMethod.POST)
		public @ResponseBody String generateIRN(@ModelAttribute("invoice")GSTR1 invoice,
				@PathVariable("returntype") String returntype, @PathVariable("usertype") String usertype,
				@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
				final String method = "generateIRN::";
				String invoiceNumber = invoice.getB2b() != null ? invoice.getB2b().get(0) != null ? invoice.getB2b().get(0).getInv() != null ? invoice.getB2b().get(0).getInv().get(0).getInum() != null ? invoice.getB2b().get(0).getInv().get(0).getInum() : "" : "" : "" : "";
				auditlogService.saveAuditLog(invoice.getUserid(), invoice.getClientid(),invoiceNumber,AuditLogConstants.GENERATEIRN,MasterGSTConstants.EINVOICE,null,null);
				String desc="";
				Client client = null;
				if(isNotEmpty(invoice.getClientid())) {
					client = clientService.findById(invoice.getClientid());
				}
				BuyerDetails buydetails = invoice.getBuyerDtls();
				DispatcherDetails dispatchDetails = invoice.getDispatcherDtls();
				ShipmentDetails shipmentDetails = invoice.getShipmentDtls();
				InvoiceParent resData = einvoiceService.populateEinvoiceDetails(invoice, client);
				
				User user = userService.findById(invoice.getUserid());
				String usrid = invoice.getUserid();
				if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
					CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
					if(isNotEmpty(companyUser)) {
						if(isNotEmpty(companyUser.getCompany())){
							if(companyUser.getCompany().contains(invoice.getClientid())){
								usrid = user.getParentid();
							}
						}
					}
				}
				String userid = userid(invoice.getUserid(),invoice.getClientid());
				if (!subscriptionService.allowUploadInvoices(userid, 1l)) {
					saveInvoice(invoice, "GSTR1", usertype, month, year, model);
					desc = "";
				}else {
					GenerateIRNResponse eresponse = iHubConsumerService.generateIRN(client.getGstnnumber(), InetAddress.getLocalHost().getHostAddress(), resData);
					
					if(isNotEmpty(eresponse) && isNotEmpty(eresponse.getStatuscd()) && (eresponse.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE) || eresponse.getStatuscd().equalsIgnoreCase("Sucess") || eresponse.getStatuscd().equalsIgnoreCase(MasterGSTConstants.SUCCESS))) {
						invoice.setIrnNo(eresponse.getData().getIrn());
						invoice.setIrnStatus("Generated");
						invoice.setGstr1orEinvoice("Einvoice");
						//invoice.setGstStatus(MasterGSTConstants.GST_STATUS_SUCCESS);
						//invoice.setGovtInvoiceStatus(MasterGSTConstants.SUCCESS);
						invoice.setSignedQrCode(eresponse.getData().getSignedQRCode());
						invoice.setSignedInvoice(eresponse.getData().getSignedInvoice());
						invoice.setAckNo(eresponse.getData().getAckNo().toString());
						invoice.setAckDt(eresponse.getData().getAckDt());
						invoice.setEinvStatus(eresponse.getData().getStatus());
						if(isNotEmpty(buydetails)) {
							invoice.setBuyerDtls(buydetails);
						}
						if(isNotEmpty(dispatchDetails)) {
							invoice.setDispatcherDtls(dispatchDetails);
						}
						if(isNotEmpty(shipmentDetails)) {
							invoice.setShipmentDtls(shipmentDetails);
						}
						clientService.changeSubscriptionData(userid,usertype);
						invoice.setSrctype("MGSTWEB");
						saveInvoice(invoice, "GSTR1", usertype, month, year, model);
					}else {
						 String jsonStr="";
							if(isNotEmpty(eresponse) && isNotEmpty(eresponse.getStatusdesc())) {
						        jsonStr = eresponse.getStatusdesc();
						        JSONArray jsonObj = new JSONArray(jsonStr);
								JSONObject object=(JSONObject) jsonObj.get(0);
								desc = object.getString("ErrorMessage");
							}
					}
				}
				
				return desc;
			}
		
		@RequestMapping(value = "/cancelIRN/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST)
		public @ResponseBody String cancelIRN(@RequestBody CancelIRN irnData ,@PathVariable("id") String userid,@PathVariable("clientid") String clientid,
				@PathVariable("returntype") String returntype, @PathVariable("month") int month,@PathVariable("year") int year,@RequestParam(value = "ids", required = true) List<String> ids,ModelMap model) throws Exception {
			final String method = "cancelIRN::";
			logger.debug(CLASSNAME + method + BEGIN);
			
			Client client = clientService.findById(clientid);
			InvoiceParent invoice=null;
			String desc="";
			for(String id : ids) {
				invoice = gstr1Repository.findOne(id);
				if(isNotEmpty(invoice) && isNotEmpty(invoice.getIrnNo())) {
					irnData.setIrn(invoice.getIrnNo());
					CancelIRNResponse cresponse = iHubConsumerService.cancelIRN(client.getGstnnumber(), InetAddress.getLocalHost().getHostAddress(),clientid,irnData);
					if(isNotEmpty(cresponse) && isNotEmpty(cresponse.getStatuscd()) && (cresponse.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE) || cresponse.getStatuscd().equalsIgnoreCase("Sucess") || cresponse.getStatuscd().equalsIgnoreCase(MasterGSTConstants.SUCCESS))) {
						String invoiceNumber = invoice != null ? invoice.getInvoiceno() != null ? invoice.getInvoiceno() : "" : "";
						auditlogService.saveAuditLog(userid, clientid,invoiceNumber,AuditLogConstants.CANCELLEDIRN,MasterGSTConstants.EINVOICE,null,null);
						clientService.cancelInvoice(id, "EINVOICE","");
					}else {
						String jsonStr="";
						if(isNotEmpty(cresponse) && isNotEmpty(cresponse.getStatusdesc()) && !"InActive".equalsIgnoreCase(cresponse.getStatusdesc())) {
					        jsonStr = cresponse.getStatusdesc();
					        JSONArray jsonObj = new JSONArray(jsonStr);
							JSONObject object=(JSONObject) jsonObj.get(0);
							desc = object.getString("ErrorMessage");
							if("Invoice is not active".equalsIgnoreCase(desc)) {
								clientService.cancelInvoice(id, "EINVOICE","");
								desc = "";
							}
						}else {
							desc = "InActive";
						}
					}
				}
			}
			
			return desc;
		}
		
		@RequestMapping(value = "/einvAuthconfigdetails/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
		public @ResponseBody EinvoiceConfigurations authEwaybillconfigdetails(@PathVariable("id") String id, @PathVariable("name") String fullname,
				@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
				@PathVariable("returntype") String returntype, @PathVariable("month") int month,
				@PathVariable("year") int year, ModelMap model) throws Exception {
			final String method = "einvAuthconfigdetails::";
			logger.debug(CLASSNAME + method + BEGIN);
			Client client = clientService.findById(clientid);
			EinvoiceConfigurations configdetails = einvoiceConfigurationRepository.findByClientid(clientid);
			if(isNotEmpty(configdetails)) {
				Response response = iHubConsumerService.authenticateEinvoiceAPI(client.getGstnnumber(), InetAddress.getLocalHost().getHostAddress(), configdetails.getUserName(), new String(Base64.getDecoder().decode(configdetails.getPassword()),MasterGSTConstants.PASSWORD_ENCODE_FORMAT));
				String msg = null;	
				if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) && (response.getStatuscd().equalsIgnoreCase(MasterGSTConstants.SUCCESS) || response.getStatuscd().equalsIgnoreCase("Sucess") || response.getStatuscd().equalsIgnoreCase(MasterGSTConstants.SUCCESS_CODE))) {
					configdetails.setConnStaus("Active");
				}else {
					configdetails.setConnStaus("InActive");
					String jsonStr="";
					if(isNotEmpty(response) && isNotEmpty(response.getStatusdesc())) {
				        jsonStr = response.getStatusdesc();
				        JSONArray jsonObj = new JSONArray(jsonStr);
						JSONObject object=(JSONObject) jsonObj.get(0);
						msg = object.getString("ErrorMessage");
					}
				}
				
				EinvoiceConfigurations configs = einvoiceService.saveEinvoiceConfigurations(configdetails);
				if(isNotEmpty(configs) && isNotEmpty(msg)){
					configs.setResponse(msg);
				}
				return configs;
			}else {
				return null;
			}
		} 
		
		@RequestMapping(value = "/uploadeInvoice/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}/{templatetype}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
		public @ResponseBody ImportResponse uploadEInvoice(@PathVariable("id") String id,
				@PathVariable("name") String fullname, @PathVariable("usertype") String usertype,
				@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
				@PathVariable("month") int month, @PathVariable("year") int year,@PathVariable("templatetype") String templatetype, @RequestParam("file") MultipartFile file,
				@RequestParam(value = "branch", required = false) String branch,
				@RequestParam(value = "vertical", required = false) String vertical,
				@RequestParam("list") List<String> list, ModelMap model, HttpServletRequest request) throws Exception {
			final String method = "uploadInvoice::";
			logger.debug(CLASSNAME + method + BEGIN);
			CustomFields customFields = customFieldsRepository.findByClientid(clientid);
			OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
			if (isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableSalesFields())) {
				otherconfig.setEnableSalesFields(false);
			}
			if (isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnablePurFields())) {
				otherconfig.setEnablePurFields(false);
			}

			boolean hsnqtyrate = false;
			if (isNotEmpty(otherconfig)) {
				if ("GSTR1".equals(returntype) || "Sales Register".equals(returntype) || "SalesRegister".equals(returntype)) {
					hsnqtyrate = otherconfig.isEnableSalesFields();
				} else {
					hsnqtyrate = otherconfig.isEnablePurFields();
				}
			}

			BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
			ImportResponse response = new ImportResponse();
			List<ImportSummary> summaryList = Lists.newArrayList();
			if (!file.isEmpty()) {
				logger.debug(CLASSNAME + method + file.getOriginalFilename());
				try {
					String xmlPath = "classpath:mastergst_einvoice_excel_config.xml";
					if(templatetype.equalsIgnoreCase("sage")) {
						xmlPath = "classpath:mastergst_sage_einvoice_config.xml";
					}else if(templatetype.equalsIgnoreCase("allu")) {
						xmlPath = "classpath:mastergst_allu_einvoice_excel_config.xml";
					}
					Resource config = resourceLoader.getResource(xmlPath);
					DateConverter converter = new DateConverter();
					String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd","dd-MM-yy","dd-MMM-yy","dd/MM/yy","dd/MMM/yy"};
					converter.setPatterns(patterns);
					ConvertUtils.register(converter, Date.class);
					ReaderConfig.getInstance().setSkipErrors(true);
					XLSReader xlsReader = ReaderBuilder.buildFromXML(config.getInputStream());
					Map<String, List<InvoiceParent>> sheetMap = einvoiceService.getEinvExcelSheetMap();
					Map<String, List<InvoiceParent>> beans = Maps.newHashMap();
					beans.put("cdnurList", sheetMap.get("cdnurList"));
					beans.put("creditList", sheetMap.get("creditList"));
					beans.put("exportList", sheetMap.get("exportList"));
					beans.put("invoiceList", sheetMap.get("invoiceList"));
					
					XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
					logger.debug(CLASSNAME + method + "Status: {}", status.isStatusOK());
					if (status.isStatusOK()) {
						List<StateConfig> states = configService.getStates();
						Workbook workbook = WorkbookFactory.create(file.getInputStream());
						Workbook workbookError = new HSSFWorkbook();
						String filename = file.getOriginalFilename();
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
						String errorfilename = "MasterGST_Import_Errors_" + dateFormat.format(new Date()) + ".xls";
						if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
							workbookError = new XSSFWorkbook();
							errorfilename = "MasterGST_Import_Errors_" + dateFormat.format(new Date()) + ".xlsx";
						}
						File convFile = new File(errorfilename);
						convFile.createNewFile();
						FileOutputStream fos = new FileOutputStream(convFile);
						for(int i=0;i<workbook.getNumberOfSheets(); i++){
							String sheetName = workbook.getSheetName(i);
							workbookError.createSheet(sheetName);
						}
						for (String key : beans.keySet()) {
							if (isNotEmpty(sheetMap.get(key)) && sheetMap.get(key).size() > 0) {
								ImportSummary summary = new ImportSummary();
								if (key.equals("invoiceList")) {
									summary.setName("Invoices");
								} else if (key.equals("creditList")) {
									summary.setName("Credit_Debit_Notes");
								} else if (key.equals("exportList")) {
									summary.setName("Exports");
								} else if(key.equals("cdnurList")){
									summary.setName("Credit_Debit_Note_Unregistered");
								}else {
									summary.setName(key);
								}
								Sheet datatypeSheet = workbook.getSheet(summary.getName());
								Sheet datatypeSheetError = workbookError.getSheet(summary.getName());
								CellStyle style = workbook.createCellStyle();
								Font font = workbook.createFont();
						        font.setColor(IndexedColors.RED.getIndex());
						        style.setFont(font);
								addErrorColumn(workbook, datatypeSheet, style);
								int lastcolumn = datatypeSheet.getRow(1).getLastCellNum()-1;
								if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
									WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 0, 0);
									WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 1, 1);
								}else {
									WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 0, 0);
									WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 1, 1);
								}
								long failed = 0;
								String strMonth = month < 10 ? "0" + month : month + "";
								String cFp = strMonth + year;
								long invsucess = 0;
								Map<String,InvoiceParent> invoicesmap = new HashMap<String, InvoiceParent>();
								long totalinvs = 0;
								Map<String,InvoiceParent> tinvoicesmap = new HashMap<String, InvoiceParent>();
								List<InvoiceParent> invoices = beans.get(key);
								List<InvoiceParent> filteredList = Lists.newArrayList();
								if (isNotEmpty(invoices)) {
									int index = 1;
									summary.setTotal(beans.get(key).size() - 1);
									//List<Row> errorRows = Lists.newArrayList();
									List<Integer> errorIndxes = Lists.newArrayList();
									for (InvoiceParent invoice : invoices) {
										if(key.equalsIgnoreCase("invoiceList")) {
											invoice.setInvtype(MasterGSTConstants.B2B);
										}else if(key.equalsIgnoreCase("creditList")) {
											invoice.setInvtype(MasterGSTConstants.CREDIT_DEBIT_NOTES);
										}else if(key.equalsIgnoreCase("cdnurList")) {
											invoice.setInvtype(MasterGSTConstants.CDNUR);
										}else if(key.equalsIgnoreCase("exportList")) {
											invoice.setInvtype(MasterGSTConstants.EXPORTS);
										}
										String invnum = "";
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
											
											if(isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getInum())) {
												invnum = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getInum().trim()).toPlainString();
											}else {
												invnum = invoice.getB2b().get(0).getInv().get(0).getInum();
											}
										}
										if(isNotEmpty(tinvoicesmap)) {
											InvoiceParent exstngInv = tinvoicesmap.get(invnum);
											if(isEmpty(exstngInv)) {
												totalinvs++;
												tinvoicesmap.put(invnum, invoice);
											}
										}else {
											totalinvs++;
											tinvoicesmap.put(invnum, invoice);
										}
										if ((isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(INVOICENO_REGEX, invnum.trim()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
														&& isNotEmpty(datatypeSheet.getRow(index))) {
														if(index != 1) {
															Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
															gstincell.setCellValue("Invalid Invoice Number Format");
															gstincell.setCellStyle(style);
														}
													errorIndxes.add(index);
												}
											
										}else if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
													if(index != 1) {
														Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
														gstincell.setCellValue("Invalid GSTIN Number");
														gstincell.setCellStyle(style);
													}
													errorIndxes.add(index);
											}
										} else {
											if (hsnqtyrate) {
												if (returntype.equals(MasterGSTConstants.GSTR1)
														&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
														&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
																|| (key.equals("creditList") && (isEmpty(((GSTR1) invoice).getCdnr())
																		|| isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())
																		|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())
																		|| isEmpty(((GSTR1) invoice).getB2b().get(0).getInv().get(0).getInum())))
																|| (key.equals("cdnurList")	&& (isEmpty(((GSTR1) invoice).getCdnur()) || "Credit/Debit Note Date*".equals(invoice.getStrDate())
																		|| isEmpty(((GSTR1) invoice).getB2b().get(0).getInv().get(0).getInum())
																		|| isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
																|| (key.equals("exportList") && (isEmpty(invoice.getEinvCategory()))
																		|| isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) || "Invoice No*".equals(invoice.getB2b().get(0).getInv().get(0).getInum()))
																		|| isEmpty(invoice.getStrDate()) || "Invoice Date*".equals(invoice.getStrDate())
																|| isEmpty(invoice.getItems())
																|| (isNotEmpty(invoice.getItems().get(0).getIgstrate())	&& invoice.getItems().get(0).getIgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getCgstrate())	&& invoice.getItems().get(0).getCgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getSgstrate())	&& invoice.getItems().get(0).getSgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getCessrate())	&& invoice.getItems().get(0).getCessrate() < 0))) {

													failed++;
													if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
														invoice.setGstr1orEinvoice("Einvoice");
														if(index != 1) {
														String errorVal = importInvoiceService.einvErrorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														}
														errorIndxes.add(index);
													}
												} else {
													if(templatetype.equalsIgnoreCase("sage")) {
														invoice = importInvoiceService.importEInvoicesHsnQtyRateMandatory(invoice,returntype,branch,vertical,month,year,patterns);
													}else {
														invoice = importInvoiceService.importEInvoicesHsnQtyRateMandatoryNew(invoice,returntype,branch,vertical,month,year,patterns,states);
													}
													if(templatetype.equalsIgnoreCase("allu")) {
														if(isNotEmpty(customFields) && isNotEmpty(customFields.getEinvoice())) {
															if(isNotEmpty(customFields.getEinvoice().get(0)) && isNotEmpty(customFields.getEinvoice().get(0).getCustomFieldName())) {
																invoice.setCustomFieldText1(customFields.getEinvoice().get(0).getCustomFieldName());
															}
															if(isNotEmpty(customFields.getEinvoice().get(1))  && isNotEmpty(customFields.getEinvoice().get(1).getCustomFieldName())) {
																invoice.setCustomFieldText2(customFields.getEinvoice().get(1).getCustomFieldName());
															}
															if(isNotEmpty(customFields.getEinvoice().get(2))  && isNotEmpty(customFields.getEinvoice().get(2).getCustomFieldName())) {
																invoice.setCustomFieldText3(customFields.getEinvoice().get(2).getCustomFieldName());
															}
															if(isNotEmpty(customFields.getEinvoice().get(3))  && isNotEmpty(customFields.getEinvoice().get(3).getCustomFieldName())) {
																invoice.setCustomFieldText4(customFields.getEinvoice().get(3).getCustomFieldName());
															}
														}
													}
													if(isNotEmpty(invoice.getItems().get(0))) {
														Item item = invoice.getItems().get(0); 
														item = bulkImportTaskService.changeInvoiceAmounts(item);
														List<Item> itms = Lists.newArrayList();
														itms.add(item);
														invoice.setItems(itms);
													}
													if(templatetype.equalsIgnoreCase("sage")) {
														buyerdetails(invoice);
													}
													filteredList.add(invoice);
													String fp = cFp;
													if(isNotEmpty(invoice)) {
														if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
															Calendar cal = Calendar.getInstance();
															cal.setTime(invoice.getDateofinvoice());
															int iMonth = cal.get(Calendar.MONTH) + 1;
															int iYear = cal.get(Calendar.YEAR);
															fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
														}
														String mapkey = "";
														if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
															mapkey = mapkey+invoice.getB2b().get(0).getInv().get(0).getInum().trim();
														}
														mapkey = mapkey+key.trim();
														mapkey = mapkey+fp.trim();
														if(isNotEmpty(invoicesmap)) {
															InvoiceParent exstngInv = invoicesmap.get(mapkey);
															if(isEmpty(exstngInv)) {
																invsucess++;
																invoicesmap.put(mapkey, invoice);
															}
														}else {
															invsucess++;
															invoicesmap.put(mapkey, invoice);
														}
													}
												}
											} else {
												if (returntype.equals(MasterGSTConstants.GSTR1)
														&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
														&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
														|| (key.equals("creditList")  && (isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())	|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
														|| (key.equals("cdnurList")	&& (isEmpty(((GSTR1) invoice).getCdnur()) || "Credit/Debit Note Date*".equals(invoice.getStrDate())
																|| isEmpty(((GSTR1) invoice).getB2b().get(0).getInv().get(0).getInum())
																|| isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
														|| (key.equals("exportList") && (isEmpty(invoice.getEinvCategory())) || isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) || "Invoice No*".equalsIgnoreCase(invoice.getB2b().get(0).getInv().get(0).getInum()))
														|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
														|| (isNotEmpty(invoice.getItems().get(0).getRateperitem())	&& invoice.getItems().get(0).getRateperitem() < 0) 
														|| (isNotEmpty(invoice.getItems().get(0).getIgstrate())	&& invoice.getItems().get(0).getIgstrate() < 0)	
														|| (isNotEmpty(invoice.getItems().get(0).getCgstrate())	&& invoice.getItems().get(0).getCgstrate() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getSgstrate())	&& invoice.getItems().get(0).getSgstrate() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getCessrate())	&& invoice.getItems().get(0).getCessrate() < 0))) {
													failed++;
													if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
														invoice.setGstr1orEinvoice("Einvoice");
														if(index != 1) {
														String errorVal = importInvoiceService.einvErrorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														}
														errorIndxes.add(index);
													}
												}  else {
													
													if(templatetype.equalsIgnoreCase("sage")) {
														invoice = importInvoiceService.importEInvoicesHsnQtyRateMandatory(invoice,returntype,branch,vertical,month,year,patterns);
													}else {
														invoice = importInvoiceService.importEInvoicesHsnQtyRateMandatoryNew(invoice,returntype,branch,vertical,month,year,patterns,states);
													}
													if(templatetype.equalsIgnoreCase("allu")) {
														if(isNotEmpty(customFields) && isNotEmpty(customFields.getEinvoice())) {
															if(isNotEmpty(customFields.getEinvoice().get(0)) && isNotEmpty(customFields.getEinvoice().get(0).getCustomFieldName())) {
																invoice.setCustomFieldText1(customFields.getEinvoice().get(0).getCustomFieldName());
															}
															if(isNotEmpty(customFields.getEinvoice().get(1))  && isNotEmpty(customFields.getEinvoice().get(1).getCustomFieldName())) {
																invoice.setCustomFieldText2(customFields.getEinvoice().get(1).getCustomFieldName());
															}
															if(isNotEmpty(customFields.getEinvoice().get(2))  && isNotEmpty(customFields.getEinvoice().get(2).getCustomFieldName())) {
																invoice.setCustomFieldText3(customFields.getEinvoice().get(2).getCustomFieldName());
															}
															if(isNotEmpty(customFields.getEinvoice().get(3))  && isNotEmpty(customFields.getEinvoice().get(3).getCustomFieldName())) {
																invoice.setCustomFieldText4(customFields.getEinvoice().get(3).getCustomFieldName());
															}
														}
													}
													if(isNotEmpty(invoice.getItems().get(0))) {
														Item item = invoice.getItems().get(0); 
														item = bulkImportTaskService.changeInvoiceAmounts(item);
														List<Item> itms = Lists.newArrayList();
														itms.add(item);
														invoice.setItems(itms);
													}
													if(templatetype.equalsIgnoreCase("sage")) {
														buyerdetails(invoice);
													}
													filteredList.add(invoice);
													String fp = cFp;
													if(isNotEmpty(invoice)) {
														if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
															Calendar cal = Calendar.getInstance();
															cal.setTime(invoice.getDateofinvoice());
															int iMonth = cal.get(Calendar.MONTH) + 1;
															int iYear = cal.get(Calendar.YEAR);
															fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
														}
														String mapkey = "";
														if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
															mapkey = mapkey+invoice.getB2b().get(0).getInv().get(0).getInum().trim();
														}
														mapkey = mapkey+key.trim();
														mapkey = mapkey+fp.trim();
														if(isNotEmpty(invoicesmap)) {
															InvoiceParent exstngInv = invoicesmap.get(mapkey);
															if(isEmpty(exstngInv)) {
																invsucess++;
																invoicesmap.put(mapkey, invoice);
															}
														}else {
															invsucess++;
															invoicesmap.put(mapkey, invoice);
														}
													}
												}

											}
										}
										index++;
									}
									if (failed != 0) {
										failed = failed - 1;
									}
									summary.setSuccess((beans.get(key).size() - 1) - (failed));
									int eC = 2;
									for (int i = 2; i <= datatypeSheet.getLastRowNum(); i++) {
										if (errorIndxes.contains(i)) {
											if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
												WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, i, eC++);
											}else {
												WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, i, eC++);
											}
										}
									}
									beans.put(key, filteredList);
								}
								summary.setFailed((failed));
								summary.setTotalinvs(totalinvs-1);
								summary.setInvsuccess(invsucess);
								summary.setInvfailed(totalinvs-1 - invsucess);
								summaryList.add(summary);
							}
						}
						try {
							workbookError.write(fos);
							fos.flush();
							fos.close();
							// workbook.close();
						} catch (IOException e) {
							logger.debug(CLASSNAME + method + "XLS Write ERROR {}", e);
							convFile = null;
						}
						String strMonth = month < 10 ? "0" + month : month + "";
						String cFp = strMonth + year;
						Map<Integer, Integer> fpMap = Maps.newHashMap();
						for (String key : beans.keySet()) {
							logger.debug(CLASSNAME + method + "{} : {}", key, beans.get(key));
							String fp = cFp;
							if (beans.get(key) instanceof InvoiceParent) {
								List<InvoiceParent> beanList = Lists.newArrayList();
								InvoiceParent invoiceParent = (InvoiceParent) beans.get(key);
								beanList.add(invoiceParent);
								if (isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getDateofinvoice())) {
									Calendar cal = Calendar.getInstance();
									cal.setTime(invoiceParent.getDateofinvoice());
									int iMonth = cal.get(Calendar.MONTH) + 1;
									int iYear = cal.get(Calendar.YEAR);
									fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
									fpMap.put(iMonth, iYear);
								}
								beans.put(key, beanList);
							}
							List<InvoiceParent> invoices = beans.get(key);
							if (isNotEmpty(invoices)) {
								for (InvoiceParent invoice : invoices) {
									if(isNotEmpty(invoice)) {
										if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
											Calendar cal = Calendar.getInstance();
											cal.setTime(invoice.getDateofinvoice());
											int iMonth = cal.get(Calendar.MONTH) + 1;
											int iYear = cal.get(Calendar.YEAR);
											fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
											fpMap.put(iMonth, iYear);
										}
										invoice.setFp(fp);
									}
								}
							}
						}
						if (isNotEmpty(fpMap.keySet())) {
							for (Integer m : fpMap.keySet()) {
								if (fpMap.keySet().size() == 1
										&& (!m.equals(new Integer(month)) || !fpMap.get(m).equals(new Integer(year)))) {
									month = m;
									year = fpMap.get(m);
								}
							}
						}
						response.setMonth(month);
						response.setYear(year);
						response.setSummaryList(summaryList);
						if (isNotEmpty(summaryList)) {
							int failedCount = 0;
							for (ImportSummary summary : summaryList) {
								if (summary.getFailed() > 0) {
									failedCount += summary.getFailed();
								}
							}
							if (failedCount > 0 && isNotEmpty(convFile)) {
								HttpSession session = request.getSession(false);
								if (isNotEmpty(session)) {
									session.setAttribute("error.xls", convFile);
									response.setShowLink(true);
								}
							} else {
								try {
									convFile.delete();
								} catch (Exception e) {
								}
							}
						}
						String invReturnType = returntype;
						clientService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, EXCEL_TEMPLATE);
					} else {
						response.setError(
								"Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.");
					}
				} catch (Exception e) {
					response.setError(
							"Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.");
					logger.error(CLASSNAME + method + " ERROR", e);
				}
			}
			return response;
		}
		private void addErrorColumn(Workbook workbook, Sheet sheet, CellStyle style){
			//CellStyle style = workbook.createCellStyle();
		    Cell cell = sheet.getRow(1).createCell(sheet.getRow(1).getLastCellNum());
		    cell.setCellValue("Errors");
		    cell.setCellStyle(style);
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
		
		private void buyerdetails(InvoiceParent invoice) {
			if(isNotEmpty(invoice.getBilledtoname())) {
				String customername = invoice.getBilledtoname();
				if(isEmpty(invoice.getBuyerDtls())) {
					BuyerDetails buyerDetails = new BuyerDetails();
					buyerDetails.setTrdNm(customername);
					buyerDetails.setLglNm(customername);
					invoice.setBuyerDtls(buyerDetails);
				}else {
					invoice.getBuyerDtls().setTrdNm(customername);
					invoice.getBuyerDtls().setLglNm(customername);
				}
			}
			if(!("Exports").equalsIgnoreCase(invoice.getInvtype())) {
				if(isNotEmpty(invoice.getBuyerPincode())) {
					if(isEmpty(invoice.getBuyerDtls())) {
						BuyerDetails buyerDetails = new BuyerDetails();
						buyerDetails.setPin(invoice.getBuyerPincode());
						invoice.setBuyerDtls(buyerDetails);
					}else {
						invoice.getBuyerDtls().setPin(invoice.getBuyerPincode());
					}
				}
			}else {
				if(isEmpty(invoice.getBuyerDtls())) {
					BuyerDetails buyerDetails = new BuyerDetails();
					buyerDetails.setPin(Integer.parseInt("999999"));
					invoice.setBuyerDtls(buyerDetails);
				}else {
					invoice.getBuyerDtls().setPin(Integer.parseInt("999999"));
				}
				
			}
			if(!("Exports").equalsIgnoreCase(invoice.getInvtype())) {
				if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					if(isEmpty(invoice.getBuyerDtls())) {
						BuyerDetails buyerDetails = new BuyerDetails();
						buyerDetails.setGstin(invoice.getB2b().get(0).getCtin());
						invoice.setBuyerDtls(buyerDetails);
					}else {
						invoice.getBuyerDtls().setGstin(invoice.getB2b().get(0).getCtin());
					}
				}
			}else {
				if(isEmpty(invoice.getBuyerDtls())) {
					BuyerDetails buyerDetails = new BuyerDetails();
					buyerDetails.setGstin("URP");
					invoice.setBuyerDtls(buyerDetails);
				}else {
					invoice.getBuyerDtls().setGstin("URP");
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
			if(isNotEmpty(invoice.getBankname())) {
				if(isEmpty(invoice.getBankDetails())) {
					CompanyBankDetails bankdetails = new CompanyBankDetails();
					bankdetails.setBankname(invoice.getBankname());
					invoice.setBankDetails(bankdetails);
				}else {
					invoice.getBankDetails().setBankname(invoice.getBankname());
				}
			}
			if(isNotEmpty(invoice.getAccountname())) {
				if(isEmpty(invoice.getBankDetails())) {
					CompanyBankDetails bankdetails = new CompanyBankDetails();
					bankdetails.setAccountName(invoice.getAccountname());
					invoice.setBankDetails(bankdetails);
				}else {
					invoice.getBankDetails().setAccountName(invoice.getAccountname());
				}
			}
			if(isNotEmpty(invoice.getReferenceNumber())) {
				String referenceno = "";
				if (isNotEmpty(invoice.getReferenceNumber())) {
					
					if(isScientificNotation(invoice.getReferenceNumber())) {
						referenceno = new BigDecimal(invoice.getReferenceNumber()).toPlainString();
					}else {
						referenceno = invoice.getReferenceNumber();
					}
				}
				invoice.setReferenceNumber(referenceno);
			}
			if(isNotEmpty(invoice.getCustomField1())) {
				String horeferenceno = "";
				if (isNotEmpty(invoice.getCustomField1())) {
					
					if(isScientificNotation(invoice.getCustomField1())) {
						horeferenceno = new BigDecimal(invoice.getCustomField1()).toPlainString();
					}else {
						horeferenceno = invoice.getCustomField1();
					}
				}
				invoice.setCustomField1(horeferenceno);
			}
			if(isNotEmpty(invoice.getAccountnumber())) {
				String accno = "";
				if (isNotEmpty(invoice.getAccountnumber())) {
					
					if(isScientificNotation(invoice.getAccountnumber())) {
						accno = new BigDecimal(invoice.getAccountnumber()).toPlainString();
					}else {
						accno = invoice.getAccountnumber();
					}
				}
				if(isEmpty(invoice.getBankDetails())) {
					CompanyBankDetails bankdetails = new CompanyBankDetails();
					bankdetails.setAccountnumber(accno);
					invoice.setBankDetails(bankdetails);
				}else {
					invoice.getBankDetails().setAccountnumber(accno);
				}
			}
			if(isNotEmpty(invoice.getBranchname())) {
				if(isEmpty(invoice.getBankDetails())) {
					CompanyBankDetails bankdetails = new CompanyBankDetails();
					bankdetails.setBranchname(invoice.getBranchname());
					invoice.setBankDetails(bankdetails);
				}else {
					invoice.getBankDetails().setBranchname(invoice.getBranchname());
				}
			}
			if(isNotEmpty(invoice.getIfsccode())) {
				if(isEmpty(invoice.getBankDetails())) {
					CompanyBankDetails bankdetails = new CompanyBankDetails();
					bankdetails.setIfsccode(invoice.getIfsccode());
					invoice.setBankDetails(bankdetails);
				}else {
					invoice.getBankDetails().setIfsccode(invoice.getIfsccode());
				}
			}
		}
		
			@RequestMapping(value = "/uploadEinvmapImportInvoice/{templateName}/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST)
		public @ResponseBody ImportResponse uploadmapImportInvoice(@PathVariable("templateName") String templateName,
				@PathVariable("id") String id, @PathVariable("name") String fullname,
				@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
				@PathVariable("returntype") String returntype, @PathVariable("month") int month,
				@PathVariable("year") int year, @RequestParam("file") MultipartFile file,
				@RequestParam("list") List<String> list,@RequestParam(value = "branch", required = false) String branch,
				@RequestParam(value = "vertical", required = false) String vertical, ModelMap model, HttpServletRequest request) throws Exception {
			final String method = "uploadEinvmapImportInvoice::";
			logger.debug(CLASSNAME + method + BEGIN);
			OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
			if (isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableSalesFields())) {
				otherconfig.setEnableSalesFields(false);
			}
			if (isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnablePurFields())) {
				otherconfig.setEnablePurFields(false);
			}

			TemplateMapperDoc templateMapperDoc = importMapperService.getMapperDoc(id, clientid, templateName);
			TemplateMapper templateMapper = importMapperService.getMapper(id, clientid, templateName);
			String simplifiedOrDetail = "Detailed";
			if (isNotEmpty(templateMapper) && isNotEmpty(templateMapper.getSimplifiedOrDetail())) {
				simplifiedOrDetail = templateMapper.getSimplifiedOrDetail();
			}
			ImportResponse response = new ImportResponse();
			if ("Simplified".equalsIgnoreCase(simplifiedOrDetail)) {
				response = einvoiceService.importEinvMapperSimplified(id, clientid, templateName, file, returntype, month, year, list,
						fullname, templateMapper, templateMapperDoc, otherconfig,branch,vertical,request);
			} else {
				response = einvoiceService.importEinvMapperDetailed(id, clientid, templateName, file, returntype, month, year, list, fullname,
						templateMapper, templateMapperDoc,otherconfig, branch,vertical,request);
			}
			return response;
		}
	  public String userid(String id,String clientid) {
				String userid = id;
				User user = userService.findById(userid);
				if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
					CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
					if(NullUtil.isNotEmpty(companyUser)) {
						List<String> clntids = companyUser.getCompany();
						if(isNotEmpty(clntids) && clntids.size() > 0){
							if(clntids.contains(clientid)){
								ClientUserMapping clntusermapping = clientUserMappingRepository.findByClientidAndCreatedByIsNotNull(clientid);
								if(isNotEmpty(clntusermapping) && isNotEmpty(clntusermapping.getCreatedBy())) {
									userid = clntusermapping.getCreatedBy();
								}else {
									userid = user.getParentid();
								}
							}
						}else {
							userid = user.getParentid();
						}
					}
				}
				return userid;
			}
	  
	  @RequestMapping(value = "/einvsubscriptionCheck/{clientid}/{id}", method = RequestMethod.GET)
		public @ResponseBody String subscriptionCheck(@PathVariable("id") String id,@PathVariable("clientid") String clientid,HttpServletRequest request) {
			logger.debug(CLASSNAME + "subscriptionCheck:: Begin");
			User user = userService.findById(id);
			String usrid = id;
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
				CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
				if(isNotEmpty(companyUser)) {
					if(isNotEmpty(companyUser.getCompany())){
						if(companyUser.getCompany().contains(clientid)){
							usrid = user.getParentid();
						}
					}
				}
			}
			String userid = userid(id,clientid);
			Client client = clientService.findById(clientid);
			
			String errormsg = "";
			if (!subscriptionService.allowUploadInvoices(userid, 1l)) {
				if(usrid.equals(user.getParentid())) {
					User usr = userRepository.findById(userid);
					if(isNotEmpty(usr)) {
						errormsg = primaryHolderMessage(client,usr);
					}else {
						errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
					}
				}else {
					errormsg = "Your subscription has expired. Kindly subscribe to proceed further!";
				}
				
				return errormsg;
			}else{
				return errormsg;
			}	
		}
	  
	  public String primaryHolderMessage(Client client, User usr) {
			
			String message = "Primary Account Holder of <span style='color:blue;'>"+client.getBusinessname()+"</span> is Subscription is expired, please contact <span style='color:blue'>"+usr.getFullname()+","+usr.getEmail()+" & "+usr.getMobilenumber()+"</span> to renew";
			return message;
		}
	  
	  @RequestMapping(value = "/deleteAllEInvoices/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST)
		public @ResponseBody String delAllEInvss(@PathVariable("id") String id,@PathVariable("clientid") String clientid,@PathVariable("returntype") String returntype,
				 @PathVariable("month") int month,	@PathVariable("year") int year, @RequestParam("booksOrReturns") String booksOrReturns, ModelMap model,HttpServletRequest request)
						throws Exception {
			final String method = "delSelectedInvss::";
			String reportType = "notreports";
			String st = request.getParameter("start");
			InvoiceFilter filter = new InvoiceFilter();
			filter.setBooksOrReturns(request.getParameter("booksorReturns"));
			int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
			String len = "-1";
			int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
			String searchVal = request.getParameter("search[value]");
			if(searchVal == null || "".equals(searchVal.trim())){
				searchVal = null;
			}
			String strMonth = month < 10 ? "0" + month : month + "";
			String desc = strMonth+year;
			auditlogService.saveAuditLog(id, clientid,desc,AuditLogConstants.DELETEALL,MasterGSTConstants.EINVOICE,null,null);
			Client client = clientService.findById(clientid);
			String sortParam = "dateofinvoice";
			String sortOrder = "asc";
			String retType = returntype;
			Pageable pageable = new PageRequest(0, 1000);
			 String irnstatus = "Not Generated,Failed";
			filter.setIrnStatus(irnstatus);
			boolean hasMore = true;
			Map<String, Object> invoicesMap = null;
			start = pageable.getPageNumber();
			length = pageable.getPageSize();
			while(hasMore) {
				invoicesMap = einvoiceService.getEInvoicesForDelete(pageable, client, id, retType, "notreports", month, year, start, length, searchVal,sortParam,sortOrder, filter, true);
				
				Page<? extends InvoiceParent> invoices = (Page<? extends InvoiceParent>)invoicesMap.get("invoices");
				start++;
				if(isEmpty(invoices) || isEmpty(invoices.getContent()) || invoices.getContent().size() == 0) {
					hasMore = false;
					break;				
				}
				if(isNotEmpty(invoices) && isNotEmpty(invoices.getContent()) && invoices.getContent().size() < 1000) {
					hasMore = false;
				}
				List<String> irnStatus = Lists.newArrayList();
				irnStatus.add("Generated");
				irnStatus.add("Cancelled");
				if(isNotEmpty(invoices)) {
					for(InvoiceParent invoiceParent : invoices) {
						if(isNotEmpty(invoiceParent) && ((isNotEmpty(invoiceParent.getIrnStatus()) && !irnstatus.contains(invoiceParent.getIrnStatus())) || isEmpty(invoiceParent.getIrnStatus()))) {
							if(isEmpty(invoiceParent.getIrnNo())) {
								deleteInvoices(id, clientid, returntype, invoiceParent.getId().toString(), booksOrReturns, month, year);
							}
						}
					}
				}
			}
			logger.debug(CLASSNAME + method + BEGIN);
			return MasterGSTConstants.SUCCESS;
		}
	  
	  @RequestMapping(value = "/delSelectedeInvss/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST)
		public @ResponseBody String delSelectedeInvss(@PathVariable("id") String id,@PathVariable("clientid") String clientid,@PathVariable("returntype") String returntype,
				 @PathVariable("month") int month,	@PathVariable("year") int year, @RequestParam("booksOrReturns") String booksOrReturns,@RequestBody DeletedInvoices deletedinvoices, ModelMap model,HttpServletRequest request)
						throws Exception {
			final String method = "delSelectedInvss::";
					
			List<String> invnos =deletedinvoices.getInvnos();
			List<String> irnstatus = Lists.newArrayList();
			irnstatus.add("Generated");
			irnstatus.add("Cancelled");
			for(String invno:invnos) {
				InvoiceParent invoice = gstr1Repository.findOne(invno);
				if(isNotEmpty(invoice) && ((isNotEmpty(invoice.getIrnStatus()) && !irnstatus.contains(invoice.getIrnStatus())) || isEmpty(invoice.getIrnStatus()))) {
					if(isEmpty(invoice.getIrnNo())) {
						String invoiceNumber = invoice != null ? invoice.getInvoiceno() != null ? invoice.getInvoiceno() : "" : "";
						auditlogService.saveAuditLog(id, clientid,invoiceNumber,AuditLogConstants.DELETE,MasterGSTConstants.EINVOICE,null,null);
						deleteInvoices(id, clientid, returntype, invno, booksOrReturns, month, year);
					}
				}
			}
			logger.debug(CLASSNAME + method + BEGIN);
			return MasterGSTConstants.SUCCESS;
		}
	  
	  public void deleteInvoices(String id, String clientid, String returntype, String invoiceId, String booksOrReturns, int month, int year) {
			String retType;
			if(returntype.equals(GSTR1)){
				retType = GSTR1;
			}else if(returntype.equals(GSTR4)){
				retType = GSTR4;
			}else if(returntype.equals(GSTR5)){
				retType = GSTR5;
			}else if(returntype.equals(GSTR6)){
				retType = GSTR6;
			}else if(returntype.equals(ANX1)){
				retType = ANX1;
			}else if(returntype.equals(MasterGSTConstants.DELIVERYCHALLANS)) {
				retType = MasterGSTConstants.DELIVERYCHALLANS;
			}else if(returntype.equals(MasterGSTConstants.PROFORMAINVOICES)) {
				retType = MasterGSTConstants.PROFORMAINVOICES;
			}else if(returntype.equals(MasterGSTConstants.ESTIMATES)) {
				retType = MasterGSTConstants.ESTIMATES;
			}else if(returntype.equals(MasterGSTConstants.PURCHASEORDER)) {
				retType = MasterGSTConstants.PURCHASEORDER;
			}else if(returntype.equals(MasterGSTConstants.EWAYBILL)) {
				retType = MasterGSTConstants.EWAYBILL;
			}else if(returntype.equals(MasterGSTConstants.EINVOICE)) {
				retType = MasterGSTConstants.EINVOICE;
			}else{
				retType = PURCHASE_REGISTER;
			}
			AccountingJournal journal = null;
			if(PURCHASE_REGISTER.equals(retType)) {
				journal = accountingJournalRepository.findByInvoiceIdAndClientIdAndReturnType(invoiceId,clientid,"GSTR2");
			}else {
				journal = accountingJournalRepository.findByInvoiceIdAndClientIdAndReturnType(invoiceId,clientid,retType);
			}
			if(isNotEmpty(journal)) {
				journal.setStatus("Deleted");
				accountingJournalRepository.save(journal);
			}
			String invoiceNo = clientService.deleteInvoice(invoiceId, retType,booksOrReturns);
			if (isNotEmpty(invoiceNo)) {
				String submissionYear = (year - 1) + "-" + (year);
				if (month > 3) {
					submissionYear = year + "-" + (year + 1);
				}
				Client client = clientService.findById(clientid);
				List<CompanyInvoices> invoiceSubmissionDatas = profileService.getUserInvoiceSubmissionDetails(clientid,
						submissionYear);
				if (isNotEmpty(invoiceSubmissionDatas)) {
					for (CompanyInvoices invoiceSubmissionData : invoiceSubmissionDatas) {
						if (isNotEmpty(invoiceSubmissionData.getPrefix())
								&& invoiceNo.startsWith(invoiceSubmissionData.getPrefix())) {
							if (isEmpty(invoiceSubmissionData.getInvoiceType())) {
								invoiceSubmissionData.setInvoiceType("ALL");
							}
							String prefix = invoiceSubmissionData.getPrefix();
							Integer inv = -1;
							try {
								inv = Integer.parseInt(invoiceNo.replace(prefix, ""));
							} catch (NumberFormatException e) {
							}
							if (inv > 0) {
								Page<? extends InvoiceParent> invoices = clientService.getInvoices(null, client, returntype,
										month, year);
								if(isNotEmpty(invoices)) {
									boolean updated = false;
									for (InvoiceParent invoice : invoices) {
										if (isNotEmpty(invoice.getInvoiceno()) && isNotEmpty(invoice.getInvtype())
												&& (invoice.getInvtype().equals(invoiceSubmissionData.getInvoiceType())
														|| invoiceSubmissionData.getInvoiceType().equals("ALL"))
												&& invoice.getInvoiceno().startsWith(prefix)) {
											try {
												Integer newInv = Integer.parseInt(invoice.getInvoiceno().replace(prefix, ""));
												if (newInv > inv) {
													newInv--;
												}
												invoice.setInvoiceno(prefix + newInv);
												updated = true;
											} catch (NumberFormatException e) {
											}
										}
									}
									if (updated) {
										clientService.saveInvoices(invoices, returntype);
									}
								}
							}
						}
					}
				}
			}
		}

}
