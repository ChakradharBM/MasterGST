package com.mastergst.storage.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/*import org.jclouds.ContextBuilder;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.MutableBlobMetadata;*/
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.mastergst.usermanagement.runtime.dao.AcknowledgementDao;
import com.mastergst.usermanagement.runtime.dao.StorageCredentialsDao;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.StorageCredentials;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;

@Service
public class StorageReadService {
	
private static final Logger logger = LogManager.getLogger(StorageReadService.class.getName());
	
	@Autowired
	private StorageCredentialsDao storageCredentialsDao;
	
	@Autowired
	private AcknowledgementDao acknowledgementDao;
	@Autowired
	GSTR1Repository gstr1Repository;
	@Autowired
	PurchaseRegisterRepository purchaseRepository;
	
	public Object[] readObject(String returnType, String invoiceNo, String invoiceId, String clientId){
		Object[] res = null;
		InvoiceParent invoice=null;
		try {
			if(returnType == "Purchages") {
				invoice= purchaseRepository.findOne(invoiceId);
				clientId =invoice.getClientid();
			}else {
				invoice= gstr1Repository.findOne(invoiceId);
				clientId =invoice.getClientid();
			}
			StorageCredentials storageCredentials = storageCredentialsDao.getStorageCredentials(clientId);
			if(storageCredentials != null){
				String attachmentUrl = acknowledgementDao.getAttachmentUrl(returnType, invoice.getInvoiceno(),invoiceId, clientId);
				if(attachmentUrl != null){
					String bucketName = storageCredentials.getBucketName();
					Regions region = Regions.fromName(storageCredentials.getRegionName());
					BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(storageCredentials.getAccessKey(), storageCredentials.getAccessSecret());
					final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(
							new AWSStaticCredentialsProvider(basicAWSCredentials)).withRegion(region).build();
					S3Object s3Obj = s3Client.getObject(bucketName, attachmentUrl);
					//System.out.println(s3Obj);
					//System.out.println(s3Obj.getObjectMetadata());
					res = new Object[3];
					res[0] = s3Obj.getObjectContent();
					res[1] = s3Obj.getObjectMetadata().getContentType();
					res[2] = s3Obj.getKey();
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured while read object from s3", e);
			e.printStackTrace();
		} 
		return res;
	}

}
