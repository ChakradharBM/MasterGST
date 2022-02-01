package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CompanyItems;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.StockAdjustments;
import com.mastergst.usermanagement.runtime.domain.StockItems;
import com.mastergst.usermanagement.runtime.domain.TotalStocksAmounts;

@Service
public class InventoryDao {
	@Autowired
	protected MongoTemplate mongoTemplate;
	
	public Page<? extends StockAdjustments> findByClientid(String clientid, String itemname,  int month, String yearCode, int start, int length, String searchVal, Pageable pageable) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("stockItemNo").is(itemname);
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllInvoicesQueryFields(query);
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		//Pageable pageable = new PageRequest((start/length), length);
		query.with(pageable);
		long total = mongoTemplate.count(query, StockAdjustments.class, "stockAdjustments");
		if (total == 0) {
			return new PageImpl<StockAdjustments>(Collections.<StockAdjustments> emptyList());
		}
		return new PageImpl<StockAdjustments>(mongoTemplate.find(query, StockAdjustments.class, "stockAdjustments"), pageable, total);
	}
	private void addAllItemsQueryFields(Query query) {
		query.fields().include("fullname");
		query.fields().include("description");
		query.fields().include("unit");
		query.fields().include("totalQtyUsage");
		query.fields().include("itemno");
		query.fields().include("stocklevel");
		query.fields().include("salePrice");
		query.fields().include("purchasePrice");
		query.fields().include("currentStock");
	}
	private Criteria getItemSearchValueCriteria(String searchVal) {
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(Criteria.where("description").regex(searchVal, "i"));
 		criterias.add(Criteria.where("unit").regex(searchVal, "i"));
 		criterias.add(Criteria.where("totalQtyUsage").regex(searchVal, "i"));
 		criterias.add(Criteria.where("itemno").regex(searchVal, "i"));
 		criterias.add(Criteria.where("stocklevel").regex(searchVal, "i"));
 		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}
	private void addAllInvoicesQueryFields(Query query) {
		query.fields().include("fullname");
		query.fields().include("stockItemNo");
		query.fields().include("stockItemName");
		query.fields().include("stockMovement");
		query.fields().include("stockItemQty");
		query.fields().include("stockPoNo");
		query.fields().include("currentStock");
		query.fields().include("dateOfMovement");
		query.fields().include("transactionType");
		query.fields().include("stockUnit");
	}

	private Criteria getSearchValueCriteria(String searchval) {
		String searchVal = searchval;
		if(searchVal.contains("(")) {
			searchVal = searchVal.replaceAll("\\(", "\\\\(");
		}
		if(searchVal.contains(")")) {
			searchVal = searchVal.replaceAll("\\)", "\\\\)");
		}
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(Criteria.where("stockItemNo").regex(searchVal, "i"));
 		criterias.add(Criteria.where("stockMovement").regex(searchVal, "i"));
 		criterias.add(Criteria.where("stockItemQty").regex(searchVal, "i"));
 		criterias.add(Criteria.where("stockPoNo").regex(searchVal, "i"));
 		criterias.add(Criteria.where("currentStock").regex(searchVal, "i"));
 		criterias.add(Criteria.where("dateOfMovement").regex(searchVal, "i"));
 		criterias.add(Criteria.where("transactionType").regex(searchVal, "i"));
 		criterias.add(Criteria.where("stockUnit").regex(searchVal, "i"));
 		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}

	public Page<? extends StockAdjustments> findByClientidAndFromtimeAndTotime(String clientid, String itemname, Date stDate, Date endDate, int start, int length, String searchVal) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("dateOfMovement").gte(stDate).lte(endDate).and("stockItemNo").is(itemname);
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		
		if(start == 0 && length == 0) {
			return new PageImpl<StockAdjustments>(mongoTemplate.find(query, StockAdjustments.class, "stockAdjustments"));			
		}else {
			if(length != -1) {
				addAllInvoicesQueryFields(query);
			}
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, StockAdjustments.class, "stockAdjustments");
			if (total == 0) {
				return new PageImpl<StockAdjustments>(Collections.<StockAdjustments> emptyList());
			}
			return new PageImpl<StockAdjustments>(mongoTemplate.find(query, StockAdjustments.class, "stockAdjustments"), pageable, total);
	   }
   }

	public Page<StockItems> getStockLedgerDetails(String clientid, String itemname, int month, String yearCode, int start, int length, String searchVal) {
		Pageable pageable = new PageRequest((start/length), length);
	    Aggregation aggregation = null;
	    Aggregation aggCount = null;
	    Criteria criteria = Criteria.where("stocks.clientid").is(clientid).and("stocks.yrCd").is(yearCode);
	    if(NullUtil.isNotEmpty(itemname)) {
	    	criteria.and("stocks.stockItemNo").is(itemname);
	    }
		if(month > 0) {
			criteria.and("stocks.mthCd").is(month+"");
		}
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
			aggregation = Aggregation.newAggregation(
    				Aggregation.lookup("stockAdjustments", "_id", "itemId", "stocks"),
    				Aggregation.match(criteria),
    				//Aggregation.unwind("stocks", false),
    				Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
       				Aggregation.limit(pageable.getPageSize())
       			);
    	aggCount = Aggregation.newAggregation(
    			Aggregation.lookup("stockAdjustments", "_id", "itemId", "stocks"),
				Aggregation.match(criteria),
				Aggregation.unwind("stocks", false)
    		);
		}else {
			aggregation = Aggregation.newAggregation(
					Aggregation.lookup("stockAdjustments", "_id", "itemId", "stocks"),
       				Aggregation.match(criteria),
       				//Aggregation.unwind("stocks", false),
       				Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
       				Aggregation.limit(pageable.getPageSize())
       			);
       	aggCount = Aggregation.newAggregation(
       			Aggregation.lookup("stockAdjustments", "_id", "itemId", "stocks"),
    			Aggregation.match(criteria)
    			//Aggregation.unwind("stocks", false)
    		);
		}
		 long total =  mongoTemplate.aggregate(aggCount, "companyitems", StockItems.class).getMappedResults().size();
		    
		    if (total == 0) {
				return new PageImpl<StockItems>(Collections.<StockItems> emptyList());
			}
		    
		    List<StockItems> aggregationResult = mongoTemplate.aggregate(aggregation, "companyitems", StockItems.class).getMappedResults();
	        return new PageImpl<StockItems>(aggregationResult, pageable, total);
	}

	public Page<StockItems> getCustomStockLedgerDetails(String clientid, String itemname, Date stDate, Date endDate, int start, int length, String searchVal) {
		//Criteria criteria = Criteria.where("clientid").is(clientid).and("dateOfMovement").gte(stDate).lte(endDate).and("stockItemNo").is(itemname);
		Pageable pageable = new PageRequest((start/length), length);
	    Aggregation aggregation = null;
	    Aggregation aggCount = null;
	    Criteria criteria = Criteria.where("stocks.clientid").is(clientid).and("stocks.dateOfMovement").gte(stDate).lte(endDate);
	    if(NullUtil.isNotEmpty(itemname)) {
	    	criteria.and("stocks.stockItemNo").is(itemname);
	    }
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
			aggregation = Aggregation.newAggregation(
    				Aggregation.lookup("stockAdjustments", "_id", "itemId", "stocks"),
    				Aggregation.match(criteria),
    				//Aggregation.unwind("stocks", false),
    				Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
       				Aggregation.limit(pageable.getPageSize())
       			);
    	aggCount = Aggregation.newAggregation(
    			Aggregation.lookup("stockAdjustments", "_id", "itemId", "stocks"),
				Aggregation.match(criteria),
				Aggregation.unwind("stocks", false)
    		);
		}else {
			aggregation = Aggregation.newAggregation(
					Aggregation.lookup("stockAdjustments", "_id", "itemId", "stocks"),
       				Aggregation.match(criteria),
       				//Aggregation.unwind("stocks", false),
       				Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
       				Aggregation.limit(pageable.getPageSize())
       			);
       	aggCount = Aggregation.newAggregation(
       			Aggregation.lookup("stockAdjustments", "_id", "itemId", "stocks"),
    			Aggregation.match(criteria)
    			//Aggregation.unwind("stocks", false)
    		);
		}
		 long total =  mongoTemplate.aggregate(aggCount, "companyitems", StockItems.class).getMappedResults().size();
		    if (total == 0) {
				return new PageImpl<StockItems>(Collections.<StockItems> emptyList());
			}
		    List<StockItems> aggregationResult = mongoTemplate.aggregate(aggregation, "companyitems", StockItems.class).getMappedResults();
	        return new PageImpl<StockItems>(aggregationResult, pageable, total);
	}

	public Page<? extends CompanyItems> getStockSalesSummaryDetails(String clientid, int month, String yearCode, int start, int length, String searchVal) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("itemType").is("Product");
		/*if(month > 0) {
			criteria.and("mthCd").is(month+"");
		}*/
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getItemSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0) {
			return new PageImpl<CompanyItems>(mongoTemplate.find(query, CompanyItems.class, "companyitems"));			
		}else {
			if(length != -1) {
				addAllItemsQueryFields(query);
			}
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, CompanyItems.class, "companyitems");
			if (total == 0) {
				return new PageImpl<CompanyItems>(Collections.<CompanyItems> emptyList());
			}
			return new PageImpl<CompanyItems>(mongoTemplate.find(query, CompanyItems.class, "companyitems"), pageable, total);
	   }
	}

	public TotalStocksAmounts getTotalQuantityAmounts(String clientid, int month, String yearCode, int start, int length, String searchVal) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("yrCd").is(yearCode).and("stockMovement").is("Out").and("transactionType").is("Sales Invoice");
		if(month > 0) {
			criteria.and("mthCd").is(month+"");
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
		AggregationResults<TotalStocksAmounts> results = mongoTemplate.aggregate(Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation), "stockAdjustments", TotalStocksAmounts.class);
		return results.getUniqueMappedResult();
	}

	private GroupOperation getGroupForTotal(String fieldname) {
		return Aggregation.group(fieldname).count().as("totalTransactions")
				.sum("stockItemQty").as("stockItemQty");
	}

	private ProjectionOperation getProjectionForTotal(String fieldname) {
		return Aggregation.project().andInclude(fieldname)
				.and("stockItemQty").multiply("sftr").as("stockItemQty");
	}
	public Page<? extends StockAdjustments> findByClientidAndFromtimeAndTotime(String clientid,  Date stDate, Date endDate, int start, int length, String searchVal) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("dateOfMovement").gte(stDate).lte(endDate).and("stockMovement").is("Out").and("transactionType").is("Sales Invoice");	
		
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if(start == 0 && length == 0){
			return new PageImpl<StockAdjustments>(mongoTemplate.find(query, StockAdjustments.class, "stockAdjustments"));
		}else {
			if(length != -1) {
				//addAllInvoicesQueryFields(query);			
			}
			if(length == -1) {
				length = Integer.MAX_VALUE;
			}
			Pageable pageable = new PageRequest((start/length), length);
			query.with(pageable);
			long total = mongoTemplate.count(query, StockAdjustments.class, "stockAdjustments");
			if (total == 0) {
				return new PageImpl<StockAdjustments>(Collections.<StockAdjustments> emptyList());
			}
			return new PageImpl<StockAdjustments>(mongoTemplate.find(query, StockAdjustments.class, "stockAdjustments"), pageable, total);
		}
	}
	public Page<? extends CompanyItems> getItemSummaryDetails(String clientid, int month, int year, int start, int length, String searchVal, Pageable pageable) {
		Criteria criteria = Criteria.where("clientid").is(clientid).and("itemType").is("Product");
		if(StringUtils.hasLength(searchVal)){
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		addAllItemsQueryFields(query);
		if(length == -1) {
			length = Integer.MAX_VALUE;
		}
		query.with(pageable);
		long total = mongoTemplate.count(query, CompanyCustomers.class, "companyitems");
		if (total == 0) {
			return new PageImpl<CompanyItems>(Collections.<CompanyItems> emptyList());
		}
		return new PageImpl<CompanyItems>(mongoTemplate.find(query, CompanyItems.class, "companyitems"), pageable, total);
	}
}
