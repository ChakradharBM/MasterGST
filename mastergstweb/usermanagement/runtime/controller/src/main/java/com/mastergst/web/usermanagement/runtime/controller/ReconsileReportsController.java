package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.configuration.service.StateRepository;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CustomData;
import com.mastergst.usermanagement.runtime.domain.CustomFields;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.ReconsileInvoices;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.GSTR2BSupport;
import com.mastergst.usermanagement.runtime.repository.CustomFieldsRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR2BSupportRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR2Repository;
import com.mastergst.usermanagement.runtime.repository.OtherConfigurationRepository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ReconsilationService;
import com.mastergst.usermanagement.runtime.service.ReconsileService;
import com.mastergst.usermanagement.runtime.support.Utility;

import net.sf.dynamicreports.jasper.builder.export.JasperXlsExporterBuilder;
import net.sf.dynamicreports.jasper.constant.JasperProperty;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.exception.DRException;

@Controller
public class ReconsileReportsController {
	
	private static final Logger logger = LogManager.getLogger(ReconsileReportsController.class.getName());
	private static final String CLASSNAME = "ReconsileReportsController::";
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private GSTR2Repository gstr2Repository;
	
	@Autowired
	private GSTR2BSupportRepository gstr2bSupportRepository;
	
	@Autowired
	private PurchaseRegisterRepository purchaseRepository;
	@Autowired
	StateRepository stateRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	OtherConfigurationRepository otherConfigurationRepository;
	
	@Autowired
	private ReconsileService reconsileService;
	
	@Autowired
	CustomFieldsRepository customFieldsRepository;
	
	@Autowired
	private ReconsilationService reconsilationService;
	
	//dwnldReconsilexls
	@RequestMapping(value = "/downloadReconsileInvsExcels/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody void getReconsileFinancialYearInvoicess(@PathVariable String id, @PathVariable String clientid,
										@PathVariable int month,@PathVariable int year, @RequestParam("dwnldtype") String dwnldtype,HttpServletResponse response, HttpServletRequest request) throws Exception {
		final String method = "getReconsileFinancialYearInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "userid\t" + id);
		logger.debug(CLASSNAME + method + "year\t" + year);
		
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String clientname = "";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			clientname = client.getBusinessname().replace("/", "");
		}
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "inline; filename='MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+year+".xls");
		
		
		Date stDate = null;
		Date endDate = null;
		Calendar cals = Calendar.getInstance();
		cals.set(year, 3, 1, 0, 0, 0);
		stDate = new java.util.Date(cals.getTimeInMillis());
		cals = Calendar.getInstance();
		cals.set(year+1, 3, 0, 23, 59, 59);
		endDate = new java.util.Date(cals.getTimeInMillis());
		
		String rtStart = "04"+year;
		String rtEnd = "03"+(year+1);		
		List<String> rtArray=Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd);

		
		List<ReconsileInvoices> reconsileInvoicesLst=getReconsileExcelData("Financialyear",clientid, month, year, stDate, endDate, rtArray);
		
		TextColumnBuilder<String> prInvTypeColumn = col.column("Invoice Type", "purchaseInvtype", type.stringType());
		TextColumnBuilder<String> prSuppliernameColumn = col.column("Supplier Name", "purchaseSuppliername", type.stringType());
		TextColumnBuilder<String> prInvoicenoColumn = col.column("Invoice Number", "purchaseInvoiceno", type.stringType());
		TextColumnBuilder<String> prInvoicedateColumn = col.column("Invoice Date", "purchaseInvoicedate", type.stringType());
		TextColumnBuilder<String> prGstnoColumn = col.column("GSTN No", "purchaseGstno", type.stringType());
		TextColumnBuilder<Double> prTotaltaxableamountColumn = col.column("Taxable Amount", "purchaseTotaltaxableamount", type.doubleType());
		TextColumnBuilder<Double> prTotalamountColumn = col.column("Total Tax", "purchaseTotalamount", type.doubleType());
		TextColumnBuilder<Double> prTotalIgstamountColumn = col.column("Total IGST", "purchaseTotalIgstAmount", type.doubleType());
		TextColumnBuilder<Double> prTotalCgstamountColumn = col.column("Total CGST", "purchaseTotalCgstAmount", type.doubleType());
		TextColumnBuilder<Double> prTotalSgstamountColumn = col.column("Total SGST", "purchaseTotalSgstAmount", type.doubleType());
		TextColumnBuilder<String> prBranchColumn = col.column("Branch", "purchaseBranch", type.stringType());
		
		TextColumnBuilder<String> statusColumn = col.column("Status", "status", type.stringType());
		
		TextColumnBuilder<String> gstr2InvTypeColumn = col.column("Invoice Type", "gstr2Invtype", type.stringType());
		TextColumnBuilder<String> gstr2SuppliernameColumn = col.column("Supplier Name", "gstr2Suppliername", type.stringType());
		TextColumnBuilder<String> gstr2InvoicenoColumn = col.column("Invoice Number", "gstr2Invoiceno", type.stringType());
		TextColumnBuilder<String> gstr2InvoicedateColumn = col.column("Invoice Date", "gstr2Invoicedate", type.stringType());
		TextColumnBuilder<String> gstr2GstnoColumn = col.column("GSTN No", "gstr2Gstno", type.stringType());
		TextColumnBuilder<Double> gstr2TotaltaxableamountColumn = col.column("Taxable Amount", "gstr2Totaltaxableamount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalamountColumn = col.column("Total Tax", "gstr2Totalamount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalIgstamountColumn = col.column("Total IGST", "gstr2TotalIgstAmount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalCgstamountColumn = col.column("Total CGST", "gstr2TotalCgstAmount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalSgstamountColumn = col.column("Total SGST", "gstr2TotalSgstAmount", type.doubleType());
		TextColumnBuilder<String> gstr2BranchColumn = col.column("Branch", "gstr2Branch", type.stringType());

		HorizontalListBuilder left = cmp.horizontalList().add(cmp.text("Books (Purchase)"));
		HorizontalListBuilder right = cmp.horizontalList().add(cmp.text("GSTR2A"));
		HorizontalListBuilder title = cmp.horizontalList().add(left, right);
		
		if(dwnldtype.equalsIgnoreCase("alldetailswise")) {
			String customField1 = "CustomField1";
			String customField2 = "CustomField2";
			String customField3 = "CustomField3";
			String customField4 = "CustomField4";
			if(NullUtil.isNotEmpty(client)){
				gstnumber = client.getGstnnumber();
				CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
				if(isNotEmpty(customFields)) {
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
				}
			}
			TextColumnBuilder<String> prFpColumn = col.column("Return Period", "purchaseReturnPeriod", type.stringType());
			TextColumnBuilder<String> prRcColumn = col.column("Reverse Charge", "purchaseReverseCharge", type.stringType());
			TextColumnBuilder<String> prTdColumn = col.column("Transaction Date", "purchaseTransactionDate", type.stringType());
			TextColumnBuilder<String> prCSNColumn = col.column("Company State Name", "purchaseCompanyStateName", type.stringType());
			TextColumnBuilder<String> prStateColumn = col.column("State", "purchaseState", type.stringType());
			TextColumnBuilder<String> prCustomField1Column = col.column(customField1, "purchasecustomField1", type.stringType());
			TextColumnBuilder<String> prCustomField2Column = col.column(customField2, "purchasecustomField2", type.stringType());
			TextColumnBuilder<String> prCustomField3Column = col.column(customField3, "purchasecustomField3", type.stringType());
			TextColumnBuilder<String> prCustomField4Column = col.column(customField4, "purchasecustomField4", type.stringType());
			TextColumnBuilder<String> gstr2FpColumn = col.column("Return Period", "gstr2ReturnPeriod", type.stringType());
			TextColumnBuilder<String> gstr2RcColumn = col.column("Reverse Charge", "gstr2ReverseCharge", type.stringType());
			TextColumnBuilder<String> gstr2TdColumn = col.column("Transaction Date", "gstr2TransactionDate", type.stringType());
			TextColumnBuilder<String> gstr2CSNColumn = col.column("Company State Name", "gstr2CompanyStateName", type.stringType());
			TextColumnBuilder<String> gstr2StateColumn = col.column("State", "gstr2State", type.stringType());
			TextColumnBuilder<String> gstr2CustomField1Column = col.column(customField1, "gstr2customField1", type.stringType());
			TextColumnBuilder<String> gstr2CustomField2Column = col.column(customField2, "gstr2customField2", type.stringType());
			TextColumnBuilder<String> gstr2CustomField3Column = col.column(customField3, "gstr2customField3", type.stringType());
			TextColumnBuilder<String> gstr2CustomField4Column = col.column(customField4, "gstr2customField4", type.stringType());
			
			try {
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				response.setHeader("Content-Disposition", "inline; filename='MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+"_"+year+".xls");
	
	            OutputStream outputStream = response.getOutputStream();
				
				JasperXlsExporterBuilder xlsExporter = export.xlsExporter(outputStream)
					.addSheetName("MasterGST")
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
						prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
						prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,
						prTotalSgstamountColumn,prTotalCgstamountColumn, prBranchColumn,
						prFpColumn,prRcColumn,prTdColumn,prCSNColumn,prStateColumn,prCustomField1Column,
						prCustomField2Column,prCustomField3Column,prCustomField4Column,
						statusColumn, 
						gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
						gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,
						gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn, gstr2BranchColumn,
						gstr2FpColumn,gstr2RcColumn,gstr2TdColumn,gstr2CSNColumn,gstr2StateColumn,
						gstr2CustomField1Column,gstr2CustomField2Column,gstr2CustomField3Column,gstr2CustomField4Column
					)
					.setDataSource(reconsileInvoicesLst)
					.toXls(xlsExporter);
				outputStream.flush();
	            outputStream.close();
			} catch (DRException e) {
				e.printStackTrace();
			}
		}else {
			try {
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				response.setHeader("Content-Disposition", "inline; filename='MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+"_"+year+".xls");
	
	            OutputStream outputStream = response.getOutputStream();
				
				JasperXlsExporterBuilder xlsExporter = export.xlsExporter(outputStream)
					.addSheetName("MasterGST")
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
						prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
						prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,
						prTotalSgstamountColumn,prTotalCgstamountColumn, prBranchColumn,
						
						statusColumn, 
						gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
						gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,
						gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn, gstr2BranchColumn
					)
					.setDataSource(reconsileInvoicesLst)
					.toXls(xlsExporter);
				outputStream.flush();
	            outputStream.close();
			} catch (DRException e) {
				e.printStackTrace();
			}
		}
		
		//return new FileSystemResource(new File("MGST_Reconciliation_"+clientname+"_"+gstnumber+year+".xls"));
	}
	
	public List<ReconsileInvoices> getReconsileExcelData(String exceltype, String clientid, int month, int year, Date stDate,Date endDate, List<String> rtArray){
		OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
		Boolean billdate = false;
		if(isNotEmpty(otherconfig)){
			billdate = otherconfig.isEnableTransDate();
		}
		List<ReconsileInvoices> reconsileInvoicesLst=new ArrayList<ReconsileInvoices>();
		
		Client client = clientService.findById(clientid);
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
		
		List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.IMP_GOODS);
		Page<? extends InvoiceParent> pr_nostatus_invoices = null;
		Page<? extends InvoiceParent> pr_status_invoices = null;
		Page<? extends InvoiceParent> gstr2invoices = gstr2Repository.findByClientidAndIsAmendmentAndFpInAndInvtypeIn(client.getId().toString(),true, rtArray, invTypes, pageable);
		if("Financialyear".equalsIgnoreCase(exceltype)) {
			pr_nostatus_invoices = clientService.getPresentMonthfyinvsMatchingIdisNull(clientid, stDate, endDate);
			pr_status_invoices = clientService.getPresentMonthfyinvsMatchingIdisNotNull(clientid, stDate, endDate);
		}else {
			if(isEmpty(otherconfig) || !billdate) {
				pr_nostatus_invoices = clientService.getPresentMonthfyinvsMatchingIdisNull(clientid, stDate, endDate);
				pr_status_invoices = clientService.getPresentMonthfyinvsMatchingIdisNotNull(clientid, stDate, endDate);
			}else {
				pr_nostatus_invoices = reconsileService.getPresentMonthfyinvsMatchingIdisNullByTransactionDate(clientid, stDate, endDate);
				pr_status_invoices = reconsileService.getPresentMonthfyinvsMatchingIdisNotNullByTransactionDate(clientid, stDate, endDate);
			}
		}
		List<String> pr_matchingids = new ArrayList<String>();
		Map<String, InvoiceParent> gstr2Map=new HashMap<String, InvoiceParent>();
		if(isNotEmpty(pr_status_invoices) && isNotEmpty(pr_status_invoices.getContent()) && pr_status_invoices.getContent().size()>0) {
			for(InvoiceParent inv:pr_status_invoices.getContent()) {
				if(isNotEmpty(inv.getMatchingId())) {
					pr_matchingids.add(inv.getMatchingId());
					if(isNotEmpty(inv.getMatchingStatus()) && !inv.getMatchingStatus().equalsIgnoreCase(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)) {
						InvoiceParent gstr2invoice = gstr2Repository.findOne(inv.getMatchingId());
						if(isNotEmpty(gstr2invoice)) {
							gstr2Map.put(gstr2invoice.getId().toString(), gstr2invoice);
						}
					}
				}else {
					reconsileInvoicesLst.add(addReconsileInvoices(purchaseRepository.findOne(inv.getId().toString())));
				}
			}
		}
		
		List<String> gstr2_matchingids = Lists.newArrayList();
		Set<String> gstr2_mappedids = new HashSet<String>();
		Set<String> purchase_mappedids = new HashSet<String>();
		
		if(isNotEmpty(gstr2invoices) && isNotEmpty(gstr2invoices.getContent()) && gstr2invoices.getContent().size() > 0) {
			for(InvoiceParent inv : gstr2invoices.getContent()) {
				if(isNotEmpty(inv.getMatchingStatus()) && !inv.getMatchingStatus().equalsIgnoreCase(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)) {
					gstr2_matchingids.add(inv.getId().toString());
					gstr2Map.put(inv.getId().toString(), inv);
				}
			}
		}
		gstr2_matchingids.addAll(pr_matchingids);
		
		Page<? extends InvoiceParent> purchase_gstr2_matchedidinvoices = clientService.getgstr2MatchingIdsInvoices(clientid, gstr2_matchingids);
		
		if(isNotEmpty(purchase_gstr2_matchedidinvoices) && isNotEmpty(purchase_gstr2_matchedidinvoices.getContent()) && purchase_gstr2_matchedidinvoices.getContent().size() > 0) {
			purchase_gstr2_matchedidinvoices.getContent().forEach(inv -> {
				inv.setTotaltaxableamount(inv.getTotaltaxableamount() * inv.getSftr());
				inv.setTotaltax(inv.getTotaltax() * inv.getSftr());
				
				inv.setTotalIgstAmount(inv.getTotalIgstAmount() * inv.getSftr());
				inv.setTotalCgstAmount(inv.getTotalCgstAmount() * inv.getSftr());
				inv.setTotalSgstAmount(inv.getTotalSgstAmount() * inv.getSftr());
				if(isNotEmpty(inv.getTotalCessAmount())) {
					inv.setTotalCessAmount(inv.getTotalCessAmount() * inv.getSftr());
				}else {
					inv.setTotalCessAmount(0d * inv.getSftr());
				}
			});
			for(InvoiceParent invoice: purchase_gstr2_matchedidinvoices.getContent()) {
				purchase_mappedids.add(invoice.getId().toString());
				ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
				
				reconsileInvoices.setPurchaseStatus(invoice.getMatchingStatus());
				if(isNotEmpty(invoice.getMatchingStatus())) {
					reconsileInvoices.setStatus(invoice.getMatchingStatus());
				}
				if(isNotEmpty(invoice.getInvtype())) {
					reconsileInvoices.setPurchaseInvtype(invoice.getInvtype());
				}
				if(isNotEmpty(invoice.getInvoiceno())) {
					reconsileInvoices.setPurchaseInvoiceno(invoice.getInvoiceno());
				}
				if(isNotEmpty(invoice.getDateofinvoice())) {
					reconsileInvoices.setPurchaseInvoicedate(sdf.format(invoice.getDateofinvoice()));
				}
				
				if (isNotEmpty(invoice.getBilledtoname())) {
					reconsileInvoices.setPurchaseSuppliername(invoice.getBilledtoname());
				}
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
						reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
					}
				}else {
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						reconsileInvoices.setPurchaseGstno(invoice.getB2b().get(0).getCtin());
					}
				}
				if(isNotEmpty(invoice.getBranch())) {
					reconsileInvoices.setPurchaseBranch(invoice.getBranch());					
				}
				if(isNotEmpty(invoice.getTotaltaxableamount())) {
					reconsileInvoices.setPurchaseTotaltaxableamount(invoice.getTotaltaxableamount());
				}
				if(isNotEmpty(invoice.getTotaltax())) {
					reconsileInvoices.setPurchaseTotalamount(invoice.getTotaltax());
				}
				if(isNotEmpty(invoice.getTotalIgstAmount())) {
					reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
				}
				if(isNotEmpty(invoice.getTotalCgstAmount())) {
					reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
				}
				if(isNotEmpty(invoice.getTotalSgstAmount())) {
					reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
				}
				if(isNotEmpty(invoice.getTotalCessAmount())) {
					reconsileInvoices.setPurchaseTotalCessAmount(invoice.getTotalCessAmount());
				}
				if(isNotEmpty(invoice.getRevchargetype())) {
					reconsileInvoices.setPurchaseReverseCharge(invoice.getRevchargetype());
				}
				if(isNotEmpty(invoice.getFp())) {
					reconsileInvoices.setPurchaseReturnPeriod(invoice.getFp());
				}
				
				if(isNotEmpty(invoice.getBillDate())) {
					reconsileInvoices.setPurchaseTransactionDate(sdf.format(invoice.getBillDate()));
				}
				
				if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
					reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
				}
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
						  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
						  StateConfig gstcode =  null;
						  if(Integer.parseInt(gstno) < 10) {
							  String tagName = gstno;
							  Query query = new Query();
							  query.limit(37);
							  query.addCriteria(Criteria.where("name").regex(tagName));
							  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
							  if(isNotEmpty(state)) {
								gstcode = state.get(0);  
							  }
						  }else {
							  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
						  }
						  if(isNotEmpty(gstcode)) {
							  reconsileInvoices.setPurchaseState(gstcode.getName()); 
						  }
						}
				}else {
				  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
					  StateConfig gstcode =  null;
					  if(Integer.parseInt(gstno) < 10) {
						  String tagName = gstno;
						  Query query = new Query();
						  query.limit(37);
						  query.addCriteria(Criteria.where("name").regex(tagName));
						  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
						  if(isNotEmpty(state)) {
							gstcode = state.get(0);  
						  }
					  }else {
						  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
					  }
					  if(isNotEmpty(gstcode)) {
						  reconsileInvoices.setPurchaseState(gstcode.getName()); 
					  } 
				  }
				}
				
				if(isNotEmpty(invoice.getCustomField1())) {
					reconsileInvoices.setPurchasecustomField1(invoice.getCustomField1());
				}
				if(isNotEmpty(invoice.getCustomField2())) {
					reconsileInvoices.setPurchasecustomField2(invoice.getCustomField2());
				}
				if(isNotEmpty(invoice.getCustomField3())) {
					reconsileInvoices.setPurchasecustomField3(invoice.getCustomField3());
				}
				if(isNotEmpty(invoice.getCustomField4())) {
					reconsileInvoices.setPurchasecustomField4(invoice.getCustomField4());
				}
				
				
				if(isNotEmpty(invoice.getMatchingId())) {
					InvoiceParent gstr2Inv= gstr2Map.get(invoice.getMatchingId());
					if(isNotEmpty(gstr2Inv)) {
						String matchingstatus = "Mismatched";
						if(!invoice.getMatchingStatus().equalsIgnoreCase("Matched") && !invoice.getMatchingStatus().equalsIgnoreCase("Round Off Matched") && !invoice.getMatchingStatus().equalsIgnoreCase("Matched In Other Months") && !invoice.getMatchingStatus().equalsIgnoreCase("Probable Matched")) {
							if((isNotEmpty(invoice.getInvoiceno()) && isNotEmpty(gstr2Inv.getInvoiceno()) && invoice.getInvoiceno().trim().equals(gstr2Inv.getInvoiceno().trim()))
									&& (isNotEmpty(invoice.getDateofinvoice()) && isNotEmpty(gstr2Inv.getDateofinvoice()) &&  sdf.format(invoice.getDateofinvoice()).equals(sdf.format(gstr2Inv.getDateofinvoice())))
									&& (isNotEmpty(invoice.getB2b().get(0).getCtin()) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin()) && invoice.getB2b().get(0).getCtin().equals(gstr2Inv.getB2b().get(0).getCtin()))
									&& (isNotEmpty(invoice.getTotaltaxableamount()) && isNotEmpty(gstr2Inv.getTotaltaxableamount()) && invoice.getTotaltaxableamount().equals(gstr2Inv.getTotaltaxableamount()))
									&& (isNotEmpty(invoice.getTotalamount()) && isNotEmpty(gstr2Inv.getTotalamount()) && invoice.getTotalamount().equals(gstr2Inv.getTotalamount()))
									&& (isNotEmpty(invoice.getTotaltax()) && isNotEmpty(gstr2Inv.getTotaltax()) && invoice.getTotaltax().equals(gstr2Inv.getTotaltax()))) {
									matchingstatus = "Matched";
							}else if((isNotEmpty(invoice.getInvoiceno()) && isNotEmpty(gstr2Inv.getInvoiceno()) && invoice.getInvoiceno().trim().equals(gstr2Inv.getInvoiceno().trim()))
									&& (isNotEmpty(invoice.getDateofinvoice()) && isNotEmpty(gstr2Inv.getDateofinvoice()) && sdf.format(invoice.getDateofinvoice()).equals(sdf.format(gstr2Inv.getDateofinvoice())))
									&& (isNotEmpty(invoice.getB2b().get(0).getCtin()) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin()) && !invoice.getB2b().get(0).getCtin().equals(gstr2Inv.getB2b().get(0).getCtin()))
									&& (isNotEmpty(invoice.getTotaltaxableamount()) && isNotEmpty(gstr2Inv.getTotaltaxableamount()) && invoice.getTotaltaxableamount().equals(gstr2Inv.getTotaltaxableamount()))
									&& (isNotEmpty(invoice.getTotalamount()) && isNotEmpty(gstr2Inv.getTotalamount()) && invoice.getTotalamount().equals(gstr2Inv.getTotalamount()))
									&& (isNotEmpty(invoice.getTotaltax()) && isNotEmpty(gstr2Inv.getTotaltax()) && invoice.getTotaltax().equals(gstr2Inv.getTotaltax()))) {
									matchingstatus = "GST No Mismatched";
							}else if((isNotEmpty(invoice.getInvoiceno()) && isNotEmpty(gstr2Inv.getInvoiceno()) && invoice.getInvoiceno().trim().equals(gstr2Inv.getInvoiceno()))
									&& (isNotEmpty(invoice.getDateofinvoice()) && isNotEmpty(gstr2Inv.getDateofinvoice()) && sdf.format(invoice.getDateofinvoice()).equals(sdf.format(gstr2Inv.getDateofinvoice())))
									&& (isNotEmpty(invoice.getB2b().get(0).getCtin()) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin()) && invoice.getB2b().get(0).getCtin().equals(gstr2Inv.getB2b().get(0).getCtin()))
									&& ((isNotEmpty(invoice.getTotaltaxableamount()) && isNotEmpty(gstr2Inv.getTotaltaxableamount()) && !invoice.getTotaltaxableamount().equals(gstr2Inv.getTotaltaxableamount()))
									|| (isNotEmpty(invoice.getTotalamount()) && isNotEmpty(gstr2Inv.getTotalamount()) && !invoice.getTotalamount().equals(gstr2Inv.getTotalamount())))
									&& (isNotEmpty(invoice.getTotaltax()) && isNotEmpty(gstr2Inv.getTotaltax()) && invoice.getTotaltax().equals(gstr2Inv.getTotaltax()))) {
									matchingstatus = "Invoice Value Mismatched";
							}else if((isNotEmpty(invoice.getInvoiceno()) && isNotEmpty(gstr2Inv.getInvoiceno()) && invoice.getInvoiceno().trim().equals(gstr2Inv.getInvoiceno()))
									&& (isNotEmpty(invoice.getDateofinvoice()) && isNotEmpty(gstr2Inv.getDateofinvoice()) && sdf.format(invoice.getDateofinvoice()).equals(sdf.format(gstr2Inv.getDateofinvoice())))
									&& (isNotEmpty(invoice.getB2b().get(0).getCtin()) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin()) && invoice.getB2b().get(0).getCtin().equals(gstr2Inv.getB2b().get(0).getCtin()))
									&& (isNotEmpty(invoice.getTotaltaxableamount()) && isNotEmpty(gstr2Inv.getTotaltaxableamount()) && invoice.getTotaltaxableamount().equals(gstr2Inv.getTotaltaxableamount()))
									&& (isNotEmpty(invoice.getTotalamount()) && isNotEmpty(gstr2Inv.getTotalamount()) && invoice.getTotalamount().equals(gstr2Inv.getTotalamount()))
									&& (isNotEmpty(invoice.getTotaltax()) && isNotEmpty(gstr2Inv.getTotaltax()) && !invoice.getTotaltax().equals(gstr2Inv.getTotaltax()))) {
									matchingstatus = "Tax Mismatched";
							}else if((isNotEmpty(invoice.getInvoiceno()) && isNotEmpty(gstr2Inv.getInvoiceno()) && !invoice.getInvoiceno().trim().equals(gstr2Inv.getInvoiceno()))
									&& (isNotEmpty(invoice.getDateofinvoice()) && isNotEmpty(gstr2Inv.getDateofinvoice()) && sdf.format(invoice.getDateofinvoice()).equals(sdf.format(gstr2Inv.getDateofinvoice())))
									&& (isNotEmpty(invoice.getB2b().get(0).getCtin()) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin()) && invoice.getB2b().get(0).getCtin().equals(gstr2Inv.getB2b().get(0).getCtin()))
									&& (isNotEmpty(invoice.getTotaltaxableamount()) && isNotEmpty(gstr2Inv.getTotaltaxableamount()) && invoice.getTotaltaxableamount().equals(gstr2Inv.getTotaltaxableamount()))
									&& (isNotEmpty(invoice.getTotalamount()) && isNotEmpty(gstr2Inv.getTotalamount()) && invoice.getTotalamount().equals(gstr2Inv.getTotalamount()))
									&& (isNotEmpty(invoice.getTotaltax()) && isNotEmpty(gstr2Inv.getTotaltax()) && invoice.getTotaltax().equals(gstr2Inv.getTotaltax()))) {
									matchingstatus = "Invoice No Mismatched";
							}else if((isNotEmpty(invoice.getInvoiceno()) && isNotEmpty(gstr2Inv.getInvoiceno()) && invoice.getInvoiceno().trim().equals(gstr2Inv.getInvoiceno()))
									&& (isNotEmpty(invoice.getDateofinvoice()) && isNotEmpty(gstr2Inv.getDateofinvoice()) && !sdf.format(invoice.getDateofinvoice()).equals(sdf.format(gstr2Inv.getDateofinvoice())))
									&& (isNotEmpty(invoice.getB2b().get(0).getCtin()) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin()) && invoice.getB2b().get(0).getCtin().equals(gstr2Inv.getB2b().get(0).getCtin()))
									&& (isNotEmpty(invoice.getTotaltaxableamount()) && isNotEmpty(gstr2Inv.getTotaltaxableamount()) && invoice.getTotaltaxableamount().equals(gstr2Inv.getTotaltaxableamount()))
									&& (isNotEmpty(invoice.getTotalamount()) && isNotEmpty(gstr2Inv.getTotalamount()) && invoice.getTotalamount().equals(gstr2Inv.getTotalamount()))
									&& (isNotEmpty(invoice.getTotaltax()) && isNotEmpty(gstr2Inv.getTotaltax()) && invoice.getTotaltax().equals(gstr2Inv.getTotaltax()))) {
									matchingstatus = "Invoice Date Mismatched";
							}
							
							reconsileInvoices.setStatus(matchingstatus);
						}
						if(isNotEmpty(invoice.getMatchingId())) {
							gstr2_mappedids.add(invoice.getMatchingId());
						}
						if(isNotEmpty(gstr2Inv.getInvtype())) {
							reconsileInvoices.setGstr2Invtype(gstr2Inv.getInvtype());
						}
						if(isNotEmpty(gstr2Inv.getInvoiceno())) {
							reconsileInvoices.setGstr2Invoiceno(gstr2Inv.getInvoiceno());
						}
						if(isNotEmpty(gstr2Inv.getDateofinvoice())) {
							reconsileInvoices.setGstr2Invoicedate(sdf.format(gstr2Inv.getDateofinvoice()));
						}
						if(isNotEmpty(gstr2Inv.getTotaltaxableamount())) {
							reconsileInvoices.setGstr2Totaltaxableamount(gstr2Inv.getTotaltaxableamount() * gstr2Inv.getSftr());
						}
						if(isNotEmpty(gstr2Inv.getTotaltax())) {
							reconsileInvoices.setGstr2Totalamount(gstr2Inv.getTotaltax() * gstr2Inv.getSftr());
						}
						if(isNotEmpty(invoice.getMatchingStatus())) {
							reconsileInvoices.setGstr2status(invoice.getMatchingStatus());
						}
						if(isNotEmpty(gstr2Inv.getTotalIgstAmount())) {
							reconsileInvoices.setGstr2TotalIgstAmount(gstr2Inv.getTotalIgstAmount() * gstr2Inv.getSftr());
						}
						if(isNotEmpty(gstr2Inv.getTotalCgstAmount())) {
							reconsileInvoices.setGstr2TotalCgstAmount(gstr2Inv.getTotalCgstAmount() * gstr2Inv.getSftr());
						}
						if(isNotEmpty(gstr2Inv.getTotalSgstAmount())) {
							reconsileInvoices.setGstr2TotalSgstAmount(gstr2Inv.getTotalSgstAmount() * gstr2Inv.getSftr());
						}
						if(isNotEmpty(gstr2Inv.getTotalCessAmount())) {
							reconsileInvoices.setGstr2TotalCessAmount(gstr2Inv.getTotalCessAmount() * gstr2Inv.getSftr());
						}
						if(isNotEmpty(gstr2Inv.getBranch())) {
							reconsileInvoices.setGstr2Branch(gstr2Inv.getBranch());
						}
						if (isNotEmpty(gstr2Inv.getBilledtoname())) {
							reconsileInvoices.setGstr2Suppliername(gstr2Inv.getBilledtoname());
						}
						if(gstr2Inv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
							if (isNotEmpty(((GSTR2)gstr2Inv).getImpGoods()) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0)) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin())) {
								reconsileInvoices.setGstr2Gstno(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin());
							}
						}else {
							if (isNotEmpty(gstr2Inv.getB2b()) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin())) {
								reconsileInvoices.setGstr2Gstno(gstr2Inv.getB2b().get(0).getCtin());
							}
						}
						
						if(isNotEmpty(gstr2Inv.getRevchargetype())) {
							reconsileInvoices.setGstr2ReverseCharge(gstr2Inv.getRevchargetype());
						}
						if(isNotEmpty(gstr2Inv.getFp())) {
							reconsileInvoices.setGstr2ReturnPeriod(gstr2Inv.getFp());
						}
						
						if(isNotEmpty(gstr2Inv.getBillDate())) {
							reconsileInvoices.setGstr2TransactionDate(sdf.format(gstr2Inv.getBillDate()));
						}
						
						if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
							reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
						}
						if(gstr2Inv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
							if (isNotEmpty(((GSTR2)gstr2Inv).getImpGoods()) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0)) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin())) {
								  String gstno = ((GSTR2)gstr2Inv).getImpGoods().get(0).getStin().trim().substring(0, 2); 
								  StateConfig gstcode =  null;
								  if(Integer.parseInt(gstno) < 10) {
									  String tagName = gstno;

									  Query query = new Query();
									  query.limit(37);
									  query.addCriteria(Criteria.where("name").regex(tagName));
									  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
									  if(isNotEmpty(state)) {
										gstcode = state.get(0);  
									  }
									  
								  }else {
									  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
								  }
								   
								  if(isNotEmpty(gstcode)) {
									  reconsileInvoices.setGstr2State(gstcode.getName()); 
								  }
							}
						}else {
						  if(isNotEmpty(gstr2Inv.getB2b()) && isNotEmpty(gstr2Inv.getB2b().get(0)) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin())) {
							  String gstno = gstr2Inv.getB2b().get(0).getCtin().trim().substring(0, 2); 
							  StateConfig gstcode =  null;
							  if(Integer.parseInt(gstno) < 10) {
								  String tagName = gstno;

								  Query query = new Query();
								  query.limit(37);
								  query.addCriteria(Criteria.where("name").regex(tagName));
								  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
								  if(isNotEmpty(state)) {
									gstcode = state.get(0);  
								  }
								  
							  }else {
								  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
							  }
							   
							  if(isNotEmpty(gstcode)) {
								  reconsileInvoices.setGstr2State(gstcode.getName()); 
							  } 
						  }
						}
						  if(isNotEmpty(gstr2Inv.getCustomField1())) {
							reconsileInvoices.setGstr2customField1(gstr2Inv.getCustomField1());
						  }
						  if(isNotEmpty(gstr2Inv.getCustomField2())) {
							reconsileInvoices.setGstr2customField2(gstr2Inv.getCustomField2());
						  }
						  if(isNotEmpty(gstr2Inv.getCustomField3())) {
							reconsileInvoices.setGstr2customField3(gstr2Inv.getCustomField3());
						  }
						  if(isNotEmpty(gstr2Inv.getCustomField4())) {
							reconsileInvoices.setGstr2customField4(gstr2Inv.getCustomField4());
						  }
						
					}else {
						reconsileInvoices.setStatus("Not in GSTR2A");
						reconsileInvoices.setGstr2Invtype("-");
						reconsileInvoices.setGstr2Invoiceno("-");
						reconsileInvoices.setGstr2Invoicedate("-");
						reconsileInvoices.setGstr2Branch("-");
						reconsileInvoices.setGstr2Suppliername("-");
						reconsileInvoices.setGstr2Gstno("-");
						reconsileInvoices.setGstr2CompanyStateName("-");
						reconsileInvoices.setGstr2ReturnPeriod("-");
						reconsileInvoices.setGstr2ReverseCharge("-");
						reconsileInvoices.setGstr2State("-");
						reconsileInvoices.setGstr2TransactionDate("-");
						reconsileInvoices.setGstr2customField1("-");
						reconsileInvoices.setGstr2customField2("-");
						reconsileInvoices.setGstr2customField3("-");
						reconsileInvoices.setGstr2customField4("-");
					}
				}else {
					reconsileInvoices.setStatus("Not in GSTR2A");
					reconsileInvoices.setGstr2Invtype("-");
					reconsileInvoices.setGstr2Invoiceno("-");
					reconsileInvoices.setGstr2Invoicedate("-");
					/*
					 * reconsileInvoices.setGstr2Totaltaxableamount(0.0);
					 * reconsileInvoices.setGstr2Totalamount(0.0);
					 */
					reconsileInvoices.setGstr2Branch("-");
					reconsileInvoices.setGstr2Suppliername("-");
					reconsileInvoices.setGstr2Gstno("-");
					reconsileInvoices.setGstr2CompanyStateName("-");
					reconsileInvoices.setGstr2ReturnPeriod("-");
					reconsileInvoices.setGstr2ReverseCharge("-");
					reconsileInvoices.setGstr2State("-");
					reconsileInvoices.setGstr2TransactionDate("-");
					reconsileInvoices.setGstr2customField1("-");
					reconsileInvoices.setGstr2customField2("-");
					reconsileInvoices.setGstr2customField3("-");
					reconsileInvoices.setGstr2customField4("-");
				}
				reconsileInvoicesLst.add(reconsileInvoices);
			}
		}
		List<String> prmatchingstatus = new ArrayList<String>();
		prmatchingstatus.add(MasterGSTConstants.GST_STATUS_NOTINGSTR2A);
		Page<? extends InvoiceParent> pr_notingstr2astatus_invoices= null;
		if("Financialyear".equalsIgnoreCase(exceltype)) {
			pr_notingstr2astatus_invoices= purchaseRepository.findByClientidAndInvtypeInAndMatchingStatusInAndDateofinvoiceBetween(clientid, invTypes, prmatchingstatus, stDate, endDate, pageable);
		}else {
			if(isEmpty(otherconfig) || !billdate) {
				pr_notingstr2astatus_invoices= purchaseRepository.findByClientidAndInvtypeInAndMatchingStatusInAndDateofinvoiceBetween(clientid, invTypes, prmatchingstatus, stDate, endDate, pageable);
			}else {
				pr_notingstr2astatus_invoices= purchaseRepository.findByClientidAndInvtypeInAndMatchingStatusInAndBillDateBetween(clientid, invTypes, prmatchingstatus, stDate, endDate, pageable);
			}
		}
		if(isNotEmpty(pr_notingstr2astatus_invoices) && isNotEmpty(pr_notingstr2astatus_invoices.getContent()) && pr_notingstr2astatus_invoices.getContent().size()>0) {
			pr_notingstr2astatus_invoices.getContent().forEach(inv -> {
				inv.setTotaltaxableamount(inv.getTotaltaxableamount() * inv.getSftr());
				inv.setTotaltax(inv.getTotaltax() * inv.getSftr());
				
				inv.setTotalIgstAmount(inv.getTotalIgstAmount() * inv.getSftr());
				inv.setTotalCgstAmount(inv.getTotalCgstAmount() * inv.getSftr());
				inv.setTotalSgstAmount(inv.getTotalSgstAmount() * inv.getSftr());
				if(isNotEmpty(inv.getTotalCessAmount())) {
					inv.setTotalCessAmount(inv.getTotalCessAmount() * inv.getSftr());
				}else {
					inv.setTotalCessAmount(0d * inv.getSftr());
				}
			});
			for(InvoiceParent invoice:pr_notingstr2astatus_invoices.getContent()){
				
				purchase_mappedids.add(invoice.getId().toString());
				ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
				
				reconsileInvoices.setStatus("Not in GSTR2A");
				if(isNotEmpty(invoice.getInvtype())) {
					reconsileInvoices.setPurchaseInvtype(invoice.getInvtype());
				}
				if(isNotEmpty(invoice.getInvoiceno())) {
					reconsileInvoices.setPurchaseInvoiceno(invoice.getInvoiceno());
				}
				if(isNotEmpty(invoice.getDateofinvoice())) {
					reconsileInvoices.setPurchaseInvoicedate(sdf.format(invoice.getDateofinvoice()));
				}
				if(isNotEmpty(invoice.getBranch())) {
					reconsileInvoices.setPurchaseBranch(invoice.getBranch());
				}
				if (isNotEmpty(invoice.getBilledtoname())) {
					reconsileInvoices.setPurchaseSuppliername(invoice.getBilledtoname());
				}
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
						reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
					}
				}else {
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						reconsileInvoices.setPurchaseGstno(invoice.getB2b().get(0).getCtin());
					}
				}
				if (isNotEmpty(invoice.getTotaltaxableamount())){
					reconsileInvoices.setPurchaseTotaltaxableamount(invoice.getTotaltaxableamount());					
				}
				if (isNotEmpty(invoice.getTotaltax())){
					reconsileInvoices.setPurchaseTotalamount(invoice.getTotaltax());					
				}
				if(isNotEmpty(invoice.getTotalIgstAmount())) {
					reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
				}
				if(isNotEmpty(invoice.getTotalCgstAmount())) {
					reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
				}
				if(isNotEmpty(invoice.getTotalSgstAmount())) {
					reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
				}
				if(isNotEmpty(invoice.getTotalCessAmount())) {
					reconsileInvoices.setPurchaseTotalCessAmount(invoice.getTotalCessAmount());
				}
				if(isNotEmpty(invoice.getRevchargetype())) {
					reconsileInvoices.setPurchaseReverseCharge(invoice.getRevchargetype());
				}
				if(isNotEmpty(invoice.getFp())) {
					reconsileInvoices.setPurchaseReturnPeriod(invoice.getFp());
				}
				
				if(isNotEmpty(invoice.getBillDate())) {
					reconsileInvoices.setPurchaseTransactionDate(sdf.format(invoice.getBillDate()));
				}
				
				if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
					reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
				}
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
						  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
						  StateConfig gstcode =  null;
						  if(Integer.parseInt(gstno) < 10) {
							  String tagName = gstno;

							  Query query = new Query();
							  query.limit(37);
							  query.addCriteria(Criteria.where("name").regex(tagName));
							  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
							  if(isNotEmpty(state)) {
								gstcode = state.get(0);  
							  }
							  
						  }else {
							  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
						  }
						   
						  if(isNotEmpty(gstcode)) {
							  reconsileInvoices.setPurchaseState(gstcode.getName()); 
						  }
					}
				}else {
				  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
					  StateConfig gstcode =  null;
					  if(Integer.parseInt(gstno) < 10) {
						  String tagName = gstno;

						  Query query = new Query();
						  query.limit(37);
						  query.addCriteria(Criteria.where("name").regex(tagName));
						  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
						  if(isNotEmpty(state)) {
							gstcode = state.get(0);  
						  }
						  
					  }else {
						  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
					  }
					   
					  if(isNotEmpty(gstcode)) {
						  reconsileInvoices.setPurchaseState(gstcode.getName()); 
					  } 
				  }
				}
				
				if(isNotEmpty(invoice.getCustomField1())) {
					reconsileInvoices.setPurchasecustomField1(invoice.getCustomField1());
				}
				if(isNotEmpty(invoice.getCustomField2())) {
					reconsileInvoices.setPurchasecustomField2(invoice.getCustomField2());
				}
				if(isNotEmpty(invoice.getCustomField3())) {
					reconsileInvoices.setPurchasecustomField3(invoice.getCustomField3());
				}
				if(isNotEmpty(invoice.getCustomField4())) {
					reconsileInvoices.setPurchasecustomField4(invoice.getCustomField4());
				}
				reconsileInvoices.setGstr2Invtype("-");
				reconsileInvoices.setGstr2Invoiceno("-");
				reconsileInvoices.setGstr2Invoicedate("-");
				reconsileInvoices.setGstr2Branch("-");
				reconsileInvoices.setGstr2Suppliername("-");
				reconsileInvoices.setGstr2Gstno("-");
				reconsileInvoices.setGstr2CompanyStateName("-");
				reconsileInvoices.setGstr2ReturnPeriod("-");
				reconsileInvoices.setGstr2ReverseCharge("-");
				reconsileInvoices.setGstr2State("-");
				reconsileInvoices.setGstr2TransactionDate("-");
				reconsileInvoices.setGstr2customField1("-");
				reconsileInvoices.setGstr2customField2("-");
				reconsileInvoices.setGstr2customField3("-");
				reconsileInvoices.setGstr2customField4("-");
				reconsileInvoicesLst.add(reconsileInvoices);
			}
		}
		if(isNotEmpty(pr_nostatus_invoices) && isNotEmpty(pr_nostatus_invoices.getContent()) && pr_nostatus_invoices.getContent().size()>0) {
			pr_nostatus_invoices.getContent().forEach(inv -> {
				inv.setTotaltaxableamount(inv.getTotaltaxableamount() * inv.getSftr());
				inv.setTotaltax(inv.getTotaltax() * inv.getSftr());
				
				inv.setTotalIgstAmount(inv.getTotalIgstAmount() * inv.getSftr());
				inv.setTotalCgstAmount(inv.getTotalCgstAmount() * inv.getSftr());
				inv.setTotalSgstAmount(inv.getTotalSgstAmount() * inv.getSftr());
				if(isNotEmpty(inv.getTotalCessAmount())) {
					inv.setTotalCessAmount(inv.getTotalCessAmount() * inv.getSftr());
				}else {
					inv.setTotalCessAmount(0d * inv.getSftr());
				}
			});
			for(InvoiceParent invoice:pr_nostatus_invoices.getContent()){
				
				purchase_mappedids.add(invoice.getId().toString());
				ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
				
				reconsileInvoices.setStatus("Not in GSTR2A");
				if(isNotEmpty(invoice.getInvtype())) {
					reconsileInvoices.setPurchaseInvtype(invoice.getInvtype());
				}
				if(isNotEmpty(invoice.getInvoiceno())) {
					reconsileInvoices.setPurchaseInvoiceno(invoice.getInvoiceno());
				}
				if(isNotEmpty(invoice.getDateofinvoice())) {
					reconsileInvoices.setPurchaseInvoicedate(sdf.format(invoice.getDateofinvoice()));
				}
				if(isNotEmpty(invoice.getBranch())) {
					reconsileInvoices.setPurchaseBranch(invoice.getBranch());
				}
				if (isNotEmpty(invoice.getBilledtoname())) {
					reconsileInvoices.setPurchaseSuppliername(invoice.getBilledtoname());
				}
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
						reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
					}
				}else {
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						reconsileInvoices.setPurchaseGstno(invoice.getB2b().get(0).getCtin());
					}
				}
				if (isNotEmpty(invoice.getTotaltaxableamount())){
					reconsileInvoices.setPurchaseTotaltaxableamount(invoice.getTotaltaxableamount());					
				}
				if (isNotEmpty(invoice.getTotaltax())){
					reconsileInvoices.setPurchaseTotalamount(invoice.getTotaltax());					
				}
				if(isNotEmpty(invoice.getTotalIgstAmount())) {
					reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
				}
				if(isNotEmpty(invoice.getTotalCgstAmount())) {
					reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
				}
				if(isNotEmpty(invoice.getTotalSgstAmount())) {
					reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
				}
				if(isNotEmpty(invoice.getTotalCessAmount())) {
					reconsileInvoices.setPurchaseTotalCessAmount(invoice.getTotalCessAmount());
				}
				if(isNotEmpty(invoice.getRevchargetype())) {
					reconsileInvoices.setPurchaseReverseCharge(invoice.getRevchargetype());
				}
				if(isNotEmpty(invoice.getFp())) {
					reconsileInvoices.setPurchaseReturnPeriod(invoice.getFp());
				}
				
				if(isNotEmpty(invoice.getBillDate())) {
					reconsileInvoices.setPurchaseTransactionDate(sdf.format(invoice.getBillDate()));
				}
				
				if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
					reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
				}
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
						  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
						  StateConfig gstcode =  null;
						  if(Integer.parseInt(gstno) < 10) {
							  String tagName = gstno;

							  Query query = new Query();
							  query.limit(37);
							  query.addCriteria(Criteria.where("name").regex(tagName));
							  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
							  if(isNotEmpty(state)) {
								gstcode = state.get(0);  
							  }
							  
						  }else {
							  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
						  }
						   
						  if(isNotEmpty(gstcode)) {
							  reconsileInvoices.setPurchaseState(gstcode.getName()); 
						  }
					}
				}else {
				  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
					  StateConfig gstcode =  null;
					  if(Integer.parseInt(gstno) < 10) {
						  String tagName = gstno;

						  Query query = new Query();
						  query.limit(37);
						  query.addCriteria(Criteria.where("name").regex(tagName));
						  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
						  if(isNotEmpty(state)) {
							gstcode = state.get(0);  
						  }
						  
					  }else {
						  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
					  }
					   
					  if(isNotEmpty(gstcode)) {
						  reconsileInvoices.setPurchaseState(gstcode.getName()); 
					  } 
				  }
				}
				if(isNotEmpty(invoice.getCustomField1())) {
					reconsileInvoices.setPurchasecustomField1(invoice.getCustomField1());
				}
				if(isNotEmpty(invoice.getCustomField2())) {
					reconsileInvoices.setPurchasecustomField2(invoice.getCustomField2());
				}
				if(isNotEmpty(invoice.getCustomField3())) {
					reconsileInvoices.setPurchasecustomField3(invoice.getCustomField3());
				}
				if(isNotEmpty(invoice.getCustomField4())) {
					reconsileInvoices.setPurchasecustomField4(invoice.getCustomField4());
				}
				reconsileInvoices.setGstr2Invtype("-");
				reconsileInvoices.setGstr2Invoiceno("-");
				reconsileInvoices.setGstr2Invoicedate("-");
				reconsileInvoices.setGstr2Branch("-");
				reconsileInvoices.setGstr2Suppliername("-");
				reconsileInvoices.setGstr2Gstno("-");
				reconsileInvoices.setGstr2CompanyStateName("-");
				reconsileInvoices.setGstr2ReturnPeriod("-");
				reconsileInvoices.setGstr2ReverseCharge("-");
				reconsileInvoices.setGstr2State("-");
				reconsileInvoices.setGstr2TransactionDate("-");
				reconsileInvoices.setGstr2customField1("-");
				reconsileInvoices.setGstr2customField2("-");
				reconsileInvoices.setGstr2customField3("-");
				reconsileInvoices.setGstr2customField4("-");
				
				reconsileInvoicesLst.add(reconsileInvoices);
			}
		}
		
		Page<? extends InvoiceParent> gstr2_nostatusinvoices = clientService.getPresentretperiodgstr2MatchingStatusisNull(clientid,rtArray);
		List<String> matchingstatus = new ArrayList<String>();
		matchingstatus.add("Not In Purchases");
		Page<? extends InvoiceParent> gstr2_notinpurchasesstatusinvoices = gstr2Repository.findByClientidAndInvtypeInAndMatchingStatusInAndIsAmendmentAndFpIn(clientid, invTypes,
				matchingstatus, true, rtArray, pageable);
		if(isNotEmpty(gstr2_notinpurchasesstatusinvoices) && isNotEmpty(gstr2_notinpurchasesstatusinvoices.getContent()) && gstr2_notinpurchasesstatusinvoices.getContent().size()>0) {
			gstr2_notinpurchasesstatusinvoices.getContent().forEach(inv -> {
				inv.setTotaltaxableamount(inv.getTotaltaxableamount() * inv.getSftr());
				inv.setTotaltax(inv.getTotaltax() * inv.getSftr());
				
				inv.setTotalIgstAmount(inv.getTotalIgstAmount() * inv.getSftr());
				inv.setTotalCgstAmount(inv.getTotalCgstAmount() * inv.getSftr());
				inv.setTotalSgstAmount(inv.getTotalSgstAmount() * inv.getSftr());
				if(isNotEmpty(inv.getTotalCessAmount())) {
					inv.setTotalCessAmount(inv.getTotalCessAmount() * inv.getSftr());
				}else {
					inv.setTotalCessAmount(0d * inv.getSftr());
				}
			});
			for(InvoiceParent invoice:gstr2_notinpurchasesstatusinvoices.getContent()){
				gstr2_mappedids.add(invoice.getId().toString());
				ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
				reconsileInvoices.setStatus("Not in Purchases");
				if(isNotEmpty(invoice.getInvtype())) {
					reconsileInvoices.setGstr2Invtype(invoice.getInvtype());
				}
				if(isNotEmpty(invoice.getInvoiceno())) {
					reconsileInvoices.setGstr2Invoiceno(invoice.getInvoiceno());
				}
				if(isNotEmpty(invoice.getDateofinvoice())) {
					reconsileInvoices.setGstr2Invoicedate(sdf.format(invoice.getDateofinvoice()));
				}
				if(isNotEmpty(invoice.getBranch())) {
					reconsileInvoices.setGstr2Branch(invoice.getBranch());
				}
				if (isNotEmpty(invoice.getBilledtoname())) {
					reconsileInvoices.setGstr2Suppliername(invoice.getBilledtoname());
				}
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					if (isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
						reconsileInvoices.setGstr2Gstno(((GSTR2)invoice).getImpGoods().get(0).getStin());
					}
				}else {
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						reconsileInvoices.setGstr2Gstno(invoice.getB2b().get(0).getCtin());
					}
				}
				if(isNotEmpty(invoice.getTotaltaxableamount())) {
					reconsileInvoices.setGstr2Totaltaxableamount(invoice.getTotaltaxableamount() * invoice.getSftr());
				}
				if(isNotEmpty(invoice.getTotaltax())) {
					reconsileInvoices.setGstr2Totalamount(invoice.getTotaltax() * invoice.getSftr());
				}
				if(isNotEmpty(invoice.getTotalIgstAmount())) {
					reconsileInvoices.setGstr2TotalIgstAmount(invoice.getTotalIgstAmount() * invoice.getSftr());
				}
				if(isNotEmpty(invoice.getTotalCgstAmount())) {
					reconsileInvoices.setGstr2TotalCgstAmount(invoice.getTotalCgstAmount() * invoice.getSftr());
				}
				if(isNotEmpty(invoice.getTotalSgstAmount())) {
					reconsileInvoices.setGstr2TotalSgstAmount(invoice.getTotalSgstAmount() * invoice.getSftr());
				}
				if(isNotEmpty(invoice.getTotalCessAmount())) {
					reconsileInvoices.setGstr2TotalCessAmount(invoice.getTotalCessAmount() * invoice.getSftr());
				}
				if(isNotEmpty(invoice.getRevchargetype())) {
					reconsileInvoices.setGstr2ReverseCharge(invoice.getRevchargetype());
				}
				if(isNotEmpty(invoice.getFp())) {
					reconsileInvoices.setGstr2ReturnPeriod(invoice.getFp());
				}
				if(isNotEmpty(invoice.getBillDate())) {
					reconsileInvoices.setGstr2TransactionDate(sdf.format(invoice.getBillDate()));
				}
				if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
					reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
				}
				if(isNotEmpty(invoice.getCustomField1())) {
					reconsileInvoices.setGstr2customField1(invoice.getCustomField1());
				  }
				  if(isNotEmpty(invoice.getCustomField2())) {
					reconsileInvoices.setGstr2customField2(invoice.getCustomField2());
				  }
				  if(isNotEmpty(invoice.getCustomField3())) {
					reconsileInvoices.setGstr2customField3(invoice.getCustomField3());
				  }
				  if(isNotEmpty(invoice.getCustomField4())) {
					reconsileInvoices.setGstr2customField4(invoice.getCustomField4());
				  }
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
						if (isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
							  String gstno = ((GSTR2)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
							  StateConfig gstcode =  null;
							  if(Integer.parseInt(gstno) < 10) {
								  String tagName = gstno;

								  Query query = new Query();
								  query.limit(37);
								  query.addCriteria(Criteria.where("name").regex(tagName));
								  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
								  if(isNotEmpty(state)) {
									gstcode = state.get(0);  
								  }
								  
							  }else {
								  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
							  }
							   
							  if(isNotEmpty(gstcode)) {
								  reconsileInvoices.setGstr2State(gstcode.getName()); 
							  }
						}
				}else {
				  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
					  StateConfig gstcode =  null;
					  if(Integer.parseInt(gstno) < 10) {
						  String tagName = gstno;

						  Query query = new Query();
						  query.limit(37);
						  query.addCriteria(Criteria.where("name").regex(tagName));
						  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
						  if(isNotEmpty(state)) {
							gstcode = state.get(0);  
						  }
						  
					  }else {
						  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
					  }
					   
					  if(isNotEmpty(gstcode)) {
						  reconsileInvoices.setGstr2State(gstcode.getName()); 
					  } 
				  }
				}
				reconsileInvoices.setGstr2Branch(invoice.getBranch());
				reconsileInvoices.setPurchaseInvtype("-");
				reconsileInvoices.setPurchaseInvoiceno("-");
				reconsileInvoices.setPurchaseInvoicedate("-");
				reconsileInvoices.setPurchaseBranch("-");
				reconsileInvoices.setPurchaseSuppliername("-");
				reconsileInvoices.setPurchaseGstno("-");
				
				reconsileInvoices.setPurchaseReverseCharge("-");
				reconsileInvoices.setPurchaseReturnPeriod("-");
				reconsileInvoices.setPurchaseTransactionDate("-");
				reconsileInvoices.setPurchaseCompanyStateName("-");
				reconsileInvoices.setPurchaseState("-");
				reconsileInvoices.setPurchasecustomField1("-");
				reconsileInvoices.setPurchasecustomField2("-");
				reconsileInvoices.setPurchasecustomField3("-");
				reconsileInvoices.setPurchasecustomField4("-");
				reconsileInvoicesLst.add(reconsileInvoices);
			}
		}
		if(isNotEmpty(gstr2_nostatusinvoices) && isNotEmpty(gstr2_nostatusinvoices.getContent()) && gstr2_nostatusinvoices.getContent().size()>0) {
			gstr2_nostatusinvoices.getContent().forEach(inv -> {
				inv.setTotaltaxableamount(inv.getTotaltaxableamount() * inv.getSftr());
				inv.setTotaltax(inv.getTotaltax() * inv.getSftr());
				
				inv.setTotalIgstAmount(inv.getTotalIgstAmount() * inv.getSftr());
				inv.setTotalCgstAmount(inv.getTotalCgstAmount() * inv.getSftr());
				inv.setTotalSgstAmount(inv.getTotalSgstAmount() * inv.getSftr());
				if(isNotEmpty(inv.getTotalCessAmount())) {
					inv.setTotalCessAmount(inv.getTotalCessAmount() * inv.getSftr());
				}else {
					inv.setTotalCessAmount(0d * inv.getSftr());
				}
			});
			for(InvoiceParent invoice:gstr2_nostatusinvoices.getContent()){
				gstr2_mappedids.add(invoice.getId().toString());
				ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
				reconsileInvoices.setStatus("Not in Purchases");
				if(isNotEmpty(invoice.getInvtype())) {
					reconsileInvoices.setGstr2Invtype(invoice.getInvtype());
				}
				if(isNotEmpty(invoice.getInvoiceno())) {
					reconsileInvoices.setGstr2Invoiceno(invoice.getInvoiceno());
				}
				if(isNotEmpty(invoice.getDateofinvoice())) {
					reconsileInvoices.setGstr2Invoicedate(sdf.format(invoice.getDateofinvoice()));
				}
				if(isNotEmpty(invoice.getBranch())) {
					reconsileInvoices.setGstr2Branch(invoice.getBranch());
				}
				if (isNotEmpty(invoice.getBilledtoname())) {
					reconsileInvoices.setGstr2Suppliername(invoice.getBilledtoname());
				}
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					if (isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
						reconsileInvoices.setGstr2Gstno(((GSTR2)invoice).getImpGoods().get(0).getStin());
					}
				}else {
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						reconsileInvoices.setGstr2Gstno(invoice.getB2b().get(0).getCtin());
					}
				}
				if(isNotEmpty(invoice.getTotaltaxableamount())) {
					reconsileInvoices.setGstr2Totaltaxableamount(invoice.getTotaltaxableamount() * invoice.getSftr());
				}
				if(isNotEmpty(invoice.getTotaltax())) {
					reconsileInvoices.setGstr2Totalamount(invoice.getTotaltax() * invoice.getSftr());
				}
				if(isNotEmpty(invoice.getTotalIgstAmount())) {
					reconsileInvoices.setGstr2TotalIgstAmount(invoice.getTotalIgstAmount() * invoice.getSftr());
				}
				if(isNotEmpty(invoice.getTotalCgstAmount())) {
					reconsileInvoices.setGstr2TotalCgstAmount(invoice.getTotalCgstAmount() * invoice.getSftr());
				}
				if(isNotEmpty(invoice.getTotalSgstAmount())) {
					reconsileInvoices.setGstr2TotalSgstAmount(invoice.getTotalSgstAmount() * invoice.getSftr());
				}
				if(isNotEmpty(invoice.getTotalCessAmount())) {
					reconsileInvoices.setGstr2TotalCessAmount(invoice.getTotalCessAmount() * invoice.getSftr());
				}
				if(isNotEmpty(invoice.getRevchargetype())) {
					reconsileInvoices.setGstr2ReverseCharge(invoice.getRevchargetype());
				}
				if(isNotEmpty(invoice.getFp())) {
					reconsileInvoices.setGstr2ReturnPeriod(invoice.getFp());
				}
				if(isNotEmpty(invoice.getBillDate())) {
					reconsileInvoices.setGstr2TransactionDate(sdf.format(invoice.getBillDate()));
				}
				if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
					reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
				}
				if(isNotEmpty(invoice.getCustomField1())) {
					reconsileInvoices.setGstr2customField1(invoice.getCustomField1());
				  }
				  if(isNotEmpty(invoice.getCustomField2())) {
					reconsileInvoices.setGstr2customField2(invoice.getCustomField2());
				  }
				  if(isNotEmpty(invoice.getCustomField3())) {
					reconsileInvoices.setGstr2customField3(invoice.getCustomField3());
				  }
				  if(isNotEmpty(invoice.getCustomField4())) {
					reconsileInvoices.setGstr2customField4(invoice.getCustomField4());
				  }
					if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
						if (isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
							  String gstno = ((GSTR2)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
							  StateConfig gstcode =  null;
							  if(Integer.parseInt(gstno) < 10) {
								  String tagName = gstno;

								  Query query = new Query();
								  query.limit(37);
								  query.addCriteria(Criteria.where("name").regex(tagName));
								  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
								  if(isNotEmpty(state)) {
									gstcode = state.get(0);  
								  }
								  
							  }else {
								  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
							  }
							   
							  if(isNotEmpty(gstcode)) {
								  reconsileInvoices.setGstr2State(gstcode.getName()); 
							  }
						}
					}else {
				  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
					  StateConfig gstcode =  null;
					  if(Integer.parseInt(gstno) < 10) {
						  String tagName = gstno;
						  Query query = new Query();
						  query.limit(37);
						  query.addCriteria(Criteria.where("name").regex(tagName));
						  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
						  if(isNotEmpty(state)) {
							gstcode = state.get(0);  
						  }
					  }else {
						  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
					  }
					  if(isNotEmpty(gstcode)) {
						  reconsileInvoices.setGstr2State(gstcode.getName()); 
					  } 
				  }
				}
				reconsileInvoices.setGstr2Branch(invoice.getBranch());
				reconsileInvoices.setPurchaseInvtype("-");
				reconsileInvoices.setPurchaseInvoiceno("-");
				reconsileInvoices.setPurchaseInvoicedate("-");
				reconsileInvoices.setPurchaseBranch("-");
				reconsileInvoices.setPurchaseSuppliername("-");
				reconsileInvoices.setPurchaseGstno("-");
				reconsileInvoices.setPurchaseReverseCharge("-");
				reconsileInvoices.setPurchaseReturnPeriod("-");
				reconsileInvoices.setPurchaseTransactionDate("-");
				reconsileInvoices.setPurchaseCompanyStateName("-");
				reconsileInvoices.setPurchaseState("-");
				reconsileInvoices.setPurchasecustomField1("-");
				reconsileInvoices.setPurchasecustomField2("-");
				reconsileInvoices.setPurchasecustomField3("-");
				reconsileInvoices.setPurchasecustomField4("-");
				reconsileInvoicesLst.add(reconsileInvoices);
			}
		}
		List<ReconsileInvoices>  manuallymatched = null;
		if("Financialyear".equalsIgnoreCase(exceltype)) {
			Page<? extends InvoiceParent> purchaseinvsMannualMatchingId = clientService.getReport_PresentMonthfyinvsMannualMatchingIdisNotNull(clientid, stDate,endDate);
			Page<? extends InvoiceParent> gstr2aMannualMatchingStatus = clientService.getReport_Presentretperiodgstr2MannualMatchingStatus(clientid,rtArray);

			manuallymatched = getManuallyMactched_Financialyear(clientid ,purchaseinvsMannualMatchingId,gstr2aMannualMatchingStatus, purchase_mappedids, gstr2_mappedids);
		}else {
			Page<? extends InvoiceParent> purchaseinvsMannualMatchingId = null;
			if(isEmpty(otherconfig) || !billdate) {
				purchaseinvsMannualMatchingId = clientService.getReport_PresentMonthfyinvsMannualMatchingIdisNotNull(clientid, stDate,endDate);
			}else {
				purchaseinvsMannualMatchingId = reconsileService.getReport_PresentMonthfyinvsMannualMatchingIdisNotNullByTransactionDate(clientid, stDate,endDate);
			}
			Page<? extends InvoiceParent> gstr2aMannualMatchingStatus = clientService.getReport_Presentretperiodgstr2MannualMatchingStatus(clientid,rtArray);
			
			manuallymatched = getManuallyMactched_Monthly(clientid, purchaseinvsMannualMatchingId, gstr2aMannualMatchingStatus, purchase_mappedids, gstr2_mappedids);
		}
		
		reconsileInvoicesLst.addAll(manuallymatched);
		
		//System.out.println("gstr2_mappedids ->"+gstr2_mappedids);
		//System.out.println("purchase_mappedids ->"+purchase_mappedids);
		
		return reconsileInvoicesLst;
	}
	
	public List<ReconsileInvoices> getManuallyMactched_Financialyear(String clientid, Page<? extends InvoiceParent> purchaseinvsMannualMatchingId,
			Page<? extends InvoiceParent> gstr2aMannualMatchingStatus, Set<String> purchase_mappedids, Set<String> gstr2_mappedids){
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		List<ReconsileInvoices> reconsileInvoicesLst=new ArrayList<>(); 
		Map<String,InvoiceParent> gstr2aMannualMatchingStatusMap=new HashMap<>();
		Map<String,InvoiceParent> gstr2MannualMatchingStatus_IdMap=new HashMap<>();
		Map<String,List<InvoiceParent>> gstr2aMannualMatchingStatus_MultipleMap=new HashMap<>();
		Map<String,List<InvoiceParent>> purchaseMannualMatchingStatus_MultipleMap=new HashMap<>();
		Client client = clientService.findById(clientid);
		Set<String> gstr2_manuallymappedids=new HashSet<>();
		
		if(isNotEmpty(gstr2aMannualMatchingStatus.getContent()) && gstr2aMannualMatchingStatus.getContent().size() > 0) {
			gstr2aMannualMatchingStatus.getContent().forEach(inv -> {
				inv.setTotaltaxableamount(inv.getTotaltaxableamount() * inv.getSftr());
				inv.setTotaltax(inv.getTotaltax() * inv.getSftr());
				
				inv.setTotalIgstAmount(inv.getTotalIgstAmount() * inv.getSftr());
				inv.setTotalCgstAmount(inv.getTotalCgstAmount() * inv.getSftr());
				inv.setTotalSgstAmount(inv.getTotalSgstAmount() * inv.getSftr());
			});
			for(InvoiceParent invoice: gstr2aMannualMatchingStatus.getContent()) {
				
				if(isNotEmpty(invoice.getMannualMatchInvoices()) && invoice.getMannualMatchInvoices().equalsIgnoreCase("multiple")) {
					
					List<InvoiceParent> existinvLst=gstr2aMannualMatchingStatus_MultipleMap.get(invoice.getMatchingId());
					if(isNotEmpty(existinvLst)) {
						existinvLst.add(invoice);
						gstr2aMannualMatchingStatus_MultipleMap.put(invoice.getMatchingId(),existinvLst);
					}else {
						List<InvoiceParent> invLst=new ArrayList<>();
						invLst.add(invoice);
						gstr2aMannualMatchingStatus_MultipleMap.put(invoice.getMatchingId(),invLst);
					}
				}else {
					gstr2_manuallymappedids.add(invoice.getId().toString());
					gstr2aMannualMatchingStatusMap.put(invoice.getId().toString(), invoice);
					gstr2MannualMatchingStatus_IdMap.put(invoice.getMatchingId(), invoice);
				}
			}
		}
		
		if(isNotEmpty(purchaseinvsMannualMatchingId.getContent()) && purchaseinvsMannualMatchingId.getContent().size() > 0) {
			for(InvoiceParent invoice: purchaseinvsMannualMatchingId.getContent()) {
				if(isNotEmpty(invoice.getMannualMatchInvoices()) && invoice.getMannualMatchInvoices().equalsIgnoreCase("multiple")) {
					List<InvoiceParent> existinvLst=purchaseMannualMatchingStatus_MultipleMap.get(invoice.getMatchingId());
					if(isNotEmpty(existinvLst)) {
						existinvLst.add(invoice);
						purchaseMannualMatchingStatus_MultipleMap.put(invoice.getMatchingId(),existinvLst);
					}else {
						List<InvoiceParent> invLst=new ArrayList<>();
						invLst.add(invoice);
						purchaseMannualMatchingStatus_MultipleMap.put(invoice.getMatchingId(),invLst);
					}
				}
			}
		}
		
		if(isNotEmpty(purchaseinvsMannualMatchingId.getContent()) && purchaseinvsMannualMatchingId.getContent().size() > 0) {
			purchaseinvsMannualMatchingId.getContent().forEach(inv -> {
				inv.setTotaltaxableamount(inv.getTotaltaxableamount() * inv.getSftr());
				inv.setTotaltax(inv.getTotaltax() * inv.getSftr());
				
				inv.setTotalIgstAmount(inv.getTotalIgstAmount() * inv.getSftr());
				inv.setTotalCgstAmount(inv.getTotalCgstAmount() * inv.getSftr());
				inv.setTotalSgstAmount(inv.getTotalSgstAmount() * inv.getSftr());
			});	
			for(InvoiceParent invoice: purchaseinvsMannualMatchingId.getContent()) {
				if(isNotEmpty(invoice.getMannualMatchInvoices()) && invoice.getMannualMatchInvoices().equalsIgnoreCase("Single")) {
					if(isNotEmpty(invoice.getId().toString())) {
						List<? extends InvoiceParent> gstr2Invs= gstr2aMannualMatchingStatus_MultipleMap.get(invoice.getId().toString());
						
						if(isNotEmpty(gstr2Invs)) {
							for(InvoiceParent gstr2Inv:gstr2Invs) {
								purchase_mappedids.add(invoice.getId().toString());
								ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
								
								if(isNotEmpty(invoice.getMatchingStatus())) {
									reconsileInvoices.setPurchaseStatus(invoice.getMatchingStatus());
								}
								if(isNotEmpty(invoice.getMatchingStatus())) {
									reconsileInvoices.setStatus(invoice.getMatchingStatus());
								}
								if(isNotEmpty(invoice.getInvtype())) {
									reconsileInvoices.setPurchaseInvtype(invoice.getInvtype());
								}
								if(isNotEmpty(invoice.getInvoiceno())) {
									reconsileInvoices.setPurchaseInvoiceno(invoice.getInvoiceno());
								}
								if(isNotEmpty(invoice.getDateofinvoice())) {
									reconsileInvoices.setPurchaseInvoicedate(sdf.format(invoice.getDateofinvoice()));
								}
								if (isNotEmpty(invoice.getBilledtoname())) {
									reconsileInvoices.setPurchaseSuppliername(invoice.getBilledtoname());
								}
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
										reconsileInvoices.setPurchaseGstno(invoice.getB2b().get(0).getCtin());
									}
								}
								
								if(isNotEmpty(invoice.getTotaltaxableamount())) {
									reconsileInvoices.setPurchaseTotaltaxableamount(invoice.getTotaltaxableamount());
								}
								if(isNotEmpty(invoice.getTotaltax())) {
									reconsileInvoices.setPurchaseTotalamount(invoice.getTotaltax());
								}
								if(isNotEmpty(invoice.getTotalIgstAmount())) {
									reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
								}
								if(isNotEmpty(invoice.getTotalCgstAmount())) {
									reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
								}
								if(isNotEmpty(invoice.getTotalSgstAmount())) {
									reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
								}
								if(isNotEmpty(invoice.getBranch())) {
									reconsileInvoices.setPurchaseBranch(invoice.getBranch());					
								}
								if(isNotEmpty(invoice.getRevchargetype())) {
									reconsileInvoices.setPurchaseReverseCharge(invoice.getRevchargetype());
								}
								if(isNotEmpty(invoice.getFp())) {
									reconsileInvoices.setPurchaseReturnPeriod(invoice.getFp());
								}
								
								if(isNotEmpty(invoice.getBillDate())) {
									reconsileInvoices.setPurchaseTransactionDate(sdf.format(invoice.getBillDate()));
								}
								
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
								}
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
										  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
										  StateConfig gstcode =  null;
										  if(Integer.parseInt(gstno) < 10) {
											  String tagName = gstno;

											  Query query = new Query();
											  query.limit(37);
											  query.addCriteria(Criteria.where("name").regex(tagName));
											  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
											  if(isNotEmpty(state)) {
												gstcode = state.get(0);  
											  }
											  
										  }else {
											  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
										  }
										   
										  if(isNotEmpty(gstcode)) {
											  reconsileInvoices.setPurchaseState(gstcode.getName()); 
										  }
									}
								}else {
								  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
									  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setPurchaseState(gstcode.getName()); 
									  } 
								  }
								}
								if(isNotEmpty(invoice.getCustomField1())) {
									reconsileInvoices.setPurchasecustomField1(invoice.getCustomField1());
								}
								if(isNotEmpty(invoice.getCustomField2())) {
									reconsileInvoices.setPurchasecustomField2(invoice.getCustomField2());
								}
								if(isNotEmpty(invoice.getCustomField3())) {
									reconsileInvoices.setPurchasecustomField3(invoice.getCustomField3());
								}
								if(isNotEmpty(invoice.getCustomField4())) {
									reconsileInvoices.setPurchasecustomField4(invoice.getCustomField4());
								}
								if(isNotEmpty(invoice.getMatchingId())) {
									gstr2_mappedids.add(invoice.getMatchingId());
								}
								if(isNotEmpty(gstr2Inv.getInvtype())) {
									reconsileInvoices.setGstr2Invtype(gstr2Inv.getInvtype());
								}
								if(isNotEmpty(gstr2Inv.getInvoiceno())) {
									reconsileInvoices.setGstr2Invoiceno(gstr2Inv.getInvoiceno());
								}
								if(isNotEmpty(gstr2Inv.getDateofinvoice())) {
									reconsileInvoices.setGstr2Invoicedate(sdf.format(gstr2Inv.getDateofinvoice()));
								}
								
								if(isNotEmpty(gstr2Inv.getTotaltaxableamount())) {
									reconsileInvoices.setGstr2Totaltaxableamount(gstr2Inv.getTotaltaxableamount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotaltax())) {
									reconsileInvoices.setGstr2Totalamount(gstr2Inv.getTotaltax() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalIgstAmount())) {
									reconsileInvoices.setGstr2TotalIgstAmount(gstr2Inv.getTotalIgstAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalCgstAmount())) {
									reconsileInvoices.setGstr2TotalCgstAmount(gstr2Inv.getTotalCgstAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalSgstAmount())) {
									reconsileInvoices.setGstr2TotalSgstAmount(gstr2Inv.getTotalSgstAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(invoice.getMatchingStatus())) {
									reconsileInvoices.setGstr2status(invoice.getMatchingStatus());
								}
								if(isNotEmpty(gstr2Inv.getBranch())) {
									reconsileInvoices.setGstr2Branch(gstr2Inv.getBranch());
								}
								if (isNotEmpty(gstr2Inv.getBilledtoname())) {
									reconsileInvoices.setGstr2Suppliername(gstr2Inv.getBilledtoname());
								}
								if(gstr2Inv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((GSTR2)gstr2Inv).getImpGoods()) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0)) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setGstr2Gstno(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(gstr2Inv.getB2b()) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin())) {
										reconsileInvoices.setGstr2Gstno(gstr2Inv.getB2b().get(0).getCtin());
									}
								}
								if(isNotEmpty(gstr2Inv.getRevchargetype())) {
									reconsileInvoices.setGstr2ReverseCharge(gstr2Inv.getRevchargetype());
								}
								if(isNotEmpty(gstr2Inv.getFp())) {
									reconsileInvoices.setGstr2ReturnPeriod(gstr2Inv.getFp());
								}
								
								if(isNotEmpty(gstr2Inv.getBillDate())) {
									reconsileInvoices.setGstr2TransactionDate(sdf.format(gstr2Inv.getBillDate()));
								}
								
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
								}
								if(isNotEmpty(gstr2Inv.getCustomField1())) {
									reconsileInvoices.setGstr2customField1(gstr2Inv.getCustomField1());
								  }
								  if(isNotEmpty(gstr2Inv.getCustomField2())) {
									reconsileInvoices.setGstr2customField2(gstr2Inv.getCustomField2());
								  }
								  if(isNotEmpty(gstr2Inv.getCustomField3())) {
									reconsileInvoices.setGstr2customField3(gstr2Inv.getCustomField3());
								  }
								  if(isNotEmpty(gstr2Inv.getCustomField4())) {
									reconsileInvoices.setGstr2customField4(gstr2Inv.getCustomField4());
								  }
									if(gstr2Inv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
										if (isNotEmpty(((GSTR2)gstr2Inv).getImpGoods()) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0)) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin())) {
											  String gstno = ((GSTR2)gstr2Inv).getImpGoods().get(0).getStin().trim().substring(0, 2); 
											  StateConfig gstcode =  null;
											  if(Integer.parseInt(gstno) < 10) {
												  String tagName = gstno;

												  Query query = new Query();
												  query.limit(37);
												  query.addCriteria(Criteria.where("name").regex(tagName));
												  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
												  if(isNotEmpty(state)) {
													gstcode = state.get(0);  
												  }
												  
											  }else {
												  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
											  }
											   
											  if(isNotEmpty(gstcode)) {
												  reconsileInvoices.setGstr2State(gstcode.getName()); 
											  }
										}
								}else {
								  if(isNotEmpty(gstr2Inv.getB2b()) && isNotEmpty(gstr2Inv.getB2b().get(0)) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin())) {
									  String gstno = gstr2Inv.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setGstr2State(gstcode.getName()); 
									  } 
								  }
								}
								reconsileInvoicesLst.add(reconsileInvoices);
							}
						}else {
							purchase_mappedids.add(invoice.getId().toString());
							ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
							
							if(isNotEmpty(invoice.getMatchingStatus())) {
								reconsileInvoices.setPurchaseStatus(invoice.getMatchingStatus());
							}
							if(isNotEmpty(invoice.getMatchingStatus())) {
								reconsileInvoices.setStatus(invoice.getMatchingStatus());
							}
							
							if(isNotEmpty(invoice.getInvtype())) {
								reconsileInvoices.setPurchaseInvtype(invoice.getInvtype());
							}
							if(isNotEmpty(invoice.getInvoiceno())) {
								reconsileInvoices.setPurchaseInvoiceno(invoice.getInvoiceno());
							}
							if(isNotEmpty(invoice.getDateofinvoice())) {
								reconsileInvoices.setPurchaseInvoicedate(sdf.format(invoice.getDateofinvoice()));
							}
							
							if (isNotEmpty(invoice.getBilledtoname())) {
								reconsileInvoices.setPurchaseSuppliername(invoice.getBilledtoname());
							}
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
								if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
									reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
								}
							}else {
								if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
									reconsileInvoices.setPurchaseGstno(invoice.getB2b().get(0).getCtin());
								}
							}
							if(isNotEmpty(invoice.getTotaltaxableamount())) {
								reconsileInvoices.setPurchaseTotaltaxableamount(invoice.getTotaltaxableamount());
							}
							if(isNotEmpty(invoice.getTotaltax())) {
								reconsileInvoices.setPurchaseTotalamount(invoice.getTotaltax());
							}
							if(isNotEmpty(invoice.getTotalIgstAmount())) {
								reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
							}
							if(isNotEmpty(invoice.getTotalCgstAmount())) {
								reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
							}
							if(isNotEmpty(invoice.getTotalSgstAmount())) {
								reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
							}
							if(isNotEmpty(invoice.getBranch())) {
								reconsileInvoices.setPurchaseBranch(invoice.getBranch());					
							}
							if(isNotEmpty(invoice.getRevchargetype())) {
								reconsileInvoices.setPurchaseReverseCharge(invoice.getRevchargetype());
							}
							if(isNotEmpty(invoice.getFp())) {
								reconsileInvoices.setPurchaseReturnPeriod(invoice.getFp());
							}
							
							if(isNotEmpty(invoice.getBillDate())) {
								reconsileInvoices.setPurchaseTransactionDate(sdf.format(invoice.getBillDate()));
							}
							
							if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
								reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
							}
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
								if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
									  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setPurchaseState(gstcode.getName()); 
									  }
								}
							}else {
							  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
								  StateConfig gstcode =  null;
								  if(Integer.parseInt(gstno) < 10) {
									  String tagName = gstno;

									  Query query = new Query();
									  query.limit(37);
									  query.addCriteria(Criteria.where("name").regex(tagName));
									  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
									  if(isNotEmpty(state)) {
										gstcode = state.get(0);  
									  }
									  
								  }else {
									  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
								  }
								   
								  if(isNotEmpty(gstcode)) {
									  reconsileInvoices.setPurchaseState(gstcode.getName()); 
								  } 
							  }
							}
							if(isNotEmpty(invoice.getCustomField1())) {
								reconsileInvoices.setPurchasecustomField1(invoice.getCustomField1());
							}
							if(isNotEmpty(invoice.getCustomField2())) {
								reconsileInvoices.setPurchasecustomField2(invoice.getCustomField2());
							}
							if(isNotEmpty(invoice.getCustomField3())) {
								reconsileInvoices.setPurchasecustomField3(invoice.getCustomField3());
							}
							if(isNotEmpty(invoice.getCustomField4())) {
								reconsileInvoices.setPurchasecustomField4(invoice.getCustomField4());
							}
							
							InvoiceParent gstr2Inv=gstr2MannualMatchingStatus_IdMap.get(invoice.getId().toString());
							
							if(isEmpty(gstr2Inv)) {
								reconsileInvoices.setStatus("Not in GSTR2A");
								reconsileInvoices.setGstr2Invtype("-");
								reconsileInvoices.setGstr2Invoiceno("-");
								reconsileInvoices.setGstr2Invoicedate("-");
								reconsileInvoices.setGstr2Branch("-");
								reconsileInvoices.setGstr2Suppliername("-");
								reconsileInvoices.setGstr2Gstno("-");
								reconsileInvoices.setGstr2CompanyStateName("-");
								reconsileInvoices.setGstr2State("-");
								reconsileInvoices.setGstr2ReverseCharge("-");
								reconsileInvoices.setGstr2ReturnPeriod("-");
								reconsileInvoices.setGstr2TransactionDate("-");
								reconsileInvoices.setGstr2customField1("-");
								reconsileInvoices.setGstr2customField2("-");
								reconsileInvoices.setGstr2customField3("-");
								reconsileInvoices.setGstr2customField4("-");
							}else {
								gstr2_manuallymappedids.remove(gstr2Inv.getId().toString());

								if(isNotEmpty(invoice.getMatchingId())) {
									gstr2_mappedids.add(invoice.getMatchingId());
								}
								if(isNotEmpty(gstr2Inv.getInvtype())) {
									reconsileInvoices.setGstr2Invtype(gstr2Inv.getInvtype());
								}
								if(isNotEmpty(gstr2Inv.getInvoiceno())) {
									reconsileInvoices.setGstr2Invoiceno(gstr2Inv.getInvoiceno());
								}
								if(isNotEmpty(gstr2Inv.getDateofinvoice())) {
									reconsileInvoices.setGstr2Invoicedate(sdf.format(gstr2Inv.getDateofinvoice()));
								}
								
								if(isNotEmpty(gstr2Inv.getTotaltaxableamount())) {
									reconsileInvoices.setGstr2Totaltaxableamount(gstr2Inv.getTotaltaxableamount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotaltax())) {
									reconsileInvoices.setGstr2Totalamount(gstr2Inv.getTotaltax() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalIgstAmount())) {
									reconsileInvoices.setGstr2TotalIgstAmount(gstr2Inv.getTotalIgstAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalCgstAmount())) {
									reconsileInvoices.setGstr2TotalCgstAmount(gstr2Inv.getTotalCgstAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalSgstAmount())) {
									reconsileInvoices.setGstr2TotalSgstAmount(gstr2Inv.getTotalSgstAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getMatchingStatus())) {
									reconsileInvoices.setGstr2status(gstr2Inv.getMatchingStatus());
								}
								if(isNotEmpty(gstr2Inv.getBranch())) {
									reconsileInvoices.setGstr2Branch(gstr2Inv.getBranch());
								}
								if (isNotEmpty(gstr2Inv.getBilledtoname())) {
									reconsileInvoices.setGstr2Suppliername(gstr2Inv.getBilledtoname());
								}
								if(gstr2Inv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((GSTR2)gstr2Inv).getImpGoods()) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0)) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setGstr2Gstno(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(gstr2Inv.getB2b()) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin())) {
										reconsileInvoices.setGstr2Gstno(gstr2Inv.getB2b().get(0).getCtin());
									}
								}
								if(isNotEmpty(gstr2Inv.getRevchargetype())) {
									reconsileInvoices.setGstr2ReverseCharge(gstr2Inv.getRevchargetype());
								}
								if(isNotEmpty(gstr2Inv.getFp())) {
									reconsileInvoices.setGstr2ReturnPeriod(gstr2Inv.getFp());
								}
								
								if(isNotEmpty(gstr2Inv.getBillDate())) {
									reconsileInvoices.setGstr2TransactionDate(sdf.format(gstr2Inv.getBillDate()));
								}
								
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
								}
								if(isNotEmpty(gstr2Inv.getCustomField1())) {
									reconsileInvoices.setGstr2customField1(gstr2Inv.getCustomField1());
								  }
								  if(isNotEmpty(gstr2Inv.getCustomField2())) {
									reconsileInvoices.setGstr2customField2(gstr2Inv.getCustomField2());
								  }
								  if(isNotEmpty(gstr2Inv.getCustomField3())) {
									reconsileInvoices.setGstr2customField3(gstr2Inv.getCustomField3());
								  }
								  if(isNotEmpty(gstr2Inv.getCustomField4())) {
									reconsileInvoices.setGstr2customField4(gstr2Inv.getCustomField4());
								  }
									if(gstr2Inv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
										if (isNotEmpty(((GSTR2)gstr2Inv).getImpGoods()) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0)) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin())) {
											  String gstno = ((GSTR2)gstr2Inv).getImpGoods().get(0).getStin().trim().substring(0, 2); 
											  StateConfig gstcode =  null;
											  if(Integer.parseInt(gstno) < 10) {
												  String tagName = gstno;

												  Query query = new Query();
												  query.limit(37);
												  query.addCriteria(Criteria.where("name").regex(tagName));
												  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
												  if(isNotEmpty(state)) {
													gstcode = state.get(0);  
												  }
												  
											  }else {
												  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
											  }
											   
											  if(isNotEmpty(gstcode)) {
												  reconsileInvoices.setGstr2State(gstcode.getName()); 
											  }
										}
								}else {
								  if(isNotEmpty(gstr2Inv.getB2b()) && isNotEmpty(gstr2Inv.getB2b().get(0)) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin())) {
									  String gstno = gstr2Inv.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setGstr2State(gstcode.getName()); 
									  } 
								  }
								}
							}
							reconsileInvoicesLst.add(reconsileInvoices);
						}
					}else {
						purchase_mappedids.add(invoice.getId().toString());
						ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
						
						if(isNotEmpty(invoice.getMatchingStatus())) {
							reconsileInvoices.setPurchaseStatus(invoice.getMatchingStatus());
						}
						/*
						 * if(isNotEmpty(invoice.getMatchingStatus())) {
						 * reconsileInvoices.setStatus(invoice.getMatchingStatus()); }
						 */
						if(isNotEmpty(invoice.getInvtype())) {
							reconsileInvoices.setPurchaseInvtype(invoice.getInvtype());
						}
						if(isNotEmpty(invoice.getInvoiceno())) {
							reconsileInvoices.setPurchaseInvoiceno(invoice.getInvoiceno());
						}
						if(isNotEmpty(invoice.getDateofinvoice())) {
							reconsileInvoices.setPurchaseInvoicedate(sdf.format(invoice.getDateofinvoice()));
						}
						
						if (isNotEmpty(invoice.getBilledtoname())) {
							reconsileInvoices.setPurchaseSuppliername(invoice.getBilledtoname());
						}
						if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
							if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
								reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
							}
						}else {
							if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								reconsileInvoices.setPurchaseGstno(invoice.getB2b().get(0).getCtin());
							}
						}
						if(isNotEmpty(invoice.getBranch())) {
							reconsileInvoices.setPurchaseBranch(invoice.getBranch());					
						}						
						if(isNotEmpty(invoice.getTotaltaxableamount())) {
							reconsileInvoices.setPurchaseTotaltaxableamount(invoice.getTotaltaxableamount());
						}
						if(isNotEmpty(invoice.getTotaltax())) {
							reconsileInvoices.setPurchaseTotalamount(invoice.getTotaltax());
						}
						if(isNotEmpty(invoice.getTotalIgstAmount())) {
							reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
						}
						if(isNotEmpty(invoice.getTotalCgstAmount())) {
							reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
						}
						if(isNotEmpty(invoice.getTotalSgstAmount())) {
							reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
						}
						if(isNotEmpty(invoice.getRevchargetype())) {
							reconsileInvoices.setPurchaseReverseCharge(invoice.getRevchargetype());
						}
						if(isNotEmpty(invoice.getFp())) {
							reconsileInvoices.setPurchaseReturnPeriod(invoice.getFp());
						}
						
						if(isNotEmpty(invoice.getBillDate())) {
							reconsileInvoices.setPurchaseTransactionDate(sdf.format(invoice.getBillDate()));
						}
						
						if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
							reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
						}
						if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
							if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
								  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
								  StateConfig gstcode =  null;
								  if(Integer.parseInt(gstno) < 10) {
									  String tagName = gstno;

									  Query query = new Query();
									  query.limit(37);
									  query.addCriteria(Criteria.where("name").regex(tagName));
									  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
									  if(isNotEmpty(state)) {
										gstcode = state.get(0);  
									  }
									  
								  }else {
									  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
								  }
								   
								  if(isNotEmpty(gstcode)) {
									  reconsileInvoices.setPurchaseState(gstcode.getName()); 
								  }
							}
						}else {
						  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
							  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
							  StateConfig gstcode =  null;
							  if(Integer.parseInt(gstno) < 10) {
								  String tagName = gstno;

								  Query query = new Query();
								  query.limit(37);
								  query.addCriteria(Criteria.where("name").regex(tagName));
								  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
								  if(isNotEmpty(state)) {
									gstcode = state.get(0);  
								  }
								  
							  }else {
								  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
							  }
							   
							  if(isNotEmpty(gstcode)) {
								  reconsileInvoices.setPurchaseState(gstcode.getName()); 
							  } 
						  }
						}
						if(isNotEmpty(invoice.getCustomField1())) {
							reconsileInvoices.setPurchasecustomField1(invoice.getCustomField1());
						}
						if(isNotEmpty(invoice.getCustomField2())) {
							reconsileInvoices.setPurchasecustomField2(invoice.getCustomField2());
						}
						if(isNotEmpty(invoice.getCustomField3())) {
							reconsileInvoices.setPurchasecustomField3(invoice.getCustomField3());
						}
						if(isNotEmpty(invoice.getCustomField4())) {
							reconsileInvoices.setPurchasecustomField4(invoice.getCustomField4());
						}
						
						reconsileInvoices.setStatus("Not in GSTR2A");
						reconsileInvoices.setGstr2Invtype("-");
						reconsileInvoices.setGstr2Invoiceno("-");
						reconsileInvoices.setGstr2Invoicedate("-");
						//reconsileInvoices.setGstr2Totaltaxableamount(0.0);
						//reconsileInvoices.setGstr2Totalamount(0.0);
						reconsileInvoices.setGstr2Branch("-");
						reconsileInvoices.setGstr2Suppliername("-");
						reconsileInvoices.setGstr2Gstno("-");
						reconsileInvoices.setGstr2CompanyStateName("-");
						reconsileInvoices.setGstr2State("-");
						reconsileInvoices.setGstr2ReverseCharge("-");
						reconsileInvoices.setGstr2ReturnPeriod("-");
						reconsileInvoices.setGstr2TransactionDate("-");
						reconsileInvoices.setGstr2customField1("-");
						reconsileInvoices.setGstr2customField2("-");
						reconsileInvoices.setGstr2customField3("-");
						reconsileInvoices.setGstr2customField4("-");
						reconsileInvoicesLst.add(reconsileInvoices);
					}
				}
			}
		}
		
		if(isNotEmpty(gstr2_manuallymappedids)) {
			for(String invoiceid:gstr2_manuallymappedids) {
				InvoiceParent invoice=gstr2aMannualMatchingStatusMap.get(invoiceid);
				if(isNotEmpty(invoice)) {
					if(isNotEmpty(invoice.getMatchingId())){
						List<? extends InvoiceParent> purchaseInvLst= purchaseMannualMatchingStatus_MultipleMap.get(invoice.getId().toString());
						if(isNotEmpty(purchaseInvLst)) {
							purchaseInvLst= purchaseRepository.findByClientidAndMatchingId(clientid,invoice.getId().toString());
						}
						
						if(isNotEmpty(purchaseInvLst)) {
							purchaseInvLst.forEach(inv -> {
								inv.setTotaltaxableamount(inv.getTotaltaxableamount() * inv.getSftr());
								inv.setTotaltax(inv.getTotaltax() * inv.getSftr());
								
								inv.setTotalIgstAmount(inv.getTotalIgstAmount() * inv.getSftr());
								inv.setTotalCgstAmount(inv.getTotalCgstAmount() * inv.getSftr());
								inv.setTotalSgstAmount(inv.getTotalSgstAmount() * inv.getSftr());
							});
							for(InvoiceParent prInv:purchaseInvLst) {
								
								ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
								
								if(isNotEmpty(prInv.getMatchingStatus())) {
									reconsileInvoices.setPurchaseStatus(prInv.getMatchingStatus());
								}
								if(isNotEmpty(prInv.getMatchingStatus())) {
									reconsileInvoices.setStatus(prInv.getMatchingStatus());
								}
								if(isNotEmpty(prInv.getInvtype())) {
									reconsileInvoices.setPurchaseInvtype(prInv.getInvtype());
								}
								if(isNotEmpty(prInv.getInvoiceno())) {
									reconsileInvoices.setPurchaseInvoiceno(prInv.getInvoiceno());
								}
								if(isNotEmpty(prInv.getDateofinvoice())) {
									reconsileInvoices.setPurchaseInvoicedate(sdf.format(prInv.getDateofinvoice()));
								}
								
								if (isNotEmpty(prInv.getBilledtoname())) {
									reconsileInvoices.setPurchaseSuppliername(prInv.getBilledtoname());
								}
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(prInv.getB2b()) && isNotEmpty(prInv.getB2b().get(0).getCtin())) {
										reconsileInvoices.setPurchaseGstno(prInv.getB2b().get(0).getCtin());
									}
								}
								if(isNotEmpty(prInv.getBranch())) {
									reconsileInvoices.setPurchaseBranch(prInv.getBranch());					
								}
								
								if(isNotEmpty(prInv.getTotaltaxableamount())) {
									reconsileInvoices.setPurchaseTotaltaxableamount(prInv.getTotaltaxableamount());
								}
								if(isNotEmpty(prInv.getTotaltax())) {
									reconsileInvoices.setPurchaseTotalamount(prInv.getTotaltax());
								}
								if(isNotEmpty(prInv.getTotalIgstAmount())) {
									reconsileInvoices.setPurchaseTotalIgstAmount(prInv.getTotalIgstAmount());
								}
								if(isNotEmpty(prInv.getTotalCgstAmount())) {
									reconsileInvoices.setPurchaseTotalCgstAmount(prInv.getTotalCgstAmount());
								}
								if(isNotEmpty(prInv.getTotalSgstAmount())) {
									reconsileInvoices.setPurchaseTotalSgstAmount(prInv.getTotalSgstAmount());
								}
								if(isNotEmpty(prInv.getRevchargetype())) {
									reconsileInvoices.setPurchaseReverseCharge(prInv.getRevchargetype());
								}
								if(isNotEmpty(prInv.getFp())) {
									reconsileInvoices.setPurchaseReturnPeriod(prInv.getFp());
								}
								
								if(isNotEmpty(prInv.getBillDate())) {
									reconsileInvoices.setPurchaseTransactionDate(sdf.format(prInv.getBillDate()));
								}
								
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
								}
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
										  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
										  StateConfig gstcode =  null;
										  if(Integer.parseInt(gstno) < 10) {
											  String tagName = gstno;

											  Query query = new Query();
											  query.limit(37);
											  query.addCriteria(Criteria.where("name").regex(tagName));
											  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
											  if(isNotEmpty(state)) {
												gstcode = state.get(0);  
											  }
											  
										  }else {
											  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
										  }
										   
										  if(isNotEmpty(gstcode)) {
											  reconsileInvoices.setPurchaseState(gstcode.getName()); 
										  }
									}
								}else {
								  if(isNotEmpty(prInv.getB2b()) && isNotEmpty(prInv.getB2b().get(0)) && isNotEmpty(prInv.getB2b().get(0).getCtin())) {
									  String gstno = prInv.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setPurchaseState(gstcode.getName()); 
									  } 
								  }
								}
								if(isNotEmpty(prInv.getCustomField1())) {
									reconsileInvoices.setPurchasecustomField1(prInv.getCustomField1());
								}
								if(isNotEmpty(prInv.getCustomField2())) {
									reconsileInvoices.setPurchasecustomField2(prInv.getCustomField2());
								}
								if(isNotEmpty(prInv.getCustomField3())) {
									reconsileInvoices.setPurchasecustomField3(prInv.getCustomField3());
								}
								if(isNotEmpty(prInv.getCustomField4())) {
									reconsileInvoices.setPurchasecustomField4(prInv.getCustomField4());
								}
								
								if(isNotEmpty(invoice.getInvtype())) {
									reconsileInvoices.setGstr2Invtype(invoice.getInvtype());
								}
								if(isNotEmpty(invoice.getInvoiceno())) {
									reconsileInvoices.setGstr2Invoiceno(invoice.getInvoiceno());
								}
								if(isNotEmpty(invoice.getDateofinvoice())) {
									reconsileInvoices.setGstr2Invoicedate(sdf.format(invoice.getDateofinvoice()));
								}
								
								if(isNotEmpty(invoice.getTotaltaxableamount())) {
									reconsileInvoices.setGstr2Totaltaxableamount(invoice.getTotaltaxableamount() * invoice.getSftr());
								}
								if(isNotEmpty(invoice.getTotaltax())) {
									reconsileInvoices.setGstr2Totalamount(invoice.getTotaltax() * invoice.getSftr());
								}
								if(isNotEmpty(invoice.getTotalIgstAmount())) {
									reconsileInvoices.setGstr2TotalIgstAmount(invoice.getTotalIgstAmount() * invoice.getSftr());
								}
								if(isNotEmpty(invoice.getTotalCgstAmount())) {
									reconsileInvoices.setGstr2TotalCgstAmount(invoice.getTotalCgstAmount() * invoice.getSftr());
								}
								if(isNotEmpty(invoice.getTotalSgstAmount())) {
									reconsileInvoices.setGstr2TotalSgstAmount(invoice.getTotalSgstAmount() * invoice.getSftr());
								}
								if(isNotEmpty(invoice.getMatchingStatus())) {
									reconsileInvoices.setGstr2status(invoice.getMatchingStatus());
								}
								if(isNotEmpty(invoice.getBranch())) {
									reconsileInvoices.setGstr2Branch(invoice.getBranch());
								}
								if (isNotEmpty(invoice.getBilledtoname())) {
									reconsileInvoices.setGstr2Suppliername(invoice.getBilledtoname());
								}
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setGstr2Gstno(((GSTR2)invoice).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
										reconsileInvoices.setGstr2Gstno(invoice.getB2b().get(0).getCtin());
									}
								}
								if(isNotEmpty(invoice.getRevchargetype())) {
									reconsileInvoices.setGstr2ReverseCharge(invoice.getRevchargetype());
								}
								if(isNotEmpty(invoice.getFp())) {
									reconsileInvoices.setGstr2ReturnPeriod(invoice.getFp());
								}
								
								if(isNotEmpty(invoice.getBillDate())) {
									reconsileInvoices.setGstr2TransactionDate(sdf.format(invoice.getBillDate()));
								}
								
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
								}
								if(isNotEmpty(invoice.getCustomField1())) {
									reconsileInvoices.setGstr2customField1(invoice.getCustomField1());
								  }
								  if(isNotEmpty(invoice.getCustomField2())) {
									reconsileInvoices.setGstr2customField2(invoice.getCustomField2());
								  }
								  if(isNotEmpty(invoice.getCustomField3())) {
									reconsileInvoices.setGstr2customField3(invoice.getCustomField3());
								  }
								  if(isNotEmpty(invoice.getCustomField4())) {
									reconsileInvoices.setGstr2customField4(invoice.getCustomField4());
								  }
								  if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
										if (isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
											  String gstno = ((GSTR2)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
											  StateConfig gstcode =  null;
											  if(Integer.parseInt(gstno) < 10) {
												  String tagName = gstno;

												  Query query = new Query();
												  query.limit(37);
												  query.addCriteria(Criteria.where("name").regex(tagName));
												  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
												  if(isNotEmpty(state)) {
													gstcode = state.get(0);  
												  }
												  
											  }else {
												  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
											  }
											   
											  if(isNotEmpty(gstcode)) {
												  reconsileInvoices.setGstr2State(gstcode.getName()); 
											  }
										}
									}else {
								  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
									  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setGstr2State(gstcode.getName()); 
									  } 
								  }
							}
								reconsileInvoicesLst.add(reconsileInvoices);
							}	
						}
					}
				}
			}
		}
		return reconsileInvoicesLst;
	}
	
	public List<ReconsileInvoices> getManuallyMactched_Monthly(String clientid, Page<? extends InvoiceParent> purchaseinvsMannualMatchingId,
			Page<? extends InvoiceParent> gstr2aMannualMatchingStatus, Set<String> purchase_mappedids, Set<String> gstr2_mappedids){
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		List<ReconsileInvoices> reconsileInvoicesLst=new ArrayList<>(); 
		Map<String,InvoiceParent> gstr2aMannualMatchingStatusMap=new HashMap<>();
		Map<String,InvoiceParent> gstr2MannualMatchingStatus_IdMap=new HashMap<>();
		Map<String,List<InvoiceParent>> gstr2aMannualMatchingStatus_MultipleMap=new HashMap<>();
		Map<String,List<InvoiceParent>> purchaseMannualMatchingStatus_MultipleMap=new HashMap<>();
		Client client = clientService.findById(clientid);
		Set<String> gstr2_manuallymappedids=new HashSet<>();
		
		if(isNotEmpty(gstr2aMannualMatchingStatus) && isNotEmpty(gstr2aMannualMatchingStatus.getContent()) && gstr2aMannualMatchingStatus.getContent().size() > 0) {
			gstr2aMannualMatchingStatus.getContent().forEach(inv -> {
				inv.setTotaltaxableamount(inv.getTotaltaxableamount() * inv.getSftr());
				inv.setTotaltax(inv.getTotaltax() * inv.getSftr());
				
				inv.setTotalIgstAmount(inv.getTotalIgstAmount() * inv.getSftr());
				inv.setTotalCgstAmount(inv.getTotalCgstAmount() * inv.getSftr());
				inv.setTotalSgstAmount(inv.getTotalSgstAmount() * inv.getSftr());
				if(isNotEmpty(inv.getTotalCessAmount())) {
					inv.setTotalCessAmount(inv.getTotalCessAmount() * inv.getSftr());
				}else {
					inv.setTotalCessAmount(0d * inv.getSftr());
				}
			});
			for(InvoiceParent invoice: gstr2aMannualMatchingStatus.getContent()) {
				if(isNotEmpty(invoice.getMannualMatchInvoices()) && invoice.getMannualMatchInvoices().equalsIgnoreCase("multiple")) {
					List<InvoiceParent> existinvLst=gstr2aMannualMatchingStatus_MultipleMap.get(invoice.getMatchingId());
					if(isNotEmpty(existinvLst)) {
						existinvLst.add(invoice);
						gstr2aMannualMatchingStatus_MultipleMap.put(invoice.getMatchingId(),existinvLst);
					}else {
						List<InvoiceParent> invLst=new ArrayList<>();
						invLst.add(invoice);
						gstr2aMannualMatchingStatus_MultipleMap.put(invoice.getMatchingId(),invLst);
					}
				}else {
					gstr2_manuallymappedids.add(invoice.getId().toString());
					gstr2aMannualMatchingStatusMap.put(invoice.getId().toString(), invoice);
					gstr2MannualMatchingStatus_IdMap.put(invoice.getMatchingId(), invoice);
				}
			}
		}
		if(isNotEmpty(purchaseinvsMannualMatchingId) && isNotEmpty(purchaseinvsMannualMatchingId.getContent()) && purchaseinvsMannualMatchingId.getContent().size() > 0) {
			for(InvoiceParent invoice: purchaseinvsMannualMatchingId.getContent()) {
				if(isNotEmpty(invoice.getMannualMatchInvoices()) && invoice.getMannualMatchInvoices().equalsIgnoreCase("multiple")) {
					List<InvoiceParent> existinvLst=purchaseMannualMatchingStatus_MultipleMap.get(invoice.getMatchingId());
					if(isNotEmpty(existinvLst)) {
						existinvLst.add(invoice);
						purchaseMannualMatchingStatus_MultipleMap.put(invoice.getMatchingId(),existinvLst);
					}else {
						List<InvoiceParent> invLst=new ArrayList<>();
						invLst.add(invoice);
						purchaseMannualMatchingStatus_MultipleMap.put(invoice.getMatchingId(),invLst);
					}
				}
			}
		}
		if(isNotEmpty(purchaseinvsMannualMatchingId) &&  isNotEmpty(purchaseinvsMannualMatchingId.getContent()) && purchaseinvsMannualMatchingId.getContent().size() > 0) {
			purchaseinvsMannualMatchingId.getContent().forEach(inv -> {
				inv.setTotaltaxableamount(inv.getTotaltaxableamount() * inv.getSftr());
				inv.setTotaltax(inv.getTotaltax() * inv.getSftr());
				
				inv.setTotalIgstAmount(inv.getTotalIgstAmount() * inv.getSftr());
				inv.setTotalCgstAmount(inv.getTotalCgstAmount() * inv.getSftr());
				inv.setTotalSgstAmount(inv.getTotalSgstAmount() * inv.getSftr());
				if(isNotEmpty(inv.getTotalCessAmount())) {
					inv.setTotalCessAmount(inv.getTotalCessAmount() * inv.getSftr());
				}else {
					inv.setTotalCessAmount(0d * inv.getSftr());
				}
			});
			for(InvoiceParent invoice: purchaseinvsMannualMatchingId.getContent()) {
				if(isNotEmpty(invoice.getMannualMatchInvoices()) && invoice.getMannualMatchInvoices().equalsIgnoreCase("Single")) {
					if(isNotEmpty(invoice.getId().toString())) {
						List<? extends InvoiceParent> gstr2Invs= gstr2aMannualMatchingStatus_MultipleMap.get(invoice.getId().toString());
						if(isEmpty(gstr2Invs)) {
							List<String> matchingid=new ArrayList<String>();
							matchingid.add(invoice.getId().toString());
							gstr2Invs= gstr2Repository.findByClientidAndMatchingIdIn(clientid, matchingid);
						}
						if(isNotEmpty(gstr2Invs)) {
							for(InvoiceParent gstr2Inv:gstr2Invs) {
								purchase_mappedids.add(invoice.getId().toString());
								ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
								
								if(isNotEmpty(invoice.getMatchingStatus())) {
									reconsileInvoices.setPurchaseStatus(invoice.getMatchingStatus());
								}
								if(isNotEmpty(invoice.getMatchingStatus())) {
									reconsileInvoices.setStatus(invoice.getMatchingStatus());
								}
								if(isNotEmpty(invoice.getInvtype())) {
									reconsileInvoices.setPurchaseInvtype(invoice.getInvtype());
								}
								if(isNotEmpty(invoice.getInvoiceno())) {
									reconsileInvoices.setPurchaseInvoiceno(invoice.getInvoiceno());
								}
								if(isNotEmpty(invoice.getDateofinvoice())) {
									reconsileInvoices.setPurchaseInvoicedate(sdf.format(invoice.getDateofinvoice()));
								}
								if (isNotEmpty(invoice.getBilledtoname())) {
									reconsileInvoices.setPurchaseSuppliername(invoice.getBilledtoname());
								}
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
										reconsileInvoices.setPurchaseGstno(invoice.getB2b().get(0).getCtin());
									}
								}
								if(isNotEmpty(invoice.getTotaltaxableamount())) {
									reconsileInvoices.setPurchaseTotaltaxableamount(invoice.getTotaltaxableamount());
								}
								if(isNotEmpty(invoice.getTotaltax())) {
									reconsileInvoices.setPurchaseTotalamount(invoice.getTotaltax());
								}
								if(isNotEmpty(invoice.getTotalIgstAmount())) {
									reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
								}
								if(isNotEmpty(invoice.getTotalCgstAmount())) {
									reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
								}
								if(isNotEmpty(invoice.getTotalSgstAmount())) {
									reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
								}
								if(isNotEmpty(invoice.getTotalCessAmount())) {
									reconsileInvoices.setPurchaseTotalCessAmount(invoice.getTotalCessAmount());
								}
								if(isNotEmpty(invoice.getBranch())) {
									reconsileInvoices.setPurchaseBranch(invoice.getBranch());					
								}
								if(isNotEmpty(invoice.getRevchargetype())) {
									reconsileInvoices.setPurchaseReverseCharge(invoice.getRevchargetype());
								}
								if(isNotEmpty(invoice.getFp())) {
									reconsileInvoices.setPurchaseReturnPeriod(invoice.getFp());
								}
								if(isNotEmpty(invoice.getBillDate())) {
									reconsileInvoices.setPurchaseTransactionDate(sdf.format(invoice.getBillDate()));
								}
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
								}
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
										  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
										  StateConfig gstcode =  null;
										  if(Integer.parseInt(gstno) < 10) {
											  String tagName = gstno;

											  Query query = new Query();
											  query.limit(37);
											  query.addCriteria(Criteria.where("name").regex(tagName));
											  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
											  if(isNotEmpty(state)) {
												gstcode = state.get(0);  
											  }
											  
										  }else {
											  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
										  }
										   
										  if(isNotEmpty(gstcode)) {
											  reconsileInvoices.setPurchaseState(gstcode.getName()); 
										  }
									}
								}else {
								  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
									  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;
										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setPurchaseState(gstcode.getName()); 
									  } 
								  }
								}
								if(isNotEmpty(invoice.getCustomField1())) {
									reconsileInvoices.setPurchasecustomField1(invoice.getCustomField1());
								}
								if(isNotEmpty(invoice.getCustomField2())) {
									reconsileInvoices.setPurchasecustomField2(invoice.getCustomField2());
								}
								if(isNotEmpty(invoice.getCustomField3())) {
									reconsileInvoices.setPurchasecustomField3(invoice.getCustomField3());
								}
								if(isNotEmpty(invoice.getCustomField4())) {
									reconsileInvoices.setPurchasecustomField4(invoice.getCustomField4());
								}
								if(isNotEmpty(invoice.getMatchingId())) {
									gstr2_mappedids.add(invoice.getMatchingId());
								}
								if(isNotEmpty(gstr2Inv.getInvtype())) {
									reconsileInvoices.setGstr2Invtype(gstr2Inv.getInvtype());
								}
								if(isNotEmpty(gstr2Inv.getInvoiceno())) {
									reconsileInvoices.setGstr2Invoiceno(gstr2Inv.getInvoiceno());
								}
								if(isNotEmpty(gstr2Inv.getDateofinvoice())) {
									reconsileInvoices.setGstr2Invoicedate(sdf.format(gstr2Inv.getDateofinvoice()));
								}
								if(isNotEmpty(gstr2Inv.getTotaltaxableamount())) {
									reconsileInvoices.setGstr2Totaltaxableamount(gstr2Inv.getTotaltaxableamount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotaltax())) {
									reconsileInvoices.setGstr2Totalamount(gstr2Inv.getTotaltax() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalIgstAmount())) {
									reconsileInvoices.setGstr2TotalIgstAmount(gstr2Inv.getTotalIgstAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalCgstAmount())) {
									reconsileInvoices.setGstr2TotalCgstAmount(gstr2Inv.getTotalCgstAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalSgstAmount())) {
									reconsileInvoices.setGstr2TotalSgstAmount(gstr2Inv.getTotalSgstAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalCessAmount())) {
									reconsileInvoices.setGstr2TotalCessAmount(gstr2Inv.getTotalCessAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getMatchingStatus())) {
									reconsileInvoices.setGstr2status(gstr2Inv.getMatchingStatus());
								}
								if(isNotEmpty(gstr2Inv.getBranch())) {
									reconsileInvoices.setGstr2Branch(gstr2Inv.getBranch());
								}
								if (isNotEmpty(gstr2Inv.getBilledtoname())) {
									reconsileInvoices.setGstr2Suppliername(gstr2Inv.getBilledtoname());
								}
								if(gstr2Inv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((GSTR2)gstr2Inv).getImpGoods()) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0)) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setGstr2Gstno(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(gstr2Inv.getB2b()) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin())) {
										reconsileInvoices.setGstr2Gstno(gstr2Inv.getB2b().get(0).getCtin());
									}
								}
								if(isNotEmpty(gstr2Inv.getRevchargetype())) {
									reconsileInvoices.setGstr2ReverseCharge(gstr2Inv.getRevchargetype());
								}
								if(isNotEmpty(gstr2Inv.getFp())) {
									reconsileInvoices.setGstr2ReturnPeriod(gstr2Inv.getFp());
								}
								if(isNotEmpty(gstr2Inv.getBillDate())) {
									reconsileInvoices.setGstr2TransactionDate(sdf.format(gstr2Inv.getBillDate()));
								}
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
								}
								if(isNotEmpty(gstr2Inv.getCustomField1())) {
									reconsileInvoices.setGstr2customField1(gstr2Inv.getCustomField1());
								  }
								  if(isNotEmpty(gstr2Inv.getCustomField2())) {
									reconsileInvoices.setGstr2customField2(gstr2Inv.getCustomField2());
								  }
								  if(isNotEmpty(gstr2Inv.getCustomField3())) {
									reconsileInvoices.setGstr2customField3(gstr2Inv.getCustomField3());
								  }
								  if(isNotEmpty(gstr2Inv.getCustomField4())) {
									reconsileInvoices.setGstr2customField4(gstr2Inv.getCustomField4());
								  }
								  if(gstr2Inv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
										if (isNotEmpty(((GSTR2)gstr2Inv).getImpGoods()) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0)) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin())) {
											  String gstno = ((GSTR2)gstr2Inv).getImpGoods().get(0).getStin().trim().substring(0, 2); 
											  StateConfig gstcode =  null;
											  if(Integer.parseInt(gstno) < 10) {
												  String tagName = gstno;

												  Query query = new Query();
												  query.limit(37);
												  query.addCriteria(Criteria.where("name").regex(tagName));
												  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
												  if(isNotEmpty(state)) {
													gstcode = state.get(0);  
												  }
												  
											  }else {
												  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
											  }
											   
											  if(isNotEmpty(gstcode)) {
												  reconsileInvoices.setGstr2State(gstcode.getName()); 
											  }
										}
									}else {
								  if(isNotEmpty(gstr2Inv.getB2b()) && isNotEmpty(gstr2Inv.getB2b().get(0)) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin())) {
									  String gstno = gstr2Inv.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;
										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setGstr2State(gstcode.getName()); 
									  } 
								  }
								}
								reconsileInvoicesLst.add(reconsileInvoices);
							}
						}else {
							purchase_mappedids.add(invoice.getId().toString());
							ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
							if(isNotEmpty(invoice.getMatchingStatus())) {
								reconsileInvoices.setPurchaseStatus(invoice.getMatchingStatus());
							}
							if(isNotEmpty(invoice.getMatchingStatus())) {
								reconsileInvoices.setStatus(invoice.getMatchingStatus());
							}
							if(isNotEmpty(invoice.getInvtype())) {
								reconsileInvoices.setPurchaseInvtype(invoice.getInvtype());
							}
							if(isNotEmpty(invoice.getInvoiceno())) {
								reconsileInvoices.setPurchaseInvoiceno(invoice.getInvoiceno());
							}
							if(isNotEmpty(invoice.getDateofinvoice())) {
								reconsileInvoices.setPurchaseInvoicedate(sdf.format(invoice.getDateofinvoice()));
							}
							if (isNotEmpty(invoice.getBilledtoname())) {
								reconsileInvoices.setPurchaseSuppliername(invoice.getBilledtoname());
							}
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
								if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
									reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
								}
							}else {
								if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
									reconsileInvoices.setPurchaseGstno(invoice.getB2b().get(0).getCtin());
								}
							}
							if(isNotEmpty(invoice.getTotaltaxableamount())) {
								reconsileInvoices.setPurchaseTotaltaxableamount(invoice.getTotaltaxableamount());
							}
							if(isNotEmpty(invoice.getTotaltax())) {
								reconsileInvoices.setPurchaseTotalamount(invoice.getTotaltax());
							}
							if(isNotEmpty(invoice.getTotalIgstAmount())) {
								reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
							}
							if(isNotEmpty(invoice.getTotalCgstAmount())) {
								reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
							}
							if(isNotEmpty(invoice.getTotalSgstAmount())) {
								reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
							}
							if(isNotEmpty(invoice.getTotalCessAmount())) {
								reconsileInvoices.setPurchaseTotalCessAmount(invoice.getTotalCessAmount());
							}
							if(isNotEmpty(invoice.getBranch())) {
								reconsileInvoices.setPurchaseBranch(invoice.getBranch());					
							}
							if(isNotEmpty(invoice.getRevchargetype())) {
								reconsileInvoices.setPurchaseReverseCharge(invoice.getRevchargetype());
							}
							if(isNotEmpty(invoice.getFp())) {
								reconsileInvoices.setPurchaseReturnPeriod(invoice.getFp());
							}
							if(isNotEmpty(invoice.getBillDate())) {
								reconsileInvoices.setPurchaseTransactionDate(sdf.format(invoice.getBillDate()));
							}
							if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
								reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
							}
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
								if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
									  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setPurchaseState(gstcode.getName()); 
									  }
								}
							}else {
							  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
								  StateConfig gstcode =  null;
								  if(Integer.parseInt(gstno) < 10) {
									  String tagName = gstno;
									  Query query = new Query();
									  query.limit(37);
									  query.addCriteria(Criteria.where("name").regex(tagName));
									  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
									  if(isNotEmpty(state)) {
										gstcode = state.get(0);  
									  }
								  }else {
									  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
								  }
								  if(isNotEmpty(gstcode)) {
									  reconsileInvoices.setPurchaseState(gstcode.getName()); 
								  } 
							  }
							}
							if(isNotEmpty(invoice.getCustomField1())) {
								reconsileInvoices.setPurchasecustomField1(invoice.getCustomField1());
							}
							if(isNotEmpty(invoice.getCustomField2())) {
								reconsileInvoices.setPurchasecustomField2(invoice.getCustomField2());
							}
							if(isNotEmpty(invoice.getCustomField3())) {
								reconsileInvoices.setPurchasecustomField3(invoice.getCustomField3());
							}
							if(isNotEmpty(invoice.getCustomField4())) {
								reconsileInvoices.setPurchasecustomField4(invoice.getCustomField4());
							}
							InvoiceParent gstr2Inv=gstr2MannualMatchingStatus_IdMap.get(invoice.getId().toString());
							
							if(isEmpty(gstr2Inv)) {
								gstr2Inv=gstr2Repository.findByClientidAndMatchingId(clientid, invoice.getId().toString());
							}
							
							if(isEmpty(gstr2Inv)) {
								reconsileInvoices.setStatus("Not in GSTR2A");
								reconsileInvoices.setGstr2Invtype("-");
								reconsileInvoices.setGstr2Invoiceno("-");
								reconsileInvoices.setGstr2Invoicedate("-");
								reconsileInvoices.setGstr2Branch("-");
								reconsileInvoices.setGstr2Suppliername("-");
								reconsileInvoices.setGstr2Gstno("-");
								reconsileInvoices.setGstr2CompanyStateName("-");
								reconsileInvoices.setGstr2State("-");
								reconsileInvoices.setGstr2ReverseCharge("-");
								reconsileInvoices.setGstr2ReturnPeriod("-");
								reconsileInvoices.setGstr2TransactionDate("-");
								reconsileInvoices.setGstr2customField1("-");
								reconsileInvoices.setGstr2customField2("-");
								reconsileInvoices.setGstr2customField3("-");
								reconsileInvoices.setGstr2customField4("-");
							}else {
								gstr2_manuallymappedids.remove(gstr2Inv.getId().toString());

								if(isNotEmpty(invoice.getMatchingId())) {
									gstr2_mappedids.add(invoice.getMatchingId());
								}
								if(isNotEmpty(gstr2Inv.getInvtype())) {
									reconsileInvoices.setGstr2Invtype(gstr2Inv.getInvtype());
								}
								if(isNotEmpty(gstr2Inv.getInvoiceno())) {
									reconsileInvoices.setGstr2Invoiceno(gstr2Inv.getInvoiceno());
								}
								if(isNotEmpty(gstr2Inv.getDateofinvoice())) {
									reconsileInvoices.setGstr2Invoicedate(sdf.format(gstr2Inv.getDateofinvoice()));
								}
								
								if(isNotEmpty(gstr2Inv.getTotaltaxableamount())) {
									reconsileInvoices.setGstr2Totaltaxableamount(gstr2Inv.getTotaltaxableamount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotaltax())) {
									reconsileInvoices.setGstr2Totalamount(gstr2Inv.getTotaltax() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalIgstAmount())) {
									reconsileInvoices.setGstr2TotalIgstAmount(gstr2Inv.getTotalIgstAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalCgstAmount())) {
									reconsileInvoices.setGstr2TotalCgstAmount(gstr2Inv.getTotalCgstAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalSgstAmount())) {
									reconsileInvoices.setGstr2TotalSgstAmount(gstr2Inv.getTotalSgstAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getTotalCessAmount())) {
									reconsileInvoices.setGstr2TotalCessAmount(gstr2Inv.getTotalCessAmount() * gstr2Inv.getSftr());
								}
								if(isNotEmpty(gstr2Inv.getMatchingStatus())) {
									reconsileInvoices.setGstr2status(gstr2Inv.getMatchingStatus());
								}
								if(isNotEmpty(gstr2Inv.getBranch())) {
									reconsileInvoices.setGstr2Branch(gstr2Inv.getBranch());
								}
								if (isNotEmpty(gstr2Inv.getBilledtoname())) {
									reconsileInvoices.setGstr2Suppliername(gstr2Inv.getBilledtoname());
								}
								if(gstr2Inv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((GSTR2)gstr2Inv).getImpGoods()) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0)) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setGstr2Gstno(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(gstr2Inv.getB2b()) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin())) {
										reconsileInvoices.setGstr2Gstno(gstr2Inv.getB2b().get(0).getCtin());
									}
								}
								if(isNotEmpty(gstr2Inv.getRevchargetype())) {
									reconsileInvoices.setGstr2ReverseCharge(gstr2Inv.getRevchargetype());
								}
								if(isNotEmpty(gstr2Inv.getFp())) {
									reconsileInvoices.setGstr2ReturnPeriod(gstr2Inv.getFp());
								}
								
								if(isNotEmpty(gstr2Inv.getBillDate())) {
									reconsileInvoices.setGstr2TransactionDate(sdf.format(gstr2Inv.getBillDate()));
								}
								
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
								}
								if(isNotEmpty(gstr2Inv.getCustomField1())) {
									reconsileInvoices.setGstr2customField1(gstr2Inv.getCustomField1());
								  }
								  if(isNotEmpty(gstr2Inv.getCustomField2())) {
									reconsileInvoices.setGstr2customField2(gstr2Inv.getCustomField2());
								  }
								  if(isNotEmpty(gstr2Inv.getCustomField3())) {
									reconsileInvoices.setGstr2customField3(gstr2Inv.getCustomField3());
								  }
								  if(isNotEmpty(gstr2Inv.getCustomField4())) {
									reconsileInvoices.setGstr2customField4(gstr2Inv.getCustomField4());
								  }
								  if(gstr2Inv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
										if (isNotEmpty(((GSTR2)gstr2Inv).getImpGoods()) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0)) && isNotEmpty(((GSTR2)gstr2Inv).getImpGoods().get(0).getStin())) {
											  String gstno = ((GSTR2)gstr2Inv).getImpGoods().get(0).getStin().trim().substring(0, 2); 
											  StateConfig gstcode =  null;
											  if(Integer.parseInt(gstno) < 10) {
												  String tagName = gstno;

												  Query query = new Query();
												  query.limit(37);
												  query.addCriteria(Criteria.where("name").regex(tagName));
												  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
												  if(isNotEmpty(state)) {
													gstcode = state.get(0);  
												  }
												  
											  }else {
												  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
											  }
											   
											  if(isNotEmpty(gstcode)) {
												  reconsileInvoices.setGstr2State(gstcode.getName()); 
											  }
										}
									}else {
								  if(isNotEmpty(gstr2Inv.getB2b()) && isNotEmpty(gstr2Inv.getB2b().get(0)) && isNotEmpty(gstr2Inv.getB2b().get(0).getCtin())) {
									  String gstno = gstr2Inv.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setGstr2State(gstcode.getName()); 
									  } 
								  }
								}
							}
							reconsileInvoicesLst.add(reconsileInvoices);
						}
					}else {
						purchase_mappedids.add(invoice.getId().toString());
						ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
						
						if(isNotEmpty(invoice.getMatchingStatus())) {
							reconsileInvoices.setPurchaseStatus(invoice.getMatchingStatus());
						}else {
							reconsileInvoices.setStatus("Not in GSTR2A");
						}
						if(isNotEmpty(invoice.getMatchingStatus())) {
							reconsileInvoices.setStatus(invoice.getMatchingStatus());					
						}
						if(isNotEmpty(invoice.getInvtype())) {
							reconsileInvoices.setPurchaseInvtype(invoice.getInvtype());
						}
						if(isNotEmpty(invoice.getInvoiceno())) {
							reconsileInvoices.setPurchaseInvoiceno(invoice.getInvoiceno());
						}
						if(isNotEmpty(invoice.getDateofinvoice())) {
							reconsileInvoices.setPurchaseInvoicedate(sdf.format(invoice.getDateofinvoice()));
						}
						
						if (isNotEmpty(invoice.getBilledtoname())) {
							reconsileInvoices.setPurchaseSuppliername(invoice.getBilledtoname());
						}		
						if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
							if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
								reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
							}
						}else {
							if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								reconsileInvoices.setPurchaseGstno(invoice.getB2b().get(0).getCtin());
							}
						}
						if(isNotEmpty(invoice.getBranch())) {
							reconsileInvoices.setPurchaseBranch(invoice.getBranch());					
						}
						if(isNotEmpty(invoice.getTotaltaxableamount())) {
							reconsileInvoices.setPurchaseTotaltaxableamount(invoice.getTotaltaxableamount());
						}
						if(isNotEmpty(invoice.getTotaltax())) {
							reconsileInvoices.setPurchaseTotalamount(invoice.getTotaltax());
						}
						if(isNotEmpty(invoice.getTotalIgstAmount())) {
							reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
						}
						if(isNotEmpty(invoice.getTotalCgstAmount())) {
							reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
						}
						if(isNotEmpty(invoice.getTotalSgstAmount())) {
							reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
						}
						if(isNotEmpty(invoice.getTotalCessAmount())) {
							reconsileInvoices.setPurchaseTotalCessAmount(invoice.getTotalCessAmount());
						}
						if(isNotEmpty(invoice.getRevchargetype())) {
							reconsileInvoices.setPurchaseReverseCharge(invoice.getRevchargetype());
						}
						if(isNotEmpty(invoice.getFp())) {
							reconsileInvoices.setPurchaseReturnPeriod(invoice.getFp());
						}
						
						if(isNotEmpty(invoice.getBillDate())) {
							reconsileInvoices.setPurchaseTransactionDate(sdf.format(invoice.getBillDate()));
						}
						
						if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
							reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
						}
						if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
							if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
								  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
								  StateConfig gstcode =  null;
								  if(Integer.parseInt(gstno) < 10) {
									  String tagName = gstno;

									  Query query = new Query();
									  query.limit(37);
									  query.addCriteria(Criteria.where("name").regex(tagName));
									  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
									  if(isNotEmpty(state)) {
										gstcode = state.get(0);  
									  }
									  
								  }else {
									  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
								  }
								   
								  if(isNotEmpty(gstcode)) {
									  reconsileInvoices.setPurchaseState(gstcode.getName()); 
								  }
							}
						}else {
						  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
							  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
							  StateConfig gstcode =  null;
							  if(Integer.parseInt(gstno) < 10) {
								  String tagName = gstno;

								  Query query = new Query();
								  query.limit(37);
								  query.addCriteria(Criteria.where("name").regex(tagName));
								  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
								  if(isNotEmpty(state)) {
									gstcode = state.get(0);  
								  }
								  
							  }else {
								  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
							  }
							   
							  if(isNotEmpty(gstcode)) {
								  reconsileInvoices.setPurchaseState(gstcode.getName()); 
							  } 
						  }
						}
						if(isNotEmpty(invoice.getCustomField1())) {
							reconsileInvoices.setPurchasecustomField1(invoice.getCustomField1());
						}
						if(isNotEmpty(invoice.getCustomField2())) {
							reconsileInvoices.setPurchasecustomField2(invoice.getCustomField2());
						}
						if(isNotEmpty(invoice.getCustomField3())) {
							reconsileInvoices.setPurchasecustomField3(invoice.getCustomField3());
						}
						if(isNotEmpty(invoice.getCustomField4())) {
							reconsileInvoices.setPurchasecustomField4(invoice.getCustomField4());
						}
						reconsileInvoices.setGstr2Invtype("-");
						reconsileInvoices.setGstr2Invoiceno("-");
						reconsileInvoices.setGstr2Invoicedate("-");
						reconsileInvoices.setGstr2Totaltaxableamount(0.0);
						reconsileInvoices.setGstr2Totalamount(0.0);
						reconsileInvoices.setGstr2Branch("-");
						reconsileInvoices.setGstr2Suppliername("-");
						reconsileInvoices.setGstr2Gstno("-");
						reconsileInvoices.setGstr2CompanyStateName("-");
						reconsileInvoices.setGstr2State("-");
						reconsileInvoices.setGstr2ReverseCharge("-");
						reconsileInvoices.setGstr2ReturnPeriod("-");
						reconsileInvoices.setGstr2TransactionDate("-");
						reconsileInvoices.setGstr2customField1("-");
						reconsileInvoices.setGstr2customField2("-");
						reconsileInvoices.setGstr2customField3("-");
						reconsileInvoices.setGstr2customField4("-");
						reconsileInvoicesLst.add(reconsileInvoices);
					}
				}
			}
		}
		
		if(isNotEmpty(gstr2_manuallymappedids)) {
			for(String invoiceid:gstr2_manuallymappedids) {
				InvoiceParent invoice=gstr2aMannualMatchingStatusMap.get(invoiceid);
				if(isNotEmpty(invoice)) {
					if(isNotEmpty(invoice.getMatchingId())){
						List<? extends InvoiceParent> purchaseInvLst= purchaseMannualMatchingStatus_MultipleMap.get(invoice.getId().toString());
						if(isEmpty(purchaseInvLst)) {
							purchaseInvLst= purchaseRepository.findByClientidAndMatchingId(clientid,invoice.getId().toString());
						}
						
						if(isNotEmpty(purchaseInvLst)) {
							purchaseInvLst.forEach(inv -> {
								inv.setTotaltaxableamount(inv.getTotaltaxableamount() * inv.getSftr());
								inv.setTotaltax(inv.getTotaltax() * inv.getSftr());
								
								inv.setTotalIgstAmount(inv.getTotalIgstAmount() * inv.getSftr());
								inv.setTotalCgstAmount(inv.getTotalCgstAmount() * inv.getSftr());
								inv.setTotalSgstAmount(inv.getTotalSgstAmount() * inv.getSftr());
								if(isNotEmpty(inv.getTotalCessAmount())) {
									inv.setTotalCessAmount(inv.getTotalCessAmount() * inv.getSftr());
								}else {
									inv.setTotalCessAmount(0d * inv.getSftr());
								}
							});
							for(InvoiceParent prInv:purchaseInvLst) {
								
								ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
								
								if(isNotEmpty(prInv.getMatchingStatus())) {
									reconsileInvoices.setPurchaseStatus(prInv.getMatchingStatus());
								}
								if(isNotEmpty(prInv.getMatchingStatus())) {
									reconsileInvoices.setStatus(prInv.getMatchingStatus());
								}
								if(isNotEmpty(prInv.getInvtype())) {
									reconsileInvoices.setPurchaseInvtype(prInv.getInvtype());
								}
								if(isNotEmpty(prInv.getInvoiceno())) {
									reconsileInvoices.setPurchaseInvoiceno(prInv.getInvoiceno());
								}
								if(isNotEmpty(prInv.getDateofinvoice())) {
									reconsileInvoices.setPurchaseInvoicedate(sdf.format(prInv.getDateofinvoice()));
								}
								
								if (isNotEmpty(prInv.getBilledtoname())) {
									reconsileInvoices.setPurchaseSuppliername(prInv.getBilledtoname());
								}
								if(prInv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((PurchaseRegister)prInv).getImpGoods()) && isNotEmpty(((PurchaseRegister)prInv).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)prInv).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setPurchaseGstno(((PurchaseRegister)prInv).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(prInv.getB2b()) && isNotEmpty(prInv.getB2b().get(0).getCtin())) {
										reconsileInvoices.setPurchaseGstno(prInv.getB2b().get(0).getCtin());
									}
								}
								if(isNotEmpty(prInv.getBranch())) {
									reconsileInvoices.setPurchaseBranch(prInv.getBranch());					
								}
								if(isNotEmpty(prInv.getTotaltaxableamount())) {
									reconsileInvoices.setPurchaseTotaltaxableamount(prInv.getTotaltaxableamount());
								}
								if(isNotEmpty(prInv.getTotaltax())) {
									reconsileInvoices.setPurchaseTotalamount(prInv.getTotaltax());
								}
								if(isNotEmpty(prInv.getTotalIgstAmount())) {
									reconsileInvoices.setPurchaseTotalIgstAmount(prInv.getTotalIgstAmount());
								}
								if(isNotEmpty(prInv.getTotalCgstAmount())) {
									reconsileInvoices.setPurchaseTotalCgstAmount(prInv.getTotalCgstAmount());
								}
								if(isNotEmpty(prInv.getTotalSgstAmount())) {
									reconsileInvoices.setPurchaseTotalSgstAmount(prInv.getTotalSgstAmount());
								}
								if(isNotEmpty(prInv.getTotalCessAmount())) {
									reconsileInvoices.setPurchaseTotalCessAmount(prInv.getTotalCessAmount());
								}
								if(isNotEmpty(prInv.getRevchargetype())) {
									reconsileInvoices.setPurchaseReverseCharge(prInv.getRevchargetype());
								}
								if(isNotEmpty(prInv.getFp())) {
									reconsileInvoices.setPurchaseReturnPeriod(prInv.getFp());
								}
								
								if(isNotEmpty(prInv.getBillDate())) {
									reconsileInvoices.setPurchaseTransactionDate(sdf.format(prInv.getBillDate()));
								}
								
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
								}
								if(prInv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((PurchaseRegister)prInv).getImpGoods()) && isNotEmpty(((PurchaseRegister)prInv).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)prInv).getImpGoods().get(0).getStin())) {
										  String gstno = ((PurchaseRegister)prInv).getImpGoods().get(0).getStin().trim().substring(0, 2); 
										  StateConfig gstcode =  null;
										  if(Integer.parseInt(gstno) < 10) {
											  String tagName = gstno;

											  Query query = new Query();
											  query.limit(37);
											  query.addCriteria(Criteria.where("name").regex(tagName));
											  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
											  if(isNotEmpty(state)) {
												gstcode = state.get(0);  
											  }
											  
										  }else {
											  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
										  }
										   
										  if(isNotEmpty(gstcode)) {
											  reconsileInvoices.setPurchaseState(gstcode.getName()); 
										  }
									}
								}else {
								  if(isNotEmpty(prInv.getB2b()) && isNotEmpty(prInv.getB2b().get(0)) && isNotEmpty(prInv.getB2b().get(0).getCtin())) {
									  String gstno = prInv.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setPurchaseState(gstcode.getName()); 
									  } 
								  }
								}
								if(isNotEmpty(prInv.getCustomField1())) {
									reconsileInvoices.setPurchasecustomField1(prInv.getCustomField1());
								}
								if(isNotEmpty(prInv.getCustomField2())) {
									reconsileInvoices.setPurchasecustomField2(prInv.getCustomField2());
								}
								if(isNotEmpty(prInv.getCustomField3())) {
									reconsileInvoices.setPurchasecustomField3(prInv.getCustomField3());
								}
								if(isNotEmpty(prInv.getCustomField4())) {
									reconsileInvoices.setPurchasecustomField4(prInv.getCustomField4());
								}
								
								if(isNotEmpty(invoice.getInvtype())) {
									reconsileInvoices.setGstr2Invtype(invoice.getInvtype());
								}
								if(isNotEmpty(invoice.getInvoiceno())) {
									reconsileInvoices.setGstr2Invoiceno(invoice.getInvoiceno());
								}
								if(isNotEmpty(invoice.getDateofinvoice())) {
									reconsileInvoices.setGstr2Invoicedate(sdf.format(invoice.getDateofinvoice()));
								}
								if(isNotEmpty(invoice.getTotaltaxableamount())) {
									reconsileInvoices.setGstr2Totaltaxableamount(invoice.getTotaltaxableamount() * invoice.getSftr());
								}
								if(isNotEmpty(invoice.getTotaltax())) {
									reconsileInvoices.setGstr2Totalamount(invoice.getTotaltax() * invoice.getSftr());
								}
								if(isNotEmpty(invoice.getTotalIgstAmount())) {
									reconsileInvoices.setGstr2TotalIgstAmount(invoice.getTotalIgstAmount() * invoice.getSftr());
								}
								if(isNotEmpty(invoice.getTotalCgstAmount())) {
									reconsileInvoices.setGstr2TotalCgstAmount(invoice.getTotalCgstAmount() * invoice.getSftr());
								}
								if(isNotEmpty(invoice.getTotalSgstAmount())) {
									reconsileInvoices.setGstr2TotalSgstAmount(invoice.getTotalSgstAmount() * invoice.getSftr());
								}
								if(isNotEmpty(invoice.getTotalCessAmount())) {
									reconsileInvoices.setGstr2TotalCessAmount(invoice.getTotalCessAmount() * invoice.getSftr());
								}
								if(isNotEmpty(invoice.getMatchingStatus())) {
									reconsileInvoices.setGstr2status(invoice.getMatchingStatus());
								}
								if(isNotEmpty(invoice.getBranch())) {
									reconsileInvoices.setGstr2Branch(invoice.getBranch());
								}
								if (isNotEmpty(invoice.getBilledtoname())) {
									reconsileInvoices.setGstr2Suppliername(invoice.getBilledtoname());
								}
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setGstr2Gstno(((GSTR2)invoice).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
										reconsileInvoices.setGstr2Gstno(invoice.getB2b().get(0).getCtin());
									}
								}
								if(isNotEmpty(invoice.getRevchargetype())) {
									reconsileInvoices.setGstr2ReverseCharge(invoice.getRevchargetype());
								}
								if(isNotEmpty(invoice.getFp())) {
									reconsileInvoices.setGstr2ReturnPeriod(invoice.getFp());
								}
								
								if(isNotEmpty(invoice.getBillDate())) {
									reconsileInvoices.setGstr2TransactionDate(sdf.format(invoice.getBillDate()));
								}
								
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
								}
								if(isNotEmpty(invoice.getCustomField1())) {
									reconsileInvoices.setGstr2customField1(invoice.getCustomField1());
								  }
								  if(isNotEmpty(invoice.getCustomField2())) {
									reconsileInvoices.setGstr2customField2(invoice.getCustomField2());
								  }
								  if(isNotEmpty(invoice.getCustomField3())) {
									reconsileInvoices.setGstr2customField3(invoice.getCustomField3());
								  }
								  if(isNotEmpty(invoice.getCustomField4())) {
									reconsileInvoices.setGstr2customField4(invoice.getCustomField4());
								  }
								  if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
										if (isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
											  String gstno = ((GSTR2)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
											  StateConfig gstcode =  null;
											  if(Integer.parseInt(gstno) < 10) {
												  String tagName = gstno;

												  Query query = new Query();
												  query.limit(37);
												  query.addCriteria(Criteria.where("name").regex(tagName));
												  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
												  if(isNotEmpty(state)) {
													gstcode = state.get(0);  
												  }
												  
											  }else {
												  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
											  }
											   
											  if(isNotEmpty(gstcode)) {
												  reconsileInvoices.setGstr2State(gstcode.getName()); 
											  }
										}
									}else {
								  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
									  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setGstr2State(gstcode.getName()); 
									  } 
								  }
								}

								reconsileInvoicesLst.add(reconsileInvoices);
							}	
						}
					}
				}
			}
		}
		return reconsileInvoicesLst;
	}
	
	public ReconsileInvoices addReconsileInvoices(InvoiceParent invoice){
		
		if(isNotEmpty(invoice)) {
			
			String clientid = "";
			if(isNotEmpty(invoice.getClientid())) {
				clientid = invoice.getClientid();
			}
			Client client = clientService.findById(clientid);
			SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
			ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
			reconsileInvoices.setStatus("Not in GSTR2A");
			
			if(isNotEmpty(invoice.getInvtype())) {
				reconsileInvoices.setPurchaseInvtype(invoice.getInvtype());
			}
			if(isNotEmpty(invoice.getInvoiceno())) {
				reconsileInvoices.setPurchaseInvoiceno(invoice.getInvoiceno());
			}
			if(isNotEmpty(invoice.getDateofinvoice())) {
				reconsileInvoices.setPurchaseInvoicedate(sdf.format(invoice.getDateofinvoice()));
			}
			
			if(isNotEmpty(invoice.getBranch())) {
				reconsileInvoices.setPurchaseBranch(invoice.getBranch());
			}
			if (isNotEmpty(invoice.getBilledtoname())) {
				reconsileInvoices.setPurchaseSuppliername(invoice.getBilledtoname());
			}
			if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
				if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
					reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
				}
			}else {
				if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					reconsileInvoices.setPurchaseGstno(invoice.getB2b().get(0).getCtin());
				}
			}
			
			if(isNotEmpty(invoice.getTotaltaxableamount())) {
				reconsileInvoices.setPurchaseTotaltaxableamount(invoice.getTotaltaxableamount());
			}
			if(isNotEmpty(invoice.getTotaltax())) {
				reconsileInvoices.setPurchaseTotalamount(invoice.getTotaltax());
			}
			if(isNotEmpty(invoice.getTotalIgstAmount())) {
				reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
			}
			if(isNotEmpty(invoice.getTotalCgstAmount())) {
				reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
			}
			if(isNotEmpty(invoice.getTotalSgstAmount())) {
				reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
			}
			if(isNotEmpty(invoice.getTotalCessAmount())) {
				reconsileInvoices.setPurchaseTotalCessAmount(invoice.getTotalCessAmount());
			}
			if(isNotEmpty(invoice.getRevchargetype())) {
				reconsileInvoices.setPurchaseReverseCharge(invoice.getRevchargetype());
			}
			if(isNotEmpty(invoice.getFp())) {
				reconsileInvoices.setPurchaseReturnPeriod(invoice.getFp());
			}
			
			if(isNotEmpty(invoice.getBillDate())) {
				reconsileInvoices.setPurchaseTransactionDate(sdf.format(invoice.getBillDate()));
			}
			
			if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
				reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
			}
			if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
				if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
					  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
					  StateConfig gstcode =  null;
					  if(Integer.parseInt(gstno) < 10) {
						  String tagName = gstno;

						  Query query = new Query();
						  query.limit(37);
						  query.addCriteria(Criteria.where("name").regex(tagName));
						  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
						  if(isNotEmpty(state)) {
							gstcode = state.get(0);  
						  }
						  
					  }else {
						  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
					  }
					   
					  if(isNotEmpty(gstcode)) {
						  reconsileInvoices.setPurchaseState(gstcode.getName()); 
					  }
				}
			}else {
			  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
				  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
				  StateConfig gstcode =  null;
				  if(Integer.parseInt(gstno) < 10) {
					  String tagName = gstno;

					  Query query = new Query();
					  query.limit(37);
					  query.addCriteria(Criteria.where("name").regex(tagName));
					  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
					  if(isNotEmpty(state)) {
						gstcode = state.get(0);  
					  }
					  
				  }else {
					  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
				  }
				   
				  if(isNotEmpty(gstcode)) {
					  reconsileInvoices.setPurchaseState(gstcode.getName()); 
				  } 
			  }
			}
			if(isNotEmpty(invoice.getCustomField1())) {
				reconsileInvoices.setPurchasecustomField1(invoice.getCustomField1());
			}
			if(isNotEmpty(invoice.getCustomField2())) {
				reconsileInvoices.setPurchasecustomField2(invoice.getCustomField2());
			}
			if(isNotEmpty(invoice.getCustomField3())) {
				reconsileInvoices.setPurchasecustomField3(invoice.getCustomField3());
			}
			if(isNotEmpty(invoice.getCustomField4())) {
				reconsileInvoices.setPurchasecustomField4(invoice.getCustomField4());
			}
			reconsileInvoices.setGstr2Invtype("-");
			reconsileInvoices.setGstr2Invoiceno("-");
			reconsileInvoices.setGstr2Invoicedate("-");
			reconsileInvoices.setGstr2Branch("-");
			reconsileInvoices.setGstr2Suppliername("-");
			reconsileInvoices.setGstr2Gstno("-");
			
			reconsileInvoices.setGstr2CompanyStateName("-");
			reconsileInvoices.setGstr2ReturnPeriod("-");
			reconsileInvoices.setGstr2ReverseCharge("-");
			reconsileInvoices.setGstr2State("-");
			reconsileInvoices.setGstr2TransactionDate("-");
			reconsileInvoices.setGstr2customField1("-");
			reconsileInvoices.setGstr2customField2("-");
			reconsileInvoices.setGstr2customField3("-");
			reconsileInvoices.setGstr2customField4("-");
			return reconsileInvoices;
		}else {
			return null;
		}
	}
	
	
	
	
	public List<ReconsileInvoices> getReconsileExcelDatagstr2b(String clientid, int month, int year, Date stDate,Date endDate, List<String> rtArray, boolean isMonthly) throws Exception{
		OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
		String yearCode = Utility.getYearCode(month, year);
		Boolean billdate = false;
		if(isNotEmpty(otherconfig)){
			billdate = otherconfig.isEnableTransDate();
		}
		List<ReconsileInvoices> reconsileInvoicesLst=new ArrayList<ReconsileInvoices>();
		Client client = clientService.findById(clientid);
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		Page<? extends InvoiceParent> pr_nostatus_invoices = reconsilationService.getPurchaseInvoices(clientid, 0, -1, month, year, MasterGSTConstants.GST_STATUS_NOTINGSTR2B, MasterGSTConstants.GST_STATUS_NOTINGSTR2B, yearCode,billdate, isMonthly);
		Page<? extends InvoiceParent> g2b_nostatus_invoices = reconsilationService.getGstr2BSupportInvoices(clientid, 0, -1, month, year, MasterGSTConstants.GST_STATUS_NOTINPURCHASES, MasterGSTConstants.GST_STATUS_NOTINPURCHASES, isMonthly);
		
		//it contains matchingstatus is not null & matchingid is not null
		Page<? extends InvoiceParent> prMatched = reconsilationService.getPurchaseInvoices(clientid, 0, -1, month, year, MasterGSTConstants.GST_STATUS_MATCHED, MasterGSTConstants.GST_STATUS_MATCHED,yearCode,billdate, isMonthly);
		Page<? extends InvoiceParent> g2bMatch = reconsilationService.getGstr2BSupportInvoices(clientid, 0, -1, month, year, MasterGSTConstants.GST_STATUS_MATCHED, MasterGSTConstants.GST_STATUS_MATCHED, isMonthly);

		List<InvoiceParent> g2b = Lists.newArrayList();
		List<InvoiceParent> g2bf = Lists.newArrayList();
		/*if(isNotEmpty(g2bMatch)) {
			g2b = (List<InvoiceParent>) g2bMatch.getContent();
		}
		if(isNotEmpty(g2b) && g2b.size() > 0) {
			g2bf.addAll(g2b);
		}*/
		List<InvoiceParent> prb = Lists.newArrayList();
		List<InvoiceParent> prbf = Lists.newArrayList();
		if(isNotEmpty(prMatched)) {
			prb = (List<InvoiceParent>) prMatched.getContent();
		}
		if(isNotEmpty(prb) && prb.size() > 0) {
			prbf.addAll(prb);
		}
		Map<String, List<String>> idsMap = findReconsilationids(prMatched,g2bMatch);
		if(isNotEmpty(idsMap)) {
			if(isNotEmpty(idsMap.get("2bIds"))) {
				List<InvoiceParent> gstr2binvoices =  Lists.newArrayList(gstr2bSupportRepository.findByIdIn(idsMap.get("2bIds")));
				if(isNotEmpty(gstr2binvoices) && gstr2binvoices.size() > 0) {
					gstr2binvoices.forEach(inv -> inv.setDocId(inv.getId().toString()));
					g2bf.addAll(gstr2binvoices);
				}
			}
			if(isNotEmpty(idsMap.get("prIds"))) {
				List<InvoiceParent> prinvoices =  Lists.newArrayList(purchaseRepository.findAll(idsMap.get("prIds")));
				if(isNotEmpty(prinvoices) && prinvoices.size() > 0) {
					prinvoices.forEach(inv -> inv.setDocId(inv.getId().toString()));
					prbf.addAll(prinvoices);
				}
			}
		}
		
		List<String> Reconsilation_G2B_Macthed_Docid = Lists.newArrayList();
		List<String> Reconsilation_PR_Macthed_Docid = Lists.newArrayList();
		if(isNotEmpty(g2bf) && g2bf.size() > 0) {
			for(InvoiceParent g2binv : g2bf) {
				for(InvoiceParent purinv : prbf) {
					if(isNotEmpty(purinv.getGstr2bMatchingId()) && purinv.getGstr2bMatchingId().size() > 0) {
						for(String g2bMatchingid : purinv.getGstr2bMatchingId()) {
							if(g2bMatchingid.equalsIgnoreCase(g2binv.getDocId())) {
								Reconsilation_PR_Macthed_Docid.add(purinv.getDocId());
								Reconsilation_G2B_Macthed_Docid.add(g2binv.getDocId());
								ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
								
								reconsileInvoices.setPurchaseStatus(purinv.getGstr2bMatchingStatus());
								if(isNotEmpty(purinv.getGstr2bMatchingStatus())) {
									reconsileInvoices.setStatus(purinv.getGstr2bMatchingStatus());
								}
								if(isNotEmpty(purinv.getInvtype())) {
									reconsileInvoices.setPurchaseInvtype(purinv.getInvtype());
								}
								if(isNotEmpty(purinv.getInvoiceno())) {
									reconsileInvoices.setPurchaseInvoiceno(purinv.getInvoiceno());
								}
								if(isNotEmpty(purinv.getDateofinvoice())) {
									reconsileInvoices.setPurchaseInvoicedate(sdf.format(purinv.getDateofinvoice()));
								}
								
								if (isNotEmpty(purinv.getBilledtoname())) {
									reconsileInvoices.setPurchaseSuppliername(purinv.getBilledtoname());
								}
								if(purinv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((PurchaseRegister)purinv).getImpGoods()) && isNotEmpty(((PurchaseRegister)purinv).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)purinv).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setPurchaseGstno(((PurchaseRegister)purinv).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(purinv.getB2b()) && isNotEmpty(purinv.getB2b().get(0).getCtin())) {
										reconsileInvoices.setPurchaseGstno(purinv.getB2b().get(0).getCtin());
									}
								}
								if(isNotEmpty(purinv.getBranch())) {
									reconsileInvoices.setPurchaseBranch(purinv.getBranch());					
								}
								if(isNotEmpty(purinv.getTotaltaxableamount())) {
									reconsileInvoices.setPurchaseTotaltaxableamount(purinv.getTotaltaxableamount());
								}
								if(isNotEmpty(purinv.getTotaltax())) {
									reconsileInvoices.setPurchaseTotalamount(purinv.getTotaltax());
								}
								if(isNotEmpty(purinv.getTotalIgstAmount())) {
									reconsileInvoices.setPurchaseTotalIgstAmount(purinv.getTotalIgstAmount());
								}
								if(isNotEmpty(purinv.getTotalCgstAmount())) {
									reconsileInvoices.setPurchaseTotalCgstAmount(purinv.getTotalCgstAmount());
								}
								if(isNotEmpty(purinv.getTotalSgstAmount())) {
									reconsileInvoices.setPurchaseTotalSgstAmount(purinv.getTotalSgstAmount());
								}
								if(isNotEmpty(purinv.getTotalCessAmount())) {
									reconsileInvoices.setPurchaseTotalCessAmount(purinv.getTotalCessAmount());
								}
								if(isNotEmpty(purinv.getRevchargetype())) {
									reconsileInvoices.setPurchaseReverseCharge(purinv.getRevchargetype());
								}
								if(isNotEmpty(purinv.getFp())) {
									reconsileInvoices.setPurchaseReturnPeriod(purinv.getFp());
								}
								
								if(isNotEmpty(purinv.getBillDate())) {
									reconsileInvoices.setPurchaseTransactionDate(sdf.format(purinv.getBillDate()));
								}
								
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
								}
								if(purinv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((PurchaseRegister)purinv).getImpGoods()) && isNotEmpty(((PurchaseRegister)purinv).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)purinv).getImpGoods().get(0).getStin())) {
										  String gstno = ((PurchaseRegister)purinv).getImpGoods().get(0).getStin().trim().substring(0, 2); 
										  StateConfig gstcode =  null;
										  if(Integer.parseInt(gstno) < 10) {
											  String tagName = gstno;
											  Query query = new Query();
											  query.limit(37);
											  query.addCriteria(Criteria.where("name").regex(tagName));
											  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
											  if(isNotEmpty(state)) {
												gstcode = state.get(0);  
											  }
										  }else {
											  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
										  }
										  if(isNotEmpty(gstcode)) {
											  reconsileInvoices.setPurchaseState(gstcode.getName()); 
										  }
										}
								}else {
								  if(isNotEmpty(purinv.getB2b()) && isNotEmpty(purinv.getB2b().get(0)) && isNotEmpty(purinv.getB2b().get(0).getCtin())) {
									  String gstno = purinv.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;
										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setPurchaseState(gstcode.getName()); 
									  } 
								  }
								}
								
								if(isNotEmpty(purinv.getCustomField1())) {
									reconsileInvoices.setPurchasecustomField1(purinv.getCustomField1());
								}
								if(isNotEmpty(purinv.getCustomField2())) {
									reconsileInvoices.setPurchasecustomField2(purinv.getCustomField2());
								}
								if(isNotEmpty(purinv.getCustomField3())) {
									reconsileInvoices.setPurchasecustomField3(purinv.getCustomField3());
								}
								if(isNotEmpty(purinv.getCustomField4())) {
									reconsileInvoices.setPurchasecustomField4(purinv.getCustomField4());
								}
								
								
								String matchingstatus = "Mismatched";
								if(!g2binv.getGstr2bMatchingStatus().equalsIgnoreCase("Matched") && !g2binv.getGstr2bMatchingStatus().equalsIgnoreCase("Round Off Matched") && !g2binv.getGstr2bMatchingStatus().equalsIgnoreCase("Matched In Other Months") && !g2binv.getGstr2bMatchingStatus().equalsIgnoreCase("Probable Matched")) {
									if((isNotEmpty(purinv.getInvoiceno()) && isNotEmpty(g2binv.getInvoiceno()) && purinv.getInvoiceno().trim().equals(g2binv.getInvoiceno().trim()))
											&& (isNotEmpty(purinv.getDateofinvoice()) && isNotEmpty(g2binv.getDateofinvoice()) &&  sdf.format(purinv.getDateofinvoice()).equals(sdf.format(g2binv.getDateofinvoice())))
											&& (isNotEmpty(purinv.getB2b().get(0).getCtin()) && isNotEmpty(g2binv.getB2b().get(0).getCtin()) && purinv.getB2b().get(0).getCtin().equals(g2binv.getB2b().get(0).getCtin()))
											&& (isNotEmpty(purinv.getTotaltaxableamount()) && isNotEmpty(g2binv.getTotaltaxableamount()) && purinv.getTotaltaxableamount().equals(g2binv.getTotaltaxableamount()))
											&& (isNotEmpty(purinv.getTotalamount()) && isNotEmpty(g2binv.getTotalamount()) && purinv.getTotalamount().equals(g2binv.getTotalamount()))
											&& (isNotEmpty(purinv.getTotaltax()) && isNotEmpty(g2binv.getTotaltax()) && purinv.getTotaltax().equals(g2binv.getTotaltax()))) {
											matchingstatus = "Matched";
									}else if((isNotEmpty(purinv.getInvoiceno()) && isNotEmpty(g2binv.getInvoiceno()) && purinv.getInvoiceno().trim().equals(g2binv.getInvoiceno().trim()))
											&& (isNotEmpty(purinv.getDateofinvoice()) && isNotEmpty(g2binv.getDateofinvoice()) && sdf.format(purinv.getDateofinvoice()).equals(sdf.format(g2binv.getDateofinvoice())))
											&& (isNotEmpty(purinv.getB2b().get(0).getCtin()) && isNotEmpty(g2binv.getB2b().get(0).getCtin()) && !purinv.getB2b().get(0).getCtin().equals(g2binv.getB2b().get(0).getCtin()))
											&& (isNotEmpty(purinv.getTotaltaxableamount()) && isNotEmpty(g2binv.getTotaltaxableamount()) && purinv.getTotaltaxableamount().equals(g2binv.getTotaltaxableamount()))
											&& (isNotEmpty(purinv.getTotalamount()) && isNotEmpty(g2binv.getTotalamount()) && purinv.getTotalamount().equals(g2binv.getTotalamount()))&& (isNotEmpty(purinv.getTotaltax()) && isNotEmpty(g2binv.getTotaltax()) && purinv.getTotaltax().equals(g2binv.getTotaltax()))) {
											matchingstatus = "GST No Mismatched";
									}else if((isNotEmpty(purinv.getInvoiceno()) && isNotEmpty(g2binv.getInvoiceno()) && purinv.getInvoiceno().trim().equals(g2binv.getInvoiceno()))
											&& (isNotEmpty(purinv.getDateofinvoice()) && isNotEmpty(g2binv.getDateofinvoice()) && sdf.format(purinv.getDateofinvoice()).equals(sdf.format(g2binv.getDateofinvoice())))
											&& (isNotEmpty(purinv.getB2b().get(0).getCtin()) && isNotEmpty(g2binv.getB2b().get(0).getCtin()) && purinv.getB2b().get(0).getCtin().equals(g2binv.getB2b().get(0).getCtin()))
											&& ((isNotEmpty(purinv.getTotaltaxableamount()) && isNotEmpty(g2binv.getTotaltaxableamount()) && !purinv.getTotaltaxableamount().equals(g2binv.getTotaltaxableamount()))
											|| (isNotEmpty(purinv.getTotalamount()) && isNotEmpty(g2binv.getTotalamount()) && !purinv.getTotalamount().equals(g2binv.getTotalamount())))
											&& (isNotEmpty(purinv.getTotaltax()) && isNotEmpty(g2binv.getTotaltax()) && purinv.getTotaltax().equals(g2binv.getTotaltax()))) {
											matchingstatus = "Invoice Value Mismatched";
									}else if((isNotEmpty(purinv.getInvoiceno()) && isNotEmpty(g2binv.getInvoiceno()) && purinv.getInvoiceno().trim().equals(g2binv.getInvoiceno()))
											&& (isNotEmpty(purinv.getDateofinvoice()) && isNotEmpty(g2binv.getDateofinvoice()) && sdf.format(purinv.getDateofinvoice()).equals(sdf.format(g2binv.getDateofinvoice())))
											&& (isNotEmpty(purinv.getB2b().get(0).getCtin()) && isNotEmpty(g2binv.getB2b().get(0).getCtin()) && purinv.getB2b().get(0).getCtin().equals(g2binv.getB2b().get(0).getCtin()))
											&& (isNotEmpty(purinv.getTotaltaxableamount()) && isNotEmpty(g2binv.getTotaltaxableamount()) && purinv.getTotaltaxableamount().equals(g2binv.getTotaltaxableamount()))
											&& (isNotEmpty(purinv.getTotalamount()) && isNotEmpty(g2binv.getTotalamount()) && purinv.getTotalamount().equals(g2binv.getTotalamount()))
											&& (isNotEmpty(purinv.getTotaltax()) && isNotEmpty(g2binv.getTotaltax()) && !purinv.getTotaltax().equals(g2binv.getTotaltax()))) {
											matchingstatus = "Tax Mismatched";
									}else if((isNotEmpty(purinv.getInvoiceno()) && isNotEmpty(g2binv.getInvoiceno()) && !purinv.getInvoiceno().trim().equals(g2binv.getInvoiceno()))
											&& (isNotEmpty(purinv.getDateofinvoice()) && isNotEmpty(g2binv.getDateofinvoice()) && sdf.format(purinv.getDateofinvoice()).equals(sdf.format(g2binv.getDateofinvoice())))
											&& (isNotEmpty(purinv.getB2b().get(0).getCtin()) && isNotEmpty(g2binv.getB2b().get(0).getCtin()) && purinv.getB2b().get(0).getCtin().equals(g2binv.getB2b().get(0).getCtin()))
											&& (isNotEmpty(purinv.getTotaltaxableamount()) && isNotEmpty(g2binv.getTotaltaxableamount()) && purinv.getTotaltaxableamount().equals(g2binv.getTotaltaxableamount()))
											&& (isNotEmpty(purinv.getTotalamount()) && isNotEmpty(g2binv.getTotalamount()) && purinv.getTotalamount().equals(g2binv.getTotalamount()))
											&& (isNotEmpty(purinv.getTotaltax()) && isNotEmpty(g2binv.getTotaltax()) && purinv.getTotaltax().equals(g2binv.getTotaltax()))) {
											matchingstatus = "Invoice No Mismatched";
									}else if((isNotEmpty(purinv.getInvoiceno()) && isNotEmpty(g2binv.getInvoiceno()) && purinv.getInvoiceno().trim().equals(g2binv.getInvoiceno()))
											&& (isNotEmpty(purinv.getDateofinvoice()) && isNotEmpty(g2binv.getDateofinvoice()) && !sdf.format(purinv.getDateofinvoice()).equals(sdf.format(g2binv.getDateofinvoice())))
											&& (isNotEmpty(purinv.getB2b().get(0).getCtin()) && isNotEmpty(g2binv.getB2b().get(0).getCtin()) && purinv.getB2b().get(0).getCtin().equals(g2binv.getB2b().get(0).getCtin()))
											&& (isNotEmpty(purinv.getTotaltaxableamount()) && isNotEmpty(g2binv.getTotaltaxableamount()) && purinv.getTotaltaxableamount().equals(g2binv.getTotaltaxableamount()))
											&& (isNotEmpty(purinv.getTotalamount()) && isNotEmpty(g2binv.getTotalamount()) && purinv.getTotalamount().equals(g2binv.getTotalamount()))
											&& (isNotEmpty(purinv.getTotaltax()) && isNotEmpty(g2binv.getTotaltax()) && purinv.getTotaltax().equals(g2binv.getTotaltax()))) {
											matchingstatus = "Invoice Date Mismatched";
									}
									
									reconsileInvoices.setStatus(matchingstatus);
								}
								
								if(isNotEmpty(g2binv.getInvtype())) {
									reconsileInvoices.setGstr2Invtype(g2binv.getInvtype());
								}
								if(isNotEmpty(g2binv.getInvoiceno())) {
									reconsileInvoices.setGstr2Invoiceno(g2binv.getInvoiceno());
								}
								if(isNotEmpty(g2binv.getDateofinvoice())) {
									reconsileInvoices.setGstr2Invoicedate(sdf.format(g2binv.getDateofinvoice()));
								}
								if(isNotEmpty(g2binv.getTotaltaxableamount())) {
									reconsileInvoices.setGstr2Totaltaxableamount(g2binv.getTotaltaxableamount());
								}
								if(isNotEmpty(g2binv.getTotaltax())) {
									reconsileInvoices.setGstr2Totalamount(g2binv.getTotaltax());
								}
								if(isNotEmpty(g2binv.getGstr2bMatchingStatus())) {
									reconsileInvoices.setGstr2status(g2binv.getGstr2bMatchingStatus());
								}
								if(isNotEmpty(g2binv.getTotalIgstAmount())) {
									reconsileInvoices.setGstr2TotalIgstAmount(g2binv.getTotalIgstAmount());
								}
								if(isNotEmpty(g2binv.getTotalCgstAmount())) {
									reconsileInvoices.setGstr2TotalCgstAmount(g2binv.getTotalCgstAmount());
								}
								if(isNotEmpty(g2binv.getTotalSgstAmount())) {
									reconsileInvoices.setGstr2TotalSgstAmount(g2binv.getTotalSgstAmount());
								}
								if(isNotEmpty(g2binv.getTotalCessAmount())) {
									reconsileInvoices.setGstr2TotalCessAmount(g2binv.getTotalCessAmount());
								}
								if(isNotEmpty(g2binv.getBranch())) {
									reconsileInvoices.setGstr2Branch(g2binv.getBranch());
								}
								if (isNotEmpty(g2binv.getBilledtoname())) {
									reconsileInvoices.setGstr2Suppliername(g2binv.getBilledtoname());
								}
								if(g2binv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((GSTR2BSupport)g2binv).getImpGoods()) && isNotEmpty(((GSTR2BSupport)g2binv).getImpGoods().get(0)) && isNotEmpty(((GSTR2BSupport)g2binv).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setGstr2Gstno(((GSTR2BSupport)g2binv).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(g2binv.getB2b()) && isNotEmpty(g2binv.getB2b().get(0).getCtin())) {
										reconsileInvoices.setGstr2Gstno(g2binv.getB2b().get(0).getCtin());
									}
								}
								
								if(isNotEmpty(g2binv.getRevchargetype())) {
									reconsileInvoices.setGstr2ReverseCharge(g2binv.getRevchargetype());
								}
								if(isNotEmpty(g2binv.getFp())) {
									reconsileInvoices.setGstr2ReturnPeriod(g2binv.getFp());
								}
								
								if(isNotEmpty(g2binv.getBillDate())) {
									reconsileInvoices.setGstr2TransactionDate(sdf.format(g2binv.getBillDate()));
								}
								
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
								}
								if(g2binv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((GSTR2BSupport)g2binv).getImpGoods()) && isNotEmpty(((GSTR2BSupport)g2binv).getImpGoods().get(0)) && isNotEmpty(((GSTR2BSupport)g2binv).getImpGoods().get(0).getStin())) {
										  String gstno = ((GSTR2BSupport)g2binv).getImpGoods().get(0).getStin().trim().substring(0, 2); 
										  StateConfig gstcode =  null;
										  if(Integer.parseInt(gstno) < 10) {
											  String tagName = gstno;

											  Query query = new Query();
											  query.limit(37);
											  query.addCriteria(Criteria.where("name").regex(tagName));
											  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
											  if(isNotEmpty(state)) {
												gstcode = state.get(0);  
											  }
											  
										  }else {
											  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
										  }
										   
										  if(isNotEmpty(gstcode)) {
											  reconsileInvoices.setGstr2State(gstcode.getName()); 
										  }
									}
								}else {
								  if(isNotEmpty(g2binv.getB2b()) && isNotEmpty(g2binv.getB2b().get(0)) && isNotEmpty(g2binv.getB2b().get(0).getCtin())) {
									  String gstno = g2binv.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setGstr2State(gstcode.getName()); 
									  } 
								  }
								}
								  if(isNotEmpty(g2binv.getCustomField1())) {
									reconsileInvoices.setGstr2customField1(g2binv.getCustomField1());
								  }
								  if(isNotEmpty(g2binv.getCustomField2())) {
									reconsileInvoices.setGstr2customField2(g2binv.getCustomField2());
								  }
								  if(isNotEmpty(g2binv.getCustomField3())) {
									reconsileInvoices.setGstr2customField3(g2binv.getCustomField3());
								  }
								  if(isNotEmpty(g2binv.getCustomField4())) {
									reconsileInvoices.setGstr2customField4(g2binv.getCustomField4());
								  }
								  reconsileInvoicesLst.add(reconsileInvoices);
							}
						}
					}
				}
			}
		}
		if(isNotEmpty(g2bf) && g2bf.size()>0) {
			for(InvoiceParent g2binv : g2bf) {
				if(!Reconsilation_G2B_Macthed_Docid.contains(g2binv.getDocId())) {
					ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
					reconsileInvoices.setStatus("Not in Purchases");
					if(isNotEmpty(g2binv.getInvtype())) {
						reconsileInvoices.setGstr2Invtype(g2binv.getInvtype());
					}
					if(isNotEmpty(g2binv.getInvoiceno())) {
						reconsileInvoices.setGstr2Invoiceno(g2binv.getInvoiceno());
					}
					if(isNotEmpty(g2binv.getDateofinvoice())) {
						reconsileInvoices.setGstr2Invoicedate(sdf.format(g2binv.getDateofinvoice()));
					}
					if(isNotEmpty(g2binv.getBranch())) {
						reconsileInvoices.setGstr2Branch(g2binv.getBranch());
					}
					if (isNotEmpty(g2binv.getBilledtoname())) {
						reconsileInvoices.setGstr2Suppliername(g2binv.getBilledtoname());
					}
					if(g2binv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
						if (isNotEmpty(((GSTR2BSupport)g2binv).getImpGoods()) && isNotEmpty(((GSTR2BSupport)g2binv).getImpGoods().get(0)) && isNotEmpty(((GSTR2BSupport)g2binv).getImpGoods().get(0).getStin())) {
							reconsileInvoices.setGstr2Gstno(((GSTR2BSupport)g2binv).getImpGoods().get(0).getStin());
						}
					}else {
						if (isNotEmpty(g2binv.getB2b()) && isNotEmpty(g2binv.getB2b().get(0).getCtin())) {
							reconsileInvoices.setGstr2Gstno(g2binv.getB2b().get(0).getCtin());
						}
					}
					if(isNotEmpty(g2binv.getTotaltaxableamount())) {
						reconsileInvoices.setGstr2Totaltaxableamount(g2binv.getTotaltaxableamount());
					}
					if(isNotEmpty(g2binv.getTotaltax())) {
						reconsileInvoices.setGstr2Totalamount(g2binv.getTotaltax());
					}
					if(isNotEmpty(g2binv.getTotalIgstAmount())) {
						reconsileInvoices.setGstr2TotalIgstAmount(g2binv.getTotalIgstAmount());
					}
					if(isNotEmpty(g2binv.getTotalCgstAmount())) {
						reconsileInvoices.setGstr2TotalCgstAmount(g2binv.getTotalCgstAmount());
					}
					if(isNotEmpty(g2binv.getTotalSgstAmount())) {
						reconsileInvoices.setGstr2TotalSgstAmount(g2binv.getTotalSgstAmount());
					}
					if(isNotEmpty(g2binv.getTotalCessAmount())) {
						reconsileInvoices.setGstr2TotalCessAmount(g2binv.getTotalCessAmount());
					}
					if(isNotEmpty(g2binv.getRevchargetype())) {
						reconsileInvoices.setGstr2ReverseCharge(g2binv.getRevchargetype());
					}
					if(isNotEmpty(g2binv.getFp())) {
						reconsileInvoices.setGstr2ReturnPeriod(g2binv.getFp());
					}
					if(isNotEmpty(g2binv.getBillDate())) {
						reconsileInvoices.setGstr2TransactionDate(sdf.format(g2binv.getBillDate()));
					}
					if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
						reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
					}
					if(isNotEmpty(g2binv.getCustomField1())) {
						reconsileInvoices.setGstr2customField1(g2binv.getCustomField1());
					  }
					  if(isNotEmpty(g2binv.getCustomField2())) {
						reconsileInvoices.setGstr2customField2(g2binv.getCustomField2());
					  }
					  if(isNotEmpty(g2binv.getCustomField3())) {
						reconsileInvoices.setGstr2customField3(g2binv.getCustomField3());
					  }
					  if(isNotEmpty(g2binv.getCustomField4())) {
						reconsileInvoices.setGstr2customField4(g2binv.getCustomField4());
					  }
						if(g2binv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
							if (isNotEmpty(((GSTR2BSupport)g2binv).getImpGoods()) && isNotEmpty(((GSTR2BSupport)g2binv).getImpGoods().get(0)) && isNotEmpty(((GSTR2BSupport)g2binv).getImpGoods().get(0).getStin())) {
								  String gstno = ((GSTR2BSupport)g2binv).getImpGoods().get(0).getStin().trim().substring(0, 2); 
								  StateConfig gstcode =  null;
								  if(Integer.parseInt(gstno) < 10) {
									  String tagName = gstno;

									  Query query = new Query();
									  query.limit(37);
									  query.addCriteria(Criteria.where("name").regex(tagName));
									  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
									  if(isNotEmpty(state)) {
										gstcode = state.get(0);  
									  }
									  
								  }else {
									  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
								  }
								   
								  if(isNotEmpty(gstcode)) {
									  reconsileInvoices.setGstr2State(gstcode.getName()); 
								  }
							}
						}else {
					  if(isNotEmpty(g2binv.getB2b()) && isNotEmpty(g2binv.getB2b().get(0)) && isNotEmpty(g2binv.getB2b().get(0).getCtin())) {
						  String gstno = g2binv.getB2b().get(0).getCtin().trim().substring(0, 2); 
						  StateConfig gstcode =  null;
						  if(Integer.parseInt(gstno) < 10) {
							  String tagName = gstno;
							  Query query = new Query();
							  query.limit(37);
							  query.addCriteria(Criteria.where("name").regex(tagName));
							  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
							  if(isNotEmpty(state)) {
								gstcode = state.get(0);  
							  }
						  }else {
							  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
						  }
						  if(isNotEmpty(gstcode)) {
							  reconsileInvoices.setGstr2State(gstcode.getName()); 
						  } 
					  }
					}
					reconsileInvoices.setGstr2Branch(g2binv.getBranch());
					reconsileInvoices.setPurchaseInvtype("-");
					reconsileInvoices.setPurchaseInvoiceno("-");
					reconsileInvoices.setPurchaseInvoicedate("-");
					reconsileInvoices.setPurchaseBranch("-");
					reconsileInvoices.setPurchaseSuppliername("-");
					reconsileInvoices.setPurchaseGstno("-");
					reconsileInvoices.setPurchaseReverseCharge("-");
					reconsileInvoices.setPurchaseReturnPeriod("-");
					reconsileInvoices.setPurchaseTransactionDate("-");
					reconsileInvoices.setPurchaseCompanyStateName("-");
					reconsileInvoices.setPurchaseState("-");
					reconsileInvoices.setPurchasecustomField1("-");
					reconsileInvoices.setPurchasecustomField2("-");
					reconsileInvoices.setPurchasecustomField3("-");
					reconsileInvoices.setPurchasecustomField4("-");
					reconsileInvoicesLst.add(reconsileInvoices);
				}
			}
		}
		if(isNotEmpty(prbf) && prbf.size()>0) {
			for(InvoiceParent prinv : prbf) {
				if(!Reconsilation_PR_Macthed_Docid.contains(prinv.getDocId())) {
					ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
					
					reconsileInvoices.setStatus("Not in GSTR2B");
					if(isNotEmpty(prinv.getInvtype())) {
						reconsileInvoices.setPurchaseInvtype(prinv.getInvtype());
					}
					if(isNotEmpty(prinv.getInvoiceno())) {
						reconsileInvoices.setPurchaseInvoiceno(prinv.getInvoiceno());
					}
					if(isNotEmpty(prinv.getDateofinvoice())) {
						reconsileInvoices.setPurchaseInvoicedate(sdf.format(prinv.getDateofinvoice()));
					}
					if(isNotEmpty(prinv.getBranch())) {
						reconsileInvoices.setPurchaseBranch(prinv.getBranch());
					}
					if (isNotEmpty(prinv.getBilledtoname())) {
						reconsileInvoices.setPurchaseSuppliername(prinv.getBilledtoname());
					}
					if(prinv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
						if (isNotEmpty(((PurchaseRegister)prinv).getImpGoods()) && isNotEmpty(((PurchaseRegister)prinv).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)prinv).getImpGoods().get(0).getStin())) {
							reconsileInvoices.setPurchaseGstno(((PurchaseRegister)prinv).getImpGoods().get(0).getStin());
						}
					}else {
						if (isNotEmpty(prinv.getB2b()) && isNotEmpty(prinv.getB2b().get(0).getCtin())) {
							reconsileInvoices.setPurchaseGstno(prinv.getB2b().get(0).getCtin());
						}
					}
					if (isNotEmpty(prinv.getTotaltaxableamount())){
						reconsileInvoices.setPurchaseTotaltaxableamount(prinv.getTotaltaxableamount());					
					}
					if (isNotEmpty(prinv.getTotaltax())){
						reconsileInvoices.setPurchaseTotalamount(prinv.getTotaltax());					
					}
					if(isNotEmpty(prinv.getTotalIgstAmount())) {
						reconsileInvoices.setPurchaseTotalIgstAmount(prinv.getTotalIgstAmount());
					}
					if(isNotEmpty(prinv.getTotalCgstAmount())) {
						reconsileInvoices.setPurchaseTotalCgstAmount(prinv.getTotalCgstAmount());
					}
					if(isNotEmpty(prinv.getTotalSgstAmount())) {
						reconsileInvoices.setPurchaseTotalSgstAmount(prinv.getTotalSgstAmount());
					}
					if(isNotEmpty(prinv.getTotalCessAmount())) {
						reconsileInvoices.setPurchaseTotalCessAmount(prinv.getTotalCessAmount());
					}
					if(isNotEmpty(prinv.getRevchargetype())) {
						reconsileInvoices.setPurchaseReverseCharge(prinv.getRevchargetype());
					}
					if(isNotEmpty(prinv.getFp())) {
						reconsileInvoices.setPurchaseReturnPeriod(prinv.getFp());
					}
					
					if(isNotEmpty(prinv.getBillDate())) {
						reconsileInvoices.setPurchaseTransactionDate(sdf.format(prinv.getBillDate()));
					}
					
					if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
						reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
					}
					if(prinv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
						if (isNotEmpty(((PurchaseRegister)prinv).getImpGoods()) && isNotEmpty(((PurchaseRegister)prinv).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)prinv).getImpGoods().get(0).getStin())) {
							  String gstno = ((PurchaseRegister)prinv).getImpGoods().get(0).getStin().trim().substring(0, 2); 
							  StateConfig gstcode =  null;
							  if(Integer.parseInt(gstno) < 10) {
								  String tagName = gstno;

								  Query query = new Query();
								  query.limit(37);
								  query.addCriteria(Criteria.where("name").regex(tagName));
								  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
								  if(isNotEmpty(state)) {
									gstcode = state.get(0);  
								  }
								  
							  }else {
								  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
							  }
							   
							  if(isNotEmpty(gstcode)) {
								  reconsileInvoices.setPurchaseState(gstcode.getName()); 
							  }
						}
					}else {
					  if(isNotEmpty(prinv.getB2b()) && isNotEmpty(prinv.getB2b().get(0)) && isNotEmpty(prinv.getB2b().get(0).getCtin())) {
						  String gstno = prinv.getB2b().get(0).getCtin().trim().substring(0, 2); 
						  StateConfig gstcode =  null;
						  if(Integer.parseInt(gstno) < 10) {
							  String tagName = gstno;

							  Query query = new Query();
							  query.limit(37);
							  query.addCriteria(Criteria.where("name").regex(tagName));
							  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
							  if(isNotEmpty(state)) {
								gstcode = state.get(0);  
							  }
							  
						  }else {
							  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
						  }
						   
						  if(isNotEmpty(gstcode)) {
							  reconsileInvoices.setPurchaseState(gstcode.getName()); 
						  } 
					  }
					}
					if(isNotEmpty(prinv.getCustomField1())) {
						reconsileInvoices.setPurchasecustomField1(prinv.getCustomField1());
					}
					if(isNotEmpty(prinv.getCustomField2())) {
						reconsileInvoices.setPurchasecustomField2(prinv.getCustomField2());
					}
					if(isNotEmpty(prinv.getCustomField3())) {
						reconsileInvoices.setPurchasecustomField3(prinv.getCustomField3());
					}
					if(isNotEmpty(prinv.getCustomField4())) {
						reconsileInvoices.setPurchasecustomField4(prinv.getCustomField4());
					}
					reconsileInvoices.setGstr2Invtype("-");
					reconsileInvoices.setGstr2Invoiceno("-");
					reconsileInvoices.setGstr2Invoicedate("-");
					reconsileInvoices.setGstr2Branch("-");
					reconsileInvoices.setGstr2Suppliername("-");
					reconsileInvoices.setGstr2Gstno("-");
					reconsileInvoices.setGstr2CompanyStateName("-");
					reconsileInvoices.setGstr2ReturnPeriod("-");
					reconsileInvoices.setGstr2ReverseCharge("-");
					reconsileInvoices.setGstr2State("-");
					reconsileInvoices.setGstr2TransactionDate("-");
					reconsileInvoices.setGstr2customField1("-");
					reconsileInvoices.setGstr2customField2("-");
					reconsileInvoices.setGstr2customField3("-");
					reconsileInvoices.setGstr2customField4("-");
					
					reconsileInvoicesLst.add(reconsileInvoices);
				}
			}
		}
		if(isNotEmpty(pr_nostatus_invoices) && isNotEmpty(pr_nostatus_invoices.getContent()) && pr_nostatus_invoices.getContent().size()>0) {
			for(InvoiceParent invoice:pr_nostatus_invoices.getContent()){
				
				ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
				
				reconsileInvoices.setStatus("Not in GSTR2B");
				if(isNotEmpty(invoice.getInvtype())) {
					reconsileInvoices.setPurchaseInvtype(invoice.getInvtype());
				}
				if(isNotEmpty(invoice.getInvoiceno())) {
					reconsileInvoices.setPurchaseInvoiceno(invoice.getInvoiceno());
				}
				if(isNotEmpty(invoice.getDateofinvoice())) {
					reconsileInvoices.setPurchaseInvoicedate(sdf.format(invoice.getDateofinvoice()));
				}
				if(isNotEmpty(invoice.getBranch())) {
					reconsileInvoices.setPurchaseBranch(invoice.getBranch());
				}
				if (isNotEmpty(invoice.getBilledtoname())) {
					reconsileInvoices.setPurchaseSuppliername(invoice.getBilledtoname());
				}
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
						reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
					}
				}else {
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						reconsileInvoices.setPurchaseGstno(invoice.getB2b().get(0).getCtin());
					}
				}
				if (isNotEmpty(invoice.getTotaltaxableamount())){
					reconsileInvoices.setPurchaseTotaltaxableamount(invoice.getTotaltaxableamount());					
				}
				if (isNotEmpty(invoice.getTotaltax())){
					reconsileInvoices.setPurchaseTotalamount(invoice.getTotaltax());					
				}
				if(isNotEmpty(invoice.getTotalIgstAmount())) {
					reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
				}
				if(isNotEmpty(invoice.getTotalCgstAmount())) {
					reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
				}
				if(isNotEmpty(invoice.getTotalSgstAmount())) {
					reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
				}
				if(isNotEmpty(invoice.getTotalCessAmount())) {
					reconsileInvoices.setPurchaseTotalCessAmount(invoice.getTotalCessAmount());
				}
				if(isNotEmpty(invoice.getRevchargetype())) {
					reconsileInvoices.setPurchaseReverseCharge(invoice.getRevchargetype());
				}
				if(isNotEmpty(invoice.getFp())) {
					reconsileInvoices.setPurchaseReturnPeriod(invoice.getFp());
				}
				
				if(isNotEmpty(invoice.getBillDate())) {
					reconsileInvoices.setPurchaseTransactionDate(sdf.format(invoice.getBillDate()));
				}
				
				if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
					reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
				}
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
						  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
						  StateConfig gstcode =  null;
						  if(Integer.parseInt(gstno) < 10) {
							  String tagName = gstno;

							  Query query = new Query();
							  query.limit(37);
							  query.addCriteria(Criteria.where("name").regex(tagName));
							  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
							  if(isNotEmpty(state)) {
								gstcode = state.get(0);  
							  }
							  
						  }else {
							  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
						  }
						   
						  if(isNotEmpty(gstcode)) {
							  reconsileInvoices.setPurchaseState(gstcode.getName()); 
						  }
					}
				}else {
				  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
					  StateConfig gstcode =  null;
					  if(Integer.parseInt(gstno) < 10) {
						  String tagName = gstno;

						  Query query = new Query();
						  query.limit(37);
						  query.addCriteria(Criteria.where("name").regex(tagName));
						  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
						  if(isNotEmpty(state)) {
							gstcode = state.get(0);  
						  }
						  
					  }else {
						  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
					  }
					   
					  if(isNotEmpty(gstcode)) {
						  reconsileInvoices.setPurchaseState(gstcode.getName()); 
					  } 
				  }
				}
				if(isNotEmpty(invoice.getCustomField1())) {
					reconsileInvoices.setPurchasecustomField1(invoice.getCustomField1());
				}
				if(isNotEmpty(invoice.getCustomField2())) {
					reconsileInvoices.setPurchasecustomField2(invoice.getCustomField2());
				}
				if(isNotEmpty(invoice.getCustomField3())) {
					reconsileInvoices.setPurchasecustomField3(invoice.getCustomField3());
				}
				if(isNotEmpty(invoice.getCustomField4())) {
					reconsileInvoices.setPurchasecustomField4(invoice.getCustomField4());
				}
				reconsileInvoices.setGstr2Invtype("-");
				reconsileInvoices.setGstr2Invoiceno("-");
				reconsileInvoices.setGstr2Invoicedate("-");
				reconsileInvoices.setGstr2Branch("-");
				reconsileInvoices.setGstr2Suppliername("-");
				reconsileInvoices.setGstr2Gstno("-");
				reconsileInvoices.setGstr2CompanyStateName("-");
				reconsileInvoices.setGstr2ReturnPeriod("-");
				reconsileInvoices.setGstr2ReverseCharge("-");
				reconsileInvoices.setGstr2State("-");
				reconsileInvoices.setGstr2TransactionDate("-");
				reconsileInvoices.setGstr2customField1("-");
				reconsileInvoices.setGstr2customField2("-");
				reconsileInvoices.setGstr2customField3("-");
				reconsileInvoices.setGstr2customField4("-");
				
				reconsileInvoicesLst.add(reconsileInvoices);
			}
		}
		
		if(isNotEmpty(g2b_nostatus_invoices) && isNotEmpty(g2b_nostatus_invoices.getContent()) && g2b_nostatus_invoices.getContent().size()>0) {
			for(InvoiceParent invoice:g2b_nostatus_invoices.getContent()){
				ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
				reconsileInvoices.setStatus("Not in Purchases");
				if(isNotEmpty(invoice.getInvtype())) {
					reconsileInvoices.setGstr2Invtype(invoice.getInvtype());
				}
				if(isNotEmpty(invoice.getInvoiceno())) {
					reconsileInvoices.setGstr2Invoiceno(invoice.getInvoiceno());
				}
				if(isNotEmpty(invoice.getDateofinvoice())) {
					reconsileInvoices.setGstr2Invoicedate(sdf.format(invoice.getDateofinvoice()));
				}
				if(isNotEmpty(invoice.getBranch())) {
					reconsileInvoices.setGstr2Branch(invoice.getBranch());
				}
				if (isNotEmpty(invoice.getBilledtoname())) {
					reconsileInvoices.setGstr2Suppliername(invoice.getBilledtoname());
				}
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					if (isNotEmpty(((GSTR2BSupport)invoice).getImpGoods()) && isNotEmpty(((GSTR2BSupport)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2BSupport)invoice).getImpGoods().get(0).getStin())) {
						reconsileInvoices.setGstr2Gstno(((GSTR2BSupport)invoice).getImpGoods().get(0).getStin());
					}
				}else {
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						reconsileInvoices.setGstr2Gstno(invoice.getB2b().get(0).getCtin());
					}
				}
				if(isNotEmpty(invoice.getTotaltaxableamount())) {
					reconsileInvoices.setGstr2Totaltaxableamount(invoice.getTotaltaxableamount());
				}
				if(isNotEmpty(invoice.getTotaltax())) {
					reconsileInvoices.setGstr2Totalamount(invoice.getTotaltax());
				}
				if(isNotEmpty(invoice.getTotalIgstAmount())) {
					reconsileInvoices.setGstr2TotalIgstAmount(invoice.getTotalIgstAmount());
				}
				if(isNotEmpty(invoice.getTotalCgstAmount())) {
					reconsileInvoices.setGstr2TotalCgstAmount(invoice.getTotalCgstAmount());
				}
				if(isNotEmpty(invoice.getTotalSgstAmount())) {
					reconsileInvoices.setGstr2TotalSgstAmount(invoice.getTotalSgstAmount());
				}
				if(isNotEmpty(invoice.getTotalCessAmount())) {
					reconsileInvoices.setGstr2TotalCessAmount(invoice.getTotalCessAmount());
				}
				if(isNotEmpty(invoice.getRevchargetype())) {
					reconsileInvoices.setGstr2ReverseCharge(invoice.getRevchargetype());
				}
				if(isNotEmpty(invoice.getFp())) {
					reconsileInvoices.setGstr2ReturnPeriod(invoice.getFp());
				}
				if(isNotEmpty(invoice.getBillDate())) {
					reconsileInvoices.setGstr2TransactionDate(sdf.format(invoice.getBillDate()));
				}
				if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
					reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
				}
				if(isNotEmpty(invoice.getCustomField1())) {
					reconsileInvoices.setGstr2customField1(invoice.getCustomField1());
				  }
				  if(isNotEmpty(invoice.getCustomField2())) {
					reconsileInvoices.setGstr2customField2(invoice.getCustomField2());
				  }
				  if(isNotEmpty(invoice.getCustomField3())) {
					reconsileInvoices.setGstr2customField3(invoice.getCustomField3());
				  }
				  if(isNotEmpty(invoice.getCustomField4())) {
					reconsileInvoices.setGstr2customField4(invoice.getCustomField4());
				  }
					if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
						if (isNotEmpty(((GSTR2BSupport)invoice).getImpGoods()) && isNotEmpty(((GSTR2BSupport)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2BSupport)invoice).getImpGoods().get(0).getStin())) {
							  String gstno = ((GSTR2BSupport)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
							  StateConfig gstcode =  null;
							  if(Integer.parseInt(gstno) < 10) {
								  String tagName = gstno;

								  Query query = new Query();
								  query.limit(37);
								  query.addCriteria(Criteria.where("name").regex(tagName));
								  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
								  if(isNotEmpty(state)) {
									gstcode = state.get(0);  
								  }
								  
							  }else {
								  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
							  }
							   
							  if(isNotEmpty(gstcode)) {
								  reconsileInvoices.setGstr2State(gstcode.getName()); 
							  }
						}
					}else {
				  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
					  StateConfig gstcode =  null;
					  if(Integer.parseInt(gstno) < 10) {
						  String tagName = gstno;
						  Query query = new Query();
						  query.limit(37);
						  query.addCriteria(Criteria.where("name").regex(tagName));
						  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
						  if(isNotEmpty(state)) {
							gstcode = state.get(0);  
						  }
					  }else {
						  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
					  }
					  if(isNotEmpty(gstcode)) {
						  reconsileInvoices.setGstr2State(gstcode.getName()); 
					  } 
				  }
				}
				reconsileInvoices.setGstr2Branch(invoice.getBranch());
				reconsileInvoices.setPurchaseInvtype("-");
				reconsileInvoices.setPurchaseInvoiceno("-");
				reconsileInvoices.setPurchaseInvoicedate("-");
				reconsileInvoices.setPurchaseBranch("-");
				reconsileInvoices.setPurchaseSuppliername("-");
				reconsileInvoices.setPurchaseGstno("-");
				reconsileInvoices.setPurchaseReverseCharge("-");
				reconsileInvoices.setPurchaseReturnPeriod("-");
				reconsileInvoices.setPurchaseTransactionDate("-");
				reconsileInvoices.setPurchaseCompanyStateName("-");
				reconsileInvoices.setPurchaseState("-");
				reconsileInvoices.setPurchasecustomField1("-");
				reconsileInvoices.setPurchasecustomField2("-");
				reconsileInvoices.setPurchasecustomField3("-");
				reconsileInvoices.setPurchasecustomField4("-");
				reconsileInvoicesLst.add(reconsileInvoices);
			}
		}
		List<ReconsileInvoices>  manuallymatched = null;
		Page<? extends InvoiceParent> prManualMatched = reconsilationService.getPurchaseInvoices(clientid, 0, -1, month, year, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED,yearCode,billdate, isMonthly);
		Page<? extends InvoiceParent> g2bManualMatch = reconsilationService.getGstr2BSupportInvoices(clientid, 0, -1, month, year, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED, isMonthly);
		manuallymatched = getManuallyMactched_MonthlyGstr2B(clientid, prManualMatched, g2bManualMatch);
		reconsileInvoicesLst.addAll(manuallymatched);
		return reconsileInvoicesLst;
	}
	
	
	
	public List<ReconsileInvoices> getManuallyMactched_MonthlyGstr2B(String clientid, Page<? extends InvoiceParent> purchaseinvsMannualMatchingId,
			Page<? extends InvoiceParent> gstr2aMannualMatchingStatus){
		
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		List<ReconsileInvoices> reconsileInvoicesLst=new ArrayList<>(); 
		Map<String,InvoiceParent> gstr2aMannualMatchingStatusMap=new HashMap<>();
		Map<String,InvoiceParent> purchaseMannualMatchingStatusMap=new HashMap<>();
		Map<String,InvoiceParent> gstr2MannualMatchingStatus_IdMap=new HashMap<>();
		Map<String,InvoiceParent> purchaseMannualMatchingStatus_IdMap=new HashMap<>();
		Map<String,List<InvoiceParent>> gstr2aMannualMatchingStatus_MultipleMap=new HashMap<>();
		Map<String,List<InvoiceParent>> purchaseMannualMatchingStatus_MultipleMap=new HashMap<>();
		Client client = clientService.findById(clientid);
		Set<String> gstr2_manuallymappedids=new HashSet<>();
		Set<String> purchase_manuallymappedids=new HashSet<>();
		
		if(isNotEmpty(gstr2aMannualMatchingStatus) && isNotEmpty(gstr2aMannualMatchingStatus.getContent()) && gstr2aMannualMatchingStatus.getContent().size() > 0) {
			for(InvoiceParent invoice: gstr2aMannualMatchingStatus.getContent()) {
				if(isNotEmpty(invoice.getGstr2bMatchingRsn()) && (invoice.getGstr2bMatchingRsn().equalsIgnoreCase("PR-Multiple") || invoice.getGstr2bMatchingRsn().equalsIgnoreCase("G2B-Multiple"))) {
					if(isNotEmpty(invoice.getGstr2bMatchingId()) && invoice.getGstr2bMatchingId().size()>0) {
						for(String matchid : invoice.getGstr2bMatchingId()) {
							List<InvoiceParent> existinvLst=gstr2aMannualMatchingStatus_MultipleMap.get(matchid);
							if(isNotEmpty(existinvLst)) {
								existinvLst.add(invoice);
								gstr2aMannualMatchingStatus_MultipleMap.put(matchid,existinvLst);
							}else {
								List<InvoiceParent> invLst=new ArrayList<>();
								invLst.add(invoice);
								gstr2aMannualMatchingStatus_MultipleMap.put(matchid,invLst);
							}
						}
					}
				}else {
					gstr2_manuallymappedids.add(invoice.getId().toString());
					gstr2aMannualMatchingStatusMap.put(invoice.getId().toString(), invoice);
					if(isNotEmpty(invoice.getGstr2bMatchingId()) && invoice.getGstr2bMatchingId().size()>0) {
						for(String matchid : invoice.getGstr2bMatchingId()) {
							gstr2MannualMatchingStatus_IdMap.put(matchid, invoice);
						}
					}	
				}
			}
		}
		if(isNotEmpty(purchaseinvsMannualMatchingId) && isNotEmpty(purchaseinvsMannualMatchingId.getContent()) && purchaseinvsMannualMatchingId.getContent().size() > 0) {
			for(InvoiceParent invoice: purchaseinvsMannualMatchingId.getContent()) {
				if(isNotEmpty(invoice.getGstr2bMatchingRsn()) && (invoice.getGstr2bMatchingRsn().equalsIgnoreCase("PR-Multiple") || invoice.getGstr2bMatchingRsn().equalsIgnoreCase("G2B-Multiple"))) {
					if(isNotEmpty(invoice.getGstr2bMatchingId()) && invoice.getGstr2bMatchingId().size()>0) {
						for(String matchid : invoice.getGstr2bMatchingId()) {
							List<InvoiceParent> existinvLst=purchaseMannualMatchingStatus_MultipleMap.get(matchid);
							if(isNotEmpty(existinvLst)) {
								existinvLst.add(invoice);
								purchaseMannualMatchingStatus_MultipleMap.put(matchid,existinvLst);
							}else {
								List<InvoiceParent> invLst=new ArrayList<>();
								invLst.add(invoice);
								purchaseMannualMatchingStatus_MultipleMap.put(matchid,invLst);
							}
						}
					}
				}else {
					purchase_manuallymappedids.add(invoice.getId().toString());
					purchaseMannualMatchingStatusMap.put(invoice.getId().toString(), invoice);
					if(isNotEmpty(invoice.getGstr2bMatchingId()) && invoice.getGstr2bMatchingId().size()>0) {
						for(String matchid : invoice.getGstr2bMatchingId()) {
							purchaseMannualMatchingStatus_IdMap.put(matchid, invoice);
						}
					}	
				}
			}
		}
		if(isNotEmpty(purchaseinvsMannualMatchingId) &&  isNotEmpty(purchaseinvsMannualMatchingId.getContent()) && purchaseinvsMannualMatchingId.getContent().size() > 0) {
				for(InvoiceParent invoice: purchaseinvsMannualMatchingId.getContent()) {
				if(isNotEmpty(invoice.getGstr2bMatchingRsn()) && (invoice.getGstr2bMatchingRsn().equalsIgnoreCase("G2B-Single") || invoice.getGstr2bMatchingRsn().equalsIgnoreCase("PR-Single"))) {
					if(isNotEmpty(invoice.getId().toString())) {
						InvoiceParent gstr2binv = gstr2MannualMatchingStatus_IdMap.get(invoice.getId().toString());
						if(isEmpty(gstr2binv)) {
							if(isNotEmpty(invoice.getGstr2bMatchingId()) && invoice.getGstr2bMatchingId().size()>0) {
								gstr2binv = gstr2bSupportRepository.findOne(invoice.getGstr2bMatchingId().get(0));
							}
						}
						if(isNotEmpty(gstr2binv)) {
							ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
							
							if(isNotEmpty(invoice.getGstr2bMatchingStatus())) {
								reconsileInvoices.setPurchaseStatus(invoice.getGstr2bMatchingStatus());
							}
							if(isNotEmpty(invoice.getGstr2bMatchingStatus())) {
								reconsileInvoices.setStatus(invoice.getGstr2bMatchingStatus());
							}
							if(isNotEmpty(invoice.getInvtype())) {
								reconsileInvoices.setPurchaseInvtype(invoice.getInvtype());
							}
							if(isNotEmpty(invoice.getInvoiceno())) {
								reconsileInvoices.setPurchaseInvoiceno(invoice.getInvoiceno());
							}
							if(isNotEmpty(invoice.getDateofinvoice())) {
								reconsileInvoices.setPurchaseInvoicedate(sdf.format(invoice.getDateofinvoice()));
							}
							if (isNotEmpty(invoice.getBilledtoname())) {
								reconsileInvoices.setPurchaseSuppliername(invoice.getBilledtoname());
							}
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
								if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
									reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
								}
							}else {
								if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
									reconsileInvoices.setPurchaseGstno(invoice.getB2b().get(0).getCtin());
								}
							}
							if(isNotEmpty(invoice.getTotaltaxableamount())) {
								reconsileInvoices.setPurchaseTotaltaxableamount(invoice.getTotaltaxableamount());
							}
							if(isNotEmpty(invoice.getTotaltax())) {
								reconsileInvoices.setPurchaseTotalamount(invoice.getTotaltax());
							}
							if(isNotEmpty(invoice.getTotalIgstAmount())) {
								reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
							}
							if(isNotEmpty(invoice.getTotalCgstAmount())) {
								reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
							}
							if(isNotEmpty(invoice.getTotalSgstAmount())) {
								reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
							}
							if(isNotEmpty(invoice.getTotalCessAmount())) {
								reconsileInvoices.setPurchaseTotalCessAmount(invoice.getTotalCessAmount());
							}
							if(isNotEmpty(invoice.getBranch())) {
								reconsileInvoices.setPurchaseBranch(invoice.getBranch());					
							}
							if(isNotEmpty(invoice.getRevchargetype())) {
								reconsileInvoices.setPurchaseReverseCharge(invoice.getRevchargetype());
							}
							if(isNotEmpty(invoice.getFp())) {
								reconsileInvoices.setPurchaseReturnPeriod(invoice.getFp());
							}
							if(isNotEmpty(invoice.getBillDate())) {
								reconsileInvoices.setPurchaseTransactionDate(sdf.format(invoice.getBillDate()));
							}
							if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
								reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
							}
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
								if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
									  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setPurchaseState(gstcode.getName()); 
									  }
								}
							}else {
							  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
								  StateConfig gstcode =  null;
								  if(Integer.parseInt(gstno) < 10) {
									  String tagName = gstno;
									  Query query = new Query();
									  query.limit(37);
									  query.addCriteria(Criteria.where("name").regex(tagName));
									  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
									  if(isNotEmpty(state)) {
										gstcode = state.get(0);  
									  }
								  }else {
									  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
								  }
								  if(isNotEmpty(gstcode)) {
									  reconsileInvoices.setPurchaseState(gstcode.getName()); 
								  } 
							  }
							}
							if(isNotEmpty(invoice.getCustomField1())) {
								reconsileInvoices.setPurchasecustomField1(invoice.getCustomField1());
							}
							if(isNotEmpty(invoice.getCustomField2())) {
								reconsileInvoices.setPurchasecustomField2(invoice.getCustomField2());
							}
							if(isNotEmpty(invoice.getCustomField3())) {
								reconsileInvoices.setPurchasecustomField3(invoice.getCustomField3());
							}
							if(isNotEmpty(invoice.getCustomField4())) {
								reconsileInvoices.setPurchasecustomField4(invoice.getCustomField4());
							}
							if(isNotEmpty(gstr2binv.getInvtype())) {
								reconsileInvoices.setGstr2Invtype(gstr2binv.getInvtype());
							}
							if(isNotEmpty(gstr2binv.getInvoiceno())) {
								reconsileInvoices.setGstr2Invoiceno(gstr2binv.getInvoiceno());
							}
							if(isNotEmpty(gstr2binv.getDateofinvoice())) {
								reconsileInvoices.setGstr2Invoicedate(sdf.format(gstr2binv.getDateofinvoice()));
							}
							if(isNotEmpty(gstr2binv.getTotaltaxableamount())) {
								reconsileInvoices.setGstr2Totaltaxableamount(gstr2binv.getTotaltaxableamount());
							}
							if(isNotEmpty(gstr2binv.getTotaltax())) {
								reconsileInvoices.setGstr2Totalamount(gstr2binv.getTotaltax());
							}
							if(isNotEmpty(gstr2binv.getTotalIgstAmount())) {
								reconsileInvoices.setGstr2TotalIgstAmount(gstr2binv.getTotalIgstAmount());
							}
							if(isNotEmpty(gstr2binv.getTotalCgstAmount())) {
								reconsileInvoices.setGstr2TotalCgstAmount(gstr2binv.getTotalCgstAmount());
							}
							if(isNotEmpty(gstr2binv.getTotalSgstAmount())) {
								reconsileInvoices.setGstr2TotalSgstAmount(gstr2binv.getTotalSgstAmount());
							}
							if(isNotEmpty(gstr2binv.getTotalCessAmount())) {
								reconsileInvoices.setGstr2TotalCessAmount(gstr2binv.getTotalCessAmount());
							}
							if(isNotEmpty(gstr2binv.getGstr2bMatchingStatus())) {
								reconsileInvoices.setGstr2status(gstr2binv.getGstr2bMatchingStatus());
							}
							if(isNotEmpty(gstr2binv.getBranch())) {
								reconsileInvoices.setGstr2Branch(gstr2binv.getBranch());
							}
							if (isNotEmpty(gstr2binv.getBilledtoname())) {
								reconsileInvoices.setGstr2Suppliername(gstr2binv.getBilledtoname());
							}
							if(gstr2binv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
								if (isNotEmpty(((GSTR2BSupport)gstr2binv).getImpGoods()) && isNotEmpty(((GSTR2BSupport)gstr2binv).getImpGoods().get(0)) && isNotEmpty(((GSTR2BSupport)gstr2binv).getImpGoods().get(0).getStin())) {
									reconsileInvoices.setGstr2Gstno(((GSTR2BSupport)gstr2binv).getImpGoods().get(0).getStin());
								}
							}else {
								if (isNotEmpty(gstr2binv.getB2b()) && isNotEmpty(gstr2binv.getB2b().get(0).getCtin())) {
									reconsileInvoices.setGstr2Gstno(gstr2binv.getB2b().get(0).getCtin());
								}
							}
							if(isNotEmpty(gstr2binv.getRevchargetype())) {
								reconsileInvoices.setGstr2ReverseCharge(gstr2binv.getRevchargetype());
							}
							if(isNotEmpty(gstr2binv.getFp())) {
								reconsileInvoices.setGstr2ReturnPeriod(gstr2binv.getFp());
							}
							if(isNotEmpty(gstr2binv.getBillDate())) {
								reconsileInvoices.setGstr2TransactionDate(sdf.format(gstr2binv.getBillDate()));
							}
							if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
								reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
							}
							if(isNotEmpty(gstr2binv.getCustomField1())) {
								reconsileInvoices.setGstr2customField1(gstr2binv.getCustomField1());
							  }
							  if(isNotEmpty(gstr2binv.getCustomField2())) {
								reconsileInvoices.setGstr2customField2(gstr2binv.getCustomField2());
							  }
							  if(isNotEmpty(gstr2binv.getCustomField3())) {
								reconsileInvoices.setGstr2customField3(gstr2binv.getCustomField3());
							  }
							  if(isNotEmpty(gstr2binv.getCustomField4())) {
								reconsileInvoices.setGstr2customField4(gstr2binv.getCustomField4());
							  }
							  if(gstr2binv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((GSTR2BSupport)gstr2binv).getImpGoods()) && isNotEmpty(((GSTR2BSupport)gstr2binv).getImpGoods().get(0)) && isNotEmpty(((GSTR2BSupport)gstr2binv).getImpGoods().get(0).getStin())) {
										  String gstno = ((GSTR2BSupport)gstr2binv).getImpGoods().get(0).getStin().trim().substring(0, 2); 
										  StateConfig gstcode =  null;
										  if(Integer.parseInt(gstno) < 10) {
											  String tagName = gstno;

											  Query query = new Query();
											  query.limit(37);
											  query.addCriteria(Criteria.where("name").regex(tagName));
											  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
											  if(isNotEmpty(state)) {
												gstcode = state.get(0);  
											  }
											  
										  }else {
											  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
										  }
										   
										  if(isNotEmpty(gstcode)) {
											  reconsileInvoices.setGstr2State(gstcode.getName()); 
										  }
									}
								}else {
							  if(isNotEmpty(gstr2binv.getB2b()) && isNotEmpty(gstr2binv.getB2b().get(0)) && isNotEmpty(gstr2binv.getB2b().get(0).getCtin())) {
								  String gstno = gstr2binv.getB2b().get(0).getCtin().trim().substring(0, 2); 
								  StateConfig gstcode =  null;
								  if(Integer.parseInt(gstno) < 10) {
									  String tagName = gstno;
									  Query query = new Query();
									  query.limit(37);
									  query.addCriteria(Criteria.where("name").regex(tagName));
									  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
									  if(isNotEmpty(state)) {
										gstcode = state.get(0);  
									  }
								  }else {
									  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
								  }
								  if(isNotEmpty(gstcode)) {
									  reconsileInvoices.setGstr2State(gstcode.getName()); 
								  } 
							  }
							}
							reconsileInvoicesLst.add(reconsileInvoices);
						}else {
							ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
							if(isNotEmpty(invoice.getGstr2bMatchingStatus())) {
								reconsileInvoices.setPurchaseStatus(invoice.getGstr2bMatchingStatus());
							}
							if(isNotEmpty(invoice.getGstr2bMatchingStatus())) {
								reconsileInvoices.setStatus(invoice.getGstr2bMatchingStatus());
							}
							if(isNotEmpty(invoice.getInvtype())) {
								reconsileInvoices.setPurchaseInvtype(invoice.getInvtype());
							}
							if(isNotEmpty(invoice.getInvoiceno())) {
								reconsileInvoices.setPurchaseInvoiceno(invoice.getInvoiceno());
							}
							if(isNotEmpty(invoice.getDateofinvoice())) {
								reconsileInvoices.setPurchaseInvoicedate(sdf.format(invoice.getDateofinvoice()));
							}
							if (isNotEmpty(invoice.getBilledtoname())) {
								reconsileInvoices.setPurchaseSuppliername(invoice.getBilledtoname());
							}
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
								if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
									reconsileInvoices.setPurchaseGstno(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
								}
							}else {
								if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
									reconsileInvoices.setPurchaseGstno(invoice.getB2b().get(0).getCtin());
								}
							}
							if(isNotEmpty(invoice.getTotaltaxableamount())) {
								reconsileInvoices.setPurchaseTotaltaxableamount(invoice.getTotaltaxableamount());
							}
							if(isNotEmpty(invoice.getTotaltax())) {
								reconsileInvoices.setPurchaseTotalamount(invoice.getTotaltax());
							}
							if(isNotEmpty(invoice.getTotalIgstAmount())) {
								reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
							}
							if(isNotEmpty(invoice.getTotalCgstAmount())) {
								reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
							}
							if(isNotEmpty(invoice.getTotalSgstAmount())) {
								reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
							}
							if(isNotEmpty(invoice.getTotalCessAmount())) {
								reconsileInvoices.setPurchaseTotalCessAmount(invoice.getTotalCessAmount());
							}
							if(isNotEmpty(invoice.getBranch())) {
								reconsileInvoices.setPurchaseBranch(invoice.getBranch());					
							}
							if(isNotEmpty(invoice.getRevchargetype())) {
								reconsileInvoices.setPurchaseReverseCharge(invoice.getRevchargetype());
							}
							if(isNotEmpty(invoice.getFp())) {
								reconsileInvoices.setPurchaseReturnPeriod(invoice.getFp());
							}
							if(isNotEmpty(invoice.getBillDate())) {
								reconsileInvoices.setPurchaseTransactionDate(sdf.format(invoice.getBillDate()));
							}
							if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
								reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
							}
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
								if (isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
									  String gstno = ((PurchaseRegister)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setPurchaseState(gstcode.getName()); 
									  }
								}
							}else {
							  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
								  StateConfig gstcode =  null;
								  if(Integer.parseInt(gstno) < 10) {
									  String tagName = gstno;
									  Query query = new Query();
									  query.limit(37);
									  query.addCriteria(Criteria.where("name").regex(tagName));
									  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
									  if(isNotEmpty(state)) {
										gstcode = state.get(0);  
									  }
								  }else {
									  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
								  }
								  if(isNotEmpty(gstcode)) {
									  reconsileInvoices.setPurchaseState(gstcode.getName()); 
								  } 
							  }
							}
							if(isNotEmpty(invoice.getCustomField1())) {
								reconsileInvoices.setPurchasecustomField1(invoice.getCustomField1());
							}
							if(isNotEmpty(invoice.getCustomField2())) {
								reconsileInvoices.setPurchasecustomField2(invoice.getCustomField2());
							}
							if(isNotEmpty(invoice.getCustomField3())) {
								reconsileInvoices.setPurchasecustomField3(invoice.getCustomField3());
							}
							if(isNotEmpty(invoice.getCustomField4())) {
								reconsileInvoices.setPurchasecustomField4(invoice.getCustomField4());
							}
							reconsileInvoices.setStatus("Not in GSTR2B");
							reconsileInvoices.setGstr2Invtype("-");
							reconsileInvoices.setGstr2Invoiceno("-");
							reconsileInvoices.setGstr2Invoicedate("-");
							reconsileInvoices.setGstr2Branch("-");
							reconsileInvoices.setGstr2Suppliername("-");
							reconsileInvoices.setGstr2Gstno("-");
							reconsileInvoices.setGstr2CompanyStateName("-");
							reconsileInvoices.setGstr2State("-");
							reconsileInvoices.setGstr2ReverseCharge("-");
							reconsileInvoices.setGstr2ReturnPeriod("-");
							reconsileInvoices.setGstr2TransactionDate("-");
							reconsileInvoices.setGstr2customField1("-");
							reconsileInvoices.setGstr2customField2("-");
							reconsileInvoices.setGstr2customField3("-");
							reconsileInvoices.setGstr2customField4("-");
							reconsileInvoicesLst.add(reconsileInvoices);
						}
					}
				}
			}
		}
		
		if(isNotEmpty(gstr2_manuallymappedids)) {
			for(String invoiceid:gstr2_manuallymappedids) {
				InvoiceParent invoice=gstr2aMannualMatchingStatusMap.get(invoiceid);
				if(isNotEmpty(invoice)) {
						List<? extends InvoiceParent> purchaseInvLst= purchaseMannualMatchingStatus_MultipleMap.get(invoice.getId().toString());
						if(isNotEmpty(purchaseInvLst)) {
							for(InvoiceParent prInv:purchaseInvLst) {
								
								ReconsileInvoices reconsileInvoices=new ReconsileInvoices();
								
								if(isNotEmpty(prInv.getGstr2bMatchingStatus())) {
									reconsileInvoices.setPurchaseStatus(prInv.getGstr2bMatchingStatus());
								}
								if(isNotEmpty(prInv.getGstr2bMatchingStatus())) {
									reconsileInvoices.setStatus(prInv.getGstr2bMatchingStatus());
								}
								if(isNotEmpty(prInv.getInvtype())) {
									reconsileInvoices.setPurchaseInvtype(prInv.getInvtype());
								}
								if(isNotEmpty(prInv.getInvoiceno())) {
									reconsileInvoices.setPurchaseInvoiceno(prInv.getInvoiceno());
								}
								if(isNotEmpty(prInv.getDateofinvoice())) {
									reconsileInvoices.setPurchaseInvoicedate(sdf.format(prInv.getDateofinvoice()));
								}
								
								if (isNotEmpty(prInv.getBilledtoname())) {
									reconsileInvoices.setPurchaseSuppliername(prInv.getBilledtoname());
								}
								if(prInv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((PurchaseRegister)prInv).getImpGoods()) && isNotEmpty(((PurchaseRegister)prInv).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)prInv).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setPurchaseGstno(((PurchaseRegister)prInv).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(prInv.getB2b()) && isNotEmpty(prInv.getB2b().get(0).getCtin())) {
										reconsileInvoices.setPurchaseGstno(prInv.getB2b().get(0).getCtin());
									}
								}
								if(isNotEmpty(prInv.getBranch())) {
									reconsileInvoices.setPurchaseBranch(prInv.getBranch());					
								}
								if(isNotEmpty(prInv.getTotaltaxableamount())) {
									reconsileInvoices.setPurchaseTotaltaxableamount(prInv.getTotaltaxableamount());
								}
								if(isNotEmpty(prInv.getTotaltax())) {
									reconsileInvoices.setPurchaseTotalamount(prInv.getTotaltax());
								}
								if(isNotEmpty(invoice.getTotalIgstAmount())) {
									reconsileInvoices.setPurchaseTotalIgstAmount(invoice.getTotalIgstAmount());
								}
								if(isNotEmpty(invoice.getTotalCgstAmount())) {
									reconsileInvoices.setPurchaseTotalCgstAmount(invoice.getTotalCgstAmount());
								}
								if(isNotEmpty(invoice.getTotalSgstAmount())) {
									reconsileInvoices.setPurchaseTotalSgstAmount(invoice.getTotalSgstAmount());
								}
								if(isNotEmpty(invoice.getTotalCessAmount())) {
									reconsileInvoices.setPurchaseTotalCessAmount(invoice.getTotalCessAmount());
								}
								if(isNotEmpty(prInv.getRevchargetype())) {
									reconsileInvoices.setPurchaseReverseCharge(prInv.getRevchargetype());
								}
								if(isNotEmpty(prInv.getFp())) {
									reconsileInvoices.setPurchaseReturnPeriod(prInv.getFp());
								}
								
								if(isNotEmpty(prInv.getBillDate())) {
									reconsileInvoices.setPurchaseTransactionDate(sdf.format(prInv.getBillDate()));
								}
								
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setPurchaseCompanyStateName(client.getStatename());
								}
								if(prInv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((PurchaseRegister)prInv).getImpGoods()) && isNotEmpty(((PurchaseRegister)prInv).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)prInv).getImpGoods().get(0).getStin())) {
										  String gstno = ((PurchaseRegister)prInv).getImpGoods().get(0).getStin().trim().substring(0, 2); 
										  StateConfig gstcode =  null;
										  if(Integer.parseInt(gstno) < 10) {
											  String tagName = gstno;

											  Query query = new Query();
											  query.limit(37);
											  query.addCriteria(Criteria.where("name").regex(tagName));
											  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
											  if(isNotEmpty(state)) {
												gstcode = state.get(0);  
											  }
											  
										  }else {
											  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
										  }
										   
										  if(isNotEmpty(gstcode)) {
											  reconsileInvoices.setPurchaseState(gstcode.getName()); 
										  }
									}
								}else {
								  if(isNotEmpty(prInv.getB2b()) && isNotEmpty(prInv.getB2b().get(0)) && isNotEmpty(prInv.getB2b().get(0).getCtin())) {
									  String gstno = prInv.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setPurchaseState(gstcode.getName()); 
									  } 
								  }
								}
								if(isNotEmpty(prInv.getCustomField1())) {
									reconsileInvoices.setPurchasecustomField1(prInv.getCustomField1());
								}
								if(isNotEmpty(prInv.getCustomField2())) {
									reconsileInvoices.setPurchasecustomField2(prInv.getCustomField2());
								}
								if(isNotEmpty(prInv.getCustomField3())) {
									reconsileInvoices.setPurchasecustomField3(prInv.getCustomField3());
								}
								if(isNotEmpty(prInv.getCustomField4())) {
									reconsileInvoices.setPurchasecustomField4(prInv.getCustomField4());
								}
								
								if(isNotEmpty(invoice.getInvtype())) {
									reconsileInvoices.setGstr2Invtype(invoice.getInvtype());
								}
								if(isNotEmpty(invoice.getInvoiceno())) {
									reconsileInvoices.setGstr2Invoiceno(invoice.getInvoiceno());
								}
								if(isNotEmpty(invoice.getDateofinvoice())) {
									reconsileInvoices.setGstr2Invoicedate(sdf.format(invoice.getDateofinvoice()));
								}
								if(isNotEmpty(invoice.getTotaltaxableamount())) {
									reconsileInvoices.setGstr2Totaltaxableamount(invoice.getTotaltaxableamount());
								}
								if(isNotEmpty(invoice.getTotaltax())) {
									reconsileInvoices.setGstr2Totalamount(invoice.getTotaltax());
								}
								if(isNotEmpty(invoice.getTotalIgstAmount())) {
									reconsileInvoices.setGstr2TotalIgstAmount(invoice.getTotalIgstAmount());
								}
								if(isNotEmpty(invoice.getTotalCgstAmount())) {
									reconsileInvoices.setGstr2TotalCgstAmount(invoice.getTotalCgstAmount());
								}
								if(isNotEmpty(invoice.getTotalSgstAmount())) {
									reconsileInvoices.setGstr2TotalSgstAmount(invoice.getTotalSgstAmount());
								}
								if(isNotEmpty(invoice.getTotalCessAmount())) {
									reconsileInvoices.setGstr2TotalCessAmount(invoice.getTotalCessAmount());
								}
								if(isNotEmpty(invoice.getGstr2bMatchingStatus())) {
									reconsileInvoices.setGstr2status(invoice.getGstr2bMatchingStatus());
								}
								if(isNotEmpty(invoice.getBranch())) {
									reconsileInvoices.setGstr2Branch(invoice.getBranch());
								}
								if (isNotEmpty(invoice.getBilledtoname())) {
									reconsileInvoices.setGstr2Suppliername(invoice.getBilledtoname());
								}
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
									if (isNotEmpty(((GSTR2BSupport)invoice).getImpGoods()) && isNotEmpty(((GSTR2BSupport)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2BSupport)invoice).getImpGoods().get(0).getStin())) {
										reconsileInvoices.setGstr2Gstno(((GSTR2BSupport)invoice).getImpGoods().get(0).getStin());
									}
								}else {
									if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
										reconsileInvoices.setGstr2Gstno(invoice.getB2b().get(0).getCtin());
									}
								}
								if(isNotEmpty(invoice.getRevchargetype())) {
									reconsileInvoices.setGstr2ReverseCharge(invoice.getRevchargetype());
								}
								if(isNotEmpty(invoice.getFp())) {
									reconsileInvoices.setGstr2ReturnPeriod(invoice.getFp());
								}
								
								if(isNotEmpty(invoice.getBillDate())) {
									reconsileInvoices.setGstr2TransactionDate(sdf.format(invoice.getBillDate()));
								}
								
								if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
									reconsileInvoices.setGstr2CompanyStateName(client.getStatename());
								}
								if(isNotEmpty(invoice.getCustomField1())) {
									reconsileInvoices.setGstr2customField1(invoice.getCustomField1());
								  }
								  if(isNotEmpty(invoice.getCustomField2())) {
									reconsileInvoices.setGstr2customField2(invoice.getCustomField2());
								  }
								  if(isNotEmpty(invoice.getCustomField3())) {
									reconsileInvoices.setGstr2customField3(invoice.getCustomField3());
								  }
								  if(isNotEmpty(invoice.getCustomField4())) {
									reconsileInvoices.setGstr2customField4(invoice.getCustomField4());
								  }
								  if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
										if (isNotEmpty(((GSTR2BSupport)invoice).getImpGoods()) && isNotEmpty(((GSTR2BSupport)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2BSupport)invoice).getImpGoods().get(0).getStin())) {
											  String gstno = ((GSTR2BSupport)invoice).getImpGoods().get(0).getStin().trim().substring(0, 2); 
											  StateConfig gstcode =  null;
											  if(Integer.parseInt(gstno) < 10) {
												  String tagName = gstno;

												  Query query = new Query();
												  query.limit(37);
												  query.addCriteria(Criteria.where("name").regex(tagName));
												  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
												  if(isNotEmpty(state)) {
													gstcode = state.get(0);  
												  }
												  
											  }else {
												  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
											  }
											   
											  if(isNotEmpty(gstcode)) {
												  reconsileInvoices.setGstr2State(gstcode.getName()); 
											  }
										}
									}else {
								  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
									  String gstno = invoice.getB2b().get(0).getCtin().trim().substring(0, 2); 
									  StateConfig gstcode =  null;
									  if(Integer.parseInt(gstno) < 10) {
										  String tagName = gstno;

										  Query query = new Query();
										  query.limit(37);
										  query.addCriteria(Criteria.where("name").regex(tagName));
										  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
										  if(isNotEmpty(state)) {
											gstcode = state.get(0);  
										  }
										  
									  }else {
										  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
									  }
									   
									  if(isNotEmpty(gstcode)) {
										  reconsileInvoices.setGstr2State(gstcode.getName()); 
									  } 
								  }
								}

								reconsileInvoicesLst.add(reconsileInvoices);
							}	
						}
				}
			}
		}
		return reconsileInvoicesLst;
	}
	
	
	public Map<String, List<String>> findReconsilationids(Page<? extends InvoiceParent> prMatched,Page<? extends InvoiceParent> g2bMatch){
		List<String> g2bids = Lists.newArrayList();
		List<String> prids = Lists.newArrayList();
		List<String> g2bbids = Lists.newArrayList();
		List<String> prrids = Lists.newArrayList();
		Map<String, List<String>> idsMap = Maps.newHashMap();
		if(isNotEmpty(prMatched) && isNotEmpty(prMatched.getContent())) {
			for(InvoiceParent inv : prMatched.getContent()) {
				g2bids.addAll(inv.getGstr2bMatchingId());
				prrids.add(inv.getId().toString());
			}
		}
		idsMap.put("2bIds" , g2bids);
		idsMap.put("prIds" , prids);
		return idsMap;		
	}
	
	@RequestMapping(value = "/dwnldReconsilexls/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public void getReconsileMonthlyInvoices(@PathVariable String id, @PathVariable String clientid, @PathVariable String returntype,@PathVariable int month,@PathVariable int year,@RequestParam("dwnldtype") String dwnldtype, HttpServletResponse response, HttpServletRequest request) throws Exception {
		final String method = "getReconsileMonthlyInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "userid\t" + id);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);
		
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String clientname = "";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			clientname = client.getBusinessname().replace("/", "");
		}
		
		String strMonth = month < 10 ? "0" + month : month + "";
		String fp = strMonth+year;
		
		Date mstDate = null;
		Date mendDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 0, 23, 59, 59);
		mstDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 23, 59, 59);
		mendDate = new java.util.Date(cal.getTimeInMillis());
		List<String> rtArray=Arrays.asList(fp);
		
		List<ReconsileInvoices> reconsileInvoicesLst=getReconsileExcelData("Monthly",clientid, month, year, mstDate, mendDate, rtArray);
		
		TextColumnBuilder<String> prInvTypeColumn = col.column("Invoice Type", "purchaseInvtype", type.stringType());
		TextColumnBuilder<String> prSuppliernameColumn = col.column("Supplier Name", "purchaseSuppliername", type.stringType());
		TextColumnBuilder<String> prInvoicenoColumn = col.column("Invoice Number", "purchaseInvoiceno", type.stringType());
		TextColumnBuilder<String> prInvoicedateColumn = col.column("Invoice Date", "purchaseInvoicedate", type.stringType());
		TextColumnBuilder<String> prGstnoColumn = col.column("GSTN No", "purchaseGstno", type.stringType());
		TextColumnBuilder<Double> prTotaltaxableamountColumn = col.column("Taxable Amount", "purchaseTotaltaxableamount", type.doubleType());
		TextColumnBuilder<Double> prTotalamountColumn = col.column("Total Tax", "purchaseTotalamount", type.doubleType());
		TextColumnBuilder<Double> prTotalIgstamountColumn = col.column("Total IGST", "purchaseTotalIgstAmount", type.doubleType());
		TextColumnBuilder<Double> prTotalCgstamountColumn = col.column("Total CGST", "purchaseTotalCgstAmount", type.doubleType());
		TextColumnBuilder<Double> prTotalSgstamountColumn = col.column("Total SGST", "purchaseTotalSgstAmount", type.doubleType());
		TextColumnBuilder<Double> prTotalCessamountColumn = col.column("Total CESS", "purchaseTotalCessAmount", type.doubleType());
		TextColumnBuilder<String> prBranchColumn = col.column("Branch", "purchaseBranch", type.stringType());
		
		
		TextColumnBuilder<String> statusColumn = col.column("Status", "status", type.stringType());
		
		TextColumnBuilder<String> gstr2InvTypeColumn = col.column("Invoice Type", "gstr2Invtype", type.stringType());
		TextColumnBuilder<String> gstr2SuppliernameColumn = col.column("Supplier Name", "gstr2Suppliername", type.stringType());
		TextColumnBuilder<String> gstr2InvoicenoColumn = col.column("Invoice Number", "gstr2Invoiceno", type.stringType());
		TextColumnBuilder<String> gstr2InvoicedateColumn = col.column("Invoice Date", "gstr2Invoicedate", type.stringType());
		TextColumnBuilder<String> gstr2GstnoColumn = col.column("GSTN No", "gstr2Gstno", type.stringType());
		TextColumnBuilder<Double> gstr2TotaltaxableamountColumn = col.column("Taxable Amount", "gstr2Totaltaxableamount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalamountColumn = col.column("Total Tax", "gstr2Totalamount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalIgstamountColumn = col.column("Total IGST", "gstr2TotalIgstAmount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalCgstamountColumn = col.column("Total CGST", "gstr2TotalCgstAmount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalSgstamountColumn = col.column("Total SGST", "gstr2TotalSgstAmount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalCessamountColumn = col.column("Total CESS", "gstr2TotalCessAmount", type.doubleType());
		TextColumnBuilder<String> gstr2BranchColumn = col.column("Branch", "gstr2Branch", type.stringType());

		HorizontalListBuilder left = cmp.horizontalList().add(cmp.text("Books (Purchase)"));
		HorizontalListBuilder right = cmp.horizontalList().add(cmp.text("GSTR2A"));
		HorizontalListBuilder title = cmp.horizontalList().add(left, right);
		if(dwnldtype.equalsIgnoreCase("alldetailswise")) {
			String customField1 = "CustomField1";
			String customField2 = "CustomField2";
			String customField3 = "CustomField3";
			String customField4 = "CustomField4";
			if(NullUtil.isNotEmpty(client)){
				gstnumber = client.getGstnnumber();
				CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
				if(isNotEmpty(customFields)) {
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
				}
			}
			TextColumnBuilder<String> prFpColumn = col.column("Return Period", "purchaseReturnPeriod", type.stringType());
			TextColumnBuilder<String> prRcColumn = col.column("Reverse Charge", "purchaseReverseCharge", type.stringType());
			TextColumnBuilder<String> prTdColumn = col.column("Transaction Date", "purchaseTransactionDate", type.stringType());
			TextColumnBuilder<String> prCSNColumn = col.column("Company State Name", "purchaseCompanyStateName", type.stringType());
			TextColumnBuilder<String> prStateColumn = col.column("State", "purchaseState", type.stringType());
			TextColumnBuilder<String> prCustomField1Column = col.column(customField1, "purchasecustomField1", type.stringType());
			TextColumnBuilder<String> prCustomField2Column = col.column(customField2, "purchasecustomField2", type.stringType());
			TextColumnBuilder<String> prCustomField3Column = col.column(customField3, "purchasecustomField3", type.stringType());
			TextColumnBuilder<String> prCustomField4Column = col.column(customField4, "purchasecustomField4", type.stringType());
			TextColumnBuilder<String> gstr2FpColumn = col.column("Return Period", "gstr2ReturnPeriod", type.stringType());
			TextColumnBuilder<String> gstr2RcColumn = col.column("Reverse Charge", "gstr2ReverseCharge", type.stringType());
			TextColumnBuilder<String> gstr2TdColumn = col.column("Transaction Date", "gstr2TransactionDate", type.stringType());
			TextColumnBuilder<String> gstr2CSNColumn = col.column("Company State Name", "gstr2CompanyStateName", type.stringType());
			TextColumnBuilder<String> gstr2StateColumn = col.column("State", "gstr2State", type.stringType());
			TextColumnBuilder<String> gstr2CustomField1Column = col.column(customField1, "gstr2customField1", type.stringType());
			TextColumnBuilder<String> gstr2CustomField2Column = col.column(customField2, "gstr2customField2", type.stringType());
			TextColumnBuilder<String> gstr2CustomField3Column = col.column(customField3, "gstr2customField3", type.stringType());
			TextColumnBuilder<String> gstr2CustomField4Column = col.column(customField4, "gstr2customField4", type.stringType());
			
			//try {
				if(reconsileInvoicesLst.size() < 10000) {
					response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
					String fileName = "MGST_"+returntype+"_"+gstnumber+"_"+month+year+".xls";
					response.setHeader("Content-Disposition", "attachment; filename="+fileName);
					File file = new File(fileName);
					FileInputStream in = null;
					OutputStream out = null;
					
					try {
						file.createNewFile();
						FileOutputStream fos = new FileOutputStream(file);
						
						JasperXlsExporterBuilder xlsExporter = export.xlsExporter(fos)
								.addSheetName("MasterGST")
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
									prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
									prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,prTotalSgstamountColumn,prTotalCgstamountColumn,prTotalCessamountColumn, prBranchColumn,
									prFpColumn,prRcColumn,prTdColumn,prCSNColumn,prStateColumn,prCustomField1Column,
									prCustomField2Column,prCustomField3Column,prCustomField4Column,
									statusColumn, 
									gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
									gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn,gstr2TotalCessamountColumn, gstr2BranchColumn,
									gstr2FpColumn,gstr2RcColumn,gstr2TdColumn,gstr2CSNColumn,gstr2StateColumn,
									gstr2CustomField1Column,gstr2CustomField2Column,gstr2CustomField3Column,gstr2CustomField4Column
								)
								.setDataSource(reconsileInvoicesLst)
								.toXls(xlsExporter);
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
						double i = ((double)reconsileInvoicesLst.size())/60000;
						int j = (int)i;
						if(i-(int)i > 0) {
							j = (int)i+1;
						}
						List<List<ReconsileInvoices>> lt = Lists.newArrayList();
						int a=0;
						int b = 60000;
						if(reconsileInvoicesLst.size() < 60000) {
							b= reconsileInvoicesLst.size();
						}
						for(int k = 1; k <= j;k++) {
							lt.add(reconsileInvoicesLst.subList(a, b));
							a = b;
							if(k == j-1) {
								b = reconsileInvoicesLst.size();
							}else {
								b = b+60000;
							}
						}
						for(List<ReconsileInvoices> InvoicesList: lt) {
							File file1 = new File("MGST_"+returntype+"_"+gstnumber+"_"+month+year+"_"+(c+1)+".xls");
							file1.createNewFile();
							FileOutputStream fos = new FileOutputStream(file1);
							
							JasperXlsExporterBuilder xlsExporter = export.xlsExporter(fos)
								.addSheetName("MasterGST")
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
									prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
									prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,prTotalSgstamountColumn,prTotalCgstamountColumn,prTotalCessamountColumn, prBranchColumn,
									prFpColumn,prRcColumn,prTdColumn,prCSNColumn,prStateColumn,prCustomField1Column,
									prCustomField2Column,prCustomField3Column,prCustomField4Column,
									statusColumn, 
									gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
									gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn,gstr2TotalCessamountColumn, gstr2BranchColumn,
									gstr2FpColumn,gstr2RcColumn,gstr2TdColumn,gstr2CSNColumn,gstr2StateColumn,
									gstr2CustomField1Column,gstr2CustomField2Column,gstr2CustomField3Column,gstr2CustomField4Column
								)
								.setDataSource(InvoicesList)
								.toXls(xlsExporter);
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
		}else {
			
			if(reconsileInvoicesLst.size() < 10000) {
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				String fileName = "MGST_"+returntype+"_"+gstnumber+"_"+month+year+".xls";
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);
				File file = new File(fileName);
				FileInputStream in = null;
				OutputStream out = null;
				
				try {
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					
					JasperXlsExporterBuilder xlsExporter = export.xlsExporter(fos)
							.addSheetName("MasterGST")
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
									prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
									prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,
									prTotalSgstamountColumn,prTotalCgstamountColumn,prTotalCessamountColumn, prBranchColumn,
									
									statusColumn, 
									gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
									gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,
									gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn,gstr2TotalCessamountColumn, gstr2BranchColumn
								)
							.setDataSource(reconsileInvoicesLst)
							.toXls(xlsExporter);
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
					double i = ((double)reconsileInvoicesLst.size())/60000;
					int j = (int)i;
					if(i-(int)i > 0) {
						j = (int)i+1;
					}
					List<List<ReconsileInvoices>> lt = Lists.newArrayList();
					int a=0;
					int b = 60000;
					if(reconsileInvoicesLst.size() < 60000) {
						b= reconsileInvoicesLst.size();
					}
					for(int k = 1; k <= j;k++) {
						lt.add(reconsileInvoicesLst.subList(a, b));
						a = b;
						if(k == j-1) {
							b = reconsileInvoicesLst.size();
						}else {
							b = b+60000;
						}
					}
					for(List<ReconsileInvoices> InvoicesList: lt) {
						File file1 = new File("MGST_"+returntype+"_"+gstnumber+"_"+month+year+"_"+(c+1)+".xls");
						file1.createNewFile();
						FileOutputStream fos = new FileOutputStream(file1);
						
						JasperXlsExporterBuilder xlsExporter = export.xlsExporter(fos)
							.addSheetName("MasterGST")
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
									prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
									prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,
									prTotalSgstamountColumn,prTotalCgstamountColumn,prTotalCessamountColumn, prBranchColumn,
									
									statusColumn, 
									gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
									gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,
									gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn,gstr2TotalCessamountColumn, gstr2BranchColumn
								)
							.setDataSource(InvoicesList)
							.toXls(xlsExporter);
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
	
	@RequestMapping(value = "/dwnldReconsilexls2b/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public void getReconsileMonthlyInvoicesGstr2b(@PathVariable String id, @PathVariable String clientid, @PathVariable String returntype, @PathVariable int month,@PathVariable int year,@RequestParam("dwnldtype") String dwnldtype,
			@RequestParam(defaultValue = MasterGSTConstants.MONTHLY, required = false) String isYearly, HttpServletResponse response, HttpServletRequest request) throws Exception {
		final String method = "getReconsileMonthlyInvoicesGstr2b::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "userid\t" + id);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);

		Boolean isMonthly = MasterGSTConstants.MONTHLY.equals(isYearly) ? true : false;
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String clientname = "";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			clientname = client.getBusinessname().replace("/", "");
		}
		//List<String> rtArray= Arrays.asList(fp);
		List<String> rtArray = null;
		Date mstDate = null;
		Date mendDate = null;
		if(isMonthly) {
			String strMonth = month < 10 ? "0" + month : month + "";
			String fp = strMonth + year;
			rtArray = Arrays.asList(fp);

			Calendar cal = Calendar.getInstance();
			cal.set(year, month - 1, 0, 23, 59, 59);
			mstDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(year, month, 0, 23, 59, 59);
			mendDate = new java.util.Date(cal.getTimeInMillis());
		}else {
			rtArray = Arrays.asList("03"+year, "04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "03"+(year+1), "03"+(year+1));
		}
		
		List<ReconsileInvoices> reconsileInvoicesLst=getReconsileExcelDatagstr2b(clientid, month, year, mstDate, mendDate, rtArray, isMonthly);
		
		TextColumnBuilder<String> prInvTypeColumn = col.column("Invoice Type", "purchaseInvtype", type.stringType());
		TextColumnBuilder<String> prSuppliernameColumn = col.column("Supplier Name", "purchaseSuppliername", type.stringType());
		TextColumnBuilder<String> prInvoicenoColumn = col.column("Invoice Number", "purchaseInvoiceno", type.stringType());
		TextColumnBuilder<String> prInvoicedateColumn = col.column("Invoice Date", "purchaseInvoicedate", type.stringType());
		TextColumnBuilder<String> prGstnoColumn = col.column("GSTN No", "purchaseGstno", type.stringType());
		TextColumnBuilder<Double> prTotaltaxableamountColumn = col.column("Taxable Amount", "purchaseTotaltaxableamount", type.doubleType());
		TextColumnBuilder<Double> prTotalamountColumn = col.column("Total Tax", "purchaseTotalamount", type.doubleType());
		TextColumnBuilder<Double> prTotalIgstamountColumn = col.column("Total IGST", "purchaseTotalIgstAmount", type.doubleType());
		TextColumnBuilder<Double> prTotalCgstamountColumn = col.column("Total CGST", "purchaseTotalCgstAmount", type.doubleType());
		TextColumnBuilder<Double> prTotalSgstamountColumn = col.column("Total SGST", "purchaseTotalSgstAmount", type.doubleType());
		TextColumnBuilder<Double> prTotalCessamountColumn = col.column("Total CESS", "purchaseTotalCessAmount", type.doubleType());
		TextColumnBuilder<String> prBranchColumn = col.column("Branch", "purchaseBranch", type.stringType());
		
		
		TextColumnBuilder<String> statusColumn = col.column("Status", "status", type.stringType());
		
		TextColumnBuilder<String> gstr2InvTypeColumn = col.column("Invoice Type", "gstr2Invtype", type.stringType());
		TextColumnBuilder<String> gstr2SuppliernameColumn = col.column("Supplier Name", "gstr2Suppliername", type.stringType());
		TextColumnBuilder<String> gstr2InvoicenoColumn = col.column("Invoice Number", "gstr2Invoiceno", type.stringType());
		TextColumnBuilder<String> gstr2InvoicedateColumn = col.column("Invoice Date", "gstr2Invoicedate", type.stringType());
		TextColumnBuilder<String> gstr2GstnoColumn = col.column("GSTN No", "gstr2Gstno", type.stringType());
		TextColumnBuilder<Double> gstr2TotaltaxableamountColumn = col.column("Taxable Amount", "gstr2Totaltaxableamount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalamountColumn = col.column("Total Tax", "gstr2Totalamount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalIgstamountColumn = col.column("Total IGST", "gstr2TotalIgstAmount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalCgstamountColumn = col.column("Total CGST", "gstr2TotalCgstAmount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalSgstamountColumn = col.column("Total SGST", "gstr2TotalSgstAmount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalCessamountColumn = col.column("Total CESS", "gstr2TotalCessAmount", type.doubleType());
		TextColumnBuilder<String> gstr2BranchColumn = col.column("Branch", "gstr2Branch", type.stringType());

		HorizontalListBuilder left = cmp.horizontalList().add(cmp.text("Books (Purchase)"));
		HorizontalListBuilder right = cmp.horizontalList().add(cmp.text("GSTR2B"));
		HorizontalListBuilder title = cmp.horizontalList().add(left, right);
		if(dwnldtype.equalsIgnoreCase("alldetailswise")) {
			String customField1 = "CustomField1";
			String customField2 = "CustomField2";
			String customField3 = "CustomField3";
			String customField4 = "CustomField4";
			if(NullUtil.isNotEmpty(client)){
				gstnumber = client.getGstnnumber();
				CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
				if(isNotEmpty(customFields)) {
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
				}
			}
			TextColumnBuilder<String> prFpColumn = col.column("Return Period", "purchaseReturnPeriod", type.stringType());
			TextColumnBuilder<String> prRcColumn = col.column("Reverse Charge", "purchaseReverseCharge", type.stringType());
			TextColumnBuilder<String> prTdColumn = col.column("Transaction Date", "purchaseTransactionDate", type.stringType());
			TextColumnBuilder<String> prCSNColumn = col.column("Company State Name", "purchaseCompanyStateName", type.stringType());
			TextColumnBuilder<String> prStateColumn = col.column("State", "purchaseState", type.stringType());
			TextColumnBuilder<String> prCustomField1Column = col.column(customField1, "purchasecustomField1", type.stringType());
			TextColumnBuilder<String> prCustomField2Column = col.column(customField2, "purchasecustomField2", type.stringType());
			TextColumnBuilder<String> prCustomField3Column = col.column(customField3, "purchasecustomField3", type.stringType());
			TextColumnBuilder<String> prCustomField4Column = col.column(customField4, "purchasecustomField4", type.stringType());
			TextColumnBuilder<String> gstr2FpColumn = col.column("Return Period", "gstr2ReturnPeriod", type.stringType());
			TextColumnBuilder<String> gstr2RcColumn = col.column("Reverse Charge", "gstr2ReverseCharge", type.stringType());
			TextColumnBuilder<String> gstr2TdColumn = col.column("Transaction Date", "gstr2TransactionDate", type.stringType());
			TextColumnBuilder<String> gstr2CSNColumn = col.column("Company State Name", "gstr2CompanyStateName", type.stringType());
			TextColumnBuilder<String> gstr2StateColumn = col.column("State", "gstr2State", type.stringType());
			TextColumnBuilder<String> gstr2CustomField1Column = col.column(customField1, "gstr2customField1", type.stringType());
			TextColumnBuilder<String> gstr2CustomField2Column = col.column(customField2, "gstr2customField2", type.stringType());
			TextColumnBuilder<String> gstr2CustomField3Column = col.column(customField3, "gstr2customField3", type.stringType());
			TextColumnBuilder<String> gstr2CustomField4Column = col.column(customField4, "gstr2customField4", type.stringType());
			
			if(reconsileInvoicesLst.size() < 10000) {
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				String fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+".xls";
				if(isMonthly) {
					fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+".xls";
				}else {
					fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+year+"_"+(year+1)+".xls";
				}
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);
				File file = new File(fileName);
				FileInputStream in = null;
				OutputStream out = null;
				
				try {
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					
					JasperXlsExporterBuilder xlsExporter = export.xlsExporter(fos)
							.addSheetName("MasterGST")
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
									prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
									prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,prTotalSgstamountColumn,prTotalCgstamountColumn,prTotalCessamountColumn, prBranchColumn,
									prFpColumn,prRcColumn,prTdColumn,prCSNColumn,prStateColumn,prCustomField1Column,
									prCustomField2Column,prCustomField3Column,prCustomField4Column,
									statusColumn, 
									gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
									gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn,gstr2TotalCessamountColumn, gstr2BranchColumn,
									gstr2FpColumn,gstr2RcColumn,gstr2TdColumn,gstr2CSNColumn,gstr2StateColumn,
									gstr2CustomField1Column,gstr2CustomField2Column,gstr2CustomField3Column,gstr2CustomField4Column
								)
							.setDataSource(reconsileInvoicesLst)
							.toXls(xlsExporter);
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
					String fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+".zip";
					if(isMonthly) {
						fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+".zip";
					}else {
						fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+year+"_"+(year+1)+".zip";
					}
					response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
					response.setContentType("application/octet-stream; charset=utf-8");
					zipOutputStream = new ZipOutputStream(nout);
					byte[] buf = new byte[1024];
					int c=0;
					int len = 0;
					double i = ((double)reconsileInvoicesLst.size())/60000;
					int j = (int)i;
					if(i-(int)i > 0) {
						j = (int)i+1;
					}
					List<List<ReconsileInvoices>> lt = Lists.newArrayList();
					int a=0;
					int b = 60000;
					if(reconsileInvoicesLst.size() < 60000) {
						b= reconsileInvoicesLst.size();
					}
					for(int k = 1; k <= j;k++) {
						lt.add(reconsileInvoicesLst.subList(a, b));
						a = b;
						if(k == j-1) {
							b = reconsileInvoicesLst.size();
						}else {
							b = b+60000;
						}
					}
					for(List<ReconsileInvoices> InvoicesList: lt) {
						File file1 = new File("MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+"_"+(c+1)+".xls");
						if(isMonthly) {
							file1 = new File("MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+"_"+(c+1)+".xls");
						}else {
							file1 = new File("MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+year+"_"+(year+1)+"_"+(c+1)+".xls");
						}
						file1.createNewFile();
						FileOutputStream fos = new FileOutputStream(file1);
						
						JasperXlsExporterBuilder xlsExporter = export.xlsExporter(fos)
							.addSheetName("MasterGST")
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
									prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
									prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,prTotalSgstamountColumn,prTotalCgstamountColumn,prTotalCessamountColumn, prBranchColumn,
									prFpColumn,prRcColumn,prTdColumn,prCSNColumn,prStateColumn,prCustomField1Column,
									prCustomField2Column,prCustomField3Column,prCustomField4Column,
									statusColumn, 
									gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
									gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn,gstr2TotalCessamountColumn, gstr2BranchColumn,
									gstr2FpColumn,gstr2RcColumn,gstr2TdColumn,gstr2CSNColumn,gstr2StateColumn,
									gstr2CustomField1Column,gstr2CustomField2Column,gstr2CustomField3Column,gstr2CustomField4Column
								)
							.setDataSource(InvoicesList)
							.toXls(xlsExporter);
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
	}else {
		
		if(reconsileInvoicesLst.size() < 10000) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			String fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+".xls";
			if(isMonthly) {
				fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+".xls";
			}else {
				fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+year+"_"+(year+1)+".xls";
			}
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			File file = new File(fileName);
			FileInputStream in = null;
			OutputStream out = null;
			
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				
				JasperXlsExporterBuilder xlsExporter = export.xlsExporter(fos)
						.addSheetName("MasterGST")
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
								prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
								prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,
								prTotalSgstamountColumn,prTotalCgstamountColumn,prTotalCessamountColumn ,prBranchColumn,
								
								statusColumn, 
								gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
								gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,
								gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn,gstr2TotalCessamountColumn, gstr2BranchColumn
							)
						.setDataSource(reconsileInvoicesLst)
						.toXls(xlsExporter);
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
				String fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+".zip";
				if(isMonthly) {
					fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+".zip";
				}else {
					fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+year+"_"+(year+1)+".zip";
				}
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
				response.setContentType("application/octet-stream; charset=utf-8");
				zipOutputStream = new ZipOutputStream(nout);
				byte[] buf = new byte[1024];
				int c=0;
				int len = 0;
				double i = ((double)reconsileInvoicesLst.size())/60000;
				int j = (int)i;
				if(i-(int)i > 0) {
					j = (int)i+1;
				}
				List<List<ReconsileInvoices>> lt = Lists.newArrayList();
				int a=0;
				int b = 60000;
				if(reconsileInvoicesLst.size() < 60000) {
					b= reconsileInvoicesLst.size();
				}
				for(int k = 1; k <= j;k++) {
					lt.add(reconsileInvoicesLst.subList(a, b));
					a = b;
					if(k == j-1) {
						b = reconsileInvoicesLst.size();
					}else {
						b = b+60000;
					}
				}
				for(List<ReconsileInvoices> InvoicesList: lt) {
					File file1 = new File("MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+"_"+(c+1)+".xls");
					if(isMonthly) {
						file1 = new File("MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+"_"+(c+1)+".xls");
					}else {
						file1 = new File("MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+year+"_"+(year+1)+"_"+(c+1)+".xls");
					}
					
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					
					JasperXlsExporterBuilder xlsExporter = export.xlsExporter(fos)
						.addSheetName("MasterGST")
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
								prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
								prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,
								prTotalSgstamountColumn,prTotalCgstamountColumn,prTotalCessamountColumn, prBranchColumn,
								
								statusColumn, 
								gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
								gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,
								gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn,gstr2TotalCessamountColumn, gstr2BranchColumn
							)
						.setDataSource(InvoicesList)
						.toXls(xlsExporter);
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
	
	
	@RequestMapping(value = "/downloadReconsileInvsExcel/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public void getReconsileFinancialYearInvoices(@PathVariable String id, @PathVariable String clientid,@PathVariable int month,@PathVariable int year, @RequestParam("dwnldtype") String dwnldtype,HttpServletResponse response, HttpServletRequest request) throws Exception {
		final String method = "getReconsileFinancialYearInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "userid\t" + id);
		logger.debug(CLASSNAME + method + "year\t" + year);
		
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		String clientname = "";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
			clientname = client.getBusinessname().replace("/", "");
		}
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "inline; filename='MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+year+".xls");
		
		
		Date stDate = null;
		Date endDate = null;
		Calendar cals = Calendar.getInstance();
		cals.set(year, 3, 1, 0, 0, 0);
		stDate = new java.util.Date(cals.getTimeInMillis());
		cals = Calendar.getInstance();
		cals.set(year+1, 3, 0, 23, 59, 59);
		endDate = new java.util.Date(cals.getTimeInMillis());
		
		String rtStart = "04"+year;
		String rtEnd = "03"+(year+1);		
		List<String> rtArray=Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd);

		
		List<ReconsileInvoices> reconsileInvoicesLst=getReconsileExcelData("Financialyear",clientid, month, year, stDate, endDate, rtArray);
		
		TextColumnBuilder<String> prInvTypeColumn = col.column("Invoice Type", "purchaseInvtype", type.stringType());
		TextColumnBuilder<String> prSuppliernameColumn = col.column("Supplier Name", "purchaseSuppliername", type.stringType());
		TextColumnBuilder<String> prInvoicenoColumn = col.column("Invoice Number", "purchaseInvoiceno", type.stringType());
		TextColumnBuilder<String> prInvoicedateColumn = col.column("Invoice Date", "purchaseInvoicedate", type.stringType());
		TextColumnBuilder<String> prGstnoColumn = col.column("GSTN No", "purchaseGstno", type.stringType());
		TextColumnBuilder<Double> prTotaltaxableamountColumn = col.column("Taxable Amount", "purchaseTotaltaxableamount", type.doubleType());
		TextColumnBuilder<Double> prTotalamountColumn = col.column("Total Tax", "purchaseTotalamount", type.doubleType());
		TextColumnBuilder<Double> prTotalIgstamountColumn = col.column("Total IGST", "purchaseTotalIgstAmount", type.doubleType());
		TextColumnBuilder<Double> prTotalCgstamountColumn = col.column("Total CGST", "purchaseTotalCgstAmount", type.doubleType());
		TextColumnBuilder<Double> prTotalSgstamountColumn = col.column("Total SGST", "purchaseTotalSgstAmount", type.doubleType());
		TextColumnBuilder<Double> prTotalCessamountColumn = col.column("Total CESS", "purchaseTotalCessAmount", type.doubleType());
		TextColumnBuilder<String> prBranchColumn = col.column("Branch", "purchaseBranch", type.stringType());
		
		TextColumnBuilder<String> statusColumn = col.column("Status", "status", type.stringType());
		
		TextColumnBuilder<String> gstr2InvTypeColumn = col.column("Invoice Type", "gstr2Invtype", type.stringType());
		TextColumnBuilder<String> gstr2SuppliernameColumn = col.column("Supplier Name", "gstr2Suppliername", type.stringType());
		TextColumnBuilder<String> gstr2InvoicenoColumn = col.column("Invoice Number", "gstr2Invoiceno", type.stringType());
		TextColumnBuilder<String> gstr2InvoicedateColumn = col.column("Invoice Date", "gstr2Invoicedate", type.stringType());
		TextColumnBuilder<String> gstr2GstnoColumn = col.column("GSTN No", "gstr2Gstno", type.stringType());
		TextColumnBuilder<Double> gstr2TotaltaxableamountColumn = col.column("Taxable Amount", "gstr2Totaltaxableamount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalamountColumn = col.column("Total Tax", "gstr2Totalamount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalIgstamountColumn = col.column("Total IGST", "gstr2TotalIgstAmount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalCgstamountColumn = col.column("Total CGST", "gstr2TotalCgstAmount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalSgstamountColumn = col.column("Total SGST", "gstr2TotalSgstAmount", type.doubleType());
		TextColumnBuilder<Double> gstr2TotalCessamountColumn = col.column("Total CESS", "gstr2TotalCessAmount", type.doubleType());
		TextColumnBuilder<String> gstr2BranchColumn = col.column("Branch", "gstr2Branch", type.stringType());

		HorizontalListBuilder left = cmp.horizontalList().add(cmp.text("Books (Purchase)"));
		HorizontalListBuilder right = cmp.horizontalList().add(cmp.text("GSTR2A"));
		HorizontalListBuilder title = cmp.horizontalList().add(left, right);
		
		if(dwnldtype.equalsIgnoreCase("alldetailswise")) {
			String customField1 = "CustomField1";
			String customField2 = "CustomField2";
			String customField3 = "CustomField3";
			String customField4 = "CustomField4";
			if(NullUtil.isNotEmpty(client)){
				gstnumber = client.getGstnnumber();
				CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
				if(isNotEmpty(customFields)) {
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
				}
			}
			TextColumnBuilder<String> prFpColumn = col.column("Return Period", "purchaseReturnPeriod", type.stringType());
			TextColumnBuilder<String> prRcColumn = col.column("Reverse Charge", "purchaseReverseCharge", type.stringType());
			TextColumnBuilder<String> prTdColumn = col.column("Transaction Date", "purchaseTransactionDate", type.stringType());
			TextColumnBuilder<String> prCSNColumn = col.column("Company State Name", "purchaseCompanyStateName", type.stringType());
			TextColumnBuilder<String> prStateColumn = col.column("State", "purchaseState", type.stringType());
			TextColumnBuilder<String> prCustomField1Column = col.column(customField1, "purchasecustomField1", type.stringType());
			TextColumnBuilder<String> prCustomField2Column = col.column(customField2, "purchasecustomField2", type.stringType());
			TextColumnBuilder<String> prCustomField3Column = col.column(customField3, "purchasecustomField3", type.stringType());
			TextColumnBuilder<String> prCustomField4Column = col.column(customField4, "purchasecustomField4", type.stringType());
			TextColumnBuilder<String> gstr2FpColumn = col.column("Return Period", "gstr2ReturnPeriod", type.stringType());
			TextColumnBuilder<String> gstr2RcColumn = col.column("Reverse Charge", "gstr2ReverseCharge", type.stringType());
			TextColumnBuilder<String> gstr2TdColumn = col.column("Transaction Date", "gstr2TransactionDate", type.stringType());
			TextColumnBuilder<String> gstr2CSNColumn = col.column("Company State Name", "gstr2CompanyStateName", type.stringType());
			TextColumnBuilder<String> gstr2StateColumn = col.column("State", "gstr2State", type.stringType());
			TextColumnBuilder<String> gstr2CustomField1Column = col.column(customField1, "gstr2customField1", type.stringType());
			TextColumnBuilder<String> gstr2CustomField2Column = col.column(customField2, "gstr2customField2", type.stringType());
			TextColumnBuilder<String> gstr2CustomField3Column = col.column(customField3, "gstr2customField3", type.stringType());
			TextColumnBuilder<String> gstr2CustomField4Column = col.column(customField4, "gstr2customField4", type.stringType());
			
			
			if(reconsileInvoicesLst.size() < 10000) {
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				String fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+".xls";
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);
				File file = new File(fileName);
				FileInputStream in = null;
				OutputStream out = null;
				
				try {
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					
					JasperXlsExporterBuilder xlsExporter = export.xlsExporter(fos)
							.addSheetName("MasterGST")
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
									prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
									prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,
									prTotalSgstamountColumn,prTotalCgstamountColumn,prTotalCessamountColumn ,prBranchColumn,
									prFpColumn,prRcColumn,prTdColumn,prCSNColumn,prStateColumn,prCustomField1Column,
									prCustomField2Column,prCustomField3Column,prCustomField4Column,
									statusColumn, 
									gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
									gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,
									gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn,gstr2TotalCessamountColumn ,gstr2BranchColumn,
									gstr2FpColumn,gstr2RcColumn,gstr2TdColumn,gstr2CSNColumn,gstr2StateColumn,
									gstr2CustomField1Column,gstr2CustomField2Column,gstr2CustomField3Column,gstr2CustomField4Column
								)
							.setDataSource(reconsileInvoicesLst)
							.toXls(xlsExporter);
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
					String fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+".zip";
					response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
					response.setContentType("application/octet-stream; charset=utf-8");
					zipOutputStream = new ZipOutputStream(nout);
					byte[] buf = new byte[1024];
					int c=0;
					int len = 0;
					double i = ((double)reconsileInvoicesLst.size())/60000;
					int j = (int)i;
					if(i-(int)i > 0) {
						j = (int)i+1;
					}
					List<List<ReconsileInvoices>> lt = Lists.newArrayList();
					int a=0;
					int b = 60000;
					if(reconsileInvoicesLst.size() < 60000) {
						b= reconsileInvoicesLst.size();
					}
					for(int k = 1; k <= j;k++) {
						lt.add(reconsileInvoicesLst.subList(a, b));
						a = b;
						if(k == j-1) {
							b = reconsileInvoicesLst.size();
						}else {
							b = b+60000;
						}
					}
					for(List<ReconsileInvoices> InvoicesList: lt) {
						File file1 = new File("MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+"_"+(c+1)+".xls");
						file1.createNewFile();
						FileOutputStream fos = new FileOutputStream(file1);
						
						JasperXlsExporterBuilder xlsExporter = export.xlsExporter(fos)
							.addSheetName("MasterGST")
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
									prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
									prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,
									prTotalSgstamountColumn,prTotalCgstamountColumn,prTotalCessamountColumn ,prBranchColumn,
									prFpColumn,prRcColumn,prTdColumn,prCSNColumn,prStateColumn,prCustomField1Column,
									prCustomField2Column,prCustomField3Column,prCustomField4Column,
									statusColumn, 
									gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
									gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,
									gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn,gstr2TotalCessamountColumn ,gstr2BranchColumn,
									gstr2FpColumn,gstr2RcColumn,gstr2TdColumn,gstr2CSNColumn,gstr2StateColumn,
									gstr2CustomField1Column,gstr2CustomField2Column,gstr2CustomField3Column,gstr2CustomField4Column
								)
							.setDataSource(InvoicesList)
							.toXls(xlsExporter);
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
	}else {
		
		if(reconsileInvoicesLst.size() < 10000) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			
			String fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+".xls";
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			File file = new File(fileName);
			FileInputStream in = null;
			OutputStream out = null;
			
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				
				JasperXlsExporterBuilder xlsExporter = export.xlsExporter(fos)
						.addSheetName("MasterGST")
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
								prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
								prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,
								prTotalSgstamountColumn,prTotalCgstamountColumn,prTotalCessamountColumn ,prBranchColumn,
								
								statusColumn, 
								gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
								gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,
								gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn,gstr2TotalCessamountColumn ,gstr2BranchColumn
							)
						.setDataSource(reconsileInvoicesLst)
						.toXls(xlsExporter);
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
				String fileName = "MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+".zip";
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
				response.setContentType("application/octet-stream; charset=utf-8");
				zipOutputStream = new ZipOutputStream(nout);
				byte[] buf = new byte[1024];
				int c=0;
				int len = 0;
				double i = ((double)reconsileInvoicesLst.size())/60000;
				int j = (int)i;
				if(i-(int)i > 0) {
					j = (int)i+1;
				}
				List<List<ReconsileInvoices>> lt = Lists.newArrayList();
				int a=0;
				int b = 60000;
				if(reconsileInvoicesLst.size() < 60000) {
					b= reconsileInvoicesLst.size();
				}
				for(int k = 1; k <= j;k++) {
					lt.add(reconsileInvoicesLst.subList(a, b));
					a = b;
					if(k == j-1) {
						b = reconsileInvoicesLst.size();
					}else {
						b = b+60000;
					}
				}
				for(List<ReconsileInvoices> InvoicesList: lt) {
					File file1 = new File("MGST_Reconciliation_"+clientname+"_"+gstnumber+"_"+month+year+"_"+(c+1)+".xls");
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					
					JasperXlsExporterBuilder xlsExporter = export.xlsExporter(fos)
						.addSheetName("MasterGST")
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
								prInvTypeColumn, prSuppliernameColumn, prInvoicenoColumn, prInvoicedateColumn,
								prGstnoColumn, prTotaltaxableamountColumn, prTotalamountColumn,prTotalIgstamountColumn,
								prTotalSgstamountColumn,prTotalCgstamountColumn,prTotalCessamountColumn, prBranchColumn,
								
								statusColumn, 
								gstr2InvTypeColumn, gstr2SuppliernameColumn, gstr2InvoicenoColumn, gstr2InvoicedateColumn,
								gstr2GstnoColumn, gstr2TotaltaxableamountColumn, gstr2TotalamountColumn,gstr2TotalIgstamountColumn,
								gstr2TotalCgstamountColumn,gstr2TotalSgstamountColumn,gstr2TotalCessamountColumn ,gstr2BranchColumn
							)
						.setDataSource(InvoicesList)
						.toXls(xlsExporter);
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
}
