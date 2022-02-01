package com.mastergst.usermanagement.runtime.service;

import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR8;
import com.mastergst.usermanagement.runtime.response.GSTR8GetResponse;

public interface INewClientService {

	public GSTR8 saveEcomInvoice(GSTR8 gstr8, String returnType);
	public GSTR8 saveEcomAInvoice(GSTR8 gstr8, String returnType);
	public GSTR8 getGstr8(String clientid, String retPeriod);
	public GSTR8GetResponse syncEcomInvoices(Client client, String userid, String returntype, int month, int year);

}
