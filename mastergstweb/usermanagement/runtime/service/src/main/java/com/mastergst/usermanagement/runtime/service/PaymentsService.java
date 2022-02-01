package com.mastergst.usermanagement.runtime.service;


import java.util.List;

import org.springframework.data.domain.Page;

import com.mastergst.usermanagement.runtime.domain.PaymentFilter;
import com.mastergst.usermanagement.runtime.domain.Payments;
import com.mastergst.usermanagement.runtime.domain.TotalPaymentsAmount;

public interface PaymentsService {
	
	public Page<Payments> getPaymentsByClientid(String clientid, String type, int start, int length, String searchVal, PaymentFilter paymentFilter);

	public String getTotalInvoicesAmounts(String clientid, String returntype, PaymentFilter filter);

	public TotalPaymentsAmount getPaymentsTotalReceivedAmounts(String clientid, String returntype, PaymentFilter filter);

	public List<String> getPaymnetsBilledToName(String clientid, String returntype);

	public List<String> getPaymnetsBilledToGstin(String clientid, String returntype);

	public Payments getPaymentsByDocid(String docid);

}
