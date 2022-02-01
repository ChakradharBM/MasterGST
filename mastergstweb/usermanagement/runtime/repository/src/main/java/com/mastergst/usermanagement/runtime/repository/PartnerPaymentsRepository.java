package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.PartnerPayments;

public interface PartnerPaymentsRepository extends MongoRepository<PartnerPayments, String>{

	public PartnerPayments findByUserid(String userid);
	public PartnerPayments findByUseridAndMthCdAndYrCd(String userid, String mthCd, String yrCd);
	public PartnerPayments findByIdAndMthCdAndYrCd(String docid, String mthCd, String yearCode);
}
