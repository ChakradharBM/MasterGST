package com.mastergst.usermanagement.runtime.domain;

public class ProfileLedgerData {

	private String ledgergroupname;
	private TrailBalanceAmounts trailBalanceAmounts;

	public ProfileLedgerData() {
		super();
	}

	public ProfileLedgerData(String ledgergroupname, TrailBalanceAmounts trailBalanceAmounts) {
		super();
		this.ledgergroupname = ledgergroupname;
		this.trailBalanceAmounts = trailBalanceAmounts;
	}

	public String getLedgergroupname() {
		return ledgergroupname;
	}

	public void setLedgergroupname(String ledgergroupname) {
		this.ledgergroupname = ledgergroupname;
	}

	public TrailBalanceAmounts getTrailBalanceAmounts() {
		return trailBalanceAmounts;
	}

	public void setTrailBalanceAmounts(TrailBalanceAmounts trailBalanceAmounts) {
		this.trailBalanceAmounts = trailBalanceAmounts;
	}

	@Override
	public String toString() {
		return "ProfileLedgerData [ledgergroupname=" + ledgergroupname + ", trailBalanceAmounts=" + trailBalanceAmounts
				+ "]";
	}
}
