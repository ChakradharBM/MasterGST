/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.EXCEL_TEMPLATE;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.common.MasterGSTConstants.TALLY_TEMPLATE;
import static com.mastergst.core.common.MasterGSTConstants.MASTERGST_EXCEL_TEMPLATE;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.reader.ReaderBuilder;
import org.jxls.reader.ReaderConfig;
import org.jxls.reader.XLSReadStatus;
import org.jxls.reader.XLSReader;
import org.jxls.util.JxlsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.CountryConfig;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR1DocDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR1DocIssueDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR1DocumentIssue;
import com.mastergst.usermanagement.runtime.domain.GSTR4;
import com.mastergst.usermanagement.runtime.domain.HSNData;
import com.mastergst.usermanagement.runtime.domain.HSNDetails;
import com.mastergst.usermanagement.runtime.domain.ImportResponse;
import com.mastergst.usermanagement.runtime.domain.ImportSummary;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.TemplateMapper;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;
import com.mastergst.usermanagement.runtime.repository.GSTR1DocumentIssueRepository;
import com.mastergst.usermanagement.runtime.repository.HSNSummaryRepository;
import com.mastergst.usermanagement.runtime.service.BulkImportTaskService;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ImportInvoiceService;
import com.mastergst.usermanagement.runtime.service.ImportMapperService;
import com.mastergst.usermanagement.runtime.service.NewimportService;
import com.mastergst.usermanagement.runtime.support.MonthCodes;
import com.mastergst.usermanagement.runtime.support.WorkbookUtility;
/**
 * Handles all import activities.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 * @since 1.0
 */
@Controller
public class ImportController {

	private static final Logger logger = LogManager.getLogger(ImportController.class.getName());
	private static final String CLASSNAME = "ImportController::";

	private String gstnFormat = "[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}|[0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1}|[0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1}|[0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1}|[9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1}";
	 private static DecimalFormat df2 = new DecimalFormat("#.##");

	private static final String INVOICENO_REGEX ="^[0-9a-zA-Z/-]{1,16}$";
	
	private static final String IMPGINVOICENO_REGEX = "^[0-9]{1,7}$";
	
	
	@Autowired
	private ClientService clientService;
	@Autowired
	private UserService userService;
	@Autowired
	private ImportMapperService importMapperService;
	@Autowired
	private ResourceLoader resourceLoader;
	@Autowired
	private	HSNSummaryRepository hsnSummaryRepository; 
	@Autowired
	private	GSTR1DocumentIssueRepository gstr1DocumentIssueRepository;
	@Autowired
	private BulkImportTaskService bulkImportTaskService; 
	@Autowired
	private ImportInvoiceService importInvoiceService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private NewimportService newimportService;
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
	
	@RequestMapping(value = "/uploadInvoice/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}/{temptype}", method = RequestMethod.POST)
	public @ResponseBody ImportResponse uploadInvoice(@PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
			@PathVariable("month") int month, @PathVariable("year") int year,@PathVariable("temptype") String temptype, @RequestParam("file") MultipartFile file,
			@RequestParam(value = "branch", required = false) String branch,
			@RequestParam(value = "vertical", required = false) String vertical,
			@RequestParam(value = "tallyfinancialYear", required = false) String hsnfinancialYear, @RequestParam(value = "tallymonth", required = false) String hsnmonth,
			@RequestParam("list") List<String> list, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "uploadInvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		User usr = userService.findById(id);
		Client client = clientService.findById(clientid);
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
				String xmlPath = "";
				if(isNotEmpty(temptype) && temptype.equalsIgnoreCase("mgstFHPL")){
					xmlPath = "classpath:mastergst_fhpl_excel_config.xml";
				}else {
					xmlPath = "classpath:mastergst_excel_config.xml";
					if(isNotEmpty(temptype) && temptype.equalsIgnoreCase("mastergst_all_fileds")){
						xmlPath = "classpath:mastergst_all_fields_excel_config2.xml";
					}
				}
				//xmlPath = "classpath:mastergst_excel_config.xml";
				if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)	|| returntype.equals(MasterGSTConstants.GSTR2)) {
					xmlPath = "classpath:mastergst_purchase_excel_config.xml";
				} else if (returntype.equals(MasterGSTConstants.GSTR6)) {
					xmlPath = "classpath:mastergst_gstr6_excel_config.xml";
				} else if (returntype.equals(MasterGSTConstants.GSTR4)) {
					xmlPath = "classpath:mastergst_gstr4_excel_config.xml";
				} else if (returntype.equals(MasterGSTConstants.GSTR5)) {
					xmlPath = "classpath:mastergst_gstr5_excel_config.xml";
				}
				Resource config = resourceLoader.getResource(xmlPath);
				DateConverter converter = new DateConverter();
				String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd","dd-MM-yy","dd-MMM-yy","dd/MM/yy","dd/MMM/yy"};
				converter.setPatterns(patterns);
				ConvertUtils.register(converter, Date.class);
				ReaderConfig.getInstance().setSkipErrors(true);
				XLSReader xlsReader = ReaderBuilder.buildFromXML(config.getInputStream());
				//JxlsHelper.getInstance().setProcessFormulas(false);
				Map<String, List<InvoiceParent>> sheetMap = getExcelSheetMap();
				Map<String, List<InvoiceParent>> beans = Maps.newHashMap();
				beans.put("creditList", sheetMap.get("creditList"));
				beans.put("exportList", sheetMap.get("exportList"));
				beans.put("advReceiptList", sheetMap.get("advReceiptList"));
				beans.put("invoiceList", sheetMap.get("invoiceList"));
				beans.put("advAdjustedList", sheetMap.get("advAdjustedList"));
				beans.put("cdnurList", sheetMap.get("cdnurList"));
				beans.put("nilList", sheetMap.get("nilList"));
				beans.put("b2buList", sheetMap.get("b2buList"));
				beans.put("impgList", sheetMap.get("impgList"));
				beans.put("impsList", sheetMap.get("impsList"));
				beans.put("itrvslList", sheetMap.get("itrvslList"));
				
				beans.put("b2baList", sheetMap.get("b2baList"));
				beans.put("b2claList", sheetMap.get("b2claList"));
				beans.put("b2csaList", sheetMap.get("b2csaList"));
				beans.put("cdnraList", sheetMap.get("cdnraList"));
				beans.put("cdnuraList", sheetMap.get("cdnuraList"));
				beans.put("expaList", sheetMap.get("expaList"));
				beans.put("ataList", sheetMap.get("ataList"));
				beans.put("txpaList", sheetMap.get("txpaList"));
				
				beans.put("hsnSummaryList", sheetMap.get("hsnSummaryList"));
				beans.put("docSummaryList", sheetMap.get("docSummaryList"));
				
				XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
				HSNDetails hsnSummary = new HSNDetails();
				GSTR1DocumentIssue documentIssue = new GSTR1DocumentIssue();
				logger.debug(CLASSNAME + method + "Status: {}", status.isStatusOK());
				if (status.isStatusOK()) {
					Map<String, String> statesMap = newimportService.getStateMap();
					String filename = file.getOriginalFilename();
					Workbook workbook = WorkbookFactory.create(file.getInputStream());
					Workbook workbookError = new HSSFWorkbook();
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
							} else if (key.equals("advReceiptList")) {
								summary.setName("AdvanceReceipt");
							} else if (key.equals("advAdjustedList") || key.equals("advAdjusted")) {
								summary.setName("AdvanceAdjusted");
							} else if (key.equals("cdnurList")) {
								summary.setName("Credit_Debit_Note_Unregistered");
							} else if (key.equals("nilList")) {
								summary.setName("Nil_Exempted_Non-GST");
							} else if (key.equals("b2buList")) {
								summary.setName("B2B_Unregistered");
							} else if (key.equals("impgList")) {
								summary.setName("Import_Goods");
							} else if (key.equals("impsList")) {
								summary.setName("Import_Services");
							} else if (key.equals("itrvslList")) {
								summary.setName("ITC_Reversal");
							} else if (key.equals("b2baList")) {//Amendment invoices key
								summary.setName("b2ba");
							}else if (key.equals("b2csaList")) {
								summary.setName("b2csa");
							} else if (key.equals("b2claList")) {
								summary.setName("b2cla");
							} else if (key.equals("cdnraList")) {
								summary.setName("cdnra");
							} else if (key.equals("cdnuraList")) {
								summary.setName("cdnura");
							} else if (key.equals("expaList")) {
								summary.setName("expa");
							} else if (key.equals("ataList")) {
								summary.setName("ata");
							} else if (key.equals("txpaList")) {
								summary.setName("txpa");
							}else if (key.equals("hsnSummaryList")) {
								summary.setName("hsn");
							} else if (key.equals("docSummaryList")) {
								summary.setName("docs");
							} else {
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
								
								if("hsnSummaryList".equalsIgnoreCase(key)) {
									List<HSNData>  hsnData= Lists.newArrayList();
									int i=1;
									for (InvoiceParent invoice : invoices) {
										if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0))) {
											if(isNotEmpty(invoice.getItems().get(0))) {
												Item item = invoice.getItems().get(0); 
												item = bulkImportTaskService.changeInvoiceAmounts(item);
												List<Item> itms = Lists.newArrayList();
												itms.add(item);
												invoice.setItems(itms);
											}
											if(isNotEmpty(invoice.getItems().get(0).getHsn()) && !"HSN".equalsIgnoreCase(invoice.getItems().get(0).getHsn())) {
											
												HSNData hsndetails = new HSNData();
												hsndetails.setNum(i);
												if(isNotEmpty(invoice.getItems().get(0).getHsn())) {
													hsndetails.setHsnSc(invoice.getItems().get(0).getHsn());
												}
												if(isNotEmpty(invoice.getItems().get(0).getDesc())) {
													String description = invoice.getItems().get(0).getDesc();
													if (description.length() > 30) {
														description = description.substring(0, 27) + "..";
													}
													hsndetails.setDesc(description);
												}
												if(isNotEmpty(invoice.getItems().get(0).getUqc())) {
													String uqc = invoice.getItems().get(0).getUqc();
													if(uqc.contains("-")) {
														String uqcdetails[] = uqc.split("-");
														hsndetails.setUqc(uqcdetails[0]);
													}else {
														hsndetails.setUqc(uqc);
													}
												}
												if(isNotEmpty(invoice.getItems().get(0).getRate())) {
													hsndetails.setRt(invoice.getItems().get(0).getRate());
												}
												if(isNotEmpty(invoice.getItems().get(0).getQuantity())) {
													hsndetails.setQty(invoice.getItems().get(0).getQuantity());
												}else {
													hsndetails.setQty(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getTotal())) {
													hsndetails.setVal(invoice.getItems().get(0).getTotal());
												}else {
													hsndetails.setVal(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
													hsndetails.setTxval(invoice.getItems().get(0).getTaxablevalue());
												}else {
													hsndetails.setTxval(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
													hsndetails.setIamt(invoice.getItems().get(0).getIgstamount());
												}else {
													hsndetails.setIamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getSgstamount())) {
													hsndetails.setSamt(invoice.getItems().get(0).getSgstamount());
												}else {
													hsndetails.setSamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
													hsndetails.setCamt(invoice.getItems().get(0).getCgstamount());
												}else {
													hsndetails.setCamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getCessamount())) {
													hsndetails.setCsamt(invoice.getItems().get(0).getCessamount());
												}else {
													hsndetails.setCsamt(0d);
												}
												hsnData.add(hsndetails);
												i++;
											}
										}
									}
									hsnSummary.setHsnData(hsnData);
								}else if("docSummaryList".equalsIgnoreCase(key)){
									List<GSTR1DocIssueDetails> docDet = Lists.newArrayList();
									Map<String,List<GSTR1DocDetails>> docdetails = Maps.newHashMap();
									List<String> dockeys = Lists.newArrayList();
									for (InvoiceParent invoice : invoices) {
										if(isNotEmpty(invoice.getDocuploads()) && isNotEmpty(invoice.getDocuploads().getDocDet()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0))  && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocnumstring()) && !"Nature of Document".equalsIgnoreCase(invoice.getDocuploads().getDocDet().get(0).getDocnumstring())) {
											if(isEmpty(docdetails)) {
												List<GSTR1DocDetails> dcdet = Lists.newArrayList();
												if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0))) {
													dockeys.add(invoice.getDocuploads().getDocDet().get(0).getDocnumstring());
													GSTR1DocDetails docdetail = new GSTR1DocDetails();
													docdetail.setNum(1);
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
														String invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
														if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
															invnumfrom = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom().trim()).toPlainString();
														}else {
															invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
														}
														docdetail.setFrom(invnumfrom);
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
														String invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
														if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
															invnumto = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo().trim()).toPlainString();
														}else {
															invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
														}
														docdetail.setTo(invnumto);
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum())) {
														docdetail.setTotnum(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum());
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
														docdetail.setCancel(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
														docdetail.setNetIssue(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum() - invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
													}
													dcdet.add(docdetail);
												}
												docdetails.put(invoice.getDocuploads().getDocDet().get(0).getDocnumstring(), dcdet);
											}else {
												if(isNotEmpty(docdetails.get(invoice.getDocuploads().getDocDet().get(0).getDocnumstring()))) {
													List<GSTR1DocDetails> dcdet = docdetails.get(invoice.getDocuploads().getDocDet().get(0).getDocnumstring());
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0))) {
														GSTR1DocDetails docdetail = new GSTR1DocDetails();
														docdetail.setNum(dcdet.size()+1);
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
															String invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
																invnumfrom = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom().trim()).toPlainString();
															}else {
																invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															}
															docdetail.setFrom(invnumfrom);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
															String invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
																invnumto = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo().trim()).toPlainString();
															}else {
																invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															}
															docdetail.setTo(invnumto);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum())) {
															docdetail.setTotnum(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setCancel(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setNetIssue(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum() - invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														dcdet.add(docdetail);
													}
													docdetails.put(invoice.getDocuploads().getDocDet().get(0).getDocnumstring(), dcdet);
												}else {
													dockeys.add(invoice.getDocuploads().getDocDet().get(0).getDocnumstring());
													List<GSTR1DocDetails> dcdet = Lists.newArrayList();
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0))) {
														GSTR1DocDetails docdetail = new GSTR1DocDetails();
														docdetail.setNum(1);
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
															String invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
																invnumfrom = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom().trim()).toPlainString();
															}else {
																invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															}
															docdetail.setFrom(invnumfrom);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
															String invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
																invnumto = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo().trim()).toPlainString();
															}else {
																invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															}
															docdetail.setTo(invnumto);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum())) {
															docdetail.setTotnum(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setCancel(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setNetIssue(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum() - invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														dcdet.add(docdetail);
													}
													docdetails.put(invoice.getDocuploads().getDocDet().get(0).getDocnumstring(), dcdet);
												}
											}
										}
									}
									
									for(String dockey : dockeys) {
										List<GSTR1DocDetails> doclist = docdetails.get(dockey);
										GSTR1DocIssueDetails doc = new GSTR1DocIssueDetails();
										if("Invoices for outward supply".equalsIgnoreCase(dockey)) {
											doc.setDocNum(1);
											doc.setDocs(doclist);
										}else if("Invoices for inward supply from unregistered person".equalsIgnoreCase(dockey)) {
											doc.setDocNum(2);
											doc.setDocs(doclist);
										}else if("Revised Invoice".equalsIgnoreCase(dockey)) {
											doc.setDocNum(3);
											doc.setDocs(doclist);
										}else if("Revised Invoice".equalsIgnoreCase(dockey)) {
											doc.setDocNum(3);
											doc.setDocs(doclist);
										}else if("Debit Note".equalsIgnoreCase(dockey)) {
											doc.setDocNum(4);
											doc.setDocs(doclist);
										}else if("Credit Note".equalsIgnoreCase(dockey)) {
											doc.setDocNum(5);
											doc.setDocs(doclist);
										}else if("Receipt Voucher".equalsIgnoreCase(dockey)) {
											doc.setDocNum(6);
											doc.setDocs(doclist);
										}else if("Payment Voucher".equalsIgnoreCase(dockey)) {
											doc.setDocNum(7);
											doc.setDocs(doclist);
										}else if("Refund Voucher".equalsIgnoreCase(dockey)) {
											doc.setDocNum(8);
											doc.setDocs(doclist);
										}else if("Delivery Challan for job work".equalsIgnoreCase(dockey)) {
											doc.setDocNum(9);
											doc.setDocs(doclist);
										}else if("Delivery Challan for supply on approval".equalsIgnoreCase(dockey)) {
											doc.setDocNum(10);
											doc.setDocs(doclist);
										}else if("Delivery Challan in case of liquid gas".equalsIgnoreCase(dockey)) {
											doc.setDocNum(11);
											doc.setDocs(doclist);
										}else if("Delivery Challan in cases other than by way of supply (excluding at S no. 9 to 11)".equalsIgnoreCase(dockey)) {
											doc.setDocNum(12);
											doc.setDocs(doclist);
										}
										docDet.add(doc);
									}
									if(isNotEmpty(docDet)) {
										documentIssue.setDocDet(docDet);
									}
								}else {
								
								List<Integer> errorIndxes = Lists.newArrayList();
								for (InvoiceParent invoice : invoices) {
									if(((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)))) {
										if(isEmpty(invoice.getStatename())) {
											if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
												invoice.setStatename(client.getStatename());
											}
										}
									}
									String invnum = "", onvnum = "";
									if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
										if(isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getInum())) {
											invnum = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getInum().trim()).toPlainString();
										}else {
											invnum = invoice.getB2b().get(0).getInv().get(0).getInum();
										}
									}
									if ((key.equals("b2baList") || key.equals("b2claList") || key.equals("cdnraList") || key.equals("cdnuraList") || key.equals("expaList")) && isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getOinum())) {
										if(isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getOinum())) {
											onvnum = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getOinum().trim()).toPlainString();
										}else {
											onvnum = invoice.getB2b().get(0).getInv().get(0).getOinum();
										}
									}
									
									String prinvnum = invnum;
									if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
										if(isNotEmpty(invoice.getStrDate())) {
											prinvnum += invoice.getStrDate();
										}
									}
									if(isNotEmpty(tinvoicesmap)) {
										InvoiceParent exstngInv = tinvoicesmap.get(prinvnum);
										if(isEmpty(exstngInv)) {
											totalinvs++;
											tinvoicesmap.put(prinvnum, invoice);
										}
									}else {
										totalinvs++;
										tinvoicesmap.put(prinvnum, invoice);
									}
									if(key.equals("b2baList") || key.equals("b2bclaList") || key.equals("cdnraList") || key.equals("cdnuraList") || key.equals("expaList")) {
										if ((isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getOinum()) && isNotEmpty(onvnum.trim()) && !Pattern.matches(INVOICENO_REGEX, onvnum.trim()))) {
											
											if(!"Original Invoice No*".equalsIgnoreCase(onvnum)) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
														&& isNotEmpty(datatypeSheet.getRow(index))) {
													if(index != 1) {
														Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Original Invoice Number Format");
														gstincell.setCellStyle(style);
													}
													errorIndxes.add(index);
												}
											}
										}
									}
									if ((isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(INVOICENO_REGEX, invnum.trim()))) {
										if(!"Invoice No*".equalsIgnoreCase(invnum)) {
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
													&& (key.equals("creditList") || key.equals("cdnraList") || key.equals("cdnurList") || key.equals("cdnuraList") || key.equals("exportList") || key.equals("expaList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| ((key.equals("cdnraList") || key.equals("creditList")) && (isEmpty(((GSTR1) invoice).getCdnr())
															|| isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())
															|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt())
															|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| ((key.equals("cdnurList") || key.equals("cdnuraList")) && (isEmpty(((GSTR1) invoice).getCdnur()) || "Credit/Debit Note Date*".equals(invoice.getStrDate())
															|| isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
															|| ((key.equals("exportList") || key.equals("expaList")) && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate()) || "Invoice Date*".equals(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| (isNotEmpty(invoice.getItems().get(0).getIgstrate())	&& invoice.getItems().get(0).getIgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstrate())	&& invoice.getItems().get(0).getCgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstrate())	&& invoice.getItems().get(0).getSgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCessrate())	&& invoice.getItems().get(0).getCessrate() < 0))) {

												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
													errorcell.setCellValue(errorVal);
													errorcell.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else if (returntype.equals(MasterGSTConstants.GSTR4)
													&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| (key.equals("creditList") && (isEmpty(((GSTR4) invoice).getCdnr()) || isEmpty(((GSTR4) invoice).getB2b().get(0).getCtin()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtNum()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
													|| (key.equals("cdnurList")	&& (isEmpty(((GSTR4) invoice).getCdnur()) || isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtNum()) || isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtty())))
													|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b())	|| isEmpty(invoice.getB2b().get(0).getCtin())))
													|| (key.equals("exportList") && (isEmpty(invoice.getExp())	|| isEmpty(invoice.getExp().get(0).getExpTyp())))
													|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
													|| (isNotEmpty(invoice.getItems().get(0).getIgstrate())	&& invoice.getItems().get(0).getIgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCgstrate())	&& invoice.getItems().get(0).getCgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getSgstrate())	&& invoice.getItems().get(0).getSgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCessrate())	&& invoice.getItems().get(0).getCessrate() < 0))) {

												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
													datatypeSheet.getRow(index).createCell(lastcolumn).setCellValue("Please Enter Valid Values");
													errorIndxes.add(index);
												}
											} else if (key.equals("nilList") && (isEmpty(invoice.getStatename())
													|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| isEmpty(invoice.getItems().get(0).getType())
													|| isEmpty(invoice.getItems().get(0).getTotal())
													|| isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													if(isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
														errorVal += "Please Enter Invoice Number,";
													}
													Cell nilError = datatypeSheet.getRow(index).createCell(lastcolumn);
													nilError.setCellValue(errorVal);
													nilError.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else if (key.equals("itrvslList")	&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| isEmpty(invoice.getB2b().get(0).getCtin()) || isEmpty(invoice.getItems())
															|| isEmpty(invoice.getItems().get(0).getItcRevtype())
															|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| (isNotEmpty(invoice.getItems().get(0).getIgstamount()) && invoice.getItems().get(0).getIgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCgstamount()) && invoice.getItems().get(0).getCgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getSgstamount()) && invoice.getItems().get(0).getSgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getIsdcessamount()) && invoice.getItems().get(0).getIsdcessamount() < 0)
															|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													Cell itcError = datatypeSheet.getRow(index).createCell(lastcolumn);
													itcError.setCellValue(errorVal);
													itcError.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else if (key.equals("impgList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													Cell impgError = datatypeSheet.getRow(index).createCell(lastcolumn);
													impgError.setCellValue(errorVal);
													impgError.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else if (key.equals("impgList") && (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid Invoice Number Format");
													gstincell.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else if (key.equals("impsList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
													impsError.setCellValue(errorVal);
													impsError.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else if ((key.equals("advAdjustedList") || key.equals("txpaList"))&& (isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													Cell advAdjustError = datatypeSheet.getRow(index).createCell(lastcolumn);
													advAdjustError.setCellValue(errorVal);
													advAdjustError.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("txpaList")
													&& !key.equals("itrvslList")
													&& ((isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
													|| (isNotEmpty(invoice.getItems()) 
													&& ((isNotEmpty(invoice.getItems().get(0).getIgstrate()) && invoice.getItems().get(0).getIgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCgstrate())	&& invoice.getItems().get(0).getCgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getSgstrate())	&& invoice.getItems().get(0).getSgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCessrate())	&& invoice.getItems().get(0).getCessrate() < 0)))))
													|| ((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2))
													&& key.equals("invoiceList") && (isEmpty(invoice.getB2b().get(0).getCtin()) || isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = "";
													if(((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)))) {
														if(key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getCtin())) {
															errorVal += "Please Enter GSTIN Number,";
														}
														if(key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
															errorVal += "Please Enter Invoice Number,";
														}
													}
													if(isEmpty(invoice.getStatename())) {
														errorVal += "Please Enter Statename,";
													}
													if(isEmpty(invoice.getStrDate())) {
														errorVal += "Please Enter Invoice Date,";
													}
													if(key.equals("b2baList")) {
														if(isEmpty(invoice.getStrOdate())) {
															errorVal += "Please Enter Original Invoice Date,";
														}
														if(isEmpty(invoice.getB2b().get(0).getInv().get(0).getOinum())) {
															errorVal += "Please Enter Original Invoice Number,";
														}
													}
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
														errorVal += "Please Enter valid Cess Rate";
													}
													Cell errors = datatypeSheet.getRow(index).createCell(lastcolumn);
													errors.setCellValue(errorVal);
													errors.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else {
												invoice = newimportService.importInvoicesHsnQtyRateMandatory(invoice,returntype,clientid,branch,vertical,month,year,patterns, statesMap);
												if(isNotEmpty(invoice.getItems().get(0))) {
													Item item = invoice.getItems().get(0); 
													item = bulkImportTaskService.changeInvoiceAmounts(item);
													List<Item> itms = Lists.newArrayList();
													itms.add(item);
													invoice.setItems(itms);
												}
												if(isNotEmpty(invoice.getItems().get(0))) {
													Item item = invoice.getItems().get(0); 
													item = bulkImportTaskService.changeInvoiceAmounts(item);
													List<Item> itms = Lists.newArrayList();
													itms.add(item);
													invoice.setItems(itms);
												}
												
												if(key.equals("b2csaList") || key.equals("ataList") || key.equals("txpaList")) {
													String pFp = null;
													
													if(isNotEmpty(invoice.getStrOdate())){
														
														Date dt = newimportService.invdate(invoice.getStrOdate(),patterns,year,month);
														if(dt != null){
															int iMonth = dt.getMonth()+1;
															int iYear = dt.getYear()+1900;
															pFp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
														}
													}
													if(pFp == null){
														Calendar cal = Calendar.getInstance();
														cal.setTime(invoice.getDateofinvoice());
														int iMonth = cal.get(Calendar.MONTH) + 1;
														int iYear = cal.get(Calendar.YEAR);
														pFp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
													}
													
													if(key.equals("txpaList")) {
														((GSTR1)invoice).getTxpd().get(0).setOmon(pFp);
													}
													if(key.equals("ataList")) {
														((GSTR1)invoice).getAt().get(0).setOmon(pFp);
													}
													if(key.equals("b2csaList")) {
														((GSTR1)invoice).getB2cs().get(0).setOmon(pFp);
													}
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
													//mapkey = mapkey+fp.trim();
													if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
														if(isNotEmpty(invoice.getStrDate())) {
															mapkey = mapkey+invoice.getStrDate();
														}else {
															mapkey = mapkey+fp.trim();
														}
													}else {
														mapkey = mapkey+fp.trim();
													}
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
													&& (key.equals("creditList") || key.equals("cdnraList") || key.equals("cdnurList") || key.equals("cdnuraList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| ((key.equals("creditList") || key.equals("cdnraList")) && (isEmpty(((GSTR1) invoice).getCdnr())
													|| isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())	|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
													|| ((key.equals("cdnurList") || key.equals("cdnurList")) && (isEmpty(((GSTR1) invoice).getCdnur()) || isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
													|| ((key.equals("exportList") || key.equals("expaList")) && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
													|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
													|| isEmpty(invoice.getItems().get(0).getHsn()) || isEmpty(invoice.getItems().get(0).getQuantity()) || (invoice.getItems().get(0).getQuantity() <= 0)
													|| (isNotEmpty(invoice.getItems().get(0).getRateperitem())	&& invoice.getItems().get(0).getRateperitem() < 0) 
													|| (isNotEmpty(invoice.getItems().get(0).getIgstrate())	&& invoice.getItems().get(0).getIgstrate() < 0)	
													|| (isNotEmpty(invoice.getItems().get(0).getCgstrate())	&& invoice.getItems().get(0).getCgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getSgstrate())	&& invoice.getItems().get(0).getSgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCessrate())	&& invoice.getItems().get(0).getCessrate() < 0))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
													errorcell.setCellValue(errorVal);
													errorcell.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else if (returntype.equals(MasterGSTConstants.GSTR4)
													&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| (key.equals("creditList") && (isEmpty(((GSTR4) invoice).getCdnr()) || isEmpty(((GSTR4) invoice).getB2b().get(0).getCtin()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtNum()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
													|| (key.equals("cdnurList")	&& (isEmpty(((GSTR4) invoice).getCdnur()) || isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtNum()) || isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtty())))
													|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())))
													|| (key.equals("exportList") && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
													|| isEmpty(invoice.getItems().get(0).getHsn()) || isEmpty(invoice.getItems().get(0).getQuantity()) || (invoice.getItems().get(0).getQuantity() <= 0) || (isNotEmpty(invoice.getItems().get(0).getRateperitem()) && invoice.getItems().get(0).getRateperitem() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) && invoice.getItems().get(0).getIgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) && invoice.getItems().get(0).getCgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) && invoice.getItems().get(0).getSgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) && invoice.getItems().get(0).getCessrate() < 0))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													datatypeSheet.getRow(index).createCell(lastcolumn).setCellValue("Please Enter Valid Values");
													errorIndxes.add(index);
												}
											} else if (key.equals("nilList") && (isEmpty(invoice.getStatename())
													|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
													|| isEmpty(invoice.getItems().get(0).getHsn())
													|| isEmpty(invoice.getItems().get(0).getUqc())
													|| isEmpty(invoice.getItems().get(0).getQuantity())
													|| isEmpty(invoice.getItems().get(0).getRateperitem())
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| isEmpty(invoice.getItems().get(0).getType())
													|| isEmpty(invoice.getItems().get(0).getTotal())
													|| isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													if(isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
														errorVal += "Please Enter Invoice Number,";
													}
													Cell nilError = datatypeSheet.getRow(index).createCell(lastcolumn);
													nilError.setCellValue(errorVal);
													nilError.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else if (key.equals("itrvslList")
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getB2b().get(0).getCtin())
															|| isEmpty(invoice.getItems())
															|| isEmpty(invoice.getItems().get(0).getItcRevtype())
															|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
															|| (isNotEmpty(invoice.getItems().get(0).getIgstamount()) && invoice.getItems().get(0).getIgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstamount()) && invoice.getItems().get(0).getCgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstamount()) && invoice.getItems().get(0).getSgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getIsdcessamount()) && invoice.getItems().get(0).getIsdcessamount() < 0)
															|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													Cell itcError = datatypeSheet.getRow(index).createCell(lastcolumn);
													itcError.setCellValue(errorVal);
													itcError.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else if (key.equals("impgList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													Cell impgError = datatypeSheet.getRow(index).createCell(lastcolumn);
													impgError.setCellValue(errorVal);
													impgError.setCellStyle(style);
													errorIndxes.add(index);
												}
											}else if (key.equals("impgList") && (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid Invoice Number Format");
													gstincell.setCellStyle(style);
													errorIndxes.add(index);
												}
											}  else if (key.equals("impsList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
													impsError.setCellValue(errorVal);
													impsError.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else if ((key.equals("advAdjustedList") || key.equals("txpaList")) && (isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													Cell advAdjustError = datatypeSheet.getRow(index).createCell(lastcolumn);
													advAdjustError.setCellValue(errorVal);
													advAdjustError.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("itrvslList")
													&& ((isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems()) 
													&& (isEmpty(invoice.getItems().get(0).getHsn())
													|| isEmpty(invoice.getItems().get(0).getQuantity())
													|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) && invoice.getItems().get(0).getIgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) && invoice.getItems().get(0).getCgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) && invoice.getItems().get(0).getSgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) && invoice.getItems().get(0).getCessrate() < 0)))))
													|| ((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2))
													&& key.equals("invoiceList") && (isEmpty(invoice.getB2b().get(0).getCtin()) || isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													String errorVal = "";
													if(((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)))) {
														if(key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getCtin())) {
															errorVal += "Please Enter GSTIN Number,";
														}
														if(key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
															errorVal += "Please Enter Invoice Number,";
														}
													}
													if(isEmpty(invoice.getStatename())) {
														errorVal += "Please Enter Statename,";
													}
													if(isEmpty(invoice.getStrDate())) {
														errorVal += "Please Enter Invoice Date,";
													}
													if(isEmpty(invoice.getItems().get(0).getHsn())) {
														errorVal += "Please Enter HSN,";
													}
													if(isEmpty(invoice.getItems().get(0).getQuantity())) {
														errorVal += "Please Enter Quantity,";
													}
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
														errorVal += "Please Enter valid Cess Rate";
													}
													Cell errors = datatypeSheet.getRow(index).createCell(lastcolumn);
													errors.setCellValue(errorVal);
													errors.setCellStyle(style);
													errorIndxes.add(index);
												}
											} else {
												invoice = newimportService.importInvoicesHsnQtyRateMandatory(invoice,returntype,clientid,branch,vertical,month,year,patterns, statesMap);
												if(isNotEmpty(invoice.getItems().get(0))) {
													Item item = invoice.getItems().get(0); 
													item = bulkImportTaskService.changeInvoiceAmounts(item);
													List<Item> itms = Lists.newArrayList();
													itms.add(item);
													invoice.setItems(itms);
												}
												
												if(key.equals("b2csaList") || key.equals("ataList") || key.equals("txpaList")) {
													String pFp = null;
													
													if(isNotEmpty(invoice.getStrOdate())){
														
														Date dt = newimportService.invdate(invoice.getStrOdate(),patterns,year,month);
														if(dt != null){
															int iMonth = dt.getMonth();
															int iYear = dt.getYear()+1900;
															pFp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
														}
													}
													if(pFp == null){
														Calendar cal = Calendar.getInstance();
														cal.setTime(invoice.getDateofinvoice());
														int iMonth = cal.get(Calendar.MONTH) + 1;
														int iYear = cal.get(Calendar.YEAR);
														pFp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
													}
													
													if(key.equals("txpaList")) {
														((GSTR1)invoice).getTxpd().get(0).setOmon(pFp);
													}
													if(key.equals("ataList")) {
														((GSTR1)invoice).getAt().get(0).setOmon(pFp);
													}
													if(key.equals("b2csaList")) {
														((GSTR1)invoice).getB2cs().get(0).setOmon(pFp);
													}
												}
												
												
												
												if(isNotEmpty(temptype) && temptype.equalsIgnoreCase("mastergst_all_fileds")){
													if(isEmpty(invoice.getIncludetax())) {
													invoice.setIncludetax("No");
												}
													if(isNotEmpty(invoice.getSamebilladdress())) {
														if("YES".equalsIgnoreCase(invoice.getSamebilladdress()) && (isNotEmpty(invoice.getB2b())
																&& isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0))
																&& isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress()))) {
															invoice.setConsigneeaddress(invoice.getB2b().get(0).getInv().get(0).getAddress());
														}
													}
													
													if(isNotEmpty(invoice.getTermDays())) {
														try {
															int terms  = Integer.parseInt(invoice.getTermDays());	
															if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
																Calendar cal = Calendar.getInstance();
																cal.setTime(invoice.getDateofinvoice());
																cal.add(Calendar.DATE, terms);
																Date currentDatePlusTerms = cal.getTime();
																invoice.setDueDate(currentDatePlusTerms);
															}
														}catch (Exception e) {
															invoice.setTermDays("0");
														}
													}
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
													invoice.setFp(fp);
													String mapkey = "";
													if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
														mapkey = mapkey+invoice.getB2b().get(0).getInv().get(0).getInum().trim();
													}
													mapkey = mapkey+key.trim();
													//mapkey = mapkey+fp.trim();
													if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
														if(isNotEmpty(invoice.getStrDate())) {
															mapkey = mapkey+invoice.getStrDate();
														}else {
															mapkey = mapkey+fp.trim();
														}
													}else {
														mapkey = mapkey+fp.trim();
													}
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
						}
							if("hsnSummaryList".equalsIgnoreCase(key) || "docSummaryList".equalsIgnoreCase(key)) {
								summary.setInvsuccess(summary.getTotal());
								summary.setSuccess(summary.getTotal());
								summary.setTotalinvs(summary.getTotal());
								summary.setInvfailed(summary.getFailed());
							}else {
								summary.setFailed((failed));
								summary.setTotalinvs(totalinvs-1);
								summary.setInvsuccess(invsucess);
								summary.setInvfailed(totalinvs-1 - invsucess);
							}
							summaryList.add(summary);
						}
					}
					try {
						workbookError.write(fos);
						fos.flush();
						fos.close();
					} catch (IOException e) {
						logger.debug(CLASSNAME + method + "XLS Write ERROR {}", e);
						convFile = null;
					}finally {
						convFile.deleteOnExit();
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
							if (summary.getInvfailed() > 0) {
								failedCount += summary.getInvfailed();
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
					String hsnFp = null;
					if(hsnfinancialYear == null || hsnmonth == null || hsnfinancialYear.trim() == "" || hsnmonth.trim() == "") {
						Calendar cal = Calendar.getInstance();
						int mth = cal.get(Calendar.MONTH)+1;
						int yr = cal.get(Calendar.YEAR);
						hsnFp = mth <= 9 ? "0"+mth+""+yr : mth+""+yr+"";
					}else {
						int mth = Integer.parseInt(hsnmonth);
						int yr = Integer.parseInt(hsnfinancialYear);
						hsnFp = mth <= 9 ? "0"+mth+""+yr : mth+""+yr+"";
					}
					hsnSummary.setClientid(clientid);
					hsnSummary.setUserid(id);
					hsnSummary.setReturnPeriod(hsnFp);
					hsnSummary.setReturnType(returntype);
					hsnSummary.setReturnPeriod(hsnFp);
					hsnSummary.setImporttype("GSTR1 MasterGST Template");
					HSNDetails hsndetails = hsnSummaryRepository.findByClientidAndReturnPeriodAndReturnType(clientid,hsnFp,returntype);
					if(isEmpty(hsndetails)) {
						hsndetails = new HSNDetails();
						hsndetails.setClientid(clientid);
						hsndetails.setUserid(id);
						hsndetails.setReturnPeriod(hsnFp);
						hsndetails.setReturnType(returntype);
						hsndetails.setImporttype("GSTR1 MasterGST Template");
						hsndetails.setHsnData(hsnSummary.getHsnData());
					}else {
						if(isNotEmpty(hsnSummary) && isNotEmpty(hsnSummary.getHsnData()) && hsnSummary.getHsnData().size() > 0) {
							hsndetails.setImporttype("GSTR1 MasterGST Template");
							hsndetails.setHsnData(hsnSummary.getHsnData());
						}
					}
					hsnSummaryRepository.save(hsndetails);
					
					documentIssue.setClientid(clientid);
					documentIssue.setReturnPeriod(hsnFp);
					GSTR1DocumentIssue docdetails = gstr1DocumentIssueRepository.findByClientidAndReturnPeriod(clientid, hsnFp);
					if(isNotEmpty(docdetails)) {
						docdetails.setClientid(clientid);
						docdetails.setReturnPeriod(hsnFp);
						docdetails.setDocDet(documentIssue.getDocDet());
					}else {
						docdetails = documentIssue;
					}
					gstr1DocumentIssueRepository.save(docdetails);
					
					String invReturnType = returntype;
					if (returntype.equals(MasterGSTConstants.GSTR2)) {
						invReturnType = MasterGSTConstants.PURCHASE_REGISTER;
					}
					newimportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, MASTERGST_EXCEL_TEMPLATE,otherconfig);
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

	@RequestMapping(value = "/uploadmapImportInvoice/{templateName}/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody ImportResponse uploadmapImportInvoice(@PathVariable("templateName") String templateName,
			@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month,
			@PathVariable("year") int year, @RequestParam("file") MultipartFile file,
			@RequestParam("list") List<String> list,@RequestParam(value = "branch", required = false) String branch,
			@RequestParam(value = "tallyfinancialYear", required = false) String hsnfinancialYear, @RequestParam(value = "tallymonth", required = false) String hsnmonth,
			@RequestParam(value = "vertical", required = false) String vertical, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "uploadmapImportInvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
		if (isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableSalesFields())) {
			otherconfig.setEnableSalesFields(false);
		}
		if (isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnablePurFields())) {
			otherconfig.setEnablePurFields(false);
		}

		boolean hsnqtyrate = false;
		if (isNotEmpty(otherconfig)) {
			if ("GSTR1".equals(returntype) || "Sales Register".equals(returntype)
					|| "SalesRegister".equals(returntype)) {
				hsnqtyrate = otherconfig.isEnableSalesFields();
			} else {
				hsnqtyrate = otherconfig.isEnablePurFields();
			}
		}
		TemplateMapperDoc templateMapperDoc = importMapperService.getMapperDoc(id, clientid, templateName);
		TemplateMapper templateMapper = importMapperService.getMapper(id, clientid, templateName);

		String simplifiedOrDetail = "Detailed";

		if (isNotEmpty(templateMapper) && isNotEmpty(templateMapper.getSimplifiedOrDetail())) {
			simplifiedOrDetail = templateMapper.getSimplifiedOrDetail();
		}
		ImportResponse response = new ImportResponse();
		String hsnFp = null;
		if(hsnfinancialYear == null || hsnmonth == null || hsnfinancialYear.trim() == "" || hsnmonth.trim() == "") {
			Calendar cal = Calendar.getInstance();
			int mth = cal.get(Calendar.MONTH)+1;
			int yr = cal.get(Calendar.YEAR);
			hsnFp = mth <= 9 ? "0"+mth+""+yr : mth+""+yr+"";
		}else {
			int mth = Integer.parseInt(hsnmonth);
			int yr = Integer.parseInt(hsnfinancialYear);
			hsnFp = mth <= 9 ? "0"+mth+""+yr : mth+""+yr+"";
		}
		if ("Simplified".equalsIgnoreCase(simplifiedOrDetail)) {
			response = importMapperSimplified(id, clientid, templateName, file, returntype, month, year, list,
					fullname, templateMapper, templateMapperDoc, hsnqtyrate,branch,vertical,otherconfig, hsnFp, request);
		} else {
			response = importMapperDetailed(id, clientid, templateName, file, returntype, month, year, list, fullname,
					templateMapper, templateMapperDoc, hsnqtyrate,branch,vertical,otherconfig, hsnFp, request);
		}

		
		return response;
	}

	@RequestMapping(value = "/uploadTallyInvoice/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody ImportResponse uploadTallyInvoice(@PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
			@RequestParam("file") MultipartFile file, @RequestParam("list") List<String> list,
			@RequestParam(value = "branch", required = false) String branch,
			@RequestParam(value = "vertical", required = false) String vertical, @RequestParam(value = "tallyfinancialYear", required = false) String tallyfinancialYear, @RequestParam(value = "tallymonth", required = false) String tallymonth, @PathVariable("month") int month,
			@PathVariable("year") int year, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "uploadTallyInvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "returntype: {}", returntype);
		logger.debug(CLASSNAME + method + "branch: {}", branch);
		logger.debug(CLASSNAME + method + "vertical: {}", vertical);
		logger.debug(CLASSNAME + method + "financialYear: {}", tallyfinancialYear);
		logger.debug(CLASSNAME + method + "tallymonth: {}", tallymonth);
		Client client = clientService.findById(clientid);
		OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
		int skipRows = 4;
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		if (!file.isEmpty()) {
			logger.debug(CLASSNAME + method + file.getOriginalFilename());
			try {
				String xmlPath = "classpath:tally_excel_config.xml";
				if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)
						|| returntype.equals(MasterGSTConstants.GSTR2)) {
					xmlPath = "classpath:tally_purchase_excel_config.xml";
				}else {
					skipRows = 4;
				}
				Resource config = resourceLoader.getResource(xmlPath);
				DateConverter converter = new DateConverter();
				String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd" };
				converter.setPatterns(patterns);
				ConvertUtils.register(converter, Date.class);
				ReaderConfig.getInstance().setSkipErrors(true);
				XLSReader xlsReader = ReaderBuilder.buildFromXML(config.getInputStream());
				Map<String, List<InvoiceParent>> sheetMap = getExcelSheetMap();
				Map<String, List<InvoiceParent>> beans = Maps.newHashMap();
				beans.put("creditList", sheetMap.get("creditList"));
				beans.put("exportList", sheetMap.get("exportList"));
				beans.put("advReceiptList", sheetMap.get("advReceiptList"));
				beans.put("b2bList", sheetMap.get("b2bList"));
				beans.put("b2cList", sheetMap.get("b2cList"));
				beans.put("b2clList", sheetMap.get("b2clList"));
				beans.put("b2buList", sheetMap.get("b2buList"));
				beans.put("cdnurList", sheetMap.get("cdnurList"));
				beans.put("nilList", sheetMap.get("nilList"));
				beans.put("impgList", sheetMap.get("impgList"));
				beans.put("impsList", sheetMap.get("impsList"));
				beans.put("itrvslList", sheetMap.get("itrvslList"));
				beans.put("hsnSummaryList", sheetMap.get("hsnSummaryList"));
				XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
				HSNDetails hsnSummary = new HSNDetails();
				logger.debug(CLASSNAME + method + "Status: {}", status.isStatusOK());
				if (status.isStatusOK()) {
					logger.debug(CLASSNAME + method + "creditList: {}", beans.get("creditList"));
					logger.debug(CLASSNAME + method + "exportList: {}", beans.get("exportList"));
					logger.debug(CLASSNAME + method + "advReceiptList: {}", beans.get("advReceiptList"));
					logger.debug(CLASSNAME + method + "b2bList: {}", beans.get("b2bList"));
					logger.debug(CLASSNAME + method + "b2cList: {}", beans.get("b2cList"));
					logger.debug(CLASSNAME + method + "b2clList: {}", beans.get("b2clList"));
					Map<String, String> statesMap = newimportService.getStateMap();
					String filename = file.getOriginalFilename();
					Workbook workbook = WorkbookFactory.create(file.getInputStream());
					Workbook workbookError = new HSSFWorkbook();
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
								summary.setName("cdnr");
							} else if (key.equals("exportList")) {
								summary.setName("exp");
							} else if (key.equals("advReceiptList")) {
								summary.setName("at");
							} else if (key.equals("advAdjustedList")) {
								summary.setName("atadj");
							} else if (key.equals("cdnurList")) {
								summary.setName("cdnur");
							} else if (key.equals("nilList")) {
								summary.setName("exemp");
							} else if (key.equals("b2buList")) {
								summary.setName("b2bur");
							} else if (key.equals("impgList")) {
								summary.setName("impg");
							} else if (key.equals("impsList")) {
								summary.setName("imps");
							} else if (key.equals("itrvslList")) {
								summary.setName("itcr");
							} else if (key.equals("b2bList")) {
								summary.setName("b2b");
							} else if (key.equals("b2clList")) {
								summary.setName("b2cl");
							} else if (key.equals("b2cList")) {
								summary.setName("b2cs");
							} else if (key.equals("hsnSummaryList")) {
								summary.setName("hsn");
							} else {
								summary.setName(key);
							}
							Sheet datatypeSheet = workbook.getSheet(summary.getName());
							Sheet datatypeSheetError = workbookError.getSheet(summary.getName());
							CellStyle style = workbook.createCellStyle();
							Font font = workbook.createFont();
					        font.setColor(IndexedColors.RED.getIndex());
					        style.setFont(font);
					        Cell cell = datatypeSheet.getRow(skipRows-1).createCell(datatypeSheet.getRow(skipRows-1).getLastCellNum());
							cell.setCellValue("Errors");
							cell.setCellStyle(style);
							int lastcolumn = datatypeSheet.getRow(1).getLastCellNum()-1;
							if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
								WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 0, 0);
								WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 1, 1);
								WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 2, 2);
								WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 3, 3);
							}else {
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 0, 0);
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 1, 1);
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 2, 2);
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 3, 3);
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
							
							if (isNotEmpty(invoices) && invoices.size() > 0) {
								int index = skipRows - 1;
								if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)
										|| returntype.equals(MasterGSTConstants.GSTR2)) {
									summary.setTotal(beans.get(key).size()-1);
								}else {
									summary.setTotal(beans.get(key).size()-1);
								}
								//List<Row> errorRows = Lists.newArrayList();
								List<Integer> errorIndxes = Lists.newArrayList();
								if("hsnSummaryList".equalsIgnoreCase(key)) {
									List<HSNData>  hsnData= Lists.newArrayList();
									int i=1;
									for (InvoiceParent invoice : invoices) {
										if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0))) {
											if(isNotEmpty(invoice.getItems().get(0))) {
												Item item = invoice.getItems().get(0); 
												item = bulkImportTaskService.changeInvoiceAmounts(item);
												List<Item> itms = Lists.newArrayList();
												itms.add(item);
												invoice.setItems(itms);
											}
											if(isNotEmpty(invoice.getItems().get(0).getHsn()) && !"HSN".equalsIgnoreCase(invoice.getItems().get(0).getHsn())) {
											HSNData hsndetails = new HSNData();
											hsndetails.setNum(i);
											if(isNotEmpty(invoice.getItems().get(0).getHsn())) {
												hsndetails.setHsnSc(invoice.getItems().get(0).getHsn());
											}
											if(isNotEmpty(invoice.getItems().get(0).getDesc())) {
												String description = invoice.getItems().get(0).getDesc();
												if (description.length() > 30) {
													description = description.substring(0, 27) + "..";
												}
												hsndetails.setDesc(description);
											}
											if(isNotEmpty(invoice.getItems().get(0).getUqc())) {
												String uqc = invoice.getItems().get(0).getUqc();
												if(uqc.contains("-")) {
													String uqcdetails[] = uqc.split("-");
													hsndetails.setUqc(uqcdetails[0]);
												}else {
													hsndetails.setUqc(uqc);
												}
											}
											if(isNotEmpty(invoice.getItems().get(0).getQuantity())) {
												hsndetails.setQty(invoice.getItems().get(0).getQuantity());
											}else {
												hsndetails.setQty(0d);
											}
											if(isNotEmpty(invoice.getItems().get(0).getTotal())) {
												hsndetails.setVal(invoice.getItems().get(0).getTotal());
											}else {
												hsndetails.setVal(0d);
											}
											if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
												hsndetails.setTxval(invoice.getItems().get(0).getTaxablevalue());
											}else {
												hsndetails.setTxval(0d);
											}
											if(isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
												hsndetails.setIamt(invoice.getItems().get(0).getIgstamount());
											}else {
												hsndetails.setIamt(0d);
											}
											if(isNotEmpty(invoice.getItems().get(0).getSgstamount())) {
												hsndetails.setSamt(invoice.getItems().get(0).getSgstamount());
											}else {
												hsndetails.setSamt(0d);
											}
											if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
												hsndetails.setCamt(invoice.getItems().get(0).getCgstamount());
											}else {
												hsndetails.setCamt(0d);
											}
											if(isNotEmpty(invoice.getItems().get(0).getCessamount())) {
												hsndetails.setCsamt(invoice.getItems().get(0).getCessamount());
											}else {
												hsndetails.setCsamt(0d);
											}
											hsnData.add(hsndetails);
											i++;
											}
										}
									}
									hsnSummary.setHsnData(hsnData);
								}else {	
									for (InvoiceParent invoice : invoices) {
										String invnum = "";
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
											
											if(isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getInum())) {
												invnum = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getInum().trim()).toPlainString();
											}else {
												invnum = invoice.getB2b().get(0).getInv().get(0).getInum();
											}
										}
										if(!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("b2cList") && !key.equals("nilList")) {
											String prinvnum = invnum;
											if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
												if(isNotEmpty(invoice.getStrDate())) {
													prinvnum += invoice.getStrDate();
												}
											}
											if(isNotEmpty(tinvoicesmap)) {
												InvoiceParent exstngInv = tinvoicesmap.get(prinvnum);
												if(isEmpty(exstngInv)) {
													totalinvs++;
													tinvoicesmap.put(prinvnum, invoice);
												}
											}else {
												totalinvs++;
												tinvoicesmap.put(prinvnum, invoice);
											}
										}
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(INVOICENO_REGEX, invnum.trim())) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													if(index != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Invoice Number Format");
														gstincell.setCellStyle(style);
													}
													errorIndxes.add(index);
												}
										}else if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().toUpperCase())) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid GSTIN Number");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										} else if (returntype.equals(MasterGSTConstants.GSTR1)
												&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStrDate())
												|| (key.equals("creditList") && (isEmpty(((GSTR1) invoice).getCdnr()) || isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt())|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtNum()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
												|| (key.equals("cdnurList") && (isEmpty(((GSTR1) invoice).getCdnur()) || isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtNum()) || isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
												|| (key.equals("exportList") && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
														|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = newimportService.tallyErrorMsg(key,invoice,returntype);
												Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
												impsError.setCellValue(errorVal);
												impsError.setCellStyle(style);
												errorIndxes.add(index);
											}
										} else if (returntype.equals(MasterGSTConstants.GSTR4)
												&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStrDate())
												|| (key.equals("creditList") && (isEmpty(((GSTR4) invoice).getCdnr()) || isEmpty(((GSTR4) invoice).getB2b().get(0).getCtin()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtNum()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
												|| (key.equals("cdnurList") && (isEmpty(((GSTR4) invoice).getCdnur()) || isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtNum()) || isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtty())))
												|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())))
												|| (key.equals("exportList") && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
														|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												datatypeSheet.getRow(index).createCell(lastcolumn).setCellValue("Please Enter Valid Values");
												errorIndxes.add(index);
											}
										}else if(key.equals("b2cList") && (isEmpty(invoice.getStatename()) || "Place Of Supply".equalsIgnoreCase(invoice.getStatename())   )) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = newimportService.tallyErrorMsg(key,invoice,returntype);
												Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
												impsError.setCellValue(errorVal);
												impsError.setCellStyle(style);
												errorIndxes.add(index);
											}
										} else if (!key.equals("nilList") && !key.equals("impgList") && !key.equals("itrvslList") && (!key.equals("b2cList") && !key.equals("creditList")
													&& (isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue()))))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = "";
												if(isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue()))) {
													errorVal += "Please Enter Values";
												}
												Cell error = datatypeSheet.getRow(index).createCell(lastcolumn);
												error.setCellValue(errorVal);
												error.setCellStyle(style);
												errorIndxes.add(index);
											}
										}else if (key.equals("impgList") && (isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue())))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = newimportService.tallyErrorMsg(key,invoice,returntype);
												Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
												impsError.setCellValue(errorVal);
												impsError.setCellStyle(style);
												errorIndxes.add(index);
											}
										}else if (key.equals("impgList") && (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim()))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Invalid Invoice Number Format");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}  else {
											invoice = newimportService.importInvoicesTally(invoice, client, returntype, branch, vertical,month,year,patterns,statesMap);
											if(key.equals("b2cList")) {
												if(isNotEmpty(tallyfinancialYear) && isNotEmpty(tallymonth)) {
													Calendar cal = Calendar.getInstance();
													int b2ctmnth = Integer.parseInt(tallymonth);
													int b2cyear = Integer.parseInt(tallyfinancialYear);
													if(b2ctmnth >= 1 && b2ctmnth < 4) {
														b2cyear = b2cyear+1;
													}
													cal.set(b2cyear, b2ctmnth - 1, 1);
													invoice.setDateofinvoice(cal.getTime());
												}
											}
											filteredList.add(invoice);
											if(!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("b2cList") && !key.equals("nilList")) {
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
													if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
														if(isNotEmpty(invoice.getStrDate())) {
															mapkey = mapkey+invoice.getStrDate();
														}else {
															mapkey = mapkey+fp.trim();
														}
													}else {
														mapkey = mapkey+fp.trim();
													}
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
										index++;
									}
								}
								if (skipRows == 1) {
									summary.setSuccess((beans.get(key).size()) - failed);
								} else {
									if (failed != 0) {
										failed = failed - 1;
									}
									summary.setSuccess((beans.get(key).size() - 1) - failed);
								}
								int eC = 4;
								for (int i = 4; i <= datatypeSheet.getLastRowNum(); i++) {
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
							summary.setFailed(failed);
							if(key.equals("advReceiptList") || key.equals("advAdjustedList") || key.equals("b2cList") || key.equals("nilList") || key.equals("hsnSummaryList")) {
								if(key.equals("hsnSummaryList")) {
									summary.setInvsuccess(summary.getTotal());
									summary.setSuccess(summary.getTotal());
								}else {
									summary.setInvsuccess(summary.getSuccess());
								}
								summary.setTotalinvs(summary.getTotal());
								summary.setInvfailed(summary.getFailed());
							}else {
								summary.setInvsuccess(invsucess);
								summary.setTotalinvs(totalinvs-1);
								summary.setInvfailed(totalinvs-1 - invsucess);
							}
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
					int hsnMonth = 0;
					int hsnYear = 0;
					for (String key : beans.keySet()) {
						if (beans.get(key) instanceof List) {
							List<InvoiceParent> invoices = beans.get(key);
							if (isNotEmpty(invoices)) {
								for (InvoiceParent invoice : invoices) {
									String fp = cFp;
									if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
										Calendar cal = Calendar.getInstance();
										cal.setTime(invoice.getDateofinvoice());
										int iMonth = cal.get(Calendar.MONTH) + 1;
										int iYear = cal.get(Calendar.YEAR);
										if(iYear >= hsnYear) {
											hsnYear = iYear;
											if(iMonth >= hsnMonth) {
												hsnMonth = iMonth;
											}
										}
										fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
										fpMap.put(iMonth, iYear);
									}
									invoice.setFp(fp);
								}
							}
						} else {
							InvoiceParent invoice = (InvoiceParent) beans.get(key);
							if (isNotEmpty(invoice)) {
								String fp = cFp;
								if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
									Calendar cal = Calendar.getInstance();
									cal.setTime(invoice.getDateofinvoice());
									int iMonth = cal.get(Calendar.MONTH) + 1;
									int iYear = cal.get(Calendar.YEAR);
									/*
									 * if(iYear >= hsnYear) { hsnYear = iYear; if(iMonth >= hsnMonth) { hsnMonth =
									 * iMonth; } }
									 */
									fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
									fpMap.put(iMonth, iYear);
								}
								invoice.setFp(fp);
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
					if (isNotEmpty(returntype) && returntype.equals(GSTR2)) {
						returntype = PURCHASE_REGISTER;
					}
					hsnSummary.setClientid(clientid);
					hsnSummary.setUserid(id);
					String hsnstrMonth = hsnMonth < 10 ? "0" + hsnMonth : hsnMonth + "";
					String hsncFp = hsnstrMonth + hsnYear;
					hsnSummary.setReturnPeriod(hsncFp);
					hsnSummary.setReturnType(returntype);
					hsnSummary.setImporttype("Tally");
					HSNDetails hsndetails = hsnSummaryRepository.findByClientidAndReturnPeriodAndReturnType(clientid,hsncFp,returntype);
					if(isNotEmpty(hsndetails)) {
						hsndetails.setClientid(clientid);
						hsndetails.setUserid(id);
						hsndetails.setReturnPeriod(hsncFp);
						hsndetails.setReturnType(returntype);
						hsndetails.setImporttype("Tally");
						hsndetails.setHsnData(hsnSummary.getHsnData());
					}else {
						hsndetails = hsnSummary;
					}
					hsnSummaryRepository.save(hsndetails);
					response.setMonth(month);
					response.setYear(year);
					response.setSummaryList(summaryList);
					if (isNotEmpty(summaryList)) {
						int failedCount = 0;
						for (ImportSummary summary : summaryList) {
							if (summary.getInvfailed() > 0) {
								failedCount += summary.getInvfailed();
							}
						}
						if (isNotEmpty(convFile) && failedCount > 0) {
							HttpSession session = request.getSession(false);
							if (isNotEmpty(session)) {
								session.setAttribute("error.xls", convFile);
								response.setShowLink(true);
							}
						}else {
							try {
								convFile.delete();
							} catch (Exception e) {
							}
						}
					}
					
					newimportService.updateExcelData(beans, list, returntype, id, fullname, clientid, TALLY_TEMPLATE,otherconfig);
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
		logger.debug(CLASSNAME + method + END);
		return response;
	}

	@RequestMapping(value = "/geterrorxls", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource getErrorFile(HttpServletResponse response, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (isNotEmpty(session) && isNotEmpty(session.getAttribute("error.xls"))) {
			File file = (File) session.getAttribute("error.xls");
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
			return new FileSystemResource(file);
		}
		return new FileSystemResource(new File("error.xls"));
	}
	
	private Map<String, List<InvoiceParent>> getExcelSheetMap() {
		Map<String, List<InvoiceParent>> sheetMap = Maps.newHashMap();
		List<InvoiceParent> creditList = Lists.newArrayList();
		List<InvoiceParent> cdnraList = Lists.newArrayList();
		List<InvoiceParent> exportList = Lists.newArrayList();
		List<InvoiceParent> expaList = Lists.newArrayList();
		List<InvoiceParent> advReceiptList = Lists.newArrayList();
		List<InvoiceParent> ataList = Lists.newArrayList();
		
		List<InvoiceParent> b2bList = Lists.newArrayList();
		List<InvoiceParent> b2baList = Lists.newArrayList();
		
		List<InvoiceParent> b2cList = Lists.newArrayList();
		List<InvoiceParent> b2csaList = Lists.newArrayList();
		List<InvoiceParent> b2clList = Lists.newArrayList();
		List<InvoiceParent> b2claList = Lists.newArrayList();
		List<InvoiceParent> cdnurList = Lists.newArrayList();
		List<InvoiceParent> cdnuraList = Lists.newArrayList();
		List<InvoiceParent> nilList = Lists.newArrayList();
		List<InvoiceParent> advAdjustedList = Lists.newArrayList();
		List<InvoiceParent> txpaList = Lists.newArrayList();
		List<InvoiceParent> b2buList = Lists.newArrayList();
		List<InvoiceParent> impgList = Lists.newArrayList();
		List<InvoiceParent> impsList = Lists.newArrayList();
		List<InvoiceParent> itrvslList = Lists.newArrayList();
		List<InvoiceParent> invoiceList = Lists.newArrayList();
		List<InvoiceParent> hsnSummaryList = Lists.newArrayList();
		List<InvoiceParent> docSummaryList = Lists.newArrayList();
		sheetMap.put("creditList", creditList);
		sheetMap.put("cdnraList", cdnraList);
		sheetMap.put("exportList", exportList);
		sheetMap.put("expaList", expaList);
		sheetMap.put("advReceiptList", advReceiptList);
		sheetMap.put("ataList", ataList);
		sheetMap.put("b2bList", b2bList);
		sheetMap.put("b2baList", b2baList);
		sheetMap.put("b2cList", b2cList);
		sheetMap.put("b2csaList", b2csaList);
		sheetMap.put("b2clList", b2clList);
		sheetMap.put("b2claList", b2claList);
		sheetMap.put("cdnurList", cdnurList);
		sheetMap.put("cdnuraList", cdnuraList);
		sheetMap.put("nilList", nilList);
		sheetMap.put("advAdjustedList", advAdjustedList);
		sheetMap.put("txpaList", txpaList);
		sheetMap.put("b2buList", b2buList);
		sheetMap.put("impgList", impgList);
		sheetMap.put("impsList", impsList);
		sheetMap.put("itrvslList", itrvslList);
		sheetMap.put("invoiceList", invoiceList);
		sheetMap.put("hsnSummaryList", hsnSummaryList);
		sheetMap.put("docSummaryList", docSummaryList);
		return sheetMap;
	}

	private ImportResponse importMapperDetailed(String id, String clientid, String templateName, MultipartFile file,
			String returntype, int month, int year, List<String> list, String fullname, TemplateMapper templateMapper,
			TemplateMapperDoc templateMapperDoc, boolean hsnqtyrate,String branch, String vertical, OtherConfigurations otherconfig, String hsnFp, HttpServletRequest request) {
		final String method = "importMapperDetailed::";
		Client client = clientService.findById(clientid);
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		int skipRows = 1;
		if (isNotEmpty(templateMapper.getSkipRows())) {
			skipRows = Integer.parseInt(templateMapper.getSkipRows());
		}
		Map<String, String> mapperConfig = Maps.newHashMap();
		if (NullUtil.isNotEmpty(templateMapper.getMapperConfig())) {
			for (String key : templateMapper.getMapperConfig().keySet()) {

				if (NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key))) {
					String page = (String) templateMapper.getMapperConfig().get(key).get("page");
					mapperConfig.put(key, page);
				}
			}
		}
		if (!file.isEmpty()) {
			try {
				DateConverter converter = new DateConverter();
				String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd" };
				converter.setPatterns(patterns);
				ConvertUtils.register(converter, Date.class);
				ReaderConfig.getInstance().setSkipErrors(true);
				String xmlPath1 = templateMapperDoc.getXmlDoc();
				InputStream in = IOUtils.toInputStream(xmlPath1, "UTF-8");
				XLSReader xlsReader = ReaderBuilder.buildFromXML(in);
				Map<String, List<InvoiceParent>> sheetMap = getExcelSheetMap();
				Map<String, List<InvoiceParent>> beans = Maps.newHashMap();
				beans.put("creditList", sheetMap.get("creditList"));
				beans.put("exportList", sheetMap.get("exportList"));
				beans.put("advReceiptList", sheetMap.get("advReceiptList"));
				beans.put("invoiceList", sheetMap.get("invoiceList"));
				beans.put("advAdjustedList", sheetMap.get("advAdjustedList"));
				beans.put("cdnurList", sheetMap.get("cdnurList"));
				beans.put("nilList", sheetMap.get("nilList"));
				beans.put("b2buList", sheetMap.get("b2buList"));
				beans.put("impgList", sheetMap.get("impgList"));
				beans.put("impsList", sheetMap.get("impsList"));
				beans.put("itrvslList", sheetMap.get("itrvslList"));
				
				beans.put("hsnSummaryList", sheetMap.get("hsnSummaryList"));
				beans.put("docSummaryList", sheetMap.get("docSummaryList"));
				
				XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
				HSNDetails hsnSummary = new HSNDetails();
				GSTR1DocumentIssue documentIssue = new GSTR1DocumentIssue();
				if (status.isStatusOK()) {
					String filename = file.getOriginalFilename();
					Workbook workbook = WorkbookFactory.create(file.getInputStream());
					Workbook workbookError = new HSSFWorkbook();
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
					Map<String, String> statesMap = newimportService.getStateMap();
					for (String key : beans.keySet()) {
						if (isNotEmpty(sheetMap.get(key)) && sheetMap.get(key).size() > 0) {
							ImportSummary summary = new ImportSummary();
							String dis = "amount";
							if (key.equals("invoiceList")) {
								summary.setName(mapperConfig.get("Invoices"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Invoices")) {
									dis = templateMapper.getDiscountConfig().get("Invoices");
								}
							} else if (key.equals("creditList")) {
								summary.setName(mapperConfig.get("Credit_Debit_Notes"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Credit_Debit_Notes")) {
									dis = templateMapper.getDiscountConfig().get("Credit_Debit_Notes");
								}
							} else if (key.equals("exportList")) {
								summary.setName(mapperConfig.get("Exports"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Exports")) {
									dis = templateMapper.getDiscountConfig().get("Exports");
								}
							} else if (key.equals("advReceiptList")) {
								summary.setName(mapperConfig.get("AdvanceReceipt"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("AdvanceReceipt")) {
									dis = templateMapper.getDiscountConfig().get("AdvanceReceipt");
								}
							} else if (key.equals("advAdjustedList")) {
								summary.setName(mapperConfig.get("AdvanceAdjusted"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("AdvanceAdjusted")) {
									dis = templateMapper.getDiscountConfig().get("AdvanceAdjusted");
								}
							} else if (key.equals("cdnurList")) {
								summary.setName(mapperConfig.get("Credit_Debit_Note_Unregistered"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Credit_Debit_Note_Unregistered")) {
									dis = templateMapper.getDiscountConfig().get("Credit_Debit_Note_Unregistered");
								}
							} else if (key.equals("nilList")) {
								summary.setName(mapperConfig.get("Nil_Exempted_Non-GST"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Nil_Exempted_Non-GST")) {
									dis = templateMapper.getDiscountConfig().get("Nil_Exempted_Non-GST");
								}
							} else if (key.equals("b2buList")) {
								summary.setName(mapperConfig.get("B2B_Unregistered"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("B2B_Unregistered")) {
									dis = templateMapper.getDiscountConfig().get("B2B_Unregistered");
								}
							} else if (key.equals("impgList")) {
								summary.setName(mapperConfig.get("Import_Goods"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Import_Goods")) {
									dis = templateMapper.getDiscountConfig().get("Import_Goods");
								}
							} else if (key.equals("impsList")) {
								summary.setName(mapperConfig.get("Import_Services"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Import_Services")) {
									dis = templateMapper.getDiscountConfig().get("Import_Services");
								}
							} else if (key.equals("itrvslList")) {
								summary.setName(mapperConfig.get("ITC_Reversal"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("ITC_Reversal")) {
									dis = templateMapper.getDiscountConfig().get("ITC_Reversal");
								}
							}else if (key.equals("hsnSummaryList")) {
								summary.setName(mapperConfig.get("hsn"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("hsn")) {
									dis = templateMapper.getDiscountConfig().get("hsn");
								}
							} else if (key.equals("docSummaryList")) {
								summary.setName(mapperConfig.get("docs"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("docs")) {
									dis = templateMapper.getDiscountConfig().get("docs");
								}
							} else {
								summary.setName(key);
							}
							Sheet datatypeSheet = workbook.getSheet(summary.getName());
							Sheet datatypeSheetError = workbookError.getSheet(summary.getName());
							CellStyle style = workbook.createCellStyle();
						    Font font = workbook.createFont();
				            font.setColor(IndexedColors.RED.getIndex());
				            style.setFont(font);
						    Cell cell = datatypeSheet.getRow(skipRows-1).createCell(datatypeSheet.getRow(skipRows-1).getLastCellNum());
						    cell.setCellValue("Errors");
						    cell.setCellStyle(style);
						    int lastcolumn = datatypeSheet.getRow(skipRows-1).getLastCellNum()-1;
						    for(int i=0;i<skipRows;i++) {
						    	if(filename.endsWith(".xlsx")  || filename.endsWith(".XLSX")) {
									WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, i, i);
								}else {
									WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, i, i);
								}
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
								int index = skipRows;
								if (skipRows == 1) {
									summary.setTotal(beans.get(key).size());
								} else {
									summary.setTotal(beans.get(key).size() - 1);
								}
								//List<Row> errorRows = Lists.newArrayList();
								List<Integer> errorIndxes = Lists.newArrayList();
								
								
								if("hsnSummaryList".equalsIgnoreCase(key)) {
									List<HSNData>  hsnData= Lists.newArrayList();
									int i=1;
									for (InvoiceParent invoice : invoices) {
										if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0))) {
											if(isNotEmpty(invoice.getItems().get(0))) {
												Item item = invoice.getItems().get(0); 
												item = bulkImportTaskService.changeInvoiceAmounts(item);
												List<Item> itms = Lists.newArrayList();
												itms.add(item);
												invoice.setItems(itms);
											}
											if(isNotEmpty(invoice.getItems().get(0).getHsn()) && !"HSN".equalsIgnoreCase(invoice.getItems().get(0).getHsn())) {
											
												HSNData hsndetails = new HSNData();
												hsndetails.setNum(i);
												if(isNotEmpty(invoice.getItems().get(0).getHsn())) {
													hsndetails.setHsnSc(invoice.getItems().get(0).getHsn());
												}
												if(isNotEmpty(invoice.getItems().get(0).getDesc())) {
													String description = invoice.getItems().get(0).getDesc();
													if (description.length() > 30) {
														description = description.substring(0, 27) + "..";
													}
													hsndetails.setDesc(description);
												}
												if(isNotEmpty(invoice.getItems().get(0).getUqc())) {
													String uqc = invoice.getItems().get(0).getUqc();
													if(uqc.contains("-")) {
														String uqcdetails[] = uqc.split("-");
														hsndetails.setUqc(uqcdetails[0]);
													}else {
														hsndetails.setUqc(uqc);
													}
												}
												if(isNotEmpty(invoice.getItems().get(0).getRate())) {
													hsndetails.setRt(invoice.getItems().get(0).getRate());
												}
												if(isNotEmpty(invoice.getItems().get(0).getQuantity())) {
													hsndetails.setQty(invoice.getItems().get(0).getQuantity());
												}else {
													hsndetails.setQty(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getTotal())) {
													hsndetails.setVal(invoice.getItems().get(0).getTotal());
												}else {
													hsndetails.setVal(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
													hsndetails.setTxval(invoice.getItems().get(0).getTaxablevalue());
												}else {
													hsndetails.setTxval(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
													hsndetails.setIamt(invoice.getItems().get(0).getIgstamount());
												}else {
													hsndetails.setIamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getSgstamount())) {
													hsndetails.setSamt(invoice.getItems().get(0).getSgstamount());
												}else {
													hsndetails.setSamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
													hsndetails.setCamt(invoice.getItems().get(0).getCgstamount());
												}else {
													hsndetails.setCamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getCessamount())) {
													hsndetails.setCsamt(invoice.getItems().get(0).getCessamount());
												}else {
													hsndetails.setCsamt(0d);
												}
												hsnData.add(hsndetails);
												i++;
											}
										}
									}
									hsnSummary.setHsnData(hsnData);
								}else if("docSummaryList".equalsIgnoreCase(key)){
									List<GSTR1DocIssueDetails> docDet = Lists.newArrayList();
									Map<String,List<GSTR1DocDetails>> docdetails = Maps.newHashMap();
									List<String> dockeys = Lists.newArrayList();
									for (InvoiceParent invoice : invoices) {
										if(isNotEmpty(invoice.getDocuploads()) && isNotEmpty(invoice.getDocuploads().getDocDet()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0))  && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocnumstring()) && !"Nature of Document".equalsIgnoreCase(invoice.getDocuploads().getDocDet().get(0).getDocnumstring())) {
											if(isEmpty(docdetails)) {
												List<GSTR1DocDetails> dcdet = Lists.newArrayList();
												if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0))) {
													dockeys.add(invoice.getDocuploads().getDocDet().get(0).getDocnumstring());
													GSTR1DocDetails docdetail = new GSTR1DocDetails();
													docdetail.setNum(1);
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
														String invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
														if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
															invnumfrom = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom().trim()).toPlainString();
														}else {
															invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
														}
														docdetail.setFrom(invnumfrom);
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
														String invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
														if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
															invnumto = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo().trim()).toPlainString();
														}else {
															invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
														}
														docdetail.setTo(invnumto);
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum())) {
														docdetail.setTotnum(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum());
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
														docdetail.setCancel(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
														docdetail.setNetIssue(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum() - invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
													}
													dcdet.add(docdetail);
												}
												docdetails.put(invoice.getDocuploads().getDocDet().get(0).getDocnumstring(), dcdet);
											}else {
												if(isNotEmpty(docdetails.get(invoice.getDocuploads().getDocDet().get(0).getDocnumstring()))) {
													List<GSTR1DocDetails> dcdet = docdetails.get(invoice.getDocuploads().getDocDet().get(0).getDocnumstring());
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0))) {
														GSTR1DocDetails docdetail = new GSTR1DocDetails();
														docdetail.setNum(dcdet.size()+1);
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
															String invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
																invnumfrom = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom().trim()).toPlainString();
															}else {
																invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															}
															docdetail.setFrom(invnumfrom);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
															String invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
																invnumto = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo().trim()).toPlainString();
															}else {
																invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															}
															docdetail.setTo(invnumto);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum())) {
															docdetail.setTotnum(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setCancel(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setNetIssue(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum() - invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														dcdet.add(docdetail);
													}
													docdetails.put(invoice.getDocuploads().getDocDet().get(0).getDocnumstring(), dcdet);
												}else {
													dockeys.add(invoice.getDocuploads().getDocDet().get(0).getDocnumstring());
													List<GSTR1DocDetails> dcdet = Lists.newArrayList();
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0))) {
														GSTR1DocDetails docdetail = new GSTR1DocDetails();
														docdetail.setNum(1);
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
															String invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
																invnumfrom = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom().trim()).toPlainString();
															}else {
																invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															}
															docdetail.setFrom(invnumfrom);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
															String invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
																invnumto = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo().trim()).toPlainString();
															}else {
																invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															}
															docdetail.setTo(invnumto);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum())) {
															docdetail.setTotnum(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setCancel(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setNetIssue(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum() - invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														dcdet.add(docdetail);
													}
													docdetails.put(invoice.getDocuploads().getDocDet().get(0).getDocnumstring(), dcdet);
												}
											}
										}
									}
									
									for(String dockey : dockeys) {
										List<GSTR1DocDetails> doclist = docdetails.get(dockey);
										GSTR1DocIssueDetails doc = new GSTR1DocIssueDetails();
										if("Invoices for outward supply".equalsIgnoreCase(dockey)) {
											doc.setDocNum(1);
											doc.setDocs(doclist);
										}else if("Invoices for inward supply from unregistered person".equalsIgnoreCase(dockey)) {
											doc.setDocNum(2);
											doc.setDocs(doclist);
										}else if("Revised Invoice".equalsIgnoreCase(dockey)) {
											doc.setDocNum(3);
											doc.setDocs(doclist);
										}else if("Revised Invoice".equalsIgnoreCase(dockey)) {
											doc.setDocNum(3);
											doc.setDocs(doclist);
										}else if("Debit Note".equalsIgnoreCase(dockey)) {
											doc.setDocNum(4);
											doc.setDocs(doclist);
										}else if("Credit Note".equalsIgnoreCase(dockey)) {
											doc.setDocNum(5);
											doc.setDocs(doclist);
										}else if("Receipt Voucher".equalsIgnoreCase(dockey)) {
											doc.setDocNum(6);
											doc.setDocs(doclist);
										}else if("Payment Voucher".equalsIgnoreCase(dockey)) {
											doc.setDocNum(7);
											doc.setDocs(doclist);
										}else if("Refund Voucher".equalsIgnoreCase(dockey)) {
											doc.setDocNum(8);
											doc.setDocs(doclist);
										}else if("Delivery Challan for job work".equalsIgnoreCase(dockey)) {
											doc.setDocNum(9);
											doc.setDocs(doclist);
										}else if("Delivery Challan for supply on approval".equalsIgnoreCase(dockey)) {
											doc.setDocNum(10);
											doc.setDocs(doclist);
										}else if("Delivery Challan in case of liquid gas".equalsIgnoreCase(dockey)) {
											doc.setDocNum(11);
											doc.setDocs(doclist);
										}else if("Delivery Challan in cases other than by way of supply (excluding at S no. 9 to 11)".equalsIgnoreCase(dockey)) {
											doc.setDocNum(12);
											doc.setDocs(doclist);
										}
										docDet.add(doc);
									}
									if(isNotEmpty(docDet)) {
										documentIssue.setDocDet(docDet);
									}
								}else {
								
								for (InvoiceParent invoice : invoices) {
									if(((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)))) {
										if(isEmpty(invoice.getStatename())) {
											if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
												invoice.setStatename(client.getStatename());
											}
										}
									}
									if(index != skipRows || skipRows == 1) {
										int vindex = index;
										if (skipRows == 1) {
											vindex = index;
										}else {
											vindex = index-1;
										}
										String invnum = "";
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
											
											if(isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getInum())) {
												invnum = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getInum().trim()).toPlainString();
											}else {
												invnum = invoice.getB2b().get(0).getInv().get(0).getInum();
											}
										}
										String prinvnum = invnum;
										if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
											if(isNotEmpty(invoice.getStrDate())) {
												prinvnum += invoice.getStrDate();
											}
										}
										if(isNotEmpty(tinvoicesmap)) {
											InvoiceParent exstngInv = tinvoicesmap.get(prinvnum);
											if(isEmpty(exstngInv)) {
												totalinvs++;
												tinvoicesmap.put(prinvnum, invoice);
											}
										}else {
											totalinvs++;
											tinvoicesmap.put(prinvnum, invoice);
										}
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(INVOICENO_REGEX, invnum.trim())) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Invoice Number Format");
														gstincell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
										}else if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())
											&& !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
											if(vindex != skipRows - 1) {
												Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
												gstincell.setCellValue("Invalid GSTIN Number");
												gstincell.setCellStyle(style);
												errorIndxes.add(vindex);
											}
										}
									} else {
										if (hsnqtyrate) {
											if (returntype.equals(MasterGSTConstants.GSTR1)	&& (key.equals("creditList") || key.equals("cdnurList")	|| key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || (key.equals("creditList") && (isEmpty(((GSTR1) invoice).getCdnr())
													|| isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())	|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty()))) || (key.equals("cdnurList")
													&& (isEmpty(((GSTR1) invoice).getCdnur()) || isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
													|| (key.equals("exportList") && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
													|| isEmpty(invoice.getStrDate())
													|| isEmpty(invoice.getItems())
													|| (isNotEmpty(invoice.getItems().get(0).getIgstrate())	&& invoice.getItems().get(0).getIgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCgstrate())	&& invoice.getItems().get(0).getCgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getSgstrate())	&& invoice.getItems().get(0).getSgstrate() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCessrate())	&& invoice.getItems().get(0).getCessrate() < 0))) {

												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (returntype.equals(MasterGSTConstants.GSTR4)
													&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) 
													|| (key.equals("creditList") && (isEmpty(((GSTR4) invoice).getCdnr()) || isEmpty(((GSTR4) invoice).getB2b().get(0).getCtin()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtNum()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList")	&& (isEmpty(((GSTR4) invoice).getCdnur()) || isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtNum()) || isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b())	|| isEmpty(invoice.getB2b().get(0).getCtin())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp())	|| isEmpty(invoice.getExp().get(0).getExpTyp())))
													|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
															|| (isNotEmpty(invoice.getItems().get(0).getIgstrate())	&& invoice.getItems().get(0).getIgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstrate())	&& invoice.getItems().get(0).getCgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstrate())	&& invoice.getItems().get(0).getSgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCessrate())	&& invoice.getItems().get(0).getCessrate() < 0))) {

												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("nilList") && (isEmpty(invoice.getStatename())
													|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| isEmpty(invoice.getItems().get(0).getType())
													|| isEmpty(invoice.getItems().get(0).getTotal()))) {

												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}

											} else if (key.equals("itrvslList")
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getB2b().get(0).getCtin())
															|| isEmpty(invoice.getItems())
															|| isEmpty(invoice.getItems().get(0).getItcRevtype())
															|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
															|| (isNotEmpty(invoice.getItems().get(0).getIgstamount()) && invoice.getItems().get(0).getIgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstamount()) && invoice.getItems().get(0).getCgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstamount()) && invoice.getItems().get(0).getSgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getIsdcessamount()) && invoice.getItems().get(0).getIsdcessamount() < 0)
															|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("impgList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("impgList") && (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Invoice Number Format");
														gstincell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("impsList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("advAdjustedList") && (isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("itrvslList")
													&& ((isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
															|| (isNotEmpty(invoice.getItems()) && ((isNotEmpty(invoice.getItems().get(0).getIgstrate())	&& invoice.getItems().get(0).getIgstrate() < 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) && invoice.getItems().get(0).getCgstrate() < 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getSgstrate())	&& invoice.getItems().get(0).getSgstrate() < 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getCessrate())	&& invoice.getItems().get(0).getCessrate() < 0)))))
													|| ((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) && key.equals("invoiceList") && (isEmpty(invoice.getB2b().get(0).getCtin()) || isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())))) {

												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
													String errorVal = "";

													if(((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)))) {
														if(key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getCtin())) {
															errorVal += "Please Enter GSTIN Number,";
														}
														if(key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
															errorVal += "Please Enter Invoice Number,";
														}
														
													}
													if(isEmpty(invoice.getStatename())) {
														errorVal += "Please Enter Statename,";
													}
													if(isEmpty(invoice.getStrDate())) {
														errorVal += "Please Enter Invoice Date,";
													}

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
														errorVal += "Please Enter valid Cess Rate";
													}
													Cell errors = datatypeSheet.getRow(vindex).createCell(lastcolumn);
													errors.setCellValue(errorVal);
													errors.setCellStyle(style);
													errorIndxes.add(vindex);
															}
											}

											} else {
												
												invoice = newimportService.importInvoicesHsnQtyRateMandatory(invoice,returntype,clientid,branch,vertical,month,year,patterns, statesMap);
												if(key.equals("invoiceList") || key.equals("exportList") || key.equals("impsList") || key.equals("impgList")) {
													if(isNotEmpty(invoice.getItems().get(0).getDiscount()) && invoice.getItems().get(0).getDiscount() > 0) {
														double discount = invoice.getItems().get(0).getDiscount();
														if(isEmpty(invoice.getItems().get(0).getRateperitem())) {
															if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																if("percentage".equalsIgnoreCase(dis)) {
																	invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																	invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																}else {
																	invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																}
															}
														}else {
															if(isNotEmpty(invoice.getItems().get(0).getQuantity()) && invoice.getItems().get(0).getQuantity() > 0) {
																if(invoice.getItems().get(0).getRateperitem() > 0) {
																	if("percentage".equalsIgnoreCase(dis)) {
																		invoice.getItems().get(0).setDiscount((invoice.getItems().get(0).getRateperitem()*invoice.getItems().get(0).getQuantity())*(discount/100));
																	}
																}else {
																	if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																		double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																		if("percentage".equalsIgnoreCase(dis)) {
																			invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																			invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																		}else {
																			invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																		}
																	}

																}
															}else {
																if(invoice.getItems().get(0).getRateperitem() > 0) {
																	invoice.getItems().get(0).setQuantity(1d);
																	if("percentage".equalsIgnoreCase(dis)) {
																		invoice.getItems().get(0).setDiscount((invoice.getItems().get(0).getRateperitem()*invoice.getItems().get(0).getQuantity())*(discount/100));
																	}
																}else {
																	if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																		double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																		if("percentage".equalsIgnoreCase(dis)) {
																			invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																			invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																		}else {
																			invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																		}
																	}

																}
															}
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
													if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
														if(isNotEmpty(invoice.getStrDate())) {
															mapkey = mapkey+invoice.getStrDate();
														}else {
															mapkey = mapkey+fp.trim();
														}
													}else {
														mapkey = mapkey+fp.trim();
													}
													//mapkey = mapkey+fp.trim();
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
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())|| (key.equals("creditList")
																	&& (isEmpty(((GSTR1) invoice).getCdnr())
																			|| isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())
																			|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt())
																			|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList")	&& (isEmpty(((GSTR1) invoice).getCdnur())
															|| isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| isEmpty(invoice.getItems().get(0).getHsn())
															|| isEmpty(invoice.getItems().get(0).getQuantity())
															|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) && invoice.getItems().get(0).getIgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) && invoice.getItems().get(0).getCgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) && invoice.getItems().get(0).getSgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) && invoice.getItems().get(0).getCessrate() < 0))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (returntype.equals(MasterGSTConstants.GSTR4)
													&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| (key.equals("creditList") && (isEmpty(((GSTR4) invoice).getCdnr())
																			|| isEmpty(((GSTR4) invoice).getB2b().get(0).getCtin())
																			|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt())
																			|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtNum())
																			|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList")	&& (isEmpty(((GSTR4) invoice).getCdnur())
																			|| isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtNum())
																			|| isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b())|| isEmpty(invoice.getB2b().get(0).getCtin())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp())|| isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| isEmpty(invoice.getItems().get(0).getHsn())
															|| isEmpty(invoice.getItems().get(0).getQuantity())
															|| (invoice.getItems().get(0).getQuantity() <= 0)
															|| (isNotEmpty(invoice.getItems().get(0).getRateperitem())&& invoice.getItems().get(0).getRateperitem() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getIgstrate())&& invoice.getItems().get(0).getIgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstrate())&& invoice.getItems().get(0).getCgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstrate())&& invoice.getItems().get(0).getSgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCessrate())&& invoice.getItems().get(0).getCessrate() < 0))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("nilList") && (isEmpty(invoice.getStatename())
													|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
													|| isEmpty(invoice.getItems().get(0).getHsn())
													|| isEmpty(invoice.getItems().get(0).getUqc())
													|| isEmpty(invoice.getItems().get(0).getQuantity())
													|| isEmpty(invoice.getItems().get(0).getRateperitem())
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| isEmpty(invoice.getItems().get(0).getType())
													|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("itrvslList")
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getB2b().get(0).getCtin())
															|| isEmpty(invoice.getItems())
															|| isEmpty(invoice.getItems().get(0).getItcRevtype())
															|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
															|| (isNotEmpty(invoice.getItems().get(0).getIgstamount())&& invoice.getItems().get(0).getIgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstamount())&& invoice.getItems().get(0).getCgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstamount())&& invoice.getItems().get(0).getSgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getIsdcessamount())&& invoice.getItems().get(0).getIsdcessamount() < 0)
															|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("impgList") && (isEmpty(invoice.getStatename())|| isEmpty(invoice.getStrDate()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("impgList") && (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Invoice Number Format");
														gstincell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("advAdjustedList") && (isEmpty(invoice.getStrDate())|| invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList")
													&& !key.equals("itrvslList")
													&& ((isEmpty(invoice.getStatename())
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| (isNotEmpty(invoice.getItems()) && (isEmpty(invoice.getItems().get(0).getHsn())
																	|| isEmpty(invoice.getItems().get(0).getQuantity())
																	|| (isNotEmpty(invoice.getItems().get(0).getIgstrate())&& invoice.getItems().get(0).getIgstrate() < 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getCgstrate())&& invoice.getItems().get(0).getCgstrate() < 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getSgstrate())&& invoice.getItems().get(0).getSgstrate() < 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getCessrate())&& invoice.getItems().get(0).getCessrate() < 0)))))
													|| ((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2))
															&& key.equals("invoiceList")&& (isEmpty(invoice.getB2b().get(0).getCtin()) || isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
													String errorVal = "";
													if(((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)))) {
														if(key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getCtin())) {
															errorVal += "Please Enter GSTIN Number,";
														}
														if(key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
															errorVal += "Please Enter Invoice Number,";
														}
														
													}
													if(isEmpty(invoice.getStatename())) {
														errorVal += "Please Enter Statename,";
													}
													if(isEmpty(invoice.getStrDate())) {
														errorVal += "Please Enter Invoice Date,";
													}
													if(isEmpty(invoice.getItems().get(0).getHsn())) {
														errorVal += "Please Enter HSN,";
													}

													if(isEmpty(invoice.getItems().get(0).getQuantity())) {
														errorVal += "Please Enter Quantity,";
													}
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
														errorVal += "Please Enter valid Cess Rate";
													}
													Cell errors = datatypeSheet.getRow(vindex).createCell(lastcolumn);
													errors.setCellValue(errorVal);
													errors.setCellStyle(style);
													errorIndxes.add(vindex);
															}
											}
											} else {
												
												invoice = newimportService.importInvoicesHsnQtyRateMandatory(invoice,returntype,clientid,branch,vertical,month,year,patterns, statesMap);
												if(key.equals("invoiceList") || key.equals("exportList") || key.equals("impsList") || key.equals("impgList")) {
													if(isNotEmpty(invoice.getItems().get(0).getDiscount()) && invoice.getItems().get(0).getDiscount() > 0) {
														double discount = invoice.getItems().get(0).getDiscount();
														if(isEmpty(invoice.getItems().get(0).getRateperitem())) {
															if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																if("percentage".equalsIgnoreCase(dis)) {
																	invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																	invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																}else {
																	invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																}
															}
														}else {
															if(isNotEmpty(invoice.getItems().get(0).getQuantity()) && invoice.getItems().get(0).getQuantity() > 0) {
																if(invoice.getItems().get(0).getRateperitem() > 0) {
																	if("percentage".equalsIgnoreCase(dis)) {
																		invoice.getItems().get(0).setDiscount((invoice.getItems().get(0).getRateperitem()*invoice.getItems().get(0).getQuantity())*(discount/100));
																	}
																}else {
																	if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																		double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																		if("percentage".equalsIgnoreCase(dis)) {
																			invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																			invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																		}else {
																			invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																		}
																	}

																}
															}else {
																if(invoice.getItems().get(0).getRateperitem() > 0) {
																	invoice.getItems().get(0).setQuantity(1d);
																	if("percentage".equalsIgnoreCase(dis)) {
																		invoice.getItems().get(0).setDiscount((invoice.getItems().get(0).getRateperitem()*invoice.getItems().get(0).getQuantity())*(discount/100));
																	}
																}else {
																	if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																		double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																		if("percentage".equalsIgnoreCase(dis)) {
																			invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																			invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																		}else {
																			invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																		}
																	}

																}
															}
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
													if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
														if(isNotEmpty(invoice.getStrDate())) {
															mapkey = mapkey+invoice.getStrDate();
														}else {
															mapkey = mapkey+fp.trim();
														}
													}else {
														mapkey = mapkey+fp.trim();
													}
													//mapkey = mapkey+fp.trim();
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
								}
									index++;
								}
								}
								if (skipRows == 1) {
									summary.setSuccess((beans.get(key).size()) - failed);
								} else {
									summary.setSuccess((beans.get(key).size() - 1) - failed);
								}
								int eC = skipRows;
								for (int i = skipRows; i <= datatypeSheet.getLastRowNum(); i++) {
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
							if("hsnSummaryList".equalsIgnoreCase(key) || "docSummaryList".equalsIgnoreCase(key)) {
								summary.setInvsuccess(summary.getTotal());
								summary.setSuccess(summary.getTotal());
								summary.setTotalinvs(summary.getTotal());
								summary.setInvfailed(summary.getFailed());
							}else {
								summary.setFailed(failed);
								summary.setTotalinvs(totalinvs);
								summary.setInvsuccess(invsucess);
								summary.setInvfailed(totalinvs - invsucess);
							}
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
							if (summary.getInvfailed() > 0) {
								failedCount += summary.getInvfailed();
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
					
					hsnSummary.setClientid(clientid);
					hsnSummary.setUserid(id);
					
					hsnSummary.setReturnType(returntype);
					hsnSummary.setReturnPeriod(hsnFp);
					hsnSummary.setImporttype("Detailed Template");
					HSNDetails hsndetails = hsnSummaryRepository.findByClientidAndReturnPeriodAndReturnType(clientid,hsnFp,returntype);
					if(isEmpty(hsndetails)) {
						hsndetails = new HSNDetails();
						hsndetails.setClientid(clientid);
						hsndetails.setUserid(id);
						hsndetails.setReturnPeriod(hsnFp);
						hsndetails.setReturnType(returntype);
						hsndetails.setImporttype("Detailed Template");
						hsndetails.setHsnData(hsnSummary.getHsnData());
					}else {
						if(isNotEmpty(hsnSummary) && isNotEmpty(hsnSummary.getHsnData()) && hsnSummary.getHsnData().size() > 0) {
							hsndetails.setHsnData(hsnSummary.getHsnData());
							hsndetails.setImporttype("Detailed Template");
						}
					}
					hsnSummaryRepository.save(hsndetails);
					documentIssue.setClientid(clientid);
					documentIssue.setReturnPeriod(hsnFp);
					GSTR1DocumentIssue docdetails = gstr1DocumentIssueRepository.findByClientidAndReturnPeriod(clientid, hsnFp);
					if(isNotEmpty(docdetails)) {
						docdetails.setClientid(clientid);
						docdetails.setReturnPeriod(hsnFp);
						docdetails.setDocDet(documentIssue.getDocDet());
					}else {
						docdetails = documentIssue;
					}
					gstr1DocumentIssueRepository.save(docdetails);
					String invReturnType = returntype;
					if (returntype.equals(MasterGSTConstants.GSTR2)) {
						invReturnType = MasterGSTConstants.PURCHASE_REGISTER;
					}
					newimportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, EXCEL_TEMPLATE,otherconfig);
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

	private ImportResponse importMapperSimplified(String id, String clientid, String templateName, MultipartFile file,
			String returntype, int month, int year, List<String> list, String fullname, TemplateMapper templateMapper,
			TemplateMapperDoc templateMapperDoc, boolean hsnqtyrate, String branch,String vertical, OtherConfigurations otherconfig, String hsnFp, HttpServletRequest request) {
		final String method = "importMapperSimplified::";
		Client client = clientService.findById(clientid);
		String stateName = client.getStatename();
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		int skipRows = 1;
		if (isNotEmpty(templateMapper.getSkipRows())) {
			skipRows = Integer.parseInt(templateMapper.getSkipRows());
		}
		Map<String, String> mapperConfig = Maps.newHashMap();

		if (NullUtil.isNotEmpty(templateMapper.getMapperConfig())) {
			for (String key : templateMapper.getMapperConfig().keySet()) {
				if (NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key))) {
					String page = (String) templateMapper.getMapperConfig().get(key).get("page");
					mapperConfig.put(key, page);
				}
			}
		}
		if (!file.isEmpty()) {
			try {
				DateConverter converter = new DateConverter();
				String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd" };
				converter.setPatterns(patterns);
				ConvertUtils.register(converter, Date.class);
				ReaderConfig.getInstance().setSkipErrors(true);
				String xmlPath1 = templateMapperDoc.getXmlDoc();
				InputStream in = IOUtils.toInputStream(xmlPath1, "UTF-8");
				XLSReader xlsReader = ReaderBuilder.buildFromXML(in);
				Map<String, List<InvoiceParent>> sheetMap = getExcelSheetMap();
				Map<String, List<InvoiceParent>> beans = Maps.newHashMap();
				beans.put("creditList", sheetMap.get("creditList"));
				beans.put("exportList", sheetMap.get("exportList"));
				beans.put("advReceiptList", sheetMap.get("advReceiptList"));
				beans.put("invoiceList", sheetMap.get("invoiceList"));
				beans.put("advAdjustedList", sheetMap.get("advAdjustedList"));
				beans.put("cdnurList", sheetMap.get("cdnurList"));
				beans.put("nilList", sheetMap.get("nilList"));
				beans.put("b2buList", sheetMap.get("b2buList"));
				beans.put("impgList", sheetMap.get("impgList"));
				beans.put("impsList", sheetMap.get("impsList"));
				beans.put("itrvslList", sheetMap.get("itrvslList"));
				
				beans.put("hsnSummaryList", sheetMap.get("hsnSummaryList"));
				beans.put("docSummaryList", sheetMap.get("docSummaryList"));
				
				XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
				HSNDetails hsnSummary = new HSNDetails();
				GSTR1DocumentIssue documentIssue = new GSTR1DocumentIssue();
				if (status.isStatusOK()) {
					Map<String, String> statesMap = newimportService.getStateMap();
					String filename = file.getOriginalFilename();
					Workbook workbook = WorkbookFactory.create(file.getInputStream());
					Workbook workbookError = new HSSFWorkbook();
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
							String dis = "amount";
							if (key.equals("invoiceList")) {
								summary.setName(mapperConfig.get("Invoices"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Invoices")) {
									dis = templateMapper.getDiscountConfig().get("Invoices");
								}
							} else if (key.equals("creditList")) {
								summary.setName(mapperConfig.get("Credit_Debit_Notes"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Credit_Debit_Notes")) {
									dis = templateMapper.getDiscountConfig().get("Credit_Debit_Notes");
								}
							} else if (key.equals("exportList")) {
								summary.setName(mapperConfig.get("Exports"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Exports")) {
									dis = templateMapper.getDiscountConfig().get("Exports");
								}
							} else if (key.equals("advReceiptList")) {
								summary.setName(mapperConfig.get("AdvanceReceipt"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("AdvanceReceipt")) {
									dis = templateMapper.getDiscountConfig().get("AdvanceReceipt");
								}
							} else if (key.equals("advAdjustedList")) {
								summary.setName(mapperConfig.get("AdvanceAdjusted"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("AdvanceAdjusted")) {
									dis = templateMapper.getDiscountConfig().get("AdvanceAdjusted");
								}
							} else if (key.equals("cdnurList")) {
								summary.setName(mapperConfig.get("Credit_Debit_Note_Unregistered"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Credit_Debit_Note_Unregistered")) {
									dis = templateMapper.getDiscountConfig().get("Credit_Debit_Note_Unregistered");
								}
							} else if (key.equals("nilList")) {
								summary.setName(mapperConfig.get("Nil_Exempted_Non-GST"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Nil_Exempted_Non-GST")) {
									dis = templateMapper.getDiscountConfig().get("Nil_Exempted_Non-GST");
								}
							} else if (key.equals("b2buList")) {
								summary.setName(mapperConfig.get("B2B_Unregistered"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("B2B_Unregistered")) {
									dis = templateMapper.getDiscountConfig().get("B2B_Unregistered");
								}
							} else if (key.equals("impgList")) {
								summary.setName(mapperConfig.get("Import_Goods"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Import_Goods")) {
									dis = templateMapper.getDiscountConfig().get("Import_Goods");
								}
							} else if (key.equals("impsList")) {
								summary.setName(mapperConfig.get("Import_Services"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Import_Services")) {
									dis = templateMapper.getDiscountConfig().get("Import_Services");
								}
							} else if (key.equals("itrvslList")) {
								summary.setName(mapperConfig.get("ITC_Reversal"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("ITC_Reversal")) {
									dis = templateMapper.getDiscountConfig().get("ITC_Reversal");
								}
							}else if (key.equals("hsnSummaryList")) {
								summary.setName(mapperConfig.get("hsn"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("hsn")) {
									dis = templateMapper.getDiscountConfig().get("hsn");
								}
							} else if (key.equals("docSummaryList")) {
								summary.setName(mapperConfig.get("docs"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("d")) {
									dis = templateMapper.getDiscountConfig().get("docs");
								}
							} else {
								summary.setName(key);
							}
							Sheet datatypeSheet = workbook.getSheet(summary.getName());
							Sheet datatypeSheetError = workbookError.getSheet(summary.getName());
							CellStyle style = workbook.createCellStyle();
						    Font font = workbook.createFont();
				            font.setColor(IndexedColors.RED.getIndex());
				            style.setFont(font);
						    Cell cell = datatypeSheet.getRow(skipRows-1).createCell(datatypeSheet.getRow(skipRows-1).getLastCellNum());
						    cell.setCellValue("Errors");
						    cell.setCellStyle(style);
							int lastcolumn = datatypeSheet.getRow(skipRows-1).getLastCellNum()-1;
							for(int i=0;i<skipRows;i++) {
								if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
									WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, i, i);
								}else {
									WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, i, i);
								}
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
								int index = skipRows;
								if (skipRows == 1) {
									summary.setTotal(beans.get(key).size());
								} else {
									summary.setTotal(beans.get(key).size() - 1);
								}
								//List<Row> errorRows = Lists.newArrayList();
								List<Integer> errorIndxes = Lists.newArrayList();
								
								if("hsnSummaryList".equalsIgnoreCase(key)) {
									List<HSNData>  hsnData= Lists.newArrayList();
									int i=1;
									for (InvoiceParent invoice : invoices) {
										if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0))) {
											if(isNotEmpty(invoice.getItems().get(0))) {
												Item item = invoice.getItems().get(0); 
												item = bulkImportTaskService.changeInvoiceAmounts(item);
												List<Item> itms = Lists.newArrayList();
												itms.add(item);
												invoice.setItems(itms);
											}
											if(isNotEmpty(invoice.getItems().get(0).getHsn()) && !"HSN".equalsIgnoreCase(invoice.getItems().get(0).getHsn())) {
											
												HSNData hsndetails = new HSNData();
												hsndetails.setNum(i);
												if(isNotEmpty(invoice.getItems().get(0).getHsn())) {
													hsndetails.setHsnSc(invoice.getItems().get(0).getHsn());
												}
												if(isNotEmpty(invoice.getItems().get(0).getDesc())) {
													String description = invoice.getItems().get(0).getDesc();
													if (description.length() > 30) {
														description = description.substring(0, 27) + "..";
													}
													hsndetails.setDesc(description);
												}
												if(isNotEmpty(invoice.getItems().get(0).getUqc())) {
													String uqc = invoice.getItems().get(0).getUqc();
													if(uqc.contains("-")) {
														String uqcdetails[] = uqc.split("-");
														hsndetails.setUqc(uqcdetails[0]);
													}else {
														hsndetails.setUqc(uqc);
													}
												}
												if(isNotEmpty(invoice.getItems().get(0).getRate())) {
													hsndetails.setRt(invoice.getItems().get(0).getRate());
												}
												if(isNotEmpty(invoice.getItems().get(0).getQuantity())) {
													hsndetails.setQty(invoice.getItems().get(0).getQuantity());
												}else {
													hsndetails.setQty(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getTotal())) {
													hsndetails.setVal(invoice.getItems().get(0).getTotal());
												}else {
													hsndetails.setVal(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
													hsndetails.setTxval(invoice.getItems().get(0).getTaxablevalue());
												}else {
													hsndetails.setTxval(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
													hsndetails.setIamt(invoice.getItems().get(0).getIgstamount());
												}else {
													hsndetails.setIamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getSgstamount())) {
													hsndetails.setSamt(invoice.getItems().get(0).getSgstamount());
												}else {
													hsndetails.setSamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
													hsndetails.setCamt(invoice.getItems().get(0).getCgstamount());
												}else {
													hsndetails.setCamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getCessamount())) {
													hsndetails.setCsamt(invoice.getItems().get(0).getCessamount());
												}else {
													hsndetails.setCsamt(0d);
												}
												hsnData.add(hsndetails);
												i++;
											}
										}
									}
									hsnSummary.setHsnData(hsnData);
								}else if("docSummaryList".equalsIgnoreCase(key)){
									List<GSTR1DocIssueDetails> docDet = Lists.newArrayList();
									Map<String,List<GSTR1DocDetails>> docdetails = Maps.newHashMap();
									List<String> dockeys = Lists.newArrayList();
									for (InvoiceParent invoice : invoices) {
										if(isNotEmpty(invoice.getDocuploads()) && isNotEmpty(invoice.getDocuploads().getDocDet()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0))  && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocnumstring()) && !"Nature of Document".equalsIgnoreCase(invoice.getDocuploads().getDocDet().get(0).getDocnumstring())) {
											if(isEmpty(docdetails)) {
												List<GSTR1DocDetails> dcdet = Lists.newArrayList();
												if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0))) {
													dockeys.add(invoice.getDocuploads().getDocDet().get(0).getDocnumstring());
													GSTR1DocDetails docdetail = new GSTR1DocDetails();
													docdetail.setNum(1);
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
														String invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
														if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
															invnumfrom = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom().trim()).toPlainString();
														}else {
															invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
														}
														docdetail.setFrom(invnumfrom);
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
														String invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
														if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
															invnumto = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo().trim()).toPlainString();
														}else {
															invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
														}
														docdetail.setTo(invnumto);
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum())) {
														docdetail.setTotnum(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum());
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
														docdetail.setCancel(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
														docdetail.setNetIssue(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum() - invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
													}
													dcdet.add(docdetail);
												}
												docdetails.put(invoice.getDocuploads().getDocDet().get(0).getDocnumstring(), dcdet);
											}else {
												if(isNotEmpty(docdetails.get(invoice.getDocuploads().getDocDet().get(0).getDocnumstring()))) {
													List<GSTR1DocDetails> dcdet = docdetails.get(invoice.getDocuploads().getDocDet().get(0).getDocnumstring());
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0))) {
														GSTR1DocDetails docdetail = new GSTR1DocDetails();
														docdetail.setNum(dcdet.size()+1);
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
															String invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
																invnumfrom = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom().trim()).toPlainString();
															}else {
																invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															}
															docdetail.setFrom(invnumfrom);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
															String invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
																invnumto = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo().trim()).toPlainString();
															}else {
																invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															}
															docdetail.setTo(invnumto);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum())) {
															docdetail.setTotnum(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setCancel(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setNetIssue(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum() - invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														dcdet.add(docdetail);
													}
													docdetails.put(invoice.getDocuploads().getDocDet().get(0).getDocnumstring(), dcdet);
												}else {
													dockeys.add(invoice.getDocuploads().getDocDet().get(0).getDocnumstring());
													List<GSTR1DocDetails> dcdet = Lists.newArrayList();
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0))) {
														GSTR1DocDetails docdetail = new GSTR1DocDetails();
														docdetail.setNum(1);
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
															String invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
																invnumfrom = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom().trim()).toPlainString();
															}else {
																invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															}
															docdetail.setFrom(invnumfrom);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
															String invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
																invnumto = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo().trim()).toPlainString();
															}else {
																invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															}
															docdetail.setTo(invnumto);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum())) {
															docdetail.setTotnum(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setCancel(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setNetIssue(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum() - invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														dcdet.add(docdetail);
													}
													docdetails.put(invoice.getDocuploads().getDocDet().get(0).getDocnumstring(), dcdet);
												}
											}
										}
									}
									
									for(String dockey : dockeys) {
										List<GSTR1DocDetails> doclist = docdetails.get(dockey);
										GSTR1DocIssueDetails doc = new GSTR1DocIssueDetails();
										if("Invoices for outward supply".equalsIgnoreCase(dockey)) {
											doc.setDocNum(1);
											doc.setDocs(doclist);
										}else if("Invoices for inward supply from unregistered person".equalsIgnoreCase(dockey)) {
											doc.setDocNum(2);
											doc.setDocs(doclist);
										}else if("Revised Invoice".equalsIgnoreCase(dockey)) {
											doc.setDocNum(3);
											doc.setDocs(doclist);
										}else if("Revised Invoice".equalsIgnoreCase(dockey)) {
											doc.setDocNum(3);
											doc.setDocs(doclist);
										}else if("Debit Note".equalsIgnoreCase(dockey)) {
											doc.setDocNum(4);
											doc.setDocs(doclist);
										}else if("Credit Note".equalsIgnoreCase(dockey)) {
											doc.setDocNum(5);
											doc.setDocs(doclist);
										}else if("Receipt Voucher".equalsIgnoreCase(dockey)) {
											doc.setDocNum(6);
											doc.setDocs(doclist);
										}else if("Payment Voucher".equalsIgnoreCase(dockey)) {
											doc.setDocNum(7);
											doc.setDocs(doclist);
										}else if("Refund Voucher".equalsIgnoreCase(dockey)) {
											doc.setDocNum(8);
											doc.setDocs(doclist);
										}else if("Delivery Challan for job work".equalsIgnoreCase(dockey)) {
											doc.setDocNum(9);
											doc.setDocs(doclist);
										}else if("Delivery Challan for supply on approval".equalsIgnoreCase(dockey)) {
											doc.setDocNum(10);
											doc.setDocs(doclist);
										}else if("Delivery Challan in case of liquid gas".equalsIgnoreCase(dockey)) {
											doc.setDocNum(11);
											doc.setDocs(doclist);
										}else if("Delivery Challan in cases other than by way of supply (excluding at S no. 9 to 11)".equalsIgnoreCase(dockey)) {
											doc.setDocNum(12);
											doc.setDocs(doclist);
										}
										docDet.add(doc);
									}
									if(isNotEmpty(docDet)) {
										documentIssue.setDocDet(docDet);
									}
								}else {
									for (InvoiceParent invoice : invoices) {
									if(((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)))) {
										if(isEmpty(invoice.getStatename())) {
											if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
												invoice.setStatename(client.getStatename());
											}
										}
									}
									if(index != skipRows || skipRows == 1) {
										int vindex = index;
										if (skipRows == 1) {
											vindex = index;
										}else {
											vindex = index-1;
										}
										String invnum = "";
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
											
											if(isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getInum())) {
												invnum = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getInum().trim()).toPlainString();
											}else {
												invnum = invoice.getB2b().get(0).getInv().get(0).getInum();
											}
										}
										String prinvnum = invnum;
										if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
											if(isNotEmpty(invoice.getStrDate())) {
												prinvnum += invoice.getStrDate();
											}
										}
										if(isNotEmpty(tinvoicesmap)) {
											InvoiceParent exstngInv = tinvoicesmap.get(prinvnum);
											if(isEmpty(exstngInv)) {
												totalinvs++;
												tinvoicesmap.put(prinvnum, invoice);
											}
										}else {
											totalinvs++;
											tinvoicesmap.put(prinvnum, invoice);
										}
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(INVOICENO_REGEX, invnum.trim())) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
												if(vindex != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
													gstincell.setCellValue("Invalid Invoice Number Format");
													gstincell.setCellStyle(style);
													errorIndxes.add(vindex);
												}
											}
										
									}else if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())
											&& !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
											if(vindex != skipRows - 1) {
												Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
												gstincell.setCellValue("Invalid GSTIN Number");
												gstincell.setCellStyle(style);
												errorIndxes.add(vindex);
											}
										}
									} else {
										if (hsnqtyrate) {
											if (returntype.equals(MasterGSTConstants.GSTR1)
													&& (key.equals("creditList") || key.equals("cdnurList")	|| key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| (key.equals("creditList")
																	&& (isEmpty(((GSTR1) invoice).getCdnr())
																			|| isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())
																			|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt())
																			|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList")	&& (isEmpty(((GSTR1) invoice).getCdnur())
															|| isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| (isNotEmpty(invoice.getItems().get(0).getRate()) && invoice.getItems().get(0).getRate() < 0))) {

												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (returntype.equals(MasterGSTConstants.GSTR4)
													&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| (key.equals("creditList")
																	&& (isEmpty(((GSTR4) invoice).getCdnr())
																			|| isEmpty(((GSTR4) invoice).getB2b().get(0).getCtin())
																			|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt())
																			|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtNum())
																			|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList")	&& (isEmpty(((GSTR4) invoice).getCdnur())
																			|| isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtNum())
																			|| isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp())|| isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| (isNotEmpty(invoice.getItems().get(0).getRate()) && invoice.getItems().get(0).getRate() < 0))) {

												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("nilList") && (isEmpty(invoice.getStatename())
													|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| isEmpty(invoice.getItems().get(0).getType())
													|| isEmpty(invoice.getItems().get(0).getTotal()))) {

												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("itrvslList")
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getB2b().get(0).getCtin())
															|| isEmpty(invoice.getItems())
															|| isEmpty(invoice.getItems().get(0).getItcRevtype())
															|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
															|| (isNotEmpty(invoice.getItems().get(0).getIgstamount())&& invoice.getItems().get(0).getIgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstamount())&& invoice.getItems().get(0).getCgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstamount())&& invoice.getItems().get(0).getSgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getIsdcessamount())&& invoice.getItems().get(0).getIsdcessamount() < 0)
															|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("impgList")&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())|| invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("impgList") && (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Invoice Number Format");
														gstincell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("impsList")&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())|| invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("advAdjustedList") && (isEmpty(invoice.getStrDate())|| invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList")&& !key.equals("itrvslList")
													&& ((((returntype.equals(MasterGSTConstants.GSTR1) || returntype.equals("SalesRegister")) && isEmpty(invoice.getStatename())) || isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| (isNotEmpty(invoice.getItems()) && ((isNotEmpty(invoice.getItems().get(0).getRate())&& invoice.getItems().get(0).getRate() < 0)))))
													|| ((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2))&& key.equals("invoiceList")&& (isEmpty(invoice.getB2b().get(0).getCtin()) || isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = "";
														if(((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)))) {
															if(key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getCtin())) {
																errorVal += "Please Enter GSTIN Number,";
															}
															if(key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
																errorVal += "Please Enter Invoice Number,";
															}
														}
														if(isEmpty(invoice.getStatename())) {
															errorVal += "Please Enter Statename,";
														}
														if(isEmpty(invoice.getStrDate())) {
															errorVal += "Please Enter Invoice Date,";
														}
														if((isNotEmpty(invoice.getItems().get(0).getRate())	&& invoice.getItems().get(0).getRate() < 0)) {
															errorVal += "Please Enter valid Rate,";
														}
														Cell errors = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errors.setCellValue(errorVal);
														errors.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
													} else {
												invoice = newimportService.importInvoicesSimplified(invoice, client,returntype,branch,vertical,month,year,patterns,statesMap);
												if(key.equals("invoiceList") || key.equals("exportList") || key.equals("impsList") || key.equals("impgList")) {
													if(isNotEmpty(invoice.getItems().get(0).getDiscount()) && invoice.getItems().get(0).getDiscount() > 0) {
														double discount = invoice.getItems().get(0).getDiscount();
														if(isEmpty(invoice.getItems().get(0).getRateperitem())) {
															if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																invoice.getItems().get(0).setQuantity(1d);
																if("percentage".equalsIgnoreCase(dis)) {
																	invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																	invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																}else {
																	invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																}
															}
														}else {
															if(isNotEmpty(invoice.getItems().get(0).getQuantity()) && invoice.getItems().get(0).getQuantity() > 0) {
																if(invoice.getItems().get(0).getRateperitem() > 0) {
																	if("percentage".equalsIgnoreCase(dis)) {
																		invoice.getItems().get(0).setDiscount((invoice.getItems().get(0).getRateperitem()*invoice.getItems().get(0).getQuantity())*(discount/100));
																	}
																}else {
																	if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																		double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																		if("percentage".equalsIgnoreCase(dis)) {
																			invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																			invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																		}else {
																			invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																		}
																	}
																}
															}else {
																if(invoice.getItems().get(0).getRateperitem() > 0) {
																	invoice.getItems().get(0).setQuantity(1d);
																	if("percentage".equalsIgnoreCase(dis)) {
																		invoice.getItems().get(0).setDiscount((invoice.getItems().get(0).getRateperitem()*invoice.getItems().get(0).getQuantity())*(discount/100));
																	}
																}else {
																	if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																		double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																		invoice.getItems().get(0).setQuantity(1d);
																		if("percentage".equalsIgnoreCase(dis)) {
																			invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																			invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																		}else {
																			invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																		}
																	}
																}
															}
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
													//mapkey = mapkey+fp.trim();
													if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
														if(isNotEmpty(invoice.getStrDate())) {
															mapkey = mapkey+invoice.getStrDate();
														}else {
															mapkey = mapkey+fp.trim();
														}
													}else {
														mapkey = mapkey+fp.trim();
													}
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
													&& (key.equals("creditList") || key.equals("cdnurList")	|| key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || (key.equals("creditList")
													&& (isEmpty(((GSTR1) invoice).getCdnr())
															|| isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())
															|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt())
															|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList") && (isEmpty(((GSTR1) invoice).getCdnur())
															|| isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp())
															|| isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| isEmpty(invoice.getItems().get(0).getHsn())
															|| isEmpty(invoice.getItems().get(0).getQuantity())
															|| (isNotEmpty(invoice.getItems().get(0).getRate())&& invoice.getItems().get(0).getRate() < 0))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell advAdjustError = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														advAdjustError.setCellValue(errorVal);
														advAdjustError.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (returntype.equals(MasterGSTConstants.GSTR4) && (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())|| (key.equals("creditList")
													&& (isEmpty(((GSTR4) invoice).getCdnr())
															|| isEmpty(((GSTR4) invoice).getB2b().get(0).getCtin())
															|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt())
															|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtNum())
															|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList") && (isEmpty(((GSTR4) invoice).getCdnur())
															|| isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtNum())
															|| isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b())|| isEmpty(invoice.getB2b().get(0).getCtin())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp())|| isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| isEmpty(invoice.getItems().get(0).getHsn())
															|| isEmpty(invoice.getItems().get(0).getQuantity())
															|| (invoice.getItems().get(0).getQuantity() <= 0)
															|| (isNotEmpty(invoice.getItems().get(0).getRateperitem())&& invoice.getItems().get(0).getRateperitem() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getRate())&& invoice.getItems().get(0).getRate() < 0))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("nilList") && (isEmpty(invoice.getStatename())
													|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
													|| isEmpty(invoice.getItems().get(0).getHsn())
													|| isEmpty(invoice.getItems().get(0).getUqc())
													|| isEmpty(invoice.getItems().get(0).getQuantity())
													|| isEmpty(invoice.getItems().get(0).getRateperitem())
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| isEmpty(invoice.getItems().get(0).getType())
													|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell advAdjustError = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														advAdjustError.setCellValue(errorVal);
														advAdjustError.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("itrvslList")
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getB2b().get(0).getCtin())
															|| isEmpty(invoice.getItems())
															|| isEmpty(invoice.getItems().get(0).getItcRevtype())
															|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
															|| (isNotEmpty(invoice.getItems().get(0).getIgstamount())&& invoice.getItems().get(0).getIgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstamount())&& invoice.getItems().get(0).getCgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstamount())&& invoice.getItems().get(0).getSgstamount() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getIsdcessamount())&& invoice.getItems().get(0).getIsdcessamount() < 0)
															|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell advAdjustError = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														advAdjustError.setCellValue(errorVal);
														advAdjustError.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("impgList") && (isEmpty(invoice.getStatename())|| isEmpty(invoice.getStrDate()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell advAdjustError = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														advAdjustError.setCellValue(errorVal);
														advAdjustError.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("impgList") && (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Invoice Number Format");
														gstincell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (key.equals("advAdjustedList") && (isEmpty(invoice.getStrDate())|| invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell advAdjustError = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														advAdjustError.setCellValue(errorVal);
														advAdjustError.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList")&& !key.equals("itrvslList")
													&& ((((returntype.equals(MasterGSTConstants.GSTR1)
															|| returntype.equals("SalesRegister"))
															&& isEmpty(invoice.getStatename()))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| (isNotEmpty(invoice.getItems()) 
															&& (isEmpty(invoice.getItems().get(0).getHsn())
															|| (isNotEmpty(invoice.getItems().get(0).getRate())&& invoice.getItems().get(0).getRate() < 0)))))
															|| ((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)|| returntype.equals(MasterGSTConstants.GSTR2))
															&& key.equals("invoiceList")&& (isEmpty(invoice.getB2b().get(0).getCtin()) || isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = "";
														if(((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)))) {
															if(key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getCtin())) {
																errorVal += "Please Enter GSTIN Number,";
															}
															if(key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
																errorVal += "Please Enter Invoice Number,";
															}
															
														}
														if(isEmpty(invoice.getStatename())) {
															errorVal += "Please Enter Statename,";
														}
														if(isEmpty(invoice.getStrDate())) {
															errorVal += "Please Enter Invoice Date,";
														}
														if(isEmpty(invoice.getItems().get(0).getHsn())) {
															errorVal += "Please Enter HSN,";
														}
														if(isEmpty(invoice.getItems().get(0).getQuantity())) {
															errorVal += "Please Enter Quantity,";
														}
														if((isNotEmpty(invoice.getItems().get(0).getRate())	&& invoice.getItems().get(0).getRate() < 0)) {
															errorVal += "Please Enter valid Rate";
														}
														Cell errors = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errors.setCellValue(errorVal);
														errors.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
													} else {
												invoice = newimportService.importInvoicesSimplified(invoice, client,returntype,branch,vertical,month,year,patterns,statesMap);
												if(key.equals("invoiceList") || key.equals("exportList") || key.equals("impsList") || key.equals("impgList")) {
													if(isNotEmpty(invoice.getItems().get(0).getDiscount()) && invoice.getItems().get(0).getDiscount() > 0) {
														double discount = invoice.getItems().get(0).getDiscount();
														if(isEmpty(invoice.getItems().get(0).getRateperitem())) {
															if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																invoice.getItems().get(0).setQuantity(1d);
																if("percentage".equalsIgnoreCase(dis)) {
																	invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																	invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																}else {
																	invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																}
															}
														}else {
															if(isNotEmpty(invoice.getItems().get(0).getQuantity()) && invoice.getItems().get(0).getQuantity() > 0) {
																if(invoice.getItems().get(0).getRateperitem() > 0) {
																	if("percentage".equalsIgnoreCase(dis)) {
																		invoice.getItems().get(0).setDiscount((invoice.getItems().get(0).getRateperitem()*invoice.getItems().get(0).getQuantity())*(discount/100));
																	}
																}else {
																	if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																		double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																		if("percentage".equalsIgnoreCase(dis)) {
																			invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																			invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																		}else {
																			invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																		}
																	}

																}
															}else {
																if(invoice.getItems().get(0).getRateperitem() > 0) {
																	invoice.getItems().get(0).setQuantity(1d);
																	if("percentage".equalsIgnoreCase(dis)) {
																		invoice.getItems().get(0).setDiscount((invoice.getItems().get(0).getRateperitem()*invoice.getItems().get(0).getQuantity())*(discount/100));
																	}
																}else {
																	if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																		double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																		invoice.getItems().get(0).setQuantity(1d);
																		if("percentage".equalsIgnoreCase(dis)) {
																			invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																			invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																		}else {
																			invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																		}
																	}

																}
															}
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
													//mapkey = mapkey+fp.trim();
													if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
														if(isNotEmpty(invoice.getStrDate())) {
															mapkey = mapkey+invoice.getStrDate();
														}else {
															mapkey = mapkey+fp.trim();
														}
													}else {
														mapkey = mapkey+fp.trim();
													}
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
								}
									index++;
								}
								}
								if (skipRows == 1) {
									summary.setSuccess((beans.get(key).size()) - failed);
								} else {
									summary.setSuccess((beans.get(key).size() - 1) - failed);
								}

								int eC = skipRows;
								for (int i = skipRows; i <= datatypeSheet.getLastRowNum(); i++) {
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
							if("hsnSummaryList".equalsIgnoreCase(key) || "docSummaryList".equalsIgnoreCase(key)) {
								summary.setInvsuccess(summary.getTotal());
								summary.setSuccess(summary.getTotal());
								summary.setTotalinvs(summary.getTotal());
								summary.setInvfailed(summary.getFailed());
							}else {
								summary.setFailed(failed);
								summary.setTotalinvs(totalinvs);
								summary.setInvsuccess(invsucess);
								summary.setInvfailed(totalinvs - invsucess);
							}
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
								if (isNotEmpty(invoice)){
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
					hsnSummary.setClientid(clientid);
					hsnSummary.setUserid(id);
					hsnSummary.setReturnPeriod(hsnFp);
					hsnSummary.setReturnType(returntype);
					hsnSummary.setImporttype("Simplified Template");
					HSNDetails hsndetails = hsnSummaryRepository.findByClientidAndReturnPeriodAndReturnType(clientid,hsnFp,returntype);
					if(isEmpty(hsndetails)) {
						hsndetails = new HSNDetails();
						hsndetails.setClientid(clientid);
						hsndetails.setUserid(id);
						hsndetails.setReturnPeriod(hsnFp);
						hsndetails.setReturnType(returntype);
						hsndetails.setImporttype("Simplified Template");
						hsndetails.setHsnData(hsnSummary.getHsnData());
					}else {
						if(isNotEmpty(hsnSummary) && isNotEmpty(hsnSummary.getHsnData()) && hsnSummary.getHsnData().size() > 0) {
							hsndetails.setImporttype("Simplified Template");
							hsndetails.setHsnData(hsnSummary.getHsnData());
						}
					}
					hsnSummaryRepository.save(hsndetails);
					
					documentIssue.setClientid(clientid);
					documentIssue.setReturnPeriod(hsnFp);
					GSTR1DocumentIssue docdetails = gstr1DocumentIssueRepository.findByClientidAndReturnPeriod(clientid, hsnFp);
					if(isNotEmpty(docdetails)) {
						docdetails.setClientid(clientid);
						docdetails.setReturnPeriod(hsnFp);
						docdetails.setDocDet(documentIssue.getDocDet());
					}else {
						docdetails = documentIssue;
					}
					gstr1DocumentIssueRepository.save(docdetails);
					response.setMonth(month);
					response.setYear(year);
					response.setSummaryList(summaryList);
					if (isNotEmpty(summaryList)) {
						int failedCount = 0;
						for (ImportSummary summary : summaryList) {
							if (summary.getInvfailed() > 0) {
								failedCount += summary.getInvfailed();
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
					if (returntype.equals(MasterGSTConstants.GSTR2)) {
						invReturnType = MasterGSTConstants.PURCHASE_REGISTER;
					}
					newimportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, EXCEL_TEMPLATE,otherconfig);
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
	
	@RequestMapping(value = "/uploadTallyInvoiceNewVersion/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}/{temptype}", method = RequestMethod.POST)
	public @ResponseBody ImportResponse uploadTallyInvoiceNewVersion(@PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
			@RequestParam("file") MultipartFile file, @RequestParam("list") List<String> list,
			@RequestParam(value = "branch", required = false) String branch,
			@RequestParam(value = "vertical", required = false) String vertical, @RequestParam(value = "tallyfinancialYear", required = false) String tallyfinancialYear, @RequestParam(value = "tallymonth", required = false) String tallymonth, @PathVariable("month") int month,
			@PathVariable("year") int year,@PathVariable("temptype") String temptype, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "uploadTallyInvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "returntype: {}", returntype);
		logger.debug(CLASSNAME + method + "branch: {}", branch);
		logger.debug(CLASSNAME + method + "vertical: {}", vertical);
		logger.debug(CLASSNAME + method + "financialYear: {}", tallyfinancialYear);
		logger.debug(CLASSNAME + method + "tallymonth: {}", tallymonth);
		int skipRows = 4;
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		if (!file.isEmpty()) {
			logger.debug(CLASSNAME + method + file.getOriginalFilename());
			try {
				String xmlPath = "";
				if(isNotEmpty(temptype) && temptype.equalsIgnoreCase("tallyprime")){
					xmlPath = "classpath:tally_excel_prime_config.xml";
				}else {
					xmlPath = "classpath:tally_excel_config_v17.xml";
				}
				if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
					xmlPath = "classpath:tally_purchase_excel_config.xml";
				}else {
					skipRows = 4;
				}
				Resource config = resourceLoader.getResource(xmlPath);
				DateConverter converter = new DateConverter();
				String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd" };
				converter.setPatterns(patterns);
				ConvertUtils.register(converter, Date.class);
				ReaderConfig.getInstance().setSkipErrors(true);
				XLSReader xlsReader = ReaderBuilder.buildFromXML(config.getInputStream());
				Map<String, List<InvoiceParent>> sheetMap = getExcelSheetMap();
				Map<String, List<InvoiceParent>> beans = Maps.newHashMap();
				beans.put("creditList", sheetMap.get("creditList"));
				beans.put("exportList", sheetMap.get("exportList"));
				beans.put("advReceiptList", sheetMap.get("advReceiptList"));
				beans.put("advAdjustedList", sheetMap.get("advAdjustedList"));
				beans.put("b2bList", sheetMap.get("b2bList"));
				beans.put("b2cList", sheetMap.get("b2cList"));
				beans.put("b2clList", sheetMap.get("b2clList"));
				beans.put("b2buList", sheetMap.get("b2buList"));
				beans.put("cdnurList", sheetMap.get("cdnurList"));
				beans.put("nilList", sheetMap.get("nilList"));
				beans.put("impgList", sheetMap.get("impgList"));
				beans.put("impsList", sheetMap.get("impsList"));
				beans.put("itrvslList", sheetMap.get("itrvslList"));
				beans.put("hsnSummaryList", sheetMap.get("hsnSummaryList"));
				XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
				HSNDetails hsnSummary = new HSNDetails();
				logger.debug(CLASSNAME + method + "Status: {}", status.isStatusOK());
				if (status.isStatusOK()) {
					Client client = clientService.findById(clientid);
					OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
					Map<String,StateConfig> states = newimportService.getStatesMap();
					logger.debug(CLASSNAME + method + "creditList: {}", beans.get("creditList"));
					logger.debug(CLASSNAME + method + "exportList: {}", beans.get("exportList"));
					logger.debug(CLASSNAME + method + "advReceiptList: {}", beans.get("advReceiptList"));
					logger.debug(CLASSNAME + method + "b2bList: {}", beans.get("b2bList"));
					logger.debug(CLASSNAME + method + "b2cList: {}", beans.get("b2cList"));
					logger.debug(CLASSNAME + method + "b2clList: {}", beans.get("b2clList"));
					Map<String, String> statesMap = newimportService.getStateMap();
					String filename = file.getOriginalFilename();
					Workbook workbook = WorkbookFactory.create(file.getInputStream());
					Workbook workbookError = new HSSFWorkbook();
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
								summary.setName("cdnr");
							} else if (key.equals("exportList")) {
								summary.setName("exp");
							} else if (key.equals("advReceiptList")) {
								summary.setName("at");
							} else if (key.equals("advAdjustedList")) {
								summary.setName("atadj");
							} else if (key.equals("cdnurList")) {
								summary.setName("cdnur");
							} else if (key.equals("nilList")) {
								summary.setName("exemp");
							} else if (key.equals("b2buList")) {
								summary.setName("b2bur");
							} else if (key.equals("impgList")) {
								summary.setName("impg");
							} else if (key.equals("impsList")) {
								summary.setName("imps");
							} else if (key.equals("itrvslList")) {
								summary.setName("itcr");
							} else if (key.equals("b2bList")) {
								summary.setName("b2b");
							} else if (key.equals("b2clList")) {
								summary.setName("b2cl");
							} else if (key.equals("b2cList")) {
								summary.setName("b2cs");
							} else if (key.equals("hsnSummaryList")) {
								summary.setName("hsn");
							} else {
								summary.setName(key);
							}
							Sheet datatypeSheet = workbook.getSheet(summary.getName());
							Sheet datatypeSheetError = workbookError.getSheet(summary.getName());
							CellStyle style = workbook.createCellStyle();
						    Font font = workbook.createFont();
				            font.setColor(IndexedColors.RED.getIndex());
				            style.setFont(font);
							Cell cell = datatypeSheet.getRow(skipRows-1).createCell(datatypeSheet.getRow(skipRows-1).getLastCellNum());
							cell.setCellValue("Errors");
							cell.setCellStyle(style);
							int lastcolumn = datatypeSheet.getRow(skipRows-1).getLastCellNum()-1;
							if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
								WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 0, 0);
								WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 1, 1);
								WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 2, 2);
								WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 3, 3);
							}else {
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 0, 0);
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 1, 1);
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 2, 2);
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 3, 3);
							}
							
							long failed = 0;
							List<InvoiceParent> invoices = beans.get(key);
							List<InvoiceParent> filteredList = Lists.newArrayList();
							
							String strMonth = month < 10 ? "0" + month : month + "";
							String cFp = strMonth + year;
							long invsucess = 0;
							Map<String,InvoiceParent> invoicesmap = new HashMap<String, InvoiceParent>();
							long totalinvs = 0;
							Map<String,InvoiceParent> tinvoicesmap = new HashMap<String, InvoiceParent>();
							
							if (isNotEmpty(invoices) && invoices.size() > 0) {
								int index = skipRows - 1;
								if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)
										|| returntype.equals(MasterGSTConstants.GSTR2)) {
									summary.setTotal(beans.get(key).size()-1);
								}else {
									summary.setTotal(beans.get(key).size()-1);
								}
								//List<Row> errorRows = Lists.newArrayList();
								List<Integer> errorIndxes = Lists.newArrayList();
								if("hsnSummaryList".equalsIgnoreCase(key)) {
									List<HSNData>  hsnData= Lists.newArrayList();
									int i=1;
									for (InvoiceParent invoice : invoices) {
										if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0))) {
											if(isNotEmpty(invoice.getItems().get(0))) {
												Item item = invoice.getItems().get(0); 
												item = bulkImportTaskService.changeInvoiceAmounts(item);
												List<Item> itms = Lists.newArrayList();
												itms.add(item);
												invoice.setItems(itms);
											}
											if(isNotEmpty(invoice.getItems().get(0).getHsn()) && !"HSN".equalsIgnoreCase(invoice.getItems().get(0).getHsn())) {
											
												HSNData hsndetails = new HSNData();
												hsndetails.setNum(i);
												if(isNotEmpty(invoice.getItems().get(0).getHsn())) {
													hsndetails.setHsnSc(invoice.getItems().get(0).getHsn());
												}
												if(isNotEmpty(invoice.getItems().get(0).getDesc())) {
													String description = invoice.getItems().get(0).getDesc();
													if (description.length() > 30) {
														description = description.substring(0, 27) + "..";
													}
													hsndetails.setDesc(description);
												}
												if(isNotEmpty(invoice.getItems().get(0).getUqc())) {
													String uqc = invoice.getItems().get(0).getUqc();
													if(uqc.contains("-")) {
														String uqcdetails[] = uqc.split("-");
														hsndetails.setUqc(uqcdetails[0]);
													}else {
														hsndetails.setUqc(uqc);
													}
												}
												if(isNotEmpty(invoice.getItems().get(0).getQuantity())) {
													hsndetails.setQty(invoice.getItems().get(0).getQuantity());
												}else {
													hsndetails.setQty(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getTotal())) {
													hsndetails.setVal(invoice.getItems().get(0).getTotal());
												}else {
													hsndetails.setVal(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getRate())) {
													hsndetails.setRt(invoice.getItems().get(0).getRate());
												}
												if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
													hsndetails.setTxval(invoice.getItems().get(0).getTaxablevalue());
												}else {
													hsndetails.setTxval(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
													hsndetails.setIamt(invoice.getItems().get(0).getIgstamount());
												}else {
													hsndetails.setIamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getSgstamount())) {
													hsndetails.setSamt(invoice.getItems().get(0).getSgstamount());
												}else {
													hsndetails.setSamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
													hsndetails.setCamt(invoice.getItems().get(0).getCgstamount());
												}else {
													hsndetails.setCamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getCessamount())) {
													hsndetails.setCsamt(invoice.getItems().get(0).getCessamount());
												}else {
													hsndetails.setCsamt(0d);
												}
												hsnData.add(hsndetails);
												i++;
											}
										}
									}
									hsnSummary.setHsnData(hsnData);
								}else {	
									for (InvoiceParent invoice : invoices) {
										String invnum = "";
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
											
											if(isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getInum())) {
												invnum = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getInum().trim()).toPlainString();
											}else {
												invnum = invoice.getB2b().get(0).getInv().get(0).getInum();
											}
										}
										if(!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("b2cList") && !key.equals("nilList")) {
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
										}
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(INVOICENO_REGEX, invnum.trim())) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													if(index != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Invoice Number Format");
														gstincell.setCellStyle(style);
													}
													errorIndxes.add(index);
												}
										}else if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().toUpperCase())) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid GSTIN Number");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if (returntype.equals(MasterGSTConstants.GSTR1)
												&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStrDate())
												|| (key.equals("creditList") && (isEmpty(((GSTR1) invoice).getCdnr()) || isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
												|| (key.equals("cdnurList") && (isEmpty(((GSTR1) invoice).getCdnur()) || isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
												|| (key.equals("exportList") && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
														|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = newimportService.tallyErrorMsg(key,invoice,returntype);
												Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
												impsError.setCellValue(errorVal);
												impsError.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
										else if (returntype.equals(MasterGSTConstants.GSTR4)
												&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStrDate())
												|| (key.equals("creditList") && (isEmpty(((GSTR4) invoice).getCdnr()) || isEmpty(((GSTR4) invoice).getB2b().get(0).getCtin()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtNum()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
												|| (key.equals("cdnurList") && (isEmpty(((GSTR4) invoice).getCdnur()) || isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtNum()) || isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtty())))
												|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())))
												|| (key.equals("exportList") && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
														|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												datatypeSheet.getRow(index).createCell(lastcolumn).setCellValue("Please Enter Valid Values");
												errorIndxes.add(index);
											}
										}else if(key.equals("b2cList") && (isEmpty(invoice.getStatename()) || "Place Of Supply".equalsIgnoreCase(invoice.getStatename())   )) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = newimportService.tallyErrorMsg(key,invoice,returntype);
												Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
												impsError.setCellValue(errorVal);
												impsError.setCellStyle(style);
												errorIndxes.add(index);
											}
										} else if (!key.equals("advAdjustedList") && !key.equals("advReceiptList") && !key.equals("nilList") && !key.equals("impgList") && !key.equals("itrvslList") && (!key.equals("b2cList") && !key.equals("creditList")
													&& (isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue()))))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = "";
												if(isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue()))) {
													errorVal += "Please Enter Values";
												}
												Cell error = datatypeSheet.getRow(index).createCell(lastcolumn);
												error.setCellValue(errorVal);
												error.setCellStyle(style);
												errorIndxes.add(index);
											}
										}else if (key.equals("impgList") && (isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue())))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = newimportService.tallyErrorMsg(key,invoice,returntype);
												Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
												impsError.setCellValue(errorVal);
												impsError.setCellStyle(style);
												errorIndxes.add(index);
											}
										}else if (key.equals("impgList") && (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim()))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Invalid Invoice Number Format");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}else if(key.equals("nilList") && (isEmpty(invoice.getNil().getInv().get(0).getSplyType()) || (isEmpty(invoice.getNil().getInv().get(0).getExptAmt())
												&& isEmpty(invoice.getNil().getInv().get(0).getNilAmt()) && isEmpty(invoice.getNil().getInv().get(0).getNgsupAmt())))) {
											boolean nilFlag = false;
											String errorMsg = null;
											if(isEmpty(invoice.getNil().getInv().get(0).getSplyType())){
												nilFlag =  true;
												errorMsg = "Please provide valid description";
											}else if(isEmpty(invoice.getNil().getInv().get(0).getExptAmt())
													&& isEmpty(invoice.getNil().getInv().get(0).getNilAmt()) && isEmpty(invoice.getNil().getInv().get(0).getNgsupAmt())){
												nilFlag =  true;
												errorMsg = "Please provide any one amount  (Exempted amount / Nil rated amount/ Non-GST supplies)";
											}
											if(nilFlag){
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													if(index != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
														gstincell.setCellValue(errorMsg);
														gstincell.setCellStyle(style);
													}
													errorIndxes.add(index);
												}
											}
										}else if((key.equals("advReceiptList") || key.equals("advAdjustedList")) && (isEmpty(invoice.getStatename()) ||  isEmpty(invoice.getItems().get(0).getAdvAdjustableAmount()))) {
											String errorMsg = "";
											failed++;
											if(isEmpty(invoice.getStatename())) {
												errorMsg ="place of supply could not empty";
											}
											if(isEmpty(invoice.getItems().get(0).getAdvAdjustableAmount())) {
												errorMsg +=" Please Enter Values";
											}
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue(errorMsg);
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else {
											invoice = newimportService.importInvoicesTally(invoice, client, returntype, branch, vertical,month,year,patterns,statesMap);												
											
											if(key.equals("b2cList")) {
												if(isNotEmpty(tallyfinancialYear) && isNotEmpty(tallymonth)) {
													Calendar cal = Calendar.getInstance();
													int b2ctmnth = Integer.parseInt(tallymonth);
													int b2cyear = Integer.parseInt(tallyfinancialYear);
													if(b2ctmnth >= 1 && b2ctmnth < 4) {
														b2cyear = b2cyear+1;
													}
													cal.set(b2cyear, b2ctmnth - 1, 1);
													invoice.setDateofinvoice(cal.getTime());
												}
											}else if(key.equals("nilList")) {
												if(isNotEmpty(tallyfinancialYear) && isNotEmpty(tallymonth)) {
													Calendar cal = Calendar.getInstance();
													int nilmnth = Integer.parseInt(tallymonth);
													int nilyear = Integer.parseInt(tallyfinancialYear);
													if(nilmnth >= 1 && nilmnth < 4) {
														nilyear = nilyear+1;
													}
													cal.set(nilyear, nilmnth - 1, 1);
													invoice.setDateofinvoice(cal.getTime());
												}
											}
											if(key.equalsIgnoreCase("creditList")) {
												if(isNotEmpty(invoice.getStatename())) {
													if (isNotEmpty(((GSTR1) invoice).getCdnr()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0)) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt())  && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0))) {
														String statename = invoice.getStatename();
														String pos = "";
														if (statename.contains("-")) {
															pos = statename.substring(0, statename.indexOf("-")).trim();
														}else {
															StateConfig state = states.get(statename.replaceAll("\\s", "").toLowerCase());
															if(isNotEmpty(state)) {
																pos = state.getTin().toString();
															}
														}
														((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setPos(pos);
													}
												}
											}
											if(key.equalsIgnoreCase("cdnurList")) {
												if(isNotEmpty(invoice.getStatename())) {
													if (isNotEmpty(invoice.getCdnur()) && isNotEmpty(invoice.getCdnur().get(0))) {
														if(isNotEmpty(invoice.getCdnur().get(0).getTyp())) {
															String typ = invoice.getCdnur().get(0).getTyp();
															if(typ.equalsIgnoreCase("B2CL")) {
																String statename = invoice.getStatename();
																String pos = "";
																if (statename.contains("-")) {
																	pos = statename.substring(0, statename.indexOf("-")).trim();
																}else {
																	StateConfig state = states.get(statename.replaceAll("\\s", "").toLowerCase());
																	if(isNotEmpty(state)) {
																		pos = state.getTin().toString();
																	}
																}
																invoice.getCdnur().get(0).setPos(pos);
															}
														}
													}
												}
											}
											filteredList.add(invoice);
											if(!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("b2cList") && !key.equals("nilList")) {
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
										index++;
									}
								}
								if (skipRows == 1) {
									summary.setSuccess((beans.get(key).size()) - failed);
								} else {
									if (failed != 0) {
										failed = failed - 1;
									}
									summary.setSuccess((beans.get(key).size() - 1) - failed);
								}
								int eC = 4;
								for (int i = 4; i <= datatypeSheet.getLastRowNum(); i++) {
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
							summary.setFailed(failed);
							if(key.equals("advReceiptList") || key.equals("advAdjustedList") || key.equals("b2cList") || key.equals("nilList") || key.equals("hsnSummaryList")) {
								if(key.equals("hsnSummaryList")) {
									summary.setInvsuccess(summary.getTotal());
									summary.setSuccess(summary.getTotal());
								}else {
									summary.setInvsuccess(summary.getSuccess());
								}
								summary.setTotalinvs(summary.getTotal());
								summary.setInvfailed(summary.getFailed());
							}else {
								summary.setInvsuccess(invsucess);
								summary.setTotalinvs(totalinvs-1);
								summary.setInvfailed(totalinvs-1 - invsucess);
							}
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
					int hsnMonth = 0;
					int hsnYear = 0;
					for (String key : beans.keySet()) {
						if (beans.get(key) instanceof List) {
							List<InvoiceParent> invoices = beans.get(key);
							if (isNotEmpty(invoices)) {
								for (InvoiceParent invoice : invoices) {
									String fp = cFp;
									if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
										Calendar cal = Calendar.getInstance();
										cal.setTime(invoice.getDateofinvoice());
										int iMonth = cal.get(Calendar.MONTH) + 1;
										int iYear = cal.get(Calendar.YEAR);
										if(iYear >= hsnYear) {
											hsnYear = iYear;
											if(iMonth >= hsnMonth) {
												hsnMonth = iMonth;
											}
										}
										fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
										fpMap.put(iMonth, iYear);
									}
									invoice.setFp(fp);
								}
							}
						} else {
							InvoiceParent invoice = (InvoiceParent) beans.get(key);
							if (isNotEmpty(invoice)) {
								String fp = cFp;
								if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
									Calendar cal = Calendar.getInstance();
									cal.setTime(invoice.getDateofinvoice());
									int iMonth = cal.get(Calendar.MONTH) + 1;
									int iYear = cal.get(Calendar.YEAR);
									/*
									 * if(iYear >= hsnYear) { hsnYear = iYear; if(iMonth >= hsnMonth) { hsnMonth =
									 * iMonth; } }
									 */
									fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
									fpMap.put(iMonth, iYear);
								}
								invoice.setFp(fp);
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
					if (isNotEmpty(returntype) && returntype.equals(GSTR2)) {
						returntype = PURCHASE_REGISTER;
					}
					hsnSummary.setClientid(clientid);
					hsnSummary.setUserid(id);
					String hsnstrMonth = hsnMonth < 10 ? "0" + hsnMonth : hsnMonth + "";
					String hsncFp = hsnstrMonth + hsnYear;
					hsnSummary.setReturnPeriod(hsncFp);
					hsnSummary.setReturnType(returntype);
					hsnSummary.setImporttype("Tally");
					HSNDetails hsndetails = hsnSummaryRepository.findByClientidAndReturnPeriodAndReturnType(clientid,hsncFp,returntype);
					if(isNotEmpty(hsndetails)) {
						hsndetails.setClientid(clientid);
						hsndetails.setUserid(id);
						hsndetails.setReturnPeriod(hsncFp);
						hsndetails.setReturnType(returntype);
						hsndetails.setImporttype("Tally");
						hsndetails.setHsnData(hsnSummary.getHsnData());
					}else {
						hsndetails = hsnSummary;
					}
					hsnSummaryRepository.save(hsndetails);
					response.setMonth(month);
					response.setYear(year);
					response.setSummaryList(summaryList);
					if (isNotEmpty(summaryList)) {
						int failedCount = 0;
						for (ImportSummary summary : summaryList) {
							if (summary.getInvfailed() > 0) {
								failedCount += summary.getInvfailed();
							}
						}
						if (isNotEmpty(convFile) && failedCount > 0) {
							HttpSession session = request.getSession(false);
							if (isNotEmpty(session)) {
								session.setAttribute("error.xls", convFile);
								response.setShowLink(true);
							}
						}else {
							try {
								convFile.delete();
							} catch (Exception e) {
							}
						}
					}
					
					newimportService.updateExcelData(beans, list, returntype, id, fullname, clientid, TALLY_TEMPLATE,otherconfig);
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
		logger.debug(CLASSNAME + method + END);
		return response;
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
	
	@RequestMapping(value = "/uploadGstr1OfflineUtility/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody ImportResponse uploadGstr1OfflineUtility(@PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
			@RequestParam("file") MultipartFile file, @RequestParam("list") List<String> list,
			@RequestParam(value = "branch", required = false) String branch,
			@RequestParam(value = "vertical", required = false) String vertical, @RequestParam(value = "tallyfinancialYear", required = false) String tallyfinancialYear, @RequestParam(value = "tallymonth", required = false) String tallymonth, @PathVariable("month") int month,
			@PathVariable("year") int year, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "uploadTallyInvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "returntype: {}", returntype);
		logger.debug(CLASSNAME + method + "branch: {}", branch);
		logger.debug(CLASSNAME + method + "vertical: {}", vertical);
		logger.debug(CLASSNAME + method + "financialYear: {}", tallyfinancialYear);
		logger.debug(CLASSNAME + method + "tallymonth: {}", tallymonth);
		int skipRows = 4;
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		if (!file.isEmpty()) {
			logger.debug(CLASSNAME + method + file.getOriginalFilename());
			try {
				String xmlPath = "";
					xmlPath = "classpath:gstr1_excel_offline_config.xml";
				if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
					xmlPath = "classpath:tally_purchase_excel_config.xml";
				}else {
					skipRows = 4;
				}
				Resource config = resourceLoader.getResource(xmlPath);
				DateConverter converter = new DateConverter();
				String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd" };
				converter.setPatterns(patterns);
				ConvertUtils.register(converter, Date.class);
				ReaderConfig.getInstance().setSkipErrors(true);
				XLSReader xlsReader = ReaderBuilder.buildFromXML(config.getInputStream());
				Map<String, List<InvoiceParent>> sheetMap = getExcelSheetMap();
				Map<String, List<InvoiceParent>> beans = Maps.newHashMap();
				beans.put("b2bList", sheetMap.get("b2bList"));
				beans.put("b2cList", sheetMap.get("b2cList"));
				beans.put("b2clList", sheetMap.get("b2clList"));
				beans.put("creditList", sheetMap.get("creditList"));
				beans.put("cdnurList", sheetMap.get("cdnurList"));
				beans.put("exportList", sheetMap.get("exportList"));
				beans.put("advReceiptList", sheetMap.get("advReceiptList"));
				beans.put("advAdjustedList", sheetMap.get("advAdjustedList"));
				
				beans.put("b2baList", sheetMap.get("b2baList"));
				beans.put("b2csaList", sheetMap.get("b2csaList"));
				beans.put("b2claList", sheetMap.get("b2claList"));
				beans.put("cdnraList", sheetMap.get("cdnraList"));
				beans.put("cdnuraList", sheetMap.get("cdnuraList"));
				beans.put("expaList", sheetMap.get("expaList"));
				beans.put("ataList", sheetMap.get("ataList"));
				beans.put("txpaList", sheetMap.get("txpaList"));
				
				beans.put("b2buList", sheetMap.get("b2buList"));
				beans.put("nilList", sheetMap.get("nilList"));
				beans.put("impgList", sheetMap.get("impgList"));
				beans.put("impsList", sheetMap.get("impsList"));
				beans.put("itrvslList", sheetMap.get("itrvslList"));
				beans.put("hsnSummaryList", sheetMap.get("hsnSummaryList"));
				beans.put("docSummaryList", sheetMap.get("docSummaryList"));
				XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
				HSNDetails hsnSummary = new HSNDetails();
				GSTR1DocumentIssue documentIssue = new GSTR1DocumentIssue();
				logger.debug(CLASSNAME + method + "Status: {}", status.isStatusOK());
				if (status.isStatusOK()) {
					Client client = clientService.findById(clientid);
					OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
					Map<String,StateConfig> states = newimportService.getStatesMap();
					logger.debug(CLASSNAME + method + "creditList: {}", beans.get("creditList"));
					logger.debug(CLASSNAME + method + "exportList: {}", beans.get("exportList"));
					logger.debug(CLASSNAME + method + "advReceiptList: {}", beans.get("advReceiptList"));
					logger.debug(CLASSNAME + method + "b2bList: {}", beans.get("b2bList"));
					logger.debug(CLASSNAME + method + "b2cList: {}", beans.get("b2cList"));
					logger.debug(CLASSNAME + method + "b2clList: {}", beans.get("b2clList"));
					Map<String, String> statesMap = newimportService.getStateMap();
					String filename = file.getOriginalFilename();
					Workbook workbook = WorkbookFactory.create(file.getInputStream());
					Workbook workbookError = null;
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
					String errorfilename = null;
					if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
						workbookError = new XSSFWorkbook();
						errorfilename = "MasterGST_Import_Errors_" + dateFormat.format(new Date()) + ".xlsx";
					}else {
						workbookError = new HSSFWorkbook();
						errorfilename = "MasterGST_Import_Errors_" + dateFormat.format(new Date()) + ".xls";
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
								summary.setName("cdnr");
							} else if (key.equals("cdnraList")) {
								summary.setName("cdnra");
							}else if (key.equals("exportList")) {
								summary.setName("exp");
							}else if (key.equals("expaList")) {
								summary.setName("expa");
							} else if (key.equals("advReceiptList")) {
								summary.setName("at");
							}else if (key.equals("ataList")) {
								summary.setName("ata");
							} else if (key.equals("advAdjustedList")) {
								summary.setName("atadj");
							}else if (key.equals("txpaList")) {
								summary.setName("atadja");
							} else if (key.equals("cdnurList")) {
								summary.setName("cdnur");
							}else if (key.equals("cdnuraList")) {
								summary.setName("cdnura");
							} else if (key.equals("nilList")) {
								summary.setName("exemp");
							} else if (key.equals("b2buList")) {
								summary.setName("b2bur");
							} else if (key.equals("impgList")) {
								summary.setName("impg");
							} else if (key.equals("impsList")) {
								summary.setName("imps");
							} else if (key.equals("itrvslList")) {
								summary.setName("itcr");
							} else if (key.equals("b2bList")) {
								summary.setName("b2b");
							}else if (key.equals("b2baList")) {
								summary.setName("b2ba");
							} else if (key.equals("b2clList")) {
								summary.setName("b2cl");
							}else if (key.equals("b2claList")) {
								summary.setName("b2cla");
							}else if (key.equals("b2cList")) {
								summary.setName("b2cs");
							} else if (key.equals("b2csaList")) {
								summary.setName("b2csa");
							} else if (key.equals("hsnSummaryList")) {
								summary.setName("hsn");
							} else if (key.equals("docSummaryList")) {
								summary.setName("docs");
							} else {
								summary.setName(key);
							}
							Sheet datatypeSheet = workbook.getSheet(summary.getName());
							Sheet datatypeSheetError = workbookError.getSheet(summary.getName());
							CellStyle style = workbook.createCellStyle();
						    Font font = workbook.createFont();
				            font.setColor(IndexedColors.RED.getIndex());
				            style.setFont(font);
							Cell cell = datatypeSheet.getRow(skipRows-1).createCell(datatypeSheet.getRow(skipRows-1).getLastCellNum());
							cell.setCellValue("Errors");
							cell.setCellStyle(style);
							int lastcolumn = datatypeSheet.getRow(skipRows-1).getLastCellNum()-1;
							if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
								WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 0, 0);
								WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 1, 1);
								WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 2, 2);
								WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 3, 3);
							}else {
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 0, 0);
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 1, 1);
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 2, 2);
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 3, 3);
							}
							
							long failed = 0;
							List<InvoiceParent> invoices = beans.get(key);
							List<InvoiceParent> filteredList = Lists.newArrayList();
							
							String strMonth = month < 10 ? "0" + month : month + "";
							String cFp = strMonth + year;
							long invsucess = 0;
							Map<String,InvoiceParent> invoicesmap = new HashMap<String, InvoiceParent>();
							long totalinvs = 0;
							Map<String,InvoiceParent> tinvoicesmap = new HashMap<String, InvoiceParent>();
							
							if (isNotEmpty(invoices) && invoices.size() > 0) {
								int index = skipRows - 1;
								if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)
										|| returntype.equals(MasterGSTConstants.GSTR2)) {
									summary.setTotal(beans.get(key).size()-1);
								}else {
									summary.setTotal(beans.get(key).size()-1);
								}
								//List<Row> errorRows = Lists.newArrayList();
								List<Integer> errorIndxes = Lists.newArrayList();
								if("hsnSummaryList".equalsIgnoreCase(key)) {
									List<HSNData>  hsnData= Lists.newArrayList();
									int i=1;
									for (InvoiceParent invoice : invoices) {
										if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0))) {
											if(isNotEmpty(invoice.getItems().get(0))) {
												Item item = invoice.getItems().get(0); 
												item = bulkImportTaskService.changeInvoiceAmounts(item);
												List<Item> itms = Lists.newArrayList();
												itms.add(item);
												invoice.setItems(itms);
											}
											if(isNotEmpty(invoice.getItems().get(0).getHsn()) && !"HSN".equalsIgnoreCase(invoice.getItems().get(0).getHsn())) {
											
												HSNData hsndetails = new HSNData();
												hsndetails.setNum(i);
												if(isNotEmpty(invoice.getItems().get(0).getHsn())) {
													hsndetails.setHsnSc(invoice.getItems().get(0).getHsn());
												}
												if(isNotEmpty(invoice.getItems().get(0).getDesc())) {
													String description = invoice.getItems().get(0).getDesc();
													if (description.length() > 30) {
														description = description.substring(0, 27) + "..";
													}
													hsndetails.setDesc(description);
												}
												if(isNotEmpty(invoice.getItems().get(0).getUqc())) {
													String uqc = invoice.getItems().get(0).getUqc();
													if(uqc.contains("-")) {
														String uqcdetails[] = uqc.split("-");
														hsndetails.setUqc(uqcdetails[0]);
													}else {
														hsndetails.setUqc(uqc);
													}
												}
												if(isNotEmpty(invoice.getItems().get(0).getRate())) {
													hsndetails.setRt(invoice.getItems().get(0).getRate());
												}
												if(isNotEmpty(invoice.getItems().get(0).getQuantity())) {
													hsndetails.setQty(invoice.getItems().get(0).getQuantity());
												}else {
													hsndetails.setQty(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getTotal())) {
													hsndetails.setVal(invoice.getItems().get(0).getTotal());
												}else {
													hsndetails.setVal(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
													hsndetails.setTxval(invoice.getItems().get(0).getTaxablevalue());
												}else {
													hsndetails.setTxval(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
													hsndetails.setIamt(invoice.getItems().get(0).getIgstamount());
												}else {
													hsndetails.setIamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getSgstamount())) {
													hsndetails.setSamt(invoice.getItems().get(0).getSgstamount());
												}else {
													hsndetails.setSamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
													hsndetails.setCamt(invoice.getItems().get(0).getCgstamount());
												}else {
													hsndetails.setCamt(0d);
												}
												if(isNotEmpty(invoice.getItems().get(0).getCessamount())) {
													hsndetails.setCsamt(invoice.getItems().get(0).getCessamount());
												}else {
													hsndetails.setCsamt(0d);
												}
												hsnData.add(hsndetails);
												i++;
											}
										}
									}
									hsnSummary.setHsnData(hsnData);
								}else if("docSummaryList".equalsIgnoreCase(key)){
									List<GSTR1DocIssueDetails> docDet = Lists.newArrayList();
									Map<String,List<GSTR1DocDetails>> docdetails = Maps.newHashMap();
									List<String> dockeys = Lists.newArrayList();
									for (InvoiceParent invoice : invoices) {
										if(isNotEmpty(invoice.getDocuploads()) && isNotEmpty(invoice.getDocuploads().getDocDet()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0))  && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocnumstring()) && !"Nature of Document".equalsIgnoreCase(invoice.getDocuploads().getDocDet().get(0).getDocnumstring())) {
											if(isEmpty(docdetails)) {
												List<GSTR1DocDetails> dcdet = Lists.newArrayList();
												if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0))) {
													dockeys.add(invoice.getDocuploads().getDocDet().get(0).getDocnumstring());
													GSTR1DocDetails docdetail = new GSTR1DocDetails();
													docdetail.setNum(1);
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
														String invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
														if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
															invnumfrom = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom().trim()).toPlainString();
														}else {
															invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
														}
														docdetail.setFrom(invnumfrom);
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
														String invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
														if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
															invnumto = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo().trim()).toPlainString();
														}else {
															invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
														}
														docdetail.setTo(invnumto);
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum())) {
														docdetail.setTotnum(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum());
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
														docdetail.setCancel(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
													}
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
														docdetail.setNetIssue(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum() - invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
													}
													dcdet.add(docdetail);
												}
												docdetails.put(invoice.getDocuploads().getDocDet().get(0).getDocnumstring(), dcdet);
											}else {
												if(isNotEmpty(docdetails.get(invoice.getDocuploads().getDocDet().get(0).getDocnumstring()))) {
													List<GSTR1DocDetails> dcdet = docdetails.get(invoice.getDocuploads().getDocDet().get(0).getDocnumstring());
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0))) {
														GSTR1DocDetails docdetail = new GSTR1DocDetails();
														docdetail.setNum(dcdet.size()+1);
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
															String invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
																invnumfrom = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom().trim()).toPlainString();
															}else {
																invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															}
															docdetail.setFrom(invnumfrom);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
															String invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
																invnumto = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo().trim()).toPlainString();
															}else {
																invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															}
															docdetail.setTo(invnumto);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum())) {
															docdetail.setTotnum(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setCancel(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setNetIssue(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum() - invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														dcdet.add(docdetail);
													}
													docdetails.put(invoice.getDocuploads().getDocDet().get(0).getDocnumstring(), dcdet);
												}else {
													dockeys.add(invoice.getDocuploads().getDocDet().get(0).getDocnumstring());
													List<GSTR1DocDetails> dcdet = Lists.newArrayList();
													if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0))) {
														GSTR1DocDetails docdetail = new GSTR1DocDetails();
														docdetail.setNum(1);
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
															String invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom())) {
																invnumfrom = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom().trim()).toPlainString();
															}else {
																invnumfrom = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getFrom();
															}
															docdetail.setFrom(invnumfrom);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
															String invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															if(isScientificNotation(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo())) {
																invnumto = new BigDecimal(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo().trim()).toPlainString();
															}else {
																invnumto = invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTo();
															}
															docdetail.setTo(invnumto);
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum())) {
															docdetail.setTotnum(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setCancel(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														if(isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum()) && isNotEmpty(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel())) {
															docdetail.setNetIssue(invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getTotnum() - invoice.getDocuploads().getDocDet().get(0).getDocs().get(0).getCancel());
														}
														dcdet.add(docdetail);
													}
													docdetails.put(invoice.getDocuploads().getDocDet().get(0).getDocnumstring(), dcdet);
												}
											}
										}
									}
									
									for(String dockey : dockeys) {
										List<GSTR1DocDetails> doclist = docdetails.get(dockey);
										GSTR1DocIssueDetails doc = new GSTR1DocIssueDetails();
										if("Invoices for outward supply".equalsIgnoreCase(dockey)) {
											doc.setDocNum(1);
											doc.setDocs(doclist);
										}else if("Invoices for inward supply from unregistered person".equalsIgnoreCase(dockey)) {
											doc.setDocNum(2);
											doc.setDocs(doclist);
										}else if("Revised Invoice".equalsIgnoreCase(dockey)) {
											doc.setDocNum(3);
											doc.setDocs(doclist);
										}else if("Revised Invoice".equalsIgnoreCase(dockey)) {
											doc.setDocNum(3);
											doc.setDocs(doclist);
										}else if("Debit Note".equalsIgnoreCase(dockey)) {
											doc.setDocNum(4);
											doc.setDocs(doclist);
										}else if("Credit Note".equalsIgnoreCase(dockey)) {
											doc.setDocNum(5);
											doc.setDocs(doclist);
										}else if("Receipt Voucher".equalsIgnoreCase(dockey)) {
											doc.setDocNum(6);
											doc.setDocs(doclist);
										}else if("Payment Voucher".equalsIgnoreCase(dockey)) {
											doc.setDocNum(7);
											doc.setDocs(doclist);
										}else if("Refund Voucher".equalsIgnoreCase(dockey)) {
											doc.setDocNum(8);
											doc.setDocs(doclist);
										}else if("Delivery Challan for job work".equalsIgnoreCase(dockey)) {
											doc.setDocNum(9);
											doc.setDocs(doclist);
										}else if("Delivery Challan for supply on approval".equalsIgnoreCase(dockey)) {
											doc.setDocNum(10);
											doc.setDocs(doclist);
										}else if("Delivery Challan in case of liquid gas".equalsIgnoreCase(dockey)) {
											doc.setDocNum(11);
											doc.setDocs(doclist);
										}else if("Delivery Challan in cases other than by way of supply (excluding at S no. 9 to 11)".equalsIgnoreCase(dockey)) {
											doc.setDocNum(12);
											doc.setDocs(doclist);
										}
										docDet.add(doc);
									}
									if(isNotEmpty(docDet)) {
										documentIssue.setDocDet(docDet);
									}
								}else {	
									for (InvoiceParent invoice : invoices) {
										
										String invnum = "", oinum = "";
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0))
												&& isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
											if(isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getInum())) {
												invnum = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getInum().trim()).toPlainString();
											}else {
												invnum = invoice.getB2b().get(0).getInv().get(0).getInum();
											}
										}
										if(key.equals("b2baList") || key.equals("b2claList") || key.equals("expaList") || key.equals("cdnraList") || key.equals("cdnuraList")) {
											if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0))
													&& isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getOinum())) {
												if(isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getOinum())) {
													oinum = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getOinum().trim()).toPlainString();
												}else {
													oinum = invoice.getB2b().get(0).getInv().get(0).getOinum().trim();
												}
											}
											
											/*if (isNotEmpty(invoice.getStrOdate())) {
												SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
												invoice.setStrOdate(sdf.format(newimportService.invdate(invoice.getStrOdate(),patterns,year,month)));
											}
											if(key.equals("expaList")) {
												invoice.setStatename(MasterGSTConstants.OTHER_TERRITORY);
											}*/
										}
										if(!key.equals("advReceiptList") && !key.equals("ataList") && !key.equals("txpaList") && !key.equals("advAdjustedList") && !key.equals("b2cList") && !key.equals("b2cList") && !key.equals("nilList")) {
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
										}
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(INVOICENO_REGEX, invnum.trim())) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													if(index != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Invoice Number Format");
														gstincell.setCellStyle(style);
													}
													errorIndxes.add(index);
												}
										}else if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().toUpperCase())) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid GSTIN Number");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if (returntype.equals(MasterGSTConstants.GSTR1)
												&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("cdnuraList") || key.equals("exportList") || key.equals("cdnraList") || key.equals("expaList"))
												&& (isEmpty(invoice.getStrDate())
												|| (key.equals("creditList") && (isEmpty(((GSTR1) invoice).getCdnr()) || isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
												|| (key.equals("cdnraList") && (isEmpty(((GSTR1) invoice).getCdnr()) || isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
												|| (key.equals("cdnuraList") && (isEmpty(((GSTR1) invoice).getCdnur()) || isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
												
												|| (key.equals("cdnurList") && (isEmpty(((GSTR1) invoice).getCdnur()) || isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
												|| (key.equals("exportList") && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
												|| (key.equals("expaList") && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
														|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													String errorVal = newimportService.tallyErrorMsg(key,invoice,returntype);
													Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
													impsError.setCellValue(errorVal);
													impsError.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if (returntype.equals(MasterGSTConstants.GSTR1) && (key.equals("b2baList") || key.equals("b2claList") || key.equals("expaList") || key.equals("cdnraList") || key.equals("cdnuraList")) && isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getOinum()) 
													&& isNotEmpty(oinum.trim()) && !Pattern.matches(INVOICENO_REGEX, oinum.trim())) {
											failed++;
												if ((key.equals("b2baList") || key.equals("b2claList") || key.equals("expaList") || key.equals("cdnraList")) && isEmpty(invoice.getStrOdate())) {
													if(index != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Original Invoice Number Format");
														gstincell.setCellStyle(style);
													}
													errorIndxes.add(index);
												}
										}else if (returntype.equals(MasterGSTConstants.GSTR1) && (key.equals("b2baList") || key.equals("b2claList") || key.equals("expaList") || key.equals("cdnraList") || key.equals("cdnuraList")) 
												&& (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0))
												&& isEmpty(invoice.getB2b().get(0).getInv().get(0).getOinum())) && isEmpty(oinum.trim())) {
											if ((key.equals("b2baList") || key.equals("b2claList") || key.equals("expaList") || key.equals("cdnraList") || key.equals("cdnuraList")) && isEmpty(invoice.getStrOdate())) {
												failed++;
												if(index != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Original Invoice Number");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if(returntype.equals(MasterGSTConstants.GSTR1) && (key.equals("b2baList") || key.equals("b2claList") || key.equals("expaList") || key.equals("cdnraList") || key.equals("cdnuraList")) && isEmpty(invoice.getStrOdate())) { 
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid Original Invoice Date");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}	
										}else if (returntype.equals(MasterGSTConstants.GSTR4)
												&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStrDate())
												|| (key.equals("creditList") && (isEmpty(((GSTR4) invoice).getCdnr()) || isEmpty(((GSTR4) invoice).getB2b().get(0).getCtin()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtNum()) || isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
												|| (key.equals("cdnurList") && (isEmpty(((GSTR4) invoice).getCdnur()) || isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtNum()) || isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtty())))
												|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())))
												|| (key.equals("exportList") && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
														|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												datatypeSheet.getRow(index).createCell(lastcolumn).setCellValue("Please Enter Valid Values");
												errorIndxes.add(index);
											}
										}else if((key.equals("b2csaList") || key.equals("b2cList")) && (isEmpty(invoice.getStatename()) || "Place Of Supply".equalsIgnoreCase(invoice.getStatename()))) {// || "Place Of Supply".equalsIgnoreCase(invoice.getStatename())
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													String errorVal = newimportService.tallyErrorMsg(key,invoice,returntype);
													Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
													impsError.setCellValue(errorVal);
													impsError.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										} else if (!key.equals("advAdjustedList") && !key.equals("ataList") && !key.equals("txpaList") && !key.equals("advReceiptList") && !key.equals("nilList") && !key.equals("impgList") && !key.equals("itrvslList") && (!key.equals("b2cList") && !key.equals("b2csaList") && !key.equals("creditList") && !key.equals("cdnuraList")
													&& (isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue()))))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													String errorVal = "";
													if(isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue()))) {
														errorVal += "Please Enter Values";
													}
													Cell error = datatypeSheet.getRow(index).createCell(lastcolumn);
													error.setCellValue(errorVal);
													error.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if (key.equals("impgList") && (isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue())))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													String errorVal = newimportService.tallyErrorMsg(key,invoice,returntype);
													Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
													impsError.setCellValue(errorVal);
													impsError.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if (key.equals("impgList") && (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim()))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid Invoice Number Format");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if(key.equals("nilList") && (isEmpty(invoice.getNil().getInv().get(0).getSplyType()) || (isEmpty(invoice.getNil().getInv().get(0).getExptAmt())
												&& isEmpty(invoice.getNil().getInv().get(0).getNilAmt()) && isEmpty(invoice.getNil().getInv().get(0).getNgsupAmt())))) {
											boolean nilFlag = false;
											String errorMsg = null;
											if(isEmpty(invoice.getNil().getInv().get(0).getSplyType())){
												nilFlag =  true;
												errorMsg = "Please provide valid description";
											}else if(isEmpty(invoice.getNil().getInv().get(0).getExptAmt())
													&& isEmpty(invoice.getNil().getInv().get(0).getNilAmt()) && isEmpty(invoice.getNil().getInv().get(0).getNgsupAmt())){
												nilFlag =  true;
												errorMsg = "Please provide any one amount  (Exempted amount / Nil rated amount/ Non-GST supplies)";
											}
											if(nilFlag){
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													if(index != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
														gstincell.setCellValue(errorMsg);
														gstincell.setCellStyle(style);
													}
													errorIndxes.add(index);
												}
											}
										}else if((key.equals("advReceiptList") || key.equals("advAdjustedList")) && (isEmpty(invoice.getStatename()) ||  isEmpty(invoice.getItems().get(0).getAdvAdjustableAmount()))) {
											String errorMsg = "";
											failed++;
											if(isEmpty(invoice.getStatename())) {
												errorMsg ="place of supply could not empty";
											}
											if(isEmpty(invoice.getItems().get(0).getAdvAdjustableAmount())) {
												errorMsg +=" Please Enter Values";
											}
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue(errorMsg);
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if ((key.equals("ataList") || key.equals("txpaList")) && (isEmpty(invoice.getStatename()) || "Original Place Of Supply".equalsIgnoreCase(invoice.getStatename()))) {
											String errorMsg = "";
											failed++;
											if(isEmpty(invoice.getStatename())) {
												errorMsg = "place of supply could not empty";
											}
											if(isEmpty(invoice.getItems().get(0).getAdvAdjustableAmount())) {
												errorMsg +=" Please Enter Values";
											}
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue(errorMsg);
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if(key.equals("b2csaList") && (isEmpty(invoice.getStrOdate()) || isEmpty(invoice.getB2cs()))) {
											String errorMsg = null;
											boolean isError = false;
											if(isEmpty(invoice.getStrOdate())) {
												errorMsg = "Financial Year could not empty";
												isError = true;
											}
											if(isEmpty(invoice.getB2cs())) {
												errorMsg = errorMsg == null ? "Original Month could not empty" : errorMsg +", Original Month could not empty";
												isError = true;
											}else {
												if(isEmpty(invoice.getB2cs().get(0))) {
													errorMsg = errorMsg == null ? "Original Month could not empty" : errorMsg +", Original Month could not empty";
													isError = true;
												}else {
													if(isEmpty(invoice.getB2cs().get(0).getOmon())) {
														errorMsg = errorMsg == null ? "Original Month could not empty" : errorMsg +", Original Month could not empty";
														isError = true;
													}
												}
											}
											if(isError) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													if(index != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
														gstincell.setCellValue(errorMsg);
														gstincell.setCellStyle(style);
													}
													errorIndxes.add(index);
												}
											}
										}else if(key.equals("ataList") && (isEmpty(invoice.getStrOdate()) || isEmpty(((GSTR1)invoice).getAt()))) {
											String errorMsg = null;
											boolean isError = false;
											if(isEmpty(invoice.getStrOdate())) {
												errorMsg = "Financial Year could not empty";
												isError = true;
											}
											
											if(key.equals("ataList")) {
												if(isEmpty(((GSTR1)invoice).getAt())) {
													errorMsg = errorMsg == null ? "Original Month could not empty" : errorMsg +", Original Month could not empty";
													isError = true;
												}else {
													if(isEmpty(((GSTR1)invoice).getAt().get(0))) {
														errorMsg = errorMsg == null ? "Original Month could not empty" : errorMsg +", Original Month could not empty";
														isError = true;
													}else {
														if(isEmpty(((GSTR1)invoice).getAt().get(0).getOmon())) {
															errorMsg = errorMsg == null ? "Original Month could not empty" : errorMsg +", Original Month could not empty";
															isError = true;
														}
													}
												}
											}
											
											if(isError) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													if(index != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
														gstincell.setCellValue(errorMsg);
														gstincell.setCellStyle(style);
													}
													errorIndxes.add(index);
												}
											}
										}else if(key.equals("txpaList") && (isEmpty(invoice.getStrOdate()) || "Financial Year".equals(invoice.getStrOdate()))) {// || isEmpty(((GSTR1)invoice).getTxpd())
											String errorMsg = null;
											boolean isError = false;
											if(isEmpty(invoice.getStrOdate())) {
												errorMsg = "Financial Year could not empty";
												isError = true;
											}
											
											if(key.equals("txpaList")) {
												if(isEmpty(((GSTR1)invoice).getTxpd())) {
													errorMsg = errorMsg == null ? "Original Month could not empty" : errorMsg +", Original Month could not empty";
													isError = true;
												}else {
													if(isEmpty(((GSTR1)invoice).getTxpd().get(0))) {
														errorMsg = errorMsg == null ? "Original Month could not empty" : errorMsg +", Original Month could not empty";
														isError = true;
													}else {
														if(isEmpty(((GSTR1)invoice).getTxpd().get(0).getOmon())) {
															errorMsg = errorMsg == null ? "Original Month could not empty" : errorMsg +", Original Month could not empty";
															isError = true;
														}
													}
												}
											}
											if(isError) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
													if(index != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
														gstincell.setCellValue(errorMsg);
														gstincell.setCellStyle(style);
													}
													errorIndxes.add(index);
												}
											}
										}else {
											invoice = newimportService.importInvoicesTally(invoice, client, returntype, branch, vertical,month,year,patterns,statesMap);												
											
											if(key.equals("b2cList") || key.equals("b2csaList")) {
												if(isNotEmpty(tallyfinancialYear) && isNotEmpty(tallymonth)) {
													Calendar cal = Calendar.getInstance();
													int b2ctmnth = Integer.parseInt(tallymonth);
													int b2cyear = Integer.parseInt(tallyfinancialYear);
													if(b2ctmnth >= 1 && b2ctmnth < 4) {
														b2cyear = b2cyear+1;
													}
													cal.set(b2cyear, b2ctmnth - 1, 1);
													invoice.setDateofinvoice(cal.getTime());
												}
											}else if(key.equals("nilList")) {
												if(isNotEmpty(tallyfinancialYear) && isNotEmpty(tallymonth)) {
													Calendar cal = Calendar.getInstance();
													int nilmnth = Integer.parseInt(tallymonth);
													int nilyear = Integer.parseInt(tallyfinancialYear);
													if(nilmnth >= 1 && nilmnth < 4) {
														nilyear = nilyear+1;
													}
													cal.set(nilyear, nilmnth - 1, 1);
													invoice.setDateofinvoice(cal.getTime());
												}
											}
											if(key.equalsIgnoreCase("creditList") || key.equalsIgnoreCase("cdnraList")) {
												if(isNotEmpty(invoice.getStatename())) {
													if (isNotEmpty(((GSTR1) invoice).getCdnr()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0)) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt())  && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0))) {
														String statename = invoice.getStatename();
														String pos = "";
														if (statename.contains("-")) {
															pos = statename.substring(0, statename.indexOf("-")).trim();
														}else {
															StateConfig state = states.get(statename.replaceAll("\\s", "").toLowerCase());
															if(isNotEmpty(state)) {
																pos = state.getTin().toString();
															}
														}
														((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setPos(pos);
													}
												}
											}
											if(key.equalsIgnoreCase("cdnurList") || key.equalsIgnoreCase("cdnuraList")) {
												if(isNotEmpty(invoice.getStatename())) {
													if (isNotEmpty(invoice.getCdnur()) && isNotEmpty(invoice.getCdnur().get(0))) {
														if(isNotEmpty(invoice.getCdnur().get(0).getTyp())) {
															String typ = invoice.getCdnur().get(0).getTyp();
															if(typ.equalsIgnoreCase("B2CL")) {
																String statename = invoice.getStatename();
																String pos = "";
																if (statename.contains("-")) {
																	pos = statename.substring(0, statename.indexOf("-")).trim();
																}else {
																	StateConfig state = states.get(statename.replaceAll("\\s", "").toLowerCase());
																	if(isNotEmpty(state)) {
																		pos = state.getTin().toString();
																	}
																}
																invoice.getCdnur().get(0).setPos(pos);
															}
														}
													}
												}
											}
											if(key.equals("b2csaList") || key.equals("ataList") || key.equals("txpaList")) {
												String finacialYear = null;
												if(isNotEmpty(invoice.getStrOdate())){
													finacialYear = invoice.getStrOdate();
												}
												String omon = null;
												if(key.equals("txpaList")) {
													if(isNotEmpty(((GSTR1)invoice).getTxpd()) && isNotEmpty(((GSTR1)invoice).getTxpd().get(0)) && isNotEmpty(((GSTR1)invoice).getTxpd().get(0).getOmon())){
														omon = ((GSTR1)invoice).getTxpd().get(0).getOmon();
													}
													((GSTR1)invoice).getTxpd().get(0).setOmon(MonthCodes.getCodes(finacialYear, omon));
												}
												if(key.equals("ataList")) {
													if(isNotEmpty(((GSTR1)invoice).getAt()) && isNotEmpty(((GSTR1)invoice).getAt().get(0)) && isNotEmpty(((GSTR1)invoice).getAt().get(0).getOmon())){
														omon = ((GSTR1)invoice).getAt().get(0).getOmon();
													}
													((GSTR1)invoice).getAt().get(0).setOmon(MonthCodes.getCodes(finacialYear, omon));
												}
												if(key.equals("b2csaList")) {
													if(isNotEmpty(((GSTR1)invoice).getB2cs()) && isNotEmpty(((GSTR1)invoice).getB2cs().get(0)) && isNotEmpty(((GSTR1)invoice).getB2cs().get(0).getOmon())){
														omon = ((GSTR1)invoice).getB2csa().get(0).getOmon();
													}													
													((GSTR1)invoice).getB2cs().get(0).setOmon(MonthCodes.getCodes(finacialYear, omon));
												}
											}
											
											filteredList.add(invoice);//filteredList
											if(!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("ataList") && !key.equals("txpaList") && !key.equals("b2cList") && !key.equals("b2csaList") && !key.equals("nilList")) {
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
										index++;
									}
								}
								if (skipRows == 1) {
									summary.setSuccess((beans.get(key).size()) - failed);
								} else {
									if (failed != 0) {
										failed = failed - 1;
									}
									summary.setSuccess((beans.get(key).size() - 1) - failed);
								}
								int eC = 4;
								for (int i = 4; i <= datatypeSheet.getLastRowNum(); i++) {
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
							summary.setFailed(failed);
							if(key.equals("advReceiptList") || key.equals("advAdjustedList") || key.equals("ataList") || key.equals("txpaList") || key.equals("b2cList") || key.equals("b2csaList") || key.equals("nilList") || key.equals("hsnSummaryList") || key.equals("docSummaryList")) {
								if(key.equals("hsnSummaryList") || key.equals("docSummaryList")) {
									summary.setInvsuccess(summary.getTotal());
									summary.setSuccess(summary.getTotal());
								}else {
									summary.setInvsuccess(summary.getSuccess());
								}
								summary.setTotalinvs(summary.getTotal());
								summary.setInvfailed(summary.getFailed());
							}else {
								summary.setInvsuccess(invsucess);
								summary.setTotalinvs(totalinvs-1);
								summary.setInvfailed(totalinvs-1 - invsucess);
							}
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
					int hsnMonth = 0;
					int hsnYear = 0;
					for (String key : beans.keySet()) {
						if (beans.get(key) instanceof List) {
							List<InvoiceParent> invoices = beans.get(key);
							if (isNotEmpty(invoices)) {
								for (InvoiceParent invoice : invoices) {
									String fp = cFp;
									if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
										Calendar cal = Calendar.getInstance();
										cal.setTime(invoice.getDateofinvoice());
										int iMonth = cal.get(Calendar.MONTH) + 1;
										int iYear = cal.get(Calendar.YEAR);
										if(iYear >= hsnYear) {
											hsnYear = iYear;
											if(iMonth >= hsnMonth) {
												hsnMonth = iMonth;
											}
										}
										fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
										fpMap.put(iMonth, iYear);
									}
									invoice.setFp(fp);
								}
							}
						} else {
							InvoiceParent invoice = (InvoiceParent) beans.get(key);
							if (isNotEmpty(invoice)) {
								String fp = cFp;
								if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
									Calendar cal = Calendar.getInstance();
									cal.setTime(invoice.getDateofinvoice());
									int iMonth = cal.get(Calendar.MONTH) + 1;
									int iYear = cal.get(Calendar.YEAR);
									/*
									 * if(iYear >= hsnYear) { hsnYear = iYear; if(iMonth >= hsnMonth) { hsnMonth =
									 * iMonth; } }
									 */
									fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
									fpMap.put(iMonth, iYear);
								}
								invoice.setFp(fp);
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
					if (isNotEmpty(returntype) && returntype.equals(GSTR2)) {
						returntype = PURCHASE_REGISTER;
					}
					hsnSummary.setClientid(clientid);
					hsnSummary.setUserid(id);
					String hsnstrMonth = hsnMonth < 10 ? "0" + hsnMonth : hsnMonth + "";
					String hsncFp = hsnstrMonth + hsnYear;
					hsnSummary.setReturnPeriod(hsncFp);
					hsnSummary.setReturnType(returntype);
					hsnSummary.setImporttype("GSTR1OFFLINETOOL");
					HSNDetails hsndetails = hsnSummaryRepository.findByClientidAndReturnPeriodAndReturnType(clientid,hsncFp,returntype);
					if(isNotEmpty(hsndetails)) {
						hsndetails.setClientid(clientid);
						hsndetails.setUserid(id);
						hsndetails.setReturnPeriod(hsncFp);
						hsndetails.setReturnType(returntype);
						hsndetails.setImporttype("GSTR1OFFLINETOOL");
						hsndetails.setHsnData(hsnSummary.getHsnData());
					}else {
						hsndetails = hsnSummary;
					}
					hsnSummaryRepository.save(hsndetails);
					
					documentIssue.setClientid(clientid);
					documentIssue.setReturnPeriod(hsncFp);
					GSTR1DocumentIssue docdetails = gstr1DocumentIssueRepository.findByClientidAndReturnPeriod(clientid, hsncFp);
					if(isNotEmpty(docdetails)) {
						docdetails.setClientid(clientid);
						docdetails.setReturnPeriod(hsncFp);
						docdetails.setDocDet(documentIssue.getDocDet());
					}else {
						docdetails = documentIssue;
					}
					gstr1DocumentIssueRepository.save(docdetails);
					response.setMonth(month);
					response.setYear(year);
					response.setSummaryList(summaryList);
					if (isNotEmpty(summaryList)) {
						int failedCount = 0;
						for (ImportSummary summary : summaryList) {
							if (summary.getInvfailed() > 0) {
								failedCount += summary.getInvfailed();
							}
						}
						if (isNotEmpty(convFile) && failedCount > 0) {
							HttpSession session = request.getSession(false);
							if (isNotEmpty(session)) {
								session.setAttribute("error.xls", convFile);
								response.setShowLink(true);
							}
						}else {
							try {
								convFile.delete();
							} catch (Exception e) {
							}
						}
					}
					
					newimportService.updateExcelData(beans, list, returntype, id, fullname, clientid, TALLY_TEMPLATE,otherconfig);
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
		logger.debug(CLASSNAME + method + END);
		return response;
	}
	
	
	
		
}
