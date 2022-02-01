package com.mastergst.usermanagement.runtime.response.gstr4;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GSTR4CMPSummary {
	
	@Id
	private ObjectId id;
	
	private String isnil;
	private GSTR4OutwardSupply out_sup;
	private GSTR4InwardSupply in_sup;
	private GSTR4InterStateSupply intr_pay;
	private GSTR4InwardSupply tax_pay;
	
	public GSTR4CMPSummary() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getIsnil() {
		return isnil;
	}

	public void setIsnil(String isnil) {
		this.isnil = isnil;
	}

	public GSTR4OutwardSupply getOut_sup() {
		return out_sup;
	}

	public void setOut_sup(GSTR4OutwardSupply out_sup) {
		this.out_sup = out_sup;
	}

	public GSTR4InwardSupply getIn_sup() {
		return in_sup;
	}

	public void setIn_sup(GSTR4InwardSupply in_sup) {
		this.in_sup = in_sup;
	}

	public GSTR4InterStateSupply getIntr_pay() {
		return intr_pay;
	}

	public void setIntr_pay(GSTR4InterStateSupply intr_pay) {
		this.intr_pay = intr_pay;
	}

	public GSTR4InwardSupply getTax_pay() {
		return tax_pay;
	}

	public void setTax_pay(GSTR4InwardSupply tax_pay) {
		this.tax_pay = tax_pay;
	}

}
