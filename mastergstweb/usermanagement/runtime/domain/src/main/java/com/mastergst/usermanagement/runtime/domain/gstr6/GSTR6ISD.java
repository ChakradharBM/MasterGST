package com.mastergst.usermanagement.runtime.domain.gstr6;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR6ISD {
	@Id
	private ObjectId id;
	
	private GSTR6EligibleDetails elglst;
	private GSTR6EligibleDetails inelglst;
	public GSTR6ISD() {
		this.id = ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public GSTR6EligibleDetails getElglst() {
		return elglst;
	}
	public void setElglst(GSTR6EligibleDetails elglst) {
		this.elglst = elglst;
	}
	public GSTR6EligibleDetails getInelglst() {
		return inelglst;
	}
	public void setInelglst(GSTR6EligibleDetails inelglst) {
		this.inelglst = inelglst;
	}
	
}
