package com.mastergst.usermanagement.runtime.domain.gstr2b;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GSTR2BIMPGSEZ {
	@Id
	private ObjectId id;
	private String ctin;
	private String trdnm;
	private List<GSTR2BIMPG> boe=LazyList.decorate(new ArrayList<GSTR2BIMPG>(),FactoryUtils.instantiateFactory(GSTR2BIMPG.class));
	
	public GSTR2BIMPGSEZ() {
		this.id = ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getCtin() {
		return ctin;
	}
	public void setCtin(String ctin) {
		this.ctin = ctin;
	}
	public String getTrdnm() {
		return trdnm;
	}
	public void setTrdnm(String trdnm) {
		this.trdnm = trdnm;
	}
	public List<GSTR2BIMPG> getBoe() {
		return boe;
	}
	public void setBoe(List<GSTR2BIMPG> boe) {
		this.boe = boe;
	}
}
