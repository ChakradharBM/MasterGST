package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * GSTR2X TCSA Credit Data invoices information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class TCSACreditData {
	
	@Id
	private ObjectId id;
	
	@DateTimeFormat(pattern = "MMYYYY")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMYYYY")
	private Date omonth;
	
	private List<TCSCreditData> tdscredit=LazyList.decorate(new ArrayList<TCSCreditData>(), 
			FactoryUtils.instantiateFactory(TCSCreditData.class));
	
	public TCSACreditData() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Date getOmonth() {
		return omonth;
	}

	public void setOmonth(Date omonth) {
		this.omonth = omonth;
	}

	public List<TCSCreditData> getTdscredit() {
		return tdscredit;
	}

	public void setTdscredit(List<TCSCreditData> tdscredit) {
		this.tdscredit = tdscredit;
	}


}
