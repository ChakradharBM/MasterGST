package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.Comments;



public interface CommentsRepository extends MongoRepository<Comments,String>{
	
	
	//public Comments findByUserid(String id);
	public List<Comments> findByUserid(String id);

	public List<Comments> findByUseridAndStageIsNotNull(String id);

	public List<Comments> findByUseridAndStageIsNull(String id);

}
