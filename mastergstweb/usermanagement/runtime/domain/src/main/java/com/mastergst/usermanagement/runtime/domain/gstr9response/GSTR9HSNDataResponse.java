package com.mastergst.usermanagement.runtime.domain.gstr9response;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * This class is HSN Data POJO.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GSTR9HSNDataResponse {
	
	List<GSTR9HSNItemsResponse> items=LazyList.decorate(new ArrayList<GSTR9HSNItemsResponse>(), FactoryUtils.instantiateFactory(GSTR9HSNItemsResponse.class));

	public GSTR9HSNDataResponse() {

	}

	public List<GSTR9HSNItemsResponse> getItems() {
		return items;
	}

	public void setItems(List<GSTR9HSNItemsResponse> items) {
		this.items = items;
	}
}
