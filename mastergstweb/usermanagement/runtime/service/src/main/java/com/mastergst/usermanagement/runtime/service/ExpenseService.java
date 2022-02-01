package com.mastergst.usermanagement.runtime.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ExpenseVO;
import com.mastergst.usermanagement.runtime.domain.Expenses;
import com.mastergst.usermanagement.runtime.domain.ExpensesFilter;

public interface ExpenseService {
	void saveExpenses(Expenses expenses, String clientid, String id);
	Map<String, Object> getExpenses(Pageable pageable, Client client, String id, String string, String reportType, int month, int year, int start, int length, String searchVal, String sortParam, String sortOrder, ExpensesFilter filter);
	Map<String, Object> getExpensesSupport(Client client, String returntype, int month, int year);
	ExpensesFilter expenseFilter(HttpServletRequest request);
	Page<? extends Expenses> getExpensesData(Client client, String returntype, int month, int year, ExpensesFilter filter);
	List<ExpenseVO> expenseListItems(Page<? extends Expenses> expenses, String returntype);
	List<ExpenseVO> getExpenseWiseList(Page<? extends Expenses> expenses, String returntype);
}
