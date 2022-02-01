package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.domain.ApisVersionFilter;
import com.mastergst.usermanagement.runtime.domain.ApisVesrion;
import com.mastergst.usermanagement.runtime.service.ApisVersionService;

@Controller
public class ApisVersionController {
	private static final Logger logger = LogManager.getLogger(ApisVersionController.class.getName());
	private static final String CLASSNAME = "APIVersionController::";

	@Autowired
	private ApisVersionService apisVersionService;
	
	@RequestMapping(value = "/apiversionpage", method = RequestMethod.GET)
	public String apiVersion(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "usertype", required = true) String userType, ModelMap model) throws Exception {
		final String method = "apiVersion::";
		logger.debug(CLASSNAME + method + BEGIN);
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", userType);
		logger.debug(CLASSNAME + method + END);
		return "admin/apiversionpage";
	}
	
	@RequestMapping(value = "/getApisVersions", method = RequestMethod.GET)
	public @ResponseBody String getApisVersions(@RequestParam String apiType, HttpServletRequest request) throws Exception {
		final String method = "getApisVersions::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		String st = request.getParameter("start");
		String len = request.getParameter("length");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);

		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		
		ApisVersionFilter filter = new ApisVersionFilter();
		
		Page<ApisVesrion> apis=apisVersionService.getApisVersions(apiType, start, length, searchVal, filter);
		if(isNotEmpty(apis)) {
			for(ApisVesrion api : apis) {
				api.setDocid(api.getId().toString());
			}
		}
				
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer = mapper.writer();
	
		return writer.writeValueAsString(apis);
	}
	
	@RequestMapping(value = "/apiversioninfo", method = RequestMethod.GET)
	public @ResponseBody ApisVesrion getApiVersionInfo(@RequestParam String docid, HttpServletRequest request) throws Exception {
		final String method = "getApiVersionInfo::";
		logger.debug(CLASSNAME + method + BEGIN);
				
		ApisVesrion api = apisVersionService.getApisVersionInfo(docid);
		if(isNotEmpty(api)) {
			api.setDocid(api.getId().toString());
		}
				
		return api;
	}
	
	@RequestMapping(value = "/updateApiVersion", method = RequestMethod.POST)
	public @ResponseBody String updateApiVersion(@RequestBody ApisVesrion apivesrion, HttpServletRequest request) throws Exception {
		final String method = "getApiVersionInfo::";
		logger.debug(CLASSNAME + method + BEGIN);
				
		apisVersionService.updateApiVersion(apivesrion);
		
		return MasterGSTConstants.SUCCESS;
	}
}
