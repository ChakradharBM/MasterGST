package com.mastergst.configuration.service;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CurrencycodesRepository extends MongoRepository<Currencycodes, String>{
	Currencycodes findByCode(String code);
	
}
