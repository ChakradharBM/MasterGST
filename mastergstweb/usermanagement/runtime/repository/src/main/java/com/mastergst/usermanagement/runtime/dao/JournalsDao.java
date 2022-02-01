package com.mastergst.usermanagement.runtime.dao;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import com.mastergst.core.common.AccountConstants;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.domain.TotalJournalAmount;

@Service
public class JournalsDao extends InvoiceDao{
	public JournalsDao(){
		super("accounting_journal");
	}
	
	public Page<AccountingJournal> findByClientidAndMonthAndYear(final String clientid, int month, String yearCode, int start, int length,String fieldName,String order){
		List<String> invtypelist = Lists.newArrayList();
		invtypelist.add(MasterGSTConstants.DELIVERYCHALLANS);
		invtypelist.add(MasterGSTConstants.PROFORMAINVOICES);
		invtypelist.add(MasterGSTConstants.PURCHASEORDER);
		invtypelist.add(MasterGSTConstants.ESTIMATES);
		Criteria criteria = Criteria.where("clientId").is(clientid)
				.and("version").is(AccountConstants.VERSION).and("yrCd").is(yearCode).and("invoiceType").nin(invtypelist);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		Sort sort = null;
		if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}
		if(start == 0 && length == 0){
			return new PageImpl<AccountingJournal>(mongoTemplate.find(query, AccountingJournal.class, COLLECTION_NAME));
		}else {
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length,sort);
			query.with(pageable);
			long total = mongoTemplate.count(query, AccountingJournal.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<AccountingJournal>(Collections.<AccountingJournal> emptyList());
			}
			return new PageImpl<AccountingJournal>(mongoTemplate.find(query, AccountingJournal.class, COLLECTION_NAME), pageable, total);
		}
	}
	
	
	public TotalJournalAmount getTotalInvoicesAmountsForMonth(String clientId, int month, String yearCode){
		List<String> invtypelist = Lists.newArrayList();
		invtypelist.add(MasterGSTConstants.DELIVERYCHALLANS);
		invtypelist.add(MasterGSTConstants.PROFORMAINVOICES);
		invtypelist.add(MasterGSTConstants.PURCHASEORDER);
		invtypelist.add(MasterGSTConstants.ESTIMATES);
		List<String> statuslist = Lists.newArrayList();
		statuslist.add("Cancelled");
		statuslist.add("Deleted");
		Criteria criteria = Criteria.where("clientId").is(clientId)
				.and("version").is(AccountConstants.VERSION).and("yrCd").is(yearCode).and("invoiceType")
				.nin(invtypelist).and("status").nin(statuslist);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
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
		AggregationResults<TotalJournalAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalJournalAmount.class);
			
		TotalJournalAmount totalnvAmount = results.getUniqueMappedResult();
		return totalnvAmount;
	}
	
	private ProjectionOperation getProjectionForTotal(String fieldname){
		return Aggregation.project().andInclude(fieldname)
				.and("debitTotal").as("debitTotal")
				.and("creditTotal").as("creditTotal");
	}
	
	private GroupOperation getGroupForTotal(String fieldname){
		return Aggregation.group(fieldname).count().as("totalTransactions")
				.sum("debitTotal").as("totalDebit")
				.sum("creditTotal").as("totalCredit");
	}
	
	private void addAllInvoicesQueryFirlds(Query query){
		query.fields().include("fullname");
		
		query.fields().include("dateofinvoice");
		query.fields().include("clientId");
		query.fields().include("userId");
		query.fields().include("invoiceId");
		query.fields().include("returnType");
		query.fields().include("drEntrie");
		query.fields().include("crEntrie");
		query.fields().include("invoiceType");
		query.fields().include("invoiceNumber");
		query.fields().include("mthCd");
		query.fields().include("qrtCd");
		query.fields().include("yrCd");
		query.fields().include("invoiceMonth");
		query.fields().include("roundOffAmount");
		query.fields().include("status");
	}
	
}
