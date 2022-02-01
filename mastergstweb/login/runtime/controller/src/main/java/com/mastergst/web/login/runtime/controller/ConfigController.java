/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.login.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.Currencycodes;
import com.mastergst.configuration.service.CurrencycodesRepository;
import com.mastergst.configuration.service.HSNConfig;
import com.mastergst.configuration.service.HSNRepository;
import com.mastergst.configuration.service.HSNSACConfig;
import com.mastergst.configuration.service.IndustryType;
import com.mastergst.configuration.service.ServiceConfig;
import com.mastergst.configuration.service.ServiceRepository;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.configuration.service.UQCConfig;
import com.mastergst.core.common.MasterGSTConstants;

/**
 * Handles Configuration Data related CRUD operations depending on the URI
 * template.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 * @since 1.0
 */
@Controller
public class ConfigController {

	private static final Logger logger = LogManager.getLogger(ConfigController.class.getName());
	private static final String CLASSNAME = "ConfigController::";

	@Autowired
	ConfigService configService;
	@Autowired
	private HSNRepository hsnRepository;
	@Autowired
	private ServiceRepository serviceRepository;
	@Autowired
	private CurrencycodesRepository currencycodesRepository;
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/hsnconfig", method = RequestMethod.GET)
	public @ResponseBody List<HSNConfig> hsnData(ModelMap model,
			@RequestParam(value = "query", required = true) String query) throws Exception {
		final String method = "hsnData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + query);
		return configService.getHSNs(query);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/sacconfig", method = RequestMethod.GET)
	public @ResponseBody List<ServiceConfig> sacData(ModelMap model,
			@RequestParam(value = "query", required = true) String query) throws Exception {
		final String method = "sacData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + query);
		return configService.getServices(query);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/uqcconfig", method = RequestMethod.GET)
	public @ResponseBody List<UQCConfig> uqcData(ModelMap model,
			@RequestParam(value = "query", required = true) String query) throws Exception {
		final String method = "uqcData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + query);
		return configService.getUQCs(query);
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/validuqcconfig", method = RequestMethod.GET)
	public @ResponseBody List<UQCConfig> uqcValidData(ModelMap model,
			@RequestParam(value = "query", required = true) String query) throws Exception {
		final String method = "uqcData::";
		logger.debug(CLASSNAME + method + BEGIN);
		return configService.getValidUQCs(query);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/stateconfig", method = RequestMethod.GET)
	public @ResponseBody List<StateConfig> stateData(ModelMap model,
			@RequestParam(value = "query", required = true) String query) throws Exception {
		final String method = "stateData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + query);
		return configService.getStates(query);
	}
	
	@RequestMapping(value = "/srchstatecd", method = RequestMethod.GET)
	public @ResponseBody StateConfig getState(ModelMap model,
			@RequestParam(value = "code", required = true) String code) throws Exception {
		final String method = "getState::";
		for(StateConfig stateConfig : configService.getStates()) {
			if(code.equals(stateConfig.getTin() < 10 ? "0" + stateConfig.getTin() : stateConfig.getTin() + "")) {
				return stateConfig;
			}
		}
		
		return null;
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/hsnsacconfig", method = RequestMethod.GET)
	public @ResponseBody HSNSACConfig hsnSacData(ModelMap model,
			@RequestParam(value = "query", required = true) String query) throws Exception {
		final String method = "hsnSacData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + query);
		return configService.getHsnSac(query);
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/validhsnconfig", method = RequestMethod.GET)
	public @ResponseBody boolean hsnValidData(ModelMap model,
			@RequestParam(value = "query", required = true) String query) throws Exception {
		final String method = "uqcData::";
		logger.debug(CLASSNAME + method + BEGIN);
		boolean hsnExists = false;
		Map<String, String> hsnMap = configService.getHSNMap();
		Map<String, String> sacMap = configService.getSACMap();
		query = query.trim();
		if(isNotEmpty(query)){
			String code = null;
			String description = null;
			if(query.contains(" : ")) {
				String hsncode[]= query.split(" : ");
				code = hsncode[0];
				description = hsncode[1];
			}else{
				code = query;
			}
			
			if (hsnMap.containsKey(code)) {
				hsnExists = true;
				description = hsnMap.get(code);
			} else if (hsnMap.containsValue(code)) {
				hsnExists = true;
			}
			
			
	if(!hsnExists){
		if (sacMap.containsKey(code)) {
			hsnExists = true;
			description = sacMap.get(code);
		} else if (sacMap.containsValue(code)) {
			hsnExists = true;
		}
		if (isEmpty(description)) {
			for (String key : hsnMap.keySet()) {
				if (hsnMap.get(key).endsWith(" : " + code)) {
					description = hsnMap.get(key); 
					hsnExists = true;
					break;
				}
			}
			for (String key : sacMap.keySet()) {
				if (sacMap.get(key).endsWith(" : " + code)) {
					description = sacMap.get(key);
					hsnExists = true;
					break;
				}
			}
		}
	}
	}
		return hsnExists;
}
	
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/hsnOrSacData", method = RequestMethod.GET)
	public @ResponseBody boolean hsnOrSacData(ModelMap model,
			@RequestParam(value = "query", required = true) String query) throws Exception {
		final String method = "hsnOrSacData::";
		logger.debug(CLASSNAME + method + BEGIN);
		boolean hsnExists = true;
		ServiceConfig sac = null;
		HSNConfig hsn = hsnRepository.findByName(query);
		if(isEmpty(hsn)){
			sac = serviceRepository.findByName(query);
		}
		if(isNotEmpty(sac)){
			hsnExists = false;
		}
		return hsnExists;
}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/currencycodes", method = RequestMethod.GET)
	public @ResponseBody List<Currencycodes> currencycodesData(ModelMap model) throws Exception {
		final String method = "hsnData::";
		logger.debug(CLASSNAME + method + BEGIN);
		return configService.getCurrencyCode();
	}
	@RequestMapping(value = "/getCountrycode", method = RequestMethod.GET)
	public @ResponseBody Currencycodes countryCodesData(ModelMap model,@RequestParam("code") String code)throws Exception {
		final String method = "countryCodesData::";
		logger.debug(CLASSNAME + method + BEGIN);
		return currencycodesRepository.findByCode(code);
	}
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/getIndustryType", method = RequestMethod.GET)
	public @ResponseBody List<IndustryType> industryTypeData(ModelMap model,
			@RequestParam(value = "query", required = true) String query) throws Exception {
		final String method = "industryTypeData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + query);
		return configService.getIndustryTypes(query);
	}
	
}
