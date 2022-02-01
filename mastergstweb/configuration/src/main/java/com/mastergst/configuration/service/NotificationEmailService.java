package com.mastergst.configuration.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationEmailService {
	private static final Logger logger = LoggerFactory
			.getLogger(NotificationEmailService.class);
	private String host;
	private String port;
	private String username;
	private String password;
	private String from;
	private String toAddress;
	private String ccAddress;
	private String subject;
	private String body;
	
	public NotificationEmailService(String host, String port, String username, String password, String from, String toAddress, 
			String ccAddress, String subject, String body){
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.from = from;
		this.toAddress = toAddress;
		this.ccAddress = ccAddress;
		this.subject = subject;
		this.body = body;
	}
	
	public void send() throws Exception{
		try {
			HtmlEmail email = new HtmlEmail();
			email.setHostName(this.host);
			email.setSmtpPort(Integer.parseInt(this.port));
			email.setAuthenticator(new DefaultAuthenticator(this.username, this.password));
			email.setSSLOnConnect(true);
			email.setFrom(this.from );
			email.setSubject(this.subject);
			email.setHtmlMsg(this.body);
			if(toAddress != null && !"".equals(toAddress.trim())){
				String[] tos = toAddress.split(",");
				for(String to : tos){
					email.addTo(to);
				}
			}
			if(ccAddress != null && !"".equals(ccAddress.trim())){
				String[] ccs = ccAddress.split(",");
				for(String cc : ccs){
					email.addCc(cc);
				}
			}
			email.send();
		} catch (NumberFormatException e) {
			logger.error("Exception occured", e);
			throw new Exception(e);
		} catch (EmailException e) {
			logger.error("Exception occured", e);
			throw new Exception(e);
		}
	}

}
