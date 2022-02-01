package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import com.mastergst.configuration.service.LatestNews;

public interface LatestNewsService {
	
	public List<LatestNews> getAllNews();
	
	
	LatestNews getNewsdata(String id);
	
	LatestNews updateNews(String id, LatestNews news);
	
	LatestNews addnews(LatestNews news);

}
