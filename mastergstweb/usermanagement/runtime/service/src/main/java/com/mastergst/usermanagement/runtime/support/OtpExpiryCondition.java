package com.mastergst.usermanagement.runtime.support;

import static com.mastergst.core.util.NullUtil.isEmpty;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.HeaderKeys;
import com.mastergst.usermanagement.runtime.service.ClientService;

public class OtpExpiryCondition {
	@Autowired
	private ClientService clientService;
	
	
	
	@RequestMapping(value = "/otpexpiry/{clientgstname}", method = RequestMethod.GET)
	public String otpexpiry(@PathVariable("clientgstname") String clientgstname,HttpServletRequest request, ModelMap model) throws Exception {
		HeaderKeys headerKeys=clientService.getHeaderkeysGstusername(clientgstname);			
		if(isEmpty(headerKeys)) {
			model.addAttribute("headerkeys", "NOTFOUND");
		}else {
			
			if(NullUtil.isNotEmpty(headerKeys) && NullUtil.isNotEmpty(headerKeys.getUpdatedDate())) {
				long duration = Calendar.getInstance().getTime().getTime() - headerKeys.getUpdatedDate().getTime();
				long diff = TimeUnit.MILLISECONDS.toMinutes(duration);
				if(NullUtil.isNotEmpty(headerKeys.getExpiry()) && headerKeys.getExpiry() == 0) {
					model.addAttribute("otpexpires","EXPIRED");
				}else {
					if(diff >= 360) { // < ((headerKey.getExpiry()/2)-30)
						model.addAttribute("otpexpires","EXPIRED");
					}
				}
			}	
		}
		return clientgstname;
	}

}
