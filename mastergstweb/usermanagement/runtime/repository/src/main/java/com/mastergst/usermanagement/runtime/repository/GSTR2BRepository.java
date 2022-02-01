package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2B;



public interface GSTR2BRepository extends MongoRepository<GSTR2B, String>{

	GSTR2B findByClientidAndFp(String clientid, String fp);
	
	Page<GSTR2B> findByClientidAndFpIn(final String clientid,List<String> fps, Pageable pageable);
	List<GSTR2B> findByFpAndClientid(String fp, String clientid);

}
