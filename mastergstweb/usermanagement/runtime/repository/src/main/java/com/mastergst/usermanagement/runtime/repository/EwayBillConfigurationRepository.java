package com.mastergst.usermanagement.runtime.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.EwayBillConfigurations;

public interface EwayBillConfigurationRepository extends MongoRepository<EwayBillConfigurations, String> {

	EwayBillConfigurations findByClientid(final String clientid);
	@Transactional
	@Modifying
	void deleteByClientid(final String clientid);
}
