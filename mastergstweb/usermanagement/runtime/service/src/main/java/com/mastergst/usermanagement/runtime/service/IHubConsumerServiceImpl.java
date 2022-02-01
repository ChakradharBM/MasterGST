/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.InputStream;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.ibm.icu.text.NumberFormat;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.domain.Base;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.JsonUtils;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.HeaderKeys;
import com.mastergst.login.runtime.repository.HeaderKeysRepository;
import com.mastergst.usermanagement.runtime.domain.Anx1InvoiceSupport;
import com.mastergst.usermanagement.runtime.domain.AnxInvoiceParent;
import com.mastergst.usermanagement.runtime.domain.AnxInvoiceSupport;
import com.mastergst.usermanagement.runtime.domain.CancelEwayBill;
import com.mastergst.usermanagement.runtime.domain.CancelIRN;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientStatus;
import com.mastergst.usermanagement.runtime.domain.EBillVehicleListDetails;
import com.mastergst.usermanagement.runtime.domain.EINVOICE;
import com.mastergst.usermanagement.runtime.domain.EWAYBILL;
import com.mastergst.usermanagement.runtime.domain.EinvoiceConfigurations;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR3B;
import com.mastergst.usermanagement.runtime.domain.GSTR4;
import com.mastergst.usermanagement.runtime.domain.GSTR8;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.ewaybill.ExtendValidity;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2B;
import com.mastergst.usermanagement.runtime.repository.ClientStatusRepository;
import com.mastergst.usermanagement.runtime.repository.EinvoiceConfigurationRepository;
import com.mastergst.usermanagement.runtime.response.ANX2Response;
import com.mastergst.usermanagement.runtime.response.Anx1ReturnsResponse;
import com.mastergst.usermanagement.runtime.response.CancelIRNResponse;
import com.mastergst.usermanagement.runtime.response.DigioRequest;
import com.mastergst.usermanagement.runtime.response.DigioResponse;
import com.mastergst.usermanagement.runtime.response.EwayBillCancelResponse;
import com.mastergst.usermanagement.runtime.response.EwayBillDateResponse;
import com.mastergst.usermanagement.runtime.response.EwayBillItems;
import com.mastergst.usermanagement.runtime.response.EwayBillResponse;
import com.mastergst.usermanagement.runtime.response.EwayBillResponseData;
import com.mastergst.usermanagement.runtime.response.EwayBillVehicleListDetails;
import com.mastergst.usermanagement.runtime.response.EwayBillVehicleUpdateResponse;
import com.mastergst.usermanagement.runtime.response.GSTR1AutoLiabilityResponse;
import com.mastergst.usermanagement.runtime.response.GSTR1Response;
import com.mastergst.usermanagement.runtime.response.GSTR2BResponse;
import com.mastergst.usermanagement.runtime.response.GSTR2Response;
import com.mastergst.usermanagement.runtime.response.GSTR6Response;
import com.mastergst.usermanagement.runtime.response.GSTR8GetResponse;
import com.mastergst.usermanagement.runtime.response.GSTR9GetResponse;
import com.mastergst.usermanagement.runtime.response.GSTRCommonResponse;
import com.mastergst.usermanagement.runtime.response.GSTRResponse;
import com.mastergst.usermanagement.runtime.response.GenerateEwayBillResponse;
import com.mastergst.usermanagement.runtime.response.GenerateEwayBillResponseData;
import com.mastergst.usermanagement.runtime.response.GenerateIRNResponse;
import com.mastergst.usermanagement.runtime.response.GenerateIRNResponseData;
import com.mastergst.usermanagement.runtime.response.LedgerResponse;
import com.mastergst.usermanagement.runtime.response.NewReturnsResponse;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.ResponseData;
import com.mastergst.usermanagement.runtime.response.TransGSTINResponse;
import com.mastergst.usermanagement.runtime.response.ewaybill.EwayBillRejectResponse;
import com.mastergst.usermanagement.runtime.response.ewaybill.ExtendValidityResponse;
import com.mastergst.usermanagement.runtime.response.gstr4.GSTR4AnnualCMPResponse;

/**
 * Service interface for consuming IHub services.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class IHubConsumerServiceImpl implements IHubConsumerService {

	private static final Logger logger = LogManager.getLogger(IHubConsumerServiceImpl.class.getName());
	private static final String CLASSNAME = "IHubConsumerServiceImpl::";
	private static boolean initialConfig = false;
	
	@Value("${ihub.base.url}")
	private String iHubUrl;
	@Value("${ihub.email}")
	private String iHubEmail;
	@Value("${ihub.client.id}")
	private String iHubClientId;
	@Value("${ihub.client.secret}")
	private String iHubClientSecret;
	@Value("${ihubebillno.client.id}")
	private String iHubEnoClientId;
	@Value("${ihubebillno.client.secret}")
	private String iHubEnoClientSecret;
	@Value("${ihubeinvno.client.id}")
	private String iHubEinvClientId;
	@Value("${ihubeinvno.client.secret}")
	private String iHubEinvClientSecret;
	@Value("${digio.client.id}")
	private String digioClientId;
	@Value("${digio.client.secret}")
	private String digioClientSecret;
	@Value("${digio.url}")
	private String digioUrl;
	@Value("${ihub.conn.timeout}")
	private Integer connectionTimeout;
	@Value("${ihub.request.timeout}")
	private Integer requestTimeout;
	
	@Autowired
	ConfigService configService;
	
	@Autowired
	ClientService clientService;
	@Autowired
	EwaybillService ewaybillService;
	@Autowired
	EinvoiceService einvoiceService;
	@Autowired
	HeaderKeysRepository headerKeysRepository;
	@Autowired
	private ClientStatusRepository clientStatusRepository;
	@Autowired
	private EinvoiceConfigurationRepository einvoiceConfigurationRepository;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public void setConfig() {
		Unirest.setConcurrency(50, 40);
		Unirest.setTimeouts(connectionTimeout, requestTimeout);
	}
	
	private Map<String, String> getGSTHeader(String state, final String gstName) {
		logger.debug(CLASSNAME + "getGSTHeader : Begin");
		Map<String, String> gstnHeader = Maps.newHashMap();
		try {
			if(!initialConfig) {
				initialConfig = true;
				setConfig();
			}
			gstnHeader.put("client_id", iHubClientId);
			gstnHeader.put("client_secret", iHubClientSecret);
			gstnHeader.put("ip_address", InetAddress.getLocalHost().getHostAddress());
			if(NullUtil.isNotEmpty(state) && NullUtil.isNotEmpty(gstName)) {
				boolean include = true;
				if (state.contains("-")) {
					state = state.substring(0, state.indexOf("-")).trim();
				}
				for(StateConfig stateConfig : configService.getStates()) {
					if(stateConfig.getCode().equals(state)) {
						gstnHeader.put("state_cd", stateConfig.getTin()+"");
						include = false;
						break;
					} else if(stateConfig.getName().equals(state)) {
						gstnHeader.put("state_cd", stateConfig.getTin()+"");
						include = false;
						break;
					} else if(stateConfig.getName().equals(stateConfig.getTin()+"-"+state)) {
						gstnHeader.put("state_cd", stateConfig.getTin()+"");
						include = false;
						break;
					}
				}
				if(include) {
					gstnHeader.put("state_cd", state);
				}
				gstnHeader.put("gst_username", gstName);
			}
		} catch(Exception e) {
			logger.error(CLASSNAME + "getGSTHeader : ERROR", e);
		}
		logger.debug(CLASSNAME + "getGSTHeader : End");
		return gstnHeader;
	}
	
	private Map<String, String> getGSTTransactionHeader(final String state, final String gstName) throws Exception {
		logger.debug(CLASSNAME + "getGSTTransactionHeader : Begin");
		Map<String, String> gstnHeader = getGSTHeader(state, gstName);
		/*HeaderKeys headerKeys = headerKeysRepository.findByGstusernameAndStatecdAndEmail(gstnHeader.get("gst_username"), gstnHeader.get("state_cd"), iHubEmail);
		if(NullUtil.isNotEmpty(headerKeys) 
				&& NullUtil.isNotEmpty(headerKeys.getAuthtoken()) 
				&& NullUtil.isNotEmpty(headerKeys.getUpdatedDate())) {
			gstnHeader.put("ip_address", headerKeys.getIpusr());
			gstnHeader.put("txn", headerKeys.getTxn());
			Date now = new Date();
			long duration = now.getTime() - headerKeys.getUpdatedDate().getTime();
			long diff = TimeUnit.MILLISECONDS.toMinutes(duration);
			logger.debug(CLASSNAME + "getGSTTransactionHeader : Difference {} & Expiry {}", diff, headerKeys.getExpiry());
			if(diff >= 300) { // < (headerKeys.getExpiry()/2)
				performRefreshToken(gstnHeader);
			}
		} else {
			throw new Exception("OTP verification is not yet completed!");
		}*/
		logger.debug(CLASSNAME + "getGSTTransactionHeader : End");
		return gstnHeader;
	}
	
	public void invokeRefreshToken(HeaderKeys headerKey) throws Exception {
		logger.debug(CLASSNAME + "invokeRefreshToken : Begin");
		if(NullUtil.isNotEmpty(headerKey.getEmail()) && headerKey.getEmail().equals(iHubEmail)) {
			Map<String, String> gstnHeader = getGSTHeader(headerKey.getStatecd(), headerKey.getGstusername());
			gstnHeader.put("ip_address", headerKey.getIpusr());
			gstnHeader.put("txn", headerKey.getTxn());
			performRefreshToken(gstnHeader);
		}
	}
	
	private Response performRefreshToken(Map<String, String> gstnHeader) throws Exception {
		String refreshTokenUrl = String.format("%s/%s", iHubUrl, "/authentication/refreshtoken");
		logger.debug(CLASSNAME + "refreshToken : Headers: {}", gstnHeader);
		HttpResponse<JsonNode> refreshResponse = Unirest.get(refreshTokenUrl)
				.queryString("email", iHubEmail)
				.headers(gstnHeader).asJson();
		Response response = mapper.readValue(refreshResponse.getBody().toString(), Response.class);
		if(NullUtil.isEmpty(response) || NullUtil.isEmpty(response.getStatuscd()) || !response.getStatuscd().equals("1")) {
			logger.debug(CLASSNAME + "refreshToken : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "refreshToken : Response getStatuscd {}", response.getStatuscd());
			String message = "API access is not enabled!";
			if(NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError())
					&& NullUtil.isNotEmpty(response.getError().getMessage())) {
				message = response.getError().getMessage();
			}
			throw new Exception(message);
		}
		logger.debug(CLASSNAME + "refreshToken : Successful {}", response.getHeader());
		return response;
	}
	
	public Response otpRequest(final String state, final String gstName, final String ipAddress) {
		logger.debug(CLASSNAME + "otpRequest : Begin");
		Long startTime = System.currentTimeMillis();
		Map<String, String> gstnHeader = getGSTHeader(state, gstName);		
		if(NullUtil.isNotEmpty(ipAddress)) {
			gstnHeader.put("ip_address", ipAddress);
		}
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/authentication/otprequest");
		logger.debug(CLASSNAME + "otpRequest : Headers: {}", gstnHeader);
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString("email", iHubEmail)
					.headers(gstnHeader).asJson();
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "otpRequest : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "otpRequest : Response getStatuscd {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "otpRequest : Duration {}", (endTime - startTime));
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "otpRequest : ERROR", e);
		}
		logger.debug(CLASSNAME + "otpRequest : End");
		return new Response();
	}
	
	public Response authRequest(final String otp, final Response otpResponse) {
		logger.debug(CLASSNAME + "authRequest : Begin");
		Long startTime = System.currentTimeMillis();
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/authentication/authtoken");
		
		try {
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("otp", otp);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(otpResponse.getHeader()).asJson();
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "authRequest : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "authRequest : Response getStatuscd {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "authRequest : Duration {}", (endTime - startTime));
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "authRequest : ERROR", e);
		}
		logger.debug(CLASSNAME + "authRequest : End");
		return new Response();
	}
	
	public Response saveReturns(final Base invoice, final String state, final String gstName, final String gstn, 
			final String retPeriod, final String returnType, final boolean initial) throws Exception {
		logger.debug(CLASSNAME + "saveReturns : Begin");
		Long startTime = System.currentTimeMillis();
		String url = "/gstr1/retsave";
		if(NullUtil.isNotEmpty(returnType) && returnType.toLowerCase().startsWith("anx") && returnType.equalsIgnoreCase(MasterGSTConstants.ANX1)) {
			url = "/annexure/save/"+returnType+"/type/"+retPeriod+"/version/V1_0";
		} else if(NullUtil.isNotEmpty(returnType) && returnType.equalsIgnoreCase(MasterGSTConstants.ANX2)) {
			url = "annexure/save/"+returnType+"/type/SAVEANX2/version/V1_0";
		}else if(NullUtil.isNotEmpty(returnType)) {
			url = "/"+returnType.toLowerCase()+"/retsave";
		}
		String requestUrl = String.format("%s/%s", iHubUrl, url);
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("email", iHubEmail);
		
		Map<String, String> header = getGSTTransactionHeader(state, gstName);
		header.put("gstin", gstn);
		if(invoice instanceof InvoiceParent) {
			header.put("ret_period", ((InvoiceParent) invoice).getFp());
		} else {
			header.put("ret_period", retPeriod);
		}
		header.put("Content-Type", "application/json");
		
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		String content;
		try {
			if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR3B)) {
				PropertyFilter gstr3bFilter = new SimpleBeanPropertyFilter() {
					public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
						if (include(writer)) {
							if(!writer.getName().equals("sup_details") && !writer.getName().equals("sup_inter") 
									&& !writer.getName().equals("itc_elg") && !writer.getName().equals("inward_sup") && !writer.getName().equals("intr_ltfee")) {
								writer.serializeAsField(pojo, jgen, provider);
								return;
							} else {
								if(writer.getName().equals("sup_details") && NullUtil.isNotEmpty(((GSTR3B) pojo).getSupDetails())
										 && (NullUtil.isNotEmpty(((GSTR3B) pojo).getSupDetails().getIsupRev())
												 || (NullUtil.isNotEmpty(((GSTR3B) pojo).getSupDetails().getOsupDet()))
												 || (NullUtil.isNotEmpty(((GSTR3B) pojo).getSupDetails().getOsupNilExmp()))
												 || (NullUtil.isNotEmpty(((GSTR3B) pojo).getSupDetails().getOsupNongst()))
												 || (NullUtil.isNotEmpty(((GSTR3B) pojo).getSupDetails().getOsupZero())))) {
									writer.serializeAsField(pojo, jgen, provider);
									return;
								} else if(writer.getName().equals("sup_inter") && NullUtil.isNotEmpty(((GSTR3B) pojo).getInterSup())
										 && (NullUtil.isNotEmpty(((GSTR3B) pojo).getInterSup().getCompDetails())
												 || (NullUtil.isNotEmpty(((GSTR3B) pojo).getInterSup().getUinDetails()))
												 || (NullUtil.isNotEmpty(((GSTR3B) pojo).getInterSup().getUnregDetails())))) {
									writer.serializeAsField(pojo, jgen, provider);
									return;
								} else if(writer.getName().equals("itc_elg") && NullUtil.isNotEmpty(((GSTR3B) pojo).getItcElg())
										 && (NullUtil.isNotEmpty(((GSTR3B) pojo).getItcElg().getItcAvl())
												 || (NullUtil.isNotEmpty(((GSTR3B) pojo).getItcElg().getItcInelg()))
												 || (NullUtil.isNotEmpty(((GSTR3B) pojo).getItcElg().getItcNet())
														 && (NullUtil.isNotEmpty(((GSTR3B) pojo).getItcElg().getItcNet().getIamt())
																 || NullUtil.isNotEmpty(((GSTR3B) pojo).getItcElg().getItcNet().getCamt())))
												 || (NullUtil.isNotEmpty(((GSTR3B) pojo).getItcElg().getItcRev())))) {
									writer.serializeAsField(pojo, jgen, provider);
									return;
								} else if(writer.getName().equals("inward_sup") && NullUtil.isNotEmpty(((GSTR3B) pojo).getInwardSup())
										 && (NullUtil.isNotEmpty(((GSTR3B) pojo).getInwardSup().getIsupDetails()))
										 && (NullUtil.isNotEmpty(((GSTR3B) pojo).getInwardSup().getIsupDetails().get(0).getInter())
												 || NullUtil.isNotEmpty(((GSTR3B) pojo).getInwardSup().getIsupDetails().get(0).getIntra()))) {
									writer.serializeAsField(pojo, jgen, provider);
									return;
								} else if(writer.getName().equals("intr_ltfee") && NullUtil.isNotEmpty(((GSTR3B) pojo).getIntrLtfee())
										 && (NullUtil.isNotEmpty(((GSTR3B) pojo).getIntrLtfee().getIntrDetails()))
										 && (NullUtil.isNotEmpty(((GSTR3B) pojo).getIntrLtfee().getIntrDetails().getIamt())
												 || NullUtil.isNotEmpty(((GSTR3B) pojo).getIntrLtfee().getIntrDetails().getCamt()))) {
									writer.serializeAsField(pojo, jgen, provider);
									return;
								}
							}
						} else if (!jgen.canOmitFields()) { // since 2.3
							writer.serializeAsOmittedField(pojo, jgen, provider);
						}
					}
					@Override
					protected boolean include(BeanPropertyWriter writer) {
						return true;
					}
					@Override
					protected boolean include(PropertyWriter writer) {
						return true;
					}
				};
				FilterProvider filters = new SimpleFilterProvider().addFilter("gstr3bFilter", gstr3bFilter);
				content = mapper.writer(filters).writeValueAsString(invoice);
			} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR1)) {
				PropertyFilter gstr1Filter = new SimpleBeanPropertyFilter() {
					public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
						if (include(writer)) {
							if(!writer.getName().equals("nil") && !writer.getName().equals("hsn")) {
								writer.serializeAsField(pojo, jgen, provider);
								return;
							} else {
								if(writer.getName().equals("nil") && NullUtil.isNotEmpty(((GSTR1) pojo).getNil())
										 && (NullUtil.isNotEmpty(((GSTR1) pojo).getNil().getInv()))) {
									writer.serializeAsField(pojo, jgen, provider);
									return;
								} else if(writer.getName().equals("hsn") && NullUtil.isNotEmpty(((GSTR1) pojo).getHsn())
										 && (NullUtil.isNotEmpty(((GSTR1) pojo).getHsn().getData()))) {
									writer.serializeAsField(pojo, jgen, provider);
									return;
								}
							}
						} else if (!jgen.canOmitFields()) { // since 2.3
							writer.serializeAsOmittedField(pojo, jgen, provider);
						}
					}
					@Override
					protected boolean include(BeanPropertyWriter writer) {
						return true;
					}
					@Override
					protected boolean include(PropertyWriter writer) {
						return true;
					}
				};
				FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", gstr1Filter);
				content = mapper.writer(filters).writeValueAsString(invoice);
			} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR4)) {
				PropertyFilter gstr1Filter = new SimpleBeanPropertyFilter() {
					public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
						if (include(writer)) {
							if(!writer.getName().equals("hsn")) {
								writer.serializeAsField(pojo, jgen, provider);
								return;
							} else {
								if(writer.getName().equals("hsn") && NullUtil.isNotEmpty(((GSTR4) pojo).getHsn())
										 && (NullUtil.isNotEmpty(((GSTR4) pojo).getHsn().getData()))) {
									writer.serializeAsField(pojo, jgen, provider);
									return;
								}
							}
						} else if (!jgen.canOmitFields()) { // since 2.3
							writer.serializeAsOmittedField(pojo, jgen, provider);
						}
					}
					@Override
					protected boolean include(BeanPropertyWriter writer) {
						return true;
					}
					@Override
					protected boolean include(PropertyWriter writer) {
						return true;
					}
				};
				FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", gstr1Filter);
				content = mapper.writer(filters).writeValueAsString(invoice);
			} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR9)) {
				PropertyFilter gstr9Filter = new SimpleBeanPropertyFilter() {
					public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
						if (include(writer)) {
							writer.serializeAsField(pojo, jgen, provider);
							return;
						} else if (!jgen.canOmitFields()) { // since 2.3
							writer.serializeAsOmittedField(pojo, jgen, provider);
						}
					}
					@Override
					protected boolean include(BeanPropertyWriter writer) {
						return true;
					}
					@Override
					protected boolean include(PropertyWriter writer) {
						return true;
					}
				};
				FilterProvider filters = new SimpleFilterProvider()
						.addFilter("gstr9Filter", gstr9Filter);
				content = mapper.writer(filters).writeValueAsString(invoice);
			} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR8)) {
				PropertyFilter gstr8Filter = new SimpleBeanPropertyFilter() {
					public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
						if (include(writer)) {
							writer.serializeAsField(pojo, jgen, provider);
							return;
						} else if (!jgen.canOmitFields()) { // since 2.3
							writer.serializeAsOmittedField(pojo, jgen, provider);
						}
					}
					@Override
					protected boolean include(BeanPropertyWriter writer) {
						return true;
					}
					@Override
					protected boolean include(PropertyWriter writer) {
						return true;
					}
				};
				FilterProvider filters = new SimpleFilterProvider().addFilter("gstr8Filter", gstr8Filter);
				content = mapper.writer(filters).writeValueAsString(invoice);
			} else {
				content = mapper.writeValueAsString(invoice);
			}
			logger.debug(CLASSNAME + "saveReturns : content {}"+ content);
			HttpResponse<JsonNode> jsonResponse = null;
			if(NullUtil.isNotEmpty(returnType) && returnType.toLowerCase().startsWith("anx")) {
				jsonResponse = Unirest.post(requestUrl)
					.queryString(queryStringMap)
					.headers(header)
					.body(new JsonNode(content)).asJson();
			} else {
				jsonResponse = Unirest.put(requestUrl)
						.queryString(queryStringMap)
						.headers(header)
						.body(new JsonNode(content)).asJson();
			}
			logger.debug(CLASSNAME + "saveReturns : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "saveReturns : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "saveReturns : Response Code {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "{} saveReturns : Duration {}", returnType, (endTime - startTime));
			if(initial && NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError())
					&& NullUtil.isNotEmpty(response.getError().getErrorcd())
					&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
				performRefreshToken(header);
				return saveReturns(invoice, state, gstName, gstn, retPeriod, returnType, false);
			} else if(NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError()) && NullUtil.isNotEmpty(response.getError().getMessage())) {
				logger.info("Response Error Message ::"+response.getError().getMessage());
				String error = processRelatederrors(response.getError().getMessage());
				response.getError().setMessage(error);
				return response;
			}else if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "saveReturns : ERROR", e);
		}
		logger.debug(CLASSNAME + "saveReturns : End");
		return new Response();
	}
	
	public Response returnStatus(final String refId, final String state, final String gstName, 
			final String gstn, final String retPeriod, final boolean initial) {
		logger.debug(CLASSNAME + "returnStatus : Begin");
		Long startTime = System.currentTimeMillis();
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/gstr/retstatus");
		
		try {
			Map<String, String> header = getGSTTransactionHeader(state, gstName);
			header.put("gstin", gstn);
			header.put("ret_period", retPeriod);
				
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("gstin", header.get("gstin"));
			queryStringMap.put("email", iHubEmail);
			
			queryStringMap.put("returnperiod", header.get("ret_period"));
			queryStringMap.put("refid", refId);
			logger.debug(CLASSNAME + "returnStatus : queryStringMap {}", queryStringMap);
			logger.debug(CLASSNAME + "returnStatus : header {}", header);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(header).asJson();
			logger.debug(CLASSNAME + "returnStatus : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "returnStatus : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				//mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "returnStatus : Response Code {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "returnStatus : Duration {}", (endTime - startTime));
			if(initial && NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError())
					&& NullUtil.isNotEmpty(response.getError().getErrorcd())
					&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
				performRefreshToken(header);
				return returnStatus(refId, state, gstName, gstn, retPeriod, false);
			} else if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnStatus : ERROR", e);
		}
		logger.debug(CLASSNAME + "returnStatus : End");
		return new Response();
	}
	
	public Response newReturnStatus(final String refId, final String state, final String gstName, 
			final String gstn, final String retPeriod, String rettype, final boolean initial) {
		logger.debug(CLASSNAME + "returnStatus : Begin");
		Long startTime = System.currentTimeMillis();
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/all/newretstatus");
		
		try {
			Map<String, String> header = getGSTTransactionHeader(state, gstName);
			header.put("gstin", gstn);
			header.put("ret_period", retPeriod);
				
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("gstin", header.get("gstin"));
			queryStringMap.put("email", iHubEmail);
			
			queryStringMap.put("returnperiod", header.get("ret_period"));
			queryStringMap.put("refid", refId);
			queryStringMap.put("rettype", rettype);
			
			logger.info(CLASSNAME + "returnStatus : queryStringMap {}", queryStringMap);
			logger.info(CLASSNAME + "returnStatus : header {}", header);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(header).asJson();
			logger.debug(CLASSNAME + "returnStatus : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "returnStatus : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				//mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "returnStatus : Response Code {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "returnStatus : Duration {}", (endTime - startTime));
			if(initial && NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError())
					&& NullUtil.isNotEmpty(response.getError().getErrorcd())
					&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
				performRefreshToken(header);
				return returnStatus(refId, state, gstName, gstn, retPeriod, false);
			} else if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnStatus : ERROR", e);
		}
		logger.debug(CLASSNAME + "returnStatus : End");
		return new Response();
	}
	
	public InvoiceParent getGSTRXInvoices(final Client client, final String gstin, final int month, final int year, final String gstr, 
			final String type, final String ctin, final String userId, final boolean initial) throws MasterGSTException {
		logger.debug(CLASSNAME + "getGSTRXInvoices : Begin");
		Long startTime = System.currentTimeMillis();
		String url=String.format("%s/%s", iHubUrl, "/"+gstr.toLowerCase()+"/");
		if(NullUtil.isNotEmpty(url)) {
			if(type.equals(MasterGSTConstants.B2B)) {
				url+="b2b";
			} else if(type.equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
				if(gstr.equals(MasterGSTConstants.GSTR1) || gstr.equals(MasterGSTConstants.GSTR1A)) {
					url+="cdnr";
				} else {
					url+="cdn";
				}
			} else if(type.equals(MasterGSTConstants.B2C)) {
				url+="b2cs";
			} else if(type.equals(MasterGSTConstants.B2CL)) {
				url+="b2cl";
			} else if(type.equals(MasterGSTConstants.B2BUR)) {
				url+="b2bur";
			} else if(type.equals(MasterGSTConstants.CDNUR)) {
				url+="cdnur";
			} else if(type.equals(MasterGSTConstants.NIL)) {
				url+="nil";
			} else if(type.equals(MasterGSTConstants.IMP_GOODS)) {
				url+="impg";
			} else if(type.equals(MasterGSTConstants.IMPGSEZ)) {
				url+="impgsez";
			} else if(type.equals(MasterGSTConstants.IMP_SERVICES)) {
				url+="imps";
			} else if(type.equals(MasterGSTConstants.ITC_REVERSAL)) {
				url+="itcrvsl";
			} else if(type.equals(MasterGSTConstants.ATPAID)) {
				url+="txp";
			} else if(type.equals(MasterGSTConstants.EXPORTS)) {
				url+="exp";
			} else if(type.equals(MasterGSTConstants.ADVANCES)) {
				if(gstr.equals(MasterGSTConstants.GSTR1)) {
					url+="at";
				} else {
					url+="txli";
				}
			} else if(type.equals("ITC")) {
				if(gstr.equals(MasterGSTConstants.GSTR6)) {
					url+="itc";
				}
			}else if(type.equals("latefee")) {
				if(gstr.equals(MasterGSTConstants.GSTR6)) {
					url+="latefee";
				}
			}else {
				url+=type.toLowerCase();
			}
			
			try {
				String strMonth =  month<10 ? "0"+month : month+"";
				String retPeriod = strMonth+year;
				
				Map<String, Object> queryStringMap = Maps.newHashMap();
				queryStringMap.put("gstin", gstin);
				queryStringMap.put("email", iHubEmail);
				queryStringMap.put("retperiod", retPeriod);
				
				if(gstr.equals(MasterGSTConstants.GSTR1A) || type.endsWith("A")) {
					if(NullUtil.isNotEmpty(ctin)) {
						queryStringMap.put("ctin", ctin);
						queryStringMap.put("actionrequired", "N");
						String startDate = null;
						if(month <= 3) {
							startDate = "01-04-"+(year-1);
						} else {
							startDate = "01-04-"+year;
						}
						queryStringMap.put("fromtime", startDate);
					}
				} else {
					if(NullUtil.isNotEmpty(ctin)) {
						queryStringMap.put("ctin", ctin);
						queryStringMap.put("actionrequired", "N");
						queryStringMap.put("fromtime", "01-"+strMonth+"-"+year);
					}
				}
				Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
				logger.debug(CLASSNAME + "getGSTRXInvoices : queryStringMap {}", queryStringMap);
				logger.debug(CLASSNAME + "getGSTRXInvoices : gstnHeader {}", gstnHeader);
				
				HttpResponse<JsonNode> jsonResponse = Unirest.get(url)
						.queryString(queryStringMap)
						.headers(gstnHeader).asJson();
				logger.debug(CLASSNAME + "getGSTRXInvoices : jsonResponse {}", jsonResponse.toString());
				logger.debug(CLASSNAME + "getGSTRXInvoices : jsonResponse.getBody() {}", jsonResponse.getBody());
				Long endTime = System.currentTimeMillis();
				logger.debug(CLASSNAME + "getGSTRXInvoices {} : Type {} : Duration {}", gstr, type, (endTime - startTime));
				if (jsonResponse.getStatus() == 200) {
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				}
				GSTRResponse response = null;
				if(gstr.equals(MasterGSTConstants.GSTR1) || gstr.equals(MasterGSTConstants.GSTR1A)) {
					response = (GSTRResponse) mapper.readValue(jsonResponse.getBody().toString(), GSTR1Response.class);
				} else if(gstr.equals(MasterGSTConstants.GSTR2) || gstr.equals(MasterGSTConstants.GSTR2A)) {
					response = (GSTRResponse) mapper.readValue(jsonResponse.getBody().toString(), GSTR2Response.class);
				} else if(gstr.equals(MasterGSTConstants.GSTR6) || gstr.equals(MasterGSTConstants.GSTR6A)) {
					response = (GSTRResponse) mapper.readValue(jsonResponse.getBody().toString(), GSTR6Response.class);
				} else {
					response = mapper.readValue(jsonResponse.getBody().toString(), GSTRResponse.class);
				}
				if(NullUtil.isNotEmpty(response) && NullUtil.isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals("1") && NullUtil.isNotEmpty(response.getData())) {
					return response.getData();
				} else if(initial && NullUtil.isNotEmpty(response) 
						&& NullUtil.isNotEmpty(response.getStatuscd()) 
						&& response.getStatuscd().equals("0")
						&& NullUtil.isNotEmpty(response.getError())
						&& NullUtil.isNotEmpty(response.getError().getErrorcd())
						&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
					performRefreshToken(gstnHeader);
					return getGSTRXInvoices(client, gstin, month, year, gstr, type, ctin, userId, false);
				} else if(NullUtil.isNotEmpty(response) && NullUtil.isNotEmpty(response.getError())) {
					if(NullUtil.isEmpty(response.getError().getMessage()) 
							|| !response.getError().getErrorcd().equals("RET13509")) {
						if("Please select the preference".equalsIgnoreCase(response.getError().getMessage().trim())	|| response.getError().getErrorcd().equals("RET13509")) {
								clientService.getDownloadGSTRXStatus(client, userId, gstr, month, year);
						}
						throw new MasterGSTException(response.getError().getMessage());
					}
				}
			} catch (Exception e) {
				logger.error(CLASSNAME + "getGSTRXInvoices : ERROR", e);
				throw new MasterGSTException(e.getMessage());
			}
		}
		logger.debug(CLASSNAME + "getGSTRXInvoices : End");
		return null;
	}
	
	public Response returnSummary(final Client client, final String gstin, final String retPeriod, 
			final String userId, final String returnType, final boolean initial) throws Exception {
		logger.debug(CLASSNAME + "returnSummary : Begin");
		Long startTime = System.currentTimeMillis();
		String url = "/gstr1/retsum";
		if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR3B)) {
			url = "/gstr3b/retsum";
		} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR1A)) {
			url = "/gstr1a/retsum";
		} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR4)) {
			url = "/gstr4/retsum";
		} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR6)) {
			url = "/gstr6/retsum";
		} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR1A)) {
			url = "/gstr1a/retsum";
		} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR2)) {
			url = "/gstr2/retsum";
		}
		String requestUrl = String.format("%s/%s", iHubUrl, url);
		
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("gstin", gstin);
		queryStringMap.put("email", iHubEmail);
		queryStringMap.put("retperiod", retPeriod);
		
		Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
		logger.debug(CLASSNAME + "returnSummary : queryStringMap {}", queryStringMap);
		logger.debug(CLASSNAME + "returnSummary : gstnHeader {}", gstnHeader);
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			logger.debug(CLASSNAME + "returnSummary : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "returnSummary : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "returnSummary : Response Code {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "{} returnSummary : Duration {}", returnType, (endTime - startTime));
			if(initial && NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError())
					&& NullUtil.isNotEmpty(response.getError().getErrorcd())
					&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
				performRefreshToken(gstnHeader);
				return returnSummary(client, gstin, retPeriod, userId, returnType, false);
			} else if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnSummary : ERROR", e);
		}
		logger.debug(CLASSNAME + "returnSummary : End");
		return new Response();
	}
	
	public Response returnHSNSummary(final Client client, final String gstin, final String returnType, final String retPeriod, 
			final String userId, final boolean initial) {
		logger.debug(CLASSNAME + "returnHSNSummary : Begin");
		Long startTime = System.currentTimeMillis();
		String url = "/gstr1/hsnsum";
		if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR4)) {
			url = "/gstr4/hsnsum";
		} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR6)) {
			url = "/gstr6/hsnsum";
		}
		String requestUrl = String.format("%s/%s", iHubUrl, url);
		
		try {
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("retperiod", retPeriod);
			
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			logger.debug(CLASSNAME + "returnHSNSummary : queryStringMap {}", queryStringMap);
			logger.debug(CLASSNAME + "returnHSNSummary : gstnHeader {}", gstnHeader);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			logger.debug(CLASSNAME + "returnHSNSummary : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "returnHSNSummary : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "returnHSNSummary : Response Code {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "{} returnHSNSummary : Duration {}", returnType, (endTime - startTime));
			if(initial && NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError())
					&& NullUtil.isNotEmpty(response.getError().getErrorcd())
					&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
				performRefreshToken(gstnHeader);
				return returnHSNSummary(client, gstin, returnType, retPeriod, userId, false);
			} else if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnHSNSummary : ERROR", e);
		}
		logger.debug(CLASSNAME + "returnHSNSummary : End");
		return new Response();
	}
	
	public Response returnDocIssue(final Client client, final String gstin, final String retPeriod, 
			final String userId, final boolean initial) {
		logger.debug(CLASSNAME + "returnDocIssue : Begin");
		Long startTime = System.currentTimeMillis();
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/gstr1/dociss");
		
		try {
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("retperiod", retPeriod);
			
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			logger.debug(CLASSNAME + "returnDocIssue : queryStringMap {}", queryStringMap);
			logger.debug(CLASSNAME + "returnDocIssue : gstnHeader {}", gstnHeader);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			logger.debug(CLASSNAME + "returnDocIssue : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "returnDocIssue : jsonResponse.getBody() {}", jsonResponse.getBody());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "Doc Issue : Duration {}", (endTime - startTime));
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "returnDocIssue : Response Code {}", response.getStatuscd());
			if(initial && NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError())
					&& NullUtil.isNotEmpty(response.getError().getErrorcd())
					&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
				performRefreshToken(gstnHeader);
				return returnDocIssue(client, gstin, retPeriod, userId, false);
			} else if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnDocIssue : ERROR", e);
		}
		logger.debug(CLASSNAME + "returnDocIssue : End");
		return new Response();
	}
	
	public Response trackStatus(final Client client, final String gstin, final String retPeriod, final String userId, 
			final String returnType, final boolean initial) {
		logger.debug(CLASSNAME + "trackStatus : Begin");
		Long startTime = System.currentTimeMillis();
		String requestUrl = String.format("%s/%s", iHubUrl, "/gstr/rettrack");
		
		try {
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("returnperiod", retPeriod);
			queryStringMap.put("type", returnType.replace("GST", ""));
			
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			logger.debug(CLASSNAME + "trackStatus : queryStringMap {}", queryStringMap);
			logger.debug(CLASSNAME + "trackStatus : gstnHeader {}", gstnHeader);
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			logger.debug(CLASSNAME + "trackStatus : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "trackStatus : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "trackStatus : Response Code {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "trackStatus : Duration {}", (endTime - startTime));
			if(initial && NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError())
					&& NullUtil.isNotEmpty(response.getError().getErrorcd())
					&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
				performRefreshToken(gstnHeader);
				return trackStatus(client, gstin, retPeriod, userId, returnType, false);
			} else if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "trackStatus : ERROR", e);
		}
		logger.debug(CLASSNAME + "trackStatus : End");
		return new Response();
	}
	
	public Response offsetLiability(final Client client, final String gstin, final String returnType, final String retPeriod, 
			final String userId, final Object offsetLiability) throws Exception {
		logger.debug(CLASSNAME + "offsetLiability : Begin");
		Long startTime = System.currentTimeMillis();
		String requestUrl=String.format("%s/%s", iHubUrl, "/"+returnType.toLowerCase()+"/retoffset");
		
		try {
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			
			gstnHeader.put("gstin", gstin);
			gstnHeader.put("ret_period", retPeriod);
			logger.debug(CLASSNAME + "offsetLiability : gstnHeader {}", gstnHeader);
			RequestConfig config = RequestConfig.custom()
					  .setConnectTimeout(connectionTimeout)
					  .setConnectionRequestTimeout(connectionTimeout)
					  .setSocketTimeout(requestTimeout).build();
			CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
			String postUrl = requestUrl+"?email="+iHubEmail;
			HttpPut httppost = new HttpPut(postUrl);
			
			httppost.setHeader("state_cd", gstnHeader.get("state_cd"));
			httppost.setHeader("gstin", client.getGstnnumber());
			httppost.setHeader("ret_period", retPeriod);
			httppost.setHeader("gst_username", client.getGstname());
			httppost.setHeader("client_id", iHubClientId);
			httppost.setHeader("client_secret", iHubClientSecret);
			httppost.setHeader("ip_address", gstnHeader.get("ip_address"));
			httppost.setHeader("txn", gstnHeader.get("txn"));
			
			mapper.setSerializationInclusion(Include.NON_EMPTY);
			String strContent = mapper.writeValueAsString(offsetLiability);
			
			StringEntity comment = new StringEntity(strContent, ContentType.APPLICATION_JSON);
			httppost.setEntity(comment);

			org.apache.http.HttpResponse httpResponse = httpclient.execute(httppost);

			HttpEntity resEntity = httpResponse.getEntity();
			logger.debug(CLASSNAME + "offsetLiability : response {}", httpResponse.toString());
			String content = EntityUtils.toString(resEntity);
			logger.debug(CLASSNAME + "offsetLiability : content {}", content);
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "{} offsetLiability : Duration {}", returnType, (endTime - startTime));
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Response response = mapper.readValue(content, Response.class);
			httpclient.close();
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "offsetLiability : ERROR", e);
		}
		logger.debug(CLASSNAME + "offsetLiability : End");
		return new Response();
	}
	
	public Response returnSubmit(final Client client, final String gstin, final String returnType, final String retPeriod, 
			final String userId, final boolean initial,final boolean latestsummary) throws Exception {
		logger.debug(CLASSNAME + "returnSubmit : Begin");
		Long startTime = System.currentTimeMillis();
		String requestUrl=String.format("%s/%s", iHubUrl, "/"+returnType.toLowerCase()+"/retsubmit");
		
		try {
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			
			gstnHeader.put("gstin", gstin);
			gstnHeader.put("ret_period", retPeriod);
			logger.debug(CLASSNAME + "returnSubmit : gstnHeader {}", gstnHeader);
			RequestConfig config = RequestConfig.custom()
					  .setConnectTimeout(connectionTimeout)
					  .setConnectionRequestTimeout(connectionTimeout)
					  .setSocketTimeout(requestTimeout).build();
			CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
			String postUrl = requestUrl+"?email="+iHubEmail;
			HttpPost httppost = new HttpPost(postUrl);
			
			httppost.setHeader("state_cd", gstnHeader.get("state_cd"));
			httppost.setHeader("gstin", client.getGstnnumber());
			httppost.setHeader("ret_period", retPeriod);
			httppost.setHeader("gst_username", client.getGstname());
			httppost.setHeader("client_id", iHubClientId);
			httppost.setHeader("client_secret", iHubClientSecret);
			httppost.setHeader("ip_address", gstnHeader.get("ip_address"));
			httppost.setHeader("txn", gstnHeader.get("txn"));
			
			ResponseData submitBody = new ResponseData();
			submitBody.setGstin(gstin);
			submitBody.setRetPeriod(retPeriod);
			if(latestsummary) {
				submitBody.setGenerateSummary("Y");
			}
			mapper.setSerializationInclusion(Include.NON_EMPTY);
			String strContent = mapper.writeValueAsString(submitBody);
			logger.debug(CLASSNAME + "returnSubmit : strContent {}", strContent);
			
			StringEntity comment = new StringEntity(strContent, ContentType.APPLICATION_JSON);
			httppost.setEntity(comment);

			org.apache.http.HttpResponse httpResponse = httpclient.execute(httppost);

			HttpEntity resEntity = httpResponse.getEntity();
			InputStream isContent = resEntity.getContent();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Response response = mapper.readValue(isContent, Response.class);
			logger.debug(CLASSNAME + "returnSubmit : response {}", response);
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "{} returnSubmit : Duration {}", returnType, (endTime - startTime));
			httpclient.close();
			if(!latestsummary) {
				if(NullUtil.isNotEmpty(response) && NullUtil.isNotEmpty(response.getStatuscd()) 
						&& (response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)
								|| (NullUtil.isNotEmpty(response.getStatusdesc()) && response.getStatusdesc().equals("You already have Submitted/Filed For Current Return Period"))
								|| (NullUtil.isNotEmpty(response.getError()) && NullUtil.isNotEmpty(response.getError().getMessage()) && response.getError().getMessage().equals("You already have Submitted/Filed For Current Return Period")))) {
					ClientStatus clientStatus = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, retPeriod);
					if("Quarterly".equals(client.getFilingOption()) && !returnType.equals(MasterGSTConstants.GSTR3B)){
						if("03".equals(retPeriod.substring(0, 2)) || "02".equals(retPeriod.substring(0, 2)) || "01".equals(retPeriod.substring(0, 2))){
							ClientStatus clientStatusqtr13 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "03"+retPeriod.substring(2));
							ClientStatus clientStatusqtr12 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "02"+retPeriod.substring(2));
							ClientStatus clientStatusqtr11 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "01"+retPeriod.substring(2));
							if(NullUtil.isEmpty(clientStatusqtr13)) {
								clientStatusqtr13 = new ClientStatus();
								clientStatusqtr13.setClientId(client.getId().toString());
								clientStatusqtr13.setReturnType(returnType);
								clientStatusqtr13.setReturnPeriod("03"+retPeriod.substring(2));
								clientStatusqtr13.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							} else {
								clientStatusqtr13.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							}
							clientStatusqtr13.setDof(new Date());
							clientStatusRepository.save(clientStatusqtr13);
							if(NullUtil.isEmpty(clientStatusqtr12)) {
								clientStatusqtr12 = new ClientStatus();
								clientStatusqtr12.setClientId(client.getId().toString());
								clientStatusqtr12.setReturnType(returnType);
								clientStatusqtr12.setReturnPeriod("02"+retPeriod.substring(2));
								clientStatusqtr12.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							} else {
								clientStatusqtr12.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							}
							clientStatusqtr12.setDof(new Date());
							clientStatusRepository.save(clientStatusqtr12);
							if(NullUtil.isEmpty(clientStatusqtr11)) {
								clientStatusqtr11 = new ClientStatus();
								clientStatusqtr11.setClientId(client.getId().toString());
								clientStatusqtr11.setReturnType(returnType);
								clientStatusqtr11.setReturnPeriod("01"+retPeriod.substring(2));
								clientStatusqtr11.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							} else {
								clientStatusqtr11.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							}
							clientStatusqtr11.setDof(new Date());
							clientStatusRepository.save(clientStatusqtr11);
						}else if("06".equals(retPeriod.substring(0, 2)) || "05".equals(retPeriod.substring(0,2)) || "04".equals(retPeriod.substring(0,2))){
							ClientStatus clientStatusqtr23 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "06"+retPeriod.substring(2));
							ClientStatus clientStatusqtr22 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "05"+retPeriod.substring(2));
							ClientStatus clientStatusqtr21 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "04"+retPeriod.substring(2));
							if(NullUtil.isEmpty(clientStatusqtr23)) {
								clientStatusqtr23 = new ClientStatus();
								clientStatusqtr23.setClientId(client.getId().toString());
								clientStatusqtr23.setReturnType(returnType);
								clientStatusqtr23.setReturnPeriod("06"+retPeriod.substring(2));
								clientStatusqtr23.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							} else {
								clientStatusqtr23.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							}
							clientStatusqtr23.setDof(new Date());
							clientStatusRepository.save(clientStatusqtr23);
							if(NullUtil.isEmpty(clientStatusqtr22)) {
								clientStatusqtr22 = new ClientStatus();
								clientStatusqtr22.setClientId(client.getId().toString());
								clientStatusqtr22.setReturnType(returnType);
								clientStatusqtr22.setReturnPeriod("05"+retPeriod.substring(2));
								clientStatusqtr22.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							} else {
								clientStatusqtr22.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							}
							clientStatusqtr22.setDof(new Date());
							clientStatusRepository.save(clientStatusqtr22);
							if(NullUtil.isEmpty(clientStatusqtr21)) {
								clientStatusqtr21 = new ClientStatus();
								clientStatusqtr21.setClientId(client.getId().toString());
								clientStatusqtr21.setReturnType(returnType);
								clientStatusqtr21.setReturnPeriod("04"+retPeriod.substring(2));
								clientStatusqtr21.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							} else {
								clientStatusqtr21.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							}
							clientStatusqtr21.setDof(new Date());
							clientStatusRepository.save(clientStatusqtr21);
						}else if("09".equals(retPeriod.substring(0, 2)) || "08".equals(retPeriod.substring(0,2)) || "07".equals(retPeriod.substring(0,2))){
							ClientStatus clientStatusqtr33 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "09"+retPeriod.substring(2));
							ClientStatus clientStatusqtr32 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "08"+retPeriod.substring(2));
							ClientStatus clientStatusqtr31 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "07"+retPeriod.substring(2));
							if(NullUtil.isEmpty(clientStatusqtr33)) {
								clientStatusqtr33 = new ClientStatus();
								clientStatusqtr33.setClientId(client.getId().toString());
								clientStatusqtr33.setReturnType(returnType);
								clientStatusqtr33.setReturnPeriod("09"+retPeriod.substring(2));
								clientStatusqtr33.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							} else {
								clientStatusqtr33.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							}
							clientStatusqtr33.setDof(new Date());
							clientStatusRepository.save(clientStatusqtr33);
							if(NullUtil.isEmpty(clientStatusqtr32)) {
								clientStatusqtr32 = new ClientStatus();
								clientStatusqtr32.setClientId(client.getId().toString());
								clientStatusqtr32.setReturnType(returnType);
								clientStatusqtr32.setReturnPeriod("08"+retPeriod.substring(2));
								clientStatusqtr32.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							} else {
								clientStatusqtr32.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							}
							clientStatusqtr32.setDof(new Date());
							clientStatusRepository.save(clientStatusqtr32);
							if(NullUtil.isEmpty(clientStatusqtr31)) {
								clientStatusqtr31 = new ClientStatus();
								clientStatusqtr31.setClientId(client.getId().toString());
								clientStatusqtr31.setReturnType(returnType);
								clientStatusqtr31.setReturnPeriod("07"+retPeriod.substring(2));
								clientStatusqtr31.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							} else {
								clientStatusqtr31.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							}
							clientStatusqtr31.setDof(new Date());
							clientStatusRepository.save(clientStatusqtr31);
						}else if("12".equals(retPeriod.substring(0, 2)) || "11".equals(retPeriod.substring(0, 2)) || "10".equals(retPeriod.substring(0, 2))){
							ClientStatus clientStatusqtr43 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "12"+retPeriod.substring(2));
							ClientStatus clientStatusqtr42 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "11"+retPeriod.substring(2));
							ClientStatus clientStatusqtr41 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "10"+retPeriod.substring(2));
							if(NullUtil.isEmpty(clientStatusqtr43)) {
								clientStatusqtr43 = new ClientStatus();
								clientStatusqtr43.setClientId(client.getId().toString());
								clientStatusqtr43.setReturnType(returnType);
								clientStatusqtr43.setReturnPeriod("12"+retPeriod.substring(2));
								clientStatusqtr43.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							} else {
								clientStatusqtr43.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							}
							clientStatusqtr43.setDof(new Date());
							clientStatusRepository.save(clientStatusqtr43);
							if(NullUtil.isEmpty(clientStatusqtr42)) {
								clientStatusqtr42 = new ClientStatus();
								clientStatusqtr42.setClientId(client.getId().toString());
								clientStatusqtr42.setReturnType(returnType);
								clientStatusqtr42.setReturnPeriod("11"+retPeriod.substring(2));
								clientStatusqtr42.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							} else {
								clientStatusqtr42.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							}
							clientStatusqtr42.setDof(new Date());
							clientStatusRepository.save(clientStatusqtr42);
							if(NullUtil.isEmpty(clientStatusqtr41)) {
								clientStatusqtr41 = new ClientStatus();
								clientStatusqtr41.setClientId(client.getId().toString());
								clientStatusqtr41.setReturnType(returnType);
								clientStatusqtr41.setReturnPeriod("10"+retPeriod.substring(2));
								clientStatusqtr41.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							} else {
								clientStatusqtr41.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
							}
							clientStatusqtr41.setDof(new Date());
							clientStatusRepository.save(clientStatusqtr41);
						}
					}else{
						if(NullUtil.isEmpty(clientStatus)) {
							clientStatus = new ClientStatus();
							clientStatus.setClientId(client.getId().toString());
							clientStatus.setReturnType(returnType);
							clientStatus.setReturnPeriod(retPeriod);
							clientStatus.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
						} else {
							clientStatus.setStatus(MasterGSTConstants.STATUS_SUBMITTED);
						}
						clientStatus.setDof(new Date());
						clientStatusRepository.save(clientStatus);
					}
				}
			}
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnSubmit : ERROR", e);
		}
		logger.debug(CLASSNAME + "returnSubmit : End");
		return new Response();
	}
	
	public Response returnOTPForEVC(final Client client, final String gstin, final String returnType,  
			final String userId, final boolean initial) {
		logger.debug(CLASSNAME + "returnOTPForEVC : Begin");
		Long startTime = System.currentTimeMillis();
		String url = "/authentication/otpforevc";
		String requestUrl = String.format("%s/%s", iHubUrl, url);
		
		try {
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			
			logger.debug(CLASSNAME + "returnOTPForEVC : gstnHeader {}", gstnHeader);
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("gstin", gstin);
			String pan = client.getSignatoryPAN();
			if(NullUtil.isEmpty(pan)) {
				pan = client.getPannumber();
			}
			queryStringMap.put("pan", pan);
			queryStringMap.put("form_type", returnType.replace("GST",""));
			logger.info(CLASSNAME + "returnOTPForEVC : Response Header {}", queryStringMap);
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "returnOTPForEVC : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "returnOTPForEVC : Response getStatuscd {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "{} returnOTPForEVC : Duration {}", returnType, (endTime - startTime));
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnOTPForEVC : ERROR", e);
		}
		logger.debug(CLASSNAME + "returnOTPForEVC : End");
		return new Response();
	}
	
	public Response proceedToFile(final Client client, final String gstin, final String retPeriod) {
		logger.debug(CLASSNAME + "proceedToFile : Begin");
		Long startTime = System.currentTimeMillis();
		String url = "/all/proceedfile";
		String requestUrl = String.format("%s/%s", iHubUrl, url);
		
		try {
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			
			logger.debug(CLASSNAME + "proceedToFile : gstnHeader {}", gstnHeader);
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("retperiod", retPeriod);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "proceedToFile : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "proceedToFile : Response getStatuscd {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "proceedToFile : Duration {}", (endTime - startTime));
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "proceedToFile : ERROR", e);
		}
		logger.debug(CLASSNAME + "proceedToFile : End");
		return new Response();
	}
	
	public Response returnFile(final Client client, final String retPeriod, final String userId, 
			String signData, final String returnType) {
		logger.debug(CLASSNAME + "returnFile : Begin");
		Long startTime = System.currentTimeMillis();
		String url = "/gstr1/retfile";
		if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR3B)) {
			url = "/gstr3b/retfile";
		} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR4)) {
			url = "/gstr4/retfile";
		} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR6)) {
			url = "/gstr6/retfile";
		} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR1A)) {
			url = "/gstr1a/retfile";
		}
		String requestUrl = String.format("%s/%s", iHubUrl, url);
		
		try {
			if(NullUtil.isNotEmpty(client)) {
				String stateCd = "";
				boolean include = true;
				for(StateConfig stateConfig : configService.getStates()) {
					if(stateConfig.getCode().equals(client.getStatename())) {
						stateCd = stateConfig.getTin()+"";
						include = false;
						break;
					} else if(stateConfig.getName().equals(client.getStatename())) {
						stateCd = stateConfig.getTin()+"";
						include = false;
						break;
					} else if(stateConfig.getName().equals(stateConfig.getTin()+"-"+client.getStatename())) {
						stateCd = stateConfig.getTin()+"";
						include = false;
						break;
					}
				}
				if(include) {
					stateCd = client.getStatename();
					if (stateCd.contains("-")) {
						stateCd = stateCd.substring(0, stateCd.indexOf("-")).trim();
					}
				}
				String ipaddr = InetAddress.getLocalHost().getHostAddress();
				HeaderKeys headerKeys = headerKeysRepository.findByGstusernameAndStatecdAndEmailAndIpusr(client.getGstname(), 
						stateCd, iHubEmail, ipaddr);
				if(NullUtil.isEmpty(headerKeys)) {
					headerKeys = headerKeysRepository.findByGstusernameAndStatecdAndEmail(client.getGstname(), stateCd, iHubEmail);
				}
				if(NullUtil.isNotEmpty(headerKeys)) {
					logger.debug(CLASSNAME + "returnFile : signData {}", signData);
					ipaddr = headerKeys.getIpusr();
					String pan = client.getPannumber();
					if(NullUtil.isNotEmpty(client.getSignatoryPAN())) {
						pan = client.getSignatoryPAN();
					}
					String postUrl = requestUrl+"?email="+iHubEmail+"&pan="+pan;
					RequestConfig config = RequestConfig.custom()
							  .setConnectTimeout(connectionTimeout)
							  .setConnectionRequestTimeout(connectionTimeout)
							  .setSocketTimeout(requestTimeout).build();
					CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
					HttpPost httppost = new HttpPost(postUrl);
					
					httppost.setHeader("state_cd", stateCd);
					httppost.setHeader("gstin", client.getGstnnumber());
					httppost.setHeader("ret_period", retPeriod);
					httppost.setHeader("gst_username", client.getGstname());
					httppost.setHeader("client_id", iHubClientId);
					httppost.setHeader("client_secret", iHubClientSecret);
					httppost.setHeader("ip_address", ipaddr);
					httppost.setHeader("content-type", "application/json");
					httppost.setHeader("txn", headerKeys.getTxn());
					
					StringEntity comment = new StringEntity(signData, ContentType.APPLICATION_JSON);
					httppost.setEntity(comment);

					org.apache.http.HttpResponse httpResponse = httpclient.execute(httppost);

					HttpEntity resEntity = httpResponse.getEntity();
					InputStream isContent = resEntity.getContent();
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					Response response = mapper.readValue(isContent, Response.class);
					logger.debug(CLASSNAME + "returnFile : response {}", response);
					Long endTime = System.currentTimeMillis();
					logger.debug(CLASSNAME + "{} returnFile : Duration {}", returnType, (endTime - startTime));
					httpclient.close();
					if(NullUtil.isNotEmpty(response) && NullUtil.isNotEmpty(response.getStatuscd()) 
							&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
						ClientStatus clientStatus = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, retPeriod);
						if("Quarterly".equals(client.getFilingOption()) && !returnType.equals(MasterGSTConstants.GSTR3B)){
							if("03".equals(retPeriod.substring(0, 2)) || "02".equals(retPeriod.substring(0, 2)) || "01".equals(retPeriod.substring(0, 2))){
								ClientStatus clientStatusqtr13 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "03"+retPeriod.substring(2));
								ClientStatus clientStatusqtr12 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "02"+retPeriod.substring(2));
								ClientStatus clientStatusqtr11 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "01"+retPeriod.substring(2));
								if(NullUtil.isEmpty(clientStatusqtr13)) {
									clientStatusqtr13 = new ClientStatus();
									clientStatusqtr13.setClientId(client.getId().toString());
									clientStatusqtr13.setReturnType(returnType);
									clientStatusqtr13.setReturnPeriod("03"+retPeriod.substring(2));
									clientStatusqtr13.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr13.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr13.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr13);
								if(NullUtil.isEmpty(clientStatusqtr12)) {
									clientStatusqtr12 = new ClientStatus();
									clientStatusqtr12.setClientId(client.getId().toString());
									clientStatusqtr12.setReturnType(returnType);
									clientStatusqtr12.setReturnPeriod("02"+retPeriod.substring(2));
									clientStatusqtr12.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr12.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr12.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr12);
								if(NullUtil.isEmpty(clientStatusqtr11)) {
									clientStatusqtr11 = new ClientStatus();
									clientStatusqtr11.setClientId(client.getId().toString());
									clientStatusqtr11.setReturnType(returnType);
									clientStatusqtr11.setReturnPeriod("01"+retPeriod.substring(2));
									clientStatusqtr11.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr11.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr11.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr11);
							}else if("06".equals(retPeriod.substring(0, 2)) || "05".equals(retPeriod.substring(0, 2)) || "04".equals(retPeriod.substring(0, 2))){
								ClientStatus clientStatusqtr23 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "06"+retPeriod.substring(2));
								ClientStatus clientStatusqtr22 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "05"+retPeriod.substring(2));
								ClientStatus clientStatusqtr21 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "04"+retPeriod.substring(2));
								if(NullUtil.isEmpty(clientStatusqtr23)) {
									clientStatusqtr23 = new ClientStatus();
									clientStatusqtr23.setClientId(client.getId().toString());
									clientStatusqtr23.setReturnType(returnType);
									clientStatusqtr23.setReturnPeriod("06"+retPeriod.substring(2));
									clientStatusqtr23.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr23.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr23.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr23);
								if(NullUtil.isEmpty(clientStatusqtr22)) {
									clientStatusqtr22 = new ClientStatus();
									clientStatusqtr22.setClientId(client.getId().toString());
									clientStatusqtr22.setReturnType(returnType);
									clientStatusqtr22.setReturnPeriod("05"+retPeriod.substring(2));
									clientStatusqtr22.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr22.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr22.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr22);
								if(NullUtil.isEmpty(clientStatusqtr21)) {
									clientStatusqtr21 = new ClientStatus();
									clientStatusqtr21.setClientId(client.getId().toString());
									clientStatusqtr21.setReturnType(returnType);
									clientStatusqtr21.setReturnPeriod("04"+retPeriod.substring(2));
									clientStatusqtr21.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr21.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr21.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr21);
							}else if("09".equals(retPeriod.substring(0, 2)) || "08".equals(retPeriod.substring(0, 2)) || "07".equals(retPeriod.substring(0, 2))){
								ClientStatus clientStatusqtr33 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "09"+retPeriod.substring(2));
								ClientStatus clientStatusqtr32 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "08"+retPeriod.substring(2));
								ClientStatus clientStatusqtr31 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "07"+retPeriod.substring(2));
								if(NullUtil.isEmpty(clientStatusqtr33)) {
									clientStatusqtr33 = new ClientStatus();
									clientStatusqtr33.setClientId(client.getId().toString());
									clientStatusqtr33.setReturnType(returnType);
									clientStatusqtr33.setReturnPeriod("09"+retPeriod.substring(2));
									clientStatusqtr33.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr33.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr33.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr33);
								if(NullUtil.isEmpty(clientStatusqtr32)) {
									clientStatusqtr32 = new ClientStatus();
									clientStatusqtr32.setClientId(client.getId().toString());
									clientStatusqtr32.setReturnType(returnType);
									clientStatusqtr32.setReturnPeriod("08"+retPeriod.substring(2));
									clientStatusqtr32.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr32.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr32.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr32);
								if(NullUtil.isEmpty(clientStatusqtr31)) {
									clientStatusqtr31 = new ClientStatus();
									clientStatusqtr31.setClientId(client.getId().toString());
									clientStatusqtr31.setReturnType(returnType);
									clientStatusqtr31.setReturnPeriod("07"+retPeriod.substring(2));
									clientStatusqtr31.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr31.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr31.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr31);
							}else if("12".equals(retPeriod.substring(0, 2)) || "11".equals(retPeriod.substring(0, 2)) || "10".equals(retPeriod.substring(0, 2))){
								ClientStatus clientStatusqtr43 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "12"+retPeriod.substring(2));
								ClientStatus clientStatusqtr42 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "11"+retPeriod.substring(2));
								ClientStatus clientStatusqtr41 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "10"+retPeriod.substring(2));
								if(NullUtil.isEmpty(clientStatusqtr43)) {
									clientStatusqtr43 = new ClientStatus();
									clientStatusqtr43.setClientId(client.getId().toString());
									clientStatusqtr43.setReturnType(returnType);
									clientStatusqtr43.setReturnPeriod("12"+retPeriod.substring(2));
									clientStatusqtr43.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr43.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr43.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr43);
								if(NullUtil.isEmpty(clientStatusqtr42)) {
									clientStatusqtr42 = new ClientStatus();
									clientStatusqtr42.setClientId(client.getId().toString());
									clientStatusqtr42.setReturnType(returnType);
									clientStatusqtr42.setReturnPeriod("11"+retPeriod.substring(2));
									clientStatusqtr42.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr42.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr42.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr42);
								if(NullUtil.isEmpty(clientStatusqtr41)) {
									clientStatusqtr41 = new ClientStatus();
									clientStatusqtr41.setClientId(client.getId().toString());
									clientStatusqtr41.setReturnType(returnType);
									clientStatusqtr41.setReturnPeriod("10"+retPeriod.substring(2));
									clientStatusqtr41.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr41.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr41.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr41);
							}
						}else{
							if(NullUtil.isEmpty(clientStatus)) {
								clientStatus = new ClientStatus();
								clientStatus.setClientId(client.getId().toString());
								clientStatus.setReturnType(returnType);
								clientStatus.setReturnPeriod(retPeriod);
								clientStatus.setStatus(MasterGSTConstants.STATUS_FILED);
							} else {
								clientStatus.setStatus(MasterGSTConstants.STATUS_FILED);
							}
							clientStatus.setDof(new Date());
							clientStatusRepository.save(clientStatus);
						}
					}
					return response;
				}
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnFile : ERROR", e);
		}
		logger.debug(CLASSNAME + "returnFile : End");
		return new Response();
	}
	
	public Response returnFileByEVC(final Client client, final String retPeriod, final String userId, 
			String content, final String evcotp, final String returnType) {
		logger.debug(CLASSNAME + "returnFileByEVC : Begin");
		Long startTime = System.currentTimeMillis();
		String url = "/gstr1/retevcfile";
		if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR2)) {
			url = "/gstr2/retevcfile";
		} else if(NullUtil.isNotEmpty(returnType) && returnType.equals(MasterGSTConstants.GSTR3B)) {
			url = "/gstr3b/retevcfile";
		}
		String requestUrl = String.format("%s/%s", iHubUrl, url);
		
		try {
			if(NullUtil.isNotEmpty(client)) {
				String stateCd = "";
				boolean include = true;
				for(StateConfig stateConfig : configService.getStates()) {
					if(stateConfig.getCode().equals(client.getStatename())) {
						stateCd = stateConfig.getTin()+"";
						include = false;
						break;
					} else if(stateConfig.getName().equals(client.getStatename())) {
						stateCd = stateConfig.getTin()+"";
						include = false;
						break;
					} else if(stateConfig.getName().equals(stateConfig.getTin()+"-"+client.getStatename())) {
						stateCd = stateConfig.getTin()+"";
						include = false;
						break;
					}
				}
				if(include) {
					stateCd = client.getStatename();
					if (stateCd.contains("-")) {
						stateCd = stateCd.substring(0, stateCd.indexOf("-")).trim();
					}
				}
				String ipaddr = InetAddress.getLocalHost().getHostAddress();
				HeaderKeys headerKeys = headerKeysRepository.findByGstusernameAndStatecdAndEmailAndIpusr(client.getGstname(), 
						stateCd, iHubEmail, ipaddr);
				if(NullUtil.isEmpty(headerKeys)) {
					headerKeys = headerKeysRepository.findByGstusernameAndStatecdAndEmail(client.getGstname(), stateCd, iHubEmail);
				}
				if(NullUtil.isNotEmpty(headerKeys)) {
					logger.debug(CLASSNAME + "returnFileByEVC : content {}", content);
					ipaddr = headerKeys.getIpusr();
					String pan = client.getPannumber();
					if(NullUtil.isNotEmpty(client.getSignatoryPAN())) {
						pan = client.getSignatoryPAN();
					}
					String postUrl = requestUrl+"?email="+iHubEmail+"&pan="+pan+"&evcotp="+evcotp;
					RequestConfig config = RequestConfig.custom()
							  .setConnectTimeout(connectionTimeout)
							  .setConnectionRequestTimeout(connectionTimeout)
							  .setSocketTimeout(requestTimeout).build();
					CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
					HttpPost httppost = new HttpPost(postUrl);
					
					httppost.setHeader("state_cd", stateCd);
					httppost.setHeader("gstin", client.getGstnnumber());
					httppost.setHeader("ret_period", retPeriod);
					httppost.setHeader("gst_username", client.getGstname());
					httppost.setHeader("client_id", iHubClientId);
					httppost.setHeader("client_secret", iHubClientSecret);
					httppost.setHeader("ip_address", ipaddr);
					httppost.setHeader("content-type", "application/json");
					httppost.setHeader("txn", headerKeys.getTxn());
					
					StringEntity comment = new StringEntity(content, ContentType.APPLICATION_JSON);
					httppost.setEntity(comment);

					org.apache.http.HttpResponse httpResponse = httpclient.execute(httppost);

					HttpEntity resEntity = httpResponse.getEntity();
					InputStream isContent = resEntity.getContent();
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					Response response = mapper.readValue(isContent, Response.class);
					logger.debug(CLASSNAME + "returnFileByEVC : response {}", response);
					Long endTime = System.currentTimeMillis();
					logger.debug(CLASSNAME + "{} returnFileByEVC : Duration {}", returnType, (endTime - startTime));
					httpclient.close();
					if(NullUtil.isNotEmpty(response) && NullUtil.isNotEmpty(response.getStatuscd()) 
							&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
						ClientStatus clientStatus = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, retPeriod);
						if("Quarterly".equals(client.getFilingOption()) && !returnType.equals(MasterGSTConstants.GSTR3B)){
							if("03".equals(retPeriod.substring(0, 2)) || "02".equals(retPeriod.substring(0, 2)) || "01".equals(retPeriod.substring(0, 2))){
								ClientStatus clientStatusqtr13 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "03"+retPeriod.substring(2));
								ClientStatus clientStatusqtr12 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "02"+retPeriod.substring(2));
								ClientStatus clientStatusqtr11 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "01"+retPeriod.substring(2));
								if(NullUtil.isEmpty(clientStatusqtr13)) {
									clientStatusqtr13 = new ClientStatus();
									clientStatusqtr13.setClientId(client.getId().toString());
									clientStatusqtr13.setReturnType(returnType);
									clientStatusqtr13.setReturnPeriod("03"+retPeriod.substring(2));
									clientStatusqtr13.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr13.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr13.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr13);
								if(NullUtil.isEmpty(clientStatusqtr12)) {
									clientStatusqtr12 = new ClientStatus();
									clientStatusqtr12.setClientId(client.getId().toString());
									clientStatusqtr12.setReturnType(returnType);
									clientStatusqtr12.setReturnPeriod("02"+retPeriod.substring(2));
									clientStatusqtr12.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr12.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr12.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr12);
								if(NullUtil.isEmpty(clientStatusqtr11)) {
									clientStatusqtr11 = new ClientStatus();
									clientStatusqtr11.setClientId(client.getId().toString());
									clientStatusqtr11.setReturnType(returnType);
									clientStatusqtr11.setReturnPeriod("01"+retPeriod.substring(2));
									clientStatusqtr11.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr11.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr11.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr11);
							}else if("06".equals(retPeriod.substring(0, 2)) || "05".equals(retPeriod.substring(0, 2)) || "04".equals(retPeriod.substring(0, 2))){
								ClientStatus clientStatusqtr23 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "06"+retPeriod.substring(2));
								ClientStatus clientStatusqtr22 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "05"+retPeriod.substring(2));
								ClientStatus clientStatusqtr21 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "04"+retPeriod.substring(2));
								if(NullUtil.isEmpty(clientStatusqtr23)) {
									clientStatusqtr23 = new ClientStatus();
									clientStatusqtr23.setClientId(client.getId().toString());
									clientStatusqtr23.setReturnType(returnType);
									clientStatusqtr23.setReturnPeriod("06"+retPeriod.substring(2));
									clientStatusqtr23.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr23.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr23.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr23);
								if(NullUtil.isEmpty(clientStatusqtr22)) {
									clientStatusqtr22 = new ClientStatus();
									clientStatusqtr22.setClientId(client.getId().toString());
									clientStatusqtr22.setReturnType(returnType);
									clientStatusqtr22.setReturnPeriod("05"+retPeriod.substring(2));
									clientStatusqtr22.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr22.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr22.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr22);
								if(NullUtil.isEmpty(clientStatusqtr21)) {
									clientStatusqtr21 = new ClientStatus();
									clientStatusqtr21.setClientId(client.getId().toString());
									clientStatusqtr21.setReturnType(returnType);
									clientStatusqtr21.setReturnPeriod("04"+retPeriod.substring(2));
									clientStatusqtr21.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr21.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr21.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr21);
							}else if("09".equals(retPeriod.substring(0, 2)) || "08".equals(retPeriod.substring(0, 2)) || "07".equals(retPeriod.substring(0, 2))){
								ClientStatus clientStatusqtr33 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "09"+retPeriod.substring(2));
								ClientStatus clientStatusqtr32 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "08"+retPeriod.substring(2));
								ClientStatus clientStatusqtr31 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "07"+retPeriod.substring(2));
								if(NullUtil.isEmpty(clientStatusqtr33)) {
									clientStatusqtr33 = new ClientStatus();
									clientStatusqtr33.setClientId(client.getId().toString());
									clientStatusqtr33.setReturnType(returnType);
									clientStatusqtr33.setReturnPeriod("09"+retPeriod.substring(2));
									clientStatusqtr33.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr33.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr33.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr33);
								if(NullUtil.isEmpty(clientStatusqtr32)) {
									clientStatusqtr32 = new ClientStatus();
									clientStatusqtr32.setClientId(client.getId().toString());
									clientStatusqtr32.setReturnType(returnType);
									clientStatusqtr32.setReturnPeriod("08"+retPeriod.substring(2));
									clientStatusqtr32.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr32.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr32.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr32);
								if(NullUtil.isEmpty(clientStatusqtr31)) {
									clientStatusqtr31 = new ClientStatus();
									clientStatusqtr31.setClientId(client.getId().toString());
									clientStatusqtr31.setReturnType(returnType);
									clientStatusqtr31.setReturnPeriod("07"+retPeriod.substring(2));
									clientStatusqtr31.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr31.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr31.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr31);
							}else if("12".equals(retPeriod.substring(0, 2)) || "11".equals(retPeriod.substring(0, 2)) || "10".equals(retPeriod.substring(0, 2))){
								ClientStatus clientStatusqtr43 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "12"+retPeriod.substring(2));
								ClientStatus clientStatusqtr42 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "11"+retPeriod.substring(2));
								ClientStatus clientStatusqtr41 = clientStatusRepository.findByClientIdAndReturnTypeAndReturnPeriod(client.getId().toString(), returnType, "10"+retPeriod.substring(2));
								if(NullUtil.isEmpty(clientStatusqtr43)) {
									clientStatusqtr43 = new ClientStatus();
									clientStatusqtr43.setClientId(client.getId().toString());
									clientStatusqtr43.setReturnType(returnType);
									clientStatusqtr43.setReturnPeriod("12"+retPeriod.substring(2));
									clientStatusqtr43.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr43.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr43.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr43);
								if(NullUtil.isEmpty(clientStatusqtr42)) {
									clientStatusqtr42 = new ClientStatus();
									clientStatusqtr42.setClientId(client.getId().toString());
									clientStatusqtr42.setReturnType(returnType);
									clientStatusqtr42.setReturnPeriod("11"+retPeriod.substring(2));
									clientStatusqtr42.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr42.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr42.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr42);
								if(NullUtil.isEmpty(clientStatusqtr41)) {
									clientStatusqtr41 = new ClientStatus();
									clientStatusqtr41.setClientId(client.getId().toString());
									clientStatusqtr41.setReturnType(returnType);
									clientStatusqtr41.setReturnPeriod("10"+retPeriod.substring(2));
									clientStatusqtr41.setStatus(MasterGSTConstants.STATUS_FILED);
								} else {
									clientStatusqtr41.setStatus(MasterGSTConstants.STATUS_FILED);
								}
								clientStatusqtr41.setDof(new Date());
								clientStatusRepository.save(clientStatusqtr41);
							}
						}else{
							if(NullUtil.isEmpty(clientStatus)) {
								clientStatus = new ClientStatus();
								clientStatus.setClientId(client.getId().toString());
								clientStatus.setReturnType(returnType);
								clientStatus.setReturnPeriod(retPeriod);
								clientStatus.setStatus(MasterGSTConstants.STATUS_FILED);
							} else {
								clientStatus.setStatus(MasterGSTConstants.STATUS_FILED);
							}
							clientStatus.setDof(new Date());
							clientStatusRepository.save(clientStatus);
						}
					}
					return response;
				}
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnFileByEVC : ERROR", e);
		}
		logger.debug(CLASSNAME + "returnFileByEVC : End");
		return new Response();
	}
	
	public DigioResponse signDocument(final String uploadData, final String clientId, final boolean isDSC) {
		logger.debug(CLASSNAME + "signDocument : Begin");
		Long startTime = System.currentTimeMillis();
		DigioResponse response = new DigioResponse();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Client client = clientService.findById(clientId);
			String auth = "Basic " + Base64.encodeBase64String((digioClientId + ":" + digioClientSecret).getBytes("utf-8"));
			DigioRequest digioRequest = new DigioRequest();
			digioRequest.setSigner(client.getEmail());
			digioRequest.setContent(uploadData);
			digioRequest.setExpire_in_days(10);
			digioRequest.setComment("Sample");
			digioRequest.setFormat("json");
			digioRequest.setNotify_signers(true);
			// digioRequest.setCallback(randomNumber(4));
			if(isDSC) {
				digioRequest.setSignature_type("dsc");
			}
			mapper.setSerializationInclusion(Include.NON_NULL);
			String strDigioRequest = mapper.writeValueAsString(digioRequest);
			
			HttpResponse<JsonNode> httpResponse = Unirest.post(digioUrl+"upload")
					.header("Authorization", auth).header("Content-Type", "application/json")
					.body(new JsonNode(strDigioRequest)).asJson();

			logger.debug(CLASSNAME + "signDocument:: httpResponse.getStatus()\t" + httpResponse.getStatus());
			logger.debug(CLASSNAME + "signDocument:: Begin httpResponse.getBody()\t" + httpResponse.getBody());
			response.setData(httpResponse.getBody().getObject().toString());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "signDocument : Duration {}", (endTime - startTime));
			logger.debug(CLASSNAME + "signDocument:: data\t" + response.getData());
		} catch(Exception e) {
			logger.error(CLASSNAME + "signDocument : ERROR", e);
		}
		
		logger.debug(CLASSNAME + "signDocument : End");
		return response;
	}
	
	public DigioResponse getSignedDocument(String signid) {
		logger.debug(CLASSNAME + "getSignedDocument : Begin {}", signid);
		Long startTime = System.currentTimeMillis();
		DigioResponse response = new DigioResponse();
		try {
			String auth = "Basic " + Base64.encodeBase64String((digioClientId + ":" + digioClientSecret).getBytes("utf-8"));
			logger.debug(CLASSNAME + "auth\t" + auth);
		
			HttpResponse<JsonNode> httpResponse = Unirest.get(digioUrl+"download")
					.header("Authorization", auth).header("Content-Type", "application/json")
					.queryString("document_id",signid).asJson();

			logger.debug(CLASSNAME + "getSignedDocument:: httpResponse.getStatus() {}", httpResponse.getStatus());
			logger.debug(CLASSNAME + "getSignedDocument:: Begin httpResponse.getBody() {}", httpResponse.getBody());
			response.setData(httpResponse.getBody().getObject().toString());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "getSignedDocument : Duration {}", (endTime - startTime));
			logger.debug(CLASSNAME + "getSignedDocument:: data {}", response.getData());
		} catch (Exception e) {
			logger.error(CLASSNAME + "getSignedDocument : ERROR", e);
		}
		return response;
	}
	
	public Response publicSearch(final String gstin) {
		logger.debug(CLASSNAME + "publicSearch : Begin");
		Long startTime = System.currentTimeMillis();
		Map<String, String> gstnHeader = Maps.newHashMap();
		gstnHeader.put("client_id", iHubClientId);
		gstnHeader.put("client_secret", iHubClientSecret);
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("gstin", gstin);
		queryStringMap.put("email", iHubEmail);
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/public/search");
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "publicSearch : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "publicSearch : Response {}", response);
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "publicSearch : Duration {}", (endTime - startTime));
			
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "publicSearch : ERROR", e);
		}
		logger.debug(CLASSNAME + "publicSearch : End");
		return new Response();
	}
	
	
	public Response authenticateEwayBillApi(final String gstin,final String ipAddress,final String uname,final String pwd) {
		logger.debug(CLASSNAME + "authenticateEwayBillApi : Begin");
		Map<String, String> gstnHeader = Maps.newHashMap();
		gstnHeader.put("client_id", iHubEnoClientId);
		gstnHeader.put("client_secret", iHubEnoClientSecret);
		gstnHeader.put("ip_address", ipAddress);
		gstnHeader.put("gstin", gstin);
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("username",uname);
		queryStringMap.put("password",pwd);
		queryStringMap.put("email", iHubEmail);
		
		String requestUrl = String.format("%s/%s", iHubUrl, "ewaybillapi/v1.03/authenticate");
		//logger.info("requesturl::"+requestUrl);
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug("authenticateEwayBillApi::"+response);
			logger.debug(CLASSNAME + "authenticateEwayBillApi : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "authenticateEwayBillApi : Response {}", response);
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "authenticateEwayBillApi : ERROR", e);
		}
		
		logger.debug(CLASSNAME + "authenticateEwayBillApi : End");
		
		return new Response();
		
		
	}
	
	@Override
	public EwayBillDateResponse getGSTINDetailsByDate(String gstin,String date, final String ipAddress) {
		logger.debug(CLASSNAME + "getGSTINDetailsByDate : Begin");
		Map<String, String> gstnHeader = Maps.newHashMap();
		gstnHeader.put("client_id", iHubEnoClientId);
		gstnHeader.put("client_secret", iHubEnoClientSecret);
		//logger.info("IpAddress--------------------"+ipAddress);
		gstnHeader.put("ip_address", ipAddress);
		gstnHeader.put("gstin", gstin);
		//logger.info("GSTINUMBER-----------------------------"+gstin);
		Map<String, Object> queryStringMap = Maps.newHashMap();
		logger.info("Date--------------------"+date);
		queryStringMap.put("date",date);
		queryStringMap.put("email", iHubEmail);
		
		String requestUrl = String.format("%s/%s", iHubUrl, "ewaybillapi/v1.03/ewayapi/getewaybillsbydate");
		//logger.info("requestUrl--------------------"+requestUrl);
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			logger.info("getGSTINDetailsByDate response body---------------------"+jsonResponse.getBody().toString());
			EwayBillDateResponse response = mapper.readValue(jsonResponse.getBody().toString(), EwayBillDateResponse.class);
			logger.debug("iHubConsumerService getGSTINDetailsByDate response--------------------"+response);
			logger.debug(CLASSNAME + "getGSTINDetailsByDate : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "getGSTINDetailsByDate : Response {}", response);
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getGSTINDetailsByDate : ERROR", e);
		}
		
		logger.debug(CLASSNAME + "getGSTINDetailsByEwayBillno : End");
		return new EwayBillDateResponse();
	}
	
	
	
	@Override
	public EwayBillResponse getGSTINDetailsByEwayBillno(String gstin,String ewbno, final String ipAddress) {
		logger.debug(CLASSNAME + "getGSTINDetailsByEwayBillno : Begin");
		Map<String, String> gstnHeader = Maps.newHashMap();
		gstnHeader.put("client_id", iHubEnoClientId);
		gstnHeader.put("client_secret", iHubEnoClientSecret);
		//logger.info("IpAddress--------------------"+ipAddress);
		gstnHeader.put("ip_address", ipAddress);
		gstnHeader.put("GSTIN", gstin);
		Map<String, Object> queryStringMap = Maps.newHashMap();
		//logger.info("Ewaybillno--------------------"+ewbno);
		queryStringMap.put("ewbNo",ewbno);
		queryStringMap.put("email", iHubEmail);
		
		String requestUrl = String.format("%s/%s", iHubUrl, "ewaybillapi/v1.03/ewayapi/getewaybill");
		logger.debug("requestUrl--------------------"+requestUrl);
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			EwayBillResponse response = mapper.readValue(jsonResponse.getBody().toString(), EwayBillResponse.class);
			logger.debug("getGSTINDetailsByEwayBillno response json body--------------------"+jsonResponse.getBody().toString());
			logger.debug("iHubConsumerService response--------------------"+response);
			logger.debug(CLASSNAME + "getGSTINDetailsByEwayBillno : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "getGSTINDetailsByEwayBillno : Response {}", response);
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getGSTINDetailsByEwayBillno : ERROR", e);
		}
		
		logger.debug(CLASSNAME + "getGSTINDetailsByEwayBillno : End");
		return new EwayBillResponse();
	}
	
		
		public EwayBillCancelResponse cancelEwayBill(String gstin, String ipAddress,CancelEwayBill cancelewaybill) {
			logger.debug(CLASSNAME + "cancelEwayBill : Begin");
			logger.debug("---------iHubConsumerService-------"+cancelewaybill);
			//ObjectMapper mapper = new ObjectMapper();
			Map<String, String> gstnHeader = Maps.newHashMap();
			gstnHeader.put("client_id", iHubEnoClientId);
			gstnHeader.put("client_secret", iHubEnoClientSecret);
			gstnHeader.put("ip_address", ipAddress);
			gstnHeader.put("gstin", gstin);
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("email", iHubEmail);
			logger.debug(cancelewaybill.getEwbNo()+"-----"+cancelewaybill.getCancelRmrk()+"-----"+cancelewaybill.getCancelRsnCode());
			String requestUrl = String.format("%s/%s", iHubUrl, "ewaybillapi/v1.03/ewayapi/canewb");
			//logger.debug("---requestUrl---"+requestUrl);
			try {
				CancelEwayBill cebill = new CancelEwayBill();
				cebill.setEwbNo(cancelewaybill.getEwbNo());
				cebill.setCancelRmrk(cancelewaybill.getCancelRmrk());
				cebill.setCancelRsnCode(cancelewaybill.getCancelRsnCode());
				
				mapper.setSerializationInclusion(Include.NON_NULL);
				String strCancelRequest = mapper.writeValueAsString(cebill);
				
				HttpResponse<String> httpResponse = Unirest
		                .post(requestUrl)
		                .queryString(queryStringMap)
		                  .header("client_id", iHubEnoClientId)
		                  .header("client_secret", iHubEnoClientSecret)
		                  .header("ip_address",ipAddress)
		                  .header("gstin",gstin)
		                .header("Content-Type", "application/json")
		                .body(strCancelRequest)
		                .asString();
					logger.debug(httpResponse.getBody());
				
				
			/*	HttpResponse<JsonNode> httpResponse = Unirest.post(requestUrl)
						.headers(gstnHeader).queryString(queryStringMap)
						.body(strCancelRequest).asJson();
				*/
			
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				EwayBillCancelResponse response = mapper.readValue(httpResponse.getBody().toString(), EwayBillCancelResponse.class);
				
				
				//logger.info("cancelEwayBill response json body--------------------"+httpResponse.getBody().toString());
				logger.debug("iHubConsumerService response--------------------"+response);
				logger.debug(CLASSNAME + "cancelEwayBill : Response Header {}", response.getHeader());
				logger.debug(CLASSNAME + "cancelEwayBill : Response {}", response);
				return response;
			} catch (Exception e) {
				logger.error(CLASSNAME + "cancelEwayBill : ERROR", e);
			}
			
			logger.debug(CLASSNAME + "cancelEwayBill : End");
			return new EwayBillCancelResponse();
			
		}
	
		
		public EwayBillVehicleUpdateResponse updateVehicle(String gstin,String ewbno,String vehicleType, String ipAddress, EBillVehicleListDetails vehicleDetails) throws ParseException {
			logger.debug(CLASSNAME + "updateVehicle : Begin");
		
			Map<String, String> gstnHeader = Maps.newHashMap();
			gstnHeader.put("client_id", iHubEnoClientId);
			gstnHeader.put("client_secret", iHubEnoClientSecret);
			gstnHeader.put("ip_address", ipAddress);
			gstnHeader.put("gstin", gstin);
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("email", iHubEmail);
		
			Number eBillNumber = NumberFormat.getInstance().parse(ewbno);
		//	logger.info("After ewaybillNo-----"+eBillNumber);
			//logger.info(cancelewaybill.getEwbNo()+"-----"+cancelewaybill.getCancelRmrk()+"-----"+cancelewaybill.getCancelRsnCode());
			String requestUrl = String.format("%s/%s", iHubUrl, "ewaybillapi/v1.03/ewayapi/vehewb");
			logger.info("---requestUrl---"+requestUrl);
			try {
				EBillVehicleListDetails cebill = new EBillVehicleListDetails();
				cebill.setVehicleNo(vehicleDetails.getVehicleNo());
				cebill.setFromState(vehicleDetails.getFromState());
				cebill.setFromPlace(vehicleDetails.getFromPlace());
				if(isNotEmpty(vehicleDetails.getTripshtNo())) {
					cebill.setTripshtNo(vehicleDetails.getTripshtNo());
				}
				if(isNotEmpty(vehicleDetails.getUserGSTINTransin())) {
					cebill.setUserGSTINTransin(vehicleDetails.getUserGSTINTransin());
				}
				if(isNotEmpty(vehicleDetails.getVehicleType())) {
					cebill.setVehicleType(vehicleDetails.getVehicleType());
				}
				cebill.setTransDocNo(vehicleDetails.getTransDocNo());
				cebill.setTransDocDate(vehicleDetails.getTransDocDate());
				//cebill.setEnteredDate(vehicleDetails.getEnteredDate());
				cebill.setTransMode(vehicleDetails.getTransMode());
				cebill.setReasonCode(vehicleDetails.getReasonCode());
				cebill.setReasonRem(vehicleDetails.getReasonRem());
				//cebill.setGroupNo(vehicleDetails.getGroupNo());
				
				mapper.setSerializationInclusion(Include.NON_NULL);
				String strUpdateRequest = mapper.writeValueAsString(cebill);
				
				JsonNode  jsn = new JsonNode(strUpdateRequest);
				jsn.getObject().accumulate("ewbNo", eBillNumber);
				//jsn.getObject().accumulate("vehicleType", vehicleType);
				
				logger.info("----------------JSON BODY------------"+jsn);
				
				HttpResponse<String> httpResponse = Unirest
		                .post(requestUrl)
		                .queryString(queryStringMap)
		                  .header("client_id", iHubEnoClientId)
		                  .header("client_secret", iHubEnoClientSecret)
		                  .header("ip_address",ipAddress)
		                  .header("gstin",gstin)
		                .header("Content-Type", "application/json")
		                .body(jsn)
		                .asString();
			
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				EwayBillVehicleUpdateResponse response = mapper.readValue(httpResponse.getBody().toString(), EwayBillVehicleUpdateResponse.class);
				
				
				//logger.info("cancelEwayBill response json body--------------------"+httpResponse.getBody().toString());
				logger.info("iHubConsumerService response--------------------"+response);
				logger.debug(CLASSNAME + "updateVehicle : Response Header {}", response.getHeader());
				logger.debug(CLASSNAME + "updateVehicle : Response {}", response);
				return response;
			} catch (Exception e) {
				logger.error(CLASSNAME + "updateVehicle : ERROR", e);
			}
			
			logger.debug(CLASSNAME + "updateVehicle : End");
			return new EwayBillVehicleUpdateResponse();
		}
		
		
		public GenerateEwayBillResponse generateEwayBill(String gstin, String ipAddress, EwayBillResponseData respData) {
			logger.debug(CLASSNAME + "generateEwayBill : Begin");
			Map<String, String> gstnHeader = Maps.newHashMap();
			gstnHeader.put("client_id", iHubEnoClientId);
			gstnHeader.put("client_secret", iHubEnoClientSecret);
			gstnHeader.put("ip_address", ipAddress);
			gstnHeader.put("gstin", gstin);
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("email", iHubEmail);
		
			String requestUrl = String.format("%s/%s", iHubUrl, "ewaybillapi/v1.03/ewayapi/genewaybill");
			try {
				EwayBillResponseData cebill = new EwayBillResponseData();
				cebill.setSupplyType(respData.getSupplyType());
				cebill.setSubSupplyType(respData.getSubSupplyType());
				if (isNotEmpty(respData.getSubSupplyType()) && "8".equalsIgnoreCase(respData.getSubSupplyType())) {
					cebill.setSubSupplyDesc("Others");
				}
				cebill.setDocType(respData.getDocType());
				cebill.setDocNo(respData.getDocNo());
				cebill.setDocDate(respData.getDocDate());
				cebill.setFromGstin(respData.getFromGstin());
				cebill.setFromTrdName(respData.getFromTrdName());
				cebill.setFromAddr1(respData.getFromAddr1());
				cebill.setFromAddr2(respData.getFromAddr2());
				cebill.setFromPlace(respData.getFromPlace());
				cebill.setFromPincode(respData.getFromPincode());
				cebill.setActFromStateCode(respData.getActFromStateCode());
				cebill.setFromStateCode(respData.getFromStateCode());
				cebill.setToGstin(respData.getToGstin());
				cebill.setToTrdName(respData.getToTrdName());
				cebill.setToAddr1(respData.getToAddr1());
				cebill.setToAddr2(respData.getToAddr2());
				cebill.setToPincode(respData.getToPincode());
				cebill.setToPlace(respData.getToPlace());
				cebill.setActToStateCode(respData.getActToStateCode());
				cebill.setToStateCode(respData.getToStateCode());
				if(isNotEmpty(respData.getTransactionType())) {
					cebill.setTransactionType(respData.getTransactionType());
				}else {
					cebill.setTransactionType(0);
				}
			
				cebill.setOtherValue(respData.getOtherValue());
				cebill.setCessNonAdvolValue(respData.getCessNonAdvolValue());
				cebill.setTransporterId(respData.getTransporterId());
				cebill.setTransporterName(respData.getTransporterName());
				cebill.setTransDistance(respData.getTransDistance());
				cebill.setCessValue(respData.getCessValue());
				cebill.setTotalValue(respData.getTotalValue());
				cebill.setTotInvValue(respData.getTotInvValue());
				if(isNotEmpty(respData.getIgstValue())) {
					cebill.setIgstValue(respData.getIgstValue());
				}
				if(isNotEmpty(respData.getCgstValue())) {
					cebill.setCgstValue(respData.getCgstValue());
				}
				if(isNotEmpty(respData.getSgstValue())) {
					cebill.setSgstValue(respData.getSgstValue());
				}
				Double totalAmount= 0d;
				List<EwayBillItems> items =  Lists.newArrayList();
				for(EwayBillItems itm : respData.getItemList()) {
					EwayBillItems eitms = new EwayBillItems();
					if(isNotEmpty(itm.getProductId())) {
						eitms.setProductId(itm.getProductId());
					}
					if(isNotEmpty(itm.getProductName())) {
						eitms.setProductName(itm.getProductName());
					}
					if(isNotEmpty(itm.getProductDesc())) {
						eitms.setProductDesc(itm.getProductDesc());
					}
					eitms.setQtyUnit(itm.getQtyUnit());
					eitms.setQuantity(itm.getQuantity());
					eitms.setHsnCode(itm.getHsnCode());
					if(isNotEmpty(itm.getIgstRate())) {
						eitms.setIgstRate(itm.getIgstRate());
					}else {
						eitms.setIgstRate(0d);
					}
					if(isNotEmpty(itm.getCgstRate())) {
						eitms.setCgstRate(itm.getCgstRate());
					}else {
						eitms.setCgstRate(0d);
					}
					if(isNotEmpty(itm.getSgstRate())) {
						eitms.setSgstRate(itm.getSgstRate());
					}else {
						eitms.setSgstRate(0d);
					}
					if(isNotEmpty(itm.getCessRate())) {
						eitms.setCessRate(itm.getCessRate());
					}else {
						eitms.setCessRate(0d);
					}
					eitms.setCessNonAdvol(itm.getCessNonAdvol());
					eitms.setTaxableAmount(itm.getTaxableAmount());
					totalAmount = itm.getTotalAmount();
					
					items.add(eitms);
					
				}
				cebill.setItemList(items);
				String vehicleno=null;String transmode=null;String transDocNo=null;String transDocDate=null;
				if(isNotEmpty(respData.getVehiclListDetails().get(0).getVehicleNo())) {
					vehicleno= respData.getVehiclListDetails().get(0).getVehicleNo();
				}else {
					vehicleno="";
				}
				if(isNotEmpty(respData.getVehiclListDetails().get(0).getTransMode())) {
					transmode = respData.getVehiclListDetails().get(0).getTransMode();
				}else {
					transmode="";
				}
				if(isNotEmpty(respData.getVehiclListDetails().get(0).getTransDocNo())) {
					transDocNo = respData.getVehiclListDetails().get(0).getTransDocNo();
				}else {
					transDocNo="";
				}
				if(isNotEmpty(respData.getVehiclListDetails().get(0).getTransDocDate())) {
					transDocDate = respData.getVehiclListDetails().get(0).getTransDocDate();
				}else {
					transDocDate="";
				}
				if(isNotEmpty(respData.getVehiclListDetails())) {
					List<EBillVehicleListDetails> vehicleList = Lists.newArrayList();
					for(EBillVehicleListDetails evlist : respData.getVehiclListDetails()) {
						EBillVehicleListDetails vdetails = new EBillVehicleListDetails();
						if(isNotEmpty(evlist.getVehicleNo())) {
							vdetails.setVehicleNo(evlist.getVehicleNo());
						}
						vdetails.setTransMode(evlist.getTransMode());
						vdetails.setTransDocNo(evlist.getTransDocNo());
						if(isNotEmpty(evlist.getTransDocDate())) {
							vdetails.setTransDocDate(evlist.getTransDocDate());
						}else {
							vdetails.setTransDocDate("");
						}
						//vdetails.setGroupNo(evlist.getGroupNo());
						vehicleno=evlist.getVehicleNo();
						
						vehicleList.add(vdetails);
					}
				}	
				//cebill.setVehiclListDetails(vehicleList);
				
				mapper.setSerializationInclusion(Include.NON_NULL);
				String strUpdateRequest = mapper.writeValueAsString(cebill);
				
				JsonNode  jsn = new JsonNode(strUpdateRequest);
				jsn.getObject().accumulate("vehicleNo",vehicleno);
				jsn.getObject().accumulate("totalAmount", totalAmount);
				jsn.getObject().accumulate("transMode", transmode);
				jsn.getObject().accumulate("transDocNo", transDocNo);
				jsn.getObject().accumulate("transDocDate", transDocDate);
				
				logger.info("----------------JSON BODY------------"+strUpdateRequest);
				
				HttpResponse<String> httpResponse = Unirest
		                .post(requestUrl)
		                .queryString(queryStringMap)
		                  .header("client_id", iHubEnoClientId)
		                  .header("client_secret", iHubEnoClientSecret)
		                  .header("ip_address",ipAddress)
		                  .header("gstin",gstin)
		                .header("Content-Type", "application/json")
		                .body(jsn)
		                .asString();
			
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				GenerateEwayBillResponse response = mapper.readValue(httpResponse.getBody().toString(), GenerateEwayBillResponse.class);
				
				
				//logger.info("cancelEwayBill response json body--------------------"+httpResponse.getBody().toString());
				logger.info("iHubConsumerService response--------------------"+response);
				logger.debug(CLASSNAME + "generateEwayBill : Response Header {}", response.getHeader());
				logger.debug(CLASSNAME + "generateEwayBill : Response {}", response);
				return response;
			} catch (Exception e) {
				logger.error(CLASSNAME + "updateVehicle : ERROR", e);
			}
			
			logger.debug(CLASSNAME + "updateVehicle : End");
			return new GenerateEwayBillResponse();
		}

		
	public LedgerResponse getLedgerCashDetails(final Client client, final String gstin, final String fromDate, final String toDate, final String userId, final boolean initial) {
		logger.debug(CLASSNAME + "getLedgerCashDetails : Begin");
		Long startTime = System.currentTimeMillis();
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/ledgers/cashdtl");
		
		try {
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("frdt", fromDate);
			queryStringMap.put("todt", toDate);
			
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			logger.debug(CLASSNAME + "getLedgerCashDetails : queryStringMap {}", queryStringMap);
			logger.debug(CLASSNAME + "getLedgerCashDetails : gstnHeader {}", gstnHeader);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			logger.debug(CLASSNAME + "getLedgerCashDetails : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "getLedgerCashDetails : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			LedgerResponse response = mapper.readValue(jsonResponse.getBody().toString(), LedgerResponse.class);
			logger.debug(CLASSNAME + "getLedgerCashDetails : Response {}", response);
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "getLedgerCashDetails : Duration {}", (endTime - startTime));
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getLedgerCashDetails : ERROR", e);
		}
		logger.debug(CLASSNAME + "getLedgerCashDetails : End");
		return new LedgerResponse();
	}
	
	public LedgerResponse getLedgerITCDetails(final Client client, final String gstin, final String fromDate, final String toDate, final String userId, final boolean initial) {
		logger.debug(CLASSNAME + "getLedgerITCDetails : Begin");
		Long startTime = System.currentTimeMillis();
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/ledgers/itc");
		
		try {
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("frdt", fromDate);
			queryStringMap.put("todt", toDate);
			
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			logger.debug(CLASSNAME + "getLedgerITCDetails : queryStringMap {}", queryStringMap);
			logger.debug(CLASSNAME + "getLedgerITCDetails : gstnHeader {}", gstnHeader);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			logger.debug(CLASSNAME + "getLedgerITCDetails : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "getLedgerITCDetails : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			LedgerResponse response = mapper.readValue(jsonResponse.getBody().toString(), LedgerResponse.class);
			logger.debug(CLASSNAME + "getLedgerITCDetails : Response {}", response);
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "getLedgerITCDetails : Duration {}", (endTime - startTime));
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getLedgerITCDetails : ERROR", e);
		}
		logger.debug(CLASSNAME + "getLedgerITCDetails : End");
		return new LedgerResponse();
	}
	
	public LedgerResponse getCashITCBalanceDetails(final Client client, final String gstin, final String retPeriod, final String userId, final boolean initial) {
		logger.debug(CLASSNAME + "getLedgerITCDetails : Begin");
		Long startTime = System.currentTimeMillis();
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/ledgers/bal");
		
		try {
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("retperiod", retPeriod);
			
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			logger.debug(CLASSNAME + "getCashITCBalanceDetails : queryStringMap {}", queryStringMap);
			logger.debug(CLASSNAME + "getCashITCBalanceDetails : gstnHeader {}", gstnHeader);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			logger.debug(CLASSNAME + "getCashITCBalanceDetails : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "getCashITCBalanceDetails : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			LedgerResponse response = mapper.readValue(jsonResponse.getBody().toString(), LedgerResponse.class);
			logger.debug(CLASSNAME + "getCashITCBalanceDetails : Response {}", response);
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "getCashITCBalanceDetails : Duration {}", (endTime - startTime));
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getCashITCBalanceDetails : ERROR", e);
		}
		logger.debug(CLASSNAME + "getCashITCBalanceDetails : End");
		return new LedgerResponse();
	}
	
	public LedgerResponse getTaxLedgerDetails(final Client client, final String gstin, final String retPeriod, final String userId, final boolean initial) {
		logger.debug(CLASSNAME + "getTaxLedgerDetails : Begin");
		Long startTime = System.currentTimeMillis();
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/ledgers/tax");
		
		try {
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("retperiod", retPeriod);
			
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			logger.debug(CLASSNAME + "getTaxLedgerDetails : queryStringMap {}", queryStringMap);
			logger.debug(CLASSNAME + "getTaxLedgerDetails : gstnHeader {}", gstnHeader);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			logger.debug(CLASSNAME + "getTaxLedgerDetails : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "getTaxLedgerDetails : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			LedgerResponse response = mapper.readValue(jsonResponse.getBody().toString(), LedgerResponse.class);
			logger.debug(CLASSNAME + "getTaxLedgerDetails : Response {}", response);
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "getTaxLedgerDetails : Duration {}", (endTime - startTime));
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getTaxLedgerDetails : ERROR", e);
		}
		logger.debug(CLASSNAME + "getTaxLedgerDetails : End");
		return new LedgerResponse();
	}
	
	public LedgerResponse getReturnLiabilityBalanceDetails(final Client client, final String gstin, final String retPeriod, final String retType, final String userId, final boolean initial) {
		logger.debug(CLASSNAME + "getReturnLiabilityBalanceDetails : Begin");
		Long startTime = System.currentTimeMillis();
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/ledgers/taxpayable");
		
		try {
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("retperiod", retPeriod);
			queryStringMap.put("rettype", retType);
			
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			logger.debug(CLASSNAME + "getReturnLiabilityBalanceDetails : queryStringMap {}", queryStringMap);
			logger.debug(CLASSNAME + "getReturnLiabilityBalanceDetails : gstnHeader {}", gstnHeader);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			logger.debug(CLASSNAME + "getReturnLiabilityBalanceDetails : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "getReturnLiabilityBalanceDetails : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			LedgerResponse response = mapper.readValue(jsonResponse.getBody().toString(), LedgerResponse.class);
			logger.debug(CLASSNAME + "getReturnLiabilityBalanceDetails : Response {}", response);
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "getReturnLiabilityBalanceDetails : Duration {}", (endTime - startTime));
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getReturnLiabilityBalanceDetails : ERROR", e);
		}
		logger.debug(CLASSNAME + "getReturnLiabilityBalanceDetails : End");
		return new LedgerResponse();
	}
	
	@Override
	public Response publicRettrack(final String gstin,final String fy) {
		logger.debug(CLASSNAME + "publicRettrack : Begin");
		Long startTime = System.currentTimeMillis();
		Map<String, String> gstnHeader = Maps.newHashMap();
		gstnHeader.put("client_id", iHubClientId);
		gstnHeader.put("client_secret", iHubClientSecret);
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("gstin", gstin);
		queryStringMap.put("fy", fy);
		queryStringMap.put("email", iHubEmail);
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/public/rettrack");
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "publicRettrack : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "publicRettrack : Response {}", response);
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "publicRettrack : Duration {}", (endTime - startTime));
			
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "publicRettrack : ERROR", e);
		}
		logger.debug(CLASSNAME + "publicRettrack : End");
		return new Response();
	}
	
	public GSTRCommonResponse getAutoCalculatedDetails(final Client client, final String returnType, final String gstin, final String retPeriod) {
		logger.debug(CLASSNAME + "getAutoCalculatedDetails : Begin");
		Long startTime = System.currentTimeMillis();
		String url = "/"+returnType.toLowerCase()+"/getautocal";
		String requestUrl = String.format("%s/%s", iHubUrl, url);
		
		try {
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			
			logger.debug(CLASSNAME + "getAutoCalculatedDetails : gstnHeader {}", gstnHeader);
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("retperiod", retPeriod);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			GSTRCommonResponse response = mapper.readValue(jsonResponse.getBody().toString(), GSTRCommonResponse.class);
			logger.debug(CLASSNAME + "getAutoCalculatedDetails : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "getAutoCalculatedDetails : Response getStatuscd {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "getAutoCalculatedDetails : Duration {}", (endTime - startTime));
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getAutoCalculatedDetails : ERROR", e);
		}
		logger.debug(CLASSNAME + "getAutoCalculatedDetails : End");
		return new GSTRCommonResponse();
	}
	
	
	public GSTR9GetResponse getGSTR9Details(final Client client, final String returnType, final String gstin, final String retPeriod) {
		logger.debug(CLASSNAME + "getGSTR9Details : Begin");
		Long startTime = System.currentTimeMillis();
		String url = "/"+returnType.toLowerCase()+"/getdet";
		String requestUrl = String.format("%s/%s", iHubUrl, url);
		
		try {
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			
			logger.debug(CLASSNAME + "getGSTR9Details : gstnHeader {}", gstnHeader);
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("retperiod", retPeriod);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			GSTR9GetResponse response = mapper.readValue(jsonResponse.getBody().toString(), GSTR9GetResponse.class);
			logger.debug(CLASSNAME + "getGSTR9Details : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "getGSTR9Details : Response getStatuscd {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "getGSTR9Details : Duration {}", (endTime - startTime));
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getGSTR9Details : ERROR", e);
		}
		logger.debug(CLASSNAME + "getGSTR9Details : End");
		return new GSTR9GetResponse();
	}
	public Response authenticateEinvoiceAPI(final String gstin,final String ipAddress,final String uname,final String pwd) {
		logger.debug(CLASSNAME + "authenticateEinvoiceAPI : Begin");
		Map<String, String> gstnHeader = Maps.newHashMap();
		gstnHeader.put("client_id", iHubEinvClientId);
		gstnHeader.put("client_secret", iHubEinvClientSecret);
		gstnHeader.put("ip_address", ipAddress);
		gstnHeader.put("gstin", gstin);
		gstnHeader.put("username",uname);
		gstnHeader.put("password",pwd);
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("email", iHubEmail);
		String requestUrl = String.format("%s/%s", iHubUrl, "einvoice/authenticate");
		logger.info("requesturl::"+requestUrl);
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug("authenticateEinvoiceAPI::"+jsonResponse.getBody().toString());
			logger.debug("authenticateEinvoiceAPI::"+response);
			logger.debug(CLASSNAME + "authenticateEinvoiceAPI : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "authenticateEinvoiceAPI : Response {}", response);
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "authenticateEinvoiceAPI : ERROR", e);
		}
		logger.debug(CLASSNAME + "authenticateEinvoiceAPI : End");
		return new Response();
	}
	public GenerateIRNResponse generateIRN(String gstin, String ipAddress, InvoiceParent respData) {
		EinvoiceConfigurations configDetails = einvoiceConfigurationRepository.findByClientid(respData.getClientid());
			String authToken="";
			String uname="";
			String pwd="";
			if(isNotEmpty(configDetails)) {
				uname=configDetails.getUserName();
				pwd=configDetails.getPassword();
			}
		HeaderKeys headerKeys = headerKeysRepository.findByGstusernameAndEmail(uname,iHubEmail);
		if(isNotEmpty(headerKeys) && isNotEmpty(headerKeys.getAuthtoken())) {
			authToken = headerKeys.getAuthtoken();
		}
		Map<String, String> gstnHeader = Maps.newHashMap();
		gstnHeader.put("client_id", iHubEinvClientId);
		gstnHeader.put("client_secret", iHubEinvClientSecret);
		gstnHeader.put("ip_address", ipAddress);
		gstnHeader.put("gstin", gstin);
		gstnHeader.put("username",uname);
		gstnHeader.put("password",pwd);
		gstnHeader.put("auth-token",authToken);
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("email", iHubEmail);
		String requestUrl = String.format("%s/%s", iHubUrl, "einvoice/type/GENERATE/version/V1_03");
		try {
		Base invoice = einvoiceService.arrangeJsonDataForIRN(respData);	
		mapper.setSerializationInclusion(Include.ALWAYS);
		String strUpdateRequest = mapper.writeValueAsString(invoice);
		logger.debug("strUpdateRequest----------"+strUpdateRequest);
		HttpResponse<String> httpResponse = Unirest
                .post(requestUrl)
                .queryString(queryStringMap)
                .header("client_id", iHubEinvClientId)
                .header("client_secret", iHubEinvClientSecret)
                .header("ip_address",ipAddress)
                .header("gstin",gstin)
                .header("username",uname)
                .header("password",pwd)
                .header("auth-token",authToken)
                .header("Content-Type", "application/json")
                .body(strUpdateRequest)
                .asString();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String resBody=httpResponse.getBody().toString();
	//	logger.info("resBody ::\t"+resBody);
		GenerateIRNResponse response = mapper.readValue(resBody, GenerateIRNResponse.class);
		logger.info("iHubConsumerService GenerateIRNResponse--------------------"+response);
		logger.info("iHubConsumerService GenerateIRNResponse--------------------"+response.getData());
		logger.debug(CLASSNAME + "GenerateIRNResponse : Response Header {}", response.getHeader());
		logger.debug(CLASSNAME + "GenerateIRNResponse : Response {}", response);
		return response;
	} catch (Exception e) {
		logger.error(CLASSNAME + "GenerateIRNResponse : ERROR", e);
	}
	logger.debug(CLASSNAME + "GenerateIRNResponse : End");
	return new GenerateIRNResponse();
	}
	@Override
	public CancelIRNResponse cancelIRN(String gstin, String ipAddress, String clientid,CancelIRN cancelirn) {
		logger.debug(CLASSNAME + "cancelIRN : Begin");
		EinvoiceConfigurations configDetails = einvoiceConfigurationRepository.findByClientid(clientid);
		String authToken="";
		String uname="";
		String pwd="";
		if(isNotEmpty(configDetails)) {
			uname=configDetails.getUserName();
			pwd=configDetails.getPassword();
		HeaderKeys headerKeys = headerKeysRepository.findByGstusernameAndEmail(uname,iHubEmail);
		if(isNotEmpty(headerKeys) && isNotEmpty(headerKeys.getAuthtoken())) {
			authToken = headerKeys.getAuthtoken();
		}else {
			CancelIRNResponse response = new CancelIRNResponse();
			response.setStatusdesc("InActive");
			response.setStatuscd("0");
			return response;
		}
		Map<String, String> gstnHeader = Maps.newHashMap();
		gstnHeader.put("client_id", iHubEinvClientId);
		gstnHeader.put("client_secret", iHubEinvClientSecret);
		gstnHeader.put("ip_address", ipAddress);
		gstnHeader.put("gstin", gstin);
		gstnHeader.put("username",uname);
		gstnHeader.put("password",pwd);
		gstnHeader.put("auth-token",authToken);
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("email", iHubEmail);
		logger.info(cancelirn.getIrn()+"-----"+cancelirn.getCnlRsn()+"-----"+cancelirn.getCnlRem());
		String requestUrl = String.format("%s/%s", iHubUrl, "einvoice/type/CANCEL/version/V1_03");
		logger.info("---requestUrl---"+requestUrl);
		logger.info("---authToken---"+authToken);
		try {
			CancelIRN cebill = new CancelIRN();
			cebill.setIrn(cancelirn.getIrn());
			cebill.setCnlRsn(cancelirn.getCnlRsn());
			cebill.setCnlRem(cancelirn.getCnlRem());
			mapper.setSerializationInclusion(Include.NON_NULL);
			String strCancelRequest = mapper.writeValueAsString(cebill);
			HttpResponse<String> httpResponse = Unirest
	                .post(requestUrl)
	                .queryString(queryStringMap)
	                .header("client_id", iHubEinvClientId)
	                  .header("client_secret", iHubEinvClientSecret)
	                  .header("ip_address",ipAddress)
	                  .header("gstin",gstin)
	                  .header("username",uname)
	                  .header("password",pwd)
	                  .header("auth-token",authToken)
	                .header("Content-Type", "application/json")
	                .body(strCancelRequest)
	                .asString();
				logger.info(httpResponse.getBody());
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			CancelIRNResponse response = mapper.readValue(httpResponse.getBody().toString(), CancelIRNResponse.class);
			logger.info("iHubConsumerService response--------------------"+httpResponse.getBody().toString());
			logger.debug(CLASSNAME + "cancelIRN : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "cancelIRN : Response {}", response);
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "cancelIRN : ERROR", e);
		}
		}else {
			return new CancelIRNResponse();
		}
		logger.debug(CLASSNAME + "cancelIRN : End");
		return new CancelIRNResponse();
	}

	@Override
	public AnxInvoiceSupport getANX2Invoices(Client client, String gstin, int month, int year, String gstr, String type,
			String ctin, String userId, boolean initial) throws MasterGSTException {
		logger.debug(CLASSNAME + "getANX2Invoices : Begin");
		Long startTime = System.currentTimeMillis();
		String url=String.format("%s/%s", iHubUrl, "annexure/"+gstr.toUpperCase()+"/type/");
		if(NullUtil.isNotEmpty(url)) {
			if(type.equals(MasterGSTConstants.B2B)) {
				url+="B2B/version/V1_0";
			}else if(type.equals(MasterGSTConstants.DE)) {
				url+="DE/version/V1_0";
			} else if(type.equals(MasterGSTConstants.SEZWP)) {
				url+="SEZWP/version/V1_0";
			} else if(type.equals(MasterGSTConstants.SEZWOP)) {
				url+="SEZWOP/version/V1_0";
			} else if(type.equals(MasterGSTConstants.ISDC)) {
				url+="ISDC/version/V1_0";
			}else {
				url+=type+"/version/V1_0";
			}
			
			try {
				String strMonth =  month<10 ? "0"+month : month+"";
				String retPeriod = strMonth+year;
				
				Map<String, Object> queryStringMap = Maps.newHashMap();
				queryStringMap.put("gstin", gstin);
				queryStringMap.put("email", iHubEmail);
				queryStringMap.put("retperiod", retPeriod);

					if(NullUtil.isNotEmpty(ctin)) {
						queryStringMap.put("ctin", ctin);
						queryStringMap.put("action", "N");
						queryStringMap.put("fromtime", "01-"+strMonth+"-"+year);
					}
				
				Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
				logger.info(CLASSNAME + "getANX2Invoices : queryStringMap {}", queryStringMap);
				logger.info(CLASSNAME + "getANX2Invoices : gstnHeader {}", gstnHeader);
				
				HttpResponse<JsonNode> jsonResponse = Unirest.get(url)
						.queryString(queryStringMap)
						.headers(gstnHeader).asJson();
				logger.info(CLASSNAME + "getANX2Invoices : jsonResponse {}", jsonResponse.toString());
				logger.info(CLASSNAME + "getANX2Invoices : jsonResponse.getBody() {}", jsonResponse.getBody());
				Long endTime = System.currentTimeMillis();
				logger.info(CLASSNAME + "getANX2Invoices {} : Type {} : Duration {}", gstr, type, (endTime - startTime));
				if (jsonResponse.getStatus() == 200) {
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				}
				NewReturnsResponse response = null;

					response = mapper.readValue(jsonResponse.getBody().toString(), NewReturnsResponse.class);
				
				if(NullUtil.isNotEmpty(response) && NullUtil.isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals("1") && NullUtil.isNotEmpty(response.getData())) {
					return response.getData();
				} else if(initial && NullUtil.isNotEmpty(response) 
						&& NullUtil.isNotEmpty(response.getStatuscd()) 
						&& response.getStatuscd().equals("0")
						&& NullUtil.isNotEmpty(response.getError())
						&& NullUtil.isNotEmpty(response.getError().getErrorcd())
						&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
					performRefreshToken(gstnHeader);
					return getANX2Invoices(client, gstin, month, year, gstr, type, ctin, userId, false);
				} else if(NullUtil.isNotEmpty(response) && NullUtil.isNotEmpty(response.getError())) {
					if(NullUtil.isEmpty(response.getError().getMessage()) 
							|| !response.getError().getMessage().startsWith("No Invoices found for the provided")) {
						throw new MasterGSTException(response.getError().getMessage());
					}
				}
			} catch (Exception e) {
				logger.error(CLASSNAME + "getANX2Invoices : ERROR", e);
				throw new MasterGSTException(e.getMessage());
			}
		}
		logger.debug(CLASSNAME + "getANX2Invoices : End");
		return null;
	}
	
	public ANX2Response newANX2ReturnStatus(final String refId, final String state, final String gstName, 
			final String gstn, final String retPeriod, String rettype, final boolean initial) {
		logger.debug(CLASSNAME + "returnStatus : Begin");
		Long startTime = System.currentTimeMillis();
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/all/newretstatus");
		
		try {
			Map<String, String> header = getGSTTransactionHeader(state, gstName);
			header.put("gstin", gstn);
			header.put("ret_period", retPeriod);
				
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("gstin", header.get("gstin"));
			queryStringMap.put("email", iHubEmail);
			
			queryStringMap.put("returnperiod", header.get("ret_period"));
			queryStringMap.put("refid", refId);
			queryStringMap.put("rettype", rettype);
			
			logger.info(CLASSNAME + "returnStatus : queryStringMap {}", queryStringMap);
			logger.info(CLASSNAME + "returnStatus : header {}", header);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(header).asJson();
			logger.debug(CLASSNAME + "returnStatus : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "returnStatus : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				//mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			ANX2Response response = mapper.readValue(jsonResponse.getBody().toString(), ANX2Response.class);
			logger.debug(CLASSNAME + "returnStatus : Response Code {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "returnStatus : Duration {}", (endTime - startTime));
			if(initial && NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError())
					&& NullUtil.isNotEmpty(response.getError().getErrorcd())
					&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
				performRefreshToken(header);
				return anx2ReturnStatus(refId, state, gstName, gstn, retPeriod, false);
			} else if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnStatus : ERROR", e);
		}
		logger.debug(CLASSNAME + "returnStatus : End");
		return new ANX2Response();
	}
	
	public ANX2Response anx2ReturnStatus(final String refId, final String state, final String gstName, 
			final String gstn, final String retPeriod, final boolean initial) {
		logger.debug(CLASSNAME + "returnStatus : Begin");
		Long startTime = System.currentTimeMillis();
		
		String requestUrl = String.format("%s/%s", iHubUrl, "/gstr/retstatus");
		
		try {
			Map<String, String> header = getGSTTransactionHeader(state, gstName);
			header.put("gstin", gstn);
			header.put("ret_period", retPeriod);
				
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("gstin", header.get("gstin"));
			queryStringMap.put("email", iHubEmail);
			
			queryStringMap.put("returnperiod", header.get("ret_period"));
			queryStringMap.put("refid", refId);
			logger.debug(CLASSNAME + "returnStatus : queryStringMap {}", queryStringMap);
			logger.debug(CLASSNAME + "returnStatus : header {}", header);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(header).asJson();
			logger.debug(CLASSNAME + "returnStatus : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "returnStatus : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				//mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			ANX2Response response = mapper.readValue(jsonResponse.getBody().toString(), ANX2Response.class);
			logger.debug(CLASSNAME + "returnStatus : Response Code {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "returnStatus : Duration {}", (endTime - startTime));
			if(initial && NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError())
					&& NullUtil.isNotEmpty(response.getError().getErrorcd())
					&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
				performRefreshToken(header);
				return anx2ReturnStatus(refId, state, gstName, gstn, retPeriod, false);
			} else if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnStatus : ERROR", e);
		}
		logger.debug(CLASSNAME + "returnStatus : End");
		return new ANX2Response();
	}
	
	public ANX2Response saveANX2Returns(final Base invoice, final String state, final String gstName, final String gstn, 
			final String retPeriod, final String returnType, final boolean initial) throws Exception {
		logger.debug(CLASSNAME + "saveReturns : Begin");
		Long startTime = System.currentTimeMillis();
		String url = "/gstr1/retsave";
		if(NullUtil.isNotEmpty(returnType) && returnType.toLowerCase().startsWith("anx") && returnType.equalsIgnoreCase(MasterGSTConstants.ANX1)) {
			url = "/annexure/save/"+returnType+"/type/"+retPeriod+"/version/V1_0";
		} else if(NullUtil.isNotEmpty(returnType) && returnType.equalsIgnoreCase(MasterGSTConstants.ANX2)) {
			url = "annexure/save/"+returnType+"/type/SAVEANX2/version/V1_0";
		}else if(NullUtil.isNotEmpty(returnType)) {
			url = "/"+returnType.toLowerCase()+"/retsave";
		}
		String requestUrl = String.format("%s/%s", iHubUrl, url);
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("email", iHubEmail);
		
		Map<String, String> header = getGSTTransactionHeader(state, gstName);
		header.put("gstin", gstn);
		if(invoice instanceof InvoiceParent) {
			header.put("ret_period", ((InvoiceParent) invoice).getFp());
		} else {
		header.put("ret_period", retPeriod);
		}
		header.put("Content-Type", "application/json");
		
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		String content;
		try {
 
				content = mapper.writeValueAsString(invoice);
			
			logger.info(CLASSNAME + "saveReturns : content {}"+ content);
			logger.info(CLASSNAME + "saveReturns : requestUrl"+ requestUrl);
			logger.info(CLASSNAME + "saveReturns : headers"+ header);
			logger.info(CLASSNAME + "saveReturns : queryStringMap"+ queryStringMap);
			HttpResponse<JsonNode> jsonResponse = null;
			if(NullUtil.isNotEmpty(returnType) && returnType.toLowerCase().startsWith("anx")) {
				jsonResponse = Unirest.post(requestUrl)
					.queryString(queryStringMap)
					.headers(header)
					.body(new JsonNode(content)).asJson();
			} else {
				jsonResponse = Unirest.put(requestUrl)
						.queryString(queryStringMap)
						.headers(header)
						.body(new JsonNode(content)).asJson();
			}
			logger.info(CLASSNAME + "saveReturns : jsonResponse {}", jsonResponse.toString());
			logger.info(CLASSNAME + "saveReturns : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			logger.info(CLASSNAME + "saveReturns : Response Body {}", jsonResponse.getBody().toString());
			ANX2Response response = mapper.readValue(jsonResponse.getBody().toString(), ANX2Response.class);
			logger.info(CLASSNAME + "saveReturns : Response Headers {}", response.getHeader());
			logger.info(CLASSNAME + "saveReturns : Response Data {}", response.getData());
			logger.info(CLASSNAME + "saveReturns : Response Code {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.info(CLASSNAME + "{} saveReturns : Duration {}", returnType, (endTime - startTime));
			if(initial && NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError())
					&& NullUtil.isNotEmpty(response.getError().getErrorcd())
					&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
				performRefreshToken(header);
				return saveANX2Returns(invoice, state, gstName, gstn, retPeriod, returnType, false);
			} else if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "saveReturns : ERROR", e);
		}
		logger.debug(CLASSNAME + "saveReturns : End");
		return new ANX2Response();
	}

	public Anx1InvoiceSupport getANX1Invoices(Client client, String gstin, int month, int year, String gstr, String type,
			String ctin, String userId, boolean initial) throws MasterGSTException {
		logger.debug(CLASSNAME + "getANX1Invoices : Begin");
		Long startTime = System.currentTimeMillis();
		String url=String.format("%s/%s", iHubUrl, "annexure/"+gstr.toUpperCase()+"/type/");
		if(NullUtil.isNotEmpty(url)) {
			if(type.equals(MasterGSTConstants.B2B)) {
				url+="B2B/version/V1_0";
			}else if(type.equals(MasterGSTConstants.B2BA)) {
				url+="B2BA/version/V1_0";
			} else if(type.equals(MasterGSTConstants.B2C)) {
				url+="B2C/version/V1_0";
			} else if(type.equals(MasterGSTConstants.DE)) {
				url+="DE/version/V1_0";
			} else if(type.equals(MasterGSTConstants.DEA)) {
				url+="DEA/version/V1_0";
			}else if(type.equals(MasterGSTConstants.ECOM)) {
				url+="ECOM/version/V1_0";
			} else if(type.equals(MasterGSTConstants.EXPWOP)) {
				url+="EXPWOP/version/V1_0";
			} else if(type.equals(MasterGSTConstants.EXPWP)) {
				url+="EXPWP/version/V1_0";
			} else if(type.equals(MasterGSTConstants.IMPGSEZ)) {
				url+="IMPGSEZ/version/V1_0";
			}else if(type.equals(MasterGSTConstants.IMPG)) {
				url+="IMPG/version/V1_0";
			} else if(type.equals(MasterGSTConstants.IMPS)) {
				url+="IMPS/version/V1_0";
			}else if(type.equals(MasterGSTConstants.MIS)) {
				url+="MIS/version/V1_0";
			} else if(type.equals(MasterGSTConstants.RJDOC)) {
				url+="RJDOC/version/V1_0";
			} else if(type.equals(MasterGSTConstants.REV)) {
				url+="REV/version/V1_0";
			} else if(type.equals(MasterGSTConstants.SEZWOP)) {
				url+="SEZWOP/version/V1_0";
			}else if(type.equals(MasterGSTConstants.SEZWOPA)) {
				url+="SEZWOPA/version/V1_0";
			} else if(type.equals(MasterGSTConstants.SEZWP)) {
				url+="SEZWP/version/V1_0";
			} else if(type.equals(MasterGSTConstants.SEZWPA)) {
				url+="SEZWPA/version/V1_0";
			}else {
				url+=type+"/version/V1_0";
			}
			
			try {
				String strMonth =  month<10 ? "0"+month : month+"";
				String retPeriod = strMonth+year;
				
				Map<String, Object> queryStringMap = Maps.newHashMap();
				queryStringMap.put("gstin", gstin);
				queryStringMap.put("email", iHubEmail);
				queryStringMap.put("retperiod", retPeriod);

					if(NullUtil.isNotEmpty(ctin)) {
						queryStringMap.put("ctin", ctin);
						queryStringMap.put("action", "N");
						queryStringMap.put("fromtime", "01-"+strMonth+"-"+year);
					}
				
				Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
				logger.info(CLASSNAME + "getANX2Invoices : queryStringMap {}", queryStringMap);
				logger.info(CLASSNAME + "getANX2Invoices : gstnHeader {}", gstnHeader);
				
				HttpResponse<JsonNode> jsonResponse = Unirest.get(url)
						.queryString(queryStringMap)
						.headers(gstnHeader).asJson();
				logger.info(CLASSNAME + "getANX2Invoices : jsonResponse {}", jsonResponse.toString());
				logger.info(CLASSNAME + "getANX2Invoices : jsonResponse.getBody() {}", jsonResponse.getBody());
				Long endTime = System.currentTimeMillis();
				logger.info(CLASSNAME + "getANX2Invoices {} : Type {} : Duration {}", gstr, type, (endTime - startTime));
				if (jsonResponse.getStatus() == 200) {
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				}
				Anx1ReturnsResponse response = null;

					response = mapper.readValue(jsonResponse.getBody().toString(), Anx1ReturnsResponse.class);
				
				if(NullUtil.isNotEmpty(response) && NullUtil.isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals("1") && NullUtil.isNotEmpty(response.getData())) {
					return response.getData();
				} else if(initial && NullUtil.isNotEmpty(response) 
						&& NullUtil.isNotEmpty(response.getStatuscd()) 
						&& response.getStatuscd().equals("0")
						&& NullUtil.isNotEmpty(response.getError())
						&& NullUtil.isNotEmpty(response.getError().getErrorcd())
						&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
					performRefreshToken(gstnHeader);
					return getANX1Invoices(client, gstin, month, year, gstr, type, ctin, userId, false);
				} else if(NullUtil.isNotEmpty(response) && NullUtil.isNotEmpty(response.getError())) {
					if(NullUtil.isEmpty(response.getError().getMessage()) 
							|| !response.getError().getMessage().startsWith("No Invoices found for the provided")) {
						throw new MasterGSTException(response.getError().getMessage());
					}
				}
			} catch (Exception e) {
				logger.error(CLASSNAME + "getANX2Invoices : ERROR", e);
				throw new MasterGSTException(e.getMessage());
			}
		}
		logger.debug(CLASSNAME + "getANX2Invoices : End");
		return null;
	}

	
	public Response anx1ReturnSummary(final Client client, final String gstin, final String retPeriod, 
			final String userId, final String returnType, final boolean initial) throws Exception {
		logger.debug(CLASSNAME + "returnSummary : Begin");
		Long startTime = System.currentTimeMillis();
		String url=String.format("%s/%s", iHubUrl, "annexure/ANX1/type/GETSUM/version/V1_0");
		String requestUrl = String.format("%s/%s", iHubUrl, url);
		
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("gstin", gstin);
		queryStringMap.put("email", iHubEmail);
		queryStringMap.put("retperiod", retPeriod);
		
		Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
		logger.debug(CLASSNAME + "returnSummary : queryStringMap {}", queryStringMap);
		logger.debug(CLASSNAME + "returnSummary : gstnHeader {}", gstnHeader);
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			logger.debug(CLASSNAME + "returnSummary : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "returnSummary : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			logger.debug(CLASSNAME + "returnSummary : Response Code {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "{} returnSummary : Duration {}", returnType, (endTime - startTime));
			if(initial && NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError())
					&& NullUtil.isNotEmpty(response.getError().getErrorcd())
					&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
				performRefreshToken(gstnHeader);
				return anx1ReturnSummary(client, gstin, retPeriod, userId, returnType, false);
			} else if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnSummary : ERROR", e);
		}
		logger.debug(CLASSNAME + "returnSummary : End");
		return new Response();
	}
	
	@Override
	public EwayBillDateResponse getEbillDetailsByOtherParties(String gstin,String date, final String ipAddress) {
		logger.debug(CLASSNAME + "getEbillDetailsByOtherParties : Begin");
		Map<String, String> gstnHeader = Maps.newHashMap();
		gstnHeader.put("client_id", iHubEnoClientId);
		gstnHeader.put("client_secret", iHubEnoClientSecret);
		gstnHeader.put("ip_address", ipAddress);
		gstnHeader.put("gstin", gstin);
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("date",date);
		queryStringMap.put("email", iHubEmail);
		String requestUrl = String.format("%s/%s", iHubUrl, "ewaybillapi/v1.03/ewayapi/getewaybillsofotherparty");
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			logger.debug("getEbillDetailsByOtherParties response body---------------------"+jsonResponse.getBody().toString());
			EwayBillDateResponse response = mapper.readValue(jsonResponse.getBody().toString(), EwayBillDateResponse.class);
			logger.debug("iHubConsumerService getEbillDetailsByOtherParties response--------------------"+response);
			logger.debug(CLASSNAME + "getEbillDetailsByOtherParties : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "getEbillDetailsByOtherParties : Response {}", response);
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getEbillDetailsByOtherParties : ERROR", e);
		}
		
		logger.debug(CLASSNAME + "getEbillDetailsByOtherParties : End");
		return new EwayBillDateResponse();
	}
	@Override
	public GSTR2B getGstr2bInvoices(Client client, String gstin, String userid, String returnPeriod, String returnType, boolean initial,int fileNum) throws MasterGSTException { 
		logger.debug(CLASSNAME + "getGstr2bInvoices : Begin");
		Long startTime = System.currentTimeMillis();
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("gstin", gstin);
		queryStringMap.put("email", iHubEmail);
		queryStringMap.put("rtnprd", returnPeriod);
		if(fileNum > 0) {
			queryStringMap.put("filenum", fileNum);
		}
		
		try {
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			logger.debug(CLASSNAME + "\tgetGstr2bInvoices : queryStringMap {}", queryStringMap);
			logger.debug(CLASSNAME + "\tgetGstr2bInvoices : gstnHeader {}", gstnHeader);
			String requestUrl = String.format("%s/%s", iHubUrl, "/gstr2b/all");
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			logger.debug(CLASSNAME + "\tgetGstr2bInvoices : jsonResponse {}", jsonResponse.toString());
			logger.debug(CLASSNAME + "\tgetGstr2bInvoices : jsonResponse.getBody() {}", jsonResponse.getBody());
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			GSTR2BResponse response = mapper.readValue(jsonResponse.getBody().toString(), GSTR2BResponse.class);
			/*String json = getGstr2bJson();
			if(fileNum == 1) {
				json = getGstr2bJsonfc1();
			}else if(fileNum == 2) {
				json = getGstr2bJsonfc2();
			}
			GSTR2BResponse response = mapper.readValue(json, GSTR2BResponse.class);*/
			logger.info(CLASSNAME + "syncInvoiceData : response {}", response);
						
			logger.debug(CLASSNAME + "returnSummary : Response Code {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "{} returnSummary : Duration {}", returnType, (endTime - startTime));
			if(initial && NullUtil.isNotEmpty(response) 
					&& NullUtil.isNotEmpty(response.getStatuscd()) 
					&& response.getStatuscd().equals("0")
					&& NullUtil.isNotEmpty(response.getError())
					&& NullUtil.isNotEmpty(response.getError().getErrorcd())
					&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
				performRefreshToken(gstnHeader);
				return getGstr2bInvoices(client, gstin, userid, returnPeriod, returnType, false,fileNum);
				
			}
			return response.getGstr2b();
		} catch (Exception e) {
			logger.error(CLASSNAME + "returnSummary : ERROR", e);
		}
		logger.debug(CLASSNAME + "returnSummary : End");
		return null;
	}
		
		
		@Override
		public GSTR2BResponse getGstr2bInvoice(Client client, String gstin, String userid,String returnPeriod, String returnType,  boolean initial,int fileNum) throws MasterGSTException { 
			logger.debug(CLASSNAME + "getGstr2bInvoices : Begin");
			Long startTime = System.currentTimeMillis();
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("rtnprd", returnPeriod);
			
			if(fileNum > 0) {
				queryStringMap.put("filenum", fileNum);
			}
			
			try {
				Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
				logger.debug(CLASSNAME + "\tgetGstr2bInvoices : queryStringMap {}", queryStringMap);
				logger.debug(CLASSNAME + "\tgetGstr2bInvoices : gstnHeader {}", gstnHeader);
				String requestUrl = String.format("%s/%s", iHubUrl, "/gstr2b/all");
				
				HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
						.queryString(queryStringMap)
						.headers(gstnHeader).asJson();
				logger.debug(CLASSNAME + "\tgetGstr2bInvoices : jsonResponse {}", jsonResponse.toString());
				logger.debug(CLASSNAME + "\tgetGstr2bInvoices : jsonResponse.getBody() {}", jsonResponse.getBody());
				if (jsonResponse.getStatus() == 200) {
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				}
				GSTR2BResponse response = mapper.readValue(jsonResponse.getBody().toString(), GSTR2BResponse.class);
				/*String json = getGstr2bJson();
				if(fileNum == 1) {
					json = getGstr2bJsonfc1();
				}else if(fileNum == 2) {
					json = getGstr2bJsonfc2();
				}
				GSTR2BResponse response = mapper.readValue(json, GSTR2BResponse.class);*/
				logger.info(CLASSNAME + "syncInvoiceData : response {}", response);
							
				logger.debug(CLASSNAME + "returnSummary : Response Code {}", response.getStatuscd());
				Long endTime = System.currentTimeMillis();
				logger.debug(CLASSNAME + "{} returnSummary : Duration {}", returnType, (endTime - startTime));
				if(initial && NullUtil.isNotEmpty(response) 
						&& NullUtil.isNotEmpty(response.getStatuscd()) 
						&& response.getStatuscd().equals("0")
						&& NullUtil.isNotEmpty(response.getError())
						&& NullUtil.isNotEmpty(response.getError().getErrorcd())
						&& (response.getError().getErrorcd().equals("RET11402") || response.getError().getErrorcd().equals("RET11401"))) {
					performRefreshToken(gstnHeader);
					return getGstr2bInvoice(client, gstin, userid, returnPeriod, returnType, false,fileNum);
					
				} else if(NullUtil.isNotEmpty(response.getStatuscd())) {
					return response;
				}
				
			} catch (Exception e) {
				logger.error(CLASSNAME + "returnSummary : ERROR", e);
			}
			logger.debug(CLASSNAME + "returnSummary : End");
			return new GSTR2BResponse();
		}
	private String processRelatederrors(String errorMessage) {
	
			logger.info(CLASSNAME + "processRelatederrors : Begin");
		StringBuilder errorValue = new StringBuilder();
		if(errorMessage.contains("[gstin] not found") || errorMessage.contains("[ctin] not found")) {
			errorValue.append("You are missing GST Number, Please give GSTIN");
		}else if(errorMessage.contains("[fp] not found")) {
			errorValue.append("You are missing Return Period, Please give fp value");
		}else if(errorMessage.contains("[inum] not found")) {
			errorValue.append("You are missing Invoice Number, Please give invoice number");
		}else if(errorMessage.contains("[idt] not found")) {
			errorValue.append("You are missing Invoice Date, Please give invoice Date");
		}else if(errorMessage.contains("[val] not found")) {
			errorValue.append("You are missing Invoice Total Value, Please give Invoice Value");
		}else if(errorMessage.contains("[pos] not found") || errorMessage.contains("#/b2cl/0") || errorMessage.contains("#/b2cla/0")) {
			errorValue.append("You are missing Place Of Supply, Please give POS");
		}else if(errorMessage.contains("[rchrg] not found")) {
			errorValue.append("You are missing Reverse Charge Value, Please give that value");
		}else if(errorMessage.contains("[inv_typ] not found")) {
			errorValue.append("You are missing Invoice Type, Please give Invoice Type");
		}else if(errorMessage.contains("[num] not found")) {
			errorValue.append("You are missing Item number in item details, Please give Item Number");
		}else if(errorMessage.contains("[rt] not found")) {
			errorValue.append("You are missing rate in item details, Please give Item rate");
		}else if(errorMessage.contains("[txval] not found")) {
			errorValue.append("You are missing Taxable Amount in item details, Please give that value");
		}else if(errorMessage.contains("#/b2ba/0/ctin") || errorMessage.contains("#/b2ba/0/ctin")) {
			errorValue.append("GSTIN value is invalid, Please Check that one");
		}else if(errorMessage.contains("#/b2b/0/inv/0/pos") || errorMessage.contains("#/b2ba/0/inv/0/pos") || errorMessage.contains("#/b2cl/0/pos") || errorMessage.contains("#/b2cla/0/pos") || errorMessage.contains("#/b2cs/0/pos") || errorMessage.contains("#/b2csa/0/pos") || errorMessage.contains("#/txpd/0/pos") || errorMessage.contains("#/txpda/0/pos") || errorMessage.contains("#/at/0/pos") || errorMessage.contains("#/ata/0/pos")) {
			errorValue.append("Place Of supply value is invalid, Please Check that one");
		}else if(errorMessage.contains("#/b2b/0/inv/0/idt") || errorMessage.contains("#/b2ba/0/inv/0/idt") || errorMessage.contains("#/b2cl/0/inv/0/idt") || errorMessage.contains("#/b2cla/0/inv/0/idt") || errorMessage.contains("#/exp/0/inv/0/idt") || errorMessage.contains("#/expa/0/inv/0/idt") || errorMessage.contains("#/cdnur/0/inv/0/idt") || errorMessage.contains("#/cdnura/0/inv/0/idt")) {
			errorValue.append("Invoice Date is invalid,Please Check that one");
		}else if(errorMessage.contains("#/b2b/0/inv/0/inum") || errorMessage.contains("#/b2ba/0/inv/0/inum") || errorMessage.contains("#/b2cl/0/inv/0/inum") || errorMessage.contains("#/b2cla/0/inv/0/inum") || errorMessage.contains("#/exp/0/inv/0/inum") || errorMessage.contains("#/expa/0/inv/0/inum") || errorMessage.contains("#/cdnur/0/inv/0/inum") || errorMessage.contains("#/cdnura/0/inv/0/inum")) {
			errorValue.append("Invoice Number is invalid, Please Check that one");
		}else if(errorMessage.contains("#/b2b/0/inv/0/rchrg") || errorMessage.contains("#/b2ba/0/inv/0/rchrg") || errorMessage.contains("#/b2cl/0/inv/0/rchrg")) {
			errorValue.append("Reverse Charge Value is invalid, Please Check that one");
		}else if(errorMessage.contains("#/b2b/0/inv/0/inv_typ") || errorMessage.contains("#/b2ba/0/inv/0/inv_typ") || errorMessage.contains("#/b2cl/0/inv/0/inv_typ")) {
			errorValue.append("Invoice Type is invalid, Please Check that one");
		}else if(errorMessage.contains("#/b2b/0/inv/0/diff_percent") || errorMessage.contains("#/b2ba/0/inv/0/diff_percent") || errorMessage.contains("#/b2cl/0/inv/0/diff_percent") || errorMessage.contains("#/b2cs/0/diff_percent") || errorMessage.contains("#/b2csa/0/diff_percent") || errorMessage.contains("#/exp/0/inv/0/diff_percent") || errorMessage.contains("#/expa/0/inv/0/diff_percent") || errorMessage.contains("#/txpd/0/inv/0/diff_percent") || errorMessage.contains("#/txpda/0/inv/0/diff_percent") || errorMessage.contains("#/at/0/diff_percent") || errorMessage.contains("#/ata/0/diff_percent") || errorMessage.contains("#/cdnur/0/diff_percent") || errorMessage.contains("#/cdnura/0/diff_percent")) {
			errorValue.append("Differential Percentage is invalid, Please Check that one");
		}else if(errorMessage.contains("#/b2b/0/inv/0/etin") || errorMessage.contains("#/b2ba/0/inv/0/etin") || errorMessage.contains("#/b2cl/0/inv/0/etin") || errorMessage.contains("#/b2cla/0/inv/0/etin") || errorMessage.contains("#/b2cs/0/etin") || errorMessage.contains("#/b2csa/0/etin")) {
			errorValue.append("E-commerce GSTIN is invalid, Please Check that one");
		}else if(errorMessage.contains("#/fp")) {
			errorValue.append("Return Period is invalid, Please Check that one");
		}else if(errorMessage.contains("#/gstin")) {
			errorValue.append("GSTIN is invalid, Please Check that one");
		}else if(errorMessage.contains("[oinum] not found")) {
			errorValue.append("You are missing Old Invoice Number, Please give Old invoice number");
		}else if(errorMessage.contains("[oidt] not found")) {
			errorValue.append("You are missing Old Invoice Date, Please give Old invoice Date");
		}else if(errorMessage.contains("#/b2ba/0/inv/0/oinum") || errorMessage.contains("#/b2cla/0/inv/0/oinum") || errorMessage.contains("#/expa/0/inv/0/oinum")) {
			errorValue.append("Old Invoice Number is invalid, Please Check that one");
		}else if(errorMessage.contains("#/b2ba/0/inv/0/oidt") || errorMessage.contains("#/b2cla/0/inv/0/oidt") || errorMessage.contains("#/expa/0/inv/0/oidt")) {
			errorValue.append("Old Invoice Date is invalid, Please Check that one");
		}else if(errorMessage.contains("#/cdnr/0/nt/0/ntty") || errorMessage.contains("#/cdnra/0/nt/0/ntty") || errorMessage.contains("#/cdnur/0/nt/0/ntty") || errorMessage.contains("#/cdnura/0/nt/0/ntty")) {
			errorValue.append("Note Type is invalid, Please Check that one");
		}else if(errorMessage.contains("[ntty] not found")) {
			errorValue.append("You are missing Note Type, Please give that value");
		}else if(errorMessage.contains("#/cdnr/0/nt/0/nt_num") || errorMessage.contains("#/cdnra/0/nt/0/nt_num") || errorMessage.contains("#/cdnur/0/nt/0/nt_num") || errorMessage.contains("#/cdnura/0/nt/0/nt_num")) {
			errorValue.append("Note Number is invalid, Please Check that one");
		}else if(errorMessage.contains("[nt_num] not found")) {
			errorValue.append("You are missing Note Number, Please give that value");
		}else if(errorMessage.contains("[nt_dt] not found")) {
			errorValue.append("You are missing Note Date, Please give that value");
		}else if(errorMessage.contains("#/cdnr/0/nt/0/nt_dt") || errorMessage.contains("#/cdnra/0/nt/0/nt_dt") || errorMessage.contains("#/cdnur/0/nt/0/nt_dt") || errorMessage.contains("#/cdnura/0/nt/0/nt_dt")) {
			errorValue.append("Note Date is invalid, Please Check that one");
		}else if(errorMessage.contains("#/cdnr/0/nt/0/pGst") || errorMessage.contains("#/cdnra/0/nt/0/pGst") || errorMessage.contains("#/cdnur/0/nt/0/pGst") || errorMessage.contains("#/cdnura/0/nt/0/pGst")) {
			errorValue.append("Pre GST is invalid, Please Check that one");
		}else if(errorMessage.contains("[pGst] not found")) {
			errorValue.append("You are missing Pre GST Value, Please give that value");
		}else if(errorMessage.contains("#/cdnra/0/nt/0/ont_num") || errorMessage.contains("#/cdnura/0/nt/0/ont_num")) {
			errorValue.append("Old Note Number is invalid, Please Check that one");
		}else if(errorMessage.contains("[ont_num] not found")) {
			errorValue.append("You are missing Old Note Number, Please give that value");
		}else if(errorMessage.contains("[ont_dt] not found")) {
			errorValue.append("You are missing Old Note Date, Please give that value");
		}else if(errorMessage.contains("#/cdnra/0/nt/0/ont_dt") || errorMessage.contains("#/cdnura/0/nt/0/ont_num")) {
			errorValue.append("Old Note Date is invalid, Please Check that one");
		}else if(errorMessage.contains("[sply_ty] not found")) {
			errorValue.append("You are missing Supply Type value, Please give that value");
		}else if(errorMessage.contains("[typ] not found")) {
			errorValue.append("You are missing Type Value, Please give that value");
		}else if(errorMessage.contains("#/b2cs/0/sply_ty") || errorMessage.contains("#/b2csa/0/sply_ty") || errorMessage.contains("#/txpd/0/sply_ty") || errorMessage.contains("#/txpda/0/sply_ty") || errorMessage.contains("#/at/0/sply_ty") || errorMessage.contains("#/ata/0/sply_ty")) {
			errorValue.append("Supply Type value is Invalid, Please Check that value");
		}else if(errorMessage.contains("#/b2cs/0/typ") || errorMessage.contains("#/b2csa/0/typ") || errorMessage.contains("#/cdnur/0/typ") || errorMessage.contains("#/cdnura/0/typ")) {
			errorValue.append("Type value is Invalid, Please Check that value");
		}else if(errorMessage.contains("[omon] not found")) {
			errorValue.append("You are missing old Financial Year value, Please give that value");
		}else if(errorMessage.contains("#/b2csa/0/omon") || errorMessage.contains("#/ata/0/pos")) {
			errorValue.append("old Financial Year value is invalid, Please Check that value");
		}else if(errorMessage.contains("sbpcode")) {
			errorValue.append("You are missing Port Code, Please Give that value");
		}else if(errorMessage.contains("#/exp/0/inv/0/sbpcode")) {
			errorValue.append("Port Code is invalid, Please Check that value");
		}else if(errorMessage.contains("sbnum")) {
			errorValue.append("You are missing Shipping Billing No, Please Give that value");
		}else if(errorMessage.contains("#/exp/0/inv/0/sbnum") || errorMessage.contains("#/exp/0/inv/0/sbnum")) {
			errorValue.append("Shipping Billing No is invalid, Please Check that value");
		}else if(errorMessage.contains("sbdt")) {
			errorValue.append("You are missing Shipping Billing Date, Please Give that value");
		}else if(errorMessage.contains("#/exp/0/inv/0/sbdt") || errorMessage.contains("#/exp/0/inv/0/sbdt")) {
			errorValue.append("Shipping Billing Date is invalid, Please Check that value");
		}else if(errorMessage.contains("#/exp/0/inv/0")) {
			errorValue.append("Please check Your Invoice Details,You Are missing mandatory Values");
		}else if(errorMessage.contains("exp_typ")) {
			errorValue.append("You are missing Export Type Value , Please Give that value");
		}else if(errorMessage.contains("#/exp/exp_typ") || errorMessage.contains("#/expa/exp_typ")) {
			errorValue.append("You are missing Export Type Value , Please Give that value");
		}else if(errorMessage.contains("#/hsn")) {
			errorValue.append("Please check Your HSN Details,You Are missing mandatory Values");
		}else if(errorMessage.contains("#/nil")) {
			errorValue.append("Please Check your Details, You Are missing mandatory Values");
		}else if(errorMessage.contains("adv_amt")) {
			errorValue.append("You are missing Advance Amount , Please Give that value");
		}else if(errorMessage.contains("#/doc_issue")) {
			errorValue.append("Please Check your Document Details , You Are missing mandatory Values");
		}else {
			errorValue.append(errorMessage);
		}
		logger.info(CLASSNAME + "processRelatederrors Error Message ::"+errorValue.toString());
		return errorValue.toString();
	}

	@Override
	public GenerateIRNResponse getIRNByDocDetails(String gstin, String ipAddress,EinvoiceConfigurations configDetails,HeaderKeys headerKeys,String invoiceNumber,String invoiceDate,String invtype) {
		String authToken="";
		String uname="";
		String pwd="";
		if(isNotEmpty(configDetails)) {
			uname=configDetails.getUserName();
			pwd=configDetails.getPassword();
		}
	if(isNotEmpty(headerKeys) && isNotEmpty(headerKeys.getAuthtoken())) {
		authToken = headerKeys.getAuthtoken();
	}
	Map<String, String> gstnHeader = Maps.newHashMap();
	gstnHeader.put("client_id", iHubEinvClientId);
	gstnHeader.put("client_secret", iHubEinvClientSecret);
	gstnHeader.put("ip_address", ipAddress);
	gstnHeader.put("gstin", gstin);
	gstnHeader.put("username",uname);
	gstnHeader.put("password",pwd);
	gstnHeader.put("auth-token",authToken);
	gstnHeader.put("docnum",invoiceNumber);
	gstnHeader.put("docdate",invoiceDate);
	Map<String, Object> queryStringMap = Maps.newHashMap();
	queryStringMap.put("email", iHubEmail);
	queryStringMap.put("param1", invtype);
	String requestUrl = String.format("%s/%s", iHubUrl, "einvoice/type/GETIRNBYDOCDETAILS/version/V1_03");
		//logger.info("requestUrl--------------------"+requestUrl);
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			logger.info("getGSTINDetailsByDate response body---------------------"+jsonResponse.getBody().toString());
			GenerateIRNResponse response = mapper.readValue(jsonResponse.getBody().toString(), GenerateIRNResponse.class);
			logger.info("iHubConsumerService getGSTINDetailsByDate response--------------------"+response);
			logger.debug(CLASSNAME + "getGSTINDetailsByDate : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "getGSTINDetailsByDate : Response {}", response);
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getGSTINDetailsByDate : ERROR", e);
		}
		
		logger.debug(CLASSNAME + "getGSTINDetailsByEwayBillno : End");
		return null;
	}
	
	@Override
	public GSTR8GetResponse getGSTR8Details(final Client client, final String returnType, final String gstin, final String retPeriod) {
		logger.debug(CLASSNAME + "getGSTR8Details : Begin");
		Long startTime = System.currentTimeMillis();
		String url = "/"+returnType.toLowerCase()+"/tcs";
		String requestUrl = String.format("%s/%s", iHubUrl, url);
		
		try {
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			
			logger.debug(CLASSNAME + "getGSTR8Details : gstnHeader {}", gstnHeader);
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("retperiod", retPeriod);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			GSTR8GetResponse response = mapper.readValue(jsonResponse.getBody().toString(), GSTR8GetResponse.class);
			logger.debug(CLASSNAME + "getGSTR8Details : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "getGSTR8Details : Response getStatuscd {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "getGSTR9Details : Duration {}", (endTime - startTime));
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getGSTR9Details : ERROR", e);
		}
		logger.debug(CLASSNAME + "getGSTR9Details : End");
		return new GSTR8GetResponse();
	}
	@Override
	public GSTR4AnnualCMPResponse getAnnualCmp(Client client, String gstin, String retPeriod) {
		logger.debug(CLASSNAME + "getAnnualCmp : Begin");
		Long startTime = System.currentTimeMillis();
		String requestUrl = String.format("%s/%s", iHubUrl, "/gstr4annual/tdscmp");
		try {
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			
			logger.debug(CLASSNAME + "getAnnualCmp : gstnHeader {}", gstnHeader);
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("retperiod", retPeriod);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			 
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			GSTR4AnnualCMPResponse response = mapper.readValue(jsonResponse.getBody().toString(), GSTR4AnnualCMPResponse.class);
			//GSTR4AnnualCMPResponse response = mapper.readValue(jsonResponse.getBody().toString(), GSTR4AnnualCMPResponse.class);
			
			//String json = gstr4AnnualReturnsCmp8JSon();
			//GSTR4AnnualCMPResponse response = mapper.readValue(json, GSTR4AnnualCMPResponse.class);
			
			logger.debug(CLASSNAME + "getAnnualCmp : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "getAnnualCmp : Response getStatuscd {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "getAnnualCmp : Duration {}", (endTime - startTime));
			if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getAnnualCmp : ERROR", e);
		}
		logger.debug(CLASSNAME + "getAnnualCmp : End");
		return null;
	}
	@Override
	public Response getGSTR3BLiablitiesAutoCalcDetails(Client client, String gstin,String retPeriod) {
		logger.debug(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : Begin");
		Long startTime = System.currentTimeMillis();
		String requestUrl = String.format("%s/%s", iHubUrl, "/gstr3b/autoliab");
		try {
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			
			logger.debug(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : gstnHeader {}", gstnHeader);
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("gstin", gstin);
			queryStringMap.put("retperiod", retPeriod);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			 
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			Response response = mapper.readValue(jsonResponse.getBody().toString(), Response.class);
			
			/*String json = gstr3bJSon();
			System.out.println(json.toString());
			Response response = mapper.readValue(json, Response.class);*/
			
			logger.debug(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : Response getStatuscd {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : Duration {}", (endTime - startTime));
			if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : ERROR", e);
		}
		logger.debug(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : End");
		return null;
	}
	@Override
	public GSTR1AutoLiabilityResponse getGSTR1AutoLiabilityDetails(Client client, String gstnnumber, String retPeriod) {
		logger.debug(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : Begin");
		Long startTime = System.currentTimeMillis();
		String requestUrl = String.format("%s/%s", iHubUrl, "/gstr3b/autoliab");
		try {
			Map<String, String> gstnHeader = getGSTTransactionHeader(client.getStatename(), client.getGstname());
			
			logger.debug(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : gstnHeader {}", gstnHeader);
			Map<String, Object> queryStringMap = Maps.newHashMap();
			queryStringMap.put("email", iHubEmail);
			queryStringMap.put("gstin", gstnnumber);
			queryStringMap.put("retperiod", retPeriod);
			
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			 
			if (jsonResponse.getStatus() == 200) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			}
			GSTR1AutoLiabilityResponse response = mapper.readValue(jsonResponse.getBody().toString(), GSTR1AutoLiabilityResponse.class);
			
			/*String json = newGstr3bJSon();
			System.out.println(json.toString());
			GSTR1AutoLiabilityResponse response = mapper.readValue(json, GSTR1AutoLiabilityResponse.class);*/
			
			logger.debug(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : Response getStatuscd {}", response.getStatuscd());
			Long endTime = System.currentTimeMillis();
			logger.debug(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : Duration {}", (endTime - startTime));
			if(NullUtil.isNotEmpty(response.getStatuscd())) {
				return response;
			}
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : ERROR", e);
		}
		logger.debug(CLASSNAME + "getGSTR3BLiablitiesAutoCalcDetails : End");
		return null;
	}
	
	@Override
	public TransGSTINResponse getTransinDetails(String transin, String gstin, String ipAddress) {
		logger.debug(CLASSNAME + "getTransinDetails : Begin");
		Map<String, String> gstnHeader = Maps.newHashMap();
		gstnHeader.put("client_id", iHubEnoClientId);
		gstnHeader.put("client_secret", iHubEnoClientSecret);
		gstnHeader.put("ip_address", ipAddress);
		gstnHeader.put("gstin", gstin);
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("trn_no",transin);
		queryStringMap.put("email", iHubEmail);
		String requestUrl = String.format("%s/%s", iHubUrl, "ewaybillapi/v1.03/ewayapi/gettransporterdetails");
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get(requestUrl)
					.queryString(queryStringMap)
					.headers(gstnHeader).asJson();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			logger.debug("getTransinDetails response body---------------------"+jsonResponse.getBody().toString());
			TransGSTINResponse response = mapper.readValue(jsonResponse.getBody().toString(), TransGSTINResponse.class);
			logger.debug("iHubConsumerService getTransinDetails response--------------------"+response);
			logger.debug(CLASSNAME + "getTransinDetails : Response Header {}", response.getHeader());
			logger.debug(CLASSNAME + "getTransinDetails : Response {}", response);
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "getTransinDetails : ERROR", e);
		}
		
		logger.debug(CLASSNAME + "getTransinDetails : End");
		return new TransGSTINResponse();
	}
@Override
public ExtendValidityResponse extendEwaybillValidity(String gstnnumber, String hostAddress, ExtendValidity ewaybill) {
	logger.debug(CLASSNAME + "extendEwaybillValidity : Begin");
	Map<String, Object> queryStringMap = Maps.newHashMap();
	queryStringMap.put("email", iHubEmail);
	String requestUrl = String.format("%s/%s", iHubUrl, "ewaybillapi/v1.03/ewayapi/extendvalidity");
	try {
		//mapper.setSerializationInclusion(Include.ALWAYS);
		mapper.setSerializationInclusion(Include.NON_NULL);
		String strUpdateRequest = mapper.writeValueAsString(ewaybill);
		logger.info("strUpdateRequest----------"+strUpdateRequest);
		HttpResponse<String> httpResponse = Unirest
                .post(requestUrl)
                .queryString(queryStringMap)
                .header("client_id", iHubEnoClientId)
                .header("client_secret", iHubEnoClientSecret)
                .header("ip_address",hostAddress)
                .header("gstin",gstnnumber)
              .header("Content-Type", "application/json")
                .body(strUpdateRequest)
                .asString();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String resBody=httpResponse.getBody().toString();
		logger.info("extendEwaybillValidity response body---------------------"+resBody.toString());
		ExtendValidityResponse response = mapper.readValue(resBody.toString(), ExtendValidityResponse.class);
		logger.info("iHubConsumerService extendEwaybillValidity response--------------------"+response);
		logger.info(CLASSNAME + "extendEwaybillValidity : Response Header {}", response.getHeader());
		logger.info(CLASSNAME + "extendEwaybillValidity : Response {}", response);
		return response;
	} catch (Exception e) {
		logger.error(CLASSNAME + "extendEwaybillValidity : ERROR", e);
	}
	
	logger.info(CLASSNAME + "extendEwaybillValidity : End");
	return new ExtendValidityResponse();
}
	@Override
	public EwayBillRejectResponse rejectEwayBill(String gstnnumber, String hostAddress, CancelEwayBill ewbno) {
		logger.debug(CLASSNAME + "rejectEwayBill : Begin");
		Map<String, Object> queryStringMap = Maps.newHashMap();
		queryStringMap.put("email", iHubEmail);
		String requestUrl = String.format("%s/%s", iHubUrl, "ewaybillapi/v1.03/ewayapi/rejewb");
		try {
			//mapper.setSerializationInclusion(Include.ALWAYS);
			mapper.setSerializationInclusion(Include.NON_NULL);
			String strUpdateRequest = mapper.writeValueAsString(ewbno);
			logger.info("strUpdateRequest----------"+strUpdateRequest);
			HttpResponse<String> httpResponse = Unirest
	                .post(requestUrl)
	                .queryString(queryStringMap)
	                .header("client_id", iHubEnoClientId)
	                .header("client_secret", iHubEnoClientSecret)
	                .header("ip_address",hostAddress)
	                .header("gstin",gstnnumber)
	              .header("Content-Type", "application/json")
	                .body(strUpdateRequest)
	                .asString();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			String resBody=httpResponse.getBody().toString();
			logger.info("rejectEwayBill response body---------------------"+resBody.toString());
			EwayBillRejectResponse response = mapper.readValue(resBody.toString(), EwayBillRejectResponse.class);
			logger.info("iHubConsumerService rejectEwayBill response--------------------"+response);
			logger.info(CLASSNAME + "rejectEwayBill : Response Header {}", response.getHeader());
			logger.info(CLASSNAME + "rejectEwayBill : Response {}", response);
			return response;
		} catch (Exception e) {
			logger.error(CLASSNAME + "rejectEwayBill : ERROR", e);
		}
		
		logger.info(CLASSNAME + "rejectEwayBill : End");
		return new EwayBillRejectResponse();
	}
	
	private String getGstr2bJson() {
		return "{"+
				
				"\"data\": {\n" + 
				"    \"chksum\": \"a5349af57170140d434cd2d0d15508ce60e5c04209403f51af3dd85ea43ce397\",\n" + 
				"    \"data\": {\n" + 
				"      \"gstin\": \"09AABCP8801C1ZN\",\n" + 
				"      \"rtnprd\": \"052021\",\n" + 
				"      \"version\": \"1.0\",\n" + 
				"      \"gendt\": \"29-06-2021\",\n" + 
				"      \"itcsumm\": {\n" + 
				"        \"itcavl\": {\n" + 
				"          \"nonrevsup\": {\n" + 
				"            \"igst\": 6140.31,\n" + 
				"            \"cgst\": 6999.93,\n" + 
				"            \"sgst\": 6999.93,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"b2b\": {\n" + 
				"              \"igst\": 6140.31,\n" + 
				"              \"cgst\": 6999.93,\n" + 
				"              \"sgst\": 6999.93,\n" + 
				"              \"cess\": 0\n" + 
				"            }\n" + 
				"          },\n" + 
				"          \"revsup\": {\n" + 
				"            \"igst\": 2628,\n" + 
				"            \"cgst\": 1603.01,\n" + 
				"            \"sgst\": 1603.01,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"b2b\": {\n" + 
				"              \"igst\": 2628,\n" + 
				"              \"cgst\": 1603.01,\n" + 
				"              \"sgst\": 1603.01,\n" + 
				"              \"cess\": 0\n" + 
				"            }\n" + 
				"          }\n" + 
				"        },\n" + 
				"        \"itcunavl\": {\n" + 
				"          \"isdsup\": {\n" + 
				"            \"igst\": 1249,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"isd\": {\n" + 
				"              \"igst\": 1249,\n" + 
				"              \"cgst\": 0,\n" + 
				"              \"sgst\": 0,\n" + 
				"              \"cess\": 0\n" + 
				"            }\n" + 
				"          }\n" + 
				"        }\n" + 
				"      },\n" + 
				"      \"cpsumm\": {\n" + 
				"        \"b2b\": [\n" + 
				"          {\n" + 
				"            \"ctin\": \"08AUOPG8995N1ZF\",\n" + 
				"            \"trdnm\": \"NEW HINDUSTAN ROADWAYS\",\n" + 
				"            \"supprd\": \"032021\",\n" + 
				"            \"supfildt\": \"23-06-2021\",\n" + 
				"            \"txval\": 52555,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 2628,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 2\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AAACR6529A1ZL\",\n" + 
				"            \"trdnm\": \"M/S RAM LAL RAM CHANDER (INDIA)  LTD.\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"26-06-2021\",\n" + 
				"            \"txval\": 111766,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 6705.96,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 6705.96,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AAACT7966R1Z9\",\n" + 
				"            \"trdnm\": \"TCI FREIGHT (A DIV OF TRANSPORT CORPORATION OF INDIA LTD)\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"19-06-2021\",\n" + 
				"            \"txval\": 64120,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 1603.01,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 1603.01,\n" + 
				"            \"ttldocs\": 5\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AACCN7214Q1ZY\",\n" + 
				"            \"trdnm\": \"M/S NUZIVEEDU SEEDS LIMITED\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"11-06-2021\",\n" + 
				"            \"txval\": 3266.29,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 293.97,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 293.97,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09CUVPM7182C1Z0\",\n" + 
				"            \"trdnm\": \"NAV BHARAT ROADWAYS\",\n" + 
				"            \"supprd\": \"032021\",\n" + 
				"            \"supfildt\": \"24-06-2021\",\n" + 
				"            \"txval\": 14030,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09CUVPM7182C1Z0\",\n" + 
				"            \"trdnm\": \"NAV BHARAT ROADWAYS\",\n" + 
				"            \"supprd\": \"122020\",\n" + 
				"            \"supfildt\": \"24-06-2021\",\n" + 
				"            \"txval\": 20565,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 2\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"27AACFH0517K1ZK\",\n" + 
				"            \"trdnm\": \"HYPACK INDUSTRIES\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"10-06-2021\",\n" + 
				"            \"txval\": 20500,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 3690,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AABCP8801C1ZQ\",\n" + 
				"            \"trdnm\": \"PRABHAT AGRI BIOTECH LTD\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"20-06-2021\",\n" + 
				"            \"txval\": 8418.38,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 1515.31,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 2\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AALCM6590K1ZS\",\n" + 
				"            \"trdnm\": \"MAGNUS CADEAUX INDIA PRIVATE LIMITED\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"22-06-2021\",\n" + 
				"            \"txval\": 18700,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 935,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 2\n" + 
				"          }\n" + 
				"        ],\n" + 
				"        \"isd\": [\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AABCP8801C2ZP\",\n" + 
				"            \"trdnm\": \"PRABHAT AGRI BIOTECH LTD\",\n" + 
				"            \"supprd\": \"042021\",\n" + 
				"            \"supfildt\": \"31-05-2021\",\n" + 
				"            \"doctyp\": \"ISDI\",\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 1249,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          }\n" + 
				"        ]\n" + 
				"      },\n" + 
				"      \"fc\": 2\n" +
				"    }\n" + 
				"  }"+
				"}";
	}
	
	private String getGstr2bJsonfc1() {
		return "{"+
				
				"\"data\": {\n" + 
				"    \"chksum\": \"a5349af57170140d434cd2d0d15508ce60e5c04209403f51af3dd85ea43ce397\",\n" + 
				"    \"data\": {\n" + 
				"      \"gstin\": \"09AABCP8801C1ZN\",\n" + 
				"      \"rtnprd\": \"052021\",\n" + 
				"      \"version\": \"1.0\",\n" + 
				"      \"gendt\": \"29-06-2021\",\n" + 
				"      \"itcsumm\": {\n" + 
				"        \"itcavl\": {\n" + 
				"          \"nonrevsup\": {\n" + 
				"            \"igst\": 6140.31,\n" + 
				"            \"cgst\": 6999.93,\n" + 
				"            \"sgst\": 6999.93,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"b2b\": {\n" + 
				"              \"igst\": 6140.31,\n" + 
				"              \"cgst\": 6999.93,\n" + 
				"              \"sgst\": 6999.93,\n" + 
				"              \"cess\": 0\n" + 
				"            }\n" + 
				"          },\n" + 
				"          \"revsup\": {\n" + 
				"            \"igst\": 2628,\n" + 
				"            \"cgst\": 1603.01,\n" + 
				"            \"sgst\": 1603.01,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"b2b\": {\n" + 
				"              \"igst\": 2628,\n" + 
				"              \"cgst\": 1603.01,\n" + 
				"              \"sgst\": 1603.01,\n" + 
				"              \"cess\": 0\n" + 
				"            }\n" + 
				"          }\n" + 
				"        },\n" + 
				"        \"itcunavl\": {\n" + 
				"          \"isdsup\": {\n" + 
				"            \"igst\": 1249,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"isd\": {\n" + 
				"              \"igst\": 1249,\n" + 
				"              \"cgst\": 0,\n" + 
				"              \"sgst\": 0,\n" + 
				"              \"cess\": 0\n" + 
				"            }\n" + 
				"          }\n" + 
				"        }\n" + 
				"      },\n" + 
				"      \"cpsumm\": {\n" + 
				"        \"b2b\": [\n" + 
				"          {\n" + 
				"            \"ctin\": \"08AUOPG8995N1ZF\",\n" + 
				"            \"trdnm\": \"NEW HINDUSTAN ROADWAYS\",\n" + 
				"            \"supprd\": \"032021\",\n" + 
				"            \"supfildt\": \"23-06-2021\",\n" + 
				"            \"txval\": 52555,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 2628,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 2\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AAACR6529A1ZL\",\n" + 
				"            \"trdnm\": \"M/S RAM LAL RAM CHANDER (INDIA)  LTD.\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"26-06-2021\",\n" + 
				"            \"txval\": 111766,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 6705.96,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 6705.96,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AAACT7966R1Z9\",\n" + 
				"            \"trdnm\": \"TCI FREIGHT (A DIV OF TRANSPORT CORPORATION OF INDIA LTD)\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"19-06-2021\",\n" + 
				"            \"txval\": 64120,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 1603.01,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 1603.01,\n" + 
				"            \"ttldocs\": 5\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AACCN7214Q1ZY\",\n" + 
				"            \"trdnm\": \"M/S NUZIVEEDU SEEDS LIMITED\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"11-06-2021\",\n" + 
				"            \"txval\": 3266.29,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 293.97,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 293.97,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09CUVPM7182C1Z0\",\n" + 
				"            \"trdnm\": \"NAV BHARAT ROADWAYS\",\n" + 
				"            \"supprd\": \"032021\",\n" + 
				"            \"supfildt\": \"24-06-2021\",\n" + 
				"            \"txval\": 14030,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09CUVPM7182C1Z0\",\n" + 
				"            \"trdnm\": \"NAV BHARAT ROADWAYS\",\n" + 
				"            \"supprd\": \"122020\",\n" + 
				"            \"supfildt\": \"24-06-2021\",\n" + 
				"            \"txval\": 20565,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 2\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"27AACFH0517K1ZK\",\n" + 
				"            \"trdnm\": \"HYPACK INDUSTRIES\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"10-06-2021\",\n" + 
				"            \"txval\": 20500,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 3690,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AABCP8801C1ZQ\",\n" + 
				"            \"trdnm\": \"PRABHAT AGRI BIOTECH LTD\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"20-06-2021\",\n" + 
				"            \"txval\": 8418.38,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 1515.31,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 2\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AALCM6590K1ZS\",\n" + 
				"            \"trdnm\": \"MAGNUS CADEAUX INDIA PRIVATE LIMITED\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"22-06-2021\",\n" + 
				"            \"txval\": 18700,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 935,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 2\n" + 
				"          }\n" + 
				"        ],\n" + 
				"        \"isd\": [\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AABCP8801C2ZP\",\n" + 
				"            \"trdnm\": \"PRABHAT AGRI BIOTECH LTD\",\n" + 
				"            \"supprd\": \"042021\",\n" + 
				"            \"supfildt\": \"31-05-2021\",\n" + 
				"            \"doctyp\": \"ISDI\",\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 1249,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          }\n" + 
				"        ]\n" + 
				"      },\n" + 
				"      \"docdata\": {\n" + 
				"        \"b2b\": [\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AABCP8801C1ZQ\",\n" + 
				"            \"trdnm\": \"PRABHAT AGRI BIOTECH LTD\",\n" + 
				"            \"supfildt\": \"20-06-2021\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"3000000019\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"31-05-2021\",\n" + 
				"                \"val\": 104,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 18,\n" + 
				"                    \"txval\": 88.13,\n" + 
				"                    \"igst\": 15.86\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"4110001080\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"05-05-2021\",\n" + 
				"                \"val\": 9829.7,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 18,\n" + 
				"                    \"txval\": 8330.25,\n" + 
				"                    \"igst\": 1499.45,\n" + 
				"                    \"cgst\": 0,\n" + 
				"                    \"sgst\": 0,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ],\n" + 
				"                \"srctyp\": \"E-Invoice\",\n" + 
				"                \"irn\": \"2f6707c2864f85768e711cdb2dce79e917f0e20eb7c8e0ac4280fbfef5d7dcdb\",\n" + 
				"                \"irngendate\": \"05-05-2021\"\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"08AUOPG8995N1ZF\",\n" + 
				"            \"trdnm\": \"NEW HINDUSTAN ROADWAYS\",\n" + 
				"            \"supfildt\": \"23-06-2021\",\n" + 
				"            \"supprd\": \"032021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"812\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"23-02-2021\",\n" + 
				"                \"val\": 16100,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 16100,\n" + 
				"                    \"igst\": 805,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"822\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"04-03-2021\",\n" + 
				"                \"val\": 36455,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 36455,\n" + 
				"                    \"igst\": 1823,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AALCM6590K1ZS\",\n" + 
				"            \"trdnm\": \"MAGNUS CADEAUX INDIA PRIVATE LIMITED\",\n" + 
				"            \"supfildt\": \"22-06-2021\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"2021-2022/2/65\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"10-05-2021\",\n" + 
				"                \"val\": 16632,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 500,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 15840,\n" + 
				"                    \"igst\": 792,\n" + 
				"                    \"cgst\": 0,\n" + 
				"                    \"sgst\": 0,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"2021-2022/2/88\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"20-05-2021\",\n" + 
				"                \"val\": 3003,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 500,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 2860,\n" + 
				"                    \"igst\": 143,\n" + 
				"                    \"cgst\": 0,\n" + 
				"                    \"sgst\": 0,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AAACT7966R1Z9\",\n" + 
				"            \"trdnm\": \"TCI FREIGHT (A DIV OF TRANSPORT CORPORATION OF INDIA LTD)\",\n" + 
				"            \"supfildt\": \"19-06-2021\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"3209202303\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"12-05-2021\",\n" + 
				"                \"val\": 9520,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 9520,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 238,\n" + 
				"                    \"sgst\": 238,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"3209202414\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"14-05-2021\",\n" + 
				"                \"val\": 8725,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 8725,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 218.13,\n" + 
				"                    \"sgst\": 218.13,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"3209202415\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"14-05-2021\",\n" + 
				"                \"val\": 8725,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 8725,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 218.13,\n" + 
				"                    \"sgst\": 218.13,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"3209202654\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"20-05-2021\",\n" + 
				"                \"val\": 5900,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 5900,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 147.5,\n" + 
				"                    \"sgst\": 147.5,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"3209202820\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"24-05-2021\",\n" + 
				"                \"val\": 31250,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 31250,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 781.25,\n" + 
				"                    \"sgst\": 781.25,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09CUVPM7182C1Z0\",\n" + 
				"            \"trdnm\": \"NAV BHARAT ROADWAYS\",\n" + 
				"            \"supfildt\": \"24-06-2021\",\n" + 
				"            \"supprd\": \"032021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"1608\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"31-01-2021\",\n" + 
				"                \"val\": 14030,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 0,\n" + 
				"                    \"txval\": 14030,\n" + 
				"                    \"cgst\": 0,\n" + 
				"                    \"sgst\": 0,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"27AACFH0517K1ZK\",\n" + 
				"            \"trdnm\": \"HYPACK INDUSTRIES\",\n" + 
				"            \"supfildt\": \"10-06-2021\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"00296/21-22\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"27-05-2021\",\n" + 
				"                \"val\": 24190,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1801,\n" + 
				"                    \"rt\": 18,\n" + 
				"                    \"txval\": 20500,\n" + 
				"                    \"igst\": 3690,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AACCN7214Q1ZY\",\n" + 
				"            \"trdnm\": \"M/S NUZIVEEDU SEEDS LIMITED\",\n" + 
				"            \"supfildt\": \"11-06-2021\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"1110003992\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"03-05-2021\",\n" + 
				"                \"val\": 3854.23,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 18,\n" + 
				"                    \"txval\": 3266.29,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 293.97,\n" + 
				"                    \"sgst\": 293.97,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ],\n" + 
				"                \"srctyp\": \"E-Invoice\",\n" + 
				"                \"irn\": \"30c65bdb6be7a36e8dbb93664de888138743d2e63df3dcde8fdf64726349bd44\",\n" + 
				"                \"irngendate\": \"03-05-2021\"\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09CUVPM7182C1Z0\",\n" + 
				"            \"trdnm\": \"NAV BHARAT ROADWAYS\",\n" + 
				"            \"supfildt\": \"24-06-2021\",\n" + 
				"            \"supprd\": \"122020\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"1426\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"31-10-2020\",\n" + 
				"                \"val\": 19205,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 0,\n" + 
				"                    \"txval\": 19205,\n" + 
				"                    \"cgst\": 0,\n" + 
				"                    \"sgst\": 0,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"1492\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"30-11-2020\",\n" + 
				"                \"val\": 1360,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 0,\n" + 
				"                    \"txval\": 1360,\n" + 
				"                    \"cgst\": 0,\n" + 
				"                    \"sgst\": 0,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AAACR6529A1ZL\",\n" + 
				"            \"trdnm\": \"M/S RAM LAL RAM CHANDER (INDIA)  LTD.\",\n" + 
				"            \"supfildt\": \"26-06-2021\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"RR/2021-22/296\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"10-05-2021\",\n" + 
				"                \"val\": 125178,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 12,\n" + 
				"                    \"txval\": 111766,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 6705.96,\n" + 
				"                    \"sgst\": 6705.96,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          }\n" + 
				"        ],\n" + 
				"        \"isd\": [\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AABCP8801C2ZP\",\n" + 
				"            \"trdnm\": \"PRABHAT AGRI BIOTECH LTD\",\n" + 
				"            \"supfildt\": \"31-05-2021\",\n" + 
				"            \"supprd\": \"042021\",\n" + 
				"            \"doclist\": [\n" + 
				"              {\n" + 
				"                \"doctyp\": \"ISDI\",\n" + 
				"                \"docnum\": \"3000000014\",\n" + 
				"                \"docdt\": \"30-04-2021\",\n" + 
				"                \"igst\": 1249,\n" + 
				"                \"cgst\": 0,\n" + 
				"                \"sgst\": 0,\n" + 
				"                \"itcelg\": \"N\"\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          }\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    }\n" + 
				"  }"+
				"}";
	}
	
	private String getGstr2bJsonfc2() {
		return "{"+
				
				"\"data\": {\n" + 
				"    \"chksum\": \"a5349af57170140d434cd2d0d15508ce60e5c04209403f51af3dd85ea43ce397\",\n" + 
				"    \"data\": {\n" + 
				"      \"gstin\": \"09AABCP8801C1ZN\",\n" + 
				"      \"rtnprd\": \"052021\",\n" + 
				"      \"version\": \"1.0\",\n" + 
				"      \"gendt\": \"29-06-2021\",\n" + 
				"      \"itcsumm\": {\n" + 
				"        \"itcavl\": {\n" + 
				"          \"nonrevsup\": {\n" + 
				"            \"igst\": 6140.31,\n" + 
				"            \"cgst\": 6999.93,\n" + 
				"            \"sgst\": 6999.93,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"b2b\": {\n" + 
				"              \"igst\": 6140.31,\n" + 
				"              \"cgst\": 6999.93,\n" + 
				"              \"sgst\": 6999.93,\n" + 
				"              \"cess\": 0\n" + 
				"            }\n" + 
				"          },\n" + 
				"          \"revsup\": {\n" + 
				"            \"igst\": 2628,\n" + 
				"            \"cgst\": 1603.01,\n" + 
				"            \"sgst\": 1603.01,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"b2b\": {\n" + 
				"              \"igst\": 2628,\n" + 
				"              \"cgst\": 1603.01,\n" + 
				"              \"sgst\": 1603.01,\n" + 
				"              \"cess\": 0\n" + 
				"            }\n" + 
				"          }\n" + 
				"        },\n" + 
				"        \"itcunavl\": {\n" + 
				"          \"isdsup\": {\n" + 
				"            \"igst\": 1249,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"isd\": {\n" + 
				"              \"igst\": 1249,\n" + 
				"              \"cgst\": 0,\n" + 
				"              \"sgst\": 0,\n" + 
				"              \"cess\": 0\n" + 
				"            }\n" + 
				"          }\n" + 
				"        }\n" + 
				"      },\n" + 
				"      \"cpsumm\": {\n" + 
				"        \"b2b\": [\n" + 
				"          {\n" + 
				"            \"ctin\": \"08AUOPG8995N1ZF\",\n" + 
				"            \"trdnm\": \"NEW HINDUSTAN ROADWAYS\",\n" + 
				"            \"supprd\": \"032021\",\n" + 
				"            \"supfildt\": \"23-06-2021\",\n" + 
				"            \"txval\": 52555,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 2628,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 2\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AAACR6529A1ZL\",\n" + 
				"            \"trdnm\": \"M/S RAM LAL RAM CHANDER (INDIA)  LTD.\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"26-06-2021\",\n" + 
				"            \"txval\": 111766,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 6705.96,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 6705.96,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AAACT7966R1Z9\",\n" + 
				"            \"trdnm\": \"TCI FREIGHT (A DIV OF TRANSPORT CORPORATION OF INDIA LTD)\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"19-06-2021\",\n" + 
				"            \"txval\": 64120,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 1603.01,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 1603.01,\n" + 
				"            \"ttldocs\": 5\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AACCN7214Q1ZY\",\n" + 
				"            \"trdnm\": \"M/S NUZIVEEDU SEEDS LIMITED\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"11-06-2021\",\n" + 
				"            \"txval\": 3266.29,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 293.97,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 293.97,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09CUVPM7182C1Z0\",\n" + 
				"            \"trdnm\": \"NAV BHARAT ROADWAYS\",\n" + 
				"            \"supprd\": \"032021\",\n" + 
				"            \"supfildt\": \"24-06-2021\",\n" + 
				"            \"txval\": 14030,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09CUVPM7182C1Z0\",\n" + 
				"            \"trdnm\": \"NAV BHARAT ROADWAYS\",\n" + 
				"            \"supprd\": \"122020\",\n" + 
				"            \"supfildt\": \"24-06-2021\",\n" + 
				"            \"txval\": 20565,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 0,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 2\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"27AACFH0517K1ZK\",\n" + 
				"            \"trdnm\": \"HYPACK INDUSTRIES\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"10-06-2021\",\n" + 
				"            \"txval\": 20500,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 3690,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AABCP8801C1ZQ\",\n" + 
				"            \"trdnm\": \"PRABHAT AGRI BIOTECH LTD\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"20-06-2021\",\n" + 
				"            \"txval\": 8418.38,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 1515.31,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 2\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AALCM6590K1ZS\",\n" + 
				"            \"trdnm\": \"MAGNUS CADEAUX INDIA PRIVATE LIMITED\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"supfildt\": \"22-06-2021\",\n" + 
				"            \"txval\": 18700,\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 935,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 2\n" + 
				"          }\n" + 
				"        ],\n" + 
				"        \"isd\": [\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AABCP8801C2ZP\",\n" + 
				"            \"trdnm\": \"PRABHAT AGRI BIOTECH LTD\",\n" + 
				"            \"supprd\": \"042021\",\n" + 
				"            \"supfildt\": \"31-05-2021\",\n" + 
				"            \"doctyp\": \"ISDI\",\n" + 
				"            \"cess\": 0,\n" + 
				"            \"cgst\": 0,\n" + 
				"            \"igst\": 1249,\n" + 
				"            \"sgst\": 0,\n" + 
				"            \"ttldocs\": 1\n" + 
				"          }\n" + 
				"        ]\n" + 
				"      },\n" + 
				"      \"docdata\": {\n" + 
				"        \"b2b\": [\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AABCP8801H1ZQ\",\n" + 
				"            \"trdnm\": \"PRABHAT AGRI BIOTECH LTD\",\n" + 
				"            \"supfildt\": \"20-06-2021\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"3000000019\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"31-05-2021\",\n" + 
				"                \"val\": 104,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 18,\n" + 
				"                    \"txval\": 88.13,\n" + 
				"                    \"igst\": 15.86\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"4110001080\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"05-05-2021\",\n" + 
				"                \"val\": 9829.7,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 18,\n" + 
				"                    \"txval\": 8330.25,\n" + 
				"                    \"igst\": 1499.45,\n" + 
				"                    \"cgst\": 0,\n" + 
				"                    \"sgst\": 0,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ],\n" + 
				"                \"srctyp\": \"E-Invoice\",\n" + 
				"                \"irn\": \"2f6707c2864f85768e711cdb2dce79e917f0e20eb7c8e0ac4280fbfef5d7dcdb\",\n" + 
				"                \"irngendate\": \"05-05-2021\"\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"08AUOPG8995F1ZF\",\n" + 
				"            \"trdnm\": \"NEW HINDUSTAN ROADWAYS\",\n" + 
				"            \"supfildt\": \"23-06-2021\",\n" + 
				"            \"supprd\": \"032021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"812\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"23-02-2021\",\n" + 
				"                \"val\": 16100,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 16100,\n" + 
				"                    \"igst\": 805,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"822\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"04-03-2021\",\n" + 
				"                \"val\": 36455,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 36455,\n" + 
				"                    \"igst\": 1823,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AALCM6590P1ZS\",\n" + 
				"            \"trdnm\": \"MAGNUS CADEAUX INDIA PRIVATE LIMITED\",\n" + 
				"            \"supfildt\": \"22-06-2021\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"2021-2022/2/65\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"10-05-2021\",\n" + 
				"                \"val\": 16632,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 500,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 15840,\n" + 
				"                    \"igst\": 792,\n" + 
				"                    \"cgst\": 0,\n" + 
				"                    \"sgst\": 0,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"2021-2022/2/88\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"20-05-2021\",\n" + 
				"                \"val\": 3003,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 500,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 2860,\n" + 
				"                    \"igst\": 143,\n" + 
				"                    \"cgst\": 0,\n" + 
				"                    \"sgst\": 0,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AAACT7966Y1Z9\",\n" + 
				"            \"trdnm\": \"TCI FREIGHT (A DIV OF TRANSPORT CORPORATION OF INDIA LTD)\",\n" + 
				"            \"supfildt\": \"19-06-2021\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"3209202303\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"12-05-2021\",\n" + 
				"                \"val\": 9520,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 9520,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 238,\n" + 
				"                    \"sgst\": 238,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"3209202414\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"14-05-2021\",\n" + 
				"                \"val\": 8725,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 8725,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 218.13,\n" + 
				"                    \"sgst\": 218.13,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"3209202415\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"14-05-2021\",\n" + 
				"                \"val\": 8725,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 8725,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 218.13,\n" + 
				"                    \"sgst\": 218.13,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"3209202654\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"20-05-2021\",\n" + 
				"                \"val\": 5900,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 5900,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 147.5,\n" + 
				"                    \"sgst\": 147.5,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"3209202820\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"24-05-2021\",\n" + 
				"                \"val\": 31250,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 5,\n" + 
				"                    \"txval\": 31250,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 781.25,\n" + 
				"                    \"sgst\": 781.25,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09CUVPM7182T1Z0\",\n" + 
				"            \"trdnm\": \"NAV BHARAT ROADWAYS\",\n" + 
				"            \"supfildt\": \"24-06-2021\",\n" + 
				"            \"supprd\": \"032021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"1608\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"31-01-2021\",\n" + 
				"                \"val\": 14030,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 0,\n" + 
				"                    \"txval\": 14030,\n" + 
				"                    \"cgst\": 0,\n" + 
				"                    \"sgst\": 0,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"27AACFH0517L1ZK\",\n" + 
				"            \"trdnm\": \"HYPACK INDUSTRIES\",\n" + 
				"            \"supfildt\": \"10-06-2021\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"00296/21-22\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"27-05-2021\",\n" + 
				"                \"val\": 24190,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1801,\n" + 
				"                    \"rt\": 18,\n" + 
				"                    \"txval\": 20500,\n" + 
				"                    \"igst\": 3690,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AACCN7214W1ZY\",\n" + 
				"            \"trdnm\": \"M/S NUZIVEEDU SEEDS LIMITED\",\n" + 
				"            \"supfildt\": \"11-06-2021\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"1110003992\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"03-05-2021\",\n" + 
				"                \"val\": 3854.23,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 18,\n" + 
				"                    \"txval\": 3266.29,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 293.97,\n" + 
				"                    \"sgst\": 293.97,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ],\n" + 
				"                \"srctyp\": \"E-Invoice\",\n" + 
				"                \"irn\": \"30c65bdb6be7a36e8dbb93664de888138743d2e63df3dcde8fdf64726349bd44\",\n" + 
				"                \"irngendate\": \"03-05-2021\"\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09CUVPM7182D1Z0\",\n" + 
				"            \"trdnm\": \"NAV BHARAT ROADWAYS\",\n" + 
				"            \"supfildt\": \"24-06-2021\",\n" + 
				"            \"supprd\": \"122020\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"1426\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"31-10-2020\",\n" + 
				"                \"val\": 19205,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 0,\n" + 
				"                    \"txval\": 19205,\n" + 
				"                    \"cgst\": 0,\n" + 
				"                    \"sgst\": 0,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              },\n" + 
				"              {\n" + 
				"                \"inum\": \"1492\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"30-11-2020\",\n" + 
				"                \"val\": 1360,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"Y\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 0,\n" + 
				"                    \"txval\": 1360,\n" + 
				"                    \"cgst\": 0,\n" + 
				"                    \"sgst\": 0,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          },\n" + 
				"          {\n" + 
				"            \"ctin\": \"09AAACR6529B1ZL\",\n" + 
				"            \"trdnm\": \"M/S RAM LAL RAM CHANDER (INDIA)  LTD.\",\n" + 
				"            \"supfildt\": \"26-06-2021\",\n" + 
				"            \"supprd\": \"052021\",\n" + 
				"            \"inv\": [\n" + 
				"              {\n" + 
				"                \"inum\": \"RR/2021-22/296\",\n" + 
				"                \"typ\": \"R\",\n" + 
				"                \"dt\": \"10-05-2021\",\n" + 
				"                \"val\": 125178,\n" + 
				"                \"pos\": \"09\",\n" + 
				"                \"rev\": \"N\",\n" + 
				"                \"itcavl\": \"Y\",\n" + 
				"                \"rsn\": \"\",\n" + 
				"                \"diffprcnt\": 1,\n" + 
				"                \"items\": [\n" + 
				"                  {\n" + 
				"                    \"num\": 1,\n" + 
				"                    \"rt\": 12,\n" + 
				"                    \"txval\": 111766,\n" + 
				"                    \"igst\": 0,\n" + 
				"                    \"cgst\": 6705.96,\n" + 
				"                    \"sgst\": 6705.96,\n" + 
				"                    \"cess\": 0\n" + 
				"                  }\n" + 
				"                ]\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          }\n" + 
				"        ],\n" + 
				"        \"isd\": [\n" + 
				"          {\n" + 
				"            \"ctin\": \"36AABCP8801D2ZP\",\n" + 
				"            \"trdnm\": \"PRABHAT AGRI BIOTECH LTD\",\n" + 
				"            \"supfildt\": \"31-05-2021\",\n" + 
				"            \"supprd\": \"042021\",\n" + 
				"            \"doclist\": [\n" + 
				"              {\n" + 
				"                \"doctyp\": \"ISDI\",\n" + 
				"                \"docnum\": \"3000000034\",\n" + 
				"                \"docdt\": \"30-04-2021\",\n" + 
				"                \"igst\": 1249,\n" + 
				"                \"cgst\": 0,\n" + 
				"                \"sgst\": 0,\n" + 
				"                \"itcelg\": \"N\"\n" + 
				"              }\n" + 
				"            ]\n" + 
				"          }\n" + 
				"        ]\n" + 
				"      }\n" + 
				"    }\n" + 
				"  }"+
				"}";
	}
	
	private String newGstr3bJSon() {
		return "{"+
				" \"data\":{" + 
				"    \"gstin\": \"03AADCG4541N1ZM\"," + 
				"    \"ret_period\": \"042021\"," + 
					"  \"sup_details\": {" + 
					"  \"osup_3_1a\": {" + 
						"  \"subtotal\": {" + 
							" \"txval\": 1111700," + 
							"      \"iamt\": 5400," + 
							"      \"camt\": 73359," + 
							"      \"samt\": 73359," + 
							"      \"csamt\": 0" + 
						"  }," + 
						"  \"det\": {" + 
							"  }" + 
					"  }," + 
						"  \"osup_3_1b\": {" + 
						"  \"subtotal\": {" + 
							" \"txval\": 1000," + 
							"\"iamt\": 20," + 
							" \"csamt\": 0" + 
						"  }," + 
						"  \"det\": {" + 
						
							"  }" + 
					"  }," + 
					"  \"osup_3_1c\": {" + 
						"  \"subtotal\": {" + 
							" \"txval\": 340" + 
						"  }," + 
						"  \"det\": {" + 
							"  }" + 
				"  }," + 
				"  \"osup_3_1e\": {" + 
					"  \"subtotal\": {" + 
						" 		\"txval\": 800" + 
					"  }," + 
					"  \"det\": {" + 
						"  }" + 
				"  }" + 
				
					"  }," + 
					"  \"inter_sup\": {" + 
					"  \"osup_unreg_3_2\": {" +
						"  \"subtotal\": [" + "{" + 
						"  \"pos\": \"05\"," + 
						"   \"txval\": 1300," + 
						"      \"iamt\": 9" + 
						"  }" +
						"  ]," + 
						"  \"det\": {" + 
						
						"  }" + 
						"  }," + 
						"  \"osup_comp_3_2\": {" +
						"  \"subtotal\": [" + "{" + 
						"  \"pos\": \"\"," + 
						"   \"txval\": 1300," + 
						"      \"iamt\": 9" + 
						"  }," +
						"{" + 
						"  \"pos\": \"07\"," + 
						"   \"txval\": 100," + 
						"      \"iamt\": 0" + 
						"  }" +
						"  ]," + 
						"  \"det\": {" + 
						
						"  }" + 
						"  }," + 
						"  \"osup_uin_3_2\": {" +
						"  \"subtotal\": [" + "{" + 
						"  \"pos\": \"05\"," + 
						"   \"txval\": 1300," + 
						"      \"iamt\": 9" + 
						"  }," +
						"{" + 
						"  \"pos\": \"08\"," + 
						"   \"txval\": 100," + 
						"      \"iamt\": 0" + 
						"  }" +
						"  ]," + 
						"  \"det\": {" + 
						
						"  }" + 
						"  }" + 
						"  }" + 

		"}}";	
	}
	private String gstr3bJSon() {
		return "{"+
				" \"data\":{" + 
				" \"r3bautopop\":{" + 
				"  \"r1fildt\": \"01-09-2020\"," + 
				"  \"r2bgendt\": \"01-09-2020\"," + 
				"  \"r3bgendt\": \"01-09-2020\"," + 
				"  \"liabitc\": {" + 
				"    \"gstin\": \"09SSAUP0009A1ZO\"," + 
				"    \"ret_period\": \"032020\"," + 
					"  \"sup_details\": {" + 
					"  \"osup_3_1a\": {" + 
						"  \"subtotal\": {" + 
							" \"txval\": 1000," + 
							"      \"iamt\": 20," + 
							"      \"camt\": 10," + 
							"      \"samt\": 10," + 
							"      \"csamt\": 5" + 
						"  }," + 
						"  \"det\": {" + 
							"  }" + 
					"  }," + 
						"  \"osup_3_1b\": {" + 
						"  \"subtotal\": {" + 
							" \"txval\": 1000," + 
							"\"iamt\": 20," + 
							" \"csamt\": 0" + 
						"  }," + 
						"  \"det\": {" + 
						
							"  }" + 
					"  }," + 
					"  \"osup_3_1c\": {" + 
						"  \"subtotal\": {" + 
							" \"txval\": 0" + 
						"  }," + 
						"  \"det\": {" + 
							"  }" + 
				"  }," + 
				"  \"osup_3_1e\": {" + 
					"  \"subtotal\": {" + 
						" 		\"txval\": 0" + 
					"  }," + 
					"  \"det\": {" + 
						"  }" + 
				"  }," + 
				"  \"osup_3_1d\": {" + 
					"  \"subtotal\": {" + 
					" \"txval\": 1000," + 
					"      \"iamt\": 20," + 
					"      \"camt\": 10," + 
					"      \"samt\": 10," + 
					"      \"csamt\": 5" + 
					"  }," + 
					"  \"det\": {" + 
						"  \"itcavl\": {" + 
							" \"txval\": 1000," + 
							"      \"igst\": 20," + 
							"      \"cgst\": 10," + 
							"      \"sgst\": 10," + 
							"      \"cess\": 5" + 
							"  }," + 
							"  \"itcunavl\": {" + 
							" \"txval\": 1000," + 
							"      \"igst\": 20," + 
							"      \"cgst\": 10," + 
							"      \"sgst\": 10," + 
							"      \"cess\": 5" + 
							"  }" + 
						"  }" + 
				"  }" +
					"  }," + 
					"  \"inter_sup\": {" + 
					"  \"osup_comp_3_2\": {" +
						"  \"subtotal\": [" + "{" + 
						"  \"pos\": \"05\"," + 
						" \"txval\": 1300," + 
						"      \"iamt\": 9" + 
						"  }" + 
						"  ]," + 
						"  \"det\": {" + 
						"  \"tbl4a\": [" + "{" + 
						"  \"pos\": \"\"," + 
						" \"txval\": 1000," + 
						"      \"iamt\": 20" + 
						"  }" + 
						"  ]" + 
						"  }" + 
						"  }" + 
						"  }," + 
						"  \"elgitc\": {" + 
						"  \"itc4a1\": {" + 
							"  \"subtotal\": {" + 
								"      \"iamt\": 20," + 
								"      \"csamt\": 5" + 
							"  }," + 
							"  \"det\": {" + 
							"  \"itcavl\": {" + 
							" \"txval\": 1000," + 
							"      \"igst\": 20," + 
							"      \"cess\": 5" + 
							"  }" + 
								"  }" + 
						"  }," +
						"  \"itc4a3\": {" + 
						"  \"subtotal\": {" + 
						" \"txval\": 1000," + 
						"      \"iamt\": 20," + 
						"      \"camt\": 10," + 
						"      \"samt\": 10," + 
						"      \"csamt\": 5" + 
						"  }," + 
						"  \"det\": {" + 
						"  \"itcavl\": {" + 
						" \"txval\": 1000," + 
						"      \"igst\": 20," + 
						"      \"cgst\": 10," + 
						"      \"sgst\": 10," + 
						"      \"cess\": 5" + 
						"  }" + 
							"  }" + 
					"  }," +
					"  \"itc4a4\": {" + 
					"  \"subtotal\": {" + 
					" \"txval\": 1000," + 
					"      \"iamt\": 20," + 
					"      \"camt\": 10," + 
					"      \"samt\": 10," + 
					"      \"csamt\": 5" + 
					"  }," + 
					"  \"det\": {" + 
					"  \"itcavl\": {" + 
					" \"txval\": 1000," + 
					"      \"igst\": 20," + 
					"      \"cgst\": 10," + 
					"      \"sgst\": 10," + 
					"      \"cess\": 5" + 
					"  }" + 
						"  }" + 
				"  }," +
				"  \"itc4a5\": {" + 
				"  \"subtotal\": {" + 
				" \"txval\": 1000," + 
				"      \"iamt\": 20," + 
				"      \"camt\": 10," + 
				"      \"samt\": 10," + 
				"      \"csamt\": 5" + 
				"  }," + 
				"  \"det\": {" + 
				"  \"itcavl\": {" + 
				" \"txval\": 1000," + 
				"      \"igst\": 20," + 
				"      \"cgst\": 10," + 
				"      \"sgst\": 10," + 
				"      \"cess\": 5" + 
				"  }" + 
					"  }" + 
			"  }," +			
			"  \"itc4b2\": {" + 
			"  \"subtotal\": {" + 
			"      \"iamt\": 20," + 
			"      \"camt\": 10," + 
			"      \"samt\": 10," + 
			"      \"csamt\": 5" + 
			"  }," + 
			"  \"det\": {" + 
			"  \"itcavl\": {" + 
			" \"txval\": 1000," + 
			"      \"igst\": 20," + 
			"      \"cgst\": 10," + 
			"      \"sgst\": 10," + 
			"      \"cess\": 5" + 
			"  }," + 
			"  \"itcunavl\": {" + 
			" \"txval\": 1000," + 
			"      \"igst\": 20," + 
			"      \"cgst\": 10," + 
			"      \"sgst\": 10," + 
			"      \"cess\": 5" + 
			"  }," + 
			"  \"itcrev\": {" + 
			" \"txval\": 1000," + 
			"      \"igst\": 20," + 
			"      \"cgst\": 10," + 
			"      \"sgst\": 10," + 
			"      \"cess\": 5" + 
			"  }" + 
				"  }" + 
		"  }" +
			
		"  }" +
			"  }" + 
			"  }" + 
		"}}";	
	}
}
