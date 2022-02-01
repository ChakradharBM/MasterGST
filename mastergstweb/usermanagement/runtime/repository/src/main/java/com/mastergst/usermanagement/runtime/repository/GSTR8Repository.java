package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR8;

public interface GSTR8Repository extends MongoRepository<GSTR8, String>{

	public GSTR8 findById(String docId);

	public GSTR8 findByClientidAndFp(String clientid, String fp);

}
