package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.google.api.client.util.Lists;

public class GSTR3BDet {
	@Id
	private ObjectId id;
	private GSTR3BItcTaxDetails itcavl;
	private GSTR3BItcTaxDetails itcunavl;
	private GSTR3BItcTaxDetails itcrev;
	private List<GSTR3BSubTotals> tbl4a = Lists.newArrayList();
	
	public GSTR3BDet() {
		this.id=ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public GSTR3BItcTaxDetails getItcavl() {
		return itcavl;
	}

	public void setItcavl(GSTR3BItcTaxDetails itcavl) {
		this.itcavl = itcavl;
	}

	public GSTR3BItcTaxDetails getItcunavl() {
		return itcunavl;
	}

	public void setItcunavl(GSTR3BItcTaxDetails itcunavl) {
		this.itcunavl = itcunavl;
	}

	public GSTR3BItcTaxDetails getItcrev() {
		return itcrev;
	}

	public void setItcrev(GSTR3BItcTaxDetails itcrev) {
		this.itcrev = itcrev;
	}

	public List<GSTR3BSubTotals> getTbl4a() {
		return tbl4a;
	}

	public void setTbl4a(List<GSTR3BSubTotals> tbl4a) {
		this.tbl4a = tbl4a;
	}
}
