package com.mastergst.web.usermanagement.runtime.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.client.util.Maps;
import com.mastergst.login.runtime.domain.ReconcileUsers;
import com.mastergst.usermanagement.runtime.service.AdminSupportService;

@Controller
public class AdminSupportController {
	
	@Autowired private AdminSupportService adminSupportService; 
	private ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping("/getReconcileTempUsers/{userid}")
	public @ResponseBody String getReconcileTempUsers(@PathVariable String userid, HttpServletRequest request) throws JsonProcessingException {
		
		String st = request.getParameter("start");
		
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		Map<String, Object> dataMap = Maps.newHashMap();
		Page<ReconcileUsers> users = adminSupportService.getReconcileInfo(start, length, searchVal);
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		dataMap.put("data", users);
		
		ObjectWriter writer =  mapper.writer();
		
		return writer.writeValueAsString(dataMap);
	}

}
