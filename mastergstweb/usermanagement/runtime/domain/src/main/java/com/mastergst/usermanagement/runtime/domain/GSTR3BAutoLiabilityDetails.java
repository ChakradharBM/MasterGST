package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GSTR3BAutoLiabilityDetails {
	@Id
	private ObjectId id;
	private GSTR3BSubTotals subtotal;
    private GSTR3BDet det;
	public GSTR3BAutoLiabilityDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public GSTR3BSubTotals getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(GSTR3BSubTotals subtotal) {
		this.subtotal = subtotal;
	}

	public GSTR3BDet getDet() {
		return det;
	}

	public void setDet(GSTR3BDet det) {
		this.det = det;
	}
}
