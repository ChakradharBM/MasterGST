package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.ProfileLedger;

public interface LedgerRepository extends MongoRepository<ProfileLedger, String> {
	List<ProfileLedger> findByClientid(final String clientid);

	List<ProfileLedger> findByClientidAndGrpsubgrpNameIn(final String clientid, final List<String> grpsubgrpName);

	List<ProfileLedger> findByClientidAndGrpsubgrpName(final String clientid, final String grpsubgrpName);

	ProfileLedger findByClientidAndLedgerNameIgnoreCase(final String clientid, final String ledgerName);

	ProfileLedger findByClientidAndBankId(String clientid, String string);

	@Transactional
	@Modifying
	void deleteByClientidAndBankId(final String clientid, final String bankid);

	List<ProfileLedger> findByClientidAndLedgerNameIn(String clientid, List<String> ledgerNames);
}
