package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.apache.commons.lang.math.NumberUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;

@Component
public class AcknowledgementDao extends InvoiceDao{

	protected AcknowledgementDao() {
		super("gstr1");
	}
	
	public List<String> getBillToNames(String clientId, int month,String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("mthCd").is(month+"").and("yrCd").is(yearCd);
		Query query = Query.query(criteria);
		List<String> billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return billToNames;
	}
	
	public List<String> getInvoicenos(String clientId, int month, String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("mthCd").is(month+"").and("yrCd").is(yearCd);
		Query query = Query.query(criteria);
		List<String> invoiceno = mongoTemplate.getCollection(COLLECTION_NAME).distinct("invoiceno", query.getQueryObject());
		return invoiceno;
	}
	
	public  Page<GSTR1> getMonthlyAndYearlyAcknowledgementInvoices(String id, String pendingOrUpload, List<String> companies,List<String> customers,int month, String yearCode, int start, int length, String searchVal, InvoiceFilter filter) {
		Criteria criteria = null;
		if(companies.size() > 0 && customers.size() > 0) {
			criteria = Criteria.where("clientid").in(companies).and("yrCd").is(yearCode).and("billedtoname").in(customers);
		}else {
			criteria = Criteria.where("clientid").in(companies).and("yrCd").is(yearCode);
		}
			
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if("pending".equalsIgnoreCase(pendingOrUpload)) {
			criteria.and("s3attachment").is(null);
		}else {
			criteria.and("s3attachment").ne(null);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(length != -1) {
			addAllInvoicesQueryFirlds(query);			
		}
		if(length == -1) {
			length =Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start/length), length);
		
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR1.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR1>(Collections.<GSTR1> emptyList());
		}
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME), pageable, total);
	}
	
	public long getNumberOfPendingAcknowledgementInvoices(String clientId){
		Criteria criteria = Criteria.where("clientid").is(clientId);
		criteria.and("s3attachment").is(null);
		Query query = Query.query(criteria);
		long total = mongoTemplate.count(query, GSTR1.class, COLLECTION_NAME);
		return total;
	}
	
	public long getNumberOfInvoices(String clientId){
		Criteria criteria = Criteria.where("clientid").is(clientId);
		Query query = Query.query(criteria);
		long total = mongoTemplate.count(query, GSTR1.class, COLLECTION_NAME);
		return total;
	}
	
	public Page<? extends InvoiceParent> getCustomAcknowledgementInvoices(String id, String pendingOrUpload, List<String> companies,List<String> customers,Date stDate, Date endDate, int start, int length, String searchVal, InvoiceFilter filter) {
		Criteria criteria = null;
		if(companies.size() > 0 && customers.size() > 0) {
			criteria = Criteria.where("clientid").in(companies).and("billedtoname").in(customers)
				.andOperator(
					Criteria.where("dateofinvoice").gte(stDate),
					Criteria.where("dateofinvoice").lte(endDate)
				);
		}else {
			criteria = Criteria.where("clientid").in(companies)
				.andOperator(
					Criteria.where("dateofinvoice").gte(stDate),
					Criteria.where("dateofinvoice").lte(endDate)
					);
		}
		
		/*Criteria criteria = Criteria.where("clientid").is(clientid)
		.andOperator(
				Criteria.where("dateofinvoice").gte(stDate),
				Criteria.where("dateofinvoice").lte(endDate)
			);*/
		if("pending".equalsIgnoreCase(pendingOrUpload)) {
			criteria.and("s3attachment").is(null);
		}else {
			criteria.and("s3attachment").ne(null);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(length != -1) {
			addAllInvoicesQueryFirlds(query);			
		}
		if(length == -1) {
			length =Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start/length), length);
		
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR1.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR1>(Collections.<GSTR1> emptyList());
		}
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME), pageable, total);

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
 		criterias.add(Criteria.where("fullname").regex(searchVal, "i"));
 		criterias.add(Criteria.where("invoiceno").regex(searchVal, "i"));
 		criterias.add(Criteria.where("billedtoname").regex(searchVal, "i"));
 		criterias.add(Criteria.where("invoiceCustomerId").regex(searchVal, "i"));
 		criterias.add(Criteria.where("totaltaxableamount").regex(searchVal, "i"));
 		criterias.add(Criteria.where("totalamount").regex(searchVal, "i"));
 		String invdate = searchVal;
 		if(invdate.indexOf("/") != -1) {
 			String invoicedate[] = invdate.split("/");
 			String strMonth= "";
 			if(invoicedate.length >= 2) {
 				int mnth = Integer.parseInt(invoicedate[1]);
 	 			strMonth = mnth < 10 && mnth > 0 ? "0" + mnth : mnth + "";
 			}

 			if(invoicedate.length == 2) {
 				invdate = invoicedate[0]+"/"+strMonth;
 			}else if(invoicedate.length == 3) {
 				invdate = invoicedate[0]+"/"+strMonth+"/"+invoicedate[2];
 			}
 			criterias.add(Criteria.where("dateofinvoice_str").regex(invdate, "i"));
 		}else {
 			criterias.add(Criteria.where("dateofinvoice_str").regex(searchVal, "i"));
 		}
		
		String serForNum = searchVal;
		boolean isNumber = NumberUtils.isNumber(serForNum);
		if(!isNumber && serForNum.indexOf(",") != -1){
			serForNum = serForNum.replaceAll(",", "");
			isNumber = NumberUtils.isNumber(serForNum);
		}
		if(isNumber){
			criterias.add(Criteria.where("totaltaxableamount_str").regex(serForNum, "i"));
			criterias.add(Criteria.where("totalamount_str").regex(serForNum, "i"));
		}
 		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}

	private void applyFilterToCriteria(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			if(filter.getInvoiceType() != null){
				criteria.and("invoiceno").in(Arrays.asList(filter.getInvoiceType()));
			}
			if(filter.getVendor() != null){
				criteria.and("billedtoname").in(Arrays.asList(filter.getVendor()));
			}
		}
	}
	
	private void addAllInvoicesQueryFirlds(Query query){
		
		query.fields().include("invoiceno");
		query.fields().include("dateofinvoice");
		query.fields().include("billedtoname");
		query.fields().include("invoiceCustomerId");
		query.fields().include("totaltaxableamount");
		query.fields().include("totalamount");
		query.fields().include("s3attachementDate");
		query.fields().include("totaltax");
	}

	public Page<? extends InvoiceParent> getUserMonthlyAndYearlyAcknowledgementInvoices(String id, List<String> clientids, String pendingOrUpload, int month, String yearCode, int start, int length, String searchVal, InvoiceFilter filter) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if("pending".equalsIgnoreCase(pendingOrUpload)) {
			criteria.and("s3attachment").is(null);
		}else {
			criteria.and("s3attachment").ne(null);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(length != -1) {
			addAllInvoicesQueryFirlds(query);
		}
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start/length), length);
		
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR1.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR1>(Collections.<GSTR1> emptyList());
		}
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME), pageable, total);
	}

	public Page<? extends InvoiceParent> getUsersCustomAcknowledgementInvoices(String id, List<String> clientids, String pendingOrUpload, Date stDate, Date endDate, int start, int length, String searchVal,	InvoiceFilter filter) {
		Criteria criteria = Criteria.where("clientid").in(clientids)
									.andOperator(
											Criteria.where("dateofinvoice").gte(stDate),
											Criteria.where("dateofinvoice").lte(endDate)
										);
		if("pending".equalsIgnoreCase(pendingOrUpload)) {
			criteria.and("s3attachment").is("pending");
		}else {
			criteria.and("s3attachment").ne("pending");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(length != -1) {
			addAllInvoicesQueryFirlds(query);
		}
		if(length == -1) {
			length =Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start/length), length);
		
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR1.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR1>(Collections.<GSTR1> emptyList());
		}
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME), pageable, total);
	}
	
	public void updateInvoiceDocumentWithAttachment(String returnType, String invNo, String attachment, String invoiceId, String clientId){
		String collectionName = "Purchages".equals(returnType) ? "purchaseregister" : "gstr1";
	
		Criteria criteria = Criteria.where("_id").is(new ObjectId(invoiceId));
		Update update = new Update();
		if(attachment == null){
			update.set("s3attachment", null);
			update.set("s3attachementDate", null);
		}else{
			update.set("s3attachment", attachment);
			update.set("s3attachementDate", new Date());
		}
		mongoTemplate.updateFirst(Query.query(criteria), update, collectionName);
	}
	
	public String getAttachmentUrl(String returnType, String invNo,String invoiceId, String clientId){
		String collectionName = "Purchages".equals(returnType) ? "purchaseregister" : "gstr1";
		Criteria criteria = Criteria.where("_id").is(new ObjectId(invoiceId));
		Query query = Query.query(criteria);
		query.fields().include("s3attachment");
		List<Map> dataMap = mongoTemplate.find(Query.query(criteria), Map.class, collectionName);
		Map data = dataMap.get(0);
		String s3attachment = (String)data.get("s3attachment");
		return s3attachment;
	}
}
