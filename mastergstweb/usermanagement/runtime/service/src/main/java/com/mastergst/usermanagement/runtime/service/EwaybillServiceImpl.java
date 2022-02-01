package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.ParseException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.icu.text.NumberFormat;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.EWAYBILL;
import com.mastergst.usermanagement.runtime.domain.ewaybill.ExtendValidity;
import com.mastergst.usermanagement.runtime.domain.ewaybill.ExtendValidityDO;

@Service
@Transactional(readOnly = true)
public class EwaybillServiceImpl implements EwaybillService {

	@Override
	public ExtendValidity extendValidityData(Client client, EWAYBILL ewaybill, ExtendValidityDO ebillData) throws ParseException {
		ExtendValidity evalidity = null;
		if(isNotEmpty(ewaybill)) {
			evalidity = new ExtendValidity();
			if(isNotEmpty(ewaybill.getEwayBillNumber())) {
				evalidity.setEwbNo(NumberFormat.getInstance().parse(ewaybill.getEwayBillNumber()));
			}
			if(isNotEmpty(ewaybill.getFromPincode())) {
				evalidity.setFromPincode(ewaybill.getFromPincode());
			}
			
			if(isNotEmpty(client)) {
				if(isNotEmpty(client.getStatename())) {
					String[] s = client.getStatename().split("-");
					evalidity.setFromPlace(s[1]);
					evalidity.setFromState(Integer.parseInt(s[0]));
				}
			}
			if(isNotEmpty(ebillData)) {
				if(isNotEmpty(ebillData.getExtnRsnCode())) {
					evalidity.setExtnRsnCode(ebillData.getExtnRsnCode());
				}
				if(isNotEmpty(ebillData.getExtnRemarks())) {
					evalidity.setExtnRemarks(ebillData.getExtnRemarks());
				}
				if(isNotEmpty(ebillData.getRemainingDistance())) {
					evalidity.setRemainingDistance(ebillData.getRemainingDistance());
				}
			}
			if(isNotEmpty(ewaybill.getVehiclListDetails()) && ewaybill.getVehiclListDetails().size() > 0) {
				if(isNotEmpty(ewaybill.getVehiclListDetails().get(0).getVehicleNo())) {
					evalidity.setVehicleNo(ewaybill.getVehiclListDetails().get(0).getVehicleNo());
				}
				if(isNotEmpty(ewaybill.getVehiclListDetails().get(0).getTransDocNo())) {
					evalidity.setTransDocNo(ewaybill.getVehiclListDetails().get(0).getTransDocNo());
				}
				if(isNotEmpty(ewaybill.getVehiclListDetails().get(0).getTransDocDate())) {
					evalidity.setTransDocDate(ewaybill.getVehiclListDetails().get(0).getTransDocDate());
				}
				if(isNotEmpty(ewaybill.getVehiclListDetails().get(0).getTransMode())) {
					evalidity.setTransMode(ewaybill.getVehiclListDetails().get(0).getTransMode());
					if("5".equals(ewaybill.getVehiclListDetails().get(0).getTransMode())) {
						evalidity.setConsignmentStatus("T");
						if(isNotEmpty(ebillData.getTransitType())) {
							evalidity.setTransitType(ebillData.getTransitType());
						}
					}else {
						evalidity.setConsignmentStatus("M");
						evalidity.setTransitType("");
					}
				}
				
			}
		}
		return evalidity;
	}
	
}
