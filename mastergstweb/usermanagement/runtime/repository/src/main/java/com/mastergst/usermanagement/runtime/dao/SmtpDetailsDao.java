package com.mastergst.usermanagement.runtime.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mastergst.usermanagement.runtime.domain.SmtpDetails;

@Service
public class SmtpDetailsDao {
	
	private static final String COLLECTION_NAME = "smtpdetails";
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public List<SmtpDetails> getClientsConfiguredSmtpDetails(){
		Query query = new Query();
		query.fields().include("clientId");
		query.fields().include("schedlueExpressionVal");
		List<SmtpDetails> smtpDetails = mongoTemplate.find(query, SmtpDetails.class, COLLECTION_NAME);
		return smtpDetails;
	}

}
