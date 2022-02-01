package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.PaymentFilter;
import com.mastergst.usermanagement.runtime.domain.Payments;
import com.mastergst.usermanagement.runtime.domain.TotalPaymentsAmount;
import com.mastergst.usermanagement.runtime.service.PaymentsService;

@Controller
public class PaymentController {
	private static final Logger logger = LogManager.getLogger(PaymentController.class.getName());
	private static final String CLASSNAME = "PaymentController::";
	
	@Autowired
	private PaymentsService paymentsService;
	
	
	@RequestMapping("/recordPayments/{userid}/{clientid}")
	public @ResponseBody String getPaymentInvoices(@PathVariable String userid, @PathVariable String clientid, @RequestParam(name = "type") String returntype, HttpServletRequest request) throws JsonProcessingException{
		final String method = "getPaymentInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "userid ::" + userid);
		logger.debug(CLASSNAME + method + "clientid ::" + clientid);
		String st = request.getParameter("start");
		
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		PaymentFilter paymentFilter = new PaymentFilter();
		paymentFilter.setFinancialYear(request.getParameter("financialYear"));
		paymentFilter.setMonth(request.getParameter("month"));
		paymentFilter.setVendor(request.getParameter("vendor"));
		paymentFilter.setGstno(request.getParameter("gstno"));
		paymentFilter.setPaymentMode(request.getParameter("paymentMode"));
		
		Page<Payments> payments = paymentsService.getPaymentsByClientid(clientid, returntype, start, length, searchVal, paymentFilter);
		
		payments.getContent().forEach(payment -> payment.setDocId(payment.getId().toString()));
		TotalPaymentsAmount paymentsAmts = paymentAmountsCalculations(clientid, returntype, paymentFilter);
		
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		invoicesMap.put("invoices", payments);
		invoicesMap.put("paymentsAmts", paymentsAmts);
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer = mapper.writer();
		
		return writer.writeValueAsString(invoicesMap);
	}
	
	@RequestMapping("/getPaymentsInfo/{docid}")
	public @ResponseBody Payments getPaymentInvoice(@PathVariable String docid, HttpServletRequest request) throws JsonProcessingException{
		final String method = "getPaymentInvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "docid ::" + docid);
		
		Payments payments = paymentsService.getPaymentsByDocid(docid);
		if(NullUtil.isNotEmpty(payments)) {
			payments.setDocId(payments.getId().toString());
		}
		return payments;
	}
	
	
	public TotalPaymentsAmount paymentAmountsCalculations(String clientid, String returntype, PaymentFilter filter) {
		Double totalPendingAmount = 0d;
		TotalPaymentsAmount paymentsAmts = paymentsService.getPaymentsTotalReceivedAmounts(clientid, returntype, filter);
		if(paymentsAmts == null) {
			paymentsAmts = new TotalPaymentsAmount();
			paymentsAmts.setTotalTransactions(0);
			paymentsAmts.setTotalPaidAmount(new BigDecimal(0d));
		}
		String totalInvoiceAmounts = paymentsService.getTotalInvoicesAmounts(clientid, returntype, filter);
		paymentsAmts.setTotalInvoiceAmount(new BigDecimal(totalInvoiceAmounts));
		
		totalPendingAmount = paymentsAmts.getTotalInvoiceAmount().doubleValue() - paymentsAmts.getTotalPaidAmount().doubleValue();
		
		paymentsAmts.setTotalPendingAmount(new BigDecimal(totalPendingAmount));
		
		return paymentsAmts;
	}
	
	@RequestMapping("/getPaymentInvsSupport/{userid}/{clientid}")
	public @ResponseBody Map<String, Object> getPaymentInvsSupport(@PathVariable String userid, @PathVariable String clientid, @RequestParam(name = "type") String returntype, HttpServletRequest request) throws JsonProcessingException{
		final String method = "getPaymentInvsSupport::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "userid ::" + userid);
		logger.debug(CLASSNAME + method + "clientid ::" + clientid);
		
		Map<String,Object> supportMap = new HashMap<>();
		
		supportMap.put("billedtoname", paymentsService.getPaymnetsBilledToName(clientid, returntype));
		supportMap.put("billedtogstin", paymentsService.getPaymnetsBilledToGstin(clientid, returntype));
		
		return supportMap;
	}
}

