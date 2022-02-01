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

import com.mastergst.configuration.service.LatestNews;
import com.mastergst.configuration.service.LatestNewsRepository;


@Service
public class LatestNewsServiceImpl implements LatestNewsService{

	
	@Autowired
	private LatestNewsRepository latestNewsRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	
	@Override
	public List<LatestNews> getAllNews() {
		Sort sort = new Sort(new Order(Direction.DESC, "updatedDate"));
		return (List<LatestNews>) latestNewsRepository.findAll(sort);
	}

	@Override
	public LatestNews updateNews(String id, LatestNews news) {
		// TODO Auto-generated method stub
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id));
		Update updatenu = new Update();
		updatenu.set("title", news.getTitle());
		updatenu.set("description", news.getDescription());
		updatenu.set("usertype", news.getUsertype());
		mongoTemplate.upsert(query, updatenu, LatestNews.class);
		return getNewsdata(id);
	}

	@Override
	public LatestNews getNewsdata(String id) {
		// TODO Auto-generated method stub
		return latestNewsRepository.findOne(id);
	}

	@Override
	public LatestNews addnews(LatestNews news) {
		// TODO Auto-generated method stub
		return latestNewsRepository.save(news);
	}


	

}
