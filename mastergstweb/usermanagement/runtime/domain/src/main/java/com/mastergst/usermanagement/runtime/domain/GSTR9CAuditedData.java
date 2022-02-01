package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9CAuditedData {
	@Id
	private ObjectId id;
	private String gstin;
	private String fp;
	@JsonProperty("act_name")
	private String actName;
	private String isauditor;
	
	GSTR9CTable5Details table5 = new GSTR9CTable5Details();
	GSTR9CTable6Details table6 = new GSTR9CTable6Details();
	GSTR9CTable7Details table7 = new GSTR9CTable7Details();
	GSTR9CTable8Details table8 = new GSTR9CTable8Details();
	GSTR9CTable9Details table9 = new GSTR9CTable9Details();
	GSTR9CTable10Details table10 = new GSTR9CTable10Details();
	GSTR9CTable11Details table11 = new GSTR9CTable11Details();
	GSTR9CTable12Details table12 = new GSTR9CTable12Details();
	GSTR9CTable13Details table13 = new GSTR9CTable13Details();
	GSTR9CTable14Details table14 = new GSTR9CTable14Details();
	GSTR9CTable15Details table15 = new GSTR9CTable15Details();
	GSTR9CTable16Details table16 = new GSTR9CTable16Details();
	@JsonProperty("add_liab")
	GSTR9CAddLiabilities addLiab = new GSTR9CAddLiabilities();
	
	public GSTR9CAuditedData() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getGstin() {
		return gstin;
	}
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}
	public String getFp() {
		return fp;
	}
	public void setFp(String fp) {
		this.fp = fp;
	}
	public String getActName() {
		return actName;
	}
	public void setActName(String actName) {
		this.actName = actName;
	}
	public String getIsauditor() {
		return isauditor;
	}
	public void setIsauditor(String isauditor) {
		this.isauditor = isauditor;
	}
	public GSTR9CTable5Details getTable5() {
		return table5;
	}
	public void setTable5(GSTR9CTable5Details table5) {
		this.table5 = table5;
	}
	public GSTR9CTable6Details getTable6() {
		return table6;
	}
	public void setTable6(GSTR9CTable6Details table6) {
		this.table6 = table6;
	}
	public GSTR9CTable7Details getTable7() {
		return table7;
	}
	public void setTable7(GSTR9CTable7Details table7) {
		this.table7 = table7;
	}
	public GSTR9CTable8Details getTable8() {
		return table8;
	}
	public void setTable8(GSTR9CTable8Details table8) {
		this.table8 = table8;
	}
	public GSTR9CTable9Details getTable9() {
		return table9;
	}
	public void setTable9(GSTR9CTable9Details table9) {
		this.table9 = table9;
	}
	public GSTR9CTable10Details getTable10() {
		return table10;
	}
	public void setTable10(GSTR9CTable10Details table10) {
		this.table10 = table10;
	}
	public GSTR9CTable11Details getTable11() {
		return table11;
	}
	public void setTable11(GSTR9CTable11Details table11) {
		this.table11 = table11;
	}
	public GSTR9CTable12Details getTable12() {
		return table12;
	}
	public void setTable12(GSTR9CTable12Details table12) {
		this.table12 = table12;
	}
	public GSTR9CTable13Details getTable13() {
		return table13;
	}
	public void setTable13(GSTR9CTable13Details table13) {
		this.table13 = table13;
	}
	public GSTR9CTable14Details getTable14() {
		return table14;
	}
	public void setTable14(GSTR9CTable14Details table14) {
		this.table14 = table14;
	}
	public GSTR9CTable15Details getTable15() {
		return table15;
	}
	public void setTable15(GSTR9CTable15Details table15) {
		this.table15 = table15;
	}
	public GSTR9CTable16Details getTable16() {
		return table16;
	}
	public void setTable16(GSTR9CTable16Details table16) {
		this.table16 = table16;
	}
	public GSTR9CAddLiabilities getAddLiab() {
		return addLiab;
	}
	public void setAddLiab(GSTR9CAddLiabilities addLiab) {
		this.addLiab = addLiab;
	}
	
	
	
}
