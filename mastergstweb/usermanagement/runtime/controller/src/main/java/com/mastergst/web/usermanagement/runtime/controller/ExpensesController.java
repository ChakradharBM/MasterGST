package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Lists;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.DeletedInvoices;
import com.mastergst.usermanagement.runtime.domain.ExpenseVO;
import com.mastergst.usermanagement.runtime.domain.Expenses;
import com.mastergst.usermanagement.runtime.domain.ExpensesFilter;
import com.mastergst.usermanagement.runtime.domain.GroupDetails;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;
import com.mastergst.usermanagement.runtime.repository.AccountingJournalRepository;
import com.mastergst.usermanagement.runtime.repository.ExpensesRepository;
import com.mastergst.usermanagement.runtime.repository.GroupDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.LedgerRepository;
import com.mastergst.usermanagement.runtime.response.InvoiceVO;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ExpenseService;

@Controller
public class ExpensesController {
	private static final Logger logger = LogManager.getLogger(ExpensesController.class.getName());
	private static final String CLASSNAME = "ExpensesController::";
	@Autowired	ExpensesRepository expensesRepository;
	@Autowired	GroupDetailsRepository groupDetailsRepository;
	@Autowired	ClientService clientService;
	@Autowired	ExpenseService expenseService;
	@Autowired	LedgerRepository ledgerRepository;
	@Autowired	AccountingJournalRepository accountingJournalRepository;
	@Autowired private MongoTemplate mongoTemplate;
	
	@RequestMapping(value = "/getExpenditureList/{clientid}")
	public @ResponseBody List<GroupDetails> getExpGroupsList(@PathVariable("clientid") String clientid) {
		List<String> expenses = Lists.newArrayList();
		expenses.add("Direct expenses");
		expenses.add("Indirect expenses");
		List<GroupDetails> grpdtls = groupDetailsRepository.findByClientidAndHeadnameIn(clientid, expenses);
		Criteria criteria = Criteria.where("clientid").is(clientid).and("ledgerpath").regex("Expenses/", "i");
		Query query = Query.query(criteria);
		@SuppressWarnings("unchecked")
		List<String> ledgerNames = mongoTemplate.getCollection("ledger").distinct("ledgerName", query.getQueryObject());
		/*List<GroupDetails> grpdetails = Lists.newArrayList();
		if(isNotEmpty(grpdtls)) {
			for(GroupDetails group : grpdtls) {
				GroupDetails grp = new GroupDetails();
				grp.setGroupname(group.getGroupname());
				grp.setHeadname(group.getHeadname());
				grp.setName(group.getName());
				grp.setUserid(group.getUserid());
				grp.setPath(group.getPath());
				grpdetails.add(grp);
			}
		}*/
		ledgerNames.forEach(ledgername -> {
			GroupDetails grp = new GroupDetails();
			grp.setGroupname(ledgername.toString());
			grpdtls.add(grp);
		});
		return grpdtls;
	}
	@RequestMapping(value = "/cp_createGroup/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody void addGroup(@RequestBody GroupDetails  group, @PathVariable("id") String id,
			@PathVariable("name") String fullname, @PathVariable("usertype") String usertype,@PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		GroupDetails grp = new GroupDetails();
		grp.setGroupname(group.getGroupname()); 
		grp.setHeadname(group.getHeadname());
		grp.setClientid(clientid);
		grp.setPath("Expenses/"+group.getHeadname()+"/"+group.getGroupname());
		groupDetailsRepository.save(grp);
	}
	
	@RequestMapping(value = "/saveExpenses/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody void addExpenses(@ModelAttribute("expense") Expenses expenses, @PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		if (NullUtil.isNotEmpty(expenses.getId()) && new ObjectId(id).equals(expenses.getId())) {
			expenses.setId(null);
		}
		expenseService.saveExpenses(expenses,clientid,id);
	}
	@RequestMapping(value = "/getExpenses/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getExpenses(@PathVariable("id") String id, @PathVariable("returntype") String returntype, 
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("reportType")String reportType,
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getExpenses::";
		Client client = clientService.findById(clientid);
	       if(isNotEmpty(client) && isNotEmpty(client.getInvoiceViewOption())) {
				if(client.getInvoiceViewOption().equalsIgnoreCase("Yearly")) {
				  month = 0;
				  ++year;
				}
			}
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = "paymentDate";
		String sortOrder = "asc";
		sortOrder = request.getParameter("order[0][dir]");
		ExpensesFilter filter = expenseService.expenseFilter(request);
		Pageable pageable = null;
		Map<String, Object> invoicesMap = expenseService.getExpenses(pageable, client, id, "EXPENSES", reportType, month, year, start, length, searchVal,sortParam,sortOrder, filter);
		Page<? extends Expenses> expenses = (Page<? extends Expenses>)invoicesMap.get("expenses");
		if(isNotEmpty(expenses)) {
			for(Expenses expense : expenses) {
				expense.setUserid(expense.getId().toString());
			}
		} 
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=mapper.writer();
		return writer.writeValueAsString(invoicesMap);
	}
	
	@RequestMapping(value = "/getExpenses_category/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getExpensesSupport(@PathVariable("returntype") String returntype, 
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, 
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getExpensesSupport::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		Map<String, Object> expensesMap = expenseService.getExpensesSupport(client, returntype, month, year);
		return expensesMap;
	}
	@RequestMapping(value = "/getExpense/{expId}/{returntype}", method = RequestMethod.GET)
	public @ResponseBody String getExpense(@PathVariable("expId") String expId,
			@PathVariable("returntype") String returntype, ModelMap model) throws Exception {
		final String method = "getExpense::";
		logger.debug(CLASSNAME + method + BEGIN);
		Expenses expense = expensesRepository.findOne(expId);
		if(expense != null){
			expense.setUserid(expense.getId().toString());
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=mapper.writer();
		return writer.writeValueAsString(expense);
	}
	
	@RequestMapping(value = "/dwnldExpenseXls/{id}/{clientid}/{returntype}/{month}/{year}/{dwnldxlstype}", method = RequestMethod.GET)
	public void downloadExpenseExcelData(@PathVariable("id") String id,	@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype, 
			@PathVariable("month") int month, @PathVariable("year") int year,@PathVariable("dwnldxlstype") String dwnldxlstype, HttpServletResponse response, HttpServletRequest request) {
		Client client = clientService.findById(clientid);
		ExpensesFilter filter = expenseService.expenseFilter(request);
		Page<? extends Expenses> expenses = null;
		expenses = expenseService.getExpensesData(client, returntype, month, year, filter);
		List<ExpenseVO> expenseVOList=null;
		if("itemwise".equalsIgnoreCase(dwnldxlstype)) {
			expenseVOList = expenseService.expenseListItems(expenses,returntype);
		}else if("expensewise".equalsIgnoreCase(dwnldxlstype)){
			expenseVOList = expenseService.getExpenseWiseList(expenses,returntype);
		}
		List<String> headers = null;
		if(dwnldxlstype.equalsIgnoreCase("itemwise")) {
			headers = Arrays.asList("Payment Mode","Ledger Name", "Payment Date","Branch Name","Expense Category","Expense Item Name","Quantity","Rate","Total Value");
		}else if(dwnldxlstype.equalsIgnoreCase("expensewise")) {
			headers = Arrays.asList("Payment Mode","Ledger Name", "Payment Date","Branch Name","Total Value");
		}
		
		if(expenseVOList.size() < 10000) {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			String fileName = "MGST_"+returntype+"_"+month+year+".xls";
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			File file = new File(fileName);
			FileInputStream in = null;
			OutputStream out = null;
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				SimpleExporter exporter = new SimpleExporter();
				if(dwnldxlstype.equalsIgnoreCase("itemwise")) {
					exporter.gridExport(headers, expenseVOList,"paymentMode,ledgerName, paymentDate,branch,category,itemname,quantity,rate,totalVal",fos);
				}else if(dwnldxlstype.equalsIgnoreCase("expensewise")) {
					exporter.gridExport(headers, expenseVOList,"paymentMode,ledgerName, paymentDate,branch,totalVal",fos);
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
				String fileName = "MGST_"+returntype+"_"+month+year+".zip";
				response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
				response.setContentType("application/octet-stream; charset=utf-8");
				zipOutputStream = new ZipOutputStream(nout);
				byte[] buf = new byte[1024];
				int c=0;
				int len = 0;
				double i = ((double)expenseVOList.size())/60000;
				int j = (int)i;
				if(i-(int)i > 0) {
					j = (int)i+1;
				}
				List<List<ExpenseVO>> lt = Lists.newArrayList();
				int a=0;
				int b = 60000;
				if(expenseVOList.size() < 60000) {
					b= expenseVOList.size();
				}
				for(int k = 1; k <= j;k++) {
					lt.add(expenseVOList.subList(a, b));
					a = b;
					if(k == j-1) {
						b = expenseVOList.size();
					}else {
						b = b+60000;
					}
				}
				
				for(List<ExpenseVO> expensesList: lt) {
					File file1 = new File("MGST_"+returntype+"_"+month+year+"_"+(c+1)+".xls");
					file1.createNewFile();
					FileOutputStream fos = new FileOutputStream(file1);
					SimpleExporter exporter = new SimpleExporter();
					if(dwnldxlstype.equalsIgnoreCase("itemwise")) {
						exporter.gridExport(headers, expensesList,"paymentMode,ledgerName, paymentDate,branch,category,itemname,quantity,rate,totalVal",fos);
					}else if(dwnldxlstype.equalsIgnoreCase("expensewise")) {
						exporter.gridExport(headers, expensesList,"paymentMode,ledgerName, paymentDate,branch,totalVal",fos);
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
	@RequestMapping(value = "/delSelectedExps/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody String delSelectedExps(@PathVariable("id") String id,@PathVariable("clientid") String clientid,@PathVariable("returntype") String returntype,
			 @PathVariable("month") int month,	@PathVariable("year") int year,@RequestBody List<String> deletedexpenses, ModelMap model,HttpServletRequest request)
					throws Exception {
		final String method = "delSelectedExps::";
		for(String expenseid:deletedexpenses) {
			AccountingJournal journal = accountingJournalRepository.findByInvoiceIdAndClientIdAndReturnType(expenseid,clientid,"EXPENSES");
			if(isNotEmpty(journal)) {
				journal.setStatus("Deleted");
				accountingJournalRepository.save(journal);
			}
			expensesRepository.delete(expenseid);
		}
		return MasterGSTConstants.SUCCESS;
	}
	@RequestMapping(value = "/deleteAllExpenses/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.POST)
	public @ResponseBody String delAllInvss(@PathVariable("id") String id,@PathVariable("clientid") String clientid,@PathVariable("returntype") String returntype,
			 @PathVariable("month") int month,	@PathVariable("year") int year, ModelMap model,HttpServletRequest request)
					throws Exception {
		ExpensesFilter filter = expenseService.expenseFilter(request);
		Client client = clientService.findById(clientid);
		 if(isNotEmpty(client) && isNotEmpty(client.getInvoiceViewOption())) {
				if(client.getInvoiceViewOption().equalsIgnoreCase("Yearly")) {
				  month = 0;
				  ++year;
				}
			}
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		String sortParam = "paymentDate";
		String sortOrder = "asc";
		sortOrder = request.getParameter("order[0][dir]");
		Pageable pageable = null;
		Map<String,Object> expenseMap = expenseService.getExpenses(pageable, client, id, "EXPENSES", "", month, year, start, length, searchVal, sortParam, sortOrder, filter);
		Page<? extends Expenses> expenses = (Page<? extends Expenses>)expenseMap.get("expenses");
		if(isNotEmpty(expenses)) {
			for(Expenses expense : expenses) {
				AccountingJournal journal = accountingJournalRepository.findByInvoiceIdAndClientIdAndReturnType(expense.getId().toString(),clientid,"EXPENSES");
				if(isNotEmpty(journal)) {
					journal.setStatus("Deleted");
					accountingJournalRepository.save(journal);
				}
				expensesRepository.delete(expense.getId().toString());
			}
		} 	
		return MasterGSTConstants.SUCCESS;
    }
	
	@RequestMapping(value = "/getLedgersList/{clientid}/{ledgerName}")
	public @ResponseBody List<ProfileLedger> getLedgersList(@PathVariable("clientid") String clientid, @PathVariable("ledgerName") String ledgerName) {
		List<ProfileLedger> ledgerdetails = ledgerRepository.findByClientidAndGrpsubgrpName(clientid, ledgerName);
		return ledgerdetails;
	}
}
