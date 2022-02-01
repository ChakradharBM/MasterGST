package com.mastergst.usermanagement.runtime.domain;

public class SubGroupsData {

	private String subgroupname;
	private TrailBalanceAmounts trailBalanceAmounts;
	private ProfileLedgerData ProfileLedgerData;

	public SubGroupsData() {
		super();
	}

	public SubGroupsData(String subgroupname, TrailBalanceAmounts trailBalanceAmounts) {
		super();
		this.subgroupname = subgroupname;
		this.trailBalanceAmounts = trailBalanceAmounts;
	}

	public SubGroupsData(String subgroupname, TrailBalanceAmounts trailBalanceAmounts,
			ProfileLedgerData profileLedgerData) {
		super();
		this.subgroupname = subgroupname;
		this.trailBalanceAmounts = trailBalanceAmounts;
		ProfileLedgerData = profileLedgerData;
	}

	public String getSubgroupname() {
		return subgroupname;
	}

	public void setSubgroupname(String subgroupname) {
		this.subgroupname = subgroupname;
	}

	public TrailBalanceAmounts getTrailBalanceAmounts() {
		return trailBalanceAmounts;
	}

	public void setTrailBalanceAmounts(TrailBalanceAmounts trailBalanceAmounts) {
		this.trailBalanceAmounts = trailBalanceAmounts;
	}

	public ProfileLedgerData getProfileLedgerData() {
		return ProfileLedgerData;
	}

	public void setProfileLedgerData(ProfileLedgerData profileLedgerData) {
		ProfileLedgerData = profileLedgerData;
	}

	@Override
	public String toString() {
		return "SubGroupsData [subgroupname=" + subgroupname + ", trailBalanceAmounts=" + trailBalanceAmounts
				+ ", ProfileLedgerData=" + ProfileLedgerData + "]";
	}
}
