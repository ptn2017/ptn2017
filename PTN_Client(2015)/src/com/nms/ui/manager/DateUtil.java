package com.nms.ui.manager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static final String FULLTIME = "yyyy-MM-dd HH:mm:ss";
	public static final String FULLTIMESDF = "yyyyMMddHHmmss";

	/**
	 * 获取系统当前时间
	 */
	public static String getDate(String format) {
		Date date = new Date();
		SimpleDateFormat sfmt = new SimpleDateFormat(format);
		return sfmt.format(date);
	}
	
	/**
	 * 获取系统当前时间后一分钟
	 */
	public static String getDateMin(String format) {
		Date date = new Date();
		date.setMinutes(date.getMinutes()+1);
		SimpleDateFormat sfmt = new SimpleDateFormat(format);
		return sfmt.format(date);
	}

	/**
	 * 获取系统当前时间后一分钟
	 */
	public static String getDateMin(String format,String time) {
		SimpleDateFormat sfmt = new SimpleDateFormat(format);
		try {
			long timeLong = sfmt.parse(time).getTime()+1000*60;
			Date date = new Date(timeLong);
			return sfmt.format(date);
		} catch (Exception e) {
			ExceptionManage.dispose(e, DateUtil.class);
		}
		return "";
	}
	/**
	 * 日期格式转换
	 * 
	 * @throws ParseException
	 */
	public static String strDate(String strDate, String format) throws ParseException {
		if(strDate != null){
		Date date = new Date();
		SimpleDateFormat sfmt = new SimpleDateFormat(format);
		date = sfmt.parse(strDate);
		sfmt.format(date);
		return sfmt.format(date);
		}else{
			return null;
		}
	}

	/**
	 * String类型转换成日期格�?	 * 
	 * @param dateStr
	 * @param formatStr
	 * @return
	 */
	public static Date StringToDate(String dateStr, String formatStr) {
		DateFormat sdf = new SimpleDateFormat(formatStr);
		Date date = null;
		try {	
			if(dateStr.split("-").length>1){
				date = sdf.parse(dateStr);
			}else{// 字符  毫秒数转为Date
				date = new Date(Long.valueOf(dateStr));
			}
			
		} catch (ParseException e) {
			ExceptionManage.dispose(e,DateUtil.class);
		}
		return date;
	}
	
	/**
	 * DATE类型转换为日期类�?	 * @param date
	 * @return
	 */
	 public static String getDate(Date date , String formatStr) {
		SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
	    return formatter.format(date);
    }

	/**
	 * 修改时间的格�?	 * 
	 * @param updateTime
	 * @return
	 */
	public static String updateTime(String strDate, String format) {
		if (strDate == null) {
			return null;
		}
		String newTime = null;
		SimpleDateFormat sdf = null;
		try {
			sdf = new SimpleDateFormat(format);
			newTime = sdf.format(sdf.parse(strDate));

		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		}
		return newTime;
	}
	/**
	 * 修改时间的格�?	 * 
	 * @param updateTime
	 * @return
	 */
	public static long updateTimeToLong(String strDate, String format) {
		if (strDate == null) {
			return 0;
		}
		SimpleDateFormat sdf = null;
		try {
			sdf = new SimpleDateFormat(format);
	    	return 	sdf.parse(strDate).getTime();

		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		}
		return 0;
	}
	
	/**
	 * 修改时间的格�?	 * 
	 * @param updateTime
	 * @return
	 */
	public static String updateTimeToString(long strDate, String format) {
		SimpleDateFormat sdf = null;
		Date date  = new Date(strDate);
		try {
			sdf = new SimpleDateFormat(format);
	    	return 	sdf.format(date);

		} catch (Exception e) {
			ExceptionManage.dispose(e, UiUtil.class);
		}
		return "";
	}
	
}
