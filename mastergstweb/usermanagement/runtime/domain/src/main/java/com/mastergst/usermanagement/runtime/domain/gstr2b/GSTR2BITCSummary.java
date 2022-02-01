package com.mastergst.usermanagement.runtime.domain.gstr2b;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GSTR2BITCSummary {
	@Id
	private ObjectId id;
	private GSTR2BITCAvailable itcavl;
	private GSTR2BITCAvailable itcunavl;

	
	
	public GSTR2BITCSummary() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public GSTR2BITCAvailable getItcavl() {
		return itcavl;
	}

	public void setItcavl(GSTR2BITCAvailable itcavl) {
		this.itcavl = itcavl;
	}

	public GSTR2BITCAvailable getItcunavl() {
		return itcunavl;
	}

	public void setItcunavl(GSTR2BITCAvailable itcunavl) {
		this.itcunavl = itcunavl;
	}

}
