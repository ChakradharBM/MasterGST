/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.usermanagement.runtime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.AspUserDetails;
import com.mastergst.usermanagement.runtime.service.AspUserDetailsService;

@Controller
public class AspUserDetailsController {

	@Autowired
	private AspUserDetailsService aspUserDetailsService;

	@RequestMapping(value = "/saveapiuserdetails/{userid}", method = RequestMethod.POST)
	public @ResponseBody String saveApiUserDetails(@RequestBody AspUserDetails aspUserDetails,@PathVariable("userid") String userid){
			aspUserDetailsService.saveAspUserDetails(aspUserDetails);
			return "success";
	}
	
	@RequestMapping(value = "/getAspUserDetails/{userid}", method = RequestMethod.GET)
	public @ResponseBody AspUserDetails getAspUserDetails(@PathVariable String userid) {
		
		AspUserDetails aspUserDetails=aspUserDetailsService.getUserid(userid);
		if(NullUtil.isNotEmpty(aspUserDetails)) {
			return aspUserDetails;
		}else {
			return null;
		}
	}

}

