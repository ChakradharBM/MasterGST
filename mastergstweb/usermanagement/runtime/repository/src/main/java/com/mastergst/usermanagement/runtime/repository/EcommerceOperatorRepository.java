package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.EcommerceOpeartor;

public interface EcommerceOperatorRepository extends MongoRepository<EcommerceOpeartor, String> {

	List<EcommerceOpeartor> findByClientid(final String clientid);

}
