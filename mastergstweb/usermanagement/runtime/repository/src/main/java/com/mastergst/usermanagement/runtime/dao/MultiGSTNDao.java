package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.MultiGSTNData;
import com.mastergst.usermanagement.runtime.repository.MultiGSTnRepository;
import com.google.common.collect.Lists;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.AllGSTINTempData;

@Component
public class MultiGSTNDao {
	
private static final String TEMP_COLLECTION_NAME = "allGSTINTempData";
private static final String GSTIN_COLLECTION_NAME = "gSTINPublicData";
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private MultiGSTnRepository multiGSTnRepository;
	
	public void multigstdata(String id) {
		Criteria criteria = Criteria.where("userid").is(id);
		Query query = Query.query(criteria);
		addAllGSTINQueryFirlds(query);
		
		List<AllGSTINTempData> tempgstndetails = mongoTemplate.find(query, AllGSTINTempData.class, "allGSTINTempData");
		List<GSTINPublicData> gstndetails = mongoTemplate.find(query, GSTINPublicData.class, "gSTINPublicData");
		List<MultiGSTNData> mulgstdet = Lists.newArrayList();
		for(AllGSTINTempData gstntempdata : tempgstndetails) {
			if(NullUtil.isNotEmpty(gstntempdata.getGstno())) {
				List<MultiGSTNData> gstndataa =  multiGSTnRepository.findByUseridAndGstin(gstntempdata.getUserid(),gstntempdata.getGstno());
				if(NullUtil.isNotEmpty(gstndataa)) {
					multiGSTnRepository.delete(gstndataa);
				}
				
				MultiGSTNData gstndata = new MultiGSTNData();
				gstndata.setGstin(gstntempdata.getGstno());
				gstndata.setUserid(gstntempdata.getUserid());
				gstndata.setGstnid(gstntempdata.getId().toString());
				if(NullUtil.isNotEmpty(gstntempdata.getStatus())) {
					gstndata.setStatus(gstntempdata.getStatus());
				}
				mulgstdet.add(gstndata);
			}
		}
		multiGSTnRepository.save(mulgstdet);
		mulgstdet = Lists.newArrayList();
		for(GSTINPublicData gstndetail : gstndetails) {
			if(NullUtil.isNotEmpty(gstndetail.getGstin())) {
				List<MultiGSTNData> mgstdataa =  multiGSTnRepository.findByUseridAndGstin(gstndetail.getUserid(),gstndetail.getGstin());
				if(NullUtil.isNotEmpty(mgstdataa)) {
					multiGSTnRepository.delete(mgstdataa);
				}
				
				MultiGSTNData mgstdata = new MultiGSTNData();
				mgstdata.setUserid(gstndetail.getUserid());
				mgstdata.setGstin(gstndetail.getGstin());
				mgstdata.setDty(gstndetail.getDty());
				mgstdata.setTradeNam(gstndetail.getTradeNam());
				mgstdata.setRgdt(gstndetail.getRgdt());
				mgstdata.setLstupdt(gstndetail.getLstupdt());
				mgstdata.setStatus(gstndetail.getStatus());
				mgstdata.setGstnid(gstndetail.getId().toString());
				mulgstdet.add(mgstdata);
			}
		}
		
		
		multiGSTnRepository.save(mulgstdet);
	}
	
	public Page<? extends MultiGSTNData> findByUserid(String id, int start, int length, String searchVal) {
		Criteria criteria = Criteria.where("userid").is(id);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllGSTINQueryFirlds(query);
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long gstntotal = mongoTemplate.count(query, GSTINPublicData.class, "gSTINPublicData");
		long temptotal = mongoTemplate.count(query, AllGSTINTempData.class, "allGSTINTempData");
		/*
		 * List<AllGSTINTempData> tempgstndetails = mongoTemplate.find(query,
		 * AllGSTINTempData.class, "allGSTINTempData"); List<GSTINPublicData>
		 * gstndetails = mongoTemplate.find(query, GSTINPublicData.class,
		 * "gSTINPublicData"); List<MultiGSTNData> mulgstdet = Lists.newArrayList();
		 * for(AllGSTINTempData gstntempdata : tempgstndetails) {
		 * if(NullUtil.isNotEmpty(gstntempdata.getGstno())) {
		 * System.out.println("tempdata-----"+gstntempdata.getGstno());
		 * List<MultiGSTNData> gstndataa =
		 * multiGSTnRepository.findByUseridAndGstin(gstntempdata.getUserid(),
		 * gstntempdata.getGstno()); if(NullUtil.isNotEmpty(gstndataa)) {
		 * multiGSTnRepository.delete(gstndataa); }
		 * 
		 * MultiGSTNData gstndata = new MultiGSTNData();
		 * gstndata.setGstin(gstntempdata.getGstno());
		 * gstndata.setUserid(gstntempdata.getUserid());
		 * gstndata.setGstnid(gstntempdata.getId().toString());
		 * if(NullUtil.isNotEmpty(gstntempdata.getStatus())) {
		 * gstndata.setStatus(gstntempdata.getStatus()); } mulgstdet.add(gstndata); } }
		 * multiGSTnRepository.save(mulgstdet); mulgstdet = Lists.newArrayList();
		 * for(GSTINPublicData gstndetail : gstndetails) {
		 * if(NullUtil.isNotEmpty(gstndetail.getGstin())) {
		 * System.out.println("publicdata-----"+gstndetail.getGstin());
		 * List<MultiGSTNData> mgstdataa =
		 * multiGSTnRepository.findByUseridAndGstin(gstndetail.getUserid(),gstndetail.
		 * getGstin()); if(NullUtil.isNotEmpty(mgstdataa)) {
		 * multiGSTnRepository.delete(mgstdataa); }
		 * 
		 * MultiGSTNData mgstdata = new MultiGSTNData();
		 * mgstdata.setUserid(gstndetail.getUserid());
		 * mgstdata.setGstin(gstndetail.getGstin());
		 * mgstdata.setDty(gstndetail.getDty());
		 * mgstdata.setTradeNam(gstndetail.getTradeNam());
		 * mgstdata.setRgdt(gstndetail.getRgdt());
		 * mgstdata.setLstupdt(gstndetail.getLstupdt());
		 * mgstdata.setStatus(gstndetail.getStatus());
		 * mgstdata.setGstnid(gstndetail.getId().toString()); mulgstdet.add(mgstdata); }
		 * }
		 * 
		 * 
		 * multiGSTnRepository.save(mulgstdet);
		 */
		
		long total = mongoTemplate.count(query, MultiGSTNData.class, "multigstdata");
		//long total = gstntotal+temptotal;
		query.with(new Sort(new Order(Direction.DESC, "status")));
		List<MultiGSTNData> mulgstdets = mongoTemplate.find(query, MultiGSTNData.class, "multigstdata");
		
		if (total == 0) {
			return new PageImpl<MultiGSTNData>(Collections.<MultiGSTNData> emptyList());
		}
		return new PageImpl<MultiGSTNData>(mulgstdets, pageable, total);
	}
	
	
	private Criteria getSearchValueCriteria(final String searchval){
		String searchVal = searchval;
		if(searchVal.contains("(")) {
			searchVal = searchVal.replaceAll("\\(", "\\\\(");
		}
		if(searchVal.contains(")")) {
			searchVal = searchVal.replaceAll("\\)", "\\\\)");
		}
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(Criteria.where("gstin").regex(searchVal, "i"));
		criterias.add(Criteria.where("gstno").regex(searchVal, "i"));
 		criterias.add(Criteria.where("tradeNam").regex(searchVal, "i"));
 		criterias.add(Criteria.where("dty").regex(searchVal, "i"));
 		criterias.add(Criteria.where("rgdt").regex(searchVal, "i"));
 		criterias.add(Criteria.where("lstupdt").regex(searchVal, "i"));
 		criterias.add(Criteria.where("status").regex(searchVal, "i"));
		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}
	
	private void addAllGSTINQueryFirlds(Query query){
		query.fields().include("gstin");
		query.fields().include("gstno");
		query.fields().include("tradeNam");
		query.fields().include("dty");
		query.fields().include("rgdt");
		query.fields().include("lstupdt");
		query.fields().include("status");
		query.fields().include("userid");
		query.fields().include("gstnid");
	}

}
