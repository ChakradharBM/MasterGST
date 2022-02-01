package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR9Dummy;

public interface GSTR9DummyRepository extends MongoRepository<GSTR9Dummy, String>{

	GSTR9Dummy findByClientidAndFp(final String clientid, String fp);
}
