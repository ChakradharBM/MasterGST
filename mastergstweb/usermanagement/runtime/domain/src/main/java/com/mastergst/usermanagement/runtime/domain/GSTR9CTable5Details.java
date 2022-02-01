package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9CTable5Details {
	@Id
	private ObjectId id;
	
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double turnovr;
	
	@JsonProperty("unbil_rev_beg")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double unbilRevBeg;
	
	@JsonProperty("unadj_adv_end")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double unadjAdvEnd;
	
	@JsonProperty("dmd_sup")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double dmdSup;
	
	@JsonProperty("crd_nts_issued")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double crdNtsIssued;
	
	@JsonProperty("trd_dis")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double trdDis;
	
	@JsonProperty("turnovr_apr_jun")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double turnovrAprJun;
	
	@JsonProperty("unbil_rev_end")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double unbilRevEnd;
	
	@JsonProperty("unadj_adv_beg")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double unadjAdvBeg;
	
	@JsonProperty("crd_note_acc")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double crdNoteAcc;
	
	@JsonProperty("adj_dta")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double adjDta;
	
	@JsonProperty("turnovr_comp")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double turnovrComp;
	
	@JsonProperty("adj_turn_sec")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double adjTurnSec;
	
	@JsonProperty("adj_turn_fef")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double adjTurnFef;
	
	@JsonProperty("adj_turn_othrsn")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double adjTurnOthrsn;
	
	@JsonProperty("annul_turn_adj")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double annulTurnAdj;
	
	@JsonProperty("annul_turn_decl")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double annulTurnDecl;
	
	@JsonProperty("unrec_turnovr")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double unrecTurnovr;
	
	public GSTR9CTable5Details() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getTurnovr() {
		return turnovr;
	}

	public void setTurnovr(Double turnovr) {
		this.turnovr = turnovr;
	}

	public Double getUnbilRevBeg() {
		return unbilRevBeg;
	}

	public void setUnbilRevBeg(Double unbilRevBeg) {
		this.unbilRevBeg = unbilRevBeg;
	}

	public Double getUnadjAdvEnd() {
		return unadjAdvEnd;
	}

	public void setUnadjAdvEnd(Double unadjAdvEnd) {
		this.unadjAdvEnd = unadjAdvEnd;
	}

	public Double getDmdSup() {
		return dmdSup;
	}

	public void setDmdSup(Double dmdSup) {
		this.dmdSup = dmdSup;
	}

	public Double getCrdNtsIssued() {
		return crdNtsIssued;
	}

	public void setCrdNtsIssued(Double crdNtsIssued) {
		this.crdNtsIssued = crdNtsIssued;
	}

	public Double getTrdDis() {
		return trdDis;
	}

	public void setTrdDis(Double trdDis) {
		this.trdDis = trdDis;
	}

	public Double getTurnovrAprJun() {
		return turnovrAprJun;
	}

	public void setTurnovrAprJun(Double turnovrAprJun) {
		this.turnovrAprJun = turnovrAprJun;
	}

	public Double getUnbilRevEnd() {
		return unbilRevEnd;
	}

	public void setUnbilRevEnd(Double unbilRevEnd) {
		this.unbilRevEnd = unbilRevEnd;
	}

	public Double getUnadjAdvBeg() {
		return unadjAdvBeg;
	}

	public void setUnadjAdvBeg(Double unadjAdvBeg) {
		this.unadjAdvBeg = unadjAdvBeg;
	}

	public Double getCrdNoteAcc() {
		return crdNoteAcc;
	}

	public void setCrdNoteAcc(Double crdNoteAcc) {
		this.crdNoteAcc = crdNoteAcc;
	}

	public Double getAdjDta() {
		return adjDta;
	}

	public void setAdjDta(Double adjDta) {
		this.adjDta = adjDta;
	}

	public Double getTurnovrComp() {
		return turnovrComp;
	}

	public void setTurnovrComp(Double turnovrComp) {
		this.turnovrComp = turnovrComp;
	}

	public Double getAdjTurnSec() {
		return adjTurnSec;
	}

	public void setAdjTurnSec(Double adjTurnSec) {
		this.adjTurnSec = adjTurnSec;
	}

	public Double getAdjTurnFef() {
		return adjTurnFef;
	}

	public void setAdjTurnFef(Double adjTurnFef) {
		this.adjTurnFef = adjTurnFef;
	}

	public Double getAdjTurnOthrsn() {
		return adjTurnOthrsn;
	}

	public void setAdjTurnOthrsn(Double adjTurnOthrsn) {
		this.adjTurnOthrsn = adjTurnOthrsn;
	}

	public Double getAnnulTurnAdj() {
		return annulTurnAdj;
	}

	public void setAnnulTurnAdj(Double annulTurnAdj) {
		this.annulTurnAdj = annulTurnAdj;
	}

	public Double getAnnulTurnDecl() {
		return annulTurnDecl;
	}

	public void setAnnulTurnDecl(Double annulTurnDecl) {
		this.annulTurnDecl = annulTurnDecl;
	}

	public Double getUnrecTurnovr() {
		return unrecTurnovr;
	}

	public void setUnrecTurnovr(Double unrecTurnovr) {
		this.unrecTurnovr = unrecTurnovr;
	}
	
	
}
