package com.mastergst.usermanagement.runtime.service;

import com.mastergst.usermanagement.runtime.domain.AspUserDetails;

public interface AspUserDetailsService {

	public AspUserDetails getUserid(String userid);
	public AspUserDetails saveAspUserDetails(AspUserDetails aspUserDetails);
	
	public AspUserDetails getAspUserDetails(String userid);
}
