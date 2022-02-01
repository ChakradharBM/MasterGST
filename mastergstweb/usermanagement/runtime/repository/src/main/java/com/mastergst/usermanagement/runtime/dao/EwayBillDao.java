package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CustomData;
import com.mastergst.usermanagement.runtime.domain.CustomFields;
import com.mastergst.usermanagement.runtime.domain.EWAYBILL;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceTypeSummery;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.repository.CustomFieldsRepository;


@Service
public class EwayBillDao extends InvoiceDao {
	

	public EwayBillDao(){
		super("ewaybill");
	}
	public TotalInvoiceAmount getTotalInvoicesAmountsForMonth(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter){
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
			cancelInvAmount = getTotalInvoicesAmountsForMonthCancelled(clientId,month,yearCode,searchVal,filter);
			//filter.setPaymentStatus("CNL");
			if(filter.getStatus() != null && filter.getStatus().length == 1) {
				List<String> statusList = Arrays.asList(filter.getStatus());
				if(statusList.contains("Cancelled")) {
					return cancelInvAmount;
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
	public TotalInvoiceAmount getTotalInvoicesAmountsForMonthCancelled(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter){
		//List<String> allStatusList = Arrays.asList("CNL");
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCode).and("status").is("CNL");
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteriaForCancelled(criteria, filter);
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
	
	public List<String> getBillToNames(String clientId, int month,String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("yrCd").is(yearCd);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	public List<String> getCustomBillToNames(String clientId, Date stDate,Date endDate){
		Criteria criteria = Criteria.where("clientid").is(clientId).andOperator(
				Criteria.where("eBillDate").gte(stDate),
				Criteria.where("eBillDate").lte(endDate)
		);
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	/* public TotalInvoiceAmount getTotalInvoicesAmountsForMonth(String clientId, int month, String yearCode, String searchVal){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("mthCd").is(month+"").and("yrCd").is(yearCode);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal();
		GroupOperation groupOperation = getGroupForTotal();
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	} */
	
	private ProjectionOperation getProjectionForTotal(String fieldname){
		return Aggregation.project().andInclude(fieldname)
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totalExemptedAmount").multiply("sftr").as("totalExemptedAmount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount");
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
				.sum("totalCessAmount").as("totalCESSAmount");
	}
	
	public List<String> getBillToNames(String clientId, String fp){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("fp").is(fp);
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	public List<String> getEBillToNames(String clientId, int month,String yearCd){
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
		ProjectionOperation projectionOperation = getProjectionForTotal("fp");
		GroupOperation groupOperation = getGroupForTotal("fp");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	}
	
	public Page<EWAYBILL> findByClientidAndMonthAndYear(final String clientid, int month, String yearCode, int start, int length, String searchVal, InvoiceFilter filter){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria,filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0){
			return new PageImpl<EWAYBILL>(mongoTemplate.find(query, EWAYBILL.class, COLLECTION_NAME));
		}else {
			addAllInvoicesQueryFirlds(query);
			/*Sort sort = null;
			if(!IbcUtils.isNullOrEmpty(fieldName) && !IbcUtils.isNullOrEmpty(order)){	
				sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
			}*/
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, EWAYBILL.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<EWAYBILL>(Collections.<EWAYBILL> emptyList());
			}
			return new PageImpl<EWAYBILL>(mongoTemplate.find(query, EWAYBILL.class, COLLECTION_NAME), pageable, total);
		}
	}
	
public Page<EWAYBILL> findByClientidAndMonthAndYear(final String clientid, int month, String yearCode, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		applyFilterToCriteria(criteria,filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0){
			return new PageImpl<EWAYBILL>(mongoTemplate.find(query, EWAYBILL.class, COLLECTION_NAME));
		}else {
			addAllInvoicesQueryFirlds(query);
			Sort sort = null;
			if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
				sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
			}
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length,sort);
			query.with(pageable);
			long total = mongoTemplate.count(query, EWAYBILL.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<EWAYBILL>(Collections.<EWAYBILL> emptyList());
			}
			return new PageImpl<EWAYBILL>(mongoTemplate.find(query, EWAYBILL.class, COLLECTION_NAME), pageable, total);
		}
	}
	
	private void applyFilterToCriteria(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			List<Criteria> criterias = new ArrayList<Criteria>();
			if(filter.getPaymentStatus() != null){
				criteria.and("paymentStatus").in(Arrays.asList(filter.getPaymentStatus()));
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
			if(filter.getReverseCharge() != null){
				criteria.and("revchargetype").in(Arrays.asList(filter.getReverseCharge()));
			}
			if(filter.getSupplyType() != null){
				criteria.and("supplyType").in(Arrays.asList(filter.getSupplyType()));
			}
			if(filter.getDocumentType() != null){
				criteria.and("docType").in(Arrays.asList(filter.getDocumentType()));
			}
			if(filter.getSubSupplyType() != null){
				criteria.and("subSupplyType").in(Arrays.asList(filter.getSubSupplyType()));
			}
			/*if(filter.getCustomFieldText1() != null){
				//Criteria criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("customField1").in(Arrays.asList(filter.getCustomField1())),Criteria.where("customField2").in(Arrays.asList(filter.getCustomField1())),Criteria.where("customField3").in(Arrays.asList(filter.getCustomField1())),Criteria.where("customField4").in(Arrays.asList(filter.getCustomField1()))));	
				Criteria criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where("customFieldText1").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText2").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText3").in(Arrays.asList(filter.getCustomFieldText1())),Criteria.where("customFieldText4").in(Arrays.asList(filter.getCustomFieldText1()))));
				criterias.add(criteriaa);
			}*/
			
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
			
			if(filter.getStatus() != null){
				Criteria criteriaa = null;
				String fieldName =  "status";
				String ewayno = "ewayBillNumber";
				List<String> statusList = new LinkedList<String>(Arrays.asList(filter.getStatus()));
				if(statusList.size() < 3) {
					if((statusList.contains("Not Generated") || statusList.contains(" ")) && !statusList.contains("Cancelled")){
						if(statusList.contains("Generated")) {
							criteriaa = new Criteria().andOperator(criteria.and(fieldName).ne("CNL"),new Criteria().orOperator(Criteria.where(ewayno).ne(null), Criteria.where(ewayno).is(""),Criteria.where(ewayno).is(null),Criteria.where(ewayno).ne("")));
						}else {
							criteriaa = new Criteria().andOperator(criteria.and(fieldName).ne("CNL"),new Criteria().orOperator(Criteria.where(ewayno).is(""),Criteria.where(ewayno).is(null)));
						}
					}else if(statusList.contains("Generated") && !statusList.contains("Cancelled")){
						if(statusList.contains("Generated")) {
							criteriaa = new Criteria().andOperator(criteria.and(fieldName).ne("CNL"),new Criteria().orOperator(Criteria.where(ewayno).ne("")));
						}else {
							criteriaa = new Criteria().andOperator(criteria.and(fieldName).ne("CNL"),new Criteria().orOperator(Criteria.where(ewayno).is(""),Criteria.where(ewayno).is(null)));
						}
					} else if(statusList.contains("Cancelled")) {
						if(statusList.contains("Generated")) {
							criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where(ewayno).ne(""),Criteria.where(fieldName).is("CNL")));
						}else if(statusList.contains("Not Generated") || statusList.contains(" ")) {
							criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where(ewayno).is(""),Criteria.where(ewayno).is(null),Criteria.where(fieldName).is("CNL")));
						}else {
							criteria.and(fieldName).is("CNL");
						}
					}else if(statusList.contains("Rejected")) {
						if(statusList.contains("Generated")) {
							criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where(ewayno).ne(""),Criteria.where(fieldName).is("Rejected")));
						}else if(statusList.contains("Not Generated") || statusList.contains(" ")) {
							criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where(ewayno).is(""),Criteria.where(ewayno).is(null),Criteria.where(fieldName).is("Rejected")));
						}else if(statusList.contains("Cancelled")) {
							criteriaa = new Criteria().andOperator(criteria,new Criteria().orOperator(Criteria.where(ewayno).ne(""),Criteria.where(fieldName).is("CNL")));
						}else {
							criteria.and(fieldName).is("Rejected");
						}
					}
				}
				if(NullUtil.isNotEmpty(criteriaa)) {
					criterias.add(criteriaa);
				}
				
			}
			if(NullUtil.isNotEmpty(criterias)) {
				criteria.andOperator(criterias.toArray(new Criteria[criterias.size()]));				
			}
		}
	}
	private void applyFilterToCriteriaForCancelled(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			if(filter.getPaymentStatus() != null){
				criteria.and("paymentStatus").in(Arrays.asList(filter.getPaymentStatus()));
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
			if(filter.getReverseCharge() != null){
				criteria.and("revchargetype").in(Arrays.asList(filter.getReverseCharge()));
			}
			if(filter.getSupplyType() != null){
				criteria.and("supplyType").in(Arrays.asList(filter.getSupplyType()));
			}
			if(filter.getDocumentType() != null){
				criteria.and("docType").in(Arrays.asList(filter.getDocumentType()));
			}
			if(filter.getSubSupplyType() != null){
				criteria.and("subSupplyType").in(Arrays.asList(filter.getSubSupplyType()));
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
		}
	}
	public Page<EWAYBILL> findByClientidAndInvtypeInAndFp(final String clientId, final List<String> invTypes, String fp, int start, int length, String searchVal){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("invtype").in(invTypes).and("fp").is(fp);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		/*Sort sort = null;
		if(!IbcUtils.isNullOrEmpty(fieldName) && !IbcUtils.isNullOrEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}*/
		Pageable pageable = new PageRequest(start, length);
		query.with(pageable);
		long total = mongoTemplate.count(query, EWAYBILL.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<EWAYBILL>(Collections.<EWAYBILL> emptyList());
		}
		return new PageImpl<EWAYBILL>(mongoTemplate.find(query, EWAYBILL.class, COLLECTION_NAME), pageable, total);
	}
	
	private Criteria getSearchValueCriteria(String searchval){
		String searchVal = searchval;
		if(searchVal.contains("(")) {
			searchVal = searchVal.replaceAll("\\(", "\\\\(");
		}
		if(searchVal.contains(")")) {
			searchVal = searchVal.replaceAll("\\)", "\\\\)");
		}
		List<Criteria> criterias = new ArrayList<Criteria>();
 		criterias.add(Criteria.where("fullname").regex(searchVal, "i"));
 		criterias.add(Criteria.where("paymentStatus").regex(searchVal, "i"));
 		criterias.add(Criteria.where("invtype").regex(searchVal, "i"));
 		criterias.add(Criteria.where("billedtoname").regex(searchVal, "i"));
 		criterias.add(Criteria.where("invoiceno").regex(searchVal, "i"));
 		criterias.add(Criteria.where("b2b.0.ctin").regex(searchVal, "i"));
		criterias.add(Criteria.where("dateofinvoice_str").regex(searchVal, "i"));
		criterias.add(Criteria.where("ewayBillDate_str").regex(searchVal, "i"));
		criterias.add(Criteria.where("ewayBillNumber").regex(searchVal, "i"));
		criterias.add(Criteria.where("validUpto").regex(searchVal, "i"));
		criterias.add(Criteria.where("customField1").regex(searchVal, "i"));
		criterias.add(Criteria.where("customField2").regex(searchVal, "i"));
		criterias.add(Criteria.where("customField3").regex(searchVal, "i"));
		criterias.add(Criteria.where("customField4").regex(searchVal, "i"));
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
		query.fields().include("clientid");
		query.fields().include("fullname");
		query.fields().include("gstStatus");
		query.fields().include("paymentStatus");
		query.fields().include("revchargetype");
		query.fields().include("invtype");
		query.fields().include("invoiceno");
		query.fields().include("billedtoname");
		query.fields().include("b2b");
		query.fields().include("cfs");
		query.fields().include("dateofinvoice");
		query.fields().include("eBillDate");
		query.fields().include("subSupplyType");
		query.fields().include("ewayBillNumber");
		query.fields().include("totaltaxableamount");
		query.fields().include("totaltax");
		query.fields().include("igstamount");
		query.fields().include("cgstamount");
		query.fields().include("sgstamount");
		query.fields().include("totalamount");
		query.fields().include("totalIgstAmount");
		query.fields().include("totalCgstAmount");
		query.fields().include("totalSgstAmount");
		query.fields().include("status");
		query.fields().include("validUpto");
		query.fields().include("ebillValidator");
		
	}
	
public List<TotalInvoiceAmount> getConsolidatedSummeryForYearMonthwise(final Client client, String yearCd, boolean checkQuarterly,InvoiceFilter filter){
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
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedSummeryForYearMonthwise(client,yearCd,checkQuarterly);
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
public List<TotalInvoiceAmount> getCancelledConsolidatedSummeryForYearMonthwise(final Client client, String yearCd, boolean checkQuarterly){
	Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("yrCd").is(yearCd).and("status").is("CNL");
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
	public List<InvoiceTypeSummery> getInvoiceSummeryByTypeForMonth(final String clientid, int month, String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("mthCd").is(month+"").and("yrCd").is(yearCd);
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
	
	public Page<EWAYBILL> findByClientidAndFromtimeAndTotime(final String clientid, Date stDate, Date endDate, int start, int length, String searchVal, InvoiceFilter filter){
		//Criteria criteria = Criteria.where("clientid").is(clientid).andOperator(Criteria.where("eBillDate").gte(stDate),Criteria.where("eBillDate").lte(endDate));
		Criteria criteria = Criteria.where("clientid").is(clientid).and("eBillDate").gte(stDate).lte(endDate);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<EWAYBILL>(mongoTemplate.find(query, EWAYBILL.class, COLLECTION_NAME));			
		}else {
			addAllInvoicesQueryFirlds(query);
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, EWAYBILL.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<EWAYBILL>(Collections.<EWAYBILL> emptyList());
			}
			return new PageImpl<EWAYBILL>(mongoTemplate.find(query, EWAYBILL.class, COLLECTION_NAME), pageable, total);
		}
	}
	
	public Page<EWAYBILL> findByClientidAndFromtimeAndTotime(final String clientid, Date stDate, Date endDate, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter){
		//Criteria criteria = Criteria.where("clientid").is(clientid).andOperator(Criteria.where("eBillDate").gte(stDate),Criteria.where("eBillDate").lte(endDate));
		Criteria criteria = Criteria.where("clientid").is(clientid).and("eBillDate").gte(stDate).lte(endDate);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<EWAYBILL>(mongoTemplate.find(query, EWAYBILL.class, COLLECTION_NAME));			
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
			long total = mongoTemplate.count(query, EWAYBILL.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<EWAYBILL>(Collections.<EWAYBILL> emptyList());
			}
			return new PageImpl<EWAYBILL>(mongoTemplate.find(query, EWAYBILL.class, COLLECTION_NAME), pageable, total);
		}
	}
	public TotalInvoiceAmount getTotalInvoicesAmountsForCustom(String clientid, Date stDate, Date endDate, String searchVal, InvoiceFilter filter){
		//Criteria criteria = Criteria.where("clientid").is(clientid).andOperator(Criteria.where("eBillDate").gte(stDate),Criteria.where("eBillDate").lte(endDate));
		Criteria criteria = Criteria.where("clientid").is(clientid).and("eBillDate").gte(stDate).lte(endDate);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
		GroupOperation groupOperation = getGroupForTotal("csftr");
		
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		TotalInvoiceAmount cancelInvAmount = new TotalInvoiceAmount();
		cancelInvAmount = getCancelledTotalInvoicesAmountsForCustom(clientid,stDate,endDate,searchVal,filter);
		//filter.setPaymentStatus("CNL");
		if(filter.getStatus() != null && filter.getStatus().length == 1) {
			List<String> statusList = Arrays.asList(filter.getStatus());
			if(statusList.contains("Cancelled")) {
				return cancelInvAmount;
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
	public TotalInvoiceAmount getCancelledTotalInvoicesAmountsForCustom(String clientid, Date stDate, Date endDate, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientid).and("status").is("CNL").and("eBillDate").gte(stDate).lte(endDate);
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
	public List<TotalInvoiceAmount> getConsolidatedSummeryForCustom(Client client, String returntype, Date stDate, Date endDate,InvoiceFilter filter) {
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("eBillDate").gte(stDate).lte(endDate);
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
		List<TotalInvoiceAmount> totalCanInvAmount = getCancelledConsolidatedSummeryForCustom(client,returntype,stDate,endDate);
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
	public List<TotalInvoiceAmount> getCancelledConsolidatedSummeryForCustom(Client client, String returntype, Date stDate, Date endDate) {
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("status").is("CNL").and("eBillDate").gte(stDate).lte(endDate);
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
	
	public List<String> getCustomFields(String clientid, int month, String yearCd,String customfieldtext) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCd);
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
}
