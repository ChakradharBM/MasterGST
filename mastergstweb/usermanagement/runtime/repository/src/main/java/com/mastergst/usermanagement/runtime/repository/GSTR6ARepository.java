package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6A;

public interface GSTR6ARepository extends MongoRepository<GSTR6A, String> {

	List<GSTR6A> findByClientidAndFpAndInvtype(String clientid, String fp, String invType);

	List<GSTR6A> findByClientidAndFpAndIsAmendmentAndInvtype(String clientid, String retPeriod, boolean b,String invType);

}
