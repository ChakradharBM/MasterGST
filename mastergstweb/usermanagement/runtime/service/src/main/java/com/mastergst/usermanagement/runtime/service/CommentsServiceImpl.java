package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Comments;
import com.mastergst.usermanagement.runtime.repository.CommentsRepository;

@Service
@Transactional(readOnly = true)
public class CommentsServiceImpl implements CommentsService{
	
	
	@Autowired
	private CommentsRepository commentsRepository;

	@Override
	@Transactional
	public Comments createComments(Comments comments) {
		
		return commentsRepository.save(comments);
	}

	@Override
	public Comments getCommentsData(String id) {
		
		//return commentsRepository.findByUserid(id);
		return null;
	}
	
	@Override
	public List<Comments> getAllComments(String id) {
		
		return commentsRepository.findByUserid(id);
	}
	
	@Override
	public List<Comments> getAllComments(String id, String stage) {
		
		if(NullUtil.isNotEmpty(stage)) {
			return commentsRepository.findByUseridAndStageIsNotNull(id);
		}
		return commentsRepository.findByUseridAndStageIsNull(id);
		//return stage !=null ? commentsRepository.findByUseridAndStageIsNotNull(id): commentsRepository.findByUserid(id);	
	}
}
