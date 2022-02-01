package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9MinorHeads {

	@Id
	private ObjectId id;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double tx;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double intr;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double pen;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double fee;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double oth;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double tot;
	
	public GSTR9MinorHeads() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getTx() {
		return tx;
	}

	public void setTx(Double tx) {
		this.tx = tx;
	}

	public Double getIntr() {
		return intr;
	}

	public void setIntr(Double intr) {
		this.intr = intr;
	}

	public Double getPen() {
		return pen;
	}

	public void setPen(Double pen) {
		this.pen = pen;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public Double getOth() {
		return oth;
	}

	public void setOth(Double oth) {
		this.oth = oth;
	}

	public Double getTot() {
		return tot;
	}

	public void setTot(Double tot) {
		this.tot = tot;
	}
	
	
}
