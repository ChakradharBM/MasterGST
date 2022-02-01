package com.mastergst.usermanagement.runtime.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.mastergst.usermanagement.runtime.domain.ApisVersionFilter;
import com.mastergst.usermanagement.runtime.domain.ApisVesrion;
import com.mastergst.usermanagement.runtime.domain.GSTR1;

@Component
public class ApisVesrionsDao {

	private static final String COLLECTION_NAME = "apis_version";
	@Autowired
	private MongoTemplate mongoTemplate;

	
	public ApisVesrion getApisVersionInfo(String docid) {

		Criteria criteria = Criteria.where("_id").is(docid);
		Query query = Query.query(criteria);
		ApisVesrion api = mongoTemplate.findOne(query, ApisVesrion.class);

		return api;
	}

	public Page<ApisVesrion> getApisVersions(String apiType, int start, int length, String searchVal,
			ApisVersionFilter filter) {

		Criteria criteria = Criteria.where("api").is(apiType);

		applyFilterToCriteria(criteria, filter);
		if (StringUtils.hasLength(searchVal)) {
			criteria = new Criteria().andOperator(criteria, getSearchValueCriteria(searchVal));
		}
		Query query = Query.query(criteria);
		if (length != -1) {
			addAllInvoicesQueryFirlds(query);
		}
		if (length == -1) {
			length = Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest((start / length), length);

		query.with(pageable);
		long total = mongoTemplate.count(query, GSTR1.class, COLLECTION_NAME);
		if (total == 0) {
			return new PageImpl<ApisVesrion>(Collections.<ApisVesrion>emptyList());
		}
		return new PageImpl<ApisVesrion>(mongoTemplate.find(query, ApisVesrion.class, COLLECTION_NAME),
				pageable, total);
	}

	private Criteria getSearchValueCriteria(final String searchVal) {
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(Criteria.where("type").regex(searchVal, "i"));
		criterias.add(Criteria.where("name").regex(searchVal, "i"));
		criterias.add(Criteria.where("method").regex(searchVal, "i"));
		criterias.add(Criteria.where("sandboxVersion").regex(searchVal, "i"));
		criterias.add(Criteria.where("productionVersion").regex(searchVal, "i"));
		if(searchVal.trim() != null &&  searchVal.trim() !="") {
			if(searchVal.toLowerCase().contains("f") || searchVal.toLowerCase().contains("a") || searchVal.toLowerCase().contains("l") || searchVal.toLowerCase().contains("s") || searchVal.toLowerCase().contains("e")) {
				criterias.add(Criteria.where("webimpl").exists(false));
			}
			if(searchVal.toLowerCase().contains("t") || searchVal.toLowerCase().contains("r") || searchVal.toLowerCase().contains("u") || searchVal.toLowerCase().contains("e")) {
				criterias.add(Criteria.where("webimpl").exists(true));			
			}			
		}
		
		return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}

	private void applyFilterToCriteria(Criteria criteria, ApisVersionFilter filter) {
		if (filter != null) {
			if (filter.getType() != null) {
				criteria.and("type").in(Arrays.asList(filter.getType()));
			}
			if (filter.getName() != null) {
				criteria.and("name").in(Arrays.asList(filter.getName()));
			}
			if (filter.getMethod() != null) {
				criteria.and("method").in(Arrays.asList(filter.getMethod()));
			}
			if (filter.getSandboxVersion() != null) {
				criteria.and("sandboxVersion").in(Arrays.asList(filter.getSandboxVersion()));
			}
			if (filter.getProductionVersion() != null) {
				criteria.and("productionVersion").in(Arrays.asList(filter.getProductionVersion()));
			}
			if (filter.getWebimpl() != null) {
				criteria.and("webimpl").in(Arrays.asList(filter.getWebimpl()));
			}
		}

	}

	private void addAllInvoicesQueryFirlds(Query query) {
		query.fields().include("api");
		query.fields().include("type");
		query.fields().include("name");
		query.fields().include("method");
		query.fields().include("sandboxVersion");
		query.fields().include("productionVersion");
		query.fields().include("webimpl");
	}
}
