/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.mastergst.configuration.service.LatestUpdates;
import com.mastergst.configuration.service.Message;

/**
 * Service interface for Message logic to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
public interface MessageService {

	Message addMessage(Message message);
	
	Message updateMessage(String id, Message message);
	
	Message getMessage(String id);
	
	List<Message> getMessages();
	
	Page<Message> getMessages(int page, int length, String sortFieldName, String order, String searchVal);
	
	List<Message> getMessagesByUserType(String userType);

	Page<LatestUpdates> getUpadtes(int page, int length, String sortFieldName, String order, String searchVal);
	
}
