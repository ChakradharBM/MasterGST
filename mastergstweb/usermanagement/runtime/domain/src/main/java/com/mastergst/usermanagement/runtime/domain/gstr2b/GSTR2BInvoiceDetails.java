package com.mastergst.usermanagement.runtime.domain.gstr2b;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GSTR2BInvoiceDetails {
	@Id
	private ObjectId id;
	private String inum;
	private String typ;
	private String dt;
	private String val;
	private String pos;
	private String rev;
	private String itcavl;
	private String rsn;
	private Integer diffprcnt;
	private String srctyp;
	private String irn;
	private String irngendate;

//GSTR2B B2BA
	private String oinum;
	private String oidt;
//GSTR2B CDNR
	private String ntnum;
	private String suptyp;
//GSTR2B CDNRA
	private String onttyp;
	private String ontnum;
	private String ontdt;
	private List<GSTR2BItems> items = LazyList.decorate(new ArrayList<GSTR2BItems>(),
			FactoryUtils.instantiateFactory(GSTR2BItems.class));

	public GSTR2BInvoiceDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getInum() {
		return inum;
	}

	public void setInum(String inum) {
		this.inum = inum;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public String getDt() {
		return dt;
	}

	public void setDt(String dt) {
		this.dt = dt;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public String getItcavl() {
		return itcavl;
	}

	public void setItcavl(String itcavl) {
		this.itcavl = itcavl;
	}

	public String getOinum() {
		return oinum;
	}

	public void setOinum(String oinum) {
		this.oinum = oinum;
	}

	public String getOidt() {
		return oidt;
	}

	public void setOidt(String oidt) {
		this.oidt = oidt;
	}

	public String getNtnum() {
		return ntnum;
	}

	public void setNtnum(String ntnum) {
		this.ntnum = ntnum;
	}

	public String getOnttyp() {
		return onttyp;
	}

	public void setOnttyp(String onttyp) {
		this.onttyp = onttyp;
	}

	public String getOntnum() {
		return ontnum;
	}

	public void setOntnum(String ontnum) {
		this.ontnum = ontnum;
	}

	public String getOntdt() {
		return ontdt;
	}

	public void setOntdt(String ontdt) {
		this.ontdt = ontdt;
	}

	public String getSuptyp() {
		return suptyp;
	}

	public void setSuptyp(String suptyp) {
		this.suptyp = suptyp;
	}

	public String getRsn() {
		return rsn;
	}

	public void setRsn(String rsn) {
		this.rsn = rsn;
	}

	public Integer getDiffprcnt() {
		return diffprcnt;
	}

	public void setDiffprcnt(Integer diffprcnt) {
		this.diffprcnt = diffprcnt;
	}

	public String getSrctyp() {
		return srctyp;
	}

	public void setSrctyp(String srctyp) {
		this.srctyp = srctyp;
	}

	public String getIrn() {
		return irn;
	}

	public void setIrn(String irn) {
		this.irn = irn;
	}

	public String getIrngendate() {
		return irngendate;
	}

	public void setIrngendate(String irngendate) {
		this.irngendate = irngendate;
	}

	public List<GSTR2BItems> getItems() {
		return items;
	}

	public void setItems(List<GSTR2BItems> items) {
		this.items = items;
	}

}
