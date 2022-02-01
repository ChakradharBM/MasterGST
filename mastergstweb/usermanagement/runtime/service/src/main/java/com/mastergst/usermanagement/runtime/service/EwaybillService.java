package com.mastergst.usermanagement.runtime.service;


import java.text.ParseException;

import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.EWAYBILL;
import com.mastergst.usermanagement.runtime.domain.ewaybill.ExtendValidity;
import com.mastergst.usermanagement.runtime.domain.ewaybill.ExtendValidityDO;


public interface EwaybillService {
	ExtendValidity extendValidityData(Client client, EWAYBILL ewaybill, ExtendValidityDO ebillData) throws ParseException;
}
