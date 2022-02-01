package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "auditorDetails")
public class AuditorAdrressDetails extends Base{
	
	private String userid;
	private String usertype;
	private String fullname;
	private String place;
	private String nameofsignatory;
	private String membershipno;
	private String panno;
	private String buildorflatno;
	private String floorno;
	private String buildingname;
	private String streetname;
	private String locality;
	private String districtname;
	private String statename;
	private String pincode;
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsertype() {
		return usertype;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getNameofsignatory() {
		return nameofsignatory;
	}
	public void setNameofsignatory(String nameofsignatory) {
		this.nameofsignatory = nameofsignatory;
	}
	public String getMembershipno() {
		return membershipno;
	}
	public void setMembershipno(String membershipno) {
		this.membershipno = membershipno;
	}
	public String getPanno() {
		return panno;
	}
	public void setPanno(String panno) {
		this.panno = panno;
	}
	public String getBuildorflatno() {
		return buildorflatno;
	}
	public void setBuildorflatno(String buildorflatno) {
		this.buildorflatno = buildorflatno;
	}
	public String getFloorno() {
		return floorno;
	}
	public void setFloorno(String floorno) {
		this.floorno = floorno;
	}
	public String getBuildingname() {
		return buildingname;
	}
	public void setBuildingname(String buildingname) {
		this.buildingname = buildingname;
	}
	public String getStreetname() {
		return streetname;
	}
	public void setStreetname(String streetname) {
		this.streetname = streetname;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public String getDistrictname() {
		return districtname;
	}
	public void setDistrictname(String districtname) {
		this.districtname = districtname;
	}
	public String getStatename() {
		return statename;
	}
	public void setStatename(String statename) {
		this.statename = statename;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	
	

}
