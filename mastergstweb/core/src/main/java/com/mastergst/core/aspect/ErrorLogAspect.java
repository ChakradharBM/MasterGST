/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.core.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.mastergst.core.domain.UserIdHolder;


@Component
@Aspect
public class ErrorLogAspect {
	
	//@AfterThrowing(pointcut = "within(com.mastergst..*)", throwing="exception")
	public void logBefore(JoinPoint joinPoint, Exception exception) {
		Logger logger = LogManager.getLogger(joinPoint.getTarget().getClass().getName());
		logger.error("\n###################UserId:"+UserIdHolder.getCurrentUserId()+"#######################################################################################################################################\n");
		logger.error(exception.getMessage(), exception);
		logger.error("\n##########################################################################################################################################################\n");
	}

}
