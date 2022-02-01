package com.mastergst.usermanagement.runtime.service;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mastergst.configuration.service.LatestUpdates;
import com.mastergst.configuration.service.LatestUpdatesRepository;
import com.mastergst.configuration.service.Message;
import com.mastergst.configuration.service.MessageRepository;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;

/**
 * Service Impl class for Subscription services to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService {
	
	private static final Logger logger = LogManager.getLogger(MessageServiceImpl.class.getName());
	private static final String CLASSNAME = "MessageServiceImpl::";
	
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private LatestUpdatesRepository latestUpdatesRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public Message addMessage(Message message) {
		return messageRepository.save(message);
	}
	
	@Override
	public Message updateMessage(String id, Message message) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update update = new Update();
		update.set("message", message.getMessage());
		update.set("subject", message.getSubject());
		update.set("usertype", message.getUsertype());
		mongoTemplate.upsert(query, update, Message.class);
		return getMessage(id);
	}
	
	@Override
	public Message getMessage(String id) {
		return messageRepository.findOne(id);
		
	}
	
	@Override
	public List<Message> getMessages() {
		return (List<Message>)messageRepository.findAll();
	}
	
	@Override
	public List<Message> getMessagesByUserType(String userType) {
		Sort sort = new Sort(new Order(Direction.DESC, "createdDate"));
		return (List<Message>)messageRepository.findByUserType(userType,sort);
	}
	
	@Override
	public Page<Message> getMessages(int page, int length,String sortFieldName, String order, String searchVal) {
		Query query = new Query();
		if(NullUtil.isNotEmpty(searchVal)){
			query.addCriteria(new Criteria().orOperator(Criteria.where("message").regex(searchVal,"i"), Criteria.where("subject").regex(searchVal,"i"), Criteria.where("usertype").regex(searchVal)));
		}
		Sort sort = null;
		if(NullUtil.isNotEmpty(sortFieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, sortFieldName));
		}
		Pageable pageable = new PageRequest(page, length, sort);
		query.with(pageable);
		
		long total = mongoTemplate.count(query, User.class, "messages");
		if (total == 0) {
			return new PageImpl<Message>(Collections.<Message> emptyList());
		}
		return new PageImpl<Message>(mongoTemplate.find(query, Message.class, "messages"), pageable, total);
	}


	@Override
	public Page<LatestUpdates> getUpadtes(int page, int length, String sortFieldName, String order, String searchVal) {
		// TODO Auto-generated method stub
		Query query = new Query();
		if(NullUtil.isNotEmpty(searchVal)){
			query.addCriteria(new Criteria().orOperator(Criteria.where("title").regex(searchVal,"i"), Criteria.where("description").regex(searchVal,"i"), Criteria.where("usertype").regex(searchVal)));
		}
		Sort sort = null;
		if(NullUtil.isNotEmpty(sortFieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, sortFieldName));
		}
		Pageable pageable = new PageRequest(page, length, sort);
		query.with(pageable);
		
		long total = mongoTemplate.count(query, User.class, "updates");
		if (total == 0) {
			return new PageImpl<LatestUpdates>(Collections.<LatestUpdates> emptyList());
		}
		return new PageImpl<LatestUpdates>(mongoTemplate.find(query, LatestUpdates.class, "updates"), pageable, total);
	}
}
