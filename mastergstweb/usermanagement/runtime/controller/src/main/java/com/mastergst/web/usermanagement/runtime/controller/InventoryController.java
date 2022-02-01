package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isNotEmpty;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.jxls.template.SimpleExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Lists;
import com.lowagie.text.pdf.AcroFields.Item;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.dao.InventoryDao;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CommonBO;
import com.mastergst.usermanagement.runtime.domain.CompanyItems;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.ProformaInvoices;
import com.mastergst.usermanagement.runtime.domain.PurchaseOrder;
import com.mastergst.usermanagement.runtime.domain.StockAdjustments;
import com.mastergst.usermanagement.runtime.domain.StockAgingVO;
import com.mastergst.usermanagement.runtime.domain.StockItems;
import com.mastergst.usermanagement.runtime.repository.CompanyItemsRepository;
import com.mastergst.usermanagement.runtime.response.InvoiceVO;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.InventoryService;

import net.sf.dynamicreports.jasper.builder.export.JasperXlsExporterBuilder;
import net.sf.dynamicreports.jasper.constant.JasperProperty;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.exception.DRException;

@Controller
public class InventoryController {
	private static final Logger logger = LogManager.getLogger(InventoryController.class.getName());
	private static final String CLASSNAME = "InventoryController::";
	
	@Autowired
	private InventoryService inventoryService;
	@Autowired
	private ClientService clientService;
	@Autowired
	private CompanyItemsRepository companyItemsRepository;
	@Autowired
	private InventoryDao inventoryDao;
	
	@RequestMapping(value = "/srchItems", method = RequestMethod.GET)
	public @ResponseBody CommonBO getCommonList(ModelMap model,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "clientid", required = true) String clientid) throws Exception {
		final String method = "getItemsList::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + query);
		return inventoryService.getItemsList(clientid, query);
	}
	
	@RequestMapping(value = "/srchItemNames", method = RequestMethod.GET)
	public @ResponseBody List<CompanyItems> getItemNames(@RequestParam(value = "query", required = true) String query) throws Exception {
		final String method = "getItemNames::";
		logger.debug(CLASSNAME + method + BEGIN);
		List<CompanyItems> citems = companyItemsRepository.findAll();
		return citems;
	}
	
	
	@RequestMapping(value = "/srchPoNos/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String userData(ModelMap model,@PathVariable("id") String id,@PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year,@RequestParam("type") String type,
			@RequestParam(value = "query", required = true) String query) throws Exception {
		final String method = "userData::";
		logger.debug(CLASSNAME + method + BEGIN);
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=mapper.writer();
		if(type.equals("PO")) {
			List<PurchaseOrder> ponos = inventoryService.getAllPOData(id, clientid, month, year, query);
			return writer.writeValueAsString(ponos);
		}else {
			List<ProformaInvoices> prnos = inventoryService.getAllProformaData(id, clientid, month, year, query);
			return writer.writeValueAsString(prnos);
		}
	}
	
	@RequestMapping(value = "/cp_createStock/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.POST)
	public String createStockAdjust(@ModelAttribute("stockItem") StockAdjustments stockItem, @PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("itemid") String itemid,ModelMap model) throws Exception {
		final String method = "createStockAdjust::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		if (NullUtil.isNotEmpty(stockItem.getId()) && new ObjectId(id).equals(stockItem.getId())) {
			stockItem.setId(null);
		}
		CompanyItems items = companyItemsRepository.findOne(itemid);
		if(isNotEmpty(items)) {
		   if(isNotEmpty(stockItem.getCurrentStock())) {
				items.setCurrentStock(stockItem.getCurrentStock());
				if(isNotEmpty(stockItem.getStockItemQty())) {
					String mov = "In";
					mov = stockItem.getStockMovement();
					if(NullUtil.isEmpty(items.getStockAdjItemQty())) {
						if(mov.equalsIgnoreCase("In")) {
							items.setStockAdjItemQty(stockItem.getStockItemQty());
						}else {
							items.setStockAdjItemQty(0d-stockItem.getStockItemQty());
						}
					}else {
						if(mov.equalsIgnoreCase("In")) {
							items.setStockAdjItemQty(items.getStockAdjItemQty() + stockItem.getStockItemQty());
						}else {
							items.setStockAdjItemQty(items.getStockAdjItemQty() - stockItem.getStockItemQty());
						}
					}
				}
				companyItemsRepository.save(items);
			}
		   stockItem.setItemId(items.getId());
		}
		inventoryService.saveStocks(stockItem, items.getId());
		
		logger.debug(CLASSNAME + method + END);
		return "redirect:/cp_items/" + id + "/" + fullname + "/" + usertype + "/" + stockItem.getClientid() + "/" + month + "/" + year;
	}
	
	@RequestMapping(value="/checkStock/{clientid}/{itemname}",method=RequestMethod.GET)
	public @ResponseBody Double checkStock(@PathVariable("clientid") String clientid,@PathVariable("itemname") String itemname) {
		Double quantity = inventoryService.getInvoiceItemsData(clientid, itemname);
		return quantity;
	}
	 @RequestMapping(value = "/getStockDetails/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
		public @ResponseBody String getStockDetails(@PathVariable("id") String id, @PathVariable("name") String fullname, 
				@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
				@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("itemname") String itemname, ModelMap model,HttpServletRequest request) throws Exception {
			final String method = "getStockDetails::";
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
			Page<? extends StockAdjustments> stocks = inventoryService.getStockDetails(pageable,clientid, itemname, month, year, start, length,searchVal);
			if(isNotEmpty(stocks)) {
				for(StockAdjustments stock : stocks) {
					stock.setUserid(stock.getId().toString());
				}
			} 
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(MapperFeature.USE_ANNOTATIONS);
			ObjectWriter writer=mapper.writer();
			return writer.writeValueAsString(stocks);
	 }
	 @RequestMapping(value = "/getCustomStockDetails/{id}/{name}/{usertype}/{clientid}/{fromtime}/{totime}", method = RequestMethod.GET)
		public @ResponseBody String getCustomStockDetails(@PathVariable("id") String id, @PathVariable("name") String fullname, 
				@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
				@PathVariable("fromtime")String fromtime, @PathVariable("totime")String totime, @RequestParam("itemname") String itemname, ModelMap model,HttpServletRequest request) throws Exception {
			final String method = "getCustomStockDetails::";
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
			Page<? extends StockAdjustments> stocks = inventoryService.getCustomStockDetails(pageable, clientid, itemname, fromtime, totime, start, length,searchVal);
			if(isNotEmpty(stocks)) {
				for(StockAdjustments stock : stocks) {
					stock.setUserid(stock.getId().toString());
				}
			} 
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(MapperFeature.USE_ANNOTATIONS);
			ObjectWriter writer=mapper.writer();
			return writer.writeValueAsString(stocks);
	 } 
	 
	 @RequestMapping(value = "/getStockLedgers/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
		public @ResponseBody String getStockLedgers(@PathVariable("id") String id, @PathVariable("name") String fullname, 
				@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
				@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("itemname") String itemname, ModelMap model,HttpServletRequest request) throws Exception {
			final String method = "getStockLedgers::";
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
			Page<? extends StockItems> stocks = inventoryService.getStockLedgerDetails(pageable,clientid, itemname, month, year, start, length,searchVal);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(MapperFeature.USE_ANNOTATIONS);
			ObjectWriter writer=mapper.writer();
			return writer.writeValueAsString(stocks);
	 }
	@RequestMapping(value = "/getCustomStockLedgers/{id}/{name}/{usertype}/{clientid}/{fromtime}/{totime}", method = RequestMethod.GET)
		public @ResponseBody String getCustomStockLedgers(@PathVariable("id") String id, @PathVariable("name") String fullname, 
				@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
				@PathVariable("fromtime")String fromtime, @PathVariable("totime")String totime, @RequestParam("itemname") String itemname, ModelMap model,HttpServletRequest request) throws Exception {
			final String method = "getCustomStockLedgers::";
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
			Page<? extends StockItems> stocks = inventoryService.getCustomStockLedgerDetails(pageable,clientid, itemname, fromtime, totime, start, length,searchVal);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(MapperFeature.USE_ANNOTATIONS);
			ObjectWriter writer=mapper.writer();
			return writer.writeValueAsString(stocks);
	 }
	 
	@RequestMapping(value = "/getStockSummaryDetails/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getStockSummaryDetails(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,HttpServletRequest request) throws Exception {
		final String method = "getStockSummaryDetails::";
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
		Map<String, Object> stocksMap = inventoryService.getStockSalesSummaryDetails(pageable,clientid, month, year, start, length,searchVal);
		Page<? extends CompanyItems> stocks = (Page<? extends CompanyItems>) stocksMap.get("items");
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=mapper.writer();
		return writer.writeValueAsString(stocksMap);
 }
	
	@RequestMapping(value = "/getStockAgingDetails/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getStockAgingDetails(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("intervalValue") int intervalValue, 
			ModelMap model,HttpServletRequest request) throws Exception {
		final String method = "getStockAgingDetails::";
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
		Map<String, Object> ageMap = inventoryService.getStockAgingDetails(pageable,clientid, intervalValue, month, year, start, length,searchVal);
		Page<? extends StockAgingVO> stocks = (Page<? extends StockAgingVO>) ageMap.get("stocks");
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=mapper.writer();
		return writer.writeValueAsString(ageMap);
 }
	@RequestMapping(value = "/dwnldStockSummaryXls/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource downloadStockSummaryExcelData(@PathVariable("id") String id, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("reportType") String reportType, HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		Client client = clientService.findById(clientid);
		String gstnumber="";
		if(isNotEmpty(client) && isNotEmpty(client.getGstnnumber())) {
			gstnumber = client.getGstnnumber();
		}
		List<CompanyItems> itm = Lists.newArrayList();
		Pageable itmPageable = new PageRequest(0, 10000);
		Page<? extends CompanyItems> items = inventoryDao.getItemSummaryDetails(clientid, month, year, 0, 0, null, itmPageable);
		itm.addAll(items.getContent());
		while(isNotEmpty(items) && items.hasNext()) {
			items = inventoryDao.getItemSummaryDetails(clientid, month, year, 0, 0, null, items.nextPageable());
			itm.addAll(items.getContent());
		}
		List<CompanyItems> itemsList = inventoryService.getReportSummaryItems(itm);
			List<String> headers = null;
			if(reportType.equals("Summary")) {
				headers = Arrays.asList("Item Name", "Item Code","Purchase Price","Selling Price","Stock Quantity","Stock Value");
			}else if(reportType.equals("Rate")) {
				headers = Arrays.asList("Item Code", "Item Name","MRP Price","Selling Price");
			}else if(reportType.equals("Sales")) {
				headers = Arrays.asList("Item Name","Unit Quantity");
			}
			OutputStream nout = null;
			ZipOutputStream zipOutputStream = null;
			try {
				nout = response.getOutputStream();
				String fileName = "MGST_" + gstnumber + "_" + month+year + ".zip";
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
				response.setContentType("application/octet-stream; charset=utf-8");
				zipOutputStream = new ZipOutputStream(nout);
				byte[] buf = new byte[1024];
				int c=0;
				int len = 0;
				double i = ((double)itemsList.size())/60000;
				int j = (int)i;
				if(i-(int)i > 0) {
					j = (int)i+1;
				}
				List<List<CompanyItems>> lt = Lists.newArrayList();
				int a=0;
				int b = 60000;
				if(itemsList.size() < 60000) {
					b= itemsList.size();
				}
				for(int k = 1; k <= j;k++) {
					lt.add(itemsList.subList(a, b));
					a = b;
					if(k == j-1) {
						b = itemsList.size();
					}else {
						b = b+60000;
					}
				}
				for(List<CompanyItems> summaryList: lt) {
					File file1 = new File("MGST_"+gstnumber+"_"+month+year+"_"+(c+1)+ ".xls");
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					SimpleExporter exporter = new SimpleExporter();
					if(reportType.equals("Summary")) {
						exporter.gridExport(headers, summaryList, "description,itemno,purchasePrice,salePrice,fullname,wholeSalePrice", fos);
					}else if(reportType.equals("Rate")) {
						exporter.gridExport(headers, summaryList, "itemno,description,mrpPrice,salePrice", fos);
					}else if(reportType.equals("Sales")) {
						exporter.gridExport(headers, summaryList, "description,clientid", fos);
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
				
			}catch (IOException e) {
				logger.error(CLASSNAME + "downloadStockSummaryExcelData : ERROR", e);
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
			
		return new FileSystemResource(new File("MGST_"+gstnumber+"_"+month+year+".xls"));
	}
	
	@RequestMapping(value = "/dwnldStockDetailXls/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource downloadStockDetailExcelData(@PathVariable("id") String id, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		String itemname = request.getParameter("itemname");
		Client client = clientService.findById(clientid);
		String gstnumber="";
		if(isNotEmpty(client) && isNotEmpty(client.getGstnnumber())) {
			gstnumber = client.getGstnnumber();
		}
		List<StockAdjustments> stock = Lists.newArrayList();
		Pageable itmPageable = new PageRequest(0, 10000);
		Page<? extends StockAdjustments> stocks = inventoryService.getStockDetails(itmPageable,clientid, itemname, month, year, 0, 0, null);
		stock.addAll(stocks.getContent());
		while(isNotEmpty(stocks) && stocks.hasNext()) {
			stocks = inventoryService.getStockDetails(stocks.nextPageable(), clientid, itemname, month, year, 0, 0, null);
			stock.addAll(stocks.getContent());
		}
			List<StockAdjustments> stocksList = inventoryService.getStockReportDetail(stock);
			List<String> headers = Arrays.asList("Date", "Item Name","Transaction Type","Quantity","Closing Stock");
			OutputStream nout = null;
			ZipOutputStream zipOutputStream = null;
			try {
				nout = response.getOutputStream();
				String fileName = "MGST_" + gstnumber + "_" + month+year + ".zip";
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
				response.setContentType("application/octet-stream; charset=utf-8");
				zipOutputStream = new ZipOutputStream(nout);
				byte[] buf = new byte[1024];
				int c=0;
				int len = 0;
				double i = ((double)stocksList.size())/60000;
				int j = (int)i;
				if(i-(int)i > 0) {
					j = (int)i+1;
				}
				List<List<StockAdjustments>> lt = Lists.newArrayList();
				int a=0;
				int b = 60000;
				if(stocksList.size() < 60000) {
					b= stocksList.size();
				}
				for(int k = 1; k <= j;k++) {
					lt.add(stocksList.subList(a, b));
					a = b;
					if(k == j-1) {
						b = stocksList.size();
					}else {
						b = b+60000;
					}
				}
				for(List<StockAdjustments> summaryList: lt) {
					File file1 = new File("MGST_"+gstnumber+"_"+month+year+"_"+(c+1)+ ".xls");
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					SimpleExporter exporter = new SimpleExporter();
					exporter.gridExport(headers, summaryList, "dateOfMovement,stockItemName,transactionType,fullname,stockComments", fos);
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
				logger.error(CLASSNAME + "downloadStockDetailExcelData : ERROR", e);
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
			
		return new FileSystemResource(new File("MGST_"+gstnumber+"_"+month+year+".xls"));
	}
	
	@RequestMapping(value = "/dwnldCustomStockDetailXls/{id}/{clientid}/{fromtime}/{totime}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource downloadCustomStockDetailExcelData(@PathVariable("id") String id, @PathVariable("clientid") String clientid, 
			@PathVariable("fromtime") String fromtime, @PathVariable("totime") String totime, HttpServletResponse response, HttpServletRequest request) {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		String itemname = request.getParameter("itemname");
		Client client = clientService.findById(clientid);
		String gstnumber="";
		if(isNotEmpty(client) && isNotEmpty(client.getGstnnumber())) {
			gstnumber = client.getGstnnumber();
		}
		List<StockAdjustments> stock = Lists.newArrayList();
		Pageable itmPageable = new PageRequest(0, 10000);
		Page<? extends StockAdjustments> stocks = inventoryService.getCustomStockDetails(itmPageable,clientid, itemname, fromtime, totime, 0, 0, null);
		stock.addAll(stocks.getContent());
		while(isNotEmpty(stocks) && stocks.hasNext()) {
			stocks = inventoryService.getCustomStockDetails(stocks.nextPageable(), clientid, itemname, fromtime, totime, 0, 0, null);
			stock.addAll(stocks.getContent());
		}
			List<StockAdjustments> stocksList = inventoryService.getStockReportDetail(stock);
			List<String> headers = Arrays.asList("Date", "Item Name","Transaction Type","Quantity","Closing Stock");
			OutputStream nout = null;
			ZipOutputStream zipOutputStream = null;
			try {
				nout = response.getOutputStream();
				String fileName = "MGST_" + gstnumber + "_" + fromtime+totime + ".zip";
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
				response.setContentType("application/octet-stream; charset=utf-8");
				zipOutputStream = new ZipOutputStream(nout);
				byte[] buf = new byte[1024];
				int c=0;
				int len = 0;
				double i = ((double)stocksList.size())/60000;
				int j = (int)i;
				if(i-(int)i > 0) {
					j = (int)i+1;
				}
				List<List<StockAdjustments>> lt = Lists.newArrayList();
				int a=0;
				int b = 60000;
				if(stocksList.size() < 60000) {
					b= stocksList.size();
				}
				for(int k = 1; k <= j;k++) {
					lt.add(stocksList.subList(a, b));
					a = b;
					if(k == j-1) {
						b = stocksList.size();
					}else {
						b = b+60000;
					}
				}
				for(List<StockAdjustments> summaryList: lt) {
					File file1 = new File("MGST_"+gstnumber+"_"+fromtime+totime+"_"+(c+1)+ ".xls");
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					SimpleExporter exporter = new SimpleExporter();
					exporter.gridExport(headers, summaryList, "dateOfMovement,stockItemName,transactionType,fullname,stockComments", fos);
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
				logger.error(CLASSNAME + "downloadStockDetailExcelData : ERROR", e);
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
			
		return new FileSystemResource(new File("MGST_"+gstnumber+"_"+fromtime+totime+".xls"));
	}
	
	@RequestMapping(value = "/dwnldStockLedgerXls/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody FileSystemResource downloadStockLedgerExcelData(@PathVariable("id") String id, @PathVariable("clientid") String clientid, 
			@PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("itemname") String itemname, HttpServletResponse response, HttpServletRequest request) throws IOException {
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		Client client = clientService.findById(clientid);
		String gstnumber="";
		if(isNotEmpty(client) && isNotEmpty(client.getGstnnumber())) {
			gstnumber = client.getGstnnumber();
		}
		Pageable pageable = null;
		Page<? extends StockItems> stocks = inventoryService.getStockLedgerDetails(pageable,clientid, itemname, month, year, 0, 0, null);
		
		TextColumnBuilder<String> itemNameColumn = col.column("Item", "description", type.stringType());
		TextColumnBuilder<Date> dateColumn = col.column("Date", "asOnDate", type.dateType());
		TextColumnBuilder<String> voucherNoColumn = col.column("Voucher No", "stocks.stockPoNo", type.stringType());
		TextColumnBuilder<Double> inwardColumn = col.column("Inwards Quantity", "totInQty", type.doubleType());
		TextColumnBuilder<Double> outwardColumn = col.column("Outwards Quantity", "totOutQty", type.doubleType());
		TextColumnBuilder<Double> priceColumn = col.column("Price", "stocks.stockPurchaseCost", type.doubleType());
		TextColumnBuilder<Double> balanceColumn = col.column("Balance Quantity", "totBalQty", type.doubleType());
		
		try {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "inline; filename='MGST_Reconciliation_"+gstnumber+"_"+month+"_"+year+".xls");

            OutputStream outputStream = response.getOutputStream();
			
			JasperXlsExporterBuilder xlsExporter = export.xlsExporter(outputStream)
				.addSheetName("MasterGST")
				.setDetectCellType(true)
				.setIgnorePageMargins(true)
				.setWhitePageBackground(false)
				.setRemoveEmptySpaceBetweenColumns(true);

			report()
				.setColumnTitleStyle(Templates.columnTitleStyle)
				.addProperty(JasperProperty.EXPORT_XLS_FREEZE_ROW, "3")
				.ignorePageWidth()
				.ignorePagination()
				.columns(itemNameColumn, dateColumn, voucherNoColumn, inwardColumn,outwardColumn,priceColumn,balanceColumn)
				.setDataSource(stocks.getContent())
				.toXls(xlsExporter);
			outputStream.flush();
            outputStream.close();
		} catch (DRException e) {
			e.printStackTrace();
		}
		
		return new FileSystemResource(new File("MGST_"+gstnumber+"_"+month+year+".xls"));
	}
}
