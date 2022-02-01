package com.mastergst.usermanagement.runtime.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EwayBillCancelResponse {
	@JsonProperty("status_cd")
	private String statuscd;

	@JsonProperty("status_desc")
	private String statusdesc;	
	
	private String message;
	
	@JsonProperty("error")
	private Error error;
	
	@JsonProperty("header")
	private Map<String, String> header;
	
	EwayBillCancelResponseData data = new EwayBillCancelResponseData();
	
	
	
	public String getStatuscd() {
		return statuscd;
	}
	public void setStatuscd(String statuscd) {
		this.statuscd = statuscd;
	}
	public String getStatusdesc() {
		return statusdesc;
	}
	public void setStatusdesc(String statusdesc) {
		this.statusdesc = statusdesc;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Error getError() {
		return error;
	}
	public void setError(Error error) {
		this.error = error;
	}
	public Map<String, String> getHeader() {
		return header;
	}
	public void setHeader(Map<String, String> header) {
		this.header = header;
	}
	public EwayBillCancelResponseData getData() {
		return data;
	}
	public void setData(EwayBillCancelResponseData data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "EwayBillCancelResponse [statuscd=" + statuscd + ", statusdesc=" + statusdesc + ", message=" + message
				+ ", error=" + error + ", header=" + header + ", data=" + data + ", getStatuscd()=" + getStatuscd()
				+ ", getStatusdesc()=" + getStatusdesc() + ", getMessage()=" + getMessage() + ", getError()="
				+ getError() + ", getHeader()=" + getHeader() + ", getData()=" + getData() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	
	
	
}
