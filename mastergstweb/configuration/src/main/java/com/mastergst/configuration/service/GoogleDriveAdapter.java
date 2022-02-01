/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Maps;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.common.collect.Lists;

@Service
public class GoogleDriveAdapter {
	
	private static final Logger logger = LogManager.getLogger(GoogleDriveAdapter.class.getName());
	
	private static final String APPLICATION_NAME = "MasterGST";
	private static final String CREDENTIALS_FOLDER = "credentials/"; // Directory to store user credentials.
	private static final String CLIENT_SECRET = "client_secret.json";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
	
	private Map<String, Drive> driveMap = Maps.newHashMap();
	
	private Drive getService(final String userid) {
		if(driveMap.containsKey(userid)) {
			return driveMap.get(userid);
		} else {
			try {
				NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
				Drive driveService = new Drive.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport, userid))
		                .setApplicationName(APPLICATION_NAME)
		                .build();
				driveMap.put(userid, driveService);
				return driveService;
			} catch (Exception e) {
				logger.error("GoogleDriveAdapter : getService : ERROR", e);
			}
		}
		return null;
	}
	
	private Credential getCredentials(final NetHttpTransport httpTransport, final String userid) throws IOException {
		// Load client secrets.
		Resource resource = new ClassPathResource(CLIENT_SECRET);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(resource.getInputStream()));
		
		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER+userid)))
				.setAccessType("offline")
				.build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
	
	public String createFolder(final String gstn, final String userid) throws IOException {
		logger.debug("GoogleDriveAdapter : createFolder : Begin");
		Drive service = getService(userid);
    	if(service != null) {
			File fileMetadata = new File();
			fileMetadata.setName(gstn);
			fileMetadata.setMimeType("application/vnd.google-apps.folder");
			File driveFile = service.files().create(fileMetadata)
					.setFields("id")
					.execute();
			logger.debug("GoogleDriveAdapter : createFolder : End");
			return driveFile.getId();
    	}
    	return null;
	}
	
	public File uploadFile(java.io.File file, final String userid) throws IOException {
		logger.debug("GoogleDriveAdapter : uploadFile : Begin");
		Drive service = getService(userid);
    	if(service != null) {
			File fileMetadata = new File();
			fileMetadata.setName(file.getName());
			FileContent mediaContent = new FileContent(URLConnection.guessContentTypeFromName(file.getName()), file);
			File driveFile = service.files().create(fileMetadata, mediaContent).setFields("id").execute();
			logger.debug("GoogleDriveAdapter : uploadFile : End");
			return driveFile;
    	}
    	return null;
	}
	
	public File uploadFile(MultipartFile file, final String userid) throws IOException {
		return uploadFile(file, null, userid);
	}
	
	public File uploadFile(MultipartFile file, String folderId, final String userid) throws IOException {
		logger.debug("GoogleDriveAdapter : uploadFile : Begin {}", file.getOriginalFilename());
		Drive service = getService(userid);
    	if(service != null) {
    		File fileMetadata = new File();
    		fileMetadata.setName(file.getOriginalFilename());
    		if(folderId != null) {
    			fileMetadata.setParents(Collections.singletonList(folderId));
    		}
    		java.io.File inputFile=new java.io.File(file.getOriginalFilename());
    		if (!inputFile.exists()) {
    			inputFile.createNewFile();
    			file.transferTo(inputFile);
    		}
    		FileContent mediaContent = new FileContent(file.getContentType(), inputFile);
			File driveFile = service.files().create(fileMetadata, mediaContent).setFields("id").execute();
			try {
				inputFile.delete();
			} catch(Exception e) {}
			return driveFile;
    	}
		logger.debug("GoogleDriveAdapter : uploadFile : End");
		return null;
	}
	
	public ByteArrayOutputStream downloadFile(String fileId, final String userid) throws IOException {
		logger.debug("GoogleDriveAdapter : downloadFile : Begin");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Drive service = getService(userid);
    	if(service != null) {
    		service.files().get(fileId).executeMediaAndDownloadTo(outputStream);
    	}
		logger.debug("GoogleDriveAdapter : downloadFile : End");
		return outputStream;
	}
	
	public java.io.File downloadFile(String fileId, String fileName, final String userid) throws IOException {
		logger.debug("GoogleDriveAdapter : downloadFile : Begin");
		java.io.File file=new java.io.File(fileName);
    	FileOutputStream outputStream = new FileOutputStream(file);
    	Drive service = getService(userid);
    	if(service != null) {
    		service.files().get(fileId).executeMediaAndDownloadTo(outputStream);
    	}
    	logger.debug("GoogleDriveAdapter : downloadFile : End");
    	return file;
	}
	
	public Map<String, String> getFiles(final String userid) throws IOException {
		logger.debug("GoogleDriveAdapter : getFiles : Begin");
		Map<String, String> filesMap = Maps.newHashMap();
		Drive service = getService(userid);
    	if(service != null) {
    		FileList result = service.files().list().setPageSize(10)
    				.setFields("nextPageToken, files(id, name)").execute();
    		List<File> files = result.getFiles();
    		if (files == null || files.isEmpty()) {
    		} else {
    			for (File file : files) {
    				filesMap.put(file.getId(), file.getName());
    			}
    		}
    	}
    	logger.debug("GoogleDriveAdapter : getFiles : End");
    	return filesMap;
	}
	
	public Map<String, String> getFiles(String folderId, final String userid) throws IOException {
		logger.debug("GoogleDriveAdapter : getFiles : Begin");
		Map<String, String> filesMap = Maps.newHashMap();
		Drive service = getService(userid);
    	if(service != null) {
    		FileList result = service.files().list()
    				.setQ("trashed = false and \'" + folderId + "\' in parents")
    				.setPageSize(100)
    				.setFields("nextPageToken, files(id, name)").execute();
    		List<File> files = result.getFiles();
    		if (files == null || files.isEmpty()) {
    		} else {
    			for (File file : files) {
    				filesMap.put(file.getId(), file.getName());
    			}
    		}
    	}
    	logger.debug("GoogleDriveAdapter : getFiles : End");
    	return filesMap;
	}
	
	public List<File> getFileList(String folderId, final String userid) throws IOException {
		logger.debug("GoogleDriveAdapter : getFileList : Begin");
		Drive service = getService(userid);
    	if(service != null) {
    		FileList result = service.files().list()
    				.setQ("trashed = false and \'" + folderId + "\' in parents")
    				.setPageSize(100)
    				.setFields("nextPageToken, files(id, name, createdTime)").execute();
    		return result.getFiles();
    	}
    	logger.debug("GoogleDriveAdapter : getFileList : End");
    	return Lists.newArrayList();
	}
	
	public void delete(String fileId, final String userid) throws IOException {
		logger.debug("GoogleDriveAdapter : delete : Begin");
		Drive service = getService(userid);
    	if(service != null) {
    		service.files().delete(fileId).execute();
    	}
    	logger.debug("GoogleDriveAdapter : delete : End");
	}
}