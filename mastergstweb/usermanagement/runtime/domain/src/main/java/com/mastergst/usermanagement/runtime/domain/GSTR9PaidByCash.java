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
public class GSTR9PaidByCash {
	
	@Id
	private ObjectId id;
	
	@JsonProperty("pd_by_cash")		
	List<GSTR9DebitDetails> pdByCash = LazyList.decorate(new ArrayList<GSTR9DebitDetails>(), 
			FactoryUtils.instantiateFactory(GSTR9DebitDetails.class));
	
	public GSTR9PaidByCash() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<GSTR9DebitDetails> getPdByCash() {
		return pdByCash;
	}

	public void setPdByCash(List<GSTR9DebitDetails> pdByCash) {
		this.pdByCash = pdByCash;
	}

	
}
