package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.EXCEL_TEMPLATE;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONObject;
import org.jxls.reader.ReaderBuilder;
import org.jxls.reader.ReaderConfig;
import org.jxls.reader.XLSReadStatus;
import org.jxls.reader.XLSReader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ibm.icu.text.NumberFormat;
import com.mastergst.configuration.service.ErrorCodeConfig;
import com.mastergst.configuration.service.ErrorCodesRepository;
import com.mastergst.core.common.AuditLogConstants;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.audit.AuditlogService;
import com.mastergst.usermanagement.runtime.domain.Branch;
import com.mastergst.usermanagement.runtime.domain.CancelEwayBill;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.EBillVehicleListDetails;
import com.mastergst.usermanagement.runtime.domain.EWAYBILL;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.ImportResponse;
import com.mastergst.usermanagement.runtime.domain.ImportSummary;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.TransPublicData;
import com.mastergst.usermanagement.runtime.domain.Vertical;
import com.mastergst.usermanagement.runtime.domain.ewaybill.ExtendValidity;
import com.mastergst.usermanagement.runtime.domain.ewaybill.ExtendValidityDO;
import com.mastergst.usermanagement.runtime.repository.EwayBillRepository;
import com.mastergst.usermanagement.runtime.repository.TransinRepository;
import com.mastergst.usermanagement.runtime.response.EwayBillCancelResponse;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.ResponseData;
import com.mastergst.usermanagement.runtime.response.TransGSTINResponse;
import com.mastergst.usermanagement.runtime.response.TransGSTINResponseData;
import com.mastergst.usermanagement.runtime.response.ewaybill.EwayBillRejectResponse;
import com.mastergst.usermanagement.runtime.response.ewaybill.ExtendValidityResponse;
import com.mastergst.usermanagement.runtime.response.ewaybill.ExtendValidityResponseData;
import com.mastergst.usermanagement.runtime.service.BulkImportTaskService;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.EwaybillService;
import com.mastergst.usermanagement.runtime.service.IHubConsumerService;
import com.mastergst.usermanagement.runtime.service.ImportInvoiceService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.ReportsService;

@Controller
public class EwayBillController {
	private static final Logger logger = LogManager.getLogger(EwayBillController.class.getName());
	private static final String CLASSNAME = "EwayBillController::";
	private String gstnFormat = "[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}|[0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1}|[0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1}|[0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1}|[9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1}";
	 private static DecimalFormat df2 = new DecimalFormat("#.##");
	 private static final String INVOICENO_REGEX ="^[0-9a-zA-Z/-]{1,16}$";
	@Autowired
	private ClientService clientService;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private UserService userService;
	@Autowired
	private AuditlogService auditlogService;
	@Autowired
    private ResourceLoader resourceLoader;
	@Autowired
	private ImportInvoiceService importInvoiceService;
	@Autowired
	private BulkImportTaskService bulkImportTaskService; 
	@Autowired
	private TransinRepository transinRepository;
	@Autowired
	private IHubConsumerService iHubConsumerService;
	@Autowired
	private ErrorCodesRepository errorCodesRepository;
	@Autowired
	private EwayBillRepository ewayBillRepository;
	@Autowired
	private EwaybillService ewaybillService;
	
	private  boolean isScientificNotation(String numberString) {
	    try {
	        new BigDecimal(numberString);
	    } catch (NumberFormatException e) {
	        return false;
	    }
	    return numberString.toUpperCase().contains("E") && numberString.charAt(1)=='.';   
	}
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	@RequestMapping(value = "/cp_ClientsEwaybillReportsGroupData/{returntype}/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String userClientReportsGroupDataEwaybill(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable  String returntype,@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
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
		return "profile/userClientGroupReportsEwaybill";
	
	}
	private Map<String, List<InvoiceParent>> getEwaybillExcelSheetMap() {
		Map<String, List<InvoiceParent>> sheetMap = Maps.newHashMap();
		List<InvoiceParent> b2bList = Lists.newArrayList();
		List<InvoiceParent> invoiceList = Lists.newArrayList();
		sheetMap.put("b2bList", b2bList);
		sheetMap.put("invoiceList", invoiceList);
		return sheetMap;
	}
	@RequestMapping(value = "/uploadebillInvoice/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody ImportResponse uploadEInvoice(@PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("file") MultipartFile file,
			@RequestParam(value = "branch", required = false) String branch,
			@RequestParam(value = "vertical", required = false) String vertical,
			@RequestParam("list") List<String> list, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "uploadInvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		if (!file.isEmpty()) {
			logger.debug(CLASSNAME + method + file.getOriginalFilename());
			try {
				String xmlPath = "classpath:mastergst_ewaybill_excel_config.xml";
				Resource config = resourceLoader.getResource(xmlPath);
				DateConverter converter = new DateConverter();
				String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd","dd-MM-yy","dd-MMM-yy","dd/MM/yy","dd/MMM/yy"};
				converter.setPatterns(patterns);
				ConvertUtils.register(converter, Date.class);
				ReaderConfig.getInstance().setSkipErrors(true);
				XLSReader xlsReader = ReaderBuilder.buildFromXML(config.getInputStream());
				// JxlsHelper.getInstance().setProcessFormulas(false);
				Map<String, List<InvoiceParent>> sheetMap = getEwaybillExcelSheetMap();
				Map<String, List<InvoiceParent>> beans = Maps.newHashMap();
				beans.put("invoiceList", sheetMap.get("invoiceList"));
				
				XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
				logger.debug(CLASSNAME + method + "Status: {}", status.isStatusOK());
				if (status.isStatusOK()) {
					Workbook workbook = WorkbookFactory.create(file.getInputStream());
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
					File convFile = new File("MasterGST_Import_Errors_" + dateFormat.format(new Date()) + ".xls");
					convFile.createNewFile();
					FileOutputStream fos = new FileOutputStream(convFile);
					for (String key : beans.keySet()) {
						if (isNotEmpty(sheetMap.get(key)) && sheetMap.get(key).size() > 0) {
							ImportSummary summary = new ImportSummary();
							if (key.equals("invoiceList")) {
								summary.setName("Invoices");
							} else {
								summary.setName(key);
							}
							Sheet datatypeSheet = workbook.getSheet(summary.getName());
							CellStyle style = workbook.createCellStyle();
						    Font font = workbook.createFont();
				            font.setColor(IndexedColors.RED.getIndex());
				            style.setFont(font);
						    Cell cell = datatypeSheet.getRow(1).createCell(datatypeSheet.getRow(1).getLastCellNum());
						    cell.setCellValue("Errors");
						    cell.setCellStyle(style);
							int lastcolumn = datatypeSheet.getRow(1).getLastCellNum()-1;
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
								List<Row> errorRows = Lists.newArrayList();
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
									if ((isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(INVOICENO_REGEX, invnum.trim()))) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) <= datatypeSheet.getLastRowNum())
												&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid Invoice Number Format");
													gstincell.setCellStyle(style);
												}
												errorRows.add(datatypeSheet.getRow(index));
										}
									
								}else if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((index) < datatypeSheet.getLastRowNum())
												&& isNotEmpty(datatypeSheet.getRow(index))) {
												if(index != 1) {
													Cell gstincell = datatypeSheet.getRow(index).createCell(lastcolumn);
													gstincell.setCellValue("Invalid GSTIN Number");
													gstincell.setCellStyle(style);
												}
											errorRows.add(datatypeSheet.getRow(index));
										}
									} else {
												invoice = importInvoiceService.importEwaybillInvoices(invoice,returntype,branch,vertical,month,year,patterns,client);
												if(isNotEmpty(invoice.getItems().get(0))) {
													Item item = invoice.getItems().get(0); 
													item = bulkImportTaskService.changeInvoiceAmounts(item);
													List<Item> itms = Lists.newArrayList();
													itms.add(item);
													invoice.setItems(itms);
												}
												if(isNotEmpty(invoice.getVehiclListDetails().get(0))) {
													EBillVehicleListDetails vehicle = invoice.getVehiclListDetails().get(0); 
													List<EBillVehicleListDetails> vehicles = Lists.newArrayList();
													vehicles.add(vehicle);
													invoice.setVehiclListDetails(vehicles);
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
									index++;
								}
								if (failed != 0) {
									failed = failed - 1;
								}
								summary.setSuccess((beans.get(key).size() - 1) - (failed));
								for (int i = 2; i < datatypeSheet.getLastRowNum(); i++) {
									if (!errorRows.contains(datatypeSheet.getRow(i))) {
										datatypeSheet.removeRow(datatypeSheet.getRow(i));
									}
								}
								for (int i = 1; i < datatypeSheet.getLastRowNum(); i++) {
									if (isEmpty(datatypeSheet.getRow(i)) || (isEmpty(datatypeSheet.getRow(i).getCell(0))
											&& isEmpty(datatypeSheet.getRow(i).getCell(1)))) {
										datatypeSheet.shiftRows(i + 1, datatypeSheet.getLastRowNum(), -1);
										i--;
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
						workbook.write(fos);
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
	
	
	@RequestMapping(value = "/transGSTINsearch/{userid}/{clientid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TransGSTINResponse transGSTINsearch(@PathVariable("userid") String userid,@PathVariable("clientid") String clientid, @RequestParam("transin") String transin, HttpServletRequest request) throws UnknownHostException {
		Client client = clientService.findById(clientid);
		ErrorCodeConfig errors=null;String desc="";
		TransGSTINResponse transResponse = new TransGSTINResponse();
		TransPublicData transdata = transinRepository.findByTransin(transin);
		String name = "";
		if(isNotEmpty(transdata)) {
			TransGSTINResponseData data = new TransGSTINResponseData();
			BeanUtils.copyProperties(transdata, data);
			transResponse.setStatuscd(MasterGSTConstants.SUCCESS_CODE);
			transResponse.setData(data);
			//name = (transResponse.getData().getTradeName()) == null ? transResponse.getData().getLegalName() : transResponse.getData().getTradeName();
			//return name;
			return transResponse;
		}else {
			if(isNotEmpty(client) && isNotEmpty(client.getGstnnumber())) {
				TransGSTINResponse response = iHubConsumerService.getTransinDetails(transin, client.getGstnnumber(), InetAddress.getLocalHost().getHostAddress());
				logger.info(response.getData().getTransin());
				if (isNotEmpty(response) && isNotEmpty(response.getData()) && isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
					
					CompletableFuture<TransGSTINResponse> future = CompletableFuture.supplyAsync(() -> response);
					future.thenAcceptAsync(responseObj -> {
						try {
							transinRepository.deleteByTransinAndUseridIsNull(transin);
						} catch(Exception e) {}
						TransPublicData publicData = new TransPublicData();
						BeanUtils.copyProperties(response.getData(), publicData);
						publicData.setCreatedDate(new Date());
						publicData.setUserid(userid);
						try {
							publicData.setIpAddress(InetAddress.getLocalHost().getHostAddress());
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}
						transinRepository.save(publicData);
					});
				}else if(NullUtil.isNotEmpty(response) && NullUtil.isNotEmpty(response.getError()) && NullUtil.isNotEmpty(response.getError().getMessage())) {
					String jsonStr="";
					jsonStr = response.getError().getMessage();
					if(jsonStr.contains("JSON validation failed due to")) {
						desc =jsonStr; 
					}else {
				        JSONObject jsonObj = new JSONObject(jsonStr);
				        String code = jsonObj.getString("errorCodes");
				        String[] cd=code.split(",");
				        for(int i=0;i<cd.length;i++) {
				        	errors = errorCodesRepository.findByErrorCode(cd[i]);
				        	if(isNotEmpty(errors) && isNotEmpty(errors.getErrorDesc())) {
				        		desc=desc+", "+errors.getErrorDesc();
				        	}
				        }
					}
					response.setStatusdesc(desc);
				}
				return response; 
			}else {
				transResponse.setStatuscd("0");
				return transResponse;
			}
		}
	}
	@RequestMapping(value = "/extendeBilllinv/{id}/{clientid}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody String extendEwayBillInvoice(@RequestBody ExtendValidityDO ebillData ,@PathVariable("id") String id,@PathVariable("clientid") String clientid, @PathVariable("month") int month,@PathVariable("year") int year,
			ModelMap model) throws Exception {
		final String method = "extendEwayBillInvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		ExtendValidity evalidity = null;
		ErrorCodeConfig errors=null;
		String desc="";
		EWAYBILL ewaybill = null;
		for(String ewaybillid : ebillData.getInvoiceIds()) {
			ewaybill = ewayBillRepository.findById(ewaybillid);
			if(isNotEmpty(ewaybill)) {
				evalidity = ewaybillService.extendValidityData(client, ewaybill, ebillData);
				ExtendValidityResponse response = iHubConsumerService.extendEwaybillValidity(client.getGstnnumber(), InetAddress.getLocalHost().getHostAddress(), evalidity);
				if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
					auditlogService.saveAuditLog(id, clientid,ewaybill.getInvoiceno(),AuditLogConstants.EXTENDVALIDITY,MasterGSTConstants.EWAYBILL,null,null);
					ExtendValidityDO extendValidity = new ExtendValidityDO();
					ExtendValidityResponseData data =  response.getData();
					if(isNotEmpty(data)) {
						if(isNotEmpty(data.getValidUpto())) {
							ewaybill.setValidUpto(data.getValidUpto());
						}
					}
					if(isNotEmpty(ebillData.getExtnRsnCode())) {
						extendValidity.setExtnRsnCode(ebillData.getExtnRsnCode());
					}
					if(isNotEmpty(ebillData.getTransitType())) {
						extendValidity.setTransitType(ebillData.getTransitType());
					}
					if(isNotEmpty(ebillData.getExtnRemarks())) {
						extendValidity.setExtnRemarks(ebillData.getExtnRemarks());
					}
					if(isNotEmpty(ebillData.getRemainingDistance())) {
						extendValidity.setRemainingDistance(ebillData.getRemainingDistance());
					}
					ewaybill.setExtendValidity(extendValidity);
					ewayBillRepository.save(ewaybill);
				}else {
					String jsonStr="";
					if(isNotEmpty(response) && isNotEmpty(response.getError()) && isNotEmpty(response.getError().getMessage())) {
						String ecodes = response.getError().getMessage();
				        jsonStr = response.getError().getMessage();
					}
				JSONObject jsonObj = new JSONObject(jsonStr);
		        String code = jsonObj.getString("errorCodes");
		        String[] cd=code.split(",");
			        for(int i=0;i<cd.length;i++) {
			        	errors = errorCodesRepository.findByErrorCode(cd[i]);
			        	if(isNotEmpty(errors) && isNotEmpty(errors.getErrorDesc())) {
			        		desc=desc+", "+errors.getErrorDesc();
			        	}
			        }
				}
			}
		}
		return desc;
	}
	
	@RequestMapping(value = "/cancelAlleBilllinv/{id}/{clientid}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody String cancelAllEwayBillInvoices(@RequestBody CancelEwayBill ebillData ,@PathVariable("id") String id,@PathVariable("clientid") String clientid, @PathVariable("month") int month,@PathVariable("year") int year,ModelMap model) throws Exception {
		final String method = "cancelAllEwayBillInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		EWAYBILL ewaybill = null;
		EwayBillCancelResponse cresponse = null;
		String desc="";
		for(String invoiceid : ebillData.getInvoiceIds()) {
			ewaybill = ewayBillRepository.findById(invoiceid);
			if(isNotEmpty(ewaybill) && isNotEmpty(ewaybill.getEwayBillNumber())) {
				ebillData.setEwbNo(NumberFormat.getInstance().parse(ewaybill.getEwayBillNumber()));
				cresponse = iHubConsumerService.cancelEwayBill(client.getGstnnumber(), InetAddress.getLocalHost().getHostAddress(),ebillData);
				ErrorCodeConfig errors=null;
				if(isNotEmpty(cresponse) && isNotEmpty(cresponse.getStatuscd()) && cresponse.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
					auditlogService.saveAuditLog(id, clientid,ewaybill.getInvoiceno(),AuditLogConstants.CANCELEWAYBILL,MasterGSTConstants.EWAYBILL,null,null);
					clientService.cancelInvoice(invoiceid, MasterGSTConstants.EWAYBILL, "");
				}else {
					 String jsonStr="";
					if(isNotEmpty(cresponse) && isNotEmpty(cresponse.getError()) && isNotEmpty(cresponse.getError().getMessage())) {
						String ecodes = cresponse.getError().getMessage();
				        jsonStr = cresponse.getError().getMessage();
					}
				
				JSONObject jsonObj = new JSONObject(jsonStr);
		        String code = jsonObj.getString("errorCodes");
		        String[] cd=code.split(",");
			        for(int i=0;i<cd.length;i++) {
			        	errors = errorCodesRepository.findByErrorCode(cd[i]);
			        	if(isNotEmpty(errors) && isNotEmpty(errors.getErrorDesc())) {
			        		desc=desc+", "+errors.getErrorDesc();
			        	}
			        }
				}
			}
		}
		return desc;
	}
	
	@RequestMapping(value = "/rejectAlleBilllinv/{id}/{clientid}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody String rejectAllEwayBillInvoices(@RequestBody List<String> invoiceids ,@PathVariable("id") String id,@PathVariable("clientid") String clientid, @PathVariable("month") int month,@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "rejectAllEwayBillInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		EWAYBILL ewaybill = null;
		EwayBillRejectResponse cresponse = null;
		String desc="";
		for(String invoiceid : invoiceids) {
			ewaybill = ewayBillRepository.findById(invoiceid);
			if(isNotEmpty(ewaybill) && isNotEmpty(ewaybill.getEwayBillNumber())) {
				CancelEwayBill rebill = new CancelEwayBill();
				rebill.setEwbNo(NumberFormat.getInstance().parse(ewaybill.getEwayBillNumber()));
				cresponse = iHubConsumerService.rejectEwayBill(client.getGstnnumber(), InetAddress.getLocalHost().getHostAddress(), rebill);
				ErrorCodeConfig errors=null;
				if(isNotEmpty(cresponse) && isNotEmpty(cresponse.getStatuscd()) && cresponse.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
					auditlogService.saveAuditLog(id, clientid,ewaybill.getInvoiceno(),AuditLogConstants.REJECTEWAYBILL,MasterGSTConstants.EWAYBILL,null,null);
					ewaybill.setEwaybillRejectDate(cresponse.getData().getEwbRejectedDate());
					ewaybill.setRejectStatus("Y");
					ewaybill.setStatus("Rejected");
					ewayBillRepository.save(ewaybill);
				}else {
					 String jsonStr="";
					if(isNotEmpty(cresponse) && isNotEmpty(cresponse.getError()) && isNotEmpty(cresponse.getError().getMessage())) {
						String ecodes = cresponse.getError().getMessage();
				        jsonStr = cresponse.getError().getMessage();
					}
				
				JSONObject jsonObj = new JSONObject(jsonStr);
		        String code = jsonObj.getString("errorCodes");
		        String[] cd=code.split(",");
			        for(int i=0;i<cd.length;i++) {
			        	errors = errorCodesRepository.findByErrorCode(cd[i]);
			        	if(isNotEmpty(errors) && isNotEmpty(errors.getErrorDesc())) {
			        		desc=desc+", "+errors.getErrorDesc();
			        	}
			        }
				}
			}
		}
		return desc;
	}
}
