package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.ADVANCES;
import static com.mastergst.core.common.MasterGSTConstants.ATA;
import static com.mastergst.core.common.MasterGSTConstants.ATPAID;
import static com.mastergst.core.common.MasterGSTConstants.B2B;
import static com.mastergst.core.common.MasterGSTConstants.B2BA;
import static com.mastergst.core.common.MasterGSTConstants.B2C;
import static com.mastergst.core.common.MasterGSTConstants.B2CL;
import static com.mastergst.core.common.MasterGSTConstants.B2CLA;
import static com.mastergst.core.common.MasterGSTConstants.B2CSA;
import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.CDNA;
import static com.mastergst.core.common.MasterGSTConstants.CDNUR;
import static com.mastergst.core.common.MasterGSTConstants.CDNURA;
import static com.mastergst.core.common.MasterGSTConstants.CREDIT_DEBIT_NOTES;
import static com.mastergst.core.common.MasterGSTConstants.EXCEL_TEMPLATE;
import static com.mastergst.core.common.MasterGSTConstants.EXPA;
import static com.mastergst.core.common.MasterGSTConstants.EXPORTS;
import static com.mastergst.core.common.MasterGSTConstants.NIL;
import static com.mastergst.core.common.MasterGSTConstants.TALLY_TEMPLATE;
import static com.mastergst.core.common.MasterGSTConstants.TXPA;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
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
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR3BDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR4;
import com.mastergst.usermanagement.runtime.domain.ImportResponse;
import com.mastergst.usermanagement.runtime.domain.ImportSummary;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.repository.HSNSummaryRepository;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ImportMapperService;
import com.mastergst.usermanagement.runtime.service.ImportSageService;
import com.mastergst.usermanagement.runtime.service.NewimportService;
import com.mastergst.usermanagement.runtime.support.WorkbookUtility;

/**
 * Handles all import sage activities.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 * @since 1.0
 */
@Controller
public class ImportSageInvoiceController {
	
	private static final Logger logger = LogManager.getLogger(ImportSageInvoiceController.class.getName());
	private static final String CLASSNAME = "ImportSageInvoiceController::";

	private String gstnFormat = "[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}|[0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1}|[0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1}|[0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1}|[9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1}";
	private static DecimalFormat df2 = new DecimalFormat("#.##");

	private static final String INVOICENO_REGEX ="^[0-9a-zA-Z/-]{1,16}$";
	private static final String IMPGINVOICENO_REGEX = "^[0-9]{1,7}$";
	Double[] csgstrate = { 0.0,0.05,0.125,0.5,0.75,1.5,2.5,3.75,6.0,9.0,14.0 }; 
	Double[] igstrate = { 0.0,0.1,0.25,1.0,1.5,3.0,5.0,7.5,12.0,18.0,28.0 }; 
	@Autowired
	private ClientService clientService;
	@Autowired
	private NewimportService newimportService;
	@Autowired
	private ImportSageService importSageService;
	@Autowired
	private ResourceLoader resourceLoader;
	@Autowired
	private	HSNSummaryRepository hsnSummaryRepository; 
	@Autowired
	private ConfigService configService;
	
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
	@RequestMapping(value = "/importSageInvoice/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody ImportResponse importSageInvoice(@PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("file") MultipartFile file,
			@RequestParam(value = "branch", required = false) String branch,
			@RequestParam(value = "vertical", required = false) String vertical,
			@RequestParam("list") List<String> list, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "importSageInvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		String stateName = client.getStatename();
		int skipRows = 1;
		OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
		if (isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableSalesFields())) {
			otherconfig.setEnableSalesFields(false);
		}
		if (isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnablePurFields())) {
			otherconfig.setEnablePurFields(false);
		}

		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		if (!file.isEmpty()) {
			logger.info(CLASSNAME + method + file.getOriginalFilename());
			try {
				String xmlPath = "classpath:mastergst_sales_excel_sage_config_new.xml";
				if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
					xmlPath = "classpath:mastergst_purchase_excel_sage_config_new.xml";
				}
				Resource config = resourceLoader.getResource(xmlPath);
				DateConverter converter = new DateConverter();
				String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd","dd-MM-yy","dd-MMM-yy","dd/MM/yy","dd/MMM/yy"};
				converter.setPatterns(patterns);
				ConvertUtils.register(converter, Date.class);
				ReaderConfig.getInstance().setSkipErrors(true);
				XLSReader xlsReader = ReaderBuilder.buildFromXML(config.getInputStream());
				// JxlsHelper.getInstance().setProcessFormulas(false);
				Map<String, List<InvoiceParent>> sheetMap = getExcelSheetMap();
				Map<String, List<InvoiceParent>> beans = Maps.newHashMap();
				beans.put("invoiceList", sheetMap.get("invoiceList"));
				XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
				logger.debug(CLASSNAME + method + "Status: {}", status.isStatusOK());
				if (status.isStatusOK()) {
					Map<String,StateConfig> statetin = getStatesTinMap();
					Workbook workbook = WorkbookFactory.create(file.getInputStream());
					String filename = file.getOriginalFilename();
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
					Map<String,List<InvoiceParent>> invoicesByType = Maps.newHashMap();
					for (String key : beans.keySet()) {
						if (isNotEmpty(sheetMap.get(key)) && sheetMap.get(key).size() > 0) {
							ImportSummary summary = new ImportSummary();
							if (key.equals("invoiceList")) {
								if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
								summary.setName("Purchase_Register");
								}else {
									summary.setName("Sales_Register");
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
						    Cell cell = datatypeSheet.getRow(1).createCell(datatypeSheet.getRow(1).getLastCellNum());
						    cell.setCellValue("Errors");
						    cell.setCellStyle(style);
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
							if (isNotEmpty(invoices) && invoices.size() > 0) {
								int index = 1;
								summary.setTotal((beans.get(key).size())-1); 
								List<Integer> errorIndxes = Lists.newArrayList();
								for (InvoiceParent invoice : invoices) {
									if(((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)))) {
										if(isEmpty(invoice.getStatename())) {
											if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
												invoice.setStatename(client.getStatename());
											}
										}
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
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows-1) {
													Cell gstincell = datatypeSheet.getRow(index+1).createCell(lastcolumn);
													gstincell.setCellValue("Invalid Invoice Number Format");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index+1);
											}
									}else if(isEmpty(invoice.getInvtype())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
												&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Invoice Type");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
										}
									}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isNotEmpty(invoice.getItems().get(0).getCgstrate()) && !useList(csgstrate,invoice.getItems().get(0).getCgstrate())) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
													if(index != 1) {
														Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
														gstincell.setCellValue("Please Enter Valid Tax Rate");
														gstincell.setCellStyle(style);
													}
													errorIndxes.add(index);
											}
									}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isNotEmpty(invoice.getItems().get(0).getSgstrate()) && !useList(csgstrate,invoice.getItems().get(0).getSgstrate())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
												&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Valid Tax Rate");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
										}
									}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isNotEmpty(invoice.getItems().get(0).getIgstrate()) && !useList(igstrate,invoice.getItems().get(0).getIgstrate())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
												&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Valid Tax Rate");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
										}
									}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isNotEmpty(invoice.getItems().get(0).getUgstrate()) && !useList(csgstrate,invoice.getItems().get(0).getUgstrate())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
												&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Valid Tax Rate");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
										}
									}else if(invoice.getInvtype().equalsIgnoreCase("B2B")) {
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())
												&& !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
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
										}else if((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) && isEmpty(invoice.getB2b().get(0).getCtin())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
													if(index != 1) {
														Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
														gstincell.setCellValue("Please Enter GSTIN Number");
														gstincell.setCellStyle(style);
													}
													errorIndxes.add(index);
											}
										}else if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Valid Values");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else {
											invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"sage");
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
									}else if(invoice.getInvtype().equalsIgnoreCase("B2BUR")) {
										if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Valid Values");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else {
											invoice.setInvtype("B2B Unregistered");
											invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"sage");
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
									}else if(invoice.getInvtype().equalsIgnoreCase("Credit Note")) {
										String invTyps = "";
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())
												&& !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
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
										}else if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Valid Values");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) && isEmpty(invoice.getCdn().get(0).getNt().get(0).getInum())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Original Invoice Number");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) && isEmpty(invoice.getCdn().get(0).getNt().get(0).getIdt())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Original Invoice Date");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if((returntype.equals(MasterGSTConstants.GSTR1) || returntype.equals("Sales Register")) && isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Original Invoice Number");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if((returntype.equals(MasterGSTConstants.GSTR1) || returntype.equals("Sales Register")) && isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Original Invoice Date");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else {
											if(isNotEmpty(invoice.getB2b().get(0).getCtin())) {
												invoice.setInvtype(MasterGSTConstants.CREDIT_DEBIT_NOTES);
												if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
													invoice.getCdn().get(0).getNt().get(0).setNtty("C");
													invoice.getCdn().get(0).getNt().get(0).setpGst("N");
													invoice.getCdn().get(0).getNt().get(0).setRsn("Others");
													try {
														Date invDate = DateUtils.parseDate(invoice.getCdn().get(0).getNt().get(0).getIdt(),patterns);
														invoice.getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
													} catch (java.text.ParseException e) {
														logger.info(CLASSNAME + method
																+ " Date parsing using format failed trying with POI date");
														try {
															double dblDate = Double.parseDouble(invoice.getCdn().get(0).getNt().get(0).getIdt());
															Date invDate = DateUtil.getJavaDate(dblDate);
															invoice.getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
														} catch (NumberFormatException exp) {
															logger.info(CLASSNAME + method + " date parsing failed for "+ invoice.getTransactionDate());
															Calendar cal = Calendar.getInstance();
															cal.set(year, month - 1, 1);
															invoice.getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
														}
													}
												}else {
													((GSTR1)invoice).getCdnr().get(0).getNt().get(0).setNtty("C");
													((GSTR1)invoice).getCdnr().get(0).getNt().get(0).setpGst("N");
													((GSTR1)invoice).getCdnr().get(0).getNt().get(0).setRsn("Others");
													try {
														Date invDate = DateUtils.parseDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt(),patterns);
														((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
													} catch (java.text.ParseException e) {
														logger.info(CLASSNAME + method
																+ " Date parsing using format failed trying with POI date");
														try {
															double dblDate = Double.parseDouble(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
															Date invDate = DateUtil.getJavaDate(dblDate);
															((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
														} catch (NumberFormatException exp) {
															logger.info(CLASSNAME + method + " date parsing failed for "+ invoice.getTransactionDate());
															Calendar cal = Calendar.getInstance();
															cal.set(year, month - 1, 1);
															((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
														}
													}
												}
											}else {
												invoice.setInvtype(MasterGSTConstants.CDNUR);
												invoice.getCdnur().get(0).setNtty("C");
												invoice.getCdnur().get(0).setpGst("N");
												invoice.getCdnur().get(0).setRsn("Others");
												if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
													invoice.getCdnur().get(0).setInum(invoice.getCdn().get(0).getNt().get(0).getInum());
													try {
														Date invDate = DateUtils.parseDate(invoice.getCdn().get(0).getNt().get(0).getIdt(),patterns);
														invoice.getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
													} catch (java.text.ParseException e) {
														logger.info(CLASSNAME + method+ " Date parsing using format failed trying with POI date");
														try {
															double dblDate = Double.parseDouble(invoice.getCdn().get(0).getNt().get(0).getIdt());
															Date invDate = DateUtil.getJavaDate(dblDate);
															invoice.getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
														} catch (NumberFormatException exp) {
															Calendar cal = Calendar.getInstance();
															cal.set(year, month - 1, 1);
															invoice.getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
														}
													}
												}else {
													invoice.getCdnur().get(0).setInum(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getInum());
													try {
														Date invDate = DateUtils.parseDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt(),patterns);
	
														((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
													} catch (java.text.ParseException e) {
														logger.info(CLASSNAME + method
																+ " Date parsing using format failed trying with POI date");
														try {
															double dblDate = Double.parseDouble(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
															Date invDate = DateUtil.getJavaDate(dblDate);
															((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
														} catch (NumberFormatException exp) {
															logger.info(CLASSNAME + method + " date parsing failed for "+ invoice.getTransactionDate());
															Calendar cal = Calendar.getInstance();
															cal.set(year, month - 1, 1);
															((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
														}
													}
												}
												if (isNotEmpty(invoice.getCategorytype())) {
													String invTyp = invoice.getCategorytype();
													if ("B2BUR".equalsIgnoreCase(invTyp)) {
														invoice.getCdnur().get(0).setInvTyp("B2BUR");
														invTyps = "B2BUR";
													} else if ("IMPS".equalsIgnoreCase(invTyp)) {
														invoice.getCdnur().get(0).setInvTyp("IMPS");
														invTyps = "IMPS";
													} else {
														invoice.getCdnur().get(0).setInvTyp(invTyp);
														invTyps = invTyp;
													}
												} else {
													invoice.getCdnur().get(0).setInvTyp("B2BUR");
													invTyps = "B2BUR";
												}
											}
											invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"sage");
											if(isEmpty(invoice.getItems().get(0).getIgstamount())) {
												invoice.getItems().get(0).setIgstamount(0d);
											}
											if(isEmpty(invoice.getItems().get(0).getCgstamount())) {
												invoice.getItems().get(0).setCgstamount(0d);
											}
											if(isEmpty(invoice.getItems().get(0).getSgstamount())) {
												invoice.getItems().get(0).setSgstamount(0d);
											}
											
											if(isEmpty(invoice.getItems().get(0).getIgstavltax())) {
												invoice.getItems().get(0).setIgstavltax(0d);
											}
											if(isEmpty(invoice.getItems().get(0).getCgstavltax())) {
												invoice.getItems().get(0).setCgstavltax(0d);
											}
											if(isEmpty(invoice.getItems().get(0).getSgstavltax())) {
												invoice.getItems().get(0).setSgstavltax(0d);
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
									}else if(invoice.getInvtype().equalsIgnoreCase("Debit Note")) {
										String invTyps = "";
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())
												&& !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
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
										}else if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Valid Values");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) && isEmpty(invoice.getCdn().get(0).getNt().get(0).getIdt())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Original Invoice Date");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if((returntype.equals(MasterGSTConstants.GSTR1) || returntype.equals("Sales Register")) && isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Original Invoice Number");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if((returntype.equals(MasterGSTConstants.GSTR1) || returntype.equals("Sales Register")) && isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Original Invoice Date");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else {
											if(isNotEmpty(invoice.getB2b().get(0).getCtin())) {
												invoice.setInvtype(MasterGSTConstants.CREDIT_DEBIT_NOTES);
												if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
													invoice.getCdn().get(0).getNt().get(0).setNtty("D");
													invoice.getCdn().get(0).getNt().get(0).setpGst("N");
													invoice.getCdn().get(0).getNt().get(0).setRsn("Others");
													try {
														Date invDate = DateUtils.parseDate(invoice.getCdn().get(0).getNt().get(0).getIdt(),patterns);
	
														invoice.getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
													} catch (java.text.ParseException e) {
														logger.info(CLASSNAME + method
																+ " Date parsing using format failed trying with POI date");
														try {
															double dblDate = Double.parseDouble(invoice.getCdn().get(0).getNt().get(0).getIdt());
															Date invDate = DateUtil.getJavaDate(dblDate);
															invoice.getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
														} catch (NumberFormatException exp) {
															logger.info(CLASSNAME + method + " date parsing failed for "+ invoice.getTransactionDate());
															Calendar cal = Calendar.getInstance();
															cal.set(year, month - 1, 1);
															invoice.getCdn().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
														}
													}
												}else {
													((GSTR1)invoice).getCdnr().get(0).getNt().get(0).setNtty("D");
													((GSTR1)invoice).getCdnr().get(0).getNt().get(0).setpGst("N");
													((GSTR1)invoice).getCdnr().get(0).getNt().get(0).setRsn("Others");
													try {
														Date invDate = DateUtils.parseDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt(),patterns);
	
														((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
													} catch (java.text.ParseException e) {
														logger.info(CLASSNAME + method
																+ " Date parsing using format failed trying with POI date");
														try {
															double dblDate = Double.parseDouble(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
															Date invDate = DateUtil.getJavaDate(dblDate);
															((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
														} catch (NumberFormatException exp) {
															logger.info(CLASSNAME + method + " date parsing failed for "+ invoice.getTransactionDate());
															Calendar cal = Calendar.getInstance();
															cal.set(year, month - 1, 1);
															((GSTR1) invoice).getCdnr().get(0).getNt().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
														}
													}
												}
											}else {
												invoice.setInvtype(MasterGSTConstants.CDNUR);
												invoice.getCdnur().get(0).setNtty("D");
												invoice.getCdnur().get(0).setpGst("N");
												invoice.getCdnur().get(0).setRsn("Others");
												if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
													invoice.getCdnur().get(0).setInum(invoice.getCdn().get(0).getNt().get(0).getInum());
													try {
														Date invDate = DateUtils.parseDate(invoice.getCdn().get(0).getNt().get(0).getIdt(),patterns);
	
														invoice.getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
													} catch (java.text.ParseException e) {
														logger.info(CLASSNAME + method+ " Date parsing using format failed trying with POI date");
														try {
															double dblDate = Double.parseDouble(invoice.getCdn().get(0).getNt().get(0).getIdt());
															Date invDate = DateUtil.getJavaDate(dblDate);
															invoice.getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
														} catch (NumberFormatException exp) {
															Calendar cal = Calendar.getInstance();
															cal.set(year, month - 1, 1);
															invoice.getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
														}
													}
												}else {
													invoice.getCdnur().get(0).setInum(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getInum());
													try {
														Date invDate = DateUtils.parseDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt(),patterns);
	
														((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
													} catch (java.text.ParseException e) {
														logger.info(CLASSNAME + method+ " Date parsing using format failed trying with POI date");
														try {
															double dblDate = Double.parseDouble(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
															Date invDate = DateUtil.getJavaDate(dblDate);
															((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(invDate));
														} catch (NumberFormatException exp) {
															Calendar cal = Calendar.getInstance();
															cal.set(year, month - 1, 1);
															((GSTR1) invoice).getCdnur().get(0).setIdt(new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime()));
														}
													}
												}
												if (isNotEmpty(invoice.getCategorytype())) {
													String invTyp = invoice.getCategorytype();
													if ("B2BUR".equalsIgnoreCase(invTyp)) {
														invoice.getCdnur().get(0).setInvTyp("B2BUR");
														invTyps = "B2BUR";
													} else if ("IMPS".equalsIgnoreCase(invTyp)) {
														invoice.getCdnur().get(0).setInvTyp("IMPS");
														invTyps = "IMPS";
													} else {
														invoice.getCdnur().get(0).setInvTyp(invTyp);
														invTyps = invTyp;
													}
												} else {
													invoice.getCdnur().get(0).setInvTyp("B2BUR");
													invTyps = "B2BUR";
												}
											}
											
											invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"sage");
											if(isEmpty(invoice.getItems().get(0).getIgstamount())) {
												invoice.getItems().get(0).setIgstamount(0d);
											}
											if(isEmpty(invoice.getItems().get(0).getCgstamount())) {
												invoice.getItems().get(0).setCgstamount(0d);
											}
											if(isEmpty(invoice.getItems().get(0).getSgstamount())) {
												invoice.getItems().get(0).setSgstamount(0d);
											}
											
											if(isEmpty(invoice.getItems().get(0).getIgstavltax())) {
												invoice.getItems().get(0).setIgstavltax(0d);
											}
											if(isEmpty(invoice.getItems().get(0).getCgstavltax())) {
												invoice.getItems().get(0).setCgstavltax(0d);
											}
											if(isEmpty(invoice.getItems().get(0).getSgstavltax())) {
												invoice.getItems().get(0).setSgstavltax(0d);
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
									}else if(invoice.getInvtype().equalsIgnoreCase("IMPG")) {
										if(isEmpty(invoice.getCategorytype())) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Category Type");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim())){
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
										}else if(isNotEmpty(invoice.getCategorytype()) && "Y".equalsIgnoreCase(invoice.getCategorytype()) && isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())
												&& !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
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
										}else if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Valid Values");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else {
											invoice.setInvtype(MasterGSTConstants.IMP_GOODS);
											invoice.setStatename(client.getStatename());
											((PurchaseRegister)invoice).getImpGoods().get(0).setIsSez(invoice.getCategorytype().toUpperCase());
											if("Y".equalsIgnoreCase(invoice.getCategorytype())) {
												((PurchaseRegister)invoice).getImpGoods().get(0).setStin(invoice.getB2b().get(0).getCtin());
												
											}
											invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"sage");
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
									}else if(invoice.getInvtype().equalsIgnoreCase("IMPS")) {
										if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Valid Values");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else {
											invoice.setInvtype(MasterGSTConstants.IMP_SERVICES);
											invoice.setStatename(client.getStatename());
											invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"sage");
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
									}else if(invoice.getInvtype().equalsIgnoreCase("NIL")) {
										if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Valid Values");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else if(isEmpty(invoice.getCategorytype())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Category Type");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										}else {
											invoice.setInvtype(MasterGSTConstants.NIL);
											if (isNotEmpty(invoice.getCategorytype())) {
												String invTyp = invoice.getCategorytype();
												if ("Nil Rated".equalsIgnoreCase(invTyp)) {
													invoice.getItems().get(0).setType("Nil Rated");
												} else if ("Exempted".equalsIgnoreCase(invTyp)) {
													invoice.getItems().get(0).setType("Exempted");
												}else if ("Non-GST".equalsIgnoreCase(invTyp)) {
													invoice.getItems().get(0).setType("Non-GST");
												}else if ("From Compounding Dealer".equalsIgnoreCase(invTyp)) {
													invoice.getItems().get(0).setType("From Compounding Dealer");
												} else {
													invoice.getB2b().get(0).getInv().get(0).setInvTyp(invTyp);
												}
											}
											if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
												invoice.setStatename(client.getStatename());
											}
											invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"sage");
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
									}else if(invoice.getInvtype().equalsIgnoreCase("EXP")) {
										if(isEmpty(invoice.getStatename())) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												datatypeSheet.getRow(index+1).createCell(lastcolumn).setCellValue("Please Enter State Code");
												errorIndxes.add(index+1);
											}
										}else if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												datatypeSheet.getRow(index+1).createCell(lastcolumn).setCellValue("Please Enter Valid Values");
												errorIndxes.add(index+1);
											}
										}else if(isEmpty(invoice.getCategorytype())){
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												datatypeSheet.getRow(index+1).createCell(lastcolumn).setCellValue("Please Enter Category Type");
												errorIndxes.add(index+1);
											}
										}else {
											invoice.setInvtype(MasterGSTConstants.EXPORTS);
											if (isNotEmpty(invoice.getCategorytype())) {
												String invTyp = invoice.getCategorytype();
												if ("WPAY".equalsIgnoreCase(invTyp)) {
													invoice.getExp().get(0).setExpTyp("WPAY");
												} else if ("WOPAY".equalsIgnoreCase(invTyp)) {
													invoice.getExp().get(0).setExpTyp("WOPAY");
												}
											}
											if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
												invoice.setStatename(client.getStatename());
											}
											invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"sage");
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
								summary.setSuccess((beans.get(key).size()) - (failed));
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
								for(InvoiceParent inv : filteredList) {
									if(isEmpty(invoicesByType)) {
										List<InvoiceParent> invByType = Lists.newArrayList();
										invByType.add(inv);
										invoicesByType.put(inv.getInvtype().toUpperCase(), invByType);
									}else {
										if(invoicesByType.containsKey(inv.getInvtype().toUpperCase())) {
											invoicesByType.get(inv.getInvtype().toUpperCase()).add(inv);
										}else {
											List<InvoiceParent> invByType = Lists.newArrayList();
											invByType.add(inv);
											invoicesByType.put(inv.getInvtype().toUpperCase(), invByType);
										}
									}
								}
							}
							summary.setFailed((failed-1));
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
					} catch (IOException e) {
						logger.debug(CLASSNAME + method + "XLS Write ERROR {}", e);
						convFile = null;
					}
					beans.remove("invoiceList");
					Iterator<Map.Entry<String,List<InvoiceParent>>> entries = invoicesByType.entrySet().iterator();
					while (entries.hasNext()) {
						Map.Entry<String,List<InvoiceParent>> entry = entries.next();
						String invtype = entry.getKey();
						beans.put(importSageService.beantype(invtype), entry.getValue());
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
					if (returntype.equals(MasterGSTConstants.GSTR2)) {
						invReturnType = MasterGSTConstants.PURCHASE_REGISTER;
					}
					newimportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, TALLY_TEMPLATE,otherconfig);
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
	private Map<String, List<InvoiceParent>> getExcelSheetMap() {
		Map<String, List<InvoiceParent>> sheetMap = Maps.newHashMap();
		List<InvoiceParent> creditList = Lists.newArrayList();
		List<InvoiceParent> exportList = Lists.newArrayList();
		List<InvoiceParent> advReceiptList = Lists.newArrayList();
		List<InvoiceParent> b2bList = Lists.newArrayList();
		List<InvoiceParent> b2cList = Lists.newArrayList();
		List<InvoiceParent> b2clList = Lists.newArrayList();
		List<InvoiceParent> cdnurList = Lists.newArrayList();
		List<InvoiceParent> nilList = Lists.newArrayList();
		List<InvoiceParent> advAdjustedList = Lists.newArrayList();
		List<InvoiceParent> b2buList = Lists.newArrayList();
		List<InvoiceParent> impgList = Lists.newArrayList();
		List<InvoiceParent> impsList = Lists.newArrayList();
		List<InvoiceParent> itrvslList = Lists.newArrayList();
		List<InvoiceParent> invoiceList = Lists.newArrayList();
		List<InvoiceParent> hsnSummaryList = Lists.newArrayList();
		sheetMap.put("creditList", creditList);
		sheetMap.put("exportList", exportList);
		sheetMap.put("advReceiptList", advReceiptList);
		sheetMap.put("b2bList", b2bList);
		sheetMap.put("b2cList", b2cList);
		sheetMap.put("b2clList", b2clList);
		sheetMap.put("cdnurList", cdnurList);
		sheetMap.put("nilList", nilList);
		sheetMap.put("advAdjustedList", advAdjustedList);
		sheetMap.put("b2buList", b2buList);
		sheetMap.put("impgList", impgList);
		sheetMap.put("impsList", impsList);
		sheetMap.put("itrvslList", itrvslList);
		sheetMap.put("invoiceList", invoiceList);
		sheetMap.put("hsnSummaryList", hsnSummaryList);
		return sheetMap;
	}
	
	public static boolean useList(Double[] arr, Double targetValue) {
		return Arrays.asList(arr).contains(targetValue);
	}

}
