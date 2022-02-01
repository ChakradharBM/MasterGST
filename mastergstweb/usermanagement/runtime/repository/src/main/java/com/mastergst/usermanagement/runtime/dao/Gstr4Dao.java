package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR4;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;


@Service
public class Gstr4Dao extends InvoiceDao{

	public Gstr4Dao(){
		super("gstr4");
	}
	
	public List<String> getBillToNames(String clientId, int month,String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCd);
		if(month>0) {
			criteria.and("mthCd").is(month+"");
		}
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	public List<TotalInvoiceAmount> getConsolidatedSummeryForYearMonthwise(final Client client, String yearCd, boolean checkQuarterly){
		
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.andInclude("mthCd", "totaltax", "totalIgstAmount", "totalCgstAmount", "totalSgstAmount");
		GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				;
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return  results.getMappedResults();
	}
public Page<GSTR4> findByClientidAndMonthAndYear(final String clientid, int month, String yearCode, int start, int length, String searchVal, InvoiceFilter filter){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode);
		if(month >0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR4>(mongoTemplate.find(query, GSTR4.class, COLLECTION_NAME));
		}else {
			addAllInvoicesQueryFirlds(query);
			/*Sort sort = null;
			if(!IbcUtils.isNullOrEmpty(fieldName) && !IbcUtils.isNullOrEmpty(order)){	
				sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
			}*/
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, GSTR4.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<GSTR4>(Collections.<GSTR4> emptyList());
			}
			return new PageImpl<GSTR4>(mongoTemplate.find(query, GSTR4.class, COLLECTION_NAME), pageable, total);
		}
	}
public TotalInvoiceAmount getTotalInvoicesAmountsForMonth(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter){
	Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode);
	if(month>0) {
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
	return results.getUniqueMappedResult();
}
private void addAllInvoicesQueryFirlds(Query query){
	query.fields().include("fullname");
	query.fields().include("gstStatus");
	query.fields().include("paymentStatus");
	query.fields().include("revchargetype");
	query.fields().include("invtype");
	query.fields().include("invoiceno");
	query.fields().include("billedtoname");
	query.fields().include("b2b");
	query.fields().include("cfs");
	query.fields().include("dateofinvoice");
	query.fields().include("totaltaxableamount");
	query.fields().include("totaltax");
	query.fields().include("totalIgstAmount");
	query.fields().include("totalCgstAmount");
	query.fields().include("totalSgstAmount");
	query.fields().include("totalamount");
	query.fields().include("branch");
	query.fields().include("vertical");
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
		criterias.add(Criteria.where("fullname").regex(searchVal, "i"));
		criterias.add(Criteria.where("paymentStatus").regex(searchVal, "i"));
		criterias.add(Criteria.where("invtype").regex(searchVal, "i"));
		criterias.add(Criteria.where("billedtoname").regex(searchVal, "i"));
		criterias.add(Criteria.where("invoiceno").regex(searchVal, "i"));
		criterias.add(Criteria.where("b2b.0.ctin").regex(searchVal, "i"));
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
			criterias.add(Criteria.where("dateofinvoice_str").regex(invdate, "i"));
		}else {
			criterias.add(Criteria.where("dateofinvoice_str").regex(searchVal, "i"));
		}
	String serForNum = searchVal;
	boolean isNumber = NumberUtils.isNumber(serForNum);
	if(!isNumber && serForNum.indexOf(",") != -1){
		serForNum = serForNum.replaceAll(",", "");
		isNumber = NumberUtils.isNumber(serForNum);
	}
	if(isNumber){
		criterias.add(Criteria.where("totaltaxableamount_str").regex(serForNum, "i"));
		criterias.add(Criteria.where("totaltax_str").regex(serForNum, "i"));
		criterias.add(Criteria.where("totalamount_str").regex(serForNum, "i"));
	}
		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
}
private ProjectionOperation getProjectionForTotal(String fieldname){
	return Aggregation.project().andInclude(fieldname)
			.and("totalamount").multiply("sftr").as("totalamount")
			.and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
			.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
			.and("totaltax").multiply("sftr").as("totaltax")
			.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
			.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
			.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
			.and("totalCessAmount").multiply("sftr").as("totalCessAmount");
}

private GroupOperation getGroupForTotal(String fieldname){
	return Aggregation.group(fieldname).count().as("totalTransactions")
			.sum("totalamount").as("totalAmount")
			.sum("totaltaxableamount").as("totalTaxableAmount")
			.sum("totalExemptedAmount").as("totalExemptedAmount")
			.sum("totaltax").as("totalTaxAmount")
			.sum("totalIgstAmount").as("totalIGSTAmount")
			.sum("totalCgstAmount").as("totalCGSTAmount")
			.sum("totalSgstAmount").as("totalSGSTAmount")
			.sum("totalCessAmount").as("totalCESSAmount");
}
private void applyFilterToCriteria(Criteria criteria, InvoiceFilter filter){
	if(filter != null){
		if(filter.getPaymentStatus() != null){
			String fieldName = "books".equals(filter.getBooksOrReturns()) ? "paymentStatus":"gstStatus";
			List<String> statusList = Arrays.asList(filter.getPaymentStatus());
			if(statusList.contains("Not Paid") || statusList.contains("Pending")){
				criteria.andOperator(new Criteria().orOperator(Criteria.where(fieldName).in(statusList), Criteria.where(fieldName).is(null)));	
			}else{
				criteria.and(fieldName).in(statusList);
			}
			
		}
		if(filter.getInvoiceType() != null){
			criteria.and("invtype").in(Arrays.asList(filter.getInvoiceType()));
		}
		if(filter.getUser() != null){
			criteria.and("fullname").in(Arrays.asList(filter.getUser()));
		}
		if(filter.getVendor() != null){
			criteria.and("billedtoname").in(Arrays.asList(filter.getVendor()));
		}
		if(filter.getBranch() != null){
			criteria.and("branch").in(Arrays.asList(filter.getBranch()));
		}
		if(filter.getVertical() != null){
			criteria.and("vertical").in(Arrays.asList(filter.getVertical()));
		}
		if(filter.getReverseCharge() != null){
			List<String> reverseChargeList = Arrays.asList(filter.getReverseCharge());
			if(reverseChargeList.contains("Regular")){
				criteria.andOperator(new Criteria().orOperator(Criteria.where("revchargetype").in(reverseChargeList), Criteria.where("revchargetype").is(null)));	
			}else{
				criteria.and("revchargetype").in(reverseChargeList);
			}
		}
	}
}	
public Map<String, Integer> getTotalInvoicesByGststatus(final String clientid, int month, int year){
	 String yearCd = getYearCode(month, year);
	Criteria criteria = Criteria.where("clientid").is(clientid).and("mthCd").is(month+"").and("yrCd").is(yearCd);
	Query query = Query.query(criteria);
	addAllInvoicesQueryFirlds(query);
	int totalInvoices = 0;
	int totalPending = 0;
	int totalUploaded = 0;
	int totalFailed = 0;
	Map<String, Integer> totalInvoicesByGststatus = Maps.newHashMap();
	List<GSTR4> gstr4Invoices = mongoTemplate.find(query, GSTR4.class, COLLECTION_NAME);
	if(NullUtil.isNotEmpty(gstr4Invoices)) {
		totalInvoices = gstr4Invoices.size();
		for(GSTR4 inv : gstr4Invoices) {
			if(NullUtil.isEmpty(inv.getGstStatus()) || inv.getGstStatus().equalsIgnoreCase("Pending")) {
				totalPending++;
			}else if(inv.getGstStatus().equalsIgnoreCase(MasterGSTConstants.GST_STATUS_SUCCESS) || inv.getGstStatus().equalsIgnoreCase(MasterGSTConstants.STATUS_FILED)) {
				totalUploaded++;
			}else {
				totalFailed++;
			}
		}
	}
	totalInvoicesByGststatus.put("totalInvoices", totalInvoices);
	totalInvoicesByGststatus.put("totalPending", totalPending);
	totalInvoicesByGststatus.put("totalUploaded", totalUploaded);
	totalInvoicesByGststatus.put("totalFailed", totalFailed);
	return totalInvoicesByGststatus;
}
}
