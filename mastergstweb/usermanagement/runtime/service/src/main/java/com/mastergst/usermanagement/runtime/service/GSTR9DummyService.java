package com.mastergst.usermanagement.runtime.service;

import com.mastergst.usermanagement.runtime.domain.GSTR9Dummy;

public interface GSTR9DummyService {
	
	GSTR9Dummy findByClientidAndFp(final String clientid, String fp);

}
