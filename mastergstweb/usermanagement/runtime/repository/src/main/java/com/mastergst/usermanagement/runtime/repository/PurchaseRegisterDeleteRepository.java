package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.PurchaseRegisterDelete;

public interface PurchaseRegisterDeleteRepository extends MongoRepository<PurchaseRegisterDelete, String> {
	
	Page<PurchaseRegisterDelete> findByDeleteddateLessThan(Date date,Pageable pageable);

}
