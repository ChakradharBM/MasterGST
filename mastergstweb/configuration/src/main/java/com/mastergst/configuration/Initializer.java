/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class Initializer extends AbstractAnnotationConfigDispatcherServletInitializer  {

	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { WebAppConfig.class };
	}
 
	protected Class<?>[] getServletConfigClasses() {
		return null;
	}
 
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		servletContext.addListener(new SessionListener());
	}
	@Override
	protected Filter[] getServletFilters() {
		return new Filter[]{new MasterGstAuthenticationFilter()};
	}

}
