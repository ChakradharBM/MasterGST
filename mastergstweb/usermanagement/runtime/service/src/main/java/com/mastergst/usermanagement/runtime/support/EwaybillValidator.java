package com.mastergst.usermanagement.runtime.support;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class EwaybillValidator {
	
	private static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public static boolean validUpto(String validDate) {
		boolean flag = false;
		try {
			Date validateDate = format.parse(validDate);
			long diff = (validateDate.getTime() - new Date().getTime())/1000;
			long diffHours = TimeUnit.SECONDS.toHours(diff);
			diff -= TimeUnit.HOURS.toSeconds (diffHours);
			long diffMinutes = TimeUnit.SECONDS.toMinutes(diff);
			if(diffHours >= -8 && diffHours <= 8) {
				if(diffHours == -8 || diffHours == 8) {
					if(diffMinutes > 0) {
						flag = false;
					}else {
						flag = true;
					}
				}else {
					flag = true;
				}
			}
		}catch (Exception e){
			flag = false;
		}
		return flag;		
	}
}



