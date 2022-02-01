/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.core.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorAdvice {
	
	private static final Logger logger = LogManager.getLogger(ErrorAdvice.class.getName());

	@ExceptionHandler
	public String handleException(Exception exception, Model model) {
		logger.error(exception.getMessage(), exception);
		model.addAttribute("errorMsg", exception.getMessage());
		return "includes/error";
	}
}
