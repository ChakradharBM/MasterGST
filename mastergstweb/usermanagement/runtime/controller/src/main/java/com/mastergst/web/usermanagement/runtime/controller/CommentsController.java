/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.web.usermanagement.runtime.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mastergst.configuration.service.EmailService;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.NullUtil;
import com.mastergst.core.util.VmUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.Comments;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.service.CommentsService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;

@RestController
public class CommentsController {
	
	
	@Autowired
	private CommentsService commentsService;

	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	private EmailService emailService;

	@Autowired
	private UserService userService;

	@PostMapping("/savecomments/{currentUser}")
	public @ResponseBody List<Comments> createComments(@PathVariable String currentUser, @RequestBody Comments comments,
			ModelMap map) {

		
		//Comments cmmnts=commentsService.getCommentsData(currentUser);
			
		/*if(NullUtil.isNotEmpty(cmmnts))	{	
			
			cmmnts.setComments(comments.getComments());
			cmmnts.setCommentDate(comments.getCommentDate());
			cmmnts.setAddedby(comments.getAddedby());
			return commentsService.createComments(cmmnts);
		}else {
		}*/
		
		comments.setUserid(currentUser);
		commentsService.createComments(comments);
		
		List<Comments> cmmntLst=commentsService.getAllComments(currentUser);
		
		Collections.sort(cmmntLst, Collections.reverseOrder());
				
		return cmmntLst;
	}
	
	
	@GetMapping("/getallcomments/{userId}")
	public @ResponseBody List<Comments> getAllComments(@PathVariable String userId, HttpServletRequest request, ModelMap map) {
		String stage = request.getParameter("stage");
		//if(stage.trim() == "") { stage = null; }
		List<Comments> cmmntLst=commentsService.getAllComments(userId, stage);
		Collections.sort(cmmntLst, Collections.reverseOrder());
		//List<Comments> cmmntLst=commentsService.getAllComments(userId);
		
		return cmmntLst;
	}

	@PostMapping("/sendEmail/{userid}")
	public @ResponseBody String senEmails(@PathVariable("userid") String userid, @RequestBody Comments comments) throws MasterGSTException {

		SubscriptionDetails subscriptionDetails = subscriptionService.getRetriveSubscriptionDetailsByObjectId(userid);
		if (NullUtil.isNotEmpty(subscriptionDetails.getUserid())) {

			User user = userService.findById(subscriptionDetails.getUserid());
			comments.setUserid(subscriptionDetails.getUserid());
		
			String[] msg = comments.getComments().split("#MGST#"); 
			
			String subject = msg[0];
			String message = msg[1];
			
			comments.setComments(message);
			commentsService.createComments(comments);
			if (NullUtil.isNotEmpty(user.getEmail())){
				//emailService.sendEnrollEmail(user.getEmail(),VmUtil.velocityTemplate("email.vm", user.getFullname(),emailMeassage, null),emailSubject);
				
				Map<String, Object> data = new HashMap<>();
				data.put("fullname", user.getFullname());
				data.put("message", message);
				sendEmails(user, data, subject);
			}
		}
		return "success";
	}
	
	@PostMapping("/sendRemindercomments/{userid}")
	public String sendReminderComments(@PathVariable String userid, @RequestBody Comments comments, HttpServletRequest request) throws MasterGSTException {
		String stage = request.getParameter("stage");
			
		comments.setUserid(userid);
		if(NullUtil.isNotEmpty(stage)) {
			comments.setStage("partnerpayment");			
		}											 
		String[] msg = comments.getComments().split("#MGST#"); 
		
		String subject = msg[0];
		String message = msg[1];
		
		comments.setComments(message);
		commentsService.createComments(comments);
		User user = userService.findById(userid);
			
		if (NullUtil.isNotEmpty(user)) {
			
			if (NullUtil.isNotEmpty(user.getEmail())){
				Map<String, Object> data = new HashMap<>();
				data.put("fullname", user.getFullname());
				data.put("message", message);
				sendEmails(user, data, subject);
			}
		}
		return "success";
	}
	
	@Async
	private void sendEmails(User user, Map<String, Object> data, String subject) throws MasterGSTException {
		emailService.sendEnrollEmail(user.getEmail(),VmUtil.velocityTemplate("email_subscription.vm", data), subject);		
	}
}
