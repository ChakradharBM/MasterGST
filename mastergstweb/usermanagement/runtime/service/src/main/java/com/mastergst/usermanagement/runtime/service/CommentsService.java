package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import com.mastergst.usermanagement.runtime.domain.Comments;

public interface CommentsService {
	
	
	public Comments createComments(Comments comments);
	public Comments getCommentsData(String id);
	public List<Comments> getAllComments(String id);
	public List<Comments> getAllComments(String id, String stage);

}
