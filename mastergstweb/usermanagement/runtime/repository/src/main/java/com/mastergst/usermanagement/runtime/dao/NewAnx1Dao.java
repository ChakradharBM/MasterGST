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

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceTypeSummery;
import com.mastergst.usermanagement.runtime.domain.NewAnx1;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;

@Service
public class NewAnx1Dao extends InvoiceDao{

	public NewAnx1Dao(){
		super("newAnx1");
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmountsForMonth(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode);
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
		return results.getUniqueMappedResult();
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmountsForFp(String clientId, String fp, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("fp").is(fp);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("fp");
		GroupOperation groupOperation = getGroupForTotal("fp");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
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
	
	public List<String> getBillToNames(String clientId, int month,String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("mthCd").is(month+"").and("yrCd").is(yearCd);
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmounts(String clientId, List<String> invTypes, String fp, String searchVal){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("invtype").in(invTypes).and("fp").is(fp);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("mthCd");
		GroupOperation groupOperation = getGroupForTotal("mthCd");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	}
	
	public Page<NewAnx1> findByClientidAndMonthAndYear(final String clientid, int month, String yearCode, int start, int length, String searchVal, InvoiceFilter filter){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
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
		long total = mongoTemplate.count(query, NewAnx1.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<NewAnx1>(Collections.<NewAnx1> emptyList());
		}
		return new PageImpl<NewAnx1>(mongoTemplate.find(query, NewAnx1.class, COLLECTION_NAME), pageable, total);
	}
	
	public List<String> allStatusList(List<String> statusList){
		List<String> allStatusList = Lists.newArrayList();
		allStatusList.add("Filed");
		allStatusList.add("Submitted");
		allStatusList.add("In Progress");
		allStatusList.add("CANCELLED");
		allStatusList.add("SUCCESS");
		allStatusList.add("Failed");
		allStatusList.add("Pending");
		allStatusList.removeAll(statusList);
		return allStatusList;
		
	}
	
	private void applyFilterToCriteria(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			if(filter.getPaymentStatus() != null){
				String fieldName = "books".equals(filter.getBooksOrReturns()) ? "paymentStatus":"gstStatus";
				int statusListlen = "books".equals(filter.getBooksOrReturns()) ? 3:7;
				List<String> statusList = Arrays.asList(filter.getPaymentStatus());
				if(statusList.size() < statusListlen) {
					if(statusList.contains("Pending") && statusList.contains("Failed") && statusList.size() <= 2) {
						List<String> allStatusList = Arrays.asList("Filed","Submitted","In Progress","SUCCESS","CANCELLED");
						criteria.and(fieldName).nin(allStatusList);
					}else if((statusList.contains("Not Paid") || statusList.contains("Pending")) && !statusList.contains("Failed")){
						criteria.andOperator(new Criteria().orOperator(Criteria.where(fieldName).in(statusList), Criteria.where(fieldName).is(null)));	
					}else if(statusList.contains("Failed")) {
						List<String> allStatusList = allStatusList(statusList);
						if(statusList.contains("Pending")) {
							criteria.andOperator(new Criteria().orOperator(Criteria.where(fieldName).nin(allStatusList), Criteria.where(fieldName).is(null)));	
						}else {
							criteria.andOperator(Criteria.where(fieldName).nin(allStatusList),Criteria.where(fieldName).ne(null));
						}
					}else{
						criteria.and(fieldName).in(statusList);
					}
				}
				
			}
			if(filter.getIrnStatus() != null){
				String fieldName =  "irnStatus";
				List<String> statusList = Arrays.asList(filter.getIrnStatus());
				if(statusList.contains("Not Generated") || statusList.contains(" ") || statusList.contains("Cancelled")){
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
				
				//criteria.and("revchargetype").in(Arrays.asList(filter.getReverseCharge()));
			}
		}
	}
	
	public Page<NewAnx1> findByClientidAndInvtypeInAndFp(final String clientId, final List<String> invTypes, String fp, int start, int length, String searchVal){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("invtype").in(invTypes).and("fp").is(fp);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		/*Sort sort = null;
		if(!IbcUtils.isNullOrEmpty(fieldName) && !IbcUtils.isNullOrEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}*/
		Pageable pageable = new PageRequest(start, length);
		query.with(pageable);
		long total = mongoTemplate.count(query, NewAnx1.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<NewAnx1>(Collections.<NewAnx1> emptyList());
		}
		return new PageImpl<NewAnx1>(mongoTemplate.find(query, NewAnx1.class, COLLECTION_NAME), pageable, total);
	}
	
	public Page<NewAnx1> findByClientidAndFp(final String clientId, String fp, int start, int length, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("fp").is(fp);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
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
		long total = mongoTemplate.count(query, NewAnx1.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<NewAnx1>(Collections.<NewAnx1> emptyList());
		}
		return new PageImpl<NewAnx1>(mongoTemplate.find(query, NewAnx1.class, COLLECTION_NAME), pageable, total);
	}
	
	private Criteria getSearchValueCriteria(final String searchVal){
		
		List<Criteria> criterias = new ArrayList<Criteria>();
 		criterias.add(Criteria.where("fullname").regex(searchVal, "i"));
 		if(searchVal.toUpperCase().startsWith("PE")) {
 			criterias.add(new Criteria().orOperator(Criteria.where("gstStatus").is(""), Criteria.where("gstStatus").is(null)));
 		}else if(searchVal.toUpperCase().startsWith("UP") || searchVal.startsWith("Up")) {
 			criterias.add(Criteria.where("gstStatus").regex(MasterGSTConstants.GST_STATUS_SUCCESS, "i"));
 		}else {
 			criterias.add(Criteria.where("gstStatus").regex(searchVal, "i"));
 		}
 		
 		if(searchVal.toUpperCase().startsWith("NO")) {
 			criterias.add(new Criteria().orOperator(Criteria.where("paymentStatus").is(""), Criteria.where("paymentStatus").is(null)));
 		}else {
 			criterias.add(Criteria.where("paymentStatus").regex(searchVal, "i"));
 		}
 		
 		
 		criterias.add(Criteria.where("invtype").regex(searchVal, "i"));
 		criterias.add(Criteria.where("billedtoname").regex(searchVal, "i"));
 		criterias.add(Criteria.where("invoiceno").regex(searchVal, "i"));
 		criterias.add(Criteria.where("b2b.0.ctin").regex(searchVal, "i"));
 		criterias.add(Criteria.where("irnNo").regex(searchVal, "i"));
		criterias.add(Criteria.where("irnStatus").regex(searchVal, "i"));
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
	
	private void addAllInvoicesQueryFirlds(Query query){
		query.fields().include("fullname");
		query.fields().include("gstStatus");
		query.fields().include("paymentStatus");
		query.fields().include("revchargetype");
		query.fields().include("invtype");
		query.fields().include("invoiceno");
		query.fields().include("billedtoname");
		query.fields().include("b2b");
		query.fields().include("anxb2b");
		query.fields().include("de");
		query.fields().include("sezwp");
		query.fields().include("sezwop");
		query.fields().include("expwp");
		query.fields().include("expwop");
		query.fields().include("b2c");
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
		query.fields().include("irnStatus");
		query.fields().include("irnNo");
	}
	
	public List<TotalInvoiceAmount> getConsolidatedSummeryForYearMonthwise(final Client client, String yearCd, boolean checkQuarterly){
		
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.andInclude("mthCd","tcstdsAmount", "totaltax", "totalIgstAmount", "totalCgstAmount", "totalSgstAmount", "totalExemptedAmount");
		GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalExemptedAmount").as("totalExemptedAmount")
				;
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return  results.getMappedResults();
	}
	
	public List<InvoiceTypeSummery> getInvoiceSummeryByTypeForMonth(final String clientid, int month, String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("mthCd").is(month+"").and("yrCd").is(yearCd);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totalamount").multiply("sftr").as("totalamount")
				.and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.andInclude("invtype");
		GroupOperation groupOperation = Aggregation.group("invtype")
				.count().as("noOfInvoices")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totaltax").as("totalTaxAmount")
				;
		AggregationResults<InvoiceTypeSummery> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, InvoiceTypeSummery.class);
		return  results.getMappedResults();
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
		List<GSTR1> gstr1Invoices = mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME);
		if(NullUtil.isNotEmpty(gstr1Invoices)) {
			totalInvoices = gstr1Invoices.size();
			for(GSTR1 inv : gstr1Invoices) {
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
