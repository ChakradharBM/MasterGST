package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import com.mastergst.configuration.service.LatestNews;
import com.mastergst.configuration.service.LatestUpdates;

public interface LatestUpdatesService {
	public void saveLastestUpdates(LatestUpdates updates);
	
	public List<LatestUpdates> getAllUpdates();
	
	
	LatestUpdates getUpdatedata(String id);
	
	LatestUpdates updateUpdates(String id, LatestUpdates update);
	
	LatestUpdates addupdates(LatestUpdates updates);

}
