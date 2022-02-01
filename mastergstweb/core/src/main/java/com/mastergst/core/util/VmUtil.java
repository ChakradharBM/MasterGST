/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.core.util;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.mastergst.core.exception.MasterGSTException;

public class VmUtil {

	private static final Logger logger = LogManager.getLogger(VmUtil.class.getName());
	private static final String CLASSNAME = "VmUtil::";

	public static String velocityTemplate(String templateName, String fullname, String username, String password)
			throws MasterGSTException {
		final String method = "velocityTemplate::";
		logger.debug(CLASSNAME + method + BEGIN);
		String html;
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty("resource.loader", "class");
			ve.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init();
			Template t = ve.getTemplate(templateName);
			VelocityContext context = new VelocityContext();
			if (NullUtil.isNotEmpty(fullname)) {
				context.put("fullname", fullname);
			}
			if (NullUtil.isNotEmpty(username)) {
				context.put("username", username);
			}
			if (NullUtil.isNotEmpty(password)) {
				context.put("password", password);
			}
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			html = writer.toString();

		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException("Velocity Exception");
		}
		logger.debug(CLASSNAME + method + END);
		return html;
	}
	
	public static String velocityTemplate(String templateName, Map<String, Object> data)
			throws MasterGSTException {
		final String method = "velocityTemplate::";
		logger.debug(CLASSNAME + method + BEGIN);
		String html;
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty("resource.loader", "class");
			ve.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init();
			Template t = ve.getTemplate(templateName);
			VelocityContext context = new VelocityContext();
			data.forEach(context::put);
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			html = writer.toString();

		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException("Velocity Exception");
		}
		logger.debug(CLASSNAME + method + END);
		return html;
	}
	
	
	public static String velocityTemplate(String templateName, String fullname, String username, String password,Map<String,String> userdetails)
			throws MasterGSTException {
		final String method = "velocityTemplate::";
		logger.debug(CLASSNAME + method + BEGIN);
		String html;
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty("resource.loader", "class");
			ve.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init();
			Template t = ve.getTemplate(templateName);
			VelocityContext context = new VelocityContext();
			if (NullUtil.isNotEmpty(fullname)) {
				context.put("fullname", fullname);
			}
			if (NullUtil.isNotEmpty(username)) {
				context.put("username", username);
			}
			if (NullUtil.isNotEmpty(password)) {
				context.put("password", password);
			}
			if (NullUtil.isNotEmpty(userdetails)) {
				context.put("userdetails", userdetails);
			}
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			html = writer.toString();

		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException("Velocity Exception");
		}
		logger.debug(CLASSNAME + method + END);
		return html;
	}

	
	public static String velocityTemplate(String templateName, String userguide,String fullname, String username, String password)
			throws MasterGSTException {
		final String method = "velocityTemplate::";
		logger.debug(CLASSNAME + method + BEGIN);
		String html;
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty("resource.loader", "class");
			ve.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init();
			Template t = ve.getTemplate(templateName);
			VelocityContext context = new VelocityContext();
			if (NullUtil.isNotEmpty(fullname)) {
				context.put("fullname", fullname);
			}
			if (NullUtil.isNotEmpty(userguide)) {
				context.put("userguide", userguide);
			}
			if (NullUtil.isNotEmpty(username)) {
				context.put("username", username);
			}
			if (NullUtil.isNotEmpty(password)) {
				context.put("password", password);
			}
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			html = writer.toString();

		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException("Velocity Exception");
		}
		logger.debug(CLASSNAME + method + END);
		return html;
	}

	public static String velocityTemplate(String templateName) throws MasterGSTException {
		final String method = "velocityTemplate::";
		logger.debug(CLASSNAME + method + BEGIN);
		String html;
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty("resource.loader", "class");
			ve.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init();
			Template t = ve.getTemplate(templateName);
			VelocityContext context = new VelocityContext();
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			html = writer.toString();
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException("Velocity Exception");
		}
		logger.debug(CLASSNAME + method + END);
		return html;
	}
	
	
	public static String velocityTemplate(String templateName,String fullname, List<ImportSummary> summary,ImportResponse resource) throws MasterGSTException {
		
		final String method = "velocityTemplate::";
		logger.debug(CLASSNAME + method + BEGIN);
		String html;
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty("resource.loader", "class");
			ve.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init();
			
			Template t = ve.getTemplate(templateName);
			VelocityContext context = new VelocityContext();
			if (NullUtil.isNotEmpty(fullname)) {
				context.put("fullname", fullname);
			}
			if (NullUtil.isNotEmpty(summary)) {
				context.put("summary", summary);
			}
			if (NullUtil.isNotEmpty(resource)) {
				context.put("resource", resource);
			}
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			html = writer.toString();
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException("Velocity Exception");
		}
		logger.debug(CLASSNAME + method + END);
		return html;
	}
	public static String velocityTemplate(String templateName,Map<String, List<SendMsgsSummary>> invsummary, String pos, Map<String, String> clientDetails, String fullname, String msgdesc)
			throws MasterGSTException {
		final String method = "velocityTemplate::";
		logger.debug(CLASSNAME + method + BEGIN);
		String html;
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty("resource.loader", "class");
			ve.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init();
			Template t = ve.getTemplate(templateName);
			VelocityContext context = new VelocityContext();
			if (NullUtil.isNotEmpty(fullname)) {
				context.put("fullname", fullname);
			}
			if (NullUtil.isNotEmpty(pos)) {
				context.put("pos", pos);
			}
			if (NullUtil.isNotEmpty(clientDetails)) {
				context.put("client", clientDetails);
			}
		
			if (NullUtil.isNotEmpty(msgdesc)) {
				context.put("msgdesc", msgdesc);
			}
			
			if (NullUtil.isNotEmpty(invsummary)) {
				context.put("invsummary", invsummary);
			}
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			html = writer.toString();

		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException("Velocity Exception");
		}
		logger.debug(CLASSNAME + method + END);
		return html;
	}
	public static String velocityTemplate(String templateName, String userguide,String fullname, String username, String password,List<String> CC)
			throws MasterGSTException {
		final String method = "velocityTemplate::";
		logger.debug(CLASSNAME + method + BEGIN);
		String html;
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty("resource.loader", "class");
			ve.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init();
			Template t = ve.getTemplate(templateName);
			VelocityContext context = new VelocityContext();
			if (NullUtil.isNotEmpty(fullname)) {
				context.put("fullname", fullname);
			}
			if (NullUtil.isNotEmpty(userguide)) {
				context.put("userguide", userguide);
			}
			if (NullUtil.isNotEmpty(username)) {
				context.put("username", username);
			}
			if (NullUtil.isNotEmpty(password)) {
				context.put("password", password);
			}
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			html = writer.toString();

		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException("Velocity Exception");
		}
		logger.debug(CLASSNAME + method + END);
		return html;
	}

	public static String velocityTemplate(String templateName, Map<String, List<SendMsgsSummary>> invsummary,Map<String,String> clientDetails, Map<String,String> invDetails, boolean signcheck) throws MasterGSTException {
		final String method = "velocityTemplate::";
		logger.debug(CLASSNAME + method + BEGIN);
		String html;
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty("resource.loader", "class");
			ve.setProperty("class.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			ve.init();
			Template t = ve.getTemplate(templateName);
			VelocityContext context = new VelocityContext();
			if (NullUtil.isNotEmpty(clientDetails)) {
				context.put("clientDetails", clientDetails);
			}
		
			if (NullUtil.isNotEmpty(invDetails)) {
				context.put("invDetails", invDetails);
			}
		
			if (NullUtil.isNotEmpty(invsummary)) {
				context.put("invsummary", invsummary);
			}
			if (NullUtil.isNotEmpty(signcheck)) {
				context.put("signcheck", signcheck);
			}
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			html = writer.toString();

		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException("Velocity Exception");
		}
		logger.debug(CLASSNAME + method + END);
		return html;

	}

}
