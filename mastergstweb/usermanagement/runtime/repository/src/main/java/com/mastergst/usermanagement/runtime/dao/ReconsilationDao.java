package com.mastergst.usermanagement.runtime.dao;

import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import com.google.api.client.util.Lists;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.GSTR2BSupport;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2B;

@Component
public class ReconsilationDao {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	List<String> invTypes = null;
	{
		invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES);
	}
	
	public Page<? extends InvoiceParent> getPurchaseInvoices(String clientid, int start, int length, int month, int year, String matchingId, String macthingStatus,String yearcode,Boolean billdate, boolean isMonthly) {
		// TODO Get Purchase Register Invoice method stub
		String strMonth = month < 10 ? "0" + month : month + "";
		String fp = strMonth + year;
		String mcd = "mthCd";
		String ycd = "yrCd";
		if(billdate) {
			mcd = "trDatemthCd";
			ycd = "trDateyrCd";
		}
		Criteria criteria = Criteria.where("clientid").is(clientid).and(ycd).is(yearcode).and("invtype").in(invTypes);
		
		if(isMonthly) {
			criteria.and(mcd).is(month+"");
		}
		
		if(macthingStatus.equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)){
			criteria.and("gstr2bMatchingStatus").is(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)
				.and("gstr2bMatchingRsn").nin("PR-Multiple");
		}else if(macthingStatus.equals(MasterGSTConstants.GST_STATUS_NOTINGSTR2B)) {
			criteria.and("gstr2bMatchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINGSTR2B);
		}else if(macthingStatus.equals(MasterGSTConstants.GST_STATUS_MATCHED)){
			criteria.and("gstr2bMatchingStatus").in(
					MasterGSTConstants.GST_STATUS_MATCHED,
					MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH,
					MasterGSTConstants.GST_STATUS_MISMATCHED,
					MasterGSTConstants.GST_STATUS_PARTIALMATCHED,
					MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED,
					MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED_PREVIOUS_MONTH,
					MasterGSTConstants.GST_STATUS_PROBABLEMATCHED,
					MasterGSTConstants.GST_STATUS_TAX_MISMATCHED,
					MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED,
					MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED,
					MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED,
					MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED
			);
		}
		
		Query query = Query.query(criteria);
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, PurchaseRegister.class, "purchaseregister");
		if (total == 0) {
			return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
		}
		return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, "purchaseregister"), pageable, total);
	}
	
	public Page<? extends InvoiceParent> getGstr2BSupportInvoices(String clientid, int start, int length, int month, int year, String matchingId, String macthingStatus,boolean isMonthly) {
		// TODO Get Gstr2B Invoice method stub
		List<String> rtArray = null;
		if(isMonthly) {
			String strMonth = month < 10 ? "0" + month : month + "";
			String fp = strMonth + year;
			rtArray = Arrays.asList(fp);
		}else{
			rtArray = Arrays.asList("03"+year, "04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "03"+(year+1), "03"+(year+1));
		}
		Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").in(rtArray).and("invtype").in(invTypes);
		
		if(macthingStatus.equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)){
			criteria.and("gstr2bMatchingStatus").is(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)
				.and("gstr2bMatchingRsn").nin("PR-Single", "G2B-Multiple", "G2B-Single");
		}else if(macthingStatus.equals(MasterGSTConstants.GST_STATUS_NOTINPURCHASES)) {
			criteria.and("gstr2bMatchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES);
		}else if(macthingStatus.equals(MasterGSTConstants.GST_STATUS_MATCHED)){
			criteria.and("gstr2bMatchingStatus").in(
					MasterGSTConstants.GST_STATUS_MATCHED,
					MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH,
					MasterGSTConstants.GST_STATUS_MISMATCHED,
					MasterGSTConstants.GST_STATUS_PARTIALMATCHED,
					MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED,
					MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED_PREVIOUS_MONTH,
					MasterGSTConstants.GST_STATUS_PROBABLEMATCHED,
					MasterGSTConstants.GST_STATUS_TAX_MISMATCHED,
					MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED,
					MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED,
					MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED,
					MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED
			);
		}
		
		Query query = Query.query(criteria);
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
	
	public GSTR2B getGstr2bInvoices(String clientid, int month, int year) {
		// TODO Get GSTR2B method stub
		String strMonth = month < 10 ? "0" + month : month + "";
		String returnPeriod = strMonth + year;		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").is(returnPeriod);
		Query query = Query.query(criteria);
		
		return mongoTemplate.findOne(query, GSTR2B.class, "gstr2b");
	}
	
	public GSTR2B getGstr2bInvoices(String clientid, String fp) {
		// TODO Get GSTR2B method stub
		Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").is(fp);
		Query query = Query.query(criteria);
		
		return mongoTemplate.findOne(query, GSTR2B.class, "gstr2b");
	}
	
	public Page<PurchaseRegister> findByClientidAndMonthAndYearForMannualMatch(final String clientid, List<String> invTypes,List<String> statusList, Date stDate,Date endDate, int start, int length, String searchVal, InvoiceFilter filter){
		
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
		
		if(start == 0 && length == 0) {
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, "purchaseregister"));
		}else {
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, PurchaseRegister.class, "purchaseregister");
			if (total == 0) {
				return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
			}
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, "purchaseregister"), pageable, total);
		}
	}
	
	public Page<GSTR2BSupport> findByG2bClientidAndMonthAndYearForMannualMatch(final String clientid, List<String> invTypes,List<String> statusList, Date stDate,Date endDate, int start, int length, String searchVal, InvoiceFilter filter){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate);
		criteria.andOperator(new Criteria().orOperator(Criteria.where("gstr2bmatchingStatus").in(statusList),Criteria.where("gstr2bmatchingStatus").is(""), Criteria.where("gstr2bmatchingStatus").is(null)));
		if(NullUtil.isNotEmpty(filter) && NullUtil.isEmpty(filter.getInvoiceType())) {
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR2BSupport>(mongoTemplate.find(query, GSTR2BSupport.class, "gst2b_support"));
		}else {
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
	
	public Page<GSTR2BSupport> findByClientidAndFpinForMannualMatch(final String clientid, List<String> invTypes,List<String> statusList, List<String> fp, int start, int length, String searchVal, InvoiceFilter filter){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").in(fp);
		criteria.andOperator(new Criteria().orOperator(Criteria.where("gstr2bmatchingStatus").in(statusList),Criteria.where("gstr2bmatchingStatus").is(""), Criteria.where("gstr2bmatchingStatus").is(null)));
		if(NullUtil.isNotEmpty(filter) && NullUtil.isEmpty(filter.getInvoiceType())) {
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR2BSupport>(mongoTemplate.find(query, GSTR2BSupport.class, "gst2b_support"));
		}else {
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
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList),Criteria.where("cdnra.0.nt.0.ntty").in(creditDebitList)));
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList),Criteria.where("cdnra.0.nt.0.ntty").in(creditDebitList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdnra.0.nt.0.ntty").in(creditDebitList)));
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
		query.fields().include("gstr2bMatchingId");
		query.fields().include("gstr2bMatchingStatus");
		query.fields().include("gstr2bMatchingRsn");
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

	public Page<? extends InvoiceParent> findByPrMatchingIdAndMatchingStatus(String invoiceid, String mannualMatched, Pageable pageable) {
		
		Criteria criteria = Criteria.where("gstr2bMatchingId").is(invoiceid);
		
		Query query = Query.query(criteria);
		
		query.with(pageable);
		long total = mongoTemplate.count(query, PurchaseRegister.class, "purchaseregister");
		if (total == 0) {
			return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
		}
		return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, "purchaseregister"), pageable, total);
	}
	
	public Page<? extends InvoiceParent> findByG2bMatchingIdAndMatchingStatus(String invoiceid, String mannualMatched, Pageable pageable) {
		
		Criteria criteria = Criteria.where("gstr2bMatchingId").is(invoiceid);
		
		Query query = Query.query(criteria);
		
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR2BSupport.class, "gst2b_support");
		if (total == 0) {
			return new PageImpl<GSTR2BSupport>(Collections.<GSTR2BSupport> emptyList());
		}
		return new PageImpl<GSTR2BSupport>(mongoTemplate.find(query, GSTR2BSupport.class, "gst2b_support"), pageable, total);
	}

	
	//Performance Changes Code
	
	@SuppressWarnings("unchecked")
	public List<String> getBillToNames(String clientId, int month, String yearCd, boolean isTransactionDate, boolean isMonthly) {
		Criteria criteria = Criteria.where("clientid").is(clientId).and("invtype").in(invTypes);
		if(isMonthly) {
			criteria.and("mthCd").is(month+"");
		}
		if(!isTransactionDate) {
			criteria.and("yrCd").is(yearCd);
		}else {
			criteria.and("trDateyrCd").is(yearCd);
		}
		Query query = Query.query(criteria);
		Criteria criteria1 = Criteria.where("clientid").is(clientId);
		Query query1 = Query.query(criteria1);
		
		List<String> billToNames = mongoTemplate.getCollection("purchaseregister").distinct("billedtoname", query.getQueryObject());
		List<String> billToNamess = mongoTemplate.getCollection("gst2b_support").distinct("billedtoname", query1.getQueryObject());

		List<String> elements = Lists.newArrayList();

		if (isNotEmpty(billToNames)) {
			elements.addAll(billToNames);
		}
		if (isNotEmpty(billToNamess)) {
			elements.addAll(billToNamess);
		}
		Stream<String> suppliers = elements.stream().distinct();
		return suppliers.collect(Collectors.toList());
	}
	
	public Page<PurchaseRegister> getReconcilePurchaseInvoices(String id, String clientid, int month, int year, String yearCd, int start, int length, String searchVal, InvoiceFilter filter, boolean isTransactionDate, boolean isMonthly) {
		
		String mCd = "mthCd";
		String yCd = "yrCd";
		if(isTransactionDate) {
			mCd = "trDatemthCd";
			yCd = "trDateyrCd";
		}
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and(yCd).is(yearCd)
				.and("gstr2bMatchingRsn").nin("PR-Multiple");
		if(isMonthly) {
			criteria.and(mCd).is(month+"");
		}
		if(filter.getInvoiceType() == null) {
			criteria.and("invtype").in(invTypes);
		}
		
		applyReconcileFilterToCriteria(criteria, filter, MasterGSTConstants.PURCHASE_REGISTER);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueForReconcileCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, PurchaseRegister.class, "purchaseregister");
		if (total == 0) {
			return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
		}
		return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, "purchaseregister"), pageable, total);
	}

	public List<GSTR2BSupport> getMatchingIdInvoices(List<String> matchedIds) {
		Criteria criteria = Criteria.where("_id").in(matchedIds);
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		return mongoTemplate.find(query, GSTR2BSupport.class, "gst2b_support");
	}

	public long getGstr2bInvoicesCount(String clientid, List<String> fp, int start, int length, String searchVal, InvoiceFilter filter) {
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").in(fp)
				.and("gstr2bMatchingRsn").nin("PR-Single", "G2B-Multiple", "G2B-Single");
		if(filter.getDocumentType() == null){
			criteria.and("gstr2bMatchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
		}else {
			boolean gflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_NOTINPURCHASES);
			boolean mflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
			if(!gflag && !mflag) {
				return 0l;
			}
		}
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueForReconcileCriteria(searchVal));
		}
		if(NullUtil.isNotEmpty(filter) && NullUtil.isEmpty(filter.getInvoiceType())) {
			criteria.and("invtype").in(invTypes);
		}
		applyReconcileFilterToCriteria(criteria, filter, MasterGSTConstants.GSTR2B);
		Query query = Query.query(criteria);
		
		return mongoTemplate.count(query, GSTR2BSupport.class, "gst2b_support");
	}

	public Page<GSTR2BSupport> getGstr2bInvoiceswithPagination(String clientid, List<String> fp, int start, int length,String searchVal, InvoiceFilter filter) {
		
		Criteria criteria = Criteria.where("clientid").in(clientid).and("fp").in(fp)
				.and("gstr2bMatchingRsn").nin("PR-Single", "G2B-Multiple", "G2B-Single");
		if(filter.getDocumentType() == null){
			criteria.and("gstr2bMatchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
		}else {
			boolean gflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_NOTINPURCHASES);
			boolean mflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
			if(!gflag && !mflag) {
				return new PageImpl<GSTR2BSupport>(Collections.<GSTR2BSupport> emptyList());
			}
		}
		if(NullUtil.isNotEmpty(filter) && NullUtil.isEmpty(filter.getInvoiceType())) {
			criteria.and("invtype").in(invTypes);
		}
		applyReconcileFilterToCriteria(criteria, filter, MasterGSTConstants.GSTR2B);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueForReconcileCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		
		long total = mongoTemplate.count(query, GSTR2BSupport.class, "gst2b_support");
		if (total == 0) {
			return new PageImpl<GSTR2BSupport>(Collections.<GSTR2BSupport> emptyList());
		}
		return new PageImpl<GSTR2BSupport>(mongoTemplate.find(query, GSTR2BSupport.class, "gst2b_support"), pageable, total);
	}
	
	public TotalInvoiceAmount getPurchaseTotalInvoicesAmounts(String clientid, int month, String yearCd, String searchVal, InvoiceFilter filter, boolean isTransactionDate, boolean isMonthly) {

		String mCd = "mthCd";
		String yCd = "yrCd";
		if(isTransactionDate) {
			mCd = "trDatemthCd";
			yCd = "trDateyrCd";
		}
		Criteria criteria = Criteria.where("clientid").is(clientid).and(yCd).is(yearCd)
			.and("gstr2bMatchingRsn").nin("PR-Multiple");
		if(isMonthly) {
			criteria.and(mCd).is(month+"");
		}
		
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);				
		}
		
		applyReconcileFilterToCriteria(criteria, filter, MasterGSTConstants.PURCHASE_REGISTER);

		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueForReconcileCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		
		ProjectionOperation projectionOperation = getProjectionForTotal(yCd);//mCd
		GroupOperation groupOperation = getGroupForTotal(yCd);//mCd
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "purchaseregister", TotalInvoiceAmount.class);
				
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		return totalnvAmount;
	}
	
	public TotalInvoiceAmount getGstr2bTotalInvoicesAmounts(String clientid, List<String> fp, String searchVal, InvoiceFilter filter) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").in(fp)
				.and("gstr2bMatchingRsn").nin("PR-Single", "G2B-Multiple", "G2B-Single");
		if(filter.getDocumentType() == null){
			criteria.and("gstr2bMatchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
		}else {
			boolean gflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_NOTINPURCHASES);
			boolean mflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
			if(!gflag && !mflag) {
				return new TotalInvoiceAmount();
			}
		}
		if(NullUtil.isNotEmpty(filter) && NullUtil.isEmpty(filter.getInvoiceType())) {
			criteria.and("invtype").in(invTypes);
		}
		applyReconcileFilterToCriteria(criteria, filter, MasterGSTConstants.GSTR2B);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueForReconcileCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("csftr");//fp
		GroupOperation groupOperation = getGroupForTotal("csftr");//fp
				
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "gst2b_support", TotalInvoiceAmount.class);
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		
		return totalnvAmount;
	}
	
	public long getPurchaseReconcileSummary(String clientid, int month, String yearCd, String type, boolean isTransactionDate, boolean isMonthly) {
		String mCd = "mthCd";
		String yCd = "yrCd";
		if(isTransactionDate) {
			mCd = "trDatemthCd";
			yCd = "trDateyrCd";
		}
		Criteria criteria = Criteria.where("clientid").is(clientid).and("invtype").in(invTypes)
						.and(yCd).is(yearCd);
		if(isMonthly) {
			criteria.and(mCd).is(month+"");
		}
		
		if(type.equals(MasterGSTConstants.GST_STATUS_NOTINGSTR2B)) {
			criteria.and("gstr2bMatchingStatus").in("", MasterGSTConstants.GST_STATUS_NOTINGSTR2B, null);				
		}else {
			if(type.equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)) {
				criteria.and("gstr2bMatchingStatus").is(type).and("gstr2bMatchingRsn").nin("PR-Multiple");
			}else {
				criteria.and("gstr2bMatchingStatus").is(type);
			}
		}
		
		Query query = Query.query(criteria);
		long total = mongoTemplate.count(query, PurchaseRegister.class, "purchaseregister");
		return total;
		
	}

	public long getGstr2bReconcileSummary(String clientid, List<String> fp, String type) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").in(fp).and("invtype").in(invTypes);
		
		if(type.equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)) {
			criteria.and("gstr2bMatchingStatus").is(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)
			.and("gstr2bMatchingRsn").nin("PR-Single", "G2B-Multiple", "G2B-Single");
		}else {
			criteria.and("gstr2bMatchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES);			
		}
		
		Query query = Query.query(criteria);
		long total = mongoTemplate.count(query, GSTR2BSupport.class, "gst2b_support");
		
		return total;
	}
	
	public long getGstr2bInvoicesCount(String clientid, String fp) {
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").is(fp);
			//criteria.and("gstr2bMatchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES);
		
		Query query = Query.query(criteria);
		return mongoTemplate.count(query, GSTR2BSupport.class, "gst2b_support");
	}

	private void applyReconcileFilterToCriteria(Criteria criteria, InvoiceFilter filter, String returntype){
		if(filter != null){
			List<Criteria> criterias = new ArrayList<Criteria>();
			
			if(filter.getDocumentType() != null){
				
				if(returntype.equals(MasterGSTConstants.GSTR2B)) {
					boolean gflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_NOTINPURCHASES);
					boolean mflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
					
					if(gflag && !mflag){
						List<String> status = Arrays.asList("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES);
						criteria.and("gstr2bMatchingStatus").in(status);
					}else if(!gflag && mflag){
						List<String> status = Arrays.asList(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
						criteria.and("gstr2bMatchingStatus").in(status);
					}else if(gflag && mflag){
						List<String> status = Arrays.asList("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
						criteria.and("gstr2bMatchingStatus").in(status);
					}else {
						return;
					}
				}else {
					boolean pflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_NOTINGSTR2B);
					if(pflag) {
						List<String> invStatusList = new LinkedList<String>(Arrays.asList(filter.getDocumentType()));
						
						if(invStatusList.contains(MasterGSTConstants.GST_STATUS_NOTINGSTR2B)){
							Criteria criteriaa = null;
							if(NullUtil.isNotEmpty(invStatusList) && NullUtil.isNotEmpty(invStatusList)) {
								criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(
										Criteria.where("gstr2bMatchingStatus").in(invStatusList),
										Criteria.where("gstr2bMatchingStatus").in("", null)));
							}
							if(NullUtil.isNotEmpty(criteriaa)) {
								criterias.add(criteriaa);
							}
						}
					}else {
						criteria.and("gstr2bMatchingStatus").in(Arrays.asList(filter.getDocumentType()));					
					}
				}
			}
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
							Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList)));
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(
							Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList)));
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
			if(NullUtil.isNotEmpty(criterias)) {
				criteria.andOperator(criterias.toArray(new Criteria[criterias.size()]));				
			}
		}
	}
	
	private Criteria getSearchValueForReconcileCriteria(final String searchVal){
		List<Criteria> criterias = new ArrayList<Criteria>();
 		criterias.add(Criteria.where("fullname").regex(searchVal, "i"));	
 		criterias.add(Criteria.where("invtype").regex(searchVal, "i"));
 		criterias.add(Criteria.where("billedtoname").regex(searchVal, "i"));
 		criterias.add(Criteria.where("invoiceno").regex(searchVal, "i"));
 		criterias.add(Criteria.where("b2b.0.ctin").regex(searchVal, "i"));
 		criterias.add(Criteria.where("cdn.0.nt.0.ntty").regex(searchVal, "i"));
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
}
