package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.mastergst.usermanagement.runtime.domain.gstr4annual.GSTR4CMPSummary;
import com.mastergst.usermanagement.runtime.domain.gstr4annual.GSTR4TdsTcsSummary;
public class GSTR4CMPData {
	
	@Id
	private ObjectId id;
	
	private GSTR4CMPSummary cmpsmry;
	private List<GSTR4TdsTcsSummary> tdstcs;
	
	public GSTR4CMPData() {
		this.id = ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public GSTR4CMPSummary getCmpsmry() {
		return cmpsmry;
	}
	public void setCmpsmry(GSTR4CMPSummary cmpsmry) {
		this.cmpsmry = cmpsmry;
	}
	public  List<GSTR4TdsTcsSummary> getTdstcs() {
		return tdstcs;
	}
	public void setTdstcs( List<GSTR4TdsTcsSummary> tdstcs) {
		this.tdstcs = tdstcs;
	}

}
