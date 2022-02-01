package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.AspUserDetails;

public interface AspUserDetailsRepository extends MongoRepository<AspUserDetails,String>{

	public AspUserDetails findByUserid(String userid);
	
	public List<AspUserDetails> findByUseridIn(List<String> userid);
}
