package com.mastergst.usermanagement.runtime.dao;

import java.util.List;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;


@Service
public class Gstr5Dao extends InvoiceDao{

	public Gstr5Dao(){
		super("gstr5");
	}
	
	public List<TotalInvoiceAmount> getConsolidatedSummeryForYearMonthwise(final Client client, String yearCd, boolean checkQuarterly){
		
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.andInclude("mthCd","totaltax", "totalIgstAmount", "totalCgstAmount", "totalSgstAmount");
		GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				;
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return  results.getMappedResults();
	}
	
}
