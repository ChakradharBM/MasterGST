package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mastergst.usermanagement.runtime.domain.GSTR1Delete;

public interface GSTR1DeleteRepository extends MongoRepository<GSTR1Delete, String> {
	Page<GSTR1Delete> findByDeleteddateLessThan(Date date,Pageable pageable);
}
