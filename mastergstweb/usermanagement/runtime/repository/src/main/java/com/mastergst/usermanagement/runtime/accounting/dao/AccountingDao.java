package com.mastergst.usermanagement.runtime.accounting.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

import com.mastergst.core.common.AccountConstants;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.accounting.domain.TotalJournalAmount;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;

@Component
public class AccountingDao {
	@Autowired private MongoTemplate mongoTemplate;
	
	public List<String> getLedgerForBankCash(String clientid){
		List<String> groupList = Arrays.asList("Cash in hand","Bank accounts");
		Criteria criteria = Criteria.where("clientid").is(clientid).and("grpsubgrpName").in(groupList);
		Query query = Query.query(criteria);
		@SuppressWarnings("unchecked")
		List<String> ledgerNames = mongoTemplate.getCollection("ledger").distinct("ledgerName", query.getQueryObject());
		return ledgerNames;
	}
	public List<String> getLedgerForExpenses(String clientid,String ledgerName){
		List<String> groupList = Arrays.asList("Cash in hand","Bank accounts");
		Criteria criteria = Criteria.where("clientid").is(clientid).and("grpsubgrpName").in(groupList);
		Query query = Query.query(criteria);
		@SuppressWarnings("unchecked")
		List<String> ledgerNames = mongoTemplate.getCollection("ledger").distinct("ledgerName", query.getQueryObject());
		return ledgerNames;
	}
	
	public Page<AccountingJournal> findByClientidAndMonthAndYear(final String clientid, List<String> mthYrCode){
		int start = 0;
		Criteria criteria = Criteria.where("clientId").is(clientid)
				.and("version").is(AccountConstants.VERSION).and("invoiceMonth").in(mthYrCode).and("status").exists(false);
		
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		int length = Integer.MAX_VALUE;
	
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, AccountingJournal.class, "accounting_journal");
		if (total == 0) {
			return new PageImpl<AccountingJournal>(Collections.<AccountingJournal> emptyList());
		}
		return new PageImpl<AccountingJournal>(mongoTemplate.find(query, AccountingJournal.class, "accounting_journal"), pageable, total);
	}
	
	public Page<AccountingJournal> findByClientIdAndDateofinvoiceBetween(String clientid, Date stDate, Date endDate) {
		int start = 0;
		Criteria criteria = Criteria.where("clientId").is(clientid)
				.and("version").is(AccountConstants.VERSION).and("status").exists(false);
		criteria.and("dateofinvoice").gte(stDate).lte(endDate);
		
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		int length = Integer.MAX_VALUE;
	
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, AccountingJournal.class, "accounting_journal");
		if (total == 0) {
			return new PageImpl<AccountingJournal>(Collections.<AccountingJournal> emptyList());
		}
		return new PageImpl<AccountingJournal>(mongoTemplate.find(query, AccountingJournal.class, "accounting_journal"), pageable, total);
	}
	
	public List<AccountingJournal> getLedgersDataNew(String ledgername, String clientid, List<String> invoiceMonth) {
		
		Query query = new Query();
		Criteria criteria = Criteria.where("clientId").is(clientid)
				.and("invoiceMonth").in(invoiceMonth).and("status").exists(false);
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(new Criteria().orOperator(
							Criteria.where("crEntrie").elemMatch(Criteria.where("name").is(ledgername)),
							Criteria.where("drEntrie").elemMatch(Criteria.where("name").is(ledgername))
						)
				);
		criteria = new Criteria().andOperator(criteria, 
				new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		addAllInvoicesQueryFirlds(query);
		query.addCriteria(criteria);
		List<AccountingJournal> invoiceJournals = mongoTemplate.find(query, AccountingJournal.class, "accounting_journal");

		return invoiceJournals;
	}
	
	public List<AccountingJournal> getCustomLedgersDataNew(String ledgername, String clientid, Date startDate, Date endDate) {
		Query query=new  Query();
		
		Criteria criteria = Criteria.where("clientId").is(clientid).and("status").exists(false);
		criteria.and("dateofinvoice").gte(startDate).lte(endDate);
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(new Criteria().orOperator(
							Criteria.where("crEntrie").elemMatch(Criteria.where("name").is(ledgername)),
							Criteria.where("drEntrie").elemMatch(Criteria.where("name").is(ledgername))
						)
				);
		criteria = new Criteria().andOperator(criteria, 
				new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		addAllInvoicesQueryFirlds(query);
		query.addCriteria(criteria);
		
		List<AccountingJournal> invoiceJournals = mongoTemplate.find(query, AccountingJournal.class, "accounting_journal");
		
		return invoiceJournals;
	}
	
	
	public Page<AccountingJournal> getMontlyAndYearlyDeductionOrCollection(String clientid, int month, String yearCode, boolean isTds, int start, int length, String searchVal) {
		
		Query query = new Query();
		Criteria criteria = Criteria.where("clientId").is(clientid).and("status").exists(false).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		String ledgername = isTds ? AccountConstants.TDS_PAYABLE : AccountConstants.TCS_PAYABLE;
		
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(new Criteria().orOperator(
				Criteria.where("crEntrie").elemMatch(Criteria.where("name").is(ledgername)),
				Criteria.where("drEntrie").elemMatch(Criteria.where("name").is(ledgername))
			));
		criteria = new Criteria().andOperator(criteria,new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		addAllTdsQueryFirlds(query);
		query.addCriteria(criteria);
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, AccountingJournal.class, "accounting_journal");
		if (total == 0) {
			return new PageImpl<AccountingJournal>(Collections.<AccountingJournal> emptyList());
		}
		return new PageImpl<AccountingJournal>(mongoTemplate.find(query, AccountingJournal.class, "accounting_journal"), pageable, total);
	}
	
	
	private ProjectionOperation getProjectionForTotal(String fieldname){
		return Aggregation.project().andInclude(fieldname)
				.and("invoiceamount").as("invoiceamount")
				.and("tcs_payable").as("tcs_payable")
				.and("tds_payable").as("tds_payable");
	}
	
	private GroupOperation getGroupForTotal(String fieldname){
		return Aggregation.group(fieldname).count().as("totalTransactions")
				.sum("invoiceamount").as("invoiceamount")
				.sum("tcs_payable").as("tcs_payable")
				.sum("tds_payable").as("tds_payable");
	}
	
	public TotalJournalAmount getTotalInvoicesAmountsForMonth(String clientid, int month, String yearCode, boolean isTds, String searchVal){
		Criteria criteria = Criteria.where("clientId").is(clientid).and("status").exists(false).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		String ledgername = isTds ? AccountConstants.TDS_PAYABLE : AccountConstants.TCS_PAYABLE;
		
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(new Criteria().orOperator(
				Criteria.where("crEntrie").elemMatch(Criteria.where("name").is(ledgername)),
				Criteria.where("drEntrie").elemMatch(Criteria.where("name").is(ledgername))
			));
		criteria = new Criteria().andOperator(criteria,new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
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
		AggregationResults<TotalJournalAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "accounting_journal", TotalJournalAmount.class);
		
		TotalJournalAmount totalnvAmount = results.getUniqueMappedResult();
		return totalnvAmount;
	}
	
	public TotalJournalAmount getTotalInvoicesAmountsForCustom(String clientid, Date stDate, Date endDate,boolean isTds, String searchVal){
		Criteria criteria = Criteria.where("clientId").is(clientid).and("status").exists(false).and("dateofinvoice").gte(stDate).lte(endDate);
		String ledgername = isTds ? AccountConstants.TDS_PAYABLE : AccountConstants.TCS_PAYABLE;
		
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(new Criteria().orOperator(
				Criteria.where("crEntrie").elemMatch(Criteria.where("name").is(ledgername)),
				Criteria.where("drEntrie").elemMatch(Criteria.where("name").is(ledgername))
			));
		criteria = new Criteria().andOperator(criteria,new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
		GroupOperation groupOperation = getGroupForTotal("csftr");
		AggregationResults<TotalJournalAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "accounting_journal", TotalJournalAmount.class);
		return results.getUniqueMappedResult();
	}
	
	
	public Page<AccountingJournal> getCustomDeductionOrCollection(String clientid, Date stDate, Date endDate, boolean isTds, int start, int length, String searchVal) {
	
		Query query = new Query();
		Criteria criteria = Criteria.where("clientId").is(clientid).and("status").exists(false);
		criteria.andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		String ledgername = isTds ? AccountConstants.TDS_PAYABLE : AccountConstants.TCS_PAYABLE;
		
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(new Criteria().orOperator(
				Criteria.where("crEntrie").elemMatch(Criteria.where("name").is(ledgername)),
				Criteria.where("drEntrie").elemMatch(Criteria.where("name").is(ledgername))
			));
		criteria = new Criteria().andOperator(criteria,new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		addAllTdsQueryFirlds(query);
		query.addCriteria(criteria);
		
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, AccountingJournal.class, "accounting_journal");
		if (total == 0) {
			return new PageImpl<AccountingJournal>(Collections.<AccountingJournal> emptyList());
		}
		return new PageImpl<AccountingJournal>(mongoTemplate.find(query, AccountingJournal.class, "accounting_journal"), pageable, total);	
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
		criterias.add(Criteria.where("invoiceNumber").regex(searchVal, "i"));
 		criterias.add(Criteria.where("vendorName").regex(searchVal, "i"));
 		criterias.add(Criteria.where("ctin").regex(searchVal, "i"));
 		criterias.add(Criteria.where("pancategory").regex(searchVal, "i"));
 		criterias.add(Criteria.where("debitTotal").regex(searchVal, "i"));
 		criterias.add(Criteria.where("creditTotal").regex(searchVal, "i"));
 		criterias.add(Criteria.where("tdspercentage").regex(searchVal, "i"));
 		criterias.add(Criteria.where("tdssection").regex(searchVal, "i"));
 		criterias.add(Criteria.where("tcspercentage").regex(searchVal, "i"));
 		criterias.add(Criteria.where("tcssection").regex(searchVal, "i"));
 		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}
	
	private void addAllInvoicesQueryFirlds(Query query){
		query.fields().include("clientId");
		query.fields().include("returnType");
		query.fields().include("drEntrie");
		query.fields().include("crEntrie");
		query.fields().include("invoiceNumber");
		query.fields().include("dateofinvoice");
		query.fields().include("invoiceType");
	}
		
	private void addAllTdsQueryFirlds(Query query){
		query.fields().include("clientId");
		query.fields().include("returnType");
		query.fields().include("drEntrie");
		query.fields().include("crEntrie");
		query.fields().include("invoiceNumber");
		query.fields().include("vendorName");
		query.fields().include("dateofinvoice");
		query.fields().include("invoiceType");
		query.fields().include("tcspercentage");
		query.fields().include("tcssection");
		query.fields().include("tdspercentage");
		query.fields().include("tdssection");
		query.fields().include("ctin");
		query.fields().include("pancategory");
		query.fields().include("creditTotal");
		query.fields().include("debitTotal");
		query.fields().include("invoiceamount");
	}
	
	public List<TotalJournalAmount> getConsolidatedMultimonthSummeryForYearMonthwise(String clientid, String yearCode,boolean isTds){
		Query query = new Query();
		Criteria criteria = Criteria.where("clientId").is(clientid).and("status").exists(false).and("yrCd").is(yearCode);
		String ledgername = isTds ? AccountConstants.TDS_PAYABLE : AccountConstants.TCS_PAYABLE;
		
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(new Criteria().orOperator(
				Criteria.where("crEntrie").elemMatch(Criteria.where("name").is(ledgername)),
				Criteria.where("drEntrie").elemMatch(Criteria.where("name").is(ledgername))
			));
		criteria = new Criteria().andOperator(criteria,new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		query.addCriteria(criteria);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project()
				.and("invoiceamount").as("invoiceamount")
				.and("tcs_payable").as("tcs_payable")
				.and("tds_payable").as("tds_payable")
				.andInclude("mthCd");
		GroupOperation groupOperation = Aggregation.group("mthCd")
				.sum("invoiceamount").as("invoiceamount")
				.sum("tcs_payable").as("tcs_payable")
				.sum("tds_payable").as("tds_payable")
				.count().as("totalTransactions");
		AggregationResults<TotalJournalAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "accounting_journal", TotalJournalAmount.class);
		List<TotalJournalAmount> totalInvAmount = results.getMappedResults();
		return totalInvAmount;
	}
	
	public List<TotalJournalAmount> getConsolidatedMultimonthSummeryForCustomMonthwise(String clientid, Date fromdate, Date todate,boolean isTds){
		Query query = new Query();
		Criteria criteria = Criteria.where("clientId").is(clientid).and("status").exists(false);
		criteria.andOperator(Criteria.where("dateofinvoice").gte(fromdate),Criteria.where("dateofinvoice").lte(todate));
		String ledgername = isTds ? AccountConstants.TDS_PAYABLE : AccountConstants.TCS_PAYABLE;
		
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(new Criteria().orOperator(
				Criteria.where("crEntrie").elemMatch(Criteria.where("name").is(ledgername)),
				Criteria.where("drEntrie").elemMatch(Criteria.where("name").is(ledgername))
			));
		criteria = new Criteria().andOperator(criteria,new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		query.addCriteria(criteria);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project()
				.and("invoiceamount").as("invoiceamount")
				.and("tcs_payable").as("tcs_payable")
				.and("tds_payable").as("tds_payable")
				.andInclude("mthCd");
		GroupOperation groupOperation = Aggregation.group("mthCd")
				.sum("invoiceamount").as("invoiceamount")
				.sum("tcs_payable").as("tcs_payable")
				.sum("tds_payable").as("tds_payable")
				.count().as("totalTransactions");
		AggregationResults<TotalJournalAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "accounting_journal", TotalJournalAmount.class);
		List<TotalJournalAmount> totalInvAmount = results.getMappedResults();
		return totalInvAmount;
	}
	
	
	
}
