package com.mastergst.configuration.service;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CountryRepository extends MongoRepository<CountryConfig, String>{

}
