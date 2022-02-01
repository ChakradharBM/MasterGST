package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR9C;

public interface GSTR9CRepository extends MongoRepository<GSTR9C, String>{
	GSTR9C findByClientidAndReturnPeriod(final String clientid, String fp);
}
