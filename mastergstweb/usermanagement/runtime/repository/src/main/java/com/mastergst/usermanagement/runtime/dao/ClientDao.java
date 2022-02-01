package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;

@Component
public class ClientDao {
	
	private static final String COLLECTION_NAME = "client";
	
	@Autowired
	private MongoTemplate mongoTemplate;

	public Page<Client> getClients(List<String> clientids, int start, int length, String searchVal){
		
		Set<ObjectId> objectIds = new HashSet<>();
		if(NullUtil.isNotEmpty(clientids)){
			for(String id: clientids) {
				objectIds.add(new ObjectId(id));
			}
		}
		
		Criteria criteria = Criteria.where("_id").in(objectIds);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllClientQueryFirlds(query);
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, Client.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<Client>(Collections.<Client> emptyList());
		}
		return new PageImpl<Client>(mongoTemplate.find(query, Client.class, COLLECTION_NAME), pageable, total);
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
 		
		criterias.add(Criteria.where("businessname").regex(searchVal, "i"));
 		criterias.add(Criteria.where("email").regex(searchVal, "i"));
 		criterias.add(Criteria.where("mobilenumber").regex(searchVal, "i"));
 		criterias.add(Criteria.where("gstnnumber").regex(searchVal, "i"));
 		criterias.add(Criteria.where("statename").regex(searchVal, "i"));
 				
 		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}
	
	private void addAllClientQueryFirlds(Query query){
		query.fields().include("businessname");
		query.fields().include("email");
		query.fields().include("mobilenumber");
		query.fields().include("gstnnumber");
		query.fields().include("statename");
		query.fields().include("logoid");
		query.fields().include("docId");
		query.fields().include("userid");
	}
}
