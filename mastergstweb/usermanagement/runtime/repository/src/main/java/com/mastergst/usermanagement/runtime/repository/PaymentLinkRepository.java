package com.mastergst.usermanagement.runtime.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.PaymentLink;


public interface PaymentLinkRepository extends MongoRepository<PaymentLink, String>{
	PaymentLink findByUserid(String userid);

}
