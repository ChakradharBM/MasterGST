package com.mastergst.usermanagement.runtime.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.TransPublicData;

public interface TransinRepository extends MongoRepository<TransPublicData, String> {

	TransPublicData findByTransin(String transin);
	
	@Transactional
	@Modifying
	void deleteByTransinAndUseridIsNull(String transin);

}
