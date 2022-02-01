/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.MongoConfiguration;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.domain.UserKeys;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.usermanagement.runtime.domain.MeterCount;
import com.mastergst.usermanagement.runtime.domain.Metering;
import com.mastergst.usermanagement.runtime.repository.MeteringRepository;
import com.mongodb.*;

/**
 * Service Impl class for Metering to perform CRUD operation.
 * 
 * @author Ashok Samart
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
@PropertySource("classpath:config.properties")
@Import({ MongoConfiguration.class })
public class MeteringServiceImpl implements MeteringService {

	private static final Logger logger = LogManager.getLogger(MeteringServiceImpl.class.getName());
	private static final String CLASSNAME = "MeteringServiceImpl::";
	
	@Resource
    private Environment env;
	
	@Autowired
	MeteringRepository meteringRepository;
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	@Transactional(readOnly=true)
	public List<Metering> getAllMetering(String userid) {
		return meteringRepository.findByUserid(userid);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<Metering> getMeteringData(final String userid, final Date startDate, final Date endDate) {
		return meteringRepository.findByUseridAndCreatedDateBetween(userid, startDate, endDate);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<Metering> getMeteringData(final String userid, final String sandBox, final String production, final Date startDate, final Date endDate) {
		List<String> list = new ArrayList<String>();
		list.add(sandBox);
		list.add(production);
		return meteringRepository.findByUseridAndStageInAndCreatedDateBetween(userid, list, startDate, endDate);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<Metering> getEWayMeteringData(final String userid, final Date startDate, final Date endDate) {
		List<String> list = new ArrayList<String>();
		list.add("EWAYAPI");
		list.add("WayBillAuthentication");
		return meteringRepository.findByUseridAndTypeInAndCreatedDateBetween(userid, list, startDate, endDate);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<Metering> getGSTMeteringData(final String userid, final Date startDate, final Date endDate) {
		List<String> list = new ArrayList<String>();
		list.add("EWAYAPI");
		list.add("WayBillAuthentication");
		list.add("e-Invoice");
		return meteringRepository.findByUseridAndCreatedDateBetweenAndTypeNotIn(userid, startDate, endDate, list);
	}
	
	@Override
	@Transactional(readOnly=true)
	public Page<Metering> getGSTAllMeterings(String dashboardType,String userid,int page, int length,String sortFieldName, String order, String searchVal,final Date startDate, final Date endDate) {
		List<String> list = new ArrayList<String>();
		list.add("EWAYAPI");
		list.add("WayBillAuthentication");
		Query query = new Query();
		
		Criteria criteria = new Criteria();
		if(NullUtil.isNotEmpty(userid)){
			query.addCriteria(criteria.where("userid").is(userid));
		}
		query.addCriteria(Criteria.where("createdDate").gte(startDate).lte(endDate));
		if("gst".equals(dashboardType)){
			list.add("e-Invoice");
			list.add("GENERATE");
			list.add("CANCEL");
			list.add("GETIRN");
			list.add("GSTNDETAILS");
			list.add("GENERATE_EWAYBILL");
			list.add("CANCEL_EWAYBILL");
			list.add("SYNC_GSTIN_FROMCP");
			list.add("GETEWAYBILLIRN");
			list.add("GETIRNBYDOCDETAILS");
			list.add("HEALTH");
			list.add("generateQrCode");
			query.addCriteria(Criteria.where("stage").is(MasterGSTConstants.PRODUCTION));
			query.addCriteria(Criteria.where("type").nin(list));
		}else if("eway".equals(dashboardType)){
			query.addCriteria(Criteria.where("stage").is(MasterGSTConstants.EWAYBILLPRODUCTION));
			query.addCriteria(Criteria.where("type").in(list));
			
		}else if("einv".equals(dashboardType)) {
			list=new ArrayList<String>();
			list.add("e-Invoice");
			list.add("GENERATE");
			list.add("CANCEL");
			list.add("GETIRN");
			list.add("GSTNDETAILS");
			list.add("GENERATE_EWAYBILL");
			list.add("CANCEL_EWAYBILL");
			list.add("SYNC_GSTIN_FROMCP");
			list.add("GETEWAYBILLIRN");
			list.add("GETIRNBYDOCDETAILS");
			list.add("HEALTH");
			list.add("generateQrCode");
			query.addCriteria(Criteria.where("stage").is(MasterGSTConstants.EINVOICE_PRODUCTION));
			query.addCriteria(Criteria.where("type").in(list));
			//query.addCriteria(Criteria.where("stage").in(Arrays.asList("EInvoiceProduction")));
		}
		if(NullUtil.isNotEmpty(searchVal)){
			criteria = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("gstnusername").regex(searchVal,"i"), Criteria.where("servicename").regex(searchVal,"i"), Criteria.where("ipusr").regex(searchVal),Criteria.where("type").regex(searchVal,"i"),Criteria.where("status").regex(searchVal,"i"),Criteria.where("size").regex(searchVal)));
		}
		query.addCriteria(criteria);
		Sort sort = null;
		if(NullUtil.isNotEmpty(sortFieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, sortFieldName));
		}
		Pageable pageable = null;
		if(length > 0){
		pageable = new PageRequest(page, length, sort);
		query.with(pageable);
		}
		long total = mongoTemplate.count(query, Metering.class, "metering");
		if (total == 0) {
			return new PageImpl<Metering>(Collections.<Metering> emptyList());
		}
		if(length < 0){
			pageable = new PageRequest(page, (int)total, sort);
			query.with(pageable);
			}
		return new PageImpl<Metering>(mongoTemplate.find(query, Metering.class, "metering"), pageable, total);
	}
	
	
	@Override
	@Transactional(readOnly=true)
	public List<MeterCount> getMeteringData(final Date startDate, final Date endDate) {
		logger.debug(CLASSNAME+" getMeteringData :: Begin");
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("createdDate").gte(startDate).lte(endDate)),
				Aggregation.group("stage").count().as("total"),
				Aggregation.project("total").and("stage").previousOperation(),
				Aggregation.sort(Sort.Direction.DESC, "total")).withOptions(Aggregation.newAggregationOptions().
				        allowDiskUse(true).build());
		AggregationResults<MeterCount> groupResults = mongoTemplate.aggregate(agg, Metering.class, MeterCount.class);
		return groupResults.getMappedResults();
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<Metering> getMeteringDataByStage(final String stage, final Date startDate, final Date endDate) {
		return meteringRepository.findByStageAndCreatedDateBetween(stage, startDate, endDate);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<Metering> getClientMeteringData(final String gstUsername, final Date startDate, final Date endDate) {
		return meteringRepository.findByGstnusernameAndCreatedDateBetween(gstUsername, startDate, endDate);
	}

	@Override
	@Transactional
	public void deleteMetering(String id) {
		meteringRepository.delete(id);
	}
	
	@Override
	@Transactional
	public void updateMeteringStage(final Date startDate, final Date endDate) {
		logger.debug(CLASSNAME+" updateMeteringStage :: Begin");
		List<Metering> meters = meteringRepository.findByCreatedDateBetween(startDate, endDate);
		List<Metering> modifiedMeters = Lists.newArrayList();
		Map<String, User> userMap = Maps.newHashMap();
		for(Metering meter : meters) {
			if(NullUtil.isEmpty(meter.getStage()) 
					&& NullUtil.isNotEmpty(meter.getUserid()) 
					&& NullUtil.isNotEmpty(meter.getUserclientid()) 
					&& NullUtil.isNotEmpty(meter.getUserclientsecret())) {
				User user = null;
				if(userMap.containsKey(meter.getUserid())) {
					user = userMap.get(meter.getUserid());
				} else {
					user = userRepository.findOne(meter.getUserid());
					if(NullUtil.isNotEmpty(user)) {
						userMap.put(meter.getUserid(), user);
					}
				}
				if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getUserkeys())) {
					for (UserKeys userKeys : user.getUserkeys()) {
						if (NullUtil.isNotEmpty(userKeys.getStage()) 
								&& (userKeys.getClientid().trim().equals(meter.getUserclientid()))
								&& (userKeys.getClientsecret().trim().equals(meter.getUserclientsecret()))) {
							meter.setStage(userKeys.getStage());
							modifiedMeters.add(meter);
						}
					}
				}
			}
		}
		if(NullUtil.isNotEmpty(modifiedMeters)) {
			meteringRepository.save(modifiedMeters);
		}
	}
	@Override
	@Transactional(readOnly=true)
	public List<Metering> getMeteringStageCount(String userid, String stage) {
		
		return meteringRepository.findByUseridAndStage(userid, stage);
	}
	
	
	@Override
	@Transactional(readOnly=true)
	public List<Metering> getMeteringDataByuserIdAndStage(String userid, String stage, Date startDate, Date endDate) {
		
		return meteringRepository.findByUseridAndStageInAndCreatedDateBetween(userid,stage,startDate,endDate);
	}

	/*@Override
	@Transactional
	public List<MeterCount> getMeteringData1(final String userid,final Date startDate, final Date endDate) {
		logger.debug(CLASSNAME+" getMeteringData :: Begin");
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("userid").is(userid)),
				Aggregation.match(Criteria.where("createdDate").gte(startDate).lte(endDate)),
				Aggregation.group("stage").count().as("total"),
				Aggregation.project("total").and("stage").previousOperation()).withOptions(Aggregation.newAggregationOptions().
				        allowDiskUse(true).build());
		AggregationResults<MeterCount> groupResults = mongoTemplate.aggregate(agg, Metering.class, MeterCount.class);
		
		return groupResults.getMappedResults();
	}*/
	
	@Override
	@Transactional(readOnly=true)
	public List<MeterCount> getAspMeteringData(final String userid,final Date startDate, final Date endDate) {
		logger.debug(CLASSNAME+" getMeteringData :: Begin");
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("userid").is(userid)),
				Aggregation.match(Criteria.where("createdDate").gte(startDate).lte(endDate)),
				Aggregation.group("stage").count().as("total"),
				Aggregation.project("total").and("stage").previousOperation(),
				Aggregation.sort(Sort.Direction.DESC, "total")).withOptions(Aggregation.newAggregationOptions().
				        allowDiskUse(true).build());
		AggregationResults<MeterCount> groupResults = mongoTemplate.aggregate(agg, Metering.class, MeterCount.class);
		
		return groupResults.getMappedResults();
	}
	
	@Override
	@Transactional(readOnly=true)
	public  Map<String, String> getUserWiseMeteringData(final String userid,final Date startDate, final Date endDate) {
		logger.debug(CLASSNAME+" getMeteringData :: Begin");
		Map<String, String> reportMap = Maps.newHashMap();
				
		try {
            MongoClient client = new MongoClient(env.getRequiredProperty("mongo.host.name"), 
    				Integer.parseInt(env.getRequiredProperty("mongo.host.port")));
            try {
                DB database = client.getDB(env.getRequiredProperty("mongo.db.name"));
                DBCollection collection = database.getCollection("metering");
                List<DBObject> pipeline = Arrays.asList(
                        new BasicDBObject()
                           .append("$match", new BasicDBObject().append("$and", Arrays.asList(
                        		   new BasicDBObject().append("userid", userid),
                        		   //SUCESS_FAIL
                        		   new BasicDBObject().append("status",new BasicDBObject().append("$in", Arrays.asList(MasterGSTConstants.SUCESS_FAIL, MasterGSTConstants.SUCESS))),
                        		   new BasicDBObject().append("createdDate",
                        		   new BasicDBObject().append("$gte", startDate).append("$lte", endDate))))), 
                        new BasicDBObject()
                                .append("$group", new BasicDBObject()
                                .append("_id", new BasicDBObject().append("stage", new BasicDBObject().append("stage", "$stage"))
                                .append("year", new BasicDBObject().append("$year", "$createdDate"))
                                //.append("month", new BasicDBObject().append("$month", "$createdDate")))
                                .append("month", new BasicDBObject().append("$month", Arrays.asList(new BasicDBObject().append("$add", Arrays.asList("$createdDate",19800000))))))
                                .append("count", new BasicDBObject().append("$sum", 1)))
                );
                AggregationOptions options = AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).allowDiskUse(false).build();
                        
                        
                Cursor cursor = collection.aggregate(pipeline, options);
                int sandboxTotal = 0,productionTotal = 0,ewayBillSandboxTotal = 0,ewayBillProductionTotal = 0,einvSandboxTotal = 0,einvproductionTotal = 0;
                try{
                while (cursor.hasNext()) {
                    BasicDBObject document = (BasicDBObject) cursor.next();
                    String count = document.getString("count");
                    String stage = ((BasicDBObject)((BasicDBObject)document.get("_id")).get("stage")).getString("stage");
                    if("Sandbox".equals(stage)){
                    	sandboxTotal = sandboxTotal+Integer.parseInt(count);
                    }else if("Production".equals(stage)){
                    	productionTotal = productionTotal+Integer.parseInt(count);
                    }else if("EwayBillSandBox".equals(stage)){
                    	ewayBillSandboxTotal = ewayBillSandboxTotal+Integer.parseInt(count);
                    }else if("EwayBillProduction".equals(stage)){
                    	ewayBillProductionTotal = ewayBillProductionTotal+Integer.parseInt(count);
                    }else if("EInvoiceSandBox".equals(stage)) {
                    	einvSandboxTotal = einvSandboxTotal+Integer.parseInt(count);
                    }else if("EInvoiceProduction".equals(stage)) {
                    	einvproductionTotal = einvproductionTotal+Integer.parseInt(count);
                    }
                   String mnth1 = (String)((BasicDBObject)document.get("_id")).getString("month");
                   reportMap.put(stage+mnth1, count);
                }
                reportMap.put("SandboxTotal", Integer.toString(sandboxTotal));
                reportMap.put("ProductionTotal", Integer.toString(productionTotal));
                reportMap.put("EwayBillSandboxTotal", Integer.toString(ewayBillSandboxTotal));
                reportMap.put("EwayBillProductionTotal", Integer.toString(ewayBillProductionTotal));
                reportMap.put("EinvSandboxTotal", Integer.toString(einvSandboxTotal));
                reportMap.put("EinvProductionTotal", Integer.toString(einvproductionTotal));
                }finally{
                	cursor.close();
                }
            } finally {
                client.close();
            }
            
        } catch (Exception e) {
            // handle exception
        }
		return reportMap;
	}

	@Override
	@Transactional(readOnly=true)
	public Map<String, String> getYearWiseMeteringData(Date startDate, Date endDate) {
		logger.debug(CLASSNAME+" getMeteringData :: Begin");
		Map<String, String> reportMap = Maps.newHashMap();
		try {
            MongoClient client = new MongoClient(env.getRequiredProperty("mongo.host.name"), 
    				Integer.parseInt(env.getRequiredProperty("mongo.host.port")));
            try {
                DB database = client.getDB(env.getRequiredProperty("mongo.db.name"));
                DBCollection collection = database.getCollection("metering");
                List<DBObject> pipeline = Arrays.asList(
                        new BasicDBObject()
                        .append("$match", new BasicDBObject().append("createdDate", new BasicDBObject().append("$gte", startDate)
                                .append("$lte", endDate)
                        )), 
                        new BasicDBObject()
                                .append("$group", new BasicDBObject()
                                .append("_id", new BasicDBObject().append("stage", new BasicDBObject().append("stage", "$stage"))
                                .append("year", new BasicDBObject().append("$year", "$createdDate"))
                                .append("month", new BasicDBObject().append("$month", "$createdDate")))
                                .append("count", new BasicDBObject().append("$sum", 1)))
                );
                AggregationOptions options = AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).allowDiskUse(false).build();
                        
                        
                Cursor cursor = collection.aggregate(pipeline, options);
                try{
                while (cursor.hasNext()) {
                    BasicDBObject document = (BasicDBObject) cursor.next();
                    String count = document.getString("count");
                    String stage = ((BasicDBObject)((BasicDBObject)document.get("_id")).get("stage")).getString("stage");
                   String mnth1 = (String)((BasicDBObject)document.get("_id")).getString("month");
                   reportMap.put(stage+mnth1, count);
                }
                }finally{
                	cursor.close();
                }
            } finally {
                client.close();
            }
            
        } catch (Exception e) {
            // handle exception
        }
		return reportMap;
	}
	@Override
	public List<Metering> getEInvMeteringData(String userid, Date startDate, Date endDate) {
		List<String> list = new ArrayList<String>();
		list.add("e-Invoice");
		list.add("GENERATE");
		list.add("CANCEL");
		list.add("GETIRN");
		list.add("GSTNDETAILS");
		list.add("GENERATE_EWAYBILL");
		list.add("CANCEL_EWAYBILL");
		list.add("SYNC_GSTIN_FROMCP");
		list.add("GETEWAYBILLIRN");
		list.add("GETIRNBYDOCDETAILS");
		list.add("HEALTH");
		list.add("generateQrCode");
		return meteringRepository.findByUseridAndTypeInAndCreatedDateBetween(userid,list,startDate,endDate);
		//return meteringRepository.findByUseridAndTypeAndStageAndCreatedDateBetween(userid, type, stage,startDate,endDate);
	}
	
	@Override
	@Transactional(readOnly=true)
	public Page<Metering> getGSTAllMeteringsFoExcel(String dashboardType,String userid,final Date startDate, final Date endDate) {
		List<String> list = new ArrayList<String>();
		list.add("EWAYAPI");
		list.add("WayBillAuthentication");
		Query query = new Query();
		
		Criteria criteria = new Criteria();
		if(NullUtil.isNotEmpty(userid)){
			query.addCriteria(criteria.where("userid").is(userid));
		}
		query.addCriteria(Criteria.where("createdDate").gte(startDate).lte(endDate));
		if("gst".equals(dashboardType)){
			list.add("e-Invoice");
			list.add("GENERATE");
			list.add("CANCEL");
			list.add("GETIRN");
			list.add("GSTNDETAILS");
			list.add("GENERATE_EWAYBILL");
			list.add("CANCEL_EWAYBILL");
			list.add("SYNC_GSTIN_FROMCP");
			list.add("GETEWAYBILLIRN");
			list.add("GETIRNBYDOCDETAILS");
			list.add("HEALTH");
			list.add("generateQrCode");
			query.addCriteria(Criteria.where("stage").is(MasterGSTConstants.PRODUCTION));
			query.addCriteria(Criteria.where("type").nin(list));
		}else if("eway".equals(dashboardType)){
			query.addCriteria(Criteria.where("stage").is(MasterGSTConstants.EWAYBILLPRODUCTION));
			query.addCriteria(Criteria.where("type").in(list));
		}else if("einv".equals(dashboardType)) {
			list=new ArrayList<String>();
			list.add("e-Invoice");
			list.add("GENERATE");
			list.add("CANCEL");
			list.add("GETIRN");
			list.add("GSTNDETAILS");
			list.add("GENERATE_EWAYBILL");
			list.add("CANCEL_EWAYBILL");
			list.add("SYNC_GSTIN_FROMCP");
			list.add("GETEWAYBILLIRN");
			list.add("GETIRNBYDOCDETAILS");
			list.add("HEALTH");
			list.add("generateQrCode");
			query.addCriteria(Criteria.where("stage").is(MasterGSTConstants.EINVOICE_PRODUCTION));
			query.addCriteria(Criteria.where("type").in(list));
			//query.addCriteria(Criteria.where("stage").in(Arrays.asList("EInvoiceProduction")));
		}
		
		query.addCriteria(criteria);
		
		Pageable pageable = null;
		
		long total = mongoTemplate.count(query, Metering.class, "metering");
		if (total == 0) {
			return new PageImpl<Metering>(Collections.<Metering> emptyList());
		}
		return new PageImpl<Metering>(mongoTemplate.find(query, Metering.class, "metering"), pageable, total);
	}
}
