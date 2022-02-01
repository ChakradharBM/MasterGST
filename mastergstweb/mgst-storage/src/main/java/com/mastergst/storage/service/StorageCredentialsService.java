package com.mastergst.storage.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.dao.StorageCredentialsDao;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;
import com.mastergst.usermanagement.runtime.domain.StorageCredentials;
import com.mastergst.usermanagement.runtime.repository.ClientRepository;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.repository.StorageCredentailsRepository;


@Service
public class StorageCredentialsService {
	
	private static final Logger logger = LogManager.getLogger(StorageCredentialsService.class.getName());
	@Autowired
	private StorageCredentialsDao storageCredentialsDao;
	@Autowired
	private ClientUserMappingRepository clientUserMappingRepository;
	@Autowired
	private StorageCredentailsRepository storageCredentailsRepository;
	@Autowired
	private ClientRepository clientRepository;
	
	
	public Map<String, String> saveStorageAccess(StorageCredentials storageCredentials){
		Map<String, String> results = new HashMap<>();
		Client client = null;
		List<ClientUserMapping> clientUserMappings = clientUserMappingRepository.findByUserid(storageCredentials.getClientId());
		List<Client> clients = null;
		StorageCredentials scredentials = null;
		if(NullUtil.isNotEmpty(storageCredentials.getGroupName())) {
			clients = clientRepository.findByGroupName(storageCredentials.getGroupName());
			if(clients != null) {
				for(Client clnt : clients) {
					scredentials = storageCredentialsDao.getStorageCredentialsBasedOnClientId(clnt.getId().toString());
					if(NullUtil.isEmpty(scredentials)) {
						StorageCredentials stCredentials = new StorageCredentials();
						stCredentials.setAccessKey(storageCredentials.getAccessKey());
						stCredentials.setAccessSecret(storageCredentials.getAccessSecret());
						stCredentials.setAccessSecret(storageCredentials.getAccessSecret());
						stCredentials.setBucketName(storageCredentials.getBucketName());
						stCredentials.setRegionName(storageCredentials.getRegionName());
						stCredentials.setGroupName(storageCredentials.getGroupName());
						stCredentials.setStorageType(storageCredentials.getStorageType());
						stCredentials.setClientId(clnt.getId().toString());
						if(NullUtil.isNotEmpty(clnt) && NullUtil.isNotEmpty(clnt.getBusinessname())) {
							stCredentials.setClientName(clnt.getBusinessname());
						}
						stCredentials.setUserid(storageCredentials.getClientId());
						storageCredentailsRepository.save(stCredentials);
					}else {
						scredentials.setAccessKey(storageCredentials.getAccessKey());
						scredentials.setAccessSecret(storageCredentials.getAccessSecret());
						scredentials.setAccessSecret(storageCredentials.getAccessSecret());
						scredentials.setBucketName(storageCredentials.getBucketName());
						scredentials.setRegionName(storageCredentials.getRegionName());
						scredentials.setGroupName(storageCredentials.getGroupName());
						scredentials.setStorageType(storageCredentials.getStorageType());
						scredentials.setClientId(clnt.getId().toString());
						if(NullUtil.isNotEmpty(clnt) && NullUtil.isNotEmpty(clnt.getBusinessname())) {
							scredentials.setClientName(clnt.getBusinessname());
						}
						scredentials.setUserid(storageCredentials.getClientId());
						storageCredentailsRepository.save(scredentials);
					}
					
				}
			}
		}else {
			if(clientUserMappings != null){
				for(ClientUserMapping clientUserMapping : clientUserMappings){
					scredentials = storageCredentialsDao.getStorageCredentialsBasedOnClientId(clientUserMapping.getClientid());
					if(NullUtil.isEmpty(scredentials)) {
						StorageCredentials stCredentials = new StorageCredentials();
						stCredentials.setAccessKey(storageCredentials.getAccessKey());
						stCredentials.setAccessSecret(storageCredentials.getAccessSecret());
						stCredentials.setAccessSecret(storageCredentials.getAccessSecret());
						stCredentials.setBucketName(storageCredentials.getBucketName());
						stCredentials.setRegionName(storageCredentials.getRegionName());
						if( NullUtil.isNotEmpty(storageCredentials.getGroupName())) {
							stCredentials.setGroupName(storageCredentials.getGroupName());
						}
						stCredentials.setStorageType(storageCredentials.getStorageType());
						stCredentials.setClientId(clientUserMapping.getClientid());
						client = clientRepository.findOne(clientUserMapping.getClientid());
						if(NullUtil.isNotEmpty(client) && NullUtil.isNotEmpty(client.getBusinessname())) {
							stCredentials.setClientName(client.getBusinessname());
						}
						stCredentials.setUserid(storageCredentials.getClientId());
						storageCredentailsRepository.save(stCredentials);
					}else {
						scredentials.setAccessKey(storageCredentials.getAccessKey());
						scredentials.setAccessSecret(storageCredentials.getAccessSecret());
						scredentials.setAccessSecret(storageCredentials.getAccessSecret());
						scredentials.setBucketName(storageCredentials.getBucketName());
						scredentials.setRegionName(storageCredentials.getRegionName());
						if( NullUtil.isNotEmpty(storageCredentials.getGroupName())) {
							scredentials.setGroupName(storageCredentials.getGroupName());
						}
						scredentials.setStorageType(storageCredentials.getStorageType());
						scredentials.setClientId(clientUserMapping.getClientid());
						client = clientRepository.findOne(clientUserMapping.getClientid());
						if(NullUtil.isNotEmpty(client) && NullUtil.isNotEmpty(client.getBusinessname())) {
							scredentials.setClientName(client.getBusinessname());
						}
						scredentials.setUserid(storageCredentials.getClientId());
						storageCredentailsRepository.save(scredentials);
					}
				}
			}
		}
		return results;	
	}
	public StorageCredentials findById(String userid) {
		return storageCredentailsRepository.findOne(userid);
	}

}
