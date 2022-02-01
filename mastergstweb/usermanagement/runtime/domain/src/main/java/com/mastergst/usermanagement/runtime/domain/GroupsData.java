package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import com.google.common.collect.Lists;

public class GroupsData {

	private String groupname;
	private List<SubGroupsData> subgroups = Lists.newArrayList();
	private ProfileLedgerData ProfileLedgerData;
	private TrailBalanceAmounts trailBalanceAmounts;

	public GroupsData() {
		super();
	}

	public GroupsData(String groupname, TrailBalanceAmounts trailBalanceAmounts) {
		super();
		this.groupname = groupname;
		this.trailBalanceAmounts = trailBalanceAmounts;
	}

	public GroupsData(String groupname, List<SubGroupsData> subgroups, ProfileLedgerData profileLedgerData,
			TrailBalanceAmounts trailBalanceAmounts) {
		super();
		this.groupname = groupname;
		this.subgroups = subgroups;
		ProfileLedgerData = profileLedgerData;
		this.trailBalanceAmounts = trailBalanceAmounts;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public List<SubGroupsData> getSubgroups() {
		return subgroups;
	}

	public void setSubgroups(List<SubGroupsData> subgroups) {
		this.subgroups = subgroups;
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
		return "GroupsData [groupname=" + groupname + ", subgroups=" + subgroups + ", ProfileLedgerData="
				+ ProfileLedgerData + ", trailBalanceAmounts=" + trailBalanceAmounts + "]";
	}
}
