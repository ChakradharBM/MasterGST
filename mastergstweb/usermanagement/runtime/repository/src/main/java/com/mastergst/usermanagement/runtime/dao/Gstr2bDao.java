package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
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

import com.google.api.client.util.Lists;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.GSTR2BSupport;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;

@Component
public class Gstr2bDao {

	@Autowired private MongoTemplate mongoTemplate;
	
	public List<String> getMultimonthBillToNames(String clientid, List<String> invTypes, int year) {
		List<String> fps = Arrays.asList("04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), "03"+(year+1));
		Criteria criteria = Criteria.where("clientid").is(clientid)
					.and("invtype").in(invTypes).and("fp").in(fps);
		
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection("gst2b_support").distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	public Page<GSTR2BSupport> getMultimonthInvoices(String clientid, List<String> invTypes, int year, int start, int length, String searchVal, InvoiceFilter filter) {
		
		
		List<String> fps = Arrays.asList("04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), "03"+(year+1));
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").in(fps);
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR2BSupport>(mongoTemplate.find(query, GSTR2BSupport.class, "gst2b_support"));
		}else {
			addAllInvoicesQueryFirlds(query);
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, GSTR2BSupport.class, "gst2b_support");
			if (total == 0) {
				return new PageImpl<GSTR2BSupport>(Collections.<GSTR2BSupport> emptyList());
			}
			return new PageImpl<GSTR2BSupport>(mongoTemplate.find(query, GSTR2BSupport.class, "gst2b_support"), pageable, total);
		}
	}

	public TotalInvoiceAmount getMultimonthTotalInvoicesAmounts(String clientid, List<String> invTypes, int year, String searchVal, InvoiceFilter filter) {
		List<String> fps = Arrays.asList("04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), "03"+(year+1));
		Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").in(fps);
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
		GroupOperation groupOperation = getGroupForTotal("csftr");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "gst2b_support", TotalInvoiceAmount.class);
		TotalInvoiceAmount totalInvAmount = results.getUniqueMappedResult();
		
		return totalInvAmount;
	}
	
	public List<TotalInvoiceAmount> getConsolidatedMultimonthSummeryForYearMonthwise(final Client client, List<String> invTypes, int year, InvoiceFilter filter){
		List<String> fps = Arrays.asList("04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), "03"+(year+1));
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString())
				.and("fp").in(fps);
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
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
				.andInclude("fp","tcstdsAmount");
		GroupOperation groupOperation = Aggregation.group("fp")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("totalitc").as("totalITCAvailable").count().as("totalTransactions");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "gst2b_support", TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		return  totalInvAmount;
	}
	
	private void applyFilterToCriteria(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			List<Criteria> criterias = new ArrayList<Criteria>();
			
			if(filter.getInvoiceType() != null){
				List<String> invTypeList = new LinkedList<String>(Arrays.asList(filter.getInvoiceType()));
				List<String> creditDebitList = Lists.newArrayList();
				if(invTypeList.contains("Credit Note")){
					creditDebitList.add("C");
					invTypeList.remove("Credit Note");
				}
				if(invTypeList.contains("Debit Note")){
					creditDebitList.add("D");
					invTypeList.remove("Debit Note");
				}
				
				Criteria criteriaa = null;
				if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(
							Criteria.where("invtype").in(invTypeList),
							Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList),
							Criteria.where("cdna.0.nt.0.ntty").in(creditDebitList)));
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(
							Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList),
							Criteria.where("cdna.0.nt.0.ntty").in(creditDebitList)));
				}else {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(
							Criteria.where("invtype").in(invTypeList)));
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
					Criteria criteriaa=new Criteria().andOperator(new Criteria().orOperator(Criteria.where("revchargetype").in(Arrays.asList(filter.getReverseCharge())),Criteria.where("revchargetype").is(""), Criteria.where("revchargetype").is(null)));
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
			if(NullUtil.isNotEmpty(criterias)) {
				criteria.andOperator(criterias.toArray(new Criteria[criterias.size()]));				
			}
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
 		criterias.add(Criteria.where("invtype").regex(searchVal, "i"));
 		criterias.add(Criteria.where("billedtoname").regex(searchVal, "i"));
 		criterias.add(Criteria.where("invoiceno").regex(searchVal, "i"));
 		criterias.add(Criteria.where("b2b.0.ctin").regex(searchVal, "i"));
 		//criterias.add(Criteria.where("cdn.0.nt.0.ntty").regex(searchVal, "i"));
 		//criterias.add(Criteria.where("cdna.0.nt.0.ntty").regex(searchVal, "i"));
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
	
	private void addAllInvoicesQueryFirlds(Query query){
		query.fields().include("clientid");
        query.fields().include("fullname");
        query.fields().include("invtype");
        query.fields().include("invoiceno");
        query.fields().include("billedtoname");
        query.fields().include("b2b");
        query.fields().include("b2ba");
        query.fields().include("cfs");
        query.fields().include("cdn");
		query.fields().include("cdna");
		query.fields().include("isd");
		query.fields().include("impGoods");
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

	public List<String> getMultimonthBillToNamesClientIdIn(List<String> clientId, String yearCd, int month, List<String> fp){
		Criteria criteria = Criteria.where("clientid").in(clientId).and("fp").in(fp);
		
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection("gst2b_support").distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}

	public Page<? extends InvoiceParent> findByClientidInAndMonthAndYear(List<String> clientids, List<String> fp, int start, int length, String searchVal, String fieldName, String order, InvoiceFilter filter) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("fp").in(fp);
		
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR2BSupport>(mongoTemplate.find(query, GSTR2BSupport.class, "gst2b_support"));
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
			long total = mongoTemplate.count(query, GSTR2BSupport.class, "gst2b_support");
			if (total == 0) {
				return new PageImpl<GSTR2BSupport>(Collections.<GSTR2BSupport> emptyList());
			}
			return new PageImpl<GSTR2BSupport>(mongoTemplate.find(query, GSTR2BSupport.class, "gst2b_support"), pageable, total);
		}
	}

	public TotalInvoiceAmount getTotalInvoicesAmountsForMonthClientIdin(List<String> clientids, List<String> fp, String searchVal, InvoiceFilter filter) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("fp").in(fp);
		
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
		GroupOperation groupOperation = getGroupForTotal("csftr");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "gst2b_support", TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	}

	public Page<? extends InvoiceParent> findByClientidInAndMonthAndYear(List<String> clientids, List<String> fp, int start, int length, String searchVal, InvoiceFilter filter, Pageable pageable) {
		
		Criteria criteria = Criteria.where("clientid").in(clientids).and("fp").in(fp);
		
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR2BSupport.class, "gst2b_support");
		if (total == 0) {
			return new PageImpl<GSTR2BSupport>(Collections.<GSTR2BSupport> emptyList());
		}
		return new PageImpl<GSTR2BSupport>(mongoTemplate.find(query, GSTR2BSupport.class, "gst2b_support"), pageable, total);
	}
		

}
