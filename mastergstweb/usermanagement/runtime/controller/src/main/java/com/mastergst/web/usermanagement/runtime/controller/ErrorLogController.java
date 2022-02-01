/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorLogController {
	
	private static final Logger logger = LogManager.getLogger(ErrorLogController.class.getName());
	private static final String CLASSNAME = "ErrorLogController::";

	@Value("${error.log.file}")
	private String errLogFile;
	
	@RequestMapping(value = "/errorlog", method = RequestMethod.GET)
	public String errorLog(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname, 
			@RequestParam(value = "usertype", required = false) String userType, Model model) throws Exception {
		final String method = "errorLog";
		logger.debug(CLASSNAME + method + BEGIN);
		StringBuilder logContent = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(errLogFile))) {
			stream.forEach(logContent::append);
		} catch (IOException e) {
			logger.error("Exception occured while reading the data");
		}
		model.addAttribute("logContent", logContent.toString());
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", userType);
		return "admin/errorLog";
	}
}
