/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.BodyPart;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Multipart;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.NullUtil;

/**
 * Service Impl class for SMS to perform CRUD operation.
 * 
 * @author Ashok Samart
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class EmailServiceImpl implements EmailService {

	private static final Logger logger = LogManager.getLogger(EmailServiceImpl.class.getName());
	private static final String CLASSNAME = "EmailServiceImpl::";
	@Autowired
	private EmailRepository emailRepository;

	@Value("${mail.smtp.host}")
	private String host;
	@Value("${mail.smtp.port}")
	private String port;
	@Value("${mail.smtp.auth}")
	private String auth;
	@Value("${mail.smtp.from}")
	private String from;
	@Value("${mail.smtp.username}")
	private String username;
	@Value("${mail.smtp.password}")
	private String password;
	@Value("${mail.smtp.subject}")
	private String subject;
	@Value("${mail.enrollment.template}")
	private String template;
	
	@Value("${mail.aws.enabled}")
	private String awsEnabled;
	@Value("${mail.aws.username}")
	private String awsUsername;
	@Value("${mail.aws.password}")
	private String awsPassword;
	@Value("${mail.aws.mailid}")
	private String awsFrom;
	@Value("${mail.aws.host}")
	private String awsHost;

	public void createEmailConfig() {
		EmailConfig emailConfig = new EmailConfig();
		emailConfig.setHost(host);
		emailConfig.setPort(port);
		emailConfig.setAuth(auth);
		emailConfig.setFrom(from);
		emailConfig.setUsername(username);
		emailConfig.setPassword(password);
		emailConfig.setSubject(subject);
		emailConfig.setTemplate(template);
		emailConfig.setAwsEnabled(awsEnabled);
		emailConfig.setAwsUsername(awsUsername);
		emailConfig.setAwsPassword(awsPassword);
		emailConfig.setAwsFrom(awsFrom);
		emailConfig.setAwsHost(awsHost);
		if (NullUtil.isNotEmpty(emailRepository.findAll())) {

			emailRepository.deleteAll();

		}

		emailRepository.save(emailConfig);

	}

	/**
	 * Sends the Eamil to mail id
	 * 
	 * @param fullname
	 *            User Fullname
	 * @param username
	 *            Username
	 * @@param password Password
	 * @throws MasterGSTException
	 */
	public void sendEnrollEmail(final String to, final String templatebody, final String subjecttxt)
			throws MasterGSTException {
		final String method = "sendEnrollEmail(final List<String> tos, final String templatename)::";
		logger.debug(CLASSNAME + method + BEGIN);
		try {
			List<EmailConfig> lEmailConfig = emailRepository.findAll();
			if (NullUtil.isEmpty(lEmailConfig)) {
				logger.debug(CLASSNAME + method + "isEmpty Begin");
				EmailConfig emailConfig = new EmailConfig();
				emailConfig.setHost(host);
				emailConfig.setPort(port);
				emailConfig.setAuth(auth);
				emailConfig.setFrom(from);
				emailConfig.setUsername(username);
				emailConfig.setPassword(password);
				emailConfig.setAwsEnabled(awsEnabled);
				emailConfig.setAwsUsername(awsUsername);
				emailConfig.setAwsPassword(awsPassword);
				emailConfig.setAwsFrom(awsFrom);
				emailConfig.setAwsHost(awsHost);
				emailConfig.setSubject(subjecttxt);
				sendHTMLEmail(to, emailConfig, templatebody);
				logger.debug(CLASSNAME + method + "lEmailConfig end");
			} else {
				EmailConfig emailConfig = lEmailConfig.get(0);
				emailConfig.setSubject(subjecttxt);
				sendHTMLEmail(to, emailConfig, templatebody);
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException(e.getMessage());
		}
		logger.debug(CLASSNAME + method + END);
	}

	public void sendEnrollEmail(final String to, final String cc, final String templatebody, final String subjecttxt)
			throws MasterGSTException {
		final String method = "sendEnrollEmail(final List<String> tos, final String templatename)::";
		logger.debug(CLASSNAME + method + BEGIN);
		try {
			List<EmailConfig> lEmailConfig = emailRepository.findAll();
			if (NullUtil.isEmpty(lEmailConfig)) {
				logger.debug(CLASSNAME + method + "isEmpty Begin");
				EmailConfig emailConfig = new EmailConfig();
				emailConfig.setHost(host);
				emailConfig.setPort(port);
				emailConfig.setAuth(auth);
				emailConfig.setFrom(from);
				emailConfig.setUsername(username);
				emailConfig.setPassword(password);
				emailConfig.setAwsEnabled(awsEnabled);
				emailConfig.setAwsUsername(awsUsername);
				emailConfig.setAwsPassword(awsPassword);
				emailConfig.setAwsFrom(awsFrom);
				emailConfig.setAwsHost(awsHost);
				emailConfig.setSubject(subjecttxt);
				sendHTMLEmail(to, emailConfig, templatebody);
				logger.debug(CLASSNAME + method + "lEmailConfig end");
			} else {
				EmailConfig emailConfig = lEmailConfig.get(0);
				emailConfig.setSubject(subjecttxt);
				sendHTMLEmail(to, emailConfig, templatebody);
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException(e.getMessage());
		}
		logger.debug(CLASSNAME + method + END);
	}
	
	private void sendHTMLEmail(final String to, final EmailConfig emailConfig, final String htmlTemplate)
			throws MasterGSTException {
		final String method = "sendHTMLEmail::";
		logger.debug(CLASSNAME + method + BEGIN);
		try {
			if(NullUtil.isNotEmpty(emailConfig.getAwsEnabled()) && emailConfig.getAwsEnabled().equals("Y")) {
				Properties props = System.getProperties();
		    	props.put("mail.transport.protocol", "smtp");
		    	props.put("mail.smtp.port", 587);
		    	props.put("mail.smtp.starttls.enable", "true");
		    	props.put("mail.smtp.auth", "true");
		    	
		    	Session session = Session.getDefaultInstance(props);
		    	
		    	MimeMessage msg = new MimeMessage(session);
		        msg.setFrom(new InternetAddress(emailConfig.getAwsFrom(), "MasterGST"));
		        msg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		        msg.setSubject(emailConfig.getSubject());
		        msg.setContent(htmlTemplate, "text/html");
		        
		        Transport transport = session.getTransport();
		        transport.connect(emailConfig.getAwsHost(), emailConfig.getAwsUsername(), emailConfig.getAwsPassword());
		        transport.sendMessage(msg, msg.getAllRecipients());
			} else {
				HtmlEmail email = new HtmlEmail();
				email.setHostName(emailConfig.getHost());
				email.setSmtpPort(Integer.parseInt(emailConfig.getPort()));
				email.setAuthenticator(new DefaultAuthenticator(emailConfig.getUsername(), emailConfig.getPassword()));
				email.setSSLOnConnect(true);
	
				email.setFrom(emailConfig.getFrom());
	
				email.setSubject(emailConfig.getSubject());
	
				email.setHtmlMsg(htmlTemplate);
	
				email.addTo(to);
				email.send();
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException(e.getMessage());
		}

		logger.debug(CLASSNAME + method + END);
	}
	
	@Override
	public void sendBulkImportEmail(String to, String cc, String templatebody, String subjecttxt,
			File file) throws MasterGSTException {
		final String method = "sendBulkImportEmail(final List<String> tos, final String templatename)::";
		logger.debug(CLASSNAME + method + BEGIN);
		try {
			List<EmailConfig> lEmailConfig = emailRepository.findAll();
			if (NullUtil.isEmpty(lEmailConfig)) {
				logger.debug(CLASSNAME + method + "lEmailConfig ismpty Begin");
				EmailConfig emailConfig = new EmailConfig();
				emailConfig.setHost(host);
				emailConfig.setPort(port);
				emailConfig.setAuth(auth);
				emailConfig.setFrom(from);
				emailConfig.setUsername(username);
				emailConfig.setPassword(password);
				emailConfig.setAwsEnabled(awsEnabled);
				emailConfig.setAwsUsername(awsUsername);
				emailConfig.setAwsPassword(awsPassword);
				emailConfig.setAwsFrom(awsFrom);
				emailConfig.setAwsHost(awsHost);
				emailConfig.setSubject(subjecttxt);
				sendBulkImportEmail(to, cc, emailConfig, templatebody,file);
				logger.debug(CLASSNAME + method + "lEmailConfig end");
			} else {
				EmailConfig emailConfig = lEmailConfig.get(0);
				emailConfig.setSubject(subjecttxt);
				sendBulkImportEmail(to, cc, emailConfig, templatebody,file);
				
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException(e.getMessage());
		}
		logger.debug(CLASSNAME + method + END);	
	}
	
	private void sendBulkImportEmail(final String to, String cc, final EmailConfig emailConfig, final String htmlTemplate, File file)
			throws MasterGSTException {
		final String method = "sendHTMLEmail::";
		logger.debug(CLASSNAME + method + BEGIN);
		try {
			if(NullUtil.isNotEmpty(emailConfig.getAwsEnabled()) && emailConfig.getAwsEnabled().equals("Y")) {
				Properties props = System.getProperties();
		    	props.put("mail.transport.protocol", "smtp");
		    	props.put("mail.smtp.port", 587);
		    	props.put("mail.smtp.starttls.enable", "true");
		    	props.put("mail.smtp.auth", "true");
		    	
		    	Session session = Session.getDefaultInstance(props);
		    	
		    	MimeMessage msg = new MimeMessage(session);
		        msg.setFrom(new InternetAddress(emailConfig.getAwsFrom(), "MasterGST"));
		        msg.setRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		        msg.setSubject(emailConfig.getSubject());
		        //msg.setContent(htmlTemplate, "text/html");
		        if(NullUtil.isNotEmpty(cc)) {
		        	msg.setRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(cc));		        
		        }
		        BodyPart messageBodyPart = new MimeBodyPart();
		         messageBodyPart.setContent(htmlTemplate, "text/html");
		         Multipart multipart = new MimeMultipart();
		         multipart.addBodyPart(messageBodyPart);
		         
		         MimeBodyPart msgp = new MimeBodyPart();
		        
		         if(file!=null) { 
		        	 msgp.attachFile(file); 
		        	 multipart.addBodyPart(msgp);
		         }
		         msg.setContent(multipart);
		        Transport transport = session.getTransport();
		        transport.connect(emailConfig.getAwsHost(), emailConfig.getAwsUsername(), emailConfig.getAwsPassword());
		        transport.sendMessage(msg, msg.getAllRecipients());
			} else {
				HtmlEmail email = new HtmlEmail();
				email.setHostName(emailConfig.getHost());
				email.setSmtpPort(Integer.parseInt(emailConfig.getPort()));
				email.setAuthenticator(new DefaultAuthenticator(emailConfig.getUsername(), emailConfig.getPassword()));
				//email.setSSLOnConnect(true);
				email.setSSLOnConnect(true);
	
				email.setFrom(emailConfig.getFrom());
	
				email.setSubject(emailConfig.getSubject());
			 if(file!=null) { email.attach(file); }
			 
				email.setHtmlMsg(htmlTemplate);
				
				email.addTo(to);
				if(NullUtil.isNotEmpty(cc)) {
					 email.addCc(cc);
				}
				email.send();
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException(e.getMessage());
		}

		logger.debug(CLASSNAME + method + END);
	}
	
	
	
	public void sendEnrollEmail(final String to, final String templatebody, final String subjecttxt,List<String> CC) throws MasterGSTException {
		final String method = "sendEnrollEmail(final List<String> tos, final String templatename)::";
		logger.debug(CLASSNAME + method + BEGIN);
		try {
			List<EmailConfig> lEmailConfig = emailRepository.findAll();
			if (NullUtil.isEmpty(lEmailConfig)) {
				logger.debug(CLASSNAME + method + "isEmpty Begin");
				EmailConfig emailConfig = new EmailConfig();
				emailConfig.setHost(host);
				emailConfig.setPort(port);
				emailConfig.setAuth(auth);
				emailConfig.setFrom(from);
				emailConfig.setUsername(username);
				emailConfig.setPassword(password);
				emailConfig.setAwsEnabled(awsEnabled);
				emailConfig.setAwsUsername(awsUsername);
				emailConfig.setAwsPassword(awsPassword);
				emailConfig.setAwsFrom(awsFrom);
				emailConfig.setAwsHost(awsHost);
				emailConfig.setSubject(subjecttxt);
				sendHTMLEmail(to, emailConfig, templatebody);
				logger.debug(CLASSNAME + method + "lEmailConfig end");
			} else {
				EmailConfig emailConfig = lEmailConfig.get(0);
				emailConfig.setSubject(subjecttxt);
				if(NullUtil.isEmpty(CC)) {
					sendHTMLEmail(to, emailConfig, templatebody);
				}else {
					sendHTMLEmail(to, emailConfig, templatebody,CC);
				}
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException(e.getMessage());
		}
		logger.debug(CLASSNAME + method + END);
	}
	private void sendHTMLEmail(final String to, final EmailConfig emailConfig, final String htmlTemplate, List<String> CC)
			throws MasterGSTException {
		final String method = "sendHTMLEmail::";
		logger.debug(CLASSNAME + method + BEGIN);
		try {
			if(NullUtil.isNotEmpty(emailConfig.getAwsEnabled()) && emailConfig.getAwsEnabled().equals("Y")) {
				Properties props = System.getProperties();
		    	props.put("mail.transport.protocol", "smtp");
		    	props.put("mail.smtp.port", 587);
		    	props.put("mail.smtp.starttls.enable", "true");
		    	props.put("mail.smtp.auth", "true");
		    	
		    	Session session = Session.getDefaultInstance(props);
		    	
		    	MimeMessage msg = new MimeMessage(session);
		        msg.setFrom(new InternetAddress(emailConfig.getAwsFrom(), "MasterGST"));
		        msg.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		        msg.setSubject(emailConfig.getSubject());
		        msg.setContent(htmlTemplate, "text/html");
		        if(NullUtil.isNotEmpty(CC)) {
					  for(String em:CC) { 
						  //msg.setRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(em));
						  if(NullUtil.isNotEmpty(em)) {
							  msg.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(em));
						  }
					  }
		        } 
		        Transport transport = session.getTransport();
		        transport.connect(emailConfig.getAwsHost(), emailConfig.getAwsUsername(), emailConfig.getAwsPassword());
		        transport.sendMessage(msg, msg.getAllRecipients());
			} else {
				HtmlEmail email = new HtmlEmail();
				email.setHostName(emailConfig.getHost());
				email.setSmtpPort(Integer.parseInt(emailConfig.getPort()));
				email.setAuthenticator(new DefaultAuthenticator(emailConfig.getUsername(), emailConfig.getPassword()));
				email.setSSLOnConnect(true);
	
				email.setFrom(emailConfig.getFrom());
	
				email.setSubject(emailConfig.getSubject());
	
				email.setHtmlMsg(htmlTemplate);
				 if(NullUtil.isNotEmpty(CC)) {
					for(String em:CC) {
						email.addCc(em);
					}
				 }
				email.addTo(to);
				email.send();
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException(e.getMessage());
		}

		logger.debug(CLASSNAME + method + END);
	}

	@Override
	public void sendAttachmentEmail(String to, List<String> ccmail, String templatebody, String subjecttxt,File file) throws MasterGSTException {
		final String method = "sendAttachmentEmail(final List<String> tos, final String templatename)::";
		logger.debug(CLASSNAME + method + BEGIN);
		try {
			List<EmailConfig> lEmailConfig = emailRepository.findAll();
			if (NullUtil.isEmpty(lEmailConfig)) {
				logger.debug(CLASSNAME + method + "isEmpty Begin");
				EmailConfig emailConfig = new EmailConfig();
				emailConfig.setHost(host);
				emailConfig.setPort(port);
				emailConfig.setAuth(auth);
				emailConfig.setFrom(from);
				emailConfig.setUsername(username);
				emailConfig.setPassword(password);
				emailConfig.setAwsEnabled(awsEnabled);
				emailConfig.setAwsUsername(awsUsername);
				emailConfig.setAwsPassword(awsPassword);
				emailConfig.setAwsFrom(awsFrom);
				emailConfig.setAwsHost(awsHost);
				emailConfig.setSubject(subjecttxt);
				sendAttachmentEmail(to, ccmail, emailConfig, templatebody,file);
				logger.debug(CLASSNAME + method + "lEmailConfig end");
			} else {
				EmailConfig emailConfig = lEmailConfig.get(0);
				emailConfig.setSubject(subjecttxt);
				sendAttachmentEmail(to, ccmail, emailConfig, templatebody,file);
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException(e.getMessage());
		}
		logger.debug(CLASSNAME + method + END);
		
	}

	private void sendAttachmentEmail(String to, List<String> ccmail, EmailConfig emailConfig, String templatebody,File file) throws MasterGSTException {
		final String method = "sendAttachmentEmail::";
		logger.debug(CLASSNAME + method + BEGIN);
		try {
			if(NullUtil.isNotEmpty(emailConfig.getAwsEnabled()) && emailConfig.getAwsEnabled().equals("Y")) {
				Properties props = System.getProperties();
		    	props.put("mail.transport.protocol", "smtp");
		    	props.put("mail.smtp.port", 587);
		    	props.put("mail.smtp.starttls.enable", "true");
		    	props.put("mail.smtp.auth", "true");
		    	
		    	Session session = Session.getDefaultInstance(props);
		    	
		    	MimeMessage msg = new MimeMessage(session);
		        msg.setFrom(new InternetAddress(emailConfig.getAwsFrom(), "MasterGST"));
		        msg.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		        msg.setSubject(emailConfig.getSubject());
		        //msg.setContent(htmlTemplate, "text/html");
		       
		        BodyPart messageBodyPart = new MimeBodyPart();
		         messageBodyPart.setContent(templatebody, "text/html");
		         Multipart multipart = new MimeMultipart();
		         multipart.addBodyPart(messageBodyPart);
		         
		         MimeBodyPart msgp = new MimeBodyPart();
		        
		         if(file!=null) { 
		        	 //msgp.attachFile(file); 
		        	 FileDataSource fds = new FileDataSource(file);   	
		        	 msgp.setDataHandler(new DataHandler(fds));
		        	 msgp.setFileName(fds.getName()+".pdf");
		        	 msgp.setDisposition("attachment");
		        	 multipart.addBodyPart(msgp);
		         }
		         msg.setContent(multipart);
		         if(NullUtil.isNotEmpty(ccmail) && ccmail.size() >= 1) {
		        	 if(NullUtil.isNotEmpty(ccmail)) {
		        		 for(String em:ccmail) { 
		        			 if(NullUtil.isNotEmpty(em)) {
		        				 msg.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(em));
		        			 }
		        		 }
		        	 }
		        } 
		        Transport transport = session.getTransport();
		        transport.connect(emailConfig.getAwsHost(), emailConfig.getAwsUsername(), emailConfig.getAwsPassword());
		        transport.sendMessage(msg, msg.getAllRecipients());
			} else {
				HtmlEmail email = new HtmlEmail();
				email.setHostName(emailConfig.getHost());
				email.setSmtpPort(Integer.parseInt(emailConfig.getPort()));
				email.setAuthenticator(new DefaultAuthenticator(emailConfig.getUsername(), emailConfig.getPassword()));
				//email.setSSLOnConnect(true);
				email.setSSLOnConnect(true);
	
				email.setFrom(emailConfig.getFrom());
	
				email.setSubject(emailConfig.getSubject());
				if(file!=null) {
					email.attach(file); 
					//FileDataSource fds = new FileDataSource(file);
					//email.attach(fds, file.getName()+"", null, EmailAttachment.ATTACHMENT);
				 }
				email.setHtmlMsg(templatebody);
				if(NullUtil.isNotEmpty(ccmail) && ccmail.size() > 1) {
					for(String em:ccmail) {
						email.addCc(em);
					}
				 }
				email.addTo(to);
				email.send();
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException(e.getMessage());
		}

		logger.debug(CLASSNAME + method + END);
		
	}
	
	@Override
	public void sendCompaignEmail(final String to, final String templatebody, final String subjecttxt) throws MasterGSTException {
		final String method = "sendEnrollEmail(final List<String> tos, final String templatename)::";
		logger.debug(CLASSNAME + method + BEGIN);
		try {
			List<EmailConfig> lEmailConfig = emailRepository.findAll();
			if (NullUtil.isEmpty(lEmailConfig)) {
				logger.debug(CLASSNAME + method + "isEmpty Begin");
				EmailConfig emailConfig = new EmailConfig();
				emailConfig.setHost(host);
				emailConfig.setPort(port);
				emailConfig.setAuth(auth);
				emailConfig.setFrom(from);
				emailConfig.setUsername(username);
				emailConfig.setPassword(password);
				emailConfig.setAwsEnabled(awsEnabled);
				emailConfig.setAwsUsername(awsUsername);
				emailConfig.setAwsPassword(awsPassword);
				emailConfig.setAwsFrom(awsFrom);
				emailConfig.setAwsHost(awsHost);
				emailConfig.setSubject(subjecttxt);
				sendHTMLEmail(to, emailConfig, templatebody);
				logger.debug(CLASSNAME + method + "lEmailConfig end");
			} else {
				EmailConfig emailConfig = lEmailConfig.get(0);
				emailConfig.setSubject(subjecttxt);
				sendHTMLEmail(to, emailConfig, templatebody);
			}
		} catch (Exception e) {
			logger.error(CLASSNAME + method + e.getMessage());
			throw new MasterGSTException(e.getMessage());
		}
		logger.debug(CLASSNAME + method + END);
	}

	
}
