package com.mastergst.usermanagement.runtime.support;

import java.util.Map;

import com.google.common.collect.Maps;
import com.ibm.icu.util.Calendar;

public class MonthCodes {
	
	private static Map<String, String> fpMap = Maps.newHashMap();
	static{
		fpMap.put("JANUARY", "01");
		fpMap.put("FEBRUARY", "02");
		fpMap.put("MARCH", "03");
		fpMap.put("APRIL", "04");
		fpMap.put("MAY", "05");
		fpMap.put("JUNE", "06");
		fpMap.put("JULY", "07");
		fpMap.put("AUGUST", "08");
		fpMap.put("SEPTEMBER", "09");
		fpMap.put("OCTOBER", "10");
		fpMap.put("NOVEMBER", "11");
		fpMap.put("DECEMBER", "12");
	}
	
	public static String getCodes(String monthFullName, String year) {

		if(monthFullName == null || year == null) {
			
			Calendar cal = Calendar.getInstance();
			int month = cal.get(Calendar.MONTH);
	        int yr = cal.get(Calendar.YEAR);
	        
	        String mthCd = month <= 9 ? "0"+month : ""+month;
	        return mthCd + yr;
		}
		String yrCd = "JANUARY".equals(monthFullName) || "FEBRUARY".equals(monthFullName) || "MARCH".equals(monthFullName) ? "20"+year.split("-")[1].trim() : year.split("-")[0].trim();
		 
		return fpMap.get(monthFullName)+yrCd;
	}

}
