package com.zengshi.butterfly.core.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtils  {

	/** yyyy-MM-dd HH:mm:ss **/
	public static final String  DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/**yyyy-MM-dd **/
	public static final String  DATE_FORMAT2 = "yyyy-MM-dd";
	/** yyyy	 */
	public static final String  DATE_FORMAT3 = "yyyy";
	/** yyyyMMdd	 */
	public static final String  DATE_FORMAT4 = "yyyyMMdd";
	/** yyyyMM	 */
	public static final String  DATE_FORMAT5 = "yyyyMM";
	/** MMdd	 */
	public static final String  DATE_FORMAT6 = "MMdd";
	
	private static final SimpleDateFormat format_yyyyMMddHH24Miss=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat format_yyyyMMdd=new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat format_yyyyMM=new SimpleDateFormat("yyyyMM");
	private static final SimpleDateFormat format_MMdd=new SimpleDateFormat("MMdd");
	private static final SimpleDateFormat format_yyyy=new SimpleDateFormat("yyyy");
	
	
	private static final SimpleDateFormat format_dd=new SimpleDateFormat("dd");
	
	
	public static Timestamp getCurrent() {
		return new Timestamp(getTime().getTimeInMillis());
	}
	
	/**
	 * <p>描述: 获得任意格式日期字符串</p> 
	 * @param pattern
	 * @return:       String   
	 * @Date          2013-5-27 上午10:13:05
	 */
	public static String getDateString(String pattern) {
		DateFormat dfmt = new SimpleDateFormat(pattern);
		return dfmt.format(new Date());
	}
	
	 /**
     * 得到当前日期字符串,默认为格式为：yyyy-MM-dd HH:mm:ss
     * @param dateFormat
     * @return
     */
    public static String getTime(String dateFormat){        
      Calendar now=Calendar.getInstance();     
      return format_yyyyMMddHH24Miss.format(now.getTime()); 
    }
   
    /**
     * 日期按dateFormat的格式，格式化成字符串
     * @param timeMillis
     * @param dateFormat
     * @return
     */
    public static String parse(long timeMillis,String dateFormat) {
    	SimpleDateFormat sf=new SimpleDateFormat(dateFormat);
    	return sf.format(new Date(timeMillis));
    }
    
    public static Date formatDate(String date,String dateFormat) throws ParseException
    {
    	SimpleDateFormat sf=new SimpleDateFormat(dateFormat);
    	return sf.parse(date);
    }
    
    
    public static Calendar getTime() {
    	Calendar time=Calendar.getInstance();
    	return time;
    }
    
    public static Calendar getTime(long timeMillis) {
    	Calendar time=getTime();
    	time.setTimeInMillis(timeMillis);
    	return time;
    }
    public static Date getDate() {
    	return getTime().getTime();
    }
    public static Date getDate(long timeMillis) {
    	return getTime(timeMillis).getTime();
    }
    public static String getYYYY() {
    	return format_yyyy.format(getDate());
    }
    
    public static String getYYYY(long timeMillis) {
    	return format_yyyy.format(getDate(timeMillis));
    }
    
    public static String getYYYMMDD() {
    	return format_yyyyMMdd.format(getDate());
    }
    public static String getYYYYMMDD(long time) {
    	return format_yyyyMMdd.format(getDate(time));
    }
    public static String getYYYYMM() {
    	return format_yyyyMM.format(getDate());
    }
    public static String getYYYYMM(long timeMillis) {
    	return format_yyyyMM.format(getDate(timeMillis));
    }
    public static String getMMDD() {
    	return format_MMdd.format(getTime());
    }
    public static String getMMDD(long timeMillis) {
    	return format_MMdd.format(getDate(timeMillis));
    }
    public static String getDD(long timeMillis) {
    	return format_dd.format(getDate(timeMillis));
    }

    public static void main(String[] arg) {
    	System.out.println(DateUtils.getYYYY());
    }
    
    public static Date addMonth(Date date, int amount) {
        return add(date, Calendar.MONTH, amount);
    }
    
    public static Date addDay(Date date, int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }
    
    public static Date add(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }
  
    public static Timestamp getTimestamp(String time) {
    	  Timestamp ts = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			ts = new Timestamp(format.parse(time).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return ts;
    }
    
    public static Timestamp getTimestampNext(String time) {
  	  Timestamp ts = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	        Calendar c = Calendar.getInstance();
	        c.setTime(format.parse(time));
	        c.add(Calendar.DATE, 1);
			ts = new Timestamp(c.getTimeInMillis());
		} catch (ParseException e) {
			e.printStackTrace();
		}
  	return ts;
  }
}
