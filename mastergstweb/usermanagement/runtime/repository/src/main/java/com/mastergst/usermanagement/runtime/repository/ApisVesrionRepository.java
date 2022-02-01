package com.mastergst.usermanagement.runtime.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.mastergst.usermanagement.runtime.domain.ApisVesrion;

public interface ApisVesrionRepository extends MongoRepository<ApisVesrion, String>{

}