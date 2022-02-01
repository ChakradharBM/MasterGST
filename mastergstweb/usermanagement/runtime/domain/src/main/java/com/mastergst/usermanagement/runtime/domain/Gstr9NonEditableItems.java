package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastergst.core.domain.Base;

public class Gstr9NonEditableItems extends Base{
	
	
	@JsonProperty("itc_3b")
	GSTR9Table6ITC3B itc3b = new GSTR9Table6ITC3B();
	@JsonProperty("itc_2a")
	GSTR9Table8ITC2A itc2a = new GSTR9Table8ITC2A();
	GSTR9Table9AllDetails iamt = new GSTR9Table9AllDetails();
	GSTR9Table9AllDetails camt = new GSTR9Table9AllDetails();
	GSTR9Table9AllDetails samt = new GSTR9Table9AllDetails();
	GSTR9Table9AllDetails csamt = new GSTR9Table9AllDetails();
	GSTR9Table9AllDetails intr = new GSTR9Table9AllDetails();
	GSTR9Table9AllDetails fee = new GSTR9Table9AllDetails();
	GSTR9Table9AllDetails pen = new GSTR9Table9AllDetails();
	GSTR9Table9AllDetails other = new GSTR9Table9AllDetails();
	GSTR9Table4OtherThanExpSezDetails table4AtoG = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table4ItoL = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table4HtoM = new GSTR9Table4OtherThanExpSezDetails();
	
	GSTR9Table5ItemDetails table5AtoF = new GSTR9Table5ItemDetails();
	GSTR9Table5ItemDetails table5HtoK = new GSTR9Table5ItemDetails();
	GSTR9Table5ItemDetails turnoverOnTaxNotPaid = new GSTR9Table5ItemDetails();
	GSTR9Table4OtherThanExpSezDetails totalTurnOver = new GSTR9Table4OtherThanExpSezDetails();
	
	GSTR9Table4OtherThanExpSezDetails table6BtoH = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table6J = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table6N = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table6O = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table7I = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table7J = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table8B = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table8D = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table8I = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table8H = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table8J = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table8K = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails table10111213 = new GSTR9Table4OtherThanExpSezDetails();
	
	
	
	public GSTR9Table6ITC3B getItc3b() {
		return itc3b;
	}

	public void setItc3b(GSTR9Table6ITC3B itc3b) {
		this.itc3b = itc3b;
	}

	public GSTR9Table8ITC2A getItc2a() {
		return itc2a;
	}

	public void setItc2a(GSTR9Table8ITC2A itc2a) {
		this.itc2a = itc2a;
	}

	public GSTR9Table9AllDetails getIamt() {
		return iamt;
	}

	public void setIamt(GSTR9Table9AllDetails iamt) {
		this.iamt = iamt;
	}

	public GSTR9Table9AllDetails getCamt() {
		return camt;
	}

	public void setCamt(GSTR9Table9AllDetails camt) {
		this.camt = camt;
	}

	public GSTR9Table9AllDetails getSamt() {
		return samt;
	}

	public void setSamt(GSTR9Table9AllDetails samt) {
		this.samt = samt;
	}

	public GSTR9Table9AllDetails getCsamt() {
		return csamt;
	}

	public void setCsamt(GSTR9Table9AllDetails csamt) {
		this.csamt = csamt;
	}

	public GSTR9Table9AllDetails getIntr() {
		return intr;
	}

	public void setIntr(GSTR9Table9AllDetails intr) {
		this.intr = intr;
	}

	public GSTR9Table9AllDetails getFee() {
		return fee;
	}

	public void setFee(GSTR9Table9AllDetails fee) {
		this.fee = fee;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable4AtoG() {
		return table4AtoG;
	}

	public void setTable4AtoG(GSTR9Table4OtherThanExpSezDetails table4AtoG) {
		this.table4AtoG = table4AtoG;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable4ItoL() {
		return table4ItoL;
	}

	public void setTable4ItoL(GSTR9Table4OtherThanExpSezDetails table4ItoL) {
		this.table4ItoL = table4ItoL;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable4HtoM() {
		return table4HtoM;
	}

	public void setTable4HtoM(GSTR9Table4OtherThanExpSezDetails table4HtoM) {
		this.table4HtoM = table4HtoM;
	}

	public GSTR9Table5ItemDetails getTable5AtoF() {
		return table5AtoF;
	}

	public void setTable5AtoF(GSTR9Table5ItemDetails table5AtoF) {
		this.table5AtoF = table5AtoF;
	}

	public GSTR9Table5ItemDetails getTable5HtoK() {
		return table5HtoK;
	}

	public void setTable5HtoK(GSTR9Table5ItemDetails table5HtoK) {
		this.table5HtoK = table5HtoK;
	}

	public GSTR9Table5ItemDetails getTurnoverOnTaxNotPaid() {
		return turnoverOnTaxNotPaid;
	}

	public void setTurnoverOnTaxNotPaid(GSTR9Table5ItemDetails turnoverOnTaxNotPaid) {
		this.turnoverOnTaxNotPaid = turnoverOnTaxNotPaid;
	}

	public GSTR9Table4OtherThanExpSezDetails getTotalTurnOver() {
		return totalTurnOver;
	}

	public void setTotalTurnOver(GSTR9Table4OtherThanExpSezDetails totalTurnOver) {
		this.totalTurnOver = totalTurnOver;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable6BtoH() {
		return table6BtoH;
	}

	public void setTable6BtoH(GSTR9Table4OtherThanExpSezDetails table6BtoH) {
		this.table6BtoH = table6BtoH;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable6J() {
		return table6J;
	}

	public void setTable6J(GSTR9Table4OtherThanExpSezDetails table6j) {
		table6J = table6j;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable6N() {
		return table6N;
	}

	public void setTable6N(GSTR9Table4OtherThanExpSezDetails table6n) {
		table6N = table6n;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable6O() {
		return table6O;
	}

	public void setTable6O(GSTR9Table4OtherThanExpSezDetails table6o) {
		table6O = table6o;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable7I() {
		return table7I;
	}

	public void setTable7I(GSTR9Table4OtherThanExpSezDetails table7i) {
		table7I = table7i;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable7J() {
		return table7J;
	}

	public void setTable7J(GSTR9Table4OtherThanExpSezDetails table7j) {
		table7J = table7j;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable8D() {
		return table8D;
	}

	public void setTable8D(GSTR9Table4OtherThanExpSezDetails table8d) {
		table8D = table8d;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable8I() {
		return table8I;
	}

	public void setTable8I(GSTR9Table4OtherThanExpSezDetails table8i) {
		table8I = table8i;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable8H() {
		return table8H;
	}

	public void setTable8H(GSTR9Table4OtherThanExpSezDetails table8h) {
		table8H = table8h;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable8J() {
		return table8J;
	}

	public void setTable8J(GSTR9Table4OtherThanExpSezDetails table8j) {
		table8J = table8j;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable8K() {
		return table8K;
	}

	public void setTable8K(GSTR9Table4OtherThanExpSezDetails table8k) {
		table8K = table8k;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable10111213() {
		return table10111213;
	}

	public void setTable10111213(GSTR9Table4OtherThanExpSezDetails table10111213) {
		this.table10111213 = table10111213;
	}

	public GSTR9Table4OtherThanExpSezDetails getTable8B() {
		return table8B;
	}

	public void setTable8B(GSTR9Table4OtherThanExpSezDetails table8b) {
		table8B = table8b;
	}

	public GSTR9Table9AllDetails getPen() {
		return pen;
	}

	public void setPen(GSTR9Table9AllDetails pen) {
		this.pen = pen;
	}

	public GSTR9Table9AllDetails getOther() {
		return other;
	}

	public void setOther(GSTR9Table9AllDetails other) {
		this.other = other;
	}

}
