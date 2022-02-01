package com.mastergst.usermanagement.runtime.dao;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;


@Service
public class SubscriptionDataDao {

	private static final String COLLECTION_NAME = "subscription_data";
	
	@Autowired
	private MongoTemplate mongoTemplate;

	private static final String[] FIELDS_ARRAY = {"userid", "allowedInvoices", "registeredDate", "expiryDate","processedInvoices", "type", "userId","user","apiType", "subscriptionType"};
	
	public List<SubscriptionDetails> getSubscriptionsEndsByDays(int noOfDays){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, noOfDays);
		Criteria criteria = Criteria.where("expiryDate").lt(calendar.getTime());
		Query query = Query.query(criteria);
		query.fields().include("userid");
		query.fields().include("registeredDate");
		query.fields().include("expiryDate");
		query.fields().include("apiType");
		List<SubscriptionDetails> subscriptionDetails = mongoTemplate.find(query, SubscriptionDetails.class, COLLECTION_NAME);
		return subscriptionDetails;
	}
	
	public List<SubscriptionDetails> getSubscriptionsAboutToExpiry(){
		//AllowedInvoices - ProcessedInvoices < 5000
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.project(FIELDS_ARRAY).andExpression("allowedInvoices - processedInvoices").as("exceedInvoices"),
				Aggregation.match(new Criteria().andOperator(Criteria.where("exceedInvoices").lt(5000),Criteria.where("exceedInvoices").gt(0)))
		); 
		List<SubscriptionDetails> subscriptionDetails = mongoTemplate.aggregate(aggregation, COLLECTION_NAME, SubscriptionDetails.class).getMappedResults();
		return subscriptionDetails;
	}
	
	
	
}
