package com.mastergst.usermanagement.runtime.service;

import com.mastergst.login.runtime.domain.User;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR4Annual;
import com.mastergst.usermanagement.runtime.response.gstr4.GSTR4AnnualCMPResponseData;

public interface Gstr4AnnualService {

	public GSTR4Annual getGstr4AnnualInvoices(String clientid, String fp);
	public void saveGstr4AnnualCmpResponseInvoices(GSTR4AnnualCMPResponseData data, User user, Client clientid, String financialyear);
	public void saveInvoices(GSTR4Annual gstr4Annual, User user, Client clientid, String fp);
	
	public void deleteGstr4AnnualData(String string, String fp);

	public void autoCalculateData(GSTR4Annual gstr4Annual, Client client, int year);
	public void syncGstr4AnnualCmp(User user, Client client, String fp);
	GSTR4Annual saveGSTR4AnnualInvoice(GSTR4Annual invoice,Client client,int month,int year);
	public void performGSTR4Reconcile(Client client, String invType, String string, String clientid, String id,int month, int year);
}
