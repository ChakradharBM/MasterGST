package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.HSNDetails;

public interface HSNSummaryRepository extends MongoRepository<HSNDetails, String>{
	
	HSNDetails findByClientidAndReturnPeriodAndReturnType(final String clientid,final String returnPeriod,final String returnType);
	
	

}
