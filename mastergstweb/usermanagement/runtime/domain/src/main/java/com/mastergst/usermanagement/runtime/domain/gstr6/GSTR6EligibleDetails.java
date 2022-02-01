package com.mastergst.usermanagement.runtime.domain.gstr6;

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
public class GSTR6EligibleDetails {
	@Id
	private ObjectId id;
	private String typ;
	private String cpty;
	private String statecd;
	//For ISDA
	private String rcpty;
	private String rstatecd;
	private List<GSTR6DocDetails> doclst =  LazyList.decorate(new ArrayList<GSTR6DocDetails>(), FactoryUtils.instantiateFactory(GSTR6DocDetails.class));
	public GSTR6EligibleDetails() {
		this.id = ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getTyp() {
		return typ;
	}
	public void setTyp(String typ) {
		this.typ = typ;
	}
	public String getCpty() {
		return cpty;
	}
	public void setCpty(String cpty) {
		this.cpty = cpty;
	}
	public String getStatecd() {
		return statecd;
	}
	public void setStatecd(String statecd) {
		this.statecd = statecd;
	}
	public String getRcpty() {
		return rcpty;
	}
	public void setRcpty(String rcpty) {
		this.rcpty = rcpty;
	}
	public String getRstatecd() {
		return rstatecd;
	}
	public void setRstatecd(String rstatecd) {
		this.rstatecd = rstatecd;
	}
	public List<GSTR6DocDetails> getDoclst() {
		return doclst;
	}
	public void setDoclst(List<GSTR6DocDetails> doclst) {
		this.doclst = doclst;
	}

}
