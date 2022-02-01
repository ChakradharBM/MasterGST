package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.SupplierStatus;

public interface SupplierStatusRepository extends MongoRepository<SupplierStatus, Long>{

	List<SupplierStatus> findBySupplieridAndReturntypeAndReturnperiodIn(String string, String gstr1, List<String> rtArray);

	List<SupplierStatus> findBySupplieridAndReturnperiodAndReturntype(String supplierid, String returnperiod, String returntype);

	List<SupplierStatus> findByClientidAndReturntypeAndReturnperiodIn(String clientid, String rettype, List<String> retperiod);
	List<SupplierStatus> findBySupplieridAndReturntypeInAndReturnperiodIn(String supid, List<String> rettype, List<String> retperiod);
}
