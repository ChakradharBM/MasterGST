package com.mastergst.usermanagement.runtime.dao;

import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.InvoiceTypeSummery;
import com.mastergst.usermanagement.runtime.domain.MismatchInvCount;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;

@Service
public class GSTR2ADao extends InvoiceDao{
	public GSTR2ADao(){
		super("gstr2");
	}
	
public List<TotalInvoiceAmount> getConsolidatedSummeryForYearMonthwise(final Client client, String yearCd, boolean checkQuarterly){
		
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totalitc").multiply("sftr").as("totalitc")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.andInclude("mthCd","tcstdsAmount");
		GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totalitc").as("totalTaxAmount")
				.sum("totalIgstAmount").as("totalIgstAmount")
				.sum("totalCgstAmount").as("totalCgstAmount")
				.sum("totalSgstAmount").as("totalSgstAmount");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getMappedResults();
	}
	
	public List<String> getBillToNames(String clientId, List<String> invTypes, String fp){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("invtype").in(invTypes).and("fp").is(fp);
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmountsForMonth(String clientId, List<String> invTypes, String fp, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientId);
		if(NullUtil.isNotEmpty(filter)) {
			if(NullUtil.isEmpty(filter.getInvoiceType())) {
				criteria.and("invtype").in(invTypes);
			}
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
				criteria.and("dateofinvoice").gte(stDate).lte(endDate);
			}else {
				criteria.and("fp").is(fp);
			}
		}else {
			criteria.and("fp").is(fp);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = null;
		GroupOperation groupOperation = null;
		if(NullUtil.isNotEmpty(filter) && NullUtil.isNotEmpty(filter.getFromtime()) && NullUtil.isNotEmpty(filter.getTotime())) {
			projectionOperation = getProjectionForTotal("csftr");
			groupOperation = getGroupForTotal("csftr");
		}else {
			projectionOperation = getProjectionForTotal("fp");
			groupOperation = getGroupForTotal("fp");
		}
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmountsForMonthClientIdin(List<String> clientId, List<String> invTypes, String fp,int month,String yrCd, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").in(clientId).and("isAmendment").is(true).and("yrCd").is(yrCd);
		String projection = "yrCd";
		if(month > 0) {
			criteria.and("fp").is(fp);
			projection = "fp";
		}
		if(NullUtil.isNotEmpty(filter) && NullUtil.isEmpty(filter.getInvoiceType())) {
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal(projection);
		GroupOperation groupOperation = getGroupForTotal(projection);
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
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
 		criterias.add(Criteria.where("impGoods.0.ctin").regex(searchVal, "i"));
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
				.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount");
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
				.sum("tcstdsAmount").as("tcsTdsAmount");
	}
	
	public Page<GSTR2> findByClientidAndMonthAndYear(final String clientid, List<String> invTypes, String fp, int start, int length, String searchVal, InvoiceFilter filter){
		
		Criteria criteria = Criteria.where("clientid").is(clientid);
		if(NullUtil.isNotEmpty(filter)) {
			if(NullUtil.isEmpty(filter.getInvoiceType())) {
				criteria.and("invtype").in(invTypes);
			}
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
				criteria.and("dateofinvoice").gte(stDate).lte(endDate);
			}else {
				criteria.and("fp").is(fp);
			}
		}else {
			criteria.and("fp").is(fp);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME));
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
			long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
			}
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable, total);
		}
	}
	
public Page<GSTR2> findByClientidAndMonthAndYear(final String clientid, List<String> invTypes, String fp, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter){
		
		Criteria criteria = Criteria.where("clientid").is(clientid);
		if(NullUtil.isNotEmpty(filter)) {
			if(NullUtil.isEmpty(filter.getInvoiceType())) {
				criteria.and("invtype").in(invTypes);
			}
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
				criteria.and("dateofinvoice").gte(stDate).lte(endDate);
			}else {
				criteria.and("fp").is(fp);
			}
		}else {
			criteria.and("fp").is(fp);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME));
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
			long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
			}
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable, total);
		}
	}
public Page<GSTR2> findByClientidInAndMonthAndYear(List<String> clientid, List<String> invTypes, String fp, int month,String yrCd,int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter){
	
	Criteria criteria = Criteria.where("clientid").in(clientid).and("isAmendment").is(true).and("yrCd").is(yrCd);
	if(month > 0) {
		criteria.and("fp").is(fp);
	}
	if(NullUtil.isNotEmpty(filter) && NullUtil.isEmpty(filter.getInvoiceType())) {
		criteria.and("invtype").in(invTypes);
	}
	applyFilterToCriteria(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	Query query = Query.query(criteria);
	if(start == 0 && length == 0) {
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME));
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
		long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
		}
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable, total);
	}
}

public Page<GSTR2> findByClientidInAndMonthAndYear(List<String> clientid, List<String> invTypes, String fp, int month,String yrCd,int start, int length, String searchVal, InvoiceFilter filter){
		
		Criteria criteria = Criteria.where("clientid").in(clientid).and("isAmendment").is(true).and("yrCd").is(yrCd);
		if(month > 0) {
			criteria.and("fp").is(fp);
		}
		if(NullUtil.isNotEmpty(filter) && NullUtil.isEmpty(filter.getInvoiceType())) {
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME));
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
			long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
			}
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable, total);
		}
	}
	
	private void applyFilterToCriteria1(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			if(filter.getPaymentStatus() != null){
				criteria.and("paymentStatus").in(Arrays.asList(filter.getPaymentStatus()));
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
	
	
	private void applyFilterToCriteria(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			List<Criteria> criterias = new ArrayList<Criteria>();
			if(filter.getPaymentStatus() != null){
				criteria.and("paymentStatus").in(Arrays.asList(filter.getPaymentStatus()));
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
				if(reverseChargeList.contains("Regular")){
					Criteria criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("revchargetype").in(reverseChargeList), Criteria.where("revchargetype").is(null)));	
					criterias.add(criteriaa);
				}else{
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
			if(filter.getGstr2aFilingStatus() != null) {
				Criteria criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("b2b.0.cfs").in(Arrays.asList(filter.getGstr2aFilingStatus())),Criteria.where("cdn.0.cfs").in(Arrays.asList(filter.getGstr2aFilingStatus())),Criteria.where("b2ba.0.cfs").in(Arrays.asList(filter.getGstr2aFilingStatus())),Criteria.where("cdna.0.cfs").in(Arrays.asList(filter.getGstr2aFilingStatus()))));
				criterias.add(criteriaa);
			}
			if(filter.getReconStatus() != null){
				Criteria criteriaa = null;
				List<String> statusList = Arrays.asList(filter.getReconStatus());
				if(statusList.contains("Not In Purchases")){
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
		query.fields().include("clientid");
		query.fields().include("fullname");
		query.fields().include("gstStatus");
		query.fields().include("paymentStatus");
		query.fields().include("revchargetype");
		query.fields().include("invtype");
		query.fields().include("invoiceno");
		query.fields().include("billedtoname");
		query.fields().include("b2b");
		query.fields().include("b2ba");
		query.fields().include("cdna");
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
		query.fields().include("totalitc");
		query.fields().include("branch");
		query.fields().include("vertical");
		query.fields().include("matchingStatus");
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
	
public Page<GSTR2> findByClientidAndMonthAndYearForMannualMatch(final String clientid, List<String> invTypes,List<String> statusList, Date stDate,Date endDate, int start, int length, String searchVal, InvoiceFilter filter){
		
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
		/*Sort sort = null;
		if(!IbcUtils.isNullOrEmpty(fieldName) && !IbcUtils.isNullOrEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}*/
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME));
		}else {
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
			}
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable, total);
		}
	}
	
public Page<GSTR2> findByClientidAndFpinForMannualMatch(final String clientid, List<String> invTypes,List<String> statusList, List<String> fp, int start, int length, String searchVal, InvoiceFilter filter){
	
	Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").in(fp);
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
	/*Sort sort = null;
	if(!IbcUtils.isNullOrEmpty(fieldName) && !IbcUtils.isNullOrEmpty(order)){	
		sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
	}*/
	if(start == 0 && length == 0) {
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME));
	}else {
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
		}
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable, total);
	}
}
	
	

}
