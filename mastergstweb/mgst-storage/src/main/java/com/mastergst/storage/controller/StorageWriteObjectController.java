package com.mastergst.storage.controller;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mastergst.storage.service.StorageWriteService;
import com.mastergst.usermanagement.runtime.domain.MgstResponse;
import static com.mastergst.core.common.MasterGSTConstants.RESULTS;

@Controller
public class StorageWriteObjectController {
	
	private static final Logger logger = LogManager.getLogger(StorageWriteObjectController.class.getName());
	@Autowired
	private StorageWriteService storageWriteService;
	
	@RequestMapping(value="uploadinvoiceattachment", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody MgstResponse uploadInvoice(@RequestParam("file") MultipartFile multipartFile, 
			@RequestParam("userId") String userId, @RequestParam("returnType") String returnType, @RequestParam("month") String month, 
			@RequestParam("year") String year, @RequestParam("invoiceNo") String invoiceNo,@RequestParam("clientId") String clientId,@RequestParam("invoiceId") String invoiceId){
		Map<String, String> results = storageWriteService.writeObject(userId, returnType, month, year, invoiceNo,invoiceId, clientId, multipartFile);
		return new MgstResponse(MgstResponse.SUCCESS);
	}
	
	@RequestMapping(value="deleteinvoiceattachment", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody MgstResponse deleteInvoiceAttachment(@RequestParam("returnType") String returnType, @RequestParam("invoiceNo") String invoiceNo, 
			@RequestParam("clientId") String clientId,@RequestParam("invoiceId") String invoiceId){
		Map<String, String> results = storageWriteService.deleteObject(returnType, invoiceNo, invoiceId,clientId);
		return new MgstResponse(results.get(RESULTS));
	}


}
