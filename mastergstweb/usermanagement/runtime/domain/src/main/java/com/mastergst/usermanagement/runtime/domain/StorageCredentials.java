package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mastergst.core.domain.Base;

@Document(collection = "storage_credentials")
public class StorageCredentials extends Base {
	
	@JsonIgnore
	protected MgstStorage storage;
	private String userid;
	protected String clientId;
	private String clientName;
	private String accessKey;
	private String accessSecret;
	private String bucketName;
	private String regionName;
	private String groupName;
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public String getAccessSecret() {
		return accessSecret;
	}
	public void setAccessSecret(String accessSecret) {
		this.accessSecret = accessSecret;
	}
	public String getStorageType(){
		if(storage == null){return null;}
		return storage.getName();	
	}
	public void setStorageType(String storageType){
		this.storage = MgstStorage.get(storageType);	
	}
	public String getClientId() {
		return clientId;
	}
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
