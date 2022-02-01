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
import com.mastergst.usermanagement.runtime.domain.CompanyItems;
import com.mastergst.usermanagement.runtime.domain.StorageCredentials;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;

@Service
public class StorageCredentialsDao {
	
	private static final String COLLECTION_NAME = "storage_credentials";
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public void saveStorageCredentials(StorageCredentials storageCredentials){
		mongoTemplate.save(storageCredentials);
	}
	
	public StorageCredentials getStorageCredentials(String userId){
		Query query = Query.query(Criteria.where("clientId").is(userId));
		List<StorageCredentials> storageCredentials = mongoTemplate.find(query, StorageCredentials.class);
		if(storageCredentials.size() > 0){
			return storageCredentials.get(0);
		}
		return null;
	}
	public List<StorageCredentials> getStorageCredentialsBasedOnId(String userId){
		Query query = Query.query(Criteria.where("userid").is(userId));
		List<StorageCredentials> storageCredentials = mongoTemplate.find(query, StorageCredentials.class);
		return storageCredentials;
	}
	
	public StorageCredentials getStorageCredentialsBasedOnClientId(String clientid){
		Query query = Query.query(Criteria.where("clientId").is(clientid));
		StorageCredentials storageCredentials = mongoTemplate.findOne(query, StorageCredentials.class);
		return storageCredentials;
	}

	 public Page<? extends StorageCredentials> findById(String userid, int start, int length, String searchVal) {
		Criteria criteria = Criteria.where("userid").is(userid);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, StorageCredentials.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<StorageCredentials>(Collections.<StorageCredentials> emptyList());
		}
		return new PageImpl<StorageCredentials>(mongoTemplate.find(query, StorageCredentials.class, COLLECTION_NAME), pageable, total);
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
 		criterias.add(Criteria.where("accessKey").regex(searchVal, "i"));
 		criterias.add(Criteria.where("accessSecret").regex(searchVal, "i"));
 		criterias.add(Criteria.where("bucketName").regex(searchVal, "i"));
 		criterias.add(Criteria.where("regionName").regex(searchVal, "i"));
 		criterias.add(Criteria.where("clientName").regex(searchVal, "i"));
 		criterias.add(Criteria.where("groupName").regex(searchVal, "i"));
 		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}

private void addAllInvoicesQueryFirlds(Query query){
		query.fields().include("accessKey");
		query.fields().include("accessSecret");
		query.fields().include("bucketName");
		query.fields().include("regionName");
		query.fields().include("clientName");
		query.fields().include("groupName");
		query.fields().include("clientId");
	}

/*public StorageCredentials findById(String userid, int start, int length, String searchVal) {
	Criteria criteria = Criteria.where("userid").is(userid);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	Query query = Query.query(criteria);
	addAllInvoicesQueryFirlds(query);
	return mongoTemplate.findOne(query,  StorageCredentials.class,COLLECTION_NAME);
} */
}
