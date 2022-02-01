/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.common.collect.Lists;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.GSTReturnSummary;
import com.mastergst.usermanagement.runtime.domain.PrintConfiguration;
import com.mastergst.usermanagement.runtime.domain.TemplateMapper;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;
import com.mastergst.usermanagement.runtime.repository.TemplateMapperDocRepository;
import com.mastergst.usermanagement.runtime.repository.TemplateMapperRepository;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.Field;
import com.mastergst.usermanagement.runtime.service.GstWorkSheet;
import com.mastergst.usermanagement.runtime.service.ImportMapperService;
import com.mastergst.usermanagement.runtime.service.ProfileService;

/**
 * Handles Import Mappings
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 * @since 1.0
 */
@Controller
public class ImportsController {

	private static final Logger logger = LogManager.getLogger(ImportsController.class.getName());
	private static final String CLASSNAME = "ImportsController::";
	
	@Autowired	ConfigService configService;
	@Autowired	private ClientService clientService;
	@Autowired	private ImportMapperService importMapperService;
	@Autowired	private UserService userService;
	@Autowired	private ProfileService profileService;
	@Autowired	private TemplateMapperRepository templateMapperRepository;
	@Autowired	private TemplateMapperDocRepository templateMapperDocRepository;
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/imports/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String obtainMapperPage(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year,@RequestParam(value="type",required=false) String type, 
			 ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "obtainMapperPage::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(NullUtil.isNotEmpty(companyUser)) {
				List<String> clntids = companyUser.getCompany();
				if(NullUtil.isNotEmpty(clntids) && clntids.size() > 0){
					if(clntids.contains(clientid)){
						model.addAttribute("companyUser", companyUser);
					}
				}
				if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
					model.addAttribute("addClient", companyUser.getAddclient());
				}else {
					model.addAttribute("addClient", "true");
				}
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("type", type);
	//	model.addAttribute("fields", importMapperService.getSalesTemplateFields());
		model.addAttribute("lGSTReturnsSummury", getGSTReturnsSummary(client));
		model.addAttribute("mapperslist", importMapperService.getMappersByClientId(clientid));
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		//model.addAttribute("type", type);
		logger.debug(CLASSNAME + method + END);
		return "mapper/mapimport";
	}
	
	private List<GSTReturnSummary> getGSTReturnsSummary(final Client client) throws Exception {
		List<GSTReturnSummary> returnSummaryList = client.getReturnsSummary();
		if(isEmpty(returnSummaryList)) {
			List<String> returntypes = configService.getDealerACL(client.getDealertype());
			returntypes = returntypes.stream().filter(String -> String.startsWith("GSTR")).collect(Collectors.toList());
			logger.debug(CLASSNAME + " getGSTReturnsSummary:: returntypes\t" + returntypes.toString());
			returnSummaryList = clientService.getGSTReturnsSummary(returntypes, null);
		}
		return returnSummaryList;
	}
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	
	@RequestMapping(value = "addmapperfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> addMappedFile(MultipartHttpServletRequest request, ModelMap model,
			@RequestParam("userId") String userId, @RequestParam("clientId") String clientId, @RequestParam("mapperName") String mapperName, @RequestParam("mapperType") String mapperType,@RequestParam("skipRows") String skipRows) throws Exception {
		Map<String, Object> result = new HashMap<>();
		validateData(userId, clientId, mapperName, mapperType, result);
		if(result.isEmpty()){
			Map<String, MultipartFile> multipartData = request.getFileMap();
			if(multipartData.isEmpty()){
				result.put("fileselect", "Please select Mapper File");
			}else{	
				Map<String,Map<String,List<String>>> mapperfieldsData = importMapperService.readMapperAndNonMapperFile(multipartData, Integer.parseInt(skipRows)-1);
				if(NullUtil.isNotEmpty(mapperfieldsData)) {
					Map<String,List<String>> mapperFields = mapperfieldsData.get("mapperFields");
					Map<String,List<String>> nonMapperFields = mapperfieldsData.get("nonMapperFields");
					if(nonMapperFields != null){
						request.getSession().setAttribute("nonMapperPages", nonMapperFields);
					}
					if(mapperFields != null){
						request.getSession().setAttribute("mapperPages", mapperFields);
						result.put("mapperPages", mapperFields.keySet());
					}
				}
				result.put("pages", getPages(mapperType));
			}
		}
		return result;
	}
	
	@RequestMapping(value = "getpagefields", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String getPageFields(HttpServletRequest request, @RequestBody Map mapFieldData) throws Exception {
		addPageFieldsToModal(request, null, mapFieldData);
		return "mapper/fieldmapper";
	}	
	@RequestMapping(value = "getpagefields/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public String getPageFields1(HttpServletRequest request,@PathVariable("id") String id, @RequestBody Map mapFieldData) throws Exception {
		addPageFieldsToModal(request, id, mapFieldData);
		return "mapper/fieldmapper";
	}	
	
	@RequestMapping(value = "mappers/{userId}/{clientId}/{mapperType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> getMappers(HttpServletRequest request,@PathVariable("userId") String userId
			,@PathVariable("clientId") String clientId
			,@PathVariable("mapperType") String mapperType) throws Exception {
		
		List<TemplateMapper> mappers = importMapperService.getByUserIdAndClientIdAndMapperTypeAndIsCompleted(userId, clientId, mapperType, "true");
		List<String> mapperNames = new ArrayList<>();
		mappers.forEach((t)->mapperNames.add(t.getMapperName()));
		return mapperNames;
	}
	
	@RequestMapping(value = "mapperdocs/{userId}/{clientId}/{mapperType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> getMapperDocs(HttpServletRequest request,@PathVariable("userId") String userId
			,@PathVariable("clientId") String clientId
			,@PathVariable("mapperType") String mapperType) throws Exception {
		
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(userId, clientId, mapperType);
		List<String> mapperNames = new ArrayList<>();
		mappers.forEach((t)->mapperNames.add(t.getMapperName()));
		return mapperNames;
	}	
	
	private void addPageFieldsToModal(HttpServletRequest request, String id, Map mapFieldData){
		Map<String, Object> result = new HashMap<>();
		String pageCode = (String)mapFieldData.get("pageCode");
		String pageName = (String)mapFieldData.get("pageName");
		String mappedPage= (String)mapFieldData.get("mappedPage");
		String mapperType= (String)mapFieldData.get("mapperType");
		String simplifiedOrDetail= (String)mapFieldData.get("simplifiedOrDetail");
		validateMapFieldData(pageCode, mappedPage, mapperType, result);
		if(result.isEmpty()){
			Map<String,List<String>> mapperPageFields = (Map<String,List<String>>)request.getSession().getAttribute("mapperPages");
			String discount = "amount";
			if(id != null){
				TemplateMapper templateMapper = importMapperService.getMapperById(id);
				
				if(NullUtil.isNotEmpty(templateMapper.getDiscountConfig())) {
					if(templateMapper.getDiscountConfig().containsKey(pageCode)) {
						discount = templateMapper.getDiscountConfig().get(pageCode);
					}
				}
				Map<String, Map> mapperConfig = templateMapper.getMapperConfig();
				mapperPageFields = templateMapper.getMapperPageFields();
				Map config = mapperConfig.get(pageName);
				String mpPage = null;
				if(config != null && mappedPage.equals(mpPage = (String)config.get("page"))){
					Map mappings = (Map)config.get("mappings");
					request.setAttribute("mappings", mappings);
				}
			}
			if(discount.equalsIgnoreCase("percentage")) {
				request.setAttribute("discount", discount);
			}
			if(mapperPageFields != null){
				request.setAttribute("mapperFields", mapperPageFields.get(mappedPage));
			}
			Map<String, GstWorkSheet> pageFields = getFields(mapperType,simplifiedOrDetail);
			GstWorkSheet gstWorkSheet = pageFields.get(pageCode);
			if(gstWorkSheet != null){
				request.setAttribute("fields", gstWorkSheet.getFields());
			}
			request.setAttribute("pageCode", pageCode);
			request.setAttribute("mappedPage", mappedPage);
		}
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "submitmapper", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map submitMapper(HttpServletRequest request, @RequestBody TemplateMapper templateMapper) throws Exception {
		final String method = "submitMapper::";
		logger.debug(CLASSNAME + method + BEGIN);
		List<String> clientIds = (List<String>) request.getSession().getAttribute("clientIds");
		if (isEmpty(clientIds)) {
			clientIds = clientService.fetchClientIds(templateMapper.getUserId());
		}
		if(templateMapper.isGlobaltemplate()) {
			templateMapper.setCompany(clientIds);
		}else {
			templateMapper.setCompany(Lists.newArrayList());
		}
		Map<String,List<String>> mapperPageFields = (Map<String,List<String>>)request.getSession().getAttribute("mapperPages");
		
		Map<String,List<String>> nonMapperPageFields = (Map<String,List<String>>)request.getSession().getAttribute("nonMapperPages");
		
		TemplateMapper templateMapper2 = importMapperService.saveMapper(templateMapper, mapperPageFields,nonMapperPageFields);
		request.getSession().removeAttribute("mapperPages");
		request.getSession().removeAttribute("nonMapperPages");
		Map<String, Object> map = new HashMap<>();
		map.put("id", templateMapper2.getId().toString());
		map.put("mapper", templateMapper2);
		logger.debug(CLASSNAME + method + END);
		return map;
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "submitmapper/{id}/{isValid}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map editSubmitMapper(HttpServletRequest request,@PathVariable("id") String id, @PathVariable("isValid") boolean isValid, 
			@RequestBody TemplateMapper templateMapper) throws Exception {
		final String method = "submitMapper::";
		logger.debug(CLASSNAME + method + BEGIN);
		List<String> clientIds = (List<String>) request.getSession().getAttribute("clientIds");
		if (isEmpty(clientIds)) {
			clientIds = clientService.fetchClientIds(templateMapper.getUserId());
		}
		if(templateMapper.isGlobaltemplate()) {
			templateMapper.setCompany(clientIds);
		}else {
			templateMapper.setCompany(Lists.newArrayList());
		}
		TemplateMapper templateMapper2 = importMapperService.editMapper(id, isValid, templateMapper);
		Map<String, Object> map = new HashMap<>();
		map.put("id", templateMapper2.getId().toString());
		map.put("mapper", templateMapper2);
		logger.debug(CLASSNAME + method + END);
		return map;
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "mapper/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map getMapper(@PathVariable("id") String id) throws Exception {
		final String method = "getMapper::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, Object> result = new HashMap<>();
		TemplateMapper templateMapper = importMapperService.getMapperById(id);
		result.put("mapper", templateMapper);
		result.put("pages", getPages(templateMapper.getMapperType()));
		logger.debug(CLASSNAME + method + END);
		return result;
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "saveGlobalTemplate/{id}/{mapperid}/{globaltemplate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map submitMapper(HttpServletRequest request, @PathVariable("id") String id,@PathVariable("mapperid") String mapperid,@PathVariable("globaltemplate") String globaltemplate) throws Exception {
		final String method = "submitMapper::";
		logger.debug(CLASSNAME + method + BEGIN);
		List<String> clientIds = (List<String>) request.getSession().getAttribute("clientIds");
		if (isEmpty(clientIds)) {
			clientIds = clientService.fetchClientIds(id);
		}
		TemplateMapper templateMapper = importMapperService.getMapperById(mapperid);
		boolean gtemplate = false;
		if("true".equals(globaltemplate)) {
			gtemplate = true;
		}
		templateMapper.setGlobaltemplate(gtemplate);
		if(templateMapper.isGlobaltemplate()) {
			templateMapper.setCompany(clientIds);
		}else {
			templateMapper.setCompany(Lists.newArrayList());
		}
		
		Map<String,List<String>> mapperPageFields = (Map<String,List<String>>)request.getSession().getAttribute("mapperPages");
		
		
		TemplateMapperDoc templateMapperDoc = templateMapperDocRepository.getByUserIdAndClientIdAndMapperTypeAndMapperName(
				templateMapper.getUserId(), templateMapper.getClientId(), templateMapper.getMapperType(), templateMapper.getMapperName());
		
		templateMapperDoc.setGlobaltemplate(gtemplate);
		if(templateMapperDoc.isGlobaltemplate()) {
			templateMapperDoc.setCompany(clientIds);
		}else {
			templateMapperDoc.setCompany(Lists.newArrayList());
		}
		TemplateMapperDoc templateMapperDoc1 = templateMapperDocRepository.save(templateMapperDoc);
		templateMapper.setTemplateMapperdocid(templateMapperDoc1.getId().toString());
		templateMapperRepository.save(templateMapper);
		request.getSession().removeAttribute("mapperPages");
		Map<String, Object> map = new HashMap<>();
		map.put("id", templateMapper.getId().toString());
		map.put("mapper", templateMapper);
		logger.debug(CLASSNAME + method + END);
		return map;
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "deletemapper/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map deleteMapper(@PathVariable("id") String id) throws Exception {
		final String method = "deleteMapper::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, Object> result = new HashMap<>();
		importMapperService.deleteMapper(id);
		logger.debug(CLASSNAME + method + END);
		return result;
	}
	
	
	private void validateData(String userId, String clientId, String mapperName, String mapperType, Map<String, Object> result){
		if(StringUtils.isEmpty(mapperType)){
			result.put("MapperType", "MapperType is required");
		}
		if(StringUtils.isEmpty(mapperName)){
			result.put("MapperName", "MapperName is required");
		}
		List<TemplateMapper> mappers = importMapperService.getMappersByUserIdAndClientId(userId, clientId);
		mappers.forEach((t)->{
			if(t.getMapperName().equalsIgnoreCase(mapperName)){
				result.put("MapperName", "MapperName already exists");
			}
		});
	}
	
	private void validateMapFieldData(String pageCode, String mappedPage, String mapperType, Map<String, Object> result){
		if(StringUtils.isEmpty(pageCode)){
			result.put("PageCode", "Page Code is Required");
		}
		if(StringUtils.isEmpty(mappedPage)){
			result.put("MappedPage", "Mapped Page is Required");
		}
		if(StringUtils.isEmpty(mapperType)){
			result.put("MapperType", "MapperType is Required");
		}
		
	}
	
	private Map<String, GstWorkSheet> getFields(String mapperType){
		Map<String, GstWorkSheet> fields = null;
		if("Sales".equalsIgnoreCase(mapperType)){
			fields = importMapperService.getSalesTemplateFields();
		}else if("einvoice".equalsIgnoreCase(mapperType)) {
			fields = importMapperService.getEinvoiceTemplateFields();
		}else{
			fields = importMapperService.getPurchageTemplateFields();
		}
		return fields;
	}
	
	private Map<String, GstWorkSheet> getFields(String mapperType,String simplifiedOrDetail){
		Map<String, GstWorkSheet> fields = null;
		if("Sales".equalsIgnoreCase(mapperType)){
			fields = importMapperService.getSalesTemplateFields(simplifiedOrDetail);
		}else if("einvoice".equalsIgnoreCase(mapperType)) {
			fields = importMapperService.getEinvoiceTemplateFields(simplifiedOrDetail);
		}else{
			fields = importMapperService.getPurchageTemplateFields(simplifiedOrDetail);
		}
		return fields;
	}
	
	private Set<String> getPages(String mapperType){
		Set<String> pages = null;
		if("Sales".equalsIgnoreCase(mapperType)){
			pages = importMapperService.getSalesTemplateAllPages();
		}else if("einvoice".equalsIgnoreCase(mapperType)) {
			pages = importMapperService.getEinvoiceTemplateAllPages();
		}else{
			pages = importMapperService.getPurchagesTemplateAllPages();
		}
		return pages;
	}
	
	
}
