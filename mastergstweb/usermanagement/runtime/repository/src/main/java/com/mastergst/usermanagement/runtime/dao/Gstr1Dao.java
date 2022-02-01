package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.NumberUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CustomFields;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.InvoiceTypeSummery;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;


@Service
public class Gstr1Dao extends InvoiceDao {
	
	
	public Gstr1Dao(){
		super("gstr1");
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmountsForMonth(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter,String gstrType){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = null;
		GroupOperation groupOperation = null;
		if(month > 0) {
			projectionOperation = getProjectionForTotal("mthCd");
			groupOperation = getGroupForTotal("mthCd");
		}else {
			projectionOperation = getProjectionForTotal("yrCd");
			groupOperation = getGroupForTotal("yrCd");
		}
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		TotalInvoiceAmount cancelInvAmount = new TotalInvoiceAmount();
		if(filter != null) {
				cancelInvAmount = getTotalInvoicesAmountsForMonthCancelled(clientId,month,yearCode,searchVal,filter,gstrType);
				if(gstrType.equalsIgnoreCase("gstrOrEinvoice")) {
					if(filter.getIrnStatus() != null && filter.getIrnStatus().length == 1) {
						List<String> statusList = Arrays.asList(filter.getIrnStatus());
						if(statusList.contains("Cancelled")) {
							return cancelInvAmount;
						}
					}
				}else {
					if(filter.getPaymentStatus() != null && filter.getPaymentStatus().length == 1) {
						List<String> statusList = Arrays.asList(filter.getPaymentStatus());
						if(statusList.contains("CANCELLED")) {
							return cancelInvAmount;
						}
					}
				}
			}
			
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
		if(totalnvAmount != null && cancelInvAmount != null) {
			totalInvAmount.setTotalTransactions(totalnvAmount.getTotalTransactions());
			totalInvAmount.setTotalTaxableAmount(totalnvAmount.getTotalTaxableAmount().subtract(cancelInvAmount.getTotalTaxableAmount()));
			totalInvAmount.setTotalExemptedAmount(totalnvAmount.getTotalExemptedAmount().subtract(cancelInvAmount.getTotalExemptedAmount()));
			totalInvAmount.setTotalTaxAmount(totalnvAmount.getTotalTaxAmount().subtract(cancelInvAmount.getTotalTaxAmount()));
			totalInvAmount.setTotalAmount(totalnvAmount.getTotalAmount().subtract(cancelInvAmount.getTotalAmount()));
			totalInvAmount.setTotalIGSTAmount(totalnvAmount.getTotalIGSTAmount().subtract(cancelInvAmount.getTotalIGSTAmount()));
			totalInvAmount.setTotalCGSTAmount(totalnvAmount.getTotalCGSTAmount().subtract(cancelInvAmount.getTotalCGSTAmount()));
			totalInvAmount.setTotalSGSTAmount(totalnvAmount.getTotalSGSTAmount().subtract(cancelInvAmount.getTotalSGSTAmount()));
			totalInvAmount.setTotalCESSAmount(totalnvAmount.getTotalCESSAmount().subtract(cancelInvAmount.getTotalCESSAmount()));
			totalInvAmount.setTcsTdsAmount(totalnvAmount.getTcsTdsAmount().subtract(cancelInvAmount.getTcsTdsAmount()));
			
			return getCalculateTotals(totalInvAmount, clientId, month, yearCode, searchVal, filter,gstrType);
		}else {
			return getCalculateTotals(totalnvAmount, clientId, month, yearCode, searchVal, filter,gstrType);
		}
	}
	
	private TotalInvoiceAmount getCalculateTotals(TotalInvoiceAmount totalInvAmount, String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter,String gstrType){
		if(totalInvAmount == null) {
			return totalInvAmount;
		}
		TotalInvoiceAmount atotalInvAmount = getAmendmentTotalInvoicesAmountsForMonth(clientId, month, yearCode, searchVal, filter, gstrType);
		if(atotalInvAmount == null) {
			return totalInvAmount;
		}
		totalInvAmount.setTotalTaxableAmount(totalInvAmount.getTotalTaxableAmount().subtract(atotalInvAmount.getTotalTaxableAmount()));
		totalInvAmount.setTotalExemptedAmount(totalInvAmount.getTotalExemptedAmount().subtract(atotalInvAmount.getTotalExemptedAmount()));
		totalInvAmount.setTotalTaxAmount(totalInvAmount.getTotalTaxAmount().subtract(atotalInvAmount.getTotalTaxAmount()));
		totalInvAmount.setTotalAmount(totalInvAmount.getTotalAmount().subtract(atotalInvAmount.getTotalAmount()));
		totalInvAmount.setTotalIGSTAmount(totalInvAmount.getTotalIGSTAmount().subtract(atotalInvAmount.getTotalIGSTAmount()));
		totalInvAmount.setTotalCGSTAmount(totalInvAmount.getTotalCGSTAmount().subtract(atotalInvAmount.getTotalCGSTAmount()));
		totalInvAmount.setTotalSGSTAmount(totalInvAmount.getTotalSGSTAmount().subtract(atotalInvAmount.getTotalSGSTAmount()));
		totalInvAmount.setTotalCESSAmount(totalInvAmount.getTotalCESSAmount().subtract(atotalInvAmount.getTotalCESSAmount()));
		totalInvAmount.setTcsTdsAmount(totalInvAmount.getTcsTdsAmount().subtract(atotalInvAmount.getTcsTdsAmount()));
		return totalInvAmount;
	}
	
	private TotalInvoiceAmount getMultimonthCalculateTotals(TotalInvoiceAmount totalInvAmount, String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter,String gstrType){
		if(totalInvAmount == null) {
			return totalInvAmount;
		}
		TotalInvoiceAmount atotalInvAmount = getMultiMonthAmendmentTotalInvoicesAmountsForMonth(clientId, month, yearCode, searchVal, filter, gstrType);
		if(atotalInvAmount == null) {
			return totalInvAmount;
		}
		totalInvAmount.setTotalTaxableAmount(totalInvAmount.getTotalTaxableAmount().subtract(atotalInvAmount.getTotalTaxableAmount()));
		totalInvAmount.setTotalExemptedAmount(totalInvAmount.getTotalExemptedAmount().subtract(atotalInvAmount.getTotalExemptedAmount()));
		totalInvAmount.setTotalTaxAmount(totalInvAmount.getTotalTaxAmount().subtract(atotalInvAmount.getTotalTaxAmount()));
		totalInvAmount.setTotalAmount(totalInvAmount.getTotalAmount().subtract(atotalInvAmount.getTotalAmount()));
		totalInvAmount.setTotalIGSTAmount(totalInvAmount.getTotalIGSTAmount().subtract(atotalInvAmount.getTotalIGSTAmount()));
		totalInvAmount.setTotalCGSTAmount(totalInvAmount.getTotalCGSTAmount().subtract(atotalInvAmount.getTotalCGSTAmount()));
		totalInvAmount.setTotalSGSTAmount(totalInvAmount.getTotalSGSTAmount().subtract(atotalInvAmount.getTotalSGSTAmount()));
		totalInvAmount.setTotalCESSAmount(totalInvAmount.getTotalCESSAmount().subtract(atotalInvAmount.getTotalCESSAmount()));
		totalInvAmount.setTcsTdsAmount(totalInvAmount.getTcsTdsAmount().subtract(atotalInvAmount.getTcsTdsAmount()));
		return totalInvAmount;
	}
	
	private TotalInvoiceAmount getAmendmentTotalInvoicesAmountsForMonth(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter,String gstrType){
		
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		
		Query query = Query.query(criteria);
		
		List<String> amendmentRefIds = mongoTemplate.getCollection(COLLECTION_NAME).distinct("amendmentRefId", query.getQueryObject());
		
		if(NullUtil.isNotEmpty(amendmentRefIds)){
			List<ObjectId> amendmentinvids = Lists.newArrayList();
			amendmentRefIds.stream().forEach(id -> amendmentinvids.add(new ObjectId(id)));
			Criteria acriteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode).and("_id").in(amendmentinvids);
			if(month > 0) {
				acriteria.and("mthCd").is(month+"");
			}
			applyFilterToCriteria(acriteria, filter);
			if(StringUtils.hasLength(searchVal)){
				acriteria = new Criteria().andOperator(acriteria, getSearchValueCriteria(searchVal));
			}
			MatchOperation matchOperation = Aggregation.match(acriteria);
			ProjectionOperation projectionOperation = null;
			GroupOperation groupOperation = null;
			if(month > 0) {
				projectionOperation = getProjectionForTotal("mthCd");
				groupOperation = getGroupForTotal("mthCd");
			}else {
				projectionOperation = getProjectionForTotal("yrCd");
				groupOperation = getGroupForTotal("yrCd");
			}
			AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
			TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
			return totalnvAmount;
		}
		return null;
	}
	
private TotalInvoiceAmount getMultiMonthAmendmentTotalInvoicesAmountsForMonth(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter,String gstrType){
		
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode)
		.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		
		List<String> amendmentRefIds = mongoTemplate.getCollection(COLLECTION_NAME).distinct("amendmentRefId", query.getQueryObject());
		
		if(NullUtil.isNotEmpty(amendmentRefIds)){
			List<ObjectId> amendmentinvids = Lists.newArrayList();
			amendmentRefIds.stream().forEach(id -> amendmentinvids.add(new ObjectId(id)));
			Criteria acriteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode).and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS)
					.and("_id").in(amendmentinvids);
			if(month > 0) {
				acriteria.and("mthCd").is(month+"");
			}
			applyFilterToCriteria(acriteria, filter);
			if(StringUtils.hasLength(searchVal)){
				acriteria = new Criteria().andOperator(acriteria, getSearchValueCriteria(searchVal));
			}
			MatchOperation matchOperation = Aggregation.match(acriteria);
			ProjectionOperation projectionOperation = null;
			GroupOperation groupOperation = null;
			if(month > 0) {
				projectionOperation = getProjectionForTotal("mthCd");
				groupOperation = getGroupForTotal("mthCd");
			}else {
				projectionOperation = getProjectionForTotal("yrCd");
				groupOperation = getGroupForTotal("yrCd");
			}
			AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
			TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
			return totalnvAmount;
		}
		return null;
	}
	
	private TotalInvoiceAmount getCalculateForCustom(TotalInvoiceAmount totalInvAmount, String clientId, Date stDate, Date endDate, String searchVal, InvoiceFilter filter){
		TotalInvoiceAmount atotalInvAmount = getAmendmentTotalInvoicesAmountsForCustom(clientId, stDate, endDate, searchVal, filter);
		if(atotalInvAmount == null) {
			return totalInvAmount;
		}
		totalInvAmount.setTotalTaxableAmount(totalInvAmount.getTotalTaxableAmount().subtract(atotalInvAmount.getTotalTaxableAmount()));
		totalInvAmount.setTotalExemptedAmount(totalInvAmount.getTotalExemptedAmount().subtract(atotalInvAmount.getTotalExemptedAmount()));
		totalInvAmount.setTotalTaxAmount(totalInvAmount.getTotalTaxAmount().subtract(atotalInvAmount.getTotalTaxAmount()));
		totalInvAmount.setTotalAmount(totalInvAmount.getTotalAmount().subtract(atotalInvAmount.getTotalAmount()));
		totalInvAmount.setTotalIGSTAmount(totalInvAmount.getTotalIGSTAmount().subtract(atotalInvAmount.getTotalIGSTAmount()));
		totalInvAmount.setTotalCGSTAmount(totalInvAmount.getTotalCGSTAmount().subtract(atotalInvAmount.getTotalCGSTAmount()));
		totalInvAmount.setTotalSGSTAmount(totalInvAmount.getTotalSGSTAmount().subtract(atotalInvAmount.getTotalSGSTAmount()));
		totalInvAmount.setTotalCESSAmount(totalInvAmount.getTotalCESSAmount().subtract(atotalInvAmount.getTotalCESSAmount()));
		totalInvAmount.setTcsTdsAmount(totalInvAmount.getTcsTdsAmount().subtract(atotalInvAmount.getTcsTdsAmount()));
		return totalInvAmount;
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmountsForCustom(String clientid, Date stDate, Date endDate, String searchVal, InvoiceFilter filter){
		//Criteria criteria = Criteria.where("clientid").is(clientid).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
		GroupOperation groupOperation = getGroupForTotal("csftr");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		TotalInvoiceAmount cancelInvAmount = new TotalInvoiceAmount();
		if(filter != null) {
			cancelInvAmount = getCancelledTotalInvoicesAmountsForCustom(clientid,stDate,endDate,searchVal,filter);
			if(filter.getPaymentStatus() != null && filter.getPaymentStatus().length == 1) {
				List<String> statusList = Arrays.asList(filter.getPaymentStatus());
				if(statusList.contains("CANCELLED")) {
					return cancelInvAmount;
				}
			}
		}
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
		if(totalnvAmount != null && cancelInvAmount != null) {
			totalInvAmount.setTotalTransactions(totalnvAmount.getTotalTransactions());
			totalInvAmount.setTotalTaxableAmount(totalnvAmount.getTotalTaxableAmount().subtract(cancelInvAmount.getTotalTaxableAmount()));
			totalInvAmount.setTotalExemptedAmount(totalnvAmount.getTotalExemptedAmount().subtract(cancelInvAmount.getTotalExemptedAmount()));
			totalInvAmount.setTotalTaxAmount(totalnvAmount.getTotalTaxAmount().subtract(cancelInvAmount.getTotalTaxAmount()));
			totalInvAmount.setTotalAmount(totalnvAmount.getTotalAmount().subtract(cancelInvAmount.getTotalAmount()));
			totalInvAmount.setTotalIGSTAmount(totalnvAmount.getTotalIGSTAmount().subtract(cancelInvAmount.getTotalIGSTAmount()));
			totalInvAmount.setTotalCGSTAmount(totalnvAmount.getTotalCGSTAmount().subtract(cancelInvAmount.getTotalCGSTAmount()));
			totalInvAmount.setTotalSGSTAmount(totalnvAmount.getTotalSGSTAmount().subtract(cancelInvAmount.getTotalSGSTAmount()));
			totalInvAmount.setTotalCESSAmount(totalnvAmount.getTotalCESSAmount().subtract(cancelInvAmount.getTotalCESSAmount()));
			totalInvAmount.setTcsTdsAmount(totalnvAmount.getTcsTdsAmount().subtract(cancelInvAmount.getTcsTdsAmount()));
			return getCalculateForCustom(totalInvAmount, clientid, stDate, endDate, searchVal, filter);
			//return totalInvAmount;
		}else {
			return getCalculateForCustom(totalnvAmount, clientid, stDate, endDate, searchVal, filter);
			//return totalnvAmount;
		}
	}
	
	private TotalInvoiceAmount getAmendmentTotalInvoicesAmountsForCustom(String clientid, Date stDate, Date endDate, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate);
		
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		
		Query query = Query.query(criteria);
		
		List<String> amendmentRefIds = mongoTemplate.getCollection(COLLECTION_NAME).distinct("amendmentRefId", query.getQueryObject());
		
		if(NullUtil.isNotEmpty(amendmentRefIds)){
			List<ObjectId> amendmentinvids = Lists.newArrayList();
			amendmentRefIds.stream().forEach(id -> amendmentinvids.add(new ObjectId(id)));
			Criteria acriteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate).and("_id").in(amendmentinvids);
			applyFilterToCriteria(acriteria, filter);
			if(StringUtils.hasLength(searchVal)){
				acriteria = new Criteria().andOperator(acriteria, getSearchValueCriteria(searchVal));
			}
			MatchOperation matchOperation = Aggregation.match(acriteria);
			ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
			GroupOperation groupOperation = getGroupForTotal("csftr");
			AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
			TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
			return totalnvAmount;
		}
		return null;
	}
	
	public List<TotalInvoiceAmount> getTotalEinvoicesAmountsForCustom(String clientid,final List<String> invTypes, Date stDate, Date endDate, String searchVal, InvoiceFilter filter){
		//Criteria criteria = Criteria.where("clientid").is(clientid).and("invtype").in(invTypes).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		Criteria criteria = Criteria.where("clientid").is(clientid).and("invtype").in(invTypes).and("dateofinvoice").gte(stDate).lte(endDate);	
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
		GroupOperation groupOperation = getGroupForTotal("csftr");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledTotalEinvoicesAmountsForCustom(clientid,stDate,endDate,searchVal,filter);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
					gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
					gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
					gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
					gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
				}
			}
		}
		return  totalInvAmount;
	}

	public TotalInvoiceAmount getTotalInvoicesAmountsForFp(String clientId, String fp, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("fp").is(fp);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("fp");
		GroupOperation groupOperation = getGroupForTotal("fp");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		TotalInvoiceAmount cancelInvAmount = new TotalInvoiceAmount();
		if(filter != null) {
			cancelInvAmount = getCancelledTotalInvoicesAmountsForFp(clientId,fp,searchVal,filter);
			if(filter.getPaymentStatus() != null && filter.getPaymentStatus().length == 1) {
				List<String> statusList = Arrays.asList(filter.getPaymentStatus());
				if(statusList.contains("CANCELLED")) {
					return cancelInvAmount;
				}
			}
		}
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
		if(totalnvAmount != null && cancelInvAmount != null) {
			totalInvAmount.setTotalTransactions(totalnvAmount.getTotalTransactions());
			totalInvAmount.setTotalTaxableAmount(totalnvAmount.getTotalTaxableAmount().subtract(cancelInvAmount.getTotalTaxableAmount()));
			totalInvAmount.setTotalExemptedAmount(totalnvAmount.getTotalExemptedAmount().subtract(cancelInvAmount.getTotalExemptedAmount()));
			totalInvAmount.setTotalTaxAmount(totalnvAmount.getTotalTaxAmount().subtract(cancelInvAmount.getTotalTaxAmount()));
			totalInvAmount.setTotalAmount(totalnvAmount.getTotalAmount().subtract(cancelInvAmount.getTotalAmount()));
			totalInvAmount.setTotalIGSTAmount(totalnvAmount.getTotalIGSTAmount().subtract(cancelInvAmount.getTotalIGSTAmount()));
			totalInvAmount.setTotalCGSTAmount(totalnvAmount.getTotalCGSTAmount().subtract(cancelInvAmount.getTotalCGSTAmount()));
			totalInvAmount.setTotalSGSTAmount(totalnvAmount.getTotalSGSTAmount().subtract(cancelInvAmount.getTotalSGSTAmount()));
			totalInvAmount.setTotalCESSAmount(totalnvAmount.getTotalCESSAmount().subtract(cancelInvAmount.getTotalCESSAmount()));
			totalInvAmount.setTcsTdsAmount(totalnvAmount.getTcsTdsAmount().subtract(cancelInvAmount.getTcsTdsAmount()));
			return totalInvAmount;
		}else {
			return totalnvAmount;
		}
	}	
	
	public TotalInvoiceAmount getTotalInvoicesAmountsForFpQuarterly(String clientId,int month,int year, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientId);
		
		List<String> mnthcd = Lists.newArrayList();
		if(month == 1 || month == 2 || month == 3) {
			mnthcd = Arrays.asList("0"+1+year,"0"+2+year,"0"+3+year);
		}else if(month == 4 || month == 5 || month == 6) {
			mnthcd = Arrays.asList("0"+4+year,"0"+5+year,"0"+6+year);
		}else if(month == 7 || month == 8 || month == 9) {
			mnthcd = Arrays.asList("0"+7+year,"0"+8+year,"0"+9+year);
		}else {
			mnthcd = Arrays.asList(10+""+year,11+""+year,12+""+year);
		}
		
		criteria.and("fp").in(mnthcd);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
		GroupOperation groupOperation = getGroupForTotal("csftr");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		TotalInvoiceAmount cancelInvAmount = new TotalInvoiceAmount();
		if(filter != null) {
			cancelInvAmount = getCancelledTotalInvoicesAmountsForFpQuarterly(clientId,month,year,searchVal,filter);
			if(filter.getPaymentStatus() != null && filter.getPaymentStatus().length == 1) {
				List<String> statusList = Arrays.asList(filter.getPaymentStatus());
				if(statusList.contains("CANCELLED")) {
					return cancelInvAmount;
				}
			}
		}
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
		if(totalnvAmount != null && cancelInvAmount != null) {
			totalInvAmount.setTotalTransactions(totalnvAmount.getTotalTransactions());
			totalInvAmount.setTotalTaxableAmount(totalnvAmount.getTotalTaxableAmount().subtract(cancelInvAmount.getTotalTaxableAmount()));
			totalInvAmount.setTotalExemptedAmount(totalnvAmount.getTotalExemptedAmount().subtract(cancelInvAmount.getTotalExemptedAmount()));
			totalInvAmount.setTotalTaxAmount(totalnvAmount.getTotalTaxAmount().subtract(cancelInvAmount.getTotalTaxAmount()));
			totalInvAmount.setTotalAmount(totalnvAmount.getTotalAmount().subtract(cancelInvAmount.getTotalAmount()));
			totalInvAmount.setTotalIGSTAmount(totalnvAmount.getTotalIGSTAmount().subtract(cancelInvAmount.getTotalIGSTAmount()));
			totalInvAmount.setTotalCGSTAmount(totalnvAmount.getTotalCGSTAmount().subtract(cancelInvAmount.getTotalCGSTAmount()));
			totalInvAmount.setTotalSGSTAmount(totalnvAmount.getTotalSGSTAmount().subtract(cancelInvAmount.getTotalSGSTAmount()));
			totalInvAmount.setTotalCESSAmount(totalnvAmount.getTotalCESSAmount().subtract(cancelInvAmount.getTotalCESSAmount()));
			totalInvAmount.setTcsTdsAmount(totalnvAmount.getTcsTdsAmount().subtract(cancelInvAmount.getTcsTdsAmount()));
			return totalInvAmount;
		}else {
			return totalnvAmount;
		}
	}
	private ProjectionOperation getProjectionForTotal(String fieldname){
		return Aggregation.project().andInclude(fieldname)
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("tcstdsAmount").multiply("sftr").as("tcstdsAmount");
	}
	
	private GroupOperation getGroupForTotal(String fieldname){
		return Aggregation.group(fieldname).count().as("totalTransactions")
				.sum("totalamount").as("totalAmount")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("totalExemptedAmount").as("totalExemptedAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount");
	}
	
	public List<String> getBillToNames(String clientId, int month,String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCd);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmounts(String clientId, List<String> invTypes, String fp, String searchVal){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("invtype").in(invTypes).and("fp").is(fp);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("mthCd");
		GroupOperation groupOperation = getGroupForTotal("mthCd");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	}
	
	public Page<GSTR1> findByClientidAndMonthAndYear(final String clientid, int month, String yearCode, int start, int length, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0){
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));
		}else {
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
	}
	
public Page<GSTR1> findByClientidAndMonthAndYearPrevMonthPendingInvoices(final String clientid, int month, String yearCode, int start, int length, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientid);
		String yrcd[] = yearCode.split("-");
		String yr = yrcd[0];
		int iyr = Integer.parseInt(yr);
		List<String> yearcodes = Lists.newArrayList();
		for(int i=2017;i<iyr;i++) {
			String yearcode = i+""+"-"+(i+1)+"";
			yearcodes.add(yearcode);
		}
		List<String> mnthcd = Lists.newArrayList();
		if(month == 1) {
			mnthcd = Arrays.asList(1+"",4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
		}else if(month == 2) {
			mnthcd = Arrays.asList(1+"",2+"",4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
		}else if(month == 3) {
			mnthcd = Arrays.asList(1+"",2+"",3+"",4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
		}else {
			for(int i=4;i<=month;i++) {
				mnthcd.add(i+"");
			}
		}
		Criteria crt1 = Criteria.where("yrCd").is(yearCode).and("mthCd").in(mnthcd);
		Criteria crt = new Criteria().orOperator(Criteria.where("yrCd").in(yearcodes),crt1);
		Query query2 = Query.query(crt);
		applyFilterToCriteria(criteria, filter);
		criteria = new Criteria().andOperator(criteria, crt);
		Query query1 = Query.query(criteria);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));			
		}else {
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
	}
	
public TotalInvoiceAmount getTotalInvoicesAmountsForMonthPreviousMonthPendingInvoices(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter){
	Criteria criteria = Criteria.where("clientid").is(clientId);
	String yrcd[] = yearCode.split("-");
	String yr = yrcd[0];
	int iyr = Integer.parseInt(yr);
	List<String> yearcodes = Lists.newArrayList();
	for(int i=2017;i<iyr;i++) {
		String yearcode = i+""+"-"+(i+1)+"";
		yearcodes.add(yearcode);
	}
	List<String> mnthcd = Lists.newArrayList();
	if(month == 1) {
		mnthcd = Arrays.asList(4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
	}else if(month == 2) {
		mnthcd = Arrays.asList(1+"",4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
	}else if(month == 3) {
		mnthcd = Arrays.asList(1+"",2+"",4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
	}else {
		for(int i=4;i<month;i++) {
			mnthcd.add(i+"");
		}
	}
	Criteria crt1 = Criteria.where("yrCd").is(yearCode).and("mthCd").in(mnthcd);
	Criteria crt = new Criteria().orOperator(Criteria.where("yrCd").in(yearcodes),crt1);
	applyFilterToCriteria(criteria, filter);
	criteria = new Criteria().andOperator(criteria, crt);
	Criteria crt2 = new Criteria().orOperator(Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode).and("mthCd").is(month+""),criteria);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	MatchOperation matchOperation = Aggregation.match(crt2);
	ProjectionOperation projectionOperation = null;
	GroupOperation groupOperation = null;
	projectionOperation = getProjectionForTotal("csftr");
	groupOperation = getGroupForTotal("csftr");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return results.getUniqueMappedResult();
}

	public List<String> allStatusList(List<String> statusList){
		List<String> allStatusList = Lists.newArrayList();
		allStatusList.add("Filed");
		allStatusList.add("Submitted");
		allStatusList.add("In Progress");
		allStatusList.add("CANCELLED");
		allStatusList.add("SUCCESS");
		allStatusList.add("Failed");
		allStatusList.add("Pending");
		allStatusList.removeAll(statusList);
		return allStatusList;
		
	}
	public Page<GSTR1> findByClientidAndFromtimeAndTotime(final String clientid, Date stDate, Date endDate, int start, int length, String searchVal, InvoiceFilter filter){
		//Criteria criteria = Criteria.where("clientid").is(clientid).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate);	
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));			
		}else {
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
	}

	private void applyFilterToCriteria(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			List<Criteria> criterias = new ArrayList<Criteria>();
			if(filter.getPaymentStatus() != null){
				Criteria criteriaa = null;
				String fieldName = "books".equals(filter.getBooksOrReturns()) ? "paymentStatus":"gstStatus";
				int statusListlen = "books".equals(filter.getBooksOrReturns()) ? 3:7;
				List<String> statusList = Arrays.asList(filter.getPaymentStatus());
				if(statusList.size() < statusListlen) {
					if(statusList.contains("Pending") && statusList.contains("Failed") && statusList.size() <= 2) {
						List<String> allStatusList = Arrays.asList("Filed","Submitted","In Progress","SUCCESS","CANCELLED");
						criteriaa = new Criteria().and(fieldName).nin(allStatusList);
					}else if((statusList.contains("Not Paid") || statusList.contains("Pending")) && !statusList.contains("Failed")){
						criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where(fieldName).in(statusList), Criteria.where(fieldName).is(""),Criteria.where(fieldName).is(null)));
					}else if(statusList.contains("Failed")) {
						List<String> allStatusList = allStatusList(statusList);
						if(statusList.contains("Pending")) {
							criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where(fieldName).nin(allStatusList), Criteria.where(fieldName).is(null)));	
						}else {
							criteriaa = new Criteria().andOperator(criteria,Criteria.where(fieldName).nin(allStatusList),Criteria.where(fieldName).ne(null));
						}
					}else{
						criteria.and(fieldName).in(statusList);
					}
					if(NullUtil.isNotEmpty(criteriaa)) {
						criterias.add(criteriaa);
					}
				}
				
			}
			if(filter.getIrnStatus() != null){
				Criteria criteriaa = null;
				String fieldName =  "irnStatus";
				List<String> statusList = new LinkedList<String>(Arrays.asList(filter.getIrnStatus()));
				if((statusList.contains("Not Generated") || statusList.contains(" ")) && !statusList.contains("Failed")){
					statusList.add(null);
					statusList.add("");
					criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where(fieldName).in(statusList), Criteria.where(fieldName).is(""),Criteria.where(fieldName).is(null)));
				} else if(statusList.contains("Failed")) {
					List<String> allStatusList = Lists.newArrayList();
					allStatusList.add("Generated");
					allStatusList.add("Cancelled");
					if(statusList.contains("Not Generated") || statusList.contains(" ")) {
						criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where(fieldName).nin(allStatusList), Criteria.where(fieldName).is(""),Criteria.where(fieldName).is(null)));	
					}else {
						allStatusList.add("");
						criteriaa = new Criteria().andOperator(criteria,Criteria.where(fieldName).nin(allStatusList),Criteria.where(fieldName).ne(null));
					}
				}else {
					criteria.and(fieldName).in(statusList);
				}
				if(NullUtil.isNotEmpty(criteriaa)) {
					criterias.add(criteriaa);
				}
				
			}
			if(filter.getInvoiceType() != null){
				List<String> invTypeList = new LinkedList<String>(Arrays.asList(filter.getInvoiceType()));
				List<String> creditDebitList = Lists.newArrayList();
				List<String> creditDebitURList = Lists.newArrayList();
				if(invTypeList.contains("Credit Note")){
					creditDebitList.add("C");
					invTypeList.remove("Credit Note");
				}
				if(invTypeList.contains("Debit Note")){
					creditDebitList.add("D");
					invTypeList.remove("Debit Note");
				}
				if(invTypeList.contains("Credit Note(UR)")){
					creditDebitURList.add("C");
					invTypeList.remove("Credit Note(UR)");
				}
				if(invTypeList.contains("Debit Note(UR)")){
					creditDebitURList.add("D");
					invTypeList.remove("Debit Note(UR)");
				}
				Criteria criteriaa = null;
				if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdnr.0.nt.0.ntty").in(creditDebitList),Criteria.where("cdnur.0.ntty").in(creditDebitURList)));
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("cdnr.0.nt.0.ntty").in(creditDebitList),Criteria.where("cdnur.0.ntty").in(creditDebitURList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdnur.0.ntty").in(creditDebitURList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isEmpty(creditDebitURList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdnr.0.nt.0.ntty").in(creditDebitList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList) && NullUtil.isEmpty(creditDebitURList)) {
					criteria.and("invtype").in(invTypeList);
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isEmpty(creditDebitURList)) {
					criteria.and("cdnr.0.nt.0.ntty").in(creditDebitList);
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
					criteria.and("cdnur.0.ntty").in(creditDebitURList);
				}
				if(NullUtil.isNotEmpty(criteriaa)) {
					criterias.add(criteriaa);
				}
			}
			if(filter.getUser() != null){
				criteria.and("fullname").in(Arrays.asList(filter.getUser()));
			}
			if(filter.getVendor() != null){
				criteria.and("billedtoname").in(Arrays.asList(filter.getVendor()));
			}
			if(filter.getBranch() != null){
				criteria.and("branch").in(Arrays.asList(filter.getBranch()));
			}
			if(filter.getVertical() != null){
				criteria.and("vertical").in(Arrays.asList(filter.getVertical()));
			}
			
			if(filter.getCustomFieldText1() != null){
				criteria.and("customField1").in(Arrays.asList(filter.getCustomFieldText1()));
			}
			if(filter.getCustomFieldText2() != null){
				criteria.and("customField2").in(Arrays.asList(filter.getCustomFieldText2()));
			}
			if(filter.getCustomFieldText3() != null){
				criteria.and("customField3").in(Arrays.asList(filter.getCustomFieldText3()));
			}
			if(filter.getCustomFieldText4() != null){
				criteria.and("customField4").in(Arrays.asList(filter.getCustomFieldText4()));
			}
			
			if(filter.getReverseCharge() != null){
				List<String> reverseChargeList = new LinkedList<String>(Arrays.asList(filter.getReverseCharge()));
				if(reverseChargeList.contains("Regular")) {
					reverseChargeList.add("N");
				}
				if(reverseChargeList.contains("Reverse")) {
					reverseChargeList.add("Y");
				}
				if(reverseChargeList.contains("Regular")){
					Criteria criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("revchargetype").in(reverseChargeList), Criteria.where("revchargetype").is(null)));	
					criterias.add(criteriaa);
				}else{
					criteria.and("revchargetype").in(reverseChargeList);
				}
			}
			/*if(filter.getCustomFieldText1() != null){
				Criteria criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("customFieldText1").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText2").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText3").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText4").in(Arrays.asList(filter.getCustomFieldText1()))));	
				criterias.add(criteriaa);
			}*/
			if(NullUtil.isNotEmpty(criterias)) {
				criteria.andOperator(criterias.toArray(new Criteria[criterias.size()]));				
			}
		}
	}
	
	public Page<GSTR1> findByClientidAndInvtypeInAndFp(final String clientId, final List<String> invTypes, String fp, int start, int length, String searchVal){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("invtype").in(invTypes).and("fp").is(fp);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));			
		}else {
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
	}
	
	public Page<GSTR1> findByClientidAndFp(final String clientId, String fp, int start, int length, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("fp").is(fp);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));			
		}else {
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
 		if(searchVal.toUpperCase().startsWith("PE")) {
 			criterias.add(new Criteria().orOperator(Criteria.where("gstStatus").is(""), Criteria.where("gstStatus").is(null)));
 		}else if(searchVal.toUpperCase().startsWith("UP") || searchVal.startsWith("Up")) {
 			criterias.add(Criteria.where("gstStatus").regex(MasterGSTConstants.GST_STATUS_SUCCESS, "i"));
 		}else {
 			criterias.add(Criteria.where("gstStatus").regex(searchVal, "i"));
 		}
 		if(searchVal.toUpperCase().startsWith("NO")) {
 			criterias.add(new Criteria().orOperator(Criteria.where("paymentStatus").is(""), Criteria.where("paymentStatus").is(null)));
 		}else {
 			criterias.add(Criteria.where("paymentStatus").regex(searchVal, "i"));
 		}
 		criterias.add(Criteria.where("invtype").regex(searchVal, "i"));
 		criterias.add(Criteria.where("billedtoname").regex(searchVal, "i"));
 		criterias.add(Criteria.where("invoiceno").regex(searchVal, "i"));
 		criterias.add(Criteria.where("b2b.0.ctin").regex(searchVal, "i"));
 		criterias.add(Criteria.where("irnNo").regex(searchVal, "i"));
		criterias.add(Criteria.where("irnStatus").regex(searchVal, "i"));
		criterias.add(Criteria.where("customField1").regex(searchVal, "i"));
		criterias.add(Criteria.where("customField2").regex(searchVal, "i"));
		criterias.add(Criteria.where("customField3").regex(searchVal, "i"));
		criterias.add(Criteria.where("customField4").regex(searchVal, "i"));
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
			criterias.add(Criteria.where("totaltax_str").regex(serForNum, "i"));
			criterias.add(Criteria.where("totalamount_str").regex(serForNum, "i"));
		}
 		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}
	
	private void addAllInvoicesQueryFirlds(Query query){
		query.fields().include("fullname");
		query.fields().include("clientid");
		query.fields().include("gstStatus");
		query.fields().include("paymentStatus");
		query.fields().include("revchargetype");
		query.fields().include("invtype");
		query.fields().include("invoiceno");
		query.fields().include("billedtoname");
		query.fields().include("b2b");
		query.fields().include("cdnr");
		query.fields().include("cdnur");
		query.fields().include("exp");
		query.fields().include("cfs");
		query.fields().include("dateofinvoice");
		query.fields().include("totaltaxableamount");
		query.fields().include("totaltax");
		query.fields().include("totalIgstAmount");
		query.fields().include("totalCgstAmount");
		query.fields().include("totalSgstAmount");
		query.fields().include("totalCessAmount");
		query.fields().include("totalamount");
		query.fields().include("pendingAmount");
		query.fields().include("receivedAmount");
		query.fields().include("branch");
		query.fields().include("vertical");
		query.fields().include("irnStatus");
		query.fields().include("irnNo");
	}
	
	public List<TotalInvoiceAmount> getConsolidatedSummeryForYearMonthwise(final Client client, String yearCd, boolean checkQuarterly,String gstrType, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd);
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
				.andInclude("mthCd","tcstdsAmount");
		GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");		
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedSummeryForYearMonthwise(client,yearCd,checkQuarterly,gstrType);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
					gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
					gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
					gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
					gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
				}
			}
		}
		return getCalculatedSummeryForYearMonthwise(totalInvAmount, client, yearCd, checkQuarterly, gstrType, filter);
		//return  totalInvAmount;
	}
	
	private List<TotalInvoiceAmount> getCalculatedSummeryForYearMonthwise(List<TotalInvoiceAmount> totalInvAmount, Client client, String yearCd, boolean checkQuarterly, String gstrType, InvoiceFilter filter) {
		
		List<TotalInvoiceAmount> atotalInvAmount = getAmendmentConsolidatedSummeryForYearMonthwise(client, yearCd, checkQuarterly, gstrType, filter);
		if(atotalInvAmount == null) {
			return totalInvAmount;
		}
		for(TotalInvoiceAmount amendmentAmount : atotalInvAmount){
			String acode = amendmentAmount.get_id();
			for(TotalInvoiceAmount totalAmount : totalInvAmount){
				String code = totalAmount.get_id();
				if(acode.equals(code)) {
					totalAmount.setTotalTaxableAmount(totalAmount.getTotalTaxableAmount().subtract(amendmentAmount.getTotalTaxableAmount()));
					totalAmount.setTotalExemptedAmount(totalAmount.getTotalExemptedAmount().subtract(amendmentAmount.getTotalExemptedAmount()));
					totalAmount.setTotalTaxAmount(totalAmount.getTotalTaxAmount().subtract(amendmentAmount.getTotalTaxAmount()));
					totalAmount.setTotalAmount(totalAmount.getTotalAmount().subtract(amendmentAmount.getTotalAmount()));
					totalAmount.setTotalIGSTAmount(totalAmount.getTotalIGSTAmount().subtract(amendmentAmount.getTotalIGSTAmount()));
					totalAmount.setTotalCGSTAmount(totalAmount.getTotalCGSTAmount().subtract(amendmentAmount.getTotalCGSTAmount()));
					totalAmount.setTotalSGSTAmount(totalAmount.getTotalSGSTAmount().subtract(amendmentAmount.getTotalSGSTAmount()));
					totalAmount.setTotalCESSAmount(totalAmount.getTotalCESSAmount().subtract(amendmentAmount.getTotalCESSAmount()));
				}
			}
		}
		return totalInvAmount;
	}

	public List<InvoiceTypeSummery> getInvoiceSummeryByTypeForMonth(final String clientid, int month, String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCd);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totalamount").multiply("sftr").as("totalamount")
				.and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.andInclude("invtype");
		GroupOperation groupOperation = Aggregation.group("invtype")
				.count().as("noOfInvoices")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totaltax").as("totalTaxAmount")
				;
		AggregationResults<InvoiceTypeSummery> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, InvoiceTypeSummery.class);
		List<InvoiceTypeSummery> totalCanInvAmount = getCancelledInvoiceSummeryByTypeForMonth(clientid,month,yearCd);
		List<InvoiceTypeSummery> totalInvAmount = results.getMappedResults();
		for(InvoiceTypeSummery gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(InvoiceTypeSummery gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
				}
			}
		}
		return  totalInvAmount;
	}
	
	public List<InvoiceTypeSummery> getCancelledInvoiceSummeryByTypeForMonth(final String clientid, int month, String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCd).and("gstStatus").is("CANCELLED");
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totalamount").multiply("sftr").as("totalamount")
				.and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.andInclude("invtype");
		GroupOperation groupOperation = Aggregation.group("invtype")
				.count().as("noOfInvoices")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totaltax").as("totalTaxAmount")
				;
		AggregationResults<InvoiceTypeSummery> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, InvoiceTypeSummery.class);
		return  results.getMappedResults();
	}
	
	public Map<String, Integer> getTotalInvoicesByGststatus(final String clientid, int month, int year){
		 String yearCd = getYearCode(month, year);
		Criteria criteria = Criteria.where("clientid").is(clientid).and("mthCd").is(month+"").and("yrCd").is(yearCd);
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		int totalInvoices = 0;
		int totalPending = 0;
		int totalUploaded = 0;
		int totalFailed = 0;
		Map<String, Integer> totalInvoicesByGststatus = Maps.newHashMap();
		List<GSTR1> gstr1Invoices = mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME);
		if(NullUtil.isNotEmpty(gstr1Invoices)) {
			totalInvoices = gstr1Invoices.size();
			for(GSTR1 inv : gstr1Invoices) {
				if(NullUtil.isEmpty(inv.getGstStatus()) || inv.getGstStatus().equalsIgnoreCase("Pending")) {
					totalPending++;
				}else if(inv.getGstStatus().equalsIgnoreCase(MasterGSTConstants.GST_STATUS_SUCCESS) || inv.getGstStatus().equalsIgnoreCase(MasterGSTConstants.STATUS_FILED)) {
					totalUploaded++;
				}else {
					totalFailed++;
				}
			}
		}
		totalInvoicesByGststatus.put("totalInvoices", totalInvoices);
		totalInvoicesByGststatus.put("totalPending", totalPending);
		totalInvoicesByGststatus.put("totalUploaded", totalUploaded);
		totalInvoicesByGststatus.put("totalFailed", totalFailed);
		return totalInvoicesByGststatus;
	}

	public List<TotalInvoiceAmount> getConsolidatedSummeryForCustom(Client client, String returntype, Date stDate, Date endDate,InvoiceFilter filter,String gstrType) {
		//Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("dateofinvoice").gte(stDate).lte(endDate);
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
				.andInclude("mthCd","tcstdsAmount");
		GroupOperation groupOperation = Aggregation.group("mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedSummeryForCustom(client,returntype,stDate,endDate,gstrType);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
					gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
					gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
					gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
					gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
				}
			}
		}
		return getAmendmentConsolidatedSummeryForCustom(totalInvAmount, client, stDate, endDate, gstrType, filter);
		//return  totalInvAmount;
	}
	

	public List<String> getCustomBillToNames(String clientId, Date stDate,Date endDate){
		Criteria criteria = Criteria.where("clientid").is(clientId).andOperator(
				Criteria.where("dateofinvoice").gte(stDate),
				Criteria.where("dateofinvoice").lte(endDate)
		);
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}

	public Page<GSTR1> getMultimonthInvoices(String clientid, List<String> invTypes, String yearCode, int start, int length, String searchVal, InvoiceFilter filter) {
		Criteria criteria = Criteria.where("clientid").is(clientid)
				.and("yrCd").is(yearCode).and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));
		}else {
			addAllInvoicesQueryFirlds(query);
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
	}
	public TotalInvoiceAmount getMultimonthTotalInvoicesAmounts(String clientid, List<String> invTypes, String yearCode,String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("yrCd");
		GroupOperation groupOperation = getGroupForTotal("yrCd");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		TotalInvoiceAmount cancelInvAmount = new TotalInvoiceAmount();
		if(filter != null) {
			cancelInvAmount = getMultimonthTotalCancelledInvoicesAmounts(clientid,invTypes,yearCode,searchVal,filter);
			if(filter.getPaymentStatus() != null && filter.getPaymentStatus().length == 1) {
				List<String> statusList = Arrays.asList(filter.getPaymentStatus());
				if(statusList.contains("CANCELLED")) {
					return cancelInvAmount;
				}
			}
		}
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
		if(totalnvAmount != null && cancelInvAmount != null) {
			totalInvAmount.setTotalTransactions(totalnvAmount.getTotalTransactions());
			totalInvAmount.setTotalTaxableAmount(totalnvAmount.getTotalTaxableAmount().subtract(cancelInvAmount.getTotalTaxableAmount()));
			totalInvAmount.setTotalExemptedAmount(totalnvAmount.getTotalExemptedAmount().subtract(cancelInvAmount.getTotalExemptedAmount()));
			totalInvAmount.setTotalTaxAmount(totalnvAmount.getTotalTaxAmount().subtract(cancelInvAmount.getTotalTaxAmount()));
			totalInvAmount.setTotalAmount(totalnvAmount.getTotalAmount().subtract(cancelInvAmount.getTotalAmount()));
			totalInvAmount.setTotalIGSTAmount(totalnvAmount.getTotalIGSTAmount().subtract(cancelInvAmount.getTotalIGSTAmount()));
			totalInvAmount.setTotalCGSTAmount(totalnvAmount.getTotalCGSTAmount().subtract(cancelInvAmount.getTotalCGSTAmount()));
			totalInvAmount.setTotalSGSTAmount(totalnvAmount.getTotalSGSTAmount().subtract(cancelInvAmount.getTotalSGSTAmount()));
			totalInvAmount.setTotalCESSAmount(totalnvAmount.getTotalCESSAmount().subtract(cancelInvAmount.getTotalCESSAmount()));
			
			return getMultimonthCalculateTotals(totalInvAmount, clientid, 0, yearCode, searchVal, filter, null);
			//return totalInvAmount;
		}else {
			return getMultimonthCalculateTotals(totalnvAmount, clientid, 0, yearCode, searchVal, filter, null);
			//return totalnvAmount;
		}
	}
	
	
	public List<String> getMultimonthBillToNames(String clientId, List<String> invTypes,String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCd)
				.and("invtype").in(invTypes).and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	public List<TotalInvoiceAmount> getConsolidatedMultimonthSummeryForYearMonthwise(Client client,List<String> invTypes, String yearCd,InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd)
				.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
				.andInclude("mthCd","tcstdsAmount");
		GroupOperation groupOperation = Aggregation.group("mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedMultimonthSummeryForYearMonthwise(client,invTypes,yearCd);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
					gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
					gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
					gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
					gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
				}
			}
		}
				
		return getAmendmentConsolidatedMultimonthSummeryForYearMonthwise(totalInvAmount, client, invTypes, yearCd, filter);
	}
	
	public List<TotalInvoiceAmount> getAmendmentConsolidatedMultimonthSummeryForYearMonthwise(List<TotalInvoiceAmount> totalInvAmount, Client client,List<String> invTypes, String yearCd,InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd)
				.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		Query query = Query.query(criteria);
		List<String> amendmentRefIds = mongoTemplate.getCollection(COLLECTION_NAME).distinct("amendmentRefId", query.getQueryObject());
		
		if(NullUtil.isNotEmpty(amendmentRefIds)){
			List<ObjectId> amendmentinvids = Lists.newArrayList();
			Criteria acriteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd)
					.and("_id").in(amendmentinvids).and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
			if(filter.getInvoiceType() == null){
				acriteria.and("invtype").in(invTypes);
			}
			applyFilterToCriteria(criteria, filter);
			amendmentRefIds.stream().forEach(id -> amendmentinvids.add(new ObjectId(id)));
			MatchOperation matchOperation = Aggregation.match(acriteria);
			ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
					.and("totaltax").multiply("sftr").as("totaltax")
					.and("totalamount").multiply("sftr").as("totalamount")
					.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
					.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
					.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
					.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
					.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
					.andInclude("mthCd","tcstdsAmount");
			GroupOperation groupOperation = Aggregation.group("mthCd")
					.sum("totaltaxableamount").as("totalTaxableAmount")
					.sum("tcstdsAmount").as("tcsTdsAmount")
					.sum("totaltax").as("totalTaxAmount")
					.sum("totalamount").as("totalAmount")
					.sum("totalIgstAmount").as("totalIGSTAmount")
					.sum("totalCgstAmount").as("totalCGSTAmount")
					.sum("totalSgstAmount").as("totalSGSTAmount")
					.sum("totalCessAmount").as("totalCESSAmount")
					.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
			AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
			
			List<TotalInvoiceAmount> atotalInvAmount = results.getMappedResults();
			
			if(atotalInvAmount == null) {
				return totalInvAmount;
			}
			
			for(TotalInvoiceAmount amendmentInvoiceAmount : atotalInvAmount){
				String cancode = amendmentInvoiceAmount.get_id();
				for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
					String code = gstr1InvoiceAmount.get_id();
					if(cancode.equals(code)) {
						gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(amendmentInvoiceAmount.getTotalTaxableAmount()));
						gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(amendmentInvoiceAmount.getTotalExemptedAmount()));
						gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(amendmentInvoiceAmount.getTotalTaxAmount()));
						gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(amendmentInvoiceAmount.getTotalAmount()));
						gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(amendmentInvoiceAmount.getTotalIGSTAmount()));
						gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(amendmentInvoiceAmount.getTotalCGSTAmount()));
						gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(amendmentInvoiceAmount.getTotalSGSTAmount()));
						gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(amendmentInvoiceAmount.getTotalCESSAmount()));
					}
				}
			}
		
		}
		return  totalInvAmount;
	}
	
	public Page<? extends InvoiceParent> findByClientidInAndMonthAndYear(List<String> clientids, int month,String yearCode, int start, int length, String searchVal, InvoiceFilter filter,String dwnldFromGSTN) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));
		}else {
			addAllInvoicesQueryFirlds(query);
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
	}

	public TotalInvoiceAmount getTotalInvoicesAmountsForMonth(List<String> clientids, int month, String yearCode,String searchVal, InvoiceFilter filter,String dwnldFromGSTN,String gstrType) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = null;
		GroupOperation groupOperation = null;
		if(month > 0) {
			projectionOperation = getProjectionForTotal("mthCd");
			groupOperation = getGroupForTotal("mthCd");
		}else {
			projectionOperation = getProjectionForTotal("yrCd");
			groupOperation = getGroupForTotal("yrCd");
		}
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		TotalInvoiceAmount cancelInvAmount = new TotalInvoiceAmount();
		if(filter != null) {
				cancelInvAmount = getCancelledTotalInvoicesAmountsForMonth(clientids,month,yearCode,searchVal,filter,dwnldFromGSTN,gstrType);
				if(gstrType.equals("gstrOrEinvoice")) {
					if(filter.getIrnStatus() != null && filter.getIrnStatus().length == 1) {
						List<String> statusList = Arrays.asList(filter.getIrnStatus());
						if(statusList.contains("Cancelled")) {
							return cancelInvAmount;
						}
					}
				}else {
					if(filter.getPaymentStatus() != null && filter.getPaymentStatus().length == 1) {
						List<String> statusList = Arrays.asList(filter.getPaymentStatus());
						if(statusList.contains("CANCELLED")) {
							return cancelInvAmount;
						}
					}
				}
			}
			
		TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
		TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
		if(totalnvAmount != null && cancelInvAmount != null) {
			totalInvAmount.setTotalTransactions(totalnvAmount.getTotalTransactions());
			totalInvAmount.setTotalTaxableAmount(totalnvAmount.getTotalTaxableAmount().subtract(cancelInvAmount.getTotalTaxableAmount()));
			totalInvAmount.setTotalExemptedAmount(totalnvAmount.getTotalExemptedAmount().subtract(cancelInvAmount.getTotalExemptedAmount()));
			totalInvAmount.setTotalTaxAmount(totalnvAmount.getTotalTaxAmount().subtract(cancelInvAmount.getTotalTaxAmount()));
			totalInvAmount.setTotalAmount(totalnvAmount.getTotalAmount().subtract(cancelInvAmount.getTotalAmount()));
			totalInvAmount.setTotalIGSTAmount(totalnvAmount.getTotalIGSTAmount().subtract(cancelInvAmount.getTotalIGSTAmount()));
			totalInvAmount.setTotalCGSTAmount(totalnvAmount.getTotalCGSTAmount().subtract(cancelInvAmount.getTotalCGSTAmount()));
			totalInvAmount.setTotalSGSTAmount(totalnvAmount.getTotalSGSTAmount().subtract(cancelInvAmount.getTotalSGSTAmount()));
			totalInvAmount.setTotalCESSAmount(totalnvAmount.getTotalCESSAmount().subtract(cancelInvAmount.getTotalCESSAmount()));
			
			return getCalculateGobalTotals(totalInvAmount, clientids, month, yearCode, searchVal, filter, gstrType);
			//return totalInvAmount;
		}else {
			return getCalculateGobalTotals(totalnvAmount, clientids, month, yearCode, searchVal, filter, gstrType);
			//return totalnvAmount;
		}
	}

	private TotalInvoiceAmount getCalculateGobalTotals(TotalInvoiceAmount totalInvAmount, List<String> clientids, int month, String yearCode, String searchVal, InvoiceFilter filter, String gstrType) {
		TotalInvoiceAmount atotalInvAmount = getAmendmentTotalInvoicesAmountsForMonth(clientids, month, yearCode, searchVal, filter, gstrType);
		if(atotalInvAmount == null) {
			return totalInvAmount;
		}
		totalInvAmount.setTotalTaxableAmount(totalInvAmount.getTotalTaxableAmount().subtract(atotalInvAmount.getTotalTaxableAmount()));
		totalInvAmount.setTotalExemptedAmount(totalInvAmount.getTotalExemptedAmount().subtract(atotalInvAmount.getTotalExemptedAmount()));
		totalInvAmount.setTotalTaxAmount(totalInvAmount.getTotalTaxAmount().subtract(atotalInvAmount.getTotalTaxAmount()));
		totalInvAmount.setTotalAmount(totalInvAmount.getTotalAmount().subtract(atotalInvAmount.getTotalAmount()));
		totalInvAmount.setTotalIGSTAmount(totalInvAmount.getTotalIGSTAmount().subtract(atotalInvAmount.getTotalIGSTAmount()));
		totalInvAmount.setTotalCGSTAmount(totalInvAmount.getTotalCGSTAmount().subtract(atotalInvAmount.getTotalCGSTAmount()));
		totalInvAmount.setTotalSGSTAmount(totalInvAmount.getTotalSGSTAmount().subtract(atotalInvAmount.getTotalSGSTAmount()));
		totalInvAmount.setTotalCESSAmount(totalInvAmount.getTotalCESSAmount().subtract(atotalInvAmount.getTotalCESSAmount()));
		totalInvAmount.setTcsTdsAmount(totalInvAmount.getTcsTdsAmount().subtract(atotalInvAmount.getTcsTdsAmount()));
		return totalInvAmount;
	}

	private TotalInvoiceAmount getAmendmentTotalInvoicesAmountsForMonth(List<String> clientids, int month,	String yearCode, String searchVal, InvoiceFilter filter, String gstrType) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		
		Query query = Query.query(criteria);
		
		List<String> amendmentRefIds = mongoTemplate.getCollection(COLLECTION_NAME).distinct("amendmentRefId", query.getQueryObject());
		
		if(NullUtil.isNotEmpty(amendmentRefIds)){
			List<ObjectId> amendmentinvids = Lists.newArrayList();
			amendmentRefIds.stream().forEach(id -> amendmentinvids.add(new ObjectId(id)));
			Criteria acriteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode).and("_id").in(amendmentinvids);
			if(month > 0) {
				acriteria.and("mthCd").is(month+"");
			}
			applyFilterToCriteria(acriteria, filter);
			if(StringUtils.hasLength(searchVal)){
				acriteria = new Criteria().andOperator(acriteria, getSearchValueCriteria(searchVal));
			}
			MatchOperation matchOperation = Aggregation.match(acriteria);
			ProjectionOperation projectionOperation = null;
			GroupOperation groupOperation = null;
			if(month > 0) {
				projectionOperation = getProjectionForTotal("mthCd");
				groupOperation = getGroupForTotal("mthCd");
			}else {
				projectionOperation = getProjectionForTotal("yrCd");
				groupOperation = getGroupForTotal("yrCd");
			}
			AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
			TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
			return totalnvAmount;
		}
		return null;
	}

	

	public Page<? extends InvoiceParent> findByClientidInAndFromtimeAndTotime(List<String> clientids, Date stDate, Date endDate, int start, int length, String searchVal, InvoiceFilter filter, String dwnldFromGSTN) {
		//Criteria criteria = Criteria.where("clientid").in(clientids).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		Criteria criteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate);	
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));
		}else {
			addAllInvoicesQueryFirlds(query);
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
	}

	public List<TotalInvoiceAmount> getTotalInvoicesAmountsForCustom(List<String> clientids, Date stDate, Date endDate,String searchVal, InvoiceFilter filter,String dwnldFromGSTN,String gstrType) {
		//Criteria criteria = Criteria.where("clientid").in(clientids).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		Criteria criteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate);	
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
		GroupOperation groupOperation = getGroupForTotal("csftr");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledTotalInvoicesAmountsForCustom(clientids,stDate,endDate,searchVal,filter,dwnldFromGSTN,gstrType);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
					gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
					gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
					gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
					gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
				}
			}
		}
		return getAdmenmentTotalInvoicesAmountsForCustom(totalInvAmount, clientids, stDate, endDate, searchVal, filter, dwnldFromGSTN, gstrType);
		//return  totalInvAmount;
	}
	
	public List<TotalInvoiceAmount> getAdmenmentTotalInvoicesAmountsForCustom(List<TotalInvoiceAmount> totalInvAmount, List<String> clientids, Date stDate, Date endDate,String searchVal, InvoiceFilter filter,String dwnldFromGSTN,String gstrType) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate);	
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		
		Query query = Query.query(criteria);
		
		List<String> amendmentRefIds = mongoTemplate.getCollection(COLLECTION_NAME).distinct("amendmentRefId", query.getQueryObject());
		
		if(NullUtil.isNotEmpty(amendmentRefIds)) {
			List<ObjectId> amendmentinvids = Lists.newArrayList();
			amendmentRefIds.stream().forEach(id -> amendmentinvids.add(new ObjectId(id)));
			Criteria acriteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate).and("_id").in(amendmentinvids);	
			if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
				acriteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
			}
			applyFilterToCriteria(acriteria, filter);
			if(StringUtils.hasLength(searchVal)){
				acriteria = new Criteria().andOperator(acriteria, getSearchValueCriteria(searchVal));
			}
			
			MatchOperation matchOperation = Aggregation.match(acriteria);
			ProjectionOperation projectionOperation = getProjectionForTotal("yrCd");
			GroupOperation groupOperation = getGroupForTotal("yrCd");
			AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
			List<TotalInvoiceAmount> atotalAmounts = results.getMappedResults();
			if(atotalAmounts == null) {
				return totalInvAmount;
			}
			for(TotalInvoiceAmount amendmentInvoiceAmount : atotalAmounts){
				String acode = amendmentInvoiceAmount.get_id();
				for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
					String code = gstr1InvoiceAmount.get_id();
					if(acode.equals(code)) {
						gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(amendmentInvoiceAmount.getTotalTaxableAmount()));
						gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(amendmentInvoiceAmount.getTotalExemptedAmount()));
						gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(amendmentInvoiceAmount.getTotalTaxAmount()));
						gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(amendmentInvoiceAmount.getTotalAmount()));
						gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(amendmentInvoiceAmount.getTotalIGSTAmount()));
						gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(amendmentInvoiceAmount.getTotalCGSTAmount()));
						gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(amendmentInvoiceAmount.getTotalSGSTAmount()));
						gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(amendmentInvoiceAmount.getTotalCESSAmount()));
					}
				}
			}
			return  totalInvAmount;
		}
		
		return  totalInvAmount;
	}
	
	public List<TotalInvoiceAmount> getConsolidatedSummeryForGlobalYearMonthwise(List<String> clientids, String yearCode, boolean checkQuarterly,String dwnldFromGSTN,String gstrType,InvoiceFilter filter) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
				.andInclude("mthCd","tcstdsAmount");
		GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedSummeryForGlobalYearMonthwise(clientids,yearCode,checkQuarterly,dwnldFromGSTN,gstrType,filter);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
					gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
					gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
					gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
					gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
				}
			}
		}
		return  getCalculateSummeryForGlobalYearMonthwise(totalInvAmount, clientids, yearCode, checkQuarterly, dwnldFromGSTN, gstrType, filter);
		//return  totalInvAmount;
	}
	
	public List<TotalInvoiceAmount> getCalculateSummeryForGlobalYearMonthwise(List<TotalInvoiceAmount> totalInvAmount, List<String> clientids, String yearCode, boolean checkQuarterly,String dwnldFromGSTN,String gstrType,InvoiceFilter filter) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		applyFilterToCriteria(criteria, filter);
		Query query = Query.query(criteria);
		List<String> amendmentRefIds = mongoTemplate.getCollection(COLLECTION_NAME).distinct("amendmentRefId", query.getQueryObject());
		
		if(NullUtil.isNotEmpty(amendmentRefIds)){
			List<ObjectId> amendmentinvids = Lists.newArrayList();
			amendmentRefIds.stream().forEach(id -> amendmentinvids.add(new ObjectId(id)));
			Criteria acriteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode).and("_id").in(amendmentinvids);
			if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
				acriteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
			}
			applyFilterToCriteria(acriteria, filter);
			MatchOperation matchOperation = Aggregation.match(acriteria);
			ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
					.and("totaltax").multiply("sftr").as("totaltax")
					.and("totalamount").multiply("sftr").as("totalamount")
					.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
					.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
					.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
					.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
					.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
					.andInclude("mthCd","tcstdsAmount");
			GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
					.sum("totaltaxableamount").as("totalTaxableAmount")
					.sum("tcstdsAmount").as("tcsTdsAmount")
					.sum("totaltax").as("totalTaxAmount")
					.sum("totalamount").as("totalAmount")
					.sum("totalIgstAmount").as("totalIGSTAmount")
					.sum("totalCgstAmount").as("totalCGSTAmount")
					.sum("totalSgstAmount").as("totalSGSTAmount")
					.sum("totalCessAmount").as("totalCESSAmount")
					.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
			AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
			List<TotalInvoiceAmount> atotalInvAmount = results.getMappedResults();
			if(atotalInvAmount == null) {
				return  totalInvAmount;
			}
			for(TotalInvoiceAmount amendmentTotal : atotalInvAmount){
				String acode = amendmentTotal.get_id();
				for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
					String code = gstr1InvoiceAmount.get_id();
					if(acode.equals(code)) {
						gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(amendmentTotal.getTotalTaxableAmount()));
						gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(amendmentTotal.getTotalExemptedAmount()));
						gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(amendmentTotal.getTotalTaxAmount()));
						gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(amendmentTotal.getTotalAmount()));
						gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(amendmentTotal.getTotalIGSTAmount()));
						gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(amendmentTotal.getTotalCGSTAmount()));
						gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(amendmentTotal.getTotalSGSTAmount()));
						gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(amendmentTotal.getTotalCESSAmount()));
					}
				}
			}
		}
		return  totalInvAmount;
	}

public List<TotalInvoiceAmount> getConsolidatedSummeryForGlobalCustom(List<String> clientids, String returntype, Date stDate, Date endDate,String dwnldFromGSTN,String gstrType,InvoiceFilter filter) {
		//Criteria criteria = Criteria.where("clientid").in(clientids).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
	Criteria criteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate);		
	if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
				.andInclude("mthCd","tcstdsAmount");
		GroupOperation groupOperation = Aggregation.group("mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedSummeryForGlobalCustom(clientids,returntype,stDate,endDate,dwnldFromGSTN,gstrType,filter);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
			String cancode = gstr1CancelledInvoiceAmount.get_id();
			for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
				String code = gstr1InvoiceAmount.get_id();
				if(cancode.equals(code)) {
					gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
					gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
					gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
					gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
					gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
					gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
					gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
					gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
				}
			}
		}
		
		return getCalculateSummeryForGlobalCustom(totalInvAmount, clientids, returntype, stDate, endDate, dwnldFromGSTN, gstrType, filter);
		//return  totalInvAmount;
	}

	public List<TotalInvoiceAmount> getCalculateSummeryForGlobalCustom(List<TotalInvoiceAmount> totalInvAmount, List<String> clientids, String returntype, Date stDate, Date endDate,String dwnldFromGSTN,String gstrType,InvoiceFilter filter) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate);		
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		applyFilterToCriteria(criteria, filter);
		Query query = Query.query(criteria);
		List<String> amendmentRefIds = mongoTemplate.getCollection(COLLECTION_NAME).distinct("amendmentRefId", query.getQueryObject());
		
		if(NullUtil.isNotEmpty(amendmentRefIds)){
			List<ObjectId> amendmentinvids = Lists.newArrayList();
			amendmentRefIds.stream().forEach(id -> amendmentinvids.add(new ObjectId(id)));
			Criteria acriteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate).and("_id").in(amendmentinvids);
			if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
				acriteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
			}
			applyFilterToCriteria(acriteria, filter);
		
			MatchOperation matchOperation = Aggregation.match(acriteria);
			ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
					.and("totaltax").multiply("sftr").as("totaltax")
					.and("totalamount").multiply("sftr").as("totalamount")
					.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
					.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
					.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
					.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
					.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
					.andInclude("mthCd","tcstdsAmount");
			GroupOperation groupOperation = Aggregation.group("mthCd")
					.sum("totaltaxableamount").as("totalTaxableAmount")
					.sum("tcstdsAmount").as("tcsTdsAmount")
					.sum("totaltax").as("totalTaxAmount")
					.sum("totalamount").as("totalAmount")
					.sum("totalIgstAmount").as("totalIGSTAmount")
					.sum("totalCgstAmount").as("totalCGSTAmount")
					.sum("totalSgstAmount").as("totalSGSTAmount")
					.sum("totalCessAmount").as("totalCESSAmount")
					.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
			AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
			List<TotalInvoiceAmount> atotalInvAmount = results.getMappedResults();
			if(atotalInvAmount == null) {
				return totalInvAmount;				
			}
			for(TotalInvoiceAmount amendmentInvoiceAmount : atotalInvAmount){
				String cancode = amendmentInvoiceAmount.get_id();
				for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
					String code = gstr1InvoiceAmount.get_id();
					if(cancode.equals(code)) {
						gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(amendmentInvoiceAmount.getTotalTaxableAmount()));
						gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(amendmentInvoiceAmount.getTotalExemptedAmount()));
						gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(amendmentInvoiceAmount.getTotalTaxAmount()));
						gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(amendmentInvoiceAmount.getTotalAmount()));
						gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(amendmentInvoiceAmount.getTotalIGSTAmount()));
						gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(amendmentInvoiceAmount.getTotalCGSTAmount()));
						gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(amendmentInvoiceAmount.getTotalSGSTAmount()));
						gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(amendmentInvoiceAmount.getTotalCESSAmount()));
					}
				}
			}
		}
		return totalInvAmount;
	}
	
	public List<TotalInvoiceAmount> getConsolidatedSummeryForYearMonthwise(List<String> clientids, String yearCd, boolean checkQuarterly, String dwnldFromGSTN){
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCd);
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
			.and("totaltax").multiply("sftr").as("totaltax")
			.and("totalamount").multiply("sftr").as("totalamount")
			.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
			.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
			.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
			.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
			.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
			.andInclude("mthCd","tcstdsAmount");
	GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
			.sum("totaltaxableamount").as("totalTaxableAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("totaltax").as("totalTaxAmount")
			.sum("totalamount").as("totalAmount")
			.sum("totalIgstAmount").as("totalIGSTAmount")
			.sum("totalCgstAmount").as("totalCGSTAmount")
			.sum("totalSgstAmount").as("totalSGSTAmount")
			.sum("totalCessAmount").as("totalCESSAmount")
			.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return  results.getMappedResults();
}

	public List<String> getBilledToNames(List<String> clientids, int month, String yearCd,String dwnldFromGSTN) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCd);
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	public List<String> getGlobalCustomBillToNames(List<String> clientIds, Date stDate,Date endDate,String dwnldFromGSTN){
		Criteria criteria = Criteria.where("clientid").in(clientIds).andOperator(
				Criteria.where("dateofinvoice").gte(stDate),
				Criteria.where("dateofinvoice").lte(endDate)
		);
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	private void applyFilterToCriteriaForCancelled(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			List<String> allStatusList = Arrays.asList("CANCELLED");
			criteria.and("gstStatus").in(allStatusList);
			if(filter.getIrnStatus() != null){
				String fieldName =  "irnStatus";
				List<String> statusList = Arrays.asList(filter.getIrnStatus());
				if(statusList.contains("Not Generated") || statusList.contains(" ") || statusList.contains("Cancelled")){
					criteria = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where(fieldName).in(statusList), Criteria.where(fieldName).is(null)));	
				}else{
					criteria.and(fieldName).in(statusList);
				}
			}
			if(filter.getInvoiceType() != null){
				criteria.and("invtype").in(Arrays.asList(filter.getInvoiceType()));
			}
			if(filter.getUser() != null){
				criteria.and("fullname").in(Arrays.asList(filter.getUser()));
			}
			if(filter.getVendor() != null){
				criteria.and("billedtoname").in(Arrays.asList(filter.getVendor()));
			}
			if(filter.getBranch() != null){
				criteria.and("branch").in(Arrays.asList(filter.getBranch()));
			}
			if(filter.getVertical() != null){
				criteria.and("vertical").in(Arrays.asList(filter.getVertical()));
			}
			if(filter.getCustomFieldText1() != null){
				criteria.and("customField1").in(Arrays.asList(filter.getCustomFieldText1()));
			}
			if(filter.getCustomFieldText2() != null){
				criteria.and("customField2").in(Arrays.asList(filter.getCustomFieldText2()));
			}
			if(filter.getCustomFieldText3() != null){
				criteria.and("customField3").in(Arrays.asList(filter.getCustomFieldText3()));
			}
			if(filter.getCustomFieldText4() != null){
				criteria.and("customField4").in(Arrays.asList(filter.getCustomFieldText4()));
			}
			if(filter.getReverseCharge() != null){
				List<String> reverseChargeList = Arrays.asList(filter.getReverseCharge());
				if(reverseChargeList.contains("Regular")){
					criteria.andOperator(criteria,new Criteria().orOperator(Criteria.where("revchargetype").in(reverseChargeList), Criteria.where("revchargetype").is(null)));	
				}else{
					criteria.and("revchargetype").in(reverseChargeList);
				}
			}
			/*if(filter.getCustomFieldText1() != null){
				criteria.andOperator(criteria,new Criteria().orOperator(Criteria.where("customFieldText1").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText1").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText1").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText1").in(Arrays.asList(filter.getCustomFieldText1()))));	
			}*/
		}
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmountsForMonthCancelled(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter,String gstrType){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if(gstrType.equalsIgnoreCase("gstrOrEinvoice")) {
			applyEinvFilterToCriteriaForCancelled(criteria, filter);
		}else {
			applyFilterToCriteriaForCancelled(criteria, filter);
		}
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = null;
		GroupOperation groupOperation = null;
		if(month > 0) {
			projectionOperation = getProjectionForTotal("mthCd");
			groupOperation = getGroupForTotal("mthCd");
		}else {
			projectionOperation = getProjectionForTotal("yrCd");
			groupOperation = getGroupForTotal("yrCd");
		}
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmountsForMonthCancelledQuarterly(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter,String gstrType){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode);
		List<String> mnthcd = Lists.newArrayList();
		if(month == 1 || month == 2 || month == 3) {
			mnthcd = Arrays.asList(1+"",2+"",3+"");
		}else if(month == 4 || month == 5 || month == 6) {
			mnthcd = Arrays.asList(4+"",5+"",6+"");
		}else if(month == 7 || month == 8 || month == 9) {
			mnthcd = Arrays.asList(7+"",8+"",9+"");
		}else {
			mnthcd = Arrays.asList(10+"",11+"",12+"");
		}
		
		if(month > 0) {
			criteria.and("mthCd").in(mnthcd);
		}
		if(gstrType.equalsIgnoreCase("gstrOrEinvoice")) {
			applyEinvFilterToCriteriaForCancelled(criteria, filter);
		}else {
			applyFilterToCriteriaForCancelled(criteria, filter);
		}
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = null;
		GroupOperation groupOperation = null;
		if(month > 0) {
			projectionOperation = getProjectionForTotal("csftr");
			groupOperation = getGroupForTotal("csftr");
		}else {
			projectionOperation = getProjectionForTotal("yrCd");
			groupOperation = getGroupForTotal("yrCd");
		}
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	}
	
public TotalInvoiceAmount getCancelledTotalInvoicesAmountsForCustom(String clientid, Date stDate, Date endDate, String searchVal, InvoiceFilter filter){
		//Criteria criteria = Criteria.where("clientid").is(clientid).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
	Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate);	
	applyFilterToCriteriaForCancelled(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
		GroupOperation groupOperation = getGroupForTotal("csftr");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	}

public List<TotalInvoiceAmount> getCancelledTotalEinvoicesAmountsForCustom(String clientid, Date stDate, Date endDate, String searchVal, InvoiceFilter filter){
	//Criteria criteria = Criteria.where("clientid").is(clientid).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
	Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate);	
	applyEinvFilterToCriteriaForCancelled(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
	GroupOperation groupOperation = getGroupForTotal("csftr");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	List<TotalInvoiceAmount> tresults = results.getMappedResults();
	return tresults;
}

public TotalInvoiceAmount getCancelledTotalInvoicesAmountsForFp(String clientId, String fp, String searchVal, InvoiceFilter filter){
	Criteria criteria = Criteria.where("clientid").is(clientId).and("fp").is(fp);
	applyFilterToCriteriaForCancelled(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = getProjectionForTotal("fp");
	GroupOperation groupOperation = getGroupForTotal("fp");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return results.getUniqueMappedResult();
}

public TotalInvoiceAmount getCancelledTotalInvoicesAmountsForFpQuarterly(String clientId, int month,int year, String searchVal, InvoiceFilter filter){
	Criteria criteria = Criteria.where("clientid").is(clientId);
	List<String> mnthcd = Lists.newArrayList();
	if(month == 1 || month == 2 || month == 3) {
		mnthcd = Arrays.asList("0"+1+year,"0"+2+year,"0"+3+year);
	}else if(month == 4 || month == 5 || month == 6) {
		mnthcd = Arrays.asList("0"+4+year,"0"+5+year,"0"+6+year);
	}else if(month == 7 || month == 8 || month == 9) {
		mnthcd = Arrays.asList("0"+7+year,"0"+8+year,"0"+9+year);
	}else {
		mnthcd = Arrays.asList(10+""+year,11+""+year,12+""+year);
	}
	
	criteria.and("fp").in(mnthcd);
	applyFilterToCriteriaForCancelled(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
	GroupOperation groupOperation = getGroupForTotal("csftr");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return results.getUniqueMappedResult();
}

public List<TotalInvoiceAmount> getCancelledConsolidatedSummeryForYearMonthwise(final Client client, String yearCd, boolean checkQuarterly,String gstrType){
	Criteria criteria = null;
	if(gstrType.equals("gstrOrEinvoice")) {
		criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd).and("irnStatus").is("Cancelled");
	}else {
		criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd).and("gstStatus").is("CANCELLED");
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
			.and("totaltax").multiply("sftr").as("totaltax")
			.and("totalamount").multiply("sftr").as("totalamount")
			.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
			.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
			.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
			.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
			.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
			.andInclude("mthCd","tcstdsAmount");
	GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
			.sum("totaltaxableamount").as("totalTaxableAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("totaltax").as("totalTaxAmount")
			.sum("totalamount").as("totalAmount")
			.sum("totalIgstAmount").as("totalIGSTAmount")
			.sum("totalCgstAmount").as("totalCGSTAmount")
			.sum("totalSgstAmount").as("totalSGSTAmount")
			.sum("totalCessAmount").as("totalCESSAmount")
			.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return  results.getMappedResults();
}

public List<TotalInvoiceAmount> getCancelledConsolidatedSummeryForCustom(Client client, String returntype, Date stDate, Date endDate,String gstrType) {
	Criteria criteria = null;
	if(gstrType.equals("gstrOrEinvoice")) {
		//criteria = Criteria.where("clientid").is(client.getId().toString()).and("irnStatus").is("Cancelled").andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		criteria = Criteria.where("clientid").is(client.getId().toString()).and("irnStatus").is("Cancelled").and("dateofinvoice").gte(stDate).lte(endDate);	
	}else {
		//criteria = Criteria.where("clientid").is(client.getId().toString()).and("gstStatus").is("CANCELLED").andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		criteria = Criteria.where("clientid").is(client.getId().toString()).and("gstStatus").is("CANCELLED").and("dateofinvoice").gte(stDate).lte(endDate);	
	}
			
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
			.and("totaltax").multiply("sftr").as("totaltax")
			.and("totalamount").multiply("sftr").as("totalamount")
			.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
			.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
			.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
			.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
			.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
			.andInclude("mthCd","tcstdsAmount");
	GroupOperation groupOperation = Aggregation.group("mthCd")
			.sum("totaltaxableamount").as("totalTaxableAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("totaltax").as("totalTaxAmount")
			.sum("totalamount").as("totalAmount")
			.sum("totalIgstAmount").as("totalIGSTAmount")
			.sum("totalCgstAmount").as("totalCGSTAmount")
			.sum("totalSgstAmount").as("totalSGSTAmount")
			.sum("totalCessAmount").as("totalCESSAmount")
			.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return  results.getMappedResults();
}

public TotalInvoiceAmount getMultimonthTotalCancelledInvoicesAmounts(String clientid, List<String> invTypes, String yearCode,String searchVal, InvoiceFilter filter){
	Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
	if(filter.getInvoiceType() == null){
		criteria.and("invtype").in(invTypes);
	}
	applyFilterToCriteriaForCancelled(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = getProjectionForTotal("yrCd");
	GroupOperation groupOperation = getGroupForTotal("yrCd");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return results.getUniqueMappedResult();
}

public List<TotalInvoiceAmount> getCancelledConsolidatedMultimonthSummeryForYearMonthwise(Client client,List<String> invTypes, String yearCd){
	Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd).and("gstStatus").is("CANCELLED")
			.and("invtype").in(invTypes).and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
			.and("totaltax").multiply("sftr").as("totaltax")
			.and("totalamount").multiply("sftr").as("totalamount")
			.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
			.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
			.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
			.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
			.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
			.andInclude("mthCd","tcstdsAmount");
	GroupOperation groupOperation = Aggregation.group("mthCd")
			.sum("totaltaxableamount").as("totalTaxableAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("totaltax").as("totalTaxAmount")
			.sum("totalamount").as("totalAmount")
			.sum("totalIgstAmount").as("totalIGSTAmount")
			.sum("totalCgstAmount").as("totalCGSTAmount")
			.sum("totalSgstAmount").as("totalSGSTAmount")
			.sum("totalCessAmount").as("totalCESSAmount")
			.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return  results.getMappedResults();
}

public TotalInvoiceAmount getCancelledTotalInvoicesAmountsForMonth(List<String> clientids, int month, String yearCode,String searchVal, InvoiceFilter filter,String dwnldFromGSTN,String gstrType) {
	Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
	if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
		criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
	}
	if(month > 0) {
		criteria.and("mthCd").is(month+"");
	}
	if(gstrType.equals("gstrOrEinvoice")) {
		applyEinvFilterToCriteriaForCancelled(criteria, filter);
	}else {
		applyFilterToCriteriaForCancelled(criteria, filter);
	}
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = null;
	GroupOperation groupOperation = null;
	if(month > 0) {
		projectionOperation = getProjectionForTotal("mthCd");
		groupOperation = getGroupForTotal("mthCd");
	}else {
		projectionOperation = getProjectionForTotal("yrCd");
		groupOperation = getGroupForTotal("yrCd");
	}
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return results.getUniqueMappedResult();
}

public List<TotalInvoiceAmount> getCancelledTotalInvoicesAmountsForCustom(List<String> clientids, Date stDate, Date endDate,String searchVal, InvoiceFilter filter,String dwnldFromGSTN,String gstrType) {
	//Criteria criteria = Criteria.where("clientid").in(clientids).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
	Criteria criteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate);	
	if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
		criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
	}
	if(gstrType.equalsIgnoreCase("gstrOrEinvoice")) {
		applyEinvFilterToCriteriaForCancelled(criteria, filter);
	}else {
		applyFilterToCriteriaForCancelled(criteria, filter);
	}
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
	GroupOperation groupOperation = getGroupForTotal("csftr");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	List<TotalInvoiceAmount> tresults = results.getMappedResults();
	return tresults;
}

public List<TotalInvoiceAmount> getCancelledConsolidatedSummeryForGlobalYearMonthwise(List<String> clientids, String yearCode, boolean checkQuarterly,String dwnldFromGSTN,String gstrType,InvoiceFilter filter) {
	Criteria criteria = null;
	/*if(gstrType.equalsIgnoreCase("gstrOrEinvoice")){
		//criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode).and("irnStatus").is("Cancelled");
		criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
	}else {
		//criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode).and("gstStatus").is("CANCELLED");
		criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
	}*/
	criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
	if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
		criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
	}
	if(gstrType.equalsIgnoreCase("gstrOrEinvoice")) {
		applyEinvFilterToCriteriaForCancelled(criteria, filter);
	}else {
		applyFilterToCriteriaForCancelled(criteria, filter);
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
			.and("totaltax").multiply("sftr").as("totaltax")
			.and("totalamount").multiply("sftr").as("totalamount")
			.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
			.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
			.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
			.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
			.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
			.andInclude("mthCd","tcstdsAmount");
	GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
			.sum("totaltaxableamount").as("totalTaxableAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("totaltax").as("totalTaxAmount")
			.sum("totalamount").as("totalAmount")
			.sum("totalIgstAmount").as("totalIGSTAmount")
			.sum("totalCgstAmount").as("totalCGSTAmount")
			.sum("totalSgstAmount").as("totalSGSTAmount")
			.sum("totalCessAmount").as("totalCESSAmount")
			.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return  results.getMappedResults();
}

public List<TotalInvoiceAmount> getCancelledConsolidatedSummeryForGlobalCustom(List<String> clientids, String returntype, Date stDate, Date endDate,String dwnldFromGSTN,String gstrType,InvoiceFilter filter) {
	Criteria criteria = null;
	/*if(gstrType.equalsIgnoreCase("gstrOrEinvoice")) {
		criteria = Criteria.where("clientid").in(clientids).and("irnStatus").is("Cancelled").and("dateofinvoice").gte(stDate).lte(endDate);	
		//criteria = Criteria.where("clientid").in(clientids).and("irnStatus").is("Cancelled").andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
	}else {
		criteria = Criteria.where("clientid").in(clientids).and("gstStatus").is("CANCELLED").and("dateofinvoice").gte(stDate).lte(endDate);	
		//criteria = Criteria.where("clientid").in(clientids).and("gstStatus").is("CANCELLED").andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
	}*/
	criteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate);
	if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
		criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
	}
	if(gstrType.equalsIgnoreCase("gstrOrEinvoice")) {
		applyEinvFilterToCriteriaForCancelled(criteria, filter);
	}else {
		applyFilterToCriteriaForCancelled(criteria, filter);
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
			.and("totaltax").multiply("sftr").as("totaltax")
			.and("totalamount").multiply("sftr").as("totalamount")
			.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
			.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
			.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
			.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
			.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
			.andInclude("mthCd","tcstdsAmount");
	GroupOperation groupOperation = Aggregation.group("mthCd")
			.sum("totaltaxableamount").as("totalTaxableAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("totaltax").as("totalTaxAmount")
			.sum("totalamount").as("totalAmount")
			.sum("totalIgstAmount").as("totalIGSTAmount")
			.sum("totalCgstAmount").as("totalCGSTAmount")
			.sum("totalSgstAmount").as("totalSGSTAmount")
			.sum("totalCessAmount").as("totalCESSAmount")
			.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return  results.getMappedResults();
}

private void applyEinvFilterToCriteriaForCancelled(Criteria criteria, InvoiceFilter filter){
	if(filter != null){
		List<String> allStatusList = Arrays.asList("Cancelled");
		criteria.and("irnStatus").in(allStatusList);
		if(filter.getInvoiceType() != null){
			criteria.and("invtype").in(Arrays.asList(filter.getInvoiceType()));
		}
		if(filter.getUser() != null){
			criteria.and("fullname").in(Arrays.asList(filter.getUser()));
		}
		if(filter.getVendor() != null){
			criteria.and("billedtoname").in(Arrays.asList(filter.getVendor()));
		}
		if(filter.getBranch() != null){
			criteria.and("branch").in(Arrays.asList(filter.getBranch()));
		}
		if(filter.getVertical() != null){
			criteria.and("vertical").in(Arrays.asList(filter.getVertical()));
		}
		if(filter.getCustomFieldText1() != null){
			criteria.and("customField1").in(Arrays.asList(filter.getCustomFieldText1()));
		}
		if(filter.getCustomFieldText2() != null){
			criteria.and("customField2").in(Arrays.asList(filter.getCustomFieldText2()));
		}
		if(filter.getCustomFieldText3() != null){
			criteria.and("customField3").in(Arrays.asList(filter.getCustomFieldText3()));
		}
		if(filter.getCustomFieldText4() != null){
			criteria.and("customField4").in(Arrays.asList(filter.getCustomFieldText4()));
		}
		if(filter.getReverseCharge() != null){
			List<String> reverseChargeList = Arrays.asList(filter.getReverseCharge());
			if(reverseChargeList.contains("Regular")){
				criteria.andOperator(criteria,new Criteria().orOperator(Criteria.where("revchargetype").in(reverseChargeList), Criteria.where("revchargetype").is(null)));	
			}else{
				criteria.and("revchargetype").in(reverseChargeList);
			}
		}
		/*if(filter.getCustomFieldText1() != null){
			criteria.andOperator(criteria,new Criteria().orOperator(Criteria.where("customFieldText1").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText1").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText1").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText1").in(Arrays.asList(filter.getCustomFieldText1()))));	
		}*/
	}
}
public Page<GSTR1> findByClientidInAndMonthAndYear(List<String> clientids, List<String> invTypes, int month, String yearCode, int start, int length, String searchVal, String booksorReturns,InvoiceFilter filter) {
	Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
		//	.and("yrCd").is(yearCode).and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
	if(booksorReturns.equalsIgnoreCase("dwnldfromgstn")) {
		criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
	}
	if(filter.getInvoiceType() == null){
		criteria.and("invtype").in(invTypes);
	}
	if(month > 0) {
		criteria.and("mthCd").is(month+"");
	}
	applyFilterToCriteria(criteria, filter);
	if(StringUtils.hasLength(searchVal)){					
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	Query query = Query.query(criteria);
	if(start == 0 && length == 0) {
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));
	}else {
		addAllInvoicesQueryFirlds(query);
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
}

public Page<GSTR1> findByClientidAndInvtypeInAndMonthAndYear(final String clientid,final List<String> invTypes, int month, String yearCode, int start, int length, String searchVal, InvoiceFilter filter){
	Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode);
	if(month > 0) {
		criteria.and("mthCd").is(month+"");
	}
	if(filter.getInvoiceType() == null){
		criteria.and("invtype").in(invTypes);
	}
	applyFilterToCriteria(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	Query query = Query.query(criteria);
	if(start == 0 && length == 0){
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));
	}else {
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
}

public TotalInvoiceAmount getEinvoiceTotalInvoicesAmountsForMonth(String clientId,final List<String> invTypes, int month, String yearCode, String searchVal, InvoiceFilter filter,String gstrType){
	Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode);
	if(month > 0) {
		criteria.and("mthCd").is(month+"");
	}
	if(filter.getInvoiceType() == null){
		criteria.and("invtype").in(invTypes);
	}
	applyFilterToCriteria(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = null;
	GroupOperation groupOperation = null;
	if(month > 0) {
		projectionOperation = getProjectionForTotal("mthCd");
		groupOperation = getGroupForTotal("mthCd");
	}else {
		projectionOperation = getProjectionForTotal("yrCd");
		groupOperation = getGroupForTotal("yrCd");
	}
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	TotalInvoiceAmount cancelInvAmount = new TotalInvoiceAmount();
	if(filter != null) {
			cancelInvAmount = getTotalEInvoicesAmountsForMonthCancelled(clientId,invTypes,month,yearCode,searchVal,filter,gstrType);
			if(gstrType.equalsIgnoreCase("gstrOrEinvoice")) {
				if(filter.getIrnStatus() != null && filter.getIrnStatus().length == 1) {
					List<String> statusList = Arrays.asList(filter.getIrnStatus());
					if(statusList.contains("Cancelled")) {
						return cancelInvAmount;
					}else {
						cancelInvAmount = new TotalInvoiceAmount();
					}
				}
			}else {
				if(filter.getPaymentStatus() != null && filter.getPaymentStatus().length == 1) {
					List<String> statusList = Arrays.asList(filter.getPaymentStatus());
					if(statusList.contains("CANCELLED")) {
						return cancelInvAmount;
					}
				}
			}
		}
		
	TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
	TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
	if(totalnvAmount != null && cancelInvAmount != null) {
		totalInvAmount.setTotalTransactions(totalnvAmount.getTotalTransactions());
		totalInvAmount.setTotalTaxableAmount(totalnvAmount.getTotalTaxableAmount().subtract(cancelInvAmount.getTotalTaxableAmount()));
		totalInvAmount.setTotalExemptedAmount(totalnvAmount.getTotalExemptedAmount().subtract(cancelInvAmount.getTotalExemptedAmount()));
		totalInvAmount.setTotalTaxAmount(totalnvAmount.getTotalTaxAmount().subtract(cancelInvAmount.getTotalTaxAmount()));
		totalInvAmount.setTotalAmount(totalnvAmount.getTotalAmount().subtract(cancelInvAmount.getTotalAmount()));
		totalInvAmount.setTotalIGSTAmount(totalnvAmount.getTotalIGSTAmount().subtract(cancelInvAmount.getTotalIGSTAmount()));
		totalInvAmount.setTotalCGSTAmount(totalnvAmount.getTotalCGSTAmount().subtract(cancelInvAmount.getTotalCGSTAmount()));
		totalInvAmount.setTotalSGSTAmount(totalnvAmount.getTotalSGSTAmount().subtract(cancelInvAmount.getTotalSGSTAmount()));
		totalInvAmount.setTotalCESSAmount(totalnvAmount.getTotalCESSAmount().subtract(cancelInvAmount.getTotalCESSAmount()));
		return totalInvAmount;
	}else {
		return totalnvAmount;
	}
}

public Page<GSTR1> findByClientidAndFromtimeAndTotimeEinvoice(final String clientid,final List<String> invTypes, Date stDate, Date endDate, int start, int length, String searchVal, InvoiceFilter filter){
	//Criteria criteria = Criteria.where("clientid").is(clientid).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
	Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate);	
	if(filter.getInvoiceType() == null){
		criteria.and("invtype").in(invTypes);
	}
	applyFilterToCriteria(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	Query query = Query.query(criteria);
	
	if(start == 0 && length == 0) {
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));			
	}else {
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
}

public List<TotalInvoiceAmount> getConsolidatedSummeryForYearMonthwiseEinvoice(final Client client,final List<String> invTypes, String yearCd, boolean checkQuarterly,String gstrType, InvoiceFilter filter){
	Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd);
	if(filter.getInvoiceType() == null){
		criteria.and("invtype").in(invTypes);
	}
	applyFilterToCriteria(criteria, filter);
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
			.and("totaltax").multiply("sftr").as("totaltax")
			.and("totalamount").multiply("sftr").as("totalamount")
			.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
			.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
			.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
			.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
			.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
			.andInclude("mthCd","tcstdsAmount");
	GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
			.sum("totaltaxableamount").as("totalTaxableAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("totaltax").as("totalTaxAmount")
			.sum("totalamount").as("totalAmount")
			.sum("totalIgstAmount").as("totalIGSTAmount")
			.sum("totalCgstAmount").as("totalCGSTAmount")
			.sum("totalSgstAmount").as("totalSGSTAmount")
			.sum("totalCessAmount").as("totalCESSAmount")
			.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");		
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedSummeryForYearMonthwise(client,yearCd,checkQuarterly,gstrType);
	List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
	for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
		String cancode = gstr1CancelledInvoiceAmount.get_id();
		for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
			String code = gstr1InvoiceAmount.get_id();
			if(cancode.equals(code)) {
				gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
				gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
				gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
				gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
				gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
				gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
				gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
				gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
			}
		}
	}
	return  totalInvAmount;
}
public List<TotalInvoiceAmount> getConsolidatedSummeryForCustomEinvoice(Client client,final List<String> invTypes, String returntype, Date stDate, Date endDate,InvoiceFilter filter,String gstrType) {
	//Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
	Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("dateofinvoice").gte(stDate).lte(endDate);	
	if(filter.getInvoiceType() == null){
		criteria.and("invtype").in(invTypes);
	}
	applyFilterToCriteria(criteria, filter);
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
			.and("totaltax").multiply("sftr").as("totaltax")
			.and("totalamount").multiply("sftr").as("totalamount")
			.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
			.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
			.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
			.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
			.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
			.andInclude("mthCd","tcstdsAmount");
	GroupOperation groupOperation = Aggregation.group("mthCd")
			.sum("totaltaxableamount").as("totalTaxableAmount")
			.sum("tcstdsAmount").as("tcsTdsAmount")
			.sum("totaltax").as("totalTaxAmount")
			.sum("totalamount").as("totalAmount")
			.sum("totalIgstAmount").as("totalIGSTAmount")
			.sum("totalCgstAmount").as("totalCGSTAmount")
			.sum("totalSgstAmount").as("totalSGSTAmount")
			.sum("totalCessAmount").as("totalCESSAmount")
			.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedSummeryForCustom(client,returntype,stDate,endDate,gstrType);
	List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
	for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalCanInvAmount){
		String cancode = gstr1CancelledInvoiceAmount.get_id();
		for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
			String code = gstr1InvoiceAmount.get_id();
			if(cancode.equals(code)) {
				gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
				gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
				gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
				gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(gstr1CancelledInvoiceAmount.getTotalAmount()));
				gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
				gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
				gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
				gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
			}
		}
	}
	return  totalInvAmount;
}

public TotalInvoiceAmount getTotalEInvoicesAmountsForMonthCancelled(String clientId,final List<String> invTypes, int month, String yearCode, String searchVal, InvoiceFilter filter,String gstrType){
	Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode);
	if(month > 0) {
		criteria.and("mthCd").is(month+"");
	}
	if(filter.getInvoiceType() == null){
		criteria.and("invtype").in(invTypes);
	}
	applyEinvFilterToCriteriaForCancelled(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = null;
	GroupOperation groupOperation = null;
	if(month > 0) {
		projectionOperation = getProjectionForTotal("mthCd");
		groupOperation = getGroupForTotal("mthCd");
	}else {
		projectionOperation = getProjectionForTotal("yrCd");
		groupOperation = getGroupForTotal("yrCd");
	}
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return results.getUniqueMappedResult();
}

public Page<GSTR1> findByClientidAndInvtypeInAndMonthAndYearWithSort(final String clientid,final List<String> invTypes, int month, String yearCode, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter){
	Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode);
	if(month > 0) {
		criteria.and("mthCd").is(month+"");
	}
	if(filter.getInvoiceType() == null){
		criteria.and("invtype").in(invTypes);
	}
	applyFilterToCriteria(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	Query query = Query.query(criteria);
	if(start == 0 && length == 0){
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));
	}else {
		if(length != -1) {
			addAllInvoicesQueryFirlds(query);			
		}
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Sort sort = null;
		if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}
		Pageable pageable = new PageRequest((start/length), length,sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR1.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR1>(Collections.<GSTR1> emptyList());
		}
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME), pageable, total);
	}
}

public Page<GSTR1> findByClientidAndInvtypeInAndMonthAndYearDeleteEinv(final String clientid,final List<String> invTypes, int month, String yearCode, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter){
	Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("irnNo").in("", null);
	if(month > 0) {
		criteria.and("mthCd").is(month+"");
	}
	if(filter.getInvoiceType() == null){
		criteria.and("invtype").in(invTypes);
	}
	List<Criteria> criterias = new ArrayList<Criteria>();
	List<String> allStatusList = Lists.newArrayList();
	allStatusList.add("Generated");
	allStatusList.add("Cancelled");
	Criteria criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("irnStatus").nin(allStatusList), Criteria.where("irnStatus").is(""),Criteria.where("irnStatus").is(null)));
	if(NullUtil.isNotEmpty(criteriaa)) {
		criterias.add(criteriaa);
	}
	if(NullUtil.isNotEmpty(criterias)) {
		criteria.andOperator(criterias.toArray(new Criteria[criterias.size()]));				
	}
	
	Query query = Query.query(criteria);
	if(start == 0 && length == 0){
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));
	}else {
		if(length != -1) {
			addAllInvoicesQueryFirlds(query);			
		}
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		Sort sort = null;
		if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}
		Pageable pageable = new PageRequest((start/length), length,sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR1.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR1>(Collections.<GSTR1> emptyList());
		}
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME), pageable, total);
	}
}

public Page<GSTR1> findByClientidAndInvtypeInAndMonthAndYearEINV(final String clientid,final List<String> invTypes, int month, String yearCode){
	List<String> irnstatus = new ArrayList<String>();
	irnstatus.add("Not Generated");
	irnstatus.add("Duplicate IRN");
	Criteria criteria = Criteria.where("clientid").is(clientid).and("invtype").in(invTypes).and("yrCd").is(yearCode);
	if(month > 0) {
		criteria.and("mthCd").is(month+"");
	}	
	List<Criteria> criterias = new ArrayList<Criteria>();
	Criteria criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("irnStatus").in(irnstatus), Criteria.where("irnStatus").is(""),Criteria.where("irnStatus").is(null)));
	if(NullUtil.isNotEmpty(criteriaa)) {
		criterias.add(criteriaa);
	}
	if(NullUtil.isNotEmpty(criterias)) {
		criteria.andOperator(criterias.toArray(new Criteria[criterias.size()]));				
	}
	Query query = Query.query(criteria);
	return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));
	
}


public TotalInvoiceAmount getTotalInvoicesAmountsForMonthQuarterly(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter,String gstrType){
	Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode);
	List<String> mnthcd = Lists.newArrayList();
	if(month == 1 || month == 2 || month == 3) {
		mnthcd = Arrays.asList(1+"",2+"",3+"");
	}else if(month == 4 || month == 5 || month == 6) {
		mnthcd = Arrays.asList(4+"",5+"",6+"");
	}else if(month == 7 || month == 8 || month == 9) {
		mnthcd = Arrays.asList(7+"",8+"",9+"");
	}else {
		mnthcd = Arrays.asList(10+"",11+"",12+"");
	}
	
	if(month > 0) {
		criteria.and("mthCd").in(mnthcd);
	}
	applyFilterToCriteria(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = null;
	GroupOperation groupOperation = null;
	if(month > 0) {
		projectionOperation = getProjectionForTotal("csftr");
		groupOperation = getGroupForTotal("csftr");
	}else {
		projectionOperation = getProjectionForTotal("yrCd");
		groupOperation = getGroupForTotal("yrCd");
	}
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	TotalInvoiceAmount cancelInvAmount = new TotalInvoiceAmount();
	if(filter != null) {
			cancelInvAmount = getTotalInvoicesAmountsForMonthCancelledQuarterly(clientId,month,yearCode,searchVal,filter,gstrType);
			if(gstrType.equalsIgnoreCase("gstrOrEinvoice")) {
				if(filter.getIrnStatus() != null && filter.getIrnStatus().length == 1) {
					List<String> statusList = Arrays.asList(filter.getIrnStatus());
					if(statusList.contains("Cancelled")) {
						return cancelInvAmount;
					}
				}
			}else {
				if(filter.getPaymentStatus() != null && filter.getPaymentStatus().length == 1) {
					List<String> statusList = Arrays.asList(filter.getPaymentStatus());
					if(statusList.contains("CANCELLED")) {
						return cancelInvAmount;
					}
				}
			}
		}
		
	TotalInvoiceAmount totalnvAmount = results.getUniqueMappedResult();
	TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
	if(totalnvAmount != null && cancelInvAmount != null) {
		totalInvAmount.setTotalTransactions(totalnvAmount.getTotalTransactions());
		totalInvAmount.setTotalTaxableAmount(totalnvAmount.getTotalTaxableAmount().subtract(cancelInvAmount.getTotalTaxableAmount()));
		totalInvAmount.setTotalExemptedAmount(totalnvAmount.getTotalExemptedAmount().subtract(cancelInvAmount.getTotalExemptedAmount()));
		totalInvAmount.setTotalTaxAmount(totalnvAmount.getTotalTaxAmount().subtract(cancelInvAmount.getTotalTaxAmount()));
		totalInvAmount.setTotalAmount(totalnvAmount.getTotalAmount().subtract(cancelInvAmount.getTotalAmount()));
		totalInvAmount.setTotalIGSTAmount(totalnvAmount.getTotalIGSTAmount().subtract(cancelInvAmount.getTotalIGSTAmount()));
		totalInvAmount.setTotalCGSTAmount(totalnvAmount.getTotalCGSTAmount().subtract(cancelInvAmount.getTotalCGSTAmount()));
		totalInvAmount.setTotalSGSTAmount(totalnvAmount.getTotalSGSTAmount().subtract(cancelInvAmount.getTotalSGSTAmount()));
		totalInvAmount.setTotalCESSAmount(totalnvAmount.getTotalCESSAmount().subtract(cancelInvAmount.getTotalCESSAmount()));
		totalInvAmount.setTcsTdsAmount(totalnvAmount.getTcsTdsAmount().subtract(cancelInvAmount.getTcsTdsAmount()));
		return getAmendmentsTotalInvoicesAmountsForMonthQuarterly(totalInvAmount, clientId, month, yearCode, searchVal, filter, gstrType);
		//return totalInvAmount;
	}else {
		return getAmendmentsTotalInvoicesAmountsForMonthQuarterly(totalnvAmount, clientId, month, yearCode, searchVal, filter, gstrType);
		//return totalnvAmount;
	}
}

public TotalInvoiceAmount getAmendmentsTotalInvoicesAmountsForMonthQuarterly(TotalInvoiceAmount totalInvAmount, String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter,String gstrType){
	if(totalInvAmount == null) {
		return totalInvAmount;
	}
	Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode);
	List<String> mnthcd = Lists.newArrayList();
	if(month == 1 || month == 2 || month == 3) {
		mnthcd = Arrays.asList(1+"",2+"",3+"");
	}else if(month == 4 || month == 5 || month == 6) {
		mnthcd = Arrays.asList(4+"",5+"",6+"");
	}else if(month == 7 || month == 8 || month == 9) {
		mnthcd = Arrays.asList(7+"",8+"",9+"");
	}else {
		mnthcd = Arrays.asList(10+"",11+"",12+"");
	}
	
	if(month > 0) {
		criteria.and("mthCd").in(mnthcd);
	}
	applyFilterToCriteria(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}
	Query query = Query.query(criteria);
	
	List<String> amendmentRefIds = mongoTemplate.getCollection(COLLECTION_NAME).distinct("amendmentRefId", query.getQueryObject());
	
	if(NullUtil.isNotEmpty(amendmentRefIds)){
		List<ObjectId> amendmentinvids = Lists.newArrayList();
		amendmentRefIds.stream().forEach(id -> amendmentinvids.add(new ObjectId(id)));
		if(NullUtil.isNotEmpty(amendmentinvids)) {
			criteria.and("_id").in(amendmentinvids);
		}
	
	
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = null;
	GroupOperation groupOperation = null;
	if(month > 0) {
		projectionOperation = getProjectionForTotal("csftr");
		groupOperation = getGroupForTotal("csftr");
	}else {
		projectionOperation = getProjectionForTotal("yrCd");
		groupOperation = getGroupForTotal("yrCd");
	}
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	
	TotalInvoiceAmount atotalnvAmount = results.getUniqueMappedResult();
	
		if(atotalnvAmount != null) {
			totalInvAmount.setTotalTransactions(totalInvAmount.getTotalTransactions());
			totalInvAmount.setTotalTaxableAmount(totalInvAmount.getTotalTaxableAmount().subtract(atotalnvAmount.getTotalTaxableAmount()));
			totalInvAmount.setTotalExemptedAmount(totalInvAmount.getTotalExemptedAmount().subtract(atotalnvAmount.getTotalExemptedAmount()));
			totalInvAmount.setTotalTaxAmount(totalInvAmount.getTotalTaxAmount().subtract(atotalnvAmount.getTotalTaxAmount()));
			totalInvAmount.setTotalAmount(totalInvAmount.getTotalAmount().subtract(atotalnvAmount.getTotalAmount()));
			totalInvAmount.setTotalIGSTAmount(totalInvAmount.getTotalIGSTAmount().subtract(atotalnvAmount.getTotalIGSTAmount()));
			totalInvAmount.setTotalCGSTAmount(totalInvAmount.getTotalCGSTAmount().subtract(atotalnvAmount.getTotalCGSTAmount()));
			totalInvAmount.setTotalSGSTAmount(totalInvAmount.getTotalSGSTAmount().subtract(atotalnvAmount.getTotalSGSTAmount()));
			totalInvAmount.setTotalCESSAmount(totalInvAmount.getTotalCESSAmount().subtract(atotalnvAmount.getTotalCESSAmount()));
			totalInvAmount.setTcsTdsAmount(totalInvAmount.getTcsTdsAmount().subtract(atotalnvAmount.getTcsTdsAmount()));
			
			return totalInvAmount;
		}
	}
	return totalInvAmount;
}


public TotalInvoiceAmount getTotalInvoicesAmountsForMonthPreviousMonthPendingInvoices(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter, InvoiceFilter filter1){
	Criteria criteria = Criteria.where("clientid").is(clientId);
	String yrcd[] = yearCode.split("-");
	String yr = yrcd[0];
	int iyr = Integer.parseInt(yr);
	List<String> yearcodes = Lists.newArrayList();
	for(int i=2017;i<iyr;i++) {
		String yearcode = i+""+"-"+(i+1)+"";
		yearcodes.add(yearcode);
	}
	List<String> mnthcd = Lists.newArrayList();
	if(month == 1) {
		mnthcd = Arrays.asList(4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
	}else if(month == 2) {
		mnthcd = Arrays.asList(1+"",4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
	}else if(month == 3) {
		mnthcd = Arrays.asList(1+"",2+"",4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
	}else {
		for(int i=4;i<month;i++) {
			mnthcd.add(i+"");
		}
	}
	Criteria crt1 = Criteria.where("yrCd").is(yearCode).and("mthCd").in(mnthcd);
	Criteria crt = new Criteria().orOperator(Criteria.where("yrCd").in(yearcodes),crt1);
	applyFilterToCriteria(criteria, filter);
	criteria = new Criteria().andOperator(criteria, crt);
	Criteria crt3 = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode).and("mthCd").is(month+"");
	applyFilterToCriteria(crt3, filter1);
	Criteria crt2 = new Criteria().orOperator(crt3,criteria);
	if(StringUtils.hasLength(searchVal)){
		crt2 = new Criteria().andOperator(crt2, getSearchValueCriteria(searchVal));
	}
	MatchOperation matchOperation = Aggregation.match(crt2);
	ProjectionOperation projectionOperation = null;
	GroupOperation groupOperation = null;
	projectionOperation = getProjectionForTotal("csftr");
	groupOperation = getGroupForTotal("csftr");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	
	if(results != null) {
		Query query = Query.query(crt2);
		TotalInvoiceAmount totalInvAmount = results.getUniqueMappedResult();
		List<String> amendmentRefIds = mongoTemplate.getCollection(COLLECTION_NAME).distinct("amendmentRefId", query.getQueryObject());
		
		if(NullUtil.isNotEmpty(amendmentRefIds)){
			List<ObjectId> amendmentinvids = Lists.newArrayList();
			amendmentRefIds.stream().forEach(id -> amendmentinvids.add(new ObjectId(id)));
			if(NullUtil.isNotEmpty(amendmentinvids)) {
				crt2.and("_id").in(amendmentinvids);
			}
			MatchOperation aMatchOperation = Aggregation.match(crt2);
			ProjectionOperation aProjectionOperation = null;
			GroupOperation aGroupOperation = null;
			projectionOperation = getProjectionForTotal("csftr");
			groupOperation = getGroupForTotal("csftr");
			AggregationResults<TotalInvoiceAmount> amedmentResults = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
			if(NullUtil.isNotEmpty(amedmentResults)) {
				TotalInvoiceAmount atotalInvAmount = amedmentResults.getUniqueMappedResult();
				if(NullUtil.isNotEmpty(atotalInvAmount)) {
					totalInvAmount.setTotalTaxableAmount(totalInvAmount.getTotalTaxableAmount().subtract(atotalInvAmount.getTotalTaxableAmount()));
					totalInvAmount.setTotalExemptedAmount(totalInvAmount.getTotalExemptedAmount().subtract(atotalInvAmount.getTotalExemptedAmount()));
					totalInvAmount.setTotalTaxAmount(totalInvAmount.getTotalTaxAmount().subtract(atotalInvAmount.getTotalTaxAmount()));
					totalInvAmount.setTotalAmount(totalInvAmount.getTotalAmount().subtract(atotalInvAmount.getTotalAmount()));
					totalInvAmount.setTotalIGSTAmount(totalInvAmount.getTotalIGSTAmount().subtract(atotalInvAmount.getTotalIGSTAmount()));
					totalInvAmount.setTotalCGSTAmount(totalInvAmount.getTotalCGSTAmount().subtract(atotalInvAmount.getTotalCGSTAmount()));
					totalInvAmount.setTotalSGSTAmount(totalInvAmount.getTotalSGSTAmount().subtract(atotalInvAmount.getTotalSGSTAmount()));
					totalInvAmount.setTotalCESSAmount(totalInvAmount.getTotalCESSAmount().subtract(atotalInvAmount.getTotalCESSAmount()));
					totalInvAmount.setTcsTdsAmount(totalInvAmount.getTcsTdsAmount().subtract(atotalInvAmount.getTcsTdsAmount()));
				}
			}
			return totalInvAmount;
		}
	}
	return results.getUniqueMappedResult();
}

public Page<GSTR1> findByClientidAndMonthAndYearPrevMonthPendingInvoices(final String clientid, int month, String yearCode, int start, int length, String searchVal, InvoiceFilter filter, InvoiceFilter filter1){
	Criteria criteria = Criteria.where("clientid").is(clientid);
	String yrcd[] = yearCode.split("-");
	String yr = yrcd[0];
	int iyr = Integer.parseInt(yr);
	List<String> yearcodes = Lists.newArrayList();
	for(int i=2017;i<iyr;i++) {
		String yearcode = i+""+"-"+(i+1)+"";
		yearcodes.add(yearcode);
	}
	List<String> mnthcd = Lists.newArrayList();
	if(month == 1) {
		mnthcd = Arrays.asList(4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
	}else if(month == 2) {
		mnthcd = Arrays.asList(1+"",4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
	}else if(month == 3) {
		mnthcd = Arrays.asList(1+"",2+"",4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
	}else {
		for(int i=4;i<month;i++) {
			mnthcd.add(i+"");
		}
	}
	Criteria crt1 = Criteria.where("yrCd").is(yearCode).and("mthCd").in(mnthcd);
	Criteria crt = new Criteria().orOperator(Criteria.where("yrCd").in(yearcodes),crt1);
	Query query2 = Query.query(crt);
	applyFilterToCriteria(criteria, filter);
	
	criteria = new Criteria().andOperator(criteria, crt);
	Criteria crt3 = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("mthCd").is(month+"");
	applyFilterToCriteria(crt3, filter1);
	Criteria crt2 = new Criteria().orOperator(crt3,criteria);
	
	Query query1 = Query.query(criteria);
	if(StringUtils.hasLength(searchVal)){
		crt2 = new Criteria().andOperator(crt2, getSearchValueCriteria(searchVal));
	}
	Query query = Query.query(crt2);
	
	if(start == 0 && length == 0) {
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));			
	}else {
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
}

	public List<String> getCustomFields(String clientid, int month, String yearCd) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCd);
		if (month > 0) {
			criteria.and("mthCd").is(month + "");
		}
		List<String> custList = getCustomFields(criteria);
		return custList;
	}

	public List<String> getReportsCustomFields(String clientId, Date stDate, Date endDate) {
		Criteria criteria = Criteria.where("clientid").is(clientId).and("dateofinvoice").gte(stDate).lte(endDate);
		List<String> custList = getCustomFields(criteria);
		return custList;
	}

	public List<String> getGlobalReportsCustomFields(List<String> clientids, int month, String yearCd,String dwnldFromGSTN) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCd);
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		List<String> custList = getCustomFields(criteria);
		return custList;
	}
	private List<String> getCustomFields(Criteria criteria){
		Query query = Query.query(criteria);
		List<String> customFields = Lists.newArrayList();
		List customField1 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField1",query.getQueryObject());
		List customField2 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField2",query.getQueryObject());
		List customField3 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField3",query.getQueryObject());
		List customField4 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField4",query.getQueryObject());
		customFields.addAll(customField1);
		customFields.addAll(customField2);
		customFields.addAll(customField3);
		customFields.addAll(customField4);
		List<String> newList = customFields.stream().distinct().collect(Collectors.toList());
		return newList;
	}
	
	public List<String> getCustomFields(String clientid, int month, String yearCd,String customfieldtext) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCd);
		if (month > 0) {
			criteria.and("mthCd").is(month + "");
		}
		List<String> custList = getCustomFields(criteria,customfieldtext);
		return custList;
	}
	
	public List<String> getCustomEinvFields(String clientid, int month, String yearCd,String customfieldtext) {
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.CDNUR);
		invTypes.add(MasterGSTConstants.EXPORTS);
		invTypes.add(MasterGSTConstants.B2C);
		Criteria criteria = Criteria.where("clientid").is(clientid).and("invtype").in(invTypes).and("yrCd").is(yearCd);
		if (month > 0) {
			criteria.and("mthCd").is(month + "");
		}
		List<String> custList = getCustomFields(criteria,customfieldtext);
		return custList;
	}
	
	
	private List<String> getCustomFields(Criteria criteria, String customfieldtext){
		Query query = Query.query(criteria);
		List<String> customFields = Lists.newArrayList();
		if(customfieldtext.equalsIgnoreCase("customFieldtext1")) {
			List customField1 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField1",query.getQueryObject());
			customFields.addAll(customField1);
		}
		if(customfieldtext.equalsIgnoreCase("customFieldtext2")) {
			List customField2 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField2",query.getQueryObject());
			customFields.addAll(customField2);
		}
		if(customfieldtext.equalsIgnoreCase("customFieldtext3")) {
			List customField3 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField3",query.getQueryObject());
			customFields.addAll(customField3);
		}
		if(customfieldtext.equalsIgnoreCase("customFieldtext4")) {
			List customField4 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField4",query.getQueryObject());
			customFields.addAll(customField4);
		}
		List<String> newList = customFields.stream().distinct().collect(Collectors.toList());
		return newList;
	}

	public List<String> getGlobalCustomFields(List<String> clientids, Date stDate, Date endDate, String dwnldFromGSTN) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate);	
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		List<String> custList = getCustomFields(criteria);
		return custList;
	}
	
	public List<String> getReportsCustomFields(String clientid,Date stDate,Date endDate,  String customfieldtext) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("dateofinvoice").gte(stDate).lte(endDate);
		Query query = Query.query(criteria);
		List<String> customFields = Lists.newArrayList();
		if(customfieldtext.equalsIgnoreCase("customFieldtext1")) {
			List customField1 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField1",query.getQueryObject());
			customFields.addAll(customField1);
		}
		if(customfieldtext.equalsIgnoreCase("customFieldtext2")) {
			List customField2 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField2",query.getQueryObject());
			customFields.addAll(customField2);
		}
		if(customfieldtext.equalsIgnoreCase("customFieldtext3")) {
			List customField3 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField3",query.getQueryObject());
			customFields.addAll(customField3);
		}
		if(customfieldtext.equalsIgnoreCase("customFieldtext4")) {
			List customField4 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField4",query.getQueryObject());
			customFields.addAll(customField4);
		}
		List<String> newList = customFields.stream().distinct().collect(Collectors.toList());
		return newList;
	}
	
	public List<String> getReportsEinvCustomFields(String clientid,Date stDate,Date endDate,  String customfieldtext) {
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.CDNUR);
		invTypes.add(MasterGSTConstants.EXPORTS);
		invTypes.add(MasterGSTConstants.B2C);
		Criteria criteria = Criteria.where("clientid").is(clientid).and("invtype").in(invTypes).and("dateofinvoice").gte(stDate).lte(endDate);
		Query query = Query.query(criteria);
		List<String> customFields = Lists.newArrayList();
		if(customfieldtext.equalsIgnoreCase("customFieldtext1")) {
			List customField1 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField1",query.getQueryObject());
			customFields.addAll(customField1);
		}
		if(customfieldtext.equalsIgnoreCase("customFieldtext2")) {
			List customField2 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField2",query.getQueryObject());
			customFields.addAll(customField2);
		}
		if(customfieldtext.equalsIgnoreCase("customFieldtext3")) {
			List customField3 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField3",query.getQueryObject());
			customFields.addAll(customField3);
		}
		if(customfieldtext.equalsIgnoreCase("customFieldtext4")) {
			List customField4 = mongoTemplate.getCollection(COLLECTION_NAME).distinct("customField4",query.getQueryObject());
			customFields.addAll(customField4);
		}
		List<String> newList = customFields.stream().distinct().collect(Collectors.toList());
		return newList;
	}
	
	
	public Page<? extends InvoiceParent> findByClientidInAndMonthAndYear(List<String> clientids, List<String> invTypes, int month, String yearCode, int start, int length, String searchVal, String booksorReturns,InvoiceFilter filter,Pageable pageable) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("yrCd").is(yearCode);
		if(booksorReturns.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR1.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR1>(Collections.<GSTR1> emptyList());
		}
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME), pageable, total);
		
	}
	
	public Page<? extends InvoiceParent> findByClientidInAndFromtimeAndTotime(List<String> clientids, Date stDate, Date endDate, int start, int length, String searchVal, InvoiceFilter filter, String dwnldFromGSTN,Pageable pageable) {
		//Criteria criteria = Criteria.where("clientid").in(clientids).andOperator(Criteria.where("dateofinvoice").gte(stDate),Criteria.where("dateofinvoice").lte(endDate));
		Criteria criteria = Criteria.where("clientid").in(clientids).and("dateofinvoice").gte(stDate).lte(endDate);	
		if(dwnldFromGSTN.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR1.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR1>(Collections.<GSTR1> emptyList());
		}
		return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME), pageable, total);
	}
	
	public List<TotalInvoiceAmount> getAmendmentConsolidatedSummeryForYearMonthwise(final Client client, String yearCd, boolean checkQuarterly,String gstrType, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd);
		applyFilterToCriteria(criteria, filter);
		Query query = Query.query(criteria);
		List<String> amendmentRefIds = mongoTemplate.getCollection(COLLECTION_NAME).distinct("amendmentRefId", query.getQueryObject());
		
		if(NullUtil.isNotEmpty(amendmentRefIds)){
			List<ObjectId> amendmentinvids = Lists.newArrayList();
			amendmentRefIds.stream().forEach(id -> amendmentinvids.add(new ObjectId(id)));
			Criteria acriteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd).and("_id").in(amendmentinvids);
			applyFilterToCriteria(acriteria, filter);
		
			MatchOperation matchOperation = Aggregation.match(acriteria);
			ProjectionOperation projectionOperation = Aggregation.project()
				.and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
				.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
				.andInclude("mthCd","tcstdsAmount");
			GroupOperation groupOperation = Aggregation.group(checkQuarterly? "qrtCd": "mthCd")
				.sum("totaltaxableamount").as("totalTaxableAmount")
				.sum("tcstdsAmount").as("tcsTdsAmount")
				.sum("totaltax").as("totalTaxAmount")
				.sum("totalamount").as("totalAmount")
				.sum("totalIgstAmount").as("totalIGSTAmount")
				.sum("totalCgstAmount").as("totalCGSTAmount")
				.sum("totalSgstAmount").as("totalSGSTAmount")
				.sum("totalCessAmount").as("totalCESSAmount")
				.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");		
			AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		
			List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		
			return  totalInvAmount;
		}
		return null;
	}
	
	public List<TotalInvoiceAmount> getAmendmentConsolidatedSummeryForCustom(Client client, Date stDate, Date endDate, InvoiceFilter filter,String gstrType) {
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("dateofinvoice").gte(stDate).lte(endDate);
		applyFilterToCriteria(criteria, filter);
		Query query = Query.query(criteria);
		List<String> amendmentRefIds = mongoTemplate.getCollection(COLLECTION_NAME).distinct("amendmentRefId", query.getQueryObject());
		
		if(NullUtil.isNotEmpty(amendmentRefIds)){
			List<ObjectId> amendmentinvids = Lists.newArrayList();
			amendmentRefIds.stream().forEach(id -> amendmentinvids.add(new ObjectId(id)));
			Criteria acriteria = Criteria.where("clientid").is(client.getId().toString())
					.and("dateofinvoice").gte(stDate).lte(endDate).and("_id").in(amendmentinvids);
			applyFilterToCriteria(acriteria, filter);
			MatchOperation matchOperation = Aggregation.match(acriteria);
			ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
					.and("totaltax").multiply("sftr").as("totaltax")
					.and("totalamount").multiply("sftr").as("totalamount")
					.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
					.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
					.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
					.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
					.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
					.andInclude("mthCd","tcstdsAmount");
			GroupOperation groupOperation = Aggregation.group("mthCd")
					.sum("totaltaxableamount").as("totalTaxableAmount")
					.sum("tcstdsAmount").as("tcsTdsAmount")
					.sum("totaltax").as("totalTaxAmount")
					.sum("totalamount").as("totalAmount")
					.sum("totalIgstAmount").as("totalIGSTAmount")
					.sum("totalCgstAmount").as("totalCGSTAmount")
					.sum("totalSgstAmount").as("totalSGSTAmount")
					.sum("totalCessAmount").as("totalCESSAmount")
					.sum("totalExemptedAmount").as("totalExemptedAmount").count().as("totalTransactions");
			AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
			List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
			return totalInvAmount;
		}
		return null;
	}
	
	private List<TotalInvoiceAmount> getAmendmentConsolidatedSummeryForCustom(List<TotalInvoiceAmount> totalInvAmount, Client client, Date stDate, Date endDate, String gstrType, InvoiceFilter filter) {
		
		List<TotalInvoiceAmount> atotalInvAmount = getAmendmentConsolidatedSummeryForCustom(client, stDate, endDate, filter, gstrType);
		if(atotalInvAmount == null) {
			return totalInvAmount;
		}
		for(TotalInvoiceAmount amendmentAmount : atotalInvAmount){
			String acode = amendmentAmount.get_id();
			for(TotalInvoiceAmount totalAmount : totalInvAmount){
				String code = totalAmount.get_id();
				if(acode.equals(code)) {
					totalAmount.setTotalTaxableAmount(totalAmount.getTotalTaxableAmount().subtract(amendmentAmount.getTotalTaxableAmount()));
					totalAmount.setTotalExemptedAmount(totalAmount.getTotalExemptedAmount().subtract(amendmentAmount.getTotalExemptedAmount()));
					totalAmount.setTotalTaxAmount(totalAmount.getTotalTaxAmount().subtract(amendmentAmount.getTotalTaxAmount()));
					totalAmount.setTotalAmount(totalAmount.getTotalAmount().subtract(amendmentAmount.getTotalAmount()));
					totalAmount.setTotalIGSTAmount(totalAmount.getTotalIGSTAmount().subtract(amendmentAmount.getTotalIGSTAmount()));
					totalAmount.setTotalCGSTAmount(totalAmount.getTotalCGSTAmount().subtract(amendmentAmount.getTotalCGSTAmount()));
					totalAmount.setTotalSGSTAmount(totalAmount.getTotalSGSTAmount().subtract(amendmentAmount.getTotalSGSTAmount()));
					totalAmount.setTotalCESSAmount(totalAmount.getTotalCESSAmount().subtract(amendmentAmount.getTotalCESSAmount()));
				}
			}
		}
		return totalInvAmount;
	}
	
	
	public Page<GSTR1> findByClientidAndMonthAndYearForDelete(final String clientid, int month, String yearCode, int start, int length, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("irnNo").in("", null);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0){
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));
		}else {
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
	}
	
	public Page<GSTR1> findByClientidAndMonthAndYearPrevMonthPendingInvoicesDelete(final String clientid, int month, String yearCode, int start, int length, String searchVal, InvoiceFilter filter, InvoiceFilter filter1){
		Criteria criteria = Criteria.where("clientid").is(clientid);
		String yrcd[] = yearCode.split("-");
		String yr = yrcd[0];
		int iyr = Integer.parseInt(yr);
		List<String> yearcodes = Lists.newArrayList();
		for(int i=2017;i<iyr;i++) {
			String yearcode = i+""+"-"+(i+1)+"";
			yearcodes.add(yearcode);
		}
		List<String> mnthcd = Lists.newArrayList();
		if(month == 1) {
			mnthcd = Arrays.asList(4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
		}else if(month == 2) {
			mnthcd = Arrays.asList(1+"",4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
		}else if(month == 3) {
			mnthcd = Arrays.asList(1+"",2+"",4+"",5+"",6+"",7+"",8+"",9+"",10+"",11+"",12+"");
		}else {
			for(int i=4;i<month;i++) {
				mnthcd.add(i+"");
			}
		}
		Criteria crt1 = Criteria.where("yrCd").is(yearCode).and("mthCd").in(mnthcd).and("irnNo").in("", null);
		Criteria crt = new Criteria().orOperator(Criteria.where("yrCd").in(yearcodes),crt1);
		Query query2 = Query.query(crt);
		applyFilterToCriteria(criteria, filter);
		
		criteria = new Criteria().andOperator(criteria, crt);
		Criteria crt3 = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("mthCd").is(month+"").and("irnNo").in("", null);
		applyFilterToCriteria(crt3, filter1);
		Criteria crt2 = new Criteria().orOperator(crt3,criteria);
		
		Query query1 = Query.query(criteria);
		if(StringUtils.hasLength(searchVal)){
			crt2 = new Criteria().andOperator(crt2, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(crt2);
		
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));			
		}else {
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
	}
	
	public Page<GSTR1> findByClientidAndFpDelete(final String clientId, String fp, int start, int length, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("fp").is(fp).and("irnNo").in("", null);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR1>(mongoTemplate.find(query, GSTR1.class, COLLECTION_NAME));			
		}else {
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
	}
	
	public GSTR1 findByInvoicenoAndClientidAndInvtypeAndDateofinvoice_str(String invoiceno,String clientid,String invtype,String dateofinvoicestr) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("invtype").is(invtype).and("invoiceno").is(invoiceno).and("dateofinvoice_str").is(dateofinvoicestr);
		Query query = Query.query(criteria);
		return mongoTemplate.findOne(query, GSTR1.class, COLLECTION_NAME);
		
	}
}
