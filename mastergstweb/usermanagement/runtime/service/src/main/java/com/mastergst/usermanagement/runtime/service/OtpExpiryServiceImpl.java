package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.util.NullUtil.isEmpty;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.HeaderKeys;
import com.mastergst.login.runtime.repository.HeaderKeysRepository;

@Service
@Transactional(readOnly = true)
public class OtpExpiryServiceImpl implements OtpExpiryService{
	private static final Logger logger = LogManager.getLogger(OtpExpiryServiceImpl.class.getName());
	@Autowired
	private HeaderKeysRepository headerKeysRepository;
	
	public enum  OtpCheck{

		OTP_NOT_VERIFIED, OTP_EXPIRED, OTP_VERIFIED
	}
	
	@Override
	public String otpexpiry(String clientGstName) {
		String expiry = "";
		HeaderKeys headerKeys=headerKeysRepository.findByGstusername(clientGstName);			
		if(isEmpty(headerKeys)) {
			expiry =  OtpCheck.OTP_NOT_VERIFIED.name();
		}else {
			
			if(NullUtil.isNotEmpty(headerKeys) && NullUtil.isNotEmpty(headerKeys.getUpdatedDate())) {
				long duration = Calendar.getInstance().getTime().getTime() - headerKeys.getUpdatedDate().getTime();
				long diff = TimeUnit.MILLISECONDS.toMinutes(duration);
				if(NullUtil.isNotEmpty(headerKeys.getExpiry()) && headerKeys.getExpiry() == 0) {
					expiry = OtpCheck.OTP_EXPIRED.name();
				}else {
					if(diff >= 360) { // < ((headerKey.getExpiry()/2)-30)
						expiry = OtpCheck.OTP_EXPIRED.name();
					}else {
						expiry= OtpCheck.OTP_VERIFIED.name();
					}
				}
			}	
		}
		return expiry;
	}
	@Override
	public String ewaybillAuthexpiry(String clientGstName) {
		String expiry = "";
		HeaderKeys headerKeys=headerKeysRepository.findByUsername(clientGstName);	
		if(isEmpty(headerKeys)) {
			expiry =  "InActive";
		}else {
			if(NullUtil.isNotEmpty(headerKeys) && NullUtil.isNotEmpty(headerKeys.getUpdatedDate())) {
				long duration = Calendar.getInstance().getTime().getTime() - headerKeys.getUpdatedDate().getTime();
				long diff = TimeUnit.MILLISECONDS.toMinutes(duration);
				if(NullUtil.isEmpty(headerKeys.getExpiry())) {
					expiry = "Expired";
				}else {
					if(diff >= 360) { 
						expiry = "Expired";
					}else {
						expiry= "Active";
					}
				}
			}	
		}
		return expiry;
	}
}
