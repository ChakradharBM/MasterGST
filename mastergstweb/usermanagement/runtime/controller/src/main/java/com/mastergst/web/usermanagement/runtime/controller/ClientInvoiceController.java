package com.mastergst.web.usermanagement.runtime.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.service.ClientService;

@Controller
@RequestMapping(value="/clientinvoice")
public class ClientInvoiceController {
	private static final Logger logger = LogManager.getLogger(ClientInvoiceController.class.getName());
	
	@Autowired
	private ClientService clientService;
	
	@RequestMapping(value = "/edit/{id}/{name}/{usertype}/{clientid}/{invId}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public String editInvoice(@PathVariable("id") String id, 
			@PathVariable("invId") String invId, @PathVariable("usertype") String usertype, @PathVariable("name") String fullName, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,
			HttpServletRequest request){
		Client client = clientService.findById(clientid);
		InvoiceParent invoiceParent = clientService.getInvoice(invId, returntype);
		model.addAttribute("invoice",invoiceParent);
		model.addAttribute("client",client);
		model.addAttribute("id",id);
		model.addAttribute("returntype",returntype);
		model.addAttribute("fullname",fullName);
		model.addAttribute("month",month);
		model.addAttribute("usertype",usertype);
		model.addAttribute("year",year);
		return "client/add_invoice";
	}
}
