package com.mastergst.usermanagement.runtime.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.domain.PaymentFilter;
import com.mastergst.usermanagement.runtime.domain.Payments;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.domain.TotalPaymentsAmount;

@Component
public class PaymentsDao {
	
	private final String COLLECTION_NAME ="record_payment";
	@Autowired
	protected MongoTemplate mongoTemplate;
	
	public Page<Payments> getPaymentInvoices(String clientid, List<String> returnTypes, int start, int length, String searchVal, PaymentFilter filter) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("returntype").in(returnTypes);
		
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(length != -1) {
			addAllInvoicesQueryFirlds(query);			
		}
		if(length == -1) {
			length =Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start/length), length);
		
		query.with(pageable);
		long total = mongoTemplate.count(query, Payments.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<Payments>(Collections.<Payments> emptyList());
		}
		return new PageImpl<Payments>(mongoTemplate.find(query, Payments.class, COLLECTION_NAME), pageable, total);
	}

	public TotalPaymentsAmount getPaymentsConsolidatedTotalAmountSummery(String clientid, String returnType, PaymentFilter filter) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("returntype").is(returnType);
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project()
				.and("cashAmount").as("cashAmount")
				.and("bankAmount").as("bankAmount")
				.and("tdsItAmount").as("tdsItAmount")
				.and("tdsGstAmount").as("tdsGstAmount")
				.and("discountAmount").as("discountAmount")
				.and("othersAmount").as("othersAmount")
				.and("paidAmount").as("paidAmount");
		
		GroupOperation groupOperation = Aggregation.group().count().as("totalTransactions")
				.sum("paidAmount").as("totalPaidAmount")
				.sum("cashAmount").as("totalCashAmount")
				.sum("bankAmount").as("totalBankAmount")
				.sum("tdsItAmount").as("totalTdsItAmount")
				.sum("tdsGstAmount").as("totalTdsGstAmount")
				.sum("discountAmount").as("totalDiscountAmount")
				.sum("othersAmount").as("totalOthersAmount");
		
		AggregationResults<TotalPaymentsAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalPaymentsAmount.class);
		TotalPaymentsAmount paymentsAmount = new TotalPaymentsAmount();
		for(TotalPaymentsAmount amts : results.getMappedResults()) {
			paymentsAmount.setTotalTransactions(paymentsAmount.getTotalTransactions() + amts.getTotalTransactions());
			paymentsAmount.setTotalCashAmount(paymentsAmount.getTotalCashAmount().add(amts.getTotalCashAmount()));
			paymentsAmount.setTotalBankAmount(paymentsAmount.getTotalBankAmount().add(amts.getTotalBankAmount()));
			paymentsAmount.setTotalTdsItAmount(paymentsAmount.getTotalTdsItAmount().add(amts.getTotalTdsItAmount()));
			paymentsAmount.setTotalTdsGstAmount(paymentsAmount.getTotalTdsGstAmount().add(amts.getTotalTdsGstAmount()));
			paymentsAmount.setTotalDiscountAmount(paymentsAmount.getTotalDiscountAmount().add(amts.getTotalDiscountAmount()));
			paymentsAmount.setTotalOthersAmount(paymentsAmount.getTotalOthersAmount().add(amts.getTotalOthersAmount()));
			paymentsAmount.setTotalPaidAmount(paymentsAmount.getTotalPaidAmount().add(amts.getTotalPaidAmount()));
		}
		return  paymentsAmount;
	}
	
	public TotalInvoiceAmount getConsolidatedTotalAmountSummery(String clientid, String returnType, PaymentFilter filter) {
		Criteria criteria = Criteria.where("clientid").is(clientid);
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project()
				.and("totalamount").as("totalamount");
		GroupOperation groupOperation = Aggregation.group()
				.sum("totalamount").as("totalAmount");
		
		AggregationResults<TotalInvoiceAmount> results = null;
		if(returnType.equals(MasterGSTConstants.GSTR2)) {
			results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "purchaseregister", TotalInvoiceAmount.class);
		}else {
			results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "gstr1", TotalInvoiceAmount.class);						
		}
		
		TotalInvoiceAmount invAmts = new TotalInvoiceAmount();
		
		for(TotalInvoiceAmount amts : results.getMappedResults()) {
			invAmts.setTotalAmount(amts.getTotalAmount());
		}
		
		return invAmts;
	}
	
	private void addAllInvoicesQueryFirlds(Query query){
		query.fields().include("userid");
		query.fields().include("clientid");
		query.fields().include("invoiceid");
		query.fields().include("voucherNumber");
		query.fields().include("invoiceNumber");
		query.fields().include("invoiceno");
		query.fields().include("customerName");
		query.fields().include("gstNumber");
		query.fields().include("paymentitems");
		query.fields().include("paymentDate");
		query.fields().include("pendingBalance");
		query.fields().include("returntype");
		query.fields().include("receivedAmount");
		
		query.fields().include("paidAmount");
		query.fields().include("cashAmount");
		query.fields().include("bankAmount");
		query.fields().include("tdsItAmount");
		query.fields().include("tdsGstAmount");
		query.fields().include("discountAmount");
		query.fields().include("othersAmount");
				
		query.fields().include("invtype");
	}
	
	private Criteria getSearchValueCriteria(final String searchval){
		String searchVal = searchval;
		if(searchVal.contains("(")) {
			searchVal = searchVal.replaceAll("\\(", "\\\\(");
		}
		if(searchVal.contains(")")) {
			searchVal = searchVal.replaceAll("\\)", "\\\\)");
		}
		List<Criteria> criterias = new ArrayList<Criteria>();
 		criterias.add(Criteria.where("voucherNumber").regex(searchVal, "i"));
 		
 		criterias.add(Criteria.where("invoiceNumber").regex(searchVal, "i"));
 		criterias.add(Criteria.where("customerName").regex(searchVal, "i"));
 		criterias.add(Criteria.where("invoiceno").regex(searchVal, "i"));
 		criterias.add(Criteria.where("gstNumber").regex(searchVal, "i"));
 		criterias.add(Criteria.where("paymentitems.0.modeOfPayment").regex(searchVal, "i"));
 		
 		String invdate = searchVal;
 		if(invdate.indexOf("/") != -1) {
 			String invoicedate[] = invdate.split("/");
 			String strMonth= "";
 			if(invoicedate.length >= 2) {
 				boolean isNumber = NumberUtils.isNumber(invoicedate[1]);
 				if(isNumber) {
 					int mnth = Integer.parseInt(invoicedate[1]);
 					strMonth = mnth < 10 && mnth > 0 ? "0" + mnth : mnth + "";
 				}
 			}

 			if(invoicedate.length == 2) {
 				invdate = invoicedate[0]+"/"+strMonth;
 			}else if(invoicedate.length == 3) {
 				invdate = invoicedate[0]+"/"+strMonth+"/"+invoicedate[2];
 			}
 			criterias.add(Criteria.where("paymentDate").regex(invdate, "i"));
 		}else {
 			criterias.add(Criteria.where("paymentDate").regex(searchVal, "i"));
 		}
		
		String serForNum = searchVal;
		boolean isNumber = NumberUtils.isNumber(serForNum);
		if(!isNumber && serForNum.indexOf(",") != -1){
			serForNum = serForNum.replaceAll(",", "");
			isNumber = NumberUtils.isNumber(serForNum);
		}
		if(isNumber){
			criterias.add(Criteria.where("receivedAmount").regex(serForNum, "i"));
		}
 		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}

	private void applyFilterToCriteria(Criteria criteria, PaymentFilter filter){
		if(filter != null){
			if(filter.getFinancialYear() != null){
				criteria.and("yrCd").in(Arrays.asList(filter.getFinancialYear()));
			}
			if(filter.getMonth() != null){
				criteria.and("mthCd").in(Arrays.asList(filter.getMonth()));
			}
			if(filter.getVendor() != null){
				criteria.and("customerName").in(Arrays.asList(filter.getVendor()));
			}
			if(filter.getGstno() != null){
				criteria.and("gstNumber").in(Arrays.asList(filter.getGstno()));
			}
			if(filter.getPaymentMode() != null){
				criteria.and("paymentitems.0.modeOfPayment").in(Arrays.asList(filter.getPaymentMode()));
			}
		}
	}

	public List<String> getBilledToNameByClientidAndReturnType(String clientid, String returntype) {
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("returntype").is(returntype);
		
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customerName", query.getQueryObject());
		return (List<String>)billToNames;
	}

	public List<String> getBilledToGstinByClientidAndReturnType(String clientid, String returntype) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("returntype").is(returntype);
		Query query = Query.query(criteria);
		List billedToGstin = mongoTemplate.getCollection(COLLECTION_NAME).distinct("gstNumber", query.getQueryObject());
		return (List<String>)billedToGstin;
	}

}
