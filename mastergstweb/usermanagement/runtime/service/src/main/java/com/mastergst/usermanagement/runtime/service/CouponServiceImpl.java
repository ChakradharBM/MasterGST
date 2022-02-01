package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mastergst.configuration.service.CouponRepository;
import com.mastergst.configuration.service.Coupons;

@Service
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService{

	@Autowired
	private CouponRepository couponRepository;
	
	@Override
	public Coupons getCoupon(String code) {
		return couponRepository.findByCode(code);
	}
	
	@Override
	public List<Coupons> getAllCoupon() {
		return couponRepository.findAll();
	}

}
