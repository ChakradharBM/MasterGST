package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.MultiGSTNData;

public interface MultiGSTnRepository extends MongoRepository<MultiGSTNData, String> {
	List<MultiGSTNData> findByUseridAndGstin(final String userid,final String gstin);
	MultiGSTNData findByGstnid(final String gstinid);
	MultiGSTNData findByUseridAndGstinAndGstnid(final String userid,final String gstin,final String gstinid);
	List<MultiGSTNData> findByUserid(final String userid);
	@Transactional
	@Modifying
	void deleteByUseridAndGstin(final String userid,final String gstin);

}
