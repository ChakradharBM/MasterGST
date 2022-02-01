package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;


import com.mastergst.usermanagement.runtime.domain.Messages;
import com.mastergst.usermanagement.runtime.domain.Payments;

public interface MessagesRepository extends MongoRepository<Messages, String> {
	
	public List<Messages> findById(String id);
	List<Messages> findByClientid(final String clientid,final String invoiceNumber);
	
}
