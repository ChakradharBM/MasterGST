package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import com.mastergst.configuration.service.Coupons;

public interface CouponService {
	Coupons getCoupon(final String code);
	
	 List<Coupons> getAllCoupon();

}
