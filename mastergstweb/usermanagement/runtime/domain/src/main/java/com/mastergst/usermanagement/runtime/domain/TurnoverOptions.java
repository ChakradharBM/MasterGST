package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class TurnoverOptions {
	
	@Id
	private ObjectId id;
	private String year;
	private Double turnover;
	
	public TurnoverOptions(ObjectId id, String year, Double turnover) {
		super();
		this.id = id;
		this.year = year;
		this.turnover = turnover;
	}
	public TurnoverOptions() {
		super();
	}
	public TurnoverOptions(String year, Double turnover) {
		this.year = year;
		this.turnover = turnover;
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public Double getTurnover() {
		return turnover;
	}
	public void setTurnover(Double turnover) {
		this.turnover = turnover;
	}
	@Override
	public String toString() {
		return "TurnoverOptions [id=" + id + ", year=" + year + ", turnover=" + turnover + "]";
	}
}
