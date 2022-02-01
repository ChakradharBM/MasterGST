package com.mastergst.usermanagement.runtime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mastergst.usermanagement.runtime.domain.GSTR9Dummy;
import com.mastergst.usermanagement.runtime.repository.GSTR9DummyRepository;

@Service
public class GSTR9DummyServiceImpl implements GSTR9DummyService{

	
	@Autowired
	private GSTR9DummyRepository gstr9DummyRepository;
	
	@Override
	public GSTR9Dummy findByClientidAndFp(String clientid, String fp) {
		
		return gstr9DummyRepository.findByClientidAndFp(clientid, fp);
	}
}
