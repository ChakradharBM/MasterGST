/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mastergst.core.domain.UserIdHolder;

public class MasterGstAuthenticationFilter implements Filter {
	
	private String contextPath = null;
	
	private AuthenticationValidator authenticationValidator;
	
	@Override
	public void init(FilterConfig chain) throws ServletException {
		String contextPath = chain.getServletContext().getContextPath();
		this.contextPath = contextPath;
		authenticationValidator = new AuthenticationValidator(contextPath);
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpResp = (HttpServletResponse) response;
		HttpSession session = httpReq.getSession(false);
		String path = (httpReq).getRequestURI();
		if((session != null && session.getAttribute("User") != null) || authenticationValidator.isValid(path)){
			if(session != null){
				UserIdHolder.setCurrentUserId((String)session.getAttribute("User"));
			}
			
				chain.doFilter(request, response);
			
		} else {
			httpResp.sendRedirect(contextPath+"/login");
		}
	}
	
	@Override
	public void destroy() {
		
	}

}
