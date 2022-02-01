package com.mastergst.web.usermanagement.runtime.controller;



import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2A;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jxls.template.SimpleExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
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
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.Branch;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientFillingStatus;
import com.mastergst.usermanagement.runtime.domain.CustomData;
import com.mastergst.usermanagement.runtime.domain.CustomFields;
import com.mastergst.usermanagement.runtime.domain.GSTR2B_vs_GSTR2;
import com.mastergst.usermanagement.runtime.domain.GSTR3B;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.Payments;
import com.mastergst.usermanagement.runtime.domain.Vertical;
import com.mastergst.usermanagement.runtime.repository.CustomFieldsRepository;
import com.mastergst.usermanagement.runtime.repository.OtherConfigurationRepository;
import com.mastergst.usermanagement.runtime.response.FinancialSummaryVO;
import com.mastergst.usermanagement.runtime.response.InvoiceVO;
import com.mastergst.usermanagement.runtime.service.ClientReportsUtil;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.InvoicesComparisonService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.ReportsService;
import com.mastergst.usermanagement.runtime.support.Utility;

import net.sf.dynamicreports.jasper.builder.export.JasperXlsExporterBuilder;
import net.sf.dynamicreports.jasper.constant.JasperProperty;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.dynamicreports.report.exception.DRException;

@Controller
public class ClientReportsController {
	private static final Logger logger = LogManager.getLogger(ClientReportsController.class.getName());
	private static final String CLASSNAME = "ClientReportsController::";
	
	@Autowired	private ClientService clientService;
	@Autowired	private ClientReportsUtil clientReportsUtil;
	@Autowired	private ReportsService reportsService;
	@Autowired	private ProfileService profileService;
	@Autowired	private UserService userService;
	@Autowired	private InvoicesComparisonService invoicesComparisonService; 
	@Autowired	OtherConfigurationRepository otherConfigurationRepository;
	@Autowired	CustomFieldsRepository customFieldsRepository;
	private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
	
	@RequestMapping(value = "/userclientfillingxls/{id}/{fullname}/{usertype}/{month}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody void getUserAllClientsFilingStatusReportXls(@PathVariable String id, @PathVariable String fullname, @PathVariable String usertype,
												@PathVariable int month,@PathVariable int year, HttpServletResponse response, HttpServletRequest request) throws Exception {
		final String method = "getUserAllClientsFilingStatusReportXls::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "userid\t" + id);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);
		String excelDwnld = request.getParameter("exceldwnld");
		List<Map<String, Object>> userclients = Lists.newArrayList();
		List<Client> lClient = Lists.newArrayList();
		if(NullUtil.isNotEmpty(id)){
			lClient = clientService.findByUserid(id);
		}
		String sheetname=year+"-"+(year+1);
		List<ClientFillingStatus> filingInfo=new ArrayList<ClientFillingStatus>();
		if(NullUtil.isNotEmpty(lClient)){
			for(Client client : lClient){
				Map<String,Object> clntname_gstnumber = Maps.newHashMap();
				clntname_gstnumber.put("clientName", client.getBusinessname());
				clntname_gstnumber.put("gstno", client.getGstnnumber());
				clntname_gstnumber.put("clientid", client.getId().toString());
				Map<String, Map<String, List<String>>> clientFilingStatus= clientService.getClientFilingStatusReport(client.getId().toString(), year,excelDwnld);
				
				clntname_gstnumber.put("statusmap", clientFilingStatus);
				userclients.add(clntname_gstnumber);
				
				
				clientFilingStatus.forEach((retType,monthlyStatus)->{
					ClientFillingStatus clientinfo=new ClientFillingStatus();
					clientinfo.setClientname(client.getBusinessname());
					clientinfo.setGstno(client.getGstnnumber());
					
					clientinfo.setRetType(retType);
					
					monthlyStatus.forEach((mnth,filingstatus)->{
						switch (mnth.substring(0,2)){
							case "04" :
									if(year == 2017) {
										clientinfo.setApr("-");
									}else {
										clientinfo.setApr(filingstatus.get(0));
									}
								break;
							case "05":
									if(year == 2017) {
										clientinfo.setMay("-");
									}else {
										clientinfo.setMay(filingstatus.get(0));
									}
								break;
							case "06":	
									if(year == 2017) {
										clientinfo.setJune("-");
									}else {
										clientinfo.setJune(filingstatus.get(0));
									}
								break;
							case "07":clientinfo.setJuly(filingstatus.get(0));
								break;
							case "08":clientinfo.setAug(filingstatus.get(0));
								break;
							case "09":clientinfo.setSept(filingstatus.get(0));
								break;
							case "10":clientinfo.setOct(filingstatus.get(0));
								break;
							case "11":clientinfo.setNov(filingstatus.get(0));
								break;
							case "12":clientinfo.setDec(filingstatus.get(0));
								break;
							case "01":clientinfo.setJan(filingstatus.get(0));
								break;
							case "02":clientinfo.setFeb(filingstatus.get(0));
								break;
							case "03":clientinfo.setMar(filingstatus.get(0));
								break;
						}
					});
					filingInfo.add(clientinfo);
				});
			}
		}
		
		
		TextColumnBuilder<String> clientname = col.column("Client Name", "clientname", type.stringType());
		TextColumnBuilder<String> gstno = col.column("GSTNO", "gstno", type.stringType()).setStyle(stl.style().conditionalStyles());
		TextColumnBuilder<String> retType = col.column("Return Type", "retType", type.stringType());
		TextColumnBuilder<String> apr = col.column("April", "apr", type.stringType()).setStyle(
				stl.style().conditionalStyles(
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("apr").equals("Filed");
							}
						}).setForegroundColor(Color.GREEN),
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("apr").equals("Pending");
							}
						}).setForegroundColor(Color.RED)));
		TextColumnBuilder<String> may = col.column("May", "may", type.stringType()).setStyle(
				stl.style().conditionalStyles(
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("may").equals("Filed");
							}
						}).setForegroundColor(Color.GREEN),
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("may").equals("Pending");
							}
						}).setForegroundColor(Color.RED)));
		TextColumnBuilder<String> june = col.column("June", "june", type.stringType()).setStyle(
				stl.style().conditionalStyles(
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("june").equals("Filed");
							}
						}).setForegroundColor(Color.GREEN),
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("june").equals("Pending");
							}
						}).setForegroundColor(Color.RED)));
		TextColumnBuilder<String> july = col.column("July", "july", type.stringType()).setStyle(
				stl.style().conditionalStyles(
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("july").equals("Filed");
							}
						}).setForegroundColor(Color.GREEN),
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("july").equals("Pending");
							}
						}).setForegroundColor(Color.RED)));
		TextColumnBuilder<String> aug = col.column("August", "aug", type.stringType()).setStyle(
				stl.style().conditionalStyles(
					stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
						@Override
						public Boolean evaluate(ReportParameters reportParameters) {
							return reportParameters.getValue("aug").equals("Filed");
						}
					}).setForegroundColor(Color.GREEN),
					stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
						@Override
						public Boolean evaluate(ReportParameters reportParameters) {
							return reportParameters.getValue("aug").equals("Pending");
						}
					}).setForegroundColor(Color.RED)));
		TextColumnBuilder<String> sept = col.column("September", "sept", type.stringType()).setStyle(
				stl.style().conditionalStyles(
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("sept").equals("Filed");
							}
						}).setForegroundColor(Color.GREEN),
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("sept").equals("Pending");
							}
						}).setForegroundColor(Color.RED)));
		TextColumnBuilder<String> oct = col.column("October", "oct", type.stringType()).setStyle(
				stl.style().conditionalStyles(
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("oct").equals("Filed");
							}
						}).setForegroundColor(Color.GREEN),
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("oct").equals("Pending");
							}
						}).setForegroundColor(Color.RED)));
		TextColumnBuilder<String> nov = col.column("November", "nov", type.stringType()).setStyle(
				stl.style().conditionalStyles(
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("nov").equals("Filed");
							}
						}).setForegroundColor(Color.GREEN),
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("nov").equals("Pending");
							}
						}).setForegroundColor(Color.RED)));
		TextColumnBuilder<String> dec = col.column("December", "dec", type.stringType()).setStyle(
				stl.style().conditionalStyles(
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("dec").equals("Filed");
							}
						}).setForegroundColor(Color.GREEN),
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("dec").equals("Pending");
							}
						}).setForegroundColor(Color.RED)));
		TextColumnBuilder<String> jan = col.column("January", "jan", type.stringType()).setStyle(
				stl.style().conditionalStyles(
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("jan").equals("Filed");
							}
						}).setForegroundColor(Color.GREEN),
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("jan").equals("Pending");
							}
						}).setForegroundColor(Color.RED)));
		TextColumnBuilder<String> feb = col.column("February", "feb", type.stringType()).setStyle(
				stl.style().conditionalStyles(
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("feb").equals("Filed");
							}
						}).setForegroundColor(Color.GREEN),
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("feb").equals("Pending");
							}
						}).setForegroundColor(Color.RED)));
		TextColumnBuilder<String> mar = col.column("March", "mar", type.stringType()).setStyle(
				stl.style().conditionalStyles(
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("mar").equals("Filed");
							}
						}).setForegroundColor(Color.GREEN),
						stl.conditionalStyle(new AbstractSimpleExpression<Boolean>() {
							@Override
							public Boolean evaluate(ReportParameters reportParameters) {
								return reportParameters.getValue("mar").equals("Pending");
							}
						}).setForegroundColor(Color.RED)));
		
		HorizontalListBuilder title = cmp.horizontalList().add(cmp.text("All Client Filing Status Report"));
		try {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "inline; filename='MGST_All_Clients_Filing_Status_"+year+"-"+(year+1)+".xls");

            OutputStream outputStream = response.getOutputStream();
			
			JasperXlsExporterBuilder xlsExporter = export.xlsExporter(outputStream)
				.addSheetName(sheetname)
				.setDetectCellType(true)
				.setIgnorePageMargins(true)
				.setWhitePageBackground(false)
				.setRemoveEmptySpaceBetweenColumns(true);

			report()
				.title(title)
				.setColumnTitleStyle(Templates.columnTitleStyle)
				.addProperty(JasperProperty.EXPORT_XLS_FREEZE_ROW, "3")
				.ignorePageWidth()
				.ignorePagination()
				.columns(
						clientname, gstno, retType, apr, may , june, july, aug, sept, oct, nov, dec, jan, feb, mar
				)
				.setDataSource(filingInfo)
				.toXls(xlsExporter);
			outputStream.flush();
            outputStream.close();
		} catch (DRException e) {
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value = "/dwnldReportsFinancialSummaryxls/{id}/{clientid}/{returntype}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource dwnldReportsFinancialSummary(@PathVariable("id") String id,@PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, 
			@RequestParam String reporttype, @RequestParam ("year") int year, @RequestParam String fromdate, @RequestParam String todate,
			HttpServletResponse response, HttpServletRequest request) throws Exception {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String clientname = "";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			clientname = client.getBusinessname().replace("/", "");
		}
		
		String fy =null;
		if(reporttype.equalsIgnoreCase("invoice_report")) {
			if(year != 0) {
				String nextyear = ((year+1)+"").substring(2);
				fy = year+"_"+nextyear;
			}else {
				fy = fromdate+"_to_"+todate;
			}
		}else {
			String nextyear = ((year+1)+"").substring(2);
			fy = year+"_"+nextyear;
		}
		
		String filename = ""+clientname+"_"+gstnumber+"_"+fy+"-Financial_Summary.xls";
		response.setHeader("Content-Disposition", "inline; filename='"+clientname+"_"+gstnumber+"_"+fy+"-Financial_Summary.xls");
		
		Map<String, Map<String, String>> summaryMap = null;
		
		if(reporttype.equalsIgnoreCase("invoice_report")) {
			if(year != 0) {
				summaryMap = clientService.getConsolidatedSummeryForYearReports(client, returntype, Utility.getYearCode(0, year), false, "invoice report",clientService.invoiceFilter(request));			
			}else {
				String fromtime= fromdate;
				String totime = todate;
				summaryMap = clientService.getConsolidatedSummeryForCustomReports(client, returntype, fromtime, totime,clientService.invoiceFilter(request));
						//getConsolidatedSummeryForYearReports(client, returntype, Utility.getYearCode(4, year), false, "invoice report");			
			}
		}else {
			summaryMap = clientService.getConsolidatedSummeryForYearReports(client, returntype, Utility.getYearCode(0, year), false, "Multimonth-Reports",clientService.invoiceFilter(request));			
		}
		
		Double totSales =0d,totTaxable= 0d, totAmt =0d, totInv = 0d, totAmt1 = 0d, totpurchase = 0d,totbalance = 0d,totSalesTax = 0d,totPurchasetax = 0d,totTax = 0d,totExempted = 0d, totTcsAmount = 0d,ptotTcsAmount=0d,totTdsAmount = 0d;
		Double totIgst = 0d, totCgst = 0d, totSgst = 0d, totCess = 0d,totItc = 0d;
		int[] monthArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		
		for (int i : monthArray) {
			totInv += Double.valueOf(summaryMap.get(i + "").get("totalTransactions"));
			totTaxable += Double.valueOf(summaryMap.get(i + "").get("Sales"));
			totAmt += Double.valueOf(summaryMap.get(i + "").get("totalamt"));
			totSalesTax += Double.valueOf(summaryMap.get(i + "").get("SalesTax"));
			totPurchasetax += Double.valueOf(summaryMap.get(i + "").get("SalesTax"));
			totExempted += Double.valueOf(summaryMap.get(i + "").get("exempted"));
			totAmt1 += Double.valueOf(summaryMap.get(i + "").get("totalamt"));
			totIgst += Double.valueOf(summaryMap.get(i + "").get("igst"));
			totCgst += Double.valueOf(summaryMap.get(i + "").get("cgst"));
			totSgst += Double.valueOf(summaryMap.get(i + "").get("sgst"));
			totCess += Double.valueOf(summaryMap.get(i + "").get("cess"));
			totTcsAmount += Double.valueOf(summaryMap.get(i + "").get("tcsamount"));
			totTdsAmount += Double.valueOf(summaryMap.get(i + "").get("tdsamount"));
			ptotTcsAmount += Double.valueOf(summaryMap.get(i + "").get("ptcsamount"));
			if(NullUtil.isNotEmpty(summaryMap.get(i + "").get("itc"))) {
				totItc += Double.valueOf(summaryMap.get(i + "").get("itc"));
			}
			totTax += Double.valueOf(summaryMap.get(i + "").get("Tax"));
		}

		Map<String, String> totReportMap = Maps.newHashMap();
		totReportMap.put("totInv", decimalFormat.format(totInv));
		totReportMap.put("totTaxable", decimalFormat.format(totTaxable));
		totReportMap.put("totAmt", decimalFormat.format(totAmt));
		totReportMap.put("totSalesTax", decimalFormat.format(totSalesTax));
		totReportMap.put("totPurchasetax", decimalFormat.format(totPurchasetax));
		totReportMap.put("totExempted", decimalFormat.format(totExempted));
		totReportMap.put("totAmt1", decimalFormat.format(totAmt1));
		totReportMap.put("totIgst", decimalFormat.format(totIgst));
		totReportMap.put("totCgst", decimalFormat.format(totCgst));
		totReportMap.put("totSgst", decimalFormat.format(totSgst));
		totReportMap.put("totCess", decimalFormat.format(totCess));
		totReportMap.put("totTcsAmount", decimalFormat.format(totTcsAmount));
		totReportMap.put("totTdsAmount", decimalFormat.format(totTdsAmount));
		totReportMap.put("ptotTcsAmount", decimalFormat.format(ptotTcsAmount));
		totReportMap.put("totItc", decimalFormat.format(totItc));
		summaryMap.put("totals", totReportMap);
		Map<String, FinancialSummaryVO> summaryMapr = Maps.newHashMap();
		Iterator iter = summaryMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Map<String,String> monthSummary= (Map<String, String>) entry.getValue();
			String mnth = entry.getKey().toString();
			if(!"totals".equals(mnth)) {
			Iterator miter = monthSummary.entrySet().iterator();
			while (miter.hasNext()) {
				Map.Entry mentry = (Map.Entry) miter.next();
				if(summaryMapr.containsKey(mentry.getKey())) {
					String typ = mentry.getKey().toString();
					 if(returntype.equals(MasterGSTConstants.GSTR1) ||returntype.equals(MasterGSTConstants.EINVOICE) || returntype.equals(MasterGSTConstants.EWAYBILL)) {
							 if(!typ.equals("PurchaseTax") && !typ.equals("tdsamount") && !typ.equals("itc") && !typ.equals("ptcsamount")) {
									FinancialSummaryVO sfinSummary = summaryMapr.get(mentry.getKey());
									switch(mnth) {
										case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
										break;
								}
								summaryMapr.put(mentry.getKey().toString(), sfinSummary);
						}	
					 }else if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
						 if(!typ.equals("exempted") && !typ.equals("tcsamount")) {
								FinancialSummaryVO sfinSummary = summaryMapr.get(mentry.getKey());
								switch(mnth) {
									case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
									break;
							}
							summaryMapr.put(mentry.getKey().toString(), sfinSummary);
						 }	
					 }else if(returntype.equals(MasterGSTConstants.GSTR2A)) {
						 if(!typ.equals("exempted") && !typ.equals("tdsamount") && !typ.equals("ptcsamount")) {
								FinancialSummaryVO sfinSummary = summaryMapr.get(mentry.getKey());
								switch(mnth) {
									case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
									break;
							}
							summaryMapr.put(mentry.getKey().toString(), sfinSummary);
						 }	
					 }
					 
					 
					 
			}else {
					String typ = mentry.getKey().toString();
					if(returntype.equals(MasterGSTConstants.GSTR1) || returntype.equals(MasterGSTConstants.EINVOICE) || returntype.equals(MasterGSTConstants.EWAYBILL)) {
							if(!typ.equals("PurchaseTax") && !typ.equals("tdsamount") && !typ.equals("ptcsamount") && !typ.equals("itc")) {
									FinancialSummaryVO sfinSummary = new FinancialSummaryVO();
									switch(typ) {
											case "totalTransactions": sfinSummary.setType("Total Transactions");
											break;
											case "Sales": sfinSummary.setType("Taxable Value");
											break;
											//case "Purchase": sfinSummary.setType("Purchases");
											//break;
											case "SalesTax": sfinSummary.setType("Tax Value");
											break;
											case "totalamt": sfinSummary.setType("Total Amount");
											break;
											case "exempted": sfinSummary.setType("Exempted Value");
											break;
											case "igst": sfinSummary.setType("IGST Amount");
											break;
											case "cgst": sfinSummary.setType("CGST Amount");
											break;
											case "sgst": sfinSummary.setType("SGST Amount");
											break;
											case "cess": sfinSummary.setType("CESS Amount");
											break;
											case "tcsamount": sfinSummary.setType("TCS Payable");
											break;
											default : sfinSummary.setType(typ);
											break;
									}
									
									switch(mnth) {
										case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
										break;
									}
									
									summaryMapr.put(mentry.getKey().toString(), sfinSummary);
							}
							
					}else if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
						if(!typ.equals("exempted") && !typ.equals("tcsamount")) {
							FinancialSummaryVO sfinSummary = new FinancialSummaryVO();
							switch(typ) {
									case "totalTransactions": sfinSummary.setType("Total Transactions");
									break;
									case "Sales": sfinSummary.setType("Taxable Value");
									break;
									//case "Purchase": sfinSummary.setType("Purchases");
									//break;
									case "SalesTax": sfinSummary.setType("Tax Value");
									break;
									case "totalamt": sfinSummary.setType("Total Amount");
									break;
									//case "exempted": sfinSummary.setType("Exempted Value");
									//break;
									case "igst": sfinSummary.setType("IGST Amount");
									break;
									case "cgst": sfinSummary.setType("CGST Amount");
									break;
									case "sgst": sfinSummary.setType("SGST Amount");
									break;
									case "cess": sfinSummary.setType("CESS Amount");
									break;
									case "ptcsamount": sfinSummary.setType("TCS Receivable");
									break;
									case "tdsamount": sfinSummary.setType("TDS Payable");
									break;
									case "itc": sfinSummary.setType("ITC Value");
									break;
									default : sfinSummary.setType(typ);
									break;
							}
							
							switch(mnth) {
								case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
								break;
							}
							
							summaryMapr.put(mentry.getKey().toString(), sfinSummary);
					}
					
					}else if(returntype.equals(MasterGSTConstants.GSTR2A)) {
						if(!typ.equals("exempted") && !typ.equals("tdsamount") && !typ.equals("ptcsamount")) {
							FinancialSummaryVO sfinSummary = new FinancialSummaryVO();
							switch(typ) {
									case "totalTransactions": sfinSummary.setType("Total Transactions");
									break;
									case "Sales": sfinSummary.setType("Taxable Value");
									break;
									//case "Purchase": sfinSummary.setType("Purchases");
									//break;
									case "SalesTax": sfinSummary.setType("Tax Value");
									break;
									case "totalamt": sfinSummary.setType("Total Amount");
									break;
									//case "exempted": sfinSummary.setType("Exempted Value");
									//break;
									case "igst": sfinSummary.setType("IGST Amount");
									break;
									case "cgst": sfinSummary.setType("CGST Amount");
									break;
									case "sgst": sfinSummary.setType("SGST Amount");
									break;
									case "cess": sfinSummary.setType("CESS Amount");
									break;
									case "tcsamount": sfinSummary.setType("TCS Value");
									break;
									case "itc": sfinSummary.setType("ITC Value");
									break;
									default : sfinSummary.setType(typ);
									break;
							}
							
							switch(mnth) {
								case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
								break;
							}
							
							summaryMapr.put(mentry.getKey().toString(), sfinSummary);
					}
					}
							
				}
			}
		}
		}
		
		Map<String,String> totals = summaryMap.get("totals");
		List<FinancialSummaryVO> financialSummaryVOList=Lists.newArrayList();
		Iterator summary = summaryMapr.entrySet().iterator();
		while (summary.hasNext()) {
			Map.Entry entry = (Map.Entry) summary.next();
			if("Sales".equals(entry.getKey().toString()) || "Purchase".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTaxable").toString()));
				financialSummaryVOList.add(sales);	
			}else if("SalesTax".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totSalesTax").toString()));
				financialSummaryVOList.add(sales);	
			}else if("PurchaseTax".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totPurchasetax").toString()));
				financialSummaryVOList.add(sales);	
			}else if("exempted".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totExempted").toString()));
				financialSummaryVOList.add(sales);	
			}else if("tcsamount".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTcsAmount").toString()));
				financialSummaryVOList.add(sales);	
			}else if("ptcsamount".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("ptotTcsAmount").toString()));
				financialSummaryVOList.add(sales);	
			}else if("tdsamount".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTdsAmount").toString()));
				financialSummaryVOList.add(sales);	
			}else if("igst".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totIgst").toString()));
				financialSummaryVOList.add(sales);	
			}else if("cgst".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totCgst").toString()));
				financialSummaryVOList.add(sales);	
			}else if("sgst".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totSgst").toString()));
				financialSummaryVOList.add(sales);	
			}else if("cess".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totCess").toString()));
				financialSummaryVOList.add(sales);	
			} else if("totalTransactions".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totInv").toString()));
				financialSummaryVOList.add(sales);	
			}else if("totTaxable".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTaxable").toString()));
				financialSummaryVOList.add(sales);	
			}else if("totalamt".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totAmt").toString()));
				financialSummaryVOList.add(sales);	
			}else if("itc".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totItc").toString()));
				financialSummaryVOList.add(sales);	
			}
				
		}
		
		File file = new File(filename);
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers = null;
					headers = Arrays.asList("", "April", "May", "June", "July", "August", "september","October", "November", "December", "January","February","March","Total");				
				
			SimpleExporter exporter = new SimpleExporter();
			exporter.gridExport(headers, financialSummaryVOList,"type, aprilamt, mayamt, juneamt,julyamt,augustamt,sepamt,octamt, novamt, decamt, janamt,febamt,maramt,totalamt",fos);
				
			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "downloadExcelData : ERROR", e);
		}
		return new FileSystemResource(new File(filename));
	}
	
	@RequestMapping(value = "/dwnldGlobalReportsFinancialSummaryxls/{id}/{returntype}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource dwnldGlobalReportsFinancialSummary(@PathVariable("id") String id,
			@PathVariable("returntype") String returntype,@RequestParam("clientids") List<String> clientids,@RequestParam ("year") int year, @RequestParam String fromdate, @RequestParam String todate,@RequestParam("booksorReturns") String booksorReturns,HttpServletResponse response, HttpServletRequest request) throws Exception {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		String fy =null;
		
			if(year != 0) {
				String nextyear = ((year+1)+"").substring(2);
				fy = year+"_"+nextyear;
			}else {
				fy = fromdate+"_to_"+todate;
			}
		
		String filename = ""+fy+"-Financial_Summary.xls";
		response.setHeader("Content-Disposition", "inline; filename='"+fy+"-Financial_Summary.xls");
		
		Map<String, Map<String, String>> summaryMap = null;
		
		
		if(year != 0) {
			summaryMap = reportsService.getConsolidatedSummeryForYearGlobalReports(clientids, returntype, Utility.getYearCode(4, year), false,booksorReturns,clientService.invoiceFilter(request));			
		}else {
			String fromtime= fromdate;
			String totime = todate;
			summaryMap = reportsService.getConsolidatedSummeryForGlobalCustomReports(clientids, returntype, fromtime, totime,booksorReturns,clientService.invoiceFilter(request));
					//getConsolidatedSummeryForYearReports(client, returntype, Utility.getYearCode(4, year), false, "invoice report");			
		}
		
		Double totSales =0d,totTaxable= 0d, totAmt =0d, totInv = 0d, totAmt1 = 0d, totpurchase = 0d,totbalance = 0d,totSalesTax = 0d,totPurchasetax = 0d,totTax = 0d,totExempted = 0d, totTcsAmount = 0d,ptotTcsAmount = 0d,totTdsAmount = 0d;
		Double totIgst = 0d, totCgst = 0d, totSgst = 0d, totCess = 0d,totItc = 0d;
		int[] monthArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		
		for (int i : monthArray) {
			totInv += Double.valueOf(summaryMap.get(i + "").get("totalTransactions"));
			totTaxable += Double.valueOf(summaryMap.get(i + "").get("Sales"));
			totAmt += Double.valueOf(summaryMap.get(i + "").get("totalamt"));
			totSalesTax += Double.valueOf(summaryMap.get(i + "").get("SalesTax"));
			totPurchasetax += Double.valueOf(summaryMap.get(i + "").get("SalesTax"));
			totExempted += Double.valueOf(summaryMap.get(i + "").get("exempted"));
			totAmt1 += Double.valueOf(summaryMap.get(i + "").get("totalamt"));
			totIgst += Double.valueOf(summaryMap.get(i + "").get("igst"));
			totCgst += Double.valueOf(summaryMap.get(i + "").get("cgst"));
			totSgst += Double.valueOf(summaryMap.get(i + "").get("sgst"));
			totCess += Double.valueOf(summaryMap.get(i + "").get("cess"));
			totTcsAmount += Double.valueOf(summaryMap.get(i + "").get("tcsamount"));
			ptotTcsAmount += Double.valueOf(summaryMap.get(i + "").get("ptcsamount"));
			totTdsAmount += Double.valueOf(summaryMap.get(i + "").get("tdsamount"));
			if(NullUtil.isNotEmpty(summaryMap.get(i + "").get("itc"))) {
				totItc += Double.valueOf(summaryMap.get(i + "").get("itc"));
			}
			totTax += Double.valueOf(summaryMap.get(i + "").get("Tax"));
		}

		Map<String, String> totReportMap = Maps.newHashMap();
		totReportMap.put("totInv", decimalFormat.format(totInv));
		totReportMap.put("totTaxable", decimalFormat.format(totTaxable));
		totReportMap.put("totAmt", decimalFormat.format(totAmt));
		totReportMap.put("totSalesTax", decimalFormat.format(totSalesTax));
		totReportMap.put("totPurchasetax", decimalFormat.format(totPurchasetax));
		totReportMap.put("totExempted", decimalFormat.format(totExempted));
		totReportMap.put("totAmt1", decimalFormat.format(totAmt1));
		totReportMap.put("totIgst", decimalFormat.format(totIgst));
		totReportMap.put("totCgst", decimalFormat.format(totCgst));
		totReportMap.put("totSgst", decimalFormat.format(totSgst));
		totReportMap.put("totCess", decimalFormat.format(totCess));
		totReportMap.put("totTcsAmount", decimalFormat.format(totTcsAmount));
		totReportMap.put("ptotTcsAmount", decimalFormat.format(ptotTcsAmount));
		totReportMap.put("totTdsAmount", decimalFormat.format(totTdsAmount));
		totReportMap.put("totItc", decimalFormat.format(totItc)); 
		summaryMap.put("totals", totReportMap);
		Map<String, FinancialSummaryVO> summaryMapr = Maps.newHashMap();
		Iterator iter = summaryMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Map<String,String> monthSummary= (Map<String, String>) entry.getValue();
			String mnth = entry.getKey().toString();
			if(!"totals".equals(mnth)) {
			Iterator miter = monthSummary.entrySet().iterator();
			while (miter.hasNext()) {
				Map.Entry mentry = (Map.Entry) miter.next();
				if(summaryMapr.containsKey(mentry.getKey())) {
					String typ = mentry.getKey().toString();
					 if(returntype.equals(MasterGSTConstants.GSTR1)) {
							 if(!typ.equals("PurchaseTax") && !typ.equals("tdsamount")  && !typ.equals("ptcsamount") && !typ.equals("itc")) {
									FinancialSummaryVO sfinSummary = summaryMapr.get(mentry.getKey());
									switch(mnth) {
										case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
										break;
								}
								summaryMapr.put(mentry.getKey().toString(), sfinSummary);
						}	
					 }else if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
						 if(!typ.equals("exempted") && !typ.equals("tcsamount")) {
								FinancialSummaryVO sfinSummary = summaryMapr.get(mentry.getKey());
								switch(mnth) {
									case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
									break;
							}
							summaryMapr.put(mentry.getKey().toString(), sfinSummary);
						 }	
					 }else if(returntype.equals(MasterGSTConstants.GSTR2A)) {
						 if(!typ.equals("exempted") && !typ.equals("tdsamount") && !typ.equals("ptcsamount")) {
								FinancialSummaryVO sfinSummary = summaryMapr.get(mentry.getKey());
								switch(mnth) {
									case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
									break;
							}
							summaryMapr.put(mentry.getKey().toString(), sfinSummary);
						 }	
					 }
					 
					 
					 
			}else {
					String typ = mentry.getKey().toString();
					if(returntype.equals(MasterGSTConstants.GSTR1)) {
							if(!typ.equals("PurchaseTax") && !typ.equals("tdsamount") && !typ.equals("ptcsamount") && !typ.equals("itc")) {
									FinancialSummaryVO sfinSummary = new FinancialSummaryVO();
									switch(typ) {
											case "totalTransactions": sfinSummary.setType("Total Transactions");
											break;
											case "Sales": sfinSummary.setType("Taxable Value");
											break;
											//case "Purchase": sfinSummary.setType("Purchases");
											//break;
											case "SalesTax": sfinSummary.setType("Tax Value");
											break;
											case "totalamt": sfinSummary.setType("Total Amount");
											break;
											case "exempted": sfinSummary.setType("Exempted Value");
											break;
											case "igst": sfinSummary.setType("IGST Amount");
											break;
											case "cgst": sfinSummary.setType("CGST Amount");
											break;
											case "sgst": sfinSummary.setType("SGST Amount");
											break;
											case "cess": sfinSummary.setType("CESS Amount");
											break;
											case "tcsamount": sfinSummary.setType("TCS Payable");
											break;
											default : sfinSummary.setType(typ);
											break;
									}
									
									switch(mnth) {
										case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
										break;
									}
									
									summaryMapr.put(mentry.getKey().toString(), sfinSummary);
							}
							
					}else if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
						if(!typ.equals("exempted") && !typ.equals("tcsamount")) {
							FinancialSummaryVO sfinSummary = new FinancialSummaryVO();
							switch(typ) {
									case "totalTransactions": sfinSummary.setType("Total Transactions");
									break;
									case "Sales": sfinSummary.setType("Taxable Value");
									break;
									//case "Purchase": sfinSummary.setType("Purchases");
									//break;
									case "SalesTax": sfinSummary.setType("Tax Value");
									break;
									case "totalamt": sfinSummary.setType("Total Amount");
									break;
									//case "exempted": sfinSummary.setType("Exempted Value");
									//break;
									case "igst": sfinSummary.setType("IGST Amount");
									break;
									case "cgst": sfinSummary.setType("CGST Amount");
									break;
									case "sgst": sfinSummary.setType("SGST Amount");
									break;
									case "cess": sfinSummary.setType("CESS Amount");
									break;
									case "tdsamount": sfinSummary.setType("TDS Payable");
									break;
									case "ptcsamount": sfinSummary.setType("TCS Receivable");
									break;
									case "itc": sfinSummary.setType("ITC Value");
									break;
									default : sfinSummary.setType(typ);
									break;
							}
							
							switch(mnth) {
								case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
								break;
							}
							
							summaryMapr.put(mentry.getKey().toString(), sfinSummary);
					}
					
					}else if(returntype.equals(MasterGSTConstants.GSTR2A)) {
						if(!typ.equals("exempted") && !typ.equals("tdsamount") && !typ.equals("ptcsamount")) {
							FinancialSummaryVO sfinSummary = new FinancialSummaryVO();
							switch(typ) {
									case "totalTransactions": sfinSummary.setType("Total Transactions");
									break;
									case "Sales": sfinSummary.setType("Taxable Value");
									break;
									//case "Purchase": sfinSummary.setType("Purchases");
									//break;
									case "SalesTax": sfinSummary.setType("Tax Value");
									break;
									case "totalamt": sfinSummary.setType("Total Amount");
									break;
									//case "exempted": sfinSummary.setType("Exempted Value");
									//break;
									case "igst": sfinSummary.setType("IGST Amount");
									break;
									case "cgst": sfinSummary.setType("CGST Amount");
									break;
									case "sgst": sfinSummary.setType("SGST Amount");
									break;
									case "cess": sfinSummary.setType("CESS Amount");
									break;
									case "tcsamount": sfinSummary.setType("TCS Value");
									break;
									case "itc": sfinSummary.setType("ITC Value");
									break;
									default : sfinSummary.setType(typ);
									break;
							}
							
							switch(mnth) {
								case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
								break;
							}
							
							summaryMapr.put(mentry.getKey().toString(), sfinSummary);
					}
					}
							
				}
			}
		}
		}
		
		Map<String,String> totals = summaryMap.get("totals");
		List<FinancialSummaryVO> financialSummaryVOList=Lists.newArrayList();
		Iterator summary = summaryMapr.entrySet().iterator();
		while (summary.hasNext()) {
			Map.Entry entry = (Map.Entry) summary.next();
			if("Sales".equals(entry.getKey().toString()) || "Purchase".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTaxable").toString()));
				financialSummaryVOList.add(sales);	
			}else if("SalesTax".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totSalesTax").toString()));
				financialSummaryVOList.add(sales);	
			}else if("PurchaseTax".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totPurchasetax").toString()));
				financialSummaryVOList.add(sales);	
			}else if("exempted".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totExempted").toString()));
				financialSummaryVOList.add(sales);	
			}else if("tcsamount".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTcsAmount").toString()));
				financialSummaryVOList.add(sales);	
			}else if("ptcsamount".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("ptotTcsAmount").toString()));
				financialSummaryVOList.add(sales);	
			}else if("tdsamount".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTdsAmount").toString()));
				financialSummaryVOList.add(sales);	
			}else if("igst".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totIgst").toString()));
				financialSummaryVOList.add(sales);	
			}else if("cgst".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totCgst").toString()));
				financialSummaryVOList.add(sales);	
			}else if("sgst".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totSgst").toString()));
				financialSummaryVOList.add(sales);	
			}else if("cess".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totCess").toString()));
				financialSummaryVOList.add(sales);	
			} else if("totalTransactions".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totInv").toString()));
				financialSummaryVOList.add(sales);	
			}else if("totTaxable".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTaxable").toString()));
				financialSummaryVOList.add(sales);	
			}else if("totalamt".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totAmt").toString()));
				financialSummaryVOList.add(sales);	
			}else if("itc".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totItc").toString()));
				financialSummaryVOList.add(sales);	
			}
				
		}
		
		File file = new File(filename);
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers = null;
					headers = Arrays.asList("", "April", "May", "June", "July", "August", "september","October", "November", "December", "January","February","March","Total");				
				
			SimpleExporter exporter = new SimpleExporter();
			exporter.gridExport(headers, financialSummaryVOList,"type, aprilamt, mayamt, juneamt,julyamt,augustamt,sepamt,octamt, novamt, decamt, janamt,febamt,maramt,totalamt",fos);
				
			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "downloadReportsExcelData : ERROR", e);
		}
		return new FileSystemResource(new File(filename));
	}
	
	
	@RequestMapping(value = "/getGlobalReportsAddtionalInvs/{id}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getGlobalReportsAddtionalInvs(@PathVariable("id") String id, @PathVariable("returntype") String returntype, 
			@PathVariable("month") int month, @PathVariable("year") int year,@RequestParam("booksorReturns") String booksorReturns, @RequestParam("clientids") List<String> clientids,
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
		//Client client = clientService.findByIdIn(listofclients);
		String retType = returntype;
		Pageable pageable = null;
		Map<String, Object> invoicesMap = reportsService.getGlobalReportInvoices(pageable, clientids, id, retType, "reports", month, year, start, length, searchVal,sortParam, sortOrder,filter, true,booksorReturns);
		Page<? extends InvoiceParent> invoices = (Page<? extends InvoiceParent>)invoicesMap.get("invoices");
		if(isNotEmpty(invoices)) {
			for(InvoiceParent invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
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
		return writer.writeValueAsString(invoicesMap);
	}
	
	@RequestMapping(value = "/getGlobalReportsAddtionalCustomInvs/{id}/{returntype}/{fromtime}/{totime}", method = RequestMethod.GET)
	public @ResponseBody String getGlobalReportsAddtionalCustomInvs(@PathVariable("id") String id, @PathVariable("returntype") String returntype, 
			@PathVariable("fromtime")String fromtime, @PathVariable("totime")String totime,@RequestParam("booksorReturns") String booksorReturns, @RequestParam("clientids")List<String> clientids,
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getGlobalReportsAddtionalCustomInvs::";
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
		String retType = returntype;
		Pageable pageable = null;
		Map<String, Object> invoicesMap = reportsService.getGlobalReportsCustomInvoices(pageable, clientids, id, retType, "reports", fromtime, totime, start, length, searchVal, filter, true,booksorReturns);
		Page<? extends InvoiceParent> invoices = (Page<? extends InvoiceParent>)invoicesMap.get("invoices");
		if(isNotEmpty(invoices)) {
			for(InvoiceParent invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
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
		return writer.writeValueAsString(invoicesMap);
	}
	
	@RequestMapping(value = "/globalReportsTotalSummary/{returntype}/{month}/{year}")
	public @ResponseBody Map<String,Map<String,String>> getConsolidateReportsTotalAmounts(@PathVariable String returntype, @PathVariable int month, @PathVariable int year,
			@RequestParam("booksorReturns") String booksorReturns,@RequestParam("clientids")List<String> clientids,HttpServletRequest request){
		final String method = "getConsolidateReportsTotalAmounts::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, Map<String, String>>	invMap = reportsService.getConsolidatedSummeryForYearGlobalReports(clientids, returntype, Utility.getYearCode(month, year), false,booksorReturns,clientService.invoiceFilter(request));
		return invMap;
	}
	@RequestMapping(value = "/globalReportsCustomTotalSummary/{returntype}/{fromtime}/{totime}")
	public @ResponseBody Map<String,Map<String,String>> getConsolidateReportsTotalAmountsFromCustom(@PathVariable String returntype,@PathVariable String fromtime, @PathVariable String totime,
			@RequestParam("booksorReturns") String booksorReturns,@RequestParam("clientids")List<String> clientids,HttpServletRequest request){
		final String method = "getConsolidateReportsTotalAmountsFromCustom::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, Map<String, String>> invMap = reportsService.getConsolidatedSummeryForGlobalCustomReports(clientids, returntype, fromtime, totime,booksorReturns,clientService.invoiceFilter(request));
		return invMap;
	}
	@RequestMapping(value = "/getAddtionalGlobalReportsInvsSupport/{returntype}/{month}/{year}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAdditionalInvoicesSupport(@PathVariable("returntype") String returntype, 
			 @PathVariable("month") int month, @PathVariable("year") int year,@RequestParam("booksorReturns") String booksorReturns, @RequestParam("clientids")List<String> clientids,
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, Object> invoicesMap = reportsService.getGlobalInvoicesSupport(clientids, returntype, "reports", month, year,booksorReturns);
		return invoicesMap;
	}
	
	@RequestMapping(value = "/getGlobalReportsCustomAddtionalInvsSupport/{returntype}/{fromtime}/{totime}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getCustomAdditionalInvoicesSupport(@PathVariable("returntype") String returntype, 
			@PathVariable String fromtime, @PathVariable String totime,@RequestParam("booksorReturns") String booksorReturns, @RequestParam("clientids")List<String> clientids, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getCustomAdditionalInvoicesSupport::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, Object> invoicesMap = reportsService.getGlobalCustomInvoicesSupport(clientids, returntype, "reports", fromtime, totime,booksorReturns);
		return invoicesMap;
	}
	
	@RequestMapping(value = "/dwnldReportsFinancialSummaryxls/{id}/{clientid}/{returntype}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource dwnldReportsFinancialSummary(@PathVariable("id") String id,@PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("year") int year,HttpServletResponse response, HttpServletRequest request) throws Exception {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String clientname = "";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			clientname = client.getBusinessname().replace("/", "");
		}
		String nextyear = ((year+1)+"").substring(2);
		
		String fy = year+"_"+nextyear;
		String filename = ""+clientname+"_"+gstnumber+"_"+fy+"-Financial_Summary.xls";
		response.setHeader("Content-Disposition", "inline; filename='"+clientname+"_"+gstnumber+"_"+fy+"-Financial_Summary.xls");
		
		Map<String, Map<String, String>> summaryMap = clientService.getConsolidatedSummeryForYearReports(client, returntype, Utility.getYearCode(4, year), false, "invoice report",clientService.invoiceFilter(request));
		
		Double totSales =0d,totTaxable= 0d, totAmt =0d, totInv = 0d, totAmt1 = 0d, totpurchase = 0d,totbalance = 0d,totSalesTax = 0d,totPurchasetax = 0d,totTax = 0d,totExempted = 0d, totTcsAmount = 0d,totTdsAmount = 0d;
		Double totIgst = 0d, totCgst = 0d, totSgst = 0d, totCess = 0d,totItc = 0d;
		int[] monthArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		
		for (int i : monthArray) {
			totInv += Double.valueOf(summaryMap.get(i + "").get("totalTransactions"));
			totTaxable += Double.valueOf(summaryMap.get(i + "").get("Sales"));
			totAmt += Double.valueOf(summaryMap.get(i + "").get("totalamt"));
			totSalesTax += Double.valueOf(summaryMap.get(i + "").get("SalesTax"));
			totPurchasetax += Double.valueOf(summaryMap.get(i + "").get("SalesTax"));
			totExempted += Double.valueOf(summaryMap.get(i + "").get("exempted"));
			totAmt1 += Double.valueOf(summaryMap.get(i + "").get("totalamt"));
			totIgst += Double.valueOf(summaryMap.get(i + "").get("igst"));
			totCgst += Double.valueOf(summaryMap.get(i + "").get("cgst"));
			totSgst += Double.valueOf(summaryMap.get(i + "").get("sgst"));
			totCess += Double.valueOf(summaryMap.get(i + "").get("cess"));
			totTcsAmount += Double.valueOf(summaryMap.get(i + "").get("tcsamount"));
			totTdsAmount += Double.valueOf(summaryMap.get(i + "").get("tdsamount"));
			if(NullUtil.isNotEmpty(summaryMap.get(i + "").get("itc"))) {
				totItc += Double.valueOf(summaryMap.get(i + "").get("itc"));
			}
			totTax += Double.valueOf(summaryMap.get(i + "").get("Tax"));
		}

		Map<String, String> totReportMap = Maps.newHashMap();
		totReportMap.put("totInv", decimalFormat.format(totInv));
		totReportMap.put("totTaxable", decimalFormat.format(totTaxable));
		totReportMap.put("totAmt", decimalFormat.format(totAmt));
		totReportMap.put("totSalesTax", decimalFormat.format(totSalesTax));
		totReportMap.put("totPurchasetax", decimalFormat.format(totPurchasetax));
		totReportMap.put("totExempted", decimalFormat.format(totExempted));
		totReportMap.put("totAmt1", decimalFormat.format(totAmt1));
		totReportMap.put("totIgst", decimalFormat.format(totIgst));
		totReportMap.put("totCgst", decimalFormat.format(totCgst));
		totReportMap.put("totSgst", decimalFormat.format(totSgst));
		totReportMap.put("totCess", decimalFormat.format(totCess));
		totReportMap.put("totTcsAmount", decimalFormat.format(totTcsAmount));
		totReportMap.put("totTdsAmount", decimalFormat.format(totTdsAmount));
		totReportMap.put("totItc", decimalFormat.format(totItc));
		/* totReportMap.put("totbalance", decimalFormat.format(totbalance));
		totReportMap.put("totSalesTax", decimalFormat.format(totSalesTax));
		totReportMap.put("totPurchasetax", decimalFormat.format(totPurchasetax));
		totReportMap.put("totTdsAmount", decimalFormat.format(totTdsAmount));*/ 
		//totReportMap.put("totTax", decimalFormat.format(totTax)); 
		summaryMap.put("totals", totReportMap);
		Map<String, FinancialSummaryVO> summaryMapr = Maps.newHashMap();
		Iterator iter = summaryMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Map<String,String> monthSummary= (Map<String, String>) entry.getValue();
			String mnth = entry.getKey().toString();
			if(!"totals".equals(mnth)) {
			Iterator miter = monthSummary.entrySet().iterator();
			while (miter.hasNext()) {
				Map.Entry mentry = (Map.Entry) miter.next();
				if(summaryMapr.containsKey(mentry.getKey())) {
					String typ = mentry.getKey().toString();
					 if(returntype.equals(MasterGSTConstants.GSTR1)) {
							 if(!typ.equals("PurchaseTax") && !typ.equals("tdsamount") && !typ.equals("itc")) {
									FinancialSummaryVO sfinSummary = summaryMapr.get(mentry.getKey());
									switch(mnth) {
										case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
										break;
								}
								summaryMapr.put(mentry.getKey().toString(), sfinSummary);
						}	
					 }else if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
						 if(!typ.equals("exempted") && !typ.equals("tcsamount")) {
								FinancialSummaryVO sfinSummary = summaryMapr.get(mentry.getKey());
								switch(mnth) {
									case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
									break;
									default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
									break;
							}
							summaryMapr.put(mentry.getKey().toString(), sfinSummary);
					}	
					 }
					 
					 
					 
			}else {
					String typ = mentry.getKey().toString();
					if(returntype.equals(MasterGSTConstants.GSTR1)) {
							if(!typ.equals("PurchaseTax") && !typ.equals("tdsamount") && !typ.equals("itc")) {
									FinancialSummaryVO sfinSummary = new FinancialSummaryVO();
									switch(typ) {
											case "totalTransactions": sfinSummary.setType("Total Transactions");
											break;
											case "Sales": sfinSummary.setType("Taxable Value");
											break;
											//case "Purchase": sfinSummary.setType("Purchases");
											//break;
											case "SalesTax": sfinSummary.setType("Tax Value");
											break;
											case "totalamt": sfinSummary.setType("Total Amount");
											break;
											case "exempted": sfinSummary.setType("Exempted Value");
											break;
											case "igst": sfinSummary.setType("IGST Amount");
											break;
											case "cgst": sfinSummary.setType("CGST Amount");
											break;
											case "sgst": sfinSummary.setType("SGST Amount");
											break;
											case "cess": sfinSummary.setType("CESS Amount");
											break;
											case "tcsamount": sfinSummary.setType("TCS Value");
											break;
											default : sfinSummary.setType(typ);
											break;
									}
									
									switch(mnth) {
										case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
										break;
										default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
										break;
									}
									
									summaryMapr.put(mentry.getKey().toString(), sfinSummary);
							}
							
					}else if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
						if(!typ.equals("exempted") && !typ.equals("tcsamount")) {
							FinancialSummaryVO sfinSummary = new FinancialSummaryVO();
							switch(typ) {
									case "totalTransactions": sfinSummary.setType("Total Transactions");
									break;
									case "Sales": sfinSummary.setType("Taxable Value");
									break;
									//case "Purchase": sfinSummary.setType("Purchases");
									//break;
									case "SalesTax": sfinSummary.setType("Tax Value");
									break;
									case "totalamt": sfinSummary.setType("Total Amount");
									break;
									//case "exempted": sfinSummary.setType("Exempted Value");
									//break;
									case "igst": sfinSummary.setType("IGST Amount");
									break;
									case "cgst": sfinSummary.setType("CGST Amount");
									break;
									case "sgst": sfinSummary.setType("SGST Amount");
									break;
									case "cess": sfinSummary.setType("CESS Amount");
									break;
									case "tdsamount": sfinSummary.setType("TDS Value");
									break;
									case "itc": sfinSummary.setType("ITC Value");
									break;
									default : sfinSummary.setType(typ);
									break;
							}
							
							switch(mnth) {
								case "1": sfinSummary.setJanamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "2": sfinSummary.setFebamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "3": sfinSummary.setMaramt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "4": sfinSummary.setAprilamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "5": sfinSummary.setMayamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "6": sfinSummary.setJuneamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "7": sfinSummary.setJulyamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "8": sfinSummary.setAugustamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "9": sfinSummary.setSepamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "10": sfinSummary.setOctamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "11": sfinSummary.setNovamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								case "12": sfinSummary.setDecamt(Double.parseDouble(mentry.getValue().toString()));
								break;
								default : sfinSummary.setTotalamt(Double.parseDouble(mentry.getValue().toString()));
								break;
							}
							
							summaryMapr.put(mentry.getKey().toString(), sfinSummary);
					}
					
					}
							
				}
			}
		}
		}
		
		Map<String,String> totals = summaryMap.get("totals");
		List<FinancialSummaryVO> financialSummaryVOList=Lists.newArrayList();
		Iterator summary = summaryMapr.entrySet().iterator();
		while (summary.hasNext()) {
			Map.Entry entry = (Map.Entry) summary.next();
			if("Sales".equals(entry.getKey().toString()) || "Purchase".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTaxable").toString()));
				financialSummaryVOList.add(sales);	
			}else if("SalesTax".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totSalesTax").toString()));
				financialSummaryVOList.add(sales);	
			}else if("PurchaseTax".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totPurchasetax").toString()));
				financialSummaryVOList.add(sales);	
			}else if("exempted".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totExempted").toString()));
				financialSummaryVOList.add(sales);	
			}else if("tcsamount".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTcsAmount").toString()));
				financialSummaryVOList.add(sales);	
			}else if("tdsamount".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTdsAmount").toString()));
				financialSummaryVOList.add(sales);	
			}else if("igst".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totIgst").toString()));
				financialSummaryVOList.add(sales);	
			}else if("cgst".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totCgst").toString()));
				financialSummaryVOList.add(sales);	
			}else if("sgst".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totSgst").toString()));
				financialSummaryVOList.add(sales);	
			}else if("cess".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totCess").toString()));
				financialSummaryVOList.add(sales);	
			} else if("totalTransactions".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totInv").toString()));
				financialSummaryVOList.add(sales);	
			}else if("totTaxable".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totTaxable").toString()));
				financialSummaryVOList.add(sales);	
			}else if("totalamt".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totAmt").toString()));
				financialSummaryVOList.add(sales);	
			}else if("itc".equals(entry.getKey().toString())) {
				FinancialSummaryVO sales = (FinancialSummaryVO) entry.getValue();
				sales.setTotalamt(Double.parseDouble(totals.get("totItc").toString()));
				financialSummaryVOList.add(sales);	
			}
				
		}
		
		File file = new File(filename);
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers = null;
					headers = Arrays.asList("", "April", "May", "June", "July", "August", "september","October", "November", "December", "January","February","March","Total");				
				
			SimpleExporter exporter = new SimpleExporter();
			exporter.gridExport(headers, financialSummaryVOList,"type, aprilamt, mayamt, juneamt,julyamt,augustamt,sepamt,octamt, novamt, decamt, janamt,febamt,maramt,totalamt",fos);
				
			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "downloadReportsExcelData : ERROR", e);
		}
		return new FileSystemResource(new File(filename));
	}

	@RequestMapping(value = "/cp_ClientsEinvoiceReportsGroupData/{returntype}/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
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
		return "profile/userClientGroupReportsEinvoice";
	
	}
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	
	@RequestMapping(value = "/getadminGSTR3BYearlyinvs/{id}/{returntype}/{year}", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> getadminGSTR3BYearlyInvoices(@PathVariable("id") String id,
			@PathVariable("returntype") String returntype,  @PathVariable("year") int year,@RequestParam("clientids") List<String> clientids, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		int[] monthArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		List<Map<String, String>> adminsummary = Lists.newArrayList();
		for(String clientid : clientids) {
			Map<String, String> summaryMap = Maps.newHashMap();
			for (int i : monthArray) {
				GSTR3B reportMap = null;
				if (i < 4) {
					String retPeriod = (i < 10 ? "0" + i : i + "")+(year+1);
					reportMap = clientService.getSuppliesInvoice(clientid, retPeriod);
					ObjectMapper mapper = new ObjectMapper();
					mapper.disable(MapperFeature.USE_ANNOTATIONS);
					ObjectWriter writer=null;
					FilterProvider filters = new SimpleFilterProvider().addFilter("gstr3bFilter", SimpleBeanPropertyFilter.serializeAll());
					writer=mapper.writer(filters);
					summaryMap.put(i + "", writer.writeValueAsString(reportMap));
				} else {
					String retPeriod = (i < 10 ? "0" + i : i + "")+year;
					reportMap = clientService.getSuppliesInvoice(clientid, retPeriod);
					ObjectMapper mapper = new ObjectMapper();
					mapper.disable(MapperFeature.USE_ANNOTATIONS);
					ObjectWriter writer=null;
					FilterProvider filters = new SimpleFilterProvider().addFilter("gstr3bFilter", SimpleBeanPropertyFilter.serializeAll());
					writer=mapper.writer(filters);
					summaryMap.put(i + "", writer.writeValueAsString(reportMap));
				}
			}
			adminsummary.add(summaryMap);
		}
		return adminsummary;
	}
	
	@RequestMapping(value = "/gstr2bvsgstr2/{id}/{clientid}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getGSTR2AB_vs_GSTR2_Report(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @RequestParam("year") int year,  @RequestParam("month") int month, ModelMap model,
			HttpServletRequest request) throws MasterGSTException, ParseException {
		final String method = "getGSTR2B_vs_GSTR2_Details::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		String st = request.getParameter("start");
		String len = request.getParameter("length");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);

		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		
		Map<String, Object> invMap = invoicesComparisonService.getGSTR2B_vs_GSTR2Report(clientid, month, year, start, length, searchVal, false);

		//Map<String, Object> invMap = new HashMap<>();
		//invMap.put("invoices", cmpLst);
		
		return invMap;
	}
	
	@RequestMapping(value = "/gstr2bvsgstr2xls/{id}/{clientid}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource getGstr2bvsGstr2Xls(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @RequestParam("year") int year,  @RequestParam("month") int month, ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws MasterGSTException, ParseException {
		final String method = "getGstr2bvsGstr2Xls::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		
		String st = request.getParameter("start");
		String len = request.getParameter("length");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);

		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		
		Map<String, Object> invMap = invoicesComparisonService.getGSTR2B_vs_GSTR2Report(clientid, month, year, start, length, searchVal, true);

		response.setHeader("Content-Disposition", "inline; filename='MGST_GSTR2B_vs_Purchase_Register" + year +".xls");
		logger.debug(invMap.get("invoices"));
		
		File file =  month == 0 ? new File("MGST_GSTR2B_vs_GSTR2_" + year + ".xls") : new File("MGST_GSTR2B_vs_GSTR2_"+ month +"_"+ year + ".xls");
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			List<String> headers= Arrays.asList("Invoice Date", "GSTIN of Supplier", "Name of Supplier", "Invoice Number",
					"Invoice Value", "Taxable Value", "IGST", "CGST", "S/UT GST","Invoice Number","Invoice Value", "Taxable Value", "IGST", "CGST", "S/UT GST","Invoice Number","Invoice Value", "Taxable Value", "IGST", "CGST", "S/UT GST");
			SimpleExporter exporter = new SimpleExporter();
			Page<GSTR2B_vs_GSTR2> page = (Page<GSTR2B_vs_GSTR2>) invMap.get("invoices");
			
			exporter.gridExport(headers, page.getContent(),
					"invoicedate, gstin, fullname, invoiceno, gstr2InvoiceValue, gstr2TaxValue, gstr2IGSTValue, gstr2CGSTValue, gstr2SGSTValue, invoiceno, gstr2BInvoiceValue, gstr2BTaxValue, gstr2BIGSTValue, gstr2BCGSTValue, gstr2BSGSTValue, invoiceno, diffInvoiceValue, diffTaxValue, diffIGSTValue, diffCGSTValue, diffSGSTValue"
					,fos);

			return new FileSystemResource(file);
		} catch (IOException e) {
			logger.error(CLASSNAME + "downloadExcelData : ERROR", e);
		}
		return new FileSystemResource(new File("MGST_GSTR2A_vs_GSTR2__" + year + ".xls"));
	}
	
	@RequestMapping(value = "/monthgstr1fulldwnldyearlyxls/{id}/{clientid}/{returntype}/{year}", method = RequestMethod.GET)
	public void getInvoiceGSTR1YearlyFullReportn(@PathVariable("id") String id,	@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype, 
			@PathVariable("year") int year, HttpServletResponse response, HttpServletRequest request) throws FileNotFoundException {
		final String method = "getInvoiceFullReport::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String customField1 = "CustomField1";
		String customField2 = "CustomField2";
		String customField3 = "CustomField3";
		String customField4 = "CustomField4";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			
			CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
			if(isNotEmpty(customFields)) {
				if("GSTR1".equals(returntype) || "SalesRegister".equals(returntype)) {
					if(isNotEmpty(customFields.getSales())) {
						int i=1;
						for(CustomData customdata : customFields.getSales()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(GSTR2.equals(returntype) || GSTR2A.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
					if(isNotEmpty(customFields.getPurchase())) {
						int i=1;
						for(CustomData customdata : customFields.getPurchase()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(returntype.equals(MasterGSTConstants.EINVOICE)){
					if(isNotEmpty(customFields.getEinvoice())) {
						int i=1;
						for(CustomData customdata : customFields.getEinvoice()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}
			}
			
		}
		Page<? extends InvoiceParent> invoices = null;
		if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
			invoices = clientService.getDaoInvoices(client, returntype, 4, year, "Multimonth-Reports", clientService.invoiceFilter(request));
		}else {
			invoices = clientService.getInvoices(null, client, id, returntype, year);
		}
		List<InvoiceVO> invoiceVOList= clientReportsUtil.invoiceListItems(invoices,returntype);
		List<String> headers=null;
		if(GSTR2.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
			headers=Arrays.asList("Invoice Date", "Invoice No","Invoice Type","Return Period", "Reverse Charge","GSTIN","SupplierID", "Supplier Name", "CompanyStateName", "Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Eway Bill Number", "Ledger", "State","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Supplier Notes",customField1,customField2,customField3,customField4,"Terms & Conditions","Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
					"Rate","Discount","Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","ITC Type", "ITC Available","Total Tax","Total Invoice Value","TDS Amount","Net Receivable(Total Invoice Value - TDS Amount)");
		}else if(GSTR2A.equals(returntype)){
			headers=Arrays.asList("Invoice Date", "Invoice No","Invoice Type","Return Period", "Reverse Charge","GSTIN", "Place Of Supply","SupplierID", "Supplier Name", "CompanyStateName", "Counter Party Filing Status","Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Eway Bill Number", "Ledger", "State","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Supplier Notes",customField1,customField2,customField3,customField4,"Terms & Conditions","Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
					"Rate","Discount","Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","ITC Type", "ITC Available","Total Tax","Total Invoice Value","TDS Amount","Net Receivable(Total Invoice Value - TDS Amount)");
		}else {
			headers=Arrays.asList("Invoice Date", "Invoice No","Invoice Status","Invoice Type","Return Period","Reverse Charge", "GSTIN","CustomerID", "Customer Name", "CompanyStateName", "Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Eway Bill Number", "Ledger", "Place Of Supply","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage","Port Code","Shipping Bill No","Shipping Bill Date","Additional Currency Code","Exchange Rate",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Customer Notes","Terms & Conditions",customField1,customField2,customField3,customField4,"Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
					"Rate","Discount","Exempted", "Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","Total Tax","Total Invoice Value","TCS Amount","Net Receivable(Total Invoice Value + TCS Amount)","Total Currency Value");
		}
		if(invoiceVOList.size() < 10000) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			String fileName = "MGST_"+returntype+"_"+gstnumber+"_"+year+".xls";
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			File file = new File(fileName);
			FileInputStream in = null;
			OutputStream out = null;
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				SimpleExporter exporter = new SimpleExporter();
				if(returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER) || returntype.equals("Unclaimed")){
					exporter.gridExport(headers, invoiceVOList,
							"invoiceDate, invoiceNo,type,returnPeriod,recharge, customerGSTIN,customerID, customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount,itcType, totalItc,totaltax,totalValue,tcsAmount,tcsNetAmount",fos);				
				}else if(returntype.equals(GSTR2A)){
					exporter.gridExport(headers, invoiceVOList,
							"invoiceDate, invoiceNo,type,returnPeriod,recharge, customerGSTIN,placeOfSupply,customerID,customerName, companyStatename,counterFilingStatus,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount,itcType, totalItc,totaltax,totalValue,tcsAmount,tcsNetAmount",fos);				
				}else {
					exporter.gridExport(headers, invoiceVOList,
							"invoiceDate, invoiceNo,gstStatus,type,returnPeriod,recharge, customerGSTIN,customerID, customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber, ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount,itemExmepted, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);
				}
				in = new FileInputStream(file);
				out = response.getOutputStream();
				byte[] buffer= new byte[8192]; // use bigger if you want
				int length = 0;
				while ((length = in.read(buffer)) > 0){
				     out.write(buffer, 0, length);
				}
				if(isNotEmpty(in)) {
					in.close();
				}
			} catch (IOException e) {
				logger.error(CLASSNAME + "downloadExcelData : ERROR", e);
			}finally {
				try {
					file.delete();	
					if (isNotEmpty(out)) {
						out.close();
					}				
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			OutputStream nout = null;
			ZipOutputStream zipOutputStream = null;
			try {
				nout = response.getOutputStream();
				String fileName = "MGST_"+returntype+"_"+gstnumber+"_"+year+".zip";
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
				response.setContentType("application/octet-stream; charset=utf-8");
				zipOutputStream = new ZipOutputStream(nout);
				byte[] buf = new byte[1024];
				int c=0;
				int len = 0;
				double i = ((double)invoiceVOList.size())/60000;
				int j = (int)i;
				if(i-(int)i > 0) {
					j = (int)i+1;
				}
				List<List<InvoiceVO>> lt = Lists.newArrayList();
				int a=0;
				int b = 60000;
				if(invoiceVOList.size() < 60000) {
					b= invoiceVOList.size();
				}
				for(int k = 1; k <= j;k++) {
					lt.add(invoiceVOList.subList(a, b));
					a = b;
					if(k == j-1) {
						b = invoiceVOList.size();
					}else {
						b = b+60000;
					}
				}
				
				for(List<InvoiceVO> InvoicesList: lt) {
					File file1 = new File("MGST_"+returntype+"_"+gstnumber+"_"+year+"_"+(c+1)+ ".xls");
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					SimpleExporter exporter = new SimpleExporter();
					if(returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER) || returntype.equals("Unclaimed")){
						exporter.gridExport(headers, InvoicesList,
								"invoiceDate, invoiceNo,type,returnPeriod,recharge, customerGSTIN,customerID, customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount,itcType, totalItc,totaltax,totalValue,tcsAmount,tcsNetAmount",fos);				
					}else if(returntype.equals(GSTR2A)){
						exporter.gridExport(headers, InvoicesList,
								"invoiceDate, invoiceNo,type,returnPeriod,recharge, customerGSTIN,placeOfSupply,customerID,customerName, companyStatename,counterFilingStatus,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount,itcType, totalItc,totaltax,totalValue,tcsAmount,tcsNetAmount",fos);				
					}else {
						exporter.gridExport(headers, InvoicesList,
								"invoiceDate, invoiceNo,gstStatus,type,returnPeriod,recharge, customerGSTIN,customerID, customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber, ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount,itemExmepted, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);
					}
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
				
			} catch (IOException e) {
				logger.error(CLASSNAME + "downloadSupplierExcelData : ERROR", e);
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
	}
	
	@RequestMapping(value = "/fulldwnldyearlyxls/{id}/{clientid}/{returntype}/{year}", method = RequestMethod.GET)
	public void getInvoiceYearlyFullReport(@PathVariable("id") String id,@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype, 
			@PathVariable("year") int year, HttpServletResponse response, HttpServletRequest request) throws FileNotFoundException {
		final String method = "getInvoiceFullReport::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String customField1 = "CustomField1";
		String customField2 = "CustomField2";
		String customField3 = "CustomField3";
		String customField4 = "CustomField4";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			
			CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
			if(isNotEmpty(customFields)) {
				if("GSTR1".equals(returntype) || "SalesRegister".equals(returntype)) {
					if(isNotEmpty(customFields.getSales())) {
						int i=1;
						for(CustomData customdata : customFields.getSales()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(GSTR2.equals(returntype) || GSTR2A.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
					if(isNotEmpty(customFields.getPurchase())) {
						int i=1;
						for(CustomData customdata : customFields.getPurchase()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(returntype.equals(MasterGSTConstants.EINVOICE)){
					if(isNotEmpty(customFields.getEinvoice())) {
						int i=1;
						for(CustomData customdata : customFields.getEinvoice()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(returntype.equals(MasterGSTConstants.EWAYBILL)) {
					if(isNotEmpty(customFields.getEwaybill())) {
						int i=1;
						for(CustomData customdata : customFields.getEwaybill()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}
			}
			
		}
		Page<? extends InvoiceParent> invoices = clientService.getDaoInvoices(client, returntype, 0, year, "invoice-report", clientService.invoiceFilter(request));
		List<InvoiceVO> invoiceVOList= clientReportsUtil.invoiceListItems(invoices,returntype);
		List<String> headers=null;
		if(GSTR2.equals(returntype) || GSTR2A.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
			headers=Arrays.asList("Invoice Date", "Invoice No","Document Type","Invoice Type","Return Period","Reverse Charge", "Reverse Charge No","Transaction Date","GSTIN","SupplierID", "Supplier Name", "CompanyStateName", "Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Original Invoice Number","Original Invoice Date","Eway Bill Number", "Ledger", "State","Reverse Charge","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Supplier Notes",customField1,customField2,customField3,customField4,"Terms & Conditions","Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
					"Rate","Discount","Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","ITC Type", "ITC Available","Total Tax","Total Invoice Value","TDS Amount","Net Receivable(Total Invoice Value - TDS Amount)");
		}else if(returntype.equals(MasterGSTConstants.EINVOICE)) {
			headers=Arrays.asList("IRN No","IRN Status","Acknowledge No","Acknowledge Date","IRN Generated Date","Einv Status","Signed Qr Code","Document Date", "Document No","Document Type","Invoice Type","Return Period","Reverse Charge", "GSTIN", "Customer ID","Customer Name","Customer PAN","CustomerTAN","CustomerTANPAN","Customer LedgerName", "CompanyStateName", "Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Eway Bill Number", "Ledger", "Place Of Supply","Reverse Charge","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage","Port Code","Shipping Bill No","Shipping Bill Date","Additional Currency Code","Exchange Rate",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Customer Notes","Terms & Conditions",customField1,customField2,customField3,customField4,"Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Bar Code","Quantity","Free Qty",
					"Rate","Discount","Exempted", "Other Charges","Assessable value","Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "State Cess","CESS Rate", "CESS Amount","Cess NonAdvol","Total Tax","Total Invoice Value","TCS Amount","Net Receivable(Total Invoice Value + TCS Amount)","Total Currency Value");
		}else {
			headers=Arrays.asList("Invoice Date", "Invoice No","Invoice Status","Document Type","Invoice Type","Return Period","Reverse Charge", "GSTIN","CustomerID", "Customer Name", "CompanyStateName", "Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Eway Bill Number", "Ledger", "Place Of Supply","Reverse Charge","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage","Port Code","Shipping Bill No","Shipping Bill Date","Additional Currency Code","Exchange Rate",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Customer Notes","Terms & Conditions",customField1,customField2,customField3,customField4,"Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
					"Rate","Discount","Exempted", "Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","Total Tax","Total Invoice Value","TCS Amount","Net Receivable(Total Invoice Value + TCS Amount)","Total Currency Value");
		}
		if(invoiceVOList.size() < 10000) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			String fileName = "MGST_"+returntype+"_"+gstnumber+"_"+year+".xls";
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			File file = new File(fileName);
			FileInputStream in = null;
			OutputStream out = null;
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				SimpleExporter exporter = new SimpleExporter();
				if(returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER) || returntype.equals("Unclaimed") || returntype.equals(GSTR2A)){
					exporter.gridExport(headers, invoiceVOList,	"invoiceDate, invoiceNo,docType,type,returnPeriod,recharge, revChargeNo,transactionDate, customerGSTIN, customerID,customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,originalInvoiceNo,originalInvoiceDate,ewayBillNumber,ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount,itcType, totalItc,totaltax,totalValue,tcsAmount,tcsNetAmount",fos);				
				}else if(returntype.equals(MasterGSTConstants.EINVOICE)) {
					exporter.gridExport(headers, invoiceVOList, "irnNo,irnStatus,ackno,ackdt,irndt,einvstatus,qrcode,invoiceDate, invoiceNo,docType,type,returnPeriod,recharge, customerGSTIN, customerID,customerName,customerPAN,customerTAN,customerTANPAN,customerLedgerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,barCode,quantity,freeQty,rateperitem,itemDiscount,itemExmepted, othrCharges,assAmt,taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, stateCess,cessRate, cessAmount,cessnonAdvol, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);
				}else {
					exporter.gridExport(headers, invoiceVOList, "invoiceDate, invoiceNo,gstStatus,docType,type,returnPeriod,recharge, customerGSTIN,customerID, customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber, ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount,itemExmepted, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);
				}
				in = new FileInputStream(file);
				out = response.getOutputStream();
				byte[] buffer= new byte[8192]; // use bigger if you want
				int length = 0;
				while ((length = in.read(buffer)) > 0){
				     out.write(buffer, 0, length);
				}
				if(isNotEmpty(in)) {
					in.close();
				}
			} catch (IOException e) {
				logger.error(CLASSNAME + "downloadExcelData : ERROR", e);
			}finally {
				try {
					file.delete();	
					if (isNotEmpty(out)) {
						out.close();
					}				
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			OutputStream nout = null;
			ZipOutputStream zipOutputStream = null;
			try {
				nout = response.getOutputStream();
				String fileName = "MGST_"+returntype+"_"+gstnumber+"_"+year+".zip";
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
				response.setContentType("application/octet-stream; charset=utf-8");
				zipOutputStream = new ZipOutputStream(nout);
				byte[] buf = new byte[1024];
				int c=0;
				int len = 0;
				double i = ((double)invoiceVOList.size())/60000;
				int j = (int)i;
				if(i-(int)i > 0) {
					j = (int)i+1;
				}
				List<List<InvoiceVO>> lt = Lists.newArrayList();
				int a=0;
				int b = 60000;
				if(invoiceVOList.size() < 60000) {
					b= invoiceVOList.size();
				}
				for(int k = 1; k <= j;k++) {
					lt.add(invoiceVOList.subList(a, b));
					a = b;
					if(k == j-1) {
						b = invoiceVOList.size();
					}else {
						b = b+60000;
					}
				}
				
				for(List<InvoiceVO> InvoicesList: lt) {
					File file1 = new File("MGST_"+returntype+"_"+gstnumber+"_"+year+"_"+(c+1)+ ".xls");
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					SimpleExporter exporter = new SimpleExporter();
					if(returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER) || returntype.equals("Unclaimed") || returntype.equals(GSTR2A)){
						exporter.gridExport(headers, InvoicesList,
								"invoiceDate, invoiceNo,docType,type,returnPeriod,recharge, revChargeNo,transactionDate, customerGSTIN, customerID,customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,originalInvoiceNo,originalInvoiceDate,ewayBillNumber,ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount,itcType, totalItc,totaltax,totalValue,tcsAmount,tcsNetAmount",fos);				
					}else if(returntype.equals(MasterGSTConstants.EINVOICE)) {
						exporter.gridExport(headers, InvoicesList,
								"irnNo,irnStatus,ackno,ackdt,irndt,einvstatus,qrcode,invoiceDate, invoiceNo,docType,type,returnPeriod,recharge, customerGSTIN, customerID,customerName,customerPAN,customerTAN,customerTANPAN,customerLedgerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,barCode,quantity,freeQty,rateperitem,itemDiscount,itemExmepted, othrCharges,assAmt,taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, stateCess,cessRate, cessAmount,cessnonAdvol, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);
					}else {
						exporter.gridExport(headers, InvoicesList,
								"invoiceDate, invoiceNo,gstStatus,docType,type,returnPeriod,recharge, customerGSTIN,customerID, customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber, ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount,itemExmepted, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);
					}
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
				
			} catch (IOException e) {
				logger.error(CLASSNAME + "downloadSupplierExcelData : ERROR", e);
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
	}
	
	@RequestMapping(value = "/fulldwnldcustomxls/{id}/{clientid}/{returntype}/{fromtime}/{totime}", method = RequestMethod.GET)
	public void getInvoiceCustomFullReport(@PathVariable("id") String id,@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,@PathVariable("fromtime") String fromtime, 
			@PathVariable("totime") String totime, HttpServletResponse response, HttpServletRequest request) throws FileNotFoundException {
		final String method = "getInvoiceFullReport::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String customField1 = "CustomField1";
		String customField2 = "CustomField2";
		String customField3 = "CustomField3";
		String customField4 = "CustomField4";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
			if(isNotEmpty(customFields)) {
				if("GSTR1".equals(returntype) || "SalesRegister".equals(returntype)) {
					if(isNotEmpty(customFields.getSales())) {
						int i=1;
						for(CustomData customdata : customFields.getSales()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(GSTR2.equals(returntype) || GSTR2A.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
					if(isNotEmpty(customFields.getPurchase())) {
						int i=1;
						for(CustomData customdata : customFields.getPurchase()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(returntype.equals(MasterGSTConstants.EINVOICE)){
					if(isNotEmpty(customFields.getEinvoice())) {
						int i=1;
						for(CustomData customdata : customFields.getEinvoice()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}
			}
		}
		Page<? extends InvoiceParent> invoices = clientService.getDaoInvoices(client, returntype, fromtime, totime, clientService.invoiceFilter(request));
		List<InvoiceVO> invoiceVOList= clientReportsUtil.invoiceListItems(invoices,returntype);
		List<String> headers=null;
		if(GSTR2.equals(returntype) || GSTR2A.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
			headers=Arrays.asList("Invoice Date", "Invoice No","Document Type","Invoice Type","Return Period","Reverse Charge", "Reverse Charge No","Transaction Date","GSTIN","SupplierID", "Supplier Name", "CompanyStateName", "Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Original Invoice Number","Original Invoice Date","Eway Bill Number", "Ledger", "State","Reverse Charge","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Supplier Notes","Terms & Conditions",customField1,customField2,customField3,customField4,"Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
					"Rate","Discount","Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","ITC Type", "ITC Available","Total Tax","Total Invoice Value","TDS Amount","Net Receivable(Total Invoice Value - TDS Amount)");
		}else if(returntype.equals(MasterGSTConstants.EINVOICE)) {
			headers=Arrays.asList("IRN No","IRN Status","Acknowledge No","Acknowledge Date","IRN Generated Date","Einv Status","Signed QrCode","Document Date", "Document No","Document Type","Invoice Type","Return Period","Reverse Charge", "GSTIN", "Customer ID","Customer Name","Customer PAN","CustomerTAN","CustomerTANPAN", "Customer LedgerName","CompanyStateName", "Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Eway Bill Number", "Ledger", "Place Of Supply","Reverse Charge","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage","Port Code","Shipping Bill No","Shipping Bill Date","Additional Currency Code","Exchange Rate",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Customer Notes","Terms & Conditions",customField1,customField2,customField3,customField4,"Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Bar Code","Quantity","Free Qty",
					"Rate","Discount","Exempted", "Other Charges","Assessable value","Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "State Cess","CESS Rate", "CESS Amount","Cess NonAdvol","Total Tax","Total Invoice Value","TCS Amount","Net Receivable(Total Invoice Value + TCS Amount)","Total Currency Value");
		}else {
			headers=Arrays.asList("Invoice Date", "Invoice No","Invoice Status","Document Type","Invoice Type","Return Period","Reverse Charge", "GSTIN","Customer ID", "Customer Name", "CompanyStateName", "Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Eway Bill Number", "Ledger", "Place Of Supply","Reverse Charge","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage","Port Code","Shipping Bill No","Shipping Bill Date","Additional Currency Code","Exchange Rate",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Customer Notes","Terms & Conditions",customField1,customField2,customField3,customField4,"Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
					"Rate","Discount","Exempted", "Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","Total Tax","Total Invoice Value","TCS Amount","Net Receivable(Total Invoice Value + TCS Amount)","Total Currency Value");
		}
		if(invoiceVOList.size() < 10000) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			String fileName = "MGST_"+returntype+"_"+gstnumber+"_"+fromtime+"_"+totime+".xls";
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			File file = new File(fileName);
			FileInputStream in = null;
			OutputStream out = null;
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				SimpleExporter exporter = new SimpleExporter();
				if(GSTR2.equals(returntype) || GSTR2A.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
					exporter.gridExport(headers, invoiceVOList,	"invoiceDate, invoiceNo,docType,type,returnPeriod,recharge,revChargeNo,transactionDate, customerGSTIN, customerID,customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,originalInvoiceNo,originalInvoiceDate,ewayBillNumber,ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount,itcType, totalItc,totaltax,totalValue,tcsAmount,tcsNetAmount",fos);				
				}else if(returntype.equals(MasterGSTConstants.EINVOICE)) {
					exporter.gridExport(headers, invoiceVOList,	"irnNo,irnStatus,ackno,ackdt,irndt,einvstatus,qrcode,invoiceDate, invoiceNo,docType,type,returnPeriod,recharge, customerGSTIN, customerID,customerName,customerPAN,customerTAN,customerTANPAN,customerLedgerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,barCode,quantity,freeQty,rateperitem,itemDiscount,itemExmepted, othrCharges,assAmt,taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, stateCess,cessRate, cessAmount,cessnonAdvol, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);
				}else {
					exporter.gridExport(headers, invoiceVOList,	"invoiceDate, invoiceNo,gstStatus,docType,type,returnPeriod,recharge, customerGSTIN, customerID,customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount,itemExmepted, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);
				}
				in = new FileInputStream(file);
				out = response.getOutputStream();
				byte[] buffer= new byte[8192]; // use bigger if you want
				int length = 0;
				while ((length = in.read(buffer)) > 0){
				     out.write(buffer, 0, length);
				}
				if(isNotEmpty(in)) {
					in.close();
				}
			} catch (IOException e) {
				logger.error(CLASSNAME + "downloadExcelData : ERROR", e);
			}finally {
				try {
					file.delete();	
					if (isNotEmpty(out)) {
						out.close();
					}				
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			OutputStream nout = null;
			ZipOutputStream zipOutputStream = null;
			try {
				nout = response.getOutputStream();
				String fileName = "MGST_"+returntype+"_"+gstnumber+"_"+fromtime+"_"+totime+".zip";
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
				response.setContentType("application/octet-stream; charset=utf-8");
				zipOutputStream = new ZipOutputStream(nout);
				byte[] buf = new byte[1024];
				int c=0;
				int len = 0;
				double i = ((double)invoiceVOList.size())/60000;
				int j = (int)i;
				if(i-(int)i > 0) {
					j = (int)i+1;
				}
				List<List<InvoiceVO>> lt = Lists.newArrayList();
				int a=0;
				int b = 60000;
				if(invoiceVOList.size() < 60000) {
					b= invoiceVOList.size();
				}
				for(int k = 1; k <= j;k++) {
					lt.add(invoiceVOList.subList(a, b));
					a = b;
					if(k == j-1) {
						b = invoiceVOList.size();
					}else {
						b = b+60000;
					}
				}
				
				for(List<InvoiceVO> InvoicesList: lt) {
					File file1 = new File("MGST_"+returntype+"_"+gstnumber+"_"+fromtime+"_"+totime+"_"+(c+1)+".xls");
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					SimpleExporter exporter = new SimpleExporter();
					if(GSTR2.equals(returntype) || GSTR2A.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
						exporter.gridExport(headers, InvoicesList,
								"invoiceDate, invoiceNo,docType,type,returnPeriod,recharge,revChargeNo,transactionDate, customerGSTIN, customerID,customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,originalInvoiceNo,originalInvoiceDate,ewayBillNumber,ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount,itcType, totalItc,totaltax,totalValue,tcsAmount,tcsNetAmount",fos);				
					}else if(returntype.equals(MasterGSTConstants.EINVOICE)) {
						exporter.gridExport(headers, InvoicesList,
								"irnNo,irnStatus,ackno,ackdt,irndt,einvstatus,qrcode,invoiceDate, invoiceNo,docType,type,returnPeriod,recharge, customerGSTIN, customerID,customerName,customerPAN,customerTAN,customerTANPAN,customerLedgerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,barCode,quantity,freeQty,rateperitem,itemDiscount,itemExmepted, othrCharges,assAmt,taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, stateCess,cessRate, cessAmount,cessnonAdvol, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);
					}else {
						exporter.gridExport(headers, InvoicesList,
							"invoiceDate, invoiceNo,gstStatus,docType,type,returnPeriod,recharge, customerGSTIN, customerID,customerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reverseCharge,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount,itemExmepted, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);
					}
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
				
			} catch (IOException e) {
				logger.error(CLASSNAME + "downloadSupplierExcelData : ERROR", e);
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
	}
	
	@RequestMapping(value = "/fulldwnldmonthlyxls/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public void getInvoiceMonthlyFullReport(@PathVariable("id") String id,@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype,@PathVariable("month") int month, 
			@PathVariable("year") int year, HttpServletResponse response, HttpServletRequest request) throws FileNotFoundException {
		final String method = "getInvoiceFullReport::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		Client client = clientService.findById(clientid);
		String invoiceView="Monthly";
		if(isNotEmpty(client) && isNotEmpty(client.getInvoiceViewOption())) {
			if(client.getInvoiceViewOption().equalsIgnoreCase("Yearly")) {
				invoiceView="Yearly";
			}
		}
		String gstnumber = "";
		String customField1 = "CustomField1";
		String customField2 = "CustomField2";
		String customField3 = "CustomField3";
		String customField4 = "CustomField4";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
			if(isNotEmpty(customFields)) {
				if("GSTR1".equals(returntype) || "SalesRegister".equals(returntype)) {
					if(isNotEmpty(customFields.getSales())) {
						int i=1;
						for(CustomData customdata : customFields.getSales()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(GSTR2.equals(returntype) || GSTR2A.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
					if(isNotEmpty(customFields.getPurchase())) {
						int i=1;
						for(CustomData customdata : customFields.getPurchase()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}else if(returntype.equals(MasterGSTConstants.EINVOICE)){
					if(isNotEmpty(customFields.getEinvoice())) {
						int i=1;
						for(CustomData customdata : customFields.getEinvoice()){
							if(isNotEmpty(customdata.getCustomFieldName())) {
								if(i == 1) {
									customField1 = customdata.getCustomFieldName();
								}else if(i==2) {
									customField2 = customdata.getCustomFieldName();
								}else if(i==3) {
									customField3 = customdata.getCustomFieldName();
								}else if(i==4) {
									customField4 = customdata.getCustomFieldName();
								}
								i++;
							}
						}
					}
				}
			}
		}
		String reportType = "notreports";
		
		String booksOrReturn = request.getParameter("booksorReturns");
		
		InvoiceFilter filter= clientService.invoiceFilter(request);
		if("SalesRegister".equals(booksOrReturn) || "PurchaseRegister".equals(booksOrReturn)) {
			filter.setBooksOrReturns("books");
		}
		boolean flag=false;
		if(isNotEmpty(booksOrReturn)) {
			if(booksOrReturn.equals(MasterGSTConstants.PROFORMAINVOICES) || booksOrReturn.equals(MasterGSTConstants.DELIVERYCHALLANS) || booksOrReturn.equals(MasterGSTConstants.ESTIMATES) || booksOrReturn.equals("SalesRegister") || booksOrReturn.equals(MasterGSTConstants.PURCHASEORDER) || booksOrReturn.equals("PurchaseRegister")){
				if(booksOrReturn.equals(MasterGSTConstants.PROFORMAINVOICES) || booksOrReturn.equals(MasterGSTConstants.DELIVERYCHALLANS) || booksOrReturn.equals(MasterGSTConstants.ESTIMATES) || booksOrReturn.equals("SalesRegister") || booksOrReturn.equals(MasterGSTConstants.PURCHASEORDER) || booksOrReturn.equals("PurchaseRegister")){
					reportType = "reports";
				}
				if(invoiceView.equalsIgnoreCase("Yearly")) {
					month = 0;
					flag= true;
				}
			}
		}
		Page<? extends InvoiceParent> invoices = null;
		if(flag) {
			Map<String, Object> invoicesMap = clientService.getInvoices(null, client, id, returntype, reportType, month, year, 0, 0, null, filter, true, null);
			
			invoices =(Page<? extends InvoiceParent>) invoicesMap.get("invoices");
		}else {
			String rptype = "invoice-report";
			if(isNotEmpty(booksOrReturn) && booksOrReturn.equals("abcd") && returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) {
				rptype = "invoice-report";
			}else {
				if((isNotEmpty(booksOrReturn) && booksOrReturn.equals("PurchaseRegister")) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase("Unclaimed")) {	
					OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(client.getId().toString());
					Boolean billdate = false;
					if(isNotEmpty(otherconfig)){
						billdate = otherconfig.isEnableTransDate();
					}
					if(isEmpty(otherconfig) || !billdate) {
						rptype = "invoice-report";
					} else {
						rptype = "bRreports";
					}
				}
			}
			invoices = 	clientService.getDaoInvoices(client, returntype, month, year, rptype, filter);
		}
		List<InvoiceVO> invoiceVOList= clientReportsUtil.invoiceListItems(invoices,returntype);
		List<String> headers=null;
		if(GSTR2.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
			headers=Arrays.asList("Invoice Date", "Invoice No","Document Type","Invoice Type","Return Period","Reverse Charge","Reverse Charge No","Transaction Date", "GSTIN", "Supplier ID","Supplier Name","Supplier PAN","Supplier TAN","SupplierTANPAN", "CompanyStateName", "Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Original Invoice Number","Original Invoice Date","Eway Bill Number", "Ledger", "State","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Supplier Notes","Terms & Conditions",customField1,customField2,customField3,customField4,"Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
					"Rate","Discount","Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","ITC Type", "ITC Available","Total Tax","Total Invoice Value","TDS Amount","Net Receivable(Total Invoice Value - TDS Amount)");
		}else if(GSTR2A.equals(returntype)){
			headers=Arrays.asList("Invoice Date", "Invoice No","Document Type","Invoice Type","Return Period","Reverse Charge","Reverse Charge No","Transaction Date", "GSTIN", "Place Of Supply", "Supplier ID","Supplier Name","Supplier PAN","Supplier TAN","SupplierTANPAN", "CompanyStateName", "Counter Party Filing Status","Original Invoice Number","Original Invoice Date","Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Eway Bill Number", "Ledger", "State","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Supplier Notes","Terms & Conditions",customField1,customField2,customField3,customField4,"Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
					"Rate","Discount","Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","ITC Type", "ITC Available","Total Tax","Total Invoice Value","TDS Amount","Net Receivable(Total Invoice Value - TDS Amount)");
		}else if(returntype.equals(MasterGSTConstants.EWAYBILL)) {
			headers=Arrays.asList("Document Date", "Document No", "Eway Bill No","Eway Bill Date","GSTIN", " Name", "Customer ID","Customer PAN","CustomerTAN","CustomerTANPAN","Customer LedgerName","CompanyGSTIN", "CompanyStateName", "Return Period",
					"Supply Type","SubSupply Type","Doc Type","From GSTIN","From Trade Name","From Addr1","From Addr2","From Place","From Pincode","From StateCode","To GSTIN","To Trade Name","To Addr1","ToAddr2",
					"To Place","To Pincode","To StateCode","Transporter Id","Transporter Name","Status","Actual Dist","No ValidDays","Valid Upto","Extended Times","rejectStatus","Vehicle Type","Transaction Type","Invoice Type", "State",
					customField1,customField2,customField3,customField4,"Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
					"Rate","Discount","Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","Total Tax","Total Invoice Value");
		}else if(returntype.equals(MasterGSTConstants.EINVOICE)) {
			headers=Arrays.asList("IRN No","IRN Status","Acknowledge No","Acknowledge Date","IRN Generated Date","Einv Status","Signed Qr code","Document Date", "Document No","Document Type","Invoice Type","Return Period","Reverse Charge", "GSTIN", "Customer ID","Customer Name","Customer PAN","CustomerTAN","CustomerTANPAN","Customer LedgerName", "CompanyStateName", "Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Eway Bill Number", "Ledger", "Place Of Supply","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage","Port Code","Shipping Bill No","Shipping Bill Date","Additional Currency Code","Exchange Rate",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Customer Notes","Terms & Conditions",customField1,customField2,customField3,customField4,"Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Bar Code","Quantity","Free Qty",
					"Rate","Discount","Exempted", "Other Charges","Assessable value","Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "State Cess","CESS Rate", "CESS Amount","Cess NonAdvol","Total Tax","Total Invoice Value","TCS Amount","Net Receivable(Total Invoice Value + TCS Amount)","Total Currency Value");
		}else {
			headers=Arrays.asList("Invoice Date", "Invoice No","Invoice Status","Document Type","Invoice Type","Return Period","Reverse Charge", "GSTIN", "Customer ID","Customer Name","Customer PAN","CustomerTAN","CustomerTANPAN","Customer LedgerName", "CompanyStateName", "Ecommerce GSTIN", "Billing Address","Shipment Address",
					"Eway Bill Number", "Ledger", "Place Of Supply","Reference", "Branch", "Vertical", "Differential Percentage(0.65)","Add TCS","Section",	"TCS Percentage","Port Code","Shipping Bill No","Shipping Bill Date","Additional Currency Code","Exchange Rate",
					"Bank Name","Account Number","Account Name","Branch Name","IFSC Code","Customer Notes","Terms & Conditions",customField1,customField2,customField3,customField4,"Item/Product/Service", "ITEM NOTES", "HSN / SAC Code","Unique Quantity Code","Quantity",
					"Rate","Discount","Exempted", "Taxable Value", "IGST Rate", "IGST Amount", "CGST Rate", "CGST Amount","SGST Rate", "SGST Amount", "CESS Rate", "CESS Amount","Total Tax","Total Invoice Value","TCS Amount","Net Receivable(Total Invoice Value + TCS Amount)","Total Currency Value");
		}
		if(invoiceVOList.size() < 10000) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			String fileName = "MGST_"+returntype+"_"+gstnumber+"_"+month+year+".xls";
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			File file = new File(fileName);
			FileInputStream in = null;
			OutputStream out = null;
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				SimpleExporter exporter = new SimpleExporter();
				if(GSTR2.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
					exporter.gridExport(headers, invoiceVOList,	"invoiceDate, invoiceNo,docType,type,returnPeriod,recharge, revChargeNo,transactionDate,customerGSTIN, customerID,customerName,customerPAN,customerTAN,customerTANPAN, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,originalInvoiceNo,originalInvoiceDate,ewayBillNumber,ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount,itcType, totalItc,totaltax,totalValue,tcsAmount,tcsNetAmount",fos);				
				}else if(GSTR2A.equals(returntype)){
					exporter.gridExport(headers, invoiceVOList,	"invoiceDate, invoiceNo,docType,type,returnPeriod,recharge, revChargeNo,transactionDate,customerGSTIN,placeOfSupply,customerID,customerName,customerPAN,customerTAN,customerTANPAN, companyStatename,counterFilingStatus,originalInvoiceNo,originalInvoiceDate,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount,itcType, totalItc,totaltax,totalValue,tcsAmount,tcsNetAmount",fos);
				}else if(returntype.equals(MasterGSTConstants.EWAYBILL)) {
					exporter.gridExport(headers, invoiceVOList,	"invoiceDate, invoiceNo, ewayBillNo,ewayBillDate,customerGSTIN, customerName,customerID,customerPAN,customerTAN,customerTANPAN,customerLedgerName,companyGSTIN,companyStatename, returnPeriod,supplyType,subSupplyType,docType,fromGstin,fromTrdName,fromAddr1,fromAddr2,fromPlace,fromPincode,fromStateCode,toGstin,toTrdName,toAddr1,toAddr2,toPlace,toPincode,toStateCode,transporterId,transporterName,status,actualDist,noValidDays,validUpto,extendedTimes,rejectStatus,vehicleType, transactionType,type, state,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue",fos);
				}else if(returntype.equals(MasterGSTConstants.EINVOICE)) {
					exporter.gridExport(headers, invoiceVOList, "irnNo,irnStatus,ackno,ackdt,irndt,einvstatus,qrcode,invoiceDate, invoiceNo,docType,type,returnPeriod,recharge, customerGSTIN, customerID,customerName,customerPAN,customerTAN,customerTANPAN,customerLedgerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,barCode,quantity,freeQty,rateperitem,itemDiscount,itemExmepted, othrCharges,assAmt,taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, stateCess,cessRate, cessAmount,cessnonAdvol, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);
				}else {
					exporter.gridExport(headers, invoiceVOList,	"invoiceDate, invoiceNo,gstStatus,docType,type,returnPeriod,recharge, customerGSTIN, customerID,customerName,customerPAN,customerTAN,customerTANPAN,customerLedgerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount,itemExmepted, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);				
				}
				in = new FileInputStream(file);
				out = response.getOutputStream();
				byte[] buffer= new byte[8192]; // use bigger if you want
				int length = 0;
				while ((length = in.read(buffer)) > 0){
				     out.write(buffer, 0, length);
				}
				if(isNotEmpty(in)) {
					in.close();
				}
			} catch (IOException e) {
				logger.error(CLASSNAME + "downloadExcelData : ERROR", e);
			}finally {
				try {
					file.delete();	
					if (isNotEmpty(out)) {
						out.close();
					}				
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			OutputStream nout = null;
			ZipOutputStream zipOutputStream = null;
			try {
				nout = response.getOutputStream();
				String fileName = "MGST_"+returntype+"_"+gstnumber+"_"+month+year+".zip";
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
				response.setContentType("application/octet-stream; charset=utf-8");
				zipOutputStream = new ZipOutputStream(nout);
				byte[] buf = new byte[1024];
				int c=0;
				int len = 0;
				double i = ((double)invoiceVOList.size())/60000;
				int j = (int)i;
				if(i-(int)i > 0) {
					j = (int)i+1;
				}
				List<List<InvoiceVO>> lt = Lists.newArrayList();
				int a=0;
				int b = 60000;
				if(invoiceVOList.size() < 60000) {
					b= invoiceVOList.size();
				}
				for(int k = 1; k <= j;k++) {
					lt.add(invoiceVOList.subList(a, b));
					a = b;
					if(k == j-1) {
						b = invoiceVOList.size();
					}else {
						b = b+60000;
					}
				}
				
				for(List<InvoiceVO> InvoicesList: lt) {
					File file1 = new File("MGST_"+returntype+"_"+gstnumber+"_"+month+year+"_"+(c+1)+".xls");
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					SimpleExporter exporter = new SimpleExporter();
					if(GSTR2.equals(returntype) || PURCHASE_REGISTER.equals(returntype) || "Unclaimed".equals(returntype)){
						exporter.gridExport(headers, InvoicesList,
								"invoiceDate, invoiceNo,docType,type,returnPeriod,recharge, revChargeNo,transactionDate,customerGSTIN, customerID,customerName,customerPAN,customerTAN,customerTANPAN, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,originalInvoiceNo,originalInvoiceDate,ewayBillNumber,ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount,itcType, totalItc,totaltax,totalValue,tcsAmount,tcsNetAmount",fos);				
					}else if(GSTR2A.equals(returntype)){
						exporter.gridExport(headers, InvoicesList,
								"invoiceDate, invoiceNo,docType,type,returnPeriod,recharge, revChargeNo,transactionDate,customerGSTIN,placeOfSupply,customerID,customerName,customerPAN,customerTAN,customerTANPAN, companyStatename,counterFilingStatus,originalInvoiceNo,originalInvoiceDate,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount,itcType, totalItc,totaltax,totalValue,tcsAmount,tcsNetAmount",fos);
					}else if(returntype.equals(MasterGSTConstants.EINVOICE)) {
						exporter.gridExport(headers, InvoicesList,
								"irnNo,irnStatus,ackno,ackdt,irndt,einvstatus,qrcode,invoiceDate, invoiceNo,docType,type,returnPeriod,recharge, customerGSTIN, customerID,customerName,customerPAN,customerTAN,customerTANPAN,customerLedgerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,barCode,quantity,freeQty,rateperitem,itemDiscount,itemExmepted, othrCharges,assAmt,taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, stateCess,cessRate, cessAmount,cessnonAdvol, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);
					}else {
						exporter.gridExport(headers, InvoicesList,
								"invoiceDate, invoiceNo,gstStatus,docType,type,returnPeriod,recharge, customerGSTIN, customerID,customerName,customerPAN,customerTAN,customerTANPAN,customerLedgerName, companyStatename,ecommerceGSTIN,billingAddress,shipingAddress,ewayBillNumber,ledgerName,state,reference,branch,vertical ,differentialPercentage,addTCS,tcsSection,tcsPercentage,portCode,shipBillNo,shipBillDate,additionalCurrencyCode,exchangeRate,bankName,accountNumber,accountName,branchName,ifsccode,customerNotes,termsAndConditions,customField1,customField2,customField3,customField4,itemno,itemNotescomments,hsnCode,uqc,quantity,rateperitem,itemDiscount,itemExmepted, taxableValue, igstRate, igstAmount, cgstRate, cgstAmount, sgstRate, sgstAmount, cessRate, cessAmount, totaltax,totalValue,tcsAmount,tcsNetAmount,currencyTotal",fos);				
					}
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
				
			} catch (IOException e) {
				logger.error(CLASSNAME + "downloadSupplierExcelData : ERROR", e);
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
	}
	
}
