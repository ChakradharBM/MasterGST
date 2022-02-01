package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.gstr9response.GSTR9DummyResponse;

public interface GSTR9DummyResponseRepository extends MongoRepository<GSTR9DummyResponse, String>{

	GSTR9DummyResponse findByClientidAndFp(final String clientid, String fp);
}
