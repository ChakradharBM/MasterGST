package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.domain.Anx2;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceTypeSummery;
import com.mastergst.usermanagement.runtime.domain.NewAnx2;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;

@Service
public class Anx2Dao  extends InvoiceDao{

	public Anx2Dao(){
		super("Anx2");
	}
	
	
	
	public List<String> getBillToNames(String clientId, int month, String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("mthCd").is(month+"").and("yrCd").is(yearCd);
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	public TotalInvoiceAmount getTotalInvoicesAmountsForMonth(String clientId, int month, String yearCode, String searchVal, InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("mthCd").is(month+"").and("yrCd").is(yearCode);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("mthCd");
		GroupOperation groupOperation = getGroupForTotal("mthCd");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return results.getUniqueMappedResult();
	}
	
	
	
	private Criteria getSearchValueCriteria(final String searchVal){
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
 		criterias.add(Criteria.where("anx2b2b.0.ctin").regex(searchVal, "i"));
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
				.and("totalitc").multiply("sftr").as("totalitc");
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
				.sum("totalitc").as("totalITCAvailable");
	}
	
	public Page<Anx2> findByClientidAndMonthAndYear(final String clientid, int month, String yearCode, int start, int length, String searchVal, InvoiceFilter filter){
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("mthCd").is(month+"").and("yrCd").is(yearCode);
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFirlds(query);
		/*Sort sort = null;
		if(!IbcUtils.isNullOrEmpty(fieldName) && !IbcUtils.isNullOrEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}*/
		Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, Anx2.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<Anx2>(Collections.<Anx2> emptyList());
		}
		return new PageImpl<Anx2>(mongoTemplate.find(query, Anx2.class, COLLECTION_NAME), pageable, total);
	}
	
	
	
	private void applyFilterToCriteria(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			if(filter.getPaymentStatus() != null){
				String fieldName = "books".equals(filter.getBooksOrReturns()) ? "paymentStatus":"gstStatus";
				List<String> statusList = Arrays.asList(filter.getPaymentStatus());
				if(statusList.contains("Not Paid") || statusList.contains("Pending")){
					criteria.andOperator(new Criteria().orOperator(Criteria.where(fieldName).in(statusList),Criteria.where(fieldName).is(""), Criteria.where(fieldName).is(null)));	
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
			if(filter.getReverseCharge() != null){
				List<String> reverseChargeList = Arrays.asList(filter.getReverseCharge());
				if(reverseChargeList.contains("Regular")) {
					criteria.andOperator(new Criteria().orOperator(Criteria.where("revchargetype").in(Arrays.asList(filter.getReverseCharge())),Criteria.where("revchargetype").is(""), Criteria.where("revchargetype").is(null)));
				}else {
					criteria.and("revchargetype").in(reverseChargeList);
				}
					
				
			}
		}
	}
	
	
	private void addAllInvoicesQueryFirlds(Query query){
		query.fields().include("fullname");
		query.fields().include("gstStatus");
		query.fields().include("revchargetype");
		query.fields().include("invtype");
		query.fields().include("invoiceno");
		query.fields().include("billedtoname");
		query.fields().include("b2b");
		query.fields().include("anx2b2b");
		query.fields().include("de");
		query.fields().include("cdn");
		query.fields().include("cdnur");
		query.fields().include("dateofinvoice");
		query.fields().include("totaltaxableamount");
		query.fields().include("totaltax");
		query.fields().include("totalIgstAmount");
		query.fields().include("totalCgstAmount");
		query.fields().include("totalSgstAmount");
		query.fields().include("totalamount");
		query.fields().include("branch");
		query.fields().include("vertical");
	
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
	

	
	
	
	
	
	
	
	
	public List<Anx2> findByClientidAndInvoiceTypeAndMonthAndYear(final String clientid, int month, String yearCode){       
        Criteria criteria = Criteria.where("clientid").is(clientid).and("mthCd").is(month+"").and("yrCd").is(yearCode).and("invtype").in(MasterGSTConstants.B2B, MasterGSTConstants.DE,MasterGSTConstants.SEZWP,MasterGSTConstants.SEZWOP,MasterGSTConstants.ISDC);
        Query query = Query.query(criteria);
        addAllInvoicesQueryFirlds(query);
        return mongoTemplate.find(query, Anx2.class, COLLECTION_NAME);
    }
	
	
	public List<Anx2> findByById(List<String> ids,String clientid){
        if(ids != null) {
           //List<ObjectId> objectsIds = ids.stream().map((id) -> new ObjectId(id)).collect(Collectors.toList());
           Criteria criteria = Criteria.where("clientid").is(clientid).and("matchingId").in(ids);
           Query query = Query.query(criteria);
           addAllInvoicesQueryFirlds(query);
          
           return mongoTemplate.find(query, Anx2.class, COLLECTION_NAME);
        }else {
            return null;
        }
    }
	

	public List<String> monthList(int month){
		List<String> mnthList = Lists.newArrayList();
		if(month > 3) {
			for(int i=4; i <= month; i++) {
				mnthList.add(i+"");	
			}
		}
		if(month <= 3) {
			for(int i=1; i <= month; i++) {
				mnthList.add(i+"");	
			}
			for(int i=4; i <= 12; i++) {
				mnthList.add(i+"");	
			}
		}
		return mnthList;
		
	}
}
