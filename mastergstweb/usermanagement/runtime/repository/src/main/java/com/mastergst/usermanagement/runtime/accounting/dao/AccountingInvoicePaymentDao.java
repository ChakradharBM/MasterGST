package com.mastergst.usermanagement.runtime.accounting.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.accounting.domain.TotalPendingAmount;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.InvoicePayments;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;

@Component
public class AccountingInvoicePaymentDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	
	@SuppressWarnings("deprecation")
	public Page<InvoicePayments> getAllInvoicePayments(String clientid, String yearcode, boolean isPayment, int start, int length, String searchVal) {
		
		final String collection_name = isPayment ? "purchaseregister" : "gstr1";
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearcode).and("paymentStatus").nin("Paid");
		
		Pageable pageable = new PageRequest((start/length), length);
	    Aggregation aggregation = null;
	    Aggregation aggCount = null;
		
		if(StringUtils.hasLength(searchVal)){
	    	criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	    	aggregation = Aggregation.newAggregation(
	    				Aggregation.lookup("record_payment", "_id", "invoiceDocId", "payments"),
	    				Aggregation.match(criteria),
	    			//	Aggregation.unwind("payments", false),
	    				Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
	       				Aggregation.limit(pageable.getPageSize())
	       			);
	    	aggCount = Aggregation.newAggregation(
    				Aggregation.lookup("record_payment", "_id", "invoiceDocId", "payments"),
    				//Aggregation.unwind("payments", false),
    				Aggregation.match(criteria)
        		);
	    }else {
	       	aggregation = Aggregation.newAggregation(
	       				Aggregation.lookup("record_payment", "_id", "invoiceDocId", "payments"),
	       				Aggregation.match(criteria),
	       				//Aggregation.unwind("payments", false),
	       				//Aggregation.match(criteria1),
	       				Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
	       				Aggregation.limit(pageable.getPageSize())
	       			);
	       	aggCount = Aggregation.newAggregation(
        			Aggregation.lookup("record_payment", "_id", "invoiceDocId", "payments"),
        			//Aggregation.unwind("payments", false),
        			Aggregation.match(criteria)
        			//Aggregation.unwind("payments", false),
       				//Aggregation.match(criteria1)
        		);
	    }
	    
	    long total =  mongoTemplate.aggregate(aggCount, collection_name, InvoicePayments.class).getMappedResults().size();
	    
	    if (total == 0) {
			return new PageImpl<InvoicePayments>(Collections.<InvoicePayments> emptyList());
		}
	    
	    List<InvoicePayments> aggregationResult = mongoTemplate.aggregate(aggregation, collection_name, InvoicePayments.class).getMappedResults();
        return new PageImpl<InvoicePayments>(aggregationResult, pageable, total);
	}
	
	public Criteria getSearchValueCriteria(String searchval) {
		String searchVal = searchval;
		if(searchVal.contains("(")) {
			searchVal = searchVal.replaceAll("\\(", "\\\\(");
		}
		if(searchVal.contains(")")) {
			searchVal = searchVal.replaceAll("\\)", "\\\\)");
		}
		List<Criteria> criterias = new ArrayList<Criteria>();
	 	criterias.add(Criteria.where("billedtoname").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("invoiceno").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("receivedAmount").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("pendingAmount").regex(searchVal, "i"));
	 	
	 	String paymentDate = searchVal;
 		if(paymentDate.indexOf("/") != -1) {
 			String paydate[] = paymentDate.split("/");
 			String strMonth= "";
 			if(paydate.length >= 2) {
 				boolean isNumber = NumberUtils.isNumber(paydate[1]);
 				if(isNumber) {
 					int mnth = Integer.parseInt(paydate[1]);
 					strMonth = mnth < 10 && mnth > 0 ? "0" + mnth : mnth + "";
 				}
 			}
 			if(paydate.length == 2) {
 				paymentDate = paydate[0]+"/"+strMonth;
 			}else if(paydate.length == 3) {
 				paymentDate = paydate[0]+"/"+strMonth+"/"+paydate[2];
 			}
 			criterias.add(Criteria.where("dueDate").regex(paymentDate, "i"));
 			criterias.add(Criteria.where("dateofinvoice").regex(paymentDate, "i"));
 		}else {
 			criterias.add(Criteria.where("dueDate").regex(paymentDate, "i"));
 			criterias.add(Criteria.where("dateofinvoice").regex(paymentDate, "i"));
 		}
			 	
	 	return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}
	
	
	public Page<? extends InvoiceParent> findByClientidAndMonthAndYear(String clientid,int monthCd, String yearcode, String fieldName,String order,int start, int length, String searchVal){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearcode).and("paymentStatus").nin("Paid").and("gstStatus").nin("CANCELLED");
		if(monthCd > 0) {
			criteria.and("mthCd").is(monthCd+"");
		}
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0){
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, "gstr1"));
		}else {
			if(length != -1) {
				addAllInvoicesQueryFirlds(query);			
			}
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Sort sort = null;
			if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
				sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
			}
			Pageable pageable = new PageRequest((start/length), length,sort);
			query.with(pageable);
			long total = mongoTemplate.count(query, GSTR1.class,  "gstr1");
			if (total == 0) {
				return new PageImpl<GSTR1>(Collections.<GSTR1> emptyList());
			}
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class,  "gstr1"), pageable, total);
		}
	}
	
	public Page<? extends InvoiceParent> findByClientidAndFromtimeAndTotime(String clientid, Date stDate, Date endDate,String fieldName,String order,int start, int length, String searchVal){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate).and("paymentStatus").nin("Paid").and("gstStatus").nin("CANCELLED");
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		
		if(start == 0 && length == 0){
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, "gstr1"));
		}else {
			if(length != -1) {
				addAllInvoicesQueryFirlds(query);			
			}
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Sort sort = null;
			if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
				sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
			}
			Pageable pageable = new PageRequest((start/length), length,sort);
			query.with(pageable);
			long total = mongoTemplate.count(query, GSTR1.class,  "gstr1");
			if (total == 0) {
				return new PageImpl<GSTR1>(Collections.<GSTR1> emptyList());
			}
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class,  "gstr1"), pageable, total);
		}
	}
	
	public Page<? extends InvoiceParent> findByClientidAndMonthAndYearPurchaseRegister(String clientid, int monthCd,String yearcode,String fieldName,String order,int start, int length, String searchVal){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearcode).and("paymentStatus").nin("Paid").and("gstStatus").nin("CANCELLED");
		if(monthCd > 0) {
			criteria.and("mthCd").is(monthCd+"");
		}
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0){
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, "purchaseregister"));
		}else {
			if(length != -1) {
				addAllInvoicesQueryFirlds(query);			
			}
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Sort sort = null;
			if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
				sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
			}
			Pageable pageable = new PageRequest((start/length), length,sort);
			query.with(pageable);
			long total = mongoTemplate.count(query, PurchaseRegister.class,  "purchaseregister");
			if (total == 0) {
				return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
			}
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class,  "purchaseregister"), pageable, total);
		}
	}
	
	public Page<? extends InvoiceParent> findByClientidAndFromtimeAndTotimePurchaseRegister(String clientid, Date stDate, Date endDate,String fieldName,String order,int start, int length, String searchVal){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate).and("paymentStatus").nin("Paid").and("gstStatus").nin("CANCELLED");
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0){
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, "purchaseregister"));
		}else {
			if(length != -1) {
				addAllInvoicesQueryFirlds(query);			
			}
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Sort sort = null;
			if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
				sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
			}
			Pageable pageable = new PageRequest((start/length), length,sort);
			query.with(pageable);
			long total = mongoTemplate.count(query, PurchaseRegister.class,  "purchaseregister");
			if (total == 0) {
				return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
			}
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class,  "purchaseregister"), pageable, total);
		}
	}
	
	private void addAllInvoicesQueryFirlds(Query query){
		query.fields().include("clientid");
		query.fields().include("fullname");
		query.fields().include("invoiceno");
		query.fields().include("billedtoname");
		query.fields().include("dateofinvoice");
		query.fields().include("dueDate");
		query.fields().include("totalamount");
		query.fields().include("pendingAmount");
		query.fields().include("receivedAmount");
	}
	
	private ProjectionOperation getProjectionForTotal(String fieldname){
		return Aggregation.project().andInclude(fieldname)
				.and("pendingAmount").as("pendingAmount");
	}
	
	private GroupOperation getGroupForTotal(String fieldname){
		return Aggregation.group(fieldname).count().as("totalTransactions")
				.sum("pendingAmount").as("pendingAmount");
	}
	
	
	public TotalPendingAmount getTotalInvoicesAmountsForMonth(String clientid,int monthCd, String yearcode,boolean isPayment,Date fromdate, Date todate){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearcode).and("paymentStatus").nin("Paid").and("gstStatus").nin("CANCELLED");
		if(monthCd > 0) {
			criteria.and("mthCd").is(monthCd+"");
		}
		if(NullUtil.isNotEmpty(fromdate)) {
			criteria.and("dueDate").gte(fromdate).lte(todate);
		}else {
			criteria.and("dueDate").lte(todate);
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = null;
		GroupOperation groupOperation = null;
		projectionOperation = getProjectionForTotal("yrCd");
		groupOperation = getGroupForTotal("yrCd");
		AggregationResults<TotalPendingAmount> results = null;
		if(isPayment) {
			results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "purchaseregister", TotalPendingAmount.class);
		}else {
			results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "gstr1", TotalPendingAmount.class);
		}
		return results.getUniqueMappedResult();
	}
	
public TotalPendingAmount getTotalInvoicesAmountsFromTimetoEndtime(String clientid,Date stDate, Date endDate,boolean isPayment,Date fromdate, Date todate){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate).and("paymentStatus").nin("Paid").and("gstStatus").nin("CANCELLED");
		if(NullUtil.isNotEmpty(fromdate)) {
			criteria.and("dueDate").gte(fromdate).lte(todate);
		}else {
			criteria.and("dueDate").lte(todate);
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = null;
		GroupOperation groupOperation = null;
		projectionOperation = getProjectionForTotal("csftr");
		groupOperation = getGroupForTotal("csftr");
		AggregationResults<TotalPendingAmount> results = null;
		if(isPayment) {
			results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "purchaseregister", TotalPendingAmount.class);
		}else {
			results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "gstr1", TotalPendingAmount.class);
		}
		return results.getUniqueMappedResult();
	}
	
}
