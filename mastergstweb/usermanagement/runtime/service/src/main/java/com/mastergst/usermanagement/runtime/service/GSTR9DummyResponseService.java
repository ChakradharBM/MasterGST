package com.mastergst.usermanagement.runtime.service;

import com.mastergst.usermanagement.runtime.domain.gstr9response.GSTR9DummyResponse;

public interface GSTR9DummyResponseService {
	
	GSTR9DummyResponse findByClientidAndFp(final String clientid, String fp);

}
