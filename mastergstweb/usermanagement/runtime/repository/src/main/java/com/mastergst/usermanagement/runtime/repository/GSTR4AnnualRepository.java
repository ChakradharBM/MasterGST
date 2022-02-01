package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR4Annual;

public interface GSTR4AnnualRepository extends MongoRepository<GSTR4Annual, String> {

	void deleteByClientidAndFp(String clientid, String fp);
	
	public GSTR4Annual findByClientidAndFp(String clientid, String fp);
}
