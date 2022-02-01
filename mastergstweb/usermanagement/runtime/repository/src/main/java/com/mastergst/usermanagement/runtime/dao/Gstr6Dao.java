package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
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
import com.mastergst.usermanagement.runtime.domain.DeliveryChallan;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR6;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.InvoiceTypeSummery;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;


@Service
public class Gstr6Dao extends InvoiceDao{

	public Gstr6Dao(){
		super("gstr6");
	}
	public TotalInvoiceAmount getTotalInvoicesAmountsForMonth(String clientId, int month, String yearCode, List<String> invtypeList, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode);
		if(month>0) {
			criteria.and("mthCd").is(month+"");
		}
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invtypeList);
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
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCd);
		if(month>0) {
			criteria.and("mthCd").is(month+"");
		}
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	public Page<GSTR6> findByClientidAndMonthAndYear(final String clientid, int month, String yearCode, List<String> invtypeList,int start, int length, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode);
		if(month>0) {
			criteria.and("mthCd").is(month+"");
		}
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invtypeList);
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
		long total = mongoTemplate.count(query, GSTR6.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR6>(Collections.<GSTR6> emptyList());
		}
		return new PageImpl<GSTR6>(mongoTemplate.find(query, GSTR6.class, COLLECTION_NAME), pageable, total);
	}
	
private void applyFilterToCriteria(Criteria criteria, InvoiceFilter filter){
	if(filter != null){
		List<Criteria> criterias = new ArrayList<Criteria>();
		if(filter.getPaymentStatus() != null){
			Criteria criteriaa = null;
			String fieldName = "books".equals(filter.getBooksOrReturns()) ? "paymentStatus":"gstStatus";
			int statusListlen = "books".equals(filter.getBooksOrReturns()) ? 3:7;
			List<String> statusList = Arrays.asList(filter.getPaymentStatus());
			if(statusList.size() < statusListlen) {
				if(statusList.contains("Pending") && statusList.contains("Failed") && statusList.size() <= 2) {
					List<String> allStatusList = Arrays.asList("Filed","Submitted","In Progress","SUCCESS","CANCELLED");
					criteriaa = new Criteria().and(fieldName).nin(allStatusList);
				}else if((statusList.contains("Not Paid") || statusList.contains("Pending")) && !statusList.contains("Failed")){
					criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where(fieldName).in(statusList), Criteria.where(fieldName).is(""),Criteria.where(fieldName).is(null)));
				}else if(statusList.contains("Failed")) {
					List<String> allStatusList = allStatusList(statusList);
					if(statusList.contains("Pending")) {
						criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where(fieldName).nin(allStatusList), Criteria.where(fieldName).is(null)));	
					}else {
						criteriaa = new Criteria().andOperator(criteria,Criteria.where(fieldName).nin(allStatusList),Criteria.where(fieldName).ne(null));
					}
				}else{
					criteria.and(fieldName).in(statusList);
				}
				if(NullUtil.isNotEmpty(criteriaa)) {
					criterias.add(criteriaa);
				}
			}
			
		}
		if(filter.getIrnStatus() != null){
			String fieldName =  "irnStatus";
			List<String> statusList = Arrays.asList(filter.getIrnStatus());
			if(statusList.contains("Not Generated") || statusList.contains(" ") || statusList.contains("Cancelled")){
				Criteria criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where(fieldName).in(statusList), Criteria.where(fieldName).is(null)));	
				criterias.add(criteriaa);
			}else{
				criteria.and(fieldName).in(statusList);
			}
			
		}
		if(filter.getInvoiceType() != null){
			List<String> invTypeList = new LinkedList<String>(Arrays.asList(filter.getInvoiceType()));
			List<String> creditDebitList = Lists.newArrayList();
			List<String> creditDebitURList = Lists.newArrayList();
			if(invTypeList.contains("Credit Note")){
				creditDebitList.add("C");
				invTypeList.remove("Credit Note");
			}
			if(invTypeList.contains("Debit Note")){
				creditDebitList.add("D");
				invTypeList.remove("Debit Note");
			}
			if(invTypeList.contains("Credit Note(UR)")){
				creditDebitURList.add("C");
				invTypeList.remove("Credit Note(UR)");
			}
			if(invTypeList.contains("Debit Note(UR)")){
				creditDebitURList.add("D");
				invTypeList.remove("Debit Note(UR)");
			}
			Criteria criteriaa = null;
			if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
				criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdnr.0.nt.0.ntty").in(creditDebitList),Criteria.where("cdnur.0.ntty").in(creditDebitURList)));
			}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
				criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("cdnr.0.nt.0.ntty").in(creditDebitList),Criteria.where("cdnur.0.ntty").in(creditDebitURList)));
			}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
				criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdnur.0.ntty").in(creditDebitURList)));
			}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isEmpty(creditDebitURList)) {
				criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdnr.0.nt.0.ntty").in(creditDebitList)));
			}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList) && NullUtil.isEmpty(creditDebitURList)) {
				criteria.and("invtype").in(invTypeList);
			}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isEmpty(creditDebitURList)) {
				criteria.and("cdnr.0.nt.0.ntty").in(creditDebitList);
			}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
				criteria.and("cdnur.0.ntty").in(creditDebitURList);
			}
			if(NullUtil.isNotEmpty(criteriaa)) {
				criterias.add(criteriaa);
			}
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
				Criteria criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("revchargetype").in(reverseChargeList), Criteria.where("revchargetype").is(null)));	
				criterias.add(criteriaa);
			}else{
				criteria.and("revchargetype").in(reverseChargeList);
			}
		}
		if(NullUtil.isNotEmpty(criterias)) {
			criteria.andOperator(criterias.toArray(new Criteria[criterias.size()]));				
		}
	}
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
	
	private void addAllInvoicesQueryFirlds(Query query){
		query.fields().include("fullname");
		query.fields().include("revchargetype");
		query.fields().include("gstStatus");
		query.fields().include("invtype");
		query.fields().include("invoiceno");
		query.fields().include("billedtoname");
		query.fields().include("b2b");
		query.fields().include("b2ba");
		query.fields().include("isd");
		query.fields().include("isda");
		query.fields().include("cdnr");
		query.fields().include("cdn");
		query.fields().include("cdna");
		query.fields().include("cdnur");
		query.fields().include("cfs");
		query.fields().include("dateofinvoice");
		query.fields().include("totaltaxableamount");
		query.fields().include("totaltax");
		query.fields().include("totalIgstAmount");
		query.fields().include("totalCgstAmount");
		query.fields().include("totalSgstAmount");
		query.fields().include("totalCessAmount");
		query.fields().include("totalamount");
		query.fields().include("branch");
		query.fields().include("vertical");
	}
	public List<TotalInvoiceAmount> getConsolidatedSummeryForYearMonthwise(final Client client, String yearCd, boolean checkQuarterly){
		
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.andInclude("mthCd", "totalitc", "totalIgstAmount", "totalCgstAmount", "totalSgstAmount");
		GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("totalitc").as("totalTaxAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				;
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return  results.getMappedResults();
	}
	public List<InvoiceTypeSummery> getInvoiceSummeryByTypeForMonth(final String clientid, int month, String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCd);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
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
		List<InvoiceTypeSummery> totalCanInvAmount = getCancelledInvoiceSummeryByTypeForMonth(clientid,month,yearCd);
		List<InvoiceTypeSummery> totalInvAmount = results.getMappedResults();
		for(InvoiceTypeSummery gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(InvoiceTypeSummery gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
				}
			}
		}
		return  totalInvAmount;
	}
	public List<InvoiceTypeSummery> getCancelledInvoiceSummeryByTypeForMonth(final String clientid, int month, String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCd).and("gstStatus").is("CANCELLED");
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
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
		List<GSTR6> gstr6Invoices = mongoTemplate.find(query, GSTR6.class, COLLECTION_NAME);
		if(NullUtil.isNotEmpty(gstr6Invoices)) {
			totalInvoices = gstr6Invoices.size();
			for(GSTR6 inv : gstr6Invoices) {
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
	public Page<? extends InvoiceParent> getGSTR6ReturnInvoices(String clientid, String userid, List<String> invtypeLst,int month, String yearCd,int start,int length) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("invtype").in(invtypeLst).and("mthCd").is(month+"").and("yrCd").is(yearCd);
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		long total = mongoTemplate.count(query, GSTR6.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR6>(Collections.<GSTR6> emptyList());
		}
		return new PageImpl<GSTR6>(mongoTemplate.find(query, GSTR6.class, COLLECTION_NAME));
	}
	public Page<? extends InvoiceParent> getGSTR6B2BInvoices(final String clientid, int month, String yearCode, String invtype) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("mthCd").is(month+"").and("yrCd").is(yearCode).and("invtype").is(invtype);
		Query query = Query.query(criteria);
		long total = mongoTemplate.count(query, GSTR6.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR6>(Collections.<GSTR6> emptyList());
		}
		return new PageImpl<GSTR6>(mongoTemplate.find(query, GSTR6.class, COLLECTION_NAME));
	}
	public Page<? extends InvoiceParent> getGSTR6ISDInvoices(final String clientid, int month, String yearCode, List<String> invtype) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("mthCd").is(month+"").and("yrCd").is(yearCode).and("invtype").in(invtype);
		Query query = Query.query(criteria);
		long total = mongoTemplate.count(query, GSTR6.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR6>(Collections.<GSTR6> emptyList());
		}
		return new PageImpl<GSTR6>(mongoTemplate.find(query, GSTR6.class, COLLECTION_NAME));
	}

}
