package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import com.mastergst.usermanagement.runtime.domain.CompanySuppliers;
@Component
public class SuppliersDao {

	private static final String COLLECTION_NAME = "company_suppliers";
	
	@Autowired
	private MongoTemplate mongoTemplate;

	public CompanySuppliers getSuppiersById(String id){
		Criteria criteria = Criteria.where("_id").is(new ObjectId(id));
		
		Query query = Query.query(criteria);
		
		return mongoTemplate.findOne(query, CompanySuppliers.class, COLLECTION_NAME);
	}
	
	public Page<CompanySuppliers> getSuppiers(List<String> clientids, int start, int length, String searchVal){
		clientids.stream().distinct();
		Criteria criteria = Criteria.where("clientid").in(clientids);
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
		long total = mongoTemplate.count(query, CompanySuppliers.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<CompanySuppliers>(Collections.<CompanySuppliers> emptyList());
		}
		return new PageImpl<CompanySuppliers>(mongoTemplate.find(query, CompanySuppliers.class, COLLECTION_NAME), pageable, total);
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
		criterias.add(Criteria.where("name").regex(searchVal, "i"));
		criterias.add(Criteria.where("fullname").regex(searchVal, "i"));
 		criterias.add(Criteria.where("email").regex(searchVal, "i"));
 		criterias.add(Criteria.where("mobilenumber").regex(searchVal, "i"));
 		criterias.add(Criteria.where("gstnnumber").regex(searchVal, "i"));
 		criterias.add(Criteria.where("state").regex(searchVal, "i"));
 				
 		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}
	
	private void addAllClientQueryFirlds(Query query){
		query.fields().include("fullname");
		query.fields().include("name");
		query.fields().include("email");
		query.fields().include("mobilenumber");
		query.fields().include("gstnnumber");
		query.fields().include("state");
		query.fields().include("contactperson");
		query.fields().include("docId");
		query.fields().include("userid");
		query.fields().include("clientid");
	}
	
}
