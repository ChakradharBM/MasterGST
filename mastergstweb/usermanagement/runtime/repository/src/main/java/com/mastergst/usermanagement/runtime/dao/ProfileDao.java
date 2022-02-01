package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CompanySuppliers;

@Service
public class ProfileDao {
	@Autowired
	protected MongoTemplate mongoTemplate;

	public Page<? extends CompanyCustomers> findByClientid(String clientid, int start, int length, String searchVal) {
		Criteria criteria = Criteria.where("clientid").is(clientid);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, CompanyCustomers.class, "companycustomers");
		if (total == 0) {
			return new PageImpl<CompanyCustomers>(Collections.<CompanyCustomers> emptyList());
		}
		return new PageImpl<CompanyCustomers>(mongoTemplate.find(query, CompanyCustomers.class, "companycustomers"), pageable, total);
	}
	
	public Page<? extends CompanySuppliers> findByid(String clientid, int start, int length, String searchVal) {
		Criteria criteria = Criteria.where("clientid").is(clientid);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, CompanyCustomers.class, "company_suppliers");
		if (total == 0) {
			return new PageImpl<CompanySuppliers>(Collections.<CompanySuppliers> emptyList());
		}
		return new PageImpl<CompanySuppliers>(mongoTemplate.find(query, CompanySuppliers.class, "company_suppliers"), pageable, total);
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
 		criterias.add(Criteria.where("type").regex(searchVal, "i"));
 		criterias.add(Criteria.where("mobilenumber").regex(searchVal, "i"));
 		criterias.add(Criteria.where("email").regex(searchVal, "i"));
 		criterias.add(Criteria.where("state").regex(searchVal, "i"));
		criterias.add(Criteria.where("customerId").regex(searchVal, "i"));
		criterias.add(Criteria.where("creditPeriod").regex(searchVal, "i"));
 		criterias.add(Criteria.where("creditAmount").regex(searchVal, "i"));
 		criterias.add(Criteria.where("supplierCustomerId").regex(searchVal, "i"));
		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}
	private void addAllInvoicesQueryFirlds(Query query){
		query.fields().include("fullname");
		query.fields().include("type");
		query.fields().include("name");
		query.fields().include("email");
		query.fields().include("mobilenumber");
		query.fields().include("state");
		query.fields().include("customerId");
		query.fields().include("creditPeriod");
		query.fields().include("creditAmount");
		query.fields().include("createdDate");
		query.fields().include("createdBy");
		query.fields().include("supplierCustomerId");
	}

	public List<CompanyCustomers> findByClientid(List<String> clientIds) {
		Criteria criteria = Criteria.where("clientid").in(clientIds);
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		long total = mongoTemplate.count(query, CompanyCustomers.class, "companycustomers");
		if (total == 0) {
			//return new PageImpl<CompanyCustomers>(Collections.<CompanyCustomers> emptyList());
			return Collections.<CompanyCustomers> emptyList();
		}
		//return new PageImpl<CompanyCustomers>(mongoTemplate.find(query, CompanyCustomers.class, "companycustomers"), pageable, total);
		return mongoTemplate.find(query, CompanyCustomers.class, "companycustomers");
	}
	
}
