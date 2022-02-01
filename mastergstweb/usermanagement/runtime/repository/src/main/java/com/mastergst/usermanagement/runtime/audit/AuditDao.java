package com.mastergst.usermanagement.runtime.audit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.dao.InvoiceDao;
import com.mastergst.usermanagement.runtime.domain.GSTR1;

@Service
public class AuditDao extends InvoiceDao{
	public AuditDao(){
		super("auditlog");
	}
	@Autowired private MongoTemplate mongoTemplate;
	
	public Page<? extends Auditlog> findByClientidInAndMonthAndYear(List<String> clientids, int month,String yearCode, int start, int length,AuditlogFilter filter, String searchVal,String fieldName, String order) {
		Criteria criteria = Criteria.where("yrCd").is(yearCode);
		if(filter != null && filter.getClientname() == null) {
			criteria.and("clientid").in(clientids);
		}
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria,filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<Auditlog>(mongoTemplate.find(query, Auditlog.class, COLLECTION_NAME));
		}else {
			addAllInvoicesQueryFirlds(query);
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Sort sort = null;
			if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
				sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
			}
			Pageable pageable = new PageRequest((start/length), length,sort);
			query.with(pageable);
			long total = mongoTemplate.count(query, Auditlog.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<Auditlog>(Collections.<Auditlog> emptyList());
			}
			return new PageImpl<Auditlog>(mongoTemplate.find(query, Auditlog.class, COLLECTION_NAME), pageable, total);
		}
	}
	
	public Page<? extends Auditlog> findByClientidInAndFromtimeAndTotime(List<String> clientids, Date stDate, Date endDate, int start, int length,AuditlogFilter filter, String searchVal,String fieldName, String order) {
		Criteria criteria = Criteria.where("createdDate").gte(stDate).lte(endDate);	
		if(filter != null && filter.getClientname() == null) {
			criteria.and("clientid").in(clientids);
		}
		applyFilterToCriteria(criteria,filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<Auditlog>(mongoTemplate.find(query, Auditlog.class, COLLECTION_NAME));
		}else {
			addAllInvoicesQueryFirlds(query);
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Sort sort = null;
			if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
				sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
			}
			Pageable pageable = new PageRequest((start/length), length,sort);
			query.with(pageable);
			long total = mongoTemplate.count(query, Auditlog.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<Auditlog>(Collections.<Auditlog> emptyList());
			}
			return new PageImpl<Auditlog>(mongoTemplate.find(query, Auditlog.class, COLLECTION_NAME), pageable, total);
		}
	}
	
	public Page<? extends Auditlog> findByClientidInAndMonthAndYearExcel(List<String> clientids, int month,String yearCode, int start, int length,AuditlogFilter filter, String searchVal,Pageable pageable) {
		Criteria criteria = Criteria.where("yrCd").is(yearCode);
		if(filter != null && filter.getClientname() == null) {
			criteria.and("clientid").in(clientids);
		}
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria,filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		query.with(pageable);
		long total = mongoTemplate.count(query, Auditlog.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<Auditlog>(Collections.<Auditlog> emptyList());
		}
		return new PageImpl<Auditlog>(mongoTemplate.find(query, Auditlog.class, COLLECTION_NAME), pageable, total);
	}
	
	public Page<? extends Auditlog> findByClientidInAndFromtimeAndTotimeExcel(List<String> clientids, Date stDate, Date endDate, int start, int length,AuditlogFilter filter, String searchVal,Pageable pageable) {
		Criteria criteria = Criteria.where("createdDate").gte(stDate).lte(endDate);	
		if(filter != null && filter.getClientname() == null) {
			criteria.and("clientid").in(clientids);
		}
		applyFilterToCriteria(criteria,filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		query.with(pageable);
		long total = mongoTemplate.count(query, Auditlog.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<Auditlog>(Collections.<Auditlog> emptyList());
		}
		return new PageImpl<Auditlog>(mongoTemplate.find(query, Auditlog.class, COLLECTION_NAME), pageable, total);
	}
	private void applyFilterToCriteria(Criteria criteria, AuditlogFilter filter){
		if(filter != null){
			List<Criteria> criterias = new ArrayList<Criteria>();
			if(filter.getAction() !=  null) {
				criteria.and("action").in(Arrays.asList(filter.getAction()));
			}
			if(filter.getClientname() != null) {
				criteria.and("clientid").in(Arrays.asList(filter.getClientname()));
			}
			if(filter.getUsername() != null) {
				criteria.and("userid").in(Arrays.asList(filter.getUsername()));
			}
			
			if(NullUtil.isNotEmpty(criterias)) {
				criteria.andOperator(criterias.toArray(new Criteria[criterias.size()]));				
			}
		}
	}
	
	private void addAllInvoicesQueryFirlds(Query query){

		query.fields().include("clientid");
		query.fields().include("username");
		query.fields().include("useremail");
		query.fields().include("clientname");
		query.fields().include("gstn");
		query.fields().include("userid");
		query.fields().include("action");
		query.fields().include("description");
		query.fields().include("createdDate");
		query.fields().include("returntype");
		query.fields().include("auditingFields");
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
 		criterias.add(Criteria.where("clientname").regex(searchVal, "i"));
 		
 		criterias.add(Criteria.where("gstn").regex(searchVal, "i"));
 		criterias.add(Criteria.where("uername").regex(searchVal, "i"));
 		criterias.add(Criteria.where("useremail").regex(searchVal, "i"));
 		criterias.add(Criteria.where("returntype").regex(searchVal, "i"));
 		criterias.add(Criteria.where("action").regex(searchVal, "i"));
 		String invdate = searchVal;
 		if(invdate.indexOf("/") != -1) {
 			String invoicedate[] = invdate.split("/");
 			String strMonth= "";
 			if(invoicedate.length >= 2) {
 				boolean isNumber = NumberUtils.isNumber(invoicedate[1]);
 				if(isNumber) {
 					int mnth = Integer.parseInt(invoicedate[1]);
 					strMonth = mnth < 10 && mnth > 0 ? "0" + mnth : mnth + "";
 				}
 			}

 			if(invoicedate.length == 2) {
 				invdate = invoicedate[0]+"/"+strMonth;
 			}else if(invoicedate.length == 3) {
 				invdate = invoicedate[0]+"/"+strMonth+"/"+invoicedate[2];
 			}
 			criterias.add(Criteria.where("createddate_str").regex(invdate, "i"));
 		}else {
 			criterias.add(Criteria.where("createddate_str").regex(searchVal, "i"));
 		}
 		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}
}
