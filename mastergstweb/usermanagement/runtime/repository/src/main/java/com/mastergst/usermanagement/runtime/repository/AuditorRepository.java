package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.AuditorAdrressDetails;


public interface AuditorRepository extends MongoRepository<AuditorAdrressDetails, String>{
	
	List<AuditorAdrressDetails> findByUserid(final String userid);
}
