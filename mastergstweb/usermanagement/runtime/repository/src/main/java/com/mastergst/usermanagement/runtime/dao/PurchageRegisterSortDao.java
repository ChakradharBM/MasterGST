package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
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

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.InvoiceTypeSummery;
import com.mastergst.usermanagement.runtime.domain.MismatchInvCount;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;

@Service
public class PurchageRegisterSortDao  extends InvoiceDao{

	public PurchageRegisterSortDao(){
		super("purchaseregister");
	}
	
	public List<TotalInvoiceAmount> getConsolidatedSummeryForYearMonthwise(final Client client, String yearCd, boolean checkQuarterly){
		
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totalitc").multiply("sftr").as("totalitc")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount")
				.and("tdsAmount").multiply("sftr").as("tdsAmount")
				.andInclude("mthCd");
		GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totalitc").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("tdsAmount").as("tdsAmount");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedSummeryForYearMonthwise(client,yearCd,checkQuarterly);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
					gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
					gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
					gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
					gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
					gstr1InvoiceAmount.setTotalITCAvailable(gstr1InvoiceAmount.getTotalITCAvailable().subtract(gstr1CancelledInvoiceAmount.getTotalITCAvailable()));
					gstr1InvoiceAmount.setTcsTdsAmount(gstr1InvoiceAmount.getTcsTdsAmount().subtract(gstr1CancelledInvoiceAmount.getTcsTdsAmount()));
					gstr1InvoiceAmount.setTdsAmount(gstr1InvoiceAmount.getTdsAmount().subtract(gstr1CancelledInvoiceAmount.getTdsAmount()));
				}
			}
		}
		return  totalInvAmount;
	}
	
	public List<String> getBillToNames(String clientId, int month, String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCd);
		if(month>0) {
			criteria.and("mthCd").is(month+"");
		}
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
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
		TotalInvoiceAmount cancelInvAmount = new TotalInvoiceAmount();
		if(filter != null) {
			cancelInvAmount = getTotalInvoicesAmountsForMonthCancelled(clientId,month,yearCode,searchVal,filter);
			if(filter.getPaymentStatus() != null && filter.getPaymentStatus().length == 1) {
				List<String> statusList = Arrays.asList(filter.getPaymentStatus());
				if(statusList.contains("CANCELLED")) {
					return cancelInvAmount;
				}
			}
		}
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
		if(totalnvAmount != null && cancelInvAmount != null) {
			totalInvAmount.setTotalTransactions(totalnvAmount.getTotalTransactions());
			totalInvAmount.setTotalTaxableAmount(totalnvAmount.getTotalTaxableAmount().subtract(cancelInvAmount.getTotalTaxableAmount()));
			totalInvAmount.setTotalExemptedAmount(totalnvAmount.getTotalExemptedAmount().subtract(cancelInvAmount.getTotalExemptedAmount()));
			totalInvAmount.setTotalTaxAmount(totalnvAmount.getTotalTaxAmount().subtract(cancelInvAmount.getTotalTaxAmount()));
			totalInvAmount.setTotalAmount(totalnvAmount.getTotalAmount().subtract(cancelInvAmount.getTotalAmount()));
			totalInvAmount.setTotalIGSTAmount(totalnvAmount.getTotalIGSTAmount().subtract(cancelInvAmount.getTotalIGSTAmount()));
			totalInvAmount.setTotalCGSTAmount(totalnvAmount.getTotalCGSTAmount().subtract(cancelInvAmount.getTotalCGSTAmount()));
			totalInvAmount.setTotalSGSTAmount(totalnvAmount.getTotalSGSTAmount().subtract(cancelInvAmount.getTotalSGSTAmount()));
			totalInvAmount.setTotalCESSAmount(totalnvAmount.getTotalCESSAmount().subtract(cancelInvAmount.getTotalCESSAmount()));
			totalInvAmount.setTotalITCAvailable(totalnvAmount.getTotalITCAvailable().subtract(cancelInvAmount.getTotalITCAvailable()));
			totalInvAmount.setTcsTdsAmount(totalnvAmount.getTcsTdsAmount().subtract(cancelInvAmount.getTcsTdsAmount()));
			totalInvAmount.setTdsAmount(totalnvAmount.getTdsAmount().subtract(cancelInvAmount.getTdsAmount()));
			return totalInvAmount;
		}else {
			return totalnvAmount;
		}
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmountsForBillDateMonth(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("trDateyrCd").is(yearCode);
		if(month>0) {
			criteria.and("trDatemthCd").is(month+"");
		}
		
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = null;
		GroupOperation groupOperation = null;
		if(month > 0) {
			projectionOperation = getProjectionForTotal("trDatemthCd");
			groupOperation = getGroupForTotal("trDatemthCd");
		}else {
			projectionOperation = getProjectionForTotal("trDateyrCd");
			groupOperation = getGroupForTotal("trDateyrCd");
		}
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		TotalInvoiceAmount cancelInvAmount = new TotalInvoiceAmount();
		if(filter != null) {
			cancelInvAmount = getTotalInvoicesAmountsForMonthCancelled(clientId,month,yearCode,searchVal,filter);
			if(filter.getPaymentStatus() != null && filter.getPaymentStatus().length == 1) {
				List<String> statusList = Arrays.asList(filter.getPaymentStatus());
				if(statusList.contains("CANCELLED")) {
					return cancelInvAmount;
				}
			}
		}
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
		if(totalnvAmount != null && cancelInvAmount != null) {
			totalInvAmount.setTotalTransactions(totalnvAmount.getTotalTransactions());
			totalInvAmount.setTotalTaxableAmount(totalnvAmount.getTotalTaxableAmount().subtract(cancelInvAmount.getTotalTaxableAmount()));
			totalInvAmount.setTotalExemptedAmount(totalnvAmount.getTotalExemptedAmount().subtract(cancelInvAmount.getTotalExemptedAmount()));
			totalInvAmount.setTotalTaxAmount(totalnvAmount.getTotalTaxAmount().subtract(cancelInvAmount.getTotalTaxAmount()));
			totalInvAmount.setTotalAmount(totalnvAmount.getTotalAmount().subtract(cancelInvAmount.getTotalAmount()));
			totalInvAmount.setTotalIGSTAmount(totalnvAmount.getTotalIGSTAmount().subtract(cancelInvAmount.getTotalIGSTAmount()));
			totalInvAmount.setTotalCGSTAmount(totalnvAmount.getTotalCGSTAmount().subtract(cancelInvAmount.getTotalCGSTAmount()));
			totalInvAmount.setTotalSGSTAmount(totalnvAmount.getTotalSGSTAmount().subtract(cancelInvAmount.getTotalSGSTAmount()));
			totalInvAmount.setTotalCESSAmount(totalnvAmount.getTotalCESSAmount().subtract(cancelInvAmount.getTotalCESSAmount()));
			totalInvAmount.setTotalITCAvailable(totalnvAmount.getTotalITCAvailable().subtract(cancelInvAmount.getTotalITCAvailable()));
			totalInvAmount.setTcsTdsAmount(totalnvAmount.getTcsTdsAmount().subtract(cancelInvAmount.getTcsTdsAmount()));
			totalInvAmount.setTdsAmount(totalnvAmount.getTdsAmount().subtract(cancelInvAmount.getTdsAmount()));
			return totalInvAmount;
		}else {
			return totalnvAmount;
		}
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
 		criterias.add(Criteria.where("customField1").regex(searchVal, "i"));
		criterias.add(Criteria.where("customField2").regex(searchVal, "i"));
		criterias.add(Criteria.where("customField3").regex(searchVal, "i"));
		criterias.add(Criteria.where("customField4").regex(searchVal, "i"));
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
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("totalitc").multiply("sftr").as("totalitc")
				.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount")
				.and("tdsAmount").multiply("sftr").as("tdsAmount");
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
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("totalitc").as("totalITCAvailable")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("tdsAmount").as("tdsAmount");
	}
	
	public Page<PurchaseRegister> findByClientidAndMonthAndYear(final String clientid, int month, String yearCode, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode);
		if(month>0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME));
		}else {
			
			addAllInvoicesQueryFirlds(query);
			Sort sort = null;
			if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
				sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
			}
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length,sort);
			query.with(pageable);
			long total = mongoTemplate.count(query, PurchaseRegister.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
			}
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME), pageable, total);
		}
	}
	public Page<PurchaseRegister> findByClientidAndBillDateMonthAndYear(final String clientid, int month, String yearCode, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("trDateyrCd").is(yearCode);
		if(month>0) {
			criteria.and("trDatemthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		Sort sort = null;
		if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start/length), length,sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, PurchaseRegister.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
		}
		return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME), pageable, total);
	}
	
	private void applyFilterToCriteria(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			List<Criteria> criterias = new ArrayList<Criteria>();
			if(filter.getPaymentStatus() != null){
				Criteria criteriaa = null;
				String fieldName = "books".equals(filter.getBooksOrReturns()) ? "paymentStatus":"gstStatus";
				List<String> statusList = Arrays.asList(filter.getPaymentStatus());
				if(statusList.contains("Not Paid") || statusList.contains("Pending")){
					criteriaa = new Criteria().andOperator(new Criteria().orOperator(Criteria.where(fieldName).in(statusList),Criteria.where(fieldName).is(""), Criteria.where(fieldName).is(null)));	
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
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList),Criteria.where("cdnur.0.ntty").in(creditDebitURList)));
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList),Criteria.where("cdnur.0.ntty").in(creditDebitURList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdnur.0.ntty").in(creditDebitURList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isEmpty(creditDebitURList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList) && NullUtil.isEmpty(creditDebitURList)) {
					criteria.and("invtype").in(invTypeList);
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isEmpty(creditDebitURList)) {
					criteria.and("cdn.0.nt.0.ntty").in(creditDebitList);
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
				if(reverseChargeList.contains("Regular")) {
					Criteria criteriaa = new Criteria().andOperator(new Criteria().orOperator(Criteria.where("revchargetype").in(Arrays.asList(filter.getReverseCharge())),Criteria.where("revchargetype").is(""), Criteria.where("revchargetype").is(null)));
					criterias.add(criteriaa);
				}else {
					criteria.and("revchargetype").in(reverseChargeList);
				}
			}
			if(filter.getGstno() != null){
				criteria.and("b2b.0.ctin").in(Arrays.asList(filter.getGstno()));
			}
			if(filter.getDateofInvoice() != null){
				criteria.and("dateofinvoice_str").in(Arrays.asList(filter.getDateofInvoice()));
			}
			if(filter.getInvoiceno() != null){
				criteria.and("invoiceno").in(Arrays.asList(filter.getInvoiceno()));
			}
			/*if(filter.getCustomFieldText1() != null){
				//Criteria criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("customField1").in(Arrays.asList(filter.getCustomField1())),Criteria.where("customField2").in(Arrays.asList(filter.getCustomField1())),Criteria.where("customField3").in(Arrays.asList(filter.getCustomField1())),Criteria.where("customField4").in(Arrays.asList(filter.getCustomField1()))));	
				Criteria criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("customFieldText1").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText2").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText3").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText4").in(Arrays.asList(filter.getCustomFieldText1()))));
				criterias.add(criteriaa);
			}*/
			if(filter.getCustomFieldText1() != null){
				criteria.and("customField1").in(Arrays.asList(filter.getCustomFieldText1()));
			}
			if(filter.getCustomFieldText2() != null){
				criteria.and("customField2").in(Arrays.asList(filter.getCustomFieldText2()));
			}
			if(filter.getCustomFieldText3() != null){
				criteria.and("customField3").in(Arrays.asList(filter.getCustomFieldText3()));
			}
			if(filter.getCustomFieldText4() != null){
				criteria.and("customField4").in(Arrays.asList(filter.getCustomFieldText4()));
			}
			if(filter.getReconStatus() != null){
				Criteria criteriaa = null;
				List<String> statusList = Arrays.asList(filter.getReconStatus());
				if(statusList.contains("Not In GSTR 2A")){
					criteriaa = new Criteria().andOperator(new Criteria().orOperator(Criteria.where("matchingStatus").in(statusList),Criteria.where("matchingStatus").is(""), Criteria.where("matchingStatus").is(null)));	
					criterias.add(criteriaa);
				}else{
					criteria.and("matchingStatus").in(statusList);
				}
			}
			if(NullUtil.isNotEmpty(criterias)) {
				criteria.andOperator(criterias.toArray(new Criteria[criterias.size()]));				
			}
		}
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
		query.fields().include("cdn");
		query.fields().include("cdnur");
		query.fields().include("impGoods");
		query.fields().include("dateofinvoice");
		query.fields().include("totaltaxableamount");
		query.fields().include("totaltax");
		query.fields().include("totalIgstAmount");
		query.fields().include("totalCgstAmount");
		query.fields().include("totalSgstAmount");
		query.fields().include("totalamount");
		query.fields().include("pendingAmount");
		query.fields().include("receivedAmount");
		query.fields().include("totalitc");
		query.fields().include("branch");
		query.fields().include("vertical");
		query.fields().include("matchingStatus");
		query.fields().include("dateofitcClaimed");
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
	
	public List<MismatchInvCount> getNoOfMismatches(List<String> clientIdStr, int month, int year){
        String yearCd = getYearCode(month, year);
        Criteria criteria = Criteria.where("clientid").in(clientIdStr).and("mthCd").is(month+"").and("yrCd").is(yearCd).and("matchingStatus").ne("Matched");
        MatchOperation matchOperation = Aggregation.match(criteria);
        AggregationResults<MismatchInvCount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, Aggregation.group("clientid").count().as("mismatchCount")), COLLECTION_NAME, MismatchInvCount.class);
        List<MismatchInvCount> resultMap = results.getMappedResults();
        return resultMap;
    }
	
	public String getTotalTaxField(){
		return "totalitc";
	}
	
	public Page<PurchaseRegister> findByClientidAndMonthAndYearForUnclimed(final String clientid, int month, String yearCode, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter){
		 List<String> mnthlist = monthList(month);
		//Criteria criteria = Criteria.where("clientid").is(clientid).and("mthCd").in(mnthlist).and("yrCd").is(yearCode).and("items").elemMatch(Criteria.where("elg").is(null));
		Criteria criteria = Criteria.where("clientid").is(clientid).and("mthCd").in(mnthlist).and("yrCd").is(yearCode);
		criteria.andOperator(new Criteria().orOperator(Criteria.where("dateofitcClaimed").is(""),Criteria.where("dateofitcClaimed").is(null),Criteria.where("items").elemMatch(Criteria.where("elg").is(null)),Criteria.where("items").elemMatch(Criteria.where("elg").is(""))));
		if(NullUtil.isNotEmpty(filter) && NullUtil.isEmpty(filter.getInvoiceType())) {
			criteria.and("invtype").nin(MasterGSTConstants.ADVANCES, MasterGSTConstants.ATPAID, MasterGSTConstants.ITC_REVERSAL, MasterGSTConstants.NIL);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Sort sort = null;
		if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}
		Pageable pageable = new PageRequest((start/length), length,sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, PurchaseRegister.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
		}
		return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME), pageable, total);
	}
	
	public Page<PurchaseRegister> findByClientidAndMonthAndYearForUnclimedTransactiondate(final String clientid, int month, String yearCode, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter){
		 List<String> mnthlist = monthList(month);
		//Criteria criteria = Criteria.where("clientid").is(clientid).and("trDatemthCd").in(mnthlist).and("trDateyrCd").is(yearCode).and("items").elemMatch(Criteria.where("elg").is(null));
		Criteria criteria = Criteria.where("clientid").is(clientid).and("trDatemthCd").in(mnthlist).and("trDateyrCd").is(yearCode);
		criteria.andOperator(new Criteria().orOperator(Criteria.where("dateofitcClaimed").is(""),Criteria.where("dateofitcClaimed").is(null),Criteria.where("items").elemMatch(Criteria.where("elg").is(null)),Criteria.where("items").elemMatch(Criteria.where("elg").is(""))));
		if(NullUtil.isNotEmpty(filter) && NullUtil.isEmpty(filter.getInvoiceType())) {
			criteria.and("invtype").nin(MasterGSTConstants.ADVANCES, MasterGSTConstants.ATPAID, MasterGSTConstants.ITC_REVERSAL, MasterGSTConstants.NIL);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Sort sort = null;
		if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}
		Pageable pageable = new PageRequest((start/length), length,sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, PurchaseRegister.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
		}
		return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME), pageable, total);
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmountsForMonthForUnclimed(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter){
		List<String> mnthlist = monthList(month);
		Criteria criteria = Criteria.where("clientid").is(clientId).and("mthCd").in(mnthlist).and("yrCd").is(yearCode)
				.and("items").elemMatch(Criteria.where("elg").is(null));
		if(NullUtil.isNotEmpty(filter) && NullUtil.isEmpty(filter.getInvoiceType())) {
			criteria.and("invtype").nin(MasterGSTConstants.ADVANCES, MasterGSTConstants.ATPAID, MasterGSTConstants.ITC_REVERSAL, MasterGSTConstants.NIL);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("yrCd");
		GroupOperation groupOperation = getGroupForTotal("yrCd");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	}
	
	public List<String> getBillToNamesUnclaimed(String clientId, int month, String yearCd){
		List<String> mnthlist = monthList(month);
		Criteria criteria = Criteria.where("clientid").is(clientId).and("mthCd").in(mnthlist).and("yrCd").is(yearCd).
				and("invtype").nin(MasterGSTConstants.ADVANCES, MasterGSTConstants.ATPAID, MasterGSTConstants.ITC_REVERSAL, MasterGSTConstants.NIL)
				.and("items").elemMatch(Criteria.where("elg").is(null));
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	public List<PurchaseRegister> findByClientidAndInvoiceTypeAndMonthAndYear(final String clientid, int month, String yearCode){       
        Criteria criteria = Criteria.where("clientid").is(clientid).and("mthCd").is(month+"").and("yrCd").is(yearCode).and("invtype").in(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,MasterGSTConstants.B2BA,MasterGSTConstants.CDNA).and("matchingStatus").is(null);
        Query query = Query.query(criteria);
        addAllInvoicesQueryFirlds(query);
        query.fields().include("matchingId");
        query.fields().include("matchingStatus");
        return mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME);
    }
	
	public List<PurchaseRegister> findByClientidAndInvoiceTypeAndMonthAndYearMatchingIdisNotNull(final String clientid, int month, String yearCode, String normalOrMannual){  
		List<String> matchingstatus = new ArrayList<String>();
		if(normalOrMannual.equalsIgnoreCase("normal")) {
			matchingstatus.add("Not In GSTR2A");
			matchingstatus.add(MasterGSTConstants.GST_STATUS_NOTINGSTR2A);
			matchingstatus.add(MasterGSTConstants.GST_STATUS_MATCHED);
			matchingstatus.add(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
			matchingstatus.add(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
			matchingstatus.add(MasterGSTConstants.GST_STATUS_MISMATCHED);
			matchingstatus.add(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
			matchingstatus.add(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED_PREVIOUS_MONTH);
		}else {
			matchingstatus.add(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
		}
        Criteria criteria = Criteria.where("clientid").is(clientid).and("mthCd").is(month+"").and("yrCd").is(yearCode).and("invtype").in(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,MasterGSTConstants.B2BA,MasterGSTConstants.CDNA).and("matchingStatus").in(matchingstatus);
        if(!normalOrMannual.equalsIgnoreCase("normal")) {
        	criteria.and("mannualMatchInvoices").is("Single");
        }
        Query query = Query.query(criteria);
        addAllInvoicesQueryFirlds(query);
        query.fields().include("matchingId");
        query.fields().include("matchingStatus");
        return mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME);
    }
	
	public List<PurchaseRegister> findByById(List<String> ids,String clientid){
        if(ids != null) {
           //List<ObjectId> objectsIds = ids.stream().map((id) -> new ObjectId(id)).collect(Collectors.toList());
           Criteria criteria = Criteria.where("clientid").is(clientid).and("matchingId").in(ids);
           Query query = Query.query(criteria);
           addAllInvoicesQueryFirlds(query);
           query.fields().include("matchingId");
           query.fields().include("matchingStatus");
           return mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME);
        }else {
            return null;
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
		List<PurchaseRegister> gstr1Invoices = mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME);
		if(NullUtil.isNotEmpty(gstr1Invoices)) {
			totalInvoices = gstr1Invoices.size();
			for(PurchaseRegister inv : gstr1Invoices) {
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
	public List<String> monthList(int month){
		List<String> mnthList = Lists.newArrayList();
		if(month > 3) {
			for(int i=4; i <= month; i++) {
				mnthList.add(i+"");	
			}
		}
		if(month <= 3) {
			for(int i=1; i <= month; i++) {
				mnthList.add(i+"");	
			}
			for(int i=4; i <= 12; i++) {
				mnthList.add(i+"");	
			}
		}
		return mnthList;
		
	}
	
public Page<PurchaseRegister> findByClientidAndMonthAndYearForMannualMatch(final String clientid, List<String> invTypes,List<String> statusList, Date stDate,Date endDate, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate);
		criteria.andOperator(new Criteria().orOperator(Criteria.where("matchingStatus").in(statusList),Criteria.where("matchingStatus").is(""), Criteria.where("matchingStatus").is(null)));
		if(NullUtil.isNotEmpty(filter) && NullUtil.isEmpty(filter.getInvoiceType())) {
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		Sort sort = null;
		if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start/length), length,sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, PurchaseRegister.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
		}
		return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME), pageable, total);
	}
	public Page<? extends InvoiceParent> findByClientidAndFromtimeAndTotime(String clientid, Date stDate, Date endDate, int start, int length, String searchVal, String fieldName, String order,InvoiceFilter filter) {
		//Criteria criteria = Criteria.where("clientid").is(clientid).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME));
		}else {
			addAllInvoicesQueryFirlds(query);
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Sort sort = null;
			if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
				sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
			}
			Pageable pageable = new PageRequest((start/length), length,sort);
			query.with(pageable);
			long total = mongoTemplate.count(query, PurchaseRegister.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
			}
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME), pageable, total);
		}
	}

	public TotalInvoiceAmount getTotalInvoicesAmountsForCustom(String clientid, Date stDate, Date endDate, String searchVal, InvoiceFilter filter) {
		//Criteria criteria = Criteria.where("clientid").is(clientid).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
		GroupOperation groupOperation = getGroupForTotal("csftr");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		TotalInvoiceAmount cancelInvAmount = new TotalInvoiceAmount();
		if(filter != null) {
			cancelInvAmount = getCancelledTotalInvoicesAmountsForCustom(clientid,stDate,endDate,searchVal,filter);
			if(filter.getPaymentStatus() != null && filter.getPaymentStatus().length == 1) {
				List<String> statusList = Arrays.asList(filter.getPaymentStatus());
				if(statusList.contains("CANCELLED")) {
					return cancelInvAmount;
				}
			}
		}
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
		if(totalnvAmount != null && cancelInvAmount != null) {
			totalInvAmount.setTotalTransactions(totalnvAmount.getTotalTransactions());
			totalInvAmount.setTotalTaxableAmount(totalnvAmount.getTotalTaxableAmount().subtract(cancelInvAmount.getTotalTaxableAmount()));
			totalInvAmount.setTotalExemptedAmount(totalnvAmount.getTotalExemptedAmount().subtract(cancelInvAmount.getTotalExemptedAmount()));
			totalInvAmount.setTotalTaxAmount(totalnvAmount.getTotalTaxAmount().subtract(cancelInvAmount.getTotalTaxAmount()));
			totalInvAmount.setTotalAmount(totalnvAmount.getTotalAmount().subtract(cancelInvAmount.getTotalAmount()));
			totalInvAmount.setTotalIGSTAmount(totalnvAmount.getTotalIGSTAmount().subtract(cancelInvAmount.getTotalIGSTAmount()));
			totalInvAmount.setTotalCGSTAmount(totalnvAmount.getTotalCGSTAmount().subtract(cancelInvAmount.getTotalCGSTAmount()));
			totalInvAmount.setTotalSGSTAmount(totalnvAmount.getTotalSGSTAmount().subtract(cancelInvAmount.getTotalSGSTAmount()));
			totalInvAmount.setTotalCESSAmount(totalnvAmount.getTotalCESSAmount().subtract(cancelInvAmount.getTotalCESSAmount()));
			totalInvAmount.setTotalITCAvailable(totalnvAmount.getTotalITCAvailable().subtract(cancelInvAmount.getTotalITCAvailable()));
			totalInvAmount.setTcsTdsAmount(totalnvAmount.getTcsTdsAmount().subtract(cancelInvAmount.getTcsTdsAmount()));
			totalInvAmount.setTdsAmount(totalnvAmount.getTdsAmount().subtract(cancelInvAmount.getTdsAmount()));
			return totalInvAmount;
		}else {
			return totalnvAmount;
		}
	}

	public List<TotalInvoiceAmount> getConsolidatedSummeryForCustom(Client client, String returntype, Date stDate, Date endDate,InvoiceFilter filter) {
		//Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("dateofinvoice").gte(stDate).lte(endDate);
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalitc").multiply("sftr").as("totalitc")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount")
				.and("tdsAmount").multiply("sftr").as("tdsAmount")
				.andInclude("mthCd");
		GroupOperation groupOperation = Aggregation.group("mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("tdsAmount").as("tdsAmount")
				.sum("totalitc").as("totalITCAvailable").count().as("totalTransactions");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedSummeryForCustom(client,returntype,stDate,endDate);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
					gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
					gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
					gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
					gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
					gstr1InvoiceAmount.setTotalITCAvailable(gstr1InvoiceAmount.getTotalITCAvailable().subtract(gstr1CancelledInvoiceAmount.getTotalITCAvailable()));
					gstr1InvoiceAmount.setTcsTdsAmount(gstr1InvoiceAmount.getTcsTdsAmount().subtract(gstr1CancelledInvoiceAmount.getTcsTdsAmount()));
					gstr1InvoiceAmount.setTdsAmount(gstr1InvoiceAmount.getTdsAmount().subtract(gstr1CancelledInvoiceAmount.getTdsAmount()));
				}
			}
		}
		return  totalInvAmount;
	}
	
	public List<TotalInvoiceAmount> getConsolidatedReportsSummeryForYear(final Client client, String yearCd,boolean checkQuarterly, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd);
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalitc").multiply("sftr").as("totalitc")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount")
				.and("tdsAmount").multiply("sftr").as("tdsAmount")
				.andInclude("mthCd");
		GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("tdsAmount").as("tdsAmount")
				.sum("totalitc").as("totalITCAvailable").count().as("totalTransactions");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedSummeryForYearMonthwise(client,yearCd,checkQuarterly);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
					gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
					gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
					gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
					gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
					gstr1InvoiceAmount.setTotalITCAvailable(gstr1InvoiceAmount.getTotalITCAvailable().subtract(gstr1CancelledInvoiceAmount.getTotalITCAvailable()));
					gstr1InvoiceAmount.setTcsTdsAmount(gstr1InvoiceAmount.getTcsTdsAmount().subtract(gstr1CancelledInvoiceAmount.getTcsTdsAmount()));
					gstr1InvoiceAmount.setTdsAmount(gstr1InvoiceAmount.getTdsAmount().subtract(gstr1CancelledInvoiceAmount.getTdsAmount()));
				}
			}
		}
		return  totalInvAmount;
	}
	
	public List<String> getCustomBillToNames(String clientId, Date stDate,Date endDate){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("dateofinvoice").gte(stDate).lte(endDate);
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	public Page<PurchaseRegister> findByItcinvoices(final String clientid, String itcinvtype, Date stDate, Date endDate, List<String> invtypes, int start, int length, String searchVal,String fieldName, String order){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("invtype").nin(invtypes);
		if(itcinvtype.equalsIgnoreCase("itc_claimed")) {
			criteria.andOperator(Criteria.where("dateofitcClaimed").gte(stDate), Criteria.where("dateofitcClaimed").lte(endDate));
		}else {
			criteria.and("items").elemMatch(Criteria.where("elg").is(null))
				.andOperator(Criteria.where("dateofinvoice").gte(stDate), Criteria.where("dateofinvoice").lte(endDate));	
		}
		
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Sort sort = null;
		if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}
		Pageable pageable = new PageRequest((start/length), length,sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, PurchaseRegister.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
		}
		return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME), pageable, total);
	}

	public TotalInvoiceAmount getItcTotalInvoicesAmounts(String clientid, List<String> invtypes, String itcinvtype, Date stDate, Date endDate, String searchVal) {
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("invtype").nin(invtypes);
		if(itcinvtype.equalsIgnoreCase("itc_claimed")) {
			criteria.andOperator(Criteria.where("dateofitcClaimed").gte(stDate), Criteria.where("dateofitcClaimed").lte(endDate));
		}else {
			criteria.and("items").elemMatch(Criteria.where("elg").is(null))
				.andOperator(Criteria.where("dateofinvoice").gte(stDate), Criteria.where("dateofinvoice").lte(endDate));	
		}
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
		GroupOperation groupOperation = getGroupForTotal("csftr");
		
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	}
 
	public Page<PurchaseRegister> findByItcinvoices(final String clientid,List<String> invtypes, String itcinvtype, Date stDate, Date endDate,String fieldName, String order){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("invtype").nin(invtypes);
		if(itcinvtype.equalsIgnoreCase("itc_claimed")) {
			criteria.andOperator(Criteria.where("dateofitcClaimed").gte(stDate), Criteria.where("dateofitcClaimed").lte(endDate));
		}else {
			criteria.and("items").elemMatch(Criteria.where("elg").is(null))
				.andOperator(Criteria.where("dateofinvoice").gte(stDate), Criteria.where("dateofinvoice").lte(endDate));	
		}
		Query query = Query.query(criteria);
		return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME));
	}
	
	public Page<? extends InvoiceParent> findByClientidInAndMonthAndYear(List<String> clientids, int month,String yearCode, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter) {
		//Criteria criteria = Criteria.where("clientid").in(clientids).is(clientids).and("yrCd").is(yearCode);
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME));
		}else {
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Sort sort = null;
			if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
				sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
			}
			Pageable pageable = new PageRequest((start/length), length,sort);
			query.with(pageable);
			long total = mongoTemplate.count(query, PurchaseRegister.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
			}
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME), pageable, total);
		}
	}
	
public TotalInvoiceAmount getTotalInvoicesAmountsForMonth(List<String> clientids, int month, String yearCode,String searchVal, InvoiceFilter filter) {
		
		//Criteria criteria = Criteria.where("clientid").in(clientids).is(clientids).and("yrCd").is(yearCode);
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
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
		TotalInvoiceAmount cancelInvAmount = new TotalInvoiceAmount();
		if(filter != null) {
			cancelInvAmount = getCancelledTotalInvoicesAmountsForMonth(clientids,month,yearCode,searchVal,filter);
			if(filter.getPaymentStatus() != null && filter.getPaymentStatus().length == 1) {
				List<String> statusList = Arrays.asList(filter.getPaymentStatus());
				if(statusList.contains("CANCELLED")) {
					return cancelInvAmount;
				}
			}
		}
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
		if(totalnvAmount != null && cancelInvAmount != null) {
			totalInvAmount.setTotalTransactions(totalnvAmount.getTotalTransactions());
			totalInvAmount.setTotalTaxableAmount(totalnvAmount.getTotalTaxableAmount().subtract(cancelInvAmount.getTotalTaxableAmount()));
			totalInvAmount.setTotalExemptedAmount(totalnvAmount.getTotalExemptedAmount().subtract(cancelInvAmount.getTotalExemptedAmount()));
			totalInvAmount.setTotalTaxAmount(totalnvAmount.getTotalTaxAmount().subtract(cancelInvAmount.getTotalTaxAmount()));
			totalInvAmount.setTotalAmount(totalnvAmount.getTotalAmount().subtract(cancelInvAmount.getTotalAmount()));
			totalInvAmount.setTotalIGSTAmount(totalnvAmount.getTotalIGSTAmount().subtract(cancelInvAmount.getTotalIGSTAmount()));
			totalInvAmount.setTotalCGSTAmount(totalnvAmount.getTotalCGSTAmount().subtract(cancelInvAmount.getTotalCGSTAmount()));
			totalInvAmount.setTotalSGSTAmount(totalnvAmount.getTotalSGSTAmount().subtract(cancelInvAmount.getTotalSGSTAmount()));
			totalInvAmount.setTotalCESSAmount(totalnvAmount.getTotalCESSAmount().subtract(cancelInvAmount.getTotalCESSAmount()));
			totalInvAmount.setTotalITCAvailable(totalnvAmount.getTotalITCAvailable().subtract(cancelInvAmount.getTotalITCAvailable()));
			totalInvAmount.setTcsTdsAmount(totalInvAmount.getTcsTdsAmount().subtract(cancelInvAmount.getTcsTdsAmount()));
			totalInvAmount.setTdsAmount(totalInvAmount.getTdsAmount().subtract(cancelInvAmount.getTdsAmount()));
			return totalInvAmount;
		}else {
			return totalnvAmount;
		}
	}
	public Page<? extends InvoiceParent> findByClientidInAndFromtimeAndTotime(List<String> clientids, Date stDate, Date endDate, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter) {
		//Criteria criteria = Criteria.where("clientid").in(clientids).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		Criteria criteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME));
		}else {
		addAllInvoicesQueryFirlds(query);
		if(length == -1) {
				length = Integer.MAX_VALUE;
			}
		Sort sort = null;
		if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}
			Pageable pageable = new PageRequest((start/length), length,sort);
			query.with(pageable);
			long total = mongoTemplate.count(query, PurchaseRegister.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
			}
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME), pageable, total);
		}
	}
	public List<TotalInvoiceAmount> getTotalInvoicesAmountsForCustom(List<String> clientids, Date stDate, Date endDate,String searchVal, InvoiceFilter filter) {
		//Criteria criteria = Criteria.where("clientid").in(clientids).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		Criteria criteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("yrCd");
		GroupOperation groupOperation = getGroupForTotal("yrCd");
		
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledTotalInvoicesAmountsForCustom(clientids,stDate,endDate,searchVal,filter);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
					gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
					gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
					gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
					gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
					gstr1InvoiceAmount.setTotalITCAvailable(gstr1InvoiceAmount.getTotalITCAvailable().subtract(gstr1CancelledInvoiceAmount.getTotalITCAvailable()));
					gstr1InvoiceAmount.setTcsTdsAmount(gstr1InvoiceAmount.getTcsTdsAmount().subtract(gstr1CancelledInvoiceAmount.getTcsTdsAmount()));
					gstr1InvoiceAmount.setTdsAmount(gstr1InvoiceAmount.getTdsAmount().subtract(gstr1CancelledInvoiceAmount.getTdsAmount()));
				}
			}
		}
		return  totalInvAmount;
	}
public List<TotalInvoiceAmount> getConsolidatedGlobalReportsSummeryForYear(List<String> clientids, String yearCd, boolean checkQuarterly,InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCd);
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalitc").multiply("sftr").as("totalitc")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount")
				.and("tdsAmount").multiply("sftr").as("tdsAmount")
				.andInclude("mthCd");
		GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("tdsAmount").as("tdsAmount")
				.sum("totalitc").as("totalITCAvailable").count().as("totalTransactions");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedGlobalReportsSummeryForYear(clientids,yearCd,checkQuarterly);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
					gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
					gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
					gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
					gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
					gstr1InvoiceAmount.setTotalITCAvailable(gstr1InvoiceAmount.getTotalITCAvailable().subtract(gstr1CancelledInvoiceAmount.getTotalITCAvailable()));
					gstr1InvoiceAmount.setTcsTdsAmount(gstr1InvoiceAmount.getTcsTdsAmount().subtract(gstr1CancelledInvoiceAmount.getTcsTdsAmount()));
					gstr1InvoiceAmount.setTdsAmount(gstr1InvoiceAmount.getTdsAmount().subtract(gstr1CancelledInvoiceAmount.getTdsAmount()));
				}
			}
		}
		return  totalInvAmount;
	}
	public List<TotalInvoiceAmount> getConsolidatedSummeryForGlobalCustom(List<String> clientids, String returntype, Date stDate, Date endDate,InvoiceFilter filter) {
		//Criteria criteria = Criteria.where("clientid").in(clientids).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		Criteria criteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate);
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalitc").multiply("sftr").as("totalitc")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount")
				.and("tdsAmount").multiply("sftr").as("tdsAmount")
				.andInclude("mthCd");
		GroupOperation groupOperation = Aggregation.group("mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("tcstdsAmount").as("tcstdsAmount")
				.sum("tdsAmount").as("tdsAmount")
				.sum("totalitc").as("totalITCAvailable").count().as("totalTransactions");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedSummeryForGlobalCustom(clientids,returntype,stDate,endDate);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
					gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
					gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
					gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
					gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
					gstr1InvoiceAmount.setTotalITCAvailable(gstr1InvoiceAmount.getTotalITCAvailable().subtract(gstr1CancelledInvoiceAmount.getTotalITCAvailable()));
					gstr1InvoiceAmount.setTcsTdsAmount(gstr1InvoiceAmount.getTcsTdsAmount().subtract(gstr1CancelledInvoiceAmount.getTcsTdsAmount()));
					gstr1InvoiceAmount.setTdsAmount(gstr1InvoiceAmount.getTdsAmount().subtract(gstr1CancelledInvoiceAmount.getTdsAmount()));
				}
			}
		}
		return  totalInvAmount;
	}

	public List<String> getBilledToNames(List<String> clientids, int month, String yearCd) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCd);
		if(month>0) {
			criteria.and("mthCd").is(month+"");
		}
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	public List<String> getGlobalCustomBillToNames(List<String> clientIds, Date stDate,Date endDate){
		Criteria criteria = Criteria.where("clientid").in(clientIds).andOperator(
				Criteria.where("dateofinvoice").gte(stDate),
				Criteria.where("dateofinvoice").lte(endDate)
		);
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	public List<TotalInvoiceAmount> getConsolidatedReportsSummeryForYearMonthwise(List<String> clientids, String yearCd, boolean checkQuarterly){
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCd);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totalitc").multiply("sftr").as("totalitc")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount")
				.and("tdsAmount").multiply("sftr").as("tdsAmount")
				.andInclude("mthCd");
		GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totalitc").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIgstAmount")
				.sum("totalCgstAmount").as("totalCgstAmount")
				.sum("totalSgstAmount").as("totalSgstAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("tdsAmount").as("tdsAmount");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getMappedResults();
	}
	
	private void applyFilterToCriteriaForCancelled(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			List<String> allStatusList = Arrays.asList("CANCELLED");
			criteria.and("gstStatus").in(allStatusList);
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
				if(reverseChargeList.contains("Regular")) {
					criteria.andOperator(new Criteria().orOperator(Criteria.where("revchargetype").in(Arrays.asList(filter.getReverseCharge())),Criteria.where("revchargetype").is(""), Criteria.where("revchargetype").is(null)));
				}else {
					criteria.and("revchargetype").in(reverseChargeList);
				}
			}
			/*if(filter.getCustomFieldText1() != null){
				criteria.andOperator(criteria,new Criteria().orOperator(Criteria.where("customFieldText1").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText2").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText3").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText4").in(Arrays.asList(filter.getCustomFieldText1()))));
				//criteria.andOperator(criteria,new Criteria().orOperator(Criteria.where("customField1").in(Arrays.asList(filter.getCustomField1())),Criteria.where("customField2").in(Arrays.asList(filter.getCustomField1())),Criteria.where("customField3").in(Arrays.asList(filter.getCustomField1())),Criteria.where("customField4").in(Arrays.asList(filter.getCustomField1()))));	
			}*/
			if(filter.getCustomFieldText1() != null){
				criteria.and("customField1").in(Arrays.asList(filter.getCustomFieldText1()));
			}
			if(filter.getCustomFieldText2() != null){
				criteria.and("customField2").in(Arrays.asList(filter.getCustomFieldText2()));
			}
			if(filter.getCustomFieldText3() != null){
				criteria.and("customField3").in(Arrays.asList(filter.getCustomFieldText3()));
			}
			if(filter.getCustomFieldText4() != null){
				criteria.and("customField4").in(Arrays.asList(filter.getCustomFieldText4()));
			}
			if(filter.getGstno() != null){
				criteria.and("b2b.0.ctin").in(Arrays.asList(filter.getGstno()));
			}
			if(filter.getDateofInvoice() != null){
				criteria.and("dateofinvoice_str").in(Arrays.asList(filter.getDateofInvoice()));
			}
			if(filter.getInvoiceno() != null){
				criteria.and("invoiceno").in(Arrays.asList(filter.getInvoiceno()));
			}
		}
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmountsForMonthCancelled(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteriaForCancelled(criteria, filter);
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
	
public List<TotalInvoiceAmount> getCancelledConsolidatedSummeryForYearMonthwise(final Client client, String yearCd, boolean checkQuarterly){
		
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd).and("gstStatus").is("CANCELLED");
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totalitc").multiply("sftr").as("totalitc")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount")
				.and("tdsAmount").multiply("sftr").as("tdsAmount")
				.andInclude("mthCd");
		GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totalitc").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("tdsAmount").as("tdsAmount");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getMappedResults();
	}

public TotalInvoiceAmount getCancelledTotalInvoicesAmountsForBillDateMonth(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter){
	Criteria criteria = Criteria.where("clientid").is(clientId).and("trDateyrCd").is(yearCode);
	if(month>0) {
		criteria.and("trDatemthCd").is(month+"");
	}
	
	applyFilterToCriteriaForCancelled(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = null;
	GroupOperation groupOperation = null;
	if(month > 0) {
		projectionOperation = getProjectionForTotal("trDatemthCd");
		groupOperation = getGroupForTotal("trDatemthCd");
	}else {
		projectionOperation = getProjectionForTotal("trDateyrCd");
		groupOperation = getGroupForTotal("trDateyrCd");
	}
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return results.getUniqueMappedResult();
}

public List<TotalInvoiceAmount> getCancelledConsolidatedReportsSummeryForYear(final Client client, String yearCd, boolean checkQuarterly){
	
	Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd).and("gstStatus").is("CANCELLED");
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
			.and("totaltax").multiply("sftr").as("totaltax")
			.and("totalitc").multiply("sftr").as("totalitc")
			.and("totalamount").multiply("sftr").as("totalamount")
			.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
			.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
			.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
			.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
			.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount")
			.and("tdsAmount").multiply("sftr").as("tdsAmount")
			.andInclude("mthCd");
	GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
			.sum("totaltaxableamount").as("totalTaxableAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("totaltax").as("totalTaxAmount")
			.sum("totalamount").as("totalAmount")
			.sum("totalIgstAmount").as("totalIGSTAmount")
			.sum("totalCgstAmount").as("totalCGSTAmount")
			.sum("totalSgstAmount").as("totalSGSTAmount")
			.sum("totalCessAmount").as("totalCESSAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("tdsAmount").as("tdsAmount")
			.sum("totalitc").as("totalITCAvailable").count().as("totalTransactions");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return  results.getMappedResults();
}

public List<TotalInvoiceAmount> getCancelledConsolidatedSummeryForCustom(Client client, String returntype, Date stDate, Date endDate) {
	//Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("gstStatus").is("CANCELLED").andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
	Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("gstStatus").is("CANCELLED").and("dateofinvoice").gte(stDate).lte(endDate);
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
			.and("totaltax").multiply("sftr").as("totaltax")
			.and("totalitc").multiply("sftr").as("totalitc")
			.and("totalamount").multiply("sftr").as("totalamount")
			.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
			.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
			.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
			.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
			.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount")
			.and("tdsAmount").multiply("sftr").as("tdsAmount")
			.andInclude("mthCd");
	GroupOperation groupOperation = Aggregation.group("mthCd")
			.sum("totaltaxableamount").as("totalTaxableAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("totaltax").as("totalTaxAmount")
			.sum("totalamount").as("totalAmount")
			.sum("totalIgstAmount").as("totalIGSTAmount")
			.sum("totalCgstAmount").as("totalCGSTAmount")
			.sum("totalSgstAmount").as("totalSGSTAmount")
			.sum("totalCessAmount").as("totalCESSAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("tdsAmount").as("tdsAmount")
			.sum("totalitc").as("totalITCAvailable").count().as("totalTransactions");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return  results.getMappedResults();
}

public TotalInvoiceAmount getCancelledTotalInvoicesAmountsForCustom(String clientid, Date stDate, Date endDate, String searchVal, InvoiceFilter filter) {
	Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate);
	//Criteria criteria = Criteria.where("clientid").is(clientid).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
	applyFilterToCriteriaForCancelled(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
	GroupOperation groupOperation = getGroupForTotal("csftr");
	
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return results.getUniqueMappedResult();
}

public TotalInvoiceAmount getCancelledTotalInvoicesAmountsForMonth(List<String> clientids, int month, String yearCode,String searchVal, InvoiceFilter filter) {
	
	//Criteria criteria = Criteria.where("clientid").in(clientids).is(clientids).and("yrCd").is(yearCode);
	Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
	if(month > 0) {
		criteria.and("mthCd").is(month+"");
	}
	applyFilterToCriteriaForCancelled(criteria, filter);
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

public List<TotalInvoiceAmount> getCancelledConsolidatedGlobalReportsSummeryForYear(List<String> clientids, String yearCd, boolean checkQuarterly){
	Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCd).and("gstStatus").is("CANCELLED");
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
			.and("totaltax").multiply("sftr").as("totaltax")
			.and("totalitc").multiply("sftr").as("totalitc")
			.and("totalamount").multiply("sftr").as("totalamount")
			.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
			.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
			.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
			.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
			.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount")
			.and("tdsAmount").multiply("sftr").as("tdsAmount")
			.andInclude("mthCd");
	GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
			.sum("totaltaxableamount").as("totalTaxableAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("totaltax").as("totalTaxAmount")
			.sum("totalamount").as("totalAmount")
			.sum("totalIgstAmount").as("totalIGSTAmount")
			.sum("totalCgstAmount").as("totalCGSTAmount")
			.sum("totalSgstAmount").as("totalSGSTAmount")
			.sum("totalCessAmount").as("totalCESSAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("tdsAmount").as("tdsAmount")
			.sum("totalitc").as("totalITCAvailable").count().as("totalTransactions");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return  results.getMappedResults();
}

public List<TotalInvoiceAmount> getCancelledConsolidatedSummeryForGlobalCustom(List<String> clientids, String returntype, Date stDate, Date endDate) {
	Criteria criteria = Criteria.where("clientid").in(clientids).and("gstStatus").is("CANCELLED")
			.andOperator(
					Criteria.where("dateofinvoice").gte(stDate),
					Criteria.where("dateofinvoice").lte(endDate)
			);
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
			.and("totaltax").multiply("sftr").as("totaltax")
			.and("totalitc").multiply("sftr").as("totalitc")
			.and("totalamount").multiply("sftr").as("totalamount")
			.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
			.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
			.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
			.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
			.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount")
			.and("tdsAmount").multiply("sftr").as("tdsAmount")
			.andInclude("mthCd");
	GroupOperation groupOperation = Aggregation.group("mthCd")
			.sum("totaltaxableamount").as("totalTaxableAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("totaltax").as("totalTaxAmount")
			.sum("totalamount").as("totalAmount")
			.sum("totalIgstAmount").as("totalIGSTAmount")
			.sum("totalCgstAmount").as("totalCGSTAmount")
			.sum("totalSgstAmount").as("totalSGSTAmount")
			.sum("totalCessAmount").as("totalCESSAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("tdsAmount").as("tdsAmount")
			.sum("totalitc").as("totalITCAvailable").count().as("totalTransactions");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return  results.getMappedResults();
}

public List<TotalInvoiceAmount> getCancelledTotalInvoicesAmountsForCustom(List<String> clientids, Date stDate, Date endDate,String searchVal, InvoiceFilter filter) {
	//Criteria criteria = Criteria.where("clientid").in(clientids).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
	Criteria criteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate);
	applyFilterToCriteriaForCancelled(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = getProjectionForTotal("yrCd");
	GroupOperation groupOperation = getGroupForTotal("yrCd");
	
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	List<TotalInvoiceAmount> tresults = results.getMappedResults();
	//return results.getUniqueMappedResult();
	return tresults;
}

}
