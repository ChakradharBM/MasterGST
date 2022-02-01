package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import com.mastergst.usermanagement.runtime.domain.AuditorAdrressDetails;

public interface AuditorService {
	
	AuditorAdrressDetails saveAuditor(AuditorAdrressDetails auditorAddress);
	AuditorAdrressDetails findAuditor(String id);
	List<AuditorAdrressDetails> getAuditors(String id);
}
