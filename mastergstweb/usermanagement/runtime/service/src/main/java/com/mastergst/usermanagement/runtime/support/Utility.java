package com.mastergst.usermanagement.runtime.support;

import java.util.Date;

import com.ibm.icu.text.SimpleDateFormat;
import com.mastergst.core.util.NullUtil;

public class Utility {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");

	public static String getYearCode(int month, int year){
		return month<4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
	}
	
	public static String getFormatedDateStr(Date date){
		if(NullUtil.isNotEmpty(date)) {
			return DATE_FORMAT.format(date);
		}else {
			return "";
		}
	}
}
