package com.mastergst.usermanagement.runtime.dao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

public abstract class InvoiceDao {

	protected final String COLLECTION_NAME;
	
	@Autowired
	protected MongoTemplate mongoTemplate;
	
	protected InvoiceDao(String collectionName){
		COLLECTION_NAME = collectionName;
	}
	
	public String getYearCode(int month, int year){
		return month<4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
	}
	
	public String getTotalTaxField(){
		return "totaltax";
	}
	
	public Map getClientTaxAndTotalAmountForMonth(String clientId, int month, int year){
		String yearCd = getYearCode(month, year);
		Criteria criteria = Criteria.where("clientid").is(clientId).and("mthCd").is(month+"").and("yrCd").is(yearCd);
		MatchOperation matchOperation = Aggregation.match(criteria);
		GroupOperation groupOperation = Aggregation.group("mthCd")
				.sum("totalamount").as("totalAmount")
				.sum(getTotalTaxField()).as("totalTaxAmount");
		AggregationResults<Map> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, groupOperation), COLLECTION_NAME, Map.class);
		return  results.getUniqueMappedResult();
	}
}
