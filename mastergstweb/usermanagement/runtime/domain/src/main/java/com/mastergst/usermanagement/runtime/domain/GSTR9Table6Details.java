package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR9 Table6  information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id","chksum"})
public class GSTR9Table6Details {
	
	@Id
	private ObjectId id;
	
	String flag;
	String chksum;
	@JsonProperty("supp_non_rchrg")
	List<GSTR9Table6SuppliesDetails> suppNonRchrg=LazyList.decorate(new ArrayList<GSTR9Table6SuppliesDetails>(), 
			FactoryUtils.instantiateFactory(GSTR9Table6SuppliesDetails.class));
	@JsonProperty("supp_rchrg_unreg")
	List<GSTR9Table6SuppliesDetails> suppRchrgUnreg=LazyList.decorate(new ArrayList<GSTR9Table6SuppliesDetails>(), 
			FactoryUtils.instantiateFactory(GSTR9Table6SuppliesDetails.class));
	@JsonProperty("supp_rchrg_reg")
	List<GSTR9Table6SuppliesDetails> suppRchrgReg=LazyList.decorate(new ArrayList<GSTR9Table6SuppliesDetails>(), 
			FactoryUtils.instantiateFactory(GSTR9Table6SuppliesDetails.class));
	List<GSTR9Table6ImportGoodsDetails> iog=LazyList.decorate(new ArrayList<GSTR9Table6ImportGoodsDetails>(), 
			FactoryUtils.instantiateFactory(GSTR9Table6ImportGoodsDetails.class));
	GSTR9Table6ImportServicesDetails ios = new GSTR9Table6ImportServicesDetails();
	GSTR9Table6IsdAndItcClaimedDetails isd = new GSTR9Table6IsdAndItcClaimedDetails();
	@JsonProperty("itc_clmd")
	GSTR9Table6IsdAndItcClaimedDetails itcClmd = new GSTR9Table6IsdAndItcClaimedDetails();
	GSTR9Table6Trans1AndTrans2Details tran1 = new GSTR9Table6Trans1AndTrans2Details();
	GSTR9Table6Trans1AndTrans2Details tran2 = new GSTR9Table6Trans1AndTrans2Details();
	GSTR9Table6IsdAndItcClaimedDetails other = new GSTR9Table6IsdAndItcClaimedDetails();
	
	public GSTR9Table6Details() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public List<GSTR9Table6SuppliesDetails> getSuppNonRchrg() {
		return suppNonRchrg;
	}

	public void setSuppNonRchrg(List<GSTR9Table6SuppliesDetails> suppNonRchrg) {
		this.suppNonRchrg = suppNonRchrg;
	}

	public List<GSTR9Table6SuppliesDetails> getSuppRchrgUnreg() {
		return suppRchrgUnreg;
	}

	public void setSuppRchrgUnreg(List<GSTR9Table6SuppliesDetails> suppRchrgUnreg) {
		this.suppRchrgUnreg = suppRchrgUnreg;
	}

	public List<GSTR9Table6SuppliesDetails> getSuppRchrgReg() {
		return suppRchrgReg;
	}

	public void setSuppRchrgReg(List<GSTR9Table6SuppliesDetails> suppRchrgReg) {
		this.suppRchrgReg = suppRchrgReg;
	}

	public List<GSTR9Table6ImportGoodsDetails> getIog() {
		return iog;
	}

	public void setIog(List<GSTR9Table6ImportGoodsDetails> iog) {
		this.iog = iog;
	}

	public GSTR9Table6ImportServicesDetails getIos() {
		return ios;
	}

	public void setIos(GSTR9Table6ImportServicesDetails ios) {
		this.ios = ios;
	}

	public GSTR9Table6IsdAndItcClaimedDetails getIsd() {
		return isd;
	}

	public void setIsd(GSTR9Table6IsdAndItcClaimedDetails isd) {
		this.isd = isd;
	}

	public GSTR9Table6IsdAndItcClaimedDetails getItcClmd() {
		return itcClmd;
	}

	public void setItcClmd(GSTR9Table6IsdAndItcClaimedDetails itcClmd) {
		this.itcClmd = itcClmd;
	}

	public GSTR9Table6Trans1AndTrans2Details getTran1() {
		return tran1;
	}

	public void setTran1(GSTR9Table6Trans1AndTrans2Details tran1) {
		this.tran1 = tran1;
	}

	public GSTR9Table6Trans1AndTrans2Details getTran2() {
		return tran2;
	}

	public void setTran2(GSTR9Table6Trans1AndTrans2Details tran2) {
		this.tran2 = tran2;
	}

	public GSTR9Table6IsdAndItcClaimedDetails getOther() {
		return other;
	}

	public void setOther(GSTR9Table6IsdAndItcClaimedDetails other) {
		this.other = other;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	
	
	
}
