package com.mastergst.usermanagement.runtime.dao;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.google.common.collect.Lists;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;


@Component
public class InvoicesMappingDao {

	private static final Logger logger = LogManager.getLogger(InvoicesMappingDao.class.getName());
	private static final String CLASSNAME = "InvoicesMappingDao::";

	@Autowired
	private MongoTemplate mongoTemplate;
	List<String> invTypes = null;
	{
		invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.IMP_GOODS);
	}
	List<String> rinvTypes = null;
	{
		rinvTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES);
	}
	
	public List<GSTR2> getGstr2aInvoices(List<String> matchedIds) {
		Criteria criteria = Criteria.where("_id").in(matchedIds);
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		return mongoTemplate.find(query, GSTR2.class, "gstr2");
	}
	public List<String> getBillToNames(String clientId, int month, String yearCd, boolean isMonthly,boolean isTransactionDate) {
		Criteria criteria = Criteria.where("clientid").is(clientId);
		/*if(isMonthly) {
			criteria.and("mthCd").is(month+"");
		}*/
		if(isMonthly) {
			 if(!isTransactionDate) {
				 criteria.and("yrCd").is(yearCd);
			 }else {
				 criteria.and("trDateyrCd").is(yearCd);
			 }
		}else {
			 criteria.and("yrCd").is(yearCd);
		 }
		Query query = Query.query(criteria);
		Criteria criteria1 = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCd);
		Query query1 = Query.query(criteria1);
		List<String> billToNames = mongoTemplate.getCollection("purchaseregister").distinct("billedtoname", query.getQueryObject());
		List<String> billToNamess = mongoTemplate.getCollection("gstr2").distinct("billedtoname", query1.getQueryObject());

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
	
	public Page<PurchaseRegister> getInvoices(String clientid, int month, String yearCd, int start, int length, String searchVal, InvoiceFilter filter,boolean isTransactiondate,boolean isYear) {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		Criteria criteria = Criteria.where("clientid").is(clientid).and("mannualMatchInvoices").ne("multiple");
		if(isYear) {
			 if(!isTransactiondate) {
				 if(month > 0) {
						criteria.and("mthCd").is(month+"");
					}
				 criteria.and("yrCd").is(yearCd);
			 }else {
				 if(month > 0) {
						criteria.and("trDatemthCd").is(month+"");
					}
				 criteria.and("trDateyrCd").is(yearCd);
			 }
		}else {
			if(month > 0) {
				criteria.and("mthCd").is(month+"");
			}
			 criteria.and("yrCd").is(yearCd);
		 }
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(rinvTypes);				
		}
		applyFilterToCriteria(criteria, filter, MasterGSTConstants.PURCHASE_REGISTER);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		
		long total = mongoTemplate.count(query, PurchaseRegister.class, "purchaseregister");
		if (total == 0) {
			return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
		}
		return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, "purchaseregister"), pageable, total);
	}
	
	public List<GSTR2> getGstr2aInvoices(String clientid, int month, String yearCd, int start, int length, String searchVal, InvoiceFilter filter,boolean isTransactiondate,boolean isYear) {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCd)
				.and("isAmendment").is(true).and("mannualMatchInvoices").in("", null, "Single");
		
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(rinvTypes);
		}
		if(filter.getDocumentType() == null){
			criteria.and("matchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
		}else {
			boolean gflag = Arrays.asList(filter.getDocumentType()).contains("Not In Purchases");
			boolean mflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
			if(!gflag && !mflag) {
				return Collections.<GSTR2> emptyList();
			}else if(mflag){}
		}
		applyGstr2aFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		return mongoTemplate.find(query, GSTR2.class, "gstr2");
	}
	
	public Page<GSTR2> getGstr2aInvoiceswithpagination(String clientid, int month, String yearCd, int start, int length, String searchVal, InvoiceFilter filter,boolean isTransactiondate,boolean isYear) {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCd)
				.and("isAmendment").is(true).and("mannualMatchInvoices").in("", null, "Single");
		
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(rinvTypes);
		}
		if(filter.getDocumentType() == null){
			criteria.and("matchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
		}else {
			boolean gflag = Arrays.asList(filter.getDocumentType()).contains("Not In Purchases");
			boolean mflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
			if(!gflag && !mflag) {
				return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
			}else if(mflag){}
		}
		applyGstr2aFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		
		long total = mongoTemplate.count(query, GSTR2.class, "gstr2");
		if (total == 0) {
			return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
		}
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, "gstr2"), pageable, total);
	}
	
	public Page<GSTR2> getGstr2aInvoiceswithpaginationm(String clientid, int month, String yearCd, int start, int length, String searchVal, InvoiceFilter filter,boolean isTransactiondate,boolean isYear) {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCd)
				.and("isAmendment").is(true).and("mannualMatchInvoices").in("", null, "Single");
		
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(rinvTypes);
		}
		if(filter.getDocumentType() == null){
			criteria.and("matchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
		}else {
			boolean gflag = Arrays.asList(filter.getDocumentType()).contains("Not In Purchases");
			boolean mflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
			if(!gflag && !mflag) {
				return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
			}else if(mflag){}
		}
		applyGstr2aFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		
		long total = mongoTemplate.count(query, GSTR2.class, "gstr2");
		if (total == 0) {
			return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
		}
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, "gstr2"), pageable, total);
	}
	
	public long getGstr2aInvoicesCount(String clientid, int month, String yearCd, int start, int length, String searchVal, InvoiceFilter filter,boolean isTransactiondate,boolean isYear) {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
				
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCd)
				.and("isAmendment").is(true).and("mannualMatchInvoices").in("", null, "Single");
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(rinvTypes);
		}
		if(filter.getDocumentType() == null){
			criteria.and("matchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
		}else {
			boolean gflag = Arrays.asList(filter.getDocumentType()).contains("Not In Purchases");
			boolean mflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
			if(!gflag && !mflag) {
				return 0l;
			}
		}
		applyGstr2aFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		//addAllInvoicesQueryFirlds(query);
		return mongoTemplate.count(query, GSTR2.class, "gstr2");
	}
	
	public TotalInvoiceAmount getPurchaseTotalInvoicesAmounts(String clientid, int month, String yearCd, String searchVal, InvoiceFilter filter,boolean isTransactiondate,boolean isYear){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("mannualMatchInvoices").ne("multiple");
		if(isYear) {
			 if(!isTransactiondate) {
				 if(month > 0) {
						criteria.and("mthCd").is(month+"");
					}
				 criteria.and("yrCd").is(yearCd);
			 }else {
				 if(month > 0) {
						criteria.and("trDatemthCd").is(month+"");
					}
				 criteria.and("trDateyrCd").is(yearCd);
			 }
		}else {
			if(month > 0) {
				criteria.and("mthCd").is(month+"");
			}
			 criteria.and("yrCd").is(yearCd);
		 }
		
		if( filter == null || filter.getInvoiceType() == null){
			criteria.and("invtype").in(rinvTypes);				
		}
		applyFilterToCriteria(criteria, filter, MasterGSTConstants.PURCHASE_REGISTER);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		
		ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
		GroupOperation groupOperation = getGroupForTotal("csftr");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "purchaseregister", TotalInvoiceAmount.class);
				
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		return totalnvAmount;
	}
	
	public TotalInvoiceAmount getGstr2aTotalInvoicesAmounts(String clientid, int month, String yearCd, String searchVal, InvoiceFilter filter,boolean isTransactiondate,boolean isYear){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCd)
				.and("isAmendment").is(true).and("mannualMatchInvoices").in("", null, "Single");
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(rinvTypes);				
		}
		if(filter.getDocumentType() == null){
			criteria.and("matchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
		}else {
			boolean gflag = Arrays.asList(filter.getDocumentType()).contains("Not In Purchases");
			boolean mflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
			
			if(gflag && !mflag){
				List<String> status = Arrays.asList("", null, "Not In Purchases");
				criteria.and("matchingStatus").in(status);
			}else if(!gflag && mflag){
				//criteria.and("mannualMatchInvoices").in("", null, "Single");
			}else if(gflag && mflag){
				List<String> status = Arrays.asList("", null, "Not In Purchases");
				criteria.and("matchingStatus").in(status);
			}else {
				return new TotalInvoiceAmount();				
			}
		}
		
		applyFilterToCriteria(criteria, filter, MasterGSTConstants.GSTR2A);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("yrCd");
		GroupOperation groupOperation = getGroupForTotal("yrCd");
				
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "gstr2", TotalInvoiceAmount.class);
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		
		return totalnvAmount;
	}
	
	public long getPurchaseReconcileSummary(String clientid, int month, String yearCd, String type,boolean isMonthly,boolean isTransactiondate) {
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("invtype").in(rinvTypes);
		if(isMonthly) {
			 if(!isTransactiondate) {
				 criteria.and("yrCd").is(yearCd);
			 }else {
				 criteria.and("trDateyrCd").is(yearCd);
			 }
		}else {
			 criteria.and("yrCd").is(yearCd);
		 }
		if(month > 0) {
			if(!isTransactiondate) {
				criteria.and("mthCd").is(month+"");
			}else {
				criteria.and("trDatemthCd").is(month+"");
			}
		}
		if(type.equals(MasterGSTConstants.GST_STATUS_NOTINGSTR2A)) {
			criteria.and("matchingStatus").in("", MasterGSTConstants.GST_STATUS_NOTINGSTR2A, null);				
		}else {
			
			if(type.equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)) {
				criteria.and("matchingStatus").is(type).and("mannualMatchInvoices").ne("multiple");
			}else {
				criteria.and("matchingStatus").is(type);					
			}
		}
		
		Query query = Query.query(criteria);
		long total = mongoTemplate.count(query, PurchaseRegister.class, "purchaseregister");
		return total;
	}
	
	public long getGstr2aReconcileSummary(String clientid, int month, String yearCd, String type) {
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCd)
				.and("isAmendment").is(true).and("invtype").in(rinvTypes);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if(type.equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)) {
			criteria.and("matchingStatus").is(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED).and("mannualMatchInvoices").in("", null, "Single");;
		}else {
			criteria.and("matchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES);			
		}
		
		Query query = Query.query(criteria);
		long total = mongoTemplate.count(query, GSTR2.class, "gstr2");
		
		return total;
	}
	
	public List<PurchaseRegister> getPurchaseRegistersMatchingStatusIs(final String invType, final String clientid, final int month, final int year,final String yearcode, boolean isYear, boolean istransactionDate,List<String> matchingstatuspr) {
		Date stDate = null;
		Date endDate = null;
		Date presentDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(presentDate);

		int presentYear = calendar.get(Calendar.YEAR);
		int presentMonth = calendar.get(Calendar.MONTH) + 1;
		Calendar cal = Calendar.getInstance();
		if (isYear) {
		if(month < 10) {
			cal.set(year-1, 3, 1, 0, 0, 0);
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
				if (presentYear != year) {
					cal.set(year, 12, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				} else {
					cal.set(year, presentMonth, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}

			} else {
				cal.set(year - 1, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				if (presentYear != year) {
					cal.set(year, 12, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				} else {
					cal.set(year, presentMonth, 0, 23, 59, 59);
			endDate = new java.util.Date(cal.getTimeInMillis());
				}
			}
		}else {
			cal.set(year, 3, 1, 0, 0, 0);
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(year + 1, 3, 0, 23, 59, 59);
			endDate = new java.util.Date(cal.getTimeInMillis());
		}
		Criteria criteria = Criteria.where("clientid").is(clientid)
				.and("invtype").is(invType)
				.and("matchingStatus").in(matchingstatuspr);
		 if(isYear) {
			 if(!istransactionDate) {
				 criteria.and("dateofinvoice").gte(stDate).lte(endDate);
			 }else {
				 criteria.and("billDate").gte(stDate).lte(endDate);
			 }
		}else {
			 criteria.and("yrCd").is(yearcode);
		 }
		Query query = Query.query(criteria);
		return mongoTemplate.find(query, PurchaseRegister.class, "purchaseregister");
		//return purchaseRepository.findByClientidAndInvtypeAndDateofinvoiceBetween(clientid, invType, stDate, endDate);
	}
	
	public List<GSTR2> findByClientidAndFpInAndInvtypeAndIsAmendment(String clientId, List<String> rtarray, String invType, boolean isAdmememt, Pageable pageable,List<String> matchingstatus) {
		
		Criteria criteria = Criteria.where("clientid").in(clientId)
				.and("fp").in(rtarray).and("isAmendment").is(true).and("invtype").is(invType).and("matchingStatus").in(matchingstatus);
		//criteria.and("matchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES);
		//criteria.and("matchingStatus").in(matchingstatus);
		/*if(isNotEmpty(reconsileMatchingIds)) {
			criteria.and("_id").nin(reconsileMatchingIds);
		}*/
		Query query = new Query();
		query.addCriteria(criteria);
		//Query query = Query.query(criteria);
		query.with(pageable);
		
		return mongoTemplate.find(query, GSTR2.class, "gstr2");
	}
	
	public long findByClientidAndFpInAndInvtypeAndIsAmendments(String clientId, List<String> rtarray, List<String> invType,List<String> matchingstatus, boolean isAdmememt) {
		Criteria criteria = Criteria.where("clientid").in(clientId)
				.and("fp").in(rtarray).and("isAmendment").is(true).and("invtype").in(invType).and("matchingStatus").in(matchingstatus);
		//criteria.and("matchingStatus").in("", null, MasterGSTConstants.GST_STATUS_NOTINPURCHASES);
		//criteria.and("matchingStatus").in(matchingstatus);
		/*if(isNotEmpty(reconsileMatchingIds)) {
			criteria.and("_id").nin(reconsileMatchingIds);
		}*/
		
		Query query = Query.query(criteria);
		return mongoTemplate.count(query, GSTR2.class, "gstr2");
		
	}
	
	
	
	public Page<GSTR2> findByClientidAndFpInAndInvtypeAndIsAmendmentAndMactingStatusIsNull(String clientId, List<String> rtarray, String invType, boolean isAdmememt, Pageable pageable) {
		List<String> matchingstatus = Lists.newArrayList();
		matchingstatus.add("");
		matchingstatus.add(null);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_NOTINPURCHASES);
		Criteria criteria = Criteria.where("clientid").is(clientId)
				.and("fp").in(rtarray).and("isAmendment").is(true).and("invtype").is(invType);
		criteria.and("matchingStatus").in(matchingstatus);
		Query query = Query.query(criteria);
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR2.class, "gstr2");
		if (total == 0) {
			return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
		}
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, "gstr2"), pageable, total);
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
	
	private void applyFilterToCriteria(Criteria criteria, InvoiceFilter filter, String rtype){
		if(filter != null){
			List<Criteria> criterias = new ArrayList<Criteria>();
			if(filter.getDocumentType() != null){
				
				if(rtype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
					boolean pflag = Arrays.asList(filter.getDocumentType()).contains("Not In GSTR 2A");
					if(pflag) {
						List<String> statusList = new LinkedList<String>(Arrays.asList(filter.getDocumentType()));
						statusList.add("");
						statusList.add(null);
						criteria.and("matchingStatus").in(statusList);										
					}else {
						criteria.and("matchingStatus").in(Arrays.asList(filter.getDocumentType()));					
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
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList)));
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList)) {
					criteria.and("invtype").in(invTypeList);
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList)) {
					criteria.and("cdn.0.nt.0.ntty").in(creditDebitList);
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
	
	private void applyGstr2aFilterToCriteria(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			List<Criteria> criterias = new ArrayList<Criteria>();
			
			if(filter.getDocumentType() != null){
				
				boolean gflag = Arrays.asList(filter.getDocumentType()).contains("Not In Purchases");
				boolean mflag = Arrays.asList(filter.getDocumentType()).contains(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
				
				if(gflag && !mflag){
					List<String> status = Arrays.asList("", null, "Not In Purchases");
					criteria.and("matchingStatus").in(status);
				}else if(!gflag && mflag){
				}else if(gflag && mflag){
					List<String> status = Arrays.asList("", null, "Not In Purchases", MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
					criteria.and("matchingStatus").in(status);
				}else {
					return;
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
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList)));
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList)) {
					criteria.and("invtype").in(invTypeList);
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) ) {
					criteria.and("cdn.0.nt.0.ntty").in(creditDebitList);
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
		query.fields().include("invtype");
		query.fields().include("fp");
		query.fields().include("billedtoname");
		query.fields().include("invoiceno");
		query.fields().include("dateofinvoice");
		query.fields().include("b2b");
		query.fields().include("cdn");
		query.fields().include("cdnur");
		query.fields().include("impGoods");

		query.fields().include("matchingId");
		query.fields().include("matchingStatus");
		query.fields().include("cfs");
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
	
}
