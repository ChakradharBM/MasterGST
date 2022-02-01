package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9CTable8Details {
	
	@Id
	private ObjectId id;
	
	private List<GSTR9CReasonDetails> rsn=LazyList.decorate(new ArrayList<GSTR9CReasonDetails>(), 
			FactoryUtils.instantiateFactory(GSTR9CReasonDetails.class));
	
	public GSTR9CTable8Details() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<GSTR9CReasonDetails> getRsn() {
		return rsn;
	}

	public void setRsn(List<GSTR9CReasonDetails> rsn) {
		this.rsn = rsn;
	}
}
