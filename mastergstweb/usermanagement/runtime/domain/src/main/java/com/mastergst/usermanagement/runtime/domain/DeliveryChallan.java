package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Document(collection = "deliverychallan")
@JsonFilter("gstr1Filter")
public class DeliveryChallan extends InvoiceParent {
	
	
	List<DeliveryChallana> dc=LazyList.decorate(new ArrayList<DeliveryChallana>(), 
			FactoryUtils.instantiateFactory(DeliveryChallana.class));

	public List<DeliveryChallana> getDc() {
		return dc;
	}

	public void setDc(List<DeliveryChallana> dc) {
		this.dc = dc;
	}
}
