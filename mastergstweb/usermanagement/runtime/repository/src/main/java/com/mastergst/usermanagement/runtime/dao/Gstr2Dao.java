package com.mastergst.usermanagement.runtime.dao;

import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

import com.mastergst.usermanagement.runtime.domain.MismatchInvCount;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.google.api.client.util.Lists;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;

import org.apache.commons.lang.math.NumberUtils;
import org.bson.types.ObjectId;


@Service
public class Gstr2Dao extends InvoiceDao {

	
	public Gstr2Dao(){
		super("gstr2");
	}
	
	public List<String> getBillToNames(String clientId, List<String> invTypes, Date stDate, Date endDate){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("invtype").in(invTypes).and("dateofinvoice").gte(stDate).lte(endDate);
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	public List<String> getBillToNames(String clientId, List<String> invTypes, String fp){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("invtype").in(invTypes).and("fp").is(fp);
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	public List<MismatchInvCount> getNoOfMismatches(List<String> clientIdStr, int month, int year){
        String yearCd = month<4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
        Criteria criteria = Criteria.where("clientid").in(clientIdStr).and("mthCd").is(month+"").and("yrCd").is(yearCd).and("matchingStatus").ne("Matched");
        MatchOperation matchOperation = Aggregation.match(criteria);
        AggregationResults<MismatchInvCount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, Aggregation.group("clientid").count().as("mismatchCount")), COLLECTION_NAME, MismatchInvCount.class);
        List<MismatchInvCount> resultMap = results.getMappedResults();
        return resultMap;
    }
	
	private void addAllInvoicesQueryFirlds(Query query){
        query.fields().include("fullname");
        query.fields().include("gstStatus");
        query.fields().include("paymentStatus");
        query.fields().include("revchargetype");
        query.fields().include("revchargeNo");
        query.fields().include("invtype");
        query.fields().include("invoiceno");
        query.fields().include("billedtoname");
        query.fields().include("b2b");
        query.fields().include("cfs");
        query.fields().include("cdn");
		query.fields().include("cdnur");
		query.fields().include("impGoods");
        query.fields().include("dateofinvoice");
        query.fields().include("totaltaxableamount");
        query.fields().include("totaltax");
		query.fields().include("totalIgstAmount");
		query.fields().include("totalCgstAmount");
		query.fields().include("totalSgstAmount");
		query.fields().include("totalamount");
		query.fields().include("totalitc");
		query.fields().include("branch");
		query.fields().include("vertical");
		query.fields().include("matchingStatus");
    }
   
     public List<GSTR2> findByClientidAndInvoiceTypeAndMonthAndYear(final String clientid, String returnPeriod){       
            Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").is(returnPeriod).and("isAmendment").is(true).and("invtype").in(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES).and("matchingStatus").is(null);
            Query query = Query.query(criteria);
            addAllInvoicesQueryFirlds(query);
            return mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME);
        }
     
     public List<GSTR2> findByClientidAndInvoiceTypeAndMonthAndYearIMPG(final String clientid, String returnPeriod,String invtype){       
         Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").is(returnPeriod).and("isAmendment").is(true).and("invtype").is(MasterGSTConstants.IMP_GOODS);
         if(invtype.equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
        	 criteria.and("impGoods.0.isSez").is("N");
         }else if(invtype.equalsIgnoreCase(MasterGSTConstants.IMPGSEZ)){
        	 criteria.and("impGoods.0.isSez").is("Y");
         }
         Query query = Query.query(criteria);
         addAllInvoicesQueryFirlds(query);
         return mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME);
     }
     
     public List<GSTR2> findByClientidAndInvoiceTypeAndMonthAndYearMatchingStatusisNotNull(final String clientid, String returnPeriod, String normalOrMannual){  
    	 List<String> matchingstatus = new ArrayList<String>();
 		if(normalOrMannual.equalsIgnoreCase("normal")) {
 			matchingstatus.add(MasterGSTConstants.GST_STATUS_NOTINPURCHASES);
 			matchingstatus.add(MasterGSTConstants.GST_STATUS_MATCHED);
 			matchingstatus.add(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
 			matchingstatus.add(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
 			matchingstatus.add(MasterGSTConstants.GST_STATUS_MISMATCHED);
 			matchingstatus.add(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
 			matchingstatus.add(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED_PREVIOUS_MONTH);
 		}else {
 			matchingstatus.add(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
 		}
         Criteria criteria = Criteria.where("clientid").is(clientid).and("fp").is(returnPeriod).and("isAmendment").is(true).and("invtype").in(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,MasterGSTConstants.B2BA,MasterGSTConstants.CDNA).and("matchingStatus").in(matchingstatus);
         if(!normalOrMannual.equalsIgnoreCase("normal")) {
         	criteria.and("mannualMatchInvoices").is("Single");
         }
         Query query = Query.query(criteria);
         addAllInvoicesQueryFirlds(query);
         return mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME);
     }
     
     public List<GSTR2> findByById(List<String> ids){
         if(ids != null) {
            List<ObjectId> objectsIds = ids.stream().map((id) -> new ObjectId(id)).collect(Collectors.toList());
            Criteria criteria = Criteria.where("_id").in(objectsIds).and("isAmendment").is(true);
            Query query = Query.query(criteria);
            addAllInvoicesQueryFirlds(query);
            return mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME);
         }else {
             return null;
         }
     }
     
	public Page<GSTR2> getMultimonthInvoices(String clientid, List<String> invTypes, String yearCode, int start, int length, String searchVal, InvoiceFilter filter) {
		
		Criteria criteria = Criteria.where("clientid").is(clientid).and("isAmendment").is(true).and("yrCd").is(yearCode);
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME));
		}else {
			addAllInvoicesQueryFirlds(query);
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
			}
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable, total);
		}
	}
	
	public TotalInvoiceAmount getMultimonthTotalInvoicesAmounts(String clientid, List<String> invTypes, String yearCode,String searchVal, InvoiceFilter filter){

		Criteria criteria = Criteria.where("clientid").is(clientid).and("isAmendment").is(true).and("yrCd").is(yearCode);
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
		TotalInvoiceAmount totalInvAmount = results.getUniqueMappedResult();
		if(filter.getInvoiceType() == null){
			TotalInvoiceAmount totalAmmendedInvAmount = getAmmendedConsolidatedSummeryForYearMonthwise(clientid,yearCode,searchVal,filter);
			Page<? extends InvoiceParent> invoices = getMultimonthInvoices(clientid,yearCode,searchVal,filter);
			List<String> invoiceNumbers = Lists.newArrayList();
			if(isNotEmpty(invoices)) {
				for(InvoiceParent invoiceParent : invoices) {
					if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA)) {
						if(isNotEmpty(((GSTR2)invoiceParent).getB2ba()) && isNotEmpty(((GSTR2)invoiceParent).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoiceParent).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoiceParent).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoiceParent).getB2ba().get(0).getInv().get(0).getOinum())) {
							invoiceNumbers.add(((GSTR2)invoiceParent).getB2ba().get(0).getInv().get(0).getOinum().trim());
						}
					}else if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
						if(isNotEmpty(((GSTR2)invoiceParent).getCdna()) && isNotEmpty(((GSTR2)invoiceParent).getCdna().get(0)) && isNotEmpty(((GSTR2)invoiceParent).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoiceParent).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoiceParent).getCdna().get(0).getNt().get(0).getOntNum())) {
							invoiceNumbers.add(((GSTR2)invoiceParent).getCdna().get(0).getNt().get(0).getOntNum().trim());
						}
					}
				}
			}
			TotalInvoiceAmount totalOriginalInvAmount = getCancelledConsolidatedSummeryForYearMonthwise(clientid,yearCode,invoiceNumbers,searchVal,filter);
			if(isNotEmpty(totalAmmendedInvAmount) && isNotEmpty(totalOriginalInvAmount)) {
				totalInvAmount.setTotalTaxableAmount((totalInvAmount.getTotalTaxableAmount().subtract(totalAmmendedInvAmount.getTotalTaxableAmount())).add((totalAmmendedInvAmount.getTotalTaxableAmount().subtract(totalOriginalInvAmount.getTotalTaxableAmount()))));
				totalInvAmount.setTotalTaxAmount((totalInvAmount.getTotalTaxAmount().subtract(totalAmmendedInvAmount.getTotalTaxAmount())).add((totalAmmendedInvAmount.getTotalTaxAmount().subtract(totalOriginalInvAmount.getTotalTaxAmount()))));
				totalInvAmount.setTotalAmount((totalInvAmount.getTotalAmount().subtract(totalAmmendedInvAmount.getTotalAmount())).add((totalAmmendedInvAmount.getTotalAmount().subtract(totalOriginalInvAmount.getTotalAmount()))));
				totalInvAmount.setTotalIGSTAmount((totalInvAmount.getTotalIGSTAmount().subtract(totalAmmendedInvAmount.getTotalIGSTAmount())).add((totalAmmendedInvAmount.getTotalIGSTAmount().subtract(totalOriginalInvAmount.getTotalIGSTAmount()))));
				totalInvAmount.setTotalCGSTAmount((totalInvAmount.getTotalCGSTAmount().subtract(totalAmmendedInvAmount.getTotalCGSTAmount())).add((totalAmmendedInvAmount.getTotalCGSTAmount().subtract(totalOriginalInvAmount.getTotalCGSTAmount()))));
				totalInvAmount.setTotalSGSTAmount((totalInvAmount.getTotalSGSTAmount().subtract(totalAmmendedInvAmount.getTotalSGSTAmount())).add((totalAmmendedInvAmount.getTotalSGSTAmount().subtract(totalOriginalInvAmount.getTotalSGSTAmount()))));
				totalInvAmount.setTotalCESSAmount((totalInvAmount.getTotalCESSAmount().subtract(totalAmmendedInvAmount.getTotalCESSAmount())).add((totalAmmendedInvAmount.getTotalCESSAmount().subtract(totalOriginalInvAmount.getTotalCESSAmount()))));
				totalInvAmount.setTcsTdsAmount((totalInvAmount.getTcsTdsAmount().subtract(totalAmmendedInvAmount.getTcsTdsAmount())).add((totalAmmendedInvAmount.getTcsTdsAmount().subtract(totalOriginalInvAmount.getTcsTdsAmount()))));
			}
		}
		return totalInvAmount;
	}
	
public Page<GSTR2> getMultimonthInvoices(String clientid, String yearCode, String searchVal, InvoiceFilter filter) {
	List<String> invTypes = Arrays.asList(MasterGSTConstants.B2BA, MasterGSTConstants.CDNA);
		Criteria criteria = Criteria.where("clientid").is(clientid).and("isAmendment").is(true).and("yrCd").is(yearCode);
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME));
	}
public Page<GSTR2> getMultimonthOriginalInvoices(String clientid, List<String> invoiceNumbers, String searchVal, InvoiceFilter filter) {
	List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES);
		Criteria criteria = Criteria.where("clientid").is(clientid).and("isAmendment").is(true).and("invoiceno").in(invoiceNumbers);
		criteria.and("invtype").in(invTypes);
		/*
		 * if(filter.getInvoiceType() == null){ criteria.and("invtype").in(invTypes); }
		 * applyFilterToCriteria(criteria, filter);
		 * if(StringUtils.hasLength(searchVal)){ criteria = new
		 * Criteria().andOperator(criteria, getSearchValueCriteria(searchVal)); }
		 */
		Query query = Query.query(criteria);
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME));
	}

public TotalInvoiceAmount getCancelledConsolidatedSummeryForYearMonthwise(String clientid, String yearCode,List<String> invoiceNumbers,String searchVal, InvoiceFilter filter){
	List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES);
	Criteria criteria = Criteria.where("clientid").is(clientid).and("isAmendment").is(true).and("invoiceno").in(invoiceNumbers);
	criteria.and("invtype").in(invTypes);
	/*if(filter.getInvoiceType() == null){
		criteria.and("invtype").in(invTypes);
	}
	applyFilterToCriteria(criteria, filter);
	if(StringUtils.hasLength(searchVal)){
		criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
	}*/
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = getProjectionForTotal("csftr");
	GroupOperation groupOperation = getGroupForTotal("csftr");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return  results.getUniqueMappedResult();
}

public List<TotalInvoiceAmount> getCancelledConsolidatedMultimonthSummeryForYearMonthwise(String clientid, String yearCode,List<String> invoiceNumbers, InvoiceFilter filter){
	List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES);
	Criteria criteria = Criteria.where("clientid").is(clientid).and("isAmendment").is(true).and("invoiceno").in(invoiceNumbers);
	if(filter.getInvoiceType() == null){
		criteria.and("invtype").in(invTypes);
	}
	applyFilterToCriteria(criteria, filter);
	MatchOperation matchOperation = Aggregation.match(criteria);
	ProjectionOperation projectionOperation = getProjectionForTotal("mthCd");
	GroupOperation groupOperation = getGroupForTotal("mthCd");
	AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
	return  results.getMappedResults();
}


	public TotalInvoiceAmount getAmmendedConsolidatedSummeryForYearMonthwise(String clientid, String yearCode,String searchVal, InvoiceFilter filter){
		List<String> invTypes = Arrays.asList(MasterGSTConstants.B2BA, MasterGSTConstants.CDNA);
		Criteria criteria = Criteria.where("clientid").is(clientid).and("isAmendment").is(true).and("yrCd").is(yearCode);
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
		return  results.getUniqueMappedResult();
	}
	
	public List<TotalInvoiceAmount> getAmmendedMultimomthConsolidatedSummeryForYearMonthwise(String clientid, String yearCode, InvoiceFilter filter){
		List<String> invTypes = Arrays.asList(MasterGSTConstants.B2BA, MasterGSTConstants.CDNA);
		Criteria criteria = Criteria.where("clientid").is(clientid).and("isAmendment").is(true).and("yrCd").is(yearCode);
		criteria.and("invtype").in(invTypes);
		/*
		 * if(filter.getInvoiceType() == null){ criteria.and("invtype").in(invTypes); }
		 * applyFilterToCriteria(criteria, filter);
		 */
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = getProjectionForTotal("mthCd");
		GroupOperation groupOperation = getGroupForTotal("mthCd");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return  results.getMappedResults();
	}
	
	
	public List<String> getMultimonthBillToNames(String clientId, List<String> invTypes,String yearCd){
		Criteria criteria = Criteria.where("clientid").is(clientId).and("isAmendment").is(true)
				.and("invtype").in(invTypes).and("yrCd").is(yearCd);
		
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}
	
	public List<String> getMultimonthBillToNamesClientIdIn(List<String> clientId, List<String> invTypes,String yearCd,int month,String fp){
		Criteria criteria = Criteria.where("clientid").in(clientId).and("isAmendment").is(true)
				.and("invtype").in(invTypes).and("yrCd").is(yearCd);
		if(month > 0) {
			criteria.and("fp").is(fp);
		}
		
		Query query = Query.query(criteria);
		List billToNames = mongoTemplate.getCollection(COLLECTION_NAME).distinct("billedtoname", query.getQueryObject());
		return (List<String>)billToNames;
	}

	public List<TotalInvoiceAmount> getConsolidatedMultimonthSummeryForYearMonthwise(final Client client, List<String> invTypes, String yearCd,InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("isAmendment").is(true).and("yrCd").is(yearCd);
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalitc").multiply("sftr").as("totalitc")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
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
				.sum("totalitc").as("totalITCAvailable").count().as("totalTransactions");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		List<TotalInvoiceAmount> totalInvAmount = results.getMappedResults();
		if(filter.getInvoiceType() == null){
			List<TotalInvoiceAmount> totalAmmendedInvAmount = getAmmendedMultimomthConsolidatedSummeryForYearMonthwise(client.getId().toString(),yearCd,filter);
			Page<? extends InvoiceParent> invoices = getMultimonthInvoices(client.getId().toString(),yearCd,null,filter);
			List<String> invoiceNumbers = Lists.newArrayList();
			if(isNotEmpty(invoices)) {
				for(InvoiceParent invoiceParent : invoices) {
					if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA)) {
						if(isNotEmpty(((GSTR2)invoiceParent).getB2ba()) && isNotEmpty(((GSTR2)invoiceParent).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoiceParent).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoiceParent).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoiceParent).getB2ba().get(0).getInv().get(0).getOinum())) {
							invoiceNumbers.add(((GSTR2)invoiceParent).getB2ba().get(0).getInv().get(0).getOinum().trim());
						}
					}else if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
						if(isNotEmpty(((GSTR2)invoiceParent).getCdna()) && isNotEmpty(((GSTR2)invoiceParent).getCdna().get(0)) && isNotEmpty(((GSTR2)invoiceParent).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoiceParent).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoiceParent).getCdna().get(0).getNt().get(0).getOntNum())) {
							invoiceNumbers.add(((GSTR2)invoiceParent).getCdna().get(0).getNt().get(0).getOntNum().trim());
						}
					}
				}
			}
			Page<? extends InvoiceParent> originalinvoices = getMultimonthOriginalInvoices(client.getId().toString(),invoiceNumbers,null,filter);
			if(isNotEmpty(invoices) && isNotEmpty(originalinvoices) && invoices.getTotalElements() > 0 && originalinvoices.getTotalElements() > 0) {
				for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalAmmendedInvAmount){
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
							gstr1InvoiceAmount.setTcsTdsAmount(gstr1InvoiceAmount.getTcsTdsAmount().subtract(gstr1CancelledInvoiceAmount.getTcsTdsAmount()));
						}
					}
				}
				for(InvoiceParent invoiceParent: invoices) {
					String invoicenumber = "";
					String monthcode = invoiceParent.getMthCd();
					if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA)) {
						if(isNotEmpty(((GSTR2)invoiceParent).getB2ba()) && isNotEmpty(((GSTR2)invoiceParent).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoiceParent).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoiceParent).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoiceParent).getB2ba().get(0).getInv().get(0).getOinum())) {
							invoicenumber = ((GSTR2)invoiceParent).getB2ba().get(0).getInv().get(0).getOinum().trim();
						}
					}else if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
						if(isNotEmpty(((GSTR2)invoiceParent).getCdna()) && isNotEmpty(((GSTR2)invoiceParent).getCdna().get(0)) && isNotEmpty(((GSTR2)invoiceParent).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoiceParent).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoiceParent).getCdna().get(0).getNt().get(0).getOntNum())) {
							invoicenumber = ((GSTR2)invoiceParent).getCdna().get(0).getNt().get(0).getOntNum().trim();
						}
					}
					for(InvoiceParent originalinvoice : originalinvoices) {
						if(isNotEmpty(invoicenumber) && isNotEmpty(originalinvoice.getInvoiceno()) && invoicenumber.equals(originalinvoice.getInvoiceno().trim())) {
							for(TotalInvoiceAmount gstr1InvoiceAmount : totalAmmendedInvAmount){
								String code = gstr1InvoiceAmount.get_id();
								if(monthcode.equals(code)) {
									int sftr = 1;
									if(isNotEmpty(originalinvoice.getSftr())) {
										sftr = originalinvoice.getSftr();
									}
									if(isNotEmpty(originalinvoice.getTotaltaxableamount())) {
										gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().subtract(BigDecimal.valueOf(originalinvoice.getTotaltaxableamount()*sftr)));
									}
									if(isNotEmpty(originalinvoice.getTotalExemptedAmount())) {
										gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().subtract(BigDecimal.valueOf(originalinvoice.getTotalExemptedAmount()*sftr)));
									}
									if(isNotEmpty(originalinvoice.getTotaltax())) {
										gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().subtract(BigDecimal.valueOf(originalinvoice.getTotaltax()*sftr)));
									}
									if(isNotEmpty(originalinvoice.getTotalamount())) {
										gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().subtract(BigDecimal.valueOf(originalinvoice.getTotalamount()*sftr)));
									}
									if(isNotEmpty(originalinvoice.getTotalIgstAmount())) {
										gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().subtract(BigDecimal.valueOf(originalinvoice.getTotalIgstAmount()*sftr)));
									}
									if(isNotEmpty(originalinvoice.getTotalCgstAmount())) {
										gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().subtract(BigDecimal.valueOf(originalinvoice.getTotalCgstAmount()*sftr)));
									}
									if(isNotEmpty(originalinvoice.getTotalSgstAmount())) {
										gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().subtract(BigDecimal.valueOf(originalinvoice.getTotalSgstAmount()*sftr)));
									}
									if(isNotEmpty(originalinvoice.getTotalCessAmount())) {
										gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().subtract(BigDecimal.valueOf(originalinvoice.getTotalCessAmount()*sftr)));
									}
									if(isNotEmpty(originalinvoice.getTcstdsAmount())) {
										gstr1InvoiceAmount.setTcsTdsAmount(gstr1InvoiceAmount.getTcsTdsAmount().subtract(BigDecimal.valueOf(originalinvoice.getTcstdsAmount()*sftr)));
									}
								}
							}
						}
					}
				}
				for(TotalInvoiceAmount gstr1CancelledInvoiceAmount : totalAmmendedInvAmount){
					String cancode = gstr1CancelledInvoiceAmount.get_id();
					for(TotalInvoiceAmount gstr1InvoiceAmount : totalInvAmount){
						String code = gstr1InvoiceAmount.get_id();
						if(cancode.equals(code)) {
							gstr1InvoiceAmount.setTotalTaxableAmount(gstr1InvoiceAmount.getTotalTaxableAmount().add(gstr1CancelledInvoiceAmount.getTotalTaxableAmount()));
							gstr1InvoiceAmount.setTotalExemptedAmount(gstr1InvoiceAmount.getTotalExemptedAmount().add(gstr1CancelledInvoiceAmount.getTotalExemptedAmount()));
							gstr1InvoiceAmount.setTotalTaxAmount(gstr1InvoiceAmount.getTotalTaxAmount().add(gstr1CancelledInvoiceAmount.getTotalTaxAmount()));
							gstr1InvoiceAmount.setTotalAmount(gstr1InvoiceAmount.getTotalAmount().add(gstr1CancelledInvoiceAmount.getTotalAmount()));
							gstr1InvoiceAmount.setTotalIGSTAmount(gstr1InvoiceAmount.getTotalIGSTAmount().add(gstr1CancelledInvoiceAmount.getTotalIGSTAmount()));
							gstr1InvoiceAmount.setTotalCGSTAmount(gstr1InvoiceAmount.getTotalCGSTAmount().add(gstr1CancelledInvoiceAmount.getTotalCGSTAmount()));
							gstr1InvoiceAmount.setTotalSGSTAmount(gstr1InvoiceAmount.getTotalSGSTAmount().add(gstr1CancelledInvoiceAmount.getTotalSGSTAmount()));
							gstr1InvoiceAmount.setTotalCESSAmount(gstr1InvoiceAmount.getTotalCESSAmount().add(gstr1CancelledInvoiceAmount.getTotalCESSAmount()));
							gstr1InvoiceAmount.setTcsTdsAmount(gstr1InvoiceAmount.getTcsTdsAmount().add(gstr1CancelledInvoiceAmount.getTcsTdsAmount()));
						}
					}
				}
			}
		}
		return  totalInvAmount;
	}
	
	
	public List<TotalInvoiceAmount> getConsolidatedMultimonthSummeryForYearMonthwisenew(final Client client, List<String> invTypes, String yearCd,InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").is(client.getId().toString()).and("isAmendment").is(true).and("yrCd").is(yearCd);
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalitc").multiply("sftr").as("totalitc")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
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
				.sum("totalitc").as("totalITCAvailable").count().as("totalTransactions");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return  results.getMappedResults();
	}
public List<TotalInvoiceAmount> getConsolidatedMultimonthSummeryForYearMonthwiseClientidIn(List<String> clientids, List<String> invTypes, String yearCd,InvoiceFilter filter){
		Criteria criteria = Criteria.where("clientid").in(clientids).and("invtype").in(invTypes)
				.and("isAmendment").is(true).and("yrCd").is(yearCd);
		applyFilterToCriteria(criteria, filter);
		MatchOperation matchOperation = Aggregation.match(criteria);
		ProjectionOperation projectionOperation = Aggregation.project().and("totaltaxableamount").multiply("sftr").as("totaltaxableamount")
				.and("totaltax").multiply("sftr").as("totaltax")
				.and("totalitc").multiply("sftr").as("totalitc")
				.and("totalamount").multiply("sftr").as("totalamount")
				.and("totalIgstAmount").multiply("sftr").as("totalIgstAmount")
				.and("totalCgstAmount").multiply("sftr").as("totalCgstAmount")
				.and("totalSgstAmount").multiply("sftr").as("totalSgstAmount")
				.and("totalCessAmount").multiply("sftr").as("totalCessAmount")
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
				.sum("totalitc").as("totalITCAvailable").count().as("totalTransactions");
		AggregationResults<TotalInvoiceAmount> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), COLLECTION_NAME, TotalInvoiceAmount.class);
		return  results.getMappedResults();
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
	
	private void applyFilterToCriteria(Criteria criteria, InvoiceFilter filter){
		if(filter != null){
			List<Criteria> criterias = new ArrayList<Criteria>();
			if(filter.getPaymentStatus() != null){
				String fieldName = "books".equals(filter.getBooksOrReturns()) ? "paymentStatus":"gstStatus";
				List<String> statusList = Arrays.asList(filter.getPaymentStatus());
				if(statusList.contains("Not Paid") || statusList.contains("Pending")){
					Criteria criteriaa=new Criteria().andOperator(new Criteria().orOperator(Criteria.where(fieldName).in(statusList),Criteria.where(fieldName).is(""), Criteria.where(fieldName).is(null)));	
					criterias.add(criteriaa);
				}else{
					criteria.and(fieldName).in(statusList);
				}
				
			}
			/*if(filter.getInvoiceType() != null){
				criteria.and("invtype").in(Arrays.asList(filter.getInvoiceType()));
			}*/
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
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList),Criteria.where("cdnur.0.ntty").in(creditDebitURList)));
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList),Criteria.where("cdnur.0.ntty").in(creditDebitURList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList) && NullUtil.isNotEmpty(creditDebitURList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdnur.0.ntty").in(creditDebitURList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isEmpty(creditDebitURList)) {
					criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("invtype").in(invTypeList),Criteria.where("cdn.0.nt.0.ntty").in(creditDebitList)));
				}else if(NullUtil.isNotEmpty(invTypeList) && NullUtil.isEmpty(creditDebitList) && NullUtil.isEmpty(creditDebitURList)) {
					criteria.and("invtype").in(invTypeList);
				}else if(NullUtil.isEmpty(invTypeList) && NullUtil.isNotEmpty(creditDebitList) && NullUtil.isEmpty(creditDebitURList)) {
					criteria.and("cdn.0.nt.0.ntty").in(creditDebitList);
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
			if(filter.getReverseCharge() != null){
				List<String> reverseChargeList = Arrays.asList(filter.getReverseCharge());
				if(reverseChargeList.contains("Regular")) {
					Criteria criteriaa=new Criteria().andOperator(new Criteria().orOperator(Criteria.where("revchargetype").in(Arrays.asList(filter.getReverseCharge())),Criteria.where("revchargetype").is(""), Criteria.where("revchargetype").is(null)));
					criterias.add(criteriaa);
				}else {
					criteria.and("revchargetype").in(reverseChargeList);
				}
			}
			if(filter.getGstno() != null){
				criteria.and("b2b.0.ctin").in(Arrays.asList(filter.getGstno()));
			}
			if(filter.getDateofInvoice() != null){
				criteria.and("dateofinvoice_str").in(Arrays.asList(filter.getDateofInvoice()));
			}
			if(filter.getInvoiceno() != null){
				criteria.and("invoiceno").in(Arrays.asList(filter.getInvoiceno()));
			}
			if(filter.getGstr2aFilingStatus() != null) {
				Criteria criteriaa = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("b2b.0.cfs").in(Arrays.asList(filter.getGstr2aFilingStatus())),Criteria.where("cdn.0.cfs").in(Arrays.asList(filter.getGstr2aFilingStatus())),Criteria.where("b2ba.0.cfs").in(Arrays.asList(filter.getGstr2aFilingStatus())),Criteria.where("cdna.0.cfs").in(Arrays.asList(filter.getGstr2aFilingStatus()))));
				criterias.add(criteriaa);
			}
			if(NullUtil.isNotEmpty(criterias)) {
				criteria.andOperator(criterias.toArray(new Criteria[criterias.size()]));				
			}
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
 		criterias.add(Criteria.where("impGoods.0.stin").regex(searchVal, "i"));
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
	public Page<GSTR2> findByClientidInAndMonthAndYear(List<String> clientids, List<String> invTypes, int month ,String yearCode, int start, int length, String searchVal, String booksorReturns,InvoiceFilter filter) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("isAmendment").is(true).and("yrCd").is(yearCode);
				//.and("yrCd").is(yearCode).and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		if(filter != null && filter.getInvoiceType() == null){
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
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME));
		}else {
			addAllInvoicesQueryFirlds(query);
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
			}
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable, total);
		}
	}

	public Page<? extends InvoiceParent> findByClientidInAndMonthAndYear(List<String> clientids, List<String> invTypes, Date stDate, Date endDate,int start, int length, String searchVal, String booksorReturns,InvoiceFilter filter) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("isAmendment").is(true).and("invtype").in(invTypes).and("dateofinvoice").gte(stDate).lte(endDate);
		if(booksorReturns.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		if(filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME));
		}else {
			addAllInvoicesQueryFirlds(query);
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
			if (total == 0) {
				return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
			}
			return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable, total);
		}
	}
	
	
	public Page<? extends InvoiceParent> findByClientidAndIsAmendmentAndInvtypeInAndMatchingStatusInAndFpIn(String clientid, List<String> invTypes, List<String> gstr2status,List<String> fps,String gstr2id,int j,Pageable pageable) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("isAmendment").is(true).and("invtype").in(invTypes).and("matchingStatus").in(gstr2status).and("fp").in(fps);
		/*if(isNotEmpty(gstr2id)) {
			ObjectId objID = new ObjectId(gstr2id);
			criteria.and("_id").gt(objID);
		}*/
		Query query = Query.query(criteria);
		Sort sort = null;
		sort = new Sort(new Order(Direction.DESC, "_id"));
		Pageable pageable1 = new PageRequest(j, 5000,sort);
		query.with(pageable1);
		
		query.skip(j*5000);
		//query.limit(5000);
		long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
		}
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable1, total);
	}
	
	
	public Page<GSTR2> findByClientidInAndMonthAndYear(List<String> clientids, List<String> invTypes, int month ,String yearCode, int start, int length, String searchVal, String booksorReturns,InvoiceFilter filter,Pageable pageable) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("isAmendment").is(true).and("yrCd").is(yearCode);
				//.and("yrCd").is(yearCode).and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		if(filter != null && filter.getInvoiceType() == null){
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
		long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
		}
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable, total);
	}
	
	public Page<? extends InvoiceParent> findByClientidInAndMonthAndYear(List<String> clientids, List<String> invTypes, Date stDate, Date endDate,int start, int length, String searchVal, String booksorReturns,InvoiceFilter filter,Pageable pageable) {
		Criteria criteria = Criteria.where("clientid").in(clientids).and("isAmendment").is(true).and("dateofinvoice").gte(stDate).lte(endDate);
		if(booksorReturns.equalsIgnoreCase("dwnldfromgstn")) {
			criteria.and("govtInvoiceStatus").is(MasterGSTConstants.SUCCESS);
		}
		if(filter != null && filter.getInvoiceType() == null){
			criteria.and("invtype").in(invTypes);
		}
		applyFilterToCriteria(criteria, filter);
		if(StringUtils.hasLength(searchVal)){					
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
		}
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable, total);
	}
	
	public Page<GSTR2> findInvoicesForReconcile(String clientid, String invType, List<String> rtarray,List<String> invoicenoin,Pageable pageable) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("invtype").is(invType).and("fp").in(rtarray).and("invoiceno").in(invoicenoin);
		Query query = Query.query(criteria);
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
		}
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable, total);
	}
	
	public Page<? extends InvoiceParent> findInvoicesForReconcile(String clientid, List<String> invTypes, Date stDate, Date endDate,List<String> matchingstatus,Pageable pageable) {
		Criteria criteria = Criteria.where("clientid").in(clientid).and("invtype").is(invTypes).and("isAmendment").is(true).and("dateofinvoice").gte(stDate).lte(endDate).and("matchingStatus").in(matchingstatus);
		Query query = Query.query(criteria);
		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR2.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<GSTR2>(Collections.<GSTR2> emptyList());
		}
		return new PageImpl<GSTR2>(mongoTemplate.find(query, GSTR2.class, COLLECTION_NAME), pageable, total);
	}

}
