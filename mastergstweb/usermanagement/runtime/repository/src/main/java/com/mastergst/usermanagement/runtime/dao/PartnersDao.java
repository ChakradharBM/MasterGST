package com.mastergst.usermanagement.runtime.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.PartnerClient;
import com.mastergst.usermanagement.runtime.domain.PartnerFilter;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.domain.TotalLeadDetails;

@Service
public class PartnersDao {
	private static final String COLLECTION_NAME = "partner_client";
	@Autowired
	protected MongoTemplate mongoTemplate;
	
	public Page<PartnerClient> getPartners(String id, int start, int length, String searchVal){
		Criteria criteria = Criteria.where("userid").is(id).and("clientid").in("", null)
				//.and("status").nin(MasterGSTConstants.SUBSCRIBED)
				.and("salesstatus").nin(MasterGSTConstants.NOT_REQUIRED);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllClientQueryFirlds(query);
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Sort sort = new Sort(new Order(Direction.ASC, "createdDate"));
		
		Pageable pageable = new PageRequest((start/length), length, sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, PartnerClient.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<PartnerClient>(Collections.<PartnerClient> emptyList());
		}
		return new PageImpl<PartnerClient>(mongoTemplate.find(query, PartnerClient.class, COLLECTION_NAME), pageable, total);
	}
	
	public Page<PartnerClient> getSalesTeamPartners(String tabName, List<String> userids, int month,String yearCode,int start, int length,String sortParam, String sortOrder, String searchVal) {
		Query query = new Query();
		Criteria criteria = Criteria.where("userid").in(userids).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		query.addCriteria(criteria);
		Sort sort = null;
		if(NullUtil.isNotEmpty(sortParam) && NullUtil.isNotEmpty(sortOrder)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(sortOrder) ? Direction.ASC : Direction.DESC, sortParam));
		}
		Pageable pageable = new PageRequest(start, length, sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, PartnerClient.class, "partner_client");
		if (total == 0) {
			return new PageImpl<PartnerClient>(Collections.<PartnerClient> emptyList());
		}
		return new PageImpl<PartnerClient>(mongoTemplate.find(query, PartnerClient.class, "partner_client"), pageable, total);
	}
	
	public Page<PartnerClient> getSalesTeamPartners(String tabName, List<String> userids, int month,String yearCode,int start, int length,String sortParam, String sortOrder, String searchVal, PartnerFilter filter) {
		Query query = new Query();
		Criteria criteria = Criteria.where("userid").in(userids).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		if(filter.getPartnername() != null){
			criteria.and("partnername").in(Arrays.asList(filter.getPartnername()));
		}
		query.addCriteria(criteria);
		Sort sort = null;
		if(NullUtil.isNotEmpty(sortParam) && NullUtil.isNotEmpty(sortOrder)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(sortOrder) ? Direction.ASC : Direction.DESC, sortParam));
		}
		Pageable pageable = new PageRequest(start, length, sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, PartnerClient.class, "partner_client");
		if (total == 0) {
			return new PageImpl<PartnerClient>(Collections.<PartnerClient> emptyList());
		}
		return new PageImpl<PartnerClient>(mongoTemplate.find(query, PartnerClient.class, "partner_client"), pageable, total);
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
			criterias.add(Criteria.where("clienttype").regex(searchVal, "i"));
			criterias.add(Criteria.where("name").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("email").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("mobilenumber").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("estimatedCost").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("status").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("createdDate").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("updatedDate").regex(searchVal, "i"));
	 		criterias.add(Criteria.where("joinDate").regex(searchVal, "i"));
	 		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
		}
	
	private void addAllClientQueryFirlds(Query query){
		query.fields().include("clienttype");
		query.fields().include("name");
		query.fields().include("email");
		query.fields().include("estimatedCost");
		query.fields().include("subscriptionAmount");
		query.fields().include("status");
		query.fields().include("mobilenumber");
		query.fields().include("createdDate");
		query.fields().include("joinDate");
		query.fields().include("updatedDate");
		query.fields().include("content");
	}

	public TotalLeadDetails getTotalLeadCounts(String tabName, List<String> userids, int month, String yearCode,int start, int length, String sortParam, String sortOrder, String searchVal) {
		Criteria criteria = Criteria.where("userid").in(userids).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		//applyFilterToCriteria(criteria, filter);
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
		AggregationResults<TotalLeadDetails> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "partner_client", TotalLeadDetails.class);
		return results.getUniqueMappedResult();
	}
	private ProjectionOperation getProjectionForTotal(String fieldname){
		/*return Aggregation.project().andInclude(fieldname)
				//.and("status").multiply("sftr").as("totalLeads")
				.and("status").multiply("sftr").as("totalNew")
				.and("status").multiply("sftr").as("totalPending")
				.and("status").multiply("sftr").as("totalJoined"); */
		return Aggregation.project().andInclude(fieldname)
				.and("estimatedCost").multiply(1).as("estimatedCost")
				.and("subscriptionAmount").multiply(1).as("subscriptionAmount");
	}
	
	private GroupOperation getGroupForTotal(String fieldname){
		/*return Aggregation.group(fieldname).count().as("totalLeads")
				.sum("status").as("totalNew")
				.sum("status").as("totalPending")
				.sum("status").as("totalJoined"); */
		return Aggregation.group(fieldname).count().as("totalLeads")
				.sum("estimatedCost").as("estimatedCost")
				.sum("subscriptionAmount").as("subscriptionAmount");
	}

	public List<TotalLeadDetails> getConsolidatedSummeryForYear(String yearCode) {
		Criteria criteria = Criteria.where("yrCd").is(yearCode);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project()
				.and("estimatedCost").multiply(1).as("estimatedCost")
				.and("subscriptionAmount").multiply(1).as("subscriptionAmount");;
		GroupOperation groupOperation = Aggregation.group("mthCd")
				.sum("estimatedCost").as("totalEstimatedRevenue")
				.sum("subscriptionAmount").as("subscriptionAmount").count().as("totalLeads");		
		AggregationResults<TotalLeadDetails> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "partner_client", TotalLeadDetails.class);
		return results.getMappedResults();
	}
	public List<TotalLeadDetails> getConsolidatedSummeryForDaysInMonth(String yearCode,int month,String dayWeek,String tabName,List<String> userids, PartnerFilter filter) {
		Criteria criteria = Criteria.where("yrCd").is(yearCode);
		criteria.and("userid").in(userids);
		if(filter.getPartnername() != null){
			criteria.and("partnername").in(Arrays.asList(filter.getPartnername()));
		}
		String fieldname = "dayCd";
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
			if("weekTable".equalsIgnoreCase(dayWeek)) {
				fieldname = "weekCd";
			}
		}else {
			fieldname = "mthCd";
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().andInclude(fieldname)
				.and("estimatedCost").multiply(1).as("estimatedCost")
				.and("subscriptionAmount").multiply(1).as("subscriptionAmount");
		GroupOperation groupOperation = Aggregation.group(fieldname).count().as("totalLeads")
				.sum("estimatedCost").as("estimatedCost")
				.sum("subscriptionAmount").as("subscriptionAmount");		
		AggregationResults<TotalLeadDetails> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "partner_client", TotalLeadDetails.class);
		return results.getMappedResults();
	}
	
	public List<TotalLeadDetails> getConsolidatedSummeryForDaysInMonthPending(String yearCode,int month,String dayWeek,String tabName,List<String> userids, PartnerFilter filter) {
		Criteria criteria = Criteria.where("yrCd").is(yearCode).and("status").is("Pending");
		criteria.and("userid").in(userids);
		if(filter.getPartnername() != null){
			criteria.and("partnername").in(Arrays.asList(filter.getPartnername()));
		}
		String fieldname = "dayCd";
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
			if("weekTable".equalsIgnoreCase(dayWeek)) {
				fieldname = "weekCd";
			}
		}else {
			fieldname = "mthCd";
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().andInclude(fieldname)
				.and("estimatedCost").multiply(1).as("estimatedCost")
				.and("subscriptionAmount").multiply(1).as("subscriptionAmount");
		GroupOperation groupOperation = Aggregation.group(fieldname).count().as("totalPending")
				.sum("estimatedCost").as("estimatedCost")
				.sum("subscriptionAmount").as("subscriptionAmount");		
		AggregationResults<TotalLeadDetails> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "partner_client", TotalLeadDetails.class);
		return results.getMappedResults();
	}
	
	public List<TotalLeadDetails> getConsolidatedSummeryForDaysInMonthJoined(String yearCode,int month,String dayWeek,String tabName,List<String> userids, PartnerFilter filter) {
		Criteria criteria = Criteria.where("yrCd").is(yearCode).and("status").is("Joined");
		criteria.and("userid").in(userids);
		if(filter.getPartnername() != null){
			criteria.and("partnername").in(Arrays.asList(filter.getPartnername()));
		}
		String fieldname = "dayCd";
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
			if("weekTable".equalsIgnoreCase(dayWeek)) {
				fieldname = "weekCd";
			}
		}else {
			fieldname = "mthCd";
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().andInclude(fieldname)
				.and("estimatedCost").multiply(1).as("estimatedCost")
				.and("subscriptionAmount").multiply(1).as("subscriptionAmount");
		GroupOperation groupOperation = Aggregation.group(fieldname).count().as("totalJoined")
				.sum("estimatedCost").as("estimatedCost")
				.sum("subscriptionAmount").as("subscriptionAmount");		
		AggregationResults<TotalLeadDetails> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "partner_client", TotalLeadDetails.class);
		return results.getMappedResults();
	}
	public List<TotalLeadDetails> getConsolidatedSummeryForDaysInMonthNew(String yearCode,int month,String dayWeek,String tabName,List<String> userids, PartnerFilter filter) {
		Criteria criteria = Criteria.where("yrCd").is(yearCode).and("status").is("New");
		criteria.and("userid").in(userids);
		if(filter.getPartnername() != null){
			criteria.and("partnername").in(Arrays.asList(filter.getPartnername()));
		}
		String fieldname = "dayCd";
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
			if("weekTable".equalsIgnoreCase(dayWeek)) {
				fieldname = "weekCd";
			}
		}else {
			fieldname = "mthCd";
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().andInclude(fieldname)
				.and("estimatedCost").multiply(1).as("estimatedCost")
				.and("subscriptionAmount").multiply(1).as("subscriptionAmount");
		GroupOperation groupOperation = Aggregation.group(fieldname).count().as("totalNew")
				.sum("estimatedCost").as("estimatedCost")
				.sum("subscriptionAmount").as("subscriptionAmount");		
		AggregationResults<TotalLeadDetails> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "partner_client", TotalLeadDetails.class);
		return results.getMappedResults();
	}
	
	public List<TotalLeadDetails> getConsolidatedSummeryForDaysInMonthDemo(String yearCode,int month,String dayWeek,String tabName,List<String> userids, PartnerFilter filter) {
		Criteria criteria = Criteria.where("yrCd").is(yearCode).and("demostatus").is(true);
		criteria.and("userid").in(userids);
		if(filter.getPartnername() != null){
			criteria.and("partnername").in(Arrays.asList(filter.getPartnername()));
		}
		String fieldname = "dayCd";
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
			if("weekTable".equalsIgnoreCase(dayWeek)) {
				fieldname = "weekCd";
			}
		}else {
			fieldname = "mthCd";
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().andInclude(fieldname)
				.and("estimatedCost").multiply(1).as("estimatedCost")
				.and("subscriptionAmount").multiply(1).as("subscriptionAmount");
		GroupOperation groupOperation = Aggregation.group(fieldname).count().as("totalDemo")
				.sum("estimatedCost").as("estimatedCost")
				.sum("subscriptionAmount").as("subscriptionAmount");		
		AggregationResults<TotalLeadDetails> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "partner_client", TotalLeadDetails.class);
		return results.getMappedResults();
	}
	
	public List<String> getPartnerNames(String yearCode,int month,String dayWeek,String tabName,List<String> userids){
		Criteria criteria = Criteria.where("yrCd").is(yearCode);
		if("salesTab".equalsIgnoreCase(tabName) || "partnersTab".equalsIgnoreCase(tabName)) {
			criteria.and("userid").in(userids);
		}
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		Query query = Query.query(criteria);
		List partnerNames = mongoTemplate.getCollection("partner_client").distinct("partnername", query.getQueryObject());
		return (List<String>)partnerNames;
	}
}
