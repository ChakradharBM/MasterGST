package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.mastergst.usermanagement.runtime.dao.PaymentsDao;
import com.mastergst.usermanagement.runtime.domain.PaymentFilter;
import com.mastergst.usermanagement.runtime.domain.Payments;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.domain.TotalPaymentsAmount;
import com.mastergst.usermanagement.runtime.repository.PaymentDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.RecordPaymentsRepository;

@Service
public class PaymentsServiceImpl implements PaymentsService {
	private static final Logger logger = LogManager.getLogger(PaymentsServiceImpl.class.getName());
	private static final String CLASSNAME = "PaymentsServiceImpl::";
	
	@Autowired
	private PaymentsDao paymentsDao;
	
	@Autowired
	RecordPaymentsRepository recordPaymentsRepository;
	
	@Override
	public Page<Payments> getPaymentsByClientid(String clientid, String returntype, int start, int length, String searchVal, PaymentFilter paymentFilter){
		final String method = "getPaymentsByClientid::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid::" + clientid);
		List<String> returntypes = null;
		if("GSTR2".equals(returntype)) {
			returntypes = Arrays.asList("GSTR2", "PurchaseRegister", "Purchase Register");
		}else {
			returntypes = Arrays.asList("GSTR1", "SalesRegister");
		}
		
		Page<Payments> payments = paymentsDao.getPaymentInvoices(clientid, returntypes, start, length, searchVal, paymentFilter);
		
		return payments;
	}
	
	@Override
	public Payments getPaymentsByDocid(String docid) {
		
		
		
		return recordPaymentsRepository.findById(docid);
	}
	
	@Override
	public TotalPaymentsAmount getPaymentsTotalReceivedAmounts(String clientid, String returntype, PaymentFilter filter) {
		final String method = "getPaymentsTotalReceivedAmounts::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid::" + clientid);
		
		TotalPaymentsAmount amts = paymentsDao.getPaymentsConsolidatedTotalAmountSummery(clientid, returntype, filter);
		
		return amts;
	}
	
	@Override
	public String getTotalInvoicesAmounts(String clientid, String returntype, PaymentFilter filter) {
		final String method = "getTotalInvoicesAmounts::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid::" + clientid);
		
		TotalInvoiceAmount amts = paymentsDao.getConsolidatedTotalAmountSummery(clientid, returntype, filter);
		
		if(amts !=null) {
			return amts.getTotalAmount().toPlainString();			
		}
		return new BigDecimal(0d).toPlainString();
	}
	
	@Override
	public List<String> getPaymnetsBilledToName(String clientid, String returntype) {
		
		return paymentsDao.getBilledToNameByClientidAndReturnType(clientid, returntype);
	}
	
	@Override
	public List<String> getPaymnetsBilledToGstin(String clientid, String returntype) {
		
		return paymentsDao.getBilledToGstinByClientidAndReturnType(clientid, returntype);
	}
}
