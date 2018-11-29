package com.sc.data.scoupon.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyDateUtil {
	
//	private String parseDate(String date,String format) {
//		SimpleDateFormat sdf = new SimpleDateFormat(format);
//		
//		Date d = new Date();
//		SimpleDateFormat formatter = new SimpleDateFormat(format);
//		Date strtodate = formatter.format(arg0);
//		return format;
//
//	}
	public Date getYestoday(){
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE,-1);
		Date time=cal.getTime();
		return time;
	}
	public String getXDay(int x,String format){
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date time= null;
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE,x);
		time=cal.getTime();		
		return formatter.format(time);
	}
	public Date getDayMonthAgo(){
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.MONTH,-1);
		Date time=cal.getTime();
		return time;
	}
	public String getDayXMonth(int x,String format){
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date time= null;
		if (x>=-12&&x<=12){
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.MONTH,x);
			time = cal.getTime();
		}
		return formatter.format(time);
	}
	public String getTimeXSecond(int x,String format){
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.SECOND,x);
		Date time = cal.getTime();
		return formatter.format(time);
	}
	public Date getDayMonthNext(){
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.MONTH,1);
		Date time=cal.getTime();
		return time;
	}
	public String getFormatFirstDayThisMonth(){
		Calendar c = Calendar.getInstance();
		Date time = c.getTime();
		SimpleDateFormat sdfm = new SimpleDateFormat("yyyyMM");
		return sdfm.format(time)+"01";
	}
}
