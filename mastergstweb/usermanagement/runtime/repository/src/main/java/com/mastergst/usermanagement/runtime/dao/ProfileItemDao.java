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

import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CompanyItems;

@Service
public class ProfileItemDao {
	@Autowired
	protected MongoTemplate mongoTemplate;

	public Page<? extends CompanyItems> findByClientid(String clientid, String type, int start, int length, String searchVal) {
		Criteria criteria = Criteria.where("clientid").is(clientid);
		if(NullUtil.isNotEmpty(type) && type.equalsIgnoreCase("reports")) {
			criteria.and("itemType").is("Product");
		}
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
		long total = mongoTemplate.count(query, CompanyCustomers.class, "companyitems");
		if (total == 0) {
			return new PageImpl<CompanyItems>(Collections.<CompanyItems> emptyList());
		}
		return new PageImpl<CompanyItems>(mongoTemplate.find(query, CompanyItems.class, "companyitems"), pageable, total);
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
		criterias.add(Criteria.where("itemno").regex(searchVal, "i"));
 		criterias.add(Criteria.where("description").regex(searchVal, "i"));
 		criterias.add(Criteria.where("unit").regex(searchVal, "i"));
 		criterias.add(Criteria.where("sellingpriceb2b").regex(searchVal, "i"));
 		criterias.add(Criteria.where("discount").regex(searchVal, "i"));
		criterias.add(Criteria.where("code").regex(searchVal, "i"));
		criterias.add(Criteria.where("taxrate").regex(searchVal, "i"));
 		criterias.add(Criteria.where("exmepted").regex(searchVal, "i"));
 		criterias.add(Criteria.where("salePriceTxt").regex(searchVal, "i"));
		criterias.add(Criteria.where("purchasePriceTxt").regex(searchVal, "i"));
 		criterias.add(Criteria.where("currentStock").regex(searchVal, "i"));
 		
		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}
	private void addAllInvoicesQueryFirlds(Query query){
		query.fields().include("fullname");
		query.fields().include("itemno");
		query.fields().include("description");
		query.fields().include("unit");
		query.fields().include("salePrice");
		query.fields().include("purchasePrice");
		query.fields().include("salePriceTxt");
		query.fields().include("purchasePriceTxt");
		query.fields().include("currentStock");
		query.fields().include("discount");
		query.fields().include("code");
		query.fields().include("taxrate");
		query.fields().include("exmepted");
		
	}
	
}
