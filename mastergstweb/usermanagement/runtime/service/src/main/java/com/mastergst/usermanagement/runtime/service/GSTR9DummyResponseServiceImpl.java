package com.mastergst.usermanagement.runtime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mastergst.usermanagement.runtime.domain.gstr9response.GSTR9DummyResponse;
import com.mastergst.usermanagement.runtime.repository.GSTR9DummyResponseRepository;

@Service
public class GSTR9DummyResponseServiceImpl implements GSTR9DummyResponseService{

	
	@Autowired
	private GSTR9DummyResponseRepository gstr9DummyResponseRepository;
	
	@Override
	public GSTR9DummyResponse findByClientidAndFp(String clientid, String fp) {
		
		return gstr9DummyResponseRepository.findByClientidAndFp(clientid, fp);
	}
}
