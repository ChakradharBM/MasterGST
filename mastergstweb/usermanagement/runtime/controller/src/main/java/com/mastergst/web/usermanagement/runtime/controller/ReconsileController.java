package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.repository.OtherConfigurationRepository;
import com.mastergst.usermanagement.runtime.service.ReconsileService;
import com.mastergst.usermanagement.runtime.support.Utility;

@Controller
public class ReconsileController {
	
	@Autowired
	private ReconsileService reconsileService;
	@Autowired
	OtherConfigurationRepository otherConfigurationRepository;
		
	@RequestMapping(value = "/reconsileinvs/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> getReconsileInvoices(@PathVariable("id") String id, @PathVariable("returntype") String returntype, 
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, 
			ModelMap model, HttpServletRequest request) throws Exception {
		String strMonth = month < 10 ? "0" + month : month + "";
		String yearCode = Utility.getYearCode(month, year);
		String fp = strMonth+year;
		OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
		Boolean billdate = false;
		if(isNotEmpty(otherconfig)){
			billdate = otherconfig.isEnableTransDate();
		}
		String purchaseRegister = "";
		String prMannualMatched = "";
		String gstr2a = "";
		String gstr2aMannualMatched = "";
		if(isEmpty(otherconfig) || !billdate) {
			purchaseRegister = reconsileService.getMatchedAndPresentMonthPurchasesInvoices(clientid, month, yearCode, fp,year);
			prMannualMatched = reconsileService.getMannualMatchedAndPresentMonthPurchasesInvoices(clientid, month, yearCode, fp,year);
			gstr2a = reconsileService.getMatchedAndPresentMonthGSTR2AInvoices(clientid, month, yearCode, fp,year);
			gstr2aMannualMatched = reconsileService.getMannualMatchedAndPresentMonthGSTR2AInvoices(clientid, month, yearCode, fp,year);
		}else {
			purchaseRegister = reconsileService.getMatchedAndPresentMonthPurchasesInvoicesByTransactionDate(clientid, month, yearCode, fp,year);
			prMannualMatched = reconsileService.getMannualMatchedAndPresentMonthPurchasesInvoicesByTransactionDate(clientid, month, yearCode, fp,year);
			gstr2a = reconsileService.getMatchedAndPresentMonthGSTR2AInvoicesByTransactionDate(clientid, month, yearCode, fp,year);
			gstr2aMannualMatched = reconsileService.getMannualMatchedAndPresentMonthGSTR2AInvoicesByTransactionDate(clientid, month, yearCode, fp,year);
		}
		
		Map<String,String> reconsileInvoices =  Maps.newHashMap();
		reconsileInvoices.put("prFY", purchaseRegister);
		reconsileInvoices.put("g2FYMatched", gstr2a);
		reconsileInvoices.put("gPMannualFYMatched", prMannualMatched);
		reconsileInvoices.put("g2MannualFYMatched", gstr2aMannualMatched);
		return reconsileInvoices;
	}
	
	@RequestMapping(value = "/yearlyreconsileinvs/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> getYearlyReconsileInvoices(@PathVariable("id") String id, @PathVariable("returntype") String returntype, 
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, 
			ModelMap model, HttpServletRequest request) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();  
		System.out.println(dateFormat.format(date));
		String purchaseRegister = reconsileService.getMatchedAndPresentMonthFYInvoices(clientid, year);
		String g2FYMatched = reconsileService.getgstr2Matchedfyinvs(clientid, year);
		String g2MannualFYMatched = reconsileService.getgstr2MannualMatchedfyinvs(clientid, year);
		String gPMannualFYMatched = reconsileService.getMannualMatchedAndPresentMonthFYInvoices(clientid, year);
		Map<String,String> reconsileInvoices =  Maps.newHashMap();
		reconsileInvoices.put("prFY", purchaseRegister);
		reconsileInvoices.put("g2FYMatched", g2FYMatched);
		reconsileInvoices.put("g2MannualFYMatched", g2MannualFYMatched);
		reconsileInvoices.put("gPMannualFYMatched", gPMannualFYMatched);
		return reconsileInvoices;
	}
}
