package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Arrays;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.usermanagement.runtime.audit.AuditlogService;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.audit.AuditLogReportUtil;
import com.mastergst.usermanagement.runtime.audit.Auditlog;
import com.mastergst.usermanagement.runtime.audit.AuditlogFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.repository.AuditLogRepository;
import com.mastergst.usermanagement.runtime.repository.ClientRepository;
import com.mastergst.usermanagement.runtime.response.InvoiceVO;
import com.mastergst.usermanagement.runtime.response.audit.AuditVO;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ProfileService;

@Controller
public class AuditlogController {
	private static final Logger logger = LogManager.getLogger(AuditlogController.class.getName());
	private static final String CLASSNAME = "AuditlogController::";
	
	@Autowired	ProfileService profileService;
	@Autowired	ClientService clientService;
	@Autowired	UserRepository userRepository;
	@Autowired  AuditlogService auditlogService; 
	@Autowired  AuditLogRepository auditLogRepository;
	@Autowired  ClientRepository clientRepository;
	@Autowired  AuditLogReportUtil auditLogReportUtil;
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	
	
	@RequestMapping(value = "/cp_auditlog/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String clientAuditLogs(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, 
			@PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "clientAuditLogs::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userRepository.findOne(id);
		List<String> lClientids = clientService.fetchClientIds(id);
		if (isNotEmpty(lClientids)) {
			model.addAttribute("lClientids", lClientids);
			List<Client> allClients = Lists.newArrayList(clientRepository.findAll(lClientids));
			model.addAttribute("allClients", allClients);
		}
		model.addAttribute("userList", profileService.getAllUsersByUserid(id));
		model.addAttribute("user", user);
		return "audit/auditlog";
	}
	
	@RequestMapping(value = "/getAuditlogs/{id}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getAdditionalInvoices(@PathVariable("id") String id,@PathVariable("month") int month, @PathVariable("year") int year,@RequestParam("clientids") List<String> clientids,ModelMap model, HttpServletRequest request) throws ParseException, Exception{
		
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = "createdDate";
		String sortOrder = "asc";
		sortOrder = request.getParameter("order[0][dir]");
		AuditlogFilter filter = auditlogService.auditlogFilter(request);
		Map<String, Object> invoicesMap = auditlogService.getAuditlogs(null, id, clientids, month, year, start, length,filter, searchVal, sortParam, sortOrder);
		Page<? extends Auditlog> invoices = (Page<? extends Auditlog>)invoicesMap.get("auditlogs");
		if(isNotEmpty(invoices)) {
			for(Auditlog invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		} 
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(invoicesMap);
	}
	
	@RequestMapping(value = "/getAuditlogsCustom/{id}/{fromtime}/{totime}", method = RequestMethod.GET)
	public @ResponseBody String getAuditlogsCustom(@PathVariable("id") String id,@PathVariable("fromtime")String fromtime, @PathVariable("totime")String totime,@RequestParam("clientids")List<String> clientids,
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getAuditlogsCustom::";
		logger.debug(CLASSNAME + method + BEGIN);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = "createdDate";
		String sortOrder = "asc";
		sortOrder = request.getParameter("order[0][dir]");
		AuditlogFilter filter = auditlogService.auditlogFilter(request);
		Map<String, Object> auditlogMap = auditlogService.getAuditLogCustom(null, id, clientids, fromtime, totime, start, length,filter, searchVal, sortParam, sortOrder);
		Page<? extends Auditlog> invoices = (Page<? extends Auditlog>)auditlogMap.get("auditlogs");
		if(isNotEmpty(invoices)) {
			for(Auditlog invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		} 
				
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(auditlogMap);
	}
	
	@RequestMapping(value = "/auditlogDetails/{id}", method = RequestMethod.GET)
	public @ResponseBody Auditlog gstinDetails(@PathVariable("id") String id, ModelMap model) throws Exception {
		final String method = "profileCenterClients::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		return auditLogRepository.findOne(id);
	}
	
	@RequestMapping(value = "/getAuditlogsCustomExcel/{id}/{fromtime}/{totime}", method = RequestMethod.GET)
	public void downloadExcelDataAuditLogCustom(@PathVariable("id") String id,@PathVariable("fromtime") String fromtime,
			 @PathVariable("totime") String totime,  @RequestParam("clientids")List<String> clientids,HttpServletResponse response, HttpServletRequest request) {
		List<Auditlog> inv = Lists.newArrayList();
		Pageable invPageable = new PageRequest(0, 10000);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String lens = request.getParameter("length");
		int length = StringUtils.isEmpty(lens) ? 10 :  Integer.parseInt(lens);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		AuditlogFilter filter = auditlogService.auditlogFilter(request);
		Page<? extends Auditlog> invoices = auditlogService.getAuditlogsCustomExcel(id, clientids, fromtime, totime, start, length, filter, searchVal, invPageable);
		inv.addAll(invoices.getContent());
		while(isNotEmpty(invoices) && invoices.hasNext()) {
			invoices = auditlogService.getAuditlogsCustomExcel(id, clientids, fromtime, totime, start, length, filter, searchVal, invoices.nextPageable());
			inv.addAll(invoices.getContent());
		}
		List<AuditVO> invoiceVOList=auditLogReportUtil.invoiceListItems(inv);
		
		List<String> headers = Arrays.asList("Client Name", "Client GSTIN", "User Name", "User Email","Action","Return Type","Invoice Number/No .of Invoices", "Description","Date",
				"Previous GSTIN","Updated GSTIN","Previous Invoice Number","Updated Invoice Number","Previous Invoice Date","Updated Invoice Date","Previous Total Taxable Amount","Updated Total Taxable Amount","Previous Total Total Amount","Updated Total Total Amount","Previous Total IGST Amount","Updated Total IGST Amount","Previous Total CGST Amount","Updated Total CGST Amount","Previous Total SGST Amount","Updated Total SGST Amount","Previous Total CESS Amount","Updated Total CESS Amount");
				
		if(invoiceVOList.size() < 60000) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			String fileName = "MGST_" + id + "_"+fromtime+"_" + totime + ".xls";
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			File file = new File(fileName);
			FileInputStream in = null;
			OutputStream out = null;
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				SimpleExporter exporter = new SimpleExporter();
				exporter.gridExport(headers, invoiceVOList,"clientname, gstn,username, useremail, action,returntype,invoicenumber, description,createddate,ogstin,ngstin,oinvoiceNumber,ninvoiceNumber, oinvoiceDate,ninvoiceDate,ototaltaxableamount,ntotaltaxableamount, ototalamount,ntotalamount,ototalIGSTAmount,ntotalIGSTAmount,ototalCGSTAmount,ntotalCGSTAmount,ototalSGSTAmount,ntotalSGSTAmount,ototalCESSAmount,ntotalCESSAmount",fos);
				
				in = new FileInputStream(file);
				out = response.getOutputStream();
				byte[] buffer= new byte[8192]; // use bigger if you want
				int lengths = 0;
				while ((lengths = in.read(buffer)) > 0){
				     out.write(buffer, 0, lengths);
				}
				if(isNotEmpty(in)) {
					in.close();
				}
			} catch (IOException e) {
				logger.error(CLASSNAME + "downloadExcelDataAuditLogCustom : ERROR", e);
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
				String fileName = "MGST_" + id + "_"+fromtime+"_" + totime + ".zip";
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
				List<List<AuditVO>> lt = Lists.newArrayList();
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
				
				for(List<AuditVO> InvoicesList: lt) {
					File file1 = new File("MGST_" + id + "_"+fromtime+"_" + totime +"_"+(c+1)+ ".xls");
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					SimpleExporter exporter = new SimpleExporter();
					exporter.gridExport(headers, InvoicesList,"clientname, gstn,username, useremail, action,returntype,invoicenumber, description,createddate,ogstin,ngstin,oinvoiceNumber,ninvoiceNumber, oinvoiceDate,ninvoiceDate,ototaltaxableamount,ntotaltaxableamount, ototalamount,ntotalamount,ototalIGSTAmount,ntotalIGSTAmount,ototalCGSTAmount,ntotalCGSTAmount,ototalSGSTAmount,ntotalSGSTAmount,ototalCESSAmount,ntotalCESSAmount",fos);
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
				logger.error(CLASSNAME + "downloadExcelDataAuditLogCustom : ERROR", e);
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
	
	
	@RequestMapping(value = "/getAuditlogsExcel/{id}/{month}/{year}", method = RequestMethod.GET)
	public void downloadExcelDataAuditlog(@PathVariable("id") String id,@PathVariable("month") int month,
			 @PathVariable("year") int year,  @RequestParam("clientids")List<String> clientids,HttpServletResponse response, HttpServletRequest request) {
		List<Auditlog> inv = Lists.newArrayList();
		Pageable invPageable = new PageRequest(0, 10000);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String lens = request.getParameter("length");
		int length = StringUtils.isEmpty(lens) ? 10 :  Integer.parseInt(lens);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		AuditlogFilter filter = auditlogService.auditlogFilter(request);
		Page<? extends Auditlog> invoices = auditlogService.getAuditlogsExcel(id, clientids, month, year, start, length, filter, searchVal, invPageable);
		inv.addAll(invoices.getContent());
		while(isNotEmpty(invoices) && invoices.hasNext()) {
			invoices = auditlogService.getAuditlogsExcel(id, clientids, month, year, start, length, filter, searchVal, invoices.nextPageable());
			inv.addAll(invoices.getContent());
		}
		List<AuditVO> invoiceVOList=auditLogReportUtil.invoiceListItems(inv);
		
		List<String> headers = Arrays.asList("Client Name", "Client GSTIN", "User Name", "User Email","Action","Return Type","Invoice Number/No .of Invoices", "Description","Date",
				"Previous GSTIN","Updated GSTIN","Previous Invoice Number","Updated Invoice Number","Previous Invoice Date","Updated Invoice Date","Previous Total Taxable Amount","Updated Total Taxable Amount","Previous Total Total Amount","Updated Total Total Amount","Previous Total IGST Amount","Updated Total IGST Amount","Previous Total CGST Amount","Updated Total CGST Amount","Previous Total SGST Amount","Updated Total SGST Amount","Previous Total CESS Amount","Updated Total CESS Amount");
		if(invoiceVOList.size() < 60000) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			String fileName = "MGST_" + id + "_"+month+"_" + year + ".xls";
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			File file = new File(fileName);
			FileInputStream in = null;
			OutputStream out = null;
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				SimpleExporter exporter = new SimpleExporter();
				exporter.gridExport(headers, invoiceVOList,"clientname, gstn,username, useremail, action,returntype,invoicenumber, description,createddate,ogstin,ngstin,oinvoiceNumber,ninvoiceNumber, oinvoiceDate,ninvoiceDate,ototaltaxableamount,ntotaltaxableamount, ototalamount,ntotalamount,ototalIGSTAmount,ntotalIGSTAmount,ototalCGSTAmount,ntotalCGSTAmount,ototalSGSTAmount,ntotalSGSTAmount,ototalCESSAmount,ntotalCESSAmount",fos);
				
				in = new FileInputStream(file);
				out = response.getOutputStream();
				byte[] buffer= new byte[8192]; // use bigger if you want
				int lengths = 0;
				while ((lengths = in.read(buffer)) > 0){
				     out.write(buffer, 0, lengths);
				}
				if(isNotEmpty(in)) {
					in.close();
				}
			} catch (IOException e) {
				logger.error(CLASSNAME + "downloadExcelDataAuditlog : ERROR", e);
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
				String fileName = "MGST_" + id + "_"+month+"_" + year + ".zip";
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
				List<List<AuditVO>> lt = Lists.newArrayList();
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
				
				for(List<AuditVO> InvoicesList: lt) {
					File file1 = new File("MGST_" + id + "_"+month+"_" + year +"_"+(c+1)+ ".xls");
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					SimpleExporter exporter = new SimpleExporter();
					exporter.gridExport(headers, InvoicesList,"clientname, gstn,username, useremail, action,returntype,invoicenumber, description,createddate,ogstin,ngstin,oinvoiceNumber,ninvoiceNumber, oinvoiceDate,ninvoiceDate,ototaltaxableamount,ntotaltaxableamount, ototalamount,ntotalamount,ototalIGSTAmount,ntotalIGSTAmount,ototalCGSTAmount,ntotalCGSTAmount,ototalSGSTAmount,ntotalSGSTAmount,ototalCESSAmount,ntotalCESSAmount",fos);
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
				logger.error(CLASSNAME + "downloadExcelDataAuditlog : ERROR", e);
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
