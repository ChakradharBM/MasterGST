package com.mastergst.storage.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mastergst.core.util.NullUtil;
import com.mastergst.storage.service.StorageCredentialsService;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.MgstResponse;
import com.mastergst.usermanagement.runtime.domain.StorageCredentials;
import com.mastergst.usermanagement.runtime.repository.ClientRepository;
import com.mastergst.usermanagement.runtime.repository.StorageCredentailsRepository;


@Controller
public class StorageAccessController {

	private static final Logger logger = LogManager.getLogger(StorageAccessController.class.getName());
	@Autowired
	private StorageCredentialsService storageCredentialsService ;
	@Autowired
	private StorageCredentailsRepository storageCredentailsRepository;
	@Autowired
	private ClientRepository clientRepository;
	
	@RequestMapping(value="saveAwsCredentials", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody MgstResponse saveAwsCredentials(@RequestBody StorageCredentials awsCredentials,@RequestParam("userid") String userid){
		logger.debug("Start");
		StorageCredentials credentials = null;
		if(NullUtil.isEmpty(userid) || userid.equals(null)  || userid.equals("undefined")) {
			storageCredentialsService.saveStorageAccess(awsCredentials);
		}else {
			credentials = storageCredentialsService.findById(userid);
			credentials.setAccessKey(awsCredentials.getAccessKey());
			credentials.setAccessSecret(awsCredentials.getAccessSecret());
			credentials.setBucketName(awsCredentials.getBucketName());
			credentials.setRegionName(awsCredentials.getRegionName());
			if( NullUtil.isNotEmpty(awsCredentials.getGroupName())) {
				credentials.setGroupName(awsCredentials.getGroupName());
			}
			credentials.setStorageType(awsCredentials.getStorageType());
			credentials.setClientId(credentials.getClientId());
			Client client = clientRepository.findOne(credentials.getClientId());
			if(NullUtil.isNotEmpty(client) && NullUtil.isNotEmpty(client.getBusinessname())) {
				credentials.setClientName(client.getBusinessname());
			}
			credentials.setClientName(credentials.getClientName());
			credentials.setUserid(awsCredentials.getUserid());
			storageCredentailsRepository.save(credentials);
		}
		return new MgstResponse(MgstResponse.SUCCESS);
	}
}
