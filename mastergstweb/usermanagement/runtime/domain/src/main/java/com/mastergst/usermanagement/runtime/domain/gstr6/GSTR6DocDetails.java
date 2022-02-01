package com.mastergst.usermanagement.runtime.domain.gstr6;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR6DocDetails {
	@Id
	private ObjectId id;
	private String chksum;
	//For ISDA
	private String cpty;
	private String statecd;
	private String rdocnum;
	private String rdocdt;
	private String odocnum;
	private String odocdt;
	private String rcrdnum;
	private String rcrddt;
	private String ocrdnum;
	private String ocrddt;
	
	@JsonProperty("isd_docty")
	private String isdDocty;
	private String docnum;
	private String docdt;
	private String crdnum;
	private String crddt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double iamti;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double iamts;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double iamtc;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double samts;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double samti;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double camti;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double camtc;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double csamt;
	public GSTR6DocDetails() {
		this.id = ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getChksum() {
		return chksum;
	}
	public void setChksum(String chksum) {
		this.chksum = chksum;
	}
	public String getCpty() {
		return cpty;
	}
	public void setCpty(String cpty) {
		this.cpty = cpty;
	}
	public String getStatecd() {
		return statecd;
	}
	public void setStatecd(String statecd) {
		this.statecd = statecd;
	}
	public String getRdocnum() {
		return rdocnum;
	}
	public void setRdocnum(String rdocnum) {
		this.rdocnum = rdocnum;
	}
	public String getRdocdt() {
		return rdocdt;
	}
	public void setRdocdt(String rdocdt) {
		this.rdocdt = rdocdt;
	}
	public String getOdocnum() {
		return odocnum;
	}
	public void setOdocnum(String odocnum) {
		this.odocnum = odocnum;
	}
	public String getOdocdt() {
		return odocdt;
	}
	public void setOdocdt(String odocdt) {
		this.odocdt = odocdt;
	}
	public String getRcrdnum() {
		return rcrdnum;
	}
	public void setRcrdnum(String rcrdnum) {
		this.rcrdnum = rcrdnum;
	}
	public String getRcrddt() {
		return rcrddt;
	}
	public void setRcrddt(String rcrddt) {
		this.rcrddt = rcrddt;
	}
	public String getOcrdnum() {
		return ocrdnum;
	}
	public void setOcrdnum(String ocrdnum) {
		this.ocrdnum = ocrdnum;
	}
	public String getOcrddt() {
		return ocrddt;
	}
	public void setOcrddt(String ocrddt) {
		this.ocrddt = ocrddt;
	}
	public String getIsdDocty() {
		return isdDocty;
	}
	public void setIsdDocty(String isdDocty) {
		this.isdDocty = isdDocty;
	}
	public String getDocnum() {
		return docnum;
	}
	public void setDocnum(String docnum) {
		this.docnum = docnum;
	}
	public String getDocdt() {
		return docdt;
	}
	public void setDocdt(String docdt) {
		this.docdt = docdt;
	}
	public String getCrdnum() {
		return crdnum;
	}
	public void setCrdnum(String crdnum) {
		this.crdnum = crdnum;
	}
	public String getCrddt() {
		return crddt;
	}
	public void setCrddt(String crddt) {
		this.crddt = crddt;
	}
	public Double getIamti() {
		return iamti;
	}
	public void setIamti(Double iamti) {
		this.iamti = iamti;
	}
	public Double getIamts() {
		return iamts;
	}
	public void setIamts(Double iamts) {
		this.iamts = iamts;
	}
	public Double getIamtc() {
		return iamtc;
	}
	public void setIamtc(Double iamtc) {
		this.iamtc = iamtc;
	}
	public Double getSamts() {
		return samts;
	}
	public void setSamts(Double samts) {
		this.samts = samts;
	}
	public Double getSamti() {
		return samti;
	}
	public void setSamti(Double samti) {
		this.samti = samti;
	}
	public Double getCamti() {
		return camti;
	}
	public void setCamti(Double camti) {
		this.camti = camti;
	}
	public Double getCamtc() {
		return camtc;
	}
	public void setCamtc(Double camtc) {
		this.camtc = camtc;
	}
	public Double getCsamt() {
		return csamt;
	}
	public void setCsamt(Double csamt) {
		this.csamt = csamt;
	}
	
}
