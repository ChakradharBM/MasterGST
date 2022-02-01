package com.mastergst.usermanagement.runtime.response.gstr2b;

public class GSTR2BVO {
	private String heading;
	private String tableValue;
	private Double igst;
	private Double cgst;
	private Double sgst;
	private Double cess;
	private String advisory;
	public GSTR2BVO() {
		this.igst = 0d;
		this.cgst = 0d;
		this.sgst = 0d;
		this.cess = 0d;
	}
	public String getHeading() {
		return heading;
	}
	public void setHeading(String heading) {
		this.heading = heading;
	}
	public String getTableValue() {
		return tableValue;
	}
	public void setTableValue(String tableValue) {
		this.tableValue = tableValue;
	}
	public Double getIgst() {
		return igst;
	}
	public void setIgst(Double igst) {
		this.igst = igst;
	}
	public Double getCgst() {
		return cgst;
	}
	public void setCgst(Double cgst) {
		this.cgst = cgst;
	}
	public Double getSgst() {
		return sgst;
	}
	public void setSgst(Double sgst) {
		this.sgst = sgst;
	}
	public Double getCess() {
		return cess;
	}
	public void setCess(Double cess) {
		this.cess = cess;
	}
	public String getAdvisory() {
		return advisory;
	}
	public void setAdvisory(String advisory) {
		this.advisory = advisory;
	}
	@Override
	public String toString() {
		return "GSTR2BVO [heading=" + heading + ", tableValue=" + tableValue + ", igst=" + igst + ", cgst=" + cgst
				+ ", sgst=" + sgst + ", cess=" + cess + "]";
	}
}
