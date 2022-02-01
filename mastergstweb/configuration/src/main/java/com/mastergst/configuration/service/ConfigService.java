/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import java.util.List;
import java.util.Map;

/**
 * Service interface for all config data to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
public interface ConfigService {

	void createMetaData();

	List<StateConfig> getStates();

	Map<String, String> getStateMap();
	
	Map<String, String> getHSNMap();
	
	Map<String, String> getSACMap();

	List<ServiceConfig> getServices();

	List<FeatureConfig> getFeatures();
	
	List<SubscriptionPlan> getPlans();
	
	List<UQCConfig> getUQCs();

	List<HSNConfig> getHSNs(final String searchQuery);

	List<ServiceConfig> getServices(final String searchQuery);

	List<StateConfig> getStates(final String searchQuery);
	
	List<UQCConfig> getUQCs(final String searchQuery);
	
	List<UQCConfig> getValidUQCs(final String uqc);

	List<String> getDealerACL(final String dealerType);

	HSNSACConfig getHsnSac(final String searchQuery);
	
	List<CountryConfig> getCountries();
	List<Groups> getGroups();
	List<Commonledger> getCommonledger();
	List<ErrorCodeConfig> getErrorCodes();
	List<Currencycodes> getCurrencyCode();
	List<IndustryType> getIndustryTypes(final String searchQuery);
	StateConfig getStateName(final String statename);
}
