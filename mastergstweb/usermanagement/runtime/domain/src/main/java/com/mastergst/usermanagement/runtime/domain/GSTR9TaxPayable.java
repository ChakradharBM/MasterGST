package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9TaxPayable {
	
	@Id
	private ObjectId id;
	
	@JsonProperty("tax_pay")		
	List<GSTR9TaxPayableSection> taxPay = LazyList.decorate(new ArrayList<GSTR9TaxPayableSection>(), 
			FactoryUtils.instantiateFactory(GSTR9TaxPayableSection.class));
	
	public GSTR9TaxPayable() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<GSTR9TaxPayableSection> getTaxPay() {
		return taxPay;
	}

	public void setTaxPay(List<GSTR9TaxPayableSection> taxPay) {
		this.taxPay = taxPay;
	}
	
	
}
