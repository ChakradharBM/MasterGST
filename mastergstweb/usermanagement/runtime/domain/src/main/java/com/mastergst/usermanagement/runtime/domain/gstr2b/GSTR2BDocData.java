package com.mastergst.usermanagement.runtime.domain.gstr2b;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GSTR2BDocData {
	@Id
	private ObjectId id;	
	private List<GSTR2BB2B> b2b=LazyList.decorate(new ArrayList<GSTR2BB2B>(),FactoryUtils.instantiateFactory(GSTR2BB2B.class));
	private List<GSTR2BB2B> b2ba=LazyList.decorate(new ArrayList<GSTR2BB2B>(),FactoryUtils.instantiateFactory(GSTR2BB2B.class));
	private List<GSTR2BCDN> cdnr=LazyList.decorate(new ArrayList<GSTR2BCDN>(),FactoryUtils.instantiateFactory(GSTR2BCDN.class));
	private List<GSTR2BCDN> cdnra=LazyList.decorate(new ArrayList<GSTR2BCDN>(),FactoryUtils.instantiateFactory(GSTR2BCDN.class));
	private List<GSTR2BISD> isd=LazyList.decorate(new ArrayList<GSTR2BISD>(),FactoryUtils.instantiateFactory(GSTR2BISD.class));
	private List<GSTR2BISD> isda=LazyList.decorate(new ArrayList<GSTR2BISD>(),FactoryUtils.instantiateFactory(GSTR2BISD.class));
	private List<GSTR2BIMPG> impg=LazyList.decorate(new ArrayList<GSTR2BIMPG>(),FactoryUtils.instantiateFactory(GSTR2BIMPG.class));
	private List<GSTR2BIMPGSEZ> impgsez=LazyList.decorate(new ArrayList<GSTR2BIMPGSEZ>(),FactoryUtils.instantiateFactory(GSTR2BIMPGSEZ.class));
	
	public GSTR2BDocData() {
		this.id = ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public List<GSTR2BB2B> getB2b() {
		return b2b;
	}
	public void setB2b(List<GSTR2BB2B> b2b) {
		this.b2b = b2b;
	}
	public List<GSTR2BB2B> getB2ba() {
		return b2ba;
	}
	public void setB2ba(List<GSTR2BB2B> b2ba) {
		this.b2ba = b2ba;
	}
	public List<GSTR2BCDN> getCdnr() {
		return cdnr;
	}
	public void setCdnr(List<GSTR2BCDN> cdnr) {
		this.cdnr = cdnr;
	}
	public List<GSTR2BCDN> getCdnra() {
		return cdnra;
	}
	public void setCdnra(List<GSTR2BCDN> cdnra) {
		this.cdnra = cdnra;
	}
	public List<GSTR2BISD> getIsd() {
		return isd;
	}
	public void setIsd(List<GSTR2BISD> isd) {
		this.isd = isd;
	}
	public List<GSTR2BISD> getIsda() {
		return isda;
	}
	public void setIsda(List<GSTR2BISD> isda) {
		this.isda = isda;
	}
	public List<GSTR2BIMPG> getImpg() {
		return impg;
	}
	public void setImpg(List<GSTR2BIMPG> impg) {
		this.impg = impg;
	}
	public List<GSTR2BIMPGSEZ> getImpgsez() {
		return impgsez;
	}
	public void setImpgsez(List<GSTR2BIMPGSEZ> impgsez) {
		this.impgsez = impgsez;
	}

}
