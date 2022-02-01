package com.mastergst.usermanagement.runtime.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mastergst.login.runtime.domain.User;

@Service
public class UsersDao {

private static final String COLLECTION_NAME = "users";
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public List<User> getUserMainDetails(List<ObjectId> userIds,List<String> userdetails){
		Criteria criteria = Criteria.where("_id").in(userIds).and("needToFollowUp").nin(userdetails).and("type").ne("partner");
		Query query = Query.query(criteria);
		query.fields().include("fullname");
		query.fields().include("email");
		query.fields().include("isRemindersEnabled");
		query.fields().include("reminderEmail");
		query.fields().include("needToFollowUp");
		List<User> subscriptionDetails = mongoTemplate.find(query, User.class, COLLECTION_NAME);
		return subscriptionDetails;
	}
}
