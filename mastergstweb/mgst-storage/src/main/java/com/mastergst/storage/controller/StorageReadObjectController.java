package com.mastergst.storage.controller;

import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mastergst.storage.service.StorageReadService;

@Controller
public class StorageReadObjectController {

	private static final Logger logger = LogManager.getLogger(StorageReadObjectController.class.getName());
	
	@Autowired
	private StorageReadService storageReadService;

	@RequestMapping(value="viewinvoiceattachment", method=RequestMethod.GET)
	public void deleteInvoiceAttachment(@RequestParam("returnType") String returnType, @RequestParam(value="invoiceNo", required = false) String invoiceNo, 
			@RequestParam(value="clientId", required = false) String clientId, @RequestParam("invoiceId") String invoiceId,HttpServletResponse response) throws Exception{
		Object[] result = storageReadService.readObject(returnType, invoiceNo,invoiceId, clientId);
		/*
		 * InputStream ins = (InputStream)result[0];
		 * response.setContentType((String)result[1]);
		 * response.addHeader("Content-Disposition", "attachment; filename="+result[2]);
		 * IOUtils.copy(ins, response.getOutputStream());
		 * response.getOutputStream().flush();
		 */
		if(result != null){
			InputStream ins = (InputStream)result[0];
			String fileName = result[2].toString();
			int indx = fileName.lastIndexOf(".");
			if(indx != -1){
				String extn = fileName.substring(indx+1);;
				if("pdf".equalsIgnoreCase(extn)){
					response.setContentType("application/pdf");
				}else if("gif".equalsIgnoreCase(extn) || "jpeg".equalsIgnoreCase(extn) || "png".equalsIgnoreCase(extn) || "tiff".equalsIgnoreCase(extn)
						|| "jpg".equalsIgnoreCase(extn)){
					if("jpg".equalsIgnoreCase(extn)){
						extn = "jpeg";
					}
					response.setContentType("image/"+extn);
				}else if("excel".equalsIgnoreCase(extn)){
					response.setContentType("application/vnd.ms-excel");
				}else if("doc".equalsIgnoreCase(extn) || "docx".equalsIgnoreCase(extn)){
					response.setContentType("application/msword");
				}else{
					response.setContentType((String)result[1]);
					response.addHeader("Content-Disposition", "attachment; filename="+result[2]);
				}
			}else{
				response.setContentType((String)result[1]);
				response.addHeader("Content-Disposition", "attachment; filename="+result[2]);
			}
			IOUtils.copy(ins, response.getOutputStream());
			response.getOutputStream().flush();
		}else{
			response.setContentType("text/html");
			response.getWriter().print("<h2>No Document Found</h2>");
			response.getWriter().flush();
		}
	}
}
