package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mastergst.core.common.AccountConstants;
import com.mastergst.usermanagement.runtime.accounting.domain.JournalEntrie;
import com.mastergst.usermanagement.runtime.dao.ExpensesDao;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.AccountingJournalExpensesItems;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ExpenseDetails;
import com.mastergst.usermanagement.runtime.domain.ExpenseItem;
import com.mastergst.usermanagement.runtime.domain.ExpenseVO;
import com.mastergst.usermanagement.runtime.domain.Expenses;
import com.mastergst.usermanagement.runtime.domain.ExpensesFilter;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.repository.AccountingJournalRepository;
import com.mastergst.usermanagement.runtime.repository.ExpensesRepository;
import com.mastergst.usermanagement.runtime.support.Utility;

@Service
@Transactional(readOnly = true)
public class ExpenseServiceImpl implements ExpenseService {
	@Autowired
	private ExpensesDao expensesDao;
	@Autowired
	private ExpensesRepository expensesRepository;
	@Autowired
	private AccountingJournalRepository accountingJournalRepository;

	public void saveExpenses(Expenses expenses, String clientid, String id) {
		expenses.setClientid(clientid);
		expenses.setUserid(id);
		Date dt = null;
		Double total=0d;
		if(isNotEmpty(expenses.getPaymentDate())) {
			dt = expenses.getPaymentDate();
		}
		if(isNotEmpty(dt)) {
			int month = dt.getMonth();
			int year = dt.getYear()+1900;
			int quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			
			expenses.setMthCd(""+month);
			expenses.setYrCd(""+yearCode);
			expenses.setQrtCd(""+quarter);
			expenses.setSftr(1);
		}
		for(ExpenseDetails expenseDetails :expenses.getExpenses()) {
			Double expenseTot=0d;
			for(ExpenseItem eitem:expenseDetails.getExpensesList()) {
				if(isNotEmpty(eitem.getTotal())) {
					total+=eitem.getTotal();
					expenseTot += eitem.getTotal();;
				}
			}
			expenseDetails.setTotalAmt(expenseTot);
		}
		expenses.setTotalAmount(total);
		Expenses expenseforJournal = expensesRepository.save(expenses);
		saveJournalExpenses(expenseforJournal,id,clientid);
		
	}
	private void saveJournalExpenses(Expenses expenses, String userid, String clientid) {
		if (isNotEmpty(expenses)) {
			AccountingJournal journal = accountingJournalRepository.findByInvoiceId(expenses.getId().toString());
			List<AccountingJournal> journals = accountingJournalRepository.findByClientId(clientid);
			String journalnumber = "1";
			if (isNotEmpty(journals)) {
				journalnumber = journals.size() + 1 + "";
			}
			if (isEmpty(journal)) {
				journal = new AccountingJournal();
			}
			if (isNotEmpty(expenses.getPaymentDate())) {
				journal.setDateofinvoice(expenses.getPaymentDate());
				Calendar cal = Calendar.getInstance();
				cal.setTime(expenses.getPaymentDate());
				int month = cal.get(Calendar.MONTH) + 1;
				int year = cal.get(Calendar.YEAR);
				String strMonth = month < 10 ? "0" + month : month + "";
				journal.setInvoiceMonth(strMonth + year);
			}
			if(isNotEmpty(expenses.getMthCd())) {
				journal.setMthCd(expenses.getMthCd());
			}
			if(isNotEmpty(expenses.getYrCd())) {
				journal.setYrCd(expenses.getYrCd());
			}
			if(isNotEmpty(expenses.getQrtCd())) {
				journal.setQrtCd(expenses.getQrtCd());
			}
			if (isNotEmpty(expenses.getUserid())) {
				journal.setUserId(expenses.getUserid());
			}
			if (isNotEmpty(expenses.getClientid())) {
				journal.setClientId(expenses.getClientid());
			}
			
			if(isNotEmpty(expenses.getLedgerName())) {
				journal.setLedgerName(expenses.getLedgerName());
			}
			journal.setInvoiceId(expenses.getId().toString());
			journal.setReturnType("EXPENSES");
			if (isEmpty(journal.getJournalNumber())) {
				journal.setJournalNumber(journalnumber);
			}
			double totalAmount=0d;
			List<AccountingJournalExpensesItems> expenselist = Lists.newArrayList();
			List<JournalEntrie> drEntries = Lists.newArrayList();
			List<JournalEntrie> crEntries = Lists.newArrayList();
			if (isNotEmpty(expenses.getExpenses())) {
				for (ExpenseDetails item : expenses.getExpenses()) {
					AccountingJournalExpensesItems expensejournal = new AccountingJournalExpensesItems();
					String category = isNotEmpty(item.getCategory()) ? item.getCategory() : "";
					expensejournal.setExpensecategory(category);
					if (isNotEmpty(item.getTotalAmt())) {
						expensejournal.setTotalAmt(item.getTotalAmt());
						totalAmount = totalAmount + item.getTotalAmt();
						drEntries.add(new JournalEntrie(category, item.getTotalAmt()));
					}
					expenselist.add(expensejournal);
				}
				String ledgerName = isNotEmpty(expenses.getLedgerName()) ? expenses.getLedgerName() : AccountConstants.CASH;
				crEntries.add(new JournalEntrie(ledgerName, totalAmount));
				journal.setDrEntrie(drEntries);
				journal.setCrEntrie(crEntries);
				journal.setJournalexpensesitems(expenselist);
				journal.setCustomerorSupplierAccount(totalAmount);
				journal.setSalesorPurchases(totalAmount);
				journal.setCreditTotal(totalAmount);
				journal.setDebitTotal(totalAmount);
			}
			accountingJournalRepository.save(journal);
		}
	}
	public Map<String, Object> getExpenses(Pageable pageable, Client client, String id, String returntype, String reportType,  int month,
			int year, int start, int length, String searchVal, String sortParam, String sortOrder, ExpensesFilter filter) {
		Map<String, Object> expensesMap = new HashMap<String, Object>();
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		Page<? extends Expenses> expenses = null;
		TotalInvoiceAmount totalInvoiceAmount = null;
		if(isNotEmpty(client.getInvoiceViewOption()) && client.getInvoiceViewOption().equalsIgnoreCase("Yearly")) {
			if(isNotEmpty(client.getFilingoptions())) {
				String yr;
				if(month == 1 || month == 2 || month == 3) {
					yr = (year-1)+"-"+(year);
				}else {
					yr = year+"-"+(year+1);
				}
				client.getFilingoptions().forEach(options->{
					if(options.getYear().equalsIgnoreCase(yr)){
						client.setFilingOption(options.getOption());			
					}
				});
			}
		}else {
			if(isNotEmpty(client.getFilingoptions())) {
				String yr;
				if(month == 1 || month == 2 || month == 3) {
					yr = (year-1)+"-"+(year);
				}else {
					yr = year+"-"+(year+1);
				}
				//String yr=year+"-"+(year+1);
				client.getFilingoptions().forEach(options->{
					if(options.getYear().equalsIgnoreCase(yr)){
						client.setFilingOption(options.getOption());			
					}
				});
			}
		}
		String yearCode = Utility.getYearCode(month, year);
		expenses = expensesDao.findByClientidAndMonthAndYear(client.getId().toString(), month, yearCode, start, length, searchVal, filter);
		totalInvoiceAmount = expensesDao.getTotalInvoicesAmountsForMonth(client.getId().toString(), month, yearCode, searchVal, filter);
		expensesMap.put("expenses", expenses);
		expensesMap.put("expensesAmount",totalInvoiceAmount);
		return expensesMap;
	}
	public Map<String, Object> getExpensesSupport(Client client, String returntype, int month, int year) {
		Map<String, Object> supportObj = new HashMap<>();
		if (isNotEmpty(returntype)) {
			String yearCd = Utility.getYearCode(month, year); 
			List<String> category = null;
			category = expensesDao.getCategory(client.getId().toString(), month, yearCd);
			supportObj.put("category", category);
		}
		return supportObj;
	}
	@Override
	public ExpensesFilter expenseFilter(HttpServletRequest request) {
		ExpensesFilter filter = new ExpensesFilter();
		String category = request.getParameter("category");
		if(isNotEmpty(category)) {
			if(category.contains("-mgst-")) {
				category = category.replaceAll("-mgst-", "&");
			}
		}
		filter.setCategory(category);
		filter.setPaymentMode(request.getParameter("paymentMode"));
		filter.setFromtime(request.getParameter("fromtime"));
		filter.setTotime(request.getParameter("totime"));
		return filter;
	}
	@Override
	public Page<? extends Expenses> getExpensesData(Client client, String returntype, int month, int year,ExpensesFilter filter) {
		String yearCode = Utility.getYearCode(month, year);
		return expensesDao.findByClientidAndMonthAndYear(client.getId().toString(), month, yearCode, 0, 0, null, filter);
	}
	@Override
	public List<ExpenseVO> expenseListItems(Page<? extends Expenses> expenses, String returntype) {
		List<ExpenseVO> expenseVOList = Lists.newArrayList();
		Double tottotal = 0d;
		SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
		if(isNotEmpty(expenses)) {
			for (Expenses expense : expenses) {
				if (isNotEmpty(expense.getExpenses())) {
					for(ExpenseDetails expDetails : expense.getExpenses()) {
						for (ExpenseItem item : expDetails.getExpensesList()) {
							ExpenseVO expo = new ExpenseVO();
							expo.setCategory(isNotEmpty(expDetails.getCategory()) ? expDetails.getCategory() : "");
							expo.setPaymentMode(isNotEmpty(expense.getPaymentMode()) ? expense.getPaymentMode() : "");
							expo.setLedgerName(isNotEmpty(expense.getLedgerName()) ? expense.getLedgerName() : "");
							expo.setBranch(isNotEmpty(expense.getBranchName()) ? expense.getBranchName() : "");
							expo.setPaymentDate(isNotEmpty(expense.getPaymentDate()) ? date.format(expense.getPaymentDate()) : "");
							expo.setItemname(isNotEmpty(item.getItemName()) ? item.getItemName() : "");
							expo.setQuantity(isNotEmpty(item.getQuantity()) ? item.getQuantity() : 1);
							expo.setRate(isNotEmpty(item.getRate()) ? item.getRate() : 0d);
							Double total = isNotEmpty(item.getTotal()) ? item.getTotal() : 0d;
							expo.setTotalVal(total);
							tottotal += total;
							expenseVOList.add(expo);
						}
					}
				}
			}
		}
		ExpenseVO totalexpo = new ExpenseVO();
		totalexpo.setTotalVal(tottotal);
		expenseVOList.add(totalexpo);
		return expenseVOList;
	}
	@Override
	public List<ExpenseVO> getExpenseWiseList(Page<? extends Expenses> expenses, String returntype) {
		List<ExpenseVO> expenseVOList = Lists.newArrayList();
		SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
		Double tottotal = 0d;
		if(isNotEmpty(expenses)) {
			for (Expenses expense : expenses) {
					ExpenseVO expo = new ExpenseVO();
					expo.setPaymentMode(isNotEmpty(expense.getPaymentMode()) ? expense.getPaymentMode() : "");
					expo.setLedgerName(isNotEmpty(expense.getLedgerName()) ? expense.getLedgerName() : "");
					expo.setBranch(isNotEmpty(expense.getBranchName()) ? expense.getBranchName() : "");
					expo.setPaymentDate(isNotEmpty(expense.getPaymentDate()) ? date.format(expense.getPaymentDate()) : "");
					Double total = isNotEmpty(expense.getTotalAmount()) ? expense.getTotalAmount() : 0d;
					expo.setTotalVal(total);
					tottotal+=total;
					expenseVOList.add(expo);
			}
		}
		ExpenseVO totalExpo = new ExpenseVO();
		totalExpo.setTotalVal(tottotal);
		expenseVOList.add(totalExpo);
		return expenseVOList;
	}
}
