package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR4A;

public interface GSTR4ARepository extends MongoRepository<GSTR4A,String> {

	List<GSTR4A> findByClientidAndFpAndIsAmendmentAndInvtype(String clientid, String retPeriod, boolean b,String invType);

	List<GSTR4A> findByClientidAndFpAndInvtype(String clientid, String fp, String invType);

}
