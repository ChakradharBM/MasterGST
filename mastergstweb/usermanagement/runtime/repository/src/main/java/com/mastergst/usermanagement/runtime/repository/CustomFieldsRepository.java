package com.mastergst.usermanagement.runtime.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.CustomFields;

public interface CustomFieldsRepository extends MongoRepository<CustomFields, String> {

	CustomFields findByClientid(String clientid);
	
}
