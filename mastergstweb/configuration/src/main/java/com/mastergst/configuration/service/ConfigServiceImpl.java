/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mastergst.core.util.NullUtil;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;

/**
 * Service Impl class for all config data to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class ConfigServiceImpl implements ConfigService {

	private static final Logger logger = LogManager.getLogger(ConfigServiceImpl.class.getName());
	private static final String CLASSNAME = "ConfigServiceImpl::";

	@Autowired
	private GroupsRepository groupsRepository;
	@Autowired
	private StateRepository stateRepository;
	@Autowired
	private HSNRepository hsnRepository;
	@Autowired
	private ServiceRepository serviceRepository;
	@Autowired
	private UQCRepository uqcRepository;
	@Autowired
	private SubscriptionPlanRepository subscriptionPlanRepository;
	@Autowired
	private CouponRepository couponRepository;
	@Autowired
	private CurrencycodesRepository currencycodesRepository;
	@Resource
	private Environment env;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private ErrorCodesRepository errorCodesRepository;
	@Autowired
	private DealerRepository dealerRepository;
	@Autowired
	private FeatureRepository featureRepository;
	@Autowired
	private CommonLedgerRepository commonLedgerRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	@Value("${metadata.json.location}")
	private String metadatajsonlocation;

	private static Map<String, List<String>> dealerMap = Maps.newHashMap();
	private static List<StateConfig> stateList = Lists.newArrayList();
	private static List<CountryConfig> countriesList = Lists.newArrayList();
	private static List<HSNConfig> hsnList = Lists.newArrayList();
	private static List<ServiceConfig> serviceList = Lists.newArrayList();
	private static List<FeatureConfig> featuresList = Lists.newArrayList();
	private static List<SubscriptionPlan> plansList = Lists.newArrayList();
	private static List<Coupons> couponList = Lists.newArrayList();
	private static List<UQCConfig> uqcList = Lists.newArrayList();
	private static List<Groups> groups = Lists.newArrayList();
	private static List<Commonledger> commonledger = Lists.newArrayList();
	private static List<ErrorCodeConfig> errorcodes = Lists.newArrayList();
	private static List<Currencycodes> currencycodes = Lists.newArrayList();
	private static List<ApisVesrionConfig> apisversion = Lists.newArrayList();
	private static List<IndustryType> industryType = Lists.newArrayList();
	private String[] metaData = { "feature", "delearfeature", "gstreturntypes", "hsn", "service", "state", "uqc","coupons","country","role","commonledger","errorcodes","currencycodes","industryType"}; //,"groups" ,"accounting_heads", "plans", "apis_version"

	public List<StateConfig> getStates() {
		final String method = "getStates::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (NullUtil.isEmpty(stateList)) {
			stateList = stateRepository.findAll();
		}
		return stateList;
	}

	public List<StateConfig> getStates(final String searchQuery) {
		final String method = "getStates(final String query)::";
		logger.debug(CLASSNAME + method + BEGIN);
		Query query = new Query();
		query.limit(37);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(Criteria.where("name")
				.regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
		logger.debug(CLASSNAME + method + "query tostring\t" + query.toString());
		List<StateConfig> states = mongoTemplate.find(query, StateConfig.class, "state");
		return states;
	}

	public Map<String, String> getStateMap() {
		final String method = "getStateMap::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, String> stateMap = Maps.newHashMap();
		List<StateConfig> states = getStates();
		if (NullUtil.isNotEmpty(states)) {
			for (StateConfig state : states) {
				stateMap.put(state.getCode(), state.getName());
			}
		}
		logger.debug(CLASSNAME + method + END);
		return stateMap;
	}
	
	public Map<String, String> getHSNMap() {
		final String method = "getHSNMap::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, String> hsnMap = Maps.newHashMap();
		List<HSNConfig> hsns = hsnRepository.findAll();
		if (NullUtil.isNotEmpty(hsns)) {
			for (HSNConfig hsn : hsns) {
				hsnMap.put(hsn.getCode(), hsn.getName());
			}
		}
		logger.debug(CLASSNAME + method + END);
		return hsnMap;
	}
	
	public Map<String, String> getSACMap() {
		final String method = "getSACMap::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, String> sacMap = Maps.newHashMap();
		List<ServiceConfig> services = getServices();
		if (NullUtil.isNotEmpty(services)) {
			for (ServiceConfig service : services) {
				sacMap.put(service.getCode(), service.getName());
			}
		}
		logger.debug(CLASSNAME + method + END);
		return sacMap;
	}

	public List<HSNConfig> getHSNs(final String searchQuery) {
		final String method = "getHSNs::";
		logger.debug(CLASSNAME + method + BEGIN);
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(Criteria.where("name")
				.regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
		logger.debug(CLASSNAME + method + "query tostring\t" + query.toString());
		List<HSNConfig> lHSNConfig = mongoTemplate.find(query, HSNConfig.class, "hsn");
		return lHSNConfig;
	}

	public HSNSACConfig getHsnSac(final String searchQuery) {
		final String method = "getHsnSac::";
		logger.debug(CLASSNAME + method + BEGIN);
		HSNSACConfig hsnSACConfig = new HSNSACConfig();
		hsnSACConfig.setHsnConfig(getHSNs(searchQuery));
		hsnSACConfig.setSacConfig(getServices(searchQuery));
		return hsnSACConfig;
	}
	
	public List<UQCConfig> getUQCs(final String searchQuery) {
		final String method = "getUQCs::";
		logger.debug(CLASSNAME + method + BEGIN);
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(Criteria.where("name")
				.regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
		logger.debug(CLASSNAME + method + "query tostring\t" + query.toString());
		List<UQCConfig> lUQCConfig = mongoTemplate.find(query, UQCConfig.class, "uqc");
		return lUQCConfig;
	}

	public List<UQCConfig> getValidUQCs(final String uqc) {
		final String method = "getUQCs::";
		logger.debug(CLASSNAME + method + BEGIN);
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(Criteria.where("name").is(uqc.toUpperCase()));
		logger.debug(CLASSNAME + method + "query tostring\t" + query.toString());
		List<UQCConfig> lUQCConfig = mongoTemplate.find(query, UQCConfig.class, "uqc");
		return lUQCConfig;
	}
	
	public List<UQCConfig> getUQCs() {
		final String method = "getUQCs::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (NullUtil.isEmpty(uqcList)) {
			uqcList = uqcRepository.findAll();
		}
		return uqcList;
	}

	public List<ServiceConfig> getServices() {
		final String method = "ServiceConfig::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (NullUtil.isEmpty(serviceList)) {
			serviceList = serviceRepository.findAll();
		}
		return serviceList;
	}

	public List<ServiceConfig> getServices(final String searchQuery) {
		final String method = "getServices(final String query)::";
		logger.debug(CLASSNAME + method + BEGIN);
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(Criteria.where("name")
				.regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
		logger.debug(CLASSNAME + method + "query tostring\t" + query.toString());
		List<ServiceConfig> lServiceConfig = mongoTemplate.find(query, ServiceConfig.class, "service");
		return lServiceConfig;
	}

	public List<FeatureConfig> getFeatures() {
		final String method = "getFeatures::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (NullUtil.isEmpty(featuresList)) {
			featuresList = featureRepository.findAll();
		}
		return featuresList;
	}
	
	public List<SubscriptionPlan> getPlans() {
		final String method = "getPlans::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (NullUtil.isEmpty(plansList)) {
			plansList = subscriptionPlanRepository.findAll();
		}
		return plansList;
	}

	public List<Coupons> getCoupons() {
		final String method = "getCoupons::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (NullUtil.isEmpty(couponList)) {
			couponList = couponRepository.findAll();
		}
		return couponList;
	}
	
	public void createMetaData() {
		final String method = "createMetaData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "metadatajsonlocation\t" + metadatajsonlocation);
		if (NullUtil.isNotEmpty(metadatajsonlocation)) {
			logger.debug(CLASSNAME + method + "is not empty\t" + metadatajsonlocation);

			try {
				// File folder = new File(metadatajsonlocation);
				// logger.debug(CLASSNAME + method + "is not empty\t" +
				// folder.getPath());
				// for (final File fileEntry : folder.listFiles()) {
				// logger.debug(CLASSNAME + method + "fileEntry.getName()\t" +
				// fileEntry.getName());
				// String collectionName = fileEntry.getName().substring(0,
				// fileEntry.getName().lastIndexOf(".json"));

				for (String collectionName : metaData) {
					if (mongoTemplate.collectionExists(collectionName)) {
						DBCollection collection = mongoTemplate.getCollection(collectionName);
						collection.drop();
					}
					DBCollection collection = mongoTemplate.getCollection(collectionName);
					String fileName = "classpath:" + metadatajsonlocation + collectionName + ".json";

					String text = Files.toString(ResourceUtils.getFile(fileName), Charsets.UTF_8);

					JSONObject json = new JSONObject(text);
					JSONArray jsonArray = json.getJSONArray(collectionName);
					int size = jsonArray.length();
					for (int i = 0; i < size; i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						DBObject dbObject = (DBObject) JSON.parse(jsonObject.toString());
						collection.insert(dbObject, WriteConcern.NORMAL);
					}
				}
				prepareDelearMap();
			} catch (Exception e) {
				logger.fatal(CLASSNAME + method + e.getMessage());
				e.printStackTrace();
			}
		}
		logger.debug(CLASSNAME + method + END);
	}

	public void prepareDelearMap() {
		final String method = "prepareDelearMap::";
		logger.debug(CLASSNAME + method + BEGIN);
		String[] dealertype = { "Compound", "NonCompound", "Casual","NonResident","InputServiceDistributor","SezUnit","SezDeveloper" };

		for (String dealer : dealertype) {
			logger.debug(CLASSNAME + method + "dealer\t" + dealer);
			List<String> dealerAcl = Lists.newArrayList();
			DealerConfig dealearconfig = dealerRepository.findByDeleartype(dealer.trim());
			if (NullUtil.isNotEmpty(dealearconfig)) {
				String featureids = dealearconfig.getFeatureid();
				StringTokenizer st = new StringTokenizer(featureids, ",");
				while (st.hasMoreElements()) {
					String featureid = (String) st.nextElement();
					FeatureConfig featureConfig = featureRepository.findByFeatureid(featureid.trim());
					if (NullUtil.isNotEmpty(featureConfig)) {
						dealerAcl.add(featureConfig.getFeaturename());
					}
				}
				dealerMap.put(dealer.trim(), dealerAcl);
			}

		}
		logger.debug(CLASSNAME + method + END);
	}

	public List<String> getDealerACL(final String dealerType) {
		final String method = "getDealerACL::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "dealerType\t" + dealerType);
		List<String> dealerAcl = Lists.newArrayList();
		if (NullUtil.isNotEmpty(dealerType)) {
			if (NullUtil.isNotEmpty(dealerMap)) {
				dealerAcl = dealerMap.get(dealerType.trim());
			} else {
				DealerConfig dealearconfig = dealerRepository.findByDeleartype(dealerType.trim());
				if (NullUtil.isNotEmpty(dealearconfig)) {
					String featureids = dealearconfig.getFeatureid();
					logger.debug(CLASSNAME + method + "featureids\t" + featureids);
					StringTokenizer st = new StringTokenizer(featureids, ",");
					while (st.hasMoreElements()) {
						String featureid = (String) st.nextElement();
						FeatureConfig featureConfig = featureRepository.findByFeatureid(featureid.trim());
						if (NullUtil.isNotEmpty(featureConfig)) {
							dealerAcl.add(featureConfig.getFeaturename());
						}
					}
					dealerMap.put(dealerType.trim(), dealerAcl);
				}
			}
		}
		logger.debug(CLASSNAME + method + END);
		return dealerAcl;
	}
	
	@Override
	public List<CountryConfig> getCountries() {
		final String method = "getStates::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (NullUtil.isEmpty(countriesList)) {
			countriesList = countryRepository.findAll();
		}
		return countriesList;
	}
	public List<Groups> getGroups() {
		if(NullUtil.isEmpty(groups)) {
			groups = groupsRepository.findAll();
		}
		return groups;
	}
	
	public List<Commonledger> getCommonledger() {
		if(NullUtil.isEmpty(commonledger)) {
			commonledger = commonLedgerRepository.findAll();
		}
		return commonledger;
	}

	@Override
	public List<ErrorCodeConfig> getErrorCodes() {
		final String method = "getStates::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (NullUtil.isEmpty(errorcodes)) {
			errorcodes = errorCodesRepository.findAll();
		}
		return errorcodes;
	}

	@Override
	public List<Currencycodes> getCurrencyCode() {
		final String method = "getStates::";
		logger.debug(CLASSNAME + method + BEGIN);
		if (NullUtil.isEmpty(currencycodes)) {
			currencycodes = currencycodesRepository.findAll();
		}
		return currencycodes;
	}

	@Override
	public StateConfig getStateName(String statename) {
		StateConfig state = stateRepository.findByName(statename);
		if(NullUtil.isEmpty(state)) {
			state = stateRepository.findByCode(statename);
		}else {
			if(statename.trim().matches("\\d*\\.?\\d+")) {
				state = stateRepository.findByTin(Integer.parseInt(statename));
			}
		}
		return state;
	}
	public List<IndustryType> getIndustryTypes(final String searchQuery) {
		final String method = "getIndustryTypes(final String query)::";
		logger.debug(CLASSNAME + method + BEGIN);
		Query query = new Query();
		query.limit(37);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(Criteria.where("name")
				.regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
		logger.debug(CLASSNAME + method + "query tostring\t" + query.toString());
		List<IndustryType> types = mongoTemplate.find(query, IndustryType.class, "industryType");
		return types;
	}
}
