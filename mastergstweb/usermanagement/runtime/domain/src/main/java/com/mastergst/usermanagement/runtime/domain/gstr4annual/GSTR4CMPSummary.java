package com.mastergst.usermanagement.runtime.domain.gstr4annual;

public class GSTR4CMPSummary {
	private String isnil;
	private GSTR4OutwardSupply out_sup;
	private GSTR4InwardSupply in_sup;
	private GSTR4InterStateSupply intr_pay;
	private GSTR4InwardSupply tax_pay;

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
