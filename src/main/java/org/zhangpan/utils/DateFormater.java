package org.zhangpan.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormater {

	private String timeFormat = "HH:mm:ss"; //hh时(大写为24进制,小写为12进制)
	private String dateFormat = "yyyy-MM-dd";
	private String dateAndTimeFormat = "yyyy-MM-dd HH:mm:ss";
	
	private SimpleDateFormat simpleDateFormat = null;
	
	private static DateFormater dateFormater = null;
	
	private DateFormater(){
		
	}
	
	public static DateFormater getInstance(){
		
		if(dateFormater == null){
			dateFormater = new DateFormater();
		}
		return dateFormater;
	}
	
	public void setFormat(String format){
		this.timeFormat = format;
		this.simpleDateFormat = new SimpleDateFormat(this.timeFormat);
	}
	
	public String getFormat(){
		return this.timeFormat;
	}
	
	/**
	 * 根据默认格式的时间字符串获取时间对象
	 * 
	 * @param date
	 * @return
	 */
	public String getTimeStr(Date date){
		if(date == null){
			return "00:00:00";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(this.timeFormat);	// hh时(大写为24进制,小写为12进制)
		return sdf.format(date);
	}
	
	/**
	 * 获取默认格式的时间字符串
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public String getTimeStr(Date date, String format){
		if(date == null || format == null){
			return "00:00:00";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	/**
	 * 根据默认格式的时间字符串获取时间对象
	 * 
	 * @param str            时间字符串
	 * @param format         时间格式
	 * @return Date          时间对象
	 */
	public Date getDate(String str, String format){
		if(str == null || format == null){
			return null;
		}
		
		Date date = null;
		SimpleDateFormat tmpSimpleDateFormat = new SimpleDateFormat(format);

		try {  
			date = tmpSimpleDateFormat.parse(str);  
		}  
		catch (Exception ex) { 
			ex.printStackTrace();
			System.out.println(ex.getMessage());  
		} 
		return date;
	}
	
	/**
	 * 根据字符串格式的("HH:mm:ss")获得时间
	 * 
	 * @param date
	 * @return
	 */
	public Date getTime(String timeStr){
		if(timeStr == null){
			return null;
		}
		Date date = null;
		
		simpleDateFormat = new SimpleDateFormat(this.timeFormat);
		
		try {  
			date = simpleDateFormat.parse(timeStr); 
		}
		catch (Exception ex) {
			ex.printStackTrace();
		} 
		return date;
	}
	
	/**
	 * 根据自定义格式的字符串格式的获得时间
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public Date getTime(String timeStr, String format){
		if(timeStr == null){
			return null;
		}
		Date date = null;
		simpleDateFormat = new SimpleDateFormat(format);
		
		try {  
			date = simpleDateFormat.parse(timeStr); 
		}
		catch (Exception ex) {  
			ex.printStackTrace();
			System.out.println(ex.getMessage()); 
		} 
		return date;
	}
	
	/**
	 * 根据字符串格式的("yyyy-MM-dd")获得日期
	 * 
	 * @param date
	 * @return
	 */
	public Date getDate(String dateStr){
		if(dateStr == null){
			return null;
		}
		
		Date date = null;
		simpleDateFormat = new SimpleDateFormat(this.dateFormat);
		
		try {  
			date = simpleDateFormat.parse(dateStr); 
		}  
		catch (Exception ex) {  
			ex.printStackTrace();
			System.out.println(ex.getMessage());  
		} 
		return date;
	}
	
	/**
	 * 根据字符串格式的("yyyy-MM-dd HH:mm:ss")获得日期
	 * 
	 * @param dateAndTime
	 * @return
	 */
	public Date getDateAndTime(String dateAndTime){
		if(dateAndTime == null){
			return null;
		}
		Date date = null;
		simpleDateFormat = new SimpleDateFormat(this.dateAndTimeFormat);
		
		try {  
			date = simpleDateFormat.parse(dateAndTime); 
		}  
		catch (Exception ex) {  
			ex.printStackTrace();
			System.out.println(ex.getMessage());  
		}
		return date;
	}

	/**
	 * @return
	 */
	public String getDateStr(Date date) {
		if(date == null || dateFormat == null){
			return "2012-01-01";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(date);
	}
	
	/**
	 * 
	 * @param date
	 * @param time
	 * @return
	 */
	public String getDateAndTimeStr(Date date, Date time) {
		
		SimpleDateFormat sdf = null;
		
		if(dateFormat == null || timeFormat == null){
			sdf = new SimpleDateFormat(dateAndTimeFormat);
			return sdf.format(new Date());
		}
		
		sdf = new SimpleDateFormat(dateFormat);
		String dateStr = null;
		if (date != null) {
			dateStr = sdf.format(date);
		}else{
			dateStr = sdf.format(new Date());
		}
		
		sdf = new SimpleDateFormat(timeFormat);
		String timeStr = null;
		if (date != null) {
			timeStr = sdf.format(time);
		}else{
			timeStr = sdf.format(new Date());
		}
		
		String dateAndTimeStr = dateStr + " " + timeStr;
		
		return dateAndTimeStr;
	}
	
	/**
	 * 获取时间和日期字符串
	 * @param date
	 * @param format
	 * @return
	 */
	public String getDateAndTimeStr(Date date, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	/**
	 * 获取时间和日期字符串
	 * @param date
	 * @return
	 */
	public String getDateAndTimeStr(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat(dateAndTimeFormat);
		return sdf.format(date);
	}
	
	
}
