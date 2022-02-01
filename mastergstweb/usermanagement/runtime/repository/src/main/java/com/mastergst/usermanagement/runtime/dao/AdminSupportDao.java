package com.mastergst.usermanagement.runtime.dao;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.login.runtime.domain.ReconcileUsers;

@Component
public class AdminSupportDao {
	
	@Autowired private MongoTemplate mongoTemplate;
	
	
	public PageImpl<ReconcileUsers> getReconcileInfo(int start ,int length, String searchVal){
		Pageable pageable = new PageRequest((start/length), length);
		Aggregation aggregation = null;
	    Aggregation aggCount = null;
	    
	    Criteria criteria = Criteria.where("userid").nin("", null);
		
	    if(StringUtils.hasLength(searchVal)){
	    	if(searchVal.contains("(")) {
				searchVal = searchVal.replaceAll("\\(", "\\\\(");
			}
			if(searchVal.contains(")")) {
				searchVal = searchVal.replaceAll("\\)", "\\\\)");
			}
	    	criteria = new Criteria().andOperator(
	    			Criteria.where("fullname").regex(Pattern.compile(searchVal, Pattern.CASE_INSENSITIVE)),
	    			Criteria.where("mobilenumber").regex(Pattern.compile(searchVal, Pattern.CASE_INSENSITIVE)),
	    			Criteria.where("type").regex(Pattern.compile(searchVal, Pattern.CASE_INSENSITIVE)),
					Criteria.where("email").regex(Pattern.compile(searchVal, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
	    	aggregation = Aggregation.newAggregation(
	    			Aggregation.lookup("users", "userid", "_id", "user"),
	    			Aggregation.match(criteria),
	    			Aggregation.unwind("user", false),
	    			Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
	       			Aggregation.limit(pageable.getPageSize())
	       	);
	    	aggCount = Aggregation.newAggregation(
	    			Aggregation.lookup("users", "userid", "_id", "user"),
    				Aggregation.match(criteria),
    				Aggregation.unwind("user", false)
        	);
	    }else {
	       	aggregation = Aggregation.newAggregation(
	       			Aggregation.lookup("users", "userid", "_id", "user"),
	       			Aggregation.match(criteria),
	       			Aggregation.unwind("user", false),
	       			Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
	       			Aggregation.limit(pageable.getPageSize())
	       	);
	       	aggCount = Aggregation.newAggregation(
        			Aggregation.lookup("users", "userid", "_id", "user"),
        			Aggregation.match(criteria),
        			Aggregation.unwind("user", false)
        	);
	    }
	    
	    long total =  mongoTemplate.aggregate(aggCount, "reconciletemp", ReconcileUsers.class).getMappedResults().size();
	    
	    if (total == 0) {
			return new PageImpl<ReconcileUsers>(Collections.<ReconcileUsers> emptyList());
		}
	    
	    List<ReconcileUsers> aggregationResult = mongoTemplate.aggregate(aggregation, "reconciletemp", ReconcileUsers.class).getMappedResults();
        return new PageImpl<ReconcileUsers>(aggregationResult, pageable, total);
	}

}
