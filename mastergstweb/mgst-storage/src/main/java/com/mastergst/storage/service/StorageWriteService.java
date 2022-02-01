package com.mastergst.storage.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/*import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.BlobBuilder;
import org.jclouds.blobstore.options.PutOptions;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.partitions.model.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.mastergst.usermanagement.runtime.dao.AcknowledgementDao;
import com.mastergst.usermanagement.runtime.dao.StorageCredentialsDao;
import com.mastergst.usermanagement.runtime.domain.StorageCredentials;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;

@Service
public class StorageWriteService {
	
	private static final Logger logger = LogManager.getLogger(StorageWriteService.class.getName());
	public static final String RESULTS = "RESULTS";
	
	@Autowired
	private StorageCredentialsDao storageCredentialsDao;
	
	@Autowired
	private AcknowledgementDao acknowledgementDao;
	@Autowired
	GSTR1Repository gstr1Repository;
	@Autowired
	PurchaseRegisterRepository purchaseRepository;
	
	/*public Map writeObject(int x,String userId, String returnType, String month, String year, String invoiceNo, String invoiceId,String clientId,  MultipartFile multipartFile){
		Map<String, String> resultMap = new HashMap<>();
		try {
			if(returnType == "Purchages") {
				clientId = purchaseRepository.findOne(invoiceId).getClientid();
			}else {
				clientId = gstr1Repository.findOne(invoiceId).getClientid();
			}
		StorageCredentials storageCredentials = storageCredentialsDao.getStorageCredentials(clientId);
			if(storageCredentials != null){
				BlobStoreContext context = ContextBuilder.newBuilder("aws-s3")
				        .credentials(storageCredentials.getAccessKey(), storageCredentials.getAccessSecret())
				        .buildView(BlobStoreContext.class);
				String bucketName = storageCredentials.getBucketName();
				
				BlobStore blobStore = context.getBlobStore();
				if(!blobStore.containerExists(bucketName)){
					blobStore.createContainerInLocation(null, bucketName);
				}
				String fileName = returnType+"/"+year+"/"+month+"/"+invoiceNo+"_"+multipartFile.getOriginalFilename();
				BlobBuilder blobBuilder = blobStore.blobBuilder(fileName).payload(multipartFile.getBytes());
				String result = blobStore.putBlob(bucketName, blobBuilder.build(), PutOptions.Builder.multipart());
				acknowledgementDao.updateInvoiceDocumentWithAttachment(returnType, invoiceNo, fileName,invoiceId, clientId);
				//System.out.println("Result:"+ result);
				resultMap.put(RESULTS, result);
			}
		} catch (NoSuchElementException e) {
			resultMap.put(RESULTS, "FAIL");
			e.printStackTrace();
		} catch (IOException e) {
			resultMap.put(RESULTS, "FAIL");
			e.printStackTrace();
		}
		return resultMap;
	}
	
	public Map<String, String> deleteObject(int x, String returnType, String invoiceNo, String invoiceId, String clientId){
		Map<String, String> resultMap = new HashMap<>();
		try {
			if(returnType == "Purchages") {
				clientId = purchaseRepository.findOne(invoiceId).getClientid();
			}else {
				clientId = gstr1Repository.findOne(invoiceId).getClientid();
			}
			StorageCredentials storageCredentials = storageCredentialsDao.getStorageCredentials(clientId);
			if(storageCredentials != null){
				String attachmentUrl = acknowledgementDao.getAttachmentUrl(returnType, invoiceNo,invoiceId, clientId);
				if(attachmentUrl != null){
					BlobStoreContext context = ContextBuilder.newBuilder("aws-s3")
					        .credentials(storageCredentials.getAccessKey(), storageCredentials.getAccessSecret())
					        .buildView(BlobStoreContext.class);
					String bucketName = storageCredentials.getBucketName();
					
					BlobStore blobStore = context.getBlobStore();
					blobStore.removeBlob(bucketName, attachmentUrl);
					acknowledgementDao.updateInvoiceDocumentWithAttachment(returnType, invoiceNo, null,invoiceId, clientId);
					resultMap.put(RESULTS, "SUCCESS");
				}else{
					resultMap.put(RESULTS, "NOT EXISTS");
				}
			}else{
				resultMap.put(RESULTS, "CREDENTIALS NOT FOUND");
			}
		} catch (Exception e) {
			resultMap.put(RESULTS, "FAIL");
			e.printStackTrace();
		} 
		return resultMap;
	}*/
	
	public Map writeObject(String userId, String returnType, String month, String year, String invoiceNo, String invoiceId,String clientId,  MultipartFile multipartFile){
		
		Map<String, String> resultMap = new HashMap<>();
		try {
			if(returnType == "Purchages") {
				clientId = purchaseRepository.findOne(invoiceId).getClientid();
			}else {
				clientId = gstr1Repository.findOne(invoiceId).getClientid();
			}
			StorageCredentials storageCredentials = storageCredentialsDao.getStorageCredentials(clientId);
			if(storageCredentials != null){
				String bucketName = storageCredentials.getBucketName();
				Regions region = Regions.fromName(storageCredentials.getRegionName());
				BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(storageCredentials.getAccessKey(), storageCredentials.getAccessSecret());
				AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(
						new AWSStaticCredentialsProvider(basicAWSCredentials)).withRegion(region).build();
				String fileName = returnType+"/"+year+"/"+month+"/"+invoiceNo+"_"+multipartFile.getOriginalFilename();
				File file = createMultipartToFile(multipartFile);
		        PutObjectResult result = s3Client.putObject(bucketName, fileName, file);
		        file.delete();
		        acknowledgementDao.updateInvoiceDocumentWithAttachment(returnType, invoiceNo, fileName,invoiceId, clientId);
		        resultMap.put(RESULTS, result.getETag());
			}
		} catch (Exception e) {
			resultMap.put(RESULTS, "FAIL");
			logger.error("Exception occured at writing object to s3",e);
			e.printStackTrace();
		}
		return resultMap;
	}
	
	private File createMultipartToFile(MultipartFile multipartFile) throws IOException{
		String fileName = multipartFile.getOriginalFilename();
		String[] fi = fileName.split("\\.");
		File file  = File.createTempFile(fi[0], fi.length == 2 ? fi[1] : null);
		FileOutputStream fos = new FileOutputStream(file);
	    fos.write(multipartFile.getBytes());
	    fos.close();
		file.deleteOnExit();
		return file;
	}
	
	public Map<String, String> deleteObject(String returnType, String invoiceNo, String invoiceId, String clientId){
		Map<String, String> resultMap = new HashMap<>();
		try {
			if(returnType == "Purchages") {
				clientId = purchaseRepository.findOne(invoiceId).getClientid();
			}else {
				clientId = gstr1Repository.findOne(invoiceId).getClientid();
			}
			StorageCredentials storageCredentials = storageCredentialsDao.getStorageCredentials(clientId);
			if(storageCredentials != null){
				String attachmentUrl = acknowledgementDao.getAttachmentUrl(returnType, invoiceNo,invoiceId, clientId);
				if(attachmentUrl != null){
					String bucketName = storageCredentials.getBucketName();
					Regions region = Regions.fromName(storageCredentials.getRegionName());
					BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(storageCredentials.getAccessKey(), storageCredentials.getAccessSecret());
					final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(
							new AWSStaticCredentialsProvider(basicAWSCredentials)).withRegion(region).build();
					s3Client.deleteObject(bucketName, attachmentUrl);
					acknowledgementDao.updateInvoiceDocumentWithAttachment(returnType, invoiceNo, null,invoiceId, clientId);
					resultMap.put(RESULTS, "SUCCESS");
				}else{
					resultMap.put(RESULTS, "NOT EXISTS");
				}
			}else{
				resultMap.put(RESULTS, "CREDENTIALS NOT FOUND");
			}
		} catch (Exception e) {
			resultMap.put(RESULTS, "FAIL");
			logger.error("Exception occured at writing object to s3",e);
			e.printStackTrace();
		} 
		return resultMap;
	}
	
	
	

}
