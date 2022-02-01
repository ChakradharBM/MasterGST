package com.mastergst.web.usermanagement.runtime.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.OtpExpiryService;

@Controller
public class OtpExpiryController {
	@Autowired
	private OtpExpiryService otpExpiryService;
	@Autowired
	private ClientService clientService;
	
	
	@RequestMapping(value = "/otpexpiry/{clientid}", method = RequestMethod.GET)
	public @ResponseBody String otpexpiry(@PathVariable("clientid") String clientid,HttpServletRequest request){
		Client client = clientService.findById(clientid);
		String expiry = "";
		if(NullUtil.isNotEmpty(client.getGstname())) {
			expiry = otpExpiryService.otpexpiry(client.getGstname());
		}
		return expiry;
	}
}
