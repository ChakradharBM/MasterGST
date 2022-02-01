package com.mastergst.usermanagement.runtime.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.domain.AspUserDetails;
import com.mastergst.usermanagement.runtime.repository.AspUserDetailsRepository;

@Service
@Transactional(readOnly = true)
public class AspUserDetailsServiceImpl implements AspUserDetailsService{

	@Autowired
	private AspUserDetailsRepository aspUserDetailsRepository;
	
	
	@Override
	@Transactional
	public AspUserDetails getUserid(String userid) {
		
		return aspUserDetailsRepository.findByUserid(userid);
	}


	@Override
	@Transactional
	public AspUserDetails saveAspUserDetails(AspUserDetails aspUserDetails) {
		
		return aspUserDetailsRepository.save(aspUserDetails);
	}
	
	@Override
	public AspUserDetails getAspUserDetails(String userid) {
				
		return aspUserDetailsRepository.findByUserid(userid);
	}
}
