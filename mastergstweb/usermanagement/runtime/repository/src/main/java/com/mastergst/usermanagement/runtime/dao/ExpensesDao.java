package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Expenses;
import com.mastergst.usermanagement.runtime.domain.ExpensesFilter;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;

@Service
public class ExpensesDao extends InvoiceDao{
	public ExpensesDao(){
		super("expenses");
	}

	public Page<? extends Expenses> findByClientidAndMonthAndYear(String clientid, int month, String yearCode, int start,int length, String searchVal, ExpensesFilter filter) {
		Criteria criteria = Criteria.where("clientid").is(clientid);
		if(NullUtil.isNotEmpty(filter.getFromtime()) && NullUtil.isNotEmpty(filter.getTotime())) {
			String fromtime = filter.getFromtime();
			String totime = filter.getTotime();
			String[] fromtimes = fromtime.split("-");
			String[] totimes = totime.split("-");
			Date stDate = null;
			Date endDate = null;
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23,59, 59);
			endDate = new java.util.Date(cal.getTimeInMillis());
			criteria.and("paymentDate").gte(stDate).lte(endDate);
		}else {
			criteria.and("yrCd").is(yearCode);
		}
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		Sort sort = null;
		
		if(start == 0 && length == 0){
			return new PageImpl<Expenses>(mongoTemplate.find(query, Expenses.class, COLLECTION_NAME));
		}else {
			if(length != -1) {
				addAllInvoicesQueryFirlds(query);			
			}
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length,sort);
			query.with(pageable);
			long total = mongoTemplate.count(query, Expenses.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<Expenses>(Collections.<Expenses> emptyList());
			}
			return new PageImpl<Expenses>(mongoTemplate.find(query, Expenses.class, COLLECTION_NAME), pageable, total);
		}
	}

	private void applyFilterToCriteria(Criteria criteria, ExpensesFilter filter) {
		if(filter != null){
			if(filter.getCategory() != null){
				criteria.and("expenses.category").in(Arrays.asList(filter.getCategory()));
			}
			if(filter.getPaymentMode() != null){
				criteria.and("paymentMode").in(Arrays.asList(filter.getPaymentMode()));
			}
		}
		
	}

	private void addAllInvoicesQueryFirlds(Query query) {
		query.fields().include("category");
		query.fields().include("paymentMode");
		query.fields().include("paymentDate");
		query.fields().include("totalAmount");
		query.fields().include("expenses");
	}

	private Criteria getSearchValueCriteria(String searchval) {
		String searchVal = searchval;
		if(searchVal.contains("(")) {
			searchVal = searchVal.replaceAll("\\(", "\\\\(");
		}
		if(searchVal.contains(")")) {
			searchVal = searchVal.replaceAll("\\)", "\\\\)");
		}
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(Criteria.where("expenses.category").regex(searchVal, "i"));
 		criterias.add(Criteria.where("paymentMode").regex(searchVal, "i"));
 		criterias.add(Criteria.where("paymentDate").regex(searchVal, "i"));
 		criterias.add(Criteria.where("totalAmount").regex(searchVal, "i"));
 		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}

	public TotalInvoiceAmount getTotalInvoicesAmountsForMonth(String clientid, int month, String yearCode, String searchVal, ExpensesFilter filter) {
		Criteria criteria = Criteria.where("clientid").is(clientid);
		if(NullUtil.isNotEmpty(filter.getFromtime()) && NullUtil.isNotEmpty(filter.getTotime())) {
			String fromtime = filter.getFromtime();
			String totime = filter.getTotime();
			String[] fromtimes = fromtime.split("-");
			String[] totimes = totime.split("-");
			Date stDate = null;
			Date endDate = null;
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23,59, 59);
			endDate = new java.util.Date(cal.getTimeInMillis());
			criteria.and("paymentDate").gte(stDate).lte(endDate);
		}else {
			criteria.and("yrCd").is(yearCode);
		}
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = null;
		GroupOperation groupOperation = null;
		if(month > 0) {
			projectionOperation = getProjectionForTotal("mthCd");
			groupOperation = getGroupForTotal("mthCd");
		}else {
			projectionOperation = getProjectionForTotal("yrCd");
			groupOperation = getGroupForTotal("yrCd");
		}
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		return totalnvAmount;
	}
	
	public List<TotalInvoiceAmount> getConsolidatedSummeryForYearMonthwise(List<String> clientids, String yearCd, boolean checkQuarterly){
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCd);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("mthCd");
	GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
			.sum("totalAmount").as("totalAmount").count().as("totalTransactions");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return  results.getMappedResults();
}
	
	
	private GroupOperation getGroupForTotal(String fieldname) {
		return Aggregation.group(fieldname).count().as("totalTransactions")
				.sum("totalAmount").as("totalAmount");
	}

	private ProjectionOperation getProjectionForTotal(String fieldname) {
		return Aggregation.project().andInclude(fieldname)
				.and("totalAmount").multiply("sftr").as("totalAmount");
	}

	public List<String> getCategory(String clientId, int month, String yearCd) {
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCd);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		Query query = Query.query(criteria);
		List category = mongoTemplate.getCollection(COLLECTION_NAME).distinct("expenses.category", query.getQueryObject());
		return (List<String>)category;
	}
	
}
