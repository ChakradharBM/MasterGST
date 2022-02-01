package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.Expenses;

public interface ExpensesRepository extends MongoRepository<Expenses, String>{

}
