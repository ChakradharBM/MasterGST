package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.beust.jcommander.internal.Lists;

public class GSTR3BInterAutoLiabilityDetails {
	@Id
	private ObjectId id;
	private List<GSTR3BSubTotals> subtotal = Lists.newArrayList();
    private GSTR3BDet det;
    
	public GSTR3BInterAutoLiabilityDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<GSTR3BSubTotals> getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(List<GSTR3BSubTotals> subtotal) {
		this.subtotal = subtotal;
	}

	public GSTR3BDet getDet() {
		return det;
	}

	public void setDet(GSTR3BDet det) {
		this.det = det;
	}
}
