package com.mastergst.usermanagement.runtime.domain.gstr2b;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GSTR2BISD {
	@Id
	private ObjectId id;
	private String ctin;
	private String trdnm;
	private String supprd;
	private String supfildt;
	private List<GSTR2BDocList> doclist = LazyList.decorate(new ArrayList<GSTR2BDocList>(),
			FactoryUtils.instantiateFactory(GSTR2BDocList.class));

	
	public GSTR2BISD() {
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

	public String getSupprd() {
		return supprd;
	}

	public void setSupprd(String supprd) {
		this.supprd = supprd;
	}

	public String getSupfildt() {
		return supfildt;
	}

	public void setSupfildt(String supfildt) {
		this.supfildt = supfildt;
	}

	public List<GSTR2BDocList> getDoclist() {
		return doclist;
	}

	public void setDoclist(List<GSTR2BDocList> doclist) {
		this.doclist = doclist;
	}
}
