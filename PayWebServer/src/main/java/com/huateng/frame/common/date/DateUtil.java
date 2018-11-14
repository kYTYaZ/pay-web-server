/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: DateUtil.java
 * Author:   justin
 * Date:     2014-7-23 下午7:49:01
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.frame.common.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.pay.common.util.StringUtil;

/**
 * 常用的日期转换类
 * 
 * @author sunguohua
 */
public class DateUtil {
	
	private static Logger logger = LoggerFactory.getLogger(DateUtil.class);
	
    public static String defaultSimpleFormater = "yyyy-MM-dd hh:mm:ss";
    public static String defaultSimpleFormater2 = "yyyyMMdd HH:mm:ss";
    public static final String YYMM = "yyyyMM";

    public static final String DDHHMMSS = "ddHHmmss";

    public static final String YYMMDDHHMMSSSSS = "yyMMddHHmmssSSS";

    public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";

    public static final String YYMMDD = "yyMMdd";

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYYMMDD = "yyyyMMdd";
    
    public static final String YYYYMMDDFORMAT="yyyy-MM-dd";
    
    public static final String YYYYMMDDHHHMMSSFORMAT="yyyy-MM-ddHH:mm:ss";

    public static final String HHMMSS = "HHmmss";
    
    public static final String YYYY = "yyyy";
    
    public static final int YEAR = 0;
    
    public static final int MONTH = 1;
    
    public static final int DAY = 2;
    
    public static final int HOUR = 3;
    
    public static final int MINUTE = 4;
    
    public static final int SECOND = 5;
    /**
     * 默认简单日期字符串
     * 
     * @return
     */
    public static String getDefaultSimpleFormater() {
        return defaultSimpleFormater;
    }

    /**
     * 设置默认简单日期格式字符串
     * 
     * @param defaultFormatString
     */
    public static void setDefaultSimpleFormater(String defaultFormatString) {
        DateUtil.defaultSimpleFormater = defaultFormatString;
    }

    /**
     * 格式化日期
     * 
     * @param date
     * @param formatString
     * @return
     */
    public static String format(Date date, String formatString) {
        if (date == null) {
            return null;
        } else {
            SimpleDateFormat df = new SimpleDateFormat(formatString);
            return df.format(date);
        }
    }

    /**
     * 格式化日期(使用默认格式)
     * 
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, defaultSimpleFormater);
    }

    /**
     * 转换成日期
     * 
     * @param dateString
     * @param formatString
     * @return
     */
    public static Date parse(String dateString, String formatString) {
        SimpleDateFormat df = new SimpleDateFormat(formatString);
        try {
            return df.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 转换成日期(使用默认格式)
     * 
     * @param dateString
     * @return
     */
    public static Date parse(String dateString) {
        return parse(dateString, defaultSimpleFormater);
    }

    /**
     * 昨天
     * 
     * @return
     */
    public static Date yesterday() {
        return addDay(-1);
    }

    /**
     * 明天
     * 
     * @return
     */
    public static Date tomorrow() {
        return addDay(1);
    }

    /**
     * 现在
     * 
     * @return
     */
    public static Date now() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 按日加
     * 
     * @param value
     * @return
     */
    public static Date addDay(int value) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_YEAR, value);
        return now.getTime();
    }

    /**
     * 按日加,指定日期
     * 
     * @param date
     * @param value
     * @return
     */
    public static Date addDay(Date date, int value) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.DAY_OF_YEAR, value);
        return now.getTime();
    }

    /**
     * 获取本月1日
     * 
     * @return
     */
    public static Date getCureentMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date getCurrentDayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();

    }

    public static Date getCurrentDayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();

    }

    /**
     * 获取本月最后一天
     * 
     * @return
     */
    public static Date getCureentMonthLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取指定日期的1号
     * 
     * @return
     */
    public static Date getMonthFirstDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取指定日期月份的最后一天
     * 
     * @return
     */
    public static Date getMonthLastDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Date getMonthLastDay2(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 按月加
     * 
     * @param value
     * @return
     */
    public static Date addMonth(int value) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MONTH, value);
        return now.getTime();
    }

    /**
     * 按月加,指定日期
     * 
     * @param date
     * @param value
     * @return
     */
    public static Date addMonth(Date date, int value) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.MONTH, value);
        return now.getTime();
    }

    /**
     * 按年加
     * 
     * @param value
     * @return
     */
    public static Date addYear(int value) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.YEAR, value);
        return now.getTime();
    }

    /**
     * 按年加,指定日期
     * 
     * @param date
     * @param value
     * @return
     */
    public static Date addYear(Date date, int value) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.YEAR, value);
        return now.getTime();
    }

    /**
     * 按小时加
     * 
     * @param value
     * @return
     */
    public static Date addHour(int value) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR_OF_DAY, value);
        return now.getTime();
    }

    /**
     * 按小时加,指定日期
     * 
     * @param date
     * @param value
     * @return
     */
    public static Date addHour(Date date, int value) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.HOUR_OF_DAY, value);
        return now.getTime();
    }

    /**
     * 按分钟加
     * 
     * @param value
     * @return
     */
    public static Date addMinute(int value) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, value);
        return now.getTime();
    }

    /**
     * 按分钟加,指定日期
     * 
     * @param date
     * @param value
     * @return
     */
    public static Date addMinute(Date date, int value) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.add(Calendar.MINUTE, value);
        return now.getTime();
    }

    /**
     * 年份
     * 
     * @return
     */
    public static int year() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR);
    }

    /**
     * 月份
     * 
     * @return
     */
    public static int month() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.MONTH);
    }

    /**
     * 日(号)
     * 
     * @return
     */
    public static int day() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 小时(点)
     * 
     * @return
     */
    public static int hour() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.HOUR);
    }

    /**
     * 分钟
     * 
     * @return
     */
    public static int minute() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.MINUTE);
    }

    /**
     * 秒
     * 
     * @return
     */
    public static int second() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.SECOND);
    }

    /**
     * 星期几(礼拜几)
     * 
     * @return
     */
    public static int weekday() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 是上午吗?
     * 
     * @return
     */
    public static boolean isAm() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.AM_PM) == 0;
    }

    /**
     * 是下午吗?
     * 
     * @return
     */
    public static boolean isPm() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.AM_PM) == 1;
    }

    public static String getDDHHMMSS() {
        return getDateStr(DDHHMMSS);
    }

    public static String getDateStr(String formatPattern) {
        Date date = new Date();
        return getDateStr(date, formatPattern);
    }
    
    public static String getDateYYYYMMDD() {
        Date date = new Date();
        return getDateStr(date, YYYYMMDD);
    }
    
    public static String getDateHHMMSS() {
        Date date = new Date();
        return getDateStr(date, HHMMSS);
    }
    
    private static String getDateStr(Date date, String formatPattern) {
     
    	if (date == null){
        	 date = new Date();
        }
           
        SimpleDateFormat df = new SimpleDateFormat(formatPattern);
        String dateStr = df.format(date);
      
        return dateStr;
    }
    
    
    

    /**
     * 
     * 字符串转日期对象
     * 
     * @param dateStr 日期字符串
     * @param formatPattern 日期格式
     * @return
     * @see 1.0
     * @since 1.0
     */
    public static Date str2Date(String dateStr, String formatPattern) {
        SimpleDateFormat df = new SimpleDateFormat(formatPattern);
        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
        	logger.error(e.getMessage(),e);
        }
        return date;
    }

    /**
     * 
     * 日期对象转字符串
     * 
     * @param date 日期对象
     * @param formatPattern 格式
     * @return
     * @see 1.0
     * @since 1.0
     */
    public static String date2Str(Date date, String formatPattern) {
        SimpleDateFormat df = new SimpleDateFormat(formatPattern);
        String dateStr = df.format(date);
        return dateStr;
    }
    
    
    /**
     * 根据模式返回一个新的SimpleDateFormat,防止在多线程的时候出现混乱
     * 
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }
    /**
     * 获得系统当前日期时间
     * 
     * @return
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = getSimpleDateFormat(YYYYMMDDHHMMSS);
        return sdf.format(Calendar.getInstance().getTime());
    }
    
    /**
     * 获得系统当前日期时间
     * 
     * @return
     */
    public static String getCurrentDateTimeFormat(String format) {
    	SimpleDateFormat sdf = getSimpleDateFormat(format);
    	return sdf.format(Calendar.getInstance().getTime());
    }
    
    /**
     * 获取过去地几天的日期
     * param:past  天数
     */
    
    public static String getPastDate(int past) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
    	Date currentDate = calendar.getTime();
    	SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");//modified by ghl at 20170830  修改日期格式yyyy-MM-dd为yyyyMMdd
    	String result = format.format(currentDate);
    	return result;
    }
    
    /**
	 * yyyyMMdd格式转为yyyy-MM-dd格式
	 * @param date
	 * @return
	 */
	
	public static String dateFormat(String dateStr) {
		Date date = str2Date(dateStr, YYYYMMDD);
		SimpleDateFormat format = new SimpleDateFormat(YYYYMMDDFORMAT);
    	String result = format.format(date);
    	return result;
	}
	
	
	/**
	 * HHmmss格式转为HH:mm:ss格式
	 * @param time
	 * @return
	 */
	public static String tmFormat(String dateStr) {
		Date date = str2Date(dateStr, YYYYMMDDHHMMSS);
		return getDateStr(date, YYYYMMDDHHHMMSSFORMAT);
	
	}
    
    
    /**
     * 比较时间偏移
     * @param beginTime
     * @param offset
     * @param endTime
     * @param dateFormat
     * @return
     */
    public static boolean compareDate(String beginTime,int offset,int offsetPosition,String endTime,String dateFormat){
    	 
    	if(StringUtil.isEmpty(dateFormat)){
    		dateFormat = YYYYMMDDHHMMSS;
    	}
    	
    	DateTime beginOffestDateTime = new DateTime();
    	
    	DateTimeFormatter  fis = DateTimeFormat.forPattern(dateFormat);
    	
    	switch(offsetPosition){
    		case YEAR:
    			beginOffestDateTime = fis.parseDateTime(beginTime).plusYears(offset);
    		break;	
    		case MONTH:
    			beginOffestDateTime = fis.parseDateTime(beginTime).plusMonths(offset);
    		break;	
    		case DAY:
    			beginOffestDateTime = fis.parseDateTime(beginTime).plusDays(offset);
    		break;	
    		case HOUR:
    			beginOffestDateTime = fis.parseDateTime(beginTime).plusHours(offset);
    		break;	
    		case MINUTE:
    			beginOffestDateTime = fis.parseDateTime(beginTime).plusMinutes(offset);
    		break;	
    		case SECOND:
    			beginOffestDateTime = fis.parseDateTime(beginTime).plusSeconds(offset);
    		break;	
    		default:
    			break; 
    		
    	}
    	
		DateTime endDateTime =  fis.parseDateTime(endTime);
		
		if(endDateTime.getMillis() < beginOffestDateTime.getMillis()){
			return true;
		}
		
		return false;
    }
    
    public static String getOffsetDate(String beginTime,int offset,int offsetPosition,String dateFormat){
    	
    	if(StringUtil.isEmpty(dateFormat)){
    		dateFormat = YYYYMMDDHHMMSS;
    	}
    	
    	DateTime beginOffestDateTime = new DateTime();
    	
    	DateTimeFormatter  fis = DateTimeFormat.forPattern(dateFormat);
    	
    	switch(offsetPosition){
    		case YEAR:
    			beginOffestDateTime = fis.parseDateTime(beginTime).plusYears(offset);
    		break;	
    		case MONTH:
    			beginOffestDateTime = fis.parseDateTime(beginTime).plusMonths(offset);
    		break;	
    		case DAY:
    			beginOffestDateTime = fis.parseDateTime(beginTime).plusDays(offset);
    		break;	
    		case HOUR:
    			beginOffestDateTime = fis.parseDateTime(beginTime).plusHours(offset);
    		break;	
    		case MINUTE:
    			beginOffestDateTime = fis.parseDateTime(beginTime).plusMinutes(offset);
    		break;	
    		case SECOND:
    			beginOffestDateTime = fis.parseDateTime(beginTime).plusSeconds(offset);
    		break;	
    		default:
    			break; 
    	}
    	
		return beginOffestDateTime.toString(fis);
    }
    
    /**
     * 获取描述
     * @return
     */
    public static String getMillisSeconde(){
    	DateTime dateTime = new DateTime();
    	String millisSeconde = String.valueOf(dateTime.getMillis() / 1000) ;
    	return millisSeconde;
    }
    
    
    public static void main(String[] args) {
    	System.out.println(getYear(""));
	}
    
    
    public static String getYear(String date){
		String year = DateUtil.getDateStr(DateUtil.YYYY);
		if (date.equals("1231")) {
			year = DateUtil.yesterday().toString().substring(0, 4);
		}else if (date.equals("0101")) {
			year = DateUtil.tomorrow().toString().substring(0, 4);
		}
		return year;
    }
    
    public static String date2string(String dateStr) {
    	if(StringUtil.isEmpty(dateStr)){
    		return "";
    	}
		String[] str = dateStr.split(" ");
		String[] date = str[0].split("-");
		String[] time = str[1].split(":");
		
		StringBuffer sb = new StringBuffer();
		for (String strDate : date) {
			sb.append(strDate);
		}
		for (String strTime : time) {
			sb.append(strTime);
		}
		return sb.toString();
	}
    
}
