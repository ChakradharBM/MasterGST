package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mastergst.usermanagement.runtime.domain.AuditorAdrressDetails;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.repository.AuditorRepository;
import com.mastergst.usermanagement.runtime.repository.ClientRepository;

@Service
@Transactional(readOnly = true)
public class AuditorServiceImpl implements AuditorService{
	private static final Logger logger = LogManager.getLogger(ClientServiceImpl.class.getName());
	private static final String CLASSNAME = "AuditorServiceImpl::";
	
	@Autowired
	AuditorRepository auditorRepository;
	
	@Override
	@Transactional
	public AuditorAdrressDetails saveAuditor(AuditorAdrressDetails auditorAdrress) {
		final String method = "saveAuditor::";
		logger.debug(CLASSNAME + method + BEGIN);
		return auditorRepository.save(auditorAdrress);
	}
	
	@Override
	@Transactional
	public AuditorAdrressDetails findAuditor(final String id) {
		final String method = "saveAuditor::";
		logger.debug(CLASSNAME + method + BEGIN);
		return auditorRepository.findOne(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<AuditorAdrressDetails> getAuditors(final String userid) {
		final String method = "getAuditors::";
		logger.debug(CLASSNAME + method + BEGIN);
		return auditorRepository.findByUserid(userid);
		
	}

}
