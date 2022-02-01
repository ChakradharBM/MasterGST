package com.mastergst.usermanagement.runtime.domain.gstr9response;

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
 * GSTR9 Table6 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({ "id" })
public class GSTR9Table6DetailsResponse {

	@Id
	private ObjectId id;

	String flag;

	@JsonProperty("supp_non_rchrg")
	List<GSTR9Table6SuppliesDetailsResponse> suppNonRchrg = LazyList.decorate(new ArrayList<GSTR9Table6SuppliesDetailsResponse>(),
			FactoryUtils.instantiateFactory(GSTR9Table6SuppliesDetailsResponse.class));
	@JsonProperty("supp_rchrg_unreg")
	List<GSTR9Table6SuppliesDetailsResponse> suppRchrgUnreg = LazyList.decorate(new ArrayList<GSTR9Table6SuppliesDetailsResponse>(),
			FactoryUtils.instantiateFactory(GSTR9Table6SuppliesDetailsResponse.class));
	@JsonProperty("supp_rchrg_reg")
	List<GSTR9Table6SuppliesDetailsResponse> suppRchrgReg = LazyList.decorate(new ArrayList<GSTR9Table6SuppliesDetailsResponse>(),
			FactoryUtils.instantiateFactory(GSTR9Table6SuppliesDetailsResponse.class));
	List<GSTR9Table6ImportGoodsDetailsResponse> iog = LazyList.decorate(new ArrayList<GSTR9Table6ImportGoodsDetailsResponse>(),
			FactoryUtils.instantiateFactory(GSTR9Table6ImportGoodsDetailsResponse.class));
	GSTR9Table6ImportServicesDetailsResponse ios = new GSTR9Table6ImportServicesDetailsResponse();
	GSTR9Table6IsdAndItcClaimedDetailsResponse isd = new GSTR9Table6IsdAndItcClaimedDetailsResponse();
	@JsonProperty("itc_clmd")
	GSTR9Table6IsdAndItcClaimedDetailsResponse itcClmd = new GSTR9Table6IsdAndItcClaimedDetailsResponse();
	GSTR9Table6Trans1AndTrans2DetailsResponse tran1 = new GSTR9Table6Trans1AndTrans2DetailsResponse();
	GSTR9Table6Trans1AndTrans2DetailsResponse tran2 = new GSTR9Table6Trans1AndTrans2DetailsResponse();
	GSTR9Table6IsdAndItcClaimedDetailsResponse other = new GSTR9Table6IsdAndItcClaimedDetailsResponse();

	GSTR9Table6IsdAndItcClaimedDetailsResponse difference = new GSTR9Table6IsdAndItcClaimedDetailsResponse();
	@JsonProperty("sub_totalBH")
	GSTR9Table6IsdAndItcClaimedDetailsResponse subTotalBH = new GSTR9Table6IsdAndItcClaimedDetailsResponse();
	@JsonProperty("sub_totalKM")
	GSTR9Table6IsdAndItcClaimedDetailsResponse subTotalKM = new GSTR9Table6IsdAndItcClaimedDetailsResponse();
	@JsonProperty("total_itc_availed")
	GSTR9Table6IsdAndItcClaimedDetailsResponse totalItcAvailed = new GSTR9Table6IsdAndItcClaimedDetailsResponse();
	@JsonProperty("itc_3b")
	GSTR9Table6IsdAndItcClaimedDetailsResponse itc3b = new GSTR9Table6IsdAndItcClaimedDetailsResponse();

	public GSTR9Table6IsdAndItcClaimedDetailsResponse getItc3b() {
		return itc3b;
	}

	public void setItc3b(GSTR9Table6IsdAndItcClaimedDetailsResponse itc3b) {
		this.itc3b = itc3b;
	}

	public GSTR9Table6IsdAndItcClaimedDetailsResponse getDifference() {
		return difference;
	}

	public void setDifference(GSTR9Table6IsdAndItcClaimedDetailsResponse difference) {
		this.difference = difference;
	}

	public GSTR9Table6IsdAndItcClaimedDetailsResponse getSubTotalBH() {
		return subTotalBH;
	}

	public void setSubTotalBH(GSTR9Table6IsdAndItcClaimedDetailsResponse subTotalBH) {
		this.subTotalBH = subTotalBH;
	}

	public GSTR9Table6IsdAndItcClaimedDetailsResponse getSubTotalKM() {
		return subTotalKM;
	}

	public void setSubTotalKM(GSTR9Table6IsdAndItcClaimedDetailsResponse subTotalKM) {
		this.subTotalKM = subTotalKM;
	}

	public GSTR9Table6IsdAndItcClaimedDetailsResponse getTotalItcAvailed() {
		return totalItcAvailed;
	}

	public void setTotalItcAvailed(GSTR9Table6IsdAndItcClaimedDetailsResponse totalItcAvailed) {
		this.totalItcAvailed = totalItcAvailed;
	}

	public GSTR9Table6DetailsResponse() {
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

	public List<GSTR9Table6SuppliesDetailsResponse> getSuppNonRchrg() {
		return suppNonRchrg;
	}

	public void setSuppNonRchrg(List<GSTR9Table6SuppliesDetailsResponse> suppNonRchrg) {
		this.suppNonRchrg = suppNonRchrg;
	}

	public List<GSTR9Table6SuppliesDetailsResponse> getSuppRchrgUnreg() {
		return suppRchrgUnreg;
	}

	public void setSuppRchrgUnreg(List<GSTR9Table6SuppliesDetailsResponse> suppRchrgUnreg) {
		this.suppRchrgUnreg = suppRchrgUnreg;
	}

	public List<GSTR9Table6SuppliesDetailsResponse> getSuppRchrgReg() {
		return suppRchrgReg;
	}

	public void setSuppRchrgReg(List<GSTR9Table6SuppliesDetailsResponse> suppRchrgReg) {
		this.suppRchrgReg = suppRchrgReg;
	}

	public List<GSTR9Table6ImportGoodsDetailsResponse> getIog() {
		return iog;
	}

	public void setIog(List<GSTR9Table6ImportGoodsDetailsResponse> iog) {
		this.iog = iog;
	}

	public GSTR9Table6ImportServicesDetailsResponse getIos() {
		return ios;
	}

	public void setIos(GSTR9Table6ImportServicesDetailsResponse ios) {
		this.ios = ios;
	}

	public GSTR9Table6IsdAndItcClaimedDetailsResponse getIsd() {
		return isd;
	}

	public void setIsd(GSTR9Table6IsdAndItcClaimedDetailsResponse isd) {
		this.isd = isd;
	}

	public GSTR9Table6IsdAndItcClaimedDetailsResponse getItcClmd() {
		return itcClmd;
	}

	public void setItcClmd(GSTR9Table6IsdAndItcClaimedDetailsResponse itcClmd) {
		this.itcClmd = itcClmd;
	}

	public GSTR9Table6Trans1AndTrans2DetailsResponse getTran1() {
		return tran1;
	}

	public void setTran1(GSTR9Table6Trans1AndTrans2DetailsResponse tran1) {
		this.tran1 = tran1;
	}

	public GSTR9Table6Trans1AndTrans2DetailsResponse getTran2() {
		return tran2;
	}

	public void setTran2(GSTR9Table6Trans1AndTrans2DetailsResponse tran2) {
		this.tran2 = tran2;
	}

	public GSTR9Table6IsdAndItcClaimedDetailsResponse getOther() {
		return other;
	}

	public void setOther(GSTR9Table6IsdAndItcClaimedDetailsResponse other) {
		this.other = other;
	}

}
