package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6Details;

public interface GSTR6DetailsRepository extends MongoRepository<GSTR6Details, String> {

	void deleteByClientidAndFp(String string, String retPeriod);

	GSTR6Details findByClientidAndFp(String clientid, String retPeriod);

}
