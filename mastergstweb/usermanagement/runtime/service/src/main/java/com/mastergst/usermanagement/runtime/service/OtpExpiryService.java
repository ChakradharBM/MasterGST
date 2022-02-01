package com.mastergst.usermanagement.runtime.service;

public interface OtpExpiryService {

	String otpexpiry(String clientGstName);

	String ewaybillAuthexpiry(String clientGstName);
}
