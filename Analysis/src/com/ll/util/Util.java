package com.ll.util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Util {
	
	public static Date strToDate(String dateStr){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static int getDayOfWeek(String dateStr){
		Calendar cal = Calendar.getInstance();
		cal.setTime(Util.strToDate(dateStr));
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		if(dayOfWeek==1){
			dayOfWeek = 7;
		}else{
			dayOfWeek = dayOfWeek-1;
		}
		return dayOfWeek;
	}

}
