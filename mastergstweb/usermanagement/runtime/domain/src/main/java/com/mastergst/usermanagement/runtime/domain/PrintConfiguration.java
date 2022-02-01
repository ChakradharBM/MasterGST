/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Role information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "print_config")
public class PrintConfiguration extends Base {
	
	private String clientid;
	private String invoiceText;
	private String qtyText;
	private String rateText;
	private String authSignText;
	private String discount;
	private String quantity;
	private String rate;
	private String panno;
	private String state;
	private String placeOfSupply;
	private String einvoiceHeaderText;
	private boolean isfooternotescheck;
	private String footernotes;
	private boolean enableRoundOffAmt;
	
	private boolean enableDiscount; 
	private boolean enableQuantity; 
	private boolean enableRate;
	private boolean enableState;
	private boolean enablePan;
	private boolean enablePlaceOfSupply;
	
	private Map<String, List<Permission>> permissions = MapUtils.lazyMap(new HashMap<String,List<Permission>>(), new Factory() {
        public Object create() {
            return LazyList.decorate(new ArrayList<Permission>(), 
                           FactoryUtils.instantiateFactory(Permission.class));
        }
    });
	
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public Map<String, List<Permission>> getPermissions() {
		return permissions;
	}
	public void setPermissions(Map<String, List<Permission>> permissions) {
		this.permissions = permissions;
	}
	public String getInvoiceText() {
		return invoiceText;
	}
	public void setInvoiceText(String invoiceText) {
		this.invoiceText = invoiceText;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getPanno() {
		return panno;
	}
	public void setPanno(String panno) {
		this.panno = panno;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPlaceOfSupply() {
		return placeOfSupply;
	}
	public void setPlaceOfSupply(String placeOfSupply) {
		this.placeOfSupply = placeOfSupply;
	}
	
	  public boolean isEnableDiscount() { 
		  return enableDiscount; 
		  }
	  public void setEnableDiscount(boolean enableDiscount) { 
		  this.enableDiscount = enableDiscount; 
	 } 
	  public boolean isEnableQuantity() { 
		  return enableQuantity;
	  } 
	  public void setEnableQuantity(boolean enableQuantity) { 
		  this.enableQuantity  = enableQuantity; 
	 } 
	  public boolean isEnableRate() {
		  return enableRate; 
	}
	  public void setEnableRate(boolean enableRate) {
		  this.enableRate = enableRate;
	  } 
	  public boolean isEnableState() { 
		  return enableState; 
		} 
	  public void setEnableState(boolean enableState) { 
		  this.enableState = enableState; 
		}
	  public boolean isEnablePan() { 
		  return enablePan; 
		} 
	  public void setEnablePan(boolean enablePan) { 
		  this.enablePan = enablePan; 
		} 
	  public boolean isEnablePlaceOfSupply() { 
		  return enablePlaceOfSupply; 
	 }
	  public void setEnablePlaceOfSupply(boolean enablePlaceOfSupply) {
	  this.enablePlaceOfSupply = enablePlaceOfSupply; 
	  }
	  public boolean isIsfooternotescheck() {
			return isfooternotescheck;
		}
		public void setIsfooternotescheck(boolean isfooternotescheck) {
			this.isfooternotescheck = isfooternotescheck;
		}
		public String getFooternotes() {
			return footernotes;
		}
		public void setFooternotes(String footernotes) {
			this.footernotes = footernotes;
		}
		public String getQtyText() {
			return qtyText;
		}
		public void setQtyText(String qtyText) {
			this.qtyText = qtyText;
		}
		public String getRateText() {
			return rateText;
		}
		public void setRateText(String rateText) {
			this.rateText = rateText;
		}
		public boolean isEnableRoundOffAmt() {
			return enableRoundOffAmt;
		}
		public void setEnableRoundOffAmt(boolean enableRoundOffAmt) {
			this.enableRoundOffAmt = enableRoundOffAmt;
		}
		public String getAuthSignText() {
			return authSignText;
		}
		public void setAuthSignText(String authSignText) {
			this.authSignText = authSignText;
		}
		public String getEinvoiceHeaderText() {
			return einvoiceHeaderText;
		}
		public void setEinvoiceHeaderText(String einvoiceHeaderText) {
			this.einvoiceHeaderText = einvoiceHeaderText;
		}
	
}
