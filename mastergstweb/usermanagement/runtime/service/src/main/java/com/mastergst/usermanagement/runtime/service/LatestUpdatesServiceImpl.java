package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mastergst.configuration.service.LatestUpdates;
import com.mastergst.configuration.service.LatestUpdatesRepository;



@Service
public class LatestUpdatesServiceImpl implements LatestUpdatesService{

	
	@Autowired
	private LatestUpdatesRepository latestUpdatesRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public void saveLastestUpdates(LatestUpdates updates) {
		latestUpdatesRepository.save(updates);
	}

	@Override
	public List<LatestUpdates> getAllUpdates() {
		Sort sort = new Sort(new Order(Direction.DESC, "updatedDate"));
		return (List<LatestUpdates>) latestUpdatesRepository.findAll(sort);
	}

	@Override
	public LatestUpdates updateUpdates(String id, LatestUpdates update) {
		// TODO Auto-generated method stub
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update updatenu = new Update();
		updatenu.set("title", update.getTitle());
		updatenu.set("description", update.getDescription());
		updatenu.set("usertype", update.getUsertype());
		mongoTemplate.upsert(query, updatenu, LatestUpdates.class);
		return getUpdatedata(id);
	}

	@Override
	public LatestUpdates getUpdatedata(String id) {
		// TODO Auto-generated method stub
		return latestUpdatesRepository.findOne(id);
	}
	
	@Override
	public LatestUpdates addupdates(LatestUpdates updates) {
		// TODO Auto-generated method stub
		return latestUpdatesRepository.save(updates);
	}

}
