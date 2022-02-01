package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR9;

public interface GSTR9Repository extends MongoRepository<GSTR9, String>{

	GSTR9 findByClientidAndFp(final String clientid, String fp);
}
