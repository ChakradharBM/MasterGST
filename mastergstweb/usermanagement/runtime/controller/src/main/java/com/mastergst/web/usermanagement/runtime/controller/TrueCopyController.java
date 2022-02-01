/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.usermanagement.runtime.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.core.util.TCClient;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR3BOffsetLiability;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.ResponseData;
import com.mastergst.usermanagement.runtime.response.TrueCopyResponse;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.IHubConsumerService;

/**
 * Handles True Copy filing depending on the URI template.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 * @since 1.0
 */
@Controller
public class TrueCopyController {
	private static final Logger logger = LogManager.getLogger(TrueCopyController.class.getName());
	private static final String CLASSNAME = "TrueCopyController::";
	
	@Autowired
	private ClientService clientService;
	@Autowired
	private IHubConsumerService iHubConsumerService;
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/truecopy/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody TrueCopyResponse trueCopy(@PathVariable("id") String id, @PathVariable("clientid") String clientid, 
			@PathVariable("returntype") String returntype, @PathVariable("month") int month, @PathVariable("year") int year) {
		logger.info(CLASSNAME + "trueCopy:: Begin");
		ObjectMapper mapper = new ObjectMapper();
		TrueCopyResponse response=new TrueCopyResponse();
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("ddMMYYYYHH:mm:ss");
	    String strDate = sdf.format(cal.getTime());
	    String prefix="FCJHYGKQ";
		String suffix="UDSWUrmx";
		String apikey="ZEAQZDWO";
		String autstr=prefix+strDate+suffix;
		String cs=TCClient.md5_sum16(apikey+autstr);
		
		String strMonth =  (month)<10 ? "0"+(month) : (month)+"";
		String retPeriod = strMonth+year;
		Client client = clientService.findById(clientid);
		String gstn = client.getGstnnumber();
		Response returnResponse = null;
		try {
			returnResponse = iHubConsumerService.returnSummary(client, gstn, retPeriod, id, returntype, true);
		} catch(Exception e) {
			logger.error(CLASSNAME + "trueCopy :: Return Summary ERROR {}", e);
		}
		String strContent = null, hashContent = null;
		if(NullUtil.isNotEmpty(returnResponse) && NullUtil.isNotEmpty(returnResponse.getData())) {
			try {
				strContent = mapper.writeValueAsString(returnResponse.getData());
				if(NullUtil.isNotEmpty(returntype) && returntype.equals(MasterGSTConstants.GSTR3B)) {
					GSTR3BOffsetLiability partB = returnResponse.getData().getTaxPymt();
					ResponseData partA = returnResponse.getData();
					partA.setTaxPymt(new GSTR3BOffsetLiability());
					String strPartA = mapper.writeValueAsString(partA);
					String strPartB = mapper.writeValueAsString(partB);
					String input256HashA = TCClient.getSha256(strPartA);
					String input256HashB = TCClient.getSha256(strPartB);
					hashContent = input256HashA + input256HashB;
					logger.info(CLASSNAME + "trueCopy:: Hash Part 1 {}",input256HashA);
					logger.info(CLASSNAME + "trueCopy:: Hash Part 2 {}",input256HashB);
				} else {
					hashContent = strContent;
				}
			} catch (JsonProcessingException e) {
				logger.error(CLASSNAME + "trueCopy:: ERROR", e);
			}	
		}
		logger.info(CLASSNAME + "trueCopy:: strContent {}",strContent);
		if(NullUtil.isNotEmpty(strContent)) {
			String input256Hash = TCClient.getSha256(hashContent);
			logger.info(CLASSNAME + "trueCopy:: Final Hash {}",input256Hash);
			response.setHash256(input256Hash);
			response.setAuthstr(autstr);
			response.setCs(cs);
			response.setContent(strContent);
		}
		return response;
	}
}
