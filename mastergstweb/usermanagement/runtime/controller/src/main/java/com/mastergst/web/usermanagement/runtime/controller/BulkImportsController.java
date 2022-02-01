package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.EXCEL_TEMPLATE;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.common.MasterGSTConstants.MASTERGST_EXCEL_TEMPLATE;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.io.IOUtils;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
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
import com.mastergst.configuration.service.EmailService;
import com.mastergst.configuration.service.Message;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.ImportResponse;
import com.mastergst.core.util.ImportSummary;
import com.mastergst.core.util.NullUtil;
import com.mastergst.core.util.VmUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.BulkImportTask;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.CustomFields;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR1DocDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR1DocIssueDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR1DocumentIssue;
import com.mastergst.usermanagement.runtime.domain.GSTR4;
import com.mastergst.usermanagement.runtime.domain.GSTReturnSummary;
import com.mastergst.usermanagement.runtime.domain.HSNData;
import com.mastergst.usermanagement.runtime.domain.HSNDetails;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.TemplateMapper;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;
import com.mastergst.usermanagement.runtime.repository.CustomFieldsRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR1DocumentIssueRepository;
import com.mastergst.usermanagement.runtime.repository.HSNSummaryRepository;
import com.mastergst.usermanagement.runtime.service.BulkImportServices;
import com.mastergst.usermanagement.runtime.service.BulkImportTaskService;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ImportInvoiceService;
import com.mastergst.usermanagement.runtime.service.ImportMapperService;
import com.mastergst.usermanagement.runtime.service.ImportSageService;
import com.mastergst.usermanagement.runtime.service.MessageService;
import com.mastergst.usermanagement.runtime.service.NewBulkImportService;
import com.mastergst.usermanagement.runtime.service.NewimportService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.support.MonthCodes;
import com.mastergst.usermanagement.runtime.support.WorkbookUtility;

@Controller
public class BulkImportsController {

	private static final Logger logger = LogManager.getLogger(BulkImportsController.class.getName());
	private static final String CLASSNAME = "BulkImportsController::";
	
	private String gstnFormat = "[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}|[0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1}|[0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1}|[0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1}|[9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1}";

	private static final String INVOICENO_REGEX ="^[0-9a-zA-Z/-]{1,16}$";
	private static final String IMPGINVOICENO_REGEX = "^[0-9]{1,7}$";
	
	Double[] csgstrate = { 0.0,0.05,0.125,0.5,0.75,1.5,2.5,3.75,6.0,9.0,14.0 }; 
	Double[] igstrate = { 0.0,0.1,0.25,1.0,1.5,3.0,5.0,7.5,12.0,18.0,28.0 };
	
	@Autowired	private EmailService emailService;
	@Autowired	private UserService userService;
	@Autowired	private ProfileService profileService;
	@Autowired	private ConfigService configService;
	@Autowired	private ResourceLoader resourceLoader;
	@Autowired	private ClientService clientService;
	@Autowired	BulkImportServices bulkImportServices;
	@Autowired	private BulkImportTaskService bulkImportTaskService; 
	@Autowired	private ImportMapperService importMapperService;
	@Autowired	private	HSNSummaryRepository hsnSummaryRepository; 
	@Autowired	private ImportSageService importSageService;
	@Autowired	private NewBulkImportService newBulkImportService;
	@Autowired	UserRepository userRepository;
	@Autowired	private ImportInvoiceService importInvoiceService;
	@Autowired	private NewimportService newimportService;
	@Autowired	private	GSTR1DocumentIssueRepository gstr1DocumentIssueRepository;
	@Autowired	MessageService messageService;
	@Autowired	CustomFieldsRepository customFieldsRepository;
	
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
	
	@RequestMapping(value = "/bulkInvoices/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody void uploadBulkInvoice(@PathVariable("id") String id, @PathVariable("name") String fullname, @PathVariable("usertype") String usertype,
			@PathVariable("clientid") String clientid, @RequestParam("returntype") String returntype,
			@PathVariable("month") int month, @PathVariable("year") int year,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value="branch", required=false) String branch,
			@RequestParam(value="vertical", required=false) String vertical,
			@RequestParam(value="list", required=false) List<String> list,
			@RequestParam(value="btallyfinancialYear", required=false) String btallyfinancialYear,
			@RequestParam(value="btallymonth", required=false) String btallymonth,
			@RequestParam(value="ccmailid", required=false) String ccmailid,
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "uploadBulkInvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		if(!file.isEmpty()) {
			BulkImportTask bulk=bulkImportTaskService.createBulkImportTask(new BulkImportTask(id,clientid,"pending",returntype.equalsIgnoreCase("GSTR1")?"Sales":returntype,file.getOriginalFilename()));
			//System.out.println(file.getOriginalFilename()+" "+file.getContentType());
			if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase("FHPLTemplate"+MasterGSTConstants.GSTR1)  || returntype.equalsIgnoreCase("mastergst_all_fileds"+MasterGSTConstants.GSTR1)) {
				bulkImports(id, fullname, clientid, returntype, file, month, year, branch, vertical, list, model, request,bulk, ccmailid);
			}else if(returntype.equalsIgnoreCase("Tally"+MasterGSTConstants.GSTR1) || returntype.equalsIgnoreCase("TallyPurchaseRegister")){
				if(returntype.equalsIgnoreCase("Tally"+MasterGSTConstants.GSTR1)) {
					returntype = MasterGSTConstants.GSTR1;
				}else {
					returntype = MasterGSTConstants.PURCHASE_REGISTER;
				}
				bulkTallyImports(id, fullname, clientid, returntype, file, month, year, branch, vertical, list,btallyfinancialYear, btallymonth,model, request,bulk, ccmailid);
			}else if(returntype.equalsIgnoreCase("TallyV17"+MasterGSTConstants.GSTR1) || returntype.equalsIgnoreCase("TallyPrime"+MasterGSTConstants.GSTR1)){
				String tallytype = returntype; 
				if(returntype.equalsIgnoreCase("TallyV17"+MasterGSTConstants.GSTR1) || returntype.equalsIgnoreCase("TallyPrime"+MasterGSTConstants.GSTR1)) {
					returntype = MasterGSTConstants.GSTR1;
				}
				bulkTallyImportsNewVersion(id, fullname, clientid, returntype,tallytype, file, month, year, branch, vertical, list,btallyfinancialYear, btallymonth,model, request,bulk, ccmailid);
			}else if(returntype.equalsIgnoreCase("GSTR1OFFLINE")){
				returntype = MasterGSTConstants.GSTR1;
				bulkGstr1Offline(id,fullname,clientid,returntype,returntype,file,month,year,branch,vertical,list, btallyfinancialYear,btallymonth,model, request, bulk,ccmailid);
			}else if(returntype.equalsIgnoreCase("SagePurchaseRegister") || returntype.equalsIgnoreCase("SageGSTR1")){
				if(returntype.equalsIgnoreCase("Sage"+MasterGSTConstants.GSTR1)) {
					returntype = MasterGSTConstants.GSTR1;
				}else {
					returntype = MasterGSTConstants.PURCHASE_REGISTER;
				}
				bulkSageImports(id, fullname, clientid, returntype, file, month, year, branch, vertical, list, model, request,bulk, ccmailid);
			}else if(returntype.equalsIgnoreCase("WalterPurchaseRegister")){
				returntype = MasterGSTConstants.PURCHASE_REGISTER;
				bulkWalterImports(id, fullname, clientid, returntype, file, month, year, branch, vertical, list, model, request,bulk, ccmailid);
			}else if(returntype.equalsIgnoreCase(MasterGSTConstants.EINVOICE)){
				returntype = MasterGSTConstants.GSTR1;
				bulkImportsForEinvoice(id, fullname, clientid, returntype, file, month, year, branch, vertical, list, model, request,bulk, ccmailid, "mgst");
			}else if(returntype.equalsIgnoreCase("EntertainmentEINVOICE")){
				returntype = MasterGSTConstants.GSTR1;
				bulkImportsForEinvoice(id, fullname, clientid, returntype, file, month, year, branch, vertical, list, model, request,bulk, ccmailid, "entertainment");
			}else if(returntype.equalsIgnoreCase("SageEINVOICE")) {
				returntype = MasterGSTConstants.GSTR1;
				bulkImportsForEinvoice(id, fullname, clientid, returntype, file, month, year, branch, vertical, list, model, request,bulk, ccmailid, "sage");
			}else {
				String hsnFp = null;
				if(btallyfinancialYear == null || btallymonth == null || btallyfinancialYear.trim() == "" || btallymonth.trim() == "") {
					Calendar cal = Calendar.getInstance();
					int mth = cal.get(Calendar.MONTH)+1;
					int yr = cal.get(Calendar.YEAR);
					hsnFp = mth <= 9 ? "0"+mth+""+yr : mth+""+yr+"";
				}else {
					int mth = Integer.parseInt(btallymonth);
					int yr = Integer.parseInt(btallyfinancialYear);
					hsnFp = mth <= 9 ? "0"+mth+""+yr : mth+""+yr+"";
				}
				customBulkImports(id, fullname, clientid, returntype, file, month, year, branch, vertical, list,hsnFp, model, request,bulk, ccmailid);
			}
		}
		logger.debug(CLASSNAME + method + MasterGSTConstants.END);
	}
	@Async
	public void bulkImports(String id, String fullname, String clientid, String returntype, MultipartFile file,int month, int year,String branch, String vertical, 
			List<String> list, ModelMap model, HttpServletRequest request, BulkImportTask bulkImportTask, String ccmail) throws MasterGSTException {		
		final String method = "bulkImports::";
		logger.debug(CLASSNAME + method + BEGIN);
		OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
		if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableSalesFields())) {
			otherconfig.setEnableSalesFields(false);
		}
		if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnablePurFields())) {
			otherconfig.setEnablePurFields(false);
		}
		boolean hsnqtyrate = false;
		if(isNotEmpty(otherconfig)) {
			if("GSTR1".equals(returntype) || "Sales Register".equals(returntype) || "SalesRegister".equals(returntype)
					|| returntype.equalsIgnoreCase("FHPLTemplate"+MasterGSTConstants.GSTR1) || returntype.equalsIgnoreCase("mastergst_all_fileds"+MasterGSTConstants.GSTR1)) {
				hsnqtyrate = otherconfig.isEnableSalesFields();
			}else {
				hsnqtyrate = otherconfig.isEnablePurFields();
			}
		}
		boolean flag=false;
		FileSystemResource fileSystemResource=null;
		
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		response.setMonth(month);
		response.setYear(year);
		List<ImportSummary> summaryList = Lists.newArrayList();
		String task = "INTERRUPTED";
		User usr=userService.findById(id);
		boolean mailflag = false;
		boolean processflag = false;
		
		HttpSession session = null;
		if (!file.isEmpty()) {
			logger.debug(CLASSNAME + method + file.getOriginalFilename());
			String temptype = null;
			
			try {
				task = "COMPLETE";
				String xmlPath = "";
				if(returntype.equalsIgnoreCase("FHPLTemplate"+MasterGSTConstants.GSTR1)) {
					returntype = MasterGSTConstants.GSTR1;
					xmlPath = "classpath:mastergst_fhpl_excel_config.xml";
				}else {
					xmlPath = "classpath:mastergst_excel_config.xml";
					if(returntype.equalsIgnoreCase("mastergst_all_fileds"+MasterGSTConstants.GSTR1)) {
						returntype = MasterGSTConstants.GSTR1;
						temptype = "mastergst_all_fileds";
						xmlPath = "classpath:mastergst_all_fields_excel_config2.xml";
					}
				}
				if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)
						|| returntype.equals(MasterGSTConstants.GSTR2)) {
					xmlPath = "classpath:mastergst_purchase_excel_config.xml";
				}else if(returntype.equals(MasterGSTConstants.GSTR6)){
					xmlPath = "classpath:mastergst_gstr6_excel_config.xml";
				}else if(returntype.equals(MasterGSTConstants.GSTR4)){
					xmlPath = "classpath:mastergst_gstr4_excel_config.xml";
				}else if(returntype.equals(MasterGSTConstants.GSTR5)){
					xmlPath = "classpath:mastergst_gstr5_excel_config.xml";
				}
				Resource config = resourceLoader.getResource(xmlPath);
				DateConverter converter = new DateConverter();
				String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd" };
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
					Map<String, String> statesMap = newBulkImportService.getStateMap();
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
							}else if (key.equals("b2baList")) {//Amendment invoices key
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
							if (isNotEmpty(invoices)) {
								int index = 1;
								summary.setTotal(beans.get(key).size()-1);
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
										String invnum = "" , onvnum = "";
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
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(INVOICENO_REGEX, invnum.trim())) {
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
											
										}else if(isNotEmpty(invoice.getB2b()) 
												&& isNotEmpty(invoice.getB2b().get(0).getCtin())
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
										}else{
											if(hsnqtyrate) {
												if (returntype.equals(MasterGSTConstants.GSTR1)
														&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList") || key.equals("cdnraList") || key.equals("cdnuraList") || key.equals("exatList"))
														&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
																|| ((key.equals("cdnraList") || key.equals("creditList")) && (isEmpty(((GSTR1)invoice).getCdnr())
																		|| isEmpty(((GSTR1)invoice).getB2b().get(0).getCtin())
																		|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt())
																		/*|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getInum())
																		|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getIdt())*/
																		|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
																|| ((key.equals("cdnuraList") || key.equals("cdnurList")) && (isEmpty(((GSTR1)invoice).getCdnur()) || "Credit/Debit Note Date*".equals(invoice.getStrDate())
																		/*|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getInum())
																		|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getIdt())*/
																		|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getNtty())))
																|| ((key.equals("expaList") || key.equals("exportList")) && (isEmpty(invoice.getExp()) || "Invoice Date*".equals(invoice.getStrDate())
																		|| isEmpty(invoice.getExp().get(0).getExpTyp())))
																|| isEmpty(invoice.getStrDate())
																|| isEmpty(invoice.getItems()) 
																|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																		&& invoice.getItems().get(0).getIgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																		&& invoice.getItems().get(0).getCgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																		&& invoice.getItems().get(0).getSgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																		&& invoice.getItems().get(0).getCessrate() < 0))) {
													
														failed++;
														if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
																&& isNotEmpty(datatypeSheet.getRow(index))) {
															String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
															Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
															errorcell.setCellValue(errorVal);
															errorcell.setCellStyle(style);
															errorIndxes.add(index);
														}
												} else if (returntype.equals(MasterGSTConstants.GSTR4)
														&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
														&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
																|| (key.equals("creditList") && (isEmpty(((GSTR4)invoice).getCdnr())
																		|| isEmpty(((GSTR4)invoice).getB2b().get(0).getCtin())
																		|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt())
																		|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtNum())
																		|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
																|| (key.equals("cdnurList") && (isEmpty(((GSTR4)invoice).getCdnur())
																		|| isEmpty(((GSTR4)invoice).getCdnur().get(0).getNtNum())
																		|| isEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())))
																|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b())
																		|| isEmpty(invoice.getB2b().get(0).getCtin())))
																|| (key.equals("exportList") && (isEmpty(invoice.getExp())
																		|| isEmpty(invoice.getExp().get(0).getExpTyp())))
																|| isEmpty(invoice.getStrDate())
																|| isEmpty(invoice.getItems()) 
																|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																		&& invoice.getItems().get(0).getIgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																		&& invoice.getItems().get(0).getCgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																		&& invoice.getItems().get(0).getSgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																		&& invoice.getItems().get(0).getCessrate() < 0))) {
													datatypeSheet.getRow(index).createCell(lastcolumn).setCellValue("Please Enter Valid Values");
													
														failed++;
														if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
																&& isNotEmpty(datatypeSheet.getRow(index))) {
															errorIndxes.add(index);
														}
												}else if(key.equals("nilList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
														|| isEmpty(invoice.getItems()) 
														|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
														|| isEmpty(invoice.getItems().get(0).getType())
														|| isEmpty(invoice.getItems().get(0).getTotal())
														|| isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()))) {
													
														failed++;
														if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
																&& isNotEmpty(datatypeSheet.getRow(index))) {
															String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
															if(isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
																errorVal += "Please Enter Invoice Number,";
															}
															Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
															errorcell.setCellValue(errorVal);
															errorcell.setCellStyle(style);
															errorIndxes.add(index);
														}
												} else if(key.equals("itrvslList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
														|| isEmpty(invoice.getB2b().get(0).getCtin())
														|| isEmpty(invoice.getItems())
														|| isEmpty(invoice.getItems().get(0).getItcRevtype())
														|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
														|| (isNotEmpty(invoice.getItems().get(0).getIgstamount()) 
																&& invoice.getItems().get(0).getIgstamount() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getCgstamount()) 
																&& invoice.getItems().get(0).getCgstamount() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getSgstamount()) 
																&& invoice.getItems().get(0).getSgstamount() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getIsdcessamount()) 
																&& invoice.getItems().get(0).getIsdcessamount() < 0)
														|| isEmpty(invoice.getItems().get(0).getTotal()))) {
													failed++;
													if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
															&& isNotEmpty(datatypeSheet.getRow(index))) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(index);
													}
												}else if(key.equals("impgList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
													failed++;
													if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
															&& isNotEmpty(datatypeSheet.getRow(index))) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
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
												} else if(key.equals("impsList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
													failed++;
													if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
															&& isNotEmpty(datatypeSheet.getRow(index))) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(index);
													}
												}else if ((key.equals("advAdjustedList") || key.equals("txpaList"))&& (isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
													
													failed++;
													if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
															&& isNotEmpty(datatypeSheet.getRow(index))) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(index);
													}
												}else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("itrvslList") && ((isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
																|| isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems())
																		&& ((isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																						&& invoice.getItems().get(0).getIgstrate() < 0)
																				|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																						&& invoice.getItems().get(0).getCgstrate() < 0)
																				|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																						&& invoice.getItems().get(0).getSgstrate() < 0)
																				|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																						&& invoice.getItems().get(0).getCessrate() < 0)))))
																|| ((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)
																		|| returntype.equals(MasterGSTConstants.GSTR2))
																		&& key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getCtin())  || isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()))) {
													
														failed++;
														if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
																&& isNotEmpty(datatypeSheet.getRow(index))) {
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
															Cell errors = datatypeSheet.getRow(index).createCell(lastcolumn);
															errors.setCellValue(errorVal);
															errors.setCellStyle(style);
															errorIndxes.add(index);
														}
													
												} else {
													invoice = newBulkImportService.importInvoicesHsnQtyRateMandatory(invoice,returntype,clientid,branch,vertical,month,year,patterns, statesMap);
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
											}else {
												if (returntype.equals(MasterGSTConstants.GSTR1)
														&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList") || key.equals("cdnraList") || key.equals("cdnuraList") || key.equals("expaList"))
														&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
																|| ((key.equals("cdnraList") || key.equals("creditList")) && (isEmpty(((GSTR1)invoice).getCdnr())
																		|| isEmpty(((GSTR1)invoice).getB2b().get(0).getCtin())
																		|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt())
																		|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
																|| ((key.equals("cdnurList") || key.equals("cdnuraList")) && (isEmpty(((GSTR1)invoice).getCdnur())
																		|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getNtty())))
																|| ((key.equals("exportList") || key.equals("expaList")) && (isEmpty(invoice.getExp())
																		|| isEmpty(invoice.getExp().get(0).getExpTyp())))
																|| isEmpty(invoice.getStrDate())
																|| isEmpty(invoice.getItems()) || isEmpty(invoice.getItems().get(0).getHsn())
																|| isEmpty(invoice.getItems().get(0).getQuantity())
																|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																		&& invoice.getItems().get(0).getIgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																		&& invoice.getItems().get(0).getCgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																		&& invoice.getItems().get(0).getSgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																		&& invoice.getItems().get(0).getCessrate() < 0))) {
													failed++;
													if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
															&& isNotEmpty(datatypeSheet.getRow(index))) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(index);
													}
												} else if (returntype.equals(MasterGSTConstants.GSTR4)
														&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
														&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
																|| (key.equals("creditList") && (isEmpty(((GSTR4)invoice).getCdnr())
																		|| isEmpty(((GSTR4)invoice).getB2b().get(0).getCtin())
																		|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt())
																		|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtNum())
																		|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
																|| (key.equals("cdnurList") && (isEmpty(((GSTR4)invoice).getCdnur())
																		|| isEmpty(((GSTR4)invoice).getCdnur().get(0).getNtNum())
																		|| isEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())))
																|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b())
																		|| isEmpty(invoice.getB2b().get(0).getCtin())))
																|| (key.equals("exportList") && (isEmpty(invoice.getExp())
																		|| isEmpty(invoice.getExp().get(0).getExpTyp())))
																|| isEmpty(invoice.getStrDate())
																|| isEmpty(invoice.getItems()) || isEmpty(invoice.getItems().get(0).getHsn())
																|| isEmpty(invoice.getItems().get(0).getQuantity())
																|| (invoice.getItems().get(0).getQuantity() <= 0)
																|| (isNotEmpty(invoice.getItems().get(0).getRateperitem()) 
																		&& invoice.getItems().get(0).getRateperitem() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																		&& invoice.getItems().get(0).getIgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																		&& invoice.getItems().get(0).getCgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																		&& invoice.getItems().get(0).getSgstrate() < 0)
																|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																		&& invoice.getItems().get(0).getCessrate() < 0))) {
													failed++;
													if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
															&& isNotEmpty(datatypeSheet.getRow(index))) {
														datatypeSheet.getRow(index).createCell(lastcolumn).setCellValue("Please Enter Valid Values");
														errorIndxes.add(index);
													}
												}else if(key.equals("nilList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
														|| isEmpty(invoice.getItems()) || isEmpty(invoice.getItems().get(0).getHsn()) 
														|| isEmpty(invoice.getItems().get(0).getUqc())
														|| isEmpty(invoice.getItems().get(0).getQuantity())
														|| isEmpty(invoice.getItems().get(0).getRateperitem())
														|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
														|| isEmpty(invoice.getItems().get(0).getType())
														|| isEmpty(invoice.getItems().get(0).getTotal())
														|| isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()))) {
													failed++;
													if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
															&& isNotEmpty(datatypeSheet.getRow(index))) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														if(isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
															errorVal += "Please Enter Invoice Number,";
														}
														Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(index);
													}
												} else if(key.equals("itrvslList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
														|| isEmpty(invoice.getB2b().get(0).getCtin())
														|| isEmpty(invoice.getItems())
														|| isEmpty(invoice.getItems().get(0).getItcRevtype())
														|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
														|| (isNotEmpty(invoice.getItems().get(0).getIgstamount()) 
																&& invoice.getItems().get(0).getIgstamount() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getCgstamount()) 
																&& invoice.getItems().get(0).getCgstamount() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getSgstamount()) 
																&& invoice.getItems().get(0).getSgstamount() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getIsdcessamount()) 
																&& invoice.getItems().get(0).getIsdcessamount() < 0)
														|| isEmpty(invoice.getItems().get(0).getTotal()))) {
													failed++;
													if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
															&& isNotEmpty(datatypeSheet.getRow(index))) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(index);
													}
												}else if(key.equals("impgList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()))) {
													failed++;
													if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
															&& isNotEmpty(datatypeSheet.getRow(index))) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
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
												}else if((key.equals("advAdjustedList") || key.equals("txpaList"))&& (isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
													failed++;
													if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
															&& isNotEmpty(datatypeSheet.getRow(index))) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(index);
													}
												}else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("itrvslList") && ((isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
																|| isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems())
																		&& (isEmpty(invoice.getItems().get(0).getHsn())
																				|| isEmpty(invoice.getItems().get(0).getQuantity())
																				|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																						&& invoice.getItems().get(0).getIgstrate() < 0)
																				|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																						&& invoice.getItems().get(0).getCgstrate() < 0)
																				|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																						&& invoice.getItems().get(0).getSgstrate() < 0)
																				|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																						&& invoice.getItems().get(0).getCessrate() < 0)))))
																|| ((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)
																		|| returntype.equals(MasterGSTConstants.GSTR2))
																		&& key.equals("invoiceList") && isEmpty(invoice.getB2b().get(0).getCtin()) && isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()))) {
													failed++;
													if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
															&& isNotEmpty(datatypeSheet.getRow(index))) {
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
													invoice = newBulkImportService.importInvoicesHsnQtyRateMandatory(invoice,returntype,clientid,branch,vertical,month,year,patterns, statesMap);
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
													
													if(isEmpty(invoice.getIncludetax())) {
														invoice.setIncludetax("No");
													}
													
													if(isNotEmpty(temptype) && "mastergst_all_fileds".equalsIgnoreCase(temptype)){

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
									if(failed != 0) {
										failed = failed-1;
									}
									summary.setSuccess((beans.get(key).size()-1) - (failed));
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
					response.setSummaryList(summaryList);
					if (isNotEmpty(summaryList)) {
						int failedCount = 0;
						for (ImportSummary summary : summaryList) {
							if (summary.getInvfailed() > 0) {
								failedCount += summary.getInvfailed();
							}
						}
						if (failedCount > 0 && isNotEmpty(convFile)) {
							session = request.getSession(false);
							if (isNotEmpty(session)) {
								session.setAttribute("bulkerror.xls", convFile);
							}
							System.out.println("error file created...");
							flag=true;
							response.setShowLink(true);
							fileSystemResource=new FileSystemResource(convFile);
						} else {
							try {
								convFile.delete();
							} catch (Exception e) {
							}
						}
					}
					String hsnFp = null;
					String hsnfinancialYear = null, hsnmonth = null;
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
					processflag = true;
					newBulkImportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, MASTERGST_EXCEL_TEMPLATE,otherconfig);
				} else {
					String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
					task = "INTERRUPTED";
					response.setError(errMsg);
					response.setShowLink(true);
				}
			} catch (Exception e) {
				task = "INTERRUPTED";
				//String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
				
				String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters/ File are Corrupted, please download latest template and re-import invoices.";
				
				bulkImportTask.setTask(task);
				response.setShowLink(true);
				response.setError(errMsg);
				List<ImportSummary> summary = null;
				
				if(processflag) {
					summary = new ArrayList<ImportSummary>();
					if(isNotEmpty(response.getSummaryList())) {
						summary=response.getSummaryList();
					}
				}
				
				bulkImportTask.setResponse(response);
				bulkImportTaskService.createBulkImportTask(bulkImportTask);
				try {
					fileSystemResource = new FileSystemResource(convert(file));
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				sendBulkImportMails(true, usr, ccmail, fullname, summary, response, fileSystemResource);
				session = request.getSession(false);
				if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulkerror.xls"))) {
					File errFile = (File) session.getAttribute("bulkerror.xls");
					try {
						errFile.delete();
					} catch (Exception ex) {}
					session.removeAttribute("bulkerror.xls");
				}
				mailflag = true;
				logger.error(CLASSNAME + method + " ERROR", e);
			}
		}
		
		List<ImportSummary> summary=new ArrayList<ImportSummary>();
		if(isNotEmpty(response.getSummaryList())) {
			summary=response.getSummaryList();
		}
		bulkImportTask.setTask(task);
		bulkImportTask.setResponse(response);
		bulkImportTaskService.createBulkImportTask(bulkImportTask);
		if(!mailflag){
			sendBulkImportMails(flag, usr, ccmail, fullname, summary, response, fileSystemResource);
			session = request.getSession(false);
			if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulkerror.xls"))) {
				File errFile = (File) session.getAttribute("bulkerror.xls");
				try {
					errFile.delete();
				} catch (Exception e) {}
				session.removeAttribute("bulkerror.xls");
			}
		}
	}
	
	public File convert(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.flush();
		fos.close();
		return convFile;
	}
	
	@Async
	public void sendBulkImportMails(boolean flag, User usr, String cc, String fullname, List<ImportSummary> summary, ImportResponse response,FileSystemResource fileSystemResource) throws MasterGSTException {
		if(flag) {
			if(isNotEmpty(usr.getEmail())) {
				emailService.sendBulkImportEmail(usr.getEmail(), cc, VmUtil.velocityTemplate("email_bulkimport.vm", fullname, summary, response), "MasterGST Bulk Import Reports",fileSystemResource.getFile());		
			}
		}else {
			if(isNotEmpty(usr.getEmail())) {
				emailService.sendBulkImportEmail(usr.getEmail(), cc, VmUtil.velocityTemplate("email_bulkimport.vm", fullname, summary, response), "MasterGST Bulk Import Reports", null);		
			}			
		}	
	}
	
	
	@Async
	public void customBulkImports(String id, String fullname, String clientid, String returntype, MultipartFile file,int month, int year,String branch, String vertical, 
			List<String> list,String hsnFp, ModelMap model, HttpServletRequest request, BulkImportTask bulkImportTask, String ccmail) throws MasterGSTException {		
		final String method = "customBulkImports::";
		logger.debug(CLASSNAME + method + BEGIN);
		OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
		if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableSalesFields())) {
			otherconfig.setEnableSalesFields(false);
		}
		if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnablePurFields())) {
			otherconfig.setEnablePurFields(false);
		}
		TemplateMapperDoc templateMapperDoc = importMapperService.getMapperDoc(id, clientid,returntype);
		TemplateMapper templateMapper = importMapperService.getMapper(id, clientid,returntype);
		
		String simplifiedOrDetail = "Detailed";
		
		String invReturnType = MasterGSTConstants.GSTR1;
		if(isNotEmpty(templateMapper.getMapperType())) {
			if("Sales".equalsIgnoreCase(templateMapper.getMapperType()) || "einvoice".equalsIgnoreCase(templateMapper.getMapperType())) {
				invReturnType = MasterGSTConstants.GSTR1;
			}else {
				invReturnType = MasterGSTConstants.PURCHASE_REGISTER;
			}
		}

		boolean hsnqtyrate = false;
		if(isNotEmpty(otherconfig)) {
			if("GSTR1".equals(invReturnType) || "Sales Register".equals(invReturnType) || "SalesRegister".equals(invReturnType)) {
				hsnqtyrate = otherconfig.isEnableSalesFields();
			}else {
				hsnqtyrate = otherconfig.isEnablePurFields();
			}
		}
		
		if (isNotEmpty(templateMapper) && isNotEmpty(templateMapper.getSimplifiedOrDetail())) {
			simplifiedOrDetail = templateMapper.getSimplifiedOrDetail();
		}
		if("einvoice".equalsIgnoreCase(templateMapper.getMapperType())){
			if ("Simplified".equalsIgnoreCase(simplifiedOrDetail)) {
				customEinvoiceBulkImportSimplified(id, clientid, file, invReturnType, month, year, list,
						fullname, templateMapper, templateMapperDoc, hsnqtyrate,otherconfig,branch,vertical, bulkImportTask, ccmail, request);
			} else {
				customEinvoiceBulkImportDetailed(id, clientid, file, invReturnType, month, year, list, fullname,
						templateMapper, templateMapperDoc, hsnqtyrate,otherconfig,branch,vertical, bulkImportTask, ccmail, request);
			}
		}else {
			if ("Simplified".equalsIgnoreCase(simplifiedOrDetail)) {
				customBulkImportSimplified(id, clientid, file, invReturnType, month, year, list,
						fullname, templateMapper, templateMapperDoc, hsnqtyrate,otherconfig,branch,vertical, bulkImportTask,hsnFp, ccmail, request);
			} else {
				customBulkImportDetailed(id, clientid, file, invReturnType, month, year, list, fullname,
						templateMapper, templateMapperDoc, hsnqtyrate,otherconfig,branch,vertical, bulkImportTask,hsnFp, ccmail, request);
			}
		}
		
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
		List<InvoiceParent> docSummaryList = Lists.newArrayList();
		
		List<InvoiceParent> b2baList = Lists.newArrayList();
		List<InvoiceParent> b2csaList = Lists.newArrayList();
		List<InvoiceParent> b2claList = Lists.newArrayList();
		List<InvoiceParent> cdnraList = Lists.newArrayList();
		List<InvoiceParent> cdnuraList = Lists.newArrayList();
		List<InvoiceParent> expaList = Lists.newArrayList();
		List<InvoiceParent> ataList = Lists.newArrayList();
		List<InvoiceParent> txpaList = Lists.newArrayList();
		
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
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}

	@RequestMapping(value = "/bulkimports/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String bulkImports(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year,@RequestParam(value="type",required=false) String type, 
			 ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "obtainMapperPage::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userRepository.findById(id);
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
		model.addAttribute("user", user);
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		//bulkImportTaskService.getBulkImportTask(id,clientid).stream().filter(bulk->bulk.getTask() =="Pending");
		model.addAttribute("bulktask", "");
		bulkImportTaskService.getBulkImportTask(id,clientid).forEach(bulk->{
			if(bulk.getTask().equalsIgnoreCase("Pending")){
				model.addAttribute("bulktask", "pending");
			}
		});
		
		List<BulkImportTask> task=bulkImportTaskService.getBulkImportTask(id,clientid);
		task.forEach(bulk->{
			if(isNotEmpty(bulk.getResponse())) {
				bulk.setImported(0l);
				bulk.setFailure(0l);
				bulk.getResponse().getSummaryList().forEach(res->{
					//System.out.println(res.getSuccess()+"\t"+res.getFailed()+"\t"+res.getName());
					bulk.setImported(bulk.getImported()+res.getSuccess());
					bulk.setFailure(bulk.getFailure()+res.getFailed());
				});				
			}
		});
		model.addAttribute("bulkimport",task);
		//model.addAttribute("bulkimport", bulkImportTaskService.getBulkImportTask(id,clientid));
		
		logger.debug(CLASSNAME + method + END);
		return "mapper/bulkImports";
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
	
	
	private void customBulkImportDetailed(String id, String clientid, MultipartFile file,
			String returntype, int month, int year, List<String> list, String fullname, TemplateMapper templateMapper,
			TemplateMapperDoc templateMapperDoc, boolean hsnqtyrate,OtherConfigurations otherconfig,String branch,String vertical,  BulkImportTask bulkImportTask,String hsnFp, String ccmail, HttpServletRequest request) throws MasterGSTException {

		final String method = "customBulkImportDetailed::";
		boolean flag=false;
		FileSystemResource fileSystemResource=null;
		Client client = clientService.findById(clientid);
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		User usr=userService.findById(id);
		String task = "INTERRUPTED";
		boolean mailflag = false;
		boolean processflag = false;
		HttpSession session = null;
		
		int skipRows = 1;
		if(isNotEmpty(templateMapper.getSkipRows())) {
			skipRows = Integer.parseInt(templateMapper.getSkipRows());
		}
		Map<String, String> mapperConfig = Maps.newHashMap();
		
		if(NullUtil.isNotEmpty(templateMapper.getMapperConfig())){
			for(String key : templateMapper.getMapperConfig().keySet()) {
				
				if(NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key))) {
					String page = (String)templateMapper.getMapperConfig().get(key).get("page");
					mapperConfig.put(key, page);
				}
			}
		}
		if (!file.isEmpty()) {
			logger.debug(CLASSNAME + method + file.getOriginalFilename());
			try {
				task = "COMPLETE";
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
				logger.info(CLASSNAME + method + "Status: {}", status.isStatusOK());
				HSNDetails hsnSummary = new HSNDetails();
				GSTR1DocumentIssue documentIssue = new GSTR1DocumentIssue();
				if (status.isStatusOK()) {
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
					Map<String, String> statesMap = newBulkImportService.getStateMap();
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
						    	if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
						    		WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, i, i);
								}else {
									WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError,i, i);
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
										}else  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
											if(vindex != skipRows - 1) {
												Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
												gstincell.setCellValue("Invalid GSTIN Number");
												gstincell.setCellStyle(style);
												errorIndxes.add(vindex);
											}
										}
									} else{
										if(hsnqtyrate) {
											if (returntype.equals(MasterGSTConstants.GSTR1)
													&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| (key.equals("creditList") && (isEmpty(((GSTR1)invoice).getCdnr())
																	|| isEmpty(((GSTR1)invoice).getB2b().get(0).getCtin())
																	|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt())
																	|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList") && (isEmpty(((GSTR1)invoice).getCdnur())
															|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp())
																	|| isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems()) 
															|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																	&& invoice.getItems().get(0).getIgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																	&& invoice.getItems().get(0).getCgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																	&& invoice.getItems().get(0).getSgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																	&& invoice.getItems().get(0).getCessrate() < 0))) {
												
													failed++;
													if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
														if(vindex != skipRows - 1) {
															String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
															Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
															errorcell.setCellValue(errorVal);
															errorcell.setCellStyle(style);
															errorIndxes.add(vindex);
														}
													}
											} else if (returntype.equals(MasterGSTConstants.GSTR4)
													&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| (key.equals("creditList") && (isEmpty(((GSTR4)invoice).getCdnr())
																	|| isEmpty(((GSTR4)invoice).getB2b().get(0).getCtin())
																	|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt())
																	|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtNum())
																	|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList") && (isEmpty(((GSTR4)invoice).getCdnur())
																	|| isEmpty(((GSTR4)invoice).getCdnur().get(0).getNtNum())
																	|| isEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b())
																	|| isEmpty(invoice.getB2b().get(0).getCtin())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp())
																	|| isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems()) 
															|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																	&& invoice.getItems().get(0).getIgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																	&& invoice.getItems().get(0).getCgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																	&& invoice.getItems().get(0).getSgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																	&& invoice.getItems().get(0).getCessrate() < 0))) {
												
													failed++;
													if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
														if(vindex != skipRows - 1) {
															errorIndxes.add(vindex);
														}
													}
											}else if(key.equals("nilList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| isEmpty(invoice.getItems()) 
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| isEmpty(invoice.getItems().get(0).getType())
													|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												
													failed++;
													if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
														if(vindex != skipRows - 1) {
															String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
															Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
															errorcell.setCellValue(errorVal);
															errorcell.setCellStyle(style);
															errorIndxes.add(vindex);
														}
													}
											} else if(key.equals("itrvslList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| isEmpty(invoice.getB2b().get(0).getCtin())
													|| isEmpty(invoice.getItems())
													|| isEmpty(invoice.getItems().get(0).getItcRevtype())
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| (isNotEmpty(invoice.getItems().get(0).getIgstamount()) 
															&& invoice.getItems().get(0).getIgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCgstamount()) 
															&& invoice.getItems().get(0).getCgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getSgstamount()) 
															&& invoice.getItems().get(0).getSgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getIsdcessamount()) 
															&& invoice.getItems().get(0).getIsdcessamount() < 0)
													|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if(key.equals("impgList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if (key.equals("impgList") && (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Invoice Number Format");
														gstincell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if(key.equals("impsList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if(key.equals("advAdjustedList") && (isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("itrvslList") && ((isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems())
																	&& ((isNotEmpty(invoice.getItems().get(0).getIgstrate()) && invoice.getItems().get(0).getIgstrate() < 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) && invoice.getItems().get(0).getCgstrate() < 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) && invoice.getItems().get(0).getSgstrate() < 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) && invoice.getItems().get(0).getCessrate() < 0)))))
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
												invoice = newBulkImportService.importInvoicesHsnQtyRateMandatory(invoice,returntype,clientid,branch,vertical,month,year,patterns, statesMap);												if(key.equals("invoiceList") || key.equals("exportList") || key.equals("impsList") || key.equals("impgList")) {
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
										}else {
											if (returntype.equals(MasterGSTConstants.GSTR1)
													&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| (key.equals("creditList") && (isEmpty(((GSTR1)invoice).getCdnr())
																	|| isEmpty(((GSTR1)invoice).getB2b().get(0).getCtin())
																	|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt())
																	|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList") && (isEmpty(((GSTR1)invoice).getCdnur())
															|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp())
																	|| isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems()) || isEmpty(invoice.getItems().get(0).getHsn())
															|| isEmpty(invoice.getItems().get(0).getQuantity())
															|| (invoice.getItems().get(0).getQuantity() <= 0)
															|| (isNotEmpty(invoice.getItems().get(0).getRateperitem()) 
																	&& invoice.getItems().get(0).getRateperitem() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																	&& invoice.getItems().get(0).getIgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																	&& invoice.getItems().get(0).getCgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																	&& invoice.getItems().get(0).getSgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																	&& invoice.getItems().get(0).getCessrate() < 0))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (returntype.equals(MasterGSTConstants.GSTR4)
													&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| (key.equals("creditList") && (isEmpty(((GSTR4)invoice).getCdnr())
																	|| isEmpty(((GSTR4)invoice).getB2b().get(0).getCtin())
																	|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt())
																	|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtNum())
																	|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList") && (isEmpty(((GSTR4)invoice).getCdnur())
																	|| isEmpty(((GSTR4)invoice).getCdnur().get(0).getNtNum())
																	|| isEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b())
																	|| isEmpty(invoice.getB2b().get(0).getCtin())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp())
																	|| isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems()) || isEmpty(invoice.getItems().get(0).getHsn())
															|| isEmpty(invoice.getItems().get(0).getQuantity())
															|| (invoice.getItems().get(0).getQuantity() <= 0)
															|| (isNotEmpty(invoice.getItems().get(0).getRateperitem()) 
																	&& invoice.getItems().get(0).getRateperitem() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																	&& invoice.getItems().get(0).getIgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																	&& invoice.getItems().get(0).getCgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																	&& invoice.getItems().get(0).getSgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																	&& invoice.getItems().get(0).getCessrate() < 0))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														errorIndxes.add(vindex);
													}
												}
											}else if(key.equals("nilList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| isEmpty(invoice.getItems()) || isEmpty(invoice.getItems().get(0).getHsn()) 
													|| isEmpty(invoice.getItems().get(0).getUqc())
													|| isEmpty(invoice.getItems().get(0).getQuantity())
													|| isEmpty(invoice.getItems().get(0).getRateperitem())
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| isEmpty(invoice.getItems().get(0).getType())
													|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if(key.equals("itrvslList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| isEmpty(invoice.getB2b().get(0).getCtin())
													|| isEmpty(invoice.getItems())
													|| isEmpty(invoice.getItems().get(0).getItcRevtype())
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| (isNotEmpty(invoice.getItems().get(0).getIgstamount()) 
															&& invoice.getItems().get(0).getIgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCgstamount()) 
															&& invoice.getItems().get(0).getCgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getSgstamount()) 
															&& invoice.getItems().get(0).getSgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getIsdcessamount()) 
															&& invoice.getItems().get(0).getIsdcessamount() < 0)
													|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if(key.equals("impgList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if (key.equals("impgList") && (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Invoice Number Format");
														gstincell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if(key.equals("advAdjustedList") && (isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("itrvslList") && ((isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems())
																	&& (isEmpty(invoice.getItems().get(0).getHsn())
																			|| isEmpty(invoice.getItems().get(0).getQuantity())
																			|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																					&& invoice.getItems().get(0).getIgstrate() < 0)
																			|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																					&& invoice.getItems().get(0).getCgstrate() < 0)
																			|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																					&& invoice.getItems().get(0).getSgstrate() < 0)
																			|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																					&& invoice.getItems().get(0).getCessrate() < 0)))))
															|| ((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)
																	|| returntype.equals(MasterGSTConstants.GSTR2))
																	&& key.equals("invoiceList") && (isEmpty(invoice.getB2b().get(0).getCtin()) || isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())))) {
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
												invoice = newBulkImportService.importInvoicesHsnQtyRateMandatory(invoice,returntype,clientid,branch,vertical,month,year,patterns, statesMap);
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
							session = request.getSession(false);
							if (isNotEmpty(session)) {
								session.setAttribute("custom-bulkimportdetailed_error.xls", convFile);
								flag=true;
								response.setShowLink(true);
								fileSystemResource=new FileSystemResource(convFile);
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
					String invReturnType = MasterGSTConstants.GSTR1;
					if(isNotEmpty(templateMapper.getMapperType())) {
						if("Sales".equalsIgnoreCase(templateMapper.getMapperType())) {
							invReturnType = MasterGSTConstants.GSTR1;
						}else {
							invReturnType = MasterGSTConstants.PURCHASE_REGISTER;
						}
					}
					processflag = true;
					newBulkImportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, EXCEL_TEMPLATE,otherconfig);
				} else {
					String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
					task = "INTERRUPTED";
					response.setError(errMsg);
					response.setShowLink(true);
					response.setError(errMsg);
				}
			} catch (Exception e) {
				
				task = "INTERRUPTED";
				String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters/ File are Corrupted, please download latest template and re-import invoices.";
				
				bulkImportTask.setTask(task);
				response.setShowLink(true);
				response.setError(errMsg);
				List<ImportSummary> summary = null;
				
				if(processflag) {
					summary = new ArrayList<ImportSummary>();
					if(isNotEmpty(response.getSummaryList())) {
						summary=response.getSummaryList();
					}
				}
				
				bulkImportTask.setResponse(response);
				bulkImportTaskService.createBulkImportTask(bulkImportTask);
				try {
					fileSystemResource = new FileSystemResource(convert(file));
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				sendBulkImportMails(true, usr, ccmail, fullname, summary, response, fileSystemResource);
				session = request.getSession(false);
				if (isNotEmpty(session) && isNotEmpty(session.getAttribute("custom-bulkimportdetailed_error.xls"))) {
					File errFile = (File) session.getAttribute("custom-bulkimportdetailed_error.xls");
					try {
						errFile.delete();
					} catch (Exception ex) {}
					session.removeAttribute("custom-bulkimportdetailed_error.xls");
				}
				mailflag = true;
				response.setError(errMsg);
				logger.error(CLASSNAME + method + " ERROR", e);
			}
		}
		
		List<ImportSummary> summary=new ArrayList<ImportSummary>();
		if(isNotEmpty(response.getSummaryList())) {
			summary=response.getSummaryList();
		}
		bulkImportTask.setTask(task);
		bulkImportTask.setResponse(response);
		bulkImportTaskService.createBulkImportTask(bulkImportTask);
		/*if(flag) {
			if(isNotEmpty(usr.getEmail())) {
				emailService.sendBulkImportEmail(usr.getEmail(), VmUtil.velocityTemplate("email_bulkimport.vm", fullname, summary, response), "MasterGST Bulk Import Reports",fileSystemResource.getFile());		
			}
		}else {
			if(isNotEmpty(usr.getEmail())) {
				emailService.sendEnrollEmail(usr.getEmail(), VmUtil.velocityTemplate("email_bulkimport.vm", fullname, summary, response), "MasterGST Bulk Import Reports");		
			}			
		}*/
		
		if(!mailflag){
			sendBulkImportMails(flag, usr, ccmail, fullname, summary, response, fileSystemResource);
			session = request.getSession(false);
			if (isNotEmpty(session) && isNotEmpty(session.getAttribute("custom-bulkimportdetailed_error.xls"))) {
				File errFile = (File) session.getAttribute("custom-bulkimportdetailed_error.xls");
				try {
					errFile.delete();
				} catch (Exception e) {}
				session.removeAttribute("custom-bulkimportdetailed_error.xls");
			}
		}
	}
	
	private void customBulkImportSimplified(String id, String clientid, MultipartFile file,
			String returntype, int month, int year, List<String> list, String fullname, TemplateMapper templateMapper,
			TemplateMapperDoc templateMapperDoc, boolean hsnqtyrate,OtherConfigurations otherconfig,String branch,String vertical,BulkImportTask bulkImportTask,String hsnFp, String ccmail, HttpServletRequest request) throws MasterGSTException {

		final String method = "customBulkImportSimplified::";
		Client client = clientService.findById(clientid);
		String stateName = client.getStatename();
		boolean flag=false;
		FileSystemResource fileSystemResource=null;
		
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		String task = "INTERRUPTED";
		User usr=userService.findById(id);
		
		boolean mailflag = false;
		boolean processflag = false;
		
		HttpSession session = null;
		int skipRows = 1;
		if(isNotEmpty(templateMapper.getSkipRows())) {
			skipRows = Integer.parseInt(templateMapper.getSkipRows());
		}
		Map<String, String> mapperConfig = Maps.newHashMap();
		
		if(NullUtil.isNotEmpty(templateMapper.getMapperConfig())){
			for(String key : templateMapper.getMapperConfig().keySet()) {
				
				if(NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key))) {
					String page = (String)templateMapper.getMapperConfig().get(key).get("page");
					mapperConfig.put(key, page);
				}
			}
		}
		if (!file.isEmpty()) {
			logger.debug(CLASSNAME + method + file.getOriginalFilename());
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
				logger.debug(CLASSNAME + method + "Status: {}", status.isStatusOK());
				HSNDetails hsnSummary = new HSNDetails();
				GSTR1DocumentIssue documentIssue = new GSTR1DocumentIssue();
				if (status.isStatusOK()) {
					Map<String, String> statesMap = newBulkImportService.getStateMap();
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
							} else if (key.equals("hsnSummaryList")) {
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
									WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError,i, i);
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
	
										}else if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
												if(vindex != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
													gstincell.setCellValue("Invalid GSTIN Number");
													gstincell.setCellStyle(style);
													errorIndxes.add(vindex);
												}
											}
									} else{
										if(hsnqtyrate) {
											if (returntype.equals(MasterGSTConstants.GSTR1)
													&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| (key.equals("creditList") && (isEmpty(((GSTR1)invoice).getCdnr())
																	|| isEmpty(((GSTR1)invoice).getB2b().get(0).getCtin())
																	|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt())
																	|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList") && (isEmpty(((GSTR1)invoice).getCdnur())
															|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp())
																	|| isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems()) 
															|| (isNotEmpty(invoice.getItems().get(0).getRate()) 
																	&& invoice.getItems().get(0).getRate() < 0))) {
												
													failed++;
													if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
														if(vindex != skipRows - 1) {
															String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
															Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
															errorcell.setCellValue(errorVal);
															errorcell.setCellStyle(style);
															errorIndxes.add(vindex);
														}
													}
											} else if (returntype.equals(MasterGSTConstants.GSTR4)
													&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| (key.equals("creditList") && (isEmpty(((GSTR4)invoice).getCdnr())
																	|| isEmpty(((GSTR4)invoice).getB2b().get(0).getCtin())
																	|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt())
																	|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtNum())
																	|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList") && (isEmpty(((GSTR4)invoice).getCdnur())
																	|| isEmpty(((GSTR4)invoice).getCdnur().get(0).getNtNum())
																	|| isEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b())
																	|| isEmpty(invoice.getB2b().get(0).getCtin())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp())
																	|| isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems()) 
															|| (isNotEmpty(invoice.getItems().get(0).getRate()) 
																	&& invoice.getItems().get(0).getRate() < 0))) {
												
													failed++;
													if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
														if(vindex != skipRows - 1) {
															errorIndxes.add(vindex);
														}
													}
											}else if(key.equals("nilList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| isEmpty(invoice.getItems()) 
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| isEmpty(invoice.getItems().get(0).getType())
													|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												
													failed++;
													if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
														if(vindex != skipRows - 1) {
															String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
															Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
															errorcell.setCellValue(errorVal);
															errorcell.setCellStyle(style);
															errorIndxes.add(vindex);
														}
													}
												
											} else if(key.equals("itrvslList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| isEmpty(invoice.getB2b().get(0).getCtin())
													|| isEmpty(invoice.getItems())
													|| isEmpty(invoice.getItems().get(0).getItcRevtype())
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| (isNotEmpty(invoice.getItems().get(0).getIgstamount()) 
															&& invoice.getItems().get(0).getIgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCgstamount()) 
															&& invoice.getItems().get(0).getCgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getSgstamount()) 
															&& invoice.getItems().get(0).getSgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getIsdcessamount()) 
															&& invoice.getItems().get(0).getIsdcessamount() < 0)
													|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if(key.equals("impgList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if (key.equals("impgList") && (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Invoice Number Format");
														gstincell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if(key.equals("impsList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if(key.equals("advAdjustedList") && (isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("itrvslList") && ((isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems())
																	&& ((isNotEmpty(invoice.getItems().get(0).getRate()) 
																					&& invoice.getItems().get(0).getRate() < 0)))))
															|| ((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)
																	|| returntype.equals(MasterGSTConstants.GSTR2))
																	&& key.equals("invoiceList") && (isEmpty(invoice.getB2b().get(0).getCtin()) || isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())))) {
												
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
												invoice = newBulkImportService.importInvoicesSimplified(invoice, client,returntype,branch,vertical,month,year,patterns,statesMap);
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
										}else {
											if (returntype.equals(MasterGSTConstants.GSTR1)
													&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| (key.equals("creditList") && (isEmpty(((GSTR1)invoice).getCdnr())
																	|| isEmpty(((GSTR1)invoice).getB2b().get(0).getCtin())
																	|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt())
																	|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList") && (isEmpty(((GSTR1)invoice).getCdnur())
															|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp())
																	|| isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems()) || isEmpty(invoice.getItems().get(0).getHsn())
															|| isEmpty(invoice.getItems().get(0).getQuantity())
															|| (isNotEmpty(invoice.getItems().get(0).getRate()) 
																	&& invoice.getItems().get(0).getRate() < 0))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell advAdjustError = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														advAdjustError.setCellValue(errorVal);
														advAdjustError.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (returntype.equals(MasterGSTConstants.GSTR4)
													&& (key.equals("invoiceList") || key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| (key.equals("creditList") && (isEmpty(((GSTR4)invoice).getCdnr())
																	|| isEmpty(((GSTR4)invoice).getB2b().get(0).getCtin())
																	|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt())
																	|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtNum())
																	|| isEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
															|| (key.equals("cdnurList") && (isEmpty(((GSTR4)invoice).getCdnur())
																	|| isEmpty(((GSTR4)invoice).getCdnur().get(0).getNtNum())
																	|| isEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())))
															|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b())
																	|| isEmpty(invoice.getB2b().get(0).getCtin())))
															|| (key.equals("exportList") && (isEmpty(invoice.getExp())
																	|| isEmpty(invoice.getExp().get(0).getExpTyp())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems()) || isEmpty(invoice.getItems().get(0).getHsn())
															|| isEmpty(invoice.getItems().get(0).getQuantity())
															|| (invoice.getItems().get(0).getQuantity() <= 0)
															|| (isNotEmpty(invoice.getItems().get(0).getRateperitem()) 
																	&& invoice.getItems().get(0).getRateperitem() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getRate()) 
																	&& invoice.getItems().get(0).getRate() < 0))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														errorIndxes.add(vindex);
													}
												}
											}else if(key.equals("nilList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| isEmpty(invoice.getItems()) || isEmpty(invoice.getItems().get(0).getHsn()) 
													|| isEmpty(invoice.getItems().get(0).getUqc())
													|| isEmpty(invoice.getItems().get(0).getQuantity())
													|| isEmpty(invoice.getItems().get(0).getRateperitem())
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| isEmpty(invoice.getItems().get(0).getType())
													|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell advAdjustError = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														advAdjustError.setCellValue(errorVal);
														advAdjustError.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if(key.equals("itrvslList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| isEmpty(invoice.getB2b().get(0).getCtin())
													|| isEmpty(invoice.getItems())
													|| isEmpty(invoice.getItems().get(0).getItcRevtype())
													|| isEmpty(invoice.getItems().get(0).getTaxablevalue())
													|| (isNotEmpty(invoice.getItems().get(0).getIgstamount()) 
															&& invoice.getItems().get(0).getIgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getCgstamount()) 
															&& invoice.getItems().get(0).getCgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getSgstamount()) 
															&& invoice.getItems().get(0).getSgstamount() < 0)
													|| (isNotEmpty(invoice.getItems().get(0).getIsdcessamount()) 
															&& invoice.getItems().get(0).getIsdcessamount() < 0)
													|| isEmpty(invoice.getItems().get(0).getTotal()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell advAdjustError = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														advAdjustError.setCellValue(errorVal);
														advAdjustError.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if(key.equals("impgList") && (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell advAdjustError = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														advAdjustError.setCellValue(errorVal);
														advAdjustError.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if (key.equals("impgList") && (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim()))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Invoice Number Format");
														gstincell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if(key.equals("advAdjustedList") && (isEmpty(invoice.getStrDate()) || invoice.getStrDate().equals("Invoice Date*"))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell advAdjustError = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														advAdjustError.setCellValue(errorVal);
														advAdjustError.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											}else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("itrvslList") && ((isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems())
																	&& (isEmpty(invoice.getItems().get(0).getHsn())
																			|| isEmpty(invoice.getItems().get(0).getQuantity())
																			|| (isNotEmpty(invoice.getItems().get(0).getRate()) 
																					&& invoice.getItems().get(0).getRate() < 0)))))
															|| ((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)
																	|| returntype.equals(MasterGSTConstants.GSTR2))
																	&& key.equals("invoiceList") && (isEmpty(invoice.getB2b().get(0).getCtin()) || isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())))) {
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
												invoice = newBulkImportService.importInvoicesSimplified(invoice, client,returntype,branch,vertical,month,year,patterns,statesMap);
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
							session = request.getSession(false);
							if (isNotEmpty(session)) {
								session.setAttribute("custom-bulkimportsimplified_error.xls", convFile);
								flag=true;
								response.setShowLink(true);
								fileSystemResource=new FileSystemResource(convFile);
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
					String invReturnType = MasterGSTConstants.GSTR1;
					if(isNotEmpty(templateMapper.getMapperType())) {
						if("Sales".equalsIgnoreCase(templateMapper.getMapperType())) {
							invReturnType = MasterGSTConstants.GSTR1;
						}else {
							invReturnType = MasterGSTConstants.PURCHASE_REGISTER;
						}
					}
					processflag = true;
					newBulkImportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, EXCEL_TEMPLATE,otherconfig);
				} else {
					String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
					task = "INTERRUPTED";
					response.setError(errMsg);
					response.setShowLink(true);
					response.setError("Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.");
				}
			} catch (Exception e) {
				task = "INTERRUPTED";
				String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters/ File are Corrupted, please download latest template and re-import invoices.";
				
				bulkImportTask.setTask(task);
				response.setShowLink(true);
				response.setError(errMsg);
				List<ImportSummary> summary = null;
				
				if(processflag) {
					summary = new ArrayList<ImportSummary>();
					if(isNotEmpty(response.getSummaryList())) {
						summary=response.getSummaryList();
					}
				}
				
				bulkImportTask.setResponse(response);
				bulkImportTaskService.createBulkImportTask(bulkImportTask);
				try {
					fileSystemResource = new FileSystemResource(convert(file));
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				sendBulkImportMails(true, usr, ccmail, fullname, summary, response, fileSystemResource);
				session = request.getSession(false);
				if (isNotEmpty(session) && isNotEmpty(session.getAttribute("custom-bulkimportsimplified_error.xls"))) {
					File errFile = (File) session.getAttribute("custom-bulkimportsimplified_error.xls");
					try {
						errFile.delete();
					} catch (Exception ex) {}
					session.removeAttribute("custom-bulkimportsimplified_error.xls");
				}
				mailflag = true;
				response.setError(errMsg);
				logger.error(CLASSNAME + method + " ERROR", e);
			}
		}
		
		List<ImportSummary> summary=new ArrayList<ImportSummary>();
		if(isNotEmpty(response.getSummaryList())) {
			summary=response.getSummaryList();
		}
		bulkImportTask.setTask(task);
		bulkImportTask.setResponse(response);
		bulkImportTaskService.createBulkImportTask(bulkImportTask);
		/*if(flag) {
			if(isNotEmpty(usr.getEmail())) {
				emailService.sendBulkImportEmail(usr.getEmail(), VmUtil.velocityTemplate("email_bulkimport.vm", fullname, summary, response), "MasterGST Bulk Import Reports",fileSystemResource.getFile());		
			}
		}else {
			if(isNotEmpty(usr.getEmail())) {
				emailService.sendEnrollEmail(usr.getEmail(), VmUtil.velocityTemplate("email_bulkimport.vm", fullname, summary, response), "MasterGST Bulk Import Reports");		
			}			
		}*/
		if(!mailflag){
			sendBulkImportMails(flag, usr, ccmail, fullname, summary, response, fileSystemResource);
			session = request.getSession(false);
			if (isNotEmpty(session) && isNotEmpty(session.getAttribute("custom-bulkimportsimplified_error.xls"))) {
				File errFile = (File) session.getAttribute("custom-bulkimportsimplified_error.xls");
				try {
					errFile.delete();
				} catch (Exception e) {}
				session.removeAttribute("custom-bulkimportsimplified_error.xls");
			}
		}
	}
	
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
	
	public Date invdate(String invdate,String[] patterns,int year,int month) {
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
	
	@Async
	public void bulkTallyImports(String id, String fullname, String clientid, String returntype, MultipartFile file,int month, int year,String branch, String vertical, 
			List<String> list, String btallyfinancialYear, String btallymonth, ModelMap model, HttpServletRequest request, BulkImportTask bulkImportTask, String ccmail) throws MasterGSTException {		
		final String method = "bulkTallyImports::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		String stateName = client.getStatename();
		int skipRows = 4;
		boolean flag=false;
		FileSystemResource fileSystemResource=null;
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		String task = "INTERRUPTED";
		User usr=userService.findById(id);

		boolean mailflag = false;
		boolean processflag = false;
		HttpSession session = null;
		if (!file.isEmpty()) {
			logger.debug(CLASSNAME + method + file.getOriginalFilename());
			try {
				task = "COMPLETE";
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
					OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
					Map<String, String> statesMap = newBulkImportService.getStateMap();
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
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
														&& isNotEmpty(datatypeSheet.getRow(index))) {
														if(index != 1) {
															Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
															gstincell.setCellValue("Invalid Invoice Number Format");
															gstincell.setCellStyle(style);
														}
														errorIndxes.add(index);
												}
											
										}else if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())
												&& !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().toUpperCase())) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid GSTIN Number");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										} else if (returntype.equals(MasterGSTConstants.GSTR1)
												&& (key.equals("creditList") || key.equals("cdnurList")
														|| key.equals("exportList"))
												&& (isEmpty(invoice.getStrDate())
														|| (key.equals("creditList") && (isEmpty(
																((GSTR1) invoice).getCdnr())
																|| isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())
																|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt())
																|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0)
																		.getNtNum())
																|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0)
																		.getNtty())))
														|| (key.equals("cdnurList") && (isEmpty(
																((GSTR1) invoice).getCdnur())
																|| isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtNum())
																|| isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
														|| (key.equals("exportList") && (isEmpty(invoice.getExp())
																|| isEmpty(invoice.getExp().get(0).getExpTyp())))
														|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = newBulkImportService.tallyErrorMsg(key,invoice,returntype);
												Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
												impsError.setCellValue(errorVal);
												impsError.setCellStyle(style);
												errorIndxes.add(index);
											}
										} else if (returntype.equals(MasterGSTConstants.GSTR4)
												&& (key.equals("invoiceList") || key.equals("creditList")
														|| key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStrDate())
														|| (key.equals("creditList") && (isEmpty(
																((GSTR4) invoice).getCdnr())
																|| isEmpty(((GSTR4) invoice).getB2b().get(0).getCtin())
																|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt())
																|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0)
																		.getNtNum())
																|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0)
																		.getNtty())))
														|| (key.equals("cdnurList") && (isEmpty(
																((GSTR4) invoice).getCdnur())
																|| isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtNum())
																|| isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtty())))
														|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b())
																|| isEmpty(invoice.getB2b().get(0).getCtin())))
														|| (key.equals("exportList") && (isEmpty(invoice.getExp())
																|| isEmpty(invoice.getExp().get(0).getExpTyp())))
														|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												datatypeSheet.getRow(index).createCell(lastcolumn).setCellValue("Please Enter Valid Values");
												errorIndxes.add(index);
											}
										}else if(key.equals("b2cList") && (isEmpty(invoice.getStatename()) || "Place Of Supply".equalsIgnoreCase(invoice.getStatename())   )) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = newBulkImportService.tallyErrorMsg(key,invoice,returntype);
												Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
												impsError.setCellValue(errorVal);
												impsError.setCellStyle(style);
												errorIndxes.add(index);
											}
										} else if (!key.equals("nilList") && !key.equals("impgList")
												&& !key.equals("itrvslList")
												&& (!key.equals("b2cList") && !key.equals("creditList")
														&& (isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
																|| (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue()))))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = "";
												if(isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue()))) {
													errorVal += "Please Enter Values";
												}
												Cell error = datatypeSheet.getRow(index).createCell(lastcolumn);
												error.setCellValue(errorVal);
												error.setCellStyle(style);
												errorIndxes.add(index);
											}
										}else if (key.equals("impgList")
												&& (isEmpty(invoice.getItems())
														|| (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue())))) {
											failed++;
											if (isNotEmpty(datatypeSheet)
													&& ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = newBulkImportService.tallyErrorMsg(key,invoice,returntype);
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
										} else {
											
											invoice = newBulkImportService.importInvoicesTally(invoice, client, returntype, branch, vertical,month,year,patterns,statesMap);
											if(key.equals("b2cList")) {
												if(isNotEmpty(btallyfinancialYear) && isNotEmpty(btallymonth)) {
													Calendar cal = Calendar.getInstance();
													int b2ctmnth = Integer.parseInt(btallymonth);
													int b2cyear = Integer.parseInt(btallyfinancialYear);
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
							if(isNotEmpty(invoice)) {
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
							session = request.getSession(false);
							if (isNotEmpty(session)) {
								session.setAttribute("bulktallyimport_errors.xls", convFile);
								flag=true;
								response.setShowLink(true);
								fileSystemResource=new FileSystemResource(convFile);
							}
						}
					}
					processflag = true;
					//bulkImportServices.updateExcelData(beans, list, returntype, id, fullname, clientid, TALLY_TEMPLATE);
					newBulkImportService.updateExcelData(beans, list, returntype, id, fullname, clientid, EXCEL_TEMPLATE,otherconfig);
				} else {
					String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
					task = "INTERRUPTED";
					response.setError(errMsg);
					response.setShowLink(true);
				}
			} catch (Exception e) {
				task = "INTERRUPTED";
				String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters/ File are Corrupted, please download latest template and re-import invoices.";
				
				bulkImportTask.setTask(task);
				response.setShowLink(true);
				response.setError(errMsg);
				List<ImportSummary> summary = null;
				
				if(processflag) {
					summary = new ArrayList<ImportSummary>();
					if(isNotEmpty(response.getSummaryList())) {
						summary=response.getSummaryList();
					}
				}
				
				bulkImportTask.setResponse(response);
				bulkImportTaskService.createBulkImportTask(bulkImportTask);
				try {
					fileSystemResource = new FileSystemResource(convert(file));
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				sendBulkImportMails(true, usr, ccmail, fullname, summary, response, fileSystemResource);
				session = request.getSession(false);
				if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulktallyimport_errors.xls"))) {
					File errFile = (File) session.getAttribute("bulktallyimport_errors.xls");
					try {
						errFile.delete();
					} catch (Exception ex) {}
					session.removeAttribute("bulktallyimport_errors.xls");
				}
				mailflag = true;
				logger.error(CLASSNAME + method + " ERROR", e);
			}
		}
		
		List<ImportSummary> summary=new ArrayList<ImportSummary>();
		if(isNotEmpty(response.getSummaryList())) {
			summary=response.getSummaryList();
		}
		bulkImportTask.setTask(task);
		bulkImportTask.setResponse(response);
		bulkImportTaskService.createBulkImportTask(bulkImportTask);
		if(!mailflag){
			sendBulkImportMails(flag, usr, ccmail, fullname, summary, response, fileSystemResource);
			session = request.getSession(false);
			if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulktallyimport_errors.xls"))) {
				File errFile = (File) session.getAttribute("bulktallyimport_errors.xls");
				try {
					errFile.delete();
				} catch (Exception e) {}
				session.removeAttribute("bulktallyimport_errors.xls");
			}
		}
	
		
	}
	
	@Async
	public void bulkTallyImportsNewVersion(String id, String fullname, String clientid, String returntype, String tallytype, MultipartFile file,int month, int year,String branch, String vertical, 
			List<String> list, String btallyfinancialYear, String btallymonth, ModelMap model, HttpServletRequest request, BulkImportTask bulkImportTask, String ccmail) throws MasterGSTException {		
		final String method = "bulkTallyImports::";
		logger.debug(CLASSNAME + method + BEGIN);
		int skipRows = 4;
		boolean flag=false;
		FileSystemResource fileSystemResource=null;
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		String task = "INTERRUPTED";
		User usr=userService.findById(id);

		boolean mailflag = false;
		boolean processflag = false;
		HttpSession session = null;
		if (!file.isEmpty()) {
			logger.debug(CLASSNAME + method + file.getOriginalFilename());
			try {
				task = "COMPLETE";
				
				String xmlPath = "";
				if(isNotEmpty(tallytype) && tallytype.equalsIgnoreCase("TallyPrimeGSTR1")){
					xmlPath = "classpath:tally_excel_prime_config.xml";
				}else {
					xmlPath = "classpath:tally_excel_config_v17.xml";
				}
				
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
					logger.debug(CLASSNAME + method + "creditList: {}", beans.get("creditList"));
					logger.debug(CLASSNAME + method + "exportList: {}", beans.get("exportList"));
					logger.debug(CLASSNAME + method + "advReceiptList: {}", beans.get("advReceiptList"));
					logger.debug(CLASSNAME + method + "b2bList: {}", beans.get("b2bList"));
					logger.debug(CLASSNAME + method + "b2cList: {}", beans.get("b2cList"));
					logger.debug(CLASSNAME + method + "b2clList: {}", beans.get("b2clList"));
					Client client = clientService.findById(clientid);
					OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
					Map<String, String> statesMap = newBulkImportService.getStateMap();
					Map<String,StateConfig> states = newBulkImportService.getStatesMap();
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
								List<Integer> errorIndxes = Lists.newArrayList();
								if("hsnSummaryList".equalsIgnoreCase(key)) {
									List<HSNData>  hsnData= Lists.newArrayList();
									int i=1;
									for (InvoiceParent invoice : invoices) {
										if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0))) {
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
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
														&& isNotEmpty(datatypeSheet.getRow(index))) {
														if(index != 1) {
															Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
															gstincell.setCellValue("Invalid Invoice Number Format");
															gstincell.setCellStyle(style);
														}
														errorIndxes.add(index);
												}
											
										}else if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())
												&& !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().toUpperCase())) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid GSTIN Number");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										} else if (returntype.equals(MasterGSTConstants.GSTR1) && (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStrDate())
														|| (key.equals("creditList") && (isEmpty(((GSTR1) invoice).getCdnr()) || isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
														|| (key.equals("cdnurList") && (isEmpty(((GSTR1) invoice).getCdnur()) || isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
														|| (key.equals("exportList") && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
														|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = newBulkImportService.tallyErrorMsg(key,invoice,returntype);
												Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
												impsError.setCellValue(errorVal);
												impsError.setCellStyle(style);
												errorIndxes.add(index);
											}
										} else if (returntype.equals(MasterGSTConstants.GSTR4)
												&& (key.equals("invoiceList") || key.equals("creditList")
														|| key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStrDate())
														|| (key.equals("creditList") && (isEmpty(
																((GSTR4) invoice).getCdnr())
																|| isEmpty(((GSTR4) invoice).getB2b().get(0).getCtin())
																|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt())
																|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0)
																		.getNtNum())
																|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0)
																		.getNtty())))
														|| (key.equals("cdnurList") && (isEmpty(
																((GSTR4) invoice).getCdnur())
																|| isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtNum())
																|| isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtty())))
														|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b())
																|| isEmpty(invoice.getB2b().get(0).getCtin())))
														|| (key.equals("exportList") && (isEmpty(invoice.getExp())
																|| isEmpty(invoice.getExp().get(0).getExpTyp())))
														|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												datatypeSheet.getRow(index).createCell(lastcolumn).setCellValue("Please Enter Valid Values");
												errorIndxes.add(index);
											}
										}else if(key.equals("b2cList") && (isEmpty(invoice.getStatename()) || "Place Of Supply".equalsIgnoreCase(invoice.getStatename())   )) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = newBulkImportService.tallyErrorMsg(key,invoice,returntype);
												Cell impsError = datatypeSheet.getRow(index).createCell(lastcolumn);
												impsError.setCellValue(errorVal);
												impsError.setCellStyle(style);
												errorIndxes.add(index);
											}
										} else if (!key.equals("nilList") && !key.equals("impgList")
												&& !key.equals("itrvslList")
												&& (!key.equals("b2cList") && !key.equals("creditList")
														&& (isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())
																|| (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue()))))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = "";
												if(isEmpty(invoice.getItems()) || (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue()))) {
													errorVal += "Please Enter Values";
												}
												Cell error = datatypeSheet.getRow(index).createCell(lastcolumn);
												error.setCellValue(errorVal);
												error.setCellStyle(style);
												errorIndxes.add(index);
											}
										}else if (key.equals("impgList")
												&& (isEmpty(invoice.getItems())
														|| (isNotEmpty(invoice.getItems()) && isEmpty(invoice.getItems().get(0).getTaxablevalue())))) {
											failed++;
											if (isNotEmpty(datatypeSheet)
													&& ((index) < datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												String errorVal = newBulkImportService.tallyErrorMsg(key,invoice,returntype);
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
										} else {
											
											invoice = newBulkImportService.importInvoicesTally(invoice, client, returntype, branch, vertical,month,year,patterns,statesMap);												
											
											if(key.equals("b2cList")) {
												if(isNotEmpty(btallyfinancialYear) && isNotEmpty(btallymonth)) {
													Calendar cal = Calendar.getInstance();
													int b2ctmnth = Integer.parseInt(btallymonth);
													int b2cyear = Integer.parseInt(btallyfinancialYear);
													if(b2ctmnth >= 1 && b2ctmnth < 4) {
														b2cyear = b2cyear+1;
													}
													cal.set(b2cyear, b2ctmnth - 1, 1);
													invoice.setDateofinvoice(cal.getTime());
												}
											}else if(key.equals("nilList")) {
												if(isNotEmpty(btallyfinancialYear) && isNotEmpty(btallymonth)) {
													Calendar cal = Calendar.getInstance();
													int nilmnth = Integer.parseInt(btallymonth);
													int nilyear = Integer.parseInt(btallyfinancialYear);
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
											if(key.equals("b2cList")) {
												if(isNotEmpty(btallyfinancialYear) && isNotEmpty(btallymonth)) {
													Calendar cal = Calendar.getInstance();
													int b2ctmnth = Integer.parseInt(btallymonth);
													int b2cyear = Integer.parseInt(btallyfinancialYear);
													if(b2ctmnth >= 1 && b2ctmnth < 4) {
														b2cyear = b2cyear+1;
													}
												cal.set(b2cyear, b2ctmnth - 1, 1);
												invoice.setDateofinvoice(cal.getTime());
												}
											}else if(key.equals("nilList")) {
												if(isNotEmpty(btallyfinancialYear) && isNotEmpty(btallymonth)) {
													Calendar cal = Calendar.getInstance();
													int nilmnth = Integer.parseInt(btallymonth);
													int nilyear = Integer.parseInt(btallyfinancialYear);
													if(nilmnth >= 1 && nilmnth < 4) {
														nilyear = nilyear+1;
													}
													cal.set(nilyear, nilmnth - 1, 1);
													invoice.setDateofinvoice(cal.getTime());
												}
											}if(key.equalsIgnoreCase("creditList")) {
												if(isNotEmpty(invoice.getStatename())) {
													if (isNotEmpty(((GSTR1) invoice).getCdnr()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0)) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt())  && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0))) {
														String pos = getStateCode(invoice.getStatename());
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
																String pos = getStateCode(invoice.getStatename());
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
							if(isNotEmpty(invoice)) {
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
							session = request.getSession(false);
							if (isNotEmpty(session)) {
								session.setAttribute("bulktallyimport-newversion_error.xls", convFile);
								flag=true;
								response.setShowLink(true);
								fileSystemResource=new FileSystemResource(convFile);
							}
						}
					}
					
					processflag = true;
					newBulkImportService.updateExcelData(beans, list, returntype, id, fullname, clientid, EXCEL_TEMPLATE,otherconfig);
				} else {
					String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
					task = "INTERRUPTED";
					response.setError(errMsg);
					response.setShowLink(true);
				}
			} catch (Exception e) {
				task = "INTERRUPTED";
				//String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
				
				String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters/ File are Corrupted, please download latest template and re-import invoices.";
				
				bulkImportTask.setTask(task);
				response.setShowLink(true);
				response.setError(errMsg);
				List<ImportSummary> summary = null;
				
				if(processflag) {
					summary = new ArrayList<ImportSummary>();
					if(isNotEmpty(response.getSummaryList())) {
						summary=response.getSummaryList();
					}
				}
				
				bulkImportTask.setResponse(response);
				bulkImportTaskService.createBulkImportTask(bulkImportTask);
				try {
					fileSystemResource = new FileSystemResource(convert(file));
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				sendBulkImportMails(true, usr, ccmail, fullname, summary, response, fileSystemResource);
				session = request.getSession(false);
				if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulktallyimport-newversion_error.xls"))) {
					File errFile = (File) session.getAttribute("bulktallyimport-newversion_error.xls");
					try {
						errFile.delete();
					} catch (Exception ex) {}
					session.removeAttribute("bulktallyimport-newversion_error.xls");
				}
				mailflag = true;
				logger.error(CLASSNAME + method + " ERROR", e);
			}
		}
		
		List<ImportSummary> summary=new ArrayList<ImportSummary>();
		if(isNotEmpty(response.getSummaryList())) {
			summary=response.getSummaryList();
		}
		bulkImportTask.setTask(task);
		bulkImportTask.setResponse(response);
		bulkImportTaskService.createBulkImportTask(bulkImportTask);
		if(!mailflag){
			sendBulkImportMails(flag, usr, ccmail, fullname, summary, response, fileSystemResource);
			session = request.getSession(false);
			if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulktallyimport-newversion_error.xls"))) {
				File errFile = (File) session.getAttribute("bulktallyimport-newversion_error.xls");
				try {
					errFile.delete();
				} catch (Exception e) {}
				session.removeAttribute("bulktallyimport-newversion_error.xls");
			}
		}
	}
	
	
	@Async
	public void bulkSageImports(String id, String fullname, String clientid, String returntype, MultipartFile file,int month, int year,String branch, String vertical, 
			List<String> list, ModelMap model, HttpServletRequest request, BulkImportTask bulkImportTask, String ccmail) throws MasterGSTException {		
		final String method = "bulkImports::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		String stateName = client.getStatename();
		int skipRows = 1;
		OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
		if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableSalesFields())) {
			otherconfig.setEnableSalesFields(false);
		}
		if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnablePurFields())) {
			otherconfig.setEnablePurFields(false);
		}
		String task = "INTERRUPTED";
		User usr=userService.findById(id);
		boolean mailflag = false;
		boolean processflag = false;
		HttpSession session = null;
		boolean hsnqtyrate = false;
		if(isNotEmpty(otherconfig)) {
			if("GSTR1".equals(returntype) || "Sales Register".equals(returntype) || "SalesRegister".equals(returntype)) {
				hsnqtyrate = otherconfig.isEnableSalesFields();
			}else {
				hsnqtyrate = otherconfig.isEnablePurFields();
			}
		}
		boolean flag=false;
		FileSystemResource fileSystemResource=null;
		
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		if (!file.isEmpty()) {
			task = "COMPLETE";
			logger.debug(CLASSNAME + method + file.getOriginalFilename());
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
				logger.info(CLASSNAME + method + "Status: {}", status.isStatusOK());
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
								summary.setTotal(beans.get(key).size()-1);
								List<Integer> errorIndxes = Lists.newArrayList();
								for (InvoiceParent invoice : invoices) {
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
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid Invoice Number Format");
													gstincell.setCellStyle(style);
												}
												errorIndxes.add(index);
											}
										
									}else if(isEmpty(invoice.getInvtype())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
												&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													datatypeSheet.getRow(index).createCell(lastcolumn).setCellValue("Please Enter Invoice Type");
												}
											errorIndxes.add(index);
										}
									}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isNotEmpty(invoice.getItems().get(0).getCgstrate()) && !useList(csgstrate,invoice.getItems().get(0).getCgstrate())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter Valid Tax Rate");
													gstincell.setCellStyle(style);
												}
											errorIndxes.add(index);
										}
								}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isNotEmpty(invoice.getItems().get(0).getSgstrate()) && !useList(csgstrate,invoice.getItems().get(0).getSgstrate())) {
									failed++;
									if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 1) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter Valid Tax Rate");
												gstincell.setCellStyle(style);
											}
										errorIndxes.add(index);
									}
								}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isNotEmpty(invoice.getItems().get(0).getIgstrate()) && !useList(igstrate,invoice.getItems().get(0).getIgstrate())) {
									failed++;
									if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 1) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter Valid Tax Rate");
												gstincell.setCellStyle(style);
											}
										errorIndxes.add(index);
									}
								}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isNotEmpty(invoice.getItems().get(0).getUgstrate()) && !useList(csgstrate,invoice.getItems().get(0).getUgstrate())) {
									failed++;
									if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
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
						// workbook.close();
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
							if (summary.getInvfailed() > 0) {
								failedCount += summary.getInvfailed();
							}
						}
						if (failedCount > 0 && isNotEmpty(convFile)) {
							session = request.getSession(false);
							if (isNotEmpty(session)) {
								session.setAttribute("bulksageimport_error.xls", convFile);
								flag=true;
								response.setShowLink(true);
								fileSystemResource=new FileSystemResource(convFile);
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
					processflag = true;
					newBulkImportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, EXCEL_TEMPLATE,otherconfig);
				} else {
					String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
					task = "INTERRUPTED";
					response.setError(errMsg);
					response.setShowLink(true);
				}
			} catch (Exception e) {
				task = "INTERRUPTED";
				String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters/ File are Corrupted, please download latest template and re-import invoices.";
				
				bulkImportTask.setTask(task);
				response.setShowLink(true);
				response.setError(errMsg);
				List<ImportSummary> summary = null;
				
				if(processflag) {
					summary = new ArrayList<ImportSummary>();
					if(isNotEmpty(response.getSummaryList())) {
						summary=response.getSummaryList();
					}
				}
				
				bulkImportTask.setResponse(response);
				bulkImportTaskService.createBulkImportTask(bulkImportTask);
				try {
					fileSystemResource = new FileSystemResource(convert(file));
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
				sendBulkImportMails(true, usr, ccmail, fullname, summary, response, fileSystemResource);
				session = request.getSession(false);
				if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulksageimport_error.xls"))) {
					File errFile = (File) session.getAttribute("bulksageimport_error.xls");
					try {
						errFile.delete();
					} catch (Exception ex) {}
					session.removeAttribute("bulksageimport_error.xls");
				}
				mailflag = true;
				logger.error(CLASSNAME + method + " ERROR", e);
			}
		}
		
		List<ImportSummary> summary=new ArrayList<ImportSummary>();
		if(isNotEmpty(response.getSummaryList())) {
			summary=response.getSummaryList();
		}
		bulkImportTask.setTask(task);
		bulkImportTask.setResponse(response);
		bulkImportTaskService.createBulkImportTask(bulkImportTask);
		if(!mailflag){
			sendBulkImportMails(flag, usr, ccmail, fullname, summary, response, fileSystemResource);
			session = request.getSession(false);
			if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulksageimport_error.xls"))) {
				File errFile = (File) session.getAttribute("bulksageimport_error.xls");
				try {
					errFile.delete();
				} catch (Exception e) {}
				session.removeAttribute("bulksageimport_error.xls");
			}
		}
	
		
	}
	public static boolean useList(Double[] arr, Double targetValue) {
		return Arrays.asList(arr).contains(targetValue);
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

@Async
public void bulkImportsForEinvoice(String id, String fullname, String clientid, String returntype, MultipartFile file,int month, int year,String branch, String vertical, 
		List<String> list, ModelMap model, HttpServletRequest request, BulkImportTask bulkImportTask, String ccmail, String templateType) throws MasterGSTException {		
	final String method = "bulkImports::";
	logger.debug(CLASSNAME + method + BEGIN);
	
	System.out.println(Thread.currentThread().getName());
	CustomFields customFields = customFieldsRepository.findByClientid(clientid);
	OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
	if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableSalesFields())) {
		otherconfig.setEnableSalesFields(false);
	}
	if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnablePurFields())) {
		otherconfig.setEnablePurFields(false);
	}
	
	boolean hsnqtyrate = false;
	if(isNotEmpty(otherconfig)) {
		hsnqtyrate = otherconfig.isEnableSalesFields();
	}
	boolean flag=false;
	FileSystemResource fileSystemResource=null;
	
	BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
	ImportResponse response = new ImportResponse();
	response.setMonth(month);
	response.setYear(year);
	List<ImportSummary> summaryList = Lists.newArrayList();
	String task = "INTERRUPTED";
	User usr=userService.findById(id);
	boolean mailflag = false;
	boolean processflag = false;
	
	HttpSession session = null;
	if (!file.isEmpty()) {
		logger.debug(CLASSNAME + method + file.getOriginalFilename());
		
		try {
			task = "COMPLETE";
			String xmlPath = "classpath:mastergst_einvoice_excel_config.xml";
			if(templateType.equalsIgnoreCase("sage")) {
				xmlPath = "classpath:mastergst_sage_einvoice_config.xml";
			}else if(templateType.equalsIgnoreCase("entertainment")) {
				xmlPath = "classpath:mastergst_allu_einvoice_excel_config.xml";
			}
			Resource config = resourceLoader.getResource(xmlPath);
			DateConverter converter = new DateConverter();
			String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd" };
			converter.setPatterns(patterns);
			ConvertUtils.register(converter, Date.class);
			ReaderConfig.getInstance().setSkipErrors(true);
			XLSReader xlsReader = ReaderBuilder.buildFromXML(config.getInputStream());
			//JxlsHelper.getInstance().setProcessFormulas(false);
			Map<String, List<InvoiceParent>> sheetMap = getExcelSheetMap();
			Map<String, List<InvoiceParent>> beans = Maps.newHashMap();
			beans.put("creditList", sheetMap.get("creditList"));
			beans.put("exportList", sheetMap.get("exportList"));
			beans.put("invoiceList", sheetMap.get("invoiceList"));
			beans.put("cdnurList", sheetMap.get("cdnurList"));
			XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
			logger.debug(CLASSNAME + method + "Status: {}", status.isStatusOK());
			
			if (status.isStatusOK()) {
				Map<String, String> statesMap = newBulkImportService.getStateMap();
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
				for (String key : beans.keySet()) {
					if (isNotEmpty(sheetMap.get(key)) && sheetMap.get(key).size() > 0) {
						ImportSummary summary = new ImportSummary();
						if (key.equals("invoiceList")) {
							summary.setName("Invoices");
						} else if (key.equals("creditList")) {
							summary.setName("Credit_Debit_Notes");
						} else if (key.equals("cdnurList")) {
							summary.setName("Credit_Debit_Note_Unregistered");
						} else if (key.equals("exportList")) {
							summary.setName("Exports");
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
						if (isNotEmpty(invoices)) {
							int index = 1;
							summary.setTotal(beans.get(key).size()-1);
							List<Integer> errorIndxes = Lists.newArrayList();
							for (InvoiceParent invoice : invoices) {
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
								if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(INVOICENO_REGEX, invnum.trim())) {
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
									
								}else if(isNotEmpty(invoice.getB2b()) 
										&& isNotEmpty(invoice.getB2b().get(0).getCtin())
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
								}else{
									if(hsnqtyrate) {
										if (returntype.equals(MasterGSTConstants.GSTR1)
												&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
														|| (key.equals("creditList") && (isEmpty(((GSTR1)invoice).getCdnr())
																|| isEmpty(((GSTR1)invoice).getB2b().get(0).getCtin())
																|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt())
																/*|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getInum())
																|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getIdt())*/
																|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
														|| (key.equals("cdnurList") && (isEmpty(((GSTR1)invoice).getCdnur()) || "Credit/Debit Note Date*".equals(invoice.getStrDate())
																/*|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getInum())
																|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getIdt())*/
																|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getNtty())))
														|| (key.equals("exportList") && (isEmpty(invoice.getEinvCategory()) || "Invoice Date*".equals(invoice.getStrDate())))
														|| isEmpty(invoice.getStrDate())
														|| isEmpty(invoice.getItems()) 
														|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																&& invoice.getItems().get(0).getIgstrate() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																&& invoice.getItems().get(0).getCgstrate() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																&& invoice.getItems().get(0).getSgstrate() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																&& invoice.getItems().get(0).getCessrate() < 0))) {
											
												failed++;
												if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
														&& isNotEmpty(datatypeSheet.getRow(index))) {
													invoice.setGstr1orEinvoice("Einvoice");
													String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
													errorcell.setCellValue(errorVal);
													errorcell.setCellStyle(style);
													errorIndxes.add(index);
												}
										} else {
											if(templateType.equalsIgnoreCase("sage")) {
												invoice = importInvoiceService.importEInvoicesHsnQtyRateMandatory(invoice, returntype, branch, vertical, month, year, patterns);
											}else {
												invoice = newBulkImportService.importEInvoicesHsnQtyRateMandatory(invoice, returntype, branch, vertical, month, year, patterns, statesMap);
											}
											if(templateType.equalsIgnoreCase("entertainment")) {
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
									}else {
										if (returntype.equals(MasterGSTConstants.GSTR1)
												&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
														|| (key.equals("creditList") && (isEmpty(((GSTR1)invoice).getCdnr())
																|| isEmpty(((GSTR1)invoice).getB2b().get(0).getCtin())
																|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt())
																|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
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
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
													&& isNotEmpty(datatypeSheet.getRow(index))) {
												invoice.setGstr1orEinvoice("Einvoice");
												String errorVal = newBulkImportService.errorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
												Cell errorcell = datatypeSheet.getRow(index).createCell(lastcolumn);
												errorcell.setCellValue(errorVal);
												errorcell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}else {
											if(templateType.equalsIgnoreCase("sage")) {
												invoice = importInvoiceService.importEInvoicesHsnQtyRateMandatory(invoice, returntype, branch, vertical, month, year, patterns);
											}else {
												invoice = newBulkImportService.importEInvoicesHsnQtyRateMandatory(invoice, returntype, branch, vertical, month, year, patterns, statesMap);
											}
											if(templateType.equalsIgnoreCase("entertainment")) {
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
							if(failed != 0) {
								failed = failed-1;
							}
							summary.setSuccess((beans.get(key).size()-1) - (failed));
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
						summary.setFailed(failed);
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
				response.setSummaryList(summaryList);
				if (isNotEmpty(summaryList)) {
					int failedCount = 0;
					for (ImportSummary summary : summaryList) {
						if (summary.getInvfailed() > 0) {
							failedCount += summary.getInvfailed();
						}
					}
					if (failedCount > 0 && isNotEmpty(convFile)) {
						session = request.getSession(false);
						if (isNotEmpty(session)) {
							session.setAttribute("bulkerror.xls", convFile);
						}
						System.out.println("error file created...");
						flag=true;
						response.setShowLink(true);
						fileSystemResource=new FileSystemResource(convFile);
					} else {
						try {
							convFile.delete();
						} catch (Exception e) {
						}
					}
				}
				String invReturnType = returntype;			
				processflag = true;
				newBulkImportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, MASTERGST_EXCEL_TEMPLATE,otherconfig);
			} else {
				String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
				task = "INTERRUPTED";
				response.setError(errMsg);
				response.setShowLink(true);
			}
		} catch (Exception e) {
			task = "INTERRUPTED";
			//String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
			
			String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters/ File are Corrupted, please download latest template and re-import invoices.";
			
			bulkImportTask.setTask(task);
			response.setShowLink(true);
			response.setError(errMsg);
			List<ImportSummary> summary = null;
			
			if(processflag) {
				summary = new ArrayList<ImportSummary>();
				if(isNotEmpty(response.getSummaryList())) {
					summary=response.getSummaryList();
				}
			}
			
			bulkImportTask.setResponse(response);
			bulkImportTaskService.createBulkImportTask(bulkImportTask);
			try {
				fileSystemResource = new FileSystemResource(convert(file));
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			sendBulkImportMails(true, usr, ccmail, fullname, summary, response, fileSystemResource);
			session = request.getSession(false);
			if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulkerror.xls"))) {
				File errFile = (File) session.getAttribute("bulkerror.xls");
				try {
					errFile.delete();
				} catch (Exception ex) {}
				session.removeAttribute("bulkerror.xls");
			}
			mailflag = true;
			logger.error(CLASSNAME + method + " ERROR", e);
		}
	}
	
	List<ImportSummary> summary=new ArrayList<ImportSummary>();
	if(isNotEmpty(response.getSummaryList())) {
		summary=response.getSummaryList();
	}
	bulkImportTask.setTask(task);
	bulkImportTask.setResponse(response);
	bulkImportTaskService.createBulkImportTask(bulkImportTask);
	if(!mailflag){
		sendBulkImportMails(flag, usr, ccmail, fullname, summary, response, fileSystemResource);
		session = request.getSession(false);
		if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulkerror.xls"))) {
			File errFile = (File) session.getAttribute("bulkerror.xls");
			try {
				errFile.delete();
			} catch (Exception e) {}
			session.removeAttribute("bulkerror.xls");
		}
	}
}

private void customEinvoiceBulkImportSimplified(String id, String clientid, MultipartFile file,
		String returntype, int month, int year, List<String> list, String fullname,TemplateMapper templateMapper, 
		TemplateMapperDoc templateMapperDoc, boolean hsnqtyrate,OtherConfigurations otherconfig, String branch, String vertical, BulkImportTask bulkImportTask,String ccmail, HttpServletRequest request) throws MasterGSTException {
	final String method = "customEinvoiceBulkImportSimplified::";
	Client client = clientService.findById(clientid);
	String stateName = client.getStatename();
	boolean flag=false;
	FileSystemResource fileSystemResource=null;
	
	BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
	ImportResponse response = new ImportResponse();
	List<ImportSummary> summaryList = Lists.newArrayList();
	String task = "INTERRUPTED";
	User usr=userService.findById(id);
	
	boolean mailflag = false;
	boolean processflag = false;
	
	HttpSession session = null;
	int skipRows = 1;
	if(isNotEmpty(templateMapper.getSkipRows())) {
		skipRows = Integer.parseInt(templateMapper.getSkipRows());
	}
	Map<String, String> mapperConfig = Maps.newHashMap();
	
	if(NullUtil.isNotEmpty(templateMapper.getMapperConfig())){
		for(String key : templateMapper.getMapperConfig().keySet()) {
			
			if(NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key))) {
				String page = (String)templateMapper.getMapperConfig().get(key).get("page");
				mapperConfig.put(key, page);
			}
		}
	}
	if (!file.isEmpty()) {
		logger.debug(CLASSNAME + method + file.getOriginalFilename());
		try {
			task = "COMPLETED";
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
			beans.put("invoiceList", sheetMap.get("invoiceList"));
			beans.put("cdnurList", sheetMap.get("cdnurList"));
			XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
			logger.debug(CLASSNAME + method + "Status: {}", status.isStatusOK());
			
			if (status.isStatusOK()) {
				Map<String, String> statesMap = newBulkImportService.getStateMap();
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
						} else if (key.equals("cdnurList")) {
							summary.setName(mapperConfig.get("Credit_Debit_Note_Unregistered"));
							if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Credit_Debit_Note_Unregistered")) {
								dis = templateMapper.getDiscountConfig().get("Credit_Debit_Note_Unregistered");
							}
						}  else {
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
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError,i, i);
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
							List<Integer> errorIndxes = Lists.newArrayList();
							for (InvoiceParent invoice : invoices) {
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

									}else if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
											if(vindex != skipRows - 1) {
												Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
												gstincell.setCellValue("Invalid GSTIN Number");
												gstincell.setCellStyle(style);
												errorIndxes.add(vindex);
											}
										}
								} else{
									if(hsnqtyrate) {
										if (returntype.equals(MasterGSTConstants.GSTR1)
												&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
														|| (key.equals("creditList") && (isEmpty(((GSTR1)invoice).getCdnr())
																|| isEmpty(((GSTR1)invoice).getB2b().get(0).getCtin())
																|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt())
																|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
														|| (key.equals("cdnurList") && (isEmpty(((GSTR1)invoice).getCdnur())
														|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getNtty())))
														|| (key.equals("exportList") && (isEmpty(invoice.getEinvCategory())))
														|| isEmpty(invoice.getStrDate())
														|| isEmpty(invoice.getItems()) 
														|| (isNotEmpty(invoice.getItems().get(0).getRate()) 
																&& invoice.getItems().get(0).getRate() < 0))) {
											
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = importInvoiceService.einvErrorMsg(key,invoice,returntype,hsnqtyrate,"custom");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
										} else {
											invoice =  newimportService.importEInvoicesSimplified(invoice, client,returntype,branch,vertical,month,year,patterns,statesMap);
											if(key.equals("invoiceList") || key.equals("exportList")) {
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
									}else {
										if (returntype.equals(MasterGSTConstants.GSTR1)
												&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
														|| (key.equals("creditList") && (isEmpty(((GSTR1)invoice).getCdnr())
																|| isEmpty(((GSTR1)invoice).getB2b().get(0).getCtin())
																|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt())
																|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
														|| (key.equals("cdnurList") && (isEmpty(((GSTR1)invoice).getCdnur())
														|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getNtty())))
														|| (key.equals("exportList") && (isEmpty(invoice.getEinvCategory())))
														|| isEmpty(invoice.getStrDate())
														|| isEmpty(invoice.getItems()) || isEmpty(invoice.getItems().get(0).getHsn())
														|| isEmpty(invoice.getItems().get(0).getQuantity())
														|| (isNotEmpty(invoice.getItems().get(0).getRate()) 
																&& invoice.getItems().get(0).getRate() < 0))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
												if(vindex != skipRows - 1) {
													String errorVal = importInvoiceService.einvErrorMsg(key,invoice,returntype,hsnqtyrate,"custom");
													Cell advAdjustError = datatypeSheet.getRow(vindex).createCell(lastcolumn);
													advAdjustError.setCellValue(errorVal);
													advAdjustError.setCellStyle(style);
													errorIndxes.add(vindex);
												}
											}
										} else {
											invoice = newimportService.importEInvoicesSimplified(invoice, client,returntype,branch,vertical,month,year,patterns,statesMap);
											if(key.equals("invoiceList") || key.equals("exportList")) {
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
							}
								index++;
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
						summary.setFailed(failed);
						summary.setTotalinvs(totalinvs);
						summary.setInvsuccess(invsucess);
						summary.setInvfailed(totalinvs - invsucess);
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
						session = request.getSession(false);
						if (isNotEmpty(session)) {
							session.setAttribute("custom-bulkimportsimplified_error.xls", convFile);
							flag=true;
							response.setShowLink(true);
							fileSystemResource=new FileSystemResource(convFile);
						}
					} else {
						try {
							convFile.delete();
						} catch (Exception e) {
						}
					}
				}
				String invReturnType = MasterGSTConstants.GSTR1;
				if(isNotEmpty(templateMapper.getMapperType())) {
					if("Sales".equalsIgnoreCase(templateMapper.getMapperType()) || "einvoice".equalsIgnoreCase(templateMapper.getMapperType())) {
						invReturnType = MasterGSTConstants.GSTR1;
					}else {
						invReturnType = MasterGSTConstants.PURCHASE_REGISTER;
					}
				}
				processflag = true;
				newBulkImportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, EXCEL_TEMPLATE,otherconfig);
			} else {
				String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
				task = "INTERRUPTED";
				response.setError(errMsg);
				response.setShowLink(true);
				response.setError("Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.");
			}
		} catch (Exception e) {
			task = "INTERRUPTED";
			String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters/ File are Corrupted, please download latest template and re-import invoices.";
			
			bulkImportTask.setTask(task);
			response.setShowLink(true);
			response.setError(errMsg);
			List<ImportSummary> summary = null;
			
			if(processflag) {
				summary = new ArrayList<ImportSummary>();
				if(isNotEmpty(response.getSummaryList())) {
					summary=response.getSummaryList();
				}
			}
			
			bulkImportTask.setResponse(response);
			bulkImportTaskService.createBulkImportTask(bulkImportTask);
			try {
				fileSystemResource = new FileSystemResource(convert(file));
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			sendBulkImportMails(true, usr, ccmail, fullname, summary, response, fileSystemResource);
			session = request.getSession(false);
			if (isNotEmpty(session) && isNotEmpty(session.getAttribute("custom-bulkimportsimplified_error.xls"))) {
				File errFile = (File) session.getAttribute("custom-bulkimportsimplified_error.xls");
				try {
					errFile.delete();
				} catch (Exception ex) {}
				session.removeAttribute("custom-bulkimportsimplified_error.xls");
			}
			mailflag = true;
			response.setError(errMsg);
			logger.error(CLASSNAME + method + " ERROR", e);
		}
	}
	
	List<ImportSummary> summary=new ArrayList<ImportSummary>();
	if(isNotEmpty(response.getSummaryList())) {
		summary=response.getSummaryList();
	}
	bulkImportTask.setTask(task);
	bulkImportTask.setResponse(response);
	bulkImportTaskService.createBulkImportTask(bulkImportTask);
	if(!mailflag){
		sendBulkImportMails(flag, usr, ccmail, fullname, summary, response, fileSystemResource);
		session = request.getSession(false);
		if (isNotEmpty(session) && isNotEmpty(session.getAttribute("custom-bulkimportsimplified_error.xls"))) {
			File errFile = (File) session.getAttribute("custom-bulkimportsimplified_error.xls");
			try {
				errFile.delete();
			} catch (Exception e) {}
			session.removeAttribute("custom-bulkimportsimplified_error.xls");
		}
	}
	
}

private void customEinvoiceBulkImportDetailed(String id, String clientid, MultipartFile file, String returntype,
		int month, int year, List<String> list, String fullname, TemplateMapper templateMapper,
		TemplateMapperDoc templateMapperDoc, boolean hsnqtyrate, OtherConfigurations otherconfig, String branch,String vertical, BulkImportTask bulkImportTask, String ccmail, HttpServletRequest request) throws MasterGSTException {
	final String method = "customEinvoiceBulkImportDetailed::";
	boolean flag=false;
	FileSystemResource fileSystemResource=null;
	Client client = clientService.findById(clientid);
	BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
	ImportResponse response = new ImportResponse();
	List<ImportSummary> summaryList = Lists.newArrayList();
	User usr=userService.findById(id);
	String task = "INTERRUPTED";
	boolean mailflag = false;
	boolean processflag = false;
	HttpSession session = null;
	
	int skipRows = 1;
	if(isNotEmpty(templateMapper.getSkipRows())) {
		skipRows = Integer.parseInt(templateMapper.getSkipRows());
	}
	Map<String, String> mapperConfig = Maps.newHashMap();
	
	if(NullUtil.isNotEmpty(templateMapper.getMapperConfig())){
		for(String key : templateMapper.getMapperConfig().keySet()) {
			
			if(NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key))) {
				String page = (String)templateMapper.getMapperConfig().get(key).get("page");
				mapperConfig.put(key, page);
			}
		}
	}
	if (!file.isEmpty()) {
		logger.debug(CLASSNAME + method + file.getOriginalFilename());
		try {
			task = "COMPLETE";
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
			beans.put("invoiceList", sheetMap.get("invoiceList"));
			beans.put("cdnurList", sheetMap.get("cdnurList"));

			XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
			logger.info(CLASSNAME + method + "Status: {}", status.isStatusOK());
			
			if (status.isStatusOK()) {
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
				Map<String, String> statesMap = newBulkImportService.getStateMap();
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
						}  else if (key.equals("cdnurList")) {
							summary.setName(mapperConfig.get("Credit_Debit_Note_Unregistered"));
							if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Credit_Debit_Note_Unregistered")) {
								dis = templateMapper.getDiscountConfig().get("Credit_Debit_Note_Unregistered");
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
								WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError,i, i);
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
							for (InvoiceParent invoice : invoices) {
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
									}else  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
									failed++;
									if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
										if(vindex != skipRows - 1) {
											Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
											gstincell.setCellValue("Invalid GSTIN Number");
											gstincell.setCellStyle(style);
											errorIndxes.add(vindex);
										}
									}
								} else{
									if(hsnqtyrate) {
										if (returntype.equals(MasterGSTConstants.GSTR1)
												&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
														|| (key.equals("creditList") && (isEmpty(((GSTR1)invoice).getCdnr())
																|| isEmpty(((GSTR1)invoice).getB2b().get(0).getCtin())
																|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt())
																|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
														|| (key.equals("cdnurList") && (isEmpty(((GSTR1)invoice).getCdnur())
														|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getNtty())))
														|| (key.equals("exportList") && (isEmpty(invoice.getEinvCategory())))
														|| isEmpty(invoice.getStrDate())
														|| isEmpty(invoice.getItems()) 
														|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																&& invoice.getItems().get(0).getIgstrate() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																&& invoice.getItems().get(0).getCgstrate() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																&& invoice.getItems().get(0).getSgstrate() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																&& invoice.getItems().get(0).getCessrate() < 0))) {
											
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = importInvoiceService.einvErrorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
										}  else {
											invoice = newimportService.importEInvoicesHsnQtyRateMandatory(invoice, returntype, branch, vertical, month, year, patterns, statesMap);												
											if(key.equals("invoiceList") || key.equals("exportList")) {
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
									}else {
										if (returntype.equals(MasterGSTConstants.GSTR1)
												&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
												&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
														|| (key.equals("creditList") && (isEmpty(((GSTR1)invoice).getCdnr())
																|| isEmpty(((GSTR1)invoice).getB2b().get(0).getCtin())
																|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt())
																|| isEmpty(((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty())))
														|| (key.equals("cdnurList") && (isEmpty(((GSTR1)invoice).getCdnur())
														|| isEmpty(((GSTR1)invoice).getCdnur().get(0).getNtty())))
														|| (key.equals("exportList") && (isEmpty(invoice.getEinvCategory())))
														|| isEmpty(invoice.getStrDate())
														|| isEmpty(invoice.getItems()) || isEmpty(invoice.getItems().get(0).getHsn())
														|| isEmpty(invoice.getItems().get(0).getQuantity())
														|| (invoice.getItems().get(0).getQuantity() <= 0)
														|| (isNotEmpty(invoice.getItems().get(0).getRateperitem()) 
																&& invoice.getItems().get(0).getRateperitem() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) 
																&& invoice.getItems().get(0).getIgstrate() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) 
																&& invoice.getItems().get(0).getCgstrate() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) 
																&& invoice.getItems().get(0).getSgstrate() < 0)
														|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) 
																&& invoice.getItems().get(0).getCessrate() < 0))) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
												if(vindex != skipRows - 1) {
													String errorVal = importInvoiceService.einvErrorMsg(key,invoice,returntype,hsnqtyrate,"mgst");
													Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
													errorcell.setCellValue(errorVal);
													errorcell.setCellStyle(style);
													errorIndxes.add(vindex);
												}
											}
										} else {
											invoice = newimportService.importEInvoicesHsnQtyRateMandatory(invoice, returntype, branch, vertical, month, year, patterns, statesMap);
											if(key.equals("invoiceList") || key.equals("exportList")) {
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
							}
								index++;
								
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
						summary.setFailed(failed);
						summary.setTotalinvs(totalinvs);
						summary.setInvsuccess(invsucess);
						summary.setInvfailed(totalinvs - invsucess);
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
						session = request.getSession(false);
						if (isNotEmpty(session)) {
							session.setAttribute("custom-bulkimportdetailed_error.xls", convFile);
							flag=true;
							response.setShowLink(true);
							fileSystemResource=new FileSystemResource(convFile);
						}
					} else {
						try {
							convFile.delete();
						} catch (Exception e) {
						}
					}
				}
				String invReturnType = MasterGSTConstants.GSTR1;
				if(isNotEmpty(templateMapper.getMapperType())) {
					if("Sales".equalsIgnoreCase(templateMapper.getMapperType()) || "einvoice".equalsIgnoreCase(templateMapper.getMapperType())) {
						invReturnType = MasterGSTConstants.GSTR1;
					}else {
						invReturnType = MasterGSTConstants.PURCHASE_REGISTER;
					}
				}
				processflag = true;
				newBulkImportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, EXCEL_TEMPLATE,otherconfig);
			} else {
				String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
				task = "INTERRUPTED";
				response.setError(errMsg);
				response.setShowLink(true);
				response.setError(errMsg);
			}
		} catch (Exception e) {
			
			task = "INTERRUPTED";
			String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters/ File are Corrupted, please download latest template and re-import invoices.";
			
			bulkImportTask.setTask(task);
			response.setShowLink(true);
			response.setError(errMsg);
			List<ImportSummary> summary = null;
			
			if(processflag) {
				summary = new ArrayList<ImportSummary>();
				if(isNotEmpty(response.getSummaryList())) {
					summary=response.getSummaryList();
				}
			}
			
			bulkImportTask.setResponse(response);
			bulkImportTaskService.createBulkImportTask(bulkImportTask);
			try {
				fileSystemResource = new FileSystemResource(convert(file));
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			sendBulkImportMails(true, usr, ccmail, fullname, summary, response, fileSystemResource);
			session = request.getSession(false);
			if (isNotEmpty(session) && isNotEmpty(session.getAttribute("custom-bulkimportdetailed_error.xls"))) {
				File errFile = (File) session.getAttribute("custom-bulkimportdetailed_error.xls");
				try {
					errFile.delete();
				} catch (Exception ex) {}
				session.removeAttribute("custom-bulkimportdetailed_error.xls");
			}
			mailflag = true;
			response.setError(errMsg);
			logger.error(CLASSNAME + method + " ERROR", e);
		}
	}
	
	List<ImportSummary> summary=new ArrayList<ImportSummary>();
	if(isNotEmpty(response.getSummaryList())) {
		summary=response.getSummaryList();
	}
	bulkImportTask.setTask(task);
	bulkImportTask.setResponse(response);
	bulkImportTaskService.createBulkImportTask(bulkImportTask);
	
	if(!mailflag){
		sendBulkImportMails(flag, usr, ccmail, fullname, summary, response, fileSystemResource);
		session = request.getSession(false);
		if (isNotEmpty(session) && isNotEmpty(session.getAttribute("custom-bulkimportdetailed_error.xls"))) {
			File errFile = (File) session.getAttribute("custom-bulkimportdetailed_error.xls");
			try {
				errFile.delete();
			} catch (Exception e) {}
			session.removeAttribute("custom-bulkimportdetailed_error.xls");
		}
	}
	
}

@Async
public void bulkGstr1Offline(String id, String fullname, String clientid, String returntype, String tallytype, MultipartFile file,int month, int year,String branch, String vertical, 
		List<String> list, String btallyfinancialYear, String btallymonth, ModelMap model, HttpServletRequest request, BulkImportTask bulkImportTask, String ccmail) throws MasterGSTException {		
	final String method = "bulkTallyImports::";
	logger.debug(CLASSNAME + method + BEGIN);
	int skipRows = 4;
	boolean flag=false;
	FileSystemResource fileSystemResource=null;
	BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
	ImportResponse response = new ImportResponse();
	List<ImportSummary> summaryList = Lists.newArrayList();
	String task = "INTERRUPTED";
	User usr=userService.findById(id);

	boolean mailflag = false;
	boolean processflag = false;
	HttpSession session = null;
	if (!file.isEmpty()) {
		logger.debug(CLASSNAME + method + file.getOriginalFilename());
		try {
			task = "COMPLETE";
			
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
			beans.put("docSummaryList", sheetMap.get("docSummaryList"));
			
			beans.put("b2baList", sheetMap.get("b2baList"));
			beans.put("b2csaList", sheetMap.get("b2csaList"));
			beans.put("b2claList", sheetMap.get("b2claList"));
			beans.put("cdnraList", sheetMap.get("cdnraList"));
			beans.put("cdnuraList", sheetMap.get("cdnuraList"));
			beans.put("expaList", sheetMap.get("expaList"));
			beans.put("ataList", sheetMap.get("ataList"));
			beans.put("txpaList", sheetMap.get("txpaList"));
			
			XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
			HSNDetails hsnSummary = new HSNDetails();
			GSTR1DocumentIssue documentIssue = new GSTR1DocumentIssue();
			logger.debug(CLASSNAME + method + "Status: {}", status.isStatusOK());
			if (status.isStatusOK()) {
				Client client = clientService.findById(clientid);
				OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
				Map<String, String> statesMap = newBulkImportService.getStateMap();
				Map<String,StateConfig> states = newBulkImportService.getStatesMap();
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
						} else if (key.equals("docSummaryList")) {
							summary.setName("docs");
						} else if (key.equals("b2baList")) {
							summary.setName("b2ba");
						}  else if (key.equals("b2csaList")) {
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
							List<Integer> errorIndxes = Lists.newArrayList();
							if("hsnSummaryList".equalsIgnoreCase(key)) {
								List<HSNData>  hsnData= Lists.newArrayList();
								int i=1;
								for (InvoiceParent invoice : invoices) {
									if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0))) {
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
									if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
										
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
										
									}else if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())
											&& !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().toUpperCase())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
												&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != skipRows - 1) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Invalid GSTIN Number");
												gstincell.setCellStyle(style);
											}
											errorIndxes.add(index);
										}
									} else if (returntype.equals(MasterGSTConstants.GSTR1) && (key.equals("creditList") || key.equals("cdnraList") || key.equals("cdnuraList") || key.equals("cdnurList") || key.equals("exportList")  || key.equals("expaList"))
											&& (isEmpty(invoice.getStrDate())
													|| ((key.equals("creditList") || key.equals("cdnraList")) && (isEmpty(((GSTR1) invoice).getCdnr()) || isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt()) || isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())))
													|| ((key.equals("cdnurList") || key.equals("cdnuraList")) && (isEmpty(((GSTR1) invoice).getCdnur()) || isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
													|| ((key.equals("exportList") || key.equals("expaList")) && (isEmpty(invoice.getExp()) || isEmpty(invoice.getExp().get(0).getExpTyp())))
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
								} else if (returntype.equals(MasterGSTConstants.GSTR4)
											&& (key.equals("invoiceList") || key.equals("creditList")
													|| key.equals("cdnurList") || key.equals("exportList"))
											&& (isEmpty(invoice.getStrDate())
													|| (key.equals("creditList") && (isEmpty(
															((GSTR4) invoice).getCdnr())
															|| isEmpty(((GSTR4) invoice).getB2b().get(0).getCtin())
															|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt())
															|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0)
																	.getNtNum())
															|| isEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0)
																	.getNtty())))
													|| (key.equals("cdnurList") && (isEmpty(
															((GSTR4) invoice).getCdnur())
															|| isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtNum())
															|| isEmpty(((GSTR4) invoice).getCdnur().get(0).getNtty())))
													|| (key.equals("invoiceList") && (isEmpty(invoice.getB2b())
															|| isEmpty(invoice.getB2b().get(0).getCtin())))
													|| (key.equals("exportList") && (isEmpty(invoice.getExp())
															|| isEmpty(invoice.getExp().get(0).getExpTyp())))
													|| isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems()))) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
												&& isNotEmpty(datatypeSheet.getRow(index))) {
											datatypeSheet.getRow(index).createCell(lastcolumn).setCellValue("Please Enter Valid Values");
											errorIndxes.add(index);
										}
									}else if(key.equals("b2cList") && (isEmpty(invoice.getStatename()) || "Place Of Supply".equalsIgnoreCase(invoice.getStatename())   )) {
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
										if (isNotEmpty(datatypeSheet)
												&& ((index) < datatypeSheet.getLastRowNum())
												&& isNotEmpty(datatypeSheet.getRow(index))) {
											String errorVal = newBulkImportService.tallyErrorMsg(key,invoice,returntype);
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
									} else if ((key.equals("ataList") || key.equals("txpaList")) && (isEmpty(invoice.getStatename()) || "Original Place Of Supply".equalsIgnoreCase(invoice.getStatename()))) {
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
										
										invoice = newBulkImportService.importInvoicesTally(invoice, client, returntype, branch, vertical,month,year,patterns,statesMap);												
										
										if(key.equals("b2cList") || key.equals("b2csaList")) {
											if(isNotEmpty(btallyfinancialYear) && isNotEmpty(btallymonth)) {
												Calendar cal = Calendar.getInstance();
												int b2ctmnth = Integer.parseInt(btallymonth);
												int b2cyear = Integer.parseInt(btallyfinancialYear);
												if(b2ctmnth >= 1 && b2ctmnth < 4) {
													b2cyear = b2cyear+1;
												}
												cal.set(b2cyear, b2ctmnth - 1, 1);
												invoice.setDateofinvoice(cal.getTime());
											}
										}else if(key.equals("nilList")) {
											if(isNotEmpty(btallyfinancialYear) && isNotEmpty(btallymonth)) {
												Calendar cal = Calendar.getInstance();
												int nilmnth = Integer.parseInt(btallymonth);
												int nilyear = Integer.parseInt(btallyfinancialYear);
												if(nilmnth >= 1 && nilmnth < 4) {
													nilyear = nilyear+1;
												}
												cal.set(nilyear, nilmnth - 1, 1);
												invoice.setDateofinvoice(cal.getTime());
											}
										}
										if(key.equalsIgnoreCase("creditList") || key.equalsIgnoreCase("cndraList")) {
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
										if(key.equals("b2cList") || key.equals("b2csaList")) {
											if(isNotEmpty(btallyfinancialYear) && isNotEmpty(btallymonth)) {
												Calendar cal = Calendar.getInstance();
												int b2ctmnth = Integer.parseInt(btallymonth);
												int b2cyear = Integer.parseInt(btallyfinancialYear);
												if(b2ctmnth >= 1 && b2ctmnth < 4) {
													b2cyear = b2cyear+1;
												}
											cal.set(b2cyear, b2ctmnth - 1, 1);
											invoice.setDateofinvoice(cal.getTime());
											}
										}else if(key.equals("nilList")) {
											if(isNotEmpty(btallyfinancialYear) && isNotEmpty(btallymonth)) {
												Calendar cal = Calendar.getInstance();
												int nilmnth = Integer.parseInt(btallymonth);
												int nilyear = Integer.parseInt(btallyfinancialYear);
												if(nilmnth >= 1 && nilmnth < 4) {
													nilyear = nilyear+1;
												}
												cal.set(nilyear, nilmnth - 1, 1);
												invoice.setDateofinvoice(cal.getTime());
											}
										}if(key.equalsIgnoreCase("creditList") || key.equalsIgnoreCase("cdnraList")) {
											if(isNotEmpty(invoice.getStatename())) {
												if (isNotEmpty(((GSTR1) invoice).getCdnr()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0)) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt())  && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0))) {
													String pos = getStateCode(invoice.getStatename());
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
															String pos = getStateCode(invoice.getStatename());
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
										filteredList.add(invoice);
										if(!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("b2cList") && !key.equals("ataList") && !key.equals("txpaList") && !key.equals("b2csaList") && !key.equals("nilList")) {
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
						if(key.equals("advReceiptList") || key.equals("advAdjustedList") || key.equals("b2cList") || key.equals("ataList") || key.equals("txpaList") || key.equals("b2csaList")  || key.equals("nilList") || key.equals("hsnSummaryList") || key.equals("docSummaryList")) {
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
						if(isNotEmpty(invoice)) {
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
						session = request.getSession(false);
						if (isNotEmpty(session)) {
							session.setAttribute("bulkgstr1offlineutility_error", convFile);
							flag=true;
							response.setShowLink(true);
							fileSystemResource=new FileSystemResource(convFile);
						}
					}
				}
				
				processflag = true;
				newBulkImportService.updateExcelData(beans, list, returntype, id, fullname, clientid, EXCEL_TEMPLATE,otherconfig);
			} else {
				String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
				task = "INTERRUPTED";
				response.setError(errMsg);
				response.setShowLink(true);
			}
		} catch (Exception e) {
			task = "INTERRUPTED";
			//String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
			
			String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters/ File are Corrupted, please download latest template and re-import invoices.";
			
			bulkImportTask.setTask(task);
			response.setShowLink(true);
			response.setError(errMsg);
			List<ImportSummary> summary = null;
			
			if(processflag) {
				summary = new ArrayList<ImportSummary>();
				if(isNotEmpty(response.getSummaryList())) {
					summary=response.getSummaryList();
				}
			}
			
			bulkImportTask.setResponse(response);
			bulkImportTaskService.createBulkImportTask(bulkImportTask);
			try {
				fileSystemResource = new FileSystemResource(convert(file));
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			sendBulkImportMails(true, usr, ccmail, fullname, summary, response, fileSystemResource);
			session = request.getSession(false);
			if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulkgstr1offlineutility_error.xls"))) {
				File errFile = (File) session.getAttribute("bulkgstr1offlineutility_error.xls");
				try {
					errFile.delete();
				} catch (Exception ex) {}
				session.removeAttribute("bulkgstr1offlineutility_error.xls");
			}
			mailflag = true;
			logger.error(CLASSNAME + method + " ERROR", e);
		}
	}
	
	List<ImportSummary> summary=new ArrayList<ImportSummary>();
	if(isNotEmpty(response.getSummaryList())) {
		summary=response.getSummaryList();
	}
	bulkImportTask.setTask(task);
	bulkImportTask.setResponse(response);
	bulkImportTaskService.createBulkImportTask(bulkImportTask);
	if(!mailflag){
		sendBulkImportMails(flag, usr, ccmail, fullname, summary, response, fileSystemResource);
		session = request.getSession(false);
		if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulkgstr1offlineutility_error.xls"))) {
			File errFile = (File) session.getAttribute("bulkgstr1offlineutility_error.xls");
			try {
				errFile.delete();
			} catch (Exception e) {}
			session.removeAttribute("bulkgstr1offlineutility_error.xls");
		}
	}
}

@Async
public void bulkWalterImports(String id, String fullname, String clientid, String returntype, MultipartFile file,int month, int year,String branch, String vertical, 
		List<String> list, ModelMap model, HttpServletRequest request, BulkImportTask bulkImportTask, String ccmail) throws MasterGSTException {		
	final String method = "bulkImports::";
	logger.debug(CLASSNAME + method + BEGIN);
	Client client = clientService.findById(clientid);
	String stateName = client.getStatename();
	int skipRows = 1;
	OtherConfigurations otherconfig = clientService.getOtherConfig(clientid);
	if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnableSalesFields())) {
		otherconfig.setEnableSalesFields(false);
	}
	if(isNotEmpty(otherconfig) && isEmpty(otherconfig.isEnablePurFields())) {
		otherconfig.setEnablePurFields(false);
	}
	String task = "INTERRUPTED";
	User usr=userService.findById(id);
	boolean mailflag = false;
	boolean processflag = false;
	HttpSession session = null;
	boolean hsnqtyrate = false;
	if(isNotEmpty(otherconfig)) {
		if("GSTR1".equals(returntype) || "Sales Register".equals(returntype) || "SalesRegister".equals(returntype)) {
			hsnqtyrate = otherconfig.isEnableSalesFields();
		}else {
			hsnqtyrate = otherconfig.isEnablePurFields();
		}
	}
	boolean flag=false;
	FileSystemResource fileSystemResource=null;
	
	BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
	ImportResponse response = new ImportResponse();
	List<ImportSummary> summaryList = Lists.newArrayList();
	if (!file.isEmpty()) {
		task = "COMPLETE";
		logger.debug(CLASSNAME + method + file.getOriginalFilename());
		try {
			String xmlPath = "classpath:mastergst_purchase_excel_walterpack_config.xml";
			if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
				xmlPath = "classpath:mastergst_purchase_excel_walterpack_config.xml";
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
			logger.info(CLASSNAME + method + "Status: {}", status.isStatusOK());
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
							summary.setName("Sheet1");
						} else {
							summary.setName(key);
						}
						Sheet datatypeSheet = workbook.getSheet(summary.getName());
						Sheet datatypeSheetError = workbookError.getSheet(summary.getName());
						CellStyle style = workbook.createCellStyle();
					    Font font = workbook.createFont();
			            font.setColor(IndexedColors.RED.getIndex());
			            style.setFont(font);
					    Cell cell = datatypeSheet.getRow(0).createCell(datatypeSheet.getRow(0).getLastCellNum());
					    cell.setCellValue("Errors");
					    cell.setCellStyle(style);
						int lastcolumn = datatypeSheet.getRow(0).getLastCellNum()-1;
						if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
				    		WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, 0, 0);
						}else {
							WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, 0, 0);
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
							summary.setTotal(beans.get(key).size());
							List<Integer> errorIndxes = Lists.newArrayList();
							for (InvoiceParent invoice : invoices) {
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
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Invalid Invoice Number Format");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
									
								}else if(isEmpty(invoice.getInvtype())) {
									failed++;
									if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter Invoice Type");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										errorIndxes.add(index);
									}
								}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isNotEmpty(invoice.getItems().get(0).getCgstrate()) && !useList(csgstrate,invoice.getItems().get(0).getCgstrate())) {
									failed++;
									if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter Valid Tax Rate");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
									}
							}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isNotEmpty(invoice.getItems().get(0).getSgstrate()) && !useList(csgstrate,invoice.getItems().get(0).getSgstrate())) {
								failed++;
								if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
										if(index != 0) {
											Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
											gstincell.setCellValue("Please Enter Valid Tax Rate");
											gstincell.setCellStyle(style);
											errorIndxes.add(index);
										}
								}
							}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isNotEmpty(invoice.getItems().get(0).getIgstrate()) && !useList(igstrate,invoice.getItems().get(0).getIgstrate())) {
								failed++;
								if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
										if(index != 0) {
											Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
											gstincell.setCellValue("Please Enter Valid Tax Rate");
											gstincell.setCellStyle(style);
											errorIndxes.add(index);
										}
								}
							}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isNotEmpty(invoice.getItems().get(0).getUgstrate()) && !useList(csgstrate,invoice.getItems().get(0).getUgstrate())) {
								failed++;
								if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
										if(index != 0) {
											Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
											gstincell.setCellValue("Please Enter Valid Tax Rate");
											gstincell.setCellStyle(style);
											errorIndxes.add(index);
										}
								}
							}else if(invoice.getInvtype().equalsIgnoreCase("B2B")) {
									if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 0) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid GSTIN Number");
													gstincell.setCellStyle(style);
													errorIndxes.add(index);
												}
										}
									}else if((returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) && isEmpty(invoice.getB2b().get(0).getCtin())){
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 0) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Please Enter GSTIN Number");
													gstincell.setCellStyle(style);
													errorIndxes.add(index);
												}
										}
									}else if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter Valid Values");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
									}else {
										invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"singlesheet");
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
								}else if(invoice.getInvtype().equalsIgnoreCase("B2B_Unregistered")) {
									if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 1) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter Valid Values");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
									}else {
										invoice.setInvtype("B2B Unregistered");
										invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"singlesheet");
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
									if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 0) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid GSTIN Number");
													gstincell.setCellStyle(style);
													errorIndxes.add(index);
												}
										}
									}else if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter Valid Values");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
									}else {
										if(isNotEmpty(invoice.getB2b().get(0).getCtin())) {
											invoice.setInvtype(MasterGSTConstants.CREDIT_DEBIT_NOTES);
											if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
											invoice.getCdn().get(0).getNt().get(0).setNtty("C");
											invoice.getCdn().get(0).getNt().get(0).setpGst("N");
											invoice.getCdn().get(0).getNt().get(0).setRsn("Others");
												/*try {
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
												}*/
											}else {
												((GSTR1)invoice).getCdnr().get(0).getNt().get(0).setNtty("C");
												((GSTR1)invoice).getCdnr().get(0).getNt().get(0).setpGst("N");
												((GSTR1)invoice).getCdnr().get(0).getNt().get(0).setRsn("Others");
												/*try {
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
												}*/
											}
										}else {
											invoice.setInvtype(MasterGSTConstants.CDNUR);
											invoice.getCdnur().get(0).setNtty("C");
											invoice.getCdnur().get(0).setpGst("N");
											invoice.getCdnur().get(0).setRsn("Others");
											/*if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
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
											}*/
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
										invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"singlesheet");
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
									if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 0) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid GSTIN Number");
													gstincell.setCellStyle(style);
													errorIndxes.add(index);
												}
										}
									}else if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter Valid Values");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
									}else {
										if(isNotEmpty(invoice.getB2b().get(0).getCtin())) {
											invoice.setInvtype(MasterGSTConstants.CREDIT_DEBIT_NOTES);
											if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
											invoice.getCdn().get(0).getNt().get(0).setNtty("D");
											invoice.getCdn().get(0).getNt().get(0).setpGst("N");
											invoice.getCdn().get(0).getNt().get(0).setRsn("Others");
												/*try {
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
												}*/
											}else {
												((GSTR1)invoice).getCdnr().get(0).getNt().get(0).setNtty("D");
												((GSTR1)invoice).getCdnr().get(0).getNt().get(0).setpGst("N");
												((GSTR1)invoice).getCdnr().get(0).getNt().get(0).setRsn("Others");
												/*try {
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
												}*/
											}
										}else {
											invoice.setInvtype(MasterGSTConstants.CDNUR);
											invoice.getCdnur().get(0).setNtty("D");
											invoice.getCdnur().get(0).setpGst("N");
											invoice.getCdnur().get(0).setRsn("Others");
											/*if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
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
											}*/
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
										
										invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"singlesheet");
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
								}else if(invoice.getInvtype().equalsIgnoreCase("Imported_Goods")) {
									if(isEmpty(invoice.getCategorytype())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter SEZ Type");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
									}else if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(IMPGINVOICENO_REGEX, invnum.trim())){
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Invalid Invoice Number Format");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
									}else if(isNotEmpty(invoice.getCategorytype()) && "Y".equalsIgnoreCase(invoice.getCategorytype()) && isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())
											&& !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 0) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid GSTIN Number");
													gstincell.setCellStyle(style);
													errorIndxes.add(index);
												}
											}
									}else if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter Valid Values");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
									}else {
										invoice.setInvtype(MasterGSTConstants.IMP_GOODS);
										invoice.setStatename(client.getStatename());
										((PurchaseRegister)invoice).getImpGoods().get(0).setIsSez(invoice.getCategorytype().toUpperCase());
										if("Y".equalsIgnoreCase(invoice.getCategorytype())) {
											((PurchaseRegister)invoice).getImpGoods().get(0).setStin(invoice.getB2b().get(0).getCtin());
											
										}
										invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"singlesheet");
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
								}else if(invoice.getInvtype().equalsIgnoreCase("Imported_Services")) {
									if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter Valid Values");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
									}else {
										invoice.setInvtype(MasterGSTConstants.IMP_SERVICES);
										invoice.setStatename(client.getStatename());
										invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"singlesheet");
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
								}else if(invoice.getInvtype().equalsIgnoreCase("Nil_Exempted_NonGST")) {
									if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter Valid Values");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
									}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isEmpty(invoice.getItems().get(0).getType())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
												&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter Supply Type");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
									}else {
										invoice.setInvtype(MasterGSTConstants.NIL);
										if (isNotEmpty(invoice.getItems().get(0).getType())) {
											String invTyp = invoice.getItems().get(0).getType();
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
										invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"singlesheet");
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
								}else if(invoice.getInvtype().equalsIgnoreCase("ITC_Reversal")) {
									if(isEmpty(invoice.getStrDate()) || isEmpty(invoice.getItems())){
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())	&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter Valid Values");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
									}else if(isNotEmpty(invoice.getItems()) && isNotEmpty(invoice.getItems().get(0)) && isEmpty(invoice.getItems().get(0).getItcRevtype())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
												&& isNotEmpty(datatypeSheet.getRow(index))) {
											if(index != 0) {
												Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
												gstincell.setCellValue("Please Enter ITC Reversal Type");
												gstincell.setCellStyle(style);
												errorIndxes.add(index);
											}
										}
									}else {
										invoice.setInvtype(MasterGSTConstants.ITC_REVERSAL);
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
										if (returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.GSTR2)) {
											invoice.setStatename(client.getStatename());
										}
										invoice = importSageService.getInvoice(client,invoice,patterns,year,month,returntype,statetin,"singlesheet");
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
							int eC = 1;
							for (int i = 1; i <= datatypeSheet.getLastRowNum(); i++) {
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
						summary.setFailed(failed);
						summary.setTotalinvs(totalinvs);
						summary.setInvsuccess(invsucess);
						summary.setInvfailed(totalinvs - invsucess);
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
						if (summary.getInvfailed() > 0) {
							failedCount += summary.getInvfailed();
						}
					}
					if (failedCount > 0 && isNotEmpty(convFile)) {
						session = request.getSession(false);
						if (isNotEmpty(session)) {
							session.setAttribute("bulksageimport_error.xls", convFile);
							flag=true;
							response.setShowLink(true);
							fileSystemResource=new FileSystemResource(convFile);
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
				processflag = true;
				newBulkImportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, EXCEL_TEMPLATE,otherconfig);
			} else {
				String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.";
				task = "INTERRUPTED";
				response.setError(errMsg);
				response.setShowLink(true);
			}
		} catch (Exception e) {
			task = "INTERRUPTED";
			String errMsg = "Sorry..!!! We have noticed changes in the Template Parameters/ File are Corrupted, please download latest template and re-import invoices.";
			
			bulkImportTask.setTask(task);
			response.setShowLink(true);
			response.setError(errMsg);
			List<ImportSummary> summary = null;
			
			if(processflag) {
				summary = new ArrayList<ImportSummary>();
				if(isNotEmpty(response.getSummaryList())) {
					summary=response.getSummaryList();
				}
			}
			
			bulkImportTask.setResponse(response);
			bulkImportTaskService.createBulkImportTask(bulkImportTask);
			try {
				fileSystemResource = new FileSystemResource(convert(file));
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			sendBulkImportMails(true, usr, ccmail, fullname, summary, response, fileSystemResource);
			session = request.getSession(false);
			if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulksageimport_error.xls"))) {
				File errFile = (File) session.getAttribute("bulksageimport_error.xls");
				try {
					errFile.delete();
				} catch (Exception ex) {}
				session.removeAttribute("bulksageimport_error.xls");
			}
			mailflag = true;
			logger.error(CLASSNAME + method + " ERROR", e);
		}
	}
	
	List<ImportSummary> summary=new ArrayList<ImportSummary>();
	if(isNotEmpty(response.getSummaryList())) {
		summary=response.getSummaryList();
	}
	bulkImportTask.setTask(task);
	bulkImportTask.setResponse(response);
	bulkImportTaskService.createBulkImportTask(bulkImportTask);
	if(!mailflag){
		sendBulkImportMails(flag, usr, ccmail, fullname, summary, response, fileSystemResource);
		session = request.getSession(false);
		if (isNotEmpty(session) && isNotEmpty(session.getAttribute("bulksageimport_error.xls"))) {
			File errFile = (File) session.getAttribute("bulksageimport_error.xls");
			try {
				errFile.delete();
			} catch (Exception e) {}
			session.removeAttribute("bulksageimport_error.xls");
		}
	}

	
}


}
