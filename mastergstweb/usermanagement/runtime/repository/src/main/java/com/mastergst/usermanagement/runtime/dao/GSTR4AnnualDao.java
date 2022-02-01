package com.mastergst.usermanagement.runtime.dao;

import java.util.Collections;
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

import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;

@Service
public class GSTR4AnnualDao extends InvoiceDao {
	public GSTR4AnnualDao(){
		super("purchaseregister");
	}
	public Page<PurchaseRegister> getSupplierWiseInvoices(final String clientid, int month, String yearCode, String invtype,String revchargeType){
		Criteria criteria = null;
		if(NullUtil.isNotEmpty(invtype) && NullUtil.isNotEmpty(revchargeType)) {
			criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("invtype").is(invtype).and("revchargetype").is(revchargeType);
		}else if(NullUtil.isEmpty(revchargeType)) {
			criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("invtype").is(invtype);
		}
		
			Query query = Query.query(criteria);
			//addAllInvoicesQueryFirlds(query);
			long total = mongoTemplate.count(query, PurchaseRegister.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
			}
			return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME));
	}

	public TotalInvoiceAmount getPurchaseInvoices(final String clientid, int month, String yearCode, String invtype,String revchargeType){
		Criteria criteria = null;
		if(NullUtil.isNotEmpty(invtype) && NullUtil.isNotEmpty(revchargeType)) {
			criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("invtype").is(invtype).and("revchargetype").is(revchargeType);
		}else if(NullUtil.isEmpty(revchargeType)) {
			criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("invtype").is(invtype);
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = null;
		GroupOperation groupOperation = null;
		projectionOperation = getProjectionForTotal("ctinftr");
		groupOperation = getGroupForTotal("ctinftr");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	}
	
	private ProjectionOperation getProjectionForTotal(String fieldname){
		return Aggregation.project().andInclude(fieldname)
				.and("totalamount").multiply("ctinftr").as("totalamount")
				.and("totaltaxableamount").multiply("ctinftr").as("totaltaxableamount")
				.and("totalExemptedAmount").multiply("ctinftr").as("totalExemptedAmount")
				.and("totaltax").multiply("ctinftr").as("totaltax")
				.and("totalIgstAmount").multiply("ctinftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("ctinftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("ctinftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("ctinftr").as("totalCessAmount");
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
	private void addAllInvoicesQueryFirlds(Query query){
		query.fields().include("revchargetype");
		query.fields().include("invtype");
		query.fields().include("b2b");
		query.fields().include("b2bur");
		query.fields().include("impServices");
		query.fields().include("totaltaxableamount");
		query.fields().include("totaltax");
		query.fields().include("totalIgstAmount");
		query.fields().include("totalCgstAmount");
		query.fields().include("totalSgstAmount");
		query.fields().include("totalamount");
	}
	
	public Page<PurchaseRegister> getSupplierWiseInvoicess(final String clientid, int month, String yearCode, String invtype,String revchargeType, int start, int length, String searchVal){
		Criteria criteria = null;
		if(NullUtil.isNotEmpty(invtype) && NullUtil.isNotEmpty(revchargeType)) {
			criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("invtype").is(invtype).and("revchargetype").is(revchargeType);
		}else if(NullUtil.isEmpty(revchargeType)) {
			criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("invtype").is(invtype);
		}
		
		Query query = Query.query(criteria);
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		
		long total = mongoTemplate.count(query, PurchaseRegister.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<PurchaseRegister>(Collections.<PurchaseRegister> emptyList());
		}
		return new PageImpl<PurchaseRegister>(mongoTemplate.find(query, PurchaseRegister.class, COLLECTION_NAME), pageable, total);
	}

	public TotalInvoiceAmount getSupplierWiseAmounts(final String clientid, int month, String yearCode, String invtype,String revchargeType){
		Criteria criteria = null;
		if(NullUtil.isNotEmpty(invtype) && NullUtil.isNotEmpty(revchargeType)) {
			criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("invtype").is(invtype).and("revchargetype").is(revchargeType);
		}else if(NullUtil.isEmpty(revchargeType)) {
			criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("invtype").is(invtype);
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = null;
		GroupOperation groupOperation = null;
		projectionOperation = getProjectionForTotal("ctinftr");
		groupOperation = getGroupForTotal("ctinftr");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	}
}
