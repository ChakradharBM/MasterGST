package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "ecommerceoperator")
public class EcommerceOpeartor extends Base {

	private String userid;
	private String fullname;
	private String clientid;
	private String name;
	private String gstnnumber;
	private String operator;
	private String email;
	private String mobilenumber;
	private String state;
	private String address;
	private String pincode;
	private String city;
	private String landline;
	private String country;
	private String operatorPanNumber;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGstnnumber() {
		return gstnnumber;
	}

	public void setGstnnumber(String gstnnumber) {
		this.gstnnumber = gstnnumber;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobilenumber() {
		return mobilenumber;
	}

	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLandline() {
		return landline;
	}

	public void setLandline(String landline) {
		this.landline = landline;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getOperatorPanNumber() {
		return operatorPanNumber;
	}

	public void setOperatorPanNumber(String operatorPanNumber) {
		this.operatorPanNumber = operatorPanNumber;
	}

	@Override
	public String toString() {
		return "EcommerceOpeartor [userid=" + userid + ", fullname=" + fullname + ", clientid=" + clientid + ", name="
				+ name + ", gstnnumber=" + gstnnumber + ", operator=" + operator + ", email=" + email
				+ ", mobilenumber=" + mobilenumber + ", state=" + state + ", address=" + address + ", pincode="
				+ pincode + ", city=" + city + ", landline=" + landline + ", country=" + country
				+ ", operatorPanNumber=" + operatorPanNumber + "]";
	}
	

}