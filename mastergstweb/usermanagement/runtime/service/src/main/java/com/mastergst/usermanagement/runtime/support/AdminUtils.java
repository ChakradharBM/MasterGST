package com.mastergst.usermanagement.runtime.support;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
public class AdminUtils {
	
	
	public Criteria getExpiredUsersSearchValueCriteria(String searchVal) {
		List<Criteria> criterias = new ArrayList<Criteria>();
	 	criterias.add(Criteria.where("user.fullname").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("user.email").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("user.mobilenumber").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("user.type").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("apiType").regex(searchVal, "i"));
			 	
	 	return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}
	
	
	public Criteria getPartnerClientSearchValueCriteria(String searchVal) {
		
		List<Criteria> criterias = new ArrayList<Criteria>();
	 	criterias.add(Criteria.where("userid").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("amount").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("apiType").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("mthCd").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("yrCd").regex(searchVal, "i"));
	 	
	 	criterias.add(Criteria.where("partnerPayment").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("partnerPayment").regex(searchVal, "i"));
	 	    
	 	String paymentDate = searchVal;
 		if(paymentDate.indexOf("/") != -1) {
 			String paydate[] = paymentDate.split("/");
 			String strMonth= "";
 			if(paydate.length >= 2) {
 				boolean isNumber = NumberUtils.isNumber(paydate[1]);
 				if(isNumber) {
 					int mnth = Integer.parseInt(paydate[1]);
 					strMonth = mnth < 10 && mnth > 0 ? "0" + mnth : mnth + "";
 				}
 			}

 			if(paydate.length == 2) {
 				paymentDate = paydate[0]+"/"+strMonth;
 			}else if(paydate.length == 3) {
 				paymentDate = paydate[0]+"/"+strMonth+"/"+paydate[2];
 			}
 			criterias.add(Criteria.where("paymentDate").regex(paymentDate, "i"));
 		}else {
 			criterias.add(Criteria.where("paymentDate").regex(searchVal, "i"));
 		}
			 	
	 	return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}

}
